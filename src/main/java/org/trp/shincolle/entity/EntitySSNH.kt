package org.trp.shincolle.entity

import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModItems
import java.util.function.Predicate

class EntitySSNH(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    init {
        this.modelPos = floatArrayOf(-6f, 8f, 0f, 50f)
        setStateMinor(STATE_MINOR_FACTION_ID, 10)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 72)
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
        updateRidingState()
    }

    private fun updateServerLogic() {
        if (this.isStateRingEffect) {
            val duration = 80 + this.level
            this.addEffect(MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false))
            val owner = this.ownerPlayer

            if (this.isStateMarried && owner != null && this.distanceToSqr(owner) < 256.0) {
                owner.addEffect(MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false))
            }
        }
    }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_HAND_RING, "gui.shincolle.equip.ring"))
        list.add(EquipOption(EQUIP_RING_BASE, "gui.shincolle.equip.ring_base"))
        list.add(EquipOption(EQUIP_TORPEDO, "gui.shincolle.equip.torpedo"))
        return list
    }

    private fun updateRidingState() {
        if (this.isPassenger()) {
            val vehicle = this.getVehicle()
            if (vehicle is LivingEntity && vehicle.isCrouching()) {
                this.stopRiding()
                return
            }
            if ((this.tickCount % 40) == 0) {
                this.addEffect(MobEffectInstance(MobEffects.REGENERATION, 60, 0, false, false))
            }
            return
        }

        val canFindTarget = (this.tickCount and 0x7F) == 0 && this.getRandom().nextInt(4) == 0
        val isActionBlocked = this.isInSittingPose || this.isStateNoEquip || this.isLeashed()
        if (canFindTarget && !isActionBlocked) {
            findRideTarget()
        }
    }

    private fun findRideTarget() {
        val range = this.getBoundingBox().inflate(6.0, 4.0, 6.0)
        val candidates = this.level().getEntities(
            this, range,
            Predicate { ent: Entity? -> ent!!.isAlive && ent.canBeCollidedWith() && canRideEntity(ent) })
        if (!candidates.isEmpty()) {
            val target = candidates.get(this.getRandom().nextInt(candidates.size))
            this.startRiding(target, true)
        }
    }

    private fun canRideEntity(target: Entity?): Boolean {
        if (target === this) {
            return false
        }
        if (target is EntityShipBase) {
            return target.ownerUUID == this.ownerUUID
        }
        if (target is Player) {
            return target.getUUID() == this.ownerUUID
        }
        return false
    }

    override fun supportsItemPickup(): Boolean {
        return true
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.SSNH_SPAWN_EGG.get()

    override val isSubmarine: Boolean
        get() = true

    companion object {
        const val EQUIP_HAND_RING: String = "equip_hand_ring"
        const val EQUIP_RING_BASE: String = "equip_ring_base"
        const val EQUIP_TORPEDO: String = "equip_torpedo"
    }
}

