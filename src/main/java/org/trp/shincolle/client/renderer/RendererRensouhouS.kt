package org.trp.shincolle.client.renderer

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelRensouhouS
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.base.EntityShincolleSimpleMob

class RendererRensouhouS<T : EntityShincolleSimpleMob?>(context: EntityRendererProvider.Context) :
    RendererSimpleMob<T?, ModelRensouhouS<T?>?>(
        context,
        ModelRensouhouS<T?>(context.bakeLayer(ModelRensouhouS.LAYER_LOCATION)),
        0.5f,
        0.34f,
        TEXTURE
    ) {
    init {
        this.addLayer(GenericGlowLayer<T?, ModelRensouhouS<T?>?>(this, TEXTURE))
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/rensouhou_s.png")
    }
}
