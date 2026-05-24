package org.trp.shincolle.network;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.Shincolle;

import java.util.Collection;
import java.util.UUID;

public record S2CDeskDiplomacySyncPayload(
        UUID ownerUuid,
        UUID[] allies,
        UUID[] banned,
        UUID[] displayUuids,
        String[] displayTeamNames,
        String[] displayLeaderNames
) implements CustomPacketPayload {
    public static final Type<S2CDeskDiplomacySyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "s2c_desk_diplomacy_sync"));

    public static final StreamCodec<FriendlyByteBuf, S2CDeskDiplomacySyncPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, S2CDeskDiplomacySyncPayload::ownerUuid,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()).map(list -> list.toArray(UUID[]::new), java.util.Arrays::asList),
            S2CDeskDiplomacySyncPayload::allies,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()).map(list -> list.toArray(UUID[]::new), java.util.Arrays::asList),
            S2CDeskDiplomacySyncPayload::banned,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()).map(list -> list.toArray(UUID[]::new), java.util.Arrays::asList),
            S2CDeskDiplomacySyncPayload::displayUuids,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).map(list -> list.toArray(String[]::new), java.util.Arrays::asList),
            S2CDeskDiplomacySyncPayload::displayTeamNames,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).map(list -> list.toArray(String[]::new), java.util.Arrays::asList),
            S2CDeskDiplomacySyncPayload::displayLeaderNames,
            S2CDeskDiplomacySyncPayload::new
    );

    public static S2CDeskDiplomacySyncPayload of(
            UUID ownerUuid,
            Collection<UUID> allies,
            Collection<UUID> banned,
            Collection<UUID> displayUuids,
            Collection<String> displayTeamNames,
            Collection<String> displayLeaderNames
    ) {
        return new S2CDeskDiplomacySyncPayload(
                ownerUuid,
                allies.toArray(UUID[]::new),
                banned.toArray(UUID[]::new),
                displayUuids.toArray(UUID[]::new),
                displayTeamNames.toArray(String[]::new),
                displayLeaderNames.toArray(String[]::new)
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
