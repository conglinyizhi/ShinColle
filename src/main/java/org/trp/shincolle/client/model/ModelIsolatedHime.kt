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
import org.trp.shincolle.client.model.LegacyPoseOffsets.ridingY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sittingY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sneakY
import org.trp.shincolle.entity.EntityIsolatedHime
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import kotlin.math.cos

class ModelIsolatedHime<T : EntityShipBase>(root: ModelPart) : ShipModelHumanoidBase<T>(), IGlowableModel {
    private var isDeadPose = false
    private var isSittingPose = false
    override var poseTranslateY = 0f

    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val Butt: ModelPart
    private val ArmRight01: ModelPart
    private val ArmLeft01: ModelPart
    private val Cloth02a: ModelPart
    private val Head: ModelPart
    private val Cloth01a: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val Hair01: ModelPart
    private val HatBase: ModelPart
    private val HairU01: ModelPart
    private val Ahoke: ModelPart
    private val Hair02: ModelPart
    private val Hair03: ModelPart
    private val Hat01: ModelPart
    private val Hat03: ModelPart
    private val Hat05: ModelPart
    private val HeadH1: ModelPart
    private val HeadH2: ModelPart
    private val HeadH3: ModelPart
    private val HeadH4: ModelPart
    private val HeadH5: ModelPart
    private val HeadH6: ModelPart
    private val Hat02a: ModelPart
    private val Hat02b: ModelPart
    private val Hat02c: ModelPart
    private val Hat02d: ModelPart
    private val Hat02e: ModelPart
    private val Hat02f: ModelPart
    private val Hat02g: ModelPart
    private val Hat02h: ModelPart
    private val Hat02i: ModelPart
    private val Hat02j: ModelPart
    private val Hat04a: ModelPart
    private val Hat04b: ModelPart
    private val Hat04c: ModelPart
    private val Hat04d: ModelPart
    private val Hat04e: ModelPart
    private val Hat04f: ModelPart
    private val Hat04g: ModelPart
    private val Hat04h: ModelPart
    private val Hat06a: ModelPart
    private val Hat02b_1: ModelPart
    private val Hat02d_1: ModelPart
    private val Hat02e_1: ModelPart
    private val Hat02f_1: ModelPart
    private val Hat02g_1: ModelPart
    private val Hat02h_1: ModelPart
    private val Hat02i_1: ModelPart
    private val Cloth01b: ModelPart
    private val Cloth01c: ModelPart
    private val Cloth01b2: ModelPart
    private val Cloth01c2: ModelPart
    private val Skirt01: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft01: ModelPart
    private val Skirt02: ModelPart
    private val Skirt03: ModelPart
    private val LegRight02a: ModelPart
    private val LegArmor02a: ModelPart
    private val LegRight02b: ModelPart
    private val LegArmor02b: ModelPart
    private val LegArmor02c: ModelPart
    private val LegLeft02a: ModelPart
    private val LegArmor01a: ModelPart
    private val LegLeft02b: ModelPart
    private val LegArmor01b: ModelPart
    private val LegArmor01c: ModelPart
    private val ArmRight02: ModelPart
    private val Cloth02c: ModelPart
    private val Cloth03a: ModelPart
    private val ArmLeft02: ModelPart
    private val Cloth02b: ModelPart
    private val Cloth03b: ModelPart
    private val EquipRdL01: ModelPart
    private val EquipRdL02: ModelPart
    private val EquipRdL03: ModelPart
    private val EquipRdL04: ModelPart
    private val EquipRdL05: ModelPart
    private val EquipRdL06: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val GlowHatBase: ModelPart
    private val buttDefaultY: Float
    private val buttDefaultZ: Float
    private val cloth01aDefaultY: Float
    private val cloth01aDefaultZ: Float
    private val armLeft02DefaultX: Float
    private val armLeft02DefaultZ: Float
    private val armRight02DefaultX: Float
    private val armRight02DefaultZ: Float
    private val legLeft01DefaultY: Float
    private val legLeft01DefaultZ: Float
    private val legLeft02aDefaultX: Float
    private val legLeft02aDefaultZ: Float
    private val legRight01DefaultY: Float
    private val legRight01DefaultZ: Float
    private val legRight02aDefaultX: Float
    private val legRight02aDefaultY: Float
    private val legRight02aDefaultZ: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.Cloth02b = this.ArmLeft01.getChild("Cloth02b")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.Cloth03b = this.ArmLeft02.getChild("Cloth03b")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.Head.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.Hair03 = this.Hair02.getChild("Hair03")
        this.Hair = this.Head.getChild("Hair")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.HatBase = this.Head.getChild("HatBase")
        this.Hat03 = this.HatBase.getChild("Hat03")
        this.Hat04f = this.Hat03.getChild("Hat04f")
        this.Hat04g = this.Hat03.getChild("Hat04g")
        this.Hat04e = this.Hat03.getChild("Hat04e")
        this.Hat04d = this.Hat03.getChild("Hat04d")
        this.Hat04a = this.Hat03.getChild("Hat04a")
        this.Hat04h = this.Hat03.getChild("Hat04h")
        this.Hat04b = this.Hat03.getChild("Hat04b")
        this.Hat04c = this.Hat03.getChild("Hat04c")
        this.Hat01 = this.HatBase.getChild("Hat01")
        this.Hat02d = this.Hat01.getChild("Hat02d")
        this.Hat02a = this.Hat01.getChild("Hat02a")
        this.Hat02b = this.Hat01.getChild("Hat02b")
        this.Hat02g = this.Hat01.getChild("Hat02g")
        this.Hat02h = this.Hat01.getChild("Hat02h")
        this.Hat02e = this.Hat01.getChild("Hat02e")
        this.Hat02c = this.Hat01.getChild("Hat02c")
        this.Hat02i = this.Hat01.getChild("Hat02i")
        this.Hat02f = this.Hat01.getChild("Hat02f")
        this.Hat02j = this.Hat01.getChild("Hat02j")
        this.Hat05 = this.HatBase.getChild("Hat05")
        this.Hat02b_1 = this.Hat05.getChild("Hat02b_1")
        this.Hat02e_1 = this.Hat05.getChild("Hat02e_1")
        this.Hat06a = this.Hat05.getChild("Hat06a")
        this.Hat02g_1 = this.Hat05.getChild("Hat02g_1")
        this.Hat02i_1 = this.Hat05.getChild("Hat02i_1")
        this.Hat02h_1 = this.Hat05.getChild("Hat02h_1")
        this.Hat02f_1 = this.Hat05.getChild("Hat02f_1")
        this.Hat02d_1 = this.Hat05.getChild("Hat02d_1")
        this.Cloth01a = this.Neck.getChild("Cloth01a")
        this.Cloth01b2 = this.Cloth01a.getChild("Cloth01b2")
        this.Cloth01c = this.Cloth01a.getChild("Cloth01c")
        this.Cloth01b = this.Cloth01a.getChild("Cloth01b")
        this.Cloth01c2 = this.Cloth01a.getChild("Cloth01c2")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.Cloth02c = this.ArmRight01.getChild("Cloth02c")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.Cloth03a = this.ArmRight02.getChild("Cloth03a")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02a = this.LegLeft01.getChild("LegLeft02a")
        this.LegArmor01a = this.LegLeft01.getChild("LegArmor01a")
        this.LegArmor01b = this.LegArmor01a.getChild("LegArmor01b")
        this.LegArmor01c = this.LegArmor01b.getChild("LegArmor01c")
        this.LegLeft02b = this.LegLeft01.getChild("LegLeft02b")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.Skirt02 = this.Skirt01.getChild("Skirt02")
        this.Skirt03 = this.Skirt02.getChild("Skirt03")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02b = this.LegRight01.getChild("LegRight02b")
        this.LegRight02a = this.LegRight01.getChild("LegRight02a")
        this.LegArmor02a = this.LegRight01.getChild("LegArmor02a")
        this.LegArmor02b = this.LegArmor02a.getChild("LegArmor02b")
        this.LegArmor02c = this.LegArmor02b.getChild("LegArmor02c")
        this.Cloth02a = this.BodyMain.getChild("Cloth02a")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        this.GlowHatBase = this.GlowHead.getChild("GlowHatBase")
        this.HeadH1 = this.GlowHatBase.getChild("HeadH1")
        this.HeadH2 = this.HeadH1.getChild("HeadH2")
        this.HeadH3 = this.HeadH2.getChild("HeadH3")
        this.HeadH4 = this.GlowHatBase.getChild("HeadH4")
        this.HeadH5 = this.HeadH4.getChild("HeadH5")
        this.HeadH6 = this.HeadH5.getChild("HeadH6")
        this.EquipRdL01 = this.GlowBodyMain.getChild("EquipRdL01")
        this.EquipRdL02 = this.EquipRdL01.getChild("EquipRdL02")
        this.EquipRdL03 = this.EquipRdL02.getChild("EquipRdL03")
        this.EquipRdL04 = this.EquipRdL03.getChild("EquipRdL04")
        this.EquipRdL05 = this.EquipRdL04.getChild("EquipRdL05")
        this.EquipRdL06 = this.EquipRdL05.getChild("EquipRdL06")
        this.initFaceParts(this.GlowHead)
        this.buttDefaultY = this.Butt.y
        this.buttDefaultZ = this.Butt.z
        this.cloth01aDefaultY = this.Cloth01a.y
        this.cloth01aDefaultZ = this.Cloth01a.z
        this.armLeft02DefaultX = this.ArmLeft02.x
        this.armLeft02DefaultZ = this.ArmLeft02.z
        this.armRight02DefaultX = this.ArmRight02.x
        this.armRight02DefaultZ = this.ArmRight02.z
        this.legLeft01DefaultY = this.LegLeft01.y
        this.legLeft01DefaultZ = this.LegLeft01.z
        this.legLeft02aDefaultX = this.LegLeft02a.x
        this.legLeft02aDefaultZ = this.LegLeft02a.z
        this.legRight01DefaultY = this.LegRight01.y
        this.legRight01DefaultZ = this.LegRight01.z
        this.legRight02aDefaultX = this.LegRight02a.x
        this.legRight02aDefaultY = this.LegRight02a.y
        this.legRight02aDefaultZ = this.LegRight02a.z
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
        applyFaceAndMouth(entity)

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
        this.Butt.z = this.buttDefaultZ
        this.Cloth01a.y = this.cloth01aDefaultY
        this.Cloth01a.z = this.cloth01aDefaultZ
        this.ArmLeft02.x = this.armLeft02DefaultX
        this.ArmLeft02.z = this.armLeft02DefaultZ
        this.ArmRight02.x = this.armRight02DefaultX
        this.ArmRight02.z = this.armRight02DefaultZ
        this.LegLeft01.y = this.legLeft01DefaultY
        this.LegLeft01.z = this.legLeft01DefaultZ
        this.LegLeft02a.x = this.legLeft02aDefaultX
        this.LegLeft02a.z = this.legLeft02aDefaultZ
        this.LegRight01.y = this.legRight01DefaultY
        this.LegRight01.z = this.legRight01DefaultZ
        this.LegRight02a.x = this.legRight02aDefaultX
        this.LegRight02a.y = this.legRight02aDefaultY
        this.LegRight02a.z = this.legRight02aDefaultZ
    }

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) {
            return
        }

        val showHatBase = entity.getEquipFlag(EntityIsolatedHime.EQUIP_HAT_BASE)
        val showHeadGear = entity.getEquipFlag(EntityIsolatedHime.EQUIP_HEAD_GEAR)
        val showCloth1 = entity.getEquipFlag(EntityIsolatedHime.EQUIP_CLOTH_1)
        val showCloth2 = entity.getEquipFlag(EntityIsolatedHime.EQUIP_CLOTH_2)
        val showCloth3 = entity.getEquipFlag(EntityIsolatedHime.EQUIP_CLOTH_3)
        val showLegOuter = entity.getEquipFlag(EntityIsolatedHime.EQUIP_LEG_OUTER)
        val showLegArmor = entity.getEquipFlag(EntityIsolatedHime.EQUIP_LEG_ARMOR)
        val showRoad = entity.getEquipFlag(EntityIsolatedHime.EQUIP_ROAD)

        this.HatBase.visible = showHatBase
        this.GlowHatBase.visible = showHatBase
        this.HeadH1.visible = showHeadGear
        this.HeadH4.visible = showHeadGear
        this.Cloth01a.visible = showCloth1
        this.Cloth02a.visible = showCloth2
        this.Cloth02b.visible = showCloth2
        this.Cloth02c.visible = showCloth2
        this.Cloth03a.visible = showCloth3
        this.Cloth03b.visible = showCloth3
        this.LegLeft02b.visible = showLegOuter
        this.LegRight02b.visible = showLegOuter
        this.LegLeft02a.visible = !showLegOuter
        this.LegRight02a.visible = !showLegOuter
        this.LegArmor01a.visible = showLegArmor
        this.LegArmor02a.visible = showLegArmor
        this.EquipRdL01.visible = showRoad
        this.EquipRdL02.visible = showRoad
        this.EquipRdL03.visible = showRoad
        this.EquipRdL04.visible = showRoad
        this.EquipRdL05.visible = showRoad
        this.EquipRdL06.visible = showRoad
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        this.Head.xRot = 0.5f
        this.Head.yRot = 0.0f
        this.Head.zRot = 0.0f
        this.Ahoke.yRot = 0.45f
        this.BodyMain.xRot = 0.5f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = -0.85f
        this.Skirt01.xRot = -0.087f
        this.Skirt02.xRot = -0.087f
        this.Skirt03.xRot = -0.052f

        this.Cloth01a.y = this.cloth01aDefaultY + (0.092f * OFFSET_SCALE)
        this.Cloth01a.z = this.cloth01aDefaultZ + (0.1f * OFFSET_SCALE)
        this.Cloth01c.xRot = -0.79f
        this.Cloth01c2.xRot = -0.73f

        this.Hair01.xRot = -0.12f
        this.Hair01.yRot = 0.0f
        this.Hair01.zRot = 0.0f
        this.Hair02.xRot = -0.33f
        this.Hair02.yRot = 0.0f
        this.Hair02.zRot = 0.0f
        this.Hair03.xRot = -0.38f
        this.Hair03.yRot = 0.0f
        this.Hair03.zRot = 0.0f

        this.ArmLeft01.xRot = -1.1f
        this.ArmLeft01.yRot = 0.39f
        this.ArmLeft01.zRot = -0.05f
        this.ArmLeft02.xRot = -1.46f
        this.ArmLeft02.zRot = 0.0f

        this.ArmRight01.xRot = -1.1f
        this.ArmRight01.yRot = -0.39f
        this.ArmRight01.zRot = 0.05f
        this.ArmRight02.xRot = -1.46f
        this.ArmRight02.zRot = 0.0f

        this.LegLeft01.xRot = -1.96f
        this.LegLeft01.yRot = -0.6f
        this.LegLeft01.zRot = 1.56f
        this.LegLeft02a.xRot = 2.1f
        this.LegLeft02a.yRot = 0.0f
        this.LegLeft02a.zRot = 0.0f
        this.LegLeft02a.z = this.legLeft02aDefaultZ + (0.37f * OFFSET_SCALE)

        this.LegRight01.xRot = -0.96f
        this.LegRight01.yRot = 0.36f
        this.LegRight01.zRot = 0.14f
        this.LegRight02a.xRot = 1.2217f
        this.LegRight02a.yRot = -1.2217f
        this.LegRight02a.zRot = 1.0472f
        this.LegRight02a.y = this.legRight02aDefaultY + (-0.06f * OFFSET_SCALE)
    }

    private fun applyBasePose(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val angleX = cos((ageInTicks * 0.08f).toDouble()).toFloat()
        val angleX1 = cos((ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f).toDouble()).toFloat()
        val angleX2 = cos((ageInTicks * 0.08f + 0.6f + limbSwing * 0.5f).toDouble()).toFloat()
        val angleAdd1 = cos((limbSwing * 0.7f).toDouble()).toFloat() * limbSwingAmount * 0.5f
        val angleAdd2 = cos(limbSwing * 0.7f + Math.PI).toFloat() * limbSwingAmount * 0.5f

        if (entity!!.isInWater()) {
            this.poseTranslateY += angleX * 0.05f + 0.025f
        }

        val addk1 = angleAdd1 - 0.157f
        val addk2 = angleAdd2 - 0.296f

        this.Head.xRot = headPitch * (Math.PI.toFloat() / 180f)
        this.Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)
        this.Head.zRot = 0.0f
        val headX = this.Head.xRot * -0.5f

        this.Ahoke.yRot = angleX * 0.25f + 0.5236f
        this.BodyMain.xRot = -0.1047f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = 0.35f

        this.Skirt01.xRot = -0.087f
        this.Skirt02.xRot = -0.087f
        this.Skirt03.xRot = -0.052f

        this.Cloth01a.xRot = angleX * 0.08f + 0.79f
        this.Cloth01a.y = this.cloth01aDefaultY + (0.092f * OFFSET_SCALE)
        this.Cloth01a.z = this.cloth01aDefaultZ + (0.1f * OFFSET_SCALE)
        this.Cloth01c.xRot = -angleX * 0.12f - 0.9f
        this.Cloth01c2.xRot = -angleX * 0.12f - 0.85f

        this.Hair01.xRot = angleX * 0.03f + 0.21f + headX
        this.Hair01.zRot = 0.0f
        this.Hair02.xRot = -angleX1 * 0.04f + 0.12f + headX
        this.Hair02.zRot = 0.0f
        this.Hair03.xRot = -angleX2 * 0.07f - 0.26f
        this.Hair03.zRot = 0.0f

        this.ArmLeft01.xRot = angleAdd2 * 0.8f - 0.05f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = angleX * 0.025f - 0.3f
        this.ArmLeft02.xRot = 0.0f
        this.ArmLeft02.yRot = 0.0f
        this.ArmLeft02.zRot = 0.0f

        this.ArmRight01.xRot = angleAdd1 * 0.8f + 0.26f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = -angleX * 0.025f + 0.3f
        this.ArmRight02.xRot = 0.0f
        this.ArmRight02.yRot = 0.0f
        this.ArmRight02.zRot = 0.0f

        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.087f
        this.LegLeft02a.xRot = 0.0f
        this.LegLeft02a.yRot = 0.0f
        this.LegLeft02a.zRot = 0.0f

        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.087f
        this.LegRight02a.xRot = 0.0f
        this.LegRight02a.yRot = 0.0f
        this.LegRight02a.zRot = 0.0f

        this.LegLeft01.xRot = addk1
        this.LegRight01.xRot = addk2

        val headZ = this.Head.zRot * -0.5f
        this.Hair01.zRot = headZ
        this.Hair02.zRot = headZ
        this.Hair03.zRot = headZ
    }

    private fun applySpecialPoseAdjustments(entity: T?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        val angleAdd1 = cos((limbSwing * 0.7f).toDouble()).toFloat() * limbSwingAmount * 0.5f
        val headX = this.Head.xRot * -0.5f

        val isSprinting = entity!!.isSprinting || limbSwingAmount > 0.9f
        val isCrouching = entity.isCrouching
        val isPassenger = entity.isPassenger
        val isSitting = entity.isInSittingPose || (isPassenger && entity.vehicle !is EntityMountBase)

        if (isSprinting) {
            this.Hair01.xRot = angleAdd1 * 0.1f + limbSwingAmount * 0.4f + headX
            this.Hair03.xRot += 0.1f
            this.ArmLeft01.zRot += limbSwingAmount * -0.2f
            this.ArmRight01.zRot += limbSwingAmount * 0.2f
        }

        if (isCrouching) {
            this.poseTranslateY = SNEAK_TRANSLATE_Y
            this.Head.xRot -= 0.6283f
            this.BodyMain.xRot = 0.8727f
            this.Skirt01.xRot = -0.35f
            this.Skirt02.xRot = -0.19f
            this.Skirt03.xRot = -0.24f
            this.ArmLeft01.xRot = -0.35f
            this.ArmLeft01.zRot = 0.2618f
            this.ArmRight01.xRot = -0.35f
            this.ArmRight01.zRot = -0.2618f
            this.LegLeft01.xRot -= 1.02f
            this.LegRight01.xRot -= 1.02f
            this.Hair01.xRot += 0.37f
            this.Hair02.xRot += 0.23f
            this.Hair03.xRot -= 0.1f
        }

        if (isPassenger && entity.vehicle is EntityMountBase) {
            this.isSittingPose = true
            this.BodyMain.yRot += -40.0f * (Math.PI.toFloat() / 180f)
            if (isSitting) {
                if (hasLegacyState(entity, 1, 4)) {
                    this.poseTranslateY += 0.02f * 3f
                    this.setFace(2)
                    this.Head.xRot += 0.1f
                    this.BodyMain.xRot = -0.1f
                    this.Butt.xRot = -0.4f
                    this.Butt.z = this.buttDefaultZ + (0.19f * OFFSET_SCALE)
                    this.Ahoke.yRot = 0.5236f
                    this.Skirt01.xRot = -0.35f
                    this.Skirt02.xRot = -0.19f
                    this.Skirt03.xRot = -0.24f
                    this.Hair01.xRot = 0.21f + headX
                    this.Hair02.xRot = -0.28f + headX
                    this.Hair03.xRot = -0.24f
                    this.ArmLeft01.xRot = -1.18f
                    this.ArmLeft01.yRot = 0.27f
                    this.ArmLeft01.zRot = -0.1f
                    this.ArmLeft02.zRot = 0.92f
                    this.ArmRight01.xRot = -1.18f
                    this.ArmRight01.yRot = -0.27f
                    this.ArmRight01.zRot = 0.1f
                    this.ArmRight02.zRot = -1.32f
                    this.LegLeft01.xRot = -2.57f
                    this.LegRight01.xRot = -2.57f
                    this.LegLeft01.y = this.legLeft01DefaultY + (0.25f * OFFSET_SCALE)
                    this.LegLeft01.z = this.legLeft01DefaultZ + (-0.2f * OFFSET_SCALE)
                    this.LegLeft01.yRot = 0.11f
                    this.LegLeft01.zRot = -0.12f
                    this.LegLeft02a.xRot = 2.75f
                    this.LegLeft02a.zRot = 0.02f
                    this.LegLeft02a.z = this.legLeft02aDefaultZ + (0.37f * OFFSET_SCALE)
                    this.LegRight01.y = this.legRight01DefaultY + (0.25f * OFFSET_SCALE)
                    this.LegRight01.z = this.legRight01DefaultZ + (-0.2f * OFFSET_SCALE)
                    this.LegRight01.yRot = -0.11f
                    this.LegRight01.zRot = 0.12f
                    this.LegRight02a.xRot = 2.75f
                    this.LegRight02a.zRot = -0.02f
                    this.LegRight02a.z = this.legRight02aDefaultZ + (0.37f * OFFSET_SCALE)
                } else if (ageInTicks % 512 > 256) {
                    this.poseTranslateY += 0.0f
                    this.Head.xRot += 0.14f
                    this.BodyMain.xRot = -0.4363f
                    this.Skirt01.xRot = -0.35f
                    this.Skirt02.xRot = -0.19f
                    this.Skirt03.xRot = -0.24f
                    this.ArmLeft01.xRot = -0.3142f
                    this.ArmLeft01.zRot = 0.349f
                    this.ArmLeft02.zRot = 1.15f
                    this.ArmRight01.xRot = -0.4363f
                    this.ArmRight01.zRot = -0.2793f
                    this.ArmRight02.zRot = -1.4f
                    this.LegLeft01.xRot = -1.309f
                    this.LegRight01.xRot = -1.7f
                    this.LegLeft01.yRot = 0.3142f
                    this.LegLeft02a.xRot = 1.0472f
                    this.LegRight01.yRot = -0.35f
                    this.LegRight01.zRot = -0.2618f
                    this.LegRight02a.xRot = 0.9f
                    this.Hair01.xRot += 0.12f
                    this.Hair02.xRot += 0.15f
                    this.Hair03.xRot += 0.25f
                } else {
                    this.poseTranslateY += 0.03f * 3f
                    this.Head.xRot += 0.14f
                    this.BodyMain.xRot = -0.5236f
                    this.Skirt01.xRot = -0.35f
                    this.Skirt02.xRot = -0.19f
                    this.Skirt03.xRot = -0.24f
                    this.ArmLeft01.xRot = -0.4363f
                    this.ArmLeft01.zRot = 0.3142f
                    this.ArmRight01.xRot = -0.4363f
                    this.ArmRight01.zRot = -0.3142f
                    this.LegLeft01.xRot = -1.6232f
                    this.LegRight01.xRot = -1.5708f
                    this.LegLeft01.zRot = -0.3142f
                    this.LegLeft02a.xRot = 1.34f
                    this.LegRight01.zRot = 0.35f
                    this.LegRight02a.xRot = 1.13f
                    this.Hair01.xRot += 0.09f
                    this.Hair02.xRot += 0.43f
                    this.Hair03.xRot += 0.49f
                }
            } else {
                this.poseTranslateY += 0.03f * 3f
                this.Head.xRot -= 0.7f
                this.BodyMain.xRot = 0.35f
                this.Hair01.xRot += 0.5f
                this.Hair02.xRot += 0.15f
                this.Hair03.xRot += 0.0f
                this.ArmLeft01.xRot = -0.5235988f
                this.ArmLeft01.yRot = 0.0f
                this.ArmLeft01.zRot = 0.34906584f
                this.ArmRight01.xRot = -0.5235988f
                this.ArmRight01.yRot = 0.0f
                this.ArmRight01.zRot = -0.34906584f
                this.LegLeft01.xRot = -1.4486233f
                this.LegRight01.xRot = -1.4486233f
                this.LegLeft01.yRot = -0.5235988f
                this.LegLeft01.zRot = -1.3962634f
                this.LegLeft02a.xRot = 2.1816616f
                this.LegLeft02a.z = this.legLeft02aDefaultZ + (0.37f * OFFSET_SCALE)
                this.LegRight01.yRot = 0.5235988f
                this.LegRight01.zRot = 1.3962634f
                this.LegRight02a.xRot = 2.1816616f
                this.LegRight02a.z = this.legRight02aDefaultZ + (0.37f * OFFSET_SCALE)
            }
        } else if (isSitting) {
            this.isSittingPose = true
            if (hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY = 0.27f * 3f
                this.Head.xRot += 0.14f
                this.BodyMain.xRot = -0.4363f
                this.Skirt01.xRot = -0.35f
                this.Skirt02.xRot = -0.19f
                this.Skirt03.xRot = -0.24f
                this.ArmLeft01.xRot = -0.3142f
                this.ArmLeft01.zRot = 0.349f
                this.ArmLeft02.zRot = 1.15f
                this.ArmRight01.xRot = -0.4363f
                this.ArmRight01.zRot = -0.2793f
                this.ArmRight02.zRot = -1.4f
                this.LegLeft01.xRot = -1.309f
                this.LegRight01.xRot = -1.7f
                this.LegLeft01.yRot = 0.3142f
                this.LegLeft02a.xRot = 1.0472f
                this.LegRight01.yRot = -0.35f
                this.LegRight01.zRot = -0.2618f
                this.LegRight02a.xRot = 0.9f
                this.Hair01.xRot += 0.12f
                this.Hair02.xRot += 0.15f
                this.Hair03.xRot += 0.25f
            } else {
                this.poseTranslateY = if (isPassenger) RIDING_TRANSLATE_Y else SIT_TRANSLATE_Y
                this.Head.xRot += 0.1f
                this.BodyMain.xRot = -0.1f
                this.Butt.xRot = -0.4f
                this.Butt.z = this.buttDefaultZ + (0.19f * OFFSET_SCALE)
                this.Ahoke.yRot = 0.5236f
                this.Skirt01.xRot = -0.35f
                this.Skirt02.xRot = -0.19f
                this.Skirt03.xRot = -0.24f
                this.Hair01.xRot = 0.21f + headX
                this.Hair02.xRot = -0.28f + headX
                this.Hair03.xRot = -0.24f
                this.ArmLeft01.xRot = -1.18f
                this.ArmLeft01.yRot = 0.27f
                this.ArmLeft01.zRot = -0.1f
                this.ArmLeft02.zRot = 0.92f
                this.ArmRight01.xRot = -1.18f
                this.ArmRight01.yRot = -0.27f
                this.ArmRight01.zRot = 0.1f
                this.ArmRight02.zRot = -1.32f
                this.LegLeft01.xRot = -2.57f
                this.LegRight01.xRot = -2.57f
                this.LegLeft01.y = this.legLeft01DefaultY + (0.25f * OFFSET_SCALE)
                this.LegLeft01.z = this.legLeft01DefaultZ + (-0.2f * OFFSET_SCALE)
                this.LegLeft01.yRot = 0.11f
                this.LegLeft01.zRot = -0.12f
                this.LegLeft02a.xRot = 2.75f
                this.LegLeft02a.zRot = 0.02f
                this.LegLeft02a.z = this.legLeft02aDefaultZ + (0.37f * OFFSET_SCALE)
                this.LegRight01.y = this.legRight01DefaultY + (0.25f * OFFSET_SCALE)
                this.LegRight01.z = this.legRight01DefaultZ + (-0.2f * OFFSET_SCALE)
                this.LegRight01.yRot = -0.11f
                this.LegRight01.zRot = 0.12f
                this.LegRight02a.xRot = 2.75f
                this.LegRight02a.zRot = -0.02f
                this.LegRight02a.z = this.legRight02aDefaultZ + (0.37f * OFFSET_SCALE)
            }
        }

        if (entity != null && entity.attackTick > 0) {
            if (entity.attackTick > 25) {
                this.ArmLeft01.xRot = -1.3f + this.Head.xRot * 0.75f
                this.ArmLeft01.yRot = -0.2f
                this.ArmLeft01.zRot = 0.0f
                this.ArmLeft02.xRot = 0.0f
                this.ArmLeft02.yRot = 0.0f
                this.ArmLeft02.zRot = 0.0f
            }
        }

        val partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
        val customAttackAnim = if (entity != null) entity.getCustomAttackAnim(partialTick) else 0.0f
        if (customAttackAnim > 0.0f) {
            val f7 = Mth.sin(customAttackAnim * customAttackAnim * Math.PI.toFloat())
            val f8 = Mth.sin(Mth.sqrt(customAttackAnim) * Math.PI.toFloat())
            this.ArmRight01.xRot = -0.3f
            this.ArmRight01.yRot = 0.0f
            this.ArmRight01.zRot = -0.1f
            this.ArmRight01.xRot += -f8 * 80.0f * (Math.PI.toFloat() / 180f)
            this.ArmRight01.yRot += -f7 * 20.0f * (Math.PI.toFloat() / 180f)
            this.ArmRight01.zRot += -f8 * 20.0f * (Math.PI.toFloat() / 180f)
            this.ArmRight02.xRot = 0.0f
            this.ArmRight02.zRot = 0.0f
        }
    }

    private fun syncGlowParts() {
        this.GlowBodyMain.copyFrom(this.BodyMain)
        this.GlowNeck.copyFrom(this.Neck)
        this.GlowHead.copyFrom(this.Head)

        this.LegLeft02b.copyFrom(this.LegLeft02a)
        this.LegRight02b.copyFrom(this.LegRight02a)
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "isolated_hime"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelIsolatedHime")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelIsolatedHime")
        private val SIT_TRANSLATE_Y = sittingY("ModelIsolatedHime")
        private val RIDING_TRANSLATE_Y = ridingY("ModelIsolatedHime")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 105).addBox(-6.5f, -11f, -4f, 13f, 15f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(24, 84).addBox(-2f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.3f, -0.7f, -0.0524f, 0f, -0.2793f)
            )

            val Cloth02b = ArmLeft01.addOrReplaceChild(
                "Cloth02b",
                CubeListBuilder.create().texOffs(128, 85).addBox(-3f, 0f, -3.5f, 6f, 6f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.9f, -1.5f, 0f, 0f, 0f, 0.0524f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(24, 63).addBox(-5f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 11f, 2.5f)
            )

            val Cloth03b = ArmLeft02.addOrReplaceChild(
                "Cloth03b",
                CubeListBuilder.create().texOffs(128, 50).mirror()
                    .addBox(-3f, 0f, -3f, 6f, 7f, 6f, CubeDeformation(0f)),
                PartPose.offset(-2.5f, 3.5f, -2.5f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(103, 35).addBox(-2.5f, -2f, -3f, 5f, 2f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10.3f, 0.5f, 0.1047f, 0f, 0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, -0.7f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(46, 104).addBox(-7.5f, 0f, 0f, 15f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.8f, -3f)
            )

            val Hair01 = Head.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(50, 30).addBox(-7.5f, 0f, -4f, 15f, 17f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -6f, 2f, 0.2094f, 0f, 0f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(0, 38).addBox(-8f, 0f, -6f, 16f, 16f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 13.5f, 2.5f, 0.1222f, 0f, 0f)
            )

            val Hair03 = Hair02.addOrReplaceChild(
                "Hair03",
                CubeListBuilder.create().texOffs(0, 15).addBox(-7.5f, 0f, -5.5f, 15f, 15f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12.5f, -0.1f, -0.2618f, 0f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 77).addBox(-8f, -8f, -7.4f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0.1f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(106, 31).addBox(0f, -6f, -10.5f, 0f, 11f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.5f, -7f, -6f, 0.5236f, 0.6981f, 0f)
            )

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(52, 56).addBox(-8.5f, 0f, 0f, 17f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -7f)
            )

            val HatBase = Head.addOrReplaceChild(
                "HatBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -14.6f, -2f, -0.0524f, 0f, 0f)
            )

            val Hat03 = HatBase.addOrReplaceChild(
                "Hat03",
                CubeListBuilder.create().texOffs(88, 23).addBox(-8.5f, 0f, -0.5f, 17f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(10f, 5.5f, 1.3f, 0f, -0.0524f, 1.5708f)
            )

            val Hat04f = Hat03.addOrReplaceChild(
                "Hat04f",
                CubeListBuilder.create().texOffs(30, 6).addBox(-2f, -3f, 0f, 4f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.3f, -1f, 2.5f, 0f, -0.034906585f, 2.9670596f)
            )

            val Hat04g = Hat03.addOrReplaceChild(
                "Hat04g",
                CubeListBuilder.create().texOffs(30, 6).addBox(-2f, -3f, 0f, 4f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.5f, -0.6f, 2.2f, -0.0524f, -0.034906585f, -3.0718f)
            )

            val Hat04e = Hat03.addOrReplaceChild(
                "Hat04e",
                CubeListBuilder.create().texOffs(60, 2).addBox(-2f, -3f, 0f, 4f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.2f, 1.1f, 2.8f, 0.1396f, 0f, 0.034906585f)
            )

            val Hat04d = Hat03.addOrReplaceChild(
                "Hat04d",
                CubeListBuilder.create().texOffs(42, 10).addBox(-2f, -3f, -10f, 4f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.9f, -1.5f, -0.2f, 0.017453293f, 0.017453293f, 2.7925267f)
            )

            val Hat04a = Hat03.addOrReplaceChild(
                "Hat04a",
                CubeListBuilder.create().texOffs(60, 15).addBox(-2f, -3f, -10f, 4f, 3f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.5f, 0.5f, -0.5f, -0.0873f, -0.034906585f, -0.0698f)
            )

            val Hat04h = Hat03.addOrReplaceChild(
                "Hat04h",
                CubeListBuilder.create().texOffs(60, 2).addBox(-2f, -3f, 0f, 4f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.9f, 1.2f, 2.4f, 0.0524f, 0.0698f, 0.2967f)
            )

            val Hat04b = Hat03.addOrReplaceChild(
                "Hat04b",
                CubeListBuilder.create().texOffs(42, 10).addBox(-2f, -3f, -10f, 4f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.2f, -2f, -0.2f, -0.0524f, 0.0873f, -3.0718f)
            )

            val Hat04c = Hat03.addOrReplaceChild(
                "Hat04c",
                CubeListBuilder.create().texOffs(60, 15).addBox(-2f, -3f, -10f, 4f, 3f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.4f, 1f, -0.1f, 0.0698f, -0.1396f, 0.2618f)
            )

            val Hat01 = HatBase.addOrReplaceChild(
                "Hat01",
                CubeListBuilder.create().texOffs(88, 23).addBox(-8.5f, 0f, -0.5f, 17f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, -3.6f, 1f)
            )

            val Hat02d = Hat01.addOrReplaceChild(
                "Hat02d",
                CubeListBuilder.create().texOffs(42, 10).addBox(-2f, -3f, -10f, 4f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.2f, -0.7f, -0.6f, -0.0524f, -0.017453293f, 2.9670596f)
            )

            val Hat02a = Hat01.addOrReplaceChild(
                "Hat02a",
                CubeListBuilder.create().texOffs(60, 15).addBox(-2f, -3f, -10f, 4f, 3f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1.5f, -0.7f, -0.0698f, 0f, 0f)
            )

            val Hat02b = Hat01.addOrReplaceChild(
                "Hat02b",
                CubeListBuilder.create().texOffs(42, 10).addBox(-2f, -3f, -10f, 4f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.2f, -0.7f, -0.6f, -0.0524f, 0.017453293f, -2.9670596f)
            )

            val Hat02g = Hat01.addOrReplaceChild(
                "Hat02g",
                CubeListBuilder.create().texOffs(30, 6).addBox(-2f, -3f, 0f, 4f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.8f, -0.5f, 2.3f, 0.0524f, 0.0524f, 3.1067f)
            )

            val Hat02h = Hat01.addOrReplaceChild(
                "Hat02h",
                CubeListBuilder.create().texOffs(60, 2).addBox(-2f, -3f, 0f, 4f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.2f, 2.4f, 2.6f, 0.0873f, -0.0524f, -0.5236f)
            )

            val Hat02e = Hat01.addOrReplaceChild(
                "Hat02e",
                CubeListBuilder.create().texOffs(60, 15).addBox(-2f, -3f, -10f, 4f, 3f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.6f, 2f, -0.6f, 0.0524f, 0.034906585f, -0.5760f)
            )

            val Hat02c = Hat01.addOrReplaceChild(
                "Hat02c",
                CubeListBuilder.create().texOffs(60, 15).addBox(-2f, -3f, -10f, 4f, 3f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.6f, 2f, -0.6f, 0.0524f, -0.034906585f, 0.5760f)
            )

            val Hat02i = Hat01.addOrReplaceChild(
                "Hat02i",
                CubeListBuilder.create().texOffs(30, 6).addBox(-2f, -3f, 0f, 4f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.8f, -0.5f, 2.3f, 0.0524f, -0.0524f, -3.1067f)
            )

            val Hat02f = Hat01.addOrReplaceChild(
                "Hat02f",
                CubeListBuilder.create().texOffs(60, 2).addBox(-2f, -3f, 0f, 4f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1.7f, 2.6f, 0.0873f, 0f, 0f)
            )

            val Hat02j = Hat01.addOrReplaceChild(
                "Hat02j",
                CubeListBuilder.create().texOffs(60, 2).addBox(-2f, -3f, 0f, 4f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.2f, 2.4f, 2.6f, 0.0873f, 0.0524f, 0.5236f)
            )

            val Hat05 = HatBase.addOrReplaceChild(
                "Hat05",
                CubeListBuilder.create().texOffs(88, 23).addBox(-8.5f, 0f, -0.5f, 17f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-10f, 5.5f, 1.3f, 0f, 0.0524f, -1.5708f)
            )

            val Hat02b_1 = Hat05.addOrReplaceChild(
                "Hat02b_1",
                CubeListBuilder.create().texOffs(42, 10).addBox(-2f, -3f, -10f, 4f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.9f, -1.5f, -0.2f, 0.017453293f, -0.017453293f, -2.7925267f)
            )

            val Hat02e_1 = Hat05.addOrReplaceChild(
                "Hat02e_1",
                CubeListBuilder.create().texOffs(60, 15).addBox(-2f, -3f, -10f, 4f, 3f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.4f, 1f, -0.1f, -0.0698f, 0.1396f, -0.2618f)
            )

            val Hat06a = Hat05.addOrReplaceChild(
                "Hat06a",
                CubeListBuilder.create().texOffs(60, 15).addBox(-2f, -3f, -10f, 4f, 3f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.5f, 0.5f, -0.5f, -0.0873f, 0.034906585f, 0.0698f)
            )

            val Hat02g_1 = Hat05.addOrReplaceChild(
                "Hat02g_1",
                CubeListBuilder.create().texOffs(30, 6).addBox(-2f, -3f, 0f, 4f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.5f, -0.6f, 2.2f, -0.0524f, 0.034906585f, 3.0718f)
            )

            val Hat02i_1 = Hat05.addOrReplaceChild(
                "Hat02i_1",
                CubeListBuilder.create().texOffs(30, 6).addBox(-2f, -3f, 0f, 4f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.3f, -1f, 2.5f, 0f, 0.034906585f, -2.9670596f)
            )

            val Hat02h_1 = Hat05.addOrReplaceChild(
                "Hat02h_1",
                CubeListBuilder.create().texOffs(60, 2).addBox(-2f, -3f, 0f, 4f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.9f, 1.2f, 2.4f, 0.0524f, -0.0698f, -0.2967f)
            )

            val Hat02f_1 = Hat05.addOrReplaceChild(
                "Hat02f_1",
                CubeListBuilder.create().texOffs(60, 2).addBox(-2f, -3f, 0f, 4f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 1.1f, 2.8f, 0.1396f, 0f, 0.034906585f)
            )

            val Hat02d_1 = Hat05.addOrReplaceChild(
                "Hat02d_1",
                CubeListBuilder.create().texOffs(42, 10).addBox(-2f, -3f, -10f, 4f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.2f, -2f, -0.2f, -0.0524f, -0.0873f, 3.0718f)
            )

            val Cloth01a = Neck.addOrReplaceChild(
                "Cloth01a",
                CubeListBuilder.create().texOffs(51, 2).addBox(-1f, -2.5f, -1f, 2f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -7.9f, 0.7854f, 0f, 0f)
            )

            val Cloth01b2 = Cloth01a.addOrReplaceChild(
                "Cloth01b2",
                CubeListBuilder.create().texOffs(51, 0).addBox(0f, -3f, -1f, 6f, 6f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.5f, 0.3f, 0.3f, 0.0873f, 0.1745f, -0.1489f)
            )

            val Cloth01c = Cloth01a.addOrReplaceChild(
                "Cloth01c",
                CubeListBuilder.create().texOffs(0, 16).addBox(-2f, 0f, 0f, 4f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, 1.6f, -0.7f, -0.7854f, 0.1396f, 0.1745f)
            )

            val Cloth01b = Cloth01a.addOrReplaceChild(
                "Cloth01b",
                CubeListBuilder.create().texOffs(51, 0).addBox(-6f, -3f, -1f, 6f, 6f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.5f, 0.3f, 0.3f, 0.0873f, -0.1745f, 0.1396f)
            )

            val Cloth01c2 = Cloth01a.addOrReplaceChild(
                "Cloth01c2",
                CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2f, 0f, 0f, 4f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, 1.6f, -0.7f, -0.733f, -0.1396f, -0.1745f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(24, 84).mirror()
                    .addBox(-3f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, -9.3f, -0.7f, 0.2618f, 0f, 0.2793f)
            )

            val Cloth02c = ArmRight01.addOrReplaceChild(
                "Cloth02c",
                CubeListBuilder.create().texOffs(128, 85).mirror()
                    .addBox(-3f, 0f, -3.5f, 6f, 6f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.9f, -1.5f, 0f, 0f, 0f, -0.0524f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(24, 63).mirror().addBox(0f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 11f, 2.5f)
            )

            val Cloth03a = ArmRight02.addOrReplaceChild(
                "Cloth03a",
                CubeListBuilder.create().texOffs(128, 50).addBox(-3f, 0f, -3f, 6f, 7f, 6f, CubeDeformation(0f)),
                PartPose.offset(2.5f, 3.5f, -2.5f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(82, 0).addBox(-7.5f, 0f, -5.7f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, 1.3f, 0.3491f, 0f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 84).addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.8f, 5.5f, -2.6f, -0.1571f, 0f, 0.0873f)
            )

            val LegLeft02a = LegLeft01.addOrReplaceChild(
                "LegLeft02a",
                CubeListBuilder.create().texOffs(0, 63).addBox(-6f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(3f, 14f, -3f)
            )

            val LegArmor01a = LegLeft01.addOrReplaceChild(
                "LegArmor01a",
                CubeListBuilder.create().texOffs(0, 3).addBox(-3.5f, -4f, 0f, 7f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 13f, -5f, -0.2618f, 0f, 0f)
            )

            val LegArmor01b = LegArmor01a.addOrReplaceChild(
                "LegArmor01b",
                CubeListBuilder.create().texOffs(12, 0).addBox(-2.5f, 0f, 0f, 5f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0.6109f, 0f, 0f)
            )

            val LegArmor01c = LegArmor01b.addOrReplaceChild(
                "LegArmor01c",
                CubeListBuilder.create().texOffs(0, 3).addBox(-1f, -4f, 0f, 2f, 4f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, 0.2f, 0.6109f, 0f, 0f)
            )

            val LegLeft02b = LegLeft01.addOrReplaceChild(
                "LegLeft02b",
                CubeListBuilder.create().texOffs(128, 63).addBox(-6f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(3f, 14f, -3f)
            )

            val Skirt01 = Butt.addOrReplaceChild(
                "Skirt01",
                CubeListBuilder.create().texOffs(128, 0).addBox(-9f, 0f, -6.2f, 18f, 4f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, 0f, -0.0873f, 0f, 0f)
            )

            val Skirt02 = Skirt01.addOrReplaceChild(
                "Skirt02",
                CubeListBuilder.create().texOffs(128, 15).addBox(-10.5f, 0f, -6f, 21f, 4f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.7f, -1f, -0.0873f, 0f, 0f)
            )

            val Skirt03 = Skirt02.addOrReplaceChild(
                "Skirt03",
                CubeListBuilder.create().texOffs(128, 32).addBox(-11.5f, 0f, -6.5f, 23f, 4f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.5f, 0f, -0.0524f, 0f, 0f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 84).mirror().addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.8f, 5.5f, -2.6f, -0.2967f, 0f, -0.0873f)
            )

            val LegRight02b = LegRight01.addOrReplaceChild(
                "LegRight02b",
                CubeListBuilder.create().texOffs(128, 63).mirror().addBox(0f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3f, 14f, -3f)
            )

            val LegRight02a = LegRight01.addOrReplaceChild(
                "LegRight02a",
                CubeListBuilder.create().texOffs(0, 63).mirror().addBox(0f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3f, 14f, -3f)
            )

            val LegArmor02a = LegRight01.addOrReplaceChild(
                "LegArmor02a",
                CubeListBuilder.create().texOffs(10, 0).addBox(-3.5f, -4f, 0f, 7f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 13f, -5f, -0.2618f, 0f, 0f)
            )

            val LegArmor02b = LegArmor02a.addOrReplaceChild(
                "LegArmor02b",
                CubeListBuilder.create().texOffs(1, 0).addBox(-2.5f, 0f, 0f, 5f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0.6109f, 0f, 0f)
            )

            val LegArmor02c = LegArmor02b.addOrReplaceChild(
                "LegArmor02c",
                CubeListBuilder.create().texOffs(0, 3).addBox(-1f, -4f, 0f, 2f, 4f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, 0.2f, 0.6109f, 0f, 0f)
            )

            val Cloth02a = BodyMain.addOrReplaceChild(
                "Cloth02a",
                CubeListBuilder.create().texOffs(128, 99).addBox(-7f, 0f, -4f, 14f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -11.5f, -0.6f, 0.0524f, 0f, 0f)
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

            val GlowHatBase = GlowHead.addOrReplaceChild(
                "GlowHatBase",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -14.6f, -2f, -0.0524f, 0f, 0f)
            )

            val HeadH1 = GlowHatBase.addOrReplaceChild(
                "HeadH1",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-2f, -2f, -2f, 2f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8.5f, -2f, 2f, 0.1745f, 0f, 0.4363f)
            )

            val HeadH2 = HeadH1.addOrReplaceChild(
                "HeadH2",
                CubeListBuilder.create().texOffs(33, 102).addBox(-1f, -1.5f, -1.5f, 1f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.8f, 0f, 0f, 0f, 0f, 0.1222f)
            )

            val HeadH3 = HeadH2.addOrReplaceChild(
                "HeadH3",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, -1f, -1f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.7f, 0f, 0f, 0f, -0.0873f, 0.1745f)
            )

            val HeadH4 = GlowHatBase.addOrReplaceChild(
                "HeadH4",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0f, -2f, -2f, 2f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8.5f, -2f, 2f, 0.1745f, 0f, -0.4363f)
            )

            val HeadH5 = HeadH4.addOrReplaceChild(
                "HeadH5",
                CubeListBuilder.create().texOffs(33, 102).addBox(0f, -1.5f, -1.5f, 1f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.8f, 0f, 0f, 0f, 0f, -0.1222f)
            )

            val HeadH6 = HeadH5.addOrReplaceChild(
                "HeadH6",
                CubeListBuilder.create().texOffs(0, 900).addBox(0f, -1f, -1f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.7f, 0f, 0f, 0f, 0.0873f, -0.1745f)
            )

            val EquipRdL01 = GlowBodyMain.addOrReplaceChild(
                "EquipRdL01",
                CubeListBuilder.create().texOffs(128, 115).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, -6f, 5f, 1.5708f, -0.1745f, -0.7854f)
            )

            val EquipRdL02 = EquipRdL01.addOrReplaceChild(
                "EquipRdL02",
                CubeListBuilder.create().texOffs(128, 115).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -11f, -0.4363f, 0f, 0f)
            )

            val EquipRdL03 = EquipRdL02.addOrReplaceChild(
                "EquipRdL03",
                CubeListBuilder.create().texOffs(128, 115).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -11f, -0.6981f, 0f, 0f)
            )

            val EquipRdL04 = EquipRdL03.addOrReplaceChild(
                "EquipRdL04",
                CubeListBuilder.create().texOffs(128, 115).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -11f, -0.3491f, 0f, 0f)
            )

            val EquipRdL05 = EquipRdL04.addOrReplaceChild(
                "EquipRdL05",
                CubeListBuilder.create().texOffs(128, 115).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -11f, -0.2618f, 0f, 0f)
            )

            val EquipRdL06 = EquipRdL05.addOrReplaceChild(
                "EquipRdL06",
                CubeListBuilder.create().texOffs(128, 115).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -11f, -0.1745f, 0f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 256, 128)
        }
    }
}
