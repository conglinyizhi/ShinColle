package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelBattleshipYamato
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityBattleshipYamato

class RendererBattleshipYamato(context: EntityRendererProvider.Context) :
    MobRenderer<EntityBattleshipYamato?, ModelBattleshipYamato<EntityBattleshipYamato?>?>(
        context,
        ModelBattleshipYamato<EntityBattleshipYamato?>(context.bakeLayer(ModelBattleshipYamato.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(
            GenericGlowLayer<EntityBattleshipYamato?, ModelBattleshipYamato<EntityBattleshipYamato?>?>(
                this,
                TEXTURE
            )
        )
    }

    override fun getTextureLocation(entity: EntityBattleshipYamato): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityBattleshipYamato, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityBattleshipYamato): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/battleship_yamato.png")
        private const val MODEL_SCALE = 0.34f
    }
}
