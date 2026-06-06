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
import org.trp.shincolle.entity.EntityCarrierHime
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelCarrierHime<T : EntityShipBase>(root: ModelPart) : ShipModelHumanoidBase<T>(), IGlowableModel {
    private var isDeadPose = false
    override val poseTranslateY = 0f
    private var poseTranslateZ = 0f
    private var isSittingPose = false

    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val Butt: ModelPart
    private val ArmRight01: ModelPart
    private val ArmLeft01: ModelPart
    private val Cloth01: ModelPart
    private val Cloth02: ModelPart
    private val Head: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val Ahoke: ModelPart
    private val HairL01: ModelPart
    private val HairR01: ModelPart
    private val HairL02: ModelPart
    private val HairR02: ModelPart
    private val Hair01: ModelPart
    private val Hair04: ModelPart
    private val Hair02: ModelPart
    private val Hair03: ModelPart
    private val Hair05: ModelPart
    private val Hair06: ModelPart
    private val Hair07: ModelPart
    private val LegLeft01: ModelPart
    private val Skirt01: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft02: ModelPart
    private val LegLeft01a: ModelPart
    private val LegLeft01b: ModelPart
    private val ShoesL01: ModelPart
    private val ShoesL02: ModelPart
    private val ShoesL03: ModelPart
    private val ShoesL04: ModelPart
    private val Skirt02: ModelPart
    private val LegRight01a: ModelPart
    private val LegRight01b: ModelPart
    private val LegRight02: ModelPart
    private val ShoesR01: ModelPart
    private val ShoesR02: ModelPart
    private val ShoesR03: ModelPart
    private val ShoesR04: ModelPart
    private val ArmRight01a: ModelPart
    private val ArmRight01b: ModelPart
    private val ArmRight02: ModelPart
    private val EquipSR01: ModelPart
    private val EquipSR02: ModelPart
    private val EquipSR04: ModelPart
    private val EquipSR03: ModelPart
    private val EquipSR05: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmLeft01a: ModelPart
    private val ArmLeft01b: ModelPart
    private val EquipSL01: ModelPart
    private val EquipSL02: ModelPart
    private val EquipSL04: ModelPart
    private val EquipSL03: ModelPart
    private val EquipSL05: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowBodyMain2: ModelPart
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val GlowArmLeft01: ModelPart
    private val GlowArmLeft02: ModelPart
    private val GlowArmRight01: ModelPart
    private val GlowArmRight02: ModelPart
    private val legLeft02DefaultX: Float
    private val legLeft02DefaultY: Float
    private val legLeft02DefaultZ: Float
    private val legRight02DefaultX: Float
    private val legRight02DefaultY: Float
    private val legRight02DefaultZ: Float
    private val equipSL01DefaultX: Float
    private val equipSL01DefaultY: Float
    private val equipSL01DefaultZ: Float
    private val equipSR01DefaultX: Float
    private val equipSR01DefaultY: Float
    private val equipSR01DefaultZ: Float
    private val armLeft02DefaultX: Float
    private val armLeft02DefaultY: Float
    private val armLeft02DefaultZ: Float
    private val armRight02DefaultX: Float
    private val armRight02DefaultY: Float
    private val armRight02DefaultZ: Float
    private val skirt01DefaultY: Float
    private val skirt02DefaultY: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.ShoesR01 = this.LegRight02.getChild("ShoesR01")
        this.ShoesR02 = this.ShoesR01.getChild("ShoesR02")
        this.ShoesR03 = this.ShoesR02.getChild("ShoesR03")
        this.ShoesR04 = this.ShoesR03.getChild("ShoesR04")
        this.LegRight01a = this.LegRight01.getChild("LegRight01a")
        this.LegRight01b = this.LegRight01.getChild("LegRight01b")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.Skirt02 = this.Skirt01.getChild("Skirt02")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.ShoesL01 = this.LegLeft02.getChild("ShoesL01")
        this.ShoesL02 = this.ShoesL01.getChild("ShoesL02")
        this.ShoesL03 = this.ShoesL02.getChild("ShoesL03")
        this.ShoesL04 = this.ShoesL03.getChild("ShoesL04")
        this.LegLeft01a = this.LegLeft01.getChild("LegLeft01a")
        this.LegLeft01b = this.LegLeft01.getChild("LegLeft01b")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft01b = this.ArmLeft01.getChild("ArmLeft01b")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.ArmLeft01a = this.ArmLeft01.getChild("ArmLeft01a")
        this.Cloth02 = this.BodyMain.getChild("Cloth02")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.Hair03 = this.Hair02.getChild("Hair03")
        this.Hair04 = this.HairMain.getChild("Hair04")
        this.Hair05 = this.Hair04.getChild("Hair05")
        this.Hair06 = this.Hair05.getChild("Hair06")
        this.Hair07 = this.Hair06.getChild("Hair07")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.ArmRight01a = this.ArmRight01.getChild("ArmRight01a")
        this.ArmRight01b = this.ArmRight01.getChild("ArmRight01b")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        initFaceParts(this.GlowHead)
        this.GlowBodyMain2 = root.getChild("GlowBodyMain2")
        this.GlowArmLeft01 = this.GlowBodyMain2.getChild("GlowArmLeft01")
        this.GlowArmLeft02 = this.GlowArmLeft01.getChild("GlowArmLeft02")
        this.EquipSL01 = this.GlowArmLeft02.getChild("EquipSL01")
        this.EquipSL02 = this.EquipSL01.getChild("EquipSL02")
        this.EquipSL03 = this.EquipSL02.getChild("EquipSL03")
        this.EquipSL04 = this.EquipSL01.getChild("EquipSL04")
        this.EquipSL05 = this.EquipSL04.getChild("EquipSL05")
        this.GlowArmRight01 = this.GlowBodyMain2.getChild("GlowArmRight01")
        this.GlowArmRight02 = this.GlowArmRight01.getChild("GlowArmRight02")
        this.EquipSR01 = this.GlowArmRight02.getChild("EquipSR01")
        this.EquipSR02 = this.EquipSR01.getChild("EquipSR02")
        this.EquipSR03 = this.EquipSR02.getChild("EquipSR03")
        this.EquipSR04 = this.EquipSR01.getChild("EquipSR04")
        this.EquipSR05 = this.EquipSR04.getChild("EquipSR05")
        this.legLeft02DefaultX = this.LegLeft02.x
        this.legLeft02DefaultY = this.LegLeft02.y
        this.legLeft02DefaultZ = this.LegLeft02.z
        this.legRight02DefaultX = this.LegRight02.x
        this.legRight02DefaultY = this.LegRight02.y
        this.legRight02DefaultZ = this.LegRight02.z
        this.equipSL01DefaultX = this.EquipSL01.x
        this.equipSL01DefaultY = this.EquipSL01.y
        this.equipSL01DefaultZ = this.EquipSL01.z
        this.equipSR01DefaultX = this.EquipSR01.x
        this.equipSR01DefaultY = this.EquipSR01.y
        this.equipSR01DefaultZ = this.EquipSR01.z
        this.armLeft02DefaultX = this.ArmLeft02.x
        this.armLeft02DefaultY = this.ArmLeft02.y
        this.armLeft02DefaultZ = this.ArmLeft02.z
        this.armRight02DefaultX = this.ArmRight02.x
        this.armRight02DefaultY = this.ArmRight02.y
        this.armRight02DefaultZ = this.ArmRight02.z
        this.skirt01DefaultY = this.Skirt01.y
        this.skirt02DefaultY = this.Skirt02.y
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
        this.LegLeft02.x = this.legLeft02DefaultX
        this.LegLeft02.y = this.legLeft02DefaultY
        this.LegLeft02.z = this.legLeft02DefaultZ
        this.LegRight02.x = this.legRight02DefaultX
        this.LegRight02.y = this.legRight02DefaultY
        this.LegRight02.z = this.legRight02DefaultZ
        this.EquipSL01.x = this.equipSL01DefaultX
        this.EquipSL01.y = this.equipSL01DefaultY
        this.EquipSL01.z = this.equipSL01DefaultZ
        this.EquipSR01.x = this.equipSR01DefaultX
        this.EquipSR01.y = this.equipSR01DefaultY
        this.EquipSR01.z = this.equipSR01DefaultZ
        this.ArmLeft02.x = this.armLeft02DefaultX
        this.ArmLeft02.y = this.armLeft02DefaultY
        this.ArmLeft02.z = this.armLeft02DefaultZ
        this.ArmRight02.x = this.armRight02DefaultX
        this.ArmRight02.y = this.armRight02DefaultY
        this.ArmRight02.z = this.armRight02DefaultZ
        this.Skirt01.y = this.skirt01DefaultY
        this.Skirt02.y = this.skirt02DefaultY
    }

    private fun applyEquipVisibility(entity: T?) {
        val showLeft = entity!!.getEquipFlag(EntityCarrierHime.EQUIP_LEFT)
        val showRight = entity.getEquipFlag(EntityCarrierHime.EQUIP_RIGHT)
        val showAny = showLeft || showRight
        this.GlowBodyMain2.visible = showAny
        this.GlowArmLeft01.visible = showLeft
        this.GlowArmRight01.visible = showRight
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        this.Head.xRot = 0.65f
        this.Head.yRot = 0.0f
        this.Head.zRot = 0.0f
        this.BoobL.xRot = -0.75f
        this.BoobR.xRot = -0.75f
        this.Ahoke.yRot = 0.7f
        this.BodyMain.xRot = -0.2f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = -0.14f
        this.Skirt01.xRot = -0.1745f
        this.Skirt02.xRot = -0.2618f
        this.Hair01.xRot = -0.1f
        this.Hair01.zRot = 0.0f
        this.Hair02.xRot = -0.2f
        this.Hair02.zRot = 0.0f
        this.Hair03.xRot = -0.14f
        this.Hair03.zRot = 0.0f
        this.Hair05.xRot = -0.4f
        this.Hair05.zRot = 0.0f
        this.Hair06.xRot = 0.14f
        this.Hair06.zRot = 0.0f
        this.Hair07.xRot = -0.2f
        this.Hair07.zRot = 0.0f
        this.HairL01.xRot = -0.14f
        this.HairL01.zRot = 0.087f
        this.HairL02.xRot = 0.17f
        this.HairL02.zRot = 0.087f
        this.HairR01.xRot = -0.14f
        this.HairR01.zRot = 0.087f
        this.HairR02.xRot = 0.17f
        this.HairR02.zRot = -0.052f

        this.ArmLeft01.xRot = 0.2f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = -0.2618f
        this.ArmLeft02.xRot = 0.0f
        this.ArmRight01.xRot = 0.2f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = 0.2618f
        this.ArmRight02.xRot = 0.0f
        this.ArmRight02.yRot = 0.0f
        this.ArmRight02.zRot = 0.0f

        this.LegLeft01.xRot = -0.9f
        this.LegLeft01.zRot = -0.14f
        this.LegLeft02.xRot = 1.2217f
        this.LegLeft02.yRot = 1.2217f
        this.LegLeft02.zRot = -1.0472f
        this.LegLeft02.x = this.legLeft02DefaultX + (0.22f * OFFSET_SCALE)
        this.LegLeft02.y = this.legLeft02DefaultY + (-0.03f * OFFSET_SCALE)
        this.LegLeft02.z = this.legLeft02DefaultZ + (0.2f * OFFSET_SCALE)

        this.LegRight01.xRot = -0.9f
        this.LegRight01.zRot = 0.14f
        this.LegRight02.xRot = 1.2217f
        this.LegRight02.yRot = -1.2217f
        this.LegRight02.zRot = 1.0472f
        this.LegRight02.x = this.legRight02DefaultX + (-0.22f * OFFSET_SCALE)
        this.LegRight02.y = this.legRight02DefaultY + (-0.03f * OFFSET_SCALE)
        this.LegRight02.z = this.legRight02DefaultZ + (0.2f * OFFSET_SCALE)
    }

    private fun applyBasePose(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f)
        val angleX2 = Mth.cos(ageInTicks * 0.08f + 0.6f + limbSwing * 0.5f)
        val angleX = Mth.cos(ageInTicks * 0.08f)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount

        if (entity!!.shipDepth > 0.0) {
            this.poseTranslateY += angleX * 0.05f + 0.025f
        }

        this.Head.xRot = headPitch * (Math.PI.toFloat() / 180f) * 0.8f
        this.Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f) * 0.57f
        this.Head.zRot = 0.0f

        this.BoobL.xRot = angleX * 0.06f - 0.75f
        this.BoobR.xRot = angleX * 0.06f - 0.75f
        this.Ahoke.yRot = angleX * 0.2f + 0.7f
        this.BodyMain.xRot = -0.1047f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = 0.35f
        this.Skirt01.xRot = -0.14f
        this.Skirt02.xRot = -0.087f

        this.Hair01.xRot = angleX * 0.03f + 0.21f
        this.Hair01.zRot = 0.0f
        this.Hair02.xRot = -angleX1 * 0.04f - 0.087f
        this.Hair02.zRot = 0.0f
        this.Hair03.xRot = -angleX2 * 0.07f - 0.14f
        this.Hair03.zRot = 0.0f
        this.Hair05.xRot = angleX * 0.06f + 0.1f
        this.Hair05.zRot = 0.0f
        this.Hair06.xRot = -angleX1 * 0.08f + 0.14f
        this.Hair06.zRot = 0.0f
        this.Hair07.xRot = -angleX2 * 0.1f - 0.2f
        this.Hair07.zRot = 0.0f
        this.HairL01.xRot = angleX * 0.04f - 0.14f
        this.HairL01.zRot = 0.0f
        this.HairL02.xRot = -angleX1 * 0.06f + 0.17f
        this.HairL02.zRot = 0.0f
        this.HairR01.xRot = angleX * 0.04f - 0.14f
        this.HairR01.zRot = 0.0f
        this.HairR02.xRot = -angleX1 * 0.06f + 0.17f
        this.HairR02.zRot = 0.0f

        this.ArmLeft01.xRot = angleAdd2 * 0.25f + 0.35f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = angleX * 0.03f - 0.26f
        this.ArmLeft02.xRot = 0.0f
        this.ArmRight01.xRot = angleAdd1 * 0.25f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = -angleX * 0.03f + 0.26f
        this.ArmRight02.xRot = 0.0f
        this.ArmRight02.yRot = 0.0f
        this.ArmRight02.zRot = 0.0f

        this.LegLeft01.zRot = 0.1f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.yRot = 0.0f
        this.LegLeft02.zRot = 0.0f
        this.LegRight01.zRot = -0.1f
        this.LegRight02.xRot = 0.0f
        this.LegRight02.yRot = 0.0f
        this.LegRight02.zRot = 0.0f

        this.EquipSL01.xRot = -1.57f
        this.EquipSL01.yRot = 0.0f
        this.EquipSL01.zRot = 1.57f
        this.EquipSR01.xRot = -1.57f
        this.EquipSR01.yRot = 0.0f
        this.EquipSR01.zRot = 1.57f
    }

    private fun applySpecialPoseAdjustments(entity: T?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        val angleX = Mth.cos(ageInTicks * 0.08f)

        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount
        var addk1 = angleAdd1 * 0.5f - 0.35f
        var addk2 = angleAdd2 * 0.5f - 0.1745f

        val isSprinting = entity != null && entity.isSprinting
        val isCrouching = entity != null && entity.isCrouching()
        val isPassenger = entity != null && entity.isPassenger()
        val isSitting = entity != null && (entity.isInSittingPose || entity.isPassenger())

        if (isSprinting || limbSwingAmount > 0.95f) {
            this.poseTranslateY += 0.05f
            this.Head.xRot -= 0.4f
            this.BodyMain.xRot = 0.7f
            this.Butt.xRot -= 0.7f
            this.Skirt01.xRot = -0.15f
            this.Skirt02.xRot = -0.32f
            this.Hair01.xRot += 0.3f
            this.ArmLeft01.xRot = 0.4f
            this.ArmLeft01.yRot = -0.5f
            this.ArmLeft01.zRot = -0.7f
            this.ArmRight01.xRot = 0.4f
            this.ArmRight01.yRot = 0.5f
            this.ArmRight01.zRot = 0.7f
        }

        if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            this.Head.xRot -= 1.0472f
            this.BodyMain.xRot = 1.0472f
            this.Butt.xRot = -0.4f
            this.Skirt01.xRot = -0.12f
            this.Skirt02.xRot = -0.16f
            this.Skirt02.y = this.skirt02DefaultY + (-0.1f * OFFSET_SCALE)
            this.Hair02.xRot -= 0.3f
            this.Hair03.xRot -= 0.3f

            this.ArmLeft01.xRot = -0.6f
            this.ArmLeft01.zRot = 0.2618f
            this.ArmRight01.xRot = -0.6f
            this.ArmRight01.zRot = -0.2618f

            addk1 -= 0.4f
            addk2 -= 0.4f
        }

        if (isPassenger && entity.getVehicle() is EntityMountBase) {
            this.isSittingPose = true
            if (isSitting) {
                if (hasLegacyState(entity, 1, 4)) {
                    this.poseTranslateY += 0.65f * 3
                    this.poseTranslateZ += -0.27f * 3
                    this.Head.xRot = -1.2217f
                    this.Head.yRot = 0.0f
                    this.Head.zRot = 0.0f
                    this.BodyMain.xRot = 1.2217f
                    this.Hair02.xRot += 0.2f
                    this.Hair03.xRot += 0.2f
                    this.Hair05.xRot -= 0.6f
                    this.Hair06.xRot -= 0.5f
                    this.ArmLeft01.xRot = -2.0f
                    this.ArmLeft01.yRot = -0.1f
                    this.ArmLeft01.zRot = -0.1f
                    this.ArmLeft02.xRot = -2.5f
                    this.ArmLeft02.y = this.armLeft02DefaultY + (0.1f * OFFSET_SCALE)
                    this.ArmLeft02.z = this.armLeft02DefaultZ + (-0.3f * OFFSET_SCALE)
                    this.ArmRight01.xRot = -2.0f
                    this.ArmRight01.yRot = 0.1f
                    this.ArmRight01.zRot = 0.1f
                    this.ArmRight02.xRot = -2.5f
                    this.ArmRight02.y = this.armRight02DefaultY + (0.1f * OFFSET_SCALE)
                    this.ArmRight02.z = this.armRight02DefaultZ + (-0.3f * OFFSET_SCALE)
                    addk1 = 0.0f
                    addk2 = 0.0f
                    this.LegLeft02.xRot = angleX * 0.4f + 0.8f
                    this.LegRight02.xRot = -angleX * 0.4f + 0.8f
                    this.GlowBodyMain2.visible = false
                } else {
                    this.poseTranslateY += 0.51f * 3
                    this.Head.yRot = -0.4f
                    this.Head.zRot = 0.2f
                    this.BodyMain.xRot = -0.25f
                    this.Butt.xRot = -0.2f
                    this.Skirt01.xRot = -0.13f
                    this.Skirt01.y = this.skirt01DefaultY + (-0.05f * OFFSET_SCALE)
                    this.Skirt02.xRot = -0.13f
                    this.Skirt02.y = this.skirt02DefaultY + (-0.05f * OFFSET_SCALE)
                    this.ArmLeft01.xRot = 0.35f
                    this.ArmLeft01.zRot = -0.2618f
                    this.ArmRight01.xRot = -0.4f
                    this.ArmRight01.zRot = 0.4f
                    addk1 = -0.9f
                    addk2 = -0.9f
                    this.LegLeft01.zRot = -0.14f
                    this.LegLeft02.xRot = 1.2217f
                    this.LegLeft02.yRot = 1.2217f
                    this.LegLeft02.zRot = -1.0472f
                    this.LegLeft02.x = this.legLeft02DefaultX + (0.22f * OFFSET_SCALE)
                    this.LegLeft02.y = this.legLeft02DefaultY + (-0.03f * OFFSET_SCALE)
                    this.LegLeft02.z = this.legLeft02DefaultZ + (0.2f * OFFSET_SCALE)
                    this.LegRight01.zRot = 0.14f
                    this.LegRight02.xRot = 1.2217f
                    this.LegRight02.yRot = -1.2217f
                    this.LegRight02.zRot = 1.0472f
                    this.LegRight02.x = this.legRight02DefaultX + (-0.22f * OFFSET_SCALE)
                    this.LegRight02.y = this.legRight02DefaultY + (-0.03f * OFFSET_SCALE)
                    this.LegRight02.z = this.legRight02DefaultZ + (0.2f * OFFSET_SCALE)
                    this.EquipSL01.xRot -= 0.06f
                    this.EquipSL01.zRot -= 1.2f
                    this.EquipSR01.xRot -= 1.2f
                }
            } else {
                this.poseTranslateY += 0.56f * 3
                this.BodyMain.xRot = -0.45f
                this.Butt.xRot = -0.2f
                this.Skirt01.xRot = -0.13f
                this.Skirt01.y = this.skirt01DefaultY + (-0.05f * OFFSET_SCALE)
                this.Skirt02.xRot = -0.13f
                this.Skirt02.y = this.skirt02DefaultY + (-0.05f * OFFSET_SCALE)
                this.ArmLeft01.xRot = 0.2f
                this.ArmLeft01.zRot = -1.1f
                this.ArmRight01.xRot = 0.2f
                this.ArmRight01.zRot = 1.1f
                addk1 = -0.8f
                addk2 = -1.2f
                this.LegLeft01.zRot = -0.14f
                this.LegLeft02.xRot = 1.2217f
                this.LegLeft02.yRot = 1.2217f
                this.LegLeft02.zRot = -1.0472f
                this.LegLeft02.x = this.legLeft02DefaultX + (0.22f * OFFSET_SCALE)
                this.LegLeft02.y = this.legLeft02DefaultY + (-0.03f * OFFSET_SCALE)
                this.LegLeft02.z = this.legLeft02DefaultZ + (0.2f * OFFSET_SCALE)
                this.LegRight01.zRot = 0.14f
                this.LegRight02.xRot = 0.9f
                this.EquipSL01.xRot = -1.2f
                this.EquipSL01.yRot = 0.1f
                this.EquipSL01.zRot = 1.0f
                this.EquipSL01.x = this.equipSL01DefaultX + (0.24f * OFFSET_SCALE)
                this.EquipSL01.y = this.equipSL01DefaultY + (-0.5f * OFFSET_SCALE)
                this.EquipSL01.z = this.equipSL01DefaultZ + (1.0f * OFFSET_SCALE)
                this.EquipSR01.xRot = -1.2f
                this.EquipSR01.yRot = -0.1f
                this.EquipSR01.zRot = -1.0f
                this.EquipSR01.x = this.equipSR01DefaultX + (-0.24f * OFFSET_SCALE)
                this.EquipSR01.y = this.equipSR01DefaultY + (-0.5f * OFFSET_SCALE)
                this.EquipSR01.z = this.equipSR01DefaultZ + (1.0f * OFFSET_SCALE)
            }
        } else if (isSitting) {
            this.isSittingPose = true
            if (entity != null && entity.getStateEmotion(1) == 4) {
                this.poseTranslateY += 0.65f * 3
                this.Head.xRot = -1.2217f
                this.Head.yRot = 0.0f
                this.Head.zRot = 0.0f
                this.BodyMain.xRot = 1.2217f
                this.Hair02.xRot += 0.2f
                this.Hair03.xRot += 0.2f
                this.Hair05.xRot -= 0.6f
                this.Hair06.xRot -= 0.5f
                this.ArmLeft01.xRot = -2.0f
                this.ArmLeft01.yRot = -0.1f
                this.ArmLeft01.zRot = -0.1f
                this.ArmLeft02.xRot = -2.5f
                this.ArmLeft02.y = this.armLeft02DefaultY + (0.1f * OFFSET_SCALE)
                this.ArmLeft02.z = this.armLeft02DefaultZ + (-0.3f * OFFSET_SCALE)
                this.ArmRight01.xRot = -2.0f
                this.ArmRight01.yRot = 0.1f
                this.ArmRight01.zRot = 0.1f
                this.ArmRight02.xRot = -2.5f
                this.ArmRight02.y = this.armRight02DefaultY + (0.1f * OFFSET_SCALE)
                this.ArmRight02.z = this.armRight02DefaultZ + (-0.3f * OFFSET_SCALE)
                addk1 = 0.0f
                addk2 = 0.0f
                this.LegLeft02.xRot = angleX * 0.4f + 0.8f
                this.LegRight02.xRot = -angleX * 0.4f + 0.8f
                this.GlowBodyMain2.visible = false
            } else {
                this.poseTranslateY += 0.51f * 3
                this.Head.yRot = -0.4f
                this.Head.zRot = 0.2f
                this.BodyMain.xRot = -0.25f
                this.Butt.xRot = -0.2f
                this.Skirt01.xRot = -0.13f
                this.Skirt01.y = this.skirt01DefaultY + (-0.05f * OFFSET_SCALE)
                this.Skirt02.xRot = -0.13f
                this.Skirt02.y = this.skirt02DefaultY + (-0.05f * OFFSET_SCALE)
                this.ArmLeft01.xRot = 0.35f
                this.ArmLeft01.zRot = -0.2618f
                this.ArmRight01.xRot = -0.4f
                this.ArmRight01.zRot = 0.4f
                addk1 = -0.9f
                addk2 = -0.9f
                this.LegLeft01.zRot = -0.14f
                this.LegLeft02.xRot = 1.2217f
                this.LegLeft02.yRot = 1.2217f
                this.LegLeft02.zRot = -1.0472f
                this.LegLeft02.x = this.legLeft02DefaultX + (0.22f * OFFSET_SCALE)
                this.LegLeft02.y = this.legLeft02DefaultY + (-0.03f * OFFSET_SCALE)
                this.LegLeft02.z = this.legLeft02DefaultZ + (0.2f * OFFSET_SCALE)
                this.LegRight01.zRot = 0.14f
                this.LegRight02.xRot = 1.2217f
                this.LegRight02.yRot = -1.2217f
                this.LegRight02.zRot = 1.0472f
                this.LegRight02.x = this.legRight02DefaultX + (-0.22f * OFFSET_SCALE)
                this.LegRight02.y = this.legRight02DefaultY + (-0.03f * OFFSET_SCALE)
                this.LegRight02.z = this.legRight02DefaultZ + (0.2f * OFFSET_SCALE)
                this.EquipSL01.xRot -= 0.06f
                this.EquipSL01.zRot -= 1.2f
                this.EquipSR01.xRot -= 1.2f
            }
        }

        if (entity != null && entity.attackTick > 0) {
            val attackTick = entity.attackTick
            if (attackTick > 41) {
                var ft = (50 - attackTick) + (ageInTicks - ageInTicks.toInt())
                val fa = Mth.cos((0.125f.let { ft *= it; ft }) * ft * Math.PI.toFloat())
                val fb = Mth.cos(Mth.sqrt(ft) * Math.PI.toFloat())
                this.ArmLeft01.xRot += -fb * 120.0f * (Math.PI.toFloat() / 180f) - 1.5f
                this.ArmLeft01.yRot += fa * 20.0f * (Math.PI.toFloat() / 180f)
                this.ArmLeft01.zRot += fb * 20.0f * (Math.PI.toFloat() / 180f) + 0.26f
            }
            if (attackTick > 36 && attackTick < 45) {
                var ft = (45 - attackTick) + (ageInTicks - ageInTicks.toInt())
                val fa = Mth.cos((0.125f.let { ft *= it; ft }) * ft * Math.PI.toFloat())
                val fb = Mth.cos(Mth.sqrt(ft) * Math.PI.toFloat())
                this.ArmRight01.xRot += -fb * 120.0f * (Math.PI.toFloat() / 180f) - 1.5f
                this.ArmRight01.yRot += -fa * 20.0f * (Math.PI.toFloat() / 180f)
                this.ArmRight01.zRot += -fb * 20.0f * (Math.PI.toFloat() / 180f) - 0.26f
            }
        }

        val partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
        val customAttackAnim = if (entity != null) entity.getCustomAttackAnim(partialTick) else 0.0f
        if (customAttackAnim > 0.0f) {
            val f7 = Mth.sin(customAttackAnim * customAttackAnim * Math.PI.toFloat())
            val f8 = Mth.sin(Mth.sqrt(customAttackAnim) * Math.PI.toFloat())
            this.ArmRight01.xRot += -f8 * 95.0f * (Math.PI.toFloat() / 180f)
            this.ArmRight01.yRot += -f7 * 20.0f * (Math.PI.toFloat() / 180f) + 0.2f
            this.ArmRight01.zRot += -f8 * 20.0f * (Math.PI.toFloat() / 180f)
        }

        val headX = this.Head.xRot * -0.5f
        val headZ = this.Head.zRot * -0.5f
        this.Hair01.xRot += headX
        this.Hair01.zRot += headZ
        this.Hair02.xRot += headX * 0.5f
        this.Hair02.zRot += headZ * 0.5f
        this.Hair03.xRot += headX * 0.5f
        this.Hair03.zRot += headZ * 0.5f
        this.Hair05.xRot += headX
        this.Hair05.zRot += headZ
        this.Hair06.xRot += headX
        this.Hair06.zRot += headZ
        this.HairL01.zRot += headZ
        this.HairL02.zRot += headZ
        this.HairR01.zRot += headZ
        this.HairR02.zRot += headZ
        this.HairL01.xRot += headX
        this.HairL02.xRot += headX
        this.HairR01.xRot += headX
        this.HairR02.xRot += headX

        this.LegLeft01.xRot = addk1
        this.LegRight01.xRot = addk2
    }

    private fun syncGlowParts() {
        this.GlowBodyMain.copyFrom(this.BodyMain)
        this.GlowBodyMain2.copyFrom(this.BodyMain)
        this.GlowNeck.copyFrom(this.Neck)
        this.GlowHead.copyFrom(this.Head)
        this.GlowArmLeft01.copyFrom(this.ArmLeft01)
        this.GlowArmLeft02.copyFrom(this.ArmLeft02)
        this.GlowArmRight01.copyFrom(this.ArmRight01)
        this.GlowArmRight02.copyFrom(this.ArmRight02)
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

        this.GlowBodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        this.GlowBodyMain2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "carrier_hime"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelCarrierHime")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelCarrierHime")
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

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(52, 61).addBox(-7.5f, 0f, -5.7f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.3f, 0.3491f, 0f, 0f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 83).mirror().addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.8f, 5.5f, -2.6f, -0.1745f, 0f, -0.1047f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(103, 0).mirror().addBox(-3f, 0f, 0f, 6f, 3f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 14f, -3f)
            )

            val ShoesR01 = LegRight02.addOrReplaceChild(
                "ShoesR01",
                CubeListBuilder.create().texOffs(100, 0).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 4f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, 3f, 0.1396f, 0f, 0f)
            )

            val ShoesR02 = ShoesR01.addOrReplaceChild(
                "ShoesR02",
                CubeListBuilder.create().texOffs(90, 0).mirror().addBox(-4f, 0f, -4f, 8f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.5f, -0.7f, 0.2094f, 0f, 0f)
            )

            val ShoesR03 = ShoesR02.addOrReplaceChild(
                "ShoesR03",
                CubeListBuilder.create().texOffs(100, 3).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.4f, -0.7f, -0.1396f, 0f, 0f)
            )

            val ShoesR04 = ShoesR03.addOrReplaceChild(
                "ShoesR04",
                CubeListBuilder.create().texOffs(104, 13).mirror()
                    .addBox(-3f, 0f, -3f, 6f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.2f, -0.3f, -0.2094f, 0f, 0f)
            )

            val LegRight01a = LegRight01.addOrReplaceChild(
                "LegRight01a",
                CubeListBuilder.create().texOffs(95, 0).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8.6f, -0.2f, 0.2094f, 0f, 0f)
            )

            val LegRight01b = LegRight01.addOrReplaceChild(
                "LegRight01b",
                CubeListBuilder.create().texOffs(96, 2).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 11.6f, -0.1f, 0.2094f, 0f, 0f)
            )

            val Skirt01 = Butt.addOrReplaceChild(
                "Skirt01",
                CubeListBuilder.create().texOffs(46, 34).addBox(-8.5f, 0f, -6f, 17f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.9f, 0f, -0.1396f, 0f, 0f)
            )

            val Skirt02 = Skirt01.addOrReplaceChild(
                "Skirt02",
                CubeListBuilder.create().texOffs(42, 47).addBox(-9f, 0f, -6f, 18f, 4f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.8f, -0.5f, -0.0873f, 0f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 83).addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.8f, 5.5f, -2.6f, -0.3491f, 0f, 0.1047f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(92, 2).addBox(-3f, 0f, 0f, 6f, 3f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 14f, -3f)
            )

            val ShoesL01 = LegLeft02.addOrReplaceChild(
                "ShoesL01",
                CubeListBuilder.create().texOffs(97, 2).addBox(-3.5f, 0f, -3.5f, 7f, 4f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, 3f, 0.1396f, 0f, 0f)
            )

            val ShoesL02 = ShoesL01.addOrReplaceChild(
                "ShoesL02",
                CubeListBuilder.create().texOffs(90, 0).addBox(-4f, 0f, -4f, 8f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.5f, -0.7f, 0.2094f, 0f, 0f)
            )

            val ShoesL03 = ShoesL02.addOrReplaceChild(
                "ShoesL03",
                CubeListBuilder.create().texOffs(95, 0).addBox(-3.5f, 0f, -3.5f, 7f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.4f, -0.7f, -0.1396f, 0f, 0f)
            )

            val ShoesL04 = ShoesL03.addOrReplaceChild(
                "ShoesL04",
                CubeListBuilder.create().texOffs(104, 13).addBox(-3f, 0f, -3f, 6f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.2f, -0.3f, -0.2094f, 0f, 0f)
            )

            val LegLeft01a = LegLeft01.addOrReplaceChild(
                "LegLeft01a",
                CubeListBuilder.create().texOffs(92, 2).addBox(-3.5f, 0f, -3.5f, 7f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8.6f, -0.2f, 0.2094f, 0f, 0f)
            )

            val LegLeft01b = LegLeft01.addOrReplaceChild(
                "LegLeft01b",
                CubeListBuilder.create().texOffs(93, 3).addBox(-3.5f, 0f, -3.5f, 7f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 11.6f, -0.1f, 0.2094f, 0f, 0f)
            )

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(0, 22).addBox(-7f, 0f, -4f, 14f, 5f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.5f, -0.3f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(33, 101).mirror()
                    .addBox(-3.5f, 0f, 0f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.5f, -8.1f, -3.7f, -0.6981f, 0.0873f, 0.0873f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(4, 85).mirror()
                    .addBox(-2f, -1f, -2.5f, 5f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.3f, -0.7f, 0.3491f, 0f, -0.2618f)
            )

            val ArmLeft01b = ArmLeft01.addOrReplaceChild(
                "ArmLeft01b",
                CubeListBuilder.create().texOffs(90, 6).mirror().addBox(-3f, 0f, -3f, 6f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.5f, 9f, -0.1f, 0.2094f, 0f, 0f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(96, 2).mirror().addBox(-5f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 12f, 2.5f)
            )

            val ArmLeft01a = ArmLeft01.addOrReplaceChild(
                "ArmLeft01a",
                CubeListBuilder.create().texOffs(90, 9).mirror().addBox(-3f, 0f, -3f, 6f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.5f, 5.5f, -0.2f, 0.2094f, 0f, 0f)
            )

            val Cloth02 = BodyMain.addOrReplaceChild(
                "Cloth02",
                CubeListBuilder.create().texOffs(36, 93).addBox(-3.5f, 0f, 0f, 7f, 8f, 0f, CubeDeformation(0f)),
                PartPose.offset(0.3f, -4.5f, -6.5f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(100, 2).addBox(-3f, -2f, -3.5f, 6f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10.3f, 0.5f, 0.1047f, 0f, 0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, -0.7f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 77).addBox(-8f, -8f, -7.2f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(106, 31).addBox(0f, -6f, -10.5f, 0f, 11f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, -6f, 0.2094f, 0.6981f, 0f)
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

            val Hair04 = HairMain.addOrReplaceChild(
                "Hair04",
                CubeListBuilder.create().texOffs(108, 0).addBox(0f, -2f, -2f, 2f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.5f, 3.5f, 6f, 0f, -0.0873f, -0.0873f)
            )

            val Hair05 = Hair04.addOrReplaceChild(
                "Hair05",
                CubeListBuilder.create().texOffs(108, 28).addBox(0f, -2.5f, -2.5f, 5f, 9f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.5f, -1f, 0f, 0.1047f, -0.0873f, -0.1745f)
            )

            val Hair06 = Hair05.addOrReplaceChild(
                "Hair06",
                CubeListBuilder.create().texOffs(109, 28).addBox(-2f, 0f, -2.5f, 4f, 7f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.5f, 4f, 0f, 0.2094f, 0f, 0.1396f)
            )

            val Hair07 = Hair06.addOrReplaceChild(
                "Hair07",
                CubeListBuilder.create().texOffs(110, 29).addBox(-2f, 0f, -2f, 4f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, 0f, -0.2618f, 0f, 0.1396f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(0, 85).mirror()
                    .addBox(-3f, -1f, -2.5f, 5f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, -9.3f, -0.7f, 0f, 0f, 0.2618f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(93, 0).mirror().addBox(0f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 12f, 2.5f)
            )

            val ArmRight01a = ArmRight01.addOrReplaceChild(
                "ArmRight01a",
                CubeListBuilder.create().texOffs(92, 3).addBox(-3f, 0f, -3f, 6f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.5f, 5.5f, -0.2f, 0.2094f, 0f, 0f)
            )

            val ArmRight01b = ArmRight01.addOrReplaceChild(
                "ArmRight01b",
                CubeListBuilder.create().texOffs(92, 0).addBox(-3f, 0f, -3f, 6f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.5f, 9f, -0.1f, 0.2094f, 0f, 0f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(33, 101).addBox(-3.5f, 0f, 0f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.5f, -8.1f, -3.7f, -0.6981f, -0.0873f, -0.0873f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -10.3f, 0.5f, 0.1047f, 0f, 0f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -1f, -0.7f)
            )

            addFaceLayer(GlowHead)

            val GlowBodyMain2 = partdefinition.addOrReplaceChild(
                "GlowBodyMain2",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val GlowArmLeft01 = GlowBodyMain2.addOrReplaceChild(
                "GlowArmLeft01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(7.8f, -9.3f, -0.7f, 0.3491f, 0f, -0.2618f)
            )

            val GlowArmLeft02 = GlowArmLeft01.addOrReplaceChild(
                "GlowArmLeft02",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(3f, 12f, 2.5f)
            )

            val EquipSL01 = GlowArmLeft02.addOrReplaceChild(
                "EquipSL01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3f, 10.5f, -6f, -1.5708f, 0f, 1.5708f)
            )

            val EquipSL02 = EquipSL01.addOrReplaceChild(
                "EquipSL02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 0f)
            )

            val EquipSL03 = EquipSL02.addOrReplaceChild(
                "EquipSL03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 0f)
            )

            val EquipSL04 = EquipSL01.addOrReplaceChild(
                "EquipSL04",
                CubeListBuilder.create().texOffs(109, 0).addBox(-0.5f, -9f, -0.5f, 1f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipSL05 = EquipSL04.addOrReplaceChild(
                "EquipSL05",
                CubeListBuilder.create().texOffs(100, 0).addBox(-1f, -2f, -1f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -9f, 0f)
            )

            val GlowArmRight01 = GlowBodyMain2.addOrReplaceChild(
                "GlowArmRight01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(-7.8f, -9.3f, -0.7f, 0f, 0f, 0.2618f)
            )

            val GlowArmRight02 = GlowArmRight01.addOrReplaceChild(
                "GlowArmRight02",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(-3f, 12f, 2.5f)
            )

            val EquipSR01 = GlowArmRight02.addOrReplaceChild(
                "EquipSR01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3f, 10.5f, -6f, -1.5708f, 0f, 1.5708f)
            )

            val EquipSR02 = EquipSR01.addOrReplaceChild(
                "EquipSR02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 0f)
            )

            val EquipSR03 = EquipSR02.addOrReplaceChild(
                "EquipSR03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 0f)
            )

            val EquipSR04 = EquipSR01.addOrReplaceChild(
                "EquipSR04",
                CubeListBuilder.create().texOffs(107, 0).addBox(-0.5f, -9f, -0.5f, 1f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipSR05 = EquipSR04.addOrReplaceChild(
                "EquipSR05",
                CubeListBuilder.create().texOffs(100, 0).addBox(-1f, -2f, -1f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -9f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
