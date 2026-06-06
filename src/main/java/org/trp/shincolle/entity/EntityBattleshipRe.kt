package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.base.ShipMovementCoordinator
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.init.ModParticles
import org.trp.shincolle.init.ModSounds
import java.util.function.Predicate
import kotlin.math.max

class EntityBattleshipRe(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    private var isPushing = false
    private var tickPush = 0
    private var targetPush: LivingEntity? = null
    private val pushMovement: ShipMovementCoordinator

    init {
        this.pushMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMBAT)
        this.modelPos = floatArrayOf(-6f, 25f, 0f, 40f)
        setStateMinor(STATE_MINOR_FACTION_ID, 6)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 15)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 2)
        setStateMinor(STATE_MINOR_RARITY, 3)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeBB)
    }


    override fun tickAliveLogic() {
        super.tickAliveLogic()

        if ((this.tickCount and 0x7F) == 0) {
            updateServerLogic()
        }
        if (this.isPushing) {
            updatePushingState()
        }
    }

    val passengersRidingOffset: Double
        get() {
            if (this.isInSittingPose) {
                return (if (this.getStateEmotion(1) == 4) this.getBbHeight() * 0.35f else 0.0f).toDouble()
            }
            return (this.getBbHeight() * 0.55f).toDouble()
        }

    override fun performLightAttack(target: Entity?) {
        if (this.level() !is ServerLevel) {
            return
        }
        if (target == null || !target.isAlive) {
            return
        }
        if (isSameOwnerAttackTarget(target)) {
            return
        }
        if (!consumeLightAmmo(1)) {
            return
        }
        this.fuel = this.fuel - Config.fuelConsumeActionLight

        var damage = this.getAttributeValue(Attributes.ATTACK_DAMAGE).toFloat()
        if (damage <= 0.0f) {
            damage = 2.0f
        }
        val hurt = target.hurt(this.damageSources().mobAttack(this), damage)

        this.spawnLightAttackMuzzleParticles(serverLevel, target)
        serverLevel.sendParticles<SimpleParticleType?>(
            ModParticles.PARTICLE_LIGHTNING.get(),
            this.getX(), this.getY() + 1.5, this.getZ(),
            1, 0.1, this.getId().toDouble(), 0.0, 0.0
        )
        serverLevel.sendParticles<SimpleParticleType?>(
            ParticleTypes.ELECTRIC_SPARK,
            target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
            4, 0.2, 0.2, 0.2, 0.0
        )

        this.playSound(
            ModSounds.SHIP_FIRELIGHT.get(), max(0.0f, Config.volumeAttack),
            this.getRandom().nextFloat() * 0.12f + 0.98f
        )

        this.attackTick = 50
        this.applyEmotesReaction(3)

        if (hurt) {
            applyChainedLightningAttack(target, damage)
        }
    }

    override fun setFaceNormal() {
        this.faceId = FACE_EYES_OPEN
        val tick = this.tickCount and EMOTION_TICK_MASK_8BIT
        if (this.getStateEmotion(7) == 4 && tick > 200) {
            this.mouthId = mapLegacyMouth(0)
        } else {
            this.mouthId = mapLegacyMouth(3)
        }
    }

    override fun setFaceCry() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 128) {
            this.faceId = FACE_DOT_EYES_TEAR
            this.mouthId = mapLegacyMouth(if (tick < 64) 2 else 5)
        } else {
            this.faceId = FACE_CRY
            this.mouthId = mapLegacyMouth(if (tick < 190) 2 else 5)
        }
    }

    override fun setFaceDamaged() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_9BIT)
        if (tick < 200) {
            this.faceId = FACE_DOT_EYES_TEAR
            this.mouthId = mapLegacyMouth(if (tick < 60) 4 else 5)
        } else if (tick < 400) {
            this.faceId = FACE_TENSION
            this.mouthId = mapLegacyMouth(if (tick < 250) 4 else 5)
        } else {
            this.faceId = FACE_SOFT
            this.mouthId = mapLegacyMouth(if (tick < 450) 4 else 5)
        }
    }

    override fun setFaceScorn() {
        this.faceId = FACE_EYES_HALF
        this.mouthId = mapLegacyMouth(1)
    }

    override fun setFaceHungry() {
        this.faceId = FACE_DESPAIR
        this.mouthId = mapLegacyMouth(5)
    }

    override fun setFaceAngry() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 128) {
            this.faceId = FACE_EYES_CLOSED
            this.mouthId = mapLegacyMouth(if (tick < 64) 3 else 4)
        } else {
            this.faceId = FACE_EYES_HALF
            this.mouthId = mapLegacyMouth(if (tick < 170) 1 else 3)
        }
    }

    override fun setFaceBored() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_9BIT)
        if (tick < 170) {
            this.faceId = FACE_EYES_CLOSED
            this.mouthId = mapLegacyMouth(if (tick < 80) 0 else 4)
        } else if (tick < 340) {
            this.faceId = FACE_WINK
            this.mouthId = mapLegacyMouth(if (tick < 250) 0 else 4)
        } else {
            this.faceId = FACE_EYES_OPEN
            this.mouthId = mapLegacyMouth(if (tick < 420) 3 else 4)
        }
    }

    override fun setFaceShy() {
        this.faceId = FACE_EYES_OPEN
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        this.mouthId = mapLegacyMouth(if (tick < 150) 2 else 4)
    }

    override fun setFaceHappy() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 140) {
            this.faceId = FACE_TENSION
            this.mouthId = mapLegacyMouth(if (tick < 80) 4 else 5)
        } else {
            this.faceId = FACE_WINK
            this.mouthId = mapLegacyMouth(4)
        }
    }

    private fun updateServerLogic() {
        if (this.isStateMarried && this.isStateRingEffect && this.getStateMinor(6) > 0) {
            val owner = this.getOwner()
            if (owner != null && this.distanceToSqr(owner) < 256.0) {
                val duration = 50 + this.getStateMinor(0)
                val amp = max(0, this.getStateMinor(0) / 50)
                owner.addEffect(MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, amp, false, false))
            }
        }

        val canFindTarget = (this.tickCount and 0xFF) == 0 && this.getRandom().nextInt(5) != 0
        val isActionBlocked =
            this.isInSittingPose || this.isPassenger() || this.isStateNoEquip || this.isLeashed() || this.isInDeadPose
        if (canFindTarget && !isActionBlocked && !this.isPushing) {
            findTargetPush()
        }
    }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_HAIR, "gui.shincolle.equip.hair"))
        list.add(EquipOption(EQUIP_BAG, "gui.shincolle.equip.bag"))
        list.add(EquipOption(EQUIP_EARS, "gui.shincolle.equip.ears"))
        return list
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

        this.targetPush!!.hasImpulse = true
        this.targetPush!!.hurtMarked = true

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
            Predicate { ent: LivingEntity? -> ent !== this && ent!!.isAlive && ent.isPushable() })
        if (!list.isEmpty()) {
            this.pushMovement.reset()
            this.targetPush = list.get(this.getRandom().nextInt(list.size))
            this.tickPush = 0
            this.isPushing = true
        }
    }

    private fun applyChainedLightningAttack(primaryTarget: Entity, baseAttack: Float) {
        if (this.level() !is ServerLevel) {
            return
        }
        val maxTargets = max(1, (this.level * 0.05f).toInt())
        val damage = baseAttack * 0.2f
        val impactBox = primaryTarget.getBoundingBox().inflate(3.5, 3.5, 3.5)
        val potentialTargets: MutableList<Entity> = serverLevel.getEntities(this, impactBox)
        var hits = 0
        for (entity in potentialTargets) {
            if (hits >= maxTargets) {
                break
            }
            if (entity === this || entity === primaryTarget || !entity.isAlive || !entity.canBeCollidedWith()) {
                continue
            }
            if (entity is EntityShipBase
                && entity.getOwnerUUID() == this.getOwnerUUID()
            ) {
                continue
            }
            entity.hurt(this.damageSources().mobAttack(this), damage)
            serverLevel.sendParticles<SimpleParticleType?>(
                ParticleTypes.ELECTRIC_SPARK,
                entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ(),
                4, 0.2, 0.2, 0.2, 0.0
            )
            hits++
        }
    }


    override val shipSpawnEggItem: Item?
        get() = ModItems.BATTLESHIP_RE_SPAWN_EGG.get()

    companion object {
        const val EQUIP_HAIR: String = "equip_hair"
        const val EQUIP_BAG: String = "equip_bag"
        const val EQUIP_EARS: String = "equip_ears"

        private const val PUSH_MAX_TICKS = 200
        private const val PUSH_ENGAGE_DISTANCE = 2.5f
    }
}
