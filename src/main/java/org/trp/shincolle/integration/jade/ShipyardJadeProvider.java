package org.trp.shincolle.integration.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.block.entity.LargeShipyardBlockEntity;
import org.trp.shincolle.block.entity.SmallShipyardBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IJadeProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class ShipyardJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor>, IJadeProvider {

    public static final ShipyardJadeProvider INSTANCE = new ShipyardJadeProvider();
    private static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "shipyard");

    private static final String KEY_ACTIVE = "active";
    private static final String KEY_POWER_CONSUMED = "powerConsumed";
    private static final String KEY_POWER_GOAL = "powerGoal";
    private static final String KEY_POWER_REMAINED = "powerRemained";
    private static final String KEY_REMAINING_TIME = "remainingTime";
    private static final String KEY_BUILD_TYPE = "buildType";
    private static final String KEY_MATS_BUILD = "matsBuild";
    private static final String KEY_MATS_STOCK = "matsStock";

    private ShipyardJadeProvider() {
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (data == null || data.isEmpty()) {
            return;
        }

        int buildType = data.getInt(KEY_BUILD_TYPE);
        if (buildType <= 0) {
            tooltip.add(Component.translatable("tooltip.shincolle.jade.shipyard.idle"));
            return;
        }

        boolean active = data.getBoolean(KEY_ACTIVE);
        int consumed = data.getInt(KEY_POWER_CONSUMED);
        int goal = data.getInt(KEY_POWER_GOAL);
        int remained = data.getInt(KEY_POWER_REMAINED);
        String remainingTime = data.getString(KEY_REMAINING_TIME);

        tooltip.add(Component.translatable(
                active ? "tooltip.shincolle.jade.shipyard.active" : "tooltip.shincolle.jade.shipyard.ready"));
        tooltip.add(Component.translatable("tooltip.shincolle.jade.shipyard.progress", consumed, goal));
        tooltip.add(Component.translatable("tooltip.shincolle.jade.shipyard.fuel", remained));
        if (!remainingTime.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.shincolle.jade.shipyard.time", remainingTime));
        }

        if (data.contains(KEY_MATS_BUILD)) {
            tooltip.add(Component.translatable(
                    "tooltip.shincolle.jade.shipyard.materials",
                    formatMats(data.getIntArray(KEY_MATS_BUILD))));
        }

        if (data.contains(KEY_MATS_STOCK)) {
            tooltip.add(Component.translatable(
                    "tooltip.shincolle.jade.shipyard.stock",
                    formatMats(data.getIntArray(KEY_MATS_STOCK))));
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        BlockEntity blockEntity = accessor.getBlockEntity();
        if (blockEntity instanceof SmallShipyardBlockEntity smallShipyard) {
            appendSmallShipyard(tag, smallShipyard);
        } else if (blockEntity instanceof LargeShipyardBlockEntity largeShipyard) {
            appendLargeShipyard(tag, largeShipyard);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    private static void appendSmallShipyard(CompoundTag tag, SmallShipyardBlockEntity shipyard) {
        tag.putBoolean(KEY_ACTIVE, shipyard.hasRemainedPower() && shipyard.getPowerGoal() > 0);
        tag.putInt(KEY_POWER_CONSUMED, shipyard.getPowerConsumed());
        tag.putInt(KEY_POWER_GOAL, shipyard.getPowerGoal());
        tag.putInt(KEY_POWER_REMAINED, shipyard.getPowerRemained());
        tag.putInt(KEY_BUILD_TYPE, shipyard.getBuildType());
        tag.putString(KEY_REMAINING_TIME, shipyard.getBuildTimeString());
        tag.putIntArray(KEY_MATS_BUILD, shipyard.getBuildRecord());
    }

    private static void appendLargeShipyard(CompoundTag tag, LargeShipyardBlockEntity shipyard) {
        tag.putBoolean(KEY_ACTIVE, shipyard.hasRemainedPower() && shipyard.getPowerGoal() > 0);
        tag.putInt(KEY_POWER_CONSUMED, shipyard.getPowerConsumed());
        tag.putInt(KEY_POWER_GOAL, shipyard.getPowerGoal());
        tag.putInt(KEY_POWER_REMAINED, shipyard.getPowerRemained());
        tag.putInt(KEY_BUILD_TYPE, shipyard.getBuildType());
        tag.putString(KEY_REMAINING_TIME, shipyard.getBuildTimeString());
        tag.putIntArray(KEY_MATS_BUILD, shipyard.getMatsBuild());
        tag.putIntArray(KEY_MATS_STOCK, shipyard.getMatsStock());
    }

    private static String formatMats(int[] mats) {
        if (mats == null || mats.length < 4) {
            return "0 / 0 / 0 / 0";
        }
        return mats[0] + " / " + mats[1] + " / " + mats[2] + " / " + mats[3];
    }
}
