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
import net.minecraft.world.entity.ai.behavior.PositionTracker
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.memory.MemoryStatus
import net.minecraft.world.entity.ai.memory.WalkTarget
import net.minecraft.world.entity.ai.sensing.Sensor
import net.minecraft.world.entity.ai.sensing.SensorType
import net.minecraft.world.entity.ai.util.DefaultRandomPos
import net.minecraft.world.entity.schedule.Activity
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.Shincolle.Companion.debugLog
import java.util.*
import java.util.Set
import java.util.function.BooleanSupplier
import kotlin.math.max
import kotlin.math.pow

internal object EntityMountBrainAi {
    val MEMORY_TYPES: MutableList<MemoryModuleType<*>?> = ImmutableList.of<MemoryModuleType<*>?>(
        MemoryModuleType.WALK_TARGET,
        MemoryModuleType.LOOK_TARGET,
        MemoryModuleType.ATTACK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE
    )
    val SENSOR_TYPES: MutableList<SensorType<out Sensor<in EntityMountBase?>?>?> =
        ImmutableList.of<SensorType<out Sensor<in EntityMountBase?>?>?>()

    fun makeBrain(mount: EntityMountBase?, brain: Brain<EntityMountBase?>): Brain<*> {
        brain.addActivity(
            Activity.CORE, ImmutableList.of<Pair<Int?, out Behavior<EntityMountBase?>?>?>(
                Pair.of<Int?, MountFollowBehavior?>(MountAiNumbers.FOLLOW_BEHAVIOR_PRIORITY, MountFollowBehavior()),
                Pair.of<Int?, MountRangeAttackBehavior?>(
                    MountAiNumbers.RANGE_ATTACK_BEHAVIOR_PRIORITY,
                    MountRangeAttackBehavior()
                ),
                Pair.of<Int?, MountRandomStrollBehavior?>(
                    MountAiNumbers.RANDOM_STROLL_BEHAVIOR_PRIORITY,
                    MountRandomStrollBehavior()
                )
            )
        )
        brain.setCoreActivities(Set.of<Activity?>(Activity.CORE))
        brain.setDefaultActivity(Activity.CORE)
        brain.useDefaultActivity()
        return brain
    }

    fun tick(level: ServerLevel, mount: EntityMountBase) {
        val brain = mount.getBrain() as Brain<EntityMountBase?>
        syncAttackTargetMemory(mount, brain)
        brain.tick(level, mount)
        brain.setActiveActivityToFirstValid(ImmutableList.of<Activity?>(Activity.CORE))
    }

    private fun syncAttackTargetMemory(mount: EntityMountBase, brain: Brain<EntityMountBase?>) {
        val target = mount.getTarget()
        if (target != null && target.isAlive()) {
            brain.setMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET, target)
        } else {
            brain.eraseMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET)
        }
    }

    private fun setPointWalkAndLookMemory(mount: EntityMountBase, target: Vec3, speed: Double, closeEnoughDist: Int) {
        BehaviorUtils.setWalkAndLookTargetMemories(mount, BlockPos.containing(target), speed.toFloat(), closeEnoughDist)
    }

    private fun setEntityWalkAndLookMemory(
        mount: EntityMountBase,
        target: Entity,
        speed: Double,
        closeEnoughDist: Int
    ) {
        if (target is LivingEntity) {
            BehaviorUtils.setWalkAndLookTargetMemories(mount, target, speed.toFloat(), closeEnoughDist)
            return
        }
        setPointWalkAndLookMemory(mount, target.position(), speed, closeEnoughDist)
    }

    private fun setEntityLookMemory(mount: EntityMountBase, target: LivingEntity) {
        BehaviorUtils.lookAtEntity(mount, target)
    }

    private fun clearWalkAndLookMemory(mount: EntityMountBase) {
        val brain = mount.getBrain()
        brain.eraseMemory<WalkTarget?>(MemoryModuleType.WALK_TARGET)
        brain.eraseMemory<PositionTracker?>(MemoryModuleType.LOOK_TARGET)
        brain.eraseMemory<Long?>(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
    }

    private fun shouldFollowHostState(mount: EntityMountBase, host: EntityShipBase?): Boolean {
        return MountBrainDecisionResolver.shouldFollowHost(decisionState(mount, host))
    }

    private fun decisionState(
        mount: EntityMountBase, host: EntityShipBase?,
        aimTick: Int = 0, lightDelay: Int = 0, heavyDelay: Int = 0
    ): MountBrainDecisionResolver.State {
        val owner = if (host == null) null else host.getOwner()
        val attackTarget = mount.getTarget()
        val aimRequiredTicks = if (host == null) Int.MAX_VALUE else
            ((MountAiNumbers.AIM_SCALE_TICKS * (MountAiNumbers.LEVEL_CAP - host.getLevel()) / MountAiNumbers.LEVEL_CAP).toInt() + MountAiNumbers.AIM_BASE_TICKS)
        val attackRangeSq = if (host == null)
            0.0
        else max(MountAiNumbers.MIN_ATTACK_RANGE, host.getLegacyShipStats().getAttackRange().toDouble()).pow(2.0)
        return MountBrainDecisionResolver.State(
            host != null,
            host != null && host.isAlive(),
            host != null && host.isOrderedToSit(),
            mount.isPassenger(),
            host != null && host.hasPointerTarget(),
            host != null && hasGuardTarget(host),
            owner != null,
            if (owner == null) -1.0 else mount.distanceToSqr(owner),
            mount.getBbWidth().toDouble(),
            if (host == null) 0 else host.getStateMinor(11),
            attackTarget != null,
            attackTarget != null && attackTarget.isAlive(),
            mount.getRandom().nextInt(MountAiNumbers.RANDOM_STROLL_CHANCE) == 0,
            aimTick,
            aimRequiredTicks,
            if (attackTarget == null) -1.0 else mount.distanceToSqr(attackTarget),
            attackRangeSq,
            host != null && host.isStateLightAttack(),
            if (host == null) 0 else host.getAmmoLight(),
            lightDelay,
            host != null && host.isStateHeavyAttack(),
            if (host == null) 0 else host.getAmmoHeavy(),
            heavyDelay
        )
    }

    private fun hasGuardTarget(host: EntityShipBase): Boolean {
        val guardTarget = host.getGuardTarget()
        if (guardTarget.isEntity()) {
            val guarded = host.getGuardedEntity()
            return guarded != null && guarded.isAlive()
        }
        return guardTarget.isBlock() && guardTarget.isIn(host.level())
    }

    private class MountFollowBehavior :
        Behavior<EntityMountBase?>(ImmutableMap.of<MemoryModuleType<*>?, MemoryStatus?>()) {
        private val recovery = ShipMovementRecoveryState()
        private var lastRecoveryTargetKey: FollowRecoveryTargetKey? = null

        override fun checkExtraStartConditions(level: ServerLevel, mount: EntityMountBase): Boolean {
            return shouldFollowHostState(mount, mount.getHost())
        }

        override fun start(level: ServerLevel, mount: EntityMountBase, gameTime: Long) {
            this.lastRecoveryTargetKey = null
            this.recovery.reset(mount.position())
            mount.followMovementCoordinator().reset()
        }

        override fun stop(level: ServerLevel, mount: EntityMountBase, gameTime: Long) {
            this.lastRecoveryTargetKey = null
            this.recovery.clear()
            clearWalkAndLookMemory(mount)
            mount.followMovementCoordinator().stop()
        }

        override fun tick(level: ServerLevel, mount: EntityMountBase, gameTime: Long) {
            val h = mount.getHost()
            if (h == null) return

            if (h.hasPointerTarget()) {
                val pt = h.getPointerTarget()
                resetRecoveryIfTargetChanged(
                    mount, FollowRecoveryTargetKey.Companion.point(
                        "pointer", pt,
                        EntityShipBase.Companion.getLegacyDimensionId(h.level())
                    )
                )
                setPointWalkAndLookMemory(mount, pt, MountAiNumbers.FOLLOW_MOVE_SPEED, 1)
                mount.followMovementCoordinator().moveTo(pt, MountAiNumbers.FOLLOW_MOVE_SPEED)
                trackAndRecoverPoint(mount, pt, "pointer")
                return
            }

            if (tickGuardTarget(mount, h)) {
                return
            }

            val owner = h.getOwner()
            if (owner == null) return

            mount.getLookControl().setLookAt(owner, MountAiNumbers.LOOK_YAW, MountAiNumbers.LOOK_PITCH)
            setEntityWalkAndLookMemory(mount, owner, MountAiNumbers.FOLLOW_MOVE_SPEED, 1)
            setEntityLookMemory(mount, owner)
            resetRecoveryIfTargetChanged(mount, FollowRecoveryTargetKey.Companion.entity("owner", owner))
            mount.followMovementCoordinator().moveTo(owner, MountAiNumbers.FOLLOW_MOVE_SPEED)
            trackAndRecoverLiving(mount, owner, "owner")
        }

        fun tickGuardTarget(mount: EntityMountBase, host: EntityShipBase): Boolean {
            val guardTarget = host.getGuardTarget()
            if (guardTarget.isEntity()) {
                val guarded = host.getGuardedEntity()
                if (guarded == null || !guarded.isAlive()) {
                    return false
                }

                mount.getLookControl().setLookAt(guarded, MountAiNumbers.LOOK_YAW, MountAiNumbers.LOOK_PITCH)
                setEntityWalkAndLookMemory(mount, guarded, MountAiNumbers.FOLLOW_MOVE_SPEED, 1)
                resetRecoveryIfTargetChanged(mount, FollowRecoveryTargetKey.Companion.entity("guardEntity", guarded))
                mount.followMovementCoordinator().moveTo(guarded, MountAiNumbers.FOLLOW_MOVE_SPEED)
                trackAndRecoverEntity(mount, guarded, "guardEntity")
                return true
            }

            if (guardTarget.isBlock() && guardTarget.isIn(host.level())) {
                val guardPos = guardTarget.blockCenter()
                mount.getLookControl()
                    .setLookAt(guardPos.x, guardPos.y, guardPos.z, MountAiNumbers.LOOK_YAW, MountAiNumbers.LOOK_PITCH)
                setPointWalkAndLookMemory(mount, guardPos, MountAiNumbers.FOLLOW_MOVE_SPEED, 1)
                resetRecoveryIfTargetChanged(mount, FollowRecoveryTargetKey.Companion.guardBlock(guardTarget))
                mount.followMovementCoordinator().moveTo(guardPos, MountAiNumbers.FOLLOW_MOVE_SPEED)
                trackAndRecoverPoint(mount, guardPos, "guardBlock")
                return true
            }

            return false
        }

        fun trackAndRecoverLiving(mount: EntityMountBase, target: LivingEntity?, reason: String?) {
            if (target == null) return
            trackAndRecover(
                mount, target.position(), mount.distanceToSqr(target), reason,
                BooleanSupplier {
                    mount.followMovementCoordinator()
                        .teleportNearLiving(target, MountAiNumbers.TELEPORT_VERTICAL_OFFSET)
                })
        }

        fun trackAndRecoverEntity(mount: EntityMountBase, target: Entity?, reason: String?) {
            if (target == null) return
            if (target is LivingEntity) {
                trackAndRecoverLiving(mount, target, reason)
                return
            }
            trackAndRecover(
                mount, target.position(), mount.distanceToSqr(target), reason,
                BooleanSupplier {
                    mount.followMovementCoordinator()
                        .teleportNearPoint(target.position(), MountAiNumbers.TELEPORT_VERTICAL_OFFSET)
                })
        }

        fun trackAndRecoverPoint(mount: EntityMountBase, target: Vec3?, reason: String?) {
            if (target == null) return
            trackAndRecover(
                mount, target, mount.distanceToSqr(target), reason,
                BooleanSupplier {
                    mount.followMovementCoordinator().teleportNearPoint(target, MountAiNumbers.TELEPORT_VERTICAL_OFFSET)
                })
        }

        fun trackAndRecover(
            mount: EntityMountBase,
            target: Vec3?,
            distSq: Double,
            reason: String?,
            teleport: BooleanSupplier
        ) {
            this.recovery.trackProgress(mount.position())
            val force = this.recovery.isStuckLongerThan(MountAiNumbers.FOLLOW_STUCK_TICK_LIMIT)
            if (!this.recovery.shouldTryTeleportThrottled(
                    force, distSq,
                    MountAiNumbers.FOLLOW_TELEPORT_DISTANCE_SQ, MountAiNumbers.FOLLOW_TELEPORT_COOLDOWN_TICKS
                )
            ) {
                return
            }
            if (!teleport.getAsBoolean()) {
                return
            }

            debugLog(
                "[SCMoveDiag] MountFollow teleportRecovery mount={} host={} reason={} target={} force={} distSq={}",
                mount.getUUID(), mount.getHostUUID(), reason, target, force, distSq
            )
            this.recovery.reset(mount.position())
        }

        fun resetRecoveryIfTargetChanged(mount: EntityMountBase, targetKey: FollowRecoveryTargetKey?) {
            if (this.lastRecoveryTargetKey == targetKey) {
                return
            }
            this.lastRecoveryTargetKey = targetKey
            this.recovery.reset(mount.position())
            mount.followMovementCoordinator().reset()
        }
    }

    private class MountRangeAttackBehavior :
        Behavior<EntityMountBase?>(ImmutableMap.of<MemoryModuleType<*>?, MemoryStatus?>()) {
        private var target: LivingEntity? = null
        private var aimTick = 0
        private var lightDelay = 0
        private var heavyDelay = 0

        override fun checkExtraStartConditions(level: ServerLevel, mount: EntityMountBase): Boolean {
            val h = mount.getHost()
            this.target = mount.getTarget()
            return MountBrainDecisionResolver.shouldRangeAttack(decisionState(mount, h))
        }

        override fun start(level: ServerLevel, mount: EntityMountBase, gameTime: Long) {
            this.aimTick = 0
            this.lightDelay = 0
            this.heavyDelay = 0
            if (this.target != null && this.target!!.isAlive()) {
                mount.getBrain().setMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET, this.target)
            }
        }

        override fun stop(level: ServerLevel, mount: EntityMountBase, gameTime: Long) {
            this.target = null
            mount.getBrain().eraseMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET)
        }

        override fun tick(level: ServerLevel, mount: EntityMountBase, gameTime: Long) {
            if (this.target == null || !this.target!!.isAlive()) return
            val h = mount.getHost()
            if (h == null) return

            mount.getLookControl().setLookAt(this.target, MountAiNumbers.LOOK_YAW, MountAiNumbers.LOOK_PITCH)
            mount.getBrain().setMemory<LivingEntity?>(MemoryModuleType.ATTACK_TARGET, this.target)
            EntityMountBrainAi.setEntityLookMemory(mount, this.target!!)
            ++this.aimTick
            if (this.lightDelay > 0) --this.lightDelay
            if (this.heavyDelay > 0) --this.heavyDelay
            val state = decisionState(mount, h, this.aimTick, this.lightDelay, this.heavyDelay)
            if (MountBrainDecisionResolver.shouldFireLight(state)) {
                h.performLightAttack(this.target)
                this.lightDelay = h.getLegacyShipStats().getLightDelay()
            }
            if (MountBrainDecisionResolver.shouldFireHeavy(state)) {
                h.performHeavyAttack(this.target)
                this.heavyDelay = h.getLegacyShipStats().getHeavyDelay()
            }
        }
    }

    private class MountRandomStrollBehavior :
        Behavior<EntityMountBase?>(ImmutableMap.of<MemoryModuleType<*>?, MemoryStatus?>()) {
        override fun checkExtraStartConditions(level: ServerLevel, mount: EntityMountBase): Boolean {
            val h = mount.getHost()
            return MountBrainDecisionResolver.shouldRandomStroll(decisionState(mount, h))
        }

        override fun start(level: ServerLevel, mount: EntityMountBase, gameTime: Long) {
            val target = DefaultRandomPos.getPos(
                mount, MountAiNumbers.RANDOM_STROLL_HORIZONTAL_RANGE, MountAiNumbers.RANDOM_STROLL_VERTICAL_RANGE
            )
            if (target != null) {
                setPointWalkAndLookMemory(mount, target, MountAiNumbers.RANDOM_STROLL_SPEED, 1)
                mount.followMovementCoordinator().moveTo(target, MountAiNumbers.RANDOM_STROLL_SPEED)
            }
        }
    }

    @JvmRecord
    private data class FollowRecoveryTargetKey(
        val reason: String?,
        val entityId: UUID?,
        val x: Int,
        val y: Int,
        val z: Int,
        val dimensionId: Int
    ) {
        companion object {
            private fun entity(reason: String?, target: Entity): FollowRecoveryTargetKey {
                return FollowRecoveryTargetKey(
                    reason, target.getUUID(), 0, 0, 0,
                    EntityShipBase.Companion.getLegacyDimensionId(target.level())
                )
            }

            private fun guardBlock(target: ShipGuardTarget): FollowRecoveryTargetKey {
                return FollowRecoveryTargetKey("guardBlock", null, target.x, target.y, target.z, target.dimensionId)
            }

            private fun point(reason: String?, target: Vec3, dimensionId: Int): FollowRecoveryTargetKey {
                return FollowRecoveryTargetKey(
                    reason, null, Mth.floor(target.x), Mth.floor(target.y), Mth.floor(target.z),
                    dimensionId
                )
            }
        }
    }
}
