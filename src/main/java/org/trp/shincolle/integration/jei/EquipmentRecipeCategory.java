package org.trp.shincolle.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.init.ModItems;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;
import java.util.List;

public class EquipmentRecipeCategory implements IRecipeCategory<EquipmentRecipeWrapper> {
    public static final RecipeType<EquipmentRecipeWrapper> SMALL_TYPE =
            RecipeType.create(Shincolle.MODID, "small_equipment", EquipmentRecipeWrapper.class);
    public static final RecipeType<EquipmentRecipeWrapper> LARGE_TYPE =
            RecipeType.create(Shincolle.MODID, "large_equipment", EquipmentRecipeWrapper.class);

    private static final int GAP = 2;
    private static final int INPUT_ROW_Y = 2;
    private static final int FUEL_ROW_Y = 22;
    private static final int OUTPUT_X = 84;
    private static final int OUTPUT_Y = 10;
    private static final int SLOT_SPACING = 20;
    private static final int WIDTH = 106;
    private static final int HEIGHT = 56;

    private static final String LANG_KEY_SMALL = "jei.category.shincolle.small_equipment";
    private static final String LANG_KEY_LARGE = "jei.category.shincolle.large_equipment";

    private final RecipeType<EquipmentRecipeWrapper> recipeType;
    private final Component title;
    private final IDrawable background;

    public EquipmentRecipeCategory(IGuiHelper guiHelper, boolean large) {
        this.recipeType = large ? LARGE_TYPE : SMALL_TYPE;
        this.title = Component.translatable(large ? LANG_KEY_LARGE : LANG_KEY_SMALL);
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
    }

    @Override
    public RecipeType<EquipmentRecipeWrapper> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    @Nullable
    @SuppressWarnings("deprecation")
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return null;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EquipmentRecipeWrapper recipe, IFocusGroup focuses) {
        List<ItemStack> inputs = recipe.inputs();
        for (int i = 0; i < 4 && i < inputs.size(); i++) {
            int x = GAP + i * SLOT_SPACING;
            ItemStack stack = inputs.get(i);
            if (!stack.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.INPUT, x, INPUT_ROW_Y)
                        .addItemStack(stack);
            }
        }

        ItemStack fuel = recipe.fuel();
        if (!fuel.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, GAP, FUEL_ROW_Y)
                    .addItemStack(fuel);
        }

        List<ItemStack> outputs = recipe.outputs();
        if (!outputs.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                    .addItemStacks(outputs);
        }
    }

    @Override
    public void draw(EquipmentRecipeWrapper recipe, IRecipeSlotsView slotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.drawString(
            Minecraft.getInstance().font,
            Component.translatable("jei.shincolle.equipment.random_tip"),
            2, HEIGHT - 11, 0xFF888888, false);
    }
}
