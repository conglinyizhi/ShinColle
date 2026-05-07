package org.trp.shincolle.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.Shincolle;

public record C2SBookStatePayload(int chapter, int page) implements CustomPacketPayload {
    public static final Type<C2SBookStatePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "c2s_book_state"));

    public static final StreamCodec<FriendlyByteBuf, C2SBookStatePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, C2SBookStatePayload::chapter,
            ByteBufCodecs.VAR_INT, C2SBookStatePayload::page,
            C2SBookStatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
