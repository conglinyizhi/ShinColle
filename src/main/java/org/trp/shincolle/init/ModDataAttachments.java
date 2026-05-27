package org.trp.shincolle.init;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.attachment.AdmiralData;

import java.util.ArrayList;
import java.util.HashSet;

public final class ModDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Shincolle.MODID);

    private static final Codec<HashSet<Integer>> SET_CODEC = Codec.INT.listOf().xmap(HashSet::new, ArrayList::new);
    private static final Codec<AdmiralData> ADMIRAL_CODEC = CompoundTag.CODEC.xmap(
            tag -> {
                AdmiralData data = new AdmiralData();
                data.deserializeNBT(tag);
                return data;
            },
            AdmiralData::serializeNBT
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HashSet<Integer>>> COLLECTED_SHIPS =
            ATTACHMENT_TYPES.register("collected_ships", () -> AttachmentType.builder(() -> new HashSet<Integer>())
                    .serialize(SET_CODEC)
                    .copyOnDeath()
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AdmiralData>> ADMIRAL_DATA =
            ATTACHMENT_TYPES.register("admiral_data", () -> AttachmentType.builder(() -> new AdmiralData())
                    .serialize(ADMIRAL_CODEC)
                    .copyOnDeath()
                    .build());

    private ModDataAttachments() {
    }
}
