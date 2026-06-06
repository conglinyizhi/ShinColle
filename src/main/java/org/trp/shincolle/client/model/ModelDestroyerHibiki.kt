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
import org.trp.shincolle.entity.EntityDestroyerHibiki
import org.trp.shincolle.entity.IShipRiderType
import org.trp.shincolle.entity.base.EntityShipBase
import kotlin.math.min

class ModelDestroyerHibiki<T : EntityShipBase>(root: ModelPart) : ShipModelHumanoidBase<T>(), IGlowableModel {
    private var isDeadPose = false
    override var poseTranslateY = 0f

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
    private val Hair02f1: ModelPart
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
    private val Hat02b: ModelPart
    private val HatBase2: ModelPart
    private val Hat201_01: ModelPart
    private val Hat201_02: ModelPart
    private val Hat201_03: ModelPart
    private val Hat201_04: ModelPart
    private val Hat201_05: ModelPart
    private val Hat201_06: ModelPart
    private val Hat201_07: ModelPart
    private val Hat201_08: ModelPart
    private val Hat201_09: ModelPart
    private val Hat201_10: ModelPart
    private val Hat201_11: ModelPart
    private val Hat201_12: ModelPart
    private val Hat202a: ModelPart
    private val Hat202b: ModelPart
    private val Hair02f2: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmLeft03: ModelPart
    private val EquipTL03: ModelPart
    private val ArmRight02: ModelPart
    private val ArmRight03: ModelPart
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
    private val EquipTR02a: ModelPart
    private val EquipTR02b: ModelPart
    private val EquipTR02c: ModelPart
    private val EquipTR02d: ModelPart
    private val EquipTR02e: ModelPart
    private val EquipTR02f: ModelPart
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
    private val hatBase2DefaultY: Float
    private val hatBase2DefaultZ: Float
    private val hatBase2DefaultXRot: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.Cloth02 = this.Cloth01.getChild("Cloth02")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.LegLeft03 = this.LegLeft02.getChild("LegLeft03")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.Skirt02 = this.Skirt01.getChild("Skirt02")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.LegRight03 = this.LegRight02.getChild("LegRight03")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.ArmLeft03 = this.ArmLeft02.getChild("ArmLeft03")
        this.EquipTL03 = this.ArmLeft02.getChild("EquipTL03")
        this.EquipBase = this.BodyMain.getChild("EquipBase")
        this.EquipMain01 = this.EquipBase.getChild("EquipMain01")
        this.EquipMain04 = this.EquipMain01.getChild("EquipMain04")
        this.EquipMain03 = this.EquipMain01.getChild("EquipMain03")
        this.EquipHead01 = this.EquipMain03.getChild("EquipHead01")
        this.EquipHead02 = this.EquipHead01.getChild("EquipHead02")
        this.EquipHead04 = this.EquipHead02.getChild("EquipHead04")
        this.EquipHead05 = this.EquipHead02.getChild("EquipHead05")
        this.EquipHead03 = this.EquipHead02.getChild("EquipHead03")
        this.EquipTR02 = this.EquipMain01.getChild("EquipTR02")
        this.EquipTR02f = this.EquipTR02.getChild("EquipTR02f")
        this.EquipTR02a = this.EquipTR02.getChild("EquipTR02a")
        this.EquipTR02e = this.EquipTR02.getChild("EquipTR02e")
        this.EquipTR02d = this.EquipTR02.getChild("EquipTR02d")
        this.EquipTR02b = this.EquipTR02.getChild("EquipTR02b")
        this.EquipTR02c = this.EquipTR02.getChild("EquipTR02c")
        this.EquipMain02 = this.EquipMain01.getChild("EquipMain02")
        this.EquipTL02 = this.EquipMain01.getChild("EquipTL02")
        this.EquipTL02d = this.EquipTL02.getChild("EquipTL02d")
        this.EquipTL02b = this.EquipTL02.getChild("EquipTL02b")
        this.EquipTL02e = this.EquipTL02.getChild("EquipTL02e")
        this.EquipTL02a = this.EquipTL02.getChild("EquipTL02a")
        this.EquipTL02c = this.EquipTL02.getChild("EquipTL02c")
        this.EquipTL02f = this.EquipTL02.getChild("EquipTL02f")
        this.EquipC01 = this.EquipBase.getChild("EquipC01")
        this.EquipC02 = this.EquipC01.getChild("EquipC02")
        this.EquipC03 = this.EquipC02.getChild("EquipC03")
        this.EquipC05a = this.EquipC02.getChild("EquipC05a")
        this.EquipC05b = this.EquipC05a.getChild("EquipC05b")
        this.EquipC04a = this.EquipC02.getChild("EquipC04a")
        this.EquipC04b = this.EquipC04a.getChild("EquipC04b")
        this.Head = this.BodyMain.getChild("Head")
        this.HairMain = this.Head.getChild("HairMain")
        this.HatBase = this.HairMain.getChild("HatBase")
        this.Hat03c = this.HatBase.getChild("Hat03c")
        this.Hat01a = this.HatBase.getChild("Hat01a")
        this.Hat01b = this.HatBase.getChild("Hat01b")
        this.Hat01c = this.HatBase.getChild("Hat01c")
        this.Hat03d = this.HatBase.getChild("Hat03d")
        this.Hat02b = this.HatBase.getChild("Hat02b")
        this.Hat01d = this.HatBase.getChild("Hat01d")
        this.Hat03a = this.HatBase.getChild("Hat03a")
        this.Hat03b = this.HatBase.getChild("Hat03b")
        this.Hat02a = this.HatBase.getChild("Hat02a")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02b1 = this.Hair01.getChild("Hair02b1")
        this.Hair02b2 = this.Hair02b1.getChild("Hair02b2")
        this.Hair02a1 = this.Hair01.getChild("Hair02a1")
        this.Hair02a2 = this.Hair02a1.getChild("Hair02a2")
        this.Hair02e1 = this.Hair01.getChild("Hair02e1")
        this.Hair02e2 = this.Hair02e1.getChild("Hair02e2")
        this.Hair02d1 = this.Hair01.getChild("Hair02d1")
        this.Hair02d2 = this.Hair02d1.getChild("Hair02d2")
        this.Hair02c1 = this.Hair01.getChild("Hair02c1")
        this.Hair02c2 = this.Hair02c1.getChild("Hair02c2")
        this.Hair02f1 = this.HairMain.getChild("Hair02f1")
        this.Hair02f2 = this.Hair02f1.getChild("Hair02f2")
        this.Hair = this.Head.getChild("Hair")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.ArmRight03 = this.ArmRight02.getChild("ArmRight03")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowHead = this.GlowBodyMain.getChild("GlowHead")
        this.initFaceParts(this.GlowHead)
        this.HatBase2 = this.GlowHead.getChild("HatBase2")
        this.Hat201_01 = this.HatBase2.getChild("Hat201_01")
        this.Hat201_02 = this.HatBase2.getChild("Hat201_02")
        this.Hat201_03 = this.HatBase2.getChild("Hat201_03")
        this.Hat201_04 = this.HatBase2.getChild("Hat201_04")
        this.Hat201_05 = this.HatBase2.getChild("Hat201_05")
        this.Hat201_06 = this.HatBase2.getChild("Hat201_06")
        this.Hat201_07 = this.HatBase2.getChild("Hat201_07")
        this.Hat201_08 = this.HatBase2.getChild("Hat201_08")
        this.Hat201_09 = this.HatBase2.getChild("Hat201_09")
        this.Hat201_10 = this.HatBase2.getChild("Hat201_10")
        this.Hat201_11 = this.HatBase2.getChild("Hat201_11")
        this.Hat201_12 = this.HatBase2.getChild("Hat201_12")
        this.Hat202a = this.HatBase2.getChild("Hat202a")
        this.Hat202b = this.HatBase2.getChild("Hat202b")
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
        this.hatBase2DefaultY = this.HatBase2.y
        this.hatBase2DefaultZ = this.HatBase2.z
        this.hatBase2DefaultXRot = this.HatBase2.xRot
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

        applyFaceAndMouth(entity)
        if (entity != null && entity.emotionPrimary == EntityShipBase.EMOTION_NORMAL) {
            hideMouthParts()
        }
        setFlushVisible(entity != null && (entity.emotionPrimary == EntityShipBase.EMOTION_SHY || entity.emotionPrimary == EntityShipBase.EMOTION_HAPPY))
        applyEquipVisibility(entity)

        if (entity != null && entity.isInDeadPose) {
            applyDeadPose()
            syncGlowParts()
            return
        }

        applyHeadRotation(Head, entity, ageInTicks, netHeadYaw, headPitch)
        Head.xRot += 0.11f

        applyBasePose(ctx)
        applySpecialPoseAdjustments(entity, ctx, ageInTicks)
        applyHairAnimation(ctx, ageInTicks, limbSwing)

        syncGlowParts()
    }

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) {
            return
        }
        EquipBase.visible = entity.getEquipFlag(EntityDestroyerHibiki.EQUIP_RIGGING)
        EquipTL03.visible = entity.getEquipFlag(EntityDestroyerHibiki.EQUIP_TORPEDO)
        val fh1 = entity.getEquipFlag(EntityDestroyerHibiki.EQUIP_HAIR_FRONT_1)
        val fh2 = entity.getEquipFlag(EntityDestroyerHibiki.EQUIP_HAIR_FRONT_2)
        val fh3 = entity.getEquipFlag(EntityDestroyerHibiki.EQUIP_HAIR_FRONT_3)

        HatBase2.xRot = hatBase2DefaultXRot
        HatBase2.y = hatBase2DefaultY
        HatBase2.z = hatBase2DefaultZ

        if (fh1) {
            HatBase.visible = true
            Hair02f1.visible = true
            Hair01.visible = true
            HatBase2.visible = false
        } else if (fh2 && fh3) {
            HatBase.visible = false
            Hair01.visible = true
            Hair02f1.visible = true
            HatBase2.visible = true
            HatBase2.xRot = -1.35f
            HatBase2.z = hatBase2DefaultZ + (0.1f * OFFSET_SCALE)
        } else if (fh2) {
            HatBase.visible = false
            Hair02f1.visible = false
            Hair01.visible = true
            HatBase2.visible = true
            HatBase2.xRot = -0.2618f
        } else if (fh3) {
            HatBase.visible = false
            Hair02f1.visible = false
            Hair01.visible = true
            HatBase2.visible = true
            HatBase2.xRot = -0.7f
            HatBase2.y = hatBase2DefaultY + (-0.06f * OFFSET_SCALE)
            HatBase2.z = hatBase2DefaultZ + (0.06f * OFFSET_SCALE)
        } else {
            HatBase.visible = false
            Hair02f1.visible = true
            Hair01.visible = true
            HatBase2.visible = false
        }
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        Head.xRot = 0.0f
        Head.yRot = 0.0f
        Head.zRot = 0.0f
        Ahoke.yRot = 0.5236f
        BodyMain.xRot = 1.4f
        Butt.xRot = 0.21f
        Skirt01.xRot = -0.052f
        Skirt02.xRot = -0.052f
        Hair01.xRot = -0.07f
        Hair01.y = hair01DefaultY + (-0.2f * OFFSET_SCALE)

        ArmLeft01.xRot = -2.8f
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = 0.7f
        ArmRight01.xRot = -2.8f
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = -0.7f
        ArmLeft02.zRot = 1.0f
        ArmRight02.zRot = -1.0f

        LegLeft01.xRot = 0.1f
        LegLeft01.yRot = Math.PI.toFloat()
        LegLeft01.zRot = -0.1f
        LegRight01.xRot = 0.1f
        LegRight01.yRot = Math.PI.toFloat()
        LegRight01.zRot = 0.1f
        LegLeft02.xRot = 0.0f
        LegLeft02.yRot = 0.0f
        LegLeft02.zRot = 0.0f
        LegRight02.xRot = 0.0f
        LegRight02.yRot = 0.0f
        LegRight02.zRot = 0.0f
    }

    private fun hideMouthParts() {
        if (Mouth0 != null) {
            Mouth0!!.visible = false
        }
        if (Mouth1 != null) {
            Mouth1!!.visible = false
        }
        if (Mouth2 != null) {
            Mouth2!!.visible = false
        }
    }

    private fun applyBasePose(ctx: PoseContext) {
        val angleX = ctx.angleX

        Ahoke.yRot = angleX * 0.2f + 1.2f
        BodyMain.xRot = -0.1047f
        BodyMain.yRot = 0.0f
        BodyMain.zRot = 0.0f
        Butt.xRot = 0.21f
        Skirt01.xRot = -0.052f
        Skirt02.xRot = -0.052f

        ArmLeft01.xRot = 0.1745f
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = angleX * 0.03f - 0.3f
        ArmLeft02.xRot = 0.0f
        ArmLeft02.zRot = 0.0f

        ArmRight01.xRot = -0.0523f
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = -angleX * 0.03f + 0.3f
        ArmRight02.xRot = 0.0f
        ArmRight02.zRot = 0.0f

        LegLeft01.yRot = 0.0f
        LegLeft01.zRot = 0.1047f
        LegLeft02.xRot = 0.0f
        LegLeft02.yRot = 0.0f
        LegLeft02.zRot = 0.0f
        LegRight01.yRot = 0.0f
        LegRight01.zRot = -0.1047f
        LegRight02.xRot = 0.0f
        LegRight02.yRot = 0.0f
        LegRight02.zRot = 0.0f

        EquipHead01.zRot = angleX * 0.2f - 1.5708f
        EquipC02.yRot = 0.5f + Head.yRot * 0.5f
        EquipC04a.xRot = min(0.0f, -0.2f + Head.xRot)
        EquipC05a.xRot = EquipC04a.xRot
        EquipC04b.xRot = EquipC04a.xRot
        EquipC05b.xRot = EquipC04a.xRot
    }

    private fun applySpecialPoseAdjustments(entity: T?, ctx: PoseContext, ageInTicks: Float) {
        var legLeftX = ctx.legAddLeft
        var legRightX = ctx.legAddRight

        val isPassenger = entity != null && entity.isPassenger
        val isCrouching = entity != null && entity.isCrouching
        val isSprinting = entity != null && entity.isSprinting

        if (entity != null && !entity.getEquipFlag(EntityDestroyerHibiki.EQUIP_RIGGING)) {
            ArmLeft01.zRot += 0.1f
            ArmRight01.zRot -= 0.1f
        }

        if (isSprinting) {
            Head.xRot -= 0.25f
            BodyMain.xRot = 0.1f
            Skirt01.xRot = -0.1f
            Skirt02.xRot = -0.1885f
            ArmLeft01.xRot = 0.35f
            ArmLeft01.zRot = -0.5f
            ArmRight01.xRot = 0.35f
            ArmRight01.zRot = 0.5f
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

        var riderType = 0
        if (entity is IShipRiderType) {
            riderType = entity.riderType
        }
        if (riderType > 0) {
            this.poseTranslateY = 0.0f
            Butt.xRot = -0.2f
            Butt.y = buttDefaultY + (-0.1f * OFFSET_SCALE)
            Skirt01.xRot = -0.07f
            Skirt01.y = skirt01DefaultY + (-0.1f * OFFSET_SCALE)
            Skirt02.xRot = -0.16f
            Skirt02.y = skirt02DefaultY + (-0.15f * OFFSET_SCALE)
            ArmLeft01.xRot = -0.3f
            ArmLeft01.yRot = -0.2f
            ArmLeft01.zRot = 0.0f
            ArmLeft02.xRot = -1.2f
            ArmRight01.xRot = -0.3f
            ArmRight01.yRot = 0.2f
            ArmRight01.zRot = 0.0f
            ArmRight02.xRot = -1.2f
            legLeftX = -0.95f
            legRightX = -0.95f
            LegLeft01.yRot = -0.5f
            LegLeft01.zRot = -0.1f
            LegLeft02.z = legLeft02DefaultZ
            LegLeft02.xRot = 0.8f
            LegLeft02.zRot = 0.0175f
            LegRight01.yRot = 0.5f
            LegRight01.zRot = 0.1f
            LegRight02.z = legRight02DefaultZ
            LegRight02.xRot = 0.8f
            LegRight02.zRot = -0.0175f
            if (ctx.isSitting) {
                ArmLeft01.xRot = -0.6f
                ArmLeft01.yRot = 0.0f
                ArmLeft01.zRot = 0.2f
                ArmLeft02.xRot = 0.0f
                ArmRight01.xRot = -0.6f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = -0.2f
                ArmRight02.xRot = 0.0f
            }
            if (riderType > 1) {
                Head.yRot *= 0.5f
                Head.zRot = 0.0f

                if (entity != null && hasLegacyState(entity, 1, 4)) {
                    ArmLeft01.xRot = 0.1f
                    ArmLeft01.yRot = 0.0f
                    ArmLeft01.zRot = -0.4f
                    ArmLeft02.xRot = 0.0f
                    ArmLeft02.zRot = 0.8f
                    ArmRight01.xRot = 0.1f
                    ArmRight01.yRot = 0.0f
                    ArmRight01.zRot = 0.4f
                    ArmRight02.xRot = 0.0f
                    ArmRight02.zRot = -0.8f
                } else {
                    ArmLeft01.xRot = -0.8f
                    ArmLeft01.yRot = -1.5f
                    ArmLeft01.zRot = 0.0f
                    ArmLeft02.xRot = 0.0f
                    ArmLeft02.zRot = 1.45f
                    ArmRight01.xRot = -0.8f
                    ArmRight01.yRot = 1.5f
                    ArmRight01.zRot = 0.0f
                    ArmRight02.xRot = 0.0f
                    ArmRight02.zRot = -1.45f
                }
                EquipBase.visible = false
                if (ctx.isSitting) {
                    Head.xRot -= 0.1f
                    BodyMain.xRot = 0.0f
                    Butt.xRot = -0.2f
                    Butt.y = buttDefaultY + (-0.1f * OFFSET_SCALE)
                    Skirt01.xRot = -0.07f
                    Skirt01.y = skirt01DefaultY + (-0.05f * OFFSET_SCALE)
                    Skirt02.xRot = -0.16f
                    Skirt02.y = skirt02DefaultY + (-0.08f * OFFSET_SCALE)
                    legLeftX = -0.65f
                    legRightX = -0.65f
                    LegLeft01.yRot = 0.2f
                    LegLeft01.zRot = 0.0f
                    LegLeft02.z = legLeft02DefaultZ + (0.375f * OFFSET_SCALE)
                    LegLeft02.xRot = 2.45f
                    LegLeft02.zRot = 0.0175f
                    LegRight01.yRot = -0.2f
                    LegRight01.zRot = 0.0f
                    LegRight02.z = legRight02DefaultZ + (0.375f * OFFSET_SCALE)
                    LegRight02.xRot = 2.45f
                    LegRight02.zRot = -0.0175f
                }
            }
        } else if (ctx.isSitting || isPassenger) {
            val mount = if (entity != null) entity.vehicle else null
            val ridingShip = mount is EntityShipBase
            if (entity != null && hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY = if (ridingShip) 0.0f else 0.52f * 3.2f
                Head.xRot = -0.9f
                Head.yRot = -1.1f
                Head.zRot = 0.0f
                BodyMain.xRot = 1.4f
                Hair01.xRot -= 0.1f
                Hair01.y = hair01DefaultY + (-0.2f * OFFSET_SCALE)

                legLeftX = -0.1f
                legRightX = 0.0f
                LegLeft01.yRot = 0.0f
                LegLeft01.zRot = 0.2f
                LegRight01.yRot = 0.0f
                LegRight01.zRot = -0.2f
                LegRight02.xRot = 0.3f

                ArmLeft01.xRot = -2.8f
                ArmLeft01.zRot = -0.2f
                ArmRight01.xRot = -2.8f
                ArmRight01.zRot = -0.7f
                ArmLeft02.zRot = 0.5f
                ArmRight02.zRot = -1.0f
            } else {
                this.poseTranslateY = if (ridingShip) 0.0f else (if (isPassenger) 0.375f * 3 else 0.3f * 3)
                Head.xRot -= 0.1f
                BodyMain.xRot = 0.0f
                Butt.xRot = -0.2f
                Butt.y = buttDefaultY + (-0.1f * OFFSET_SCALE)
                Skirt01.xRot = -0.07f
                Skirt01.y = skirt01DefaultY + (-0.05f * OFFSET_SCALE)
                Skirt02.xRot = -0.16f
                Skirt02.y = skirt02DefaultY + (-0.08f * OFFSET_SCALE)

                ArmLeft01.xRot = -0.4f
                ArmLeft01.zRot = 0.15f
                ArmRight01.xRot = -0.4f
                ArmRight01.zRot = -0.15f

                legLeftX = -0.65f
                legRightX = -0.65f
                LegLeft01.yRot = 0.2f
                LegLeft02.z = legLeft02DefaultZ + (0.375f * OFFSET_SCALE)
                LegLeft02.xRot = 2.45f

                LegRight01.yRot = -0.2f
                LegRight02.z = legRight02DefaultZ + (0.375f * OFFSET_SCALE)
                LegRight02.xRot = 2.45f
            }
        }

        if (entity != null && entity.attackTick > 30) {
            ArmLeft01.xRot = -1.55f
            ArmLeft01.yRot = 0.3f
            ArmLeft01.zRot = 0.0f
            ArmLeft02.zRot = 0.7f
            ArmRight01.xRot = -1.7f
            ArmRight01.yRot = -0.1f
            ArmRight01.zRot = 1.5f
            ArmRight02.zRot = 0.0f
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

        if (entity != null && hasLegacyState(entity, 6, 1)) {
            Head.xRot += 0.6f
            ArmLeft01.xRot = -0.44f
            ArmLeft01.yRot = 0.0f
            ArmLeft01.zRot = 0.4f
            ArmLeft02.zRot = 0.0f
            ArmRight01.xRot = -0.4f
            ArmRight01.yRot = 0.0f
            ArmRight01.zRot = -0.4f
            ArmRight02.zRot = 0.0f
        }

        LegLeft01.xRot = legLeftX
        LegRight01.xRot = legRightX
    }

    private fun applyHairAnimation(ctx: PoseContext, ageInTicks: Float, limbSwing: Float) {
        val angleX = ctx.angleX
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f)
        val angleX2 = Mth.cos(ageInTicks * 0.08f + 0.6f + limbSwing * 0.5f)

        Hair01.xRot = angleX * 0.04f + 0.26f
        Hair01.zRot = 0.0f

        if (Hair02a1 != null) {
            Hair02a1.xRot = -angleX1 * 0.1f + 0.26f
            Hair02a1.zRot = 0.0f
            Hair02a2.xRot = -angleX2 * 0.13f - 0.26f
        }
        if (Hair02b1 != null) {
            Hair02b1.xRot = -angleX1 * 0.1f + 0.26f
            Hair02b1.zRot = 0.26f
            Hair02b2.xRot = -angleX2 * 0.13f - 0.44f
        }
        if (Hair02c1 != null) {
            Hair02c1.xRot = -angleX1 * 0.1f + 0.17f
            Hair02c1.zRot = -0.4f
            Hair02c2.xRot = -angleX2 * 0.13f - 0.35f
        }
        if (Hair02d1 != null) {
            Hair02d1.xRot = 0.2618f
            Hair02d1.zRot = -angleX1 * 0.05f + 0.35f
            Hair02d2.zRot = -angleX2 * 0.07f - 0.52f
        }
        if (Hair02e1 != null) {
            Hair02e1.xRot = 0.05f
            Hair02e1.zRot = angleX1 * 0.05f - 0.6f
            Hair02e2.zRot = angleX2 * 0.07f + 0.87f
        }

        HairL01.xRot = angleX * 0.04f - 0.2618f
        HairL01.zRot = 0.087f
        HairL02.xRot = -angleX1 * 0.1f + 0.3142f
        HairL02.zRot = 0.0873f
        HairR01.xRot = angleX * 0.04f - 0.2618f
        HairR01.zRot = -0.0873f
        HairR02.xRot = -angleX1 * 0.1f + 0.21f
        HairR02.zRot = -0.0873f

        val headX = Head.xRot * -0.5f
        val headZ = Head.zRot * -0.5f

        Hair01.xRot += headX
        Hair01.zRot += headZ
        if (Hair02a1 != null) {
            Hair02a1.xRot += headX
            Hair02a1.zRot += headZ
        }
        if (Hair02b1 != null) {
            Hair02b1.xRot += headX
            Hair02b1.zRot += headZ
        }
        if (Hair02c1 != null) {
            Hair02c1.xRot += headX
            Hair02c1.zRot += headZ
        }
        if (Hair02d1 != null) {
            Hair02d1.xRot += headX
            Hair02d1.zRot += headZ
        }
        if (Hair02e1 != null) {
            Hair02e1.xRot += headX
            Hair02e1.zRot += headZ
        }

        HairL01.zRot += headZ
        HairL02.zRot += headZ
        HairR01.zRot += headZ
        HairR02.zRot += headZ * 2.0f
        HairL01.xRot += headX
        HairL02.xRot += headX
        HairR01.xRot += headX
        HairR02.xRot += headX
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

    private fun syncGlowParts() {
        GlowBodyMain!!.copyFrom(BodyMain)
        GlowHead.copyFrom(Head)
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "destroyer_hibiki"), "main")

        private val DEAD_TRANSLATE_Y = deadY("ModelDestroyerHibiki")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelDestroyerHibiki")
        private const val OFFSET_SCALE = 16.0f

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

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(54, 66).addBox(-7f, 0f, 0f, 14f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3f, -4f, 0.2094f, 0f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 59).mirror().addBox(-3f, 0f, -3f, 6f, 12f, 6f, CubeDeformation(0f)),
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

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 59).addBox(-3f, 0f, -3f, 6f, 12f, 6f, CubeDeformation(0f)),
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

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(0, 88).mirror()
                    .addBox(-2.5f, -1f, -3f, 6f, 11f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.3f, -9.4f, -0.7f, 0.1745f, 0f, -0.3142f)
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
                PartPose.offsetAndRotation(0.5f, 4f, -3f, -0.1396f, -0.1047f, -0.0524f)
            )

            val EquipBase = BodyMain.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, 0f)
            )

            val EquipMain01 = EquipBase.addOrReplaceChild(
                "EquipMain01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.5f, -1f, 0f, 11f, 9f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, -4f, 5f)
            )

            val EquipMain04 = EquipMain01.addOrReplaceChild(
                "EquipMain04",
                CubeListBuilder.create().texOffs(0, 26).addBox(-3f, 0f, -3f, 6f, 16f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -16.5f, 9f, -0.0873f, 0f, 0f)
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

            val EquipHead05 = EquipHead02.addOrReplaceChild(
                "EquipHead05",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -5f, 0f, 2f, 10f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -2f)
            )

            val EquipHead03 = EquipHead02.addOrReplaceChild(
                "EquipHead03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4.8f, 2.5f, -0.2618f, 0f, 0f)
            )

            val EquipTR02 = EquipMain01.addOrReplaceChild(
                "EquipTR02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, -4f, -9f, 3f, 9f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5.5f, 6f, 4.5f, 0.1396f, 0.0698f, 0f)
            )

            val EquipTR02f = EquipTR02.addOrReplaceChild(
                "EquipTR02f",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, 2.3f, 2.5f)
            )

            val EquipTR02a = EquipTR02.addOrReplaceChild(
                "EquipTR02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 11f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, 0f, -19.8f)
            )

            val EquipTR02e = EquipTR02.addOrReplaceChild(
                "EquipTR02e",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, 0f, 2.2f)
            )

            val EquipTR02d = EquipTR02.addOrReplaceChild(
                "EquipTR02d",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, -2.3f, 3f)
            )

            val EquipTR02b = EquipTR02.addOrReplaceChild(
                "EquipTR02b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, -2.3f, -18.8f)
            )

            val EquipTR02c = EquipTR02.addOrReplaceChild(
                "EquipTR02c",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 11f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, 2.3f, -19.5f)
            )

            val EquipMain02 = EquipMain01.addOrReplaceChild(
                "EquipMain02",
                CubeListBuilder.create().texOffs(52, 8).addBox(-4f, 0f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6.9f, 1.2f, 0.6283f, 0f, 0f)
            )

            val EquipTL02 = EquipMain01.addOrReplaceChild(
                "EquipTL02",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -4f, -9f, 3f, 9f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.5f, 6f, 4.5f, 0.1396f, -0.0698f, 0f)
            )

            val EquipTL02d = EquipTL02.addOrReplaceChild(
                "EquipTL02d",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(1.3f, -2.3f, 3f)
            )

            val EquipTL02b = EquipTL02.addOrReplaceChild(
                "EquipTL02b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offset(1.3f, -2.3f, -18.8f)
            )

            val EquipTL02e = EquipTL02.addOrReplaceChild(
                "EquipTL02e",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(1.3f, 0f, 2.2f)
            )

            val EquipTL02a = EquipTL02.addOrReplaceChild(
                "EquipTL02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 11f, CubeDeformation(0f)),
                PartPose.offset(1.3f, 0f, -19.8f)
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
                PartPose.offsetAndRotation(-1f, -2.4f, 1.5f, -0.1047f, 0.7854f, 0f)
            )

            val Hat03c = HatBase.addOrReplaceChild(
                "Hat03c",
                CubeListBuilder.create().texOffs(23, 43).addBox(0f, -4f, 0f, 5f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, 2f, 0f)
            )

            val Hat01a = HatBase.addOrReplaceChild(
                "Hat01a",
                CubeListBuilder.create().texOffs(46, 0).addBox(0f, 0f, -6f, 6f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offset(-0.7f, 0f, 0f)
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

            val Hat03d = HatBase.addOrReplaceChild(
                "Hat03d",
                CubeListBuilder.create().texOffs(23, 43).addBox(-5f, -4f, 0f, 5f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offset(0.5f, 2f, 0f)
            )

            val Hat02b = HatBase.addOrReplaceChild(
                "Hat02b",
                CubeListBuilder.create().texOffs(0, 24).addBox(0f, 0f, 0f, 0f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.8f, -1.7f, -2f, -0.1396f, 0f, 0f)
            )

            val Hat01d = HatBase.addOrReplaceChild(
                "Hat01d",
                CubeListBuilder.create().texOffs(46, 0).addBox(-6f, 0f, -6f, 6f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offset(0.7f, 0f, 0f)
            )

            val Hat03a = HatBase.addOrReplaceChild(
                "Hat03a",
                CubeListBuilder.create().texOffs(23, 43).mirror().addBox(0f, -4f, -5f, 5f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.3f, 2f, 0f, -0.1396f, 0f, 0f)
            )

            val Hat03b = HatBase.addOrReplaceChild(
                "Hat03b",
                CubeListBuilder.create().texOffs(23, 43).addBox(-5f, -4f, -5f, 5f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.3f, 2f, 0f, -0.1396f, 0f, 0f)
            )

            val Hat02a = HatBase.addOrReplaceChild(
                "Hat02a",
                CubeListBuilder.create().texOffs(55, 0).addBox(-4.5f, 0f, -6f, 9f, 0f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, -6f, 0.1745f, 0f, 0f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(38, 23).addBox(-7.5f, 0f, -10f, 15f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 12f, 0.2618f, 0f, 0f)
            )

            val Hair02b1 = Hair01.addOrReplaceChild(
                "Hair02b1",
                CubeListBuilder.create().texOffs(24, 26).addBox(-2f, 0f, 0f, 4f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, 7f, -2.4f, 0.2618f, -0.1745f, 0.2618f)
            )

            val Hair02b2 = Hair02b1.addOrReplaceChild(
                "Hair02b2",
                CubeListBuilder.create().texOffs(24, 66).addBox(-2f, 0f, 0f, 4f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, -0.4363f, 0f, 0f)
            )

            val Hair02a1 = Hair01.addOrReplaceChild(
                "Hair02a1",
                CubeListBuilder.create().texOffs(24, 26).addBox(-2f, 0f, 0f, 4f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10f, -2.2f, 0.2618f, 0f, 0f)
            )

            val Hair02a2 = Hair02a1.addOrReplaceChild(
                "Hair02a2",
                CubeListBuilder.create().texOffs(24, 32).addBox(-2f, 0f, 0f, 4f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, -0.2618f, 0f, 0f)
            )

            val Hair02e1 = Hair01.addOrReplaceChild(
                "Hair02e1",
                CubeListBuilder.create().texOffs(24, 22).addBox(0f, 0f, -2f, 0f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.4f, -4f, -5.5f, 0.0524f, 0f, -0.6109f)
            )

            val Hair02e2 = Hair02e1.addOrReplaceChild(
                "Hair02e2",
                CubeListBuilder.create().texOffs(24, 62).mirror().addBox(0f, 0f, -2f, 0f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0f, 0f, 0.8727f)
            )

            val Hair02d1 = Hair01.addOrReplaceChild(
                "Hair02d1",
                CubeListBuilder.create().texOffs(28, 22).addBox(0f, 0f, -2f, 0f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.4f, 0f, -5.5f, 0.2618f, 0f, 0.3491f)
            )

            val Hair02d2 = Hair02d1.addOrReplaceChild(
                "Hair02d2",
                CubeListBuilder.create().texOffs(28, 62).mirror().addBox(0f, 0f, -2f, 0f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0f, 0f, -0.5236f)
            )

            val Hair02c1 = Hair01.addOrReplaceChild(
                "Hair02c1",
                CubeListBuilder.create().texOffs(24, 26).addBox(-2f, 0f, 0f, 4f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.2f, 6f, -2.4f, 0.1745f, 0.1745f, -0.4014f)
            )

            val Hair02c2 = Hair02c1.addOrReplaceChild(
                "Hair02c2",
                CubeListBuilder.create().texOffs(24, 66).addBox(-2f, 0f, 0f, 4f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, -0.3491f, 0f, 0f)
            )

            val Hair02f1 = HairMain.addOrReplaceChild(
                "Hair02f1",
                CubeListBuilder.create().texOffs(25, 26).addBox(-1.5f, 0f, 0f, 3f, 5f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5f, 1f, 9.5f, 0.7854f, 0.3491f, -0.1396f)
            )

            val Hair02f2 = Hair02f1.addOrReplaceChild(
                "Hair02f2",
                CubeListBuilder.create().texOffs(26, 68).mirror()
                    .addBox(-1.5f, 0f, 0f, 3f, 4f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, 0f, 0.6283f, 0f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 81).addBox(-8f, -8f, -7.4f, 16f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0.3f)
            )

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(52, 45).addBox(-8.5f, 0f, 0f, 17f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -6.2f, -7.1f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(89, 102).addBox(-0.5f, 0f, 0f, 1f, 9f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8f, 2.6f, -4.7f, -0.2618f, 0.0873f, -0.0873f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(88, 104).addBox(-0.5f, 0f, 0f, 1f, 7f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.1f, 8.5f, 0f, 0.2094f, 0f, -0.0873f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(0, 37).addBox(0f, 0f, -11f, 0f, 11f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.5f, -7.4f, -7f, -0.5236f, 1.2217f, 0f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(89, 102).addBox(-0.5f, 0f, 0f, 1f, 9f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, 2.5f, -4.4f, -0.2618f, -0.0873f, 0.0873f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(88, 104).addBox(-0.5f, 0f, 0f, 1f, 7f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.1f, 8.5f, 0f, 0.3142f, 0f, 0.0873f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(0, 88).addBox(-3.5f, -1f, -3f, 6f, 11f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.3f, -9.4f, -0.7f, 0.1745f, 0f, 0.3142f)
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
            addFaceLayerHibiki(GlowHead)

            val HatBase2 = GlowHead.addOrReplaceChild(
                "HatBase2",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -8f, 0f, -0.2618f, 0f, 0f)
            )

            val Hat201_01 = HatBase2.addOrReplaceChild(
                "Hat201_01",
                CubeListBuilder.create().texOffs(98, 46).mirror().addBox(0f, -9f, 0f, 9f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -9f)
            )

            val Hat201_02 = HatBase2.addOrReplaceChild(
                "Hat201_02",
                CubeListBuilder.create().texOffs(98, 46).addBox(-9f, -9f, 0f, 9f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -9f)
            )

            val Hat201_03 = HatBase2.addOrReplaceChild(
                "Hat201_03",
                CubeListBuilder.create().texOffs(98, 46).mirror().addBox(0f, -9f, 0f, 9f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 8f)
            )

            val Hat201_04 = HatBase2.addOrReplaceChild(
                "Hat201_04",
                CubeListBuilder.create().texOffs(98, 46).addBox(-9f, -9f, 0f, 9f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 8f)
            )

            val Hat201_05 = HatBase2.addOrReplaceChild(
                "Hat201_05",
                CubeListBuilder.create().texOffs(98, 46).mirror().addBox(0f, -9f, 0f, 9f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, 0f, 0f, 0f, 1.5708f, 0f)
            )

            val Hat201_06 = HatBase2.addOrReplaceChild(
                "Hat201_06",
                CubeListBuilder.create().texOffs(98, 46).addBox(0f, -9f, 0f, 9f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, 0f, 9f, 0f, 1.5708f, 0f)
            )

            val Hat201_07 = HatBase2.addOrReplaceChild(
                "Hat201_07",
                CubeListBuilder.create().texOffs(98, 46).mirror().addBox(0f, -9f, 0f, 9f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9f, 0f, 0f, 0f, 1.5708f, 0f)
            )

            val Hat201_08 = HatBase2.addOrReplaceChild(
                "Hat201_08",
                CubeListBuilder.create().texOffs(98, 46).addBox(0f, -9f, 0f, 9f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9f, 0f, 9f, 0f, 1.5708f, 0f)
            )

            val Hat201_09 = HatBase2.addOrReplaceChild(
                "Hat201_09",
                CubeListBuilder.create().texOffs(98, 46).mirror().addBox(0f, 0f, 0f, 9f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -9f, 0f, -1.5708f, 0f, 0f)
            )

            val Hat201_10 = HatBase2.addOrReplaceChild(
                "Hat201_10",
                CubeListBuilder.create().texOffs(98, 46).addBox(0f, 0f, 0f, 9f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9f, -9f, 0f, -1.5708f, 0f, 0f)
            )

            val Hat201_11 = HatBase2.addOrReplaceChild(
                "Hat201_11",
                CubeListBuilder.create().texOffs(98, 46).mirror().addBox(0f, 0f, 0f, 9f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -9f, 9f, -1.5708f, 0f, 0f)
            )

            val Hat201_12 = HatBase2.addOrReplaceChild(
                "Hat201_12",
                CubeListBuilder.create().texOffs(98, 46).addBox(0f, 0f, 0f, 9f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9f, -9f, 9f, -1.5708f, 0f, 0f)
            )

            val Hat202a = HatBase2.addOrReplaceChild(
                "Hat202a",
                CubeListBuilder.create().texOffs(46, 8).addBox(-3f, 0f, -0.5f, 6f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(9.5f, -2.5f, 0f, 0f, 1.57f, -0.0873f)
            )

            val Hat202b = HatBase2.addOrReplaceChild(
                "Hat202b",
                CubeListBuilder.create().texOffs(46, 8).addBox(-3f, 0f, -0.5f, 6f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9.5f, -2.5f, 0f, 0f, 1.57f, 0.0873f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
