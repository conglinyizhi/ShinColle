package org.trp.shincolle.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.Shincolle;

import java.util.HashSet;

public record S2CAdmiralDataSyncPayload(CompoundTag admiralNbt, int[] collectedShips) implements CustomPacketPayload {
    public static final Type<S2CAdmiralDataSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "s2c_admiral_data_sync"));

    public static final StreamCodec<FriendlyByteBuf, S2CAdmiralDataSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, S2CAdmiralDataSyncPayload::admiralNbt,
            ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()).map(list -> list.stream().mapToInt(Integer::intValue).toArray(), ints -> java.util.Arrays.stream(ints).boxed().toList()), S2CAdmiralDataSyncPayload::collectedShips,
            S2CAdmiralDataSyncPayload::new
    );

    public static S2CAdmiralDataSyncPayload of(CompoundTag admiralNbt, HashSet<Integer> collectedShips) {
        int[] values = collectedShips.stream().mapToInt(Integer::intValue).toArray();
        return new S2CAdmiralDataSyncPayload(admiralNbt, values);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
