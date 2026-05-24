package org.trp.shincolle.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.Shincolle;

import java.util.UUID;

public record C2SDeskOpenShipPayload(UUID shipUuid) implements CustomPacketPayload {
    public static final Type<C2SDeskOpenShipPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "c2s_desk_open_ship"));

    public static final StreamCodec<FriendlyByteBuf, C2SDeskOpenShipPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeUUID(payload.shipUuid()),
            buf -> new C2SDeskOpenShipPayload(buf.readUUID())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
