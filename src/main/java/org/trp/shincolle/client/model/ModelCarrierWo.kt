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
import org.trp.shincolle.entity.base.EntityShipBase

class ModelCarrierWo<T : EntityShipBase?>(root: ModelPart) : ShipModelHumanoidBase<T?>(), IGlowableModel {
    private var isDeadPose = false
    private var poseTranslateY = 0f
    private var isSittingPose = false

    private val BodyMain: ModelPart
    private val Butt: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmRight02: ModelPart
    private val Neck: ModelPart
    private val Neck02: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val CloakNeck: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft01: ModelPart
    private val LegRight02: ModelPart
    private val LegLeft02: ModelPart
    private val ShoesRight: ModelPart
    private val ShoesLeft: ModelPart
    private val Staff: ModelPart
    private val StaffHead: ModelPart
    private val Head: ModelPart
    private val Hair: ModelPart
    private val Ahoke: ModelPart
    private val HairL01: ModelPart
    private val Hair00a: ModelPart
    private val Hair00b: ModelPart
    private val HairR01: ModelPart
    private val HairL02: ModelPart
    private val HairR02: ModelPart
    private val EquipBase: ModelPart
    private val Equip01: ModelPart
    private val Equip02: ModelPart
    private val Equip03: ModelPart
    private val Equip04: ModelPart
    private val EquipEye01: ModelPart
    private val EquipEye02: ModelPart
    private val EquipT01L: ModelPart
    private val EquipT01R: ModelPart
    private val Equip05: ModelPart
    private val Equip06: ModelPart
    private val EquipLC01: ModelPart
    private val EquipRC01: ModelPart
    private val EquipTB01L: ModelPart
    private val EquipTB01R: ModelPart
    private val EquipTooth01: ModelPart
    private val EquipTooth02: ModelPart
    private val EquipTooth03: ModelPart
    private val EquipT02L: ModelPart
    private val EquipT03L: ModelPart
    private val EquipT02R: ModelPart
    private val EquipT03R: ModelPart
    private val EquipLC02: ModelPart
    private val EquipLC03: ModelPart
    private val EquipRC02: ModelPart
    private val EquipRC03: ModelPart
    private val EquipTB02L: ModelPart
    private val EquipTB03L: ModelPart
    private val EquipTB02R: ModelPart
    private val EquipTB03R: ModelPart
    private val Cloak01: ModelPart
    private val Cloak02: ModelPart
    private val Cloak03: ModelPart
    private val Neck03: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowHead: ModelPart
    private val GlowEquipBase: ModelPart
    private val buttDefaultY: Float
    private val buttDefaultZ: Float
    private val legLeft02DefaultZ: Float
    private val legRight02DefaultZ: Float
    private val staffDefaultX: Float
    private val staffDefaultY: Float
    private val staffDefaultZ: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Head = this.BodyMain.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.Hair00a = this.Hair.getChild("Hair00a")
        this.Hair00b = this.Hair.getChild("Hair00b")
        this.EquipBase = this.Head.getChild("EquipBase")
        this.Equip05 = this.EquipBase.getChild("Equip05")
        this.Equip03 = this.EquipBase.getChild("Equip03")
        this.EquipTooth03 = this.EquipBase.getChild("EquipTooth03")
        this.Equip04 = this.EquipBase.getChild("Equip04")
        this.EquipLC01 = this.EquipBase.getChild("EquipLC01")
        this.EquipLC02 = this.EquipLC01.getChild("EquipLC02")
        this.EquipLC03 = this.EquipLC01.getChild("EquipLC03")
        this.EquipTB01L = this.EquipBase.getChild("EquipTB01L")
        this.EquipTB02L = this.EquipTB01L.getChild("EquipTB02L")
        this.EquipTB03L = this.EquipTB02L.getChild("EquipTB03L")
        this.Equip06 = this.EquipBase.getChild("Equip06")
        this.Equip02 = this.EquipBase.getChild("Equip02")
        this.EquipT01R = this.EquipBase.getChild("EquipT01R")
        this.EquipT02R = this.EquipT01R.getChild("EquipT02R")
        this.EquipT03R = this.EquipT02R.getChild("EquipT03R")
        this.EquipTB01R = this.EquipBase.getChild("EquipTB01R")
        this.EquipTB02R = this.EquipTB01R.getChild("EquipTB02R")
        this.EquipTB03R = this.EquipTB02R.getChild("EquipTB03R")
        this.EquipRC01 = this.EquipBase.getChild("EquipRC01")
        this.EquipRC02 = this.EquipRC01.getChild("EquipRC02")
        this.EquipRC03 = this.EquipRC01.getChild("EquipRC03")
        this.EquipTooth02 = this.EquipBase.getChild("EquipTooth02")
        this.EquipT01L = this.EquipBase.getChild("EquipT01L")
        this.EquipT02L = this.EquipT01L.getChild("EquipT02L")
        this.EquipT03L = this.EquipT02L.getChild("EquipT03L")
        this.Equip01 = this.EquipBase.getChild("Equip01")
        this.EquipTooth01 = this.EquipBase.getChild("EquipTooth01")
        this.Neck03 = this.BodyMain.getChild("Neck03")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.CloakNeck = this.BodyMain.getChild("CloakNeck")
        this.Cloak01 = this.CloakNeck.getChild("Cloak01")
        this.Cloak02 = this.Cloak01.getChild("Cloak02")
        this.Cloak03 = this.Cloak02.getChild("Cloak03")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.Staff = this.ArmRight02.getChild("Staff")
        this.StaffHead = this.Staff.getChild("StaffHead")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Neck02 = this.Neck.getChild("Neck02")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.ShoesLeft = this.LegLeft02.getChild("ShoesLeft")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.ShoesRight = this.LegRight02.getChild("ShoesRight")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowHead = this.GlowBodyMain.getChild("GlowHead")
        initFaceParts(this.GlowHead)
        this.GlowEquipBase = this.GlowHead.getChild("GlowEquipBase")
        this.EquipEye01 = this.GlowEquipBase.getChild("EquipEye01")
        this.EquipEye02 = this.GlowEquipBase.getChild("EquipEye02")
        this.buttDefaultY = this.Butt.y
        this.buttDefaultZ = this.Butt.z
        this.legLeft02DefaultZ = this.LegLeft02.z
        this.legRight02DefaultZ = this.LegRight02.z
        this.staffDefaultX = this.Staff.x
        this.staffDefaultY = this.Staff.y
        this.staffDefaultZ = this.Staff.z
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
    }

    private fun resetOffsets() {
        this.Butt.y = this.buttDefaultY
        this.Butt.z = this.buttDefaultZ
        this.LegLeft02.z = this.legLeft02DefaultZ
        this.LegRight02.z = this.legRight02DefaultZ
        this.Staff.x = this.staffDefaultX
        this.Staff.y = this.staffDefaultY
        this.Staff.z = this.staffDefaultZ
    }

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) {
            this.EquipBase.visible = true
            this.GlowEquipBase.visible = true
            this.Staff.visible = true
            this.Neck.visible = true
            this.CloakNeck.visible = true
            return
        }

        this.EquipBase.visible = hasLegacyModelFlag(entity, 0)
        this.GlowEquipBase.visible = this.EquipBase.visible
        this.Staff.visible = hasLegacyModelFlag(entity, 1)
        this.Neck.visible = hasLegacyModelFlag(entity, 2)
        this.CloakNeck.visible = hasLegacyModelFlag(entity, 3)
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        this.Head.yRot = 0.0f
        this.Head.xRot = 0.0f
        this.BoobL.xRot = -0.63f
        this.BoobR.xRot = -0.63f
        this.Ahoke.yRot = 0.5236f
        this.ArmRight02.yRot = 0.0f
        this.Butt.y = this.buttDefaultY
        this.BodyMain.xRot = 0.2094f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = -0.4189f
        this.Butt.z = this.buttDefaultZ + (-0.12f * OFFSET_SCALE)
        this.ArmLeft01.xRot = -1.0472f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = 0.4189f
        this.ArmLeft02.xRot = -0.1396f
        this.ArmLeft02.yRot = 0.0f
        this.ArmLeft02.zRot = 1.2915f
        this.ArmRight01.xRot = -0.8727f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = -0.0873f
        this.ArmRight02.zRot = -1.1345f
        this.LegLeft01.xRot = -2.2689f
        this.LegLeft01.yRot = -0.2094f
        this.LegLeft01.zRot = -0.2094f
        this.LegLeft02.xRot = 1.7454f
        this.LegLeft02.z = this.legLeft02DefaultZ + (0.3f * OFFSET_SCALE)
        this.LegRight01.xRot = -2.2689f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = 0.0873f
        this.LegRight02.xRot = 1.5708f
        this.LegRight02.z = this.legRight02DefaultZ + (0.3f * OFFSET_SCALE)
        this.Cloak01.xRot = 0.2618f
        this.Cloak02.xRot = -1.3963f
        this.Cloak03.xRot = -0.9425f

        this.Staff.xRot = 1.309f
        this.Staff.yRot = -0.5934f
        this.Staff.zRot = -0.2094f
        this.Staff.x = this.staffDefaultX + (-0.3f * OFFSET_SCALE)
        this.Staff.y = this.staffDefaultY + (-1.5f * OFFSET_SCALE)
        this.Staff.z = this.staffDefaultZ + (-1.7f * OFFSET_SCALE)

        this.EquipLC01.xRot = this.Head.xRot
        this.EquipRC01.xRot = this.Head.xRot
        this.EquipT01L.xRot = -0.2618f
        this.EquipT01L.zRot = -0.2618f
        this.EquipT02L.xRot = -0.3491f
        this.EquipT02L.zRot = 0.2618f
        this.EquipT03L.xRot = 1.0472f
        this.EquipT03L.zRot = 1.0472f
        this.EquipT01R.xRot = -0.2618f
        this.EquipT01R.zRot = 0.2618f
        this.EquipT02R.xRot = -0.3491f
        this.EquipT02R.zRot = -0.2618f
        this.EquipT03R.xRot = 1.0472f
        this.EquipT03R.zRot = -1.0472f

        this.EquipTB01L.xRot = 0.1745f
        this.EquipTB01L.zRot = -0.3491f
        this.EquipTB02L.xRot = -0.6981f
        this.EquipTB02L.zRot = 0.3491f
        this.EquipTB03L.xRot = 0.1745f
        this.EquipTB03L.zRot = 0.2618f
        this.EquipTB01R.xRot = 0.1745f
        this.EquipTB01R.zRot = 0.3491f
        this.EquipTB02R.xRot = -0.6981f
        this.EquipTB02R.zRot = -0.3491f
        this.EquipTB03R.xRot = 0.1745f
        this.EquipTB03R.zRot = -0.2618f
    }

    private fun applyBasePose(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val angleZ = Mth.cos(ageInTicks * 0.08f)
        var addk1 = Mth.cos(limbSwing * 0.4f) * 0.5f * limbSwingAmount
        var addk2 = Mth.cos(limbSwing * 0.4f + Math.PI.toFloat()) * 0.5f * limbSwingAmount

        if (entity!!.shipDepth > 0.0) {
            this.poseTranslateY += angleZ * 0.05f + 0.025f
        }

        this.Head.xRot = headPitch * (Math.PI.toFloat() / 180f) * 0.6875f
        this.Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f) * 0.5729f

        this.BoobL.xRot = -angleZ * 0.06f - 0.63f
        this.BoobR.xRot = -angleZ * 0.06f - 0.63f
        this.Ahoke.yRot = angleZ * 0.25f + 0.5236f

        this.ArmLeft01.xRot = -0.3f
        this.ArmRight01.xRot = -0.3f
        this.ArmLeft01.yRot = 0.0f
        this.ArmRight01.yRot = 0.0f
        this.ArmLeft01.zRot = 0.24f
        this.ArmRight01.zRot = -0.24f
        this.ArmLeft02.xRot = 0.0f
        this.ArmLeft02.yRot = 0.0f
        this.ArmLeft02.zRot = 0.0f
        this.ArmRight02.yRot = 0.0f
        this.ArmRight02.zRot = 0.0f

        this.BodyMain.xRot = -0.1745f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f

        this.Butt.xRot = 0.5236f
        this.Butt.y = this.buttDefaultY
        this.Butt.z = this.buttDefaultZ

        this.HairL01.xRot = -0.3f
        this.HairL02.xRot = 0.35f
        this.HairR01.xRot = -0.3f
        this.HairR02.xRot = 0.35f
        this.HairL01.zRot = -0.314f
        this.HairL02.zRot = 0.2618f
        this.HairR01.zRot = 0.314f
        this.HairR02.zRot = -0.2618f

        addk1 += -0.349f
        addk2 += -0.349f

        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.052f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.z = this.legLeft02DefaultZ
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.052f
        this.LegRight02.xRot = 0.0f
        this.LegRight02.z = this.legRight02DefaultZ

        this.Cloak01.xRot = angleZ * 0.05f + 0.2618f
        this.Cloak02.xRot = angleZ * 0.1f + 0.1745f
        this.Cloak03.xRot = angleZ * 0.15f + 0.2618f

        this.Staff.xRot = 0.0f
        this.Staff.yRot = 0.0f
        this.Staff.zRot = 1.8326f
        this.Staff.x = this.staffDefaultX + (-0.7f * OFFSET_SCALE)
        this.Staff.y = this.staffDefaultY + (-1.7f * OFFSET_SCALE)
        this.Staff.z = this.staffDefaultZ + (-1.4f * OFFSET_SCALE)

        this.EquipLC01.xRot = this.Head.xRot
        this.EquipRC01.xRot = this.Head.xRot

        this.EquipT01L.xRot = angleZ * 0.05f - 0.2618f
        this.EquipT01L.zRot = angleZ * 0.05f - 0.2618f
        this.EquipT02L.xRot = angleZ * 0.1f
        this.EquipT02L.zRot = angleZ * 0.1f
        this.EquipT03L.xRot = angleZ * 0.25f
        this.EquipT03L.zRot = angleZ * 0.25f

        this.EquipT01R.xRot = angleZ * 0.05f - 0.2618f
        this.EquipT01R.zRot = -angleZ * 0.05f + 0.2618f
        this.EquipT02R.xRot = angleZ * 0.1f
        this.EquipT02R.zRot = -angleZ * 0.1f
        this.EquipT03R.xRot = angleZ * 0.25f
        this.EquipT03R.zRot = -angleZ * 0.25f

        this.EquipTB01L.xRot = -angleZ * 0.05f + 0.2618f
        this.EquipTB01L.zRot = angleZ * 0.05f - 0.2618f
        this.EquipTB02L.xRot = -angleZ * 0.1f
        this.EquipTB02L.zRot = angleZ * 0.1f
        this.EquipTB03L.xRot = -angleZ * 0.25f
        this.EquipTB03L.zRot = angleZ * 0.25f

        this.EquipTB01R.xRot = -angleZ * 0.05f + 0.2618f
        this.EquipTB01R.zRot = -angleZ * 0.05f + 0.2618f
        this.EquipTB02R.xRot = -angleZ * 0.1f
        this.EquipTB02R.zRot = -angleZ * 0.1f
        this.EquipTB03R.xRot = -angleZ * 0.25f
        this.EquipTB03R.zRot = -angleZ * 0.25f

        this.LegLeft01.xRot = addk1
        this.LegRight01.xRot = addk2
    }

    private fun applySpecialPoseAdjustments(entity: T?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        val angleZ = Mth.cos(ageInTicks * 0.08f)
        var addk1 = this.LegLeft01.xRot
        var addk2 = this.LegRight01.xRot

        val isSprinting = entity != null && entity.isSprinting
        val isCrouching = entity != null && entity.isCrouching()
        val isPassenger = entity != null && entity.isPassenger()
        val isSitting = entity != null && (entity.getIsSitting() || isPassenger)

        if (isSprinting || limbSwingAmount > 0.9f) {
            val angleZFast = Mth.cos(ageInTicks * 0.3f)
            this.ArmLeft01.xRot = -0.6981f
            this.ArmRight01.xRot = -0.6981f
            this.ArmLeft01.yRot = 0.4f
            this.ArmRight01.yRot = -0.4f
            this.ArmLeft01.zRot = 0.0f
            this.ArmRight01.zRot = 0.0f
            this.BodyMain.xRot = -0.349f
            addk1 = 0.0f
            addk2 = 0.0f
            this.LegLeft01.yRot = 0.0f
            this.LegRight01.yRot = 0.0f
            this.LegLeft01.zRot = 0.05236f
            this.LegRight01.zRot = -0.05236f

            this.Cloak01.xRot = angleZFast * 0.1f + 1.2f
            this.Cloak02.xRot = angleZFast * 0.25f
            this.Cloak03.xRot = angleZFast * 0.15f

            this.Staff.xRot = 1.3f
            this.Staff.yRot = -0.182f
            this.Staff.zRot = -1.2292f
            this.Staff.x = this.staffDefaultX + (0.2f * OFFSET_SCALE)
            this.Staff.y = this.staffDefaultY + (-1.0f * OFFSET_SCALE)
            this.Staff.z = this.staffDefaultZ + (-0.1f * OFFSET_SCALE)

            this.EquipT01L.xRot = angleZFast * 0.05f + 0.2618f
            this.EquipT01L.zRot = -0.2618f
            this.EquipT02L.xRot = angleZFast * 0.15f + 0.2618f
            this.EquipT02L.zRot = -0.2618f
            this.EquipT03L.xRot = angleZFast * 0.45f + 0.5236f
            this.EquipT03L.zRot = -0.2618f

            this.EquipT01R.xRot = angleZFast * 0.05f + 0.2618f
            this.EquipT01R.zRot = 0.2618f
            this.EquipT02R.xRot = angleZFast * 0.15f + 0.2618f
            this.EquipT02R.zRot = 0.2618f
            this.EquipT03R.xRot = angleZFast * 0.45f + 0.5236f
            this.EquipT03R.zRot = 0.2618f

            this.EquipTB01L.xRot = angleZFast * 0.05f + 0.349f
            this.EquipTB01L.zRot = -0.349f
            this.EquipTB02L.xRot = angleZFast * 0.15f + 0.5236f
            this.EquipTB02L.zRot = 0.1745f
            this.EquipTB03L.xRot = angleZFast * 0.45f + 0.5236f
            this.EquipTB03L.zRot = 0.1745f

            this.EquipTB01R.xRot = angleZFast * 0.05f + 0.349f
            this.EquipTB01R.zRot = 0.349f
            this.EquipTB02R.xRot = angleZFast * 0.15f + 0.5236f
            this.EquipTB02R.zRot = -0.1745f
            this.EquipTB03R.xRot = angleZFast * 0.45f + 0.5236f
            this.EquipTB03R.zRot = -0.1745f
        }

        if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            this.ArmLeft01.xRot = 0.7f
            this.ArmRight01.xRot = 0.7f
            this.BodyMain.xRot = 0.5f
            this.Head.xRot -= 0.5f
            this.Cloak01.xRot = angleZ * 0.02f + 0.34f
            addk1 -= 0.66f
            addk2 -= 0.66f
        } else {
            this.Head.xRot += 0.2f
        }

        if (isSitting) {
            this.isSittingPose = true
            if (hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY += 0.41f
                this.BodyMain.xRot = 0.2094f
                this.BodyMain.yRot = 0.0f
                this.BodyMain.zRot = 0.0f
                this.Butt.xRot = -0.4189f
                this.Butt.z = this.buttDefaultZ + (-0.12f * OFFSET_SCALE)
                this.Head.yRot *= 0.5f
                this.ArmLeft01.xRot = -1.0472f
                this.ArmLeft01.yRot = 0.0f
                this.ArmLeft01.zRot = 0.4189f
                this.ArmLeft02.xRot = -0.1396f
                this.ArmLeft02.yRot = 0.0f
                this.ArmLeft02.zRot = 1.2915f
                this.ArmRight01.xRot = -0.8727f
                this.ArmRight01.yRot = 0.0f
                this.ArmRight01.zRot = -0.0873f
                this.ArmRight02.zRot = -1.1345f
                addk1 = -2.2689f
                addk2 = -2.2689f
                this.LegLeft01.yRot = -0.2094f
                this.LegLeft01.zRot = -0.2094f
                this.LegLeft02.xRot = 1.7454f
                this.LegLeft02.z = this.legLeft02DefaultZ + (0.3f * OFFSET_SCALE)
                this.LegRight01.yRot = 0.0f
                this.LegRight01.zRot = 0.0873f
                this.LegRight02.xRot = 1.5708f
                this.LegRight02.z = this.legRight02DefaultZ + (0.3f * OFFSET_SCALE)
                this.Cloak01.xRot = 0.2618f
                this.Cloak02.xRot = -1.3963f
                this.Cloak03.xRot = -0.9425f
                this.Staff.xRot = 1.309f
                this.Staff.yRot = -0.5934f
                this.Staff.zRot = -0.2094f
                this.Staff.x = this.staffDefaultX + (-0.3f * OFFSET_SCALE)
                this.Staff.y = this.staffDefaultY + (-1.5f * OFFSET_SCALE)
                this.Staff.z = this.staffDefaultZ + (-1.7f * OFFSET_SCALE)
            } else {
                this.ArmLeft01.xRot = 0.4f
                this.ArmLeft01.yRot = 0.0f
                this.ArmLeft01.zRot = -0.32f
                this.ArmRight01.xRot = 0.34f
                this.ArmRight01.yRot = 0.0f
                this.ArmRight01.zRot = 0.5236f
                this.BodyMain.xRot = -0.349f
                this.BodyMain.yRot = -1.57f
                this.BodyMain.zRot = -0.0873f
                this.Head.xRot += -0.25f
                this.Head.yRot += 0.4f
                this.Head.zRot += 0.0f
                addk1 = angleZ * 0.3f - 1.0472f
                addk2 = -angleZ * 0.3f - 1.0472f
                this.LegLeft01.yRot = 0.0f
                this.LegRight01.yRot = 0.0f
                this.LegLeft01.zRot = 0.05236f
                this.LegRight01.zRot = -0.05236f

                this.Cloak01.xRot = angleZ * 0.1f + 0.4f
                this.Cloak02.xRot = angleZ * 0.15f
                this.Cloak03.xRot = angleZ * 0.15f

                this.Staff.xRot = 0.2f
                this.Staff.yRot = 0.0f
                this.Staff.zRot = -2.0f
                this.Staff.x = this.staffDefaultX + (1.1f * OFFSET_SCALE)
                this.Staff.y = this.staffDefaultY + (-1.95f * OFFSET_SCALE)
                this.Staff.z = this.staffDefaultZ + (-1.4f * OFFSET_SCALE)

                this.EquipT01L.xRot = -angleZ * 0.05f + 0.2618f
                this.EquipT01L.zRot = -0.2618f
                this.EquipT02L.xRot = -angleZ * 0.15f + 0.2618f
                this.EquipT02L.zRot = -0.1618f
                this.EquipT03L.xRot = -angleZ * 0.45f + 0.0f
                this.EquipT03L.zRot = -0.2618f

                this.EquipT01R.xRot = angleZ * 0.05f + 0.2618f
                this.EquipT01R.zRot = 0.2618f
                this.EquipT02R.xRot = angleZ * 0.15f + 0.2618f
                this.EquipT02R.zRot = 0.1618f
                this.EquipT03R.xRot = angleZ * 0.45f + 0.0f
                this.EquipT03R.zRot = 0.2618f

                this.EquipTB01L.xRot = angleZ * 0.05f + 0.349f
                this.EquipTB01L.zRot = -0.349f
                this.EquipTB02L.xRot = angleZ * 0.15f + 0.2236f
                this.EquipTB02L.zRot = 0.1745f
                this.EquipTB03L.xRot = angleZ * 0.45f + 0.1236f
                this.EquipTB03L.zRot = 0.1745f

                this.EquipTB01R.xRot = -angleZ * 0.05f + 0.349f
                this.EquipTB01R.zRot = 0.349f
                this.EquipTB02R.xRot = -angleZ * 0.15f + 0.2236f
                this.EquipTB02R.zRot = -0.1745f
                this.EquipTB03R.xRot = -angleZ * 0.45f + 0.1236f
                this.EquipTB03R.zRot = -0.1745f
            }
        }

        val headX = this.Head.xRot * -0.5f
        val headZ = this.Head.zRot * -0.5f
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

        if (entity != null && entity.attackTick > 0) {
            this.ArmLeft01.xRot = this.Head.xRot - 1.5f
            this.ArmRight01.zRot = 0.7f
            this.ArmRight01.xRot = 0.4f
            this.Staff.xRot = 1.5f
            this.Staff.yRot = 0.0f
            this.Staff.zRot = -1.2f
            this.Staff.x = this.staffDefaultX + (-0.2f * OFFSET_SCALE)
            this.Staff.y = this.staffDefaultY + (-1.2f * OFFSET_SCALE)
            this.Staff.z = this.staffDefaultZ + (-1.0f * OFFSET_SCALE)
        }

        val partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
        val customAttackAnim = if (entity != null) entity.getCustomAttackAnim(partialTick) else 0.0f
        if (customAttackAnim > 0.0f) {
            val f7 = Mth.sin(customAttackAnim * customAttackAnim * Math.PI.toFloat())
            val f8 = Mth.sin(Mth.sqrt(customAttackAnim) * Math.PI.toFloat())
            this.ArmRight01.xRot = -0.2f
            this.ArmRight01.yRot = 0.0f
            this.ArmRight01.zRot = -0.1f
            this.ArmRight01.xRot += -f8 * 80.0f * (Math.PI.toFloat() / 180f)
            this.ArmRight01.yRot += -f7 * 20.0f * (Math.PI.toFloat() / 180f) + 0.2f
            this.ArmRight01.zRot += -f8 * 20.0f * (Math.PI.toFloat() / 180f)
            this.ArmRight02.xRot = 0.0f
            this.ArmRight02.yRot = 0.0f
            this.ArmRight02.zRot = 0.0f
        }
    }

    private fun syncGlowParts() {
        this.GlowBodyMain.copyFrom(this.BodyMain)
        this.GlowHead.copyFrom(this.Head)
        this.GlowEquipBase.copyFrom(this.EquipBase)
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

        this.GlowBodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "carrier_wo"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelCarrierWo")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelCarrierWo")
        private const val MODEL_SCALE = 0.44f
        private const val MODEL_OFFSET_Y = 1.9f

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.5f, -12f, -4f, 13f, 17f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -11f, 0f, -0.3491f, 0f, 0f)
            )

            val Head = BodyMain.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(43, 101).addBox(-7f, -14f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -13f, -0.5f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(128, 61).addBox(-8f, -8f, -7.2f, 16f, 14f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, -7f, 0f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(31, 89).addBox(0f, -13.5f, -12f, 0f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -4.5f, 0f, 0.7f, 0f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(175, 61).mirror()
                    .addBox(-1f, 0f, -2f, 2f, 9f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, 0f, -2f, -0.5236f, 0.1745f, 0.3142f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(176, 74).addBox(-1f, 0f, -2.2f, 2f, 9f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8f, 0f, 0.3491f, 0f, -0.2618f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(175, 61).addBox(-1f, 0f, -2f, 2f, 9f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, 0f, -2f, -0.5236f, -0.1745f, -0.3142f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(176, 74).mirror()
                    .addBox(-1f, 0f, -2.2f, 2f, 9f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8f, 0f, 0.3491f, 0f, 0.2618f)
            )

            val Hair00a = Hair.addOrReplaceChild(
                "Hair00a",
                CubeListBuilder.create().texOffs(128, 82).addBox(-7.5f, -7.5f, -1f, 15f, 8f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -0.5f)
            )

            val Hair00b = Hair.addOrReplaceChild(
                "Hair00b",
                CubeListBuilder.create().texOffs(43, 21).addBox(-7.5f, 0f, 0f, 15f, 10f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.3f, -2.5f, 0.1745f, 0f, 0f)
            )

            val EquipBase = Head.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10f, -3f, 0.0873f, 0f, 0f)
            )

            val Equip05 = EquipBase.addOrReplaceChild(
                "Equip05",
                CubeListBuilder.create().texOffs(104, 4).addBox(-24f, -18f, -15f, 48f, 18f, 28f, CubeDeformation(0f)),
                PartPose.offset(0f, -5f, 2.5f)
            )

            val Equip03 = EquipBase.addOrReplaceChild(
                "Equip03",
                CubeListBuilder.create().texOffs(112, 0).addBox(-16f, -18f, -20f, 32f, 18f, 40f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -5.5f, 4f, 0.0698f, 0f, 0f)
            )

            val EquipTooth03 = EquipBase.addOrReplaceChild(
                "EquipTooth03",
                CubeListBuilder.create().texOffs(128, 99).mirror()
                    .addBox(-14f, 0f, 0f, 14f, 8f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-12.4f, -17f, -20.3f, 0.0698f, 0.5236f, -0.0524f)
            )

            val Equip04 = EquipBase.addOrReplaceChild(
                "Equip04",
                CubeListBuilder.create().texOffs(112, 0).addBox(-12f, -15f, -24f, 24f, 15f, 46f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 5.5f, 0.1047f, 0f, 0f)
            )

            val EquipLC01 = EquipBase.addOrReplaceChild(
                "EquipLC01",
                CubeListBuilder.create().texOffs(128, 0).addBox(-3.5f, -5.5f, -7.5f, 7f, 11f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(30f, -7f, 4f, -0.1745f, -0.2618f, 0.1745f)
            )

            val EquipLC02 = EquipLC01.addOrReplaceChild(
                "EquipLC02",
                CubeListBuilder.create().texOffs(128, 0).addBox(-1.5f, -1.5f, -17f, 3f, 3f, 17f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, -2f, -7f, -0.1047f, 0f, 0f)
            )

            val EquipLC03 = EquipLC01.addOrReplaceChild(
                "EquipLC03",
                CubeListBuilder.create().texOffs(128, 0).addBox(-1.5f, -1.5f, -16f, 3f, 3f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, -7f, 0.1047f, 0f, 0f)
            )

            val EquipTB01L = EquipBase.addOrReplaceChild(
                "EquipTB01L",
                CubeListBuilder.create().texOffs(128, 0).addBox(-3f, -2f, -3f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(15f, -6f, 10f, 0.1745f, 0f, -0.3491f)
            )

            val EquipTB02L = EquipTB01L.addOrReplaceChild(
                "EquipTB02L",
                CubeListBuilder.create().texOffs(21, 56).addBox(-2.5f, -2f, -2.5f, 5f, 16f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 0f, 0.4363f, 0f, -0.3491f)
            )

            val EquipTB03L = EquipTB02L.addOrReplaceChild(
                "EquipTB03L",
                CubeListBuilder.create().texOffs(21, 56).addBox(-2f, -2f, -2f, 4f, 15f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 13.5f, 0f, 0.6981f, 0f, 0.7854f)
            )

            val Equip06 = EquipBase.addOrReplaceChild(
                "Equip06",
                CubeListBuilder.create().texOffs(96, 0).addBox(-29f, -13f, -13f, 58f, 13f, 22f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 4.5f, 0.0698f, 0f, 0f)
            )

            val Equip02 = EquipBase.addOrReplaceChild(
                "Equip02",
                CubeListBuilder.create().texOffs(120, 0).addBox(-18f, -22f, -15f, 36f, 22f, 32f, CubeDeformation(0f)),
                PartPose.offset(0f, -3f, 2f)
            )

            val EquipT01R = EquipBase.addOrReplaceChild(
                "EquipT01R",
                CubeListBuilder.create().texOffs(128, 0).addBox(-4f, 0f, -4f, 8f, 10f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-17f, -7f, -8f, -0.2618f, 0f, 0.2618f)
            )

            val EquipT02R = EquipT01R.addOrReplaceChild(
                "EquipT02R",
                CubeListBuilder.create().texOffs(21, 56).addBox(-3f, -2f, -3f, 6f, 22f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8f, 0f, -0.1745f, 0f, 0.2618f)
            )

            val EquipT03R = EquipT02R.addOrReplaceChild(
                "EquipT03R",
                CubeListBuilder.create().texOffs(21, 56).addBox(-2.5f, -2f, -2.5f, 5f, 20f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 20f, 0f, 1.0472f, 0f, -0.7854f)
            )

            val EquipTB01R = EquipBase.addOrReplaceChild(
                "EquipTB01R",
                CubeListBuilder.create().texOffs(128, 0).addBox(-3f, -2f, -3f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-15f, -6f, 10f, 0.1745f, 0f, 0.3491f)
            )

            val EquipTB02R = EquipTB01R.addOrReplaceChild(
                "EquipTB02R",
                CubeListBuilder.create().texOffs(21, 56).addBox(-2.5f, -2f, -2.5f, 5f, 16f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 0f, 0.4363f, 0f, 0.3491f)
            )

            val EquipTB03R = EquipTB02R.addOrReplaceChild(
                "EquipTB03R",
                CubeListBuilder.create().texOffs(21, 56).addBox(-2f, -2f, -2f, 4f, 15f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 13.5f, 0f, 0.6981f, 0f, -0.7854f)
            )

            val EquipRC01 = EquipBase.addOrReplaceChild(
                "EquipRC01",
                CubeListBuilder.create().texOffs(128, 0).addBox(-3.5f, -5.5f, -7.5f, 7f, 11f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-30f, -7f, 4f, -0.1745f, 0.2618f, -0.1745f)
            )

            val EquipRC02 = EquipRC01.addOrReplaceChild(
                "EquipRC02",
                CubeListBuilder.create().texOffs(128, 0).addBox(-1.5f, -1.5f, -17f, 3f, 3f, 17f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1f, -2f, -7f, -0.1047f, 0f, 0f)
            )

            val EquipRC03 = EquipRC01.addOrReplaceChild(
                "EquipRC03",
                CubeListBuilder.create().texOffs(128, 0).addBox(-1.5f, -1.5f, -16f, 3f, 3f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, -7f, 0.1047f, 0f, 0f)
            )

            val EquipTooth02 = EquipBase.addOrReplaceChild(
                "EquipTooth02",
                CubeListBuilder.create().texOffs(128, 99).addBox(0f, 0f, 0f, 14f, 8f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(12.4f, -17f, -20.3f, 0.1047f, -0.5236f, 0.0524f)
            )

            val EquipT01L = EquipBase.addOrReplaceChild(
                "EquipT01L",
                CubeListBuilder.create().texOffs(128, 0).addBox(-4f, 0f, -4f, 8f, 10f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(17f, -7f, -8f, -0.2618f, 0f, -0.2618f)
            )

            val EquipT02L = EquipT01L.addOrReplaceChild(
                "EquipT02L",
                CubeListBuilder.create().texOffs(21, 56).addBox(-3f, -2f, -3f, 6f, 22f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8f, 0f, -0.1745f, 0f, -0.2618f)
            )

            val EquipT03L = EquipT02L.addOrReplaceChild(
                "EquipT03L",
                CubeListBuilder.create().texOffs(21, 56).addBox(-2.5f, -2f, -2.5f, 5f, 20f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 20f, 0f, 1.0472f, 0f, 0.7854f)
            )

            val Equip01 = EquipBase.addOrReplaceChild(
                "Equip01",
                CubeListBuilder.create().texOffs(128, 0).addBox(-9f, -28.5f, -7f, 18f, 27f, 22f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0.0873f, 0f, 0f)
            )

            val EquipTooth01 = EquipBase.addOrReplaceChild(
                "EquipTooth01",
                CubeListBuilder.create().texOffs(128, 112).addBox(-12f, 0f, 0f, 24f, 15f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -19.3f, -20.6f, 0.1047f, 0f, 0f)
            )

            val Neck03 = BodyMain.addOrReplaceChild(
                "Neck03",
                CubeListBuilder.create().texOffs(8, 0).addBox(-2.5f, -2f, -2.5f, 5f, 2f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.9f, -0f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(3, 27).mirror()
                    .addBox(-3.5f, 0f, -1f, 7f, 5f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.5f, -9f, -3.2f, -0.7854f, 0.0873f, 0.14f)
            )

            val CloakNeck = BodyMain.addOrReplaceChild(
                "CloakNeck",
                CubeListBuilder.create().texOffs(192, 61).addBox(-10f, 0f, -6f, 20f, 7f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -12f, -1.5f, 0.3142f, 0f, 0f)
            )

            val Cloak01 = CloakNeck.addOrReplaceChild(
                "Cloak01",
                CubeListBuilder.create().texOffs(216, 85).addBox(-10f, 0f, 0f, 20f, 12f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6.5f, 6f, 0.5f, 0f, 0f)
            )

            val Cloak02 = Cloak01.addOrReplaceChild(
                "Cloak02",
                CubeListBuilder.create().texOffs(208, 97).addBox(-12f, 0f, 0f, 24f, 16f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12f, 0f, -0.4554f, 0f, 0f)
            )

            val Cloak03 = Cloak02.addOrReplaceChild(
                "Cloak03",
                CubeListBuilder.create().texOffs(196, 113).addBox(-15f, 0f, 0f, 30f, 15f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 16f, 0f, 0.3491f, 0f, 0f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(0, 54).addBox(-5f, -1f, -2f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(-4.7f, -9f, 0f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(0, 71).addBox(0f, 0f, -4f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(-5f, 11f, 2f)
            )

            val Staff = ArmRight02.addOrReplaceChild(
                "Staff",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, -15f, 0f, 3f, 28f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, 35f, 21f, 1.1839f, -0.182f, -1.2292f)
            )

            val StaffHead = Staff.addOrReplaceChild(
                "StaffHead",
                CubeListBuilder.create().texOffs(38, 80).addBox(0f, -13f, 0f, 4f, 13f, 8f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, -15f, -1f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(46, 41).addBox(-7.5f, -1.5f, -7f, 15f, 4f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -13f, -2f, 0.4189f, 0f, 0f)
            )

            val Neck02 = Neck.addOrReplaceChild(
                "Neck02",
                CubeListBuilder.create().texOffs(128, 0).addBox(-1.5f, 0f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, -5f, -0.52f, 0f, 0f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(0, 54).mirror().addBox(0f, -1f, -2f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(4.7f, -9f, 0f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(0, 71).mirror().addBox(-5f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(5f, 11f, 3f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(3, 27).addBox(-3.5f, 0f, -1f, 7f, 5f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.5f, -9f, -3.2f, -0.7854f, -0.0873f, -0.14f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 38).addBox(-7.5f, -2f, -4.1f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4.7f, 0.5f, 0.5236f, 0f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 88).mirror().addBox(-3f, 0f, -3f, 6f, 12f, 6f, CubeDeformation(0f)),
                PartPose.offset(4.2f, 5f, -1f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(1, 110).mirror().addBox(-3f, 0f, 0f, 6f, 7f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 12f, -3f)
            )

            val ShoesLeft = LegLeft02.addOrReplaceChild(
                "ShoesLeft",
                CubeListBuilder.create().texOffs(0, 109).addBox(-3.5f, 4.5f, -0.5f, 7f, 9f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 88).addBox(-3f, 0f, -3f, 6f, 12f, 6f, CubeDeformation(0f)),
                PartPose.offset(-4.2f, 5f, -1f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(1, 110).addBox(-3f, 0f, 0f, 6f, 7f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 12f, -3f)
            )

            val ShoesRight = LegRight02.addOrReplaceChild(
                "ShoesRight",
                CubeListBuilder.create().texOffs(0, 109).addBox(-3.5f, 4.5f, -0.5f, 7f, 9f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -11f, 0f, -0.3491f, 0f, 0f)
            )

            val GlowHead = GlowBodyMain.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -13f, -0.5f)
            )

            val GlowEquipBase = GlowHead.addOrReplaceChild(
                "GlowEquipBase",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -10f, -3f, 0.0873f, 0f, 0f)
            )

            val EquipEye01 = GlowEquipBase.addOrReplaceChild(
                "EquipEye01",
                CubeListBuilder.create().texOffs(44, 0).addBox(-7.5f, -6f, 0f, 15f, 6f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-14.5f, -21f, -8f, 0.1396f, 0.1396f, -0.2618f)
            )

            val EquipEye02 = GlowEquipBase.addOrReplaceChild(
                "EquipEye02",
                CubeListBuilder.create().texOffs(44, 0).addBox(-7.5f, -6f, 0f, 15f, 6f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(14.5f, -21f, -8f, 0.1396f, -0.1396f, 0.2618f)
            )

            addFaceLayerWo(GlowHead)

            return LayerDefinition.create(meshdefinition, 256, 128)
        }
    }
}
