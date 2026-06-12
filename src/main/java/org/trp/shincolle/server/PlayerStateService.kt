package org.trp.shincolle.server

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.network.PacketDistributor
import org.trp.shincolle.attachment.AdmiralData
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModDataAttachments
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.item.PointerItem
import org.trp.shincolle.network.S2CAdmiralDataSyncPayload
import org.trp.shincolle.utility.ShipLookupHelper
import java.util.*
import java.util.function.Predicate

object PlayerStateService {
    private const val DAILY_MARRIED_SCAN_RADIUS = 64.0

    @JvmStatic
    fun admiralData(player: Player): AdmiralData {
        return player.getData<AdmiralData>(ModDataAttachments.ADMIRAL_DATA)
    }

    fun collectedShips(player: Player): HashSet<Int?> {
        return player.getData<HashSet<Int?>>(ModDataAttachments.COLLECTED_SHIPS)
    }

    fun admiralSyncPayload(player: Player): S2CAdmiralDataSyncPayload {
        return S2CAdmiralDataSyncPayload.of(admiralData(player).serializeNBT(), collectedShips(player))
    }

    @JvmStatic
    fun applyAdmiralSync(player: Player?, admiralNbt: CompoundTag, collectedShipIds: IntArray) {
        if (player == null) {
            return
        }

        admiralData(player).deserializeNBT(admiralNbt)
        val collected = collectedShips(player)
        collected.clear()
        for (classId in collectedShipIds) {
            collected.add(classId)
        }
    }

    @JvmStatic
    fun refreshClientPointerSelection(player: Player?) {
        if (player == null || !player.level().isClientSide) {
            return
        }

        var mode = PointerItem.MODE_SINGLE
        var pointerStack = ItemStack.EMPTY
        val main = player.mainHandItem
        if (main.`is`(ModItems.POINTER_ITEM.get())) {
            pointerStack = main
        } else {
            val off = player.offhandItem
            if (off.`is`(ModItems.POINTER_ITEM.get())) {
                pointerStack = off
            }
        }
        if (!pointerStack.isEmpty() && pointerStack.item is PointerItem) {
            mode = (pointerStack.item as PointerItem).getMode(pointerStack)
        }

        if (mode == PointerItem.MODE_FORMATION) {
            val data = admiralData(player)
            val teamId = data.getCurrentTeamID()
            val ships = player.level().getEntitiesOfClass<EntityShipBase?>(
                EntityShipBase::class.java,
                player.boundingBox.inflate(100.0),
                Predicate { ship: EntityShipBase? -> ship!!.isOwnedBy(player) && !ship.isInDeadPose })
            for (ship in ships) {
                if (ship.formationTeam == teamId) {
                    val slot = ship.formationSlot
                    ship.isPointerSelected = data.isSelected(teamId, slot)
                } else {
                    ship.isPointerSelected = false
                }
            }
        }
    }

    @JvmStatic
    fun giveInitialManualIfNeeded(player: ServerPlayer): Boolean {
        val data = admiralData(player)
        if (data.hasReceivedBook()) {
            return false
        }

        val bookStack = ItemStack(ModItems.DESK_ITEM_BOOK.get())
        if (!player.addItem(bookStack)) {
            player.drop(bookStack, false)
        }
        data.setHasReceivedBook(true)
        return true
    }

    @JvmStatic
    fun copyPersistentPlayerState(original: Player, clone: Player) {
        admiralData(clone).deserializeNBT(admiralData(original).serializeNBT())

        val originalCollected = collectedShips(original)
        val clonedCollected = collectedShips(clone)
        clonedCollected.clear()
        clonedCollected.addAll(originalCollected)
    }

    @JvmStatic
    fun registerCollectedShip(player: ServerPlayer, classId: Int): Boolean {
        if (classId < 0) {
            return false
        }

        val changed = collectedShips(player).add(classId)
        if (changed) {
            sendAdmiralState(player)
        }
        return changed
    }

    @JvmStatic
    fun hasCollectedShip(player: Player?, classId: Int): Boolean {
        return player != null && collectedShips(player).contains(classId)
    }

    @JvmStatic
    fun syncAdmiralState(player: ServerPlayer) {
        reconcileOwnedMarriedShipCount(player)
        sendAdmiralState(player)
    }

    fun sendAdmiralState(player: ServerPlayer) {
        PacketDistributor.sendToPlayer(player, admiralSyncPayload(player))
    }

    /**
     * Returns the number of married ships owned by [player].
     *
     * For server players this returns the cached count stored in [AdmiralData].
     * For other players a nearby scan is used as a fallback. Daily logic only
     * searches within [DAILY_MARRIED_SCAN_RADIUS]; use [getOwnedMarriedShipCount]
     * with [fullScan] set to true for administrative full-map recounts.
     */
    @JvmStatic
    fun getOwnedMarriedShipCount(player: Player?): Int {
        if (player == null) {
            return 0
        }
        if (player is ServerPlayer) {
            return reconcileOwnedMarriedShipCount(player)
        }

        val stored = admiralData(player).getMarriedShipCount()
        if (stored > 0) {
            return stored
        }

        return countNearbyMarriedShips(player, DAILY_MARRIED_SCAN_RADIUS)
    }

    /**
     * Returns the number of married ships owned by [player].
     *
     * @param fullScan if true, perform a full 256×128×256 scan and refresh the
     * stored married ship count. Intended for admin commands and special events.
     */
    @JvmStatic
    fun getOwnedMarriedShipCount(player: Player?, fullScan: Boolean): Int {
        if (player == null) {
            return 0
        }
        if (!fullScan) {
            return getOwnedMarriedShipCount(player)
        }
        return countAllMarriedShips(player)
    }

    private fun countNearbyMarriedShips(player: Player, radius: Double): Int {
        val ships = ShipLookupHelper.nearbyOwnedShips(
            player.level(), player, radius, player.level().gameTime, false
        )
        val count = ships.count { it.isStateMarried }
        if (count > 0) {
            admiralData(player).setMarriedShipCount(count)
        }
        return count
    }

    private fun countAllMarriedShips(player: Player): Int {
        val ownerId = player.uuid
        val search = player.boundingBox.inflate(256.0, 128.0, 256.0)
        val scanned = player.level().getEntitiesOfClass<EntityShipBase>(
            EntityShipBase::class.java, search,
            Predicate { ship: EntityShipBase ->
                ship.isAlive
                        && !ship.isRemoved && ship.isTame
                        && ship.isStateMarried
                        && ship.ownerUUID == ownerId
            }).size
        if (scanned > 0) {
            admiralData(player).setMarriedShipCount(scanned)
        }
        return scanned
    }

    fun isRingFlightActive(player: Player?): Boolean {
        if (player == null) {
            return false
        }
        return admiralData(player).isRingFlightActive
    }

    fun setRingFlightActive(player: Player?, active: Boolean) {
        if (player == null) {
            return
        }
        admiralData(player).isRingFlightActive = active
    }

    fun reconcileOwnedMarriedShipCount(player: ServerPlayer): Int {
        return admiralData(player).getMarriedShipCount()
    }

    @JvmStatic
    fun adjustOwnedMarriedShipCount(player: Player?, delta: Int) {
        if (player == null || delta == 0) {
            return
        }

        admiralData(player).addMarriedShipCount(delta)
        if (player is ServerPlayer) {
            sendAdmiralState(player)
        }
    }

    fun currentTeamId(player: Player?): Int {
        if (player == null) {
            return 0
        }
        return admiralData(player).getCurrentTeamID()
    }

    fun setCurrentTeamId(player: Player?, teamId: Int): Boolean {
        if (player == null || teamId < 0 || teamId >= AdmiralData.TEAM_COUNT) {
            return false
        }

        val data = admiralData(player)
        if (data.getCurrentTeamID() == teamId) {
            return false
        }
        data.setCurrentTeamID(teamId)
        return true
    }

    fun setCurrentTeamFormation(player: Player?, formationId: Int): Boolean {
        if (player == null || formationId < 0) {
            return false
        }

        val data = admiralData(player)
        if (data.getFormationID(data.getCurrentTeamID()) == formationId) {
            return false
        }
        data.setFormationID(data.getCurrentTeamID(), formationId)
        return true
    }

    fun setCurrentTeamSlotSelected(player: Player?, slotId: Int, selected: Boolean): Boolean {
        if (player == null || slotId < 0 || slotId >= AdmiralData.SLOT_COUNT) {
            return false
        }

        val data = admiralData(player)
        val teamId = data.getCurrentTeamID()
        if (data.isSelected(teamId, slotId) == selected) {
            return false
        }
        data.setSelected(teamId, slotId, selected)
        return true
    }

    fun setCurrentTeamName(player: Player?, name: String?): Boolean {
        if (player == null) {
            return false
        }
        val data = admiralData(player)
        val teamId = data.getCurrentTeamID()
        val currentName = data.getTeamName(teamId)
        val nextName: String?
        if (name == null || name.isBlank()) {
            nextName = "Team " + (teamId + 1)
        } else {
            nextName = name.trim()
        }
        if (currentName == nextName) {
            return false
        }
        data.setTeamName(teamId, name)
        return true
    }

    fun swapCurrentTeamSlots(player: Player?, slot1: Int, slot2: Int): Boolean {
        if (player == null || slot1 < 0 || slot1 >= AdmiralData.SLOT_COUNT || slot2 < 0 || slot2 >= AdmiralData.SLOT_COUNT || slot1 == slot2) {
            return false
        }

        admiralData(player).swapShips(currentTeamId(player), slot1, slot2)
        return true
    }

    fun assignShipToCurrentTeam(player: Player?, shipUuid: UUID?): Int {
        if (player == null || shipUuid == null) {
            return -1
        }
        return admiralData(player).assignShipToTeam(currentTeamId(player), shipUuid)
    }

    fun removeShipFromTeams(player: Player?, shipUuid: UUID?): Boolean {
        if (player == null || shipUuid == null) {
            return false
        }
        return admiralData(player).removeShip(shipUuid)
    }

    fun setCurrentTeamSlot(player: Player?, slotId: Int, shipUuid: UUID?): SlotAssignment? {
        if (player == null || slotId < 0 || slotId >= AdmiralData.SLOT_COUNT || shipUuid == null) {
            return null
        }

        val data = admiralData(player)
        val teamId = data.getCurrentTeamID()
        val replacedUuid = data.getShipUUID(teamId, slotId)
        if (shipUuid == replacedUuid && data.isSelected(teamId, slotId)) {
            return null
        }
        data.removeShip(shipUuid)
        if (replacedUuid != null && replacedUuid != shipUuid) {
            data.removeShip(replacedUuid)
        }
        data.setShipUUID(teamId, slotId, shipUuid)
        data.setSelected(teamId, slotId, true)
        return SlotAssignment(teamId, slotId, replacedUuid)
    }

    @JvmRecord
    data class SlotAssignment(val teamId: Int, val slotId: Int, val replacedUuid: UUID?)
}
