package org.trp.shincolle.event

import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty
import org.trp.shincolle.Shincolle
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.item.PointerItem

@EventBusSubscriber(modid = Shincolle.MODID)
object PointerItemHandler {
    @JvmStatic
    @SubscribeEvent
    fun onLeftClickEmpty(event: LeftClickEmpty) {
        handleLeftClick(event.getEntity())
    }

    @JvmStatic
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
            (stack.getItem() as PointerItem).onSwingMiss(player, stack)
        } else {
            stack = player.getOffhandItem()
            if (stack.`is`(ModItems.POINTER_ITEM.get()) && stack.getItem() is PointerItem) {
                (stack.getItem() as PointerItem).onSwingMiss(player, stack)
            }
        }
    }
}
