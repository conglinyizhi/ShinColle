package org.trp.shincolle.item

import net.minecraft.ChatFormatting
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
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.trp.shincolle.menu.RecipePaperMenu
import org.trp.shincolle.utility.RecipePaperData
import java.util.function.Consumer

class RecipePaperItem(properties: Properties) : Item(properties.stacksTo(1)) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)
        if (!level.isClientSide && player is ServerPlayer) {
            player.openMenu(
                SimpleMenuProvider(
                    MenuConstructor { id: Int, inv: Inventory?, p: Player? -> RecipePaperMenu(id, inv!!, stack, hand) },
                    Component.translatable("gui.shincolle.recipepaper.title")
                ), Consumer { buf: RegistryFriendlyByteBuf? -> buf!!.writeEnum(hand) })
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
        val recipeGrid = RecipePaperData.loadRecipeGrid(stack, context.registries()!!)
        val inputList: MutableList<ItemStack> = recipeGridAsList(recipeGrid)

        if (!RecipePaperData.hasAnyRecipeIngredient(inputList)) {
            return
        }

        var result = RecipePaperData.loadStoredRecipeResult(stack, context.registries()!!)
        if (result.isEmpty() && context.level() != null) {
            result = RecipePaperData.getRecipePreviewResult(context.level()!!, inputList as MutableList<ItemStack?>)
        }
        appendRecipePreviewTooltip(tooltipComponents, inputList, result)
    }

    companion object {
        fun recipeGridAsList(recipeGrid: Array<ItemStack?>): MutableList<ItemStack> {
            val inputList: MutableList<ItemStack> = ArrayList<ItemStack>(9)
            for (i in 0..8) {
                inputList.add(recipeGrid[i]!!)
            }
            return inputList
        }

        fun appendRecipePreviewTooltip(
            tooltipComponents: MutableList<Component?>,
            inputList: MutableList<ItemStack>,
            result: ItemStack
        ) {
            if (!result.isEmpty()) {
                tooltipComponents.add(
                    Component.translatable("gui.shincolle.recipepaper.result")
                        .withStyle(ChatFormatting.YELLOW)
                        .append(" ")
                        .append(result.hoverName.copy().withStyle(ChatFormatting.WHITE))
                )
            }

            tooltipComponents.add(
                Component.translatable("gui.shincolle.recipepaper.material").withStyle(ChatFormatting.AQUA)
            )
            for (ingredient in inputList) {
                if (!ingredient.isEmpty()) {
                    tooltipComponents.add(
                        Component.literal("  ").append(ingredient.hoverName).withStyle(ChatFormatting.GRAY)
                    )
                }
            }
        }
    }
}
