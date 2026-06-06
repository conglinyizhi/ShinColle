package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelBattleshipRu
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityBattleshipRu

class RendererBattleshipRu(context: EntityRendererProvider.Context) :
    MobRenderer<EntityBattleshipRu, ModelBattleshipRu<EntityBattleshipRu>>(
        context,
        ModelBattleshipRu<EntityBattleshipRu>(context.bakeLayer(ModelBattleshipRu.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityBattleshipRu, ModelBattleshipRu<EntityBattleshipRu>>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityBattleshipRu): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityBattleshipRu, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityBattleshipRu): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/battleship_ru.png")
        private const val MODEL_SCALE = 0.34f
    }
}
