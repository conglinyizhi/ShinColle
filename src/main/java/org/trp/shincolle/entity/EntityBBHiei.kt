package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModItems
import kotlin.math.max

class EntityBBHiei(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    @Suppress("MagicNumber")
    override val baseModelScale: Float = 0.45f

    init {
        this.modelPos = floatArrayOf(0f, 25f, 0f, 40f)
        setStateMinor(STATE_MINOR_FACTION_ID, 6)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 61)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 3)
        setStateMinor(STATE_MINOR_RARITY, 2)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeBB)
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
    }

    val passengersRidingOffset: Double
        get() {
            if (!this.isInSittingPose) {
                return (this.bbHeight * 0.75f).toDouble()
            }
            if (checkModelState(1, this.getStateEmotion(0))) {
                return (this.bbHeight * 0.42f).toDouble()
            }
            if (this.getStateEmotion(1) == 4) {
                return 0.0
            }
            return (this.bbHeight * 0.35f).toDouble()
        }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_RIGGING, "gui.shincolle.equip.rigging"))
        list.add(EquipOption(EQUIP_ANCHOR, "gui.shincolle.equip.anchor"))
        return list
    }

    private fun updateClientParticles() {
        if (this.tickCount % 4 == 0 && !this.isInSittingPose && this.getEquipFlag(EQUIP_RIGGING) && !this.isInDeadPose) {
            val partPos = rotateXZByAxis(-0.6f, 0.0f, this.yBodyRot * Mth.DEG_TO_RAD, 1.0f)
            for (i in 0..2) {
                this.level().addParticle(
                    ParticleTypes.SMOKE,
                    this.x + partPos[1], this.y + 1.17 + i * 0.1, this.z + partPos[0],
                    0.0, 0.0, 0.0
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
        val duration = 100 + this.getStateMinor(0) * 2
        val amp = max(0, this.getStateMinor(0) / 120)
        for (ship in ships) {
            if (ship === this) {
                continue
            }
            if (ship.ownerUUID != this.ownerUUID) {
                continue
            }
            ship.addEffect(MobEffectInstance(MobEffects.SATURATION, duration, amp, false, false))
        }
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.BB_HIEI_SPAWN_EGG.get()

    companion object {
        const val EQUIP_RIGGING: String = "equip_rigging"
        const val EQUIP_ANCHOR: String = "equip_anchor"
    }
}

