package org.trp.shincolle.client.screen

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.trp.shincolle.client.gui.component.Sprites
import org.trp.shincolle.menu.SmallShipyardMenu
import java.util.List

class SmallShipyardScreen(menu: SmallShipyardMenu, playerInventory: Inventory, title: Component) :
    AbstractContainerScreen<SmallShipyardMenu?>(menu, playerInventory, title) {
    private var guiTicks = 0f

    init {
        this.imageWidth = 176
        this.imageHeight = 164
        this.inventoryLabelX = 8
        this.inventoryLabelY = this.imageHeight - 94
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick)
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        this.renderTooltip(guiGraphics, mouseX, mouseY)

        if (inside(mouseX - this.leftPos, mouseY - this.topPos, 9, 17, 23, 49)) {
            guiGraphics.renderTooltip(
                this.font,
                Component.literal(this.menu.powerRemained.toString()),
                mouseX,
                mouseY
            )
        }
        if (inside(mouseX - this.leftPos, mouseY - this.topPos, 8, 53, 26, 71)) {
            guiGraphics.renderComponentTooltip(
                this.font, List.of<Component?>(
                    Component.translatable("gui.shincolle.shipyard.fuel_slot").withStyle(ChatFormatting.GOLD),
                    Component.translatable("gui.shincolle.shipyard.instant_tip").withStyle(ChatFormatting.GRAY)
                ), mouseX, mouseY
            )
        }

        this.guiTicks += 0.125f
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0f, 0f, this.imageWidth, this.imageHeight, 256, 256)

        val powerScale = this.menu.powerScale
        if (powerScale > 0) {
            guiGraphics.blit(
                TEXTURE, this.leftPos + 10, this.topPos + 48 - powerScale, 176f, (47 - powerScale).toFloat(),
                12, powerScale, 256, 256
            )
        }

        drawBuildTypeIndicator(guiGraphics)
    }

    override fun renderLabels(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        guiGraphics.drawString(
            this.font,
            this.title,
            this.imageWidth / 2 - this.font.width(this.title) / 2,
            6,
            0x404040,
            false
        )
        guiGraphics.drawString(
            this.font,
            this.playerInventoryTitle,
            this.inventoryLabelX,
            this.inventoryLabelY,
            0x404040,
            false
        )

        val time = this.menu.buildTimeString
        guiGraphics.drawString(this.font, time, 71 - this.font.width(time) / 2, 51, 0x404040, false)

        if (!this.menu.hasMaterial()) {
            val text: Component = Component.translatable("gui.shincolle.nomaterial")
            guiGraphics.drawString(this.font, text, 80 - this.font.width(text) / 2, 67, 0xFF3333, false)
        } else if (!this.menu.hasPower()) {
            val text: Component = Component.translatable("gui.shincolle.nofuel")
            guiGraphics.drawString(this.font, text, 80 - this.font.width(text) / 2, 67, 0xFF3333, false)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            val x = mouseX.toInt() - this.leftPos
            val y = mouseY.toInt() - this.topPos
            if (inside(x, y, 123, 17, 141, 35)) {
                sendMenuButton(SmallShipyardMenu.BUTTON_SHIP)
                return true
            }
            if (inside(x, y, 143, 17, 161, 35)) {
                sendMenuButton(SmallShipyardMenu.BUTTON_EQUIP)
                return true
            }
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun drawBuildTypeIndicator(guiGraphics: GuiGraphics) {
        val buildType = this.menu.buildType
        if (buildType == 0) {
            return
        }

        val u = Sprites.SHIPYARD_SMALL_BUILD_ICON_U
        val x = if (buildType == 1 || buildType == 3) 123 else 143
        val animating = buildType == 3 || buildType == 4
        val v = if (animating) 65 + (this.guiTicks.toInt() % 6) * 18 else 47
        guiGraphics.blit(
            TEXTURE,
            this.leftPos + x,
            this.topPos + 17,
            u.toFloat(),
            v.toFloat(),
            Sprites.SHIPYARD_SMALL_BUILD_ICON_W,
            Sprites.SHIPYARD_SMALL_BUILD_ICON_H,
            256,
            256
        )
    }

    private fun sendMenuButton(id: Int) {
        if (Minecraft.getInstance().gameMode != null) {
            Minecraft.getInstance().gameMode!!.handleInventoryButtonClick(this.menu.containerId, id)
        }
    }

    companion object {
        private val TEXTURE: ResourceLocation = Sprites.T_SMALL_SHIPYARD

        private fun inside(x: Int, y: Int, x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
            return x >= x1 && x < x2 && y >= y1 && y < y2
        }
    }
}
