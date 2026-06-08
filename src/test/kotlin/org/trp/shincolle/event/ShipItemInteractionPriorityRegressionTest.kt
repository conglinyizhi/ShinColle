package org.trp.shincolle.event

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class ShipItemInteractionPriorityRegressionTest {
    private val SHIP_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt")
    private val MOUNT_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/entity/base/EntityMountBase.kt")
    private val HOST_INTERACTION_ROUTER_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/entity/base/ShipHostInteractionRouter.kt")

    @Test
    fun shipMobInteractShouldLetItemEntityInteractionsWinBeforeDefaultShipInteraction() {
        val source = Files.readString(SHIP_SOURCE)

        assertTrue(!source.contains("InteractionResult itemInteractionResult = stack.getItem().interactLivingEntity(stack, player, this, hand);")) {
            "Ship interactions should not manually duplicate held-item entity interaction"
        }
        assertTrue(source.contains("if (!this.level().isClientSide && hand == InteractionHand.MAIN_HAND) {")) {
            "Ship interactions should still preserve the server-side default interaction gate"
        }
        assertTrue(source.contains("if (stack.is(ModItems.TRAINING_BOOK.get()) || stack.is(ModItems.MODERN_KIT.get())) {")) {
            "Ship interactions should leave training book and modernization kit to the vanilla item->entity interaction path"
        }
        assertFalse(source.contains("Ship mobInteract")) {
            "Ship interactions should not emit noisy per-click diagnostics during normal right-click routing"
        }
    }

    @Test
    fun mountMobInteractShouldHonorHostInteractionBeforeStartingRide() {
        val source = Files.readString(MOUNT_SOURCE)

        assertTrue(source.contains("if (this.host != null && this.host.isOwnedBy(player) && ShipHostInteractionRouter.shouldForwardToHost(heldStack)) {")) {
            "Mount interactions should only forward host-bound ship tools"
        }
        assertTrue(source.contains("InteractionResult hostInteractionResult = ShipHostInteractionRouter.forwardToHost(this.host, player, hand, heldStack);")) {
            "Mount interactions should forward selected right-clicks through the host-aware router"
        }
        assertTrue(source.contains("if (hostInteractionResult != InteractionResult.PASS) {")) {
            "Mount interactions should stop when the host ship handled the right-click"
        }
        assertTrue(source.contains("if (this.level().isClientSide) return InteractionResult.SUCCESS;")) {
            "Mount interactions should keep the client-side success path"
        }
        assertTrue(source.contains("if (!player.isSecondaryUseActive()) {")) {
            "Mount interactions should stop before riding when the forwarded host interaction already handled the right-click"
        }
        assertFalse(source.contains("Mount mobInteract")) {
            "Mount interactions should not emit noisy per-click diagnostics during normal right-click routing"
        }
    }

    @Test
    fun hostInteractionForwardingRulesShouldStayOutOfMountEntity() {
        val mount = Files.readString(MOUNT_SOURCE)
        val router = Files.readString(HOST_INTERACTION_ROUTER_SOURCE)

        assertTrue(router.contains("final class ShipHostInteractionRouter")) {
            "Host interaction forwarding rules should live in a small router"
        }
        assertTrue(router.contains("static boolean shouldForwardToHost(ItemStack stack)")) {
            "Router should own the host-bound item predicate"
        }
        assertTrue(router.contains("static InteractionResult forwardToHost(EntityShipBase host, Player player, InteractionHand hand, ItemStack stack)")) {
            "Router should own the host interaction dispatch"
        }
        assertTrue(router.contains("stack.is(ModItems.MODERN_KIT.get()) || stack.is(ModItems.TRAINING_BOOK.get())")) {
            "Router should preserve direct item->entity dispatch for tools implemented as items"
        }
        assertTrue(router.contains("return host.mobInteract(player, hand);")) {
            "Router should preserve default ship interaction forwarding for host-bound items"
        }
        assertTrue(!mount.contains("stack.is(org.trp.shincolle.init.ModItems")) {
            "Mount entity should not own a hard-coded ship-tool item list"
        }
        assertTrue(!mount.contains("stack.getItem() instanceof org.trp.shincolle.item")) {
            "Mount entity should not own item-class forwarding rules"
        }
    }
}
