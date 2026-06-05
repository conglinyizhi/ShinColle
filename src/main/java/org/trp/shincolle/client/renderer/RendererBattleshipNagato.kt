package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelBattleshipNagato
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityBattleshipNagato

class RendererBattleshipNagato(context: EntityRendererProvider.Context) :
    MobRenderer<EntityBattleshipNagato?, ModelBattleshipNagato<EntityBattleshipNagato?>?>(
        context,
        ModelBattleshipNagato<EntityBattleshipNagato?>(context.bakeLayer(ModelBattleshipNagato.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(
            GenericGlowLayer<EntityBattleshipNagato?, ModelBattleshipNagato<EntityBattleshipNagato?>?>(
                this,
                TEXTURE
            )
        )
    }

    override fun getTextureLocation(entity: EntityBattleshipNagato): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityBattleshipNagato, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityBattleshipNagato): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/battleship_nagato.png")
        private const val MODEL_SCALE = 0.34f
    }
}
