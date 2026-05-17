package org.trp.shincolle.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.Shincolle;

public record C2SDeskGuiPayload(int guiFunc, int radarZoom) implements CustomPacketPayload {
    public static final Type<C2SDeskGuiPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "c2s_desk_gui"));

    public static final StreamCodec<FriendlyByteBuf, C2SDeskGuiPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, C2SDeskGuiPayload::guiFunc,
            ByteBufCodecs.VAR_INT, C2SDeskGuiPayload::radarZoom,
            C2SDeskGuiPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
