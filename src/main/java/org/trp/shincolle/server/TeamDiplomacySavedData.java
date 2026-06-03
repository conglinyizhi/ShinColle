package org.trp.shincolle.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

public final class TeamDiplomacySavedData extends SavedData {
    private static final String DATA_ID = "shincolle_team_diplomacy";

    private final Map<UUID, TeamDiplomacyEntry> entries = new HashMap<>();

    public static TeamDiplomacySavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<TeamDiplomacySavedData>(TeamDiplomacySavedData::new, TeamDiplomacySavedData::load, null),
                DATA_ID
        );
    }

    private static TeamDiplomacySavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        TeamDiplomacySavedData data = new TeamDiplomacySavedData();
        ListTag list = tag.getList("Entries", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entryTag = list.getCompound(i);
            if (!entryTag.hasUUID("Owner")) {
                continue;
            }
            UUID owner = entryTag.getUUID("Owner");
            TeamDiplomacyEntry entry = new TeamDiplomacyEntry(owner);
            entry.teamName = entryTag.getString("TeamName");
            entry.leaderName = entryTag.getString("LeaderName");
            readUuidList(entryTag.getList("Allies", Tag.TAG_STRING), entry.allies);
            readUuidList(entryTag.getList("Banned", Tag.TAG_STRING), entry.banned);
            data.entries.put(owner, entry);
        }
        return data;
    }

    private static void readUuidList(ListTag list, Collection<UUID> output) {
        for (int i = 0; i < list.size(); i++) {
            String raw = list.getString(i);
            try {
                output.add(UUID.fromString(raw));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public TeamDiplomacyEntry getOrCreate(UUID owner) {
        return this.entries.computeIfAbsent(owner, TeamDiplomacyEntry::new);
    }

    public TeamDiplomacyEntry get(UUID owner) {
        return owner == null ? null : this.entries.get(owner);
    }

    public boolean areAllies(UUID owner, UUID target) {
        if (owner == null || target == null) {
            return false;
        }
        if (owner.equals(target)) {
            return true;
        }
        TeamDiplomacyEntry entry = this.entries.get(owner);
        return entry != null && entry.allies.contains(target);
    }

    public boolean isBanned(UUID owner, UUID target) {
        if (owner == null || target == null || owner.equals(target)) {
            return false;
        }
        TeamDiplomacyEntry entry = this.entries.get(owner);
        return entry != null && entry.banned.contains(target);
    }

    public boolean addAlly(UUID owner, UUID target) {
        if (!isValidRelation(owner, target)) {
            return false;
        }
        TeamDiplomacyEntry entry = getOrCreate(owner);
        entry.banned.remove(target);
        boolean changed = entry.allies.add(target);
        if (changed) {
            setDirty();
        }
        return changed;
    }

    public boolean removeAlly(UUID owner, UUID target) {
        if (!isValidRelation(owner, target)) {
            return false;
        }
        TeamDiplomacyEntry entry = get(owner);
        boolean changed = entry != null && entry.allies.remove(target);
        if (changed) {
            setDirty();
        }
        return changed;
    }

    public boolean addBanned(UUID owner, UUID target) {
        if (!isValidRelation(owner, target)) {
            return false;
        }
        TeamDiplomacyEntry entry = getOrCreate(owner);
        entry.allies.remove(target);
        boolean changed = entry.banned.add(target);
        if (changed) {
            setDirty();
        }
        return changed;
    }

    public boolean removeBanned(UUID owner, UUID target) {
        if (!isValidRelation(owner, target)) {
            return false;
        }
        TeamDiplomacyEntry entry = get(owner);
        boolean changed = entry != null && entry.banned.remove(target);
        if (changed) {
            setDirty();
        }
        return changed;
    }

    public boolean setDisplayData(UUID owner, String teamName, String leaderName) {
        if (owner == null) {
            return false;
        }
        TeamDiplomacyEntry entry = getOrCreate(owner);
        String nextTeamName = teamName == null ? "" : teamName;
        String nextLeaderName = leaderName == null ? "" : leaderName;
        if (entry.teamName.equals(nextTeamName) && entry.leaderName.equals(nextLeaderName)) {
            return false;
        }
        entry.teamName = nextTeamName;
        entry.leaderName = nextLeaderName;
        setDirty();
        return true;
    }

    private static boolean isValidRelation(UUID owner, UUID target) {
        return owner != null && target != null && !owner.equals(target);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (TeamDiplomacyEntry entry : this.entries.values()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID("Owner", entry.owner);
            entryTag.putString("TeamName", entry.teamName);
            entryTag.putString("LeaderName", entry.leaderName);
            entryTag.put("Allies", writeUuidList(entry.allies));
            entryTag.put("Banned", writeUuidList(entry.banned));
            list.add(entryTag);
        }
        tag.put("Entries", list);
        return tag;
    }

    private static ListTag writeUuidList(Collection<UUID> uuids) {
        ListTag list = new ListTag();
        for (UUID uuid : uuids) {
            list.add(StringTag.valueOf(uuid.toString()));
        }
        return list;
    }

    public static final class TeamDiplomacyEntry {
        private final UUID owner;
        private String teamName = "";
        private String leaderName = "";
        private final TreeSet<UUID> allies = new TreeSet<>();
        private final TreeSet<UUID> banned = new TreeSet<>();

        private TeamDiplomacyEntry(UUID owner) {
            this.owner = owner;
        }

        public UUID owner() {
            return this.owner;
        }

        public String teamName() {
            return this.teamName;
        }

        public String leaderName() {
            return this.leaderName;
        }

        public Collection<UUID> allies() {
            return this.allies;
        }

        public Collection<UUID> banned() {
            return this.banned;
        }
    }
}
