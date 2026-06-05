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
import org.trp.shincolle.entity.EntityCAHime
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import kotlin.math.cos
import kotlin.math.sin

class ModelCAHime<T : EntityShipBase?>(root: ModelPart) : ShipModelHumanoidBase<T?>(), IGlowableModel {
    private var isDeadPose = false
    private var isSittingPose = false
    private var poseTranslateY = 0f

    private val BodyMain: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val LegLeft01: ModelPart
    private val LegRight01: ModelPart
    private val Neck: ModelPart
    private val Head: ModelPart
    private val TailBase: ModelPart
    private val Band01: ModelPart
    private val Band02: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmRight02: ModelPart
    private val LegLeft02: ModelPart
    private val LegRight02: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val Ear01: ModelPart
    private val Ear02: ModelPart
    private val Horn01: ModelPart
    private val Horn02: ModelPart
    private val HatBase: ModelPart
    private val Ahoke: ModelPart
    private val Hair01: ModelPart
    private val Hair02a: ModelPart
    private val Hair02b: ModelPart
    private val Hair03a: ModelPart
    private val Hair03b: ModelPart
    private val Horn03: ModelPart
    private val HatL: ModelPart
    private val HatR: ModelPart
    private val HatEyeL: ModelPart
    private val HatEyeR: ModelPart
    private val Tail01: ModelPart
    private val Tail01_1: ModelPart
    private val Tail02: ModelPart
    private val Tail03: ModelPart
    private val Tail04: ModelPart
    private val Tail05: ModelPart
    private val Tail06: ModelPart
    private val Tail07: ModelPart
    private val Tail08: ModelPart
    private val Tail09: ModelPart
    private val TailHead01: ModelPart
    private val TailJaw01: ModelPart
    private val TailC01: ModelPart
    private val TailC02: ModelPart
    private val Tail02_1: ModelPart
    private val Tail03_1: ModelPart
    private val Tail04_1: ModelPart
    private val Tail05_1: ModelPart
    private val Tail06_1: ModelPart
    private val Tail07_1: ModelPart
    private val Tail08_1: ModelPart
    private val Tail09_1: ModelPart
    private val TailHead01_1: ModelPart
    private val TailJaw01_1: ModelPart
    private val TailC01_1: ModelPart
    private val TailC02_1: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowHead: ModelPart
    private val headDefaultY: Float
    private val hatBaseDefaultY: Float
    private val hatBaseDefaultZ: Float
    private val armLeft01DefaultZ: Float
    private val armRight01DefaultZ: Float
    private val tailBaseDefaultY: Float
    private val tailBaseDefaultZ: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Neck = this.BodyMain.getChild("Neck")
        this.LegRight01 = this.BodyMain.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.LegLeft01 = this.BodyMain.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.Band02 = this.BodyMain.getChild("Band02")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.TailBase = this.BodyMain.getChild("TailBase")
        this.Tail01 = this.TailBase.getChild("Tail01")
        this.Tail02 = this.Tail01.getChild("Tail02")
        this.Tail03 = this.Tail02.getChild("Tail03")
        this.Tail04 = this.Tail03.getChild("Tail04")
        this.Tail05 = this.Tail04.getChild("Tail05")
        this.Tail06 = this.Tail05.getChild("Tail06")
        this.Tail07 = this.Tail06.getChild("Tail07")
        this.Tail08 = this.Tail07.getChild("Tail08")
        this.Tail09 = this.Tail08.getChild("Tail09")
        this.TailHead01 = this.Tail09.getChild("TailHead01")
        this.TailC01 = this.TailHead01.getChild("TailC01")
        this.TailC02 = this.TailHead01.getChild("TailC02")
        this.TailJaw01 = this.Tail09.getChild("TailJaw01")
        this.Tail01_1 = this.TailBase.getChild("Tail01_1")
        this.Tail02_1 = this.Tail01_1.getChild("Tail02_1")
        this.Tail03_1 = this.Tail02_1.getChild("Tail03_1")
        this.Tail04_1 = this.Tail03_1.getChild("Tail04_1")
        this.Tail05_1 = this.Tail04_1.getChild("Tail05_1")
        this.Tail06_1 = this.Tail05_1.getChild("Tail06_1")
        this.Tail07_1 = this.Tail06_1.getChild("Tail07_1")
        this.Tail08_1 = this.Tail07_1.getChild("Tail08_1")
        this.Tail09_1 = this.Tail08_1.getChild("Tail09_1")
        this.TailHead01_1 = this.Tail09_1.getChild("TailHead01_1")
        this.TailC02_1 = this.TailHead01_1.getChild("TailC02_1")
        this.TailC01_1 = this.TailHead01_1.getChild("TailC01_1")
        this.TailJaw01_1 = this.Tail09_1.getChild("TailJaw01_1")
        this.Head = this.BodyMain.getChild("Head")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair03a = this.HairMain.getChild("Hair03a")
        this.Hair02b = this.HairMain.getChild("Hair02b")
        this.Hair02a = this.HairMain.getChild("Hair02a")
        this.Hair03b = this.HairMain.getChild("Hair03b")
        this.Ear01 = this.Head.getChild("Ear01")
        this.Horn02 = this.Head.getChild("Horn02")
        this.Horn03 = this.Horn02.getChild("Horn03")
        this.Hair = this.Head.getChild("Hair")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HatBase = this.Head.getChild("HatBase")
        this.HatL = this.HatBase.getChild("HatL")
        this.HatEyeL = this.HatL.getChild("HatEyeL")
        this.HatR = this.HatBase.getChild("HatR")
        this.HatEyeR = this.HatR.getChild("HatEyeR")
        this.Ear02 = this.Head.getChild("Ear02")
        this.Horn01 = this.Head.getChild("Horn01")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.Band01 = this.BodyMain.getChild("Band01")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowHead = this.GlowBodyMain.getChild("GlowHead")
        initFaceParts(this.GlowHead)
        this.headDefaultY = this.Head.y
        this.hatBaseDefaultY = this.HatBase.y
        this.hatBaseDefaultZ = this.HatBase.z
        this.armLeft01DefaultZ = this.ArmLeft01.z
        this.armRight01DefaultZ = this.ArmRight01.z
        this.tailBaseDefaultY = this.TailBase.y
        this.tailBaseDefaultZ = this.TailBase.z
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
        this.applyFaceAndMouth(entity)

        if (entity is EntityShipBase && entity.isInDeadPose) {
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

        this.Head.y = this.headDefaultY
        this.HatBase.y = this.hatBaseDefaultY
        this.HatBase.z = this.hatBaseDefaultZ
        this.ArmLeft01.z = this.armLeft01DefaultZ
        this.ArmRight01.z = this.armRight01DefaultZ
        this.TailBase.y = this.tailBaseDefaultY
        this.TailBase.z = this.tailBaseDefaultZ
    }

    private fun applyEquipVisibility(entity: T?) {
        val showTail1 = entity!!.getEquipFlag(EntityCAHime.EQUIP_TAIL_1)
        val showTail2 = entity.getEquipFlag(EntityCAHime.EQUIP_TAIL_2)
        val showHat1 = entity.getEquipFlag(EntityCAHime.EQUIP_HAT_1)
        val showHat2 = entity.getEquipFlag(EntityCAHime.EQUIP_HAT_2)
        val showHat3 = entity.getEquipFlag(EntityCAHime.EQUIP_HAT_3)

        this.TailBase.visible = showTail1 || showTail2
        if (showHat2 || showHat3) {
            this.HatBase.visible = true
            this.Hair01.visible = false
            this.Horn01.visible = true
            this.Horn02.visible = true
            this.Ear01.visible = true
            this.Ear02.visible = true
        } else if (showHat1) {
            this.HatBase.visible = true
            this.Hair01.visible = false
            this.Horn01.visible = false
            this.Horn02.visible = false
            this.Ear01.visible = false
            this.Ear02.visible = false
        } else {
            this.HatBase.visible = false
            this.Hair01.visible = true
            this.Horn01.visible = true
            this.Horn02.visible = true
            this.Ear01.visible = true
            this.Ear02.visible = true
        }
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        this.Head.xRot = 0.7853f
        this.Head.yRot = 0.0f
        this.Ahoke.xRot = -0.2618f
        this.BodyMain.xRot = 0.0f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = -1.4835f

        this.ArmLeft01.xRot = -0.4f
        this.ArmLeft01.zRot = 0.4537f
        this.ArmLeft02.zRot = 0.0f
        this.ArmRight01.xRot = -0.8f
        this.ArmRight01.zRot = -0.05f
        this.ArmRight02.zRot = 0.0f

        this.LegLeft01.xRot = 0.5f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.4537f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.zRot = 0.0f
        this.LegRight01.xRot = 0.8f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.05f
        this.LegRight02.xRot = 0.0f
        this.LegRight02.zRot = 0.0f

        this.HatBase.xRot = 0.0f

        this.TailHead01.xRot = -0.17f
        this.TailJaw01.xRot = 0.26f
        this.TailHead01_1.xRot = 0.0f
        this.TailJaw01_1.xRot = 0.2f
        this.Tail01.xRot = -1.4f
        this.Tail01.yRot = 1.57f
        this.Tail02.xRot = -0.3f
        this.Tail02.yRot = 0.2f
        this.Tail03.xRot = -0.3f
        this.Tail03.yRot = 0.3f
        this.Tail04.xRot = 0.2f
        this.Tail04.yRot = 0.4f
        this.Tail05.xRot = 0.1f
        this.Tail05.yRot = 0.5f
        this.Tail06.xRot = -0.1f
        this.Tail06.yRot = 0.4f
        this.Tail07.xRot = -0.1f
        this.Tail07.yRot = 0.3f
        this.Tail08.xRot = 0.1f
        this.Tail08.yRot = 0.2f
        this.Tail09.xRot = 0.0f
        this.Tail09.yRot = 0.1f

        this.Tail01_1.xRot = -1.4f
        this.Tail01_1.yRot = -1.7f
        this.Tail02_1.xRot = -0.2f
        this.Tail02_1.yRot = 0.2f
        this.Tail03_1.xRot = -0.1f
        this.Tail03_1.yRot = 0.3f
        this.Tail04_1.xRot = 0.0f
        this.Tail04_1.yRot = 0.4f
        this.Tail05_1.xRot = 0.0f
        this.Tail05_1.yRot = 0.5f
        this.Tail06_1.xRot = -0.1f
        this.Tail06_1.yRot = 0.4f
        this.Tail07_1.xRot = -0.1f
        this.Tail07_1.yRot = 0.3f
        this.Tail08_1.xRot = 0.2f
        this.Tail08_1.yRot = 0.2f
        this.Tail09_1.xRot = -0.2f
        this.Tail09_1.yRot = 0.3f
    }

    private fun applyBasePose(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val angleX = cos((ageInTicks * 0.08f + limbSwing * 0.25f).toDouble()).toFloat()
        val angleX1 = cos((ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f).toDouble()).toFloat()
        val angleX2 = cos((ageInTicks * 0.08f + 0.6f + limbSwing * 0.5f).toDouble()).toFloat()
        val angleAdd1 = cos((limbSwing * 0.7f).toDouble()).toFloat() * limbSwingAmount
        val angleAdd2 = cos(limbSwing * 0.7f + Math.PI).toFloat() * limbSwingAmount

        if (entity!!.isInWater()) {
            this.poseTranslateY += angleX * 0.05f + 0.025f
        } else {
            this.poseTranslateY += 0.5f
        }

        var addk1 = angleAdd1 * 0.35f - 0.14f
        var addk2 = angleAdd2 * 0.35f + 0.14f

        if (entity.isSprinting || limbSwingAmount > 0.8f) {
            addk1 *= 2.0f
            addk2 *= 2.0f
        }

        this.ArmRight01.xRot = addk1
        this.ArmLeft01.xRot = addk2

        this.Head.xRot = headPitch * (Math.PI.toFloat() / 180f)
        this.Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)

        this.Ahoke.xRot = angleX * 0.05f - 0.2618f
        this.BodyMain.xRot = 0.0f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f

        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = 0.21f
        this.ArmLeft02.zRot = 0.0f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = -0.21f
        this.ArmRight02.zRot = 0.0f

        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.1745f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.zRot = 0.0f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.1745f
        this.LegRight02.xRot = 0.0f
        this.LegRight02.zRot = 0.0f

        this.HatBase.xRot = 0.0f

        val cosf2 = FloatArray(9)
        for (i in 0..8) {
            cosf2[i] = cos((ageInTicks * 0.1f + limbSwing * 0.25f + 0.8f * i).toDouble()).toFloat()
        }

        this.TailHead01.xRot = -angleX * 0.075f - 0.1f
        this.TailJaw01.xRot = angleX * 0.1f + 0.18f
        this.TailHead01_1.xRot = -angleX2 * 0.12f - 0.1f
        this.TailJaw01_1.xRot = angleX2 * 0.15f + 0.26f
        this.TailC01.xRot = angleX1 * 0.3f - 0.2f
        this.TailC02.xRot = angleX2 * 0.3f - 0.2f
        this.TailC01_1.xRot = angleX1 * 0.3f - 0.2f
        this.TailC02_1.xRot = angleX2 * 0.3f - 0.2f

        val showTail1 = entity.getEquipFlag(EntityCAHime.EQUIP_TAIL_1)
        val showTail2 = entity.getEquipFlag(EntityCAHime.EQUIP_TAIL_2)
        val showTailBoth = showTail1 && showTail2
        if (showTailBoth) {
            this.TailBase.y = this.tailBaseDefaultY + (-0.15f * OFFSET_SCALE)
            this.TailBase.z = this.tailBaseDefaultZ
            this.Tail01.xRot = 0.26f
            this.Tail01.yRot = 1.7f + cosf2[0] * 0.015f
            this.Tail02.xRot = 0.61f
            this.Tail02.yRot = -0.09f + cosf2[1] * 0.02f
            this.Tail03.xRot = 0.61f
            this.Tail03.yRot = -0.09f + cosf2[2] * 0.025f
            this.Tail04.xRot = 0.52f
            this.Tail04.yRot = cosf2[3] * 0.03f
            this.Tail05.xRot = 0.52f
            this.Tail05.yRot = cosf2[4] * 0.04f
            this.Tail06.xRot = 0.35f
            this.Tail06.yRot = cosf2[5] * 0.05f
            this.Tail07.xRot = 0.17f
            this.Tail07.yRot = 0.1f + cosf2[6] * 0.06f
            this.Tail08.xRot = 0.09f
            this.Tail08.yRot = 0.1f + cosf2[7] * 0.08f
            this.Tail09.xRot = -0.09f
            this.Tail09.yRot = 0.5f + cosf2[8] * 0.15f

            this.Tail01_1.xRot = 0.7f
            this.Tail01_1.yRot = -1.57f + cosf2[0] * 0.02f
            this.Tail02_1.xRot = 0.35f
            this.Tail02_1.yRot = 0.26f + cosf2[1] * 0.03f
            this.Tail03_1.xRot = 0.44f
            this.Tail03_1.yRot = 0.35f + cosf2[2] * 0.04f
            this.Tail04_1.xRot = 0.35f
            this.Tail04_1.yRot = 0.44f + cosf2[3] * 0.05f
            this.Tail05_1.xRot = 0.52f
            this.Tail05_1.yRot = 0.35f + cosf2[4] * 0.06f
            this.Tail06_1.xRot = 0.09f
            this.Tail06_1.yRot = 0.26f + cosf2[5] * 0.07f
            this.Tail07_1.xRot = -0.35f
            this.Tail07_1.yRot = 0.35f + cosf2[6] * 0.08f
            this.Tail08_1.xRot = -0.52f
            this.Tail08_1.yRot = 0.35f + cosf2[7] * 0.09f
            this.Tail09_1.xRot = -0.09f
            this.Tail09_1.yRot = 0.44f + cosf2[8] * 0.12f
        } else if (showTail1) {
            this.TailBase.y = this.tailBaseDefaultY + (-0.15f * OFFSET_SCALE)
            this.TailBase.z = this.tailBaseDefaultZ
            this.Tail01.xRot = -0.17f + cosf2[0] * 0.03f
            this.Tail01.yRot = 1.3f + cosf2[0] * 0.03f
            this.Tail02.xRot = 0.26f + cosf2[1] * 0.03f
            this.Tail02.yRot = -0.52f + cosf2[1] * 0.03f
            this.Tail03.xRot = 0.35f + cosf2[2] * 0.03f
            this.Tail03.yRot = -0.52f + cosf2[2] * 0.03f
            this.Tail04.xRot = 0.52f + cosf2[3] * 0.03f
            this.Tail04.yRot = -0.44f + cosf2[3] * 0.03f
            this.Tail05.xRot = 0.52f + cosf2[4] * 0.04f
            this.Tail05.yRot = -0.17f + cosf2[4] * 0.04f
            this.Tail06.xRot = 0.35f + cosf2[5] * 0.05f
            this.Tail06.yRot = 0.35f + cosf2[5] * 0.05f
            this.Tail07.xRot = 0.44f + cosf2[6] * 0.06f
            this.Tail07.yRot = 0.17f + cosf2[6] * 0.06f
            this.Tail08.xRot = 0.52f + cosf2[7] * 0.08f
            this.Tail08.yRot = 0.17f + cosf2[7] * 0.08f
            this.Tail09.xRot = 0.52f + cosf2[8] * 0.15f
            this.Tail09.yRot = 0.17f + cosf2[8] * 0.15f

            this.Tail01_1.xRot = -0.17f + cosf2[0] * 0.03f
            this.Tail01_1.yRot = -1.3f + cosf2[0] * 0.03f
            this.Tail02_1.xRot = 0.26f + cosf2[1] * 0.03f
            this.Tail02_1.yRot = 0.52f + cosf2[1] * 0.03f
            this.Tail03_1.xRot = 0.35f + cosf2[2] * 0.03f
            this.Tail03_1.yRot = 0.52f + cosf2[2] * 0.03f
            this.Tail04_1.xRot = 0.52f + cosf2[3] * 0.03f
            this.Tail04_1.yRot = 0.44f + cosf2[3] * 0.03f
            this.Tail05_1.xRot = 0.52f + cosf2[4] * 0.04f
            this.Tail05_1.yRot = 0.17f + cosf2[4] * 0.04f
            this.Tail06_1.xRot = 0.35f + cosf2[5] * 0.05f
            this.Tail06_1.yRot = -0.35f + cosf2[5] * 0.05f
            this.Tail07_1.xRot = 0.44f + cosf2[6] * 0.06f
            this.Tail07_1.yRot = -0.17f + cosf2[6] * 0.06f
            this.Tail08_1.xRot = 0.52f + cosf2[7] * 0.08f
            this.Tail08_1.yRot = -0.17f + cosf2[7] * 0.08f
            this.Tail09_1.xRot = 0.52f + cosf2[8] * 0.15f
            this.Tail09_1.yRot = -0.17f + cosf2[8] * 0.15f
        } else if (showTail2) {
            this.TailBase.y = this.tailBaseDefaultY + (-0.54f * OFFSET_SCALE)
            this.TailBase.z = this.tailBaseDefaultZ + (0.86f * OFFSET_SCALE)
            this.Tail01.xRot = -0.17f + cosf2[0] * 0.03f
            this.Tail01.yRot = 1.3f + cosf2[0] * 0.03f
            this.Tail02.xRot = 0.26f + cosf2[1] * 0.03f
            this.Tail02.yRot = -0.52f + cosf2[1] * 0.03f
            this.Tail03.xRot = 0.35f + cosf2[2] * 0.03f
            this.Tail03.yRot = -0.52f + cosf2[2] * 0.03f
            this.Tail04.xRot = 0.52f + cosf2[3] * 0.03f
            this.Tail04.yRot = -0.44f + cosf2[3] * 0.03f
            this.Tail05.xRot = 0.52f + cosf2[4] * 0.04f
            this.Tail05.yRot = -0.17f + cosf2[4] * 0.04f
            this.Tail06.xRot = 0.35f + cosf2[5] * 0.05f
            this.Tail06.yRot = 0.35f + cosf2[5] * 0.05f
            this.Tail07.xRot = 0.44f + cosf2[6] * 0.06f
            this.Tail07.yRot = 0.17f + cosf2[6] * 0.06f
            this.Tail08.xRot = 0.52f + cosf2[7] * 0.08f
            this.Tail08.yRot = 0.17f + cosf2[7] * 0.08f
            this.Tail09.xRot = 0.52f + cosf2[8] * 0.15f
            this.Tail09.yRot = 0.17f + cosf2[8] * 0.15f

            this.Tail01_1.xRot = -0.17f + cosf2[0] * 0.03f
            this.Tail01_1.yRot = -1.3f + cosf2[0] * 0.03f
            this.Tail02_1.xRot = 0.26f + cosf2[1] * 0.03f
            this.Tail02_1.yRot = 0.52f + cosf2[1] * 0.03f
            this.Tail03_1.xRot = 0.35f + cosf2[2] * 0.03f
            this.Tail03_1.yRot = 0.52f + cosf2[2] * 0.03f
            this.Tail04_1.xRot = 0.52f + cosf2[3] * 0.03f
            this.Tail04_1.yRot = 0.44f + cosf2[3] * 0.03f
            this.Tail05_1.xRot = 0.52f + cosf2[4] * 0.04f
            this.Tail05_1.yRot = 0.17f + cosf2[4] * 0.04f
            this.Tail06_1.xRot = 0.35f + cosf2[5] * 0.05f
            this.Tail06_1.yRot = -0.35f + cosf2[5] * 0.05f
            this.Tail07_1.xRot = 0.44f + cosf2[6] * 0.06f
            this.Tail07_1.yRot = -0.17f + cosf2[6] * 0.06f
            this.Tail08_1.xRot = 0.52f + cosf2[7] * 0.08f
            this.Tail08_1.yRot = -0.17f + cosf2[7] * 0.08f
            this.Tail09_1.xRot = 0.52f + cosf2[8] * 0.15f
            this.Tail09_1.yRot = -0.17f + cosf2[8] * 0.15f
        }

        var modf2 = ageInTicks % 128.0f
        if (modf2 < 6.0f) {
            if (modf2 >= 3.0f) {
                modf2 -= 3.0f
            }
            val anglef2 = sin((modf2 * 1.0472f).toDouble()).toFloat() * 0.25f
            this.Ear01.zRot = anglef2 + 0.1745f
            this.Ear02.zRot = -anglef2 - 0.1745f
        } else {
            this.Ear01.zRot = 0.1745f
            this.Ear02.zRot = -0.1745f
        }

        this.LegLeft01.xRot = addk1
        this.LegRight01.xRot = addk2
    }

    private fun applySpecialPoseAdjustments(entity: T?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        val isCrouching = entity!!.isCrouching()
        val isPassenger = entity.isPassenger()
        val isSitting = entity.isInSittingPose || (isPassenger && entity.getVehicle() !is EntityMountBase)
        val hat1 = entity.getEquipFlag(EntityCAHime.EQUIP_HAT_1)
        val hat2 = entity.getEquipFlag(EntityCAHime.EQUIP_HAT_2)
        val hat3 = entity.getEquipFlag(EntityCAHime.EQUIP_HAT_3)
        val hatBoth = hat1 && hat2

        if (isCrouching) {
            this.Head.y = this.headDefaultY + (0.2f * OFFSET_SCALE)
        }

        if (isSitting) {
            this.isSittingPose = true
            if (entity != null && hasLegacyState(entity, 1, 4)) {
                if (hatBoth) {
                    this.HatBase.xRot = -1.8f
                    this.HatBase.y = this.hatBaseDefaultY + (0.3f * OFFSET_SCALE)
                    this.HatBase.z = this.hatBaseDefaultZ + (0.07f * OFFSET_SCALE)
                } else if (hat1) {
                    this.HatBase.xRot = 1.37f
                    this.HatBase.y = this.hatBaseDefaultY + (-0.45f * OFFSET_SCALE)
                    this.HatBase.z = this.hatBaseDefaultZ + (-0.2f * OFFSET_SCALE)
                } else if (hat3) {
                    this.HatBase.xRot = -0.85f
                    this.HatBase.y = this.hatBaseDefaultY + (0.1f * OFFSET_SCALE)
                    this.HatBase.z = this.hatBaseDefaultZ + (0.07f * OFFSET_SCALE)
                } else {
                    this.HatBase.xRot = 0.0f
                    this.HatBase.y = this.hatBaseDefaultY
                    this.HatBase.z = this.hatBaseDefaultZ
                }
                this.poseTranslateY = SIT_TRANSLATE_Y
                this.Head.xRot = -0.2f
                this.Head.zRot = -0.09f
                this.BodyMain.zRot = 0.09f
                this.ArmLeft01.xRot = -1.31f
                this.ArmLeft01.yRot = 0.17f
                this.ArmLeft01.zRot = 0.0f
                this.ArmLeft02.zRot = 0.0f
                this.ArmRight01.xRot = -1.22f
                this.ArmRight01.yRot = 1.05f
                this.ArmRight01.zRot = 0.0f
                this.ArmRight02.zRot = 0.0f
                this.LegLeft01.xRot = 1.31f
                this.LegLeft01.yRot = -0.7f
                this.LegLeft01.zRot = 0.0f
                this.LegRight01.xRot = 1.22f
                this.LegRight01.yRot = -0.87f
                this.LegRight01.zRot = 0.0f
            } else if (entity != null && hasLegacyState(entity, 7, 4)) {
                if (hatBoth) {
                    this.HatBase.xRot = -1.8f
                    this.HatBase.y = this.hatBaseDefaultY + (0.6f * OFFSET_SCALE)
                    this.HatBase.z = this.hatBaseDefaultZ + (-0.3f * OFFSET_SCALE)
                } else if (hat1) {
                    this.HatBase.xRot = 1.37f
                    this.HatBase.y = this.hatBaseDefaultY + (-0.45f * OFFSET_SCALE)
                    this.HatBase.z = this.hatBaseDefaultZ + (-0.2f * OFFSET_SCALE)
                } else if (hat3) {
                    this.HatBase.xRot = -0.85f
                    this.HatBase.y = this.hatBaseDefaultY + (0.6f * OFFSET_SCALE)
                    this.HatBase.z = this.hatBaseDefaultZ + (0.07f * OFFSET_SCALE)
                } else {
                    this.HatBase.xRot = 0.0f
                    this.HatBase.y = this.hatBaseDefaultY
                    this.HatBase.z = this.hatBaseDefaultZ
                }
                this.poseTranslateY = 0.22f * 4.1f
                this.Head.xRot = 1.5359f
                this.Head.y = this.headDefaultY + (0.25f * OFFSET_SCALE)
                this.ArmLeft01.xRot = -1.5359f
                this.ArmLeft01.zRot = 0.0f
                this.ArmLeft01.z = this.armLeft01DefaultZ + (-0.18f * OFFSET_SCALE)
                this.ArmLeft02.zRot = 0.0f
                this.ArmRight01.xRot = -1.5359f
                this.ArmRight01.zRot = 0.0f
                this.ArmRight01.z = this.armRight01DefaultZ + (-0.18f * OFFSET_SCALE)
                this.ArmRight02.zRot = 0.0f
                this.LegLeft01.xRot = 1.5359f
                this.LegRight01.xRot = 1.5359f
            } else {
                if (hatBoth) {
                    this.HatBase.xRot = -1.8f
                    this.HatBase.y = this.hatBaseDefaultY + (0.2f * OFFSET_SCALE)
                    this.HatBase.z = this.hatBaseDefaultZ + (0.07f * OFFSET_SCALE)
                } else if (hat1) {
                    this.HatBase.xRot = 1.37f
                    this.HatBase.y = this.hatBaseDefaultY + (-0.45f * OFFSET_SCALE)
                    this.HatBase.z = this.hatBaseDefaultZ + (-0.2f * OFFSET_SCALE)
                } else if (hat3) {
                    this.HatBase.xRot = -0.85f
                    this.HatBase.y = this.hatBaseDefaultY
                    this.HatBase.z = this.hatBaseDefaultZ + (0.07f * OFFSET_SCALE)
                } else {
                    this.HatBase.xRot = 0.0f
                    this.HatBase.y = this.hatBaseDefaultY
                    this.HatBase.z = this.hatBaseDefaultZ
                }
                this.poseTranslateY = 0.22f * 4.1f
                this.Head.xRot = -0.5f
                this.Head.y = this.headDefaultY + (0.25f * OFFSET_SCALE)
                this.ArmLeft01.xRot = -1.5359f
                this.ArmLeft01.zRot = 0.0f
                this.ArmLeft01.z = this.armLeft01DefaultZ + (-0.18f * OFFSET_SCALE)
                this.ArmLeft02.zRot = 1.1868f
                this.ArmRight01.xRot = -1.5359f
                this.ArmRight01.zRot = 0.0f
                this.ArmRight01.z = this.armRight01DefaultZ + (-0.18f * OFFSET_SCALE)
                this.ArmRight02.zRot = -1.1868f
                this.LegLeft01.xRot = 1.5359f
                this.LegRight01.xRot = 1.5359f
            }
        }

        if (entity != null && entity.attackTick > 30) {
            this.TailHead01.xRot = -0.6f
            this.TailJaw01.xRot = 0.5f
            this.TailHead01_1.xRot = -0.6f
            this.TailJaw01_1.xRot = 0.5f
            this.TailC01.xRot = -0.1f
            this.TailC02.xRot = -0.1f
            this.TailC01_1.xRot = -0.1f
            this.TailC02_1.xRot = -0.1f
            this.Tail01.xRot = 0.2f
            this.Tail01.yRot = 1.2f
            this.Tail02.xRot = 0.4f
            this.Tail02.yRot = -0.5f
            this.Tail03.xRot = 0.4f
            this.Tail03.yRot = -0.32f
            this.Tail04.xRot = 0.4f
            this.Tail04.yRot = 0.4f
            this.Tail05.xRot = 0.2f
            this.Tail05.yRot = 0.4f
            this.Tail06.xRot = 0.3f
            this.Tail06.yRot = 0.4f
            this.Tail07.xRot = 0.2f
            this.Tail07.yRot = 0.4f
            this.Tail08.xRot = 0.1f
            this.Tail08.yRot = 0.3f
            this.Tail09.xRot = 0.1f
            this.Tail09.yRot = 0.3f
            this.Tail01_1.xRot = -0.17f
            this.Tail01_1.yRot = -1.5f
            this.Tail02_1.xRot = 0.26f
            this.Tail02_1.yRot = 0.52f
            this.Tail03_1.xRot = 0.35f
            this.Tail03_1.yRot = 0.52f
            this.Tail04_1.xRot = 0.52f
            this.Tail04_1.yRot = 0.3f
            this.Tail05_1.xRot = 0.52f
            this.Tail05_1.yRot = 0.17f
            this.Tail06_1.xRot = 0.35f
            this.Tail06_1.yRot = -0.35f
            this.Tail07_1.xRot = 0.2f
            this.Tail07_1.yRot = -0.17f
            this.Tail08_1.xRot = 0.3f
            this.Tail08_1.yRot = -0.17f
            this.Tail09_1.xRot = 0.5f
            this.Tail09_1.yRot = -0.17f
            val progress = entity.attackTick + (1.0f - ageInTicks + ageInTicks.toInt())
            if (entity.attackTick > 47) {
                this.TailHead01.xRot = (progress - 50.0f) * 0.3f - 0.1f
                this.TailJaw01.xRot = (50.0f - progress) * 0.3f + 0.1f
            } else if (entity.attackTick > 39) {
                this.TailHead01.xRot = -0.7f + (47.0f - progress) * 0.06f
                this.TailJaw01.xRot = 0.7f - (47.0f - progress) * 0.06f
            } else {
                this.TailHead01.xRot = -0.25f
                this.TailJaw01.xRot = 0.25f
            }
            this.TailHead01_1.xRot = this.TailHead01.xRot
            this.TailJaw01_1.xRot = this.TailJaw01.xRot
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
    }

    private fun syncGlowParts() {
        this.GlowBodyMain.copyFrom(this.BodyMain)
        this.GlowHead.copyFrom(this.Head)
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
        val usePoseTranslate = this.poseTranslateY != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, this.poseTranslateY, 0.0f)
        }

        this.GlowBodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "ca_hime"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelCAHime")
        private val SIT_TRANSLATE_Y = sittingY("ModelCAHime")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 93).addBox(-5.5f, -4.5f, -12f, 11f, 10f, 24f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(0, 78).addBox(-5f, -4f, -4.5f, 10f, 5f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2f, -9.4f, 0.4189f, 0f, 0f)
            )

            val LegRight01 = BodyMain.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(66, 92).mirror()
                    .addBox(-2.5f, 0f, -2.5f, 5f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, 3f, 8.3f, -0.1396f, 0f, -0.1745f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(66, 105).mirror()
                    .addBox(-2.5f, 0f, 0f, 5f, 7f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 8f, -2.5f)
            )

            val LegLeft01 = BodyMain.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(46, 92).addBox(-2.5f, 0f, -2.5f, 5f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, 3f, 8.3f, 0.1396f, 0f, 0.1745f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(46, 105).addBox(-2.5f, 0f, 0f, 5f, 7f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 8f, -2.5f)
            )

            val Band02 = BodyMain.addOrReplaceChild(
                "Band02",
                CubeListBuilder.create().texOffs(40, 39).addBox(-0.5f, 0f, 0f, 1f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.5f, 1.7f, -12f, -0.0873f, 0f, 0f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(0, 92).mirror()
                    .addBox(-2.5f, 0f, -2.5f, 5f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, 3f, -6f, 0.1396f, 0f, -0.2094f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(0, 105).mirror().addBox(0f, 0f, -5f, 5f, 7f, 5f, CubeDeformation(0f)),
                PartPose.offset(-2.5f, 8f, 2.5f)
            )

            val TailBase = BodyMain.addOrReplaceChild(
                "TailBase",
                CubeListBuilder.create().texOffs(57, 21).addBox(-4f, -2f, 0f, 8f, 5f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 7f, -2f)
            )

            val Tail01 = TailBase.addOrReplaceChild(
                "Tail01",
                CubeListBuilder.create().texOffs(58, 16).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.5f, 0f, 3f, 0.2618f, 1.5708f, 0f)
            )

            val Tail02 = Tail01.addOrReplaceChild(
                "Tail02",
                CubeListBuilder.create().texOffs(58, 17).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.6109f, -0.0873f, 0f)
            )

            val Tail03 = Tail02.addOrReplaceChild(
                "Tail03",
                CubeListBuilder.create().texOffs(54, 16).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.6109f, -0.0873f, 0f)
            )

            val Tail04 = Tail03.addOrReplaceChild(
                "Tail04",
                CubeListBuilder.create().texOffs(54, 19).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.5236f, 0f, 0f)
            )

            val Tail05 = Tail04.addOrReplaceChild(
                "Tail05",
                CubeListBuilder.create().texOffs(53, 16).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.5236f, 0f, 0f)
            )

            val Tail06 = Tail05.addOrReplaceChild(
                "Tail06",
                CubeListBuilder.create().texOffs(83, 0).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.3491f, 0f, 0f)
            )

            val Tail07 = Tail06.addOrReplaceChild(
                "Tail07",
                CubeListBuilder.create().texOffs(86, 0).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.1745f, 0.0873f, 0f)
            )

            val Tail08 = Tail07.addOrReplaceChild(
                "Tail08",
                CubeListBuilder.create().texOffs(83, 0).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.0873f, 0.2618f, 0f)
            )

            val Tail09 = Tail08.addOrReplaceChild(
                "Tail09",
                CubeListBuilder.create().texOffs(96, 0).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, -0.0873f, 0.4363f, 0f)
            )

            val TailHead01 = Tail09.addOrReplaceChild(
                "TailHead01",
                CubeListBuilder.create().texOffs(40, 0).addBox(-4.5f, 0f, 0f, 9f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1.8f, 3.5f, -0.1745f, 0f, 0f)
            )

            val TailC01 = TailHead01.addOrReplaceChild(
                "TailC01",
                CubeListBuilder.create().texOffs(100, 8).addBox(-1f, -1f, 0f, 2f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offset(2f, 4.5f, 9.5f)
            )

            val TailC02 = TailHead01.addOrReplaceChild(
                "TailC02",
                CubeListBuilder.create().texOffs(100, 8).addBox(-1f, -1f, 0f, 2f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offset(-2f, 4.5f, 9.5f)
            )

            val TailJaw01 = Tail09.addOrReplaceChild(
                "TailJaw01",
                CubeListBuilder.create().texOffs(90, 18).addBox(-4.5f, -4f, 0f, 9f, 4f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.7f, 3.3f, 0.2618f, 0f, 0f)
            )

            val Tail01_1 = TailBase.addOrReplaceChild(
                "Tail01_1",
                CubeListBuilder.create().texOffs(54, 16).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.5f, 0f, 3f, 0.6981f, -1.5708f, 0f)
            )

            val Tail02_1 = Tail01_1.addOrReplaceChild(
                "Tail02_1",
                CubeListBuilder.create().texOffs(56, 17).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.3491f, 0.2618f, 0f)
            )

            val Tail03_1 = Tail02_1.addOrReplaceChild(
                "Tail03_1",
                CubeListBuilder.create().texOffs(58, 16).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.4363f, 0.3491f, 0f)
            )

            val Tail04_1 = Tail03_1.addOrReplaceChild(
                "Tail04_1",
                CubeListBuilder.create().texOffs(53, 18).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.3491f, 0.4363f, 0f)
            )

            val Tail05_1 = Tail04_1.addOrReplaceChild(
                "Tail05_1",
                CubeListBuilder.create().texOffs(58, 19).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.5236f, 0.3491f, 0f)
            )

            val Tail06_1 = Tail05_1.addOrReplaceChild(
                "Tail06_1",
                CubeListBuilder.create().texOffs(85, 2).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, 0.0873f, 0.2618f, 0f)
            )

            val Tail07_1 = Tail06_1.addOrReplaceChild(
                "Tail07_1",
                CubeListBuilder.create().texOffs(86, 0).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, -0.3491f, 0.3491f, 0f)
            )

            val Tail08_1 = Tail07_1.addOrReplaceChild(
                "Tail08_1",
                CubeListBuilder.create().texOffs(83, 0).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, -0.5236f, 0.3491f, 0f)
            )

            val Tail09_1 = Tail08_1.addOrReplaceChild(
                "Tail09_1",
                CubeListBuilder.create().texOffs(96, 0).addBox(-4f, -3.5f, 0f, 8f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, -0.0873f, 0.4363f, 0f)
            )

            val TailHead01_1 = Tail09_1.addOrReplaceChild(
                "TailHead01_1",
                CubeListBuilder.create().texOffs(40, 0).addBox(-4.5f, 0f, 0f, 9f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1.8f, 3.5f, -0.1745f, 0f, 0f)
            )

            val TailC02_1 = TailHead01_1.addOrReplaceChild(
                "TailC02_1",
                CubeListBuilder.create().texOffs(100, 8).addBox(-1f, -1f, 0f, 2f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offset(-2f, 4.5f, 9.5f)
            )

            val TailC01_1 = TailHead01_1.addOrReplaceChild(
                "TailC01_1",
                CubeListBuilder.create().texOffs(100, 8).addBox(-1f, -1f, 0f, 2f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offset(2f, 4.5f, 9.5f)
            )

            val TailJaw01_1 = Tail09_1.addOrReplaceChild(
                "TailJaw01_1",
                CubeListBuilder.create().texOffs(90, 18).addBox(-4.5f, -4f, 0f, 9f, 4f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.7f, 2.7f, 0.1745f, 0f, 0f)
            )

            val Head = BodyMain.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 65).addBox(-7f, -11f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -13f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(0, 56).addBox(-7.5f, 0f, 0f, 15f, 12f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.5f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(0, 40).addBox(-7.5f, 0f, 0f, 15f, 7f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1.6f, 0.3491f, 0f, 0f)
            )

            val Hair03a = HairMain.addOrReplaceChild(
                "Hair03a",
                CubeListBuilder.create().texOffs(90, 32).addBox(-1.5f, 0f, -3f, 3f, 12f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.4f, 9.8f, 5.5f, -0.2094f, -0.1396f, 0.0698f)
            )

            val Hair02b = HairMain.addOrReplaceChild(
                "Hair02b",
                CubeListBuilder.create().texOffs(81, 116).addBox(-1.5f, 0f, -3.3f, 3f, 7f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.9f, 4.7f, 0f, 0f, 0f, 0.0873f)
            )

            val Hair02a = HairMain.addOrReplaceChild(
                "Hair02a",
                CubeListBuilder.create().texOffs(81, 116).addBox(-1.5f, 0f, -3.3f, 3f, 7f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.9f, 4.7f, 0f, 0f, 0f, -0.0873f)
            )

            val Hair03b = HairMain.addOrReplaceChild(
                "Hair03b",
                CubeListBuilder.create().texOffs(90, 32).addBox(-1.5f, 0f, -3f, 3f, 12f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.4f, 9.8f, 5.5f, -0.2094f, 0.1396f, -0.0698f)
            )

            val Ear01 = Head.addOrReplaceChild(
                "Ear01",
                CubeListBuilder.create().texOffs(0, 26).addBox(-2f, 0f, -7f, 4f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.2f, -11f, 6.8f, -0.8378f, -0.1222f, 0.1745f)
            )

            val Horn02 = Head.addOrReplaceChild(
                "Horn02",
                CubeListBuilder.create().texOffs(40, 39).addBox(-1.5f, -1.5f, -6f, 3f, 3f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.3f, -7.5f, -6f, -0.8727f, -0.4363f, 0.2618f)
            )

            val Horn03 = Horn02.addOrReplaceChild(
                "Horn03",
                CubeListBuilder.create().texOffs(40, 39).addBox(-3f, -3f, -6f, 3f, 3f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.5f, 1.5f, -6f, -0.6981f, 0f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 40).addBox(-8f, -8f, -7.2f, 16f, 17f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -4f, 0f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(104, 29).addBox(0f, 0f, -12f, 0f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -4f, -7.6f, -0.2618f, 1.4835f, -0.2618f)
            )

            val HatBase = Head.addOrReplaceChild(
                "HatBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -3.1f, 5.8f)
            )

            val HatL = HatBase.addOrReplaceChild(
                "HatL",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -14f, -1f, 10f, 16f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.3f, 2.1f, -2.9f, 0.5236f, 0.0873f, 0.0698f)
            )

            val HatEyeL = HatL.addOrReplaceChild(
                "HatEyeL",
                CubeListBuilder.create().texOffs(22, 28).addBox(0f, -3f, -3f, 1f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(9.6f, -6f, 5.3f, 0.0873f, -0.0524f, -0.0524f)
            )

            val HatR = HatBase.addOrReplaceChild(
                "HatR",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-10f, -14f, -1f, 10f, 16f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.3f, 2.1f, -2.9f, 0.5236f, -0.0873f, -0.0698f)
            )

            val HatEyeR = HatR.addOrReplaceChild(
                "HatEyeR",
                CubeListBuilder.create().texOffs(22, 28).mirror()
                    .addBox(-1f, -3f, -3f, 1f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9.6f, -6f, 5.3f, 0.0873f, 0.0524f, 0.0524f)
            )

            val Ear02 = Head.addOrReplaceChild(
                "Ear02",
                CubeListBuilder.create().texOffs(0, 26).mirror().addBox(-2f, 0f, -7f, 4f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.2f, -11f, 6.8f, -0.8378f, 0.1222f, -0.1745f)
            )

            val Horn01 = Head.addOrReplaceChild(
                "Horn01",
                CubeListBuilder.create().texOffs(40, 39).addBox(-1.5f, -1.5f, -6f, 3f, 3f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3f, -7.5f, -6f, -0.8727f, 0.4363f, -0.5236f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(0, 92).addBox(-2.5f, 0f, -2.5f, 5f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, 3f, -6f, -0.1396f, 0f, 0.2094f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(0, 105).addBox(-5f, 0f, -5f, 5f, 7f, 5f, CubeDeformation(0f)),
                PartPose.offset(2.5f, 8f, 2.5f)
            )

            val Band01 = BodyMain.addOrReplaceChild(
                "Band01",
                CubeListBuilder.create().texOffs(40, 39).addBox(-0.5f, 0f, 0f, 1f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.5f, 1.7f, -12f, -0.1745f, 0f, 0f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 0f)
            )

            val GlowHead = GlowBodyMain.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -6f, -13f)
            )

            addFaceLayerCAHime(GlowHead)

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
