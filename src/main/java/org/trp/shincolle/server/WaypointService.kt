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

        if (player.level().getBlockEntity(from) is IWaypoint
            && player.level().getBlockEntity(to) is IWaypoint
            && checkWaypointOwner(player, wpFrom)
        ) {
            wpFrom.setNextPos(to)
            if (wpTo.getNextPos() != from) {
                wpTo.setLastPos(from)
            }
            player.displayClientMessage(
                Component.translatable("chat.shincolle.wrench.setwp")
                    .append(
                        (" " + from.getX() + " " + from.getY() + " " + from.getZ()
                                + " --> " + to.getX() + " " + to.getY() + " " + to.getZ())
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
        if (player.level().getBlockEntity(waypointPos) is IWaypoint
            && isWaypointContainer(container)
            && checkWaypointOwner(player, waypoint)
        ) {
            waypoint.setChestPos(containerPos)
            player.displayClientMessage(
                Component.translatable("chat.shincolle.wrench.setwp")
                    .append(
                        (" " + waypointPos.getX() + " " + waypointPos.getY() + " " + waypointPos.getZ()
                                + " & " + containerPos.getX() + " " + containerPos.getY() + " " + containerPos.getZ())
                    ), false
            )
        }
    }

    private fun checkWaypointOwner(player: Player, waypoint: IWaypoint): Boolean {
        if (waypoint.ownerUUID != null && waypoint.ownerUUID != player.getUUID()) {
            player.displayClientMessage(Component.translatable("chat.shincolle.wrongowner"), false)
            return false
        }
        return true
    }

    private fun isWaypointContainer(blockEntity: BlockEntity?): Boolean {
        return blockEntity is BaseContainerBlockEntity || blockEntity is CraneBlockEntity
    }
}
