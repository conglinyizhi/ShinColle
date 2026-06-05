package org.trp.shincolle.integration.jade

import snownee.jade.api.IWailaClientRegistration

@WailaPlugin
class ShinColleJadePlugin : IWailaPlugin {
    public override fun register(registration: IWailaCommonRegistration) {
        registration.registerBlockDataProvider(
            ShipyardJadeProvider.Companion.INSTANCE,
            SmallShipyardBlockEntity::class.java
        )
        registration.registerBlockDataProvider(
            ShipyardJadeProvider.Companion.INSTANCE,
            LargeShipyardBlockEntity::class.java
        )
    }

    public override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerBlockComponent(ShipyardJadeProvider.Companion.INSTANCE, SmallShipyardBlock::class.java)
        registration.registerBlockComponent(ShipyardJadeProvider.Companion.INSTANCE, LargeShipyardBlock::class.java)
        registration.registerEntityComponent(ShipJadeProvider.INSTANCE, EntityShipBase::class.java)
    }

    companion object {
        val SMALL_SHIPYARD_DATA: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "small_shipyard")
        val LARGE_SHIPYARD_DATA: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "large_shipyard")
    }
}
