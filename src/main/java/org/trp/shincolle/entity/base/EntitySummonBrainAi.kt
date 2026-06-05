package org.trp.shincolle.entity.base

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.mojang.datafixers.util.Pair
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.Brain
import net.minecraft.world.entity.ai.behavior.Behavior
import net.minecraft.world.entity.ai.behavior.BehaviorUtils
import net.minecraft.world.entity.ai.behavior.PositionTracker
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.memory.MemoryStatus
import net.minecraft.world.entity.ai.memory.WalkTarget
import net.minecraft.world.entity.ai.sensing.Sensor
import net.minecraft.world.entity.ai.sensing.SensorType
import net.minecraft.world.entity.ai.util.DefaultRandomPos
import net.minecraft.world.entity.schedule.Activity
import net.minecraft.world.phys.Vec3
import java.util.Set

internal object EntitySummonBrainAi {
    val MEMORY_TYPES: MutableList<MemoryModuleType<*>?> = ImmutableList.of<MemoryModuleType<*>?>(
        MemoryModuleType.WALK_TARGET,
        MemoryModuleType.LOOK_TARGET,
        MemoryModuleType.ATTACK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE
    )
    val SENSOR_TYPES: MutableList<SensorType<out Sensor<in EntitySummonBase?>?>?> =
        ImmutableList.of<SensorType<out Sensor<in EntitySummonBase?>?>?>()

    fun makeBrain(summon: EntitySummonBase?, brain: Brain<EntitySummonBase?>): Brain<*> {
        brain.addActivity(
            Activity.CORE, ImmutableList.of<Pair<Int?, out Behavior<EntitySummonBase?>?>?>(
                Pair.of<Int?, SummonAttackBehavior?>(SummonAiNumbers.ATTACK_BEHAVIOR_PRIORITY, SummonAttackBehavior()),
                Pair.of<Int?, SummonFollowCarrierBehavior?>(
                    SummonAiNumbers.FOLLOW_BEHAVIOR_PRIORITY,
                    SummonFollowCarrierBehavior()
                ),
                Pair.of<Int?, SummonLookAtPlayerBehavior?>(
                    SummonAiNumbers.LOOK_AT_PLAYER_PRIORITY,
                    SummonLookAtPlayerBehavior()
                ),
                Pair.of<Int?, SummonRandomLookBehavior?>(
                    SummonAiNumbers.RANDOM_LOOK_PRIORITY,
                    SummonRandomLookBehavior()
                )
            )
        )
        brain.addActivity(
            Activity.IDLE, ImmutableList.of<Pair<Int?, SummonRandomStrollBehavior?>?>(
                Pair.of<Int?, SummonRandomStrollBehavior?>(
                    SummonAiNumbers.RANDOM_STROLL_PRIORITY,
                    SummonRandomStrollBehavior()
                )
            )
        )
        brain.setCoreActivities(Set.of<Activity?>(Activity.CORE))
        brain.setDefaultActivity(Activity.IDLE)
        brain.useDefaultActivity()
        return brain
    }

    fun tick(level: ServerLevel, summon: EntitySummonBase) {
        val brain = summon.getBrain() as Brain<EntitySummonBase?>
        syncAttackTargetMemory(summon, brain)
        brain.tick(level, summon)
        brain.setActiveActivityToFirstValid(ImmutableList.of<Activity?>(Activity.IDLE))
    }

    private fun syncAttackTargetMemory(summon: EntitySummonBase, brain: Brain<EntitySummonBase?>) {
        val target = summon.getTarget()
        if (target != null && target.isAlive) {
            brain.setMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET, target)
        } else {
            brain.eraseMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET)
        }
    }

    private fun setPointWalkAndLookMemory(summon: EntitySummonBase, target: Vec3, speed: Double, closeEnoughDist: Int) {
        BehaviorUtils.setWalkAndLookTargetMemories(
            summon,
            BlockPos.containing(target),
            speed.toFloat(),
            closeEnoughDist
        )
    }

    private fun setEntityWalkAndLookMemory(
        summon: EntitySummonBase,
        target: LivingEntity,
        speed: Double,
        closeEnoughDist: Int
    ) {
        BehaviorUtils.setWalkAndLookTargetMemories(summon, target, speed.toFloat(), closeEnoughDist)
    }

    private fun setEntityLookMemory(summon: EntitySummonBase, target: LivingEntity) {
        BehaviorUtils.lookAtEntity(summon, target)
    }

    private fun clearWalkAndLookMemory(summon: EntitySummonBase) {
        val brain = summon.getBrain()
        brain.eraseMemory<WalkTarget?>(MemoryModuleType.WALK_TARGET)
        brain.eraseMemory<PositionTracker?>(MemoryModuleType.LOOK_TARGET)
        brain.eraseMemory<Long?>(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
    }

    private fun clearWalkMemory(summon: EntitySummonBase) {
        val brain = summon.getBrain()
        brain.eraseMemory<WalkTarget?>(MemoryModuleType.WALK_TARGET)
        brain.eraseMemory<Long?>(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
    }

    private fun shouldFollowCarrier(summon: EntitySummonBase): Boolean {
        val carrier = summon.getCarrier()
        return SummonBrainDecisionResolver.shouldFollowCarrier(decisionState(summon, carrier))
    }

    private fun decisionState(summon: EntitySummonBase, carrier: EntityShipBase?): SummonBrainDecisionResolver.State {
        val attackTarget = summon.getTarget()
        return SummonBrainDecisionResolver.State(
            carrier != null,
            carrier != null && carrier.isAlive,
            if (carrier == null) -1.0 else summon.distanceToSqr(carrier),
            attackTarget != null,
            attackTarget != null && attackTarget.isAlive,
            summon.getRandom().nextInt(SummonAiNumbers.RANDOM_STROLL_CHANCE) == 0,
            if (attackTarget == null) -1.0 else summon.distanceToSqr(attackTarget),
            summon.getAttackRangeSq().toDouble(),
            0
        )
    }

    private fun decisionState(
        summon: EntitySummonBase,
        carrier: EntityShipBase?,
        attackDelay: Int
    ): SummonBrainDecisionResolver.State {
        val attackTarget = summon.getTarget()
        return SummonBrainDecisionResolver.State(
            carrier != null,
            carrier != null && carrier.isAlive,
            if (carrier == null) -1.0 else summon.distanceToSqr(carrier),
            attackTarget != null,
            attackTarget != null && attackTarget.isAlive,
            summon.getRandom().nextInt(SummonAiNumbers.RANDOM_STROLL_CHANCE) == 0,
            if (attackTarget == null) -1.0 else summon.distanceToSqr(attackTarget),
            summon.getAttackRangeSq().toDouble(),
            attackDelay
        )
    }

    private class SummonAttackBehavior :
        Behavior<EntitySummonBase?>(ImmutableMap.of<MemoryModuleType<*>?, MemoryStatus?>()) {
        private var attackDelay = 0

        override fun checkExtraStartConditions(level: ServerLevel, summon: EntitySummonBase): Boolean {
            return SummonBrainDecisionResolver.shouldAttack(decisionState(summon, summon.getCarrier()))
        }

        override fun start(level: ServerLevel, summon: EntitySummonBase, gameTime: Long) {
            summon.attackMovementCoordinator().reset()
            val target = summon.getTarget()
            if (target != null && target.isAlive) {
                summon.getBrain().setMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET, target)
            }
        }

        override fun stop(level: ServerLevel, summon: EntitySummonBase, gameTime: Long) {
            clearWalkAndLookMemory(summon)
            summon.getBrain().eraseMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET)
            summon.attackMovementCoordinator().stop()
        }

        override fun tick(level: ServerLevel, summon: EntitySummonBase, gameTime: Long) {
            val target = summon.getTarget()
            if (target == null) {
                clearWalkAndLookMemory(summon)
                summon.getBrain().eraseMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET)
                summon.attackMovementCoordinator().stop()
                return
            }

            summon.getLookControl()
                .setLookAt(target, SummonAiNumbers.ATTACK_LOOK_YAW, SummonAiNumbers.ATTACK_LOOK_PITCH)
            summon.getBrain().setMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET, target)
            setEntityLookMemory(summon, target)
            val state = decisionState(summon, summon.getCarrier(), this.attackDelay)
            if (SummonBrainDecisionResolver.shouldChaseAttackTarget(state)) {
                setEntityWalkAndLookMemory(summon, target, SummonAiNumbers.ATTACK_MOVE_SPEED, 1)
                summon.attackMovementCoordinator().moveTo(target, SummonAiNumbers.ATTACK_MOVE_SPEED)
            } else {
                clearWalkMemory(summon)
                summon.attackMovementCoordinator().stop()
                if (SummonBrainDecisionResolver.shouldPerformAttack(state)) {
                    summon.performAttack(target)
                    this.attackDelay = SummonAiNumbers.ATTACK_DELAY_TICKS
                }
            }

            if (this.attackDelay > 0) {
                this.attackDelay--
            }
        }
    }

    private class SummonFollowCarrierBehavior :
        Behavior<EntitySummonBase?>(ImmutableMap.of<MemoryModuleType<*>?, MemoryStatus?>()) {
        private var timeToRecalcPath = 0

        override fun checkExtraStartConditions(level: ServerLevel, summon: EntitySummonBase): Boolean {
            return shouldFollowCarrier(summon)
        }

        override fun start(level: ServerLevel, summon: EntitySummonBase, gameTime: Long) {
            summon.followMovementCoordinator().reset()
            this.timeToRecalcPath = 0
        }

        override fun tick(level: ServerLevel, summon: EntitySummonBase, gameTime: Long) {
            val carrier = summon.getCarrier()
            if (carrier == null) {
                clearWalkAndLookMemory(summon)
                summon.followMovementCoordinator().stop()
                return
            }
            summon.getLookControl()
                .setLookAt(carrier, SummonAiNumbers.ATTACK_LOOK_YAW, SummonAiNumbers.ATTACK_LOOK_PITCH)
            setEntityWalkAndLookMemory(summon, carrier, SummonAiNumbers.FOLLOW_CARRIER_SPEED, 1)
            setEntityLookMemory(summon, carrier)
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = SummonAiNumbers.FOLLOW_RECALC_TICKS
                summon.followMovementCoordinator().moveTo(carrier, SummonAiNumbers.FOLLOW_CARRIER_SPEED)
            }
        }

        override fun stop(level: ServerLevel, summon: EntitySummonBase, gameTime: Long) {
            clearWalkAndLookMemory(summon)
            summon.followMovementCoordinator().stop()
        }
    }

    private class SummonLookAtPlayerBehavior :
        Behavior<EntitySummonBase?>(ImmutableMap.of<MemoryModuleType<*>?, MemoryStatus?>()) {
        override fun checkExtraStartConditions(level: ServerLevel, summon: EntitySummonBase): Boolean {
            return summon.getTarget() == null
        }

        override fun tick(level: ServerLevel, summon: EntitySummonBase, gameTime: Long) {
            val player = level.getNearestPlayer(summon, SummonAiNumbers.LOOK_AT_PLAYER_DISTANCE.toDouble())
            if (player != null) {
                summon.getLookControl()
                    .setLookAt(player, SummonAiNumbers.ATTACK_LOOK_YAW, SummonAiNumbers.ATTACK_LOOK_PITCH)
                setEntityLookMemory(summon, player)
            }
        }
    }

    private class SummonRandomLookBehavior :
        Behavior<EntitySummonBase?>(ImmutableMap.of<MemoryModuleType<*>?, MemoryStatus?>()) {
        override fun checkExtraStartConditions(level: ServerLevel, summon: EntitySummonBase): Boolean {
            return summon.getTarget() == null && summon.getRandom().nextInt(SummonAiNumbers.RANDOM_LOOK_CHANCE) == 0
        }
    }

    private class SummonRandomStrollBehavior :
        Behavior<EntitySummonBase?>(ImmutableMap.of<MemoryModuleType<*>?, MemoryStatus?>()) {
        override fun checkExtraStartConditions(level: ServerLevel, summon: EntitySummonBase): Boolean {
            return SummonBrainDecisionResolver.shouldRandomStroll(decisionState(summon, summon.getCarrier()))
        }

        override fun start(level: ServerLevel, summon: EntitySummonBase, gameTime: Long) {
            val target = DefaultRandomPos.getPos(
                summon, SummonAiNumbers.RANDOM_STROLL_HORIZONTAL_RANGE, SummonAiNumbers.RANDOM_STROLL_VERTICAL_RANGE
            )
            if (target != null) {
                setPointWalkAndLookMemory(summon, target, SummonAiNumbers.RANDOM_STROLL_SPEED, 1)
                summon.followMovementCoordinator().moveTo(target, SummonAiNumbers.RANDOM_STROLL_SPEED)
            }
        }
    }
}
