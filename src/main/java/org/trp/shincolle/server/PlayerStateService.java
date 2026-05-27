package org.trp.shincolle.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import org.trp.shincolle.attachment.AdmiralData;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.init.ModDataAttachments;
import org.trp.shincolle.init.ModItems;
import org.trp.shincolle.network.S2CAdmiralDataSyncPayload;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public final class PlayerStateService {
    public record SlotAssignment(int teamId, int slotId, UUID replacedUuid) {
    }

    private PlayerStateService() {
    }

    public static AdmiralData admiralData(Player player) {
        return player.getData(ModDataAttachments.ADMIRAL_DATA);
    }

    public static HashSet<Integer> collectedShips(Player player) {
        return player.getData(ModDataAttachments.COLLECTED_SHIPS);
    }

    public static S2CAdmiralDataSyncPayload admiralSyncPayload(Player player) {
        return S2CAdmiralDataSyncPayload.of(admiralData(player).serializeNBT(), collectedShips(player));
    }

    public static void applyAdmiralSync(Player player, CompoundTag admiralNbt, int[] collectedShipIds) {
        if (player == null) {
            return;
        }

        admiralData(player).deserializeNBT(admiralNbt);
        HashSet<Integer> collected = collectedShips(player);
        collected.clear();
        for (int classId : collectedShipIds) {
            collected.add(classId);
        }
    }

    public static boolean giveInitialManualIfNeeded(ServerPlayer player) {
        AdmiralData data = admiralData(player);
        if (data.hasReceivedBook()) {
            return false;
        }

        ItemStack bookStack = new ItemStack(ModItems.DESK_ITEM_BOOK.get());
        if (!player.addItem(bookStack)) {
            player.drop(bookStack, false);
        }
        data.setHasReceivedBook(true);
        return true;
    }

    public static void copyPersistentPlayerState(Player original, Player clone) {
        admiralData(clone).deserializeNBT(admiralData(original).serializeNBT());

        HashSet<Integer> originalCollected = collectedShips(original);
        HashSet<Integer> clonedCollected = collectedShips(clone);
        clonedCollected.clear();
        clonedCollected.addAll(originalCollected);
    }

    public static boolean registerCollectedShip(ServerPlayer player, int classId) {
        if (classId < 0) {
            return false;
        }

        boolean changed = collectedShips(player).add(classId);
        if (changed) {
            sendAdmiralState(player);
        }
        return changed;
    }

    public static boolean hasCollectedShip(Player player, int classId) {
        return player != null && collectedShips(player).contains(classId);
    }

    public static void syncAdmiralState(ServerPlayer player) {
        reconcileOwnedMarriedShipCount(player);
        sendAdmiralState(player);
    }

    public static void sendAdmiralState(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, admiralSyncPayload(player));
    }

    public static int getOwnedMarriedShipCount(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return reconcileOwnedMarriedShipCount(serverPlayer);
        }

        int stored = admiralData(player).getMarriedShipCount();
        if (stored > 0) {
            return stored;
        }

        UUID ownerId = player.getUUID();
        AABB search = player.getBoundingBox().inflate(256.0D, 128.0D, 256.0D);
        int scanned = player.level().getEntitiesOfClass(EntityShipBase.class, search,
                ship -> ship.isAlive()
                        && ship.isTame()
                        && ship.isStateMarried()
                        && Objects.equals(ship.getOwnerUUID(), ownerId)).size();
        if (scanned > 0) {
            admiralData(player).setMarriedShipCount(scanned);
        }
        return scanned;
    }

    public static int reconcileOwnedMarriedShipCount(ServerPlayer player) {
        UUID ownerId = player.getUUID();
        int marriedCount = 0;
        for (ServerLevel level : player.server.getAllLevels()) {
            marriedCount += level.getEntitiesOfClass(
                    EntityShipBase.class,
                    AABB.INFINITE,
                    ship -> ship.isAlive()
                            && ship.isTame()
                            && ship.isStateMarried()
                            && Objects.equals(ship.getOwnerUUID(), ownerId)
            ).size();
        }

        admiralData(player).setMarriedShipCount(marriedCount);
        return marriedCount;
    }

    public static void adjustOwnedMarriedShipCount(Player player, int delta) {
        if (delta == 0) {
            return;
        }

        admiralData(player).addMarriedShipCount(delta);
        if (player instanceof ServerPlayer serverPlayer) {
            sendAdmiralState(serverPlayer);
        }
    }

    public static int currentTeamId(Player player) {
        return admiralData(player).getCurrentTeamID();
    }

    public static boolean setCurrentTeamId(Player player, int teamId) {
        if (teamId < 0 || teamId >= AdmiralData.TEAM_COUNT) {
            return false;
        }

        admiralData(player).setCurrentTeamID(teamId);
        return true;
    }

    public static boolean setCurrentTeamFormation(Player player, int formationId) {
        if (formationId < 0) {
            return false;
        }

        AdmiralData data = admiralData(player);
        data.setFormationID(data.getCurrentTeamID(), formationId);
        return true;
    }

    public static boolean setCurrentTeamSlotSelected(Player player, int slotId, boolean selected) {
        if (slotId < 0 || slotId >= AdmiralData.SLOT_COUNT) {
            return false;
        }

        admiralData(player).setSelected(currentTeamId(player), slotId, selected);
        return true;
    }

    public static boolean setCurrentTeamName(Player player, String name) {
        admiralData(player).setTeamName(currentTeamId(player), name);
        return true;
    }

    public static boolean swapCurrentTeamSlots(Player player, int slot1, int slot2) {
        if (slot1 < 0 || slot1 >= AdmiralData.SLOT_COUNT
                || slot2 < 0 || slot2 >= AdmiralData.SLOT_COUNT
                || slot1 == slot2) {
            return false;
        }

        admiralData(player).swapShips(currentTeamId(player), slot1, slot2);
        return true;
    }

    public static int assignShipToCurrentTeam(Player player, UUID shipUuid) {
        return admiralData(player).assignShipToTeam(currentTeamId(player), shipUuid);
    }

    public static boolean removeShipFromTeams(Player player, UUID shipUuid) {
        return admiralData(player).removeShip(shipUuid);
    }

    public static SlotAssignment setCurrentTeamSlot(Player player, int slotId, UUID shipUuid) {
        if (slotId < 0 || slotId >= AdmiralData.SLOT_COUNT || shipUuid == null) {
            return null;
        }

        AdmiralData data = admiralData(player);
        int teamId = data.getCurrentTeamID();
        UUID replacedUuid = data.getShipUUID(teamId, slotId);
        data.removeShip(shipUuid);
        if (replacedUuid != null && !replacedUuid.equals(shipUuid)) {
            data.removeShip(replacedUuid);
        }
        data.setShipUUID(teamId, slotId, shipUuid);
        data.setSelected(teamId, slotId, true);
        return new SlotAssignment(teamId, slotId, replacedUuid);
    }
}
