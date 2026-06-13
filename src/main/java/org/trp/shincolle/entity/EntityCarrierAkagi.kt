package org.trp.shincolle.entity

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.Config
import org.trp.shincolle.Shincolle
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.init.ModItems
import java.util.List
import kotlin.math.max

class EntityCarrierAkagi(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    @Suppress("MagicNumber")
    override val baseModelScale: Float = 0.46f

    init {
        this.modelPos = floatArrayOf(0f, 20f, 0f, 40f)
        setStateMinor(STATE_MINOR_FACTION_ID, 5)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 48)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 1)
        setStateMinor(STATE_MINOR_RARITY, 8)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeCV)
        this.isStateGuiBtn1 = false
        this.isStateGuiBtn2 = false
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
        if ((this.tickCount % 128) == 0 && this.random.nextInt(4) == 0 && !this.isStateNoEquip) {
            this.applyParticleEmotion(9)
        }
    }

    private fun updateServerLogic() {
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

        val duration = 50 + this.getStateMinor(0)
        val amp = max(0, this.getStateMinor(0) / 85)
        for (ship in ships) {
            if (ship === this) {
                continue
            }
            if (ship.ownerUUID != this.ownerUUID) {
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
        get() = this.bbHeight * 0.65

    override val aircraftLightLevelBonus: Float
        get() = 0.28f

    override val aircraftHeavyLevelBonus: Float
        get() = 0.18f

    override fun onAircraftLaunched(lightAircraft: Boolean) {
        this.playSound(
            SoundEvents.ARROW_SHOOT,
            Config.volumeAttack + 0.2f,
            1f / (this.random.nextFloat() * 0.4f + 1.2f) + 0.5f
        )
    }

    override fun onLightAttackSound(ship: EntityShipBase, target: LivingEntity?): SoundEvent? {
        return tryGetAttackVoice()
    }

    override fun onHeavyAttackSound(ship: EntityShipBase, target: LivingEntity?): SoundEvent? {
        return tryGetAttackVoice()
    }

    private fun tryGetAttackVoice(): SoundEvent? {
        if (this.random.nextFloat() < 0.3f) {
            val customId = ResourceLocation.fromNamespaceAndPath(
                Shincolle.MODID,
                Config.ShipCustomSoundType.ATTACK.soundPath() + "-" + this.getStateMinor(STATE_MINOR_SHIP_CLASS)
            )
            return BuiltInRegistries.SOUND_EVENT.get(customId)
        }
        return null
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.CARRIER_AKAGI_SPAWN_EGG.get()

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

