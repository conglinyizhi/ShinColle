package org.trp.shincolle.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.LegacyPoseOffsets.deadY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sittingY
import org.trp.shincolle.entity.EntityDestroyerNi
import org.trp.shincolle.entity.base.EntityShipBase

class ModelDestroyerNi<T : Entity>(root: ModelPart) : EntityModel<T>(), IGlowableModel {
    private val Back: ModelPart
    private val NeckBack: ModelPart
    private val Body: ModelPart
    private val TailBack: ModelPart
    private val Head: ModelPart
    private val NeckBody: ModelPart
    private val EquipBase: ModelPart
    private val ArmLeft: ModelPart
    private val ArmRight: ModelPart
    private val k00: ModelPart
    private val ToothU: ModelPart
    private val Face00: ModelPart
    private val Face01: ModelPart
    private val Face02: ModelPart
    private val k01: ModelPart
    private val k02: ModelPart
    private val k03: ModelPart
    private val Equip01: ModelPart
    private val Equip02: ModelPart
    private val Equip03: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val TailEnd1: ModelPart
    private val GlowBack: ModelPart
    private val GlowNeckBack: ModelPart
    private val GlowHead: ModelPart
    var poseTranslateY = 0f

    init {
        this.Back = root.getChild("Back")
        this.Body = this.Back.getChild("Body")
        this.NeckBack = this.Back.getChild("NeckBack")
        this.NeckBody = this.NeckBack.getChild("NeckBody")
        this.ArmRight = this.NeckBack.getChild("ArmRight")
        this.ArmRight01 = this.ArmRight.getChild("ArmRight01")
        this.EquipBase = this.NeckBack.getChild("EquipBase")
        this.Equip01 = this.EquipBase.getChild("Equip01")
        this.Equip02 = this.Equip01.getChild("Equip02")
        this.Equip03 = this.Equip02.getChild("Equip03")
        this.Head = this.NeckBack.getChild("Head")
        this.ToothU = this.Head.getChild("ToothU")
        this.ArmLeft = this.NeckBack.getChild("ArmLeft")
        this.ArmLeft01 = this.ArmLeft.getChild("ArmLeft01")
        this.TailBack = this.Back.getChild("TailBack")
        this.TailEnd1 = this.TailBack.getChild("TailEnd1")
        this.GlowBack = root.getChild("GlowBack")
        this.GlowNeckBack = this.GlowBack.getChild("GlowNeckBack")
        this.GlowHead = this.GlowNeckBack.getChild("GlowHead")
        this.Face00 = this.GlowHead.getChild("Face00")
        this.Face01 = this.GlowHead.getChild("Face01")
        this.Face02 = this.GlowHead.getChild("Face02")
        this.k00 = this.GlowHead.getChild("k00")
        this.k01 = this.k00.getChild("k01")
        this.k02 = this.k00.getChild("k02")
        this.k03 = this.k00.getChild("k03")
    }

    override fun setupAnim(
        entity: T,
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

        val angleX = Mth.cos(ageInTicks * 0.125f)
        if (entity.shipDepth > 0.0) {
            poseTranslateY += angleX * 0.05f + 0.025f
        }

        if (entity.isInDeadPose) {
            applyDeadPose(entity)
            syncGlowParts()
            return
        }

        Back.xRot = 0.7854f
        ArmLeft.xRot = -0.5f
        ArmLeft.yRot = -0.7f
        ArmLeft.zRot = -1.2217f
        ArmRight.xRot = -0.5f
        ArmRight.yRot = 0.7f
        ArmRight.zRot = 1.2217f
        ArmLeft01.xRot = 0.0f
        ArmLeft01.zRot = 1.4f
        ArmRight01.xRot = 0.0f
        ArmRight01.zRot = -1.4f
        Equip01.xRot = 1.0f

        applyFaceFromEntity(entity)
        applyEquipVisibility(entity)
        applyLook(netHeadYaw, headPitch)
        applyTailPose(angleX)
        if (entity.isInSittingPose) {
            applySittingPose(entity, angleX)
        } else {
            applyLegPose(limbSwing, limbSwingAmount)
        }
        syncGlowParts()
    }

    private fun applyFaceFromEntity(ship: EntityShipBase) {
        val faceId = ship.faceId
        val faceIndex = if (faceId >= 0) faceId % 3 else 0
        setFace(faceIndex)
    }

    private fun setFace(faceIndex: Int) {
        Face00.visible = faceIndex == 0
        Face01.visible = faceIndex == 1
        Face02.visible = faceIndex == 2
    }

    private fun applyEquipVisibility(ship: EntityShipBase) {
        EquipBase.visible = ship.getEquipFlag(EntityDestroyerNi.EQUIP_RIGGING)
        val showOrnament = ship.getEquipFlag(EntityDestroyerNi.EQUIP_HEAD_ORNAMENT)
        k00.visible = showOrnament
        k01.visible = showOrnament
        k02.visible = showOrnament
        k03.visible = showOrnament
    }

    private fun applyLook(headYaw: Float, headPitch: Float) {
        if (headPitch != 0.0f) {
            NeckBack.xRot = headPitch * 0.005f
            NeckBack.yRot = headYaw * 0.005f
            Head.xRot = headPitch * 0.005f
            Head.yRot = headYaw * 0.005f
        }
    }

    private fun applyDeadPose(ship: EntityShipBase?) {
        this.poseTranslateY = DEAD_TRANSLATE_Y
        setFace(2)
        NeckBack.xRot = 0.3f
        NeckBack.yRot = 0.0f
        Head.xRot = 0.3f
        Head.yRot = 0.0f
        Equip01.yRot = 0.5f
        Equip02.zRot = 1.0f
        Equip03.zRot = -0.8f
        Back.xRot = -0.3236f
        ArmLeft.xRot = -1.4f
        ArmLeft.yRot = -0.7f
        ArmLeft.zRot = -0.2618f
        ArmRight.xRot = -1.4f
        ArmRight.yRot = 0.9f
        ArmRight.zRot = 0.2618f
        ArmLeft01.xRot = 0.0f
        ArmLeft01.zRot = 1.2f
        ArmRight01.xRot = 0.0f
        ArmRight01.zRot = -0.8f
        TailBack.xRot = -0.1f
        TailEnd1.xRot = 0.05f
        Equip01.xRot = 2.0f
    }

    private fun applyLegPose(limbSwing: Float, limbSwingAmount: Float) {
        val angle1 = Mth.cos(limbSwing * 0.6662f) * 1.1f * limbSwingAmount
        ArmLeft.xRot = angle1 - 0.5f
        ArmRight.xRot = -angle1 - 0.5f
    }

    private fun applySittingPose(ship: EntityShipBase, angleX: Float) {
        this.poseTranslateY = SITTING_TRANSLATE_Y
        if (ship.emotionSecondary == EntityShipBase.EMOTION_BORED) {
            poseTranslateY += angleX * 0.2f - 0.05f
            ArmLeft.zRot = -angleX * 0.6f - 1.0472f
            ArmLeft01.zRot = angleX * 0.5f + 1.2f
            ArmRight.zRot = angleX * 0.6f + 1.0472f
            ArmRight01.zRot = -angleX * 0.5f - 1.2f
            TailBack.xRot = angleX * 0.1f + 0.2f
            TailEnd1.xRot = angleX * 0.1f + 0.2f
        } else {
            poseTranslateY += 0.75f
            Back.xRot = -0.5236f
            ArmLeft.xRot = -0.6981f
            ArmLeft.yRot = -0.2618f
            ArmLeft.zRot = -0.2618f
            ArmRight.xRot = -0.6981f
            ArmRight.yRot = 0.2618f
            ArmRight.zRot = 0.2618f
            ArmLeft01.xRot = -1.9199f
            ArmLeft01.zRot = -0.6981f
            ArmRight01.xRot = -1.9199f
            ArmRight01.zRot = 0.6981f
            TailBack.xRot = angleX * 0.1f + 0.2f
            TailEnd1.xRot = angleX * 0.1f + 0.2f
            Equip01.xRot = 2.0f
        }
    }

    private fun applyTailPose(angleX: Float) {
        TailBack.xRot = angleX * 0.2f
        TailEnd1.xRot = angleX * 0.2f
        Equip01.yRot = angleX * 0.2f + 0.5f
        Equip02.zRot = angleX * 0.3f + 1.0f
        Equip03.zRot = angleX * 0.4f - 0.8f
    }

    private fun syncGlowParts() {
        GlowBack.copyFrom(Back)
        GlowNeckBack.copyFrom(NeckBack)
        GlowHead.copyFrom(Head)
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

        Back.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

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

        GlowBack.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "destroyer_ni"), "main")
        private val SITTING_TRANSLATE_Y = sittingY("ModelDestroyerNi")
        private val DEAD_TRANSLATE_Y = deadY("ModelDestroyerNi")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val Back = partdefinition.addOrReplaceChild(
                "Back",
                CubeListBuilder.create().texOffs(14, 76).addBox(-12f, -12f, -14f, 24f, 21f, 26f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -40f, 0f, 0.7854f, 0f, 0f)
            )

            val Body = Back.addOrReplaceChild(
                "Body",
                CubeListBuilder.create().texOffs(0, 33).addBox(-10f, 0f, 0f, 20f, 12f, 24f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 11f, -14f, 0.3643f, 0f, 0f)
            )

            val NeckBack = Back.addOrReplaceChild(
                "NeckBack",
                CubeListBuilder.create().texOffs(10, 76).addBox(-14f, -10f, -20f, 28f, 25f, 26f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2.5f, -14f, 0.0873f, 0f, 0f)
            )

            val NeckBody = NeckBack.addOrReplaceChild(
                "NeckBody",
                CubeListBuilder.create().texOffs(1, 36).addBox(-11f, 0f, -9f, 22f, 10f, 21f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 13f, -4f, -0.3187f, 0f, 0f)
            )

            val ArmRight = NeckBack.addOrReplaceChild(
                "ArmRight",
                CubeListBuilder.create().texOffs(0, 31).addBox(-4f, 0f, -4f, 8f, 30f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-13f, 15f, -9f, -0.5236f, 0.6981f, 1.0472f)
            )

            val ArmRight01 = ArmRight.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(2, 32).addBox(-3.5f, 0f, -3.5f, 7f, 30f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 28f, 0f, 0f, 0f, -1.3963f)
            )

            val EquipBase = NeckBack.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(11, 89).addBox(-20f, 0f, 0f, 40f, 13f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 11f, -26f)
            )

            val Equip01 = EquipBase.addOrReplaceChild(
                "Equip01",
                CubeListBuilder.create().texOffs(54, 64).addBox(0f, 0f, 0f, 0f, 24f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(18f, 13f, 9f, 1.0472f, 0.7854f, 0f)
            )

            val Equip02 = Equip01.addOrReplaceChild(
                "Equip02",
                CubeListBuilder.create().texOffs(54, 64).addBox(0f, 0f, 0f, 0f, 28f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 24f, 0f, 0f, 0f, 1.309f)
            )

            val Equip03 = Equip02.addOrReplaceChild(
                "Equip03",
                CubeListBuilder.create().texOffs(54, 64).addBox(0f, 0f, 0f, 0f, 32f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 28f, 0f, 0f, 0f, -1.0472f)
            )

            val Head = NeckBack.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(0, 70).addBox(-16f, -14f, -28f, 32f, 22f, 32f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3f, -19f, 0.0873f, 0f, 0f)
            )

            val ToothU = Head.addOrReplaceChild(
                "ToothU",
                CubeListBuilder.create().texOffs(0, 0).addBox(-11f, 0f, 0f, 22f, 9f, 22f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, -29f, 0.1396f, 0f, 0f)
            )

            val ArmLeft = NeckBack.addOrReplaceChild(
                "ArmLeft",
                CubeListBuilder.create().texOffs(0, 31).addBox(-4f, 0f, -4f, 8f, 30f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(13f, 15f, -9f, -0.5236f, -0.6981f, -1.0472f)
            )

            val ArmLeft01 = ArmLeft.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(2, 32).addBox(-3.5f, 0f, -3.5f, 7f, 30f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 28f, 0f, 0f, 0f, 1.3963f)
            )

            val TailBack = Back.addOrReplaceChild(
                "TailBack",
                CubeListBuilder.create().texOffs(22, 80).addBox(-10f, -4f, 0f, 20f, 17f, 22f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 9f, -0.1745f, 0f, 0f)
            )

            val TailEnd1 = TailBack.addOrReplaceChild(
                "TailEnd1",
                CubeListBuilder.create().texOffs(28, 82).addBox(-8f, -3f, 0f, 16f, 13f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 19f, -0.1745f, 0f, 0f)
            )

            val GlowBack = partdefinition.addOrReplaceChild(
                "GlowBack",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -40f, 0f, 0.7854f, 0f, 0f)
            )

            val GlowNeckBack = GlowBack.addOrReplaceChild(
                "GlowNeckBack",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -2.5f, -14f, 0.0873f, 0f, 0f)
            )

            val GlowHead = GlowNeckBack.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 3f, -19f, 0.0873f, 0f, 0f)
            )

            val Face00 = GlowHead.addOrReplaceChild(
                "Face00",
                CubeListBuilder.create().texOffs(68, 40).addBox(-10f, 0f, 0f, 20f, 0f, 20f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.3f, -27f)
            )

            val Face01 = GlowHead.addOrReplaceChild(
                "Face01",
                CubeListBuilder.create().texOffs(68, 20).addBox(-10f, 0f, 0f, 20f, 0f, 20f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.2f, -27f)
            )

            val Face02 = GlowHead.addOrReplaceChild(
                "Face02",
                CubeListBuilder.create().texOffs(68, 0).addBox(-10f, 0f, 0f, 20f, 0f, 20f, CubeDeformation(0f)),
                PartPose.offset(0f, -14.1f, -27f)
            )

            val k00 = GlowHead.addOrReplaceChild(
                "k00",
                CubeListBuilder.create().texOffs(100, 60).addBox(0f, 0f, 0f, 5f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(14f, -12f, 0f, -0.3491f, 0.2618f, 0f)
            )

            val k01 = k00.addOrReplaceChild(
                "k01",
                CubeListBuilder.create().texOffs(106, 76).addBox(1f, -18.5f, 1f, 3f, 18f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -0.5236f, 0f, 0f)
            )

            val k02 = k00.addOrReplaceChild(
                "k02",
                CubeListBuilder.create().texOffs(106, 76).addBox(0.8f, -25f, -0.7f, 3f, 18f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -1.3963f, 0f, 0f)
            )

            val k03 = k00.addOrReplaceChild(
                "k03",
                CubeListBuilder.create().texOffs(106, 76).addBox(0.6f, -24.5f, -2.5f, 3f, 18f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, -2.0944f, 0f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
