package org.trp.shincolle.integration.jei

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import net.minecraft.client.gui.Font
import net.minecraft.network.chat.Component
import java.util.Map

class ShipAcquisitionCategory(guiHelper: IGuiHelper) : IRecipeCategory<ShipAcquisitionWrapper?> {
    val title: Component
    private val background: IDrawable?
    private val icon: IDrawable?

    init {
        this.title = Component.translatable("jei.category.shincolle.ship_acquisition")
        this.background = guiHelper.createBlankDrawable(width, height)
        this.icon = guiHelper.createDrawableItemStack(
            ItemStack(ModItems.SHIPSPAWNEGGL.get())
        )
    }

    val recipeType: RecipeType<ShipAcquisitionWrapper?>?
        get() = TYPE

    @Suppress("deprecation")
    public override fun getBackground(): IDrawable? {
        return background
    }

    public override fun getIcon(): IDrawable? {
        return icon
    }

    public override fun setRecipe(
        builder: IRecipeLayoutBuilder,
        recipe: ShipAcquisitionWrapper,
        focuses: IFocusGroup?
    ) {
        var i = 0
        while (i < recipe.sourceIcons.size && i < 4) {
            builder.addSlot(RecipeIngredientRole.INPUT, GAP + i * 20, ICON_ROW_Y)
                .addItemStack(recipe.sourceIcons.get(i))
            i++
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, EGG_X, ICON_ROW_Y)
            .addItemStack(recipe.shipEgg)
    }

    public override fun draw(
        recipe: ShipAcquisitionWrapper,
        slotsView: IRecipeSlotsView?,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        val font: Font = Minecraft.getInstance().font
        // Source labels below icons
        var i = 0
        while (i < recipe.sourceLangKeys.size && i < 4) {
            var label = Component.translatable(recipe.sourceLangKeys.get(i)).getString()
            if (font.width(label) > 18) label = font.plainSubstrByWidth(label, 18)
            guiGraphics.drawString(font, label, GAP + i * 20, ICON_ROW_Y + SLOT_SIZE + 2, -0x777778, false)
            i++
        }
        // Chapter reference below source labels
        if (!recipe.sourceLangKeys.isEmpty()) {
            val chapKey = CHAPTER_REF.getOrDefault(recipe.sourceLangKeys.get(0), "")
            if (!chapKey.isEmpty()) {
                guiGraphics.drawString(font, Component.translatable(chapKey), GAP, height - 11, -0x99999a, false)
            }
        }
    }

    companion object {
        val TYPE: RecipeType<ShipAcquisitionWrapper?>? =
            RecipeType.create(Shincolle.MODID, "ship_acquisition", ShipAcquisitionWrapper::class.java)

        private const val SLOT_SIZE = 18
        private const val GAP = 2
        private const val ICON_ROW_Y = 10
        private const val EGG_X = 84
        val width: Int = 106
            get() = Companion.field
        val height: Int = 70
            get() = Companion.field

        private val CHAPTER_REF: MutableMap<String?, String> = Map.of<String?, String?>(
            "jei.source.shincolle.small_shipyard", "jei.ref.shincolle.chap4",
            "jei.source.shincolle.large_shipyard", "jei.ref.shincolle.chap4",
            "jei.source.shincolle.wild_kanmusu", "jei.ref.shincolle.chap5"
        )
    }
}
