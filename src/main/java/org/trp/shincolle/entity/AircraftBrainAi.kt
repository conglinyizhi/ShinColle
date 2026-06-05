package org.trp.shincolle.entity

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.mojang.datafixers.util.Pair
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
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
import net.minecraft.world.entity.schedule.Activity
import net.minecraft.world.phys.Vec3
import java.util.Set

internal object AircraftBrainAi {
    val MEMORY_TYPES: MutableList<MemoryModuleType<*>?> = ImmutableList.of<MemoryModuleType<*>?>(
        MemoryModuleType.WALK_TARGET,
        MemoryModuleType.LOOK_TARGET,
        MemoryModuleType.ATTACK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE
    )
    val SENSOR_TYPES: MutableList<SensorType<out Sensor<in EntityAircraftBase?>?>?> =
        ImmutableList.of<SensorType<out Sensor<in EntityAircraftBase?>?>?>()

    fun makeBrain(aircraft: EntityAircraftBase?, brain: Brain<EntityAircraftBase?>): Brain<*> {
        brain.addActivity(
            Activity.CORE, ImmutableList.of<Pair<Int?, AircraftAttackBehavior?>?>(
                Pair.of<Int?, AircraftAttackBehavior?>(
                    AircraftAiNumbers.ATTACK_BEHAVIOR_PRIORITY,
                    AircraftAttackBehavior()
                )
            )
        )
        brain.setCoreActivities(Set.of<Activity?>(Activity.CORE))
        brain.setDefaultActivity(Activity.CORE)
        brain.useDefaultActivity()
        return brain
    }

    fun tick(level: ServerLevel, aircraft: EntityAircraftBase) {
        val brain = brain(aircraft)
        syncAttackTargetMemory(aircraft)
        brain.tick(level, aircraft)
        brain.setActiveActivityToFirstValid(ImmutableList.of<Activity?>(Activity.CORE))
    }

    private fun brain(aircraft: EntityAircraftBase): Brain<EntityAircraftBase?> {
        return aircraft.getBrain() as Brain<EntityAircraftBase?>
    }

    private fun syncAttackTargetMemory(aircraft: EntityAircraftBase) {
        val brain = aircraft.getBrain()
        val target = aircraft.getMissionTarget()
        if (AircraftBrainDecisionResolver.canAttackMissionTarget(decisionState(aircraft, target))
            && target is LivingEntity
        ) {
            brain.setMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET, target)
        } else {
            brain.eraseMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET)
        }
    }

    private fun setPointWalkAndLookMemory(
        aircraft: EntityAircraftBase,
        target: Vec3,
        speed: Double,
        closeEnoughDist: Int
    ) {
        BehaviorUtils.setWalkAndLookTargetMemories(
            aircraft,
            BlockPos.containing(target),
            speed.toFloat(),
            closeEnoughDist
        )
    }

    private fun setEntityLookMemory(aircraft: EntityAircraftBase, target: Entity?) {
        if (target is LivingEntity) {
            BehaviorUtils.lookAtEntity(aircraft, target)
        }
    }

    private fun clearWalkAndLookMemory(aircraft: EntityAircraftBase) {
        val brain = aircraft.getBrain()
        brain.eraseMemory<WalkTarget?>(MemoryModuleType.WALK_TARGET)
        brain.eraseMemory<PositionTracker?>(MemoryModuleType.LOOK_TARGET)
        brain.eraseMemory<Long?>(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
    }

    private fun decisionState(host: EntityAircraftBase, targetEntity: Entity?): AircraftBrainDecisionResolver.State {
        return AircraftBrainDecisionResolver.State(
            targetEntity != null,
            targetEntity != null && targetEntity.isAlive(),
            host.isMissionLightAircraft(),
            host.hasAmmoLight(),
            host.hasAmmoHeavy(),
            host.getMissionTick(),
            host.getAttackDelay(),
            targetEntity != null && host.hasLineOfSight(targetEntity),
            if (targetEntity == null) -1.0 else host.distanceToSqr(
                targetEntity.getX(),
                targetEntity.getY() + AircraftAiNumbers.ATTACK_TARGET_Y_OFFSET, targetEntity.getZ()
            )
        )
    }

    private class AircraftAttackBehavior :
        Behavior<EntityAircraftBase?>(ImmutableMap.of<MemoryModuleType<*>?, MemoryStatus?>()) {
        private var target: Entity? = null
        private var randPos: Vec3? = null

        override fun checkExtraStartConditions(level: ServerLevel, host: EntityAircraftBase): Boolean {
            val targetEntity = host.getMissionTarget()
            if (AircraftBrainDecisionResolver.shouldStartAttack(decisionState(host, targetEntity))) {
                this.target = targetEntity
                return true
            }
            return false
        }

        override fun start(level: ServerLevel, host: EntityAircraftBase, gameTime: Long) {
            host.attackMovementCoordinator().reset()
            syncAttackTargetMemory(host)
            updateRandomPos(host)
        }

        override fun canStillUse(level: ServerLevel, host: EntityAircraftBase, gameTime: Long): Boolean {
            val targetEntity = host.getMissionTarget()
            if (!AircraftBrainDecisionResolver.canAttackMissionTarget(decisionState(host, targetEntity))) {
                return false
            }
            this.target = targetEntity
            return true
        }

        override fun stop(level: ServerLevel, host: EntityAircraftBase, gameTime: Long) {
            this.target = null
            this.randPos = null
            clearWalkAndLookMemory(host)
            host.getBrain().eraseMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET)
            host.attackMovementCoordinator().stop()
        }

        override fun tick(level: ServerLevel, host: EntityAircraftBase, gameTime: Long) {
            if (this.target == null) return

            val state = decisionState(host, this.target)
            syncAttackTargetMemory(host)
            setEntityLookMemory(host, this.target)

            if ((host.tickCount and AircraftAiNumbers.ATTACK_RECALC_INTERVAL_MASK) == 0 || this.randPos == null || host.attackMovementCoordinator()
                    .isNavigationDone()
            ) {
                updateRandomPos(host)
            }
            if (this.randPos == null) {
                clearWalkAndLookMemory(host)
                host.attackMovementCoordinator().stop()
                return
            }

            val speed = AircraftBrainDecisionResolver.attackMoveSpeed(state)
            AircraftBrainAi.setPointWalkAndLookMemory(host, this.randPos!!, speed, 1)
            host.attackMovementCoordinator().moveTo(this.randPos, speed)

            if (AircraftBrainDecisionResolver.shouldFire(state)) {
                if (host.isMissionLightAircraft() && host.hasAmmoLight()) {
                    host.attackWithLightAmmo(this.target)
                } else if (!host.isMissionLightAircraft() && host.hasAmmoHeavy()) {
                    host.attackWithHeavyAmmo(this.target)
                }
            }
        }

        fun updateRandomPos(host: EntityAircraftBase) {
            val ref = if (this.target != null) this.target else host
            this.randPos = host.getRandomCruisePos(ref)
        }
    }
}
