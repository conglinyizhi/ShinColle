package org.trp.shincolle.server

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.entity.BlockEntity
import org.trp.shincolle.Config
import org.trp.shincolle.block.entity.CraneBlockEntity
import org.trp.shincolle.block.entity.IWaypoint

object WaypointService {
    @JvmStatic
    fun handleAction(player: Player?, action: Int, pos1: BlockPos?, pos2: BlockPos?) {
        if (player == null) {
            return
        }
        if (player.level() == null) {
            return
        }
        if (pos1 == null || pos2 == null) {
            return
        }

        if (action == 0) {
            pairWaypointToWaypoint(player, pos1, pos2)
        } else if (action == 1) {
            pairWaypointToContainer(player, pos1, pos2)
        } else if (action == 2) {
            autoPair(player, pos1, pos2)
        }
    }

    private fun autoPair(player: Player, pos1: BlockPos, pos2: BlockPos) {
        val be1 = player.level().getBlockEntity(pos1)
        val be2 = player.level().getBlockEntity(pos2)

        if (be1 is IWaypoint && be2 is IWaypoint) {
            pairWaypointToWaypoint(player, pos1, pos2)
        } else if (be1 is IWaypoint && isWaypointContainer(be2)) {
            pairWaypointToContainer(player, pos1, pos2)
        } else if (be2 is IWaypoint && isWaypointContainer(be1)) {
            pairWaypointToContainer(player, pos2, pos1)
        } else {
            player.displayClientMessage(Component.translatable("chat.shincolle.wrench.wrongtile"), false)
        }
    }

    private fun pairWaypointToWaypoint(player: Player, from: BlockPos, to: BlockPos) {
        if (from.distSqr(to) > Config.pairDistWaypoint.toDouble() * Config.pairDistWaypoint) {
            player.displayClientMessage(Component.translatable("chat.shincolle.wrench.wptoofar"), false)
            return
        }

        val wpFrom = player.level().getBlockEntity(from)
        val wpTo = player.level().getBlockEntity(to)
        if (wpFrom is IWaypoint && wpTo is IWaypoint && checkWaypointOwner(player, wpFrom)) {
            wpFrom.nextPos = to
            if (wpTo.nextPos != from) {
                wpTo.lastPos = from
            }
            player.displayClientMessage(
                Component.translatable("chat.shincolle.wrench.setwp")
                    .append(
                        (" " + from.x + " " + from.y + " " + from.z
                                + " --> " + to.x + " " + to.y + " " + to.z)
                    ), false
            )
        }
    }

    private fun pairWaypointToContainer(player: Player, waypointPos: BlockPos, containerPos: BlockPos) {
        if (waypointPos.distSqr(containerPos) > Config.pairDistChest.toDouble() * Config.pairDistChest) {
            player.displayClientMessage(Component.translatable("chat.shincolle.wrench.toofar"), false)
            return
        }

        val container = player.level().getBlockEntity(containerPos)
        val waypoint = player.level().getBlockEntity(waypointPos)
        if (waypoint is IWaypoint && isWaypointContainer(container) && checkWaypointOwner(player, waypoint)) {
            waypoint.chestPos = containerPos
            player.displayClientMessage(
                Component.translatable("chat.shincolle.wrench.setwp")
                    .append(
                        (" " + waypointPos.x + " " + waypointPos.y + " " + waypointPos.z
                                + " & " + containerPos.x + " " + containerPos.y + " " + containerPos.z)
                    ), false
            )
        }
    }

    private fun checkWaypointOwner(player: Player, waypoint: IWaypoint): Boolean {
        if (waypoint.ownerUUID != null && waypoint.ownerUUID != player.uuid) {
            player.displayClientMessage(Component.translatable("chat.shincolle.wrongowner"), false)
            return false
        }
        return true
    }

    private fun isWaypointContainer(blockEntity: BlockEntity?): Boolean {
        return blockEntity is BaseContainerBlockEntity || blockEntity is CraneBlockEntity
    }
}
