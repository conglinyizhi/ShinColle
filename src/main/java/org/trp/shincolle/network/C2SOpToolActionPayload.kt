package org.trp.shincolle.network

import java.util.Optional
import java.util.UUID
import java.util.function.BiFunction
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle

@JvmRecord
data class C2SOpToolActionPayload(
    val action: Int,
    val targetEntity: Optional<UUID>
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
        const val ACTION_TOGGLE_UNATTACKABLE_TARGET: Int = 0
        const val ACTION_SHOW_UNATTACKABLE_TARGETS: Int = 1

        val TYPE: CustomPacketPayload.Type<C2SOpToolActionPayload?> =
            CustomPacketPayload.Type<C2SOpToolActionPayload?>(
                ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "c2s_optool_action")
            )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, C2SOpToolActionPayload?> =
            StreamCodec.composite<FriendlyByteBuf?, C2SOpToolActionPayload?, Int?, Optional<UUID>?>(
                ByteBufCodecs.VAR_INT,
                C2SOpToolActionPayload::action,
                ByteBufCodecs.optional<FriendlyByteBuf, UUID>(UUIDUtil.STREAM_CODEC),
                C2SOpToolActionPayload::targetEntity,
                BiFunction { action: Int?, targetEntity: Optional<UUID> ->
                    C2SOpToolActionPayload(action!!, targetEntity)
                }
            )
    }
}
