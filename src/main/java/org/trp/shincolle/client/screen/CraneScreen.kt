package org.trp.shincolle.client.screen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.trp.shincolle.client.gui.component.Sprites
import org.trp.shincolle.menu.CraneMenu

class CraneScreen(menu: CraneMenu, playerInventory: Inventory, title: Component) :
    AbstractContainerScreen<CraneMenu>(menu, playerInventory, title) {

    init {
        this.imageWidth = 176
        this.imageHeight = 201
        this.inventoryLabelX = 8
        this.inventoryLabelY = this.imageHeight - 94
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick)
        super.render(graphics, mouseX, mouseY, partialTick)
        this.renderTooltip(graphics, mouseX, mouseY)
    }

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val x = this.leftPos
        val y = this.topPos

        graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f)
        graphics.blit(TEXTURE, x, y, 0f, 0f, this.imageWidth, this.imageHeight, 256, 256)

        if (menu.isActive) {
            graphics.blit(TEXTURE, x + 7, y + 6, Sprites.CRANE_BTN_ACTIVE_U.toFloat(), Sprites.CRANE_BTN_ACTIVE_V.toFloat(), Sprites.CRANE_BTN_ACTIVE_W, Sprites.CRANE_BTN_ACTIVE_H, 256, 256)
        }

        if (menu.isCheckMetadata) graphics.blit(TEXTURE, x + 23, y + 22, Sprites.CRANE_CHK_METADATA_U.toFloat(), Sprites.CRANE_CHK_METADATA_V.toFloat(), Sprites.CRANE_CHK_METADATA_W, Sprites.CRANE_CHK_METADATA_H, 256, 256)
        if (menu.isCheckOredict) graphics.blit(TEXTURE, x + 37, y + 22, Sprites.CRANE_CHK_OREDICT_U.toFloat(), Sprites.CRANE_CHK_OREDICT_V.toFloat(), Sprites.CRANE_CHK_OREDICT_W, Sprites.CRANE_CHK_OREDICT_H, 256, 256)
        if (menu.isCheckNbt) graphics.blit(TEXTURE, x + 51, y + 22, Sprites.CRANE_CHK_NBT_U.toFloat(), Sprites.CRANE_CHK_NBT_V.toFloat(), Sprites.CRANE_CHK_NBT_W, Sprites.CRANE_CHK_NBT_H, 256, 256)

        val redMode = menu.getModeRedstone()
        if (redMode == 1) graphics.blit(TEXTURE, x + 65, y + 22, Sprites.CRANE_RED_MODE1_U.toFloat(), Sprites.CRANE_RED_MODE1_V.toFloat(), Sprites.CRANE_RED_MODE1_W, Sprites.CRANE_RED_MODE1_H, 256, 256)
        else if (redMode == 2) graphics.blit(TEXTURE, x + 65, y + 22, Sprites.CRANE_RED_MODE2_U.toFloat(), Sprites.CRANE_RED_MODE2_V.toFloat(), Sprites.CRANE_RED_MODE2_W, Sprites.CRANE_RED_MODE2_H, 256, 256)

        if (!menu.isEnabLoad) {
            graphics.blit(TEXTURE, x + 7, y + 52, Sprites.CRANE_BTN_DISABLED_U.toFloat(), Sprites.CRANE_BTN_DISABLED_V.toFloat(), Sprites.CRANE_BTN_DISABLED_W, Sprites.CRANE_BTN_DISABLED_H, 256, 256)
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            graphics.blit(TEXTURE, x + 8, y + 65, Sprites.CRANE_DISABLED_OVERLAY_U.toFloat(), Sprites.CRANE_DISABLED_OVERLAY_V.toFloat(), Sprites.CRANE_DISABLED_OVERLAY_W, Sprites.CRANE_DISABLED_OVERLAY_H, 256, 256)
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        }
        if (!menu.isEnabUnload) {
            graphics.blit(TEXTURE, x + 7, y + 83, Sprites.CRANE_BTN_DISABLED_U.toFloat(), Sprites.CRANE_BTN_DISABLED_V.toFloat(), Sprites.CRANE_BTN_DISABLED_W, Sprites.CRANE_BTN_DISABLED_H, 256, 256)
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            graphics.blit(TEXTURE, x + 8, y + 96, Sprites.CRANE_DISABLED_OVERLAY_U.toFloat(), Sprites.CRANE_DISABLED_OVERLAY_V.toFloat(), Sprites.CRANE_DISABLED_OVERLAY_W, Sprites.CRANE_DISABLED_OVERLAY_H, 256, 256)
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        }

        val liqMode = menu.getModeLiquid()
        if (liqMode == 0) graphics.blit(TEXTURE, x + 23, y + 36, Sprites.CRANE_LIQ_MODE0_U.toFloat(), Sprites.CRANE_LIQ_MODE0_V.toFloat(), Sprites.CRANE_LIQ_MODE0_W, Sprites.CRANE_LIQ_MODE0_H, 256, 256)
        else if (liqMode == 1) graphics.blit(TEXTURE, x + 23, y + 36, Sprites.CRANE_LIQ_MODE1_U.toFloat(), Sprites.CRANE_LIQ_MODE1_V.toFloat(), Sprites.CRANE_LIQ_MODE1_W, Sprites.CRANE_LIQ_MODE1_H, 256, 256)
        else if (liqMode == 2) graphics.blit(TEXTURE, x + 23, y + 36, Sprites.CRANE_LIQ_MODE2_U.toFloat(), Sprites.CRANE_LIQ_MODE2_V.toFloat(), Sprites.CRANE_LIQ_MODE2_W, Sprites.CRANE_LIQ_MODE2_H, 256, 256)

        val energyMode = menu.getModeEnergy()
        if (energyMode == 0) graphics.blit(TEXTURE, x + 39, y + 36, Sprites.CRANE_ENERGY_MODE0_U.toFloat(), Sprites.CRANE_ENERGY_MODE0_V.toFloat(), Sprites.CRANE_ENERGY_MODE0_W, Sprites.CRANE_ENERGY_MODE0_H, 256, 256)
        else if (energyMode == 1) graphics.blit(TEXTURE, x + 39, y + 36, Sprites.CRANE_ENERGY_MODE1_U.toFloat(), Sprites.CRANE_ENERGY_MODE1_V.toFloat(), Sprites.CRANE_ENERGY_MODE1_W, Sprites.CRANE_ENERGY_MODE1_H, 256, 256)
        else if (energyMode == 2) graphics.blit(TEXTURE, x + 39, y + 36, Sprites.CRANE_ENERGY_MODE2_U.toFloat(), Sprites.CRANE_ENERGY_MODE2_V.toFloat(), Sprites.CRANE_ENERGY_MODE2_W, Sprites.CRANE_ENERGY_MODE2_H, 256, 256)

        for (i in 0 until 18) {
            val stack = menu.getSlot(i).item
            if (!stack.isEmpty) {
                val slotMode = menu.getItemMode(i)
                val sx = x + 7 + (i % 9) * 18
                val sy = y + if (i < 9) 64 else 95
                if (slotMode) {
                    graphics.blit(TEXTURE, sx, sy, Sprites.CRANE_SLOT_ON_U.toFloat(), Sprites.CRANE_SLOT_ON_V.toFloat(), Sprites.CRANE_SLOT_ON_W, Sprites.CRANE_SLOT_ON_H, 256, 256)
                } else {
                    graphics.blit(TEXTURE, sx, sy, Sprites.CRANE_SLOT_OFF_U.toFloat(), Sprites.CRANE_SLOT_OFF_V.toFloat(), Sprites.CRANE_SLOT_OFF_W, Sprites.CRANE_SLOT_OFF_H, 256, 256)
                }
            }
        }
    }

    override fun renderLabels(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        graphics.drawString(this.font, this.title, (this.imageWidth - this.font.width(this.title)) / 2, 6, 0x404040, false)
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false)

        val modeStr = getModeString(menu.getCraneMode())
        val len = this.font.width(modeStr) / 2
        graphics.drawString(this.font, modeStr, 57 - len, 9, 0xFFFF00, true)

        graphics.drawString(this.font, Component.translatable("gui.shincolle.crane.toship"), 21, 54, 0xFF6666, false)
        graphics.drawString(this.font, Component.translatable("gui.shincolle.crane.tochest"), 21, 85, 0x404040, false)

        val ship = menu.shipEntity
        if (ship != null) {
            val timeStr = formatTime(menu.getShipTimer())
            val timeLen = this.font.width(timeStr) / 2
            graphics.drawString(this.font, timeStr, 133 - timeLen, 10, 0x404040, false)

            val name = if (ship.hasCustomName())
                ship.customName!!
            else
                Component.translatable(ship.type.descriptionId)
            graphics.drawString(this.font, name, 80, 24, 0xFFFFFF, true)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val xClick = (mouseX - leftPos).toInt()
        val yClick = (mouseY - topPos).toInt()

        if (inside(xClick, yClick, 7, 6, 20, 19)) { sendButton(0); return true }
        if (inside(xClick, yClick, 22, 5, 91, 20)) { sendButton(if (button == 1) 11 else 1); return true }
        if (inside(xClick, yClick, 23, 22, 34, 33)) { sendButton(2); return true }
        if (inside(xClick, yClick, 37, 22, 48, 33)) { sendButton(3); return true }
        if (inside(xClick, yClick, 51, 22, 62, 33)) { sendButton(6); return true }
        if (inside(xClick, yClick, 65, 22, 76, 33)) { sendButton(7); return true }
        if (inside(xClick, yClick, 7, 52, 18, 63)) { sendButton(4); return true }
        if (inside(xClick, yClick, 7, 83, 18, 94)) { sendButton(5); return true }
        if (inside(xClick, yClick, 23, 36, 36, 49)) { sendButton(8); return true }
        if (inside(xClick, yClick, 39, 36, 52, 49)) { sendButton(9); return true }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun renderTooltip(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        super.renderTooltip(graphics, mouseX, mouseY)
        val mx = mouseX - leftPos
        val my = mouseY - topPos

        val tooltip = mutableListOf<Component>()
        if (my > 21 && my < 34) {
            if (mx > 22 && mx < 35) tooltip.add(Component.translatable("gui.shincolle.crane.usemeta"))
            else if (mx > 36 && mx < 49) tooltip.add(Component.translatable("gui.shincolle.crane.useoredict"))
            else if (mx > 50 && mx < 63) tooltip.add(Component.translatable("gui.shincolle.crane.usenbt"))
            else if (mx > 64 && mx < 77) {
                val r = menu.getModeRedstone()
                tooltip.add(Component.translatable("gui.shincolle.crane.red$r"))
            }
        } else if (my > 35 && my < 50) {
            if (mx > 22 && mx < 37) {
                val l = menu.getModeLiquid()
                tooltip.add(Component.translatable("gui.shincolle.crane.liquid$l"))
            } else if (mx > 38 && mx < 53) {
                val e = menu.getModeEnergy()
                tooltip.add(Component.translatable("gui.shincolle.crane.energy$e"))
            }
        }

        if (mx > 22 && mx < 91 && my > 5 && my < 20) {
            val m = menu.getCraneMode()
            when (m) {
                0 -> tooltip.add(Component.translatable("gui.shincolle.crane.nowait1"))
                1 -> {
                    tooltip.add(Component.translatable("gui.shincolle.crane.untilfull1"))
                    tooltip.add(Component.translatable("gui.shincolle.crane.untilfull2"))
                }
                2 -> {
                    tooltip.add(Component.translatable("gui.shincolle.crane.untilempty1"))
                    tooltip.add(Component.translatable("gui.shincolle.crane.untilempty2"))
                }
                3 -> {
                    tooltip.add(Component.translatable("gui.shincolle.crane.excess1"))
                    tooltip.add(Component.translatable("gui.shincolle.crane.excess2"))
                }
                4 -> {
                    tooltip.add(Component.translatable("gui.shincolle.crane.remain1"))
                    tooltip.add(Component.translatable("gui.shincolle.crane.remain2"))
                }
            }
        }

        if (tooltip.isNotEmpty()) {
            graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY)
        }
    }

    private fun sendButton(id: Int) {
        if (Minecraft.getInstance().gameMode != null) {
            Minecraft.getInstance().gameMode!!.handleInventoryButtonClick(menu.containerId, id)
        }
    }

    companion object {
        private val TEXTURE: ResourceLocation = Sprites.T_CRANE

        private fun inside(x: Int, y: Int, x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
            return x >= x1 && x < x2 && y >= y1 && y < y2
        }

        private fun formatTime(ticks: Int): String {
            val sec = (ticks * 0.05f).toInt()
            val hours = sec / 3600
            val minutes = (sec % 3600) / 60
            val seconds = sec % 60
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

        private fun getModeString(mode: Int): String {
            return when (mode) {
                0 -> Component.translatable("gui.shincolle.crane.nowait").string
                1 -> Component.translatable("gui.shincolle.crane.untilfull").string
                2 -> Component.translatable("gui.shincolle.crane.untilempty").string
                3 -> Component.translatable("gui.shincolle.crane.excess").string
                4 -> Component.translatable("gui.shincolle.crane.remain").string
                else -> {
                    val time = if (mode < 10) (mode - 4) * 0.5f
                    else if (mode < 15) (mode - 9) * 10f
                    else (mode - 14).toFloat()
                    if (mode < 15) String.format("%.1f s", time)
                    else String.format("%.0f min", time)
                }
            }
        }
    }
}