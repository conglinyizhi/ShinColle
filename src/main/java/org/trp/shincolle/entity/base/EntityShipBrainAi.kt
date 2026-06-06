package org.trp.shincolle.entity.base

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.mojang.datafixers.util.Pair
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.Brain
import net.minecraft.world.entity.ai.behavior.Behavior
import net.minecraft.world.entity.ai.behavior.BehaviorUtils
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.memory.MemoryStatus
import net.minecraft.world.entity.ai.sensing.Sensor
import net.minecraft.world.entity.ai.sensing.SensorType
import net.minecraft.world.entity.ai.util.DefaultRandomPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.schedule.Activity
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.Shincolle.Companion.debugLog
import org.trp.shincolle.Shincolle.Companion.diagnosticLog
import org.trp.shincolle.entity.base.ShipBrainActivityResolver.FollowState
import org.trp.shincolle.entity.base.ShipBrainMemory.PointerTargetMemory
import org.trp.shincolle.entity.base.ShipBrainMemory.GuardTargetMemory
import org.trp.shincolle.entity.base.ShipBrainMemory.FollowStateMemory
import org.trp.shincolle.entity.base.ShipBrainMemory.RecoveryStateMemory
import org.trp.shincolle.entity.base.ShipBrainMemory.PassiveCombatStateMemory
import org.trp.shincolle.entity.base.ShipPointerEntityDecisionResolver.AttackState
import org.trp.shincolle.init.ModMemoryModules
import org.trp.shincolle.server.PlayerStateService.admiralData
import org.trp.shincolle.utility.FormationHelper.getFormationDirection
import org.trp.shincolle.utility.FormationHelper.getFormationPos
import java.util.*
import java.util.Map
import java.util.Set
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

internal object EntityShipBrainAi {
    val MEMORY_TYPES: MutableList<MemoryModuleType<*>> = ImmutableList.of<MemoryModuleType<*>>(
        MemoryModuleType.WALK_TARGET,
        MemoryModuleType.LOOK_TARGET,
        MemoryModuleType.ATTACK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
        ModMemoryModules.SHIP_POINTER_TARGET.get(),
        ModMemoryModules.SHIP_GUARD_TARGET.get(),
        ModMemoryModules.SHIP_FOLLOW_STATE.get(),
        ModMemoryModules.SHIP_PASSIVE_COMBAT_STATE.get(),
        ModMemoryModules.SHIP_POINTER_RECOVERY.get()!!,
        ModMemoryModules.SHIP_GUARD_RECOVERY.get()!!,
        ModMemoryModules.SHIP_FOLLOW_RECOVERY.get()!!,
        ModMemoryModules.SHIP_COMBAT_RECOVERY.get()!!
    )
    val SENSOR_TYPES: MutableList<SensorType<out Sensor<in EntityShipBase>>?> =
        ImmutableList.of<SensorType<out Sensor<in EntityShipBase>>?>()
    private val MODE_ACTIVITIES: MutableMap<ShipBrainActivityResolver.Mode, Activity> =
        EnumMap<ShipBrainActivityResolver.Mode, Activity>(
            Map.of<ShipBrainActivityResolver.Mode, Activity>(
                ShipBrainActivityResolver.Mode.COMMAND, Activity.WORK,
                ShipBrainActivityResolver.Mode.GUARD, Activity.MEET,
                ShipBrainActivityResolver.Mode.FOLLOW, Activity.PLAY,
                ShipBrainActivityResolver.Mode.COMBAT, Activity.FIGHT,
                ShipBrainActivityResolver.Mode.IDLE, Activity.IDLE
            )
        )

    fun makeBrain(ship: EntityShipBase, brain: Brain<EntityShipBase>): Brain<*> {
        initCoreActivity(brain)
        initCommandActivity(brain)
        initGuardActivity(brain)
        initFollowActivity(brain)
        initCombatActivity(brain)
        initIdleActivity(brain)
        brain.setCoreActivities(Set.of<Activity>(Activity.CORE))
        brain.setDefaultActivity(Activity.IDLE)
        brain.useDefaultActivity()
        return brain
    }

    fun tick(level: ServerLevel, ship: EntityShipBase) {
        val brain = ship.getBrain() as Brain<EntityShipBase>
        syncShipStateMemory(ship, brain)
        logServerBrainTickIfNeeded(ship, brain)
        syncAttackTargetMemory(ship, brain)
        brain.tick(level, ship)
        brain.setActiveActivityToFirstValid(
            resolveActiveActivities(
                activityState(ship, brain), brain.isActive(activityFor(ShipBrainActivityResolver.Mode.FOLLOW))
            )
        )
    }

    private fun resolveActiveActivities(
        state: ShipBrainActivityResolver.State,
        following: Boolean
    ): ImmutableList<Activity> {
        val activities = ImmutableList.builder<Activity>()
        for (mode in ShipBrainActivityResolver.resolveActiveModes(state, following)) {
            activities.add(activityFor(mode!!))
        }
        return activities.build()
    }

    private fun activityFor(mode: ShipBrainActivityResolver.Mode): Activity {
        return MODE_ACTIVITIES.get(mode)!!
    }

    private fun activityState(
        ship: EntityShipBase,
        brain: Brain<EntityShipBase> = typedBrain(ship)
    ): ShipBrainActivityResolver.State {
        val pointerMemory = pointerTargetMemory(brain)
        val guardMemory = guardTargetMemory(brain)
        val followMemory = followStateMemory(brain)
        return ShipBrainActivityResolver.State(
            canMove(ship),
            pointerMemory.hasAnyTarget(),
            ship.target != null,
            guardMemory.canGuard,
            followMemory.shouldFollow,
            followMemory.ownerPresent,
            followMemory.ownerHasCombatRation,
            followMemory.ownerDistanceSq,
            followMemory.followMinConfig,
            followMemory.followMaxConfig
        )
    }

    private fun typedBrain(ship: EntityShipBase): Brain<EntityShipBase> {
        val brain = ship.getBrain() as Brain<EntityShipBase>
        return brain
    }

    private fun syncShipStateMemory(ship: EntityShipBase, brain: Brain<EntityShipBase>) {
        brain.setMemory<PointerTargetMemory?>(
            ModMemoryModules.SHIP_POINTER_TARGET.get(),
            ShipBrainMemory.pointerTarget(ship)
        )
        brain.setMemory<GuardTargetMemory?>(ModMemoryModules.SHIP_GUARD_TARGET.get(), ShipBrainMemory.guardTarget(ship))
        brain.setMemory<FollowStateMemory?>(ModMemoryModules.SHIP_FOLLOW_STATE.get(), ShipBrainMemory.followState(ship))
    }

    private fun pointerTargetMemory(ship: EntityShipBase): PointerTargetMemory {
        return pointerTargetMemory(typedBrain(ship))
    }

    private fun pointerTargetMemory(brain: Brain<EntityShipBase>): PointerTargetMemory {
        return brain.getMemory<PointerTargetMemory?>(ModMemoryModules.SHIP_POINTER_TARGET.get())
            .orElse(
                PointerTargetMemory(
                    false, null, null, 0L, false, null, false, null, 0L,
                    -1.0, 0.0, 0.0, false, false, false, ShipAiNumbers.POINTER_ENTITY_ATTACK_RANGE_SQR, false, false
                )
            )
    }

    private fun guardTargetMemory(ship: EntityShipBase): GuardTargetMemory {
        return guardTargetMemory(typedBrain(ship))
    }

    private fun guardTargetMemory(brain: Brain<EntityShipBase>): GuardTargetMemory {
        return brain.getMemory<GuardTargetMemory?>(ModMemoryModules.SHIP_GUARD_TARGET.get())
            .orElse(GuardTargetMemory(ShipGuardTarget.NONE, false, false, null, false, null, null, 0))
    }

    private fun followStateMemory(ship: EntityShipBase): FollowStateMemory {
        return followStateMemory(typedBrain(ship))
    }

    private fun followStateMemory(brain: Brain<EntityShipBase>): FollowStateMemory {
        return brain.getMemory<FollowStateMemory?>(ModMemoryModules.SHIP_FOLLOW_STATE.get())
            .orElse(FollowStateMemory(false, "memoryMissing", false, null, null, 0.0, 0, false, -1.0, 0, 0))
    }

    private fun passiveCombatStateMemory(ship: EntityShipBase): PassiveCombatStateMemory {
        return passiveCombatStateMemory(typedBrain(ship))
    }

    private fun passiveCombatStateMemory(brain: Brain<EntityShipBase>): PassiveCombatStateMemory {
        return brain.getMemory<PassiveCombatStateMemory?>(ModMemoryModules.SHIP_PASSIVE_COMBAT_STATE.get())
            .orElse(ShipBrainMemory.noPassiveCombatState())
    }

    private fun syncAttackTargetMemory(ship: EntityShipBase, brain: Brain<EntityShipBase>) {
        val target = ship.target
        if (target != null && target.isAlive) {
            brain.setMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET, target)
        } else {
            brain.eraseMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET)
        }
    }


    private fun logServerBrainTickIfNeeded(ship: EntityShipBase, brain: Brain<EntityShipBase>) {
        if (ship.tickCount % 40 != 0) {
            return
        }
        val pointerMemory = pointerTargetMemory(brain)
        val guardMemory = guardTargetMemory(brain)
        val followMemory = followStateMemory(brain)
        if (ship.ownerUUID == null && ship.target == null && !pointerMemory.hasAnyTarget() && guardMemory.target?.isActive != true) {
            return
        }
        diagnosticLog(
            "[SCBrainDiag] serverTick ship={} activity={} ownerUuid={} ownerPresent={} tame={} deadPose={} noFuel={} shouldFollow={} reason={} distSq={} target={} pointer={} guard={}",
            ship.uuid,
            describeDesiredActivity(ship, brain),
            ship.ownerUUID,
            followMemory.ownerPresent,
            ship.isTame,
            ship.isInDeadPose,
            ship.isNoFuel,
            followMemory.shouldFollow,
            followMemory.blockReason,
            followMemory.ownerDistanceSq,
            ship.target != null,
            pointerMemory.hasAnyTarget(),
            guardMemory.target?.isActive == true
        )
    }

    private fun describeDesiredActivity(
        ship: EntityShipBase,
        brain: Brain<EntityShipBase> = typedBrain(ship)
    ): String {
        return ShipBrainActivityResolver.describeDesiredActivity(activityState(ship, brain))
    }

    private fun initCoreActivity(brain: Brain<EntityShipBase>) {
        brain.addActivity(
            Activity.CORE, ImmutableList.of<Pair<Int?, out Behavior<EntityShipBase>?>?>(
                Pair.of<Int?, ShipBrainDiagnosticBehavior?>(
                    ShipAiNumbers.CORE_DIAGNOSTIC_PRIORITY,
                    ShipBrainDiagnosticBehavior()
                ),
                Pair.of<Int?, ShipPassiveCombatTargetingBehavior?>(
                    ShipAiNumbers.CORE_PASSIVE_TARGETING_PRIORITY,
                    ShipPassiveCombatTargetingBehavior()
                ),
                Pair.of<Int?, ShipLookAtPlayerBehavior?>(
                    ShipAiNumbers.LOOK_AT_PLAYER_PRIORITY,
                    ShipLookAtPlayerBehavior()
                ),
                Pair.of<Int?, ShipRandomLookAroundBehavior?>(
                    ShipAiNumbers.RANDOM_LOOK_PRIORITY,
                    ShipRandomLookAroundBehavior()
                )
            )
        )
    }

    private fun initCommandActivity(brain: Brain<EntityShipBase>) {
        brain.addActivity(
            activityFor(ShipBrainActivityResolver.Mode.COMMAND),
            ImmutableList.of<Pair<Int, ShipPointerMoveBehavior>?>(
                Pair.of<Int?, ShipPointerMoveBehavior?>(
                    ShipAiNumbers.COMMAND_POINTER_PRIORITY,
                    ShipPointerMoveBehavior()
                )
            )
        )
    }

    private fun initGuardActivity(brain: Brain<EntityShipBase>) {
        brain.addActivity(
            activityFor(ShipBrainActivityResolver.Mode.GUARD), ImmutableList.of<Pair<Int, ShipGuardMoveBehavior>?>(
                Pair.of<Int?, ShipGuardMoveBehavior?>(ShipAiNumbers.GUARD_MOVE_PRIORITY, ShipGuardMoveBehavior())
            )
        )
    }

    private fun initFollowActivity(brain: Brain<EntityShipBase>) {
        brain.addActivity(
            activityFor(ShipBrainActivityResolver.Mode.FOLLOW), ImmutableList.of<Pair<Int, ShipFollowOwnerBehavior>?>(
                Pair.of<Int?, ShipFollowOwnerBehavior?>(ShipAiNumbers.FOLLOW_OWNER_PRIORITY, ShipFollowOwnerBehavior())
            )
        )
    }

    private fun initCombatActivity(brain: Brain<EntityShipBase>) {
        brain.addActivity(
            activityFor(ShipBrainActivityResolver.Mode.COMBAT),
            ImmutableList.of<Pair<Int, ShipCombatMemoryBehavior>?>(
                Pair.of<Int?, ShipCombatMemoryBehavior?>(
                    ShipAiNumbers.COMBAT_MEMORY_PRIORITY,
                    ShipCombatMemoryBehavior()
                )
            )
        )
    }

    private fun initIdleActivity(brain: Brain<EntityShipBase>) {
        brain.addActivity(
            Activity.IDLE, ImmutableList.of<Pair<Int, ShipRandomStrollBehavior>?>(
                Pair.of<Int?, ShipRandomStrollBehavior?>(
                    ShipAiNumbers.RANDOM_STROLL_PRIORITY,
                    ShipRandomStrollBehavior()
                )
            )
        )
    }

    private fun canMove(ship: EntityShipBase): Boolean {
        return !ship.isOrderedToSit && !ship.isInSittingPose && !ship.isInDeadPose && !ship.isVehicle && !isPassengerOfLivingVehicle(
            ship
        )
    }

    private fun isPassengerOfLivingVehicle(ship: EntityShipBase): Boolean {
        if (!ship.isPassenger) return false
        val vehicle = ship.vehicle
        return vehicle != null && vehicle.isAlive
    }

    private fun shouldFollowOwner(ship: EntityShipBase, following: Boolean): Boolean {
        return ShipBrainActivityResolver.shouldFollowOwner(activityState(ship), following)
    }

    private fun resolveFollowMinDistance(followMemory: FollowStateMemory): Float {
        return ShipBrainActivityResolver.resolveFollowMinDistance(
            followMemory.followMinConfig
        )
    }

    private fun resolveFollowMaxDistance(followMemory: FollowStateMemory, minDist: Float): Float {
        return ShipBrainActivityResolver.resolveFollowMaxDistance(
            followMemory.followMaxConfig, minDist
        )
    }

    private fun hasSameDimensionOwnerPosition(level: ServerLevel, followMemory: FollowStateMemory): Boolean {
        return followMemory.ownerPresent
                && followMemory.ownerPos != null && followMemory.ownerDimensionId == EntityShipBase.getLegacyDimensionId(
            level
        )
    }

    private fun guardResolverState(
        guardTarget: ShipGuardTarget,
        guardMemory: GuardTargetMemory,
        guardedEntity: Entity?,
        distanceSqr: Double,
        summoning: Boolean
    ): ShipGuardDecisionResolver.State {
        return ShipGuardDecisionResolver.State(
            guardTarget.isEntity,
            guardMemory.hasLiveEntityTarget(),
            guardMemory.hasBlockTarget(),
            guardedEntity != null,
            distanceSqr,
            summoning,
            guardMemory.dimensionId,
            if (guardedEntity == null) Int.MIN_VALUE else EntityShipBase.getLegacyDimensionId(guardedEntity.level())
        )
    }

    private fun followResolverState(
        level: ServerLevel,
        ship: EntityShipBase,
        followMemory: FollowStateMemory
    ): FollowState {
        return FollowState(
            canMove(ship),
            followMemory.shouldFollow,
            hasSameDimensionOwnerPosition(level, followMemory),
            followMemory.ownerHasCombatRation,
            followMemory.ownerDistanceSq,
            followMemory.followMinConfig
        )
    }

    private fun setPointWalkAndLookMemory(ship: EntityShipBase, target: Vec3, speed: Double, closeEnoughDist: Int) {
        BehaviorUtils.setWalkAndLookTargetMemories(ship, BlockPos.containing(target), speed.toFloat(), closeEnoughDist)
    }

    private fun setEntityWalkAndLookMemory(ship: EntityShipBase, target: Entity, speed: Double, closeEnoughDist: Int) {
        if (target is LivingEntity) {
            BehaviorUtils.setWalkAndLookTargetMemories(ship, target, speed.toFloat(), closeEnoughDist)
            return
        }
        setPointWalkAndLookMemory(ship, target.position(), speed, closeEnoughDist)
    }

    private fun setEntityLookMemory(ship: EntityShipBase, target: LivingEntity) {
        BehaviorUtils.lookAtEntity(ship, target)
    }


    private fun closeEnoughDistance(distanceSq: Double): Int {
        return max(1, Mth.ceil(sqrt(distanceSq)))
    }

    private class ShipBrainDiagnosticBehavior :
        Behavior<EntityShipBase>(ImmutableMap.of<MemoryModuleType<*>, MemoryStatus>()) {
        private var nextBrainLogTick = 0
        private var nextCanStillUseLogTick = 0

        override fun checkExtraStartConditions(level: ServerLevel, ship: EntityShipBase): Boolean {
            return !ship.isInDeadPose
        }

        override fun start(level: ServerLevel, ship: EntityShipBase, gameTime: Long) {
            val followMemory = followStateMemory(ship)
            diagnosticLog(
                "[SCBrainDiag] brainStart ship={} gameTime={} tick={} activity={} ownerUuid={} tame={} deadPose={} shouldFollow={} reason={}",
                ship.uuid,
                gameTime,
                ship.tickCount,
                describeDesiredActivity(ship),
                ship.ownerUUID,
                ship.isTame,
                ship.isInDeadPose,
                followMemory.shouldFollow,
                followMemory.blockReason
            )
        }

        override fun canStillUse(level: ServerLevel, ship: EntityShipBase, gameTime: Long): Boolean {
            val canStillUse = !ship.isInDeadPose
            if (!canStillUse || ship.tickCount >= this.nextCanStillUseLogTick) {
                this.nextCanStillUseLogTick = ship.tickCount + 40
                val followMemory = followStateMemory(ship)
                diagnosticLog(
                    "[SCBrainDiag] brainCanStillUse ship={} canStillUse={} gameTime={} tick={} activity={} deadPose={} ownerUuid={} shouldFollow={} reason={}",
                    ship.uuid,
                    canStillUse,
                    gameTime,
                    ship.tickCount,
                    describeDesiredActivity(ship),
                    ship.isInDeadPose,
                    ship.ownerUUID,
                    followMemory.shouldFollow,
                    followMemory.blockReason
                )
            }
            return canStillUse
        }

        override fun timedOut(gameTime: Long): Boolean {
            return false
        }

        override fun tick(level: ServerLevel, ship: EntityShipBase, gameTime: Long) {
            if (ship.tickCount < this.nextBrainLogTick) {
                return
            }
            this.nextBrainLogTick = ship.tickCount + 40
            val pointerMemory = pointerTargetMemory(ship)
            val guardMemory = guardTargetMemory(ship)
            val followMemory = followStateMemory(ship)
            diagnosticLog(
                "[SCBrainDiag] brainTick ship={} activity={} ownerUuid={} ownerPresent={} tame={} canMove={} shouldFollow={} reason={} distSq={} pointer={} guard={} target={}",
                ship.uuid,
                describeDesiredActivity(ship),
                ship.ownerUUID,
                followMemory.ownerPresent,
                ship.isTame,
                canMove(ship),
                followMemory.shouldFollow,
                followMemory.blockReason,
                followMemory.ownerDistanceSq,
                pointerMemory.hasAnyTarget(),
                guardMemory.target?.isActive == true,
                ship.target != null
            )
        }

        override fun stop(level: ServerLevel, ship: EntityShipBase, gameTime: Long) {
            val followMemory = followStateMemory(ship)
            diagnosticLog(
                "[SCBrainDiag] brainStop ship={} gameTime={} tick={} activity={} ownerUuid={} tame={} deadPose={} shouldFollow={} reason={}",
                ship.uuid,
                gameTime,
                ship.tickCount,
                describeDesiredActivity(ship),
                ship.ownerUUID,
                ship.isTame,
                ship.isInDeadPose,
                followMemory.shouldFollow,
                followMemory.blockReason
            )
        }
    }

    private class ShipCombatMemoryBehavior :
        Behavior<EntityShipBase>(ImmutableMap.of<MemoryModuleType<*>, MemoryStatus>()) {
        private val combatRecovery = ShipMovementRecoveryState()
        private var nextCombatPathTick = 0
        private var lastCombatTargetId: UUID? = null

        override fun checkExtraStartConditions(level: ServerLevel, ship: EntityShipBase): Boolean {
            return ship.target != null && !ship.isInDeadPose
        }

        override fun canStillUse(level: ServerLevel, ship: EntityShipBase, gameTime: Long): Boolean {
            return ship.target != null && !ship.isInDeadPose
        }

        override fun tick(level: ServerLevel, ship: EntityShipBase, gameTime: Long) {
            var state = ship.updatePassiveCombatStateBrain()
            ship.getBrain()
                .setMemory<PassiveCombatStateMemory?>(ModMemoryModules.SHIP_PASSIVE_COMBAT_STATE.get(), state)
            state = passiveCombatStateMemory(ship)
            val target = ship.target
            if (target != null && target.isAlive) {
                ship.getBrain().setMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET, target)
            } else {
                ship.getBrain().eraseMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET)
                clearCombatMoveState(ship)
                return
            }

            if (!state.hasTarget()) {
                clearCombatMoveState(ship)
                return
            }
            if (state.shouldChase) {
                tickPassiveCombatChase(ship, target, state)
                return
            }

            this.combatRecovery.reset(ship.position())
            syncCombatRecoveryMemory(ship)
            ship.combatMovementCoordinator().stop()
            if (!state.needsMovement) {
                ship.tickPassiveCombatActionsBrain(state)
            }
        }

        override fun stop(level: ServerLevel, ship: EntityShipBase, gameTime: Long) {
            if (ship.target == null || !ship.target!!.isAlive) {
                ship.clearPassiveCombatTargetBrain(true)
            }
            ship.getBrain().eraseMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET)
            ship.getBrain().eraseMemory<PassiveCombatStateMemory?>(ModMemoryModules.SHIP_PASSIVE_COMBAT_STATE.get())
            clearCombatMoveState(ship)
        }

        fun tickPassiveCombatChase(
            ship: EntityShipBase, target: LivingEntity,
            state: PassiveCombatStateMemory
        ) {
            if (this.lastCombatTargetId != state.targetId) {
                this.nextCombatPathTick = 0
                this.lastCombatTargetId = state.targetId
                this.combatRecovery.reset(ship.position())
                ship.combatMovementCoordinator().reset()
                debugLog(
                    "[SCMoveDiag] PassiveCombat targetChanged ship={} target={}",
                    ship.uuid, state.targetId
                )
            }

            setEntityWalkAndLookMemory(ship, target, state.moveSpeed, 1)
            this.combatRecovery.trackProgress(ship.position())
            syncCombatRecoveryMemory(ship)
            if (ShipCombatDecisionResolver.shouldClearAfterStuck(this.combatRecovery)) {
                if (tryPassiveCombatTeleportRecovery(ship, target, state.distanceSqr, true)) {
                    return
                }
                debugLog(
                    "[SCMoveDiag] PassiveCombat stuckClear ship={} target={} stuckTicks={} distanceSqr={}",
                    ship.uuid, target.uuid, this.combatRecovery.stuckTicks(), state.distanceSqr
                )
                ship.clearPassiveCombatTargetBrain(true)
                clearCombatMoveState(ship)
                return
            }

            if (this.nextCombatPathTick-- <= 0 || ship.combatMovementCoordinator().isNavigationDone) {
                this.nextCombatPathTick = ShipAiNumbers.PASSIVE_COMBAT_PATH_RECALC_INTERVAL
                if (tryPassiveCombatTeleportRecovery(ship, target, state.distanceSqr, false)) {
                    return
                }
                val movement = ship.combatMovementCoordinator()
                if (!movement.moveTo(target, state.moveSpeed)) {
                    val failCount = ShipBrainRecoverySupport.recordMoveFailureAndSync(
                        ship, this.combatRecovery,
                        ModMemoryModules.SHIP_COMBAT_RECOVERY.get()!!, ShipAiNumbers.PASSIVE_COMBAT_STUCK_TICK_LIMIT
                    )
                    if (this.combatRecovery.shouldLogMoveFailure(
                            ship.tickCount,
                            ShipAiNumbers.PASSIVE_COMBAT_MOVE_FAIL_LOG_INTERVAL
                        )
                    ) {
                        debugLog(
                            "[SCMoveDiag] PassiveCombat moveFail ship={} target={} failCount={} distanceSqr={}",
                            ship.uuid, target.uuid, failCount, state.distanceSqr
                        )
                    }
                    if (ShipCombatDecisionResolver.shouldClearAfterMoveFailures(failCount)) {
                        if (tryPassiveCombatTeleportRecovery(ship, target, state.distanceSqr, true)) {
                            return
                        }
                        debugLog(
                            "[SCMoveDiag] PassiveCombat failClear ship={} target={} failCount={}",
                            ship.uuid, target.uuid, this.combatRecovery.moveFailCount()
                        )
                        ship.clearPassiveCombatTargetBrain(true)
                        clearCombatMoveState(ship)
                    }
                    this.nextCombatPathTick = 2
                } else {
                    ShipBrainRecoverySupport.clearMoveFailuresAndSync(
                        ship, this.combatRecovery,
                        ModMemoryModules.SHIP_COMBAT_RECOVERY.get()!!, ShipAiNumbers.PASSIVE_COMBAT_STUCK_TICK_LIMIT
                    )
                }
            }
        }

        fun tryPassiveCombatTeleportRecovery(
            ship: EntityShipBase, target: LivingEntity,
            distanceSqr: Double, force: Boolean
        ): Boolean {
            val recoveryState =
                ShipRecoveryDecisionResolver.State(
                    force,
                    distanceSqr,
                    ShipAiNumbers.PASSIVE_COMBAT_TELEPORT_DISTANCE_SQ
                )
            if (!ShipBrainRecoverySupport.shouldTryTeleportRecovery(
                    this.combatRecovery, recoveryState,
                    ShipAiNumbers.PASSIVE_COMBAT_TELEPORT_COOLDOWN_TICKS
                )
            ) {
                return false
            }
            if (!ship.combatMovementCoordinator().teleportNearLiving(target, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)) {
                return false
            }

            debugLog(
                "[SCMoveDiag] PassiveCombat teleportRecovery ship={} target={} force={} distanceSqr={}",
                ship.uuid, target.uuid, force, distanceSqr
            )
            this.nextCombatPathTick = 0
            this.combatRecovery.reset(ship.position())
            syncCombatRecoveryMemory(ship)
            return true
        }

        fun clearCombatMoveState(ship: EntityShipBase) {
            this.nextCombatPathTick = 0
            this.lastCombatTargetId = null
            ShipBrainRecoverySupport.clearMovementRuntime(
                ship, this.combatRecovery, ModMemoryModules.SHIP_COMBAT_RECOVERY.get()!!,
                ship.combatMovementCoordinator()
            )
        }

        fun syncCombatRecoveryMemory(ship: EntityShipBase) {
            ShipBrainRecoverySupport.syncRecoveryMemory(
                ship, ModMemoryModules.SHIP_COMBAT_RECOVERY.get()!!,
                this.combatRecovery, ShipAiNumbers.PASSIVE_COMBAT_STUCK_TICK_LIMIT
            )
        }
    }

    private class ShipPassiveCombatTargetingBehavior :
        Behavior<EntityShipBase>(ImmutableMap.of<MemoryModuleType<*>, MemoryStatus>()) {
        override fun checkExtraStartConditions(level: ServerLevel, ship: EntityShipBase): Boolean {
            return !ship.isNoFuel && !ship.hasPointerTargetEntity() && !ship.isInDeadPose
        }

        override fun canStillUse(level: ServerLevel, ship: EntityShipBase, gameTime: Long): Boolean {
            return !ship.isNoFuel && !ship.hasPointerTargetEntity() && !ship.isInDeadPose
        }

        override fun timedOut(gameTime: Long): Boolean {
            return false
        }

        override fun tick(level: ServerLevel, ship: EntityShipBase, gameTime: Long) {
            ship.tickPassiveCombatTargetingBrain()
        }

        override fun stop(level: ServerLevel, ship: EntityShipBase, gameTime: Long) {
            if (ship.isNoFuel || ship.hasPointerTargetEntity() || ship.isInDeadPose) {
                ship.clearPassiveCombatTargetBrain(true)
            }
        }
    }

    private class ShipPointerMoveBehavior :
        Behavior<EntityShipBase>(ImmutableMap.of<MemoryModuleType<*>, MemoryStatus>()) {
        private val pointerRecovery = ShipMovementRecoveryState()
        private var nextPointerPathTick = 0
        private var pointerEntityMeleeAttackTick = 0
        private var pointerEntityLightShotTick = 0
        private var pointerEntityHeavyShotTick = 0
        private var lastRawPointerTarget: Vec3? = null
        private var lastPointerEntityTargetId: UUID? = null

        override fun checkExtraStartConditions(level: ServerLevel, ship: EntityShipBase): Boolean {
            return pointerTargetMemory(ship).hasAnyTarget() && canMove(ship)
        }

        override fun canStillUse(level: ServerLevel, ship: EntityShipBase, gameTime: Long): Boolean {
            return pointerTargetMemory(ship).hasAnyTarget() && canMove(ship)
        }

        override fun timedOut(gameTime: Long): Boolean {
            return false
        }

        override fun tick(level: ServerLevel, ship: EntityShipBase, gameTime: Long) {
            val pointerMemory = pointerTargetMemory(ship)
            if (pointerMemory.entityTargetAlive && pointerMemory.entityTargetPos != null) {
                tickPointerEntityMove(ship, pointerMemory)
                return
            }

            val rawTarget = pointerMemory.rawPointTarget
            val target = pointerMemory.adjustedPointTarget
            if (rawTarget == null || target == null) {
                clearPointerMoveState(ship)
                return
            }

            if (this.lastRawPointerTarget == null || rawTarget.distanceToSqr(this.lastRawPointerTarget) > ShipAiNumbers.TARGET_SWITCH_DISTANCE_SQ) {
                this.nextPointerPathTick = 0
                this.lastRawPointerTarget = rawTarget
                this.lastPointerEntityTargetId = null
                this.pointerRecovery.reset(ship.position())
                ship.pointerMovementCoordinator().reset()
                debugLog("ShipBrain pointerTargetChanged ship={} target={}", ship.uuid, target)
            }

            if (ship.distanceToSqr(target) <= ShipAiNumbers.POINTER_MOVE_REACH_SQR) {
                clearPointerMoveState(ship)
                return
            }

            setPointWalkAndLookMemory(
                ship, target, ShipAiNumbers.POINTER_MOVE_SPEED,
                closeEnoughDistance(ShipAiNumbers.POINTER_MOVE_REACH_SQR)
            )
            ship.resetInteractionEmotionState()
            this.pointerRecovery.trackProgress(ship.position())
            syncPointerRecoveryMemory(ship)
            if (this.pointerRecovery.isStuckLongerThan(ShipAiNumbers.MOVE_STUCK_TICK_LIMIT)) {
                if (tryPointerTeleportRecovery(ship, target, true)) {
                    return
                }
                debugLog(
                    "[SCMoveDiag] PointerGoal stuckClear ship={} target={} stuckTicks={}",
                    ship.uuid, target, this.pointerRecovery.stuckTicks()
                )
                ship.clearPointerTarget()
                clearPointerMoveState(ship)
                return
            }
            if (tryPointerTeleportRecovery(ship, target, false)) {
                return
            }

            if (this.nextPointerPathTick-- <= 0) {
                this.nextPointerPathTick = ShipAiNumbers.PATH_RECALC_INTERVAL_TICKS
                val movement = ship.pointerMovementCoordinator()
                if (!movement.moveTo(target, ShipAiNumbers.POINTER_MOVE_SPEED)) {
                    val failCount = ShipBrainRecoverySupport.recordMoveFailureAndSync(
                        ship, this.pointerRecovery,
                        ModMemoryModules.SHIP_POINTER_RECOVERY.get()!!, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT
                    )
                    if (this.pointerRecovery.shouldLogMoveFailure(
                            ship.tickCount,
                            ShipAiNumbers.MOVE_FAIL_LOG_INTERVAL
                        )
                    ) {
                        debugLog(
                            "[SCMoveDiag] PointerGoal moveFail ship={} target={} failCount={}",
                            ship.uuid, target, failCount
                        )
                    }
                    if (failCount > ShipAiNumbers.MOVE_FAIL_LIMIT) {
                        if (tryPointerTeleportRecovery(ship, target, true)) {
                            return
                        }
                        debugLog(
                            "[SCMoveDiag] PointerGoal failClear ship={} target={} failCount={}",
                            ship.uuid, target, this.pointerRecovery.moveFailCount()
                        )
                        ship.clearPointerTarget()
                        clearPointerMoveState(ship)
                    }
                } else {
                    ShipBrainRecoverySupport.clearMoveFailuresAndSync(
                        ship, this.pointerRecovery,
                        ModMemoryModules.SHIP_POINTER_RECOVERY.get()!!, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT
                    )
                    debugLog("ShipBrain pointerMoveOk ship={} target={}", ship.uuid, target)
                }
            }
        }

        override fun stop(level: ServerLevel, ship: EntityShipBase, gameTime: Long) {
            clearPointerMoveState(ship)
        }

        fun tickPointerEntityMove(ship: EntityShipBase, pointerMemory: PointerTargetMemory) {
            val target: Entity? = ship.pointerTargetEntity
            if (target == null || !target.isAlive) {
                ship.clearPointerTargetEntity()
                clearPointerMoveState(ship)
                return
            }
            if (this.lastPointerEntityTargetId != pointerMemory.entityTargetId) {
                this.nextPointerPathTick = 0
                this.lastRawPointerTarget = null
                this.lastPointerEntityTargetId = pointerMemory.entityTargetId
                this.pointerRecovery.reset(ship.position())
                resetPointerEntityAttackCadence(ship)
                ship.pointerMovementCoordinator().reset()
                debugLog(
                    "[SCMoveDiag] PointerEntity targetChanged ship={} target={}",
                    ship.uuid, pointerMemory.entityTargetId
                )
            }

            if (!pointerMemory.entityShouldChase) {
                this.nextPointerPathTick = 0
                ShipBrainRecoverySupport.resetMovementRuntime(
                    ship, this.pointerRecovery, ModMemoryModules.SHIP_POINTER_RECOVERY.get()!!,
                    ship.pointerMovementCoordinator(), ShipAiNumbers.POINTER_ENTITY_STUCK_TICK_LIMIT
                )
                ShipBrainRecoverySupport.clearWalkAndLookMemory(ship)
                if (target is LivingEntity) {
                    setEntityLookMemory(ship, target)
                } else {
                    ship.lookControl.setLookAt(
                        target.x, target.y, target.z,
                        ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH
                    )
                }
                tickPointerEntityAttacks(ship, target, pointerMemory)
                return
            }

            setEntityWalkAndLookMemory(ship, target, ShipAiNumbers.POINTER_ENTITY_MOVE_SPEED, 1)
            val distanceSqr = pointerMemory.entityDistanceSqr
            this.pointerRecovery.trackProgress(ship.position())
            syncPointerEntityRecoveryMemory(ship)
            if (ShipPointerEntityDecisionResolver.shouldClearAfterStuck(this.pointerRecovery)) {
                if (tryPointerEntityTeleportRecovery(ship, target, distanceSqr, true)) {
                    return
                }
                debugLog(
                    "[SCMoveDiag] PointerEntity stuckClear ship={} target={} stuckTicks={} distanceSqr={}",
                    ship.uuid, target.uuid, this.pointerRecovery.stuckTicks(), distanceSqr
                )
                ship.clearPointerTargetEntity()
                clearPointerMoveState(ship)
                return
            }
            if (this.nextPointerPathTick-- <= 0 || ship.pointerMovementCoordinator().isNavigationDone) {
                this.nextPointerPathTick = ShipAiNumbers.POINTER_ENTITY_PATH_RECALC_INTERVAL
                if (tryPointerEntityTeleportRecovery(ship, target, distanceSqr, false)) {
                    return
                }
                val movement = ship.pointerMovementCoordinator()
                if (!movement.moveTo(target, ShipAiNumbers.POINTER_ENTITY_MOVE_SPEED)) {
                    val failCount = ShipBrainRecoverySupport.recordMoveFailureAndSync(
                        ship, this.pointerRecovery,
                        ModMemoryModules.SHIP_POINTER_RECOVERY.get()!!, ShipAiNumbers.POINTER_ENTITY_STUCK_TICK_LIMIT
                    )
                    if (this.pointerRecovery.shouldLogMoveFailure(
                            ship.tickCount,
                            ShipAiNumbers.POINTER_ENTITY_MOVE_FAIL_LOG_INTERVAL
                        )
                    ) {
                        debugLog(
                            "[SCMoveDiag] PointerEntity moveFail ship={} target={} failCount={} distanceSqr={}",
                            ship.uuid, target.uuid, failCount, distanceSqr
                        )
                    }
                    if (ShipPointerEntityDecisionResolver.shouldClearAfterMoveFailures(failCount)) {
                        if (tryPointerEntityTeleportRecovery(ship, target, distanceSqr, true)) {
                            return
                        }
                        debugLog(
                            "[SCMoveDiag] PointerEntity failClear ship={} target={} failCount={}",
                            ship.uuid, target.uuid, this.pointerRecovery.moveFailCount()
                        )
                        ship.clearPointerTargetEntity()
                        clearPointerMoveState(ship)
                    }
                    this.nextPointerPathTick = 2
                } else {
                    ShipBrainRecoverySupport.clearMoveFailuresAndSync(
                        ship, this.pointerRecovery,
                        ModMemoryModules.SHIP_POINTER_RECOVERY.get()!!, ShipAiNumbers.POINTER_ENTITY_STUCK_TICK_LIMIT
                    )
                }
            }
        }

        fun tryPointerTeleportRecovery(ship: EntityShipBase, target: Vec3, force: Boolean): Boolean {
            val recoveryState =
                ShipRecoveryDecisionResolver.State(
                    force,
                    ship.distanceToSqr(target),
                    ShipAiNumbers.TELEPORT_DISTANCE_SQ
                )
            if (!ShipBrainRecoverySupport.shouldTryTeleportRecovery(
                    this.pointerRecovery, recoveryState,
                    ShipAiNumbers.TELEPORT_COOLDOWN_TICKS
                )
            ) {
                return false
            }
            if (!ship.pointerMovementCoordinator().teleportNearPoint(target, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)) {
                return false
            }
            debugLog(
                "[SCMoveDiag] PointerGoal teleportRecovery ship={} target={} force={}",
                ship.uuid, target, force
            )
            this.nextPointerPathTick = 0
            this.pointerRecovery.reset(ship.position())
            syncPointerEntityRecoveryMemory(ship)
            return true
        }

        fun tryPointerEntityTeleportRecovery(
            ship: EntityShipBase,
            target: Entity,
            distanceSqr: Double,
            force: Boolean
        ): Boolean {
            val recoveryState =
                ShipRecoveryDecisionResolver.State(
                    force,
                    distanceSqr,
                    ShipAiNumbers.POINTER_ENTITY_TELEPORT_DISTANCE_SQ
                )
            if (!ShipBrainRecoverySupport.shouldTryTeleportRecovery(
                    this.pointerRecovery, recoveryState,
                    ShipAiNumbers.POINTER_ENTITY_TELEPORT_COOLDOWN_TICKS
                )
            ) {
                return false
            }
            val movement = ship.pointerMovementCoordinator()
            val teleported = if (target is LivingEntity)
                movement.teleportNearLiving(target, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)
            else
                movement.teleportNearPoint(target.position(), ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)
            if (!teleported) {
                return false
            }
            debugLog(
                "[SCMoveDiag] PointerEntity teleportRecovery ship={} target={} force={}",
                ship.uuid, target.uuid, force
            )
            this.nextPointerPathTick = 0
            this.pointerRecovery.reset(ship.position())
            syncPointerRecoveryMemory(ship)
            return true
        }

        fun clearPointerMoveState(ship: EntityShipBase) {
            this.nextPointerPathTick = 0
            this.lastRawPointerTarget = null
            this.lastPointerEntityTargetId = null
            this.pointerEntityMeleeAttackTick = 0
            this.pointerEntityLightShotTick = 0
            this.pointerEntityHeavyShotTick = 0
            ShipBrainRecoverySupport.clearMovementRuntime(
                ship, this.pointerRecovery, ModMemoryModules.SHIP_POINTER_RECOVERY.get()!!,
                ship.pointerMovementCoordinator()
            )
        }

        fun resetPointerEntityAttackCadence(ship: EntityShipBase) {
            val aimDelay = ShipPointerEntityDecisionResolver.aimDelayTicks(ship.level)
            this.pointerEntityMeleeAttackTick = ship.tickCount + aimDelay
            this.pointerEntityLightShotTick = ship.tickCount + aimDelay
            this.pointerEntityHeavyShotTick = ship.tickCount + aimDelay
            ship.combat.resetAircraftLaunchDelay()
        }

        fun tickPointerEntityAttacks(
            ship: EntityShipBase, target: Entity,
            pointerMemory: PointerTargetMemory
        ) {
            ship.moveControl.setWantedPosition(ship.x, ship.y, ship.z, 0.0)
            val combat = ship.combat
            if (combat.hasAircraftAttackEnabled()) {
                combat.tryPerformAircraftCycle(target)
            }

            val attackState = AttackState(
                ship.tickCount,
                combat.canUseLightAmmo(),
                ship.legacyShipStats.lightDelay,
                this.pointerEntityLightShotTick,
                combat.canUseHeavyAmmo(),
                ship.legacyShipStats.heavyDelay,
                this.pointerEntityHeavyShotTick,
                pointerMemory.entityCanMeleeAttack,
                pointerMemory.entityDistanceSqr,
                pointerMemory.entityAttackRangeSqr,
                ship.legacyShipStats.meleeDelay,
                this.pointerEntityMeleeAttackTick
            )

            if (ShipPointerEntityDecisionResolver.shouldFireLightAttack(attackState)) {
                this.pointerEntityLightShotTick = ship.tickCount
                ship.performLightAttack(target)
            }

            if (ShipPointerEntityDecisionResolver.shouldFireHeavyAttack(attackState)) {
                if (ship.performHeavyAttack(target)) {
                    this.pointerEntityHeavyShotTick = ship.tickCount
                }
            }

            if (ShipPointerEntityDecisionResolver.shouldFireMeleeAttack(attackState)) {
                this.pointerEntityMeleeAttackTick = ship.tickCount
                ship.doHurtTarget(target)
            }
        }

        fun syncPointerRecoveryMemory(ship: EntityShipBase) {
            ShipBrainRecoverySupport.syncRecoveryMemory(
                ship, ModMemoryModules.SHIP_POINTER_RECOVERY.get()!!,
                this.pointerRecovery, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT
            )
        }

        fun syncPointerEntityRecoveryMemory(ship: EntityShipBase) {
            ShipBrainRecoverySupport.syncRecoveryMemory(
                ship, ModMemoryModules.SHIP_POINTER_RECOVERY.get()!!,
                this.pointerRecovery, ShipAiNumbers.POINTER_ENTITY_STUCK_TICK_LIMIT
            )
        }
    }

    private class ShipGuardMoveBehavior :
        Behavior<EntityShipBase>(ImmutableMap.of<MemoryModuleType<*>, MemoryStatus>()) {
        private val guardRecovery = ShipMovementRecoveryState()
        private var nextGuardPathTick = 0
        private var lastGuardRecoveryTargetKey: GuardRecoveryTargetKey? = null

        override fun checkExtraStartConditions(level: ServerLevel, ship: EntityShipBase): Boolean {
            return guardTargetMemory(ship).canGuard
        }

        override fun canStillUse(level: ServerLevel, ship: EntityShipBase, gameTime: Long): Boolean {
            return guardTargetMemory(ship).canGuard
        }

        override fun timedOut(gameTime: Long): Boolean {
            return false
        }

        override fun tick(level: ServerLevel, ship: EntityShipBase, gameTime: Long) {
            val guardMemory = guardTargetMemory(ship)
            val guardedEntity = ship.guardedEntity
            val guardTarget = guardMemory.target ?: return

            val timer = ship.getStateTimer(ShipAiNumbers.GUARD_SUMMON_TIMER_ID)
            val isSummoning = timer > 0
            if (isSummoning) {
                ship.setStateTimer(ShipAiNumbers.GUARD_SUMMON_TIMER_ID, timer - 1)
            }

            val unresolvedGuardState = guardResolverState(
                guardTarget, guardMemory, guardedEntity, 0.0, isSummoning
            )
            val target: Vec3
            if (guardMemory.hasLiveEntityTarget()) {
                if (ShipGuardDecisionResolver.shouldSyncEntityDimension(unresolvedGuardState)) {
                    ship.setGuardedPos(
                        -1, -1, -1, EntityShipBase.getLegacyDimensionId(guardedEntity!!.level()),
                        ShipGuardTarget.Type.ENTITY.legacyId()
                    )
                }
                target = guardMemory.guardedEntityPos!!
            } else if (guardMemory.hasBlockTarget()) {
                target = guardMemory.blockCenter!!
            } else {
                clearGuardMoveState(ship)
                return
            }

            resetGuardRecoveryIfTargetChanged(ship, guardTarget, guardedEntity)
            val distSq = ship.distanceToSqr(target)
            val guardState = guardResolverState(
                guardTarget, guardMemory, guardedEntity, distSq, isSummoning
            )
            val stopDistanceSq = ShipGuardDecisionResolver.stopDistanceSqr(guardState)
            if (guardedEntity != null) {
                setEntityWalkAndLookMemory(
                    ship, guardedEntity, ShipAiNumbers.GUARD_MOVE_SPEED,
                    closeEnoughDistance(stopDistanceSq)
                )
            } else {
                setPointWalkAndLookMemory(
                    ship, target, ShipAiNumbers.GUARD_MOVE_SPEED,
                    closeEnoughDistance(stopDistanceSq)
                )
            }

            if (ShipGuardDecisionResolver.shouldMove(guardState)) {
                this.guardRecovery.trackProgress(ship.position())
                syncGuardRecoveryMemory(ship)
                if (tryGuardTeleportRecovery(ship, target, guardedEntity, distSq, false)) {
                    return
                }
                if (ShipGuardDecisionResolver.shouldClearAfterStuck(this.guardRecovery)) {
                    if (tryGuardTeleportRecovery(ship, target, guardedEntity, distSq, true)) {
                        return
                    }
                    debugLog(
                        "[SCMoveDiag] GuardGoal stuckDisable ship={} target={} stuckTicks={}",
                        ship.uuid, target, this.guardRecovery.stuckTicks()
                    )
                    disableGuardState(ship)
                    return
                }
                if (this.nextGuardPathTick-- <= 0 || ship.guardMovementCoordinator().isNavigationDone) {
                    this.nextGuardPathTick = ShipAiNumbers.PATH_RECALC_INTERVAL_TICKS
                    if (!ship.guardMovementCoordinator().moveTo(target, ShipAiNumbers.GUARD_MOVE_SPEED)) {
                        val failCount = ShipBrainRecoverySupport.recordMoveFailureAndSync(
                            ship, this.guardRecovery,
                            ModMemoryModules.SHIP_GUARD_RECOVERY.get()!!, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT
                        )
                        if (this.guardRecovery.shouldLogMoveFailure(
                                ship.tickCount,
                                ShipAiNumbers.MOVE_FAIL_LOG_INTERVAL
                            )
                        ) {
                            debugLog(
                                "[SCMoveDiag] GuardGoal moveFail ship={} target={} failCount={}",
                                ship.uuid, target, failCount
                            )
                        }
                        if (ShipGuardDecisionResolver.shouldClearAfterMoveFailures(failCount)) {
                            if (tryGuardTeleportRecovery(ship, target, guardedEntity, distSq, true)) {
                                return
                            }
                            debugLog(
                                "[SCMoveDiag] GuardGoal failDisable ship={} target={} failCount={}",
                                ship.uuid, target, this.guardRecovery.moveFailCount()
                            )
                            disableGuardState(ship)
                            return
                        }
                    } else {
                        ShipBrainRecoverySupport.clearMoveFailuresAndSync(
                            ship, this.guardRecovery,
                            ModMemoryModules.SHIP_GUARD_RECOVERY.get()!!, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT
                        )
                        debugLog("ShipBrain guardMoveOk ship={} target={} distSq={}", ship.uuid, target, distSq)
                    }
                }
            } else {
                this.nextGuardPathTick = 0
                ShipBrainRecoverySupport.resetMovementRuntime(
                    ship, this.guardRecovery, ModMemoryModules.SHIP_GUARD_RECOVERY.get()!!,
                    ship.guardMovementCoordinator(), ShipAiNumbers.MOVE_STUCK_TICK_LIMIT
                )
            }

            updateGuardLook(ship, guardedEntity, target, distSq, isSummoning)
        }

        override fun stop(level: ServerLevel, ship: EntityShipBase, gameTime: Long) {
            clearGuardMoveState(ship)
        }

        fun resetGuardRecoveryIfTargetChanged(
            ship: EntityShipBase,
            guardTarget: ShipGuardTarget,
            guardedEntity: Entity?
        ) {
            val targetKey = GuardRecoveryTargetKey.from(guardTarget, guardedEntity)
            if (this.lastGuardRecoveryTargetKey == targetKey) {
                return
            }
            this.lastGuardRecoveryTargetKey = targetKey
            this.nextGuardPathTick = 0
            this.guardRecovery.reset(ship.position())
            syncGuardRecoveryMemory(ship)
            ship.guardMovementCoordinator().reset()
        }

        fun tryGuardTeleportRecovery(
            ship: EntityShipBase,
            target: Vec3,
            guardedEntity: Entity?,
            distSq: Double,
            force: Boolean
        ): Boolean {
            val recoveryState =
                ShipRecoveryDecisionResolver.State(
                    force,
                    distSq,
                    ShipAiNumbers.TELEPORT_DISTANCE_SQ
                )
            if (!ShipBrainRecoverySupport.shouldTryTeleportRecovery(
                    this.guardRecovery, recoveryState,
                    ShipAiNumbers.TELEPORT_COOLDOWN_TICKS
                )
            ) {
                return false
            }
            val teleported = if (guardedEntity is LivingEntity)
                ship.guardMovementCoordinator()
                    .teleportNearLiving(guardedEntity, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)
            else
                ship.guardMovementCoordinator().teleportNearPoint(target, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)
            if (!teleported) {
                return false
            }
            debugLog(
                "[SCMoveDiag] GuardGoal teleportRecovery ship={} target={} force={} distSq={}",
                ship.uuid, target, force, distSq
            )
            this.nextGuardPathTick = 0
            this.guardRecovery.reset(ship.position())
            syncGuardRecoveryMemory(ship)
            return true
        }

        fun disableGuardState(ship: EntityShipBase) {
            this.nextGuardPathTick = 0
            this.lastGuardRecoveryTargetKey = null
            ShipBrainRecoverySupport.clearMovementRuntime(
                ship, this.guardRecovery, ModMemoryModules.SHIP_GUARD_RECOVERY.get()!!,
                ship.guardMovementCoordinator()
            )
            ship.setStateFlag(EntityShipBase.STATE_FLAG_DISABLE_GUARD_POS, true)
            ship.clearGuardTarget()
        }

        fun clearGuardMoveState(ship: EntityShipBase) {
            this.nextGuardPathTick = 0
            this.lastGuardRecoveryTargetKey = null
            ShipBrainRecoverySupport.clearMovementRuntime(
                ship, this.guardRecovery, ModMemoryModules.SHIP_GUARD_RECOVERY.get()!!,
                ship.guardMovementCoordinator()
            )
        }

        fun syncGuardRecoveryMemory(ship: EntityShipBase) {
            ShipBrainRecoverySupport.syncRecoveryMemory(
                ship, ModMemoryModules.SHIP_GUARD_RECOVERY.get()!!,
                this.guardRecovery, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT
            )
        }

        fun updateGuardLook(
            ship: EntityShipBase,
            guardedEntity: Entity?,
            target: Vec3,
            distSq: Double,
            isSummoning: Boolean
        ) {
            if (guardedEntity is LivingEntity) {
                ship.lookControl.setLookAt(
                    guardedEntity.x, guardedEntity.eyeY, guardedEntity.z,
                    ShipAiNumbers.FOCUS_LOOK_YAW, ShipAiNumbers.FOCUS_LOOK_PITCH
                )
            } else if (guardedEntity != null) {
                ship.lookControl.setLookAt(
                    guardedEntity.x, guardedEntity.y, guardedEntity.z,
                    ShipAiNumbers.FOCUS_LOOK_YAW, ShipAiNumbers.FOCUS_LOOK_PITCH
                )
            } else if (ShipGuardDecisionResolver.shouldLookAtOwnerOrPlayer(
                    ShipGuardDecisionResolver.State(
                        false,
                        false,
                        false,
                        false,
                        distSq,
                        isSummoning,
                        0,
                        0
                    )
                )
            ) {
                lookAtOwnerOrNearestPlayer(ship)
            } else {
                ship.lookControl.setLookAt(
                    target.x, target.y + ship.eyeHeight, target.z,
                    ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH
                )
            }
        }

        fun lookAtOwnerOrNearestPlayer(ship: EntityShipBase) {
            var lookTarget = ship.owner
            if (lookTarget == null || ship.distanceToSqr(lookTarget) > ShipAiNumbers.GUARD_OWNER_LOOK_MAX_DISTANCE_SQ) {
                lookTarget = ship.level().getNearestPlayer(ship, ShipAiNumbers.GUARD_NEAREST_PLAYER_LOOK_DISTANCE)
            }
            if (lookTarget != null) {
                ship.lookControl.setLookAt(
                    lookTarget.x, lookTarget.eyeY, lookTarget.z,
                    ShipAiNumbers.FOCUS_LOOK_YAW, ShipAiNumbers.FOCUS_LOOK_PITCH
                )
                return
            }

            val yaw = ship.yRot
            val rad = -yaw * ShipAiNumbers.DEGREES_TO_RADIANS
            val tx = ship.x + sin(rad) * ShipAiNumbers.GUARD_FALLBACK_LOOK_DISTANCE
            val ty = ship.eyeY
            val tz = ship.z + cos(rad) * ShipAiNumbers.GUARD_FALLBACK_LOOK_DISTANCE
            ship.lookControl.setLookAt(tx, ty, tz, ShipAiNumbers.FOCUS_LOOK_YAW, ShipAiNumbers.FOCUS_LOOK_PITCH)
        }
    }

    private class ShipFollowOwnerBehavior :
        Behavior<EntityShipBase>(ImmutableMap.of<MemoryModuleType<*>, MemoryStatus>()) {
        private val followRecovery = ShipMovementRecoveryState()
        private var lastOwnerX = 0.0
        private var lastOwnerY = 0.0
        private var lastOwnerZ = 0.0
        private var hasOwnerPos = false
        private var followOwnerActive = false
        private var formationDir = booleanArrayOf(false, true)
        private var nextFollowPathTick = 0

        override fun checkExtraStartConditions(level: ServerLevel, ship: EntityShipBase): Boolean {
            return shouldFollowOwner(ship, false)
        }

        override fun canStillUse(level: ServerLevel, ship: EntityShipBase, gameTime: Long): Boolean {
            val followMemory = followStateMemory(ship)
            return ShipBrainActivityResolver.shouldContinueFollow(followResolverState(level, ship, followMemory))
        }

        override fun timedOut(gameTime: Long): Boolean {
            return false
        }

        override fun tick(level: ServerLevel, ship: EntityShipBase, gameTime: Long) {
            val followMemory = followStateMemory(ship)
            val owner = ship.owner
            val ownerPos = followMemory.ownerPos
            if (owner == null || !hasSameDimensionOwnerPosition(level, followMemory) || ownerPos == null) {
                clearFollowMoveState(ship)
                return
            }

            ship.resetInteractionEmotionState()
            if (followMemory.ownerHasCombatRation) {
                ship.emotionPrimary = EntityShipBase.EMOTION_HAPPY
                if (ship.tickCount % ShipAiNumbers.FOLLOW_POSITIVE_EMOTE_INTERVAL == 0) {
                    val positiveEmotes = arrayOf<EmotionParticleType>(
                        EmotionParticleType.HEART,
                        EmotionParticleType.MUSIC_NOTE,
                        EmotionParticleType.HAPPY_BOB,
                        EmotionParticleType.SPARKLE_EYES,
                        EmotionParticleType.POUT_BOUNCE,
                        EmotionParticleType.LAUGH,
                        EmotionParticleType.HAPPY_GLANCE,
                        EmotionParticleType.BLINK,
                        EmotionParticleType.BLUSH
                    )
                    val selected = positiveEmotes[ship.random.nextInt(positiveEmotes.size)]
                    ship.applyParticleEmotion(selected)
                }
            }

            ship.lookControl.setLookAt(owner, ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH)

            val teamId = ship.formationTeam
            val slotId = ship.formationSlot
            var moveTarget = ownerPos
            if (teamId >= 0 && slotId >= 0 && owner is Player) {
                val data = admiralData(owner)
                val formationId = data.getFormationID(teamId)
                updateFormationDirection(owner)
                moveTarget = getFormationPos(
                    formationId, slotId, ownerPos, this.formationDir[0], this.formationDir[1]
                )
            }
            val minDist = resolveFollowMinDistance(followMemory)
            setPointWalkAndLookMemory(
                ship, moveTarget, ShipAiNumbers.FOLLOW_OWNER_SPEED,
                closeEnoughDistance((minDist * minDist).toDouble())
            )
            setEntityLookMemory(ship, owner)

            if (!this.followOwnerActive) {
                val maxDist = resolveFollowMaxDistance(followMemory, minDist)
                diagnosticLog(
                    "{} start ship={} owner={} distSq={} minDist={} maxDist={}",
                    FOLLOW_DEBUG_PREFIX,
                    ship.uuid,
                    followMemory.ownerId,
                    followMemory.ownerDistanceSq,
                    minDist,
                    maxDist
                )
            }
            this.followOwnerActive = true
            val movement = ship.followOwnerMovementCoordinator()
            val distSq = followMemory.ownerDistanceSq

            if (this.nextFollowPathTick-- <= 0) {
                this.nextFollowPathTick = ShipAiNumbers.FOLLOW_PATH_RECALC_INTERVAL
                val moved = movement.moveTo(moveTarget, ShipAiNumbers.FOLLOW_OWNER_SPEED)
                diagnosticLog(
                    "{} move ship={} owner={} target={} moved={}",
                    FOLLOW_DEBUG_PREFIX, ship.uuid, owner.uuid, moveTarget, moved
                )
                if (!moved) {
                    val failCount = ShipBrainRecoverySupport.recordMoveFailureAndSync(
                        ship, this.followRecovery,
                        ModMemoryModules.SHIP_FOLLOW_RECOVERY.get()!!, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT
                    )
                    if (this.followRecovery.shouldLogMoveFailure(
                            ship.tickCount,
                            ShipAiNumbers.FOLLOW_MOVE_FAIL_LOG_INTERVAL
                        )
                    ) {
                        diagnosticLog(
                            "{} moveFail ship={} owner={} target={} failCount={}",
                            FOLLOW_DEBUG_PREFIX, ship.uuid, owner.uuid, moveTarget, failCount
                        )
                    }
                    if (failCount > ShipAiNumbers.FOLLOW_MOVE_FAIL_LIMIT) {
                        diagnosticLog(
                            "{} failClear ship={} owner={} failCount={}",
                            FOLLOW_DEBUG_PREFIX, ship.uuid, owner.uuid, this.followRecovery.moveFailCount()
                        )
                        clearFollowMoveState(ship)
                        return
                    }
                    this.nextFollowPathTick = 2
                } else {
                    ShipBrainRecoverySupport.clearMoveFailuresAndSync(
                        ship, this.followRecovery,
                        ModMemoryModules.SHIP_FOLLOW_RECOVERY.get()!!, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT
                    )
                }
            }

            this.followRecovery.trackProgress(ship.position())
            val force = this.followRecovery.isStuckLongerThan(ShipAiNumbers.MOVE_STUCK_TICK_LIMIT)
            if (!this.followRecovery.shouldTryTeleportThrottled(
                    force, distSq,
                    ShipAiNumbers.TELEPORT_DISTANCE_SQ, ShipAiNumbers.FOLLOW_TELEPORT_COOLDOWN_TICKS
                )
            ) {
                syncFollowRecoveryMemory(ship)
                return
            }

            if (!movement.teleportNearLiving(owner, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)) {
                syncFollowRecoveryMemory(ship)
                return
            }

            diagnosticLog(
                "{} teleportRecovery ship={} owner={} force={} distSq={} stuckTicks={}",
                FOLLOW_DEBUG_PREFIX,
                ship.uuid, owner.uuid, force, distSq, this.followRecovery.stuckTicks()
            )
            this.followRecovery.reset(ship.position())
            syncFollowRecoveryMemory(ship)
        }

        override fun stop(level: ServerLevel, ship: EntityShipBase, gameTime: Long) {
            clearFollowMoveState(ship)
        }

        fun clearFollowMoveState(ship: EntityShipBase) {
            val followMemory = followStateMemory(ship)
            if (this.followOwnerActive && followMemory.ownerPresent) {
                val minDist = resolveFollowMinDistance(followMemory)
                val maxDist = resolveFollowMaxDistance(followMemory, minDist)
                diagnosticLog(
                    "{} stop ship={} owner={} distSq={} minDist={} maxDist={}",
                    FOLLOW_DEBUG_PREFIX,
                    ship.uuid,
                    followMemory.ownerId,
                    followMemory.ownerDistanceSq,
                    minDist,
                    maxDist
                )
            }
            this.hasOwnerPos = false
            this.followOwnerActive = false
            ShipBrainRecoverySupport.clearMovementRuntime(
                ship, this.followRecovery, ModMemoryModules.SHIP_FOLLOW_RECOVERY.get()!!,
                ship.followOwnerMovementCoordinator()
            )
        }

        fun syncFollowRecoveryMemory(ship: EntityShipBase) {
            ShipBrainRecoverySupport.syncRecoveryMemory(
                ship, ModMemoryModules.SHIP_FOLLOW_RECOVERY.get()!!,
                this.followRecovery, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT
            )
        }

        fun updateFormationDirection(owner: LivingEntity) {
            val ox = owner.x
            val oy = owner.y
            val oz = owner.z
            if (!this.hasOwnerPos) {
                this.lastOwnerX = ox
                this.lastOwnerY = oy
                this.lastOwnerZ = oz
                this.hasOwnerPos = true
                return
            }

            val dx = this.lastOwnerX - ox
            val dy = this.lastOwnerY - oy
            val dz = this.lastOwnerZ - oz
            val dsq = dx * dx + dy * dy + dz * dz
            if (dsq > ShipAiNumbers.FOLLOW_FORMATION_UPDATE_DISTANCE_SQ) {
                this.formationDir = getFormationDirection(
                    ox, oz, this.lastOwnerX, this.lastOwnerZ
                )
                this.lastOwnerX = ox
                this.lastOwnerY = oy
                this.lastOwnerZ = oz
            }
        }

        companion object {
            private const val FOLLOW_DEBUG_PREFIX = "[SCFollowDebug]"
        }
    }

    private class ShipLookAtPlayerBehavior :
        Behavior<EntityShipBase>(ImmutableMap.of<MemoryModuleType<*>, MemoryStatus>()) {
        override fun checkExtraStartConditions(level: ServerLevel, ship: EntityShipBase): Boolean {
            return !ship.isInDeadPose
        }

        override fun canStillUse(level: ServerLevel, ship: EntityShipBase, gameTime: Long): Boolean {
            return !ship.isInDeadPose
        }

        override fun tick(level: ServerLevel, ship: EntityShipBase, gameTime: Long) {
            val guardMemory = guardTargetMemory(ship)
            val pointerMemory = pointerTargetMemory(ship)
            val guardedEntity = ship.guardedEntity
            if (guardedEntity is LivingEntity) {
                ship.lookControl.setLookAt(
                    guardedEntity.x, guardedEntity.eyeY, guardedEntity.z,
                    ShipAiNumbers.FOCUS_LOOK_YAW, ShipAiNumbers.FOCUS_LOOK_PITCH
                )
                return
            }
            if (guardedEntity != null) {
                ship.lookControl.setLookAt(
                    guardedEntity.x, guardedEntity.y, guardedEntity.z,
                    ShipAiNumbers.FOCUS_LOOK_YAW, ShipAiNumbers.FOCUS_LOOK_PITCH
                )
                return
            }
            if (pointerMemory.entityTargetAlive && pointerMemory.entityTargetPos != null) {
                val target = pointerMemory.entityTargetPos!!
                ship.lookControl.setLookAt(
                    target.x, target.y + ship.eyeHeight, target.z,
                    ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH
                )
                return
            }
            val followMemory = followStateMemory(ship)
            if (shouldFollowOwnerLook(ship, followMemory)) {
                val target = followMemory.ownerPos!!
                ship.lookControl.setLookAt(
                    target.x, followMemory.ownerEyeY, target.z,
                    ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH
                )
                return
            }
            if (guardMemory.hasBlockTarget()) {
                val target = guardMemory.blockCenter!!
                ship.lookControl.setLookAt(
                    target.x, target.y + ship.eyeHeight, target.z,
                    ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH
                )
                return
            }
            val player = level.getNearestPlayer(ship, ShipAiNumbers.LOOK_AT_PLAYER_DISTANCE.toDouble())
            if (player != null) {
                ship.lookControl.setLookAt(player, ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH)
            }
        }

        fun shouldFollowOwnerLook(ship: EntityShipBase, followMemory: FollowStateMemory): Boolean {
            if (followMemory.ownerPos == null) {
                return false
            }
            if (followMemory.shouldFollow) {
                return true
            }
            return followMemory.ownerDistanceSq <= ShipAiNumbers.GUARD_OWNER_LOOK_MAX_DISTANCE_SQ
                    && guardTargetMemory(ship).target?.isActive == true
        }
    }

    private class ShipRandomLookAroundBehavior :
        Behavior<EntityShipBase>(ImmutableMap.of<MemoryModuleType<*>, MemoryStatus>()) {
        override fun checkExtraStartConditions(level: ServerLevel, ship: EntityShipBase): Boolean {
            return !ship.isInDeadPose && ship.random.nextInt(ShipAiNumbers.RANDOM_LOOK_CHANCE) == 0
        }

        override fun start(level: ServerLevel, ship: EntityShipBase, gameTime: Long) {
            val yaw = ship.yRot
            val rad = -yaw * ShipAiNumbers.DEGREES_TO_RADIANS
            val tx = ship.x + sin(rad) * ShipAiNumbers.GUARD_FALLBACK_LOOK_DISTANCE
            val ty = ship.eyeY
            val tz = ship.z + cos(rad) * ShipAiNumbers.GUARD_FALLBACK_LOOK_DISTANCE
            ship.lookControl.setLookAt(tx, ty, tz, ShipAiNumbers.FOCUS_LOOK_YAW, ShipAiNumbers.FOCUS_LOOK_PITCH)
        }
    }

    private class ShipRandomStrollBehavior :
        Behavior<EntityShipBase>(ImmutableMap.of<MemoryModuleType<*>, MemoryStatus>()) {
        override fun checkExtraStartConditions(level: ServerLevel, ship: EntityShipBase): Boolean {
            return !ship.isOrderedToSit && !ship.isInSittingPose && !ship.isInDeadPose && !isPassengerOfLivingVehicle(
                ship
            ) && !ship.isVehicle && !pointerTargetMemory(ship).hasAnyTarget() && ship.target == null && !followStateMemory(
                ship
            ).shouldFollow && guardTargetMemory(ship).target?.isActive != true && ship.random
                .nextInt(ShipAiNumbers.RANDOM_STROLL_CHANCE) == 0
        }

        override fun start(level: ServerLevel, ship: EntityShipBase, gameTime: Long) {
            val target = DefaultRandomPos.getPos(
                ship, ShipAiNumbers.RANDOM_STROLL_HORIZONTAL_RANGE, ShipAiNumbers.RANDOM_STROLL_VERTICAL_RANGE
            )
            if (target != null) {
                diagnosticLog("[SCIdleDiag] randomStroll ship={} target={}", ship.uuid, target)
                setPointWalkAndLookMemory(ship, target, ShipAiNumbers.RANDOM_STROLL_SPEED, 1)
                ship.idleMovementCoordinator().moveTo(target, ShipAiNumbers.RANDOM_STROLL_SPEED)
            }
        }
    }

    @JvmRecord
    private data class GuardRecoveryTargetKey(
        val type: ShipGuardTarget.Type?,
        val entityId: UUID?,
        val x: Int,
        val y: Int,
        val z: Int,
        val dimensionId: Int
    ) {
        companion object {
            fun from(guardTarget: ShipGuardTarget, guardedEntity: Entity?): GuardRecoveryTargetKey {
                if (guardTarget.isEntity && guardedEntity != null) {
                    return GuardRecoveryTargetKey(
                        guardTarget.type, guardedEntity.uuid, 0, 0, 0,
                        EntityShipBase.getLegacyDimensionId(guardedEntity.level())
                    )
                }
                return GuardRecoveryTargetKey(
                    guardTarget.type, null, guardTarget.x, guardTarget.y,
                    guardTarget.z, guardTarget.dimensionId
                )
            }
        }
    }
}
