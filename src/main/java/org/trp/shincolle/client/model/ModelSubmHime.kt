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
import org.trp.shincolle.entity.EntitySubmHime
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelSubmHime<T : EntityShipBase>(root: ModelPart) : ShipModelHumanoidBase<T>(), IGlowableModel {
    private var isDeadPose = false
    private var isSittingPose = false
    override val poseTranslateY = 0f

    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val Butt: ModelPart
    private val ArmRight01: ModelPart
    private val ArmLeft01: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val EquipBack: ModelPart
    private val Head: ModelPart
    private val Collar01: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val Ahoke01: ModelPart
    private val Ahoke01a: ModelPart
    private val HairU01: ModelPart
    private val HairR01: ModelPart
    private val HairL01: ModelPart
    private val HairR02: ModelPart
    private val HairL02: ModelPart
    private val Hair01: ModelPart
    private val Hair02: ModelPart
    private val Hair03: ModelPart
    private val Ahoke02: ModelPart
    private val Ahoke03: ModelPart
    private val Ahoke04: ModelPart
    private val Ahoke05: ModelPart
    private val Ahoke06: ModelPart
    private val Ahoke02a: ModelPart
    private val Ahoke03a: ModelPart
    private val Ahoke04a: ModelPart
    private val Ahoke05a: ModelPart
    private val Ahoke06a: ModelPart
    private val Collar02: ModelPart
    private val Collar03: ModelPart
    private val Collar04: ModelPart
    private val Collar05: ModelPart
    private val Collar05a: ModelPart
    private val Collar05b: ModelPart
    private val LegLeft01: ModelPart
    private val Skirt01: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft02: ModelPart
    private val Skirt02: ModelPart
    private val LegRight02: ModelPart
    private val ArmRight02: ModelPart
    private val ArmLeft02: ModelPart
    private val EquipTube00: ModelPart
    private val EquipTube00_1: ModelPart
    private val EquipTube01: ModelPart
    private val EquipTube01a: ModelPart
    private val EquipTube02: ModelPart
    private val EquipTube02a: ModelPart
    private val EquipTube03: ModelPart
    private val EquipTube03a: ModelPart
    private val EquipTube04: ModelPart
    private val EquipTube04a: ModelPart
    private val EquipTube05: ModelPart
    private val EquipTube05a: ModelPart
    private val EquipTBase: ModelPart
    private val EquipT01: ModelPart
    private val EquipT02: ModelPart
    private val EquipT03: ModelPart
    private val EquipT04: ModelPart
    private val EquipT05: ModelPart
    private val EquipT06: ModelPart
    private val EquipT07: ModelPart
    private val EquipT02a: ModelPart
    private val EquipT02b: ModelPart
    private val EquipT02c: ModelPart
    private val EquipT02d: ModelPart
    private val EquipTJaw01: ModelPart
    private val EquipTJaw02: ModelPart
    private val EquipTEyeA: ModelPart
    private val EquipTEyeB: ModelPart
    private val EquipTube01_1: ModelPart
    private val EquipTube01a_1: ModelPart
    private val EquipTube02_1: ModelPart
    private val EquipTube02a_1: ModelPart
    private val EquipTube03_1: ModelPart
    private val EquipTube03a_1: ModelPart
    private val EquipTube04_1: ModelPart
    private val EquipTube04a_1: ModelPart
    private val EquipTube05_1: ModelPart
    private val EquipTube05a_1: ModelPart
    private val EquipTBase_1: ModelPart
    private val EquipT01_1: ModelPart
    private val EquipT03_1: ModelPart
    private val EquipT05_1: ModelPart
    private val EquipT06_1: ModelPart
    private val EquipT07_1: ModelPart
    private val EquipT02_1: ModelPart
    private val EquipT04_1: ModelPart
    private val EquipT02a_1: ModelPart
    private val EquipT02b_1: ModelPart
    private val EquipT02c_1: ModelPart
    private val EquipT02d_1: ModelPart
    private val EquipTJaw01_1: ModelPart
    private val EquipTJaw02_1: ModelPart
    private val EquipTEyeA_1: ModelPart
    private val EquipTEyeB_1: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val GlowEquipBase: ModelPart
    private val buttDefaultY: Float
    private val buttDefaultZ: Float
    private val skirt01DefaultY: Float
    private val skirt01DefaultZ: Float
    private val skirt02DefaultY: Float
    private val skirt02DefaultZ: Float
    private val legLeft02DefaultX: Float
    private val legLeft02DefaultY: Float
    private val legLeft02DefaultZ: Float
    private val legRight02DefaultX: Float
    private val legRight02DefaultY: Float
    private val legRight02DefaultZ: Float
    private val armLeft02DefaultX: Float
    private val armLeft02DefaultZ: Float
    private val armRight02DefaultX: Float
    private val armRight02DefaultZ: Float
    private val boobLDefaultX: Float
    private val boobRDefaultX: Float
    private val equipTBaseDefaultY: Float
    private val equipTBase1DefaultY: Float


    init {
        this.BodyMain = root.getChild("BodyMain")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.Butt = this.BodyMain.getChild("Butt")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.Skirt02 = this.Skirt01.getChild("Skirt02")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Collar01 = this.Neck.getChild("Collar01")
        this.Collar02 = this.Collar01.getChild("Collar02")
        this.Collar03 = this.Collar02.getChild("Collar03")
        this.Collar04 = this.Collar03.getChild("Collar04")
        this.Collar05 = this.Collar04.getChild("Collar05")
        this.Collar05a = this.Collar05.getChild("Collar05a")
        this.Collar05b = this.Collar05.getChild("Collar05b")
        this.Head = this.Neck.getChild("Head")
        this.Ahoke01a = this.Head.getChild("Ahoke01a")
        this.Ahoke02a = this.Ahoke01a.getChild("Ahoke02a")
        this.Ahoke03a = this.Ahoke02a.getChild("Ahoke03a")
        this.Ahoke04a = this.Ahoke03a.getChild("Ahoke04a")
        this.Ahoke05a = this.Ahoke04a.getChild("Ahoke05a")
        this.Ahoke06a = this.Ahoke05a.getChild("Ahoke06a")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.Hair03 = this.Hair02.getChild("Hair03")
        this.Hair = this.Head.getChild("Hair")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.Ahoke01 = this.Head.getChild("Ahoke01")
        this.Ahoke02 = this.Ahoke01.getChild("Ahoke02")
        this.Ahoke03 = this.Ahoke02.getChild("Ahoke03")
        this.Ahoke04 = this.Ahoke03.getChild("Ahoke04")
        this.Ahoke05 = this.Ahoke04.getChild("Ahoke05")
        this.Ahoke06 = this.Ahoke05.getChild("Ahoke06")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        this.EquipBack = this.GlowBodyMain.getChild("EquipBack")
        this.initFaceParts(this.GlowHead)
        this.GlowEquipBase = this.GlowBodyMain.getChild("GlowEquipBase")
        this.EquipTube00 = this.GlowEquipBase.getChild("EquipTube00")
        this.EquipTube01 = this.EquipTube00.getChild("EquipTube01")
        this.EquipTube02 = this.EquipTube01.getChild("EquipTube02")
        this.EquipTube03 = this.EquipTube02.getChild("EquipTube03")
        this.EquipTube04 = this.EquipTube03.getChild("EquipTube04")
        this.EquipTube05 = this.EquipTube04.getChild("EquipTube05")
        this.EquipTube05a = this.EquipTube05.getChild("EquipTube05a")
        this.EquipTBase = this.EquipTube05a.getChild("EquipTBase")
        this.EquipT01 = this.EquipTBase.getChild("EquipT01")
        this.EquipT07 = this.EquipTBase.getChild("EquipT07")
        this.EquipT02 = this.EquipTBase.getChild("EquipT02")
        this.EquipT02c = this.EquipT02.getChild("EquipT02c")
        this.EquipT02a = this.EquipT02.getChild("EquipT02a")
        this.EquipT02d = this.EquipT02.getChild("EquipT02d")
        this.EquipT02b = this.EquipT02.getChild("EquipT02b")
        this.EquipT04 = this.EquipTBase.getChild("EquipT04")
        this.EquipTEyeB = this.EquipT04.getChild("EquipTEyeB")
        this.EquipTJaw01 = this.EquipT04.getChild("EquipTJaw01")
        this.EquipTJaw02 = this.EquipT04.getChild("EquipTJaw02")
        this.EquipTEyeA = this.EquipT04.getChild("EquipTEyeA")
        this.EquipT06 = this.EquipTBase.getChild("EquipT06")
        this.EquipT03 = this.EquipTBase.getChild("EquipT03")
        this.EquipT05 = this.EquipTBase.getChild("EquipT05")
        this.EquipTube04a = this.EquipTube04.getChild("EquipTube04a")
        this.EquipTube03a = this.EquipTube03.getChild("EquipTube03a")
        this.EquipTube02a = this.EquipTube02.getChild("EquipTube02a")
        this.EquipTube01a = this.EquipTube01.getChild("EquipTube01a")
        this.EquipTube00_1 = this.GlowEquipBase.getChild("EquipTube00_1")
        this.EquipTube01_1 = this.EquipTube00_1.getChild("EquipTube01_1")
        this.EquipTube01a_1 = this.EquipTube01_1.getChild("EquipTube01a_1")
        this.EquipTube02_1 = this.EquipTube01_1.getChild("EquipTube02_1")
        this.EquipTube03_1 = this.EquipTube02_1.getChild("EquipTube03_1")
        this.EquipTube03a_1 = this.EquipTube03_1.getChild("EquipTube03a_1")
        this.EquipTube04_1 = this.EquipTube03_1.getChild("EquipTube04_1")
        this.EquipTube04a_1 = this.EquipTube04_1.getChild("EquipTube04a_1")
        this.EquipTube05_1 = this.EquipTube04_1.getChild("EquipTube05_1")
        this.EquipTube05a_1 = this.EquipTube05_1.getChild("EquipTube05a_1")
        this.EquipTBase_1 = this.EquipTube05a_1.getChild("EquipTBase_1")
        this.EquipT05_1 = this.EquipTBase_1.getChild("EquipT05_1")
        this.EquipT04_1 = this.EquipTBase_1.getChild("EquipT04_1")
        this.EquipTJaw01_1 = this.EquipT04_1.getChild("EquipTJaw01_1")
        this.EquipTJaw02_1 = this.EquipT04_1.getChild("EquipTJaw02_1")
        this.EquipTEyeB_1 = this.EquipT04_1.getChild("EquipTEyeB_1")
        this.EquipTEyeA_1 = this.EquipT04_1.getChild("EquipTEyeA_1")
        this.EquipT07_1 = this.EquipTBase_1.getChild("EquipT07_1")
        this.EquipT01_1 = this.EquipTBase_1.getChild("EquipT01_1")
        this.EquipT02_1 = this.EquipTBase_1.getChild("EquipT02_1")
        this.EquipT02a_1 = this.EquipT02_1.getChild("EquipT02a_1")
        this.EquipT02b_1 = this.EquipT02_1.getChild("EquipT02b_1")
        this.EquipT02c_1 = this.EquipT02_1.getChild("EquipT02c_1")
        this.EquipT02d_1 = this.EquipT02_1.getChild("EquipT02d_1")
        this.EquipT03_1 = this.EquipTBase_1.getChild("EquipT03_1")
        this.EquipT06_1 = this.EquipTBase_1.getChild("EquipT06_1")
        this.EquipTube02a_1 = this.EquipTube02_1.getChild("EquipTube02a_1")
        this.buttDefaultY = this.Butt.y
        this.buttDefaultZ = this.Butt.z
        this.skirt01DefaultY = this.Skirt01.y
        this.skirt01DefaultZ = this.Skirt01.z
        this.skirt02DefaultY = this.Skirt02.y
        this.skirt02DefaultZ = this.Skirt02.z
        this.legLeft02DefaultX = this.LegLeft02.x
        this.legLeft02DefaultY = this.LegLeft02.y
        this.legLeft02DefaultZ = this.LegLeft02.z
        this.legRight02DefaultX = this.LegRight02.x
        this.legRight02DefaultY = this.LegRight02.y
        this.legRight02DefaultZ = this.LegRight02.z
        this.armLeft02DefaultX = this.ArmLeft02.x
        this.armLeft02DefaultZ = this.ArmLeft02.z
        this.armRight02DefaultX = this.ArmRight02.x
        this.armRight02DefaultZ = this.ArmRight02.z
        this.boobLDefaultX = this.BoobL.x
        this.boobRDefaultX = this.BoobR.x
        this.equipTBaseDefaultY = this.EquipTBase.y
        this.equipTBase1DefaultY = this.EquipTBase_1.y
    }

    override fun setupAnim(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        this.resetOffsets()
        this.applyEquipVisibility(entity)
        applyFaceAndMouth(entity)

        val inDeadPose = entity != null && entity.isInDeadPose

        if (inDeadPose) {
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
        this.Skirt01.y = this.skirt01DefaultY
        this.Skirt01.z = this.skirt01DefaultZ
        this.Skirt02.y = this.skirt02DefaultY
        this.Skirt02.z = this.skirt02DefaultZ
        this.LegLeft02.x = this.legLeft02DefaultX
        this.LegLeft02.y = this.legLeft02DefaultY
        this.LegLeft02.z = this.legLeft02DefaultZ
        this.LegRight02.x = this.legRight02DefaultX
        this.LegRight02.y = this.legRight02DefaultY
        this.LegRight02.z = this.legRight02DefaultZ
        this.ArmLeft02.x = this.armLeft02DefaultX
        this.ArmLeft02.z = this.armLeft02DefaultZ
        this.ArmRight02.x = this.armRight02DefaultX
        this.ArmRight02.z = this.armRight02DefaultZ
        this.BoobL.x = this.boobLDefaultX
        this.BoobR.x = this.boobRDefaultX
        this.EquipTBase.y = this.equipTBaseDefaultY
        this.EquipTBase_1.y = this.equipTBase1DefaultY
    }

    private fun applyEquipVisibility(entity: T?) {
        this.Collar01.visible = entity!!.getEquipFlag(EntitySubmHime.EQUIP_COLLAR)
        this.EquipTBase.visible = true
        this.EquipTBase_1.visible = true
        this.GlowEquipBase.visible = true
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        this.Head.xRot = -0.15f
        this.Head.yRot = 0.0f
        this.Head.zRot = 0.0f
        this.BoobL.xRot = -0.76f
        this.BoobR.xRot = -0.76f
        this.BodyMain.xRot = 1.6f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = 1.2f
        this.Butt.y = this.buttDefaultY + (-0.2f * OFFSET_SCALE)
        this.Butt.z = this.buttDefaultZ + (-0.14f * OFFSET_SCALE)
        this.Skirt01.xRot = -0.94f
        this.Skirt01.y = this.skirt01DefaultY + (0.09f * OFFSET_SCALE)
        this.Skirt01.z = this.skirt01DefaultZ + (-0.03f * OFFSET_SCALE)
        this.Skirt02.xRot = -0.3f

        this.Hair01.xRot = 0.35f
        this.Hair01.zRot = 0.0f
        this.Hair02.xRot = -0.2f
        this.Hair02.zRot = 0.0f
        this.Hair03.xRot = -0.35f
        this.Hair03.zRot = 0.0f
        this.HairL01.xRot = -0.14f
        this.HairL02.xRot = 0.17f
        this.HairR01.xRot = -0.14f
        this.HairR02.xRot = 0.17f

        this.ArmLeft01.xRot = -2.9f
        this.ArmLeft01.yRot = -0.6981f
        this.ArmLeft01.zRot = 0.08f
        this.ArmLeft02.xRot = 0.0f
        this.ArmLeft02.yRot = 0.0f
        this.ArmLeft02.zRot = 0.0f
        this.ArmRight01.xRot = -2.9f
        this.ArmRight01.yRot = 0.6981f
        this.ArmRight01.zRot = -0.08f
        this.ArmRight02.xRot = 0.0f
        this.ArmRight02.yRot = 0.0f
        this.ArmRight02.zRot = 0.0f

        this.LegLeft01.xRot = -1.9f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.05f
        this.LegLeft02.xRot = 0.64f
        this.LegRight01.xRot = -1.9f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.05f
        this.LegRight02.xRot = 0.64f

        this.GlowEquipBase.visible = false
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
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.4f + limbSwing * 0.5f)
        val angleX2 = Mth.cos(ageInTicks * 0.08f + 0.8f + limbSwing * 0.5f)
        val angleX3 = Mth.cos(ageInTicks * 0.08f + 1.2f + limbSwing * 0.5f)
        val angleX4 = Mth.cos(ageInTicks * 0.08f + 1.6f + limbSwing * 0.5f)
        val angleX5 = Mth.cos(ageInTicks * 0.08f + 2.0f + limbSwing * 0.5f)
        val angleX6 = Mth.cos(ageInTicks * 0.08f + 2.4f + limbSwing * 0.5f)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount * 0.5f
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount * 0.5f

        this.Head.xRot = headPitch * (Math.PI.toFloat() / 180f)
        this.Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)
        this.Head.zRot = 0.0f

        val headX = this.Head.xRot * -0.5f
        val headZ = this.Head.zRot * -0.5f

        this.Ahoke01.xRot = angleX1 * 0.07f - 2.01f
        this.Ahoke01.yRot = 0.52f
        this.Ahoke01.zRot = 0.0f
        this.Ahoke02.xRot = -angleX2 * 0.09f + 1.04f
        this.Ahoke03.xRot = angleX3 * 0.15f + 0.78f
        this.Ahoke04.xRot = -angleX4 * 0.1f + 0.44f
        this.Ahoke05.xRot = -angleX5 * 0.15f - 0.17f
        this.Ahoke06.xRot = angleX6 * 0.18f - 0.31f

        this.Ahoke01a.xRot = angleX1 * 0.07f - 2.27f
        this.Ahoke01a.yRot = -2.62f
        this.Ahoke01a.zRot = 0.0f
        this.Ahoke02a.xRot = -angleX2 * 0.09f + 0.79f
        this.Ahoke03a.xRot = angleX3 * 0.15f + 1.05f
        this.Ahoke04a.xRot = -angleX4 * 0.1f + 0.41f
        this.Ahoke05a.xRot = -angleX5 * 0.15f - 0.3f
        this.Ahoke06a.xRot = angleX6 * 0.18f - 0.25f

        this.BodyMain.xRot = -0.1047f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f

        this.Butt.xRot = 0.35f

        this.BoobL.xRot = angleX * 0.06f - 0.76f
        this.BoobL.yRot = -0.087f
        this.BoobL.zRot = -0.07f
        this.BoobR.xRot = angleX * 0.06f - 0.76f
        this.BoobR.yRot = 0.087f
        this.BoobR.zRot = 0.07f

        val showCollar = entity!!.getEquipFlag(EntitySubmHime.EQUIP_COLLAR)
        val showTails = entity.getEquipFlag(EntitySubmHime.EQUIP_TAILS)
        if (showCollar) {
            this.BoobL.x = this.boobLDefaultX
            this.BoobR.x = this.boobRDefaultX
        } else {
            this.BoobL.x = this.boobLDefaultX + (-0.05f * OFFSET_SCALE)
            this.BoobR.x = this.boobRDefaultX + (0.05f * OFFSET_SCALE)
        }

        this.Collar01.xRot = 0.035f
        this.Collar03.xRot = angleX * 0.08f + 0.26f
        this.Collar04.xRot = -angleX * 0.08f + 0.45f

        this.Skirt01.xRot = -0.087f
        this.Skirt02.xRot = -0.087f

        this.Hair01.xRot = angleX * 0.03f + 0.26f + headX
        this.Hair01.zRot = headZ
        this.Hair02.xRot = -angleX1 * 0.04f - 0.087f + headX
        this.Hair02.zRot = headZ
        this.Hair03.xRot = -angleX2 * 0.07f - 0.052f
        this.Hair03.zRot = headZ

        this.HairL01.xRot = angleX * 0.02f + headX - 0.19f
        this.HairL01.zRot = headZ - 0.087f
        this.HairL02.xRot = -angleX1 * 0.04f + headX + 0.17f
        this.HairL02.zRot = headZ + 0.087f

        this.HairR01.xRot = angleX * 0.02f + headX - 0.19f
        this.HairR01.zRot = headZ + 0.087f
        this.HairR02.xRot = -angleX1 * 0.04f + headX + 0.17f
        this.HairR02.zRot = headZ - 0.052f

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

        this.LegLeft01.xRot = angleAdd1 * 0.6f - 0.3f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.087f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.yRot = 0.0f
        this.LegLeft02.zRot = 0.0f

        this.LegRight01.xRot = angleAdd2 * 0.6f - 0.2f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.087f
        this.LegRight02.xRot = 0.0f
        this.LegRight02.yRot = 0.0f
        this.LegRight02.zRot = 0.0f

        if (showTails) {
            this.EquipTBase.visible = false
            this.EquipTBase_1.visible = false
            this.GlowEquipBase.xRot = 0.3f
            this.EquipTube00.xRot = 0.2618f
            this.EquipTube00.yRot = Mth.cos(-ageInTicks * 0.1f + 0.7f) * 0.1f + 0.61f
            this.EquipTube00.zRot = this.EquipTube00.yRot * 0.125f
            this.EquipTube01.xRot = 0.35f
            this.EquipTube01.yRot = Mth.cos(-ageInTicks * 0.1f + 1.4f) * 0.125f
            this.EquipTube01.zRot = this.EquipTube01.yRot * 0.125f
            this.EquipTube02.xRot = 0.5235f
            this.EquipTube02.yRot = Mth.cos(-ageInTicks * 0.1f + 2.1f) * 0.15f
            this.EquipTube02.zRot = this.EquipTube02.yRot * 0.125f
            this.EquipTube03.xRot = 0.61f
            this.EquipTube03.yRot = Mth.cos(-ageInTicks * 0.1f + 2.8f) * 0.175f
            this.EquipTube03.zRot = this.EquipTube03.yRot * 0.125f
            this.EquipTube04.xRot = 0.6981f
            this.EquipTube04.yRot = Mth.cos(-ageInTicks * 0.1f + 3.5f) * 0.2f
            this.EquipTube04.zRot = this.EquipTube04.yRot * 0.125f
            this.EquipTube05.xRot = 0.61f
            this.EquipTube05.yRot = Mth.cos(-ageInTicks * 0.1f + 4.2f) * 0.175f
            this.EquipTube05.zRot = this.EquipTube05.yRot * 0.125f

            this.EquipTube00_1.xRot = this.EquipTube00.xRot
            this.EquipTube00_1.yRot = -this.EquipTube00.yRot
            this.EquipTube00_1.zRot = this.EquipTube00.zRot
            this.EquipTube01_1.xRot = this.EquipTube01.xRot
            this.EquipTube01_1.yRot = this.EquipTube01.yRot
            this.EquipTube01_1.zRot = this.EquipTube01.zRot
            this.EquipTube02_1.xRot = this.EquipTube02.xRot
            this.EquipTube02_1.yRot = this.EquipTube02.yRot
            this.EquipTube02_1.zRot = this.EquipTube02.zRot
            this.EquipTube03_1.xRot = this.EquipTube03.xRot
            this.EquipTube03_1.yRot = this.EquipTube03.yRot
            this.EquipTube03_1.zRot = this.EquipTube03.zRot
            this.EquipTube04_1.xRot = this.EquipTube04.xRot
            this.EquipTube04_1.yRot = this.EquipTube04.yRot
            this.EquipTube04_1.zRot = this.EquipTube04.zRot
            this.EquipTube05_1.xRot = this.EquipTube05.xRot
            this.EquipTube05_1.yRot = this.EquipTube05.yRot
            this.EquipTube05_1.zRot = this.EquipTube05.zRot
        } else {
            this.EquipTBase.visible = true
            this.EquipTBase_1.visible = true
            this.GlowEquipBase.xRot = 0.0f
        }
    }

    private fun applySpecialPoseAdjustments(entity: T?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        val angleX = Mth.cos(ageInTicks * 0.08f)
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.4f + limbSwing * 0.5f)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount * 0.5f
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount * 0.5f
        var addk1 = angleAdd1 * 0.6f - 0.3f
        var addk2 = angleAdd2 * 0.6f - 0.2f
        var addHL1 = 0.0f
        var addHR1 = 0.0f
        var addHL2 = 0.0f
        var addHR2 = 0.0f
        val isPassenger = entity!!.isPassenger()
        val isCrouching = entity.isCrouching()
        val isSprinting = if (entity != null) entity.isSprinting else limbSwingAmount > 0.9f
        val isSitting = entity.isInSittingPose || (isPassenger && entity.getVehicle() !is EntityMountBase)
        val showTails = entity.getEquipFlag(EntitySubmHime.EQUIP_TAILS)

        if (isSprinting) {
            if (isPassenger) {
                if (limbSwingAmount > 0.5f) {
                    this.Head.xRot += 0.4f
                    this.Hair01.xRot += 0.1f
                    this.Hair02.xRot -= 0.2f
                    this.Hair03.xRot -= 0.2f
                }
            } else {
                this.poseTranslateY += 0.06f
                this.Head.xRot -= 1.1f
                this.Hair01.xRot += 0.6f
                this.Hair02.xRot += 0.5f
                this.Hair03.xRot += 0.2f
                this.Ahoke01.xRot += 0.38f
                this.Ahoke01.yRot = 0.7f
                this.Ahoke01.zRot = 0.4f
                this.Ahoke01a.yRot = -2.5f
                this.Ahoke01a.zRot = -0.2f
            }
            this.BodyMain.xRot = 1.2566f
            this.BoobL.xRot = angleAdd2 * 0.1f - 0.83f
            this.BoobL.zRot = -0.07f
            this.BoobR.xRot = angleAdd2 * 0.1f - 0.83f
            this.BoobR.zRot = 0.07f
            this.Collar03.xRot += angleAdd2 * 0.1f
            this.Collar04.xRot += angleAdd2 * 0.1f
            this.ArmLeft01.xRot = -2.7f
            this.ArmLeft01.zRot = -0.22f
            this.ArmRight01.xRot = -2.7f
            this.ArmRight01.zRot = 0.22f
            this.LegLeft01.zRot = 0.05f
            this.LegRight01.zRot = -0.05f
            if (showTails) {
                this.GlowEquipBase.xRot = 0.3f
                this.EquipTube00.xRot = Mth.cos(-ageInTicks * 0.4f + 0.7f) * 0.1f + 0.4f
                this.EquipTube00.yRot = Mth.cos(-ageInTicks * 0.4f + 0.7f) * 0.1f + 0.9f
                this.EquipTube00.zRot = this.EquipTube00.yRot * 0.125f
                this.EquipTube01.xRot = Mth.cos(-ageInTicks * 0.4f + 1.4f) * 0.125f
                this.EquipTube01.yRot = Mth.cos(-ageInTicks * 0.4f + 1.4f) * 0.125f
                this.EquipTube01.zRot = this.EquipTube01.yRot * 0.125f
                this.EquipTube02.xRot = Mth.cos(-ageInTicks * 0.4f + 2.1f) * 0.15f
                this.EquipTube02.yRot = Mth.cos(-ageInTicks * 0.4f + 2.1f) * 0.15f
                this.EquipTube02.zRot = this.EquipTube02.yRot * 0.125f
                this.EquipTube03.xRot = Mth.cos(-ageInTicks * 0.4f + 2.8f) * 0.175f
                this.EquipTube03.yRot = Mth.cos(-ageInTicks * 0.4f + 2.8f) * 0.175f
                this.EquipTube03.zRot = this.EquipTube03.yRot * 0.125f
                this.EquipTube04.xRot = Mth.cos(-ageInTicks * 0.4f + 3.5f) * 0.2f
                this.EquipTube04.yRot = Mth.cos(-ageInTicks * 0.4f + 3.5f) * 0.2f
                this.EquipTube04.zRot = this.EquipTube04.yRot * 0.125f
                this.EquipTube05.xRot = Mth.cos(-ageInTicks * 0.4f + 4.2f) * 0.175f
                this.EquipTube05.yRot = Mth.cos(-ageInTicks * 0.4f + 4.2f) * 0.175f
                this.EquipTube05.zRot = this.EquipTube05.yRot * 0.125f
                this.EquipTube00_1.xRot = this.EquipTube00.xRot
                this.EquipTube00_1.yRot = -this.EquipTube00.yRot
                this.EquipTube00_1.zRot = -this.EquipTube00.zRot
                this.EquipTube01_1.xRot = this.EquipTube01.xRot
                this.EquipTube01_1.yRot = this.EquipTube01.yRot
                this.EquipTube01_1.zRot = this.EquipTube01.zRot
                this.EquipTube02_1.xRot = this.EquipTube02.xRot
                this.EquipTube02_1.yRot = this.EquipTube02.yRot
                this.EquipTube02_1.zRot = this.EquipTube02.zRot
                this.EquipTube03_1.xRot = this.EquipTube03.xRot
                this.EquipTube03_1.yRot = this.EquipTube03.yRot
                this.EquipTube03_1.zRot = this.EquipTube03.zRot
                this.EquipTube04_1.xRot = this.EquipTube04.xRot
                this.EquipTube04_1.yRot = this.EquipTube04.yRot
                this.EquipTube04_1.zRot = this.EquipTube04.zRot
                this.EquipTube05_1.xRot = this.EquipTube05.xRot
                this.EquipTube05_1.yRot = this.EquipTube05.yRot
                this.EquipTube05_1.zRot = this.EquipTube05.zRot
            }
        }

        if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            this.Head.xRot -= 0.6283f
            this.BodyMain.xRot = 0.8727f
            this.Skirt01.xRot = -0.34f
            this.Skirt01.y = this.skirt01DefaultY + (-0.2f * OFFSET_SCALE)
            this.Skirt01.z = this.skirt01DefaultZ + (0.03f * OFFSET_SCALE)
            this.Skirt02.xRot = -0.27f
            this.Collar01.xRot -= 0.35f
            this.Collar03.xRot -= 0.3f
            this.Collar04.xRot -= 0.35f
            this.BoobL.xRot -= 0.2f
            this.BoobL.zRot = -0.04f
            this.BoobR.xRot -= 0.2f
            this.BoobR.zRot = 0.04f
            this.ArmLeft01.xRot = -0.35f
            this.ArmLeft01.zRot = 0.2618f
            this.ArmRight01.xRot = -0.35f
            this.ArmRight01.zRot = -0.2618f
            addk1 -= 0.94f
            addk2 -= 0.94f
            this.LegLeft01.zRot = 0.2f
            this.LegRight01.zRot = -0.2f
            this.Hair01.xRot = this.Hair01.xRot * 0.5f + 0.4f
            this.Hair02.xRot = this.Hair02.xRot * 0.75f + 0.25f
            this.Hair03.xRot -= 0.1f
            this.GlowEquipBase.xRot = -0.2f
        }

        if (isSitting && !isPassenger) {
            if ((entity.tickCount and 0x1FF) > 256) {
                this.poseTranslateY += -angleX * 0.05f + 0.1f
                this.Head.xRot *= 0.5f
                this.Head.yRot *= 0.75f
                this.Head.xRot += 0.5f
                this.BodyMain.xRot = 1.6f
                this.Skirt01.xRot = -0.33f
                this.Skirt01.y = this.skirt01DefaultY + (-0.23f * OFFSET_SCALE)
                this.Skirt02.xRot = -0.12f
                this.Skirt02.y = this.skirt02DefaultY + (-0.16f * OFFSET_SCALE)
                this.Ahoke01.xRot += 0.38f
                this.Ahoke01.yRot = 0.8f
                this.Ahoke01.zRot = 0.4f
                this.Hair01.xRot -= 0.2f
                this.Hair02.xRot -= 0.25f
                this.Hair03.xRot -= 0.3f
                this.ArmLeft01.xRot = -1.5f
                this.ArmLeft01.zRot = -2.3f
                this.ArmRight01.xRot = -1.5f
                this.ArmRight01.zRot = 2.3f
                addk1 = -1.8f
                addk2 = -1.8f
                this.LegLeft01.yRot = -0.1f - angleX * 0.02f
                this.LegRight01.yRot = 0.1f + angleX * 0.02f
            } else if (hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY += 0.52f * 3
                this.Head.xRot = 0.4f
                this.Skirt01.xRot = -0.64f
                this.Skirt01.y = this.skirt01DefaultY + (-0.17f * OFFSET_SCALE)
                this.Skirt01.z = this.skirt01DefaultZ
                this.Skirt02.xRot = 0.29f
                this.Skirt02.y = this.skirt02DefaultY + (-0.04f * OFFSET_SCALE)
                this.Skirt02.z = this.skirt02DefaultZ + (0.02f * OFFSET_SCALE)
                this.Hair01.xRot -= 0.2f
                this.Hair02.xRot -= 0.15f
                this.Hair03.xRot -= 0.1f
                this.Ahoke01.xRot -= 0.1f
                this.ArmLeft01.xRot = 0.4f
                this.ArmLeft01.yRot = -2.9670596f
                this.ArmLeft01.zRot = -2.62f
                this.ArmLeft02.xRot = 0.0f
                this.ArmLeft02.yRot = 0.0f
                this.ArmLeft02.zRot = 1.0f
                this.ArmRight01.xRot = 0.5235988f
                this.ArmRight01.yRot = 2.9670596f
                this.ArmRight01.zRot = 2.62f
                this.ArmRight02.xRot = 0.0f
                this.ArmRight02.yRot = 0.0f
                this.ArmRight02.zRot = -1.0f
                addk1 = -2.4130921f
                addk2 = -2.268928f
                this.LegLeft01.yRot = 0.0f
                this.LegLeft01.zRot = -0.27314404f
                this.LegLeft02.xRot = 1.4570009f
                this.LegLeft02.yRot = 0.0f
                this.LegLeft02.zRot = 0.0f
                this.LegRight01.yRot = 0.0f
                this.LegRight01.zRot = 0.22759093f
                this.LegRight02.xRot = 1.0471976f
                this.LegRight02.yRot = 0.0f
                this.LegRight02.zRot = 0.0f
            } else {
                this.poseTranslateY += SITTING_TRANSLATE_Y
                this.Head.xRot -= 0.7f
                this.BodyMain.xRot = 0.35f
                this.Hair01.xRot += 0.3f
                this.Hair02.xRot += 0.3f
                this.Hair03.xRot += 0.3f
                this.Skirt01.xRot = -0.32f
                this.Skirt01.y = this.skirt01DefaultY + (-0.05f * OFFSET_SCALE)
                this.Skirt02.xRot = -0.21f
                this.Collar01.xRot += 0.1f
                this.Collar03.xRot += 0.1f
                this.ArmLeft01.xRot = -0.5235988f
                this.ArmLeft01.yRot = 0.0f
                this.ArmLeft01.zRot = 0.34906584f
                this.ArmLeft02.xRot = 0.0f
                this.ArmLeft02.zRot = 0.0f
                this.ArmRight01.xRot = -0.5235988f
                this.ArmRight01.yRot = 0.0f
                this.ArmRight01.zRot = -0.34906584f
                this.ArmRight02.xRot = 0.0f
                this.ArmRight02.zRot = 0.0f
                addk1 = -1.4486233f
                addk2 = -1.4486233f
                this.LegLeft01.yRot = -0.5235988f
                this.LegLeft01.zRot = -1.3962634f
                this.LegLeft02.xRot = 2.1816616f
                this.LegLeft02.z = this.legLeft02DefaultZ + (0.37f * OFFSET_SCALE)
                this.LegRight01.yRot = 0.5235988f
                this.LegRight01.zRot = 1.3962634f
                this.LegRight02.xRot = 2.1816616f
                this.LegRight02.z = this.legRight02DefaultZ + (0.37f * OFFSET_SCALE)
            }
        } else if (isPassenger && entity.getVehicle() is EntityMountBase) {
            this.isSittingPose = true
            if (isSitting) {
                this.poseTranslateY += 0.4f
                this.Head.xRot -= 0.7f
                this.BodyMain.xRot = 0.35f
                this.Hair01.xRot += 0.3f
                this.Hair02.xRot += 0.3f
                this.Hair03.xRot += 0.3f
                this.Skirt01.xRot = -0.32f
                this.Skirt01.y = this.skirt01DefaultY + (-0.05f * OFFSET_SCALE)
                this.Skirt02.xRot = -0.21f
                this.Collar01.xRot += 0.1f
                this.Collar03.xRot += 0.1f
                this.ArmLeft01.xRot = -0.8f
                this.ArmLeft01.yRot = 0.0f
                this.ArmLeft01.zRot = -0.2f
                this.ArmRight01.xRot = -0.8f
                this.ArmRight01.yRot = 0.0f
                this.ArmRight01.zRot = 0.2f
                addk1 = -1.4486f
                addk2 = -1.4486f
                this.LegLeft01.yRot = -0.5236f
                this.LegLeft01.zRot = -0.2f
                this.LegLeft02.xRot = 0.8f
                this.LegRight01.yRot = 0.5236f
                this.LegRight01.zRot = 0.2f
                this.LegRight02.xRot = 0.8f
            } else {
                this.poseTranslateY += RIDING_TRANSLATE_Y
                this.Head.xRot *= 0.5f
                this.Head.yRot *= 0.75f
                this.Head.xRot -= 1.0f
                this.BodyMain.xRot = 1.0f
                this.Skirt01.xRot = -0.33f
                this.Skirt01.y = this.skirt01DefaultY + (-0.23f * OFFSET_SCALE)
                this.Skirt02.xRot = -0.12f
                this.Skirt02.y = this.skirt02DefaultY + (-0.16f * OFFSET_SCALE)
                this.Collar01.xRot -= 0.5f
                this.Collar03.xRot -= 0.5f
                this.Ahoke01.xRot += 0.38f
                this.Ahoke01.yRot = 0.8f
                this.Ahoke01.zRot = 0.4f
                this.Hair01.xRot += 0.5f
                this.Hair02.xRot += 0.65f
                this.Hair03.xRot += 0.5f
                addHL1 = -0.6f
                addHR1 = -0.6f
                addHL2 = -0.5f
                addHR2 = -0.5f
                this.ArmLeft01.xRot = -1.4f
                this.ArmLeft01.yRot = -0.0f
                this.ArmRight01.xRot = -1.4f
                this.ArmRight01.yRot = 0.0f
                addk1 = -1.7f
                addk2 = -1.7f
                this.LegLeft01.yRot = -0.2f
                this.LegRight01.yRot = 0.2f
            }
        } else if (isPassenger) {
            this.poseTranslateY += RIDING_TRANSLATE_Y
            this.Head.xRot *= 0.5f
            this.Head.yRot *= 0.75f
            this.Head.xRot -= 1.0f
            this.BodyMain.xRot = 1.0f
            this.Skirt01.xRot = -0.33f
            this.Skirt01.y = this.skirt01DefaultY + (-0.23f * OFFSET_SCALE)
            this.Skirt02.xRot = -0.12f
            this.Skirt02.y = this.skirt02DefaultY + (-0.16f * OFFSET_SCALE)
            this.Collar01.xRot -= 0.5f
            this.Collar03.xRot -= 0.5f
            this.Collar04.xRot -= 0.5f
            this.Ahoke01.xRot += 0.38f
            this.Ahoke01.yRot = 0.8f
            this.Ahoke01.zRot = 0.4f
            this.Hair01.xRot += 0.5f
            this.Hair02.xRot += 0.65f
            this.Hair03.xRot += 0.5f
            addHL1 = -0.6f
            addHR1 = -0.6f
            addHL2 = -0.5f
            addHR2 = -0.5f
            this.ArmLeft01.xRot = -1.4f
            this.ArmRight01.xRot = -1.4f
            addk1 = -1.7f
            addk2 = -1.7f
            this.LegLeft01.yRot = -0.2f
            this.LegRight01.yRot = 0.2f
        }

        if (entity != null && entity.attackTick > 14) {
            if (isPassenger) {
                this.poseTranslateY += 0.02f
                this.Head.xRot *= 0.5f
                this.Head.yRot *= 0.75f
                this.Head.xRot -= 0.5f
                this.BodyMain.xRot = 1.1f
                this.Collar01.xRot -= 0.2f
                this.Ahoke01.xRot += 0.38f
                this.Ahoke01.yRot = 0.8f
                this.Ahoke01.zRot = 0.4f
                this.Hair01.xRot += 0.2f
                this.Hair02.xRot += -0.1f
                this.Hair03.xRot += -0.1f
                addHL1 = -0.6f
                addHR1 = -0.6f
                addHL2 = -0.5f
                addHR2 = -0.5f
                addk1 = -1.8f
                addk2 = -1.8f
                this.LegLeft01.yRot = -0.1f
                this.LegRight01.yRot = 0.1f
                this.GlowEquipBase.xRot = 0.5f
            } else {
                this.poseTranslateY += 0.22f
                this.Head.xRot *= 0.5f
                this.Head.yRot *= 0.75f
                this.Head.xRot -= 1.6f
                this.BodyMain.xRot = 1.6f
                this.Collar01.xRot -= 0.5f
                this.Collar03.xRot -= 0.5f
                this.Collar04.xRot -= 0.5f
                this.Ahoke01.xRot += 0.38f
                this.Ahoke01.yRot = 0.8f
                this.Ahoke01.zRot = 0.4f
                this.Hair01.xRot += 1.0f
                this.Hair02.xRot += 0.6f
                this.Hair03.xRot += 0.7f
                addHL1 = -0.6f
                addHR1 = -0.6f
                addHL2 = -0.5f
                addHR2 = -0.5f
                addk1 = -2.2f
                addk2 = -2.2f
                this.LegLeft01.yRot = -0.1f
                this.LegRight01.yRot = 0.1f
                this.GlowEquipBase.xRot = 0.0f
            }
            this.Skirt01.xRot = -0.33f
            this.Skirt01.y = this.skirt01DefaultY + (-0.23f * OFFSET_SCALE)
            this.Skirt02.xRot = -0.12f
            this.Skirt02.y = this.skirt02DefaultY + (-0.16f * OFFSET_SCALE)
            this.ArmLeft01.xRot = -1.6f
            this.ArmLeft01.yRot = -0.2f
            this.ArmRight01.xRot = -1.2f
            this.ArmRight01.yRot = 1.2f
        }

        setTorpedo(entity.attackTick, showTails)

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

        val headX = this.Head.xRot * -0.5f
        this.HairL01.xRot = angleX * 0.02f + headX - 0.19f + addHL1
        this.HairL02.xRot = -angleX1 * 0.04f + headX + 0.17f + addHL2
        this.HairR01.xRot = angleX * 0.02f + headX - 0.19f + addHR1
        this.HairR02.xRot = -angleX1 * 0.04f + headX + 0.17f + addHR2
        val headZ = this.Head.zRot * -0.5f
        this.Hair01.zRot = headZ
        this.Hair02.zRot = headZ
        this.Hair03.zRot = headZ
        this.HairL01.zRot = headZ - 0.087f
        this.HairL02.zRot = headZ + 0.087f
        this.HairR01.zRot = headZ + 0.087f
        this.HairR02.zRot = headZ - 0.052f
        this.LegLeft01.xRot = addk1
        this.LegRight01.xRot = addk2
    }

    private fun setTorpedo(attackTime: Int, showTails: Boolean) {
        if (attackTime <= 14) {
            this.GlowEquipBase.visible = showTails
            this.EquipTBase.visible = false
            this.EquipTBase_1.visible = false
            return
        }
        this.EquipTube00.xRot = 0.2618f
        this.EquipTube00.yRot = 0.61f
        this.EquipTube00.zRot = 0.0f
        this.EquipTube01.xRot = 0.35f
        this.EquipTube01.yRot = 0.0f
        this.EquipTube01.zRot = 0.0f
        this.EquipTube02.xRot = 0.5235f
        this.EquipTube02.yRot = 0.0f
        this.EquipTube02.zRot = 0.0f
        this.EquipTube03.xRot = 0.61f
        this.EquipTube03.yRot = 0.0f
        this.EquipTube03.zRot = 0.0f
        this.EquipTube04.xRot = 0.6981f
        this.EquipTube04.yRot = 0.0f
        this.EquipTube04.zRot = 0.0f
        this.EquipTube05.xRot = 0.61f
        this.EquipTube05.yRot = 0.0f
        this.EquipTube05.zRot = 0.0f
        this.EquipTube00_1.xRot = 0.2618f
        this.EquipTube00_1.yRot = -0.61f
        this.EquipTube00_1.zRot = 0.0f
        this.EquipTube01_1.xRot = 0.35f
        this.EquipTube01_1.yRot = 0.0f
        this.EquipTube01_1.zRot = 0.0f
        this.EquipTube02_1.xRot = 0.5235f
        this.EquipTube02_1.yRot = 0.0f
        this.EquipTube02_1.zRot = 0.0f
        this.EquipTube03_1.xRot = 0.61f
        this.EquipTube03_1.yRot = 0.0f
        this.EquipTube03_1.zRot = 0.0f
        this.EquipTube04_1.xRot = 0.6981f
        this.EquipTube04_1.yRot = 0.0f
        this.EquipTube04_1.zRot = 0.0f
        this.EquipTube05_1.xRot = 0.61f
        this.EquipTube05_1.yRot = 0.0f
        this.EquipTube05_1.zRot = 0.0f
        this.EquipTBase.visible = true
        this.EquipTBase_1.visible = true
        this.GlowEquipBase.visible = true
        when (attackTime) {
            50 -> {
                this.EquipTBase.y = this.equipTBaseDefaultY + (-2.73f * OFFSET_SCALE)
                this.EquipTBase_1.y = this.equipTBase1DefaultY + (-2.73f * OFFSET_SCALE)
            }

            49 -> {
                this.EquipTBase.y = this.equipTBaseDefaultY + (-2.71f * OFFSET_SCALE)
                this.EquipTBase_1.y = this.equipTBase1DefaultY + (-2.71f * OFFSET_SCALE)
            }

            48 -> {
                this.EquipTBase.y = this.equipTBaseDefaultY + (-2.69f * OFFSET_SCALE)
                this.EquipTBase_1.y = this.equipTBase1DefaultY + (-2.69f * OFFSET_SCALE)
            }

            47 -> {
                this.EquipTBase.y = this.equipTBaseDefaultY + (-2.375f * OFFSET_SCALE)
                this.EquipTBase_1.y = this.equipTBase1DefaultY + (-2.375f * OFFSET_SCALE)
            }

            46 -> {
                this.EquipTBase.y = this.equipTBaseDefaultY + (-2.06f * OFFSET_SCALE)
                this.EquipTBase_1.y = this.equipTBase1DefaultY + (-2.06f * OFFSET_SCALE)
            }

            45 -> {
                this.EquipTBase.y = this.equipTBaseDefaultY + (-1.75f * OFFSET_SCALE)
                this.EquipTBase_1.y = this.equipTBase1DefaultY + (-1.75f * OFFSET_SCALE)
            }

            44 -> {
                this.EquipTBase.y = this.equipTBaseDefaultY + (-1.44f * OFFSET_SCALE)
                this.EquipTBase_1.y = this.equipTBase1DefaultY + (-1.44f * OFFSET_SCALE)
            }

            43 -> {
                this.EquipTBase.y = this.equipTBaseDefaultY + (-1.125f * OFFSET_SCALE)
                this.EquipTBase_1.y = this.equipTBase1DefaultY + (-1.125f * OFFSET_SCALE)
            }

            42 -> {
                this.EquipTBase.y = this.equipTBaseDefaultY + (-0.81f * OFFSET_SCALE)
                this.EquipTBase_1.y = this.equipTBase1DefaultY + (-0.81f * OFFSET_SCALE)
            }

            41 -> {
                this.EquipTBase.y = this.equipTBaseDefaultY + (-0.5f * OFFSET_SCALE)
                this.EquipTBase_1.y = this.equipTBase1DefaultY + (-0.5f * OFFSET_SCALE)
            }

            40 -> {
                this.EquipTBase.y = this.equipTBaseDefaultY + (-0.19f * OFFSET_SCALE)
                this.EquipTBase_1.y = this.equipTBase1DefaultY + (-0.19f * OFFSET_SCALE)
            }

            39 -> {
                this.EquipTBase.y = this.equipTBaseDefaultY + (-0.095f * OFFSET_SCALE)
                this.EquipTBase_1.y = this.equipTBase1DefaultY + (-0.095f * OFFSET_SCALE)
            }

            else -> {
                this.EquipTBase.y = this.equipTBaseDefaultY
                this.EquipTBase_1.y = this.equipTBase1DefaultY
            }
        }
        when (attackTime) {
            49, 50 -> {
                this.EquipT07.visible = true
                this.EquipT07_1.visible = true
                this.EquipT06.visible = false
                this.EquipT06_1.visible = false
                this.EquipT05.visible = false
                this.EquipT05_1.visible = false
                this.EquipT04.visible = false
                this.EquipT04_1.visible = false
                this.EquipT03.visible = false
                this.EquipT03_1.visible = false
                this.EquipT02.visible = false
                this.EquipT02_1.visible = false
                this.EquipT01.visible = false
                this.EquipT01_1.visible = false
            }

            47, 48 -> {
                this.EquipT07.visible = true
                this.EquipT07_1.visible = true
                this.EquipT06.visible = true
                this.EquipT06_1.visible = true
                this.EquipT05.visible = false
                this.EquipT05_1.visible = false
                this.EquipT04.visible = false
                this.EquipT04_1.visible = false
                this.EquipT03.visible = false
                this.EquipT03_1.visible = false
                this.EquipT02.visible = false
                this.EquipT02_1.visible = false
                this.EquipT01.visible = false
                this.EquipT01_1.visible = false
            }

            45, 46 -> {
                this.EquipT07.visible = true
                this.EquipT07_1.visible = true
                this.EquipT06.visible = true
                this.EquipT06_1.visible = true
                this.EquipT05.visible = true
                this.EquipT05_1.visible = true
                this.EquipT04.visible = false
                this.EquipT04_1.visible = false
                this.EquipT03.visible = false
                this.EquipT03_1.visible = false
                this.EquipT02.visible = false
                this.EquipT02_1.visible = false
                this.EquipT01.visible = false
                this.EquipT01_1.visible = false
            }

            43, 44 -> {
                this.EquipT07.visible = true
                this.EquipT07_1.visible = true
                this.EquipT06.visible = true
                this.EquipT06_1.visible = true
                this.EquipT05.visible = true
                this.EquipT05_1.visible = true
                this.EquipT04.visible = true
                this.EquipT04_1.visible = true
                this.EquipT03.visible = false
                this.EquipT03_1.visible = false
                this.EquipT02.visible = false
                this.EquipT02_1.visible = false
                this.EquipT01.visible = false
                this.EquipT01_1.visible = false
            }

            41, 42 -> {
                this.EquipT07.visible = true
                this.EquipT07_1.visible = true
                this.EquipT06.visible = true
                this.EquipT06_1.visible = true
                this.EquipT05.visible = true
                this.EquipT05_1.visible = true
                this.EquipT04.visible = true
                this.EquipT04_1.visible = true
                this.EquipT03.visible = true
                this.EquipT03_1.visible = true
                this.EquipT02.visible = false
                this.EquipT02_1.visible = false
                this.EquipT01.visible = false
                this.EquipT01_1.visible = false
            }

            39, 40 -> {
                this.EquipT07.visible = true
                this.EquipT07_1.visible = true
                this.EquipT06.visible = true
                this.EquipT06_1.visible = true
                this.EquipT05.visible = true
                this.EquipT05_1.visible = true
                this.EquipT04.visible = true
                this.EquipT04_1.visible = true
                this.EquipT03.visible = true
                this.EquipT03_1.visible = true
                this.EquipT02.visible = true
                this.EquipT02_1.visible = true
                this.EquipT01.visible = false
                this.EquipT01_1.visible = false
            }

            else -> if (attackTime < 39) {
                this.EquipT07.visible = true
                this.EquipT07_1.visible = true
                this.EquipT06.visible = true
                this.EquipT06_1.visible = true
                this.EquipT05.visible = true
                this.EquipT05_1.visible = true
                this.EquipT04.visible = true
                this.EquipT04_1.visible = true
                this.EquipT03.visible = true
                this.EquipT03_1.visible = true
                this.EquipT02.visible = true
                this.EquipT02_1.visible = true
                this.EquipT01.visible = true
                this.EquipT01_1.visible = true
            }
        }
    }

    private fun syncGlowParts() {
        this.GlowBodyMain.copyFrom(this.BodyMain)
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "subm_hime"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelSubmHime")
        private val SITTING_TRANSLATE_Y = sittingY("ModelSubmHime")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelSubmHime")
        private val RIDING_TRANSLATE_Y = ridingY("ModelSubmHime")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 104).addBox(-6.5f, -11f, -4f, 13f, 17f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(0, 36).addBox(-3f, 0f, 0f, 6f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.2f, -8.5f, -3.8f, -0.8727f, 0.0873f, 0.0698f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(24, 71).mirror()
                    .addBox(-3f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, -9.3f, -0.7f, 0f, 0f, 0.2618f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(24, 54).mirror().addBox(0f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 11f, 2.5f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 88).addBox(-7.5f, 0f, -5.7f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.3f, 0.3491f, 0f, 0f)
            )

            val Skirt01 = Butt.addOrReplaceChild(
                "Skirt01",
                CubeListBuilder.create().texOffs(128, 0).addBox(-8.5f, 0f, -8.5f, 17f, 5f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3f, 1.5f, -0.0873f, 0f, 0f)
            )

            val Skirt02 = Skirt01.addOrReplaceChild(
                "Skirt02",
                CubeListBuilder.create().texOffs(128, 17).addBox(-10.5f, 0f, -6.5f, 21f, 5f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3.5f, -2.7f, -0.0873f, 0f, 0f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 68).mirror().addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.8f, 5.5f, -2.6f, -0.192f, 0f, -0.0873f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 47).mirror().addBox(0f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3f, 14f, -3f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 68).addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.8f, 5.5f, -2.6f, -0.2967f, 0f, 0.0873f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 47).addBox(-6f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(3f, 14f, -3f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(24, 71).addBox(-2f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.3f, -0.7f, 0.2094f, 0f, -0.2618f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(24, 54).addBox(-5f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 11f, 2.5f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(24, 63).addBox(-2.5f, -3f, -2.9f, 5f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -9.6f, 0.5f, 0.1047f, 0f, 0f)
            )

            val Collar01 = Neck.addOrReplaceChild(
                "Collar01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6f, -2f, -4f, 12f, 3f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -1.1f, -1.2f)
            )

            val Collar02 = Collar01.addOrReplaceChild(
                "Collar02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 2f, 4f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.6f, -3.2f, -0.6981f, 0f, 0f)
            )

            val Collar03 = Collar02.addOrReplaceChild(
                "Collar03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 2f, 3f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3.5f, 0f, 0.2618f, 0f, 0f)
            )

            val Collar04 = Collar03.addOrReplaceChild(
                "Collar04",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, 0f, -1f, 1f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.5f, 0f, 0.4554f, 0f, 0f)
            )

            val Collar05 = Collar04.addOrReplaceChild(
                "Collar05",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, 0f, -1f, 5f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 4f, -0.2f)
            )

            val Collar05a = Collar05.addOrReplaceChild(
                "Collar05a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -2f, -0.5f, 3f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.5f, 2f, 0f, 0f, -0.0873f, -0.3491f)
            )

            val Collar05b = Collar05.addOrReplaceChild(
                "Collar05b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, -2f, -0.5f, 3f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.5f, 2f, 0f, 0f, 0.0873f, 0.3491f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, -0.7f)
            )

            val Ahoke01a = Head.addOrReplaceChild(
                "Ahoke01a",
                CubeListBuilder.create().texOffs(50, 79).addBox(-2f, 0f, 0f, 4f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, -1.5f, -2.2689f, -2.618f, 0f)
            )

            val Ahoke02a = Ahoke01a.addOrReplaceChild(
                "Ahoke02a",
                CubeListBuilder.create().texOffs(50, 79).addBox(-2f, 0f, 0f, 4f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.9f, 0f, 0.7854f, -0.0524f, 0f)
            )

            val Ahoke03a = Ahoke02a.addOrReplaceChild(
                "Ahoke03a",
                CubeListBuilder.create().texOffs(50, 79).addBox(-2f, 0f, 0f, 4f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.9f, 0f, 1.0472f, 0.0524f, 0f)
            )

            val Ahoke04a = Ahoke03a.addOrReplaceChild(
                "Ahoke04a",
                CubeListBuilder.create().texOffs(50, 77).addBox(-2f, 0f, 0f, 4f, 8f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.9f, 0f, 0.4887f, 0.0524f, 0f)
            )

            val Ahoke05a = Ahoke04a.addOrReplaceChild(
                "Ahoke05a",
                CubeListBuilder.create().texOffs(50, 77).addBox(-2f, 0f, 0f, 4f, 8f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7.9f, 0f, -0.2618f, 0.0873f, 0f)
            )

            val Ahoke06a = Ahoke05a.addOrReplaceChild(
                "Ahoke06a",
                CubeListBuilder.create().texOffs(42, 89).mirror().addBox(-2f, 0f, 0f, 4f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7.9f, 0f, -0.5236f, 0.0873f, 0f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(46, 104).addBox(-7.5f, 0f, 0f, 15f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.8f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(80, 0).addBox(-7.5f, 0f, 0f, 15f, 17f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1f, 0.2618f, 0f, 0f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(72, 29).addBox(-8f, 0f, -5f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 13.5f, 5.5f, -0.0873f, 0f, 0f)
            )

            val Hair03 = Hair02.addOrReplaceChild(
                "Hair03",
                CubeListBuilder.create().texOffs(26, 32).addBox(-8f, 0f, -4.5f, 16f, 15f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12.5f, -0.1f, -0.0524f, 0f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 77).addBox(-8f, -8f, -7.4f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0.1f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(0, 18).mirror().addBox(-1f, 0f, 0f, 2f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7f, 3f, -5.5f, -0.192f, 0.1745f, 0.0873f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(0, 18).mirror().addBox(-1f, 0f, 0f, 2f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 10f, 0f, 0.1745f, 0f, -0.0524f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(0, 18).addBox(-1f, 0f, 0f, 2f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, 3f, -5.5f, -0.192f, -0.1745f, -0.0873f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(0, 18).addBox(-1f, 0f, 0f, 2f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10f, 0f, 0.1745f, 0f, 0.0873f)
            )

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(52, 56).addBox(-8.5f, 0f, 0f, 17f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -7f)
            )

            val Ahoke01 = Head.addOrReplaceChild(
                "Ahoke01",
                CubeListBuilder.create().texOffs(50, 77).addBox(-2f, 0f, 0f, 4f, 8f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, -15f, 0f, -2.0071f, 0.5236f, 0f)
            )

            val Ahoke02 = Ahoke01.addOrReplaceChild(
                "Ahoke02",
                CubeListBuilder.create().texOffs(50, 77).addBox(-2f, 0f, 0f, 4f, 8f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7.9f, 0f, 1.0472f, -0.0524f, 0f)
            )

            val Ahoke03 = Ahoke02.addOrReplaceChild(
                "Ahoke03",
                CubeListBuilder.create().texOffs(50, 79).addBox(-2f, 0f, 0f, 4f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7.9f, 0f, 0.7854f, 0.0524f, 0f)
            )

            val Ahoke04 = Ahoke03.addOrReplaceChild(
                "Ahoke04",
                CubeListBuilder.create().texOffs(50, 77).addBox(-2f, 0f, 0f, 4f, 8f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.9f, 0f, 0.4363f, 0.0524f, 0f)
            )

            val Ahoke05 = Ahoke04.addOrReplaceChild(
                "Ahoke05",
                CubeListBuilder.create().texOffs(50, 79).addBox(-2f, 0f, 0f, 4f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7.9f, 0f, -0.1745f, 0.0873f, 0f)
            )

            val Ahoke06 = Ahoke05.addOrReplaceChild(
                "Ahoke06",
                CubeListBuilder.create().texOffs(42, 90).addBox(-2f, 0f, 0f, 4f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.9f, 0f, -0.4363f, 0.0873f, 0f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(0, 36).mirror().addBox(-3f, 0f, 0f, 6f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.2f, -8.5f, -3.7f, -0.8727f, -0.0873f, -0.0698f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 104),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(24, 63),
                PartPose.offsetAndRotation(0f, -9.6f, 0.5f, 0.1047f, 0f, 0f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(44, 101),
                PartPose.offset(0f, -1f, -0.7f)
            )
            addFaceLayer(GlowHead)

            val EquipBack = GlowBodyMain.addOrReplaceChild(
                "EquipBack",
                CubeListBuilder.create().texOffs(17, 31).addBox(-2f, 0f, -2f, 4f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.7f, 4.4f, -0.7854f, 0f, 0f)
            )

            val GlowEquipBase = GlowBodyMain.addOrReplaceChild(
                "GlowEquipBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 8f, 3f)
            )

            val EquipTube00 = GlowEquipBase.addOrReplaceChild(
                "EquipTube00",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.5f, 3f, 1.4f, 0.2618f, 0.61f, 0f)
            )

            val EquipTube01 = EquipTube00.addOrReplaceChild(
                "EquipTube01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4.5f, 0f, 0.3491f, 0f, 0f)
            )

            val EquipTube02 = EquipTube01.addOrReplaceChild(
                "EquipTube02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, -1f, 0.5236f, 0f, 0f)
            )

            val EquipTube03 = EquipTube02.addOrReplaceChild(
                "EquipTube03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0.6109f, 0f, 0f)
            )

            val EquipTube04 = EquipTube03.addOrReplaceChild(
                "EquipTube04",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0.6981f, 0f, 0f)
            )

            val EquipTube05 = EquipTube04.addOrReplaceChild(
                "EquipTube05",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0.6109f, 0f, 0f)
            )

            val EquipTube05a = EquipTube05.addOrReplaceChild(
                "EquipTube05a",
                CubeListBuilder.create().texOffs(44, 67).addBox(-1f, 0f, 0f, 2f, 7f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipTBase = EquipTube05a.addOrReplaceChild(
                "EquipTBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 26f, 1f, 0f, 0.61f, 0f)
            )

            val EquipT01 = EquipTBase.addOrReplaceChild(
                "EquipT01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, 0f, -2f, 4f, 3f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, -19f, 0f)
            )

            val EquipT07 = EquipTBase.addOrReplaceChild(
                "EquipT07",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, 0f, -2f, 4f, 1f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, 24.7f, 0f)
            )

            val EquipT02 = EquipTBase.addOrReplaceChild(
                "EquipT02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, 0f, -3f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -16f, 0f)
            )

            val EquipT02c = EquipT02.addOrReplaceChild(
                "EquipT02c",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, -0.5f, 3f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, 5.9f, 0f, 1.5708f, 0f)
            )

            val EquipT02a = EquipT02.addOrReplaceChild(
                "EquipT02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, -0.5f, 3f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offset(2.9f, 0.5f, 0f)
            )

            val EquipT02d = EquipT02.addOrReplaceChild(
                "EquipT02d",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, -0.5f, 3f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, -5.9f, 0f, -1.5708f, 0f)
            )

            val EquipT02b = EquipT02.addOrReplaceChild(
                "EquipT02b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, 0f, -0.5f, 3f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offset(-2.9f, 0.5f, 0f)
            )

            val EquipT04 = EquipTBase.addOrReplaceChild(
                "EquipT04",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, 0f, -3f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 4f, 0f)
            )

            val EquipTEyeB = EquipT04.addOrReplaceChild(
                "EquipTEyeB",
                CubeListBuilder.create().texOffs(0, 14).addBox(0f, 0f, 0f, 0f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.2f, 10.9f, 3f, -2.0944f, 0f, 0f)
            )

            val EquipTJaw01 = EquipT04.addOrReplaceChild(
                "EquipTJaw01",
                CubeListBuilder.create().texOffs(59, 25).addBox(-3.5f, 0f, 0f, 7f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6.3f, 0.5f, 0.2618f, 0f, 0f)
            )

            val EquipTJaw02 = EquipT04.addOrReplaceChild(
                "EquipTJaw02",
                CubeListBuilder.create().texOffs(59, 25).mirror()
                    .addBox(-3.5f, 0f, -2.5f, 7f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 4.6f, 1f)
            )

            val EquipTEyeA = EquipT04.addOrReplaceChild(
                "EquipTEyeA",
                CubeListBuilder.create().texOffs(0, 14).addBox(0f, 0f, 0f, 0f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.2f, 10.9f, 3f, -2.0944f, 0f, 0f)
            )

            val EquipT06 = EquipTBase.addOrReplaceChild(
                "EquipT06",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, 0f, -2.5f, 5f, 1f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 24f, 0f)
            )

            val EquipT03 = EquipTBase.addOrReplaceChild(
                "EquipT03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, 0f, -3f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, 0f)
            )

            val EquipT05 = EquipTBase.addOrReplaceChild(
                "EquipT05",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, 0f, -3f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 14f, 0f)
            )

            val EquipTube04a = EquipTube04.addOrReplaceChild(
                "EquipTube04a",
                CubeListBuilder.create().texOffs(44, 67).addBox(-1f, 0f, 0f, 2f, 7f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipTube03a = EquipTube03.addOrReplaceChild(
                "EquipTube03a",
                CubeListBuilder.create().texOffs(44, 67).addBox(-1f, 0f, 0f, 2f, 7f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipTube02a = EquipTube02.addOrReplaceChild(
                "EquipTube02a",
                CubeListBuilder.create().texOffs(44, 67).addBox(-1f, 0f, 0f, 2f, 7f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipTube01a = EquipTube01.addOrReplaceChild(
                "EquipTube01a",
                CubeListBuilder.create().texOffs(44, 67).addBox(-1f, 0f, -1f, 2f, 7f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipTube00_1 = GlowEquipBase.addOrReplaceChild(
                "EquipTube00_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.5f, 3f, 1.4f, 0.2618f, -0.61f, 0f)
            )

            val EquipTube01_1 = EquipTube00_1.addOrReplaceChild(
                "EquipTube01_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4.5f, 0f, 0.3491f, 0f, 0f)
            )

            val EquipTube01a_1 = EquipTube01_1.addOrReplaceChild(
                "EquipTube01a_1",
                CubeListBuilder.create().texOffs(44, 67).addBox(-1f, 0f, -1f, 2f, 7f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipTube02_1 = EquipTube01_1.addOrReplaceChild(
                "EquipTube02_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, -1f, 0.5236f, 0f, 0f)
            )

            val EquipTube03_1 = EquipTube02_1.addOrReplaceChild(
                "EquipTube03_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0.6109f, 0f, 0f)
            )

            val EquipTube03a_1 = EquipTube03_1.addOrReplaceChild(
                "EquipTube03a_1",
                CubeListBuilder.create().texOffs(44, 67).addBox(-1f, 0f, 0f, 2f, 7f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipTube04_1 = EquipTube03_1.addOrReplaceChild(
                "EquipTube04_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0.6981f, 0f, 0f)
            )

            val EquipTube04a_1 = EquipTube04_1.addOrReplaceChild(
                "EquipTube04a_1",
                CubeListBuilder.create().texOffs(44, 67).addBox(-1f, 0f, 0f, 2f, 7f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipTube05_1 = EquipTube04_1.addOrReplaceChild(
                "EquipTube05_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0.6109f, 0f, 0f)
            )

            val EquipTube05a_1 = EquipTube05_1.addOrReplaceChild(
                "EquipTube05a_1",
                CubeListBuilder.create().texOffs(44, 67).addBox(-1f, 0f, 0f, 2f, 7f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipTBase_1 = EquipTube05a_1.addOrReplaceChild(
                "EquipTBase_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 26f, 1f, 0f, -0.61f, 0f)
            )

            val EquipT05_1 = EquipTBase_1.addOrReplaceChild(
                "EquipT05_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, 0f, -3f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 14f, 0f)
            )

            val EquipT04_1 = EquipTBase_1.addOrReplaceChild(
                "EquipT04_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, 0f, -3f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 4f, 0f)
            )

            val EquipTJaw01_1 = EquipT04_1.addOrReplaceChild(
                "EquipTJaw01_1",
                CubeListBuilder.create().texOffs(59, 25).addBox(-3.5f, 0f, 0f, 7f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6.3f, 0.5f, 0.2618f, 0f, 0f)
            )

            val EquipTJaw02_1 = EquipT04_1.addOrReplaceChild(
                "EquipTJaw02_1",
                CubeListBuilder.create().texOffs(59, 25).mirror()
                    .addBox(-3.5f, 0f, -2.5f, 7f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 4.6f, 1f)
            )

            val EquipTEyeB_1 = EquipT04_1.addOrReplaceChild(
                "EquipTEyeB_1",
                CubeListBuilder.create().texOffs(0, 14).addBox(0f, 0f, 0f, 0f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.2f, 10.9f, 3f, -2.0944f, 0f, 0f)
            )

            val EquipTEyeA_1 = EquipT04_1.addOrReplaceChild(
                "EquipTEyeA_1",
                CubeListBuilder.create().texOffs(0, 14).addBox(0f, 0f, 0f, 0f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.2f, 10.9f, 3f, -2.0944f, 0f, 0f)
            )

            val EquipT07_1 = EquipTBase_1.addOrReplaceChild(
                "EquipT07_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, 0f, -2f, 4f, 1f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, 24.7f, 0f)
            )

            val EquipT01_1 = EquipTBase_1.addOrReplaceChild(
                "EquipT01_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, 0f, -2f, 4f, 3f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, -19f, 0f)
            )

            val EquipT02_1 = EquipTBase_1.addOrReplaceChild(
                "EquipT02_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, 0f, -3f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -16f, 0f)
            )

            val EquipT02a_1 = EquipT02_1.addOrReplaceChild(
                "EquipT02a_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, -0.5f, 3f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offset(2.9f, 0.5f, 0f)
            )

            val EquipT02b_1 = EquipT02_1.addOrReplaceChild(
                "EquipT02b_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, 0f, -0.5f, 3f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offset(-2.9f, 0.5f, 0f)
            )

            val EquipT02c_1 = EquipT02_1.addOrReplaceChild(
                "EquipT02c_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, -0.5f, 3f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, 5.9f, 0f, 1.5708f, 0f)
            )

            val EquipT02d_1 = EquipT02_1.addOrReplaceChild(
                "EquipT02d_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, -0.5f, 3f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, -5.9f, 0f, -1.5708f, 0f)
            )

            val EquipT03_1 = EquipTBase_1.addOrReplaceChild(
                "EquipT03_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, 0f, -3f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, 0f)
            )

            val EquipT06_1 = EquipTBase_1.addOrReplaceChild(
                "EquipT06_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, 0f, -2.5f, 5f, 1f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 24f, 0f)
            )

            val EquipTube02a_1 = EquipTube02_1.addOrReplaceChild(
                "EquipTube02a_1",
                CubeListBuilder.create().texOffs(44, 67).addBox(-1f, 0f, 0f, 2f, 7f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 256, 128)
        }
    }
}
