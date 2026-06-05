package org.trp.shincolle.client.renderer

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelMountCaWD
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer

class RendererMountCaWD<T : Mob?>(context: EntityRendererProvider.Context) : RendererSimpleMob<T?, ModelMountCaWD<T?>?>(
    context,
    ModelMountCaWD<T?>(context.bakeLayer(ModelMountCaWD.LAYER_LOCATION)),
    1.1f,
    1.1f,
    TEXTURE
) {
    init {
        this.addLayer(GenericGlowLayer<T?, ModelMountCaWD<T?>?>(this, TEXTURE))
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/mount_ca_w_d.png")
    }
}
