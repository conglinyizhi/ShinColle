package org.trp.shincolle.client.renderer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.entity.projectile.EntityProjectileBeam;

public class RendererProjectileBeam extends EntityRenderer<EntityProjectileBeam> {
    private static final ResourceLocation DUMMY_TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/missingno.png");

    public RendererProjectileBeam(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityProjectileBeam entity) {
        return DUMMY_TEXTURE;
    }
}
