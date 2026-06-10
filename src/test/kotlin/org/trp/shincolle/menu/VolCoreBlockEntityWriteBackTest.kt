package org.trp.shincolle.menu

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.trp.shincolle.block.entity.VolCoreBlockEntity
import org.trp.shincolle.init.ModBlocks

class VolCoreBlockEntityWriteBackTest {

    @Test
    fun `vol core menu state should persist through block entity write back`() {
        val entity = TestVolCoreBlockEntity()

        entity.setRemainedPower(480)
        entity.setBtnActive(true)

        val tag = entity.saveForTest()
        val restored = TestVolCoreBlockEntity()
        restored.loadForTest(tag)

        assertThat(restored.getRemainedPower()).isEqualTo(480)
        assertThat(restored.isBtnActive()).isTrue()
    }

    @Test
    fun `vol core menu state setters should only sync when values actually change`() {
        val entity = TestVolCoreBlockEntity()

        entity.resetSyncCount()
        entity.setRemainedPower(200)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setRemainedPower(200)
        assertThat(entity.syncCount).isZero()

        entity.resetSyncCount()
        entity.setBtnActive(true)
        assertThat(entity.syncCount).isEqualTo(1)

        entity.resetSyncCount()
        entity.setBtnActive(true)
        assertThat(entity.syncCount).isZero()
    }

    private class TestVolCoreBlockEntity :
        VolCoreBlockEntity(BlockPos.ZERO, ModBlocks.VOL_CORE.get()!!.defaultBlockState()) {
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
