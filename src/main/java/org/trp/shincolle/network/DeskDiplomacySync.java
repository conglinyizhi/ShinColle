package org.trp.shincolle.network;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class DeskDiplomacySync {
    private static UUID ownerUuid;
    private static final Set<UUID> allies = new HashSet<>();
    private static final Set<UUID> banned = new HashSet<>();
    private static final Map<UUID, String> teamNames = new HashMap<>();
    private static final Map<UUID, String> leaderNames = new HashMap<>();

    private DeskDiplomacySync() {
    }

    public static void update(
            UUID owner,
            Collection<UUID> nextAllies,
            Collection<UUID> nextBanned,
            Collection<UUID> displayUuids,
            Collection<String> displayTeamNames,
            Collection<String> displayLeaderNames
    ) {
        ownerUuid = owner;
        allies.clear();
        banned.clear();
        teamNames.clear();
        leaderNames.clear();
        allies.addAll(nextAllies);
        banned.addAll(nextBanned);

        UUID[] uuids = displayUuids.toArray(UUID[]::new);
        String[] teams = displayTeamNames.toArray(String[]::new);
        String[] leaders = displayLeaderNames.toArray(String[]::new);
        int count = Math.min(uuids.length, Math.min(teams.length, leaders.length));
        for (int i = 0; i < count; i++) {
            UUID uuid = uuids[i];
            if (uuid == null) {
                continue;
            }
            teamNames.put(uuid, teams[i] == null ? "" : teams[i]);
            leaderNames.put(uuid, leaders[i] == null ? "" : leaders[i]);
        }
    }

    public static void clear() {
        ownerUuid = null;
        allies.clear();
        banned.clear();
        teamNames.clear();
        leaderNames.clear();
    }

    public static UUID getOwnerUuid() {
        return ownerUuid;
    }

    public static boolean isAlly(UUID target) {
        return target != null && allies.contains(target);
    }

    public static boolean isBanned(UUID target) {
        return target != null && banned.contains(target);
    }

    public static Set<UUID> getAllies() {
        return Collections.unmodifiableSet(allies);
    }

    public static Set<UUID> getBanned() {
        return Collections.unmodifiableSet(banned);
    }

    public static String getTeamName(UUID target) {
        return target == null ? "" : teamNames.getOrDefault(target, "");
    }

    public static String getLeaderName(UUID target) {
        return target == null ? "" : leaderNames.getOrDefault(target, "");
    }
}
