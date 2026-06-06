package org.trp.shincolle.entity

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

class EntityHeavyCruiserRi(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    init {
        this.modelPos = floatArrayOf(0f, 20f, 0f, 40f)
        setStateMinor(STATE_MINOR_FACTION_ID, 2)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 9)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 4)
        setStateMinor(STATE_MINOR_RARITY, 4)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeCA)
        this.isStateGuiBtn3 = false
        this.isStateGuiBtn4 = false
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()

        if ((this.tickCount % 128) == 0) {
            updateServerLogic()
        }
    }

    val passengersRidingOffset: Double
        get() {
            if (this.isInSittingPose) {
                return (if (this.getStateEmotion(1) == 4) this.getBbHeight() * 0.05f else this.getBbHeight() * 0.55f).toDouble()
            }
            return (this.getBbHeight() * 0.7f).toDouble()
        }

    private fun updateServerLogic() {
        if (!this.level().isDay() && this.isStateRingEffect) {
            val duration = 150
            val ampSpeed = max(0, this.getStateMinor(0) / 50)
            val ampJump = max(0, this.getStateMinor(0) / 40)
            this.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, ampSpeed, false, false))
            this.addEffect(MobEffectInstance(MobEffects.JUMP, duration, ampJump, false, false))
        }
    }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_LEFT, "gui.shincolle.equip.left"))
        list.add(EquipOption(EQUIP_RIGHT, "gui.shincolle.equip.right"))
        list.add(EquipOption(EQUIP_CLOAK, "gui.shincolle.equip.cloak"))
        list.add(EquipOption(EQUIP_HAIR, "gui.shincolle.equip.hair"))
        return list
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.HEAVY_CRUISER_RI_SPAWN_EGG.get()

    companion object {
        const val EQUIP_LEFT: String = "equip_left"
        const val EQUIP_RIGHT: String = "equip_right"
        const val EQUIP_CLOAK: String = "equip_cloak"
        const val EQUIP_HAIR: String = "equip_hair"
    }
}

