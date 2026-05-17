package org.trp.shincolle.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.client.model.ModelRensouhouS;
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer;
import org.trp.shincolle.entity.base.EntityShincolleSimpleMob;

public class RendererRensouhouS<T extends EntityShincolleSimpleMob> extends RendererSimpleMob<T, ModelRensouhouS<T>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/rensouhou_s.png");

    public RendererRensouhouS(EntityRendererProvider.Context context) {
        super(context, new ModelRensouhouS<>(context.bakeLayer(ModelRensouhouS.LAYER_LOCATION)), 0.5f, 0.34f, TEXTURE);
        this.addLayer(new GenericGlowLayer<>(this, TEXTURE));
    }
}
