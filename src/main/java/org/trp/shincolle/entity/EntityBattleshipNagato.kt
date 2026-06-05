package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
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

class EntityBattleshipNagato(type: EntityType<out TamableAnimal?>?, level: Level?) : EntityShipBase(type, level) {
    private val eventMovement: ShipMovementCoordinator
    private var loveEventMoveTarget: LivingEntity? = null
    private var loveEventMoveTicks = 0

    init {
        this.eventMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMMAND)
        this.modelPos = floatArrayOf(0f, 25f, 0f, 40f)
        setStateMinor(STATE_MINOR_FACTION_ID, 6)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 37)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 3)
        setStateMinor(STATE_MINOR_RARITY, 2)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeBB)
        this.isStateGuiBtn3 = false
        this.isStateGuiBtn4 = false
    }

    override fun aiStep() {
        super.aiStep()

        if (this.level().isClientSide) {
            updateClientParticles()
        }
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()
        tickLoveEventMovement()
        if ((this.tickCount % 128) == 0) {
            addMoraleSpecialEvent()
            if (this.isStateMarried && this.isStateRingEffect && this.getStateMinor(6) > 0) {
                applyBuffToNearbyAllies()
            }
        }

        if (!this.level().isClientSide && this.getStateEmotion(EMOTION_ATTACK_PHASE) > 0) {
            if (!this.isStateGuiBtn2() || !this.isStateHeavyAttack || this.ammoHeavy <= 0) {
                this.setStateEmotion(EMOTION_ATTACK_PHASE, 0, true)
            }
        }
    }

    val passengersRidingOffset: Double
        get() {
            if (this.isInSittingPose) {
                if (checkModelState(1, this.getStateEmotion(0))) {
                    return (this.getBbHeight() * 0.42f).toDouble()
                }
                if (this.getStateEmotion(1) == 4) {
                    return 0.0
                }
                return (this.getBbHeight() * 0.35f).toDouble()
            }
            return (this.getBbHeight() * 0.75f).toDouble()
        }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_HEAD, "gui.shincolle.equip.head"))
        list.add(EquipOption(EQUIP_CANNON, "gui.shincolle.equip.cannon"))
        return list
    }

    protected override fun performHeavyAttack(target: Entity?): Boolean {
        if (this.level() !is ServerLevel) {
            return false
        }
        if (target == null || !target.isAlive) {
            return false
        }
        if (isSameOwnerAttackTarget(target)) {
            return false
        }
        if (!consumeHeavyAmmo(1)) {
            return false
        }
        this.fuel = this.fuel - Config.fuelConsumeActionHeavy

        val phase = this.getStateEmotion(EMOTION_ATTACK_PHASE) + 1

        when (phase) {
            1 -> this.playSound(ModSounds.SHIP_AP_P2.get(), max(0.0f, Config.volumeAttack), 1.0f)
            3 -> this.playSound(ModSounds.SHIP_AP_ATTACK.get(), max(0.0f, Config.volumeAttack), 1.0f)
            else -> this.playSound(ModSounds.SHIP_AP_P1.get(), max(0.0f, Config.volumeAttack), 1.0f)
        }

        if (phase > 3) {
            this.setStateEmotion(EMOTION_ATTACK_PHASE, 0, true)
            performFinalAttack(serverLevel, target)
            this.tryFlareTarget(target)
            this.attackTick = 50
            this.applyEmotesReaction(3)
            return true
        } else {
            this.setStateEmotion(EMOTION_ATTACK_PHASE, phase, true)
            spawnAttackChargeParticles(serverLevel, phase)
            this.tryFlareTarget(target)
            this.attackTick = 50
            this.applyEmotesReaction(3)
            return false
        }
    }

    private fun updateClientParticles() {
        if (this.tickCount % 4 == 0 && !this.isInSittingPose && this.getEquipFlag(EQUIP_CANNON) && !this.isInDeadPose) {
            val partPos = rotateXZByAxis(-0.56f, 0.0f, this.yBodyRot * Mth.DEG_TO_RAD, 1.0f)
            for (i in 0..2) {
                this.level().addParticle(
                    ParticleTypes.SMOKE,
                    this.getX() + partPos[1], this.getY() + 1.5 + i * 0.1, this.getZ() + partPos[0],
                    0.0, 0.0, 0.0
                )
            }
        }

        if (this.tickCount % 8 == 0) {
            val atkPhase = this.getStateEmotion(EMOTION_ATTACK_PHASE)
            if (atkPhase == 1 || atkPhase == 3) {
                this.level().addParticle(
                    ModParticles.PARTICLE_CHI.get(),
                    this.getX(), this.getY(), this.getZ(),
                    0.12, this.getId().toDouble(), 1.0
                )
            }
        }
    }

    private fun applyBuffToNearbyAllies() {
        val ships = this.level().getEntitiesOfClass<EntityShipBase?>(
            EntityShipBase::class.java,
            this.getBoundingBox().inflate(16.0, 16.0, 16.0)
        )
        if (ships.isEmpty()) {
            return
        }
        val duration = 50 + this.getStateMinor(0)
        val amp = max(0, this.getStateMinor(0) / 70)
        for (ship in ships) {
            if (ship === this) {
                continue
            }
            if (ship.getOwnerUUID() != this.getOwnerUUID()) {
                continue
            }
            ship.addEffect(MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, amp, false, false))
        }
    }

    private fun addMoraleSpecialEvent() {
        if (this.isInDeadPose) {
            return
        }
        val nearby = this.level().getEntitiesOfClass<LivingEntity?>(
            LivingEntity::class.java,
            this.getBoundingBox().inflate(16.0, 12.0, 16.0),
            Predicate { entity: LivingEntity? ->
                entity is EntityNorthernHime
                        || (entity is EntityShipBase && entity.getStateMinor(STATE_MINOR_FACTION_ID) != -1)
            })
        if (nearby.isEmpty()) {
            return
        }
        if (this.getMorale() < 7650) {
            this.addMorale(150 * nearby.size)
        }
        if (!this.isInSittingPose && !this.isPassenger() && this.getRandom().nextFloat() > 0.5f) {
            val target = nearby.get(this.getRandom().nextInt(nearby.size))
            startLoveEventMovement(target)
            val particleId: Int = LOVE_PARTICLES[this.getRandom().nextInt(LOVE_PARTICLES.size)]
            this.applyParticleEmotion(particleId)
        }
    }

    private fun startLoveEventMovement(target: LivingEntity?) {
        if (target == null || !target.isAlive) {
            return
        }
        this.loveEventMoveTarget = target
        this.loveEventMoveTicks = 0
        this.eventMovement.reset()
        if (!this.eventMovement.moveTo(target, 1.0)) {
            stopLoveEventMovement()
        }
    }

    private fun tickLoveEventMovement() {
        val target = this.loveEventMoveTarget
        if (target == null) {
            return
        }
        this.loveEventMoveTicks++
        if (!canContinueLoveEventMovement(target)) {
            stopLoveEventMovement()
            return
        }
        if ((this.tickCount and 0xF) == 0 && !this.eventMovement.moveTo(target, 1.0)) {
            stopLoveEventMovement()
        }
    }

    private fun canContinueLoveEventMovement(target: LivingEntity): Boolean {
        return target.isAlive
                && !this.isInSittingPose && !this.isPassenger() && !this.isLeashed() && !this.isInDeadPose && this.loveEventMoveTicks <= LOVE_EVENT_MOVE_MAX_TICKS && this.distanceToSqr(
            target
        ) > LOVE_EVENT_MOVE_STOP_DISTANCE_SQ
    }

    private fun stopLoveEventMovement() {
        this.eventMovement.stop()
        this.loveEventMoveTarget = null
        this.loveEventMoveTicks = 0
    }

    private fun spawnAttackChargeParticles(serverLevel: ServerLevel, phase: Int) {
        if (phase == 2) {
            for (i in 0..19) {
                val newPos1 = rotateXZByAxis(0.35f, 0.0f, 0.314f * i, 1.0f)
                serverLevel.sendParticles<SimpleParticleType?>(
                    ModParticles.PARTICLE_SPRAY.get(),
                    this.getX(), this.getY() + 0.3, this.getZ(),
                    0, newPos1[0].toDouble(), 0.0, newPos1[1].toDouble(), 1.0
                )
            }
        } else {
            for (i in 0..19) {
                val newPos1 = rotateXZByAxis(2.0f, 0.0f, 0.314f * i, 1.0f)
                serverLevel.sendParticles<SimpleParticleType?>(
                    ModParticles.PARTICLE_SPRAY.get(),
                    this.getX() + newPos1[0], this.getY() + 1.0, this.getZ() + newPos1[1],
                    0, -newPos1[0] * 0.06, 0.0, -newPos1[1] * 0.06, 1.0
                )
            }
        }
    }

    private fun performFinalAttack(serverLevel: ServerLevel, target: Entity) {
        val delta = target.position().subtract(this.position())
        val dir = if (delta.lengthSqr() < 1.0E-6) Vec3.ZERO else delta.normalize()
        val newPos = target.position().add(dir.scale(2.0))

        val originX = this.getX()
        val originY = this.getY()
        val originZ = this.getZ()

        this.moveTo(newPos.x, newPos.y, newPos.z, this.getYRot(), this.getXRot())

        val baseDamage = this.getAttributeValue(Attributes.ATTACK_DAMAGE).toFloat()
        val damage = max(4.0f, baseDamage * 1.4f)
        target.hurt(this.damageSources().mobAttack(this), damage)

        val impact = this.getBoundingBox().inflate(3.5, 3.5, 3.5)
        for (hit in serverLevel.getEntities(this, impact)) {
            if (hit === this || hit === target || !hit.isAlive) {
                continue
            }
            hit.hurt(this.damageSources().mobAttack(this), damage * 0.5f)
        }

        val tx = target.getX()
        val ty = target.getY() + target.getBbHeight() * 0.5
        val tz = target.getZ()
        val dx = tx - originX
        val dy = ty - originY
        val dz = tz - originZ

        serverLevel.sendParticles<SimpleParticleType?>(
            ModParticles.PARTICLE_WAYPOINT_LINE_RED.get(),
            originX,
            originY,
            originZ,
            0,
            dx,
            dy,
            dz,
            1.0
        )
        serverLevel.sendParticles<SimpleParticleType?>(
            ModParticles.PARTICLE_WAYPOINT_LINE_RED.get(),
            originX,
            originY + 0.4,
            originZ,
            0,
            dx,
            dy,
            dz,
            1.0
        )
        serverLevel.sendParticles<SimpleParticleType?>(
            ModParticles.PARTICLE_WAYPOINT_LINE_RED.get(),
            originX,
            originY + 0.8,
            originZ,
            0,
            dx,
            dy,
            dz,
            1.0
        )

        for (i in 0..19) {
            val newPos1 = rotateXZByAxis(1.0f, 0.0f, 0.314f * i, 1.0f)
            serverLevel.sendParticles<SimpleParticleType?>(
                ModParticles.PARTICLE_SPRAY_RED.get(),
                tx, ty + 0.3, tz,
                0, newPos1[0] * 0.35, 0.0, newPos1[1] * 0.35, 1.0
            )
        }

        serverLevel.sendParticles<SimpleParticleType?>(
            ModParticles.PARTICLE_91TYPE.get(),
            tx, ty + 3.0, tz,
            1, 0.6, 0.0, 0.0, 0.0
        )

        serverLevel.sendParticles<SimpleParticleType?>(
            ParticleTypes.EXPLOSION, this.getX(), this.getY() + 1.0, this.getZ(),
            6, 0.2, 0.2, 0.2, 0.0
        )
    }

    override fun getShipSpawnEggItem(): Item {
        return ModItems.BATTLESHIP_NAGATO_SPAWN_EGG.get()
    }

    companion object {
        const val EQUIP_HEAD: String = "equip_head"
        const val EQUIP_CANNON: String = "equip_cannon"

        private const val EMOTION_ATTACK_PHASE = 5
        private const val LOVE_EVENT_MOVE_MAX_TICKS = 80
        private const val LOVE_EVENT_MOVE_STOP_DISTANCE_SQ = 9.0
        private val LOVE_PARTICLES = intArrayOf(31, 1, 7, 16, 29)
    }
}
