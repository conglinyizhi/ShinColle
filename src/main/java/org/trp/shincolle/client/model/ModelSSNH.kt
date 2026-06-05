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
import org.trp.shincolle.entity.EntitySSNH
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelSSNH<T : EntityShipBase?>(root: ModelPart) : ShipModelHumanoidBase<T?>(), IGlowableModel {
    private var isDeadPose = false
    private var isSittingPose = false
    private var poseTranslateY = 0f
    private var poseTranslateZ = 0f

    private val BodyMain: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val Butt: ModelPart
    private val EquipBase: ModelPart
    private val Cloth01: ModelPart
    private val Neck: ModelPart
    private val Cloth00: ModelPart
    private val RingBase: ModelPart
    private val ArmLeft02: ModelPart
    private val EquipTBase: ModelPart
    private val EqyuipT01: ModelPart
    private val EqyuipT02: ModelPart
    private val EqyuipT04: ModelPart
    private val EqyuipT03: ModelPart
    private val EquipT03a: ModelPart
    private val EqyuipT05: ModelPart
    private val EquipT05a: ModelPart
    private val EquipT05b: ModelPart
    private val EquipT05c: ModelPart
    private val EquipT05d: ModelPart
    private val EquipTBase_2: ModelPart
    private val EqyuipT01_2: ModelPart
    private val EqyuipT02_2: ModelPart
    private val EqyuipT04_2: ModelPart
    private val EqyuipT03_2: ModelPart
    private val EquipT03a_2: ModelPart
    private val EqyuipT05_2: ModelPart
    private val EquipT05a_2: ModelPart
    private val EquipT05b_2: ModelPart
    private val EquipT05c_2: ModelPart
    private val EquipT05d_2: ModelPart
    private val ArmRight02: ModelPart
    private val EquipHandRing: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft01: ModelPart
    private val LegRight02: ModelPart
    private val LegLeft02: ModelPart
    private val Cloth02: ModelPart
    private val Cloth03: ModelPart
    private val Cloth04: ModelPart
    private val Head: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val Ahoke01: ModelPart
    private val Ahoke01a: ModelPart
    private val HairU01: ModelPart
    private val Hair01: ModelPart
    private val Hair02: ModelPart
    private val Ahoke02: ModelPart
    private val Ahoke03: ModelPart
    private val Ahoke04: ModelPart
    private val Ahoke05: ModelPart
    private val Ahoke06: ModelPart
    private val Ahoke02a: ModelPart
    private val Ahoke03a: ModelPart
    private val Ahoke04a: ModelPart
    private val Ahoke05a: ModelPart
    private val Ahoke06a: ModelPart
    private val Ring01: ModelPart
    private val Ring02: ModelPart
    private val Ring03Base: ModelPart
    private val Ring03a: ModelPart
    private val Ring03b: ModelPart
    private val Ring03c: ModelPart
    private val Ring03d: ModelPart
    private val Ring03e: ModelPart
    private val Ring03f: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val armLeft02DefaultX: Float
    private val armLeft02DefaultZ: Float
    private val armRight02DefaultX: Float
    private val armRight02DefaultZ: Float
    private val legLeft02DefaultX: Float
    private val legLeft02DefaultY: Float
    private val legLeft02DefaultZ: Float
    private val legRight02DefaultX: Float
    private val legRight02DefaultY: Float
    private val legRight02DefaultZ: Float
    private val equipTBaseDefaultX: Float
    private val equipTBaseDefaultY: Float
    private val equipTBase2DefaultX: Float
    private val equipTBase2DefaultY: Float
    private val buttDefaultY: Float
    private val buttDefaultZ: Float
    private val cloth02DefaultY: Float
    private val cloth03DefaultY: Float
    private val cloth03DefaultZ: Float
    private val cloth04DefaultY: Float
    private val cloth04DefaultZ: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.EquipTBase = this.ArmLeft02.getChild("EquipTBase")
        this.EqyuipT01 = this.EquipTBase.getChild("EqyuipT01")
        this.EqyuipT02 = this.EqyuipT01.getChild("EqyuipT02")
        this.EqyuipT03 = this.EqyuipT02.getChild("EqyuipT03")
        this.EquipT03a = this.EqyuipT03.getChild("EquipT03a")
        this.EqyuipT04 = this.EqyuipT01.getChild("EqyuipT04")
        this.EqyuipT05 = this.EqyuipT04.getChild("EqyuipT05")
        this.EquipT05b = this.EqyuipT05.getChild("EquipT05b")
        this.EquipT05c = this.EqyuipT05.getChild("EquipT05c")
        this.EquipT05a = this.EqyuipT05.getChild("EquipT05a")
        this.EquipT05d = this.EqyuipT05.getChild("EquipT05d")
        this.Cloth00 = this.BodyMain.getChild("Cloth00")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.Ahoke01 = this.Head.getChild("Ahoke01")
        this.Ahoke02 = this.Ahoke01.getChild("Ahoke02")
        this.Ahoke03 = this.Ahoke02.getChild("Ahoke03")
        this.Ahoke04 = this.Ahoke03.getChild("Ahoke04")
        this.Ahoke05 = this.Ahoke04.getChild("Ahoke05")
        this.Ahoke06 = this.Ahoke05.getChild("Ahoke06")
        this.Ahoke01a = this.Head.getChild("Ahoke01a")
        this.Ahoke02a = this.Ahoke01a.getChild("Ahoke02a")
        this.Ahoke03a = this.Ahoke02a.getChild("Ahoke03a")
        this.Ahoke04a = this.Ahoke03a.getChild("Ahoke04a")
        this.Ahoke05a = this.Ahoke04a.getChild("Ahoke05a")
        this.Ahoke06a = this.Ahoke05a.getChild("Ahoke06a")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.EquipBase = this.BodyMain.getChild("EquipBase")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.Cloth02 = this.Cloth01.getChild("Cloth02")
        this.Cloth03 = this.Cloth02.getChild("Cloth03")
        this.Cloth04 = this.Cloth03.getChild("Cloth04")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.EquipHandRing = this.ArmRight02.getChild("EquipHandRing")
        this.EquipTBase_2 = this.ArmRight02.getChild("EquipTBase_2")
        this.EqyuipT01_2 = this.EquipTBase_2.getChild("EqyuipT01_2")
        this.EqyuipT02_2 = this.EqyuipT01_2.getChild("EqyuipT02_2")
        this.EqyuipT03_2 = this.EqyuipT02_2.getChild("EqyuipT03_2")
        this.EquipT03a_2 = this.EqyuipT03_2.getChild("EquipT03a_2")
        this.EqyuipT04_2 = this.EqyuipT01_2.getChild("EqyuipT04_2")
        this.EqyuipT05_2 = this.EqyuipT04_2.getChild("EqyuipT05_2")
        this.EquipT05a_2 = this.EqyuipT05_2.getChild("EquipT05a_2")
        this.EquipT05b_2 = this.EqyuipT05_2.getChild("EquipT05b_2")
        this.EquipT05c_2 = this.EqyuipT05_2.getChild("EquipT05c_2")
        this.EquipT05d_2 = this.EqyuipT05_2.getChild("EquipT05d_2")
        this.RingBase = this.BodyMain.getChild("RingBase")
        this.Ring01 = this.RingBase.getChild("Ring01")
        this.Ring02 = this.Ring01.getChild("Ring02")
        this.Ring03Base = this.Ring02.getChild("Ring03Base")
        this.Ring03b = this.Ring03Base.getChild("Ring03b")
        this.Ring03c = this.Ring03Base.getChild("Ring03c")
        this.Ring03a = this.Ring03Base.getChild("Ring03a")
        this.Ring03e = this.Ring03Base.getChild("Ring03e")
        this.Ring03d = this.Ring03Base.getChild("Ring03d")
        this.Ring03f = this.Ring03Base.getChild("Ring03f")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        this.initFaceParts(this.GlowHead)
        this.armLeft02DefaultX = this.ArmLeft02.x
        this.armLeft02DefaultZ = this.ArmLeft02.z
        this.armRight02DefaultX = this.ArmRight02.x
        this.armRight02DefaultZ = this.ArmRight02.z
        this.legLeft02DefaultX = this.LegLeft02.x
        this.legLeft02DefaultY = this.LegLeft02.y
        this.legLeft02DefaultZ = this.LegLeft02.z
        this.legRight02DefaultX = this.LegRight02.x
        this.legRight02DefaultY = this.LegRight02.y
        this.legRight02DefaultZ = this.LegRight02.z
        this.equipTBaseDefaultX = this.EquipTBase.x
        this.equipTBaseDefaultY = this.EquipTBase.y
        this.equipTBase2DefaultX = this.EquipTBase_2.x
        this.equipTBase2DefaultY = this.EquipTBase_2.y
        this.buttDefaultY = this.Butt.y
        this.buttDefaultZ = this.Butt.z
        this.cloth02DefaultY = this.Cloth02.y
        this.cloth03DefaultY = this.Cloth03.y
        this.cloth03DefaultZ = this.Cloth03.z
        this.cloth04DefaultY = this.Cloth04.y
        this.cloth04DefaultZ = this.Cloth04.z
    }

    override fun setupAnim(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        this.resetOffsets()
        this.applyEquipVisibility(entity)
        applyFaceAndMouth(entity)

        val inDeadPose = entity != null && entity.isInDeadPose

        if (inDeadPose) {
            this.applyDeadPose()
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
        this.poseTranslateZ = 0.0f

        this.ArmLeft02.x = this.armLeft02DefaultX
        this.ArmLeft02.z = this.armLeft02DefaultZ
        this.ArmRight02.x = this.armRight02DefaultX
        this.ArmRight02.z = this.armRight02DefaultZ
        this.LegLeft02.x = this.legLeft02DefaultX
        this.LegLeft02.y = this.legLeft02DefaultY
        this.LegLeft02.z = this.legLeft02DefaultZ
        this.LegRight02.x = this.legRight02DefaultX
        this.LegRight02.y = this.legRight02DefaultY
        this.LegRight02.z = this.legRight02DefaultZ
        this.EquipTBase.x = this.equipTBaseDefaultX
        this.EquipTBase.y = this.equipTBaseDefaultY
        this.EquipTBase_2.x = this.equipTBase2DefaultX
        this.EquipTBase_2.y = this.equipTBase2DefaultY
        this.Butt.y = this.buttDefaultY
        this.Butt.z = this.buttDefaultZ
        this.Cloth02.y = this.cloth02DefaultY
        this.Cloth03.y = this.cloth03DefaultY
        this.Cloth03.z = this.cloth03DefaultZ
        this.Cloth04.y = this.cloth04DefaultY
        this.Cloth04.z = this.cloth04DefaultZ
    }

    private fun applyEquipVisibility(entity: T?) {
        val showHandRing = entity!!.getEquipFlag(EntitySSNH.EQUIP_HAND_RING)
        val showRingBase = entity.getEquipFlag(EntitySSNH.EQUIP_RING_BASE)
        val showTorpedo = entity.getEquipFlag(EntitySSNH.EQUIP_TORPEDO)

        this.EquipHandRing.visible = showHandRing
        this.RingBase.visible = showRingBase
        this.EquipTBase.visible = showTorpedo
        this.EquipTBase_2.visible = false
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        this.Head.xRot = -0.15f
        this.Head.yRot = 0.0f
        this.Head.zRot = 0.0f
        this.BodyMain.xRot = 1.6f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = 0.21f
        this.Butt.y = this.buttDefaultY
        this.Butt.z = this.buttDefaultZ
        this.Cloth03.xRot = 0.087f
        this.Cloth03.y = this.cloth03DefaultY
        this.Cloth03.z = this.cloth03DefaultZ
        this.Cloth04.xRot = -0.052f
        this.Cloth04.y = this.cloth04DefaultY
        this.Cloth04.z = this.cloth04DefaultZ
        this.Hair01.xRot = 0.35f
        this.Hair01.zRot = 0.0f
        this.Hair02.xRot = -0.2f
        this.Hair02.zRot = 0.0f

        this.ArmLeft01.xRot = -3.0f
        this.ArmLeft01.yRot = -0.6981f
        this.ArmLeft01.zRot = 0.08f
        this.ArmLeft02.xRot = 0.0f
        this.ArmLeft02.yRot = 0.0f
        this.ArmLeft02.zRot = 0.0f
        this.ArmRight01.xRot = -3.0f
        this.ArmRight01.yRot = 0.6981f
        this.ArmRight01.zRot = -0.08f
        this.ArmRight02.xRot = 0.0f
        this.ArmRight02.yRot = 0.0f
        this.ArmRight02.zRot = 0.0f

        this.LegLeft01.xRot = -0.3f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = -0.05f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.y = this.legLeft02DefaultY
        this.LegLeft02.z = this.legLeft02DefaultZ
        this.LegRight01.xRot = -0.3f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = 0.05f
        this.LegRight02.xRot = 0.0f
        this.LegRight02.y = this.legRight02DefaultY
        this.LegRight02.z = this.legRight02DefaultZ

        this.EquipTBase.xRot = 0.8f
        this.EquipTBase.yRot = 0.0f
        this.EquipTBase.zRot = 1.2f
        this.EquipTBase.x = this.equipTBaseDefaultX
        this.EquipTBase.y = this.equipTBaseDefaultY
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
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.4f + limbSwing * 0.5f)
        val angleX2 = Mth.cos(ageInTicks * 0.08f + 0.8f + limbSwing * 0.5f)
        val angleX3 = Mth.cos(ageInTicks * 0.08f + 1.2f + limbSwing * 0.5f)
        val angleX4 = Mth.cos(ageInTicks * 0.08f + 1.6f + limbSwing * 0.5f)
        val angleX5 = Mth.cos(ageInTicks * 0.08f + 2.0f + limbSwing * 0.5f)
        val angleX6 = Mth.cos(ageInTicks * 0.08f + 2.4f + limbSwing * 0.5f)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount * 0.5f
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount * 0.5f

        if (entity!!.shipDepth > 0.0) {
            this.poseTranslateY = angleX * 0.025f + 0.025f
        }

        val addk1 = angleAdd1 * 0.6f - 0.1f
        val addk2 = angleAdd2 * 0.6f - 0.1f

        this.Head.xRot = headPitch * (Math.PI.toFloat() / 180f)
        this.Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f) * 0.4f
        this.Head.zRot = 0.0f
        val headX = this.Head.xRot * -0.5f
        val headZ = this.Head.zRot * -0.5f

        this.Ahoke01.xRot = angleX1 * 0.07f - 2.01f
        this.Ahoke01.yRot = 0.52f
        this.Ahoke01.zRot = 0.0f
        this.Ahoke02.xRot = -angleX2 * 0.09f + 1.04f
        this.Ahoke03.xRot = angleX3 * 0.15f + 0.78f
        this.Ahoke04.xRot = -angleX4 * 0.1f + 0.44f
        this.Ahoke05.xRot = -angleX5 * 0.15f - 0.17f
        this.Ahoke06.xRot = angleX6 * 0.18f - 0.31f

        this.Ahoke01a.xRot = angleX1 * 0.07f - 2.27f
        this.Ahoke01a.yRot = -2.62f
        this.Ahoke01a.zRot = 0.0f
        this.Ahoke02a.xRot = -angleX2 * 0.09f + 0.79f
        this.Ahoke03a.xRot = angleX3 * 0.15f + 1.05f
        this.Ahoke04a.xRot = -angleX4 * 0.1f + 0.41f
        this.Ahoke05a.xRot = -angleX5 * 0.15f - 0.3f
        this.Ahoke06a.xRot = angleX6 * 0.18f - 0.25f

        this.BodyMain.xRot = -0.0873f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = 0.21f
        this.Cloth02.xRot = 0.087f
        this.Cloth03.xRot = 0.087f
        this.Cloth04.xRot = -0.052f

        this.Hair01.xRot = angleX * 0.03f + 0.26f + headX
        this.Hair01.zRot = headZ
        this.Hair02.xRot = -angleX1 * 0.04f - 0.087f + headX
        this.Hair02.zRot = headZ

        this.ArmLeft01.xRot = angleAdd2 * 0.8f - 0.05f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = angleX * 0.025f - 0.3f
        this.ArmLeft02.xRot = 0.0f
        this.ArmLeft02.yRot = 0.0f
        this.ArmLeft02.zRot = 0.0f

        this.ArmRight01.xRot = angleAdd1 * 0.8f + 0.26f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = -angleX * 0.025f + 0.4f
        this.ArmRight02.xRot = 0.0f
        this.ArmRight02.yRot = 0.0f
        this.ArmRight02.zRot = 0.0f

        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = -0.035f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.yRot = 0.0f
        this.LegLeft02.zRot = 0.0f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = 0.035f
        this.LegRight02.xRot = 0.0f
        this.LegRight02.yRot = 0.0f
        this.LegRight02.zRot = 0.0f

        this.EquipTBase.xRot = 0.15f
        this.EquipTBase.yRot = 0.0f
        this.EquipTBase.zRot = 0.0f
        this.EquipTBase.x = this.equipTBaseDefaultX + (-0.13f * OFFSET_SCALE)
        this.EquipTBase.y = this.equipTBaseDefaultY

        this.EquipTBase_2.xRot = 0.15f
        this.EquipTBase_2.yRot = 0.0f
        this.EquipTBase_2.zRot = 0.0f
        this.EquipTBase_2.x = this.equipTBase2DefaultX
        this.EquipTBase_2.y = this.equipTBase2DefaultY

        this.LegLeft01.xRot = addk1
        this.LegRight01.xRot = addk2
    }

    private fun applySpecialPoseAdjustments(entity: T?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        val angleX = Mth.cos(ageInTicks * 0.08f)
        val isPassenger = entity!!.isPassenger()
        val isCrouching = entity.isCrouching()
        val isSprinting = if (entity != null) entity.getIsSprinting() else limbSwingAmount > 0.9f
        val isSitting = entity.getIsSitting() || (isPassenger && entity.getVehicle() !is EntityMountBase)
        val showTorpedo = entity != null && entity.getEquipFlag(EntitySSNH.EQUIP_TORPEDO)

        if (isSprinting) {
            if (isPassenger) {
                if (limbSwingAmount > 0.5f) {
                    this.Head.xRot += 0.4f
                    this.Hair01.xRot += 0.1f
                    this.Hair02.xRot -= 0.2f
                }
            } else {
                this.poseTranslateY -= 0.06f
                this.Head.xRot -= 1.3f
                this.Hair01.xRot += 0.6f
                this.Hair02.xRot += 0.5f
                this.Ahoke01.xRot += 0.38f
                this.Ahoke01.yRot = 0.7f
                this.Ahoke01.zRot = 0.4f
                this.Ahoke01a.yRot = -2.5f
                this.Ahoke01a.zRot = -0.2f
            }
            this.BodyMain.xRot = 1.5f
            this.ArmLeft01.xRot = -2.9f
            this.ArmLeft01.zRot = -0.22f
            this.ArmRight01.xRot = -2.9f
            this.ArmRight01.zRot = 0.22f
            this.LegLeft01.zRot = 0.05f
            this.LegRight01.zRot = -0.05f

            if (showTorpedo) {
                this.EquipTBase.xRot = 1.42f
                this.EquipTBase.yRot = 0.0f
                this.EquipTBase.zRot = -0.22f
                this.EquipTBase.x = this.equipTBaseDefaultX + (0.17f * OFFSET_SCALE)
                this.EquipTBase.y = this.equipTBaseDefaultY + (0.64f * OFFSET_SCALE)
                this.EquipTBase_2.visible = true
                this.EquipTBase_2.xRot = 1.42f
                this.EquipTBase_2.yRot = 0.0f
                this.EquipTBase_2.zRot = 0.22f
                this.EquipTBase_2.x = this.equipTBase2DefaultX + (-0.17f * OFFSET_SCALE)
                this.EquipTBase_2.y = this.equipTBase2DefaultY + (0.64f * OFFSET_SCALE)
            } else {
                this.EquipTBase_2.visible = false
            }
        }

        if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            this.Head.xRot -= 0.6283f
            this.BodyMain.xRot = 0.8727f
            this.Cloth03.xRot = -0.34f
            this.Cloth03.y = this.cloth03DefaultY + (-0.2f * OFFSET_SCALE)
            this.Cloth03.z = this.cloth03DefaultZ + (0.03f * OFFSET_SCALE)
            this.Cloth04.xRot = -0.27f
            this.ArmLeft01.xRot = -0.35f
            this.ArmLeft01.zRot = 0.2618f
            this.ArmRight01.xRot = -0.35f
            this.ArmRight01.zRot = -0.2618f

            this.LegLeft01.xRot -= 0.94f
            this.LegRight01.xRot -= 0.94f
            this.LegLeft01.zRot = 0.2f
            this.LegRight01.zRot = -0.2f
            this.Hair01.xRot = this.Hair01.xRot * 0.5f + 0.4f
            this.Hair02.xRot = this.Hair02.xRot * 0.75f + 0.25f

            this.EquipTBase.xRot = 0.48f
            this.EquipTBase.yRot = 1.55f
            this.EquipTBase.zRot = 0.0f
            this.EquipTBase.x = this.equipTBaseDefaultX
            this.EquipTBase.y = this.equipTBaseDefaultY
        }

        if (isSitting) {
            if (!isPassenger && entity != null && (entity.tickCount and 0x1FF) > 256) {
                this.poseTranslateY += -angleX * 0.05f - 0.1f
                this.Head.xRot *= 0.5f
                this.Head.yRot *= 0.75f
                this.Head.xRot += 0.5f
                this.BodyMain.xRot = 1.6f
                this.Cloth03.xRot = -0.33f
                this.Cloth03.y = this.cloth03DefaultY + (-0.23f * OFFSET_SCALE)
                this.Cloth04.xRot = -0.12f
                this.Cloth04.y = this.cloth04DefaultY + (-0.16f * OFFSET_SCALE)
                this.Ahoke01.xRot += 0.38f
                this.Ahoke01.yRot = 0.8f
                this.Ahoke01.zRot = 0.4f
                this.Hair01.xRot -= 0.2f
                this.Hair02.xRot -= 0.25f
                this.ArmLeft01.xRot = -1.5f
                this.ArmLeft01.zRot = -2.3f
                this.ArmRight01.xRot = -1.5f
                this.ArmRight01.zRot = 2.3f
                this.LegLeft01.xRot = -1.8f
                this.LegRight01.xRot = -1.8f
                this.LegLeft01.yRot = -0.1f - angleX * 0.02f
                this.LegRight01.yRot = 0.1f + angleX * 0.02f
            } else if (!isPassenger && entity != null && hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY += 0.26f * 3.5f
                this.Head.xRot = 0.4f
                this.Cloth03.xRot = -0.64f
                this.Cloth03.y = this.cloth03DefaultY + (-0.17f * OFFSET_SCALE)
                this.Cloth03.z = this.cloth03DefaultZ
                this.Cloth04.xRot = 0.29f
                this.Cloth04.y = this.cloth04DefaultY + (-0.04f * OFFSET_SCALE)
                this.Cloth04.z = this.cloth04DefaultZ + (0.02f * OFFSET_SCALE)
                this.Hair01.xRot -= 0.2f
                this.Hair02.xRot -= 0.15f
                this.Ahoke01.xRot -= 0.1f
                this.ArmLeft01.xRot = 0.4f
                this.ArmLeft01.yRot = -2.9670596f
                this.ArmLeft01.zRot = -2.62f
                this.ArmLeft02.xRot = 0.0f
                this.ArmLeft02.yRot = 0.0f
                this.ArmLeft02.zRot = 1.0f
                this.ArmRight01.xRot = 0.5235988f
                this.ArmRight01.yRot = 2.9670596f
                this.ArmRight01.zRot = 2.62f
                this.ArmRight02.xRot = 0.0f
                this.ArmRight02.yRot = 0.0f
                this.ArmRight02.zRot = -1.0f
                this.LegLeft01.xRot = -2.4130921f
                this.LegRight01.xRot = -2.268928f
                this.LegLeft01.yRot = 0.0f
                this.LegLeft01.zRot = -0.27314404f
                this.LegLeft02.xRot = 1.4570009f
                this.LegLeft02.yRot = 0.0f
                this.LegLeft02.zRot = 0.0f
                this.LegRight01.yRot = 0.0f
                this.LegRight01.zRot = 0.22759093f
                this.LegRight02.xRot = 1.0471976f
                this.LegRight02.yRot = 0.0f
                this.LegRight02.zRot = 0.0f
                this.EquipTBase.visible = false
                this.EquipTBase_2.visible = false
            } else {
                this.poseTranslateY += SITTING_TRANSLATE_Y
                this.Head.xRot -= 0.7f
                this.BodyMain.xRot = 0.35f
                this.Hair01.xRot += 0.3f
                this.Hair02.xRot += 0.3f
                this.Cloth03.xRot = -0.32f
                this.Cloth03.y = this.cloth03DefaultY + (-0.05f * OFFSET_SCALE)
                this.Cloth04.xRot = -0.21f

                this.ArmLeft01.xRot = -0.5235f
                this.ArmLeft01.yRot = 0.0f
                this.ArmLeft01.zRot = 0.349f
                this.ArmRight01.xRot = -0.5235f
                this.ArmRight01.yRot = 0.0f
                this.ArmRight01.zRot = -0.349f

                this.LegLeft01.xRot = -1.4486f
                this.LegRight01.xRot = -1.4486f
                this.LegLeft01.yRot = -0.5235f
                this.LegLeft01.zRot = -1.3962f
                this.LegRight01.yRot = 0.5235f
                this.LegRight01.zRot = 1.3962f

                this.LegLeft02.xRot = 2.1816f
                this.LegLeft02.z = this.legLeft02DefaultZ + (0.37f * OFFSET_SCALE)
                this.LegRight02.xRot = 2.1816f
                this.LegRight02.z = this.legRight02DefaultZ + (0.37f * OFFSET_SCALE)
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
            this.EquipTBase.xRot = -1.4f
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

    private fun syncGlowParts() {
        this.GlowBodyMain.copyFrom(this.BodyMain)
        this.GlowNeck.copyFrom(this.Neck)
        this.GlowHead.copyFrom(this.Head)
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

        this.BodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

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

        this.GlowBodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "subm_hime_new"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelSSNH")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelSSNH")
        private val SITTING_TRANSLATE_Y = sittingY("ModelSSNH")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 113).addBox(-5.5f, -11f, -3.5f, 11f, 9f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, 0f, -0.0873f, 0f, 0f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 78).addBox(-5.5f, 0f, 0f, 11f, 6f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2f, -4f, 0.2094f, 0f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 98).mirror()
                    .addBox(-2.5f, 0f, -2.5f, 5f, 9f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.2f, 5.5f, 2.4f, -0.1047f, 0f, -0.0524f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 98).mirror()
                    .addBox(-2.5f, 0f, 0f, 5f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 9f, -2.5f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 98).addBox(-2.5f, 0f, -2.5f, 5f, 9f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.2f, 5.5f, 2.4f, -0.1047f, 0f, 0.0524f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 98).addBox(-2.5f, 0f, 0f, 5f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 9f, -2.5f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(2, 99).mirror().addBox(-1f, -1f, -2f, 4f, 8f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, -9.3f, -0.7f, 0.1396f, 0f, -0.2618f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(2, 99).mirror().addBox(-4f, 0f, -4f, 4f, 9f, 4f, CubeDeformation(0f)),
                PartPose.offset(3f, 7f, 2f)
            )

            val EquipTBase = ArmLeft02.addOrReplaceChild(
                "EquipTBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.6f, 9f, -2f, 0.0873f, 0f, 0f)
            )

            val EqyuipT01 = EquipTBase.addOrReplaceChild(
                "EqyuipT01",
                CubeListBuilder.create().texOffs(5, 0).addBox(-2f, 0f, -2f, 4f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -1.5708f, 0f, 0f)
            )

            val EqyuipT02 = EqyuipT01.addOrReplaceChild(
                "EqyuipT02",
                CubeListBuilder.create().texOffs(4, 0).addBox(-2f, 0f, -2f, 4f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, 6.9f, 0f)
            )

            val EqyuipT03 = EqyuipT02.addOrReplaceChild(
                "EqyuipT03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, 0f, -2f, 4f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, 6.9f, 0f)
            )

            val EquipT03a = EqyuipT03.addOrReplaceChild(
                "EquipT03a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 1f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 6.9f, 0f)
            )

            val EqyuipT04 = EqyuipT01.addOrReplaceChild(
                "EqyuipT04",
                CubeListBuilder.create().texOffs(3, 4).addBox(-2f, 0f, -2f, 4f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, -6.9f, 0f)
            )

            val EqyuipT05 = EqyuipT04.addOrReplaceChild(
                "EqyuipT05",
                CubeListBuilder.create().texOffs(2, 3).addBox(-2f, 0f, -2f, 4f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -6.9f, 0f, 0f, 0f, 0.0214f)
            )

            val EquipT05b = EqyuipT05.addOrReplaceChild(
                "EquipT05b",
                CubeListBuilder.create().texOffs(14, 4).addBox(-0.5f, 0f, 0f, 1f, 6f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.9f, 1f, 0f, 0f, -1.5708f, 0f)
            )

            val EquipT05c = EqyuipT05.addOrReplaceChild(
                "EquipT05c",
                CubeListBuilder.create().texOffs(0, 4).addBox(-0.5f, 0f, 0f, 1f, 6f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.9f, 1f, 0f, 0f, 1.5708f, 0f)
            )

            val EquipT05a = EqyuipT05.addOrReplaceChild(
                "EquipT05a",
                CubeListBuilder.create().texOffs(8, 7).addBox(-0.5f, 0f, 0f, 1f, 6f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 1f, 1.9f)
            )

            val EquipT05d = EqyuipT05.addOrReplaceChild(
                "EquipT05d",
                CubeListBuilder.create().texOffs(0, 8).addBox(-0.5f, 0f, -2f, 1f, 6f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 1f, -1.9f)
            )

            val Cloth00 = BodyMain.addOrReplaceChild(
                "Cloth00",
                CubeListBuilder.create().texOffs(56, 41).addBox(-6f, 0f, -2.9f, 12f, 8f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.3f, -1f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(2, 99).addBox(-2f, -2f, -2f, 4f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10.3f, 0.2f, 0.0873f, 0f, 0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -1.5f, -1f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 77).addBox(-8f, -8f, -7.4f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.6f, 0.1f)
            )

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(52, 56).addBox(-8.5f, 0f, 0f, 17f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -7f)
            )

            val Ahoke01 = Head.addOrReplaceChild(
                "Ahoke01",
                CubeListBuilder.create().texOffs(50, 77).addBox(-2f, 0f, 0f, 4f, 8f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, -15f, 0f, -2.0071f, 0.5236f, 0f)
            )

            val Ahoke02 = Ahoke01.addOrReplaceChild(
                "Ahoke02",
                CubeListBuilder.create().texOffs(50, 77).addBox(-2f, 0f, 0f, 4f, 8f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7.9f, 0f, 1.0472f, -0.0524f, 0f)
            )

            val Ahoke03 = Ahoke02.addOrReplaceChild(
                "Ahoke03",
                CubeListBuilder.create().texOffs(50, 79).addBox(-2f, 0f, 0f, 4f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7.9f, 0f, 0.7854f, 0.0524f, 0f)
            )

            val Ahoke04 = Ahoke03.addOrReplaceChild(
                "Ahoke04",
                CubeListBuilder.create().texOffs(50, 77).addBox(-2f, 0f, 0f, 4f, 8f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.9f, 0f, 0.4363f, 0.0524f, 0f)
            )

            val Ahoke05 = Ahoke04.addOrReplaceChild(
                "Ahoke05",
                CubeListBuilder.create().texOffs(50, 79).addBox(-2f, 0f, 0f, 4f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7.9f, 0f, -0.1745f, 0.0873f, 0f)
            )

            val Ahoke06 = Ahoke05.addOrReplaceChild(
                "Ahoke06",
                CubeListBuilder.create().texOffs(42, 90).addBox(-2f, 0f, 0f, 4f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.9f, 0f, -0.4363f, 0.0873f, 0f)
            )

            val Ahoke01a = Head.addOrReplaceChild(
                "Ahoke01a",
                CubeListBuilder.create().texOffs(50, 79).addBox(-2f, 0f, 0f, 4f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, -1.5f, -2.2689f, -2.618f, 0f)
            )

            val Ahoke02a = Ahoke01a.addOrReplaceChild(
                "Ahoke02a",
                CubeListBuilder.create().texOffs(50, 79).addBox(-2f, 0f, 0f, 4f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.9f, 0f, 0.7854f, -0.0524f, 0f)
            )

            val Ahoke03a = Ahoke02a.addOrReplaceChild(
                "Ahoke03a",
                CubeListBuilder.create().texOffs(50, 79).addBox(-2f, 0f, 0f, 4f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.9f, 0f, 1.0472f, 0.0524f, 0f)
            )

            val Ahoke04a = Ahoke03a.addOrReplaceChild(
                "Ahoke04a",
                CubeListBuilder.create().texOffs(50, 77).addBox(-2f, 0f, 0f, 4f, 8f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.9f, 0f, 0.4887f, 0.0524f, 0f)
            )

            val Ahoke05a = Ahoke04a.addOrReplaceChild(
                "Ahoke05a",
                CubeListBuilder.create().texOffs(50, 77).addBox(-2f, 0f, 0f, 4f, 8f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7.9f, 0f, -0.2618f, 0.0873f, 0f)
            )

            val Ahoke06a = Ahoke05a.addOrReplaceChild(
                "Ahoke06a",
                CubeListBuilder.create().texOffs(42, 89).mirror().addBox(-2f, 0f, 0f, 4f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7.9f, 0f, -0.5236f, 0.0873f, 0f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(46, 104).addBox(-7.5f, 0f, 0f, 15f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.8f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(80, 0).addBox(-7.5f, 0f, 0f, 15f, 12f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1f, 0.2618f, 0f, 0f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(80, 22).addBox(-8f, 0f, -5f, 16f, 11f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9.5f, 5.8f, -0.0873f, 0f, 0f)
            )

            val EquipBase = BodyMain.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -5f, 0f)
            )

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(0, 66).addBox(-6f, 0f, 0f, 12f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3.3f, -4.3f, 0.0524f, 0f, 0f)
            )

            val Cloth02 = Cloth01.addOrReplaceChild(
                "Cloth02",
                CubeListBuilder.create().texOffs(0, 53).addBox(-7f, 0f, 0f, 14f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3f, -0.3f, 0.0873f, 0f, 0f)
            )

            val Cloth03 = Cloth02.addOrReplaceChild(
                "Cloth03",
                CubeListBuilder.create().texOffs(0, 40).addBox(-8f, 0f, 0f, 16f, 3f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.3f, -0.2f, 0.0873f, 0f, 0f)
            )

            val Cloth04 = Cloth03.addOrReplaceChild(
                "Cloth04",
                CubeListBuilder.create().texOffs(0, 26).addBox(-9f, 0f, 0f, 18f, 3f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, -0.3f, -0.0524f, 0f, 0f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(2, 99).addBox(-3f, -1f, -2f, 4f, 8f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, -9.3f, -0.7f, 0.1396f, 0f, 0.6109f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(2, 99).addBox(0f, 0f, -4f, 4f, 9f, 4f, CubeDeformation(0f)),
                PartPose.offset(-3f, 7f, 2f)
            )

            val EquipHandRing = ArmRight02.addOrReplaceChild(
                "EquipHandRing",
                CubeListBuilder.create().texOffs(0, 91).addBox(-2.5f, 0f, -2.5f, 5f, 2f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 4f, -2f)
            )

            val EquipTBase_2 = ArmRight02.addOrReplaceChild(
                "EquipTBase_2",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.6f, 9f, -2f, 0.0873f, 0f, 0f)
            )

            val EqyuipT01_2 = EquipTBase_2.addOrReplaceChild(
                "EqyuipT01_2",
                CubeListBuilder.create().texOffs(5, 0).addBox(-2f, 0f, -2f, 4f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -1.5708f, 0f, 0f)
            )

            val EqyuipT02_2 = EqyuipT01_2.addOrReplaceChild(
                "EqyuipT02_2",
                CubeListBuilder.create().texOffs(4, 0).addBox(-2f, 0f, -2f, 4f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, 6.9f, 0f)
            )

            val EqyuipT03_2 = EqyuipT02_2.addOrReplaceChild(
                "EqyuipT03_2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, 0f, -2f, 4f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, 6.9f, 0f)
            )

            val EquipT03a_2 = EqyuipT03_2.addOrReplaceChild(
                "EquipT03a_2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 1f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 6.9f, 0f)
            )

            val EqyuipT04_2 = EqyuipT01_2.addOrReplaceChild(
                "EqyuipT04_2",
                CubeListBuilder.create().texOffs(3, 4).addBox(-2f, 0f, -2f, 4f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, -6.9f, 0f)
            )

            val EqyuipT05_2 = EqyuipT04_2.addOrReplaceChild(
                "EqyuipT05_2",
                CubeListBuilder.create().texOffs(2, 3).addBox(-2f, 0f, -2f, 4f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -6.9f, 0f, 0f, 0f, 0.0214f)
            )

            val EquipT05a_2 = EqyuipT05_2.addOrReplaceChild(
                "EquipT05a_2",
                CubeListBuilder.create().texOffs(8, 7).addBox(-0.5f, 0f, 0f, 1f, 6f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 1f, 1.9f)
            )

            val EquipT05b_2 = EqyuipT05_2.addOrReplaceChild(
                "EquipT05b_2",
                CubeListBuilder.create().texOffs(14, 4).addBox(-0.5f, 0f, 0f, 1f, 6f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.9f, 1f, 0f, 0f, -1.5708f, 0f)
            )

            val EquipT05c_2 = EqyuipT05_2.addOrReplaceChild(
                "EquipT05c_2",
                CubeListBuilder.create().texOffs(0, 4).addBox(-0.5f, 0f, 0f, 1f, 6f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.9f, 1f, 0f, 0f, 1.5708f, 0f)
            )

            val EquipT05d_2 = EqyuipT05_2.addOrReplaceChild(
                "EquipT05d_2",
                CubeListBuilder.create().texOffs(0, 8).addBox(-0.5f, 0f, -2f, 1f, 6f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 1f, -1.9f)
            )

            val RingBase = BodyMain.addOrReplaceChild(
                "RingBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -16f, -0.4f)
            )

            val Ring01 = RingBase.addOrReplaceChild(
                "Ring01",
                CubeListBuilder.create().texOffs(62, 0).addBox(-4f, 0f, -0.5f, 8f, 10f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.5f, 4.3f, 0f, -0.8203f, 1.501f, 0f)
            )

            val Ring02 = Ring01.addOrReplaceChild(
                "Ring02",
                CubeListBuilder.create().texOffs(62, 13).addBox(-4f, -9f, -0.5f, 8f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.3f, 8.5f, 0.2f, 0.2276f, -0.0387f, -2.7925f)
            )

            val Ring03Base = Ring02.addOrReplaceChild(
                "Ring03Base",
                CubeListBuilder.create().texOffs(0, 0).addBox(2f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, -10f, 1.7f, -0.6109f, 0.1745f, -0.1047f)
            )

            val Ring03b = Ring03Base.addOrReplaceChild(
                "Ring03b",
                CubeListBuilder.create().texOffs(36, 0).addBox(0f, -2f, -2f, 9f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.9f, 2f, 0f, 0f, 0f, -1.5708f)
            )

            val Ring03c = Ring03Base.addOrReplaceChild(
                "Ring03c",
                CubeListBuilder.create().texOffs(36, 0).addBox(0f, -2f, -2f, 9f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offset(-4f, -8.9f, 0f)
            )

            val Ring03a = Ring03Base.addOrReplaceChild(
                "Ring03a",
                CubeListBuilder.create().texOffs(36, 0).addBox(0f, -2f, -2f, 9f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val Ring03e = Ring03Base.addOrReplaceChild(
                "Ring03e",
                CubeListBuilder.create().texOffs(36, 0).addBox(0f, -2f, -1f, 1f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, -10.8f, 0f, 0f, 0f, 0.0873f)
            )

            val Ring03d = Ring03Base.addOrReplaceChild(
                "Ring03d",
                CubeListBuilder.create().texOffs(36, 0).addBox(0f, -2f, -2f, 9f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, -1.9f, 0f, 0f, 0f, -1.5708f)
            )

            val Ring03f = Ring03Base.addOrReplaceChild(
                "Ring03f",
                CubeListBuilder.create().texOffs(36, 0).addBox(-1f, -2f, -1f, 1f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, -10.8f, 0f, 0f, 0f, -0.0873f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 2f, 0f, -0.0873f, 0f, 0f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -10.3f, 0.2f, 0.0873f, 0f, 0f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -1.5f, -1f)
            )
            addFaceLayer(GlowHead)

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
