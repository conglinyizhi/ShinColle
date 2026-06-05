package org.trp.shincolle.item

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuConstructor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.trp.shincolle.menu.DeskMenu
import java.util.function.Consumer

class DeskItemRadar(properties: Properties) : Item(properties) {
    override fun getDescriptionId(): String {
        return "item.shincolle.deskitemradar.name"
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)

        if (!level.isClientSide && player is ServerPlayer) {
            player.openMenu(
                SimpleMenuProvider(
                    MenuConstructor { id: Int, inv: Inventory?, p: Player? -> DeskMenu(id, inv!!, 1) },
                    Component.translatable("item.shincolle.deskitemradar.name")
                ), Consumer { buffer: RegistryFriendlyByteBuf? ->
                    buffer!!.writeInt(1)
                    buffer.writeInt(0)
                    buffer.writeInt(0)
                })
        }

        return InteractionResultHolder.sidedSuccess<ItemStack?>(stack, level.isClientSide)
    }
}
