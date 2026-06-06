package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.projectile.EntityAbyssMissile
import org.trp.shincolle.entity.projectile.EntityAbyssMissile.MoveType
import org.trp.shincolle.init.ModItems
import kotlin.math.max

class EntityBattleshipRu(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    private var remainAttack = 0
    private var skillTarget: Vec3 = Vec3.ZERO

    init {
        this.modelPos = floatArrayOf(-6f, 25f, 0f, 40f)
        setStateMinor(STATE_MINOR_FACTION_ID, 6)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 13)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 2)
        setStateMinor(STATE_MINOR_RARITY, 2)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeBB)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
    }

    override fun aiStep() {
        super.aiStep()

        if (this.level().isClientSide) {
            updateClientEffects()
        }
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()
        updateServerEffects()
    }

    val passengersRidingOffset: Double
        get() {
            if (!this.isInSittingPose) {
                return (this.getBbHeight() * 0.72f).toDouble()
            }
            if (checkModelState(0, this.getStateEmotion(0))) {
                if (this.getStateEmotion(1) == 4) {
                    return (this.getBbHeight() * 0.51f).toDouble()
                }
                if (this.getStateEmotion(7) == 4) {
                    return 0.0
                }
                return (this.getBbHeight() * 0.55f).toDouble()
            }
            return (this.getBbHeight() * 0.45f).toDouble()
        }

    override fun performHeavyAttack(target: Entity?): Boolean {
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
        this.attackTick = 50
        this.applyEmotesReaction(3)

        val targetPos = target.position().add(0.0, target.getBbHeight() * 0.35, 0.0)
        this.skillTarget = targetPos
        if (this.getStateEmotion(EMOTION_SKILL_PHASE) == 0) {
            this.setStateEmotion(EMOTION_SKILL_PHASE, 1, true)
            this.remainAttack = 5 + (this.level * 0.035f).toInt()
        }
        return true
    }

    override fun getBreedOffspring(level: ServerLevel, otherParent: AgeableMob): AgeableMob? {
        return null
    }

    override fun isFood(stack: ItemStack): Boolean {
        return false
    }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_WEAPON, "gui.shincolle.equip.weapon"))
        list.add(EquipOption(EQUIP_BASE, "gui.shincolle.equip.base"))
        list.add(EquipOption(EQUIP_GLOVES, "gui.shincolle.equip.gloves"))
        list.add(EquipOption(EQUIP_EYE_EFFECT, "gui.shincolle.equip.eye_effect"))
        return list
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.putInt("RuRemainAttack", this.remainAttack)
        tag.putDouble("RuTargetX", this.skillTarget.x)
        tag.putDouble("RuTargetY", this.skillTarget.y)
        tag.putDouble("RuTargetZ", this.skillTarget.z)
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        this.remainAttack = max(0, tag.getInt("RuRemainAttack"))
        if (tag.contains("RuTargetX")) {
            this.skillTarget = Vec3(tag.getDouble("RuTargetX"), tag.getDouble("RuTargetY"), tag.getDouble("RuTargetZ"))
        }
    }

    private fun updateClientEffects() {
        if ((this.tickCount and 0xF) != 0) {
            return
        }
        if ((this.tickCount and 0x3F) == 0) {
            if (this.getStateEmotion(1) == 4 && checkModelState(0, this.getStateEmotion(0))
                && (this.tickCount and 0x1FF) > 400
            ) {
                this.level().addParticle(
                    ParticleTypes.HAPPY_VILLAGER,
                    this.getX(), this.getY() + 0.6, this.getZ(),
                    0.0, 0.0, 0.0
                )
            }
        }
    }

    private fun updateServerEffects() {
        if ((this.tickCount and 0x7F) == 0 && this.level().isDay() && this.isStateRingEffect) {
            this.addEffect(MobEffectInstance(MobEffects.LUCK, 150, max(0, this.getStateMinor(0) / 140), false, false))
        }
        if (this.getStateEmotion(EMOTION_SKILL_PHASE) > 0) {
            updateSkillEffect()
        }
    }

    private fun updateSkillEffect() {
        if (this.remainAttack > 0) {
            if ((this.tickCount and 3) == 0) {
                --this.remainAttack
                spawnSkillMissile()
            }
        } else {
            this.setStateEmotion(EMOTION_SKILL_PHASE, 0, true)
            this.remainAttack = 0
        }
    }

    private fun spawnSkillMissile() {
        if (this.level() !is ServerLevel) {
            return
        }
        val baseDamage = this.getAttributeValue(Attributes.ATTACK_DAMAGE).toFloat()
        val damage = max(2.0f, baseDamage * 0.2f)
        val speed = 0.7f
        val life = 200
        val explosionRadius = 3.5f

        val targetX = this.skillTarget.x + this.getRandom().nextFloat() * 8.0f - 4.0f
        val targetY = this.skillTarget.y + this.getRandom().nextFloat() * 4.0f - 2.0f
        val targetZ = this.skillTarget.z + this.getRandom().nextFloat() * 8.0f - 4.0f

        val origin = this.position().add(0.0, this.getBbHeight() * 0.4, 0.0)
        val target = Vec3(targetX, targetY, targetZ)
        val direction = target.subtract(origin)
        val velocity = if (direction.lengthSqr() < 1.0E-6) Vec3.ZERO else direction.normalize().scale(speed.toDouble())

        val missile = EntityAbyssMissile(
            serverLevel, this, null, damage,
            MoveType.PRESET_VELOCITY, speed, 0.25f, 0.25f, velocity, life, explosionRadius
        )
        missile.setPos(origin.x, origin.y, origin.z)
        serverLevel.addFreshEntity(missile)
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.BATTLESHIP_RU_SPAWN_EGG.get()

    companion object {
        const val EQUIP_WEAPON: String = "equip_weapon"
        const val EQUIP_BASE: String = "equip_base"
        const val EQUIP_GLOVES: String = "equip_gloves"
        const val EQUIP_EYE_EFFECT: String = "equip_eye_effect"

        private const val EMOTION_SKILL_PHASE = 5

        @JvmStatic
        fun createAttributes(): AttributeSupplier.Builder {
            return createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 220.0)
                .add(Attributes.MOVEMENT_SPEED, 0.24)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.STEP_HEIGHT, 1.0)
        }
    }
}
