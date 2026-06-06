package org.trp.shincolle.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.LegacyPoseOffsets.deadY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sittingY
import org.trp.shincolle.entity.EntityDestroyerI
import org.trp.shincolle.entity.base.EntityShipBase

class ModelDestroyerI<T : Entity>(root: ModelPart) : EntityModel<T>(), IGlowableModel {
    private val PBack: ModelPart
    private val PNeck: ModelPart
    private val PHead: ModelPart
    private val PJawBottom: ModelPart
    private val PBody: ModelPart
    private val PLegLeft: ModelPart
    private val PLegLeftEnd: ModelPart
    private val PLegRight: ModelPart
    private val PLegRightEnd: ModelPart
    private val PTail: ModelPart
    private val PTailLeft: ModelPart
    private val PTailLeftEnd: ModelPart
    private val PTailRight: ModelPart
    private val PTailRightEnd: ModelPart
    private val PTailEnd: ModelPart
    private val PKisaragi00: ModelPart
    private val PKisaragi01: ModelPart
    private val PKisaragi02: ModelPart
    private val PKisaragi03: ModelPart
    private val PEyeLightL0: ModelPart
    private val PEyeLightL1: ModelPart
    private val PEyeLightL2: ModelPart
    private val PEyeLightR0: ModelPart
    private val PEyeLightR1: ModelPart
    private val PEyeLightR2: ModelPart
    private val GlowPBack: ModelPart
    private val GlowPNeck: ModelPart
    private val GlowPHead: ModelPart
    var poseTranslateY = 0f

    init {
        this.PBack = root.getChild("PBack")
        this.PNeck = this.PBack.getChild("PNeck")
        this.PHead = this.PNeck.getChild("PHead")
        this.PJawBottom = this.PHead.getChild("PJawBottom")
        this.PBody = this.PBack.getChild("PBody")
        this.PLegLeft = this.PBody.getChild("PLegLeft")
        this.PLegLeftEnd = this.PLegLeft.getChild("PLegLeftEnd")
        this.PLegRight = this.PBody.getChild("PLegRight")
        this.PLegRightEnd = this.PLegRight.getChild("PLegRightEnd")
        this.PTail = this.PBack.getChild("PTail")
        this.PTailLeft = this.PTail.getChild("PTailLeft")
        this.PTailLeftEnd = this.PTailLeft.getChild("PTailLeftEnd")
        this.PTailRight = this.PTail.getChild("PTailRight")
        this.PTailRightEnd = this.PTailRight.getChild("PTailRightEnd")
        this.PTailEnd = this.PTail.getChild("PTailEnd")
        this.GlowPBack = root.getChild("GlowPBack")
        this.GlowPNeck = this.GlowPBack.getChild("GlowPNeck")
        this.GlowPHead = this.GlowPNeck.getChild("GlowPHead")
        this.PEyeLightL0 = this.GlowPHead.getChild("PEyeLightL0")
        this.PEyeLightL1 = this.GlowPHead.getChild("PEyeLightL1")
        this.PEyeLightL2 = this.GlowPHead.getChild("PEyeLightL2")
        this.PEyeLightR0 = this.GlowPHead.getChild("PEyeLightR0")
        this.PEyeLightR1 = this.GlowPHead.getChild("PEyeLightR1")
        this.PEyeLightR2 = this.GlowPHead.getChild("PEyeLightR2")
        this.PKisaragi00 = this.GlowPHead.getChild("PKisaragi00")
        this.PKisaragi01 = this.GlowPHead.getChild("PKisaragi01")
        this.PKisaragi02 = this.GlowPHead.getChild("PKisaragi02")
        this.PKisaragi03 = this.GlowPHead.getChild("PKisaragi03")
    }

    override fun setupAnim(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        poseTranslateY = 0.0f
        if (entity !is EntityShipBase) {
            return
        }

        val angleZ = Mth.cos(ageInTicks * 0.125f)
        if (entity.shipDepth > 0.0) {
            poseTranslateY += angleZ * 0.05f + 0.025f
        }

        if (entity.isInDeadPose) {
            applyDeadPose()
            syncGlowParts()
            return
        }

        resetPose()
        applyFaceFromEntity(entity)
        applyEquipVisibility(entity)
        applyLook(netHeadYaw, headPitch, angleZ)
        if (entity.isInSittingPose()) {
            applySittingPose(entity, angleZ)
        } else {
            applyLegPose(limbSwing, limbSwingAmount)
            applyTailPose(angleZ)
            PBack.zRot = -0.31f
            poseTranslateY += 0.42f
        }
        syncGlowParts()
    }

    private fun resetPose() {
        PBack.xRot = 0.0f
        PBack.yRot = 0.0f
        PBack.zRot = 0.0f
        PLegLeft.xRot = 0.0f
        PLegLeft.zRot = 0.0f
        PLegRight.xRot = 0.0f
        PLegRight.zRot = 0.0f
    }

    private fun applyLook(headYaw: Float, headPitch: Float, angleZ: Float) {
        if (headPitch != 0.0f) {
            PNeck.yRot = headYaw * 0.006f
            PNeck.zRot = headPitch * 0.006f
            PHead.yRot = headYaw * 0.006f
            PHead.zRot = headPitch * 0.006f
            PTail.yRot = headYaw * -0.006f
        } else {
            PNeck.yRot = 0.0f
            PNeck.zRot = 0.2f
            PHead.yRot = 0.0f
            PHead.zRot = angleZ * 0.15f + 0.2f
            PTail.yRot = 0.0f
        }
    }

    private fun applyDeadPose() {
        this.poseTranslateY = DEAD_TRANSLATE_Y

        setFace(2)
        PBack.xRot = 1.4835f
        PBack.zRot = 0.0f
        PNeck.yRot = 0.0f
        PNeck.zRot = 0.2f
        PHead.yRot = 0.0f
        PHead.zRot = 0.2f
        PTail.yRot = 0.0f
        PLegLeft.xRot = -1.0472f
        PLegLeft.zRot = 0.0f
        PLegLeftEnd.zRot = -1.4f
        PLegRight.xRot = 0.087f
        PLegRight.zRot = -0.7854f
        PLegRightEnd.zRot = -1.4f
        PTail.zRot = 0.2f
        PTailEnd.zRot = 0.3f
        PJawBottom.zRot = -0.3f
    }

    private fun applyEquipVisibility(ship: EntityShipBase) {
        val show = ship.getEquipFlag(EntityDestroyerI.EQUIP_HEAD_ORNAMENT)
        PKisaragi00.visible = show
        PKisaragi01.visible = show
        PKisaragi02.visible = show
        PKisaragi03.visible = show
    }

    private fun applyFaceFromEntity(ship: EntityShipBase) {
        val faceId = ship.faceId
        val faceIndex = if (faceId >= 0) faceId % 3 else 0
        setFace(faceIndex)
    }

    private fun setFace(faceIndex: Int) {
        val show0 = faceIndex == 0
        val show1 = faceIndex == 1
        val show2 = faceIndex == 2
        PEyeLightL0.visible = show0
        PEyeLightR0.visible = show0
        PEyeLightL1.visible = show1
        PEyeLightR1.visible = show1
        PEyeLightL2.visible = show2
        PEyeLightR2.visible = show2
    }

    private fun applySittingPose(ship: EntityShipBase, angleZ: Float) {
        this.poseTranslateY = SITTING_TRANSLATE_Y
        if (ship.emotionSecondary == EntityShipBase.EMOTION_BORED) {
            poseTranslateY += 0.5f
            PBack.zRot = 0.6f
            PNeck.zRot = -0.25f
            PHead.zRot = -0.3f
            PLegRight.zRot = -1.0f
            PLegLeft.zRot = -1.0f
            PLegRightEnd.zRot = -1.1f
            PLegLeftEnd.zRot = -1.1f
            PTail.zRot = -0.6f
            PTailEnd.zRot = -0.6f
            PJawBottom.zRot = -0.7f
        } else {
            poseTranslateY += 0.68f
            PBack.zRot = -0.8f
            PNeck.zRot = -0.3f
            PLegRight.zRot = -0.8f
            PLegLeft.zRot = -0.8f
            PLegRightEnd.zRot = -1.4f
            PLegLeftEnd.zRot = -1.4f
            PTail.zRot = 0.4f
            PTailEnd.zRot = angleZ * 0.2f + 0.4f
            PJawBottom.zRot = angleZ * 0.05f - 0.3f
            PHead.zRot = angleZ * 0.02f + 0.4f
        }
    }

    private fun applyTailPose(angleZ: Float) {
        PTail.zRot = angleZ * 0.2f
        PTailEnd.zRot = angleZ * 0.3f
        PJawBottom.zRot = angleZ * 0.2f - 0.3f
    }

    private fun applyLegPose(limbSwing: Float, limbSwingAmount: Float) {
        PLegRight.zRot = Mth.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount - 0.6f
        PLegLeft.zRot = Mth.cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.4f * limbSwingAmount - 0.6f
        PLegRightEnd.zRot = Mth.sin(limbSwing * 0.6662f) * limbSwingAmount - 0.4f
        PLegLeftEnd.zRot = Mth.sin(limbSwing * 0.6662f + Math.PI.toFloat()) * limbSwingAmount - 0.4f
    }

    private fun syncGlowParts() {
        GlowPBack.copyFrom(PBack)
        GlowPNeck.copyFrom(PNeck)
        GlowPHead.copyFrom(PHead)
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        val usePoseTranslate = poseTranslateY != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, poseTranslateY, 0.0f)
        }

        PBack.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

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
        val usePoseTranslate = poseTranslateY != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, poseTranslateY, 0.0f)
        }

        GlowPBack.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "destroyer_i"), "main")
        private val SITTING_TRANSLATE_Y = sittingY("ModelDestroyerI")
        private val DEAD_TRANSLATE_Y = deadY("ModelDestroyerI")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val PBack = partdefinition.addOrReplaceChild(
                "PBack", CubeListBuilder.create().texOffs(128, 8)
                    .addBox(-12f, -10f, -12f, 28f, 20f, 24f), PartPose.offset(-8f, -16f, 0f)
            )

            val PNeck = PBack.addOrReplaceChild(
                "PNeck", CubeListBuilder.create()
                    .texOffs(128, 0).addBox(-3f, -11f, -13f, 30f, 26f, 26f)
                    .texOffs(128, 28).addBox(6f, 15f, -10f, 21f, 4f, 20f)
                    .texOffs(0, 70).addBox(-8f, 7f, -9f, 18f, 14f, 18f), PartPose.offset(15f, 0f, 0f)
            )

            val PHead = PNeck.addOrReplaceChild(
                "PHead", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-3f, -12f, -16f, 32f, 32f, 32f)
                    .texOffs(96, 0).addBox(14.5f, 20f, -6f, 4f, 6f, 12f)
                    .texOffs(128, 54).addBox(0f, 20f, -10f, 18f, 6f, 4f)
                    .texOffs(128, 54).addBox(0f, 20f, 6f, 18f, 6f, 4f)
                    .texOffs(0, 102).addBox(-3f, 20f, -11f, 22f, 2f, 22f), PartPose.offset(26f, 0f, 0f)
            )

            val PJawBottom = PHead.addOrReplaceChild(
                "PJawBottom", CubeListBuilder.create()
                    .texOffs(92, 64).addBox(-3f, 0f, -10f, 3f, 18f, 20f)
                    .texOffs(96, 19).addBox(-1f, 7.5f, 6f, 4f, 10f, 3f)
                    .texOffs(96, 19).addBox(-1f, 7.5f, -9f, 4f, 10f, 3f)
                    .texOffs(0, 0).addBox(-1f, 14.5f, -6f, 4f, 3f, 12f), PartPose.offset(-6f, 18f, 0f)
            )

            val PBody = PBack.addOrReplaceChild(
                "PBody", CubeListBuilder.create().texOffs(0, 64)
                    .addBox(-10f, 10f, -11f, 24f, 16f, 22f), PartPose.offset(0f, 0f, 0f)
            )

            val PLegLeft = PBody.addOrReplaceChild(
                "PLegLeft", CubeListBuilder.create().texOffs(0, 80)
                    .addBox(-3f, -4f, -1f, 8f, 14f, 8f), PartPose.offset(-3f, 24f, 6f)
            )

            val PLegLeftEnd = PLegLeft.addOrReplaceChild(
                "PLegLeftEnd", CubeListBuilder.create().texOffs(0, 90)
                    .addBox(-12f, -3f, -4f, 12f, 6f, 6f), PartPose.offset(1f, 8f, 4f)
            )

            val PLegRight = PBody.addOrReplaceChild(
                "PLegRight", CubeListBuilder.create().texOffs(0, 80)
                    .addBox(-3f, -4f, -5f, 8f, 14f, 8f), PartPose.offset(-3f, 24f, -8f)
            )

            val PLegRightEnd = PLegRight.addOrReplaceChild(
                "PLegRightEnd", CubeListBuilder.create().texOffs(0, 90)
                    .addBox(-12f, -3f, -3f, 12f, 6f, 6f), PartPose.offset(1f, 8f, -1f)
            )

            val PTail = PBack.addOrReplaceChild(
                "PTail", CubeListBuilder.create()
                    .texOffs(128, 16).addBox(-22f, -6f, -10f, 26f, 16f, 20f)
                    .texOffs(0, 68).addBox(-8f, 2f, -8f, 18f, 18f, 14f), PartPose.offset(-12f, -2f, 0f)
            )

            val PTailLeft = PTail.addOrReplaceChild(
                "PTailLeft", CubeListBuilder.create().texOffs(128, 28)
                    .addBox(-8f, -4f, 0f, 12f, 18f, 6f), PartPose.offset(-12f, 4f, 8f)
            )

            val PTailLeftEnd = PTailLeft.addOrReplaceChild(
                "PTailLeftEnd", CubeListBuilder.create().texOffs(128, 36)
                    .addBox(-24f, -4f, -2f, 24f, 12f, 4f), PartPose.offset(0f, 9f, 5f)
            )

            val PTailRight = PTail.addOrReplaceChild(
                "PTailRight", CubeListBuilder.create().texOffs(128, 28)
                    .addBox(-8f, -4f, -6f, 12f, 18f, 6f), PartPose.offset(-12f, 4f, -8f)
            )

            val PTailRightEnd = PTailRight.addOrReplaceChild(
                "PTailRightEnd", CubeListBuilder.create().texOffs(128, 36)
                    .addBox(-24f, -4f, -2f, 24f, 12f, 4f), PartPose.offset(0f, 9f, -5f)
            )

            val PTailEnd = PTail.addOrReplaceChild(
                "PTailEnd", CubeListBuilder.create().texOffs(128, 26)
                    .addBox(-20f, -6f, -8f, 24f, 10f, 16f), PartPose.offset(-22f, 2f, 0f)
            )

            val GlowPBack = partdefinition.addOrReplaceChild(
                "GlowPBack",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(-8f, -16f, 0f)
            )

            val GlowPNeck = GlowPBack.addOrReplaceChild(
                "GlowPNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(15f, 0f, 0f)
            )

            val GlowPHead = GlowPNeck.addOrReplaceChild(
                "GlowPHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(26f, 0f, 0f)
            )
            GlowPHead.addOrReplaceChild(
                "PEyeLightL0",
                CubeListBuilder.create().texOffs(138, 64).mirror().addBox(-3f, 0f, 15.1f, 24f, 20f, 1f),
                PartPose.ZERO
            )
            GlowPHead.addOrReplaceChild(
                "PEyeLightL1",
                CubeListBuilder.create().texOffs(138, 85).mirror().addBox(-3f, 0f, 15.1f, 24f, 20f, 1f),
                PartPose.ZERO
            )
            GlowPHead.addOrReplaceChild(
                "PEyeLightL2",
                CubeListBuilder.create().texOffs(138, 106).mirror().addBox(-3f, 0f, 15.1f, 24f, 20f, 1f),
                PartPose.ZERO
            )
            GlowPHead.addOrReplaceChild(
                "PEyeLightR0",
                CubeListBuilder.create().texOffs(138, 64).addBox(-3f, 0f, -16.1f, 24f, 20f, 1f),
                PartPose.ZERO
            )
            GlowPHead.addOrReplaceChild(
                "PEyeLightR1",
                CubeListBuilder.create().texOffs(138, 85).addBox(-3f, 0f, -16.1f, 24f, 20f, 1f),
                PartPose.ZERO
            )
            GlowPHead.addOrReplaceChild(
                "PEyeLightR2",
                CubeListBuilder.create().texOffs(138, 106).addBox(-3f, 0f, -16.1f, 24f, 20f, 1f),
                PartPose.ZERO
            )

            val PKisaragi00 = GlowPHead.addOrReplaceChild(
                "PKisaragi00", CubeListBuilder.create().texOffs(66, 102)
                    .addBox(0f, 0f, 0f, 8f, 8f, 5f), PartPose.offset(-7f, -9f, 14f)
            )

            val PKisaragi01 = GlowPHead.addOrReplaceChild(
                "PKisaragi01", CubeListBuilder.create().texOffs(114, 102)
                    .addBox(-2f, -16f, 1f, 8f, 20f, 3f), PartPose.offset(-7f, -9f, 14f)
            )

            val PKisaragi02 = GlowPHead.addOrReplaceChild(
                "PKisaragi02", CubeListBuilder.create().texOffs(92, 102)
                    .addBox(-7f, -17f, 0.8f, 8f, 18f, 3f), PartPose.offset(-7f, -9f, 14f)
            )

            val PKisaragi03 = GlowPHead.addOrReplaceChild(
                "PKisaragi03", CubeListBuilder.create().texOffs(92, 102)
                    .addBox(-9f, -18f, 0.6f, 8f, 18f, 3f), PartPose.offset(-7f, -9f, 14f)
            )

            return LayerDefinition.create(meshdefinition, 256, 128)
        }
    }
}
