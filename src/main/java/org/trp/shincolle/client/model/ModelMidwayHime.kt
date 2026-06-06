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
import org.trp.shincolle.entity.EntityMidwayHime
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import kotlin.math.cos

class ModelMidwayHime<T : EntityShipBase>(root: ModelPart) : ShipModelHumanoidBase<T>(), IGlowableModel {
    private var isDeadPose = false
    private var isSittingPose = false
    override var poseTranslateY = 0f

    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val Butt: ModelPart
    private val ArmRight01: ModelPart
    private val ArmLeft01: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val EquipSR01: ModelPart
    private val EquipSR01b: ModelPart
    private val EquipSR01c: ModelPart
    private val Head: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val HeadHL: ModelPart
    private val HeadHR: ModelPart
    private val HairU01: ModelPart
    private val Ahoke: ModelPart
    private val HairR01: ModelPart
    private val HairL01: ModelPart
    private val HairR02: ModelPart
    private val HairL02: ModelPart
    private val Hair01: ModelPart
    private val Hair02: ModelPart
    private val Hair03: ModelPart
    private val HeadHL2: ModelPart
    private val HeadHL3: ModelPart
    private val HeadHR2: ModelPart
    private val HeadHR3: ModelPart
    private val LegLeft01: ModelPart
    private val Skirt01: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft02: ModelPart
    private val Skirt02: ModelPart
    private val Skirt03: ModelPart
    private val LegRight02: ModelPart
    private val ArmRight02: ModelPart
    private val ArmRight02a: ModelPart
    private val ArmRight02b: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmLeft02a: ModelPart
    private val ArmLeft02b: ModelPart
    private val Collar02: ModelPart
    private val Collar03a1: ModelPart
    private val Collar03a2: ModelPart
    private val Collar03a3: ModelPart
    private val Collar03a3_1: ModelPart
    private val Collar03a4: ModelPart
    private val Collar03a5: ModelPart
    private val Collar03a6: ModelPart
    private val Collar03a7: ModelPart
    private val Collar03a8: ModelPart
    private val Collar03a9: ModelPart
    private val Collar03a10: ModelPart
    private val Collar03a11: ModelPart
    private val Collar03a12: ModelPart
    private val Collar03a13: ModelPart
    private val Collar03a14: ModelPart
    private val Collar03a15: ModelPart
    private val Collar03b1: ModelPart
    private val Collar03b2: ModelPart
    private val Collar03b3: ModelPart
    private val Collar03b3_1: ModelPart
    private val Collar03b4: ModelPart
    private val Collar03b5: ModelPart
    private val Collar03b6: ModelPart
    private val Collar03b7: ModelPart
    private val Collar03b8: ModelPart
    private val Collar03b9: ModelPart
    private val Collar03b10: ModelPart
    private val Collar03b11: ModelPart
    private val Collar03b12: ModelPart
    private val Collar03b13: ModelPart
    private val Collar03b14: ModelPart
    private val Collar03b15: ModelPart
    private val EquipSR02: ModelPart
    private val EquipSR03: ModelPart
    private val EquipSR04: ModelPart
    private val EquipSR05: ModelPart
    private val EquipSR02b: ModelPart
    private val EquipSR03b: ModelPart
    private val EquipSR04b: ModelPart
    private val EquipSR02c: ModelPart
    private val EquipSR03c: ModelPart
    private val EquipSR04c: ModelPart
    private val EquipSR05c: ModelPart
    private val Collar01: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowBodyMain2: ModelPart
    private val GlowBodyMain2a: ModelPart
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val buttDefaultY: Float
    private val buttDefaultZ: Float
    private val skirt01DefaultY: Float
    private val skirt01DefaultZ: Float
    private val skirt02DefaultY: Float
    private val skirt03DefaultY: Float
    private val armLeft02DefaultX: Float
    private val armLeft02DefaultZ: Float
    private val armRight02DefaultX: Float
    private val armRight02DefaultZ: Float
    private val legLeft01DefaultY: Float
    private val legLeft01DefaultZ: Float
    private val legLeft02DefaultX: Float
    private val legLeft02DefaultY: Float
    private val legLeft02DefaultZ: Float
    private val legRight01DefaultY: Float
    private val legRight01DefaultZ: Float
    private val legRight02DefaultX: Float
    private val legRight02DefaultY: Float
    private val legRight02DefaultZ: Float
    private val equipSR01DefaultZ: Float
    private val equipSR02DefaultZ: Float
    private val equipSR03DefaultZ: Float
    private val equipSR04DefaultZ: Float
    private val equipSR05DefaultZ: Float
    private val equipSR01bDefaultZ: Float
    private val equipSR02bDefaultZ: Float
    private val equipSR03bDefaultZ: Float
    private val equipSR04bDefaultZ: Float
    private val equipSR01cDefaultZ: Float
    private val equipSR02cDefaultZ: Float
    private val equipSR03cDefaultZ: Float
    private val equipSR04cDefaultZ: Float
    private val equipSR05cDefaultZ: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.ArmRight02a = this.ArmRight02.getChild("ArmRight02a")
        this.ArmRight02b = this.ArmRight02a.getChild("ArmRight02b")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.ArmLeft02a = this.ArmLeft02.getChild("ArmLeft02a")
        this.ArmLeft02b = this.ArmLeft02a.getChild("ArmLeft02b")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.Hair03 = this.Hair02.getChild("Hair03")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.Skirt02 = this.Skirt01.getChild("Skirt02")
        this.Skirt03 = this.Skirt02.getChild("Skirt03")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.Collar01 = this.GlowNeck.getChild("Collar01")
        this.Collar02 = this.Collar01.getChild("Collar02")
        this.Collar03a2 = this.Collar02.getChild("Collar03a2")
        this.Collar03b2 = this.Collar03a2.getChild("Collar03b2")
        this.Collar03a8 = this.Collar02.getChild("Collar03a8")
        this.Collar03b8 = this.Collar03a8.getChild("Collar03b8")
        this.Collar03a7 = this.Collar02.getChild("Collar03a7")
        this.Collar03b7 = this.Collar03a7.getChild("Collar03b7")
        this.Collar03a15 = this.Collar02.getChild("Collar03a15")
        this.Collar03b15 = this.Collar03a15.getChild("Collar03b15")
        this.Collar03a9 = this.Collar02.getChild("Collar03a9")
        this.Collar03b9 = this.Collar03a9.getChild("Collar03b9")
        this.Collar03a10 = this.Collar02.getChild("Collar03a10")
        this.Collar03b10 = this.Collar03a10.getChild("Collar03b10")
        this.Collar03a12 = this.Collar02.getChild("Collar03a12")
        this.Collar03b12 = this.Collar03a12.getChild("Collar03b12")
        this.Collar03a11 = this.Collar02.getChild("Collar03a11")
        this.Collar03b11 = this.Collar03a11.getChild("Collar03b11")
        this.Collar03a13 = this.Collar02.getChild("Collar03a13")
        this.Collar03b13 = this.Collar03a13.getChild("Collar03b13")
        this.Collar03a1 = this.Collar02.getChild("Collar03a1")
        this.Collar03b1 = this.Collar03a1.getChild("Collar03b1")
        this.Collar03a4 = this.Collar02.getChild("Collar03a4")
        this.Collar03b4 = this.Collar03a4.getChild("Collar03b4")
        this.Collar03a3 = this.Collar02.getChild("Collar03a3")
        this.Collar03b3 = this.Collar03a3.getChild("Collar03b3")
        this.Collar03a14 = this.Collar02.getChild("Collar03a14")
        this.Collar03b14 = this.Collar03a14.getChild("Collar03b14")
        this.Collar03a3_1 = this.Collar02.getChild("Collar03a3_1")
        this.Collar03b3_1 = this.Collar03a3_1.getChild("Collar03b3_1")
        this.Collar03a6 = this.Collar02.getChild("Collar03a6")
        this.Collar03b6 = this.Collar03a6.getChild("Collar03b6")
        this.Collar03a5 = this.Collar02.getChild("Collar03a5")
        this.Collar03b5 = this.Collar03a5.getChild("Collar03b5")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        this.HeadHL = this.GlowHead.getChild("HeadHL")
        this.HeadHL2 = this.HeadHL.getChild("HeadHL2")
        this.HeadHL3 = this.HeadHL2.getChild("HeadHL3")
        this.HeadHR = this.GlowHead.getChild("HeadHR")
        this.HeadHR2 = this.HeadHR.getChild("HeadHR2")
        this.HeadHR3 = this.HeadHR2.getChild("HeadHR3")
        this.initFaceParts(this.GlowHead)
        this.GlowBodyMain2 = root.getChild("GlowBodyMain2")
        this.GlowBodyMain2a = this.GlowBodyMain2.getChild("GlowBodyMain2a")
        this.EquipSR01 = this.GlowBodyMain2a.getChild("EquipSR01")
        this.EquipSR02 = this.EquipSR01.getChild("EquipSR02")
        this.EquipSR03 = this.EquipSR02.getChild("EquipSR03")
        this.EquipSR04 = this.EquipSR03.getChild("EquipSR04")
        this.EquipSR05 = this.EquipSR04.getChild("EquipSR05")
        this.EquipSR01b = this.GlowBodyMain2a.getChild("EquipSR01b")
        this.EquipSR02b = this.EquipSR01b.getChild("EquipSR02b")
        this.EquipSR03b = this.EquipSR02b.getChild("EquipSR03b")
        this.EquipSR04b = this.EquipSR03b.getChild("EquipSR04b")
        this.EquipSR01c = this.GlowBodyMain2a.getChild("EquipSR01c")
        this.EquipSR02c = this.EquipSR01c.getChild("EquipSR02c")
        this.EquipSR03c = this.EquipSR02c.getChild("EquipSR03c")
        this.EquipSR04c = this.EquipSR03c.getChild("EquipSR04c")
        this.EquipSR05c = this.EquipSR04c.getChild("EquipSR05c")
        this.buttDefaultY = this.Butt.y
        this.buttDefaultZ = this.Butt.z
        this.skirt01DefaultY = this.Skirt01.y
        this.skirt01DefaultZ = this.Skirt01.z
        this.skirt02DefaultY = this.Skirt02.y
        this.skirt03DefaultY = this.Skirt03.y
        this.armLeft02DefaultX = this.ArmLeft02.x
        this.armLeft02DefaultZ = this.ArmLeft02.z
        this.armRight02DefaultX = this.ArmRight02.x
        this.armRight02DefaultZ = this.ArmRight02.z
        this.legLeft01DefaultY = this.LegLeft01.y
        this.legLeft01DefaultZ = this.LegLeft01.z
        this.legLeft02DefaultX = this.LegLeft02.x
        this.legLeft02DefaultY = this.LegLeft02.y
        this.legLeft02DefaultZ = this.LegLeft02.z
        this.legRight01DefaultY = this.LegRight01.y
        this.legRight01DefaultZ = this.LegRight01.z
        this.legRight02DefaultX = this.LegRight02.x
        this.legRight02DefaultY = this.LegRight02.y
        this.legRight02DefaultZ = this.LegRight02.z
        this.equipSR01DefaultZ = this.EquipSR01.z
        this.equipSR02DefaultZ = this.EquipSR02.z
        this.equipSR03DefaultZ = this.EquipSR03.z
        this.equipSR04DefaultZ = this.EquipSR04.z
        this.equipSR05DefaultZ = this.EquipSR05.z
        this.equipSR01bDefaultZ = this.EquipSR01b.z
        this.equipSR02bDefaultZ = this.EquipSR02b.z
        this.equipSR03bDefaultZ = this.EquipSR03b.z
        this.equipSR04bDefaultZ = this.EquipSR04b.z
        this.equipSR01cDefaultZ = this.EquipSR01c.z
        this.equipSR02cDefaultZ = this.EquipSR02c.z
        this.equipSR03cDefaultZ = this.EquipSR03c.z
        this.equipSR04cDefaultZ = this.EquipSR04c.z
        this.equipSR05cDefaultZ = this.EquipSR05c.z
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

        this.Butt.y = this.buttDefaultY
        this.Butt.z = this.buttDefaultZ
        this.Skirt01.y = this.skirt01DefaultY
        this.Skirt01.z = this.skirt01DefaultZ
        this.Skirt02.y = this.skirt02DefaultY
        this.Skirt03.y = this.skirt03DefaultY
        this.ArmLeft02.x = this.armLeft02DefaultX
        this.ArmLeft02.z = this.armLeft02DefaultZ
        this.ArmRight02.x = this.armRight02DefaultX
        this.ArmRight02.z = this.armRight02DefaultZ
        this.LegLeft01.y = this.legLeft01DefaultY
        this.LegLeft01.z = this.legLeft01DefaultZ
        this.LegLeft02.x = this.legLeft02DefaultX
        this.LegLeft02.y = this.legLeft02DefaultY
        this.LegLeft02.z = this.legLeft02DefaultZ
        this.LegRight01.y = this.legRight01DefaultY
        this.LegRight01.z = this.legRight01DefaultZ
        this.LegRight02.x = this.legRight02DefaultX
        this.LegRight02.y = this.legRight02DefaultY
        this.LegRight02.z = this.legRight02DefaultZ
    }

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) {
            return
        }

        val showRigging = entity.getEquipFlag(EntityMidwayHime.EQUIP_RIGGING)
                && entity.attackTick > 0
        val showCollar = entity.getEquipFlag(EntityMidwayHime.EQUIP_COLLAR)

        if (showRigging) {
            applyRoadAppearance(entity.attackTick)
        } else {
            hideRoadSegments()
        }
        this.GlowBodyMain2.visible = showRigging
        this.Collar01.visible = showCollar
    }

    private fun applyRoadAppearance(attackTick: Int) {
        hideRoadSegments()

        when (attackTick) {
            26, 50 -> {
                setRoadStageVisible(1)
                setRoadStageOffsetZ(0.7f, 0f, 0f, 0f, 0f)
            }

            27, 49 -> {
                setRoadStageVisible(2)
                setRoadStageOffsetZ(0.45f, 0.25f, 0f, 0f, 0f)
            }

            28, 48 -> {
                setRoadStageVisible(3)
                setRoadStageOffsetZ(0.25f, 0.2f, 0.25f, 0f, 0f)
            }

            29, 47 -> {
                setRoadStageVisible(4)
                setRoadStageOffsetZ(0.1f, 0.15f, 0.2f, 0.25f, 0f)
            }

            30, 46 -> {
                setRoadStageVisible(5)
                setRoadStageOffsetZ(0f, 0.1f, 0.15f, 0.2f, 0.25f)
            }

            31, 45 -> {
                setRoadStageVisible(5)
                setRoadStageOffsetZ(0f, 0f, 0.1f, 0.15f, 0.2f)
            }

            32, 44 -> {
                setRoadStageVisible(5)
                setRoadStageOffsetZ(0f, 0f, 0f, 0.1f, 0.15f)
            }

            33, 43 -> {
                setRoadStageVisible(5)
                setRoadStageOffsetZ(0f, 0f, 0f, 0f, 0.1f)
            }

            else -> if (attackTick > 29 && attackTick < 47) {
                setRoadStageVisible(5)
                setRoadStageOffsetZ(0f, 0f, 0f, 0f, 0f)
            }
        }
    }

    private fun hideRoadSegments() {
        setRoadStageVisible(0)
        setRoadStageOffsetZ(0f, 0f, 0f, 0f, 0f)
    }

    private fun setRoadStageVisible(stage: Int) {
        val stage1 = stage >= 1
        val stage2 = stage >= 2
        val stage3 = stage >= 3
        val stage4 = stage >= 4
        val stage5 = stage >= 5

        this.EquipSR01.visible = stage1
        this.EquipSR01b.visible = stage1
        this.EquipSR01c.visible = stage1
        this.EquipSR02.visible = stage2
        this.EquipSR02b.visible = stage2
        this.EquipSR02c.visible = stage2
        this.EquipSR03.visible = stage3
        this.EquipSR03b.visible = stage3
        this.EquipSR03c.visible = stage3
        this.EquipSR04.visible = stage4
        this.EquipSR04b.visible = stage4
        this.EquipSR04c.visible = stage4
        this.EquipSR05.visible = stage5
        this.EquipSR05c.visible = stage5
    }

    private fun setRoadStageOffsetZ(offset1: Float, offset2: Float, offset3: Float, offset4: Float, offset5: Float) {
        this.EquipSR01.z = this.equipSR01DefaultZ + (offset1 * OFFSET_SCALE)
        this.EquipSR01b.z = this.equipSR01bDefaultZ + (offset1 * OFFSET_SCALE)
        this.EquipSR01c.z = this.equipSR01cDefaultZ + (offset1 * OFFSET_SCALE)

        this.EquipSR02.z = this.equipSR02DefaultZ + (offset2 * OFFSET_SCALE)
        this.EquipSR02b.z = this.equipSR02bDefaultZ + (offset2 * OFFSET_SCALE)
        this.EquipSR02c.z = this.equipSR02cDefaultZ + (offset2 * OFFSET_SCALE)

        this.EquipSR03.z = this.equipSR03DefaultZ + (offset3 * OFFSET_SCALE)
        this.EquipSR03b.z = this.equipSR03bDefaultZ + (offset3 * OFFSET_SCALE)
        this.EquipSR03c.z = this.equipSR03cDefaultZ + (offset3 * OFFSET_SCALE)

        this.EquipSR04.z = this.equipSR04DefaultZ + (offset4 * OFFSET_SCALE)
        this.EquipSR04b.z = this.equipSR04bDefaultZ + (offset4 * OFFSET_SCALE)
        this.EquipSR04c.z = this.equipSR04cDefaultZ + (offset4 * OFFSET_SCALE)

        this.EquipSR05.z = this.equipSR05DefaultZ + (offset5 * OFFSET_SCALE)
        this.EquipSR05c.z = this.equipSR05cDefaultZ + (offset5 * OFFSET_SCALE)
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        this.Head.xRot = -0.2618f
        this.Head.yRot = 0.0f
        this.Head.zRot = 0.0f
        this.Ahoke.yRot = -1.0f
        this.BodyMain.xRot = 1.2217f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 1.2217f
        this.Butt.xRot = -0.05f
        this.Skirt01.xRot = -0.34f
        this.Skirt02.xRot = -0.27f
        this.Skirt03.xRot = -0.22f
        this.Collar01.xRot = 0.035f
        this.Hair01.xRot = 0.2f
        this.Hair01.zRot = -0.2f
        this.Hair02.xRot = 0.2f
        this.Hair02.zRot = -0.15f
        this.HairL01.zRot = 0.0873f
        this.HairL02.zRot = -0.3142f
        this.HairR01.zRot = -0.0873f
        this.HairR02.zRot = -1.2217f
        this.HairL01.xRot = -0.28f
        this.HairL02.xRot = 0.15f
        this.HairR01.xRot = -0.35f
        this.HairR02.xRot = 0.18f
        this.BoobL.xRot = -1.0f
        this.BoobL.zRot = -0.12f
        this.BoobR.xRot = -0.7f
        this.BoobR.zRot = -0.12f

        this.ArmLeft01.xRot = -0.35f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = -3.0f
        this.ArmLeft02.xRot = 0.0f
        this.ArmLeft02.zRot = 0.0f

        this.ArmRight01.xRot = -0.5f
        this.ArmRight01.yRot = 0.3f
        this.ArmRight01.zRot = -0.5f
        this.ArmRight02.xRot = 0.0f
        this.ArmRight02.zRot = -0.8727f

        this.LegLeft01.xRot = -0.14f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.09f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.yRot = 0.0f
        this.LegLeft02.zRot = 0.0f

        this.LegRight01.xRot = -1.2217f
        this.LegRight01.yRot = -0.5236f
        this.LegRight01.zRot = 0.0f
        this.LegRight02.xRot = 1.0472f
        this.LegRight02.yRot = 0.0f
        this.LegRight02.zRot = 0.0f
    }

    private fun applyBasePose(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val angleX = cos((ageInTicks * 0.08f).toDouble()).toFloat()
        val angleX1 = cos((ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f).toDouble()).toFloat()
        val angleX2 = cos((ageInTicks * 0.08f + 0.6f + limbSwing * 0.5f).toDouble()).toFloat()
        val angleAdd1 = cos((limbSwing * 0.7f).toDouble()).toFloat() * limbSwingAmount * 0.5f
        val angleAdd2 = cos(limbSwing * 0.7f + Math.PI).toFloat() * limbSwingAmount * 0.5f

        if (entity!!.isInWater()) {
            this.poseTranslateY += angleX * 0.025f + 0.025f
        }

        val addk1 = angleAdd1 * 0.6f - 0.27f
        val addk2 = angleAdd2 * 0.6f - 0.19f

        this.Head.xRot = headPitch * (Math.PI.toFloat() / 180f)
        this.Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)
        this.Head.zRot = 0.0f
        val headX = this.Head.xRot * -0.5f

        this.Ahoke.yRot = angleX * 0.15f + 0.6f
        this.BodyMain.xRot = -0.1047f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = 0.35f
        this.BoobL.xRot = angleX * 0.08f - 0.76f
        this.BoobL.zRot = 0.08f
        this.BoobR.xRot = angleX * 0.08f - 0.76f
        this.BoobR.zRot = -0.08f
        this.Collar01.xRot = 0.035f + this.Head.xRot * 0.8f

        this.Skirt01.xRot = -0.087f
        this.Skirt02.xRot = angleX1 * 0.015f - 0.087f
        this.Skirt03.xRot = -angleX2 * 0.04f - 0.052f

        this.Hair01.xRot = angleX * 0.03f + 0.26f + headX
        this.Hair01.zRot = 0.0f
        this.Hair02.xRot = -angleX1 * 0.04f - 0.087f + headX
        this.Hair02.zRot = 0.0f
        this.Hair03.xRot = -angleX2 * 0.07f - 0.052f
        this.Hair03.zRot = 0.0f

        this.ArmLeft01.xRot = -0.26f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = 0.28f
        this.ArmLeft02.xRot = 0.0f
        this.ArmLeft02.yRot = 0.0f
        this.ArmLeft02.zRot = 0.0f

        this.ArmRight01.xRot = -0.26f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = -0.28f
        this.ArmRight02.xRot = 0.0f
        this.ArmRight02.yRot = 0.0f
        this.ArmRight02.zRot = 0.0f

        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.087f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.yRot = 0.0f
        this.LegLeft02.zRot = 0.0f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.087f
        this.LegRight02.xRot = 0.0f
        this.LegRight02.yRot = 0.0f
        this.LegRight02.zRot = 0.0f

        this.LegLeft01.xRot = addk1
        this.LegRight01.xRot = addk2

        val headZ = this.Head.zRot * -0.5f
        this.HairL01.xRot = angleX * 0.02f + headX - 0.19f
        this.HairL02.xRot = -angleX1 * 0.04f + headX + 0.17f
        this.HairR01.xRot = angleX * 0.02f + headX - 0.19f
        this.HairR02.xRot = -angleX1 * 0.04f + headX + 0.17f
        this.Hair01.zRot = headZ
        this.Hair02.zRot = headZ
        this.Hair03.zRot = headZ
        this.HairL01.zRot = headZ - 0.087f
        this.HairL02.zRot = headZ + 0.087f
        this.HairR01.zRot = headZ + 0.087f
        this.HairR02.zRot = headZ - 0.052f
    }

    private fun applySpecialPoseAdjustments(entity: T?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        val angleAdd1 = cos((limbSwing * 0.7f).toDouble()).toFloat() * limbSwingAmount * 0.5f
        val angleAdd2 = cos(limbSwing * 0.7f + Math.PI).toFloat() * limbSwingAmount * 0.5f
        val headX = this.Head.xRot * -0.5f

        val isSprinting = entity!!.isSprinting || limbSwingAmount > 0.9f
        val isCrouching = entity.isCrouching()
        val isPassenger = entity.isPassenger()
        val isSitting = entity.isInSittingPose || (isPassenger && entity.getVehicle() !is EntityMountBase)

        if (isSprinting) {
            this.Hair01.xRot = angleAdd1 * 0.1f + limbSwingAmount * 0.4f + headX
            this.Hair03.xRot += 0.1f
            this.BoobL.xRot = angleAdd2 * 0.1f - 0.83f
            this.BoobL.zRot = -0.07f
            this.BoobR.xRot = angleAdd1 * 0.1f - 0.83f
            this.BoobR.zRot = 0.07f
            this.ArmLeft01.xRot = angleAdd2 * 0.8f + 0.1745f
            this.ArmLeft01.yRot = 0.0f
            this.ArmLeft01.zRot = -0.35f
            this.ArmRight01.xRot = angleAdd1 * 0.8f + 0.1745f
            this.ArmRight01.yRot = 0.0f
            this.ArmRight01.zRot = 0.35f
        }

        if (isCrouching) {
            this.poseTranslateY = SNEAK_TRANSLATE_Y
            this.Head.xRot -= 0.6283f
            this.BodyMain.xRot = 0.8727f
            this.Skirt01.xRot = -0.34f
            this.Skirt01.y = this.skirt01DefaultY + (-0.2f * OFFSET_SCALE)
            this.Skirt01.z = this.skirt01DefaultZ + (0.03f * OFFSET_SCALE)
            this.Skirt02.xRot = -0.27f
            this.Skirt03.xRot = -0.22f
            this.Collar01.xRot -= 0.35f
            this.BoobL.xRot -= 0.2f
            this.BoobL.zRot = -0.04f
            this.BoobR.xRot -= 0.2f
            this.BoobR.zRot = 0.04f
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
            this.Hair03.xRot -= 0.1f
        }

        if (isPassenger && entity.getVehicle() is EntityMountBase) {
            this.isSittingPose = true
            if (isSitting) {
                if (hasLegacyState(entity, 1, 4)) {
                    this.poseTranslateY = 1.43f
                    this.Head.xRot -= 0.1f
                    this.BodyMain.xRot = 0.0f
                    this.Butt.xRot = -0.2f
                    this.BoobL.xRot -= 0.1f
                    this.BoobL.zRot = 0.16f
                    this.BoobR.xRot -= 0.1f
                    this.BoobR.zRot = -0.16f
                    this.Skirt01.xRot = -0.05f
                    this.Skirt01.y = this.skirt01DefaultY + (-0.1f * OFFSET_SCALE)
                    this.Skirt02.xRot = -0.15f
                    this.Skirt02.y = this.skirt02DefaultY + (-0.1f * OFFSET_SCALE)
                    this.Skirt03.xRot = -0.1f
                    this.Skirt03.y = this.skirt03DefaultY + (-0.1f * OFFSET_SCALE)
                    this.ArmLeft01.xRot = -0.6f
                    this.ArmLeft01.zRot = 0.1f
                    this.ArmLeft02.zRot = 0.39f
                    this.ArmRight01.xRot = -0.6f
                    this.ArmRight01.zRot = -0.1f
                    this.ArmRight02.zRot = -0.39f
                    this.LegLeft01.xRot = -0.9f
                    this.LegRight01.xRot = -0.9f
                    this.LegLeft01.yRot = 0.19f
                    this.LegLeft01.zRot = 0.0f
                    this.LegLeft02.xRot = 2.67f
                    this.LegLeft02.zRot = 0.0175f
                    this.LegLeft02.z = this.legLeft02DefaultZ + (0.375f * OFFSET_SCALE)
                    this.LegRight01.yRot = -0.19f
                    this.LegRight01.zRot = 0.0f
                    this.LegRight02.xRot = 2.67f
                    this.LegRight02.zRot = -0.0175f
                    this.LegRight02.z = this.legRight02DefaultZ + (0.375f * OFFSET_SCALE)
                } else {
                    this.poseTranslateY = 1.43f
                    this.Head.xRot -= 0.1f
                    this.BodyMain.xRot = 0.0f
                    this.Butt.xRot = -0.2f
                    this.BoobL.xRot -= 0.1f
                    this.BoobL.zRot = 0.16f
                    this.BoobR.xRot -= 0.1f
                    this.BoobR.zRot = -0.16f
                    this.Skirt01.xRot = -0.05f
                    this.Skirt01.y = this.skirt01DefaultY + (-0.1f * OFFSET_SCALE)
                    this.Skirt02.xRot = -0.15f
                    this.Skirt02.y = this.skirt02DefaultY + (-0.1f * OFFSET_SCALE)
                    this.Skirt03.xRot = -0.1f
                    this.Skirt03.y = this.skirt03DefaultY + (-0.1f * OFFSET_SCALE)
                    this.ArmLeft01.xRot = -0.46f
                    this.ArmLeft01.zRot = 0.35f
                    this.ArmRight01.xRot = -0.46f
                    this.ArmRight01.zRot = -0.35f
                    this.LegLeft01.xRot = -0.9f
                    this.LegRight01.xRot = -0.9f
                    this.LegLeft01.yRot = 0.19f
                    this.LegLeft01.zRot = 0.0f
                    this.LegLeft02.xRot = 2.67f
                    this.LegLeft02.zRot = 0.0175f
                    this.LegLeft02.z = this.legLeft02DefaultZ + (0.375f * OFFSET_SCALE)
                    this.LegRight01.yRot = -0.19f
                    this.LegRight01.zRot = 0.0f
                    this.LegRight02.xRot = 2.67f
                    this.LegRight02.zRot = -0.0175f
                    this.LegRight02.z = this.legRight02DefaultZ + (0.375f * OFFSET_SCALE)
                }
            } else {
                this.poseTranslateY = RIDING_TRANSLATE_Y
                this.Head.xRot -= 0.7f
                this.BodyMain.xRot = 0.35f
                this.Skirt01.xRot = -0.23f
                this.Skirt01.y = this.skirt01DefaultY + (-0.23f * OFFSET_SCALE)
                this.Skirt02.xRot = -0.2f
                this.Skirt02.y = this.skirt02DefaultY + (-0.17f * OFFSET_SCALE)
                this.Skirt03.xRot = -0.2f
                this.Skirt03.y = this.skirt03DefaultY + (-0.15f * OFFSET_SCALE)
                this.Collar01.xRot -= 0.35f
                this.ArmLeft01.xRot = -0.5236f
                this.ArmLeft01.yRot = 0.0f
                this.ArmLeft01.zRot = 0.3491f
                this.ArmRight01.xRot = -0.5236f
                this.ArmRight01.yRot = 0.0f
                this.ArmRight01.zRot = -0.3491f
                this.LegLeft01.xRot = -1.4486f
                this.LegRight01.xRot = -1.4486f
                this.LegLeft01.yRot = -0.5236f
                this.LegLeft01.zRot = -1.3963f
                this.LegLeft02.xRot = 2.1817f
                this.LegLeft02.z = this.legLeft02DefaultZ + (0.37f * OFFSET_SCALE)
                this.LegRight01.yRot = 0.5236f
                this.LegRight01.zRot = 1.3963f
                this.LegRight02.xRot = 2.1817f
                this.LegRight02.z = this.legRight02DefaultZ + (0.37f * OFFSET_SCALE)
                if (isSprinting) {
                    this.Hair01.xRot += 0.5f
                    this.Hair02.xRot += 0.4f
                    this.Hair03.xRot += 0.2f
                } else {
                    this.Hair01.xRot += 0.2f
                    this.Hair02.xRot += 0.4f
                    this.Hair03.xRot += 0.2f
                }
            }
        } else if (isSitting) {
            this.isSittingPose = true
            if (hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY = 0.43f * 3f
                this.Head.xRot -= 0.1f
                this.BodyMain.xRot = 0.0f
                this.Butt.xRot = -0.2f
                this.BoobL.xRot -= 0.1f
                this.BoobL.zRot = 0.16f
                this.BoobR.xRot -= 0.1f
                this.BoobR.zRot = -0.16f
                this.Skirt01.xRot = -0.05f
                this.Skirt01.y = this.skirt01DefaultY + (-0.1f * OFFSET_SCALE)
                this.Skirt02.xRot = -0.15f
                this.Skirt02.y = this.skirt02DefaultY + (-0.1f * OFFSET_SCALE)
                this.Skirt03.xRot = -0.1f
                this.Skirt03.y = this.skirt03DefaultY + (-0.1f * OFFSET_SCALE)
                this.ArmLeft01.xRot = -0.6f
                this.ArmLeft01.zRot = 0.1f
                this.ArmLeft02.zRot = 0.39f
                this.ArmRight01.xRot = -0.6f
                this.ArmRight01.zRot = -0.1f
                this.ArmRight02.zRot = -0.39f
                this.LegLeft01.xRot = -0.9f
                this.LegRight01.xRot = -0.9f
                this.LegLeft01.yRot = 0.19f
                this.LegLeft01.zRot = 0.0f
                this.LegLeft02.xRot = 2.67f
                this.LegLeft02.zRot = 0.0175f
                this.LegLeft02.z = this.legLeft02DefaultZ + (0.375f * OFFSET_SCALE)
                this.LegRight01.yRot = -0.19f
                this.LegRight01.zRot = 0.0f
                this.LegRight02.xRot = 2.67f
                this.LegRight02.zRot = -0.0175f
                this.LegRight02.z = this.legRight02DefaultZ + (0.375f * OFFSET_SCALE)
            } else {
                this.poseTranslateY = if (isPassenger) RIDING_TRANSLATE_Y else SIT_TRANSLATE_Y
                this.Head.xRot -= 0.7f
                this.BodyMain.xRot = 0.35f
                this.Skirt01.xRot = -0.23f
                this.Skirt01.y = this.skirt01DefaultY + (-0.23f * OFFSET_SCALE)
                this.Skirt02.xRot = -0.2f
                this.Skirt02.y = this.skirt02DefaultY + (-0.17f * OFFSET_SCALE)
                this.Skirt03.xRot = -0.2f
                this.Skirt03.y = this.skirt03DefaultY + (-0.15f * OFFSET_SCALE)
                this.Collar01.xRot -= 0.35f
                this.ArmLeft01.xRot = -0.5236f
                this.ArmLeft01.yRot = 0.0f
                this.ArmLeft01.zRot = 0.3491f
                this.ArmRight01.xRot = -0.5236f
                this.ArmRight01.yRot = 0.0f
                this.ArmRight01.zRot = -0.3491f
                this.LegLeft01.xRot = -1.4486f
                this.LegRight01.xRot = -1.4486f
                this.LegLeft01.yRot = -0.5236f
                this.LegLeft01.zRot = -1.3963f
                this.LegLeft02.xRot = 2.1817f
                this.LegLeft02.z = this.legLeft02DefaultZ + (0.37f * OFFSET_SCALE)
                this.LegRight01.yRot = 0.5236f
                this.LegRight01.zRot = 1.3963f
                this.LegRight02.xRot = 2.1817f
                this.LegRight02.z = this.legRight02DefaultZ + (0.37f * OFFSET_SCALE)
                if (isSprinting) {
                    this.Hair01.xRot += 0.5f
                    this.Hair02.xRot += 0.4f
                    this.Hair03.xRot += 0.2f
                } else {
                    this.Hair01.xRot += 0.2f
                    this.Hair02.xRot += 0.4f
                    this.Hair03.xRot += 0.2f
                }
            }
        }

        if (entity != null && entity.attackTick > 0) {
            if (entity.attackTick > 20) {
                this.ArmLeft01.xRot = -1.7f + this.Head.xRot * 0.75f
                this.ArmLeft01.yRot = -0.2f
                this.ArmLeft01.zRot = 0.0f
                this.ArmLeft02.xRot = 0.0f
                this.ArmLeft02.yRot = 0.0f
                this.ArmLeft02.zRot = 0.0f
            }
            this.GlowBodyMain2a.xRot = this.ArmLeft01.xRot * -0.3f
        }

        val partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
        val customAttackAnim = if (entity != null) entity.getCustomAttackAnim(partialTick) else 0.0f
        if (customAttackAnim > 0.0f) {
            val f7 = Mth.sin(customAttackAnim * customAttackAnim * Math.PI.toFloat())
            val f8 = Mth.sin(Mth.sqrt(customAttackAnim) * Math.PI.toFloat())
            this.ArmRight01.xRot = -0.3f
            this.ArmRight01.yRot = 0.0f
            this.ArmRight01.zRot = -0.1f
            this.ArmRight01.xRot += -f8 * 80.0f * (Math.PI.toFloat() / 180f)
            this.ArmRight01.yRot += -f7 * 20.0f * (Math.PI.toFloat() / 180f)
            this.ArmRight01.zRot += -f8 * 20.0f * (Math.PI.toFloat() / 180f)
            this.ArmRight02.xRot = 0.0f
            this.ArmRight02.zRot = 0.0f
        }
    }

    private fun syncGlowParts() {
        this.GlowBodyMain.copyFrom(this.BodyMain)
        this.GlowBodyMain2.copyFrom(this.BodyMain)
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
        this.GlowBodyMain2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "midway_hime"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelMidwayHime")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelMidwayHime")
        private val SIT_TRANSLATE_Y = sittingY("ModelMidwayHime")
        private val RIDING_TRANSLATE_Y = ridingY("ModelMidwayHime")

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
                CubeListBuilder.create().texOffs(24, 71).mirror()
                    .addBox(-3f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, -9.3f, -0.7f, -0.0873f, 0f, 0.2618f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(24, 54).mirror().addBox(0f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 11f, 2.5f)
            )

            val ArmRight02a = ArmRight02.addOrReplaceChild(
                "ArmRight02a",
                CubeListBuilder.create().texOffs(75, 47).mirror().addBox(-3f, 0f, -3f, 6f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.5f, 3f, -2.5f, 0.0524f, 0f, 0f)
            )

            val ArmRight02b = ArmRight02a.addOrReplaceChild(
                "ArmRight02b",
                CubeListBuilder.create().texOffs(78, 37).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 1.9f, 0.2f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(0, 35).mirror().addBox(-3.5f, 0f, 0f, 7f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.4f, -8.5f, -3.7f, -0.8727f, -0.0873f, -0.0698f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(24, 71).addBox(-2f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.3f, -0.7f, 0.3491f, 0f, -0.2618f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(24, 54).addBox(-5f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 11f, 2.5f)
            )

            val ArmLeft02a = ArmLeft02.addOrReplaceChild(
                "ArmLeft02a",
                CubeListBuilder.create().texOffs(75, 47).addBox(-3f, 0f, -3f, 6f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.5f, 3f, -2.5f, 0.0524f, 0f, 0f)
            )

            val ArmLeft02b = ArmLeft02a.addOrReplaceChild(
                "ArmLeft02b",
                CubeListBuilder.create().texOffs(78, 37).addBox(-3.5f, 0f, -3.5f, 7f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 1.9f, 0.2f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(0, 35).addBox(-3.5f, 0f, 0f, 7f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.5f, -8.5f, -3.8f, -0.8727f, 0.0873f, 0.0698f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(24, 80).addBox(-2.5f, -3f, -2.9f, 5f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10.3f, 0.5f, 0.1047f, 0f, 0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -0.8f, -0.7f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 77).addBox(-8f, -8f, -7.4f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0.1f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(0, 10).addBox(-1f, 0f, 0f, 2f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, 3f, -5.5f, -0.192f, -0.1745f, -0.0873f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(0, 10).addBox(-1f, 0f, 0f, 2f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10f, 0f, 0.1745f, 0f, 0.0873f)
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

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(0, 10).mirror().addBox(-1f, 0f, 0f, 2f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7f, 3f, -5.5f, -0.192f, 0.1745f, 0.0873f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(0, 10).mirror().addBox(-1f, 0f, 0f, 2f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 10f, 0f, 0.1745f, 0f, -0.0524f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(46, 104).addBox(-7.5f, 0f, 0f, 15f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.8f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(14, 0).addBox(-7.5f, 0f, 0f, 15f, 17f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1f, 0.2618f, 0f, 0f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(62, 0).addBox(-8f, 0f, -5f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 13.5f, 5.5f, -0.0873f, 0f, 0f)
            )

            val Hair03 = Hair02.addOrReplaceChild(
                "Hair03",
                CubeListBuilder.create().texOffs(26, 28).addBox(-8f, 0f, -4.5f, 16f, 15f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12.5f, -0.1f, -0.0524f, 0f, 0f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 88).addBox(-7.5f, 0f, -5.7f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.3f, 0.3491f, 0f, 0f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 68).mirror().addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.8f, 5.5f, -2.6f, -0.0873f, 0f, -0.0873f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 47).mirror().addBox(0f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3f, 14f, -3f)
            )

            val Skirt01 = Butt.addOrReplaceChild(
                "Skirt01",
                CubeListBuilder.create().texOffs(128, 0).addBox(-8.5f, 0f, -8.5f, 17f, 6f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3f, 1.5f, -0.0873f, 0f, 0f)
            )

            val Skirt02 = Skirt01.addOrReplaceChild(
                "Skirt02",
                CubeListBuilder.create().texOffs(128, 17).addBox(-10.5f, 0f, -6.5f, 21f, 6f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4.5f, -2.7f, -0.0873f, 0f, 0f)
            )

            val Skirt03 = Skirt02.addOrReplaceChild(
                "Skirt03",
                CubeListBuilder.create().texOffs(128, 37).addBox(-13f, 0f, -7.5f, 26f, 6f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4.5f, 0.3f, -0.0524f, 0f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 68).addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.8f, 5.5f, -2.6f, -0.2793f, 0f, 0.0873f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 47).addBox(-6f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(3f, 14f, -3f)
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

            val Collar01 = GlowNeck.addOrReplaceChild(
                "Collar01",
                CubeListBuilder.create().texOffs(66, 25).addBox(-6f, -2f, -4f, 12f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1.9f, -1.2f, 0.035f, 0f, 0f)
            )

            val Collar02 = Collar01.addOrReplaceChild(
                "Collar02",
                CubeListBuilder.create().texOffs(128, 60).addBox(-7f, -1.5f, -5.7f, 14f, 3f, 11f, CubeDeformation(0f)),
                PartPose.offset(0f, -2.5f, -1f)
            )

            val Collar03a2 = Collar02.addOrReplaceChild(
                "Collar03a2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, 0.6f, -3.5f)
            )

            val Collar03b2 = Collar03a2.addOrReplaceChild(
                "Collar03b2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -3f, 3f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.5f, -0.8727f, 0f, 0f)
            )

            val Collar03a8 = Collar02.addOrReplaceChild(
                "Collar03a8",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-1.5f, 0f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.5f, 0.2f, 3.4f, -1.7453f, -2.618f, 0.0524f)
            )

            val Collar03b8 = Collar03a8.addOrReplaceChild(
                "Collar03b8",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.5f, 0f, -3f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, 1.5f, -0.7679f, 0f, 0f)
            )

            val Collar03a7 = Collar02.addOrReplaceChild(
                "Collar03a7",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.4f, 0.1f, 2.7f, -1.7453f, -2.2689f, 0f)
            )

            val Collar03b7 = Collar03a7.addOrReplaceChild(
                "Collar03b7",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -3f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.5f, -0.8378f, 0f, 0f)
            )

            val Collar03a15 = Collar02.addOrReplaceChild(
                "Collar03a15",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-1.5f, 0f, -1.5f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5f, 0.2f, -2.1f, -1.6581f, 0.8029f, 0.0873f)
            )

            val Collar03b15 = Collar03a15.addOrReplaceChild(
                "Collar03b15",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.5f, 0f, -3f, 3f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.5f, -0.8378f, 0f, 0f)
            )

            val Collar03a9 = Collar02.addOrReplaceChild(
                "Collar03a9",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.4f, 0.4f, 2.6f, -1.7453f, -3.0369f, 0.0524f)
            )

            val Collar03b9 = Collar03a9.addOrReplaceChild(
                "Collar03b9",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -3f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, 1.5f, -0.6981f, 0f, 0f)
            )

            val Collar03a10 = Collar02.addOrReplaceChild(
                "Collar03a10",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-1.5f, 0f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.4f, 0.4f, 2.6f, -1.7453f, 3.0369f, -0.0524f)
            )

            val Collar03b10 = Collar03a10.addOrReplaceChild(
                "Collar03b10",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.5f, 0f, -3f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, 1.5f, -0.6981f, 0f, 0f)
            )

            val Collar03a12 = Collar02.addOrReplaceChild(
                "Collar03a12",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-1.5f, 0f, -1.5f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5.4f, 0.1f, 2.7f, -1.7453f, 2.2689f, 0f)
            )

            val Collar03b12 = Collar03a12.addOrReplaceChild(
                "Collar03b12",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.5f, 0f, -3f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.5f, -0.8378f, 0f, 0f)
            )

            val Collar03a11 = Collar02.addOrReplaceChild(
                "Collar03a11",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.5f, 0.2f, 3.4f, -1.7453f, 2.618f, -0.0524f)
            )

            val Collar03b11 = Collar03a11.addOrReplaceChild(
                "Collar03b11",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -3f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, 1.5f, -0.7679f, 0f, 0f)
            )

            val Collar03a13 = Collar02.addOrReplaceChild(
                "Collar03a13",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-1.5f, 0f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.6f, 0.1f, 1.7f, -1.7453f, 1.6057f, 0f)
            )

            val Collar03b13 = Collar03a13.addOrReplaceChild(
                "Collar03b13",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.5f, 0f, -3f, 3f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, 1.5f, -0.9948f, 0f, 0f)
            )

            val Collar03a1 = Collar02.addOrReplaceChild(
                "Collar03a1",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-1.5f, 0f, -1.5f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offset(1.3f, 0.6f, -3.5f)
            )

            val Collar03b1 = Collar03a1.addOrReplaceChild(
                "Collar03b1",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.5f, 0f, -3f, 3f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.5f, -0.8727f, 0f, 0f)
            )

            val Collar03a4 = Collar02.addOrReplaceChild(
                "Collar03a4",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5f, 0.2f, -2.1f, -1.6581f, -0.8029f, -0.0873f)
            )

            val Collar03b4 = Collar03a4.addOrReplaceChild(
                "Collar03b4",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -3f, 3f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.5f, -0.8378f, 0f, 0f)
            )

            val Collar03a3 = Collar02.addOrReplaceChild(
                "Collar03a3",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.3f, 0.5f, -3.5f, -2.0071f, -0.2094f, 0.0698f)
            )

            val Collar03b3 = Collar03a3.addOrReplaceChild(
                "Collar03b3",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -2f, 2f, 4f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, 1.5f, -0.9599f, 0f, 0f)
            )

            val Collar03a14 = Collar02.addOrReplaceChild(
                "Collar03a14",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.6f, 0.1f, -1.3f, -1.7453f, 1.4312f, 0f)
            )

            val Collar03b14 = Collar03a14.addOrReplaceChild(
                "Collar03b14",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -3f, 3f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.5f, -0.8378f, 0f, 0f)
            )

            val Collar03a3_1 = Collar02.addOrReplaceChild(
                "Collar03a3_1",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-1.5f, 0f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.3f, 0.5f, -3.5f, -2.0071f, 0.2094f, -0.0698f)
            )

            val Collar03b3_1 = Collar03a3_1.addOrReplaceChild(
                "Collar03b3_1",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1f, 0f, -2f, 2f, 4f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, 1.5f, -0.9599f, 0f, 0f)
            )

            val Collar03a6 = Collar02.addOrReplaceChild(
                "Collar03a6",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 5f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.6f, 0.1f, 1.7f, -1.7453f, -1.6057f, 0f)
            )

            val Collar03b6 = Collar03a6.addOrReplaceChild(
                "Collar03b6",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -3f, 3f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, 1.5f, -0.9948f, 0f, 0f)
            )

            val Collar03a5 = Collar02.addOrReplaceChild(
                "Collar03a5",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-1.5f, 0f, -1.5f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.6f, 0.1f, -1.3f, -1.7453f, -1.4312f, 0f)
            )

            val Collar03b5 = Collar03a5.addOrReplaceChild(
                "Collar03b5",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.5f, 0f, -3f, 3f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.5f, -0.8378f, 0f, 0f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -0.8f, -0.7f)
            )
            addFaceLayer(GlowHead)

            val HeadHL = GlowHead.addOrReplaceChild(
                "HeadHL",
                CubeListBuilder.create().texOffs(40, 104).mirror()
                    .addBox(0f, -2.5f, -2.5f, 3f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.4f, -10.6f, 0.8f, -0.7854f, -0.1745f, -0.384f)
            )

            val HeadHL2 = HeadHL.addOrReplaceChild(
                "HeadHL2",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -2f, -2f, 1f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offset(3f, 0f, 0f)
            )

            val HeadHL3 = HeadHL2.addOrReplaceChild(
                "HeadHL3",
                CubeListBuilder.create().texOffs(44, 70).addBox(0f, -1.5f, -1.5f, 1f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offset(1f, 0f, 0f)
            )

            val HeadHR = GlowHead.addOrReplaceChild(
                "HeadHR",
                CubeListBuilder.create().texOffs(40, 104).addBox(-3f, -2.5f, -2.5f, 3f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.4f, -10.6f, 0.8f, -0.7854f, 0.1745f, 0.384f)
            )

            val HeadHR2 = HeadHR.addOrReplaceChild(
                "HeadHR2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -2f, -2f, 1f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offset(-3f, 0f, 0f)
            )

            val HeadHR3 = HeadHR2.addOrReplaceChild(
                "HeadHR3",
                CubeListBuilder.create().texOffs(44, 70).addBox(-1f, -1.5f, -1.5f, 1f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offset(-1f, 0f, 0f)
            )

            val GlowBodyMain2 = partdefinition.addOrReplaceChild(
                "GlowBodyMain2",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val GlowBodyMain2a = GlowBodyMain2.addOrReplaceChild(
                "GlowBodyMain2a",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(10f, -14f, -39f)
            )

            val EquipSR01 = GlowBodyMain2a.addOrReplaceChild(
                "EquipSR01",
                CubeListBuilder.create().texOffs(108, 25).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(40f, -11f, 13f, 0f, 0.5236f, 1.5708f)
            )

            val EquipSR02 = EquipSR01.addOrReplaceChild(
                "EquipSR02",
                CubeListBuilder.create().texOffs(108, 25).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 0f)
            )

            val EquipSR03 = EquipSR02.addOrReplaceChild(
                "EquipSR03",
                CubeListBuilder.create().texOffs(108, 25).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 0f)
            )

            val EquipSR04 = EquipSR03.addOrReplaceChild(
                "EquipSR04",
                CubeListBuilder.create().texOffs(108, 25).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 0f)
            )

            val EquipSR05 = EquipSR04.addOrReplaceChild(
                "EquipSR05",
                CubeListBuilder.create().texOffs(108, 25).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 0f)
            )

            val EquipSR01b = GlowBodyMain2a.addOrReplaceChild(
                "EquipSR01b",
                CubeListBuilder.create().texOffs(108, 25).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-33f, -9f, 13.7f, -0.5918f, -0.3665f, -0.5918f)
            )

            val EquipSR02b = EquipSR01b.addOrReplaceChild(
                "EquipSR02b",
                CubeListBuilder.create().texOffs(108, 25).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 0f)
            )

            val EquipSR03b = EquipSR02b.addOrReplaceChild(
                "EquipSR03b",
                CubeListBuilder.create().texOffs(108, 25).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 0f)
            )

            val EquipSR04b = EquipSR03b.addOrReplaceChild(
                "EquipSR04b",
                CubeListBuilder.create().texOffs(108, 25).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 0f)
            )

            val EquipSR01c = GlowBodyMain2a.addOrReplaceChild(
                "EquipSR01c",
                CubeListBuilder.create().texOffs(108, 25).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-12f, 30f, -19f, 0.5585f, -0.3491f, -2.5307f)
            )

            val EquipSR02c = EquipSR01c.addOrReplaceChild(
                "EquipSR02c",
                CubeListBuilder.create().texOffs(108, 25).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 0f)
            )

            val EquipSR03c = EquipSR02c.addOrReplaceChild(
                "EquipSR03c",
                CubeListBuilder.create().texOffs(108, 25).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 0f)
            )

            val EquipSR04c = EquipSR03c.addOrReplaceChild(
                "EquipSR04c",
                CubeListBuilder.create().texOffs(108, 25).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 0f)
            )

            val EquipSR05c = EquipSR04c.addOrReplaceChild(
                "EquipSR05c",
                CubeListBuilder.create().texOffs(108, 25).addBox(-4.5f, 0f, -0.5f, 9f, 16f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 16f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 256, 128)
        }
    }
}
