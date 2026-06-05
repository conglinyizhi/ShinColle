package org.trp.shincolle.integration.jei

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import net.minecraft.client.gui.Font
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Items
import org.trp.shincolle.client.gui.component.Sprites
import java.util.List
import kotlin.math.ceil
import kotlin.math.max

class EquipmentRecipeCategory(guiHelper: IGuiHelper, private val large: Boolean) :
    IRecipeCategory<EquipmentRecipeWrapper?> {
    private val recipeType: RecipeType<EquipmentRecipeWrapper?>?
    val title: Component
    private val background: IDrawable?

    init {
        this.recipeType = if (large) LARGE_TYPE else SMALL_TYPE
        this.title = Component.translatable(
            if (large)
                "jei.title.shincolle.large_equipment"
            else
                "jei.title.shincolle.small_equipment"
        )
        if (large) {
            this.background = guiHelper.createDrawable(Sprites.T_LARGE_SHIPYARD, 0, 0, LG_W, LG_H)
        } else {
            this.background = guiHelper.createDrawable(Sprites.T_SMALL_SHIPYARD, 0, 0, SM_W, SM_H)
        }
    }

    public override fun getRecipeType(): RecipeType<EquipmentRecipeWrapper?>? {
        return recipeType
    }

    @Suppress("deprecation")
    public override fun getBackground(): IDrawable? {
        return background
    }

    val icon: IDrawable?
        get() = null
    val width: Int
        get() = if (large) LG_W else SM_W
    val height: Int
        get() = if (large) LG_H else SM_H

    public override fun setRecipe(
        builder: IRecipeLayoutBuilder,
        recipe: EquipmentRecipeWrapper,
        focuses: IFocusGroup?
    ) {
        if (large) {
            builder.addSlot(RecipeIngredientRole.INPUT, LG_MAT_X, LG_MAT_Y[0]).addItemStack(recipe.inputs.get(0))
            builder.addSlot(RecipeIngredientRole.INPUT, LG_MAT_X, LG_MAT_Y[1]).addItemStack(recipe.inputs.get(1))
            builder.addSlot(RecipeIngredientRole.INPUT, LG_MAT_X, LG_MAT_Y[2]).addItemStack(recipe.inputs.get(2))
            builder.addSlot(RecipeIngredientRole.INPUT, LG_MAT_X, LG_MAT_Y[3]).addItemStack(recipe.inputs.get(3))
            // Invisible fuel slot at bottom of power bar
            builder.addSlot(RecipeIngredientRole.INPUT, LG_POWER_X, LG_POWER_Y + LG_POWER_H - 18)
                .addItemStacks(fuelItems)
                .addTooltipCallback({ slot, lines -> lines.add(Component.translatable("jei.shincolle.fuel_hint_large")) })
            builder.addSlot(RecipeIngredientRole.OUTPUT, LG_OUT_X, LG_OUT_Y).addItemStacks(recipe.outputs)
        } else {
            for (i in 0..3) {
                builder.addSlot(RecipeIngredientRole.INPUT, SM_MAT_X[i], SM_MAT_Y).addItemStack(recipe.inputs.get(i))
            }
            builder.addSlot(RecipeIngredientRole.INPUT, SM_FUEL_X, SM_FUEL_Y).addItemStacks(
                fuelItems
            )
            builder.addSlot(RecipeIngredientRole.OUTPUT, SM_OUT_X, SM_OUT_Y).addItemStacks(recipe.outputs)
        }
    }

    public override fun draw(
        recipe: EquipmentRecipeWrapper,
        slotsView: IRecipeSlotsView?,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        val font: Font = Minecraft.getInstance().font
        if (large) {
            // Darken the lava fill by drawing a dark underlay first
            guiGraphics.fill(LG_POWER_X, LG_POWER_Y, LG_POWER_X + 12, LG_POWER_Y + LG_POWER_H, -0xccef00)
            guiGraphics.blit(Sprites.T_LARGE_SHIPYARD, LG_POWER_X, LG_POWER_Y, 208f, 0f, 12, LG_POWER_H, 256, 256)
            val text: MutableComponent = Component.translatable("jei.shincolle.ref_equip")
            guiGraphics.drawString(font, text, (LG_W - font.width(text)) / 2, 4, -0x99999a, false)
        } else {
            // Lava fill for small equipment
            guiGraphics.fill(10, 17, 10 + 12, 17 + 31, -0xccef00)
            guiGraphics.blit(Sprites.T_SMALL_SHIPYARD, 10, 17, 176f, 16f, 12, 31, 256, 256)
            // Draw flickering fuel estimate
            val totalMats = recipe.inputs.stream().filter { s: ItemStack? -> !s.isEmpty() }
                .mapToInt { obj: ItemStack? -> obj.getCount() }.sum()
            val power = 57600 + max(0, totalMats - 64) * 2100
            val lavaNeeded = ceil(power.toDouble() / 20000.0).toInt()
            val coalNeeded = ceil(power.toDouble() / 1600.0).toInt()
            val showLava = (System.currentTimeMillis() / 1000) % 2 == 0L
            val fuelStr = if (showLava) String.format("%d桶岩浆", lavaNeeded) else String.format("%d个煤", coalNeeded)
            val color = if (showLava) -0x9a00 else -0x777778
            guiGraphics.drawString(font, fuelStr, 71 - font.width(fuelStr) / 2, 51, color, false)
            val text: MutableComponent = Component.translatable("jei.shincolle.ref_equip")
            guiGraphics.drawString(font, text, (SM_W - font.width(text)) / 2, SM_H - 11, -0x99999a, false)
        }
    }

    companion object {
        val SMALL_TYPE: RecipeType<EquipmentRecipeWrapper?>? =
            RecipeType.create(Shincolle.MODID, "small_equipment", EquipmentRecipeWrapper::class.java)
        val LARGE_TYPE: RecipeType<EquipmentRecipeWrapper?>? =
            RecipeType.create(Shincolle.MODID, "large_equipment", EquipmentRecipeWrapper::class.java)

        private val SM_MAT_X = intArrayOf(33, 53, 73, 93)
        private const val SM_MAT_Y = 29
        private const val SM_FUEL_X = 8
        private const val SM_FUEL_Y = 53
        private const val SM_OUT_X = 134
        private const val SM_OUT_Y = 44
        private const val SM_W = 176
        private const val SM_H = 86

        private const val LG_MAT_X = 27
        private val LG_MAT_Y = intArrayOf(14, 33, 52, 71)
        private const val LG_OUT_X = 168
        private const val LG_OUT_Y = 51
        private const val LG_POWER_X = 9
        private const val LG_POWER_Y = 19
        private const val LG_POWER_H = 65
        private const val LG_W = 208
        private const val LG_H = 92

        private val fuelItems: MutableList<ItemStack>
            get() = List.of<ItemStack?>(
                ItemStack(Items.LAVA_BUCKET),
                ItemStack(Items.COAL_BLOCK),
                ItemStack(Items.COAL),
                ItemStack(Items.CHARCOAL),
                ItemStack(Items.BLAZE_ROD),
                ItemStack(Items.DRIED_KELP_BLOCK),
                ItemStack(Items.BAMBOO_BLOCK)
            )
    }
}
