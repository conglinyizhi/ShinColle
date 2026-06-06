package org.trp.shincolle.entity

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
import java.util.List
import kotlin.math.max

class EntityCarrierKaga(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    init {
        this.modelPos = floatArrayOf(0f, 20f, 0f, 40f)
        setStateMinor(STATE_MINOR_FACTION_ID, 5)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 47)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 1)
        setStateMinor(STATE_MINOR_RARITY, 8)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeCV)
        this.isStateGuiBtn1 = false
        this.isStateGuiBtn2 = false
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()

        if ((this.tickCount % 128) == 0) {
            updateServerLogic()
        }
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

        val duration = 50 + this.getStateMinor(0)
        val amp = max(0, this.getStateMinor(0) / 85)
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

    override fun supportsAircraftCombat(): Boolean {
        return true
    }

    override fun getAttackAircraftType(isLightAircraft: Boolean): EntityType<out TamableAnimal?> {
        return if (isLightAircraft) ModEntities.AIRPLANE_ZERO.get() else ModEntities.AIRPLANE_T.get()
    }

    override val aircraftLaunchHeight: Double
        get() = this.getBbHeight() * 0.65

    override fun getAircraftLightLevelBonus(): Float {
        return 0.4f
    }

    override fun getAircraftHeavyLevelBonus(): Float {
        return 0.2f
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.CARRIER_KAGA_SPAWN_EGG.get()

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.addAll(
            List.of<EquipOption?>(
                EquipOption(EQUIP_CAT_PARTS, "gui.shincolle.equip.cat_parts"),
                EquipOption(EQUIP_BACK_QUIVER, "gui.shincolle.equip.back_quiver"),
                EquipOption(EQUIP_BREASTPLATE, "gui.shincolle.equip.breastplate"),
                EquipOption(EQUIP_RIGGING, "gui.shincolle.equip.rigging"),
                EquipOption(EQUIP_DECK_HAND, "gui.shincolle.equip.deck_hand"),
                EquipOption(EQUIP_BOW, "gui.shincolle.equip.bow"),
                EquipOption(EQUIP_SKIRT, "gui.shincolle.equip.skirt"),
                EquipOption(EQUIP_SHOES, "gui.shincolle.equip.shoes")
            )
        )
        return list
    }

    companion object {
        const val EQUIP_CAT_PARTS: String = "equip_cat_parts"
        const val EQUIP_BACK_QUIVER: String = "equip_back_quiver"
        const val EQUIP_BREASTPLATE: String = "equip_breastplate"
        const val EQUIP_RIGGING: String = "equip_rigging"
        const val EQUIP_DECK_HAND: String = "equip_deck_hand"
        const val EQUIP_BOW: String = "equip_bow"
        const val EQUIP_SKIRT: String = "equip_skirt"
        const val EQUIP_SHOES: String = "equip_shoes"
    }
}

