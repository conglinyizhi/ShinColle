package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModItems
import kotlin.math.max

class EntityTransportWa(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    @Suppress("MagicNumber")
    override val baseModelScale: Float = 0.4f

    init {
        this.modelPos = floatArrayOf(-3f, 20f, 0f, 45f)
        setStateMinor(STATE_MINOR_FACTION_ID, 7)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 16)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 0)
        setStateMinor(STATE_MINOR_RARITY, 3)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeAP)
        this.isStateGuiBtn1 = false
        this.isStateGuiBtn2 = false
        this.isStateGuiBtn3 = false
        this.isStateGuiBtn4 = false
        this.isStateCanRide = true
    }

    override val isNonCombatShip: Boolean
        get() = true

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

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_BASE, "gui.shincolle.equip.base"))
        list.add(EquipOption(EQUIP_LEG, "gui.shincolle.equip.leg"))
        list.add(EquipOption(EQUIP_HEAD_BASE, "gui.shincolle.equip.head_base"))
        return list
    }

    private fun updateClientEffects() {
        if ((this.tickCount % 128) == 0 && this.random.nextInt(4) == 0) {
            this.applyParticleEmotion(2)
        }
    }

    private fun updateServerLogic() {
        if (this.getStateMinor(6) <= 5400) {
            consumeSupplyItems(0)
        }
        if (this.ammoLight <= 540) {
            consumeSupplyItems(1)
        }
        if (this.ammoHeavy <= 270) {
            consumeSupplyItems(2)
        }

        if ((this.tickCount % 256) == 0 && !this.isStateNoEquip) {
            trySupplyAllies()
        }
    }

    private fun trySupplyAllies() {
        var supCount = this.level / 50 + 1
        val range = 2.0 + this.getAttributeValue(Attributes.FOLLOW_RANGE) * 0.5
        val ships = this.level().getEntitiesOfClass<EntityShipBase?>(
            EntityShipBase::class.java,
            this.boundingBox.inflate(range, range, range)
        )
        if (ships.isEmpty()) {
            return
        }

        for (ship in ships) {
            if (supCount <= 0) {
                break
            }
            if (ship === this) {
                continue
            }
            if (ship.ownerUUID != this.ownerUUID) {
                continue
            }

            var supplied = false
            if (this.getStateMinor(6) > 5400 && ship.getStateMinor(6) < 2700) {
                addGrudge(-5400)
                val grant = max(0, (5400.0f * ship.legacyShipStats.getBuffedAttr(17)).toInt())
                ship.setStateMinor(6, max(0, ship.getStateMinor(6) + grant))
                supplied = true
            }
            if (this.ammoLight >= 540 && ship.ammoLight < 270) {
                addAmmoLight(-540)
                val grant = max(0, (540.0f * ship.legacyShipStats.getBuffedAttr(18)).toInt())
                ship.ammoLight = max(0, ship.ammoLight + grant)
                supplied = true
            }
            if (this.ammoHeavy >= 270 && ship.ammoHeavy < 135) {
                addAmmoHeavy(-270)
                val grant = max(0, (270.0f * ship.legacyShipStats.getBuffedAttr(18)).toInt())
                ship.ammoHeavy = max(0, ship.ammoHeavy + grant)
                supplied = true
            }

            if (supplied) {
                spawnSupplyParticles(ship)
                supCount--
            }
        }
    }

    private fun spawnSupplyParticles(target: EntityShipBase) {
        if (this.level() !is ServerLevel) {
            return
        }
        val midX = (this.x + target.x) * 0.5
        val midY = (this.y + target.y) * 0.5 + 0.6
        val midZ = (this.z + target.z) * 0.5
        (this.level() as ServerLevel).sendParticles<SimpleParticleType?>(
            ParticleTypes.HAPPY_VILLAGER, midX, midY, midZ,
            6, 0.3, 0.2, 0.3, 0.01
        )
    }

    private fun consumeSupplyItems(type: Int) {
        when (type) {
            0 -> {
                if (consumeItemInInventory(ModItems.GRUDGE.get())) {
                    addGrudge(3000)
                    return
                }
                if (consumeItemInInventory(ModItems.GRUDGE_HEAVY_BLOCK.get())) {
                    addGrudge(27000)
                }
            }

            1 -> {
                if (consumeItemInInventory(ModItems.AMMO_LIGHT.get())) {
                    addAmmoLight(30)
                    return
                }
                if (consumeItemInInventory(ModItems.AMMO_LIGHT_CONTAINER.get())) {
                    addAmmoLight(270)
                }
            }

            2 -> {
                if (consumeItemInInventory(ModItems.AMMO_HEAVY.get())) {
                    addAmmoHeavy(15)
                    return
                }
                if (consumeItemInInventory(ModItems.AMMO_HEAVY_CONTAINER.get())) {
                    addAmmoHeavy(135)
                }
            }

            else -> {}
        }
    }


    private fun addGrudge(amount: Int) {
        val next = max(0, this.getStateMinor(6) + amount)
        this.setStateMinor(6, next)
    }

    private fun addAmmoLight(amount: Int) {
        val next = max(0, this.ammoLight + amount)
        this.ammoLight = next
    }

    private fun addAmmoHeavy(amount: Int) {
        val next = max(0, this.ammoHeavy + amount)
        this.ammoHeavy = next
    }

    override fun supportsItemPickup(): Boolean {
        return true
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.TRANSPORT_WA_SPAWN_EGG.get()

    companion object {
        const val EQUIP_BASE: String = "equip_base"
        const val EQUIP_LEG: String = "equip_leg"
        const val EQUIP_HEAD_BASE: String = "equip_head_base"
    }
}
