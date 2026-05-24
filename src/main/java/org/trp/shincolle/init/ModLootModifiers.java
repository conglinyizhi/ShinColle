package org.trp.shincolle.init;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.loot.LegacyChestLootModifier;

public class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Shincolle.MODID);

    public static final net.neoforged.neoforge.registries.DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<LegacyChestLootModifier>> LEGACY_CHEST_LOOT =
            LOOT_MODIFIERS.register("legacy_chest_loot", () -> LegacyChestLootModifier.CODEC);

    private ModLootModifiers() {}
}
