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
import org.trp.shincolle.client.model.LegacyPoseOffsets.ridingZ
import org.trp.shincolle.client.model.LegacyPoseOffsets.sittingY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sneakY
import org.trp.shincolle.entity.EntityNorthernHime
import org.trp.shincolle.entity.base.EntityShipBase

class ModelNorthernHime<T : EntityNorthernHime?>(root: ModelPart) : ShipModelHumanoidBase<T?>(), IGlowableModel {
    private val BodyMain: ModelPart?
    private val Cloth01: ModelPart
    private val Cloth02: ModelPart
    private val Cloth03: ModelPart
    private val SantaCloth01: ModelPart
    private val Neck: ModelPart
    private val Head: ModelPart
    private val HairMain: ModelPart
    private val Hair01: ModelPart
    private val Hair02: ModelPart
    private val HairS01a: ModelPart
    private val HairS01b: ModelPart
    private val HairS02a: ModelPart
    private val HairS02b: ModelPart
    private val Hair: ModelPart
    private val HairR01: ModelPart
    private val HairR02: ModelPart
    private val Ahoke: ModelPart
    private val HairL01: ModelPart
    private val HairL02: ModelPart
    private val SantaHat01: ModelPart
    private val SantaHat02: ModelPart
    private val SantaHat03: ModelPart
    private val SantaHat04: ModelPart
    private val SantaHat05: ModelPart
    private val Butt: ModelPart
    private val LegRight01: ModelPart
    private val LegRight02: ModelPart
    private val ShoesR: ModelPart
    private val LegLeft01: ModelPart
    private val LegLeft02: ModelPart
    private val ShoesL: ModelPart
    private val ShoesL2: ModelPart
    private val EquipBase: ModelPart
    private val EquipLT01: ModelPart
    private val EquipLT02: ModelPart
    private val EquipLT03: ModelPart
    private val EquipLT04: ModelPart
    private val EquipLT05: ModelPart
    private val EquipLT06: ModelPart
    private val EquipRT01: ModelPart
    private val EquipRT02: ModelPart
    private val HeadBase: ModelPart
    private val TailHead1: ModelPart
    private val TailHead2: ModelPart
    private val TailHeadCR1: ModelPart
    private val TailJaw1: ModelPart
    private val TailHeadCL1: ModelPart
    private val ArmRight01: ModelPart
    private val ArmRight02: ModelPart
    private val ArmRight03: ModelPart
    private val ArmRight04: ModelPart
    private val ArmRight05: ModelPart
    private val ArmRightItem: ModelPart
    private val ArmRight06: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmLeft03: ModelPart
    private val ArmLeft04: ModelPart
    private val ArmLeft05: ModelPart
    private val ArmLeft06: ModelPart
    private val EquipUmbre01a: ModelPart
    private val EquipUmbre01b: ModelPart
    private val EquipUmbre01c: ModelPart
    private val EquipUmbre03a: ModelPart
    private val EquipUmbre03b: ModelPart
    private val EquipUmbre02a: ModelPart
    private val EquipUmbre02b: ModelPart
    private val EquipUmbre02: ModelPart
    private val GlowBodyMain: ModelPart?
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val HeadHR: ModelPart
    private val HeadHR2: ModelPart
    private val HeadHR3: ModelPart
    private val HeadHL: ModelPart
    private val HeadHL2: ModelPart
    private val HeadHL3: ModelPart
    private val GlowEquipBase: ModelPart
    private val GlowEquipRT01: ModelPart
    private val GlowEquipRT02: ModelPart
    private val GlowHeadBase: ModelPart
    private val GlowTailHead1: ModelPart
    private val TailHeadT01: ModelPart
    private val GlowTailHead2: ModelPart
    private val TailHeadC2: ModelPart
    private val TailHeadC3: ModelPart
    private val GlowTailJaw1: ModelPart
    private val TailJawT01: ModelPart
    private val EquipRoad01: ModelPart
    private val EquipRoad02: ModelPart
    private val EquipRoad03: ModelPart
    private val GlowEquipLT01: ModelPart
    private val GlowEquipLT02: ModelPart
    private val GlowEquipLT03: ModelPart
    private val GlowEquipLT04: ModelPart
    private val GlowEquipLT05: ModelPart
    private val GlowEquipLT06: ModelPart
    private val EquipLHead: ModelPart
    private val EquipLHead01: ModelPart
    private val EquipLHead02: ModelPart
    private val EquipLHead03: ModelPart
    private var isDeadPose = false
    private var poseTranslateY = 0f
    private var poseTranslateZ = 0f
    private var isSittingPose = false

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.Cloth02 = this.Cloth01.getChild("Cloth02")
        this.Cloth03 = this.Cloth02.getChild("Cloth03")
        this.SantaCloth01 = this.Cloth03.getChild("SantaCloth01")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.HairS01a = this.Hair01.getChild("HairS01a")
        this.HairS01b = this.HairS01a.getChild("HairS01b")
        this.HairS02a = this.Hair01.getChild("HairS02a")
        this.HairS02b = this.HairS02a.getChild("HairS02b")
        this.Hair = this.Head.getChild("Hair")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.SantaHat01 = this.Head.getChild("SantaHat01")
        this.SantaHat02 = this.SantaHat01.getChild("SantaHat02")
        this.SantaHat03 = this.SantaHat02.getChild("SantaHat03")
        this.SantaHat04 = this.SantaHat03.getChild("SantaHat04")
        this.SantaHat05 = this.SantaHat04.getChild("SantaHat05")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.ShoesR = this.LegRight02.getChild("ShoesR")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.ShoesL = this.LegLeft02.getChild("ShoesL")
        this.ShoesL2 = this.LegLeft01.getChild("ShoesL2")
        this.EquipBase = this.BodyMain.getChild("EquipBase")
        this.EquipLT01 = this.EquipBase.getChild("EquipLT01")
        this.EquipLT02 = this.EquipLT01.getChild("EquipLT02")
        this.EquipLT03 = this.EquipLT02.getChild("EquipLT03")
        this.EquipLT04 = this.EquipLT03.getChild("EquipLT04")
        this.EquipLT05 = this.EquipLT04.getChild("EquipLT05")
        this.EquipLT06 = this.EquipLT05.getChild("EquipLT06")
        this.EquipRT01 = this.EquipBase.getChild("EquipRT01")
        this.EquipRT02 = this.EquipRT01.getChild("EquipRT02")
        this.HeadBase = this.EquipRT02.getChild("HeadBase")
        this.TailHead1 = this.HeadBase.getChild("TailHead1")
        this.TailHead2 = this.TailHead1.getChild("TailHead2")
        this.TailHeadCR1 = this.HeadBase.getChild("TailHeadCR1")
        this.TailJaw1 = this.HeadBase.getChild("TailJaw1")
        this.TailHeadCL1 = this.HeadBase.getChild("TailHeadCL1")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.ArmRight03 = this.ArmRight02.getChild("ArmRight03")
        this.ArmRight04 = this.ArmRight03.getChild("ArmRight04")
        this.ArmRight05 = this.ArmRight04.getChild("ArmRight05")
        this.ArmRightItem = this.ArmRight05.getChild("ArmRightItem")
        this.ArmRight06 = this.ArmRight05.getChild("ArmRight06")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.ArmLeft03 = this.ArmLeft02.getChild("ArmLeft03")
        this.ArmLeft04 = this.ArmLeft03.getChild("ArmLeft04")
        this.ArmLeft05 = this.ArmLeft04.getChild("ArmLeft05")
        this.ArmLeft06 = this.ArmLeft05.getChild("ArmLeft06")
        this.EquipUmbre01a = this.ArmLeft05.getChild("EquipUmbre01a")
        this.EquipUmbre01b = this.EquipUmbre01a.getChild("EquipUmbre01b")
        this.EquipUmbre01c = this.EquipUmbre01b.getChild("EquipUmbre01c")
        this.EquipUmbre03a = this.EquipUmbre01c.getChild("EquipUmbre03a")
        this.EquipUmbre03b = this.EquipUmbre03a.getChild("EquipUmbre03b")
        this.EquipUmbre02a = this.EquipUmbre01c.getChild("EquipUmbre02a")
        this.EquipUmbre02b = this.EquipUmbre02a.getChild("EquipUmbre02b")
        this.EquipUmbre02 = this.EquipUmbre01a.getChild("EquipUmbre02")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        this.HeadHR = this.GlowHead.getChild("HeadHR")
        this.HeadHR2 = this.HeadHR.getChild("HeadHR2")
        this.HeadHR3 = this.HeadHR2.getChild("HeadHR3")
        this.HeadHL = this.GlowHead.getChild("HeadHL")
        this.HeadHL2 = this.HeadHL.getChild("HeadHL2")
        this.HeadHL3 = this.HeadHL2.getChild("HeadHL3")
        this.GlowEquipBase = this.GlowBodyMain.getChild("GlowEquipBase")
        this.GlowEquipRT01 = this.GlowEquipBase.getChild("GlowEquipRT01")
        this.GlowEquipRT02 = this.GlowEquipRT01.getChild("GlowEquipRT02")
        this.GlowHeadBase = this.GlowEquipRT02.getChild("GlowHeadBase")
        this.GlowTailHead1 = this.GlowHeadBase.getChild("GlowTailHead1")
        this.TailHeadT01 = this.GlowTailHead1.getChild("TailHeadT01")
        this.GlowTailHead2 = this.GlowTailHead1.getChild("GlowTailHead2")
        this.TailHeadC2 = this.GlowTailHead2.getChild("TailHeadC2")
        this.TailHeadC3 = this.GlowTailHead2.getChild("TailHeadC3")
        this.GlowTailJaw1 = this.GlowHeadBase.getChild("GlowTailJaw1")
        this.TailJawT01 = this.GlowTailJaw1.getChild("TailJawT01")
        this.EquipRoad01 = this.GlowHeadBase.getChild("EquipRoad01")
        this.EquipRoad02 = this.EquipRoad01.getChild("EquipRoad02")
        this.EquipRoad03 = this.EquipRoad02.getChild("EquipRoad03")
        this.GlowEquipLT01 = this.GlowEquipBase.getChild("GlowEquipLT01")
        this.GlowEquipLT02 = this.GlowEquipLT01.getChild("GlowEquipLT02")
        this.GlowEquipLT03 = this.GlowEquipLT02.getChild("GlowEquipLT03")
        this.GlowEquipLT04 = this.GlowEquipLT03.getChild("GlowEquipLT04")
        this.GlowEquipLT05 = this.GlowEquipLT04.getChild("GlowEquipLT05")
        this.GlowEquipLT06 = this.GlowEquipLT05.getChild("GlowEquipLT06")
        this.EquipLHead = this.GlowEquipLT06.getChild("EquipLHead")
        this.EquipLHead01 = this.EquipLHead.getChild("EquipLHead01")
        this.EquipLHead02 = this.EquipLHead01.getChild("EquipLHead02")
        this.EquipLHead03 = this.EquipLHead02.getChild("EquipLHead03")

        this.initFaceParts(GlowHead)
    }

    override fun setupAnim(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val ctx = computePoseContext(entity, limbSwing, limbSwingAmount, ageInTicks, LEG_BASE_X_ROT_OFFSET)
        resetPoseState()

        applyFaceAndGear(entity, ctx)
        if (isDeadPose(entity)) {
            applyDeadPose()
            syncGlowParts()
            return
        }
        applyHeadRotation(Head, entity, ageInTicks, netHeadYaw, headPitch)
        applyBasePose(ctx)
        applyHeadTiltHair()
        applyEquipAnimation(entity, ctx, ageInTicks, limbSwingAmount)
        applySpecialPoseAdjustments(entity, ctx, ageInTicks)
        syncGlowParts()
    }

    private fun applyEquipVisibility(ship: EntityShipBase?) {
        if (ship is EntityNorthernHime) {
            val cannon = ship.getEquipFlag(EntityNorthernHime.EQUIP_CANNON)
            val santaCloth = ship.getEquipFlag(EntityNorthernHime.EQUIP_SANTA_CLOTH)
            val santaHat = ship.getEquipFlag(EntityNorthernHime.EQUIP_SANTA_HAT)
            val umbrella = ship.getEquipFlag(EntityNorthernHime.EQUIP_UMBRELLA)
            val shoes = ship.getEquipFlag(EntityNorthernHime.EQUIP_SHOES)

            this.GlowEquipBase.visible = cannon
            this.EquipBase.visible = cannon
            this.SantaCloth01.visible = santaCloth
            this.SantaHat01.visible = santaHat
            this.EquipUmbre01a.visible = umbrella
            this.ShoesL.visible = shoes
            this.ShoesL2.visible = shoes
            this.ShoesR.visible = shoes
        }
    }

    private fun applyBasePose(ctx: PoseContext) {
        val headX = Head.xRot * -0.5f

        BodyMain!!.xRot = BODY_BASE_X_ROT
        Ahoke.xRot = ctx.angleX * 0.25f + 0.35f
        Hair01.xRot = ctx.angleX * 0.02f + 0.35f + headX
        Hair02.xRot = ctx.angleX * 0.04f + 0.14f + headX
        HairL01.xRot = ctx.angleX * 0.02f + headX - 0.26f
        HairL02.xRot = ctx.angleX * 0.02f + headX + 0.26f
        HairR01.xRot = ctx.angleX * 0.02f + headX - 0.26f
        HairR02.xRot = ctx.angleX * 0.02f + headX + 0.26f

        ArmLeft01.xRot = ctx.angleAdd2 + ARM_BASE_X_ROT
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = -ctx.angleX * 0.1f - ARM_BASE_Z_ROT
        ArmLeft02.xRot = 0.0f
        ArmLeft04.yRot = 0.0f

        ArmRight01.xRot = ctx.angleAdd1 + ARM_BASE_X_ROT
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = ctx.angleX * 0.1f + ARM_BASE_Z_ROT
        ArmRight02.xRot = 0.0f
        ArmRight04.yRot = 0.0f

        LegLeft01.yRot = 0.0f
        LegLeft01.zRot = -LEG_BASE_Z_ROT
        LegLeft02.xRot = 0.0f
        LegRight01.yRot = 0.0f
        LegRight01.zRot = LEG_BASE_Z_ROT
        LegRight02.xRot = 0.0f
    }

    private fun applyFaceAndGear(entity: T?, ctx: PoseContext?) {
        if (entity is EntityShipBase) {
            applyFaceAndMouth(entity)
            setFlushVisible(
                entity.emotionPrimary == EntityShipBase.EMOTION_SHY
                        || entity.emotionPrimary == EntityShipBase.EMOTION_HAPPY
            )
            applyEquipVisibility(entity)
        }
    }

    private fun applyEquipAnimation(entity: T?, ctx: PoseContext?, ageInTicks: Float, limbSwingAmount: Float) {
        val angleX = Mth.cos(ageInTicks * 0.08f)
        val showCannon = entity!!.getEquipFlag(EntityNorthernHime.EQUIP_CANNON)
        val showUmbrella = entity.getEquipFlag(EntityNorthernHime.EQUIP_UMBRELLA)

        ArmLeft02.y = 0.0f
        EquipUmbre03b.yRot = 0.0f

        if (showCannon) {
            EquipBase.xRot = 0.0f
            TailJaw1.xRot = angleX * 0.08f - 0.15f
            TailHeadC2.xRot = angleX * 0.12f
            TailHeadC3.xRot = -angleX * 0.08f + 0.1f
            EquipLHead01.yRot = angleX * 0.1f - 0.5f
            EquipLHead01.zRot = -angleX * 0.1f - 0.1f
            EquipLHead02.yRot = angleX * 0.3f + 0.1f
            EquipLHead02.zRot = -angleX * 0.3f
        }

        if (showUmbrella) {
            ArmLeft01.xRot = 0.0f
            ArmLeft01.yRot = -0.26f
            ArmLeft01.zRot = -0.52f
            ArmLeft02.y = 0.25f * OFFSET_SCALE
            ArmLeft02.xRot = -1.57f
            ArmLeft04.yRot = -0.52f
            EquipUmbre03b.yRot = angleX * 0.3f + 0.7f
        }

        if (entity.isSprinting || limbSwingAmount > 0.9f) {
            this.setFace(EntityShipBase.FACE_TENSION)
            ArmLeft01.zRot = -1.0f
            ArmRight01.xRot = -2.9f
            ArmRight01.zRot = -0.7f
            if (showUmbrella) {
                ArmLeft04.yRot = -1.0f
            }
        }
    }

    private fun syncGlowParts() {
        if (this.GlowBodyMain != null) {
            GlowBodyMain.copyFrom(this.BodyMain)
            GlowNeck.copyFrom(Neck)
            GlowHead.copyFrom(Head)
            GlowTailJaw1.copyFrom(TailJaw1)
        }
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        val usePoseTranslate = this.poseTranslateY != 0.0f || this.poseTranslateZ != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, this.poseTranslateY, this.poseTranslateZ)
        }

        if (this.BodyMain != null) {
            this.BodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        }

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
        val usePoseTranslate = this.poseTranslateY != 0.0f || this.poseTranslateZ != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, this.poseTranslateY, this.poseTranslateZ)
        }

        if (this.GlowBodyMain != null) {
            this.GlowBodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        }

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    private fun resetPoseState() {
        this.isDeadPose = false
        this.isSittingPose = false
        this.poseTranslateY = 0.0f
        this.poseTranslateZ = 0.0f
    }

    private fun isDeadPose(entity: T?): Boolean {
        return entity != null && entity.isInDeadPose
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        Head.xRot = 0.5f
        Head.yRot = 0.0f
        BodyMain!!.xRot = BODY_BASE_X_ROT
        Hair01.xRot = 0.2f
        Hair02.xRot = -0.3f
        HairL01.xRot = -0.26f
        HairL02.xRot = 0.26f
        HairR01.xRot = -0.26f
        HairR02.xRot = 0.26f

        ArmLeft01.xRot = ARM_BASE_X_ROT
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = -0.57f
        ArmLeft02.xRot = 0.0f
        ArmLeft02.y = 0.0f
        ArmLeft02.z = 0.0f
        ArmLeft04.yRot = 0.0f

        ArmRight01.xRot = ARM_BASE_X_ROT
        ArmRight01.zRot = 0.57f
        ArmRight02.xRot = 0.0f

        LegLeft01.xRot = SIT_LEG_X_ROT
        LegLeft01.yRot = -SIT_LEG_Y_ROT
        LegLeft01.zRot = -LEG_BASE_Z_ROT
        LegLeft02.xRot = 0.0f
        LegRight01.xRot = SIT_LEG_X_ROT
        LegRight01.yRot = SIT_LEG_Y_ROT
        LegRight01.zRot = LEG_BASE_Z_ROT
        LegRight02.xRot = 0.0f
    }

    private fun applyHeadTiltHair() {
        val headZ = Head.zRot * -0.5f
        Hair01.zRot = headZ
        Hair02.zRot = headZ
        HairL01.zRot = headZ - 0.14f
        HairL02.zRot = headZ + 0.087f
        HairR01.zRot = headZ + 0.14f
        HairR02.zRot = headZ - 0.052f
    }

    private fun applySpecialPoseAdjustments(entity: T?, ctx: PoseContext, ageInTicks: Float) {
        var legLeftX = ctx.legAddLeft
        var legRightX = ctx.legAddRight
        val showUmbrella = entity != null && entity.getEquipFlag(EntityNorthernHime.EQUIP_UMBRELLA)
        val isPassenger = entity != null && entity.isPassenger()
        val isCrouching = entity != null && entity.isCrouching()
        val useAltSit = entity != null && entity.emotionSecondary == EntityShipBase.EMOTION_BORED

        if (isCrouching) {
            this.poseTranslateY = SNEAK_TRANSLATE_Y
            Head.xRot -= 0.8727f
            BodyMain!!.xRot = 1.0472f
            Hair01.xRot += 0.2236f
            legLeftX -= 1.2f
            legRightX -= 1.2f
            EquipBase.xRot -= 0.8727f
            if (showUmbrella) {
                ArmLeft01.yRot = -1.05f
                ArmLeft02.xRot = -2.01f
                ArmLeft04.yRot = -1.05f
            }
        }

        if (ctx.isSitting && !isPassenger) {
            this.isSittingPose = true
            this.poseTranslateY = SITTING_TRANSLATE_Y
            Head.yRot *= SIT_HEAD_YAW_SCALE
            if (useAltSit) {
                Head.xRot -= 0.15f
                BodyMain!!.xRot = -0.3142f
                ArmLeft01.xRot = -2.0f
                ArmLeft01.yRot = -0.35f
                ArmLeft01.zRot = 0.35f
                ArmRight01.xRot = -2.9f
                ArmRight01.yRot = 0.35f
                ArmRight01.zRot = -0.35f
                legLeftX = -1.4f
                legRightX = -1.4f
                LegLeft01.yRot = -SIT_LEG_Y_ROT
                LegRight01.yRot = SIT_LEG_Y_ROT
                ArmLeft02.y = 0.0f
                ArmLeft02.xRot = 0.0f
                ArmLeft04.yRot = 0.0f
            } else {
                ArmLeft01.zRot -= SIT_ARM_Z_ROT_DELTA
                ArmRight01.zRot += SIT_ARM_Z_ROT_DELTA
                legLeftX = SIT_LEG_X_ROT
                legRightX = SIT_LEG_X_ROT
                LegLeft01.yRot = -SIT_LEG_Y_ROT
                LegRight01.yRot = SIT_LEG_Y_ROT
                ArmLeft02.y = 0.0f
            }
        }

        if (isPassenger) {
            this.poseTranslateY = RIDING_TRANSLATE_Y
            this.poseTranslateZ = RIDING_TRANSLATE_Z
            if (ctx.isSitting) {
                ArmLeft01.xRot = -0.8f
                ArmLeft01.zRot = -0.35f
                ArmRight01.xRot = -0.8f
                ArmRight01.zRot = 0.35f
                legLeftX = SIT_LEG_X_ROT
                legRightX = SIT_LEG_X_ROT
                LegLeft01.yRot = -0.5f
                LegRight01.yRot = 0.5f
                if (showUmbrella) {
                    ArmLeft02.y = 0.0f
                    ArmLeft02.xRot = -0.8f
                    ArmLeft04.yRot = -0.4f
                }
            } else {
                Head.xRot -= 0.25f
                ArmLeft01.xRot = -1.2f
                ArmLeft01.yRot = -0.2f
                ArmLeft01.zRot = -0.2f
                ArmRight01.xRot = -2.53f
                ArmRight01.zRot = -0.7f
                legLeftX = SIT_LEG_X_ROT
                legRightX = SIT_LEG_X_ROT
                LegLeft01.yRot = -0.5f
                LegRight01.yRot = 0.5f
                if (showUmbrella) {
                    ArmLeft02.y = 0.0f
                    ArmLeft02.xRot = -0.2f
                    ArmLeft04.yRot = -0.4f
                }
            }
        }

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

        if (!ctx.isSitting && !isPassenger) {
            LegLeft01.yRot = 0.0f
            LegRight01.yRot = 0.0f
        }
        LegLeft01.xRot = legLeftX
        LegRight01.xRot = legRightX
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "northern_hime"), "main")
        private val SITTING_TRANSLATE_Y = sittingY("ModelNorthernHime")
        private const val SIT_HEAD_YAW_SCALE = 0.25f
        private const val SIT_LEG_Y_ROT = 0.2618f
        private val SIT_LEG_X_ROT = -1.66f
        private const val SIT_ARM_Z_ROT_DELTA = 0.05f
        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelNorthernHime")
        private val RIDING_TRANSLATE_Y = ridingY("ModelNorthernHime")
        private val RIDING_TRANSLATE_Z = ridingZ("ModelNorthernHime")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelNorthernHime")
        private val BODY_BASE_X_ROT = -0.087f
        private const val ARM_BASE_X_ROT = 0.2618f
        private const val ARM_BASE_Z_ROT = 0.5235f
        private const val LEG_BASE_Z_ROT = 0.05f
        private const val LEG_BASE_X_ROT_OFFSET = 0.1745f
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 114)
                    .addBox(-6.5f, -11.0f, -4.0f, 13.0f, 7.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 2.0f, 0.0f, -0.0873f, 0.0f, 0.0f)
            )

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(128, 75)
                    .addBox(-7.0f, 0.0f, 0.0f, 14.0f, 4.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -5.0f, -4.4f)
            )

            val Cloth02 = Cloth01.addOrReplaceChild(
                "Cloth02",
                CubeListBuilder.create().texOffs(128, 87)
                    .addBox(-7.5f, 0.0f, 0.0f, 15.0f, 4.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 3.0f, -0.3f, 0.1396f, 0.0f, 0.0f)
            )

            val Cloth03 = Cloth02.addOrReplaceChild(
                "Cloth03",
                CubeListBuilder.create().texOffs(128, 100)
                    .addBox(-8.0f, 0.0f, 0.0f, 16.0f, 4.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 3.0f, -0.2f, 0.1396f, 0.0f, 0.0f)
            )

            val SantaCloth01 = Cloth03.addOrReplaceChild(
                "SantaCloth01",
                CubeListBuilder.create().texOffs(128, 114)
                    .addBox(-8.5f, 0.0f, 0.0f, 17.0f, 2.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 3.0f, -0.3f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(129, 58)
                    .addBox(-7.0f, -2.0f, -6.0f, 14.0f, 3.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -11.3f, -0.5f, 0.0524f, 0.0f, 0.0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7.0f, -14.5f, -6.5f, 14.0f, 14.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -1.5f, 0.0f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(48, 55)
                    .addBox(-7.5f, 0.0f, 0.0f, 15.0f, 12.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -15.0f, -3.0f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(1, 70)
                    .addBox(-7.5f, 0.0f, 0.0f, 15.0f, 12.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 9.0f, 2.0f, 0.3491f, 0.0f, 0.0f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(0, 70)
                    .addBox(-8.0f, 0.0f, -8.0f, 16.0f, 12.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 9.5f, 7.5f, 0.1367f, 0.0f, 0.0f)
            )

            val HairS01a = Hair01.addOrReplaceChild(
                "HairS01a",
                CubeListBuilder.create().texOffs(38, 19)
                    .addBox(0.0f, 0.0f, -2.0f, 0.0f, 7.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(7.5f, -1.0f, 3.5f, 0.087f, 0.0f, -0.2618f)
            )

            val HairS01b = HairS01a.addOrReplaceChild(
                "HairS01b",
                CubeListBuilder.create().texOffs(46, 26)
                    .addBox(0.0f, 0.0f, -2.0f, 0.0f, 7.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 7.0f, 0.0f, 0.0f, 0.0f, -0.2618f)
            )

            val HairS02a = Hair01.addOrReplaceChild(
                "HairS02a",
                CubeListBuilder.create().texOffs(38, 19)
                    .addBox(0.0f, 0.0f, -2.0f, 0.0f, 7.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-7.5f, 3.0f, 2.5f, 0.087f, 0.0f, 0.35f)
            )

            val HairS02b = HairS02a.addOrReplaceChild(
                "HairS02b",
                CubeListBuilder.create().texOffs(38, 25)
                    .addBox(0.0f, 0.0f, -2.0f, 0.0f, 7.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 7.0f, 0.0f, 0.0f, 0.0f, 0.35f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 77)
                    .addBox(-8.0f, -8.0f, -7.2f, 16.0f, 16.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -7.5f, 0.0f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(86, 102)
                    .addBox(-1.0f, 0.0f, 0.0f, 2.0f, 8.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-6.5f, 3.0f, -4.5f, -0.2618f, 0.1745f, 0.1396f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(86, 102)
                    .addBox(-1.0f, 0.0f, 0.0f, 2.0f, 8.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.2f, 7.5f, 0.0f, 0.2618f, 0.0f, -0.0524f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(104, 29)
                    .addBox(0.0f, -12.0f, -6.0f, 0.0f, 12.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -5.0f, -5.0f, 0.35f, 2.1f, 0.0f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(86, 102)
                    .addBox(-1.0f, 0.0f, 0.0f, 2.0f, 8.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(6.5f, 3.0f, -4.5f, -0.2618f, -0.1745f, -0.1396f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(86, 102)
                    .addBox(-1.0f, 0.0f, 0.0f, 2.0f, 8.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.2f, 7.5f, 0.0f, 0.2618f, 0.0f, 0.0873f)
            )

            val SantaHat01 = Head.addOrReplaceChild(
                "SantaHat01",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-6.5f, 0.0f, -6.5f, 13.0f, 3.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.0f, -16.5f, 3.0f, -0.4363f, 0.8727f, -0.1396f)
            )

            val SantaHat02 = SantaHat01.addOrReplaceChild(
                "SantaHat02",
                CubeListBuilder.create().texOffs(58, 24)
                    .addBox(-4.5f, -8.0f, -4.5f, 9.0f, 7.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 3.0f, -0.5f, -0.5236f, 0.1745f, 0.0f)
            )

            val SantaHat03 = SantaHat02.addOrReplaceChild(
                "SantaHat03",
                CubeListBuilder.create().texOffs(65, 27)
                    .addBox(-2.5f, -6.0f, -2.5f, 6.0f, 6.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -5.0f, -1.0f, -0.2731f, 0.0f, -0.5009f)
            )

            val SantaHat04 = SantaHat03.addOrReplaceChild(
                "SantaHat04",
                CubeListBuilder.create().texOffs(67, 28)
                    .addBox(-2.0f, -6.0f, -2.0f, 4.0f, 6.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.5f, -4.5f, 0.0f, -1.1383f, -0.2731f, 0.0f)
            )

            val SantaHat05 = SantaHat04.addOrReplaceChild(
                "SantaHat05",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.0f, -6.0f, -3.0f, 6.0f, 6.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.0f, -5.8f, 2.0f, 0.6109f, 0.6981f, -0.5236f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(92, 28)
                    .addBox(-5.5f, 0.0f, 0.0f, 11.0f, 6.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -2.0f, -4.0f, 0.2618f, 0.0f, 0.0f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 99)
                    .addBox(-2.5f, 0.0f, -2.5f, 5.0f, 8.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-3.2f, 5.5f, 2.4f, -0.1745f, 0.0f, 0.0524f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 99)
                    .addBox(-2.5f, 0.0f, 0.0f, 5.0f, 9.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 8.0f, -2.5f)
            )

            val ShoesR = LegRight02.addOrReplaceChild(
                "ShoesR",
                CubeListBuilder.create().texOffs(80, 45)
                    .addBox(-3.0f, 0.0f, -3.0f, 6.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 4.0f, 2.5f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 99)
                    .addBox(-2.5f, 0.0f, -2.5f, 5.0f, 8.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.2f, 5.5f, 2.4f, -0.1745f, 0.0f, -0.0524f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 99)
                    .addBox(-2.5f, 0.0f, 0.0f, 5.0f, 9.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 8.0f, -2.5f)
            )

            val ShoesL = LegLeft02.addOrReplaceChild(
                "ShoesL",
                CubeListBuilder.create().texOffs(80, 45)
                    .addBox(-3.0f, 0.0f, -3.0f, 6.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 4.0f, 2.5f)
            )

            val ShoesL2 = LegLeft01.addOrReplaceChild(
                "ShoesL2",
                CubeListBuilder.create().texOffs(80, 45)
                    .addBox(-3.0f, 0.0f, -3.0f, 6.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 4.0f, 0.0f)
            )

            val EquipBase = BodyMain.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -5.0f, 0.0f)
            )

            val EquipLT01 = EquipBase.addOrReplaceChild(
                "EquipLT01",
                CubeListBuilder.create().texOffs(0, 45)
                    .addBox(0.0f, -2.5f, -2.5f, 6.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.0f, 4.0f, 2.5f, 0.0f, -1.0472f, -0.2618f)
            )

            val EquipLT02 = EquipLT01.addOrReplaceChild(
                "EquipLT02",
                CubeListBuilder.create().texOffs(0, 45)
                    .addBox(0.0f, -2.5f, -2.5f, 6.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(6.0f, 0.0f, 0.0f, 0.0f, 0.3491f, -0.2618f)
            )

            val EquipLT03 = EquipLT02.addOrReplaceChild(
                "EquipLT03",
                CubeListBuilder.create().texOffs(0, 45)
                    .addBox(0.0f, -2.5f, -2.5f, 6.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(6.0f, 0.0f, 0.0f, 0.0f, 0.3491f, -0.2618f)
            )

            val EquipLT04 = EquipLT03.addOrReplaceChild(
                "EquipLT04",
                CubeListBuilder.create().texOffs(0, 45)
                    .addBox(0.0f, -2.5f, -2.5f, 6.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(6.0f, 0.0f, 0.0f, 0.0f, 0.3491f, -0.2618f)
            )

            val EquipLT05 = EquipLT04.addOrReplaceChild(
                "EquipLT05",
                CubeListBuilder.create().texOffs(0, 45)
                    .addBox(0.0f, -2.5f, -2.5f, 6.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(6.0f, 0.0f, 0.0f, 0.0f, 0.3491f, -0.2618f)
            )

            val EquipLT06 = EquipLT05.addOrReplaceChild(
                "EquipLT06",
                CubeListBuilder.create().texOffs(0, 45)
                    .addBox(0.0f, -2.5f, -2.5f, 6.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(6.0f, 0.0f, 0.0f, 0.0f, 0.3491f, -0.2618f)
            )

            val EquipRT01 = EquipBase.addOrReplaceChild(
                "EquipRT01",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-16.0f, -2.0f, -2.0f, 16.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 4.0f, 4.0f, 0.0f, 0.7854f, 0.3491f)
            )

            val EquipRT02 = EquipRT01.addOrReplaceChild(
                "EquipRT02",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-16.0f, -2.0f, -4.0f, 16.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-16.0f, 0.0f, 2.0f, 0.0f, -1.0472f, 0.0f)
            )

            val HeadBase = EquipRT02.addOrReplaceChild(
                "HeadBase",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-6.0f, -8.0f, 2.0f, 12.0f, 15.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-14.0f, -3.0f, 0.0f, -0.4363f, -2.7925f, -0.1396f)
            )

            val TailHead1 = HeadBase.addOrReplaceChild(
                "TailHead1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-7.0f, -0.2f, -5.6f, 14.0f, 8.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -9.5f, 4.0f, 0.1745f, 0.0f, 0.0f)
            )

            val TailHead2 = TailHead1.addOrReplaceChild(
                "TailHead2",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-7.0f, 0.0f, 0.0f, 14.0f, 8.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -1.0f, 4.5f)
            )

            val TailHeadCR1 = HeadBase.addOrReplaceChild(
                "TailHeadCR1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, 0.0f, -3.0f, 2.0f, 11.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-6.0f, -5.0f, 12.0f, 0.0f, -0.0524f, 0.0f)
            )

            val TailJaw1 = HeadBase.addOrReplaceChild(
                "TailJaw1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-6.5f, 0.0f, 0.0f, 13.0f, 5.0f, 14.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 3.0f, 5.0f, -0.2731f, 0.0f, 0.0f)
            )

            val TailHeadCL1 = HeadBase.addOrReplaceChild(
                "TailHeadCL1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, 0.0f, -3.0f, 2.0f, 11.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(6.0f, -5.0f, 12.0f, 0.0f, 0.0524f, 0.0f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(2, 100)
                    .addBox(-3.0f, -1.0f, -2.0f, 4.0f, 5.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-6.0f, -9.8f, -0.7f, 0.2618f, 0.0f, 0.5236f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(2, 100)
                    .addBox(-2.0f, 0.0f, -4.0f, 4.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.0f, 4.0f, 2.0f)
            )

            val ArmRight03 = ArmRight02.addOrReplaceChild(
                "ArmRight03",
                CubeListBuilder.create().texOffs(0, 90)
                    .addBox(-3.0f, 0.0f, -3.0f, 6.0f, 3.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 1.0f, -2.0f)
            )

            val ArmRight04 = ArmRight03.addOrReplaceChild(
                "ArmRight04",
                CubeListBuilder.create().texOffs(72, 43)
                    .addBox(-4.0f, 0.0f, -4.0f, 8.0f, 2.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 3.0f, 0.0f)
            )

            val ArmRight05 = ArmRight04.addOrReplaceChild(
                "ArmRight05",
                CubeListBuilder.create().texOffs(20, 100)
                    .addBox(-3.0f, 0.0f, -3.5f, 6.0f, 7.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 2.0f, 0.0f)
            )

            val ArmRightItem = ArmRight05.addOrReplaceChild(
                "ArmRightItem",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val ArmRight06 = ArmRight05.addOrReplaceChild(
                "ArmRight06",
                CubeListBuilder.create().texOffs(20, 100)
                    .addBox(-1.5f, 0.0f, -3.0f, 3.0f, 4.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.0f, 1.0f, -1.5f, -0.0873f, -0.0873f, -0.1745f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(2, 100)
                    .addBox(-1.0f, -1.0f, -2.0f, 4.0f, 5.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(6.0f, -9.8f, -0.7f, -0.2731f, 0.0f, -0.5236f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(2, 100)
                    .addBox(-2.0f, 0.0f, -4.0f, 4.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.0f, 4.0f, 2.0f)
            )

            val ArmLeft03 = ArmLeft02.addOrReplaceChild(
                "ArmLeft03",
                CubeListBuilder.create().texOffs(0, 90)
                    .addBox(-3.0f, 0.0f, -3.0f, 6.0f, 3.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 1.0f, -2.0f)
            )

            val ArmLeft04 = ArmLeft03.addOrReplaceChild(
                "ArmLeft04",
                CubeListBuilder.create().texOffs(72, 43)
                    .addBox(-4.0f, 0.0f, -4.0f, 8.0f, 2.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 3.0f, 0.0f)
            )

            val ArmLeft05 = ArmLeft04.addOrReplaceChild(
                "ArmLeft05",
                CubeListBuilder.create().texOffs(20, 100)
                    .addBox(-3.0f, 0.0f, -3.5f, 6.0f, 7.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 2.0f, 0.0f)
            )

            val ArmLeft06 = ArmLeft05.addOrReplaceChild(
                "ArmLeft06",
                CubeListBuilder.create().texOffs(20, 100)
                    .addBox(-1.5f, 0.0f, -3.0f, 3.0f, 4.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-2.0f, 1.0f, -1.5f, -0.0873f, 0.0873f, 0.1745f)
            )

            val EquipUmbre01a = ArmLeft05.addOrReplaceChild(
                "EquipUmbre01a",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, -6.0f, 2.0f, 2.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.0f, 4.0f, -1.0f)
            )

            val EquipUmbre01b = EquipUmbre01a.addOrReplaceChild(
                "EquipUmbre01b",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, -12.0f, 2.0f, 2.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, -6.0f)
            )

            val EquipUmbre01c = EquipUmbre01b.addOrReplaceChild(
                "EquipUmbre01c",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, -12.0f, 2.0f, 2.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, -12.0f)
            )

            val EquipUmbre03a = EquipUmbre01c.addOrReplaceChild(
                "EquipUmbre03a",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, -7.0f, 0.0f, 13.0f, 17.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, -14.4f, 0.0f, -0.2618f, 0.3643f)
            )

            val EquipUmbre03b = EquipUmbre03a.addOrReplaceChild(
                "EquipUmbre03b",
                CubeListBuilder.create().texOffs(54, 0)
                    .addBox(-2.0f, -6.0f, 0.0f, 5.0f, 12.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(1.5f, 2.0f, 2.9f, -0.0911f, 0.6829f, 0.1367f)
            )

            val EquipUmbre02a = EquipUmbre01c.addOrReplaceChild(
                "EquipUmbre02a",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-16.0f, -9.0f, -2.0f, 20.0f, 18.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-3.0f, 0.0f, -12.0f, 0.0f, 0.1745f, 0.5236f)
            )

            val EquipUmbre02b = EquipUmbre02a.addOrReplaceChild(
                "EquipUmbre02b",
                CubeListBuilder.create().texOffs(54, 0)
                    .addBox(-11.0f, -8.0f, 0.0f, 13.0f, 16.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(1.0f, 0.0f, 0.0f, -0.0524f, -0.0873f, 0.0f)
            )

            val EquipUmbre02 = EquipUmbre01a.addOrReplaceChild(
                "EquipUmbre02",
                CubeListBuilder.create().texOffs(38, 57)
                    .addBox(-2.5f, -1.0f, 0.0f, 5.0f, 2.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 6.0f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0f, 2.0f, 0.0f, -0.0873f, 0.0f, 0.0f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0f, -11.3f, -0.5f, 0.0524f, 0.0f, 0.0f)
            )

            val GlowHead =
                GlowNeck.addOrReplaceChild("GlowHead", CubeListBuilder.create(), PartPose.offset(0.0f, -1.5f, 0.0f))

            val HeadHR = GlowHead.addOrReplaceChild(
                "HeadHR",
                CubeListBuilder.create().texOffs(30, 90)
                    .addBox(-3.0f, -2.5f, -2.5f, 3.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-5.9f, -10.8f, 1.0f, -0.7854f, 0.1745f, 0.3142f)
            )

            val HeadHR2 = HeadHR.addOrReplaceChild(
                "HeadHR2",
                CubeListBuilder.create().texOffs(30, 90)
                    .addBox(-1.0f, -2.0f, -2.0f, 1.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-3.0f, 0.0f, 0.0f)
            )

            val HeadHR3 = HeadHR2.addOrReplaceChild(
                "HeadHR3",
                CubeListBuilder.create().texOffs(30, 90)
                    .addBox(-1.0f, -1.5f, -1.5f, 1.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.0f, 0.0f, 0.0f)
            )

            val HeadHL = GlowHead.addOrReplaceChild(
                "HeadHL",
                CubeListBuilder.create().texOffs(30, 90)
                    .addBox(0.0f, -2.5f, -2.5f, 3.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(5.9f, -10.9f, 1.0f, -0.7854f, -0.1745f, -0.3142f)
            )

            val HeadHL2 = HeadHL.addOrReplaceChild(
                "HeadHL2",
                CubeListBuilder.create().texOffs(30, 90)
                    .addBox(0.0f, -2.0f, -2.0f, 1.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(3.0f, 0.0f, 0.0f)
            )

            val HeadHL3 = HeadHL2.addOrReplaceChild(
                "HeadHL3",
                CubeListBuilder.create().texOffs(30, 90)
                    .addBox(0.0f, -1.5f, -1.5f, 1.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.0f, 0.0f, 0.0f)
            )

            val GlowEquipBase = GlowBodyMain.addOrReplaceChild(
                "GlowEquipBase",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, -5.0f, 0.0f)
            )

            val GlowEquipRT01 = GlowEquipBase.addOrReplaceChild(
                "GlowEquipRT01",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0f, 4.0f, 4.0f, 0.0f, 0.7854f, 0.3491f)
            )

            val GlowEquipRT02 = GlowEquipRT01.addOrReplaceChild(
                "GlowEquipRT02",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(-16.0f, 0.0f, 2.0f, 0.0f, -1.0472f, 0.0f)
            )

            val GlowHeadBase = GlowEquipRT02.addOrReplaceChild(
                "GlowHeadBase",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(-14.0f, -3.0f, 0.0f, -0.4363f, -2.7925f, -0.1396f)
            )

            val GlowTailHead1 = GlowHeadBase.addOrReplaceChild(
                "GlowTailHead1",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0f, -9.5f, 4.0f, 0.1745f, 0.0f, 0.0f)
            )

            val TailHeadT01 = GlowTailHead1.addOrReplaceChild(
                "TailHeadT01",
                CubeListBuilder.create().texOffs(0, 55)
                    .addBox(-6.0f, 0.0f, 0.0f, 12.0f, 5.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 4.5f, 4.5f, -0.1745f, 0.0f, 0.0f)
            )

            val GlowTailHead2 = GlowTailHead1.addOrReplaceChild(
                "GlowTailHead2",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, -1.0f, 4.5f)
            )

            val TailHeadC2 = GlowTailHead2.addOrReplaceChild(
                "TailHeadC2",
                CubeListBuilder.create().texOffs(0, 13)
                    .addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.2f, 3.2f, 10.5f, 0.0873f, 0.0873f, 0.0176f)
            )

            val TailHeadC3 = GlowTailHead2.addOrReplaceChild(
                "TailHeadC3",
                CubeListBuilder.create().texOffs(0, 13)
                    .addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-3.2f, 3.2f, 10.5f, 0.0873f, -0.0873f, 0.0f)
            )

            val GlowTailJaw1 = GlowHeadBase.addOrReplaceChild(
                "GlowTailJaw1",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0f, 3.0f, 5.0f, -0.2731f, 0.0f, 0.0f)
            )

            val TailJawT01 = GlowTailJaw1.addOrReplaceChild(
                "TailJawT01",
                CubeListBuilder.create().texOffs(0, 56)
                    .addBox(-5.5f, 0.0f, 0.0f, 11.0f, 5.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -3.0f, 4.0f, 0.1745f, 0.0f, 0.0f)
            )

            val EquipRoad01 = GlowHeadBase.addOrReplaceChild(
                "EquipRoad01",
                CubeListBuilder.create().texOffs(46, 41)
                    .addBox(0.0f, 0.0f, 0.0f, 7.0f, 2.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(6.0f, -11.5f, -3.0f, -0.2094f, 0.0873f, 0.0f)
            )

            val EquipRoad02 = EquipRoad01.addOrReplaceChild(
                "EquipRoad02",
                CubeListBuilder.create().texOffs(46, 41)
                    .addBox(0.0f, 0.0f, 0.0f, 7.0f, 2.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 12.0f)
            )

            val EquipRoad03 = EquipRoad02.addOrReplaceChild(
                "EquipRoad03",
                CubeListBuilder.create().texOffs(46, 41)
                    .addBox(0.0f, 0.0f, 0.0f, 7.0f, 2.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 12.0f)
            )

            val GlowEquipLT01 = GlowEquipBase.addOrReplaceChild(
                "GlowEquipLT01",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(2.0f, 4.0f, 2.5f, 0.0f, -1.0472f, -0.2618f)
            )

            val GlowEquipLT02 = GlowEquipLT01.addOrReplaceChild(
                "GlowEquipLT02",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(6.0f, 0.0f, 0.0f, 0.0f, 0.3491f, -0.2618f)
            )

            val GlowEquipLT03 = GlowEquipLT02.addOrReplaceChild(
                "GlowEquipLT03",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(6.0f, 0.0f, 0.0f, 0.0f, 0.3491f, -0.2618f)
            )

            val GlowEquipLT04 = GlowEquipLT03.addOrReplaceChild(
                "GlowEquipLT04",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(6.0f, 0.0f, 0.0f, 0.0f, 0.3491f, -0.2618f)
            )

            val GlowEquipLT05 = GlowEquipLT04.addOrReplaceChild(
                "GlowEquipLT05",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(6.0f, 0.0f, 0.0f, 0.0f, 0.3491f, -0.2618f)
            )

            val GlowEquipLT06 = GlowEquipLT05.addOrReplaceChild(
                "GlowEquipLT06",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(6.0f, 0.0f, 0.0f, 0.0f, 0.3491f, -0.2618f)
            )

            val EquipLHead = GlowEquipLT06.addOrReplaceChild(
                "EquipLHead",
                CubeListBuilder.create().texOffs(0, 29)
                    .addBox(0.0f, -3.5f, -5.0f, 10.0f, 7.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(5.0f, 0.0f, -1.0f, 0.0f, -0.6981f, -0.1745f)
            )

            val EquipLHead01 = EquipLHead.addOrReplaceChild(
                "EquipLHead01",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-12.0f, -1.0f, 0.0f, 12.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.0f, 0.0f, -4.0f, 0.0f, -0.5236f, -0.3491f)
            )

            val EquipLHead02 = EquipLHead01.addOrReplaceChild(
                "EquipLHead02",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-12.0f, -1.0f, 0.0f, 12.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-11.5f, 0.0f, 0.0f, 0.0f, 0.5236f, -0.2618f)
            )

            val EquipLHead03 = EquipLHead02.addOrReplaceChild(
                "EquipLHead03",
                CubeListBuilder.create().texOffs(24, 48)
                    .addBox(-5.0f, -1.5f, -1.0f, 6.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-11.5f, 0.0f, 0.0f, 0.0f, 0.3187f, 0.0f)
            )

            addFaceLayer(GlowHead)

            return LayerDefinition.create(meshdefinition, 256, 128)
        }
    }
}
