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

class ModelLargeShipyard(private val root: ModelPart) : EntityModel<Entity>() {
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "large_shipyard"), "main")

        fun createBodyLayer(): LayerDefinition {
            val mesh = MeshDefinition()
            val part = mesh.getRoot()

            val body = part.addOrReplaceChild(
                "body_main",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 18.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "base00",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-24.0f, 0.0f, -24.0f, 16.0f, 6.0f, 16.0f, CubeDeformation.NONE),
                PartPose.ZERO
            )
            body.addOrReplaceChild(
                "base01",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-8.0f, -2.0f, -24.0f, 16.0f, 8.0f, 16.0f, CubeDeformation.NONE),
                PartPose.ZERO
            )
            body.addOrReplaceChild(
                "base02",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(8.0f, -1.0f, -24.0f, 16.0f, 7.0f, 16.0f, CubeDeformation.NONE),
                PartPose.ZERO
            )
            body.addOrReplaceChild(
                "base03",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(8.0f, 0.0f, -8.0f, 16.0f, 6.0f, 16.0f, CubeDeformation.NONE),
                PartPose.ZERO
            )
            body.addOrReplaceChild(
                "base04",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(8.0f, -3.0f, 8.0f, 16.0f, 9.0f, 16.0f, CubeDeformation.NONE),
                PartPose.ZERO
            )
            body.addOrReplaceChild(
                "base05",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-8.0f, -2.0f, 8.0f, 16.0f, 8.0f, 16.0f, CubeDeformation.NONE),
                PartPose.ZERO
            )
            body.addOrReplaceChild(
                "base06",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-24.0f, -1.0f, 8.0f, 16.0f, 7.0f, 16.0f, CubeDeformation.NONE),
                PartPose.ZERO
            )
            body.addOrReplaceChild(
                "base07",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-24.0f, -2.0f, -8.0f, 16.0f, 8.0f, 16.0f, CubeDeformation.NONE),
                PartPose.ZERO
            )
            body.addOrReplaceChild(
                "base08",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-8.0f, 1.0f, -8.0f, 16.0f, 5.0f, 16.0f, CubeDeformation.NONE),
                PartPose.ZERO
            )

            val body01 = body.addOrReplaceChild(
                "body01",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, 0.0f, 0.0f, 14.0f, 6.0f, 14.0f, CubeDeformation.NONE),
                PartPose.offset(7.0f, -6.0f, 6.0f)
            )
            body.addOrReplaceChild(
                "body02",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-7.0f, -4.9f, 11.0f, 18.0f, 5.0f, 11.0f, CubeDeformation.NONE),
                PartPose.ZERO
            )
            val body03 = body.addOrReplaceChild(
                "body03",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, 0.0f, 0.0f, 15.0f, 7.0f, 13.0f, CubeDeformation.NONE),
                PartPose.offset(-20.6f, -7.0f, 8.0f)
            )
            body.addOrReplaceChild(
                "body04",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-22.0f, -4.0f, -10.0f, 12.0f, 4.0f, 20.0f, CubeDeformation.NONE),
                PartPose.ZERO
            )
            val body05 = body.addOrReplaceChild(
                "body05",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, 0.0f, 0.0f, 12.0f, 5.0f, 15.0f, CubeDeformation.NONE),
                PartPose.offset(-20.0f, -5.0f, -22.0f)
            )
            body.addOrReplaceChild(
                "body06",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-10.0f, -6.0f, -23.0f, 20.0f, 6.0f, 10.0f, CubeDeformation.NONE),
                PartPose.ZERO
            )
            val body07 = body.addOrReplaceChild(
                "body07",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, 0.0f, 0.0f, 12.0f, 7.0f, 14.0f, CubeDeformation.NONE),
                PartPose.offset(10.0f, -7.0f, -20.0f)
            )
            body.addOrReplaceChild(
                "body08",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(15.0f, -5.0f, -10.0f, 8.0f, 5.0f, 18.0f, CubeDeformation.NONE),
                PartPose.ZERO
            )

            val pillar01a = body01.addOrReplaceChild(
                "pillar01a",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-5.0f, -10.0f, -5.0f, 10.0f, 10.0f, 10.0f, CubeDeformation.NONE),
                PartPose.offsetAndRotation(7.0f, 2.0f, 7.0f, 0.17453292f, 0.0f, -0.17453292f)
            )
            pillar01a.addOrReplaceChild(
                "pillar01b",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.0f, -13.3f, -3.0f, 7.0f, 8.0f, 7.0f, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0f, -3.0f, 0.0f, 0.17453292f, 0.0f, -0.17453292f)
            )

            val pillar01a_1 = body07.addOrReplaceChild(
                "pillar01a_1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -10.0f, -4.0f, 9.0f, 10.0f, 9.0f, CubeDeformation.NONE),
                PartPose.offsetAndRotation(5.0f, 2.0f, 6.0f, -0.17453292f, 0.0f, -0.17453292f)
            )
            pillar01a_1.addOrReplaceChild(
                "pillar01b_1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.0f, -8.0f, -3.0f, 6.0f, 8.0f, 6.0f, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0f, -9.0f, 0.5f, -0.17453292f, 0.0f, -0.17453292f)
            )

            val pillar02a = body03.addOrReplaceChild(
                "pillar02a",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-5.5f, -10.0f, -4.5f, 11.0f, 10.0f, 9.0f, CubeDeformation.NONE),
                PartPose.offsetAndRotation(8.0f, 2.0f, 6.0f, 0.17453292f, 0.0f, 0.17453292f)
            )
            pillar02a.addOrReplaceChild(
                "pillar02b",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-5.5f, -6.0f, -4.0f, 8.0f, 8.0f, 7.0f, CubeDeformation.NONE),
                PartPose.offsetAndRotation(2.0f, -10.6f, 0.5f, 0.17453292f, 0.0f, 0.17453292f)
            )

            val pillar03a = body05.addOrReplaceChild(
                "pillar03a",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.5f, -8.0f, -5.0f, 9.0f, 9.0f, 10.0f, CubeDeformation.NONE),
                PartPose.offsetAndRotation(6.0f, 1.0f, 7.0f, -0.17453292f, 0.0f, 0.17453292f)
            )
            pillar03a.addOrReplaceChild(
                "pillar03b",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.0f, -6.0f, -3.0f, 6.0f, 8.0f, 6.0f, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0f, -8.5f, 0.0f, -0.17453292f, 0.0f, 0.17453292f)
            )

            return LayerDefinition.create(mesh, 64, 32)
        }
    }
}
