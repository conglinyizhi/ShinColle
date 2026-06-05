package org.trp.shincolle.network

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.codec.StreamDecoder
import net.minecraft.network.codec.StreamEncoder
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import java.util.*

@JvmRecord
data class C2SDeskOpenShipPayload(val shipUuid: UUID?) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<C2SDeskOpenShipPayload?> = CustomPacketPayload.Type<C2SDeskOpenShipPayload?>(
            ResourceLocation.fromNamespaceAndPath(
                Shincolle.MODID,
                "c2s_desk_open_ship"
            )
        )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, C2SDeskOpenShipPayload?> =
            StreamCodec.of<FriendlyByteBuf?, C2SDeskOpenShipPayload?>(
                StreamEncoder { buf: FriendlyByteBuf?, payload: C2SDeskOpenShipPayload? -> buf!!.writeUUID(payload!!.shipUuid) },
                StreamDecoder { buf: FriendlyByteBuf? -> C2SDeskOpenShipPayload(buf!!.readUUID()) }
            )
    }
}
