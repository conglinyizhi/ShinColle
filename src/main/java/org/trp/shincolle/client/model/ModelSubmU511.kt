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
import org.trp.shincolle.entity.EntitySubmU511
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelSubmU511<T : EntityShipBase>(root: ModelPart) : ShipModelHumanoidBase<T>(), IGlowableModel {
    private var isDeadPose = false
    private var isSittingPose = false
    override var poseTranslateY = 0f

    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val Butt: ModelPart
    private val Cloth01: ModelPart
    private val EquipBase: ModelPart
    private val Head: ModelPart
    private val Pipe: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val Hat01: ModelPart
    private val Ahoke: ModelPart
    private val HairL01: ModelPart
    private val HairR01: ModelPart
    private val HairL02: ModelPart
    private val HairR02: ModelPart
    private val Hair01: ModelPart
    private val Hat02: ModelPart
    private val Ear1: ModelPart
    private val Ear2: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmLeft03: ModelPart
    private val ArmRight02: ModelPart
    private val ArmRight03: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft01: ModelPart
    private val Skirt: ModelPart
    private val LegRight02: ModelPart
    private val LegLeft02: ModelPart
    private val EquipMid: ModelPart
    private val EquipL: ModelPart
    private val EquipR: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val armRight03DefaultX: Float
    private val armRight03DefaultY: Float
    private val armRight03DefaultZRot: Float
    private val legLeft01DefaultXRot: Float
    private val legLeft01DefaultYRot: Float
    private val legLeft01DefaultZRot: Float
    private val legRight01DefaultXRot: Float
    private val legRight01DefaultYRot: Float
    private val legRight01DefaultZRot: Float
    private val hair01DefaultXRot: Float
    private val hairL01DefaultXRot: Float
    private val hairL02DefaultXRot: Float
    private val hairR01DefaultXRot: Float
    private val hairR02DefaultXRot: Float
    private val hairL01DefaultZRot: Float
    private val hairL02DefaultZRot: Float
    private val hairR01DefaultZRot: Float
    private val hairR02DefaultZRot: Float
    private val ear1DefaultZRot: Float
    private val ear2DefaultZRot: Float
    private val pipeDefaultXRot: Float
    private val skirtDefaultXRot: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Cloth01 = this.BodyMain.getChild("Cloth01")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.Hat01 = this.Head.getChild("Hat01")
        this.Hat02 = this.Hat01.getChild("Hat02")
        this.Ear2 = this.Hat02.getChild("Ear2")
        this.Ear1 = this.Hat02.getChild("Ear1")
        this.Hair = this.Head.getChild("Hair")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairMain = this.Head.getChild("HairMain")
        this.Hair01 = this.HairMain.getChild("Hair01")
        this.Pipe = this.Neck.getChild("Pipe")
        this.Butt = this.BodyMain.getChild("Butt")
        this.Skirt = this.Butt.getChild("Skirt")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LegLeft02 = this.LegLeft01.getChild("LegLeft02")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.ArmRight03 = this.ArmRight02.getChild("ArmRight03")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.ArmLeft03 = this.ArmLeft02.getChild("ArmLeft03")
        this.EquipBase = this.BodyMain.getChild("EquipBase")
        this.EquipMid = this.EquipBase.getChild("EquipMid")
        this.EquipL = this.EquipMid.getChild("EquipL")
        this.EquipR = this.EquipMid.getChild("EquipR")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        this.initFaceParts(this.GlowHead)
        this.armRight03DefaultX = this.ArmRight03.x
        this.armRight03DefaultY = this.ArmRight03.y
        this.armRight03DefaultZRot = this.ArmRight03.zRot
        this.legLeft01DefaultXRot = this.LegLeft01.xRot
        this.legLeft01DefaultYRot = this.LegLeft01.yRot
        this.legLeft01DefaultZRot = this.LegLeft01.zRot
        this.legRight01DefaultXRot = this.LegRight01.xRot
        this.legRight01DefaultYRot = this.LegRight01.yRot
        this.legRight01DefaultZRot = this.LegRight01.zRot
        this.hair01DefaultXRot = this.Hair01.xRot
        this.hairL01DefaultXRot = this.HairL01.xRot
        this.hairL02DefaultXRot = this.HairL02.xRot
        this.hairR01DefaultXRot = this.HairR01.xRot
        this.hairR02DefaultXRot = this.HairR02.xRot
        this.hairL01DefaultZRot = this.HairL01.zRot
        this.hairL02DefaultZRot = this.HairL02.zRot
        this.hairR01DefaultZRot = this.HairR01.zRot
        this.hairR02DefaultZRot = this.HairR02.zRot
        this.ear1DefaultZRot = this.Ear1.zRot
        this.ear2DefaultZRot = this.Ear2.zRot
        this.pipeDefaultXRot = this.Pipe.xRot
        this.skirtDefaultXRot = this.Skirt.xRot
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

        this.ArmRight03.x = this.armRight03DefaultX
        this.ArmRight03.y = this.armRight03DefaultY
        this.ArmRight03.zRot = this.armRight03DefaultZRot
        this.LegLeft01.xRot = this.legLeft01DefaultXRot
        this.LegLeft01.yRot = this.legLeft01DefaultYRot
        this.LegLeft01.zRot = this.legLeft01DefaultZRot
        this.LegRight01.xRot = this.legRight01DefaultXRot
        this.LegRight01.yRot = this.legRight01DefaultYRot
        this.LegRight01.zRot = this.legRight01DefaultZRot
        this.Hair01.xRot = this.hair01DefaultXRot
        this.HairL01.xRot = this.hairL01DefaultXRot
        this.HairL02.xRot = this.hairL02DefaultXRot
        this.HairR01.xRot = this.hairR01DefaultXRot
        this.HairR02.xRot = this.hairR02DefaultXRot
        this.HairL01.zRot = this.hairL01DefaultZRot
        this.HairL02.zRot = this.hairL02DefaultZRot
        this.HairR01.zRot = this.hairR01DefaultZRot
        this.HairR02.zRot = this.hairR02DefaultZRot
        this.Ear1.zRot = this.ear1DefaultZRot
        this.Ear2.zRot = this.ear2DefaultZRot
        this.Pipe.xRot = this.pipeDefaultXRot
        this.Skirt.xRot = this.skirtDefaultXRot
    }

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) return
        this.EquipBase.visible = entity.getEquipFlag(EntitySubmU511.EQUIP_BASE)
        this.Hat01.visible = entity.getEquipFlag(EntitySubmU511.EQUIP_HAT)
        this.Pipe.visible = entity.getEquipFlag(EntitySubmU511.EQUIP_PIPE)
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.035f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.035f
        this.LegLeft01.xRot = -2.8f
        this.LegLeft02.xRot = 1.4f
        this.LegRight01.xRot = -2.8f
        this.LegRight02.xRot = 1.4f

        this.Pipe.xRot = -0.0873f
        this.Ahoke.yRot = 0.5236f
        this.Head.xRot = 0.2618f
        this.Head.yRot = 0.0f
        this.BodyMain.xRot = 0.35f

        this.ArmLeft01.xRot = -0.7f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = -0.12f
        this.ArmRight01.xRot = -0.96f
        this.ArmRight01.yRot = -0.35f
        this.ArmRight01.zRot = 0.12f
        this.ArmRight03.zRot = -1.57f
        this.ArmRight03.x = this.armRight03DefaultX + (-0.153f * OFFSET_SCALE)
        this.ArmRight03.y = this.armRight03DefaultY + (0.1f * OFFSET_SCALE)

        this.Hair01.xRot = 0.05f
        this.Ear1.zRot = -0.2618f
        this.Ear2.zRot = 0.2618f
        this.Skirt.xRot = 2.618f
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
        val angleAdd1 = Mth.cos(limbSwing * 0.7f) * limbSwingAmount * 0.5f
        val angleAdd2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount * 0.5f

        if (entity!!.shipDepth > 0.0) {
            this.poseTranslateY = angleX * 0.05f + 0.025f
        }

        val addk1 = angleAdd1 - 0.2118f
        val addk2 = angleAdd2 - 0.1118f
        this.Head.xRot = headPitch * (Math.PI.toFloat() / 180f) * 0.8f + 0.1f
        this.Head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f) * 0.5f
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

        this.Ear1.zRot = angleX * 0.1f - 0.2618f
        this.Ear2.zRot = angleX * 0.1f + 0.2618f

        this.ArmLeft01.xRot = angleAdd2 * 0.5f + 0.15f
        this.ArmLeft01.yRot = 0.0f
        this.ArmLeft01.zRot = -angleX * 0.06f - 0.16f
        this.ArmRight01.xRot = angleAdd1 * 0.5f
        this.ArmRight01.yRot = 0.0f
        this.ArmRight01.zRot = angleX * 0.06f + 0.16f
        this.ArmRight03.zRot = 0.0f
        this.ArmRight03.x = this.armRight03DefaultX
        this.ArmRight03.y = this.armRight03DefaultY

        this.LegLeft01.yRot = 0.0f
        this.LegLeft01.zRot = 0.035f
        this.LegRight01.yRot = 0.0f
        this.LegRight01.zRot = -0.035f
        this.LegLeft02.xRot = 0.0f
        this.LegRight02.xRot = 0.0f
        this.Pipe.xRot = -0.0873f
        this.Skirt.xRot = 0.35f

        this.LegLeft01.xRot = addk1
        this.LegRight01.xRot = addk2

        val headX = this.Head.xRot * -0.5f
        val headZ = this.Head.zRot * -0.5f
        this.Hair01.xRot += headX
        this.Hair01.zRot += headZ
        this.HairL01.zRot += headZ
        this.HairL02.zRot += headZ
        this.HairR01.zRot += headZ
        this.HairR02.zRot += headZ
        this.HairL01.xRot += headX
        this.HairL02.xRot += headX
        this.HairR01.xRot += headX
        this.HairR02.xRot += headX
    }

    private fun applySpecialPoseAdjustments(entity: T?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        val angleX = Mth.cos(ageInTicks * 0.08f)
        val isPassenger = entity!!.isPassenger()
        val isCrouching = entity.isCrouching()
        val isSprinting = if (entity != null) entity.isSprinting else limbSwingAmount > 0.9f
        val isSitting = entity.isInSittingPose || (isPassenger && entity.getVehicle() !is EntityMountBase)

        if (isSprinting) {
        }

        if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            this.Head.xRot -= 0.8727f
            this.BodyMain.xRot = 1.0472f
            this.Hair01.xRot += 0.2236f
            this.LegLeft01.xRot -= 1.2f
            this.LegRight01.xRot -= 1.2f
            this.Pipe.xRot = -0.7854f
            this.Skirt.xRot = 0.8727f
        }

        if (isSitting) {
            if (entity != null && hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY += 0.41f * 3.2f
                this.Head.xRot += 0.2618f
                this.BodyMain.xRot = 0.35f
                this.HairL01.xRot -= 0.2f
                this.HairR01.xRot -= 0.2f
                this.HairL02.xRot -= 0.2f
                this.HairR02.xRot -= 0.2f
                this.ArmLeft01.xRot = -angleX * 0.2f - 0.7f
                this.ArmRight01.xRot = -0.96f
                this.ArmRight01.yRot = -0.35f
                this.ArmRight03.zRot = -1.57f
                this.ArmRight03.x = this.armRight03DefaultX + (-0.153f * OFFSET_SCALE)
                this.ArmRight03.y = this.armRight03DefaultY + (0.1f * OFFSET_SCALE)
                this.Hair01.xRot -= 0.25f
                this.LegLeft01.xRot = -2.8f
                this.LegRight01.xRot = -2.8f
                this.LegLeft02.xRot = 1.4f
                this.LegRight02.xRot = 1.4f
                this.Skirt.xRot = 2.618f
            } else {
                this.poseTranslateY += SITTING_TRANSLATE_Y
                this.Head.xRot -= 0.7f
                this.BodyMain.xRot = 0.5236f
                this.HairL01.xRot -= 0.3f
                this.HairR01.xRot -= 0.3f
                this.HairL02.xRot -= 0.3f
                this.HairR02.xRot -= 0.3f
                this.ArmLeft01.xRot = -0.5236f
                this.ArmLeft01.zRot = 0.3146f
                this.ArmRight01.xRot = -0.5236f
                this.ArmRight01.zRot = -0.3146f
                this.LegLeft01.xRot = -2.2689f
                this.LegRight01.xRot = -2.2689f
                this.LegLeft01.yRot = -0.3491f
                this.LegRight01.yRot = 0.3491f
                this.Pipe.xRot = -0.7854f
                this.Skirt.xRot = 0.8727f
            }
        }

        if (entity != null && entity.attackTick > 43) {
            var ft = (50 - entity.attackTick) + (ageInTicks - ageInTicks.toInt())
            val fa = Mth.cos((0.08f.let { ft *= it; ft }) * ft * Math.PI.toFloat())
            val fb = Mth.cos(Mth.sqrt(ft) * Math.PI.toFloat())
            this.ArmLeft01.xRot += -fb * 80.0f * (Math.PI.toFloat() / 180f) - 0.9f
            this.ArmLeft01.yRot += fa * 20.0f * (Math.PI.toFloat() / 180f) - 0.3f
            this.ArmLeft01.zRot += fb * 10.0f * (Math.PI.toFloat() / 180f)
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

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        @JvmField
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "subm_u_511"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = LegacyPoseOffsets.deadY("ModelSubmU511")
        private val SITTING_TRANSLATE_Y = LegacyPoseOffsets.sittingY("ModelSubmU511")
        private val SNEAK_TRANSLATE_Y = LegacyPoseOffsets.sneakY("ModelSubmU511")

        @JvmStatic
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 104).addBox(-6.5f, -11f, -4f, 13f, 21f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, -13f, 0f)
            )

            val Cloth01 = BodyMain.addOrReplaceChild(
                "Cloth01",
                CubeListBuilder.create().texOffs(84, 0).addBox(-7f, 0f, -4.5f, 14f, 11f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -11.5f, 0f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -2f, -6f, 9f, 4f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10.5f, 0f, 0.0524f, 0f, 0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -1.5f, 0f)
            )

            val Hat01 = Head.addOrReplaceChild(
                "Hat01",
                CubeListBuilder.create().texOffs(30, 24).addBox(-3f, -6f, 0.5f, 6f, 6f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, -6f, -0.1396f, 0f, 0f)
            )

            val Hat02 = Hat01.addOrReplaceChild(
                "Hat02",
                CubeListBuilder.create().texOffs(4, 17).addBox(-8f, 0f, 0.5f, 16f, 1f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, 8.4f, 0.3142f, 0f, 0f)
            )

            val Ear2 = Hat02.addOrReplaceChild(
                "Ear2",
                CubeListBuilder.create().texOffs(4, 18).addBox(0f, 0f, -4f, 0f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8f, -1f, 0f, 0f, 0f, 0.2618f)
            )

            val Ear1 = Hat02.addOrReplaceChild(
                "Ear1",
                CubeListBuilder.create().texOffs(4, 18).mirror().addBox(0f, 0f, -4f, 0f, 8f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, -1f, 0f, 0f, 0f, -0.2618f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 75).addBox(-8f, -8f, -6.8f, 16f, 17f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, -0.5f)
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

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(104, 29).addBox(0f, -4f, -11f, 0f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -8f, -5f, 0f, 0.5236f, 0f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(48, 47).addBox(-7.5f, 0f, 0f, 15f, 9f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -15f, -3f)
            )

            val Hair01 = HairMain.addOrReplaceChild(
                "Hair01",
                CubeListBuilder.create().texOffs(49, 47).addBox(-7.5f, 0f, 0f, 15f, 18f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1.1f, 0.2618f, 0f, 0f)
            )

            val Pipe = Neck.addOrReplaceChild(
                "Pipe",
                CubeListBuilder.create().texOffs(0, 17).addBox(0f, -26f, 0f, 1f, 25f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, -1f, -3.5f, -0.0873f, 0f, 0.0873f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(80, 19).addBox(-8f, 5f, -5f, 16f, 9f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0.2618f, 0f, 0f)
            )

            val Skirt = Butt.addOrReplaceChild(
                "Skirt",
                CubeListBuilder.create().texOffs(80, 19).addBox(-8f, 0f, -4.5f, 16f, 9f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, 5f, -2f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 85).addBox(-3f, 0f, -3f, 6f, 13f, 6f, CubeDeformation(0f)),
                PartPose.offset(-3.8f, 9.5f, -2.7f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 67).addBox(-3f, 0f, 0f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 13f, -3f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 85).addBox(-3f, 0f, -3f, 6f, 13f, 6f, CubeDeformation(0f)),
                PartPose.offset(3.8f, 9.5f, -2.7f)
            )

            val LegLeft02 = LegLeft01.addOrReplaceChild(
                "LegLeft02",
                CubeListBuilder.create().texOffs(0, 67).addBox(-3f, 0f, 0f, 6f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 13f, -3f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(24, 67).addBox(-4.5f, -1f, -3.5f, 7f, 8f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.2f, -9f, -0.7f, 0f, 0f, 0.1047f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(24, 95).addBox(-2.5f, 0f, -3f, 5f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offset(-0.8f, 7f, 0.5f)
            )

            val ArmRight03 = ArmRight02.addOrReplaceChild(
                "ArmRight03",
                CubeListBuilder.create().texOffs(28, 78).addBox(-2.5f, 0f, -4f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 3f, 1f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(24, 67).mirror()
                    .addBox(-2.5f, -1f, -3.5f, 7f, 8f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.2f, -9f, -0.7f, 0f, 0f, -0.1047f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(24, 95).mirror()
                    .addBox(-2.5f, 0f, -3f, 5f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offset(0.8f, 7f, 0.5f)
            )

            val ArmLeft03 = ArmLeft02.addOrReplaceChild(
                "ArmLeft03",
                CubeListBuilder.create().texOffs(28, 78).mirror()
                    .addBox(-2.5f, 0f, -4f, 5f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 3f, 1f)
            )

            val EquipBase = BodyMain.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(60, 0).addBox(-3f, 0f, 1f, 6f, 16f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8f, 3f, 0.4363f, 0f, 0f)
            )

            val EquipMid = EquipBase.addOrReplaceChild(
                "EquipMid",
                CubeListBuilder.create().texOffs(0, 0).addBox(-13f, 0f, 0f, 26f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -5f, 2f, 0.1396f, 0f, 0f)
            )

            val EquipL = EquipMid.addOrReplaceChild(
                "EquipL",
                CubeListBuilder.create().texOffs(0, 23).mirror()
                    .addBox(0f, 0f, -20f, 5f, 13f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(11.5f, 0f, 4f, -0.3142f, -0.1745f, 0f)
            )

            val EquipR = EquipMid.addOrReplaceChild(
                "EquipR",
                CubeListBuilder.create().texOffs(0, 23).addBox(-5f, 0f, -20f, 5f, 13f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-11.5f, 0f, 4f, -0.3142f, 0.1745f, 0f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -13f, 0f)
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

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
