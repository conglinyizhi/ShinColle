package org.trp.shincolle.network

import com.mojang.datafixers.util.Function3
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.Shincolle
import java.util.*

@JvmRecord
data class C2SPointerActionPayload(
    val action: Int,
    val targetEntity: Optional<UUID?>,
    val targetPos: Optional<Vec3?>
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

    private object Vec3Util {
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, Vec3> = object : StreamCodec<FriendlyByteBuf, Vec3> {
            override fun decode(buffer: FriendlyByteBuf): Vec3 {
                return Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
            }

            override fun encode(buffer: FriendlyByteBuf, value: Vec3) {
                buffer.writeDouble(value.x)
                buffer.writeDouble(value.y)
                buffer.writeDouble(value.z)
            }
        }
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<C2SPointerActionPayload?> =
            CustomPacketPayload.Type<C2SPointerActionPayload?>(
                ResourceLocation.fromNamespaceAndPath(
                    Shincolle.MODID,
                    "c2s_pointer_action"
                )
            )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, C2SPointerActionPayload?> =
            StreamCodec.composite<FriendlyByteBuf?, C2SPointerActionPayload?, Int?, Optional<UUID?>?, Optional<Vec3?>?>(
                ByteBufCodecs.VAR_INT,
                C2SPointerActionPayload::action,
                ByteBufCodecs.optional<FriendlyByteBuf, UUID>(UUIDUtil.STREAM_CODEC),
                C2SPointerActionPayload::targetEntity,
                ByteBufCodecs.optional<FriendlyByteBuf, Vec3>(Vec3Util.STREAM_CODEC),
                C2SPointerActionPayload::targetPos,
                Function3 { action: Int?, targetEntity: Optional<UUID?>, targetPos: Optional<Vec3?> ->
                    C2SPointerActionPayload(
                        action!!,
                        targetEntity,
                        targetPos
                    )
                }
            )
    }
}
