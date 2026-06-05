package org.trp.shincolle.client

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.attachment.AdmiralData
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.item.PointerItem
import org.trp.shincolle.item.PointerItem.Companion.getModeTranslationKey
import org.trp.shincolle.server.PlayerStateService
import java.util.*

object PointerItemClientHelper {
    fun appendHoverText(item: PointerItem, stack: ItemStack, tooltipComponents: MutableList<Component?>) {
        val mc = Minecraft.getInstance()
        if (mc.player == null) {
            return
        }

        val data = PlayerStateService.admiralData(mc.player!!)
        val teamId = data.getCurrentTeamID()
        val formationId = data.getFormationID(teamId)
        val mode = item.getMode(stack)

        val modeComp: Component = Component.translatable(getModeTranslationKey(mode))
            .withStyle(modeStyle(mode))
        if (mode == PointerItem.MODE_FORMATION) {
            val formationComp: Component = Component.translatable("gui.shincolle.formation.format" + formationId)
                .withStyle(ChatFormatting.GOLD)
            tooltipComponents.add(modeComp.copy().append(" : ").append(formationComp))
        } else {
            tooltipComponents.add(modeComp)
        }

        tooltipComponents.add(Component.translatable("gui.shincolle.pointer3").withStyle(ChatFormatting.GRAY))
        tooltipComponents.add(
            Component.translatable("gui.shincolle.pointer4")
                .append(" " + (teamId + 1))
                .withStyle(ChatFormatting.YELLOW, ChatFormatting.UNDERLINE)
        )

        var displayedCount = 1
        for (i in 0..<AdmiralData.SLOT_COUNT) {
            val uuid = data.getShipUUID(teamId, i)
            if (uuid == null) {
                continue
            }

            val shipLine = resolveShipLine(mc, data, teamId, i, displayedCount, uuid)
            tooltipComponents.add(shipLine)
            displayedCount++
        }
    }

    private fun resolveShipLine(
        mc: Minecraft,
        data: AdmiralData,
        teamId: Int,
        slotId: Int,
        displayedCount: Int,
        uuid: UUID?
    ): Component {
        if (mc.level != null) {
            for (entity in mc.level!!.entitiesForRendering()) {
                if (entity.getUUID() == uuid && entity is EntityShipBase) {
                    val name =
                        if (entity.hasCustomName()) entity.getCustomName()!!.getString() else entity.getDisplayName()!!
                            .getString()
                    val color = if (data.isSelected(teamId, slotId)) ChatFormatting.WHITE else ChatFormatting.GRAY
                    return Component.literal(displayedCount.toString() + ": " + name + " - Lv " + entity.level)
                        .withStyle(color)
                }
            }
        }

        return Component.translatable("gui.shincolle.formation.nosignal")
            .withStyle(ChatFormatting.DARK_RED)
            .append(Component.literal(" |||").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.OBFUSCATED))
    }

    private fun modeStyle(mode: Int): ChatFormatting {
        return when (mode) {
            PointerItem.MODE_GROUP -> ChatFormatting.RED
            PointerItem.MODE_FORMATION -> ChatFormatting.GOLD
            else -> ChatFormatting.AQUA
        }
    }
}
