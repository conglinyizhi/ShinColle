package org.trp.shincolle.entity

import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.init.ModItems
import kotlin.math.max

class EntityCarrierHime(type: EntityType<out TamableAnimal?>?, level: Level?) : EntityShipBase(type, level) {
    init {
        setModelPos(floatArrayOf(-6f, 30f, 0f, 40f))
        setStateMinor(STATE_MINOR_FACTION_ID, 10)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 20)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 1)
        setStateMinor(STATE_MINOR_RARITY, 3)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeCV)
        setStateGuiBtn1(false)
        setStateGuiBtn2(false)
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()

        if ((this.tickCount % 128) == 0) {
            updateServerLogic()
        }
    }

    private fun updateServerLogic() {
        if (!(this.isStateMarried() && this.isStateRingEffect() && this.getStateMinor(6) > 0)) {
            return
        }

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
            ship.addEffect(MobEffectInstance(MobEffects.JUMP, duration, amp, false, false))
        }
    }

    override fun getEquipOptions(): MutableList<EquipOption?> {
        val list: MutableList<EquipOption?> = ArrayList<EquipOption?>(super.getEquipOptions())
        list.add(EquipOption(EQUIP_LEFT, "gui.shincolle.equip.left"))
        list.add(EquipOption(EQUIP_RIGHT, "gui.shincolle.equip.right"))
        return list
    }

    override fun supportsAircraftCombat(): Boolean {
        return true
    }

    override fun getAttackAircraftType(isLightAircraft: Boolean): EntityType<out TamableAnimal?> {
        return if (isLightAircraft) ModEntities.AIRPLANE.get() else ModEntities.TAKOYAKI.get()
    }

    override fun getAircraftLaunchHeight(): Double {
        return this.getBbHeight() * 0.9
    }

    override fun getShipSpawnEggItem(): Item {
        return ModItems.CARRIER_HIME_SPAWN_EGG.get()
    }

    override fun hasShipMounts(): Boolean {
        return true
    }

    override fun summonMountEntity(): EntityMountBase {
        return EntityMountCaH(ModEntities.MOUNT_CA_H.get(), this.level())
    }

    companion object {
        const val EQUIP_LEFT: String = "equip_left"
        const val EQUIP_RIGHT: String = "equip_right"
    }
}

