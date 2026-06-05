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

class TeamDiplomacySavedData : SavedData() {
    private val entries: MutableMap<UUID?, TeamDiplomacyEntry> = HashMap<UUID?, TeamDiplomacyEntry>()

    fun getOrCreate(owner: UUID?): TeamDiplomacyEntry {
        return this.entries.computeIfAbsent(owner) { key: UUID? -> TeamDiplomacyEntry(key!!) }
    }

    fun get(owner: UUID?): TeamDiplomacyEntry? {
        return if (owner == null) null else this.entries.get(owner)
    }

    fun areAllies(owner: UUID?, target: UUID?): Boolean {
        if (owner == null || target == null) {
            return false
        }
        if (owner == target) {
            return true
        }
        val entry = this.entries.get(owner)
        return entry != null && entry.allies.contains(target)
    }

    fun isBanned(owner: UUID?, target: UUID?): Boolean {
        if (owner == null || target == null || owner == target) {
            return false
        }
        val entry = this.entries.get(owner)
        return entry != null && entry.banned.contains(target)
    }

    fun addAlly(owner: UUID?, target: UUID?): Boolean {
        if (!isValidRelation(owner, target)) {
            return false
        }
        val entry = getOrCreate(owner)
        entry.banned.remove(target)
        val changed = entry.allies.add(target!!)
        if (changed) {
            setDirty()
        }
        return changed
    }

    fun removeAlly(owner: UUID?, target: UUID?): Boolean {
        if (!isValidRelation(owner, target)) {
            return false
        }
        val entry = get(owner)
        val changed = entry != null && entry.allies.remove(target)
        if (changed) {
            setDirty()
        }
        return changed
    }

    fun addBanned(owner: UUID?, target: UUID?): Boolean {
        if (!isValidRelation(owner, target)) {
            return false
        }
        val entry = getOrCreate(owner)
        entry.allies.remove(target)
        val changed = entry.banned.add(target!!)
        if (changed) {
            setDirty()
        }
        return changed
    }

    fun removeBanned(owner: UUID?, target: UUID?): Boolean {
        if (!isValidRelation(owner, target)) {
            return false
        }
        val entry = get(owner)
        val changed = entry != null && entry.banned.remove(target)
        if (changed) {
            setDirty()
        }
        return changed
    }

    fun setDisplayData(owner: UUID?, teamName: String?, leaderName: String?): Boolean {
        if (owner == null) {
            return false
        }
        val entry = getOrCreate(owner)
        val nextTeamName = if (teamName == null) "" else teamName
        val nextLeaderName = if (leaderName == null) "" else leaderName
        if (entry.teamName == nextTeamName && entry.leaderName == nextLeaderName) {
            return false
        }
        entry.teamName = nextTeamName
        entry.leaderName = nextLeaderName
        setDirty()
        return true
    }

    override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
        val list = ListTag()
        for (entry in this.entries.values) {
            val entryTag = CompoundTag()
            entryTag.putUUID("Owner", entry.owner)
            entryTag.putString("TeamName", entry.teamName)
            entryTag.putString("LeaderName", entry.leaderName)
            entryTag.put("Allies", writeUuidList(entry.allies))
            entryTag.put("Banned", writeUuidList(entry.banned))
            list.add(entryTag)
        }
        tag.put("Entries", list)
        return tag
    }

    class TeamDiplomacyEntry internal constructor(val owner: UUID) {
        internal var teamName = ""
        internal var leaderName = ""
        internal val allies = TreeSet<UUID>()
        internal val banned = TreeSet<UUID>()
    }

    companion object {
        private const val DATA_ID = "shincolle_team_diplomacy"

        fun get(level: ServerLevel): TeamDiplomacySavedData {
            return level.getServer().overworld().getDataStorage().computeIfAbsent<TeamDiplomacySavedData>(
                Factory<TeamDiplomacySavedData?>(
                    Supplier { TeamDiplomacySavedData() },
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

        private fun load(tag: CompoundTag, registries: HolderLookup.Provider?): TeamDiplomacySavedData {
            val data = TeamDiplomacySavedData()
            val list = tag.getList("Entries", Tag.TAG_COMPOUND.toInt())
            for (i in list.indices) {
                val entryTag = list.getCompound(i)
                if (!entryTag.hasUUID("Owner")) {
                    continue
                }
                val owner = entryTag.getUUID("Owner")
                val entry = TeamDiplomacyEntry(owner)
                entry.teamName = entryTag.getString("TeamName")
                entry.leaderName = entryTag.getString("LeaderName")
                readUuidList(entryTag.getList("Allies", Tag.TAG_STRING.toInt()), entry.allies)
                readUuidList(entryTag.getList("Banned", Tag.TAG_STRING.toInt()), entry.banned)
                data.entries.put(owner, entry)
            }
            return data
        }

        private fun readUuidList(list: ListTag, output: MutableCollection<UUID>) {
            for (i in list.indices) {
                val raw = list.getString(i)
                try {
                    output.add(UUID.fromString(raw))
                } catch (ignored: IllegalArgumentException) {
                }
            }
        }

        private fun isValidRelation(owner: UUID?, target: UUID?): Boolean {
            return owner != null && target != null && (owner != target)
        }

        private fun writeUuidList(uuids: MutableCollection<UUID>): ListTag {
            val list = ListTag()
            for (uuid in uuids) {
                list.add(StringTag.valueOf(uuid.toString()))
            }
            return list
        }
    }
}
