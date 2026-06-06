package org.trp.shincolle.item

import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.enchantment.ItemEnchantments
import java.util.*
import java.util.function.Consumer
import java.util.function.UnaryOperator
import kotlin.math.max
import kotlin.math.min

class LegacyEquipItem @JvmOverloads constructor(
    properties: Properties,
    private val legacyNameBase: String,
    equipTypeByVariant: IntArray,
    modelVariantByVariant: IntArray? = null
) : Item(properties.stacksTo(1)) {
    private val equipTypeByVariant: IntArray
    private val modelVariantByVariant: IntArray

    init {
        this.equipTypeByVariant = equipTypeByVariant.clone()
        this.modelVariantByVariant = IntArray(this.equipTypeByVariant.size)
        if (modelVariantByVariant != null) {
            val copyLen = min(this.modelVariantByVariant.size, modelVariantByVariant.size)
            for (i in 0..<copyLen) {
                this.modelVariantByVariant[i] = max(0, modelVariantByVariant[i])
            }
        }
    }

    val variantCount: Int
        get() = this.equipTypeByVariant.size

    fun getVariant(stack: ItemStack): Int {
        if (this.equipTypeByVariant.size <= 1) {
            return 0
        }

        val customData = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
        if (customData == null) {
            return 0
        }

        val raw = customData.copyTag().getInt(TAG_VARIANT)
        return Mth.clamp(raw, 0, this.equipTypeByVariant.size - 1)
    }

    fun getEquipTypeId(stack: ItemStack): Int {
        return this.equipTypeByVariant[getVariant(stack)]
    }

    fun getEquipId(stack: ItemStack): Int {
        val variant = getVariant(stack)
        return this.equipTypeByVariant[variant] + variant * 100
    }

    fun getModelVariant(stack: ItemStack): Int {
        return this.modelVariantByVariant[getVariant(stack)]
    }

    fun createVariantStack(variant: Int): ItemStack {
        val clamped = Mth.clamp(variant, 0, this.equipTypeByVariant.size - 1)
        val stack = ItemStack(this)

        if (clamped > 0) {
            stack.update<CustomData?>(
                DataComponents.CUSTOM_DATA, CustomData.EMPTY,
                UnaryOperator { data: CustomData? ->
                    data!!.update(Consumer { tag: CompoundTag? ->
                        tag!!.putInt(
                            TAG_VARIANT,
                            clamped
                        )
                    })
                })
        }

        return stack
    }

    fun addAllVariantsToCreativeTab(output: CreativeModeTab.Output) {
        for (variant in this.equipTypeByVariant.indices) {
            output.accept(createVariantStack(variant))
        }
    }

    override fun getName(stack: ItemStack): Component {
        val variant = getVariant(stack)
        val suffix = if (variant > 0) variant.toString() else ""
        return Component.translatable("item.shincolle." + this.legacyNameBase + suffix + ".name")
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    override fun isFoil(stack: ItemStack): Boolean {
        if (super.isFoil(stack)) {
            return true
        }

        return hasLegacyEnchantData(stack) || hasLegacyFoilVariant(stack)
    }

    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        val scaledLines: MutableList<Component?> = ArrayList<Component?>()

        addSpecialTooltip(stack, scaledLines)

        val equipId = getEquipId(stack)
        val main = LegacyEquipStats.getMainAttrs(equipId)
        val misc = LegacyEquipStats.getMiscAttrs(equipId)
        if (main != null && misc != null) {
            addMainTooltip(main, scaledLines)
            addMiscTooltip(misc, scaledLines)
        }

        if (scaledLines.isEmpty()) {
            return Optional.empty()
        }

        return Optional.of(ScaledTextTooltipData(scaledLines, 1.0f))
    }

    private fun addSpecialTooltip(stack: ItemStack, tooltipComponents: MutableList<Component?>) {
        val variant = getVariant(stack)

        when (this.legacyNameBase) {
            "EquipCompass" -> tooltipComponents.add(
                Component.translatable("gui.shincolle.compass").withStyle(ChatFormatting.GRAY)
            )

            "EquipFlare" -> tooltipComponents.add(
                Component.translatable("gui.shincolle.flare").withStyle(ChatFormatting.GRAY)
            )

            "EquipSearchlight" -> tooltipComponents.add(
                Component.translatable("gui.shincolle.searchlight").withStyle(ChatFormatting.GRAY)
            )

            "EquipDrum" -> {
                if (variant == 1) {
                    tooltipComponents.add(Component.translatable("gui.shincolle.drum1").withStyle(ChatFormatting.GRAY))
                } else if (variant == 2) {
                    tooltipComponents.add(Component.translatable("gui.shincolle.drum2b").withStyle(ChatFormatting.GRAY))
                } else {
                    tooltipComponents.add(Component.translatable("gui.shincolle.drum").withStyle(ChatFormatting.GRAY))
                }
            }

            "EquipAmmo" -> {
                if (variant == 5) {
                    tooltipComponents.add(
                        Component.translatable("gui.shincolle.equip.gravity").withStyle(ChatFormatting.YELLOW)
                    )
                } else if (variant == 8) {
                    tooltipComponents.add(
                        Component.translatable("gui.shincolle.equip.cluster").withStyle(ChatFormatting.YELLOW)
                    )
                }
            }

            "EquipTorpedo" -> {
                val speedLevel: Int = getTorpedoSpeedLevel(variant)
                if (speedLevel > 0) {
                    tooltipComponents.add(
                        Component.translatable("gui.shincolle.equip.torpedospeed", speedLevel)
                            .withStyle(ChatFormatting.YELLOW)
                    )
                }
            }

            else -> {}
        }
    }

    private fun hasLegacyFoilVariant(stack: ItemStack): Boolean {
        return "EquipAmmo" == this.legacyNameBase && getVariant(stack) == 7
    }

    companion object {
        private const val TAG_VARIANT = "LegacyVariant"

        private fun getTorpedoSpeedLevel(variant: Int): Int {
            return when (variant) {
                3, 4 -> 1
                5 -> 2
                6 -> 3
                else -> 0
            }
        }

        private fun addMainTooltip(main: FloatArray, tooltipComponents: MutableList<Component?>) {
            addFlatStat(tooltipComponents, main[0], "gui.shincolle.hp", ChatFormatting.RED, 1)
            addFlatStat(tooltipComponents, main[1], "gui.shincolle.firepower1", ChatFormatting.RED, 1)
            addFlatStat(tooltipComponents, main[2], "gui.shincolle.torpedo", ChatFormatting.GREEN, 1)
            addFlatStat(tooltipComponents, main[3], "gui.shincolle.airfirepower", ChatFormatting.RED, 1)
            addFlatStat(tooltipComponents, main[4], "gui.shincolle.airtorpedo", ChatFormatting.GREEN, 1)
            addPercentStat(tooltipComponents, main[5], "gui.shincolle.armor", ChatFormatting.WHITE, 1)
            addFlatStat(tooltipComponents, main[6], "gui.shincolle.attackspeed", ChatFormatting.WHITE, 2)
            addFlatStat(tooltipComponents, main[7], "gui.shincolle.movespeed", ChatFormatting.GRAY, 2)
            addFlatStat(tooltipComponents, main[8], "gui.shincolle.range", ChatFormatting.LIGHT_PURPLE, 1)
            addPercentStat(tooltipComponents, main[9], "gui.shincolle.critical", ChatFormatting.AQUA, 0)
            addPercentStat(tooltipComponents, main[10], "gui.shincolle.doublehit", ChatFormatting.YELLOW, 0)
            addPercentStat(tooltipComponents, main[11], "gui.shincolle.triplehit", ChatFormatting.GOLD, 0)
            addPercentStat(tooltipComponents, main[12], "gui.shincolle.missreduce", ChatFormatting.RED, 0)
            addPercentStat(tooltipComponents, main[15], "gui.shincolle.dodge", ChatFormatting.GOLD, 0)
            addFlatStat(tooltipComponents, main[13], "gui.shincolle.antiair", ChatFormatting.YELLOW, 1)
            addFlatStat(tooltipComponents, main[14], "gui.shincolle.antiss", ChatFormatting.AQUA, 1)

            addLabelPercent(tooltipComponents, main[16], "gui.shincolle.equip.xp", ChatFormatting.GREEN)
            addLabelPercent(tooltipComponents, main[17], "gui.shincolle.equip.grudge", ChatFormatting.LIGHT_PURPLE)
            addLabelPercent(tooltipComponents, main[18], "gui.shincolle.equip.ammo", ChatFormatting.AQUA)
            addLabelPercent(tooltipComponents, main[19], "gui.shincolle.equip.hpres", ChatFormatting.GREEN)
            addLabelPercent(tooltipComponents, main[20], "gui.shincolle.equip.kb", ChatFormatting.RED)
        }

        private fun addMiscTooltip(misc: IntArray, tooltipComponents: MutableList<Component?>) {
            if (misc[0] == 1) {
                tooltipComponents.add(
                    Component.translatable("gui.shincolle.notforcarrier").withStyle(ChatFormatting.RED)
                )
            } else if (misc[0] == 3) {
                tooltipComponents.add(
                    Component.translatable("gui.shincolle.carrieronly").withStyle(ChatFormatting.AQUA)
                )
            }

            tooltipComponents.add(
                Component.translatable(if (misc[3] > 400) "block.shincolle.large_shipyard" else "block.shincolle.small_shipyard")
                    .withStyle(ChatFormatting.GOLD)
            )

            val materialKey = when (misc[4]) {
                1 -> "item.shincolle.abyss_metal"
                2 -> "item.shincolle.ammo"
                3 -> "item.shincolle.abyss_polymetal"
                else -> "item.shincolle.grudge"
            }

            val mats = Component.translatable("gui.shincolle.equip.matstype")
                .withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(Component.literal(" (").withStyle(ChatFormatting.GRAY))
                .append(Component.translatable(materialKey).withStyle(ChatFormatting.GRAY))
                .append(Component.literal(") " + misc[3] + "  ").withStyle(ChatFormatting.GRAY))
                .append(
                    Component.translatable("gui.shincolle.equip.matsrarelevel").withStyle(ChatFormatting.LIGHT_PURPLE)
                )
                .append(Component.literal(" " + misc[2]).withStyle(ChatFormatting.GRAY))
            tooltipComponents.add(mats)
        }

        private fun addFlatStat(
            tooltipComponents: MutableList<Component?>,
            value: Float,
            key: String,
            color: ChatFormatting,
            precision: Int
        ) {
            if (value == 0.0f) {
                return
            }

            val format = if (precision <= 0) "%.0f" else (if (precision == 1) "%.1f" else "%.2f")
            tooltipComponents.add(
                Component.literal(String.format(Locale.ROOT, format, value) + " ")
                    .withStyle(color)
                    .append(Component.translatable(key).withStyle(color))
            )
        }

        private fun addPercentStat(
            tooltipComponents: MutableList<Component?>,
            value: Float,
            key: String,
            color: ChatFormatting,
            precision: Int
        ) {
            if (value == 0.0f) {
                return
            }

            val percent = value * 100.0f
            val format = if (precision <= 0) "%.0f%%" else "%.1f%%"
            tooltipComponents.add(
                Component.literal(String.format(Locale.ROOT, format, percent) + " ")
                    .withStyle(color)
                    .append(Component.translatable(key).withStyle(color))
            )
        }

        private fun addLabelPercent(
            tooltipComponents: MutableList<Component?>,
            value: Float,
            key: String,
            color: ChatFormatting
        ) {
            if (value == 0.0f) {
                return
            }

            tooltipComponents.add(
                Component.translatable(key)
                    .withStyle(color)
                    .append(Component.literal(String.format(Locale.ROOT, " %.0f%%", value * 100.0f)).withStyle(color))
            )
        }

        private fun hasLegacyEnchantData(stack: ItemStack): Boolean {
            val enchantments = stack.getOrDefault<ItemEnchantments>(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)
            if (!enchantments.isEmpty()) {
                return true
            }

            val customData = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
            if (customData == null) {
                return false
            }

            return customData.copyTag().contains("PList")
        }
    }
}
