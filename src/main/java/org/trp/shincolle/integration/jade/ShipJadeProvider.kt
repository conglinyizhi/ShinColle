package org.trp.shincolle.integration.jade

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.Shincolle
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.item.DebugInspectorItem
import org.trp.shincolle.menu.ShipContainerMenu
import snownee.jade.api.EntityAccessor
import snownee.jade.api.IEntityComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.config.IPluginConfig
import snownee.jade.api.ui.IElement
import snownee.jade.api.ui.IElementHelper

enum class ShipJadeProvider : IEntityComponentProvider {
    INSTANCE;

    override fun appendTooltip(tooltip: ITooltip, accessor: EntityAccessor, config: IPluginConfig) {
        val ship = accessor.entity as? EntityShipBase ?: return
        val creativeInfinite = ship.hasCreativeDebugger()
        val helper = IElementHelper.get()

        // Line 1: Owner | Level | Status
        val ownerName = ship.getOwner()?.name?.string ?: "?"
        val ownerText = Component.translatable("gui.shincolle.owner").append(": $ownerName")
        val levelText = Component.literal("Lv.${ship.level}")
        val statusText = runningState(ship)
        tooltip.add(
            listOf(
                helper.text(ownerText),
                helper.text(Component.literal(" | ")),
                helper.text(levelText),
                helper.text(Component.literal(" | ")),
                helper.text(statusText)
            )
        )

        // Line 2: Resource icons (ammo light / ammo heavy / grudge)
        val resourceLine = mutableListOf<IElement>()

        // Light ammo
        resourceLine.add(helper.smallItem(ItemStack(ModItems.AMMO_LIGHT.get()!!)))
        resourceLine.add(
            helper.text(
                if (creativeInfinite) DebugInspectorItem.creativeInfiniteLabel()
                else Component.literal(" ${ship.ammoLight}")
            )
        )

        resourceLine.add(helper.spacer(6, 0))

        // Heavy ammo
        resourceLine.add(helper.smallItem(ItemStack(ModItems.AMMO_HEAVY.get()!!)))
        resourceLine.add(
            helper.text(
                if (creativeInfinite) DebugInspectorItem.creativeInfiniteLabel()
                else Component.literal(" ${ship.ammoHeavy}")
            )
        )

        resourceLine.add(helper.spacer(6, 0))

        // Grudge
        resourceLine.add(helper.smallItem(ItemStack(ModItems.GRUDGE.get())))
        resourceLine.add(
            helper.text(
                if (creativeInfinite) DebugInspectorItem.creativeInfiniteLabel()
                else Component.literal(" ${ship.fuel}")
            )
        )

        tooltip.add(resourceLine)
    }

    override fun getUid(): ResourceLocation = UID

    companion object {
        private val UID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "ship")

        private fun runningState(ship: EntityShipBase): Component {
            if (!ship.isAlive) {
                return Component.translatable("tooltip.shincolle.jade.ship.status.idle")
            }
            if (ship.isNoFuel) {
                return Component.translatable("tooltip.shincolle.jade.ship.status.no_fuel")
            }

            val taskId = ship.getStateMinor(ShipContainerMenu.STATE_MINOR_TASK_ID)
            if (taskId in 1..4) {
                return Component.translatable(
                    when (taskId) {
                        1 -> "gui.shincolle.ai.cooking"
                        2 -> "gui.shincolle.ai.fishing"
                        3 -> "gui.shincolle.ai.mining"
                        4 -> "gui.shincolle.ai.crafting"
                        else -> "tooltip.shincolle.jade.ship.status.idle"
                    }
                )
            }
            if (ship.hasBlockGuardTarget()) {
                return Component.translatable("tooltip.shincolle.jade.ship.status.guard")
            }
            if (ship.hasPointerTargetEntity()) {
                return Component.translatable("tooltip.shincolle.jade.ship.status.pointer_attack")
            }
            if (ship.hasPointerTarget()) {
                return Component.translatable("tooltip.shincolle.jade.ship.status.pointer_move")
            }
            if (ship.shouldFollowOwner()) {
                return Component.translatable("tooltip.shincolle.jade.ship.status.follow")
            }
            return Component.translatable(
                when (ship.explainFollowBlockReason()) {
                    "orderedToSit", "sittingPose" -> "tooltip.shincolle.jade.ship.status.standby"
                    else -> "tooltip.shincolle.jade.ship.status.idle"
                }
            )
        }
    }
}
