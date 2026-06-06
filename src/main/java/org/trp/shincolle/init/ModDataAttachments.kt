package org.trp.shincolle.init

import com.mojang.serialization.Codec
import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import org.trp.shincolle.Shincolle
import org.trp.shincolle.attachment.AdmiralData
import java.util.function.Function
import java.util.function.Supplier

object ModDataAttachments {
    val ATTACHMENT_TYPES: DeferredRegister<AttachmentType<*>?> =
        DeferredRegister.create<AttachmentType<*>?>(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Shincolle.MODID)

    private val SET_CODEC: Codec<HashSet<Int?>?> = Codec.INT.listOf().xmap<HashSet<Int?>?>(
        Function { c: MutableList<Int?>? -> HashSet(c) },
        Function { c: HashSet<Int?>? -> ArrayList(c) })
    private val ADMIRAL_CODEC: Codec<AdmiralData?> = CompoundTag.CODEC.xmap<AdmiralData?>(
        Function { tag: CompoundTag? ->
            val data = AdmiralData()
            data.deserializeNBT(tag!!)
            data
        },
        Function { obj: AdmiralData? -> obj!!.serializeNBT() }
    )

    val COLLECTED_SHIPS: DeferredHolder<AttachmentType<*>?, AttachmentType<HashSet<Int?>?>?> =
        ATTACHMENT_TYPES.register<AttachmentType<HashSet<Int?>?>?>("collected_ships", Supplier {
            AttachmentType.builder<HashSet<Int?>?>(
                Supplier { HashSet<Int?>() })
                .serialize(SET_CODEC)
                .copyOnDeath()
                .build()
        })

    val ADMIRAL_DATA: DeferredHolder<AttachmentType<*>?, AttachmentType<AdmiralData?>?> =
        ATTACHMENT_TYPES.register<AttachmentType<AdmiralData?>?>("admiral_data", Supplier {
            AttachmentType.builder<AdmiralData?>(
                Supplier { AdmiralData() })
                .serialize(ADMIRAL_CODEC)
                .copyOnDeath()
                .build()
        })
}
