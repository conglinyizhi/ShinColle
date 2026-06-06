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
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.init.ModItems
import kotlin.math.max

class EntityCarrierWo(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    init {
        this.modelPos = floatArrayOf(0f, 20f, 0f, 30f)
        setStateMinor(STATE_MINOR_FACTION_ID, 5)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 12)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 1)
        setStateMinor(STATE_MINOR_RARITY, 5)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeCV)
        this.isStateGuiBtn1 = false
        this.isStateGuiBtn2 = false
        this.isStateLightAircraftAttack = true
        this.isStateHeavyAircraftAttack = true
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

        if ((this.tickCount and 0xF) == 0 && checkModelState(4, this.getStateEmotion(0))
            && !this.isInSittingPose && !this.isPassenger()
        ) {
            this.level().addParticle(
                ParticleTypes.CLOUD,
                this.getX(), this.getY() + 1.2, this.getZ(),
                0.0, 0.02, 0.0
            )
        }
    }

    private fun spawnEyeGlowParticles() {
        val radYaw = (this.yBodyRot % 360.0f) * Mth.DEG_TO_RAD
        val zOffset = if (this.isInSittingPose) -0.15f else 0.2f
        val left = rotateXZByAxis(zOffset, 0.55f, radYaw, 1.0f)
        val right = rotateXZByAxis(zOffset, -0.55f, radYaw, 1.0f)
        val yOffset = if (this.isInSittingPose) 1.25 else 1.5
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

        val duration = 30 + this.getStateMinor(0)
        val amp = max(0, this.getStateMinor(0) / 80)
        for (ship in ships) {
            if (ship === this) {
                continue
            }
            if (ship.ownerUUID != this.ownerUUID) {
                continue
            }
            ship.addEffect(MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, amp, false, false))
        }
    }

    override fun supportsAircraftCombat(): Boolean {
        return true
    }

    override fun getAttackAircraftType(isLightAircraft: Boolean): EntityType<out TamableAnimal?> {
        return if (isLightAircraft) ModEntities.AIRPLANE.get() else ModEntities.TAKOYAKI.get()
    }

    override val aircraftLaunchHeight: Double
        get() = this.getBbHeight() * 0.9

    override val aircraftLightLevelBonus: Float
        get() = 0.25f

    override val aircraftHeavyLevelBonus: Float
        get() = 0.15f

    override val shipSpawnEggItem: Item?
        get() = ModItems.CARRIER_WO_SPAWN_EGG.get()

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_CLOAKNECK, "gui.shincolle.equip.cloakneck"))
        list.add(EquipOption(EQUIP_EQUIPBASE, "gui.shincolle.equip.equipbase"))
        list.add(EquipOption(EQUIP_GLOWEQUIPBASE, "gui.shincolle.equip.glowequipbase"))
        list.add(EquipOption(EQUIP_NECK, "gui.shincolle.equip.neck"))
        list.add(EquipOption(EQUIP_STAFF, "gui.shincolle.equip.staff"))
        return list
    }

    companion object {
        const val EQUIP_CLOAKNECK: String = "equip_cloakneck"
        const val EQUIP_EQUIPBASE: String = "equip_equipbase"
        const val EQUIP_GLOWEQUIPBASE: String = "equip_glowequipbase"
        const val EQUIP_NECK: String = "equip_neck"
        const val EQUIP_STAFF: String = "equip_staff"
    }
}

