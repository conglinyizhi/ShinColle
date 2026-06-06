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
import org.trp.shincolle.client.model.LegacyPoseOffsets.deadY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sneakY
import org.trp.shincolle.entity.EntityCarrierWDemon
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelCarrierWDemon<T : EntityShipBase>(root: ModelPart) : ShipModelHumanoidBase<T>(), IGlowableModel {
    private var isDeadPose = false
    override var poseTranslateY = 0f
    private var poseTranslateZ = 0f
    private var isSittingPose = false

    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val Butt: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val Cloth01: ModelPart
    private val EquipBase: ModelPart
    private val Head: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val HeadS04: ModelPart
    private val HeadS05: ModelPart
    private val Ahoke: ModelPart
    private val HairL01: ModelPart
    private val HairR01: ModelPart
    private val HairL02: ModelPart
    private val HairR02: ModelPart
    private val Hair01: ModelPart
    private val Hair02: ModelPart
    private val Hair03: ModelPart
    private val HeadS02: ModelPart
    private val HeadS03: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft01: ModelPart
    private val Skirt01: ModelPart
    private val LegRight02: ModelPart
    private val ShoesR01: ModelPart
    private val ShoesR02: ModelPart
    private val ShoesR03: ModelPart
    private val ShoesR04: ModelPart
    private val LegLeft02: ModelPart
    private val ShoesL01: ModelPart
    private val ShoesL02: ModelPart
    private val ShoesL03: ModelPart
    private val ShoesL04: ModelPart
    private val Skirt02: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmLeft03: ModelPart
    private val ArmLeft04: ModelPart
    private val ArmLeft05: ModelPart
    private val ArmRight02: ModelPart
    private val ArmRight03: ModelPart
    private val ArmRight04: ModelPart
    private val ArmRight05: ModelPart
    private val EquipL01: ModelPart
    private val EquipR01: ModelPart
    private val EquipL02: ModelPart
    private val EquipL03: ModelPart
    private val EquipL04: ModelPart
    private val EquipL05: ModelPart
    private val EquipR02: ModelPart
    private val EquipR03: ModelPart
    private val EquipR04: ModelPart
    private val EquipR05: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowBodyMain2: ModelPart
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val equipL01DefaultX: Float
    private val equipL01DefaultY: Float
    private val equipL01DefaultZ: Float
    private val equipR01DefaultX: Float
    private val equipR01DefaultY: Float
    private val equipR01DefaultZ: Float
    private val legLeft02DefaultX: Float
    private val legLeft02DefaultY: Float
    private val legLeft02DefaultZ: Float
    private val legRight02DefaultX: Float
    private val legRight02DefaultY: Float
    private val legRight02DefaultZ: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.ArmRight03 = this.ArmRight02.getChild("ArmRight03")
        this.ArmRight04 = this.ArmRight03.getChild("ArmRight04")
        this.ArmRight05 = this.ArmRight04.getChild("ArmRight05")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.ArmLeft03 = this.ArmLeft02.getChild("ArmLeft03")
        this.ArmLeft04 = this.ArmLeft03.getChild("ArmLeft04")
        this.ArmLeft05 = this.ArmLeft04.getChild("ArmLeft05")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.ShoesL01 = this.LegLeft02.getChild("ShoesL01")
        this.ShoesL02 = this.ShoesL01.getChild("ShoesL02")
        this.ShoesL03 = this.ShoesL02.getChild("ShoesL03")
        this.ShoesL04 = this.ShoesL03.getChild("ShoesL04")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.Skirt02 = this.Skirt01.getChild("Skirt02")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.ShoesR01 = this.LegRight02.getChild("ShoesR01")
        this.ShoesR02 = this.ShoesR01.getChild("ShoesR02")
        this.ShoesR03 = this.ShoesR02.getChild("ShoesR03")
        this.ShoesR04 = this.ShoesR03.getChild("ShoesR04")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.HeadS05 = this.Head.getChild("HeadS05")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.Hair03 = this.Hair02.getChild("Hair03")
        this.HeadS04 = this.Head.getChild("HeadS04")
        this.HeadS02 = root.getChild("HeadS02")
        this.HeadS03 = root.getChild("HeadS03")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        initFaceParts(this.GlowHead)
        this.GlowBodyMain2 = root.getChild("GlowBodyMain2")
        this.EquipBase = this.GlowBodyMain2.getChild("EquipBase")
        this.EquipL01 = this.EquipBase.getChild("EquipL01")
        this.EquipL02 = this.EquipL01.getChild("EquipL02")
        this.EquipL03 = this.EquipL02.getChild("EquipL03")
        this.EquipL04 = this.EquipL03.getChild("EquipL04")
        this.EquipL05 = this.EquipL04.getChild("EquipL05")
        this.EquipR01 = this.EquipBase.getChild("EquipR01")
        this.EquipR02 = this.EquipR01.getChild("EquipR02")
        this.EquipR03 = this.EquipR02.getChild("EquipR03")
        this.EquipR04 = this.EquipR03.getChild("EquipR04")
        this.EquipR05 = this.EquipR04.getChild("EquipR05")
        this.equipL01DefaultX = this.EquipL01.x
        this.equipL01DefaultY = this.EquipL01.y
        this.equipL01DefaultZ = this.EquipL01.z
        this.equipR01DefaultX = this.EquipR01.x
        this.equipR01DefaultY = this.EquipR01.y
        this.equipR01DefaultZ = this.EquipR01.z
        this.legLeft02DefaultX = this.LegLeft02.x
        this.legLeft02DefaultY = this.LegLeft02.y
        this.legLeft02DefaultZ = this.LegLeft02.z
        this.legRight02DefaultX = this.LegRight02.x
        this.legRight02DefaultY = this.LegRight02.y
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
        this.resetPoseState()
        this.resetOffsets()
        this.applyEquipVisibility(entity)
        this.applyFaceAndMouth(entity)

        if (entity != null && entity.isInDeadPose) {
            this.applyDeadPose()
            this.syncGlowParts()
            return
        }

        this.applyBasePose(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch)
        this.applySpecialPoseAdjustments(entity, limbSwing, limbSwingAmount, ageInTicks)
        this.syncGlowParts()
    }

    private fun resetPoseState() {
        this.isDeadPose = false
        this.isSittingPose = false
        this.poseTranslateY = 0.0f
        this.poseTranslateZ = 0.0f
    }

    private fun resetOffsets() {
        this.EquipL01.x = this.equipL01DefaultX
        this.EquipL01.y = this.equipL01DefaultY
        this.EquipL01.z = this.equipL01DefaultZ
        this.EquipR01.x = this.equipR01DefaultX
        this.EquipR01.y = this.equipR01DefaultY
        this.EquipR01.z = this.equipR01DefaultZ
        this.LegLeft02.x = this.legLeft02DefaultX
        this.LegLeft02.y = this.legLeft02DefaultY
        this.LegLeft02.z = this.legLeft02DefaultZ
        this.LegRight02.x = this.legRight02DefaultX
        this.LegRight02.y = this.legRight02DefaultY
        this.LegRight02.z = this.legRight02DefaultZ
    }

    private fun applyEquipVisibility(entity: T?) {
        this.EquipBase.visible = entity!!.getEquipFlag(EntityCarrierWDemon.EQUIP_RIGGING)
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        this.Head.xRot = 0.55f
        this.Head.yRot = 0.0f
        this.Head.zRot = 0.0f
        this.BoobL.xRot = -0.7f
        this.BoobR.xRot = -0.7f
        this.Ahoke.yRot = 0.7f
        this.BodyMain.xRot = -0.1047f
        this.Hair01.xRot = -0.1f
        this.Hair01.zRot = 0.0f
        this.Hair02.xRot = -0.2f
        this.Hair02.zRot = 0.0f
        this.Hair03.xRot = -0.14f
        this.Hair03.zRot = 0.0f
        this.HairL01.zRot = 0.087f
        this.HairL02.zRot = 0.087f
        this.HairR01.zRot = 0.087f
        this.HairR02.zRot = -0.052f
        this.HairL01.xRot = -0.65f
        this.HairL02.xRot = 0.17f
        this.HairR01.xRot = -0.65f
        this.HairR02.xRot = 0.17f

        this.Neck.xRot = 0.3f
        this.Butt.xRot = -0.14f
        this.Skirt01.xRot = -0.1745f
        this.Skirt02.xRot = -0.2618f

        this.ArmLeft01.xRot = 0.4f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = -0.2618f
        this.ArmLeft03.xRot = 0.0f
        this.ArmLeft03.zRot = 0.0f
        this.ArmLeft05.zRot = 0.2618f

        this.ArmRight01.xRot = 0.4f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = 0.2618f
        this.ArmRight03.xRot = 0.0f
        this.ArmRight03.zRot = 0.0f

        this.LegLeft01.xRot = -1.0472f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = -0.14f
        this.LegLeft02.xRot = 1.2217f
        this.LegLeft02.yRot = 1.2217f
        this.LegLeft02.zRot = -1.0472f
        this.LegLeft02.x = this.legLeft02DefaultX + (0.175f * OFFSET_SCALE)
        this.LegLeft02.y = this.legLeft02DefaultY + (-0.02f * OFFSET_SCALE)
        this.LegLeft02.z = this.legLeft02DefaultZ + (0.1635f * OFFSET_SCALE)
        this.ShoesL04.xRot = -0.1f

        this.LegRight01.xRot = -1.0472f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = 0.14f
        this.LegRight02.xRot = 1.2217f
        this.LegRight02.yRot = -1.2217f
        this.LegRight02.zRot = 1.0472f
        this.LegRight02.x = this.legRight02DefaultX + (-0.175f * OFFSET_SCALE)
        this.LegRight02.y = this.legRight02DefaultY + (-0.05f * OFFSET_SCALE)
        this.LegRight02.z = this.legRight02DefaultZ + (0.1635f * OFFSET_SCALE)

        this.EquipBase.xRot = 0.0f
        this.EquipL01.xRot = 0.2618f
        this.EquipL01.yRot = 0.1745f
        this.EquipL01.zRot = 0.0f
        this.EquipL01.x = this.equipL01DefaultX
        this.EquipL01.y = this.equipL01DefaultY + (0.6f * OFFSET_SCALE)
        this.EquipL01.z = this.equipL01DefaultZ
        this.EquipL05.zRot = 0.0f

        this.EquipR01.xRot = 0.2618f
        this.EquipR01.yRot = -0.1745f
        this.EquipR01.zRot = 0.0f
        this.EquipR01.x = this.equipR01DefaultX
        this.EquipR01.y = this.equipR01DefaultY + (0.6f * OFFSET_SCALE)
        this.EquipR01.z = this.equipR01DefaultZ
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
            this.poseTranslateY += angleX * 0.05f + 0.025f
        }

        this.Head.xRot = headPitch * (Math.PI.toFloat() / 180f) * 0.8f
        this.Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f) * 0.57f
        this.Head.zRot = 0.0f

        val headX = this.Head.xRot * -0.5f
        val headZ = this.Head.zRot * -0.5f

        this.BoobL.xRot = angleX * 0.08f - 0.7f
        this.BoobR.xRot = angleX * 0.08f - 0.7f
        this.Ahoke.yRot = angleX * 0.15f + 0.7f
        this.Neck.xRot = 0.1f
        this.BodyMain.xRot = -0.1047f
        this.Butt.xRot = 0.3142f
        this.Skirt01.xRot = -0.14f
        this.Skirt02.xRot = -0.087f

        this.Hair01.xRot = angleX * 0.03f + 0.21f + headX
        this.Hair01.zRot = headZ
        this.Hair02.xRot = -angleX1 * 0.04f - 0.09f + headX
        this.Hair02.zRot = headZ
        this.Hair03.xRot = -angleX2 * 0.07f - 0.14f
        this.Hair03.zRot = 0.0f

        this.HairL01.zRot = headZ - 0.087f
        this.HairL02.zRot = headZ + 0.087f
        this.HairR01.zRot = headZ + 0.087f
        this.HairR02.zRot = headZ - 0.052f
        this.HairL01.xRot = angleX * 0.02f + headX - 0.14f
        this.HairL02.xRot = angleX * 0.02f + headX + 0.17f
        this.HairR01.xRot = angleX * 0.02f + headX - 0.14f
        this.HairR02.xRot = angleX * 0.02f + headX + 0.17f

        this.ArmLeft01.xRot = 0.2618f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = -0.7f
        this.ArmLeft03.xRot = -0.14f
        this.ArmLeft03.zRot = 1.4835f
        this.ArmLeft05.zRot = 0.2618f
        this.ArmRight01.xRot = angleAdd1 * 0.375f + 0.2618f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = 0.2618f
        this.ArmRight03.xRot = 0.0f
        this.ArmRight03.zRot = 0.0f

        this.LegLeft01.xRot = angleAdd1 * 0.6f - 0.35f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.14f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.yRot = 0.0f
        this.LegLeft02.zRot = 0.0f
        this.ShoesL04.xRot = -0.1f

        this.LegRight01.xRot = angleAdd2 * 0.6f - 0.07f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.14f
        this.LegRight02.xRot = 0.0f
        this.LegRight02.yRot = 0.0f
        this.LegRight02.zRot = 0.0f

        this.EquipBase.xRot = 0.0f
        this.EquipL01.xRot = 0.2618f
        this.EquipL01.yRot = 0.1745f
        this.EquipL01.zRot = 0.0f
        this.EquipL01.y = this.equipL01DefaultY + (angleX * 0.125f * OFFSET_SCALE)
        this.EquipL05.zRot = 0.0f

        this.EquipR01.xRot = 0.2618f
        this.EquipR01.yRot = -0.1745f
        this.EquipR01.zRot = 0.0f
        this.EquipR01.y = this.equipR01DefaultY + (-angleX * 0.125f * OFFSET_SCALE)
    }

    private fun applySpecialPoseAdjustments(entity: T?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        val angleX = Mth.cos(ageInTicks * 0.08f)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount * 0.7f
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount * 0.7f

        var addk1 = angleAdd1 * 0.6f - 0.35f
        var addk2 = angleAdd2 * 0.6f - 0.07f

        val isSprinting = entity != null && entity.isSprinting
        val isCrouching = entity != null && entity.isCrouching()
        val isPassenger = entity != null && entity.isPassenger()
        val isSitting = entity != null && (entity.isInSittingPose || entity.isPassenger())

        if (isSprinting || limbSwingAmount > 0.9f) {
            this.Hair01.xRot += 0.09f
            this.Hair02.xRot += 0.43f
            this.Hair03.xRot += 0.49f
            this.BoobL.xRot = angleAdd2 * 0.1f - 0.83f
            this.BoobR.xRot = angleAdd1 * 0.1f - 0.83f
            this.ArmLeft01.xRot = angleAdd2 * 0.6f + 0.2618f
            this.ArmLeft01.yRot = 0.0f
            this.ArmLeft01.zRot = -0.3f
            this.ArmLeft03.xRot = 0.0f
            this.ArmLeft03.zRot = 0.0f
            this.ArmLeft05.zRot = 0.0f
            this.ArmRight01.xRot = angleAdd1 * 0.6f + 0.2618f
            this.ArmRight01.yRot = 0.0f
            this.ArmRight01.zRot = 0.3f
        }

        if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            this.Head.xRot -= 0.6283f
            this.BodyMain.xRot = 0.8727f
            this.Butt.xRot = -0.6283f
            this.Skirt01.xRot = -0.1745f
            this.Skirt02.xRot = -0.2618f
            this.ArmLeft01.xRot = 0.2618f
            this.ArmLeft01.yRot = 0.0f
            this.ArmLeft01.zRot = 0.2618f
            this.ArmLeft03.xRot = 0.0f
            this.ArmLeft03.zRot = 0.0f
            this.ArmLeft05.zRot = 0.0f
            this.ArmRight01.xRot = 0.2618f
            this.ArmRight01.yRot = 0.0f
            this.ArmRight01.zRot = -0.2618f
            this.ArmRight05.zRot = 0.0f
            this.Hair01.xRot += 0.37f
            this.Hair02.xRot += 0.23f
            this.Hair03.xRot -= 0.1f
        }

        if (isPassenger && entity.getVehicle() is EntityMountBase) {
            this.isSittingPose = true
            this.EquipBase.visible = true
            if (isSitting) {
                if (hasLegacyState(entity, 1, 4)) {
                    this.poseTranslateY += 0.48f * 3
                    val nodTick = ageInTicks.toInt() % 60
                    this.Head.xRot = 0.3f
                    if (nodTick < 30) {
                        if (nodTick < 6) {
                            this.Head.xRot = nodTick * 0.02f + 0.3f
                        } else if (nodTick < 11) {
                            this.Head.xRot = (nodTick - 5) * 0.03f + 0.4f
                        } else if (nodTick < 14) {
                            this.Head.xRot = (nodTick - 10) * -0.09f + 0.55f
                        }
                    }
                    this.Head.yRot = 0.0f
                    this.Head.zRot = 0.0f
                    val headX = this.Head.xRot * -0.5f
                    this.Hair01.xRot = angleX * 0.012f + 0.21f + headX
                    this.Hair02.xRot = angleX * 0.015f - 0.09f + headX
                    this.Neck.xRot = 0.3f
                    this.Butt.xRot = -0.14f
                    this.Skirt01.xRot = -0.1745f
                    this.Skirt02.xRot = -0.2618f
                    this.ArmLeft01.xRot = 0.4f
                    this.ArmLeft01.zRot = -0.2618f
                    this.ArmLeft03.xRot = 0.0f
                    this.ArmLeft03.zRot = 0.0f
                    this.ArmRight01.xRot = 0.4f
                    this.ArmRight01.zRot = 0.2618f
                    this.LegLeft01.zRot = -0.14f
                    this.LegLeft02.xRot = 1.2217f
                    this.LegLeft02.yRot = 1.2217f
                    this.LegLeft02.zRot = -1.0472f
                    this.LegLeft02.x = this.legLeft02DefaultX + (0.175f * OFFSET_SCALE)
                    this.LegLeft02.y = this.legLeft02DefaultY + (-0.02f * OFFSET_SCALE)
                    this.LegLeft02.z = this.legLeft02DefaultZ + (0.1635f * OFFSET_SCALE)
                    this.LegRight01.zRot = 0.14f
                    this.LegRight02.xRot = 1.2217f
                    this.LegRight02.yRot = -1.2217f
                    this.LegRight02.zRot = 1.0472f
                    this.LegRight02.x = this.legRight02DefaultX + (-0.175f * OFFSET_SCALE)
                    this.LegRight02.y = this.legRight02DefaultY + (-0.05f * OFFSET_SCALE)
                    this.LegRight02.z = this.legRight02DefaultZ + (0.1635f * OFFSET_SCALE)
                    addk1 = -1.0472f
                    addk2 = -1.0472f
                    this.LegLeft01.xRot = addk1
                    this.LegRight01.xRot = addk2
                    this.EquipBase.xRot = -0.4f
                    this.EquipL01.x = this.equipL01DefaultX + (-0.3f * OFFSET_SCALE)
                    this.EquipL01.y = this.equipL01DefaultY + (0.6f * OFFSET_SCALE)
                    this.EquipL01.z = this.equipL01DefaultZ + (0.6f * OFFSET_SCALE)
                    this.EquipL01.yRot = 1.4f
                    this.EquipR01.x = this.equipR01DefaultX + (0.3f * OFFSET_SCALE)
                    this.EquipR01.y = this.equipR01DefaultY + (0.6f * OFFSET_SCALE)
                    this.EquipR01.z = this.equipR01DefaultZ + (0.6f * OFFSET_SCALE)
                    this.EquipR01.yRot = -1.4f
                } else {
                    this.poseTranslateY += 0.64f * 3
                    this.poseTranslateZ += -0.11f * 3
                    this.Neck.xRot = 0.35f
                    this.BodyMain.xRot = -0.6283f
                    this.Butt.xRot = -0.6283f
                    this.Skirt01.xRot = -0.1745f
                    this.Skirt02.xRot = -0.2618f
                    this.ArmRight01.xRot = angleX * 0.125f + 0.5236f
                    this.ArmRight01.zRot = 0.45f
                    this.ArmRight03.xRot = -0.5f
                    addk1 = -0.8727f
                    addk2 = -0.35f
                    this.LegLeft01.xRot = addk1
                    this.LegRight01.xRot = addk2
                    this.LegLeft01.zRot = 0.4363f
                    this.LegLeft02.xRot = 0.7854f
                    this.LegRight01.zRot = -0.35f
                    this.LegRight02.xRot = 0.8727f
                    this.ShoesL04.xRot = angleX * 0.25f - 0.1f
                    this.EquipBase.xRot = 0.2f
                    this.EquipL01.x = this.equipL01DefaultX + (-0.25f * OFFSET_SCALE)
                    this.EquipL01.y = this.equipL01DefaultY + (0.1f * OFFSET_SCALE)
                    this.EquipL01.yRot = 1.4f
                    this.EquipR01.x = this.equipR01DefaultX + (0.25f * OFFSET_SCALE)
                    this.EquipR01.y = this.equipR01DefaultY + (0.1f * OFFSET_SCALE)
                    this.EquipR01.yRot = -1.4f
                }
            } else {
                this.poseTranslateY += 0.64f * 3
                this.poseTranslateZ += -0.11f * 3
                this.Neck.xRot = 0.35f
                this.BodyMain.xRot = -0.6283f
                this.Butt.xRot = -0.6283f
                this.Skirt01.xRot = -0.1745f
                this.Skirt02.xRot = -0.2618f
                this.ArmRight01.xRot = angleX * 0.125f + 0.5236f
                this.ArmRight01.zRot = 0.45f
                this.ArmRight03.xRot = -0.5f
                addk1 = -0.8727f
                addk2 = -0.35f
                this.LegLeft01.xRot = addk1
                this.LegRight01.xRot = addk2
                this.LegLeft01.zRot = 0.4363f
                this.LegLeft02.xRot = 0.7854f
                this.LegRight01.zRot = -0.35f
                this.LegRight02.xRot = 0.8727f
                this.ShoesL04.xRot = angleX * 0.25f - 0.1f
                this.EquipBase.xRot = -0.9f
                this.EquipL01.x = this.equipL01DefaultX + (-0.25f * OFFSET_SCALE)
                this.EquipL01.y = this.equipL01DefaultY + (0.1f * OFFSET_SCALE)
                this.EquipL01.z = this.equipL01DefaultZ + (0.45f * OFFSET_SCALE)
                this.EquipL01.yRot = 1.4f
                this.EquipR01.x = this.equipR01DefaultX + (0.25f * OFFSET_SCALE)
                this.EquipR01.y = this.equipR01DefaultY + (0.1f * OFFSET_SCALE)
                this.EquipR01.z = this.equipR01DefaultZ + (0.45f * OFFSET_SCALE)
                this.EquipR01.yRot = -1.4f
            }
        } else if (isSitting) {
            this.isSittingPose = true
            if (entity != null && entity.getStateEmotion(1) == 4) {
                this.poseTranslateY += 0.48f * 3
                val nodTick = ageInTicks.toInt() % 60
                this.Head.xRot = 0.2f
                if (nodTick < 30) {
                    if (nodTick < 6) {
                        this.Head.xRot = nodTick * 0.02f + 0.2f
                    } else if (nodTick < 11) {
                        this.Head.xRot = (nodTick - 5) * 0.03f + 0.3f
                    } else if (nodTick < 14) {
                        this.Head.xRot = (nodTick - 10) * -0.09f + 0.45f
                    }
                }
                this.Head.yRot = 0.0f
                this.Head.zRot = 0.0f
                val headX = this.Head.xRot * -0.5f
                this.Hair01.xRot = angleX * 0.012f + 0.21f + headX
                this.Hair02.xRot = angleX * 0.015f - 0.09f + headX
                this.Neck.xRot = 0.3f
                this.Butt.xRot = -0.14f
                this.Skirt01.xRot = -0.1745f
                this.Skirt02.xRot = -0.2618f
                this.ArmLeft01.xRot = 0.4f
                this.ArmLeft01.zRot = -0.2618f
                this.ArmLeft03.xRot = 0.0f
                this.ArmLeft03.zRot = 0.0f
                this.ArmRight01.xRot = 0.4f
                this.ArmRight01.zRot = 0.2618f
                this.LegLeft01.zRot = -0.14f
                this.LegLeft02.xRot = 1.2217f
                this.LegLeft02.yRot = 1.2217f
                this.LegLeft02.zRot = -1.0472f
                this.LegLeft02.x = this.legLeft02DefaultX + (0.175f * OFFSET_SCALE)
                this.LegLeft02.y = this.legLeft02DefaultY + (-0.02f * OFFSET_SCALE)
                this.LegLeft02.z = this.legLeft02DefaultZ + (0.1635f * OFFSET_SCALE)
                this.LegRight01.zRot = 0.14f
                this.LegRight02.xRot = 1.2217f
                this.LegRight02.yRot = -1.2217f
                this.LegRight02.zRot = 1.0472f
                this.LegRight02.x = this.legRight02DefaultX + (-0.175f * OFFSET_SCALE)
                this.LegRight02.y = this.legRight02DefaultY + (-0.05f * OFFSET_SCALE)
                this.LegRight02.z = this.legRight02DefaultZ + (0.1635f * OFFSET_SCALE)
                addk1 = -1.0472f
                addk2 = -1.0472f
                this.LegLeft01.xRot = addk1
                this.LegRight01.xRot = addk2
                this.EquipL01.y = this.equipL01DefaultY + (0.6f * OFFSET_SCALE)
                this.EquipR01.y = this.equipR01DefaultY + (0.6f * OFFSET_SCALE)
            } else {
                this.poseTranslateY += 0.39f * 3
                this.Neck.xRot = 0.35f
                this.BodyMain.xRot = -0.6283f
                this.Butt.xRot = -0.6283f
                this.Skirt01.xRot = -0.1745f
                this.Skirt02.xRot = -0.2618f
                this.ArmRight01.xRot = angleX * 0.125f + 0.5236f
                this.ArmRight01.zRot = 0.45f
                this.ArmRight03.xRot = -0.5f
                addk1 = -0.8727f
                addk2 = -0.35f
                this.LegLeft01.xRot = addk1
                this.LegRight01.xRot = addk2
                this.LegLeft01.zRot = 0.4363f
                this.LegLeft02.xRot = 0.7854f
                this.LegRight01.zRot = -0.35f
                this.LegRight02.xRot = 0.8727f
                this.ShoesL04.xRot = angleX * 0.25f - 0.1f
                this.EquipL01.x = this.equipL01DefaultX + (-1.9f * OFFSET_SCALE)
                this.EquipL01.y = this.equipL01DefaultY + (0.6f * OFFSET_SCALE)
                this.EquipL01.z = this.equipL01DefaultZ + (0.4f * OFFSET_SCALE)
                this.EquipL01.xRot = 0.0f
                this.EquipL01.yRot = 1.57f
                this.EquipL05.zRot = -1.0f
                this.EquipR01.x = this.equipR01DefaultX + (1.9f * OFFSET_SCALE)
                this.EquipR01.y = this.equipR01DefaultY + (-1.0f * OFFSET_SCALE)
                this.EquipR01.z = this.equipR01DefaultZ + (-0.4f * OFFSET_SCALE)
                this.EquipR01.xRot = -1.5708f
                this.EquipR01.yRot = 0.6f
                this.EquipR01.zRot = -1.5708f
            }
        }

        if (entity != null && entity.attackTick > 0) {
            if (isPassenger) {
                this.ArmRight01.xRot = -1.1f
                this.ArmRight03.xRot = 0.0f
                this.EquipBase.visible = true
                this.EquipBase.xRot = -1.2f + this.Head.xRot
                this.EquipL01.xRot = -0.1f
                this.EquipR01.xRot = 0.1f
            } else {
                this.ArmRight01.xRot = -1.5f
                this.EquipBase.visible = true
                this.EquipBase.xRot = -1.6f + this.Head.xRot
                this.EquipL01.y = this.equipL01DefaultY
                this.EquipR01.y = this.equipR01DefaultY
            }
        }

        val partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
        val customAttackAnim = if (entity != null) entity.getCustomAttackAnim(partialTick) else 0.0f
        if (customAttackAnim > 0.0f) {
            val f7 = Mth.sin(customAttackAnim * customAttackAnim * Math.PI.toFloat())
            val f8 = Mth.sin(Mth.sqrt(customAttackAnim) * Math.PI.toFloat())
            this.ArmRight01.xRot = -0.4f
            this.ArmRight01.yRot = 0.0f
            this.ArmRight01.zRot = -0.2f
            this.ArmRight01.xRot += -f8 * 80.0f * (Math.PI.toFloat() / 180f)
            this.ArmRight01.yRot += -f7 * 20.0f * (Math.PI.toFloat() / 180f) + 0.2f
            this.ArmRight01.zRot += -f8 * 20.0f * (Math.PI.toFloat() / 180f)
        }
    }

    private fun syncGlowParts() {
        this.GlowBodyMain.copyFrom(this.BodyMain)
        this.GlowBodyMain2.copyFrom(this.BodyMain)
        this.GlowNeck.copyFrom(this.Neck)
        this.GlowHead.copyFrom(this.Head)
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
        this.HeadS02.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        this.HeadS03.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

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

        this.GlowBodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        this.GlowBodyMain2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "carrier_w_demon"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelCarrierWDemon")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelCarrierWDemon")
        private const val MODEL_SCALE = 0.47f
        private const val MODEL_OFFSET_Y = 1.7f

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 104).addBox(-6.5f, -11f, -4f, 13f, 17f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(0, 9).addBox(-3.5f, 0f, 0f, 6f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offset(0.5f, -4.7f, -6.3f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(2, 85).addBox(-3f, -1f, -2.5f, 5f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offset(-7.8f, -9.3f, -0.7f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(25, 85).addBox(-3f, 0f, -3f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, 3f, 0f)
            )

            val ArmRight03 = ArmRight02.addOrReplaceChild(
                "ArmRight03",
                CubeListBuilder.create().texOffs(100, 14).addBox(-1.5f, 0f, -6.5f, 7f, 8f, 7f, CubeDeformation(0f)),
                PartPose.offset(-2f, 10f, 3f)
            )

            val ArmRight04 = ArmRight03.addOrReplaceChild(
                "ArmRight04",
                CubeListBuilder.create().texOffs(54, 49).addBox(-4f, 0f, -4f, 8f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, 8f, -3f, 0f, 0.0873f, 0f)
            )

            val ArmRight05 = ArmRight04.addOrReplaceChild(
                "ArmRight05",
                CubeListBuilder.create().texOffs(72, 36).addBox(-2.5f, 0f, -3f, 5f, 5f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.5f, 0f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(2, 85).mirror()
                    .addBox(-2f, -1f, -2.5f, 5f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offset(7.8f, -9.3f, -0.7f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(25, 85).mirror()
                    .addBox(-3f, 0f, -3f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(0.5f, 3f, 0f)
            )

            val ArmLeft03 = ArmLeft02.addOrReplaceChild(
                "ArmLeft03",
                CubeListBuilder.create().texOffs(100, 14).mirror()
                    .addBox(-5.5f, 0f, -6.5f, 7f, 8f, 7f, CubeDeformation(0f)),
                PartPose.offset(2f, 10f, 3f)
            )

            val ArmLeft04 = ArmLeft03.addOrReplaceChild(
                "ArmLeft04",
                CubeListBuilder.create().texOffs(54, 49).mirror().addBox(-4f, 0f, -4f, 8f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, 8f, -3f, 0f, -0.0873f, 0f)
            )

            val ArmLeft05 = ArmLeft04.addOrReplaceChild(
                "ArmLeft05",
                CubeListBuilder.create().texOffs(72, 36).mirror()
                    .addBox(-2.5f, 0f, -3f, 5f, 5f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.5f, 0f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(52, 61).addBox(-7.5f, 0f, -5.7f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, 4f, 1.3f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 84).mirror().addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offset(4.8f, 5.5f, -2.6f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 95).mirror().addBox(-3f, 0f, 0f, 6f, 3f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 14f, -3f)
            )

            val ShoesL01 = LegLeft02.addOrReplaceChild(
                "ShoesL01",
                CubeListBuilder.create().texOffs(99, 1).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1f, 2.6f, 0.2618f, 0f, 0f)
            )

            val ShoesL02 = ShoesL01.addOrReplaceChild(
                "ShoesL02",
                CubeListBuilder.create().texOffs(98, 0).mirror()
                    .addBox(-3.5f, 0f, -4f, 7f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, 3f, -0.7f)
            )

            val ShoesL03 = ShoesL02.addOrReplaceChild(
                "ShoesL03",
                CubeListBuilder.create().texOffs(66, 0).mirror().addBox(-4f, 0f, -4f, 8f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, 4f, -0.9f)
            )

            val ShoesL04 = ShoesL03.addOrReplaceChild(
                "ShoesL04",
                CubeListBuilder.create().texOffs(32, 0).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 4f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3f, -1f, -0.12f, 0f, 0f)
            )

            val Skirt01 = Butt.addOrReplaceChild(
                "Skirt01",
                CubeListBuilder.create().texOffs(0, 22).addBox(-8.5f, 0f, -6f, 17f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 2.9f, 0f)
            )

            val Skirt02 = Skirt01.addOrReplaceChild(
                "Skirt02",
                CubeListBuilder.create().texOffs(42, 47).addBox(-9f, 0f, -6f, 18f, 4f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, 2.8f, -0.5f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 84).addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offset(-4.8f, 5.5f, -2.6f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 95).addBox(-3f, 0f, 0f, 6f, 3f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 14f, -3f)
            )

            val ShoesR01 = LegRight02.addOrReplaceChild(
                "ShoesR01",
                CubeListBuilder.create().texOffs(99, 1).addBox(-3.5f, 0f, -3.5f, 7f, 4f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1f, 2.6f, 0.2618f, 0f, 0f)
            )

            val ShoesR02 = ShoesR01.addOrReplaceChild(
                "ShoesR02",
                CubeListBuilder.create().texOffs(98, 0).addBox(-3.5f, 0f, -4f, 7f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, 3f, -0.7f)
            )

            val ShoesR03 = ShoesR02.addOrReplaceChild(
                "ShoesR03",
                CubeListBuilder.create().texOffs(66, 0).addBox(-4f, 0f, -4f, 8f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, 4f, -0.9f)
            )

            val ShoesR04 = ShoesR03.addOrReplaceChild(
                "ShoesR04",
                CubeListBuilder.create().texOffs(32, 0).addBox(-3.5f, 0f, -3.5f, 7f, 4f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3f, -1f, -0.12f, 0f, 0f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(33, 101).mirror()
                    .addBox(-3.5f, 0f, 0f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.7f, -8.1f, -3.7f, -0.6981f, 0.1396f, 0.0873f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(33, 101).addBox(-3.5f, 0f, 0f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.7f, -8.1f, -3.7f, -0.6981f, -0.1396f, -0.0873f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(88, 29).addBox(-5.5f, -2f, -5f, 11f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, -10.3f, -0.2f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -1.5f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 77).addBox(-8f, -8f, -7.2f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(104, 29).addBox(0f, -4f, -11.5f, 0f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -9f, -5.5f, 0f, 0.6981f, 0f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(86, 101).mirror().addBox(-1f, 0f, 0f, 2f, 8f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7f, 3f, -5.5f, -0.1396f, 0.1745f, 0.0873f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(86, 101).mirror().addBox(-1f, 0f, 0f, 2f, 9f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 7f, 0f, 0.1745f, 0f, -0.0524f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(86, 101).addBox(-1f, 0f, 0f, 2f, 8f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, 3f, -5.5f, -0.1396f, -0.1745f, -0.0873f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(86, 101).addBox(-1f, 0f, 0f, 2f, 9f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0.1745f, 0f, 0.0873f)
            )

            val HeadS05 = Head.addOrReplaceChild(
                "HeadS05",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8.1f, -7.5f, -6.7f, 0.7854f, 0f, 0f)
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

            val HeadS04 = Head.addOrReplaceChild(
                "HeadS04",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8.1f, -7.5f, -6.7f, 0.7854f, 0f, 0f)
            )

            val HeadS02 = partdefinition.addOrReplaceChild(
                "HeadS02",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 3f, 3f, 0f, CubeDeformation(0f)),
                PartPose.offset(2.9f, -2.5f, 0f)
            )

            val HeadS03 = partdefinition.addOrReplaceChild(
                "HeadS03",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 3f, 3f, 0f, CubeDeformation(0f)),
                PartPose.offset(-2.5f, 2.9f, 0f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -10.3f, -0.2f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -1.5f, 0f)
            )

            addFaceLayer(GlowHead)

            val GlowBodyMain2 = partdefinition.addOrReplaceChild(
                "GlowBodyMain2",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val EquipBase = GlowBodyMain2.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -23f, 0f)
            )

            val EquipL01 = EquipBase.addOrReplaceChild(
                "EquipL01",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0f, 0f, -3.5f, 2f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offset(30f, 0f, 0f)
            )

            val EquipL02 = EquipL01.addOrReplaceChild(
                "EquipL02",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-0.5f, 0f, -10f, 3f, 2f, 20f, CubeDeformation(0f)),
                PartPose.offset(0f, 2f, 0f)
            )

            val EquipL03 = EquipL02.addOrReplaceChild(
                "EquipL03",
                CubeListBuilder.create().texOffs(43, 0).mirror()
                    .addBox(0f, 0f, -8.5f, 2f, 9f, 17f, CubeDeformation(0f)),
                PartPose.offset(0f, 2f, 0f)
            )

            val EquipL04 = EquipL03.addOrReplaceChild(
                "EquipL04",
                CubeListBuilder.create().texOffs(67, 14).mirror()
                    .addBox(0f, 0f, -6.5f, 2f, 9f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 9f, 0f)
            )

            val EquipL05 = EquipL04.addOrReplaceChild(
                "EquipL05",
                CubeListBuilder.create().texOffs(46, 29).mirror()
                    .addBox(0f, 0f, -4.5f, 2f, 9f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 9f, 0f)
            )

            val EquipR01 = EquipBase.addOrReplaceChild(
                "EquipR01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, 0f, -3.5f, 2f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offset(-30f, 0f, 0f)
            )

            val EquipR02 = EquipR01.addOrReplaceChild(
                "EquipR02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, 0f, -10f, 3f, 2f, 20f, CubeDeformation(0f)),
                PartPose.offset(0f, 2f, 0f)
            )

            val EquipR03 = EquipR02.addOrReplaceChild(
                "EquipR03",
                CubeListBuilder.create().texOffs(43, 0).addBox(-2f, 0f, -8.5f, 2f, 9f, 17f, CubeDeformation(0f)),
                PartPose.offset(0f, 2f, 0f)
            )

            val EquipR04 = EquipR03.addOrReplaceChild(
                "EquipR04",
                CubeListBuilder.create().texOffs(67, 14).addBox(-2f, 0f, -6.5f, 2f, 9f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 9f, 0f)
            )

            val EquipR05 = EquipR04.addOrReplaceChild(
                "EquipR05",
                CubeListBuilder.create().texOffs(46, 29).addBox(-2f, 0f, -4.5f, 2f, 9f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 9f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
