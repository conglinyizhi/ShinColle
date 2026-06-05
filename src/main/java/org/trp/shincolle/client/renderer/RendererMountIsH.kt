package org.trp.shincolle.client.renderer

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelMountIsH
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer

class RendererMountIsH<T : Mob?>(context: EntityRendererProvider.Context) : RendererSimpleMob<T?, ModelMountIsH<T?>?>(
    context,
    ModelMountIsH<T?>(context.bakeLayer(ModelMountIsH.LAYER_LOCATION)),
    0.7f,
    0.7f,
    TEXTURE
) {
    init {
        this.addLayer(GenericGlowLayer<T?, ModelMountIsH<T?>?>(this, TEXTURE))
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/mount_is_h.png")
    }
}
