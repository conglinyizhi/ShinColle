package org.trp.shincolle.network

import com.mojang.datafixers.util.Function6
import io.netty.buffer.ByteBuf
import net.minecraft.core.UUIDUtil
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import java.util.*

@JvmRecord
data class S2CDeskDiplomacySyncPayload(
    val ownerUuid: UUID?,
    val allies: Array<UUID?>,
    val banned: Array<UUID?>,
    val displayUuids: Array<UUID?>,
    val displayTeamNames: Array<String?>,
    val displayLeaderNames: Array<String?>
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<S2CDeskDiplomacySyncPayload> =
            CustomPacketPayload.Type<S2CDeskDiplomacySyncPayload>(
                ResourceLocation.fromNamespaceAndPath(
                    Shincolle.MODID,
                    "s2c_desk_diplomacy_sync"
                )
            )

        private val UUID_ARRAY_CODEC: StreamCodec<FriendlyByteBuf, Array<UUID?>> =
            StreamCodec.of<FriendlyByteBuf, Array<UUID?>>(
                { buffer, array ->
                    buffer.writeVarInt(array.size)
                    for (uuid in array) {
                        if (uuid != null) {
                            buffer.writeBoolean(true)
                            buffer.writeUUID(uuid)
                        } else {
                            buffer.writeBoolean(false)
                        }
                    }
                },
                { buffer ->
                    val size = buffer.readVarInt()
                    Array(size) {
                        if (buffer.readBoolean()) buffer.readUUID() else null
                    }
                }
            )

        private val STRING_ARRAY_CODEC: StreamCodec<FriendlyByteBuf, Array<String?>> =
            StreamCodec.of<FriendlyByteBuf, Array<String?>>(
                { buffer, array ->
                    buffer.writeVarInt(array.size)
                    for (s in array) {
                        if (s != null) {
                            buffer.writeBoolean(true)
                            buffer.writeUtf(s)
                        } else {
                            buffer.writeBoolean(false)
                        }
                    }
                },
                { buffer ->
                    val size = buffer.readVarInt()
                    Array(size) {
                        if (buffer.readBoolean()) buffer.readUtf() else null
                    }
                }
            )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, S2CDeskDiplomacySyncPayload> =
            StreamCodec.composite<FriendlyByteBuf, S2CDeskDiplomacySyncPayload, UUID?, Array<UUID?>, Array<UUID?>, Array<UUID?>, Array<String?>, Array<String?>>(
                UUIDUtil.STREAM_CODEC, S2CDeskDiplomacySyncPayload::ownerUuid,
                UUID_ARRAY_CODEC, S2CDeskDiplomacySyncPayload::allies,
                UUID_ARRAY_CODEC, S2CDeskDiplomacySyncPayload::banned,
                UUID_ARRAY_CODEC, S2CDeskDiplomacySyncPayload::displayUuids,
                STRING_ARRAY_CODEC, S2CDeskDiplomacySyncPayload::displayTeamNames,
                STRING_ARRAY_CODEC, S2CDeskDiplomacySyncPayload::displayLeaderNames,
                Function6 { ownerUuid, allies, banned, displayUuids, displayTeamNames, displayLeaderNames ->
                    S2CDeskDiplomacySyncPayload(
                        ownerUuid,
                        allies,
                        banned,
                        displayUuids,
                        displayTeamNames,
                        displayLeaderNames
                    )
                }
            )

        fun of(
            ownerUuid: UUID?,
            allies: MutableCollection<UUID?>,
            banned: MutableCollection<UUID?>,
            displayUuids: MutableCollection<UUID?>,
            displayTeamNames: MutableCollection<String?>,
            displayLeaderNames: MutableCollection<String?>
        ): S2CDeskDiplomacySyncPayload {
            return S2CDeskDiplomacySyncPayload(
                ownerUuid,
                allies.toTypedArray(),
                banned.toTypedArray(),
                displayUuids.toTypedArray(),
                displayTeamNames.toTypedArray(),
                displayLeaderNames.toTypedArray()
            )
        }
    }
}
