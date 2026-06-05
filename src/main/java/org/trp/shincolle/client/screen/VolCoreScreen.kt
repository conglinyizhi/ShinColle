package org.trp.shincolle.client.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.trp.shincolle.client.gui.component.Sprites
import org.trp.shincolle.menu.VolCoreMenu

class VolCoreScreen(menu: VolCoreMenu, inventory: Inventory, title: Component) :
    AbstractContainerScreen<VolCoreMenu?>(menu, inventory, title) {
    init {
        this.imageWidth = 176
        this.imageHeight = 166
        this.inventoryLabelX = 8
        this.inventoryLabelY = this.imageHeight - 94
    }

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        graphics.blit(GUI_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight)

        if (menu.isBtnActive) {
            graphics.blit(
                GUI_TEXTURE,
                leftPos + 7,
                topPos + 6,
                Sprites.VOLCORE_BTN_ACTIVE_U,
                Sprites.VOLCORE_BTN_ACTIVE_V,
                Sprites.VOLCORE_BTN_W,
                Sprites.VOLCORE_BTN_H
            )
        }

        val power = menu.remainedPower
        if (power > 0) {
            val scaleBar = (power * 31.0 / 9600.0).toInt()
            graphics.blit(GUI_TEXTURE, leftPos + 38, topPos + 59 - scaleBar, 0, 197 - scaleBar, 12, scaleBar)
        }
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick)
        super.render(graphics, mouseX, mouseY, partialTick)
        this.renderTooltip(graphics, mouseX, mouseY)
    }

    override fun renderLabels(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        graphics.drawString(
            this.font,
            this.title,
            this.imageWidth / 2 - this.font.width(this.title) / 2,
            6,
            0x404040,
            false
        )
        graphics.drawString(
            this.font,
            this.playerInventoryTitle,
            this.inventoryLabelX,
            this.inventoryLabelY,
            0x404040,
            false
        )
    }

    override fun renderTooltip(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        super.renderTooltip(graphics, mouseX, mouseY)

        if (isHovering(36, 27, 16, 34, mouseX.toDouble(), mouseY.toDouble())) {
            val tooltip: MutableList<Component?> = ArrayList<Component?>()
            tooltip.add(Component.literal(menu.remainedPower.toString()))
            graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val xClick = (mouseX - leftPos).toInt()
        val yClick = (mouseY - topPos).toInt()

        if (xClick >= 7 && xClick <= 20 && yClick >= 6 && yClick <= 19) {
            if (this.minecraft != null && this.minecraft!!.gameMode != null) {
                this.minecraft!!.gameMode!!.handleInventoryButtonClick(menu.containerId, 0)
                return true
            }
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    companion object {
        private val GUI_TEXTURE: ResourceLocation = Sprites.T_VOLCORE
    }
}
