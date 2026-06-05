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
import org.trp.shincolle.entity.EntityDestroyerRo
import org.trp.shincolle.entity.base.EntityShipBase

class ModelDestroyerRo<T : Entity?>(root: ModelPart) : EntityModel<T?>(), IGlowableModel {
    private val Back: ModelPart
    private val NeckBack: ModelPart
    private val Body: ModelPart
    private val TailBack: ModelPart
    private val LegLeftFront: ModelPart
    private val LegRightFront: ModelPart
    private val BodyTurbine: ModelPart
    private val Head: ModelPart
    private val NeckBody: ModelPart
    private val HeadD03: ModelPart
    private val HeadU01: ModelPart
    private val HeadD01: ModelPart
    private val FaceL00: ModelPart
    private val FaceL01: ModelPart
    private val FaceL02: ModelPart
    private val FaceR00: ModelPart
    private val FaceR01: ModelPart
    private val FaceR02: ModelPart
    private val k00: ModelPart
    private val HeadD04: ModelPart
    private val UpperTooth: ModelPart
    private val HeadU02: ModelPart
    private val LowerTooth: ModelPart
    private val k01: ModelPart
    private val k02: ModelPart
    private val k03: ModelPart
    private val tube01: ModelPart
    private val tube02: ModelPart
    private val tube03: ModelPart
    private val TailEnd: ModelPart
    private val TailBack01: ModelPart
    private val TailBack02: ModelPart
    private val LegLeftEnd: ModelPart
    private val LegRightEnd: ModelPart
    private val GlowBack: ModelPart
    private val GlowNeckBack: ModelPart
    private val GlowHead: ModelPart
    private var poseTranslateY = 0f

    init {
        this.Back = root.getChild("Back")
        this.TailBack = this.Back.getChild("TailBack")
        this.TailBack02 = this.TailBack.getChild("TailBack02")
        this.TailBack01 = this.TailBack.getChild("TailBack01")
        this.TailEnd = this.TailBack.getChild("TailEnd")
        this.LegRightFront = this.Back.getChild("LegRightFront")
        this.LegRightEnd = this.LegRightFront.getChild("LegRightEnd")
        this.Body = this.Back.getChild("Body")
        this.LegLeftFront = this.Back.getChild("LegLeftFront")
        this.LegLeftEnd = this.LegLeftFront.getChild("LegLeftEnd")
        this.NeckBack = this.Back.getChild("NeckBack")
        this.Head = this.NeckBack.getChild("Head")
        this.HeadD04 = this.Head.getChild("HeadD04")
        this.HeadU01 = this.Head.getChild("HeadU01")
        this.HeadU02 = this.HeadU01.getChild("HeadU02")
        this.UpperTooth = this.HeadU01.getChild("UpperTooth")
        this.HeadD01 = this.Head.getChild("HeadD01")
        this.LowerTooth = this.HeadD01.getChild("LowerTooth")
        this.HeadD03 = this.NeckBack.getChild("HeadD03")
        this.NeckBody = this.NeckBack.getChild("NeckBody")
        this.tube01 = this.NeckBody.getChild("tube01")
        this.tube03 = this.tube01.getChild("tube03")
        this.tube02 = this.tube01.getChild("tube02")
        this.BodyTurbine = this.Back.getChild("BodyTurbine")
        this.GlowBack = root.getChild("GlowBack")
        this.GlowNeckBack = this.GlowBack.getChild("GlowNeckBack")
        this.GlowHead = this.GlowNeckBack.getChild("GlowHead")
        this.FaceL00 = this.GlowHead.getChild("FaceL00")
        this.FaceL01 = this.GlowHead.getChild("FaceL01")
        this.FaceL02 = this.GlowHead.getChild("FaceL02")
        this.FaceR00 = this.GlowHead.getChild("FaceR00")
        this.FaceR01 = this.GlowHead.getChild("FaceR01")
        this.FaceR02 = this.GlowHead.getChild("FaceR02")
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

        Back.xRot = -0.2618f
        Back.yRot = 0.0f
        Back.zRot = 0.0f
        NeckBack.xRot = 0.0873f
        Head.xRot = 0.3f
        LegRightFront.yRot = 0.0f
        LegLeftFront.yRot = 0.0f

        applyFaceFromEntity(entity)
        applyEquipVisibility(entity)
        applyLook(netHeadYaw, headPitch, angleX)
        if (entity.isInSittingPose()) {
            applySittingPose(entity, angleX)
        } else {
            applyLegPose(entity, limbSwing, limbSwingAmount, angleX)
            applyTailPose(angleX)
        }
        syncGlowParts()
    }

    private fun applyFaceFromEntity(ship: EntityShipBase) {
        val faceId = ship.faceId
        val faceIndex = if (faceId >= 0) faceId % 3 else 0
        setFace(faceIndex)
    }

    private fun setFace(faceIndex: Int) {
        FaceL00.visible = faceIndex == 0
        FaceR00.visible = faceIndex == 0
        FaceL01.visible = faceIndex == 1
        FaceR01.visible = faceIndex == 1
        FaceL02.visible = faceIndex == 2
        FaceR02.visible = faceIndex == 2
    }

    private fun applyEquipVisibility(ship: EntityShipBase) {
        val show = ship.getEquipFlag(EntityDestroyerRo.EQUIP_HEAD_ORNAMENT)
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
            TailBack.xRot = 0.1f
            TailBack.yRot = headYaw * -0.005f
            TailEnd.xRot = 0.1f
            TailEnd.yRot = headYaw * -0.005f
            tube01.xRot = headPitch * -0.005f - 0.8727f
            tube01.yRot = headYaw * -0.005f
        } else {
            Head.xRot = angleX * 0.08f + 0.3f
            HeadD01.xRot = angleX * 0.05f + 0.7f
            NeckBack.xRot = 0.0873f
            NeckBack.yRot = 0.0f
            Head.yRot = 0.0f
            TailBack.yRot = 0.0f
            TailEnd.yRot = 0.0f
            tube01.xRot = -0.8727f
            tube01.yRot = 0.0f
        }
    }

    private fun applyDeadPose(ship: EntityShipBase?) {
        this.poseTranslateY = DEAD_TRANSLATE_Y
        setFace(1)
        HeadD01.xRot = 0.7f
        NeckBack.xRot = 0.0f
        NeckBack.yRot = 0.1f
        Head.xRot = 0.1f
        Head.yRot = 0.1f
        Back.xRot = 0.0f
        Back.yRot = Math.PI.toFloat()
        Back.zRot = Math.PI.toFloat()
        LegRightFront.xRot = 1.57f
        LegRightFront.yRot = -0.52f
        LegLeftFront.xRot = 1.57f
        LegLeftFront.yRot = 0.52f
        LegRightEnd.xRot = 1.0f
        LegLeftEnd.xRot = 1.0f
        TailBack.xRot = 0.1f
        TailBack.yRot = -0.15f
        TailEnd.xRot = 0.1f
        TailEnd.yRot = -0.15f
        tube01.xRot = -0.8f
        tube01.yRot = -0.12f
    }

    private fun applySittingPose(ship: EntityShipBase, angleX: Float) {
        this.poseTranslateY = SITTING_TRANSLATE_Y
        poseTranslateY += 0.45f
        if (ship.emotionSecondary == EntityShipBase.EMOTION_BORED) {
            setFace(2)
            Back.xRot = 0.0f
            Back.yRot = Math.PI.toFloat()
            Back.zRot = Math.PI.toFloat()
            Head.xRot = angleX * 0.08f + 0.35f
            LegRightFront.xRot = angleX * 0.3f + 0.5f
            LegLeftFront.xRot = -angleX * 0.3f + 0.5f
            LegRightEnd.xRot = angleX * 0.3f + 0.5f
            LegLeftEnd.xRot = -angleX * 0.3f + 0.5f
            TailBack.xRot = -0.3f
            TailBack.yRot = angleX * 0.3f
            TailEnd.xRot = -0.3f
            TailEnd.yRot = angleX * 0.5f
            tube01.xRot = -0.8f
        } else {
            Back.xRot = -0.7f
            Head.xRot = angleX * 0.08f + 0.35f
            LegRightFront.xRot = -0.6981f
            LegLeftFront.xRot = -0.6981f
            LegRightEnd.xRot = 0.1745f
            LegLeftEnd.xRot = 0.1745f
            TailBack.xRot = 0.5f
            TailBack.yRot = angleX * 0.3f
            TailEnd.xRot = 0.6f
            TailEnd.yRot = angleX * 0.5f
            tube01.xRot = -0.6f
        }
    }

    private fun applyTailPose(angleX: Float) {
        TailBack.xRot = angleX * 0.1f - 0.1f
        TailEnd.xRot = angleX * 0.25f - 0.1f
    }

    private fun applyLegPose(ship: EntityShipBase, limbSwing: Float, limbSwingAmount: Float, angleX: Float) {
        if (ship.isSprinting || limbSwingAmount > 0.9f) {
            LegRightFront.xRot = Mth.cos(limbSwing * 0.6662f) * 0.4f * limbSwingAmount + 1.0f
            LegLeftFront.xRot = Mth.cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 0.4f * limbSwingAmount + 1.0f
            LegRightEnd.xRot = Mth.sin(limbSwing * 0.6662f) * limbSwingAmount + 0.5f
            LegLeftEnd.xRot = Mth.sin(limbSwing * 0.6662f + Math.PI.toFloat()) * limbSwingAmount + 0.5f
        } else {
            LegRightFront.xRot = angleX * 0.3f + 0.8f
            LegLeftFront.xRot = -angleX * 0.3f + 0.8f
            LegRightEnd.xRot = angleX * 0.3f + 0.5f
            LegLeftEnd.xRot = -angleX * 0.3f + 0.5f
        }
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "destroyer_ro"), "main")
        private val SITTING_TRANSLATE_Y = sittingY("ModelDestroyerRo")
        private val DEAD_TRANSLATE_Y = deadY("ModelDestroyerRo")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val Back = partdefinition.addOrReplaceChild(
                "Back",
                CubeListBuilder.create().texOffs(2, 32).addBox(-12f, -12f, -14f, 24f, 22f, 28f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -16f, 0f, -0.2618f, 0f, 0f)
            )

            val TailBack = Back.addOrReplaceChild(
                "TailBack",
                CubeListBuilder.create().texOffs(12, 38).addBox(-10f, -8f, 0f, 20f, 14f, 22f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2f, 11f, -0.0873f, 0f, 0f)
            )

            val TailBack02 = TailBack.addOrReplaceChild(
                "TailBack02",
                CubeListBuilder.create().texOffs(30, 40).addBox(-2f, 0f, 0f, 4f, 10f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8f, 0f, 15f, -1.0472f, 0f, -0.4014f)
            )

            val TailBack01 = TailBack.addOrReplaceChild(
                "TailBack01",
                CubeListBuilder.create().texOffs(30, 40).addBox(-2f, 0f, 0f, 4f, 10f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, 0f, 15f, -1.0472f, 0f, 0.4014f)
            )

            val TailEnd = TailBack.addOrReplaceChild(
                "TailEnd",
                CubeListBuilder.create().texOffs(14, 36).addBox(-8f, -6.5f, 0f, 16f, 10f, 24f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 19f, -0.0873f, 0f, 0f)
            )

            val LegRightFront = Back.addOrReplaceChild(
                "LegRightFront",
                CubeListBuilder.create().texOffs(20, 104).addBox(-4f, -4f, -4f, 8f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, 12f, -3f, 0.7854f, 0f, 0f)
            )

            val LegRightEnd = LegRightFront.addOrReplaceChild(
                "LegRightEnd",
                CubeListBuilder.create().texOffs(24, 106).addBox(-3f, -3f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12f, 0f, 0.5236f, 0f, 0f)
            )

            val Body = Back.addOrReplaceChild(
                "Body",
                CubeListBuilder.create().texOffs(4, 96).addBox(-8f, 0f, 0f, 16f, 7f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8f, -10f, 0.5236f, 0f, 0f)
            )

            val LegLeftFront = Back.addOrReplaceChild(
                "LegLeftFront",
                CubeListBuilder.create().texOffs(20, 104).addBox(-4f, -4f, -4f, 8f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, 12f, -3f, 0.7854f, 0f, 0f)
            )

            val LegLeftEnd = LegLeftFront.addOrReplaceChild(
                "LegLeftEnd",
                CubeListBuilder.create().texOffs(24, 106).addBox(-3f, -3f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12f, 0f, 0.5236f, 0f, 0f)
            )

            val NeckBack = Back.addOrReplaceChild(
                "NeckBack",
                CubeListBuilder.create().texOffs(8, 40).addBox(-13f, -11f, -20f, 26f, 26f, 22f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3f, -12f, 0.0873f, 0f, 0f)
            )

            val Head = NeckBack.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(6, 42).addBox(-15f, -12f, -16f, 30f, 27f, 18f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -17.5f, 0.2618f, 0f, 0f)
            )

            val HeadD04 = Head.addOrReplaceChild(
                "HeadD04",
                CubeListBuilder.create().texOffs(2, 94).addBox(-8f, 0f, 0f, 16f, 12f, 18f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, -15f, -0.2618f, 0f, 0f)
            )

            val HeadU01 = Head.addOrReplaceChild(
                "HeadU01",
                CubeListBuilder.create().texOffs(6, 40).addBox(-14f, -21f, -9f, 28f, 16f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, -19f, -0.0873f, 0f, 0f)
            )

            val HeadU02 = HeadU01.addOrReplaceChild(
                "HeadU02",
                CubeListBuilder.create().texOffs(6, 40).addBox(-14f, 0f, 0f, 28f, 15f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -20f, -23f, 0.0873f, 0f, 0f)
            )

            val UpperTooth = HeadU01.addOrReplaceChild(
                "UpperTooth",
                CubeListBuilder.create().texOffs(0, 0).addBox(-12f, 0f, 0f, 24f, 10f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -6f, -15f, 0.3491f, 0f, 0f)
            )

            val HeadD01 = Head.addOrReplaceChild(
                "HeadD01",
                CubeListBuilder.create().texOffs(0, 34).addBox(-13f, 1.5f, -25f, 26f, 10f, 28f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1f, -10.3f, 0.6981f, 0f, 0f)
            )

            val LowerTooth = HeadD01.addOrReplaceChild(
                "LowerTooth",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-12f, 0f, 0f, 24f, 10f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9.5f, -5.5f, -3.4907f, 0f, 0f)
            )

            val HeadD03 = NeckBack.addOrReplaceChild(
                "HeadD03",
                CubeListBuilder.create().texOffs(2, 94).addBox(-8.5f, 0f, 0f, 17f, 12f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10.3f, -23f, -0.0524f, 0f, 0f)
            )

            val NeckBody = NeckBack.addOrReplaceChild(
                "NeckBody",
                CubeListBuilder.create().texOffs(0, 94).addBox(-9f, 0f, -9f, 18f, 14f, 18f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, -9f, 0.3491f, 0f, 0f)
            )

            val tube01 = NeckBody.addOrReplaceChild(
                "tube01",
                CubeListBuilder.create().texOffs(31, 40).addBox(-1.5f, 0f, 0f, 3f, 3f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12f, 3f, -0.8727f, 0f, 0f)
            )

            val tube03 = tube01.addOrReplaceChild(
                "tube03",
                CubeListBuilder.create().texOffs(24, 32).addBox(-1f, 0f, 0f, 2f, 2f, 28f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, 1.5f, 18f, 1.0472f, -0.1396f, 0f)
            )

            val tube02 = tube01.addOrReplaceChild(
                "tube02",
                CubeListBuilder.create().texOffs(24, 32).addBox(-1f, 0f, 0f, 2f, 2f, 28f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1f, 1.5f, 18f, 1.0472f, 0.1396f, 0f)
            )

            val BodyTurbine = Back.addOrReplaceChild(
                "BodyTurbine",
                CubeListBuilder.create().texOffs(86, 89).addBox(-4.5f, 0f, 0f, 9f, 9f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, -2f, -0.5236f, 0f, 0f)
            )

            val GlowBack = partdefinition.addOrReplaceChild(
                "GlowBack",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -16f, 0f, -0.2618f, 0f, 0f)
            )

            val GlowNeckBack = GlowBack.addOrReplaceChild(
                "GlowNeckBack",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -3f, -12f, 0.0873f, 0f, 0f)
            )

            val GlowHead = GlowNeckBack.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 0f, -17.5f, 0.2618f, 0f, 0f)
            )

            val FaceL00 = GlowHead.addOrReplaceChild(
                "FaceL00",
                CubeListBuilder.create().texOffs(96, 96).addBox(0f, 0f, 0f, 0f, 16f, 16f, CubeDeformation(0f)),
                PartPose.offset(15.1f, -8f, -16f)
            )

            val FaceL01 = GlowHead.addOrReplaceChild(
                "FaceL01",
                CubeListBuilder.create().texOffs(96, 0).addBox(0f, 0f, 0f, 0f, 16f, 16f, CubeDeformation(0f)),
                PartPose.offset(15.1f, -8f, -16f)
            )

            val FaceL02 = GlowHead.addOrReplaceChild(
                "FaceL02",
                CubeListBuilder.create().texOffs(96, 16).addBox(0f, 0f, 0f, 0f, 16f, 16f, CubeDeformation(0f)),
                PartPose.offset(15.1f, -8f, -16f)
            )

            val FaceR00 = GlowHead.addOrReplaceChild(
                "FaceR00",
                CubeListBuilder.create().texOffs(96, 96).addBox(0f, 0f, 0f, 0f, 16f, 16f, CubeDeformation(0f)),
                PartPose.offset(-15.1f, -8f, -16f)
            )

            val FaceR01 = GlowHead.addOrReplaceChild(
                "FaceR01",
                CubeListBuilder.create().texOffs(96, 0).addBox(0f, 0f, 0f, 0f, 16f, 16f, CubeDeformation(0f)),
                PartPose.offset(-15.1f, -8f, -16f)
            )

            val FaceR02 = GlowHead.addOrReplaceChild(
                "FaceR02",
                CubeListBuilder.create().texOffs(96, 16).addBox(0f, 0f, 0f, 0f, 16f, 16f, CubeDeformation(0f)),
                PartPose.offset(-15.1f, -8f, -16f)
            )

            val k00 = GlowHead.addOrReplaceChild(
                "k00",
                CubeListBuilder.create().texOffs(54, 94).addBox(0f, 0f, 0f, 5f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(12f, -10f, 0f, 0f, 0.1745f, 0f)
            )

            val k01 = k00.addOrReplaceChild(
                "k01",
                CubeListBuilder.create().texOffs(72, 102).addBox(1f, -18.5f, 1f, 3f, 18f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -0.5236f, 0f, 0f)
            )

            val k02 = k00.addOrReplaceChild(
                "k02",
                CubeListBuilder.create().texOffs(72, 102).addBox(0.8f, -25f, -0.7f, 3f, 18f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -1.3963f, 0f, 0f)
            )

            val k03 = k00.addOrReplaceChild(
                "k03",
                CubeListBuilder.create().texOffs(72, 102).addBox(0.6f, -24.5f, -2.5f, 3f, 18f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -2.0944f, 0f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
