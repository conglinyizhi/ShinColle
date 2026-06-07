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
import org.trp.shincolle.client.model.LegacyPoseOffsets.sprintY
import org.trp.shincolle.entity.EntitySubmKa
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelSubmKa<T : EntityShipBase>(root: ModelPart) : ShipModelHumanoidBase<T>(), IGlowableModel {
    private var isDeadPose = false
    private var isSittingPose = false
    override var poseTranslateY = 0f

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
    private val EquipHead01: ModelPart
    private val Ahoke: ModelPart
    private val HairU01: ModelPart
    private val HairL01: ModelPart
    private val HairR01: ModelPart
    private val HairL02: ModelPart
    private val HairR02: ModelPart
    private val Hair01: ModelPart
    private val Hair02: ModelPart
    private val Hair03: ModelPart
    private val EquipHead02: ModelPart
    private val EquipHead03: ModelPart
    private val EquipHead04: ModelPart
    private val EquipHead05: ModelPart
    private val ArmLeft02: ModelPart
    private val EquipT01a: ModelPart
    private val EquipT01b: ModelPart
    private val ArmRight02: ModelPart
    private val EquipC01: ModelPart
    private val EquipC02: ModelPart
    private val EquipC02a: ModelPart
    private val EquipC02b: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowHead: ModelPart
    private val GlowArmLeft01: ModelPart
    private val GlowArmLeft02: ModelPart
    private val buttDefaultXRot: Float
    private val hair01DefaultXRot: Float
    private val hair02DefaultXRot: Float
    private val hair03DefaultXRot: Float
    private val hairL01DefaultZRot: Float
    private val hairL02DefaultZRot: Float
    private val hairR01DefaultZRot: Float
    private val hairR02DefaultZRot: Float
    private val equipT01aDefaultX: Float
    private val equipT01aDefaultY: Float
    private val equipT01aDefaultZ: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Head = this.BodyMain.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairU01 = this.Hair.getChild("HairU01")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.EquipHead01 = this.Head.getChild("EquipHead01")
        this.EquipHead03 = this.EquipHead01.getChild("EquipHead03")
        this.EquipHead04 = this.EquipHead03.getChild("EquipHead04")
        this.EquipHead05 = this.EquipHead04.getChild("EquipHead05")
        this.EquipHead02 = this.EquipHead01.getChild("EquipHead02")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Hair02 = this.Hair01.getChild("Hair02")
        this.Hair03 = this.Hair02.getChild("Hair03")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.BodyMain2 = this.BodyMain.getChild("BodyMain2")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.Butt2 = this.BodyMain.getChild("Butt2")
        this.BoobL2 = this.BodyMain.getChild("BoobL2")
        this.BodyMain1 = this.BodyMain.getChild("BodyMain1")
        this.EquipBase = this.BodyMain.getChild("EquipBase")
        this.EquipC01 = this.EquipBase.getChild("EquipC01")
        this.EquipC02 = this.EquipC01.getChild("EquipC02")
        this.EquipC02b = this.EquipC02.getChild("EquipC02b")
        this.EquipC02a = this.EquipC02.getChild("EquipC02a")
        this.Butt1 = this.BodyMain.getChild("Butt1")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.BoobR2 = this.BodyMain.getChild("BoobR2")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowHead = this.GlowBodyMain.getChild("GlowHead")
        this.GlowArmLeft01 = this.GlowBodyMain.getChild("GlowArmLeft01")
        this.GlowArmLeft02 = this.GlowArmLeft01.getChild("GlowArmLeft02")
        this.EquipT01a = this.GlowArmLeft02.getChild("EquipT01a")
        this.EquipT01b = this.EquipT01a.getChild("EquipT01b")
        this.initFaceParts(this.GlowHead)
        this.buttDefaultXRot = this.Butt.xRot
        this.hair01DefaultXRot = this.Hair01.xRot
        this.hair02DefaultXRot = this.Hair02.xRot
        this.hair03DefaultXRot = this.Hair03.xRot
        this.hairL01DefaultZRot = this.HairL01.zRot
        this.hairL02DefaultZRot = this.HairL02.zRot
        this.hairR01DefaultZRot = this.HairR01.zRot
        this.hairR02DefaultZRot = this.HairR02.zRot
        this.equipT01aDefaultX = this.EquipT01a.x
        this.equipT01aDefaultY = this.EquipT01a.y
        this.equipT01aDefaultZ = this.EquipT01a.z
    }

    override fun setupAnim(
        entity: T,
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
            this.applyDeadPose(ageInTicks)
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
        this.Hair01.xRot = this.hair01DefaultXRot
        this.Hair02.xRot = this.hair02DefaultXRot
        this.Hair03.xRot = this.hair03DefaultXRot
        this.HairL01.zRot = this.hairL01DefaultZRot
        this.HairL02.zRot = this.hairL02DefaultZRot
        this.HairR01.zRot = this.hairR01DefaultZRot
        this.HairR02.zRot = this.hairR02DefaultZRot

        this.EquipT01a.x = this.equipT01aDefaultX
        this.EquipT01a.y = this.equipT01aDefaultY
        this.EquipT01a.z = this.equipT01aDefaultZ
    }

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) return
        this.EquipBase.visible = entity.getEquipFlag(EntitySubmKa.EQUIP_BASE)
        this.EquipHead01.visible = entity.getEquipFlag(EntitySubmKa.EQUIP_HEAD_BASE)
        val showNormalBody = entity.getEquipFlag(EntitySubmKa.EQUIP_NORMAL_BODY)
        this.BodyMain1.visible = showNormalBody
        this.Butt1.visible = showNormalBody
        this.BoobL.visible = showNormalBody
        this.BoobR.visible = showNormalBody
        this.BodyMain2.visible = !showNormalBody
        this.Butt2.visible = !showNormalBody
        this.BoobL2.visible = !showNormalBody
        this.BoobR2.visible = !showNormalBody
        this.EquipT01a.visible = entity.getEquipFlag(EntitySubmKa.EQUIP_TORPEDO)
    }

    private fun applyDeadPose(ageInTicks: Float) {
        this.isDeadPose = true
        val angleX = Mth.cos(ageInTicks * 0.08f)
        this.poseTranslateY = DEAD_TRANSLATE_Y + angleX * 0.05f

        this.Head.xRot = 0.5f
        this.Head.yRot = 0.0f
        this.BodyMain.xRot = 1.6f
        this.Hair01.xRot = 0.1f
        this.Hair02.xRot = -0.5f
        this.Hair03.xRot = -0.5f

        this.ArmLeft01.xRot = -1.6f
        this.ArmLeft01.yRot = -0.15f - angleX * 0.05f
        this.ArmRight01.xRot = -1.6f
        this.ArmRight01.yRot = 0.15f + angleX * 0.05f

        this.LegLeft01.xRot = -1.6f
        this.LegRight01.xRot = -1.6f
        this.LegLeft01.yRot = -0.1f - angleX * 0.05f
        this.LegRight01.yRot = 0.1f + angleX * 0.05f
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
        val angleX1 = Mth.cos(ageInTicks * 0.1f + 0.3f + limbSwing * 0.5f)
        val angleX2 = Mth.cos(ageInTicks * 0.1f + 0.6f + limbSwing * 0.5f)
        val angleX3 = Mth.cos(ageInTicks * 0.1f + 0.9f + limbSwing * 0.5f)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount * 0.7f
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount * 0.7f

        if (entity!!.shipDepth > 0.0) {
            this.poseTranslateY = angleX * 0.05f + 0.025f
        }

        this.Head.xRot = headPitch * (Math.PI.toFloat() / 180f) + 0.1047f
        this.Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)
        this.Head.zRot = 0.0f

        this.BoobL.xRot = angleX * 0.08f - 0.76f
        this.BoobR.xRot = angleX * 0.08f - 0.76f
        this.Ahoke.yRot = angleX * 0.15f + 0.6f

        this.BodyMain.xRot = -0.1047f
        this.BodyMain.yRot = 0.0f
        this.BodyMain.zRot = 0.0f
        this.Butt.xRot = 0.21f

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

        this.ArmLeft01.xRot = 0.2094f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = -angleX * 0.05f - 0.3142f
        this.ArmLeft02.zRot = 0.0f

        this.ArmRight01.xRot = 0.0f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = angleX * 0.05f + 0.2094f
        this.ArmRight02.xRot = 0.0f

        val addk1 = angleAdd1 * 0.6f - 0.157f
        val addk2 = angleAdd2 * 0.6f - 0.035f
        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.1f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.1f

        this.LegLeft01.xRot = addk1
        this.LegRight01.xRot = addk2

        this.EquipT01a.xRot = 0.14f
        this.EquipT01a.zRot = 0.0f
        this.EquipT01a.x = this.equipT01aDefaultX
        this.EquipT01a.y = this.equipT01aDefaultY
        this.EquipT01a.z = this.equipT01aDefaultZ

        this.EquipC02.yRot = this.Head.yRot
        this.EquipC02a.xRot = this.Head.xRot

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

    private fun applySpecialPoseAdjustments(entity: T?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        val isPassenger = entity!!.isPassenger
        val isCrouching = entity.isCrouching
        val isSprinting = if (entity != null) entity.isSprinting else limbSwingAmount > 0.92f
        val isSitting = entity.isInSittingPose || (isPassenger && entity.vehicle !is EntityMountBase)
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount * 0.7f

        if (isSprinting) {
            this.poseTranslateY = SPRINT_TRANSLATE_Y
            this.Head.xRot -= 1.1f
            this.BodyMain.xRot = 1.2566f
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
            this.EquipT01a.xRot = 1.2566f
            this.EquipT01a.zRot = -0.1885f
            this.EquipT01a.x = this.equipT01aDefaultX + (-0.08f * OFFSET_SCALE)
        }

        if (isCrouching) {
            this.poseTranslateY = SNEAK_TRANSLATE_Y
            this.Head.xRot -= 1.0472f
            this.BodyMain.xRot = 1.0472f
            this.Butt.xRot = -0.8378f
            this.Hair01.xRot -= 0.1f
            this.Hair02.xRot -= 0.2f
            this.Hair03.xRot -= 0.5f
            this.HairR01.zRot -= 0.5f
            this.HairR02.zRot -= 0.2f
            this.ArmLeft01.xRot = -0.7f
            this.ArmLeft01.zRot = 0.2618f
            this.ArmRight01.xRot = -0.7f
            this.ArmRight01.zRot = -0.2618f

            this.LegLeft01.xRot -= 0.1f
            this.LegRight01.xRot -= 0.1f
        }

        if (isSitting && !isPassenger) {
            val angleX = Mth.cos(ageInTicks * 0.08f)
            if (entity != null && hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY = angleX * 0.05f + 0.18f
                this.Head.xRot -= 0.8f
                this.BodyMain.xRot = 0.7854f
                this.ArmLeft01.xRot = -0.8727f
                this.ArmLeft01.yRot = -0.7854f
                this.ArmLeft01.zRot = -0.3491f
                this.ArmLeft02.zRot = 1.5708f
                this.ArmRight01.xRot = -2.3562f
                this.ArmRight01.yRot = 0.5236f
                this.ArmRight01.zRot = -0.2618f
                this.ArmRight02.xRot = -0.7f
                this.LegLeft01.xRot = 0.45f + angleX * 0.1f
                this.LegRight01.xRot = 0.45f - angleX * 0.1f
                this.EquipT01a.visible = true
                this.EquipT01a.xRot = 0.2618f
            } else {
                this.poseTranslateY = SITTING_TRANSLATE_Y
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
            }
        }

        if (entity != null && entity.attackTick > 41) {
            var ft = (50 - entity.attackTick) + (ageInTicks - ageInTicks.toInt())
            val fa = Mth.cos((0.125f.let { ft *= it; ft }) * ft * Math.PI.toFloat())
            val fb = Mth.cos(Mth.sqrt(ft) * Math.PI.toFloat())
            this.ArmLeft01.xRot += -fb * 80.0f * (Math.PI.toFloat() / 180f) - 1.6f
            this.ArmLeft01.yRot += fa * 20.0f * (Math.PI.toFloat() / 180f)
            this.ArmLeft01.zRot += fb * 20.0f * (Math.PI.toFloat() / 180f) + 0.4f
            this.EquipT01a.x = this.equipT01aDefaultX + (0.2f * OFFSET_SCALE)
            this.EquipT01a.y = this.equipT01aDefaultY + (0.2f * OFFSET_SCALE)
            this.EquipT01a.z = this.equipT01aDefaultZ + (-0.5f * OFFSET_SCALE)
        }
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

    private fun syncGlowParts() {
        this.GlowBodyMain.copyFrom(this.BodyMain)
        this.GlowHead.copyFrom(this.Head)
        this.GlowArmLeft01.copyFrom(this.ArmLeft01)
        this.GlowArmLeft02.copyFrom(this.ArmLeft02)
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "subm_ka"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelSubmKa")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelSubmKa")
        private val SITTING_TRANSLATE_Y = sittingY("ModelSubmKa")
        private val SPRINT_TRANSLATE_Y = sprintY("ModelSubmKa")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 106).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -11f, -3f, -0.1047f, 0f, 0f)
            )

            val Head = BodyMain.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.8f, -0.5f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 81).addBox(-8f, -8f, -7.4f, 16f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0.4f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(24, 91).addBox(-2.5f, 0f, 0f, 5f, 9f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.9f, 8f, -7.2f, -0.1745f, 0.1396f, -0.0524f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(24, 91).addBox(-2.5f, 0f, 0f, 5f, 12f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.3f, 7.5f, 0.1f, 0.3142f, 0.1745f, 0.1745f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(39, 14).addBox(0f, -5f, -10.5f, 0f, 11f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, -7f, -5.5f, 0.2618f, 0.6981f, 0f)
            )

            val HairU01 = Hair.addOrReplaceChild(
                "HairU01",
                CubeListBuilder.create().texOffs(50, 44).mirror()
                    .addBox(-8.5f, 0f, 0f, 17f, 15f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, -6f, -7.7f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(24, 88).mirror().addBox(-1f, 0f, 0f, 2f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.9f, 7.7f, -7.5f, -0.1745f, 0.4363f, 0.1396f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(24, 88).mirror().addBox(-1f, 0f, 0f, 2f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.3f, 10f, 0f, 0.2443f, 0.0873f, -0.1396f)
            )

            val EquipHead01 = Head.addOrReplaceChild(
                "EquipHead01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, -3f, 0f, 6f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1f, -8f, 0.2618f, 0f, 0f)
            )

            val EquipHead03 = EquipHead01.addOrReplaceChild(
                "EquipHead03",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -1f, -1f, 8f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, 0.5f, -1f, 0f, -0.0873f, 0f)
            )

            val EquipHead04 = EquipHead03.addOrReplaceChild(
                "EquipHead04",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, 0f, 2f, 2f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, 0f, -1f, -0.1745f, 0.0873f, 0f)
            )

            val EquipHead05 = EquipHead04.addOrReplaceChild(
                "EquipHead05",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, 0f, -1f, 2f, 9f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.4f, 14f, 0f, 0f, 0.5918f)
            )

            val EquipHead02 = EquipHead01.addOrReplaceChild(
                "EquipHead02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, -2f, 0f, 4f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.5f, -3f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(46, 104).addBox(-7.5f, 0f, 0f, 15f, 11f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.8f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(0, 62).addBox(-7.5f, 0f, 0f, 15f, 16f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8f, 1.1f, 0.2094f, 0f, 0f)
            )

            val Hair02 = Hair01.addOrReplaceChild(
                "Hair02",
                CubeListBuilder.create().texOffs(0, 63).addBox(-8f, 0f, -5f, 16f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 13.5f, 5.5f, -0.0873f, 0f, 0f)
            )

            val Hair03 = Hair02.addOrReplaceChild(
                "Hair03",
                CubeListBuilder.create().texOffs(0, 40).addBox(-8f, 0f, -4.5f, 16f, 15f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12.5f, 0f, -0.1396f, 0f, 0f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(34, 102).addBox(-3f, 0f, 0f, 6f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.2f, -8.6f, -3.9f, -0.6981f, 0.0873f, 0.0698f)
            )

            val BodyMain2 = BodyMain.addOrReplaceChild(
                "BodyMain2",
                CubeListBuilder.create().texOffs(88, 0).addBox(-6.5f, -11f, -4f, 13f, 15f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(2, 88).mirror()
                    .addBox(-2f, -1f, -2.5f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -9.7f, -0.7f, 0.2094f, 0f, -0.3142f)
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

            val BoobL2 = BodyMain.addOrReplaceChild(
                "BoobL2",
                CubeListBuilder.create().texOffs(65, 34).mirror().addBox(-3f, 0f, 0f, 6f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.44f, -8.6f, -3.9f, -0.6981f, -0.0873f, -0.0698f)
            )

            val BodyMain1 = BodyMain.addOrReplaceChild(
                "BodyMain1",
                CubeListBuilder.create().texOffs(0, 106).addBox(-6.5f, -11f, -4f, 13f, 15f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipBase = BodyMain.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipC01 = EquipBase.addOrReplaceChild(
                "EquipC01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6f, 0f, 0f, 12f, 10f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, -9f, 3f)
            )

            val EquipC02 = EquipC01.addOrReplaceChild(
                "EquipC02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, -3.5f, 0f, 7f, 7f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, 3f, -0.0873f, 0f, 0f)
            )

            val EquipC02b = EquipC02.addOrReplaceChild(
                "EquipC02b",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 9f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offset(3f, -2f, 1f)
            )

            val EquipC02a = EquipC02.addOrReplaceChild(
                "EquipC02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -11f, -1f, 2f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -3f, 8f)
            )

            val Butt1 = BodyMain.addOrReplaceChild(
                "Butt1",
                CubeListBuilder.create().texOffs(52, 66).addBox(-7.5f, 0f, -7f, 15f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.5f, 2.8f, 0.2094f, 0f, 0f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(52, 66),
                PartPose.offsetAndRotation(0f, 2.5f, 2.8f, 0.2094f, 0f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 87).mirror().addBox(-3f, 0f, -3f, 6f, 12f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.4f, 6.5f, -4f, -0.1571f, 0f, 0.1047f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 87).mirror().addBox(0f, 0f, 0f, 6f, 13f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3f, 12f, -3f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 87).addBox(-3f, 0f, -3f, 6f, 12f, 6f, CubeDeformation(0f)),
                PartPose.offset(-4.4f, 6.5f, -4f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 87).addBox(-6f, 0f, 0f, 6f, 13f, 6f, CubeDeformation(0f)),
                PartPose.offset(3f, 12f, -3f)
            )

            val BoobR2 = BodyMain.addOrReplaceChild(
                "BoobR2",
                CubeListBuilder.create().texOffs(106, 37).addBox(-3f, 0f, 0f, 6f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.44f, -8.6f, -3.9f, -0.6981f, 0.0873f, 0.0698f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(2, 88).addBox(-3f, -1f, -2.5f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.8f, -9.7f, -0.7f, 0f, 0f, 0.2094f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(2, 88).addBox(0f, 0f, -5f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offset(-3f, 10f, 2.5f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(34, 102).mirror().addBox(-3f, 0f, 0f, 6f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.2f, -8.6f, -3.9f, -0.6981f, -0.0873f, -0.0698f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -11f, -3f, -0.1047f, 0f, 0f)
            )

            val GlowHead = GlowBodyMain.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -11.8f, -0.5f)
            )
            addFaceLayer(GlowHead)

            val GlowArmLeft01 = GlowBodyMain.addOrReplaceChild(
                "GlowArmLeft01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(7.8f, -9.7f, -0.7f, 0.2094f, 0f, -0.3142f)
            )

            val GlowArmLeft02 = GlowArmLeft01.addOrReplaceChild(
                "GlowArmLeft02",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(3f, 10f, 2.5f)
            )

            val EquipT01a = GlowArmLeft02.addOrReplaceChild(
                "EquipT01a",
                CubeListBuilder.create().texOffs(16, 0).addBox(-2f, -3f, -5f, 4f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offset(-6.5f, 6.5f, -1f)
            )

            val EquipT01b = EquipT01a.addOrReplaceChild(
                "EquipT01b",
                CubeListBuilder.create().texOffs(0, 10).addBox(-2.5f, -3.5f, 0f, 5f, 7f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -12.9f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
