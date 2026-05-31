package org.trp.shincolle.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.trp.shincolle.Shincolle;

import javax.annotation.Nullable;

public class ShipAcquisitionCategory implements IRecipeCategory<ShipAcquisitionWrapper> {
    public static final RecipeType<ShipAcquisitionWrapper> TYPE =
            RecipeType.create(Shincolle.MODID, "ship_acquisition", ShipAcquisitionWrapper.class);

    private static final int SLOT_SIZE = 18;
    private static final int GAP = 2;
    private static final int ICON_ROW_Y = 2;
    private static final int EGG_X = 84;
    private static final int WIDTH = 106;
    private static final int HEIGHT = 56;

    private final Component title;
    private final IDrawable background;

    public ShipAcquisitionCategory(IGuiHelper guiHelper) {
        this.title = Component.translatable("jei.category.shincolle.ship_acquisition");
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
    }

    @Override
    public RecipeType<ShipAcquisitionWrapper> getRecipeType() {
        return TYPE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, ShipAcquisitionWrapper recipe, IFocusGroup focuses) {
        // Source icons as inputs
        for (int i = 0; i < recipe.sourceIcons().size() && i < 4; i++) {
            int x = GAP + i * 20;
            builder.addSlot(RecipeIngredientRole.INPUT, x, ICON_ROW_Y)
                    .addItemStack(recipe.sourceIcons().get(i));
        }
        // Ship egg as output
        builder.addSlot(RecipeIngredientRole.OUTPUT, EGG_X, 10)
                .addItemStack(recipe.shipEgg());
    }

    @Override
    public void draw(ShipAcquisitionWrapper recipe, IRecipeSlotsView slotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Draw source labels below icons
        var font = Minecraft.getInstance().font;
        for (int i = 0; i < recipe.sourceLangKeys().size() && i < 4; i++) {
            String label = Component.translatable(recipe.sourceLangKeys().get(i)).getString();
            // Truncate if too long
            if (font.width(label) > 18) {
                label = font.plainSubstrByWidth(label, 18);
            }
            int x = GAP + i * 20;
            guiGraphics.drawString(font, label, x, ICON_ROW_Y + SLOT_SIZE + 1, 0xFF888888, false);
        }
    }
}
