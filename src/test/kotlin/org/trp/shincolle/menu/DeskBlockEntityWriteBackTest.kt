package org.trp.shincolle.menu

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.trp.shincolle.block.entity.DeskBlockEntity
import org.trp.shincolle.init.ModBlocks
import org.trp.shincolle.reference.Values

class DeskBlockEntityWriteBackTest {

    @Test
    fun `desk block entity should clamp book chapter and page to valid ranges on write back`() {
        val entity = TestDeskBlockEntity()

        entity.setBookChap(-1)
        entity.setBookPage(-1)
        assertThat(entity.getBookChap()).isEqualTo(0)
        assertThat(entity.getBookPage()).isEqualTo(0)

        entity.setBookChap(999)
        entity.setBookPage(999)
        assertThat(entity.getBookChap()).isEqualTo(Values.PageLimit.lastIndex)
        assertThat(entity.getBookPage()).isEqualTo(Values.PageLimit[Values.PageLimit.lastIndex])

        val tag = entity.saveForTest()
        val restored = TestDeskBlockEntity()
        restored.loadForTest(tag)

        assertThat(restored.getBookChap()).isEqualTo(Values.PageLimit.lastIndex)
        assertThat(restored.getBookPage()).isEqualTo(Values.PageLimit[Values.PageLimit.lastIndex])
    }

    @Test
    fun `desk block entity should clamp gui func and radar zoom to valid ranges on write back`() {
        val entity = TestDeskBlockEntity()

        entity.setGuiFunc(-1)
        entity.setRadarZoomLv(-1)
        assertThat(entity.getGuiFunc()).isEqualTo(0)
        assertThat(entity.getRadarZoomLv()).isEqualTo(0)

        entity.setGuiFunc(99)
        entity.setRadarZoomLv(99)
        assertThat(entity.getGuiFunc()).isEqualTo(4)
        assertThat(entity.getRadarZoomLv()).isEqualTo(2)

        val tag = entity.saveForTest()
        val restored = TestDeskBlockEntity()
        restored.loadForTest(tag)

        assertThat(restored.getGuiFunc()).isEqualTo(4)
        assertThat(restored.getRadarZoomLv()).isEqualTo(2)
    }

    @Test
    fun `desk block entity should normalize page when chapter changes to smaller max page`() {
        val entity = TestDeskBlockEntity()

        entity.setBookChap(Values.PageLimit.lastIndex)
        entity.setBookPage(Values.PageLimit[Values.PageLimit.lastIndex])
        assertThat(entity.getBookPage()).isEqualTo(Values.PageLimit[Values.PageLimit.lastIndex])

        entity.setBookChap(0)
        assertThat(entity.getBookPage()).isEqualTo(Values.PageLimit[0])
    }

    private class TestDeskBlockEntity : DeskBlockEntity(BlockPos.ZERO, ModBlocks.DESK.get()!!.defaultBlockState()) {
        fun saveForTest(): CompoundTag = CompoundTag().also { saveAdditional(it, registries()) }
        fun loadForTest(tag: CompoundTag) = loadAdditional(tag, registries())
    }

    companion object {
        private fun registries(): HolderLookup.Provider = RegistryAccess.EMPTY
    }
}
