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
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.projectile.EntityAbyssMissile
import org.trp.shincolle.init.ModItems
import kotlin.math.max

class EntitySubmU511(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    @Suppress("MagicNumber")
    override val baseModelScale: Float = 0.34f

    init {
        this.modelPos = floatArrayOf(0f, 20f, 0f, 45f)
        setStateMinor(STATE_MINOR_FACTION_ID, 8)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 38)
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
            val duration = 40 + this.level
            this.addEffect(MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false))
            val owner = this.ownerPlayer

            if (this.isStateMarried && owner != null && this.distanceToSqr(owner) < 256.0) {
                owner.addEffect(MobEffectInstance(MobEffects.INVISIBILITY, Config.SHIP_BUFF_DURATION.get(), 0, false, false))
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

        this.fuel -= Config.fuelConsumeActionHeavy
        this.attackTick = 50
        this.applyEmotesReaction(3)
        spawnTorpedoes(target)
        return true
    }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_BASE, "gui.shincolle.equip.base"))
        list.add(EquipOption(EQUIP_HAT, "gui.shincolle.equip.hat"))
        list.add(EquipOption(EQUIP_PIPE, "gui.shincolle.equip.pipe"))
        return list
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
        val yawRad = this.yRot * Mth.DEG_TO_RAD

        for (offset in TORPEDO_OFFSETS) {
            val pos = rotateXZByAxis(offset[0], offset[1], yawRad, 1.0f)
            val missile = EntityAbyssMissile((this.level() as ServerLevel), this, target, damage, speed, life, explosionRadius)
            missile.setPos(this.x + pos[1], this.y + this.bbHeight * 0.6, this.z + pos[0])
            (this.level() as ServerLevel).addFreshEntity(missile)
        }
    }

    override fun supportsItemPickup(): Boolean {
        return true
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.SUBM_U511_SPAWN_EGG.get()

    override val isSubmarine: Boolean
        get() = true

    companion object {
        const val EQUIP_BASE: String = "equip_base"
        const val EQUIP_HAT: String = "equip_hat"
        const val EQUIP_PIPE: String = "equip_pipe"

        private val TORPEDO_OFFSETS = arrayOf<FloatArray>(
            floatArrayOf(0.1f, 0.0f)
        )
    }
}
