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
import org.trp.shincolle.entity.EntityTransportWa
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelTransportWa<T : EntityShipBase>(root: ModelPart) : ShincolleShipModel<T>() {

    private val BodyMain: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val Butt: ModelPart
    private val Head: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val Cloth03: ModelPart
    private val EquipBase: ModelPart
    private val Cloth01b: ModelPart
    private val Cloth01a: ModelPart
    private val Cloth2b: ModelPart
    private val Cloth2a: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft01: ModelPart
    private val LegRight02: ModelPart
    private val LegLeft02: ModelPart
    private val ClothLeg: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val EquipHeadBase: ModelPart
    private val Ahoke: ModelPart
    private val HairU01: ModelPart
    private val Hair01: ModelPart
    private val ClothHead: ModelPart
    private val EquipHead01: ModelPart
    private val EquipHead02: ModelPart
    private val EquipHead03: ModelPart
    private val EquipHead04: ModelPart
    private val EquipHead05: ModelPart
    private val EquipHead06: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmRight02: ModelPart
    private val Cloth04: ModelPart
    private val Cloth00a: ModelPart
    private val Cloth00b: ModelPart
    private val Cloth00c: ModelPart
    private val Cloth00d: ModelPart
    private val EquipBack01a: ModelPart
    private val EquipBack01b: ModelPart
    private val EquipBack01c: ModelPart
    private val EquipBack01d: ModelPart
    private val EquipBack01e: ModelPart
    private val EquipBack01f: ModelPart
    private val EquipBack01g: ModelPart
    private val EquipBack01h: ModelPart
    private val EquipBack01i: ModelPart
    private val EquipBack01j: ModelPart
    private val EquipBack01k: ModelPart
    private val EquipBack01l: ModelPart
    private val EquipBack01m: ModelPart
    private val EquipBack01n: ModelPart
    private val EquipBack01o: ModelPart
    private val EquipBack01p: ModelPart
    private val EquipBack01q: ModelPart
    private val EquipBack01r: ModelPart
    private val EquipTubeR01: ModelPart
    private val EquipTubeL01: ModelPart
    private val EquipBack01s: ModelPart
    private val EquipBack01t: ModelPart
    private val EquipBack01u: ModelPart
    private val EquipBack01v: ModelPart
    private val EquipBack01w: ModelPart
    private val EquipBack01x: ModelPart
    private val EquipBack01y: ModelPart
    private val EquipBack01z: ModelPart
    private val EquipBack01za: ModelPart
    private val EquipBack01zb: ModelPart
    private val EquipBack01zc: ModelPart
    private val EquipBack01zd: ModelPart
    private val EquipTubeR02: ModelPart
    private val EquipTubeR03: ModelPart
    private val EquipTubeL02: ModelPart
    private val EquipTubeL03: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowBodyMain2: ModelPart
    private val GlowHead: ModelPart
    private val GlowEquipBase: ModelPart
    private val GlowEquipTubeL01: ModelPart
    private val GlowEquipTubeL02: ModelPart
    private val GlowEquipTubeR01: ModelPart
    private val GlowEquipTubeR02: ModelPart
    private val buttDefaultXRot: Float
    private val buttDefaultY: Float
    private val buttDefaultZ: Float
    private val cloth03DefaultXRot: Float
    private val cloth04DefaultXRot: Float
    private val armLeft01DefaultXRot: Float
    private val armLeft01DefaultYRot: Float
    private val armLeft01DefaultZRot: Float
    private val armRight01DefaultXRot: Float
    private val armRight01DefaultYRot: Float
    private val armRight01DefaultZRot: Float
    private val legLeft01DefaultXRot: Float
    private val legLeft01DefaultYRot: Float
    private val legLeft01DefaultZRot: Float
    private val legRight01DefaultXRot: Float
    private val legRight01DefaultYRot: Float
    private val legRight01DefaultZRot: Float
    private val equipBaseDefaultXRot: Float
    private val equipBaseDefaultY: Float
    private val equipBaseDefaultZ: Float
    private val equipTubeL01DefaultXRot: Float
    private val equipTubeR01DefaultXRot: Float

    protected override val bodyMain: ModelPart get() = BodyMain
    protected override val neck: ModelPart get() = BodyMain
    protected override val head: ModelPart get() = Head
    protected override val glowBodyMain: ModelPart? get() = GlowBodyMain
    protected override val glowNeck: ModelPart? get() = null
    protected override val glowHead: ModelPart? get() = GlowHead

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Head = this.BodyMain.getChild("Head")
        this.EquipHeadBase = this.Head.getChild("EquipHeadBase")
        this.EquipHead03 = this.EquipHeadBase.getChild("EquipHead03")
        this.EquipHead04 = this.EquipHeadBase.getChild("EquipHead04")
        this.EquipHead05 = this.EquipHeadBase.getChild("EquipHead05")
        this.EquipHead06 = this.EquipHeadBase.getChild("EquipHead06")
        this.EquipHead02 = this.EquipHeadBase.getChild("EquipHead02")
        this.EquipHead01 = this.EquipHeadBase.getChild("EquipHead01")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.ClothHead = this.HairMain.getChild("ClothHead")
        this.Hair = this.Head.getChild("Hair")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.Cloth01b = this.BodyMain.getChild("Cloth01b")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.ClothLeg = this.LegLeft01.getChild("ClothLeg")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.Cloth01a = this.BodyMain.getChild("Cloth01a")
        this.EquipBase = this.BodyMain.getChild("EquipBase")
        this.EquipBack01t = this.EquipBase.getChild("EquipBack01t")
        this.EquipBack01m = this.EquipBase.getChild("EquipBack01m")
        this.EquipBack01v = this.EquipBase.getChild("EquipBack01v")
        this.EquipBack01j = this.EquipBase.getChild("EquipBack01j")
        this.EquipBack01q = this.EquipBase.getChild("EquipBack01q")
        this.EquipBack01x = this.EquipBase.getChild("EquipBack01x")
        this.EquipBack01zb = this.EquipBase.getChild("EquipBack01zb")
        this.EquipBack01n = this.EquipBase.getChild("EquipBack01n")
        this.EquipBack01a = this.EquipBase.getChild("EquipBack01a")
        this.EquipBack01p = this.EquipBase.getChild("EquipBack01p")
        this.EquipBack01i = this.EquipBase.getChild("EquipBack01i")
        this.EquipBack01d = this.EquipBase.getChild("EquipBack01d")
        this.EquipBack01w = this.EquipBase.getChild("EquipBack01w")
        this.EquipBack01o = this.EquipBase.getChild("EquipBack01o")
        this.EquipTubeR01 = this.EquipBase.getChild("EquipTubeR01")
        this.EquipTubeR02 = this.EquipTubeR01.getChild("EquipTubeR02")
        this.EquipBack01g = this.EquipBase.getChild("EquipBack01g")
        this.EquipTubeL01 = this.EquipBase.getChild("EquipTubeL01")
        this.EquipTubeL02 = this.EquipTubeL01.getChild("EquipTubeL02")
        this.EquipBack01zc = this.EquipBase.getChild("EquipBack01zc")
        this.EquipBack01b = this.EquipBase.getChild("EquipBack01b")
        this.EquipBack01e = this.EquipBase.getChild("EquipBack01e")
        this.EquipBack01h = this.EquipBase.getChild("EquipBack01h")
        this.EquipBack01s = this.EquipBase.getChild("EquipBack01s")
        this.EquipBack01r = this.EquipBase.getChild("EquipBack01r")
        this.EquipBack01f = this.EquipBase.getChild("EquipBack01f")
        this.EquipBack01k = this.EquipBase.getChild("EquipBack01k")
        this.EquipBack01l = this.EquipBase.getChild("EquipBack01l")
        this.EquipBack01za = this.EquipBase.getChild("EquipBack01za")
        this.EquipBack01z = this.EquipBase.getChild("EquipBack01z")
        this.EquipBack01zd = this.EquipBase.getChild("EquipBack01zd")
        this.EquipBack01u = this.EquipBase.getChild("EquipBack01u")
        this.EquipBack01y = this.EquipBase.getChild("EquipBack01y")
        this.EquipBack01c = this.EquipBase.getChild("EquipBack01c")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.Cloth03 = this.BodyMain.getChild("Cloth03")
        this.Cloth00b = this.Cloth03.getChild("Cloth00b")
        this.Cloth04 = this.Cloth03.getChild("Cloth04")
        this.Cloth00d = this.Cloth03.getChild("Cloth00d")
        this.Cloth00a = this.Cloth03.getChild("Cloth00a")
        this.Cloth00c = this.Cloth03.getChild("Cloth00c")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.Cloth2a = this.BoobL.getChild("Cloth2a")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.Cloth2b = this.BoobR.getChild("Cloth2b")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowHead = this.GlowBodyMain.getChild("GlowHead")
        initFaceParts(this.GlowHead)
        this.GlowBodyMain2 = root.getChild("GlowBodyMain2")
        this.GlowEquipBase = this.GlowBodyMain2.getChild("GlowEquipBase")
        this.GlowEquipTubeL01 = this.GlowEquipBase.getChild("GlowEquipTubeL01")
        this.GlowEquipTubeL02 = this.GlowEquipTubeL01.getChild("GlowEquipTubeL02")
        this.EquipTubeL03 = this.GlowEquipTubeL02.getChild("EquipTubeL03")
        this.GlowEquipTubeR01 = this.GlowEquipBase.getChild("GlowEquipTubeR01")
        this.GlowEquipTubeR02 = this.GlowEquipTubeR01.getChild("GlowEquipTubeR02")
        this.EquipTubeR03 = this.GlowEquipTubeR02.getChild("EquipTubeR03")
        this.buttDefaultXRot = this.Butt.xRot
        this.buttDefaultY = this.Butt.y
        this.buttDefaultZ = this.Butt.z
        this.cloth03DefaultXRot = this.Cloth03.xRot
        this.cloth04DefaultXRot = this.Cloth04.xRot
        this.armLeft01DefaultXRot = this.ArmLeft01.xRot
        this.armLeft01DefaultYRot = this.ArmLeft01.yRot
        this.armLeft01DefaultZRot = this.ArmLeft01.zRot
        this.armRight01DefaultXRot = this.ArmRight01.xRot
        this.armRight01DefaultYRot = this.ArmRight01.yRot
        this.armRight01DefaultZRot = this.ArmRight01.zRot
        this.legLeft01DefaultXRot = this.LegLeft01.xRot
        this.legLeft01DefaultYRot = this.LegLeft01.yRot
        this.legLeft01DefaultZRot = this.LegLeft01.zRot
        this.legRight01DefaultXRot = this.LegRight01.xRot
        this.legRight01DefaultYRot = this.LegRight01.yRot
        this.legRight01DefaultZRot = this.LegRight01.zRot
        this.equipBaseDefaultXRot = this.EquipBase.xRot
        this.equipBaseDefaultY = this.EquipBase.y
        this.equipBaseDefaultZ = this.EquipBase.z
        this.equipTubeL01DefaultXRot = this.EquipTubeL01.xRot
        this.equipTubeR01DefaultXRot = this.EquipTubeR01.xRot
    }

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        resetPoseState()
        this.resetOffsets()
        this.applyEquipVisibility(entity)
        this.applyFaceAndMouth(entity)

        val inDeadPose = entity != null && entity.isInDeadPose

        if (inDeadPose) {
            this.applyDeadPose(entity)
            this.syncGlowParts()
            return
        }

        this.applyBasePose(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch)
        this.applySpecialPoseAdjustments(entity, limbSwing, limbSwingAmount, ageInTicks)
        this.syncGlowParts()
    }

    private fun resetOffsets() {
        this.isDeadPose = false
        this.isSittingPose = false
        this.poseTranslateY = 0.0f

        this.Butt.xRot = this.buttDefaultXRot
        this.Butt.y = this.buttDefaultY
        this.Butt.z = this.buttDefaultZ
        this.Cloth03.xRot = this.cloth03DefaultXRot
        this.Cloth04.xRot = this.cloth04DefaultXRot
        this.ArmLeft01.xRot = this.armLeft01DefaultXRot
        this.ArmLeft01.yRot = this.armLeft01DefaultYRot
        this.ArmLeft01.zRot = this.armLeft01DefaultZRot
        this.ArmRight01.xRot = this.armRight01DefaultXRot
        this.ArmRight01.yRot = this.armRight01DefaultYRot
        this.ArmRight01.zRot = this.armRight01DefaultZRot
        this.LegLeft01.xRot = this.legLeft01DefaultXRot
        this.LegLeft01.yRot = this.legLeft01DefaultYRot
        this.LegLeft01.zRot = this.legLeft01DefaultZRot
        this.LegRight01.xRot = this.legRight01DefaultXRot
        this.LegRight01.yRot = this.legRight01DefaultYRot
        this.LegRight01.zRot = this.legRight01DefaultZRot
        this.EquipBase.xRot = this.equipBaseDefaultXRot
        this.EquipBase.y = this.equipBaseDefaultY
        this.EquipBase.z = this.equipBaseDefaultZ
        this.EquipTubeL01.xRot = this.equipTubeL01DefaultXRot
        this.EquipTubeR01.xRot = this.equipTubeR01DefaultXRot
    }

    private fun applyEquipVisibility(entity: T?) {
        val showEquipBase = entity!!.getEquipFlag(EntityTransportWa.EQUIP_BASE)
        val showLeg = entity.getEquipFlag(EntityTransportWa.EQUIP_LEG)
        val showHeadBase = entity.getEquipFlag(EntityTransportWa.EQUIP_HEAD_BASE)
        val showEquipBaseWithLeg = showEquipBase || !showLeg

        this.EquipBase.visible = showEquipBaseWithLeg
        this.GlowEquipBase.visible = showEquipBaseWithLeg
        this.LegLeft01.visible = showLeg
        this.LegRight01.visible = showLeg
        this.EquipHeadBase.visible = showHeadBase
        this.Ahoke.visible = !showHeadBase
    }

    private fun applyDeadPose(entity: T?) {
        beginDeadPose(DEAD_TRANSLATE_Y)
        val showLeg = entity != null && entity.getEquipFlag(EntityTransportWa.EQUIP_LEG)

        this.Head.xRot = 0.3f
        this.Head.yRot = 0.0f
        this.BoobL.xRot = -0.75f
        this.BoobR.xRot = -0.75f
        this.Ahoke.yRot = 0.7f
        this.BodyMain.xRot = 2.8f
        this.Cloth03.xRot = 0.17f
        this.Cloth04.xRot = -0.8f
        this.Butt.xRot = if (showLeg) -0.8f else -1.1f
        this.Butt.z = this.buttDefaultZ + ((if (showLeg) 0.0f else 0.1f) * OFFSET_SCALE)

        this.ArmLeft01.xRot = -0.35f
        this.ArmLeft01.zRot = -2.6f
        this.ArmRight01.xRot = -0.35f
        this.ArmRight01.zRot = 2.6f

        this.LegLeft01.xRot = if (showLeg) -1.0f else -0.24f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.1047f
        this.LegRight01.xRot = if (showLeg) -0.9f else -0.14f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.1047f

        this.EquipBase.y = this.equipBaseDefaultY + ((if (showLeg) 0.2f else 0.45f) * OFFSET_SCALE)
        this.EquipBase.z = this.equipBaseDefaultZ + ((if (showLeg) -0.45f else -0.85f) * OFFSET_SCALE)
        this.EquipBase.xRot = if (showLeg) -2.2f else -3.1f
        this.EquipTubeL01.xRot = if (showLeg) 0.1f else -0.3f
        this.EquipTubeR01.xRot = if (showLeg) 0.1f else -0.3f
    }

    private fun applyBasePose(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val angleX = Mth.cos(ageInTicks * 0.08f + limbSwing * 0.25f)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount

        if (entity!!.shipDepth > 0.0) {
            this.poseTranslateY = angleX * 0.05f + 0.025f
        }

        val addk1 = angleAdd1 * 0.5f - 0.24f
        val addk2 = angleAdd2 * 0.5f - 0.14f

        this.Head.xRot = headPitch * (Math.PI.toFloat() / 180f) + 0.1047f
        this.Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)
        this.Head.zRot = if (entity != null) entity.getHeadTiltAngle(ageInTicks) else 0.0f

        this.BoobL.xRot = angleX * 0.05f - 0.75f
        this.BoobR.xRot = angleX * 0.05f - 0.75f
        this.Ahoke.yRot = angleX * 0.25f + 0.7f

        this.BodyMain.xRot = -0.1047f
        this.Butt.xRot = 0.3142f
        this.Cloth03.xRot = 0.1745f
        this.Cloth04.xRot = angleX * 0.05f - 0.15f

        this.ArmLeft01.xRot = angleAdd2 * 0.25f + 0.21f
        this.ArmLeft01.zRot = angleX * 0.03f - 0.21f
        this.ArmRight01.xRot = angleAdd1 * 0.25f + 0.05f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = -angleX * 0.03f + 0.21f

        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.1047f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.1047f

        this.EquipBase.xRot = 0.05236f
        this.EquipTubeL01.xRot = angleX * 0.08f - 0.35f
        this.EquipTubeR01.xRot = -angleX * 0.08f - 0.35f

        this.LegLeft01.xRot = addk1
        this.LegRight01.xRot = addk2
    }

    private fun applySpecialPoseAdjustments(entity: T?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        val hideLeg = entity != null && !entity.getEquipFlag(EntityTransportWa.EQUIP_LEG)
        val isPassenger = entity!!.isPassenger
        val isCrouching = entity.isCrouching
        val isSprinting = if (entity != null) entity.isSprinting else limbSwingAmount > 0.9f
        val isSitting = entity.isInSittingPose || (isPassenger && entity.vehicle !is EntityMountBase)
        val sitSwing = Mth.cos(ageInTicks * 0.5f) * 0.5f

        if (hideLeg) {
            this.Cloth04.xRot += 0.23f
            this.Butt.xRot = 0.7f
            this.Butt.y = this.buttDefaultY + (-0.1f * OFFSET_SCALE)
            this.Butt.z = this.buttDefaultZ + (-0.05f * OFFSET_SCALE)
            this.ArmLeft01.xRot += 0.2f
            this.ArmLeft01.zRot -= 0.3f
            this.ArmRight01.xRot += 0.2f
            this.ArmRight01.zRot += 0.3f
            this.EquipBase.xRot = -0.4f
            this.EquipTubeL01.xRot += 0.35f
            this.EquipTubeR01.xRot += 0.35f
        }

        if (isSprinting) {
            this.poseTranslateY += 0.1f
            this.Head.xRot -= 0.2f
            this.BodyMain.xRot = 0.35f
            this.Cloth04.xRot -= 0.4f
            this.ArmLeft01.zRot -= 0.2f + limbSwingAmount * 0.25f
            this.ArmRight01.zRot += 0.2f + limbSwingAmount * 0.25f
            this.LegLeft01.xRot -= 0.45f
            this.LegRight01.xRot -= 0.45f
        }

        if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            this.Head.xRot -= 1.0472f
            this.BodyMain.xRot = 1.0472f
            this.Butt.xRot = if (hideLeg) 0.8f else -0.8378f
            this.Cloth03.xRot -= 0.7f
            this.Cloth04.xRot -= 0.45f
            this.ArmLeft01.xRot = -0.7f
            this.ArmLeft01.zRot = 0.2618f
            this.ArmRight01.xRot = -0.7f
            this.ArmRight01.zRot = -0.2618f
        }

        if (isSitting) {
            if (hideLeg) {
                if (entity != null && hasLegacyState(entity, 1, 4)) {
                    this.poseTranslateY += 0.54f
                    this.Head.xRot = -0.9f
                    this.Head.yRot = 0.0f
                    this.Head.zRot = 0.0f
                    this.Ahoke.yRot = 0.5236f
                    this.BodyMain.xRot = 1.4835f
                    this.ArmLeft01.xRot = sitSwing + 0.25f
                    this.ArmLeft01.zRot = -2.3f
                    this.ArmRight01.xRot = -sitSwing + 0.25f
                    this.ArmRight01.zRot = 2.3f
                    this.LegLeft01.yRot = 0.0f
                    this.LegLeft01.zRot = 0.03f
                    this.LegRight01.yRot = 0.0f
                    this.LegRight01.zRot = -0.03f
                } else {
                    this.poseTranslateY += -0.17f
                    this.Head.xRot = -0.7f
                    this.Head.yRot = 0.0f
                    this.Head.zRot = 0.0f
                    this.Ahoke.yRot = 0.5236f
                    this.BodyMain.xRot = -1.7453f
                    this.Cloth04.xRot = 0.4f
                    this.ArmLeft01.xRot = 0.85f
                    this.ArmLeft01.zRot = -2.3f
                    this.ArmRight01.xRot = 0.85f
                    this.ArmRight01.zRot = 2.3f
                    this.LegLeft01.yRot = 0.0f
                    this.LegLeft01.zRot = 0.03f
                    this.LegRight01.yRot = 0.0f
                    this.LegRight01.zRot = -0.03f
                }
            } else if (entity != null && hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY += 0.53f * 3.2f
                this.Head.xRot = -0.7f
                this.Head.yRot = 0.0f
                this.Head.zRot = 0.0f
                this.Ahoke.yRot = 0.5236f
                this.BodyMain.xRot = 1.4835f
                this.ArmLeft01.xRot = sitSwing + 0.25f
                this.ArmLeft01.zRot = -2.3f
                this.ArmRight01.xRot = -sitSwing + 0.25f
                this.ArmRight01.zRot = 2.3f
                this.LegLeft01.xRot = -sitSwing + 0.2f
                this.LegRight01.xRot = sitSwing + 0.2f
                this.LegLeft01.yRot = 0.0f
                this.LegLeft01.zRot = 0.03f
                this.LegRight01.yRot = 0.0f
                this.LegRight01.zRot = -0.03f
                this.EquipTubeL01.xRot = 1.3f
                this.EquipTubeR01.xRot = 1.3f
            } else {
                this.poseTranslateY += SITTING_TRANSLATE_Y
                this.Head.xRot -= 0.7f
                this.BodyMain.xRot = 0.5236f
                this.ArmLeft01.xRot = -0.5236f
                this.ArmLeft01.zRot = 0.3146f
                this.ArmRight01.xRot = -0.5236f
                this.ArmRight01.zRot = -0.3146f
                this.LegLeft01.xRot = -2.2689f
                this.LegRight01.xRot = -2.2689f
                this.LegLeft01.yRot = -0.3491f
                this.LegRight01.yRot = 0.3491f
                this.EquipBase.xRot = -0.4f
                this.EquipTubeL01.xRot = 0.9f
                this.EquipTubeR01.xRot = 0.9f
            }
        }

        if (entity != null && entity.attackTick > 40) {
            this.poseTranslateY += 0.08f
            this.Head.xRot -= 1.0472f
            this.BodyMain.xRot = 1.7f
            this.Butt.xRot = -0.8378f
            this.Cloth03.xRot -= 0.7f
            this.Cloth04.xRot -= 1.1f
            this.ArmLeft01.xRot = -0.9f
            this.ArmLeft01.zRot = 0.2618f
            this.ArmRight01.xRot = -1.9f
            this.ArmRight01.zRot = -0.2618f
            this.EquipBase.xRot = -1.4f
            this.LegLeft01.xRot -= 0.7f
            this.LegRight01.xRot -= 0.7f
        }

        val partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
        val customAttackAnim = if (entity != null) entity.getCustomAttackAnim(partialTick) else 0.0f
        if (customAttackAnim > 0.0f) {
            val f7 = Mth.sin(customAttackAnim * customAttackAnim * Math.PI.toFloat())
            val f8 = Mth.sin(Mth.sqrt(customAttackAnim) * Math.PI.toFloat())
            this.ArmRight01.xRot += -f8 * 80.0f * (Math.PI.toFloat() / 180f)
            this.ArmRight01.yRot += -f7 * 20.0f * (Math.PI.toFloat() / 180f) + 0.2f
            this.ArmRight01.zRot += -f8 * 20.0f * (Math.PI.toFloat() / 180f)
        }
    }

    companion object {
        @JvmField
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "transport_wa"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = LegacyPoseOffsets.deadY("ModelTransportWa")
        private val SNEAK_TRANSLATE_Y = LegacyPoseOffsets.sneakY("ModelTransportWa")
        private val SITTING_TRANSLATE_Y = LegacyPoseOffsets.sittingY("ModelTransportWa")

        @JvmStatic
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 105).addBox(-6.5f, -11f, -4f, 13f, 16f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -11f, -3f, -0.1047f, 0f, 0f)
            )

            val Head = BodyMain.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -11.8f, -1f, 0.1047f, 0f, 0f)
            )

            val EquipHeadBase = Head.addOrReplaceChild(
                "EquipHeadBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -13.8f, 0f)
            )

            val EquipHead03 = EquipHeadBase.addOrReplaceChild(
                "EquipHead03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-8f, 0f, -9f, 16f, 10f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -4.3f, -7f, 0.2618f, 0f, 0f)
            )

            val EquipHead04 = EquipHeadBase.addOrReplaceChild(
                "EquipHead04",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 16f, 9f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -5.2f, -2.8f, -0.5009f, -0.7213f, 0.3449f)
            )

            val EquipHead05 = EquipHeadBase.addOrReplaceChild(
                "EquipHead05",
                CubeListBuilder.create().texOffs(0, 63).addBox(-7f, 0f, 0f, 14f, 5f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7.3f, -12f, 0.3142f, 0f, 0f)
            )

            val EquipHead06 = EquipHeadBase.addOrReplaceChild(
                "EquipHead06",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 1f, 1f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipHead02 = EquipHeadBase.addOrReplaceChild(
                "EquipHead02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-15f, 0f, 0f, 15f, 9f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, -3f, -12f, 0f, 0.3491f, -0.2094f)
            )

            val EquipHead01 = EquipHeadBase.addOrReplaceChild(
                "EquipHead01",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 15f, 9f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, -3f, -12f, 0f, -0.3491f, 0.2094f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(47, 104).addBox(-7.5f, 0f, 0f, 15f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.8f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(0, 32).addBox(-7.5f, 0f, 0f, 15f, 9f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7.2f, 1.1f, 0.0873f, 0f, 0f)
            )

            val ClothHead = HairMain.addOrReplaceChild(
                "ClothHead",
                CubeListBuilder.create().texOffs(48, 0).addBox(-8f, 0f, 0f, 16f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1.1f, 1.5f, -0.0698f, 0f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 81).addBox(-8f, -8f, -7.4f, 16f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0.4f)
            )

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(50, 45).addBox(-8.5f, 0f, 0f, 17f, 14f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -6.5f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(104, 29).addBox(0f, -12f, -6.5f, 0f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, -4.5f, 1.2f, 0.6981f, 0f)
            )

            val Cloth01b = BodyMain.addOrReplaceChild(
                "Cloth01b",
                CubeListBuilder.create().texOffs(96, 19).mirror()
                    .addBox(-4f, 0f, -4f, 8f, 14f, 8f, CubeDeformation(0f)),
                PartPose.offset(-5.6f, -11.6f, -0.6f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(52, 66).addBox(-7.5f, 0f, -5.7f, 15f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3f, 1.3f, 0.3142f, 0f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 83).mirror().addBox(-3f, 0f, -3f, 6f, 12f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.4f, 5.5f, -2.6f, -0.2443f, 0f, 0.1047f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 83).mirror().addBox(0f, 0f, 0f, 6f, 13f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3f, 12f, -3f)
            )

            val ClothLeg = LegLeft01.addOrReplaceChild(
                "ClothLeg",
                CubeListBuilder.create().texOffs(30, 78).addBox(-3.5f, 0f, -3.5f, 7f, 4f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 4f, 0f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 83).addBox(-3f, 0f, -3f, 6f, 12f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.4f, 5.5f, -2.6f, -0.1396f, 0f, -0.1047f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 83).addBox(-6f, 0f, 0f, 6f, 13f, 6f, CubeDeformation(0f)),
                PartPose.offset(3f, 12f, -3f)
            )

            val Cloth01a = BodyMain.addOrReplaceChild(
                "Cloth01a",
                CubeListBuilder.create().texOffs(96, 19).addBox(-4f, 0f, -4f, 8f, 14f, 8f, CubeDeformation(0f)),
                PartPose.offset(5.6f, -11.6f, -0.6f)
            )

            val EquipBase = BodyMain.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -4.5f, 7.5f, 0.0524f, 0f, 0f)
            )

            val EquipBack01t = EquipBase.addOrReplaceChild(
                "EquipBack01t",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0f, 0f, 0f, 10f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -10f, 32f)
            )

            val EquipBack01m = EquipBase.addOrReplaceChild(
                "EquipBack01m",
                CubeListBuilder.create().texOffs(21, 6).addBox(0f, 0f, 0f, 6f, 10f, 10f, CubeDeformation(0f)),
                PartPose.offset(16f, -10f, 16f)
            )

            val EquipBack01v = EquipBase.addOrReplaceChild(
                "EquipBack01v",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 10f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(-10f, 0f, 32f)
            )

            val EquipBack01j = EquipBase.addOrReplaceChild(
                "EquipBack01j",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 16f, 16f, 16f, CubeDeformation(0f)),
                PartPose.offset(-16f, -16f, 16f)
            )

            val EquipBack01q = EquipBase.addOrReplaceChild(
                "EquipBack01q",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0f, 0f, 0f, 6f, 10f, 10f, CubeDeformation(0f)),
                PartPose.offset(-22f, 0f, 16f)
            )

            val EquipBack01x = EquipBase.addOrReplaceChild(
                "EquipBack01x",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0f, 0f, 0f, 10f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -22f, 16f)
            )

            val EquipBack01zb = EquipBase.addOrReplaceChild(
                "EquipBack01zb",
                CubeListBuilder.create().texOffs(0, 14).mirror().addBox(0f, 0f, 0f, 10f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offset(-10f, 16f, 6f)
            )

            val EquipBack01n = EquipBase.addOrReplaceChild(
                "EquipBack01n",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 6f, 10f, 10f, CubeDeformation(0f)),
                PartPose.offset(16f, 0f, 16f)
            )

            val EquipBack01a = EquipBase.addOrReplaceChild(
                "EquipBack01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 16f, 16f, 16f, CubeDeformation(0f)),
                PartPose.offset(0f, -16f, 0f)
            )

            val EquipBack01p = EquipBase.addOrReplaceChild(
                "EquipBack01p",
                CubeListBuilder.create().texOffs(7, 6).addBox(0f, 0f, 0f, 6f, 10f, 10f, CubeDeformation(0f)),
                PartPose.offset(-22f, -10f, 6f)
            )

            val EquipBack01i = EquipBase.addOrReplaceChild(
                "EquipBack01i",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0f, 0f, 0f, 16f, 16f, 16f, CubeDeformation(0f)),
                PartPose.offset(-16f, 0f, 16f)
            )

            val EquipBack01d = EquipBase.addOrReplaceChild(
                "EquipBack01d",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0f, 0f, 0f, 16f, 16f, 16f, CubeDeformation(0f)),
                PartPose.offset(-16f, 0f, 0f)
            )

            val EquipBack01w = EquipBase.addOrReplaceChild(
                "EquipBack01w",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0f, 0f, 0f, 10f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -22f, 6f)
            )

            val EquipBack01o = EquipBase.addOrReplaceChild(
                "EquipBack01o",
                CubeListBuilder.create().texOffs(26, 12).addBox(0f, 0f, 0f, 6f, 10f, 10f, CubeDeformation(0f)),
                PartPose.offset(-22f, 0f, 6f)
            )

            val EquipTubeR01 = EquipBase.addOrReplaceChild(
                "EquipTubeR01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, 0f, -4f, 8f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-18f, 3f, 28f, -0.3491f, 0.1396f, 0.1396f)
            )

            val EquipTubeR02 = EquipTubeR01.addOrReplaceChild(
                "EquipTubeR02",
                CubeListBuilder.create().texOffs(10, 0).addBox(-4.5f, 0f, -8.5f, 9f, 16f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 16f, 4f, -0.9561f, 0f, 0f)
            )

            val EquipBack01g = EquipBase.addOrReplaceChild(
                "EquipBack01g",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 16f, 16f, 16f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 16f)
            )

            val EquipTubeL01 = EquipBase.addOrReplaceChild(
                "EquipTubeL01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, 0f, -4f, 8f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(18f, 3f, 28f, -0.3491f, -0.1396f, -0.1396f)
            )

            val EquipTubeL02 = EquipTubeL01.addOrReplaceChild(
                "EquipTubeL02",
                CubeListBuilder.create().texOffs(12, 0).addBox(-4.5f, 0f, -8.5f, 9f, 16f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 16f, 4f, -0.9561f, 0f, 0f)
            )

            val EquipBack01zc = EquipBase.addOrReplaceChild(
                "EquipBack01zc",
                CubeListBuilder.create().texOffs(7, 13).mirror().addBox(0f, 0f, 0f, 10f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offset(-10f, 16f, 16f)
            )

            val EquipBack01b = EquipBase.addOrReplaceChild(
                "EquipBack01b",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0f, 0f, 0f, 16f, 16f, 16f, CubeDeformation(0f)),
                PartPose.offset(-16f, -16f, 0f)
            )

            val EquipBack01e = EquipBase.addOrReplaceChild(
                "EquipBack01e",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0f, 0f, 0f, 10f, 16f, 4f, CubeDeformation(0f)),
                PartPose.offset(-10f, -6f, -4f)
            )

            val EquipBack01h = EquipBase.addOrReplaceChild(
                "EquipBack01h",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0f, 0f, 0f, 16f, 16f, 16f, CubeDeformation(0f)),
                PartPose.offset(0f, -16f, 16f)
            )

            val EquipBack01s = EquipBase.addOrReplaceChild(
                "EquipBack01s",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0f, 0f, 0f, 10f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 32f)
            )

            val EquipBack01r = EquipBase.addOrReplaceChild(
                "EquipBack01r",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0f, 0f, 0f, 6f, 10f, 10f, CubeDeformation(0f)),
                PartPose.offset(-22f, -10f, 16f)
            )

            val EquipBack01f = EquipBase.addOrReplaceChild(
                "EquipBack01f",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 10f, 16f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -4f)
            )

            val EquipBack01k = EquipBase.addOrReplaceChild(
                "EquipBack01k",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0f, 0f, 0f, 6f, 10f, 10f, CubeDeformation(0f)),
                PartPose.offset(16f, 0f, 6f)
            )

            val EquipBack01l = EquipBase.addOrReplaceChild(
                "EquipBack01l",
                CubeListBuilder.create().texOffs(0, 11).mirror().addBox(0f, 0f, 0f, 6f, 10f, 10f, CubeDeformation(0f)),
                PartPose.offset(16f, -10f, 6f)
            )

            val EquipBack01za = EquipBase.addOrReplaceChild(
                "EquipBack01za",
                CubeListBuilder.create().texOffs(0, 16).addBox(0f, 0f, 0f, 10f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 6f)
            )

            val EquipBack01z = EquipBase.addOrReplaceChild(
                "EquipBack01z",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 10f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offset(-10f, -22f, 6f)
            )

            val EquipBack01zd = EquipBase.addOrReplaceChild(
                "EquipBack01zd",
                CubeListBuilder.create().texOffs(0, 6).addBox(0f, 0f, 0f, 10f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 16f)
            )

            val EquipBack01u = EquipBase.addOrReplaceChild(
                "EquipBack01u",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 10f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offset(-10f, -10f, 32f)
            )

            val EquipBack01y = EquipBase.addOrReplaceChild(
                "EquipBack01y",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 10f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offset(-10f, -22f, 16f)
            )

            val EquipBack01c = EquipBase.addOrReplaceChild(
                "EquipBack01c",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 16f, 16f, 16f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(2, 84).mirror()
                    .addBox(-2f, -1f, -2.5f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.7f, -0.7f, 0f, 0f, -0.2094f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(2, 84).mirror().addBox(-5f, 0f, -5f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 10f, 2.5f)
            )

            val Cloth03 = BodyMain.addOrReplaceChild(
                "Cloth03",
                CubeListBuilder.create().texOffs(58, 32).addBox(-7f, 0f, -4.7f, 14f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 1.3f, -0.5f, 0.1745f, 0f, 0.0873f)
            )

            val Cloth00b = Cloth03.addOrReplaceChild(
                "Cloth00b",
                CubeListBuilder.create().texOffs(19, 79).addBox(-7f, -2f, 0f, 7f, 4f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, 3.3f, -0.2094f, 0.2618f, 0.1745f)
            )

            val Cloth04 = Cloth03.addOrReplaceChild(
                "Cloth04",
                CubeListBuilder.create().texOffs(70, 21).addBox(-6.5f, 0f, 0f, 13f, 11f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.5f, -4.7f, -0.2094f, 0f, -0.0873f)
            )

            val Cloth00d = Cloth03.addOrReplaceChild(
                "Cloth00d",
                CubeListBuilder.create().texOffs(88, 101).addBox(-2.5f, 0f, 0f, 5f, 12f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.8f, 1f, 4.5f, 0.3491f, -0.1396f, 0.3142f)
            )

            val Cloth00a = Cloth03.addOrReplaceChild(
                "Cloth00a",
                CubeListBuilder.create().texOffs(19, 79).mirror().addBox(0f, -2f, 0f, 7f, 4f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.5f, 0.5f, 3.5f, -0.0911f, -0.2618f, -0.1745f)
            )

            val Cloth00c = Cloth03.addOrReplaceChild(
                "Cloth00c",
                CubeListBuilder.create().texOffs(88, 101).mirror()
                    .addBox(-2.5f, 0f, 0f, 5f, 12f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.3f, 1f, 4.5f, 0.3142f, 0.1396f, -0.3491f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(33, 101).mirror().addBox(-3f, 0f, 0f, 6f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.1f, -8.5f, -3.7f, -0.6981f, 0.0785f, 0.0785f)
            )

            val Cloth2a = BoobL.addOrReplaceChild(
                "Cloth2a",
                CubeListBuilder.create().texOffs(26, 89).addBox(0f, 0f, 0f, 5f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.2f, -0.5f, -0.7f, 0f, -0.0785f, -0.0785f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(2, 84).addBox(-3f, -1f, -2.5f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, -9.7f, -0.7f, 0.2094f, 0f, 0.2094f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(2, 84).addBox(0f, 0f, -5f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 10f, 2.5f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(33, 101).addBox(-3f, 0f, 0f, 6f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.1f, -8.5f, -3.7f, -0.6981f, -0.0785f, -0.0785f)
            )

            val Cloth2b = BoobR.addOrReplaceChild(
                "Cloth2b",
                CubeListBuilder.create().texOffs(26, 89).mirror().addBox(-5f, 0f, 0f, 5f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.2f, -0.5f, -0.7f, 0f, 0.0785f, 0.0785f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 105),
                PartPose.offset(0f, -11f, -3f)
            )

            val GlowHead = GlowBodyMain.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(44, 101),
                PartPose.offset(0f, -11.8f, -1f)
            )

            addFaceLayer(GlowHead)

            val GlowBodyMain2 = partdefinition.addOrReplaceChild(
                "GlowBodyMain2",
                CubeListBuilder.create().texOffs(0, 105),
                PartPose.offset(0f, -11f, -3f)
            )

            val GlowEquipBase = GlowBodyMain2.addOrReplaceChild(
                "GlowEquipBase",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -4.5f, 7.5f)
            )

            val GlowEquipTubeL01 = GlowEquipBase.addOrReplaceChild(
                "GlowEquipTubeL01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(18f, 3f, 28f)
            )

            val GlowEquipTubeL02 = GlowEquipTubeL01.addOrReplaceChild(
                "GlowEquipTubeL02",
                CubeListBuilder.create().texOffs(12, 0),
                PartPose.offsetAndRotation(0f, 16f, 4f, -0.9561f, 0f, 0f)
            )

            val EquipTubeL03 = GlowEquipTubeL02.addOrReplaceChild(
                "EquipTubeL03",
                CubeListBuilder.create().texOffs(92, 0).addBox(-4.5f, 0f, -4.5f, 9f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 16.1f, -4f)
            )

            val GlowEquipTubeR01 = GlowEquipBase.addOrReplaceChild(
                "GlowEquipTubeR01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(-18f, 3f, 28f)
            )

            val GlowEquipTubeR02 = GlowEquipTubeR01.addOrReplaceChild(
                "GlowEquipTubeR02",
                CubeListBuilder.create().texOffs(10, 0),
                PartPose.offsetAndRotation(0f, 16f, 4f, -0.9561f, 0f, 0f)
            )

            val EquipTubeR03 = GlowEquipTubeR02.addOrReplaceChild(
                "EquipTubeR03",
                CubeListBuilder.create().texOffs(92, 0).addBox(-4.5f, 0f, -4.5f, 9f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 16.1f, -4f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
