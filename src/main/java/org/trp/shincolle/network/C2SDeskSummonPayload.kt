package org.trp.shincolle.network

import io.netty.buffer.ByteBuf
import net.minecraft.core.UUIDUtil
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import java.util.*
import java.util.List
import java.util.function.Function

class C2SDeskSummonPayload(shipUuids: MutableList<UUID?>?) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    val shipUuids: MutableList<UUID?>?

    init {
        var shipUuids = shipUuids
        shipUuids = List.copyOf<UUID?>(shipUuids)
        this.shipUuids = shipUuids
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<C2SDeskSummonPayload?> = CustomPacketPayload.Type<C2SDeskSummonPayload?>(
            ResourceLocation.fromNamespaceAndPath(
                Shincolle.MODID,
                "c2s_desk_summon"
            )
        )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, C2SDeskSummonPayload?> =
            StreamCodec.composite<FriendlyByteBuf?, C2SDeskSummonPayload?, MutableList<UUID?>?>(
                UUIDUtil.STREAM_CODEC.apply<MutableList<UUID?>?>(ByteBufCodecs.list<ByteBuf?, UUID?>()),
                C2SDeskSummonPayload::shipUuids,
                Function { shipUuids: MutableList<UUID?>? -> C2SDeskSummonPayload(shipUuids) }
            )
    }
}
