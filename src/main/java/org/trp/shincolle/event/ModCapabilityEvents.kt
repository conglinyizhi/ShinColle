package org.trp.shincolle.event

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.ICapabilityProvider
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack
import org.trp.shincolle.init.ModDataComponents
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.item.ShipTankItem

object ModCapabilityEvents {
    fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        event.registerItem<IFluidHandlerItem?, Void?>(
            Capabilities.FluidHandler.ITEM,
            ICapabilityProvider { stack: ItemStack?, context: Void? ->
                FluidHandlerItemStack(
                    ModDataComponents.SHIPTANK_FLUID,
                    stack,
                    ShipTankItem.getCapacity(stack!!)
                )
            },
            ModItems.SHIP_TANK.get()
        )
    }
}
