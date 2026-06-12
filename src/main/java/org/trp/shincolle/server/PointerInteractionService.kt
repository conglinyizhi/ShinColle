package org.trp.shincolle.server

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.inventory.MenuConstructor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.*
import org.trp.shincolle.attachment.AdmiralData
import org.trp.shincolle.block.entity.CraneBlockEntity
import org.trp.shincolle.block.entity.IWaypoint
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.item.PointerItem
import org.trp.shincolle.menu.FormationMenu
import org.trp.shincolle.utility.FormationHelper.applyShipGuardEntity
import org.trp.shincolle.utility.ShipLookupHelper
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate

object PointerInteractionService {
    const val POINTER_SEARCH_RADIUS: Double = 100.0
    private val POINTER_TARGET_DURATION_TICKS = 20L * 60L * 5L
    private const val POINTER_TARGET_SAME_DISTANCE_SQR = 0.25

    fun canAssignPointerEntityTarget(player: Player?, ship: EntityShipBase?, target: Entity?): Boolean {
        if (player == null || ship == null || target == null) {
            return false
        }
        if (target !is LivingEntity) {
            return false
        }
        if (target === player || target === ship || !target.isAlive) {
            return false
        }
        if (target.isSpectator()) {
            return false
        }
        if (target is Player && target.abilities.invulnerable) {
            return false
        }
        if (target is EntityShipBase && target.isOwnedBy(player)) {
            return false
        }
        if (sharesOwner(ship, target)) {
            return false
        }
        if (TeamDiplomacyService.isDiplomaticAlly(ship, target)) {
            return false
        }
        return !TargetProtectionService.isUnattackableTargetClass(ship, target)
    }

    @JvmStatic
    fun getPointerStack(player: Player?): ItemStack {
        if (player == null) {
            return ItemStack.EMPTY
        }

        val main = player.mainHandItem
        if (main.item is PointerItem) {
            return main
        }

        val off = player.offhandItem
        if (off.item is PointerItem) {
            return off
        }

        return ItemStack.EMPTY
    }

    fun handlePayloadAction(
        player: Player?, pointerStack: ItemStack, action: Int,
        targetEntityUuid: Optional<UUID>, targetPos: Optional<Vec3>
    ) {
        if (player == null || player.level().isClientSide) {
            return
        }
        if (pointerStack.item !is PointerItem) {
            return
        }

        val pointerItem = pointerStack.item as PointerItem
        if (action == 0) {
            cyclePointerMode(player, pointerItem, pointerStack)
        } else if (action == 1 || action == 2) {
            assignFormationPointerTarget(player, pointerItem, pointerStack, action, targetEntityUuid, targetPos)
        } else if (action == 3) {
            targetEntityUuid.ifPresent(Consumer { uuid: UUID ->
                PointerInteractionService.openOwnedShipMenu(
                    player,
                    uuid!!
                )
            })
        } else if (action == 4) {
            player.openMenu(
                SimpleMenuProvider(
                    MenuConstructor { id: Int, inv: Inventory, p: Player -> FormationMenu(id, inv) },
                    Component.translatable("gui.shincolle.formation.title")
                )
            )
        } else if (action == 5) {
            targetEntityUuid.ifPresent(Consumer { uuid: UUID ->
                FormationService.handlePointerRosterToggle(
                    player,
                    uuid
                )
            })
        }
    }


    /**
     * Handle pointer attack event. Returns true if the attack was consumed.
     */
    @JvmStatic
    fun handlePointerAttack(player: Player?, target: Entity?): Boolean {
        val pointerStack = getPointerStack(player)
        if (pointerStack.isEmpty()) return false
        if (player == null || player.level().isClientSide) return false
        handleAttackSelection(player, pointerStack, target)
        return true
    }


    fun handleAttackSelection(player: Player?, pointerStack: ItemStack, targetEntity: Entity?) {
        if (player == null || pointerStack.isEmpty() || player.level().isClientSide) {
            return
        }
        if (player.isShiftKeyDown()) {
            if (pointerStack.item is PointerItem) {
                cyclePointerMode(player, pointerStack.item as PointerItem, pointerStack)
            }
            return
        }

        val ship = asShipOrHostedShip(targetEntity)
        if (ship == null || !ship.isAlive || ship.isInDeadPose || !ship.isOwnedBy(player)) {
            return
        }

        val mode = if (pointerStack.item is PointerItem) (pointerStack.item as PointerItem).getMode(pointerStack) else PointerItem.MODE_SINGLE
        if (mode == PointerItem.MODE_GROUP || mode == PointerItem.MODE_FORMATION) {
            toggleGroupedSelection(player, ship, mode)
            return
        }

        clearOwnedPointerSelection(player, ship, POINTER_SEARCH_RADIUS)
        applyPointerModeSelectionState(player, PointerItem.MODE_SINGLE)
        ship.isPointerSelected = true
    }

    fun handleTargetCommand(player: Player?, pointerStack: ItemStack) {
        if (player == null || player.level().isClientSide || pointerStack.isEmpty()) {
            return
        }

        val level = player.level()
        val ownedShips = ShipLookupHelper.nearbyOwnedShips(
            level, player, POINTER_SEARCH_RADIUS, level.gameTime, true
        )
        var ships = ownedShips
            .filter { it.isPointerSelected && !it.isInDeadPose }
            .toMutableList()
        if (ships.isEmpty() || pointerStack.item !is PointerItem) {
            return
        }

        val pointerItem = pointerStack.item as PointerItem
        val mode: Int = pointerItem.getMode(pointerStack)
        if (mode == PointerItem.MODE_SINGLE && ships.size > 1) {
            ships.sortWith(compareBy { it.distanceToSqr(player) })
            val selected = ships[0]
            applyPointerModeSelectionState(player, PointerItem.MODE_SINGLE)
            ships.clear()
            ships.add(selected)
        } else if (mode == PointerItem.MODE_FORMATION) {
            val data = PlayerStateService.admiralData(player)
            val teamId = data.getCurrentTeamID()
            ships = ownedShips
                .filter { it.formationTeam == teamId && !it.isInDeadPose }
                .toMutableList()
        }

        val hitRes = getLookTargetResult(player)
        if (hitRes != null) {
            handleEntityTargetCommand(player, ships, hitRes.entity)
            return
        }

        handleBlockTargetCommand(player, ships)
    }

    @JvmStatic
    fun getLookTargetResult(player: Player?): EntityHitResult? {
        if (player == null) {
            return null
        }
        val reach = POINTER_SEARCH_RADIUS
        val eyePos = player.eyePosition
        val look = player.getViewVector(1.0f)
        val end = eyePos.add(look.x * reach, look.y * reach, look.z * reach)
        val searchBox = player.boundingBox.expandTowards(look.scale(reach)).inflate(1.0)
        return ProjectileUtil.getEntityHitResult(
            player.level(), player, eyePos, end, searchBox,
            Predicate { entity -> !entity.isSpectator() && entity.isPickable && entity !== player })
    }

    private fun cyclePointerMode(player: Player, pointerItem: PointerItem, pointerStack: ItemStack?) {
        if (player.level().isClientSide) {
            return
        }

        val nextMode = pointerItem.cycleMode(pointerStack!!)
        applyPointerModeSelectionState(player, nextMode)
        player.displayClientMessage(Component.translatable(PointerItem.getModeTranslationKey(nextMode)), true)
    }

    private fun applyPointerModeSelectionState(player: Player?, nextMode: Int) {
        if (player == null) {
            return
        }
        if (player.level().isClientSide) {
            return
        }

        val level = player.level()
        val ownedShips = ShipLookupHelper.nearbyOwnedShips(
            level, player, POINTER_SEARCH_RADIUS, level.gameTime, true
        )

        if (nextMode == PointerItem.MODE_SINGLE) {
            val ships = ownedShips
                .filter { it.isPointerSelected && !it.isInDeadPose }
                .toMutableList()
            if (ships.size > 1) {
                ships.sortWith(compareBy { it.distanceToSqr(player) })
                val keep = ships[0]
                clearOwnedPointerSelection(player, keep, POINTER_SEARCH_RADIUS)
                keep.isPointerSelected = true
            }
        } else if (nextMode == PointerItem.MODE_FORMATION) {
            val data = PlayerStateService.admiralData(player)
            val teamId = data.getCurrentTeamID()
            for (ship in ownedShips) {
                if (!ship.isInDeadPose) {
                    ship.isPointerSelected = ship.formationTeam == teamId
                }
            }
        }
    }

    private fun clearOwnedPointerSelection(player: Player?, keepSelected: EntityShipBase?, radius: Double) {
        if (player == null) {
            return
        }
        val level = player.level()
        val ships = ShipLookupHelper.nearbyOwnedShips(
            level, player, radius, level.gameTime, true
        ).filter { it.isPointerSelected && !it.isInDeadPose }
        for (ship in ships) {
            if (ship === keepSelected) {
                continue
            }
            ship.isPointerSelected = false
            ship.clearPointerTarget()
            ship.clearPointerTargetEntity()
        }
    }

    private fun assignFormationPointerTarget(
        player: Player, pointerItem: PointerItem, pointerStack: ItemStack,
        action: Int, targetEntityUuid: Optional<UUID>, targetPos: Optional<Vec3>
    ) {
        if (pointerItem.getMode(pointerStack) != PointerItem.MODE_FORMATION
            || player.level() !is ServerLevel
        ) {
            return
        }

        val data = PlayerStateService.admiralData(player)
        val teamId = data.getCurrentTeamID()
        val serverLevel = player.level() as ServerLevel
        for (i in 0..<AdmiralData.SLOT_COUNT) {
            if (!data.isSelected(teamId, i)) {
                continue
            }

            val shipUuid = data.getShipUUID(teamId, i)
            if (shipUuid == null) {
                continue
            }

            val entity: Entity? = serverLevel.getEntity(shipUuid)
            if ((entity !is EntityShipBase) || !entity.isOwnedBy(player) || !entity.isAlive || entity.isRemoved) {
                continue
            }

            if (action == 1 && targetEntityUuid.isPresent()) {
                val target: Entity? = serverLevel.getEntity(targetEntityUuid.get())
                if (target == null || !target.isAlive || target.isRemoved) {
                    continue
                }
                if (canAssignPointerEntityTarget(player, entity, target)) {
                    entity.setPointerTargetEntity(target, POINTER_TARGET_DURATION_TICKS)
                }
            } else if (action == 2 && targetPos.isPresent()) {
                entity.setPointerTarget(targetPos.get(), POINTER_TARGET_DURATION_TICKS)
            }
        }
    }

    private fun openOwnedShipMenu(player: Player?, shipUuid: UUID) {
        if (player == null) {
            return
        }
        if (player.level() !is ServerLevel) {
            return
        }

        val serverLevel = player.level() as ServerLevel
        val entity: Entity? = serverLevel.getEntity(shipUuid)
        if (entity is EntityShipBase
            && entity.isOwnedBy(player)
            && entity.isAlive
            && !entity.isRemoved
        ) {
            entity.openShipMenu(player)
        }
    }

    private fun toggleGroupedSelection(player: Player, ship: EntityShipBase, mode: Int) {
        val data = PlayerStateService.admiralData(player)
        val teamId = data.getCurrentTeamID()
        val existingTeam = data.findShipTeam(ship.uuid)
        val existingSlot = if (existingTeam >= 0) data.findShipSlot(existingTeam, ship.uuid) else -1
        var shouldSync = false

        if (existingTeam != -1) {
            if (existingTeam == teamId) {
                if (mode == PointerItem.MODE_FORMATION) {
                    if (PlayerStateService.removeShipFromTeams(player, ship.uuid)) {
                        FormationService.clearFormationState(ship)
                        shouldSync = true
                    }
                } else {
                    val nextState = !data.isSelected(teamId, existingSlot)
                    if (PlayerStateService.setCurrentTeamSlotSelected(player, existingSlot, nextState)) {
                        ship.isPointerSelected = nextState
                        shouldSync = true
                    }
                }
            } else if (mode == PointerItem.MODE_FORMATION) {
                val assignedSlot = PlayerStateService.assignShipToCurrentTeam(player, ship.uuid)
                if (assignedSlot != -1) {
                    FormationService.applyFormationState(ship, teamId, assignedSlot, true)
                    shouldSync = true
                } else {
                    player.displayClientMessage(Component.translatable("chat.shincolle.formation.teamfull"), false)
                }
            } else {
                ship.togglePointerSelected()
            }
            if (shouldSync) {
                sendAdmiralStateIfServerPlayer(player)
            }
        } else if (mode == PointerItem.MODE_FORMATION) {
            val assignedSlot = PlayerStateService.assignShipToCurrentTeam(player, ship.uuid)
            if (assignedSlot != -1) {
                FormationService.applyFormationState(ship, teamId, assignedSlot, true)
                sendAdmiralStateIfServerPlayer(player)
            } else {
                player.displayClientMessage(Component.translatable("chat.shincolle.formation.teamfull"), false)
            }
        } else {
            ship.togglePointerSelected()
        }

        if (!ship.isPointerSelected) {
            ship.clearPointerTarget()
            ship.clearPointerTargetEntity()
        }
    }

    private fun handleEntityTargetCommand(player: Player, ships: MutableList<EntityShipBase>, target: Entity?) {
        if (player.isShiftKeyDown()) {
            var guardTarget = target
            if (guardTarget is EntityMountBase && guardTarget.host != null) {
                guardTarget = guardTarget.host
            }
            if (guardTarget is EntityShipBase && guardTarget.isOwnedBy(player)) {
                for (ship in ships) {
                    applyShipGuardEntity(ship, guardTarget)
                    ship.clearPointerTarget()
                    ship.clearPointerTargetEntity()
                }
            }
            return
        }

        if (target === player || target is EntityShipBase && target.isOwnedBy(player)) {
            return
        }
        for (ship in ships) {
            if (!canAssignPointerEntityTarget(player, ship, target)) {
                continue
            }
            if (ship.hasPointerTargetEntity() && ship.pointerTargetEntity === target) {
                ship.clearPointerTargetEntity()
                ship.clearPointerTarget()
                continue
            }
            ship.setPointerTargetEntity(target, POINTER_TARGET_DURATION_TICKS)
        }
    }

    private fun handleBlockTargetCommand(player: Player, ships: MutableList<EntityShipBase>) {
        var target = getLookTarget(player)
        if (target == null) {
            return
        }

        val blockHit = getLookBlockResult(player)
        var guardPos: BlockPos? = null
        val be = player.level().getBlockEntity(blockHit.blockPos)
        if (be is IWaypoint) {
            val resolved = resolveWaypointTarget(player.level(), blockHit.blockPos, be)
            guardPos = resolved
            target = Vec3.atBottomCenterOf(resolved)
        }

        for (ship in ships) {
            if (ship.hasPointerTarget() && isSamePointerTarget(ship.pointerTarget, target)) {
                ship.clearPointerTarget()
                continue
            }
            ship.setPointerTarget(target, POINTER_TARGET_DURATION_TICKS)
            if (guardPos != null) {
                ship.setGuardBlockTarget(guardPos)
                ship.setStateFlag(EntityShipBase.STATE_FLAG_DISABLE_GUARD_POS, false)
            }
        }
    }

    private fun asShipOrHostedShip(targetEntity: Entity?): EntityShipBase? {
        if (targetEntity is EntityShipBase) {
            return targetEntity
        }
        if (targetEntity is EntityMountBase) {
            return targetEntity.host
        }
        return null
    }

    private fun sharesOwner(ship: EntityShipBase, target: Entity?): Boolean {
        val ownerId = ship.ownerUUID
        if (ownerId == null) {
            return false
        }
        if (target is Player) {
            return ownerId == target.uuid
        }
        if (target is TamableAnimal) {
            return ownerId == target.ownerUUID
        }
        if (target is EntityMountBase) {
            val host = target.host
            if (host != null) {
                return host.ownerUUID == ownerId
            }
            return target.hostUUID == ownerId
        }
        return false
    }

    fun getTargetOwnerUUID(target: Entity?): UUID? {
        if (target is Player) {
            return target.uuid
        }
        if (target is EntityShipBase) {
            return target.ownerUUID
        }
        if (target is TamableAnimal) {
            return target.ownerUUID
        }
        if (target is EntityMountBase) {
            val host = target.host
            if (host != null) {
                return host.ownerUUID
            }
            return target.hostUUID
        }
        if (target is Enemy) {
            return null
        }
        return null
    }

    private fun getLookTarget(player: Player): Vec3? {
        val hit = getLookBlockResult(player)
        if (hit.type != HitResult.Type.BLOCK) {
            return null
        }

        val pos = hit.blockPos
        return Vec3.atBottomCenterOf(pos).add(0.0, 1.0, 0.0)
    }

    private fun getLookBlockResult(player: Player): BlockHitResult {
        val reach = POINTER_SEARCH_RADIUS
        val eyePos = player.eyePosition
        val look = player.getViewVector(1.0f)
        val end = eyePos.add(look.x * reach, look.y * reach, look.z * reach)
        return player.level().clip(ClipContext(eyePos, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, player))
    }

    private fun resolveWaypointTarget(level: Level, waypointPos: BlockPos, waypoint: IWaypoint): BlockPos {
        val next = waypoint.nextPos
        if (next != null && isCraneTarget(level, next)) {
            return next
        }
        val chest = waypoint.chestPos
        if (chest != null && isCraneTarget(level, chest)) {
            return chest
        }
        return waypointPos
    }

    private fun isCraneTarget(level: Level, pos: BlockPos?): Boolean {
        if (pos == null || pos == BlockPos.ZERO) {
            return false
        }
        return level.getBlockEntity(pos) is CraneBlockEntity
    }

    private fun isSamePointerTarget(current: Vec3?, next: Vec3?): Boolean {
        if (current == null || next == null) {
            return false
        }
        return current.distanceToSqr(next) <= POINTER_TARGET_SAME_DISTANCE_SQR
    }

    private fun sendAdmiralStateIfServerPlayer(player: Player?) {
        if (player is ServerPlayer) {
            PlayerStateService.sendAdmiralState(player)
        }
    }

    /**
     * Handle pointer left-click on a block: cancels the event if shift is held.
     */
    @JvmStatic
    fun handleLeftClickBlock(player: Player?, event: LeftClickBlock) {
        if (player == null) return
        val pointerStack = getPointerStack(player)
        if (pointerStack.isEmpty()) return
        if (player.level().isClientSide) return
        if (player.isShiftKeyDown()) {
            event.setCanceled(true)
        }
    }

    /**
     * Handle pointer right-click on a block. Returns true if the interaction was consumed.
     */
    @JvmStatic
    fun handleRightClickBlock(player: Player?, event: RightClickBlock): Boolean {
        if (player == null) return false
        val pointerStack = getPointerStack(player)
        if (pointerStack.isEmpty() || player.isShiftKeyDown()) return false
        handleTargetCommand(player, pointerStack)
        event.setCanceled(true)
        return true
    }

    /**
     * Handle pointer right-click on an item. Returns true if the interaction was consumed.
     */
    @JvmStatic
    fun handleRightClickItem(player: Player?, event: RightClickItem): Boolean {
        if (player == null) return false
        val pointerStack = getPointerStack(player)
        if (pointerStack.isEmpty() || player.isShiftKeyDown()) return false
        handleTargetCommand(player, pointerStack)
        event.setCanceled(true)
        return true
    }
}
