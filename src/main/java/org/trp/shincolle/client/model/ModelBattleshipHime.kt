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
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelBattleshipHime<T : EntityShipBase>(root: ModelPart) : ShipModelHumanoidBase<T>(), IGlowableModel {
    private var isDeadPose = false
    private var isSittingPose = false
    override var poseTranslateY = 0f
    private var poseTranslateZ = 0f

    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val Butt: ModelPart
    private val Cloth01: ModelPart
    private val Head: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val HeadHL: ModelPart
    private val HeadHR: ModelPart
    private val Ahoke: ModelPart
    private val HairL01: ModelPart
    private val HairR01: ModelPart
    private val HairL02: ModelPart
    private val HairL03: ModelPart
    private val HairR02: ModelPart
    private val HairR03: ModelPart
    private val Hair01: ModelPart
    private val Hair02: ModelPart
    private val Hair03: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmRight02: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft01: ModelPart
    private val LegRight02: ModelPart
    private val ClothR02: ModelPart
    private val ClothR03: ModelPart
    private val LegLeft02: ModelPart
    private val ClothL02: ModelPart
    private val ClothL03: ModelPart
    private val GlowBodyMain: ModelPart?
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val buttDefaultZ: Float
    private val armLeft02DefaultX: Float
    private val armLeft02DefaultY: Float
    private val armLeft02DefaultZ: Float
    private val armRight02DefaultX: Float
    private val armRight02DefaultY: Float
    private val armRight02DefaultZ: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.ClothL02 = this.LegLeft01.getChild("ClothL02")
        this.ClothL03 = this.LegLeft01.getChild("ClothL03")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.ClothR02 = this.LegRight01.getChild("ClothR02")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.ClothR03 = this.LegRight01.getChild("ClothR03")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.HairR03 = this.HairR02.getChild("HairR03")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.HairL03 = this.HairL02.getChild("HairL03")
        this.HeadHL = this.Head.getChild("HeadHL")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.Hair03 = this.Hair02.getChild("Hair03")
        this.HeadHR = this.Head.getChild("HeadHR")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        initFaceParts(this.GlowHead)
        this.buttDefaultZ = this.Butt.z
        this.armLeft02DefaultX = this.ArmLeft02.x
        this.armLeft02DefaultY = this.ArmLeft02.y
        this.armLeft02DefaultZ = this.ArmLeft02.z
        this.armRight02DefaultX = this.ArmRight02.x
        this.armRight02DefaultY = this.ArmRight02.y
        this.armRight02DefaultZ = this.ArmRight02.z
    }

    override fun setupAnim(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val ctx = computePoseContext(entity, limbSwing, limbSwingAmount, ageInTicks, 0.0f)
        applyFaceAndMouth(entity)
        setFlushVisible(
            entity != null && (entity.getEmotionPrimary() == EntityShipBase.EMOTION_SHY
                    || entity.getEmotionPrimary() == EntityShipBase.EMOTION_HAPPY)
        )
        this.resetOffsets()
        this.applyEquipVisibility(entity)

        if (entity is EntityShipBase && entity.isInDeadPose) {
            this.applyDeadPose()
            this.syncGlowParts()
            return
        }

        this.applyBasePose(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch)
        this.applySpecialPoseAdjustments(entity, ctx, limbSwing, limbSwingAmount, ageInTicks)
        this.syncGlowParts()
    }

    private fun resetOffsets() {
        this.isDeadPose = false
        this.isSittingPose = false
        this.poseTranslateY = 0.0f
        this.poseTranslateZ = 0.0f

        this.Butt.z = this.buttDefaultZ
        this.ArmLeft02.x = this.armLeft02DefaultX
        this.ArmLeft02.y = this.armLeft02DefaultY
        this.ArmLeft02.z = this.armLeft02DefaultZ
        this.ArmRight02.x = this.armRight02DefaultX
        this.ArmRight02.y = this.armRight02DefaultY
        this.ArmRight02.z = this.armRight02DefaultZ
    }

    private fun applyEquipVisibility(entity: T?) {
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        this.Head.xRot = 0.0f
        this.Head.yRot = 0.0f
        this.Head.zRot = 0.0f
        this.BoobL.xRot = -0.7f
        this.BoobR.xRot = -0.7f
        this.BodyMain.zRot = 0.0f
        this.Hair01.xRot = 0.26f
        this.Hair02.xRot = -0.08f
        this.Hair03.xRot = -0.14f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft02.xRot = 0.0f
        this.ArmRight02.xRot = 0.0f
        this.LegLeft01.yRot = 0.0f
        this.LegRight01.yRot = 0.0f

        this.Head.xRot += 0.14f
        this.BodyMain.xRot = 0.4f
        this.Butt.xRot = -0.4f
        this.Butt.z = this.buttDefaultZ + (0.19f * OFFSET_SCALE)
        this.BoobL.xRot -= 0.2f
        this.BoobR.xRot -= 0.2f
        this.ArmLeft01.xRot = -1.3f
        this.ArmLeft01.zRot = -0.1f
        this.ArmLeft02.zRot = 1.15f
        this.ArmRight01.xRot = -1.3f
        this.ArmRight01.zRot = 0.1f
        this.ArmRight02.zRot = -1.4f

        this.LegLeft01.zRot = -0.2f
        this.LegLeft02.xRot = 1.34f
        this.LegRight01.zRot = 0.2f
        this.LegRight02.xRot = 1.13f

        this.Hair01.xRot -= 0.2f
        this.Hair02.xRot -= 0.2f
        this.Hair03.xRot -= 0.1f

        val headZ = this.Head.zRot * -0.5f
        this.Hair01.zRot = headZ
        this.Hair02.zRot = headZ
        this.HairL01.zRot = headZ
        this.HairL02.zRot = headZ + 0.087f
        this.HairR01.zRot = headZ
        this.HairR02.zRot = headZ - 0.052f

        val headX = this.Head.xRot * -0.5f
        this.HairL01.xRot = headX - 0.1f
        this.HairL02.xRot = headX - 0.3f
        this.HairL03.xRot = headX
        this.HairR01.xRot = headX - 0.1f
        this.HairR02.xRot = headX - 0.3f
        this.HairR03.xRot = headX

        this.LegLeft01.xRot = -2.1232f
        this.LegRight01.xRot = -2.0708f
    }

    private fun applyBasePose(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val angleX = Mth.cos(ageInTicks * 0.08f)
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f)
        val angleX2 = Mth.cos(ageInTicks * 0.08f + 0.6f + limbSwing * 0.5f)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount * 0.7f
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount * 0.7f

        this.poseTranslateY = BASE_TRANSLATE_Y
        if (entity!!.shipDepth > 0.0) {
            this.poseTranslateY += angleX * 0.05f + 0.025f
        }

        this.Head.xRot = headPitch * 0.014f + 0.05f
        this.Head.yRot = netHeadYaw * 0.01f
        this.BoobL.xRot = angleX * 0.06f - 0.7f
        this.BoobR.xRot = angleX * 0.06f - 0.7f
        this.Ahoke.zRot = angleX * 0.02f - 0.02f
        this.BodyMain.xRot = -0.1f
        this.Butt.xRot = 0.2618f

        this.Hair01.xRot = angleX * 0.03f + 0.15f
        this.Hair02.xRot = -angleX1 * 0.04f - 0.05f
        this.Hair03.xRot = -angleX2 * 0.07f - 0.08f
        this.Hair01.zRot = 0.0f
        this.Hair02.zRot = 0.0f
        this.Hair03.zRot = 0.0f

        this.HairL01.xRot = angleX * 0.02f - 0.14f
        this.HairL02.xRot = -angleX1 * 0.04f + 0.08f
        this.HairL03.xRot = -angleX2 * 0.07f + 0.1f
        this.HairR01.xRot = angleX * 0.02f - 0.14f
        this.HairR02.xRot = -angleX1 * 0.04f + 0.08f
        this.HairR03.xRot = -angleX2 * 0.07f + 0.1f

        this.HairL01.zRot = -0.14f
        this.HairL02.zRot = 0.087f
        this.HairL03.zRot = 0.087f
        this.HairR01.zRot = 0.14f
        this.HairR02.zRot = -0.06f
        this.HairR03.zRot = -0.06f

        this.ArmLeft01.xRot = angleAdd2 * 0.8f
        this.ArmLeft01.zRot = angleX * 0.08f - 0.2f
        this.ArmLeft02.xRot = 0.0f
        this.ArmLeft02.zRot = 0.0f

        this.ArmRight01.xRot = angleAdd1 * 0.8f + 0.1745f
        this.ArmRight01.zRot = -angleX * 0.08f + 0.2f
        this.ArmRight02.xRot = 0.0f
        this.ArmRight02.yRot = 0.0f
        this.ArmRight02.zRot = 0.0f

        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.087f
        this.LegLeft02.xRot = 0.0f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.087f
        this.LegRight02.xRot = 0.0f

        val headX = this.Head.xRot * -0.5f
        var headZ = this.Head.zRot * -0.5f
        if (entity != null) {
            headZ += entity.getHeadTiltAngle(ageInTicks) * -0.5f
        }

        this.Hair01.xRot += headX
        this.Hair01.zRot += headZ
        this.Hair02.xRot += headX * 0.5f
        this.Hair02.zRot += headZ * 0.5f
        this.Hair03.xRot += headX * 0.5f
        this.Hair03.zRot += headZ * 0.5f

        this.HairL01.xRot += headX
        this.HairL02.xRot += headX * 0.5f
        this.HairL03.xRot += headX * 0.5f
        this.HairR01.xRot += headX
        this.HairR02.xRot += headX * 0.5f
        this.HairR03.xRot += headX * 0.5f

        this.HairL01.zRot += headZ
        this.HairL02.zRot += headZ * 0.5f
        this.HairL03.zRot += headZ * 0.5f
        this.HairR01.zRot += headZ
        this.HairR02.zRot += headZ * 0.5f
        this.HairR03.zRot += headZ * 0.5f

        this.LegLeft01.xRot = angleAdd1 - 0.122f
        this.LegRight01.xRot = angleAdd2 - 0.174f
    }

    private fun applySpecialPoseAdjustments(
        entity: T?,
        ctx: PoseContext,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float
    ) {
        val angleAdd1 = ctx.angleAdd1
        val angleAdd2 = ctx.angleAdd2
        var legAddLeft = angleAdd1 - 0.122f
        var legAddRight = angleAdd2 - 0.174f
        val isCrouching = entity != null && entity.isCrouching()
        val isPassenger = entity != null && entity.isPassenger()
        val isSitting = ctx.isSitting || (isPassenger && entity.getVehicle() !is EntityMountBase)

        if (isCrouching) {
            this.poseTranslateY = SNEAK_TRANSLATE_Y
            this.Head.xRot -= 0.6283f
            this.BodyMain.xRot = 0.8727f
            this.ArmLeft01.xRot = -0.35f
            this.ArmLeft01.zRot = 0.2618f
            this.ArmRight01.xRot = -0.35f
            this.ArmRight01.zRot = -0.2618f

            legAddLeft -= 0.88f
            legAddRight -= 0.88f

            this.Hair01.xRot += 0.37f
            this.Hair02.xRot += 0.23f
            this.Hair03.xRot -= 0.1f
        }

        if (isSitting && !isPassenger) {
            this.isSittingPose = true
            if (entity != null && hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY += (0.65f * 2.4).toFloat()
                this.Head.xRot = -1.2217f
                this.Head.yRot *= 0.5f
                this.BodyMain.xRot = 1.2217f
                this.ArmLeft01.xRot = -1.9199f
                this.ArmLeft01.zRot = -0.1745f
                this.ArmLeft02.xRot = -2.31f
                this.ArmLeft02.y = this.armLeft02DefaultY + (0.22f * OFFSET_SCALE)
                this.ArmLeft02.z = this.armLeft02DefaultZ + (-0.21f * OFFSET_SCALE)
                this.ArmRight01.xRot = -1.9199f
                this.ArmRight01.zRot = 0.1745f
                this.ArmRight02.xRot = -2.31f
                this.ArmRight02.y = this.armRight02DefaultY + (0.22f * OFFSET_SCALE)
                this.ArmRight02.z = this.armRight02DefaultZ + (-0.21f * OFFSET_SCALE)
                legAddLeft = 0.0f
                legAddRight = 0.0f
                this.LegLeft02.xRot = ctx.angleX * 0.4f + 1.0f
                this.LegRight02.xRot = -ctx.angleX * 0.4f + 1.0f
                this.Hair01.xRot += 0.1f
                this.Hair02.xRot += 0.05f
                this.HairL01.xRot -= 0.3f
                this.HairR01.xRot -= 0.3f
                this.HairL02.xRot += 0.3f
                this.HairR02.xRot += 0.3f
            } else {
                this.poseTranslateY += SITTING_TRANSLATE_Y
                this.Head.xRot += 0.14f
                this.BodyMain.xRot = -0.4363f
                this.BoobL.xRot -= 0.25f
                this.BoobR.xRot -= 0.25f
                this.ArmLeft01.xRot = -0.3142f
                this.ArmLeft01.zRot = 0.349f
                this.ArmLeft02.zRot = 1.15f
                this.ArmRight01.xRot = -0.4363f
                this.ArmRight01.zRot = -0.2793f
                this.ArmRight02.zRot = -1.4f
                legAddLeft = -1.309f
                legAddRight = -1.7f
                this.LegLeft01.yRot = 0.3142f
                this.LegLeft02.xRot = 1.0472f
                this.LegRight01.yRot = -0.35f
                this.LegRight01.zRot = -0.2618f
                this.LegRight02.xRot = 0.9f
                this.Hair01.xRot += 0.12f
                this.Hair02.xRot += 0.15f
                this.Hair03.xRot += 0.25f
            }
        }

        if (isPassenger && entity.getVehicle() is EntityMountBase) {
            this.isSittingPose = true
            if (isSitting) {
                if (entity != null && hasLegacyState(entity, 1, 4)) {
                    this.poseTranslateY += 0.68f * 3
                    this.poseTranslateZ += -0.05f * 3
                    this.Head.xRot = -1.2217f
                    this.Head.yRot /= 2.0f
                    this.BodyMain.xRot = 1.2217f
                    this.ArmLeft01.xRot = -1.9199f
                    this.ArmLeft01.zRot = -0.1745f
                    this.ArmLeft02.xRot = -2.31f
                    this.ArmLeft02.y = this.armLeft02DefaultY + (0.22f * OFFSET_SCALE)
                    this.ArmLeft02.z = this.armLeft02DefaultZ + (-0.21f * OFFSET_SCALE)
                    this.ArmRight01.xRot = -1.9199f
                    this.ArmRight01.zRot = 0.1745f
                    this.ArmRight02.xRot = -2.31f
                    this.ArmRight02.y = this.armRight02DefaultY + (0.22f * OFFSET_SCALE)
                    this.ArmRight02.z = this.armRight02DefaultZ + (-0.21f * OFFSET_SCALE)
                    legAddLeft = 0.0f
                    legAddRight = 0.0f
                    this.LegLeft02.xRot = ctx.angleX * 0.4f + 1.0f
                    this.LegRight02.xRot = -ctx.angleX * 0.4f + 1.0f
                    this.Hair01.xRot += 0.1f
                    this.Hair02.xRot += 0.05f
                    this.HairL01.xRot -= 0.3f
                    this.HairR01.xRot -= 0.3f
                    this.HairL02.xRot += 0.3f
                    this.HairR02.xRot += 0.3f
                } else {
                    this.poseTranslateY += 0.51f * 3
                    this.poseTranslateZ += -0.05f * 3
                    this.Head.xRot += 0.14f
                    this.BodyMain.xRot = -0.4363f
                    this.BoobL.xRot -= 0.25f
                    this.BoobR.xRot -= 0.25f
                    this.ArmLeft01.xRot = -0.3142f
                    this.ArmLeft01.zRot = 0.349f
                    this.ArmLeft02.zRot = 1.15f
                    this.ArmRight01.xRot = -0.4363f
                    this.ArmRight01.zRot = -0.2793f
                    this.ArmRight02.zRot = -1.4f
                    legAddLeft = -1.309f
                    legAddRight = -1.7f
                    this.LegLeft01.yRot = 0.3142f
                    this.LegLeft02.xRot = 1.0472f
                    this.LegRight01.yRot = -0.35f
                    this.LegRight01.zRot = -0.2618f
                    this.LegRight02.xRot = 0.9f
                    this.Hair01.xRot += 0.12f
                    this.Hair02.xRot += 0.15f
                    this.Hair03.xRot += 0.25f
                }
            } else {
                this.poseTranslateY += 0.17f * 3
                this.Head.xRot += 0.1745f
                this.BodyMain.xRot = -0.35f
                this.ArmLeft01.xRot = -0.2f
                this.ArmLeft01.zRot = 0.349f
                this.ArmLeft02.zRot = 1.15f
                this.ArmRight01.xRot = -0.3f
                this.ArmRight01.zRot = -0.2793f
                this.ArmRight02.zRot = -1.4f
                legAddLeft = 0.1745f
                legAddRight = -0.8727f
                this.LegLeft01.zRot = -0.1f
                this.LegRight01.zRot = 0.1f
                this.LegRight02.xRot = 1.0472f
                this.Hair01.xRot += 0.12f
                this.Hair02.xRot += 0.22f
                this.Hair03.xRot += 0.25f
            }
        }

        if (entity != null && entity.attackTick > 20) {
            this.ArmLeft01.xRot = -1.6f
            this.ArmLeft01.yRot = 0.0f
            this.ArmLeft01.zRot = 0.21f
            this.ArmLeft02.xRot = 0.0f
            this.ArmLeft02.zRot = 0.0f
        }

        val partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
        val customAttackAnim = if (entity != null) entity.getCustomAttackAnim(partialTick) else 0.0f
        if (customAttackAnim > 0.0f) {
            val f7 = Mth.sin(customAttackAnim * customAttackAnim * Math.PI.toFloat())
            val f8 = Mth.sin(Mth.sqrt(customAttackAnim) * Math.PI.toFloat())
            ArmRight01.xRot += -f8 * 80.0f * (Math.PI.toFloat() / 180f)
            ArmRight01.yRot += -f7 * 20.0f * (Math.PI.toFloat() / 180f) + 0.2f
            ArmRight01.zRot += -f8 * 20.0f * (Math.PI.toFloat() / 180f)
        }

        this.LegLeft01.xRot = legAddLeft
        this.LegRight01.xRot = legAddRight
    }

    private fun syncGlowParts() {
        if (this.GlowBodyMain != null) {
            this.GlowBodyMain.copyFrom(this.BodyMain)
            this.GlowNeck.copyFrom(this.Neck)
            this.GlowHead.copyFrom(this.Head)
        }
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        val usePoseTranslate = this.poseTranslateY != 0.0f || this.poseTranslateZ != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, this.poseTranslateY, this.poseTranslateZ)
        }

        this.BodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

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
        val usePoseTranslate = this.poseTranslateY != 0.0f || this.poseTranslateZ != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, this.poseTranslateY, this.poseTranslateZ)
        }

        this.GlowBodyMain!!.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        @JvmField
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "battleship_hime"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val BASE_TRANSLATE_Y = LegacyPoseOffsets.baseY("ModelBattleshipHime")
        private val DEAD_TRANSLATE_Y = LegacyPoseOffsets.deadY("ModelBattleshipHime")
        private val SNEAK_TRANSLATE_Y = LegacyPoseOffsets.sneakY("ModelBattleshipHime")
        private val SITTING_TRANSLATE_Y = LegacyPoseOffsets.sittingY("ModelBattleshipHime")
        private val RIDING_TRANSLATE_Y = LegacyPoseOffsets.ridingY("ModelBattleshipHime")
        private val RIDING_TRANSLATE_Z = LegacyPoseOffsets.ridingZ("ModelBattleshipHime")

        @JvmStatic
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 104).addBox(-6.5f, -11f, -4f, 13f, 17f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.0524f, 0f, 0f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(33, 101).addBox(-3.5f, 0f, 0f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.7f, -9f, -3.5f, -0.6981f, -0.1396f, -0.0873f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(15, 80).mirror()
                    .addBox(-2f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.3f, -0.7f, 0f, 0f, -0.2094f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(0, 71).mirror().addBox(-5f, 0f, -5f, 5f, 13f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 11f, 2.5f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(33, 101).mirror()
                    .addBox(-3.5f, 0f, 0f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.7f, -9f, -3.5f, -0.6981f, 0.1396f, 0.0873f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(82, 13).addBox(-7.5f, 4f, -5.5f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0.2618f, 0f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 84).mirror().addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.5f, 9.5f, -2.7f, -0.2094f, 0f, 0.0524f)
            )

            val ClothL02 = LegLeft01.addOrReplaceChild(
                "ClothL02",
                CubeListBuilder.create().texOffs(10, 1).addBox(-4.4f, 0f, -3.7f, 8f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1f, 0f, 0.1396f, 0f, 0f)
            )

            val ClothL03 = LegLeft01.addOrReplaceChild(
                "ClothL03",
                CubeListBuilder.create().texOffs(8, 0).addBox(-4.5f, 0f, -3.8f, 9f, 5f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1.5f, 0.1f, 0.0873f, 0f, 0f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(24, 80).mirror().addBox(-3f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 14f, -3f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 84).addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.5f, 9.5f, -2.7f, -0.2094f, 0f, -0.0524f)
            )

            val ClothR02 = LegRight01.addOrReplaceChild(
                "ClothR02",
                CubeListBuilder.create().texOffs(10, 1).addBox(-3.6f, 0f, -3.7f, 8f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1f, 0f, 0.1396f, 0f, 0f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(24, 80).addBox(-3f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 14f, -3f)
            )

            val ClothR03 = LegRight01.addOrReplaceChild(
                "ClothR03",
                CubeListBuilder.create().texOffs(8, 0).addBox(-4.5f, 0f, -3.8f, 9f, 5f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1.5f, 0.1f, 0.0873f, 0f, 0f)
            )

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(84, 0).addBox(-7f, 0f, -4.5f, 14f, 5f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1f, 0f, 0.1396f, 0f, 0f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(44, 0).addBox(-4.5f, -2f, -4f, 9f, 1f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10f, -0.5f, 0.0524f, 0f, 0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -1.5f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 75).addBox(-8f, -8f, -7.2f, 16f, 17f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(88, 100).addBox(-1f, 0f, 0f, 2f, 11f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.5f, 0f, -5f, -0.1396f, 0.1745f, 0.1396f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(88, 100).addBox(-1f, 0f, 0f, 2f, 12f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 10f, 0f, 0.0873f, 0f, -0.0524f)
            )

            val HairR03 = HairR02.addOrReplaceChild(
                "HairR03",
                CubeListBuilder.create().texOffs(88, 100).addBox(-1f, 0f, 0f, 2f, 13f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 11f, 0f, 0.1396f, 0f, -0.0524f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(108, 41).addBox(-2f, 0f, 0f, 10f, 12f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -5.3f, -7.2f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(88, 100).addBox(-1f, 0f, 0f, 2f, 11f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.5f, 0f, -5f, -0.1396f, -0.1745f, -0.1396f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(88, 100).addBox(-1f, 0f, 0f, 2f, 12f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10f, 0f, 0.0524f, 0f, 0.0873f)
            )

            val HairL03 = HairL02.addOrReplaceChild(
                "HairL03",
                CubeListBuilder.create().texOffs(88, 100).addBox(-1f, 0f, 0f, 2f, 13f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 11f, 0f, 0.1396f, 0f, 0.0873f)
            )

            val HeadHL = Head.addOrReplaceChild(
                "HeadHL",
                CubeListBuilder.create().texOffs(120, 29).mirror()
                    .addBox(-1f, -9f, -1f, 2f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.5f, -7.5f, -3.3f, 0.6981f, 0f, 0.1396f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(2, 0).addBox(-7.5f, 0f, 0f, 15f, 9f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -15f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(50, 46).addBox(-7.5f, 0f, 0f, 15f, 17f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1f, 0.1745f, 0f, 0f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(2, 47).addBox(-8f, 0f, -5f, 16f, 15f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12f, 5.7f, -0.0524f, 0f, 0f)
            )

            val Hair03 = Hair02.addOrReplaceChild(
                "Hair03",
                CubeListBuilder.create().texOffs(3, 24).addBox(-8f, 0f, -5.5f, 16f, 13f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12f, 0.2f, -0.0897f, 0f, 0.0162f)
            )

            val HeadHR = Head.addOrReplaceChild(
                "HeadHR",
                CubeListBuilder.create().texOffs(120, 29).addBox(-1f, -9f, -1f, 2f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.5f, -7.5f, -3.3f, 0.6981f, 0f, -0.1396f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(15, 80).addBox(-3f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, -9.3f, -0.7f, 0.1047f, 0f, 0.2094f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(0, 71).addBox(0f, 0f, -5f, 5f, 13f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 11f, 2.5f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.0524f, 0f, 0f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -10f, -0.5f, 0.0524f, 0f, 0f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -1.5f, 0f)
            )

            addFaceLayer(GlowHead)

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
