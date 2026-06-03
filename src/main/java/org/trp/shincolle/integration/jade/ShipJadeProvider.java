package org.trp.shincolle.integration.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.item.DebugInspectorItem;
import org.trp.shincolle.menu.ShipContainerMenu;
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
        boolean creativeInfinite = ship.hasCreativeDebugger();

        tooltip.add(Component.translatable("gui.shincolle.level")
                .append(": " + ship.getLevel()));
        tooltip.add(Component.translatable("gui.shincolle.hp")
                .append(": " + Math.round(ship.getHealth()) + " / " + Math.round(ship.getMaxHealth()))
                .append(creativeInfinite ? Component.literal(" ").append(DebugInspectorItem.creativeInfiniteLabel()) : Component.empty()));
        tooltip.add(Component.translatable("gui.shincolle.ammolight")
                .append(": ")
                .append(creativeInfinite ? DebugInspectorItem.creativeInfiniteLabel() : Component.literal(String.valueOf(ship.getAmmoLight()))));
        tooltip.add(Component.translatable("gui.shincolle.ammoheavy")
                .append(": ")
                .append(creativeInfinite ? DebugInspectorItem.creativeInfiniteLabel() : Component.literal(String.valueOf(ship.getAmmoHeavy()))));
        tooltip.add(Component.translatable("tooltip.shincolle.jade.ship.status", runningState(ship)));
        tooltip.add(Component.translatable("gui.shincolle.grudge")
                .append(": ")
                .append(creativeInfinite ? DebugInspectorItem.creativeInfiniteLabel() : Component.literal(String.valueOf(ship.getFuel()))));
    }

    private static Component runningState(EntityShipBase ship) {
        if (!ship.isAlive()) {
            return Component.translatable("tooltip.shincolle.jade.ship.status.idle");
        }
        if (ship.isNoFuel()) {
            return Component.translatable("tooltip.shincolle.jade.ship.status.no_fuel");
        }

        int taskId = ship.getStateMinor(ShipContainerMenu.STATE_MINOR_TASK_ID);
        if (taskId >= 1 && taskId <= 4) {
            return Component.translatable(switch (taskId) {
                case 1 -> "gui.shincolle.ai.cooking";
                case 2 -> "gui.shincolle.ai.fishing";
                case 3 -> "gui.shincolle.ai.mining";
                case 4 -> "gui.shincolle.ai.crafting";
                default -> "tooltip.shincolle.jade.ship.status.idle";
            });
        }
        if (ship.hasBlockGuardTarget()) {
            return Component.translatable("tooltip.shincolle.jade.ship.status.guard");
        }
        if (ship.hasPointerTargetEntity()) {
            return Component.translatable("tooltip.shincolle.jade.ship.status.pointer_attack");
        }
        if (ship.hasPointerTarget()) {
            return Component.translatable("tooltip.shincolle.jade.ship.status.pointer_move");
        }
        if (ship.shouldFollowOwner()) {
            return Component.translatable("tooltip.shincolle.jade.ship.status.follow");
        }
        return Component.translatable(switch (ship.explainFollowBlockReason()) {
            case "orderedToSit", "sittingPose" -> "tooltip.shincolle.jade.ship.status.standby";
            default -> "tooltip.shincolle.jade.ship.status.idle";
        });
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
