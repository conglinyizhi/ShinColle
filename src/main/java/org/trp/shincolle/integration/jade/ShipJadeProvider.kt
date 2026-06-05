package org.trp.shincolle.integration.jade

import net.minecraft.network.chat.Component
import snownee.jade.api.EntityAccessor

enum class ShipJadeProvider : IEntityComponentProvider {
    INSTANCE;

    public override fun appendTooltip(tooltip: ITooltip, accessor: EntityAccessor, config: IPluginConfig?) {
        if (accessor.getEntity() !is EntityShipBase) {
            return
        }
        val creativeInfinite: Boolean = ship.hasCreativeDebugger()

        tooltip.add(
            Component.translatable("gui.shincolle.level")
                .append(": " + ship.getLevel())
        )
        tooltip.add(
            Component.translatable("gui.shincolle.hp")
                .append(": " + Math.round(ship.getHealth()) + " / " + Math.round(ship.getMaxHealth()))
                .append(
                    if (creativeInfinite) Component.literal(" ")
                        .append(DebugInspectorItem.creativeInfiniteLabel()) else Component.empty()
                )
        )
        tooltip.add(
            Component.translatable("gui.shincolle.ammolight")
                .append(": ")
                .append(
                    if (creativeInfinite) DebugInspectorItem.creativeInfiniteLabel() else Component.literal(
                        ship.ammoLight.toString()
                    )
                )
        )
        tooltip.add(
            Component.translatable("gui.shincolle.ammoheavy")
                .append(": ")
                .append(
                    if (creativeInfinite) DebugInspectorItem.creativeInfiniteLabel() else Component.literal(
                        ship.ammoHeavy.toString()
                    )
                )
        )
        tooltip.add(Component.translatable("tooltip.shincolle.jade.ship.status", runningState(ship)))
        tooltip.add(
            Component.translatable("gui.shincolle.grudge")
                .append(": ")
                .append(
                    if (creativeInfinite) DebugInspectorItem.creativeInfiniteLabel() else Component.literal(
                        ship.fuel.toString()
                    )
                )
        )
    }

    val uid: ResourceLocation
        get() = UID

    companion object {
        private val UID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "ship")

        private fun runningState(ship: EntityShipBase): Component {
            if (!ship.isAlive) {
                return Component.translatable("tooltip.shincolle.jade.ship.status.idle")
            }
            if (ship.isNoFuel()) {
                return Component.translatable("tooltip.shincolle.jade.ship.status.no_fuel")
            }

            val taskId: Int = ship.getStateMinor(ShipContainerMenu.STATE_MINOR_TASK_ID)
            if (taskId >= 1 && taskId <= 4) {
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
