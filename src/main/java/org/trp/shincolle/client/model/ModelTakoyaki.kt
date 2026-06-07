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
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import org.trp.shincolle.Shincolle

class ModelTakoyaki<T : Entity>(root: ModelPart) : EntityModel<T>(), IGlowableModel {
    private val BodyMain: ModelPart
    private val JawMain: ModelPart
    private val EyeL: ModelPart
    private val Body1: ModelPart
    private val Body2: ModelPart
    private val Body3: ModelPart
    private val EarL: ModelPart
    private val EarR: ModelPart
    private val Jaw1: ModelPart
    private val Jaw2: ModelPart
    private val Jaw3: ModelPart
    private val Tongue: ModelPart
    private val GlowBodyMain: ModelPart

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Body1 = this.BodyMain.getChild("Body1")
        this.JawMain = this.BodyMain.getChild("JawMain")
        this.Jaw1 = this.JawMain.getChild("Jaw1")
        this.Jaw2 = this.JawMain.getChild("Jaw2")
        this.Jaw3 = this.JawMain.getChild("Jaw3")
        this.Tongue = this.JawMain.getChild("Tongue")
        this.Body3 = this.BodyMain.getChild("Body3")
        this.Body2 = this.BodyMain.getChild("Body2")
        this.EarL = this.BodyMain.getChild("EarL")
        this.EarR = this.BodyMain.getChild("EarR")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.EyeL = this.GlowBodyMain.getChild("EyeL")
    }

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val headRadY = netHeadYaw * (Math.PI.toFloat() / 180f)
        val headRadX = headPitch * (Math.PI.toFloat() / 180f)

        this.BodyMain.yRot = headRadY
        this.BodyMain.xRot = headRadX
        this.GlowBodyMain.yRot = headRadY
        this.GlowBodyMain.xRot = headRadX

        this.JawMain.zRot = 0.0f
        this.JawMain.xRot = Mth.cos(ageInTicks * 0.125f) * 0.2f + 1.1f
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "takoyaki"), "main")

        @JvmStatic
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(76, 42).addBox(-6.5f, -6.4f, -6.5f, 13f, 9f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -0.3491f, 0f, 0f)
            )

            val Body1 = BodyMain.addOrReplaceChild(
                "Body1",
                CubeListBuilder.create().texOffs(76, 19).addBox(-5f, -4.3f, -8f, 10f, 7f, 16f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val JawMain = BodyMain.addOrReplaceChild(
                "JawMain",
                CubeListBuilder.create().texOffs(0, 38).addBox(-6.5f, -1.1f, -8f, 13f, 6f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3.5f, 3f, 1.3f, 0f, 0f)
            )

            val Jaw1 = JawMain.addOrReplaceChild(
                "Jaw1",
                CubeListBuilder.create().texOffs(0, 17).addBox(-5f, 0f, 0f, 10f, 5f, 16f, CubeDeformation(0f)),
                PartPose.offset(0f, -1.2f, -9.5f)
            )

            val Jaw2 = JawMain.addOrReplaceChild(
                "Jaw2",
                CubeListBuilder.create().texOffs(0, 2).addBox(-8f, 0f, 0f, 16f, 5f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, -6.5f)
            )

            val Jaw3 = JawMain.addOrReplaceChild(
                "Jaw3",
                CubeListBuilder.create().texOffs(42, 0).addBox(-5f, 5f, -5.5f, 10f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val Tongue = JawMain.addOrReplaceChild(
                "Tongue",
                CubeListBuilder.create().texOffs(50, 39).addBox(-4.5f, 0f, -7f, 9f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2f, 0.5f, -0.0873f, 0f, 0f)
            )

            val Body3 = BodyMain.addOrReplaceChild(
                "Body3",
                CubeListBuilder.create().texOffs(76, 1).addBox(-8f, -5f, -5f, 16f, 8f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -0.5f, 0f)
            )

            val Body2 = BodyMain.addOrReplaceChild(
                "Body2",
                CubeListBuilder.create().texOffs(54, 19).addBox(-5f, -8.5f, -4.5f, 10f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0f, 0f, -0.0136f)
            )

            val EarL = BodyMain.addOrReplaceChild(
                "EarL",
                CubeListBuilder.create().texOffs(114, 20).mirror()
                    .addBox(-2f, -8f, -1.5f, 4f, 8f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.5f, -4.5f, 3f, -0.5236f, -0.5236f, 0.7854f)
            )

            val EarR = BodyMain.addOrReplaceChild(
                "EarR",
                CubeListBuilder.create().texOffs(114, 20).addBox(-2f, -8f, -1.5f, 4f, 8f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5.5f, -4.5f, 3f, -0.5236f, 0.5236f, -0.7854f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 0f)
            )

            val EyeL = GlowBodyMain.addOrReplaceChild(
                "EyeL",
                CubeListBuilder.create().texOffs(65, 50).addBox(0f, -3f, -3f, 0f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8.1f, -3.3f, 0.5f, -0.1745f, 0f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 64)
        }
    }
}
