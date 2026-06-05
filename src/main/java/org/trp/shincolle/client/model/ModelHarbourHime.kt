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
import net.minecraft.world.entity.LivingEntity
import org.trp.shincolle.Shincolle
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelHarbourHime<T : EntityShipBase?>(root: ModelPart) : ShipModelHumanoidBase<T?>(), IGlowableModel {
    private var isDeadPose = false
    private var isSittingPose = false
    private var poseTranslateY = 0f

    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val ArmLeft01: ModelPart
    private val Butt: ModelPart
    private val ArmRight01: ModelPart
    private val Head: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val HeadH: ModelPart
    private val Ahoke: ModelPart
    private val HairL01: ModelPart
    private val HairR01: ModelPart
    private val HairL02: ModelPart
    private val HairR02: ModelPart
    private val Hair01: ModelPart
    private val Hair02: ModelPart
    private val Hair03: ModelPart
    private val HeadH2: ModelPart
    private val HeadH3: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmLeft03: ModelPart
    private val ArmLeft04: ModelPart
    private val ArmLeft05: ModelPart
    private val ArmLeft06: ModelPart
    private val ArmLeft07: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft01: ModelPart
    private val Skirt: ModelPart
    private val LegRight02: ModelPart
    private val ShoesR: ModelPart
    private val LegLeft02: ModelPart
    private val ShoesL: ModelPart
    private val ArmRight02: ModelPart
    private val ArmRight03: ModelPart
    private val ArmRight04: ModelPart
    private val ArmRight05: ModelPart
    private val ArmRight06: ModelPart
    private val ArmRight07: ModelPart
    private val GlowBodyMain: ModelPart?
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val buttDefaultY: Float
    private val buttDefaultZ: Float
    private val skirtDefaultY: Float
    private val legLeft02DefaultZ: Float
    private val legRight02DefaultZ: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.ArmLeft03 = this.ArmLeft02.getChild("ArmLeft03")
        this.ArmLeft04 = this.ArmLeft03.getChild("ArmLeft04")
        this.ArmLeft05 = this.ArmLeft04.getChild("ArmLeft05")
        this.ArmLeft06 = this.ArmLeft05.getChild("ArmLeft06")
        this.ArmLeft07 = this.ArmLeft06.getChild("ArmLeft07")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.ArmRight03 = this.ArmRight02.getChild("ArmRight03")
        this.ArmRight04 = this.ArmRight03.getChild("ArmRight04")
        this.ArmRight05 = this.ArmRight04.getChild("ArmRight05")
        this.ArmRight06 = this.ArmRight05.getChild("ArmRight06")
        this.ArmRight07 = this.ArmRight06.getChild("ArmRight07")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.ShoesR = this.LegRight02.getChild("ShoesR")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.ShoesL = this.LegLeft02.getChild("ShoesL")
        this.Skirt = this.Butt.getChild("Skirt")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.Hair03 = this.Hair02.getChild("Hair03")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        this.HeadH = this.GlowHead.getChild("HeadH")
        this.HeadH2 = this.HeadH.getChild("HeadH2")
        this.HeadH3 = this.HeadH2.getChild("HeadH3")
        this.initFaceParts(this.GlowHead)
        this.buttDefaultY = this.Butt.y
        this.buttDefaultZ = this.Butt.z
        this.skirtDefaultY = this.Skirt.y
        this.legLeft02DefaultZ = this.LegLeft02.z
        this.legRight02DefaultZ = this.LegRight02.z
    }

    override fun setupAnim(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        var limbSwing = limbSwing
        var limbSwingAmount = limbSwingAmount
        var netHeadYaw = netHeadYaw
        var headPitch = headPitch
        val partialTick = ageInTicks - entity!!.tickCount.toFloat()
        if (entity.isPassenger() && entity.getVehicle() is LivingEntity) {
            limbSwingAmount = vehicle.walkAnimation.speed(partialTick)
            limbSwing = vehicle.walkAnimation.position(partialTick)

            if (vehicle is EntityMountBase) {
                val vBodyRot = Mth.lerp(partialTick, vehicle.yBodyRotO, vehicle.yBodyRot)
                val vHeadRot = Mth.lerp(partialTick, vehicle.yHeadRotO, vehicle.yHeadRot)
                netHeadYaw = vHeadRot - vBodyRot
                headPitch = Mth.lerp(partialTick, vehicle.xRotO, vehicle.getXRot())
            }
        }
        val ctx = computePoseContext(entity, limbSwing, limbSwingAmount, ageInTicks, 0.0f)
        this.resetOffsets()
        this.applyEquipVisibility(entity)
        applyFaceAndMouth(entity)

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

        this.Butt.y = this.buttDefaultY
        this.Butt.z = this.buttDefaultZ
        this.Skirt.y = this.skirtDefaultY
        this.LegLeft02.z = this.legLeft02DefaultZ
        this.LegRight02.z = this.legRight02DefaultZ
    }

    private fun applyEquipVisibility(entity: T?) {
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        this.Head.xRot = -0.35f
        this.Head.yRot = 0.0f
        this.Head.zRot = 0.0f
        this.BoobL.xRot = -0.76f
        this.BoobR.xRot = -0.76f
        this.Ahoke.yRot = 0.6f
        this.BodyMain.xRot = 1.4835f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = 1.0472f
        this.Butt.z = this.buttDefaultZ + (-0.05f * OFFSET_SCALE)
        this.Skirt.y = this.skirtDefaultY + (-0.1f * OFFSET_SCALE)
        this.Hair01.xRot = 0.35f
        this.Hair01.zRot = 0.0f
        this.Hair02.xRot = 0.2f
        this.Hair02.zRot = 0.0f
        this.Hair03.xRot = -0.3f
        this.Hair03.zRot = 0.0f
        this.HairL01.xRot = -0.14f
        this.HairL02.xRot = 0.17f
        this.HairR01.xRot = -0.14f
        this.HairR02.xRot = 0.17f

        this.ArmLeft01.xRot = -2.967f
        this.ArmLeft01.yRot = -0.6981f
        this.ArmLeft01.zRot = 0.08f
        this.ArmLeft03.xRot = 0.0f
        this.ArmLeft03.yRot = 0.35f
        this.ArmLeft03.zRot = 0.0f
        this.ArmLeft06.xRot = 0.0873f
        this.ArmLeft06.yRot = 0.14f
        this.ArmLeft06.zRot = 0.26f
        this.ArmLeft07.xRot = -0.2618f

        this.ArmRight01.xRot = -2.967f
        this.ArmRight01.yRot = 0.6981f
        this.ArmRight01.zRot = -0.08f
        this.ArmRight03.xRot = 0.0f
        this.ArmRight03.yRot = -0.35f
        this.ArmRight03.zRot = 0.0f
        this.ArmRight06.zRot = -0.26f
        this.ArmRight07.xRot = -0.2618f

        this.LegLeft02.z = this.legLeft02DefaultZ
        this.LegRight02.z = this.legRight02DefaultZ
        this.LegLeft01.xRot = -1.7f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.05f
        this.LegLeft02.xRot = 0.7f
        this.LegRight01.xRot = -1.7f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.05f
        this.LegRight02.xRot = 0.7f
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
        if (entity!!.shipDepth > 0.0) {
            this.poseTranslateY = angleX * 0.05f + 0.025f
        }

        this.Head.xRot = headPitch * 0.014f
        this.Head.yRot = netHeadYaw * 0.01f
        this.Head.zRot = 0.0f
        val headX = this.Head.xRot * -0.5f

        this.BoobL.xRot = angleX * 0.08f - 0.76f
        this.BoobR.xRot = angleX * 0.08f - 0.76f
        this.Ahoke.yRot = angleX * 0.15f + 0.6f
        this.BodyMain.xRot = -0.1047f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = 0.3142f

        this.Hair01.xRot = angleX * 0.03f + 0.21f + headX
        this.Hair01.zRot = 0.0f
        this.Hair02.xRot = -angleX1 * 0.04f - 0.08f + headX
        this.Hair02.zRot = 0.0f
        this.Hair03.xRot = -angleX2 * 0.07f - 0.14f
        this.Hair03.zRot = 0.0f

        this.ArmLeft01.xRot = -0.2618f
        this.ArmLeft01.yRot = 0.7f
        this.ArmLeft01.zRot = 0.0f
        this.ArmLeft03.xRot = 0.0f
        this.ArmLeft03.yRot = 0.35f
        this.ArmLeft03.zRot = 0.0f
        this.ArmLeft06.xRot = 0.0873f
        this.ArmLeft06.yRot = 0.14f
        this.ArmLeft06.zRot = angleX * 0.1f + 0.26f
        this.ArmLeft07.xRot = -0.2618f

        this.ArmRight01.xRot = -0.2618f
        this.ArmRight01.yRot = -0.7f
        this.ArmRight01.zRot = 0.0f
        this.ArmRight03.xRot = 0.0f
        this.ArmRight03.yRot = -0.35f
        this.ArmRight03.zRot = 0.0f
        this.ArmRight06.zRot = -angleX * 0.1f - 0.26f
        this.ArmRight07.xRot = -0.2618f

        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.05f
        this.LegLeft02.xRot = 0.0f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.05f
        this.LegRight02.xRot = 0.0f

        val headZ = this.Head.zRot * -0.5f
        this.Hair01.zRot = headZ
        this.Hair02.zRot = headZ
        this.HairL01.zRot = headZ - 0.14f
        this.HairL02.zRot = headZ + 0.087f
        this.HairR01.zRot = headZ + 0.14f
        this.HairR02.zRot = headZ - 0.052f

        this.HairL01.xRot = angleX * 0.02f + headX - 0.14f
        this.HairL02.xRot = -angleX1 * 0.04f + headX + 0.17f
        this.HairR01.xRot = angleX * 0.02f + headX - 0.14f
        this.HairR02.xRot = -angleX1 * 0.04f + headX + 0.17f

        this.LegLeft01.xRot = angleAdd1 * 0.6f - 0.21f
        this.LegRight01.xRot = angleAdd2 * 0.6f - 0.21f
    }

    private fun applySpecialPoseAdjustments(
        entity: T?,
        ctx: PoseContext?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float
    ) {
        val isCrouching = entity!!.isCrouching()
        val isPassenger = entity.isPassenger()
        val isSitting =
            (entity != null && entity.isInSittingPose) || (entity != null && entity.isPassenger() && (entity.getVehicle() !is EntityMountBase))

        if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            this.Head.xRot -= 0.6283f
            this.BodyMain.xRot = 0.8727f
            this.ArmLeft01.xRot = -0.61f
            this.ArmLeft01.yRot = 0.35f
            this.ArmLeft01.zRot = -0.14f
            this.ArmLeft03.yRot = 0.7f
            this.ArmLeft06.zRot = -0.35f
            this.ArmRight01.xRot = -0.61f
            this.ArmRight01.yRot = -0.35f
            this.ArmRight01.zRot = 0.14f
            this.ArmRight03.yRot = -0.7f
            this.ArmRight06.zRot = 0.35f

            this.LegLeft01.xRot -= 1.0f
            this.LegRight01.xRot -= 1.0f

            this.Hair01.xRot += 0.37f
            this.Hair02.xRot += 0.23f
            this.Hair03.xRot -= 0.1f
        }

        if (isPassenger && entity.getVehicle() is EntityMountBase) {
            this.isSittingPose = true
            if (isSitting) {
                if (hasLegacyState(entity, 1, 4)) {
                    this.setFace(2)
                    this.poseTranslateY += 0.57f * 3
                    this.Head.xRot = this.Head.xRot * 0.5f + 0.55f
                    this.Head.yRot = this.Head.yRot * 0.5f - 0.2f
                    this.BodyMain.xRot = -0.61f
                    this.BodyMain.yRot = -0.2618f
                    this.BodyMain.zRot = -0.5236f
                    this.ArmLeft01.xRot = 1.3f
                    this.ArmLeft01.yRot = 0.7f
                    this.ArmLeft01.zRot = -0.1745f
                    this.ArmLeft03.xRot = -2.53f
                    this.ArmLeft03.yRot = -0.7f
                    this.ArmLeft06.xRot = -0.5236f
                    this.ArmLeft06.yRot = -0.5236f
                    this.ArmLeft06.zRot = 0.7f
                    this.ArmRight01.xRot = 0.7f
                    this.ArmRight01.yRot = 0.0f
                    this.ArmRight01.zRot = 0.5236f
                    this.ArmRight03.xRot = -1.57f
                    this.ArmRight03.yRot = 0.14f
                    this.ArmRight03.zRot = 1.7453f
                    this.ArmRight06.zRot = -0.5236f
                    this.LegLeft01.xRot = -1.05f
                    this.LegRight01.xRot = -1.31f
                    this.LegLeft01.zRot = -0.5236f
                    this.LegLeft02.xRot = 1.05f
                    this.LegRight01.yRot = -0.4363f
                    this.LegRight02.xRot = 0.7f
                    this.Hair01.xRot -= 0.12f
                    this.Hair01.zRot = -0.09f
                    this.Hair02.xRot -= 0.18f
                    this.Hair02.zRot = -0.26f
                    this.Hair03.xRot -= 0.21f
                    this.Hair03.zRot = -0.35f
                } else {
                    this.poseTranslateY += 0.41f * 3
                    this.Head.xRot -= 0.35f
                    this.Hair01.xRot += 0.35f
                    this.ArmLeft01.xRot = 0.2f
                    this.ArmLeft01.yRot = 0.0f
                    this.ArmLeft01.zRot = -1.1f
                    this.ArmLeft03.yRot = 0.0f
                    this.ArmLeft03.zRot = -0.4f
                    this.ArmRight01.xRot = 0.2f
                    this.ArmRight01.yRot = 0.0f
                    this.ArmRight01.zRot = 1.1f
                    this.ArmRight03.zRot = 0.4f
                    this.LegLeft01.xRot = -1.2217f
                    this.LegRight01.xRot = -1.2217f
                    this.LegLeft02.z = this.legLeft02DefaultZ + (0.37f * OFFSET_SCALE)
                    this.LegRight02.z = this.legRight02DefaultZ + (0.37f * OFFSET_SCALE)
                    this.LegLeft01.yRot = 0.14f
                    this.LegRight01.yRot = -0.14f
                    this.LegLeft02.xRot = 2.53f
                    this.LegRight02.xRot = 2.53f
                }
            } else {
                this.Hair01.xRot += 0.35f
                this.ArmLeft01.xRot = 0.5f
                this.ArmLeft01.yRot = 0.0f
                this.ArmLeft01.zRot = -0.7f
                this.ArmLeft03.xRot = -0.5f
                this.ArmLeft03.yRot = 0.0f
                this.ArmLeft03.zRot = -0.4f
                this.ArmLeft06.zRot = 0.4f
                this.ArmLeft07.xRot = -1.2f
                this.ArmRight01.xRot = 0.5f
                this.ArmRight01.yRot = 0.0f
                this.ArmRight01.zRot = 0.7f
                this.ArmRight03.xRot = -0.5f
                this.ArmRight03.yRot = 0.0f
                this.ArmRight03.zRot = 0.4f
                this.ArmRight06.zRot = -0.4f
                this.ArmRight07.xRot = -1.2f
            }
        } else if (isSitting) {
            this.isSittingPose = true
            this.poseTranslateY += (if (isPassenger) RIDING_TRANSLATE_Y else SITTING_TRANSLATE_Y)
            if (hasLegacyState(entity, 1, 4)) {
                this.setFace(2)
                this.poseTranslateY += 0.2f
                this.Head.xRot = this.Head.xRot * 0.5f + 0.55f
                this.Head.yRot = this.Head.yRot * 0.5f - 0.2f
                this.BodyMain.xRot = -0.61f
                this.BodyMain.yRot = -0.2618f
                this.BodyMain.zRot = -0.5236f
                this.ArmLeft01.xRot = 1.3f
                this.ArmLeft01.yRot = 0.7f
                this.ArmLeft01.zRot = -0.1745f
                this.ArmLeft03.xRot = -2.53f
                this.ArmLeft03.yRot = -0.7f
                this.ArmLeft06.xRot = -0.5236f
                this.ArmLeft06.yRot = -0.5236f
                this.ArmLeft06.zRot = 0.7f
                this.ArmRight01.xRot = 0.7f
                this.ArmRight01.yRot = 0.0f
                this.ArmRight01.zRot = 0.5236f
                this.ArmRight03.xRot = -1.57f
                this.ArmRight03.yRot = 0.14f
                this.ArmRight03.zRot = 1.7453f
                this.ArmRight06.zRot = -0.5236f
                this.LegLeft01.xRot = -1.05f
                this.LegRight01.xRot = -1.31f
                this.LegLeft01.zRot = -0.5236f
                this.LegLeft02.xRot = 1.05f
                this.LegRight01.yRot = -0.4363f
                this.LegRight02.xRot = 0.7f
                this.Hair01.xRot -= 0.12f
                this.Hair01.zRot = -0.09f
            } else {
                this.Head.xRot -= 0.25f
                this.ArmLeft01.xRot = -0.44f
                this.ArmLeft01.yRot = 0.44f
                this.ArmLeft01.zRot = 0.0f
                this.ArmLeft03.yRot = 0.87f
                this.ArmLeft06.zRot = 0.1f
                this.ArmRight01.xRot = -0.44f
                this.ArmRight01.yRot = -0.44f
                this.ArmRight01.zRot = 0.0f
                this.ArmRight03.yRot = -0.87f
                this.ArmRight06.zRot = -0.1f
                this.LegLeft01.xRot = -1.2217f
                this.LegRight01.xRot = -1.2217f
                this.LegLeft02.z = this.legLeft02DefaultZ + (0.37f * OFFSET_SCALE)
                this.LegRight02.z = this.legRight02DefaultZ + (0.37f * OFFSET_SCALE)
                this.LegLeft01.yRot = 0.14f
                this.LegRight01.yRot = -0.14f
                this.LegLeft02.xRot = 2.53f
                this.LegRight02.xRot = 2.53f
            }
        }

        if (entity != null && entity.attackTick > 0) {
            if (entity.attackTick > 25) {
                this.setFace(3)
            }
            this.ArmLeft01.xRot = -1.4f
            this.ArmLeft01.yRot = -0.14f
            this.ArmLeft01.zRot = 0.0f
            this.ArmLeft06.zRot = -0.96f
            this.ArmRight01.xRot = -1.4f
            this.ArmRight01.yRot = 0.14f
            this.ArmRight01.zRot = 0.0f
            this.ArmRight06.zRot = 0.96f
        }

        val partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
        val customAttackAnim = if (entity != null) entity.getCustomAttackAnim(partialTick) else 0.0f
        if (customAttackAnim > 0.0f) {
            val f7 = Mth.sin(customAttackAnim * customAttackAnim * Math.PI.toFloat())
            val f8 = Mth.sin(Mth.sqrt(customAttackAnim) * Math.PI.toFloat())
            this.ArmRight01.xRot += -f8 * 80.0f * (Math.PI.toFloat() / 180f)
            this.ArmRight01.yRot += -f7 * 20.0f * (Math.PI.toFloat() / 180f) + 1.0f
            this.ArmRight01.zRot += -f8 * 20.0f * (Math.PI.toFloat() / 180f)
        }
    }

    private fun syncGlowParts() {
        if (this.GlowBodyMain != null) {
            this.GlowBodyMain.copyFrom(this.BodyMain)
            this.GlowNeck.copyFrom(this.Neck)
            this.GlowHead.copyFrom(this.Head)
            this.GlowHead.getChild("HeadH").copyFrom(this.HeadH)
            this.GlowHead.getChild("HeadH").getChild("HeadH2").copyFrom(this.HeadH2)
            this.GlowHead.getChild("HeadH").getChild("HeadH2").getChild("HeadH3").copyFrom(this.HeadH3)
        }
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
        val usePoseTranslate = this.poseTranslateY != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, this.poseTranslateY, 0.0f)
        }

        this.GlowBodyMain!!.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "harbour_hime"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = 0.74f * 3
        private const val SNEAK_TRANSLATE_Y = 0.1f
        private val SITTING_TRANSLATE_Y = 0.4f * 3
        private const val RIDING_TRANSLATE_Y = 0.5f

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 104).addBox(-6.5f, -11f, -4f, 13f, 17f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(2, 85).mirror()
                    .addBox(-2f, -1f, -2.5f, 5f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.3f, -0.7f, -0.2618f, 0.6981f, 0f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(72, 38).mirror()
                    .addBox(-3f, 0f, -3.5f, 6f, 10f, 7f, CubeDeformation(0f)),
                PartPose.offset(0.5f, 4f, 0f)
            )

            val ArmLeft03 = ArmLeft02.addOrReplaceChild(
                "ArmLeft03",
                CubeListBuilder.create().texOffs(46, 46).addBox(-4f, 0f, -4.5f, 8f, 5f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0f, 0.3491f, 0f)
            )

            val ArmLeft04 = ArmLeft03.addOrReplaceChild(
                "ArmLeft04",
                CubeListBuilder.create().texOffs(50, 60).addBox(-5f, 0f, -5.5f, 10f, 6f, 11f, CubeDeformation(0f)),
                PartPose.offset(0f, 5f, 0f)
            )

            val ArmLeft05 = ArmLeft04.addOrReplaceChild(
                "ArmLeft05",
                CubeListBuilder.create().texOffs(46, 0).addBox(-5.5f, -0.2f, -6.5f, 11f, 4f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 6f, 0f)
            )

            val ArmLeft06 = ArmLeft05.addOrReplaceChild(
                "ArmLeft06",
                CubeListBuilder.create().texOffs(68, 17).addBox(0f, 0f, -4.2f, 5f, 9f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, 1f, 0.5f, 0.0873f, 0.1396f, 0.2618f)
            )

            val ArmLeft07 = ArmLeft06.addOrReplaceChild(
                "ArmLeft07",
                CubeListBuilder.create().texOffs(43, 18).addBox(0f, 0f, -3f, 4f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, 0f, -2f, -0.2618f, 0f, 0.1745f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(2, 85).mirror()
                    .addBox(-3f, -1f, -2.5f, 5f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, -9.3f, -0.7f, -0.2618f, -0.6981f, 0f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(72, 38).mirror()
                    .addBox(-3f, 0f, -3.5f, 6f, 10f, 7f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, 4f, 0f)
            )

            val ArmRight03 = ArmRight02.addOrReplaceChild(
                "ArmRight03",
                CubeListBuilder.create().texOffs(46, 46).addBox(-4f, 0f, -4.5f, 8f, 5f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0f, -0.3491f, 0f)
            )

            val ArmRight04 = ArmRight03.addOrReplaceChild(
                "ArmRight04",
                CubeListBuilder.create().texOffs(50, 60).mirror()
                    .addBox(-5f, 0f, -5.5f, 10f, 6f, 11f, CubeDeformation(0f)),
                PartPose.offset(0f, 5f, 0f)
            )

            val ArmRight05 = ArmRight04.addOrReplaceChild(
                "ArmRight05",
                CubeListBuilder.create().texOffs(46, 0).mirror()
                    .addBox(-5.5f, -0.2f, -6.5f, 11f, 4f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 6f, 0f)
            )

            val ArmRight06 = ArmRight05.addOrReplaceChild(
                "ArmRight06",
                CubeListBuilder.create().texOffs(68, 17).mirror()
                    .addBox(-4f, 0f, -4.2f, 5f, 9f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1f, 1f, 0.5f, 0.0873f, -0.1396f, -0.2618f)
            )

            val ArmRight07 = ArmRight06.addOrReplaceChild(
                "ArmRight07",
                CubeListBuilder.create().texOffs(43, 18).addBox(-3f, 0f, -3f, 4f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1f, 0f, -2f, -0.2618f, 0f, -0.1745f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(46, 33).addBox(-4f, 0f, 0f, 8f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.9f, -8.1f, -4f, -0.8727f, 0.0873f, -0.0873f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7.5f, 4f, -5.7f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0.3142f, 0f, 0f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 84).addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5.2f, 9.5f, -2.6f, -0.2094f, 0f, -0.0524f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 84).addBox(-3f, 0f, 0f, 6f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 14f, -3f)
            )

            val ShoesR = LegRight02.addOrReplaceChild(
                "ShoesR",
                CubeListBuilder.create().texOffs(100, 0).addBox(-3.5f, 0f, -3.5f, 7f, 14f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 1f, 3f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 84).mirror().addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.2f, 9.5f, -2.6f, -0.2094f, 0f, 0.0524f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 84).mirror().addBox(-3f, 0f, 0f, 6f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 14f, -3f)
            )

            val ShoesL = LegLeft02.addOrReplaceChild(
                "ShoesL",
                CubeListBuilder.create().texOffs(100, 0).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 14f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1f, 3f, 0f, 0f, 0.0365f)
            )

            val Skirt = Butt.addOrReplaceChild(
                "Skirt",
                CubeListBuilder.create().texOffs(0, 19).addBox(-8.5f, 0f, 0f, 17f, 6f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6.9f, -6f, -0.1396f, 0f, 0f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(46, 33).mirror().addBox(-4f, 0f, 0f, 8f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.9f, -8.1f, -4f, -0.8727f, -0.0873f, 0.0873f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(88, 26).addBox(-5.5f, -2f, -5f, 11f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10.3f, -0.2f, 0.1047f, 0f, 0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -1.5f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(45, 77).addBox(-8f, -8f, -7.2f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(24, 84).addBox(-1f, 0f, 0f, 2f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, 3f, -5.5f, -0.14f, -0.1745f, -0.0873f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(24, 84).addBox(-1f, 0f, 0f, 2f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.1f, 10f, 0.1f, 0.1745f, 0f, 0.0873f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(24, 84).mirror().addBox(-1f, 0f, 0f, 2f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7f, 3f, -5.5f, -0.14f, 0.1745f, 0.0873f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(24, 84).mirror().addBox(-1f, 0f, 0f, 2f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.1f, 10f, 0.1f, 0.1745f, 0f, -0.0524f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(104, 29).addBox(0f, -4f, -11.5f, 0f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -9f, -5.5f, -0.1745f, 0.6981f, 0f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(0, 57).addBox(-7.5f, 0f, 0f, 15f, 12f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -15f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(0, 57).addBox(-7.5f, 0f, 0f, 15f, 17f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1f, 0.2094f, 0f, 0f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(0, 58).addBox(-8f, 0f, -5f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 13.5f, 5.5f, -0.0873f, 0f, 0f)
            )

            val Hair03 = Hair02.addOrReplaceChild(
                "Hair03",
                CubeListBuilder.create().texOffs(0, 35).addBox(-8f, 0f, -4.5f, 16f, 15f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12.5f, -0.1f, -0.1396f, 0f, 0f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -10.3f, -0.2f, 0.1047f, 0f, 0f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -1.5f, 0f)
            )
            addFaceLayer(GlowHead)

            val HeadH = GlowHead.addOrReplaceChild(
                "HeadH",
                CubeListBuilder.create().texOffs(33, 101).mirror()
                    .addBox(-2f, -2f, -3f, 4f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10f, -6.5f, -0.3142f, 0f, 0f)
            )

            val HeadH2 = HeadH.addOrReplaceChild(
                "HeadH2",
                CubeListBuilder.create().texOffs(84, 64).addBox(-1.5f, -1.5f, -3f, 3f, 3f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.2f, -3.7f, -0.1396f, 0f, 0f)
            )

            val HeadH3 = HeadH2.addOrReplaceChild(
                "HeadH3",
                CubeListBuilder.create().texOffs(45, 105).addBox(-1f, -1.2f, -3f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -3.8f, -0.1396f, 0f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
