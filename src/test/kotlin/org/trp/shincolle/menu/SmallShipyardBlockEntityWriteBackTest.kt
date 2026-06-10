package org.trp.shincolle.menu

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.trp.shincolle.block.entity.SmallShipyardBlockEntity
import org.trp.shincolle.init.ModBlocks
import org.trp.shincolle.init.ModItems

class SmallShipyardBlockEntityWriteBackTest {

    @Test
    fun `small shipyard build type should clamp to valid menu range on write back`() {
        val entity = TestSmallShipyardBlockEntity()

        entity.setBuildType(-1)
        assertThat(entity.getBuildType()).isZero()

        entity.setBuildType(99)
        assertThat(entity.getBuildType()).isEqualTo(4)

        val tag = entity.saveForTest()
        val restored = TestSmallShipyardBlockEntity()
        restored.loadForTest(tag)

        assertThat(restored.getBuildType()).isEqualTo(4)
    }

    @Test
    fun `small shipyard repeat build should snapshot current material mix for write back`() {
        val entity = TestSmallShipyardBlockEntity()
        entity.inventory.setStackInSlot(0, ItemStack(ModItems.GRUDGE.get(), 3))
        entity.inventory.setStackInSlot(1, ItemStack(ModItems.ABYSS_METAL.get(), 4))
        entity.inventory.setStackInSlot(2, ItemStack(ModItems.AMMO_LIGHT.get(), 5))
        entity.inventory.setStackInSlot(3, ItemStack(ModItems.ABYSS_POLYMETAL.get(), 6))

        entity.setBuildType(3)
        assertThat(entity.getBuildRecord()).containsExactly(3, 4, 5, 6)

        val tag = entity.saveForTest()
        val restored = TestSmallShipyardBlockEntity()
        restored.loadForTest(tag)

        assertThat(restored.getBuildType()).isEqualTo(3)
        assertThat(restored.getBuildRecord()).containsExactly(3, 4, 5, 6)
    }

    private class TestSmallShipyardBlockEntity :
        SmallShipyardBlockEntity(BlockPos.ZERO, ModBlocks.SMALL_SHIPYARD.get()!!.defaultBlockState()) {
        fun saveForTest(): CompoundTag = CompoundTag().also { saveAdditional(it, registries()) }
        fun loadForTest(tag: CompoundTag) = loadAdditional(tag, registries())
    }

    companion object {
        private fun registries(): HolderLookup.Provider = RegistryAccess.EMPTY
    }
}
