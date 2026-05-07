package org.trp.shincolle.init;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.trp.shincolle.Shincolle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ModDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Shincolle.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Set<Integer>>> COLLECTED_SHIPS =
            ATTACHMENT_TYPES.register("collected_ships", () -> AttachmentType.builder(() -> (Set<Integer>) new HashSet<Integer>())
                    .serialize(Codec.INT.listOf().xmap(HashSet::new, ArrayList::new))
                    .copyOnDeath()
                    .build());

    private ModDataAttachments() {
    }
}
