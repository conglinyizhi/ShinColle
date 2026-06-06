package org.trp.shincolle.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.LightTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import org.trp.shincolle.Shincolle

class ModelVortex(private val root: ModelPart) : EntityModel<Entity>() {
    override fun setupAnim(
        entity: Entity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        root.render(poseStack, vertexConsumer, LightTexture.FULL_BRIGHT, packedOverlay, color)
    }

    companion object {
        @JvmField
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "model_vortex"), "main")

        @JvmStatic
        fun createBodyLayer(): LayerDefinition {
            val mesh = MeshDefinition()
            val part = mesh.getRoot()

            part.addOrReplaceChild(
                "vortex",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-64.0f, -64.0f, -0.5f, 128.0f, 128.0f, 1.0f, CubeDeformation.NONE),
                PartPose.ZERO
            )

            return LayerDefinition.create(mesh, 256, 128)
        }
    }
}
