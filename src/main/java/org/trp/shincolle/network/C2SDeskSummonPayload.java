package org.trp.shincolle.network;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.Shincolle;

import java.util.List;
import java.util.UUID;

public record C2SDeskSummonPayload(List<UUID> shipUuids) implements CustomPacketPayload {
    public static final Type<C2SDeskSummonPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "c2s_desk_summon"));

    public static final StreamCodec<FriendlyByteBuf, C2SDeskSummonPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()),
            C2SDeskSummonPayload::shipUuids,
            C2SDeskSummonPayload::new
    );

    public C2SDeskSummonPayload {
        shipUuids = List.copyOf(shipUuids);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
