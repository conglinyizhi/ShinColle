package org.trp.shincolle.client.tooltip

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.network.chat.Component
import org.trp.shincolle.item.ScaledTextTooltipData

class ScaledTextClientTooltip(data: ScaledTextTooltipData) : ClientTooltipComponent {
    private val lines: MutableList<Component?>?
    private val scale: Float

    init {
        this.lines = data.lines
        this.scale = data.scale
    }

    override fun getHeight(): Int {
        return (this.lines!!.size * 10 * this.scale).toInt()
    }

    override fun getWidth(font: Font): Int {
        var maxWidth = 0
        for (line in this.lines!!) {
            val width = font.width(line)
            if (width > maxWidth) {
                maxWidth = width
            }
        }
        return (maxWidth * this.scale).toInt()
    }

    override fun renderImage(font: Font, x: Int, y: Int, guiGraphics: GuiGraphics) {
        guiGraphics.pose().pushPose()

        guiGraphics.pose().translate(x.toFloat(), y.toFloat(), 0f)
        guiGraphics.pose().scale(this.scale, this.scale, 1.0f)

        var currentY = 0
        for (line in this.lines!!) {
            guiGraphics.drawString(font, line, 0, currentY, -1, true)
            currentY += 10
        }

        guiGraphics.pose().popPose()
    }
}
