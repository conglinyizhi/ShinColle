package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelBattleshipRe
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityBattleshipRe

class RendererBattleshipRe(context: EntityRendererProvider.Context) :
    MobRenderer<EntityBattleshipRe?, ModelBattleshipRe<EntityBattleshipRe?>?>(
        context,
        ModelBattleshipRe<EntityBattleshipRe?>(context.bakeLayer(ModelBattleshipRe.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityBattleshipRe?, ModelBattleshipRe<EntityBattleshipRe?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityBattleshipRe): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityBattleshipRe, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityBattleshipRe): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/battleship_re.png")
        private const val MODEL_SCALE = 0.34f
    }
}
