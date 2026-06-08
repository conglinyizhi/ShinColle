package org.trp.shincolle.server

import net.minecraft.world.item.ItemStack
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Optional
import java.util.UUID

class PointerInteractionServiceTest {

    @Test
    fun `pointer interaction service should reject null players at lightweight entrypoints`() {
        assertThat(PointerInteractionService.canAssignPointerEntityTarget(null, null, null)).isFalse()
        assertThat(PointerInteractionService.getPointerStack(null)).isEqualTo(ItemStack.EMPTY)
        assertThat(PointerInteractionService.handlePointerAttack(null, null)).isFalse()
        assertThat(PointerInteractionService.getLookTargetResult(null)).isNull()

        PointerInteractionService.handlePayloadAction(null, ItemStack.EMPTY, 0, Optional.empty(), Optional.empty())
        PointerInteractionService.handleAttackSelection(null, ItemStack.EMPTY, null)
        PointerInteractionService.handleTargetCommand(null, ItemStack.EMPTY)
    }

    @Test
    fun `pointer interaction service should ignore empty stacks and null identifiers`() {
        PointerInteractionService.handlePayloadAction(
            null,
            ItemStack.EMPTY,
            3,
            Optional.of(UUID.randomUUID()),
            Optional.empty()
        )
    }
}
