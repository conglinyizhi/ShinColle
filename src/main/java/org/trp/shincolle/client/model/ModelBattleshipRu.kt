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
import org.trp.shincolle.entity.EntityBattleshipRu
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelBattleshipRu<T : EntityShipBase?>(root: ModelPart) : ShipModelHumanoidBase<T?>(), IGlowableModel {
    private var isDeadPose = false
    private var isSittingPose = false
    private var poseTranslateY = 0f

    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val Butt: ModelPart
    private val ArmRight01: ModelPart
    private val ArmLeft01: ModelPart
    private val EquipBase: ModelPart?
    private val Head: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val Ahoke: ModelPart
    private val HairL01: ModelPart
    private val HairR01: ModelPart
    private val HairL02: ModelPart
    private val HairR02: ModelPart
    private val Hair01: ModelPart
    private val Hair02: ModelPart
    private val LegLeft01: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft02: ModelPart?
    private val Shoe01: ModelPart
    private val LegRight02: ModelPart?
    private val Shoe02: ModelPart
    private val ArmRight02: ModelPart
    private val EquipRBase: ModelPart?
    private val EquipR01: ModelPart
    private val EquipRC01a: ModelPart
    private val EquipRC02a: ModelPart
    private val EquipRC03a: ModelPart
    private val EquipRC04a: ModelPart
    private val EquipR02a: ModelPart
    private val EquipR03a: ModelPart
    private val EquipR04a: ModelPart
    private val EquipR05a: ModelPart
    private val EquipR06a: ModelPart
    private val EquipR07: ModelPart
    private val EquipR02b: ModelPart
    private val EquipR03b: ModelPart
    private val EquipR04b: ModelPart
    private val EquipR05b: ModelPart
    private val EquipR06b: ModelPart
    private val EquipR08: ModelPart
    private val EquipR09: ModelPart
    private val EquipR10: ModelPart
    private val EquipRC01b: ModelPart
    private val EquipRC01c: ModelPart
    private val EquipRC02b: ModelPart
    private val EquipRC03b: ModelPart
    private val EquipRC03c: ModelPart
    private val ArmLeft02: ModelPart
    private val EquipLBase: ModelPart?
    private val EquipL01: ModelPart
    private val EquipLC01a: ModelPart
    private val EquipLC02a: ModelPart
    private val EquipLC03a: ModelPart
    private val EquipLC04a: ModelPart
    private val EquipL02a: ModelPart
    private val EquipL03a: ModelPart
    private val EquipL04a: ModelPart
    private val EquipL05a: ModelPart
    private val EquipL06a: ModelPart
    private val EquipL07: ModelPart
    private val EquipL02b: ModelPart
    private val EquipL03b: ModelPart
    private val EquipL04b: ModelPart
    private val EquipL05b: ModelPart
    private val EquipL06b: ModelPart
    private val EquipL08: ModelPart
    private val EquipL09: ModelPart
    private val EquipL10: ModelPart
    private val EquipLC01b: ModelPart
    private val EquipLC01c: ModelPart
    private val EquipLC02b: ModelPart
    private val EquipLC03b: ModelPart
    private val EquipLC03c: ModelPart
    private val Equip01a: ModelPart
    private val Equip01b: ModelPart
    private val Equip02: ModelPart
    private val Equip03a: ModelPart
    private val EquipCB01: ModelPart
    private val Equip03b: ModelPart
    private val EquipCB03: ModelPart
    private val EquipCB02a: ModelPart
    private val EquipCB02b: ModelPart
    private val EquipCB04a: ModelPart
    private val EquipCB04b: ModelPart
    private val GloveR: ModelPart?
    private val GloveL: ModelPart?
    private val GlowBodyMain: ModelPart?
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val Skirt01: ModelPart
    private val legLeft02DefaultY: Float
    private val legLeft02DefaultZ: Float
    private val legRight02DefaultY: Float
    private val legRight02DefaultZ: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.Hair = this.Head.getChild("Hair")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.EquipBase = this.BodyMain.getChild("EquipBase")
        this.Equip02 = this.EquipBase.getChild("Equip02")
        this.Equip03b = this.Equip02.getChild("Equip03b")
        this.EquipCB03 = this.Equip02.getChild("EquipCB03")
        this.EquipCB04b = this.EquipCB03.getChild("EquipCB04b")
        this.EquipCB04a = this.EquipCB03.getChild("EquipCB04a")
        this.EquipCB01 = this.Equip02.getChild("EquipCB01")
        this.EquipCB02a = this.EquipCB01.getChild("EquipCB02a")
        this.EquipCB02b = this.EquipCB01.getChild("EquipCB02b")
        this.Equip03a = this.Equip02.getChild("Equip03a")
        this.Equip01b = this.EquipBase.getChild("Equip01b")
        this.Equip01a = this.EquipBase.getChild("Equip01a")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.EquipRBase = this.ArmRight02.getChild("EquipRBase")
        this.EquipRC02a = this.EquipRBase.getChild("EquipRC02a")
        this.EquipRC02b = this.EquipRC02a.getChild("EquipRC02b")
        this.EquipR04a = this.EquipRBase.getChild("EquipR04a")
        this.EquipR03a = this.EquipRBase.getChild("EquipR03a")
        this.EquipR02a = this.EquipRBase.getChild("EquipR02a")
        this.EquipR06a = this.EquipRBase.getChild("EquipR06a")
        this.EquipR04b = this.EquipRBase.getChild("EquipR04b")
        this.EquipR02b = this.EquipRBase.getChild("EquipR02b")
        this.EquipR01 = this.EquipRBase.getChild("EquipR01")
        this.EquipR09 = this.EquipRBase.getChild("EquipR09")
        this.EquipR05a = this.EquipRBase.getChild("EquipR05a")
        this.EquipRC01a = this.EquipRBase.getChild("EquipRC01a")
        this.EquipRC01b = this.EquipRC01a.getChild("EquipRC01b")
        this.EquipRC01c = this.EquipRC01a.getChild("EquipRC01c")
        this.EquipR05b = this.EquipRBase.getChild("EquipR05b")
        this.EquipRC03a = this.EquipRBase.getChild("EquipRC03a")
        this.EquipRC03b = this.EquipRC03a.getChild("EquipRC03b")
        this.EquipRC03c = this.EquipRC03a.getChild("EquipRC03c")
        this.EquipR06b = this.EquipRBase.getChild("EquipR06b")
        this.EquipR10 = this.EquipRBase.getChild("EquipR10")
        this.EquipR08 = this.EquipRBase.getChild("EquipR08")
        this.EquipR07 = this.EquipRBase.getChild("EquipR07")
        this.EquipR03b = this.EquipRBase.getChild("EquipR03b")
        this.EquipRC04a = this.EquipRBase.getChild("EquipRC04a")
        this.GloveR = this.ArmRight02.getChild("GloveR")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.EquipLBase = this.ArmLeft02.getChild("EquipLBase")
        this.EquipL04a = this.EquipLBase.getChild("EquipL04a")
        this.EquipLC01a = this.EquipLBase.getChild("EquipLC01a")
        this.EquipLC01b = this.EquipLC01a.getChild("EquipLC01b")
        this.EquipLC01c = this.EquipLC01a.getChild("EquipLC01c")
        this.EquipL06b = this.EquipLBase.getChild("EquipL06b")
        this.EquipLC02a = this.EquipLBase.getChild("EquipLC02a")
        this.EquipLC02b = this.EquipLC02a.getChild("EquipLC02b")
        this.EquipL08 = this.EquipLBase.getChild("EquipL08")
        this.EquipL04b = this.EquipLBase.getChild("EquipL04b")
        this.EquipL05b = this.EquipLBase.getChild("EquipL05b")
        this.EquipLC03a = this.EquipLBase.getChild("EquipLC03a")
        this.EquipLC03b = this.EquipLC03a.getChild("EquipLC03b")
        this.EquipLC03c = this.EquipLC03a.getChild("EquipLC03c")
        this.EquipL06a = this.EquipLBase.getChild("EquipL06a")
        this.EquipL10 = this.EquipLBase.getChild("EquipL10")
        this.EquipL02a = this.EquipLBase.getChild("EquipL02a")
        this.EquipL07 = this.EquipLBase.getChild("EquipL07")
        this.EquipL09 = this.EquipLBase.getChild("EquipL09")
        this.EquipL01 = this.EquipLBase.getChild("EquipL01")
        this.EquipLC04a = this.EquipLBase.getChild("EquipLC04a")
        this.EquipL05a = this.EquipLBase.getChild("EquipL05a")
        this.EquipL03a = this.EquipLBase.getChild("EquipL03a")
        this.EquipL02b = this.EquipLBase.getChild("EquipL02b")
        this.EquipL03b = this.EquipLBase.getChild("EquipL03b")
        this.GloveL = this.ArmLeft02.getChild("GloveL")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.Shoe02 = this.LegRight02.getChild("Shoe02")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.Shoe01 = this.LegLeft02.getChild("Shoe01")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        initFaceParts(this.GlowHead)
        this.legLeft02DefaultY = this.LegLeft02.y
        this.legLeft02DefaultZ = this.LegLeft02.z
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
        val ctx = computePoseContext(entity, limbSwing, limbSwingAmount, ageInTicks, 0.0f)
        resetPoseState()
        resetOffsets()

        applyFaceAndMouth(entity)
        setFlushVisible(
            entity != null && (entity.emotionPrimary == EntityShipBase.EMOTION_SHY
                    || entity.emotionPrimary == EntityShipBase.EMOTION_HAPPY)
        )
        applyEquipVisibility(entity)

        if (isDeadPose(entity)) {
            applyDeadPose()
            syncGlowParts()
            return
        }

        applyHeadRotation(Head, entity, ageInTicks, netHeadYaw, headPitch)

        applyBasePose(ctx, limbSwing, limbSwingAmount, ageInTicks, headPitch)
        applySpecialPoseAdjustments(entity, ctx, limbSwing, limbSwingAmount, ageInTicks)
        applyHairAnimation(ctx, limbSwing, ageInTicks, limbSwingAmount)

        syncGlowParts()
    }

    private fun resetPoseState() {
        this.isDeadPose = false
        this.isSittingPose = false
        this.poseTranslateY = 0.0f
    }

    private fun resetOffsets() {
        if (LegLeft02 != null) {
            LegLeft02.y = legLeft02DefaultY
            LegLeft02.z = legLeft02DefaultZ
        }
        if (LegRight02 != null) {
            LegRight02.y = legRight02DefaultY
            LegRight02.z = legRight02DefaultZ
        }
    }

    private fun isDeadPose(entity: T?): Boolean {
        return entity != null && entity.isInDeadPose
    }

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) return
        val showWeapon = entity.getEquipFlag(EntityBattleshipRu.EQUIP_WEAPON)
        val showBase = entity.getEquipFlag(EntityBattleshipRu.EQUIP_BASE)
        val showGlove = entity.getEquipFlag(EntityBattleshipRu.EQUIP_GLOVES)
        if (EquipLBase != null) EquipLBase.visible = showWeapon
        if (EquipRBase != null) EquipRBase.visible = showWeapon
        if (EquipBase != null) EquipBase.visible = showBase
        if (GloveL != null) GloveL.visible = showGlove
        if (GloveR != null) GloveR.visible = showGlove
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        Head.xRot = 0.0f
        Head.yRot = 0.0f
        Head.zRot = 0.0f

        BoobL.xRot = -0.7f
        BoobR.xRot = -0.7f
        Ahoke.yRot = 0.5236f

        BodyMain.xRot = 1.4f
        BodyMain.yRot = 0.0f
        Neck.xRot = 0.1f

        ArmLeft01.xRot = -3.0f
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = 0.2618f

        ArmRight01.xRot = -3.0f
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = -0.2618f

        LegLeft01.xRot = -0.1f
        LegLeft01.yRot = 0.0f
        LegLeft01.zRot = 0.0873f

        LegRight01.xRot = -0.1f
        LegRight01.yRot = 0.0f
        LegRight01.zRot = -0.0873f

        HairL01.xRot = -0.2f
        HairR01.xRot = -0.2f
        HairL02.xRot = -0.1f
        HairR02.xRot = -0.1f
        Hair01.xRot = -0.4f
        Hair02.xRot = -0.3f
    }

    private fun applyBasePose(
        ctx: PoseContext,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headPitch: Float
    ) {
        val angleX = ctx.angleX

        BoobL.xRot = angleX * 0.06f - 0.7f
        BoobR.xRot = angleX * 0.06f - 0.7f
        Ahoke.yRot = angleX * 0.25f + 0.5236f

        BodyMain.xRot = -0.1047f
        BodyMain.yRot = 0.0f
        Neck.xRot = 0.1047f

        ArmLeft01.xRot = ctx.angleAdd2 * 0.4f + 0.1f
        ArmLeft01.yRot = 0.0f

        ArmRight01.xRot = ctx.angleAdd1 * 0.4f
        ArmRight01.yRot = 0.0f

        ArmLeft02.xRot = 0.0f
        ArmLeft02.zRot = 0.0f


        ArmRight02.xRot = 0.0f
        ArmRight02.zRot = 0.0f

        LegLeft01.yRot = 0.0f
        LegLeft01.zRot = 0.0873f
        LegLeft02!!.xRot = 0.0f

        LegRight01.yRot = 0.0f
        LegRight01.zRot = -0.0873f
        LegRight02!!.xRot = 0.0f

        this.EquipLBase!!.xRot = 0.0f
        this.EquipLBase.yRot = 0.0f
        this.EquipLBase.zRot = 0.0f
        this.EquipRBase!!.xRot = 0.0f
        this.EquipRBase.yRot = 0.0f
        this.EquipRBase.zRot = 0.0f
        this.EquipCB02a.xRot = this.Head.xRot * 0.9f + 0.8f
        this.EquipCB02b.xRot = this.Head.xRot * 0.8f + 0.9f
        this.EquipCB04a.xRot = this.Head.xRot * 1.1f + 0.7f
        this.EquipCB04b.xRot = this.Head.xRot * 0.9f + 0.8f
        this.EquipLC01b.xRot = this.Head.xRot * 0.9f - 0.05f
        this.EquipLC01c.xRot = this.Head.xRot * 0.8f - 0.08f
        this.EquipLC02b.xRot = this.Head.xRot * 1.1f + 0.1f
        this.EquipLC03b.xRot = this.Head.xRot * 0.9f + 0.05f
        this.EquipLC03c.xRot = this.Head.xRot * 0.8f + 0.08f
        this.EquipRC01b.xRot = this.Head.xRot * 0.9f - 0.05f
        this.EquipRC01c.xRot = this.Head.xRot * 0.8f - 0.08f
        this.EquipRC02b.xRot = this.Head.xRot * 1.1f + 0.1f
        this.EquipRC03b.xRot = this.Head.xRot * 0.9f + 0.05f
        this.EquipRC03c.xRot = this.Head.xRot * 0.8f + 0.08f
    }

    private fun applySpecialPoseAdjustments(
        entity: T?,
        ctx: PoseContext,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float
    ) {
        val angleX = ctx.angleX

        var legAddLeft = ctx.angleAdd1 * 0.5f - 0.18f
        var legAddRight = ctx.angleAdd2 * 0.5f - 0.18f
        val showWeapon = entity != null && entity.getEquipFlag(EntityBattleshipRu.EQUIP_WEAPON)
        val spStand =
            entity != null && showWeapon && hasLegacyState(entity, 1, 4) && ((entity.tickCount and 0x1FF) > 400)

        if (entity != null && entity.shipDepth > 0.0) {
            this.poseTranslateY += ctx.angleX * 0.05f + 0.025f
        }

        if (showWeapon) {
            this.ArmLeft01.zRot = angleX * 0.03f - 0.3f
            this.ArmRight01.zRot = -angleX * 0.03f + 0.3f
        } else {
            this.ArmLeft01.zRot = angleX * 0.03f - 0.15f
            this.ArmRight01.zRot = -angleX * 0.03f + 0.15f
        }

        if (spStand) {
            this.poseTranslateY += 0.12f
            BodyMain.xRot = 1.0472f
            Head.xRot -= 0.18203785f
            ArmLeft01.xRot = -1.0472f
            ArmLeft01.yRot = 0.0f
            ArmLeft01.zRot = -0.34906584f
            ArmRight01.xRot = -1.0472f
            ArmRight01.yRot = 0.0f
            ArmRight01.zRot = 0.34906584f
            legAddLeft = -1.3962634f
            legAddRight = -1.3962634f
            LegLeft01.yRot = 0.0f
            LegLeft01.zRot = 0.08726646f
            LegRight01.yRot = 0.0f
            LegRight01.zRot = -0.08726646f
        }

        val isCrouching = entity != null && entity.isCrouching()
        val isSitting =
            ctx.isSitting || (entity != null && entity.isPassenger() && (entity.getVehicle() !is EntityMountBase))
        val isSprinting = entity != null && entity.isSprinting || limbSwingAmount > 0.9f

        if (isSprinting) {
            if (spStand) this.poseTranslateY -= 0.12f
            this.poseTranslateY += 0.05f
            BodyMain.xRot = -0.1f

            if (showWeapon) {
                ArmLeft01.xRot = ctx.angleAdd2 * 0.05f + 0.5f
                ArmRight01.xRot = ctx.angleAdd1 * 0.05f + 0.5f
            } else {
                ArmLeft01.xRot = ctx.angleAdd2 * 0.9f + 0.5f
                ArmRight01.xRot = ctx.angleAdd1 * 0.9f + 0.5f
            }

            ArmLeft01.yRot = 0.0f
            ArmLeft02.xRot = -1.0f
            ArmLeft02.zRot = 0.0f
            ArmRight01.yRot = 0.0f
            ArmRight02.xRot = -1.0f
            ArmRight02.zRot = 0.0f

            legAddLeft = ctx.angleAdd1 * 0.7f - 0.28f
            legAddRight = ctx.angleAdd2 * 0.7f - 0.21f
            LegLeft01.yRot = 0.0f
            LegLeft01.zRot = 0.0873f
            LegRight01.yRot = 0.0f
            LegRight01.zRot = -0.0873f

            EquipLBase!!.xRot = 0.5f
            EquipLBase.yRot = 0.0f
            EquipLBase.zRot = 0.0f
            EquipRBase!!.xRot = 0.5f
            EquipRBase.yRot = 0.0f
            EquipRBase.zRot = 0.0f
        }

        if (isCrouching) {
            if (spStand) this.poseTranslateY -= 0.12f
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            Head.xRot -= 0.6283f
            BodyMain.xRot = 0.8727f

            if (showWeapon) {
                ArmLeft01.xRot = ctx.angleAdd2 * 0.05f + 0.5f
                ArmLeft01.zRot = -0.25f
                ArmLeft02.xRot = -1.0f
                ArmRight01.xRot = ctx.angleAdd1 * 0.05f + 0.5f
                ArmRight01.zRot = 0.25f
                ArmRight02.xRot = -1.0f
            } else {
                ArmLeft01.xRot = -0.35f
                ArmLeft01.zRot = 0.2618f
                ArmLeft02.xRot = 0.0f
                ArmRight01.xRot = -0.35f
                ArmRight01.zRot = -0.2618f
                ArmRight02.xRot = 0.0f
            }

            ArmLeft01.yRot = 0.0f
            ArmLeft02.zRot = 0.0f
            ArmRight01.yRot = 0.0f
            ArmRight02.zRot = 0.0f

            legAddLeft -= 1.1f
            legAddRight -= 1.1f
            Hair01.xRot += 0.37f
            Hair02.xRot += 0.23f
        }

        if (isSitting) {
            this.isSittingPose = true
            if (spStand) this.poseTranslateY -= 0.12f

            if (entity != null && hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY += 0.25f * 3
                BodyMain.xRot = -0.10471976f
                BodyMain.yRot = -0.34906584f
                Head.yRot -= 0.5235988f
                ArmLeft01.xRot = 0.87266463f
                ArmLeft01.yRot = 0.0f
                ArmLeft01.zRot = -0.34906584f
                ArmLeft02.xRot = -0.7853982f
                ArmLeft02.yRot = 0.0f
                ArmLeft02.zRot = 0.0f
                ArmRight01.xRot = -0.43633232f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = 0.34906584f
                ArmRight02.xRot = -0.87266463f
                ArmRight02.yRot = 0.0f
                ArmRight02.zRot = 0.0f
                legAddLeft = -1.4835298f
                legAddRight = -0.43633232f
                LegLeft01.yRot = 0.0f
                LegLeft01.zRot = 0.08726646f
                LegLeft02!!.xRot = 1.3962634f
                LegLeft02.yRot = 0.0f
                LegLeft02.zRot = 0.0f
                LegRight01.yRot = 0.0f
                LegRight01.zRot = -0.08726646f
                LegRight02!!.xRot = 1.4835298f
                LegRight02.yRot = 0.0f
                LegRight02.zRot = 0.0f
            } else if (entity != null && hasLegacyState(entity, 7, 4) && showWeapon) {
                this.poseTranslateY += 0.52f * 3
                BodyMain.xRot = 0.7853982f
                Butt.xRot = 0.2617994f
                Head.xRot = 0.5235988f
                Hair01.xRot = -0.34906584f
                Hair02.xRot = -0.12217305f
                ArmLeft01.xRot = 2.6179938f
                ArmLeft01.yRot = 0.0f
                ArmLeft01.zRot = 0.0f
                ArmRight01.xRot = 2.6179938f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = 0.0f
                legAddLeft = 0.2617994f
                legAddRight = 0.2617994f
                LegLeft01.yRot = 0.0f
                LegLeft01.zRot = 0.08726646f
                LegLeft02!!.xRot = 0.2617994f
                LegLeft02.yRot = 0.0f
                LegLeft02.zRot = 0.0f
                LegRight01.yRot = 0.0f
                LegRight01.zRot = -0.08726646f
                LegRight02!!.xRot = 0.2617994f
                LegRight02.yRot = 0.0f
                LegRight02.zRot = 0.0f
                EquipLBase!!.xRot = 1.2217305f
                EquipLBase.yRot = 0.0f
                EquipLBase.zRot = 0.0f
                EquipRBase!!.xRot = 1.2217305f
                EquipRBase.yRot = 0.0f
                EquipRBase.zRot = 0.0f
            } else if (showWeapon) {
                this.poseTranslateY += 0.2f * 3
                BodyMain.xRot = 0.18203785f
                Butt.xRot = 0.2617994f
                Head.xRot -= 0.20943952f
                ArmLeft01.xRot = 0.13962634f
                ArmLeft01.yRot = 0.0f
                ArmLeft01.zRot = -0.34906584f
                ArmRight01.xRot = -1.1838568f
                ArmRight01.yRot = 0.8f
                ArmRight01.zRot = 0.0f
                ArmRight02.xRot = -1.3089969f
                ArmRight02.yRot = 0.0f
                ArmRight02.zRot = 0.0f
                legAddLeft = -1.61f
                legAddRight = -1.57f
                LegLeft01.yRot = 0.0f
                LegLeft01.zRot = 0.08726646f
                LegLeft02!!.xRot = 1.5f
                LegLeft02.yRot = 0.0f
                LegLeft02.zRot = 0.0f
                LegRight01.yRot = 0.0f
                LegRight01.zRot = -0.08726646f
                LegRight02!!.xRot = 0.6f
                LegRight02.yRot = 0.0f
                LegRight02.zRot = 0.0f
                EquipLBase!!.xRot = 0.0f
                EquipLBase.yRot = -1.5707964f
                EquipLBase.zRot = 0.31415927f
                EquipRBase!!.xRot = 0.7285004f
                EquipRBase.yRot = 0.0f
                EquipRBase.zRot = 0.0f
            } else {
                this.poseTranslateY += SITTING_TRANSLATE_Y
                BodyMain.xRot = 0.27314404f
                Butt.xRot = 0.2617994f
                Head.xRot -= 0.41887903f
                ArmLeft01.xRot = 0.091106184f
                ArmLeft01.yRot = 0.0f
                ArmLeft01.zRot = -0.63739425f
                ArmLeft02.xRot = 0.0f
                ArmLeft02.yRot = 0.0f
                ArmLeft02.zRot = 1.3658947f
                ArmRight01.xRot = -0.85f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = 0.0f
                ArmRight02.xRot = 0.0f
                ArmRight02.yRot = 0.0f
                ArmRight02.zRot = -0.5009095f
                legAddLeft = -1.2747885f
                legAddRight = -2.1399481f
                LegLeft01.yRot = 0.0f
                LegLeft01.zRot = 0.08726646f
                LegLeft02!!.xRot = 2.321986f
                LegLeft02.yRot = 0.0f
                LegLeft02.zRot = 0.0f
                LegLeft02.z = legLeft02DefaultZ + (0.375f * OFFSET_SCALE)
                LegRight01.yRot = 0.0f
                LegRight01.zRot = -0.08726646f
                LegRight02!!.xRot = 1.5707964f
                LegRight02.yRot = 0.0f
                LegRight02.zRot = 0.0f
            }
        }

        if (entity != null && entity.attackTick > 0) {
            if (spStand) this.poseTranslateY -= 0.12f
            BodyMain.xRot = -0.1047f
            BodyMain.yRot = 0.0f
            BodyMain.zRot = 0.0f
            Butt.xRot = 0.35f
            if (showWeapon) {
                ArmLeft02.xRot = -0.87266463f
                ArmRight02.xRot = -0.87266463f
            } else {
                ArmLeft02.xRot = 0.0f
                ArmRight02.xRot = 0.0f
            }
            ArmLeft01.xRot = -0.5235988f
            ArmLeft01.yRot = -0.5235988f
            ArmLeft01.zRot = -0.2617994f
            ArmLeft02.yRot = 0.0f
            ArmLeft02.zRot = 0.0f
            ArmRight01.xRot = -0.5235988f
            ArmRight01.yRot = 0.5235988f
            ArmRight01.zRot = 0.2617994f
            ArmRight02.yRot = 0.0f
            ArmRight02.zRot = 0.0f
            legAddLeft = ctx.angleAdd1 * 0.5f - 0.28f
            legAddRight = ctx.angleAdd2 * 0.5f - 0.21f
            EquipLBase!!.xRot = 0.0f
            EquipLBase.yRot = -0.2617994f
            EquipLBase.zRot = 0.34906584f
            EquipRBase!!.xRot = 0.0f
            EquipRBase.yRot = 0.2617994f
            EquipRBase.zRot = -0.34906584f
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

        LegLeft01.xRot = legAddLeft
        LegRight01.xRot = legAddRight
    }

    private fun applyHairAnimation(ctx: PoseContext, limbSwing: Float, ageInTicks: Float, limbSwingAmount: Float) {
        val angleX = ctx.angleX
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f)

        HairL01.xRot = angleX * 0.02f - 0.09f
        HairL02.xRot = angleX1 * 0.03f - 0.09f
        HairR01.xRot = angleX * 0.02f - 0.09f
        HairR02.xRot = angleX1 * 0.03f - 0.09f
        Hair01.xRot = angleX * 0.03f - 0.1f
        Hair02.xRot = angleX1 * 0.04f - 0.1f

        if (limbSwingAmount > 0.9f) {
            HairL01.xRot += 0.2f
            HairL02.xRot += 0.2f
            HairR01.xRot += 0.2f
            HairR02.xRot += 0.2f
            Hair01.xRot += 0.3f
            Hair02.xRot += 0.3f
        } else if (this.isSittingPose) {
            HairL01.xRot -= 0.1f
            HairL02.xRot -= 0.1f
            HairR01.xRot -= 0.1f
            HairR02.xRot -= 0.1f
            Hair01.xRot -= 0.15f
            Hair02.xRot -= 0.15f
        }

        val headX = Head.xRot * -0.5f
        val headZ = Head.zRot * -0.5f

        Hair01.xRot += headX
        Hair02.xRot += headX
        HairL01.xRot += headX
        HairL02.xRot += headX
        HairR01.xRot += headX
        HairR02.xRot += headX

        HairL01.zRot = headZ
        HairL02.zRot = headZ
        HairR01.zRot = headZ
        HairR02.zRot = headZ
        Hair01.zRot = headZ
        Hair02.zRot = headZ
    }

    private fun syncGlowParts() {
        if (this.GlowBodyMain != null) {
            GlowBodyMain.copyFrom(BodyMain)
            GlowNeck.copyFrom(Neck)
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
        if (GlowBodyMain == null) return
        val usePoseTranslate = this.poseTranslateY != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, this.poseTranslateY, 0.0f)
        }

        GlowBodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "battleship_ru"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelBattleshipRu")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelBattleshipRu")
        private val SITTING_TRANSLATE_Y = sittingY("ModelBattleshipRu")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 104).addBox(-6.5f, -11f, -4f, 13f, 17f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(54, 121).addBox(-2.5f, -2f, -3.6f, 5f, 2f, 5f, CubeDeformation(0f)),
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

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(50, 54).addBox(-7.5f, 0f, 0f, 15f, 14f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1f, 0.1396f, 0f, 0f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(0, 63).addBox(-7f, 0f, -5f, 14f, 12f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12.5f, 6.5f, -0.1222f, 0f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 77).addBox(-8f, -8f, -7.4f, 16f, 15f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0.1f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(114, 45).addBox(-1.5f, 0f, 0f, 5f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.7f, -6f, -7.5f, -0.0873f, 0f, 0.1367f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(88, 100).addBox(-1f, 0f, 0f, 2f, 10f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.8f, 6.5f, -6.3f, -0.0873f, -0.07f, 0.0524f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(88, 100).addBox(-1f, 0f, 0f, 2f, 11f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10f, 0f, 0.1222f, 0f, -0.0873f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(88, 101).mirror()
                    .addBox(-1f, 0f, 0f, 2f, 10f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.8f, 6.5f, -6.3f, -0.0873f, 0.07f, -0.0524f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(88, 100).mirror()
                    .addBox(-1f, 0f, 0f, 2f, 11f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 10f, 0f, 0.0873f, 0f, 0.0873f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(33, 101).mirror()
                    .addBox(-3.5f, 0f, 0f, 7f, 5f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.2f, -9f, -3.4f, -0.6981f, 0.0873f, 0.0873f)
            )

            val EquipBase = BodyMain.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -9f, 1f)
            )

            val Equip02 = EquipBase.addOrReplaceChild(
                "Equip02",
                CubeListBuilder.create().texOffs(4, 4).addBox(-6f, 0f, 0f, 12f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, -7f, 3f)
            )

            val Equip03b = Equip02.addOrReplaceChild(
                "Equip03b",
                CubeListBuilder.create().texOffs(66, 0).addBox(-10f, 0f, 0f, 10f, 5f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-10f, -2f, 0.5f, -0.4363f, 0.2618f, -0.7854f)
            )

            val EquipCB03 = Equip02.addOrReplaceChild(
                "EquipCB03",
                CubeListBuilder.create().texOffs(66, 0).mirror().addBox(-10f, 0f, 0f, 10f, 5f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-10f, -4f, 1f, -0.3491f, 0.3491f, -0.4363f)
            )

            val EquipCB04b = EquipCB03.addOrReplaceChild(
                "EquipCB04b",
                CubeListBuilder.create().texOffs(11, 8).addBox(-1f, -9f, -1f, 2f, 9f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3f, 1f, 5f, 0.6109f, 0f, 0f)
            )

            val EquipCB04a = EquipCB03.addOrReplaceChild(
                "EquipCB04a",
                CubeListBuilder.create().texOffs(9, 4).addBox(-1f, -9f, -1f, 2f, 9f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, 1f, 5f, 0.7854f, 0f, 0f)
            )

            val EquipCB01 = Equip02.addOrReplaceChild(
                "EquipCB01",
                CubeListBuilder.create().texOffs(66, 0).addBox(0f, 0f, 0f, 10f, 5f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(10f, -4f, 1f, -0.3491f, -0.3491f, 0.4363f)
            )

            val EquipCB02a = EquipCB01.addOrReplaceChild(
                "EquipCB02a",
                CubeListBuilder.create().texOffs(13, 8).mirror().addBox(-1f, -9f, -1f, 2f, 9f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3f, 1f, 5f, 0.5236f, 0f, 0f)
            )

            val EquipCB02b = EquipCB01.addOrReplaceChild(
                "EquipCB02b",
                CubeListBuilder.create().texOffs(13, 8).addBox(-1f, -9f, -1f, 2f, 9f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, 1f, 5f, 0.8727f, 0f, 0f)
            )

            val Equip03a = Equip02.addOrReplaceChild(
                "Equip03a",
                CubeListBuilder.create().texOffs(66, 0).addBox(0f, 0f, 0f, 10f, 5f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(10f, -2f, 0.5f, -0.4363f, -0.2618f, 0.7854f)
            )

            val Equip01b = EquipBase.addOrReplaceChild(
                "Equip01b",
                CubeListBuilder.create().texOffs(66, 0).mirror().addBox(-10f, 0f, 0f, 10f, 5f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, -3f, -5.5f, 0f, 0.1745f, 0.3491f)
            )

            val Equip01a = EquipBase.addOrReplaceChild(
                "Equip01a",
                CubeListBuilder.create().texOffs(66, 0).addBox(0f, 0f, 0f, 10f, 5f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, -3f, -5.5f, 0f, -0.1745f, -0.3491f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(24, 84).mirror()
                    .addBox(-3f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, -9f, -0.7f, 0f, 0.4363f, 0.3491f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(24, 84).mirror().addBox(0f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 11f, 2.5f)
            )

            val EquipRBase = ArmRight02.addOrReplaceChild(
                "EquipRBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(2f, 12f, -3f)
            )

            val EquipRC02a = EquipRBase.addOrReplaceChild(
                "EquipRC02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, 0f, 0f, 8f, 11f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 1.5f, 0f)
            )

            val EquipRC02b = EquipRC02a.addOrReplaceChild(
                "EquipRC02b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 2f, 12f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 10f, 4f)
            )

            val EquipR04a = EquipRBase.addOrReplaceChild(
                "EquipR04a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 1f, 14f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.5f, 1f, 1.2f, 0.0873f, 0f, 0f)
            )

            val EquipR03a = EquipRBase.addOrReplaceChild(
                "EquipR03a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 1f, 17f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7f, -2.4f, -3f, -0.2269f, -0.1396f, 0f)
            )

            val EquipR02a = EquipRBase.addOrReplaceChild(
                "EquipR02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, 0f, 0f, 1f, 21f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5f, -4f, -9f, -0.5236f, -0.1745f, 0f)
            )

            val EquipR06a = EquipRBase.addOrReplaceChild(
                "EquipR06a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 1f, 14f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9.6f, -3.3f, 24.2f, 0.3491f, 0.6981f, 0.2618f)
            )

            val EquipR04b = EquipRBase.addOrReplaceChild(
                "EquipR04b",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1f, 0f, 0f, 1f, 14f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.5f, 1f, 1.2f, 0.0873f, 0f, 0f)
            )

            val EquipR02b = EquipRBase.addOrReplaceChild(
                "EquipR02b",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-0.5f, 0f, 0f, 1f, 21f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5f, -4f, -9f, -0.5236f, 0.1745f, 0f)
            )

            val EquipR01 = EquipRBase.addOrReplaceChild(
                "EquipR01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, 0f, 0f, 8f, 5f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1f, -2f, 0f, 0f, -1.5708f, 0f)
            )

            val EquipR09 = EquipRBase.addOrReplaceChild(
                "EquipR09",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6f, 0f, 0f, 12f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.2f, 20.6f, 0.3142f, 0f, 0f)
            )

            val EquipR05a = EquipRBase.addOrReplaceChild(
                "EquipR05a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 1f, 14f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.5f, 0f, 13f, 0.2618f, -0.2618f, 0f)
            )

            val EquipRC01a = EquipRBase.addOrReplaceChild(
                "EquipRC01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, 0f, 9f, 12f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, -6f, 0.1396f, 0f, 0f)
            )

            val EquipRC01b = EquipRC01a.addOrReplaceChild(
                "EquipRC01b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 2f, 12f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.8f, 11f, 2f, -0.0524f, 0f, 0f)
            )

            val EquipRC01c = EquipRC01a.addOrReplaceChild(
                "EquipRC01c",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 2f, 12f, 2f, CubeDeformation(0f)),
                PartPose.offset(1.8f, 11f, 2f)
            )

            val EquipR05b = EquipRBase.addOrReplaceChild(
                "EquipR05b",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1f, 0f, 0f, 1f, 14f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.5f, 0f, 13f, 0.2618f, 0.2618f, 0f)
            )

            val EquipRC03a = EquipRBase.addOrReplaceChild(
                "EquipRC03a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, 0f, 9f, 11f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1f, 9f, -0.1745f, 0f, 0f)
            )

            val EquipRC03b = EquipRC03a.addOrReplaceChild(
                "EquipRC03b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 2f, 12f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.8f, 10f, 3.5f, 0.1047f, 0f, 0f)
            )

            val EquipRC03c = EquipRC03a.addOrReplaceChild(
                "EquipRC03c",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 2f, 12f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.8f, 10f, 3.5f, 0.1396f, 0f, 0f)
            )

            val EquipR06b = EquipRBase.addOrReplaceChild(
                "EquipR06b",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1f, 0f, 0f, 1f, 14f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(9.6f, -3.3f, 24.2f, 0.3491f, -0.6981f, -0.2618f)
            )

            val EquipR10 = EquipRBase.addOrReplaceChild(
                "EquipR10",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, 0f, 0f, 4f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.7f, 1f, 14f, 0.1745f, -0.1745f, 0f)
            )

            val EquipR08 = EquipRBase.addOrReplaceChild(
                "EquipR08",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, 0f, 9f, 13f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1f, -10f, -0.1396f, 0f, 0f)
            )

            val EquipR07 = EquipRBase.addOrReplaceChild(
                "EquipR07",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, 0f, 0f, 10f, 13f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -4f, 29f, -0.0873f, 0f, 0f)
            )

            val EquipR03b = EquipRBase.addOrReplaceChild(
                "EquipR03b",
                CubeListBuilder.create().texOffs(46, 0).mirror().addBox(-1f, 0f, 0f, 1f, 17f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, -2.4f, -3f, -0.2269f, 0.1396f, 0f)
            )

            val EquipRC04a = EquipRBase.addOrReplaceChild(
                "EquipRC04a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, 0f, 0f, 10f, 13f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, 12.5f, 0.1396f, 0f, 0f)
            )

            val GloveR = ArmRight02.addOrReplaceChild(
                "GloveR",
                CubeListBuilder.create().texOffs(2, 34).addBox(2.5f, 5.5f, -2.5f, 6f, 7f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3f, 0f, -3f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(24, 84).addBox(-2f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9f, -0.7f, 0.2276f, -0.4363f, -0.3491f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(24, 84).addBox(-5f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 11f, 2.5f)
            )

            val EquipLBase = ArmLeft02.addOrReplaceChild(
                "EquipLBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(-3f, 12f, -3f)
            )

            val EquipL04a = EquipLBase.addOrReplaceChild(
                "EquipL04a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 1f, 14f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.5f, 1f, 1.2f, 0.0873f, 0f, 0f)
            )

            val EquipLC01a = EquipLBase.addOrReplaceChild(
                "EquipLC01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, 0f, 9f, 12f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, -6f, 0.1396f, 0f, 0f)
            )

            val EquipLC01b = EquipLC01a.addOrReplaceChild(
                "EquipLC01b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 2f, 12f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.8f, 11f, 2f, -0.0524f, 0f, 0f)
            )

            val EquipLC01c = EquipLC01a.addOrReplaceChild(
                "EquipLC01c",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 2f, 12f, 2f, CubeDeformation(0f)),
                PartPose.offset(1.8f, 11f, 2f)
            )

            val EquipL06b = EquipLBase.addOrReplaceChild(
                "EquipL06b",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1f, 0f, 0f, 1f, 14f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(9.6f, -3.3f, 24.2f, 0.3491f, -0.6981f, -0.2618f)
            )

            val EquipLC02a = EquipLBase.addOrReplaceChild(
                "EquipLC02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, 0f, 0f, 8f, 11f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 1.5f, 0f)
            )

            val EquipLC02b = EquipLC02a.addOrReplaceChild(
                "EquipLC02b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 2f, 12f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 10f, 4f)
            )

            val EquipL08 = EquipLBase.addOrReplaceChild(
                "EquipL08",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, 0f, 9f, 13f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1f, -10f, -0.1396f, 0f, 0f)
            )

            val EquipL04b = EquipLBase.addOrReplaceChild(
                "EquipL04b",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1f, 0f, 0f, 1f, 14f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.5f, 1f, 1.2f, 0.0873f, 0f, 0f)
            )

            val EquipL05b = EquipLBase.addOrReplaceChild(
                "EquipL05b",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1f, 0f, 0f, 1f, 14f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.5f, 0f, 13f, 0.2618f, 0.2618f, 0f)
            )

            val EquipLC03a = EquipLBase.addOrReplaceChild(
                "EquipLC03a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, 0f, 9f, 11f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1f, 9f, -0.1745f, 0f, 0f)
            )

            val EquipLC03b = EquipLC03a.addOrReplaceChild(
                "EquipLC03b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 2f, 12f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.8f, 10f, 3.5f, 0.1047f, 0f, 0f)
            )

            val EquipLC03c = EquipLC03a.addOrReplaceChild(
                "EquipLC03c",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 2f, 12f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.8f, 10f, 3.5f, 0.1396f, 0f, 0f)
            )

            val EquipL06a = EquipLBase.addOrReplaceChild(
                "EquipL06a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 1f, 14f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9.6f, -3.3f, 24.2f, 0.3491f, 0.6981f, 0.2618f)
            )

            val EquipL10 = EquipLBase.addOrReplaceChild(
                "EquipL10",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 4f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.7f, 1f, 14f, 0.1745f, 0.1745f, 0f)
            )

            val EquipL02a = EquipLBase.addOrReplaceChild(
                "EquipL02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, 0f, 0f, 1f, 21f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5f, -4f, -9f, -0.5236f, -0.1745f, 0f)
            )

            val EquipL07 = EquipLBase.addOrReplaceChild(
                "EquipL07",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, 0f, 0f, 10f, 13f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -4f, 29f, -0.0873f, 0f, 0f)
            )

            val EquipL09 = EquipLBase.addOrReplaceChild(
                "EquipL09",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6f, 0f, 0f, 12f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.2f, 20.6f, 0.3142f, 0f, 0f)
            )

            val EquipL01 = EquipLBase.addOrReplaceChild(
                "EquipL01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, 0f, 0f, 8f, 5f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1f, -2f, 0f, 0f, -1.5708f, 0f)
            )

            val EquipLC04a = EquipLBase.addOrReplaceChild(
                "EquipLC04a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, 0f, 0f, 10f, 13f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, 12.5f, 0.1396f, 0f, 0f)
            )

            val EquipL05a = EquipLBase.addOrReplaceChild(
                "EquipL05a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 1f, 14f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.5f, 0f, 13f, 0.2618f, -0.2618f, 0f)
            )

            val EquipL03a = EquipLBase.addOrReplaceChild(
                "EquipL03a",
                CubeListBuilder.create().texOffs(46, 0).addBox(0f, 0f, 0f, 1f, 17f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7f, -2.4f, -3f, -0.2269f, -0.1396f, 0f)
            )

            val EquipL02b = EquipLBase.addOrReplaceChild(
                "EquipL02b",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-0.5f, 0f, 0f, 1f, 21f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5f, -4f, -9f, -0.5236f, 0.1745f, 0f)
            )

            val EquipL03b = EquipLBase.addOrReplaceChild(
                "EquipL03b",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1f, 0f, 0f, 1f, 17f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, -2.4f, -3f, -0.2269f, 0.1396f, 0f)
            )

            val GloveL = ArmLeft02.addOrReplaceChild(
                "GloveL",
                CubeListBuilder.create().texOffs(2, 34).addBox(-2.5f, 5.5f, -2.5f, 6f, 7f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3f, 0f, -3f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 47).addBox(-7.5f, 0f, -5.7f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.3f, 0.3491f, 0f, 0f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 84).mirror().addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.8f, 5.5f, -2.6f, -0.1396f, 0f, -0.0873f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 84).mirror().addBox(0f, 0f, 0f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3f, 14f, -3f)
            )

            val Shoe02 = LegRight02.addOrReplaceChild(
                "Shoe02",
                CubeListBuilder.create().texOffs(0, 33).addBox(-3.5f, 0f, -3.5f, 7f, 6f, 7f, CubeDeformation(0f)),
                PartPose.offset(3f, 9f, 3f)
            )

            val Skirt01 = Butt.addOrReplaceChild(
                "Skirt01",
                CubeListBuilder.create().texOffs(46, 41).addBox(-8.5f, 0f, -6f, 17f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.9f, 0f, -0.1396f, 0f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 84).addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.8f, 5.5f, -2.6f, -0.2793f, 0f, 0.0873f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 84).addBox(-6f, 0f, 0f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(3f, 14f, -3f)
            )

            val Shoe01 = LegLeft02.addOrReplaceChild(
                "Shoe01",
                CubeListBuilder.create().texOffs(0, 33).addBox(-3.5f, 0f, -3.5f, 7f, 6f, 7f, CubeDeformation(0f)),
                PartPose.offset(-3f, 9f, 3f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(33, 101).addBox(-3.5f, 0f, 0f, 7f, 5f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.2f, -9f, -3.4f, -0.6981f, -0.0873f, -0.0873f)
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
