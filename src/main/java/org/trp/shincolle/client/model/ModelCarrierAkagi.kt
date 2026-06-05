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
import org.trp.shincolle.entity.EntityCarrierAkagi
import org.trp.shincolle.entity.base.EntityShipBase
import kotlin.math.max
import kotlin.math.min

class ModelCarrierAkagi<T : EntityShipBase?>(root: ModelPart) : ShipModelHumanoidBase<T?>(), IGlowableModel {
    private var isDeadPose = false
    private var isSittingPose = false
    private var poseTranslateY = 0f

    private val BodyMain: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val Butt: ModelPart
    private val Head: ModelPart
    private val Cloth01: ModelPart
    private val Cloth02: ModelPart
    private val Cloth05: ModelPart
    private val Cloth06: ModelPart
    private val EquipB01: ModelPart
    private val EquipC01: ModelPart
    private val EquipABase: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val ClothBody01: ModelPart
    private val ClothBody02: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft01: ModelPart
    private val Skirt01: ModelPart
    private val Tail01: ModelPart
    private val LegRight02: ModelPart
    private val EquipSR01: ModelPart
    private val LegLeft02: ModelPart
    private val EquipSL01: ModelPart
    private val Skirt02: ModelPart
    private val Cloth07: ModelPart
    private val Cloth08: ModelPart
    private val Cloth09: ModelPart
    private val EquipS01: ModelPart
    private val Tail02: ModelPart
    private val Tail03: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val Ear01: ModelPart
    private val Ear02: ModelPart
    private val Ahoke: ModelPart
    private val HairU01: ModelPart
    private val HairR01: ModelPart
    private val HairL01: ModelPart
    private val HairR02: ModelPart
    private val HairL02: ModelPart
    private val Hair01: ModelPart
    private val Hair02: ModelPart
    private val Cloth03: ModelPart
    private val Cloth04: ModelPart
    private val EquipC02: ModelPart
    private val EquipABelt01: ModelPart
    private val EquipABody01: ModelPart
    private val EquipABody04: ModelPart
    private val EquipABody02: ModelPart
    private val EquipABody03: ModelPart
    private val EquipABody05: ModelPart
    private val EquipAArr01a: ModelPart
    private val EquipAArr02a: ModelPart
    private val EquipAArr03a: ModelPart
    private val EquipABody05b: ModelPart
    private val EquipABody05c: ModelPart
    private val EquipABelt02: ModelPart
    private val EquipAArr01b: ModelPart
    private val EquipAArr02b: ModelPart
    private val EquipAArr03b: ModelPart
    private val ArmLeft02: ModelPart
    private val ClothHL01: ModelPart
    private val EquipE01: ModelPart
    private val EquipE02: ModelPart
    private val EquipE04: ModelPart
    private val EquipE03: ModelPart
    private val EquipE05: ModelPart
    private val EquipE06: ModelPart
    private val ClothHL02: ModelPart
    private val ClothHL03: ModelPart
    private val ArmRight02: ModelPart
    private val ClothHL01_1: ModelPart
    private val EquipD01: ModelPart
    private val EquipGlove: ModelPart
    private val ClothHL02_1: ModelPart
    private val ClothHL03_1: ModelPart
    private val EquipD02: ModelPart
    private val EquipD03: ModelPart
    private val EquipD04: ModelPart
    private val GlowBodyMain: ModelPart?
    private val GlowHead: ModelPart
    private val armRight02DefaultX: Float
    private val clothHL02_1DefaultY: Float
    private val clothHL03_1DefaultY: Float
    private val legLeft02DefaultX: Float
    private val legLeft02DefaultY: Float
    private val legLeft02DefaultZ: Float
    private val legRight02DefaultX: Float
    private val legRight02DefaultY: Float
    private val legRight02DefaultZ: Float
    private val equipE01DefaultX: Float
    private val equipD02DefaultY: Float
    private val tail01DefaultXRot: Float
    private val tail02DefaultXRot: Float
    private val tail03DefaultXRot: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.ClothBody02 = this.BodyMain.getChild("ClothBody02")
        this.Head = this.BodyMain.getChild("Head")
        this.Ear02 = this.Head.getChild("Ear02")
        this.Hair = this.Head.getChild("Hair")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.Ear01 = this.Head.getChild("Ear01")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.EquipSL01 = this.LegLeft02.getChild("EquipSL01")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.EquipSR01 = this.LegRight02.getChild("EquipSR01")
        this.Skirt01 = this.Butt.getChild("Skirt01")
        this.Cloth08 = this.Skirt01.getChild("Cloth08")
        this.Cloth09 = this.Skirt01.getChild("Cloth09")
        this.EquipS01 = this.Skirt01.getChild("EquipS01")
        this.Skirt02 = this.Skirt01.getChild("Skirt02")
        this.Cloth07 = this.Skirt01.getChild("Cloth07")
        this.Tail01 = this.Butt.getChild("Tail01")
        this.Tail02 = this.Tail01.getChild("Tail02")
        this.Tail03 = this.Tail02.getChild("Tail03")
        this.EquipABase = this.BodyMain.getChild("EquipABase")
        this.EquipABelt01 = this.EquipABase.getChild("EquipABelt01")
        this.EquipABody01 = this.EquipABelt01.getChild("EquipABody01")
        this.EquipABody02 = this.EquipABody01.getChild("EquipABody02")
        this.EquipABody03 = this.EquipABody01.getChild("EquipABody03")
        this.EquipABody04 = this.EquipABody01.getChild("EquipABody04")
        this.EquipAArr01a = this.EquipABody04.getChild("EquipAArr01a")
        this.EquipAArr01b = this.EquipAArr01a.getChild("EquipAArr01b")
        this.EquipAArr02a = this.EquipABody04.getChild("EquipAArr02a")
        this.EquipAArr02b = this.EquipAArr02a.getChild("EquipAArr02b")
        this.EquipABody05 = this.EquipABody04.getChild("EquipABody05")
        this.EquipABody05b = this.EquipABody05.getChild("EquipABody05b")
        this.EquipABody05c = this.EquipABody05b.getChild("EquipABody05c")
        this.EquipABelt02 = this.EquipABody05c.getChild("EquipABelt02")
        this.EquipAArr03a = this.EquipABody04.getChild("EquipAArr03a")
        this.EquipAArr03b = this.EquipAArr03a.getChild("EquipAArr03b")
        this.Cloth02 = this.BodyMain.getChild("Cloth02")
        this.Cloth03 = this.Cloth02.getChild("Cloth03")
        this.Cloth04 = this.Cloth02.getChild("Cloth04")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.EquipD01 = this.ArmRight01.getChild("EquipD01")
        this.EquipD02 = this.EquipD01.getChild("EquipD02")
        this.EquipD03 = this.EquipD02.getChild("EquipD03")
        this.EquipD04 = this.EquipD03.getChild("EquipD04")
        this.ClothHL01_1 = this.ArmRight01.getChild("ClothHL01_1")
        this.ClothHL02_1 = this.ClothHL01_1.getChild("ClothHL02_1")
        this.ClothHL03_1 = this.ClothHL02_1.getChild("ClothHL03_1")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.EquipGlove = this.ArmRight02.getChild("EquipGlove")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.EquipB01 = this.BodyMain.getChild("EquipB01")
        this.ClothBody01 = this.BodyMain.getChild("ClothBody01")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ClothHL01 = this.ArmLeft01.getChild("ClothHL01")
        this.ClothHL02 = this.ClothHL01.getChild("ClothHL02")
        this.ClothHL03 = this.ClothHL02.getChild("ClothHL03")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.EquipE01 = this.ArmLeft02.getChild("EquipE01")
        this.EquipE02 = this.EquipE01.getChild("EquipE02")
        this.EquipE03 = this.EquipE02.getChild("EquipE03")
        this.EquipE04 = this.EquipE01.getChild("EquipE04")
        this.EquipE05 = this.EquipE04.getChild("EquipE05")
        this.EquipE06 = this.EquipE05.getChild("EquipE06")
        this.Cloth06 = this.BodyMain.getChild("Cloth06")
        this.EquipC01 = this.BodyMain.getChild("EquipC01")
        this.EquipC02 = this.EquipC01.getChild("EquipC02")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.Cloth05 = this.BodyMain.getChild("Cloth05")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowHead = this.GlowBodyMain.getChild("GlowHead")
        initFaceParts(this.GlowHead)
        this.armRight02DefaultX = this.ArmRight02.x
        this.clothHL02_1DefaultY = this.ClothHL02_1.y
        this.clothHL03_1DefaultY = this.ClothHL03_1.y
        this.legLeft02DefaultX = this.LegLeft02.x
        this.legLeft02DefaultY = this.LegLeft02.y
        this.legLeft02DefaultZ = this.LegLeft02.z
        this.legRight02DefaultX = this.LegRight02.x
        this.legRight02DefaultY = this.LegRight02.y
        this.legRight02DefaultZ = this.LegRight02.z
        this.equipE01DefaultX = this.EquipE01.x
        this.equipD02DefaultY = this.EquipD02.y
        this.tail01DefaultXRot = this.Tail01.xRot
        this.tail02DefaultXRot = this.Tail02.xRot
        this.tail03DefaultXRot = this.Tail03.xRot
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

        if (entity != null && entity.isInDeadPose) {
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

        this.ArmRight02.x = this.armRight02DefaultX
        this.ClothHL02_1.y = this.clothHL02_1DefaultY
        this.ClothHL03_1.y = this.clothHL03_1DefaultY
        this.LegLeft02.x = this.legLeft02DefaultX
        this.LegLeft02.y = this.legLeft02DefaultY
        this.LegLeft02.z = this.legLeft02DefaultZ
        this.LegRight02.x = this.legRight02DefaultX
        this.LegRight02.y = this.legRight02DefaultY
        this.LegRight02.z = this.legRight02DefaultZ
        this.EquipE01.x = this.equipE01DefaultX
        this.EquipD02.y = this.equipD02DefaultY
        this.Tail01.xRot = this.tail01DefaultXRot
        this.Tail02.xRot = this.tail02DefaultXRot
        this.Tail03.xRot = this.tail03DefaultXRot
    }

    private fun applyEquipVisibility(entity: EntityShipBase?) {
        if (entity == null) return
        val show_CatParts = entity.getEquipFlag(EntityCarrierAkagi.EQUIP_CAT_PARTS)
        this.Ear01.visible = show_CatParts
        this.Ear02.visible = show_CatParts
        this.Tail01.visible = show_CatParts

        this.EquipABase.visible = entity.getEquipFlag(EntityCarrierAkagi.EQUIP_BACK_QUIVER)
        this.EquipB01.visible = entity.getEquipFlag(EntityCarrierAkagi.EQUIP_BREASTPLATE)
        this.EquipC01.visible = entity.getEquipFlag(EntityCarrierAkagi.EQUIP_RIGGING)
        this.EquipD01.visible = entity.getEquipFlag(EntityCarrierAkagi.EQUIP_DECK_HAND)

        val show_Bow = entity.getEquipFlag(EntityCarrierAkagi.EQUIP_BOW)
        this.EquipE01.visible = show_Bow
        this.EquipGlove.visible = show_Bow

        this.EquipS01.visible = entity.getEquipFlag(EntityCarrierAkagi.EQUIP_SKIRT)

        val show_Shoes = entity.getEquipFlag(EntityCarrierAkagi.EQUIP_SHOES)
        this.EquipSL01.visible = show_Shoes
        this.EquipSR01.visible = show_Shoes
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        this.Skirt01.xRot = -0.2f
        this.Skirt02.xRot = -0.3f
        this.ArmRight02.x = this.armRight02DefaultX

        this.ArmRight01.zRot += 0.15f
        this.ArmLeft01.zRot -= 0.15f
        this.Tail01.xRot = -1.85f
        this.Tail02.xRot = -0.6f
        this.Tail03.xRot = -0.6f

        this.LegLeft02.yRot = 0.0f
        this.LegLeft02.x = this.legLeft02DefaultX
        this.LegLeft02.y = this.legLeft02DefaultY
        this.LegRight02.yRot = 0.0f
        this.LegRight02.x = this.legRight02DefaultX
        this.LegRight02.y = this.legRight02DefaultY

        this.EquipE01.xRot = 0.05f
        this.EquipE01.yRot = -0.2f
        this.EquipE01.zRot = 0.0f
        this.EquipE01.x = this.equipE01DefaultX
        this.EquipE02.xRot = -0.4887f
        this.EquipE05.xRot = 0.4538f

        this.EquipD02.xRot = 0.25f
        this.EquipD02.yRot = 1.6755f
        this.EquipD02.zRot = 3.1416f
        this.EquipD02.y = this.equipD02DefaultY

        this.EquipS01.xRot = -0.95f
        this.Head.xRot = -0.2618f
        this.Head.yRot = 0.0f
        this.Head.zRot = 0.0f
        this.BoobL.xRot = -1.0f
        this.BoobR.xRot = -1.0f
        this.Ahoke.yRot = -1.0f

        this.BodyMain.xRot = 1.2217f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 1.2217f
        this.Butt.xRot = -0.05f

        this.Hair01.xRot = 0.2f
        this.Hair01.zRot = -0.36f
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

        this.ArmLeft01.xRot = -0.35f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = -3.0f
        this.ArmLeft02.xRot = 0.0f

        this.ArmRight01.xRot = -0.35f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = -0.35f
        this.ArmRight02.xRot = 0.0f
        this.ArmRight02.zRot = -0.8727f

        this.LegLeft01.xRot = -0.14f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.09f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.zRot = 0.0f
        this.LegLeft02.z = this.legLeft02DefaultZ

        this.LegRight01.xRot = -1.2217f
        this.LegRight01.yRot = -0.5236f
        this.LegRight01.zRot = 0.0f
        this.LegRight02.xRot = 1.0472f
        this.LegRight02.zRot = 0.0f
        this.LegRight02.z = this.legRight02DefaultZ
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
        val angleX1 = Mth.cos(ageInTicks * 0.1f + 0.3f + limbSwing * 0.5f)
        val angleX2 = Mth.cos(ageInTicks * 0.1f + 0.6f + limbSwing * 0.5f)
        val angleX3 = Mth.cos(ageInTicks * 0.1f + 0.9f + limbSwing * 0.5f)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount
        val headX = headPitch * (Math.PI.toFloat() / 180f) * -0.5f
        val headZ = if (entity != null) entity.getHeadTiltAngle(ageInTicks) else 0.0f

        this.Head.xRot = headPitch * (Math.PI.toFloat() / 180f) + 0.1047f
        this.Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)
        this.BoobL.xRot = angleX * 0.06f - 0.8f
        this.BoobR.xRot = angleX * 0.06f - 0.8f
        this.Ahoke.yRot = angleX * 0.25f + 0.45f
        this.BodyMain.xRot = -0.1047f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = 0.3142f
        this.Skirt01.xRot = -0.14f
        this.Skirt02.xRot = -0.0873f
        this.ClothHL02_1.y = this.clothHL02_1DefaultY
        this.ClothHL03_1.y = this.clothHL03_1DefaultY

        this.Hair01.xRot = angleX * 0.04f + 0.23f
        this.Hair01.zRot = 0.0f
        this.Hair02.xRot = -angleX1 * 0.07f - 0.1f
        this.Hair02.zRot = 0.0f
        this.HairL01.xRot = -0.16f
        this.HairL02.xRot = 0.1745f
        this.HairR01.xRot = -0.14f
        this.HairR02.xRot = 0.174f
        this.HairL01.zRot = -0.0873f
        this.HairL02.zRot = 0.087f
        this.HairR01.zRot = 0.0873f
        this.HairR02.zRot = -0.053f

        this.ArmLeft01.xRot = angleAdd2 * 0.25f + 0.21f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = angleX * 0.03f - 0.21f
        this.ArmLeft02.xRot = 0.0f

        this.ArmRight01.xRot = angleAdd1 * 0.25f + 0.05f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = -angleX * 0.03f + 0.21f
        this.ArmRight02.zRot = 0.0f
        this.ArmRight02.x = this.armRight02DefaultX

        val state = if (entity != null) entity.getStateEmotion(0) else 0
        val hasBag = (state and (1 shl 3)) != 0
        val hasTail = entity!!.getEquipFlag(EntityCarrierAkagi.EQUIP_CAT_PARTS)
        if (hasBag) {
            this.ArmRight01.zRot += 0.15f
        }
        if (hasTail) {
            this.Tail01.xRot = angleX1 * 0.5f - 0.7f
            this.Tail02.xRot = -angleX2 * 0.5f
            this.Tail03.xRot = -angleX3 * 0.5f
        }

        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.1396f
        this.LegLeft02.xRot = 0.0f
        this.LegLeft02.yRot = 0.0f
        this.LegLeft02.zRot = 0.0f
        this.LegLeft02.x = this.legLeft02DefaultX
        this.LegLeft02.y = this.legLeft02DefaultY
        this.LegLeft02.z = this.legLeft02DefaultZ

        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.1396f
        this.LegRight02.xRot = 0.0f
        this.LegRight02.yRot = 0.0f
        this.LegRight02.zRot = 0.0f
        this.LegRight02.x = this.legRight02DefaultX
        this.LegRight02.y = this.legRight02DefaultY
        this.LegRight02.z = this.legRight02DefaultZ

        this.EquipE01.xRot = 0.05f
        this.EquipE01.yRot = 0.0f
        this.EquipE01.zRot = 0.0f
        this.EquipE01.x = this.equipE01DefaultX
        this.EquipE02.xRot = -0.4887f
        this.EquipE05.xRot = 0.4538f

        this.EquipD01.xRot = 0.0f
        this.EquipD02.xRot = -0.05f
        this.EquipD02.yRot = 1.6755f
        this.EquipD02.zRot = 3.1416f
        this.EquipD02.y = this.equipD02DefaultY

        this.EquipS01.xRot = -0.28f

        var modf2 = ageInTicks % 128.0f
        if (modf2 < 6.0f) {
            if (modf2 >= 3.0f) {
                modf2 -= 3.0f
            }
            val anglef2 = Mth.sin(modf2 * 1.0472f) * 0.25f
            this.Ear01.zRot = -anglef2 - 0.14f
            this.Ear02.zRot = anglef2 + 0.14f
        } else {
            this.Ear01.zRot = -0.14f
            this.Ear02.zRot = 0.14f
        }

        this.Head.zRot = headZ
        this.Hair01.xRot += headX
        this.Hair02.xRot += headX * 0.1f
        this.Hair01.zRot += headZ
        this.Hair02.zRot += headZ * 0.7f
        this.HairL01.zRot += headZ
        this.HairL02.zRot += headZ * 0.8f
        this.HairR01.zRot += headZ
        this.HairR02.zRot += headZ * 0.8f
        this.HairL01.xRot += angleX * 0.04f + headX
        this.HairL02.xRot += angleX1 * 0.07f + headX * 0.8f
        this.HairR01.xRot += angleX * 0.04f + headX
        this.HairR02.xRot += angleX1 * 0.07f + headX * 0.8f
    }

    private fun applySpecialPoseAdjustments(entity: T?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount
        var addk1 = angleAdd1 * 0.5f - 0.2793f
        var addk2 = angleAdd2 * 0.5f - 0.1396f

        val isSprinting = entity != null && entity.isSprinting
        val isCrouching = entity != null && entity.isCrouching()
        val isPassenger = entity != null && entity.isPassenger()
        val isSitting = entity != null && (entity.getIsSitting() || entity.isPassenger())

        if (isSprinting || limbSwingAmount > 0.1f) {
            this.Hair01.xRot = angleAdd1 * 0.1f + limbSwingAmount * 0.4f
            this.Hair02.xRot += 0.5f
            this.ArmLeft01.zRot += limbSwingAmount * -0.2f
            this.ArmRight01.zRot += limbSwingAmount * 0.2f
        }

        if (isCrouching) {
            this.poseTranslateY = SNEAK_TRANSLATE_Y
            this.Head.xRot -= 1.0472f
            this.BodyMain.xRot = 1.0472f
            this.Butt.xRot = -0.8378f
            this.ArmLeft01.xRot = -0.7f
            this.ArmLeft01.zRot = 0.2618f
            this.ArmRight01.xRot = -0.7f
            this.ArmRight01.zRot = -0.2618f
            this.EquipD02.xRot = 0.15f
            this.EquipE01.yRot = 1.3f
            this.Tail01.xRot += 1.3f
        }

        if (isSitting) {
            this.isSittingPose = true
            if (entity != null && entity.getStateEmotion(1) == 4) {
                this.poseTranslateY = 0.43f * 3
                val nodTick = ageInTicks.toInt() % 60
                this.Head.xRot = 0.4f
                if (nodTick < 30) {
                    if (nodTick < 6) {
                        this.Head.xRot = nodTick * 0.02f + 0.4f
                    } else if (nodTick < 11) {
                        this.Head.xRot = (nodTick - 5) * 0.03f + 0.5f
                    } else if (nodTick < 14) {
                        this.Head.xRot = (nodTick - 10) * -0.09f + 0.65f
                    }
                }
                this.Head.yRot = 0.0f
                this.Head.zRot = 0.0f
                this.Butt.xRot = -0.2f
                this.Skirt01.xRot = -0.26f
                this.Skirt02.xRot = -0.45f
                this.ArmLeft01.xRot = 0.4f
                this.ArmLeft01.zRot = -0.2618f
                this.ArmRight01.xRot = 0.4f
                this.ArmRight01.zRot = 0.2618f
                addk1 = -0.9f
                addk2 = -0.9f
                this.LegLeft01.zRot = -0.14f
                this.LegLeft02.xRot = 1.2217f
                this.LegLeft02.yRot = 1.2217f
                this.LegLeft02.zRot = -1.0472f
                this.LegLeft02.x = this.legLeft02DefaultX + (0.17f * OFFSET_SCALE)
                this.LegLeft02.y = this.legLeft02DefaultY + (-0.03f * OFFSET_SCALE)
                this.LegLeft02.z = this.legLeft02DefaultZ + (0.2f * OFFSET_SCALE)
                this.LegRight01.zRot = 0.14f
                this.LegRight02.xRot = 1.2217f
                this.LegRight02.yRot = -1.2217f
                this.LegRight02.zRot = 1.0472f
                this.LegRight02.x = this.legRight02DefaultX + (-0.17f * OFFSET_SCALE)
                this.LegRight02.y = this.legRight02DefaultY + (-0.03f * OFFSET_SCALE)
                this.LegRight02.z = this.legRight02DefaultZ + (0.2f * OFFSET_SCALE)
                this.Tail01.xRot += 1.7f
                this.Tail02.xRot += 0.15f
                this.Tail03.xRot += 0.15f
                this.Tail01.xRot *= 0.2f
                this.Tail02.xRot *= 0.2f
                this.Tail03.xRot *= 0.2f
                this.EquipE01.yRot = 1.7f
                this.EquipE01.zRot = 0.15f
                this.EquipD02.xRot = 0.2f
                this.EquipD02.y = this.equipD02DefaultY + (-0.5f * OFFSET_SCALE)
            } else {
                this.poseTranslateY = SITTING_TRANSLATE_Y
                this.Head.xRot += 0.1047f
                this.BodyMain.xRot = -0.1396f
                this.Butt.xRot = 0.1396f
                this.ArmLeft01.xRot = -0.4f
                this.ArmLeft01.zRot = 0.2618f
                this.ArmRight01.xRot = -0.4f
                this.ArmRight01.zRot = -0.2618f
                addk1 = -1.0472f
                addk2 = -1.0472f
                this.LegLeft01.yRot = 0.0524f
                this.LegLeft01.zRot = 0.0f
                this.LegLeft02.z = this.legLeft02DefaultZ + (0.38f * OFFSET_SCALE)
                this.LegLeft02.xRot = 2.5831f
                this.LegLeft02.zRot = 0.0175f
                this.LegRight01.yRot = -0.0524f
                this.LegRight01.zRot = 0.0f
                this.LegRight02.z = this.legRight02DefaultZ + (0.38f * OFFSET_SCALE)
                this.LegRight02.xRot = 2.5831f
                this.LegRight02.zRot = -0.0175f
                this.Tail01.xRot += 1.0f
                this.Tail02.xRot += 0.15f
                this.Tail03.xRot += 0.15f
                this.EquipE01.yRot = 1.7f
                this.EquipE01.zRot = -0.2f
                this.EquipD02.xRot = 0.2f
                this.EquipD02.y = this.equipD02DefaultY + (-0.5f * OFFSET_SCALE)
            }
        }

        if (entity != null && entity.attackTick > 20) {
            if (entity.attackTick >= 49) {
                entity.attackTick2 = 0
            }
            var tick = entity.attackTick2
            val parTick = ageInTicks - ageInTicks.toInt() + tick
            this.Head.xRot = 0.0f
            this.Head.yRot = -1.31f
            this.BodyMain.xRot = -0.05f
            this.BodyMain.yRot = 1.4f
            this.ClothHL02_1.y = this.clothHL02_1DefaultY + (-0.17f * OFFSET_SCALE)
            this.ClothHL03_1.y = this.clothHL03_1DefaultY + (-0.2f * OFFSET_SCALE)
            this.ArmLeft01.xRot = -1.5708f
            this.ArmLeft01.yRot = -1.35f
            this.ArmLeft01.zRot = 0.0f
            this.ArmRight01.xRot = 0.0f
            this.ArmRight01.yRot = 2.1817f
            this.ArmRight01.zRot = 1.5708f
            this.ArmRight02.zRot = min(-1.57f, -2.44f + 0.15f * parTick)
            this.ArmRight02.x = this.armRight02DefaultX + (0.31f * OFFSET_SCALE)
            addk1 = -0.35f
            addk2 = -0.23f
            this.LegLeft01.zRot = -0.14f
            this.LegRight01.zRot = 0.14f
            this.EquipD01.xRot = 1.3f
            this.EquipD02.xRot = -1.15f
            this.EquipD02.yRot = -2.0f
            this.EquipD02.zRot = 1.7453f
            this.EquipE01.xRot = 0.2618f
            this.EquipE01.zRot = -0.23f
            this.EquipE01.x = this.equipE01DefaultX + (-0.15f * OFFSET_SCALE)
            this.EquipE02.xRot = min(-0.49f, -0.7f + 0.1f * parTick)
            this.EquipE05.xRot = max(0.45f, 0.7f - 0.1f * parTick)
            if (tick > 5 && tick < 12) {
                val wave = Mth.sin(parTick * 0.2244f)
                this.EquipE01.xRot -= 0.36f * wave
                this.EquipE01.zRot -= 5.0f * wave
            }
            if (tick >= 12) {
                this.EquipE01.xRot = -0.1f
                this.EquipE01.zRot = -3.3f
            }
            entity.attackTick2 = ++tick
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

        this.LegLeft01.xRot = addk1
        this.LegRight01.xRot = addk2
    }

    private fun syncGlowParts() {
        if (this.GlowBodyMain != null) {
            this.GlowBodyMain.copyFrom(this.BodyMain)
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

        if (this.GlowBodyMain != null) {
            this.GlowBodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        }

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "carrier_akagi"), "main")

        private val DEAD_TRANSLATE_Y = deadY("ModelCarrierAkagi")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelCarrierAkagi")
        private val SITTING_TRANSLATE_Y = sittingY("ModelCarrierAkagi")
        private const val OFFSET_SCALE = 16.0f

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 104).addBox(-6.5f, -11f, -4f, 13f, 17f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val ClothBody02 = BodyMain.addOrReplaceChild(
                "ClothBody02",
                CubeListBuilder.create().texOffs(0, 113).addBox(-1f, 0f, 0f, 2f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, -3.8f, -2.3f, 0.2618f, 0f, 0.2618f)
            )

            val Head = BodyMain.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -11.8f, -1f, 0.1047f, 0f, 0f)
            )

            val Ear02 = Head.addOrReplaceChild(
                "Ear02",
                CubeListBuilder.create().texOffs(20, 0).addBox(-1.5f, 0f, -6f, 3f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.8f, -14.5f, 5.7f, -0.7854f, -0.2618f, 0.1396f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 81).addBox(-8f, -8f, -7.4f, 16f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0.2f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(104, 29).addBox(0f, -4f, -11.5f, 0f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, -9f, -5.5f, 0.0873f, 0.6981f, 0f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(86, 101).mirror().addBox(-1f, 0f, 0f, 2f, 8f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7f, 3f, -5.5f, -0.1396f, 0.1745f, 0.0873f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(86, 101).mirror().addBox(-1f, 0f, 0f, 2f, 9f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 7f, 0f, 0.1745f, 0f, -0.0524f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(86, 101).addBox(-1f, 0f, 0f, 2f, 8f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, 3f, -5.5f, -0.1396f, -0.1745f, -0.0873f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(86, 101).addBox(-1f, 0f, 0f, 2f, 9f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0.1745f, 0f, 0.0873f)
            )

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(82, 0).addBox(-8.5f, 0f, 0f, 17f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -6.5f)
            )

            val Ear01 = Head.addOrReplaceChild(
                "Ear01",
                CubeListBuilder.create().texOffs(20, 0).mirror()
                    .addBox(-1.5f, 0f, -6f, 3f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.8f, -14.5f, 5.7f, -0.7854f, 0.2618f, -0.1396f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(48, 34).addBox(-7.5f, 0f, 0f, 15f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.8f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(189, 0).addBox(-7.5f, 0f, 0f, 15f, 14f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9.5f, 1f, 0.2094f, 0f, 0f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(192, 25).addBox(-7f, 0f, -4.5f, 14f, 13f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 11f, 6.2f, -0.1047f, 0f, 0f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(33, 101).mirror()
                    .addBox(-3.5f, 0f, 0f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.8f, -8.5f, -3.5f, -0.6981f, -0.1047f, -0.0873f)
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

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 83).mirror().addBox(-3f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 14f, -3f)
            )

            val EquipSL01 = LegLeft02.addOrReplaceChild(
                "EquipSL01",
                CubeListBuilder.create().texOffs(24, 90).mirror()
                    .addBox(-3f, 0f, -3.5f, 6f, 4f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 15f, 3f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 63).addBox(-3f, 0f, -3f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.8f, 5.5f, -2.6f, -0.1396f, 0f, -0.1396f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 83).addBox(-3f, 0f, 0f, 6f, 15f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 14f, -3f)
            )

            val EquipSR01 = LegRight02.addOrReplaceChild(
                "EquipSR01",
                CubeListBuilder.create().texOffs(24, 90).addBox(-3f, 0f, -3.5f, 6f, 4f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 15f, 3f)
            )

            val Skirt01 = Butt.addOrReplaceChild(
                "Skirt01",
                CubeListBuilder.create().texOffs(0, 28).addBox(-8.5f, 0f, -6.3f, 17f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.3f, 0f, -0.1396f, 0f, 0f)
            )

            val Cloth08 = Skirt01.addOrReplaceChild(
                "Cloth08",
                CubeListBuilder.create().texOffs(24, 80).addBox(-3f, 0f, 0f, 3f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.5f, 0.5f, -7f, -0.1571f, -0.1047f, 0.1745f)
            )

            val Cloth09 = Skirt01.addOrReplaceChild(
                "Cloth09",
                CubeListBuilder.create().texOffs(34, 80).addBox(-3f, 0f, 0f, 6f, 10f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.2f, -6.8f, -0.1396f, 0f, 0f)
            )

            val EquipS01 = Skirt01.addOrReplaceChild(
                "EquipS01",
                CubeListBuilder.create().texOffs(58, 55).addBox(-5.5f, 0f, 0f, 11f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.3f, -7.3f, -0.2793f, 0f, 0f)
            )

            val Skirt02 = Skirt01.addOrReplaceChild(
                "Skirt02",
                CubeListBuilder.create().texOffs(0, 44).addBox(-9f, 0f, -6f, 18f, 8f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, -0.6f, -0.0873f, 0f, 0f)
            )

            val Cloth07 = Skirt01.addOrReplaceChild(
                "Cloth07",
                CubeListBuilder.create().texOffs(24, 80).addBox(0f, 0f, 0f, 3f, 8f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.5f, 0.5f, -7f, -0.1745f, -0.1396f, -0.2094f)
            )

            val Tail01 = Butt.addOrReplaceChild(
                "Tail01",
                CubeListBuilder.create().texOffs(63, 36).addBox(-1f, -1f, 0f, 2f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6.5f, 1f, -0.8727f, 0f, 0f)
            )

            val Tail02 = Tail01.addOrReplaceChild(
                "Tail02",
                CubeListBuilder.create().texOffs(63, 36).addBox(-1f, -1f, 0f, 2f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 7.5f, 0.6981f, 0f, 0f)
            )

            val Tail03 = Tail02.addOrReplaceChild(
                "Tail03",
                CubeListBuilder.create().texOffs(63, 36).addBox(-1f, -1f, 0f, 2f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 7.5f, 0.6981f, 0f, 0f)
            )

            val EquipABase = BodyMain.addOrReplaceChild(
                "EquipABase",
                CubeListBuilder.create().texOffs(44, 35).addBox(-0.5f, -1f, -0.3f, 3f, 2f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, -8f, 3.6f, 0f, 0.1396f, 0f)
            )

            val EquipABelt01 = EquipABase.addOrReplaceChild(
                "EquipABelt01",
                CubeListBuilder.create().texOffs(0, 27).addBox(-12f, 0f, -0.5f, 12f, 0f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0.3491f, 0.1396f, -0.5236f)
            )

            val EquipABody01 = EquipABelt01.addOrReplaceChild(
                "EquipABody01",
                CubeListBuilder.create().texOffs(86, 55).addBox(-5f, -5.5f, -1f, 4f, 8f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-12.5f, -2.5f, 0f, 0f, 0f, -0.7854f)
            )

            val EquipABody02 = EquipABody01.addOrReplaceChild(
                "EquipABody02",
                CubeListBuilder.create().texOffs(128, 37).addBox(-0.5f, 0f, -0.5f, 1f, 6f, 1f, CubeDeformation(0f)),
                PartPose.offset(-3.5f, -11.4f, 0f)
            )

            val EquipABody03 = EquipABody01.addOrReplaceChild(
                "EquipABody03",
                CubeListBuilder.create().texOffs(128, 34).addBox(-3.5f, -0.5f, -0.5f, 7f, 1f, 1f, CubeDeformation(0f)),
                PartPose.offset(-3.5f, -6.5f, 0f)
            )

            val EquipABody04 = EquipABody01.addOrReplaceChild(
                "EquipABody04",
                CubeListBuilder.create().texOffs(128, 28).addBox(-3f, 0f, -3f, 6f, 3f, 6f, CubeDeformation(0f)),
                PartPose.offset(-8f, 0f, 0f)
            )

            val EquipAArr01a = EquipABody04.addOrReplaceChild(
                "EquipAArr01a",
                CubeListBuilder.create().texOffs(4, 47).addBox(0f, -4f, 0f, 1f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.5f, 0.7f, 0f, 0f, 0f, 0.0524f)
            )

            val EquipAArr01b = EquipAArr01a.addOrReplaceChild(
                "EquipAArr01b",
                CubeListBuilder.create().texOffs(0, 48).addBox(-0.5f, -2.7f, 0.5f, 2f, 4f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -2f, 0f)
            )

            val EquipAArr02a = EquipABody04.addOrReplaceChild(
                "EquipAArr02a",
                CubeListBuilder.create().texOffs(4, 47).addBox(0f, -4f, 0f, 1f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.5f, 0.3f, -1.1f, 0.0524f, -0.3187f, -0.0524f)
            )

            val EquipAArr02b = EquipAArr02a.addOrReplaceChild(
                "EquipAArr02b",
                CubeListBuilder.create().texOffs(0, 48).addBox(-0.5f, -2.7f, 0.5f, 2f, 4f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -2f, 0f)
            )

            val EquipABody05 = EquipABody04.addOrReplaceChild(
                "EquipABody05",
                CubeListBuilder.create().texOffs(128, 13).addBox(-2.5f, 0f, -2.5f, 5f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 3f, 0f)
            )

            val EquipABody05b = EquipABody05.addOrReplaceChild(
                "EquipABody05b",
                CubeListBuilder.create().texOffs(128, 13).addBox(-2.5f, 0f, -2.5f, 5f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 10f, 0f)
            )

            val EquipABody05c = EquipABody05b.addOrReplaceChild(
                "EquipABody05c",
                CubeListBuilder.create().texOffs(128, 13).addBox(-2.5f, 0f, -2.5f, 5f, 10f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 10f, 0f)
            )

            val EquipABelt02 = EquipABody05c.addOrReplaceChild(
                "EquipABelt02",
                CubeListBuilder.create().texOffs(0, 27).addBox(0f, 0f, -0.5f, 17f, 0f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3f, 2f, 0f, 0f, 0f, -0.7741f)
            )

            val EquipAArr03a = EquipABody04.addOrReplaceChild(
                "EquipAArr03a",
                CubeListBuilder.create().texOffs(4, 47).addBox(0f, -4f, 0f, 1f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.6f, 0f, 0.4f, -0.0349f, -0.2618f, 0f)
            )

            val EquipAArr03b = EquipAArr03a.addOrReplaceChild(
                "EquipAArr03b",
                CubeListBuilder.create().texOffs(0, 48).addBox(-0.5f, -2.7f, 0.5f, 2f, 4f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -2f, 0f)
            )

            val Cloth02 = BodyMain.addOrReplaceChild(
                "Cloth02",
                CubeListBuilder.create().texOffs(44, 19).mirror()
                    .addBox(0f, -3.5f, -4.6f, 1f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.8f, -7.9f, 0f, 0.0873f, -0.1396f, -0.1396f)
            )

            val Cloth03 = Cloth02.addOrReplaceChild(
                "Cloth03",
                CubeListBuilder.create().texOffs(0, 64).addBox(0f, -4f, -1f, 1f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, -4.6f, 0.1396f, -0.3142f, 0.1396f)
            )

            val Cloth04 = Cloth02.addOrReplaceChild(
                "Cloth04",
                CubeListBuilder.create().texOffs(0, 64).addBox(0f, 0f, -1f, 1f, 4f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.1f, 0.6f, -4.5f, -0.1396f, -0.3491f, -0.2094f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(0, 8).addBox(-3f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, -8.7f, -0.7f, 0f, 0f, 0.3142f)
            )

            val EquipD01 = ArmRight01.addOrReplaceChild(
                "EquipD01",
                CubeListBuilder.create().texOffs(150, 13).addBox(-3f, 0f, -3.5f, 8f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.3f, 2f, 0f, 0f, 3.1416f, 0f)
            )

            val EquipD02 = EquipD01.addOrReplaceChild(
                "EquipD02",
                CubeListBuilder.create().texOffs(58, 55).addBox(-5.5f, 0f, 0f, 11f, 9f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.6f, 3f, 0f, -0.0349f, 1.4661f, 3.1416f)
            )

            val EquipD03 = EquipD02.addOrReplaceChild(
                "EquipD03",
                CubeListBuilder.create().texOffs(153, 21).addBox(-5.5f, -26f, 0f, 11f, 26f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipD04 = EquipD03.addOrReplaceChild(
                "EquipD04",
                CubeListBuilder.create().texOffs(128, 90).addBox(-4.5f, 0f, 0f, 9f, 11f, 1f, CubeDeformation(0f)),
                PartPose.offset(0f, -37f, 0f)
            )

            val ClothHL01_1 = ArmRight01.addOrReplaceChild(
                "ClothHL01_1",
                CubeListBuilder.create().texOffs(43, 1).addBox(-3.5f, 0f, -3f, 6f, 5f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -1.5f, 0f)
            )

            val ClothHL02_1 = ClothHL01_1.addOrReplaceChild(
                "ClothHL02_1",
                CubeListBuilder.create().texOffs(42, 1).addBox(-4f, 0f, -3f, 7f, 5f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 4.5f, 0f)
            )

            val ClothHL03_1 = ClothHL02_1.addOrReplaceChild(
                "ClothHL03_1",
                CubeListBuilder.create().texOffs(40, 0).addBox(-3.5f, 0f, -3.5f, 8f, 5f, 7f, CubeDeformation(0f)),
                PartPose.offset(-1f, 4f, 0f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(0, 8).addBox(0f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 11f, 2.5f)
            )

            val EquipGlove = ArmRight02.addOrReplaceChild(
                "EquipGlove",
                CubeListBuilder.create().texOffs(128, 103).addBox(-3f, 0f, -3f, 6f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offset(2.5f, 6.3f, -2.5f)
            )

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(98, 31).addBox(-4f, 0f, -4f, 8f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -12.1f, -0.6f, 0.1745f, 0f, 0f)
            )

            val EquipB01 = BodyMain.addOrReplaceChild(
                "EquipB01",
                CubeListBuilder.create().texOffs(62, 22).addBox(-7f, -6f, -6f, 14f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -4.2f, 0.7f, 0.6981f, 0f, 0f)
            )

            val ClothBody01 = BodyMain.addOrReplaceChild(
                "ClothBody01",
                CubeListBuilder.create().texOffs(0, 113).addBox(-1f, 0f, 0f, 2f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, -3.8f, -2.3f, 0.2618f, 0f, -0.2618f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(0, 8).mirror()
                    .addBox(-2f, -1f, -2.5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -8.7f, -0.7f, 0.2094f, 0f, -0.2094f)
            )

            val ClothHL01 = ArmLeft01.addOrReplaceChild(
                "ClothHL01",
                CubeListBuilder.create().texOffs(43, 1).addBox(-2.5f, 0f, -3f, 6f, 5f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, -1.5f, 0f)
            )

            val ClothHL02 = ClothHL01.addOrReplaceChild(
                "ClothHL02",
                CubeListBuilder.create().texOffs(42, 1).addBox(-3f, 0f, -3f, 7f, 5f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 4.5f, 0f)
            )

            val ClothHL03 = ClothHL02.addOrReplaceChild(
                "ClothHL03",
                CubeListBuilder.create().texOffs(40, 0).addBox(-2.5f, 0f, -3.5f, 8f, 5f, 7f, CubeDeformation(0f)),
                PartPose.offset(-1f, 4f, 0f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(0, 8).mirror().addBox(-5f, 0f, -5f, 5f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(3f, 11f, 2.5f)
            )

            val EquipE01 = ArmLeft02.addOrReplaceChild(
                "EquipE01",
                CubeListBuilder.create().texOffs(128, 37).addBox(-0.5f, -0.5f, -20f, 1f, 1f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.8f, 10.5f, -3f, 0.0524f, 0f, 0f)
            )

            val EquipE02 = EquipE01.addOrReplaceChild(
                "EquipE02",
                CubeListBuilder.create().texOffs(128, 74).addBox(-0.5f, -0.5f, -15f, 1f, 1f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -19.7f, -0.4887f, 0f, 0f)
            )

            val EquipE03 = EquipE02.addOrReplaceChild(
                "EquipE03",
                CubeListBuilder.create().texOffs(134, 80).addBox(-0.5f, -0.5f, -9f, 1f, 1f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -14.7f, 0.3142f, 0f, 0f)
            )

            val EquipE04 = EquipE01.addOrReplaceChild(
                "EquipE04",
                CubeListBuilder.create().texOffs(133, 58).addBox(-0.5f, -0.5f, 0f, 1f, 1f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -0.2f, -0.1396f, 0f, 0f)
            )

            val EquipE05 = EquipE04.addOrReplaceChild(
                "EquipE05",
                CubeListBuilder.create().texOffs(131, 77).addBox(-0.5f, -0.5f, 0f, 1f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 14.7f, 0.4538f, 0f, 0f)
            )

            val EquipE06 = EquipE05.addOrReplaceChild(
                "EquipE06",
                CubeListBuilder.create().texOffs(135, 81).addBox(-0.5f, -0.5f, 0f, 1f, 1f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 11.7f, -0.2793f, 0f, 0f)
            )

            val Cloth06 = BodyMain.addOrReplaceChild(
                "Cloth06",
                CubeListBuilder.create().texOffs(104, 21).addBox(0f, 0f, 0f, 12f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, -11.6f, 3.2f, 0.0698f, 0f, 0f)
            )

            val EquipC01 = BodyMain.addOrReplaceChild(
                "EquipC01",
                CubeListBuilder.create().texOffs(128, 0).addBox(-9f, 0f, -4f, 18f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.2f, 6.4f, -0.8f, 0f, 0.0873f, -0.182f)
            )

            val EquipC02 = EquipC01.addOrReplaceChild(
                "EquipC02",
                CubeListBuilder.create().texOffs(64, 7).addBox(-2.5f, 0f, -3f, 3f, 9f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8f, -0.5f, 1.5f, 0.1745f, 0f, 0.3491f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(33, 101).addBox(-3.5f, 0f, 0f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.8f, -8.5f, -3.5f, -0.6981f, 0.1047f, 0.0873f)
            )

            val Cloth05 = BodyMain.addOrReplaceChild(
                "Cloth05",
                CubeListBuilder.create().texOffs(44, 19).addBox(-1f, -3.5f, -4.6f, 1f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5.8f, -7.9f, 0f, 0.0873f, 0.1396f, 0.1396f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -15f, 0f, -0.1047f, 0f, 0f)
            )

            val GlowHead = GlowBodyMain.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -11.8f, -1f, 0.1047f, 0f, 0f)
            )

            addFaceLayer(GlowHead)

            return LayerDefinition.create(meshdefinition, 256, 128)
        }
    }
}