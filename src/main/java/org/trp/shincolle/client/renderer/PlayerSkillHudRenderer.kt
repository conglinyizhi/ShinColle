package org.trp.shincolle.client.renderer

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import org.trp.shincolle.client.gui.component.Sprites
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

object PlayerSkillHudRenderer {
    private const val ICON_SIZE = 16
    private const val ICON_GAP = 2
    private const val PANEL_PADDING = 6
    private const val PANEL_HEIGHT = 28
    private const val PANEL_BOTTOM_OFFSET = 56
    private const val ICON_U_STEP = 16
    private const val ICON_V = 0

    fun render(guiGraphics: GuiGraphics) {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return
        if (mc.options.hideGui || mc.screen != null) return

        val ship = resolveSkillHostShip(player) ?: return
        val slots = buildSkillSlots(ship)
        if (slots.none { it.visible }) return

        val visibleSlots = slots.filter { it.visible }
        val panelWidth = visibleSlots.size * ICON_SIZE + (visibleSlots.size - 1) * ICON_GAP + PANEL_PADDING * 2
        val x = (mc.window.guiScaledWidth - panelWidth) / 2
        val y = mc.window.guiScaledHeight - PANEL_BOTTOM_OFFSET

        guiGraphics.fill(x, y, x + panelWidth, y + PANEL_HEIGHT, 0x90000000.toInt())

        var iconX = x + PANEL_PADDING
        for ((index, slot) in visibleSlots.withIndex()) {
            renderSlot(guiGraphics, mc, iconX, y + PANEL_PADDING, index + 1, slot)
            iconX += ICON_SIZE + ICON_GAP
        }
    }

    private fun renderSlot(
        guiGraphics: GuiGraphics,
        mc: Minecraft,
        x: Int,
        y: Int,
        keyIndex: Int,
        slot: SkillSlot
    ) {
        guiGraphics.blit(Sprites.T_HUD, x, y, slot.textureU.toFloat(), ICON_V.toFloat(), ICON_SIZE, ICON_SIZE, 256, 256)

        if (!slot.available) {
            guiGraphics.fill(x, y, x + ICON_SIZE, y + ICON_SIZE, 0xA0000000.toInt())
        }

        val keyText = keyIndex.toString()
        val textX = x + (ICON_SIZE - mc.font.width(keyText)) / 2
        guiGraphics.drawString(mc.font, keyText, textX, y + ICON_SIZE + 1, 0xFFFFFF, true)
    }

    private fun resolveSkillHostShip(player: Player): EntityShipBase? {
        val vehicle = player.vehicle
        if (vehicle is EntityMountBase) {
            return vehicle.host
        }

        for (passenger in player.passengers) {
            if (passenger is EntityShipBase && passenger.isAlive) {
                return passenger
            }
        }

        return null
    }

    private fun buildSkillSlots(ship: EntityShipBase): List<SkillSlot> {
        return listOf(
            SkillSlot(0 * ICON_U_STEP, ship.isStateGuiBtn1, ship.isStateGuiBtn1 && ship.isStateLightAttack && ship.ammoLight > 0),
            SkillSlot(1 * ICON_U_STEP, ship.isStateGuiBtn2, ship.isStateGuiBtn2 && ship.isStateHeavyAttack && ship.ammoHeavy > 0),
            SkillSlot(2 * ICON_U_STEP, ship.isStateGuiBtn3, ship.isStateGuiBtn3 && ship.isStateLightAircraftAttack && ship.hasAirLight() && ship.ammoLight >= 5),
            SkillSlot(3 * ICON_U_STEP, ship.isStateGuiBtn4, ship.isStateGuiBtn4 && ship.isStateHeavyAircraftAttack && ship.hasAirHeavy() && ship.ammoHeavy >= 5)
        )
    }

    private data class SkillSlot(
        val textureU: Int,
        val visible: Boolean,
        val available: Boolean
    )
}
