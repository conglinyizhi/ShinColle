package org.trp.shincolle.network

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import java.util.function.BiFunction

@JvmRecord
data class C2SBookStatePayload(val chapter: Int, val page: Int) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<C2SBookStatePayload?> = CustomPacketPayload.Type<C2SBookStatePayload?>(
            ResourceLocation.fromNamespaceAndPath(
                Shincolle.MODID,
                "c2s_book_state"
            )
        )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, C2SBookStatePayload?> =
            StreamCodec.composite<FriendlyByteBuf?, C2SBookStatePayload?, Int?, Int?>(
                ByteBufCodecs.VAR_INT, C2SBookStatePayload::chapter,
                ByteBufCodecs.VAR_INT, C2SBookStatePayload::page,
                BiFunction { chapter: Int?, page: Int? -> C2SBookStatePayload(chapter!!, page!!) }
            )
    }
}
