package org.trp.shincolle.integration.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.entity.base.EntityShipBase;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum ShipJadeProvider implements IEntityComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "ship");

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (!(accessor.getEntity() instanceof EntityShipBase ship)) {
            return;
        }

        tooltip.add(Component.translatable("gui.shincolle.level")
                .append(": " + ship.getLevel()));
        tooltip.add(Component.translatable("gui.shincolle.hp")
                .append(": " + Math.round(ship.getHealth()) + " / " + Math.round(ship.getMaxHealth())));
        tooltip.add(Component.translatable("gui.shincolle.ammolight")
                .append(": " + ship.getAmmoLight()));
        tooltip.add(Component.translatable("gui.shincolle.ammoheavy")
                .append(": " + ship.getAmmoHeavy()));
        tooltip.add(Component.translatable("gui.shincolle.grudge")
                .append(": " + ship.getFuel()));
    }

    @Override
