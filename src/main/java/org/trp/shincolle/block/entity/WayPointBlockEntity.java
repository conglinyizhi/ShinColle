package org.trp.shincolle.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.trp.shincolle.client.WaypointClientHelper;
import org.trp.shincolle.init.ModBlockEntities;

import javax.annotation.Nullable;
import java.util.UUID;

public class WayPointBlockEntity extends BlockEntity implements IWaypoint {

    private int tickCount = 0;
    private BlockPos lastPos = BlockPos.ZERO;
    private BlockPos nextPos = BlockPos.ZERO;
    private BlockPos chestPos = BlockPos.ZERO;
    private int wpStayTime = 0;
    private UUID ownerUUID = null;
    private String ownerName = "";

    public WayPointBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WAYPOINT.get(), pos, state);
    }

    public BlockPos getLastPos() { return lastPos; }
    public void setLastPos(BlockPos pos) {
        this.lastPos = pos == null ? BlockPos.ZERO : pos;
        markForSync();
    }

    public BlockPos getNextPos() { return nextPos; }
    public void setNextPos(BlockPos pos) {
        this.nextPos = pos == null ? BlockPos.ZERO : pos;
        markForSync();
    }

    public BlockPos getChestPos() { return chestPos; }
    public void setChestPos(BlockPos pos) {
        this.chestPos = pos == null ? BlockPos.ZERO : pos;
        markForSync();
    }

    public int getWpStayTime() { return wpStayTime; }

    public int getStayTimeTicks() {
        if (wpStayTime >= 1 && wpStayTime <= 5) return wpStayTime * 100;
        if (wpStayTime >= 6 && wpStayTime <= 10) return (wpStayTime - 5) * 1200;
        if (wpStayTime >= 11 && wpStayTime <= 16) return (wpStayTime - 10) * 12000;
        return 0;
    }

    @Override
    public String getStayTimeDisplay() {
        int ticks = getStayTimeTicks();
        if (ticks == 0) return "0s";
        int totalSec = ticks / 20;
        if (totalSec < 60) return totalSec + "s";
        return (totalSec / 60) + "m";
    }

    public void nextWpStayTime() {
        wpStayTime = (wpStayTime + 1) % 17;
        markForSync();
    }

    @Override
    @Nullable
    public UUID getOwnerUUID() { return ownerUUID; }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.ownerUUID = uuid;
        markForSync();
    }

    @Override
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String name) {
        this.ownerName = name;
        markForSync();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("lastPos", NbtUtils.writeBlockPos(lastPos));
        tag.put("nextPos", NbtUtils.writeBlockPos(nextPos));
        tag.put("chestPos", NbtUtils.writeBlockPos(chestPos));
        tag.putInt("wpStayTime", wpStayTime);
        if (ownerUUID != null) {
            tag.putUUID("ownerUUID", ownerUUID);
        }
        tag.putString("ownerName", ownerName);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("lastPos")) lastPos = NbtUtils.readBlockPos(tag, "lastPos").orElse(BlockPos.ZERO);
        if (tag.contains("nextPos")) nextPos = NbtUtils.readBlockPos(tag, "nextPos").orElse(BlockPos.ZERO);
        if (tag.contains("chestPos")) chestPos = NbtUtils.readBlockPos(tag, "chestPos").orElse(BlockPos.ZERO);
        wpStayTime = tag.getInt("wpStayTime");
        if (tag.hasUUID("ownerUUID")) ownerUUID = tag.getUUID("ownerUUID");
        ownerName = tag.getString("ownerName");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WayPointBlockEntity be) {
        be.tickCount++;

        if (level.isClientSide) {
            tickClient(level, pos, be);
        } else {
            be.serverTick();
        }
    }

    private void serverTick() {
        if (this.tickCount % 64 == 0) {
            checkValidity();
        }
    }

    private void checkValidity() {
        if (this.level == null || this.level.isClientSide) return;

        if (this.nextPos != BlockPos.ZERO) {
            var be = this.level.getBlockEntity(this.nextPos);
            if (!(be instanceof IWaypoint)) {
                this.nextPos = BlockPos.ZERO;
                markForSync();
            }
        }

        if (this.chestPos != BlockPos.ZERO) {
            var be = this.level.getBlockEntity(this.chestPos);
            if (be == null || this.level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, this.chestPos, null) == null) {
                this.chestPos = BlockPos.ZERO;
                markForSync();
            }
        }
    }

    private void markForSync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), net.minecraft.world.level.block.Block.UPDATE_ALL);
        }
    }
    private static void tickClient(Level level, BlockPos pos, WayPointBlockEntity be) {
        WaypointClientHelper.tickClient(level, pos, be, be.tickCount);
    }
}
