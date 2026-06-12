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
import org.trp.shincolle.client.model.LegacyPoseOffsets.sittingY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sneakY
import org.trp.shincolle.entity.EntitySubmRo500
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelSubmRo500<T : EntityShipBase>(root: ModelPart) : ShincolleShipModel<T>() {

    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val Butt: ModelPart
    private val Cloth01: ModelPart
    private val EquipBase1: ModelPart
    private val EquipBase2: ModelPart
    private val Head: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val FlowerBase: ModelPart
    private val Ahoke: ModelPart
    private val HairL01: ModelPart
    private val HairR01: ModelPart
    private val HairL02: ModelPart
    private val HairR02: ModelPart
    private val Hair01: ModelPart
    private val Flower1: ModelPart
    private val Flower2: ModelPart
    private val Flower3: ModelPart
    private val Flower4: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmRight02: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft01: ModelPart
    private val LegRight02: ModelPart
    private val LegLeft02: ModelPart
    private val Equip101: ModelPart
    private val Equip102: ModelPart
    private val Equip103: ModelPart
    private val Equip104: ModelPart
    private val Equip201: ModelPart
    private val Equip202: ModelPart
    private val Equip203: ModelPart
    private val Equip204: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val armLeft01DefaultXRot: Float
    private val armLeft01DefaultYRot: Float
    private val armLeft01DefaultZRot: Float
    private val armRight01DefaultXRot: Float
    private val armRight01DefaultYRot: Float
    private val armRight01DefaultZRot: Float
    private val armLeft02DefaultXRot: Float
    private val armRight02DefaultXRot: Float
    private val legLeft01DefaultXRot: Float
    private val legLeft01DefaultYRot: Float
    private val legLeft01DefaultZRot: Float
    private val legRight01DefaultXRot: Float
    private val legRight01DefaultYRot: Float
    private val legRight01DefaultZRot: Float
    private val legLeft02DefaultXRot: Float
    private val legRight02DefaultXRot: Float
    private val hair01DefaultXRot: Float
    private val hair01DefaultZRot: Float
    private val hairL01DefaultXRot: Float
    private val hairL02DefaultXRot: Float
    private val hairR01DefaultXRot: Float
    private val hairR02DefaultXRot: Float
    private val hairL01DefaultZRot: Float
    private val hairL02DefaultZRot: Float
    private val hairR01DefaultZRot: Float
    private val hairR02DefaultZRot: Float
    private val equipBase1DefaultZ: Float
    private val equipBase2DefaultY: Float
    private val equipBase2DefaultXRot: Float

    protected override val bodyMain: ModelPart get() = BodyMain
    protected override val neck: ModelPart get() = Neck
    protected override val head: ModelPart get() = Head
    protected override val glowBodyMain: ModelPart? get() = GlowBodyMain
    protected override val glowNeck: ModelPart? get() = GlowNeck
    protected override val glowHead: ModelPart? get() = GlowHead

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.EquipBase1 = this.BodyMain.getChild("EquipBase1")
        this.Equip101 = this.EquipBase1.getChild("Equip101")
        this.Equip103 = this.Equip101.getChild("Equip103")
        this.Equip104 = this.Equip101.getChild("Equip104")
        this.Equip102 = this.Equip101.getChild("Equip102")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.EquipBase2 = this.BodyMain.getChild("EquipBase2")
        this.Equip204 = this.EquipBase2.getChild("Equip204")
        this.Equip203 = this.EquipBase2.getChild("Equip203")
        this.Equip202 = this.EquipBase2.getChild("Equip202")
        this.Equip201 = this.EquipBase2.getChild("Equip201")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        this.FlowerBase = this.GlowHead.getChild("FlowerBase")
        this.Flower1 = this.FlowerBase.getChild("Flower1")
        this.Flower2 = this.FlowerBase.getChild("Flower2")
        this.Flower3 = this.FlowerBase.getChild("Flower3")
        this.Flower4 = this.FlowerBase.getChild("Flower4")
        this.initFaceParts(this.GlowHead)
        this.armLeft01DefaultXRot = this.ArmLeft01.xRot
        this.armLeft01DefaultYRot = this.ArmLeft01.yRot
        this.armLeft01DefaultZRot = this.ArmLeft01.zRot
        this.armRight01DefaultXRot = this.ArmRight01.xRot
        this.armRight01DefaultYRot = this.ArmRight01.yRot
        this.armRight01DefaultZRot = this.ArmRight01.zRot
        this.armLeft02DefaultXRot = this.ArmLeft02.xRot
        this.armRight02DefaultXRot = this.ArmRight02.xRot
        this.legLeft01DefaultXRot = this.LegLeft01.xRot
        this.legLeft01DefaultYRot = this.LegLeft01.yRot
        this.legLeft01DefaultZRot = this.LegLeft01.zRot
        this.legRight01DefaultXRot = this.LegRight01.xRot
        this.legRight01DefaultYRot = this.LegRight01.yRot
        this.legRight01DefaultZRot = this.LegRight01.zRot
        this.legLeft02DefaultXRot = this.LegLeft02.xRot
        this.legRight02DefaultXRot = this.LegRight02.xRot
        this.hair01DefaultXRot = this.Hair01.xRot
        this.hair01DefaultZRot = this.Hair01.zRot
        this.hairL01DefaultXRot = this.HairL01.xRot
        this.hairL02DefaultXRot = this.HairL02.xRot
        this.hairR01DefaultXRot = this.HairR01.xRot
        this.hairR02DefaultXRot = this.HairR02.xRot
        this.hairL01DefaultZRot = this.HairL01.zRot
        this.hairL02DefaultZRot = this.HairL02.zRot
        this.hairR01DefaultZRot = this.HairR01.zRot
        this.hairR02DefaultZRot = this.HairR02.zRot
        this.equipBase1DefaultZ = this.EquipBase1.z
        this.equipBase2DefaultY = this.EquipBase2.y
        this.equipBase2DefaultXRot = this.EquipBase2.xRot
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
        this.isDeadPose = false
        this.isSittingPose = false
        this.poseTranslateY = 0.0f

        this.ArmLeft01.xRot = this.armLeft01DefaultXRot
        this.ArmLeft01.yRot = this.armLeft01DefaultYRot
        this.ArmLeft01.zRot = this.armLeft01DefaultZRot
        this.ArmRight01.xRot = this.armRight01DefaultXRot
        this.ArmRight01.yRot = this.armRight01DefaultYRot
        this.ArmRight01.zRot = this.armRight01DefaultZRot
        this.ArmLeft02.xRot = this.armLeft02DefaultXRot
        this.ArmRight02.xRot = this.armRight02DefaultXRot
        this.LegLeft01.xRot = this.legLeft01DefaultXRot
        this.LegLeft01.yRot = this.legLeft01DefaultYRot
        this.LegLeft01.zRot = this.legLeft01DefaultZRot
        this.LegRight01.xRot = this.legRight01DefaultXRot
        this.LegRight01.yRot = this.legRight01DefaultYRot
        this.LegRight01.zRot = this.legRight01DefaultZRot
        this.LegLeft02.xRot = this.legLeft02DefaultXRot
        this.LegRight02.xRot = this.legRight02DefaultXRot
        this.Hair01.xRot = this.hair01DefaultXRot
        this.Hair01.zRot = this.hair01DefaultZRot
        this.HairL01.xRot = this.hairL01DefaultXRot
        this.HairL02.xRot = this.hairL02DefaultXRot
        this.HairR01.xRot = this.hairR01DefaultXRot
        this.HairR02.xRot = this.hairR02DefaultXRot
        this.HairL01.zRot = this.hairL01DefaultZRot
        this.HairL02.zRot = this.hairL02DefaultZRot
        this.HairR01.zRot = this.hairR01DefaultZRot
        this.HairR02.zRot = this.hairR02DefaultZRot
        this.EquipBase1.z = this.equipBase1DefaultZ
        this.EquipBase2.y = this.equipBase2DefaultY
        this.EquipBase2.xRot = this.equipBase2DefaultXRot
    }

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) return
        this.EquipBase1.visible = entity.getEquipFlag(EntitySubmRo500.EQUIP_BASE_1)
        this.EquipBase2.visible = entity.getEquipFlag(EntitySubmRo500.EQUIP_BASE_2)
        this.FlowerBase.visible = entity.getEquipFlag(EntitySubmRo500.EQUIP_FLOWER)
    }

    private fun applyDeadPose() {
        beginDeadPose(DEAD_TRANSLATE_Y)

        this.Head.xRot = -0.35f
        this.Head.yRot = 0.0f
        this.Ahoke.yRot = 0.5236f
        this.BodyMain.xRot = -1.6f
        this.Hair01.xRot = 0.3f
        this.ArmLeft01.xRot = 3.1f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = 0.7f
        this.ArmRight01.xRot = 3.1f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = -0.7f
        this.ArmLeft02.xRot = 0.0f
        this.ArmRight02.xRot = 0.0f

        this.LegLeft01.xRot = -0.2f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = -0.1f
        this.LegRight01.xRot = -0.2f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = 0.1f
        this.LegLeft02.xRot = 0.0f
        this.LegRight02.xRot = 0.0f

        this.EquipBase1.z = this.equipBase1DefaultZ
        this.EquipBase2.y = this.equipBase2DefaultY
        this.EquipBase2.xRot = 0.3142f
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
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount

        if (entity!!.shipDepth > 0.0) {
            this.poseTranslateY = angleX * 0.05f + 0.025f
        }

        val addk1 = angleAdd1 - 0.122f
        val addk2 = angleAdd2 - 0.122f

        this.Head.xRot = headPitch * (Math.PI.toFloat() / 180f) * 0.8f
        this.Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f) * 0.7f
        this.Head.zRot = 0.0f

        this.Ahoke.yRot = angleX * 0.25f + 0.5236f
        this.BodyMain.xRot = -0.1f
        this.Hair01.xRot = angleX * 0.06f + 0.3f
        this.Hair01.zRot = 0.0f
        this.HairL01.xRot = -0.17f
        this.HairL02.xRot = 0.17f
        this.HairR01.xRot = -0.17f
        this.HairR02.xRot = 0.17f
        this.HairL01.zRot = -0.14f
        this.HairL02.zRot = 0.08f
        this.HairR01.zRot = 0.14f
        this.HairR02.zRot = -0.05f

        this.ArmLeft01.xRot = 0.157f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = -0.384f
        this.ArmRight01.xRot = 0.157f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = 0.384f

        this.ArmLeft02.xRot = 0.0f
        this.ArmRight02.xRot = 0.0f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = -0.035f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = 0.035f
        this.LegLeft02.xRot = 0.0f
        this.LegRight02.xRot = 0.0f

        this.EquipBase1.z = this.equipBase1DefaultZ
        this.EquipBase2.y = this.equipBase2DefaultY
        this.EquipBase2.xRot = 0.3142f

        this.LegLeft01.xRot = addk1
        this.LegRight01.xRot = addk2
    }

    private fun applySpecialPoseAdjustments(entity: T?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        val isPassenger = entity!!.isPassenger
        val isCrouching = entity.isCrouching
        val isSprinting = if (entity != null) entity.isSprinting else limbSwingAmount > 0.9f
        val isSitting = entity.isInSittingPose || (isPassenger && entity.vehicle !is EntityMountBase)

        if (isSprinting) {
            this.BodyMain.xRot = 0.1745f
            this.Head.xRot -= 0.35f
            this.LegLeft01.xRot -= 0.25f
            this.LegRight01.xRot -= 0.25f

            if (Mth.sin(ageInTicks * 0.15f) > 0.0f) {
                this.ArmLeft01.xRot = 2.6f
                this.ArmLeft01.zRot = 0.7f
                this.ArmRight01.xRot = 2.6f
                this.ArmRight01.zRot = -0.7f
            } else {
                this.ArmRight01.xRot = -2.8f
                this.ArmRight01.zRot = -0.7f
            }
        }

        if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            this.Head.xRot -= 0.8727f
            this.BodyMain.xRot = 1.0472f
            this.Hair01.xRot += 0.2236f
            this.LegLeft01.xRot -= 1.2f
            this.LegRight01.xRot -= 1.2f
        }

        if (isSitting) {
            val angleX2 = Mth.cos(ageInTicks * 0.25f)
            if (entity != null && hasLegacyState(entity, 1, 4)) {
                if (entity.shipDepth > 0.0) {
                    this.poseTranslateY -= 0.21f
                } else {
                    this.poseTranslateY += 0.43f * 3.2f
                }
                this.Head.xRot += 0.35f
                this.BodyMain.xRot = -0.7f
                this.ArmLeft01.xRot = 0.5236f
                this.ArmLeft01.zRot = -0.5236f
                this.ArmLeft02.xRot = -1.0472f
                this.ArmRight01.xRot = 0.7f
                this.ArmRight01.zRot = 0.5236f
                this.ArmRight02.xRot = -1.0472f
                this.LegLeft01.xRot = -1.9f
                this.LegRight01.xRot = -1.9f
                this.LegLeft02.xRot = angleX2 * 0.4f + 0.8f
                this.LegRight02.xRot = -angleX2 * 0.4f + 0.8f
                this.EquipBase1.z = this.equipBase1DefaultZ + (-0.9f * OFFSET_SCALE)
                this.EquipBase2.visible = true
                this.EquipBase2.xRot = 0.7f
            } else {
                if (entity.shipDepth > 0.0) {
                    this.poseTranslateY -= 0.22f
                } else {
                    this.poseTranslateY += SITTING_TRANSLATE_Y
                }
                this.Head.xRot += 0.2f
                this.BodyMain.xRot = -0.7f
                this.ArmLeft01.xRot = 0.95f
                this.ArmLeft01.zRot = -0.3146f
                this.ArmRight01.xRot = 0.95f
                this.ArmRight01.zRot = 0.3146f
                this.LegLeft01.xRot = -1.1f
                this.LegRight01.xRot = -1.1f
                this.LegLeft01.yRot = -0.3491f
                this.LegRight01.yRot = 0.3491f
                this.EquipBase1.z = this.equipBase1DefaultZ + (-0.15f * OFFSET_SCALE)
                this.EquipBase2.y = this.equipBase2DefaultY + (-0.15f * OFFSET_SCALE)
            }
        }

        if (entity != null && entity.attackTick > 41) {
            var ft = (50 - entity.attackTick) + (ageInTicks - ageInTicks.toInt())
            val fa = Mth.sin((0.125f.let { ft *= it; ft }) * ft * Math.PI.toFloat())
            val fb = Mth.sin(Mth.sqrt(ft) * Math.PI.toFloat())
            this.ArmLeft01.xRot += -fb * 180.0f * (Math.PI.toFloat() / 180f) + 0.1f
            this.ArmLeft01.yRot += fa * 20.0f * (Math.PI.toFloat() / 180f) - 0.6f
            this.ArmLeft01.zRot += fb * 20.0f * (Math.PI.toFloat() / 180f)
            this.ArmRight01.xRot += -fb * 180.0f * (Math.PI.toFloat() / 180f) + 0.1f
            this.ArmRight01.yRot += -fa * 20.0f * (Math.PI.toFloat() / 180f) + 0.6f
            this.ArmRight01.zRot += -fb * 20.0f * (Math.PI.toFloat() / 180f)
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

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "subm_ro_500"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelSubmRo500")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelSubmRo500")
        private val SITTING_TRANSLATE_Y = sittingY("ModelSubmRo500")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 104).addBox(-6.5f, -11f, -4f, 13f, 17f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -13.5f, 0f, -0.1047f, 0f, 0f)
            )

            val EquipBase1 = BodyMain.addOrReplaceChild(
                "EquipBase1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 7f, 18f)
            )

            val Equip101 = EquipBase1.addOrReplaceChild(
                "Equip101",
                CubeListBuilder.create().texOffs(0, 0).addBox(-15f, -2.5f, -2.5f, 36f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -6f, -9.5f, 0.5236f, 0.0524f, 0.1396f)
            )

            val Equip103 = Equip101.addOrReplaceChild(
                "Equip103",
                CubeListBuilder.create().texOffs(24, 73).addBox(0f, -1f, -3f, 7f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-22f, 0f, 0f, 0.7854f, 0f, 0f)
            )

            val Equip104 = Equip101.addOrReplaceChild(
                "Equip104",
                CubeListBuilder.create().texOffs(54, 10).addBox(0f, -1.5f, -1.5f, 2f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offset(21f, 0f, 0f)
            )

            val Equip102 = Equip101.addOrReplaceChild(
                "Equip102",
                CubeListBuilder.create().texOffs(28, 73).addBox(0f, -3f, -1f, 7f, 6f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-22f, 0f, 0f, 0.7854f, 0f, 0f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(82, 18).addBox(-7.5f, 4.8f, -5.6f, 15f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0.2618f, 0f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 85).addBox(-3f, 0f, -3f, 6f, 13f, 6f, CubeDeformation(0f)),
                PartPose.offset(4.2f, 11f, -2.2f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 65).mirror().addBox(-3f, 0f, 0f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 13f, -3f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 85).addBox(-3f, 0f, -3f, 6f, 13f, 6f, CubeDeformation(0f)),
                PartPose.offset(-4.2f, 11f, -2.2f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 65).addBox(-3f, 0f, 0f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 13f, -3f)
            )

            val EquipBase2 = BodyMain.addOrReplaceChild(
                "EquipBase2",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8f, -2f, 0.3142f, 0f, 0f)
            )

            val Equip204 = EquipBase2.addOrReplaceChild(
                "Equip204",
                CubeListBuilder.create().texOffs(0, 10).addBox(0f, 0f, 0f, 24f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offset(-9f, 0f, -14f)
            )

            val Equip203 = EquipBase2.addOrReplaceChild(
                "Equip203",
                CubeListBuilder.create().texOffs(46, 10).addBox(0f, 0f, 0f, 6f, 6f, 24f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(9f, 6f, 16f, -Math.PI.toFloat(), 0f, 0f)
            )

            val Equip202 = EquipBase2.addOrReplaceChild(
                "Equip202",
                CubeListBuilder.create().texOffs(0, 10).mirror().addBox(0f, 0f, 0f, 24f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offset(-15f, 0f, 10f)
            )

            val Equip201 = EquipBase2.addOrReplaceChild(
                "Equip201",
                CubeListBuilder.create().texOffs(46, 10).addBox(0f, 0f, 0f, 6f, 6f, 24f, CubeDeformation(0f)),
                PartPose.offset(-15f, 0f, -14f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(0, 22).addBox(-3f, -2f, -3f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10.5f, 0f, 0.0524f, 0f, 0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -1.5f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 75).addBox(-8f, -8f, -6.8f, 16f, 17f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, -0.5f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(88, 100).addBox(-1f, 0f, 0f, 2f, 8f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.5f, 0f, -4f, -0.1745f, 0.1745f, 0.1396f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(88, 100).mirror().addBox(-1f, 0f, 0f, 2f, 8f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 6f, 0f, -0.1745f, 0f, -0.0524f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(88, 100).mirror().addBox(-1f, 0f, 0f, 2f, 8f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.5f, 0f, -4f, -0.1745f, -0.1745f, -0.1396f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(88, 100).addBox(-1f, 0f, 0f, 2f, 8f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, 0f, -0.1745f, 0f, 0.0873f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(104, 29).addBox(0f, -5f, -12f, 0f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -8.5f, -5f, 0f, 0.5236f, 0f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(48, 47).addBox(-7.5f, 0f, 0f, 15f, 9f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -15f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(49, 47).addBox(-7.5f, 0f, 0f, 15f, 18f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1.1f, 0.3491f, 0f, 0f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(24, 81).addBox(-4.5f, -0.5f, -2.5f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, -9f, -0.5f, 0.1571f, 0f, 0.384f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(24, 86).addBox(-2.5f, 0f, -5f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offset(-2f, 10.5f, 2.5f)
            )

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(84, 0).addBox(-7f, 0f, -4.5f, 14f, 10f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.3f, 0f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(24, 81).mirror()
                    .addBox(-0.5f, -0.5f, -2.5f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, -9f, -0.5f, 0.1571f, 0f, -0.384f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(24, 56).mirror()
                    .addBox(-2.5f, 0f, -5f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offset(2f, 10.5f, 2.5f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -13.5f, 0f, -0.1047f, 0f, 0f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -10.5f, 0f, 0.0524f, 0f, 0f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -1.5f, 0f)
            )
            addFaceLayer(GlowHead)

            val FlowerBase = GlowHead.addOrReplaceChild(
                "FlowerBase",
                CubeListBuilder.create().texOffs(0, 7).addBox(0f, 0f, -1.5f, 0f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8.8f, -12f, -4f, -0.6981f, 0.0873f, -0.0873f)
            )

            val Flower1 = FlowerBase.addOrReplaceChild(
                "Flower1",
                CubeListBuilder.create().texOffs(0, 7).addBox(0f, 0f, -1.5f, 0f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 1.309f, -0.0873f, 0f)
            )

            val Flower2 = FlowerBase.addOrReplaceChild(
                "Flower2",
                CubeListBuilder.create().texOffs(0, 7).addBox(0f, 0f, -1.5f, 0f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 2.5307f, 0f, -0.0873f)
            )

            val Flower3 = FlowerBase.addOrReplaceChild(
                "Flower3",
                CubeListBuilder.create().texOffs(0, 7).addBox(0f, 0f, -1.5f, 0f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -2.618f, 0f, -0.0873f)
            )

            val Flower4 = FlowerBase.addOrReplaceChild(
                "Flower4",
                CubeListBuilder.create().texOffs(0, 7).addBox(0f, 0f, -1.5f, 0f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -1.2217f, 0f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
