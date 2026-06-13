package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.projectile.EntityProjectileBeam
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.init.ModParticles
import org.trp.shincolle.init.ModSounds
import java.util.List
import kotlin.math.max

class EntityBattleshipYamato(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    @Suppress("MagicNumber")
    override val baseModelScale: Float = 0.5f

    init {
        this.modelPos = floatArrayOf(0f, 25f, 0f, 40f)
        setStateMinor(STATE_MINOR_FACTION_ID, 6)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 46)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 3)
        setStateMinor(STATE_MINOR_RARITY, 4)
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
        if ((this.tickCount % 128) == 0) {
            applyBuffToNearbyAllies()
        }

        if (!this.level().isClientSide && this.getStateEmotion(EMOTION_ATTACK_PHASE) > 0) {
            if (!this.isStateGuiBtn2 || !this.isStateHeavyAttack || this.ammoHeavy <= 0) {
                this.setStateEmotion(EMOTION_ATTACK_PHASE, 0, true)
            }
        }
    }

    val passengersRidingOffset: Double
        get() {
            if (!this.isInSittingPose) {
                return (this.bbHeight * 0.75f).toDouble()
            }
            if (checkModelState(0, this.getStateEmotion(0))) {
                return (this.bbHeight * 0.5f).toDouble()
            }
            if (this.getStateEmotion(1) == 4) {
                return (this.bbHeight * 0.1f).toDouble()
            }
            return (this.bbHeight * 0.4f).toDouble()
        }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.addAll(
            List.of<EquipOption?>(
                EquipOption(EQUIP_BELT, "gui.shincolle.equip.belt"),
                EquipOption(EQUIP_HEAD_BASE, "gui.shincolle.equip.head_base"),
                EquipOption(EQUIP_UPPER, "gui.shincolle.equip.upper"),
                EquipOption(EQUIP_LEG, "gui.shincolle.equip.leg")
            )
        )
        return list
    }

    override fun performHeavyAttack(target: Entity?): Boolean {
        if (this.level() !is ServerLevel) {
            return false
        }
        if (target == null || !target.isAlive) {
            return false
        }
        if (isSameOwnerAttackTarget(target)) {
            return false
        }

        if (this.getStateEmotion(EMOTION_ATTACK_PHASE) > 0) {
            if (!consumeHeavyAmmo(1)) {
                return false
            }
            this.fuel -= Config.fuelConsumeActionHeavy
            val baseDamage = this.getAttributeValue(Attributes.ATTACK_DAMAGE).toFloat()
            val damage = max(6.0f, baseDamage * 1.6f)
            this.playSound(ModSounds.SHIP_YAMATO_SHOT.get(), max(0.0f, Config.volumeAttack), 1.0f)
            spawnBeamEntity((this.level() as ServerLevel), target, damage)
            this.setStateEmotion(EMOTION_ATTACK_PHASE, 0, true)
        } else {
            this.setStateEmotion(EMOTION_ATTACK_PHASE, 1, true)
            this.playSound(ModSounds.SHIP_YAMATO_READY.get(), max(0.0f, Config.volumeAttack), 1.0f)
            (this.level() as ServerLevel).sendParticles<SimpleParticleType?>(
                ModParticles.PARTICLE_CUBE.get(),
                this.x, this.y + this.bbHeight * 0.6, this.z,
                0, 1.5, this.id.toDouble(), 0.0, 1.0
            )
            for (i in 0..5) {
                (this.level() as ServerLevel).sendParticles<SimpleParticleType?>(
                    ModParticles.PARTICLE_LIGHTNING.get(),
                    this.x, this.y + 1.2, this.z,
                    0, 0.1, this.id.toDouble(), 3.0, 1.0
                )
            }
            this.tryFlareTarget(target)
            this.attackTick = 50
            this.applyEmotesReaction(3)
            return false
        }

        this.attackTick = 50
        this.applyEmotesReaction(3)
        return true
    }

    private fun updateClientParticles() {
        if (this.tickCount % 4 == 0 && checkModelState(0, this.getStateEmotion(0))
            && !this.isInSittingPose && !this.isStateNoEquip
        ) {
            val partPos = rotateXZByAxis(-0.63f, 0.0f, this.yBodyRot * Mth.DEG_TO_RAD, 1.0f)
            for (i in 0..2) {
                this.level().addParticle(
                    ParticleTypes.SMOKE,
                    this.x + partPos[1], this.y + 1.65 + i * 0.1, this.z + partPos[0],
                    0.0, 0.0, 0.0
                )
            }
        }

        if (this.tickCount % 16 == 0 && this.getStateEmotion(EMOTION_ATTACK_PHASE) > 0) {
            for (i in 0..3) {
                this.level().addParticle(
                    ModParticles.PARTICLE_LIGHTNING.get(),
                    this.x, this.y + 1.2, this.z,
                    0.1, this.id.toDouble(), 1.0
                )
            }
        }
    }

    private fun applyBuffToNearbyAllies() {
        if (!(this.isStateMarried && this.isStateRingEffect && this.getStateMinor(6) > 0)) {
            return
        }
        val ships = this.level().getEntitiesOfClass<EntityShipBase?>(
            EntityShipBase::class.java,
            this.boundingBox.inflate(16.0, 16.0, 16.0)
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
            if (ship.ownerUUID != this.ownerUUID) {
                continue
            }
            ship.addEffect(MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, amp, false, false))
            ship.addEffect(MobEffectInstance(MobEffects.FIRE_RESISTANCE, duration, amp, false, false))
        }
    }

    private fun spawnBeamEntity(serverLevel: ServerLevel, target: Entity, damage: Float) {
        val start = this.position().add(0.0, this.bbHeight * 0.7, 0.0)
        val end = target.position().add(0.0, target.bbHeight * 0.5, 0.0)
        var delta = end.subtract(start)
        val dist = delta.length()
        if (dist > 1.0E-4) {
            delta = delta.scale(1.0 / dist)
        }
        val beam = EntityProjectileBeam((this.level() as ServerLevel))
        beam.initAttrs(this, 0, delta.x.toFloat(), delta.y.toFloat(), delta.z.toFloat(), damage)
        (this.level() as ServerLevel).addFreshEntity(beam)
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.BATTLESHIP_YAMATO_SPAWN_EGG.get()

    companion object {
        const val EQUIP_BELT: String = "equip_belt"
        const val EQUIP_HEAD_BASE: String = "equip_head_base"
        const val EQUIP_UPPER: String = "equip_upper"
        const val EQUIP_LEG: String = "equip_leg"

        private const val EMOTION_ATTACK_PHASE = 5
    }
}
