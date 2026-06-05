package org.trp.shincolle.server

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.saveddata.SavedData
import java.util.*
import java.util.function.BiFunction
import java.util.function.Supplier

class PlayerTargetListSavedData : SavedData() {
    private val entries: MutableMap<UUID?, TreeSet<String?>> = HashMap<UUID?, TreeSet<String?>>()

    fun toggle(owner: UUID?, className: String?): Boolean {
        if (owner == null || className == null || className.isBlank()) {
            return false
        }

        val classNames = this.entries.computeIfAbsent(owner) { key: UUID? -> TreeSet<String?>() }
        val added: Boolean
        if (classNames.contains(className)) {
            classNames.remove(className)
            added = false
            if (classNames.isEmpty()) {
                this.entries.remove(owner)
            }
        } else {
            classNames.add(className)
            added = true
        }

        setDirty()
        return added
    }

    fun contains(owner: UUID?, className: String?): Boolean {
        if (owner == null || className == null) {
            return false
        }
        val classNames = this.entries.get(owner)
        return classNames != null && classNames.contains(className)
    }

    fun entries(owner: UUID?): MutableCollection<String?> {
        val classNames = if (owner == null) null else this.entries.get(owner)
        return if (classNames == null) mutableListOf<String?>() else Collections.unmodifiableSet<String?>(classNames)
    }

    override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
        val list = ListTag()
        for (entry in this.entries.entries) {
            val entryTag = CompoundTag()
            entryTag.putUUID("Owner", entry.key)
            val classes = ListTag()
            for (className in entry.value) {
                classes.add(StringTag.valueOf(className))
            }
            entryTag.put("ClassNames", classes)
            list.add(entryTag)
        }
        tag.put("Entries", list)
        return tag
    }

    companion object {
        private const val DATA_ID = "shincolle_player_target_lists"

        fun get(level: ServerLevel): PlayerTargetListSavedData {
            return level.getServer().overworld().getDataStorage().computeIfAbsent<PlayerTargetListSavedData>(
                Factory<PlayerTargetListSavedData?>(
                    Supplier { PlayerTargetListSavedData() },
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

        private fun load(tag: CompoundTag, registries: HolderLookup.Provider?): PlayerTargetListSavedData {
            val data = PlayerTargetListSavedData()
            val list = tag.getList("Entries", Tag.TAG_COMPOUND.toInt())
            for (i in list.indices) {
                val entryTag = list.getCompound(i)
                if (!entryTag.hasUUID("Owner")) {
                    continue
                }
                val owner = entryTag.getUUID("Owner")
                val classNames = TreeSet<String?>()
                val classes = entryTag.getList("ClassNames", Tag.TAG_STRING.toInt())
                for (j in classes.indices) {
                    val className = classes.getString(j)
                    if (!className.isBlank()) {
                        classNames.add(className)
                    }
                }
                if (!classNames.isEmpty()) {
                    data.entries.put(owner, classNames)
                }
            }
            return data
        }
    }
}
