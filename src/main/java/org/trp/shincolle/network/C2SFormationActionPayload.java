package org.trp.shincolle.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.Shincolle;

import java.util.Optional;
import java.util.UUID;

public record C2SFormationActionPayload(int action, int param1, int param2, String paramString, Optional<UUID> paramUUID) implements CustomPacketPayload {
    public static final Type<C2SFormationActionPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "c2s_formation_action"));

    public static final StreamCodec<FriendlyByteBuf, C2SFormationActionPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, C2SFormationActionPayload::action,
            ByteBufCodecs.VAR_INT, C2SFormationActionPayload::param1,
            ByteBufCodecs.VAR_INT, C2SFormationActionPayload::param2,
            ByteBufCodecs.STRING_UTF8, C2SFormationActionPayload::paramString,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), C2SFormationActionPayload::paramUUID,
            C2SFormationActionPayload::new
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
}
