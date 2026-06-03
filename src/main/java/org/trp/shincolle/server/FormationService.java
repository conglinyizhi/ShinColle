package org.trp.shincolle.server;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.trp.shincolle.attachment.AdmiralData;
import org.trp.shincolle.entity.base.EntityShipBase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class FormationService {
    private static final double NEARBY_SHIP_SYNC_RADIUS = 100.0D;
    private static final double NEARBY_SELECTED_IMPORT_RADIUS = 64.0D;

    private FormationService() {
    }

    public static void clearFormationState(EntityShipBase ship) {
        if (ship == null) {
            return;
        }
        ship.setFormationTeam(-1);
        ship.setFormationSlot(-1);
        ship.setPointerSelected(false);
        ship.clearPointerTarget();
        ship.clearPointerTargetEntity();
    }

    public static void applyFormationState(EntityShipBase ship, int teamId, int slotId, boolean selected) {
        if (ship == null) {
            return;
        }
        ship.setFormationTeam(teamId);
        ship.setFormationSlot(slotId);
        ship.setPointerSelected(selected);
    }

    public static void syncNearbyShipsForCurrentTeam(Player player, boolean clearDeselectedTargets) {
        if (player == null) {
            return;
        }
        AdmiralData data = PlayerStateService.admiralData(player);
        int teamId = data.getCurrentTeamID();
        List<EntityShipBase> ships = player.level().getEntitiesOfClass(EntityShipBase.class,
                player.getBoundingBox().inflate(NEARBY_SHIP_SYNC_RADIUS),
                ship -> ship.isOwnedBy(player) && !ship.isInDeadPose());

        for (EntityShipBase ship : ships) {
            if (ship.getFormationTeam() == teamId) {
                int slot = ship.getFormationSlot();
                ship.setPointerSelected(data.isSelected(teamId, slot));
            } else {
                ship.setPointerSelected(false);
                if (clearDeselectedTargets) {
                    ship.clearPointerTarget();
                    ship.clearPointerTargetEntity();
                }
            }
        }
    }

    public static void handleFormationAction(Player player, int action, int param1, int param2,
                                             String paramString, Optional<UUID> paramUuid) {
        if (player == null) {
            return;
        }
        AdmiralData data = PlayerStateService.admiralData(player);

        switch (action) {
            case 0 -> {
                if (PlayerStateService.setCurrentTeamId(player, param1)) {
                    syncNearbyShipsForCurrentTeam(player, true);
                }
            }
            case 1 -> PlayerStateService.setCurrentTeamFormation(player, param1);
            case 2 -> setCurrentTeamSlotSelected(player, data, param1, param2 != 0);
            case 3 -> removeCurrentTeamSlot(player, data, param1);
            case 4 -> PlayerStateService.setCurrentTeamName(player, paramString);
            case 5 -> paramUuid.ifPresent(uuid -> replaceCurrentTeamSlot(player, param1, uuid));
            case 6 -> swapCurrentTeamSlots(player, data, param1, param2);
            case 7 -> importNearbySelectedShips(player, data);
            case 8 -> openCurrentTeamSlotShipMenu(player, data, param1);
            default -> {
            }
        }

        if (player instanceof ServerPlayer serverPlayer) {
            PlayerStateService.sendAdmiralState(serverPlayer);
        }
    }

    public static void handlePointerRosterToggle(Player player, UUID targetUuid) {
        if (player == null || targetUuid == null) {
            return;
        }
        AdmiralData data = PlayerStateService.admiralData(player);
        int teamId = data.getCurrentTeamID();
        int slot = data.findShipSlot(teamId, targetUuid);

        if (slot != -1) {
            boolean nextState = !data.isSelected(teamId, slot);
            if (PlayerStateService.setCurrentTeamSlotSelected(player, slot, nextState)) {
                withServerShip(player, targetUuid, ship -> ship.setPointerSelected(nextState));
            }
        } else {
            int assignedSlot = PlayerStateService.assignShipToCurrentTeam(player, targetUuid);
            if (assignedSlot == -1) {
                player.displayClientMessage(Component.translatable("chat.shincolle.formation.teamfull"), false);
            } else {
                withServerShip(player, targetUuid, ship -> applyFormationState(ship, teamId, assignedSlot, true));
            }
        }

        if (player instanceof ServerPlayer serverPlayer) {
            PlayerStateService.sendAdmiralState(serverPlayer);
        }
    }

    private static void setCurrentTeamSlotSelected(Player player, AdmiralData data, int slotId, boolean selected) {
        if (!PlayerStateService.setCurrentTeamSlotSelected(player, slotId, selected)) {
            return;
        }

        UUID shipUuid = data.getShipUUID(data.getCurrentTeamID(), slotId);
        if (shipUuid != null) {
            withServerShip(player, shipUuid, ship -> ship.setPointerSelected(selected));
        }
    }

    private static void openCurrentTeamSlotShipMenu(Player player, AdmiralData data, int slotId) {
        if (slotId < 0 || slotId >= AdmiralData.SLOT_COUNT) {
            return;
        }

        UUID shipUuid = data.getShipUUID(data.getCurrentTeamID(), slotId);
        if (shipUuid != null) {
            withServerShip(player, shipUuid, ship -> ship.openShipMenu(player));
        }
    }

    private static void removeCurrentTeamSlot(Player player, AdmiralData data, int slotId) {
        if (slotId < 0 || slotId >= AdmiralData.SLOT_COUNT) {
            return;
        }

        UUID shipUuid = data.getShipUUID(data.getCurrentTeamID(), slotId);
        if (shipUuid != null) {
            withServerShip(player, shipUuid, FormationService::clearFormationState);
        }
        PlayerStateService.removeShipFromTeams(player, shipUuid);
    }

    private static void replaceCurrentTeamSlot(Player player, int slotId, UUID shipUuid) {
        PlayerStateService.SlotAssignment assignment = PlayerStateService.setCurrentTeamSlot(player, slotId, shipUuid);
        if (assignment == null) {
            return;
        }

        UUID replacedUuid = assignment.replacedUuid();
        if (replacedUuid != null && !replacedUuid.equals(shipUuid)) {
            withServerShip(player, replacedUuid, FormationService::clearFormationState);
        }
        withServerShip(player, shipUuid, ship -> applyFormationState(ship, assignment.teamId(), assignment.slotId(), true));
    }

    private static void swapCurrentTeamSlots(Player player, AdmiralData data, int slot1, int slot2) {
        if (!PlayerStateService.swapCurrentTeamSlots(player, slot1, slot2)) {
            return;
        }

        int teamId = data.getCurrentTeamID();
        UUID uuid1 = data.getShipUUID(teamId, slot1);
        if (uuid1 != null) {
            withServerShip(player, uuid1, ship -> {
                ship.setFormationSlot(slot1);
                ship.setPointerSelected(data.isSelected(teamId, slot1));
            });
        }

        UUID uuid2 = data.getShipUUID(teamId, slot2);
        if (uuid2 != null) {
            withServerShip(player, uuid2, ship -> {
                ship.setFormationSlot(slot2);
                ship.setPointerSelected(data.isSelected(teamId, slot2));
            });
        }
    }

    private static void importNearbySelectedShips(Player player, AdmiralData data) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        int teamId = data.getCurrentTeamID();
        boolean teamFilledDuringSync = false;
        List<EntityShipBase> nearbySelected = serverLevel.getEntitiesOfClass(EntityShipBase.class,
                player.getBoundingBox().inflate(NEARBY_SELECTED_IMPORT_RADIUS),
                ship -> ship.isPointerSelected() && player.getUUID().equals(ship.getOwnerUUID()));
        List<EntityShipBase> nearbyOwned = serverLevel.getEntitiesOfClass(EntityShipBase.class,
                player.getBoundingBox().inflate(NEARBY_SHIP_SYNC_RADIUS),
                ship -> player.getUUID().equals(ship.getOwnerUUID()) && !ship.isInDeadPose());

        for (EntityShipBase ship : nearbySelected) {
            if (!data.isShipInTeam(teamId, ship.getUUID())
                    && PlayerStateService.assignShipToCurrentTeam(player, ship.getUUID()) == -1) {
                teamFilledDuringSync = true;
            }
        }

        for (EntityShipBase ship : nearbyOwned) {
            if (ship.getFormationTeam() == teamId && !data.isShipInTeam(teamId, ship.getUUID())) {
                clearFormationState(ship);
            }
        }

        for (int i = 0; i < AdmiralData.SLOT_COUNT; i++) {
            UUID uuid = data.getShipUUID(teamId, i);
            if (uuid != null) {
                final int slotId = i;
                withServerShip(player, uuid, ship -> applyFormationState(ship, teamId, slotId, data.isSelected(teamId, slotId)));
            }
        }

        if (teamFilledDuringSync) {
            player.displayClientMessage(Component.translatable("chat.shincolle.formation.teamfull"), false);
        }
    }

    private static void withServerShip(Player player, UUID shipUuid, java.util.function.Consumer<EntityShipBase> action) {
        if (!(player.level() instanceof ServerLevel serverLevel) || shipUuid == null) {
            return;
        }

        Entity entity = serverLevel.getEntity(shipUuid);
        if (entity instanceof EntityShipBase ship) {
            action.accept(ship);
        }
    }
}
