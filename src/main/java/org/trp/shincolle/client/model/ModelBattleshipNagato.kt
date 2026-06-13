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
import org.trp.shincolle.entity.EntityBattleshipNagato
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelBattleshipNagato<T : EntityShipBase>(root: ModelPart) : ShincolleShipModel<T>() {
    private val BodyMain: ModelPart
    private val Neck: ModelPart?
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val Butt: ModelPart
    private val Cloth: ModelPart
    private val Head: ModelPart?
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val HeadEquip: ModelPart?
    private val HeadEquip05: ModelPart?
    private val Ahoke: ModelPart
    private val HairMidL01: ModelPart
    private val HairMidL02: ModelPart
    private val HeadEquip01: ModelPart?
    private val HeadEquip03: ModelPart?
    private val HeadEquip02: ModelPart?
    private val HeadEquip04: ModelPart?
    private val ArmLeft02: ModelPart?
    private val ArmRight02: ModelPart?
    private val LegRight: ModelPart
    private val LegLeft: ModelPart
    private val Skirt: ModelPart
    private val ShoesR: ModelPart
    private val ShoesL: ModelPart
    private val SkirtEquip: ModelPart
    private val EquipBase: ModelPart?
    private val EquipL01: ModelPart
    private val EquipR01: ModelPart
    private val EquipBaseM01: ModelPart
    private val EquipBaseM02: ModelPart
    private val EquipBaseM03: ModelPart
    private val EquipL02: ModelPart
    private val EquipL03: ModelPart
    private val EquipR04: ModelPart
    private val EquipLCBase01: ModelPart
    private val EquipLC2Base01: ModelPart
    private val EquipLC2Base02: ModelPart
    private val EquipLC201: ModelPart
    private val EquipLC203: ModelPart
    private val EquipLC202: ModelPart
    private val EquipLC204: ModelPart
    private val EquipLCBase02: ModelPart
    private val EquipLC01: ModelPart
    private val EquipLC03: ModelPart
    private val EquipLCRadar: ModelPart
    private val EquipLC02: ModelPart
    private val EquipLC04: ModelPart
    private val EquipR02: ModelPart
    private val EquipR03: ModelPart
    private val EquipRCBase01: ModelPart
    private val EquipR04_1: ModelPart
    private val EquipRCBase02: ModelPart
    private val EquipRC01: ModelPart
    private val EquipRC03: ModelPart
    private val EquipRCRadar: ModelPart
    private val EquipRC02: ModelPart
    private val EquipRC04: ModelPart
    private val EquipRC2Base01: ModelPart
    private val EquipRC2Base02: ModelPart
    private val EquipRC201: ModelPart
    private val EquipRC203: ModelPart
    private val EquipRC202: ModelPart
    private val EquipRC204: ModelPart
    private val GlowBodyMain: ModelPart?
    private val GlowNeck: ModelPart?
    private val GlowHead: ModelPart?

    protected override val bodyMain: ModelPart get() = BodyMain
    protected override val neck: ModelPart? get() = Neck
    protected override val head: ModelPart? get() = Head
    protected override val glowBodyMain: ModelPart? get() = GlowBodyMain
    protected override val glowNeck: ModelPart? get() = GlowNeck
    protected override val glowHead: ModelPart? get() = GlowHead

    private val armLeft02DefaultX: Float
    private val armLeft02DefaultY: Float
    private val armLeft02DefaultZ: Float
    private val armRight02DefaultX: Float
    private val armRight02DefaultY: Float
    private val armRight02DefaultZ: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Cloth = this.BodyMain.getChild("Cloth")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairMain = this.Head.getChild("HairMain")
        this.HairMidL01 = this.HairMain.getChild("HairMidL01")
        this.HairMidL02 = this.HairMidL01.getChild("HairMidL02")
        this.HeadEquip05 = this.Head.getChild("HeadEquip05")
        this.HeadEquip = this.Head.getChild("HeadEquip")
        this.HeadEquip03 = this.HeadEquip.getChild("HeadEquip03")
        this.HeadEquip04 = this.HeadEquip03.getChild("HeadEquip04")
        this.HeadEquip01 = this.HeadEquip.getChild("HeadEquip01")
        this.HeadEquip02 = this.HeadEquip01.getChild("HeadEquip02")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegRight = this.Butt.getChild("LegRight")
        this.ShoesR = this.LegRight.getChild("ShoesR")
        this.Skirt = this.Butt.getChild("Skirt")
        this.SkirtEquip = this.Skirt.getChild("SkirtEquip")
        this.EquipBase = this.SkirtEquip.getChild("EquipBase")
        this.EquipBaseM01 = this.EquipBase.getChild("EquipBaseM01")
        this.EquipBaseM03 = this.EquipBase.getChild("EquipBaseM03")
        this.EquipBaseM02 = this.EquipBase.getChild("EquipBaseM02")
        this.EquipL01 = this.EquipBase.getChild("EquipL01")
        this.EquipL02 = this.EquipL01.getChild("EquipL02")
        this.EquipL03 = this.EquipL02.getChild("EquipL03")
        this.EquipR04 = this.EquipL03.getChild("EquipR04")
        this.EquipLC2Base01 = this.EquipR04.getChild("EquipLC2Base01")
        this.EquipLC2Base02 = this.EquipLC2Base01.getChild("EquipLC2Base02")
        this.EquipLC201 = this.EquipLC2Base02.getChild("EquipLC201")
        this.EquipLC202 = this.EquipLC201.getChild("EquipLC202")
        this.EquipLC203 = this.EquipLC2Base02.getChild("EquipLC203")
        this.EquipLC204 = this.EquipLC203.getChild("EquipLC204")
        this.EquipLCBase01 = this.EquipL03.getChild("EquipLCBase01")
        this.EquipLCBase02 = this.EquipLCBase01.getChild("EquipLCBase02")
        this.EquipLC01 = this.EquipLCBase02.getChild("EquipLC01")
        this.EquipLC02 = this.EquipLC01.getChild("EquipLC02")
        this.EquipLCRadar = this.EquipLCBase02.getChild("EquipLCRadar")
        this.EquipLC03 = this.EquipLCBase02.getChild("EquipLC03")
        this.EquipLC04 = this.EquipLC03.getChild("EquipLC04")
        this.EquipR01 = this.EquipBase.getChild("EquipR01")
        this.EquipR02 = this.EquipR01.getChild("EquipR02")
        this.EquipR03 = this.EquipR02.getChild("EquipR03")
        this.EquipRCBase01 = this.EquipR03.getChild("EquipRCBase01")
        this.EquipRCBase02 = this.EquipRCBase01.getChild("EquipRCBase02")
        this.EquipRC03 = this.EquipRCBase02.getChild("EquipRC03")
        this.EquipRC04 = this.EquipRC03.getChild("EquipRC04")
        this.EquipRC01 = this.EquipRCBase02.getChild("EquipRC01")
        this.EquipRC02 = this.EquipRC01.getChild("EquipRC02")
        this.EquipRCRadar = this.EquipRCBase02.getChild("EquipRCRadar")
        this.EquipR04_1 = this.EquipR03.getChild("EquipR04_1")
        this.EquipRC2Base01 = this.EquipR04_1.getChild("EquipRC2Base01")
        this.EquipRC2Base02 = this.EquipRC2Base01.getChild("EquipRC2Base02")
        this.EquipRC203 = this.EquipRC2Base02.getChild("EquipRC203")
        this.EquipRC204 = this.EquipRC203.getChild("EquipRC204")
        this.EquipRC201 = this.EquipRC2Base02.getChild("EquipRC201")
        this.EquipRC202 = this.EquipRC201.getChild("EquipRC202")
        this.LegLeft = this.Butt.getChild("LegLeft")
        this.ShoesL = this.LegLeft.getChild("ShoesL")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        initFaceParts(this.GlowHead)
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
        setFlushVisible(
            entity != null && (entity.emotionPrimary == EntityShipBase.EMOTION_SHY
                    || entity.emotionPrimary == EntityShipBase.EMOTION_HAPPY)
        )
        applyEquipVisibility(entity)

        if (isDeadPose(entity)) {
            this.applyDeadPose()
            this.syncGlowParts()
            return
        }

        applyHeadRotation(Head, entity, ageInTicks, netHeadYaw, headPitch)

        applyBasePose(ctx, ageInTicks, headPitch, limbSwing, limbSwingAmount)
        applySpecialPoseAdjustments(entity, ctx, ageInTicks, limbSwingAmount)

        syncGlowParts()
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
        val showHead = entity.getEquipFlag(EntityBattleshipNagato.EQUIP_HEAD)
        val showCannon = entity.getEquipFlag(EntityBattleshipNagato.EQUIP_CANNON)
        if (HeadEquip != null) HeadEquip.visible = showHead
        if (HeadEquip01 != null) HeadEquip01.visible = showHead
        if (HeadEquip02 != null) HeadEquip02.visible = showHead
        if (HeadEquip03 != null) HeadEquip03.visible = showHead
        if (HeadEquip04 != null) HeadEquip04.visible = showHead
        if (HeadEquip05 != null) HeadEquip05.visible = showHead
        if (EquipBase != null) EquipBase.visible = showCannon
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        this.Head!!.xRot = 0.0f
        this.Head.yRot = 0.0f
        this.BoobL.xRot = -0.7854f
        this.BoobR.xRot = -0.7854f
        this.Ahoke.yRot = 0.5236f
        this.ArmLeft01.yRot = 0.0f
        this.ArmRight01.yRot = 0.0f
        this.BodyMain.xRot = 1.48f
        this.HairMidL01.xRot = 0.2f
        this.HairMidL02.xRot = -0.3f
        this.ArmLeft01.xRot = -2.97f
        this.ArmLeft01.zRot = 0.26f
        this.ArmRight01.xRot = -2.8f
        this.ArmRight01.zRot = -1.3f
        this.ArmRight02!!.zRot = -0.9f
        this.LegLeft.xRot = -0.26f
        this.LegRight.xRot = -0.26f
        this.LegLeft.yRot = 0.0f
        this.LegRight.yRot = 0.0f
        this.LegLeft.zRot = -0.14f
        this.LegRight.zRot = 0.14f
        this.EquipBase!!.visible = false
    }

    private fun applyBasePose(
        ctx: PoseContext?,
        ageInTicks: Float,
        headPitch: Float,
        limbSwing: Float,
        limbSwingAmount: Float
    ) {
        var f6: Float
        val angleX = Mth.cos(ageInTicks * 0.08f)
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount

        this.BoobL.xRot = angleX * 0.06f - 0.7854f
        this.BoobR.xRot = angleX * 0.06f - 0.7854f
        this.Ahoke.yRot = angleX * 0.25f + 0.5236f
        this.BodyMain.xRot = -0.1f
        this.HairMidL01.xRot = angleX * 0.06f + 0.2f
        this.HairMidL02.xRot = -angleX1 * 0.09f - 0.17f
        this.HairMidL01.zRot = 0.0f
        this.HairMidL02.zRot = 0.0f
        this.ArmLeft01.xRot = angleAdd2 * 0.6f + 0.15f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = angleX * 0.1f - 0.26f
        this.ArmRight01.xRot = angleAdd1 * 0.6f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = -angleX * 0.1f + 0.26f
        this.ArmRight02!!.xRot = 0.0f
        this.ArmRight02.yRot = 0.0f
        this.ArmRight02.zRot = 0.0f
        this.LegLeft.yRot = 0.0f
        this.LegLeft.zRot = 0.05f
        this.LegRight.yRot = 0.0f
        this.LegRight.zRot = -0.05f
    }

    private fun applySpecialPoseAdjustments(entity: T?, ctx: PoseContext, ageInTicks: Float, limbSwingAmount: Float) {
        val angleX = ctx.angleX
        var legAddLeft = ctx.angleAdd1 * 0.5f - 0.28f
        var legAddRight = ctx.angleAdd2 * 0.5f - 0.21f
        val showCannon = entity != null && entity.getEquipFlag(EntityBattleshipNagato.EQUIP_CANNON)

        if (entity != null && entity.shipDepth > 0.0) {
            this.poseTranslateY += ctx.angleX * 0.05f + 0.025f
        }

        val isCrouching = entity != null && entity.isCrouching
        val isSitting =
            ctx.isSitting || (entity != null && entity.isPassenger && (entity.vehicle !is EntityMountBase))
        val isSprinting = entity != null && entity.isSprinting || limbSwingAmount > 0.9f

        if (showCannon) {
            this.EquipBase!!.xRot = 0.17f
            if (this.Head!!.xRot <= 0.0f) {
                this.EquipLC201.xRot = this.Head.xRot * 0.9f
                this.EquipLC203.xRot = this.Head.xRot * 1.2f
                this.EquipRC201.xRot = this.Head.xRot * 1.1f
                this.EquipRC203.xRot = this.Head.xRot * 0.85f
            }
            this.EquipLCBase02.xRot = this.Head.xRot
            this.EquipLC2Base01.xRot = 0.0f
            this.EquipLC2Base02.yRot = this.Head.yRot
            this.EquipLC01.yRot = angleX * 0.1f - 0.26f
            this.EquipLC03.yRot = -angleX * 0.08f - 0.15f
            this.EquipRCBase02.xRot = this.Head.xRot
            this.EquipRC2Base01.xRot = 0.0f
            this.EquipRC2Base02.yRot = this.Head.yRot
            this.EquipRC01.yRot = angleX * 0.08f + 0.2f
            this.EquipRC03.yRot = -angleX * 0.1f + 0.1f
        }

        if (isSprinting) {
            Head!!.xRot -= 0.35f
            BodyMain.xRot = 0.5236f
            HairMidL01.xRot += 0.3f
            HairMidL02.xRot += 0.3f

            ArmLeft01.xRot = ctx.angleAdd2 * 1.4f - 0.1f
            ArmRight01.xRot = ctx.angleAdd1 * 1.4f - 0.1f
            ArmLeft01.zRot = ctx.angleX * 0.1f - 0.4f
            ArmRight01.zRot = -ctx.angleX * 0.1f + 0.4f

            legAddLeft -= 0.55f
            legAddRight -= 0.55f
            LegLeft.yRot = 0.0f
            LegRight.yRot = 0.0f
            LegLeft.zRot = 0.0f
            LegRight.zRot = 0.0f
            if (showCannon) {
                EquipLCBase02.xRot -= 0.45f
                EquipRCBase02.xRot -= 0.5f
            }
        }

        if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            Head!!.xRot -= 0.35f
            BodyMain.xRot = 0.5236f
            ArmLeft01.zRot = ctx.angleX * 0.1f - 0.4f
            ArmRight01.zRot = -ctx.angleX * 0.1f + 0.4f

            legAddLeft -= 0.55f
            legAddRight -= 0.55f
            LegLeft.yRot = 0.0f
            LegRight.yRot = 0.0f
            LegLeft.zRot = 0.0f
            LegRight.zRot = 0.0f
            if (showCannon) {
                EquipLCBase02.xRot -= 0.45f
                EquipRCBase02.xRot -= 0.5f
            }
        }

        if (isSitting) {
            this.isSittingPose = true
            if (showCannon) {
                this.poseTranslateY += 0.42f
                BodyMain.xRot = -0.09f
                ArmLeft01.xRot = 0.52f
                ArmLeft01.zRot = -1.04f
                ArmRight01.xRot = 0.52f
                ArmRight01.zRot = 1.04f
                legAddLeft = -1.4f
                legAddRight = -1.4f
                LegLeft.yRot = -0.14f
                LegRight.yRot = 0.14f
                LegLeft.zRot = 0.0f
                LegRight.zRot = 0.0f
                EquipLCBase02.xRot = 1.57f
                EquipLC2Base01.xRot = 0.8f
                EquipLC01.yRot = 0.0f
                EquipLC03.yRot = 0.0f
                EquipLC201.xRot = 0.0f
                EquipLC203.xRot = 0.0f
                EquipRCBase02.xRot = 1.57f
                EquipRC2Base01.xRot = 0.8f
                EquipRC01.yRot = 0.0f
                EquipRC03.yRot = 0.0f
                EquipRC201.xRot = 0.0f
                EquipRC203.xRot = 0.0f
            } else if (entity != null && hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY += (0.71f * 2.7).toFloat()
                BodyMain.xRot = 1.48f
                HairMidL01.xRot = 0.2f
                HairMidL02.xRot = -0.3f
                ArmLeft01.xRot = -2.97f
                ArmLeft01.zRot = 0.26f
                ArmRight01.xRot = -2.8f
                ArmRight01.zRot = -1.3f
                ArmRight02!!.zRot = -0.9f
                legAddLeft = -0.26f
                legAddRight = -0.26f
                LegLeft.yRot = 0.0f
                LegRight.yRot = 0.0f
                LegLeft.zRot = -0.14f
                LegRight.zRot = 0.14f
            } else {
                this.poseTranslateY += SITTING_TRANSLATE_Y
                BodyMain.xRot = -0.09f
                ArmLeft01.xRot = -0.63f
                ArmLeft01.zRot = 0.14f
                ArmRight01.xRot = -0.63f
                ArmRight01.zRot = -0.14f
                legAddLeft = -1.75f
                legAddRight = -1.75f
                LegLeft.yRot = -0.14f
                LegRight.yRot = 0.14f
                LegLeft.zRot = 0.0f
                LegRight.zRot = 0.0f
            }
        }

        if (entity != null && entity.attackTick > 20) {
            if (entity.getStateEmotion(5) == 0 || entity.getStateEmotion(5) == 2) {
                this.poseTranslateY += 0.35f
                Head!!.xRot -= 1.22f
                BodyMain.xRot = 1.75f
                HairMidL01.xRot += 0.3f
                HairMidL02.xRot += 0.6f
                ArmLeft01.xRot = -1.75f
                ArmLeft01.yRot = 0.0f
                ArmLeft01.zRot = 0.0f
                ArmRight01.xRot = -1.05f
                ArmRight01.yRot = 2.62f
                ArmRight01.zRot = 0.7f
                ArmRight02!!.zRot = -0.79f
                legAddLeft = -1.75f
                legAddRight = -2.27f
                LegLeft.yRot = -0.44f
                LegRight.yRot = 0.44f
                LegLeft.zRot = 0.0f
                LegRight.zRot = 0.0f
                EquipBase!!.xRot = -1.22f
                EquipLCBase02.xRot -= 0.5f
                EquipRCBase02.xRot -= 0.5f
            } else {
                BodyMain.xRot = -0.17f
                ArmLeft01.xRot = -1.57f
                ArmLeft01.yRot = -0.26f
                ArmLeft01.zRot = 0.0f
                ArmRight01.xRot = 0.0f
                ArmRight01.zRot = 0.87f
                ArmRight02!!.zRot = -1.57f
                legAddLeft += 0.2618f
                legAddRight += 0.2618f
                LegLeft.yRot = 0.0f
                LegRight.yRot = 0.0f
                LegLeft.zRot = -0.17f
                LegRight.zRot = 0.17f
            }
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

        LegLeft.xRot = legAddLeft
        LegRight.xRot = legAddRight
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "battleship_nagato"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelBattleshipNagato")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelBattleshipNagato")
        private val SITTING_TRANSLATE_Y = sittingY("ModelBattleshipNagato")

        private val BODY_BASE_X_ROT = -0.1047f
        private const val BUTT_BASE_X_ROT = 0.1745f
        private val SKIRT_BASE_X_ROT = -0.1745f
        private const val LEG_BASE_Z_ROT = 0.0873f

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 105).addBox(-6.5f, -10f, -4f, 13f, 15f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, -14f, 0f)
            )

            val Cloth = BodyMain.addOrReplaceChild(
                "Cloth",
                CubeListBuilder.create().texOffs(96, 16).addBox(-5.5f, 0f, 0f, 11f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.5f, -5f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(46, 14).addBox(-7f, -0.5f, -4.5f, 14f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -10f, 0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 75).addBox(-8f, -8f, -7.2f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(33, 87).addBox(0f, -3f, -10f, 0f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, -10f, -5f, 0f, 0.5236f, 0f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(48, 56).addBox(-7.5f, 0f, 0f, 15f, 9f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -15f, -3f)
            )

            val HairMidL01 = HairMain.addOrReplaceChild(
                "HairMidL01",
                CubeListBuilder.create().texOffs(48, 34).addBox(-7.5f, 0f, 0f, 15f, 13f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1.5f, 0.3491f, 0f, 0f)
            )

            val HairMidL02 = HairMidL01.addOrReplaceChild(
                "HairMidL02",
                CubeListBuilder.create().texOffs(0, 32).addBox(-7f, 0f, 0f, 14f, 14f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1.8f, -0.1745f, 0f, 0f)
            )

            val HeadEquip05 = Head.addOrReplaceChild(
                "HeadEquip05",
                CubeListBuilder.create().texOffs(128, 0).addBox(-16f, 0f, 0f, 32f, 1f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.5f, -1f)
            )

            val HeadEquip = Head.addOrReplaceChild(
                "HeadEquip",
                CubeListBuilder.create().texOffs(128, 0).addBox(-9.5f, 0f, 0f, 19f, 4f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -8f, -1f, 0.1047f, 0f, 0f)
            )

            val HeadEquip03 = HeadEquip.addOrReplaceChild(
                "HeadEquip03",
                CubeListBuilder.create().texOffs(128, 0).addBox(-4f, 0f, -2f, 4f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.5f, 0f, 9f, 0f, 0.7854f, 0.1745f)
            )

            val HeadEquip04 = HeadEquip03.addOrReplaceChild(
                "HeadEquip04",
                CubeListBuilder.create().texOffs(92, 30).addBox(-10f, -1f, -1f, 10f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, 1f, 0f, 0f, 0f, 0.0873f)
            )

            val HeadEquip01 = HeadEquip.addOrReplaceChild(
                "HeadEquip01",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, -2f, 4f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.5f, 0f, 9f, 0f, -0.7854f, -0.1745f)
            )

            val HeadEquip02 = HeadEquip01.addOrReplaceChild(
                "HeadEquip02",
                CubeListBuilder.create().texOffs(92, 30).mirror()
                    .addBox(0f, -1f, -1f, 10f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, 1f, 0f, 0f, 0f, -0.0873f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(0, 70).addBox(-3.5f, 0f, 0f, 7f, 5f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.7f, -9f, -3.5f, -0.7854f, -0.1396f, -0.0873f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(0, 70).mirror().addBox(-3.5f, 0f, 0f, 7f, 5f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.7f, -9f, -3.5f, -0.7854f, 0.1396f, 0.0873f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(24, 53).mirror()
                    .addBox(-2.5f, 0f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8.5f, -10f, 0f, 0f, 0f, -0.1571f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(0, 53).mirror()
                    .addBox(-2.5f, 0f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 10f, 0f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(24, 53).addBox(-2.5f, 0f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8.5f, -10f, 0f, 0f, 0f, 0.1571f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(0, 53).addBox(-2.5f, 0f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 10f, 0f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 17).addBox(-8f, 4f, -5.5f, 16f, 8f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0.2618f, 0f, 0f)
            )

            val LegRight = Butt.addOrReplaceChild(
                "LegRight",
                CubeListBuilder.create().texOffs(0, 80).addBox(-3f, 0f, -3f, 6f, 19f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.5f, 9.5f, -3f, -0.2618f, 0f, -0.0524f)
            )

            val ShoesR = LegRight.addOrReplaceChild(
                "ShoesR",
                CubeListBuilder.create().texOffs(22, 70).addBox(-3.5f, 0f, -3.5f, 7f, 9f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 19f, -0.2f)
            )

            val Skirt = Butt.addOrReplaceChild(
                "Skirt",
                CubeListBuilder.create().texOffs(0, 0).addBox(-8.5f, 0f, -4.5f, 17f, 6f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, -2f, -0.1367f, 0f, 0f)
            )

            val SkirtEquip = Skirt.addOrReplaceChild(
                "SkirtEquip",
                CubeListBuilder.create().texOffs(71, 0).addBox(-9f, 0f, -5f, 18f, 3f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -3f, 0.2f)
            )

            val EquipBase = SkirtEquip.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(128, 0).addBox(-2.5f, 0f, 0f, 5f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 4f, 0.1745f, 0f, 0f)
            )

            val EquipBaseM01 = EquipBase.addOrReplaceChild(
                "EquipBaseM01",
                CubeListBuilder.create().texOffs(128, 0).addBox(-1f, 0f, 0f, 2f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 12f, -0.6981f, 0f, 0f)
            )

            val EquipBaseM03 = EquipBase.addOrReplaceChild(
                "EquipBaseM03",
                CubeListBuilder.create().texOffs(128, 92).addBox(-3f, -14f, 0f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 9f, -0.4363f, 0f, 0f)
            )

            val EquipBaseM02 = EquipBase.addOrReplaceChild(
                "EquipBaseM02",
                CubeListBuilder.create().texOffs(128, 0).addBox(-3.5f, 0f, 0f, 7f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, -1.5f, 11f)
            )

            val EquipL01 = EquipBase.addOrReplaceChild(
                "EquipL01",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 14f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3f, 8f, -0.2094f, 0f, 0f)
            )

            val EquipL02 = EquipL01.addOrReplaceChild(
                "EquipL02",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 10f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(11.5f, 0f, 0.6f, 0f, 0.5236f, 0f)
            )

            val EquipL03 = EquipL02.addOrReplaceChild(
                "EquipL03",
                CubeListBuilder.create().texOffs(128, 26).addBox(0f, 0f, -14f, 6f, 18f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.3f, 0f, 1.3f, 0f, -0.6981f, 0f)
            )

            val EquipR04 = EquipL03.addOrReplaceChild(
                "EquipR04",
                CubeListBuilder.create().texOffs(128, 60).addBox(0f, 0f, -10f, 6f, 7f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 11f, -13f, 0f, 0.1745f, 0f)
            )

            val EquipLC2Base01 = EquipR04.addOrReplaceChild(
                "EquipLC2Base01",
                CubeListBuilder.create().texOffs(128, 0).addBox(-4.5f, 0f, -10f, 9f, 10f, 10f, CubeDeformation(0f)),
                PartPose.offset(3f, -1f, -10f)
            )

            val EquipLC2Base02 = EquipLC2Base01.addOrReplaceChild(
                "EquipLC2Base02",
                CubeListBuilder.create().texOffs(128, 0).addBox(-5f, -5f, -10f, 10f, 5f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -1f, 0.0524f, 0f, 0f)
            )

            val EquipLC201 = EquipLC2Base02.addOrReplaceChild(
                "EquipLC201",
                CubeListBuilder.create().texOffs(128, 117).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, -4f, -8f, -0.1396f, 0f, 0f)
            )

            val EquipLC202 = EquipLC201.addOrReplaceChild(
                "EquipLC202",
                CubeListBuilder.create().texOffs(132, 113).addBox(-1f, -1f, -13f, 2f, 2f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val EquipLC203 = EquipLC2Base02.addOrReplaceChild(
                "EquipLC203",
                CubeListBuilder.create().texOffs(128, 117).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -4f, -8f, -0.0873f, 0f, 0f)
            )

            val EquipLC204 = EquipLC203.addOrReplaceChild(
                "EquipLC204",
                CubeListBuilder.create().texOffs(132, 113).addBox(-1f, -1f, -13f, 2f, 2f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val EquipLCBase01 = EquipL03.addOrReplaceChild(
                "EquipLCBase01",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, -5.5f, -10f, 7f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1f, -7f, 0f, 0.0873f, 0f)
            )

            val EquipLCBase02 = EquipLCBase01.addOrReplaceChild(
                "EquipLCBase02",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, -5f, -4f, 5f, 10f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, 0f, -6.5f, -0.1745f, 0.0524f, 0f)
            )

            val EquipLC01 = EquipLCBase02.addOrReplaceChild(
                "EquipLC01",
                CubeListBuilder.create().texOffs(128, 117).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, -2f, -4f, 0f, -0.2618f, 0f)
            )

            val EquipLC02 = EquipLC01.addOrReplaceChild(
                "EquipLC02",
                CubeListBuilder.create().texOffs(132, 113).addBox(-1f, -1f, -13f, 2f, 2f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val EquipLCRadar = EquipLCBase02.addOrReplaceChild(
                "EquipLCRadar",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, -7.5f, 0f, 1f, 15f, 5f, CubeDeformation(0f)),
                PartPose.offset(5.2f, 0f, 5.5f)
            )

            val EquipLC03 = EquipLCBase02.addOrReplaceChild(
                "EquipLC03",
                CubeListBuilder.create().texOffs(128, 117).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, 2f, -4f, 0f, -0.1396f, 0f)
            )

            val EquipLC04 = EquipLC03.addOrReplaceChild(
                "EquipLC04",
                CubeListBuilder.create().texOffs(132, 113).addBox(-1f, -1f, -13f, 2f, 2f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val EquipR01 = EquipBase.addOrReplaceChild(
                "EquipR01",
                CubeListBuilder.create().texOffs(128, 0).addBox(-14f, 0f, 0f, 14f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3f, 8f, -0.2094f, 0f, 0f)
            )

            val EquipR02 = EquipR01.addOrReplaceChild(
                "EquipR02",
                CubeListBuilder.create().texOffs(128, 0).addBox(-10f, 0f, 0f, 10f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-11.5f, 0f, 0.6f, 0f, -0.5236f, 0f)
            )

            val EquipR03 = EquipR02.addOrReplaceChild(
                "EquipR03",
                CubeListBuilder.create().texOffs(128, 26).addBox(-6f, 0f, -14f, 6f, 18f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5.3f, 0f, 1.3f, 0f, 0.6981f, 0f)
            )

            val EquipRCBase01 = EquipR03.addOrReplaceChild(
                "EquipRCBase01",
                CubeListBuilder.create().texOffs(128, 0).addBox(-7f, -5.5f, -10f, 7f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1f, -7f, 0f, -0.0873f, 0f)
            )

            val EquipRCBase02 = EquipRCBase01.addOrReplaceChild(
                "EquipRCBase02",
                CubeListBuilder.create().texOffs(128, 0).addBox(-5f, -5f, -4f, 5f, 10f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7f, 0f, -6.5f, -0.1745f, -0.0524f, 0f)
            )

            val EquipRC03 = EquipRCBase02.addOrReplaceChild(
                "EquipRC03",
                CubeListBuilder.create().texOffs(128, 117).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, 2f, -4f, 0f, 0.2094f, 0f)
            )

            val EquipRC04 = EquipRC03.addOrReplaceChild(
                "EquipRC04",
                CubeListBuilder.create().texOffs(132, 113).addBox(-1f, -1f, -13f, 2f, 2f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val EquipRC01 = EquipRCBase02.addOrReplaceChild(
                "EquipRC01",
                CubeListBuilder.create().texOffs(128, 117).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, -2f, -4f, 0f, 0.2618f, 0f)
            )

            val EquipRC02 = EquipRC01.addOrReplaceChild(
                "EquipRC02",
                CubeListBuilder.create().texOffs(132, 113).addBox(-1f, -1f, -13f, 2f, 2f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val EquipRCRadar = EquipRCBase02.addOrReplaceChild(
                "EquipRCRadar",
                CubeListBuilder.create().texOffs(128, 0).addBox(-1f, -7.5f, 0f, 1f, 15f, 5f, CubeDeformation(0f)),
                PartPose.offset(-5.2f, 0f, 5.5f)
            )

            val EquipR04_1 = EquipR03.addOrReplaceChild(
                "EquipR04_1",
                CubeListBuilder.create().texOffs(128, 60).addBox(-6f, 0f, -10f, 6f, 7f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 11f, -13f, 0f, -0.1745f, 0f)
            )

            val EquipRC2Base01 = EquipR04_1.addOrReplaceChild(
                "EquipRC2Base01",
                CubeListBuilder.create().texOffs(128, 0).addBox(-4.5f, 0f, -10f, 9f, 10f, 10f, CubeDeformation(0f)),
                PartPose.offset(-3f, -1f, -10f)
            )

            val EquipRC2Base02 = EquipRC2Base01.addOrReplaceChild(
                "EquipRC2Base02",
                CubeListBuilder.create().texOffs(128, 0).addBox(-5f, -5f, -10f, 10f, 5f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -1f, 0.0524f, 0f, 0f)
            )

            val EquipRC203 = EquipRC2Base02.addOrReplaceChild(
                "EquipRC203",
                CubeListBuilder.create().texOffs(128, 117).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -4f, -8f, -0.1745f, 0f, 0f)
            )

            val EquipRC204 = EquipRC203.addOrReplaceChild(
                "EquipRC204",
                CubeListBuilder.create().texOffs(132, 113).addBox(-1f, -1f, -13f, 2f, 2f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val EquipRC201 = EquipRC2Base02.addOrReplaceChild(
                "EquipRC201",
                CubeListBuilder.create().texOffs(128, 117).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, -4f, -8f, -0.0873f, 0f, 0f)
            )

            val EquipRC202 = EquipRC201.addOrReplaceChild(
                "EquipRC202",
                CubeListBuilder.create().texOffs(132, 113).addBox(-1f, -1f, -13f, 2f, 2f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val LegLeft = Butt.addOrReplaceChild(
                "LegLeft",
                CubeListBuilder.create().texOffs(0, 80).mirror().addBox(-3f, 0f, -3f, 6f, 19f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.5f, 9.5f, -3f, -0.2618f, 0f, 0.0524f)
            )

            val ShoesL = LegLeft.addOrReplaceChild(
                "ShoesL",
                CubeListBuilder.create().texOffs(22, 70).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 9f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 19f, -0.2f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -14f, 0f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -10f, 0f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -1f, 0f)
            )

            addFaceLayer(GlowHead)

            return LayerDefinition.create(meshdefinition, 256, 128)
        }
    }
}
