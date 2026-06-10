package org.trp.shincolle.menu

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.trp.shincolle.block.entity.LargeShipyardBlockEntity
import org.trp.shincolle.init.ModBlocks

class LargeShipyardBlockEntityWriteBackTest {

    @Test
    fun `large shipyard selectors should clamp to valid menu ranges on write back`() {
        val entity = TestLargeShipyardBlockEntity()

        entity.setBuildType(-1)
        entity.setInvMode(-1)
        entity.setSelectMat(-1)
        assertThat(entity.getBuildType()).isZero()
        assertThat(entity.getInvMode()).isZero()
        assertThat(entity.getSelectMat()).isZero()

        entity.setBuildType(99)
        entity.setInvMode(99)
        entity.setSelectMat(99)
        assertThat(entity.getBuildType()).isEqualTo(4)
        assertThat(entity.getInvMode()).isEqualTo(1)
        assertThat(entity.getSelectMat()).isEqualTo(3)

        val tag = entity.saveForTest()
        val restored = TestLargeShipyardBlockEntity()
        restored.loadForTest(tag)

        assertThat(restored.getBuildType()).isEqualTo(4)
        assertThat(restored.getInvMode()).isEqualTo(1)
        assertThat(restored.getSelectMat()).isEqualTo(3)
    }

    @Test
    fun `large shipyard write back should sanitize incomplete material arrays`() {
        val restored = TestLargeShipyardBlockEntity()
        val tag = CompoundTag()
        tag.putInt("BuildType", 2)
        tag.putInt("InvMode", 1)
        tag.putInt("SelectMat", 99)
        tag.putIntArray("MatsBuild", intArrayOf(1, 2))
        tag.putIntArray("MatsStock", intArrayOf(3))

        restored.loadForTest(tag)

        assertThat(restored.getBuildType()).isEqualTo(2)
        assertThat(restored.getInvMode()).isEqualTo(1)
        assertThat(restored.getSelectMat()).isEqualTo(3)
        assertThat(restored.matsBuild).containsExactly(0, 0, 0, 0)
        assertThat(restored.matsStock).containsExactly(0, 0, 0, 0)
    }

    private class TestLargeShipyardBlockEntity :
        LargeShipyardBlockEntity(BlockPos.ZERO, ModBlocks.LARGE_SHIPYARD.get()!!.defaultBlockState()) {
        fun saveForTest(): CompoundTag = CompoundTag().also { saveAdditional(it, registries()) }
        fun loadForTest(tag: CompoundTag) = loadAdditional(tag, registries())
    }

    companion object {
        private fun registries(): HolderLookup.Provider = RegistryAccess.EMPTY
    }
}
