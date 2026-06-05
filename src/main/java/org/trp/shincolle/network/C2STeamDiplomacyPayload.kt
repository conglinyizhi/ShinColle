package org.trp.shincolle.network

import net.minecraft.core.UUIDUtil
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import java.util.*
import java.util.function.BiFunction

@JvmRecord
data class C2STeamDiplomacyPayload(val action: Int, val targetUuid: UUID?) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {
        const val ACTION_ADD_ALLY: Int = 0
        const val ACTION_REMOVE_ALLY: Int = 1
        const val ACTION_ADD_BANNED: Int = 2
        const val ACTION_REMOVE_BANNED: Int = 3

        val TYPE: CustomPacketPayload.Type<C2STeamDiplomacyPayload?> =
            CustomPacketPayload.Type<C2STeamDiplomacyPayload?>(
                ResourceLocation.fromNamespaceAndPath(
                    Shincolle.MODID,
                    "c2s_team_diplomacy"
                )
            )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, C2STeamDiplomacyPayload?> =
            StreamCodec.composite<FriendlyByteBuf?, C2STeamDiplomacyPayload?, Int?, UUID?>(
                ByteBufCodecs.VAR_INT, C2STeamDiplomacyPayload::action,
                UUIDUtil.STREAM_CODEC, C2STeamDiplomacyPayload::targetUuid,
                BiFunction { action: Int?, targetUuid: UUID? -> C2STeamDiplomacyPayload(action!!, targetUuid) }
            )
    }
}
