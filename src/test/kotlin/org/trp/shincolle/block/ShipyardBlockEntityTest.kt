package org.trp.shincolle.block

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.trp.shincolle.block.entity.LargeShipyardBlockEntity
import org.trp.shincolle.block.entity.SmallShipyardBlockEntity
import org.trp.shincolle.block.entity.VolCoreBlockEntity
import org.trp.shincolle.init.ModBlocks
import org.trp.shincolle.init.ModItems

class ShipyardBlockEntityTest {

    @Test
    fun `vol core should skip noop sync writes and persist state`() {
        val entity = TestVolCoreBlockEntity()

        entity.resetSyncCount()
        entity.setRemainedPower(3200)
        entity.setRemainedPower(3200)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setBtnActive(true)
        entity.setBtnActive(true)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.inventory.setStackInSlot(0, ItemStack(ModItems.GRUDGE.get(), 3))
        val restored = TestVolCoreBlockEntity()
        restored.loadForTest(entity.saveForTest())

        assertThat(restored.getRemainedPower()).isEqualTo(3200)
        assertThat(restored.isBtnActive()).isTrue()
        assertThat(restored.inventory.getStackInSlot(0).item).isEqualTo(ModItems.GRUDGE.get())
        assertThat(restored.inventory.getStackInSlot(0).count).isEqualTo(3)
    }

    @Test
    fun `small shipyard repeat build should capture current materials and skip noop writes`() {
        val entity = TestSmallShipyardBlockEntity()
        entity.inventory.setStackInSlot(SmallShipyardBlockEntity.SLOT_GRUDGE, ItemStack(ModItems.GRUDGE.get(), 20))
        entity.inventory.setStackInSlot(SmallShipyardBlockEntity.SLOT_ABYSSIUM, ItemStack(ModItems.ABYSS_METAL.get(), 21))
        entity.inventory.setStackInSlot(SmallShipyardBlockEntity.SLOT_AMMO, ItemStack(ModItems.AMMO_LIGHT.get(), 22))
        entity.inventory.setStackInSlot(SmallShipyardBlockEntity.SLOT_POLYMETAL, ItemStack(ModItems.ABYSS_POLYMETAL.get(), 23))

        entity.resetSyncCount()
        entity.setBuildType(3)
        assertThat(entity.getBuildType()).isEqualTo(3)
        assertThat(entity.getBuildRecord()).containsExactly(20, 21, 22, 23)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setBuildType(3)
        assertThat(entity.syncCount).isZero()

        entity.inventory.setStackInSlot(SmallShipyardBlockEntity.SLOT_GRUDGE, ItemStack(ModItems.GRUDGE.get(), 24))
        entity.resetSyncCount()
        entity.setBuildType(3)
        assertThat(entity.getBuildRecord()).containsExactly(24, 21, 22, 23)
        assertThat(entity.syncCount).isEqualTo(1)

        val restored = TestSmallShipyardBlockEntity()
        restored.loadForTest(entity.saveForTest())
        assertThat(restored.getBuildType()).isEqualTo(3)
        assertThat(restored.getBuildRecord()).containsExactly(24, 21, 22, 23)
    }

    @Test
    fun `large shipyard should clamp setters move materials and persist sanitized state`() {
        val entity = TestLargeShipyardBlockEntity()

        entity.resetSyncCount()
        entity.setBuildType(9)
        entity.setBuildType(9)
        assertThat(entity.getBuildType()).isEqualTo(4)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setInvMode(99)
        entity.setInvMode(99)
        assertThat(entity.getInvMode()).isEqualTo(1)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setSelectMat(99)
        entity.setSelectMat(99)
        assertThat(entity.getSelectMat()).isEqualTo(3)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.matsStock[1] = 150
        entity.resetSyncCount()
        entity.moveBuildMaterialAmount(1, 1)
        assertThat(entity.matsBuild[1]).isEqualTo(100)
        assertThat(entity.matsStock[1]).isEqualTo(50)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.moveBuildMaterialAmount(1, 5)
        assertThat(entity.matsBuild[1]).isEqualTo(0)
        assertThat(entity.matsStock[1]).isEqualTo(150)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.moveBuildMaterialAmount(1, 5)
        assertThat(entity.syncCount).isZero()

        val restored = TestLargeShipyardBlockEntity()
        restored.loadForTest(CompoundTag().apply {
            putInt("BuildType", 4)
            putInt("InvMode", 1)
            putInt("SelectMat", 99)
            putIntArray("MatsBuild", intArrayOf(1, 2, 3))
            putIntArray("MatsStock", intArrayOf(4, 5, 6, 7, 8))
        })

        assertThat(restored.getBuildType()).isEqualTo(4)
        assertThat(restored.getInvMode()).isEqualTo(1)
        assertThat(restored.getSelectMat()).isEqualTo(3)
        assertThat(restored.matsBuild).containsExactly(0, 0, 0, 0)
        assertThat(restored.matsStock).containsExactly(4, 5, 6, 7)
    }

    private class TestVolCoreBlockEntity : VolCoreBlockEntity(BlockPos.ZERO, ModBlocks.VOL_CORE.get()!!.defaultBlockState()) {
        var syncCount = 0
            private set

        override fun markForSync() {
            syncCount++
        }

        fun resetSyncCount() {
            syncCount = 0
        }

        fun saveForTest(): CompoundTag = CompoundTag().also { saveAdditional(it, registries()) }

        fun loadForTest(tag: CompoundTag) {
            loadAdditional(tag, registries())
        }
    }

    private class TestSmallShipyardBlockEntity : SmallShipyardBlockEntity(BlockPos.ZERO, ModBlocks.SMALL_SHIPYARD.get()!!.defaultBlockState()) {
        var syncCount = 0
            private set

        override fun markForSync() {
            syncCount++
        }

        fun resetSyncCount() {
            syncCount = 0
        }

        fun saveForTest(): CompoundTag = CompoundTag().also { saveAdditional(it, registries()) }

        fun loadForTest(tag: CompoundTag) {
            loadAdditional(tag, registries())
        }
    }

    private class TestLargeShipyardBlockEntity : LargeShipyardBlockEntity(BlockPos.ZERO, ModBlocks.LARGE_SHIPYARD.get()!!.defaultBlockState()) {
        var syncCount = 0
            private set

        override fun markForSync() {
            syncCount++
        }

        fun resetSyncCount() {
            syncCount = 0
        }

        fun loadForTest(tag: CompoundTag) {
            loadAdditional(tag, registries())
        }
    }

    companion object {
        private fun registries(): HolderLookup.Provider = RegistryAccess.EMPTY
    }
}
