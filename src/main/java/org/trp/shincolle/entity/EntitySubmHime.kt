package org.trp.shincolle.entity

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
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.projectile.EntityAbyssMissile
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.init.ModItems
import kotlin.math.max

class EntitySubmHime(type: EntityType<out TamableAnimal?>?, level: Level?) : EntityShipBase(type, level) {
    init {
        this.modelPos = floatArrayOf(0f, 25f, 0f, 45f)
        setStateMinor(STATE_MINOR_FACTION_ID, 10)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 44)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 6)
        setStateMinor(STATE_MINOR_RARITY, 3)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeSS)
        this.isStateGuiBtn3 = false
        this.isStateGuiBtn4 = false
        this.isStateCanRide = true
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()

        if ((this.tickCount % 128) == 0) {
            updateServerLogic()
        }
    }

    private fun updateServerLogic() {
        if (this.isStateRingEffect) {
            val duration = 80 + this.level
            this.addEffect(MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false))
            if (this.isStateMarried && this.ownerPlayer != null && this.distanceToSqr(this.ownerPlayer) < 256.0) {
                this.ownerPlayer.addEffect(MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false))
            }
        }
    }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_COLLAR, "gui.shincolle.equip.collar"))
        list.add(EquipOption(EQUIP_TAILS, "gui.shincolle.equip.tails"))
        return list
    }

    protected override fun performHeavyAttack(target: Entity?): Boolean {
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
        val damage = max(4.0f, baseDamage * 0.4f)
        val speed = 0.65f
        val life = 180
        val explosionRadius = 3.5f
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
        return ModItems.SUBM_HIME_SPAWN_EGG.get()
    }

    override fun hasShipMounts(): Boolean {
        return true
    }

    override fun summonMountEntity(): EntityMountBase {
        return EntityMountSuH(ModEntities.MOUNT_SU_H.get(), this.level())
    }

    override fun isSubmarine(): Boolean {
        return true
    }

    companion object {
        const val EQUIP_COLLAR: String = "equip_collar"
        const val EQUIP_TAILS: String = "equip_tails"

        private val TORPEDO_OFFSETS = arrayOf<FloatArray>(
            floatArrayOf(0.15f, 0.45f), floatArrayOf(0.15f, -0.45f)
        )
    }
}
