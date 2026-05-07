package org.trp.shincolle.init;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.trp.shincolle.Shincolle;

public final class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Shincolle.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> SHIPTANK_FLUID =
            DATA_COMPONENTS.registerComponentType("shiptank_fluid", builder -> builder
                    .persistent(SimpleFluidContent.CODEC)
                    .networkSynchronized(SimpleFluidContent.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BOOK_PAGE =
            DATA_COMPONENTS.registerComponentType("book_page", builder -> builder
                    .persistent(net.minecraft.util.ExtraCodecs.NON_NEGATIVE_INT)
                    .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BOOK_CHAPTER =
            DATA_COMPONENTS.registerComponentType("book_chapter", builder -> builder
                    .persistent(net.minecraft.util.ExtraCodecs.NON_NEGATIVE_INT)
                    .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.VAR_INT));

    private ModDataComponents() {
    }
}
