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
import org.trp.shincolle.entity.EntityDestroyerAkatsuki
import org.trp.shincolle.entity.EntityDestroyerIkazuchi
import org.trp.shincolle.entity.EntityDestroyerInazuma
import org.trp.shincolle.entity.base.EntityShipBase

class ModelDestroyerIkazuchi<T : EntityShipBase>(root: ModelPart) : ShipModelHumanoidBase<T>(), IGlowableModel {
    private val BodyMain: ModelPart
    private val ArmRight01: ModelPart
    private val ArmRight02: ModelPart
    private val ArmRight03: ModelPart
    private val EquipHead01: ModelPart
    private val EquipHead02: ModelPart
    private val EquipHead05: ModelPart
    private val EquipHead03: ModelPart
    private val EquipHead04: ModelPart
    private val EquipBase: ModelPart
    private val EquipC01: ModelPart
    private val EquipC02: ModelPart
    private val EquipC05a: ModelPart
    private val EquipC05b: ModelPart
    private val EquipC04a: ModelPart
    private val EquipC04b: ModelPart
    private val EquipC03: ModelPart
    private val EquipMain01: ModelPart
    private val EquipTL02_1: ModelPart
    private val EquipTL02c_1: ModelPart
    private val EquipTL02a_1: ModelPart
    private val EquipTL02d_1: ModelPart
    private val EquipTL02b_1: ModelPart
    private val EquipTL02f_1: ModelPart
    private val EquipTL03_1: ModelPart
    private val EquipTL02e_1: ModelPart
    private val EquipMain02: ModelPart
    private val EquipMain03: ModelPart
    private val EquipMain04: ModelPart
    private val EquipTL02: ModelPart
    private val EquipTL02c: ModelPart
    private val EquipTL02b: ModelPart
    private val EquipTL03: ModelPart
    private val EquipTL02e: ModelPart
    private val EquipTL02f: ModelPart
    private val EquipTL02d: ModelPart
    private val EquipTL02a: ModelPart
    private val Cloth01: ModelPart
    private val Cloth02: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmLeft03: ModelPart
    private val Butt: ModelPart
    private val LegRight01: ModelPart
    private val LegRight02: ModelPart
    private val LegRight03: ModelPart
    private val Skirt01: ModelPart
    private val Skirt02: ModelPart
    private val LegLeft01: ModelPart
    private val LegLeft02: ModelPart
    private val LegLeft03: ModelPart
    private val Head: ModelPart
    private val Hair: ModelPart
    private val HairU01: ModelPart
    private val HairL01: ModelPart
    private val HairL02: ModelPart
    private val Ahoke: ModelPart
    private val HairR01: ModelPart
    private val HairR02: ModelPart
    private val HairMain: ModelPart
    private val Hair01: ModelPart
    private val GlowBodyMain: ModelPart?
    private val GlowHead: ModelPart
    private val buttDefaultY: Float
    private val skirt01DefaultY: Float
    private val skirt02DefaultY: Float
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
    private var isDeadPose = false
    override var poseTranslateY = 0f
    private var isSittingPose = false

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.ArmRight03 = this.ArmRight02.getChild("ArmRight03")
        this.EquipHead01 = this.ArmRight03.getChild("EquipHead01")
        this.EquipHead02 = this.EquipHead01.getChild("EquipHead02")
        this.EquipHead05 = this.EquipHead02.getChild("EquipHead05")
        this.EquipHead03 = this.EquipHead02.getChild("EquipHead03")
        this.EquipHead04 = this.EquipHead02.getChild("EquipHead04")
        this.EquipBase = this.BodyMain.getChild("EquipBase")
        this.EquipC01 = this.EquipBase.getChild("EquipC01")
        this.EquipC02 = this.EquipC01.getChild("EquipC02")
        this.EquipC05a = this.EquipC02.getChild("EquipC05a")
        this.EquipC05b = this.EquipC05a.getChild("EquipC05b")
        this.EquipC04a = this.EquipC02.getChild("EquipC04a")
        this.EquipC04b = this.EquipC04a.getChild("EquipC04b")
        this.EquipC03 = this.EquipC02.getChild("EquipC03")
        this.EquipMain01 = this.EquipBase.getChild("EquipMain01")
        this.EquipTL02_1 = this.EquipMain01.getChild("EquipTL02_1")
        this.EquipTL02c_1 = this.EquipTL02_1.getChild("EquipTL02c_1")
        this.EquipTL02a_1 = this.EquipTL02_1.getChild("EquipTL02a_1")
        this.EquipTL02d_1 = this.EquipTL02_1.getChild("EquipTL02d_1")
        this.EquipTL02b_1 = this.EquipTL02_1.getChild("EquipTL02b_1")
        this.EquipTL02f_1 = this.EquipTL02_1.getChild("EquipTL02f_1")
        this.EquipTL03_1 = this.EquipTL02_1.getChild("EquipTL03_1")
        this.EquipTL02e_1 = this.EquipTL02_1.getChild("EquipTL02e_1")
        this.EquipMain02 = this.EquipMain01.getChild("EquipMain02")
        this.EquipMain03 = this.EquipMain01.getChild("EquipMain03")
        this.EquipMain04 = this.EquipMain01.getChild("EquipMain04")
        this.EquipTL02 = this.EquipMain01.getChild("EquipTL02")
        this.EquipTL02c = this.EquipTL02.getChild("EquipTL02c")
        this.EquipTL02b = this.EquipTL02.getChild("EquipTL02b")
        this.EquipTL03 = this.EquipTL02.getChild("EquipTL03")
        this.EquipTL02e = this.EquipTL02.getChild("EquipTL02e")
        this.EquipTL02f = this.EquipTL02.getChild("EquipTL02f")
        this.EquipTL02d = this.EquipTL02.getChild("EquipTL02d")
        this.EquipTL02a = this.EquipTL02.getChild("EquipTL02a")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.Cloth02 = this.Cloth01.getChild("Cloth02")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.ArmLeft03 = this.ArmLeft02.getChild("ArmLeft03")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.LegRight03 = this.LegRight02.getChild("LegRight03")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.Skirt02 = this.Skirt01.getChild("Skirt02")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.LegLeft03 = this.LegLeft02.getChild("LegLeft03")
        this.Head = this.BodyMain.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowHead = this.GlowBodyMain.getChild("GlowHead")
        this.buttDefaultY = this.Butt.y
        this.skirt01DefaultY = this.Skirt01.y
        this.skirt02DefaultY = this.Skirt02.y
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

        this.initFaceParts(this.GlowHead)
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
        resetOffsets()

        applyFaceAndMouth(entity)
        setFlushVisible(
            entity != null
                    && (entity.emotionPrimary == EntityShipBase.EMOTION_SHY
                    || entity.emotionPrimary == EntityShipBase.EMOTION_HAPPY)
        )
        applyEquipVisibility(entity)
        if (isDeadPose(entity)) {
            applyDeadPose()
            syncGlowParts()
            return
        }
        applyHeadRotation(Head, entity, ageInTicks, netHeadYaw, headPitch)
        Head.xRot += HEAD_BASE_X_ROT

        applyBasePose(ctx)
        applyEquipAnimation(entity, ctx, limbSwingAmount)
        applySpecialPoseAdjustments(entity, ctx, ageInTicks)

        syncGlowParts()
    }

    private fun resetPoseState() {
        this.isDeadPose = false
        this.isSittingPose = false
        this.poseTranslateY = 0.0f
    }

    private fun resetOffsets() {
        Butt.y = buttDefaultY
        Skirt01.y = skirt01DefaultY
        Skirt02.y = skirt02DefaultY
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

    private fun isDeadPose(entity: T?): Boolean {
        return entity != null && entity.isInDeadPose
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
        LegLeft02.yRot = 0.0f
        LegLeft02.zRot = 0.0f
        LegRight02.xRot = 0.0f
        LegRight02.yRot = 0.0f
        LegRight02.zRot = 0.0f
        EquipHead01.yRot = -1.4f
        EquipHead01.zRot = 1.4f
        EquipC02.yRot = 0.6f
        EquipC04a.xRot = 0.0f
        EquipC05a.xRot = -0.2f
    }

    private fun applyEquipVisibility(entity: EntityShipBase?) {
        if (entity == null) {
            return
        }
        val showRigging = entity.getEquipFlag(EntityDestroyerIkazuchi.EQUIP_RIGGING)
        val showAnchor = entity.getEquipFlag(EntityDestroyerIkazuchi.EQUIP_ANCHOR)

        EquipBase.visible = showRigging
        EquipHead01.visible = showAnchor
    }

    private fun applyBasePose(ctx: PoseContext) {
        val angleX = ctx.angleX

        BodyMain.xRot = BODY_BASE_X_ROT
        Butt.xRot = BUTT_BASE_X_ROT
        Skirt01.xRot = SKIRT_BASE_X_ROT
        Skirt02.xRot = SKIRT_BASE_X_ROT

        Ahoke.yRot = AHOKE_BASE_Y_ROT + angleX * 0.2f

        ArmLeft01.xRot = ctx.angleAdd2 * 0.25f + ARM_LEFT_BASE_X_ROT
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = angleX * 0.03f - ARM_BASE_Z_ROT
        ArmLeft02.xRot = 0.0f
        ArmLeft02.zRot = 0.0f

        ArmRight01.xRot = ctx.angleAdd1 * 0.25f + ARM_RIGHT_BASE_X_ROT
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = -angleX * 0.03f + ARM_BASE_Z_ROT
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
    }

    private fun applyEquipAnimation(entity: T?, ctx: PoseContext, limbSwingAmount: Float) {
        EquipHead01.yRot = 0.0f
        EquipHead01.zRot = 0.0f
        EquipC02.yRot = 0.5f + Head.yRot * 0.5f
        EquipC04a.xRot = -0.2f + Head.xRot
        if (EquipC04a.xRot > 0.0f) {
            EquipC04a.xRot = 0.0f
        }
        EquipC05a.xRot = EquipC04a.xRot

        val showRigging = entity != null && entity.getEquipFlag(EntityDestroyerIkazuchi.EQUIP_RIGGING)
        if (!showRigging) {
            ArmLeft01.zRot += 0.1f
            ArmRight01.zRot -= 0.1f
        }

        if (entity != null && (entity.isSprinting || limbSwingAmount > 0.9f)) {
            this.setFace(EntityShipBase.FACE_TENSION)
            Head.xRot -= 0.25f
            BodyMain.xRot = 0.1f
            Skirt01.xRot = -0.1f
            Skirt02.xRot = -0.1885f
            ArmLeft01.xRot += 0.1f
            ArmLeft01.zRot -= 0.3f
            ArmRight01.xRot = -2.2f + ctx.angleAdd1 * 0.2f
            ArmRight01.zRot = -0.4712f
            EquipHead01.yRot = -0.3142f
        }
    }

    private fun applySittingOrLegPose(ctx: PoseContext) {
        this.isSittingPose = ctx.isSitting
        if (ctx.isSitting) {
            applySittingPose(
                Head, ArmLeft01, ArmRight01, LegLeft01, LegRight01,
                SIT_HEAD_YAW_SCALE, SIT_ARM_Z_ROT_DELTA, SIT_LEG_Y_ROT, SIT_LEG_X_ROT
            )
            return
        }

        val legAddLeft = ctx.angleAdd1 * 0.5f - 0.14f
        val legAddRight = ctx.angleAdd2 * 0.5f - 0.03f
        LegLeft01.xRot = legAddLeft
        LegRight01.xRot = legAddRight
    }

    private fun applySpecialPoseAdjustments(entity: T?, ctx: PoseContext, ageInTicks: Float) {
        var legLeftX = ctx.angleAdd1 * 0.5f - 0.14f
        var legRightX = ctx.angleAdd2 * 0.5f - 0.03f
        val isPassenger = entity != null && entity.isPassenger()
        val isCrouching = entity != null && entity.isCrouching()
        val useAltSit = entity != null && entity.emotionSecondary == EntityShipBase.EMOTION_BORED

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

        if (ctx.isSitting || isPassenger) {
            this.isSittingPose = ctx.isSitting
            val mount = if (entity != null) entity.getVehicle() else null
            if (mount is EntityShipBase) {
                this.poseTranslateY = 0.0f
            } else {
                this.poseTranslateY = if (ctx.isSitting && !isPassenger) SITTING_TRANSLATE_Y else RIDING_TRANSLATE_Y
            }
            val ridingInazumaOrAkatsuki = mount is EntityDestroyerInazuma || mount is EntityDestroyerAkatsuki
            val mountScorn = mount is EntityShipBase && mount.getStateEmotion(1) == 4
            if (ridingInazumaOrAkatsuki) {
                if (mountScorn) {
                    BodyMain.xRot = -0.1f
                    Butt.xRot = -0.2f
                    Butt.y = buttDefaultY + (-0.1f * OFFSET_SCALE)
                    Skirt01.xRot = -0.07f
                    Skirt01.y = skirt01DefaultY + (-0.05f * OFFSET_SCALE)
                    Skirt02.xRot = -0.16f
                    Skirt02.y = skirt02DefaultY + (-0.08f * OFFSET_SCALE)
                    ArmLeft01.xRot = -0.5f
                    ArmLeft01.yRot = -0.2f
                    ArmLeft01.zRot = 0.0f
                    ArmLeft02.xRot = -1.45f
                    ArmLeft02.zRot = 0.0f
                    ArmRight01.xRot = -0.5f
                    ArmRight01.yRot = 0.2f
                    ArmRight01.zRot = 0.0f
                    ArmRight02.xRot = -1.45f
                    ArmRight02.zRot = 0.0f
                    legLeftX = -0.65f
                    legRightX = -0.65f
                    LegLeft01.yRot = 0.0f
                    LegLeft01.zRot = -0.25f
                    LegLeft02.z = legLeft02DefaultZ
                    LegLeft02.xRot = 0.8f
                    LegLeft02.zRot = 0.0175f
                    LegRight01.yRot = 0.0f
                    LegRight01.zRot = 0.25f
                    LegRight02.z = legRight02DefaultZ
                    LegRight02.xRot = 0.8f
                    LegRight02.zRot = -0.0175f
                    EquipHead01.visible = false
                } else {
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
                    ArmLeft02.zRot = 0.0f
                    ArmRight01.xRot = -1.8f
                    ArmRight01.yRot = 0.2f
                    ArmRight01.zRot = 0.0f
                    ArmRight02.xRot = 0.0f
                    ArmRight02.zRot = 0.0f
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
                }
            } else if (useAltSit) {
                val parTick = ageInTicks - ageInTicks.toInt() + (if (entity != null) (entity.tickCount % 256) else 0)
                Head.xRot -= 0.1f
                BodyMain.xRot = -0.25f
                Butt.xRot = -0.2f
                Butt.y = buttDefaultY + (-0.1f * OFFSET_SCALE)
                Skirt01.xRot = -0.07f
                Skirt01.y = skirt01DefaultY + (-0.1f * OFFSET_SCALE)
                Skirt02.xRot = -0.16f
                Skirt02.y = skirt02DefaultY + (-0.15f * OFFSET_SCALE)
                ArmLeft01.xRot = 2.5f
                ArmLeft01.zRot = 0.1f
                ArmLeft02.zRot = 1.0f
                ArmRight01.xRot = 2.5f
                ArmRight01.zRot = -0.1f
                ArmRight02.zRot = -1.0f
                legLeftX = -0.9f
                legRightX = -0.9f
                LegLeft01.zRot = -0.14f
                LegLeft02.xRot = 1.2217f
                LegLeft02.yRot = 1.2217f
                LegLeft02.zRot = -1.0472f
                LegLeft02.x = legLeft02DefaultX + (SIT_LOWER_LEG_X_OFFSET * OFFSET_SCALE)
                LegLeft02.y = legLeft02DefaultY + (0.05f * OFFSET_SCALE)
                LegLeft02.z = legLeft02DefaultZ + (0.35f * OFFSET_SCALE)
                LegRight01.zRot = 0.14f
                LegRight02.xRot = 1.2217f
                LegRight02.yRot = -1.2217f
                LegRight02.zRot = 1.0472f
                LegRight02.x = legRight02DefaultX + (-SIT_LOWER_LEG_X_OFFSET * OFFSET_SCALE)
                LegRight02.y = legRight02DefaultY + (0.05f * OFFSET_SCALE)
                LegRight02.z = legRight02DefaultZ + (0.35f * OFFSET_SCALE)
                EquipHead01.visible = false
                if (parTick < 30.0f) {
                    val az = Mth.sin(parTick * 0.033f * 1.5708f) * 1.8f
                    val az1 = az * 1.6f
                    ArmLeft01.zRot = 0.1f + az
                    ArmLeft02.zRot = 1.0f - az1
                    if (ArmLeft02.zRot < 0.0f) {
                        ArmLeft02.zRot = 0.0f
                    }
                    ArmRight01.zRot = -0.1f - az
                    ArmRight02.zRot = -1.0f + az1
                    if (ArmRight02.zRot > 0.0f) {
                        ArmRight02.zRot = 0.0f
                    }
                } else if (parTick < 45.0f) {
                    ArmLeft01.zRot = 1.9f
                    ArmLeft02.zRot = 0.0f
                    ArmRight01.zRot = -1.9f
                    ArmRight02.zRot = 0.0f
                } else if (parTick < 53.0f) {
                    val az = Mth.cos((parTick - 45.0f) * 0.125f * 1.5708f)
                    val az1 = az * 1.8f
                    ArmLeft01.zRot = 0.1f + az1
                    ArmLeft02.zRot = 1.0f - az
                    ArmRight01.zRot = -0.1f - az1
                    ArmRight02.zRot = -1.0f + az
                }
            } else {
                Head.xRot -= 0.1f
                BodyMain.xRot = -0.25f
                Butt.xRot = -0.2f
                Butt.y = buttDefaultY + (-0.1f * OFFSET_SCALE)
                Skirt01.xRot = -0.07f
                Skirt01.y = skirt01DefaultY + (-0.1f * OFFSET_SCALE)
                Skirt02.xRot = -0.16f
                Skirt02.y = skirt02DefaultY + (-0.15f * OFFSET_SCALE)
                ArmLeft01.xRot = 0.3f
                ArmLeft01.zRot = -0.2618f
                ArmRight01.xRot = 0.3f
                ArmRight01.zRot = 0.2618f
                legLeftX = -0.9f
                legRightX = -0.9f
                LegLeft01.zRot = -0.14f
                LegLeft02.xRot = 1.2217f
                LegLeft02.yRot = 1.2217f
                LegLeft02.zRot = -1.0472f
                LegLeft02.x = legLeft02DefaultX + (SIT_LOWER_LEG_X_OFFSET * OFFSET_SCALE)
                LegLeft02.y = legLeft02DefaultY + (0.05f * OFFSET_SCALE)
                LegLeft02.z = legLeft02DefaultZ + (0.35f * OFFSET_SCALE)
                LegRight01.zRot = 0.14f
                LegRight02.xRot = 1.2217f
                LegRight02.yRot = -1.2217f
                LegRight02.zRot = 1.0472f
                LegRight02.x = legRight02DefaultX + (-SIT_LOWER_LEG_X_OFFSET * OFFSET_SCALE)
                LegRight02.y = legRight02DefaultY + (0.05f * OFFSET_SCALE)
                LegRight02.z = legRight02DefaultZ + (0.35f * OFFSET_SCALE)
                EquipHead01.zRot = 1.2f
            }
        }

        val attackAnim = if (entity != null) entity.getAttackAnim(ageInTicks) else 0.0f
        val attackTicks = attackAnim * 50.0f
        if (attackTicks > 20.0f && !isPassenger) {
            Head.xRot -= 0.1f
            EquipHead01.yRot = -0.3142f
            if (entity != null && (entity.tickCount % 128) < 64) {
                ArmLeft01.xRot = 0.2356f
                ArmLeft01.zRot = -0.7854f
                ArmLeft02.zRot = 1.5708f
                ArmLeft02.x = armLeft02DefaultX + (-0.15f * OFFSET_SCALE)
                ArmRight01.xRot = -1.6f + ctx.angleAdd1 * 0.2f
                ArmRight01.zRot = -0.4f
            } else {
                ArmLeft01.xRot = 0.2356f
                ArmLeft01.zRot = -0.7854f
                ArmLeft02.zRot = 1.5708f
                ArmLeft02.x = armLeft02DefaultX + (-0.15f * OFFSET_SCALE)
                ArmRight01.xRot = 0.2356f
                ArmRight01.zRot = 0.7854f
                ArmRight02.zRot = -1.5708f
                ArmRight02.x = armRight02DefaultX + (0.15f * OFFSET_SCALE)
                EquipHead01.visible = false
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

        LegLeft01.xRot = legLeftX
        LegRight01.xRot = legRightX
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

        this.GlowBodyMain!!.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "destroyer_ikazuchi"), "main")
        private val SITTING_TRANSLATE_Y = sittingY("ModelDestroyerIkazuchi")
        private const val SIT_HEAD_YAW_SCALE = 0.25f
        private const val SIT_LEG_Y_ROT = 0.2618f
        private val SIT_LEG_X_ROT = -1.66f
        private const val SIT_ARM_Z_ROT_DELTA = 0.05f
        private const val SIT_LOWER_LEG_X_OFFSET = 0.32f
        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelDestroyerIkazuchi")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelDestroyerIkazuchi")
        private val RIDING_TRANSLATE_Y = ridingY("ModelDestroyerIkazuchi")
        private val BODY_BASE_X_ROT = -0.1047f
        private const val HEAD_BASE_X_ROT = 0.1047f
        private const val BUTT_BASE_X_ROT = 0.2094f
        private val SKIRT_BASE_X_ROT = -0.0524f
        private const val ARM_LEFT_BASE_X_ROT = 0.21f
        private val ARM_RIGHT_BASE_X_ROT = -0.07f
        private const val ARM_BASE_Z_ROT = 0.3491f
        private const val LEG_BASE_Z_ROT = 0.1047f
        private const val LEG_BASE_X_ROT_OFFSET = 0.1745f
        private const val AHOKE_BASE_Y_ROT = 1.0472f
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 105)
                    .addBox(-6.5f, -11.0f, -4.0f, 13.0f, 14.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -9.0f, 0.0f, -0.1047f, 0.0f, 0.0f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(0, 88)
                    .addBox(-3.5f, -1.0f, -3.0f, 6.0f, 11.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-7.3f, -9.4f, -0.7f, -0.0698f, 0.0f, 0.3491f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(24, 88)
                    .addBox(0.0f, 0.0f, -6.0f, 6.0f, 8.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(-3.5f, 10.0f, 3.0f)
            )

            val ArmRight03 = ArmRight02.addOrReplaceChild(
                "ArmRight03",
                CubeListBuilder.create().texOffs(36, 102)
                    .addBox(-2.5f, 0.0f, -2.5f, 5.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(3.0f, 6.0f, -3.0f)
            )

            val EquipHead01 = ArmRight03.addOrReplaceChild(
                "EquipHead01",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, 0.0f, -12.0f, 2.0f, 3.0f, 18.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.5f, 3.0f, 0.0f, 0.3142f, 0.0f, 0.0f)
            )

            val EquipHead02 = EquipHead01.addOrReplaceChild(
                "EquipHead02",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.5f, -7.0f, 0.0f, 3.0f, 14.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.0f, 1.5f, -15.0f)
            )

            val EquipHead05 = EquipHead02.addOrReplaceChild(
                "EquipHead05",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -5.0f, 0.0f, 2.0f, 10.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, -2.0f)
            )

            val EquipHead03 = EquipHead02.addOrReplaceChild(
                "EquipHead03",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 4.8f, 2.5f, -0.2618f, 0.0f, 0.0f)
            )

            val EquipHead04 = EquipHead02.addOrReplaceChild(
                "EquipHead04",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -4.8f, 2.5f, 0.2618f, 0.0f, 0.0f)
            )

            val EquipBase = BodyMain.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -1.0f, 0.0f)
            )

            val EquipC01 = EquipBase.addOrReplaceChild(
                "EquipC01",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 8.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-7.0f, -11.0f, 9.0f)
            )

            val EquipC02 = EquipC01.addOrReplaceChild(
                "EquipC02",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.5f, -3.0f, -3.5f, 7.0f, 3.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-2.0f, 0.5f, 0.0f, -0.1745f, 0.6283f, 0.0f)
            )

            val EquipC05a = EquipC02.addOrReplaceChild(
                "EquipC05a",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, -6.0f, 2.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.5f, -3.0f, 0.0f)
            )

            val EquipC05b = EquipC05a.addOrReplaceChild(
                "EquipC05b",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-0.5f, -0.5f, -10.0f, 1.0f, 1.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, -6.0f)
            )

            val EquipC04a = EquipC02.addOrReplaceChild(
                "EquipC04a",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, -6.0f, 2.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.5f, -3.0f, 0.0f)
            )

            val EquipC04b = EquipC04a.addOrReplaceChild(
                "EquipC04b",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-0.5f, -0.5f, -10.0f, 1.0f, 1.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, -6.0f)
            )

            val EquipC03 = EquipC02.addOrReplaceChild(
                "EquipC03",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.0f, 0.0f, 0.0f, 6.0f, 2.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -5.0f, -2.0f)
            )

            val EquipMain01 = EquipBase.addOrReplaceChild(
                "EquipMain01",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-5.5f, -1.0f, 0.0f, 11.0f, 9.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -4.0f, 5.0f)
            )

            val EquipTL02_1 = EquipMain01.addOrReplaceChild(
                "EquipTL02_1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.0f, -4.0f, -9.0f, 3.0f, 8.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-5.5f, 6.0f, 4.5f, 0.1396f, 0.0698f, 0.0f)
            )

            val EquipTL02c_1 = EquipTL02_1.addOrReplaceChild(
                "EquipTL02c_1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.3f, 2.3f, -18.8f)
            )

            val EquipTL02a_1 = EquipTL02_1.addOrReplaceChild(
                "EquipTL02a_1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.3f, 0.0f, -19.8f)
            )

            val EquipTL02d_1 = EquipTL02_1.addOrReplaceChild(
                "EquipTL02d_1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.3f, -2.3f, 3.0f)
            )

            val EquipTL02b_1 = EquipTL02_1.addOrReplaceChild(
                "EquipTL02b_1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.3f, -2.3f, -18.8f)
            )

            val EquipTL02f_1 = EquipTL02_1.addOrReplaceChild(
                "EquipTL02f_1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.3f, 2.3f, 3.0f)
            )

            val EquipTL03_1 = EquipTL02_1.addOrReplaceChild(
                "EquipTL03_1",
                CubeListBuilder.create().texOffs(36, 45)
                    .addBox(-1.0f, 0.0f, -8.0f, 1.0f, 24.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-3.0f, -12.0f, 3.0f, 0.0f, 0.3491f, 0.0873f)
            )

            val EquipTL02e_1 = EquipTL02_1.addOrReplaceChild(
                "EquipTL02e_1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.3f, 0.0f, 2.5f)
            )

            val EquipMain02 = EquipMain01.addOrReplaceChild(
                "EquipMain02",
                CubeListBuilder.create().texOffs(46, 9)
                    .addBox(-4.5f, 0.0f, 0.0f, 9.0f, 7.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 7.7f, 0.6f, 0.6283f, 0.0f, 0.0f)
            )

            val EquipMain03 = EquipMain01.addOrReplaceChild(
                "EquipMain03",
                CubeListBuilder.create().texOffs(59, 15)
                    .addBox(-1.0f, 0.0f, -1.5f, 2.0f, 6.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 9.5f, 9.0f, 0.5009f, 0.0f, 0.0f)
            )

            val EquipMain04 = EquipMain01.addOrReplaceChild(
                "EquipMain04",
                CubeListBuilder.create().texOffs(0, 26)
                    .addBox(-3.0f, 0.0f, -3.0f, 6.0f, 16.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -16.5f, 9.0f, -0.0873f, 0.0f, 0.0f)
            )

            val EquipTL02 = EquipMain01.addOrReplaceChild(
                "EquipTL02",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, -4.0f, -9.0f, 3.0f, 8.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(5.5f, 6.0f, 4.5f, 0.1396f, -0.0698f, 0.0f)
            )

            val EquipTL02c = EquipTL02.addOrReplaceChild(
                "EquipTL02c",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.3f, 2.3f, -18.8f)
            )

            val EquipTL02b = EquipTL02.addOrReplaceChild(
                "EquipTL02b",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.3f, -2.3f, -18.8f)
            )

            val EquipTL03 = EquipTL02.addOrReplaceChild(
                "EquipTL03",
                CubeListBuilder.create().texOffs(36, 45)
                    .addBox(0.0f, 0.0f, -8.0f, 1.0f, 24.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.0f, -12.0f, 3.0f, 0.0f, -0.3491f, -0.0873f)
            )

            val EquipTL02e = EquipTL02.addOrReplaceChild(
                "EquipTL02e",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.3f, 0.0f, 2.5f)
            )

            val EquipTL02f = EquipTL02.addOrReplaceChild(
                "EquipTL02f",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.3f, 2.3f, 3.0f)
            )

            val EquipTL02d = EquipTL02.addOrReplaceChild(
                "EquipTL02d",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.3f, -2.3f, 3.0f)
            )

            val EquipTL02a = EquipTL02.addOrReplaceChild(
                "EquipTL02a",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.3f, 0.0f, -19.8f)
            )

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(84, 31)
                    .addBox(-7.0f, 0.0f, -4.4f, 14.0f, 7.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -11.6f, 0.0f)
            )

            val Cloth02 = Cloth01.addOrReplaceChild(
                "Cloth02",
                CubeListBuilder.create().texOffs(22, 48)
                    .addBox(-3.0f, 0.0f, 0.0f, 6.0f, 10.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 4.8f, -4.3f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(0, 88)
                    .addBox(-2.5f, -1.0f, -3.0f, 6.0f, 11.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(7.3f, -9.4f, -0.7f, 0.2094f, 0.0f, -0.3491f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(24, 88)
                    .addBox(-6.0f, 0.0f, -6.0f, 6.0f, 8.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(3.5f, 10.0f, 3.0f)
            )

            val ArmLeft03 = ArmLeft02.addOrReplaceChild(
                "ArmLeft03",
                CubeListBuilder.create().texOffs(36, 102)
                    .addBox(-2.5f, 0.0f, -2.5f, 5.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(-3.0f, 6.0f, -3.0f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(54, 66)
                    .addBox(-7.0f, 0.0f, 0.0f, 14.0f, 7.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 3.0f, -4.0f, 0.2094f, 0.0f, 0.0f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 59)
                    .addBox(-3.0f, 0.0f, -3.0f, 6.0f, 12.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(-4.4f, 5.5f, 3.2f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 72)
                    .addBox(-6.0f, 0.0f, 0.0f, 6.0f, 10.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(3.0f, 12.0f, -3.0f)
            )

            val LegRight03 = LegRight02.addOrReplaceChild(
                "LegRight03",
                CubeListBuilder.create().texOffs(30, 76)
                    .addBox(-3.5f, 0.0f, -3.5f, 7.0f, 5.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(-3.0f, 8.0f, 2.9f)
            )

            val Skirt01 = Butt.addOrReplaceChild(
                "Skirt01",
                CubeListBuilder.create().texOffs(80, 16)
                    .addBox(-7.5f, 0.0f, 0.0f, 15.0f, 6.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 1.7f, -0.4f, -0.0524f, 0.0f, 0.0f)
            )

            val Skirt02 = Skirt01.addOrReplaceChild(
                "Skirt02",
                CubeListBuilder.create().texOffs(76, 0)
                    .addBox(-8.0f, 0.0f, 0.0f, 16.0f, 6.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 3.5f, -0.4f, -0.0524f, 0.0f, 0.0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 59)
                    .addBox(-3.0f, 0.0f, -3.0f, 6.0f, 12.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.4f, 5.5f, 3.2f, -0.1396f, 0.0f, 0.1047f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 72)
                    .addBox(0.0f, 0.0f, 0.0f, 6.0f, 10.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(-3.0f, 12.0f, -3.0f)
            )

            val LegLeft03 = LegLeft02.addOrReplaceChild(
                "LegLeft03",
                CubeListBuilder.create().texOffs(30, 76)
                    .addBox(-3.5f, 0.0f, -3.5f, 7.0f, 5.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(3.0f, 8.0f, 2.9f)
            )

            val Head = BodyMain.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7.0f, -14.5f, -6.5f, 14.0f, 14.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -11.8f, -1.0f, 0.1047f, 0.0f, 0.0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 81)
                    .addBox(-8.0f, -8.0f, -7.4f, 16.0f, 12.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -7.5f, 0.3f)
            )

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(52, 45)
                    .addBox(-8.5f, 0.0f, 0.0f, 17.0f, 15.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -6.0f, -7.0f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(88, 101)
                    .addBox(0.0f, 0.0f, 0.0f, 1.0f, 9.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(7.0f, -1.0f, -4.7f, 0.576f, 0.2618f, -0.2618f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(88, 103)
                    .addBox(-1.0f, 0.0f, 0.0f, 1.0f, 7.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(1.0f, 9.0f, 0.0f, 0.0f, 0.0f, 1.0472f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(0, 37)
                    .addBox(0.0f, -11.0f, -7.0f, 0.0f, 11.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.0f, -6.0f, -6.0f, 1.0472f, 1.0472f, 0.0f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(88, 101)
                    .addBox(-1.0f, 0.0f, 0.0f, 1.0f, 9.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-7.0f, -1.0f, -4.7f, 0.576f, -0.2618f, 0.2618f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(88, 103)
                    .addBox(0.0f, 0.0f, 0.0f, 1.0f, 7.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.0f, 9.0f, 0.0f, 0.0f, 0.0f, -1.0472f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(46, 104)
                    .addBox(-7.5f, 0.0f, 0.0f, 15.0f, 11.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -14.8f, -3.0f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(36, 26)
                    .addBox(-7.5f, 0.0f, 0.0f, 15.0f, 10.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 6.8f, 1.1f, 0.2094f, 0.0f, 0.0f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, -9.0f, 0.0f)
            )

            val GlowHead = GlowBodyMain.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, -11.8f, -1.0f)
            )

            addFaceLayer(GlowHead)

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
