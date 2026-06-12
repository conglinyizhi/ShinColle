package org.trp.shincolle.menu

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.trp.shincolle.block.entity.CraneBlockEntity
import org.trp.shincolle.init.ModBlocks

class CraneBlockEntityMenuWriteBackTest {

    @Test
    fun `crane menu boolean toggles should persist through block entity write back`() {
        val entity = TestCraneBlockEntity()

        entity.isActive = true
        entity.checkMetadata = true
        entity.checkOredict = true
        entity.checkNbt = true
        entity.enabLoad = false
        entity.enabUnload = false

        val tag = entity.saveForTest()
        val restored = TestCraneBlockEntity()
        restored.loadForTest(tag)

        assertThat(restored.isActive).isTrue()
        assertThat(restored.checkMetadata).isTrue()
        assertThat(restored.checkOredict).isTrue()
        assertThat(restored.checkNbt).isTrue()
        assertThat(restored.enabLoad).isFalse()
        assertThat(restored.enabUnload).isFalse()
    }

    @Test
    fun `crane menu mode selectors should persist all valid mode ranges`() {
        val entity = TestCraneBlockEntity()

        entity.modeRedstone = 2
        entity.modeLiquid = 1
        entity.modeEnergy = 2
        entity.modeItem = 7

        val tag = entity.saveForTest()
        val restored = TestCraneBlockEntity()
        restored.loadForTest(tag)

        assertThat(restored.modeRedstone).isEqualTo(2)
        assertThat(restored.modeLiquid).isEqualTo(1)
        assertThat(restored.modeEnergy).isEqualTo(2)
        assertThat(restored.modeItem).isEqualTo(7)
    }

    @Test
    fun `crane menu item mode bitmap should allow individual slot toggles`() {
        val entity = TestCraneBlockEntity()

        for (slot in 0..17) {
            entity.setItemMode(slot, true)
            assertThat(entity.getItemMode(slot)).isTrue()
        }

        for (slot in 0..17) {
            entity.setItemMode(slot, false)
            assertThat(entity.getItemMode(slot)).isFalse()
        }
    }

    @Test
    fun `crane menu active toggle should trigger sync once per actual change`() {
        val entity = TestCraneBlockEntity()

        entity.resetSyncCount()
        entity.isActive = true
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.isActive = true
        assertThat(entity.syncCount).isZero()

        entity.resetSyncCount()
        entity.isActive = false
        assertThat(entity.syncCount).isEqualTo(1)
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
        fun loadForTest(tag: CompoundTag) = loadAdditional(tag, registries())
    }

    companion object {
        private fun registries(): HolderLookup.Provider = RegistryAccess.EMPTY
    }
}
