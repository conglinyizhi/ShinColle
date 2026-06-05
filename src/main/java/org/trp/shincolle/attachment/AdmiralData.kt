package org.trp.shincolle.attachment

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.neoforged.neoforge.attachment.IAttachmentHolder
import java.util.*
import kotlin.math.max
import kotlin.math.min

class AdmiralData {
    private val teams = Array<Array<UUID?>?>(TEAM_COUNT) { arrayOfNulls<UUID>(SLOT_COUNT) }
    private val selectionStates = Array<BooleanArray?>(TEAM_COUNT) { BooleanArray(SLOT_COUNT) }
    private val formationIDs = IntArray(TEAM_COUNT)
    private val teamNames = arrayOfNulls<String>(TEAM_COUNT)
    private var currentTeamID = 0
    private var hasReceivedBook = false
    private var marriedShipCount = 0
    var isRingFlightActive: Boolean = false

    init {
        for (i in 0..<TEAM_COUNT) {
            teamNames[i] = "Team " + (i + 1)
            for (j in 0..<SLOT_COUNT) {
                selectionStates[i]!![j] = true
            }
        }
    }

    fun hasReceivedBook(): Boolean {
        return hasReceivedBook
    }

    fun setHasReceivedBook(hasReceivedBook: Boolean) {
        this.hasReceivedBook = hasReceivedBook
    }

    fun getMarriedShipCount(): Int {
        return marriedShipCount
    }

    fun setMarriedShipCount(marriedShipCount: Int) {
        this.marriedShipCount = max(0, marriedShipCount)
    }

    fun addMarriedShipCount(delta: Int) {
        this.marriedShipCount = max(0, this.marriedShipCount + delta)
    }

    fun getShipUUID(teamId: Int, slotId: Int): UUID? {
        if (teamId < 0 || teamId >= TEAM_COUNT || slotId < 0 || slotId >= SLOT_COUNT) return null
        return teams[teamId]!![slotId]
    }

    fun setShipUUID(teamId: Int, slotId: Int, uuid: UUID?) {
        if (teamId < 0 || teamId >= TEAM_COUNT || slotId < 0 || slotId >= SLOT_COUNT) return
        teams[teamId]!![slotId] = uuid
    }

    fun isSelected(teamId: Int, slotId: Int): Boolean {
        if (teamId < 0 || teamId >= TEAM_COUNT || slotId < 0 || slotId >= SLOT_COUNT) return false
        return selectionStates[teamId]!![slotId]
    }

    fun setSelected(teamId: Int, slotId: Int, selected: Boolean) {
        if (teamId < 0 || teamId >= TEAM_COUNT || slotId < 0 || slotId >= SLOT_COUNT) return
        selectionStates[teamId]!![slotId] = selected
    }

    fun getFormationID(teamId: Int): Int {
        if (teamId < 0 || teamId >= TEAM_COUNT) return 0
        return formationIDs[teamId]
    }

    fun setFormationID(teamId: Int, formationId: Int) {
        if (teamId < 0 || teamId >= TEAM_COUNT) return
        formationIDs[teamId] = max(0, formationId)
    }

    fun getTeamName(teamId: Int): String? {
        if (teamId < 0 || teamId >= TEAM_COUNT) return ""
        return teamNames[teamId]
    }

    fun swapShips(teamId: Int, slot1: Int, slot2: Int) {
        if (teamId < 0 || teamId >= TEAM_COUNT) return
        if (slot1 < 0 || slot1 >= SLOT_COUNT || slot2 < 0 || slot2 >= SLOT_COUNT) return

        val tempUUID = teams[teamId]!![slot1]
        teams[teamId]!![slot1] = teams[teamId]!![slot2]
        teams[teamId]!![slot2] = tempUUID

        val tempSel = selectionStates[teamId]!![slot1]
        selectionStates[teamId]!![slot1] = selectionStates[teamId]!![slot2]
        selectionStates[teamId]!![slot2] = tempSel
    }

    fun setTeamName(teamId: Int, name: String?) {
        if (teamId < 0 || teamId >= TEAM_COUNT) return
        if (name == null || name.isBlank()) {
            teamNames[teamId] = "Team " + (teamId + 1)
        } else {
            teamNames[teamId] = name.trim()
        }
    }

    fun getCurrentTeamID(): Int {
        return currentTeamID
    }

    fun setCurrentTeamID(currentTeamID: Int) {
        this.currentTeamID = max(0, min(TEAM_COUNT - 1, currentTeamID))
    }

    fun findFirstEmptySlot(teamId: Int): Int {
        if (teamId < 0 || teamId >= TEAM_COUNT) return -1
        for (i in 0..<SLOT_COUNT) {
            if (teams[teamId]!![i] == null) return i
        }
        return -1
    }

    fun isShipInTeam(teamId: Int, uuid: UUID?): Boolean {
        if (teamId < 0 || teamId >= TEAM_COUNT || uuid == null) return false
        for (i in 0..<SLOT_COUNT) {
            if (uuid == teams[teamId]!![i]) return true
        }
        return false
    }

    fun isShipInAnyTeam(uuid: UUID?): Boolean {
        if (uuid == null) return false
        for (i in 0..<TEAM_COUNT) {
            if (isShipInTeam(i, uuid)) return true
        }
        return false
    }

    fun findShipTeam(uuid: UUID?): Int {
        if (uuid == null) return -1
        for (teamId in 0..<TEAM_COUNT) {
            if (isShipInTeam(teamId, uuid)) {
                return teamId
            }
        }
        return -1
    }

    fun findShipSlot(teamId: Int, uuid: UUID?): Int {
        if (teamId < 0 || teamId >= TEAM_COUNT || uuid == null) return -1
        for (slotId in 0..<SLOT_COUNT) {
            if (uuid == teams[teamId]!![slotId]) {
                return slotId
            }
        }
        return -1
    }

    fun removeShip(uuid: UUID?): Boolean {
        val teamId = findShipTeam(uuid)
        if (teamId == -1) return false
        val slotId = findShipSlot(teamId, uuid)
        if (slotId == -1) return false
        teams[teamId]!![slotId] = null
        selectionStates[teamId]!![slotId] = true
        return true
    }

    fun assignShipToTeam(teamId: Int, uuid: UUID?): Int {
        if (teamId < 0 || teamId >= TEAM_COUNT || uuid == null) return -1
        val existingSlot = findShipSlot(teamId, uuid)
        if (existingSlot != -1) return existingSlot

        val slotId = findFirstEmptySlot(teamId)
        if (slotId == -1) return -1
        removeShip(uuid)
        teams[teamId]!![slotId] = uuid
        selectionStates[teamId]!![slotId] = true
        return slotId
    }

    fun sanitize() {
        currentTeamID = max(0, min(TEAM_COUNT - 1, currentTeamID))
        for (i in 0..<TEAM_COUNT) {
            if (teamNames[i] == null || teamNames[i]!!.isBlank()) {
                teamNames[i] = "Team " + (i + 1)
            }
            formationIDs[i] = max(0, formationIDs[i])
        }
    }

    fun serializeNBT(): CompoundTag {
        val nbt = CompoundTag()
        val teamsList = ListTag()
        for (i in 0..<TEAM_COUNT) {
            val teamTag = CompoundTag()
            val slotsList = ListTag()
            for (j in 0..<SLOT_COUNT) {
                val slotTag = CompoundTag()
                if (teams[i]!![j] != null) {
                    slotTag.putUUID("UUID", teams[i]!![j])
                }
                slotTag.putBoolean("Selected", selectionStates[i]!![j])
                slotsList.add(slotTag)
            }
            teamTag.put("Slots", slotsList)
            teamTag.putInt("Formation", formationIDs[i])
            teamTag.putString("Name", teamNames[i])
            teamsList.add(teamTag)
        }
        nbt.put("Teams", teamsList)
        nbt.putInt("CurrentTeam", currentTeamID)
        nbt.putBoolean("HasReceivedBook", hasReceivedBook)
        nbt.putInt("MarriedShipCount", marriedShipCount)
        nbt.putBoolean("RingFlightActive", this.isRingFlightActive)
        return nbt
    }

    fun deserializeNBT(nbt: CompoundTag) {
        if (nbt.contains("Teams", Tag.TAG_LIST.toInt())) {
            val teamsList = nbt.getList("Teams", Tag.TAG_COMPOUND.toInt())
            for (i in 0..<min(TEAM_COUNT, teamsList.size)) {
                val teamTag = teamsList.getCompound(i)
                if (teamTag.contains("Slots", Tag.TAG_LIST.toInt())) {
                    val slotsList = teamTag.getList("Slots", Tag.TAG_COMPOUND.toInt())
                    for (j in 0..<min(SLOT_COUNT, slotsList.size)) {
                        val slotTag = slotsList.getCompound(j)
                        if (slotTag.hasUUID("UUID")) {
                            teams[i]!![j] = slotTag.getUUID("UUID")
                        } else {
                            teams[i]!![j] = null
                        }
                        selectionStates[i]!![j] = slotTag.getBoolean("Selected")
                    }
                }
                formationIDs[i] = teamTag.getInt("Formation")
                teamNames[i] = teamTag.getString("Name")
            }
        }
        currentTeamID = nbt.getInt("CurrentTeam")
        hasReceivedBook = nbt.getBoolean("HasReceivedBook")
        marriedShipCount = max(0, nbt.getInt("MarriedShipCount"))
        this.isRingFlightActive = nbt.getBoolean("RingFlightActive")
        sanitize()
    }

    fun write(holder: IAttachmentHolder?): CompoundTag {
        return serializeNBT()
    }

    companion object {
        const val TEAM_COUNT: Int = 9
        const val SLOT_COUNT: Int = 6

        fun read(nbt: CompoundTag, holder: IAttachmentHolder?): AdmiralData {
            val data = AdmiralData()
            data.deserializeNBT(nbt)
            return data
        }
    }
}
