package org.trp.shincolle.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;

final class AircraftBrainAi {
    static final List<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE
    );
    static final List<SensorType<? extends net.minecraft.world.entity.ai.sensing.Sensor<? super EntityAircraftBase>>> SENSOR_TYPES =
            ImmutableList.of();

    private AircraftBrainAi() {
    }

    static Brain<?> makeBrain(EntityAircraftBase aircraft, Brain<EntityAircraftBase> brain) {
        brain.addActivity(Activity.CORE, ImmutableList.of(
                Pair.of(AircraftAiNumbers.ATTACK_BEHAVIOR_PRIORITY, new AircraftAttackBehavior())
        ));
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.CORE);
        brain.useDefaultActivity();
        return brain;
    }

    static void tick(ServerLevel level, EntityAircraftBase aircraft) {
        Brain<EntityAircraftBase> brain = brain(aircraft);
        syncAttackTargetMemory(aircraft);
        brain.tick(level, aircraft);
        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.CORE));
    }

    @SuppressWarnings("unchecked")
    private static Brain<EntityAircraftBase> brain(EntityAircraftBase aircraft) {
        return (Brain<EntityAircraftBase>) aircraft.getBrain();
    }

    private static void syncAttackTargetMemory(EntityAircraftBase aircraft) {
        Brain<?> brain = aircraft.getBrain();
        Entity target = aircraft.getMissionTarget();
        if (AircraftBrainDecisionResolver.canAttackMissionTarget(decisionState(aircraft, target))
                && target instanceof LivingEntity livingTarget) {
            brain.setMemory(MemoryModuleType.ATTACK_TARGET, livingTarget);
        } else {
            brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
        }
    }

    private static void setPointWalkAndLookMemory(EntityAircraftBase aircraft, Vec3 target, double speed, int closeEnoughDist) {
        BehaviorUtils.setWalkAndLookTargetMemories(aircraft, BlockPos.containing(target), (float) speed, closeEnoughDist);
    }

    private static void setEntityLookMemory(EntityAircraftBase aircraft, Entity target) {
        if (target instanceof LivingEntity livingTarget) {
            BehaviorUtils.lookAtEntity(aircraft, livingTarget);
        }
    }

    private static void clearWalkAndLookMemory(EntityAircraftBase aircraft) {
        Brain<?> brain = aircraft.getBrain();
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
        brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    private static AircraftBrainDecisionResolver.State decisionState(EntityAircraftBase host, Entity targetEntity) {
        return new AircraftBrainDecisionResolver.State(
                targetEntity != null,
                targetEntity != null && targetEntity.isAlive(),
                host.isMissionLightAircraft(),
                host.hasAmmoLight(),
                host.hasAmmoHeavy(),
                host.getMissionTick(),
                host.getAttackDelay(),
                targetEntity != null && host.hasLineOfSight(targetEntity),
                targetEntity == null ? -1.0D : host.distanceToSqr(targetEntity.getX(),
                        targetEntity.getY() + AircraftAiNumbers.ATTACK_TARGET_Y_OFFSET, targetEntity.getZ())
        );
    }

    private static final class AircraftAttackBehavior extends Behavior<EntityAircraftBase> {
        private Entity target;
        private Vec3 randPos;

        AircraftAttackBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntityAircraftBase host) {
            Entity targetEntity = host.getMissionTarget();
            if (AircraftBrainDecisionResolver.shouldStartAttack(decisionState(host, targetEntity))) {
                this.target = targetEntity;
                return true;
            }
            return false;
        }

        @Override
        protected void start(ServerLevel level, EntityAircraftBase host, long gameTime) {
            host.attackMovementCoordinator().reset();
            syncAttackTargetMemory(host);
            updateRandomPos(host);
        }

        @Override
        protected boolean canStillUse(ServerLevel level, EntityAircraftBase host, long gameTime) {
            Entity targetEntity = host.getMissionTarget();
            if (!AircraftBrainDecisionResolver.canAttackMissionTarget(decisionState(host, targetEntity))) {
                return false;
            }
            this.target = targetEntity;
            return true;
        }

        @Override
        protected void stop(ServerLevel level, EntityAircraftBase host, long gameTime) {
            this.target = null;
            this.randPos = null;
            clearWalkAndLookMemory(host);
            host.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            host.attackMovementCoordinator().stop();
        }

        @Override
        protected void tick(ServerLevel level, EntityAircraftBase host, long gameTime) {
            if (this.target == null) return;

            AircraftBrainDecisionResolver.State state = decisionState(host, this.target);
            syncAttackTargetMemory(host);
            setEntityLookMemory(host, this.target);

            if ((host.tickCount & AircraftAiNumbers.ATTACK_RECALC_INTERVAL_MASK) == 0
                    || this.randPos == null
                    || host.attackMovementCoordinator().isNavigationDone()) {
                updateRandomPos(host);
            }
            if (this.randPos == null) {
                clearWalkAndLookMemory(host);
                host.attackMovementCoordinator().stop();
                return;
            }

            double speed = AircraftBrainDecisionResolver.attackMoveSpeed(state);
            setPointWalkAndLookMemory(host, this.randPos, speed, 1);
            host.attackMovementCoordinator().moveTo(this.randPos, speed);

            if (AircraftBrainDecisionResolver.shouldFire(state)) {
                if (host.isMissionLightAircraft() && host.hasAmmoLight()) {
                    host.attackWithLightAmmo(this.target);
                } else if (!host.isMissionLightAircraft() && host.hasAmmoHeavy()) {
                    host.attackWithHeavyAmmo(this.target);
                }
            }
        }

        private void updateRandomPos(EntityAircraftBase host) {
            Entity ref = this.target != null ? this.target : host;
            this.randPos = host.getRandomCruisePos(ref);
        }

    }
}
