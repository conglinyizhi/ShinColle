package org.trp.shincolle.entity

import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.init.ModItems
import kotlin.math.max

class EntityBattleshipHime(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    init {
        this.modelPos = floatArrayOf(-6f, 30f, 0f, 40f)
        setStateMinor(STATE_MINOR_FACTION_ID, 10)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 26)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 3)
        setStateMinor(STATE_MINOR_RARITY, 1)
        this.isStateGuiBtn3 = false
        this.isStateGuiBtn4 = false
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()

        if ((this.tickCount % 128) == 0) {
            applyBuffToNearbyAllies()
        }
    }

    val passengersRidingOffset: Double
        get() {
            if (this.isInSittingPose) {
                if (this.getStateEmotion(1) == 4) {
                    return 0.0
                }
                return (this.getBbHeight() * 0.62f).toDouble()
            }
            return (this.getBbHeight() * 0.76f).toDouble()
        }

    private fun applyBuffToNearbyAllies() {
        if (!(this.isStateMarried && this.isStateRingEffect && this.getStateMinor(6) > 0)) {
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
            ship.addEffect(MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, amp, false, false))
            ship.addEffect(MobEffectInstance(MobEffects.FIRE_RESISTANCE, duration, amp, false, false))
        }
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.BATTLESHIP_HIME_SPAWN_EGG.get()

    override fun hasShipMounts(): Boolean {
        return true
    }

    override fun summonMountEntity(): EntityMountBase {
        return EntityMountBaH(ModEntities.MOUNT_BA_H.get(), this.level())
    }
}

