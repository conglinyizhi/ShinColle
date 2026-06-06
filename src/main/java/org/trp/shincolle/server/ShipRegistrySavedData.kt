package org.trp.shincolle.server

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData
import org.trp.shincolle.entity.base.EntityShipBase
import java.util.*
import java.util.function.BiFunction
import java.util.function.Supplier

class ShipRegistrySavedData : SavedData() {
    private val ships: MutableMap<UUID?, ShipEntry> = HashMap<UUID?, ShipEntry>()

    fun updateShip(ship: EntityShipBase) {
        if (ship.level() !is ServerLevel) {
            return
        }

        val serverLevel = ship.level() as ServerLevel
        val shipUuid = ship.uuid
        val next = ShipEntry(
            shipUuid,
            ship.name.string,
            BuiltInRegistries.ENTITY_TYPE.getKey(ship.type),
            serverLevel.dimension(),
            ship.blockPosition().immutable(),
            ship.ownerUUID,
            ship.isStateMarried,
            ship.isHostileShipMob,
            false
        )

        val prev = this.ships.put(shipUuid, next)
        if (next != prev) {
            setDirty()
        }
    }

    fun markRemoved(ship: EntityShipBase) {
        if (ship.level() !is ServerLevel) {
            return
        }

        val serverLevel = ship.level() as ServerLevel
        val shipUuid = ship.uuid
        val previous = this.ships.get(shipUuid)
        val next = ShipEntry(
            shipUuid,
            ship.name.string,
            BuiltInRegistries.ENTITY_TYPE.getKey(ship.type),
            serverLevel.dimension(),
            ship.blockPosition().immutable(),
            ship.ownerUUID,
            ship.isStateMarried,
            ship.isHostileShipMob,
            true
        )
        this.ships.put(shipUuid, next)
        if (next != previous) {
            setDirty()
        }
    }

    fun delete(shipUuid: UUID?): Boolean {
        if (shipUuid == null) {
            return false
        }
        val removed = this.ships.remove(shipUuid)
        if (removed != null) {
            setDirty()
            return true
        }
        return false
    }

    fun get(shipUuid: UUID?): ShipEntry? {
        return if (shipUuid == null) null else this.ships.get(shipUuid)
    }

    fun all(): MutableCollection<ShipEntry> {
        return Collections.unmodifiableCollection(this.ships.values)
    }

    fun listSorted(): MutableList<ShipEntry> {
        val list: MutableList<ShipEntry> = ArrayList(this.ships.values)
        list.sortWith(
            compareBy<ShipEntry> { it.ownerUuid?.toString() ?: "" }
                .thenBy { it.typeId.toString() }
                .thenBy(String.CASE_INSENSITIVE_ORDER) { it.displayName ?: "" }
                .thenBy { it.shipUuid }
        )
        return list
    }

    override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
        val list = ListTag()
        for (entry in this.ships.values) {
            list.add(entry.toTag())
        }
        tag.put("Ships", list)
        return tag
    }

    @JvmRecord
    data class ShipEntry(
        @JvmField val shipUuid: UUID?,
        @JvmField val displayName: String?,
        @JvmField val typeId: ResourceLocation?,
        @JvmField val dimension: ResourceKey<Level?>?,
        @JvmField val pos: BlockPos?,
        @JvmField val ownerUuid: UUID?,
        @JvmField val married: Boolean,
        @JvmField val hostile: Boolean,
        @JvmField val removed: Boolean
    ) {
        internal fun toTag(): CompoundTag {
            val tag = CompoundTag()
            tag.putUUID("ShipUuid", this.shipUuid)
            tag.putString("DisplayName", this.displayName)
            tag.putString("TypeId", this.typeId.toString())
            tag.putString("Dimension", (this.dimension ?: Level.OVERWORLD).location().toString())
            val safePos = this.pos ?: BlockPos.ZERO
            tag.putInt("PosX", safePos.x)
            tag.putInt("PosY", safePos.y)
            tag.putInt("PosZ", safePos.z)
            if (this.ownerUuid != null) {
                tag.putUUID("OwnerUuid", this.ownerUuid)
            }
            tag.putBoolean("Married", this.married)
            tag.putBoolean("Hostile", this.hostile)
            tag.putBoolean("Removed", this.removed)
            return tag
        }

        companion object {
            internal fun fromTag(tag: CompoundTag): ShipEntry? {
                if (!tag.hasUUID("ShipUuid")) {
                    return null
                }

                val typeId = ResourceLocation.tryParse(tag.getString("TypeId"))
                val dimensionId = ResourceLocation.tryParse(tag.getString("Dimension"))
                if (typeId == null || dimensionId == null) {
                    return null
                }

                val ownerUuid = if (tag.hasUUID("OwnerUuid")) tag.getUUID("OwnerUuid") else null
                return ShipEntry(
                    tag.getUUID("ShipUuid"),
                    tag.getString("DisplayName"),
                    typeId,
                    ResourceKey.create<Level?>(Registries.DIMENSION, dimensionId),
                    BlockPos(tag.getInt("PosX"), tag.getInt("PosY"), tag.getInt("PosZ")),
                    ownerUuid,
                    tag.getBoolean("Married"),
                    tag.getBoolean("Hostile"),
                    tag.getBoolean("Removed")
                )
            }
        }
    }

    companion object {
        private const val DATA_ID = "shincolle_ship_registry"

        @JvmStatic
        fun get(level: ServerLevel): ShipRegistrySavedData {
            return level.getServer().overworld().getDataStorage().computeIfAbsent<ShipRegistrySavedData>(
                Factory<ShipRegistrySavedData?>(
                    Supplier { ShipRegistrySavedData() },
                    BiFunction { tag: CompoundTag?, registries: HolderLookup.Provider? ->
                        Companion.load(
                            tag!!,
                            registries
                        )
                    },
                    null
                ),
                DATA_ID
            )
        }

        private fun load(tag: CompoundTag, registries: HolderLookup.Provider?): ShipRegistrySavedData {
            val data = ShipRegistrySavedData()
            val list = tag.getList("Ships", Tag.TAG_COMPOUND.toInt())
            for (i in list.indices) {
                val entryTag = list.getCompound(i)
                if (!entryTag.hasUUID("ShipUuid")) {
                    continue
                }
                val entry = ShipEntry.fromTag(entryTag)
                if (entry != null) {
                    data.ships.put(entry.shipUuid, entry)
                }
            }
            return data
        }
    }
}
