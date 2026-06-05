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

class ModelAirplane<T : Entity?>(root: ModelPart) : EntityModel<T?>(), IGlowableModel {
    private val BodyMain: ModelPart
    private val EyeL: ModelPart
    private val EyeR: ModelPart
    private val AirfoilL: ModelPart
    private val AirfoilR: ModelPart
    private val Head: ModelPart
    private val BodyFront: ModelPart
    private val Tail: ModelPart
    private val Tongue: ModelPart
    private val BombL: ModelPart
    private val BombR: ModelPart
    private val GunBase: ModelPart
    private val Gun: ModelPart
    private val GlowBodyMain: ModelPart

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Head = this.BodyMain.getChild("Head")
        this.BombR = this.BodyMain.getChild("BombR")
        this.AirfoilL = this.BodyMain.getChild("AirfoilL")
        this.AirfoilR = this.BodyMain.getChild("AirfoilR")
        this.BombL = this.BodyMain.getChild("BombL")
        this.GunBase = this.BodyMain.getChild("GunBase")
        this.Gun = this.GunBase.getChild("Gun")
        this.Tail = this.BodyMain.getChild("Tail")
        this.BodyFront = this.BodyMain.getChild("BodyFront")
        this.Tongue = this.BodyMain.getChild("Tongue")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.EyeL = this.GlowBodyMain.getChild("EyeL")
        this.EyeR = this.GlowBodyMain.getChild("EyeR")
    }

    override fun setupAnim(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val yaw = netHeadYaw * (Math.PI.toFloat() / 180f)
        val pitch = headPitch * (Math.PI.toFloat() / 180f)

        BodyMain.yRot = yaw
        BodyMain.xRot = pitch
        syncGlowParts()
    }

    private fun syncGlowParts() {
        GlowBodyMain.copyFrom(BodyMain)
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        BodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    override fun renderGlow(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        GlowBodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    companion object {
        @JvmField
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "airplane"), "main")

        @JvmStatic
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(3, 18).addBox(-3f, -3f, -1f, 6f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val Head = BodyMain.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(8, 24).addBox(-2f, -2f, -2f, 4f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -6.2f, 0f, 0.7854f, 0f)
            )

            val BombR = BodyMain.addOrReplaceChild(
                "BombR",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 2f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, 2.3f, -1f, 0f, 0f, 0.7854f)
            )

            val AirfoilL = BodyMain.addOrReplaceChild(
                "AirfoilL",
                CubeListBuilder.create().texOffs(0, 17).addBox(-2.5f, -2f, -6f, 5f, 4f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.5f, 0f, 0f, 0f, 0.5236f, 0.1222f)
            )

            val AirfoilR = BodyMain.addOrReplaceChild(
                "AirfoilR",
                CubeListBuilder.create().texOffs(0, 17).addBox(-2.5f, -2f, -6f, 5f, 4f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.5f, 0f, 0f, 0f, -0.5236f, -0.1222f)
            )

            val BombL = BodyMain.addOrReplaceChild(
                "BombL",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 2f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, 2.3f, -1f, 0f, 0f, 0.7854f)
            )

            val GunBase = BodyMain.addOrReplaceChild(
                "GunBase",
                CubeListBuilder.create().texOffs(10, 24).addBox(-1.5f, 0f, 0f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 4f, 2.5f)
            )

            val Gun = GunBase.addOrReplaceChild(
                "Gun",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, 0f, -8f, 1f, 1f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.5f, 0f, 0.0524f, 0f, 0f)
            )

            val Tail = BodyMain.addOrReplaceChild(
                "Tail",
                CubeListBuilder.create().texOffs(0, 19).addBox(-4f, -2.5f, -4f, 8f, 5f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 4.3f, 0f, 0.7854f, 0f)
            )

            val BodyFront = BodyMain.addOrReplaceChild(
                "BodyFront",
                CubeListBuilder.create().texOffs(12, 6).addBox(-2.5f, -2.6f, -2.5f, 5f, 6f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -3.2f, 0.0873f, 0f, 0f)
            )

            val Tongue = BodyMain.addOrReplaceChild(
                "Tongue",
                CubeListBuilder.create().texOffs(0, 13).addBox(-1.5f, 0f, -3f, 3f, 1f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.3f, -3.5f, 1.6581f, 0f, 0f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 0f)
            )

            val EyeL = GlowBodyMain.addOrReplaceChild(
                "EyeL",
                CubeListBuilder.create().texOffs(16, 0).addBox(-2f, 0f, -2f, 4f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.7f, -3.2f, 2f, 0f, 0.7854f, 0.1745f)
            )

            val EyeR = GlowBodyMain.addOrReplaceChild(
                "EyeR",
                CubeListBuilder.create().texOffs(16, 0).addBox(-2f, 0f, -2f, 4f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.7f, -3.2f, 2f, 0f, -2.3562f, -0.1745f)
            )

            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }
}
