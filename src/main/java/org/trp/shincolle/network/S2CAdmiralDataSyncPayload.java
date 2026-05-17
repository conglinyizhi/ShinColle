package org.trp.shincolle.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.Shincolle;

public record S2CAdmiralDataSyncPayload(CompoundTag nbt) implements CustomPacketPayload {
    public static final Type<S2CAdmiralDataSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "s2c_admiral_data_sync"));

    public static final StreamCodec<FriendlyByteBuf, S2CAdmiralDataSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, S2CAdmiralDataSyncPayload::nbt,
            S2CAdmiralDataSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
