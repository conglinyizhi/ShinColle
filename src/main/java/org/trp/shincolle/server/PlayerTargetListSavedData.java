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

public final class PlayerTargetListSavedData extends SavedData {
    private static final String DATA_ID = "shincolle_player_target_lists";

    private final Map<UUID, TreeSet<String>> entries = new HashMap<>();

    public static PlayerTargetListSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(PlayerTargetListSavedData::new, PlayerTargetListSavedData::load, null),
                DATA_ID
        );
    }

    private static PlayerTargetListSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        PlayerTargetListSavedData data = new PlayerTargetListSavedData();
        ListTag list = tag.getList("Entries", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entryTag = list.getCompound(i);
            if (!entryTag.hasUUID("Owner")) {
                continue;
            }
            UUID owner = entryTag.getUUID("Owner");
            TreeSet<String> classNames = new TreeSet<>();
            ListTag classes = entryTag.getList("ClassNames", Tag.TAG_STRING);
            for (int j = 0; j < classes.size(); j++) {
                String className = classes.getString(j);
                if (!className.isBlank()) {
                    classNames.add(className);
                }
            }
            if (!classNames.isEmpty()) {
                data.entries.put(owner, classNames);
            }
        }
        return data;
    }

    public boolean toggle(UUID owner, String className) {
        if (owner == null || className == null || className.isBlank()) {
            return false;
        }

        TreeSet<String> classNames = this.entries.computeIfAbsent(owner, key -> new TreeSet<>());
        boolean added;
        if (classNames.contains(className)) {
            classNames.remove(className);
            added = false;
            if (classNames.isEmpty()) {
                this.entries.remove(owner);
            }
        } else {
            classNames.add(className);
            added = true;
        }

        setDirty();
        return added;
    }

    public boolean contains(UUID owner, String className) {
        if (owner == null || className == null) {
            return false;
        }
        TreeSet<String> classNames = this.entries.get(owner);
        return classNames != null && classNames.contains(className);
    }

    public Collection<String> entries(UUID owner) {
        TreeSet<String> classNames = owner == null ? null : this.entries.get(owner);
        return classNames == null ? java.util.List.of() : java.util.Collections.unmodifiableSet(classNames);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Map.Entry<UUID, TreeSet<String>> entry : this.entries.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID("Owner", entry.getKey());
            ListTag classes = new ListTag();
            for (String className : entry.getValue()) {
                classes.add(StringTag.valueOf(className));
            }
            entryTag.put("ClassNames", classes);
            list.add(entryTag);
        }
        tag.put("Entries", list);
        return tag;
    }
}
