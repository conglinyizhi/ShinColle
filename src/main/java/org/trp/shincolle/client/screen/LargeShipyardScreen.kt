package org.trp.shincolle.client.screen

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.trp.shincolle.client.gui.component.Sprites
import org.trp.shincolle.menu.LargeShipyardMenu

class LargeShipyardScreen(menu: LargeShipyardMenu, playerInventory: Inventory, title: Component) :
    AbstractContainerScreen<LargeShipyardMenu>(menu, playerInventory, title) {

    private var guiTicks = 0f

    init {
        this.imageWidth = 208
        this.imageHeight = 223
        this.inventoryLabelX = 8
        this.inventoryLabelY = this.imageHeight - 94
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick)
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        this.renderTooltip(guiGraphics, mouseX, mouseY)

        val mx = mouseX - this.leftPos
        val my = mouseY - this.topPos
        if (inside(mx, my, 8, 19, 22, 84)) {
            guiGraphics.renderTooltip(this.font, Component.literal(this.menu.powerRemained.toString()), mouseX, mouseY)
        }
        if (insideInclusive(mx, my, 151, 95, 169, 113)) {
            guiGraphics.renderComponentTooltip(
                this.font, listOf(
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
            guiGraphics.blit(TEXTURE, this.leftPos + 9, this.topPos + 83 - powerScale, 208f, (64 - powerScale).toFloat(),
                12, powerScale, 256, 256)
        }

        drawBuildIcons(guiGraphics)
        drawSelectionHighlights(guiGraphics)
    }

    override fun renderLabels(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        guiGraphics.drawString(this.font, this.title, (this.imageWidth - this.font.width(this.title)) / 2, 6, 0x404040, false)
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false)

        val time = this.menu.buildTimeString
        guiGraphics.drawString(this.font, time, 176 - this.font.width(time) / 2, 77, 0x707070, false)

        if (!this.menu.hasMaterial() && this.menu.buildType != 0) {
            val text = Component.translatable("gui.shincolle.nomaterial")
            guiGraphics.drawString(this.font, text, 105 - this.font.width(text) / 2, 99, 0xFF6666, false)
        } else if (!this.menu.hasPower()) {
            val text = Component.translatable("gui.shincolle.nofuel")
            guiGraphics.drawString(this.font, text, 105 - this.font.width(text) / 2, 99, 0xFF6666, false)
        }

        for (i in 0..3) {
            val y = 20 + i * 19
            val matBuild = this.menu.getMatBuild(i).toString()
            val matStock = this.menu.getMatStock(i).toString()

            val color = getMaterialColor(this.menu.getMatBuild(i))
            guiGraphics.drawString(this.font, matBuild, 73 - this.font.width(matBuild) / 2, y, color, false)
            guiGraphics.drawString(this.font, matStock, 125 - this.font.width(matStock) / 2, y, 0xEED15A, false)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            val x = mouseX.toInt() - this.leftPos
            val y = mouseY.toInt() - this.topPos

            if (insideInclusive(x, y, 157, 24, 175, 42)) {
                sendMenuButton(LargeShipyardMenu.BUTTON_BUILD_SHIP)
                return true
            }
            if (insideInclusive(x, y, 177, 24, 195, 42)) {
                sendMenuButton(LargeShipyardMenu.BUTTON_BUILD_EQUIP)
                return true
            }
            if (insideInclusive(x, y, 23, 93, 48, 112)) {
                sendMenuButton(LargeShipyardMenu.BUTTON_TOGGLE_INV_MODE)
                return true
            }

            for (i in 0..3) {
                val iconTop = 14 + i * 19
                if (insideInclusive(x, y, 27, iconTop, 45, iconTop + 18)) {
                    sendMenuButton(LargeShipyardMenu.BUTTON_SELECT_MAT_0_A + i)
                    return true
                }
            }

            for (i in 0..3) {
                val rowTop = 19 + i * 19
                if (insideInclusive(x, y, 51, rowTop, 97, rowTop + 8)) {
                    sendMenuButton(LargeShipyardMenu.BUTTON_SELECT_MAT_0_B + i)
                    return true
                }
            }

            val selected = this.menu.selectMat
            val addRowTop = 8 + selected * 19
            val removeRowTop = 28 + selected * 19
            for (i in 0..3) {
                val x1 = 50 + i * 12
                val x2 = x1 + 12
                if (insideInclusive(x, y, x1, addRowTop, x2, addRowTop + 10)) {
                    sendMenuButton(LargeShipyardMenu.BUTTON_MAT_AMOUNT_BASE + i)
                    return true
                }
                if (insideInclusive(x, y, x1, removeRowTop, x2, removeRowTop + 10)) {
                    sendMenuButton(LargeShipyardMenu.BUTTON_MAT_AMOUNT_BASE + 4 + i)
                    return true
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun getMaterialColor(amount: Int): Int {
        if (amount < 100) return 0xFF6666
        return if (amount == 1000) 0xEED15A else 0xFFFFFF
    }

    private fun drawBuildIcons(guiGraphics: GuiGraphics) {
        val buildType = this.menu.buildType
        if (buildType == 0) return

        val equip = buildType == 2 || buildType == 4
        val building = buildType == 3 || buildType == 4
        val x = this.leftPos + if (equip) 177 else 157
        val y = this.topPos + 24
        val v = if (building) ANIM_ICON_V_OFFSETS[this.guiTicks.toInt() % ANIM_ICON_V_OFFSETS.size] else 64

        guiGraphics.blit(
            TEXTURE, x, y,
            Sprites.SHIPYARD_LARGE_SELECTION_ICON_U.toFloat(), v.toFloat(),
            Sprites.SHIPYARD_LARGE_SELECTION_ICON_W, Sprites.SHIPYARD_LARGE_SELECTION_ICON_H,
            256, 256
        )
    }

    private fun drawSelectionHighlights(guiGraphics: GuiGraphics) {
        val selectMat = this.menu.selectMat
        guiGraphics.blit(
            TEXTURE, this.leftPos + 50, this.topPos + 8 + selectMat * 19,
            Sprites.SHIPYARD_LARGE_SELECTION_BG_U.toFloat(), Sprites.SHIPYARD_LARGE_SELECTION_BG_V.toFloat(),
            Sprites.SHIPYARD_LARGE_SELECTION_BG_W, Sprites.SHIPYARD_LARGE_SELECTION_BG_H,
            256, 256
        )
        guiGraphics.blit(
            TEXTURE, this.leftPos + 27, this.topPos + 14 + selectMat * 19,
            Sprites.SHIPYARD_LARGE_SELECTION_ICON_U.toFloat(), Sprites.SHIPYARD_LARGE_SELECTION_ICON_V.toFloat(),
            Sprites.SHIPYARD_LARGE_SELECTION_ICON_W, Sprites.SHIPYARD_LARGE_SELECTION_ICON_H,
            256, 256
        )

        if (this.menu.invMode == 1) {
            guiGraphics.blit(
                TEXTURE, this.leftPos + 23, this.topPos + 92,
                Sprites.SHIPYARD_LARGE_INV_MODE_ICON_U.toFloat(), Sprites.SHIPYARD_LARGE_INV_MODE_ICON_V.toFloat(),
                Sprites.SHIPYARD_LARGE_INV_MODE_ICON_W, Sprites.SHIPYARD_LARGE_INV_MODE_ICON_H,
                256, 256
            )
        }
    }

    private fun sendMenuButton(id: Int) {
        if (Minecraft.getInstance().gameMode != null) {
            Minecraft.getInstance().gameMode!!.handleInventoryButtonClick(this.menu.containerId, id)
        }
    }

    companion object {
        private val TEXTURE: ResourceLocation = Sprites.T_LARGE_SHIPYARD
        private val ANIM_ICON_V_OFFSETS = intArrayOf(103, 121, 139, 157, 175, 193)

        private fun inside(x: Int, y: Int, x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
            return x >= x1 && x < x2 && y >= y1 && y < y2
        }

        private fun insideInclusive(x: Int, y: Int, x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
            return x >= x1 && x <= x2 && y >= y1 && y <= y2
        }
    }
}