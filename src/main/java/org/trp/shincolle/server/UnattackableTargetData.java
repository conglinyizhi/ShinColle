package org.trp.shincolle.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public final class UnattackableTargetData extends SavedData {
    private static final String DATA_ID = "shincolle_unattackable_targets";

    private final Set<String> classNames = new TreeSet<>();

    public static UnattackableTargetData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<UnattackableTargetData>(UnattackableTargetData::new, UnattackableTargetData::load, null),
                DATA_ID
        );
    }

    private static UnattackableTargetData load(CompoundTag tag, HolderLookup.Provider registries) {
        UnattackableTargetData data = new UnattackableTargetData();
        ListTag list = tag.getList("ClassNames", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            String name = list.getString(i);
            if (!name.isBlank()) {
                data.classNames.add(name);
            }
        }
        return data;
    }

    public boolean toggle(String className) {
        if (className == null || className.isBlank()) {
            return false;
        }

        boolean added;
        if (this.classNames.contains(className)) {
            this.classNames.remove(className);
            added = false;
        } else {
            this.classNames.add(className);
            added = true;
        }

        setDirty();
        return added;
    }

    public boolean contains(String className) {
        return className != null && this.classNames.contains(className);
    }

    public Collection<String> entries() {
        return this.classNames;
    }

    @Override
    public CompoundTag save(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (String className : this.classNames) {
            list.add(StringTag.valueOf(className));
        }
        tag.put("ClassNames", list);
        return tag;
    }
}
