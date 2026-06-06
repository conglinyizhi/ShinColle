package org.trp.shincolle.server

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import org.trp.shincolle.attachment.AdmiralData
import org.trp.shincolle.entity.base.EntityShipBase
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate

object FormationService {
    private const val NEARBY_SHIP_SYNC_RADIUS = 100.0
    private const val NEARBY_SELECTED_IMPORT_RADIUS = 64.0

    fun clearFormationState(ship: EntityShipBase?) {
        if (ship == null) {
            return
        }
        ship.formationTeam = -1
        ship.formationSlot = -1
        ship.isPointerSelected = false
        ship.clearPointerTarget()
        ship.clearPointerTargetEntity()
    }

    fun applyFormationState(ship: EntityShipBase?, teamId: Int, slotId: Int, selected: Boolean) {
        if (ship == null) {
            return
        }
        ship.formationTeam = teamId
        ship.formationSlot = slotId
        ship.isPointerSelected = selected
    }

    fun syncNearbyShipsForCurrentTeam(player: Player?, clearDeselectedTargets: Boolean) {
        if (player == null) {
            return
        }
        val data = PlayerStateService.admiralData(player)
        val teamId = data.getCurrentTeamID()
        val ships = player.level().getEntitiesOfClass<EntityShipBase?>(
            EntityShipBase::class.java,
            player.getBoundingBox().inflate(NEARBY_SHIP_SYNC_RADIUS),
            Predicate { ship: EntityShipBase? -> ship!!.isOwnedBy(player) && !ship.isInDeadPose })

        for (ship in ships) {
            if (ship.formationTeam == teamId) {
                val slot = ship.formationSlot
                ship.isPointerSelected = data.isSelected(teamId, slot)
            } else {
                ship.isPointerSelected = false
                if (clearDeselectedTargets) {
                    ship.clearPointerTarget()
                    ship.clearPointerTargetEntity()
                }
            }
        }
    }

    fun handleFormationAction(
        player: Player?, action: Int, param1: Int, param2: Int,
        paramString: String?, paramUuid: Optional<UUID?>
    ) {
        if (player == null) {
            return
        }
        val data = PlayerStateService.admiralData(player)
        var shouldSync = false

        when (action) {
            0 -> {
                if (PlayerStateService.setCurrentTeamId(player, param1)) {
                    syncNearbyShipsForCurrentTeam(player, true)
                    shouldSync = true
                }
            }

            1 -> shouldSync = PlayerStateService.setCurrentTeamFormation(player, param1)
            2 -> shouldSync = setCurrentTeamSlotSelected(player, data, param1, param2 != 0)
            3 -> shouldSync = removeCurrentTeamSlot(player, data, param1)
            4 -> shouldSync = PlayerStateService.setCurrentTeamName(player, paramString)
            5 -> shouldSync =
                paramUuid.map<Boolean?>(Function { uuid: UUID? -> replaceCurrentTeamSlot(player, param1, uuid) })
                    .orElse(false)

            6 -> shouldSync = swapCurrentTeamSlots(player, data, param1, param2)
            7 -> shouldSync = importNearbySelectedShips(player, data)
            8 -> openCurrentTeamSlotShipMenu(player, data, param1)
            else -> {}
        }

        if (shouldSync && player is ServerPlayer) {
            PlayerStateService.sendAdmiralState(player)
        }
    }

    fun handlePointerRosterToggle(player: Player?, targetUuid: UUID?) {
        if (player == null || targetUuid == null) {
            return
        }
        val data = PlayerStateService.admiralData(player)
        val teamId = data.getCurrentTeamID()
        val slot = data.findShipSlot(teamId, targetUuid)
        var shouldSync = false

        if (slot != -1) {
            val nextState = !data.isSelected(teamId, slot)
            if (PlayerStateService.setCurrentTeamSlotSelected(player, slot, nextState)) {
                withServerShip(
                    player,
                    targetUuid,
                    Consumer { ship: EntityShipBase? -> ship!!.isPointerSelected = nextState })
                shouldSync = true
            }
        } else {
            val assignedSlot = PlayerStateService.assignShipToCurrentTeam(player, targetUuid)
            if (assignedSlot == -1) {
                player.displayClientMessage(Component.translatable("chat.shincolle.formation.teamfull"), false)
            } else {
                withServerShip(
                    player,
                    targetUuid,
                    Consumer { ship: EntityShipBase? -> applyFormationState(ship, teamId, assignedSlot, true) })
                shouldSync = true
            }
        }

        if (shouldSync && player is ServerPlayer) {
            PlayerStateService.sendAdmiralState(player)
        }
    }

    private fun setCurrentTeamSlotSelected(
        player: Player?,
        data: AdmiralData,
        slotId: Int,
        selected: Boolean
    ): Boolean {
        if (!PlayerStateService.setCurrentTeamSlotSelected(player, slotId, selected)) {
            return false
        }

        val shipUuid = data.getShipUUID(data.getCurrentTeamID(), slotId)
        if (shipUuid != null) {
            FormationService.withServerShip(
                player!!,
                shipUuid,
                Consumer { ship: EntityShipBase? -> ship!!.isPointerSelected = selected })
        }
        return true
    }

    private fun openCurrentTeamSlotShipMenu(player: Player, data: AdmiralData, slotId: Int) {
        if (slotId < 0 || slotId >= AdmiralData.SLOT_COUNT) {
            return
        }

        val shipUuid = data.getShipUUID(data.getCurrentTeamID(), slotId)
        if (shipUuid != null) {
            withServerShip(player, shipUuid, Consumer { ship: EntityShipBase? -> ship!!.openShipMenu(player) })
        }
    }

    private fun removeCurrentTeamSlot(player: Player, data: AdmiralData, slotId: Int): Boolean {
        if (slotId < 0 || slotId >= AdmiralData.SLOT_COUNT) {
            return false
        }

        val shipUuid = data.getShipUUID(data.getCurrentTeamID(), slotId)
        if (shipUuid == null) {
            return false
        }
        withServerShip(player, shipUuid, Consumer { obj: EntityShipBase? -> FormationService.clearFormationState(obj) })
        return PlayerStateService.removeShipFromTeams(player, shipUuid)
    }

    private fun replaceCurrentTeamSlot(player: Player?, slotId: Int, shipUuid: UUID?): Boolean {
        val assignment = PlayerStateService.setCurrentTeamSlot(player, slotId, shipUuid)
        if (assignment == null) {
            return false
        }

        val replacedUuid = assignment.replacedUuid
        if (replacedUuid != null && replacedUuid != shipUuid) {
            FormationService.withServerShip(
                player!!,
                replacedUuid,
                Consumer { obj: EntityShipBase? -> FormationService.clearFormationState(obj) })
        }
        FormationService.withServerShip(
            player!!,
            shipUuid,
            Consumer { ship: EntityShipBase? -> applyFormationState(ship, assignment.teamId, assignment.slotId, true) })
        return true
    }

    private fun swapCurrentTeamSlots(player: Player?, data: AdmiralData, slot1: Int, slot2: Int): Boolean {
        if (!PlayerStateService.swapCurrentTeamSlots(player, slot1, slot2)) {
            return false
        }

        val teamId = data.getCurrentTeamID()
        val uuid1 = data.getShipUUID(teamId, slot1)
        if (uuid1 != null) {
            FormationService.withServerShip(player!!, uuid1, Consumer { ship: EntityShipBase? ->
                ship!!.formationSlot = slot1
                ship.isPointerSelected = data.isSelected(teamId, slot1)
            })
        }

        val uuid2 = data.getShipUUID(teamId, slot2)
        if (uuid2 != null) {
            FormationService.withServerShip(player!!, uuid2, Consumer { ship: EntityShipBase? ->
                ship!!.formationSlot = slot2
                ship.isPointerSelected = data.isSelected(teamId, slot2)
            })
        }
        return true
    }

    private fun importNearbySelectedShips(player: Player, data: AdmiralData): Boolean {
        if (player.level() !is ServerLevel) {
            return false
        }

        val teamId = data.getCurrentTeamID()
        var teamFilledDuringSync = false
        var changed = false
        val serverLevel = player.level() as ServerLevel
        val nearbySelected: MutableList<EntityShipBase> = serverLevel.getEntitiesOfClass<EntityShipBase?>(
            EntityShipBase::class.java,
            player.getBoundingBox().inflate(NEARBY_SELECTED_IMPORT_RADIUS),
            Predicate { ship: EntityShipBase? -> ship!!.isPointerSelected && player.getUUID() == ship.ownerUUID })
        val nearbyOwned: MutableList<EntityShipBase> = serverLevel.getEntitiesOfClass<EntityShipBase?>(
            EntityShipBase::class.java,
            player.getBoundingBox().inflate(NEARBY_SHIP_SYNC_RADIUS),
            Predicate { ship: EntityShipBase? -> player.getUUID() == ship!!.ownerUUID && !ship.isInDeadPose })

        for (ship in nearbySelected) {
            if (!data.isShipInTeam(teamId, ship.getUUID())) {
                if (PlayerStateService.assignShipToCurrentTeam(player, ship.getUUID()) == -1) {
                    teamFilledDuringSync = true
                } else {
                    changed = true
                }
            }
        }

        for (ship in nearbyOwned) {
            if (ship.formationTeam == teamId && !data.isShipInTeam(teamId, ship.getUUID())) {
                clearFormationState(ship)
                changed = true
            }
        }

        for (i in 0..<AdmiralData.SLOT_COUNT) {
            val uuid = data.getShipUUID(teamId, i)
            if (uuid != null) {
                val slotId = i
                withServerShip(
                    player,
                    uuid,
                    Consumer { ship: EntityShipBase? ->
                        applyFormationState(
                            ship,
                            teamId,
                            slotId,
                            data.isSelected(teamId, slotId)
                        )
                    })
            }
        }

        if (teamFilledDuringSync) {
            player.displayClientMessage(Component.translatable("chat.shincolle.formation.teamfull"), false)
        }
        return changed
    }

    private fun withServerShip(player: Player, shipUuid: UUID?, action: Consumer<EntityShipBase?>) {
        if (player.level() !is ServerLevel || shipUuid == null) {
            return
        }

        val serverLevel = player.level() as ServerLevel
        val entity: Entity? = serverLevel.getEntity(shipUuid)
        if (entity is EntityShipBase && entity.isOwnedBy(player) && entity.isAlive && !entity.isRemoved) {
            action.accept(entity)
        }
    }
}
