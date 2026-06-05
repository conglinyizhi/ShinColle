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
import java.util.function.Function
import java.util.function.IntFunction

@JvmRecord
data class S2CDeskDiplomacySyncPayload(
    val ownerUuid: UUID?,
    val allies: Array<UUID?>?,
    val banned: Array<UUID?>?,
    val displayUuids: Array<UUID?>?,
    val displayTeamNames: Array<String?>?,
    val displayLeaderNames: Array<String?>?
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<S2CDeskDiplomacySyncPayload?> =
            CustomPacketPayload.Type<S2CDeskDiplomacySyncPayload?>(
                ResourceLocation.fromNamespaceAndPath(
                    Shincolle.MODID,
                    "s2c_desk_diplomacy_sync"
                )
            )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, S2CDeskDiplomacySyncPayload?> =
            StreamCodec.composite<FriendlyByteBuf?, S2CDeskDiplomacySyncPayload?, UUID?, Array<UUID?>?, Array<UUID?>?, Array<UUID?>?, Array<String?>?, Array<String?>?>(
                UUIDUtil.STREAM_CODEC, S2CDeskDiplomacySyncPayload::ownerUuid,
                UUIDUtil.STREAM_CODEC.apply<MutableList<UUID?>?>(ByteBufCodecs.list<ByteBuf?, UUID?>())
                    .map<Array<UUID?>?>(
                        Function { list: MutableList<UUID?>? -> list.toArray<UUID?>(IntFunction { _Dummy_.__Array__() }) },
                        Function { a: Array<UUID?>? -> Arrays.asList(a) }),
                S2CDeskDiplomacySyncPayload::allies,
                UUIDUtil.STREAM_CODEC.apply<MutableList<UUID?>?>(ByteBufCodecs.list<ByteBuf?, UUID?>())
                    .map<Array<UUID?>?>(
                        Function { list: MutableList<UUID?>? -> list.toArray<UUID?>(IntFunction { _Dummy_.__Array__() }) },
                        Function { a: Array<UUID?>? -> Arrays.asList(a) }),
                S2CDeskDiplomacySyncPayload::banned,
                UUIDUtil.STREAM_CODEC.apply<MutableList<UUID?>?>(ByteBufCodecs.list<ByteBuf?, UUID?>())
                    .map<Array<UUID?>?>(
                        Function { list: MutableList<UUID?>? -> list.toArray<UUID?>(IntFunction { _Dummy_.__Array__() }) },
                        Function { a: Array<UUID?>? -> Arrays.asList(a) }),
                S2CDeskDiplomacySyncPayload::displayUuids,
                ByteBufCodecs.STRING_UTF8.apply<MutableList<String?>?>(ByteBufCodecs.list<ByteBuf?, String?>())
                    .map<Array<String?>?>(
                        Function { list: MutableList<String?>? -> list.toArray<String?>(IntFunction { _Dummy_.__Array__() }) },
                        Function { a: Array<String?>? -> Arrays.asList(a) }),
                S2CDeskDiplomacySyncPayload::displayTeamNames,
                ByteBufCodecs.STRING_UTF8.apply<MutableList<String?>?>(ByteBufCodecs.list<ByteBuf?, String?>())
                    .map<Array<String?>?>(
                        Function { list: MutableList<String?>? -> list.toArray<String?>(IntFunction { _Dummy_.__Array__() }) },
                        Function { a: Array<String?>? -> Arrays.asList(a) }),
                S2CDeskDiplomacySyncPayload::displayLeaderNames,
                Function6 { ownerUuid: UUID?, allies: Array<UUID?>?, banned: Array<UUID?>?, displayUuids: Array<UUID?>?, displayTeamNames: Array<String?>?, displayLeaderNames: Array<String?>? ->
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
                allies.toArray<UUID?>(IntFunction { _Dummy_.__Array__() }),
                banned.toArray<UUID?>(IntFunction { _Dummy_.__Array__() }),
                displayUuids.toArray<UUID?>(IntFunction { _Dummy_.__Array__() }),
                displayTeamNames.toArray<String?>(IntFunction { _Dummy_.__Array__() }),
                displayLeaderNames.toArray<String?>(IntFunction { _Dummy_.__Array__() })
            )
        }
    }
}
