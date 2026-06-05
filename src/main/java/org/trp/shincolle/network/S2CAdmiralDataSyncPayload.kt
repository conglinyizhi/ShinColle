package org.trp.shincolle.network

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function

@JvmRecord
data class S2CAdmiralDataSyncPayload(val admiralNbt: CompoundTag?, val collectedShips: IntArray?) :
    CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<S2CAdmiralDataSyncPayload?> =
            CustomPacketPayload.Type<S2CAdmiralDataSyncPayload?>(
                ResourceLocation.fromNamespaceAndPath(
                    Shincolle.MODID,
                    "s2c_admiral_data_sync"
                )
            )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, S2CAdmiralDataSyncPayload?> =
            StreamCodec.composite<FriendlyByteBuf?, S2CAdmiralDataSyncPayload?, CompoundTag?, IntArray?>(
                ByteBufCodecs.COMPOUND_TAG,
                S2CAdmiralDataSyncPayload::admiralNbt,
                ByteBufCodecs.VAR_INT.apply<MutableList<Int?>?>(ByteBufCodecs.list<ByteBuf?, Int?>())
                    .map<IntArray?>(Function { list: MutableList<Int?>? ->
                        list!!.stream().mapToInt { obj: Int? -> obj!!.toInt() }.toArray()
                    }, Function { ints: IntArray? -> Arrays.stream(ints).boxed().toList() }),
                S2CAdmiralDataSyncPayload::collectedShips,
                BiFunction { admiralNbt: CompoundTag?, collectedShips: IntArray? ->
                    S2CAdmiralDataSyncPayload(
                        admiralNbt,
                        collectedShips
                    )
                }
            )

        fun of(admiralNbt: CompoundTag?, collectedShips: HashSet<Int?>): S2CAdmiralDataSyncPayload {
            val values = collectedShips.stream().mapToInt { obj: Int? -> obj!!.toInt() }.toArray()
            return S2CAdmiralDataSyncPayload(admiralNbt, values)
        }
    }
}
