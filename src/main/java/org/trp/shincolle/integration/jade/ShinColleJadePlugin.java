package org.trp.shincolle.integration.jade;

import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.block.LargeShipyardBlock;
import org.trp.shincolle.block.SmallShipyardBlock;
import org.trp.shincolle.block.entity.LargeShipyardBlockEntity;
import org.trp.shincolle.block.entity.SmallShipyardBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class ShinColleJadePlugin implements IWailaPlugin {

    public static final ResourceLocation SMALL_SHIPYARD_DATA =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "small_shipyard");
    public static final ResourceLocation LARGE_SHIPYARD_DATA =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "large_shipyard");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(ShipyardJadeProvider.INSTANCE, SmallShipyardBlockEntity.class);
        registration.registerBlockDataProvider(ShipyardJadeProvider.INSTANCE, LargeShipyardBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(ShipyardJadeProvider.INSTANCE, SmallShipyardBlock.class);
        registration.registerBlockComponent(ShipyardJadeProvider.INSTANCE, LargeShipyardBlock.class);
    }
}
