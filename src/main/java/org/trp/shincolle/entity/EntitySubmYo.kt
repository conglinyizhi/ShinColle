package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
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
import org.trp.shincolle.entity.projectile.EntityAbyssMissile
import org.trp.shincolle.init.ModItems
import kotlin.math.max

class EntitySubmYo(type: EntityType<out TamableAnimal?>?, level: Level?) : EntityShipBase(type, level) {
    init {
        this.modelPos = floatArrayOf(0f, 25f, 0f, 45f)
        setStateMinor(STATE_MINOR_FACTION_ID, 8)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 18)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 6)
        setStateMinor(STATE_MINOR_RARITY, 2)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeSS)
        this.isStateGuiBtn3 = false
        this.isStateGuiBtn4 = false
        this.isStateCanRide = true
    }

    override fun aiStep() {
        super.aiStep()

        if (this.level().isClientSide) {
            updateClientEffects()
        }
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()
        if ((this.tickCount % 128) == 0) {
            updateServerLogic()
        }
    }

    private fun updateClientEffects() {
        if ((this.tickCount % 4) == 0) {
            val shouldGlow = checkModelState(0, this.getStateEmotion(0))
                    && !this.isStateNoEquip && !(this.isInSittingPose && this.getStateEmotion(1) == 4)
            if (shouldGlow) {
                spawnEyeGlowParticles()
            }
        }
    }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_BASE, "gui.shincolle.equip.base"))
        list.add(EquipOption(EQUIP_NORMAL_BODY, "gui.shincolle.equip.normal_body"))
        return list
    }

    private fun spawnEyeGlowParticles() {
        val radYaw = (this.yBodyRot % 360.0f) * Mth.DEG_TO_RAD
        val zOffset = if (this.isInSittingPose) -0.1f else 0.15f
        val left = rotateXZByAxis(zOffset, 0.35f, radYaw, 1.0f)
        val right = rotateXZByAxis(zOffset, -0.35f, radYaw, 1.0f)
        val yOffset = if (this.isInSittingPose) 1.2 else 1.4
        this.level().addParticle(
            ParticleTypes.END_ROD,
            this.getX() + left[1], this.getY() + yOffset, this.getZ() + left[0],
            0.0, 0.02, 0.0
        )
        this.level().addParticle(
            ParticleTypes.END_ROD,
            this.getX() + right[1], this.getY() + yOffset, this.getZ() + right[0],
            0.0, 0.02, 0.0
        )
    }

    private fun updateServerLogic() {
        if (this.isStateRingEffect) {
            val duration = 40 + this.level
            this.addEffect(MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false))
            if (this.isStateMarried && this.ownerPlayer != null && this.distanceToSqr(this.ownerPlayer) < 256.0) {
                this.ownerPlayer.addEffect(MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false))
            }
        }
    }

    override fun performHeavyAttack(target: Entity?): Boolean {
        if (target == null || this.level().isClientSide) {
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
        spawnTorpedoes(target)
        return true
    }

    private fun spawnTorpedoes(target: Entity?) {
        if (this.level() !is ServerLevel) {
            return
        }

        val baseDamage = this.getAttributeValue(Attributes.ATTACK_DAMAGE).toFloat()
        val damage = max(3.0f, baseDamage * 0.35f)
        val speed = 0.65f
        val life = 160
        val explosionRadius = 3.0f
        val yawRad = this.getYRot() * Mth.DEG_TO_RAD

        for (offset in TORPEDO_OFFSETS) {
            val pos = rotateXZByAxis(offset[0], offset[1], yawRad, 1.0f)
            val missile = EntityAbyssMissile(serverLevel, this, target, damage, speed, life, explosionRadius)
            missile.setPos(this.getX() + pos[1], this.getY() + this.getBbHeight() * 0.6, this.getZ() + pos[0])
            serverLevel.addFreshEntity(missile)
        }
    }

    override fun supportsItemPickup(): Boolean {
        return true
    }

    override fun getShipSpawnEggItem(): Item {
        return ModItems.SUBM_YO_SPAWN_EGG.get()
    }

    override fun isSubmarine(): Boolean {
        return true
    }

    companion object {
        const val EQUIP_BASE: String = "equip_base"
        const val EQUIP_NORMAL_BODY: String = "equip_normal_body"

        private val TORPEDO_OFFSETS = arrayOf<FloatArray>(
            floatArrayOf(0.1f, 0.35f), floatArrayOf(0.1f, -0.35f)
        )
    }
}
