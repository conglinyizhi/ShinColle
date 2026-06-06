package org.trp.shincolle.client.renderer

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelMountSuH
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer

class RendererMountSuH<T : Mob>(context: EntityRendererProvider.Context) : RendererSimpleMob<T, ModelMountSuH<T>>(
    context,
    ModelMountSuH<T>(context.bakeLayer(ModelMountSuH.LAYER_LOCATION)),
    0.6f,
    0.6f,
    TEXTURE
) {
    init {
        this.addLayer(GenericGlowLayer<T, ModelMountSuH<T>>(this, TEXTURE))
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/mount_su_h.png")
    }
}
