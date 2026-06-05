package org.trp.shincolle.client.renderer

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelMountMiH
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer

class RendererMountMiH<T : Mob?>(context: EntityRendererProvider.Context) : RendererSimpleMob<T?, ModelMountMiH<T?>?>(
    context,
    ModelMountMiH<T?>(context.bakeLayer(ModelMountMiH.LAYER_LOCATION)),
    0.97f,
    0.97f,
    TEXTURE
) {
    init {
        this.addLayer(GenericGlowLayer<T?, ModelMountMiH<T?>?>(this, TEXTURE))
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/mount_mi_h.png")
    }
}
