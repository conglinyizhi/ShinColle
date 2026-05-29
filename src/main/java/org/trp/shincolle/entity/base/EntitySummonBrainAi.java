package org.trp.shincolle.entity.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;

final class EntitySummonBrainAi {
    static final List<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE
    );
    static final List<SensorType<? extends net.minecraft.world.entity.ai.sensing.Sensor<? super EntitySummonBase>>> SENSOR_TYPES =
            ImmutableList.of(
                    SensorType.NEAREST_LIVING_ENTITIES,
                    SensorType.NEAREST_PLAYERS
            );

    private EntitySummonBrainAi() {
    }

    static Brain<?> makeBrain(EntitySummonBase summon, Brain<EntitySummonBase> brain) {
        brain.addActivity(Activity.CORE, ImmutableList.of(
                Pair.of(SummonAiNumbers.ATTACK_BEHAVIOR_PRIORITY, new SummonAttackBehavior()),
                Pair.of(SummonAiNumbers.FOLLOW_BEHAVIOR_PRIORITY, new SummonFollowCarrierBehavior()),
                Pair.of(SummonAiNumbers.LOOK_AT_PLAYER_PRIORITY, new SummonLookAtPlayerBehavior()),
                Pair.of(SummonAiNumbers.RANDOM_LOOK_PRIORITY, new SummonRandomLookBehavior())
        ));
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(SummonAiNumbers.RANDOM_STROLL_PRIORITY, new SummonRandomStrollBehavior())
        ));
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    @SuppressWarnings("unchecked")
    static void tick(ServerLevel level, EntitySummonBase summon) {
        Brain<EntitySummonBase> brain = (Brain<EntitySummonBase>) summon.getBrain();
        brain.tick(level, summon);
        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }

    private static final class SummonAttackBehavior extends Behavior<EntitySummonBase> {
        private final ShipMovementCoordinator movement;
        private int attackDelay;

        SummonAttackBehavior() {
            super(ImmutableMap.of());
            this.movement = null;
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntitySummonBase summon) {
            LivingEntity target = summon.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        protected void start(ServerLevel level, EntitySummonBase summon, long gameTime) {
            summon.attackMovementCoordinator().reset();
        }

        @Override
        protected void tick(ServerLevel level, EntitySummonBase summon, long gameTime) {
            LivingEntity target = summon.getTarget();
            if (target == null) {
                return;
            }

            summon.getLookControl().setLookAt(target, SummonAiNumbers.ATTACK_LOOK_YAW, SummonAiNumbers.ATTACK_LOOK_PITCH);
            double distSq = summon.distanceToSqr(target);
            if (distSq > summon.getAttackRangeSq()) {
                summon.attackMovementCoordinator().moveTo(target, SummonAiNumbers.ATTACK_MOVE_SPEED);
            } else {
                summon.attackMovementCoordinator().stop();
                if (this.attackDelay <= 0) {
                    summon.performAttack(target);
                    this.attackDelay = SummonAiNumbers.ATTACK_DELAY_TICKS;
                }
            }

            if (this.attackDelay > 0) {
                this.attackDelay--;
            }
        }
    }

    private static final class SummonFollowCarrierBehavior extends Behavior<EntitySummonBase> {
        private int timeToRecalcPath;

        SummonFollowCarrierBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntitySummonBase summon) {
            EntityShipBase carrier = summon.getCarrier();
            return carrier != null
                    && carrier.isAlive()
                    && summon.distanceToSqr(carrier) > SummonAiNumbers.FOLLOW_CARRIER_DISTANCE_SQ
                    && summon.getTarget() == null;
        }

        @Override
        protected void start(ServerLevel level, EntitySummonBase summon, long gameTime) {
            summon.followMovementCoordinator().reset();
            this.timeToRecalcPath = 0;
        }

        @Override
        protected void tick(ServerLevel level, EntitySummonBase summon, long gameTime) {
            EntityShipBase carrier = summon.getCarrier();
            if (carrier == null) {
                return;
            }
            summon.getLookControl().setLookAt(carrier, SummonAiNumbers.ATTACK_LOOK_YAW, SummonAiNumbers.ATTACK_LOOK_PITCH);
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = SummonAiNumbers.FOLLOW_RECALC_TICKS;
                summon.followMovementCoordinator().moveTo(carrier, SummonAiNumbers.FOLLOW_CARRIER_SPEED);
            }
        }
    }

    private static final class SummonLookAtPlayerBehavior extends Behavior<EntitySummonBase> {
        SummonLookAtPlayerBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntitySummonBase summon) {
            return summon.getTarget() == null;
        }

        @Override
        protected void tick(ServerLevel level, EntitySummonBase summon, long gameTime) {
            Player player = level.getNearestPlayer(summon, SummonAiNumbers.LOOK_AT_PLAYER_DISTANCE);
            if (player != null) {
                summon.getLookControl().setLookAt(player, SummonAiNumbers.ATTACK_LOOK_YAW, SummonAiNumbers.ATTACK_LOOK_PITCH);
            }
        }
    }

    private static final class SummonRandomLookBehavior extends Behavior<EntitySummonBase> {
        SummonRandomLookBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntitySummonBase summon) {
            return summon.getTarget() == null && summon.getRandom().nextInt(SummonAiNumbers.RANDOM_LOOK_CHANCE) == 0;
        }
    }

    private static final class SummonRandomStrollBehavior extends Behavior<EntitySummonBase> {
        SummonRandomStrollBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntitySummonBase summon) {
            return summon.getTarget() == null && summon.getRandom().nextInt(SummonAiNumbers.RANDOM_STROLL_CHANCE) == 0;
        }

        @Override
        protected void start(ServerLevel level, EntitySummonBase summon, long gameTime) {
            Vec3 target = net.minecraft.world.entity.ai.util.DefaultRandomPos.getPos(
                    summon, SummonAiNumbers.RANDOM_STROLL_HORIZONTAL_RANGE, SummonAiNumbers.RANDOM_STROLL_VERTICAL_RANGE);
            if (target != null) {
                summon.followMovementCoordinator().moveTo(target, SummonAiNumbers.RANDOM_STROLL_SPEED);
            }
        }
    }
}
