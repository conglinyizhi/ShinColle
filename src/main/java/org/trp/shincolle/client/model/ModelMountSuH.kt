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
import net.minecraft.world.entity.Entity
import org.trp.shincolle.Shincolle
import org.trp.shincolle.entity.base.EntityMountBase
import kotlin.math.cos

class ModelMountSuH<T : Entity>(root: ModelPart) : EntityModel<T>(), IGlowableModel {
    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val Head01: ModelPart
    private val Jaw: ModelPart
    private val NeckFront: ModelPart
    private val Body01: ModelPart
    private val Head02: ModelPart
    private val Head03: ModelPart
    private val Head04: ModelPart
    private val Head05: ModelPart
    private val Head06: ModelPart
    private val Head07a: ModelPart
    private val HeadTooth: ModelPart
    private val Eye01a: ModelPart
    private val Eye01b: ModelPart
    private val Eye02a: ModelPart
    private val Eye02b: ModelPart
    private val Eye03a: ModelPart
    private val Eye03b: ModelPart
    private val JawTooth: ModelPart
    private val Jaw02: ModelPart
    private val Body02: ModelPart
    private val Body01a: ModelPart
    private val Body02a: ModelPart
    private val Body02b: ModelPart
    private val Body03: ModelPart
    private val Body03a: ModelPart
    private val Body03b: ModelPart
    private val Body04: ModelPart
    private val Body04a: ModelPart
    private val Body04b: ModelPart
    private val Bridge02: ModelPart
    private val Bridge01: ModelPart
    private val Head07b: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowNeck: ModelPart
    private val GlowJaw: ModelPart
    private val GlowHead01: ModelPart

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head04 = this.Neck.getChild("Head04")
        this.Head06 = this.Neck.getChild("Head06")
        this.Head02 = this.Neck.getChild("Head02")
        this.Head07a = this.Neck.getChild("Head07a")
        this.Head07b = this.Head07a.getChild("Head07b")
        this.Head03 = this.Neck.getChild("Head03")
        this.Bridge01 = this.Head03.getChild("Bridge01")
        this.Head05 = this.Neck.getChild("Head05")
        this.Jaw = this.Neck.getChild("Jaw")
        this.Jaw02 = this.Jaw.getChild("Jaw02")
        this.Body01 = this.Neck.getChild("Body01")
        this.Body01a = this.Body01.getChild("Body01a")
        this.Bridge02 = this.Body01a.getChild("Bridge02")
        this.Body02 = this.Body01.getChild("Body02")
        this.Body03 = this.Body02.getChild("Body03")
        this.Body03b = this.Body03.getChild("Body03b")
        this.Body03a = this.Body03.getChild("Body03a")
        this.Body04 = this.Body03.getChild("Body04")
        this.Body04a = this.Body04.getChild("Body04a")
        this.Body04b = this.Body04.getChild("Body04b")
        this.Body02b = this.Body02.getChild("Body02b")
        this.Body02a = this.Body02.getChild("Body02a")
        this.Head01 = this.Neck.getChild("Head01")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowJaw = this.GlowNeck.getChild("GlowJaw")
        this.JawTooth = this.GlowJaw.getChild("JawTooth")
        this.GlowHead01 = this.GlowNeck.getChild("GlowHead01")
        this.HeadTooth = this.GlowHead01.getChild("HeadTooth")
        this.Eye01a = this.GlowHead01.getChild("Eye01a")
        this.Eye01b = this.GlowHead01.getChild("Eye01b")
        this.Eye02a = this.GlowHead01.getChild("Eye02a")
        this.Eye02b = this.GlowHead01.getChild("Eye02b")
        this.Eye03a = this.GlowHead01.getChild("Eye03a")
        this.Eye03b = this.GlowHead01.getChild("Eye03b")
        this.NeckFront = this.GlowNeck.getChild("NeckFront")
    }

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val angleX = cos((ageInTicks * 0.08f).toDouble()).toFloat()
        val angleX2 = cos((-limbSwing * 0.8f + 0.7f).toDouble()).toFloat()

        var offsetY = 0.2f
        if (entity is EntityMountBase && entity.shipDepth > 0.0) {
            offsetY += angleX * 0.025f + 0.025f
        }
        this.BodyMain.y = offsetY * 16.0f
        this.GlowBodyMain.y = this.BodyMain.y

        this.Jaw.xRot = angleX * 0.075f + 0.26f
        this.GlowJaw.xRot = this.Jaw.xRot

        if (limbSwingAmount > 0.9f) {
            this.Body03.yRot = angleX2 * 0.075f
            this.Body04.yRot = angleX2 * 0.15f
        } else {
            this.Body03.yRot = 0.0f
            this.Body04.yRot = angleX * 0.15f
        }
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        BodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    override fun renderGlow(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        GlowBodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "mount_su_h"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 10f, 8f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7f, -7.5f, -14f, 14f, 15f, 14f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val Head04 = Neck.addOrReplaceChild(
                "Head04",
                CubeListBuilder.create().texOffs(0, 3).addBox(-9.5f, 0f, 0f, 19f, 8f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, -23.9f, -29.9f)
            )

            val Head06 = Neck.addOrReplaceChild(
                "Head06",
                CubeListBuilder.create().texOffs(0, 3).addBox(-7.5f, 0f, 0f, 15f, 6f, 11f, CubeDeformation(0f)),
                PartPose.offset(0f, -12.1f, -40.8f)
            )

            val Head02 = Neck.addOrReplaceChild(
                "Head02",
                CubeListBuilder.create().texOffs(0, 3).addBox(-9.5f, 0f, 0f, 19f, 10f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, -16f, -29.9f)
            )

            val Head07a = Neck.addOrReplaceChild(
                "Head07a",
                CubeListBuilder.create().texOffs(0, 4).addBox(-6f, 0f, -6f, 12f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -23.8f, -41.7f, 0f, 0.7854f, 0f)
            )

            val Head07b = Head07a.addOrReplaceChild(
                "Head07b",
                CubeListBuilder.create().texOffs(0, 4).addBox(-5f, 0f, -5f, 10f, 12f, 10f, CubeDeformation(0f)),
                PartPose.offset(-0.7f, 5.5f, 0.7f)
            )

            val Head03 = Neck.addOrReplaceChild(
                "Head03",
                CubeListBuilder.create().texOffs(0, 3).addBox(-9.5f, 0f, 0f, 19f, 8f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, -23.9f, -18f)
            )

            val Bridge01 = Head03.addOrReplaceChild(
                "Bridge01",
                CubeListBuilder.create().texOffs(0, 44).addBox(-3.5f, 0f, 0f, 7f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.1f, 0f, 1.5708f, 0f, 0f)
            )

            val Head05 = Neck.addOrReplaceChild(
                "Head05",
                CubeListBuilder.create().texOffs(0, 3).addBox(-8.5f, 0f, 0f, 17f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, -24f, -41.8f)
            )

            val Jaw = Neck.addOrReplaceChild(
                "Jaw",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7.5f, 0f, -16f, 15f, 7f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, -11.6f, 0.2618f, 0f, 0f)
            )

            val Jaw02 = Jaw.addOrReplaceChild(
                "Jaw02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.5f, 0f, -5.5f, 11f, 5f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.8f, -14.8f, -0.3316f, 0.7854f, -0.2409f)
            )

            val Body01 = Neck.addOrReplaceChild(
                "Body01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-8.5f, -12f, 0f, 17f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, -3f, -8.3f)
            )

            val Body01a = Body01.addOrReplaceChild(
                "Body01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7f, 0f, 0f, 14f, 9f, 11f, CubeDeformation(0f)),
                PartPose.offset(0f, -20.7f, 0f)
            )

            val Bridge02 = Body01a.addOrReplaceChild(
                "Bridge02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, 0f, 0f, 6f, 10f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.1f, 0f, 1.5708f, 0f, 0f)
            )

            val Body02 = Body01.addOrReplaceChild(
                "Body02",
                CubeListBuilder.create().texOffs(0, 3).addBox(-7f, -15f, 0f, 14f, 15f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, 6f, -0.2618f, 0f, 0f)
            )

            val Body03 = Body02.addOrReplaceChild(
                "Body03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-8f, -10f, 0f, 16f, 10f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3f, 8f, -0.1745f, 0f, 0f)
            )

            val Body03b = Body03.addOrReplaceChild(
                "Body03b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.5f, 0f, 0f, 11f, 10f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, -19.9f, -2f)
            )

            val Body03a = Body03.addOrReplaceChild(
                "Body03a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6f, -6f, 0f, 12f, 6f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, 4.5f, 0.4363f, 0f, 0f)
            )

            val Body04 = Body03.addOrReplaceChild(
                "Body04",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -8f, 0f, 9f, 8f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1f, 11f, 0.4363f, 0f, 0f)
            )

            val Body04a = Body04.addOrReplaceChild(
                "Body04a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, -6f, 0f, 5f, 6f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, 9.5f)
            )

            val Body04b = Body04.addOrReplaceChild(
                "Body04b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, 0f, 0f, 7f, 9f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15.6f, 6f, -0.4363f, 0f, 0f)
            )

            val Body02b = Body02.addOrReplaceChild(
                "Body02b",
                CubeListBuilder.create().texOffs(0, 3).addBox(-4.5f, 0f, 0f, 9f, 7f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, -21.8f, -2f)
            )

            val Body02a = Body02.addOrReplaceChild(
                "Body02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.5f, 0f, 0f, 13f, 7f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2.1f, 0f, 0.2618f, 0f, 0f)
            )

            val Head01 = Neck.addOrReplaceChild(
                "Head01",
                CubeListBuilder.create().texOffs(0, 3).addBox(-9.5f, -7f, -11f, 19f, 10f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, -9f, -7f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 10f, 8f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 0f)
            )

            val GlowJaw = GlowNeck.addOrReplaceChild(
                "GlowJaw",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 2f, -11.6f, 0.2618f, 0f, 0f)
            )

            val JawTooth = GlowJaw.addOrReplaceChild(
                "JawTooth",
                CubeListBuilder.create().texOffs(57, 46).mirror()
                    .addBox(-6.5f, 0f, -14f, 13f, 3f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1.7f, -2f, -0.0873f, -0.0223f, 0f)
            )

            val GlowHead01 = GlowNeck.addOrReplaceChild(
                "GlowHead01",
                CubeListBuilder.create().texOffs(0, 3),
                PartPose.offset(0f, -9f, -7f)
            )

            val HeadTooth = GlowHead01.addOrReplaceChild(
                "HeadTooth",
                CubeListBuilder.create().texOffs(56, 45).addBox(-6.5f, 0f, -6.5f, 13f, 4f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.5f, -15f, 0.0524f, 0f, 0f)
            )

            val Eye01a = GlowHead01.addOrReplaceChild(
                "Eye01a",
                CubeListBuilder.create().texOffs(77, 0).addBox(0f, 0f, 0f, 0f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offset(9.6f, -9f, -15f)
            )

            val Eye01b = GlowHead01.addOrReplaceChild(
                "Eye01b",
                CubeListBuilder.create().texOffs(77, 0).addBox(0f, 0f, 0f, 0f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offset(-9.6f, -9f, -15f)
            )

            val Eye02a = GlowHead01.addOrReplaceChild(
                "Eye02a",
                CubeListBuilder.create().texOffs(77, 8).addBox(0f, 0f, 0.1f, 0f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offset(9.6f, -9f, -15f)
            )

            val Eye02b = GlowHead01.addOrReplaceChild(
                "Eye02b",
                CubeListBuilder.create().texOffs(77, 8).addBox(0f, 0f, 0f, 0f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offset(-9.6f, -9f, -15f)
            )

            val Eye03a = GlowHead01.addOrReplaceChild(
                "Eye03a",
                CubeListBuilder.create().texOffs(77, 16).addBox(0f, 0f, 0f, 0f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offset(9.6f, -9f, -15f)
            )

            val Eye03b = GlowHead01.addOrReplaceChild(
                "Eye03b",
                CubeListBuilder.create().texOffs(77, 16).addBox(0f, 0f, 0f, 0f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offset(-9.6f, -9f, -15f)
            )

            val NeckFront = GlowNeck.addOrReplaceChild(
                "NeckFront",
                CubeListBuilder.create().texOffs(30, 48).addBox(-5.5f, 0f, 0f, 11f, 14f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -8.5f, -15f)
            )

            return LayerDefinition.create(meshdefinition, 128, 64)
        }
    }
}
