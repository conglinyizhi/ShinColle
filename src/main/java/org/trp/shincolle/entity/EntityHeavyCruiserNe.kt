package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.base.ShipMovementCoordinator
import org.trp.shincolle.init.ModItems
import java.util.function.Predicate
import kotlin.math.max

class EntityHeavyCruiserNe(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    private var isPushing = false
    private var tickPush = 0
    private var targetPush: LivingEntity? = null
    private val pushMovement: ShipMovementCoordinator

    init {
        this.pushMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMBAT)
        this.modelPos = floatArrayOf(0f, 10f, 0f, 40f)
        setStateMinor(STATE_MINOR_FACTION_ID, 2)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 10)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 4)
        setStateMinor(STATE_MINOR_RARITY, 0)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeCA)
        this.isStateGuiBtn3 = false
        this.isStateGuiBtn4 = false
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()

        if ((this.tickCount % 128) == 0) {
            updateServerLogic()
        }
        if (this.isPushing) {
            updatePushingState()
        }
    }

    val passengersRidingOffset: Double
        get() {
            if (this.isInSittingPose) {
                return (if (this.getStateEmotion(1) == 4) this.getBbHeight() * 0.05f else this.getBbHeight() * 0.55f).toDouble()
            }
            return (this.getBbHeight() * 0.7f).toDouble()
        }

    private fun updateServerLogic() {
        if (!this.level().isDay() && this.isStateRingEffect) {
            val duration = 150
            val ampSpeed = max(0, this.getStateMinor(0) / 50)
            this.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, ampSpeed, false, false))
        }

        val canFindTarget = (this.tickCount and 0xFF) == 0 && this.getRandom().nextInt(5) == 0
        val isActionBlocked =
            this.isInSittingPose || this.isPassenger() || this.isStateNoEquip || this.isLeashed() || this.isInDeadPose
        if (canFindTarget && !isActionBlocked && !this.isPushing) {
            findTargetPush()
        }
    }

    private fun updatePushingState() {
        this.tickPush++
        if (this.tickPush > PUSH_MAX_TICKS || this.targetPush == null || !this.targetPush!!.isAlive || this.isInDeadPose) {
            cancelPush()
            return
        }
        if (this.distanceTo(this.targetPush) <= PUSH_ENGAGE_DISTANCE) {
            executePushAttack()
        } else if (this.tickCount % 32 == 0 && !this.pushMovement.moveTo(this.targetPush, 1.0)) {
            cancelPush()
        }
    }

    private fun executePushAttack() {
        val yawRad = this.getYRot() * Mth.DEG_TO_RAD
        val push = Vec3((-Mth.sin(yawRad) * 0.5f).toDouble(), 0.5, (Mth.cos(yawRad) * 0.5f).toDouble())
        this.targetPush!!.setDeltaMovement(this.targetPush!!.getDeltaMovement().add(push))
        this.swing(InteractionHand.MAIN_HAND)
        if (this.level() is ServerLevel) {
            val serverLevel = this.level() as ServerLevel
            serverLevel.sendParticles<SimpleParticleType?>(
                ParticleTypes.CLOUD,
                this.targetPush!!.getX(), this.targetPush!!.getY() + 1.0, this.targetPush!!.getZ(),
                6, 0.2, 0.2, 0.2, 0.02
            )
        }
        cancelPush()
    }

    private fun cancelPush() {
        this.pushMovement.stop()
        this.isPushing = false
        this.tickPush = 0
        this.targetPush = null
    }

    private fun findTargetPush() {
        val impactBox = this.getBoundingBox().inflate(12.0, 6.0, 12.0)
        val list = this.level().getEntitiesOfClass<LivingEntity?>(
            LivingEntity::class.java, impactBox,
            Predicate { ent: LivingEntity? -> ent !== this && ent!!.isAlive && ent.canBeCollidedWith() })
        if (!list.isEmpty()) {
            this.pushMovement.reset()
            this.targetPush = list.get(this.getRandom().nextInt(list.size))
            this.tickPush = 0
            this.isPushing = true
        }
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.HEAVY_CRUISER_NE_SPAWN_EGG.get()

    companion object {
        private const val PUSH_MAX_TICKS = 200
        private const val PUSH_ENGAGE_DISTANCE = 2.5f
    }
}
