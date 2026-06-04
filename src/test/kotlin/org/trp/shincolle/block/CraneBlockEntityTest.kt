package org.trp.shincolle.block

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.trp.shincolle.block.entity.CraneBlockEntity
import org.trp.shincolle.init.ModBlocks
import java.util.UUID

class CraneBlockEntityTest {

    @Test
    fun `crane block entity setters should skip noop sync writes`() {
        val owner = UUID.randomUUID()
        val entity = TestCraneBlockEntity()

        entity.resetSyncCount()
        entity.setRemainedPower(1200)
        entity.setRemainedPower(1200)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setActive(true)
        entity.setActive(true)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setCheckMetadata(true)
        entity.setCheckMetadata(true)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setCheckOredict(true)
        entity.setCheckOredict(true)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setCheckNbt(true)
        entity.setCheckNbt(true)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setEnabLoad(false)
        entity.setEnabLoad(false)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setEnabUnload(false)
        entity.setEnabUnload(false)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setCraneMode(3)
        entity.setCraneMode(3)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setModeItem(7)
        entity.setModeItem(7)
        assertThat(entity.syncCount).isEqualTo(1)

        val itemModeEntity = TestCraneBlockEntity()
        itemModeEntity.resetSyncCount()
        itemModeEntity.setItemMode(2, true)
        itemModeEntity.setItemMode(2, true)
        assertThat(itemModeEntity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setModeRedstone(2)
        entity.setModeRedstone(2)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setModeLiquid(1)
        entity.setModeLiquid(1)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setModeEnergy(2)
        entity.setModeEnergy(2)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setLastPos(BlockPos(1, 2, 3))
        entity.setLastPos(BlockPos(1, 2, 3))
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setNextPos(BlockPos(4, 5, 6))
        entity.setNextPos(BlockPos(4, 5, 6))
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setChestPos(BlockPos(7, 8, 9))
        entity.setChestPos(BlockPos(7, 8, 9))
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setOwnerUUID(owner)
        entity.setOwnerUUID(owner)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setOwnerName("Admiral")
        entity.setOwnerName("Admiral")
        assertThat(entity.syncCount).isEqualTo(1)
    }

    @Test
    fun `crane block entity should persist normalized routing and mode state`() {
        val owner = UUID.randomUUID()
        val entity = TestCraneBlockEntity()

        entity.setRemainedPower(1200)
        entity.setActive(true)
        entity.setCheckMetadata(true)
        entity.setCheckOredict(true)
        entity.setCheckNbt(true)
        entity.setEnabLoad(false)
        entity.setEnabUnload(false)
        entity.setCraneMode(4)
        entity.setModeItem(0)
        entity.setItemMode(1, true)
        entity.setItemMode(5, true)
        entity.setModeRedstone(2)
        entity.setModeLiquid(1)
        entity.setModeEnergy(2)
        entity.setLastPos(BlockPos(1, 2, 3))
        entity.setNextPos(BlockPos(4, 5, 6))
        entity.setChestPos(BlockPos(7, 8, 9))
        entity.setOwnerUUID(owner)
        entity.setOwnerName(null)

        val tag = entity.saveForTest()
        val restored = TestCraneBlockEntity()
        restored.loadForTest(tag)

        assertThat(restored.getRemainedPower()).isEqualTo(1200)
        assertThat(restored.isActive()).isTrue()
        assertThat(restored.isCheckMetadata()).isTrue()
        assertThat(restored.isCheckOredict()).isTrue()
        assertThat(restored.isCheckNbt()).isTrue()
        assertThat(restored.isEnabLoad()).isFalse()
        assertThat(restored.isEnabUnload()).isFalse()
        assertThat(restored.getCraneMode()).isEqualTo(4)
        assertThat(restored.getItemMode(1)).isTrue()
        assertThat(restored.getItemMode(5)).isTrue()
        assertThat(restored.getModeRedstone()).isEqualTo(2)
        assertThat(restored.getModeLiquid()).isEqualTo(1)
        assertThat(restored.getModeEnergy()).isEqualTo(2)
        assertThat(restored.getLastPos()).isEqualTo(BlockPos(1, 2, 3))
        assertThat(restored.getNextPos()).isEqualTo(BlockPos(4, 5, 6))
        assertThat(restored.getChestPos()).isEqualTo(BlockPos(7, 8, 9))
        assertThat(restored.ownerUUID).isEqualTo(owner)
        assertThat(restored.ownerName).isEmpty()
        assertThat(tag.getBoolean("IsPaired")).isTrue()
    }

    @Test
    fun `crane block entity should normalize null owner and chest state`() {
        val entity = TestCraneBlockEntity()

        entity.setChestPos(null)
        entity.setOwnerUUID(null)
        entity.setOwnerName(null)

        val tag = entity.saveForTest()

        assertThat(entity.getChestPos()).isEqualTo(BlockPos.ZERO)
        assertThat(entity.ownerUUID).isNull()
        assertThat(entity.ownerName).isEmpty()
        assertThat(tag.getBoolean("IsPaired")).isFalse()
        assertThat(tag.contains("OwnerUUID")).isFalse()
    }

    private class TestCraneBlockEntity : CraneBlockEntity(BlockPos.ZERO, ModBlocks.CRANE.get().defaultBlockState()) {
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

    companion object {
        private fun registries(): HolderLookup.Provider = RegistryAccess.EMPTY
    }
}
