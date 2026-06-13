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
import org.trp.shincolle.entity.EntitySubmYo
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelSubmYo<T : EntityShipBase>(root: ModelPart) : ShincolleShipModel<T>() {

    private val BodyMain: ModelPart
    private val Butt: ModelPart
    private val Head: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val BodyMain1: ModelPart
    private val BodyMain2: ModelPart
    private val BoobL: ModelPart
    private val BoobL2: ModelPart
    private val BoobR: ModelPart
    private val BoobR2: ModelPart
    private val Butt1: ModelPart
    private val Butt2: ModelPart
    private val EquipBase: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft01: ModelPart
    private val LegRight02: ModelPart
    private val LegLeft02: ModelPart
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
    private val Hair03: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmRight02: ModelPart
    private val EquipBody00: ModelPart
    private val EquipJaw00: ModelPart
    private val EquipHeadBack00: ModelPart
    private val EquipBody01: ModelPart
    private val EquipBody02: ModelPart
    private val EquipJaw00a: ModelPart
    private val EquipT01: ModelPart
    private val EquipJaw01: ModelPart
    private val EquipJaw02: ModelPart
    private val EquipJaw03: ModelPart
    private val EquipJaw04: ModelPart
    private val EquipJaw01a: ModelPart
    private val EquipJaw02a: ModelPart
    private val EquipJaw03a: ModelPart
    private val EquipJaw04a: ModelPart
    private val EquipT01a: ModelPart
    private val EquipT01b: ModelPart
    private val EquipT01c: ModelPart
    private val EquipHeadBack00a: ModelPart
    private val EquipHead00: ModelPart
    private val EquipT02: ModelPart
    private val EquipHead00a: ModelPart
    private val EquipHead00b: ModelPart
    private val EquipHead00c: ModelPart
    private val Eye01: ModelPart
    private val Eye02: ModelPart
    private val Eye03: ModelPart
    private val EquipHead01: ModelPart
    private val EquipHead02: ModelPart
    private val EquipHead03: ModelPart
    private val EquipHead04: ModelPart
    private val EquipHead01a: ModelPart
    private val EquipHead02a: ModelPart
    private val EquipHead03a: ModelPart
    private val EquipHead04a: ModelPart
    private val EquipE01a: ModelPart
    private val EquipE01b: ModelPart
    private val EquipE01c: ModelPart
    private val EquipE01d: ModelPart
    private val EquipT02a: ModelPart
    private val EquipT02b: ModelPart
    private val EquipT02c: ModelPart
    private val EquipS02a: ModelPart
    private val EquipS02b: ModelPart
    private val EquipS02c: ModelPart
    private val EquipS02d: ModelPart
    private val EquipS01a: ModelPart
    private val EquipS01b: ModelPart
    private val EquipS01c: ModelPart
    private val EquipS01d: ModelPart
    private val EquipT03: ModelPart
    private val EquipT04: ModelPart
    private val EquipT03a: ModelPart
    private val EquipT03b: ModelPart
    private val EquipT03c: ModelPart
    private val EquipT04a: ModelPart
    private val EquipT04b: ModelPart
    private val EquipT04c: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowHead: ModelPart
    private val GlowEquipBase: ModelPart
    private val GlowEquipBody00: ModelPart
    private val GlowEquipHeadBack00: ModelPart
    private val GlowEquipHeadBack00a: ModelPart
    private val GlowEquipHead00: ModelPart
    private val GlowEquipBody01: ModelPart
    private val buttDefaultZ: Float
    private val hair01DefaultXRot: Float
    private val hair01DefaultZRot: Float
    private val hair02DefaultXRot: Float
    private val hair02DefaultZRot: Float
    private val hair03DefaultXRot: Float
    private val hair03DefaultZRot: Float
    private val hairL01DefaultXRot: Float
    private val hairL01DefaultZRot: Float
    private val hairL02DefaultXRot: Float
    private val hairL02DefaultZRot: Float
    private val hairR01DefaultXRot: Float
    private val hairR01DefaultZRot: Float
    private val hairR02DefaultXRot: Float
    private val hairR02DefaultZRot: Float
    private val equipHeadBack00DefaultXRot: Float
    private val equipT01aDefaultXRot: Float
    private val equipT01bDefaultXRot: Float
    private val equipT01cDefaultXRot: Float
    private val equipT02aDefaultXRot: Float
    private val equipT02bDefaultXRot: Float
    private val equipT02cDefaultXRot: Float
    private val equipT03aDefaultXRot: Float
    private val equipT03bDefaultXRot: Float
    private val equipT03cDefaultXRot: Float
    private val equipT04aDefaultXRot: Float
    private val equipT04bDefaultXRot: Float
    private val equipT04cDefaultXRot: Float
    private val equipT03aDefaultZRot: Float
    private val equipT03bDefaultZRot: Float
    private val equipT03cDefaultZRot: Float
    private val equipT04aDefaultZRot: Float
    private val equipT04bDefaultZRot: Float
    private val equipT04cDefaultZRot: Float
    private val equipS01aDefaultXRot: Float
    private val equipS01bDefaultXRot: Float
    private val equipS01cDefaultXRot: Float
    private val equipS01dDefaultXRot: Float

    protected override val bodyMain: ModelPart get() = BodyMain
    protected override val neck: ModelPart? = null
    protected override val head: ModelPart get() = Head
    protected override val glowBodyMain: ModelPart get() = GlowBodyMain
    protected override val glowNeck: ModelPart? = null
    protected override val glowHead: ModelPart get() = GlowHead

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.BodyMain2 = this.BodyMain.getChild("BodyMain2")
        this.BoobL2 = this.BodyMain.getChild("BoobL2")
        this.BoobR2 = this.BodyMain.getChild("BoobR2")
        this.Head = this.BodyMain.getChild("Head")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.Hair03 = this.Hair02.getChild("Hair03")
        this.Hair = this.Head.getChild("Hair")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.Butt1 = this.BodyMain.getChild("Butt1")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.BodyMain1 = this.BodyMain.getChild("BodyMain1")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.EquipBase = this.BodyMain.getChild("EquipBase")
        this.EquipBody00 = this.EquipBase.getChild("EquipBody00")
        this.EquipBody01 = this.EquipBody00.getChild("EquipBody01")
        this.EquipS02b = this.EquipBody01.getChild("EquipS02b")
        this.EquipS02a = this.EquipBody01.getChild("EquipS02a")
        this.EquipS02c = this.EquipBody01.getChild("EquipS02c")
        this.EquipS02d = this.EquipBody01.getChild("EquipS02d")
        this.EquipJaw00 = this.EquipBody00.getChild("EquipJaw00")
        this.EquipJaw00a = this.EquipJaw00.getChild("EquipJaw00a")
        this.EquipJaw04 = this.EquipJaw00a.getChild("EquipJaw04")
        this.EquipJaw04a = this.EquipJaw04.getChild("EquipJaw04a")
        this.EquipJaw03 = this.EquipJaw00a.getChild("EquipJaw03")
        this.EquipJaw03a = this.EquipJaw03.getChild("EquipJaw03a")
        this.EquipJaw01 = this.EquipJaw00a.getChild("EquipJaw01")
        this.EquipJaw01a = this.EquipJaw01.getChild("EquipJaw01a")
        this.EquipJaw02 = this.EquipJaw00a.getChild("EquipJaw02")
        this.EquipJaw02a = this.EquipJaw02.getChild("EquipJaw02a")
        this.EquipT01 = this.EquipJaw00.getChild("EquipT01")
        this.EquipT01a = this.EquipT01.getChild("EquipT01a")
        this.EquipT01b = this.EquipT01a.getChild("EquipT01b")
        this.EquipT01c = this.EquipT01b.getChild("EquipT01c")
        this.EquipBody02 = this.EquipBody00.getChild("EquipBody02")
        this.EquipT03 = this.EquipBody02.getChild("EquipT03")
        this.EquipT03a = this.EquipT03.getChild("EquipT03a")
        this.EquipT03b = this.EquipT03a.getChild("EquipT03b")
        this.EquipT03c = this.EquipT03b.getChild("EquipT03c")
        this.EquipT04 = this.EquipBody02.getChild("EquipT04")
        this.EquipT04a = this.EquipT04.getChild("EquipT04a")
        this.EquipT04b = this.EquipT04a.getChild("EquipT04b")
        this.EquipT04c = this.EquipT04b.getChild("EquipT04c")
        this.EquipHeadBack00 = this.EquipBody00.getChild("EquipHeadBack00")
        this.EquipHeadBack00a = this.EquipHeadBack00.getChild("EquipHeadBack00a")
        this.EquipT02 = this.EquipHeadBack00a.getChild("EquipT02")
        this.EquipT02a = this.EquipT02.getChild("EquipT02a")
        this.EquipT02b = this.EquipT02a.getChild("EquipT02b")
        this.EquipT02c = this.EquipT02b.getChild("EquipT02c")
        this.EquipHead00 = this.EquipHeadBack00a.getChild("EquipHead00")
        this.EquipHead00b = this.EquipHead00.getChild("EquipHead00b")
        this.EquipHead00c = this.EquipHead00.getChild("EquipHead00c")
        this.EquipHead00a = this.EquipHead00.getChild("EquipHead00a")
        this.EquipHead01 = this.EquipHead00a.getChild("EquipHead01")
        this.EquipHead01a = this.EquipHead01.getChild("EquipHead01a")
        this.EquipHead02 = this.EquipHead00a.getChild("EquipHead02")
        this.EquipHead02a = this.EquipHead02.getChild("EquipHead02a")
        this.EquipHead04 = this.EquipHead00a.getChild("EquipHead04")
        this.EquipHead04a = this.EquipHead04.getChild("EquipHead04a")
        this.EquipHead03 = this.EquipHead00a.getChild("EquipHead03")
        this.EquipHead03a = this.EquipHead03.getChild("EquipHead03a")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.Butt2 = this.BodyMain.getChild("Butt2")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowHead = this.GlowBodyMain.getChild("GlowHead")
        this.GlowEquipBase = this.GlowBodyMain.getChild("GlowEquipBase")
        this.initFaceParts(this.GlowHead)
        this.GlowEquipBody00 = this.GlowEquipBase.getChild("GlowEquipBody00")
        this.GlowEquipHeadBack00 = this.GlowEquipBody00.getChild("GlowEquipHeadBack00")
        this.GlowEquipHeadBack00a = this.GlowEquipHeadBack00.getChild("GlowEquipHeadBack00a")
        this.GlowEquipHead00 = this.GlowEquipHeadBack00a.getChild("GlowEquipHead00")
        this.Eye01 = this.GlowEquipHead00.getChild("Eye01")
        this.Eye02 = this.GlowEquipHead00.getChild("Eye02")
        this.Eye03 = this.GlowEquipHead00.getChild("Eye03")
        this.EquipE01b = this.Eye03.getChild("EquipE01b")
        this.EquipE01d = this.Eye03.getChild("EquipE01d")
        this.EquipE01c = this.Eye03.getChild("EquipE01c")
        this.EquipE01a = this.Eye03.getChild("EquipE01a")
        this.GlowEquipBody01 = this.GlowEquipBody00.getChild("GlowEquipBody01")
        this.EquipS01a = this.GlowEquipBody01.getChild("EquipS01a")
        this.EquipS01b = this.GlowEquipBody01.getChild("EquipS01b")
        this.EquipS01c = this.GlowEquipBody01.getChild("EquipS01c")
        this.EquipS01d = this.GlowEquipBody01.getChild("EquipS01d")
        this.buttDefaultZ = this.Butt.z
        this.hair01DefaultXRot = this.Hair01.xRot
        this.hair01DefaultZRot = this.Hair01.zRot
        this.hair02DefaultXRot = this.Hair02.xRot
        this.hair02DefaultZRot = this.Hair02.zRot
        this.hair03DefaultXRot = this.Hair03.xRot
        this.hair03DefaultZRot = this.Hair03.zRot
        this.hairL01DefaultXRot = this.HairL01.xRot
        this.hairL01DefaultZRot = this.HairL01.zRot
        this.hairL02DefaultXRot = this.HairL02.xRot
        this.hairL02DefaultZRot = this.HairL02.zRot
        this.hairR01DefaultXRot = this.HairR01.xRot
        this.hairR01DefaultZRot = this.HairR01.zRot
        this.hairR02DefaultXRot = this.HairR02.xRot
        this.hairR02DefaultZRot = this.HairR02.zRot
        this.equipHeadBack00DefaultXRot = this.EquipHeadBack00.xRot
        this.equipT01aDefaultXRot = this.EquipT01a.xRot
        this.equipT01bDefaultXRot = this.EquipT01b.xRot
        this.equipT01cDefaultXRot = this.EquipT01c.xRot
        this.equipT02aDefaultXRot = this.EquipT02a.xRot
        this.equipT02bDefaultXRot = this.EquipT02b.xRot
        this.equipT02cDefaultXRot = this.EquipT02c.xRot
        this.equipT03aDefaultXRot = this.EquipT03a.xRot
        this.equipT03bDefaultXRot = this.EquipT03b.xRot
        this.equipT03cDefaultXRot = this.EquipT03c.xRot
        this.equipT04aDefaultXRot = this.EquipT04a.xRot
        this.equipT04bDefaultXRot = this.EquipT04b.xRot
        this.equipT04cDefaultXRot = this.EquipT04c.xRot
        this.equipT03aDefaultZRot = this.EquipT03a.zRot
        this.equipT03bDefaultZRot = this.EquipT03b.zRot
        this.equipT03cDefaultZRot = this.EquipT03c.zRot
        this.equipT04aDefaultZRot = this.EquipT04a.zRot
        this.equipT04bDefaultZRot = this.EquipT04b.zRot
        this.equipT04cDefaultZRot = this.EquipT04c.zRot
        this.equipS01aDefaultXRot = this.EquipS01a.xRot
        this.equipS01bDefaultXRot = this.EquipS01b.xRot
        this.equipS01cDefaultXRot = this.EquipS01c.xRot
        this.equipS01dDefaultXRot = this.EquipS01d.xRot
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
        this.Butt.z = this.buttDefaultZ
        this.Hair01.xRot = this.hair01DefaultXRot
        this.Hair01.zRot = this.hair01DefaultZRot
        this.Hair02.xRot = this.hair02DefaultXRot
        this.Hair02.zRot = this.hair02DefaultZRot
        this.Hair03.xRot = this.hair03DefaultXRot
        this.Hair03.zRot = this.hair03DefaultZRot
        this.HairL01.xRot = this.hairL01DefaultXRot
        this.HairL01.zRot = this.hairL01DefaultZRot
        this.HairL02.xRot = this.hairL02DefaultXRot
        this.HairL02.zRot = this.hairL02DefaultZRot
        this.HairR01.xRot = this.hairR01DefaultXRot
        this.HairR01.zRot = this.hairR01DefaultZRot
        this.HairR02.xRot = this.hairR02DefaultXRot
        this.HairR02.zRot = this.hairR02DefaultZRot

        this.EquipHeadBack00.xRot = this.equipHeadBack00DefaultXRot
        this.EquipT01a.xRot = this.equipT01aDefaultXRot
        this.EquipT01b.xRot = this.equipT01bDefaultXRot
        this.EquipT01c.xRot = this.equipT01cDefaultXRot
        this.EquipT02a.xRot = this.equipT02aDefaultXRot
        this.EquipT02b.xRot = this.equipT02bDefaultXRot
        this.EquipT02c.xRot = this.equipT02cDefaultXRot
        this.EquipT03a.xRot = this.equipT03aDefaultXRot
        this.EquipT03b.xRot = this.equipT03bDefaultXRot
        this.EquipT03c.xRot = this.equipT03cDefaultXRot
        this.EquipT04a.xRot = this.equipT04aDefaultXRot
        this.EquipT04b.xRot = this.equipT04bDefaultXRot
        this.EquipT04c.xRot = this.equipT04cDefaultXRot
        this.EquipT03a.zRot = this.equipT03aDefaultZRot
        this.EquipT03b.zRot = this.equipT03bDefaultZRot
        this.EquipT03c.zRot = this.equipT03cDefaultZRot
        this.EquipT04a.zRot = this.equipT04aDefaultZRot
        this.EquipT04b.zRot = this.equipT04bDefaultZRot
        this.EquipT04c.zRot = this.equipT04cDefaultZRot
        this.EquipS01a.xRot = this.equipS01aDefaultXRot
        this.EquipS01b.xRot = this.equipS01bDefaultXRot
        this.EquipS01c.xRot = this.equipS01cDefaultXRot
        this.EquipS01d.xRot = this.equipS01dDefaultXRot
    }

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) return
        val showEquip = entity.getEquipFlag(EntitySubmYo.EQUIP_BASE)
        this.EquipBase.visible = showEquip
        this.GlowEquipBase.visible = showEquip
        this.Hair03.visible = showEquip
        this.LegLeft01.visible = !showEquip
        this.LegRight01.visible = !showEquip
        this.Head.visible = true
        this.GlowHead.visible = true
        val showNormalBody = entity.getEquipFlag(EntitySubmYo.EQUIP_NORMAL_BODY)
        this.BodyMain1.visible = showNormalBody
        this.Butt1.visible = showNormalBody
        this.BoobL.visible = showNormalBody
        this.BoobR.visible = showNormalBody
        this.BodyMain2.visible = !showNormalBody
        this.Butt2.visible = !showNormalBody
        this.BoobL2.visible = !showNormalBody
        this.BoobR2.visible = !showNormalBody
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y
        this.poseTranslateZ = DEAD_TRANSLATE_Z

        this.EquipBase.visible = true
        this.GlowEquipBase.visible = true

        this.Head.visible = false
        this.GlowHead.visible = false
        this.LegLeft01.visible = false
        this.LegRight01.visible = false

        this.BoobL.xRot = -0.76f
        this.BoobR.xRot = -0.76f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = 0.21f
        this.Butt.z = this.buttDefaultZ

        this.Hair01.xRot = 0.209f
        this.Hair01.zRot = 0.0f
        this.Hair02.xRot = -0.087f
        this.Hair02.zRot = 0.0f
        this.Hair03.xRot = -0.139f
        this.Hair03.zRot = 0.0f
        this.HairL01.xRot = -0.1f
        this.HairL02.xRot = 0.3142f
        this.HairR01.xRot = -0.1f
        this.HairR02.xRot = 0.1745f
        this.HairL01.zRot = -0.0524f
        this.HairL02.zRot = 0.1745f
        this.HairR01.zRot = 0.1396f
        this.HairR02.zRot = -0.1396f

        this.BodyMain.xRot = 0.2f
        this.ArmLeft01.xRot = -0.25f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = 0.2618f
        this.ArmRight01.xRot = -0.25f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = -0.2618f

        this.EquipHeadBack00.xRot = -0.15f
        this.EquipT01a.xRot = 0.5f
        this.EquipT01b.xRot = 0.5f
        this.EquipT01c.xRot = 0.5f
        this.EquipT02a.xRot = -0.7f
        this.EquipT02b.xRot = -0.5f
        this.EquipT02c.xRot = -0.5f

        this.EquipT03a.xRot = 0.0f
        this.EquipT03b.xRot = 0.0f
        this.EquipT03c.xRot = 0.0f
        this.EquipT04a.xRot = 0.0f
        this.EquipT04b.xRot = 0.0f
        this.EquipT04c.xRot = 0.0f
        this.EquipT03a.zRot = 0.5f
        this.EquipT03b.zRot = 0.6f
        this.EquipT03c.zRot = 0.7f
        this.EquipT04a.zRot = 0.3f
        this.EquipT04b.zRot = 0.3f
        this.EquipT04c.zRot = 0.3f
        this.EquipS01a.xRot = 0.2f
        this.EquipS01b.xRot = 0.3f
        this.EquipS01c.xRot = 0.2f
        this.EquipS01d.xRot = 0.5f
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
        val angleX4 = Mth.cos(ageInTicks * 0.3f + 2.0f + limbSwing * 0.5f)
        val angleX5 = Mth.cos(ageInTicks * 0.3f + 4.0f + limbSwing * 0.5f)
        val angleX6 = Mth.cos(ageInTicks * 0.3f + 6.0f + limbSwing * 0.5f)
        val angleX7 = Mth.sin(ageInTicks)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount * 0.7f
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount * 0.7f

        if (entity!!.shipDepth > 0.0) {
            this.poseTranslateY = angleX * 0.05f + 0.025f
        }

        this.Head.xRot = headPitch * (Math.PI.toFloat() / 180f) * 0.8f
        this.Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f) * 0.57f
        this.Head.zRot = 0.0f

        this.BoobL.xRot = angleX * 0.08f - 0.76f
        this.BoobR.xRot = angleX * 0.08f - 0.76f
        this.Ahoke.yRot = angleX * 0.15f + 0.6f

        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = 0.21f
        this.Butt.z = this.buttDefaultZ

        this.Hair01.xRot = 0.209f
        this.Hair01.zRot = 0.0f
        this.Hair02.xRot = -0.087f
        this.Hair02.zRot = 0.0f
        this.Hair03.xRot = -0.139f
        this.Hair03.zRot = 0.0f
        this.HairL01.xRot = -0.1f
        this.HairL02.xRot = 0.3142f
        this.HairR01.xRot = -0.1f
        this.HairR02.xRot = 0.1745f
        this.HairL01.zRot = -0.0524f
        this.HairL02.zRot = 0.1745f
        this.HairR01.zRot = 0.1396f
        this.HairR02.zRot = -0.1396f

        val showEquip = entity != null && entity.getEquipFlag(EntitySubmYo.EQUIP_BASE)
        var addk1 = angleAdd1 * 0.6f - 0.157f
        var addk2 = angleAdd2 * 0.6f - 0.035f
        if (showEquip) {
            this.poseTranslateY += angleX * 0.035f + 0.1f
            this.poseTranslateZ += -0.1f
            this.Head.xRot -= 0.7f
            this.BodyMain.xRot = 0.7f

            this.ArmLeft01.xRot = -angleX * 0.1f - 1.0472f
            this.ArmLeft01.yRot = 0.0f
            this.ArmLeft01.zRot = -angleX * 0.1f - 0.7f
            this.ArmRight01.xRot = -angleX * 0.1f - 1.0472f
            this.ArmRight01.yRot = 0.0f
            this.ArmRight01.zRot = angleX * 0.1f + 0.7f

            this.EquipHeadBack00.xRot = angleX * 0.05f - 1.7f
            this.EquipT01a.xRot = angleX6 * 0.22f + 0.5f
            this.EquipT01b.xRot = angleX5 * 0.44f
            this.EquipT01c.xRot = angleX4 * 0.66f
            this.EquipT02a.xRot = -angleX6 * 0.22f
            this.EquipT02b.xRot = -angleX5 * 0.44f
            this.EquipT02c.xRot = -angleX4 * 0.66f

            this.EquipT03a.xRot = 0.0f
            this.EquipT03b.xRot = 0.0f
            this.EquipT03c.xRot = 0.0f
            this.EquipT04a.xRot = 0.0f
            this.EquipT04b.xRot = 0.0f
            this.EquipT04c.xRot = 0.0f
            this.EquipT03a.zRot = angleX6 * 0.25f
            this.EquipT03b.zRot = angleX5 * 0.5f
            this.EquipT03c.zRot = angleX4 * 0.75f
            this.EquipT04a.zRot = -angleX6 * 0.25f
            this.EquipT04b.zRot = -angleX5 * 0.5f
            this.EquipT04c.zRot = -angleX4 * 0.75f

            val randSim = Mth.sin(ageInTicks * 0.5f) * 0.5f + 0.5f
            this.EquipS01a.xRot = angleX7 * 0.05f * randSim - 0.2618f
            this.EquipS01b.xRot = angleX7 * 0.05f * randSim - 0.2618f
            this.EquipS01c.xRot = -angleX7 * 0.05f * randSim + 0.2618f
            this.EquipS01d.xRot = -angleX7 * 0.05f * randSim + 0.2618f
            addk1 = 0.0f
            addk2 = 0.0f
        } else {
            this.Head.xRot += 0.1f
            this.BodyMain.xRot = -0.1047f
            this.ArmLeft01.xRot = 0.2094f
            this.ArmLeft01.yRot = 0.0f
            this.ArmLeft01.zRot = -angleX * 0.05f - 0.3142f
            this.ArmRight01.xRot = 0.0f
            this.ArmRight01.yRot = 0.0f
            this.ArmRight01.zRot = angleX * 0.05f + 0.2094f
            this.LegLeft01.yRot = 0.0f
            this.LegLeft01.zRot = 0.1f
            this.LegRight01.yRot = 0.0f
            this.LegRight01.zRot = -0.1f
        }

        this.LegLeft01.xRot = addk1
        this.LegRight01.xRot = addk2
    }

    private fun applySpecialPoseAdjustments(entity: T?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        val isPassenger = entity!!.isPassenger
        val isCrouching = entity.isCrouching
        val isSprinting = if (entity != null) entity.isSprinting else limbSwingAmount > 0.92f
        val isSitting = entity.isInSittingPose || (isPassenger && entity.vehicle !is EntityMountBase)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount * 0.7f

        if (isSprinting) {
            this.poseTranslateY += SPRINT_TRANSLATE_Y
            this.Head.xRot += 0.6f
            this.Head.xRot -= 1.1f
            this.BodyMain.xRot = 1.1f
            this.BoobL.xRot = angleAdd1 * 0.08f - 0.7f
            this.BoobL.zRot = -0.07f
            this.BoobR.xRot = angleAdd1 * 0.08f - 0.7f
            this.BoobR.zRot = 0.07f

            this.ArmLeft01.xRot = -2.5133f
            this.ArmLeft01.zRot = -0.22f
            this.ArmRight01.xRot = -2.5133f
            this.ArmRight01.zRot = 0.22f
            this.LegLeft01.zRot = 0.05f
            this.LegRight01.zRot = -0.05f
        }

        if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            this.Head.xRot -= 1.0472f
            this.BodyMain.xRot = 1.0472f
            this.Butt.xRot = -0.8378f
            this.Hair01.xRot -= 0.1f
            this.Hair02.xRot -= 0.2f
            this.Hair03.xRot -= 0.5f
            this.ArmLeft01.xRot = -0.7f
            this.ArmLeft01.zRot = 0.2618f
            this.ArmRight01.xRot = -0.7f
            this.ArmRight01.zRot = -0.2618f
            this.LegLeft01.xRot -= 0.1f
            this.LegRight01.xRot -= 0.1f

            this.Head.xRot += 0.8f
            this.ArmLeft01.xRot = -0.25f
            this.ArmRight01.xRot = -0.25f
            this.EquipHeadBack00.xRot += 0.4f
        }

        if (isSitting && !isPassenger) {
            val showEquip = entity != null && entity.getEquipFlag(EntitySubmYo.EQUIP_BASE)
            val angleX = Mth.cos(ageInTicks * 0.08f)
            if (entity != null && hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY -= angleX * 0.05f
                this.Head.xRot *= 0.5f
                this.Head.yRot *= 0.75f
                this.Head.xRot += 0.5f
                this.BodyMain.xRot = 1.6f
                this.ArmLeft01.xRot = -1.6f
                this.ArmLeft01.zRot = -2.3f
                this.ArmRight01.xRot = -1.6f
                this.ArmRight01.zRot = 2.3f
                this.LegLeft01.xRot = -1.6f
                this.LegRight01.xRot = -1.6f
                this.LegLeft01.yRot = -0.1f - angleX * 0.05f
                this.LegRight01.yRot = 0.1f + angleX * 0.05f
                if (showEquip) {
                    this.poseTranslateY += 0.36f
                    val ax = Mth.cos(ageInTicks * 0.5f) * 0.5f
                    this.ArmLeft01.xRot = ax + 0.1f
                    this.ArmRight01.xRot = -ax + 0.1f
                    this.EquipHeadBack00.xRot = ax * 0.1f - 0.7f
                }
            } else {
                this.Head.xRot -= 0.7f
                this.BodyMain.xRot = 0.5236f
                this.ArmLeft01.xRot = -0.4f
                this.ArmLeft01.zRot = 0.3146f
                this.ArmRight01.xRot = -0.4f
                this.ArmRight01.zRot = -0.3146f
                this.LegLeft01.xRot = -2.18f
                this.LegRight01.xRot = -2.18f
                this.LegLeft01.yRot = -0.3491f
                this.LegRight01.yRot = 0.3491f
                if (showEquip) {
                    this.Head.xRot += 0.7f
                    this.BodyMain.xRot = 0.3f
                    this.ArmLeft01.xRot = -0.27f
                    this.ArmLeft01.zRot = 0.3146f
                    this.ArmRight01.xRot = -0.27f
                    this.ArmRight01.zRot = -0.3146f
                    this.EquipHeadBack00.xRot += 0.45f
                } else {
                    this.poseTranslateY += 0.45f * 3
                }
            }
        }

        if (entity != null && entity.attackTick > 41) {
            var ft = (50 - entity.attackTick) + (ageInTicks - ageInTicks.toInt())
            val fa = Mth.sin((0.125f.let { ft *= it; ft }) * ft * Math.PI.toFloat())
            val fb = Mth.sin(Mth.sqrt(ft) * Math.PI.toFloat())
            this.ArmLeft01.xRot += -fb * 80.0f * (Math.PI.toFloat() / 180f) - 0.3f
            this.ArmLeft01.yRot += fa * 20.0f * (Math.PI.toFloat() / 180f) - 0.4f
            this.ArmLeft01.zRot += fb * 20.0f * (Math.PI.toFloat() / 180f)
            this.ArmRight01.xRot += -fb * 80.0f * (Math.PI.toFloat() / 180f) - 0.3f
            this.ArmRight01.yRot += -fa * 20.0f * (Math.PI.toFloat() / 180f) + 0.4f
            this.ArmRight01.zRot += -fb * 20.0f * (Math.PI.toFloat() / 180f)
        }

        applyHairMotion(limbSwing, ageInTicks)

        val partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
        val customAttackAnim = if (entity != null) entity.getCustomAttackAnim(partialTick) else 0.0f
        if (customAttackAnim > 0.0f) {
            val f7 = Mth.sin(customAttackAnim * customAttackAnim * Math.PI.toFloat())
            val f8 = Mth.sin(Mth.sqrt(customAttackAnim) * Math.PI.toFloat())
            this.ArmRight01.xRot += -f8 * 80.0f * (Math.PI.toFloat() / 180f) - 0.3f
            this.ArmRight01.yRot += -f7 * 20.0f * (Math.PI.toFloat() / 180f) + 0.4f
            this.ArmRight01.zRot += -f8 * 20.0f * (Math.PI.toFloat() / 180f)
        }
    }

    private fun applyHairMotion(limbSwing: Float, ageInTicks: Float) {
        val angleX = Mth.cos(ageInTicks * 0.08f)
        val angleX1 = Mth.cos(ageInTicks * 0.1f + 0.3f + limbSwing * 0.5f)
        val angleX2 = Mth.cos(ageInTicks * 0.1f + 0.6f + limbSwing * 0.5f)
        val angleX3 = Mth.cos(ageInTicks * 0.1f + 0.9f + limbSwing * 0.5f)

        val headX = this.Head.xRot * -0.5f
        val headZ = this.Head.zRot * -0.5f
        this.Hair01.xRot += angleX1 * 0.08f + headX
        this.Hair02.xRot += -angleX2 * 0.08f + headX * 0.5f + 0.1f
        this.Hair03.xRot += -angleX3 * 0.08f + headX * 0.5f + 0.1f
        this.Hair01.zRot += headZ
        this.Hair02.zRot += headZ * 0.5f
        this.Hair03.zRot += headZ * 0.5f

        this.HairL01.xRot += angleX * 0.04f + headX
        this.HairL02.xRot += angleX * 0.05f + headX * 0.8f
        this.HairR01.xRot += angleX * 0.04f + headX
        this.HairR02.xRot += angleX * 0.05f + headX * 0.8f
        this.HairL01.zRot += headZ
        this.HairL02.zRot += headZ
        this.HairR01.zRot += headZ * 2.5f
        this.HairR02.zRot += headZ * 0.8f
    }

    override fun syncExtraGlowParts() {
        this.GlowEquipHeadBack00.copyFrom(this.EquipHeadBack00)
    }

    companion object {
        @JvmField
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "subm_yo"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = LegacyPoseOffsets.deadY("ModelSubmYo")
        private val DEAD_TRANSLATE_Z = LegacyPoseOffsets.deadZ("ModelSubmYo")
        private val SNEAK_TRANSLATE_Y = LegacyPoseOffsets.sneakY("ModelSubmYo")
        private val SPRINT_TRANSLATE_Y = LegacyPoseOffsets.sprintY("ModelSubmYo")

        @JvmStatic
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 106).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -11f, -3f, 0.6981f, 0f, 0f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(2, 88).addBox(-3f, -1f, -2.5f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, -9.7f, -0.7f, -1.2217f, 0f, 0.8727f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(2, 88).addBox(0f, 0f, -5f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 10f, 2.5f)
            )

            val BodyMain2 = BodyMain.addOrReplaceChild(
                "BodyMain2",
                CubeListBuilder.create().texOffs(88, 0).addBox(-6.5f, -11f, -4f, 13f, 15f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val BoobL2 = BodyMain.addOrReplaceChild(
                "BoobL2",
                CubeListBuilder.create().texOffs(65, 34).mirror().addBox(-3f, 0f, 0f, 6f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.44f, -8.6f, -3.9f, -0.6981f, -0.0873f, -0.0698f)
            )

            val BoobR2 = BodyMain.addOrReplaceChild(
                "BoobR2",
                CubeListBuilder.create().texOffs(106, 37).addBox(-3f, 0f, 0f, 6f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.44f, -8.6f, -3.9f, -0.6981f, 0.0873f, 0.0698f)
            )

            val Head = BodyMain.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -11.8f, -0.5f, -0.5236f, 0f, 0f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(46, 104).addBox(-7.5f, 0f, 0f, 15f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.8f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(0, 62).addBox(-7.5f, 0f, 0f, 15f, 16f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8f, 1.1f, 0.576f, 0f, 0f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(0, 63).addBox(-8f, 0f, -5f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 13.5f, 5.5f, 0.3491f, 0f, 0f)
            )

            val Hair03 = Hair02.addOrReplaceChild(
                "Hair03",
                CubeListBuilder.create().texOffs(0, 40).addBox(-8f, 0f, -4.5f, 16f, 15f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12.5f, 0f, 1.7453f, 0f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 81).addBox(-8f, -8f, -7.4f, 16f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0.4f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(24, 91).addBox(-2.5f, 0f, 0f, 5f, 9f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.9f, 8f, -7.2f, 0.0873f, 0.1396f, -0.0524f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(24, 91).addBox(-2.5f, 0f, 0f, 5f, 12f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.3f, 7.5f, 0.1f, 0.3142f, 0.1745f, 0.1745f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(24, 88).mirror().addBox(-1f, 0f, 0f, 2f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.7f, 7.9f, -7.5f, -0.1396f, 0.4363f, 0.1396f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(24, 88).mirror().addBox(-1f, 0f, 0f, 2f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.3f, 10f, 0f, 0.1745f, 0.0873f, -0.1396f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(0, 18).addBox(0f, -5f, -10.5f, 0f, 11f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, -7f, -5.5f, 0.2618f, 0.6981f, 0f)
            )

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(50, 44).mirror()
                    .addBox(-8.5f, 0f, 0f, 17f, 15f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -7.7f)
            )

            val Butt1 = BodyMain.addOrReplaceChild(
                "Butt1",
                CubeListBuilder.create().texOffs(52, 66).addBox(-7.5f, 0f, -7f, 15f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.5f, 2.8f, 0.2094f, 0f, 0f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 2.5f, 2.8f, 0.2094f, 0f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 87).mirror().addBox(-3f, 0f, -3f, 6f, 12f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.4f, 6.5f, -4f, 1.5708f, 0f, 0.1047f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 87).mirror().addBox(0f, 0f, 0f, 6f, 13f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3f, 12f, -3f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 87).addBox(-3f, 0f, -3f, 6f, 12f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.4f, 6.5f, -4f, 1.5708f, 0f, -0.1047f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 87).addBox(-6f, 0f, 0f, 6f, 13f, 6f, CubeDeformation(0f)),
                PartPose.offset(3f, 12f, -3f)
            )

            val BodyMain1 = BodyMain.addOrReplaceChild(
                "BodyMain1",
                CubeListBuilder.create().texOffs(0, 106).addBox(-6.5f, -11f, -4f, 13f, 15f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(34, 102).mirror().addBox(-3f, 0f, 0f, 6f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.2f, -8.6f, -3.9f, -0.6981f, -0.0873f, -0.0698f)
            )

            val EquipBase = BodyMain.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipBody00 = EquipBase.addOrReplaceChild(
                "EquipBody00",
                CubeListBuilder.create().texOffs(1, 0).addBox(-10f, -10f, 1f, 20f, 12f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, 7.5f, -0.5236f, 0f, 0f)
            )

            val EquipBody01 = EquipBody00.addOrReplaceChild(
                "EquipBody01",
                CubeListBuilder.create().texOffs(5, 0).addBox(-8.5f, 0f, 0f, 17f, 12f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2.4f, 3f, 0.4538f, 0f, 0f)
            )

            val EquipS02b = EquipBody01.addOrReplaceChild(
                "EquipS02b",
                CubeListBuilder.create().texOffs(22, 32).addBox(-4.5f, -2.5f, -1f, 9f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(9f, 9f, 12f, -0.7854f, -1.7453f, 0f)
            )

            val EquipS02a = EquipBody01.addOrReplaceChild(
                "EquipS02a",
                CubeListBuilder.create().texOffs(22, 32).addBox(-4.5f, -2.5f, -1f, 9f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(9f, 9f, 4f, -0.7854f, -1.3963f, 0f)
            )

            val EquipS02c = EquipBody01.addOrReplaceChild(
                "EquipS02c",
                CubeListBuilder.create().texOffs(22, 32).addBox(-4.5f, -2.5f, -1f, 9f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9f, 9f, 12f, -0.7854f, 1.7453f, 0f)
            )

            val EquipS02d = EquipBody01.addOrReplaceChild(
                "EquipS02d",
                CubeListBuilder.create().texOffs(22, 32).addBox(-4.5f, -2.5f, -1f, 9f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9f, 9f, 4f, -0.7854f, 1.3963f, 0f)
            )

            val EquipJaw00 = EquipBody00.addOrReplaceChild(
                "EquipJaw00",
                CubeListBuilder.create().texOffs(1, 0).addBox(-10f, 0f, -11f, 20f, 12f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3f, 5f, 0.1396f, 0f, 0f)
            )

            val EquipJaw00a = EquipJaw00.addOrReplaceChild(
                "EquipJaw00a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-10f, -2f, -6f, 20f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 11f, -10f, 0.3491f, 0f, 0f)
            )

            val EquipJaw04 = EquipJaw00a.addOrReplaceChild(
                "EquipJaw04",
                CubeListBuilder.create().texOffs(18, 5).addBox(-6f, -15f, -4f, 12f, 15f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.8f, 1.2f, -2.7f, 0.1396f, 1.5708f, 0f)
            )

            val EquipJaw04a = EquipJaw04.addOrReplaceChild(
                "EquipJaw04a",
                CubeListBuilder.create().texOffs(22, 25).addBox(-6f, -5f, 0f, 12f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -14.5f, -3f, -0.1745f, 0f, 0f)
            )

            val EquipJaw03 = EquipJaw00a.addOrReplaceChild(
                "EquipJaw03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6f, -15f, -4f, 12f, 15f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.8f, 1.2f, -2.7f, 0.1396f, -1.5708f, 0f)
            )

            val EquipJaw03a = EquipJaw03.addOrReplaceChild(
                "EquipJaw03a",
                CubeListBuilder.create().texOffs(22, 25).addBox(-6f, -5f, 0f, 12f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -14.5f, -3f, -0.1745f, 0f, 0f)
            )

            val EquipJaw01 = EquipJaw00a.addOrReplaceChild(
                "EquipJaw01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6f, -16f, -4f, 12f, 15f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5.1f, 2f, -4f, 0.1745f, 0.1396f, 0f)
            )

            val EquipJaw01a = EquipJaw01.addOrReplaceChild(
                "EquipJaw01a",
                CubeListBuilder.create().texOffs(22, 25).addBox(-6f, -5f, 0f, 12f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15.5f, -3f, -0.1745f, 0f, 0f)
            )

            val EquipJaw02 = EquipJaw00a.addOrReplaceChild(
                "EquipJaw02",
                CubeListBuilder.create().texOffs(35, 0).addBox(-6f, -16f, -4f, 12f, 15f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.1f, 2f, -4f, 0.1745f, -0.1396f, 0f)
            )

            val EquipJaw02a = EquipJaw02.addOrReplaceChild(
                "EquipJaw02a",
                CubeListBuilder.create().texOffs(22, 25).addBox(-6f, -5f, 0f, 12f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15.5f, -3f, -0.1745f, 0f, 0f)
            )

            val EquipT01 = EquipJaw00.addOrReplaceChild(
                "EquipT01",
                CubeListBuilder.create().texOffs(38, 0).addBox(-3.5f, -3.5f, -3.5f, 7f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 15f, -8f, 0.6283f, 0f, 0f)
            )

            val EquipT01a = EquipT01.addOrReplaceChild(
                "EquipT01a",
                CubeListBuilder.create().texOffs(68, 14).addBox(-2.5f, 0f, -2.5f, 5f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, 0f, 0.3187f, 0f, 0f)
            )

            val EquipT01b = EquipT01a.addOrReplaceChild(
                "EquipT01b",
                CubeListBuilder.create().texOffs(68, 14).addBox(-2.5f, 0f, -2.5f, 5f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8.5f, 0f, 0.5236f, 0f, 0f)
            )

            val EquipT01c = EquipT01b.addOrReplaceChild(
                "EquipT01c",
                CubeListBuilder.create().texOffs(70, 15).addBox(-2f, 0f, -2f, 4f, 9f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8.5f, 0f, -0.3491f, 0f, 0f)
            )

            val EquipBody02 = EquipBody00.addOrReplaceChild(
                "EquipBody02",
                CubeListBuilder.create().texOffs(7, 0).addBox(-8f, 0f, 0f, 16f, 12f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -12f, 11f)
            )

            val EquipT03 = EquipBody02.addOrReplaceChild(
                "EquipT03",
                CubeListBuilder.create().texOffs(24, 7).addBox(-3.5f, 0f, -3.5f, 7f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, 9.5f, 8.5f, 1.3963f, 0.1745f, 0f)
            )

            val EquipT03a = EquipT03.addOrReplaceChild(
                "EquipT03a",
                CubeListBuilder.create().texOffs(68, 14).addBox(-2.5f, 0f, -2.5f, 5f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, 0f, -0.2618f, 0f, 0f)
            )

            val EquipT03b = EquipT03a.addOrReplaceChild(
                "EquipT03b",
                CubeListBuilder.create().texOffs(68, 14).addBox(-2.5f, 0f, -2.5f, 5f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8.5f, 0f, 0.3491f, 0f, 0f)
            )

            val EquipT03c = EquipT03b.addOrReplaceChild(
                "EquipT03c",
                CubeListBuilder.create().texOffs(70, 15).addBox(-2f, 0f, -2f, 4f, 9f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8.5f, 0f, -0.3491f, 0f, 0f)
            )

            val EquipT04 = EquipBody02.addOrReplaceChild(
                "EquipT04",
                CubeListBuilder.create().texOffs(0, 7).addBox(-3.5f, 0f, -3.5f, 7f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7f, 9.5f, 8.5f, 1.3963f, -0.1745f, 0f)
            )

            val EquipT04a = EquipT04.addOrReplaceChild(
                "EquipT04a",
                CubeListBuilder.create().texOffs(68, 14).addBox(-2.5f, 0f, -2.5f, 5f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, 0f, 0.1745f, 0f, 0f)
            )

            val EquipT04b = EquipT04a.addOrReplaceChild(
                "EquipT04b",
                CubeListBuilder.create().texOffs(68, 14).addBox(-2.5f, 0f, -2.5f, 5f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8.5f, 0f, -0.5236f, 0f, 0f)
            )

            val EquipT04c = EquipT04b.addOrReplaceChild(
                "EquipT04c",
                CubeListBuilder.create().texOffs(70, 15).addBox(-2f, 0f, -2f, 4f, 9f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8.5f, 0f, 0.3491f, 0f, 0f)
            )

            val EquipHeadBack00 = EquipBody00.addOrReplaceChild(
                "EquipHeadBack00",
                CubeListBuilder.create().texOffs(1, 0).addBox(-9f, -10f, -10f, 18f, 12f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -8f, 9f, -1.3963f, 0f, 0f)
            )

            val EquipHeadBack00a = EquipHeadBack00.addOrReplaceChild(
                "EquipHeadBack00a",
                CubeListBuilder.create().texOffs(6, 0).addBox(-8f, -11f, -11f, 16f, 11f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -4f, -3f)
            )

            val EquipT02 = EquipHeadBack00a.addOrReplaceChild(
                "EquipT02",
                CubeListBuilder.create().texOffs(20, 3).addBox(-3.5f, 0f, -3.5f, 7f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -9f, -10f, 2.618f, 0f, 0f)
            )

            val EquipT02a = EquipT02.addOrReplaceChild(
                "EquipT02a",
                CubeListBuilder.create().texOffs(68, 14).addBox(-2.5f, 0f, -2.5f, 5f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, 0f, -0.1745f, 0f, 0f)
            )

            val EquipT02b = EquipT02a.addOrReplaceChild(
                "EquipT02b",
                CubeListBuilder.create().texOffs(68, 14).addBox(-2.5f, 0f, -2.5f, 5f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8.5f, 0f, -0.5236f, 0f, 0f)
            )

            val EquipT02c = EquipT02b.addOrReplaceChild(
                "EquipT02c",
                CubeListBuilder.create().texOffs(70, 15).addBox(-2f, 0f, -2f, 4f, 9f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8.5f, 0f, 0.4189f, 0f, 0f)
            )

            val EquipHead00 = EquipHeadBack00a.addOrReplaceChild(
                "EquipHead00",
                CubeListBuilder.create().texOffs(1, 0).addBox(-10f, -12f, -11f, 20f, 12f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1f, -10f, 0.2094f, 0f, 0f)
            )

            val EquipHead00b = EquipHead00.addOrReplaceChild(
                "EquipHead00b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -3.5f, 3f, 10f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-10f, -2f, -2f, 0.2094f, -0.0698f, 0.1396f)
            )

            val EquipHead00c = EquipHead00.addOrReplaceChild(
                "EquipHead00c",
                CubeListBuilder.create().texOffs(17, 5).addBox(-1.5f, 0f, -3.5f, 3f, 10f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(10f, -2f, -2f, 0.2094f, 0.0698f, -0.1396f)
            )

            val EquipHead00a = EquipHead00.addOrReplaceChild(
                "EquipHead00a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-10f, -4f, -5.5f, 20f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -8f, -12f, 0.2094f, 0f, 0f)
            )

            val EquipHead01 = EquipHead00a.addOrReplaceChild(
                "EquipHead01",
                CubeListBuilder.create().texOffs(8, 6).addBox(-6f, 0f, -4f, 12f, 15f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5.1f, -4f, -4f, -0.1745f, 0.1396f, 0f)
            )

            val EquipHead01a = EquipHead01.addOrReplaceChild(
                "EquipHead01a",
                CubeListBuilder.create().texOffs(22, 25).addBox(-6f, 0f, 0f, 12f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 14.5f, -3f, 0.1745f, 0f, 0f)
            )

            val EquipHead02 = EquipHead00a.addOrReplaceChild(
                "EquipHead02",
                CubeListBuilder.create().texOffs(32, 0).addBox(-6f, 0f, -4f, 12f, 15f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.1f, -4f, -4f, -0.1745f, -0.1396f, 0f)
            )

            val EquipHead02a = EquipHead02.addOrReplaceChild(
                "EquipHead02a",
                CubeListBuilder.create().texOffs(22, 25).addBox(-6f, 0f, 0f, 12f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 14.5f, -3f, 0.1745f, 0f, 0f)
            )

            val EquipHead04 = EquipHead00a.addOrReplaceChild(
                "EquipHead04",
                CubeListBuilder.create().texOffs(34, 5).addBox(-6f, 0f, -4f, 12f, 15f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.8f, -4.2f, -2.6f, -0.1396f, 1.5708f, 0f)
            )

            val EquipHead04a = EquipHead04.addOrReplaceChild(
                "EquipHead04a",
                CubeListBuilder.create().texOffs(22, 25).addBox(-6f, 0f, 0f, 12f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 14.5f, -3f, 0.1745f, 0f, 0f)
            )

            val EquipHead03 = EquipHead00a.addOrReplaceChild(
                "EquipHead03",
                CubeListBuilder.create().texOffs(0, 4).addBox(-6f, 0f, -4f, 12f, 15f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.8f, -4.2f, -2.6f, -0.1396f, -1.5708f, 0f)
            )

            val EquipHead03a = EquipHead03.addOrReplaceChild(
                "EquipHead03a",
                CubeListBuilder.create().texOffs(22, 25).addBox(-6f, 0f, 0f, 12f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 14.5f, -3f, 0.1745f, 0f, 0f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(2, 88).mirror()
                    .addBox(-2f, -1f, -2.5f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.7f, -0.7f, -1.2217f, 0f, -0.8727f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(2, 88).mirror().addBox(-5f, 0f, -5f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 10f, 2.5f)
            )

            val Butt2 = BodyMain.addOrReplaceChild(
                "Butt2",
                CubeListBuilder.create().texOffs(82, 22).addBox(-7.5f, 0f, -7f, 15f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.5f, 2.8f, 0.2094f, 0f, 0f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(34, 102).addBox(-3f, 0f, 0f, 6f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.2f, -8.6f, -3.9f, -0.6981f, 0.0873f, 0.0698f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -11f, -3f, 0.6981f, 0f, 0f)
            )

            val GlowHead = GlowBodyMain.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -11.8f, -0.5f, -0.5236f, 0f, 0f)
            )
            addFaceLayer(GlowHead)

            val GlowEquipBase = GlowBodyMain.addOrReplaceChild(
                "GlowEquipBase",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 0f)
            )

            val GlowEquipBody00 = GlowEquipBase.addOrReplaceChild(
                "GlowEquipBody00",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 5f, 7.5f, -0.5236f, 0f, 0f)
            )

            val GlowEquipHeadBack00 = GlowEquipBody00.addOrReplaceChild(
                "GlowEquipHeadBack00",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -8f, 9f, -1.3963f, 0f, 0f)
            )

            val GlowEquipHeadBack00a = GlowEquipHeadBack00.addOrReplaceChild(
                "GlowEquipHeadBack00a",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -4f, -3f)
            )

            val GlowEquipHead00 = GlowEquipHeadBack00a.addOrReplaceChild(
                "GlowEquipHead00",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -1f, -10f, 0.2094f, 0f, 0f)
            )

            val Eye01 = GlowEquipHead00.addOrReplaceChild(
                "Eye01",
                CubeListBuilder.create().texOffs(70, 0).addBox(-1f, 0f, -3f, 2f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(11f, -8.5f, -6f, 0f, -0.1047f, -0.1745f)
            )

            val Eye02 = GlowEquipHead00.addOrReplaceChild(
                "Eye02",
                CubeListBuilder.create().texOffs(70, 0).addBox(-1f, 0f, -3f, 2f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-11f, -8.5f, -6f, 0f, 0.1047f, 0.1745f)
            )

            val Eye03 = GlowEquipHead00.addOrReplaceChild(
                "Eye03",
                CubeListBuilder.create().texOffs(70, 0).addBox(-1f, -3.5f, -3.5f, 2f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 0f, 0f, 0.8727f, 1.5708f)
            )

            val EquipE01b = Eye03.addOrReplaceChild(
                "EquipE01b",
                CubeListBuilder.create().texOffs(22, 32).addBox(-4.5f, -2.5f, -1f, 9f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.3f, -5f, 0f, -2.2689f, 1.5708f, 0f)
            )

            val EquipE01d = Eye03.addOrReplaceChild(
                "EquipE01d",
                CubeListBuilder.create().texOffs(22, 32).addBox(-4.5f, -2.5f, -1f, 9f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.5f, 0f, 5f, 0.5236f, 0f, 1.5708f)
            )

            val EquipE01c = Eye03.addOrReplaceChild(
                "EquipE01c",
                CubeListBuilder.create().texOffs(22, 32).addBox(-4.5f, -2.5f, -1f, 9f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.0472f, 0f, 1.5708f)
            )

            val EquipE01a = Eye03.addOrReplaceChild(
                "EquipE01a",
                CubeListBuilder.create().texOffs(22, 32).addBox(-4.5f, -2.5f, -1f, 9f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.3f, 5f, 0f, 2.2689f, 1.5708f, 0f)
            )

            val GlowEquipBody01 = GlowEquipBody00.addOrReplaceChild(
                "GlowEquipBody01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -2.4f, 3f, 0.4538f, 0f, 0f)
            )

            val EquipS01a = GlowEquipBody01.addOrReplaceChild(
                "EquipS01a",
                CubeListBuilder.create().texOffs(41, 35).addBox(-3f, 0f, -3f, 6f, 3f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.5f, 11f, 3f, -0.2618f, 0f, -0.2618f)
            )

            val EquipS01b = GlowEquipBody01.addOrReplaceChild(
                "EquipS01b",
                CubeListBuilder.create().texOffs(41, 35).addBox(-3f, 0f, -3f, 6f, 3f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.5f, 11f, 3f, -0.2618f, 0f, 0.2618f)
            )

            val EquipS01c = GlowEquipBody01.addOrReplaceChild(
                "EquipS01c",
                CubeListBuilder.create().texOffs(41, 35).addBox(-3f, 0f, -3f, 6f, 3f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.5f, 11f, 11f, 0.2618f, 0f, -0.2618f)
            )

            val EquipS01d = GlowEquipBody01.addOrReplaceChild(
                "EquipS01d",
                CubeListBuilder.create().texOffs(41, 35).addBox(-3f, 0f, -3f, 6f, 3f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.5f, 11f, 11f, 0.2618f, 0f, 0.2618f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
