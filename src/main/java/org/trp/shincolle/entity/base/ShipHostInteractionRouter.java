package org.trp.shincolle.entity.base;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.trp.shincolle.init.ModItems;
import org.trp.shincolle.item.CombatRationItem;

final class ShipHostInteractionRouter {
    private ShipHostInteractionRouter() {
    }

    static boolean shouldForwardToHost(ItemStack stack) {
        return stack.is(ModItems.MODERN_KIT.get())
                || stack.is(ModItems.TRAINING_BOOK.get())
                || stack.is(ModItems.BUCKET_REPAIR.get())
                || stack.getItem() instanceof CombatRationItem
                || stack.is(ModItems.TOY_AIRPLANE.get())
                || stack.is(ModItems.GRUDGE.get())
                || stack.is(ModItems.MARRIAGE_RING.get())
                || stack.is(ModItems.KAITAI_HAMMER.get());
    }

    static InteractionResult forwardToHost(EntityShipBase host, Player player, InteractionHand hand, ItemStack stack) {
        if (stack.is(ModItems.MODERN_KIT.get()) || stack.is(ModItems.TRAINING_BOOK.get())) {
            return stack.getItem().interactLivingEntity(stack, player, host, hand);
        }
        return host.mobInteract(player, hand);
    }
}
