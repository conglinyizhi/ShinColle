package org.trp.shincolle.network

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.codec.StreamDecoder
import net.minecraft.network.codec.StreamEncoder
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle

@JvmRecord
data class C2SWaypointActionPayload(
    val action: Int,
    val x1: Int, val y1: Int, val z1: Int,
    val x2: Int, val y2: Int, val z2: Int
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {
        val ID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "c2s_waypoint_action")
        val TYPE: CustomPacketPayload.Type<C2SWaypointActionPayload?> =
            CustomPacketPayload.Type<C2SWaypointActionPayload?>(
                ID
            )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, C2SWaypointActionPayload?> =
            StreamCodec.of<FriendlyByteBuf?, C2SWaypointActionPayload?>(
                StreamEncoder { buf: FriendlyByteBuf?, payload: C2SWaypointActionPayload? ->
                    buf!!.writeInt(payload!!.action)
                    buf.writeInt(payload.x1)
                    buf.writeInt(payload.y1)
                    buf.writeInt(payload.z1)
                    buf.writeInt(payload.x2)
                    buf.writeInt(payload.y2)
                    buf.writeInt(payload.z2)
                },
                StreamDecoder { buf: FriendlyByteBuf? ->
                    C2SWaypointActionPayload(
                        buf!!.readInt(),
                        buf.readInt(), buf.readInt(), buf.readInt(),
                        buf.readInt(), buf.readInt(), buf.readInt()
                    )
                }
            )
    }
}
