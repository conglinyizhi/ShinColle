package org.trp.shincolle.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
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
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE
    );
    static final List<SensorType<? extends net.minecraft.world.entity.ai.sensing.Sensor<? super EntityAircraftBase>>> SENSOR_TYPES =
            ImmutableList.of(
                    SensorType.NEAREST_LIVING_ENTITIES,
                    SensorType.NEAREST_PLAYERS
            );

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

    @SuppressWarnings("unchecked")
    static void tick(ServerLevel level, EntityAircraftBase aircraft) {
        Brain<EntityAircraftBase> brain = (Brain<EntityAircraftBase>) aircraft.getBrain();
        brain.tick(level, aircraft);
        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.CORE));
    }

    private static final class AircraftAttackBehavior extends Behavior<EntityAircraftBase> {
        private Entity target;
        private Vec3 randPos;
        private double distSq;
        private float rangeSq;

        AircraftAttackBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntityAircraftBase host) {
            Entity targetEntity = host.getMissionTarget();
            if (!canAttackMissionTarget(host, targetEntity)) {
                return false;
            }
            if (host.getMissionTick() > AircraftAiNumbers.ATTACK_ACTIVATION_TICKS) {
                this.target = targetEntity;
                return true;
            }
            return false;
        }

        @Override
        protected void start(ServerLevel level, EntityAircraftBase host, long gameTime) {
            float attackRange = host.isMissionLightAircraft() ? AircraftAiNumbers.ATTACK_RANGE_LIGHT : AircraftAiNumbers.ATTACK_RANGE_HEAVY;
            this.rangeSq = attackRange * attackRange;
            host.attackMovementCoordinator().reset();
            updateRandomPos(host);
        }

        @Override
        protected boolean canStillUse(ServerLevel level, EntityAircraftBase host, long gameTime) {
            Entity targetEntity = host.getMissionTarget();
            if (!canAttackMissionTarget(host, targetEntity)) {
                return false;
            }
            this.target = targetEntity;
            return true;
        }

        @Override
        protected void stop(ServerLevel level, EntityAircraftBase host, long gameTime) {
            this.target = null;
            this.randPos = null;
            host.attackMovementCoordinator().stop();
        }

        @Override
        protected void tick(ServerLevel level, EntityAircraftBase host, long gameTime) {
            if (this.target == null) return;

            this.distSq = host.distanceToSqr(this.target.getX(), this.target.getY() + AircraftAiNumbers.ATTACK_TARGET_Y_OFFSET, this.target.getZ());

            if ((host.tickCount & AircraftAiNumbers.ATTACK_RECALC_INTERVAL_MASK) == 0 || this.randPos == null || host.getNavigation().isDone()) {
                updateRandomPos(host);
            }

            double speed = host.getAttackDelay() > 0
                    ? AircraftAiNumbers.ATTACK_SPEED_SLOW
                    : (this.distSq > this.rangeSq ? AircraftAiNumbers.ATTACK_SPEED_FAST : AircraftAiNumbers.ATTACK_SPEED_SLOW);
            host.attackMovementCoordinator().moveTo(this.randPos, speed);

            if (host.getAttackDelay() <= 0 && host.hasLineOfSight(this.target) && this.distSq < this.rangeSq) {
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

        private boolean canAttackMissionTarget(EntityAircraftBase host, Entity targetEntity) {
            if (targetEntity == null || !targetEntity.isAlive()) {
                return false;
            }
            return host.isMissionLightAircraft() ? host.hasAmmoLight() : host.hasAmmoHeavy();
        }
    }
}
