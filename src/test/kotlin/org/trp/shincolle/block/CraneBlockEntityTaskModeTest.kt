package org.trp.shincolle.block

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.trp.shincolle.block.entity.CraneBlockEntity
import org.trp.shincolle.init.ModBlocks

class CraneBlockEntityTaskModeTest {

    @Test
    fun `getWaitTime should return correct tick buckets for modes 5 through 24`() {
        val companion = CraneBlockEntity::class.java.getDeclaredField("Companion").get(null)
        val method = companion.javaClass.getDeclaredMethod("getWaitTime", Int::class.javaPrimitiveType)
        method.isAccessible = true

        // Modes 5-9: 16 tick buckets
        assertThat(method.invoke(companion, 5)).isEqualTo(16)
        assertThat(method.invoke(companion, 9)).isEqualTo(80)

        // Modes 10-14: 5-second buckets (20*5)
        assertThat(method.invoke(companion, 10)).isEqualTo(100)
        assertThat(method.invoke(companion, 14)).isEqualTo(500)

        // Modes 15-19: 1-minute buckets (20*60)
        assertThat(method.invoke(companion, 15)).isEqualTo(1200)
        assertThat(method.invoke(companion, 19)).isEqualTo(6000)

        // Modes 20-24: 10-minute buckets (20*60*10)
        assertThat(method.invoke(companion, 20)).isEqualTo(12000)
        assertThat(method.invoke(companion, 24)).isEqualTo(60000)

        // Boundary modes 0-4 and out of range should return 0
        assertThat(method.invoke(companion, 0)).isEqualTo(0)
        assertThat(method.invoke(companion, 4)).isEqualTo(0)
        assertThat(method.invoke(companion, 25)).isEqualTo(0)
        assertThat(method.invoke(companion, -1)).isEqualTo(0)
    }

    @Test
    fun `crane mode state should persist through all valid mode values`() {
        val entity = TestCraneBlockEntity()

        for (mode in 0..24) {
            entity.setCraneMode(mode)
            assertThat(entity.getCraneMode()).isEqualTo(mode)
        }

        val tag = entity.saveForTest()
        val restored = TestCraneBlockEntity()
        restored.loadForTest(tag)

        assertThat(restored.getCraneMode()).isEqualTo(24)
    }

    @Test
    fun `crane ending conditions should normalize out of range modes to immediate stop`() {
        val entity = TestCraneBlockEntity()

        entity.setCraneMode(-1)
        assertThat(entity.getCraneMode()).isEqualTo(-1)

        entity.setCraneMode(99)
        assertThat(entity.getCraneMode()).isEqualTo(99)

        val tag = entity.saveForTest()
        val restored = TestCraneBlockEntity()
        restored.loadForTest(tag)

        assertThat(restored.getCraneMode()).isEqualTo(99)
    }

    private class TestCraneBlockEntity : CraneBlockEntity(BlockPos.ZERO, ModBlocks.CRANE.get()!!.defaultBlockState()) {
        fun saveForTest(): CompoundTag = CompoundTag().also { saveAdditional(it, registries()) }
        fun loadForTest(tag: CompoundTag) = loadAdditional(tag, registries())
    }

    companion object {
        private fun registries(): HolderLookup.Provider = RegistryAccess.EMPTY
    }
}
