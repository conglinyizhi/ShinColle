package org.trp.shincolle.item

import net.minecraft.ChatFormatting
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuConstructor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.neoforged.fml.ModList
import org.trp.shincolle.init.ModDataComponents
import org.trp.shincolle.integration.PatchouliIntegration
import org.trp.shincolle.menu.DeskMenu
import java.util.function.Consumer

class DeskItemBook(properties: Properties) : Item(properties) {
    override fun getDescriptionId(): String {
        return "item.shincolle.deskitembook.name"
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)

        val chap = stack.getOrDefault<Int?>(ModDataComponents.BOOK_CHAPTER, 0)
        val page = stack.getOrDefault<Int?>(ModDataComponents.BOOK_PAGE, 0)

        if (ModList.get().isLoaded("patchouli") && PatchouliIntegration.hasBook(PATCHOULI_BOOK_ID)) {
            PatchouliIntegration.openBook(player, PATCHOULI_BOOK_ID)
            return InteractionResultHolder.sidedSuccess<ItemStack?>(stack, level.isClientSide)
        }

        if (!level.isClientSide && player is ServerPlayer) {
            player.openMenu(
                SimpleMenuProvider(
                    MenuConstructor { id: Int, inv: Inventory?, p: Player? -> DeskMenu(id, inv!!, 2, chap, page) },
                    Component.translatable("item.shincolle.deskitembook.name")
                ), Consumer { buffer: RegistryFriendlyByteBuf? ->
                    buffer!!.writeInt(2)
                    buffer.writeInt(chap)
                    buffer.writeInt(page)
                })
        }

        return InteractionResultHolder.sidedSuccess<ItemStack?>(stack, level.isClientSide)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
        tooltipComponents.add(
            Component.translatable("tooltip.shincolle.deskitembook.open_manual").withStyle(ChatFormatting.GOLD)
        )
        tooltipComponents.add(
            Component.translatable("tooltip.shincolle.deskitembook.patchouli_manual").withStyle(ChatFormatting.AQUA)
        )
    }

    companion object {
        val PATCHOULI_BOOK_ID: ResourceLocation = ResourceLocation.fromNamespaceAndPath("shincolle", "shincolle_manual")
    }
}
