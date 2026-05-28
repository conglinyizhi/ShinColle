package org.trp.shincolle.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.trp.shincolle.menu.RecipePaperMenu;
import org.trp.shincolle.utility.RecipePaperData;

import java.util.List;

public class RecipePaperItem extends Item {

    public RecipePaperItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new RecipePaperMenu(id, inv, stack, hand),
                    Component.translatable("gui.shincolle.recipepaper.title")
            ), buf -> buf.writeEnum(hand));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        ItemStack[] recipeGrid = RecipePaperData.loadRecipeGrid(stack, context.registries());
        List<ItemStack> inputList = new java.util.ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            ItemStack ingredient = recipeGrid[i];
            inputList.add(ingredient);
        }

        if (!RecipePaperData.hasAnyRecipeIngredient(recipeGrid)) {
            return;
        }

        ItemStack result = RecipePaperData.loadStoredRecipeResult(stack, context.registries());
        if (result.isEmpty() && context.level() != null) {
            result = RecipePaperData.getRecipePreviewResult(context.level(), inputList);
        }
        if (!result.isEmpty()) {
            tooltipComponents.add(Component.translatable("gui.shincolle.recipepaper.result")
                    .withStyle(ChatFormatting.YELLOW)
                    .append(" ")
                    .append(result.getHoverName().copy().withStyle(ChatFormatting.WHITE)));
        }

        tooltipComponents.add(Component.translatable("gui.shincolle.recipepaper.material").withStyle(ChatFormatting.AQUA));
        for (ItemStack ingredient : inputList) {
            if (!ingredient.isEmpty()) {
                tooltipComponents.add(Component.literal("  ").append(ingredient.getHoverName()).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
