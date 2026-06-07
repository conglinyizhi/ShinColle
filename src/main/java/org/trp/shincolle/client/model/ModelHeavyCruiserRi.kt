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
import org.trp.shincolle.entity.EntityHeavyCruiserRi
import org.trp.shincolle.entity.base.EntityShipBase

class ModelHeavyCruiserRi<T : EntityShipBase>(root: ModelPart) : ShipModelHumanoidBase<T>(), IGlowableModel {
    override var poseTranslateY = 0f

    private val BodyMain: ModelPart
    private val Butt: ModelPart
    private val ArmLeft: ModelPart
    private val ArmRight: ModelPart
    private val Neck: ModelPart
    private val EquipBase: ModelPart
    private val LegRight: ModelPart
    private val LegLeft: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val EquipLeftBase: ModelPart
    private val EquipLeftTube1: ModelPart
    private val EquipLeftBase2: ModelPart
    private val EquipLeftBase3: ModelPart
    private val EquipLeftBase4: ModelPart
    private val EquipLeftTube2: ModelPart
    private val EquipLeftTube3: ModelPart
    private val EquipLeftTooth: ModelPart
    private val EquipRightBase: ModelPart
    private val EquipRightTube1: ModelPart
    private val EquipRightBase1: ModelPart
    private val EquipRightBase2: ModelPart
    private val EquipRightBase3: ModelPart
    private val EquipRightBase4: ModelPart
    private val EquipRightTube2: ModelPart
    private val EquipRightTube3: ModelPart
    private val EquipRightTooth1: ModelPart
    private val EquipRightTooth2: ModelPart
    private val Head: ModelPart
    private val Cloak: ModelPart
    private val Hair: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val GlowArmLeft: ModelPart
    private val GlowEquipLeftBase: ModelPart
    private val GlowEquipLeftBase3: ModelPart
    private val GlowArmRight: ModelPart
    private val GlowEquipRightBase: ModelPart
    private val GlowEquipRightBase2: ModelPart
    private val GlowEquipRightBase3: ModelPart
    private val ShoesRight: ModelPart
    private val ShoesLeft: ModelPart
    private val HeadTail0: ModelPart
    private val HeadTail1: ModelPart
    private val HeadTail2: ModelPart

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.ArmRight = this.BodyMain.getChild("ArmRight")
        this.EquipRightBase = this.ArmRight.getChild("EquipRightBase")
        this.EquipRightBase1 = this.EquipRightBase.getChild("EquipRightBase1")
        this.EquipRightBase4 = this.EquipRightBase.getChild("EquipRightBase4")
        this.EquipRightBase3 = this.EquipRightBase.getChild("EquipRightBase3")
        this.EquipRightBase2 = this.EquipRightBase.getChild("EquipRightBase2")
        this.EquipRightTube1 = this.EquipRightBase.getChild("EquipRightTube1")
        this.EquipRightTube2 = this.EquipRightTube1.getChild("EquipRightTube2")
        this.EquipRightTube3 = this.EquipRightTube2.getChild("EquipRightTube3")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.Hair = this.Head.getChild("Hair")
        this.HeadTail0 = this.Head.getChild("HeadTail0")
        this.HeadTail1 = this.HeadTail0.getChild("HeadTail1")
        this.HeadTail2 = this.HeadTail1.getChild("HeadTail2")
        this.Cloak = this.Neck.getChild("Cloak")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegRight = this.Butt.getChild("LegRight")
        this.ShoesRight = this.LegRight.getChild("ShoesRight")
        this.LegLeft = this.Butt.getChild("LegLeft")
        this.ShoesLeft = this.LegLeft.getChild("ShoesLeft")
        this.EquipBase = this.BodyMain.getChild("EquipBase")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.ArmLeft = this.BodyMain.getChild("ArmLeft")
        this.EquipLeftBase = this.ArmLeft.getChild("EquipLeftBase")
        this.EquipLeftBase2 = this.EquipLeftBase.getChild("EquipLeftBase2")
        this.EquipLeftBase4 = this.EquipLeftBase.getChild("EquipLeftBase4")
        this.EquipLeftBase3 = this.EquipLeftBase.getChild("EquipLeftBase3")
        this.EquipLeftTube1 = this.EquipLeftBase.getChild("EquipLeftTube1")
        this.EquipLeftTube2 = this.EquipLeftTube1.getChild("EquipLeftTube2")
        this.EquipLeftTube3 = this.EquipLeftTube2.getChild("EquipLeftTube3")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        this.GlowArmLeft = this.GlowBodyMain.getChild("GlowArmLeft")
        this.GlowEquipLeftBase = this.GlowArmLeft.getChild("GlowEquipLeftBase")
        this.GlowEquipLeftBase3 = this.GlowEquipLeftBase.getChild("GlowEquipLeftBase3")
        this.EquipLeftTooth = this.GlowEquipLeftBase3.getChild("EquipLeftTooth")
        this.GlowArmRight = this.GlowBodyMain.getChild("GlowArmRight")
        this.GlowEquipRightBase = this.GlowArmRight.getChild("GlowEquipRightBase")
        this.GlowEquipRightBase2 = this.GlowEquipRightBase.getChild("GlowEquipRightBase2")
        this.EquipRightTooth1 = this.GlowEquipRightBase2.getChild("EquipRightTooth1")
        this.GlowEquipRightBase3 = this.GlowEquipRightBase.getChild("GlowEquipRightBase3")
        this.EquipRightTooth2 = this.GlowEquipRightBase3.getChild("EquipRightTooth2")
        this.initFaceParts(this.GlowHead)
    }

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        this.poseTranslateY = 0.0f
        if (entity !is EntityShipBase) {
            return
        }

        applyEquipVisibility(entity)

        if (entity.isInDeadPose) {
            this.applyDeadPose()
            this.GlowBodyMain.copyFrom(this.BodyMain)
            this.GlowNeck.copyFrom(this.Neck)
            this.GlowHead.copyFrom(this.Head)
            this.GlowArmLeft.copyFrom(this.ArmLeft)
            this.GlowArmRight.copyFrom(this.ArmRight)
            return
        }
        val angleZ = Mth.cos(ageInTicks * 0.08f)
        if (entity.shipDepth > 0.0f) {
            this.poseTranslateY += angleZ * 0.05f + 0.025f
        }

        var addk1 = Mth.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount - 0.087f
        var addk2 = Mth.cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.4f * limbSwingAmount - 0.2f

        this.Head.yRot = netHeadYaw * 0.01f
        this.Head.xRot = headPitch * 0.008f
        this.Cloak.xRot = angleZ * 0.2f + 1.0f
        this.BoobL.xRot = -angleZ * 0.06f - 0.73f
        this.BoobR.xRot = -angleZ * 0.06f - 0.73f
        this.BodyMain.xRot = -0.15f
        this.ArmLeft.zRot = angleZ * -0.06f - 0.25f
        this.ArmLeft.xRot = 0.2f
        this.ArmRight.xRot = 0.2f
        this.ArmRight.yRot = 0.0f
        this.ArmRight.zRot = angleZ * 0.06f + 0.25f
        this.LegLeft.zRot = 0.087f
        this.LegRight.zRot = -0.087f
        this.LegLeft.yRot = 0.0f
        this.LegRight.yRot = 0.0f
        this.HeadTail0.xRot = angleZ * 0.05f + 0.26f
        this.HeadTail1.xRot = angleZ * 0.1f + 0.09f

        if (entity.isSprinting || limbSwingAmount > 0.9f) {
            this.ArmLeft.xRot = 1.0f
            this.ArmRight.xRot = 1.0f
            this.BodyMain.xRot = 0.5f
            this.HeadTail0.xRot = angleZ * 0.05f + 0.8f
            addk1 -= 0.4f
            addk2 -= 0.4f
        }

        this.Head.zRot = entity.getHeadTiltAngle(ageInTicks)
        if (entity.isCrouching) {
            this.poseTranslateY += 0.05f
            this.ArmLeft.xRot = 0.7f
            this.ArmRight.xRot = 0.7f
            this.BodyMain.xRot = 0.5f
            addk1 -= 0.6f
            addk2 -= 0.6f
        }

        val isSitting = entity.isInSittingPose || entity.isPassenger
        if (isSitting) {
            if (entity.getStateEmotion(1) == 4) {
                this.poseTranslateY += 0.44f * 3
                this.ArmLeft.xRot = 0.6f
                this.ArmRight.xRot = 0.6f
                this.ArmLeft.zRot = -0.6f
                this.ArmRight.zRot = 0.6f
                this.BodyMain.xRot = -0.6f
                this.Head.xRot -= 0.2f
                addk1 = -1.58f
                addk2 = -1.58f
                this.LegLeft.zRot = 1.2f
                this.LegRight.zRot = -1.2f
                this.LegLeft.yRot = -0.75f
                this.LegRight.yRot = 0.75f
                this.HeadTail0.xRot += 0.7f
            } else {
                this.poseTranslateY += 0.45f * 3
                this.ArmLeft.xRot = -0.6f
                this.ArmLeft.zRot = 0.3f
                this.ArmRight.xRot = -0.6f
                this.ArmRight.zRot = -0.3f
                this.BodyMain.xRot = 0.3f
                this.Head.xRot -= 0.35f
                addk1 = -2.0f
                addk2 = -2.0f
                this.LegLeft.yRot = 0.15f
                this.LegRight.yRot = -0.15f
                this.LegLeft.zRot = 1.2f
                this.LegRight.zRot = -1.2f
            }
        }

        this.LegLeft.xRot = addk1
        this.LegRight.xRot = addk2

        if (entity.attackTick > 15) {
            this.ArmLeft.xRot = headPitch / 57.29578f - 1.5f
            this.ArmRight.zRot = 0.7f
            this.ArmRight.xRot = 0.4f
        }

        val partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
        val customAttackAnim = if (entity != null) entity.getCustomAttackAnim(partialTick) else 0.0f
        if (customAttackAnim > 0.0f) {
            val f7 = Mth.sin(customAttackAnim * customAttackAnim * Math.PI.toFloat())
            val f8 = Mth.sin(Mth.sqrt(customAttackAnim) * Math.PI.toFloat())
            this.ArmRight.xRot = -0.5f
            this.ArmRight.yRot = 0.0f
            this.ArmRight.zRot = 0.2f
            this.ArmRight.xRot += -f8 * 80.0f * (Math.PI.toFloat() / 180f)
            this.ArmRight.yRot += -f7 * 20.0f * (Math.PI.toFloat() / 180f) + 0.2f
            this.ArmRight.zRot += -f8 * 20.0f * (Math.PI.toFloat() / 180f)
        }

        applyFaceAndMouth(entity)

        this.GlowBodyMain.copyFrom(this.BodyMain)
        this.GlowNeck.copyFrom(this.Neck)
        this.GlowHead.copyFrom(this.Head)
        this.GlowArmLeft.copyFrom(this.ArmLeft)
        this.GlowArmRight.copyFrom(this.ArmRight)
    }

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) {
            return
        }

        val showLeft = entity.getEquipFlag(EntityHeavyCruiserRi.EQUIP_LEFT)
        val showRight = entity.getEquipFlag(EntityHeavyCruiserRi.EQUIP_RIGHT)
        val showCloak = entity.getEquipFlag(EntityHeavyCruiserRi.EQUIP_CLOAK)
        val showHair = entity.getEquipFlag(EntityHeavyCruiserRi.EQUIP_HAIR)

        this.EquipBase.visible = showLeft || showRight
        this.EquipLeftBase.visible = showLeft
        this.GlowEquipLeftBase.visible = showLeft
        this.EquipRightBase.visible = showRight
        this.GlowEquipRightBase.visible = showRight
        this.Cloak.visible = showCloak
        this.HeadTail0.visible = showHair
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

        GlowBodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    private fun applyDeadPose() {
        this.poseTranslateY += 0.46f * 3
        this.Head.xRot = 0.2f
        this.Head.yRot = 0.0f
        this.Head.zRot = 0.0f
        this.Cloak.xRot = -0.2f
        this.BoobL.xRot = -0.73f
        this.BoobR.xRot = -0.73f
        this.BodyMain.xRot = 0.3f
        this.HeadTail0.xRot = -0.05f
        this.HeadTail1.xRot = -0.05f
        this.ArmLeft.xRot = -0.6f
        this.ArmRight.xRot = -0.6f
        this.ArmLeft.zRot = 0.5f
        this.ArmRight.zRot = -0.5f
        this.LegLeft.xRot = -2.0f
        this.LegLeft.yRot = 0.15f
        this.LegLeft.zRot = 1.2f
        this.LegRight.xRot = -2.0f
        this.LegRight.yRot = -0.15f
        this.LegRight.zRot = -1.2f
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "heavy_cruiser_ri"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.5f, -10f, -4f, 13f, 16f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -14f, 0f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(1, 26).mirror()
                    .addBox(-3.5f, 0f, -1f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.3f, -8.5f, -2.5f, -0.7854f, 0.087f, 0.087f)
            )

            val ArmRight = BodyMain.addOrReplaceChild(
                "ArmRight",
                CubeListBuilder.create().texOffs(0, 53).addBox(-5f, 0f, -2.5f, 5f, 25f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, -9.5f, 0f, 0.2f, 0f, 0.2618f)
            )

            val EquipRightBase = ArmRight.addOrReplaceChild(
                "EquipRightBase",
                CubeListBuilder.create().texOffs(78, 6).addBox(-7.5f, 0f, -4.5f, 13f, 14f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, 16f, 0f, 0f, 0f, -0.0873f)
            )

            val EquipRightBase1 = EquipRightBase.addOrReplaceChild(
                "EquipRightBase1",
                CubeListBuilder.create().texOffs(85, 4).addBox(0f, -20f, 0f, 4f, 21f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5f, 0f, -5.5f, 0f, 0f, -0.0873f)
            )

            val EquipRightBase4 = EquipRightBase.addOrReplaceChild(
                "EquipRightBase4",
                CubeListBuilder.create().texOffs(81, 0).addBox(0f, 0f, 0f, 4f, 25f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5f, 0f, -7.5f, 0f, 0f, -0.0873f)
            )

            val EquipRightBase3 = EquipRightBase.addOrReplaceChild(
                "EquipRightBase3",
                CubeListBuilder.create().texOffs(90, 8).addBox(0f, 0f, -3.5f, 3f, 8f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1f, 14f, 0f, 0f, 0f, -0.2618f)
            )

            val EquipRightBase2 = EquipRightBase.addOrReplaceChild(
                "EquipRightBase2",
                CubeListBuilder.create().texOffs(85, 5).addBox(-5f, 0f, -5f, 5f, 10f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.2f, 13f, 0f, 0f, 0f, 0.0873f)
            )

            val EquipRightTube1 = EquipRightBase.addOrReplaceChild(
                "EquipRightTube1",
                CubeListBuilder.create().texOffs(82, 56).addBox(-1.5f, -16f, -1.5f, 3f, 16f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1f, 8f, 3f, -1.0472f, 0f, 0f)
            )

            val EquipRightTube2 = EquipRightTube1.addOrReplaceChild(
                "EquipRightTube2",
                CubeListBuilder.create().texOffs(82, 56).addBox(-1.5f, -13f, -1.5f, 3f, 14f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, 0f, 0.7854f, -0.1745f, 0f)
            )

            val EquipRightTube3 = EquipRightTube2.addOrReplaceChild(
                "EquipRightTube3",
                CubeListBuilder.create().texOffs(82, 56).addBox(-3.5f, -23.5f, -1.4f, 3f, 25f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -12f, 0f, 1.3963f, -0.3491f, 0f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(78, 5).addBox(-5.5f, 0f, -5.6f, 11f, 3f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -13f, 1f, 0.1f, 0f, 0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(43, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.5f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(34, 68).addBox(-8f, -8f, -7.2f, 16f, 16f, 16f, CubeDeformation(0f)),
                PartPose.offset(0f, -7f, 0f)
            )

            val HeadTail0 = Head.addOrReplaceChild(
                "HeadTail0",
                CubeListBuilder.create().texOffs(20, 54).addBox(-4.5f, 0f, -3f, 9f, 14f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, 8f, 0.2618f, 0f, 0f)
            )

            val HeadTail1 = HeadTail0.addOrReplaceChild(
                "HeadTail1",
                CubeListBuilder.create().texOffs(24, 54).addBox(-3.5f, 0f, -3f, 7f, 16f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12f, 0f, 0.09f, 0f, 0f)
            )

            val HeadTail2 = HeadTail1.addOrReplaceChild(
                "HeadTail2",
                CubeListBuilder.create().texOffs(21, 55).addBox(-4f, 0f, -2.5f, 8f, 18f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 14f, 0f, -0.1745f, 0f, 0f)
            )

            val Cloak = Neck.addOrReplaceChild(
                "Cloak",
                CubeListBuilder.create().texOffs(0, 112).addBox(-8f, 0f, 0f, 16f, 16f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1f, 4f, 1.309f, 0f, 0f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 36).addBox(-8f, 0f, -4.1f, 16f, 8f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, 0f, 0.2618f, 0f, 0f)
            )

            val LegRight = Butt.addOrReplaceChild(
                "LegRight",
                CubeListBuilder.create().texOffs(1, 85).addBox(-3f, 0f, -3f, 6f, 17f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.7f, 7.5f, -1f, -0.2f, 0f, -0.087f)
            )

            val ShoesRight = LegRight.addOrReplaceChild(
                "ShoesRight",
                CubeListBuilder.create().texOffs(52, 52).addBox(-3.5f, 17f, -3.5f, 7f, 9f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val LegLeft = Butt.addOrReplaceChild(
                "LegLeft",
                CubeListBuilder.create().texOffs(1, 85).addBox(-3f, 0f, -3f, 6f, 17f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.7f, 7.5f, -1f, -0.087f, 0f, 0.087f)
            )

            val ShoesLeft = LegLeft.addOrReplaceChild(
                "ShoesLeft",
                CubeListBuilder.create().texOffs(52, 52).mirror()
                    .addBox(-3.5f, 17f, -3.5f, 7f, 9f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipBase = BodyMain.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(82, 12).addBox(-5f, 0f, 0f, 10f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, -11f, 4f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(1, 26).addBox(-3.5f, 0f, -1f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.3f, -8.5f, -2.5f, -0.7854f, -0.087f, -0.087f)
            )

            val ArmLeft = BodyMain.addOrReplaceChild(
                "ArmLeft",
                CubeListBuilder.create().texOffs(0, 53).mirror()
                    .addBox(0f, 0f, -2.5f, 5f, 25f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, -9.5f, 0f, -0.087f, 0f, -0.2618f)
            )

            val EquipLeftBase = ArmLeft.addOrReplaceChild(
                "EquipLeftBase",
                CubeListBuilder.create().texOffs(76, 1).addBox(-6f, 0f, -7f, 10f, 14f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, 16f, 0f, 0f, 0f, 0.0873f)
            )

            val EquipLeftBase2 = EquipLeftBase.addOrReplaceChild(
                "EquipLeftBase2",
                CubeListBuilder.create().texOffs(82, 5).addBox(-3f, -7f, -5f, 8f, 7f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, 0f, 0f, 0f, 0f, 0.0255f)
            )

            val EquipLeftBase4 = EquipLeftBase.addOrReplaceChild(
                "EquipLeftBase4",
                CubeListBuilder.create().texOffs(83, 9).addBox(-6.5f, 0f, 0f, 11f, 16f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6.5f, 2.5f, 0.0873f, 0f, 0f)
            )

            val EquipLeftBase3 = EquipLeftBase.addOrReplaceChild(
                "EquipLeftBase3",
                CubeListBuilder.create().texOffs(77, 5).addBox(-7.5f, 5f, -10f, 13f, 19f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -0.0873f, 0f, 0f)
            )

            val EquipLeftTube1 = EquipLeftBase.addOrReplaceChild(
                "EquipLeftTube1",
                CubeListBuilder.create().texOffs(82, 56).addBox(-1.5f, -16f, -1.5f, 3f, 16f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, 8f, 3f, -0.6981f, 0.5236f, 0f)
            )

            val EquipLeftTube2 = EquipLeftTube1.addOrReplaceChild(
                "EquipLeftTube2",
                CubeListBuilder.create().texOffs(82, 56).addBox(-1.5f, -12f, -1.5f, 3f, 12f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, 0f, 0.8727f, 0f, 0f)
            )

            val EquipLeftTube3 = EquipLeftTube2.addOrReplaceChild(
                "EquipLeftTube3",
                CubeListBuilder.create().texOffs(82, 56).addBox(-1.5f, -20f, -1.5f, 3f, 20f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -11f, 0f, 1.4486f, 0.7854f, 0.2618f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -14f, 0f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -13f, 1f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0.5f, 0f)
            )
            addFaceLayer(GlowHead)

            val GlowArmLeft = GlowBodyMain.addOrReplaceChild(
                "GlowArmLeft",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(7f, -10f, 0f, 0f, 0f, -0.2618f)
            )

            val GlowEquipLeftBase = GlowArmLeft.addOrReplaceChild(
                "GlowEquipLeftBase",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(7f, 16f, 0f, 0f, 0f, 0.0873f)
            )

            val GlowEquipLeftBase3 = GlowEquipLeftBase.addOrReplaceChild(
                "GlowEquipLeftBase3",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 0f, 0f, -0.0873f, 0f, 0f)
            )

            val EquipLeftTooth = GlowEquipLeftBase3.addOrReplaceChild(
                "EquipLeftTooth",
                CubeListBuilder.create().texOffs(44, 0).addBox(-5.5f, 0f, 0f, 9f, 7f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 14f, -1.2f, 0.0873f, 0f, 0f)
            )

            val GlowArmRight = GlowBodyMain.addOrReplaceChild(
                "GlowArmRight",
                CubeListBuilder.create().texOffs(0, 53),
                PartPose.offsetAndRotation(-7f, -10f, 0f, 0f, 0f, 0.2618f)
            )

            val GlowEquipRightBase = GlowArmRight.addOrReplaceChild(
                "GlowEquipRightBase",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(-6f, 16f, 0f, 0f, 0f, -0.0873f)
            )

            val GlowEquipRightBase2 = GlowEquipRightBase.addOrReplaceChild(
                "GlowEquipRightBase2",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(-4.2f, 13f, 0f, 0f, 0f, 0.0873f)
            )

            val EquipRightTooth1 = GlowEquipRightBase2.addOrReplaceChild(
                "EquipRightTooth1",
                CubeListBuilder.create().texOffs(44, 13).addBox(0f, 0f, -4f, 2f, 5f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, 4f, 0f)
            )

            val GlowEquipRightBase3 = GlowEquipRightBase.addOrReplaceChild(
                "GlowEquipRightBase3",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(1f, 14f, 0f, 0f, 0f, -0.2618f)
            )

            val EquipRightTooth2 = GlowEquipRightBase3.addOrReplaceChild(
                "EquipRightTooth2",
                CubeListBuilder.create().texOffs(59, 24).addBox(0f, 0f, -2.5f, 2f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offset(-1.6f, 2.3f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
