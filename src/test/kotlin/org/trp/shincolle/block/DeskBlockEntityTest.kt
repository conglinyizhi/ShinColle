package org.trp.shincolle.block

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.trp.shincolle.block.entity.DeskBlockEntity
import org.trp.shincolle.init.ModBlocks
import org.trp.shincolle.reference.Values

class DeskBlockEntityTest {

    @Test
    fun `desk block entity should clamp setters and persist normalized state`() {
        val entity = TestDeskBlockEntity()

        entity.setGuiFunc(9)
        entity.setRadarZoomLv(7)
        entity.setBookPage(999)
        entity.setBookChap(999)

        assertThat(entity.getGuiFunc()).isEqualTo(4)
        assertThat(entity.getRadarZoomLv()).isEqualTo(2)
        assertThat(entity.getBookChap()).isEqualTo(Values.PageLimit.lastIndex)
        assertThat(entity.getBookPage()).isEqualTo(Values.PageLimit[0])

        val restored = TestDeskBlockEntity()
        restored.loadForTest(entity.saveForTest())

        assertThat(restored.getGuiFunc()).isEqualTo(4)
        assertThat(restored.getRadarZoomLv()).isEqualTo(2)
        assertThat(restored.getBookChap()).isEqualTo(Values.PageLimit.lastIndex)
        assertThat(restored.getBookPage()).isEqualTo(Values.PageLimit[0])
    }

    @Test
    fun `desk block entity should re-clamp page when chapter changes and load invalid tags safely`() {
        val entity = TestDeskBlockEntity()
        entity.setBookChap(1)
        entity.setBookPage(20)

        entity.setBookChap(0)

        assertThat(entity.getBookChap()).isEqualTo(0)
        assertThat(entity.getBookPage()).isEqualTo(Values.PageLimit[0])

        val restored = TestDeskBlockEntity()
        restored.loadForTest(CompoundTag().apply {
            putInt("guiFunc", 99)
            putInt("radarZoom", -3)
            putInt("bookChap", 999)
            putInt("bookPage", 999)
        })

        assertThat(restored.getGuiFunc()).isEqualTo(4)
        assertThat(restored.getRadarZoomLv()).isEqualTo(0)
        assertThat(restored.getBookChap()).isEqualTo(Values.PageLimit.lastIndex)
        assertThat(restored.getBookPage()).isEqualTo(Values.PageLimit.last())
    }

    private class TestDeskBlockEntity : DeskBlockEntity(BlockPos.ZERO, ModBlocks.DESK.get()!!.defaultBlockState()) {
        fun saveForTest(): CompoundTag = CompoundTag().also { saveAdditional(it, registries()) }

        fun loadForTest(tag: CompoundTag) {
            loadAdditional(tag, registries())
        }
    }

    companion object {
        private fun registries(): HolderLookup.Provider = RegistryAccess.EMPTY
    }
}
