package org.trp.shincolle.init

import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.trp.shincolle.Shincolle
import java.util.function.Supplier

object ModTabs {
    val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab?> =
        DeferredRegister.create<CreativeModeTab?>(Registries.CREATIVE_MODE_TAB, Shincolle.MODID)

    val SHINCOLLE_TAB: DeferredHolder<CreativeModeTab?, CreativeModeTab?> =
        CREATIVE_MODE_TABS.register<CreativeModeTab?>("shincolle_tab", Supplier {
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.shincolle"))
                .icon(Supplier { ItemStack(ModItems.NORTHERN_HIME_SPAWN_EGG.get()) })
                .displayItems { _, output -> ShinColleCreativeTabContents.populate(output) }
                .build()
        })
}
