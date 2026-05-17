package org.trp.shincolle.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.trp.shincolle.Shincolle;

import java.util.Optional;
import java.util.UUID;

public record C2SPointerActionPayload(int action, Optional<UUID> targetEntity, Optional<Vec3> targetPos) implements CustomPacketPayload {
    public static final Type<C2SPointerActionPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "c2s_pointer_action"));

    public static final StreamCodec<FriendlyByteBuf, C2SPointerActionPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, C2SPointerActionPayload::action,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), C2SPointerActionPayload::targetEntity,
            ByteBufCodecs.optional(Vec3Util.STREAM_CODEC), C2SPointerActionPayload::targetPos,
            C2SPointerActionPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    
    private static class UUIDUtil {
        public static final StreamCodec<FriendlyByteBuf, UUID> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public UUID decode(FriendlyByteBuf buffer) {
                return buffer.readUUID();
            }

            @Override
            public void encode(FriendlyByteBuf buffer, UUID value) {
                buffer.writeUUID(value);
            }
        };
    }

    private static class Vec3Util {
        public static final StreamCodec<FriendlyByteBuf, Vec3> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public Vec3 decode(FriendlyByteBuf buffer) {
                return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            }

            @Override
            public void encode(FriendlyByteBuf buffer, Vec3 value) {
                buffer.writeDouble(value.x);
                buffer.writeDouble(value.y);
                buffer.writeDouble(value.z);
            }
        };
    }
}
