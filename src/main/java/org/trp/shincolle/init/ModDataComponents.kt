package org.trp.shincolle.init

import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.util.ExtraCodecs
import net.neoforged.neoforge.fluids.SimpleFluidContent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.trp.shincolle.Shincolle
import java.util.function.UnaryOperator

object ModDataComponents {
    val DATA_COMPONENTS: DeferredRegister.DataComponents =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Shincolle.MODID)

    @JvmField
    val SHIPTANK_FLUID: DeferredHolder<DataComponentType<*>?, DataComponentType<SimpleFluidContent?>?> =
        DATA_COMPONENTS.registerComponentType<SimpleFluidContent?>(
            "shiptank_fluid",
            UnaryOperator { builder: DataComponentType.Builder<SimpleFluidContent?>? ->
                builder!!
                    .persistent(SimpleFluidContent.CODEC)
                    .networkSynchronized(SimpleFluidContent.STREAM_CODEC)
            })

    val BOOK_PAGE: DeferredHolder<DataComponentType<*>?, DataComponentType<Int?>?> =
        DATA_COMPONENTS.registerComponentType<Int?>(
            "book_page",
            UnaryOperator { builder: DataComponentType.Builder<Int?>? ->
                builder!!
                    .persistent(ExtraCodecs.NON_NEGATIVE_INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
            })

    val BOOK_CHAPTER: DeferredHolder<DataComponentType<*>?, DataComponentType<Int?>?> =
        DATA_COMPONENTS.registerComponentType<Int?>(
            "book_chapter",
            UnaryOperator { builder: DataComponentType.Builder<Int?>? ->
                builder!!
                    .persistent(ExtraCodecs.NON_NEGATIVE_INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
            })
}
