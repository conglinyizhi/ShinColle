package org.trp.shincolle.init

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.util.RandomSource
import net.neoforged.neoforge.registries.DeferredRegister
import org.trp.shincolle.Config
import org.trp.shincolle.Config.ShipCustomSoundType
import org.trp.shincolle.Shincolle
import java.util.*
import java.util.function.Supplier

object ModSounds {
    val SOUND_EVENTS: DeferredRegister<SoundEvent> =
        DeferredRegister.create<SoundEvent>(Registries.SOUND_EVENT, Shincolle.MODID)

    private val SHIP_TIME_SOUNDS: MutableList<Supplier<SoundEvent>> = createShipTimeSounds()

    val SHIP_IDLE: Supplier<SoundEvent> = register("ship-idle")
    val SHIP_HURT: Supplier<SoundEvent> = register("ship-hurt")
    val SHIP_DEATH: Supplier<SoundEvent> = register("ship-death")
    @JvmField
    val SHIP_FIRELIGHT: Supplier<SoundEvent> = register("ship-firelight")
    val SHIP_EXPLODE: Supplier<SoundEvent> = register("ship-explode")
    @JvmField
    val SHIP_FIREHEAVY: Supplier<SoundEvent> = register("ship-fireheavy")
    val SHIP_HIT: Supplier<SoundEvent> = register("ship-hit")
    @JvmField
    val SHIP_LEVELUP: Supplier<SoundEvent> = register("ship-levelup")
    val SHIP_MACHINEGUN: Supplier<SoundEvent> = register("ship-machinegun")
    @JvmField
    val SHIP_AIRCRAFT: Supplier<SoundEvent> = register("ship-aircraft")
    val SHIP_MARRY: Supplier<SoundEvent> = register("ship-marry")
    val SHIP_FEED: Supplier<SoundEvent> = register("ship-feed")
    val SHIP_KNOCKBACK: Supplier<SoundEvent> = register("ship-knockback")
    val SHIP_ITEM: Supplier<SoundEvent> = register("ship-item")
    val SHIP_AP_P1: Supplier<SoundEvent> = register("ship-ap_phase1")
    val SHIP_AP_P2: Supplier<SoundEvent> = register("ship-ap_phase2")
    val SHIP_AP_ATTACK: Supplier<SoundEvent> = register("ship-ap_attack")
    val SHIP_YAMATO_READY: Supplier<SoundEvent> = register("ship-yamato_ready")
    val SHIP_YAMATO_SHOT: Supplier<SoundEvent> = register("ship-yamato_shot")

    private fun createShipTimeSounds(): MutableList<Supplier<SoundEvent>> {
        val sounds: MutableList<Supplier<SoundEvent>> = ArrayList<Supplier<SoundEvent>>(24)
        for (i in 0..23) {
            sounds.add(register("ship-time" + i))
        }
        return Collections.unmodifiableList<Supplier<SoundEvent>>(sounds)
    }

    fun getShipTimeSound(hour: Int): SoundEvent {
        val idx = Math.floorMod(hour, SHIP_TIME_SOUNDS.size)
        return SHIP_TIME_SOUNDS.get(idx).get()
    }

    @JvmStatic
    fun getShipSound(type: ShipCustomSoundType, shipClass: Int, random: RandomSource): SoundEvent {
        val rateMap = Config.customSoundRates
        if (rateMap != null && !rateMap.isEmpty()) {
            val shipRates = rateMap.get(shipClass)
            if (shipRates != null) {
                val chance = shipRates.get(type)
                if (chance != null && chance > 0.0f && random.nextFloat() < chance) {
                    val customId = ResourceLocation.fromNamespaceAndPath(
                        Shincolle.MODID,
                        type.soundPath() + "-" + shipClass
                    )
                    val customSound = BuiltInRegistries.SOUND_EVENT.get(customId)
                    if (customSound != null) {
                        return customSound
                    }
                }
            }
        }

        return defaultSound(type)
    }

    private fun defaultSound(type: ShipCustomSoundType): SoundEvent {
        return when (type) {
            ShipCustomSoundType.IDLE -> SHIP_IDLE.get()
            ShipCustomSoundType.ATTACK -> SHIP_HIT.get()
            ShipCustomSoundType.HURT -> SHIP_HURT.get()
            ShipCustomSoundType.DEAD -> SHIP_DEATH.get()
            ShipCustomSoundType.MARRY -> SHIP_MARRY.get()
            ShipCustomSoundType.KNOCKBACK -> SHIP_KNOCKBACK.get()
            ShipCustomSoundType.ITEM -> SHIP_ITEM.get()
            ShipCustomSoundType.FEED -> SHIP_FEED.get()
            ShipCustomSoundType.TIMEKEEP00 -> SHIP_TIME_SOUNDS.get(0).get()
            ShipCustomSoundType.TIMEKEEP01 -> SHIP_TIME_SOUNDS.get(1).get()
            ShipCustomSoundType.TIMEKEEP02 -> SHIP_TIME_SOUNDS.get(2).get()
            ShipCustomSoundType.TIMEKEEP03 -> SHIP_TIME_SOUNDS.get(3).get()
            ShipCustomSoundType.TIMEKEEP04 -> SHIP_TIME_SOUNDS.get(4).get()
            ShipCustomSoundType.TIMEKEEP05 -> SHIP_TIME_SOUNDS.get(5).get()
            ShipCustomSoundType.TIMEKEEP06 -> SHIP_TIME_SOUNDS.get(6).get()
            ShipCustomSoundType.TIMEKEEP07 -> SHIP_TIME_SOUNDS.get(7).get()
            ShipCustomSoundType.TIMEKEEP08 -> SHIP_TIME_SOUNDS.get(8).get()
            ShipCustomSoundType.TIMEKEEP09 -> SHIP_TIME_SOUNDS.get(9).get()
            ShipCustomSoundType.TIMEKEEP10 -> SHIP_TIME_SOUNDS.get(10).get()
            ShipCustomSoundType.TIMEKEEP11 -> SHIP_TIME_SOUNDS.get(11).get()
            ShipCustomSoundType.TIMEKEEP12 -> SHIP_TIME_SOUNDS.get(12).get()
            ShipCustomSoundType.TIMEKEEP13 -> SHIP_TIME_SOUNDS.get(13).get()
            ShipCustomSoundType.TIMEKEEP14 -> SHIP_TIME_SOUNDS.get(14).get()
            ShipCustomSoundType.TIMEKEEP15 -> SHIP_TIME_SOUNDS.get(15).get()
            ShipCustomSoundType.TIMEKEEP16 -> SHIP_TIME_SOUNDS.get(16).get()
            ShipCustomSoundType.TIMEKEEP17 -> SHIP_TIME_SOUNDS.get(17).get()
            ShipCustomSoundType.TIMEKEEP18 -> SHIP_TIME_SOUNDS.get(18).get()
            ShipCustomSoundType.TIMEKEEP19 -> SHIP_TIME_SOUNDS.get(19).get()
            ShipCustomSoundType.TIMEKEEP20 -> SHIP_TIME_SOUNDS.get(20).get()
            ShipCustomSoundType.TIMEKEEP21 -> SHIP_TIME_SOUNDS.get(21).get()
            ShipCustomSoundType.TIMEKEEP22 -> SHIP_TIME_SOUNDS.get(22).get()
            ShipCustomSoundType.TIMEKEEP23 -> SHIP_TIME_SOUNDS.get(23).get()
        }
    }

    private fun register(name: String): Supplier<SoundEvent> {
        val id = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, name)
        return SOUND_EVENTS.register<SoundEvent>(name, Supplier { SoundEvent.createVariableRangeEvent(id) })
    }
}
