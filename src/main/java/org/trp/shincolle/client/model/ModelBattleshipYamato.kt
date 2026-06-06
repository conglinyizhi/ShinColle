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
import org.trp.shincolle.entity.EntityBattleshipYamato
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelBattleshipYamato<T : EntityShipBase>(root: ModelPart) : ShipModelHumanoidBase<T>(), IGlowableModel {
    private var isDeadPose = false
    private var isSittingPose = false
    override val poseTranslateY = 0f

    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val Butt: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val Cloth01: ModelPart
    private val EquipBaseBelt: ModelPart?
    private val Head: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val EquipHeadBase: ModelPart?
    private val Ahoke: ModelPart
    private val HairL01: ModelPart
    private val HairR01: ModelPart
    private val HairU01: ModelPart
    private val HairL02: ModelPart
    private val HairL03: ModelPart
    private val HairR02: ModelPart
    private val HairR03: ModelPart
    private val HairBase: ModelPart
    private val Hair00: ModelPart
    private val Hair01: ModelPart
    private val Hair02: ModelPart
    private val Hair03: ModelPart
    private val Hair04: ModelPart
    private val HeadEquip01a: ModelPart
    private val HeadEquip02a: ModelPart
    private val HeadEquip01b: ModelPart
    private val HeadEquip01c: ModelPart
    private val HeadEquip01d: ModelPart
    private val HeadEquip01b2: ModelPart
    private val HeadEquip02b: ModelPart
    private val HeadEquip02c: ModelPart
    private val HeadEquip02d: ModelPart
    private val HeadEquip02b2: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft01: ModelPart
    private val Skirt01: ModelPart
    private val AnchorL: ModelPart
    private val AnchorR: ModelPart
    private val LegRight02: ModelPart
    private val EquipLegR01: ModelPart?
    private val ShoesR01: ModelPart
    private val EquipLegR02a: ModelPart
    private val EquipLegR02b: ModelPart
    private val EquipLegR02c: ModelPart
    private val LegLeft02: ModelPart
    private val EquipLegL01: ModelPart?
    private val ShoesL01: ModelPart
    private val EquipLegL02a: ModelPart
    private val EquipLegL02b: ModelPart
    private val EquipLegL02c: ModelPart
    private val Skirt02: ModelPart
    private val ArmLeft01a: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmRight02: ModelPart
    private val EquipU01: ModelPart?
    private val EquipU01a: ModelPart
    private val EquipU01b: ModelPart
    private val EquipU02: ModelPart
    private val EquipU03a: ModelPart
    private val EquipU04a: ModelPart
    private val EquipU05a: ModelPart
    private val EquipU06: ModelPart
    private val EquipU09a: ModelPart
    private val EquipU09b: ModelPart
    private val EquipU09c: ModelPart
    private val EquipU03b: ModelPart
    private val EquipU03c: ModelPart
    private val EquipU03d: ModelPart
    private val EquipU04b: ModelPart
    private val EquipU04c: ModelPart
    private val EquipU04d: ModelPart
    private val EquipU05b: ModelPart
    private val EquipU05c: ModelPart
    private val EquipU05d: ModelPart
    private val EquipU07: ModelPart
    private val EquipU08: ModelPart
    private val Cloth02a: ModelPart
    private val Cloth02b: ModelPart
    private val EquipRotateBase: ModelPart
    private val EquipBaseBelt2: ModelPart
    private val EquipBaseM01a: ModelPart
    private val EquipBaseM01b: ModelPart
    private val EquipL01: ModelPart
    private val EquipR01: ModelPart
    private val EquipBaseM02: ModelPart
    private val EquipL02: ModelPart
    private val EquipL03: ModelPart
    private val EquipL04: ModelPart
    private val EquipLCBase01: ModelPart
    private val EquipL05: ModelPart
    private val EquipLC2Base01: ModelPart
    private val EquipLC3Base01: ModelPart
    private val EquipLC2Base02: ModelPart
    private val EquipLC201a: ModelPart
    private val EquipLC202a: ModelPart
    private val EquipLC203a: ModelPart
    private val EquipLC2Radar01: ModelPart
    private val EquipLC2Radar02: ModelPart
    private val EquipLC201b: ModelPart
    private val EquipLC202b: ModelPart
    private val EquipLC203b: ModelPart
    private val EquipLC3Base02: ModelPart
    private val EquipLC301a: ModelPart
    private val EquipLC302a: ModelPart
    private val EquipLC303a: ModelPart
    private val EquipLC3Radar01: ModelPart
    private val EquipLC3Radar02: ModelPart
    private val EquipLC301b: ModelPart
    private val EquipLC302b: ModelPart
    private val EquipLC303b: ModelPart
    private val EquipLCBase02: ModelPart
    private val EquipLC01a: ModelPart
    private val EquipLC02a: ModelPart
    private val EquipLC03a: ModelPart
    private val EquipLCRadar01: ModelPart
    private val EquipLCRadar02: ModelPart
    private val EquipLC01b: ModelPart
    private val EquipLC02b: ModelPart
    private val EquipLC03b: ModelPart
    private val EquipR02: ModelPart
    private val EquipMCBase01a: ModelPart
    private val EquipMCBase01b: ModelPart
    private val EquipR03: ModelPart
    private val EquipRCBase01: ModelPart
    private val EquipR04: ModelPart
    private val EquipRCBase02: ModelPart
    private val EquipRC01a: ModelPart
    private val EquipRC02a: ModelPart
    private val EquipRC03a: ModelPart
    private val EquipRCRadar01: ModelPart
    private val EquipRCRadar02: ModelPart
    private val EquipRC01b: ModelPart
    private val EquipRC02b: ModelPart
    private val EquipRC03b: ModelPart
    private val EquipR05: ModelPart
    private val EquipRC2Base01: ModelPart
    private val EquipRC3Base01: ModelPart
    private val EquipRC2Base02: ModelPart
    private val EquipRC201a: ModelPart
    private val EquipRC202a: ModelPart
    private val EquipRC203a: ModelPart
    private val EquipRC2Radar01: ModelPart
    private val EquipRC2Radar02: ModelPart
    private val EquipRC201b: ModelPart
    private val EquipRC202b: ModelPart
    private val EquipRC203b: ModelPart
    private val EquipRC3Base02: ModelPart
    private val EquipRC301a: ModelPart
    private val EquipRC302a: ModelPart
    private val EquipRC303a: ModelPart
    private val EquipRC3Radar01: ModelPart
    private val EquipRC3Radar02: ModelPart
    private val EquipRC301b: ModelPart
    private val EquipRC302b: ModelPart
    private val EquipRC303b: ModelPart
    private val EquipLCBase01_1: ModelPart
    private val EquipLCBase02_1: ModelPart
    private val EquipLC01a_1: ModelPart
    private val EquipLC02a_1: ModelPart
    private val EquipLC03a_1: ModelPart
    private val EquipMCRadar01: ModelPart
    private val EquipMCRadar02: ModelPart
    private val EquipLC01b_1: ModelPart
    private val EquipLC02b_1: ModelPart
    private val EquipLC03b_1: ModelPart
    private val EquipBaseM03: ModelPart
    private val GlowBodyMain: ModelPart?
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val legLeft02DefaultZ: Float
    private val legRight02DefaultZ: Float
    private val armLeft02DefaultX: Float
    private val armLeft02DefaultY: Float
    private val armLeft02DefaultZ: Float
    private val armRight02DefaultX: Float
    private val armRight02DefaultY: Float
    private val armRight02DefaultZ: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.ArmLeft01a = this.ArmLeft01.getChild("ArmLeft01a")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.Cloth02a = this.Cloth01.getChild("Cloth02a")
        this.Cloth02b = this.Cloth02a.getChild("Cloth02b")
        this.EquipBaseBelt = this.BodyMain.getChild("EquipBaseBelt")
        this.EquipRotateBase = this.EquipBaseBelt.getChild("EquipRotateBase")
        this.EquipBaseM01a = this.EquipRotateBase.getChild("EquipBaseM01a")
        this.EquipBaseM01b = this.EquipRotateBase.getChild("EquipBaseM01b")
        this.EquipR01 = this.EquipBaseM01b.getChild("EquipR01")
        this.EquipMCBase01b = this.EquipR01.getChild("EquipMCBase01b")
        this.EquipR02 = this.EquipR01.getChild("EquipR02")
        this.EquipR03 = this.EquipR02.getChild("EquipR03")
        this.EquipRCBase01 = this.EquipR03.getChild("EquipRCBase01")
        this.EquipRCBase02 = this.EquipRCBase01.getChild("EquipRCBase02")
        this.EquipRCRadar02 = this.EquipRCBase02.getChild("EquipRCRadar02")
        this.EquipRCRadar01 = this.EquipRCBase02.getChild("EquipRCRadar01")
        this.EquipRC02a = this.EquipRCBase02.getChild("EquipRC02a")
        this.EquipRC02b = this.EquipRC02a.getChild("EquipRC02b")
        this.EquipRC03a = this.EquipRCBase02.getChild("EquipRC03a")
        this.EquipRC03b = this.EquipRC03a.getChild("EquipRC03b")
        this.EquipRC01a = this.EquipRCBase02.getChild("EquipRC01a")
        this.EquipRC01b = this.EquipRC01a.getChild("EquipRC01b")
        this.EquipR04 = this.EquipR03.getChild("EquipR04")
        this.EquipRC3Base01 = this.EquipR04.getChild("EquipRC3Base01")
        this.EquipRC3Base02 = this.EquipRC3Base01.getChild("EquipRC3Base02")
        this.EquipRC302a = this.EquipRC3Base02.getChild("EquipRC302a")
        this.EquipRC302b = this.EquipRC302a.getChild("EquipRC302b")
        this.EquipRC3Radar02 = this.EquipRC3Base02.getChild("EquipRC3Radar02")
        this.EquipRC303a = this.EquipRC3Base02.getChild("EquipRC303a")
        this.EquipRC303b = this.EquipRC303a.getChild("EquipRC303b")
        this.EquipRC3Radar01 = this.EquipRC3Base02.getChild("EquipRC3Radar01")
        this.EquipRC301a = this.EquipRC3Base02.getChild("EquipRC301a")
        this.EquipRC301b = this.EquipRC301a.getChild("EquipRC301b")
        this.EquipRC2Base01 = this.EquipR04.getChild("EquipRC2Base01")
        this.EquipRC2Base02 = this.EquipRC2Base01.getChild("EquipRC2Base02")
        this.EquipRC2Radar02 = this.EquipRC2Base02.getChild("EquipRC2Radar02")
        this.EquipRC2Radar01 = this.EquipRC2Base02.getChild("EquipRC2Radar01")
        this.EquipRC203a = this.EquipRC2Base02.getChild("EquipRC203a")
        this.EquipRC203b = this.EquipRC203a.getChild("EquipRC203b")
        this.EquipRC202a = this.EquipRC2Base02.getChild("EquipRC202a")
        this.EquipRC202b = this.EquipRC202a.getChild("EquipRC202b")
        this.EquipRC201a = this.EquipRC2Base02.getChild("EquipRC201a")
        this.EquipRC201b = this.EquipRC201a.getChild("EquipRC201b")
        this.EquipR05 = this.EquipR04.getChild("EquipR05")
        this.EquipMCBase01a = this.EquipR01.getChild("EquipMCBase01a")
        this.EquipLCBase01_1 = this.EquipMCBase01a.getChild("EquipLCBase01_1")
        this.EquipLCBase02_1 = this.EquipLCBase01_1.getChild("EquipLCBase02_1")
        this.EquipMCRadar02 = this.EquipLCBase02_1.getChild("EquipMCRadar02")
        this.EquipLC02a_1 = this.EquipLCBase02_1.getChild("EquipLC02a_1")
        this.EquipLC02b_1 = this.EquipLC02a_1.getChild("EquipLC02b_1")
        this.EquipLC01a_1 = this.EquipLCBase02_1.getChild("EquipLC01a_1")
        this.EquipLC01b_1 = this.EquipLC01a_1.getChild("EquipLC01b_1")
        this.EquipLC03a_1 = this.EquipLCBase02_1.getChild("EquipLC03a_1")
        this.EquipLC03b_1 = this.EquipLC03a_1.getChild("EquipLC03b_1")
        this.EquipMCRadar01 = this.EquipLCBase02_1.getChild("EquipMCRadar01")
        this.EquipL01 = this.EquipBaseM01b.getChild("EquipL01")
        this.EquipL02 = this.EquipL01.getChild("EquipL02")
        this.EquipL03 = this.EquipL02.getChild("EquipL03")
        this.EquipLCBase01 = this.EquipL03.getChild("EquipLCBase01")
        this.EquipLCBase02 = this.EquipLCBase01.getChild("EquipLCBase02")
        this.EquipLC02a = this.EquipLCBase02.getChild("EquipLC02a")
        this.EquipLC02b = this.EquipLC02a.getChild("EquipLC02b")
        this.EquipLCRadar02 = this.EquipLCBase02.getChild("EquipLCRadar02")
        this.EquipLCRadar01 = this.EquipLCBase02.getChild("EquipLCRadar01")
        this.EquipLC03a = this.EquipLCBase02.getChild("EquipLC03a")
        this.EquipLC03b = this.EquipLC03a.getChild("EquipLC03b")
        this.EquipLC01a = this.EquipLCBase02.getChild("EquipLC01a")
        this.EquipLC01b = this.EquipLC01a.getChild("EquipLC01b")
        this.EquipL04 = this.EquipL03.getChild("EquipL04")
        this.EquipLC2Base01 = this.EquipL04.getChild("EquipLC2Base01")
        this.EquipLC2Base02 = this.EquipLC2Base01.getChild("EquipLC2Base02")
        this.EquipLC203a = this.EquipLC2Base02.getChild("EquipLC203a")
        this.EquipLC203b = this.EquipLC203a.getChild("EquipLC203b")
        this.EquipLC2Radar02 = this.EquipLC2Base02.getChild("EquipLC2Radar02")
        this.EquipLC2Radar01 = this.EquipLC2Base02.getChild("EquipLC2Radar01")
        this.EquipLC201a = this.EquipLC2Base02.getChild("EquipLC201a")
        this.EquipLC201b = this.EquipLC201a.getChild("EquipLC201b")
        this.EquipLC202a = this.EquipLC2Base02.getChild("EquipLC202a")
        this.EquipLC202b = this.EquipLC202a.getChild("EquipLC202b")
        this.EquipLC3Base01 = this.EquipL04.getChild("EquipLC3Base01")
        this.EquipLC3Base02 = this.EquipLC3Base01.getChild("EquipLC3Base02")
        this.EquipLC3Radar02 = this.EquipLC3Base02.getChild("EquipLC3Radar02")
        this.EquipLC3Radar01 = this.EquipLC3Base02.getChild("EquipLC3Radar01")
        this.EquipLC303a = this.EquipLC3Base02.getChild("EquipLC303a")
        this.EquipLC303b = this.EquipLC303a.getChild("EquipLC303b")
        this.EquipLC302a = this.EquipLC3Base02.getChild("EquipLC302a")
        this.EquipLC302b = this.EquipLC302a.getChild("EquipLC302b")
        this.EquipLC301a = this.EquipLC3Base02.getChild("EquipLC301a")
        this.EquipLC301b = this.EquipLC301a.getChild("EquipLC301b")
        this.EquipL05 = this.EquipL04.getChild("EquipL05")
        this.EquipBaseM02 = this.EquipBaseM01b.getChild("EquipBaseM02")
        this.EquipBaseM03 = this.EquipBaseM02.getChild("EquipBaseM03")
        this.EquipBaseBelt2 = this.EquipBaseBelt.getChild("EquipBaseBelt2")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.EquipU01 = this.ArmRight02.getChild("EquipU01")
        this.EquipU01a = this.EquipU01.getChild("EquipU01a")
        this.EquipU02 = this.EquipU01.getChild("EquipU02")
        this.EquipU09a = this.EquipU02.getChild("EquipU09a")
        this.EquipU06 = this.EquipU02.getChild("EquipU06")
        this.EquipU07 = this.EquipU06.getChild("EquipU07")
        this.EquipU08 = this.EquipU07.getChild("EquipU08")
        this.EquipU09b = this.EquipU02.getChild("EquipU09b")
        this.EquipU04a = this.EquipU02.getChild("EquipU04a")
        this.EquipU04b = this.EquipU04a.getChild("EquipU04b")
        this.EquipU04c = this.EquipU04b.getChild("EquipU04c")
        this.EquipU04d = this.EquipU04c.getChild("EquipU04d")
        this.EquipU03a = this.EquipU02.getChild("EquipU03a")
        this.EquipU03b = this.EquipU03a.getChild("EquipU03b")
        this.EquipU03c = this.EquipU03b.getChild("EquipU03c")
        this.EquipU03d = this.EquipU03c.getChild("EquipU03d")
        this.EquipU05a = this.EquipU02.getChild("EquipU05a")
        this.EquipU05b = this.EquipU05a.getChild("EquipU05b")
        this.EquipU05c = this.EquipU05b.getChild("EquipU05c")
        this.EquipU05d = this.EquipU05c.getChild("EquipU05d")
        this.EquipU09c = this.EquipU02.getChild("EquipU09c")
        this.EquipU01b = this.EquipU01.getChild("EquipU01b")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.EquipHeadBase = this.Head.getChild("EquipHeadBase")
        this.HeadEquip02a = this.EquipHeadBase.getChild("HeadEquip02a")
        this.HeadEquip02d = this.HeadEquip02a.getChild("HeadEquip02d")
        this.HeadEquip02b = this.HeadEquip02a.getChild("HeadEquip02b")
        this.HeadEquip02b2 = this.HeadEquip02b.getChild("HeadEquip02b2")
        this.HeadEquip02c = this.HeadEquip02a.getChild("HeadEquip02c")
        this.HeadEquip01a = this.EquipHeadBase.getChild("HeadEquip01a")
        this.HeadEquip01d = this.HeadEquip01a.getChild("HeadEquip01d")
        this.HeadEquip01c = this.HeadEquip01a.getChild("HeadEquip01c")
        this.HeadEquip01b = this.HeadEquip01a.getChild("HeadEquip01b")
        this.HeadEquip01b2 = this.HeadEquip01b.getChild("HeadEquip01b2")
        this.Hair = this.Head.getChild("Hair")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.HairR03 = this.HairR02.getChild("HairR03")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.HairL03 = this.HairL02.getChild("HairL03")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairMain = this.Head.getChild("HairMain")
        this.HairBase = this.HairMain.getChild("HairBase")
        this.Hair00 = this.HairBase.getChild("Hair00")
        this.Hair01 = this.Hair00.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.Hair03 = this.Hair02.getChild("Hair03")
        this.Hair04 = this.Hair03.getChild("Hair04")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.EquipLegL01 = this.LegLeft01.getChild("EquipLegL01")
        this.EquipLegL02c = this.EquipLegL01.getChild("EquipLegL02c")
        this.EquipLegL02a = this.EquipLegL01.getChild("EquipLegL02a")
        this.EquipLegL02b = this.EquipLegL01.getChild("EquipLegL02b")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.ShoesL01 = this.LegLeft02.getChild("ShoesL01")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.Skirt02 = this.Skirt01.getChild("Skirt02")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.EquipLegR01 = this.LegRight01.getChild("EquipLegR01")
        this.EquipLegR02a = this.EquipLegR01.getChild("EquipLegR02a")
        this.EquipLegR02c = this.EquipLegR01.getChild("EquipLegR02c")
        this.EquipLegR02b = this.EquipLegR01.getChild("EquipLegR02b")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.ShoesR01 = this.LegRight02.getChild("ShoesR01")
        this.AnchorL = this.Butt.getChild("AnchorL")
        this.AnchorR = this.Butt.getChild("AnchorR")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        initFaceParts(this.GlowHead)
        this.legLeft02DefaultZ = this.LegLeft02.z
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

        applyBasePose(ctx, ageInTicks, headPitch, limbSwingAmount)
        applySpecialPoseAdjustments(entity, ctx, ageInTicks, limbSwingAmount)
        applyHairAnimation(ctx, ageInTicks, limbSwing, limbSwingAmount)

        syncGlowParts()
    }

    private fun resetPoseState() {
        this.isDeadPose = false
        this.isSittingPose = false
        this.poseTranslateY = 0.0f
    }

    private fun resetOffsets() {
        LegLeft02.z = legLeft02DefaultZ
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

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) return
        if (EquipBaseBelt != null) EquipBaseBelt.visible = entity.getEquipFlag(EntityBattleshipYamato.EQUIP_BELT)
        if (EquipHeadBase != null) EquipHeadBase.visible = entity.getEquipFlag(EntityBattleshipYamato.EQUIP_HEAD_BASE)
        if (EquipU01 != null) EquipU01.visible = entity.getEquipFlag(EntityBattleshipYamato.EQUIP_UPPER)
        val showLeg = entity.getEquipFlag(EntityBattleshipYamato.EQUIP_LEG)
        if (EquipLegR01 != null) EquipLegR01.visible = showLeg
        if (EquipLegL01 != null) EquipLegL01.visible = showLeg
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        Head.xRot = -0.2618f
        Head.yRot = 0.0f
        Head.zRot = 0.0f
        BoobL.xRot = -1.0f
        BoobR.xRot = -1.0f
        Cloth02a.xRot = -1.0f
        Ahoke.yRot = -1.0f

        BodyMain.xRot = 1.2217f
        BodyMain.yRot = 0.0f
        BodyMain.zRot = 1.2217f
        Butt.xRot = -0.05f

        ArmLeft01.xRot = -0.35f
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = -3.0f
        ArmLeft02.xRot = 0.0f

        ArmRight01.xRot = -0.35f
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = -0.35f
        ArmRight02.xRot = 0.0f
        ArmRight02.zRot = -0.8727f

        LegLeft01.xRot = -0.14f
        LegLeft01.yRot = 0.0f
        LegLeft01.zRot = 0.09f
        LegLeft02.xRot = 0.0f
        LegLeft02.zRot = 0.0f

        LegRight01.xRot = -1.2217f
        LegRight01.yRot = -0.5236f
        LegRight01.zRot = 0.0f
        LegRight02.xRot = 1.0472f
        LegRight02.zRot = 0.0f

        AnchorL.xRot = -0.2f
        AnchorR.xRot = -0.2f
        AnchorR.zRot = 0.35f

        if (EquipU01 != null) EquipU01.visible = false
        if (EquipBaseBelt != null) EquipBaseBelt.visible = false
    }

    private fun applyBasePose(ctx: PoseContext, ageInTicks: Float, headPitch: Float, limbSwingAmount: Float) {
        val angleX = ctx.angleX

        BoobL.xRot = angleX * 0.06f - 0.75f
        BoobR.xRot = angleX * 0.06f - 0.75f
        Cloth02a.xRot = angleX * 0.06f - 0.7f
        Ahoke.yRot = angleX * 0.25f + 0.45f

        BodyMain.xRot = -0.1047f
        BodyMain.yRot = 0.0f
        BodyMain.zRot = 0.0f
        Butt.xRot = 0.3142f

        ArmLeft01.xRot = ctx.angleAdd2 * 0.25f + 0.18f
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = angleX * 0.03f - 0.26f
        ArmLeft02.xRot = 0.0f

        ArmRight01.xRot = ctx.angleAdd1 * 0.25f + 0.18f
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = -angleX * 0.03f + 0.26f
        ArmRight02.xRot = 0.0f
        ArmRight02.zRot = 0.0f

        if (EquipU01 != null) EquipU01.yRot = 2.4f

        LegLeft01.yRot = 0.0f
        LegLeft01.zRot = 0.1396f
        LegLeft02.xRot = 0.0f
        LegLeft02.zRot = 0.0f

        LegRight01.yRot = 0.0f
        LegRight01.zRot = -0.1396f
        LegRight02.xRot = 0.0f
        LegRight02.zRot = 0.0f

        AnchorL.xRot = limbSwingAmount * 0.5f - 0.2f
        AnchorR.xRot = limbSwingAmount * 0.5f - 0.2f
        AnchorR.zRot = 0.35f

        EquipRotateBase.xRot = 0.0f
        EquipLCBase02_1.yRot = 3.1415f

        if (Head.xRot <= 0.0f) {
            EquipLC01a.xRot = Head.xRot * 0.7f
            EquipLC02a.xRot = Head.xRot
            EquipLC03a.xRot = Head.xRot * 0.8f
            EquipLC201a.xRot = Head.xRot * 1.2f
            EquipLC202a.xRot = Head.xRot
            EquipLC203a.xRot = Head.xRot * 0.9f
            EquipRC01a.xRot = Head.xRot * 0.9f
            EquipRC02a.xRot = Head.xRot
            EquipRC03a.xRot = Head.xRot * 0.75f
            EquipRC201a.xRot = Head.xRot * 0.85f
            EquipRC202a.xRot = Head.xRot * 1.1f
            EquipRC203a.xRot = Head.xRot
        }

        EquipLCBase02.yRot = Head.yRot * 1.3f
        EquipLC2Base02.yRot = Head.yRot * 1.45f
        EquipLC3Base02.yRot = -Head.xRot
        EquipRCBase02.yRot = Head.yRot * 1.3f
        EquipRC2Base02.yRot = Head.yRot * 1.45f
        EquipRC3Base02.yRot = Head.xRot
    }

    private fun applySpecialPoseAdjustments(entity: T?, ctx: PoseContext, ageInTicks: Float, limbSwingAmount: Float) {
        var legAddLeft = ctx.angleAdd1 * 0.5f - 0.2793f
        var legAddRight = ctx.angleAdd2 * 0.5f - 0.1396f
        val showCannon = entity != null && entity.getEquipFlag(EntityBattleshipYamato.EQUIP_BELT)
        val showUmbrella = entity != null && entity.getEquipFlag(EntityBattleshipYamato.EQUIP_UPPER)

        if (entity != null && entity.shipDepth > 0.0) {
            this.poseTranslateY += ctx.angleX * 0.05f + 0.025f
        }

        val isCrouching = entity != null && entity.isCrouching()
        val isSitting =
            ctx.isSitting || (entity != null && entity.isPassenger() && (entity.getVehicle() !is EntityMountBase))
        val isSprinting = entity != null && entity.isSprinting || limbSwingAmount > 0.1f

        if (isSprinting) {
            Hair01.xRot += limbSwingAmount * 0.25f
            ArmLeft01.zRot += limbSwingAmount * -0.25f
        }

        if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            Head.xRot -= 1.0472f
            BodyMain.xRot = 1.0472f
            Butt.xRot = -0.8378f

            ArmLeft01.xRot = -0.7f
            ArmLeft01.zRot = 0.2618f
            if (showUmbrella) {
                ArmRight01.xRot -= 1.0472f
            } else {
                ArmRight01.xRot = -0.7f
                ArmRight01.yRot = 0.0f
                ArmRight01.zRot = -0.2618f
                ArmRight02.xRot = 0.0f
            }

            Hair01.xRot = -1.2109f
            Hair01.zRot = -0.4363f
            Hair02.xRot = -0.5236f
            Hair02.zRot = -0.3491f
            Hair03.xRot = 0.0f
            Hair03.zRot = 0.4363f
            Hair04.xRot = -0.3491f
            Hair04.zRot = 0.2618f

            if (showCannon) {
                EquipRotateBase.xRot -= 1.0472f
            }
        }

        if (isSitting) {
            this.isSittingPose = true
            if (showCannon) {
                this.poseTranslateY += 0.4f * 3
                Head.xRot -= 0.2f
                BodyMain.xRot = -0.1396f
                Butt.xRot = 0.1396f
                ArmLeft01.xRot = -0.2094f
                ArmLeft01.zRot = 0.2618f
                if (showUmbrella) {
                    ArmRight01.xRot = 0.1745f
                    ArmRight01.yRot = 0.0f
                    ArmRight01.zRot = 0.1571f
                    ArmRight02.xRot = -1.4835f
                } else {
                    ArmRight01.xRot = -0.2094f
                    ArmRight01.yRot = 0.0f
                    ArmRight01.zRot = -0.2618f
                    ArmRight02.xRot = 0.0f
                }
                legAddLeft = -1.0472f
                legAddRight = -1.0472f
                LegLeft01.yRot = 0.0524f
                LegLeft01.zRot = 0.0f
                LegLeft02.z = legLeft02DefaultZ + (0.38f * OFFSET_SCALE)
                LegLeft02.xRot = 2.5831f
                LegLeft02.zRot = 0.0175f
                LegRight01.yRot = -0.0524f
                LegRight01.zRot = 0.0f
                LegRight02.z = legRight02DefaultZ + (0.38f * OFFSET_SCALE)
                LegRight02.xRot = 2.5831f
                LegRight02.zRot = -0.0175f
                EquipLCBase02_1.yRot = 0.0f
            } else if (entity != null && hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY += 0.5f * 2.5f
                Head.xRot -= 0.21f
                Head.yRot -= 0.4363f
                BodyMain.xRot = 0.2618f
                BodyMain.yRot = 0.35f
                BodyMain.zRot = 0.4363f
                Hair01.xRot = -0.95f
                Hair01.zRot = -0.2618f
                Hair02.xRot = -0.3491f
                Hair02.zRot = -0.3491f
                Hair03.xRot = -0.3491f
                Hair03.zRot = -0.3491f
                Hair04.xRot = -0.4363f
                Hair04.zRot = -0.4363f
                ArmLeft01.xRot = -0.35f
                ArmLeft01.yRot = -0.5236f
                ArmLeft01.zRot = -0.2618f
                ArmLeft02.xRot = -0.5236f
                if (showUmbrella) {
                    ArmRight01.xRot = 0.0f
                    ArmRight01.yRot = 0.0f
                    ArmRight01.zRot = -0.0524f
                    ArmRight02.xRot = -1.0472f
                } else {
                    ArmRight01.xRot = 0.0873f
                    ArmRight01.yRot = 0.0f
                    ArmRight01.zRot = 0.0873f
                    ArmRight02.xRot = -0.5236f
                }
                legAddLeft = -0.0873f
                legAddRight = -0.4363f
                LegLeft01.yRot = 0.0f
                LegLeft01.zRot = 1.0472f
                LegLeft02.xRot = 0.4363f
                LegRight01.yRot = 0.0f
                LegRight01.zRot = 0.925f
                LegRight02.xRot = 0.5236f
                EquipU01!!.yRot = 2.15f
                EquipU01.zRot = -1.85f
                AnchorR.zRot = 0.7f
            } else {
                this.poseTranslateY += SITTING_TRANSLATE_Y
                Head.xRot += 0.1047f
                BodyMain.xRot = -0.1396f
                Butt.xRot = 0.1396f
                Hair01.xRot = -0.6108f
                Hair01.zRot = -0.2618f
                Hair02.xRot = -0.4363f
                Hair02.zRot = 0.4363f
                Hair03.xRot = -0.3491f
                Hair03.zRot = 0.4363f
                Hair04.xRot = -0.5236f
                Hair04.zRot = 0.5236f
                ArmLeft01.xRot = -0.2094f
                ArmLeft01.zRot = 0.2618f
                if (showUmbrella) {
                    ArmRight01.xRot = 0.1745f
                    ArmRight01.yRot = 0.0f
                    ArmRight01.zRot = 0.1571f
                    ArmRight02.xRot = -1.4835f
                } else {
                    ArmRight01.xRot = -0.2094f
                    ArmRight01.yRot = 0.0f
                    ArmRight01.zRot = -0.2618f
                    ArmRight02.xRot = 0.0f
                }
                legAddLeft = -1.4835f
                legAddRight = -1.4835f
                LegLeft01.yRot = 0.0524f
                LegLeft01.zRot = -1.4835f
                LegLeft02.z = legLeft02DefaultZ + (0.38f * OFFSET_SCALE)
                LegLeft02.xRot = 2.1f
                LegLeft02.zRot = 0.0175f
                LegRight01.yRot = -0.0524f
                LegRight01.zRot = 1.4835f
                LegRight02.z = legRight02DefaultZ + (0.38f * OFFSET_SCALE)
                LegRight02.xRot = 1.9199f
                LegRight02.zRot = -0.0175f
            }
        }

        if (entity != null && entity.attackTick > 0) {
            ArmLeft01.xRot = -1.5708f
            ArmLeft01.yRot = -0.2f + Head.yRot
            ArmLeft01.zRot = 0.0f
        }

        val partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
        val customAttackAnim = if (entity != null) entity.getCustomAttackAnim(partialTick) else 0.0f
        if (customAttackAnim > 0.0f) {
            val f7 = Mth.sin(customAttackAnim * customAttackAnim * Math.PI.toFloat())
            val f8 = Mth.sin(Mth.sqrt(customAttackAnim) * Math.PI.toFloat())
            ArmRight01.xRot = -0.2f
            ArmRight01.yRot = 0.0f
            ArmRight01.zRot = -0.1f
            ArmRight01.xRot += -f8 * 80.0f * (Math.PI.toFloat() / 180f)
            ArmRight01.yRot += -f7 * 20.0f * (Math.PI.toFloat() / 180f) + 0.2f
            ArmRight01.zRot += -f8 * 20.0f * (Math.PI.toFloat() / 180f)
            ArmRight02.xRot = 0.0f
            ArmRight02.yRot = 0.0f
            ArmRight02.zRot = 0.0f
        }

        LegLeft01.xRot = legAddLeft
        LegRight01.xRot = legAddRight
    }

    private fun applyHairAnimation(ctx: PoseContext, ageInTicks: Float, limbSwing: Float, limbSwingAmount: Float) {
        val headZ = Head.zRot * -0.5f
        val headX = Head.xRot * -0.5f - 0.05f
        val angleX = ctx.angleX
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f)
        val angleX2 = Mth.cos(ageInTicks * 0.08f + 0.6f + limbSwing * 0.5f)
        val angleX3 = Mth.cos(ageInTicks * 0.08f + 0.9f + limbSwing * 0.5f)

        if (!this.isDeadPose && !this.isSittingPose) {
            Hair01.xRot = angleX * 0.03f - 0.7f
            Hair01.zRot = 0.0f
            Hair02.xRot = -angleX1 * 0.04f - 0.11f
            Hair02.zRot = 0.0f
            Hair03.xRot = -angleX2 * 0.07f - 0.05f
            Hair03.zRot = 0.0f
            Hair04.xRot = -angleX3 * 0.1f - 0.02f
            Hair04.zRot = 0.0f

            if (limbSwingAmount > 0.1f) {
                Hair01.xRot += limbSwingAmount * 0.25f
            }
        } else if (this.isSittingPose) {
            Hair01.xRot = -0.6108f
            Hair01.zRot = -0.2618f
            Hair02.xRot = -0.4363f
            Hair02.zRot = 0.4363f
            Hair03.xRot = -0.3491f
            Hair03.zRot = 0.4363f
            Hair04.xRot = -0.5236f
            Hair04.zRot = 0.5236f
        } else {
            Hair01.zRot = -0.36f
            Hair04.xRot = -0.35f
            Hair04.zRot = 0.1f
        }

        Hair01.xRot += headX
        Hair02.xRot += headX * 0.5f
        Hair03.xRot += headX * 0.2f
        Hair04.xRot += headX * 0.2f

        val movementShake = if (this.isSittingPose) 0f else 1.0f

        Hair01.zRot += (ctx.angleAdd1 * 0.04f * movementShake) + headZ
        Hair02.zRot += (ctx.angleAdd2 * 0.06f * movementShake) + headZ * 0.8f
        Hair03.zRot += (ctx.angleAdd2 * 0.08f * movementShake) + headZ * 0.4f
        Hair04.zRot += (ctx.angleAdd2 * 0.1f * movementShake) + headZ * 0.4f

        HairL01.zRot = headZ + 0.0873f
        HairL02.zRot = headZ * 0.8f - 0.3142f
        HairL03.zRot = headZ * 0.4f + 0.18f
        HairR01.zRot = headZ - 0.0873f
        HairR02.zRot = headZ * 0.8f + 0.25f
        HairR03.zRot = headZ * 0.4f - 0.15f

        HairL01.xRot = (angleX * 0.04f * movementShake) + headX - 0.28f
        HairL02.xRot = (angleX1 * 0.05f * movementShake) + headX * 0.8f + 0.15f
        HairL03.xRot = (angleX2 * 0.07f * movementShake) + headX * 0.4f + 0.05f
        HairR01.xRot = (angleX * 0.04f * movementShake) + headX - 0.35f
        HairR02.xRot = (angleX1 * 0.05f * movementShake) + headX * 0.8f + 0.18f
        HairR03.xRot = (angleX2 * 0.07f * movementShake) + headX * 0.4f + 0.02f
    }

    private fun syncGlowParts() {
        if (this.GlowBodyMain != null) {
            GlowBodyMain.copyFrom(BodyMain)
            GlowNeck.copyFrom(Neck)
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "battleship_yamato"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelBattleshipYamato")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelBattleshipYamato")
        private val SITTING_TRANSLATE_Y = sittingY("ModelBattleshipYamato")

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
                CubeListBuilder.create().texOffs(33, 101).mirror()
                    .addBox(-3.5f, 0f, 0f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.7f, -8.5f, -3.5f, -0.6981f, 0.1396f, 0.0873f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(0, 29).mirror()
                    .addBox(-2f, -1f, -2.5f, 5f, 14f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.7f, -0.7f, 0.2094f, 0f, -0.2618f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(20, 29).mirror()
                    .addBox(-5f, 0f, -5f, 5f, 14f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 13f, 2.5f)
            )

            val ArmLeft01a = ArmLeft01.addOrReplaceChild(
                "ArmLeft01a",
                CubeListBuilder.create().texOffs(25, 69).mirror().addBox(-3f, 0f, -3f, 6f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offset(0.5f, 5.5f, 0f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(33, 101).addBox(-3.5f, 0f, 0f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.7f, -8.5f, -3.5f, -0.6981f, -0.1396f, -0.0873f)
            )

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6f, 0f, -4f, 12f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.3f, -0.3f)
            )

            val Cloth02a = Cloth01.addOrReplaceChild(
                "Cloth02a",
                CubeListBuilder.create().texOffs(21, 62).addBox(-3.5f, 0f, 0f, 7f, 4f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3.2f, -4f, -0.6981f, 0f, 0f)
            )

            val Cloth02b = Cloth02a.addOrReplaceChild(
                "Cloth02b",
                CubeListBuilder.create().texOffs(24, 66).addBox(-1f, 0f, 0f, 2f, 3f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 0f, 0.9425f, 0f, 0f)
            )

            val EquipBaseBelt = BodyMain.addOrReplaceChild(
                "EquipBaseBelt",
                CubeListBuilder.create().texOffs(66, 0).addBox(-8f, 0.7f, -2f, 16f, 4f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, -2.5f, 0.1047f, 0f, 0f)
            )

            val EquipRotateBase = EquipBaseBelt.addOrReplaceChild(
                "EquipRotateBase",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 10f)
            )

            val EquipBaseM01a = EquipRotateBase.addOrReplaceChild(
                "EquipBaseM01a",
                CubeListBuilder.create().texOffs(128, 0).addBox(2.5f, 0f, -1f, 5f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -1f, 0.8727f, 0f, 0f)
            )

            val EquipBaseM01b = EquipRotateBase.addOrReplaceChild(
                "EquipBaseM01b",
                CubeListBuilder.create().texOffs(128, 0).addBox(-7.5f, 0f, -1f, 5f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -1f, 0.8727f, 0f, 0f)
            )

            val EquipR01 = EquipBaseM01b.addOrReplaceChild(
                "EquipR01",
                CubeListBuilder.create().texOffs(128, 0).addBox(-16f, 0f, 0f, 16f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.5f, 3f, -0.8727f, 0f, 0f)
            )

            val EquipMCBase01b = EquipR01.addOrReplaceChild(
                "EquipMCBase01b",
                CubeListBuilder.create().texOffs(128, 0).addBox(-4f, 0f, 0f, 4f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8f, 8f, 0f, 1.0472f, 0f, 0f)
            )

            val EquipR02 = EquipR01.addOrReplaceChild(
                "EquipR02",
                CubeListBuilder.create().texOffs(128, 0).addBox(-13f, 0f, 0f, 13f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-13.5f, -0.5f, 0.6f, 0f, -0.5236f, 0f)
            )

            val EquipR03 = EquipR02.addOrReplaceChild(
                "EquipR03",
                CubeListBuilder.create().texOffs(128, 29).addBox(-6f, 0f, -14f, 6f, 22f, 17f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-10.5f, -2.5f, -1f, 0f, 0.6981f, 0f)
            )

            val EquipRCBase01 = EquipR03.addOrReplaceChild(
                "EquipRCBase01",
                CubeListBuilder.create().texOffs(196, 16).addBox(-8.5f, -5f, -7f, 16f, 9f, 14f, CubeDeformation(0f)),
                PartPose.offset(-3f, 3f, -5.5f)
            )

            val EquipRCBase02 = EquipRCBase01.addOrReplaceChild(
                "EquipRCBase02",
                CubeListBuilder.create().texOffs(128, 0).addBox(-8.5f, -8f, -7f, 17f, 8f, 21f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.5f, -4.5f, -2f, -0.0524f, 0f, 0f)
            )

            val EquipRCRadar02 = EquipRCBase02.addOrReplaceChild(
                "EquipRCRadar02",
                CubeListBuilder.create().texOffs(58, 0).mirror().addBox(0f, 0f, 0f, 5f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offset(-13.3f, -7f, 5f)
            )

            val EquipRCRadar01 = EquipRCBase02.addOrReplaceChild(
                "EquipRCRadar01",
                CubeListBuilder.create().texOffs(58, 0).addBox(0f, 0f, 0f, 5f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offset(8.3f, -7f, 5f)
            )

            val EquipRC02a = EquipRCBase02.addOrReplaceChild(
                "EquipRC02a",
                CubeListBuilder.create().texOffs(128, 118).addBox(-2f, -2f, -5f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -4.5f, -6f, -0.0873f, 0f, 0f)
            )

            val EquipRC02b = EquipRC02a.addOrReplaceChild(
                "EquipRC02b",
                CubeListBuilder.create().texOffs(204, 39).addBox(-1.5f, -1.5f, -17f, 3f, 3f, 17f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val EquipRC03a = EquipRCBase02.addOrReplaceChild(
                "EquipRC03a",
                CubeListBuilder.create().texOffs(128, 118).addBox(-2f, -2f, -5f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5f, -4.5f, -6f, -0.3491f, 0f, 0f)
            )

            val EquipRC03b = EquipRC03a.addOrReplaceChild(
                "EquipRC03b",
                CubeListBuilder.create().texOffs(204, 39).addBox(-1.5f, -1.5f, -17f, 3f, 3f, 17f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val EquipRC01a = EquipRCBase02.addOrReplaceChild(
                "EquipRC01a",
                CubeListBuilder.create().texOffs(128, 118).addBox(-2f, -2f, -5f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5f, -4.5f, -6f, -0.1745f, 0f, 0f)
            )

            val EquipRC01b = EquipRC01a.addOrReplaceChild(
                "EquipRC01b",
                CubeListBuilder.create().texOffs(204, 39).addBox(-1.5f, -1.5f, -17f, 3f, 3f, 17f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val EquipR04 = EquipR03.addOrReplaceChild(
                "EquipR04",
                CubeListBuilder.create().texOffs(128, 70).addBox(-6f, 0f, -13f, 6f, 11f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 11f, -12.7f, 0f, -0.2094f, 0f)
            )

            val EquipRC3Base01 = EquipR04.addOrReplaceChild(
                "EquipRC3Base01",
                CubeListBuilder.create().texOffs(211, 23).addBox(-4f, 0f, 0f, 8f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7f, 5f, -12f, 0f, 0f, -1.5708f)
            )

            val EquipRC3Base02 = EquipRC3Base01.addOrReplaceChild(
                "EquipRC3Base02",
                CubeListBuilder.create().texOffs(128, 0).addBox(-4.5f, -5f, -5.5f, 9f, 5f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, 4f, -0.0524f, -0.182f, 0f)
            )

            val EquipRC302a = EquipRC3Base02.addOrReplaceChild(
                "EquipRC302a",
                CubeListBuilder.create().texOffs(128, 122).addBox(-1f, -1f, -2f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3f, -6f, -0.1396f, 0f, 0f)
            )

            val EquipRC302b = EquipRC302a.addOrReplaceChild(
                "EquipRC302b",
                CubeListBuilder.create().texOffs(163, 30).addBox(-0.5f, -0.5f, -9f, 1f, 1f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -2f)
            )

            val EquipRC3Radar02 = EquipRC3Base02.addOrReplaceChild(
                "EquipRC3Radar02",
                CubeListBuilder.create().texOffs(128, 38).mirror().addBox(0f, 0f, 0f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offset(-6.4f, -4f, -1f)
            )

            val EquipRC303a = EquipRC3Base02.addOrReplaceChild(
                "EquipRC303a",
                CubeListBuilder.create().texOffs(128, 122).addBox(-1f, -1f, -2f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.6f, -3f, -6f, -0.1396f, 0f, 0f)
            )

            val EquipRC303b = EquipRC303a.addOrReplaceChild(
                "EquipRC303b",
                CubeListBuilder.create().texOffs(163, 30).addBox(-0.5f, -0.5f, -9f, 1f, 1f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -2f)
            )

            val EquipRC3Radar01 = EquipRC3Base02.addOrReplaceChild(
                "EquipRC3Radar01",
                CubeListBuilder.create().texOffs(128, 38).addBox(0f, 0f, 0f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offset(4.4f, -4f, -1f)
            )

            val EquipRC301a = EquipRC3Base02.addOrReplaceChild(
                "EquipRC301a",
                CubeListBuilder.create().texOffs(128, 122).addBox(-1f, -1f, -2f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.6f, -3f, -6f, -0.1745f, 0f, 0f)
            )

            val EquipRC301b = EquipRC301a.addOrReplaceChild(
                "EquipRC301b",
                CubeListBuilder.create().texOffs(163, 30).addBox(-0.5f, -0.5f, -9f, 1f, 1f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -2f)
            )

            val EquipRC2Base01 = EquipR04.addOrReplaceChild(
                "EquipRC2Base01",
                CubeListBuilder.create().texOffs(211, 23).addBox(-4f, 0f, 0f, 8f, 6f, 7f, CubeDeformation(0f)),
                PartPose.offset(-2.5f, -4f, -10.5f)
            )

            val EquipRC2Base02 = EquipRC2Base01.addOrReplaceChild(
                "EquipRC2Base02",
                CubeListBuilder.create().texOffs(128, 0).addBox(-4.5f, -5f, -5.5f, 9f, 5f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, 4f, -0.0524f, 0f, 0f)
            )

            val EquipRC2Radar02 = EquipRC2Base02.addOrReplaceChild(
                "EquipRC2Radar02",
                CubeListBuilder.create().texOffs(128, 38).mirror().addBox(0f, 0f, 0f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offset(-6.4f, -4f, -1f)
            )

            val EquipRC2Radar01 = EquipRC2Base02.addOrReplaceChild(
                "EquipRC2Radar01",
                CubeListBuilder.create().texOffs(128, 38).addBox(0f, 0f, 0f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offset(4.4f, -4f, -1f)
            )

            val EquipRC203a = EquipRC2Base02.addOrReplaceChild(
                "EquipRC203a",
                CubeListBuilder.create().texOffs(128, 122).addBox(-1f, -1f, -2f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.6f, -3f, -6f, -0.1396f, 0f, 0f)
            )

            val EquipRC203b = EquipRC203a.addOrReplaceChild(
                "EquipRC203b",
                CubeListBuilder.create().texOffs(163, 30).addBox(-0.5f, -0.5f, -9f, 1f, 1f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -2f)
            )

            val EquipRC202a = EquipRC2Base02.addOrReplaceChild(
                "EquipRC202a",
                CubeListBuilder.create().texOffs(128, 122).addBox(-1f, -1f, -2f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3f, -6f, -0.1396f, 0f, 0f)
            )

            val EquipRC202b = EquipRC202a.addOrReplaceChild(
                "EquipRC202b",
                CubeListBuilder.create().texOffs(163, 30).addBox(-0.5f, -0.5f, -9f, 1f, 1f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -2f)
            )

            val EquipRC201a = EquipRC2Base02.addOrReplaceChild(
                "EquipRC201a",
                CubeListBuilder.create().texOffs(128, 122).addBox(-1f, -1f, -2f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.6f, -3f, -6f, -0.1745f, 0f, 0f)
            )

            val EquipRC201b = EquipRC201a.addOrReplaceChild(
                "EquipRC201b",
                CubeListBuilder.create().texOffs(163, 30).addBox(-0.5f, -0.5f, -9f, 1f, 1f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -2f)
            )

            val EquipR05 = EquipR04.addOrReplaceChild(
                "EquipR05",
                CubeListBuilder.create().texOffs(174, 36).addBox(0f, 0f, -10f, 5f, 13f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, -2.5f, -13f, 0f, -0.7854f, 0f)
            )

            val EquipMCBase01a = EquipR01.addOrReplaceChild(
                "EquipMCBase01a",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 4f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, 8f, 0f, 1.0472f, 0f, 0f)
            )

            val EquipLCBase01_1 = EquipMCBase01a.addOrReplaceChild(
                "EquipLCBase01_1",
                CubeListBuilder.create().texOffs(196, 16).addBox(-8f, -5f, -7f, 16f, 8f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8f, 7f, 3f, -2.5953f, 0f, 0f)
            )

            val EquipLCBase02_1 = EquipLCBase01_1.addOrReplaceChild(
                "EquipLCBase02_1",
                CubeListBuilder.create().texOffs(128, 0).addBox(-8.5f, -8f, -10f, 17f, 8f, 21f, CubeDeformation(0f)),
                PartPose.offset(0.5f, -4.5f, 0f)
            )

            val EquipMCRadar02 = EquipLCBase02_1.addOrReplaceChild(
                "EquipMCRadar02",
                CubeListBuilder.create().texOffs(58, 0).mirror().addBox(0f, 0f, 0f, 5f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offset(-13.3f, -7f, 2f)
            )

            val EquipLC02a_1 = EquipLCBase02_1.addOrReplaceChild(
                "EquipLC02a_1",
                CubeListBuilder.create().texOffs(128, 118).addBox(-2f, -2f, -5f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -4.5f, -9f, -0.2618f, 0f, 0f)
            )

            val EquipLC02b_1 = EquipLC02a_1.addOrReplaceChild(
                "EquipLC02b_1",
                CubeListBuilder.create().texOffs(204, 39).addBox(-1.5f, -1.5f, -17f, 3f, 3f, 17f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val EquipLC01a_1 = EquipLCBase02_1.addOrReplaceChild(
                "EquipLC01a_1",
                CubeListBuilder.create().texOffs(128, 118).addBox(-2f, -2f, -5f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5f, -4.5f, -9f, -0.1745f, 0f, 0f)
            )

            val EquipLC01b_1 = EquipLC01a_1.addOrReplaceChild(
                "EquipLC01b_1",
                CubeListBuilder.create().texOffs(204, 39).addBox(-1.5f, -1.5f, -17f, 3f, 3f, 17f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val EquipLC03a_1 = EquipLCBase02_1.addOrReplaceChild(
                "EquipLC03a_1",
                CubeListBuilder.create().texOffs(128, 118).addBox(-2f, -2f, -5f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5f, -4.5f, -9f, -0.1396f, 0f, 0f)
            )

            val EquipLC03b_1 = EquipLC03a_1.addOrReplaceChild(
                "EquipLC03b_1",
                CubeListBuilder.create().texOffs(204, 39).addBox(-1.5f, -1.5f, -17f, 3f, 3f, 17f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val EquipMCRadar01 = EquipLCBase02_1.addOrReplaceChild(
                "EquipMCRadar01",
                CubeListBuilder.create().texOffs(58, 0).addBox(0f, 0f, 0f, 5f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offset(8.3f, -7f, 2f)
            )

            val EquipL01 = EquipBaseM01b.addOrReplaceChild(
                "EquipL01",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 16f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.5f, 3f, -0.8727f, 0f, 0f)
            )

            val EquipL02 = EquipL01.addOrReplaceChild(
                "EquipL02",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 13f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(13.5f, -0.5f, 0.6f, 0f, 0.5236f, 0f)
            )

            val EquipL03 = EquipL02.addOrReplaceChild(
                "EquipL03",
                CubeListBuilder.create().texOffs(128, 29).addBox(0f, 0f, -14f, 6f, 22f, 17f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(10.5f, -2.5f, -1f, 0f, -0.6981f, 0f)
            )

            val EquipLCBase01 = EquipL03.addOrReplaceChild(
                "EquipLCBase01",
                CubeListBuilder.create().texOffs(196, 16).addBox(-7.5f, -5f, -7f, 16f, 9f, 14f, CubeDeformation(0f)),
                PartPose.offset(3f, 3f, -5.5f)
            )

            val EquipLCBase02 = EquipLCBase01.addOrReplaceChild(
                "EquipLCBase02",
                CubeListBuilder.create().texOffs(128, 0).addBox(-8.5f, -8f, -7f, 17f, 8f, 21f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.5f, -4.5f, -2f, -0.0524f, 0f, 0f)
            )

            val EquipLC02a = EquipLCBase02.addOrReplaceChild(
                "EquipLC02a",
                CubeListBuilder.create().texOffs(128, 118).addBox(-2f, -2f, -5f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -4.5f, -6f, -0.2618f, 0f, 0f)
            )

            val EquipLC02b = EquipLC02a.addOrReplaceChild(
                "EquipLC02b",
                CubeListBuilder.create().texOffs(204, 39).addBox(-1.5f, -1.5f, -17f, 3f, 3f, 17f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val EquipLCRadar02 = EquipLCBase02.addOrReplaceChild(
                "EquipLCRadar02",
                CubeListBuilder.create().texOffs(58, 0).mirror().addBox(0f, 0f, 0f, 5f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offset(-13.3f, -7f, 5f)
            )

            val EquipLCRadar01 = EquipLCBase02.addOrReplaceChild(
                "EquipLCRadar01",
                CubeListBuilder.create().texOffs(58, 0).addBox(0f, 0f, 0f, 5f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offset(8.3f, -7f, 5f)
            )

            val EquipLC03a = EquipLCBase02.addOrReplaceChild(
                "EquipLC03a",
                CubeListBuilder.create().texOffs(128, 118).addBox(-2f, -2f, -5f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5f, -4.5f, -6f, -0.1396f, 0f, 0f)
            )

            val EquipLC03b = EquipLC03a.addOrReplaceChild(
                "EquipLC03b",
                CubeListBuilder.create().texOffs(204, 39).addBox(-1.5f, -1.5f, -17f, 3f, 3f, 17f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val EquipLC01a = EquipLCBase02.addOrReplaceChild(
                "EquipLC01a",
                CubeListBuilder.create().texOffs(128, 118).addBox(-2f, -2f, -5f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5f, -4.5f, -6f, -0.1745f, 0f, 0f)
            )

            val EquipLC01b = EquipLC01a.addOrReplaceChild(
                "EquipLC01b",
                CubeListBuilder.create().texOffs(204, 39).addBox(-1.5f, -1.5f, -17f, 3f, 3f, 17f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val EquipL04 = EquipL03.addOrReplaceChild(
                "EquipL04",
                CubeListBuilder.create().texOffs(128, 70).addBox(0f, 0f, -13f, 6f, 11f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 11f, -12.7f, 0f, 0.2094f, 0f)
            )

            val EquipLC2Base01 = EquipL04.addOrReplaceChild(
                "EquipLC2Base01",
                CubeListBuilder.create().texOffs(211, 23).addBox(-4f, 0f, 0f, 8f, 6f, 7f, CubeDeformation(0f)),
                PartPose.offset(2.5f, -4f, -10.5f)
            )

            val EquipLC2Base02 = EquipLC2Base01.addOrReplaceChild(
                "EquipLC2Base02",
                CubeListBuilder.create().texOffs(128, 0).addBox(-4.5f, -5f, -5.5f, 9f, 5f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, 4f, -0.0524f, -0.2731f, 0f)
            )

            val EquipLC203a = EquipLC2Base02.addOrReplaceChild(
                "EquipLC203a",
                CubeListBuilder.create().texOffs(128, 122).addBox(-1f, -1f, -2f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.6f, -3f, -6f, -0.1396f, 0f, 0f)
            )

            val EquipLC203b = EquipLC203a.addOrReplaceChild(
                "EquipLC203b",
                CubeListBuilder.create().texOffs(163, 30).addBox(-0.5f, -0.5f, -9f, 1f, 1f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -2f)
            )

            val EquipLC2Radar02 = EquipLC2Base02.addOrReplaceChild(
                "EquipLC2Radar02",
                CubeListBuilder.create().texOffs(128, 38).mirror().addBox(0f, 0f, 0f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offset(-6.4f, -4f, -1f)
            )

            val EquipLC2Radar01 = EquipLC2Base02.addOrReplaceChild(
                "EquipLC2Radar01",
                CubeListBuilder.create().texOffs(128, 38).addBox(0f, 0f, 0f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offset(4.4f, -4f, -1f)
            )

            val EquipLC201a = EquipLC2Base02.addOrReplaceChild(
                "EquipLC201a",
                CubeListBuilder.create().texOffs(128, 122).addBox(-1f, -1f, -2f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.6f, -3f, -6f, -0.1745f, 0f, 0f)
            )

            val EquipLC201b = EquipLC201a.addOrReplaceChild(
                "EquipLC201b",
                CubeListBuilder.create().texOffs(163, 30).addBox(-0.5f, -0.5f, -9f, 1f, 1f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -2f)
            )

            val EquipLC202a = EquipLC2Base02.addOrReplaceChild(
                "EquipLC202a",
                CubeListBuilder.create().texOffs(128, 122).addBox(-1f, -1f, -2f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3f, -6f, -0.1396f, 0f, 0f)
            )

            val EquipLC202b = EquipLC202a.addOrReplaceChild(
                "EquipLC202b",
                CubeListBuilder.create().texOffs(163, 30).addBox(-0.5f, -0.5f, -9f, 1f, 1f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -2f)
            )

            val EquipLC3Base01 = EquipL04.addOrReplaceChild(
                "EquipLC3Base01",
                CubeListBuilder.create().texOffs(211, 23).addBox(-4f, 0f, 0f, 8f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, 5f, -12f, 0f, 0f, 1.5708f)
            )

            val EquipLC3Base02 = EquipLC3Base01.addOrReplaceChild(
                "EquipLC3Base02",
                CubeListBuilder.create().texOffs(128, 0).addBox(-4.5f, -5f, -5.5f, 9f, 5f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, 4f, 0.0524f, 0.1367f, 0f)
            )

            val EquipLC3Radar02 = EquipLC3Base02.addOrReplaceChild(
                "EquipLC3Radar02",
                CubeListBuilder.create().texOffs(128, 38).mirror().addBox(0f, 0f, 0f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offset(-6.4f, -4f, -1f)
            )

            val EquipLC3Radar01 = EquipLC3Base02.addOrReplaceChild(
                "EquipLC3Radar01",
                CubeListBuilder.create().texOffs(128, 38).addBox(0f, 0f, 0f, 2f, 2f, 4f, CubeDeformation(0f)),
                PartPose.offset(4.4f, -4f, -1f)
            )

            val EquipLC303a = EquipLC3Base02.addOrReplaceChild(
                "EquipLC303a",
                CubeListBuilder.create().texOffs(128, 122).addBox(-1f, -1f, -2f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.6f, -3f, -6f, -0.1396f, 0f, 0f)
            )

            val EquipLC303b = EquipLC303a.addOrReplaceChild(
                "EquipLC303b",
                CubeListBuilder.create().texOffs(163, 30).addBox(-0.5f, -0.5f, -9f, 1f, 1f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -2f)
            )

            val EquipLC302a = EquipLC3Base02.addOrReplaceChild(
                "EquipLC302a",
                CubeListBuilder.create().texOffs(128, 122).addBox(-1f, -1f, -2f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3f, -6f, -0.1396f, 0f, 0f)
            )

            val EquipLC302b = EquipLC302a.addOrReplaceChild(
                "EquipLC302b",
                CubeListBuilder.create().texOffs(163, 30).addBox(-0.5f, -0.5f, -9f, 1f, 1f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -2f)
            )

            val EquipLC301a = EquipLC3Base02.addOrReplaceChild(
                "EquipLC301a",
                CubeListBuilder.create().texOffs(128, 122).addBox(-1f, -1f, -2f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.6f, -3f, -6f, -0.1745f, 0f, 0f)
            )

            val EquipLC301b = EquipLC301a.addOrReplaceChild(
                "EquipLC301b",
                CubeListBuilder.create().texOffs(163, 30).addBox(-0.5f, -0.5f, -9f, 1f, 1f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -2f)
            )

            val EquipL05 = EquipL04.addOrReplaceChild(
                "EquipL05",
                CubeListBuilder.create().texOffs(174, 36).addBox(-5f, 0f, -10f, 5f, 13f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, -2.5f, -13f, 0f, 0.7854f, 0f)
            )

            val EquipBaseM02 = EquipBaseM01b.addOrReplaceChild(
                "EquipBaseM02",
                CubeListBuilder.create().texOffs(128, 0).addBox(-9f, 0f, 0f, 18f, 10f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1.3f, 7.7f, -0.5918f, 0f, 0f)
            )

            val EquipBaseM03 = EquipBaseM02.addOrReplaceChild(
                "EquipBaseM03",
                CubeListBuilder.create().texOffs(128, 95).addBox(-3.5f, -15f, 0f, 7f, 15f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, -2.5f, -0.6981f, 0f, 0f)
            )

            val EquipBaseBelt2 = EquipBaseBelt.addOrReplaceChild(
                "EquipBaseBelt2",
                CubeListBuilder.create().texOffs(210, 0).addBox(-7f, 0f, -4f, 14f, 6f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -8.7f, 2.5f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(0, 29).addBox(-3f, -1f, -2.5f, 5f, 14f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, -9.7f, -0.7f, 0.2618f, 0f, 0.2094f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(20, 29).addBox(0f, 0f, -5f, 5f, 14f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3f, 13f, 2.5f, -1.4835f, 0f, 0f)
            )

            val EquipU01 = ArmRight02.addOrReplaceChild(
                "EquipU01",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, -4f, 0f, 1f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.5f, 13f, -5f, -1.7453f, 2.4086f, -1.9199f)
            )

            val EquipU01a = EquipU01.addOrReplaceChild(
                "EquipU01a",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, -12f, 0f)
            )

            val EquipU02 = EquipU01.addOrReplaceChild(
                "EquipU02",
                CubeListBuilder.create().texOffs(222, 32).addBox(-1f, 0f, -1f, 2f, 3f, 2f, CubeDeformation(0f)),
                PartPose.offset(0.5f, -15f, 0.5f)
            )

            val EquipU09a = EquipU02.addOrReplaceChild(
                "EquipU09a",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 10f, 1f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5f, -23f, 6f, -0.2618f, 0f, 0f)
            )

            val EquipU06 = EquipU02.addOrReplaceChild(
                "EquipU06",
                CubeListBuilder.create().texOffs(166, 60).addBox(-8f, 0f, -8f, 16f, 1f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -31.1f, 5.5f, -0.1379f, 0.7854f, -0.096f)
            )

            val EquipU07 = EquipU06.addOrReplaceChild(
                "EquipU07",
                CubeListBuilder.create().texOffs(214, 66).addBox(0f, -1f, 0f, 9f, 1f, 9f, CubeDeformation(0f)),
                PartPose.offset(-4.5f, 0f, -4.5f)
            )

            val EquipU08 = EquipU07.addOrReplaceChild(
                "EquipU08",
                CubeListBuilder.create().texOffs(214, 61).addBox(0f, -2f, 0f, 4f, 1f, 4f, CubeDeformation(0f)),
                PartPose.offset(2.5f, 0f, 2.5f)
            )

            val EquipU09b = EquipU02.addOrReplaceChild(
                "EquipU09b",
                CubeListBuilder.create().texOffs(128, 0).addBox(-0.4f, 0f, 0f, 1f, 1f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -24f, -3f, 0f, 0.5061f, 0.2618f)
            )

            val EquipU04a = EquipU02.addOrReplaceChild(
                "EquipU04a",
                CubeListBuilder.create().texOffs(128, 0).addBox(-0.5f, -8f, -0.5f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 0.1f, 0.3f, -0.2618f, 0f, 0.2094f)
            )

            val EquipU04b = EquipU04a.addOrReplaceChild(
                "EquipU04b",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, -16f, -0.5f)
            )

            val EquipU04c = EquipU04b.addOrReplaceChild(
                "EquipU04c",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, -8f, 0f)
            )

            val EquipU04d = EquipU04c.addOrReplaceChild(
                "EquipU04d",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, -8f, 0f)
            )

            val EquipU03a = EquipU02.addOrReplaceChild(
                "EquipU03a",
                CubeListBuilder.create().texOffs(128, 0).addBox(-0.5f, -8f, -0.5f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.1f, -0.3f, 0.1047f, 0f, 0f)
            )

            val EquipU03b = EquipU03a.addOrReplaceChild(
                "EquipU03b",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, -16f, -0.5f)
            )

            val EquipU03c = EquipU03b.addOrReplaceChild(
                "EquipU03c",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, -8f, 0f)
            )

            val EquipU03d = EquipU03c.addOrReplaceChild(
                "EquipU03d",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, -8f, 0f)
            )

            val EquipU05a = EquipU02.addOrReplaceChild(
                "EquipU05a",
                CubeListBuilder.create().texOffs(128, 0).addBox(-0.5f, -8f, -0.5f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.2f, 0.1f, 0.3f, -0.2618f, 0f, -0.2094f)
            )

            val EquipU05b = EquipU05a.addOrReplaceChild(
                "EquipU05b",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, -16f, -0.5f)
            )

            val EquipU05c = EquipU05b.addOrReplaceChild(
                "EquipU05c",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, -8f, 0f)
            )

            val EquipU05d = EquipU05c.addOrReplaceChild(
                "EquipU05d",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, -8f, 0f)
            )

            val EquipU09c = EquipU02.addOrReplaceChild(
                "EquipU09c",
                CubeListBuilder.create().texOffs(128, 0).addBox(-0.6f, 0f, 0f, 1f, 1f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -24f, -3f, 0f, -0.5061f, -0.2618f)
            )

            val EquipU01b = EquipU01.addOrReplaceChild(
                "EquipU01b",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, 0f, 1f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 3f, 0f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(0, 16).addBox(-4.5f, -2f, -5f, 9f, 3f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10.7f, -0.2f, 0.2094f, 0f, 0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1f, 0f, -0.1047f, 0f, 0f)
            )

            val EquipHeadBase = Head.addOrReplaceChild(
                "EquipHeadBase",
                CubeListBuilder.create().texOffs(128, 0).addBox(-8f, 0f, 0f, 16f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, -9.5f, 0f)
            )

            val HeadEquip02a = EquipHeadBase.addOrReplaceChild(
                "HeadEquip02a",
                CubeListBuilder.create().texOffs(128, 0).addBox(-2f, 0f, -2f, 2f, 3f, 4f, CubeDeformation(0f)),
                PartPose.offset(-8f, 0.2f, 5f)
            )

            val HeadEquip02d = HeadEquip02a.addOrReplaceChild(
                "HeadEquip02d",
                CubeListBuilder.create().texOffs(91, 64).addBox(0f, 0f, 0f, 0f, 4f, 2f, CubeDeformation(0f)),
                PartPose.offset(-3.5f, 0.2f, -1f)
            )

            val HeadEquip02b = HeadEquip02a.addOrReplaceChild(
                "HeadEquip02b",
                CubeListBuilder.create().texOffs(128, 0).addBox(-4f, -1f, -1f, 4f, 1f, 2f, CubeDeformation(0f)),
                PartPose.offset(-2f, 1.5f, 0.5f)
            )

            val HeadEquip02b2 = HeadEquip02b.addOrReplaceChild(
                "HeadEquip02b2",
                CubeListBuilder.create().texOffs(128, 0).addBox(-1.5f, -1.5f, 0f, 3f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offset(-4f, 0f, -2f)
            )

            val HeadEquip02c = HeadEquip02a.addOrReplaceChild(
                "HeadEquip02c",
                CubeListBuilder.create().texOffs(43, 82).mirror().addBox(0f, 0f, 0f, 7f, 4f, 0f, CubeDeformation(0f)),
                PartPose.offset(-7f, -3.5f, 0.5f)
            )

            val HeadEquip01a = EquipHeadBase.addOrReplaceChild(
                "HeadEquip01a",
                CubeListBuilder.create().texOffs(128, 0).addBox(0f, 0f, -2f, 2f, 3f, 4f, CubeDeformation(0f)),
                PartPose.offset(8f, 0.2f, 5f)
            )

            val HeadEquip01d = HeadEquip01a.addOrReplaceChild(
                "HeadEquip01d",
                CubeListBuilder.create().texOffs(91, 64).addBox(0f, 0f, 0f, 0f, 4f, 2f, CubeDeformation(0f)),
                PartPose.offset(3.5f, 0.2f, -1f)
            )

            val HeadEquip01c = HeadEquip01a.addOrReplaceChild(
                "HeadEquip01c",
                CubeListBuilder.create().texOffs(43, 82).addBox(0f, 0f, 0f, 7f, 4f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -3.5f, 0.5f)
            )

            val HeadEquip01b = HeadEquip01a.addOrReplaceChild(
                "HeadEquip01b",
                CubeListBuilder.create().texOffs(128, 0).mirror().addBox(0f, -1f, -1f, 4f, 1f, 2f, CubeDeformation(0f)),
                PartPose.offset(2f, 1.5f, 0.5f)
            )

            val HeadEquip01b2 = HeadEquip01b.addOrReplaceChild(
                "HeadEquip01b2",
                CubeListBuilder.create().texOffs(128, 0).addBox(-1.5f, -1.5f, 0f, 3f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offset(4f, 0f, -2f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 81).addBox(-8f, -8f, -7.4f, 16f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.2f, 0f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(40, 89).mirror().addBox(-1f, 0f, 0f, 2f, 9f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7f, 1f, -3f, -0.4014f, 0.1745f, -0.0873f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(86, 101).mirror()
                    .addBox(-1f, 0f, 0f, 2f, 10f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 8f, 0.3f, 0.2967f, 0f, 0.3142f)
            )

            val HairR03 = HairR02.addOrReplaceChild(
                "HairR03",
                CubeListBuilder.create().texOffs(86, 101).mirror()
                    .addBox(-1f, 0f, 0f, 2f, 10f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.1f, 9f, 0.1f, 0.1396f, 0f, -0.2269f)
            )

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(56, 23).addBox(-8.5f, 0f, 0f, 17f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -8.8f, -5.7f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(40, 89).addBox(-1f, 0f, 0f, 2f, 9f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, 1f, -3f, -0.3665f, -0.1745f, 0.0873f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(86, 101).addBox(-1f, 0f, 0f, 2f, 10f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.2f, 8f, 0.3f, 0.2269f, 0f, -0.3142f)
            )

            val HairL03 = HairL02.addOrReplaceChild(
                "HairL03",
                CubeListBuilder.create().texOffs(86, 101).addBox(-1f, 0f, 0f, 2f, 10f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.1f, 9f, 0.1f, 0.1745f, 0f, 0.2269f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(104, 29).addBox(0f, -4f, -11.5f, 0f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -9f, -5.5f, 0.1745f, 0.6981f, 0f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(159, 107).addBox(-7.5f, 0f, 0f, 15f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.8f, -3f)
            )

            val HairBase = HairMain.addOrReplaceChild(
                "HairBase",
                CubeListBuilder.create().texOffs(102, 35).addBox(-5f, 0f, -0.7f, 10f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.5f, 5.5f, 0.8727f, 0f, 0f)
            )

            val Hair00 = HairBase.addOrReplaceChild(
                "Hair00",
                CubeListBuilder.create().texOffs(170, 81).addBox(-3.5f, 0f, -4f, 7f, 7f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.2f, 2.5f)
            )

            val Hair01 = Hair00.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(166, 78).addBox(-4f, -1f, -0.2f, 8f, 20f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.7f, 1.3f, -0.7285f, 0f, -0.3643f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(169, 80).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 18f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 16.5f, 5f, -0.3491f, 0f, -0.2731f)
            )

            val Hair03 = Hair02.addOrReplaceChild(
                "Hair03",
                CubeListBuilder.create().texOffs(170, 81).addBox(-3.5f, 0f, -4f, 7f, 16f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 15f, 1f, 0.2618f, 0f, 0.3643f)
            )

            val Hair04 = Hair03.addOrReplaceChild(
                "Hair04",
                CubeListBuilder.create().texOffs(209, 108).addBox(-3f, 0f, -3.2f, 6f, 15f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 13f, 0f, -0.3491f, 0f, 0.2731f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(52, 65).addBox(-7.5f, 0f, -5.7f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 1.3f, 0.3142f, 0f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 63).mirror().addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.8f, 5.5f, -2.6f, -0.2793f, 0f, 0.1396f)
            )

            val EquipLegL01 = LegLeft01.addOrReplaceChild(
                "EquipLegL01",
                CubeListBuilder.create().texOffs(154, 12).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 9f, 0f)
            )

            val EquipLegL02c = EquipLegL01.addOrReplaceChild(
                "EquipLegL02c",
                CubeListBuilder.create().texOffs(0, 84).addBox(0f, 0f, 0f, 1f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.2f, -0.7f, -2.5f, 0f, 0f, -0.0524f)
            )

            val EquipLegL02a = EquipLegL01.addOrReplaceChild(
                "EquipLegL02a",
                CubeListBuilder.create().texOffs(0, 84).addBox(0f, 0f, 0f, 1f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.4f, -0.9f, -0.9f, 0f, 0f, -0.0524f)
            )

            val EquipLegL02b = EquipLegL01.addOrReplaceChild(
                "EquipLegL02b",
                CubeListBuilder.create().texOffs(0, 84).addBox(0f, 0f, 0f, 1f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.2f, -0.7f, 0.7f, 0f, 0f, -0.0524f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 83).mirror().addBox(-3f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 14f, -3f)
            )

            val ShoesL01 = LegLeft02.addOrReplaceChild(
                "ShoesL01",
                CubeListBuilder.create().texOffs(18, 80).mirror()
                    .addBox(-3.5f, 0f, -0.5f, 7f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 7f, 0f)
            )

            val Skirt01 = Butt.addOrReplaceChild(
                "Skirt01",
                CubeListBuilder.create().texOffs(0, 48).addBox(-8.5f, 0f, -6f, 17f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.3f, 0f, -0.1396f, 0f, 0f)
            )

            val Skirt02 = Skirt01.addOrReplaceChild(
                "Skirt02",
                CubeListBuilder.create().texOffs(42, 51).addBox(-9f, 0f, -6f, 18f, 4f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.9f, -0.4f, -0.0873f, 0f, 0f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(226, 83).addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.8f, 5.5f, -2.6f, -0.1396f, 0f, -0.1396f)
            )

            val EquipLegR01 = LegRight01.addOrReplaceChild(
                "EquipLegR01",
                CubeListBuilder.create().texOffs(133, 8).addBox(-3.5f, 0f, -3.5f, 7f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 9f, 0f)
            )

            val EquipLegR02a = EquipLegR01.addOrReplaceChild(
                "EquipLegR02a",
                CubeListBuilder.create().texOffs(0, 84).addBox(-1f, 0f, 0f, 1f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.2f, -0.7f, -2.5f, 0f, 0f, 0.0524f)
            )

            val EquipLegR02c = EquipLegR01.addOrReplaceChild(
                "EquipLegR02c",
                CubeListBuilder.create().texOffs(0, 84).addBox(-1f, 0f, 0f, 1f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.2f, -0.7f, 0.7f, 0f, 0f, 0.0524f)
            )

            val EquipLegR02b = EquipLegR01.addOrReplaceChild(
                "EquipLegR02b",
                CubeListBuilder.create().texOffs(0, 84).addBox(-1f, 0f, 0f, 1f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.4f, -0.8f, -0.9f, 0f, 0f, 0.0524f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(201, 83).addBox(-3f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 14f, -3f)
            )

            val ShoesR01 = LegRight02.addOrReplaceChild(
                "ShoesR01",
                CubeListBuilder.create().texOffs(18, 80).addBox(-3.5f, 0f, -0.5f, 7f, 2f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 7f, 0f)
            )

            val AnchorL = Butt.addOrReplaceChild(
                "AnchorL",
                CubeListBuilder.create().texOffs(24, 90).mirror().addBox(0f, 0f, -3f, 1f, 7f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.7f, 2f, -2f, 0f, 0f, -0.3491f)
            )

            val AnchorR = Butt.addOrReplaceChild(
                "AnchorR",
                CubeListBuilder.create().texOffs(24, 90).addBox(-1f, 0f, -3f, 1f, 7f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.7f, 2f, -2f, 0f, 0f, 0.3491f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -15f, 0f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -10.7f, -0.2f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -1f, 0f)
            )

            addFaceLayer(GlowHead)

            return LayerDefinition.create(meshdefinition, 256, 128)
        }
    }
}
