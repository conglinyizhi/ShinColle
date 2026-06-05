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
import org.trp.shincolle.client.model.LegacyPoseOffsets.sittingAltY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sittingY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sneakY
import org.trp.shincolle.entity.EntityBBHaruna
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelBBHaruna<T : EntityShipBase?>(root: ModelPart) : ShipModelHumanoidBase<T?>(), IGlowableModel {
    private var isDeadPose = false
    private var poseTranslateY = 0f
    private var isSittingPose = false

    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val Butt: ModelPart
    private val ArmLeft01: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val ArmRight01: ModelPart
    private val Cloth03a1: ModelPart
    private val Cloth03a2: ModelPart
    private val EquipBase: ModelPart
    private val Head: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val EquipHeadBase: ModelPart
    private val HairU01: ModelPart
    private val HairR01: ModelPart
    private val HairL01: ModelPart
    private val HairCBase: ModelPart
    private val HairCBaseB: ModelPart
    private val Ahoke: ModelPart
    private val HairR02: ModelPart
    private val HairL02: ModelPart
    private val Hair01: ModelPart
    private val Hair02: ModelPart
    private val EquipHead01: ModelPart
    private val EquipHead00: ModelPart
    private val EquipHead01_1: ModelPart
    private val EquipHead02: ModelPart
    private val EquipHead03: ModelPart
    private val EquipHead02_1: ModelPart
    private val EquipHead03_1: ModelPart
    private val LegLeft01: ModelPart
    private val Skirt01: ModelPart
    private val LegRight01: ModelPart
    private val SkirtB01: ModelPart
    private val Cloth02a1: ModelPart
    private val Cloth02b1: ModelPart
    private val LegLeft02: ModelPart
    private val Skirt02: ModelPart
    private val LegRight02: ModelPart
    private val Cloth01a: ModelPart
    private val Cloth02c1: ModelPart
    private val Cloth02c1_1: ModelPart
    private val Cloth01b: ModelPart
    private val Cloth01c: ModelPart
    private val Cloth01b2: ModelPart
    private val Cloth01c2: ModelPart
    private val Cloth02c2: ModelPart
    private val Cloth02c3: ModelPart
    private val Cloth02c4: ModelPart
    private val Cloth02c2_1: ModelPart
    private val Cloth02c3_1: ModelPart
    private val Cloth02c4_1: ModelPart
    private val Cloth02a2: ModelPart
    private val Cloth02a3: ModelPart
    private val Cloth02b2: ModelPart
    private val Cloth02b3: ModelPart
    private val ArmLeft02: ModelPart
    private val ClothA01: ModelPart
    private val ClothA02: ModelPart
    private val ClothA03: ModelPart
    private val ClothA04: ModelPart
    private val ClothA05: ModelPart
    private val Cloth03b: ModelPart
    private val ClothB01: ModelPart
    private val Cloth03b_1: ModelPart
    private val ArmRight02: ModelPart
    private val ClothA01_1: ModelPart
    private val ClothA02a: ModelPart
    private val ClothA03a: ModelPart
    private val ClothA04a: ModelPart
    private val ClothA05a: ModelPart
    private val EquipD01a: ModelPart
    private val EquipD02a: ModelPart
    private val EquipD02b: ModelPart
    private val EquipD01b: ModelPart
    private val EquipD02c: ModelPart
    private val EquipD02d: ModelPart
    private val EquipD03a1: ModelPart
    private val EquipD03b1: ModelPart
    private val EquipD03c1: ModelPart
    private val EquipD03d1: ModelPart
    private val EquipD01aa: ModelPart
    private val EquipD01ba: ModelPart
    private val EquipD01bb: ModelPart
    private val EquipD03a2: ModelPart
    private val EquipD03aa: ModelPart
    private val EquipD03ab: ModelPart
    private val EquipD03a3: ModelPart
    private val EquipD03a4: ModelPart
    private val EquipB05: ModelPart
    private val EquipCL1Base01L2: ModelPart
    private val EquipCL1Base02: ModelPart
    private val EquipCL1a1: ModelPart
    private val EquipCL1a1_1: ModelPart
    private val EquipCL1Base01a: ModelPart
    private val EquipCL1a2: ModelPart
    private val EquipCL1a2_1: ModelPart
    private val EquipD03a2_1: ModelPart
    private val EquipD03aa_1: ModelPart
    private val EquipD03ab_1: ModelPart
    private val EquipD03a3_1: ModelPart
    private val EquipD03a4_1: ModelPart
    private val EquipB05_1: ModelPart
    private val EquipCL1Base01R2: ModelPart
    private val EquipCL1Base02_1: ModelPart
    private val EquipCL1a1_2: ModelPart
    private val EquipCL1a1_3: ModelPart
    private val EquipCL1Base01a_1: ModelPart
    private val EquipCL1a2_2: ModelPart
    private val EquipCL1a2_3: ModelPart
    private val EquipD03c1a: ModelPart
    private val EquipD03c1b: ModelPart
    private val EquipD03c2: ModelPart
    private val EquipD03c2a: ModelPart
    private val EquipD03c3: ModelPart
    private val EquipD03c3a: ModelPart
    private val EquipB05_2: ModelPart
    private val EquipCL1Base01L1: ModelPart
    private val EquipCL1Base02_2: ModelPart
    private val EquipCL1a1_4: ModelPart
    private val EquipCL1a1_5: ModelPart
    private val EquipCL1Base01a_2: ModelPart
    private val EquipCL1Base01b: ModelPart
    private val EquipCL1a2_4: ModelPart
    private val EquipCL1a2_5: ModelPart
    private val EquipD03c1a_1: ModelPart
    private val EquipD03c1b_1: ModelPart
    private val EquipD03c2_1: ModelPart
    private val EquipD03c2a_1: ModelPart
    private val EquipD03c3_1: ModelPart
    private val EquipD03c3a_1: ModelPart
    private val EquipB05_3: ModelPart
    private val EquipCL1Base01R1: ModelPart
    private val EquipCL1Base02_3: ModelPart
    private val EquipCL1a1_6: ModelPart
    private val EquipCL1a1_7: ModelPart
    private val EquipCL1Base01a_3: ModelPart
    private val EquipCL1Base01b_1: ModelPart
    private val EquipCL1a2_6: ModelPart
    private val EquipCL1a2_7: ModelPart
    private val GlowBodyMain: ModelPart?
    private val GlowHead: ModelPart
    private val GlowNeck: ModelPart
    private val buttDefaultY: Float
    private val buttDefaultZ: Float
    private val skirt01DefaultY: Float
    private val skirt01DefaultZ: Float
    private val clothA03DefaultX: Float
    private val clothA03DefaultY: Float
    private val clothA03DefaultZ: Float
    private val clothA03aDefaultX: Float
    private val clothA03aDefaultY: Float
    private val clothA03aDefaultZ: Float
    private val clothA04DefaultY: Float
    private val clothA04DefaultZ: Float
    private val clothA04aDefaultY: Float
    private val clothA04aDefaultZ: Float
    private val clothA05DefaultY: Float
    private val clothA05DefaultZ: Float
    private val clothA05aDefaultY: Float
    private val clothA05aDefaultZ: Float
    private val armLeft02DefaultX: Float
    private val armLeft02DefaultZ: Float
    private val armRight02DefaultX: Float
    private val armRight02DefaultZ: Float
    private val legLeft01DefaultX: Float
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

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Butt = this.BodyMain.getChild("Butt")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.Skirt02 = this.Skirt01.getChild("Skirt02")
        this.Cloth02b1 = this.Butt.getChild("Cloth02b1")
        this.Cloth02b2 = this.Cloth02b1.getChild("Cloth02b2")
        this.Cloth02b3 = this.Cloth02b2.getChild("Cloth02b3")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.SkirtB01 = this.Butt.getChild("SkirtB01")
        this.Cloth02c1 = this.SkirtB01.getChild("Cloth02c1")
        this.Cloth02c2 = this.Cloth02c1.getChild("Cloth02c2")
        this.Cloth02c3 = this.Cloth02c2.getChild("Cloth02c3")
        this.Cloth02c4 = this.Cloth02c3.getChild("Cloth02c4")
        this.Cloth02c1_1 = this.SkirtB01.getChild("Cloth02c1_1")
        this.Cloth02c2_1 = this.Cloth02c1_1.getChild("Cloth02c2_1")
        this.Cloth02c3_1 = this.Cloth02c2_1.getChild("Cloth02c3_1")
        this.Cloth02c4_1 = this.Cloth02c3_1.getChild("Cloth02c4_1")
        this.Cloth01a = this.SkirtB01.getChild("Cloth01a")
        this.Cloth01b2 = this.Cloth01a.getChild("Cloth01b2")
        this.Cloth01c = this.Cloth01a.getChild("Cloth01c")
        this.Cloth01c2 = this.Cloth01a.getChild("Cloth01c2")
        this.Cloth01b = this.Cloth01a.getChild("Cloth01b")
        this.Cloth02a1 = this.Butt.getChild("Cloth02a1")
        this.Cloth02a2 = this.Cloth02a1.getChild("Cloth02a2")
        this.Cloth02a3 = this.Cloth02a2.getChild("Cloth02a3")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.ClothA02 = this.ArmLeft02.getChild("ClothA02")
        this.ClothA03 = this.ClothA02.getChild("ClothA03")
        this.ClothA04 = this.ClothA03.getChild("ClothA04")
        this.ClothA05 = this.ClothA04.getChild("ClothA05")
        this.ClothA01 = this.ArmLeft01.getChild("ClothA01")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.HairCBase = this.Hair.getChild("HairCBase")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.HairCBaseB = this.Hair.getChild("HairCBaseB")
        this.EquipHeadBase = this.Head.getChild("EquipHeadBase")
        this.EquipHead00 = this.EquipHeadBase.getChild("EquipHead00")
        this.EquipHead01 = this.EquipHeadBase.getChild("EquipHead01")
        this.EquipHead02 = this.EquipHead01.getChild("EquipHead02")
        this.EquipHead03 = this.EquipHead02.getChild("EquipHead03")
        this.EquipHead01_1 = this.EquipHeadBase.getChild("EquipHead01_1")
        this.EquipHead02_1 = this.EquipHead01_1.getChild("EquipHead02_1")
        this.EquipHead03_1 = this.EquipHead02_1.getChild("EquipHead03_1")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.ClothB01 = this.BoobR.getChild("ClothB01")
        this.Cloth03b = this.BoobR.getChild("Cloth03b")
        this.Cloth03a1 = this.BodyMain.getChild("Cloth03a1")
        this.Cloth03a2 = this.BodyMain.getChild("Cloth03a2")
        this.EquipBase = this.BodyMain.getChild("EquipBase")
        this.EquipD01a = this.EquipBase.getChild("EquipD01a")
        this.EquipD02d = this.EquipD01a.getChild("EquipD02d")
        this.EquipD03b1 = this.EquipD01a.getChild("EquipD03b1")
        this.EquipD03ab_1 = this.EquipD03b1.getChild("EquipD03ab_1")
        this.EquipD03aa_1 = this.EquipD03b1.getChild("EquipD03aa_1")
        this.EquipD03a2_1 = this.EquipD03b1.getChild("EquipD03a2_1")
        this.EquipD03a3_1 = this.EquipD03a2_1.getChild("EquipD03a3_1")
        this.EquipD03a4_1 = this.EquipD03a3_1.getChild("EquipD03a4_1")
        this.EquipB05_1 = this.EquipD03a4_1.getChild("EquipB05_1")
        this.EquipCL1Base01R2 = this.EquipB05_1.getChild("EquipCL1Base01R2")
        this.EquipCL1Base01a_1 = this.EquipCL1Base01R2.getChild("EquipCL1Base01a_1")
        this.EquipCL1Base02_1 = this.EquipCL1Base01R2.getChild("EquipCL1Base02_1")
        this.EquipCL1a1_3 = this.EquipCL1Base01R2.getChild("EquipCL1a1_3")
        this.EquipCL1a2_3 = this.EquipCL1a1_3.getChild("EquipCL1a2_3")
        this.EquipCL1a1_2 = this.EquipCL1Base01R2.getChild("EquipCL1a1_2")
        this.EquipCL1a2_2 = this.EquipCL1a1_2.getChild("EquipCL1a2_2")
        this.EquipD03c1 = this.EquipD01a.getChild("EquipD03c1")
        this.EquipD03c1b = this.EquipD03c1.getChild("EquipD03c1b")
        this.EquipD03c1a = this.EquipD03c1.getChild("EquipD03c1a")
        this.EquipD03c2 = this.EquipD03c1.getChild("EquipD03c2")
        this.EquipD03c2a = this.EquipD03c2.getChild("EquipD03c2a")
        this.EquipD03c3 = this.EquipD03c2.getChild("EquipD03c3")
        this.EquipB05_2 = this.EquipD03c3.getChild("EquipB05_2")
        this.EquipCL1Base01L1 = this.EquipB05_2.getChild("EquipCL1Base01L1")
        this.EquipCL1Base02_2 = this.EquipCL1Base01L1.getChild("EquipCL1Base02_2")
        this.EquipCL1Base01b = this.EquipCL1Base01L1.getChild("EquipCL1Base01b")
        this.EquipCL1a1_5 = this.EquipCL1Base01L1.getChild("EquipCL1a1_5")
        this.EquipCL1a2_5 = this.EquipCL1a1_5.getChild("EquipCL1a2_5")
        this.EquipCL1a1_4 = this.EquipCL1Base01L1.getChild("EquipCL1a1_4")
        this.EquipCL1a2_4 = this.EquipCL1a1_4.getChild("EquipCL1a2_4")
        this.EquipCL1Base01a_2 = this.EquipCL1Base01L1.getChild("EquipCL1Base01a_2")
        this.EquipD03c3a = this.EquipD03c3.getChild("EquipD03c3a")
        this.EquipD02b = this.EquipD01a.getChild("EquipD02b")
        this.EquipD01aa = this.EquipD01a.getChild("EquipD01aa")
        this.EquipD02a = this.EquipD01a.getChild("EquipD02a")
        this.EquipD01b = this.EquipD01a.getChild("EquipD01b")
        this.EquipD01ba = this.EquipD01b.getChild("EquipD01ba")
        this.EquipD01bb = this.EquipD01b.getChild("EquipD01bb")
        this.EquipD03d1 = this.EquipD01a.getChild("EquipD03d1")
        this.EquipD03c2_1 = this.EquipD03d1.getChild("EquipD03c2_1")
        this.EquipD03c3_1 = this.EquipD03c2_1.getChild("EquipD03c3_1")
        this.EquipD03c3a_1 = this.EquipD03c3_1.getChild("EquipD03c3a_1")
        this.EquipB05_3 = this.EquipD03c3_1.getChild("EquipB05_3")
        this.EquipCL1Base01R1 = this.EquipB05_3.getChild("EquipCL1Base01R1")
        this.EquipCL1Base02_3 = this.EquipCL1Base01R1.getChild("EquipCL1Base02_3")
        this.EquipCL1a1_6 = this.EquipCL1Base01R1.getChild("EquipCL1a1_6")
        this.EquipCL1a2_6 = this.EquipCL1a1_6.getChild("EquipCL1a2_6")
        this.EquipCL1Base01a_3 = this.EquipCL1Base01R1.getChild("EquipCL1Base01a_3")
        this.EquipCL1a1_7 = this.EquipCL1Base01R1.getChild("EquipCL1a1_7")
        this.EquipCL1a2_7 = this.EquipCL1a1_7.getChild("EquipCL1a2_7")
        this.EquipCL1Base01b_1 = this.EquipCL1Base01R1.getChild("EquipCL1Base01b_1")
        this.EquipD03c2a_1 = this.EquipD03c2_1.getChild("EquipD03c2a_1")
        this.EquipD03c1a_1 = this.EquipD03d1.getChild("EquipD03c1a_1")
        this.EquipD03c1b_1 = this.EquipD03d1.getChild("EquipD03c1b_1")
        this.EquipD03a1 = this.EquipD01a.getChild("EquipD03a1")
        this.EquipD03a2 = this.EquipD03a1.getChild("EquipD03a2")
        this.EquipD03a3 = this.EquipD03a2.getChild("EquipD03a3")
        this.EquipD03a4 = this.EquipD03a3.getChild("EquipD03a4")
        this.EquipB05 = this.EquipD03a4.getChild("EquipB05")
        this.EquipCL1Base01L2 = this.EquipB05.getChild("EquipCL1Base01L2")
        this.EquipCL1a1 = this.EquipCL1Base01L2.getChild("EquipCL1a1")
        this.EquipCL1a2 = this.EquipCL1a1.getChild("EquipCL1a2")
        this.EquipCL1a1_1 = this.EquipCL1Base01L2.getChild("EquipCL1a1_1")
        this.EquipCL1a2_1 = this.EquipCL1a1_1.getChild("EquipCL1a2_1")
        this.EquipCL1Base01a = this.EquipCL1Base01L2.getChild("EquipCL1Base01a")
        this.EquipCL1Base02 = this.EquipCL1Base01L2.getChild("EquipCL1Base02")
        this.EquipD03ab = this.EquipD03a1.getChild("EquipD03ab")
        this.EquipD03aa = this.EquipD03a1.getChild("EquipD03aa")
        this.EquipD02c = this.EquipD01a.getChild("EquipD02c")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.Cloth03b_1 = this.BoobL.getChild("Cloth03b_1")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ClothA01_1 = this.ArmRight01.getChild("ClothA01_1")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.ClothA02a = this.ArmRight02.getChild("ClothA02a")
        this.ClothA03a = this.ClothA02a.getChild("ClothA03a")
        this.ClothA04a = this.ClothA03a.getChild("ClothA04a")
        this.ClothA05a = this.ClothA04a.getChild("ClothA05a")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        initFaceParts(this.GlowHead)
        this.buttDefaultY = this.Butt.y
        this.buttDefaultZ = this.Butt.z
        this.skirt01DefaultY = this.Skirt01.y
        this.skirt01DefaultZ = this.Skirt01.z
        this.clothA03DefaultX = this.ClothA03.x
        this.clothA03DefaultY = this.ClothA03.y
        this.clothA03DefaultZ = this.ClothA03.z
        this.clothA03aDefaultX = this.ClothA03a.x
        this.clothA03aDefaultY = this.ClothA03a.y
        this.clothA03aDefaultZ = this.ClothA03a.z
        this.clothA04DefaultY = this.ClothA04.y
        this.clothA04DefaultZ = this.ClothA04.z
        this.clothA04aDefaultY = this.ClothA04a.y
        this.clothA04aDefaultZ = this.ClothA04a.z
        this.clothA05DefaultY = this.ClothA05.y
        this.clothA05DefaultZ = this.ClothA05.z
        this.clothA05aDefaultY = this.ClothA05a.y
        this.clothA05aDefaultZ = this.ClothA05a.z
        this.armLeft02DefaultX = this.ArmLeft02.x
        this.armLeft02DefaultZ = this.ArmLeft02.z
        this.armRight02DefaultX = this.ArmRight02.x
        this.armRight02DefaultZ = this.ArmRight02.z
        this.legLeft01DefaultX = this.LegLeft01.x
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
    }

    override fun setupAnim(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        resetOffsets()

        applyFaceAndMouth(entity)
        setFlushVisible(entity != null && (entity.emotionPrimary == EntityShipBase.EMOTION_SHY || entity.emotionPrimary == EntityShipBase.EMOTION_HAPPY))
        applyEquipVisibility(entity)

        if (isDeadPose(entity)) {
            applyDeadPose(entity)
            syncGlowParts()
            return
        }

        applyBasePose(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch)
        applySpecialPoseAdjustments(entity, limbSwing, limbSwingAmount, ageInTicks)
        syncGlowParts()
    }

    private fun resetOffsets() {
        this.isDeadPose = false
        this.isSittingPose = false
        this.poseTranslateY = 0.0f

        Butt.y = buttDefaultY
        Butt.z = buttDefaultZ
        Skirt01.y = skirt01DefaultY
        Skirt01.z = skirt01DefaultZ
        ClothA03.x = clothA03DefaultX
        ClothA03.y = clothA03DefaultY
        ClothA03.z = clothA03DefaultZ
        ClothA03a.x = clothA03aDefaultX
        ClothA03a.y = clothA03aDefaultY
        ClothA03a.z = clothA03aDefaultZ
        ClothA04.y = clothA04DefaultY
        ClothA04.z = clothA04DefaultZ
        ClothA04a.y = clothA04aDefaultY
        ClothA04a.z = clothA04aDefaultZ
        ClothA05.y = clothA05DefaultY
        ClothA05.z = clothA05DefaultZ
        ClothA05a.y = clothA05aDefaultY
        ClothA05a.z = clothA05aDefaultZ
        ArmLeft02.x = armLeft02DefaultX
        ArmLeft02.z = armLeft02DefaultZ
        ArmRight02.x = armRight02DefaultX
        ArmRight02.z = armRight02DefaultZ
        LegLeft01.x = legLeft01DefaultX
        LegLeft01.y = legLeft01DefaultY
        LegLeft01.z = legLeft01DefaultZ
        LegLeft02.x = legLeft02DefaultX
        LegLeft02.y = legLeft02DefaultY
        LegLeft02.z = legLeft02DefaultZ
        LegRight01.y = legRight01DefaultY
        LegRight01.z = legRight01DefaultZ
        LegRight02.x = legRight02DefaultX
        LegRight02.y = legRight02DefaultY
        LegRight02.z = legRight02DefaultZ
    }

    private fun isDeadPose(entity: T?): Boolean {
        return entity != null && entity.isInDeadPose
    }

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) return
        val showRigging = entity.getEquipFlag(EntityBBHaruna.EQUIP_RIGGING)
        val showHat = entity.getEquipFlag(EntityBBHaruna.EQUIP_ANCHOR)

        EquipBase.visible = showRigging
        EquipHeadBase.visible = showHat
    }

    private fun applyDeadPose(entity: T?) {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        Head.xRot = 0.0f
        Ahoke.yRot = 0.65f
        BoobL.xRot = -0.8f
        BoobR.xRot = -0.8f
        ClothB01.xRot = 0.96f
        BodyMain.xRot = -1.75f
        BodyMain.yRot = 0.0f
        BodyMain.zRot = 0.0f
        Butt.xRot = 0.35f
        Skirt01.xRot = -0.087f
        Skirt02.xRot = -0.087f
        SkirtB01.xRot = 0.087f

        Hair01.xRot = 0.26f
        Hair02.xRot = -0.17f

        Cloth02a1.xRot = -0.33f
        Cloth02b1.xRot = -0.33f
        Cloth02c1.xRot = 0.6283f
        Cloth02c1_1.xRot = 0.6283f
        Cloth02c2.xRot = -0.7854f
        Cloth02c2_1.xRot = -0.7854f
        Cloth02c3.xRot = -0.1396f
        Cloth02c3_1.xRot = -0.1396f

        ArmLeft01.xRot = -0.087f
        ArmLeft01.yRot = 0.873f
        ArmLeft01.zRot = -0.17f
        ArmLeft02.xRot = -2.1f
        ArmLeft02.zRot = 0.0f
        ArmLeft02.z = armLeft02DefaultZ + (-0.33f * OFFSET_SCALE)

        ArmRight01.xRot = -0.087f
        ArmRight01.yRot = -0.873f
        ArmRight01.zRot = 0.17f
        ArmRight02.xRot = -2.1f
        ArmRight02.zRot = 0.0f
        ArmRight02.z = armRight02DefaultZ + (-0.33f * OFFSET_SCALE)

        LegLeft01.xRot = -0.1f
        LegLeft01.yRot = 0.0f
        LegLeft01.zRot = 0.02f
        LegRight01.xRot = -0.1f
        LegRight01.yRot = 0.0f
        LegRight01.zRot = -0.02f

        EquipBase.visible = false
    }

    private fun applyBasePose(
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val angleX = Mth.cos(ageInTicks * 0.08f + limbSwing * 0.25f)
        val angleX1 = Mth.cos(ageInTicks * 0.1f + 0.35f + limbSwing * 0.5f)
        val angleX2 = Mth.cos(ageInTicks * 0.1f + 0.7f + limbSwing * 0.5f)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount

        Head.xRot = headPitch * (Math.PI.toFloat() / 180f)
        Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)

        Ahoke.yRot = angleX * 0.15f + 0.65f
        BoobL.xRot = angleX * 0.06f - 0.8f
        BoobR.xRot = angleX * 0.06f - 0.8f
        ClothB01.xRot = 0.96f - angleX * 0.08f

        BodyMain.xRot = -0.1047f
        BodyMain.yRot = 0.0f
        BodyMain.zRot = 0.0f
        Butt.xRot = 0.35f
        Skirt01.xRot = -0.087f
        Skirt02.xRot = -0.087f
        SkirtB01.xRot = 0.087f

        ClothA03.yRot = 0.0f
        ClothA03a.yRot = 0.0f
        Cloth02a1.xRot = -0.5585f
        Cloth02b1.xRot = -0.5585f
        Cloth02c1.xRot = 0.6283f
        Cloth02c1_1.xRot = 0.6283f
        Cloth02c2.xRot = -0.7854f
        Cloth02c2_1.xRot = -0.7854f
        Cloth02c3.xRot = -0.1396f + angleX1 * 0.06f
        Cloth02c3_1.xRot = -0.1396f + angleX1 * 0.06f
        Cloth02c4.xRot = -angleX2 * 0.06f
        Cloth02c4_1.xRot = -angleX2 * 0.06f

        ArmLeft01.xRot = angleAdd2 * 0.25f + 0.3f
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = angleX * 0.03f - 0.25f
        ArmLeft02.xRot = 0.0f
        ArmLeft02.zRot = 0.0f

        ArmRight01.xRot = angleAdd1 * 0.25f - 0.087f
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

        val headX = Head.xRot * -0.5f
        val headZ = Head.zRot * -0.5f
        Hair01.xRot = angleX * 0.03f + 0.26f + headX
        Hair01.zRot = headZ
        Hair02.xRot = -angleX1 * 0.04f - 0.17f + headX
        Hair02.zRot = headZ
        HairL01.xRot = angleX * 0.02f + headX - 0.19f
        HairL01.zRot = headZ - 0.087f
        HairL02.xRot = -angleX1 * 0.04f + headX + 0.17f
        HairL02.zRot = headZ + 0.087f
        HairR01.xRot = angleX * 0.02f + headX - 0.19f
        HairR01.zRot = headZ + 0.087f
        HairR02.xRot = -angleX1 * 0.04f + headX + 0.17f
        HairR02.zRot = headZ - 0.052f

        EquipCL1a1.xRot = Head.xRot * 0.8f - 0.21f
        EquipCL1a1_1.xRot = Head.xRot * 0.7f - 0.23f
        EquipCL1a1_2.xRot = Head.xRot * 0.85f - 0.2f
        EquipCL1a1_3.xRot = Head.xRot * 0.75f - 0.25f
        EquipCL1a1_4.xRot = Head.xRot * 0.8f - 0.2f
        EquipCL1a1_5.xRot = Head.xRot * 0.85f - 0.19f
        EquipCL1a1_6.xRot = Head.xRot * 0.75f - 0.21f
        EquipCL1a1_7.xRot = Head.xRot * 0.88f - 0.19f

        EquipD03c1.zRot = -0.35f + Head.xRot * 0.5f
        EquipD03c2.zRot = -0.26f + Head.xRot * 0.5f
        EquipD03c3.zRot = 0.61f - Head.xRot
        EquipD03d1.zRot = -EquipD03c1.zRot
        EquipD03c2_1.zRot = EquipD03c2.zRot
        EquipD03c3_1.zRot = EquipD03c3.zRot
        EquipD03a1.zRot = 0.52f + Head.xRot * 0.5f
        EquipD03b1.zRot = -EquipD03a1.zRot

        EquipCL1Base01L1.yRot = Head.yRot * 0.75f
        EquipCL1Base01L2.yRot = Head.yRot * 0.75f
        EquipCL1Base01R1.yRot = Head.yRot * 0.75f
        EquipCL1Base01R2.yRot = Head.yRot * 0.75f
    }

    private fun applySpecialPoseAdjustments(entity: T?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount
        var addk1 = angleAdd1 * 0.3f - 0.28f
        var addk2 = angleAdd2 * 0.3f - 0.21f
        var spcStand = true

        if (entity != null && (entity.isSprinting || limbSwingAmount > 0.9f)) {
            spcStand = false
            BodyMain.xRot = 0.2f
            Skirt01.xRot = -0.4f
            Skirt02.xRot = -0.1f
            SkirtB01.xRot = -0.13f
            Cloth02c1.xRot = 1.17f
            Cloth02c1_1.xRot = 1.17f
            Cloth02c2.xRot = -0.63f
            Cloth02c2_1.xRot = -0.63f
            Hair01.xRot += 0.2f
            Hair02.xRot += 0.2f

            ArmLeft01.xRot = 0.2f
            ArmLeft01.yRot = 0.5f
            ArmLeft01.zRot = 0.17f
            ArmLeft02.xRot = -1.9f
            ArmLeft02.z = armLeft02DefaultZ + (-0.33f * OFFSET_SCALE)
            ArmRight01.xRot = 0.2f
            ArmRight01.yRot = -0.5f
            ArmRight01.zRot = -0.17f
            ArmRight02.xRot = -1.9f
            ArmRight02.z = armRight02DefaultZ + (-0.33f * OFFSET_SCALE)

            ClothA03.yRot = 0.4f
            ClothA03a.yRot = -0.4f
            addk1 = angleAdd1 * 0.35f - 0.48f
            addk2 = angleAdd2 * 0.35f - 0.41f
        }

        if (entity != null && entity.isCrouching()) {
            spcStand = false
            this.poseTranslateY = SNEAK_TRANSLATE_Y
            Head.xRot -= 0.6283f
            BodyMain.xRot = 0.8727f
            Skirt01.xRot = -0.34f
            Skirt01.y = skirt01DefaultY + (-0.2f * OFFSET_SCALE)
            Skirt01.z = skirt01DefaultZ + (0.03f * OFFSET_SCALE)
            Skirt02.xRot = -0.27f
            Cloth02a1.xRot = -1.23f
            Cloth02b1.xRot = -1.23f
            Cloth02c2.xRot -= 0.35f
            Cloth02c2_1.xRot -= 0.35f
            ArmLeft01.xRot = -0.35f
            ArmLeft01.zRot = 0.2618f
            ArmRight01.xRot = -0.35f
            ArmRight01.zRot = -0.2618f
            addk1 -= 0.94f
            addk2 -= 0.94f
            LegLeft01.zRot = 0.2f
            LegRight01.zRot = -0.2f
            Hair01.xRot = Hair01.xRot * 0.5f + 0.4f
            Hair02.xRot = Hair02.xRot * 0.75f + 0.25f
        }

        if (entity != null && ((entity.isPassenger() && entity.getVehicle() !is EntityMountBase) || entity.isInSittingPose())) {
            spcStand = false
            this.isSittingPose = true
            val sitTick = entity.tickCount % 512
            val isScorn = entity.getStateEmotion(1) == 4

            if (sitTick > 256) {
                if (isScorn) {
                    this.poseTranslateY = SITTING_ALT_TRANSLATE_Y
                    Head.xRot = -0.35f
                    Head.yRot = 0.0f
                    BodyMain.xRot = -1.6f
                    ArmLeft01.xRot = 3.0f
                    ArmLeft01.yRot = 0.0f
                    ArmLeft01.zRot = 0.7f
                    ArmRight01.xRot = 3.0f
                    ArmRight01.yRot = 0.0f
                    ArmRight01.zRot = -0.7f
                    ArmLeft02.xRot = 0.0f
                    ArmRight02.xRot = 0.0f
                    addk1 = -0.2f
                    addk2 = -0.2f
                    LegLeft01.yRot = 0.0f
                    LegLeft01.zRot = -0.1f
                    LegLeft02.xRot = 0.0f
                    LegRight01.yRot = 0.0f
                    LegRight01.zRot = 0.1f
                    LegRight02.xRot = 0.0f
                    EquipBase.visible = false
                } else {
                    this.poseTranslateY = SITTING_TRANSLATE_Y
                    Head.xRot += 0.1f
                    BodyMain.xRot = -0.1f
                    Butt.xRot = -0.4f
                    Butt.z = buttDefaultZ + (0.19f * OFFSET_SCALE)
                    Skirt01.xRot = -0.35f
                    Skirt02.xRot = -0.19f
                    Cloth02a1.xRot = 0.2f
                    Cloth02b1.xRot = 0.2f
                    Cloth02c1.xRot = 1.5f
                    Cloth02c2.xRot = 0.35f
                    Cloth02c3.xRot = 0.05f
                    Cloth02c1_1.xRot = 1.5f
                    Cloth02c2_1.xRot = 0.35f
                    Cloth02c3_1.xRot = 0.05f
                    ArmLeft01.xRot = -1.18f
                    ArmLeft01.yRot = 0.27f
                    ArmLeft01.zRot = -0.1f
                    ArmLeft02.zRot = 0.92f
                    ArmRight01.xRot = -1.18f
                    ArmRight01.yRot = -0.27f
                    ArmRight01.zRot = 0.1f
                    ArmRight02.zRot = -1.32f
                    addk1 = -2.57f
                    addk2 = -2.57f
                    LegLeft01.y = legLeft01DefaultY + (0.25f * OFFSET_SCALE)
                    LegLeft01.z = legLeft01DefaultZ + (-0.2f * OFFSET_SCALE)
                    LegLeft01.yRot = 0.11f
                    LegLeft01.zRot = -0.12f
                    LegLeft02.xRot = 2.75f
                    LegLeft02.z = legLeft02DefaultZ + (0.37f * OFFSET_SCALE)
                    LegRight01.y = legRight01DefaultY + (0.25f * OFFSET_SCALE)
                    LegRight01.z = legRight01DefaultZ + (-0.2f * OFFSET_SCALE)
                    LegRight01.yRot = -0.11f
                    LegRight01.zRot = 0.12f
                    LegRight02.xRot = 2.75f
                    LegRight02.z = legRight02DefaultZ + (0.37f * OFFSET_SCALE)
                }
            } else {
                this.poseTranslateY = SITTING_IDLE_TRANSLATE_Y
                Head.xRot -= 0.1f
                BodyMain.xRot = 0.0f
                Butt.xRot = -0.2f
                Skirt01.xRot = -0.15f
                Skirt02.xRot = 0.1f
                SkirtB01.xRot = -0.1f
                Cloth02a1.xRot = -0.6f
                Cloth02b1.xRot = -0.6f
                Cloth02c1.xRot = 1.27f
                Cloth02c2.xRot = -0.8f
                Cloth02c3.xRot = 1.05f
                Cloth02c4.xRot = 0.35f
                Cloth02c1_1.xRot = 1.27f
                Cloth02c2_1.xRot = -0.8f
                Cloth02c3_1.xRot = 1.05f
                Cloth02c4_1.xRot = 0.35f
                ArmLeft01.xRot = -0.45f
                ArmLeft01.zRot = 0.32f
                ArmRight01.xRot = -0.45f
                ArmRight01.zRot = -0.32f
                addk1 = -0.65f
                addk2 = -0.65f
                LegLeft01.yRot = 0.1f
                LegLeft02.xRot = 2.45f
                LegLeft02.z = legLeft02DefaultZ + (0.375f * OFFSET_SCALE)
                LegRight01.yRot = -0.1f
                LegRight02.xRot = 2.45f
                LegRight02.z = legRight02DefaultZ + (0.375f * OFFSET_SCALE)
            }
        }

        if (entity != null && entity.attackTick > 20) {
            spcStand = false
            setFace(3)
            setMouth(5)
            BodyMain.xRot = -0.17f
            ArmLeft01.xRot = 0.2f
            ArmLeft01.yRot = 0.5f
            ArmLeft01.zRot = 0.1f
            ArmLeft02.xRot = -1.9f
            ArmLeft02.z = armLeft02DefaultZ + (-0.33f * OFFSET_SCALE)
            ArmRight01.xRot = 0.2f
            ArmRight01.yRot = -0.5f
            ArmRight01.zRot = -0.1f
            ArmRight02.xRot = -1.9f
            ArmRight02.z = armRight02DefaultZ + (-0.33f * OFFSET_SCALE)
            addk1 += 0.14f
            addk2 += 0.07f
            LegLeft01.zRot = -0.17f
            LegRight01.zRot = 0.17f
        }

        if (spcStand && (entity == null || (entity.tickCount % 512) > 256)) {
            BodyMain.xRot = -0.17f
            ArmLeft01.xRot = 0.15f
            ArmLeft01.yRot = 0.55f
            ArmLeft01.zRot = 0.15f
            ArmLeft02.xRot = -2.2f
            ArmLeft02.z = armLeft02DefaultZ + (-0.33f * OFFSET_SCALE)
            ArmRight01.xRot = 0.15f
            ArmRight01.yRot = -0.55f
            ArmRight01.zRot = -0.15f
            ArmRight02.xRot = -2.2f
            ArmRight02.z = armRight02DefaultZ + (-0.33f * OFFSET_SCALE)
            addk1 += 0.14f
            addk2 += 0.07f
            if (entity != null && hasLegacyState(entity, 7, 4)) {
                setFace(3)
                setMouth(5)
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

        val handL = BodyMain.xRot + ArmLeft01.xRot + ArmLeft02.xRot
        val handR = BodyMain.xRot + ArmRight01.xRot + ArmRight02.xRot
        val handLc = Mth.cos(handL)
        val handLs = Mth.sin(handL)
        val handRc = Mth.cos(handR)
        val handRs = Mth.sin(handR)

        ClothA03.y = clothA03DefaultY + (handLc * 0.1f) * OFFSET_SCALE
        ClothA04.y = clothA04DefaultY + (handLc * 0.2f) * OFFSET_SCALE
        ClothA05.y = clothA05DefaultY + (handLc * 0.25f) * OFFSET_SCALE
        ClothA03.z = clothA03DefaultZ + (handLs * -0.32f) * OFFSET_SCALE
        ClothA04.z = clothA04DefaultZ + (handLs * -0.32f) * OFFSET_SCALE
        ClothA05.z = clothA05DefaultZ + (handLs * -0.32f) * OFFSET_SCALE

        ClothA03a.y = clothA03aDefaultY + (handRc * 0.1f) * OFFSET_SCALE
        ClothA04a.y = clothA04aDefaultY + (handRc * 0.2f) * OFFSET_SCALE
        ClothA05a.y = clothA05aDefaultY + (handRc * 0.25f) * OFFSET_SCALE
        ClothA03a.z = clothA03aDefaultZ + (handRs * -0.32f) * OFFSET_SCALE
        ClothA04a.z = clothA04aDefaultZ + (handRs * -0.32f) * OFFSET_SCALE
        ClothA05a.z = clothA05aDefaultZ + (handRs * -0.32f) * OFFSET_SCALE

        LegLeft01.xRot = addk1
        LegRight01.xRot = addk2
    }

    private fun syncGlowParts() {
        if (this.GlowBodyMain != null) {
            this.GlowBodyMain.copyFrom(this.BodyMain)
            this.GlowNeck.copyFrom(this.Neck)
            this.GlowHead.copyFrom(this.Head)
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "bb_haruna"), "main")

        private val SITTING_TRANSLATE_Y = sittingY("ModelBBHaruna")
        private val SITTING_ALT_TRANSLATE_Y = sittingAltY("ModelBBHaruna")
        private val SITTING_IDLE_TRANSLATE_Y = 0.39f * 3
        private val SNEAK_TRANSLATE_Y = sneakY("ModelBBHaruna")
        private val DEAD_TRANSLATE_Y = deadY("ModelBBHaruna")
        private const val OFFSET_SCALE = 16.0f

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 104).addBox(-6.5f, -11f, -4f, 13f, 17f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 88).addBox(-7.5f, 0f, -5.7f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.3f, 0.3491f, 0f, 0f)
            )

            val Skirt01 = Butt.addOrReplaceChild(
                "Skirt01",
                CubeListBuilder.create().texOffs(128, 0).addBox(-8.5f, 0f, -8.5f, 17f, 5f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3.5f, 1.5f, -0.0873f, 0f, 0f)
            )

            val Skirt02 = Skirt01.addOrReplaceChild(
                "Skirt02",
                CubeListBuilder.create().texOffs(128, 17).addBox(-9.5f, 0f, -6.5f, 19f, 5f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3.5f, -2.7f, -0.0873f, 0f, 0f)
            )

            val Cloth02b1 = Butt.addOrReplaceChild(
                "Cloth02b1",
                CubeListBuilder.create().texOffs(59, 0).addBox(-1.5f, 0f, 0f, 3f, 5f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, 2.3f, -6.8f, -0.4363f, 0f, 0.0698f)
            )

            val Cloth02b2 = Cloth02b1.addOrReplaceChild(
                "Cloth02b2",
                CubeListBuilder.create().texOffs(59, 0).addBox(-1.5f, 0f, 0f, 3f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4.9f, 0f, 0.1745f, 0f, -0.0524f)
            )

            val Cloth02b3 = Cloth02b2.addOrReplaceChild(
                "Cloth02b3",
                CubeListBuilder.create().texOffs(59, 0).addBox(-1.5f, 0f, 0f, 3f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.9f, 0f, 0f, 0f, -0.0524f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 68).addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.8f, 5.5f, -2.6f, -0.2967f, 0f, 0.0873f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 47).addBox(-6f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(3f, 14f, -3f)
            )

            val SkirtB01 = Butt.addOrReplaceChild(
                "SkirtB01",
                CubeListBuilder.create().texOffs(128, 36).addBox(-8f, 0f, -4.5f, 16f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, -1.9f, 0.0873f, 0f, 0f)
            )

            val Cloth02c1 = SkirtB01.addOrReplaceChild(
                "Cloth02c1",
                CubeListBuilder.create().texOffs(58, 7).addBox(-3.5f, 0f, 0f, 7f, 4f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.6f, 1.9f, 4.4f, 0.6283f, 0f, -0.0873f)
            )

            val Cloth02c2 = Cloth02c1.addOrReplaceChild(
                "Cloth02c2",
                CubeListBuilder.create().texOffs(58, 7).addBox(-3.5f, 0f, 0f, 7f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 0f, -0.7854f, 0f, 0f)
            )

            val Cloth02c3 = Cloth02c2.addOrReplaceChild(
                "Cloth02c3",
                CubeListBuilder.create().texOffs(58, 7).addBox(-3.5f, 0f, 0f, 7f, 8f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 6.9f, 0f)
            )

            val Cloth02c4 = Cloth02c3.addOrReplaceChild(
                "Cloth02c4",
                CubeListBuilder.create().texOffs(58, 7).addBox(-3.5f, 0f, 0f, 7f, 8f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 7.9f, 0f)
            )

            val Cloth02c1_1 = SkirtB01.addOrReplaceChild(
                "Cloth02c1_1",
                CubeListBuilder.create().texOffs(58, 7).addBox(-3.5f, 0f, 0f, 7f, 4f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.6f, 1.9f, 4.4f, 0.6283f, 0f, 0.0873f)
            )

            val Cloth02c2_1 = Cloth02c1_1.addOrReplaceChild(
                "Cloth02c2_1",
                CubeListBuilder.create().texOffs(58, 7).addBox(-3.5f, 0f, 0f, 7f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 0f, -0.7854f, 0f, 0f)
            )

            val Cloth02c3_1 = Cloth02c2_1.addOrReplaceChild(
                "Cloth02c3_1",
                CubeListBuilder.create().texOffs(58, 7).addBox(-3.5f, 0f, 0f, 7f, 8f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 6.9f, 0f)
            )

            val Cloth02c4_1 = Cloth02c3_1.addOrReplaceChild(
                "Cloth02c4_1",
                CubeListBuilder.create().texOffs(58, 7).addBox(-3.5f, 0f, 0f, 7f, 8f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 7.9f, 0f)
            )

            val Cloth01a = SkirtB01.addOrReplaceChild(
                "Cloth01a",
                CubeListBuilder.create().texOffs(81, 0).addBox(-1f, -2.5f, -1f, 2f, 3f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.3f, -5f, -0.2618f, 0f, 0f)
            )

            val Cloth01b2 = Cloth01a.addOrReplaceChild(
                "Cloth01b2",
                CubeListBuilder.create().texOffs(65, 0).addBox(0f, -3f, -1f, 6f, 3f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, 0.3f, 0.0873f, 0.1745f, 0.3491f)
            )

            val Cloth01c = Cloth01a.addOrReplaceChild(
                "Cloth01c",
                CubeListBuilder.create().texOffs(73, 5).addBox(-1.5f, 0f, 0f, 3f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, -0.4f, -0.7f, -0.2618f, 0.1396f, 0.1745f)
            )

            val Cloth01c2 = Cloth01a.addOrReplaceChild(
                "Cloth01c2",
                CubeListBuilder.create().texOffs(73, 5).mirror().addBox(-1.5f, 0f, 0f, 3f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -0.4f, -0.7f, -0.2618f, -0.1396f, -0.1745f)
            )

            val Cloth01b = Cloth01a.addOrReplaceChild(
                "Cloth01b",
                CubeListBuilder.create().texOffs(65, 0).addBox(-6f, -3f, -1f, 6f, 3f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, 0.3f, 0.0873f, -0.1745f, -0.3491f)
            )

            val Cloth02a1 = Butt.addOrReplaceChild(
                "Cloth02a1",
                CubeListBuilder.create().texOffs(59, 0).addBox(-1.5f, 0f, 0f, 3f, 5f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, 2.3f, -6.8f, -0.4363f, 0f, -0.0698f)
            )

            val Cloth02a2 = Cloth02a1.addOrReplaceChild(
                "Cloth02a2",
                CubeListBuilder.create().texOffs(59, 0).addBox(-1.5f, 0f, 0f, 3f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4.9f, 0f, 0.2443f, 0f, 0.0524f)
            )

            val Cloth02a3 = Cloth02a2.addOrReplaceChild(
                "Cloth02a3",
                CubeListBuilder.create().texOffs(59, 0).addBox(-1.5f, 0f, 0f, 3f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.9f, 0f, 0f, 0f, 0.0524f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 68).mirror().addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.8f, 5.5f, -2.6f, -0.192f, 0f, -0.0873f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 47).mirror().addBox(0f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3f, 14f, -3f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(24, 71).addBox(-2f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.3f, -0.7f, 0.2731f, 0f, -0.3142f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(24, 54).addBox(-5f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 11f, 2.5f)
            )

            val ClothA02 = ArmLeft02.addOrReplaceChild(
                "ClothA02",
                CubeListBuilder.create().texOffs(128, 49).addBox(-3f, 0f, -3f, 6f, 9f, 6f, CubeDeformation(0f)),
                PartPose.offset(-2.5f, -0.1f, -2.5f)
            )

            val ClothA03 = ClothA02.addOrReplaceChild(
                "ClothA03",
                CubeListBuilder.create().texOffs(128, 65).addBox(-2.5f, 0f, 0f, 5f, 9f, 6f, CubeDeformation(0f)),
                PartPose.offset(0.1f, 1.9f, -2.2f)
            )

            val ClothA04 = ClothA03.addOrReplaceChild(
                "ClothA04",
                CubeListBuilder.create().texOffs(128, 81).addBox(-2f, 0f, 0f, 4f, 8f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.9f, 0.8f)
            )

            val ClothA05 = ClothA04.addOrReplaceChild(
                "ClothA05",
                CubeListBuilder.create().texOffs(128, 96).addBox(-1.5f, 0f, 0f, 3f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 1.9f, 0.8f)
            )

            val ClothA01 = ArmLeft01.addOrReplaceChild(
                "ClothA01",
                CubeListBuilder.create().texOffs(128, 109).addBox(-3f, 0f, -3f, 6f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offset(0.5f, 5.1f, 0f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(24, 63).addBox(-2.5f, -3f, -2.9f, 5f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -9.6f, 0.5f, 0.1047f, 0f, 0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.4f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, -0.7f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 77).addBox(-8f, -8f, -7.4f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0.1f)
            )

            val HairCBase = Hair.addOrReplaceChild(
                "HairCBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, 1f, -1.6f, 0f, 0f, -0.3142f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(90, 103).addBox(-0.5f, 0f, 0f, 1f, 8f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.5f, 1.5f, -4.5f, -0.192f, -0.1745f, -0.0873f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(90, 103).addBox(-0.5f, 0f, 0f, 1f, 8f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8f, 0f, 0.1745f, 0f, 0.0873f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(106, 31).addBox(0f, -5f, -10.5f, 0f, 11f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, -8f, -4.5f, 0f, 0.7854f, 0f)
            )

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(52, 56).addBox(-8.5f, 0f, 0f, 17f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -7f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(90, 103).mirror()
                    .addBox(-0.5f, 0f, 0f, 1f, 8f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.5f, 1.5f, -4.5f, -0.192f, 0.1745f, 0.0873f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(90, 103).mirror()
                    .addBox(-0.5f, 0f, 0f, 1f, 8f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 8f, 0f, 0.1745f, 0f, -0.0524f)
            )

            val HairCBaseB = Hair.addOrReplaceChild(
                "HairCBaseB",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, 1f, -1.6f, 0f, 0f, 0.3142f)
            )

            val EquipHeadBase = Head.addOrReplaceChild(
                "EquipHeadBase",
                CubeListBuilder.create().texOffs(40, 23).addBox(-8f, 0f, 7f, 16f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.8f, -7.6f)
            )

            val EquipHead00 = EquipHeadBase.addOrReplaceChild(
                "EquipHead00",
                CubeListBuilder.create().texOffs(45, 33).addBox(-8.5f, 0f, 0f, 17f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -4.1f, 5f, 0.1047f, 0f, 0f)
            )

            val EquipHead01 = EquipHeadBase.addOrReplaceChild(
                "EquipHead01",
                CubeListBuilder.create().texOffs(43, 105).addBox(0f, -0.7f, -0.3f, 2f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offset(6.7f, 0.2f, 5.7f)
            )

            val EquipHead02 = EquipHead01.addOrReplaceChild(
                "EquipHead02",
                CubeListBuilder.create().texOffs(45, 106).addBox(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, CubeDeformation(0f)),
                PartPose.offset(2.4f, 0.8f, 1.2f)
            )

            val EquipHead03 = EquipHead02.addOrReplaceChild(
                "EquipHead03",
                CubeListBuilder.create().texOffs(33, 105).addBox(0f, 0f, 0f, 5f, 3f, 0f, CubeDeformation(0f)),
                PartPose.offset(0.2f, -1.5f, 0f)
            )

            val EquipHead01_1 = EquipHeadBase.addOrReplaceChild(
                "EquipHead01_1",
                CubeListBuilder.create().texOffs(43, 105).addBox(-2f, -0.7f, -0.3f, 2f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offset(-6.7f, 0.2f, 5.7f)
            )

            val EquipHead02_1 = EquipHead01_1.addOrReplaceChild(
                "EquipHead02_1",
                CubeListBuilder.create().texOffs(43, 107).addBox(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, CubeDeformation(0f)),
                PartPose.offset(-2.4f, 0.8f, 1.2f)
            )

            val EquipHead03_1 = EquipHead02_1.addOrReplaceChild(
                "EquipHead03_1",
                CubeListBuilder.create().texOffs(33, 105).addBox(-5f, 0f, 0f, 5f, 3f, 0f, CubeDeformation(0f)),
                PartPose.offset(-0.2f, -1.5f, 0f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(46, 104).addBox(-7.5f, 0f, 0f, 15f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.8f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(153, 106).addBox(-7.5f, 0f, 0f, 15f, 13f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1f, 0.2618f, 0f, 0f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(202, 107).addBox(-8f, 0f, -5f, 16f, 13f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10.5f, 5.7f, -0.1745f, 0f, 0f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(0, 39).addBox(-3.5f, 0f, 0f, 7f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.5f, -8.2f, -3.8f, -0.8727f, -0.0873f, -0.0698f)
            )

            val ClothB01 = BoobR.addOrReplaceChild(
                "ClothB01",
                CubeListBuilder.create().texOffs(25, 37).addBox(-4.5f, 0f, 0f, 9f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.9f, 4.6f, 1.6f, 0.9599f, -0.0068f, 0.0948f)
            )

            val Cloth03b = BoobR.addOrReplaceChild(
                "Cloth03b",
                CubeListBuilder.create().texOffs(161, 80).mirror().addBox(-1f, 0f, 0f, 2f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.6f, -0.8f, -0.1f, 0f, 0f, 0.0873f)
            )

            val Cloth03a1 = BodyMain.addOrReplaceChild(
                "Cloth03a1",
                CubeListBuilder.create().texOffs(159, 55).addBox(-1f, 0f, 0f, 2f, 18f, 7f, CubeDeformation(0f)),
                PartPose.offset(4.1f, -11.1f, -4.1f)
            )

            val Cloth03a2 = BodyMain.addOrReplaceChild(
                "Cloth03a2",
                CubeListBuilder.create().texOffs(159, 55).mirror()
                    .addBox(-1f, 0f, 0f, 2f, 18f, 7f, CubeDeformation(0f)),
                PartPose.offset(-4.1f, -11.1f, -4.1f)
            )

            val EquipBase = BodyMain.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 7.5f, 5.5f)
            )

            val EquipD01a = EquipBase.addOrReplaceChild(
                "EquipD01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, 0f, 0f, 8f, 10f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, 0f, 0.0698f, 0f, 0f)
            )

            val EquipD02d = EquipD01a.addOrReplaceChild(
                "EquipD02d",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 6f, 12f, 4f, CubeDeformation(0f)),
                PartPose.offset(-5.9f, 5f, 5.9f)
            )

            val EquipD03b1 = EquipD01a.addOrReplaceChild(
                "EquipD03b1",
                CubeListBuilder.create().texOffs(107, 13).addBox(0.5f, -1f, -2.5f, 6f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offset(-5f, 5.8f, 7.5f)
            )

            val EquipD03ab_1 = EquipD03b1.addOrReplaceChild(
                "EquipD03ab_1",
                CubeListBuilder.create().texOffs(100, 30).addBox(0f, 0f, 0f, 9f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, -1.5f, 6.4f)
            )

            val EquipD03aa_1 = EquipD03b1.addOrReplaceChild(
                "EquipD03aa_1",
                CubeListBuilder.create().texOffs(100, 30).addBox(0f, 0f, 0f, 9f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, -1.5f, -3.4f)
            )

            val EquipD03a2_1 = EquipD03b1.addOrReplaceChild(
                "EquipD03a2_1",
                CubeListBuilder.create().texOffs(107, 13).addBox(0f, 0f, 0f, 6f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offset(6.4f, -1f, -2.5f)
            )

            val EquipD03a3_1 = EquipD03a2_1.addOrReplaceChild(
                "EquipD03a3_1",
                CubeListBuilder.create().texOffs(107, 13).addBox(0f, 0f, 0f, 6f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offset(5.9f, 0f, 0f)
            )

            val EquipD03a4_1 = EquipD03a3_1.addOrReplaceChild(
                "EquipD03a4_1",
                CubeListBuilder.create().texOffs(107, 13).addBox(0f, 0f, 0f, 6f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offset(5.9f, 0f, 0f)
            )

            val EquipB05_1 = EquipD03a4_1.addOrReplaceChild(
                "EquipB05_1",
                CubeListBuilder.create().texOffs(100, 30).addBox(-3.5f, 0f, -3.5f, 7f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offset(1.5f, -0.4f, 4.5f)
            )

            val EquipCL1Base01R2 = EquipB05_1.addOrReplaceChild(
                "EquipCL1Base01R2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -4f, -1.5f, 9f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offset(0.2f, 0.1f, 0f)
            )

            val EquipCL1Base01a_1 = EquipCL1Base01R2.addOrReplaceChild(
                "EquipCL1Base01a_1",
                CubeListBuilder.create().texOffs(0, 9).addBox(-2.5f, 0f, 0f, 5f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -5.4f, -1f, -0.0873f, 0f, 0f)
            )

            val EquipCL1Base02_1 = EquipCL1Base01R2.addOrReplaceChild(
                "EquipCL1Base02_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -4f, -2f, 9f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.3f, -2.8f, 0.1745f, 0f, 0f)
            )

            val EquipCL1a1_3 = EquipCL1Base01R2.addOrReplaceChild(
                "EquipCL1a1_3",
                CubeListBuilder.create().texOffs(19, 29).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, -2.3f, -2.5f, -0.1698f, 0f, 0f)
            )

            val EquipCL1a2_3 = EquipCL1a1_3.addOrReplaceChild(
                "EquipCL1a2_3",
                CubeListBuilder.create().texOffs(151, 67).addBox(-1f, 0f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.5708f, 0f, 0f)
            )

            val EquipCL1a1_2 = EquipCL1Base01R2.addOrReplaceChild(
                "EquipCL1a1_2",
                CubeListBuilder.create().texOffs(19, 29).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -2.3f, -2.5f, -0.182f, 0f, 0f)
            )

            val EquipCL1a2_2 = EquipCL1a1_2.addOrReplaceChild(
                "EquipCL1a2_2",
                CubeListBuilder.create().texOffs(151, 67).addBox(-1f, 0f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.5708f, 0f, 0f)
            )

            val EquipD03c1 = EquipD01a.addOrReplaceChild(
                "EquipD03c1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -1.5f, 0f, 8f, 1f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5f, 5.5f, 4f, 0f, 0f, -0.3491f)
            )

            val EquipD03c1b = EquipD03c1.addOrReplaceChild(
                "EquipD03c1b",
                CubeListBuilder.create().texOffs(100, 30).addBox(-0.5f, 0f, -0.5f, 2f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offset(7.2f, -1f, 1.5f)
            )

            val EquipD03c1a = EquipD03c1.addOrReplaceChild(
                "EquipD03c1a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 8f, 1f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.2f, 0f)
            )

            val EquipD03c2 = EquipD03c1.addOrReplaceChild(
                "EquipD03c2",
                CubeListBuilder.create().texOffs(100, 30).addBox(0f, -1.5f, -1.5f, 3f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, -0.2f, 1.5f, 0f, 0f, -0.2618f)
            )

            val EquipD03c2a = EquipD03c2.addOrReplaceChild(
                "EquipD03c2a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -1.5f, -4.5f, 8f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offset(1.5f, 0.1f, 0f)
            )

            val EquipD03c3 = EquipD03c2.addOrReplaceChild(
                "EquipD03c3",
                CubeListBuilder.create().texOffs(100, 30).addBox(0f, -1.5f, -1.5f, 4f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.9f, 0f, 0f, 0f, 0f, 0.6109f)
            )

            val EquipB05_2 = EquipD03c3.addOrReplaceChild(
                "EquipB05_2",
                CubeListBuilder.create().texOffs(100, 30).addBox(-3.5f, 0f, -3.5f, 7f, 4f, 7f, CubeDeformation(0f)),
                PartPose.offset(6.3f, -2f, 0f)
            )

            val EquipCL1Base01L1 = EquipB05_2.addOrReplaceChild(
                "EquipCL1Base01L1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -4f, -1.5f, 9f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offset(0.2f, 0.1f, 0f)
            )

            val EquipCL1Base02_2 = EquipCL1Base01L1.addOrReplaceChild(
                "EquipCL1Base02_2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -4f, -2f, 9f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.3f, -2.8f, 0.1745f, 0f, 0f)
            )

            val EquipCL1Base01b = EquipCL1Base01L1.addOrReplaceChild(
                "EquipCL1Base01b",
                CubeListBuilder.create().texOffs(109, 24).addBox(-5.5f, 0f, 0f, 11f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -5.6f, 2f)
            )

            val EquipCL1a1_5 = EquipCL1Base01L1.addOrReplaceChild(
                "EquipCL1a1_5",
                CubeListBuilder.create().texOffs(19, 29).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, -2.3f, -2.5f, -0.1698f, 0f, 0f)
            )

            val EquipCL1a2_5 = EquipCL1a1_5.addOrReplaceChild(
                "EquipCL1a2_5",
                CubeListBuilder.create().texOffs(151, 67).addBox(-1f, 0f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.5708f, 0f, 0f)
            )

            val EquipCL1a1_4 = EquipCL1Base01L1.addOrReplaceChild(
                "EquipCL1a1_4",
                CubeListBuilder.create().texOffs(19, 29).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -2.3f, -2.5f, -0.182f, 0f, 0f)
            )

            val EquipCL1a2_4 = EquipCL1a1_4.addOrReplaceChild(
                "EquipCL1a2_4",
                CubeListBuilder.create().texOffs(151, 67).addBox(-1f, 0f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.5708f, 0f, 0f)
            )

            val EquipCL1Base01a_2 = EquipCL1Base01L1.addOrReplaceChild(
                "EquipCL1Base01a_2",
                CubeListBuilder.create().texOffs(0, 9).addBox(-2.5f, 0f, 0f, 5f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -5.4f, -1f, -0.0873f, 0f, 0f)
            )

            val EquipD03c3a = EquipD03c3.addOrReplaceChild(
                "EquipD03c3a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 8f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offset(2.3f, -1.4f, -4.5f)
            )

            val EquipD02b = EquipD01a.addOrReplaceChild(
                "EquipD02b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, -0.6f, 7f, 14f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3.4f, 5f, 1.7f)
            )

            val EquipD01aa = EquipD01a.addOrReplaceChild(
                "EquipD01aa",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, -2.5f, 0f, 5f, 5f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9.5f, -4f, 0.2269f, 0f, 0f)
            )

            val EquipD02a = EquipD01a.addOrReplaceChild(
                "EquipD02a",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-3.5f, 0f, -0.6f, 7f, 14f, 5f, CubeDeformation(0f)),
                PartPose.offset(3.4f, 5f, 1.7f)
            )

            val EquipD01b = EquipD01a.addOrReplaceChild(
                "EquipD01b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, 0f, 0f, 8f, 9f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.4f, 5.9f)
            )

            val EquipD01ba = EquipD01b.addOrReplaceChild(
                "EquipD01ba",
                CubeListBuilder.create().texOffs(22, 22).addBox(-1.5f, 0f, 0f, 3f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 1f, 6f)
            )

            val EquipD01bb = EquipD01b.addOrReplaceChild(
                "EquipD01bb",
                CubeListBuilder.create().texOffs(22, 22).addBox(-1.5f, 0f, 0f, 3f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 4.9f, 6f)
            )

            val EquipD03d1 = EquipD01a.addOrReplaceChild(
                "EquipD03d1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -1.5f, 0f, 8f, 1f, 3f, CubeDeformation(0f)),
                PartPose.offset(-5f, 5.5f, 7f)
            )

            val EquipD03c2_1 = EquipD03d1.addOrReplaceChild(
                "EquipD03c2_1",
                CubeListBuilder.create().texOffs(100, 30).addBox(0f, -1.5f, -1.5f, 3f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, -0.2f, 1.5f, 0f, 0f, -0.2618f)
            )

            val EquipD03c3_1 = EquipD03c2_1.addOrReplaceChild(
                "EquipD03c3_1",
                CubeListBuilder.create().texOffs(100, 30).addBox(0f, -1.5f, -1.5f, 4f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.9f, 0f, 0f, 0f, 0f, 0.6109f)
            )

            val EquipD03c3a_1 = EquipD03c3_1.addOrReplaceChild(
                "EquipD03c3a_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 8f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offset(2.3f, -1.4f, -4.5f)
            )

            val EquipB05_3 = EquipD03c3_1.addOrReplaceChild(
                "EquipB05_3",
                CubeListBuilder.create().texOffs(100, 30).addBox(-3.5f, 0f, -3.5f, 7f, 4f, 7f, CubeDeformation(0f)),
                PartPose.offset(6.3f, -2f, 0f)
            )

            val EquipCL1Base01R1 = EquipB05_3.addOrReplaceChild(
                "EquipCL1Base01R1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -4f, -1.5f, 9f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offset(0.2f, 0.1f, 0f)
            )

            val EquipCL1Base02_3 = EquipCL1Base01R1.addOrReplaceChild(
                "EquipCL1Base02_3",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -4f, -2f, 9f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.3f, -2.8f, 0.1745f, 0f, 0f)
            )

            val EquipCL1a1_6 = EquipCL1Base01R1.addOrReplaceChild(
                "EquipCL1a1_6",
                CubeListBuilder.create().texOffs(19, 29).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -2.3f, -2.5f, -0.182f, 0f, 0f)
            )

            val EquipCL1a2_6 = EquipCL1a1_6.addOrReplaceChild(
                "EquipCL1a2_6",
                CubeListBuilder.create().texOffs(151, 67).addBox(-1f, 0f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.5708f, 0f, 0f)
            )

            val EquipCL1Base01a_3 = EquipCL1Base01R1.addOrReplaceChild(
                "EquipCL1Base01a_3",
                CubeListBuilder.create().texOffs(0, 9).addBox(-2.5f, 0f, 0f, 5f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -5.4f, -1f, -0.0873f, 0f, 0f)
            )

            val EquipCL1a1_7 = EquipCL1Base01R1.addOrReplaceChild(
                "EquipCL1a1_7",
                CubeListBuilder.create().texOffs(19, 29).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, -2.3f, -2.5f, -0.1698f, 0f, 0f)
            )

            val EquipCL1a2_7 = EquipCL1a1_7.addOrReplaceChild(
                "EquipCL1a2_7",
                CubeListBuilder.create().texOffs(151, 67).addBox(-1f, 0f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.5708f, 0f, 0f)
            )

            val EquipCL1Base01b_1 = EquipCL1Base01R1.addOrReplaceChild(
                "EquipCL1Base01b_1",
                CubeListBuilder.create().texOffs(109, 24).addBox(-5.5f, 0f, 0f, 11f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -5.6f, 2f)
            )

            val EquipD03c2a_1 = EquipD03c2_1.addOrReplaceChild(
                "EquipD03c2a_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -1.5f, -4.5f, 8f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offset(1.5f, 0.1f, 0f)
            )

            val EquipD03c1a_1 = EquipD03d1.addOrReplaceChild(
                "EquipD03c1a_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 8f, 1f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.2f, 0f)
            )

            val EquipD03c1b_1 = EquipD03d1.addOrReplaceChild(
                "EquipD03c1b_1",
                CubeListBuilder.create().texOffs(100, 30).addBox(-0.5f, 0f, -0.5f, 2f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offset(7.2f, -1f, 1.5f)
            )

            val EquipD03a1 = EquipD01a.addOrReplaceChild(
                "EquipD03a1",
                CubeListBuilder.create().texOffs(107, 13).addBox(0.5f, -1f, -2.5f, 6f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5f, 5.8f, 3.5f, 0f, 0f, 0.5236f)
            )

            val EquipD03a2 = EquipD03a1.addOrReplaceChild(
                "EquipD03a2",
                CubeListBuilder.create().texOffs(107, 13).addBox(0f, 0f, 0f, 6f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offset(6.4f, -1f, -2.5f)
            )

            val EquipD03a3 = EquipD03a2.addOrReplaceChild(
                "EquipD03a3",
                CubeListBuilder.create().texOffs(107, 13).addBox(0f, 0f, 0f, 6f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offset(5.9f, 0f, 0f)
            )

            val EquipD03a4 = EquipD03a3.addOrReplaceChild(
                "EquipD03a4",
                CubeListBuilder.create().texOffs(107, 13).addBox(0f, 0f, 0f, 6f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offset(5.9f, 0f, 0f)
            )

            val EquipB05 = EquipD03a4.addOrReplaceChild(
                "EquipB05",
                CubeListBuilder.create().texOffs(100, 30).addBox(-3.5f, 0f, -3.5f, 7f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offset(1.5f, -0.4f, 4.5f)
            )

            val EquipCL1Base01L2 = EquipB05.addOrReplaceChild(
                "EquipCL1Base01L2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -4f, -1.5f, 9f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offset(0.2f, 0.1f, 0f)
            )

            val EquipCL1a1 = EquipCL1Base01L2.addOrReplaceChild(
                "EquipCL1a1",
                CubeListBuilder.create().texOffs(19, 29).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -2.3f, -2.5f, -0.182f, 0f, 0f)
            )

            val EquipCL1a2 = EquipCL1a1.addOrReplaceChild(
                "EquipCL1a2",
                CubeListBuilder.create().texOffs(151, 67).addBox(-1f, 0f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.5708f, 0f, 0f)
            )

            val EquipCL1a1_1 = EquipCL1Base01L2.addOrReplaceChild(
                "EquipCL1a1_1",
                CubeListBuilder.create().texOffs(19, 29).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, -2.3f, -2.5f, -0.1698f, 0f, 0f)
            )

            val EquipCL1a2_1 = EquipCL1a1_1.addOrReplaceChild(
                "EquipCL1a2_1",
                CubeListBuilder.create().texOffs(151, 67).addBox(-1f, 0f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.5708f, 0f, 0f)
            )

            val EquipCL1Base01a = EquipCL1Base01L2.addOrReplaceChild(
                "EquipCL1Base01a",
                CubeListBuilder.create().texOffs(0, 9).addBox(-2.5f, 0f, 0f, 5f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -5.4f, -1f, -0.0873f, 0f, 0f)
            )

            val EquipCL1Base02 = EquipCL1Base01L2.addOrReplaceChild(
                "EquipCL1Base02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -4f, -2f, 9f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.3f, -2.8f, 0.1745f, 0f, 0f)
            )

            val EquipD03ab = EquipD03a1.addOrReplaceChild(
                "EquipD03ab",
                CubeListBuilder.create().texOffs(100, 30).addBox(0f, 0f, 0f, 9f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, -1.5f, 6.4f)
            )

            val EquipD03aa = EquipD03a1.addOrReplaceChild(
                "EquipD03aa",
                CubeListBuilder.create().texOffs(100, 30).addBox(0f, 0f, 0f, 9f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, -1.5f, -3.4f)
            )

            val EquipD02c = EquipD01a.addOrReplaceChild(
                "EquipD02c",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-3.5f, 0f, 0f, 6f, 12f, 4f, CubeDeformation(0f)),
                PartPose.offset(3.3f, 5f, 5.9f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(25, 44).addBox(-3.5f, 0f, 0f, 7f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.5f, -8.2f, -3.7f, -0.8727f, 0.0873f, 0.0698f)
            )

            val Cloth03b_1 = BoobL.addOrReplaceChild(
                "Cloth03b_1",
                CubeListBuilder.create().texOffs(161, 80).addBox(-1f, 0f, 0f, 2f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.6f, -0.8f, -0.1f, 0f, 0f, -0.0873f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(24, 71).addBox(-3f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, -9.3f, -0.7f, -0.0873f, 0f, 0.3142f)
            )

            val ClothA01_1 = ArmRight01.addOrReplaceChild(
                "ClothA01_1",
                CubeListBuilder.create().texOffs(128, 109).addBox(-3f, 0f, -3f, 6f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, 5.1f, 0f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(24, 54).addBox(0f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 11f, 2.5f)
            )

            val ClothA02a = ArmRight02.addOrReplaceChild(
                "ClothA02a",
                CubeListBuilder.create().texOffs(128, 49).addBox(-3f, 0f, -3f, 6f, 9f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.5f, -0.1f, -2.5f, 0f, 0.0128f, 0f)
            )

            val ClothA03a = ClothA02a.addOrReplaceChild(
                "ClothA03a",
                CubeListBuilder.create().texOffs(128, 65).addBox(-2.5f, 0f, 0f, 5f, 9f, 6f, CubeDeformation(0f)),
                PartPose.offset(-0.1f, 1.9f, -2.2f)
            )

            val ClothA04a = ClothA03a.addOrReplaceChild(
                "ClothA04a",
                CubeListBuilder.create().texOffs(128, 81).addBox(-2f, 0f, 0f, 4f, 8f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.9f, 0.8f)
            )

            val ClothA05a = ClothA04a.addOrReplaceChild(
                "ClothA05a",
                CubeListBuilder.create().texOffs(128, 96).addBox(-1.5f, 0f, 0f, 3f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 1.9f, 0.8f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 104),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(24, 63),
                PartPose.offsetAndRotation(0f, -9.6f, 0.5f, 0.1047f, 0f, 0f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(44, 101),
                PartPose.offset(0f, -1f, -0.7f)
            )

            addFaceLayer(GlowHead)

            return LayerDefinition.create(meshdefinition, 256, 128)
        }
    }
}
