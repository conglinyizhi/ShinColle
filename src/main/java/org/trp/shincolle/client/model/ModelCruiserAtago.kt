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
import org.trp.shincolle.client.model.LegacyPoseOffsets.deadY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sneakY
import org.trp.shincolle.entity.EntityCruiserAtago
import org.trp.shincolle.entity.base.EntityShipBase

class ModelCruiserAtago<T : EntityShipBase>(root: ModelPart) : ShincolleShipModel<T>() {

    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val Butt: ModelPart
    private val ArmRight01: ModelPart
    private val ArmLeft01: ModelPart
    private val BoobR: ModelPart?
    private val BoobL: ModelPart?
    private val Cloth01: ModelPart
    private val EquipBase: ModelPart
    private val EquipBag00: ModelPart
    private val Head: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val Hat01: ModelPart
    private val HairU01: ModelPart
    private val Ahoke: ModelPart
    private val Hair01: ModelPart?
    private val Hair01a1: ModelPart
    private val Hair02: ModelPart?
    private val Hair01b1: ModelPart
    private val Hair01c1: ModelPart
    private val Hair01d1: ModelPart
    private val Hair01b1_1: ModelPart
    private val Hair01c1_1: ModelPart
    private val Hair01a2: ModelPart
    private val Hair01a3: ModelPart?
    private val Hair01b2: ModelPart
    private val Hair01b3: ModelPart?
    private val Hair01c2: ModelPart
    private val Hair01c3: ModelPart?
    private val Hair01d2: ModelPart
    private val Hair01d3: ModelPart?
    private val Hair01b2_1: ModelPart
    private val Hair01b3_1: ModelPart?
    private val Hair01c2_1: ModelPart
    private val Hair01c3_1: ModelPart?
    private val Hat02: ModelPart
    private val Hat03: ModelPart
    private val LegLeft01: ModelPart
    private val Skirt01: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft02: ModelPart
    private val ShoeL03: ModelPart
    private val ShoeL01: ModelPart
    private val ShoeL02: ModelPart
    private val ShoeL04: ModelPart
    private val Skirt02: ModelPart
    private val Skirt03: ModelPart?
    private val LegRight02: ModelPart
    private val ShoeL03_1: ModelPart
    private val ShoeR01: ModelPart
    private val ShoeR02: ModelPart
    private val ShoeL04_1: ModelPart
    private val ArmRight02: ModelPart
    private val ArmRight02a: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmLeft02a: ModelPart
    private val Cloth01a: ModelPart
    private val Cloth01b: ModelPart
    private val Cloth01c: ModelPart
    private val Cloth01d: ModelPart
    private val Equip00: ModelPart
    private val EquipCannonBase: ModelPart
    private val EquipLIn01: ModelPart
    private val EquipRIn01: ModelPart
    private val EquipOut01: ModelPart
    private val EquipOut01_1: ModelPart
    private val EquipC01a: ModelPart
    private val EquipLIn02: ModelPart
    private val EquipLIn03: ModelPart
    private val EquipLIn07: ModelPart
    private val EquipLIn08: ModelPart
    private val EquipLIn09: ModelPart
    private val EquipLIn04: ModelPart
    private val EquipLIn06a: ModelPart
    private val EquipLIn05: ModelPart
    private val EquipLIn06b: ModelPart
    private val EquipRIn02: ModelPart
    private val EquipRIn03: ModelPart
    private val EquipRIn07: ModelPart
    private val EquipRIn08: ModelPart
    private val EquipRIn09: ModelPart
    private val EquipRIn04: ModelPart
    private val EquipRIn06a: ModelPart
    private val EquipRIn05: ModelPart
    private val EquipRIn06b: ModelPart
    private val EquipOut02: ModelPart
    private val EquipOut03: ModelPart
    private val EquipOut04: ModelPart
    private val EquipOut05: ModelPart
    private val EquipOut02_1: ModelPart
    private val EquipOut03_1: ModelPart
    private val EquipOut04_1: ModelPart
    private val EquipOut05_1: ModelPart
    private val EquipC01b: ModelPart
    private val EquipC01c: ModelPart
    private val EquipC01e: ModelPart
    private val EquipC01d: ModelPart
    private val EquipC01f: ModelPart
    private val EquipC01a_1: ModelPart
    private val EquipC01a_2: ModelPart
    private val EquipC01b_1: ModelPart
    private val EquipC01c_1: ModelPart
    private val EquipC01e_1: ModelPart
    private val EquipC01d_1: ModelPart
    private val EquipC01f_1: ModelPart
    private val EquipC01b_2: ModelPart
    private val EquipC01c_2: ModelPart
    private val EquipC01e_2: ModelPart
    private val EquipC01d_2: ModelPart
    private val EquipC01f_2: ModelPart
    private val EquipBag01: ModelPart
    private val EquipBag02: ModelPart
    private val EquipBag03: ModelPart
    private val GlowBodyMain: ModelPart?
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart?
    private val SkirtIn: ModelPart
    private val buttDefaultY: Float
    private val skirt01DefaultY: Float
    private val skirt02DefaultY: Float
    private val skirt03DefaultY: Float
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

    protected override val bodyMain: ModelPart get() = BodyMain
    protected override val neck: ModelPart get() = Neck
    protected override val head: ModelPart get() = Head
    protected override val glowBodyMain: ModelPart? get() = GlowBodyMain
    protected override val glowNeck: ModelPart? get() = GlowNeck
    protected override val glowHead: ModelPart? get() = GlowHead

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.ArmRight02a = this.ArmRight02.getChild("ArmRight02a")
        this.EquipBag00 = this.BodyMain.getChild("EquipBag00")
        this.EquipBag01 = this.EquipBag00.getChild("EquipBag01")
        this.EquipBag02 = this.EquipBag01.getChild("EquipBag02")
        this.EquipBag03 = this.EquipBag01.getChild("EquipBag03")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.ShoeL03 = this.LegLeft01.getChild("ShoeL03")
        this.ShoeL04 = this.ShoeL03.getChild("ShoeL04")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.ShoeL01 = this.LegLeft02.getChild("ShoeL01")
        this.ShoeL02 = this.ShoeL01.getChild("ShoeL02")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.Skirt02 = this.Skirt01.getChild("Skirt02")
        this.Skirt03 = this.Skirt02.getChild("Skirt03")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.ShoeL03_1 = this.LegRight01.getChild("ShoeL03_1")
        this.ShoeL04_1 = this.ShoeL03_1.getChild("ShoeL04_1")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.ShoeR01 = this.LegRight02.getChild("ShoeR01")
        this.ShoeR02 = this.ShoeR01.getChild("ShoeR02")
        this.SkirtIn = this.Butt.getChild("SkirtIn")
        this.EquipBase = this.BodyMain.getChild("EquipBase")
        this.Equip00 = this.EquipBase.getChild("Equip00")
        this.EquipOut01 = this.Equip00.getChild("EquipOut01")
        this.EquipOut02 = this.EquipOut01.getChild("EquipOut02")
        this.EquipOut03 = this.EquipOut02.getChild("EquipOut03")
        this.EquipOut04 = this.EquipOut03.getChild("EquipOut04")
        this.EquipOut05 = this.EquipOut04.getChild("EquipOut05")
        this.EquipRIn01 = this.Equip00.getChild("EquipRIn01")
        this.EquipRIn02 = this.EquipRIn01.getChild("EquipRIn02")
        this.EquipRIn07 = this.EquipRIn02.getChild("EquipRIn07")
        this.EquipRIn09 = this.EquipRIn02.getChild("EquipRIn09")
        this.EquipRIn03 = this.EquipRIn02.getChild("EquipRIn03")
        this.EquipRIn06a = this.EquipRIn03.getChild("EquipRIn06a")
        this.EquipRIn06b = this.EquipRIn06a.getChild("EquipRIn06b")
        this.EquipRIn04 = this.EquipRIn03.getChild("EquipRIn04")
        this.EquipRIn05 = this.EquipRIn04.getChild("EquipRIn05")
        this.EquipRIn08 = this.EquipRIn02.getChild("EquipRIn08")
        this.EquipC01a = this.Equip00.getChild("EquipC01a")
        this.EquipC01b = this.EquipC01a.getChild("EquipC01b")
        this.EquipC01c = this.EquipC01b.getChild("EquipC01c")
        this.EquipC01d = this.EquipC01c.getChild("EquipC01d")
        this.EquipC01e = this.EquipC01b.getChild("EquipC01e")
        this.EquipC01f = this.EquipC01e.getChild("EquipC01f")
        this.EquipOut01_1 = this.Equip00.getChild("EquipOut01_1")
        this.EquipOut02_1 = this.EquipOut01_1.getChild("EquipOut02_1")
        this.EquipOut03_1 = this.EquipOut02_1.getChild("EquipOut03_1")
        this.EquipOut04_1 = this.EquipOut03_1.getChild("EquipOut04_1")
        this.EquipOut05_1 = this.EquipOut04_1.getChild("EquipOut05_1")
        this.EquipLIn01 = this.Equip00.getChild("EquipLIn01")
        this.EquipLIn02 = this.EquipLIn01.getChild("EquipLIn02")
        this.EquipLIn09 = this.EquipLIn02.getChild("EquipLIn09")
        this.EquipLIn08 = this.EquipLIn02.getChild("EquipLIn08")
        this.EquipLIn03 = this.EquipLIn02.getChild("EquipLIn03")
        this.EquipLIn04 = this.EquipLIn03.getChild("EquipLIn04")
        this.EquipLIn05 = this.EquipLIn04.getChild("EquipLIn05")
        this.EquipLIn06a = this.EquipLIn03.getChild("EquipLIn06a")
        this.EquipLIn06b = this.EquipLIn06a.getChild("EquipLIn06b")
        this.EquipLIn07 = this.EquipLIn02.getChild("EquipLIn07")
        this.EquipCannonBase = this.EquipBase.getChild("EquipCannonBase")
        this.EquipC01a_2 = this.EquipCannonBase.getChild("EquipC01a_2")
        this.EquipC01b_2 = this.EquipC01a_2.getChild("EquipC01b_2")
        this.EquipC01c_2 = this.EquipC01b_2.getChild("EquipC01c_2")
        this.EquipC01d_2 = this.EquipC01c_2.getChild("EquipC01d_2")
        this.EquipC01e_2 = this.EquipC01b_2.getChild("EquipC01e_2")
        this.EquipC01f_2 = this.EquipC01e_2.getChild("EquipC01f_2")
        this.EquipC01a_1 = this.EquipCannonBase.getChild("EquipC01a_1")
        this.EquipC01b_1 = this.EquipC01a_1.getChild("EquipC01b_1")
        this.EquipC01c_1 = this.EquipC01b_1.getChild("EquipC01c_1")
        this.EquipC01d_1 = this.EquipC01c_1.getChild("EquipC01d_1")
        this.EquipC01e_1 = this.EquipC01b_1.getChild("EquipC01e_1")
        this.EquipC01f_1 = this.EquipC01e_1.getChild("EquipC01f_1")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair01b1 = this.Hair01.getChild("Hair01b1")
        this.Hair01b2 = this.Hair01b1.getChild("Hair01b2")
        this.Hair01b3 = this.Hair01b2.getChild("Hair01b3")
        this.Hair01c1_1 = this.Hair01.getChild("Hair01c1_1")
        this.Hair01c2_1 = this.Hair01c1_1.getChild("Hair01c2_1")
        this.Hair01c3_1 = this.Hair01c2_1.getChild("Hair01c3_1")
        this.Hair01c1 = this.Hair01.getChild("Hair01c1")
        this.Hair01c2 = this.Hair01c1.getChild("Hair01c2")
        this.Hair01c3 = this.Hair01c2.getChild("Hair01c3")
        this.Hair01b1_1 = this.Hair01.getChild("Hair01b1_1")
        this.Hair01b2_1 = this.Hair01b1_1.getChild("Hair01b2_1")
        this.Hair01b3_1 = this.Hair01b2_1.getChild("Hair01b3_1")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.Hair01d1 = this.Hair01.getChild("Hair01d1")
        this.Hair01d2 = this.Hair01d1.getChild("Hair01d2")
        this.Hair01d3 = this.Hair01d2.getChild("Hair01d3")
        this.Hair01a1 = this.Hair01.getChild("Hair01a1")
        this.Hair01a2 = this.Hair01a1.getChild("Hair01a2")
        this.Hair01a3 = this.Hair01a2.getChild("Hair01a3")
        this.Hat01 = this.Head.getChild("Hat01")
        this.Hat02 = this.Hat01.getChild("Hat02")
        this.Hat03 = this.Hat02.getChild("Hat03")
        this.Hair = this.Head.getChild("Hair")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.Cloth01a = this.Cloth01.getChild("Cloth01a")
        this.Cloth01b = this.Cloth01.getChild("Cloth01b")
        this.Cloth01d = this.Cloth01.getChild("Cloth01d")
        this.Cloth01c = this.Cloth01.getChild("Cloth01c")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.ArmLeft02a = this.ArmLeft02.getChild("ArmLeft02a")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        this.initFaceParts(this.GlowHead)
        this.buttDefaultY = this.Butt.y
        this.skirt01DefaultY = this.Skirt01.y
        this.skirt02DefaultY = this.Skirt02.y
        this.skirt03DefaultY = this.Skirt03.y
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
        val ctx = computePoseContext(entity, limbSwing, limbSwingAmount, ageInTicks, 0.14f)

        this.isDeadPose = false
        this.poseTranslateY = 0.0f
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
        Head.xRot += 0.11f

        applyBasePose(ctx)
        applySpecialPoseAdjustments(entity, ctx, ageInTicks)
        applyHairAndBoobAnimation(ctx, ageInTicks, limbSwing)

        syncGlowParts()
    }

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) return
        val showRigging = entity.getEquipFlag(EntityCruiserAtago.EQUIP_RIGGING)
        EquipBase.visible = showRigging
    }

    private fun applyDeadPose() {
        beginDeadPose(DEAD_TRANSLATE_Y)

        this.Head.xRot = 0.55f
        this.Head.yRot = -0.2f
        this.BodyMain.xRot = -0.7f
        this.BodyMain.yRot = -0.2618f
        this.BodyMain.zRot = -0.5236f
        this.Butt.xRot = -0.2618f
        this.Cloth01.xRot = 0.3f
        this.Skirt01.xRot = -0.2443f
        this.Skirt02.xRot = -0.0873f
        this.Skirt03!!.xRot = -0.0873f
        this.SkirtIn.xRot = -0.2618f
        this.ArmLeft01.xRot = -0.2618f
        this.ArmLeft01.yRot = 0.7f
        this.ArmLeft01.zRot = -0.5236f
        this.ArmLeft02.xRot = -2.1f
        this.ArmLeft02.yRot = 0.0f
        this.ArmLeft02.zRot = 0.0f
        this.ArmRight01.xRot = 0.7f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = 0.5236f
        this.ArmRight02.xRot = -1.45f
        this.ArmRight02.yRot = 0.0f
        this.ArmRight02.zRot = 0.0f
        this.LegLeft01.xRot = -0.79f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = -0.14f
        this.LegLeft02.xRot = 1.4f
        this.LegRight01.xRot = -0.7f
        this.LegRight01.yRot = -0.4363f
        this.LegRight01.zRot = 0.0f
        this.LegRight02.xRot = 0.7f
        this.Hair01!!.xRot = 0.35f
        this.Hair01.zRot = 0.0f
        this.Hair02!!.xRot = 0.2f
        this.Hair02.zRot = 0.0f
    }

    private fun applyBasePose(ctx: PoseContext) {
        val angleX = ctx.angleX

        Ahoke.yRot = angleX * 0.15f + 0.65f
        BodyMain.xRot = -0.1047f
        BodyMain.yRot = 0.0f
        BodyMain.zRot = 0.0f
        Butt.xRot = 0.35f
        Skirt01.xRot = -0.07f
        Skirt02.xRot = -0.052f
        if (Skirt03 != null) Skirt03.xRot = -0.052f

        ArmLeft01.xRot = ctx.angleAdd2 * 0.25f + 0.3f
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = angleX * 0.03f - 0.25f
        ArmLeft02.xRot = 0.0f
        ArmLeft02.zRot = 0.0f

        ArmRight01.xRot = ctx.angleAdd1 * 0.25f - 0.087f
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = -angleX * 0.03f + 0.25f
        ArmRight02.xRot = 0.0f
        ArmRight02.zRot = 0.0f

        LegLeft01.yRot = 0.0f
        LegLeft01.zRot = 0.0873f
        LegLeft02.xRot = 0.0f
        LegLeft02.yRot = 0.0f
        LegLeft02.zRot = 0.0f
        LegRight01.yRot = 0.0f
        LegRight01.zRot = -0.0873f
        LegRight02.xRot = 0.0f
        LegRight02.yRot = 0.0f
        LegRight02.zRot = 0.0f
    }

    private fun applySpecialPoseAdjustments(entity: T?, ctx: PoseContext, ageInTicks: Float) {
        var legLeftX = ctx.legAddLeft
        var legRightX = ctx.legAddRight

        val isPassenger = entity != null && entity.isPassenger
        val isCrouching = entity != null && entity.isCrouching
        val isSprinting = entity != null && entity.isSprinting
        val isLegacyEmote1 = entity != null && hasLegacyState(entity, 1, 4)
        val isLegacyEmote6 = entity != null && hasLegacyState(entity, 6, 1)
        val isLegacyEmote7 = entity != null && hasLegacyState(entity, 7, 4)
        var spcStand = false

        if (entity != null && !entity.getEquipFlag(EntityCruiserAtago.EQUIP_RIGGING)) {
            ArmLeft01.zRot += 0.1f
            ArmRight01.zRot -= 0.1f
        }

        if (isLegacyEmote1) {
            spcStand = true
            Head.yRot *= 0.25f
            ArmLeft01.xRot = -0.34906584f
            ArmLeft01.yRot = 0.0f
            ArmLeft01.zRot = 0.4553564f
            ArmLeft02.xRot = 0.0f
            ArmLeft02.yRot = 0.0f
            ArmLeft02.zRot = 1.0471976f
            ArmRight01.xRot = -0.5462881f
            ArmRight01.yRot = -0.2617994f
            ArmRight01.zRot = -0.13962634f
            ArmRight02.xRot = -2.5307274f
            ArmRight02.yRot = 0.0f
            ArmRight02.zRot = 0.0f
            ArmRight02.z = armRight02DefaultZ + (-0.32f * OFFSET_SCALE)
        } else if (isLegacyEmote7) {
            spcStand = true
            ArmLeft01.xRot = -3.14f
            ArmLeft01.yRot = 0.0f
            ArmLeft01.zRot = 0.52f
            ArmLeft02.xRot = 0.0f
            ArmLeft02.yRot = 0.0f
            ArmLeft02.zRot = 0.0f
            ArmLeft02.x = armLeft02DefaultX
            ArmLeft02.z = armLeft02DefaultZ
            ArmRight01.xRot = -3.14f
            ArmRight01.yRot = 0.0f
            ArmRight01.zRot = -0.52f
            ArmRight02.xRot = 0.0f
            ArmRight02.yRot = 0.0f
            ArmRight02.zRot = 0.0f
            ArmRight02.x = armRight02DefaultX
            ArmRight02.z = armRight02DefaultZ
        }

        if (isSprinting) {
            Head.xRot -= 0.25f
            BodyMain.xRot = 0.1f
            Skirt01.xRot = -0.1f
            Skirt02.xRot = -0.1885f
            if (Skirt03 != null) Skirt03.xRot = -0.2f
            ArmLeft01.xRot = 0.35f
            ArmLeft01.zRot = -0.5f
            ArmRight01.xRot = 0.35f
            ArmRight01.zRot = 0.5f
            legLeftX -= 0.2f
            legRightX -= 0.2f

            if (isLegacyEmote1) {
                ArmLeft01.xRot = -0.35f
                ArmLeft01.yRot = -1.7f - ctx.angleAdd2 * 0.5f
                ArmLeft01.zRot = 0.0f
                ArmLeft02.xRot = -2.4f
                ArmLeft02.yRot = 0.0f
                ArmLeft02.zRot = 0.0f
                ArmLeft02.x = armLeft02DefaultX
                ArmLeft02.y = armLeft02DefaultY
                ArmLeft02.z = armLeft02DefaultZ + (-0.315f * OFFSET_SCALE)
                ArmRight01.xRot = -0.35f
                ArmRight01.yRot = 1.7f + ctx.angleAdd1 * 0.5f
                ArmRight01.zRot = 0.0f
                ArmRight02.xRot = -2.4f
                ArmRight02.yRot = 0.0f
                ArmRight02.zRot = 0.0f
                ArmRight02.x = armRight02DefaultX
                ArmRight02.y = armRight02DefaultY
                ArmRight02.z = armRight02DefaultZ + (-0.315f * OFFSET_SCALE)
            }
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
            if (Skirt03 != null) Skirt03.xRot = -0.5f

            ArmLeft01.xRot = -0.6f
            ArmLeft01.zRot = 0.2618f
            ArmRight01.xRot = -0.6f
            ArmRight01.zRot = -0.2618f
            legLeftX -= 0.55f
            legRightX -= 0.55f
        }

        if (ctx.isSitting || isPassenger) {
            if (isLegacyEmote6) {
                this.poseTranslateY = 0.34f * 3.0f
                Head.xRot -= 0.91f
                BodyMain.xRot = 0.7f
                BodyMain.yRot = 0.0f
                BodyMain.zRot = 0.0f
                Skirt01.xRot = -0.24f
                Skirt02.xRot = -0.09f
                Skirt03!!.xRot = 0.21f
                SkirtIn.xRot = -0.26f
                ArmLeft01.xRot = -0.45f
                ArmLeft01.yRot = 0.0f
                ArmLeft01.zRot = 0.21f
                ArmRight01.xRot = -0.45f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = -0.21f
                legLeftX = -1.59f
                legRightX = -1.59f
                LegLeft01.yRot = 0.0f
                LegLeft01.zRot = 0.09f
                LegLeft02.xRot = 2.1f
                LegLeft02.yRot = 0.0f
                LegLeft02.zRot = 0.0f
                LegLeft02.z = legLeft02DefaultZ + (0.37f * OFFSET_SCALE)
                LegRight01.yRot = 0.0f
                LegRight01.zRot = -0.09f
                LegRight02.xRot = 2.1f
                LegRight02.yRot = 0.0f
                LegRight02.zRot = 0.0f
                LegRight02.z = legRight02DefaultZ + (0.37f * OFFSET_SCALE)
            } else if (isLegacyEmote1) {
                this.poseTranslateY = 0.58f * 3.0f
                Head.xRot = 0.55f
                Head.yRot = -0.2f
                BodyMain.xRot = -0.7f
                BodyMain.yRot = -0.2618f
                BodyMain.zRot = -0.5236f
                Butt.xRot = -0.2618f
                Cloth01.xRot = 0.3f
                Skirt01.xRot = -0.2443f
                Skirt02.xRot = -0.0873f
                Skirt03!!.xRot = -0.0873f
                SkirtIn.xRot = -0.2618f
                ArmLeft01.xRot = -0.2618f
                ArmLeft01.yRot = 0.7f
                ArmLeft01.zRot = -0.5236f
                ArmLeft02.xRot = -2.1f
                ArmLeft02.yRot = 0.0f
                ArmLeft02.zRot = 0.0f
                ArmLeft02.z = armLeft02DefaultZ + (-0.31f * OFFSET_SCALE)
                ArmRight01.xRot = 0.7f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = 0.5236f
                ArmRight02.xRot = -1.45f
                ArmRight02.yRot = 0.0f
                ArmRight02.zRot = 0.0f
                legLeftX = -0.79f
                legRightX = -0.7f
                LegLeft01.yRot = 0.0f
                LegLeft01.zRot = -0.14f
                LegLeft02.xRot = 1.4f
                LegRight01.yRot = -0.4363f
                LegRight01.zRot = 0.0f
                LegRight02.xRot = 0.7f
                if (Hair01 != null) Hair01.xRot = 0.35f
                if (Hair02 != null) Hair02.xRot = 0.2f
            } else {
                this.poseTranslateY = if (isPassenger) 0.3f * 3.0f else 0.45f * 3.0f
                Head.xRot -= 0.1f
                BodyMain.xRot = 0.0f
                Butt.xRot = -0.2f
                Butt.y = buttDefaultY + (-0.1f * OFFSET_SCALE)
                Skirt01.xRot = -0.07f
                Skirt01.y = skirt01DefaultY + (-0.05f * OFFSET_SCALE)
                Skirt02.xRot = -0.16f
                Skirt02.y = skirt02DefaultY + (-0.08f * OFFSET_SCALE)
                if (Skirt03 != null) {
                    Skirt03.xRot = -0.2f
                    Skirt03.y = skirt03DefaultY + (-0.1f * OFFSET_SCALE)
                }
                SkirtIn.xRot = -0.76f
                if (!spcStand) {
                    ArmLeft01.xRot = -0.4f
                    ArmLeft01.zRot = 0.2618f
                    ArmRight01.xRot = -0.4f
                    ArmRight01.zRot = -0.2618f
                }
                legLeftX = -0.65f
                legRightX = -0.65f
                LegLeft01.yRot = 0.1f
                LegLeft01.zRot = 0.0f
                LegLeft02.z = legLeft02DefaultZ + (0.375f * OFFSET_SCALE)
                LegLeft02.xRot = 2.45f
                LegLeft02.zRot = 0.0175f
                LegRight01.yRot = -0.1f
                LegRight01.zRot = 0.0f
                LegRight02.z = legRight02DefaultZ + (0.375f * OFFSET_SCALE)
                LegRight02.xRot = 2.45f
                LegRight02.zRot = -0.0175f
            }
        }

        if (entity != null && entity.attackTick > 30) {
            ArmLeft01.xRot = -Math.PI.toFloat()
            ArmLeft01.yRot = 0.0f
            ArmLeft01.zRot = 0.52f
            ArmLeft02.xRot = 0.0f
            ArmLeft02.yRot = 0.0f
            ArmLeft02.zRot = 0.0f
            ArmLeft02.x = armLeft02DefaultX
            ArmLeft02.z = armLeft02DefaultZ
            ArmRight01.xRot = -Math.PI.toFloat()
            ArmRight01.yRot = 0.0f
            ArmRight01.zRot = -0.52f
            ArmRight02.xRot = 0.0f
            ArmRight02.yRot = 0.0f
            ArmRight02.zRot = 0.0f
            ArmRight02.x = armRight02DefaultX
            ArmRight02.z = armRight02DefaultZ
        }

        LegLeft01.xRot = legLeftX
        LegRight01.xRot = legRightX

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
    }

    private fun applyHairAndBoobAnimation(ctx: PoseContext, ageInTicks: Float, limbSwing: Float) {
        val angleX = ctx.angleX
        val angleX1 = Mth.cos(ageInTicks * 0.1f + 0.3f + limbSwing * 0.5f)

        if (BoobL != null) BoobL.xRot = angleX * 0.06f - 0.8f
        if (BoobR != null) BoobR.xRot = angleX * 0.06f - 0.8f

        val headX = Head.xRot * -0.5f

        if (Hair01 != null) {
            Hair01.xRot = angleX * 0.03f + 0.21f + headX
            Hair01.zRot = 0.0f
        }
        if (Hair02 != null) {
            Hair02.xRot = -angleX1 * 0.04f + 0.26f + headX
            Hair02.zRot = 0.0f
        }

        if (Hair01a3 != null) Hair01a3.zRot = angleX * 0.2f + 1.05f
        if (Hair01b3 != null) Hair01b3.zRot = angleX * 0.15f + 1.31f
        if (Hair01c3 != null) Hair01c3.zRot = angleX * 0.1f + 1.05f
        if (Hair01d3 != null) Hair01d3.zRot = angleX * 0.2f - 1.05f

        if (Hair01b3_1 != null) Hair01b3_1.zRot = angleX * 0.15f - 1.31f
        if (Hair01c3_1 != null) Hair01c3_1.zRot = angleX * 0.1f - 1.05f
    }

    private fun resetOffsets() {
        Butt.y = buttDefaultY
        Skirt01.y = skirt01DefaultY
        Skirt02.y = skirt02DefaultY
        Skirt03!!.y = skirt03DefaultY
        Hair01!!.y = hair01DefaultY
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

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "cruiser_atago"), "main")

        private val DEAD_TRANSLATE_Y = deadY("ModelCruiserAtago")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelCruiserAtago")
        private const val OFFSET_SCALE = 16.0f

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
                PartPose.offsetAndRotation(-7.8f, -9.3f, -0.7f, -0.0873f, 0f, 0.2618f)
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
                PartPose.offsetAndRotation(2.5f, 7.8f, -2.4f, 0.0524f, 0f, 0f)
            )

            val EquipBag00 = BodyMain.addOrReplaceChild(
                "EquipBag00",
                CubeListBuilder.create().texOffs(32, 27).addBox(-3f, 0f, 0f, 6f, 14f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.9f, -10.9f, -0.7f, 0.2618f, 1.3963f, 0.0873f)
            )

            val EquipBag01 = EquipBag00.addOrReplaceChild(
                "EquipBag01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1.5f, 8f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.5f, 13.5f, 0.5f, 0f, 0f, 0.0873f)
            )

            val EquipBag02 = EquipBag01.addOrReplaceChild(
                "EquipBag02",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 1f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offset(5f, -1.9f, -0.5f)
            )

            val EquipBag03 = EquipBag01.addOrReplaceChild(
                "EquipBag03",
                CubeListBuilder.create().texOffs(6, 22).addBox(-1f, 0f, -1f, 2f, 3f, 2f, CubeDeformation(0f)),
                PartPose.offset(3f, -2.9f, 0f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(0, 35).mirror().addBox(-3.5f, 0f, 0f, 7f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.5f, -8.5f, -3.8f, -0.8727f, -0.0524f, 0.0873f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 47).addBox(-7.5f, 0f, -5.7f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.3f, 0.3491f, 0f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 84).addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.8f, 5.5f, -2.6f, -0.2793f, 0f, 0.0873f)
            )

            val ShoeL03 = LegLeft01.addOrReplaceChild(
                "ShoeL03",
                CubeListBuilder.create().texOffs(20, 33).addBox(0f, 0f, -2.2f, 1f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offset(2.9f, 8f, 0f)
            )

            val ShoeL04 = ShoeL03.addOrReplaceChild(
                "ShoeL04",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 1f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 3f, -0.7f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 63).addBox(-6f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(3f, 14f, -3f)
            )

            val ShoeL01 = LegLeft02.addOrReplaceChild(
                "ShoeL01",
                CubeListBuilder.create().texOffs(19, 63).addBox(0f, 0f, 0f, 4f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, 12.5f, 3.6f, -0.6981f, -0.1396f, -0.6981f)
            )

            val ShoeL02 = ShoeL01.addOrReplaceChild(
                "ShoeL02",
                CubeListBuilder.create().texOffs(24, 80).addBox(0f, -3f, 0f, 10f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, 3f, 0.1f, 0f, 0f, -0.6981f)
            )

            val Skirt01 = Butt.addOrReplaceChild(
                "Skirt01",
                CubeListBuilder.create().texOffs(128, 0).addBox(-8.5f, 0f, -8f, 17f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3f, 1.5f, -0.0698f, 0f, 0f)
            )

            val Skirt02 = Skirt01.addOrReplaceChild(
                "Skirt02",
                CubeListBuilder.create().texOffs(128, 16).addBox(-9f, 0f, -6f, 18f, 9f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4.5f, -2.7f, -0.0873f, 0f, 0f)
            )

            val Skirt03 = Skirt02.addOrReplaceChild(
                "Skirt03",
                CubeListBuilder.create().texOffs(128, 37).addBox(-10f, 0f, -6.5f, 20f, 11f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7.5f, 0f, 0.0911f, 0f, 0f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 84).mirror().addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.8f, 5.5f, -2.6f, -0.0873f, 0f, -0.0873f)
            )

            val ShoeL03_1 = LegRight01.addOrReplaceChild(
                "ShoeL03_1",
                CubeListBuilder.create().texOffs(20, 33).addBox(0f, 0f, -2.2f, 1f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3.9f, 8f, 0f)
            )

            val ShoeL04_1 = ShoeL03_1.addOrReplaceChild(
                "ShoeL04_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 1f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 3f, -0.7f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 63).mirror().addBox(0f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3f, 14f, -3f)
            )

            val ShoeR01 = LegRight02.addOrReplaceChild(
                "ShoeR01",
                CubeListBuilder.create().texOffs(19, 63).mirror().addBox(-4f, 0f, 0f, 4f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, 12.5f, 3.6f, -0.6981f, 0.1396f, 0.6981f)
            )

            val ShoeR02 = ShoeR01.addOrReplaceChild(
                "ShoeR02",
                CubeListBuilder.create().texOffs(24, 80).mirror()
                    .addBox(-10f, -3f, 0f, 10f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, 3f, 0.1f, 0f, 0f, 0.6981f)
            )

            val SkirtIn = Butt.addOrReplaceChild(
                "SkirtIn",
                CubeListBuilder.create().texOffs(74, 0).addBox(-8f, 0f, -5f, 16f, 5f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7.8f, -1.9f, -0.2618f, 0f, 0f)
            )

            val EquipBase = BodyMain.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val Equip00 = EquipBase.addOrReplaceChild(
                "Equip00",
                CubeListBuilder.create().texOffs(0, 0).addBox(-11.5f, -1f, -1.5f, 12f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3f, 3f, 0f, 1.5708f, 0f)
            )

            val EquipOut01 = Equip00.addOrReplaceChild(
                "EquipOut01",
                CubeListBuilder.create().texOffs(30, 0).addBox(0f, 0f, -0.5f, 12f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-11.2f, -1f, -0.2f, 0.2618f, -1.4835f, 0f)
            )

            val EquipOut02 = EquipOut01.addOrReplaceChild(
                "EquipOut02",
                CubeListBuilder.create().texOffs(33, 0).mirror().addBox(0f, 0f, -1f, 9f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(12f, 0f, 0.5f, 0f, 0.5236f, 0f)
            )

            val EquipOut03 = EquipOut02.addOrReplaceChild(
                "EquipOut03",
                CubeListBuilder.create().texOffs(36, 0).addBox(0f, 0f, -1f, 6f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(9f, 0f, 0f, 0f, 0.6981f, 0f)
            )

            val EquipOut04 = EquipOut03.addOrReplaceChild(
                "EquipOut04",
                CubeListBuilder.create().texOffs(36, 0).mirror().addBox(0f, 0f, -1f, 6f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, 0f, 0f, 0f, 0.6981f, 0f)
            )

            val EquipOut05 = EquipOut04.addOrReplaceChild(
                "EquipOut05",
                CubeListBuilder.create().texOffs(0, 28).addBox(0f, 0f, -1f, 1f, 6f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.5f, 2.5f, -0.5f, 0f, 0f, 0.3491f)
            )

            val EquipRIn01 = Equip00.addOrReplaceChild(
                "EquipRIn01",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, -1f, 7f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, -1f, -1f, -0.0873f, 1.2217f, 0f)
            )

            val EquipRIn02 = EquipRIn01.addOrReplaceChild(
                "EquipRIn02",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 8f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, 0f, -1f, 0f, -1.1345f, 0f)
            )

            val EquipRIn07 = EquipRIn02.addOrReplaceChild(
                "EquipRIn07",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 1f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8.5f, -0.7f, 0.5f, 0f, -0.5236f, 0f)
            )

            val EquipRIn09 = EquipRIn02.addOrReplaceChild(
                "EquipRIn09",
                CubeListBuilder.create().texOffs(6, 22).addBox(-1f, 0f, -1f, 2f, 4f, 2f, CubeDeformation(0f)),
                PartPose.offset(2.9f, -3.2f, 0.5f)
            )

            val EquipRIn03 = EquipRIn02.addOrReplaceChild(
                "EquipRIn03",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 4f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, 0f, 0f, 0f, -0.8727f, 0f)
            )

            val EquipRIn06a = EquipRIn03.addOrReplaceChild(
                "EquipRIn06a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.3f, -0.5f, 1.5f, 0f, -0.6981f, 0f)
            )

            val EquipRIn06b = EquipRIn06a.addOrReplaceChild(
                "EquipRIn06b",
                CubeListBuilder.create().texOffs(6, 22).addBox(-1f, -3f, -1f, 2f, 3f, 2f, CubeDeformation(0f)),
                PartPose.offset(-0.3f, 0f, 0f)
            )

            val EquipRIn04 = EquipRIn03.addOrReplaceChild(
                "EquipRIn04",
                CubeListBuilder.create().texOffs(0, 22).addBox(0f, 0f, -1f, 2f, 5f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.5f, 1.9f, 1.3f, 0f, -0.2618f, 0f)
            )

            val EquipRIn05 = EquipRIn04.addOrReplaceChild(
                "EquipRIn05",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, 0f, -1f, 7f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.5f, 4.7f, -0.4f, 0f, -0.2618f, 0.0873f)
            )

            val EquipRIn08 = EquipRIn02.addOrReplaceChild(
                "EquipRIn08",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(5.2f, -1.9f, 1.3f)
            )

            val EquipC01a = Equip00.addOrReplaceChild(
                "EquipC01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, -1.5f, -1.5f, 4f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offset(-13f, 0.5f, 0f)
            )

            val EquipC01b = EquipC01a.addOrReplaceChild(
                "EquipC01b",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -9f, -4.5f, 4f, 13f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5.9f, 0f, 0f, 0f, 0f, 0.0873f)
            )

            val EquipC01c = EquipC01b.addOrReplaceChild(
                "EquipC01c",
                CubeListBuilder.create().texOffs(30, 9).addBox(-1.5f, -5f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offset(2f, -8f, -2.2f)
            )

            val EquipC01d = EquipC01c.addOrReplaceChild(
                "EquipC01d",
                CubeListBuilder.create().texOffs(14, 22).addBox(-1f, 0f, -1f, 2f, 10f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.9f, 0f)
            )

            val EquipC01e = EquipC01b.addOrReplaceChild(
                "EquipC01e",
                CubeListBuilder.create().texOffs(30, 9).addBox(-1.5f, -5f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offset(2f, -8f, 2.2f)
            )

            val EquipC01f = EquipC01e.addOrReplaceChild(
                "EquipC01f",
                CubeListBuilder.create().texOffs(14, 22).addBox(-1f, 0f, -1f, 2f, 10f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.9f, 0f)
            )

            val EquipOut01_1 = Equip00.addOrReplaceChild(
                "EquipOut01_1",
                CubeListBuilder.create().texOffs(30, 0).addBox(0f, 0f, -0.5f, 12f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-11.2f, -1f, 0.2f, -0.2618f, 1.4835f, 0f)
            )

            val EquipOut02_1 = EquipOut01_1.addOrReplaceChild(
                "EquipOut02_1",
                CubeListBuilder.create().texOffs(33, 0).mirror().addBox(0f, 0f, 0f, 9f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(12f, 0f, -0.5f, 0f, -0.5236f, 0f)
            )

            val EquipOut03_1 = EquipOut02_1.addOrReplaceChild(
                "EquipOut03_1",
                CubeListBuilder.create().texOffs(36, 0).addBox(0f, 0f, 0f, 6f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(9f, 0f, 0f, 0f, -0.6981f, 0f)
            )

            val EquipOut04_1 = EquipOut03_1.addOrReplaceChild(
                "EquipOut04_1",
                CubeListBuilder.create().texOffs(36, 0).mirror().addBox(0f, 0f, 0f, 6f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, 0f, 0f, 0f, -0.6981f, 0f)
            )

            val EquipOut05_1 = EquipOut04_1.addOrReplaceChild(
                "EquipOut05_1",
                CubeListBuilder.create().texOffs(0, 28).addBox(0f, 0f, -1f, 1f, 6f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.5f, 2.5f, 0.5f, 0f, 0f, 0.3491f)
            )

            val EquipLIn01 = Equip00.addOrReplaceChild(
                "EquipLIn01",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 7f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, -1f, 1f, 0.0873f, -1.2217f, 0f)
            )

            val EquipLIn02 = EquipLIn01.addOrReplaceChild(
                "EquipLIn02",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, -1f, 8f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, 0f, 1f, 0f, 1.1345f, 0f)
            )

            val EquipLIn09 = EquipLIn02.addOrReplaceChild(
                "EquipLIn09",
                CubeListBuilder.create().texOffs(6, 22).addBox(-1f, 0f, -1f, 2f, 4f, 2f, CubeDeformation(0f)),
                PartPose.offset(2.9f, -3.2f, -0.5f)
            )

            val EquipLIn08 = EquipLIn02.addOrReplaceChild(
                "EquipLIn08",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(5.2f, -1.9f, -1.3f)
            )

            val EquipLIn03 = EquipLIn02.addOrReplaceChild(
                "EquipLIn03",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, -1f, 4f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, 0f, 0f, 0f, 0.8727f, 0f)
            )

            val EquipLIn04 = EquipLIn03.addOrReplaceChild(
                "EquipLIn04",
                CubeListBuilder.create().texOffs(0, 22).addBox(0f, 0f, 0f, 2f, 5f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.5f, 1.9f, -1.3f, 0f, 0.2618f, 0f)
            )

            val EquipLIn05 = EquipLIn04.addOrReplaceChild(
                "EquipLIn05",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, 0f, 0f, 7f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.5f, 4.7f, 0.4f, 0f, 0.2618f, 0.0873f)
            )

            val EquipLIn06a = EquipLIn03.addOrReplaceChild(
                "EquipLIn06a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.3f, -0.5f, -1.5f, 0f, 0.6981f, 0f)
            )

            val EquipLIn06b = EquipLIn06a.addOrReplaceChild(
                "EquipLIn06b",
                CubeListBuilder.create().texOffs(6, 22).addBox(-1f, -3f, -1f, 2f, 3f, 2f, CubeDeformation(0f)),
                PartPose.offset(-0.3f, 0f, 0f)
            )

            val EquipLIn07 = EquipLIn02.addOrReplaceChild(
                "EquipLIn07",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 1f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8.5f, -0.7f, -0.5f, 0f, 0.5236f, 0f)
            )

            val EquipCannonBase = EquipBase.addOrReplaceChild(
                "EquipCannonBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 5f, 0.2618f, 0f, 0f)
            )

            val EquipC01a_2 = EquipCannonBase.addOrReplaceChild(
                "EquipC01a_2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, -1.5f, -1.5f, 4f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offset(-20f, 5f, 0f)
            )

            val EquipC01b_2 = EquipC01a_2.addOrReplaceChild(
                "EquipC01b_2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, -9f, -4.5f, 4f, 13f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.9f, 0f, 0f, 0f, 0f, -0.0873f)
            )

            val EquipC01c_2 = EquipC01b_2.addOrReplaceChild(
                "EquipC01c_2",
                CubeListBuilder.create().texOffs(30, 9).addBox(-1.5f, -5f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offset(-2f, -8f, -2.2f)
            )

            val EquipC01d_2 = EquipC01c_2.addOrReplaceChild(
                "EquipC01d_2",
                CubeListBuilder.create().texOffs(14, 22).addBox(-1f, 0f, -1f, 2f, 10f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.9f, 0f)
            )

            val EquipC01e_2 = EquipC01b_2.addOrReplaceChild(
                "EquipC01e_2",
                CubeListBuilder.create().texOffs(30, 9).addBox(-1.5f, -5f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offset(-2f, -8f, 2.2f)
            )

            val EquipC01f_2 = EquipC01e_2.addOrReplaceChild(
                "EquipC01f_2",
                CubeListBuilder.create().texOffs(14, 22).addBox(-1f, 0f, -1f, 2f, 10f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.9f, 0f)
            )

            val EquipC01a_1 = EquipCannonBase.addOrReplaceChild(
                "EquipC01a_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, -1.5f, -1.5f, 4f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offset(20f, 5f, 0f)
            )

            val EquipC01b_1 = EquipC01a_1.addOrReplaceChild(
                "EquipC01b_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -9f, -4.5f, 4f, 13f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.9f, 0f, 0f, 0f, 0f, 0.0873f)
            )

            val EquipC01c_1 = EquipC01b_1.addOrReplaceChild(
                "EquipC01c_1",
                CubeListBuilder.create().texOffs(30, 9).addBox(-1.5f, -5f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offset(2f, -8f, -2.2f)
            )

            val EquipC01d_1 = EquipC01c_1.addOrReplaceChild(
                "EquipC01d_1",
                CubeListBuilder.create().texOffs(14, 22).addBox(-1f, 0f, -1f, 2f, 10f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.9f, 0f)
            )

            val EquipC01e_1 = EquipC01b_1.addOrReplaceChild(
                "EquipC01e_1",
                CubeListBuilder.create().texOffs(30, 9).addBox(-1.5f, -5f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offset(2f, -8f, 2.2f)
            )

            val EquipC01f_1 = EquipC01e_1.addOrReplaceChild(
                "EquipC01f_1",
                CubeListBuilder.create().texOffs(14, 22).addBox(-1f, 0f, -1f, 2f, 10f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.9f, 0f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(0, 35).addBox(-3.5f, 0f, 0f, 7f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.5f, -8.5f, -3.8f, -0.8727f, 0.0524f, -0.0873f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(98, 23).addBox(-3.5f, -2f, -4.9f, 7f, 2f, 8f, CubeDeformation(0f)),
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
                CubeListBuilder.create().texOffs(50, 40).addBox(-8f, 0f, -8f, 16f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8.5f, 10f, 0.2094f, 0f, 0f)
            )

            val Hair01b1 = Hair01.addOrReplaceChild(
                "Hair01b1",
                CubeListBuilder.create().texOffs(128, 73).mirror().addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.5f, 1f, -4.5f, 0.2618f, -0.8727f, -0.6981f)
            )

            val Hair01b2 = Hair01b1.addOrReplaceChild(
                "Hair01b2",
                CubeListBuilder.create().texOffs(128, 79).mirror().addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, 0f, 0f, 0f, -0.2618f)
            )

            val Hair01b3 = Hair01b2.addOrReplaceChild(
                "Hair01b3",
                CubeListBuilder.create().texOffs(128, 85).addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, 0f, 0f, 0f, 1.309f)
            )

            val Hair01c1_1 = Hair01.addOrReplaceChild(
                "Hair01c1_1",
                CubeListBuilder.create().texOffs(128, 73).addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.5f, 1.5f, -4f, 0.6981f, 0.7854f, 0.9599f)
            )

            val Hair01c2_1 = Hair01c1_1.addOrReplaceChild(
                "Hair01c2_1",
                CubeListBuilder.create().texOffs(128, 79).addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, 0f, 0f, 0f, -0.2618f)
            )

            val Hair01c3_1 = Hair01c2_1.addOrReplaceChild(
                "Hair01c3_1",
                CubeListBuilder.create().texOffs(128, 85).mirror().addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, 0f, 0f, 0f, -1.0472f)
            )

            val Hair01c1 = Hair01.addOrReplaceChild(
                "Hair01c1",
                CubeListBuilder.create().texOffs(128, 73).mirror().addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.5f, 1f, -4f, 0.6981f, -0.7854f, -0.9599f)
            )

            val Hair01c2 = Hair01c1.addOrReplaceChild(
                "Hair01c2",
                CubeListBuilder.create().texOffs(128, 79).mirror().addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, 0f, 0f, 0f, 0.2618f)
            )

            val Hair01c3 = Hair01c2.addOrReplaceChild(
                "Hair01c3",
                CubeListBuilder.create().texOffs(128, 85).addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, 0f, 0f, 0f, 1.0472f)
            )

            val Hair01b1_1 = Hair01.addOrReplaceChild(
                "Hair01b1_1",
                CubeListBuilder.create().texOffs(128, 73).addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.5f, 1f, -4.5f, 0.2618f, 0.8727f, 0.6981f)
            )

            val Hair01b2_1 = Hair01b1_1.addOrReplaceChild(
                "Hair01b2_1",
                CubeListBuilder.create().texOffs(128, 79).addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, 0f, 0f, 0f, 0.2618f)
            )

            val Hair01b3_1 = Hair01b2_1.addOrReplaceChild(
                "Hair01b3_1",
                CubeListBuilder.create().texOffs(128, 85).mirror().addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, 0f, 0f, 0f, -1.309f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(46, 22).addBox(-8.5f, 0f, -8.9f, 17f, 9f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.5f, 0f, 0.2618f, 0f, 0f)
            )

            val Hair01d1 = Hair01.addOrReplaceChild(
                "Hair01d1",
                CubeListBuilder.create().texOffs(128, 73).addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.6f, 0f, -5.5f, -0.0873f, 1.0472f, 0.5236f)
            )

            val Hair01d2 = Hair01d1.addOrReplaceChild(
                "Hair01d2",
                CubeListBuilder.create().texOffs(128, 79).addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, 0f, 0f, 0f, 0.3491f)
            )

            val Hair01d3 = Hair01d2.addOrReplaceChild(
                "Hair01d3",
                CubeListBuilder.create().texOffs(128, 85).mirror().addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, 0f, 0f, 0f, -1.0472f)
            )

            val Hair01a1 = Hair01.addOrReplaceChild(
                "Hair01a1",
                CubeListBuilder.create().texOffs(128, 73).mirror().addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.6f, 0f, -5.5f, -0.0873f, -1.0472f, -0.5236f)
            )

            val Hair01a2 = Hair01a1.addOrReplaceChild(
                "Hair01a2",
                CubeListBuilder.create().texOffs(128, 79).mirror().addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, 0f, 0f, 0f, -0.3491f)
            )

            val Hair01a3 = Hair01a2.addOrReplaceChild(
                "Hair01a3",
                CubeListBuilder.create().texOffs(128, 85).addBox(0f, 0f, -2f, 0f, 6f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, 0f, 0f, 0f, 1.0472f)
            )

            val Hat01 = Head.addOrReplaceChild(
                "Hat01",
                CubeListBuilder.create().texOffs(24, 18).addBox(-3.5f, 0f, -3.5f, 7f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.8f, -14.7f, 3.8f, -0.2618f, 0f, 0.1745f)
            )

            val Hat02 = Hat01.addOrReplaceChild(
                "Hat02",
                CubeListBuilder.create().texOffs(49, 11).addBox(-4f, 0f, -4f, 8f, 3f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -3f, 0f)
            )

            val Hat03 = Hat02.addOrReplaceChild(
                "Hat03",
                CubeListBuilder.create().texOffs(42, 7).addBox(0f, 0f, -1f, 0f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.6f, 1.6f, 2f, 0.2618f, 0.1396f, -0.5236f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 77).addBox(-8f, -8f, -7.4f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0.1f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(106, 31).addBox(0f, -6f, -10.5f, 0f, 11f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.5f, -7f, -6f, 0.2094f, 0.6981f, 0f)
            )

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(52, 56).addBox(-8.5f, 0f, 0f, 17f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -7f)
            )

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -10f, 0f)
            )

            val Cloth01a = Cloth01.addOrReplaceChild(
                "Cloth01a",
                CubeListBuilder.create().texOffs(0, 65).addBox(-2f, 0f, 0f, 2f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.5f, -5f, -0.8727f, 0.5236f, 0.1745f)
            )

            val Cloth01b = Cloth01.addOrReplaceChild(
                "Cloth01b",
                CubeListBuilder.create().texOffs(0, 65).addBox(0f, 0f, 0f, 2f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.5f, -5f, -0.8727f, -0.5236f, -0.1745f)
            )

            val Cloth01d = Cloth01.addOrReplaceChild(
                "Cloth01d",
                CubeListBuilder.create().texOffs(0, 84).addBox(0f, 0f, 0f, 2f, 4f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.4f, -5f, -0.5934f, -0.2618f, 0f)
            )

            val Cloth01c = Cloth01.addOrReplaceChild(
                "Cloth01c",
                CubeListBuilder.create().texOffs(0, 84).addBox(-2f, 0f, 0f, 2f, 4f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.4f, -5f, -0.6109f, 0.2618f, 0f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(24, 84).addBox(-2f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.3f, -0.7f, 0.3491f, 0f, -0.2618f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(24, 63).addBox(-5f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 11f, 2.5f)
            )

            val ArmLeft02a = ArmLeft02.addOrReplaceChild(
                "ArmLeft02a",
                CubeListBuilder.create().texOffs(104, 33).addBox(-3f, 0f, -3f, 6f, 3f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.5f, 7.8f, -2.4f, 0.0524f, 0f, 0f)
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

            return LayerDefinition.create(meshdefinition, 256, 128)
        }
    }
}
