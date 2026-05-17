package org.trp.shincolle.block.entity;

import net.minecraft.core.BlockPos;

import java.util.UUID;

public interface IWaypoint {
    BlockPos getLastPos();
    void setLastPos(BlockPos pos);
    BlockPos getNextPos();
    void setNextPos(BlockPos pos);
    BlockPos getChestPos();
    void setChestPos(BlockPos pos);
    UUID getOwnerUUID();
    default String getStayTimeDisplay() { return ""; }
    default String getOwnerName() { return ""; }
    default boolean showBaseParticle() { return true; }
}
