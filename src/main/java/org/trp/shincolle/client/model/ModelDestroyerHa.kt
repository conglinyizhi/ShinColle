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
import org.trp.shincolle.client.model.LegacyPoseOffsets.deadY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sittingY
import org.trp.shincolle.entity.EntityDestroyerHa
import org.trp.shincolle.entity.base.EntityShipBase

class ModelDestroyerHa<T : Entity?>(root: ModelPart) : EntityModel<T?>(), IGlowableModel {
    private val Back: ModelPart
    private val NeckBack: ModelPart
    private val Body: ModelPart
    private val TailBack: ModelPart
    private val Head: ModelPart
    private val NeckBody: ModelPart
    private val HeadD01: ModelPart
    private val k00: ModelPart
    private val ToothU: ModelPart
    private val Face00: ModelPart
    private val Face01: ModelPart
    private val Face02: ModelPart
    private val HeadD02: ModelPart
    private val ToothL: ModelPart
    private val HeadD03: ModelPart
    private val k01: ModelPart
    private val k02: ModelPart
    private val k03: ModelPart
    private val LegLeftFront: ModelPart
    private val LegRightFront: ModelPart
    private val LegLeftEnd: ModelPart
    private val LegRightEnd: ModelPart
    private val TailEnd1: ModelPart
    private val TailEnd2: ModelPart
    private val GlowBack: ModelPart
    private val GlowNeckBack: ModelPart
    private val GlowHead: ModelPart
    private var poseTranslateY = 0f

    init {
        this.Back = root.getChild("Back")
        this.TailBack = this.Back.getChild("TailBack")
        this.TailEnd1 = this.TailBack.getChild("TailEnd1")
        this.TailEnd2 = this.TailBack.getChild("TailEnd2")
        this.NeckBack = this.Back.getChild("NeckBack")
        this.Head = this.NeckBack.getChild("Head")
        this.ToothU = this.Head.getChild("ToothU")
        this.HeadD01 = this.Head.getChild("HeadD01")
        this.HeadD02 = this.HeadD01.getChild("HeadD02")
        this.ToothL = this.HeadD02.getChild("ToothL")
        this.HeadD03 = this.HeadD02.getChild("HeadD03")
        this.NeckBody = this.NeckBack.getChild("NeckBody")
        this.Body = this.Back.getChild("Body")
        this.LegRightFront = this.Body.getChild("LegRightFront")
        this.LegRightEnd = this.LegRightFront.getChild("LegRightEnd")
        this.LegLeftFront = this.Body.getChild("LegLeftFront")
        this.LegLeftEnd = this.LegLeftFront.getChild("LegLeftEnd")
        this.GlowBack = root.getChild("GlowBack")
        this.GlowNeckBack = this.GlowBack.getChild("GlowNeckBack")
        this.GlowHead = this.GlowNeckBack.getChild("GlowHead")
        this.Face00 = this.GlowHead.getChild("Face00")
        this.Face01 = this.GlowHead.getChild("Face01")
        this.Face02 = this.GlowHead.getChild("Face02")
        this.k00 = this.GlowHead.getChild("k00")
        this.k01 = this.k00.getChild("k01")
        this.k02 = this.k00.getChild("k02")
        this.k03 = this.k00.getChild("k03")
    }

    override fun setupAnim(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        poseTranslateY = 0.0f
        if (entity !is EntityShipBase) {
            return
        }

        val angleX = Mth.cos(ageInTicks * 0.125f)
        if (entity.shipDepth > 0.0) {
            poseTranslateY += angleX * 0.05f + 0.025f
        }

        if (entity.isInDeadPose) {
            applyDeadPose(entity)
            syncGlowParts()
            return
        }

        Back.xRot = -0.1f
        Back.zRot = 0.0f
        NeckBack.xRot = -0.15f
        NeckBack.yRot = 0.0f
        Head.xRot = -0.2f
        Head.yRot = 0.0f
        LegLeftFront.zRot = 0.0f
        LegLeftEnd.zRot = 0.0f
        LegRightFront.zRot = 0.0f

        applyFaceFromEntity(entity)
        applyEquipVisibility(entity)
        applyLook(netHeadYaw, headPitch, angleX)
        if (entity.isInSittingPose()) {
            applySittingPose(entity, ageInTicks)
        } else {
            applyTailPose(angleX)
            applyLegPose(limbSwing, limbSwingAmount)
        }
        syncGlowParts()
    }

    private fun applyFaceFromEntity(ship: EntityShipBase) {
        val faceId = ship.faceId
        val faceIndex = if (faceId >= 0) faceId % 3 else 0
        setFace(faceIndex)
    }

    private fun setFace(faceIndex: Int) {
        Face00.visible = faceIndex == 0
        Face01.visible = faceIndex == 1
        Face02.visible = faceIndex == 2
    }

    private fun applyEquipVisibility(ship: EntityShipBase) {
        val show = ship.getEquipFlag(EntityDestroyerHa.EQUIP_HEAD_ORNAMENT)
        k00.visible = show
        k01.visible = show
        k02.visible = show
        k03.visible = show
    }

    private fun applyLook(headYaw: Float, headPitch: Float, angleX: Float) {
        if (headPitch != 0.0f) {
            NeckBack.xRot = headPitch * 0.005f
            NeckBack.yRot = headYaw * 0.005f
            Head.xRot = headPitch * 0.005f
            Head.yRot = headYaw * 0.005f
            HeadD01.xRot = angleX * 0.05f - 0.05f
            TailBack.xRot = 0.15f
            TailBack.yRot = headYaw * -0.005f
            TailEnd1.xRot = 0.2f
            TailEnd1.yRot = headYaw * -0.005f
        } else {
            HeadD01.xRot = angleX * 0.05f - 0.05f
        }
    }

    private fun applyDeadPose(ship: EntityShipBase?) {
        this.poseTranslateY = DEAD_TRANSLATE_Y
        setFace(2)
        Back.xRot = 0.0f
        Back.zRot = -1.66f
        NeckBack.xRot = 0.1745f
        NeckBack.yRot = 0.0f
        Head.xRot = 0.1745f
        Head.yRot = 0.0f
        HeadD01.xRot = 0.1745f
        TailBack.xRot = 0.4f
        TailBack.yRot = 0.0f
        TailEnd1.xRot = 0.4f
        TailEnd1.yRot = 0.0f
        LegLeftFront.xRot = 0.35f
        LegLeftFront.zRot = 0.52f
        LegLeftEnd.xRot = 0.0f
        LegLeftEnd.zRot = 0.52f
        LegRightFront.xRot = -0.2f
        LegRightFront.zRot = 0.087f
        LegRightEnd.xRot = 0.52f
    }

    private fun applySittingPose(ship: EntityShipBase, ageInTicks: Float) {
        this.poseTranslateY = SITTING_TRANSLATE_Y
        val angle1 = Mth.cos(ageInTicks)
        if (ship.emotionSecondary == EntityShipBase.EMOTION_BORED) {
            setFace(1)
            poseTranslateY += 0.4f
            Back.xRot = -0.8f
            NeckBack.xRot = -0.2618f
            Head.xRot = -0.2618f
            HeadD01.xRot = -angle1 * 0.05f + 0.2618f
            LegRightFront.xRot = -0.7f
            LegLeftFront.xRot = angle1 * 0.5f - 2.5f
            LegRightEnd.xRot = 0.35f
            LegLeftEnd.xRot = angle1 * 0.3f + 0.7f
            TailBack.xRot = 0.35f
            TailEnd1.xRot = 0.35f
        } else {
            poseTranslateY += 0.5f
            Back.xRot = 0.0f
            Back.zRot = -1.5708f
            NeckBack.xRot = 0.1745f
            Head.xRot = 0.1745f
            HeadD01.xRot = 0.1745f
            LegRightFront.xRot = 0.0f
            LegLeftFront.xRot = 0.5f
            LegRightEnd.xRot = 1.7f
            LegLeftEnd.xRot = 1.5f
            TailBack.xRot = -0.7f
            TailEnd1.xRot = -0.5f
        }
    }

    private fun applyTailPose(angleX: Float) {
        TailBack.xRot = angleX * 0.05f + 0.1745f
        TailEnd1.xRot = angleX * 0.1f + 0.2618f
    }

    private fun applyLegPose(limbSwing: Float, limbSwingAmount: Float) {
        val angle1 = Mth.cos(limbSwing * 0.6662f) * 0.5f * limbSwingAmount
        val angle2 = Mth.sin(limbSwing * 0.6662f) * 0.5f * limbSwingAmount
        LegRightFront.xRot = angle1 - 0.5f
        LegLeftFront.xRot = -angle1 - 0.5f
        LegRightEnd.xRot = angle2 + 1.0f
        LegLeftEnd.xRot = -angle2 + 1.0f
    }

    private fun syncGlowParts() {
        GlowBack.copyFrom(Back)
        GlowNeckBack.copyFrom(NeckBack)
        GlowHead.copyFrom(Head)
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        val usePoseTranslate = poseTranslateY != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, poseTranslateY, 0.0f)
        }

        Back.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    override fun renderGlow(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        val usePoseTranslate = poseTranslateY != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, poseTranslateY, 0.0f)
        }

        GlowBack.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "destroyer_ha"), "main")
        private val SITTING_TRANSLATE_Y = sittingY("ModelDestroyerHa")
        private val DEAD_TRANSLATE_Y = deadY("ModelDestroyerHa")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val Back = partdefinition.addOrReplaceChild(
                "Back",
                CubeListBuilder.create().texOffs(20, 73).addBox(-12f, -12f, -14f, 24f, 22f, 28f, CubeDeformation(0f)),
                PartPose.offset(0f, -22f, 0f)
            )

            val TailBack = Back.addOrReplaceChild(
                "TailBack",
                CubeListBuilder.create().texOffs(30, 79).addBox(-10f, -4f, 0f, 20f, 17f, 22f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 9f, 0.0873f, 0f, 0f)
            )

            val TailEnd1 = TailBack.addOrReplaceChild(
                "TailEnd1",
                CubeListBuilder.create().texOffs(36, 81).addBox(-8f, -3f, 0f, 16f, 12f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 19f, 0.1745f, 0f, 0f)
            )

            val TailEnd2 = TailBack.addOrReplaceChild(
                "TailEnd2",
                CubeListBuilder.create().texOffs(42, 85).addBox(-7f, -5f, 0f, 14f, 10f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8f, 20f, -0.5236f, 0f, 0f)
            )

            val NeckBack = Back.addOrReplaceChild(
                "NeckBack",
                CubeListBuilder.create().texOffs(24, 79).addBox(-13f, -10f, -20f, 26f, 26f, 22f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2.5f, -11f, -0.0873f, 0f, 0f)
            )

            val Head = NeckBack.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(16, 75).addBox(-13.5f, -14f, -28f, 27f, 27f, 26f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3f, -13f, -0.1745f, 0f, 0f)
            )

            val ToothU = Head.addOrReplaceChild(
                "ToothU",
                CubeListBuilder.create().texOffs(0, 0).addBox(-11f, 0f, 0f, 22f, 7f, 22f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12.5f, -28.5f, 0.0524f, 0f, 0f)
            )

            val HeadD01 = Head.addOrReplaceChild(
                "HeadD01",
                CubeListBuilder.create().texOffs(45, 94).addBox(-12f, 0f, -3f, 24f, 16f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12f, -3f, -0.1396f, 0f, 0f)
            )

            val HeadD02 = HeadD01.addOrReplaceChild(
                "HeadD02",
                CubeListBuilder.create().texOffs(27, 77).addBox(-10.5f, 0f, -21f, 21f, 8f, 24f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9.5f, -1.5f, 0.3491f, 0f, 0f)
            )

            val ToothL = HeadD02.addOrReplaceChild(
                "ToothL",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-11f, 0f, -22f, 22f, 7f, 22f, CubeDeformation(0f)),
                PartPose.offset(0f, 1f, 0.5f)
            )

            val HeadD03 = HeadD02.addOrReplaceChild(
                "HeadD03",
                CubeListBuilder.create().texOffs(44, 83).addBox(-5f, 0f, 0f, 10f, 10f, 18f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, -28f, 0.3491f, 0f, 0f)
            )

            val NeckBody = NeckBack.addOrReplaceChild(
                "NeckBody",
                CubeListBuilder.create().texOffs(46, 34).addBox(-9f, 0f, -9f, 18f, 11f, 22f, CubeDeformation(0f)),
                PartPose.offset(0f, 15f, -8f)
            )

            val Body = Back.addOrReplaceChild(
                "Body",
                CubeListBuilder.create().texOffs(44, 32).addBox(-9f, 0f, 0f, 18f, 14f, 24f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 11f, -18f, 0.1745f, 0f, 0f)
            )

            val LegRightFront = Body.addOrReplaceChild(
                "LegRightFront",
                CubeListBuilder.create().texOffs(66, 46).addBox(-5f, -4f, -5f, 10f, 16f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-12f, 7f, 14f, -0.5236f, 0f, 0f)
            )

            val LegRightEnd = LegRightFront.addOrReplaceChild(
                "LegRightEnd",
                CubeListBuilder.create().texOffs(70, 48).addBox(-4f, -3f, -4f, 8f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12f, 0f, 0.6981f, 0f, 0f)
            )

            val LegLeftFront = Body.addOrReplaceChild(
                "LegLeftFront",
                CubeListBuilder.create().texOffs(66, 46).addBox(-5f, -4f, -5f, 10f, 16f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(12f, 7f, 14f, -0.5236f, 0f, 0f)
            )

            val LegLeftEnd = LegLeftFront.addOrReplaceChild(
                "LegLeftEnd",
                CubeListBuilder.create().texOffs(70, 48).addBox(-4f, -3f, -4f, 8f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12f, 0f, 0.6981f, 0f, 0f)
            )

            val GlowBack = partdefinition.addOrReplaceChild(
                "GlowBack",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -22f, 0f)
            )

            val GlowNeckBack = GlowBack.addOrReplaceChild(
                "GlowNeckBack",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -2.5f, -11f, -0.0873f, 0f, 0f)
            )

            val GlowHead = GlowNeckBack.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 3f, -13f, -0.1745f, 0f, 0f)
            )

            val Face00 = GlowHead.addOrReplaceChild(
                "Face00",
                CubeListBuilder.create().texOffs(0, 81).addBox(-10f, 0f, 0f, 20f, 20f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -12f, -28.1f)
            )

            val Face01 = GlowHead.addOrReplaceChild(
                "Face01",
                CubeListBuilder.create().texOffs(0, 61).addBox(-10f, 0f, 0f, 20f, 20f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -12f, -28.2f)
            )

            val Face02 = GlowHead.addOrReplaceChild(
                "Face02",
                CubeListBuilder.create().texOffs(0, 41).addBox(-10f, 0f, 0f, 20f, 20f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -12f, -28.3f)
            )

            val k00 = GlowHead.addOrReplaceChild(
                "k00",
                CubeListBuilder.create().texOffs(102, 84).addBox(0f, 0f, 0f, 5f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(13f, -8f, -10f, 0f, 0.1745f, 0f)
            )

            val k01 = k00.addOrReplaceChild(
                "k01",
                CubeListBuilder.create().texOffs(90, 0).addBox(1f, -18.5f, 1f, 3f, 18f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -0.5236f, 0f, 0f)
            )

            val k02 = k00.addOrReplaceChild(
                "k02",
                CubeListBuilder.create().texOffs(90, 0).addBox(0.8f, -25f, -0.7f, 3f, 18f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -1.3963f, 0f, 0f)
            )

            val k03 = k00.addOrReplaceChild(
                "k03",
                CubeListBuilder.create().texOffs(90, 0).addBox(0.6f, -24.5f, -2.5f, 3f, 18f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -2.0944f, 0f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
