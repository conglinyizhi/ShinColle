package org.trp.shincolle.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.trp.shincolle.menu.RecipePaperMenu;

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
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("Recipe", 9)) {
                ListTag list = tag.getList("Recipe", 10);
                ItemStack[] stacks = new ItemStack[10];
                for (int i = 0; i < 10; i++) stacks[i] = ItemStack.EMPTY;

                for (int i = 0; i < list.size(); i++) {
                    CompoundTag itemTag = list.getCompound(i);
                    int slot = itemTag.getInt("Slot");
                    if (slot >= 0 && slot < 10) {
                        stacks[slot] = ItemStack.parseOptional(context.registries(), itemTag);
                    }
                }

                if (stacks[9].isEmpty() && context.level() != null) {
                    List<ItemStack> inputList = new java.util.ArrayList<>();
                    for (int i = 0; i < 9; i++) {
                        inputList.add(stacks[i]);
                    }
                    CraftingInput input = CraftingInput.of(3, 3, inputList);
                    context.level().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, context.level())
                            .ifPresent(recipe -> stacks[9] = recipe.value().assemble(input, context.registries()));
                }

                if (!stacks[9].isEmpty()) {
                    tooltipComponents.add(Component.translatable("gui.shincolle.recipepaper.result")
                            .withStyle(ChatFormatting.YELLOW)
                            .append(" ")
                            .append(stacks[9].getHoverName().copy().withStyle(ChatFormatting.WHITE)));
                }

                tooltipComponents.add(Component.translatable("gui.shincolle.recipepaper.material").withStyle(ChatFormatting.AQUA));
                for (int i = 0; i < 9; i++) {
                    if (!stacks[i].isEmpty()) {
                        tooltipComponents.add(Component.literal("  ").append(stacks[i].getHoverName()).withStyle(ChatFormatting.GRAY));
                    }
                }
            }
        }
    }
}
