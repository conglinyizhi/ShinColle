package org.trp.shincolle.integration.jade

import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.block.LargeShipyardBlock
import org.trp.shincolle.block.SmallShipyardBlock
import org.trp.shincolle.block.entity.LargeShipyardBlockEntity
import org.trp.shincolle.block.entity.SmallShipyardBlockEntity
import org.trp.shincolle.entity.base.EntityShipBase
import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaCommonRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.WailaPlugin

@WailaPlugin
class ShinColleJadePlugin : IWailaPlugin {

    override fun register(registration: IWailaCommonRegistration) {
        registration.registerBlockDataProvider(
            ShipyardJadeProvider.INSTANCE,
            SmallShipyardBlockEntity::class.java
        )
        registration.registerBlockDataProvider(
            ShipyardJadeProvider.INSTANCE,
            LargeShipyardBlockEntity::class.java
        )
        registration.registerEntityDataProvider(ShipJadeProvider.INSTANCE, EntityShipBase::class.java)
    }

    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerBlockComponent(ShipyardJadeProvider.INSTANCE, SmallShipyardBlock::class.java)
        registration.registerBlockComponent(ShipyardJadeProvider.INSTANCE, LargeShipyardBlock::class.java)
        registration.registerEntityComponent(ShipJadeProvider.INSTANCE, EntityShipBase::class.java)
    }

    companion object {
        val SMALL_SHIPYARD_DATA: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "small_shipyard")
        val LARGE_SHIPYARD_DATA: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "large_shipyard")
    }
}
