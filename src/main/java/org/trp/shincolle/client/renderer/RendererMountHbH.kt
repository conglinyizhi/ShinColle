package org.trp.shincolle.client.renderer

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelMountHbH
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer

class RendererMountHbH<T : Mob?>(context: EntityRendererProvider.Context) : RendererSimpleMob<T?, ModelMountHbH<T?>?>(
    context,
    ModelMountHbH<T?>(context.bakeLayer(ModelMountHbH.LAYER_LOCATION)),
    0.8f,
    0.8f,
    TEXTURE
) {
    init {
        this.addLayer(GenericGlowLayer<T?, ModelMountHbH<T?>?>(this, TEXTURE))
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/mount_hb_h.png")
    }
}
