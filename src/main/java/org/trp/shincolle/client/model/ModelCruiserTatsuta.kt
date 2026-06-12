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
import org.trp.shincolle.client.model.LegacyPoseOffsets.sittingY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sneakY
import org.trp.shincolle.entity.EntityCruiserTatsuta
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelCruiserTatsuta<T : EntityShipBase>(root: ModelPart) : ShincolleShipModel<T>() {

    private val BodyMain: ModelPart
    protected override val bodyMain: ModelPart get() = BodyMain
    private val Neck: ModelPart
    protected override val neck: ModelPart get() = Neck
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val Butt: ModelPart
    private val ArmRight01: ModelPart
    private val ArmLeft01: ModelPart
    private val Cloth01: ModelPart
    private val Equip00: ModelPart
    private val Head: ModelPart
    protected override val head: ModelPart get() = Head
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val CirBase: ModelPart
    private val HairU01: ModelPart
    private val Ahoke: ModelPart
    private val Hair01: ModelPart
    private val Cir00: ModelPart
    private val Cir01: ModelPart
    private val Cir02: ModelPart
    private val Cir03: ModelPart
    private val Cir04: ModelPart
    private val LegLeft01: ModelPart
    private val Skirt01: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft02: ModelPart
    private val Skirt02: ModelPart
    private val LegRight02: ModelPart
    private val ArmRight02: ModelPart
    private val ArmLeft02: ModelPart
    private val EquipSL00: ModelPart
    private val EquipSL01: ModelPart
    private val EquipSL04: ModelPart
    private val EquipSL02: ModelPart
    private val EquipSL03a: ModelPart
    private val EquipSL03b: ModelPart
    private val EquipSL03c: ModelPart
    private val EquipSL05: ModelPart
    private val Equip01a: ModelPart
    private val Equip01b: ModelPart
    private val Equip01c: ModelPart
    private val Equip02a: ModelPart
    private val Equip01d: ModelPart
    private val Equip03L: ModelPart
    private val Equip03R: ModelPart
    private val EquipCL01: ModelPart
    private val EquipCL02: ModelPart
    private val EquipCL03a: ModelPart
    private val EquipCL03b: ModelPart
    private val EquipCL03c: ModelPart
    private val EquipCR01: ModelPart
    private val EquipCR02: ModelPart
    private val EquipCR03a: ModelPart
    private val EquipCR03b: ModelPart
    private val EquipCR03c: ModelPart
    private val Equip02b: ModelPart
    private val Equip02c: ModelPart
    private val Equip02d: ModelPart
    private val GlowBodyMain: ModelPart?
    protected override val glowBodyMain: ModelPart? get() = GlowBodyMain
    private val GlowNeck: ModelPart
    protected override val glowNeck: ModelPart? get() = GlowNeck
    private val GlowHead: ModelPart
    protected override val glowHead: ModelPart? get() = GlowHead
    private val GlowBodyMain2: ModelPart
    protected override val glowBodyMain2: ModelPart? get() = GlowBodyMain2
    private val GlowNeck2: ModelPart
    protected override val glowNeck2: ModelPart? get() = GlowNeck2
    private val GlowHead2: ModelPart
    protected override val glowHead2: ModelPart? get() = GlowHead2
    private val GlowEquip00: ModelPart?
    private val GlowEquip01a: ModelPart
    private val GlowEquip02a: ModelPart
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
    private val cirBaseDefaultY: Float
    private val hair01DefaultZRot: Float
    private val equipSL00DefaultX: Float
    private val equipSL00DefaultY: Float
    private val equipSL00DefaultZ: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.EquipSL00 = this.ArmLeft02.getChild("EquipSL00")
        this.equipSL00DefaultX = this.EquipSL00.x
        this.equipSL00DefaultY = this.EquipSL00.y
        this.equipSL00DefaultZ = this.EquipSL00.z
        this.EquipSL04 = this.EquipSL00.getChild("EquipSL04")
        this.EquipSL05 = this.EquipSL04.getChild("EquipSL05")
        this.EquipSL01 = this.EquipSL00.getChild("EquipSL01")
        this.EquipSL02 = this.EquipSL01.getChild("EquipSL02")
        this.EquipSL03c = this.EquipSL02.getChild("EquipSL03c")
        this.EquipSL03a = this.EquipSL02.getChild("EquipSL03a")
        this.EquipSL03b = this.EquipSL02.getChild("EquipSL03b")
        this.Equip00 = this.BodyMain.getChild("Equip00")
        this.Equip01a = this.Equip00.getChild("Equip01a")
        this.Equip02a = this.Equip01a.getChild("Equip02a")
        this.Equip01b = this.Equip01a.getChild("Equip01b")
        this.Equip01c = this.Equip01a.getChild("Equip01c")
        this.Equip01d = this.Equip01c.getChild("Equip01d")
        this.Equip03R = this.Equip01d.getChild("Equip03R")
        this.EquipCR01 = this.Equip03R.getChild("EquipCR01")
        this.EquipCR02 = this.EquipCR01.getChild("EquipCR02")
        this.EquipCR03c = this.EquipCR02.getChild("EquipCR03c")
        this.EquipCR03b = this.EquipCR02.getChild("EquipCR03b")
        this.EquipCR03a = this.EquipCR02.getChild("EquipCR03a")
        this.Equip03L = this.Equip01d.getChild("Equip03L")
        this.EquipCL01 = this.Equip03L.getChild("EquipCL01")
        this.EquipCL02 = this.EquipCL01.getChild("EquipCL02")
        this.EquipCL03b = this.EquipCL02.getChild("EquipCL03b")
        this.EquipCL03a = this.EquipCL02.getChild("EquipCL03a")
        this.EquipCL03c = this.EquipCL02.getChild("EquipCL03c")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.Skirt02 = this.Skirt01.getChild("Skirt02")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        this.initFaceParts(this.GlowHead)
        this.GlowEquip00 = this.GlowBodyMain.getChild("GlowEquip00")
        this.GlowEquip01a = this.GlowEquip00.getChild("GlowEquip01a")
        this.GlowEquip02a = this.GlowEquip01a.getChild("GlowEquip02a")
        this.Equip02b = this.GlowEquip02a.getChild("Equip02b")
        this.Equip02c = this.Equip02b.getChild("Equip02c")
        this.Equip02d = this.Equip02c.getChild("Equip02d")
        this.GlowBodyMain2 = root.getChild("GlowBodyMain2")
        this.GlowNeck2 = this.GlowBodyMain2.getChild("GlowNeck2")
        this.GlowHead2 = this.GlowNeck2.getChild("GlowHead2")
        this.CirBase = this.GlowHead2.getChild("CirBase")
        this.Cir00 = this.CirBase.getChild("Cir00")
        this.Cir01 = this.Cir00.getChild("Cir01")
        this.Cir02 = this.Cir00.getChild("Cir02")
        this.Cir03 = this.Cir00.getChild("Cir03")
        this.Cir04 = this.Cir00.getChild("Cir04")
        this.cirBaseDefaultY = this.CirBase.y
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
        this.hair01DefaultZRot = this.Hair01.zRot
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
        super.resetPoseState()
        resetOffsets()

        applyFaceAndMouth(entity)
        applyEquipVisibility(entity)

        if (isDeadPose(entity)) {
            applyDeadPose()
            syncGlowParts()
            return
        }

        applyHeadRotation(Head, entity, ageInTicks, netHeadYaw, headPitch)

        applyBasePose(ctx, ageInTicks, headPitch)
        applySpecialPoseAdjustments(entity, ctx, ageInTicks, limbSwingAmount)

        val headX = Head.xRot * -0.5f
        val headZ = Head.zRot * -0.5f
        Hair01.xRot += headX
        Hair01.zRot += headZ

        syncGlowParts()
    }

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) return

        val showEquip = entity.getEquipFlag(EntityCruiserTatsuta.EQUIP_RIGGING)
        Equip00.visible = showEquip
        if (GlowEquip00 != null) GlowEquip00.visible = showEquip
        CirBase.visible = entity.getEquipFlag(EntityCruiserTatsuta.EQUIP_RING)
        EquipSL00.visible = entity.getEquipFlag(EntityCruiserTatsuta.EQUIP_SIDE)
    }

    private fun applyDeadPose() {
        beginDeadPose(DEAD_TRANSLATE_Y)

        Head.xRot = 0.9599f
        Head.yRot = 0.0f
        Head.zRot = 0.0f
        Ahoke.xRot = 0.2618f
        Ahoke.yRot = 1.8326f
        Ahoke.zRot = 0.2618f

        BodyMain.xRot = -0.2618f
        Butt.xRot = -0.2618f
        Skirt01.xRot = -0.1745f
        Skirt02.xRot = -0.2094f

        ArmLeft01.xRot = 0.4142f
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = -0.4363f
        ArmLeft02.xRot = -0.1047f
        ArmLeft02.yRot = 0.0f
        ArmLeft02.zRot = 0.0f

        ArmRight01.xRot = 0.3618f
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = 0.2731f
        ArmRight02.xRot = -0.2731f
        ArmRight02.yRot = 0.0f
        ArmRight02.zRot = 0.0f

        EquipSL00.xRot = -1.6835f
        EquipSL00.yRot = 0.0f
        EquipSL00.zRot = -1.1f
        EquipCL02.xRot = 1.63f
        EquipCR02.xRot = 1.63f

        Cir00.yRot = 0.0f
        CirBase.y = cirBaseDefaultY + (0.26f * OFFSET_SCALE)

        LegLeft01.xRot = -1.7453f
        LegLeft01.yRot = -0.5463f
        LegLeft01.zRot = 1.4835f
        LegLeft02.xRot = 0.4363f
        LegLeft02.yRot = 0.0f
        LegLeft02.zRot = 0.0f

        LegRight01.xRot = -1.5708f
        LegRight01.yRot = 0.0873f
        LegRight01.zRot = -0.1745f
        LegRight02.xRot = 1.1345f
        LegRight02.yRot = 0.0f
        LegRight02.zRot = 0.0f
    }

    private fun applyBasePose(ctx: PoseContext, ageInTicks: Float, headPitch: Float) {
        val angleX = ctx.angleX

        Neck.xRot = 0.1047f
        Neck.yRot = 0.0f
        Neck.zRot = 0.0f

        BodyMain.xRot = BODY_BASE_X_ROT
        BodyMain.yRot = 0.0f
        BodyMain.zRot = 0.0f
        Butt.xRot = BUTT_BASE_X_ROT
        Skirt01.xRot = SKIRT_BASE_X_ROT
        Skirt02.xRot = SKIRT02_BASE_X_ROT

        Cloth01.xRot = angleX * 0.06f - 0.7f
        BoobL.xRot = angleX * 0.06f - 0.8f
        BoobR.xRot = angleX * 0.06f - 0.8f
        Hair01.xRot = angleX * 0.04f + 0.2618f
        Hair01.zRot = hair01DefaultZRot

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

        Cir00.yRot = 0.0f
        CirBase.y = cirBaseDefaultY

        EquipSL00.xRot = -1.1f
        EquipSL00.yRot = 0.4f
        EquipSL00.zRot = 0.0f

        EquipCL02.xRot = headPitch * 0.015f + 0.7f
        EquipCR02.xRot = headPitch * 0.015f + 0.7f
    }

    private fun applySpecialPoseAdjustments(entity: T?, ctx: PoseContext, ageInTicks: Float, limbSwingAmount: Float) {
        var legAddLeft = ctx.angleAdd1 * 0.5f - 0.28f
        var legAddRight = ctx.angleAdd2 * 0.5f - 0.21f

        if (entity != null && entity.shipDepth > 0.0) {
            this.poseTranslateY += ctx.angleX * 0.05f + 0.025f
        }

        var spcStand = false
        val tickPhase = if (entity != null) (entity.tickCount and 0x1FF) else 0
        if (entity != null && hasLegacyState(entity, 1, 4)) {
            spcStand = true
            if (tickPhase > 320) {
                setFace(EntityShipBase.FACE_WINK)
            } else if (tickPhase > 160) {
                setFace(EntityShipBase.FACE_EYES_OPEN)
            } else {
                setFace(EntityShipBase.FACE_EYES_CLOSED)
            }
            Head.yRot = 0.0f
            ArmLeft01.xRot = -0.3491f
            ArmLeft01.yRot = 0.0f
            ArmLeft01.zRot = 0.4554f
            ArmLeft02.xRot = 0.0f
            ArmLeft02.yRot = 0.0f
            ArmLeft02.zRot = 1.0472f
            ArmRight01.xRot = -0.5463f
            ArmRight01.yRot = -0.2618f
            ArmRight01.zRot = -0.1396f
            ArmRight02.xRot = -2.5307f
            ArmRight02.zRot = 0.0f
            ArmRight02.z = armRight02DefaultZ + (-0.32f * OFFSET_SCALE)
            EquipSL00.xRot = -1.17f
            EquipSL00.yRot = 1.45f
            EquipSL00.zRot = 0.0f
            if (hasLegacyState(entity, 7, 4)) {
                ArmLeft01.xRot = 0.6981f
                ArmLeft01.yRot = -1.0472f
                ArmLeft01.zRot = -2.4435f
                ArmLeft02.xRot = -1.3963f
                ArmLeft02.yRot = 0.0f
                ArmLeft02.zRot = 0.0f
                EquipSL00.xRot = -1.5708f
                EquipSL00.yRot = 0.9f
                EquipSL00.zRot = 0.0f
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
            ArmRight02.zRot = 0.0f

            legAddLeft = ctx.angleAdd1 - 0.28f
            legAddRight = ctx.angleAdd2 - 0.21f

            EquipSL00.xRot = -1.5f
            EquipSL00.yRot = 0.2f
            EquipSL00.zRot = 0.0f
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

            Cir00.yRot = ageInTicks * 0.025f
        }

        if (isSitting) {
            this.isSittingPose = true

            if (entity != null && hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY += 0.5f * 3.2f
                Head.xRot = 0.0f
                Head.yRot += 1.2217f
                Head.zRot = -0.0873f
                BodyMain.xRot = -0.35f
                BodyMain.yRot = -1.4486f
                Butt.xRot = -0.3840f
                Skirt01.xRot = -0.1745f
                Skirt02.xRot = -0.2618f
                ArmLeft01.xRot = -1.22f
                ArmLeft01.yRot = 0.3142f
                ArmLeft01.zRot = 0.0f
                ArmRight01.xRot = -0.1745f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = 0.2618f
                legAddLeft = -1.57f
                legAddRight = -1.4f
                LegLeft01.zRot = 0.0873f
                LegLeft02.xRot = 0.6109f
                LegRight01.yRot = 0.0f
                LegRight01.zRot = -0.0873f
                LegRight02.xRot = 1.4835f
                Cir00.yRot = ageInTicks * 0.025f
                EquipSL00.xRot = 1.42f
                EquipSL00.yRot = -0.18f
                EquipSL00.zRot = 0.0f
                EquipSL00.y = equipSL00DefaultY + (0.15f * OFFSET_SCALE)
            } else {
                this.poseTranslateY += SITTING_TRANSLATE_Y
                BodyMain.xRot = -0.3f
                Butt.xRot = -0.2f
                Skirt01.xRot = -0.26f
                Skirt02.xRot = -0.45f
                legAddLeft = -0.9f
                legAddRight = -0.9f
                LegLeft01.zRot = -0.14f
                LegLeft02.xRot = 1.2217f
                LegLeft02.yRot = 1.2217f
                LegLeft02.zRot = -1.0472f
                LegLeft02.y = legLeft02DefaultY + (-0.06f * OFFSET_SCALE)
                LegRight01.zRot = 0.14f
                LegRight02.xRot = 1.2217f
                LegRight02.yRot = -1.2217f
                LegRight02.zRot = 1.0472f
                LegRight02.y = legRight02DefaultY + (-0.06f * OFFSET_SCALE)
                Cir00.yRot = ageInTicks * 0.025f
                EquipSL00.xRot = -1.06f
                EquipSL00.yRot = 0.02f
                EquipSL00.zRot = -1.29f
                if (spcStand) {
                    ArmRight01.xRot += 0.3f
                    if (entity != null && hasLegacyState(entity, 7, 4)) {
                        EquipSL00.xRot = -1.5708f
                        EquipSL00.yRot = 1.2f
                        EquipSL00.zRot = 0.0f
                    } else {
                        EquipSL00.visible = false
                    }
                } else {
                    ArmLeft01.xRot += 0.1f
                    ArmLeft01.yRot = 0.0f
                    ArmLeft01.zRot = -0.25f
                    ArmRight01.xRot += 0.3f
                    ArmRight01.yRot = 0.0f
                    ArmRight01.zRot = 0.25f
                }
            }
        }

        if (entity != null && entity.attackTick > 30) {
            if (!hasLegacyState(entity, 5, 1)) {
                this.poseTranslateY += 0.05f + entity.scaleLevel * 0.02f
                BodyMain.xRot = 0.1745f
                BodyMain.yRot = 0.0f
                BodyMain.zRot = 0.0f
                Butt.xRot = 0.0f
                Head.xRot = -0.1f
                Skirt01.xRot = -0.1396f
                Skirt02.xRot = -0.0873f
                ArmLeft01.xRot = -1.6755f
                ArmLeft01.yRot = 0.5236f
                ArmLeft01.zRot = 0.0f
                ArmLeft02.xRot = 0.0f
                ArmLeft02.yRot = 0.0f
                ArmLeft02.zRot = 0.0f
                ArmRight01.xRot = 0.5236f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = 0.5236f
                ArmRight02.xRot = 0.0f
                ArmRight02.yRot = 0.0f
                ArmRight02.zRot = 0.0f
                legAddLeft = -0.5236f
                legAddRight = 0.2618f
                LegLeft01.yRot = 0.0f
                LegLeft01.zRot = 0.0873f
                LegLeft02.xRot = 0.3643f
                LegRight01.yRot = 0.0f
                LegRight01.zRot = -0.0873f
                EquipSL00.visible = true
                EquipSL00.xRot = -0.1367f
                EquipSL00.yRot = 1.5708f
                EquipSL00.zRot = 0.1367f
                if (entity.attackTick < 51) {
                    if (entity.attackTick > 45) {
                        val tick = 4 - (entity.attackTick - 46)
                        val parTick = ageInTicks - ageInTicks.toInt() + tick
                        ArmLeft01.yRot = 0.52f - 0.524f * parTick
                    } else {
                        ArmLeft01.yRot = -2.1f
                    }
                }
            } else {
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
                ArmRight02.zRot = 0.0f
                legAddLeft = ctx.angleAdd1 - 0.28f
                legAddRight = ctx.angleAdd2 - 0.21f
                EquipSL00.xRot = -1.5f
                EquipSL00.yRot = 0.2f
                EquipSL00.zRot = 0.0f
            }

            if (hasLegacyState(entity, 5, 2)) {
                Head.xRot = -0.2618f
                BodyMain.xRot = 0.0f
                BodyMain.yRot = ageInTicks * -2.0f
                ArmLeft01.xRot = -1.6755f
                ArmLeft01.yRot = -1.3963f
                ArmLeft01.zRot = 0.0f
                ArmRight01.xRot = 0.1745f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = 1.6755f
                legAddLeft = -0.5236f
                legAddRight = 0.1396f
                LegLeft01.yRot = 0.0f
                LegLeft01.zRot = 0.0873f
                LegLeft02.xRot = 1.0472f
                LegRight01.yRot = 0.0f
                LegRight01.zRot = -0.0873f
            } else if (hasLegacyState(entity, 5, 3)) {
                Head.xRot = -0.7854f
                BodyMain.xRot = 1.3963f
                Butt.xRot = -0.8727f
                ArmLeft01.xRot = -2.35f
                ArmLeft01.yRot = 0.2618f
                ArmLeft01.zRot = 0.0f
                ArmRight01.xRot = 0.6981f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = 0.6981f
                legAddLeft = 0.2618f
                legAddRight = -0.5236f
                LegLeft01.yRot = 0.0f
                LegLeft01.zRot = 0.0873f
                LegLeft02.xRot = 0.2618f
                LegRight01.yRot = 0.0f
                LegRight01.zRot = -0.0873f
                LegRight02.xRot = 1.3963f
                EquipSL00.xRot = 0.0f
                EquipSL00.yRot = 0.0f
                EquipSL00.zRot = -0.1745f
                EquipSL00.x =
                    equipSL00DefaultX + (0.32f * OFFSET_SCALE) + (50 - entity.attackTick) * (0.22f * OFFSET_SCALE)
                EquipSL00.y =
                    equipSL00DefaultY + (2.0f * OFFSET_SCALE) + (50 - entity.attackTick) * (5.0f * OFFSET_SCALE)
                EquipSL00.z = equipSL00DefaultZ + (-0.08f * OFFSET_SCALE)
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
        CirBase.y = cirBaseDefaultY
        EquipSL00.x = equipSL00DefaultX
        EquipSL00.y = equipSL00DefaultY
        EquipSL00.z = equipSL00DefaultZ
    }

    override fun syncExtraGlowParts() {
        if (GlowEquip00 != null) {
        GlowEquip00.copyFrom(Equip00)
        GlowEquip01a.copyFrom(Equip01a)
        GlowEquip02a.copyFrom(Equip02a)
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "cruiser_tatsuta"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelCruiserTatsuta")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelCruiserTatsuta")
        private val SITTING_TRANSLATE_Y = sittingY("ModelCruiserTatsuta")

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
                PartPose.offsetAndRotation(-7.8f, -9.3f, -0.7f, -0.1745f, 0f, 0.2618f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(24, 63).mirror().addBox(0f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 11f, 2.5f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(33, 101).addBox(-3f, 0f, 0f, 6f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.1f, -8f, -3.6f, -0.6981f, 0.1047f, 0.1396f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(34, 101).mirror().addBox(-3f, 0f, 0f, 6f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -8f, -3.7f, -0.6981f, -0.0925f, -0.1396f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(0, 18).addBox(-2.5f, -2f, -3.6f, 5f, 2f, 5f, CubeDeformation(0f)),
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
                PartPose.offset(0f, -7.7f, 0.1f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(106, 31).addBox(0f, -1f, -5.5f, 0f, 11f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.5f, -4f, -7.5f, 0.2618f, 1.8326f, 0.2618f)
            )

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(52, 56).addBox(-8.5f, 0f, 0f, 17f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -6.9f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(46, 104).addBox(-7.5f, 0f, 0f, 15f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.8f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(10, 16).addBox(-8f, 0f, -8f, 16f, 7f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 8.2f, 0.1745f, 0f, 0f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(24, 84).addBox(-2f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.3f, -0.7f, 0.3142f, 0f, -0.5236f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(24, 63).addBox(-5f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 11f, 2.5f)
            )

            val EquipSL00 = ArmLeft02.addOrReplaceChild(
                "EquipSL00",
                CubeListBuilder.create().texOffs(106, 0).addBox(-0.5f, -6f, -0.5f, 1f, 12f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.5f, 10f, -2f, -1.5708f, -0.0873f, 0.5236f)
            )

            val EquipSL04 = EquipSL00.addOrReplaceChild(
                "EquipSL04",
                CubeListBuilder.create().texOffs(106, 0).addBox(-0.5f, 0f, -0.5f, 1f, 12f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, -17.9f, 0f)
            )

            val EquipSL05 = EquipSL04.addOrReplaceChild(
                "EquipSL05",
                CubeListBuilder.create().texOffs(106, 0).addBox(-0.5f, 0f, -0.5f, 1f, 12f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.9f, 0f)
            )

            val EquipSL01 = EquipSL00.addOrReplaceChild(
                "EquipSL01",
                CubeListBuilder.create().texOffs(106, 0).addBox(-0.5f, 0f, -0.5f, 1f, 12f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 5.9f, 0f)
            )

            val EquipSL02 = EquipSL01.addOrReplaceChild(
                "EquipSL02",
                CubeListBuilder.create().texOffs(110, 0).addBox(-0.5f, 0f, -0.5f, 1f, 12f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 11.9f, 0f)
            )

            val EquipSL03c = EquipSL02.addOrReplaceChild(
                "EquipSL03c",
                CubeListBuilder.create().texOffs(114, 0).addBox(-0.5f, -7f, -2f, 1f, 8f, 2f, CubeDeformation(0f)),
                PartPose.offset(-0.1f, 13.9f, 3.1f)
            )

            val EquipSL03a = EquipSL02.addOrReplaceChild(
                "EquipSL03a",
                CubeListBuilder.create().texOffs(120, 0).addBox(-0.5f, 0f, -0.5f, 1f, 14f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 11.9f, -0.4f)
            )

            val EquipSL03b = EquipSL02.addOrReplaceChild(
                "EquipSL03b",
                CubeListBuilder.create().texOffs(102, 0).addBox(-0.5f, -11f, -1f, 1f, 11f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.1f, 25.7f, 2.1f, -0.0873f, 0f, 0f)
            )

            val Equip00 = BodyMain.addOrReplaceChild(
                "Equip00",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, -1.5f, -2f, 3f, 3f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 5f, 0.0873f, 0f, 0f)
            )

            val Equip01a = Equip00.addOrReplaceChild(
                "Equip01a",
                CubeListBuilder.create().texOffs(26, 0).addBox(-3.5f, 0f, 0f, 7f, 7f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, -12f, 0f)
            )

            val Equip02a = Equip01a.addOrReplaceChild(
                "Equip02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, 0f, 9f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, -0.4f, 10f)
            )

            val Equip01b = Equip01a.addOrReplaceChild(
                "Equip01b",
                CubeListBuilder.create().texOffs(50, 0).addBox(-5.5f, 0f, 0f, 11f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 7f, 0f)
            )

            val Equip01c = Equip01a.addOrReplaceChild(
                "Equip01c",
                CubeListBuilder.create().texOffs(26, 0).addBox(-3.5f, 0f, 0f, 7f, 7f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 5f)
            )

            val Equip01d = Equip01c.addOrReplaceChild(
                "Equip01d",
                CubeListBuilder.create().texOffs(50, 0).addBox(-5.5f, 0f, 0f, 11f, 11f, 5f, CubeDeformation(0f)),
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
                CubeListBuilder.create().texOffs(0, 2).mirror().addBox(-1f, -3f, -4f, 1f, 7f, 9f, CubeDeformation(0f)),
                PartPose.offset(-1.9f, 0f, 0f)
            )

            val EquipCR03c = EquipCR02.addOrReplaceChild(
                "EquipCR03c",
                CubeListBuilder.create().texOffs(0, 27).addBox(-1f, -7f, -1f, 2f, 14f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.9f, -0.5f, 2.7f, 0f, -0.3491f, 0f)
            )

            val EquipCR03b = EquipCR02.addOrReplaceChild(
                "EquipCR03b",
                CubeListBuilder.create().texOffs(0, 27).addBox(-1f, -7f, -1f, 2f, 14f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.9f, -0.5f, 0.5f, 0f, -0.3491f, 0f)
            )

            val EquipCR03a = EquipCR02.addOrReplaceChild(
                "EquipCR03a",
                CubeListBuilder.create().texOffs(0, 27).addBox(-1f, -7f, -1f, 2f, 14f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.9f, -0.5f, -1.7f, 0f, -0.3491f, 0f)
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
                CubeListBuilder.create().texOffs(0, 2).addBox(0f, -3f, -4f, 1f, 7f, 9f, CubeDeformation(0f)),
                PartPose.offset(1.9f, 0f, 0f)
            )

            val EquipCL03b = EquipCL02.addOrReplaceChild(
                "EquipCL03b",
                CubeListBuilder.create().texOffs(0, 27).addBox(-1f, -7f, -1f, 2f, 14f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.9f, -0.5f, 0.5f, 0f, 0.3491f, 0f)
            )

            val EquipCL03a = EquipCL02.addOrReplaceChild(
                "EquipCL03a",
                CubeListBuilder.create().texOffs(0, 27).addBox(-1f, -7f, -1f, 2f, 14f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.9f, -0.5f, -1.7f, 0f, 0.3491f, 0f)
            )

            val EquipCL03c = EquipCL02.addOrReplaceChild(
                "EquipCL03c",
                CubeListBuilder.create().texOffs(0, 27).addBox(-1f, -7f, -1f, 2f, 14f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.9f, -0.5f, 2.7f, 0f, 0.3491f, 0f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 47).addBox(-7.5f, 0f, -5.7f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.3f, 0.3491f, 0f, 0f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 84).mirror().addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.8f, 5.5f, -2.6f, -0.0873f, 0f, -0.0873f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 63).mirror().addBox(0f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3f, 14f, -3f)
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

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(112, 34).addBox(-4f, 0f, 0f, 8f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -9.6f, -3.8f, -0.576f, 0f, 0f)
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
                CubeListBuilder.create().texOffs(104, 24).addBox(-4f, 0f, 0f, 8f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 4f)
            )

            val Equip02c = Equip02b.addOrReplaceChild(
                "Equip02c",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, 0f, 7f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 3f, 4f)
            )

            val Equip02d = Equip02c.addOrReplaceChild(
                "Equip02d",
                CubeListBuilder.create().texOffs(0, 49).addBox(-1f, 0f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, 0f)
            )

            val GlowBodyMain2 = partdefinition.addOrReplaceChild(
                "GlowBodyMain2",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val GlowNeck2 = GlowBodyMain2.addOrReplaceChild(
                "GlowNeck2",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -10.3f, 0.5f, 0.1047f, 0f, 0f)
            )

            val GlowHead2 = GlowNeck2.addOrReplaceChild(
                "GlowHead2",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -1f, -0.7f)
            )

            val CirBase = GlowHead2.addOrReplaceChild(
                "CirBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -21f, 4f, -0.2618f, 0f, 0f)
            )

            val Cir00 = CirBase.addOrReplaceChild(
                "Cir00",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val Cir01 = Cir00.addOrReplaceChild(
                "Cir01",
                CubeListBuilder.create().texOffs(20, 12).addBox(-6f, 0f, -0.5f, 12f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5.5f)
            )

            val Cir02 = Cir00.addOrReplaceChild(
                "Cir02",
                CubeListBuilder.create().texOffs(20, 12).addBox(-6f, 0f, -0.5f, 12f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 5.5f)
            )

            val Cir03 = Cir00.addOrReplaceChild(
                "Cir03",
                CubeListBuilder.create().texOffs(20, 12).addBox(-6f, 0f, -0.5f, 12f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.5f, 0f, 0f, 0f, 1.5708f, 0f)
            )

            val Cir04 = Cir00.addOrReplaceChild(
                "Cir04",
                CubeListBuilder.create().texOffs(20, 12).addBox(-6f, 0f, -0.5f, 12f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5.5f, 0f, 0f, 0f, -1.5708f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
