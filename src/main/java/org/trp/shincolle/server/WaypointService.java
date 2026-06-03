package org.trp.shincolle.server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.trp.shincolle.Config;
import org.trp.shincolle.block.entity.CraneBlockEntity;
import org.trp.shincolle.block.entity.IWaypoint;

public final class WaypointService {
    private WaypointService() {
    }

    public static void handleAction(Player player, int action, BlockPos pos1, BlockPos pos2) {
        if (player == null) {
            return;
        }
        if (player.level() == null) {
            return;
        }
        if (pos1 == null || pos2 == null) {
            return;
        }

        if (action == 0) {
            pairWaypointToWaypoint(player, pos1, pos2);
        } else if (action == 1) {
            pairWaypointToContainer(player, pos1, pos2);
        } else if (action == 2) {
            autoPair(player, pos1, pos2);
        }
    }

    private static void autoPair(Player player, BlockPos pos1, BlockPos pos2) {
        BlockEntity be1 = player.level().getBlockEntity(pos1);
        BlockEntity be2 = player.level().getBlockEntity(pos2);

        if (be1 instanceof IWaypoint && be2 instanceof IWaypoint) {
            pairWaypointToWaypoint(player, pos1, pos2);
        } else if (be1 instanceof IWaypoint && isWaypointContainer(be2)) {
            pairWaypointToContainer(player, pos1, pos2);
        } else if (be2 instanceof IWaypoint && isWaypointContainer(be1)) {
            pairWaypointToContainer(player, pos2, pos1);
        } else {
            player.displayClientMessage(Component.translatable("chat.shincolle.wrench.wrongtile"), false);
        }
    }

    private static void pairWaypointToWaypoint(Player player, BlockPos from, BlockPos to) {
        if (from.distSqr(to) > (double) Config.pairDistWaypoint * Config.pairDistWaypoint) {
            player.displayClientMessage(Component.translatable("chat.shincolle.wrench.wptoofar"), false);
            return;
        }

        if (player.level().getBlockEntity(from) instanceof IWaypoint wpFrom
                && player.level().getBlockEntity(to) instanceof IWaypoint wpTo
                && checkWaypointOwner(player, wpFrom)) {
            wpFrom.setNextPos(to);
            if (!wpTo.getNextPos().equals(from)) {
                wpTo.setLastPos(from);
            }
            player.displayClientMessage(Component.translatable("chat.shincolle.wrench.setwp")
                    .append(" " + from.getX() + " " + from.getY() + " " + from.getZ()
                            + " --> " + to.getX() + " " + to.getY() + " " + to.getZ()), false);
        }
    }

    private static void pairWaypointToContainer(Player player, BlockPos waypointPos, BlockPos containerPos) {
        if (waypointPos.distSqr(containerPos) > (double) Config.pairDistChest * Config.pairDistChest) {
            player.displayClientMessage(Component.translatable("chat.shincolle.wrench.toofar"), false);
            return;
        }

        BlockEntity container = player.level().getBlockEntity(containerPos);
        if (player.level().getBlockEntity(waypointPos) instanceof IWaypoint waypoint
                && isWaypointContainer(container)
                && checkWaypointOwner(player, waypoint)) {
            waypoint.setChestPos(containerPos);
            player.displayClientMessage(Component.translatable("chat.shincolle.wrench.setwp")
                    .append(" " + waypointPos.getX() + " " + waypointPos.getY() + " " + waypointPos.getZ()
                            + " & " + containerPos.getX() + " " + containerPos.getY() + " " + containerPos.getZ()), false);
        }
    }

    private static boolean checkWaypointOwner(Player player, IWaypoint waypoint) {
        if (waypoint.getOwnerUUID() != null && !waypoint.getOwnerUUID().equals(player.getUUID())) {
            player.displayClientMessage(Component.translatable("chat.shincolle.wrongowner"), false);
            return false;
        }
        return true;
    }

    private static boolean isWaypointContainer(BlockEntity blockEntity) {
        return blockEntity instanceof BaseContainerBlockEntity || blockEntity instanceof CraneBlockEntity;
    }
}
