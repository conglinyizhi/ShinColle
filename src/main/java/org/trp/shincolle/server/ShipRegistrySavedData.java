package org.trp.shincolle.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.trp.shincolle.entity.base.EntityShipBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ShipRegistrySavedData extends SavedData {
    private static final String DATA_ID = "shincolle_ship_registry";

    private final Map<UUID, ShipEntry> ships = new HashMap<>();

    public static ShipRegistrySavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(ShipRegistrySavedData::new, ShipRegistrySavedData::load, null),
                DATA_ID
        );
    }

    private static ShipRegistrySavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        ShipRegistrySavedData data = new ShipRegistrySavedData();
        ListTag list = tag.getList("Ships", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entryTag = list.getCompound(i);
            if (!entryTag.hasUUID("ShipUuid")) {
                continue;
            }
            ShipEntry entry = ShipEntry.fromTag(entryTag);
            if (entry != null) {
                data.ships.put(entry.shipUuid(), entry);
            }
        }
        return data;
    }

    public void updateShip(EntityShipBase ship) {
        if (!(ship.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        UUID shipUuid = ship.getUUID();
        ShipEntry next = new ShipEntry(
                shipUuid,
                ship.getName().getString(),
                net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(ship.getType()),
                serverLevel.dimension(),
                ship.blockPosition().immutable(),
                ship.getOwnerUUID(),
                ship.isStateMarried(),
                ship.isHostileShipMob(),
                false
        );

        ShipEntry prev = this.ships.put(shipUuid, next);
        if (!next.equals(prev)) {
            setDirty();
        }
    }

    public void markRemoved(EntityShipBase ship) {
        if (!(ship.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        UUID shipUuid = ship.getUUID();
        ShipEntry previous = this.ships.get(shipUuid);
        ShipEntry next = new ShipEntry(
                shipUuid,
                ship.getName().getString(),
                net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(ship.getType()),
                serverLevel.dimension(),
                ship.blockPosition().immutable(),
                ship.getOwnerUUID(),
                ship.isStateMarried(),
                ship.isHostileShipMob(),
                true
        );
        this.ships.put(shipUuid, next);
        if (!next.equals(previous)) {
            setDirty();
        }
    }

    public boolean delete(UUID shipUuid) {
        if (shipUuid == null) {
            return false;
        }
        ShipEntry removed = this.ships.remove(shipUuid);
        if (removed != null) {
            setDirty();
            return true;
        }
        return false;
    }

    public ShipEntry get(UUID shipUuid) {
        return shipUuid == null ? null : this.ships.get(shipUuid);
    }

    public Collection<ShipEntry> all() {
        return this.ships.values();
    }

    public List<ShipEntry> listSorted() {
        List<ShipEntry> list = new ArrayList<>(this.ships.values());
        list.sort(Comparator
                .comparing((ShipEntry entry) -> entry.ownerUuid() == null ? "" : entry.ownerUuid().toString())
                .thenComparing(ShipEntry::typeId, Comparator.comparing(ResourceLocation::toString))
                .thenComparing(ShipEntry::displayName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(ShipEntry::shipUuid));
        return list;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (ShipEntry entry : this.ships.values()) {
            list.add(entry.toTag());
        }
        tag.put("Ships", list);
        return tag;
    }

    public record ShipEntry(
            UUID shipUuid,
            String displayName,
            ResourceLocation typeId,
            ResourceKey<Level> dimension,
            net.minecraft.core.BlockPos pos,
            UUID ownerUuid,
            boolean married,
            boolean hostile,
            boolean removed
    ) {
        private CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("ShipUuid", this.shipUuid);
            tag.putString("DisplayName", this.displayName);
            tag.putString("TypeId", this.typeId.toString());
            tag.putString("Dimension", this.dimension.location().toString());
            tag.putInt("PosX", this.pos.getX());
            tag.putInt("PosY", this.pos.getY());
            tag.putInt("PosZ", this.pos.getZ());
            if (this.ownerUuid != null) {
                tag.putUUID("OwnerUuid", this.ownerUuid);
            }
            tag.putBoolean("Married", this.married);
            tag.putBoolean("Hostile", this.hostile);
            tag.putBoolean("Removed", this.removed);
            return tag;
        }

        private static ShipEntry fromTag(CompoundTag tag) {
            if (!tag.hasUUID("ShipUuid")) {
                return null;
            }

            ResourceLocation typeId = ResourceLocation.tryParse(tag.getString("TypeId"));
            ResourceLocation dimensionId = ResourceLocation.tryParse(tag.getString("Dimension"));
            if (typeId == null || dimensionId == null) {
                return null;
            }

            UUID ownerUuid = tag.hasUUID("OwnerUuid") ? tag.getUUID("OwnerUuid") : null;
            return new ShipEntry(
                    tag.getUUID("ShipUuid"),
                    tag.getString("DisplayName"),
                    typeId,
                    ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, dimensionId),
                    new net.minecraft.core.BlockPos(tag.getInt("PosX"), tag.getInt("PosY"), tag.getInt("PosZ")),
                    ownerUuid,
                    tag.getBoolean("Married"),
                    tag.getBoolean("Hostile"),
                    tag.getBoolean("Removed")
            );
        }
    }
}
