package org.trp.shincolle.network;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class DeskDiplomacySync {
    private static UUID ownerUuid;
    private static final Set<UUID> allies = new HashSet<>();
    private static final Set<UUID> banned = new HashSet<>();

    private DeskDiplomacySync() {
    }

    public static void update(UUID owner, Collection<UUID> nextAllies, Collection<UUID> nextBanned) {
        ownerUuid = owner;
        allies.clear();
        banned.clear();
        allies.addAll(nextAllies);
        banned.addAll(nextBanned);
    }

    public static void clear() {
        ownerUuid = null;
        allies.clear();
        banned.clear();
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
}
