package org.trp.shincolle.client.renderer

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelTakoyaki
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer

class RendererTakoyaki<T : Mob>(context: EntityRendererProvider.Context) : RendererSimpleMob<T, ModelTakoyaki<T>>(
    context,
    ModelTakoyaki(context.bakeLayer(ModelTakoyaki.LAYER_LOCATION)),
    0.5f,
    0.34f,
    TEXTURE
) {
    init {
        @Suppress("UNCHECKED_CAST")
        this.addLayer(GenericGlowLayer(this as net.minecraft.client.renderer.entity.RenderLayerParent<T, net.minecraft.client.model.EntityModel<T>>, TEXTURE))
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/takoyaki.png")
    }
}
