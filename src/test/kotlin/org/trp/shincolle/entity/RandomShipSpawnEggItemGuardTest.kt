package org.trp.shincolle.entity

import net.minecraft.world.item.ItemStack
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.item.RandomShipSpawnEggItem

class RandomShipSpawnEggItemGuardTest {

    @Test
    fun `injectRandomEntityData should silently ignore null level`() {
        val method = RandomShipSpawnEggItem::class.java.getDeclaredMethod(
            "injectRandomEntityData",
            net.minecraft.world.level.Level::class.java,
            net.minecraft.world.entity.player.Player::class.java,
            ItemStack::class.java
        )
        method.isAccessible = true

        val item = ModItems.SHIPSPAWNEGGL.get()
        assertThat(item).isInstanceOf(RandomShipSpawnEggItem::class.java)
        val egg = item as RandomShipSpawnEggItem

        assertThatNoException().isThrownBy {
            method.invoke(egg, null, null, ItemStack(egg))
        }
    }

    @Test
    fun `injectRandomEntityData should silently ignore empty stack`() {
        val method = RandomShipSpawnEggItem::class.java.getDeclaredMethod(
            "injectRandomEntityData",
            net.minecraft.world.level.Level::class.java,
            net.minecraft.world.entity.player.Player::class.java,
            ItemStack::class.java
        )
        method.isAccessible = true

        val item = ModItems.SHIPSPAWNEGGL.get()
        assertThat(item).isInstanceOf(RandomShipSpawnEggItem::class.java)
        val egg = item as RandomShipSpawnEggItem

        assertThatNoException().isThrownBy {
            method.invoke(egg, null, null, ItemStack.EMPTY)
        }
    }
}
