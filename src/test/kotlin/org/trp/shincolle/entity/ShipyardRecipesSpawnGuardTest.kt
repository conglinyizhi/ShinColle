package org.trp.shincolle.entity

import net.minecraft.world.item.ItemStack
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Test
import org.trp.shincolle.crafting.ShipyardRecipes

class ShipyardRecipesSpawnGuardTest {

    @Test
    fun `getShipyardMatsTag should return null for empty stack`() {
        assertThat(ShipyardRecipes.getShipyardMatsTag(ItemStack.EMPTY)).isNull()
    }

    @Test
    fun `getHeavyGrudgeMatsTag should return null for empty stack`() {
        assertThat(ShipyardRecipes.getHeavyGrudgeMatsTag(ItemStack.EMPTY)).isNull()
    }

    @Test
    fun `getHeavyGrudgeFuelTag should return zero for empty stack`() {
        assertThat(ShipyardRecipes.getHeavyGrudgeFuelTag(ItemStack.EMPTY)).isEqualTo(0)
    }

    @Test
    fun `addLargeMaterialStock should return false for empty stack`() {
        val matStock = intArrayOf(0, 0, 0, 0)
        assertThat(ShipyardRecipes.addLargeMaterialStock(matStock, ItemStack.EMPTY)).isFalse()
    }

    @Test
    fun `putHeavyGrudgeStorageTag should silently ignore empty stack`() {
        assertThatNoException().isThrownBy {
            ShipyardRecipes.putHeavyGrudgeStorageTag(ItemStack.EMPTY, intArrayOf(1, 2, 3, 4), 100)
        }
    }
}
