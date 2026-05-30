package org.trp.shincolle.entity.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.phys.Vec3;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.init.ModMemoryModules;
import org.trp.shincolle.server.PlayerStateService;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

final class EntityShipBrainAi {
    static final List<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            ModMemoryModules.SHIP_POINTER_TARGET.get(),
            ModMemoryModules.SHIP_GUARD_TARGET.get(),
            ModMemoryModules.SHIP_FOLLOW_STATE.get(),
            ModMemoryModules.SHIP_PASSIVE_COMBAT_STATE.get(),
            ModMemoryModules.SHIP_POINTER_RECOVERY.get(),
            ModMemoryModules.SHIP_GUARD_RECOVERY.get(),
            ModMemoryModules.SHIP_FOLLOW_RECOVERY.get(),
            ModMemoryModules.SHIP_COMBAT_RECOVERY.get()
    );
    static final List<SensorType<? extends net.minecraft.world.entity.ai.sensing.Sensor<? super EntityShipBase>>> SENSOR_TYPES =
            ImmutableList.of();
    private static final Map<ShipBrainActivityResolver.Mode, Activity> MODE_ACTIVITIES = new EnumMap<>(Map.of(
            ShipBrainActivityResolver.Mode.COMMAND, Activity.WORK,
            ShipBrainActivityResolver.Mode.GUARD, Activity.MEET,
            ShipBrainActivityResolver.Mode.FOLLOW, Activity.PLAY,
            ShipBrainActivityResolver.Mode.COMBAT, Activity.FIGHT,
            ShipBrainActivityResolver.Mode.IDLE, Activity.IDLE
    ));

    private EntityShipBrainAi() {
    }

    static Brain<?> makeBrain(EntityShipBase ship, Brain<EntityShipBase> brain) {
        initCoreActivity(brain);
        initCommandActivity(brain);
        initGuardActivity(brain);
        initFollowActivity(brain);
        initCombatActivity(brain);
        initIdleActivity(brain);
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    @SuppressWarnings("unchecked")
    static void tick(ServerLevel level, EntityShipBase ship) {
        Brain<EntityShipBase> brain = (Brain<EntityShipBase>) ship.getBrain();
        syncShipStateMemory(ship, brain);
        logServerBrainTickIfNeeded(ship, brain);
        syncAttackTargetMemory(ship, brain);
        brain.tick(level, ship);
        brain.setActiveActivityToFirstValid(resolveActiveActivities(
                activityState(ship, brain), brain.isActive(activityFor(ShipBrainActivityResolver.Mode.FOLLOW))));
    }

    private static ImmutableList<Activity> resolveActiveActivities(ShipBrainActivityResolver.State state, boolean following) {
        ImmutableList.Builder<Activity> activities = ImmutableList.builder();
        for (ShipBrainActivityResolver.Mode mode : ShipBrainActivityResolver.resolveActiveModes(state, following)) {
            activities.add(activityFor(mode));
        }
        return activities.build();
    }

    private static Activity activityFor(ShipBrainActivityResolver.Mode mode) {
        return MODE_ACTIVITIES.get(mode);
    }

    private static ShipBrainActivityResolver.State activityState(EntityShipBase ship) {
        return activityState(ship, typedBrain(ship));
    }

    private static ShipBrainActivityResolver.State activityState(EntityShipBase ship, Brain<EntityShipBase> brain) {
        ShipBrainMemory.PointerTargetMemory pointerMemory = pointerTargetMemory(brain);
        ShipBrainMemory.GuardTargetMemory guardMemory = guardTargetMemory(brain);
        ShipBrainMemory.FollowStateMemory followMemory = followStateMemory(brain);
        return new ShipBrainActivityResolver.State(
                canMove(ship),
                pointerMemory.hasAnyTarget(),
                ship.getTarget() != null,
                guardMemory.canGuard(),
                followMemory.shouldFollow(),
                followMemory.ownerPresent(),
                followMemory.ownerHasCombatRation(),
                followMemory.ownerDistanceSq(),
                followMemory.followMinConfig(),
                followMemory.followMaxConfig()
        );
    }

    private static Brain<EntityShipBase> typedBrain(EntityShipBase ship) {
        @SuppressWarnings("unchecked")
        Brain<EntityShipBase> brain = (Brain<EntityShipBase>) ship.getBrain();
        return brain;
    }

    private static void syncShipStateMemory(EntityShipBase ship, Brain<EntityShipBase> brain) {
        brain.setMemory(ModMemoryModules.SHIP_POINTER_TARGET.get(), ShipBrainMemory.pointerTarget(ship));
        brain.setMemory(ModMemoryModules.SHIP_GUARD_TARGET.get(), ShipBrainMemory.guardTarget(ship));
        brain.setMemory(ModMemoryModules.SHIP_FOLLOW_STATE.get(), ShipBrainMemory.followState(ship));
    }

    private static ShipBrainMemory.PointerTargetMemory pointerTargetMemory(EntityShipBase ship) {
        return pointerTargetMemory(typedBrain(ship));
    }

    private static ShipBrainMemory.PointerTargetMemory pointerTargetMemory(Brain<EntityShipBase> brain) {
        return brain.getMemory(ModMemoryModules.SHIP_POINTER_TARGET.get())
                .orElse(new ShipBrainMemory.PointerTargetMemory(false, null, null, 0L, false, null, false, null, 0L,
                        -1.0D, 0.0D, 0.0D, false, false, false, ShipAiNumbers.POINTER_ENTITY_ATTACK_RANGE_SQR, false, false));
    }

    private static ShipBrainMemory.GuardTargetMemory guardTargetMemory(EntityShipBase ship) {
        return guardTargetMemory(typedBrain(ship));
    }

    private static ShipBrainMemory.GuardTargetMemory guardTargetMemory(Brain<EntityShipBase> brain) {
        return brain.getMemory(ModMemoryModules.SHIP_GUARD_TARGET.get())
                .orElse(new ShipBrainMemory.GuardTargetMemory(ShipGuardTarget.NONE, false, false, null, false, null, null, 0));
    }

    private static ShipBrainMemory.FollowStateMemory followStateMemory(EntityShipBase ship) {
        return followStateMemory(typedBrain(ship));
    }

    private static ShipBrainMemory.FollowStateMemory followStateMemory(Brain<EntityShipBase> brain) {
        return brain.getMemory(ModMemoryModules.SHIP_FOLLOW_STATE.get())
                .orElse(new ShipBrainMemory.FollowStateMemory(false, "memoryMissing", false, null, null, 0.0D, 0, false, -1.0D, 0, 0));
    }

    private static ShipBrainMemory.PassiveCombatStateMemory passiveCombatStateMemory(EntityShipBase ship) {
        return passiveCombatStateMemory(typedBrain(ship));
    }

    private static ShipBrainMemory.PassiveCombatStateMemory passiveCombatStateMemory(Brain<EntityShipBase> brain) {
        return brain.getMemory(ModMemoryModules.SHIP_PASSIVE_COMBAT_STATE.get())
                .orElse(ShipBrainMemory.noPassiveCombatState());
    }

    private static void syncAttackTargetMemory(EntityShipBase ship, Brain<EntityShipBase> brain) {
        LivingEntity target = ship.getTarget();
        if (target != null && target.isAlive()) {
            brain.setMemory(MemoryModuleType.ATTACK_TARGET, target);
        } else {
            brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
        }
    }

    private static void syncRecoveryMemory(EntityShipBase ship, MemoryModuleType<ShipBrainMemory.RecoveryStateMemory> memoryType,
                                           ShipMovementRecoveryState recovery, int stuckLimit) {
        typedBrain(ship).setMemory(memoryType,
                ShipBrainMemory.recoveryState(recovery, recovery.isStuckLongerThan(stuckLimit)));
    }

    private static void clearRecoveryMemory(EntityShipBase ship, MemoryModuleType<ShipBrainMemory.RecoveryStateMemory> memoryType) {
        typedBrain(ship).eraseMemory(memoryType);
    }

    private static void logServerBrainTickIfNeeded(EntityShipBase ship, Brain<EntityShipBase> brain) {
        if (ship.tickCount % 40 != 0) {
            return;
        }
        ShipBrainMemory.PointerTargetMemory pointerMemory = pointerTargetMemory(brain);
        ShipBrainMemory.GuardTargetMemory guardMemory = guardTargetMemory(brain);
        ShipBrainMemory.FollowStateMemory followMemory = followStateMemory(brain);
        if (ship.getOwnerUUID() == null && ship.getTarget() == null && !pointerMemory.hasAnyTarget()
                && !guardMemory.target().isActive()) {
            return;
        }
        Shincolle.diagnosticLog(
                "[SCBrainDiag] serverTick ship={} activity={} ownerUuid={} ownerPresent={} tame={} deadPose={} noFuel={} shouldFollow={} reason={} distSq={} target={} pointer={} guard={}",
                ship.getUUID(),
                describeDesiredActivity(ship, brain),
                ship.getOwnerUUID(),
                followMemory.ownerPresent(),
                ship.isTame(),
                ship.isInDeadPose(),
                ship.isNoFuel(),
                followMemory.shouldFollow(),
                followMemory.blockReason(),
                followMemory.ownerDistanceSq(),
                ship.getTarget() != null,
                pointerMemory.hasAnyTarget(),
                guardMemory.target().isActive());
    }

    private static String describeDesiredActivity(EntityShipBase ship) {
        return describeDesiredActivity(ship, typedBrain(ship));
    }

    private static String describeDesiredActivity(EntityShipBase ship, Brain<EntityShipBase> brain) {
        return ShipBrainActivityResolver.describeDesiredActivity(activityState(ship, brain));
    }

    private static void initCoreActivity(Brain<EntityShipBase> brain) {
        brain.addActivity(Activity.CORE, ImmutableList.of(
                Pair.of(ShipAiNumbers.CORE_DIAGNOSTIC_PRIORITY, new ShipBrainDiagnosticBehavior()),
                Pair.of(ShipAiNumbers.CORE_PASSIVE_TARGETING_PRIORITY, new ShipPassiveCombatTargetingBehavior()),
                Pair.of(ShipAiNumbers.LOOK_AT_PLAYER_PRIORITY, new ShipLookAtPlayerBehavior()),
                Pair.of(ShipAiNumbers.RANDOM_LOOK_PRIORITY, new ShipRandomLookAroundBehavior())
        ));
    }

    private static void initCommandActivity(Brain<EntityShipBase> brain) {
        brain.addActivity(activityFor(ShipBrainActivityResolver.Mode.COMMAND), ImmutableList.of(
                Pair.of(ShipAiNumbers.COMMAND_POINTER_PRIORITY, new ShipPointerMoveBehavior())
        ));
    }

    private static void initGuardActivity(Brain<EntityShipBase> brain) {
        brain.addActivity(activityFor(ShipBrainActivityResolver.Mode.GUARD), ImmutableList.of(
                Pair.of(ShipAiNumbers.GUARD_MOVE_PRIORITY, new ShipGuardMoveBehavior())
        ));
    }

    private static void initFollowActivity(Brain<EntityShipBase> brain) {
        brain.addActivity(activityFor(ShipBrainActivityResolver.Mode.FOLLOW), ImmutableList.of(
                Pair.of(ShipAiNumbers.FOLLOW_OWNER_PRIORITY, new ShipFollowOwnerBehavior())
        ));
    }

    private static void initCombatActivity(Brain<EntityShipBase> brain) {
        brain.addActivity(activityFor(ShipBrainActivityResolver.Mode.COMBAT), ImmutableList.of(
                Pair.of(ShipAiNumbers.COMBAT_MEMORY_PRIORITY, new ShipCombatMemoryBehavior())
        ));
    }

    private static void initIdleActivity(Brain<EntityShipBase> brain) {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(ShipAiNumbers.RANDOM_STROLL_PRIORITY, new ShipRandomStrollBehavior())
        ));
    }

    private static boolean canMove(EntityShipBase ship) {
        return !ship.isOrderedToSit()
                && !ship.isInSittingPose()
                && !ship.isPassenger()
                && !ship.isVehicle()
                && !ship.isInDeadPose();
    }

    private static boolean shouldFollowOwner(EntityShipBase ship, boolean following) {
        return ShipBrainActivityResolver.shouldFollowOwner(activityState(ship), following);
    }

    private static float resolveFollowMinDistance(ShipBrainMemory.FollowStateMemory followMemory) {
        return ShipBrainActivityResolver.resolveFollowMinDistance(
                followMemory.followMinConfig());
    }

    private static float resolveFollowMaxDistance(ShipBrainMemory.FollowStateMemory followMemory, float minDist) {
        return ShipBrainActivityResolver.resolveFollowMaxDistance(
                followMemory.followMaxConfig(), minDist);
    }

    private static boolean hasSameDimensionOwnerPosition(ServerLevel level, ShipBrainMemory.FollowStateMemory followMemory) {
        return followMemory.ownerPresent()
                && followMemory.ownerPos() != null
                && followMemory.ownerDimensionId() == EntityShipBase.getLegacyDimensionId(level);
    }

    private static ShipGuardDecisionResolver.State guardResolverState(ShipGuardTarget guardTarget,
                                                                      ShipBrainMemory.GuardTargetMemory guardMemory,
                                                                      Entity guardedEntity,
                                                                      double distanceSqr,
                                                                      boolean summoning) {
        return new ShipGuardDecisionResolver.State(
                guardTarget.isEntity(),
                guardMemory.hasLiveEntityTarget(),
                guardMemory.hasBlockTarget(),
                guardedEntity != null,
                distanceSqr,
                summoning,
                guardMemory.dimensionId(),
                guardedEntity == null ? Integer.MIN_VALUE : EntityShipBase.getLegacyDimensionId(guardedEntity.level())
        );
    }

    private static ShipBrainActivityResolver.FollowState followResolverState(ServerLevel level,
                                                                             EntityShipBase ship,
                                                                             ShipBrainMemory.FollowStateMemory followMemory) {
        return new ShipBrainActivityResolver.FollowState(
                canMove(ship),
                followMemory.shouldFollow(),
                hasSameDimensionOwnerPosition(level, followMemory),
                followMemory.ownerHasCombatRation(),
                followMemory.ownerDistanceSq(),
                followMemory.followMinConfig()
        );
    }

    private static void setPointWalkAndLookMemory(EntityShipBase ship, Vec3 target, double speed, int closeEnoughDist) {
        BehaviorUtils.setWalkAndLookTargetMemories(ship, BlockPos.containing(target), (float) speed, closeEnoughDist);
    }

    private static void setEntityWalkAndLookMemory(EntityShipBase ship, Entity target, double speed, int closeEnoughDist) {
        if (target instanceof LivingEntity livingEntity) {
            BehaviorUtils.setWalkAndLookTargetMemories(ship, livingEntity, (float) speed, closeEnoughDist);
            return;
        }
        setPointWalkAndLookMemory(ship, target.position(), speed, closeEnoughDist);
    }

    private static void setEntityLookMemory(EntityShipBase ship, LivingEntity target) {
        BehaviorUtils.lookAtEntity(ship, target);
    }

    private static void clearWalkAndLookMemory(EntityShipBase ship) {
        Brain<?> brain = ship.getBrain();
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
        brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    private static int closeEnoughDistance(double distanceSq) {
        return Math.max(1, Mth.ceil(Math.sqrt(distanceSq)));
    }

    private static final class ShipBrainDiagnosticBehavior extends Behavior<EntityShipBase> {
        private int nextBrainLogTick;
        private int nextCanStillUseLogTick;

        ShipBrainDiagnosticBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntityShipBase ship) {
            return !ship.isInDeadPose();
        }

        @Override
        protected void start(ServerLevel level, EntityShipBase ship, long gameTime) {
            ShipBrainMemory.FollowStateMemory followMemory = followStateMemory(ship);
            Shincolle.diagnosticLog(
                    "[SCBrainDiag] brainStart ship={} gameTime={} tick={} activity={} ownerUuid={} tame={} deadPose={} shouldFollow={} reason={}",
                    ship.getUUID(),
                    gameTime,
                    ship.tickCount,
                    describeDesiredActivity(ship),
                    ship.getOwnerUUID(),
                    ship.isTame(),
                    ship.isInDeadPose(),
                    followMemory.shouldFollow(),
                    followMemory.blockReason());
        }

        @Override
        protected boolean canStillUse(ServerLevel level, EntityShipBase ship, long gameTime) {
            boolean canStillUse = !ship.isInDeadPose();
            if (!canStillUse || ship.tickCount >= this.nextCanStillUseLogTick) {
                this.nextCanStillUseLogTick = ship.tickCount + 40;
                ShipBrainMemory.FollowStateMemory followMemory = followStateMemory(ship);
                Shincolle.diagnosticLog(
                        "[SCBrainDiag] brainCanStillUse ship={} canStillUse={} gameTime={} tick={} activity={} deadPose={} ownerUuid={} shouldFollow={} reason={}",
                        ship.getUUID(),
                        canStillUse,
                        gameTime,
                        ship.tickCount,
                        describeDesiredActivity(ship),
                        ship.isInDeadPose(),
                        ship.getOwnerUUID(),
                        followMemory.shouldFollow(),
                        followMemory.blockReason());
            }
            return canStillUse;
        }

        @Override
        protected boolean timedOut(long gameTime) {
            return false;
        }

        @Override
        protected void tick(ServerLevel level, EntityShipBase ship, long gameTime) {
            if (ship.tickCount < this.nextBrainLogTick) {
                return;
            }
            this.nextBrainLogTick = ship.tickCount + 40;
            ShipBrainMemory.PointerTargetMemory pointerMemory = pointerTargetMemory(ship);
            ShipBrainMemory.GuardTargetMemory guardMemory = guardTargetMemory(ship);
            ShipBrainMemory.FollowStateMemory followMemory = followStateMemory(ship);
            Shincolle.diagnosticLog(
                    "[SCBrainDiag] brainTick ship={} activity={} ownerUuid={} ownerPresent={} tame={} canMove={} shouldFollow={} reason={} distSq={} pointer={} guard={} target={}",
                    ship.getUUID(),
                    describeDesiredActivity(ship),
                    ship.getOwnerUUID(),
                    followMemory.ownerPresent(),
                    ship.isTame(),
                    canMove(ship),
                    followMemory.shouldFollow(),
                    followMemory.blockReason(),
                    followMemory.ownerDistanceSq(),
                    pointerMemory.hasAnyTarget(),
                    guardMemory.target().isActive(),
                    ship.getTarget() != null);
        }

        @Override
        protected void stop(ServerLevel level, EntityShipBase ship, long gameTime) {
            ShipBrainMemory.FollowStateMemory followMemory = followStateMemory(ship);
            Shincolle.diagnosticLog(
                    "[SCBrainDiag] brainStop ship={} gameTime={} tick={} activity={} ownerUuid={} tame={} deadPose={} shouldFollow={} reason={}",
                    ship.getUUID(),
                    gameTime,
                    ship.tickCount,
                    describeDesiredActivity(ship),
                    ship.getOwnerUUID(),
                    ship.isTame(),
                    ship.isInDeadPose(),
                    followMemory.shouldFollow(),
                    followMemory.blockReason());
        }
    }

    private static final class ShipCombatMemoryBehavior extends Behavior<EntityShipBase> {
        private final ShipMovementRecoveryState combatRecovery = new ShipMovementRecoveryState();
        private int nextCombatPathTick;
        private UUID lastCombatTargetId;

        ShipCombatMemoryBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntityShipBase ship) {
            return ship.getTarget() != null && !ship.isInDeadPose();
        }

        @Override
        protected boolean canStillUse(ServerLevel level, EntityShipBase ship, long gameTime) {
            return ship.getTarget() != null && !ship.isInDeadPose();
        }

        @Override
        protected void tick(ServerLevel level, EntityShipBase ship, long gameTime) {
            ShipBrainMemory.PassiveCombatStateMemory state = ship.updatePassiveCombatStateBrain();
            ship.getBrain().setMemory(ModMemoryModules.SHIP_PASSIVE_COMBAT_STATE.get(), state);
            state = passiveCombatStateMemory(ship);
            LivingEntity target = ship.getTarget();
            if (target != null && target.isAlive()) {
                ship.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
            } else {
                ship.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                clearCombatMoveState(ship);
                return;
            }

            if (!state.hasTarget()) {
                clearCombatMoveState(ship);
                return;
            }
            if (state.shouldChase()) {
                tickPassiveCombatChase(ship, target, state);
                return;
            }

            this.combatRecovery.reset(ship.position());
            syncCombatRecoveryMemory(ship);
            ship.combatMovementCoordinator().stop();
            if (!state.needsMovement()) {
                ship.tickPassiveCombatActionsBrain(state);
            }
        }

        @Override
        protected void stop(ServerLevel level, EntityShipBase ship, long gameTime) {
            if (ship.getTarget() == null || !ship.getTarget().isAlive()) {
                ship.clearPassiveCombatTargetBrain(true);
            }
            ship.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            ship.getBrain().eraseMemory(ModMemoryModules.SHIP_PASSIVE_COMBAT_STATE.get());
            clearCombatMoveState(ship);
        }

        private void tickPassiveCombatChase(EntityShipBase ship, LivingEntity target,
                                            ShipBrainMemory.PassiveCombatStateMemory state) {
            if (!Objects.equals(this.lastCombatTargetId, state.targetId())) {
                this.nextCombatPathTick = 0;
                this.lastCombatTargetId = state.targetId();
                this.combatRecovery.reset(ship.position());
                ship.combatMovementCoordinator().reset();
                Shincolle.debugLog("[SCMoveDiag] PassiveCombat targetChanged ship={} target={}",
                        ship.getUUID(), state.targetId());
            }

            setEntityWalkAndLookMemory(ship, target, state.moveSpeed(), 1);
            this.combatRecovery.trackProgress(ship.position());
            syncCombatRecoveryMemory(ship);
            if (ShipRecoveryDecisionResolver.shouldClearAfterStuck(
                    this.combatRecovery.stuckTicks(), ShipAiNumbers.PASSIVE_COMBAT_STUCK_TICK_LIMIT)) {
                if (tryPassiveCombatTeleportRecovery(ship, target, state.distanceSqr(), true)) {
                    return;
                }
                Shincolle.debugLog("[SCMoveDiag] PassiveCombat stuckClear ship={} target={} stuckTicks={} distanceSqr={}",
                        ship.getUUID(), target.getUUID(), this.combatRecovery.stuckTicks(), state.distanceSqr());
                ship.clearPassiveCombatTargetBrain(true);
                clearCombatMoveState(ship);
                return;
            }

            if (this.nextCombatPathTick-- <= 0 || ship.combatMovementCoordinator().isNavigationDone()) {
                this.nextCombatPathTick = ShipAiNumbers.PASSIVE_COMBAT_PATH_RECALC_INTERVAL;
                if (tryPassiveCombatTeleportRecovery(ship, target, state.distanceSqr(), false)) {
                    return;
                }
                ShipMovementCoordinator movement = ship.combatMovementCoordinator();
                if (!movement.moveTo(target, state.moveSpeed())) {
                    int failCount = this.combatRecovery.recordMoveFailure();
                    syncCombatRecoveryMemory(ship);
                    if (this.combatRecovery.shouldLogMoveFailure(ship.tickCount,
                            ShipAiNumbers.PASSIVE_COMBAT_MOVE_FAIL_LOG_INTERVAL)) {
                        Shincolle.debugLog("[SCMoveDiag] PassiveCombat moveFail ship={} target={} failCount={} distanceSqr={}",
                                ship.getUUID(), target.getUUID(), failCount, state.distanceSqr());
                    }
                    if (ShipRecoveryDecisionResolver.shouldClearAfterMoveFailures(
                            failCount, ShipAiNumbers.PASSIVE_COMBAT_MOVE_FAIL_LIMIT)) {
                        if (tryPassiveCombatTeleportRecovery(ship, target, state.distanceSqr(), true)) {
                            return;
                        }
                        Shincolle.debugLog("[SCMoveDiag] PassiveCombat failClear ship={} target={} failCount={}",
                                ship.getUUID(), target.getUUID(), this.combatRecovery.moveFailCount());
                        ship.clearPassiveCombatTargetBrain(true);
                        clearCombatMoveState(ship);
                    }
                    this.nextCombatPathTick = 2;
                } else {
                    this.combatRecovery.clearMoveFailures();
                    syncCombatRecoveryMemory(ship);
                }
            }
        }

        private boolean tryPassiveCombatTeleportRecovery(EntityShipBase ship, LivingEntity target,
                                                         double distanceSqr, boolean force) {
            ShipRecoveryDecisionResolver.State recoveryState =
                    new ShipRecoveryDecisionResolver.State(
                            force,
                            distanceSqr,
                            ShipAiNumbers.PASSIVE_COMBAT_TELEPORT_DISTANCE_SQ);
            if (!ShipRecoveryDecisionResolver.shouldAttemptTeleport(recoveryState)) {
                return false;
            }
            if (!this.combatRecovery.shouldTryTeleportThrottled(force, distanceSqr,
                    ShipAiNumbers.PASSIVE_COMBAT_TELEPORT_DISTANCE_SQ,
                    ShipAiNumbers.PASSIVE_COMBAT_TELEPORT_COOLDOWN_TICKS)) {
                return false;
            }
            if (!ship.combatMovementCoordinator().teleportNearLiving(target, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)) {
                return false;
            }

            Shincolle.debugLog("[SCMoveDiag] PassiveCombat teleportRecovery ship={} target={} force={} distanceSqr={}",
                    ship.getUUID(), target.getUUID(), force, distanceSqr);
            this.nextCombatPathTick = 0;
            this.combatRecovery.reset(ship.position());
            syncCombatRecoveryMemory(ship);
            return true;
        }

        private void clearCombatMoveState(EntityShipBase ship) {
            this.nextCombatPathTick = 0;
            this.lastCombatTargetId = null;
            this.combatRecovery.clear();
            clearRecoveryMemory(ship, ModMemoryModules.SHIP_COMBAT_RECOVERY.get());
            clearWalkAndLookMemory(ship);
            ship.combatMovementCoordinator().stop();
        }

        private void syncCombatRecoveryMemory(EntityShipBase ship) {
            syncRecoveryMemory(ship, ModMemoryModules.SHIP_COMBAT_RECOVERY.get(),
                    this.combatRecovery, ShipAiNumbers.PASSIVE_COMBAT_STUCK_TICK_LIMIT);
        }
    }

    private static final class ShipPassiveCombatTargetingBehavior extends Behavior<EntityShipBase> {
        ShipPassiveCombatTargetingBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntityShipBase ship) {
            return !ship.isNoFuel() && !ship.hasPointerTargetEntity() && !ship.isInDeadPose();
        }

        @Override
        protected boolean canStillUse(ServerLevel level, EntityShipBase ship, long gameTime) {
            return !ship.isNoFuel() && !ship.hasPointerTargetEntity() && !ship.isInDeadPose();
        }

        @Override
        protected boolean timedOut(long gameTime) {
            return false;
        }

        @Override
        protected void tick(ServerLevel level, EntityShipBase ship, long gameTime) {
            ship.tickPassiveCombatTargetingBrain();
        }

        @Override
        protected void stop(ServerLevel level, EntityShipBase ship, long gameTime) {
            if (ship.isNoFuel() || ship.hasPointerTargetEntity() || ship.isInDeadPose()) {
                ship.clearPassiveCombatTargetBrain(true);
            }
        }
    }

    private static final class ShipPointerMoveBehavior extends Behavior<EntityShipBase> {
        private final ShipMovementRecoveryState pointerRecovery = new ShipMovementRecoveryState();
        private int nextPointerPathTick;
        private int pointerEntityMeleeAttackTick;
        private int pointerEntityLightShotTick;
        private int pointerEntityHeavyShotTick;
        private Vec3 lastRawPointerTarget;
        private UUID lastPointerEntityTargetId;

        ShipPointerMoveBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntityShipBase ship) {
            return pointerTargetMemory(ship).hasAnyTarget() && canMove(ship);
        }

        @Override
        protected boolean canStillUse(ServerLevel level, EntityShipBase ship, long gameTime) {
            return pointerTargetMemory(ship).hasAnyTarget() && canMove(ship);
        }

        @Override
        protected boolean timedOut(long gameTime) {
            return false;
        }

        @Override
        protected void tick(ServerLevel level, EntityShipBase ship, long gameTime) {
            ShipBrainMemory.PointerTargetMemory pointerMemory = pointerTargetMemory(ship);
            if (pointerMemory.entityTargetAlive() && pointerMemory.entityTargetPos() != null) {
                tickPointerEntityMove(ship, pointerMemory);
                return;
            }

            Vec3 rawTarget = pointerMemory.rawPointTarget();
            Vec3 target = pointerMemory.adjustedPointTarget();
            if (rawTarget == null || target == null) {
                clearPointerMoveState(ship);
                return;
            }

            if (this.lastRawPointerTarget == null || rawTarget.distanceToSqr(this.lastRawPointerTarget) > ShipAiNumbers.TARGET_SWITCH_DISTANCE_SQ) {
                this.nextPointerPathTick = 0;
                this.lastRawPointerTarget = rawTarget;
                this.lastPointerEntityTargetId = null;
                this.pointerRecovery.reset(ship.position());
                ship.pointerMovementCoordinator().reset();
                Shincolle.debugLog("ShipBrain pointerTargetChanged ship={} target={}", ship.getUUID(), target);
            }

            if (ship.distanceToSqr(target) <= ShipAiNumbers.POINTER_MOVE_REACH_SQR) {
                clearPointerMoveState(ship);
                return;
            }

            setPointWalkAndLookMemory(ship, target, ShipAiNumbers.POINTER_MOVE_SPEED,
                    closeEnoughDistance(ShipAiNumbers.POINTER_MOVE_REACH_SQR));
            ship.resetInteractionEmotionState();
            this.pointerRecovery.trackProgress(ship.position());
            syncPointerRecoveryMemory(ship);
            if (this.pointerRecovery.isStuckLongerThan(ShipAiNumbers.MOVE_STUCK_TICK_LIMIT)) {
                if (tryPointerTeleportRecovery(ship, target, true)) {
                    return;
                }
                Shincolle.debugLog("[SCMoveDiag] PointerGoal stuckClear ship={} target={} stuckTicks={}",
                        ship.getUUID(), target, this.pointerRecovery.stuckTicks());
                ship.clearPointerTarget();
                clearPointerMoveState(ship);
                return;
            }
            if (tryPointerTeleportRecovery(ship, target, false)) {
                return;
            }

            if (this.nextPointerPathTick-- <= 0) {
                this.nextPointerPathTick = ShipAiNumbers.PATH_RECALC_INTERVAL_TICKS;
                ShipMovementCoordinator movement = ship.pointerMovementCoordinator();
                if (!movement.moveTo(target, ShipAiNumbers.POINTER_MOVE_SPEED)) {
                    int failCount = this.pointerRecovery.recordMoveFailure();
                    syncPointerRecoveryMemory(ship);
                    if (this.pointerRecovery.shouldLogMoveFailure(ship.tickCount, ShipAiNumbers.MOVE_FAIL_LOG_INTERVAL)) {
                        Shincolle.debugLog("[SCMoveDiag] PointerGoal moveFail ship={} target={} failCount={}",
                                ship.getUUID(), target, failCount);
                    }
                    if (failCount > ShipAiNumbers.MOVE_FAIL_LIMIT) {
                        if (tryPointerTeleportRecovery(ship, target, true)) {
                            return;
                        }
                        Shincolle.debugLog("[SCMoveDiag] PointerGoal failClear ship={} target={} failCount={}",
                                ship.getUUID(), target, this.pointerRecovery.moveFailCount());
                        ship.clearPointerTarget();
                        clearPointerMoveState(ship);
                    }
                } else {
                    this.pointerRecovery.clearMoveFailures();
                    syncPointerRecoveryMemory(ship);
                    Shincolle.debugLog("ShipBrain pointerMoveOk ship={} target={}", ship.getUUID(), target);
                }
            }
        }

        @Override
        protected void stop(ServerLevel level, EntityShipBase ship, long gameTime) {
            clearPointerMoveState(ship);
        }

        private void tickPointerEntityMove(EntityShipBase ship, ShipBrainMemory.PointerTargetMemory pointerMemory) {
            Entity target = ship.getPointerTargetEntity();
            if (target == null || !target.isAlive()) {
                ship.clearPointerTargetEntity();
                clearPointerMoveState(ship);
                return;
            }
            if (!Objects.equals(this.lastPointerEntityTargetId, pointerMemory.entityTargetId())) {
                this.nextPointerPathTick = 0;
                this.lastRawPointerTarget = null;
                this.lastPointerEntityTargetId = pointerMemory.entityTargetId();
                this.pointerRecovery.reset(ship.position());
                resetPointerEntityAttackCadence(ship);
                ship.pointerMovementCoordinator().reset();
                Shincolle.debugLog("[SCMoveDiag] PointerEntity targetChanged ship={} target={}",
                        ship.getUUID(), pointerMemory.entityTargetId());
            }

            if (!pointerMemory.entityShouldChase()) {
                this.nextPointerPathTick = 0;
                this.pointerRecovery.reset(ship.position());
                syncPointerEntityRecoveryMemory(ship);
                ship.pointerMovementCoordinator().stop();
                clearWalkAndLookMemory(ship);
                if (target instanceof LivingEntity livingTarget) {
                    setEntityLookMemory(ship, livingTarget);
                } else {
                    ship.getLookControl().setLookAt(target.getX(), target.getY(), target.getZ(),
                            ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH);
                }
                tickPointerEntityAttacks(ship, target, pointerMemory);
                return;
            }

            setEntityWalkAndLookMemory(ship, target, ShipAiNumbers.POINTER_ENTITY_MOVE_SPEED, 1);
            double distanceSqr = pointerMemory.entityDistanceSqr();
            this.pointerRecovery.trackProgress(ship.position());
            syncPointerEntityRecoveryMemory(ship);
            if (ShipRecoveryDecisionResolver.shouldClearAfterStuck(
                    this.pointerRecovery.stuckTicks(), ShipAiNumbers.POINTER_ENTITY_STUCK_TICK_LIMIT)) {
                if (tryPointerEntityTeleportRecovery(ship, target, distanceSqr, true)) {
                    return;
                }
                Shincolle.debugLog("[SCMoveDiag] PointerEntity stuckClear ship={} target={} stuckTicks={} distanceSqr={}",
                        ship.getUUID(), target.getUUID(), this.pointerRecovery.stuckTicks(), distanceSqr);
                ship.clearPointerTargetEntity();
                clearPointerMoveState(ship);
                return;
            }
            if (this.nextPointerPathTick-- <= 0 || ship.pointerMovementCoordinator().isNavigationDone()) {
                this.nextPointerPathTick = ShipAiNumbers.POINTER_ENTITY_PATH_RECALC_INTERVAL;
                if (tryPointerEntityTeleportRecovery(ship, target, distanceSqr, false)) {
                    return;
                }
                ShipMovementCoordinator movement = ship.pointerMovementCoordinator();
                if (!movement.moveTo(target, ShipAiNumbers.POINTER_ENTITY_MOVE_SPEED)) {
                    int failCount = this.pointerRecovery.recordMoveFailure();
                    syncPointerEntityRecoveryMemory(ship);
                    if (this.pointerRecovery.shouldLogMoveFailure(ship.tickCount,
                            ShipAiNumbers.POINTER_ENTITY_MOVE_FAIL_LOG_INTERVAL)) {
                        Shincolle.debugLog("[SCMoveDiag] PointerEntity moveFail ship={} target={} failCount={} distanceSqr={}",
                                ship.getUUID(), target.getUUID(), failCount, distanceSqr);
                    }
                    if (ShipRecoveryDecisionResolver.shouldClearAfterMoveFailures(
                            failCount, ShipAiNumbers.POINTER_ENTITY_MOVE_FAIL_LIMIT)) {
                        if (tryPointerEntityTeleportRecovery(ship, target, distanceSqr, true)) {
                            return;
                        }
                        Shincolle.debugLog("[SCMoveDiag] PointerEntity failClear ship={} target={} failCount={}",
                                ship.getUUID(), target.getUUID(), this.pointerRecovery.moveFailCount());
                        ship.clearPointerTargetEntity();
                        clearPointerMoveState(ship);
                    }
                    this.nextPointerPathTick = 2;
                } else {
                    this.pointerRecovery.clearMoveFailures();
                    syncPointerEntityRecoveryMemory(ship);
                }
            }
        }

        private boolean tryPointerTeleportRecovery(EntityShipBase ship, Vec3 target, boolean force) {
            if (!this.pointerRecovery.shouldTryTeleportThrottled(force, ship.distanceToSqr(target),
                    ShipAiNumbers.TELEPORT_DISTANCE_SQ, ShipAiNumbers.TELEPORT_COOLDOWN_TICKS)) {
                return false;
            }
            if (!ship.pointerMovementCoordinator().teleportNearPoint(target, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)) {
                return false;
            }
            Shincolle.debugLog("[SCMoveDiag] PointerGoal teleportRecovery ship={} target={} force={}",
                    ship.getUUID(), target, force);
            this.nextPointerPathTick = 0;
            this.pointerRecovery.reset(ship.position());
            syncPointerEntityRecoveryMemory(ship);
            return true;
        }

        private boolean tryPointerEntityTeleportRecovery(EntityShipBase ship, Entity target, double distanceSqr, boolean force) {
            ShipRecoveryDecisionResolver.State recoveryState =
                    new ShipRecoveryDecisionResolver.State(
                            force,
                            distanceSqr,
                            ShipAiNumbers.POINTER_ENTITY_TELEPORT_DISTANCE_SQ);
            if (!ShipRecoveryDecisionResolver.shouldAttemptTeleport(recoveryState)) {
                return false;
            }
            if (!this.pointerRecovery.shouldTryTeleportThrottled(force, distanceSqr,
                    ShipAiNumbers.POINTER_ENTITY_TELEPORT_DISTANCE_SQ,
                    ShipAiNumbers.POINTER_ENTITY_TELEPORT_COOLDOWN_TICKS)) {
                return false;
            }
            ShipMovementCoordinator movement = ship.pointerMovementCoordinator();
            boolean teleported = target instanceof LivingEntity livingTarget
                    ? movement.teleportNearLiving(livingTarget, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)
                    : movement.teleportNearPoint(target.position(), ShipAiNumbers.TELEPORT_VERTICAL_OFFSET);
            if (!teleported) {
                return false;
            }
            Shincolle.debugLog("[SCMoveDiag] PointerEntity teleportRecovery ship={} target={} force={}",
                    ship.getUUID(), target.getUUID(), force);
            this.nextPointerPathTick = 0;
            this.pointerRecovery.reset(ship.position());
            syncPointerRecoveryMemory(ship);
            return true;
        }

        private void clearPointerMoveState(EntityShipBase ship) {
            this.nextPointerPathTick = 0;
            this.lastRawPointerTarget = null;
            this.lastPointerEntityTargetId = null;
            this.pointerEntityMeleeAttackTick = 0;
            this.pointerEntityLightShotTick = 0;
            this.pointerEntityHeavyShotTick = 0;
            this.pointerRecovery.clear();
            clearRecoveryMemory(ship, ModMemoryModules.SHIP_POINTER_RECOVERY.get());
            clearWalkAndLookMemory(ship);
            ship.pointerMovementCoordinator().stop();
        }

        private void resetPointerEntityAttackCadence(EntityShipBase ship) {
            int aimDelay = ShipPointerEntityDecisionResolver.aimDelayTicks(ship.getLevel());
            this.pointerEntityMeleeAttackTick = ship.tickCount + aimDelay;
            this.pointerEntityLightShotTick = ship.tickCount + aimDelay;
            this.pointerEntityHeavyShotTick = ship.tickCount + aimDelay;
            ship.getCombat().resetAircraftLaunchDelay();
        }

        private void tickPointerEntityAttacks(EntityShipBase ship, Entity target,
                                              ShipBrainMemory.PointerTargetMemory pointerMemory) {
            ship.getMoveControl().setWantedPosition(ship.getX(), ship.getY(), ship.getZ(), 0.0D);
            EntityShipBaseCombat combat = ship.getCombat();
            if (combat.hasAircraftAttackEnabled()) {
                combat.tryPerformAircraftCycle(target);
            }

            ShipPointerEntityDecisionResolver.AttackState attackState = new ShipPointerEntityDecisionResolver.AttackState(
                    ship.tickCount,
                    combat.canUseLightAmmo(),
                    ship.getLegacyShipStats().getLightDelay(),
                    this.pointerEntityLightShotTick,
                    combat.canUseHeavyAmmo(),
                    ship.getLegacyShipStats().getHeavyDelay(),
                    this.pointerEntityHeavyShotTick,
                    pointerMemory.entityCanMeleeAttack(),
                    pointerMemory.entityDistanceSqr(),
                    pointerMemory.entityAttackRangeSqr(),
                    ship.getLegacyShipStats().getMeleeDelay(),
                    this.pointerEntityMeleeAttackTick
            );

            if (ShipPointerEntityDecisionResolver.shouldFireLightAttack(attackState)) {
                this.pointerEntityLightShotTick = ship.tickCount;
                ship.performLightAttack(target);
            }

            if (ShipPointerEntityDecisionResolver.shouldFireHeavyAttack(attackState)) {
                if (ship.performHeavyAttack(target)) {
                    this.pointerEntityHeavyShotTick = ship.tickCount;
                }
            }

            if (ShipPointerEntityDecisionResolver.shouldFireMeleeAttack(attackState)) {
                this.pointerEntityMeleeAttackTick = ship.tickCount;
                ship.doHurtTarget(target);
            }
        }

        private void syncPointerRecoveryMemory(EntityShipBase ship) {
            syncRecoveryMemory(ship, ModMemoryModules.SHIP_POINTER_RECOVERY.get(),
                    this.pointerRecovery, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT);
        }

        private void syncPointerEntityRecoveryMemory(EntityShipBase ship) {
            syncRecoveryMemory(ship, ModMemoryModules.SHIP_POINTER_RECOVERY.get(),
                    this.pointerRecovery, ShipAiNumbers.POINTER_ENTITY_STUCK_TICK_LIMIT);
        }
    }

    private static final class ShipGuardMoveBehavior extends Behavior<EntityShipBase> {
        private final ShipMovementRecoveryState guardRecovery = new ShipMovementRecoveryState();
        private int nextGuardPathTick;
        private GuardRecoveryTargetKey lastGuardRecoveryTargetKey;

        ShipGuardMoveBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntityShipBase ship) {
            return guardTargetMemory(ship).canGuard();
        }

        @Override
        protected boolean canStillUse(ServerLevel level, EntityShipBase ship, long gameTime) {
            return guardTargetMemory(ship).canGuard();
        }

        @Override
        protected boolean timedOut(long gameTime) {
            return false;
        }

        @Override
        protected void tick(ServerLevel level, EntityShipBase ship, long gameTime) {
            ShipBrainMemory.GuardTargetMemory guardMemory = guardTargetMemory(ship);
            Entity guardedEntity = ship.getGuardedEntity();
            ShipGuardTarget guardTarget = guardMemory.target();

            int timer = ship.getStateTimer(ShipAiNumbers.GUARD_SUMMON_TIMER_ID);
            boolean isSummoning = timer > 0;
            if (isSummoning) {
                ship.setStateTimer(ShipAiNumbers.GUARD_SUMMON_TIMER_ID, timer - 1);
            }

            ShipGuardDecisionResolver.State unresolvedGuardState = guardResolverState(
                    guardTarget, guardMemory, guardedEntity, 0.0D, isSummoning);
            Vec3 target;
            if (guardMemory.hasLiveEntityTarget()) {
                if (ShipGuardDecisionResolver.shouldSyncEntityDimension(unresolvedGuardState)) {
                    ship.setGuardedPos(-1, -1, -1, EntityShipBase.getLegacyDimensionId(guardedEntity.level()),
                            ShipGuardTarget.Type.ENTITY.legacyId());
                }
                target = guardMemory.guardedEntityPos();
            } else if (guardMemory.hasBlockTarget()) {
                target = guardMemory.blockCenter();
            } else {
                clearGuardMoveState(ship);
                return;
            }

            resetGuardRecoveryIfTargetChanged(ship, guardTarget, guardedEntity);
            double distSq = ship.distanceToSqr(target);
            ShipGuardDecisionResolver.State guardState = guardResolverState(
                    guardTarget, guardMemory, guardedEntity, distSq, isSummoning);
            double stopDistanceSq = ShipGuardDecisionResolver.stopDistanceSqr(guardState);
            if (guardedEntity != null) {
                setEntityWalkAndLookMemory(ship, guardedEntity, ShipAiNumbers.GUARD_MOVE_SPEED,
                        closeEnoughDistance(stopDistanceSq));
            } else {
                setPointWalkAndLookMemory(ship, target, ShipAiNumbers.GUARD_MOVE_SPEED,
                        closeEnoughDistance(stopDistanceSq));
            }

            if (ShipGuardDecisionResolver.shouldMove(guardState)) {
                this.guardRecovery.trackProgress(ship.position());
                syncGuardRecoveryMemory(ship);
                if (tryGuardTeleportRecovery(ship, target, guardedEntity, distSq, false)) {
                    return;
                }
                if (ShipRecoveryDecisionResolver.shouldClearAfterStuck(
                        this.guardRecovery.stuckTicks(), ShipAiNumbers.MOVE_STUCK_TICK_LIMIT)) {
                    if (tryGuardTeleportRecovery(ship, target, guardedEntity, distSq, true)) {
                        return;
                    }
                    Shincolle.debugLog("[SCMoveDiag] GuardGoal stuckDisable ship={} target={} stuckTicks={}",
                            ship.getUUID(), target, this.guardRecovery.stuckTicks());
                    disableGuardState(ship);
                    return;
                }
                if (this.nextGuardPathTick-- <= 0 || ship.guardMovementCoordinator().isNavigationDone()) {
                    this.nextGuardPathTick = ShipAiNumbers.PATH_RECALC_INTERVAL_TICKS;
                    if (!ship.guardMovementCoordinator().moveTo(target, ShipAiNumbers.GUARD_MOVE_SPEED)) {
                        int failCount = this.guardRecovery.recordMoveFailure();
                        syncGuardRecoveryMemory(ship);
                        if (this.guardRecovery.shouldLogMoveFailure(ship.tickCount, ShipAiNumbers.MOVE_FAIL_LOG_INTERVAL)) {
                            Shincolle.debugLog("[SCMoveDiag] GuardGoal moveFail ship={} target={} failCount={}",
                                    ship.getUUID(), target, failCount);
                        }
                        if (ShipRecoveryDecisionResolver.shouldClearAfterMoveFailures(
                                failCount, ShipAiNumbers.MOVE_FAIL_LIMIT)) {
                            if (tryGuardTeleportRecovery(ship, target, guardedEntity, distSq, true)) {
                                return;
                            }
                            Shincolle.debugLog("[SCMoveDiag] GuardGoal failDisable ship={} target={} failCount={}",
                                    ship.getUUID(), target, this.guardRecovery.moveFailCount());
                            disableGuardState(ship);
                            return;
                        }
                    } else {
                        this.guardRecovery.clearMoveFailures();
                        syncGuardRecoveryMemory(ship);
                        Shincolle.debugLog("ShipBrain guardMoveOk ship={} target={} distSq={}", ship.getUUID(), target, distSq);
                    }
                }
            } else {
                this.nextGuardPathTick = 0;
                this.guardRecovery.reset(ship.position());
                syncGuardRecoveryMemory(ship);
                ship.guardMovementCoordinator().stop();
            }

            updateGuardLook(ship, guardedEntity, target, distSq, isSummoning);
        }

        @Override
        protected void stop(ServerLevel level, EntityShipBase ship, long gameTime) {
            clearGuardMoveState(ship);
        }

        private void resetGuardRecoveryIfTargetChanged(EntityShipBase ship, ShipGuardTarget guardTarget, Entity guardedEntity) {
            GuardRecoveryTargetKey targetKey = GuardRecoveryTargetKey.from(guardTarget, guardedEntity);
            if (Objects.equals(this.lastGuardRecoveryTargetKey, targetKey)) {
                return;
            }
            this.lastGuardRecoveryTargetKey = targetKey;
            this.nextGuardPathTick = 0;
            this.guardRecovery.reset(ship.position());
            syncGuardRecoveryMemory(ship);
            ship.guardMovementCoordinator().reset();
        }

        private boolean tryGuardTeleportRecovery(EntityShipBase ship, Vec3 target, Entity guardedEntity, double distSq, boolean force) {
            ShipRecoveryDecisionResolver.State recoveryState =
                    new ShipRecoveryDecisionResolver.State(
                            force,
                            distSq,
                            ShipAiNumbers.TELEPORT_DISTANCE_SQ);
            if (!ShipRecoveryDecisionResolver.shouldAttemptTeleport(recoveryState)) {
                return false;
            }
            if (!this.guardRecovery.shouldTryTeleportThrottled(force, distSq,
                    ShipAiNumbers.TELEPORT_DISTANCE_SQ, ShipAiNumbers.TELEPORT_COOLDOWN_TICKS)) {
                return false;
            }
            boolean teleported = guardedEntity instanceof LivingEntity livingGuarded
                    ? ship.guardMovementCoordinator().teleportNearLiving(livingGuarded, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)
                    : ship.guardMovementCoordinator().teleportNearPoint(target, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET);
            if (!teleported) {
                return false;
            }
            Shincolle.debugLog("[SCMoveDiag] GuardGoal teleportRecovery ship={} target={} force={} distSq={}",
                    ship.getUUID(), target, force, distSq);
            this.nextGuardPathTick = 0;
            this.guardRecovery.reset(ship.position());
            syncGuardRecoveryMemory(ship);
            return true;
        }

        private void disableGuardState(EntityShipBase ship) {
            this.nextGuardPathTick = 0;
            this.lastGuardRecoveryTargetKey = null;
            this.guardRecovery.clear();
            clearRecoveryMemory(ship, ModMemoryModules.SHIP_GUARD_RECOVERY.get());
            clearWalkAndLookMemory(ship);
            ship.guardMovementCoordinator().stop();
            ship.setStateFlag(EntityShipBase.STATE_FLAG_DISABLE_GUARD_POS, true);
            ship.clearGuardTarget();
        }

        private void clearGuardMoveState(EntityShipBase ship) {
            this.nextGuardPathTick = 0;
            this.lastGuardRecoveryTargetKey = null;
            this.guardRecovery.clear();
            clearRecoveryMemory(ship, ModMemoryModules.SHIP_GUARD_RECOVERY.get());
            clearWalkAndLookMemory(ship);
            ship.guardMovementCoordinator().stop();
        }

        private void syncGuardRecoveryMemory(EntityShipBase ship) {
            syncRecoveryMemory(ship, ModMemoryModules.SHIP_GUARD_RECOVERY.get(),
                    this.guardRecovery, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT);
        }

        private void updateGuardLook(EntityShipBase ship, Entity guardedEntity, Vec3 target, double distSq, boolean isSummoning) {
            if (guardedEntity instanceof LivingEntity livingEntity) {
                ship.getLookControl().setLookAt(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ(),
                        ShipAiNumbers.FOCUS_LOOK_YAW, ShipAiNumbers.FOCUS_LOOK_PITCH);
            } else if (guardedEntity != null) {
                ship.getLookControl().setLookAt(guardedEntity.getX(), guardedEntity.getY(), guardedEntity.getZ(),
                        ShipAiNumbers.FOCUS_LOOK_YAW, ShipAiNumbers.FOCUS_LOOK_PITCH);
            } else if (ShipGuardDecisionResolver.shouldLookAtOwnerOrPlayer(
                    new ShipGuardDecisionResolver.State(
                            false,
                            false,
                            false,
                            false,
                            distSq,
                            isSummoning,
                            0,
                            0))) {
                lookAtOwnerOrNearestPlayer(ship);
            } else {
                ship.getLookControl().setLookAt(target.x, target.y + ship.getEyeHeight(), target.z,
                        ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH);
            }
        }

        private void lookAtOwnerOrNearestPlayer(EntityShipBase ship) {
            LivingEntity lookTarget = ship.getOwner();
            if (lookTarget == null || ship.distanceToSqr(lookTarget) > ShipAiNumbers.GUARD_OWNER_LOOK_MAX_DISTANCE_SQ) {
                lookTarget = ship.level().getNearestPlayer(ship, ShipAiNumbers.GUARD_NEAREST_PLAYER_LOOK_DISTANCE);
            }
            if (lookTarget != null) {
                ship.getLookControl().setLookAt(lookTarget.getX(), lookTarget.getEyeY(), lookTarget.getZ(),
                        ShipAiNumbers.FOCUS_LOOK_YAW, ShipAiNumbers.FOCUS_LOOK_PITCH);
                return;
            }

            float yaw = ship.getYRot();
            double rad = -yaw * ShipAiNumbers.DEGREES_TO_RADIANS;
            double tx = ship.getX() + Math.sin(rad) * ShipAiNumbers.GUARD_FALLBACK_LOOK_DISTANCE;
            double ty = ship.getEyeY();
            double tz = ship.getZ() + Math.cos(rad) * ShipAiNumbers.GUARD_FALLBACK_LOOK_DISTANCE;
            ship.getLookControl().setLookAt(tx, ty, tz, ShipAiNumbers.FOCUS_LOOK_YAW, ShipAiNumbers.FOCUS_LOOK_PITCH);
        }
    }

    private static final class ShipFollowOwnerBehavior extends Behavior<EntityShipBase> {
        private static final String FOLLOW_DEBUG_PREFIX = "[SCFollowDebug]";
        private final ShipMovementRecoveryState followRecovery = new ShipMovementRecoveryState();
        private double lastOwnerX;
        private double lastOwnerY;
        private double lastOwnerZ;
        private boolean hasOwnerPos;
        private boolean followOwnerActive;
        private boolean[] formationDir = new boolean[]{false, true};

        ShipFollowOwnerBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntityShipBase ship) {
            return shouldFollowOwner(ship, false);
        }

        @Override
        protected boolean canStillUse(ServerLevel level, EntityShipBase ship, long gameTime) {
            ShipBrainMemory.FollowStateMemory followMemory = followStateMemory(ship);
            return ShipBrainActivityResolver.shouldContinueFollow(followResolverState(level, ship, followMemory));
        }

        @Override
        protected boolean timedOut(long gameTime) {
            return false;
        }

        @Override
        protected void tick(ServerLevel level, EntityShipBase ship, long gameTime) {
            ShipBrainMemory.FollowStateMemory followMemory = followStateMemory(ship);
            LivingEntity owner = ship.getOwner();
            Vec3 ownerPos = followMemory.ownerPos();
            if (owner == null || !hasSameDimensionOwnerPosition(level, followMemory)) {
                clearFollowMoveState(ship);
                return;
            }

            ship.resetInteractionEmotionState();
            if (followMemory.ownerHasCombatRation()) {
                ship.setEmotionPrimary(EntityShipBase.EMOTION_HAPPY);
                if (ship.tickCount % ShipAiNumbers.FOLLOW_POSITIVE_EMOTE_INTERVAL == 0) {
                    EmotionParticleType[] positiveEmotes = {
                            EmotionParticleType.HEART,
                            EmotionParticleType.MUSIC_NOTE,
                            EmotionParticleType.HAPPY_BOB,
                            EmotionParticleType.SPARKLE_EYES,
                            EmotionParticleType.POUT_BOUNCE,
                            EmotionParticleType.LAUGH,
                            EmotionParticleType.HAPPY_GLANCE,
                            EmotionParticleType.BLINK,
                            EmotionParticleType.BLUSH
                    };
                    EmotionParticleType selected = positiveEmotes[ship.getRandom().nextInt(positiveEmotes.length)];
                    ship.applyParticleEmotion(selected);
                }
            }

            ship.getLookControl().setLookAt(owner, ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH);

            int teamId = ship.getFormationTeam();
            int slotId = ship.getFormationSlot();
            Vec3 moveTarget = ownerPos;
            if (teamId >= 0 && slotId >= 0 && owner instanceof Player ownerPlayer) {
                org.trp.shincolle.attachment.AdmiralData data = PlayerStateService.admiralData(ownerPlayer);
                int formationId = data.getFormationID(teamId);
                updateFormationDirection(owner);
                moveTarget = org.trp.shincolle.utility.FormationHelper.getFormationPos(
                        formationId, slotId, ownerPos, this.formationDir[0], this.formationDir[1]);
            }
            float minDist = resolveFollowMinDistance(followMemory);
            setPointWalkAndLookMemory(ship, moveTarget, ShipAiNumbers.FOLLOW_OWNER_SPEED,
                    closeEnoughDistance(minDist * minDist));
            setEntityLookMemory(ship, owner);

            if (!this.followOwnerActive) {
                float maxDist = resolveFollowMaxDistance(followMemory, minDist);
                Shincolle.diagnosticLog("{} start ship={} owner={} distSq={} minDist={} maxDist={}",
                        FOLLOW_DEBUG_PREFIX, ship.getUUID(), followMemory.ownerId(), followMemory.ownerDistanceSq(), minDist, maxDist);
            }
            this.followOwnerActive = true;
            ShipMovementCoordinator movement = ship.followOwnerMovementCoordinator();
            boolean moved = movement.moveTo(moveTarget, ShipAiNumbers.FOLLOW_OWNER_SPEED);
            Shincolle.diagnosticLog("{} move ship={} owner={} target={} moved={}",
                    FOLLOW_DEBUG_PREFIX, ship.getUUID(), owner.getUUID(), moveTarget, moved);
            double distSq = followMemory.ownerDistanceSq();
            this.followRecovery.trackProgress(ship.position());
            boolean force = this.followRecovery.isStuckLongerThan(ShipAiNumbers.MOVE_STUCK_TICK_LIMIT);
            if (!this.followRecovery.shouldTryTeleportThrottled(force, distSq,
                    ShipAiNumbers.TELEPORT_DISTANCE_SQ, ShipAiNumbers.FOLLOW_TELEPORT_COOLDOWN_TICKS)) {
                syncFollowRecoveryMemory(ship);
                return;
            }
            if (!movement.teleportNearLiving(owner, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)) {
                syncFollowRecoveryMemory(ship);
                return;
            }
            Shincolle.diagnosticLog("{} teleportRecovery ship={} owner={} force={} distSq={} stuckTicks={}",
                    FOLLOW_DEBUG_PREFIX,
                    ship.getUUID(), owner.getUUID(), force, distSq, this.followRecovery.stuckTicks());
            this.followRecovery.reset(ship.position());
            syncFollowRecoveryMemory(ship);
        }

        @Override
        protected void stop(ServerLevel level, EntityShipBase ship, long gameTime) {
            clearFollowMoveState(ship);
        }

        private void clearFollowMoveState(EntityShipBase ship) {
            ShipBrainMemory.FollowStateMemory followMemory = followStateMemory(ship);
            if (this.followOwnerActive && followMemory.ownerPresent()) {
                float minDist = resolveFollowMinDistance(followMemory);
                float maxDist = resolveFollowMaxDistance(followMemory, minDist);
                Shincolle.diagnosticLog("{} stop ship={} owner={} distSq={} minDist={} maxDist={}",
                        FOLLOW_DEBUG_PREFIX, ship.getUUID(), followMemory.ownerId(), followMemory.ownerDistanceSq(), minDist, maxDist);
            }
            this.followRecovery.clear();
            clearRecoveryMemory(ship, ModMemoryModules.SHIP_FOLLOW_RECOVERY.get());
            this.hasOwnerPos = false;
            this.followOwnerActive = false;
            clearWalkAndLookMemory(ship);
            ship.followOwnerMovementCoordinator().stop();
        }

        private void syncFollowRecoveryMemory(EntityShipBase ship) {
            syncRecoveryMemory(ship, ModMemoryModules.SHIP_FOLLOW_RECOVERY.get(),
                    this.followRecovery, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT);
        }

        private void updateFormationDirection(LivingEntity owner) {
            double ox = owner.getX();
            double oy = owner.getY();
            double oz = owner.getZ();
            if (!this.hasOwnerPos) {
                this.lastOwnerX = ox;
                this.lastOwnerY = oy;
                this.lastOwnerZ = oz;
                this.hasOwnerPos = true;
                return;
            }

            double dx = this.lastOwnerX - ox;
            double dy = this.lastOwnerY - oy;
            double dz = this.lastOwnerZ - oz;
            double dsq = dx * dx + dy * dy + dz * dz;
            if (dsq > ShipAiNumbers.FOLLOW_FORMATION_UPDATE_DISTANCE_SQ) {
                this.formationDir = org.trp.shincolle.utility.FormationHelper.getFormationDirection(
                        ox, oz, this.lastOwnerX, this.lastOwnerZ);
                this.lastOwnerX = ox;
                this.lastOwnerY = oy;
                this.lastOwnerZ = oz;
            }
        }
    }

    private static final class ShipLookAtPlayerBehavior extends Behavior<EntityShipBase> {
        ShipLookAtPlayerBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntityShipBase ship) {
            return !ship.isInDeadPose();
        }

        @Override
        protected boolean canStillUse(ServerLevel level, EntityShipBase ship, long gameTime) {
            return !ship.isInDeadPose();
        }

        @Override
        protected void tick(ServerLevel level, EntityShipBase ship, long gameTime) {
            ShipBrainMemory.GuardTargetMemory guardMemory = guardTargetMemory(ship);
            ShipBrainMemory.PointerTargetMemory pointerMemory = pointerTargetMemory(ship);
            Entity guardedEntity = ship.getGuardedEntity();
            if (guardedEntity instanceof LivingEntity livingEntity) {
                ship.getLookControl().setLookAt(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ(),
                        ShipAiNumbers.FOCUS_LOOK_YAW, ShipAiNumbers.FOCUS_LOOK_PITCH);
                return;
            }
            if (guardedEntity != null) {
                ship.getLookControl().setLookAt(guardedEntity.getX(), guardedEntity.getY(), guardedEntity.getZ(),
                        ShipAiNumbers.FOCUS_LOOK_YAW, ShipAiNumbers.FOCUS_LOOK_PITCH);
                return;
            }
            if (pointerMemory.entityTargetAlive() && pointerMemory.entityTargetPos() != null) {
                Vec3 target = pointerMemory.entityTargetPos();
                ship.getLookControl().setLookAt(target.x, target.y + ship.getEyeHeight(), target.z,
                        ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH);
                return;
            }
            ShipBrainMemory.FollowStateMemory followMemory = followStateMemory(ship);
            if (shouldFollowOwnerLook(ship, followMemory)) {
                Vec3 target = followMemory.ownerPos();
                ship.getLookControl().setLookAt(target.x, followMemory.ownerEyeY(), target.z,
                        ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH);
                return;
            }
            if (guardMemory.hasBlockTarget()) {
                Vec3 target = guardMemory.blockCenter();
                ship.getLookControl().setLookAt(target.x, target.y + ship.getEyeHeight(), target.z,
                        ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH);
                return;
            }
            Player player = level.getNearestPlayer(ship, ShipAiNumbers.LOOK_AT_PLAYER_DISTANCE);
            if (player != null) {
                ship.getLookControl().setLookAt(player, ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH);
            }
        }

        private boolean shouldFollowOwnerLook(EntityShipBase ship, ShipBrainMemory.FollowStateMemory followMemory) {
            if (followMemory.ownerPos() == null) {
                return false;
            }
            if (followMemory.shouldFollow()) {
                return true;
            }
            return followMemory.ownerDistanceSq() <= ShipAiNumbers.GUARD_OWNER_LOOK_MAX_DISTANCE_SQ
                    && guardTargetMemory(ship).target().isActive();
        }
    }

    private static final class ShipRandomLookAroundBehavior extends Behavior<EntityShipBase> {
        ShipRandomLookAroundBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntityShipBase ship) {
            return !ship.isInDeadPose() && ship.getRandom().nextInt(ShipAiNumbers.RANDOM_LOOK_CHANCE) == 0;
        }

        @Override
        protected void start(ServerLevel level, EntityShipBase ship, long gameTime) {
            float yaw = ship.getYRot();
            double rad = -yaw * ShipAiNumbers.DEGREES_TO_RADIANS;
            double tx = ship.getX() + Math.sin(rad) * ShipAiNumbers.GUARD_FALLBACK_LOOK_DISTANCE;
            double ty = ship.getEyeY();
            double tz = ship.getZ() + Math.cos(rad) * ShipAiNumbers.GUARD_FALLBACK_LOOK_DISTANCE;
            ship.getLookControl().setLookAt(tx, ty, tz, ShipAiNumbers.FOCUS_LOOK_YAW, ShipAiNumbers.FOCUS_LOOK_PITCH);
        }
    }

    private static final class ShipRandomStrollBehavior extends Behavior<EntityShipBase> {
        ShipRandomStrollBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntityShipBase ship) {
            return !ship.isOrderedToSit()
                    && !ship.isInSittingPose()
                    && !ship.isInDeadPose()
                    && !ship.isPassenger()
                    && !ship.isVehicle()
                    && !pointerTargetMemory(ship).hasAnyTarget()
                    && ship.getTarget() == null
                    && !followStateMemory(ship).shouldFollow()
                    && !guardTargetMemory(ship).target().isActive()
                    && ship.getRandom().nextInt(ShipAiNumbers.RANDOM_STROLL_CHANCE) == 0;
        }

        @Override
        protected void start(ServerLevel level, EntityShipBase ship, long gameTime) {
            Vec3 target = net.minecraft.world.entity.ai.util.DefaultRandomPos.getPos(
                    ship, ShipAiNumbers.RANDOM_STROLL_HORIZONTAL_RANGE, ShipAiNumbers.RANDOM_STROLL_VERTICAL_RANGE);
            if (target != null) {
                Shincolle.diagnosticLog("[SCIdleDiag] randomStroll ship={} target={}", ship.getUUID(), target);
                setPointWalkAndLookMemory(ship, target, ShipAiNumbers.RANDOM_STROLL_SPEED, 1);
                ship.idleMovementCoordinator().moveTo(target, ShipAiNumbers.RANDOM_STROLL_SPEED);
            }
        }
    }

    private record GuardRecoveryTargetKey(ShipGuardTarget.Type type, UUID entityId, int x, int y, int z, int dimensionId) {
        private static GuardRecoveryTargetKey from(ShipGuardTarget guardTarget, Entity guardedEntity) {
            if (guardTarget.isEntity() && guardedEntity != null) {
                return new GuardRecoveryTargetKey(guardTarget.type(), guardedEntity.getUUID(), 0, 0, 0,
                        EntityShipBase.getLegacyDimensionId(guardedEntity.level()));
            }
            return new GuardRecoveryTargetKey(guardTarget.type(), null, guardTarget.x(), guardTarget.y(),
                    guardTarget.z(), guardTarget.dimensionId());
        }
    }
}
