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
import org.trp.shincolle.client.model.LegacyPoseOffsets.sittingAltY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sittingY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sneakY
import org.trp.shincolle.entity.EntityBBKongou
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelBBKongou<T : EntityShipBase>(root: ModelPart) : ShipModelHumanoidBase<T>(), IGlowableModel {
    private var isDeadPose = false
    private var isSittingPose = false
    override var poseTranslateY = 0f

    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val Butt: ModelPart
    private val Ahoke00: ModelPart?
    private val ArmLeft01: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val ArmRight01: ModelPart
    private val EquipBase: ModelPart?
    private val Cloth03a1: ModelPart
    private val Cloth03a2: ModelPart
    private val Head: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val Ahoke01: ModelPart?
    private val EquipHeadBase: ModelPart?
    private val HairU01: ModelPart
    private val HairR01: ModelPart
    private val HairL01: ModelPart
    private val HairCBase: ModelPart?
    private val HairCBaseB: ModelPart?
    private val HairS01: ModelPart?
    private val HairS02: ModelPart?
    private val HairR02: ModelPart
    private val HairL02: ModelPart
    private val HairC01: ModelPart
    private val HairC02: ModelPart
    private val HairC03: ModelPart
    private val HairC04: ModelPart
    private val HairC05: ModelPart
    private val HairC01b: ModelPart
    private val HairC02b: ModelPart
    private val HairC03b: ModelPart
    private val HairC04b: ModelPart
    private val HairC05b: ModelPart
    private val Hair01: ModelPart
    private val Hair02: ModelPart
    private val Ahoke02: ModelPart
    private val Ahoke03: ModelPart
    private val Ahoke04: ModelPart
    private val EquipHead01: ModelPart
    private val EquipHead01a: ModelPart
    private val EquipHead02: ModelPart
    private val EquipHead03: ModelPart
    private val EquipHead02a: ModelPart
    private val EquipHead03a: ModelPart
    private val LegLeft01: ModelPart
    private val Skirt01: ModelPart
    private val LegRight01: ModelPart
    private val SkirtB01: ModelPart
    private val LegLeft02: ModelPart
    private val Skirt02: ModelPart
    private val LegRight02: ModelPart
    private val Cloth01a: ModelPart
    private val Cloth02a1: ModelPart
    private val Cloth02b1: ModelPart
    private val Cloth02c1: ModelPart
    private val Cloth02c1_1: ModelPart
    private val Cloth01b: ModelPart
    private val Cloth01c: ModelPart
    private val Cloth01b2: ModelPart
    private val Cloth01c2: ModelPart
    private val Cloth02a2: ModelPart
    private val Cloth02a3: ModelPart
    private val Cloth02b2: ModelPart
    private val Cloth02b3: ModelPart
    private val Cloth02c2: ModelPart
    private val Cloth02c3: ModelPart
    private val Cloth02c4: ModelPart
    private val Cloth02c2_1: ModelPart
    private val Cloth02c3_1: ModelPart
    private val Cloth02c4_1: ModelPart
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
    private val EquipB01: ModelPart
    private val EquipB00a: ModelPart
    private val EquipB00a_1: ModelPart
    private val EquipB02: ModelPart
    private val EquipB01a: ModelPart
    private val EquipB01b00: ModelPart
    private val EquipB04: ModelPart
    private val EquipB04_1: ModelPart
    private val EquipB03: ModelPart
    private val EquipB02a: ModelPart
    private val EquipB01c: ModelPart
    private val EquipB01b01a: ModelPart
    private val EquipB01b01b: ModelPart
    private val EquipB01b01c: ModelPart
    private val EquipB01b02: ModelPart
    private val EquipB01b03: ModelPart
    private val EquipB01b04: ModelPart
    private val EquipB01b05: ModelPart
    private val EquipB01b06: ModelPart
    private val EquipB05: ModelPart
    private val EquipB06a: ModelPart
    private val EquipB06b: ModelPart
    private val EquipB06c: ModelPart
    private val EquipB06d: ModelPart
    private val EquipB06e: ModelPart
    private val EquipB06f: ModelPart
    private val EquipCL1Base01: ModelPart
    private val EquipCL1Base02: ModelPart
    private val EquipCL1a1: ModelPart
    private val EquipCL1a1_1: ModelPart
    private val EquipCL1a2: ModelPart
    private val EquipCL1a2_1: ModelPart
    private val EquipB05_1: ModelPart
    private val EquipB07a1: ModelPart
    private val EquipB07b1: ModelPart
    private val EquipB07c1: ModelPart
    private val EquipB07d1: ModelPart
    private val EquipCL1Base01_1: ModelPart
    private val EquipCL1Base02_1: ModelPart
    private val EquipCL1a1_2: ModelPart
    private val EquipCL1a1_3: ModelPart
    private val EquipCL1a2_2: ModelPart
    private val EquipCL1a2_3: ModelPart
    private val EquipB07a2: ModelPart
    private val EquipB07b2: ModelPart
    private val EquipB07c2: ModelPart
    private val EquipB07d2: ModelPart
    private val EquipB07d3: ModelPart
    private val EquipB05_2: ModelPart
    private val EquipB06a_1: ModelPart
    private val EquipB06b_1: ModelPart
    private val EquipB06c_1: ModelPart
    private val EquipB06d_1: ModelPart
    private val EquipB06e_1: ModelPart
    private val EquipB06f_1: ModelPart
    private val EquipCL1Base01_2: ModelPart
    private val EquipCL1Base02_2: ModelPart
    private val EquipCL1a1_4: ModelPart
    private val EquipCL1a1_5: ModelPart
    private val EquipCL1a2_4: ModelPart
    private val EquipCL1a2_5: ModelPart
    private val EquipB05_3: ModelPart
    private val EquipB07a1_1: ModelPart
    private val EquipB07b1_1: ModelPart
    private val EquipB07c1_1: ModelPart
    private val EquipB07d1_1: ModelPart
    private val EquipCL1Base01_3: ModelPart
    private val EquipCL1Base02_3: ModelPart
    private val EquipCL1a1_6: ModelPart
    private val EquipCL1a1_7: ModelPart
    private val EquipCL1a2_6: ModelPart
    private val EquipCL1a2_7: ModelPart
    private val EquipB07a2_1: ModelPart
    private val EquipB07b2_1: ModelPart
    private val EquipB07c2_1: ModelPart
    private val EquipB07d2_1: ModelPart
    private val EquipB07d3_1: ModelPart
    private val EquipB00b: ModelPart
    private val EquipB00c: ModelPart
    private val EquipB00d: ModelPart
    private val EquipB00b_1: ModelPart
    private val EquipB00c_1: ModelPart
    private val EquipB00d_1: ModelPart
    private val GlowBodyMain: ModelPart?
    private val GlowHead: ModelPart?
    private val GlowNeck: ModelPart?
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
    private val clothA03DefaultY: Float
    private val clothA03DefaultZ: Float
    private val clothA04DefaultY: Float
    private val clothA04DefaultZ: Float
    private val clothA05DefaultY: Float
    private val clothA05DefaultZ: Float
    private val clothA03aDefaultY: Float
    private val clothA03aDefaultZ: Float
    private val clothA04aDefaultY: Float
    private val clothA04aDefaultZ: Float
    private val clothA05aDefaultY: Float
    private val clothA05aDefaultZ: Float
    private val buttDefaultY: Float
    private val buttDefaultZ: Float
    private val skirt01DefaultY: Float
    private val skirt01DefaultZ: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.Cloth03b_1 = this.BoobL.getChild("Cloth03b_1")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.ClothA02a = this.ArmRight02.getChild("ClothA02a")
        this.ClothA03a = this.ClothA02a.getChild("ClothA03a")
        this.ClothA04a = this.ClothA03a.getChild("ClothA04a")
        this.ClothA05a = this.ClothA04a.getChild("ClothA05a")
        this.ClothA01_1 = this.ArmRight01.getChild("ClothA01_1")
        this.Cloth03a1 = this.BodyMain.getChild("Cloth03a1")
        this.EquipBase = this.BodyMain.getChild("EquipBase")
        this.EquipB01 = this.EquipBase.getChild("EquipB01")
        this.EquipB01b00 = this.EquipB01.getChild("EquipB01b00")
        this.EquipB01b01b = this.EquipB01b00.getChild("EquipB01b01b")
        this.EquipB01b02 = this.EquipB01b00.getChild("EquipB01b02")
        this.EquipB01b01a = this.EquipB01b00.getChild("EquipB01b01a")
        this.EquipB01b05 = this.EquipB01b00.getChild("EquipB01b05")
        this.EquipB01b06 = this.EquipB01b00.getChild("EquipB01b06")
        this.EquipB01b04 = this.EquipB01b00.getChild("EquipB01b04")
        this.EquipB01b01c = this.EquipB01b00.getChild("EquipB01b01c")
        this.EquipB01b03 = this.EquipB01b00.getChild("EquipB01b03")
        this.EquipB01a = this.EquipB01.getChild("EquipB01a")
        this.EquipB01c = this.EquipB01a.getChild("EquipB01c")
        this.EquipB02 = this.EquipB01.getChild("EquipB02")
        this.EquipB02a = this.EquipB02.getChild("EquipB02a")
        this.EquipB03 = this.EquipB02.getChild("EquipB03")
        this.EquipB04_1 = this.EquipB01.getChild("EquipB04_1")
        this.EquipB06b_1 = this.EquipB04_1.getChild("EquipB06b_1")
        this.EquipB05_2 = this.EquipB04_1.getChild("EquipB05_2")
        this.EquipCL1Base01_2 = this.EquipB05_2.getChild("EquipCL1Base01_2")
        this.EquipCL1a1_4 = this.EquipCL1Base01_2.getChild("EquipCL1a1_4")
        this.EquipCL1a2_4 = this.EquipCL1a1_4.getChild("EquipCL1a2_4")
        this.EquipCL1Base02_2 = this.EquipCL1Base01_2.getChild("EquipCL1Base02_2")
        this.EquipCL1a1_5 = this.EquipCL1Base01_2.getChild("EquipCL1a1_5")
        this.EquipCL1a2_5 = this.EquipCL1a1_5.getChild("EquipCL1a2_5")
        this.EquipB06e_1 = this.EquipB04_1.getChild("EquipB06e_1")
        this.EquipB06d_1 = this.EquipB04_1.getChild("EquipB06d_1")
        this.EquipB05_3 = this.EquipB06d_1.getChild("EquipB05_3")
        this.EquipCL1Base01_3 = this.EquipB05_3.getChild("EquipCL1Base01_3")
        this.EquipCL1Base02_3 = this.EquipCL1Base01_3.getChild("EquipCL1Base02_3")
        this.EquipCL1a1_7 = this.EquipCL1Base01_3.getChild("EquipCL1a1_7")
        this.EquipCL1a2_7 = this.EquipCL1a1_7.getChild("EquipCL1a2_7")
        this.EquipCL1a1_6 = this.EquipCL1Base01_3.getChild("EquipCL1a1_6")
        this.EquipCL1a2_6 = this.EquipCL1a1_6.getChild("EquipCL1a2_6")
        this.EquipB07a1_1 = this.EquipB06d_1.getChild("EquipB07a1_1")
        this.EquipB07a2_1 = this.EquipB07a1_1.getChild("EquipB07a2_1")
        this.EquipB07d1_1 = this.EquipB06d_1.getChild("EquipB07d1_1")
        this.EquipB07d3_1 = this.EquipB07d1_1.getChild("EquipB07d3_1")
        this.EquipB07d2_1 = this.EquipB07d1_1.getChild("EquipB07d2_1")
        this.EquipB07c1_1 = this.EquipB06d_1.getChild("EquipB07c1_1")
        this.EquipB07c2_1 = this.EquipB07c1_1.getChild("EquipB07c2_1")
        this.EquipB07b1_1 = this.EquipB06d_1.getChild("EquipB07b1_1")
        this.EquipB07b2_1 = this.EquipB07b1_1.getChild("EquipB07b2_1")
        this.EquipB06a_1 = this.EquipB04_1.getChild("EquipB06a_1")
        this.EquipB06f_1 = this.EquipB04_1.getChild("EquipB06f_1")
        this.EquipB06c_1 = this.EquipB04_1.getChild("EquipB06c_1")
        this.EquipB04 = this.EquipB01.getChild("EquipB04")
        this.EquipB05 = this.EquipB04.getChild("EquipB05")
        this.EquipCL1Base01 = this.EquipB05.getChild("EquipCL1Base01")
        this.EquipCL1Base02 = this.EquipCL1Base01.getChild("EquipCL1Base02")
        this.EquipCL1a1_1 = this.EquipCL1Base01.getChild("EquipCL1a1_1")
        this.EquipCL1a2_1 = this.EquipCL1a1_1.getChild("EquipCL1a2_1")
        this.EquipCL1a1 = this.EquipCL1Base01.getChild("EquipCL1a1")
        this.EquipCL1a2 = this.EquipCL1a1.getChild("EquipCL1a2")
        this.EquipB06e = this.EquipB04.getChild("EquipB06e")
        this.EquipB06d = this.EquipB04.getChild("EquipB06d")
        this.EquipB05_1 = this.EquipB06d.getChild("EquipB05_1")
        this.EquipCL1Base01_1 = this.EquipB05_1.getChild("EquipCL1Base01_1")
        this.EquipCL1a1_2 = this.EquipCL1Base01_1.getChild("EquipCL1a1_2")
        this.EquipCL1a2_2 = this.EquipCL1a1_2.getChild("EquipCL1a2_2")
        this.EquipCL1Base02_1 = this.EquipCL1Base01_1.getChild("EquipCL1Base02_1")
        this.EquipCL1a1_3 = this.EquipCL1Base01_1.getChild("EquipCL1a1_3")
        this.EquipCL1a2_3 = this.EquipCL1a1_3.getChild("EquipCL1a2_3")
        this.EquipB07c1 = this.EquipB06d.getChild("EquipB07c1")
        this.EquipB07c2 = this.EquipB07c1.getChild("EquipB07c2")
        this.EquipB07d1 = this.EquipB06d.getChild("EquipB07d1")
        this.EquipB07d3 = this.EquipB07d1.getChild("EquipB07d3")
        this.EquipB07d2 = this.EquipB07d1.getChild("EquipB07d2")
        this.EquipB07b1 = this.EquipB06d.getChild("EquipB07b1")
        this.EquipB07b2 = this.EquipB07b1.getChild("EquipB07b2")
        this.EquipB07a1 = this.EquipB06d.getChild("EquipB07a1")
        this.EquipB07a2 = this.EquipB07a1.getChild("EquipB07a2")
        this.EquipB06a = this.EquipB04.getChild("EquipB06a")
        this.EquipB06b = this.EquipB04.getChild("EquipB06b")
        this.EquipB06c = this.EquipB04.getChild("EquipB06c")
        this.EquipB06f = this.EquipB04.getChild("EquipB06f")
        this.EquipB00a = this.EquipBase.getChild("EquipB00a")
        this.EquipB00b = this.EquipB00a.getChild("EquipB00b")
        this.EquipB00c = this.EquipB00b.getChild("EquipB00c")
        this.EquipB00d = this.EquipB00c.getChild("EquipB00d")
        this.EquipB00a_1 = this.EquipBase.getChild("EquipB00a_1")
        this.EquipB00b_1 = this.EquipB00a_1.getChild("EquipB00b_1")
        this.EquipB00c_1 = this.EquipB00b_1.getChild("EquipB00c_1")
        this.EquipB00d_1 = this.EquipB00c_1.getChild("EquipB00d_1")
        this.Cloth03a2 = this.BodyMain.getChild("Cloth03a2")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.ClothB01 = this.BoobR.getChild("ClothB01")
        this.Cloth03b = this.BoobR.getChild("Cloth03b")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.SkirtB01 = this.Butt.getChild("SkirtB01")
        this.Cloth02b1 = this.SkirtB01.getChild("Cloth02b1")
        this.Cloth02b2 = this.Cloth02b1.getChild("Cloth02b2")
        this.Cloth02b3 = this.Cloth02b2.getChild("Cloth02b3")
        this.Cloth01a = this.SkirtB01.getChild("Cloth01a")
        this.Cloth01c2 = this.Cloth01a.getChild("Cloth01c2")
        this.Cloth01b2 = this.Cloth01a.getChild("Cloth01b2")
        this.Cloth01c = this.Cloth01a.getChild("Cloth01c")
        this.Cloth01b = this.Cloth01a.getChild("Cloth01b")
        this.Cloth02a1 = this.SkirtB01.getChild("Cloth02a1")
        this.Cloth02a2 = this.Cloth02a1.getChild("Cloth02a2")
        this.Cloth02a3 = this.Cloth02a2.getChild("Cloth02a3")
        this.Cloth02c1 = this.SkirtB01.getChild("Cloth02c1")
        this.Cloth02c2 = this.Cloth02c1.getChild("Cloth02c2")
        this.Cloth02c3 = this.Cloth02c2.getChild("Cloth02c3")
        this.Cloth02c4 = this.Cloth02c3.getChild("Cloth02c4")
        this.Cloth02c1_1 = this.SkirtB01.getChild("Cloth02c1_1")
        this.Cloth02c2_1 = this.Cloth02c1_1.getChild("Cloth02c2_1")
        this.Cloth02c3_1 = this.Cloth02c2_1.getChild("Cloth02c3_1")
        this.Cloth02c4_1 = this.Cloth02c3_1.getChild("Cloth02c4_1")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.Skirt02 = this.Skirt01.getChild("Skirt02")
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
        this.HairU01 = this.Hair.getChild("HairU01")
        this.HairS01 = this.Hair.getChild("HairS01")
        this.HairCBase = this.Hair.getChild("HairCBase")
        this.HairC01 = this.HairCBase.getChild("HairC01")
        this.HairC02 = this.HairC01.getChild("HairC02")
        this.HairC03 = this.HairC02.getChild("HairC03")
        this.HairC04 = this.HairC03.getChild("HairC04")
        this.HairC05 = this.HairC04.getChild("HairC05")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.HairCBaseB = this.Hair.getChild("HairCBaseB")
        this.HairC01b = this.HairCBaseB.getChild("HairC01b")
        this.HairC02b = this.HairC01b.getChild("HairC02b")
        this.HairC03b = this.HairC02b.getChild("HairC03b")
        this.HairC04b = this.HairC03b.getChild("HairC04b")
        this.HairC05b = this.HairC04b.getChild("HairC05b")
        this.HairS02 = this.Hair.getChild("HairS02")
        this.EquipHeadBase = this.Head.getChild("EquipHeadBase")
        this.EquipHead01a = this.EquipHeadBase.getChild("EquipHead01a")
        this.EquipHead03a = this.EquipHead01a.getChild("EquipHead03a")
        this.EquipHead02a = this.EquipHead01a.getChild("EquipHead02a")
        this.EquipHead01 = this.EquipHeadBase.getChild("EquipHead01")
        this.EquipHead03 = this.EquipHead01.getChild("EquipHead03")
        this.EquipHead02 = this.EquipHead01.getChild("EquipHead02")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.Ahoke00 = this.Head.getChild("Ahoke00")
        this.Ahoke01 = this.Head.getChild("Ahoke01")
        this.Ahoke02 = this.Ahoke01.getChild("Ahoke02")
        this.Ahoke03 = this.Ahoke02.getChild("Ahoke03")
        this.Ahoke04 = this.Ahoke03.getChild("Ahoke04")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        initFaceParts(this.GlowHead)
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
        this.clothA03DefaultY = this.ClothA03.y
        this.clothA03DefaultZ = this.ClothA03.z
        this.clothA04DefaultY = this.ClothA04.y
        this.clothA04DefaultZ = this.ClothA04.z
        this.clothA05DefaultY = this.ClothA05.y
        this.clothA05DefaultZ = this.ClothA05.z
        this.clothA03aDefaultY = this.ClothA03a.y
        this.clothA03aDefaultZ = this.ClothA03a.z
        this.clothA04aDefaultY = this.ClothA04a.y
        this.clothA04aDefaultZ = this.ClothA04a.z
        this.clothA05aDefaultY = this.ClothA05a.y
        this.clothA05aDefaultZ = this.ClothA05a.z
        this.buttDefaultY = this.Butt.y
        this.buttDefaultZ = this.Butt.z
        this.skirt01DefaultY = this.Skirt01.y
        this.skirt01DefaultZ = this.Skirt01.z
    }

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val ctx = computePoseContext(entity, limbSwing, limbSwingAmount, ageInTicks, 0.0f)
        resetPoseState()
        resetOffsets()

        applyFaceAndMouth(entity)
        setFlushVisible(
            entity != null && (entity.emotionPrimary == EntityShipBase.EMOTION_SHY
                    || entity.emotionPrimary == EntityShipBase.EMOTION_HAPPY)
        )
        applyEquipVisibility(entity)

        if (isDeadPose(entity)) {
            applyDeadPose()
            syncGlowParts()
            return
        }

        applyHeadRotation(Head, entity, ageInTicks, netHeadYaw, headPitch)

        applyBasePose(ctx, limbSwing, limbSwingAmount, ageInTicks, headPitch)
        applySpecialPoseAdjustments(entity, ctx, limbSwing, limbSwingAmount, ageInTicks)
        applyHairAndClothAnimation(ctx, limbSwing, ageInTicks, limbSwingAmount)

        syncGlowParts()
    }

    private fun resetPoseState() {
        this.isDeadPose = false
        this.isSittingPose = false
        this.poseTranslateY = 0.0f
    }

    private fun resetOffsets() {
        ArmLeft02.x = armLeft02DefaultX
        ArmLeft02.z = armLeft02DefaultZ
        ArmRight02.x = armRight02DefaultX
        ArmRight02.z = armRight02DefaultZ

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

        ClothA03.y = clothA03DefaultY
        ClothA03.z = clothA03DefaultZ
        ClothA04.y = clothA04DefaultY
        ClothA04.z = clothA04DefaultZ
        ClothA05.y = clothA05DefaultY
        ClothA05.z = clothA05DefaultZ

        ClothA03a.y = clothA03aDefaultY
        ClothA03a.z = clothA03aDefaultZ
        ClothA04a.y = clothA04aDefaultY
        ClothA04a.z = clothA04aDefaultZ
        ClothA05a.y = clothA05aDefaultY
        ClothA05a.z = clothA05aDefaultZ

        Butt.y = buttDefaultY
        Butt.z = buttDefaultZ
        Skirt01.y = skirt01DefaultY
        Skirt01.z = skirt01DefaultZ
    }

    private fun isDeadPose(entity: T?): Boolean {
        return entity != null && entity.isInDeadPose
    }

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) return
        val showEquip = entity.getEquipFlag(EntityBBKongou.EQUIP_RIGGING)
        val showHead = entity.getEquipFlag(EntityBBKongou.EQUIP_HEAD_BASE)
        val showHairSet = entity.getEquipFlag(EntityBBKongou.EQUIP_HAIR_SET)
        val showAhoke00 = entity.getEquipFlag(EntityBBKongou.EQUIP_AHOKE)
        if (EquipBase != null) EquipBase.visible = showEquip
        if (EquipHeadBase != null) EquipHeadBase.visible = showHead
        if (HairS01 != null) HairS01.visible = showHairSet
        if (HairS02 != null) HairS02.visible = showHairSet
        if (HairCBase != null) HairCBase.visible = showHairSet
        if (HairCBaseB != null) HairCBaseB.visible = showHairSet
        if (Ahoke00 != null) Ahoke00.visible = showAhoke00
        if (Ahoke01 != null) Ahoke01.visible = !showAhoke00
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        Head.xRot = 0.0f
        Head.yRot = 0.0f
        Head.zRot = 0.0f
        BodyMain.xRot = 1.4f
        Butt.xRot = 0.21f
        BoobL.xRot = -0.8f
        BoobR.xRot = -0.8f
        ClothB01.xRot = 0.96f
        Skirt01.xRot = -0.087f
        Skirt02.xRot = -0.087f
        SkirtB01.xRot = 0.087f

        Cloth02a1.xRot = -0.5585f
        Cloth02b1.xRot = -0.5585f
        Cloth02c1.xRot = 0.6283f
        Cloth02c1_1.xRot = 0.6283f
        Cloth02c2.xRot = -0.7854f
        Cloth02c2_1.xRot = -0.7854f
        Cloth02c3.xRot = -0.1396f
        Cloth02c3_1.xRot = -0.1396f
        Cloth02c4.xRot = 0.0f
        Cloth02c4_1.xRot = 0.0f
        Cloth02a2.xRot = 0.1745f
        Cloth02b2.xRot = 0.1745f
        Cloth02a3.xRot = 0.0f
        Cloth02b3.xRot = 0.0f

        Ahoke00!!.xRot = 0.6632f
        Ahoke00.yRot = 0.523f
        Ahoke00.zRot = 0.0f
        Ahoke01!!.xRot = 2.7f
        Ahoke02.xRot = 1.22f
        Ahoke03.xRot = 1.48f
        Ahoke04.xRot = 0.96f

        Hair01.xRot = 0.1f
        Hair01.yRot = 0.0f
        Hair01.zRot = 0.0f
        Hair02.xRot = -0.3f
        Hair02.yRot = 0.0f
        Hair02.zRot = 0.0f

        ArmLeft01.xRot = -2.8f
        ArmLeft01.yRot = 0.1f
        ArmLeft01.zRot = 0.84f
        ArmLeft02.xRot = 0.0f
        ArmLeft02.zRot = 1.0f

        ArmRight01.xRot = 0.0f
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = 0.2f
        ArmRight02.xRot = 0.0f
        ArmRight02.zRot = 0.0f

        LegLeft01.xRot = -0.12f
        LegLeft01.yRot = 0.0f
        LegLeft01.zRot = -0.05f
        LegLeft02.xRot = 0.0f
        LegLeft02.yRot = 0.0f
        LegLeft02.zRot = 0.0f

        LegRight01.xRot = -0.12f
        LegRight01.yRot = 0.0f
        LegRight01.zRot = 0.26f
        LegRight02.xRot = 0.0f
        LegRight02.yRot = 0.0f
        LegRight02.zRot = -0.4f

        EquipBase!!.visible = false
    }

    private fun applyBasePose(
        ctx: PoseContext,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headPitch: Float
    ) {
        val angleX = ctx.angleX
        val angleX1 = Mth.cos(ageInTicks * 0.1f + 0.35f + limbSwing * 0.5f)
        val angleX2 = Mth.cos(ageInTicks * 0.1f + 0.7f + limbSwing * 0.5f)
        val angleX3 = Mth.cos(ageInTicks * 0.1f + 1.05f + limbSwing * 0.5f)
        val angleX4 = Mth.cos(ageInTicks * 0.1f + 1.4f + limbSwing * 0.5f)

        Ahoke00!!.xRot = angleX2 * 0.05f + 0.66f
        Ahoke00.yRot = -angleX * 0.15f + 0.53f
        Ahoke01!!.xRot = -angleX1 * 0.09f + 2.7f
        Ahoke02.xRot = angleX2 * 0.15f + 1.22f
        Ahoke03.xRot = -angleX3 * 0.1f + 1.48f
        Ahoke04.xRot = -angleX4 * 0.1f + 0.96f

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
        Cloth02a2.xRot = 0.12f + angleX1 * 0.06f
        Cloth02b2.xRot = 0.12f + angleX1 * 0.06f
        Cloth02a3.xRot = -angleX2 * 0.06f
        Cloth02b3.xRot = -angleX2 * 0.06f

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

        EquipCL1a1.xRot = Head.xRot * 0.8f - 0.21f
        EquipCL1a1_1.xRot = Head.xRot * 0.7f - 0.23f
        EquipCL1a1_2.xRot = Head.xRot * 0.85f - 0.2f
        EquipCL1a1_3.xRot = Head.xRot * 0.75f - 0.25f
        EquipCL1a1_4.xRot = Head.xRot * 0.8f - 0.2f
        EquipCL1a1_5.xRot = Head.xRot * 0.85f - 0.19f
        EquipCL1a1_6.xRot = Head.xRot * 0.75f - 0.21f
        EquipCL1a1_7.xRot = Head.xRot * 0.88f - 0.19f
        EquipCL1Base01.yRot = Head.yRot * 0.5f - 0.9f
        EquipCL1Base01_1.yRot = Head.yRot * 0.75f
        EquipCL1Base01_2.yRot = Head.yRot * 0.5f + 0.9f
        EquipCL1Base01_3.yRot = Head.yRot * 0.75f
    }

    private fun applySpecialPoseAdjustments(
        entity: T?,
        ctx: PoseContext,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float
    ) {
        var legAddLeft = ctx.angleAdd1 * 0.3f - 0.28f
        var legAddRight = ctx.angleAdd2 * 0.3f - 0.21f
        var spcStand = true

        if (entity != null && entity.shipDepth > 0.0) {
            this.poseTranslateY += ctx.angleX * 0.05f + 0.025f
        }

        val isCrouching = entity != null && entity.isCrouching
        val isSitting =
            ctx.isSitting || (entity != null && entity.isPassenger && (entity.vehicle !is EntityMountBase))
        val isSprinting = (entity != null && entity.isSprinting) || limbSwingAmount > 0.9f

        if (isSprinting) {
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

            ArmLeft01.xRot = ctx.angleAdd2 * 1.2f + 0.5f
            ArmLeft01.yRot = 0.0f
            ArmLeft02.xRot = -1.0f
            ArmLeft02.zRot = 0.0f
            ArmRight01.xRot = ctx.angleAdd1 * 1.2f + 0.5f
            ArmRight01.yRot = 0.0f
            ArmRight02.xRot = -1.0f
            ArmRight02.zRot = 0.0f

            legAddLeft = ctx.angleAdd1 * 0.7f - 0.48f
            legAddRight = ctx.angleAdd2 * 0.7f - 0.41f
        }

        if (isCrouching) {
            spcStand = false
            this.poseTranslateY += SNEAK_TRANSLATE_Y
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

            legAddLeft -= 0.94f
            legAddRight -= 0.94f
            LegLeft01.zRot = 0.2f
            LegRight01.zRot = -0.2f
        }

        if (isSitting) {
            spcStand = false
            this.isSittingPose = true
            val sitPhase = if (entity != null) (entity.tickCount % 512) else 0
            if (sitPhase > 256) {
                if (entity != null && hasLegacyState(entity, 1, 4)) {
                    this.poseTranslateY += SITTING_ALT_TRANSLATE_Y
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
                    legAddLeft = -0.2f
                    legAddRight = -0.2f
                    LegLeft01.yRot = 0.0f
                    LegLeft01.zRot = -0.1f
                    LegLeft02.xRot = 0.0f
                    LegRight01.yRot = 0.0f
                    LegRight01.zRot = 0.1f
                    LegRight02.xRot = 0.0f
                    EquipBase!!.visible = false
                } else {
                    this.poseTranslateY += 0.55f * 3
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
                    Cloth02c4.xRot = 0.0f
                    Cloth02c1_1.xRot = 1.5f
                    Cloth02c2_1.xRot = 0.35f
                    Cloth02c3_1.xRot = 0.05f
                    Cloth02c4_1.xRot = 0.0f
                    ClothA03.yRot = 0.2f
                    ClothA03a.yRot = -0.2f
                    ArmLeft01.xRot = -1.18f
                    ArmLeft01.yRot = 0.27f
                    ArmLeft01.zRot = -0.1f
                    ArmLeft02.zRot = 0.92f
                    ArmRight01.xRot = -1.18f
                    ArmRight01.yRot = -0.27f
                    ArmRight01.zRot = 0.1f
                    ArmRight02.zRot = -1.32f
                    legAddLeft = -2.57f
                    legAddRight = -2.57f
                    LegLeft01.y = legLeft01DefaultY + (0.25f * OFFSET_SCALE)
                    LegLeft01.z = legLeft01DefaultZ + (-0.2f * OFFSET_SCALE)
                    LegLeft01.yRot = 0.11f
                    LegLeft01.zRot = -0.12f
                    LegLeft02.xRot = 2.75f
                    LegLeft02.zRot = 0.02f
                    LegLeft02.z = legLeft02DefaultZ + (0.37f * OFFSET_SCALE)
                    LegRight01.y = legRight01DefaultY + (0.25f * OFFSET_SCALE)
                    LegRight01.z = legRight01DefaultZ + (-0.2f * OFFSET_SCALE)
                    LegRight01.yRot = -0.11f
                    LegRight01.zRot = 0.12f
                    LegRight02.xRot = 2.75f
                    LegRight02.zRot = -0.02f
                    LegRight02.z = legRight02DefaultZ + (0.37f * OFFSET_SCALE)
                }
            } else {
                this.poseTranslateY += SITTING_TRANSLATE_Y
                Head.xRot += 0.14f
                BodyMain.xRot = -0.4363f
                Skirt01.xRot = -0.35f
                Skirt02.xRot = -0.19f
                SkirtB01.xRot = -0.12f
                Cloth02a2.xRot += 0.32f
                Cloth02a3.xRot += 0.4f
                Cloth02b2.xRot += 0.32f
                Cloth02b3.xRot += 0.4f
                Cloth02c1.xRot += 0.45f
                Cloth02c2.xRot += 0.1f
                Cloth02c1_1.xRot += 0.45f
                Cloth02c2_1.xRot += 0.1f
                ClothA03.yRot = 1.49f
                ClothA03a.yRot = -1.33f
                ArmLeft01.xRot = -0.3142f
                ArmLeft01.zRot = 0.349f
                ArmLeft02.zRot = 1.15f
                ArmRight01.xRot = -0.4363f
                ArmRight01.zRot = -0.2793f
                ArmRight02.zRot = -1.4f
                legAddLeft = -1.309f
                legAddRight = -1.7f
                LegLeft01.yRot = 0.3142f
                LegLeft02.xRot = 1.0472f
                LegRight01.yRot = -0.35f
                LegRight01.zRot = -0.2618f
                LegRight02.xRot = 0.9f
                Hair01.xRot += 0.12f
                Hair02.xRot += 0.15f
            }
        }

        if (entity != null && entity.attackTick > 20) {
            spcStand = false
            BodyMain.xRot = -0.17f
            ArmLeft01.xRot = -1.57f
            ArmLeft01.yRot = -0.26f
            ArmLeft01.zRot = 0.0f
            ArmRight01.xRot = 0.0f
            ArmRight01.zRot = 0.87f
            ArmRight02.zRot = -1.57f
            legAddLeft += 0.14f
            legAddRight += 0.07f
            LegLeft01.yRot = 0.0f
            LegLeft01.zRot = -0.17f
            LegRight01.yRot = 0.0f
            LegRight01.zRot = 0.17f
        }

        if (spcStand && (entity == null || (entity.tickCount % 512) > 256)) {
            BodyMain.xRot = -0.17f
            ArmLeft01.xRot = -1.57f
            ArmLeft01.yRot = -0.26f
            ArmLeft01.zRot = 0.0f
            ArmRight01.xRot = 0.0f
            ArmRight01.zRot = 0.87f
            ArmRight02.zRot = -1.57f
            legAddLeft += 0.14f
            legAddRight += 0.07f
            LegLeft01.yRot = 0.0f
            LegLeft01.zRot = -0.17f
            LegRight01.yRot = 0.0f
            LegRight01.zRot = 0.17f
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

        LegLeft01.xRot = legAddLeft
        LegRight01.xRot = legAddRight
    }

    private fun applyHairAndClothAnimation(
        ctx: PoseContext,
        limbSwing: Float,
        ageInTicks: Float,
        limbSwingAmount: Float
    ) {
        val angleX = ctx.angleX
        val angleX1 = Mth.cos(ageInTicks * 0.1f + 0.35f + limbSwing * 0.5f)
        val headX = Head.xRot * -0.5f
        val headZ = Head.zRot * -0.5f

        Hair01.xRot = angleX * 0.03f + 0.26f + headX
        Hair02.xRot = -angleX1 * 0.04f - 0.17f + headX

        if (this.isSittingPose) {
            Hair01.xRot = 0.21f + headX
            Hair02.xRot = -0.28f + headX
        } else if (limbSwingAmount > 0.9f) {
            Hair01.xRot += 0.2f
            Hair02.xRot += 0.2f
        } else if (this.poseTranslateY != 0.0f && !this.isDeadPose) {
            Hair01.xRot = Hair01.xRot * 0.5f + 0.4f
            Hair02.xRot = Hair02.xRot * 0.75f + 0.25f
        }

        HairL01.xRot = angleX * 0.02f + headX - 0.19f
        HairL02.xRot = -angleX1 * 0.04f + headX + 0.17f
        HairR01.xRot = angleX * 0.02f + headX - 0.19f
        HairR02.xRot = -angleX1 * 0.04f + headX + 0.17f

        Hair01.zRot = headZ
        Hair02.zRot = headZ
        HairL01.zRot = headZ - 0.087f
        HairL02.zRot = headZ + 0.087f
        HairR01.zRot = headZ + 0.087f
        HairR02.zRot = headZ - 0.052f

        val HandL = BodyMain.xRot + ArmLeft01.xRot + ArmLeft02.xRot
        val HandR = BodyMain.xRot + ArmRight01.xRot + ArmRight02.xRot
        val HandLc = Mth.cos(HandL)
        val HandLs = Mth.sin(HandL)
        val HandRc = Mth.cos(HandR)
        val HandRs = Mth.sin(HandR)

        ClothA03.y = clothA03DefaultY + (HandLc * 0.1f * OFFSET_SCALE)
        ClothA04.y = clothA04DefaultY + (HandLc * 0.2f * OFFSET_SCALE)
        ClothA05.y = clothA05DefaultY + (HandLc * 0.25f * OFFSET_SCALE)
        ClothA03.z = clothA03DefaultZ + (HandLs * -0.32f * OFFSET_SCALE)
        ClothA04.z = clothA04DefaultZ + (HandLs * -0.32f * OFFSET_SCALE)
        ClothA05.z = clothA05DefaultZ + (HandLs * -0.32f * OFFSET_SCALE)

        ClothA03a.y = clothA03aDefaultY + (HandRc * 0.1f * OFFSET_SCALE)
        ClothA04a.y = clothA04aDefaultY + (HandRc * 0.2f * OFFSET_SCALE)
        ClothA05a.y = clothA05aDefaultY + (HandRc * 0.25f * OFFSET_SCALE)
        ClothA03a.z = clothA03aDefaultZ + (HandRs * -0.32f * OFFSET_SCALE)
        ClothA04a.z = clothA04aDefaultZ + (HandRs * -0.32f * OFFSET_SCALE)
        ClothA05a.z = clothA05aDefaultZ + (HandRs * -0.32f * OFFSET_SCALE)
    }

    private fun syncGlowParts() {
        if (this.GlowBodyMain != null) {
            GlowBodyMain.copyFrom(BodyMain)
            if (this.GlowNeck != null) GlowNeck.copyFrom(Neck)
            if (this.GlowHead != null) GlowHead.copyFrom(Head)
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "bb_kongou"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelBBKongou")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelBBKongou")
        private val SITTING_TRANSLATE_Y = sittingY("ModelBBKongou")
        private val SITTING_ALT_TRANSLATE_Y = sittingAltY("ModelBBKongou")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 104).addBox(-6.5f, -11f, -4f, 13f, 17f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
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

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(24, 54).addBox(0f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 11f, 2.5f)
            )

            val ClothA02a = ArmRight02.addOrReplaceChild(
                "ClothA02a",
                CubeListBuilder.create().texOffs(128, 49).addBox(-3f, 0f, -3f, 6f, 9f, 6f, CubeDeformation(0f)),
                PartPose.offset(2.5f, -0.1f, -2.5f)
            )

            val ClothA03a = ClothA02a.addOrReplaceChild(
                "ClothA03a",
                CubeListBuilder.create().texOffs(128, 65).addBox(-2.5f, 0f, 0f, 5f, 9f, 6f, CubeDeformation(0f)),
                PartPose.offset(-0.1f, -0.1f, -2.2f)
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

            val ClothA01_1 = ArmRight01.addOrReplaceChild(
                "ClothA01_1",
                CubeListBuilder.create().texOffs(128, 109).addBox(-3f, 0f, -3f, 6f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, 5.1f, 0f)
            )

            val Cloth03a1 = BodyMain.addOrReplaceChild(
                "Cloth03a1",
                CubeListBuilder.create().texOffs(159, 55).addBox(-1f, 0f, 0f, 2f, 18f, 7f, CubeDeformation(0f)),
                PartPose.offset(4.1f, -11.1f, -4.1f)
            )

            val EquipBase = BodyMain.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 6.5f, 9f)
            )

            val EquipB01 = EquipBase.addOrReplaceChild(
                "EquipB01",
                CubeListBuilder.create().texOffs(185, 0).addBox(-5.5f, 0f, 0f, 11f, 10f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, -2f, 0f)
            )

            val EquipB01b00 = EquipB01.addOrReplaceChild(
                "EquipB01b00",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.1f, 9.8f)
            )

            val EquipB01b01b = EquipB01b00.addOrReplaceChild(
                "EquipB01b01b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, 0f, -0.5f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1f, -7.9f, 0f, -0.0873f, 0f, -0.1222f)
            )

            val EquipB01b02 = EquipB01b00.addOrReplaceChild(
                "EquipB01b02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, 0f, -2f, 4f, 1f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, -8.7f, 0.6f)
            )

            val EquipB01b01a = EquipB01b00.addOrReplaceChild(
                "EquipB01b01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, 0f, -0.5f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7.9f, 1.2f, 0.1222f, 0f, 0f)
            )

            val EquipB01b05 = EquipB01b00.addOrReplaceChild(
                "EquipB01b05",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.6f, 0f, -0.5f, 1f, 18f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, -33.4f, 0.3f)
            )

            val EquipB01b06 = EquipB01b00.addOrReplaceChild(
                "EquipB01b06",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.5f, 0f, 0f, 11f, 1f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, -29f, -0.1f)
            )

            val EquipB01b04 = EquipB01b00.addOrReplaceChild(
                "EquipB01b04",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 1f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, -15.5f, 0.5f)
            )

            val EquipB01b01c = EquipB01b00.addOrReplaceChild(
                "EquipB01b01c",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, 0f, -0.5f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, -7.9f, 0f, -0.0873f, 0f, 0.1222f)
            )

            val EquipB01b03 = EquipB01b00.addOrReplaceChild(
                "EquipB01b03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 2f, 6f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.5f, 0.5f)
            )

            val EquipB01a = EquipB01.addOrReplaceChild(
                "EquipB01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, -3.5f, 7f, 4f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, -3.9f, 4.8f)
            )

            val EquipB01c = EquipB01a.addOrReplaceChild(
                "EquipB01c",
                CubeListBuilder.create().texOffs(0, 20).addBox(-2.5f, 0f, -2.5f, 5f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, -4.9f, 0.5f)
            )

            val EquipB02 = EquipB01.addOrReplaceChild(
                "EquipB02",
                CubeListBuilder.create().texOffs(226, 0).mirror()
                    .addBox(-4.5f, 0f, 0f, 9f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 8.9f)
            )

            val EquipB02a = EquipB02.addOrReplaceChild(
                "EquipB02a",
                CubeListBuilder.create().texOffs(0, 30).addBox(-2f, 0f, -1f, 4f, 5f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, -4.9f, 4.6f)
            )

            val EquipB03 = EquipB02.addOrReplaceChild(
                "EquipB03",
                CubeListBuilder.create().texOffs(185, 20).mirror().addBox(-3f, 0f, 0f, 6f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 4.9f)
            )

            val EquipB04_1 = EquipB01.addOrReplaceChild(
                "EquipB04_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, 0f, 0f, 5f, 4f, 12f, CubeDeformation(0f)),
                PartPose.offset(-5f, -2f, -0.5f)
            )

            val EquipB06b_1 = EquipB04_1.addOrReplaceChild(
                "EquipB06b_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, 0f, 0f, 3f, 6f, 11f, CubeDeformation(0f)),
                PartPose.offset(-11.4f, 4f, 0.5f)
            )

            val EquipB05_2 = EquipB04_1.addOrReplaceChild(
                "EquipB05_2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, -4.5f, 9f, 8f, 9f, CubeDeformation(0f)),
                PartPose.offset(-9.4f, -3.8f, 6f)
            )

            val EquipCL1Base01_2 = EquipB05_2.addOrReplaceChild(
                "EquipCL1Base01_2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -4f, -1.5f, 9f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.1f, 0f, 0f, 1.5708f, 0f)
            )

            val EquipCL1a1_4 = EquipCL1Base01_2.addOrReplaceChild(
                "EquipCL1a1_4",
                CubeListBuilder.create().texOffs(19, 29).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -2.3f, -2.5f, -0.2094f, 0f, 0f)
            )

            val EquipCL1a2_4 = EquipCL1a1_4.addOrReplaceChild(
                "EquipCL1a2_4",
                CubeListBuilder.create().texOffs(151, 67).addBox(-1f, 0f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.5708f, 0f, 0f)
            )

            val EquipCL1Base02_2 = EquipCL1Base01_2.addOrReplaceChild(
                "EquipCL1Base02_2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -4f, -2f, 9f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.3f, -2.8f, 0.1745f, 0f, 0f)
            )

            val EquipCL1a1_5 = EquipCL1Base01_2.addOrReplaceChild(
                "EquipCL1a1_5",
                CubeListBuilder.create().texOffs(19, 29).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, -2.3f, -2.5f, -0.2094f, 0f, 0f)
            )

            val EquipCL1a2_5 = EquipCL1a1_5.addOrReplaceChild(
                "EquipCL1a2_5",
                CubeListBuilder.create().texOffs(151, 67).addBox(-1f, 0f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.5708f, 0f, 0f)
            )

            val EquipB06e_1 = EquipB04_1.addOrReplaceChild(
                "EquipB06e_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 4f, 9f, 11f, CubeDeformation(0f)),
                PartPose.offset(-25.1f, 4f, 0.5f)
            )

            val EquipB06d_1 = EquipB04_1.addOrReplaceChild(
                "EquipB06d_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, 0f, 0f, 4f, 9f, 11f, CubeDeformation(0f)),
                PartPose.offset(-17.2f, 4f, 0.5f)
            )

            val EquipB05_3 = EquipB06d_1.addOrReplaceChild(
                "EquipB05_3",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, -4.5f, 9f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offset(-4.8f, -1.9f, 5f)
            )

            val EquipCL1Base01_3 = EquipB05_3.addOrReplaceChild(
                "EquipCL1Base01_3",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -4f, -1.5f, 9f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.1f, 0f)
            )

            val EquipCL1Base02_3 = EquipCL1Base01_3.addOrReplaceChild(
                "EquipCL1Base02_3",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -4f, -2f, 9f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.3f, -2.8f, 0.1745f, 0f, 0f)
            )

            val EquipCL1a1_7 = EquipCL1Base01_3.addOrReplaceChild(
                "EquipCL1a1_7",
                CubeListBuilder.create().texOffs(19, 29).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, -2.3f, -2.5f, -0.2094f, 0f, 0f)
            )

            val EquipCL1a2_7 = EquipCL1a1_7.addOrReplaceChild(
                "EquipCL1a2_7",
                CubeListBuilder.create().texOffs(151, 67).addBox(-1f, 0f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.5708f, 0f, 0f)
            )

            val EquipCL1a1_6 = EquipCL1Base01_3.addOrReplaceChild(
                "EquipCL1a1_6",
                CubeListBuilder.create().texOffs(19, 29).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -2.3f, -2.5f, -0.2094f, 0f, 0f)
            )

            val EquipCL1a2_6 = EquipCL1a1_6.addOrReplaceChild(
                "EquipCL1a2_6",
                CubeListBuilder.create().texOffs(151, 67).addBox(-1f, 0f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.5708f, 0f, 0f)
            )

            val EquipB07a1_1 = EquipB06d_1.addOrReplaceChild(
                "EquipB07a1_1",
                CubeListBuilder.create().texOffs(153, 49).mirror()
                    .addBox(-12f, -2f, -0.5f, 12f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.2f, 8.7f, 12f, -0.2094f, 0.0873f, -0.1222f)
            )

            val EquipB07a2_1 = EquipB07a1_1.addOrReplaceChild(
                "EquipB07a2_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, -2f, -1f, 5f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-12f, 0f, 0.5f, 0f, -1.0734f, 0f)
            )

            val EquipB07d1_1 = EquipB06d_1.addOrReplaceChild(
                "EquipB07d1_1",
                CubeListBuilder.create().texOffs(153, 49).mirror()
                    .addBox(-12f, -2f, -0.5f, 12f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1f, -1.6f, 12.4f, 0f, 0.0873f, 0f)
            )

            val EquipB07d3_1 = EquipB07d1_1.addOrReplaceChild(
                "EquipB07d3_1",
                CubeListBuilder.create().texOffs(51, 0).addBox(-2f, -7f, -0.5f, 2f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1.7f, 0f, 0f, 0f, 0.0873f)
            )

            val EquipB07d2_1 = EquipB07d1_1.addOrReplaceChild(
                "EquipB07d2_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6f, -2f, -1f, 6f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-12f, 0f, 0.5f, 0f, -1.0472f, 0f)
            )

            val EquipB07c1_1 = EquipB06d_1.addOrReplaceChild(
                "EquipB07c1_1",
                CubeListBuilder.create().texOffs(153, 49).mirror()
                    .addBox(-12f, -2f, -0.5f, 12f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1f, 2.4f, 12.4f, 0f, 0.0873f, 0f)
            )

            val EquipB07c2_1 = EquipB07c1_1.addOrReplaceChild(
                "EquipB07c2_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7f, -2f, -1f, 7f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-12f, 0f, 0.5f, 0f, -1.0472f, 0f)
            )

            val EquipB07b1_1 = EquipB06d_1.addOrReplaceChild(
                "EquipB07b1_1",
                CubeListBuilder.create().texOffs(153, 49).mirror()
                    .addBox(-12f, -2f, -0.5f, 12f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1f, 5.6f, 12.4f, 0f, 0.0873f, -0.0524f)
            )

            val EquipB07b2_1 = EquipB07b1_1.addOrReplaceChild(
                "EquipB07b2_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6f, -2f, -1f, 6f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-12f, 0f, 0.5f, 0f, -1.0472f, 0f)
            )

            val EquipB06a_1 = EquipB04_1.addOrReplaceChild(
                "EquipB06a_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-9f, 0f, 0f, 9f, 4f, 11f, CubeDeformation(0f)),
                PartPose.offset(-2.5f, 3.9f, 0.5f)
            )

            val EquipB06f_1 = EquipB04_1.addOrReplaceChild(
                "EquipB06f_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 2f, 8f, 11f, CubeDeformation(0f)),
                PartPose.offset(-27f, 4f, 0.5f)
            )

            val EquipB06c_1 = EquipB04_1.addOrReplaceChild(
                "EquipB06c_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, 0f, 0f, 3f, 8f, 11f, CubeDeformation(0f)),
                PartPose.offset(-14.3f, 4f, 0.5f)
            )

            val EquipB04 = EquipB01.addOrReplaceChild(
                "EquipB04",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 5f, 4f, 12f, CubeDeformation(0f)),
                PartPose.offset(5f, -2f, -0.5f)
            )

            val EquipB05 = EquipB04.addOrReplaceChild(
                "EquipB05",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, -4.5f, 9f, 8f, 9f, CubeDeformation(0f)),
                PartPose.offset(9.4f, -3.8f, 6f)
            )

            val EquipCL1Base01 = EquipB05.addOrReplaceChild(
                "EquipCL1Base01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -4f, -1.5f, 9f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.1f, 0f, 0f, -1.5708f, 0f)
            )

            val EquipCL1Base02 = EquipCL1Base01.addOrReplaceChild(
                "EquipCL1Base02",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-4.5f, -4f, -2f, 9f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.3f, -2.8f, 0.1745f, 0f, 0f)
            )

            val EquipCL1a1_1 = EquipCL1Base01.addOrReplaceChild(
                "EquipCL1a1_1",
                CubeListBuilder.create().texOffs(19, 29).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, -2.3f, -2.5f, -0.2094f, 0f, 0f)
            )

            val EquipCL1a2_1 = EquipCL1a1_1.addOrReplaceChild(
                "EquipCL1a2_1",
                CubeListBuilder.create().texOffs(151, 67).addBox(-1f, 0f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.5708f, 0f, 0f)
            )

            val EquipCL1a1 = EquipCL1Base01.addOrReplaceChild(
                "EquipCL1a1",
                CubeListBuilder.create().texOffs(19, 29).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -2.3f, -2.5f, -0.2094f, 0f, 0f)
            )

            val EquipCL1a2 = EquipCL1a1.addOrReplaceChild(
                "EquipCL1a2",
                CubeListBuilder.create().texOffs(151, 67).addBox(-1f, 0f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.5708f, 0f, 0f)
            )

            val EquipB06e = EquipB04.addOrReplaceChild(
                "EquipB06e",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 4f, 9f, 11f, CubeDeformation(0f)),
                PartPose.offset(21.1f, 4f, 0.5f)
            )

            val EquipB06d = EquipB04.addOrReplaceChild(
                "EquipB06d",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 4f, 9f, 11f, CubeDeformation(0f)),
                PartPose.offset(17.2f, 4f, 0.5f)
            )

            val EquipB05_1 = EquipB06d.addOrReplaceChild(
                "EquipB05_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0f, -4.5f, 9f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offset(4.8f, -1.9f, 5f)
            )

            val EquipCL1Base01_1 = EquipB05_1.addOrReplaceChild(
                "EquipCL1Base01_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -4f, -1.5f, 9f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.1f, 0f)
            )

            val EquipCL1a1_2 = EquipCL1Base01_1.addOrReplaceChild(
                "EquipCL1a1_2",
                CubeListBuilder.create().texOffs(19, 29).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -2.3f, -2.5f, -0.2094f, 0f, 0f)
            )

            val EquipCL1a2_2 = EquipCL1a1_2.addOrReplaceChild(
                "EquipCL1a2_2",
                CubeListBuilder.create().texOffs(151, 67).addBox(-1f, 0f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.5708f, 0f, 0f)
            )

            val EquipCL1Base02_1 = EquipCL1Base01_1.addOrReplaceChild(
                "EquipCL1Base02_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -4f, -2f, 9f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.3f, -2.8f, 0.1745f, 0f, 0f)
            )

            val EquipCL1a1_3 = EquipCL1Base01_1.addOrReplaceChild(
                "EquipCL1a1_3",
                CubeListBuilder.create().texOffs(19, 29).addBox(-1.5f, -1.5f, -5f, 3f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, -2.3f, -2.5f, -0.1698f, 0f, 0f)
            )

            val EquipCL1a2_3 = EquipCL1a1_3.addOrReplaceChild(
                "EquipCL1a2_3",
                CubeListBuilder.create().texOffs(151, 67).addBox(-1f, 0f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5f, -1.5708f, 0f, 0f)
            )

            val EquipB07c1 = EquipB06d.addOrReplaceChild(
                "EquipB07c1",
                CubeListBuilder.create().texOffs(153, 49).addBox(0f, -2f, -0.5f, 12f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, 2.4f, 12.4f, 0f, -0.0873f, 0f)
            )

            val EquipB07c2 = EquipB07c1.addOrReplaceChild(
                "EquipB07c2",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -2f, -1f, 7f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(12f, 0f, 0.5f, 0f, 1.0472f, 0f)
            )

            val EquipB07d1 = EquipB06d.addOrReplaceChild(
                "EquipB07d1",
                CubeListBuilder.create().texOffs(153, 49).addBox(0f, -2f, -0.5f, 12f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, -1.6f, 12.4f, 0f, -0.0873f, 0f)
            )

            val EquipB07d3 = EquipB07d1.addOrReplaceChild(
                "EquipB07d3",
                CubeListBuilder.create().texOffs(51, 0).addBox(0f, -7f, -0.5f, 2f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1.7f, 0f, 0f, 0f, -0.0873f)
            )

            val EquipB07d2 = EquipB07d1.addOrReplaceChild(
                "EquipB07d2",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -2f, -1f, 6f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(12f, 0f, 0.5f, 0f, 1.0472f, 0f)
            )

            val EquipB07b1 = EquipB06d.addOrReplaceChild(
                "EquipB07b1",
                CubeListBuilder.create().texOffs(153, 49).addBox(0f, -2f, -0.5f, 12f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, 5.6f, 12.4f, 0f, -0.0873f, 0.0524f)
            )

            val EquipB07b2 = EquipB07b1.addOrReplaceChild(
                "EquipB07b2",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -2f, -1f, 6f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(12f, 0f, 0.5f, 0f, 1.0472f, 0f)
            )

            val EquipB07a1 = EquipB06d.addOrReplaceChild(
                "EquipB07a1",
                CubeListBuilder.create().texOffs(153, 49).addBox(0f, -2f, -0.5f, 12f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.2f, 8.7f, 12f, -0.2094f, -0.0873f, 0.1222f)
            )

            val EquipB07a2 = EquipB07a1.addOrReplaceChild(
                "EquipB07a2",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -2f, -1f, 5f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(12f, 0f, 0.5f, 0f, 1.0734f, 0f)
            )

            val EquipB06a = EquipB04.addOrReplaceChild(
                "EquipB06a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 9f, 4f, 11f, CubeDeformation(0f)),
                PartPose.offset(2.5f, 3.9f, 0.5f)
            )

            val EquipB06b = EquipB04.addOrReplaceChild(
                "EquipB06b",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 3f, 6f, 11f, CubeDeformation(0f)),
                PartPose.offset(11.4f, 4f, 0.5f)
            )

            val EquipB06c = EquipB04.addOrReplaceChild(
                "EquipB06c",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 3f, 8f, 11f, CubeDeformation(0f)),
                PartPose.offset(14.3f, 4f, 0.5f)
            )

            val EquipB06f = EquipB04.addOrReplaceChild(
                "EquipB06f",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 2f, 8f, 11f, CubeDeformation(0f)),
                PartPose.offset(25f, 4f, 0.5f)
            )

            val EquipB00a = EquipBase.addOrReplaceChild(
                "EquipB00a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 6f, 4f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.2f, -5.5f, -1f, 0f, 0.2618f, 0f)
            )

            val EquipB00b = EquipB00a.addOrReplaceChild(
                "EquipB00b",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, -2f, 8f, 4f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, -0.1f, 2f, 0f, 1.309f, 0f)
            )

            val EquipB00c = EquipB00b.addOrReplaceChild(
                "EquipB00c",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.7f, 2f, -0.3f, 0f, 0f, 0.6109f)
            )

            val EquipB00d = EquipB00c.addOrReplaceChild(
                "EquipB00d",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, -4f, -0.5f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 1.1f)
            )

            val EquipB00a_1 = EquipBase.addOrReplaceChild(
                "EquipB00a_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6f, 0f, 0f, 6f, 4f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.2f, -5.5f, -1f, 0f, -0.2618f, 0f)
            )

            val EquipB00b_1 = EquipB00a_1.addOrReplaceChild(
                "EquipB00b_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-8f, 0f, -2f, 8f, 4f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, -0.1f, 2f, 0f, -1.309f, 0f)
            )

            val EquipB00c_1 = EquipB00b_1.addOrReplaceChild(
                "EquipB00c_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5.7f, 2f, -0.3f, 0f, 0f, -0.6109f)
            )

            val EquipB00d_1 = EquipB00c_1.addOrReplaceChild(
                "EquipB00d_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, -4f, -0.5f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 1.1f)
            )

            val Cloth03a2 = BodyMain.addOrReplaceChild(
                "Cloth03a2",
                CubeListBuilder.create().texOffs(159, 55).mirror()
                    .addBox(-1f, 0f, 0f, 2f, 18f, 7f, CubeDeformation(0f)),
                PartPose.offset(-4.1f, -11.1f, -4.1f)
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

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 88).addBox(-7.5f, 0f, -5.7f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.3f, 0.3491f, 0f, 0f)
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

            val Cloth02b1 = SkirtB01.addOrReplaceChild(
                "Cloth02b1",
                CubeListBuilder.create().texOffs(59, 0).addBox(-1.5f, 0f, 0f, 3f, 5f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, 1.8f, -4.9f, -0.5585f, 0f, 0.0698f)
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

            val Cloth01a = SkirtB01.addOrReplaceChild(
                "Cloth01a",
                CubeListBuilder.create().texOffs(81, 0).addBox(-1f, -2.5f, -1f, 2f, 3f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.3f, -5f, -0.2618f, 0f, 0f)
            )

            val Cloth01c2 = Cloth01a.addOrReplaceChild(
                "Cloth01c2",
                CubeListBuilder.create().texOffs(73, 5).mirror().addBox(-1.5f, 0f, 0f, 3f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -0.4f, -0.7f, -0.2618f, -0.1396f, -0.1745f)
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

            val Cloth01b = Cloth01a.addOrReplaceChild(
                "Cloth01b",
                CubeListBuilder.create().texOffs(65, 0).addBox(-6f, -3f, -1f, 6f, 3f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, 0.3f, 0.0873f, -0.1745f, -0.3491f)
            )

            val Cloth02a1 = SkirtB01.addOrReplaceChild(
                "Cloth02a1",
                CubeListBuilder.create().texOffs(59, 0).addBox(-1.5f, 0f, 0f, 3f, 5f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, 1.8f, -4.9f, -0.5585f, 0f, -0.0698f)
            )

            val Cloth02a2 = Cloth02a1.addOrReplaceChild(
                "Cloth02a2",
                CubeListBuilder.create().texOffs(59, 0).addBox(-1.5f, 0f, 0f, 3f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4.9f, 0f, 0.1745f, 0f, 0.0524f)
            )

            val Cloth02a3 = Cloth02a2.addOrReplaceChild(
                "Cloth02a3",
                CubeListBuilder.create().texOffs(59, 0).addBox(-1.5f, 0f, 0f, 3f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.9f, 0f, 0f, 0f, 0.0524f)
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

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(24, 71).addBox(-2f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.3f, -0.7f, 0.1745f, 0f, -0.3142f)
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
                PartPose.offset(0.1f, -0.1f, -2.2f)
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
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, -0.7f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 77).addBox(-8f, -8f, -7.4f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0.1f)
            )

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(52, 56).addBox(-8.5f, 0f, 0f, 17f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -7f)
            )

            val HairS01 = Hair.addOrReplaceChild(
                "HairS01",
                CubeListBuilder.create().texOffs(110, 22).addBox(-1.5f, -3f, -3f, 3f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8.8f, 3.1f, 3.3f, 0f, 0.0524f, 0f)
            )

            val HairCBase = Hair.addOrReplaceChild(
                "HairCBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, 1f, -1.6f, 0f, 0f, -0.3142f)
            )

            val HairC01 = HairCBase.addOrReplaceChild(
                "HairC01",
                CubeListBuilder.create().texOffs(40, 0).addBox(0f, 0f, 0f, 2f, 4f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -6f, -7f, 1.3963f, -0.1396f, 0f)
            )

            val HairC02 = HairC01.addOrReplaceChild(
                "HairC02",
                CubeListBuilder.create().texOffs(40, 0).addBox(0f, 0f, 0f, 2f, 10f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -1.3439f, 0f, 0f)
            )

            val HairC03 = HairC02.addOrReplaceChild(
                "HairC03",
                CubeListBuilder.create().texOffs(40, 0).addBox(0f, 0f, 0f, 2f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10f, 0f, 0.5009f, 0f, -0.8727f)
            )

            val HairC04 = HairC03.addOrReplaceChild(
                "HairC04",
                CubeListBuilder.create().texOffs(40, 0).addBox(0f, 0f, 0f, 2f, 11f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 1.0472f, 0f, 0f)
            )

            val HairC05 = HairC04.addOrReplaceChild(
                "HairC05",
                CubeListBuilder.create().texOffs(40, 0).addBox(0f, 0f, 0f, 2f, 5f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 11f, 0f, 1.7453f, 0f, 0f)
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

            val HairC01b = HairCBaseB.addOrReplaceChild(
                "HairC01b",
                CubeListBuilder.create().texOffs(40, 0).addBox(-2f, 0f, 0f, 2f, 4f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -6f, -7f, 1.3963f, 0.1396f, 0f)
            )

            val HairC02b = HairC01b.addOrReplaceChild(
                "HairC02b",
                CubeListBuilder.create().texOffs(40, 0).addBox(-2f, 0f, 0f, 2f, 10f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -1.3439f, 0f, 0f)
            )

            val HairC03b = HairC02b.addOrReplaceChild(
                "HairC03b",
                CubeListBuilder.create().texOffs(40, 0).addBox(-2f, 0f, 0f, 2f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10f, 0f, 0.5009f, 0f, 0.8727f)
            )

            val HairC04b = HairC03b.addOrReplaceChild(
                "HairC04b",
                CubeListBuilder.create().texOffs(40, 0).addBox(-2f, 0f, 0f, 2f, 11f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 1.0472f, 0f, 0f)
            )

            val HairC05b = HairC04b.addOrReplaceChild(
                "HairC05b",
                CubeListBuilder.create().texOffs(40, 0).addBox(-2f, 0f, 0f, 2f, 5f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 11f, 0f, 1.7453f, 0f, 0f)
            )

            val HairS02 = Hair.addOrReplaceChild(
                "HairS02",
                CubeListBuilder.create().texOffs(110, 22).addBox(-1.5f, -3f, -3f, 3f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8.8f, 3.1f, 3.3f, 0f, -0.0524f, 0f)
            )

            val EquipHeadBase = Head.addOrReplaceChild(
                "EquipHeadBase",
                CubeListBuilder.create().texOffs(33, 16).addBox(-8f, 0f, 0f, 16f, 2f, 15f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.8f, -7.6f)
            )

            val EquipHead01a = EquipHeadBase.addOrReplaceChild(
                "EquipHead01a",
                CubeListBuilder.create().texOffs(36, 108).addBox(-8f, 0f, 0f, 8f, 1f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.5f, 0.2f, 7f, 0f, 0f, 0.0873f)
            )

            val EquipHead03a = EquipHead01a.addOrReplaceChild(
                "EquipHead03a",
                CubeListBuilder.create().texOffs(40, 105).addBox(-7f, 0f, 0f, 7f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offset(0.2f, 0.9f, 0.5f)
            )

            val EquipHead02a = EquipHead01a.addOrReplaceChild(
                "EquipHead02a",
                CubeListBuilder.create().texOffs(44, 82).addBox(-7f, 0f, 1f, 7f, 2f, 0f, CubeDeformation(0f)),
                PartPose.offset(-0.4f, -1.9f, 0f)
            )

            val EquipHead01 = EquipHeadBase.addOrReplaceChild(
                "EquipHead01",
                CubeListBuilder.create().texOffs(36, 108).addBox(0f, 0f, 0f, 8f, 1f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.5f, 0.2f, 7f, 0f, 0f, -0.0873f)
            )

            val EquipHead03 = EquipHead01.addOrReplaceChild(
                "EquipHead03",
                CubeListBuilder.create().texOffs(40, 105).addBox(0f, 0f, 0f, 7f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offset(0.2f, 0.9f, 0.5f)
            )

            val EquipHead02 = EquipHead01.addOrReplaceChild(
                "EquipHead02",
                CubeListBuilder.create().texOffs(44, 82).addBox(0f, 0f, 1f, 7f, 2f, 0f, CubeDeformation(0f)),
                PartPose.offset(0.4f, -1.9f, 0f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(46, 104).addBox(-7.5f, 0f, 0f, 15f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.8f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(80, 0).addBox(-7.5f, 0f, 0f, 15f, 13f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1f, 0.2618f, 0f, 0f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(52, 35).addBox(-8f, 0f, -5f, 16f, 13f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10.5f, 5.7f, -0.1745f, 0f, 0f)
            )

            val Ahoke00 = Head.addOrReplaceChild(
                "Ahoke00",
                CubeListBuilder.create().texOffs(100, 28).addBox(0f, -9f, 0f, 0f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.6f, -13f, -4f, 0.6632f, 0.5236f, 0f)
            )

            val Ahoke01 = Head.addOrReplaceChild(
                "Ahoke01",
                CubeListBuilder.create().texOffs(44, 0).addBox(-1.5f, 0f, 0f, 3f, 5f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, -5f, 2.7053f, -2.8798f, 0f)
            )

            val Ahoke02 = Ahoke01.addOrReplaceChild(
                "Ahoke02",
                CubeListBuilder.create().texOffs(44, 0).addBox(-1.5f, 0f, 0f, 3f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, 0f, 1.2217f, 0f, 0f)
            )

            val Ahoke03 = Ahoke02.addOrReplaceChild(
                "Ahoke03",
                CubeListBuilder.create().texOffs(44, 0).addBox(-1.5f, 0f, 0f, 3f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 1.4835f, 0f, 0f)
            )

            val Ahoke04 = Ahoke03.addOrReplaceChild(
                "Ahoke04",
                CubeListBuilder.create().texOffs(44, 0).addBox(-1.5f, 0f, 0f, 3f, 4f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, 0f, 0.9599f, 0f, 0f)
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
