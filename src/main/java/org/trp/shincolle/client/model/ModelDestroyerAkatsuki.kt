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
import org.trp.shincolle.entity.EntityDestroyerAkatsuki
import org.trp.shincolle.entity.base.EntityShipBase

class ModelDestroyerAkatsuki<T : EntityShipBase?>(root: ModelPart) : ShipModelHumanoidBase<T?>(), IGlowableModel {
    private var isDeadPose = false
    private var poseTranslateY = 0f

    private val BodyMain: ModelPart
    private val Butt: ModelPart
    private val Head: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val Cloth01: ModelPart
    private val EquipBase: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft01: ModelPart
    private val Skirt01: ModelPart
    private val LegRight02: ModelPart
    private val LegRight03: ModelPart
    private val LegLeft02: ModelPart
    private val LegLeft03: ModelPart
    private val Skirt02: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val Ahoke: ModelPart
    private val HairU01: ModelPart
    private val HairL01: ModelPart
    private val HairR01: ModelPart
    private val HairL02: ModelPart
    private val HairR02: ModelPart
    private val Hair01: ModelPart
    private val HatBase: ModelPart
    private val Hair02a1: ModelPart?
    private val Hair02b1: ModelPart?
    private val Hair02c1: ModelPart?
    private val Hair02d1: ModelPart?
    private val Hair02e1: ModelPart?
    private val Hair02a2: ModelPart
    private val Hair02b2: ModelPart
    private val Hair02c2: ModelPart
    private val Hair02d2: ModelPart
    private val Hair02e2: ModelPart
    private val Hat01a: ModelPart
    private val Hat01b: ModelPart
    private val Hat01c: ModelPart
    private val Hat01d: ModelPart
    private val Hat02a: ModelPart
    private val Hat03a: ModelPart
    private val Hat03b: ModelPart
    private val Hat03c: ModelPart
    private val Hat03d: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmLeft03: ModelPart
    private val EquipTL03: ModelPart
    private val ArmRight02: ModelPart
    private val ArmRight03: ModelPart
    private val EquipTR03: ModelPart
    private val EquipC12: ModelPart
    private val EquipC13: ModelPart
    private val EquipC14a: ModelPart
    private val EquipC15a: ModelPart
    private val EquipC14b: ModelPart
    private val EquipC15b: ModelPart
    private val Cloth02: ModelPart
    private val EquipMain01: ModelPart
    private val EquipC01: ModelPart
    private val EquipMain02: ModelPart
    private val EquipMain03: ModelPart
    private val EquipMain04: ModelPart
    private val EquipTL02: ModelPart
    private val EquipTR02: ModelPart
    private val EquipHead01: ModelPart
    private val EquipHead02: ModelPart
    private val EquipHead03: ModelPart
    private val EquipHead04: ModelPart
    private val EquipHead05: ModelPart
    private val EquipTL02a: ModelPart
    private val EquipTL02b: ModelPart
    private val EquipTL02c: ModelPart
    private val EquipTL02d: ModelPart
    private val EquipTL02e: ModelPart
    private val EquipTL02f: ModelPart
    private val EquipTL02g: ModelPart
    private val EquipTL02h: ModelPart
    private val EquipTR02a: ModelPart
    private val EquipTR02b: ModelPart
    private val EquipTR02c: ModelPart
    private val EquipTR02d: ModelPart
    private val EquipTR02e: ModelPart
    private val EquipTR02f: ModelPart
    private val EquipTR02g: ModelPart
    private val EquipTR02h: ModelPart
    private val EquipSL01: ModelPart
    private val EquipSL01a: ModelPart
    private val EquipSL01b: ModelPart
    private val EquipSL01c: ModelPart
    private val EquipSL01d: ModelPart
    private val EquipSL01e: ModelPart
    private val EquipSL01f: ModelPart
    private val LightEF01: ModelPart
    private val LightEF01a: ModelPart
    private val LightEF01b: ModelPart
    private val EquipC02: ModelPart
    private val EquipC03: ModelPart
    private val EquipC04a: ModelPart
    private val EquipC05a: ModelPart
    private val EquipC04b: ModelPart
    private val EquipC05b: ModelPart
    private val GlowBodyMain: ModelPart?
    private val GlowHead: ModelPart
    private val buttDefaultY: Float
    private val skirt01DefaultY: Float
    private val skirt02DefaultY: Float
    private val hair01DefaultY: Float
    private val legLeft02DefaultX: Float
    private val legLeft02DefaultY: Float
    private val legLeft02DefaultZ: Float
    private val legRight02DefaultX: Float
    private val legRight02DefaultY: Float
    private val legRight02DefaultZ: Float
    private val armLeft02DefaultX: Float
    private val armLeft02DefaultY: Float
    private val armLeft02DefaultZ: Float
    private val armRight02DefaultX: Float
    private val armRight02DefaultY: Float
    private val armRight02DefaultZ: Float
    private val hair01DefaultXRot: Float
    private val hair01DefaultZRot: Float
    private val hair02a1DefaultXRot: Float
    private val hair02a1DefaultZRot: Float
    private val hair02a2DefaultXRot: Float
    private val hair02a2DefaultZRot: Float
    private val hair02b1DefaultXRot: Float
    private val hair02b1DefaultZRot: Float
    private val hair02b2DefaultXRot: Float
    private val hair02b2DefaultZRot: Float
    private val hair02c1DefaultXRot: Float
    private val hair02c1DefaultZRot: Float
    private val hair02c2DefaultXRot: Float
    private val hair02c2DefaultZRot: Float
    private val hair02d1DefaultXRot: Float
    private val hair02d1DefaultZRot: Float
    private val hair02d2DefaultXRot: Float
    private val hair02d2DefaultZRot: Float
    private val hair02e1DefaultXRot: Float
    private val hair02e1DefaultZRot: Float
    private val hair02e2DefaultXRot: Float
    private val hair02e2DefaultZRot: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.Cloth02 = this.Cloth01.getChild("Cloth02")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.ArmRight03 = this.ArmRight02.getChild("ArmRight03")
        this.EquipTR03 = this.ArmRight02.getChild("EquipTR03")
        this.EquipC12 = this.ArmRight02.getChild("EquipC12")
        this.EquipC13 = this.EquipC12.getChild("EquipC13")
        this.EquipC15a = this.EquipC12.getChild("EquipC15a")
        this.EquipC15b = this.EquipC15a.getChild("EquipC15b")
        this.EquipC14a = this.EquipC12.getChild("EquipC14a")
        this.EquipC14b = this.EquipC14a.getChild("EquipC14b")
        this.EquipBase = this.BodyMain.getChild("EquipBase")
        this.EquipC01 = this.EquipBase.getChild("EquipC01")
        this.EquipC02 = this.EquipC01.getChild("EquipC02")
        this.EquipC04a = this.EquipC02.getChild("EquipC04a")
        this.EquipC04b = this.EquipC04a.getChild("EquipC04b")
        this.EquipC03 = this.EquipC02.getChild("EquipC03")
        this.EquipC05a = this.EquipC02.getChild("EquipC05a")
        this.EquipC05b = this.EquipC05a.getChild("EquipC05b")
        this.EquipMain01 = this.EquipBase.getChild("EquipMain01")
        this.EquipMain03 = this.EquipMain01.getChild("EquipMain03")
        this.EquipHead01 = this.EquipMain03.getChild("EquipHead01")
        this.EquipHead02 = this.EquipHead01.getChild("EquipHead02")
        this.EquipHead04 = this.EquipHead02.getChild("EquipHead04")
        this.EquipHead03 = this.EquipHead02.getChild("EquipHead03")
        this.EquipHead05 = this.EquipHead02.getChild("EquipHead05")
        this.EquipTL02 = this.EquipMain01.getChild("EquipTL02")
        this.EquipTL02g = this.EquipTL02.getChild("EquipTL02g")
        this.EquipTL02e = this.EquipTL02.getChild("EquipTL02e")
        this.EquipTL02c = this.EquipTL02.getChild("EquipTL02c")
        this.EquipTL02f = this.EquipTL02.getChild("EquipTL02f")
        this.EquipTL02d = this.EquipTL02.getChild("EquipTL02d")
        this.EquipTL02a = this.EquipTL02.getChild("EquipTL02a")
        this.EquipTL02b = this.EquipTL02.getChild("EquipTL02b")
        this.EquipTL02h = this.EquipTL02.getChild("EquipTL02h")
        this.EquipTR02 = this.EquipMain01.getChild("EquipTR02")
        this.EquipTR02c = this.EquipTR02.getChild("EquipTR02c")
        this.EquipTR02b = this.EquipTR02.getChild("EquipTR02b")
        this.EquipTR02h = this.EquipTR02.getChild("EquipTR02h")
        this.EquipTR02e = this.EquipTR02.getChild("EquipTR02e")
        this.EquipTR02f = this.EquipTR02.getChild("EquipTR02f")
        this.EquipTR02d = this.EquipTR02.getChild("EquipTR02d")
        this.EquipTR02a = this.EquipTR02.getChild("EquipTR02a")
        this.EquipTR02g = this.EquipTR02.getChild("EquipTR02g")
        this.EquipMain02 = this.EquipMain01.getChild("EquipMain02")
        this.EquipMain04 = this.EquipMain01.getChild("EquipMain04")
        this.Head = this.BodyMain.getChild("Head")
        this.HairMain = this.Head.getChild("HairMain")
        this.HatBase = this.HairMain.getChild("HatBase")
        this.Hat03a = this.HatBase.getChild("Hat03a")
        this.Hat01b = this.HatBase.getChild("Hat01b")
        this.Hat01c = this.HatBase.getChild("Hat01c")
        this.Hat01d = this.HatBase.getChild("Hat01d")
        this.Hat01a = this.HatBase.getChild("Hat01a")
        this.Hat02a = this.HatBase.getChild("Hat02a")
        this.Hat03c = this.HatBase.getChild("Hat03c")
        this.Hat03d = this.HatBase.getChild("Hat03d")
        this.Hat03b = this.HatBase.getChild("Hat03b")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02b1 = this.Hair01.getChild("Hair02b1")
        this.Hair02b2 = this.Hair02b1.getChild("Hair02b2")
        this.Hair02a1 = this.Hair01.getChild("Hair02a1")
        this.Hair02a2 = this.Hair02a1.getChild("Hair02a2")
        this.Hair02e1 = this.Hair01.getChild("Hair02e1")
        this.Hair02e2 = this.Hair02e1.getChild("Hair02e2")
        this.Hair02c1 = this.Hair01.getChild("Hair02c1")
        this.Hair02c2 = this.Hair02c1.getChild("Hair02c2")
        this.Hair02d1 = this.Hair01.getChild("Hair02d1")
        this.Hair02d2 = this.Hair02d1.getChild("Hair02d2")
        this.Hair = this.Head.getChild("Hair")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.ArmLeft03 = this.ArmLeft02.getChild("ArmLeft03")
        this.EquipTL03 = this.ArmLeft02.getChild("EquipTL03")
        this.EquipSL01 = this.BodyMain.getChild("EquipSL01")
        this.EquipSL01a = this.EquipSL01.getChild("EquipSL01a")
        this.EquipSL01b = this.EquipSL01.getChild("EquipSL01b")
        this.EquipSL01c = this.EquipSL01.getChild("EquipSL01c")
        this.EquipSL01d = this.EquipSL01.getChild("EquipSL01d")
        this.EquipSL01e = this.EquipSL01.getChild("EquipSL01e")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.LegRight03 = this.LegRight02.getChild("LegRight03")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.LegLeft03 = this.LegLeft02.getChild("LegLeft03")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.Skirt02 = this.Skirt01.getChild("Skirt02")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowHead = this.GlowBodyMain.getChild("GlowHead")
        this.initFaceParts(this.GlowHead)
        this.EquipSL01f = this.GlowBodyMain.getChild("EquipSL01f")
        this.LightEF01 = this.EquipSL01f.getChild("LightEF01")
        this.LightEF01a = this.EquipSL01f.getChild("LightEF01a")
        this.LightEF01b = this.EquipSL01f.getChild("LightEF01b")
        this.buttDefaultY = this.Butt.y
        this.skirt01DefaultY = this.Skirt01.y
        this.skirt02DefaultY = this.Skirt02.y
        this.hair01DefaultY = this.Hair01.y
        this.legLeft02DefaultX = this.LegLeft02.x
        this.legLeft02DefaultY = this.LegLeft02.y
        this.legLeft02DefaultZ = this.LegLeft02.z
        this.legRight02DefaultX = this.LegRight02.x
        this.legRight02DefaultY = this.LegRight02.y
        this.legRight02DefaultZ = this.LegRight02.z
        this.armLeft02DefaultX = this.ArmLeft02.x
        this.armLeft02DefaultY = this.ArmLeft02.y
        this.armLeft02DefaultZ = this.ArmLeft02.z
        this.armRight02DefaultX = this.ArmRight02.x
        this.armRight02DefaultY = this.ArmRight02.y
        this.armRight02DefaultZ = this.ArmRight02.z
        this.hair01DefaultXRot = this.Hair01.xRot
        this.hair01DefaultZRot = this.Hair01.zRot
        this.hair02a1DefaultXRot = this.Hair02a1.xRot
        this.hair02a1DefaultZRot = this.Hair02a1.zRot
        this.hair02a2DefaultXRot = this.Hair02a2.xRot
        this.hair02a2DefaultZRot = this.Hair02a2.zRot
        this.hair02b1DefaultXRot = this.Hair02b1.xRot
        this.hair02b1DefaultZRot = this.Hair02b1.zRot
        this.hair02b2DefaultXRot = this.Hair02b2.xRot
        this.hair02b2DefaultZRot = this.Hair02b2.zRot
        this.hair02c1DefaultXRot = this.Hair02c1.xRot
        this.hair02c1DefaultZRot = this.Hair02c1.zRot
        this.hair02c2DefaultXRot = this.Hair02c2.xRot
        this.hair02c2DefaultZRot = this.Hair02c2.zRot
        this.hair02d1DefaultXRot = this.Hair02d1.xRot
        this.hair02d1DefaultZRot = this.Hair02d1.zRot
        this.hair02d2DefaultXRot = this.Hair02d2.xRot
        this.hair02d2DefaultZRot = this.Hair02d2.zRot
        this.hair02e1DefaultXRot = this.Hair02e1.xRot
        this.hair02e1DefaultZRot = this.Hair02e1.zRot
        this.hair02e2DefaultXRot = this.Hair02e2.xRot
        this.hair02e2DefaultZRot = this.Hair02e2.zRot
    }

    override fun setupAnim(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val ctx = computePoseContext(entity, limbSwing, limbSwingAmount, ageInTicks, 0.14f)

        this.isDeadPose = false
        this.poseTranslateY = 0.0f
        resetOffsets()
        resetHairRotations()

        applyFaceAndMouth(entity)
        setFlushVisible(entity != null && (entity.emotionPrimary == EntityShipBase.EMOTION_SHY || entity.emotionPrimary == EntityShipBase.EMOTION_HAPPY))
        applyEquipVisibility(entity)

        if (entity != null && entity.isInDeadPose) {
            applyDeadPose()
            syncGlowParts()
            return
        }

        applyHeadRotation(Head, entity, ageInTicks, netHeadYaw, headPitch)
        Head.xRot += HEAD_BASE_X_ROT

        applyBasePose(entity, ctx, ageInTicks, limbSwing, limbSwingAmount)
        applySpecialPoseAdjustments(entity, ctx, ageInTicks)
        applyHairAnimation(ctx, ageInTicks, limbSwing)

        syncGlowParts()
    }

    private fun resetOffsets() {
        Butt.y = buttDefaultY
        Skirt01.y = skirt01DefaultY
        Skirt02.y = skirt02DefaultY
        Hair01.y = hair01DefaultY
        LegLeft02.x = legLeft02DefaultX
        LegLeft02.y = legLeft02DefaultY
        LegLeft02.z = legLeft02DefaultZ
        LegRight02.x = legRight02DefaultX
        LegRight02.y = legRight02DefaultY
        LegRight02.z = legRight02DefaultZ
        ArmLeft02.x = armLeft02DefaultX
        ArmLeft02.y = armLeft02DefaultY
        ArmLeft02.z = armLeft02DefaultZ
        ArmRight02.x = armRight02DefaultX
        ArmRight02.y = armRight02DefaultY
        ArmRight02.z = armRight02DefaultZ
    }

    private fun resetHairRotations() {
        Hair01.xRot = hair01DefaultXRot
        Hair01.zRot = hair01DefaultZRot
        Hair02a1!!.xRot = hair02a1DefaultXRot
        Hair02a1.zRot = hair02a1DefaultZRot
        Hair02a2.xRot = hair02a2DefaultXRot
        Hair02a2.zRot = hair02a2DefaultZRot
        Hair02b1!!.xRot = hair02b1DefaultXRot
        Hair02b1.zRot = hair02b1DefaultZRot
        Hair02b2.xRot = hair02b2DefaultXRot
        Hair02b2.zRot = hair02b2DefaultZRot
        Hair02c1!!.xRot = hair02c1DefaultXRot
        Hair02c1.zRot = hair02c1DefaultZRot
        Hair02c2.xRot = hair02c2DefaultXRot
        Hair02c2.zRot = hair02c2DefaultZRot
        Hair02d1!!.xRot = hair02d1DefaultXRot
        Hair02d1.zRot = hair02d1DefaultZRot
        Hair02d2.xRot = hair02d2DefaultXRot
        Hair02d2.zRot = hair02d2DefaultZRot
        Hair02e1!!.xRot = hair02e1DefaultXRot
        Hair02e1.zRot = hair02e1DefaultZRot
        Hair02e2.xRot = hair02e2DefaultXRot
        Hair02e2.zRot = hair02e2DefaultZRot
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        Head.xRot = 0.0f
        Head.yRot = 0.0f
        Head.zRot = 0.0f
        Ahoke.yRot = 0.5236f
        BodyMain.xRot = 1.55f
        Butt.xRot = BUTT_BASE_X_ROT
        Skirt01.xRot = SKIRT_BASE_X_ROT
        Skirt02.xRot = SKIRT_BASE_X_ROT

        ArmLeft01.xRot = -3.0f
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = 0.3f
        ArmRight01.xRot = -3.0f
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = -0.3f
        ArmLeft02.xRot = 0.0f
        ArmLeft02.zRot = 0.0f
        ArmRight02.xRot = 0.0f
        ArmRight02.zRot = 0.0f

        LegLeft01.xRot = -0.2618f
        LegLeft01.yRot = 0.0f
        LegLeft01.zRot = 0.03f
        LegRight01.xRot = -0.2618f
        LegRight01.yRot = 0.0f
        LegRight01.zRot = -0.03f
        LegLeft02.xRot = 0.0f
        LegRight02.xRot = 0.0f

        EquipHead01.yRot = -1.4f
        EquipHead01.zRot = 1.4f
        EquipC02.yRot = 0.6f
        EquipC04a.xRot = 0.0f
        EquipC05a.xRot = -0.2f
    }

    private fun applyEquipVisibility(entity: EntityShipBase?) {
        if (entity == null) return
        val showRigging = entity.getEquipFlag(EntityDestroyerAkatsuki.EQUIP_RIGGING)
        val showAnchor = entity.getEquipFlag(EntityDestroyerAkatsuki.EQUIP_ANCHOR)
        val showHat = entity.getEquipFlag(EntityDestroyerAkatsuki.EQUIP_HAT)
        val showHandCannon = entity.getEquipFlag(EntityDestroyerAkatsuki.EQUIP_HAND_CANNON)
        val showArmTorpedo = entity.getEquipFlag(EntityDestroyerAkatsuki.EQUIP_ARM_TORPEDO)
        val showShoulderCannon = entity.getEquipFlag(EntityDestroyerAkatsuki.EQUIP_SHOULDER_SEARCHLIGHT)

        EquipBase.visible = showRigging
        EquipHead01.visible = showAnchor
        HatBase.visible = showHat
        EquipC12.visible = showHandCannon
        EquipTR03.visible = showArmTorpedo
        EquipTL03.visible = showArmTorpedo
        EquipSL01.visible = showShoulderCannon
        EquipSL01f.visible = showShoulderCannon
    }

    private fun applyBasePose(
        entity: T?,
        ctx: PoseContext,
        ageInTicks: Float,
        limbSwing: Float,
        limbSwingAmount: Float
    ) {
        val angleX = ctx.angleX
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f)
        val angleX2 = Mth.cos(ageInTicks * 0.08f + 0.6f + limbSwing * 0.5f)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount

        if (entity!!.shipDepth > 0.0) {
            this.poseTranslateY += angleX * 0.05f + 0.025f
        }

        this.Ahoke.yRot = angleX * 0.2f + 1.0f
        this.BodyMain.xRot = -0.1047f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = 0.21f
        this.Skirt01.xRot = -0.052f
        this.Skirt02.xRot = -0.052f
        this.Hair01.xRot = angleX * 0.04f + 0.23f
        this.Hair01.zRot = 0.0f
        this.Hair02a1!!.xRot = -angleX1 * 0.1f + 0.21f
        this.Hair02a1.zRot = 0.0f
        this.Hair02b1!!.xRot = -angleX1 * 0.1f + 0.21f
        this.Hair02b1.zRot = 0.1745f
        this.Hair02c1!!.xRot = -angleX1 * 0.1f + 0.14f
        this.Hair02c1.zRot = -0.1745f
        this.Hair02d1!!.xRot = 0.2618f
        this.Hair02d1.zRot = -angleX1 * 0.1f + 0.35f
        this.Hair02e1!!.xRot = 0.2618f
        this.Hair02e1.zRot = angleX1 * 0.1f - 0.44f
        this.Hair02a2.xRot = -angleX2 * 0.13f + 0.14f
        this.Hair02b2.xRot = -angleX2 * 0.13f + 0.14f
        this.Hair02c2.xRot = -angleX2 * 0.13f + 0.14f
        this.Hair02d2.zRot = -angleX2 * 0.13f - 0.17f
        this.Hair02e2.zRot = angleX2 * 0.13f + 0.26f
        this.HairL01.xRot = angleX * 0.04f + -0.0524f
        this.HairL01.zRot = 0.1396f
        this.HairL02.xRot = -angleX1 * 0.1f + 0.0873f
        this.HairL02.zRot = 0.0873f
        this.HairR01.xRot = angleX * 0.04f + -0.0524f
        this.HairR01.zRot = -0.1396f
        this.HairR02.xRot = -angleX1 * 0.1f + 0.0873f
        this.HairR02.zRot = -0.0873f
        this.ArmLeft01.xRot = angleAdd2 * 0.25f + 0.1745f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = angleX * 0.03f - 0.42f
        this.ArmLeft02.xRot = 0.0f
        this.ArmLeft02.zRot = 0.0f
        this.ArmRight01.xRot = angleAdd1 * 0.25f - 0.0523f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = -angleX * 0.03f + 0.42f
        this.ArmRight02.xRot = 0.0f
        this.ArmRight02.zRot = 0.0f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.1047f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.yRot = 0.0f
        this.LegLeft02.zRot = 0.0f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.1047f
        this.LegRight02.xRot = 0.0f
        this.LegRight02.yRot = 0.0f
        this.LegRight02.zRot = 0.0f
        this.EquipHead01.zRot = angleX * 0.2f - 1.5708f
        this.EquipC02.yRot = 0.5f + this.Head.yRot * 0.5f
        this.EquipC04a.xRot = -0.2f + this.Head.xRot
        if (this.EquipC04a.xRot > 0.0f) {
            this.EquipC04a.xRot = 0.0f
        }
        this.EquipC05a.xRot = this.EquipC04a.xRot
        this.EquipC14a.xRot = this.EquipC04a.xRot
        this.EquipC15a.xRot = this.EquipC04a.xRot
    }

    private fun applySpecialPoseAdjustments(entity: T?, ctx: PoseContext, ageInTicks: Float) {
        var legLeftX = ctx.legAddLeft
        var legRightX = ctx.legAddRight

        val isPassenger = entity != null && entity.isPassenger()
        val isCrouching = entity != null && entity.isCrouching()
        val isSprinting = entity != null && entity.isSprinting
        val isLegacyEmote1 = hasLegacyState(entity, 1, 4)
        val isLegacyEmote6 = hasLegacyState(entity, 6, 1)
        val ridingState = if (entity != null) entity.ridingState else 0

        if (entity != null && !entity.getEquipFlag(EntityDestroyerAkatsuki.EQUIP_RIGGING)) {
            ArmLeft01.zRot += 0.1f
            ArmRight01.zRot -= 0.1f
        }

        if (isSprinting) {
            this.setFace(EntityShipBase.FACE_TENSION)
            Head.xRot = -0.25f
            BodyMain.xRot = 0.1f
            Skirt01.xRot = -0.1f
            Skirt02.xRot = -0.1885f
            ArmLeft01.xRot = -3.6f
            ArmLeft01.yRot = 0.0f
            ArmLeft01.zRot = 0.87f
            ArmRight01.xRot = -3.6f
            ArmRight01.yRot = 0.0f
            ArmRight01.zRot = -0.87f
            legLeftX -= 0.2f
            legRightX -= 0.2f
        }

        if (isCrouching) {
            this.poseTranslateY = SNEAK_TRANSLATE_Y
            Head.xRot -= 1.0472f
            BodyMain.xRot = 1.0472f
            Butt.xRot = -0.4f
            Butt.y = buttDefaultY + (-0.19f * OFFSET_SCALE)
            Skirt01.xRot = -0.12f
            Skirt02.xRot = -0.4f
            Skirt02.y = skirt02DefaultY + (-0.1f * OFFSET_SCALE)
            ArmLeft01.xRot = -0.6f
            ArmLeft01.zRot = 0.2618f
            ArmRight01.xRot = -0.6f
            ArmRight01.zRot = -0.2618f
            legLeftX -= 0.55f
            legRightX -= 0.55f
        }

        if (ridingState > 0) {
            Head.yRot *= 0.5f
            Head.zRot = 0.0f
            if (isLegacyEmote1) {
                ArmLeft01.xRot = 0.1f
                ArmLeft01.yRot = 0.0f
                ArmLeft01.zRot = -0.4f
                ArmLeft02.zRot = 0.8f
                ArmRight01.xRot = 0.1f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = 0.4f
                ArmRight02.zRot = -0.8f
            } else {
                ArmLeft01.xRot = -0.8f
                ArmLeft01.yRot = -1.5f
                ArmLeft01.zRot = 0.0f
                ArmLeft02.zRot = 1.45f
                ArmRight01.xRot = -0.8f
                ArmRight01.yRot = 1.5f
                ArmRight01.zRot = 0.0f
                ArmRight02.zRot = -1.45f
            }
            EquipBase.visible = false
            EquipTL03.visible = false
            EquipTR03.visible = false
            EquipC12.visible = false
            EquipSL01.visible = false
            EquipSL01f.visible = false

            if (entity != null && entity.getIsSitting()) {
                this.poseTranslateY = 0.525f * 3.0f
                this.setFace(EntityShipBase.FACE_DOT_EYES)
                Head.xRot = -1.1f
                Head.yRot = 0.0f
                Head.zRot = 0.0f
                BodyMain.xRot = 1.4f
                Hair01.xRot -= 0.3f
                legLeftX = -0.1f
                legRightX = 0.0f
                LegLeft01.yRot = 0.0f
                LegLeft01.zRot = 0.2f
                LegRight01.yRot = 0.0f
                LegRight01.zRot = -0.2f
                LegRight02.xRot = 0.3f

                val tickMod = entity.tickCount % 128
                if (tickMod < 64) {
                    val armx = Mth.cos(ageInTicks * 0.6f) * -0.5f
                    this.setFace(EntityShipBase.FACE_TENSION)
                    ArmLeft01.xRot = armx - 3.3f
                    ArmLeft01.yRot = 0.0f
                    ArmLeft01.zRot = 0.7f
                    ArmLeft02.zRot = 0.0f
                    ArmRight01.xRot = -armx - 3.3f
                    ArmRight01.yRot = 0.0f
                    ArmRight01.zRot = -0.7f
                    ArmRight02.zRot = 0.0f
                } else {
                    ArmLeft01.xRot = -2.8f
                    ArmLeft01.yRot = 0.0f
                    ArmLeft01.zRot = 0.7f
                    ArmLeft02.zRot = 1.0f
                    ArmRight01.xRot = -2.8f
                    ArmRight01.yRot = 0.0f
                    ArmRight01.zRot = -0.7f
                    ArmRight02.zRot = -1.0f
                }

                ArmLeft02.x = armLeft02DefaultX
                ArmLeft02.y = armLeft02DefaultY
                ArmLeft02.z = armLeft02DefaultZ
                ArmRight02.x = armRight02DefaultX
                ArmRight02.y = armRight02DefaultY
                ArmRight02.z = armRight02DefaultZ
            }
        } else if (ctx.isSitting || isPassenger) {
            if (isLegacyEmote1) {
                this.poseTranslateY = 0.675f * 2.5f
                this.setFace(EntityShipBase.FACE_DOT_EYES)
                Head.xRot = -1.1f
                Head.yRot = 0.0f
                Head.zRot = 0.0f
                BodyMain.xRot = 1.4f
                Hair01.xRot -= 0.3f
                legLeftX = -0.1f
                legRightX = 0.0f
                LegLeft01.yRot = 0.0f
                LegLeft01.zRot = 0.2f
                LegRight01.yRot = 0.0f
                LegRight01.zRot = -0.2f
                LegRight02.xRot = 0.3f

                val tickMod = if (entity != null) entity.tickCount % 80 else 0
                if (tickMod < 40) {
                    val armx = Mth.cos(ageInTicks * 0.6f) * -0.5f
                    this.setFace(EntityShipBase.FACE_TENSION)
                    ArmLeft01.xRot = armx - 3.3f
                    ArmLeft01.yRot = 0.0f
                    ArmLeft01.zRot = 0.7f
                    ArmRight01.xRot = -armx - 3.3f
                    ArmRight01.yRot = 0.0f
                    ArmRight01.zRot = -0.7f
                    ArmLeft02.zRot = 0.0f
                    ArmRight02.zRot = 0.0f
                } else {
                    ArmLeft01.xRot = -2.8f
                    ArmLeft01.yRot = 0.0f
                    ArmLeft01.zRot = 0.7f
                    ArmRight01.xRot = -2.8f
                    ArmRight01.yRot = 0.0f
                    ArmRight01.zRot = -0.7f
                    ArmLeft02.zRot = 1.0f
                    ArmRight02.zRot = -1.0f
                }
                ArmLeft02.x = armLeft02DefaultX
                ArmRight02.x = armRight02DefaultX
            } else {
                this.poseTranslateY = 0.375f * 2.8f
                BodyMain.xRot = -0.25f
                Butt.xRot = -0.2f
                Skirt01.xRot = -0.07f
                Skirt02.xRot = -0.16f

                ArmLeft01.xRot = 0.35f
                ArmLeft01.zRot = -0.2618f
                ArmRight01.xRot = 0.35f
                ArmRight01.zRot = 0.2618f

                legLeftX = -0.9f
                legRightX = -0.9f
                LegLeft01.yRot = 0.0f
                LegLeft01.zRot = -0.14f
                LegLeft02.xRot = 1.2217f
                LegLeft02.yRot = 1.2217f
                LegLeft02.zRot = -1.0472f
                LegLeft02.x = legLeft02DefaultX + (0.32f * OFFSET_SCALE)
                LegLeft02.y = legLeft02DefaultY + (0.05f * OFFSET_SCALE)
                LegLeft02.z = legLeft02DefaultZ + (0.35f * OFFSET_SCALE)

                LegRight01.yRot = 0.0f
                LegRight01.zRot = 0.14f
                LegRight02.xRot = 1.2217f
                LegRight02.yRot = -1.2217f
                LegRight02.zRot = 1.0472f
                LegRight02.x = legRight02DefaultX + (-0.32f * OFFSET_SCALE)
                LegRight02.y = legRight02DefaultY + (0.05f * OFFSET_SCALE)
                LegRight02.z = legRight02DefaultZ + (0.35f * OFFSET_SCALE)
            }
        }

        if (entity != null && entity.attackTick > 30) {
            this.setFace(EntityShipBase.FACE_TENSION)
            ArmLeft01.xRot = -1.55f
            ArmLeft01.yRot = 0.3f
            ArmLeft01.zRot = 0.0f
            ArmLeft02.xRot = 0.0f
            ArmLeft02.zRot = 0.7f
            ArmRight01.xRot = -1.7f
            ArmRight01.yRot = -0.1f
            ArmRight01.zRot = 1.5f
            ArmRight02.xRot = 0.0f
            ArmRight02.zRot = 0.0f
        }

        if (isLegacyEmote6) {
            this.setFace(EntityShipBase.FACE_WINK)
            Head.xRot += 0.6f
            Head.yRot = 0.0f
            Head.zRot = 0.0f
            ArmLeft01.xRot = -2.4f
            ArmLeft01.yRot = 0.0f
            ArmLeft01.zRot = 0.5f
            ArmLeft02.zRot = 0.9f
            ArmRight01.xRot = -2.4f
            ArmRight01.yRot = 0.0f
            ArmRight01.zRot = -0.5f
            ArmRight02.zRot = -0.9f
            EquipTL03.visible = false
            EquipTR03.visible = false
            EquipC12.visible = false
        }

        LegLeft01.xRot = legLeftX
        LegRight01.xRot = legRightX

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

    private fun applyHairAnimation(ctx: PoseContext, ageInTicks: Float, limbSwing: Float) {
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f)
        val headX = Head.xRot * -0.5f
        val headZ = Head.zRot * -0.5f

        HairL01.zRot += headZ
        HairL02.zRot += headZ
        HairR01.zRot += headZ
        HairR02.zRot += headZ
        HairL01.xRot += ctx.angleX * 0.04f + headX
        HairL02.xRot += -angleX1 * 0.07f + headX
        HairR01.xRot += ctx.angleX * 0.04f + headX
        HairR02.xRot += -angleX1 * 0.07f + headX

        Hair01.xRot += headX
        Hair01.zRot += headZ

        if (Hair02a1 != null) {
            Hair02a1.xRot += headX
            Hair02a1.zRot += headZ
            Hair02a2.xRot += headX * 0.5f
            Hair02a2.zRot += headZ * 0.5f
        }
        if (Hair02b1 != null) {
            Hair02b1.xRot += headX
            Hair02b1.zRot += headZ
            Hair02b2.xRot += headX * 0.5f
            Hair02b2.zRot += headZ * 0.5f
        }
        if (Hair02c1 != null) {
            Hair02c1.xRot += headX
            Hair02c1.zRot += headZ
            Hair02c2.xRot += headX * 0.5f
            Hair02c2.zRot += headZ * 0.5f
        }
        if (Hair02d1 != null) {
            Hair02d1.xRot += headX
            Hair02d1.zRot += headZ
            Hair02d2.xRot += headX * 0.5f
            Hair02d2.zRot += headZ * 0.5f
        }
        if (Hair02e1 != null) {
            Hair02e1.xRot += headX
            Hair02e1.zRot += headZ
            Hair02e2.xRot += headX * 0.5f
            Hair02e2.zRot += headZ * 0.5f
        }
    }

    private fun syncGlowParts() {
        if (this.GlowBodyMain != null) {
            GlowBodyMain.copyFrom(BodyMain)
            GlowHead.copyFrom(Head)
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

        if (GlowBodyMain != null) {
            GlowBodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        }

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "destroyer_akatsuki"), "main")

        private val DEAD_TRANSLATE_Y = deadY("ModelDestroyerAkatsuki")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelDestroyerAkatsuki")
        private const val OFFSET_SCALE = 16.0f

        private val BODY_BASE_X_ROT = -0.1047f
        private const val HEAD_BASE_X_ROT = 0.1047f
        private const val BUTT_BASE_X_ROT = 0.21f
        private val SKIRT_BASE_X_ROT = -0.052f
        private val ARM_BASE_X_ROT = -0.1745f
        private const val ARM_BASE_Z_ROT = 0.2618f
        private const val AHOKE_BASE_Y_ROT = 0.5236f

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 105).addBox(-6.5f, -11f, -4f, 13f, 14f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -9f, 0f, -0.1047f, 0f, 0f)
            )

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(84, 31).addBox(-7f, 0f, -4.4f, 14f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.6f, 0f)
            )

            val Cloth02 = Cloth01.addOrReplaceChild(
                "Cloth02",
                CubeListBuilder.create().texOffs(24, 73).addBox(-3f, 0f, 0f, 6f, 10f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 4.8f, -4.3f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(0, 88).addBox(-3.5f, -1f, -3f, 6f, 11f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.3f, -9.4f, -0.7f, -0.0524f, 0f, 0.4189f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(24, 88).addBox(0f, 0f, -6f, 6f, 8f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3.5f, 10f, 3f)
            )

            val ArmRight03 = ArmRight02.addOrReplaceChild(
                "ArmRight03",
                CubeListBuilder.create().texOffs(36, 102).addBox(-2.5f, 0f, -2.5f, 5f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 6f, -3f)
            )

            val EquipTR03 = ArmRight02.addOrReplaceChild(
                "EquipTR03",
                CubeListBuilder.create().texOffs(36, 45).addBox(-1f, -12f, -3.5f, 1f, 24f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.5f, 2f, 3f, 0.3491f, 0.3491f, 0.0873f)
            )

            val EquipC12 = ArmRight02.addOrReplaceChild(
                "EquipC12",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, -3f, -4.5f, 7f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, -3f, 1.5708f, 1.5708f, 0f)
            )

            val EquipC13 = EquipC12.addOrReplaceChild(
                "EquipC13",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, 0f, 0f, 6f, 2f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, -5f, -2f)
            )

            val EquipC15a = EquipC12.addOrReplaceChild(
                "EquipC15a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -6f, 2f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offset(1.5f, -3f, 0f)
            )

            val EquipC15b = EquipC15a.addOrReplaceChild(
                "EquipC15b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, -0.5f, -10f, 1f, 1f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -6f)
            )

            val EquipC14a = EquipC12.addOrReplaceChild(
                "EquipC14a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -6f, 2f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offset(-1.5f, -3f, 0f)
            )

            val EquipC14b = EquipC14a.addOrReplaceChild(
                "EquipC14b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, -0.5f, -10f, 1f, 1f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -6f)
            )

            val EquipBase = BodyMain.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, 0f)
            )

            val EquipC01 = EquipBase.addOrReplaceChild(
                "EquipC01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, 0f, -2f, 4f, 8f, 4f, CubeDeformation(0f)),
                PartPose.offset(-7f, -11f, 9f)
            )

            val EquipC02 = EquipC01.addOrReplaceChild(
                "EquipC02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, -3f, -3.5f, 7f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, 0.5f, 0f, -0.1745f, 0.6283f, 0f)
            )

            val EquipC04a = EquipC02.addOrReplaceChild(
                "EquipC04a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -6f, 2f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offset(-1.5f, -3f, 0f)
            )

            val EquipC04b = EquipC04a.addOrReplaceChild(
                "EquipC04b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, -0.5f, -10f, 1f, 1f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -6f)
            )

            val EquipC03 = EquipC02.addOrReplaceChild(
                "EquipC03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, 0f, 0f, 6f, 2f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, -5f, -2f)
            )

            val EquipC05a = EquipC02.addOrReplaceChild(
                "EquipC05a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -6f, 2f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offset(1.5f, -3f, 0f)
            )

            val EquipC05b = EquipC05a.addOrReplaceChild(
                "EquipC05b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, -0.5f, -10f, 1f, 1f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -6f)
            )

            val EquipMain01 = EquipBase.addOrReplaceChild(
                "EquipMain01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.5f, -1f, 0f, 11f, 9f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, -4f, 5f)
            )

            val EquipMain03 = EquipMain01.addOrReplaceChild(
                "EquipMain03",
                CubeListBuilder.create().texOffs(63, 13).addBox(-1f, 0f, -1.5f, 2f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9.5f, 9f, 0.5009f, 0f, 0f)
            )

            val EquipHead01 = EquipMain03.addOrReplaceChild(
                "EquipHead01",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -1.5f, -12f, 2f, 3f, 18f, CubeDeformation(0f)),
                PartPose.offset(0f, 6.5f, -0.5f)
            )

            val EquipHead02 = EquipHead01.addOrReplaceChild(
                "EquipHead02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, -7f, 0f, 3f, 14f, 3f, CubeDeformation(0f)),
                PartPose.offset(1f, 0f, -15f)
            )

            val EquipHead04 = EquipHead02.addOrReplaceChild(
                "EquipHead04",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -4.8f, 2.5f, 0.2618f, 0f, 0f)
            )

            val EquipHead03 = EquipHead02.addOrReplaceChild(
                "EquipHead03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4.8f, 2.5f, -0.2618f, 0f, 0f)
            )

            val EquipHead05 = EquipHead02.addOrReplaceChild(
                "EquipHead05",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -5f, 0f, 2f, 10f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -2f)
            )

            val EquipTL02 = EquipMain01.addOrReplaceChild(
                "EquipTL02",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -4f, -9f, 3f, 11f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.5f, 6f, 4.5f, 0.1396f, -0.0698f, 0f)
            )

            val EquipTL02g = EquipTL02.addOrReplaceChild(
                "EquipTL02g",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 11f, CubeDeformation(0f)),
                PartPose.offset(1.3f, 4.6f, -19f)
            )

            val EquipTL02e = EquipTL02.addOrReplaceChild(
                "EquipTL02e",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(1.3f, 0f, 2.2f)
            )

            val EquipTL02c = EquipTL02.addOrReplaceChild(
                "EquipTL02c",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 11f, CubeDeformation(0f)),
                PartPose.offset(1.3f, 2.3f, -19.5f)
            )

            val EquipTL02f = EquipTL02.addOrReplaceChild(
                "EquipTL02f",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(1.3f, 2.3f, 2.5f)
            )

            val EquipTL02d = EquipTL02.addOrReplaceChild(
                "EquipTL02d",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(1.3f, -2.3f, 3f)
            )

            val EquipTL02a = EquipTL02.addOrReplaceChild(
                "EquipTL02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 11f, CubeDeformation(0f)),
                PartPose.offset(1.3f, 0f, -19.8f)
            )

            val EquipTL02b = EquipTL02.addOrReplaceChild(
                "EquipTL02b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offset(1.3f, -2.3f, -18.8f)
            )

            val EquipTL02h = EquipTL02.addOrReplaceChild(
                "EquipTL02h",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(1.3f, 4.6f, 2.8f)
            )

            val EquipTR02 = EquipMain01.addOrReplaceChild(
                "EquipTR02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, -4f, -9f, 3f, 11f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5.5f, 6f, 4.5f, 0.1396f, 0.0698f, 0f)
            )

            val EquipTR02c = EquipTR02.addOrReplaceChild(
                "EquipTR02c",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 11f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, 2.3f, -19.5f)
            )

            val EquipTR02b = EquipTR02.addOrReplaceChild(
                "EquipTR02b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, -2.3f, -18.8f)
            )

            val EquipTR02h = EquipTR02.addOrReplaceChild(
                "EquipTR02h",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, 4.6f, 2.8f)
            )

            val EquipTR02e = EquipTR02.addOrReplaceChild(
                "EquipTR02e",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, 0f, 2.2f)
            )

            val EquipTR02f = EquipTR02.addOrReplaceChild(
                "EquipTR02f",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, 2.3f, 2.5f)
            )

            val EquipTR02d = EquipTR02.addOrReplaceChild(
                "EquipTR02d",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, -2.3f, 3f)
            )

            val EquipTR02a = EquipTR02.addOrReplaceChild(
                "EquipTR02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 11f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, 0f, -19.8f)
            )

            val EquipTR02g = EquipTR02.addOrReplaceChild(
                "EquipTR02g",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, 4.6f, -19f)
            )

            val EquipMain02 = EquipMain01.addOrReplaceChild(
                "EquipMain02",
                CubeListBuilder.create().texOffs(52, 8).addBox(-4f, 0f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6.9f, 1.2f, 0.6283f, 0f, 0f)
            )

            val EquipMain04 = EquipMain01.addOrReplaceChild(
                "EquipMain04",
                CubeListBuilder.create().texOffs(0, 26).addBox(-3f, 0f, -3f, 6f, 16f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -16.5f, 9f, -0.0873f, 0f, 0f)
            )

            val Head = BodyMain.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -11.8f, -1f, 0.1047f, 0f, 0f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(46, 104).addBox(-7.5f, 0f, 0f, 15f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.8f, -2.8f)
            )

            val HatBase = HairMain.addOrReplaceChild(
                "HatBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2.6f, 4.5f, -0.0698f, 0.182f, 0f)
            )

            val Hat03a = HatBase.addOrReplaceChild(
                "Hat03a",
                CubeListBuilder.create().texOffs(23, 43).mirror().addBox(0f, -4f, -5f, 5f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.3f, 2f, 0f, -0.1396f, 0f, 0f)
            )

            val Hat01b = HatBase.addOrReplaceChild(
                "Hat01b",
                CubeListBuilder.create().texOffs(46, 0).addBox(0f, 0f, 0f, 6f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offset(-0.7f, 0f, 0f)
            )

            val Hat01c = HatBase.addOrReplaceChild(
                "Hat01c",
                CubeListBuilder.create().texOffs(46, 0).addBox(-6f, 0f, 0f, 6f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offset(0.7f, 0f, 0f)
            )

            val Hat01d = HatBase.addOrReplaceChild(
                "Hat01d",
                CubeListBuilder.create().texOffs(46, 0).addBox(-6f, 0f, -6f, 6f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offset(0.7f, 0f, 0f)
            )

            val Hat01a = HatBase.addOrReplaceChild(
                "Hat01a",
                CubeListBuilder.create().texOffs(46, 0).addBox(0f, 0f, -6f, 6f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offset(-0.7f, 0f, 0f)
            )

            val Hat02a = HatBase.addOrReplaceChild(
                "Hat02a",
                CubeListBuilder.create().texOffs(55, 0).addBox(-4.5f, 0f, -6f, 9f, 0f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, -6f, 0.0873f, 0f, 0f)
            )

            val Hat03c = HatBase.addOrReplaceChild(
                "Hat03c",
                CubeListBuilder.create().texOffs(23, 43).addBox(0f, -4f, 0f, 5f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, 2f, 0f)
            )

            val Hat03d = HatBase.addOrReplaceChild(
                "Hat03d",
                CubeListBuilder.create().texOffs(23, 43).addBox(-5f, -4f, 0f, 5f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offset(0.5f, 2f, 0f)
            )

            val Hat03b = HatBase.addOrReplaceChild(
                "Hat03b",
                CubeListBuilder.create().texOffs(23, 43).addBox(-5f, -4f, -5f, 5f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.3f, 2f, 0f, -0.1396f, 0f, 0f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(38, 23).addBox(-7.5f, 0f, -10f, 15f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 12f, 0.1396f, 0f, 0f)
            )

            val Hair02b1 = Hair01.addOrReplaceChild(
                "Hair02b1",
                CubeListBuilder.create().texOffs(24, 26).addBox(-2f, 0f, 0f, 4f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, 7f, -2.4f, 0.2094f, -0.1745f, 0.1745f)
            )

            val Hair02b2 = Hair02b1.addOrReplaceChild(
                "Hair02b2",
                CubeListBuilder.create().texOffs(24, 59).addBox(-2f, 0f, 0f, 4f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0.1396f, 0f, 0f)
            )

            val Hair02a1 = Hair01.addOrReplaceChild(
                "Hair02a1",
                CubeListBuilder.create().texOffs(24, 26).addBox(-2f, 0f, 0f, 4f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10f, -2.2f, 0.2094f, 0f, 0f)
            )

            val Hair02a2 = Hair02a1.addOrReplaceChild(
                "Hair02a2",
                CubeListBuilder.create().texOffs(24, 32).addBox(-2f, 0f, 0f, 4f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0.1396f, 0f, 0f)
            )

            val Hair02e1 = Hair01.addOrReplaceChild(
                "Hair02e1",
                CubeListBuilder.create().texOffs(24, 22).addBox(0f, 0f, -2f, 0f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.4f, 1f, -5.5f, 0.2618f, 0f, -0.4363f)
            )

            val Hair02e2 = Hair02e1.addOrReplaceChild(
                "Hair02e2",
                CubeListBuilder.create().texOffs(24, 62).mirror().addBox(0f, 0f, -2f, 0f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0f, 0f, 0.2618f)
            )

            val Hair02c1 = Hair01.addOrReplaceChild(
                "Hair02c1",
                CubeListBuilder.create().texOffs(24, 26).addBox(-2f, 0f, 0f, 4f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.2f, 6f, -2.4f, 0.1396f, 0.1745f, -0.1745f)
            )

            val Hair02c2 = Hair02c1.addOrReplaceChild(
                "Hair02c2",
                CubeListBuilder.create().texOffs(24, 66).addBox(-2f, 0f, 0f, 4f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0.1396f, 0f, 0f)
            )

            val Hair02d1 = Hair01.addOrReplaceChild(
                "Hair02d1",
                CubeListBuilder.create().texOffs(24, 22).addBox(0f, 0f, -2f, 0f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.4f, 1f, -5.5f, 0.2618f, 0f, 0.3491f)
            )

            val Hair02d2 = Hair02d1.addOrReplaceChild(
                "Hair02d2",
                CubeListBuilder.create().texOffs(24, 62).mirror().addBox(0f, 0f, -2f, 0f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0f, 0f, -0.1745f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 81).addBox(-8f, -8f, -7.4f, 16f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0.3f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(89, 102).addBox(-0.5f, 0f, 0f, 1f, 9f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, 2f, -6.7f, -0.0524f, -0.0873f, 0.1396f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(88, 104).addBox(-0.5f, 0f, 0f, 1f, 7f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.1f, 7.5f, 0f, 0.0873f, 0f, 0.0873f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(89, 102).addBox(-0.5f, 0f, 0f, 1f, 9f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8f, 2f, -6.7f, -0.0524f, 0.0873f, -0.1396f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(88, 104).addBox(-0.5f, 0f, 0f, 1f, 7f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.1f, 7.5f, 0f, 0.0873f, 0f, -0.0873f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(0, 37).addBox(0f, -11f, -7f, 0f, 11f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.5f, -4.4f, -5.3f, 1.9199f, 1.0472f, 0f)
            )

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(52, 45).addBox(-8.5f, 0f, 0f, 17f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -6.2f, -7.1f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(0, 88).mirror()
                    .addBox(-2.5f, -1f, -3f, 6f, 11f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.3f, -9.4f, -0.7f, 0.1745f, 0f, -0.4189f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(24, 88).mirror().addBox(-6f, 0f, -6f, 6f, 8f, 6f, CubeDeformation(0f)),
                PartPose.offset(3.5f, 10f, 3f)
            )

            val ArmLeft03 = ArmLeft02.addOrReplaceChild(
                "ArmLeft03",
                CubeListBuilder.create().texOffs(36, 102).mirror()
                    .addBox(-2.5f, 0f, -2.5f, 5f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 6f, -3f)
            )

            val EquipTL03 = ArmLeft02.addOrReplaceChild(
                "EquipTL03",
                CubeListBuilder.create().texOffs(36, 45).addBox(0f, -12f, -3.5f, 1f, 24f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.5f, 4f, -3f, -1.2217f, -0.1047f, -0.0873f)
            )

            val EquipSL01 = BodyMain.addOrReplaceChild(
                "EquipSL01",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 4f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offset(7f, -14f, 0f)
            )

            val EquipSL01a = EquipSL01.addOrReplaceChild(
                "EquipSL01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 5f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, -5f, -1f)
            )

            val EquipSL01b = EquipSL01.addOrReplaceChild(
                "EquipSL01b",
                CubeListBuilder.create().texOffs(28, 8).addBox(0f, 0f, 0f, 1f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, -4f, -2f)
            )

            val EquipSL01c = EquipSL01.addOrReplaceChild(
                "EquipSL01c",
                CubeListBuilder.create().texOffs(6, 15).addBox(0f, 0f, 0f, 1f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offset(4f, -4f, -2f)
            )

            val EquipSL01d = EquipSL01.addOrReplaceChild(
                "EquipSL01d",
                CubeListBuilder.create().texOffs(12, 3).addBox(0f, 0f, 0f, 3f, 1f, 1f, CubeDeformation(0f)),
                PartPose.offset(1f, -5f, -2f)
            )

            val EquipSL01e = EquipSL01.addOrReplaceChild(
                "EquipSL01e",
                CubeListBuilder.create().texOffs(29, 5).addBox(0f, 0f, 0f, 3f, 1f, 1f, CubeDeformation(0f)),
                PartPose.offset(1f, -1f, -2f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(54, 66).addBox(-7f, 0f, 0f, 14f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3f, -4f, 0.2094f, 0f, 0f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 61).addBox(-3f, 0f, -3f, 6f, 12f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.4f, 5.5f, 3.2f, -0.0524f, 0f, -0.1047f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 72).addBox(-6f, 0f, 0f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(3f, 12f, -3f)
            )

            val LegRight03 = LegRight02.addOrReplaceChild(
                "LegRight03",
                CubeListBuilder.create().texOffs(30, 76).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 5f, 7f, CubeDeformation(0f)),
                PartPose.offset(-3f, 8f, 2.9f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 61).mirror().addBox(-3f, 0f, -3f, 6f, 12f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.4f, 5.5f, 3.2f, -0.1396f, 0f, 0.1047f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 72).mirror().addBox(0f, 0f, 0f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3f, 12f, -3f)
            )

            val LegLeft03 = LegLeft02.addOrReplaceChild(
                "LegLeft03",
                CubeListBuilder.create().texOffs(30, 76).addBox(-3.5f, 0f, -3.5f, 7f, 5f, 7f, CubeDeformation(0f)),
                PartPose.offset(3f, 8f, 2.9f)
            )

            val Skirt01 = Butt.addOrReplaceChild(
                "Skirt01",
                CubeListBuilder.create().texOffs(80, 16).addBox(-7.5f, 0f, 0f, 15f, 6f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1.7f, -0.4f, -0.0524f, 0f, 0f)
            )

            val Skirt02 = Skirt01.addOrReplaceChild(
                "Skirt02",
                CubeListBuilder.create().texOffs(76, 0).addBox(-8f, 0f, 0f, 16f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3.5f, -0.4f, -0.0524f, 0f, 0f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -9f, 0f)
            )

            val GlowHead = GlowBodyMain.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -11.8f, -1f)
            )
            addFaceLayer(GlowHead)

            val EquipSL01f = GlowBodyMain.addOrReplaceChild(
                "EquipSL01f",
                CubeListBuilder.create().texOffs(43, 26).addBox(0f, 0f, 0f, 3f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offset(8f, -18f, -2f)
            )

            val LightEF01 = EquipSL01f.addOrReplaceChild(
                "LightEF01",
                CubeListBuilder.create().texOffs(47, 27).addBox(0f, 0f, 0f, 16f, 3f, 0f, CubeDeformation(0f)),
                PartPose.offset(3f, 0f, -0.1f)
            )

            val LightEF01a = EquipSL01f.addOrReplaceChild(
                "LightEF01a",
                CubeListBuilder.create().texOffs(47, 27).addBox(0f, 0f, 0f, 16f, 3f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -0.1f, 0f, 0f, -1.5708f)
            )

            val LightEF01b = EquipSL01f.addOrReplaceChild(
                "LightEF01b",
                CubeListBuilder.create().texOffs(47, 27).addBox(0f, 0f, 0f, 16f, 3f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3f, 3f, -0.1f, 0f, 0f, 1.5708f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
