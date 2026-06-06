package org.trp.shincolle.client.renderer

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelMountCaH
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer

class RendererMountCaH<T : Mob?>(context: EntityRendererProvider.Context) : RendererSimpleMob<T?, ModelMountCaH<T?>?>(
    context,
    ModelMountCaH<T>(context.bakeLayer(ModelMountCaH.LAYER_LOCATION)),
    1.1f,
    1.1f,
    TEXTURE
) {
    init {
        this.addLayer(GenericGlowLayer<T?, ModelMountCaH<T?>?>(this, TEXTURE))
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/mount_ca_h.png")
    }
}
