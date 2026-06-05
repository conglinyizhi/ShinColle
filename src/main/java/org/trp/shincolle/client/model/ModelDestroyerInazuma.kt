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
import org.trp.shincolle.entity.EntityDestroyerInazuma
import org.trp.shincolle.entity.base.EntityShipBase
import kotlin.math.min

class ModelDestroyerInazuma<T : EntityShipBase?>(root: ModelPart) : ShipModelHumanoidBase<T?>(), IGlowableModel {
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
    private val Hair02: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmLeft03: ModelPart
    private val ArmRight02: ModelPart
    private val ArmRight03: ModelPart
    private val Cloth02: ModelPart
    private val EquipMain01: ModelPart
    private val EquipC01: ModelPart
    private val EquipMain02: ModelPart
    private val EquipMain03: ModelPart
    private val EquipMain04: ModelPart
    private val EquipTL02: ModelPart
    private val EquipTL02_1: ModelPart
    private val EquipHead01: ModelPart
    private val EquipHead02: ModelPart
    private val EquipHead03: ModelPart
    private val EquipHead04: ModelPart
    private val EquipHead05: ModelPart
    private val EquipTL02a: ModelPart
    private val EquipTL02b: ModelPart
    private val EquipTL02c: ModelPart
    private val EquipTL03: ModelPart
    private val EquipTL02d: ModelPart
    private val EquipTL02e: ModelPart
    private val EquipTL02f: ModelPart
    private val EquipTL02a_1: ModelPart
    private val EquipTL02b_1: ModelPart
    private val EquipTL02c_1: ModelPart
    private val EquipTL03_1: ModelPart
    private val EquipTL02d_1: ModelPart
    private val EquipTL02e_1: ModelPart
    private val EquipTL02f_1: ModelPart
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
        this.Head = this.BodyMain.getChild("Head")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.Hair = this.Head.getChild("Hair")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.LegRight03 = this.LegRight02.getChild("LegRight03")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.Skirt02 = this.Skirt01.getChild("Skirt02")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.LegLeft03 = this.LegLeft02.getChild("LegLeft03")
        this.EquipBase = this.BodyMain.getChild("EquipBase")
        this.EquipC01 = this.EquipBase.getChild("EquipC01")
        this.EquipC02 = this.EquipC01.getChild("EquipC02")
        this.EquipC05a = this.EquipC02.getChild("EquipC05a")
        this.EquipC05b = this.EquipC05a.getChild("EquipC05b")
        this.EquipC03 = this.EquipC02.getChild("EquipC03")
        this.EquipC04a = this.EquipC02.getChild("EquipC04a")
        this.EquipC04b = this.EquipC04a.getChild("EquipC04b")
        this.EquipMain01 = this.EquipBase.getChild("EquipMain01")
        this.EquipMain03 = this.EquipMain01.getChild("EquipMain03")
        this.EquipHead01 = this.EquipMain03.getChild("EquipHead01")
        this.EquipHead02 = this.EquipHead01.getChild("EquipHead02")
        this.EquipHead05 = this.EquipHead02.getChild("EquipHead05")
        this.EquipHead04 = this.EquipHead02.getChild("EquipHead04")
        this.EquipHead03 = this.EquipHead02.getChild("EquipHead03")
        this.EquipMain02 = this.EquipMain01.getChild("EquipMain02")
        this.EquipTL02 = this.EquipMain01.getChild("EquipTL02")
        this.EquipTL03 = this.EquipTL02.getChild("EquipTL03")
        this.EquipTL02f = this.EquipTL02.getChild("EquipTL02f")
        this.EquipTL02b = this.EquipTL02.getChild("EquipTL02b")
        this.EquipTL02a = this.EquipTL02.getChild("EquipTL02a")
        this.EquipTL02c = this.EquipTL02.getChild("EquipTL02c")
        this.EquipTL02d = this.EquipTL02.getChild("EquipTL02d")
        this.EquipTL02e = this.EquipTL02.getChild("EquipTL02e")
        this.EquipMain04 = this.EquipMain01.getChild("EquipMain04")
        this.EquipTL02_1 = this.EquipMain01.getChild("EquipTL02_1")
        this.EquipTL02b_1 = this.EquipTL02_1.getChild("EquipTL02b_1")
        this.EquipTL02d_1 = this.EquipTL02_1.getChild("EquipTL02d_1")
        this.EquipTL02c_1 = this.EquipTL02_1.getChild("EquipTL02c_1")
        this.EquipTL02f_1 = this.EquipTL02_1.getChild("EquipTL02f_1")
        this.EquipTL02a_1 = this.EquipTL02_1.getChild("EquipTL02a_1")
        this.EquipTL03_1 = this.EquipTL02_1.getChild("EquipTL03_1")
        this.EquipTL02e_1 = this.EquipTL02_1.getChild("EquipTL02e_1")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.Cloth02 = this.Cloth01.getChild("Cloth02")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.ArmLeft03 = this.ArmLeft02.getChild("ArmLeft03")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.ArmRight03 = this.ArmRight02.getChild("ArmRight03")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowHead = this.GlowBodyMain.getChild("GlowHead")
        this.initFaceParts(this.GlowHead)
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
    }

    override fun setupAnim(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val ctx = computePoseContext(entity, limbSwing, limbSwingAmount, ageInTicks, 0.1222f)
        resetPoseState()
        resetOffsets()

        applyFaceAndMouth(entity)
        setFlushVisible(entity != null && (entity.emotionPrimary == EntityShipBase.EMOTION_SHY || entity.emotionPrimary == EntityShipBase.EMOTION_HAPPY))
        applyEquipVisibility(entity)

        if (isDeadPose(entity)) {
            applyDeadPose()
            syncGlowParts()
            return
        }

        applyHeadRotation(Head, entity, ageInTicks, netHeadYaw, headPitch)
        Head.xRot += HEAD_BASE_X_ROT

        applyBasePose(ctx)
        applySpecialPoseAdjustments(entity, ctx, ageInTicks)
        applyHairAnimation(ctx, ageInTicks, limbSwing)

        syncGlowParts()
    }

    private fun resetPoseState() {
        this.isDeadPose = false
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
        ArmRight02.x = armRight02DefaultX
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
        ArmLeft02.zRot = 0.0f
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
    }

    private fun applyEquipVisibility(entity: EntityShipBase?) {
        if (entity == null) return
        val showRigging = entity.getEquipFlag(EntityDestroyerInazuma.EQUIP_RIGGING)
        EquipBase.visible = showRigging
    }

    private fun applyBasePose(ctx: PoseContext) {
        val angleX = ctx.angleX

        BodyMain.xRot = BODY_BASE_X_ROT
        Butt.xRot = BUTT_BASE_X_ROT
        Skirt01.xRot = SKIRT_BASE_X_ROT
        Skirt02.xRot = SKIRT_BASE_X_ROT

        Ahoke.yRot = AHOKE_BASE_Y_ROT + angleX * 0.2f

        HairL01.xRot = -0.0524f
        HairL01.zRot = 0.1396f
        HairL02.xRot = 0.0873f
        HairL02.zRot = 0.0873f
        HairR01.xRot = -0.0524f
        HairR01.zRot = -0.1396f
        HairR02.xRot = 0.0873f
        HairR02.zRot = -0.0873f

        ArmLeft01.xRot = ARM_BASE_X_ROT
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = ARM_BASE_Z_ROT
        ArmLeft02.xRot = 0.0f
        ArmLeft02.zRot = 0.0f

        ArmRight01.xRot = ARM_BASE_X_ROT
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = -ARM_BASE_Z_ROT
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

        EquipHead01.zRot = angleX * 0.2f - 1.5708f
        EquipC02.yRot = 0.5f + Head.yRot * 0.5f
        EquipC04a.xRot = min(0.0f, -0.2f + Head.xRot)
        EquipC05a.xRot = EquipC04a.xRot
    }

    private fun applySpecialPoseAdjustments(entity: T?, ctx: PoseContext, ageInTicks: Float) {
        var legLeftX = ctx.angleAdd1 * 0.5f - 0.1222f
        var legRightX = ctx.angleAdd2 * 0.5f - 0.0698f
        val isPassenger = entity != null && entity.isPassenger()
        val isCrouching = entity != null && entity.isCrouching()
        val isSprinting = entity != null && entity.isSprinting

        if (isSprinting) {
            val armz = Mth.cos(ageInTicks * 0.8f) * 0.6f
            val armx = Mth.sin(ageInTicks * 0.8f) * -0.5f
            this.setFace(EntityShipBase.FACE_TENSION)
            Head.xRot -= 0.25f
            BodyMain.xRot = 0.1f
            Skirt01.xRot = -0.1f
            Skirt02.xRot = -0.1885f
            ArmLeft01.xRot = armx
            ArmLeft01.zRot = -1.9f + armz
            ArmRight01.xRot = -armx
            ArmRight01.zRot = 1.9f + armz
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

        if (entity != null && entity.ridingState > 0) {
            if (entity.ridingState > 1) {
                Head.yRot *= 0.5f
                Head.zRot = 0.0f
                if (hasLegacyState(entity, 1, 4)) {
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
                if (entity.isInSittingPose && entity.ridingState != 3) {
                    this.poseTranslateY = 0.275f * 3
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
                    LegLeft01.yRot = 0.1f
                    LegLeft02.z = legLeft02DefaultZ + (0.375f * OFFSET_SCALE)
                    LegLeft02.xRot = 2.45f
                    LegLeft02.zRot = 0.0175f
                    LegRight01.yRot = -0.1f
                    LegRight02.z = legRight02DefaultZ + (0.375f * OFFSET_SCALE)
                    LegRight02.xRot = 2.45f
                    LegRight02.zRot = -0.0175f
                }
            }
            if (entity.ridingState == 1 || entity.ridingState == 3) {
                Butt.xRot = -0.2f
                Butt.y = buttDefaultY + (-0.1f * OFFSET_SCALE)
                Skirt01.xRot = -0.07f
                Skirt01.y = skirt01DefaultY + (-0.1f * OFFSET_SCALE)
                Skirt02.xRot = -0.16f
                Skirt02.y = skirt02DefaultY + (-0.15f * OFFSET_SCALE)
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
        } else if (ctx.isSitting || isPassenger) {
            val mount = if (entity != null) entity.getVehicle() else null
            val ridingShip = mount is EntityShipBase
            if (entity != null && hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY = if (ridingShip) 0.0f else 0.375f * 3.2f
                Head.yRot -= 0.4f
                Head.zRot += 0.2f
                BodyMain.xRot = -0.25f
                Butt.xRot = -0.2f
                Butt.y = buttDefaultY + (-0.1f * OFFSET_SCALE)
                Skirt01.xRot = -0.07f
                Skirt01.y = skirt01DefaultY + (-0.1f * OFFSET_SCALE)
                Skirt02.xRot = -0.16f
                Skirt02.y = skirt02DefaultY + (-0.15f * OFFSET_SCALE)
                ArmLeft01.xRot = 0.35f
                ArmLeft01.zRot = -0.2618f
                ArmRight01.xRot = -0.4f
                ArmRight01.zRot = -0.2356f
                ArmRight02.zRot = -0.2356f
                legLeftX = -0.9f
                legRightX = -0.9f
                LegLeft01.zRot = -0.14f
                LegLeft02.xRot = 1.2217f
                LegLeft02.yRot = 1.2217f
                LegLeft02.zRot = -1.0472f
                LegLeft02.x = legLeft02DefaultX + (0.32f * OFFSET_SCALE)
                LegLeft02.y = legLeft02DefaultY + (0.05f * OFFSET_SCALE)
                LegLeft02.z = legLeft02DefaultZ + (0.35f * OFFSET_SCALE)
                LegRight01.zRot = 0.14f
                LegRight02.xRot = 1.2217f
                LegRight02.yRot = -1.2217f
                LegRight02.zRot = 1.0472f
                LegRight02.x = legRight02DefaultX + (-0.32f * OFFSET_SCALE)
                LegRight02.y = legRight02DefaultY + (0.05f * OFFSET_SCALE)
                LegRight02.z = legRight02DefaultZ + (0.35f * OFFSET_SCALE)
            } else {
                this.poseTranslateY = if (ridingShip) 0.0f else (if (isPassenger) 0.375f * 3.2f else 0.275f * 3.2f)
                Head.xRot -= 0.1f
                BodyMain.xRot = 0.0f
                Butt.xRot = -0.2f
                Butt.y = buttDefaultY + (-0.1f * OFFSET_SCALE)
                Skirt01.xRot = -0.07f
                Skirt01.y = skirt01DefaultY + (-0.05f * OFFSET_SCALE)
                Skirt02.xRot = -0.16f
                Skirt02.y = skirt02DefaultY + (-0.08f * OFFSET_SCALE)
                ArmLeft01.xRot = -0.4f
                ArmLeft01.zRot = 0.2618f
                ArmRight01.xRot = -0.4f
                ArmRight01.zRot = -0.2618f
                legLeftX = -0.65f
                legRightX = -0.65f
                LegLeft01.yRot = 0.1f
                LegLeft02.z = legLeft02DefaultZ + (0.375f * OFFSET_SCALE)
                LegLeft02.xRot = 2.45f
                LegLeft02.zRot = 0.0175f
                LegRight01.yRot = -0.1f
                LegRight02.z = legRight02DefaultZ + (0.375f * OFFSET_SCALE)
                LegRight02.xRot = 2.45f
                LegRight02.zRot = -0.0175f
            }
        }

        if (entity != null && entity.attackTick > 30) {
            BodyMain.xRot = 0.5f
            Butt.xRot = -0.3f
            Butt.y = buttDefaultY + (-0.15f * OFFSET_SCALE)
            ArmLeft01.xRot = -0.8f
            ArmLeft01.yRot = 0.4712f
            ArmLeft01.zRot = -0.3142f
            ArmLeft02.xRot = -0.9425f
            ArmLeft02.zRot = 0.0f
            ArmRight01.xRot = -0.8f
            ArmRight01.yRot = -0.4712f
            ArmRight01.zRot = 0.3142f
            ArmRight02.xRot = -0.9425f
            ArmRight02.zRot = 0.0f
            legLeftX -= 0.15f
            legRightX -= 0.15f
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

    private fun applyHairAnimation(ctx: PoseContext, ageInTicks: Float, limbSwing: Float) {
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f)
        val headX = Head.xRot * -0.5f
        val headZ = Head.zRot * -0.5f

        HairL01.zRot += headZ
        HairL02.zRot += headZ
        HairR01.zRot += headZ
        HairR02.zRot += headZ * 2.0f

        HairL01.xRot += ctx.angleX * 0.04f + headX
        HairL02.xRot += -angleX1 * 0.07f + headX
        HairR01.xRot += ctx.angleX * 0.04f + headX
        HairR02.xRot += -angleX1 * 0.07f + headX
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "destroyer_inazuma"), "main")

        private val DEAD_TRANSLATE_Y = deadY("ModelDestroyerInazuma")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelDestroyerInazuma")
        private const val OFFSET_SCALE = 16.0f

        private val BODY_BASE_X_ROT = -0.1047f
        private const val HEAD_BASE_X_ROT = 0.11f
        private const val BUTT_BASE_X_ROT = 0.21f
        private val SKIRT_BASE_X_ROT = -0.052f
        private val ARM_BASE_X_ROT = -0.2793f
        private const val ARM_BASE_Z_ROT = 0.2793f
        private const val LEG_BASE_Z_ROT = 0.1047f
        private const val AHOKE_BASE_Y_ROT = 1.0472f

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 105).addBox(-6.5f, -11f, -4f, 13f, 14f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -9f, 0f, -0.1047f, 0f, 0f)
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
                PartPose.offset(0f, -12.5f, -2.8f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(51, 108).addBox(-3.5f, 0f, 0f, 7f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8.7f, 6.2f, 0.1367f, 0f, 0f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(62, 27).addBox(-3.5f, -9f, 0f, 7f, 14f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -5.4f, 3.5f, -0.3142f, 0f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 81).addBox(-8f, -8f, -7.4f, 16f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0.3f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(0, 37).addBox(0f, -11f, -7f, 0f, 11f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, -6f, -6f, 1.0472f, 1.0472f, 0f)
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

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(52, 45).addBox(-8.5f, 0f, 0f, 17f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -7f)
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

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(54, 66).addBox(-7f, 0f, 0f, 14f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3f, -4f, 0.2094f, 0f, 0f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 59).addBox(-3f, 0f, -3f, 6f, 12f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.4f, 5.5f, 3.2f, -0.0698f, 0f, -0.1047f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 71).addBox(-6f, 0f, 0f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(3f, 12f, -3f)
            )

            val LegRight03 = LegRight02.addOrReplaceChild(
                "LegRight03",
                CubeListBuilder.create().texOffs(30, 76).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 5f, 7f, CubeDeformation(0f)),
                PartPose.offset(-3f, 8f, 2.9f)
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

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 59).mirror().addBox(-3f, 0f, -3f, 6f, 12f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.4f, 5.5f, 3.2f, -0.1222f, 0f, 0.1047f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 71).mirror().addBox(0f, 0f, 0f, 6f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3f, 12f, -3f)
            )

            val LegLeft03 = LegLeft02.addOrReplaceChild(
                "LegLeft03",
                CubeListBuilder.create().texOffs(30, 76).addBox(-3.5f, 0f, -3.5f, 7f, 5f, 7f, CubeDeformation(0f)),
                PartPose.offset(3f, 8f, 2.9f)
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

            val EquipC03 = EquipC02.addOrReplaceChild(
                "EquipC03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, 0f, 0f, 6f, 2f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, -5f, -2f)
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

            val EquipMain01 = EquipBase.addOrReplaceChild(
                "EquipMain01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.5f, -1f, 0f, 11f, 9f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, -4f, 5f)
            )

            val EquipMain03 = EquipMain01.addOrReplaceChild(
                "EquipMain03",
                CubeListBuilder.create().texOffs(59, 15).addBox(-1f, 0f, -1.5f, 2f, 6f, 3f, CubeDeformation(0f)),
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

            val EquipHead05 = EquipHead02.addOrReplaceChild(
                "EquipHead05",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -5f, 0f, 2f, 10f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -2f)
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

            val EquipMain02 = EquipMain01.addOrReplaceChild(
                "EquipMain02",
                CubeListBuilder.create().texOffs(46, 9).addBox(-4.5f, 0f, 0f, 9f, 7f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7.7f, 0.6f, 0.6283f, 0f, 0f)
            )

            val EquipTL02 = EquipMain01.addOrReplaceChild(
                "EquipTL02",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -4f, -9f, 3f, 8f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.5f, 6f, 4.5f, 0.1396f, -0.0698f, 0f)
            )

            val EquipTL03 = EquipTL02.addOrReplaceChild(
                "EquipTL03",
                CubeListBuilder.create().texOffs(36, 45).addBox(0f, 0f, -8f, 1f, 24f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3f, -12f, 3f, 0f, -0.3491f, -0.0873f)
            )

            val EquipTL02f = EquipTL02.addOrReplaceChild(
                "EquipTL02f",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(1.3f, 2.3f, 3f)
            )

            val EquipTL02b = EquipTL02.addOrReplaceChild(
                "EquipTL02b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offset(1.3f, -2.3f, -18.8f)
            )

            val EquipTL02a = EquipTL02.addOrReplaceChild(
                "EquipTL02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 11f, CubeDeformation(0f)),
                PartPose.offset(1.3f, 0f, -19.8f)
            )

            val EquipTL02c = EquipTL02.addOrReplaceChild(
                "EquipTL02c",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offset(1.3f, 2.3f, -18.8f)
            )

            val EquipTL02d = EquipTL02.addOrReplaceChild(
                "EquipTL02d",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(1.3f, -2.3f, 3f)
            )

            val EquipTL02e = EquipTL02.addOrReplaceChild(
                "EquipTL02e",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(1.3f, 0f, 2.5f)
            )

            val EquipMain04 = EquipMain01.addOrReplaceChild(
                "EquipMain04",
                CubeListBuilder.create().texOffs(0, 26).addBox(-3f, 0f, -3f, 6f, 16f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -16.5f, 9f, -0.0873f, 0f, 0f)
            )

            val EquipTL02_1 = EquipMain01.addOrReplaceChild(
                "EquipTL02_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, -4f, -9f, 3f, 8f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5.5f, 6f, 4.5f, 0.1396f, 0.0698f, 0f)
            )

            val EquipTL02b_1 = EquipTL02_1.addOrReplaceChild(
                "EquipTL02b_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, -2.3f, -18.8f)
            )

            val EquipTL02d_1 = EquipTL02_1.addOrReplaceChild(
                "EquipTL02d_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, -2.3f, 3f)
            )

            val EquipTL02c_1 = EquipTL02_1.addOrReplaceChild(
                "EquipTL02c_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, 2.3f, -18.8f)
            )

            val EquipTL02f_1 = EquipTL02_1.addOrReplaceChild(
                "EquipTL02f_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, 2.3f, 3f)
            )

            val EquipTL02a_1 = EquipTL02_1.addOrReplaceChild(
                "EquipTL02a_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 11f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, 0f, -19.8f)
            )

            val EquipTL03_1 = EquipTL02_1.addOrReplaceChild(
                "EquipTL03_1",
                CubeListBuilder.create().texOffs(36, 45).addBox(-1f, 0f, -8f, 1f, 24f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3f, -12f, 3f, 0f, 0.3491f, 0.0873f)
            )

            val EquipTL02e_1 = EquipTL02_1.addOrReplaceChild(
                "EquipTL02e_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, 0f, 2.5f)
            )

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(84, 31).addBox(-7f, 0f, -4.4f, 14f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.6f, 0f)
            )

            val Cloth02 = Cloth01.addOrReplaceChild(
                "Cloth02",
                CubeListBuilder.create().texOffs(22, 48).addBox(-3f, 0f, 0f, 6f, 10f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 4.8f, -4.3f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(0, 88).mirror()
                    .addBox(-2.5f, -1f, -3f, 6f, 11f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.3f, -9.4f, -0.7f, -0.2793f, 0f, 0.2793f)
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

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(0, 88).addBox(-3.5f, -1f, -3f, 6f, 11f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.3f, -9.4f, -0.7f, -0.2793f, 0f, -0.2793f)
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
            addFaceLayer(GlowHead)

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
