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
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.phys.Vec3;
import org.trp.shincolle.Shincolle;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

final class EntityMountBrainAi {
    static final List<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE
    );
    static final List<SensorType<? extends net.minecraft.world.entity.ai.sensing.Sensor<? super EntityMountBase>>> SENSOR_TYPES =
            ImmutableList.of();

    private EntityMountBrainAi() {
    }

    static Brain<?> makeBrain(EntityMountBase mount, Brain<EntityMountBase> brain) {
        brain.addActivity(Activity.CORE, ImmutableList.of(
                Pair.of(MountAiNumbers.FOLLOW_BEHAVIOR_PRIORITY, new MountFollowBehavior()),
                Pair.of(MountAiNumbers.RANGE_ATTACK_BEHAVIOR_PRIORITY, new MountRangeAttackBehavior()),
                Pair.of(MountAiNumbers.RANDOM_STROLL_BEHAVIOR_PRIORITY, new MountRandomStrollBehavior())
        ));
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.CORE);
        brain.useDefaultActivity();
        return brain;
    }

    @SuppressWarnings("unchecked")
    static void tick(ServerLevel level, EntityMountBase mount) {
        Brain<EntityMountBase> brain = (Brain<EntityMountBase>) mount.getBrain();
        syncAttackTargetMemory(mount, brain);
        brain.tick(level, mount);
        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.CORE));
    }

    private static void syncAttackTargetMemory(EntityMountBase mount, Brain<EntityMountBase> brain) {
        LivingEntity target = mount.getTarget();
        if (target != null && target.isAlive()) {
            brain.setMemory(MemoryModuleType.ATTACK_TARGET, target);
        } else {
            brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
        }
    }

    private static void setPointWalkAndLookMemory(EntityMountBase mount, Vec3 target, double speed, int closeEnoughDist) {
        BehaviorUtils.setWalkAndLookTargetMemories(mount, BlockPos.containing(target), (float) speed, closeEnoughDist);
    }

    private static void setEntityWalkAndLookMemory(EntityMountBase mount, Entity target, double speed, int closeEnoughDist) {
        if (target instanceof LivingEntity livingTarget) {
            BehaviorUtils.setWalkAndLookTargetMemories(mount, livingTarget, (float) speed, closeEnoughDist);
            return;
        }
        setPointWalkAndLookMemory(mount, target.position(), speed, closeEnoughDist);
    }

    private static void setEntityLookMemory(EntityMountBase mount, LivingEntity target) {
        BehaviorUtils.lookAtEntity(mount, target);
    }

    private static void clearWalkAndLookMemory(EntityMountBase mount) {
        Brain<?> brain = mount.getBrain();
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
        brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    private static boolean shouldFollowHostState(EntityMountBase mount, EntityShipBase host) {
        return MountBrainDecisionResolver.shouldFollowHost(decisionState(mount, host));
    }

    private static MountBrainDecisionResolver.State decisionState(EntityMountBase mount, EntityShipBase host) {
        return decisionState(mount, host, 0, 0, 0);
    }

    private static MountBrainDecisionResolver.State decisionState(EntityMountBase mount, EntityShipBase host,
                                                                 int aimTick, int lightDelay, int heavyDelay) {
        LivingEntity owner = host == null ? null : host.getOwner();
        LivingEntity attackTarget = mount.getTarget();
        int aimRequiredTicks = host == null ? Integer.MAX_VALUE
                : (int) (MountAiNumbers.AIM_SCALE_TICKS * (MountAiNumbers.LEVEL_CAP - host.getLevel()) / MountAiNumbers.LEVEL_CAP)
                + MountAiNumbers.AIM_BASE_TICKS;
        double attackRangeSq = host == null ? 0.0D
                : Math.pow(Math.max(MountAiNumbers.MIN_ATTACK_RANGE, host.getLegacyShipStats().getAttackRange()), 2);
        return new MountBrainDecisionResolver.State(
                host != null,
                host != null && host.isAlive(),
                host != null && host.isOrderedToSit(),
                mount.isPassenger(),
                host != null && host.hasPointerTarget(),
                host != null && hasGuardTarget(host),
                owner != null,
                owner == null ? -1.0D : mount.distanceToSqr(owner),
                mount.getBbWidth(),
                host == null ? 0 : host.getStateMinor(11),
                attackTarget != null,
                attackTarget != null && attackTarget.isAlive(),
                mount.getRandom().nextInt(MountAiNumbers.RANDOM_STROLL_CHANCE) == 0,
                aimTick,
                aimRequiredTicks,
                attackTarget == null ? -1.0D : mount.distanceToSqr(attackTarget),
                attackRangeSq,
                host != null && host.isStateLightAttack(),
                host == null ? 0 : host.getAmmoLight(),
                lightDelay,
                host != null && host.isStateHeavyAttack(),
                host == null ? 0 : host.getAmmoHeavy(),
                heavyDelay
        );
    }

    private static boolean hasGuardTarget(EntityShipBase host) {
        ShipGuardTarget guardTarget = host.getGuardTarget();
        if (guardTarget.isEntity()) {
            Entity guarded = host.getGuardedEntity();
            return guarded != null && guarded.isAlive();
        }
        return guardTarget.isBlock() && guardTarget.isIn(host.level());
    }

    private static final class MountFollowBehavior extends Behavior<EntityMountBase> {
        private final ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();
        private FollowRecoveryTargetKey lastRecoveryTargetKey;

        MountFollowBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntityMountBase mount) {
            return shouldFollowHostState(mount, mount.getHost());
        }

        @Override
        protected void start(ServerLevel level, EntityMountBase mount, long gameTime) {
            this.lastRecoveryTargetKey = null;
            this.recovery.reset(mount.position());
            mount.followMovementCoordinator().reset();
        }

        @Override
        protected void stop(ServerLevel level, EntityMountBase mount, long gameTime) {
            this.lastRecoveryTargetKey = null;
            this.recovery.clear();
            clearWalkAndLookMemory(mount);
            mount.followMovementCoordinator().stop();
        }

        @Override
        protected void tick(ServerLevel level, EntityMountBase mount, long gameTime) {
            EntityShipBase h = mount.getHost();
            if (h == null) return;

            if (h.hasPointerTarget()) {
                Vec3 pt = h.getPointerTarget();
                resetRecoveryIfTargetChanged(mount, FollowRecoveryTargetKey.point("pointer", pt,
                        EntityShipBase.getLegacyDimensionId(h.level())));
                setPointWalkAndLookMemory(mount, pt, MountAiNumbers.FOLLOW_MOVE_SPEED, 1);
                mount.followMovementCoordinator().moveTo(pt, MountAiNumbers.FOLLOW_MOVE_SPEED);
                trackAndRecoverPoint(mount, pt, "pointer");
                return;
            }

            if (tickGuardTarget(mount, h)) {
                return;
            }

            LivingEntity owner = h.getOwner();
            if (owner == null) return;

            mount.getLookControl().setLookAt(owner, MountAiNumbers.LOOK_YAW, MountAiNumbers.LOOK_PITCH);
            setEntityWalkAndLookMemory(mount, owner, MountAiNumbers.FOLLOW_MOVE_SPEED, 1);
            setEntityLookMemory(mount, owner);
            resetRecoveryIfTargetChanged(mount, FollowRecoveryTargetKey.entity("owner", owner));
            mount.followMovementCoordinator().moveTo(owner, MountAiNumbers.FOLLOW_MOVE_SPEED);
            trackAndRecoverLiving(mount, owner, "owner");
        }

        private boolean tickGuardTarget(EntityMountBase mount, EntityShipBase host) {
            ShipGuardTarget guardTarget = host.getGuardTarget();
            if (guardTarget.isEntity()) {
                Entity guarded = host.getGuardedEntity();
                if (guarded == null || !guarded.isAlive()) {
                    return false;
                }

                mount.getLookControl().setLookAt(guarded, MountAiNumbers.LOOK_YAW, MountAiNumbers.LOOK_PITCH);
                setEntityWalkAndLookMemory(mount, guarded, MountAiNumbers.FOLLOW_MOVE_SPEED, 1);
                resetRecoveryIfTargetChanged(mount, FollowRecoveryTargetKey.entity("guardEntity", guarded));
                mount.followMovementCoordinator().moveTo(guarded, MountAiNumbers.FOLLOW_MOVE_SPEED);
                trackAndRecoverEntity(mount, guarded, "guardEntity");
                return true;
            }

            if (guardTarget.isBlock() && guardTarget.isIn(host.level())) {
                Vec3 guardPos = guardTarget.blockCenter();
                mount.getLookControl().setLookAt(guardPos.x, guardPos.y, guardPos.z, MountAiNumbers.LOOK_YAW, MountAiNumbers.LOOK_PITCH);
                setPointWalkAndLookMemory(mount, guardPos, MountAiNumbers.FOLLOW_MOVE_SPEED, 1);
                resetRecoveryIfTargetChanged(mount, FollowRecoveryTargetKey.guardBlock(guardTarget));
                mount.followMovementCoordinator().moveTo(guardPos, MountAiNumbers.FOLLOW_MOVE_SPEED);
                trackAndRecoverPoint(mount, guardPos, "guardBlock");
                return true;
            }

            return false;
        }

        private void trackAndRecoverLiving(EntityMountBase mount, LivingEntity target, String reason) {
            if (target == null) return;
            trackAndRecover(mount, target.position(), mount.distanceToSqr(target), reason,
                    () -> mount.followMovementCoordinator().teleportNearLiving(target, MountAiNumbers.TELEPORT_VERTICAL_OFFSET));
        }

        private void trackAndRecoverEntity(EntityMountBase mount, Entity target, String reason) {
            if (target == null) return;
            if (target instanceof LivingEntity livingTarget) {
                trackAndRecoverLiving(mount, livingTarget, reason);
                return;
            }
            trackAndRecover(mount, target.position(), mount.distanceToSqr(target), reason,
                    () -> mount.followMovementCoordinator().teleportNearPoint(target.position(), MountAiNumbers.TELEPORT_VERTICAL_OFFSET));
        }

        private void trackAndRecoverPoint(EntityMountBase mount, Vec3 target, String reason) {
            if (target == null) return;
            trackAndRecover(mount, target, mount.distanceToSqr(target), reason,
                    () -> mount.followMovementCoordinator().teleportNearPoint(target, MountAiNumbers.TELEPORT_VERTICAL_OFFSET));
        }

        private void trackAndRecover(EntityMountBase mount, Vec3 target, double distSq, String reason, java.util.function.BooleanSupplier teleport) {
            this.recovery.trackProgress(mount.position());
            boolean force = this.recovery.isStuckLongerThan(MountAiNumbers.FOLLOW_STUCK_TICK_LIMIT);
            if (!this.recovery.shouldTryTeleportThrottled(force, distSq,
                    MountAiNumbers.FOLLOW_TELEPORT_DISTANCE_SQ, MountAiNumbers.FOLLOW_TELEPORT_COOLDOWN_TICKS)) {
                return;
            }
            if (!teleport.getAsBoolean()) {
                return;
            }

            Shincolle.debugLog("[SCMoveDiag] MountFollow teleportRecovery mount={} host={} reason={} target={} force={} distSq={}",
                    mount.getUUID(), mount.getHostUUID(), reason, target, force, distSq);
            this.recovery.reset(mount.position());
        }

        private void resetRecoveryIfTargetChanged(EntityMountBase mount, FollowRecoveryTargetKey targetKey) {
            if (Objects.equals(this.lastRecoveryTargetKey, targetKey)) {
                return;
            }
            this.lastRecoveryTargetKey = targetKey;
            this.recovery.reset(mount.position());
            mount.followMovementCoordinator().reset();
        }
    }

    private static final class MountRangeAttackBehavior extends Behavior<EntityMountBase> {
        private LivingEntity target;
        private int aimTick = 0;
        private int lightDelay = 0;
        private int heavyDelay = 0;

        MountRangeAttackBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntityMountBase mount) {
            EntityShipBase h = mount.getHost();
            this.target = mount.getTarget();
            return MountBrainDecisionResolver.shouldRangeAttack(decisionState(mount, h));
        }

        @Override
        protected void start(ServerLevel level, EntityMountBase mount, long gameTime) {
            this.aimTick = 0;
            this.lightDelay = 0;
            this.heavyDelay = 0;
            if (this.target != null && this.target.isAlive()) {
                mount.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, this.target);
            }
        }

        @Override
        protected void stop(ServerLevel level, EntityMountBase mount, long gameTime) {
            this.target = null;
            mount.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        }

        @Override
        protected void tick(ServerLevel level, EntityMountBase mount, long gameTime) {
            if (this.target == null || !this.target.isAlive()) return;
            EntityShipBase h = mount.getHost();
            if (h == null) return;

            mount.getLookControl().setLookAt(this.target, MountAiNumbers.LOOK_YAW, MountAiNumbers.LOOK_PITCH);
            mount.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, this.target);
            setEntityLookMemory(mount, this.target);
            ++this.aimTick;
            if (this.lightDelay > 0) --this.lightDelay;
            if (this.heavyDelay > 0) --this.heavyDelay;
            MountBrainDecisionResolver.State state = decisionState(mount, h, this.aimTick, this.lightDelay, this.heavyDelay);
            if (MountBrainDecisionResolver.shouldFireLight(state)) {
                h.performLightAttack(this.target);
                this.lightDelay = h.getLegacyShipStats().getLightDelay();
            }
            if (MountBrainDecisionResolver.shouldFireHeavy(state)) {
                h.performHeavyAttack(this.target);
                this.heavyDelay = h.getLegacyShipStats().getHeavyDelay();
            }
        }
    }

    private static final class MountRandomStrollBehavior extends Behavior<EntityMountBase> {
        MountRandomStrollBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntityMountBase mount) {
            EntityShipBase h = mount.getHost();
            return MountBrainDecisionResolver.shouldRandomStroll(decisionState(mount, h));
        }

        @Override
        protected void start(ServerLevel level, EntityMountBase mount, long gameTime) {
            Vec3 target = net.minecraft.world.entity.ai.util.DefaultRandomPos.getPos(
                    mount, MountAiNumbers.RANDOM_STROLL_HORIZONTAL_RANGE, MountAiNumbers.RANDOM_STROLL_VERTICAL_RANGE);
            if (target != null) {
                setPointWalkAndLookMemory(mount, target, MountAiNumbers.RANDOM_STROLL_SPEED, 1);
                mount.followMovementCoordinator().moveTo(target, MountAiNumbers.RANDOM_STROLL_SPEED);
            }
        }
    }

    private record FollowRecoveryTargetKey(String reason, UUID entityId, int x, int y, int z, int dimensionId) {
        private static FollowRecoveryTargetKey entity(String reason, Entity target) {
            return new FollowRecoveryTargetKey(reason, target.getUUID(), 0, 0, 0,
                    EntityShipBase.getLegacyDimensionId(target.level()));
        }

        private static FollowRecoveryTargetKey guardBlock(ShipGuardTarget target) {
            return new FollowRecoveryTargetKey("guardBlock", null, target.x(), target.y(), target.z(), target.dimensionId());
        }

        private static FollowRecoveryTargetKey point(String reason, Vec3 target, int dimensionId) {
            return new FollowRecoveryTargetKey(reason, null, Mth.floor(target.x), Mth.floor(target.y), Mth.floor(target.z),
                    dimensionId);
        }
    }
}
