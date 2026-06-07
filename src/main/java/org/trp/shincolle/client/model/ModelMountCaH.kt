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

class ModelMountCaH<T : Entity>(root: ModelPart) : EntityModel<T>(), IGlowableModel {
    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val Seat01: ModelPart
    private val Back01: ModelPart
    private val Back02: ModelPart
    private val Back03: ModelPart
    private val Back04: ModelPart
    private val WingL02: ModelPart
    private val WingR02: ModelPart
    private val CannonL01: ModelPart
    private val CannonR01: ModelPart
    private val Tube01a: ModelPart
    private val Tube02a: ModelPart
    private val Cannon01a: ModelPart
    private val Cannon01a_1: ModelPart
    private val Cannon01a_2: ModelPart
    private val Cannon01a_3: ModelPart
    private val Cannon01a_4: ModelPart
    private val Cannon01a_5: ModelPart
    private val Cannon01a_6: ModelPart
    private val Cannon01a_7: ModelPart
    private val Tube01a_1: ModelPart
    private val Tube01a_2: ModelPart
    private val Head01: ModelPart
    private val Jaw01: ModelPart
    private val Head02: ModelPart
    private val HeadTooth01: ModelPart
    private val HeadTooth02: ModelPart
    private val Jaw02: ModelPart
    private val JawTooth01: ModelPart
    private val JawTooth02: ModelPart
    private val CannonL02: ModelPart
    private val CannonR02: ModelPart
    private val Tube01b: ModelPart
    private val Tube02b: ModelPart
    private val Cannon01b: ModelPart
    private val Cannon01c: ModelPart
    private val Cannon01b_1: ModelPart
    private val Cannon01c_1: ModelPart
    private val Cannon01b_2: ModelPart
    private val Cannon01c_2: ModelPart
    private val Cannon01b_3: ModelPart
    private val Cannon01c_3: ModelPart
    private val Cannon01b_4: ModelPart
    private val Cannon01c_4: ModelPart
    private val Cannon01b_5: ModelPart
    private val Cannon01c_5: ModelPart
    private val Cannon01b_6: ModelPart
    private val Cannon01c_6: ModelPart
    private val Cannon01b_7: ModelPart
    private val Cannon01c_7: ModelPart
    private val Tube01b_1: ModelPart
    private val Tube01b_2: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowBodyMain2: ModelPart
    private val GlowNeck: ModelPart
    private val GlowJaw01: ModelPart
    private val GlowHead01: ModelPart
    private val GlowCannonL01: ModelPart
    private val GlowCannonR01: ModelPart

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Back02 = this.BodyMain.getChild("Back02")
        this.Cannon01a_7 = this.BodyMain.getChild("Cannon01a_7")
        this.Cannon01b_7 = this.Cannon01a_7.getChild("Cannon01b_7")
        this.Cannon01c_7 = this.Cannon01b_7.getChild("Cannon01c_7")
        this.Seat01 = this.BodyMain.getChild("Seat01")
        this.Cannon01a = this.BodyMain.getChild("Cannon01a")
        this.Cannon01b = this.Cannon01a.getChild("Cannon01b")
        this.Cannon01c = this.Cannon01b.getChild("Cannon01c")
        this.Tube02a = this.BodyMain.getChild("Tube02a")
        this.Tube02b = this.Tube02a.getChild("Tube02b")
        this.Cannon01a_6 = this.BodyMain.getChild("Cannon01a_6")
        this.Cannon01b_6 = this.Cannon01a_6.getChild("Cannon01b_6")
        this.Cannon01c_6 = this.Cannon01b_6.getChild("Cannon01c_6")
        this.CannonR01 = this.BodyMain.getChild("CannonR01")
        this.Cannon01a_1 = this.BodyMain.getChild("Cannon01a_1")
        this.Cannon01b_1 = this.Cannon01a_1.getChild("Cannon01b_1")
        this.Cannon01c_1 = this.Cannon01b_1.getChild("Cannon01c_1")
        this.Cannon01a_5 = this.BodyMain.getChild("Cannon01a_5")
        this.Cannon01b_5 = this.Cannon01a_5.getChild("Cannon01b_5")
        this.Cannon01c_5 = this.Cannon01b_5.getChild("Cannon01c_5")
        this.CannonL01 = this.BodyMain.getChild("CannonL01")
        this.Cannon01a_4 = this.BodyMain.getChild("Cannon01a_4")
        this.Cannon01b_4 = this.Cannon01a_4.getChild("Cannon01b_4")
        this.Cannon01c_4 = this.Cannon01b_4.getChild("Cannon01c_4")
        this.Tube01a_1 = this.BodyMain.getChild("Tube01a_1")
        this.Tube01b_1 = this.Tube01a_1.getChild("Tube01b_1")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head01 = this.Neck.getChild("Head01")
        this.Head02 = this.Head01.getChild("Head02")
        this.Jaw01 = this.Neck.getChild("Jaw01")
        this.Jaw02 = this.Jaw01.getChild("Jaw02")
        this.Cannon01a_3 = this.BodyMain.getChild("Cannon01a_3")
        this.Cannon01b_3 = this.Cannon01a_3.getChild("Cannon01b_3")
        this.Cannon01c_3 = this.Cannon01b_3.getChild("Cannon01c_3")
        this.Back01 = this.BodyMain.getChild("Back01")
        this.Back04 = this.BodyMain.getChild("Back04")
        this.Cannon01a_2 = this.BodyMain.getChild("Cannon01a_2")
        this.Cannon01b_2 = this.Cannon01a_2.getChild("Cannon01b_2")
        this.Cannon01c_2 = this.Cannon01b_2.getChild("Cannon01c_2")
        this.Tube01a_2 = this.BodyMain.getChild("Tube01a_2")
        this.Tube01b_2 = this.Tube01a_2.getChild("Tube01b_2")
        this.Back03 = this.BodyMain.getChild("Back03")
        this.Tube01a = this.BodyMain.getChild("Tube01a")
        this.Tube01b = this.Tube01a.getChild("Tube01b")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowJaw01 = this.GlowNeck.getChild("GlowJaw01")
        this.JawTooth01 = this.GlowJaw01.getChild("JawTooth01")
        this.JawTooth02 = this.JawTooth01.getChild("JawTooth02")
        this.GlowHead01 = this.GlowNeck.getChild("GlowHead01")
        this.HeadTooth01 = this.GlowHead01.getChild("HeadTooth01")
        this.HeadTooth02 = this.HeadTooth01.getChild("HeadTooth02")
        this.GlowCannonL01 = this.GlowBodyMain.getChild("GlowCannonL01")
        this.CannonL02 = this.GlowCannonL01.getChild("CannonL02")
        this.GlowCannonR01 = this.GlowBodyMain.getChild("GlowCannonR01")
        this.CannonR02 = this.GlowCannonR01.getChild("CannonR02")
        this.GlowBodyMain2 = root.getChild("GlowBodyMain2")
        this.WingL02 = this.GlowBodyMain2.getChild("WingL02")
        this.WingR02 = this.GlowBodyMain2.getChild("WingR02")
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

        var offsetY = 0.0f
        if (entity is EntityMountBase && entity.shipDepth > 0.0) {
            offsetY += angleX * 0.025f + 0.025f
        }
        this.BodyMain.y = offsetY * 16.0f
        this.GlowBodyMain.y = this.BodyMain.y
        this.GlowBodyMain2.y = this.BodyMain.y

        this.Jaw01.xRot = angleX * 0.025f + 0.32f
        if (entity is EntityMountBase && entity.host != null && entity.host!!.isOrderedToSit) {
            this.Jaw01.xRot = 0.7f
        }
        this.GlowJaw01.xRot = this.Jaw01.xRot

        this.CannonL02.xRot = angleX * 0.05f - 0.3f
        this.CannonR02.xRot = -angleX * 0.05f
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
        GlowBodyMain2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "mount_ca_h"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.5f, 0f, 0f, 13f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 8f)
            )

            val Back02 = BodyMain.addOrReplaceChild(
                "Back02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, 0f, 0f, 10f, 14f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, -7f, 6f)
            )

            val Cannon01a_7 = BodyMain.addOrReplaceChild(
                "Cannon01a_7",
                CubeListBuilder.create().texOffs(20, 8).addBox(-2f, -2f, -3f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-12.5f, 7.5f, 5f, -0.1745f, 0.5236f, 0f)
            )

            val Cannon01b_7 = Cannon01a_7.addOrReplaceChild(
                "Cannon01b_7",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -4f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -1.8f)
            )

            val Cannon01c_7 = Cannon01b_7.addOrReplaceChild(
                "Cannon01c_7",
                CubeListBuilder.create().texOffs(16, 12).addBox(-0.5f, -0.5f, -7f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -4f)
            )

            val Seat01 = BodyMain.addOrReplaceChild(
                "Seat01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7.5f, 0f, 0f, 15f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10.5f, 0.3f, -0.1047f, 0f, 0f)
            )

            val Cannon01a = BodyMain.addOrReplaceChild(
                "Cannon01a",
                CubeListBuilder.create().texOffs(19, 0).addBox(-2f, -2f, -3f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, 12f, 4f, 0.2094f, -0.2618f, 0f)
            )

            val Cannon01b = Cannon01a.addOrReplaceChild(
                "Cannon01b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -4f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -1.8f)
            )

            val Cannon01c = Cannon01b.addOrReplaceChild(
                "Cannon01c",
                CubeListBuilder.create().texOffs(4, 8).addBox(-0.5f, -0.5f, -7f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -4f)
            )

            val Tube02a = BodyMain.addOrReplaceChild(
                "Tube02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -7f, -1f, 1f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3f, 2f, 9f, -0.7854f, -0.1396f, -0.2618f)
            )

            val Tube02b = Tube02a.addOrReplaceChild(
                "Tube02b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -7f, -1f, 1f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 0f, 1.3963f, 0f, 0f)
            )

            val Cannon01a_6 = BodyMain.addOrReplaceChild(
                "Cannon01a_6",
                CubeListBuilder.create().texOffs(20, 8).addBox(-2f, -2f, -3f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9f, 8f, 4f, -0.1396f, 0.2618f, 0f)
            )

            val Cannon01b_6 = Cannon01a_6.addOrReplaceChild(
                "Cannon01b_6",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -4f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -1.8f)
            )

            val Cannon01c_6 = Cannon01b_6.addOrReplaceChild(
                "Cannon01c_6",
                CubeListBuilder.create().texOffs(28, 12).addBox(-0.5f, -0.5f, -7f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -4f)
            )

            val CannonR01 = BodyMain.addOrReplaceChild(
                "CannonR01",
                CubeListBuilder.create().texOffs(9, 0).addBox(-3.5f, -5f, -8f, 7f, 5f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, -6f, 9f, -0.6981f, 0.1047f, 0f)
            )

            val Cannon01a_1 = BodyMain.addOrReplaceChild(
                "Cannon01a_1",
                CubeListBuilder.create().texOffs(20, 8).addBox(-2f, -2f, -3f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(12f, 11f, 5f, 0.1396f, -0.4189f, 0f)
            )

            val Cannon01b_1 = Cannon01a_1.addOrReplaceChild(
                "Cannon01b_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -4f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -1.8f)
            )

            val Cannon01c_1 = Cannon01b_1.addOrReplaceChild(
                "Cannon01c_1",
                CubeListBuilder.create().texOffs(20, 12).addBox(-0.5f, -0.5f, -7f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -4f)
            )

            val Cannon01a_5 = BodyMain.addOrReplaceChild(
                "Cannon01a_5",
                CubeListBuilder.create().texOffs(20, 8).addBox(-2f, -2f, -3f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-12f, 11f, 5f, 0.2094f, 0.3142f, 0f)
            )

            val Cannon01b_5 = Cannon01a_5.addOrReplaceChild(
                "Cannon01b_5",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -4f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -1.8f)
            )

            val Cannon01c_5 = Cannon01b_5.addOrReplaceChild(
                "Cannon01c_5",
                CubeListBuilder.create().texOffs(12, 12).addBox(-0.5f, -0.5f, -7f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -4f)
            )

            val CannonL01 = BodyMain.addOrReplaceChild(
                "CannonL01",
                CubeListBuilder.create().texOffs(9, 0).addBox(-3.5f, -5f, -8f, 7f, 5f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, -6f, 9f, -0.6981f, -0.1047f, 0f)
            )

            val Cannon01a_4 = BodyMain.addOrReplaceChild(
                "Cannon01a_4",
                CubeListBuilder.create().texOffs(20, 8).addBox(-2f, -2f, -3f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8f, 12f, 4f, 0.2094f, 0.2094f, 0f)
            )

            val Cannon01b_4 = Cannon01a_4.addOrReplaceChild(
                "Cannon01b_4",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -4f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -1.8f)
            )

            val Cannon01c_4 = Cannon01b_4.addOrReplaceChild(
                "Cannon01c_4",
                CubeListBuilder.create().texOffs(32, 12).addBox(-0.5f, -0.5f, -7f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -4f)
            )

            val Tube01a_1 = BodyMain.addOrReplaceChild(
                "Tube01a_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -7f, 0f, 1f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(11f, 8f, 7f, -0.6981f, 0f, -0.3491f)
            )

            val Tube01b_1 = Tube01a_1.addOrReplaceChild(
                "Tube01b_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -7f, -1f, 1f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 1f, 1.3963f, 0f, 0f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(54, 0).addBox(-6f, 0f, 0f, 12f, 12f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -5f)
            )

            val Head01 = Neck.addOrReplaceChild(
                "Head01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7f, -6f, -15f, 14f, 6f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 5.8f, 5f)
            )

            val Head02 = Head01.addOrReplaceChild(
                "Head02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, 0f, -5f, 10f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -5.9f, -15f, 0f, 0.7854f, 0f)
            )

            val Jaw01 = Neck.addOrReplaceChild(
                "Jaw01",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-7f, 0f, -15f, 14f, 6f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 6f, 0.3142f, 0f, 0f)
            )

            val Jaw02 = Jaw01.addOrReplaceChild(
                "Jaw02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, 0f, -5f, 10f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.1f, -15f, 0f, 0.7854f, 0f)
            )

            val Cannon01a_3 = BodyMain.addOrReplaceChild(
                "Cannon01a_3",
                CubeListBuilder.create().texOffs(20, 8).addBox(-2f, -2f, -3f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(12.5f, 7.5f, 5f, -0.0524f, -0.5236f, 0f)
            )

            val Cannon01b_3 = Cannon01a_3.addOrReplaceChild(
                "Cannon01b_3",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -4f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -1.8f)
            )

            val Cannon01c_3 = Cannon01b_3.addOrReplaceChild(
                "Cannon01c_3",
                CubeListBuilder.create().texOffs(24, 12).addBox(-0.5f, -0.5f, -7f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -4f)
            )

            val Back01 = BodyMain.addOrReplaceChild(
                "Back01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6f, 0f, 0f, 12f, 9f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, -9f, 1f)
            )

            val Back04 = BodyMain.addOrReplaceChild(
                "Back04",
                CubeListBuilder.create().texOffs(0, 0).addBox(-8f, 0f, 0f, 16f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 7f, 6f)
            )

            val Cannon01a_2 = BodyMain.addOrReplaceChild(
                "Cannon01a_2",
                CubeListBuilder.create().texOffs(20, 8).addBox(-2f, -2f, -3f, 4f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(9f, 8f, 4f, -0.1396f, -0.3142f, 0f)
            )

            val Cannon01b_2 = Cannon01a_2.addOrReplaceChild(
                "Cannon01b_2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -1f, -4f, 2f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -1.8f)
            )

            val Cannon01c_2 = Cannon01b_2.addOrReplaceChild(
                "Cannon01c_2",
                CubeListBuilder.create().texOffs(8, 12).addBox(-0.5f, -0.5f, -7f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -4f)
            )

            val Tube01a_2 = BodyMain.addOrReplaceChild(
                "Tube01a_2",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -7f, 0f, 1f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-12f, 8f, 6.7f, -0.6981f, 0f, 0.3491f)
            )

            val Tube01b_2 = Tube01a_2.addOrReplaceChild(
                "Tube01b_2",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -7f, -1f, 1f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 1f, 1.3963f, 0f, 0f)
            )

            val Back03 = BodyMain.addOrReplaceChild(
                "Back03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-9f, 0f, 0f, 18f, 14f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, -5f, 0f)
            )

            val Tube01a = BodyMain.addOrReplaceChild(
                "Tube01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -7f, 0f, 1f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, 1f, 9f, -0.7854f, 0.8727f, 0.2618f)
            )

            val Tube01b = Tube01a.addOrReplaceChild(
                "Tube01b",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -7f, -1f, 1f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 1f, 1.3963f, 0f, 0f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 8f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, -5f)
            )

            val GlowJaw01 = GlowNeck.addOrReplaceChild(
                "GlowJaw01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 7f, 6f, 0.3142f, 0f, 0f)
            )

            val JawTooth01 = GlowJaw01.addOrReplaceChild(
                "JawTooth01",
                CubeListBuilder.create().texOffs(24, 24).addBox(-6.5f, 0f, -14f, 13f, 4f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.8f, -0.8f, -0.1396f, 0f, 0f)
            )

            val JawTooth02 = JawTooth01.addOrReplaceChild(
                "JawTooth02",
                CubeListBuilder.create().texOffs(0, 23).addBox(-4.5f, 0f, -4.5f, 9f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -13.9f, -0.075f, 0.7854f, -0.0524f)
            )

            val GlowHead01 = GlowNeck.addOrReplaceChild(
                "GlowHead01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 5.8f, 5f)
            )

            val HeadTooth01 = GlowHead01.addOrReplaceChild(
                "HeadTooth01",
                CubeListBuilder.create().texOffs(24, 24).addBox(-6.5f, 0f, -6.5f, 13f, 4f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, 1.9f, -7.5f)
            )

            val HeadTooth02 = HeadTooth01.addOrReplaceChild(
                "HeadTooth02",
                CubeListBuilder.create().texOffs(0, 23).addBox(-4.5f, 0f, -4.5f, 9f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -6.4f, -0.075f, 0.7854f, -0.0524f)
            )

            val GlowCannonL01 = GlowBodyMain.addOrReplaceChild(
                "GlowCannonL01",
                CubeListBuilder.create().texOffs(9, 0),
                PartPose.offsetAndRotation(4f, -6f, 9f, -0.6981f, -0.1047f, 0f)
            )

            val CannonL02 = GlowCannonL01.addOrReplaceChild(
                "CannonL02",
                CubeListBuilder.create().texOffs(0, 9).addBox(-1f, -1f, -12f, 2f, 2f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3.2f, -7.5f, -0.2618f, 0f, 0f)
            )

            val GlowCannonR01 = GlowBodyMain.addOrReplaceChild(
                "GlowCannonR01",
                CubeListBuilder.create().texOffs(9, 0),
                PartPose.offsetAndRotation(-4f, -6f, 9f, -0.6981f, 0.1047f, 0f)
            )

            val CannonR02 = GlowCannonR01.addOrReplaceChild(
                "CannonR02",
                CubeListBuilder.create().texOffs(0, 9).addBox(-1f, -1f, -12f, 2f, 2f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3.2f, -7.5f, -0.1396f, 0f, 0f)
            )

            val GlowBodyMain2 = partdefinition.addOrReplaceChild(
                "GlowBodyMain2",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 8f)
            )

            val WingL02 = GlowBodyMain2.addOrReplaceChild(
                "WingL02",
                CubeListBuilder.create().texOffs(0, 41).addBox(0f, -3f, -14f, 4f, 6f, 17f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, -2f, 6f, 0f, -0.1047f, 0f)
            )

            val WingR02 = GlowBodyMain2.addOrReplaceChild(
                "WingR02",
                CubeListBuilder.create().texOffs(0, 41).mirror()
                    .addBox(-4f, -3f, -14f, 4f, 6f, 17f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, -2f, 6f, 0f, 0.1047f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 64)
        }
    }
}
