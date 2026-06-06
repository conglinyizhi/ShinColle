package org.trp.shincolle.entity

import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
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

class EntityIsolatedHime(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    init {
        this.modelPos = floatArrayOf(-6f, 30f, 0f, 40f)
        setStateMinor(STATE_MINOR_FACTION_ID, 10)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 29)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 2)
        setStateMinor(STATE_MINOR_RARITY, 8)
        this.isStateCanRide = true
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
        spawnMissile(target)
        return true
    }

    private fun spawnMissile(target: Entity?) {
        if (this.level() !is ServerLevel) {
            return
        }

        val baseDamage = this.getAttributeValue(Attributes.ATTACK_DAMAGE).toFloat()
        val damage = max(5.0f, baseDamage * 0.45f)
        val speed = 0.75f
        val life = 200
        val explosionRadius = 4.0f
        val yawRad = this.getYRot() * Mth.DEG_TO_RAD
        val pos = rotateXZByAxis(0.1f, 0.0f, yawRad, 1.0f)
        val missile = EntityAbyssMissile(serverLevel, this, target, damage, speed, life, explosionRadius)
        missile.setPos(this.getX() + pos[1], this.getY() + this.getBbHeight() * 0.65, this.getZ() + pos[0])
        serverLevel.addFreshEntity(missile)
    }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_HAT_BASE, "gui.shincolle.equip.head_base"))
        list.add(EquipOption(EQUIP_HEAD_GEAR, "gui.shincolle.equip.head"))
        list.add(EquipOption(EQUIP_CLOTH_1, "gui.shincolle.equip.upper"))
        list.add(EquipOption(EQUIP_CLOTH_2, "gui.shincolle.equip.cloak"))
        list.add(EquipOption(EQUIP_CLOTH_3, "gui.shincolle.equip.upper"))
        list.add(EquipOption(EQUIP_LEG_OUTER, "gui.shincolle.equip.leg"))
        list.add(EquipOption(EQUIP_LEG_ARMOR, "gui.shincolle.equip.leg"))
        list.add(EquipOption(EQUIP_ROAD, "gui.shincolle.equip.rigging"))
        return list
    }

    override fun getShipSpawnEggItem(): Item {
        return ModItems.ISOLATED_HIME_SPAWN_EGG.get()
    }

    override fun hasShipMounts(): Boolean {
        return true
    }

    override fun summonMountEntity(): EntityMountBase {
        return EntityMountIsH(ModEntities.MOUNT_IS_H.get(), this.level())
    }

    companion object {
        const val EQUIP_HAT_BASE: String = "equip_hat_base"
        const val EQUIP_HEAD_GEAR: String = "equip_head_gear"
        const val EQUIP_CLOTH_1: String = "equip_cloth_1"
        const val EQUIP_CLOTH_2: String = "equip_cloth_2"
        const val EQUIP_CLOTH_3: String = "equip_cloth_3"
        const val EQUIP_LEG_OUTER: String = "equip_leg_outer"
        const val EQUIP_LEG_ARMOR: String = "equip_leg_armor"
        const val EQUIP_ROAD: String = "equip_road"
    }
}
