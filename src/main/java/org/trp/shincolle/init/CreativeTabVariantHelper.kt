package org.trp.shincolle.init

import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredItem
import org.trp.shincolle.item.AbyssNuggetItem
import org.trp.shincolle.item.CombatRationItem
import org.trp.shincolle.item.GrudgeItem
import org.trp.shincolle.item.LegacyEquipItem
import org.trp.shincolle.item.PointerItem
import org.trp.shincolle.item.ShipTankItem
import java.util.function.ToIntFunction

object CreativeTabVariantHelper {
    fun addLegacyEquipVariants(output: CreativeModeTab.Output, item: DeferredItem<Item>) {
        val resolved = item.get()
        if (resolved is LegacyEquipItem) {
            resolved.addAllVariantsToCreativeTab(output)
            return
        }

        output.accept(resolved)
    }

    fun addSortedLegacyEquipVariants(output: CreativeModeTab.Output, item: DeferredItem<Item>) {
        val resolved = item.get()
        if (resolved !is LegacyEquipItem) {
            output.accept(resolved)
            return
        }

        val variants: MutableList<ItemStack> = ArrayList()
        for (variant in 0..<resolved.variantCount) {
            variants.add(resolved.createVariantStack(variant))
        }

        variants.sortWith(
            Comparator
                .comparingInt<ItemStack>(ToIntFunction { stack -> resolved.getVariant(stack) })
                .thenComparingInt(ToIntFunction { stack -> resolved.getEquipId(stack) })
                .thenComparingInt(ToIntFunction { stack -> resolved.getVariant(stack) })
        )

        for (stack in variants) {
            output.accept(stack)
        }
    }

    fun addShipTankVariants(output: CreativeModeTab.Output) {
        addResolvedVariants(output, ModItems.SHIP_TANK.get())
    }

    fun addCombatRationVariants(output: CreativeModeTab.Output) {
        addResolvedVariants(output, ModItems.COMBAT_RATION.get())
    }

    fun addGrudgeVariants(output: CreativeModeTab.Output) {
        addResolvedVariants(output, ModItems.GRUDGE.get())
    }

    fun addAbyssNuggetVariants(output: CreativeModeTab.Output) {
        addResolvedVariants(output, ModItems.ABYSS_NUGGET.get())
    }

    fun addPointerVariants(output: CreativeModeTab.Output) {
        addResolvedVariants(output, ModItems.POINTER_ITEM.get())
    }

    private fun addResolvedVariants(output: CreativeModeTab.Output, item: Item) {
        when (item) {
            is ShipTankItem -> item.addAllVariantsToCreativeTab(output)
            is CombatRationItem -> item.addAllVariantsToCreativeTab(output)
            is GrudgeItem -> item.addAllVariantsToCreativeTab(output)
            is AbyssNuggetItem -> item.addAllVariantsToCreativeTab(output)
            is PointerItem -> item.addAllVariantsToCreativeTab(output)
            else -> output.accept(item)
        }
    }
}
