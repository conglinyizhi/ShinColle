package org.trp.shincolle.server;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.trp.shincolle.attachment.AdmiralData;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.network.C2STeamDiplomacyPayload;
import org.trp.shincolle.network.S2CDeskDiplomacySyncPayload;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.UUID;

public final class TeamDiplomacyService {
    private TeamDiplomacyService() {
    }

    public static boolean isDiplomaticAlly(EntityShipBase ship, Entity target) {
        if (ship == null || target == null) {
            return false;
        }
        if (!(ship.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        UUID owner = ship.getOwnerUUID();
        UUID targetOwner = PointerInteractionService.getTargetOwnerUUID(target);
        return TeamDiplomacySavedData.get(serverLevel).areAllies(owner, targetOwner);
    }

    public static boolean isDiplomaticBanned(EntityShipBase ship, Entity target) {
        if (ship == null || target == null) {
            return false;
        }
        if (!(ship.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        UUID owner = ship.getOwnerUUID();
        UUID targetOwner = PointerInteractionService.getTargetOwnerUUID(target);
        return TeamDiplomacySavedData.get(serverLevel).isBanned(owner, targetOwner);
    }

    public static void handleAction(Player player, int action, UUID target) {
        if (player == null) {
            return;
        }
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        UUID owner = player.getUUID();
        if (target == null || owner.equals(target)) {
            return;
        }

        TeamDiplomacySavedData diplomacy = TeamDiplomacySavedData.get(serverLevel);
        Player targetPlayer = serverLevel.getPlayerByUUID(target);
        Component targetName = targetPlayer != null ? targetPlayer.getDisplayName() : Component.literal(target.toString());
        boolean changed;
        Component message;
        switch (action) {
            case C2STeamDiplomacyPayload.ACTION_ADD_ALLY -> {
                changed = diplomacy.addAlly(owner, target);
                message = changed
                        ? Component.translatable("chat.shincolle.team.ally_added").append(targetName)
                        : Component.translatable("chat.shincolle.team.ally_unchanged").append(targetName);
            }
            case C2STeamDiplomacyPayload.ACTION_REMOVE_ALLY -> {
                changed = diplomacy.removeAlly(owner, target);
                message = changed
                        ? Component.translatable("chat.shincolle.team.ally_removed").append(targetName)
                        : Component.translatable("chat.shincolle.team.ally_missing").append(targetName);
            }
            case C2STeamDiplomacyPayload.ACTION_ADD_BANNED -> {
                changed = diplomacy.addBanned(owner, target);
                message = changed
                        ? Component.translatable("chat.shincolle.team.hostile_added").append(targetName)
                        : Component.translatable("chat.shincolle.team.hostile_unchanged").append(targetName);
            }
            case C2STeamDiplomacyPayload.ACTION_REMOVE_BANNED -> {
                changed = diplomacy.removeBanned(owner, target);
                message = changed
                        ? Component.translatable("chat.shincolle.team.hostile_removed").append(targetName)
                        : Component.translatable("chat.shincolle.team.hostile_missing").append(targetName);
            }
            default -> {
                return;
            }
        }

        player.displayClientMessage(message, false);
        if (changed && player instanceof ServerPlayer serverPlayer) {
            sendDeskDiplomacySync(serverPlayer);
        }
    }

    public static void sendDeskDiplomacySync(ServerPlayer player) {
        TeamDiplomacySavedData diplomacy = TeamDiplomacySavedData.get(player.serverLevel());
        updateDiplomacyDisplayData(player, diplomacy);
        TeamDiplomacySavedData.TeamDiplomacyEntry entry = diplomacy.getOrCreate(player.getUUID());

        LinkedHashSet<UUID> displayIds = new LinkedHashSet<>();
        displayIds.addAll(entry.allies());
        displayIds.addAll(entry.banned());

        ArrayList<UUID> uuids = new ArrayList<>();
        ArrayList<String> teamNames = new ArrayList<>();
        ArrayList<String> leaderNames = new ArrayList<>();
        for (UUID target : displayIds) {
            if (target == null) {
                continue;
            }
            uuids.add(target);
            TeamDiplomacySavedData.TeamDiplomacyEntry targetEntry = diplomacy.get(target);
            teamNames.add(targetEntry == null ? "" : targetEntry.teamName());
            String leaderName = targetEntry == null ? "" : targetEntry.leaderName();
            if (leaderName.isBlank()) {
                leaderName = resolveDiplomacyLeaderName(player, target);
            }
            leaderNames.add(leaderName);
        }

        PacketDistributor.sendToPlayer(player, S2CDeskDiplomacySyncPayload.of(
                player.getUUID(),
                entry.allies(),
                entry.banned(),
                uuids,
                teamNames,
                leaderNames
        ));
    }

    private static void updateDiplomacyDisplayData(ServerPlayer player, TeamDiplomacySavedData diplomacy) {
        AdmiralData data = PlayerStateService.admiralData(player);
        String teamName = data.getTeamName(data.getCurrentTeamID());
        String leaderName = player.getName().getString();
        diplomacy.setDisplayData(player.getUUID(), teamName, leaderName);
    }

    private static String resolveDiplomacyLeaderName(ServerPlayer player, UUID target) {
        if (target == null) {
            return "";
        }
        ServerPlayer onlinePlayer = player.server.getPlayerList().getPlayer(target);
        if (onlinePlayer != null) {
            return onlinePlayer.getName().getString();
        }
        GameProfileCache profileCache = player.server.getProfileCache();
        if (profileCache == null) {
            return "";
        }
        return profileCache.get(target).map(com.mojang.authlib.GameProfile::getName).orElse("");
    }
}
