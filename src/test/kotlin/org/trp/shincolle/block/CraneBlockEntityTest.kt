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
        entity.remainedPower = 1200
        entity.remainedPower = 1200
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.isActive = true
        entity.isActive = true
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.checkMetadata = true
        entity.checkMetadata = true
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.checkOredict = true
        entity.checkOredict = true
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.checkNbt = true
        entity.checkNbt = true
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.enabLoad = false
        entity.enabLoad = false
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.enabUnload = false
        entity.enabUnload = false
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.craneMode = 3
        entity.craneMode = 3
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.modeItem = 7
        entity.modeItem = 7
        assertThat(entity.syncCount).isEqualTo(1)

        val itemModeEntity = TestCraneBlockEntity()
        itemModeEntity.resetSyncCount()
        itemModeEntity.setItemMode(2, true)
        itemModeEntity.setItemMode(2, true)
        assertThat(itemModeEntity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.modeRedstone = 2
        entity.modeRedstone = 2
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.modeLiquid = 1
        entity.modeLiquid = 1
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.modeEnergy = 2
        entity.modeEnergy = 2
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.lastPos = BlockPos(1, 2, 3)
        entity.lastPos = BlockPos(1, 2, 3)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.nextPos = BlockPos(4, 5, 6)
        entity.nextPos = BlockPos(4, 5, 6)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.chestPos = BlockPos(7, 8, 9)
        entity.chestPos = BlockPos(7, 8, 9)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.ownerUUID = owner
        entity.ownerUUID = owner
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.ownerName = "Admiral"
        entity.ownerName = "Admiral"
        assertThat(entity.syncCount).isEqualTo(1)
    }

    @Test
    fun `crane block entity should persist normalized routing and mode state`() {
        val owner = UUID.randomUUID()
        val entity = TestCraneBlockEntity()

        entity.remainedPower = 1200
        entity.isActive = true
        entity.checkMetadata = true
        entity.checkOredict = true
        entity.checkNbt = true
        entity.enabLoad = false
        entity.enabUnload = false
        entity.craneMode = 4
        entity.modeItem = 0
        entity.setItemMode(1, true)
        entity.setItemMode(5, true)
        entity.modeRedstone = 2
        entity.modeLiquid = 1
        entity.modeEnergy = 2
        entity.lastPos = BlockPos(1, 2, 3)
        entity.nextPos = BlockPos(4, 5, 6)
        entity.chestPos = BlockPos(7, 8, 9)
        entity.ownerUUID = owner
        entity.ownerName = ""

        val tag = entity.saveForTest()
        val restored = TestCraneBlockEntity()
        restored.loadForTest(tag)

        assertThat(restored.remainedPower).isEqualTo(1200)
        assertThat(restored.isActive).isTrue()
        assertThat(restored.checkMetadata).isTrue()
        assertThat(restored.checkOredict).isTrue()
        assertThat(restored.checkNbt).isTrue()
        assertThat(restored.enabLoad).isFalse()
        assertThat(restored.enabUnload).isFalse()
        assertThat(restored.craneMode).isEqualTo(4)
        assertThat(restored.getItemMode(1)).isTrue()
        assertThat(restored.getItemMode(5)).isTrue()
        assertThat(restored.modeRedstone).isEqualTo(2)
        assertThat(restored.modeLiquid).isEqualTo(1)
        assertThat(restored.modeEnergy).isEqualTo(2)
        assertThat(restored.lastPos).isEqualTo(BlockPos(1, 2, 3))
        assertThat(restored.nextPos).isEqualTo(BlockPos(4, 5, 6))
        assertThat(restored.chestPos).isEqualTo(BlockPos(7, 8, 9))
        assertThat(restored.ownerUUID).isEqualTo(owner)
        assertThat(restored.ownerName).isEmpty()
        assertThat(tag.getBoolean("IsPaired")).isTrue()
    }

    @Test
    fun `crane block entity should normalize null owner and chest state`() {
        val entity = TestCraneBlockEntity()

        entity.chestPos = null
        entity.ownerUUID = null
        entity.ownerName = ""

        val tag = entity.saveForTest()

        assertThat(entity.chestPos).isEqualTo(BlockPos.ZERO)
        assertThat(entity.ownerUUID).isNull()
        assertThat(entity.ownerName).isEmpty()
        assertThat(tag.getBoolean("IsPaired")).isFalse()
        assertThat(tag.contains("OwnerUUID")).isFalse()
    }

    private class TestCraneBlockEntity : CraneBlockEntity(BlockPos.ZERO, ModBlocks.CRANE.get()!!.defaultBlockState()) {
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
