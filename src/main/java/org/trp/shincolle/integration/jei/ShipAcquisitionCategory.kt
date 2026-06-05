package org.trp.shincolle.integration.jei

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.Shincolle
import org.trp.shincolle.init.ModItems

class ShipAcquisitionCategory(guiHelper: IGuiHelper) : IRecipeCategory<ShipAcquisitionWrapper> {

    private val title: Component = Component.translatable("jei.category.shincolle.ship_acquisition")
    private val background: IDrawable = guiHelper.createBlankDrawable(WIDTH, HEIGHT)
    private val icon: IDrawable = guiHelper.createDrawableItemStack(ItemStack(ModItems.SHIPSPAWNEGGL.get()))

    override fun getRecipeType(): RecipeType<ShipAcquisitionWrapper> = TYPE
    override fun getTitle(): Component = title
    override fun getBackground(): IDrawable = background
    override fun getIcon(): IDrawable = icon
    override fun getWidth(): Int = WIDTH
    override fun getHeight(): Int = HEIGHT

    override fun setRecipe(
        builder: IRecipeLayoutBuilder,
        recipe: ShipAcquisitionWrapper,
        focuses: IFocusGroup
    ) {
        var i = 0
        while (i < recipe.sourceIcons.size && i < 4) {
            builder.addSlot(RecipeIngredientRole.INPUT, GAP + i * 20, ICON_ROW_Y)
                .addItemStack(recipe.sourceIcons[i])
            i++
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, EGG_X, ICON_ROW_Y)
            .addItemStack(recipe.shipEgg)
    }

    override fun draw(
        recipe: ShipAcquisitionWrapper,
        slotsView: IRecipeSlotsView,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        val font = Minecraft.getInstance().font
        // Source labels below icons
        var i = 0
        while (i < recipe.sourceLangKeys.size && i < 4) {
            var label = Component.translatable(recipe.sourceLangKeys[i]).string
            if (font.width(label) > 18) label = font.plainSubstrByWidth(label, 18)
            guiGraphics.drawString(font, label, GAP + i * 20, ICON_ROW_Y + SLOT_SIZE + 2, -0x777778, false)
            i++
        }
        // Chapter reference below source labels
        if (recipe.sourceLangKeys.isNotEmpty()) {
            val chapKey = CHAPTER_REF[recipe.sourceLangKeys[0]]
            if (!chapKey.isNullOrEmpty()) {
                guiGraphics.drawString(font, Component.translatable(chapKey), GAP, HEIGHT - 11, -0x99999a, false)
            }
        }
    }

    companion object {
        val TYPE: RecipeType<ShipAcquisitionWrapper> =
            RecipeType.create(Shincolle.MODID, "ship_acquisition", ShipAcquisitionWrapper::class.java)

        private const val SLOT_SIZE = 18
        private const val GAP = 2
        private const val ICON_ROW_Y = 10
        private const val EGG_X = 84
        private const val WIDTH = 106
        private const val HEIGHT = 70

        private val CHAPTER_REF: Map<String, String> = mapOf(
            "jei.source.shincolle.small_shipyard" to "jei.ref.shincolle.chap4",
            "jei.source.shincolle.large_shipyard" to "jei.ref.shincolle.chap4",
            "jei.source.shincolle.wild_kanmusu" to "jei.ref.shincolle.chap5"
        )
    }
}
