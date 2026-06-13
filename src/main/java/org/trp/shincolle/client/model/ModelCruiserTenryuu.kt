package org.trp.shincolle.client.model

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
import org.trp.shincolle.client.model.LegacyPoseOffsets.sittingAltY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sittingY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sneakY
import org.trp.shincolle.entity.EntityCruiserTenryuu
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelCruiserTenryuu<T : EntityShipBase>(root: ModelPart) : ShincolleShipModel<T>() {
    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val Butt: ModelPart
    private val ArmRight01: ModelPart
    private val ArmLeft01: ModelPart
    private val Cloth01: ModelPart
    private val EquipSR01: ModelPart
    private val Equip00: ModelPart
    private val Head: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val EarL01: ModelPart
    private val EarR01: ModelPart
    private val EyeMask: ModelPart
    private val HairU01: ModelPart
    private val Ahoke: ModelPart
    private val Hair01: ModelPart
    private val EarL02: ModelPart
    private val EarL03: ModelPart
    private val EarL04: ModelPart
    private val EarR02: ModelPart
    private val EarR03: ModelPart
    private val EarR04: ModelPart
    private val LegLeft01: ModelPart
    private val Skirt01: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft02: ModelPart
    private val ShoeL01: ModelPart
    private val ShoeL00: ModelPart
    private val ShoeL02: ModelPart
    private val Skirt02: ModelPart
    private val LegRight02: ModelPart
    private val ShoeR01: ModelPart
    private val ShoeR00: ModelPart
    private val ShoeR02: ModelPart
    private val ArmRight02: ModelPart
    private val ArmRight02a: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmLeft02a: ModelPart
    private val EquipSL00: ModelPart
    private val EquipSL00a: ModelPart
    private val EquipSL00b: ModelPart
    private val EquipSL01: ModelPart
    private val EquipSL02: ModelPart
    private val EquipSL02a: ModelPart
    private val EquipSL03: ModelPart
    private val EquipSL03a: ModelPart
    private val EquipSR02: ModelPart
    private val EquipSR03: ModelPart
    private val Equip01a: ModelPart
    private val Equip01b: ModelPart
    private val Equip01c: ModelPart
    private val Equip02a: ModelPart
    private val Equip01d: ModelPart
    private val Equip03L: ModelPart
    private val Equip03R: ModelPart
    private val EquipCL01: ModelPart
    private val EquipCL02: ModelPart
    private val EquipCL03: ModelPart
    private val EquipCL04: ModelPart
    private val EquipCL05: ModelPart
    private val EquipCR01: ModelPart
    private val EquipCR02: ModelPart
    private val EquipCR03: ModelPart
    private val EquipCR04: ModelPart
    private val EquipCR05: ModelPart
    private val Equip02b: ModelPart
    private val Equip02c: ModelPart
    private val Equip02d: ModelPart
    private val GlowBodyMain: ModelPart?
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val GlowEquip00: ModelPart?
    private val GlowEquip01a: ModelPart
    private val GlowEquip02a: ModelPart

    protected override val bodyMain: ModelPart get() = BodyMain
    protected override val neck: ModelPart get() = Neck
    protected override val head: ModelPart get() = Head
    protected override val glowBodyMain: ModelPart? get() = GlowBodyMain
    protected override val glowNeck: ModelPart get() = GlowNeck
    protected override val glowHead: ModelPart get() = GlowHead

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

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.ArmRight02a = this.ArmRight02.getChild("ArmRight02a")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.EquipSL00 = this.ArmLeft02.getChild("EquipSL00")
        this.EquipSL00b = this.EquipSL00.getChild("EquipSL00b")
        this.EquipSL01 = this.EquipSL00.getChild("EquipSL01")
        this.EquipSL02 = this.EquipSL01.getChild("EquipSL02")
        this.EquipSL03 = this.EquipSL02.getChild("EquipSL03")
        this.EquipSL03a = this.EquipSL03.getChild("EquipSL03a")
        this.EquipSL02a = this.EquipSL02.getChild("EquipSL02a")
        this.EquipSL00a = this.EquipSL00.getChild("EquipSL00a")
        this.ArmLeft02a = this.ArmLeft02.getChild("ArmLeft02a")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.EyeMask = this.Head.getChild("EyeMask")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.ShoeR01 = this.LegRight02.getChild("ShoeR01")
        this.ShoeR02 = this.ShoeR01.getChild("ShoeR02")
        this.ShoeR00 = this.LegRight02.getChild("ShoeR00")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.Skirt02 = this.Skirt01.getChild("Skirt02")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.ShoeL01 = this.LegLeft02.getChild("ShoeL01")
        this.ShoeL02 = this.ShoeL01.getChild("ShoeL02")
        this.ShoeL00 = this.LegLeft02.getChild("ShoeL00")
        this.EquipSR01 = this.BodyMain.getChild("EquipSR01")
        this.EquipSR02 = this.EquipSR01.getChild("EquipSR02")
        this.EquipSR03 = this.EquipSR02.getChild("EquipSR03")
        this.Equip00 = this.BodyMain.getChild("Equip00")
        this.Equip01a = this.Equip00.getChild("Equip01a")
        this.Equip01b = this.Equip01a.getChild("Equip01b")
        this.Equip01c = this.Equip01a.getChild("Equip01c")
        this.Equip01d = this.Equip01c.getChild("Equip01d")
        this.Equip03R = this.Equip01d.getChild("Equip03R")
        this.EquipCR01 = this.Equip03R.getChild("EquipCR01")
        this.EquipCR02 = this.EquipCR01.getChild("EquipCR02")
        this.EquipCR03 = this.EquipCR02.getChild("EquipCR03")
        this.EquipCR04 = this.EquipCR03.getChild("EquipCR04")
        this.EquipCR05 = this.EquipCR04.getChild("EquipCR05")
        this.Equip03L = this.Equip01d.getChild("Equip03L")
        this.EquipCL01 = this.Equip03L.getChild("EquipCL01")
        this.EquipCL02 = this.EquipCL01.getChild("EquipCL02")
        this.EquipCL03 = this.EquipCL02.getChild("EquipCL03")
        this.EquipCL04 = this.EquipCL03.getChild("EquipCL04")
        this.EquipCL05 = this.EquipCL04.getChild("EquipCL05")
        this.Equip02a = this.Equip01a.getChild("Equip02a")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        this.initFaceParts(this.GlowHead)
        this.EarL01 = this.GlowHead.getChild("EarL01")
        this.EarL02 = this.EarL01.getChild("EarL02")
        this.EarL03 = this.EarL02.getChild("EarL03")
        this.EarL04 = this.EarL03.getChild("EarL04")
        this.EarR01 = this.GlowHead.getChild("EarR01")
        this.EarR02 = this.EarR01.getChild("EarR02")
        this.EarR03 = this.EarR02.getChild("EarR03")
        this.EarR04 = this.EarR03.getChild("EarR04")
        this.GlowEquip00 = this.GlowBodyMain.getChild("GlowEquip00")
        this.GlowEquip01a = this.GlowEquip00.getChild("GlowEquip01a")
        this.GlowEquip02a = this.GlowEquip01a.getChild("GlowEquip02a")
        this.Equip02b = this.GlowEquip02a.getChild("Equip02b")
        this.Equip02c = this.Equip02b.getChild("Equip02c")
        this.Equip02d = this.Equip02c.getChild("Equip02d")
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
    }

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val ctx = computePoseContext(entity, limbSwing, limbSwingAmount, ageInTicks, 0.0f)
        resetPoseState()
        resetOffsets()

        applyFaceAndMouth(entity)
        applyEquipVisibility(entity)

        if (isDeadPose(entity)) {
            applyDeadPose()
            syncGlowParts()
            return
        }

        applyHeadRotation(Head, entity, ageInTicks, netHeadYaw, headPitch)

        applyBasePose(ctx)
        applyEarAnimation(ageInTicks)
        applyEquipAnimation(entity, headPitch)
        applySpecialPoseAdjustments(entity, ctx, ageInTicks, limbSwingAmount)

        syncGlowParts()
    }

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) return

        val showEquip = entity.getEquipFlag(EntityCruiserTenryuu.EQUIP_RIGGING)
        Equip00.visible = showEquip
        if (GlowEquip00 != null) GlowEquip00.visible = showEquip
        val showEars = entity.getEquipFlag(EntityCruiserTenryuu.EQUIP_EARS)
        EarL01.visible = showEars
        EarR01.visible = showEars
        val showSideEquip = entity.getEquipFlag(EntityCruiserTenryuu.EQUIP_SIDE)
        EquipSL00.visible = showSideEquip
        EquipSR01.visible = showSideEquip
        EyeMask.visible = entity.getEquipFlag(EntityCruiserTenryuu.EQUIP_MASK)
        val showShoes = entity.getEquipFlag(EntityCruiserTenryuu.EQUIP_SHOES)
        ShoeL02.visible = showShoes
        ShoeR02.visible = showShoes
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        Ahoke.xRot = 0.2094f
        Ahoke.yRot = 0.6981f
        Ahoke.zRot = 0.0f
        Head.xRot = 0.15f
        Head.yRot = 0.0f
        Head.zRot = 0.0f
        BodyMain.xRot = 1.7453f
        BodyMain.yRot = 0.0f
        BodyMain.zRot = -0.5236f
        Butt.xRot = -0.7854f
        Butt.yRot = 0.0f
        Butt.zRot = 0.0f
        Skirt01.xRot = 0.0f
        Skirt02.xRot = -0.0873f
        Skirt02.yRot = 0.0f
        Skirt02.zRot = 0.0f

        ArmLeft01.xRot = -1.3963f
        ArmLeft01.yRot = -0.3491f
        ArmLeft01.zRot = -0.1745f
        ArmLeft02.xRot = -1.4835f
        ArmLeft02.yRot = 0.0f
        ArmLeft02.zRot = 0.0f
        ArmLeft02.z = armLeft02DefaultZ + (-0.2f * OFFSET_SCALE)

        ArmRight01.xRot = -1.3090f
        ArmRight01.yRot = -0.8727f
        ArmRight01.zRot = 0.0f
        ArmRight02.xRot = 0.0f
        ArmRight02.yRot = 0.0f
        ArmRight02.zRot = -0.1745f

        LegLeft01.xRot = -0.6981f
        LegLeft01.yRot = -0.6981f
        LegLeft01.zRot = -0.2618f
        LegLeft02.xRot = 1.5708f
        LegLeft02.yRot = 0.0f
        LegLeft02.zRot = 0.0f

        LegRight01.xRot = 0.0f
        LegRight01.yRot = -0.7854f
        LegRight01.zRot = -0.5760f
        LegRight02.xRot = 1.3090f
        LegRight02.yRot = 0.0f
        LegRight02.zRot = 0.0f

        EquipSL00.visible = false
        Equip00.xRot = 0.0873f
        Equip00.yRot = 0.0f
        Equip00.zRot = 0.0f
        EquipSR01.xRot = -0.1f
        EquipSR01.yRot = -0.1396f
        EquipSR01.zRot = -0.1396f
        EarL01.xRot = 0.6f
        EarL01.yRot = -0.1745f
        EarL01.zRot = -0.0873f
        EarR01.xRot = 0.6f
        EarR01.yRot = 0.1745f
        EarR01.zRot = 0.0873f
    }

    private fun applyBasePose(ctx: PoseContext) {
        val angleX = ctx.angleX

        BoobL.xRot = angleX * 0.06f - 0.8f
        BoobR.xRot = angleX * 0.06f - 0.8f
        Ahoke.yRot = angleX * 0.25f + 0.7f

        BodyMain.xRot = BODY_BASE_X_ROT
        BodyMain.yRot = 0.0f
        BodyMain.zRot = 0.0f
        Butt.xRot = BUTT_BASE_X_ROT
        Skirt01.xRot = SKIRT_BASE_X_ROT
        Skirt02.xRot = SKIRT02_BASE_X_ROT

        ArmLeft01.xRot = ctx.angleAdd2 * 0.25f + 0.2f
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = angleX * 0.03f - 0.25f
        ArmLeft02.xRot = 0.0f
        ArmLeft02.zRot = 0.0f

        ArmRight01.xRot = 0.0f
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = -angleX * 0.03f + 0.25f
        ArmRight02.xRot = 0.0f
        ArmRight02.zRot = 0.0f

        LegLeft01.yRot = 0.0f
        LegLeft01.zRot = LEG_BASE_Z_ROT
        LegLeft02.xRot = 0.0f
        LegLeft02.yRot = 0.0f
        LegLeft02.zRot = 0.0f

        LegRight01.yRot = 0.0f
        LegRight01.zRot = -LEG_BASE_Z_ROT
        LegRight02.xRot = 0.0f
        LegRight02.yRot = 0.0f
        LegRight02.zRot = 0.0f

        EquipSL00.xRot = -1.57f
        EquipSL00.yRot = -0.14f
        EquipSL00.zRot = 1.57f
        EquipSR01.xRot = 1.3f
    }

    private fun applyEarAnimation(ageInTicks: Float) {
        EarL01.xRot = Mth.cos(ageInTicks * 0.08f) * 0.1f + 0.0873f
        EarR01.xRot = EarL01.xRot

        var modf2 = ageInTicks % 128.0f
        if (modf2 < 6.0f) {
            if (modf2 >= 3.0f) {
                modf2 -= 3.0f
            }
            val anglef2 = Mth.sin(modf2 * 1.0472f) * 0.08f
            EarL01.zRot = -anglef2 - 0.0873f
            EarR01.zRot = anglef2 + 0.0873f
        } else {
            EarL01.zRot = -0.0873f
            EarR01.zRot = 0.0873f
        }
    }

    private fun applyEquipAnimation(entity: T?, headPitch: Float) {
        EquipCL02.xRot = headPitch * 0.008f + 0.7f
        EquipCR02.xRot = headPitch * 0.008f + 0.7f
        EquipCL04.xRot = headPitch * 0.008f
        EquipCR04.xRot = headPitch * 0.008f
    }

    private fun applySpecialPoseAdjustments(entity: T?, ctx: PoseContext, ageInTicks: Float, limbSwingAmount: Float) {
        var legAddLeft = ctx.angleAdd1 * 0.5f - 0.28f
        var legAddRight = ctx.angleAdd2 * 0.5f - 0.21f

        if (entity != null && entity.shipDepth > 0.0) {
            this.poseTranslateY += ctx.angleX * 0.05f + 0.025f
        }

        val tickPhase = if (entity != null) (entity.tickCount and 0x1FF) else 0
        if (entity != null && hasLegacyState(entity, 1, 4)) {
            if (tickPhase > 180) {
                ArmLeft01.xRot = 0.44f
                ArmLeft01.yRot = -0.14f
                ArmLeft01.zRot = -0.52f
                ArmRight01.xRot = -0.17f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = 0.7f
                ArmRight02.zRot = -1.22f
                legAddLeft = ctx.angleAdd1 * 0.5f - 0.35f
                legAddRight = ctx.angleAdd2 * 0.5f - 0.09f
                if (hasLegacyState(entity, 7, 4)) {
                    ArmLeft01.xRot = -1.4f
                    ArmLeft01.yRot = -1.4f
                    ArmLeft01.zRot = 0.87f
                    ArmLeft02.xRot = -2.1f
                    ArmLeft02.z = armLeft02DefaultZ + (-0.32f * OFFSET_SCALE)
                    EquipSL00.xRot = -1.83f
                    EquipSL00.yRot = 0.35f
                    EquipSL00.zRot = 1.57f
                }
            } else {
                setFace(EntityShipBase.FACE_WINK)
                BodyMain.xRot = -0.44f
                Head.xRot = 0.52f
                Head.yRot = 0.0f
                Head.zRot = 0.0f
                ArmLeft01.xRot = -1.05f
                ArmLeft01.yRot = -1.05f
                ArmLeft01.zRot = 1.4f
                ArmLeft02.zRot = 2.1f
                ArmLeft02.x = armLeft02DefaultX + (-0.32f * OFFSET_SCALE)
                ArmLeft02.z = armLeft02DefaultZ
                ArmRight01.xRot = -1.57f
                ArmRight01.yRot = -1.31f
                ArmRight01.zRot = 1.22f
                ArmRight02.xRot = -0.96f
                legAddLeft = ctx.angleAdd1 * 0.5f + 0.4f
                legAddRight = ctx.angleAdd2 * 0.5f + 0.09f
                LegLeft01.yRot = 0.0f
                LegLeft01.zRot = if (limbSwingAmount > 0.1f) 0.05f else 0.26f
                LegRight01.yRot = 0.0f
                LegRight01.zRot = if (limbSwingAmount > 0.1f) -0.05f else -0.26f
                Skirt01.xRot = 0.0f
                Skirt02.xRot = 0.09f
                EquipSL00.visible = false
            }
        }

        val isCrouching = entity != null && entity.isCrouching
        val isSitting =
            ctx.isSitting || (entity != null && entity.isPassenger && (entity.vehicle !is EntityMountBase))
        val isSprinting = entity != null && entity.isSprinting || limbSwingAmount > 0.9f

        if (isSprinting) {
            this.poseTranslateY += 0.1f
            Head.xRot -= 0.6f
            BodyMain.xRot = 0.9f
            Butt.xRot -= 0.7f
            Skirt01.xRot = -0.15f
            Skirt02.xRot = -0.32f
            ArmLeft01.xRot = 0.7f
            ArmLeft01.yRot = -1.1f
            ArmLeft01.zRot = -1.0f
            ArmRight01.xRot = 0.7f
            ArmRight01.yRot = 1.1f
            ArmRight01.zRot = 1.0f
            if (!EquipSR01.visible) {
                ArmRight02.zRot = 0.0f
            } else if (tickPhase > 300) {
                ArmRight02.zRot = -1.1f
            }
            legAddLeft = ctx.angleAdd1 - 0.28f
            legAddRight = ctx.angleAdd2 - 0.21f
            EquipSR01.xRot = 0.7f
        }

        if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            Head.xRot -= 1.0472f
            BodyMain.xRot = 1.0472f
            Butt.xRot = -0.4f
            Skirt01.xRot = -0.12f
            Skirt02.xRot = -0.16f
            Skirt02.y = skirt02DefaultY + (-0.1f * OFFSET_SCALE)

            if (!EquipSL00.visible) {
                ArmLeft01.xRot = -0.6f
                ArmLeft01.zRot = 0.2618f
                ArmRight01.xRot = -0.6f
                ArmRight01.zRot = -0.2618f
            } else {
                ArmLeft01.xRot = ctx.angleAdd2 * 0.25f - 0.1f
                ArmLeft01.yRot = -0.7f
                ArmLeft01.zRot = -0.3f
                ArmRight01.xRot = ctx.angleAdd1 * 0.25f - 0.1f
                ArmRight01.yRot = 0.7f
                ArmRight01.zRot = 0.3f
            }

            legAddLeft -= 0.4f
            legAddRight -= 0.4f
            EquipSR01.xRot = 0.0f
        }

        if (isSitting) {
            this.isSittingPose = true
            this.poseTranslateY += SITTING_TRANSLATE_Y

            if (entity != null && hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY -= 0.1f
                BodyMain.xRot = 0.7f
                Butt.xRot = -0.79f
                Head.xRot -= 1.2f
                if (EquipSL00.visible && hasLegacyState(entity, 7, 4)) {
                    ArmLeft01.xRot = -2.44f
                    ArmLeft01.yRot = 1.05f
                    ArmLeft01.zRot = 2.44f
                    ArmLeft02.xRot = 0.0f
                    ArmLeft02.zRot = 1.92f
                    ArmLeft02.x = armLeft02DefaultX + (-0.32f * OFFSET_SCALE)
                    ArmRight01.xRot = -1.13f
                    ArmRight01.yRot = 0.44f
                    ArmRight01.zRot = 0.52f
                    ArmRight02.xRot = 0.0f
                    ArmRight02.zRot = -0.52f
                    EquipSL00.xRot = -0.3f
                    EquipSL00.yRot = -0.22f
                    EquipSL00.zRot = 1.77f
                    EquipSR01.xRot = 0.81f
                } else {
                    ArmLeft01.xRot = -1.13f
                    ArmLeft01.yRot = -0.44f
                    ArmLeft01.zRot = -0.52f
                    ArmLeft02.xRot = 0.0f
                    ArmLeft02.zRot = 0.52f
                    ArmRight01.xRot = -1.13f
                    ArmRight01.yRot = 0.44f
                    ArmRight01.zRot = 0.52f
                    ArmRight02.xRot = 0.0f
                    ArmRight02.zRot = -0.52f
                    EquipSL00.xRot = -0.2f
                    EquipSL00.yRot = -0.1f
                    EquipSL00.zRot = 1.4f
                    EquipSR01.xRot = 0.81f
                }
                legAddLeft = -2.1f
                legAddRight = -2.1f
                LegLeft01.yRot = -0.58f
                LegLeft01.zRot = 0.05f
                LegLeft02.xRot = 2.44f
                LegLeft02.z = legLeft02DefaultZ + (0.38f * OFFSET_SCALE)
                LegRight01.yRot = 0.58f
                LegRight01.zRot = -0.05f
                LegRight02.xRot = 2.44f
                LegRight02.z = legRight02DefaultZ + (0.38f * OFFSET_SCALE)
                Skirt01.xRot = -0.17f
                Skirt02.xRot = -0.26f
            } else {
                BodyMain.xRot = 0.0873f
                Butt.xRot = -0.1745f
                Head.xRot -= 0.2f
                ArmLeft01.xRot = 0.2618f
                ArmLeft01.yRot = 0.0f
                ArmLeft01.zRot = -0.2618f
                ArmRight01.xRot = -1.1345f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = 0.0f
                ArmRight02.zRot = -1.2217f
                legAddLeft = -1.45f
                legAddRight = -2.1f
                LegLeft01.xRot = -1.4486f
                LegLeft01.yRot = 0.0873f
                LegLeft01.zRot = 0.0f
                LegRight01.xRot = -2.0944f
                LegRight01.yRot = 0.0911f
                LegRight01.zRot = 0.1745f
                LegRight02.xRot = 1.3963f
                Skirt01.xRot = -0.17f
                Skirt02.xRot = -0.26f
                EquipSL00.xRot = -1.6755f
                EquipSL00.yRot = 0.1745f
                EquipSL00.zRot = 0.8727f
                EquipSR01.xRot = 1.3090f
                EquipSR01.yRot = -0.1396f
                EquipSR01.zRot = -0.1396f
            }
        }

        if (entity != null && entity.attackTick > 30) {
            this.poseTranslateY = 0.22f + entity.scaleLevel * 0.12f
            Head.xRot = -0.4363f
            Head.yRot = 0.0f
            Head.zRot = 0.0f
            BodyMain.xRot = 1.0472f
            BodyMain.yRot = 0.2618f
            BodyMain.zRot = 0.0f
            Butt.xRot = -0.5236f
            Butt.yRot = 0.0f
            Butt.zRot = 0.0f
            ArmLeft01.xRot = -0.7854f
            ArmLeft01.yRot = 0.2618f
            ArmLeft01.zRot = 0.5236f
            ArmLeft02.xRot = 0.0f
            ArmLeft02.yRot = 0.0f
            ArmLeft02.zRot = 0.7854f
            ArmRight01.xRot = 0.5236f
            ArmRight01.yRot = -0.3491f
            ArmRight01.zRot = 0.1745f
            ArmRight02.xRot = -1.3090f
            ArmRight02.yRot = 0.0f
            ArmRight02.zRot = 0.0f
            legAddLeft = 0.31f
            legAddRight = -1.57f
            LegLeft01.yRot = -0.1745f
            LegLeft01.zRot = 0.0873f
            LegLeft02.xRot = 0.13f
            LegRight01.yRot = 0.0f
            LegRight01.zRot = 0.1396f
            LegRight02.xRot = 1.2292f
            EquipSL00.visible = true
            EquipSR01.xRot = 0.8652f
            EquipSR01.yRot = -0.1396f
            EquipSR01.zRot = -0.1396f
            EquipSL00.xRot = 1.5935f
            EquipSL00.yRot = 0.1820f
            EquipSL00.zRot = 1.5708f

            if (entity.attackTick < 51) {
                if (entity.attackTick > 45) {
                    val tick = 4 - (entity.attackTick - 46)
                    val parTick = ageInTicks - ageInTicks.toInt() + tick
                    ArmLeft01.xRot = -0.785f - 0.644f * parTick
                    ArmLeft02.zRot = 0.785f - 0.157f * parTick
                    EquipSL00.yRot = 0.182f + 0.278f * parTick
                } else {
                    ArmLeft01.xRot = -4.1f
                    ArmLeft02.zRot = 0.0f
                    EquipSL00.yRot = 1.57f
                }
            }

            if (hasLegacyState(entity, 5, 3)) {
                BodyMain.xRot = 2.1f
                ArmLeft01.xRot = -1.92f
                ArmLeft01.yRot = 0.4f
                ArmLeft01.zRot = 0.26f
                ArmLeft02.zRot = 0.0f
                ArmRight01.xRot = -1.92f
                ArmRight01.yRot = -0.4f
                ArmRight01.zRot = 0.26f
                ArmRight02.xRot = 0.0f
                EquipSL00.xRot = -1.4f
                EquipSL00.yRot = -0.14f
                EquipSL00.zRot = 1.57f
            }
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

        LegLeft01.xRot = legAddLeft
        LegRight01.xRot = legAddRight
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

    protected override fun syncExtraGlowParts() {
        GlowEquip00?.copyFrom(Equip00)
        GlowEquip01a.copyFrom(Equip01a)
        GlowEquip02a.copyFrom(Equip02a)
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "cruiser_tenryuu"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelCruiserTenryuu")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelCruiserTenryuu")
        private val SITTING_TRANSLATE_Y = sittingY("ModelCruiserTenryuu")
        private val SITTING_ALT_TRANSLATE_Y = sittingAltY("ModelCruiserTenryuu")

        private val BODY_BASE_X_ROT = -0.1047f
        private const val BUTT_BASE_X_ROT = 0.35f
        private val SKIRT_BASE_X_ROT = -0.14f
        private val SKIRT02_BASE_X_ROT = -0.09f
        private const val LEG_BASE_Z_ROT = 0.0873f

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
                CubeListBuilder.create().texOffs(24, 84).mirror()
                    .addBox(-3f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, -9.3f, -0.7f, 0f, 0f, 0.3491f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(24, 63).mirror().addBox(0f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 11f, 2.5f)
            )

            val ArmRight02a = ArmRight02.addOrReplaceChild(
                "ArmRight02a",
                CubeListBuilder.create().texOffs(104, 33).mirror()
                    .addBox(-3f, 0f, -3f, 6f, 3f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.5f, 1.3f, -2.4f, 0.0698f, 0f, 0f)
            )

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(16, 22).addBox(-4f, 0f, -4f, 8f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.7f, -0.2f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(24, 84).addBox(-2f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.3f, -0.7f, 0.1047f, 0f, -0.3491f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(24, 63).addBox(-5f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 11f, 2.5f)
            )

            val EquipSL00 = ArmLeft02.addOrReplaceChild(
                "EquipSL00",
                CubeListBuilder.create().texOffs(98, 27).addBox(0f, -4f, -0.5f, 2f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, 9.3f, -3f, -1.5708f, -0.1396f, 1.5708f)
            )

            val EquipSL00b = EquipSL00.addOrReplaceChild(
                "EquipSL00b",
                CubeListBuilder.create().texOffs(66, 40).addBox(0f, -2f, -0.5f, 3f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.1f, -3.8f, 0f, 0f, 0f, 0.1396f)
            )

            val EquipSL01 = EquipSL00.addOrReplaceChild(
                "EquipSL01",
                CubeListBuilder.create().texOffs(90, 0).addBox(-2.5f, 0f, -0.5f, 3f, 12f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.1f, 4.7f, 0f, 0f, 0f, 0.0698f)
            )

            val EquipSL02 = EquipSL01.addOrReplaceChild(
                "EquipSL02",
                CubeListBuilder.create().texOffs(90, 0).addBox(-2.5f, 0f, -0.5f, 3f, 11f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 11.9f, 0f, 0f, 0f, 0.1047f)
            )

            val EquipSL03 = EquipSL02.addOrReplaceChild(
                "EquipSL03",
                CubeListBuilder.create().texOffs(90, 0).addBox(-2.5f, 0f, -0.5f, 3f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10.9f, 0f, 0f, 0f, 0.1396f)
            )

            val EquipSL03a = EquipSL03.addOrReplaceChild(
                "EquipSL03a",
                CubeListBuilder.create().texOffs(46, 62).addBox(-2.5f, 0f, -0.5f, 2f, 11f, 1f, CubeDeformation(0f)),
                PartPose.offset(-1.7f, -3f, -0.2f)
            )

            val EquipSL02a = EquipSL02.addOrReplaceChild(
                "EquipSL02a",
                CubeListBuilder.create().texOffs(46, 62).mirror()
                    .addBox(0f, 0f, -0.5f, 2f, 11f, 1f, CubeDeformation(0f)),
                PartPose.offset(-4.3f, -3f, 0f)
            )

            val EquipSL00a = EquipSL00.addOrReplaceChild(
                "EquipSL00a",
                CubeListBuilder.create().texOffs(67, 35).addBox(0f, 0f, -1f, 4f, 1f, 2f, CubeDeformation(0f)),
                PartPose.offset(-0.7f, 3.9f, 0f)
            )

            val ArmLeft02a = ArmLeft02.addOrReplaceChild(
                "ArmLeft02a",
                CubeListBuilder.create().texOffs(104, 33).addBox(-3f, 0f, -3f, 6f, 3f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.5f, 1.3f, -2.4f, 0.0698f, 0f, 0f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(29, 12).addBox(-2.5f, -2f, -3.6f, 5f, 2f, 5f, CubeDeformation(0f)),
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

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(52, 56).addBox(-8.5f, 0f, 0f, 17f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -7f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(106, 31).addBox(0f, -6f, -10.5f, 0f, 11f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.5f, -7f, -6f, 0.2094f, 0.6981f, 0f)
            )

            val EyeMask = Head.addOrReplaceChild(
                "EyeMask",
                CubeListBuilder.create().texOffs(114, 17).addBox(0f, 0f, 0f, 6f, 5f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.7f, -8.4f, -6.7f, 0f, 0f, 0.4363f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(46, 104).addBox(-7.5f, 0f, 0f, 15f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.8f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(46, 21).addBox(-7.5f, 0f, 0f, 15f, 5f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 11.5f, 3.3f, 0.2618f, 0f, 0f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(33, 101).mirror()
                    .addBox(-3.5f, 0f, 0f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.5f, -8.1f, -3.7f, -0.6981f, 0.0873f, 0.0873f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 47).addBox(-7.5f, 0f, -5.7f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.3f, 0.3491f, 0f, 0f)
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

            val ShoeR01 = LegRight02.addOrReplaceChild(
                "ShoeR01",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offset(3f, 10.5f, 3f)
            )

            val ShoeR02 = ShoeR01.addOrReplaceChild(
                "ShoeR02",
                CubeListBuilder.create().texOffs(74, 6).mirror()
                    .addBox(-0.5f, 0f, -10f, 1f, 3f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.3f, -2.5f, -0.1745f, 0f, 0f)
            )

            val ShoeR00 = LegRight02.addOrReplaceChild(
                "ShoeR00",
                CubeListBuilder.create().texOffs(6, 5).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 4f, 7f, CubeDeformation(0f)),
                PartPose.offset(3f, 4.2f, 3f)
            )

            val Skirt01 = Butt.addOrReplaceChild(
                "Skirt01",
                CubeListBuilder.create().texOffs(46, 43).addBox(-8.5f, 0f, -6f, 17f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.9f, 0f, -0.1396f, 0f, 0f)
            )

            val Skirt02 = Skirt01.addOrReplaceChild(
                "Skirt02",
                CubeListBuilder.create().texOffs(0, 33).addBox(-9f, 0f, -6f, 18f, 4f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.8f, -0.5f, -0.0873f, 0f, 0f)
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

            val ShoeL01 = LegLeft02.addOrReplaceChild(
                "ShoeL01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, -3.5f, 7f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3f, 10.5f, 3f, 0.0873f, 0f, 0f)
            )

            val ShoeL02 = ShoeL01.addOrReplaceChild(
                "ShoeL02",
                CubeListBuilder.create().texOffs(74, 6).addBox(-0.5f, 0f, -10f, 1f, 3f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.3f, -2.5f, -0.1745f, 0f, 0f)
            )

            val ShoeL00 = LegLeft02.addOrReplaceChild(
                "ShoeL00",
                CubeListBuilder.create().texOffs(6, 5).addBox(-3.6f, 0f, -3.5f, 7f, 4f, 7f, CubeDeformation(0f)),
                PartPose.offset(-3f, 4.2f, 3f)
            )

            val EquipSR01 = BodyMain.addOrReplaceChild(
                "EquipSR01",
                CubeListBuilder.create().texOffs(118, 0).addBox(-1f, -2f, -1.5f, 2f, 12f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9f, 5.5f, -5f, 1.309f, -0.1396f, -0.1396f)
            )

            val EquipSR02 = EquipSR01.addOrReplaceChild(
                "EquipSR02",
                CubeListBuilder.create().texOffs(108, 0).addBox(-1f, 0f, -3f, 2f, 12f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10f, 1.5f, -0.0524f, 0f, 0f)
            )

            val EquipSR03 = EquipSR02.addOrReplaceChild(
                "EquipSR03",
                CubeListBuilder.create().texOffs(98, 0).addBox(-1f, 0f, -3f, 2f, 12f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12f, 0f, -0.0524f, 0f, 0f)
            )

            val Equip00 = BodyMain.addOrReplaceChild(
                "Equip00",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, -1.5f, -2f, 3f, 3f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 5f, 0.0873f, 0f, 0f)
            )

            val Equip01a = Equip00.addOrReplaceChild(
                "Equip01a",
                CubeListBuilder.create().texOffs(28, 0).addBox(-3.5f, 0f, 0f, 7f, 7f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, -12f, 0f)
            )

            val Equip01b = Equip01a.addOrReplaceChild(
                "Equip01b",
                CubeListBuilder.create().texOffs(52, 0).addBox(-5.5f, 0f, 0f, 11f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 7f, 0f)
            )

            val Equip01c = Equip01a.addOrReplaceChild(
                "Equip01c",
                CubeListBuilder.create().texOffs(28, 0).addBox(-3.5f, 0f, 0f, 7f, 7f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 5f)
            )

            val Equip01d = Equip01c.addOrReplaceChild(
                "Equip01d",
                CubeListBuilder.create().texOffs(52, 0).addBox(-5.5f, 0f, 0f, 11f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 7f, 0f)
            )

            val Equip03R = Equip01d.addOrReplaceChild(
                "Equip03R",
                CubeListBuilder.create().texOffs(86, 104).mirror().addBox(-4f, 0f, 0f, 4f, 8f, 2f, CubeDeformation(0f)),
                PartPose.offset(-5f, 1.5f, 4.5f)
            )

            val EquipCR01 = Equip03R.addOrReplaceChild(
                "EquipCR01",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-2f, -1f, -1f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(-3.5f, 3.5f, 2f)
            )

            val EquipCR02 = EquipCR01.addOrReplaceChild(
                "EquipCR02",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-5f, -3f, -2f, 5f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offset(-1.9f, 0f, 0f)
            )

            val EquipCR03 = EquipCR02.addOrReplaceChild(
                "EquipCR03",
                CubeListBuilder.create().texOffs(0, 18).mirror().addBox(-5f, 0f, 0f, 5f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -7f, -1.5f)
            )

            val EquipCR04 = EquipCR03.addOrReplaceChild(
                "EquipCR04",
                CubeListBuilder.create().texOffs(46, 36).mirror()
                    .addBox(-1.5f, -5.8f, -1.5f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offset(-2.5f, 3f, 3f)
            )

            val EquipCR05 = EquipCR04.addOrReplaceChild(
                "EquipCR05",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-1f, -13.6f, -1f, 2f, 8f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val Equip03L = Equip01d.addOrReplaceChild(
                "Equip03L",
                CubeListBuilder.create().texOffs(86, 104).addBox(0f, 0f, 0f, 4f, 8f, 2f, CubeDeformation(0f)),
                PartPose.offset(5f, 1.5f, 4.5f)
            )

            val EquipCL01 = Equip03L.addOrReplaceChild(
                "EquipCL01",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -1f, -1f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(3.5f, 3.5f, 2f)
            )

            val EquipCL02 = EquipCL01.addOrReplaceChild(
                "EquipCL02",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -3f, -2f, 5f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offset(1.9f, 0f, 0f)
            )

            val EquipCL03 = EquipCL02.addOrReplaceChild(
                "EquipCL03",
                CubeListBuilder.create().texOffs(0, 18).addBox(0f, 0f, 0f, 5f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -7f, -1.5f)
            )

            val EquipCL04 = EquipCL03.addOrReplaceChild(
                "EquipCL04",
                CubeListBuilder.create().texOffs(46, 36).addBox(-1.5f, -5.8f, -1.5f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offset(2.5f, 3f, 3f)
            )

            val EquipCL05 = EquipCL04.addOrReplaceChild(
                "EquipCL05",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -13.6f, -1f, 2f, 8f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val Equip02a = Equip01a.addOrReplaceChild(
                "Equip02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, 0f, 9f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, -0.4f, 10f)
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

            val EarL01 = GlowHead.addOrReplaceChild(
                "EarL01",
                CubeListBuilder.create().texOffs(43, 75).addBox(-1f, -2.5f, -2.5f, 2f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(9f, -11f, 4f, 0.0873f, -0.1745f, -0.0873f)
            )

            val EarL02 = EarL01.addOrReplaceChild(
                "EarL02",
                CubeListBuilder.create().texOffs(88, 41).addBox(0f, -4f, -3.5f, 2f, 4f, 7f, CubeDeformation(0f)),
                PartPose.offset(-1f, -2.5f, -1f)
            )

            val EarL03 = EarL02.addOrReplaceChild(
                "EarL03",
                CubeListBuilder.create().texOffs(88, 31).addBox(0f, -5f, 0f, 2f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -3f, -3.2f)
            )

            val EarL04 = EarL03.addOrReplaceChild(
                "EarL04",
                CubeListBuilder.create().texOffs(74, 34).addBox(0f, -4f, 0f, 2f, 4f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, -5f, 0.3f)
            )

            val EarR01 = GlowHead.addOrReplaceChild(
                "EarR01",
                CubeListBuilder.create().texOffs(43, 75).mirror()
                    .addBox(-1f, -2.5f, -2.5f, 2f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9f, -11f, 4f, 0.0873f, 0.1745f, 0.0873f)
            )

            val EarR02 = EarR01.addOrReplaceChild(
                "EarR02",
                CubeListBuilder.create().texOffs(88, 41).mirror()
                    .addBox(0f, -4f, -3.5f, 2f, 4f, 7f, CubeDeformation(0f)),
                PartPose.offset(-1f, -2.5f, -1f)
            )

            val EarR03 = EarR02.addOrReplaceChild(
                "EarR03",
                CubeListBuilder.create().texOffs(88, 31).mirror().addBox(0f, -5f, 0f, 2f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -3f, -3.2f)
            )

            val EarR04 = EarR03.addOrReplaceChild(
                "EarR04",
                CubeListBuilder.create().texOffs(74, 34).mirror().addBox(0f, -4f, 0f, 2f, 4f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, -5f, 0.3f)
            )

            val GlowEquip00 = GlowBodyMain.addOrReplaceChild(
                "GlowEquip00",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 4f, 5f, 0.0873f, 0f, 0f)
            )

            val GlowEquip01a = GlowEquip00.addOrReplaceChild(
                "GlowEquip01a",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -12f, 0f)
            )

            val GlowEquip02a = GlowEquip01a.addOrReplaceChild(
                "GlowEquip02a",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -0.4f, 10f)
            )

            val Equip02b = GlowEquip02a.addOrReplaceChild(
                "Equip02b",
                CubeListBuilder.create().texOffs(104, 23).addBox(-4f, 0f, 0f, 8f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 4f)
            )

            val Equip02c = Equip02b.addOrReplaceChild(
                "Equip02c",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, 0f, 7f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 3f, 4f)
            )

            val Equip02d = Equip02c.addOrReplaceChild(
                "Equip02d",
                CubeListBuilder.create().texOffs(0, 28).addBox(-1f, 0f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
