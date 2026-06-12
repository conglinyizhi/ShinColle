@file:Suppress("SENSELESS_COMPARISON")
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
import org.trp.shincolle.entity.EntityBattleshipRe
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelBattleshipRe<T : EntityShipBase>(root: ModelPart) : ShincolleShipModel<T>() {
    private var isTailPoseOverride = false

    private val BodyMain: ModelPart
    private val Cloth: ModelPart
    private val Neck: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val ArmLeft02: ModelPart?
    private val ArmRight02: ModelPart?
    private val BagMain: ModelPart?
    private val TailBase: ModelPart
    private val Butt: ModelPart
    private val Cloth2: ModelPart
    private val Head: ModelPart
    protected override val head: ModelPart get() = Head
    protected override val neck: ModelPart get() = Neck
    protected override val bodyMain: ModelPart get() = BodyMain
    private val Ear01: ModelPart?
    private val Ear02: ModelPart?
    private val Hair: ModelPart
    private val Hair01: ModelPart?
    private val HairU01: ModelPart?
    private val Cap: ModelPart?
    private val Cap2: ModelPart?
    private val Ahoke: ModelPart
    private val BoobM: ModelPart
    private val PalmLeft: ModelPart
    private val PalmRight: ModelPart
    private val BagMain2: ModelPart
    private val BagStrap1: ModelPart
    private val BagStrap2: ModelPart
    private val Tail1: ModelPart
    private val TailBack0: ModelPart
    private val Tail2: ModelPart
    private val TailBack1: ModelPart
    private val Tail3: ModelPart
    private val TailBack2: ModelPart
    private val Tail4: ModelPart
    private val TailBack3: ModelPart
    private val Tail5: ModelPart
    private val TailBack4: ModelPart
    private val Tail6: ModelPart
    private val TailBack5: ModelPart
    private val TailHeadBase: ModelPart
    private val TailBack6: ModelPart
    private val TailJaw1: ModelPart
    private val TailHead1: ModelPart
    private val TailHeadCL1: ModelPart
    private val TailHeadCR1: ModelPart
    private val TailJawT01: ModelPart
    private val TailJaw2: ModelPart
    private val TailJaw3: ModelPart
    private val TailHead2: ModelPart
    private val TailHeadT01: ModelPart
    private val TailHeadC1: ModelPart
    private val TailHead3: ModelPart
    private val TailHeadC2: ModelPart
    private val TailHeadC3: ModelPart
    private val TailHeadC4: ModelPart
    private val TailHeadCL2: ModelPart
    private val TailHeadCL3: ModelPart
    private val TailHeadCR2: ModelPart
    private val TailHeadCR3: ModelPart
    private val LegRight: ModelPart
    private val LegLeft: ModelPart
    private val GlowBodyMain: ModelPart?
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    protected override val glowHead: ModelPart? get() = GlowHead
    protected override val glowNeck: ModelPart? get() = GlowNeck
    protected override val glowBodyMain: ModelPart? get() = GlowBodyMain
    private val GlowTailBase: ModelPart
    private val GlowTail1: ModelPart
    private val GlowTail2: ModelPart
    private val GlowTail3: ModelPart
    private val GlowTail4: ModelPart
    private val GlowTail5: ModelPart
    private val GlowTail6: ModelPart
    private val GlowTailHeadBase: ModelPart
    private val GlowTailHead1: ModelPart
    private val GlowTailJaw1: ModelPart
    private val armLeft02DefaultX: Float
    private val armLeft02DefaultY: Float
    private val armLeft02DefaultZ: Float
    private val armRight02DefaultX: Float
    private val armRight02DefaultY: Float
    private val armRight02DefaultZ: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.PalmLeft = this.ArmLeft02.getChild("PalmLeft")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.BagMain = this.BodyMain.getChild("BagMain")
        this.BagStrap2 = this.BagMain.getChild("BagStrap2")
        this.BagStrap1 = this.BagMain.getChild("BagStrap1")
        this.BagMain2 = this.BagMain.getChild("BagMain2")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.PalmRight = this.ArmRight02.getChild("PalmRight")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.BoobM = this.BoobR.getChild("BoobM")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegLeft = this.Butt.getChild("LegLeft")
        this.LegRight = this.Butt.getChild("LegRight")
        this.Cloth = this.BodyMain.getChild("Cloth")
        this.Cloth2 = this.Cloth.getChild("Cloth2")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.Cap = this.Head.getChild("Cap")
        this.Ear01 = this.Head.getChild("Ear01")
        this.Ear02 = this.Head.getChild("Ear02")
        this.Hair = this.Head.getChild("Hair")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.Hair01 = this.Head.getChild("Hair01")
        this.HairU01 = this.Head.getChild("HairU01")
        this.Cap2 = this.Neck.getChild("Cap2")
        this.TailBase = this.BodyMain.getChild("TailBase")
        this.Tail1 = this.TailBase.getChild("Tail1")
        this.Tail2 = this.Tail1.getChild("Tail2")
        this.Tail3 = this.Tail2.getChild("Tail3")
        this.Tail4 = this.Tail3.getChild("Tail4")
        this.Tail5 = this.Tail4.getChild("Tail5")
        this.Tail6 = this.Tail5.getChild("Tail6")
        this.TailHeadBase = this.Tail6.getChild("TailHeadBase")
        this.TailHeadCL1 = this.TailHeadBase.getChild("TailHeadCL1")
        this.TailHeadCL2 = this.TailHeadCL1.getChild("TailHeadCL2")
        this.TailHeadCL3 = this.TailHeadCL1.getChild("TailHeadCL3")
        this.TailHeadCR1 = this.TailHeadBase.getChild("TailHeadCR1")
        this.TailHeadCR2 = this.TailHeadCR1.getChild("TailHeadCR2")
        this.TailHeadCR3 = this.TailHeadCR1.getChild("TailHeadCR3")
        this.TailHead1 = this.TailHeadBase.getChild("TailHead1")
        this.TailHeadC1 = this.TailHead1.getChild("TailHeadC1")
        this.TailHeadC2 = this.TailHeadC1.getChild("TailHeadC2")
        this.TailHeadC3 = this.TailHeadC1.getChild("TailHeadC3")
        this.TailHeadC4 = this.TailHeadC1.getChild("TailHeadC4")
        this.TailHead3 = this.TailHead1.getChild("TailHead3")
        this.TailHead2 = this.TailHead1.getChild("TailHead2")
        this.TailJaw1 = this.TailHeadBase.getChild("TailJaw1")
        this.TailJaw2 = this.TailJaw1.getChild("TailJaw2")
        this.TailJaw3 = this.TailJaw1.getChild("TailJaw3")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        initFaceParts(this.GlowHead)
        this.GlowTailBase = this.GlowBodyMain.getChild("GlowTailBase")
        this.GlowTail1 = this.GlowTailBase.getChild("GlowTail1")
        this.GlowTail2 = this.GlowTail1.getChild("GlowTail2")
        this.GlowTail3 = this.GlowTail2.getChild("GlowTail3")
        this.GlowTail4 = this.GlowTail3.getChild("GlowTail4")
        this.GlowTail5 = this.GlowTail4.getChild("GlowTail5")
        this.GlowTail6 = this.GlowTail5.getChild("GlowTail6")
        this.TailBack6 = this.GlowTail6.getChild("TailBack6")
        this.GlowTailHeadBase = this.GlowTail6.getChild("GlowTailHeadBase")
        this.GlowTailHead1 = this.GlowTailHeadBase.getChild("GlowTailHead1")
        this.TailHeadT01 = this.GlowTailHead1.getChild("TailHeadT01")
        this.GlowTailJaw1 = this.GlowTailHeadBase.getChild("GlowTailJaw1")
        this.TailJawT01 = this.GlowTailJaw1.getChild("TailJawT01")
        this.TailBack5 = this.GlowTail5.getChild("TailBack5")
        this.TailBack4 = this.GlowTail4.getChild("TailBack4")
        this.TailBack3 = this.GlowTail3.getChild("TailBack3")
        this.TailBack2 = this.GlowTail2.getChild("TailBack2")
        this.TailBack1 = this.GlowTail1.getChild("TailBack1")
        this.TailBack0 = this.GlowTailBase.getChild("TailBack0")
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
        this.BodyMain.allParts.forEach { obj: ModelPart? -> obj!!.resetPose() }
        this.GlowBodyMain!!.allParts.forEach { obj: ModelPart? -> obj!!.resetPose() }

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
        applySpecialPoseAdjustments(entity, ctx, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw)
        applyTailAnimation(ctx, limbSwing, limbSwingAmount, ageInTicks)

        var modf2 = ageInTicks % 128.0f
        if (modf2 < 6.0f) {
            if (modf2 >= 3.0f) {
                modf2 -= 3.0f
            }
            val anglef2 = Mth.sin(modf2 * 1.0472f) * 0.25f
            Ear01!!.zRot = -anglef2 - 0.14f
            Ear02!!.zRot = anglef2 + 0.14f
        } else {
            Ear01!!.zRot = -0.14f
            Ear02!!.zRot = 0.14f
        }

        syncGlowParts()
    }

    override fun resetPoseState() {
        super.resetPoseState()
        this.isTailPoseOverride = false
    }

    private fun resetOffsets() {
        if (ArmLeft02 != null) {
            ArmLeft02.x = armLeft02DefaultX
            ArmLeft02.y = armLeft02DefaultY
            ArmLeft02.z = armLeft02DefaultZ
        }
        if (ArmRight02 != null) {
            ArmRight02.x = armRight02DefaultX
            ArmRight02.y = armRight02DefaultY
            ArmRight02.z = armRight02DefaultZ
        }
    }

    

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) return

        val showHair = entity.getEquipFlag(EntityBattleshipRe.EQUIP_HAIR)
        if (Hair01 != null) Hair01.visible = showHair
        if (HairU01 != null) HairU01.visible = showHair
        if (Cap != null) Cap.visible = !showHair
        if (Cap2 != null) Cap2.visible = showHair
        if (BagMain != null) BagMain.visible = entity.getEquipFlag(EntityBattleshipRe.EQUIP_BAG)

        val showEars = entity.getEquipFlag(EntityBattleshipRe.EQUIP_EARS)
        if (Ear01 != null) Ear01.visible = showEars
        if (Ear02 != null) Ear02.visible = showEars
    }

    private fun applyDeadPose() {
        beginDeadPose(DEAD_TRANSLATE_Y)

        Head.xRot = -0.5236f
        Head.yRot = 0.0f
        Head.zRot = 0.0f
        BoobL.xRot = -0.73f
        BoobR.xRot = -0.73f
        Ahoke.yRot = 0.5236f

        BodyMain.xRot = 1.5708f
        BodyMain.yRot = 0.0f
        Cloth2.xRot = -0.0524f

        ArmLeft01.xRot = -2.9671f
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = 0.0349f
        ArmLeft02!!.zRot = 0.0f

        ArmRight01.xRot = -2.9671f
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = -0.0349f
        ArmRight02!!.zRot = 0.0f

        BagStrap1.xRot = 0.2618f
        BagStrap1.yRot = -0.1396f
        BagStrap1.zRot = -0.1745f
        BagStrap2.xRot = 0.3491f
        BagStrap2.yRot = 0.3491f

        LegLeft.xRot = -0.3491f
        LegLeft.yRot = 0.0f
        LegRight.xRot = -0.3491f
        LegRight.yRot = 0.0f

        TailBase.xRot = -0.4f
        TailBase.yRot = -0.8f
        TailBase.zRot = 0.0f
        Tail1.xRot = -0.3f
        Tail1.yRot = -0.35f
        Tail2.xRot = -0.35f
        Tail2.yRot = -0.3f
        Tail3.xRot = -0.4f
        Tail3.yRot = -0.2f
        Tail4.xRot = -0.25f
        Tail4.yRot = 0.2f
        Tail5.xRot = 0.25f
        Tail5.yRot = 0.2f
        Tail6.xRot = 0.35f
        Tail6.yRot = 0.2f
        TailHeadBase.xRot = 0.4f
        TailHeadBase.yRot = 0.0f
        TailHead1.xRot = 0.2618f
        TailJaw1.xRot = -0.7f

        if (Hair01 != null) Hair01.visible = false
        if (Ear01 != null) Ear01.visible = false
        if (Ear02 != null) Ear02.visible = false
    }

    private fun applyBasePose(
        ctx: PoseContext,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headPitch: Float
    ) {
        val angleX = ctx.angleX

        BoobL.xRot = -angleX * 0.06f - 0.73f
        BoobR.xRot = -angleX * 0.06f - 0.73f
        Ahoke.yRot = angleX * 0.25f + 0.5236f

        Head.xRot -= 0.5236f
        Cap2!!.xRot = -1.4f

        BodyMain.xRot = 0.0873f
        BodyMain.yRot = 0.0f
        Cloth2.xRot = -0.0524f

        ArmLeft01.xRot = 0.2618f
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = angleX * 0.1f - 0.5236f
        ArmLeft02!!.zRot = 0.0f

        ArmRight01.xRot = 0.2618f
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = -angleX * 0.1f + 0.5236f
        ArmRight02!!.zRot = 0.0f

        BagStrap1.xRot = 0.2618f
        BagStrap1.yRot = -0.1396f
        BagStrap1.zRot = -0.1745f
        BagStrap2.xRot = 0.3491f
        BagStrap2.yRot = 0.3491f

        LegLeft.yRot = 0.0f
        LegRight.yRot = 0.0f
    }

    private fun applySpecialPoseAdjustments(
        entity: T?,
        ctx: PoseContext,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float
    ) {
        var addk1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount - 0.2618f
        var addk2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount - 0.2618f

        if (entity != null && entity.shipDepth > 0.0) {
            this.poseTranslateY += ctx.angleX * 0.05f + 0.025f
        }

        val tickPhase = if (entity != null) entity.tickCount else 0

        val isCrouching = entity != null && entity.isCrouching
        val isSitting =
            ctx.isSitting || (entity != null && entity.isPassenger && (entity.vehicle !is EntityMountBase))
        val isSprinting = entity != null && entity.isSprinting || limbSwingAmount > 0.9f

        if (isSprinting) {
            val t2 = (tickPhase and 0x3FF).toFloat()
            if (t2 > 700.0f) {
                this.poseTranslateY += 0.05f
                ArmLeft01.xRot = Mth.cos(ageInTicks * 0.8f) * 0.1f - 2.0944f
                ArmLeft01.yRot = -0.5236f
                ArmLeft01.zRot = 0.0f
                ArmRight01.xRot = -Mth.cos(ageInTicks * 0.8f) * 0.1f - 2.0944f
                ArmRight01.yRot = 0.5236f
                ArmRight01.zRot = 0.0f

                Head.xRot *= 0.75f
                Head.xRot -= 0.5236f
                Cap2!!.xRot = -1.74f
                BodyMain.xRot = 0.5236f
                BodyMain.yRot = 3.1416f
                Cloth2.xRot = -0.7854f

                addk1 = addk1 * 0.1f - 1.2708f
                addk2 = addk2 * 0.1f - 1.2708f
                LegLeft.yRot = -0.2618f
                LegRight.yRot = 0.2618f

                BagStrap1.xRot = 0.0872f
                BagStrap1.yRot = 0.0f
                BagStrap1.zRot = -0.1745f
                BagStrap2.xRot = 0.0872f
                BagStrap2.yRot = 0.3491f

                applySprintingTailAnimation(ctx.angleX, limbSwing, limbSwingAmount)
            } else if (t2 > 400.0f) {
                this.poseTranslateY += 0.05f
                ArmLeft01.xRot = -1.0472f
                ArmLeft01.yRot = 0.2618f
                ArmLeft01.zRot = 0.0f
                ArmRight01.xRot = -2.7925f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = netHeadYaw * (-Math.PI.toFloat() / 180f)

                Head.xRot *= 0.75f
                Head.xRot -= 1.2217f
                Cap2!!.xRot = -1.74f
                BodyMain.xRot = 1.2217f
                BodyMain.yRot = 0.0f
                Cloth2.xRot = -0.3491f

                addk1 = -1.0472f
                addk2 = -1.0472f
                LegLeft.yRot = -0.3491f
                LegRight.yRot = 0.3491f

                BagStrap1.xRot = 0.2618f
                BagStrap1.yRot = 0.0f
                BagStrap1.zRot = 0.0f
                BagStrap2.xRot = 0.3491f
                BagStrap2.yRot = 0.3491f

                applySprintingTailPoseStatic(ctx.angleX)
            } else {
                this.poseTranslateY += 0.1f
                ArmLeft01.xRot = Mth.cos(limbSwing * 0.8f) * 0.1f + 0.6981f
                ArmLeft01.yRot = 0.0f
                ArmLeft01.zRot = -0.6981f
                ArmRight01.xRot = Mth.cos(limbSwing * 0.8f) * 0.1f + 0.6981f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = 0.6981f

                Head.xRot *= 0.75f
                Head.xRot -= 1.0472f
                Cap2!!.xRot = -1.74f
                BodyMain.xRot = 0.8727f
                BodyMain.yRot = 0.0f
                Cloth2.xRot = -0.5236f

                addk1 -= 0.5f
                addk2 -= 0.5f
                LegLeft.yRot = 0.0f
                LegRight.yRot = 0.0f

                BagStrap1.xRot = 0.15f
                BagStrap1.yRot = -1.0472f
                BagStrap1.zRot = 0.0f
                BagStrap2.xRot = 0.3491f
                BagStrap2.yRot = 1.0472f

                applySprintingTailAnimationAlt(ctx.angleX, limbSwing, limbSwingAmount)
            }
            this.isTailPoseOverride = true
        } else if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            ArmLeft01.xRot = 0.5236f
            ArmLeft01.zRot = -0.5236f
            ArmRight01.xRot = 0.5236f
            ArmRight01.zRot = 0.5236f

            Head.xRot = -1.2217f
            BodyMain.xRot = 1.0472f
            Cloth2.xRot = -0.5236f
            addk1 -= 0.95f
            addk2 -= 0.95f

            LegLeft.yRot = 0.0f
            LegRight.yRot = 0.0f

            BagStrap1.xRot = 0.15f
            BagStrap1.yRot = -1.0472f
            BagStrap1.zRot = 0.0f
            BagStrap2.xRot = 0.3491f
            BagStrap2.yRot = 1.0472f
            BagStrap2.zRot = 0.0f

            applyCrouchTailPose()
            this.isTailPoseOverride = true
        } else if (isSitting) {
            this.isSittingPose = true
            Cap2!!.visible = false
            if ((tickPhase and 0x3FF) > 512) {
                if (entity != null && hasLegacyState(entity, 1, 4)) {
                    this.poseTranslateY += 0.13f
                    Head.xRot += 0.3f
                    BodyMain.xRot = -0.3f
                    Cloth2.xRot = -0.3f
                    ArmLeft01.xRot = 2.3f
                    ArmLeft01.yRot = 0.0f
                    ArmLeft01.zRot = 0.2f
                    ArmLeft02!!.zRot = 1.0f
                    ArmRight01.xRot = 2.3f
                    ArmRight01.yRot = 0.0f
                    ArmRight01.zRot = -0.2f
                    ArmRight02!!.zRot = -1.0f

                    val parTick = ageInTicks - ageInTicks.toInt() + (tickPhase and 0xFF)
                    if (parTick < 30.0f) {
                        val az = Mth.sin(parTick * 0.033f * 1.5708f) * 1.6f
                        val az1 = az * 1.6f
                        setFace(EntityShipBase.FACE_WINK)
                        ArmLeft01.zRot = 0.2f + az
                        ArmLeft02.zRot = 1.0f - az1
                        if (ArmLeft02.zRot < 0.0f) ArmLeft02.zRot = 0.0f
                        ArmRight01.zRot = -0.2f - az
                        ArmRight02.zRot = -1.0f + az1
                        if (ArmRight02.zRot > 0.0f) ArmRight02.zRot = 0.0f
                    } else if (parTick < 45.0f) {
                        setFace(EntityShipBase.FACE_WINK)
                        ArmLeft01.zRot = 1.8f
                        ArmLeft02.zRot = 0.0f
                        ArmRight01.zRot = -1.8f
                        ArmRight02.zRot = 0.0f
                    } else if (parTick < 53.0f) {
                        val az = Mth.cos((parTick - 45.0f) * 0.125f * 1.5708f)
                        val az1 = az * 1.6f
                        ArmLeft01.zRot = 0.2f + az1
                        ArmLeft02.zRot = 1.0f - az
                        ArmRight01.zRot = -0.2f - az1
                        ArmRight02.zRot = -1.0f + az
                    }

                    BagStrap1.xRot = 0.6f
                    BagStrap1.yRot = 0.0f
                    BagStrap1.zRot = 0.0f
                    BagStrap2.xRot = 1.0472f
                    BagStrap2.yRot = 1.3963f
                    addk1 = ctx.angleX * 0.1f - 0.9f
                    addk2 = -ctx.angleX * 0.1f - 0.9f
                    LegLeft.yRot = -0.2f
                    LegRight.yRot = 0.2f
                    TailBase.xRot = -1.0f
                    TailBase.yRot = 0.2618f
                    TailBase.zRot = 0.0f
                    Tail1.xRot = 0.6981f
                    Tail1.yRot = 0.0872f
                    Tail1.zRot = 0.0f
                    Tail2.xRot = 0.5236f
                    Tail2.yRot = 0.0872f
                    Tail2.zRot = 0.1745f
                    Tail3.xRot = 0.0f
                    Tail3.yRot = 0.6981f
                    Tail3.zRot = 0.0f
                    Tail4.xRot = 0.0f
                    Tail4.yRot = 0.6981f
                    Tail4.zRot = 0.0f
                    Tail5.xRot = 0.0f
                    Tail5.yRot = 0.5236f
                    Tail5.zRot = 0.0f
                    Tail6.xRot = 0.0f
                    Tail6.yRot = 0.5236f
                    Tail6.zRot = 0.0f
                    TailHeadBase.xRot = 0.2618f
                    TailHeadBase.yRot = 0.5236f
                    TailHeadBase.zRot = 0.0f
                    TailHead1.xRot = 0.2618f
                    TailJaw1.xRot = ctx.angleX * 0.1f - 0.2618f

                    this.isTailPoseOverride = true
                } else {
                    this.poseTranslateY += SITTING_TRANSLATE_Y
                    Head.xRot *= 0.8f
                    Head.xRot -= 1.8f
                    Head.yRot *= 0.5f
                    BodyMain.xRot = 1.5708f
                    Cloth2.xRot = -0.0524f
                    ArmLeft01.xRot = -2.9671f
                    ArmLeft01.zRot = 0.0349f
                    ArmLeft02!!.zRot = 1.3962f
                    ArmRight01.xRot = -2.9671f
                    ArmRight01.zRot = -0.0349f
                    ArmRight02!!.zRot = -1.3962f
                    BagStrap1.xRot = 0.2618f
                    BagStrap1.yRot = -0.1396f
                    BagStrap1.zRot = -0.1745f
                    BagStrap2.xRot = 0.3491f
                    BagStrap2.yRot = 0.3491f
                    addk1 = -0.3491f
                    addk2 = -0.3491f

                    applySittingTailAnimation(ctx.angleX, ageInTicks)
                    this.isTailPoseOverride = true
                }
            } else {
                setFace(EntityShipBase.FACE_EYES_CLOSED)
                this.poseTranslateY += 0.17f * 2
                ArmLeft01.xRot = -1.7f
                ArmLeft01.yRot = -0.1f
                ArmLeft01.zRot = 0.0f
                ArmRight01.xRot = -1.8f
                ArmRight01.yRot = 0.1f
                ArmRight01.zRot = 0.0f
                Head.xRot = -1.5f
                Head.yRot = 0.0f
                Head.zRot = 0.7f
                Cap2.xRot = -1.74f
                BodyMain.xRot = 1.8f
                Cloth2.xRot = -0.3491f
                addk1 = -1.8f
                addk2 = -1.8f
                LegLeft.yRot = -0.23f
                LegRight.yRot = 0.23f
                BagStrap1.xRot = 0.2618f
                BagStrap1.yRot = 0.0f
                BagStrap1.zRot = 0.0f
                BagStrap2.xRot = 0.3491f
                BagStrap2.yRot = 0.3491f
                TailBase.xRot = 1.6f
                TailBase.yRot = 0.0f
                TailBase.zRot = 3.1415f
                Tail1.xRot = 0.8f
                Tail1.yRot = 0.0f
                Tail1.zRot = 0.0f
                Tail2.xRot = 0.8f
                Tail2.yRot = 0.0f
                Tail2.zRot = 0.0f
                Tail3.xRot = 0.9f
                Tail3.yRot = 0.0f
                Tail3.zRot = 0.0f
                Tail4.xRot = 0.9f
                Tail4.yRot = 0.0f
                Tail4.zRot = 0.0f
                Tail5.xRot = 0.4f
                Tail5.yRot = 0.0f
                Tail5.zRot = 0.0f
                Tail6.xRot = -0.4f
                Tail6.yRot = 0.0f
                Tail6.zRot = 0.0f
                TailHeadBase.xRot = -0.3f
                TailHeadBase.yRot = 0.0f
                TailHeadBase.zRot = 0.8f
                TailHead1.xRot = 0.1745f
                TailJaw1.xRot = -0.5f

                this.isTailPoseOverride = true
            }
        }

        if (entity != null && entity.attackTick > 0) {
            this.poseTranslateY += 0.13f
            ArmLeft01.xRot = 0.5236f
            ArmLeft01.yRot = 0.0f
            ArmLeft01.zRot = -0.5236f
            ArmRight01.xRot = -2.7925f
            ArmRight01.yRot = 0.0f
            ArmRight01.zRot = -0.2618f
            Head.xRot = -1.2217f
            Head.yRot = 0.0f
            BodyMain.xRot = 1.0472f
            Cloth2.xRot = -0.5236f
            addk1 -= 1.48f
            addk2 -= 0.26f
            BagStrap1.xRot = 0.15f
            BagStrap1.yRot = -1.0472f
            BagStrap1.zRot = 0.0f
            BagStrap2.xRot = 0.3491f
            BagStrap2.yRot = 0.3491f
            TailBase.xRot = 0.6f
            TailBase.yRot = 0.0f
            TailBase.zRot = 3.1416f
            Tail1.xRot = -0.2618f
            Tail1.yRot = 0.0f
            Tail1.zRot = 0.0f
            Tail2.xRot = -0.5236f
            Tail2.yRot = 0.0f
            Tail2.zRot = 0.0f
            Tail3.xRot = -0.2618f
            Tail3.yRot = 0.0f
            Tail3.zRot = 0.0f
            Tail4.xRot = -0.2618f
            Tail4.yRot = 0.0f
            Tail4.zRot = 0.0f
            Tail5.xRot = -0.5236f
            Tail5.yRot = 0.0f
            Tail5.zRot = 0.0f
            Tail6.xRot = -0.5236f
            Tail6.yRot = 0.0f
            Tail6.zRot = 0.0f
            TailHeadBase.xRot = -0.2618f
            TailHeadBase.yRot = 0.0f
            TailHeadBase.zRot = 0.0f
            if (entity.attackTick > 47) {
                TailHead1.xRot = (50 - entity.attackTick) * 0.15f + 0.4f
                TailJaw1.xRot = (entity.attackTick - 50) * 0.15f - 0.4f
            } else if (entity.attackTick > 39) {
                TailHead1.xRot = 0.76f - (46 - entity.attackTick) * 0.06f
                TailJaw1.xRot = -0.76f + (46 - entity.attackTick) * 0.06f
            } else {
                TailHead1.xRot = 0.4f
                TailJaw1.xRot = -0.4f
            }

            this.isTailPoseOverride = true
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

        LegLeft.xRot = addk1
        LegRight.xRot = addk2
    }

    private fun applyTailAnimation(ctx: PoseContext, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        if (this.isTailPoseOverride) {
            return
        }

        applyIdleTailAnimation(ctx.angleX, ageInTicks)
    }

    private fun applyIdleTailAnimation(angleX: Float, ageInTicks: Float) {
        TailBase.xRot = -0.5236f
        TailBase.yRot = Mth.cos(-ageInTicks * 0.1f) * 0.1f
        TailBase.zRot = 0.0f
        Tail1.xRot = 0.5236f
        Tail1.yRot = Mth.cos(-ageInTicks * 0.1f + 0.7f) * 0.1f
        Tail1.zRot = 0.0f
        Tail2.xRot = 0.5236f
        Tail2.yRot = Mth.cos(-ageInTicks * 0.1f + 1.4f) * 0.15f
        Tail2.zRot = 0.0f
        Tail3.xRot = 0.5236f
        Tail3.yRot = Mth.cos(-ageInTicks * 0.1f + 2.1f) * 0.2f
        Tail3.zRot = 0.0f
        Tail4.xRot = 0.5236f
        Tail4.yRot = Mth.cos(-ageInTicks * 0.1f + 2.8f) * 0.25f
        Tail4.zRot = 0.0f
        Tail5.xRot = -0.5236f
        Tail5.yRot = Mth.cos(-ageInTicks * 0.1f + 3.5f) * 0.3f
        Tail5.zRot = 0.0f
        Tail6.xRot = -0.5236f
        Tail6.yRot = Mth.cos(-ageInTicks * 0.1f + 4.2f) * 0.35f
        Tail6.zRot = 0.0f
        TailHeadBase.xRot = -0.5236f
        TailHeadBase.yRot = Mth.cos(-ageInTicks * 0.1f + 4.9f) * 0.4f
        TailHeadBase.zRot = 0.0f
        TailHead1.xRot = 0.1745f
        TailJaw1.xRot = angleX * 0.1f - 0.15f
    }

    private fun applySittingTailAnimation(angleX: Float, ageInTicks: Float) {
        TailBase.xRot = -0.7f
        TailBase.yRot = Mth.cos(-ageInTicks * 0.1f) * 0.1f
        TailBase.zRot = Mth.cos(-ageInTicks * 0.1f) * 0.05f
        Tail1.xRot = 0.35f
        Tail1.yRot = Mth.cos(-ageInTicks * 0.1f + 0.7f) * 0.2f
        Tail1.zRot = -Mth.cos(-ageInTicks * 0.1f + 0.7f) * 0.05f
        Tail2.xRot = 0.35f
        Tail2.yRot = Mth.cos(-ageInTicks * 0.1f + 1.4f) * 0.3f
        Tail2.zRot = -Mth.cos(-ageInTicks * 0.1f + 1.4f) * 0.05f
        Tail3.xRot = 0.35f
        Tail3.yRot = Mth.cos(-ageInTicks * 0.1f + 2.1f) * 0.4f
        Tail3.zRot = -Mth.cos(-ageInTicks * 0.1f + 2.1f) * 0.05f
        Tail4.xRot = -0.2618f
        Tail4.yRot = Mth.cos(-ageInTicks * 0.1f + 2.8f) * 0.5f
        Tail4.zRot = Mth.cos(-ageInTicks * 0.1f + 2.8f) * 0.025f
        Tail5.xRot = -0.35f
        Tail5.yRot = Mth.cos(-ageInTicks * 0.1f + 3.5f) * 0.55f
        Tail5.zRot = Mth.cos(-ageInTicks * 0.1f + 3.5f) * 0.05f
        Tail6.xRot = -0.35f
        Tail6.yRot = Mth.cos(-ageInTicks * 0.1f + 4.2f) * 0.6f
        Tail6.zRot = Mth.cos(-ageInTicks * 0.1f + 4.2f) * 0.05f
        TailHeadBase.xRot = -0.15f
        TailHeadBase.yRot = Mth.cos(-ageInTicks * 0.1f + 4.9f) * 0.65f
        TailHeadBase.zRot = Mth.cos(-ageInTicks * 0.1f + 4.9f) * 0.025f
        TailHead1.xRot = 0.2618f
        TailJaw1.xRot = angleX * 0.1f - 0.15f
    }

    private fun applySprintingTailAnimation(angleX: Float, limbSwing: Float, limbSwingAmount: Float) {
        TailBase.xRot = -1.3f
        TailBase.yRot = -Mth.cos(limbSwing * 0.25f - 5.0f) * 0.2f * limbSwingAmount
        TailBase.zRot = Mth.cos(limbSwing * 0.25f - 5.0f) * 0.4f * limbSwingAmount
        Tail1.xRot = 0.2618f
        Tail1.yRot = -Mth.cos(limbSwing * 0.25f - 4.2f) * 0.3f * limbSwingAmount
        Tail1.zRot = -Mth.cos(limbSwing * 0.25f - 4.2f) * 0.1f * limbSwingAmount
        Tail2.xRot = 0.2618f
        Tail2.yRot = -Mth.cos(limbSwing * 0.25f - 3.5f) * 0.4f * limbSwingAmount
        Tail2.zRot = -Mth.cos(limbSwing * 0.25f - 3.5f) * 0.1f * limbSwingAmount
        Tail3.xRot = 0.1745f
        Tail3.yRot = -Mth.cos(limbSwing * 0.25f - 2.8f) * 0.5f * limbSwingAmount
        Tail3.zRot = 0.0f
        Tail4.xRot = 0.1745f
        Tail4.yRot = -Mth.cos(limbSwing * 0.25f - 2.1f) * 0.5f * limbSwingAmount
        Tail4.zRot = 0.0f
        Tail5.xRot = 0.0873f
        Tail5.yRot = -Mth.cos(limbSwing * 0.25f - 1.4f) * 0.4f * limbSwingAmount
        Tail5.zRot = 0.0f
        Tail6.xRot = 0.0873f
        Tail6.yRot = -Mth.cos(limbSwing * 0.25f - 0.7f) * 0.3f * limbSwingAmount
        Tail6.zRot = 0.0f
        TailHeadBase.xRot = -0.0873f
        TailHeadBase.yRot = -Mth.cos(limbSwing * 0.25f) * 0.2f * limbSwingAmount
        TailHeadBase.zRot = 0.0f
        TailHead1.xRot = 0.3f
        TailJaw1.xRot = angleX * 0.2f - 0.3f
    }

    private fun applySprintingTailPoseStatic(angleX: Float) {
        TailBase.xRot = 1.0472f
        TailBase.yRot = 0.0f
        TailBase.zRot = 3.1415f
        Tail1.xRot = 0.7854f
        Tail1.yRot = 0.0f
        Tail1.zRot = 0.0f
        Tail2.xRot = 0.7854f
        Tail2.yRot = 0.0f
        Tail2.zRot = 0.0f
        Tail3.xRot = 0.7854f
        Tail3.yRot = 0.0f
        Tail3.zRot = 0.0f
        Tail4.xRot = 0.7854f
        Tail4.yRot = 0.0f
        Tail4.zRot = 0.0f
        Tail5.xRot = 0.5236f
        Tail5.yRot = 0.0f
        Tail5.zRot = 0.0f
        Tail6.xRot = -0.2618f
        Tail6.yRot = 0.0f
        Tail6.zRot = 0.0f
        TailHeadBase.xRot = 0.0f
        TailHeadBase.yRot = 0.0f
        TailHeadBase.zRot = 0.0f
        TailHead1.xRot = 0.1745f
        TailJaw1.xRot = angleX * 0.15f - 0.3f
    }

    private fun applySprintingTailAnimationAlt(angleX: Float, limbSwing: Float, limbSwingAmount: Float) {
        val swing = -limbSwing * 0.3f
        TailBase.xRot = -0.7f
        TailBase.yRot = -Mth.cos(swing) * 0.2f * limbSwingAmount
        TailBase.zRot = Mth.cos(swing) * 0.3f * limbSwingAmount
        Tail1.xRot = 0.2618f
        Tail1.yRot = -Mth.cos(swing + 0.7f) * 0.2f * limbSwingAmount
        Tail1.zRot = -Mth.cos(swing + 0.7f) * 0.1f * limbSwingAmount
        Tail2.xRot = 0.2618f
        Tail2.yRot = -Mth.cos(swing + 1.4f) * 0.3f * limbSwingAmount
        Tail2.zRot = -Mth.cos(swing + 1.4f) * 0.1f * limbSwingAmount
        Tail3.xRot = -0.2618f
        Tail3.yRot = -Mth.cos(swing + 2.2f) * 0.3f * limbSwingAmount
        Tail3.zRot = Mth.cos(swing + 2.2f) * 0.1f * limbSwingAmount
        Tail4.xRot = -0.2618f
        Tail4.yRot = -Mth.cos(swing + 2.8f) * 0.4f * limbSwingAmount
        Tail4.zRot = Mth.cos(swing + 2.8f) * 0.1f * limbSwingAmount
        Tail5.xRot = -0.2618f
        Tail5.yRot = -Mth.cos(swing + 3.5f) * 0.4f * limbSwingAmount
        Tail5.zRot = Mth.cos(swing + 3.5f) * 0.1f * limbSwingAmount
        Tail6.xRot = -0.2618f
        Tail6.yRot = -Mth.cos(swing + 4.2f) * 0.5f * limbSwingAmount
        Tail6.zRot = Mth.cos(swing + 4.2f) * 0.1f * limbSwingAmount
        TailHeadBase.xRot = 0.2618f
        TailHeadBase.yRot = -Mth.cos(swing + 4.9f) * 0.6f * limbSwingAmount
        TailHeadBase.zRot = -Mth.cos(swing + 4.9f) * 0.1f * limbSwingAmount
        TailHead1.xRot = 0.1745f
        TailJaw1.xRot = angleX * 0.15f - 0.3f
    }

    private fun applyCrouchTailPose() {
        TailBase.xRot = 0.7f
        TailBase.yRot = 0.0f
        TailBase.zRot = 3.1416f
        Tail1.xRot = -0.2618f
        Tail1.yRot = 0.0f
        Tail1.zRot = 0.0f
        Tail2.xRot = -0.5236f
        Tail2.yRot = 0.0f
        Tail2.zRot = 0.0f
        Tail3.xRot = -0.2618f
        Tail3.yRot = 0.0f
        Tail3.zRot = 0.0f
        Tail4.xRot = -0.2618f
        Tail4.yRot = 0.0f
        Tail4.zRot = 0.0f
        Tail5.xRot = -0.5236f
        Tail5.yRot = 0.0f
        Tail5.zRot = 0.0f
        Tail6.xRot = -0.5236f
        Tail6.yRot = 0.0f
        Tail6.zRot = 0.0f
        TailHeadBase.xRot = -0.2618f
        TailHeadBase.yRot = 0.0f
        TailHeadBase.zRot = 0.0f
        TailHead1.xRot = 0.1745f
        TailJaw1.xRot = -0.2f
    }
    override fun syncExtraGlowParts() {
        GlowTailBase.copyFrom(TailBase)
        GlowTail1.copyFrom(Tail1)
        GlowTail2.copyFrom(Tail2)
        GlowTail3.copyFrom(Tail3)
        GlowTail4.copyFrom(Tail4)
        GlowTail5.copyFrom(Tail5)
        GlowTail6.copyFrom(Tail6)
        GlowTailHeadBase.copyFrom(TailHeadBase)
        GlowTailHead1.copyFrom(TailHead1)
        GlowTailJaw1.copyFrom(TailJaw1)
    }

    

    

    

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "battleship_re"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelBattleshipRe")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelBattleshipRe")
        private val SITTING_TRANSLATE_Y = sittingY("ModelBattleshipRe")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 34).addBox(-7f, -9f, -4f, 14f, 15f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -8f, 0f, 0.0524f, 0f, 0f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(0, 57).mirror().addBox(0f, 0f, -3f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.5f, -8.5f, -0.5f, 0.2618f, 0f, -0.4363f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(0, 57).mirror().addBox(-6f, 0f, -6f, 6f, 7f, 6f, CubeDeformation(0f)),
                PartPose.offset(6f, 10f, 3f)
            )

            val PalmLeft = ArmLeft02.addOrReplaceChild(
                "PalmLeft",
                CubeListBuilder.create().texOffs(0, 89).mirror()
                    .addBox(-2.5f, 0f, -2.5f, 5f, 4f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 7f, -3f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(0, 80).mirror().addBox(-3.5f, 0f, 0f, 7f, 5f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.5f, -9.5f, -3f, -0.7854f, -0.1222f, -0.0873f)
            )

            val BagMain = BodyMain.addOrReplaceChild(
                "BagMain",
                CubeListBuilder.create().texOffs(37, 23).addBox(-8f, 0f, 0f, 14f, 12f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3f, -13f, 6.5f, -0.2618f, 0f, 0.0873f)
            )

            val BagStrap2 = BagMain.addOrReplaceChild(
                "BagStrap2",
                CubeListBuilder.create().texOffs(82, 24).addBox(-3f, 0f, -15f, 3f, 10f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5f, 1f, 2f, 0.3491f, 0.3491f, 0.1396f)
            )

            val BagStrap1 = BagMain.addOrReplaceChild(
                "BagStrap1",
                CubeListBuilder.create().texOffs(103, 16).addBox(0f, 0f, -11f, 3f, 10f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.5f, 1f, 0.5f, 0.2618f, -0.1396f, -0.1745f)
            )

            val BagMain2 = BagMain.addOrReplaceChild(
                "BagMain2",
                CubeListBuilder.create().texOffs(36, 23).addBox(-7.5f, 0f, 0f, 15f, 9f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.5f, 11f, -0.5f, 0.6981f, 0f, -0.2618f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(0, 57).addBox(-6f, 0f, -3f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.5f, -8.5f, -0.5f, 0.2618f, 0f, 0.4363f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(0, 57).addBox(0f, 0f, -6f, 6f, 7f, 6f, CubeDeformation(0f)),
                PartPose.offset(-6f, 10f, 3f)
            )

            val PalmRight = ArmRight02.addOrReplaceChild(
                "PalmRight",
                CubeListBuilder.create().texOffs(0, 89).addBox(-2.5f, 0f, -2.5f, 5f, 4f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3f, 7f, -3f, 0f, 0.0253f, 0f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(0, 80).addBox(-3.5f, 0f, 0f, 7f, 5f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.5f, -9.5f, -3f, -0.7854f, 0.1222f, 0.0873f)
            )

            val BoobM = BoobR.addOrReplaceChild(
                "BoobM",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.2f, 4.5f, 0.3f, 0.7854f, 0f, -0.0873f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(106, 0).addBox(-8f, 4f, -5f, 16f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0.1745f, 0f, 0f)
            )

            val LegLeft = Butt.addOrReplaceChild(
                "LegLeft",
                CubeListBuilder.create().texOffs(0, 98).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 22f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.5f, 11f, -2f, -0.2269f, 0f, 0.0524f)
            )

            val LegRight = Butt.addOrReplaceChild(
                "LegRight",
                CubeListBuilder.create().texOffs(0, 98).addBox(-3.5f, 0f, -3.5f, 7f, 22f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.5f, 11f, -2f, -0.2269f, 0f, -0.0524f)
            )

            val Cloth = BodyMain.addOrReplaceChild(
                "Cloth",
                CubeListBuilder.create().texOffs(0, 0).addBox(-8f, 0f, -4.5f, 16f, 14f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, -8.5f, 0f)
            )

            val Cloth2 = Cloth.addOrReplaceChild(
                "Cloth2",
                CubeListBuilder.create().texOffs(50, 0).addBox(-8.5f, 0f, -5f, 17f, 12f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12f, 0f, -0.0524f, 0f, 0f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(21, 85).addBox(-7.5f, -1.5f, -7f, 15f, 5f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -11.5f, 0.5f, 0.2618f, 0f, 0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(39, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.5f, 0f, -0.1745f, 0f, 0f)
            )

            val Cap = Head.addOrReplaceChild(
                "Cap",
                CubeListBuilder.create().texOffs(204, 40).addBox(-8f, -17f, -2f, 16f, 17f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.6f, 2f, 0.2f, 0f, 0f)
            )

            val Ear01 = Head.addOrReplaceChild(
                "Ear01",
                CubeListBuilder.create().texOffs(136, 17).mirror()
                    .addBox(-1.5f, 0f, -6f, 3f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.5f, -14.5f, 5.7f, -0.6981f, 0.2618f, -0.1396f)
            )

            val Ear02 = Head.addOrReplaceChild(
                "Ear02",
                CubeListBuilder.create().texOffs(136, 17).addBox(-1.5f, 0f, -6f, 3f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.5f, -14.5f, 5.7f, -0.6981f, -0.2618f, 0.1396f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(24, 61).addBox(-7.5f, -8f, -8f, 15f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.3f, 0f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(28, 90).addBox(0f, -6f, -11f, 0f, 11f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, -4f, -0.1742f, 0.5236f, 0f)
            )

            val Hair01 = Head.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(186, 0).addBox(-7f, 0f, -12f, 14f, 9f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -9.5f, 9.5f, 0.1257f, 0f, 0f)
            )

            val HairU01 = Head.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(189, 19).addBox(-8f, -14.7f, 0f, 16f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -0.2f, -7.2f)
            )

            val Cap2 = Neck.addOrReplaceChild(
                "Cap2",
                CubeListBuilder.create().texOffs(206, 42).addBox(-8f, -15f, 0f, 16f, 15f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2f, -3f, -1.4f, 0f, 0f)
            )

            val TailBase = BodyMain.addOrReplaceChild(
                "TailBase",
                CubeListBuilder.create().texOffs(208, 103).addBox(-6f, -6f, 0f, 12f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7.5f, 0f, -0.2618f, 0f, 0f)
            )

            val Tail1 = TailBase.addOrReplaceChild(
                "Tail1",
                CubeListBuilder.create().texOffs(208, 103).addBox(-6f, -6f, 0f, 12f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 9f, 0.2618f, 0f, 0f)
            )

            val Tail2 = Tail1.addOrReplaceChild(
                "Tail2",
                CubeListBuilder.create().texOffs(208, 103).addBox(-6f, -6f, 0f, 12f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 9f, 0.5236f, 0f, 0f)
            )

            val Tail3 = Tail2.addOrReplaceChild(
                "Tail3",
                CubeListBuilder.create().texOffs(208, 103).addBox(-6f, -6f, 0f, 12f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 9f, 0.5236f, 0f, 0f)
            )

            val Tail4 = Tail3.addOrReplaceChild(
                "Tail4",
                CubeListBuilder.create().texOffs(208, 103).addBox(-6f, -6f, 0f, 12f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 9f, 0.2618f, 0f, 0f)
            )

            val Tail5 = Tail4.addOrReplaceChild(
                "Tail5",
                CubeListBuilder.create().texOffs(208, 103).addBox(-6f, -6f, 0f, 12f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 9f, -0.5236f, 0f, 0f)
            )

            val Tail6 = Tail5.addOrReplaceChild(
                "Tail6",
                CubeListBuilder.create().texOffs(208, 103).addBox(-5.5f, -6.5f, 0f, 11f, 13f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 9f, -0.5236f, 0f, 0f)
            )

            val TailHeadBase = Tail6.addOrReplaceChild(
                "TailHeadBase",
                CubeListBuilder.create().texOffs(157, 96),
                PartPose.offset(0f, 0f, 9f)
            )

            val TailHeadCL1 = TailHeadBase.addOrReplaceChild(
                "TailHeadCL1",
                CubeListBuilder.create().texOffs(207, 80).addBox(0f, 0f, 0f, 5f, 6f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, -6f, 5f, 0.0873f, 0.1745f, 0f)
            )

            val TailHeadCL2 = TailHeadCL1.addOrReplaceChild(
                "TailHeadCL2",
                CubeListBuilder.create().texOffs(207, 77).addBox(0f, 0f, 0f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, 0.5f, 7f, 0.0873f, 0.1745f, 0f)
            )

            val TailHeadCL3 = TailHeadCL1.addOrReplaceChild(
                "TailHeadCL3",
                CubeListBuilder.create().texOffs(207, 77).addBox(0f, 0f, 0f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, 3.5f, 7f, -0.0524f, 0.1745f, 0f)
            )

            val TailHeadCR1 = TailHeadBase.addOrReplaceChild(
                "TailHeadCR1",
                CubeListBuilder.create().texOffs(207, 80).addBox(-5f, 0f, 0f, 5f, 6f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, -6f, 5f, 0.0873f, -0.1745f, 0f)
            )

            val TailHeadCR2 = TailHeadCR1.addOrReplaceChild(
                "TailHeadCR2",
                CubeListBuilder.create().texOffs(207, 77).addBox(-2f, 0f, 0f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, 0.5f, 7f, 0.0873f, -0.1745f, 0f)
            )

            val TailHeadCR3 = TailHeadCR1.addOrReplaceChild(
                "TailHeadCR3",
                CubeListBuilder.create().texOffs(207, 77).addBox(-2f, 0f, 0f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, 3.5f, 7f, -0.0524f, -0.1745f, 0f)
            )

            val TailHead1 = TailHeadBase.addOrReplaceChild(
                "TailHead1",
                CubeListBuilder.create().texOffs(191, 70).addBox(-5.5f, 0f, -0.5f, 11f, 8f, 17f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -8.5f, 4f, 0.1396f, 0f, 0f)
            )

            val TailHeadC1 = TailHead1.addOrReplaceChild(
                "TailHeadC1",
                CubeListBuilder.create().texOffs(201, 78).addBox(-4.5f, 0f, 0f, 9f, 5f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3.5f, 0f, 0.3491f, 0f, 0f)
            )

            val TailHeadC2 = TailHeadC1.addOrReplaceChild(
                "TailHeadC2",
                CubeListBuilder.create().texOffs(207, 77).addBox(-1f, 0f, 0f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1f, 8.5f, 0.1396f, 0f, 0f)
            )

            val TailHeadC3 = TailHeadC1.addOrReplaceChild(
                "TailHeadC3",
                CubeListBuilder.create().texOffs(207, 77).addBox(-1f, 0f, 0f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.8f, 1f, 8.5f, 0.1396f, -0.0524f, 0f)
            )

            val TailHeadC4 = TailHeadC1.addOrReplaceChild(
                "TailHeadC4",
                CubeListBuilder.create().texOffs(207, 77).addBox(-1f, 0f, 0f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.8f, 1f, 8.5f, 0.1396f, 0.0524f, 0f)
            )

            val TailHead3 = TailHead1.addOrReplaceChild(
                "TailHead3",
                CubeListBuilder.create().texOffs(200, 80).addBox(-6f, 0f, 0f, 12f, 4f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 14.5f, 0.5236f, 0f, 0f)
            )

            val TailHead2 = TailHead1.addOrReplaceChild(
                "TailHead2",
                CubeListBuilder.create().texOffs(182, 68).addBox(-9f, 0f, 0f, 18f, 8f, 19f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1.5f, 4.5f, 0.1396f, 0f, 0f)
            )

            val TailJaw1 = TailHeadBase.addOrReplaceChild(
                "TailJaw1",
                CubeListBuilder.create().texOffs(194, 106).addBox(-6.5f, 0f, 0f, 13f, 5f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3f, 5f, -0.1745f, 0f, 0f)
            )

            val TailJaw2 = TailJaw1.addOrReplaceChild(
                "TailJaw2",
                CubeListBuilder.create().texOffs(197, 77).addBox(-5f, 0f, 0f, 10f, 5f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, 8f, -0.1745f, 0f, 0f)
            )

            val TailJaw3 = TailJaw1.addOrReplaceChild(
                "TailJaw3",
                CubeListBuilder.create().texOffs(207, 80).addBox(-2.5f, -2.5f, 0f, 5f, 5f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 15.5f, -0.1004f, 0f, 0f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -8f, 0f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -11.5f, 0.5f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -0.5f, 0f)
            )

            addFaceLayer(GlowHead)

            val GlowTailBase = GlowBodyMain.addOrReplaceChild(
                "GlowTailBase",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 7.5f, 0f)
            )

            val GlowTail1 = GlowTailBase.addOrReplaceChild(
                "GlowTail1",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 9f)
            )

            val GlowTail2 = GlowTail1.addOrReplaceChild(
                "GlowTail2",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 9f)
            )

            val GlowTail3 = GlowTail2.addOrReplaceChild(
                "GlowTail3",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 9f)
            )

            val GlowTail4 = GlowTail3.addOrReplaceChild(
                "GlowTail4",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 9f)
            )

            val GlowTail5 = GlowTail4.addOrReplaceChild(
                "GlowTail5",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 9f)
            )

            val GlowTail6 = GlowTail5.addOrReplaceChild(
                "GlowTail6",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 9f)
            )

            val TailBack6 = GlowTail6.addOrReplaceChild(
                "TailBack6",
                CubeListBuilder.create().texOffs(163, 70).addBox(-3.5f, 0f, 0f, 7f, 2f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7.5f, 0f, 0.1745f, 0f, 0f)
            )

            val GlowTailHeadBase = GlowTail6.addOrReplaceChild(
                "GlowTailHeadBase",
                CubeListBuilder.create().texOffs(157, 96).addBox(-5f, -7f, 0f, 10f, 14f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 9f)
            )

            val GlowTailHead1 = GlowTailHeadBase.addOrReplaceChild(
                "GlowTailHead1",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -8.5f, 4f)
            )

            val TailHeadT01 = GlowTailHead1.addOrReplaceChild(
                "TailHeadT01",
                CubeListBuilder.create().texOffs(141, 29).addBox(-6f, 0f, 0f, 12f, 5f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4.5f, 4.5f, -0.1745f, 0f, 0f)
            )

            val GlowTailJaw1 = GlowTailHeadBase.addOrReplaceChild(
                "GlowTailJaw1",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 3f, 5f)
            )

            val TailJawT01 = GlowTailJaw1.addOrReplaceChild(
                "TailJawT01",
                CubeListBuilder.create().texOffs(143, 46).addBox(-5.5f, 0f, 0f, 11f, 5f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3f, 4f, 0.1745f, 0f, 0f)
            )

            val TailBack5 = GlowTail5.addOrReplaceChild(
                "TailBack5",
                CubeListBuilder.create().texOffs(163, 70).addBox(-3.5f, 0f, 0f, 7f, 2f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 0f, 0.1745f, 0f, 0f)
            )

            val TailBack4 = GlowTail4.addOrReplaceChild(
                "TailBack4",
                CubeListBuilder.create().texOffs(163, 70).addBox(-3.5f, 0f, 0f, 7f, 2f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 0f, 0.1745f, 0f, 0f)
            )

            val TailBack3 = GlowTail3.addOrReplaceChild(
                "TailBack3",
                CubeListBuilder.create().texOffs(163, 70).addBox(-3.5f, 0f, 0f, 7f, 2f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 0f, 0.1745f, 0f, 0f)
            )

            val TailBack2 = GlowTail2.addOrReplaceChild(
                "TailBack2",
                CubeListBuilder.create().texOffs(163, 70).addBox(-3.5f, 0f, 0f, 7f, 2f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 0f, 0.1745f, 0f, 0f)
            )

            val TailBack1 = GlowTail1.addOrReplaceChild(
                "TailBack1",
                CubeListBuilder.create().texOffs(163, 70).addBox(-3.5f, 0f, 0f, 7f, 2f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 0f, 0.1745f, 0f, 0f)
            )

            val TailBack0 = GlowTailBase.addOrReplaceChild(
                "TailBack0",
                CubeListBuilder.create().texOffs(163, 70).addBox(-3.5f, 0f, 0f, 7f, 2f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 0f, 0.1745f, 0f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 256, 128)
        }
    }
}
