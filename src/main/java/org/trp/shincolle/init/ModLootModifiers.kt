package org.trp.shincolle.init

import com.mojang.serialization.MapCodec
import net.neoforged.neoforge.common.loot.IGlobalLootModifier
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import org.trp.shincolle.Shincolle
import org.trp.shincolle.loot.LegacyChestLootModifier
import java.util.function.Supplier

object ModLootModifiers {
    val LOOT_MODIFIERS: DeferredRegister<MapCodec<out IGlobalLootModifier?>?> =
        DeferredRegister.create<MapCodec<out IGlobalLootModifier?>?>(
            NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Shincolle.MODID
        )

    val LEGACY_CHEST_LOOT: DeferredHolder<MapCodec<out IGlobalLootModifier?>?, MapCodec<LegacyChestLootModifier?>?> =
        LOOT_MODIFIERS.register<MapCodec<LegacyChestLootModifier?>?>(
            "legacy_chest_loot",
            Supplier { LegacyChestLootModifier.CODEC })
}
