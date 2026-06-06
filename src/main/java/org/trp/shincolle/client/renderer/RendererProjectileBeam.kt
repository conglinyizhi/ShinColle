package org.trp.shincolle.client.renderer

import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.entity.projectile.EntityProjectileBeam

class RendererProjectileBeam(context: EntityRendererProvider.Context) : EntityRenderer<EntityProjectileBeam>(context) {
    override fun getTextureLocation(entity: EntityProjectileBeam): ResourceLocation {
        return DUMMY_TEXTURE
    }

    companion object {
        private val DUMMY_TEXTURE: ResourceLocation =
            ResourceLocation.withDefaultNamespace("textures/misc/missingno.png")
    }
}
