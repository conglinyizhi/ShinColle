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
import org.trp.shincolle.entity.EntityAirfieldHime
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelAirfieldHime<T : EntityShipBase>(root: ModelPart) : ShincolleShipModel<T>() {

    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val Butt: ModelPart
    private val Head: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val HeadHL: ModelPart
    private val HeadHR: ModelPart
    private val Ahoke: ModelPart
    private val HairL01: ModelPart
    private val HairR01: ModelPart
    private val HairL02: ModelPart
    private val HairR02: ModelPart
    private val Hair01: ModelPart
    private val Hair02: ModelPart
    private val Hair03: ModelPart
    private val HeadHL2: ModelPart
    private val HeadHL3: ModelPart
    private val HeadHR2: ModelPart
    private val HeadHR3: ModelPart
    private val ArmLeft02: ModelPart
    private val EquipHand01: ModelPart
    private val ArmRight02: ModelPart
    private val EquipHand02: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft01: ModelPart
    private val LegRight02: ModelPart
    private val ShoesR: ModelPart
    private val LegLeft02: ModelPart
    private val ShoesL: ModelPart
    private val EquipRdL01: ModelPart
    private val EquipRdR01: ModelPart
    private val EquipRdL02: ModelPart
    private val EquipRdL03: ModelPart
    private val EquipRdL04: ModelPart
    private val EquipRdL05: ModelPart
    private val EquipRdL06: ModelPart
    private val EquipRdR02: ModelPart
    private val EquipRdR03: ModelPart
    private val EquipRdR04: ModelPart
    private val EquipRdR05: ModelPart
    private val EquipRdR06: ModelPart
    private val GlowEquipBase: ModelPart
    private val GlowBodyMain: ModelPart?
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val buttDefaultY: Float
    private val buttDefaultZ: Float

    protected override val bodyMain: ModelPart get() = BodyMain
    protected override val neck: ModelPart get() = Neck
    protected override val head: ModelPart get() = Head
    protected override val glowBodyMain: ModelPart? get() = GlowBodyMain
    protected override val glowNeck: ModelPart? get() = GlowNeck
    protected override val glowHead: ModelPart? get() = GlowHead

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.ShoesL = this.LegLeft02.getChild("ShoesL")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.ShoesR = this.LegRight02.getChild("ShoesR")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.EquipHand02 = this.ArmRight02.getChild("EquipHand02")
        this.EquipHand01 = this.ArmRight01.getChild("EquipHand01")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.Hair03 = this.Hair02.getChild("Hair03")
        this.Hair = this.Head.getChild("Hair")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        initFaceParts(this.GlowHead)
        this.HeadHL = this.GlowHead.getChild("HeadHL")
        this.HeadHL2 = this.HeadHL.getChild("HeadHL2")
        this.HeadHL3 = this.HeadHL2.getChild("HeadHL3")
        this.HeadHR = this.GlowHead.getChild("HeadHR")
        this.HeadHR2 = this.HeadHR.getChild("HeadHR2")
        this.HeadHR3 = this.HeadHR2.getChild("HeadHR3")
        this.GlowEquipBase = this.GlowBodyMain.getChild("GlowEquipBase")
        this.EquipRdL01 = this.GlowEquipBase.getChild("EquipRdL01")
        this.EquipRdL02 = this.EquipRdL01.getChild("EquipRdL02")
        this.EquipRdL03 = this.EquipRdL02.getChild("EquipRdL03")
        this.EquipRdL04 = this.EquipRdL03.getChild("EquipRdL04")
        this.EquipRdL05 = this.EquipRdL04.getChild("EquipRdL05")
        this.EquipRdL06 = this.EquipRdL05.getChild("EquipRdL06")
        this.EquipRdR01 = this.GlowEquipBase.getChild("EquipRdR01")
        this.EquipRdR02 = this.EquipRdR01.getChild("EquipRdR02")
        this.EquipRdR03 = this.EquipRdR02.getChild("EquipRdR03")
        this.EquipRdR04 = this.EquipRdR03.getChild("EquipRdR04")
        this.EquipRdR05 = this.EquipRdR04.getChild("EquipRdR05")
        this.EquipRdR06 = this.EquipRdR05.getChild("EquipRdR06")
        this.buttDefaultY = this.Butt.y
        this.buttDefaultZ = this.Butt.z
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
        this.resetOffsets()
        this.applyEquipVisibility(entity)
        applyFaceAndMouth(entity)

        if (isDeadPose(entity)) {
            this.applyDeadPose()
            this.syncGlowParts()
            return
        }

        this.applyBasePose(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch)
        this.applySpecialPoseAdjustments(entity, ctx, limbSwing, limbSwingAmount, ageInTicks)
        this.syncGlowParts()
    }

    private fun resetOffsets() {
        this.isDeadPose = false
        this.isSittingPose = false
        this.poseTranslateY = 0.0f

        this.Butt.y = this.buttDefaultY
        this.Butt.z = this.buttDefaultZ
    }

    private fun applyEquipVisibility(entity: T?) {
        val showHand = entity!!.getEquipFlag(EntityAirfieldHime.EQUIP_HAND)
        this.EquipHand01.visible = showHand
        this.EquipHand02.visible = showHand
        this.EquipRdL01.visible = false
        this.EquipRdR01.visible = false
        this.EquipRdL02.visible = false
        this.EquipRdR02.visible = false
        this.EquipRdL03.visible = false
        this.EquipRdR03.visible = false
        this.EquipRdL04.visible = false
        this.EquipRdR04.visible = false
        this.EquipRdL05.visible = false
        this.EquipRdR05.visible = false
        this.EquipRdL06.visible = false
        this.EquipRdR06.visible = false
    }

    private fun applyDeadPose() {
        beginDeadPose(DEAD_TRANSLATE_Y)

        this.Head.xRot = 0.0f
        this.Head.yRot = 0.0f
        this.Head.zRot = 0.0f
        var headX = this.Head.xRot * -0.5f
        this.BoobL.xRot = -0.7f
        this.BoobR.xRot = -0.7f
        this.Ahoke.yRot = 0.5236f
        this.BodyMain.zRot = 0.0f
        this.Hair01.xRot = 0.26f + headX
        this.Hair02.xRot = -0.08f + headX
        this.Hair03.xRot = -0.14f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft02.xRot = 0.0f
        this.ArmRight02.xRot = 0.0f
        this.LegLeft01.yRot = 0.0f
        this.LegRight01.yRot = 0.0f

        this.Head.xRot += 0.14f
        this.BodyMain.xRot = 0.4f
        this.Butt.xRot = -0.4f
        this.Butt.z = this.buttDefaultZ + (0.19f * OFFSET_SCALE)
        this.BoobL.xRot -= 0.2f
        this.BoobR.xRot -= 0.2f
        this.ArmLeft01.xRot = -1.3f
        this.ArmLeft01.zRot = -0.1f
        this.ArmLeft02.zRot = 1.15f
        this.ArmRight01.xRot = -1.3f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = 0.1f
        this.ArmRight02.zRot = -1.4f

        this.LegLeft01.zRot = -0.2f
        this.LegLeft02.xRot = 1.34f
        this.LegRight01.zRot = 0.2f
        this.LegRight02.xRot = 1.13f

        this.Hair01.xRot -= 0.2f
        this.Hair02.xRot -= 0.2f
        this.Hair03.xRot -= 0.1f

        val headZ = this.Head.zRot * -0.5f
        this.Hair01.zRot = headZ
        this.Hair02.zRot = headZ
        this.HairL01.zRot = headZ
        this.HairL02.zRot = headZ + 0.087f
        this.HairR01.zRot = headZ
        this.HairR02.zRot = headZ - 0.052f

        headX = this.Head.xRot * -0.5f
        this.HairL01.xRot = headX - 0.5f
        this.HairL02.xRot = headX - 0.1f
        this.HairR01.xRot = headX - 0.5f
        this.HairR02.xRot = headX - 0.1f

        this.LegLeft01.xRot = -2.1232f
        this.LegRight01.xRot = -2.0708f
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
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f)
        val angleX2 = Mth.cos(ageInTicks * 0.08f + 0.6f + limbSwing * 0.5f)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount * 0.7f
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount * 0.7f

        if (entity!!.shipDepth > 0) {
            this.poseTranslateY = angleX * 0.05f + 0.025f
        }

        this.Head.xRot = headPitch * 0.014f
        this.Head.yRot = netHeadYaw * 0.01f
        this.Head.zRot = 0.0f

        val headX = this.Head.xRot * -0.5f

        this.BoobL.xRot = angleX * 0.06f - 0.7f
        this.BoobR.xRot = angleX * 0.06f - 0.7f
        this.Ahoke.yRot = angleX * 0.25f + 0.5236f
        this.BodyMain.xRot = -0.1745f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = 0.3142f
        this.Butt.z = this.buttDefaultZ

        this.Hair01.xRot = angleX * 0.03f + 0.26f + headX
        this.Hair01.zRot = 0.0f
        this.Hair02.xRot = -angleX1 * 0.04f - 0.08f + headX
        this.Hair02.zRot = 0.0f
        this.Hair03.xRot = -angleX2 * 0.07f - 0.14f
        this.Hair03.zRot = 0.0f

        this.ArmLeft01.xRot = angleAdd2 * 0.8f + 0.2f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = angleX * 0.08f - 0.2f
        this.ArmLeft02.xRot = 0.0f
        this.ArmLeft02.zRot = 0.0f

        this.ArmRight01.xRot = angleAdd1 * 0.8f + 0.2f
        this.ArmRight01.zRot = -angleX * 0.08f + 0.2f
        this.ArmRight02.xRot = 0.0f
        this.ArmRight02.zRot = 0.0f

        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.14f
        this.LegLeft02.xRot = 0.0f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.14f
        this.LegRight02.xRot = 0.0f

        val headZ = this.Head.zRot * -0.5f
        this.Hair01.zRot = headZ
        this.Hair02.zRot = headZ
        this.HairL01.zRot = headZ - 0.14f
        this.HairL02.zRot = headZ + 0.087f
        this.HairR01.zRot = headZ + 0.14f
        this.HairR02.zRot = headZ - 0.052f

        this.HairL01.xRot = angleX * 0.03f + headX - 0.26f
        this.HairL02.xRot = -angleX1 * 0.04f + headX + 0.26f
        this.HairR01.xRot = angleX * 0.03f + headX - 0.26f
        this.HairR02.xRot = -angleX1 * 0.04f + headX + 0.26f

        this.LegLeft01.xRot = angleAdd1
        this.LegRight01.xRot = angleAdd2 - 0.2f
    }

    private fun applySpecialPoseAdjustments(
        entity: T?,
        ctx: PoseContext,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float
    ) {
        val isCrouching = entity!!.isCrouching
        val isPassenger = entity.isPassenger
        var isSitting = ctx.isSitting
        if (entity != null) {
            isSitting = isSitting || entity.isInSittingPose

            if (entity.isPassenger && entity.vehicle !is EntityMountBase) {
                isSitting = true
            }
        }

        if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            this.Head.xRot -= 0.6283f
            this.BodyMain.xRot = 0.8727f
            this.ArmLeft01.xRot = -0.35f
            this.ArmLeft01.zRot = 0.2618f
            this.ArmRight01.xRot = -0.35f
            this.ArmRight01.zRot = -0.2618f

            this.LegLeft01.xRot -= 1.1f
            this.LegRight01.xRot -= 1.1f

            this.Hair01.xRot += 0.37f
            this.Hair02.xRot += 0.23f
            this.Hair03.xRot -= 0.1f
        }

        if (isPassenger && entity.vehicle is EntityMountBase) {
            if (isSitting) {
                this.poseTranslateY += RIDING_TRANSLATE_Y
                if (hasLegacyState(entity, 1, 4)) {
                    this.Head.xRot -= 0.3f
                    this.BodyMain.xRot = -0.4363f
                    this.BoobL.xRot -= 0.25f
                    this.BoobR.xRot -= 0.25f
                    this.ArmLeft01.xRot = -0.3142f
                    this.ArmLeft01.zRot = 0.349f
                    this.ArmLeft02.zRot = 1.15f
                    this.ArmRight01.xRot = -0.4363f
                    this.ArmRight01.zRot = -0.2793f
                    this.ArmRight02.zRot = -1.4f
                    this.LegLeft01.xRot = -1.309f
                    this.LegRight01.xRot = -1.7f
                    this.LegLeft01.yRot = 0.3142f
                    this.LegLeft02.xRot = 1.0472f
                    this.LegRight01.yRot = -0.35f
                    this.LegRight01.zRot = -0.2618f
                    this.LegRight02.xRot = 0.9f
                    this.Hair01.xRot += 0.12f
                    this.Hair02.xRot += 0.15f
                    this.Hair03.xRot += 0.25f
                } else {
                    this.BodyMain.xRot = -0.5236f
                    this.BoobL.xRot -= 0.2f
                    this.BoobR.xRot -= 0.2f
                    this.ArmLeft01.xRot = -0.4363f
                    this.ArmLeft01.zRot = 0.3142f
                    this.ArmRight01.xRot = -0.4363f
                    this.ArmRight01.zRot = -0.3142f
                    this.LegLeft01.xRot = -1.6232f
                    this.LegRight01.xRot = -1.5708f
                    this.LegLeft01.zRot = -0.3142f
                    this.LegLeft02.xRot = 1.34f
                    this.LegRight01.zRot = 0.35f
                    this.LegRight02.xRot = 1.13f
                    this.Hair01.xRot += 0.09f
                    this.Hair02.xRot += 0.43f
                    this.Hair03.xRot += 0.49f
                }
            } else {
                this.Head.xRot -= 0.1f
                this.ArmLeft01.xRot = 0.5f
                this.ArmLeft01.zRot = -1.2f
                this.ArmRight01.xRot = 0.5f
                this.ArmRight01.zRot = 1.2f
                this.LegLeft01.xRot = -0.2618f
                this.LegRight01.xRot = -0.35f
                this.LegRight02.xRot = 0.8727f
                this.Hair01.xRot += 0.45f
                this.Hair02.xRot += 0.43f
                this.Hair03.xRot += 0.49f
            }
        } else if (isSitting || isPassenger) {
            this.poseTranslateY += (if (isPassenger) RIDING_TRANSLATE_Y else SITTING_TRANSLATE_Y)
            if (hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY = 0.27f * 3f
                this.Head.xRot += 0.14f
                this.BodyMain.xRot = -0.4363f
                this.BoobL.xRot -= 0.25f
                this.BoobR.xRot -= 0.25f
                this.ArmLeft01.xRot = -0.3142f
                this.ArmLeft01.zRot = 0.349f
                this.ArmLeft02.zRot = 1.15f
                this.ArmRight01.xRot = -0.4363f
                this.ArmRight01.zRot = -0.2793f
                this.ArmRight02.zRot = -1.4f
                this.LegLeft01.xRot = -1.309f
                this.LegRight01.xRot = -1.7f
                this.LegLeft01.yRot = 0.3142f
                this.LegLeft02.xRot = 1.0472f
                this.LegRight01.yRot = -0.35f
                this.LegRight01.zRot = -0.2618f
                this.LegRight02.xRot = 0.9f
                this.Hair01.xRot += 0.12f
                this.Hair02.xRot += 0.15f
                this.Hair03.xRot += 0.25f
            } else {
                this.Head.xRot += 0.14f
                this.BodyMain.xRot = -0.5236f
                this.BoobL.xRot -= 0.2f
                this.BoobR.xRot -= 0.2f
                this.ArmLeft01.xRot = -0.4363f
                this.ArmLeft01.zRot = 0.3142f
                this.ArmRight01.xRot = -0.4363f
                this.ArmRight01.zRot = -0.3142f
                this.LegLeft01.zRot = -0.3142f
                this.LegLeft02.xRot = 1.34f
                this.LegRight01.zRot = 0.35f
                this.LegRight02.xRot = 1.13f
                this.Hair01.xRot += 0.09f
                this.Hair02.xRot += 0.43f
                this.Hair03.xRot += 0.49f
                this.LegLeft01.xRot = -1.6232f
                this.LegRight01.xRot = -1.5708f
            }
        }

        if (entity != null && entity.attackTick > 0) {
            if (entity.attackTick > 25) {
                if (hasLegacyModelFlag(entity, 2)) {
                    this.poseTranslateY += 0.15f
                    this.Head.yRot *= 0.8f
                    this.Head.xRot = 0.4538f
                    this.BodyMain.xRot = -1.0472f
                    this.BodyMain.zRot = -0.2094f
                    this.ArmLeft01.xRot = -0.35f
                    this.ArmLeft01.zRot = -0.35f
                    this.ArmLeft02.xRot = -0.5f
                    this.ArmRight01.xRot = 1.2f
                    this.ArmRight01.zRot = 0.5236f
                    this.ArmRight02.xRot = -0.35f
                    this.LegLeft01.xRot = 0.5236f
                    this.LegRight01.xRot = 0.1745f
                    this.LegLeft01.zRot = 0.2618f
                    this.LegLeft02.xRot = 0.5236f
                    this.LegRight01.zRot = 0.1745f
                    this.LegRight02.xRot = 0.5236f
                    this.Hair01.xRot += 0.09f
                    this.Hair02.xRot += 0.43f
                    this.Hair03.xRot += 0.49f
                } else if (hasLegacyModelFlag(entity, 3)) {
                    this.Head.yRot *= 0.8f
                    this.Head.xRot = 0.2094f
                    this.Head.zRot = -0.2618f
                    this.BodyMain.xRot = -0.35f
                    this.BodyMain.zRot = 0.1745f
                    this.ArmLeft01.xRot = -1.2217f
                    this.ArmLeft01.yRot = 0.5236f
                    this.ArmLeft01.zRot = -0.35f
                    this.ArmLeft02.xRot = -1.3963f
                    this.ArmRight01.xRot = 0.7854f
                    this.ArmRight01.zRot = 0.5236f
                    this.ArmRight02.xRot = -0.5236f
                    this.LegLeft01.xRot = -0.2618f
                    this.LegRight01.xRot = 0.3142f
                    this.LegLeft01.zRot = -0.4363f
                    this.LegLeft02.xRot = 0.2618f
                    this.LegRight01.zRot = 0.0873f
                    this.Hair01.xRot += 0.09f
                    this.Hair02.xRot += 0.43f
                    this.Hair03.xRot += 0.49f
                } else {
                    this.ArmLeft01.xRot = -1.3f
                    this.ArmLeft01.yRot = -0.7f
                    this.ArmLeft01.zRot = 0.0f
                }
            }
            setRoad(entity.attackTick)
        }

        val angleX = Mth.cos(ageInTicks * 0.08f)
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f)
        val headX = this.Head.xRot * -0.5f
        this.HairL01.xRot = angleX * 0.03f + headX - 0.26f
        this.HairL02.xRot = -angleX1 * 0.04f + headX + 0.26f
        this.HairR01.xRot = angleX * 0.03f + headX - 0.26f
        this.HairR02.xRot = -angleX1 * 0.04f + headX + 0.26f

        val partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
        val customAttackAnim = if (entity != null) entity.getCustomAttackAnim(partialTick) else 0.0f
        if (customAttackAnim > 0.0f) {
            val f7 = Mth.sin(customAttackAnim * customAttackAnim * Math.PI.toFloat())
            val f8 = Mth.sin(Mth.sqrt(customAttackAnim) * Math.PI.toFloat())
            ArmRight01.xRot += -f8 * 80.0f * (Math.PI.toFloat() / 180f)
            ArmRight01.yRot += -f7 * 20.0f * (Math.PI.toFloat() / 180f) + 0.2f
            ArmRight01.zRot += -f8 * 20.0f * (Math.PI.toFloat() / 180f)
        }
    }

    private fun setRoad(attackTime: Int) {
        when (attackTime) {
            26, 50 -> {
                this.EquipRdL01.visible = true
                this.EquipRdR01.visible = true
                this.EquipRdL02.visible = false
                this.EquipRdR02.visible = false
            }

            27, 49 -> {
                this.EquipRdL01.visible = true
                this.EquipRdR01.visible = true
                this.EquipRdL02.visible = true
                this.EquipRdR02.visible = true
                this.EquipRdL03.visible = false
                this.EquipRdR03.visible = false
            }

            28, 48 -> {
                this.EquipRdL01.visible = true
                this.EquipRdR01.visible = true
                this.EquipRdL02.visible = true
                this.EquipRdR02.visible = true
                this.EquipRdL03.visible = true
                this.EquipRdR03.visible = true
                this.EquipRdL04.visible = false
                this.EquipRdR04.visible = false
            }

            29, 47 -> {
                this.EquipRdL01.visible = true
                this.EquipRdR01.visible = true
                this.EquipRdL02.visible = true
                this.EquipRdR02.visible = true
                this.EquipRdL03.visible = true
                this.EquipRdR03.visible = true
                this.EquipRdL04.visible = true
                this.EquipRdR04.visible = true
                this.EquipRdL05.visible = false
                this.EquipRdR05.visible = false
            }

            30, 46 -> {
                this.EquipRdL01.visible = true
                this.EquipRdR01.visible = true
                this.EquipRdL02.visible = true
                this.EquipRdR02.visible = true
                this.EquipRdL03.visible = true
                this.EquipRdR03.visible = true
                this.EquipRdL04.visible = true
                this.EquipRdR04.visible = true
                this.EquipRdL05.visible = true
                this.EquipRdR05.visible = true
                this.EquipRdL06.visible = false
                this.EquipRdR06.visible = false
            }

            else -> if (attackTime > 30 && attackTime < 46) {
                this.EquipRdL01.visible = true
                this.EquipRdR01.visible = true
                this.EquipRdL02.visible = true
                this.EquipRdR02.visible = true
                this.EquipRdL03.visible = true
                this.EquipRdR03.visible = true
                this.EquipRdL04.visible = true
                this.EquipRdR04.visible = true
                this.EquipRdL05.visible = true
                this.EquipRdR05.visible = true
                this.EquipRdL06.visible = true
                this.EquipRdR06.visible = true
            }
        }
    }

    companion object {
        @JvmField
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "airfield_hime"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = LegacyPoseOffsets.deadY("ModelAirfieldHime")
        private val SNEAK_TRANSLATE_Y = LegacyPoseOffsets.sneakY("ModelAirfieldHime")
        private val SITTING_TRANSLATE_Y = LegacyPoseOffsets.sittingY("ModelAirfieldHime")
        private val RIDING_TRANSLATE_Y = LegacyPoseOffsets.ridingY("ModelAirfieldHime")

        @JvmStatic
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 104).addBox(-6.5f, -11f, -4f, 13f, 17f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1745f, 0f, 0f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(33, 101).addBox(-3.5f, 0f, 0f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.7f, -8.6f, -3.5f, -0.6981f, -0.1396f, -0.0873f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(33, 101).mirror()
                    .addBox(-3.5f, 0f, 0f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.7f, -8.6f, -3.5f, -0.6981f, 0.1396f, 0.0873f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(39, 0).addBox(-7.5f, 4f, -5.7f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0.3142f, 0f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 84).mirror().addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.7f, 9.5f, -2.6f, 0f, 0f, 0.14f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 84).mirror().addBox(-3f, 0f, 0f, 6f, 7f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 14f, -3f)
            )

            val ShoesL = LegLeft02.addOrReplaceChild(
                "ShoesL",
                CubeListBuilder.create().texOffs(87, 0).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 8f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 7f, 3f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 84).addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.7f, 9.5f, -2.6f, -0.1047f, 0f, -0.14f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 84).addBox(-3f, 0f, 0f, 6f, 7f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 14f, -3f)
            )

            val ShoesR = LegRight02.addOrReplaceChild(
                "ShoesR",
                CubeListBuilder.create().texOffs(87, 0).addBox(-3.5f, 0f, -3.5f, 7f, 8f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 7f, 3f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(2, 85).addBox(-3f, -1f, -2.5f, 5f, 13f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, -9.3f, -0.7f, 0.2094f, 0f, 0.2094f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(24, 83).addBox(0f, 0f, -5f, 5f, 13f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 12f, 2.5f)
            )

            val EquipHand02 = ArmRight02.addOrReplaceChild(
                "EquipHand02",
                CubeListBuilder.create().texOffs(0, 17).addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offset(2.5f, -0.5f, -2.5f)
            )

            val EquipHand01 = ArmRight01.addOrReplaceChild(
                "EquipHand01",
                CubeListBuilder.create().texOffs(0, 17).addBox(-3f, 0f, -3f, 6f, 5f, 6f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, 7.5f, 0f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(88, 26).addBox(-5.5f, -2f, -5f, 11f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10.3f, -0.5f, 0.2094f, 0f, 0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -1.5f, 0f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(48, 55).addBox(-7.5f, 0f, 0f, 15f, 12f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -15f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(46, 29).addBox(-7.5f, 0f, 0f, 15f, 17f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1f, 0.2618f, 0f, 0f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(0, 59).addBox(-8f, 0f, -5f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 13.5f, 5.5f, -0.0873f, 0f, 0f)
            )

            val Hair03 = Hair02.addOrReplaceChild(
                "Hair03",
                CubeListBuilder.create().texOffs(0, 37).addBox(-8f, 0f, -4.5f, 16f, 15f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12.5f, -0.1f, -0.1396f, 0f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(45, 77).addBox(-8f, -8f, -7.2f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(104, 29).addBox(0f, -4f, -11.5f, 0f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10.5f, -5f, 0f, 0.5236f, 0f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(25, 18).mirror().addBox(-1f, 0f, 0f, 2f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.5f, 3f, -3f, -0.2618f, 0.1745f, 0.1396f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(25, 18).mirror().addBox(-1f, 0f, 0f, 2f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 10f, 0f, 0.2618f, 0f, -0.0524f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(25, 18).addBox(-1f, 0f, 0f, 2f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.5f, 3f, -3f, -0.2618f, -0.1745f, -0.1396f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(25, 18).addBox(-1f, 0f, 0f, 2f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10f, 0f, 0.2618f, 0f, 0.0873f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(2, 85).mirror()
                    .addBox(-2f, -1f, -2.5f, 5f, 13f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.3f, -0.7f, 0.2094f, 0f, -0.2094f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(24, 83).mirror()
                    .addBox(-5f, 0f, -5f, 5f, 13f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 12f, 2.5f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 104),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1745f, 0f, 0f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(88, 26),
                PartPose.offsetAndRotation(0f, -10.3f, -0.5f, 0.2094f, 0f, 0f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(44, 101),
                PartPose.offset(0f, -1.5f, 0f)
            )

            val HeadHL = GlowHead.addOrReplaceChild(
                "HeadHL",
                CubeListBuilder.create().texOffs(39, 28).mirror()
                    .addBox(0f, -2.5f, -2.5f, 3f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.4f, -10.6f, 0.8f, -0.7854f, -0.1745f, -0.3142f)
            )

            val HeadHL2 = HeadHL.addOrReplaceChild(
                "HeadHL2",
                CubeListBuilder.create().texOffs(47, 56).addBox(0f, -2f, -2f, 1f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offset(3f, 0f, 0f)
            )

            val HeadHL3 = HeadHL2.addOrReplaceChild(
                "HeadHL3",
                CubeListBuilder.create().texOffs(43, 30).addBox(0f, -1.5f, -1.5f, 1f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offset(1f, 0f, 0f)
            )

            val HeadHR = GlowHead.addOrReplaceChild(
                "HeadHR",
                CubeListBuilder.create().texOffs(39, 28).mirror()
                    .addBox(-3f, -2.5f, -2.5f, 3f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.4f, -10.6f, 0.8f, -0.7854f, 0.1745f, 0.3142f)
            )

            val HeadHR2 = HeadHR.addOrReplaceChild(
                "HeadHR2",
                CubeListBuilder.create().texOffs(47, 56).addBox(-1f, -2f, -2f, 1f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offset(-3f, 0f, 0f)
            )

            val HeadHR3 = HeadHR2.addOrReplaceChild(
                "HeadHR3",
                CubeListBuilder.create().texOffs(43, 30).addBox(-1f, -1.5f, -1.5f, 1f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offset(-1f, 0f, 0f)
            )

            val GlowEquipBase = GlowBodyMain.addOrReplaceChild(
                "GlowEquipBase",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipRdL01 = GlowEquipBase.addOrReplaceChild(
                "EquipRdL01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5f, 0f, 6f, 1.4f, -0.3491f, -0.3491f)
            )

            val EquipRdL02 = EquipRdL01.addOrReplaceChild(
                "EquipRdL02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -11f, -0.3491f, 0f, 0f)
            )

            val EquipRdL03 = EquipRdL02.addOrReplaceChild(
                "EquipRdL03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -11f, -0.4363f, 0f, 0f)
            )

            val EquipRdL04 = EquipRdL03.addOrReplaceChild(
                "EquipRdL04",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -11f, -0.3491f, 0f, 0f)
            )

            val EquipRdL05 = EquipRdL04.addOrReplaceChild(
                "EquipRdL05",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -11f, -0.2618f, 0f, 0f)
            )

            val EquipRdL06 = EquipRdL05.addOrReplaceChild(
                "EquipRdL06",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -11f, -0.1745f, 0f, 0f)
            )

            val EquipRdR01 = GlowEquipBase.addOrReplaceChild(
                "EquipRdR01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5f, 0f, 6f, 1.4f, 0.3491f, 0.3491f)
            )

            val EquipRdR02 = EquipRdR01.addOrReplaceChild(
                "EquipRdR02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -11f, -0.3491f, 0f, 0f)
            )

            val EquipRdR03 = EquipRdR02.addOrReplaceChild(
                "EquipRdR03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -11f, -0.4363f, 0f, 0f)
            )

            val EquipRdR04 = EquipRdR03.addOrReplaceChild(
                "EquipRdR04",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -11f, -0.3491f, 0f, 0f)
            )

            val EquipRdR05 = EquipRdR04.addOrReplaceChild(
                "EquipRdR05",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -11f, -0.2618f, 0f, 0f)
            )

            val EquipRdR06 = EquipRdR05.addOrReplaceChild(
                "EquipRdR06",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, -12f, 7f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -11f, -0.1745f, 0f, 0f)
            )

            addFaceLayer(GlowHead)

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
