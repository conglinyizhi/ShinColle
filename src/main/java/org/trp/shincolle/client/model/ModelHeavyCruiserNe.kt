package org.trp.shincolle.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Minecraft
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
import org.trp.shincolle.entity.base.EntityShipBase

class ModelHeavyCruiserNe<T : EntityShipBase?>(root: ModelPart) : ShipModelHumanoidBase<T?>(), IGlowableModel {
    private var poseTranslateY = 0f
    private val headDefaultY: Float
    private val glowHeadDefaultY: Float
    private val armLeft01DefaultZ: Float
    private val armRight01DefaultZ: Float

    private val BodyMain: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val LegLeft01: ModelPart
    private val LegRight01: ModelPart
    private val Neck: ModelPart
    private val Head: ModelPart
    private val Cloth01: ModelPart
    private val TailBase: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmRight02: ModelPart
    private val LegLeft02: ModelPart
    private val LegRight02: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val Ear01: ModelPart
    private val Ear02: ModelPart
    private val Ahoke: ModelPart
    private val Hair01: ModelPart
    private val Hair02: ModelPart
    private val Hair03: ModelPart
    private val TailL01: ModelPart
    private val TailR01: ModelPart
    private val TailL02: ModelPart
    private val TailL03: ModelPart
    private val TailL04: ModelPart
    private val TailL05: ModelPart
    private val TailL06: ModelPart
    private val TailLHead01: ModelPart
    private val TailLHead02: ModelPart
    private val TailLC01: ModelPart
    private val TailLC02: ModelPart
    private val TailLC03: ModelPart
    private val TailR02: ModelPart
    private val TailR03: ModelPart
    private val TailR04: ModelPart
    private val TailR05: ModelPart
    private val TailR06: ModelPart
    private val TailRHead01: ModelPart
    private val TailRHead02: ModelPart
    private val TailRC01: ModelPart
    private val TailRC02: ModelPart
    private val TailRC03: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowHead: ModelPart

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.LegLeft01 = this.BodyMain.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.Neck = this.BodyMain.getChild("Neck")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.TailBase = this.BodyMain.getChild("TailBase")
        this.TailL01 = this.TailBase.getChild("TailL01")
        this.TailL02 = this.TailL01.getChild("TailL02")
        this.TailL03 = this.TailL02.getChild("TailL03")
        this.TailL04 = this.TailL03.getChild("TailL04")
        this.TailL05 = this.TailL04.getChild("TailL05")
        this.TailL06 = this.TailL05.getChild("TailL06")
        this.TailLHead01 = this.TailL06.getChild("TailLHead01")
        this.TailLC01 = this.TailLHead01.getChild("TailLC01")
        this.TailLC02 = this.TailLHead01.getChild("TailLC02")
        this.TailLC03 = this.TailLHead01.getChild("TailLC03")
        this.TailLHead02 = this.TailL06.getChild("TailLHead02")
        this.TailR01 = this.TailBase.getChild("TailR01")
        this.TailR02 = this.TailR01.getChild("TailR02")
        this.TailR03 = this.TailR02.getChild("TailR03")
        this.TailR04 = this.TailR03.getChild("TailR04")
        this.TailR05 = this.TailR04.getChild("TailR05")
        this.TailR06 = this.TailR05.getChild("TailR06")
        this.TailRHead01 = this.TailR06.getChild("TailRHead01")
        this.TailRC02 = this.TailRHead01.getChild("TailRC02")
        this.TailRC01 = this.TailRHead01.getChild("TailRC01")
        this.TailRC03 = this.TailRHead01.getChild("TailRC03")
        this.TailRHead02 = this.TailR06.getChild("TailRHead02")
        this.LegRight01 = this.BodyMain.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.Head = this.BodyMain.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.Ear02 = this.Head.getChild("Ear02")
        this.Ear01 = this.Head.getChild("Ear01")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair02 = this.HairMain.getChild("Hair02")
        this.Hair03 = this.Hair02.getChild("Hair03")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowHead = this.GlowBodyMain.getChild("GlowHead")
        this.initFaceParts(this.GlowHead)
        this.headDefaultY = this.Head.y
        this.glowHeadDefaultY = this.GlowHead.y
        this.armLeft01DefaultZ = this.ArmLeft01.z
        this.armRight01DefaultZ = this.ArmRight01.z
    }

    override fun setupAnim(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        this.poseTranslateY = 0.4f
        this.Head.y = this.headDefaultY
        this.GlowHead.y = this.glowHeadDefaultY
        this.ArmLeft01.z = this.armLeft01DefaultZ
        this.ArmRight01.z = this.armRight01DefaultZ
        if (entity !is EntityShipBase) {
            return
        }

        if (entity.isInDeadPose) {
            this.applyDeadPose()
            this.GlowBodyMain.copyFrom(this.BodyMain)
            this.GlowHead.copyFrom(this.Head)
            return
        }

        val angleX = Mth.cos(ageInTicks * 0.08f + limbSwing * 0.25f)
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f)
        val angleX2 = Mth.cos(ageInTicks * 0.08f + 0.6f + limbSwing * 0.5f)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount
        var addk1: Float
        var addk2: Float

        if (entity.shipDepth > 0.0f) {
            this.poseTranslateY += angleX * 0.05f + 0.025f
        }

        addk1 = angleAdd1 * 0.5f - 0.14f
        addk2 = angleAdd2 * 0.5f + 0.14f
        this.ArmRight01.xRot = addk1
        this.ArmLeft01.xRot = addk2
        this.Head.xRot = headPitch * 0.014f
        this.Head.yRot = netHeadYaw * 0.01f
        this.Ahoke.yRot = angleX * 0.25f + 0.45f
        this.BodyMain.xRot = 0.0f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Hair02.xRot = angleX1 * 0.04f + 0.21f
        this.Hair02.zRot = 0.0f
        this.Hair03.xRot = angleX2 * 0.07f - 0.2618f
        this.Hair03.zRot = 0.0f
        this.ArmLeft01.zRot = 0.21f
        this.ArmLeft02.zRot = 0.0f
        this.ArmRight01.zRot = -0.21f
        this.ArmRight02.zRot = 0.0f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.1745f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.zRot = 0.0f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.1745f
        this.LegRight02.xRot = 0.0f
        this.LegRight02.zRot = 0.0f
        this.TailBase.xRot = 0.8f
        this.TailL01.xRot = 0.2618f
        this.TailL01.yRot = Mth.cos(-ageInTicks * 0.1f + 0.7f) * 0.2f + 0.5f
        this.TailL01.zRot = this.TailL01.yRot * 0.25f
        this.TailL02.xRot = 0.2618f
        this.TailL02.yRot = Mth.cos(-ageInTicks * 0.1f + 1.4f) * 0.25f
        this.TailL02.zRot = this.TailL02.yRot * 0.25f
        this.TailL03.xRot = 0.2618f
        this.TailL03.yRot = Mth.cos(-ageInTicks * 0.1f + 2.1f) * 0.3f
        this.TailL03.zRot = this.TailL03.yRot * 0.25f
        this.TailL04.xRot = 0.35f
        this.TailL04.yRot = Mth.cos(-ageInTicks * 0.1f + 2.8f) * 0.35f
        this.TailL04.zRot = this.TailL04.yRot * 0.25f
        this.TailL05.xRot = 0.4f
        this.TailL05.yRot = Mth.cos(-ageInTicks * 0.1f + 3.5f) * 0.4f
        this.TailL05.zRot = this.TailL05.yRot * 0.25f
        this.TailL06.xRot = 0.45f
        this.TailL06.yRot = Mth.cos(-ageInTicks * 0.1f + 4.2f) * 0.35f
        this.TailL06.zRot = this.TailL06.yRot * 0.25f
        this.TailR01.xRot = 0.2618f
        this.TailR01.yRot = Mth.cos(-ageInTicks * 0.1f + 0.7f) * 0.2f - 0.5f
        this.TailR01.zRot = this.TailR01.yRot * 0.25f
        this.TailR02.xRot = 0.2618f
        this.TailR02.yRot = Mth.cos(-ageInTicks * 0.1f + 1.4f) * 0.25f
        this.TailR02.zRot = this.TailR02.yRot * 0.25f
        this.TailR03.xRot = 0.2618f
        this.TailR03.yRot = Mth.cos(-ageInTicks * 0.1f + 2.1f) * 0.3f
        this.TailR03.zRot = this.TailR03.yRot * 0.25f
        this.TailR04.xRot = 0.35f
        this.TailR04.yRot = Mth.cos(-ageInTicks * 0.1f + 2.8f) * 0.35f
        this.TailR04.zRot = this.TailR04.yRot * 0.25f
        this.TailR05.xRot = 0.4f
        this.TailR05.yRot = Mth.cos(-ageInTicks * 0.1f + 3.5f) * 0.4f
        this.TailR05.zRot = this.TailR05.yRot * 0.25f
        this.TailR06.xRot = 0.45f
        this.TailR06.yRot = Mth.cos(-ageInTicks * 0.1f + 4.2f) * 0.45f
        this.TailR06.zRot = this.TailR06.yRot * 0.25f

        var modf2 = ageInTicks % 128.0f
        if (modf2 < 6.0f) {
            if (modf2 >= 3.0f) {
                modf2 -= 3.0f
            }
            val anglef2 = Mth.sin(modf2 * 1.0472f) * 0.25f
            this.Ear01.zRot = anglef2 + 0.1745f
            this.Ear02.zRot = -anglef2 - 0.1745f
        } else {
            this.Ear01.zRot = 0.1745f
            this.Ear02.zRot = -0.1745f
        }

        if (entity.getIsSprinting() || limbSwingAmount > 0.8f) {
            addk1 *= 2.0f
            addk2 *= 2.0f
            this.ArmRight01.xRot = addk1
            this.ArmLeft01.xRot = addk2
        }

        this.Head.zRot = entity.getHeadTiltAngle(ageInTicks)
        if (entity.isCrouching()) {
            this.Head.y = this.headDefaultY + (0.2f * OFFSET_SCALE)
            this.GlowHead.y = this.glowHeadDefaultY + (0.2f * OFFSET_SCALE)
        }

        if (entity.getIsSitting() || entity.isPassenger()) {
            if (entity.getStateEmotion(1) == 4) {
                this.poseTranslateY += 0.22f * 5
                this.Head.xRot = 1.5359f
                this.Head.y = this.headDefaultY + (0.25f * OFFSET_SCALE)
                this.GlowHead.xRot = 1.5359f
                this.GlowHead.y = this.glowHeadDefaultY + (0.25f * OFFSET_SCALE)
                addk1 = 1.5359f
                addk2 = 1.5359f
                this.ArmLeft01.xRot = -1.5359f
                this.ArmLeft01.zRot = 0.0f
                this.ArmLeft01.z = this.armLeft01DefaultZ + (-0.18f * OFFSET_SCALE)
                this.ArmRight01.xRot = -1.5359f
                this.ArmRight01.zRot = 0.0f
                this.ArmRight01.z = this.armRight01DefaultZ + (-0.18f * OFFSET_SCALE)
                this.TailBase.xRot = 0.0873f
                this.TailL01.xRot = 0.02618f
                this.TailL01.yRot *= 0.5f
                this.TailL02.xRot = -0.02618f
                this.TailL02.yRot *= 0.5f
                this.TailL03.xRot = -0.02618f
                this.TailL03.yRot *= 0.5f
                this.TailL04.xRot = -0.035f
                this.TailL04.yRot *= 0.5f
                this.TailL05.xRot = -0.04f
                this.TailL05.yRot *= 0.5f
                this.TailL06.xRot = -0.045f
                this.TailL06.yRot *= 0.5f
                this.TailR01.xRot = -0.02618f
                this.TailR01.yRot *= 0.5f
                this.TailR02.xRot = -0.02618f
                this.TailR02.yRot *= 0.5f
                this.TailR03.xRot = -0.02618f
                this.TailR03.yRot *= 0.5f
                this.TailR04.xRot = -0.035f
                this.TailR04.yRot *= 0.5f
                this.TailR05.xRot = -0.04f
                this.TailR05.yRot *= 0.5f
                this.TailR06.xRot = -0.045f
                this.TailR06.yRot *= 0.5f
            } else {
                this.poseTranslateY += 0.22f * 5
                this.Head.xRot -= 0.5f
                this.GlowHead.xRot -= 0.5f
                this.Head.y = this.headDefaultY + (0.25f * OFFSET_SCALE)
                this.GlowHead.y = this.glowHeadDefaultY + (0.25f * OFFSET_SCALE)
                addk1 = 1.5359f
                addk2 = 1.5359f
                this.ArmLeft01.xRot = -1.5359f
                this.ArmLeft01.zRot = 0.0f
                this.ArmLeft01.z = this.armLeft01DefaultZ + (-0.18f * OFFSET_SCALE)
                this.ArmLeft02.zRot = 1.1868f
                this.ArmRight01.xRot = -1.5359f
                this.ArmRight01.zRot = 0.0f
                this.ArmRight01.z = this.armRight01DefaultZ + (-0.18f * OFFSET_SCALE)
                this.ArmRight02.zRot = -1.1868f
            }
        }

        if (entity.attackTick > 20) {
            this.TailL01.xRot = 0.2618f
            this.TailL01.yRot = 0.2618f
            this.TailL01.zRot = 0.0f
            this.TailL02.xRot = 0.35f
            this.TailL02.yRot = 0.1748f
            this.TailL02.zRot = 0.0f
            this.TailL03.xRot = 0.4363f
            this.TailL03.yRot = 0.14f
            this.TailL03.zRot = 0.0f
            this.TailL04.xRot = 0.5236f
            this.TailL04.yRot = 0.14f
            this.TailL04.zRot = 0.0f
            this.TailL05.xRot = 0.6109f
            this.TailL05.yRot = 0.1745f
            this.TailL05.zRot = 0.0f
            this.TailL06.xRot = 0.35f
            this.TailL06.yRot = 0.0f
            this.TailL06.zRot = 0.0f
            this.TailR01.xRot = 0.2618f
            this.TailR01.yRot = -0.2618f
            this.TailR01.zRot = 0.0f
            this.TailR02.xRot = 0.35f
            this.TailR02.yRot = -0.1748f
            this.TailR02.zRot = 0.0f
            this.TailR03.xRot = 0.35f
            this.TailR03.yRot = -0.14f
            this.TailR03.zRot = 0.0f
            this.TailR04.xRot = 0.4363f
            this.TailR04.yRot = -0.14f
            this.TailR04.zRot = 0.0f
            this.TailR05.xRot = 0.4363f
            this.TailR05.yRot = -0.14f
            this.TailR05.zRot = 0.0f
            this.TailR06.xRot = 0.35f
            this.TailR06.yRot = 0.0f
            this.TailR06.zRot = 0.0f
        }

        val partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
        val customAttackAnim = if (entity != null) entity.getCustomAttackAnim(partialTick) else 0.0f
        if (customAttackAnim > 0.0f) {
            val f7 = Mth.sin(customAttackAnim * customAttackAnim * Math.PI.toFloat())
            val f8 = Mth.sin(Mth.sqrt(customAttackAnim) * Math.PI.toFloat())
            this.ArmRight01.xRot = -0.6f - f8 * 80.0f * (Math.PI.toFloat() / 180f)
            this.ArmRight01.yRot = 0.0f - f7 * 20.0f * (Math.PI.toFloat() / 180f) + 0.2f
            this.ArmRight01.zRot = 0.2f - -f8 * 20.0f * (Math.PI.toFloat() / 180f)
        }

        val headZ = this.Head.zRot * -0.5f
        val headX = this.Head.xRot * -0.5f - 0.05f
        this.Hair02.xRot += headX * 0.5f
        this.Hair03.xRot += headX * 0.2f
        this.Hair02.zRot += headZ * 0.8f
        this.Hair03.zRot += headZ * 0.4f
        this.LegLeft01.xRot = addk1
        this.LegRight01.xRot = addk2
        applyFaceAndMouth(entity)
        this.GlowBodyMain.copyFrom(this.BodyMain)
        this.GlowHead.copyFrom(this.Head)
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        val usePoseTranslate = this.poseTranslateY != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, this.poseTranslateY, 0.0f)
        }

        BodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

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
        val usePoseTranslate = this.poseTranslateY != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, this.poseTranslateY, 0.0f)
        }

        GlowBodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    private fun applyDeadPose() {
        this.poseTranslateY += 0.2f * 5
        this.Head.xRot = 0.7853f
        this.Head.yRot = 0.0f
        this.Ahoke.yRot = 0.45f
        this.BodyMain.xRot = 0.0f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = -1.4835f
        this.Head.y = this.headDefaultY + (0.0f * OFFSET_SCALE)
        this.GlowHead.y = this.glowHeadDefaultY + (0.0f * OFFSET_SCALE)
        this.Hair02.xRot = 0.21f
        this.Hair02.zRot = 0.0f
        this.Hair03.xRot = -0.2618f
        this.Hair03.zRot = 0.0f
        this.ArmLeft01.xRot = 0.1745f
        this.ArmLeft01.zRot = 0.4537f
        this.ArmLeft01.z = this.armLeft01DefaultZ + (0.0f * OFFSET_SCALE)
        this.ArmLeft02.zRot = 0.0f
        this.ArmRight01.xRot = -0.1745f
        this.ArmRight01.zRot = -0.05f
        this.ArmRight01.z = this.armRight01DefaultZ + (0.0f * OFFSET_SCALE)
        this.ArmRight02.zRot = 0.0f
        this.LegLeft01.xRot = -0.1745f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.4537f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.zRot = 0.0f
        this.LegRight01.xRot = 0.1745f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.05f
        this.LegRight02.xRot = 0.0f
        this.LegRight02.zRot = 0.0f
        this.TailBase.xRot = 0.8f
        this.TailL01.xRot = 0.2618f
        this.TailL01.yRot = -0.2f
        this.TailL01.zRot = this.TailL01.yRot * 0.25f
        this.TailL02.xRot = 0.2618f
        this.TailL02.yRot = -0.3f
        this.TailL02.zRot = this.TailL02.yRot * 0.25f
        this.TailL03.xRot = 0.2618f
        this.TailL03.yRot = -0.2f
        this.TailL03.zRot = this.TailL03.yRot * 0.25f
        this.TailL04.xRot = 0.35f
        this.TailL04.yRot = 0.2f
        this.TailL04.zRot = this.TailL04.yRot * 0.25f
        this.TailL05.xRot = 0.4f
        this.TailL05.yRot = 0.2f
        this.TailL05.zRot = this.TailL05.yRot * 0.25f
        this.TailL06.xRot = 0.45f
        this.TailL06.yRot = 0.1f
        this.TailL06.zRot = this.TailL06.yRot * 0.25f
        this.TailR01.xRot = 0.6f
        this.TailR01.yRot = 0.2617f
        this.TailR01.zRot = this.TailR01.yRot * 0.25f
        this.TailR02.xRot = 0.6f
        this.TailR02.yRot = -0.2f
        this.TailR02.zRot = this.TailR02.yRot * 0.25f
        this.TailR03.xRot = 0.5f
        this.TailR03.yRot = -0.1f
        this.TailR03.zRot = this.TailR03.yRot * 0.25f
        this.TailR04.xRot = 0.3f
        this.TailR04.yRot = -0.1f
        this.TailR04.zRot = this.TailR04.yRot * 0.25f
        this.TailR05.xRot = 0.1f
        this.TailR05.yRot = 0.1f
        this.TailR05.zRot = this.TailR05.yRot * 0.25f
        this.TailR06.xRot = -0.1f
        this.TailR06.yRot = 0.1f
        this.TailR06.zRot = this.TailR06.yRot * 0.25f
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "heavy_cruiser_ne"), "main")

        private const val OFFSET_SCALE = 16.0f

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 93).addBox(-5.5f, -4.5f, -12f, 11f, 10f, 24f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val LegLeft01 = BodyMain.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(48, 92).addBox(-2.5f, 0f, -2.5f, 5f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, 3f, 8.3f, 0.1396f, 0f, 0.1745f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(48, 105).addBox(-2.5f, 0f, 0f, 5f, 7f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 8f, -2.5f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(0, 78).addBox(-5f, -2f, -4.5f, 10f, 5f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -4f, -9.4f, 0.4189f, 0f, 0f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(0, 92).addBox(-2.5f, 0f, -2.5f, 5f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, 3f, -6f, -0.1396f, 0f, 0.2094f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(0, 105).addBox(-5f, 0f, -5f, 5f, 7f, 5f, CubeDeformation(0f)),
                PartPose.offset(2.5f, 8f, 2.5f)
            )

            val TailBase = BodyMain.addOrReplaceChild(
                "TailBase",
                CubeListBuilder.create().texOffs(98, 0).addBox(-4f, -4f, 0f, 8f, 8f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.5f, 9f, 0.7854f, 0f, 0f)
            )

            val TailL01 = TailBase.addOrReplaceChild(
                "TailL01",
                CubeListBuilder.create().texOffs(98, 0).addBox(-3f, -3f, 0f, 6f, 6f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.5f, 0f, 6f, 0.2618f, 0.4189f, 0f)
            )

            val TailL02 = TailL01.addOrReplaceChild(
                "TailL02",
                CubeListBuilder.create().texOffs(95, 3).addBox(-3f, -3f, 0f, 6f, 6f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.2618f, 0.3142f, 0f)
            )

            val TailL03 = TailL02.addOrReplaceChild(
                "TailL03",
                CubeListBuilder.create().texOffs(95, 1).addBox(-3.5f, -3.5f, 0f, 7f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.2618f, 0.2443f, 0f)
            )

            val TailL04 = TailL03.addOrReplaceChild(
                "TailL04",
                CubeListBuilder.create().texOffs(97, 3).addBox(-3.5f, -3.5f, 0f, 7f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.2618f, 0.2094f, 0f)
            )

            val TailL05 = TailL04.addOrReplaceChild(
                "TailL05",
                CubeListBuilder.create().texOffs(95, 2).addBox(-4f, -3.5f, 0f, 8f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.2618f, 0.1396f, 0f)
            )

            val TailL06 = TailL05.addOrReplaceChild(
                "TailL06",
                CubeListBuilder.create().texOffs(89, 0).addBox(-4.5f, -3.5f, 0f, 9f, 7f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.2618f, 0.0698f, 0f)
            )

            val TailLHead01 = TailL06.addOrReplaceChild(
                "TailLHead01",
                CubeListBuilder.create().texOffs(76, 18).addBox(-5.5f, -2f, 0f, 11f, 6f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -2.5f, -0.1222f, 0f, 0f)
            )

            val TailLC01 = TailLHead01.addOrReplaceChild(
                "TailLC01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.2f, 13.5f, -0.182f, 0f, 0f)
            )

            val TailLC02 = TailLHead01.addOrReplaceChild(
                "TailLC02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3f, 2f, 13.5f, -0.0911f, -0.0873f, 0f)
            )

            val TailLC03 = TailLHead01.addOrReplaceChild(
                "TailLC03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3f, 2f, 13.5f, -0.1367f, 0.0873f, 0f)
            )

            val TailLHead02 = TailL06.addOrReplaceChild(
                "TailLHead02",
                CubeListBuilder.create().texOffs(22, 27).addBox(-5f, -4f, 0f, 10f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 1.5f, 0.0873f, 0f, 0f)
            )

            val TailR01 = TailBase.addOrReplaceChild(
                "TailR01",
                CubeListBuilder.create().texOffs(101, 0).mirror().addBox(-3f, -3f, 0f, 6f, 6f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.5f, 0f, 6f, 0.2618f, -0.0698f, 0f)
            )

            val TailR02 = TailR01.addOrReplaceChild(
                "TailR02",
                CubeListBuilder.create().texOffs(102, 3).mirror().addBox(-3f, -3f, 0f, 6f, 6f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.2618f, 0f, 0f)
            )

            val TailR03 = TailR02.addOrReplaceChild(
                "TailR03",
                CubeListBuilder.create().texOffs(97, 2).mirror()
                    .addBox(-3.5f, -3.5f, 0f, 7f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.3142f, 0.0698f, 0f)
            )

            val TailR04 = TailR03.addOrReplaceChild(
                "TailR04",
                CubeListBuilder.create().texOffs(100, 2).mirror()
                    .addBox(-3.5f, -3.5f, 0f, 7f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.4189f, 0.1396f, 0f)
            )

            val TailR05 = TailR04.addOrReplaceChild(
                "TailR05",
                CubeListBuilder.create().texOffs(97, 0).mirror()
                    .addBox(-4f, -3.5f, 0f, 8f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.5236f, 0.1396f, 0f)
            )

            val TailR06 = TailR05.addOrReplaceChild(
                "TailR06",
                CubeListBuilder.create().texOffs(89, 1).mirror()
                    .addBox(-4.5f, -3.5f, 0f, 9f, 7f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.2618f, 0.1396f, 0f)
            )

            val TailRHead01 = TailR06.addOrReplaceChild(
                "TailRHead01",
                CubeListBuilder.create().texOffs(76, 18).mirror()
                    .addBox(-5.5f, -2f, 0f, 11f, 6f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -2.5f, -0.1222f, 0f, 0f)
            )

            val TailRC02 = TailRHead01.addOrReplaceChild(
                "TailRC02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3f, 2f, 13.5f, -0.0911f, -0.0873f, 0f)
            )

            val TailRC01 = TailRHead01.addOrReplaceChild(
                "TailRC01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.2f, 13.5f, -0.182f, 0f, 0f)
            )

            val TailRC03 = TailRHead01.addOrReplaceChild(
                "TailRC03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3f, 2f, 13.5f, -0.1367f, 0.0873f, 0f)
            )

            val TailRHead02 = TailR06.addOrReplaceChild(
                "TailRHead02",
                CubeListBuilder.create().texOffs(22, 27).mirror()
                    .addBox(-5f, -4f, 0f, 10f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 1.5f, 0.0873f, 0f, 0f)
            )

            val LegRight01 = BodyMain.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(48, 92).mirror()
                    .addBox(-2.5f, 0f, -2.5f, 5f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, 3f, 8.3f, -0.1396f, 0f, -0.1745f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(48, 105).mirror()
                    .addBox(-2.5f, 0f, 0f, 5f, 7f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 8f, -2.5f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(0, 92).mirror()
                    .addBox(-2.5f, 0f, -2.5f, 5f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, 3f, -6f, 0.1396f, 0f, -0.2094f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(0, 105).mirror().addBox(0f, 0f, -5f, 5f, 7f, 5f, CubeDeformation(0f)),
                PartPose.offset(-2.5f, 8f, 2.5f)
            )

            val Head = BodyMain.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 65).addBox(-7f, -11f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -13f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 40).addBox(-8f, -8f, -7.2f, 16f, 17f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -4f, 0f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(104, 29).addBox(0f, -4f, -11.5f, 0f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -8.5f, -5f, 0f, 0.5236f, 0f)
            )

            val Ear02 = Head.addOrReplaceChild(
                "Ear02",
                CubeListBuilder.create().texOffs(0, 26).mirror().addBox(-2f, 0f, -7f, 4f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.2f, -11f, 6.8f, -0.8378f, 0.1222f, -0.1745f)
            )

            val Ear01 = Head.addOrReplaceChild(
                "Ear01",
                CubeListBuilder.create().texOffs(0, 26).addBox(-2f, 0f, -7f, 4f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.2f, -11f, 6.8f, -0.8378f, -0.1222f, 0.1745f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(0, 56).addBox(-7.5f, 0f, 0f, 15f, 12f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.5f, -3f)
            )

            val Hair02 = HairMain.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(78, 92).addBox(-2f, 0f, -3.5f, 3f, 10f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.3f, 4.7f, 2f, 0.2094f, 0f, 0.1745f)
            )

            val Hair03 = Hair02.addOrReplaceChild(
                "Hair03",
                CubeListBuilder.create().texOffs(80, 109).addBox(-2f, 0f, -3f, 3f, 12f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 7.5f, -0.3f, -0.2618f, 0f, -0.2618f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(0, 40).addBox(-7.5f, 0f, 0f, 15f, 7f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1.6f, 0.3491f, 0f, 0f)
            )

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(42, 39).addBox(-4f, 0f, 0f, 8f, 9f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1f, -13f, -0.0873f, 0f, 0f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 0f)
            )

            val GlowHead = GlowBodyMain.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -6f, -13f)
            )
            addFaceLayerCAHime(GlowHead)

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
