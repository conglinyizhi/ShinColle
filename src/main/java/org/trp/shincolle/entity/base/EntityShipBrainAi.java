package org.trp.shincolle.entity.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.phys.Vec3;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.menu.ShipContainerMenu;
import org.trp.shincolle.server.PlayerStateService;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

final class EntityShipBrainAi {
    static final List<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE
    );
    static final List<SensorType<? extends net.minecraft.world.entity.ai.sensing.Sensor<? super EntityShipBase>>> SENSOR_TYPES =
            ImmutableList.of(
                    SensorType.NEAREST_LIVING_ENTITIES,
                    SensorType.NEAREST_PLAYERS
            );

    private EntityShipBrainAi() {
    }

    static Brain<?> makeBrain(EntityShipBase ship, Brain<EntityShipBase> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    @SuppressWarnings("unchecked")
    static void tick(ServerLevel level, EntityShipBase ship) {
        logServerBrainTickIfNeeded(ship);
        Brain<EntityShipBase> brain = (Brain<EntityShipBase>) ship.getBrain();
        brain.tick(level, ship);
        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }

    private static void logServerBrainTickIfNeeded(EntityShipBase ship) {
        if (ship.tickCount % 40 != 0) {
            return;
        }
        if (ship.getOwnerUUID() == null && ship.getTarget() == null && !ship.hasPointerTarget()
                && !ship.getGuardTarget().isActive()) {
            return;
        }
        LivingEntity owner = ship.getOwner();
        double distSq = owner == null ? -1.0D : ship.distanceToSqr(owner);
        Shincolle.diagnosticLog(
                "[SCBrainDiag] serverTick ship={} ownerUuid={} ownerPresent={} tame={} deadPose={} noFuel={} shouldFollow={} reason={} distSq={} target={} pointer={} guard={}",
                ship.getUUID(),
                ship.getOwnerUUID(),
                owner != null,
                ship.isTame(),
                ship.isInDeadPose(),
                ship.isNoFuel(),
                ship.shouldFollowOwner(),
                ship.explainFollowBlockReason(),
                distSq,
                ship.getTarget() != null,
                ship.hasPointerTarget() || ship.hasPointerTargetEntity(),
                ship.getGuardTarget().isActive());
    }

    private static void initCoreActivity(Brain<EntityShipBase> brain) {
        brain.addActivity(Activity.CORE, ImmutableList.of(
                Pair.of(ShipAiNumbers.CORE_MOVEMENT_PRIORITY, new ShipMovementBehavior()),
                Pair.of(ShipAiNumbers.LOOK_AT_PLAYER_PRIORITY, new ShipLookAtPlayerBehavior()),
                Pair.of(ShipAiNumbers.RANDOM_LOOK_PRIORITY, new ShipRandomLookAroundBehavior())
        ));
    }

    private static void initIdleActivity(Brain<EntityShipBase> brain) {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(ShipAiNumbers.RANDOM_STROLL_PRIORITY, new ShipRandomStrollBehavior())
        ));
    }

    private static final class ShipMovementBehavior extends Behavior<EntityShipBase> {
        private final ShipMovementRecoveryState pointerRecovery = new ShipMovementRecoveryState();
        private final ShipMovementRecoveryState guardRecovery = new ShipMovementRecoveryState();
        private final ShipMovementRecoveryState followRecovery = new ShipMovementRecoveryState();
        private static final String FOLLOW_DEBUG_PREFIX = "[SCFollowDebug]";
        private int nextPointerPathTick;
        private int nextGuardPathTick;
        private Vec3 lastRawPointerTarget;
        private GuardRecoveryTargetKey lastGuardRecoveryTargetKey;
        private double lastOwnerX;
        private double lastOwnerY;
        private double lastOwnerZ;
        private boolean hasOwnerPos;
        private boolean followOwnerActive;
        private int nextBrainLogTick;
        private int nextFollowSkipLogTick;
        private int nextCanStillUseLogTick;
        private boolean[] formationDir = new boolean[]{false, true};

        ShipMovementBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntityShipBase ship) {
            return !ship.isInDeadPose();
        }

        @Override
        protected void start(ServerLevel level, EntityShipBase ship, long gameTime) {
            Shincolle.diagnosticLog(
                    "[SCBrainDiag] movementStart ship={} gameTime={} tick={} ownerUuid={} tame={} deadPose={} shouldFollow={} reason={}",
                    ship.getUUID(),
                    gameTime,
                    ship.tickCount,
                    ship.getOwnerUUID(),
                    ship.isTame(),
                    ship.isInDeadPose(),
                    ship.shouldFollowOwner(),
                    ship.explainFollowBlockReason());
        }

        @Override
        protected boolean canStillUse(ServerLevel level, EntityShipBase ship, long gameTime) {
            boolean canStillUse = !ship.isInDeadPose();
            if (!canStillUse || ship.tickCount >= this.nextCanStillUseLogTick) {
                this.nextCanStillUseLogTick = ship.tickCount + 40;
                Shincolle.diagnosticLog(
                        "[SCBrainDiag] movementCanStillUse ship={} canStillUse={} gameTime={} tick={} deadPose={} ownerUuid={} shouldFollow={} reason={}",
                        ship.getUUID(),
                        canStillUse,
                        gameTime,
                        ship.tickCount,
                        ship.isInDeadPose(),
                        ship.getOwnerUUID(),
                        ship.shouldFollowOwner(),
                        ship.explainFollowBlockReason());
            }
            return canStillUse;
        }

        @Override
        protected boolean timedOut(long gameTime) {
            return false;
        }

        @Override
        protected void tick(ServerLevel level, EntityShipBase ship, long gameTime) {
            logBrainTickIfNeeded(ship);
            if (ship.hasPointerTarget() && canMove(ship)) {
                logMovementBranch(ship, "pointer");
                tickPointerMove(ship);
                return;
            }

            clearPointerMoveState(ship);

            if (canGuard(ship)) {
                logMovementBranch(ship, "guard");
                tickGuardMove(ship);
                return;
            }

            clearGuardMoveState(ship);

            if (shouldFollowOwner(ship)) {
                logMovementBranch(ship, "follow");
                tickFollowOwner(ship);
                return;
            }

            logFollowSkipIfNeeded(ship);
            clearFollowMoveState(ship);
        }

        @Override
        protected void stop(ServerLevel level, EntityShipBase ship, long gameTime) {
            Shincolle.diagnosticLog(
                    "[SCBrainDiag] movementStop ship={} gameTime={} tick={} ownerUuid={} tame={} deadPose={} shouldFollow={} reason={} followActive={}",
                    ship.getUUID(),
                    gameTime,
                    ship.tickCount,
                    ship.getOwnerUUID(),
                    ship.isTame(),
                    ship.isInDeadPose(),
                    ship.shouldFollowOwner(),
                    ship.explainFollowBlockReason(),
                    this.followOwnerActive);
            clearPointerMoveState(ship);
            clearGuardMoveState(ship);
            clearFollowMoveState(ship);
        }

        private boolean canMove(EntityShipBase ship) {
            return !ship.isOrderedToSit()
                    && !ship.isInSittingPose()
                    && !ship.isPassenger()
                    && !ship.isVehicle()
                    && !ship.isInDeadPose();
        }

        private void logBrainTickIfNeeded(EntityShipBase ship) {
            if (ship.tickCount < this.nextBrainLogTick) {
                return;
            }
            this.nextBrainLogTick = ship.tickCount + 40;
            LivingEntity owner = ship.getOwner();
            double distSq = owner == null ? -1.0D : ship.distanceToSqr(owner);
            Shincolle.diagnosticLog(
                    "[SCBrainDiag] movementTick ship={} ownerUuid={} ownerPresent={} tame={} canMove={} shouldFollow={} reason={} distSq={} pointer={} guard={} target={} followActive={}",
                    ship.getUUID(),
                    ship.getOwnerUUID(),
                    owner != null,
                    ship.isTame(),
                    canMove(ship),
                    ship.shouldFollowOwner(),
                    ship.explainFollowBlockReason(),
                    distSq,
                    ship.hasPointerTarget() || ship.hasPointerTargetEntity(),
                    ship.getGuardTarget().isActive(),
                    ship.getTarget() != null,
                    this.followOwnerActive);
        }

        private void tickPointerMove(EntityShipBase ship) {
            Vec3 rawTarget = ship.getRawPointerTarget();
            Vec3 target = ship.getPointerTarget();
            if (rawTarget == null || target == null) {
                clearPointerMoveState(ship);
                return;
            }

            if (this.lastRawPointerTarget == null || rawTarget.distanceToSqr(this.lastRawPointerTarget) > ShipAiNumbers.TARGET_SWITCH_DISTANCE_SQ) {
                this.nextPointerPathTick = 0;
                this.lastRawPointerTarget = rawTarget;
                this.pointerRecovery.reset(ship.position());
                ship.pointerMovementCoordinator().reset();
                Shincolle.debugLog("ShipBrain pointerTargetChanged ship={} target={}", ship.getUUID(), target);
            }

            if (ship.distanceToSqr(target) <= ShipAiNumbers.POINTER_MOVE_REACH_SQR) {
                clearPointerMoveState(ship);
                return;
            }

            ship.resetInteractionEmotionState();
            this.pointerRecovery.trackProgress(ship.position());
            if (this.pointerRecovery.isStuckLongerThan(ShipAiNumbers.MOVE_STUCK_TICK_LIMIT)) {
                if (tryPointerTeleportRecovery(ship, target, true)) {
                    return;
                }
                Shincolle.debugLog("PointerGoal stuckClear ship={} target={} stuckTicks={}",
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
                    if (this.pointerRecovery.shouldLogMoveFailure(ship.tickCount, ShipAiNumbers.MOVE_FAIL_LOG_INTERVAL)) {
                        Shincolle.debugLog("PointerGoal moveFail ship={} target={} failCount={}",
                                ship.getUUID(), target, failCount);
                    }
                    if (failCount > ShipAiNumbers.MOVE_FAIL_LIMIT) {
                        if (tryPointerTeleportRecovery(ship, target, true)) {
                            return;
                        }
                        Shincolle.debugLog("PointerGoal failClear ship={} target={} failCount={}",
                                ship.getUUID(), target, this.pointerRecovery.moveFailCount());
                        ship.clearPointerTarget();
                        clearPointerMoveState(ship);
                    }
                } else {
                    this.pointerRecovery.clearMoveFailures();
                    Shincolle.debugLog("ShipBrain pointerMoveOk ship={} target={}", ship.getUUID(), target);
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
            Shincolle.debugLog("PointerGoal teleportRecovery ship={} target={} force={}",
                    ship.getUUID(), target, force);
            this.nextPointerPathTick = 0;
            this.pointerRecovery.reset(ship.position());
            return true;
        }

        private void clearPointerMoveState(EntityShipBase ship) {
            this.nextPointerPathTick = 0;
            this.lastRawPointerTarget = null;
            this.pointerRecovery.clear();
            ship.pointerMovementCoordinator().stop();
        }

        private boolean canGuard(EntityShipBase ship) {
            if (!canMove(ship) || ship.getStateFlag(EntityShipBase.STATE_FLAG_DISABLE_GUARD_POS)) {
                return false;
            }
            ShipGuardTarget guardTarget = ship.getGuardTarget();
            if (guardTarget.isBlock()) {
                return guardTarget.isIn(ship.level());
            }
            if (guardTarget.isEntity()) {
                Entity guarded = ship.getGuardedEntity();
                return guarded != null && guarded.isAlive();
            }
            return false;
        }

        private void tickGuardMove(EntityShipBase ship) {
            Entity guardedEntity = ship.getGuardedEntity();
            ShipGuardTarget guardTarget = ship.getGuardTarget();

            int timer = ship.getStateTimer(ShipAiNumbers.GUARD_SUMMON_TIMER_ID);
            boolean isSummoning = timer > 0;
            if (isSummoning) {
                ship.setStateTimer(ShipAiNumbers.GUARD_SUMMON_TIMER_ID, timer - 1);
            }

            Vec3 target;
            if (guardTarget.isEntity() && guardedEntity != null) {
                if (ship.getGuardedPos(3) != EntityShipBase.getLegacyDimensionId(guardedEntity.level())) {
                    ship.setGuardedPos(-1, -1, -1, EntityShipBase.getLegacyDimensionId(guardedEntity.level()),
                            ShipGuardTarget.Type.ENTITY.legacyId());
                }
                target = guardedEntity.position();
            } else {
                target = guardTarget.blockCenter();
            }

            resetGuardRecoveryIfTargetChanged(ship, guardTarget, guardedEntity);
            double distSq = ship.distanceToSqr(target);
            double stopDistanceSq = guardTarget.isEntity()
                    ? ShipAiNumbers.GUARD_ENTITY_STOP_DISTANCE_SQ
                    : ShipAiNumbers.GUARD_BLOCK_STOP_DISTANCE_SQ;

            if (distSq > stopDistanceSq) {
                this.guardRecovery.trackProgress(ship.position());
                if (tryGuardTeleportRecovery(ship, target, guardedEntity, distSq, false)) {
                    return;
                }
                if (this.guardRecovery.isStuckLongerThan(ShipAiNumbers.MOVE_STUCK_TICK_LIMIT)) {
                    if (tryGuardTeleportRecovery(ship, target, guardedEntity, distSq, true)) {
                        return;
                    }
                    Shincolle.debugLog("GuardGoal stuckDisable ship={} target={} stuckTicks={}",
                            ship.getUUID(), target, this.guardRecovery.stuckTicks());
                    disableGuardState(ship);
                    return;
                }
                if (this.nextGuardPathTick-- <= 0 || ship.getNavigation().isDone()) {
                    this.nextGuardPathTick = ShipAiNumbers.PATH_RECALC_INTERVAL_TICKS;
                    if (!ship.guardMovementCoordinator().moveTo(target, ShipAiNumbers.GUARD_MOVE_SPEED)) {
                        int failCount = this.guardRecovery.recordMoveFailure();
                        if (this.guardRecovery.shouldLogMoveFailure(ship.tickCount, ShipAiNumbers.MOVE_FAIL_LOG_INTERVAL)) {
                            Shincolle.debugLog("GuardGoal moveFail ship={} target={} failCount={}",
                                    ship.getUUID(), target, failCount);
                        }
                        if (failCount > ShipAiNumbers.MOVE_FAIL_LIMIT) {
                            if (tryGuardTeleportRecovery(ship, target, guardedEntity, distSq, true)) {
                                return;
                            }
                            Shincolle.debugLog("GuardGoal failDisable ship={} target={} failCount={}",
                                    ship.getUUID(), target, this.guardRecovery.moveFailCount());
                            disableGuardState(ship);
                            return;
                        }
                    } else {
                        this.guardRecovery.clearMoveFailures();
                        Shincolle.debugLog("ShipBrain guardMoveOk ship={} target={} distSq={}", ship.getUUID(), target, distSq);
                    }
                }
            } else {
                this.nextGuardPathTick = 0;
                this.guardRecovery.reset(ship.position());
                ship.guardMovementCoordinator().stop();
            }

            updateGuardLook(ship, guardedEntity, target, distSq, isSummoning);
        }

        private void resetGuardRecoveryIfTargetChanged(EntityShipBase ship, ShipGuardTarget guardTarget, Entity guardedEntity) {
            GuardRecoveryTargetKey targetKey = GuardRecoveryTargetKey.from(guardTarget, guardedEntity);
            if (Objects.equals(this.lastGuardRecoveryTargetKey, targetKey)) {
                return;
            }
            this.lastGuardRecoveryTargetKey = targetKey;
            this.nextGuardPathTick = 0;
            this.guardRecovery.reset(ship.position());
            ship.guardMovementCoordinator().reset();
        }

        private boolean tryGuardTeleportRecovery(EntityShipBase ship, Vec3 target, Entity guardedEntity, double distSq, boolean force) {
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
            Shincolle.debugLog("GuardGoal teleportRecovery ship={} target={} force={} distSq={}",
                    ship.getUUID(), target, force, distSq);
            this.nextGuardPathTick = 0;
            this.guardRecovery.reset(ship.position());
            return true;
        }

        private void disableGuardState(EntityShipBase ship) {
            this.nextGuardPathTick = 0;
            this.lastGuardRecoveryTargetKey = null;
            this.guardRecovery.clear();
            ship.guardMovementCoordinator().stop();
            ship.setStateFlag(EntityShipBase.STATE_FLAG_DISABLE_GUARD_POS, true);
            ship.clearGuardTarget();
        }

        private void clearGuardMoveState(EntityShipBase ship) {
            this.nextGuardPathTick = 0;
            this.lastGuardRecoveryTargetKey = null;
            this.guardRecovery.clear();
            ship.guardMovementCoordinator().stop();
        }

        private void updateGuardLook(EntityShipBase ship, Entity guardedEntity, Vec3 target, double distSq, boolean isSummoning) {
            if (guardedEntity instanceof LivingEntity livingEntity) {
                ship.getLookControl().setLookAt(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ(),
                        ShipAiNumbers.FOCUS_LOOK_YAW, ShipAiNumbers.FOCUS_LOOK_PITCH);
            } else if (guardedEntity != null) {
                ship.getLookControl().setLookAt(guardedEntity.getX(), guardedEntity.getY(), guardedEntity.getZ(),
                        ShipAiNumbers.FOCUS_LOOK_YAW, ShipAiNumbers.FOCUS_LOOK_PITCH);
            } else if (isSummoning || distSq < ShipAiNumbers.GUARD_NEAR_LOOK_DISTANCE_SQ) {
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

        private boolean shouldFollowOwner(EntityShipBase ship) {
            if (!ship.shouldFollowOwner() || !canMove(ship)) {
                return false;
            }
            LivingEntity owner = ship.getOwner();
            if (owner == null) {
                return false;
            }
            double distSq = ship.distanceToSqr(owner);
            if (owner instanceof Player player && ship.playerHasCombatRation(player)) {
                return distSq > ShipAiNumbers.FOLLOW_RATION_DISTANCE_SQ;
            }
            float minDist = resolveFollowMinDistance(ship);
            float maxDist = resolveFollowMaxDistance(ship, minDist);
            if (this.followOwnerActive) {
                return distSq > minDist * minDist;
            }
            return distSq > maxDist * maxDist;
        }

        private void tickFollowOwner(EntityShipBase ship) {
            LivingEntity owner = ship.getOwner();
            if (owner == null) {
                clearFollowMoveState(ship);
                return;
            }

            ship.resetInteractionEmotionState();
            if (owner instanceof Player player && ship.playerHasCombatRation(player)) {
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
            Vec3 moveTarget = owner.position();
            if (teamId >= 0 && slotId >= 0 && owner instanceof Player ownerPlayer) {
                org.trp.shincolle.attachment.AdmiralData data = PlayerStateService.admiralData(ownerPlayer);
                int formationId = data.getFormationID(teamId);
                updateFormationDirection(owner);
                moveTarget = org.trp.shincolle.utility.FormationHelper.getFormationPos(
                        formationId, slotId, owner.position(), this.formationDir[0], this.formationDir[1]);
            }

            if (!this.followOwnerActive) {
                float minDist = resolveFollowMinDistance(ship);
                float maxDist = resolveFollowMaxDistance(ship, minDist);
                Shincolle.diagnosticLog("{} start ship={} owner={} distSq={} minDist={} maxDist={}",
                        FOLLOW_DEBUG_PREFIX, ship.getUUID(), owner.getUUID(), ship.distanceToSqr(owner), minDist, maxDist);
            }
            this.followOwnerActive = true;
            ShipMovementCoordinator movement = ship.followOwnerMovementCoordinator();
            boolean moved = movement.moveTo(moveTarget, ShipAiNumbers.FOLLOW_OWNER_SPEED);
            Shincolle.diagnosticLog("{} move ship={} owner={} target={} moved={}",
                    FOLLOW_DEBUG_PREFIX, ship.getUUID(), owner.getUUID(), moveTarget, moved);
            double distSq = ship.distanceToSqr(owner);
            this.followRecovery.trackProgress(ship.position());
            boolean force = this.followRecovery.isStuckLongerThan(ShipAiNumbers.MOVE_STUCK_TICK_LIMIT);
            if (!this.followRecovery.shouldTryTeleportThrottled(force, distSq,
                    ShipAiNumbers.TELEPORT_DISTANCE_SQ, ShipAiNumbers.FOLLOW_TELEPORT_COOLDOWN_TICKS)) {
                return;
            }
            if (!movement.teleportNearLiving(owner, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)) {
                return;
            }
            Shincolle.diagnosticLog("{} teleportRecovery ship={} owner={} force={} distSq={} stuckTicks={}",
                    FOLLOW_DEBUG_PREFIX,
                    ship.getUUID(), owner.getUUID(), force, distSq, this.followRecovery.stuckTicks());
            this.followRecovery.reset(ship.position());
        }

        private void clearFollowMoveState(EntityShipBase ship) {
            LivingEntity owner = ship.getOwner();
            if (this.followOwnerActive && owner != null) {
                float minDist = resolveFollowMinDistance(ship);
                float maxDist = resolveFollowMaxDistance(ship, minDist);
                Shincolle.diagnosticLog("{} stop ship={} owner={} distSq={} minDist={} maxDist={}",
                        FOLLOW_DEBUG_PREFIX, ship.getUUID(), owner.getUUID(), ship.distanceToSqr(owner), minDist, maxDist);
            }
            this.followRecovery.clear();
            this.hasOwnerPos = false;
            this.followOwnerActive = false;
            ship.followOwnerMovementCoordinator().stop();
        }

        private void logFollowSkipIfNeeded(EntityShipBase ship) {
            if (ship.tickCount < this.nextFollowSkipLogTick) {
                return;
            }

            this.nextFollowSkipLogTick = ship.tickCount + 40;
            LivingEntity owner = ship.getOwner();
            double distSq = owner == null ? -1.0D : ship.distanceToSqr(owner);
            float minDist = resolveFollowMinDistance(ship);
            float maxDist = resolveFollowMaxDistance(ship, minDist);
            Shincolle.diagnosticLog(
                    "{} skip ship={} owner={} ownerUuid={} ownerPresent={} tame={} hostile={} distSq={} minDist={} maxDist={} canMove={} shouldFollow={} reason={} pointer={} guard={} target={} noFuel={} sitting={}",
                    FOLLOW_DEBUG_PREFIX,
                    ship.getUUID(),
                    owner == null ? null : owner.getUUID(),
                    ship.getOwnerUUID(),
                    owner != null,
                    ship.isTame(),
                    ship.isHostileShipMob(),
                    distSq,
                    minDist,
                    maxDist,
                    canMove(ship),
                    ship.shouldFollowOwner(),
                    ship.explainFollowBlockReason(),
                    ship.hasPointerTarget() || ship.hasPointerTargetEntity(),
                    ship.getGuardTarget().isActive(),
                    ship.getTarget() != null,
                    ship.isNoFuel(),
                    ship.getIsSitting());
        }

        private void logMovementBranch(EntityShipBase ship, String branch) {
            if (ship.tickCount < this.nextFollowSkipLogTick) {
                return;
            }

            this.nextFollowSkipLogTick = ship.tickCount + 40;
            LivingEntity owner = ship.getOwner();
            double distSq = owner == null ? -1.0D : ship.distanceToSqr(owner);
            Shincolle.diagnosticLog(
                    "{} branch ship={} branch={} ownerPresent={} distSq={} canMove={} pointer={} guard={} target={} shouldFollow={} reason={}",
                    FOLLOW_DEBUG_PREFIX,
                    ship.getUUID(),
                    branch,
                    owner != null,
                    distSq,
                    canMove(ship),
                    ship.hasPointerTarget() || ship.hasPointerTargetEntity(),
                    ship.getGuardTarget().isActive(),
                    ship.getTarget() != null,
                    ship.shouldFollowOwner(),
                    ship.explainFollowBlockReason());
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

        private float resolveFollowMinDistance(EntityShipBase ship) {
            int configured = ship.getStateMinor(ShipContainerMenu.STATE_MINOR_FOLLOW_MIN);
            if (configured <= 0) {
                return ShipAiNumbers.FOLLOW_OWNER_MIN_DIST;
            }
            return (float) Mth.clamp(configured, ShipAiNumbers.FOLLOW_MIN_DIST_CONFIG_MIN,
                    ShipAiNumbers.FOLLOW_MIN_DIST_CONFIG_MAX);
        }

        private float resolveFollowMaxDistance(EntityShipBase ship, float minDist) {
            int configured = ship.getStateMinor(ShipContainerMenu.STATE_MINOR_FOLLOW_MAX);
            if (configured <= 0) {
                return Math.max(ShipAiNumbers.FOLLOW_OWNER_MAX_DIST, minDist + ShipAiNumbers.FOLLOW_MAX_DIST_PADDING);
            }
            int minValue = Math.max(ShipAiNumbers.FOLLOW_MAX_DIST_CONFIG_MIN, Mth.floor(minDist) + 1);
            return (float) Mth.clamp(configured, minValue, ShipAiNumbers.FOLLOW_MAX_DIST_CONFIG_MAX);
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
            Entity guardedEntity = ship.getGuardedEntity();
            ShipGuardTarget guardTarget = ship.getGuardTarget();
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
            if (ship.hasPointerTargetEntity() && ship.getPointerTargetEntity() != null) {
                ship.getLookControl().setLookAt(ship.getPointerTargetEntity(), ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH);
                return;
            }
            LivingEntity owner = ship.getOwner();
            if (owner != null && shouldFollowOwnerLook(ship, owner)) {
                ship.getLookControl().setLookAt(owner, ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH);
                return;
            }
            if (guardTarget.isBlock()) {
                Vec3 target = guardTarget.blockCenter();
                ship.getLookControl().setLookAt(target.x, target.y + ship.getEyeHeight(), target.z,
                        ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH);
                return;
            }
            Player player = level.getNearestPlayer(ship, ShipAiNumbers.LOOK_AT_PLAYER_DISTANCE);
            if (player != null) {
                ship.getLookControl().setLookAt(player, ShipAiNumbers.LOOK_YAW, ShipAiNumbers.LOOK_PITCH);
            }
        }

        private boolean shouldFollowOwnerLook(EntityShipBase ship, LivingEntity owner) {
            if (ship.shouldFollowOwner()) {
                return true;
            }
            return ship.distanceToSqr(owner) <= ShipAiNumbers.GUARD_OWNER_LOOK_MAX_DISTANCE_SQ
                    && ship.getGuardTarget().isActive();
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
                    && !ship.hasPointerTarget()
                    && ship.getTarget() == null
                    && !ship.shouldFollowOwner()
                    && !ship.getGuardTarget().isActive()
                    && ship.getRandom().nextInt(ShipAiNumbers.RANDOM_STROLL_CHANCE) == 0;
        }

        @Override
        protected void start(ServerLevel level, EntityShipBase ship, long gameTime) {
            Vec3 target = net.minecraft.world.entity.ai.util.DefaultRandomPos.getPos(
                    ship, ShipAiNumbers.RANDOM_STROLL_HORIZONTAL_RANGE, ShipAiNumbers.RANDOM_STROLL_VERTICAL_RANGE);
            if (target != null) {
                Shincolle.diagnosticLog("[SCIdleDiag] randomStroll ship={} target={}", ship.getUUID(), target);
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
