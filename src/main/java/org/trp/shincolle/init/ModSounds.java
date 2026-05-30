package org.trp.shincolle.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.trp.shincolle.Config;
import org.trp.shincolle.Shincolle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, Shincolle.MODID);

    private static final List<Supplier<SoundEvent>> SHIP_TIME_SOUNDS = createShipTimeSounds();

    public static final Supplier<SoundEvent> SHIP_IDLE = register("ship-idle");
    public static final Supplier<SoundEvent> SHIP_HURT = register("ship-hurt");
    public static final Supplier<SoundEvent> SHIP_DEATH = register("ship-death");
    public static final Supplier<SoundEvent> SHIP_FIRELIGHT = register("ship-firelight");
    public static final Supplier<SoundEvent> SHIP_EXPLODE = register("ship-explode");
    public static final Supplier<SoundEvent> SHIP_FIREHEAVY = register("ship-fireheavy");
    public static final Supplier<SoundEvent> SHIP_HIT = register("ship-hit");
    public static final Supplier<SoundEvent> SHIP_LEVELUP = register("ship-levelup");
    public static final Supplier<SoundEvent> SHIP_MACHINEGUN = register("ship-machinegun");
    public static final Supplier<SoundEvent> SHIP_AIRCRAFT = register("ship-aircraft");
    public static final Supplier<SoundEvent> SHIP_MARRY = register("ship-marry");
    public static final Supplier<SoundEvent> SHIP_FEED = register("ship-feed");
    public static final Supplier<SoundEvent> SHIP_KNOCKBACK = register("ship-knockback");
    public static final Supplier<SoundEvent> SHIP_ITEM = register("ship-item");
    public static final Supplier<SoundEvent> SHIP_AP_P1 = register("ship-ap_phase1");
    public static final Supplier<SoundEvent> SHIP_AP_P2 = register("ship-ap_phase2");
    public static final Supplier<SoundEvent> SHIP_AP_ATTACK = register("ship-ap_attack");
    public static final Supplier<SoundEvent> SHIP_YAMATO_READY = register("ship-yamato_ready");
    public static final Supplier<SoundEvent> SHIP_YAMATO_SHOT = register("ship-yamato_shot");

    private static List<Supplier<SoundEvent>> createShipTimeSounds() {
        List<Supplier<SoundEvent>> sounds = new ArrayList<>(24);
        for (int i = 0; i < 24; i++) {
            sounds.add(register("ship-time" + i));
        }
        return Collections.unmodifiableList(sounds);
    }

    public static SoundEvent getShipTimeSound(int hour) {
        int idx = Math.floorMod(hour, SHIP_TIME_SOUNDS.size());
        return SHIP_TIME_SOUNDS.get(idx).get();
    }

    public static SoundEvent getShipSound(Config.ShipCustomSoundType type, int shipClass, RandomSource random) {
        if (type == null) {
            return null;
        }

        Map<Integer, EnumMap<Config.ShipCustomSoundType, Float>> rateMap = Config.customSoundRates;
        if (rateMap != null && !rateMap.isEmpty()) {
            EnumMap<Config.ShipCustomSoundType, Float> shipRates = rateMap.get(shipClass);
            if (shipRates != null) {
                Float chance = shipRates.get(type);
                if (chance != null && chance > 0.0F && random.nextFloat() < chance) {
                    ResourceLocation customId = ResourceLocation.fromNamespaceAndPath(
                            Shincolle.MODID,
                            type.soundPath() + "-" + shipClass
                    );
                    SoundEvent customSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(customId);
                    if (customSound != null) {
                        return customSound;
                    }
                }
            }
        }

        return defaultSound(type);
    }

    private static SoundEvent defaultSound(Config.ShipCustomSoundType type) {
        return switch (type) {
            case IDLE -> SHIP_IDLE.get();
            case ATTACK -> SHIP_HIT.get();
            case HURT -> SHIP_HURT.get();
            case DEAD -> SHIP_DEATH.get();
            case MARRY -> SHIP_MARRY.get();
            case KNOCKBACK -> SHIP_KNOCKBACK.get();
            case ITEM -> SHIP_ITEM.get();
            case FEED -> SHIP_FEED.get();
            case TIMEKEEP00 -> SHIP_TIME_SOUNDS.get(0).get();
            case TIMEKEEP01 -> SHIP_TIME_SOUNDS.get(1).get();
            case TIMEKEEP02 -> SHIP_TIME_SOUNDS.get(2).get();
            case TIMEKEEP03 -> SHIP_TIME_SOUNDS.get(3).get();
            case TIMEKEEP04 -> SHIP_TIME_SOUNDS.get(4).get();
            case TIMEKEEP05 -> SHIP_TIME_SOUNDS.get(5).get();
            case TIMEKEEP06 -> SHIP_TIME_SOUNDS.get(6).get();
            case TIMEKEEP07 -> SHIP_TIME_SOUNDS.get(7).get();
            case TIMEKEEP08 -> SHIP_TIME_SOUNDS.get(8).get();
            case TIMEKEEP09 -> SHIP_TIME_SOUNDS.get(9).get();
            case TIMEKEEP10 -> SHIP_TIME_SOUNDS.get(10).get();
            case TIMEKEEP11 -> SHIP_TIME_SOUNDS.get(11).get();
            case TIMEKEEP12 -> SHIP_TIME_SOUNDS.get(12).get();
            case TIMEKEEP13 -> SHIP_TIME_SOUNDS.get(13).get();
            case TIMEKEEP14 -> SHIP_TIME_SOUNDS.get(14).get();
            case TIMEKEEP15 -> SHIP_TIME_SOUNDS.get(15).get();
            case TIMEKEEP16 -> SHIP_TIME_SOUNDS.get(16).get();
            case TIMEKEEP17 -> SHIP_TIME_SOUNDS.get(17).get();
            case TIMEKEEP18 -> SHIP_TIME_SOUNDS.get(18).get();
            case TIMEKEEP19 -> SHIP_TIME_SOUNDS.get(19).get();
            case TIMEKEEP20 -> SHIP_TIME_SOUNDS.get(20).get();
            case TIMEKEEP21 -> SHIP_TIME_SOUNDS.get(21).get();
            case TIMEKEEP22 -> SHIP_TIME_SOUNDS.get(22).get();
            case TIMEKEEP23 -> SHIP_TIME_SOUNDS.get(23).get();
        };
    }

    private static Supplier<SoundEvent> register(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
