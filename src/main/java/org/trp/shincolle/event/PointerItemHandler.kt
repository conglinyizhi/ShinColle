package org.trp.shincolle.event

import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty
import org.trp.shincolle.Shincolle
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.item.PointerItem
import org.trp.shincolle.item.PointerItem.onSwingMiss

@EventBusSubscriber(modid = Shincolle.MODID)
object PointerItemHandler {
    @SubscribeEvent
    fun onLeftClickEmpty(event: LeftClickEmpty) {
        handleLeftClick(event.getEntity())
    }

    @SubscribeEvent
    fun onLeftClickBlock(event: LeftClickBlock) {
        if (event.getLevel().isClientSide) {
            handleLeftClick(event.getEntity())
            if (event.getEntity().isShiftKeyDown()) {
                event.setCanceled(true)
            }
        }
    }

    private fun handleLeftClick(player: Player) {
        if (!player.isShiftKeyDown()) {
            return
        }
        var stack = player.getMainHandItem()
        if (stack.`is`(ModItems.POINTER_ITEM.get()) && stack.getItem() is PointerItem) {
            pointer.onSwingMiss(player, stack)
        } else {
            stack = player.getOffhandItem()
            if (stack.`is`(ModItems.POINTER_ITEM.get()) && stack.getItem() is PointerItem) {
                pointer.onSwingMiss(player, stack)
            }
        }
    }
}
