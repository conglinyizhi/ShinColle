package org.trp.shincolle.client.renderer

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelMountAfH
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer

class RendererMountAfH<T : Mob?>(context: EntityRendererProvider.Context) : RendererSimpleMob<T?, ModelMountAfH<T?>?>(
    context,
    ModelMountAfH<T?>(context.bakeLayer(ModelMountAfH.LAYER_LOCATION)),
    0.7f,
    0.7f,
    TEXTURE
) {
    init {
        this.addLayer(GenericGlowLayer<T?, ModelMountAfH<T?>?>(this, TEXTURE))
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/mount_af_h.png")
    }
}
