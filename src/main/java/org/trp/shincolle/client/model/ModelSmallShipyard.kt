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
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import org.trp.shincolle.Shincolle

class ModelSmallShipyard(private val root: ModelPart) : EntityModel<Entity>() {
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
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "small_shipyard"), "main")

        fun createBodyLayer(): LayerDefinition {
            val mesh = MeshDefinition()
            val part = mesh.getRoot()

            part.addOrReplaceChild(
                "shape1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-8.0f, -1.0f, -8.0f, 16.0f, 1.0f, 16.0f, CubeDeformation.NONE),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            part.addOrReplaceChild(
                "shape2",
                CubeListBuilder.create().texOffs(0, 19)
                    .addBox(-7.0f, -4.0f, -3.0f, 14.0f, 3.0f, 10.0f, CubeDeformation.NONE),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            part.addOrReplaceChild(
                "shape3",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-6.0f, -7.0f, -1.5f, 6.0f, 4.0f, 6.0f, CubeDeformation.NONE),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            part.addOrReplaceChild(
                "shape4",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-5.0f, -13.0f, -1.0f, 4.0f, 6.0f, 4.0f, CubeDeformation.NONE),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            part.addOrReplaceChild(
                "shape5",
                CubeListBuilder.create().texOffs(48, 6)
                    .addBox(-4.5f, -16.0f, -0.5f, 3.0f, 3.0f, 3.0f, CubeDeformation.NONE),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            part.addOrReplaceChild(
                "shape6",
                CubeListBuilder.create().texOffs(32, 20)
                    .addBox(-3.5f, -6.0f, 0.0f, 10.0f, 3.0f, 6.0f, CubeDeformation.NONE),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            part.addOrReplaceChild(
                "shape7",
                CubeListBuilder.create().texOffs(0, 10)
                    .addBox(2.0f, -9.0f, 1.5f, 4.0f, 3.0f, 4.0f, CubeDeformation.NONE),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            part.addOrReplaceChild(
                "shape8",
                CubeListBuilder.create().texOffs(48, 12)
                    .addBox(2.5f, -11.5f, 2.0f, 3.0f, 3.0f, 3.0f, CubeDeformation.NONE),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            part.addOrReplaceChild(
                "shape9",
                CubeListBuilder.create().texOffs(0, 17)
                    .addBox(-5.0f, -3.0f, -7.0f, 11.0f, 2.0f, 4.0f, CubeDeformation.NONE),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            part.addOrReplaceChild(
                "shape10",
                CubeListBuilder.create().texOffs(0, 10)
                    .addBox(-1.0f, -5.0f, -6.5f, 4.0f, 2.0f, 4.0f, CubeDeformation.NONE),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            part.addOrReplaceChild(
                "shape11",
                CubeListBuilder.create().texOffs(48, 0)
                    .addBox(-0.5f, -8.0f, -6.0f, 3.0f, 3.0f, 3.0f, CubeDeformation.NONE),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(mesh, 64, 32)
        }
    }
}
