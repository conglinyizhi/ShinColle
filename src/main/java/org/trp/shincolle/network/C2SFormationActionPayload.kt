package org.trp.shincolle.network

import com.mojang.datafixers.util.Function5
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import java.util.*

@JvmRecord
data class C2SFormationActionPayload(
    val action: Int,
    val param1: Int,
    val param2: Int,
    val paramString: String?,
    val paramUUID: Optional<UUID?>
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    private object UUIDUtil {
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, UUID> = object : StreamCodec<FriendlyByteBuf, UUID> {
            override fun decode(buffer: FriendlyByteBuf): UUID {
                return buffer.readUUID()
            }

            override fun encode(buffer: FriendlyByteBuf, value: UUID) {
                buffer.writeUUID(value)
            }
        }
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<C2SFormationActionPayload?> =
            CustomPacketPayload.Type<C2SFormationActionPayload?>(
                ResourceLocation.fromNamespaceAndPath(
                    Shincolle.MODID,
                    "c2s_formation_action"
                )
            )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, C2SFormationActionPayload?> =
            StreamCodec.composite<FriendlyByteBuf?, C2SFormationActionPayload?, Int?, Int?, Int?, String?, Optional<UUID?>?>(
                ByteBufCodecs.VAR_INT,
                C2SFormationActionPayload::action,
                ByteBufCodecs.VAR_INT,
                C2SFormationActionPayload::param1,
                ByteBufCodecs.VAR_INT,
                C2SFormationActionPayload::param2,
                ByteBufCodecs.STRING_UTF8,
                C2SFormationActionPayload::paramString,
                ByteBufCodecs.optional<FriendlyByteBuf, UUID>(UUIDUtil.STREAM_CODEC),
                C2SFormationActionPayload::paramUUID,
                Function5 { action: Int?, param1: Int?, param2: Int?, paramString: String?, paramUUID: Optional<UUID?> ->
                    C2SFormationActionPayload(
                        action!!,
                        param1!!,
                        param2!!,
                        paramString,
                        paramUUID
                    )
                }
            )
    }
}
