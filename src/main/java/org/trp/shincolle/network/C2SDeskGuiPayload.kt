package org.trp.shincolle.network

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import java.util.function.BiFunction

@JvmRecord
data class C2SDeskGuiPayload(val guiFunc: Int, val radarZoom: Int) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<C2SDeskGuiPayload?> = CustomPacketPayload.Type<C2SDeskGuiPayload?>(
            ResourceLocation.fromNamespaceAndPath(
                Shincolle.MODID,
                "c2s_desk_gui"
            )
        )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, C2SDeskGuiPayload?> =
            StreamCodec.composite<FriendlyByteBuf?, C2SDeskGuiPayload?, Int?, Int?>(
                ByteBufCodecs.VAR_INT, C2SDeskGuiPayload::guiFunc,
                ByteBufCodecs.VAR_INT, C2SDeskGuiPayload::radarZoom,
                BiFunction { guiFunc: Int?, radarZoom: Int? -> C2SDeskGuiPayload(guiFunc!!, radarZoom!!) }
            )
    }
}
