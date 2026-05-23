package org.trp.shincolle.network;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.Shincolle;

import java.util.UUID;

public record C2STeamDiplomacyPayload(int action, UUID targetUuid) implements CustomPacketPayload {
    public static final int ACTION_ADD_ALLY = 0;
    public static final int ACTION_REMOVE_ALLY = 1;
    public static final int ACTION_ADD_BANNED = 2;
    public static final int ACTION_REMOVE_BANNED = 3;

    public static final Type<C2STeamDiplomacyPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "c2s_team_diplomacy"));

    public static final StreamCodec<FriendlyByteBuf, C2STeamDiplomacyPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, C2STeamDiplomacyPayload::action,
            UUIDUtil.STREAM_CODEC, C2STeamDiplomacyPayload::targetUuid,
            C2STeamDiplomacyPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
