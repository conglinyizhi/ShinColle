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

        entity.setActive(true)
        entity.setCheckMetadata(true)
        entity.setCheckOredict(true)
        entity.setCheckNbt(true)
        entity.setEnabLoad(false)
        entity.setEnabUnload(false)

        val tag = entity.saveForTest()
        val restored = TestCraneBlockEntity()
        restored.loadForTest(tag)

        assertThat(restored.isActive()).isTrue()
        assertThat(restored.isCheckMetadata()).isTrue()
        assertThat(restored.isCheckOredict()).isTrue()
        assertThat(restored.isCheckNbt()).isTrue()
        assertThat(restored.isEnabLoad()).isFalse()
        assertThat(restored.isEnabUnload()).isFalse()
    }

    @Test
    fun `crane menu mode selectors should persist all valid mode ranges`() {
        val entity = TestCraneBlockEntity()

        entity.setModeRedstone(2)
        entity.setModeLiquid(1)
        entity.setModeEnergy(2)
        entity.setModeItem(7)

        val tag = entity.saveForTest()
        val restored = TestCraneBlockEntity()
        restored.loadForTest(tag)

        assertThat(restored.getModeRedstone()).isEqualTo(2)
        assertThat(restored.getModeLiquid()).isEqualTo(1)
        assertThat(restored.getModeEnergy()).isEqualTo(2)
        assertThat(restored.getModeItem()).isEqualTo(7)
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
        entity.setActive(true)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setActive(true)
        assertThat(entity.syncCount).isZero()

        entity.resetSyncCount()
        entity.setActive(false)
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
