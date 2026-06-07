@file:Suppress("SENSELESS_COMPARISON")
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
import org.trp.shincolle.client.model.LegacyPoseOffsets.sittingY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sneakY
import org.trp.shincolle.entity.EntityDestroyerHime
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import kotlin.math.cos

class ModelDestroyerHime<T : EntityShipBase>(root: ModelPart) : ShipModelHumanoidBase<T>(), IGlowableModel {
    private var isDeadPose = false
    private var isSittingPose = false
    override var poseTranslateY = 0f

    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val Butt: ModelPart
    private val ArmRight01: ModelPart
    private val ArmLeft01: ModelPart
    private val Cloth01: ModelPart
    private val Head: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val Hair02: ModelPart
    private val Hat01: ModelPart
    private val HairU01: ModelPart
    private val Ahoke: ModelPart
    private val HairL01: ModelPart
    private val HairR01: ModelPart
    private val Hair01: ModelPart
    private val Hair03: ModelPart
    private val Hair04: ModelPart
    private val Hair05: ModelPart
    private val Hair06: ModelPart
    private val Hat02a: ModelPart
    private val Hat03: ModelPart
    private val Hat04a: ModelPart
    private val Hat05a: ModelPart
    private val Hat06a: ModelPart
    private val Hat06b: ModelPart
    private val Hat02b: ModelPart
    private val Hat04b: ModelPart
    private val Hat04c: ModelPart
    private val Hat05b: ModelPart
    private val LegLeft01: ModelPart
    private val LegRight01: ModelPart
    private val EquipLegL: ModelPart
    private val EquipLegR: ModelPart
    private val EquipBaseL: ModelPart
    private val EquipBaseR: ModelPart
    private val BeltBase: ModelPart
    private val LegLeft02: ModelPart
    private val LegRight02: ModelPart
    private val EquipLHead: ModelPart
    private val EquipLJaw: ModelPart
    private val EquipLB: ModelPart
    private val EquipLT01: ModelPart
    private val EquipLTU: ModelPart
    private val EquipHeadC01: ModelPart
    private val EquipHeadC02: ModelPart
    private val EquipLTD: ModelPart
    private val EquipLT02a: ModelPart
    private val EquipLT02b: ModelPart
    private val EquipLT02c: ModelPart
    private val EquipLT02d: ModelPart
    private val EquipRHead: ModelPart
    private val EquipLJaw_1: ModelPart
    private val EquipLB_1: ModelPart
    private val EquipLT01_1: ModelPart
    private val EquipLTU_1: ModelPart
    private val EquipHeadC01_1: ModelPart
    private val EquipHeadC02_1: ModelPart
    private val EquipLTD_1: ModelPart
    private val EquipLT02a_1: ModelPart
    private val EquipLT02b_1: ModelPart
    private val EquipLT02c_1: ModelPart
    private val EquipLT02d_1: ModelPart
    private val Belt01: ModelPart
    private val Belt02: ModelPart
    private val Belt03: ModelPart
    private val Belt04: ModelPart
    private val Belt05: ModelPart
    private val Belt06: ModelPart
    private val Belt07: ModelPart
    private val ArmRight02: ModelPart
    private val ArmRight02a: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmLeft02a: ModelPart
    private val Cannon01: ModelPart
    private val Cannon02: ModelPart
    private val Cannon03: ModelPart
    private val Cannon04: ModelPart
    private val Cannon05: ModelPart
    private val Cloth02: ModelPart
    private val Skirt01: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val buttDefaultY: Float
    private val skirt01DefaultY: Float
    private val armLeft02DefaultX: Float
    private val armLeft02DefaultZ: Float
    private val armRight02DefaultX: Float
    private val armRight02DefaultZ: Float
    private val legLeft01DefaultY: Float
    private val legLeft02DefaultX: Float
    private val legLeft02DefaultY: Float
    private val legLeft02DefaultZ: Float
    private val legRight01DefaultY: Float
    private val legRight02DefaultX: Float
    private val legRight02DefaultY: Float
    private val legRight02DefaultZ: Float
    private val equipBaseLDefaultY: Float
    private val equipBaseRDefaultY: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.ArmRight02a = this.ArmRight02.getChild("ArmRight02a")
        this.Butt = this.BodyMain.getChild("Butt")
        this.EquipLegL = this.Butt.getChild("EquipLegL")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.EquipLegR = this.Butt.getChild("EquipLegR")
        this.BeltBase = this.Butt.getChild("BeltBase")
        this.Belt05 = this.BeltBase.getChild("Belt05")
        this.Belt01 = this.BeltBase.getChild("Belt01")
        this.Belt02 = this.BeltBase.getChild("Belt02")
        this.Belt06 = this.BeltBase.getChild("Belt06")
        this.Belt03 = this.BeltBase.getChild("Belt03")
        this.Belt07 = this.BeltBase.getChild("Belt07")
        this.Belt04 = this.BeltBase.getChild("Belt04")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.EquipBaseL = this.Butt.getChild("EquipBaseL")
        this.EquipLHead = this.EquipBaseL.getChild("EquipLHead")
        this.EquipLTU = this.EquipLHead.getChild("EquipLTU")
        this.EquipHeadC01 = this.EquipLHead.getChild("EquipHeadC01")
        this.EquipHeadC02 = this.EquipHeadC01.getChild("EquipHeadC02")
        this.EquipLT01 = this.EquipBaseL.getChild("EquipLT01")
        this.EquipLT02b = this.EquipLT01.getChild("EquipLT02b")
        this.EquipLT02d = this.EquipLT01.getChild("EquipLT02d")
        this.EquipLT02c = this.EquipLT01.getChild("EquipLT02c")
        this.EquipLT02a = this.EquipLT01.getChild("EquipLT02a")
        this.EquipLJaw = this.EquipBaseL.getChild("EquipLJaw")
        this.EquipLTD = this.EquipLJaw.getChild("EquipLTD")
        this.EquipLB = this.EquipBaseL.getChild("EquipLB")
        this.EquipBaseR = this.Butt.getChild("EquipBaseR")
        this.EquipRHead = this.EquipBaseR.getChild("EquipRHead")
        this.EquipLTU_1 = this.EquipRHead.getChild("EquipLTU_1")
        this.EquipHeadC01_1 = this.EquipRHead.getChild("EquipHeadC01_1")
        this.EquipHeadC02_1 = this.EquipHeadC01_1.getChild("EquipHeadC02_1")
        this.EquipLT01_1 = this.EquipBaseR.getChild("EquipLT01_1")
        this.EquipLT02a_1 = this.EquipLT01_1.getChild("EquipLT02a_1")
        this.EquipLT02b_1 = this.EquipLT01_1.getChild("EquipLT02b_1")
        this.EquipLT02d_1 = this.EquipLT01_1.getChild("EquipLT02d_1")
        this.EquipLT02c_1 = this.EquipLT01_1.getChild("EquipLT02c_1")
        this.EquipLJaw_1 = this.EquipBaseR.getChild("EquipLJaw_1")
        this.EquipLTD_1 = this.EquipLJaw_1.getChild("EquipLTD_1")
        this.EquipLB_1 = this.EquipBaseR.getChild("EquipLB_1")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.Cloth02 = this.Cloth01.getChild("Cloth02")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.Hair02 = this.Head.getChild("Hair02")
        this.Hair03 = this.Hair02.getChild("Hair03")
        this.Hair04 = this.Hair03.getChild("Hair04")
        this.Hair05 = this.Hair04.getChild("Hair05")
        this.Hair06 = this.Hair05.getChild("Hair06")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hat01 = this.Head.getChild("Hat01")
        this.Hat04a = this.Hat01.getChild("Hat04a")
        this.Hat04b = this.Hat04a.getChild("Hat04b")
        this.Hat04c = this.Hat04b.getChild("Hat04c")
        this.Hat05a = this.Hat01.getChild("Hat05a")
        this.Hat05b = this.Hat05a.getChild("Hat05b")
        this.Hat03 = this.Hat01.getChild("Hat03")
        this.Hat02b = this.Hat01.getChild("Hat02b")
        this.Hat06b = this.Hat01.getChild("Hat06b")
        this.Hat02a = this.Hat01.getChild("Hat02a")
        this.Hat06a = this.Hat01.getChild("Hat06a")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.ArmLeft02a = this.ArmLeft02.getChild("ArmLeft02a")
        this.Cannon01 = this.ArmLeft02.getChild("Cannon01")
        this.Cannon03 = this.Cannon01.getChild("Cannon03")
        this.Cannon04 = this.Cannon01.getChild("Cannon04")
        this.Cannon02 = this.Cannon01.getChild("Cannon02")
        this.Cannon05 = this.Cannon01.getChild("Cannon05")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        this.initFaceParts(this.GlowHead)
        this.buttDefaultY = this.Butt.y
        this.skirt01DefaultY = this.Skirt01.y
        this.armLeft02DefaultX = this.ArmLeft02.x
        this.armLeft02DefaultZ = this.ArmLeft02.z
        this.armRight02DefaultX = this.ArmRight02.x
        this.armRight02DefaultZ = this.ArmRight02.z
        this.legLeft01DefaultY = this.LegLeft01.y
        this.legLeft02DefaultX = this.LegLeft02.x
        this.legLeft02DefaultY = this.LegLeft02.y
        this.legLeft02DefaultZ = this.LegLeft02.z
        this.legRight01DefaultY = this.LegRight01.y
        this.legRight02DefaultX = this.LegRight02.x
        this.legRight02DefaultY = this.LegRight02.y
        this.legRight02DefaultZ = this.LegRight02.z
        this.equipBaseLDefaultY = this.EquipBaseL.y
        this.equipBaseRDefaultY = this.EquipBaseR.y
    }

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        this.resetOffsets()
        this.applyEquipVisibility(entity)
        this.applyFaceAndMouth(entity)

        if (entity is EntityShipBase && entity.isInDeadPose) {
            this.applyDeadPose()
            this.syncGlowParts()
            return
        }

        this.applyBasePose(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch)
        this.applySpecialPoseAdjustments(entity, limbSwing, limbSwingAmount, ageInTicks)

        this.syncGlowParts()
    }

    private fun resetOffsets() {
        this.isDeadPose = false
        this.isSittingPose = false
        this.poseTranslateY = 0.0f

        this.Butt.y = this.buttDefaultY
        this.Skirt01.y = this.skirt01DefaultY
        this.ArmLeft02.x = this.armLeft02DefaultX
        this.ArmLeft02.z = this.armLeft02DefaultZ
        this.ArmRight02.x = this.armRight02DefaultX
        this.ArmRight02.z = this.armRight02DefaultZ
        this.LegLeft01.y = this.legLeft01DefaultY
        this.LegLeft02.x = this.legLeft02DefaultX
        this.LegLeft02.y = this.legLeft02DefaultY
        this.LegLeft02.z = this.legLeft02DefaultZ
        this.LegRight01.y = this.legRight01DefaultY
        this.LegRight02.x = this.legRight02DefaultX
        this.LegRight02.y = this.legRight02DefaultY
        this.LegRight02.z = this.legRight02DefaultZ
        this.EquipBaseL.y = this.equipBaseLDefaultY
        this.EquipBaseR.y = this.equipBaseRDefaultY
    }

    private fun applyEquipVisibility(entity: EntityShipBase?) {
        if (entity == null) {
            return
        }
        val showRigging = entity.getEquipFlag(EntityDestroyerHime.EQUIP_RIGGING)
        this.EquipBaseL.visible = showRigging
        this.EquipBaseR.visible = showRigging
        this.Hat01.visible = entity.getEquipFlag(EntityDestroyerHime.EQUIP_HAT)
        this.Cannon01.visible = entity.getEquipFlag(EntityDestroyerHime.EQUIP_CANNON)
        this.BeltBase.visible = entity.getEquipFlag(EntityDestroyerHime.EQUIP_BELT)
        val showLeg = entity.getEquipFlag(EntityDestroyerHime.EQUIP_LEG)
        this.LegLeft01.visible = showLeg
        this.LegRight01.visible = showLeg
        val showHand = entity.getEquipFlag(EntityDestroyerHime.EQUIP_HAND)
        this.ArmLeft02a.visible = showHand
        this.ArmRight02a.visible = showHand
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        this.Head.xRot = 0.0f
        this.Head.yRot = 0.0f
        this.Head.zRot = 0.0f
        this.Ahoke.yRot = 0.7f
        this.BodyMain.xRot = 1.45f
        this.Butt.xRot = 0.21f
        this.BeltBase.xRot = 0.09f
        this.Skirt01.xRot = -0.21f

        this.Hair03.xRot = 0.0f
        this.Hair04.xRot = 0.0f
        this.Hair05.xRot = 0.0f
        this.Hair06.xRot = 0.0f
        this.Hair03.zRot = 0.1f
        this.Hair04.zRot = 0.2f
        this.Hair05.zRot = 0.3f
        this.Hair06.zRot = 0.4f

        this.ArmLeft01.xRot = -2.8f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = 0.7f
        this.ArmLeft02.xRot = 0.0f
        this.ArmLeft02.yRot = 0.0f
        this.ArmLeft02.zRot = 1.0f

        this.ArmRight01.xRot = -2.8f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = -0.7f
        this.ArmRight02.xRot = 0.0f
        this.ArmRight02.yRot = 0.0f
        this.ArmRight02.zRot = -1.0f

        this.LegLeft01.xRot = 0.1f
        this.LegLeft01.yRot = 3.1415f
        this.LegLeft01.zRot = -0.1f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.yRot = 0.0f
        this.LegLeft02.zRot = 0.0f

        this.LegRight01.xRot = 0.1f
        this.LegRight01.yRot = 3.1415f
        this.LegRight01.zRot = 0.1f
        this.LegRight02.xRot = 0.0f
        this.LegRight02.yRot = 0.0f
        this.LegRight02.zRot = 0.0f
    }

    private fun applyBasePose(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val angleX = cos((ageInTicks * 0.08f + limbSwing * 0.25f).toDouble()).toFloat()
        val angleX1 = cos((ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f).toDouble()).toFloat()
        val angleX2 = cos((ageInTicks * 0.08f + 0.6f + limbSwing * 0.5f).toDouble()).toFloat()
        val angleX3 = cos((ageInTicks * 0.08f + 0.9f + limbSwing * 0.5f).toDouble()).toFloat()
        val angleAdd1 = cos((limbSwing * 0.7f).toDouble()).toFloat() * limbSwingAmount
        val angleAdd2 = cos(limbSwing * 0.7f + Math.PI).toFloat() * limbSwingAmount

        if (entity!!.isInWater()) {
            this.poseTranslateY += angleX * 0.05f + 0.025f
        } else {
            this.poseTranslateY += angleX * 0.015f + 0.025f
        }

        val addk1 = angleAdd1 * 0.5f - 0.28f
        val addk2 = angleAdd2 * 0.5f - 0.21f
        val headX = headPitch * (Math.PI.toFloat() / 180f) * -0.5f

        this.Head.xRot = headPitch * (Math.PI.toFloat() / 180f)
        this.Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)
        this.Head.zRot = 0.0f
        this.Ahoke.yRot = angleX * 0.05f + 0.7f
        this.BodyMain.xRot = -0.1047f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = 0.35f
        this.BeltBase.xRot = 0.09f
        this.Skirt01.xRot = -0.21f

        this.Hat06a.xRot = -angleX * 0.1f + 0.7f
        this.Hat06b.xRot = -angleX3 * 0.1f + 1.04f
        this.Hair03.xRot = angleX * 0.05f - 0.09f + headX
        this.Hair03.zRot = -0.09f
        this.Hair04.xRot = -angleX1 * 0.06f + 0.26f + headX
        this.Hair04.zRot = -0.22f
        this.Hair05.xRot = -angleX2 * 0.07f + 0.52f + headX
        this.Hair05.zRot = 0.35f
        this.Hair06.xRot = -angleX3 * 0.12f - 0.15f + headX
        this.Hair06.zRot = 0.52f

        this.ArmLeft01.zRot = angleX * 0.03f - 0.3f
        this.ArmRight01.zRot = -angleX * 0.03f + 0.3f

        this.ArmLeft01.xRot = angleAdd2 * 0.4f + 0.26f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft02.xRot = 0.0f
        this.ArmLeft02.zRot = 0.0f

        this.ArmRight01.xRot = angleAdd1 * 0.4f + 0.26f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight02.xRot = 0.0f
        this.ArmRight02.zRot = 0.0f

        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.0873f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.yRot = 0.0f
        this.LegLeft02.zRot = 0.0f

        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.0873f
        this.LegRight02.xRot = 0.0f
        this.LegRight02.yRot = 0.0f
        this.LegRight02.zRot = 0.0f

        this.EquipBaseL.xRot = 0.05f
        this.EquipBaseR.xRot = 0.05f
        this.EquipHeadC02.xRot = this.Head.xRot * 0.5f - 0.04f
        this.EquipHeadC02_1.xRot = this.Head.xRot * 0.5f - 0.12f
        this.EquipLT01.xRot = this.Head.xRot * 0.8f - 0.2f
        this.EquipLT01_1.xRot = this.Head.xRot * 0.8f - 0.2f
        this.EquipLJaw.xRot = angleX * 0.15f + 0.15f
        this.EquipLJaw_1.xRot = angleX3 * 0.15f + 0.15f

        this.HairL01.xRot = angleX * 0.02f + headX + 0.14f
        this.HairR01.xRot = angleX * 0.02f + headX - 0.09f

        val headZ = this.Head.zRot * -0.5f
        this.Hair03.zRot += headZ
        this.Hair04.zRot += headZ
        this.Hair05.zRot += headZ
        this.Hair06.zRot += headZ
        this.HairL01.zRot = headZ - 0.09f
        this.HairR01.zRot = headZ - 0.09f

        this.LegLeft01.xRot = addk1
        this.LegRight01.xRot = addk2
    }

    private fun applySpecialPoseAdjustments(entity: T?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        val angleAdd1 = cos((limbSwing * 0.7f).toDouble()).toFloat() * limbSwingAmount
        val angleAdd2 = cos(limbSwing * 0.7f + Math.PI).toFloat() * limbSwingAmount

        val isSprinting = entity!!.isSprinting || limbSwingAmount > 0.9f
        val isCrouching = entity.isCrouching
        val isPassenger = entity.isPassenger
        val isSitting = entity.isInSittingPose || (isPassenger && entity.vehicle !is EntityMountBase)

        if (isSprinting) {
            this.Head.xRot -= 0.5f
            this.BodyMain.xRot = 0.5f
            this.ArmLeft01.xRot = angleAdd2 * 0.1f + 0.55f
            this.ArmLeft01.yRot = 0.0f
            this.ArmLeft01.zRot = -0.5f
            this.ArmRight01.xRot = angleAdd1 * 0.1f + 0.55f
            this.ArmRight01.yRot = 0.0f
            this.ArmRight01.zRot = 0.5f
            this.Hair05.xRot -= 0.2f
            this.LegLeft01.xRot = angleAdd1 * 0.8f - 0.75f
            this.LegRight01.xRot = angleAdd2 * 0.8f - 0.75f
        }

        if (isCrouching) {
            this.poseTranslateY = SNEAK_TRANSLATE_Y
            this.Head.xRot -= 0.6283f
            this.BodyMain.xRot = 0.8727f
            this.ArmLeft01.xRot = -0.35f
            this.ArmLeft01.yRot = 0.0f
            this.ArmLeft01.zRot = 0.2618f
            this.ArmRight01.xRot = -0.35f
            this.ArmRight01.yRot = 0.0f
            this.ArmRight01.zRot = -0.2618f
            this.LegLeft01.xRot -= 1.0f
            this.LegRight01.xRot -= 1.0f
            this.Hair03.xRot -= 0.6f
            this.Hair04.xRot -= 0.6f
            this.Hair05.xRot -= 0.6f
            this.Hair06.xRot -= 0.6f
        }

        if (isSitting) {
            this.isSittingPose = true
            if (hasLegacyState(entity, 1, 4)) {
                if (hasLegacyState(entity, 7, 4)) {
                    this.poseTranslateY = 0.46f * 3.5f
                    Head.xRot = 0.4f
                    BeltBase.xRot = -0.9f
                    Skirt01.xRot = -0.14f
                    Skirt01.y = skirt01DefaultY + (-0.12f * OFFSET_SCALE)
                    ArmLeft01.xRot = 0.4f
                    ArmLeft01.yRot = -2.9671f
                    ArmLeft01.zRot = -2.62f
                    ArmLeft02.zRot = 1.0f
                    ArmRight01.xRot = 0.5236f
                    ArmRight01.yRot = 2.9671f
                    ArmRight01.zRot = 2.62f
                    ArmRight02.zRot = -1.0f
                    LegLeft01.xRot = -2.4131f
                    LegRight01.xRot = -2.2689f
                    LegLeft01.zRot = -0.2731f
                    LegLeft02.xRot = 1.4570f
                    LegRight01.zRot = 0.2276f
                    LegRight02.xRot = 1.0472f
                    EquipBaseL.xRot = -0.6f
                    EquipBaseR.xRot = -0.6f
                    EquipBaseL.y = equipBaseLDefaultY + (-0.62f * OFFSET_SCALE)
                    EquipBaseR.y = equipBaseRDefaultY + (-0.62f * OFFSET_SCALE)
                } else {
                    this.poseTranslateY = 0.43f * 3.5f
                    Head.xRot -= 0.7f
                    BodyMain.xRot = 0.35f
                    BeltBase.xRot = -0.5f
                    Skirt01.xRot = -0.14f
                    Skirt01.y = skirt01DefaultY + (-0.12f * OFFSET_SCALE)
                    ArmLeft01.xRot = -0.5236f
                    ArmLeft01.yRot = 0.0f
                    ArmLeft01.zRot = 0.3491f
                    ArmRight01.xRot = -0.5236f
                    ArmRight01.yRot = 0.0f
                    ArmRight01.zRot = -0.3491f
                    LegLeft01.xRot = -1.4486f
                    LegLeft01.yRot = -0.5236f
                    LegLeft01.zRot = -1.3963f
                    LegLeft02.xRot = 2.1817f
                    LegLeft02.z = legLeft02DefaultZ + (0.37f * OFFSET_SCALE)
                    LegRight01.xRot = -1.4486f
                    LegRight01.yRot = 0.5236f
                    LegRight01.zRot = 1.3963f
                    LegRight02.xRot = 2.1817f
                    LegRight02.z = legRight02DefaultZ + (0.37f * OFFSET_SCALE)
                    EquipBaseL.xRot = -0.9f
                    EquipBaseR.xRot = -0.9f
                    EquipBaseL.y = equipBaseLDefaultY + (-0.4f * OFFSET_SCALE)
                    EquipBaseR.y = equipBaseRDefaultY + (-0.4f * OFFSET_SCALE)
                    Hair03.xRot -= 0.1f
                    Hair04.xRot -= 0.3f
                    Hair05.xRot -= 0.5f
                    Hair06.xRot -= 0.6f
                }
            } else if (!EquipBaseL.visible) {
                this.poseTranslateY = 0.44f * 3.5f
                Head.xRot -= 0.7f
                BodyMain.xRot = 0.5236f
                BeltBase.xRot = -0.9f
                Skirt01.xRot = -0.14f
                Skirt01.y = skirt01DefaultY + (-0.12f * OFFSET_SCALE)
                ArmLeft01.xRot = -0.5236f
                ArmLeft01.yRot = 0.0f
                ArmLeft01.zRot = 0.3146f
                ArmRight01.xRot = -0.5236f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = -0.3146f
                LegLeft01.xRot = -2.2689f
                LegRight01.xRot = -2.2689f
                LegLeft01.yRot = -0.3491f
                LegLeft01.zRot = 0.0873f
                LegLeft02.z = legLeft02DefaultZ
                LegRight01.yRot = 0.3491f
                LegRight01.zRot = -0.0873f
                LegRight02.z = legRight02DefaultZ
                Hair03.xRot -= 0.1f
                Hair04.xRot -= 0.3f
                Hair05.xRot -= 0.5f
                Hair06.xRot -= 0.6f
            } else {
                this.poseTranslateY = SIT_TRANSLATE_Y
                Head.xRot -= 0.7f
                BodyMain.xRot = 0.5236f
                ArmLeft01.xRot = -0.5236f
                ArmLeft01.yRot = 0.0f
                ArmLeft01.zRot = 0.3146f
                ArmRight01.xRot = -0.5236f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = -0.3146f
                LegLeft01.xRot = -2.23f
                LegRight01.xRot = -2.23f
                LegLeft01.yRot = -0.3491f
                LegLeft01.zRot = 0.0873f
                LegRight01.yRot = 0.3491f
                LegRight01.zRot = -0.0873f
                EquipBaseL.xRot = -1.34f
                EquipBaseR.xRot = -1.34f
                Hair03.xRot -= 0.1f
                Hair04.xRot -= 0.3f
                Hair05.xRot -= 0.5f
                Hair06.xRot -= 0.6f
            }
        }

        if (entity.attackTick > 0) {
            ArmLeft01.xRot = -1.4f + Head.xRot * 0.75f
            ArmLeft01.yRot = 0.17f
            ArmLeft01.zRot = 0.26f
            ArmLeft02.zRot = 0.0f
            ArmRight01.xRot = -1.22f + Head.xRot * 0.75f
            ArmRight01.yRot = 0.0f
            ArmRight01.zRot = -0.52f
            ArmRight02.zRot = -0.78f
        }

        val partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
        val customAttackAnim = if (entity != null) entity.getCustomAttackAnim(partialTick) else 0.0f
        if (customAttackAnim > 0.0f) {
            val f7 = Mth.sin(customAttackAnim * customAttackAnim * Math.PI.toFloat())
            val f8 = Mth.sin(Mth.sqrt(customAttackAnim) * Math.PI.toFloat())
            ArmRight01.xRot = -0.4f
            ArmRight01.yRot = 0.0f
            ArmRight01.zRot = -0.2f
            ArmRight01.xRot += -f8 * 80.0f * (Math.PI.toFloat() / 180f)
            ArmRight01.yRot += -f7 * 20.0f * (Math.PI.toFloat() / 180f) + 0.2f
            ArmRight01.zRot += -f8 * 20.0f * (Math.PI.toFloat() / 180f)
        }
    }

    private fun syncGlowParts() {
        this.GlowBodyMain.copyFrom(this.BodyMain)
        this.GlowNeck.copyFrom(this.Neck)
        this.GlowHead.copyFrom(this.Head)
        this.EquipLegL.copyFrom(this.LegLeft01)
        this.EquipLegR.copyFrom(this.LegRight01)
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "destroyer_hime"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelDestroyerHime")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelDestroyerHime")
        private val SIT_TRANSLATE_Y = sittingY("ModelDestroyerHime")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 104).addBox(-6.5f, -11f, -4f, 13f, 17f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(24, 86).mirror()
                    .addBox(-3f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, -9.3f, -0.7f, 0.2618f, 0f, 0.7854f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(24, 69).mirror().addBox(0f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 11f, 2.5f)
            )

            val ArmRight02a = ArmRight02.addOrReplaceChild(
                "ArmRight02a",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.5f, 6.5f, -2.4f, 0.0524f, 0f, 0f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 47).addBox(-7.5f, 0f, -5.7f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.3f, 0.3491f, 0f, 0f)
            )

            val EquipLegL = Butt.addOrReplaceChild(
                "EquipLegL",
                CubeListBuilder.create().texOffs(19, 3).addBox(-3.5f, 0f, -3.5f, 7f, 9f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.8f, 5.5f, -2.6f, -0.2793f, 0f, 0.0873f)
            )

            val Skirt01 = Butt.addOrReplaceChild(
                "Skirt01",
                CubeListBuilder.create().texOffs(74, 10).addBox(-8.5f, 0f, -6f, 17f, 7f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.5f, -0.5f, -0.2094f, 0f, 0f)
            )

            val EquipLegR = Butt.addOrReplaceChild(
                "EquipLegR",
                CubeListBuilder.create().texOffs(9, 0).addBox(-3.5f, 0f, -3.5f, 7f, 9f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.8f, 5.5f, -2.6f, -0.2094f, 0f, -0.0873f)
            )

            val BeltBase = Butt.addOrReplaceChild(
                "BeltBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0.0873f, 0f, 0f)
            )

            val Belt05 = BeltBase.addOrReplaceChild(
                "Belt05",
                CubeListBuilder.create().texOffs(0, 2).addBox(0f, 0f, -1f, 9f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1f, 4f, 0f, 0.1047f, 0f)
            )

            val Belt01 = BeltBase.addOrReplaceChild(
                "Belt01",
                CubeListBuilder.create().texOffs(0, 8).addBox(-9f, 0f, 0f, 9f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1f, -8f, 0f, 0.1047f, 0f)
            )

            val Belt02 = BeltBase.addOrReplaceChild(
                "Belt02",
                CubeListBuilder.create().texOffs(0, 13).addBox(0f, 0f, 0f, 9f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1f, -8f, 0f, -0.1047f, 0f)
            )

            val Belt06 = BeltBase.addOrReplaceChild(
                "Belt06",
                CubeListBuilder.create().texOffs(0, 0).addBox(-9f, 0f, -1f, 9f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1f, 4f, 0f, -0.1047f, 0f)
            )

            val Belt03 = BeltBase.addOrReplaceChild(
                "Belt03",
                CubeListBuilder.create().texOffs(0, 11).addBox(0f, 0f, 0f, 9f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8.9f, -1f, 2.8f, 0f, 1.5708f, 0f)
            )

            val Belt07 = BeltBase.addOrReplaceChild(
                "Belt07",
                CubeListBuilder.create().texOffs(0, 34).addBox(0f, 0f, 0f, 1f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8.8f, -2.1f, -4f, -0.5236f, 0f, 0f)
            )

            val Belt04 = BeltBase.addOrReplaceChild(
                "Belt04",
                CubeListBuilder.create().texOffs(0, 6).addBox(-9f, 0f, 0f, 9f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8.9f, -1f, 2.8f, 0f, -1.5708f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 84).addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.8f, 5.5f, -2.6f, -0.2793f, 0f, 0.0873f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 63).addBox(-6f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(3f, 14f, -3f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 84).mirror().addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.8f, 5.5f, -2.6f, -0.2094f, 0f, -0.0873f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 63).mirror().addBox(0f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3f, 14f, -3f)
            )

            val EquipBaseL = Butt.addOrReplaceChild(
                "EquipBaseL",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -3f, -3f, 16f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, 10f, -3f, 0.0524f, -0.1396f, 0.1396f)
            )

            val EquipLHead = EquipBaseL.addOrReplaceChild(
                "EquipLHead",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.5f, -6f, -10f, 11f, 6f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(9f, -2f, 0f, -0.1396f, 0f, 0f)
            )

            val EquipLTU = EquipLHead.addOrReplaceChild(
                "EquipLTU",
                CubeListBuilder.create().texOffs(47, 29).addBox(-4.5f, 0f, -9f, 9f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1.1f, -0.7f, 0.0873f, 0f, 0f)
            )

            val EquipHeadC01 = EquipLHead.addOrReplaceChild(
                "EquipHeadC01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, 0f, 0f, 5f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -8.7f, -7f, -0.1745f, 0f, 0f)
            )

            val EquipHeadC02 = EquipHeadC01.addOrReplaceChild(
                "EquipHeadC02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, -0.6f, -7f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 1.5f, 0.5f)
            )

            val EquipLT01 = EquipBaseL.addOrReplaceChild(
                "EquipLT01",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -5f, -6f, 4f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(15f, 0f, 0f, 0f, -0.1396f, 0f)
            )

            val EquipLT02b = EquipLT01.addOrReplaceChild(
                "EquipLT02b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -4f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offset(2f, -0.3f, -5.8f)
            )

            val EquipLT02d = EquipLT01.addOrReplaceChild(
                "EquipLT02d",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -4f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offset(2f, 4.3f, -5.8f)
            )

            val EquipLT02c = EquipLT01.addOrReplaceChild(
                "EquipLT02c",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -4f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offset(2f, 2f, -5.8f)
            )

            val EquipLT02a = EquipLT01.addOrReplaceChild(
                "EquipLT02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -4f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offset(2f, -2.6f, -5.8f)
            )

            val EquipLJaw = EquipBaseL.addOrReplaceChild(
                "EquipLJaw",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, 4f, -9f, 8f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(9f, 0f, -1.2f, 0.1745f, 0f, 0f)
            )

            val EquipLTD = EquipLJaw.addOrReplaceChild(
                "EquipLTD",
                CubeListBuilder.create().texOffs(47, 29).mirror()
                    .addBox(-4.5f, 0f, -9f, 9f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 5f, 0.2f)
            )

            val EquipLB = EquipBaseL.addOrReplaceChild(
                "EquipLB",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, 0f, 0f, 10f, 10f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(9f, -3.5f, -5f, 0.0873f, 0f, 0f)
            )

            val EquipBaseR = Butt.addOrReplaceChild(
                "EquipBaseR",
                CubeListBuilder.create().texOffs(0, 0).addBox(-16f, -3f, -3f, 16f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7f, 10f, -3f, 0.0524f, 0.1396f, -0.1396f)
            )

            val EquipRHead = EquipBaseR.addOrReplaceChild(
                "EquipRHead",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.5f, -6f, -10f, 11f, 6f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9f, -2f, 0f, -0.1396f, 0f, 0f)
            )

            val EquipLTU_1 = EquipRHead.addOrReplaceChild(
                "EquipLTU_1",
                CubeListBuilder.create().texOffs(47, 29).addBox(-4.5f, 0f, -9f, 9f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1.1f, -0.7f, 0.0873f, 0f, 0f)
            )

            val EquipHeadC01_1 = EquipRHead.addOrReplaceChild(
                "EquipHeadC01_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, 0f, 0f, 5f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -8.7f, -7f, -0.1745f, 0f, 0f)
            )

            val EquipHeadC02_1 = EquipHeadC01_1.addOrReplaceChild(
                "EquipHeadC02_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, -0.6f, -7f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 1.5f, 0.5f)
            )

            val EquipLT01_1 = EquipBaseR.addOrReplaceChild(
                "EquipLT01_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, -5f, -6f, 4f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-15f, 0f, 0f, 0f, 0.1396f, 0f)
            )

            val EquipLT02a_1 = EquipLT01_1.addOrReplaceChild(
                "EquipLT02a_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -4f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offset(-2f, -2.6f, -5.8f)
            )

            val EquipLT02b_1 = EquipLT01_1.addOrReplaceChild(
                "EquipLT02b_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -4f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offset(-2f, -0.3f, -5.8f)
            )

            val EquipLT02d_1 = EquipLT01_1.addOrReplaceChild(
                "EquipLT02d_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -4f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offset(-2f, 4.3f, -5.8f)
            )

            val EquipLT02c_1 = EquipLT01_1.addOrReplaceChild(
                "EquipLT02c_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -4f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offset(-2f, 2f, -5.8f)
            )

            val EquipLJaw_1 = EquipBaseR.addOrReplaceChild(
                "EquipLJaw_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, 4f, -9f, 8f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9f, 0f, -1.2f, 0.1745f, 0f, 0f)
            )

            val EquipLTD_1 = EquipLJaw_1.addOrReplaceChild(
                "EquipLTD_1",
                CubeListBuilder.create().texOffs(47, 29).addBox(-4.5f, 0f, -9f, 9f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 5f, 0.2f)
            )

            val EquipLB_1 = EquipBaseR.addOrReplaceChild(
                "EquipLB_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, 0f, 0f, 10f, 10f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9f, -3.5f, -5f, 0.0873f, 0f, 0f)
            )

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(84, 27).addBox(-7f, 0f, -4.4f, 14f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -11.3f, 0f, -0.0873f, 0f, 0f)
            )

            val Cloth02 = Cloth01.addOrReplaceChild(
                "Cloth02",
                CubeListBuilder.create().texOffs(38, 47).addBox(-4f, 0f, 0f, 8f, 8f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.4f, -4.3f, 0.0524f, 0f, 0f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, -2f, -4.9f, 7f, 2f, 8f, CubeDeformation(0f)),
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
                CubeListBuilder.create().texOffs(50, 77).addBox(-8f, -8f, -7.4f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0.1f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(90, 101).mirror()
                    .addBox(-0.5f, 0f, -1.5f, 1f, 7f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, 6.5f, -4.4f, -0.0873f, -0.0873f, -0.0873f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(90, 101).addBox(-0.5f, 0f, -1.5f, 1f, 7f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, 7f, -4.4f, -0.1396f, 0.0873f, 0.0873f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(106, 31).addBox(0f, -6f, -10.5f, 0f, 11f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.5f, -7f, -6f, 0.2094f, 0.6981f, 0f)
            )

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(52, 56).addBox(-8.5f, 0f, 0f, 17f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -7f)
            )

            val Hair02 = Head.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(9, 6).addBox(0f, -1.5f, -1.5f, 2f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.5f, -10f, 3.5f, 0f, -0.0873f, 0f)
            )

            val Hair03 = Hair02.addOrReplaceChild(
                "Hair03",
                CubeListBuilder.create().texOffs(40, 99).addBox(0f, -3f, -2f, 4f, 11f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.7f, 0f, 0f, -0.0873f, 0f, -0.0873f)
            )

            val Hair04 = Hair03.addOrReplaceChild(
                "Hair04",
                CubeListBuilder.create().texOffs(40, 99).addBox(-2f, 0f, -2f, 4f, 11f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, 6.5f, 0f, 0.2618f, 0f, -0.2276f)
            )

            val Hair05 = Hair04.addOrReplaceChild(
                "Hair05",
                CubeListBuilder.create().texOffs(40, 99).addBox(-2f, 0f, -2f, 4f, 11f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 0f, 0.5236f, 0f, 0.3491f)
            )

            val Hair06 = Hair05.addOrReplaceChild(
                "Hair06",
                CubeListBuilder.create().texOffs(40, 99).addBox(-2f, 0f, -2f, 4f, 11f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 0f, -0.2618f, 0f, 0.5236f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(46, 104).addBox(-7.5f, 0f, 0f, 15f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.8f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(54, 44).addBox(-7.5f, 0f, -7f, 15f, 5f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9.9f, 10f, 0.1571f, 0f, 0f)
            )

            val Hat01 = Head.addOrReplaceChild(
                "Hat01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6f, -6f, -6f, 12f, 6f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -11f, 1f, -0.4363f, 0f, 0f)
            )

            val Hat04a = Hat01.addOrReplaceChild(
                "Hat04a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, -8f, -3f, 6f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7.4f, -5.5f, 0.2618f, 0.2618f, -0.1745f)
            )

            val Hat04b = Hat04a.addOrReplaceChild(
                "Hat04b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, -5f, -2.5f, 5f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -6.8f, -0.4f, -0.5236f, 0f, 0f)
            )

            val Hat04c = Hat04b.addOrReplaceChild(
                "Hat04c",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, -5f, -2f, 4f, 5f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3.9f, 0f, -0.6109f, 0f, 0f)
            )

            val Hat05a = Hat01.addOrReplaceChild(
                "Hat05a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, -5f, -2f, 4f, 5f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -10f, 6f, -0.0873f, 0.5236f, 0.1745f)
            )

            val Hat05b = Hat05a.addOrReplaceChild(
                "Hat05b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, -4f, -1.5f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -4.1f, 0.3f, 0.6109f, 0f, 0f)
            )

            val Hat03 = Hat01.addOrReplaceChild(
                "Hat03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-8.5f, -6f, 0f, 17f, 7f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -5.4f, 0f, -0.2731f, 0f, 0f)
            )

            val Hat02b = Hat01.addOrReplaceChild(
                "Hat02b",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -6f, -7f, 10f, 7f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.7f, -5f, -2.9f, 0.1745f, -0.0524f, 0.0524f)
            )

            val Hat06b = Hat01.addOrReplaceChild(
                "Hat06b",
                CubeListBuilder.create().texOffs(44, 61).addBox(0f, 0f, -2f, 0f, 12f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8.5f, -6.4f, 2.5f, 1.0472f, 0.0873f, -0.4363f)
            )

            val Hat02a = Hat01.addOrReplaceChild(
                "Hat02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-10f, -6f, -7f, 10f, 7f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.7f, -5f, -3f, 0.1745f, 0.0524f, -0.0524f)
            )

            val Hat06a = Hat01.addOrReplaceChild(
                "Hat06a",
                CubeListBuilder.create().texOffs(44, 61).mirror().addBox(0f, 0f, -2f, 0f, 12f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8.5f, -6f, 2f, 0.6981f, 0.2618f, -0.6981f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(24, 86).addBox(-2f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.3f, -0.7f, 0.2618f, 0f, -0.7854f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(24, 69).addBox(-5f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 11f, 2.5f)
            )

            val ArmLeft02a = ArmLeft02.addOrReplaceChild(
                "ArmLeft02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, -3.5f, 7f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.5f, 6.5f, -2.4f, 0.0524f, 0f, 0f)
            )

            val Cannon01 = ArmLeft02.addOrReplaceChild(
                "Cannon01",
                CubeListBuilder.create().texOffs(22, 21).addBox(-3f, 0f, -3f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(-2.5f, 3f, -2.5f)
            )

            val Cannon03 = Cannon01.addOrReplaceChild(
                "Cannon03",
                CubeListBuilder.create().texOffs(0, 21).addBox(-3.5f, 0f, 0f, 7f, 9f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.3f, 0f, 0.0524f, 0f, 0f)
            )

            val Cannon04 = Cannon01.addOrReplaceChild(
                "Cannon04",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, 0f, -0.5f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(-1f, 10f, 0f)
            )

            val Cannon02 = Cannon01.addOrReplaceChild(
                "Cannon02",
                CubeListBuilder.create().texOffs(52, 0).addBox(-4f, 0f, -6f, 8f, 13f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1f, 2f, -0.0873f, 0f, 0f)
            )

            val Cannon05 = Cannon01.addOrReplaceChild(
                "Cannon05",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, 0f, -0.5f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(1f, 10f, 0f)
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

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
