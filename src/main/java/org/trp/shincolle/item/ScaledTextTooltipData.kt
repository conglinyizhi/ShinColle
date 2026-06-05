package org.trp.shincolle.item

import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.tooltip.TooltipComponent

@JvmRecord
data class ScaledTextTooltipData(@JvmField val lines: MutableList<Component?>?, @JvmField val scale: Float) : TooltipComponent
