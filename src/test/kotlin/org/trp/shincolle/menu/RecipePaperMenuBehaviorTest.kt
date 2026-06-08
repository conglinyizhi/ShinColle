package org.trp.shincolle.menu

import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems

class RecipePaperMenuBehaviorTest {

    @Test
    fun `preview result slot helper should only match legacy slot nine`() {
        assertThat(RecipePaperMenu.isPreviewResultSlot(-1)).isFalse()
        assertThat(RecipePaperMenu.isPreviewResultSlot(0)).isFalse()
        assertThat(RecipePaperMenu.isPreviewResultSlot(8)).isFalse()
        assertThat(RecipePaperMenu.isPreviewResultSlot(9)).isTrue()
        assertThat(RecipePaperMenu.isPreviewResultSlot(10)).isFalse()
    }

    @Test
    fun `host stack binding should stay reference based per hand`() {
        val host = ItemStack(ModItems.RECIPE_PAPER.get())
        val sameReference = host
        val differentMatchingStack = ItemStack(ModItems.RECIPE_PAPER.get())

        assertThat(RecipePaperMenu.isStillBoundToHostStack(sameReference, host)).isTrue()
        assertThat(RecipePaperMenu.isStillBoundToHostStack(differentMatchingStack, host)).isFalse()
        assertThat(RecipePaperMenu.isStillBoundToHostStack(ItemStack.EMPTY, host)).isFalse()
    }
}
