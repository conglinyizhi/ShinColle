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
import org.trp.shincolle.Shincolle
import org.trp.shincolle.entity.base.EntityShincolleSimpleMob

class ModelRensouhou<T : EntityShincolleSimpleMob?>(root: ModelPart) : EntityModel<T>() {
    private val BodyMain: ModelPart
    private val SwimRing: ModelPart
    private val Head: ModelPart
    private val ArmLeft: ModelPart
    private val ArmRight: ModelPart
    private val LegLeft: ModelPart
    private val LegRight: ModelPart
    private val Propeller: ModelPart
    private val EarL: ModelPart
    private val EarR: ModelPart
    private val HeadBack: ModelPart
    private val Radar: ModelPart
    private val CannonL01: ModelPart
    private val CannonR01: ModelPart
    private val Face0: ModelPart
    private val Face1: ModelPart
    private val Face2: ModelPart
    private val CannonL02: ModelPart
    private val CannonR02: ModelPart

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.ArmLeft = this.BodyMain.getChild("ArmLeft")
        this.ArmRight = this.BodyMain.getChild("ArmRight")
        this.SwimRing = this.BodyMain.getChild("SwimRing")
        this.Propeller = this.SwimRing.getChild("Propeller")
        this.LegRight = this.SwimRing.getChild("LegRight")
        this.LegLeft = this.SwimRing.getChild("LegLeft")
        this.Head = this.BodyMain.getChild("Head")
        this.Face2 = this.Head.getChild("Face2")
        this.EarL = this.Head.getChild("EarL")
        this.CannonR01 = this.Head.getChild("CannonR01")
        this.CannonR02 = this.CannonR01.getChild("CannonR02")
        this.Face1 = this.Head.getChild("Face1")
        this.Radar = this.Head.getChild("Radar")
        this.CannonL01 = this.Head.getChild("CannonL01")
        this.CannonL02 = this.CannonL01.getChild("CannonL02")
        this.HeadBack = this.Head.getChild("HeadBack")
        this.Face0 = this.Head.getChild("Face0")
        this.EarR = this.Head.getChild("EarR")
    }

    override fun setupAnim(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val angleX = Mth.cos(ageInTicks * 0.08f)
        val angleRun = Mth.cos(limbSwing) * limbSwingAmount
        val addk1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount + 0.7f
        val addk2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount + 0.7f

        this.Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)
        this.BodyMain.xRot = 0.0f
        this.ArmLeft.xRot = angleX * 0.3f + 0.9f
        this.ArmRight.xRot = angleX * 0.3f + 0.9f
        this.CannonL01.xRot = angleX * 0.05f - 0.5f
        this.CannonR01.xRot = -angleX * 0.05f - 0.5f
        this.Propeller.zRot = ageInTicks / 4.0f

        if (limbSwingAmount > 0.9f) {
            this.setFace(2)
            this.BodyMain.xRot = 0.2618f
            this.ArmLeft.xRot = angleRun * 0.3f + 0.9f
            this.ArmRight.xRot = angleRun * 0.3f + 0.9f
            this.CannonL01.xRot = angleRun * 0.05f - 0.5f
            this.CannonR01.xRot = -angleRun * 0.05f - 0.5f
            this.Propeller.zRot = limbSwing / 2.0f
        } else {
            this.setFace(0)
        }

        if (entity != null && entity.attackTick > 0) {
            this.setFace(2)
        }

        this.LegLeft.xRot = addk1
        this.LegRight.xRot = addk2
    }

    private fun setFace(emo: Int) {
        this.Face0.visible = (emo == 0)
        this.Face1.visible = (emo == 1)
        this.Face2.visible = (emo == 2)
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

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "rensouhou"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, -6f, -5f, 10f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, 7f, 0f)
            )

            val ArmLeft = BodyMain.addOrReplaceChild(
                "ArmLeft",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, 0f, -8f, 5f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5f, -4f, -4f, 1.0472f, -0.5236f, 0f)
            )

            val ArmRight = BodyMain.addOrReplaceChild(
                "ArmRight",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, 0f, -8f, 5f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5f, -4f, -4f, 1.0472f, 0.5236f, 0f)
            )

            val SwimRing = BodyMain.addOrReplaceChild(
                "SwimRing",
                CubeListBuilder.create().texOffs(0, 29).addBox(-9f, 0f, -9f, 18f, 7f, 18f, CubeDeformation(0f)),
                PartPose.offset(0f, 5f, 0f)
            )

            val Propeller = SwimRing.addOrReplaceChild(
                "Propeller",
                CubeListBuilder.create().texOffs(0, 24).addBox(-2.5f, -2.5f, 0f, 5f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 4f, 9f)
            )

            val LegRight = SwimRing.addOrReplaceChild(
                "LegRight",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, 0f, -7f, 5f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, 6f, 0f, 0.5236f, 0.3491f, 0f)
            )

            val LegLeft = SwimRing.addOrReplaceChild(
                "LegLeft",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, 0f, -7f, 5f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, 6f, 0f, 0.5236f, -0.3491f, 0f)
            )

            val Head = BodyMain.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(56, 37).addBox(-9f, -8f, -9f, 18f, 9f, 18f, CubeDeformation(0f)),
                PartPose.offset(0f, -7f, 0f)
            )

            val Face2 = Head.addOrReplaceChild(
                "Face2",
                CubeListBuilder.create().texOffs(88, 0).addBox(-8.5f, 0f, 0f, 17f, 9f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -8f, -9.1f)
            )

            val EarL = Head.addOrReplaceChild(
                "EarL",
                CubeListBuilder.create().texOffs(55, 20).addBox(-2f, 0f, 0f, 4f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offset(7f, -11f, -9f)
            )

            val CannonR01 = Head.addOrReplaceChild(
                "CannonR01",
                CubeListBuilder.create().texOffs(54, 36).addBox(-2f, -2f, -6f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offset(-2.5f, -9f, -2f)
            )

            val CannonR02 = CannonR01.addOrReplaceChild(
                "CannonR02",
                CubeListBuilder.create().texOffs(0, 1).addBox(-1.5f, -1.5f, -26f, 3f, 3f, 20f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val Face1 = Head.addOrReplaceChild(
                "Face1",
                CubeListBuilder.create().texOffs(54, 9).addBox(-8.5f, 0f, 0f, 17f, 9f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -8f, -9.1f)
            )

            val Radar = Head.addOrReplaceChild(
                "Radar",
                CubeListBuilder.create().texOffs(0, 37).addBox(0f, 0f, 0f, 4f, 4f, 5f, CubeDeformation(0f)),
                PartPose.offset(5f, -15f, -5f)
            )

            val CannonL01 = Head.addOrReplaceChild(
                "CannonL01",
                CubeListBuilder.create().texOffs(54, 36).addBox(-2f, -2f, -6f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offset(2.5f, -9f, -2f)
            )

            val CannonL02 = CannonL01.addOrReplaceChild(
                "CannonL02",
                CubeListBuilder.create().texOffs(0, 1).addBox(-1.5f, -1.5f, -26f, 3f, 3f, 20f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val HeadBack = Head.addOrReplaceChild(
                "HeadBack",
                CubeListBuilder.create().texOffs(70, 22).addBox(-9f, 0f, 0f, 18f, 4f, 11f, CubeDeformation(0f)),
                PartPose.offset(0f, -12f, -2f)
            )

            val Face0 = Head.addOrReplaceChild(
                "Face0",
                CubeListBuilder.create().texOffs(54, 0).addBox(-8.5f, 0f, 0f, 17f, 9f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -8f, -9.1f)
            )

            val EarR = Head.addOrReplaceChild(
                "EarR",
                CubeListBuilder.create().texOffs(55, 20).mirror().addBox(-2f, 0f, 0f, 4f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offset(-7f, -11f, -9f)
            )

            return LayerDefinition.create(meshdefinition, 128, 64)
        }
    }
}
