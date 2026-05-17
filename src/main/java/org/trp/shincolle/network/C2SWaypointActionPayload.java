package org.trp.shincolle.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.Shincolle;

public record C2SWaypointActionPayload(
        int action,
        int x1, int y1, int z1,
        int x2, int y2, int z2
) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "c2s_waypoint_action");
    public static final CustomPacketPayload.Type<C2SWaypointActionPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, C2SWaypointActionPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeInt(payload.action());
                buf.writeInt(payload.x1());
                buf.writeInt(payload.y1());
                buf.writeInt(payload.z1());
                buf.writeInt(payload.x2());
                buf.writeInt(payload.y2());
                buf.writeInt(payload.z2());
            },
            buf -> new C2SWaypointActionPayload(
                    buf.readInt(),
                    buf.readInt(), buf.readInt(), buf.readInt(),
                    buf.readInt(), buf.readInt(), buf.readInt()
            )
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
