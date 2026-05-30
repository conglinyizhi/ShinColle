package org.trp.shincolle.entity.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
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
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE
    );
    static final List<SensorType<? extends net.minecraft.world.entity.ai.sensing.Sensor<? super EntitySummonBase>>> SENSOR_TYPES =
            ImmutableList.of();

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
        syncAttackTargetMemory(summon, brain);
        brain.tick(level, summon);
        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }

    private static void syncAttackTargetMemory(EntitySummonBase summon, Brain<EntitySummonBase> brain) {
        LivingEntity target = summon.getTarget();
        if (target != null && target.isAlive()) {
            brain.setMemory(MemoryModuleType.ATTACK_TARGET, target);
        } else {
            brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
        }
    }

    private static void setPointWalkAndLookMemory(EntitySummonBase summon, Vec3 target, double speed, int closeEnoughDist) {
        BehaviorUtils.setWalkAndLookTargetMemories(summon, BlockPos.containing(target), (float) speed, closeEnoughDist);
    }

    private static void setEntityWalkAndLookMemory(EntitySummonBase summon, LivingEntity target, double speed, int closeEnoughDist) {
        BehaviorUtils.setWalkAndLookTargetMemories(summon, target, (float) speed, closeEnoughDist);
    }

    private static void setEntityLookMemory(EntitySummonBase summon, LivingEntity target) {
        BehaviorUtils.lookAtEntity(summon, target);
    }

    private static void clearWalkAndLookMemory(EntitySummonBase summon) {
        Brain<?> brain = summon.getBrain();
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
        brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    private static void clearWalkMemory(EntitySummonBase summon) {
        Brain<?> brain = summon.getBrain();
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    private static boolean shouldFollowCarrier(EntitySummonBase summon) {
        EntityShipBase carrier = summon.getCarrier();
        return SummonBrainDecisionResolver.shouldFollowCarrier(decisionState(summon, carrier));
    }

    private static SummonBrainDecisionResolver.State decisionState(EntitySummonBase summon, EntityShipBase carrier) {
        LivingEntity attackTarget = summon.getTarget();
        return new SummonBrainDecisionResolver.State(
                carrier != null,
                carrier != null && carrier.isAlive(),
                carrier == null ? -1.0D : summon.distanceToSqr(carrier),
                attackTarget != null,
                attackTarget != null && attackTarget.isAlive(),
                summon.getRandom().nextInt(SummonAiNumbers.RANDOM_STROLL_CHANCE) == 0,
                attackTarget == null ? -1.0D : summon.distanceToSqr(attackTarget),
                summon.getAttackRangeSq(),
                0
        );
    }

    private static SummonBrainDecisionResolver.State decisionState(EntitySummonBase summon, EntityShipBase carrier, int attackDelay) {
        LivingEntity attackTarget = summon.getTarget();
        return new SummonBrainDecisionResolver.State(
                carrier != null,
                carrier != null && carrier.isAlive(),
                carrier == null ? -1.0D : summon.distanceToSqr(carrier),
                attackTarget != null,
                attackTarget != null && attackTarget.isAlive(),
                summon.getRandom().nextInt(SummonAiNumbers.RANDOM_STROLL_CHANCE) == 0,
                attackTarget == null ? -1.0D : summon.distanceToSqr(attackTarget),
                summon.getAttackRangeSq(),
                attackDelay
        );
    }

    private static final class SummonAttackBehavior extends Behavior<EntitySummonBase> {
        private int attackDelay;

        SummonAttackBehavior() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, EntitySummonBase summon) {
            return SummonBrainDecisionResolver.shouldAttack(decisionState(summon, summon.getCarrier()));
        }

        @Override
        protected void start(ServerLevel level, EntitySummonBase summon, long gameTime) {
            summon.attackMovementCoordinator().reset();
            LivingEntity target = summon.getTarget();
            if (target != null && target.isAlive()) {
                summon.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
            }
        }

        @Override
        protected void stop(ServerLevel level, EntitySummonBase summon, long gameTime) {
            clearWalkAndLookMemory(summon);
            summon.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            summon.attackMovementCoordinator().stop();
        }

        @Override
        protected void tick(ServerLevel level, EntitySummonBase summon, long gameTime) {
            LivingEntity target = summon.getTarget();
            if (target == null) {
                clearWalkAndLookMemory(summon);
                summon.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                summon.attackMovementCoordinator().stop();
                return;
            }

            summon.getLookControl().setLookAt(target, SummonAiNumbers.ATTACK_LOOK_YAW, SummonAiNumbers.ATTACK_LOOK_PITCH);
            summon.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
            setEntityLookMemory(summon, target);
            SummonBrainDecisionResolver.State state = decisionState(summon, summon.getCarrier(), this.attackDelay);
            if (SummonBrainDecisionResolver.shouldChaseAttackTarget(state)) {
                setEntityWalkAndLookMemory(summon, target, SummonAiNumbers.ATTACK_MOVE_SPEED, 1);
                summon.attackMovementCoordinator().moveTo(target, SummonAiNumbers.ATTACK_MOVE_SPEED);
            } else {
                clearWalkMemory(summon);
                summon.attackMovementCoordinator().stop();
                if (SummonBrainDecisionResolver.shouldPerformAttack(state)) {
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
            return shouldFollowCarrier(summon);
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
                clearWalkAndLookMemory(summon);
                summon.followMovementCoordinator().stop();
                return;
            }
            summon.getLookControl().setLookAt(carrier, SummonAiNumbers.ATTACK_LOOK_YAW, SummonAiNumbers.ATTACK_LOOK_PITCH);
            setEntityWalkAndLookMemory(summon, carrier, SummonAiNumbers.FOLLOW_CARRIER_SPEED, 1);
            setEntityLookMemory(summon, carrier);
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = SummonAiNumbers.FOLLOW_RECALC_TICKS;
                summon.followMovementCoordinator().moveTo(carrier, SummonAiNumbers.FOLLOW_CARRIER_SPEED);
            }
        }

        @Override
        protected void stop(ServerLevel level, EntitySummonBase summon, long gameTime) {
            clearWalkAndLookMemory(summon);
            summon.followMovementCoordinator().stop();
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
                setEntityLookMemory(summon, player);
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
            return SummonBrainDecisionResolver.shouldRandomStroll(decisionState(summon, summon.getCarrier()));
        }

        @Override
        protected void start(ServerLevel level, EntitySummonBase summon, long gameTime) {
            Vec3 target = net.minecraft.world.entity.ai.util.DefaultRandomPos.getPos(
                    summon, SummonAiNumbers.RANDOM_STROLL_HORIZONTAL_RANGE, SummonAiNumbers.RANDOM_STROLL_VERTICAL_RANGE);
            if (target != null) {
                setPointWalkAndLookMemory(summon, target, SummonAiNumbers.RANDOM_STROLL_SPEED, 1);
                summon.followMovementCoordinator().moveTo(target, SummonAiNumbers.RANDOM_STROLL_SPEED);
            }
        }
    }
}
