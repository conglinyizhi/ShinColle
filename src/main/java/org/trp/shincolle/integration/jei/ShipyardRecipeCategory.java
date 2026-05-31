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
import net.minecraft.world.item.Items;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.client.gui.component.Sprites;

import javax.annotation.Nullable;
import java.util.List;

public class ShipyardRecipeCategory implements IRecipeCategory<ShipyardRecipeWrapper> {
    public static final RecipeType<ShipyardRecipeWrapper> SMALL_TYPE =
            RecipeType.create(Shincolle.MODID, "small_shipyard", ShipyardRecipeWrapper.class);
    public static final RecipeType<ShipyardRecipeWrapper> LARGE_TYPE =
            RecipeType.create(Shincolle.MODID, "large_shipyard", ShipyardRecipeWrapper.class);

    private static final int[] SM_MAT_X = {33, 53, 73, 93};
    private static final int SM_MAT_Y = 29;
    private static final int SM_FUEL_X = 8;
    private static final int SM_FUEL_Y = 53;
    private static final int SM_OUT_X = 134;
    private static final int SM_OUT_Y = 44;
    private static final int SM_W = 176;
    private static final int SM_H = 86;
    private static final int SM_TIME_X = 71;
    private static final int SM_TIME_Y = 51;

    private static final int LG_MAT_X = 27;
    private static final int[] LG_MAT_Y = {14, 33, 52, 71};
    private static final int LG_OUT_X = 168;
    private static final int LG_OUT_Y = 51;
    private static final int LG_W = 208;
    private static final int LG_H = 92;
    // Power bar area (large shipyard): x=8..22, y=19..84
    private static final int LG_POWER_X = 9;
    private static final int LG_POWER_Y = 19;
    private static final int LG_POWER_W = 13;
    private static final int LG_POWER_H = 65;

    private final RecipeType<ShipyardRecipeWrapper> recipeType;
    private final Component title;
    private final IDrawable background;
    private final int bgW, bgH;
    private final boolean large;

    public ShipyardRecipeCategory(IGuiHelper guiHelper, boolean large) {
        this.large = large;
        this.recipeType = large ? LARGE_TYPE : SMALL_TYPE;
        this.title = Component.translatable(large
                ? "jei.title.shincolle.large_shipyard"
                : "jei.title.shincolle.small_shipyard");
        if (large) {
            bgW = LG_W; bgH = LG_H;
            this.background = guiHelper.createDrawable(Sprites.T_LARGE_SHIPYARD, 0, 0, LG_W, LG_H);
        } else {
            bgW = SM_W; bgH = SM_H;
            this.background = guiHelper.createDrawable(Sprites.T_SMALL_SHIPYARD, 0, 0, SM_W, SM_H);
        }
    }

    @Override public RecipeType<ShipyardRecipeWrapper> getRecipeType() { return recipeType; }
    @Override public Component getTitle() { return title; }
    @Override @Nullable @SuppressWarnings("deprecation") public IDrawable getBackground() { return background; }
    @Override public @Nullable IDrawable getIcon() { return null; }
    @Override public int getWidth() { return bgW; }
    @Override public int getHeight() { return bgH; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ShipyardRecipeWrapper recipe, IFocusGroup focuses) {
        if (large) {
            builder.addSlot(RecipeIngredientRole.INPUT, LG_MAT_X, LG_MAT_Y[0]).addItemStack(recipe.inputs().get(0));
            builder.addSlot(RecipeIngredientRole.INPUT, LG_MAT_X, LG_MAT_Y[1]).addItemStack(recipe.inputs().get(1));
            builder.addSlot(RecipeIngredientRole.INPUT, LG_MAT_X, LG_MAT_Y[2]).addItemStack(recipe.inputs().get(2));
            builder.addSlot(RecipeIngredientRole.INPUT, LG_MAT_X, LG_MAT_Y[3]).addItemStack(recipe.inputs().get(3));
            // Invisible fuel indicator slot at power bar area (tooltip on hover)
            builder.addSlot(RecipeIngredientRole.INPUT, LG_POWER_X, LG_POWER_Y + LG_POWER_H - 18)
                    .addItemStacks(getFuelItems())
                    .addTooltipCallback((slot, lines) -> {
                        lines.add(Component.translatable("jei.shincolle.fuel_hint_large"));
                    });
            builder.addSlot(RecipeIngredientRole.OUTPUT, LG_OUT_X, LG_OUT_Y).addItemStacks(recipe.outputs());
        } else {
            for (int i = 0; i < 4; i++) {
                builder.addSlot(RecipeIngredientRole.INPUT, SM_MAT_X[i], SM_MAT_Y)
                        .addItemStack(recipe.inputs().get(i));
            }
            builder.addSlot(RecipeIngredientRole.INPUT, SM_FUEL_X, SM_FUEL_Y).addItemStacks(getFuelItems());
            builder.addSlot(RecipeIngredientRole.OUTPUT, SM_OUT_X, SM_OUT_Y).addItemStacks(recipe.outputs());
        }
    }

    @Override
    public void draw(ShipyardRecipeWrapper recipe, IRecipeSlotsView slotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        if (large) {
            // Dark underlay + lava fill from GUI texture
            guiGraphics.fill(LG_POWER_X, LG_POWER_Y, LG_POWER_X + 12, LG_POWER_Y + LG_POWER_H, 0xFF331100);
            // Draw lava fill from GUI texture (power bar fill at UV 208,0)
            guiGraphics.blit(Sprites.T_LARGE_SHIPYARD,
                LG_POWER_X, LG_POWER_Y, 208, 0,
                12, LG_POWER_H, 256, 256);
            // Reference text
            var text = Component.translatable("jei.shincolle.ref_shipyard");
            guiGraphics.drawString(font, text, (bgW - font.width(text)) / 2, 4, 0xFF666666, false);
        } else {
            // Fuel estimate
            int totalMats = recipe.inputs().stream().filter(s -> !s.isEmpty()).mapToInt(ItemStack::getCount).sum();
            int power = 57600 + Math.max(0, totalMats - 64) * 2100;
            int lavaNeeded = (int) Math.ceil((double) power / 20000.0);
            String fuelStr = String.format("\u9700\u5CA9\u6D46: %d\u6876", lavaNeeded);
            guiGraphics.drawString(font, fuelStr, SM_TIME_X - font.width(fuelStr) / 2, SM_TIME_Y, 0xFFFF6600, false);
            // Lava fill for small shipyard (power bar at UV 176,16)
            guiGraphics.fill(10, 17, 10 + 12, 17 + 31, 0xFF331100);
            guiGraphics.blit(Sprites.T_SMALL_SHIPYARD, 10, 17, 176, 16, 12, 31, 256, 256);
            // Reference text centered
            var ref = Component.translatable("jei.shincolle.ref_shipyard");
            guiGraphics.drawString(font, ref, (bgW - font.width(ref)) / 2, bgH - 11, 0xFF666666, false);
        }
    }

    private static List<ItemStack> getFuelItems() {
        return List.of(
            new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.COAL_BLOCK),
            new ItemStack(Items.COAL), new ItemStack(Items.CHARCOAL),
            new ItemStack(Items.BLAZE_ROD), new ItemStack(Items.DRIED_KELP_BLOCK),
            new ItemStack(Items.BAMBOO_BLOCK));
    }
}
