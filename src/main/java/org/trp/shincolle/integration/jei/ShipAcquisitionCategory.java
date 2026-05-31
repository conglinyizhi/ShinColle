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
import org.trp.shincolle.init.ModItems;

import javax.annotation.Nullable;
import java.util.Map;

public class ShipAcquisitionCategory implements IRecipeCategory<ShipAcquisitionWrapper> {
    public static final RecipeType<ShipAcquisitionWrapper> TYPE =
            RecipeType.create(Shincolle.MODID, "ship_acquisition", ShipAcquisitionWrapper.class);

    private static final int SLOT_SIZE = 18;
    private static final int GAP = 2;
    private static final int ICON_ROW_Y = 10;
    private static final int EGG_X = 84;
    private static final int WIDTH = 106;
    private static final int HEIGHT = 70;

    private static final Map<String, String> CHAPTER_REF = Map.of(
            "jei.source.shincolle.small_shipyard", "jei.ref.shincolle.chap4",
            "jei.source.shincolle.large_shipyard", "jei.ref.shincolle.chap4",
            "jei.source.shincolle.wild_kanmusu", "jei.ref.shincolle.chap5"
    );

    private final Component title;
    private final IDrawable background;
    private final IDrawable icon;

    public ShipAcquisitionCategory(IGuiHelper guiHelper) {
        this.title = Component.translatable("jei.category.shincolle.ship_acquisition");
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(
            new ItemStack(ModItems.SHIPSPAWNEGGL.get()));
    }

    @Override
    public RecipeType<ShipAcquisitionWrapper> getRecipeType() { return TYPE; }

    @Override
    public Component getTitle() { return title; }

    @Override
    @Nullable @SuppressWarnings("deprecation")
    public IDrawable getBackground() { return background; }

    @Override
    public @Nullable IDrawable getIcon() { return icon; }

    @Override
    public int getWidth() { return WIDTH; }

    @Override
    public int getHeight() { return HEIGHT; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ShipAcquisitionWrapper recipe, IFocusGroup focuses) {
        for (int i = 0; i < recipe.sourceIcons().size() && i < 4; i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, GAP + i * 20, ICON_ROW_Y)
                    .addItemStack(recipe.sourceIcons().get(i));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, EGG_X, ICON_ROW_Y)
                .addItemStack(recipe.shipEgg());
    }

    @Override
    public void draw(ShipAcquisitionWrapper recipe, IRecipeSlotsView slotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        // Source labels below icons
        for (int i = 0; i < recipe.sourceLangKeys().size() && i < 4; i++) {
            String label = Component.translatable(recipe.sourceLangKeys().get(i)).getString();
            if (font.width(label) > 18) label = font.plainSubstrByWidth(label, 18);
            guiGraphics.drawString(font, label, GAP + i * 20, ICON_ROW_Y + SLOT_SIZE + 2, 0xFF888888, false);
        }
        // Chapter reference below source labels
        if (!recipe.sourceLangKeys().isEmpty()) {
            String chapKey = CHAPTER_REF.getOrDefault(recipe.sourceLangKeys().get(0), "");
            if (!chapKey.isEmpty()) {
                guiGraphics.drawString(font, Component.translatable(chapKey), GAP, HEIGHT - 11, 0xFF666666, false);
            }
        }
    }
}
