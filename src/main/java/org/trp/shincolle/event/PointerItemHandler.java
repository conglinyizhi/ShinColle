package org.trp.shincolle.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.init.ModItems;
import org.trp.shincolle.item.PointerItem;

@EventBusSubscriber(modid = Shincolle.MODID)
public class PointerItemHandler {

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        handleLeftClick(event.getEntity());
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        
        
        if (event.getLevel().isClientSide) {
            handleLeftClick(event.getEntity());
            if (event.getEntity().isShiftKeyDown()) {
                event.setCanceled(true);
            }
        }
    }

    private static void handleLeftClick(Player player) {
        if (!player.isShiftKeyDown()) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (stack.is(ModItems.POINTER_ITEM.get()) && stack.getItem() instanceof PointerItem pointer) {
            pointer.onSwingMiss(player, stack);
        } else {
            stack = player.getOffhandItem();
            if (stack.is(ModItems.POINTER_ITEM.get()) && stack.getItem() instanceof PointerItem pointer) {
                pointer.onSwingMiss(player, stack);
            }
        }
    }
}
