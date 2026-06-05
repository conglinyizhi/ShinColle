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

class ModelMountCaWD<T : Entity?>(root: ModelPart) : EntityModel<T?>(), IGlowableModel {
    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val WingL01a: ModelPart
    private val WingR01a: ModelPart
    private val Seat01: ModelPart
    private val Back01: ModelPart
    private val Back02: ModelPart
    private val WingL03: ModelPart
    private val WingR03: ModelPart
    private val WingL04: ModelPart
    private val WingR04: ModelPart
    private val Back03: ModelPart
    private val Back04: ModelPart
    private val WingL02: ModelPart
    private val WingR02: ModelPart
    private val CannonL01: ModelPart
    private val CannonR01: ModelPart
    private val Tube01a: ModelPart
    private val Tube02a: ModelPart
    private val CannonM01: ModelPart
    private val Head01: ModelPart
    private val Jaw01: ModelPart
    private val Head02: ModelPart
    private val HeadTooth01: ModelPart
    private val HeadTooth02: ModelPart
    private val Jaw02: ModelPart
    private val JawTooth01: ModelPart
    private val JawTooth02: ModelPart
    private val WingL01b: ModelPart
    private val WingL01c: ModelPart
    private val WingL01Fire: ModelPart
    private val WingR01b: ModelPart
    private val WingR01c: ModelPart
    private val WingR01Fire: ModelPart
    private val Seat02: ModelPart
    private val Seat03: ModelPart
    private val CannonL02: ModelPart
    private val CannonR02: ModelPart
    private val Tube01b: ModelPart
    private val Tube02b: ModelPart
    private val CannonM02: ModelPart
    private val CannonM04: ModelPart
    private val CannonM03: ModelPart
    private val CannonM05: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowBodyMain2: ModelPart
    private val GlowNeck: ModelPart
    private val GlowJaw01: ModelPart
    private val GlowHead01: ModelPart
    private val GlowWingL01a: ModelPart
    private val GlowWingL01a2: ModelPart
    private val GlowWingL01b: ModelPart
    private val GlowWingR01a: ModelPart
    private val GlowWingR01a2: ModelPart
    private val GlowWingR01b: ModelPart
    private val GlowCannonL01: ModelPart
    private val GlowCannonR01: ModelPart
    private val GlowCannonM01: ModelPart
    private val GlowCannonM02: ModelPart
    private val GlowCannonM04: ModelPart

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Head01 = this.Neck.getChild("Head01")
        this.Head02 = this.Head01.getChild("Head02")
        this.Jaw01 = this.Neck.getChild("Jaw01")
        this.Jaw02 = this.Jaw01.getChild("Jaw02")
        this.Tube01a = this.BodyMain.getChild("Tube01a")
        this.Tube01b = this.Tube01a.getChild("Tube01b")
        this.Seat01 = this.BodyMain.getChild("Seat01")
        this.Seat03 = this.Seat01.getChild("Seat03")
        this.Seat02 = this.Seat01.getChild("Seat02")
        this.WingR01a = this.BodyMain.getChild("WingR01a")
        this.Back01 = this.BodyMain.getChild("Back01")
        this.Back03 = this.BodyMain.getChild("Back03")
        this.CannonM01 = this.BodyMain.getChild("CannonM01")
        this.CannonM02 = this.CannonM01.getChild("CannonM02")
        this.CannonM04 = this.CannonM01.getChild("CannonM04")
        this.CannonR01 = this.BodyMain.getChild("CannonR01")
        this.WingL01a = this.BodyMain.getChild("WingL01a")
        this.Back02 = this.BodyMain.getChild("Back02")
        this.Back04 = this.BodyMain.getChild("Back04")
        this.CannonL01 = this.BodyMain.getChild("CannonL01")
        this.Tube02a = this.BodyMain.getChild("Tube02a")
        this.Tube02b = this.Tube02a.getChild("Tube02b")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowJaw01 = this.GlowNeck.getChild("GlowJaw01")
        this.JawTooth01 = this.GlowJaw01.getChild("JawTooth01")
        this.JawTooth02 = this.JawTooth01.getChild("JawTooth02")
        this.GlowHead01 = this.GlowNeck.getChild("GlowHead01")
        this.HeadTooth01 = this.GlowHead01.getChild("HeadTooth01")
        this.HeadTooth02 = this.HeadTooth01.getChild("HeadTooth02")
        this.GlowWingL01a = this.GlowBodyMain.getChild("GlowWingL01a")
        this.GlowWingL01b = this.GlowWingL01a.getChild("GlowWingL01b")
        this.WingL01Fire = this.GlowWingL01b.getChild("WingL01Fire")
        this.GlowWingR01a = this.GlowBodyMain.getChild("GlowWingR01a")
        this.GlowWingR01b = this.GlowWingR01a.getChild("GlowWingR01b")
        this.WingR01Fire = this.GlowWingR01b.getChild("WingR01Fire")
        this.GlowCannonL01 = this.GlowBodyMain.getChild("GlowCannonL01")
        this.CannonL02 = this.GlowCannonL01.getChild("CannonL02")
        this.GlowCannonR01 = this.GlowBodyMain.getChild("GlowCannonR01")
        this.CannonR02 = this.GlowCannonR01.getChild("CannonR02")
        this.GlowCannonM01 = this.GlowBodyMain.getChild("GlowCannonM01")
        this.GlowCannonM02 = this.GlowCannonM01.getChild("GlowCannonM02")
        this.CannonM03 = this.GlowCannonM02.getChild("CannonM03")
        this.GlowCannonM04 = this.GlowCannonM01.getChild("GlowCannonM04")
        this.CannonM05 = this.GlowCannonM04.getChild("CannonM05")
        this.GlowBodyMain2 = root.getChild("GlowBodyMain2")
        this.WingL02 = this.GlowBodyMain2.getChild("WingL02")
        this.WingR02 = this.GlowBodyMain2.getChild("WingR02")
        this.WingL03 = this.GlowBodyMain2.getChild("WingL03")
        this.WingR03 = this.GlowBodyMain2.getChild("WingR03")
        this.WingL04 = this.GlowBodyMain2.getChild("WingL04")
        this.WingR04 = this.GlowBodyMain2.getChild("WingR04")
        this.GlowWingL01a2 = this.GlowBodyMain2.getChild("GlowWingL01a2")
        this.WingL01b = this.GlowWingL01a2.getChild("WingL01b")
        this.WingL01c = this.WingL01b.getChild("WingL01c")
        this.GlowWingR01a2 = this.GlowBodyMain2.getChild("GlowWingR01a2")
        this.WingR01b = this.GlowWingR01a2.getChild("WingR01b")
        this.WingR01c = this.WingR01b.getChild("WingR01c")
    }

    override fun setupAnim(
        entity: T?,
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
        if (entity is EntityMountBase && entity.getHost() != null && entity.getHost()!!.isOrderedToSit()) {
            this.Jaw01.xRot = 0.7f
        }
        this.GlowJaw01.xRot = this.Jaw01.xRot

        this.CannonL02.xRot = angleX * 0.05f - 0.3f
        this.CannonR02.xRot = -angleX * 0.05f
        this.CannonM03.xRot = -angleX * 0.05f
        this.CannonM05.xRot = angleX * 0.05f

        val showFire = limbSwingAmount > 0.2f
        this.WingL01Fire.visible = showFire
        this.WingR01Fire.visible = showFire
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "mount_ca_w_d"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.5f, 0f, 0f, 13f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 8f)
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

            val Tube01a = BodyMain.addOrReplaceChild(
                "Tube01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -7f, 0f, 1f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, 1f, 9f, -0.7854f, 0.8727f, 0.2618f)
            )

            val Tube01b = Tube01a.addOrReplaceChild(
                "Tube01b",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -7f, -1f, 1f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 1f, 1.3963f, 0f, 0f)
            )

            val Seat01 = BodyMain.addOrReplaceChild(
                "Seat01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7.5f, 0f, 0f, 15f, 11f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10.5f, 0.3f, -0.1047f, 0f, 0f)
            )

            val Seat03 = Seat01.addOrReplaceChild(
                "Seat03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2f, 0f, -9f, 2f, 10f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.2f, 1f, 0.5f, 0.1047f, 0.1047f, -0.1047f)
            )

            val Seat02 = Seat01.addOrReplaceChild(
                "Seat02",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, -9f, 2f, 10f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.2f, 1f, 0.5f, 0.1047f, -0.1047f, 0.1047f)
            )

            val WingR01a = BodyMain.addOrReplaceChild(
                "WingR01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7f, 0f, 0f, 7f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, 13.5f, -4f, 0f, 0.3491f, -0.5236f)
            )

            val Back01 = BodyMain.addOrReplaceChild(
                "Back01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6f, 0f, 0f, 12f, 9f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, -9f, 1f)
            )

            val Back03 = BodyMain.addOrReplaceChild(
                "Back03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-9f, 0f, 0f, 18f, 14f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, -5f, 0f)
            )

            val CannonM01 = BodyMain.addOrReplaceChild(
                "CannonM01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, -3f, -4f, 6f, 3f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -8.5f, 7f, -0.8727f, 0f, 0f)
            )

            val CannonM02 = CannonM01.addOrReplaceChild(
                "CannonM02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, -1f, -2f, 1f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(1.3f, -1.7f, -3.5f)
            )

            val CannonM04 = CannonM01.addOrReplaceChild(
                "CannonM04",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, -1f, -2f, 1f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(-1.3f, -1.7f, -3.5f)
            )

            val CannonR01 = BodyMain.addOrReplaceChild(
                "CannonR01",
                CubeListBuilder.create().texOffs(9, 0).addBox(-3.5f, -5f, -8f, 7f, 5f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8f, -6f, 9f, -0.5236f, 0.5236f, 0f)
            )

            val WingL01a = BodyMain.addOrReplaceChild(
                "WingL01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 7f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, 13.5f, -4f, 0f, -0.3491f, 0.5236f)
            )

            val Back02 = BodyMain.addOrReplaceChild(
                "Back02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, 0f, 0f, 10f, 14f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, -7f, 6f)
            )

            val Back04 = BodyMain.addOrReplaceChild(
                "Back04",
                CubeListBuilder.create().texOffs(0, 0).addBox(-8f, 0f, 0f, 16f, 2f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 7f, 6f)
            )

            val CannonL01 = BodyMain.addOrReplaceChild(
                "CannonL01",
                CubeListBuilder.create().texOffs(9, 0).addBox(-3.5f, -5f, -8f, 7f, 5f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, -6f, 9f, -0.5236f, -0.5236f, 0f)
            )

            val Tube02a = BodyMain.addOrReplaceChild(
                "Tube02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -7f, -1f, 1f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5f, 2f, 9f, -0.7854f, -0.1396f, -0.2618f)
            )

            val Tube02b = Tube02a.addOrReplaceChild(
                "Tube02b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1f, -7f, -1f, 1f, 7f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 0f, 1.3963f, 0f, 0f)
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
                CubeListBuilder.create().texOffs(78, 48).addBox(-6.5f, 0f, -14f, 13f, 4f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.8f, -0.8f, -0.1396f, 0f, 0f)
            )

            val JawTooth02 = JawTooth01.addOrReplaceChild(
                "JawTooth02",
                CubeListBuilder.create().texOffs(54, 46).addBox(-4.5f, 0f, -4.5f, 9f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -13.9f, -0.075f, 0.7854f, -0.0524f)
            )

            val GlowHead01 = GlowNeck.addOrReplaceChild(
                "GlowHead01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 5.8f, 5f)
            )

            val HeadTooth01 = GlowHead01.addOrReplaceChild(
                "HeadTooth01",
                CubeListBuilder.create().texOffs(78, 48).addBox(-6.5f, 0f, -6.5f, 13f, 4f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, 1.9f, -7.5f)
            )

            val HeadTooth02 = HeadTooth01.addOrReplaceChild(
                "HeadTooth02",
                CubeListBuilder.create().texOffs(54, 46).addBox(-4.5f, 0f, -4.5f, 9f, 4f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -6.4f, -0.075f, 0.7854f, -0.0524f)
            )

            val GlowWingL01a = GlowBodyMain.addOrReplaceChild(
                "GlowWingL01a",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(6f, 13.5f, -4f, 0f, -0.3491f, 0.5236f)
            )

            val GlowWingL01b = GlowWingL01a.addOrReplaceChild(
                "GlowWingL01b",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(6.5f, -1.5f, -4f, -0.0873f, -0.0873f, 0f)
            )

            val WingL01Fire = GlowWingL01b.addOrReplaceChild(
                "WingL01Fire",
                CubeListBuilder.create().texOffs(116, 48).addBox(-1f, -2f, 0f, 2f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offset(1.5f, 2.5f, 8.1f)
            )

            val GlowWingR01a = GlowBodyMain.addOrReplaceChild(
                "GlowWingR01a",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(-6f, 13.5f, -4f, 0f, 0.3491f, -0.5236f)
            )

            val GlowWingR01b = GlowWingR01a.addOrReplaceChild(
                "GlowWingR01b",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(-6.5f, -1.5f, -4f, -0.0873f, 0.0873f, 0f)
            )

            val WingR01Fire = GlowWingR01b.addOrReplaceChild(
                "WingR01Fire",
                CubeListBuilder.create().texOffs(116, 48).addBox(-1f, -2f, 0f, 2f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offset(-1.5f, 2.5f, 8.1f)
            )

            val GlowCannonL01 = GlowBodyMain.addOrReplaceChild(
                "GlowCannonL01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(8f, -6f, 9f, -0.5236f, -0.5236f, 0f)
            )

            val CannonL02 = GlowCannonL01.addOrReplaceChild(
                "CannonL02",
                CubeListBuilder.create().texOffs(0, 9).addBox(-1f, -1f, -12f, 2f, 2f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3.2f, -7.5f, -0.2618f, 0f, 0f)
            )

            val GlowCannonR01 = GlowBodyMain.addOrReplaceChild(
                "GlowCannonR01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(-8f, -6f, 9f, -0.5236f, 0.5236f, 0f)
            )

            val CannonR02 = GlowCannonR01.addOrReplaceChild(
                "CannonR02",
                CubeListBuilder.create().texOffs(0, 9).addBox(-1f, -1f, -12f, 2f, 2f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, -3.2f, -7.5f)
            )

            val GlowCannonM01 = GlowBodyMain.addOrReplaceChild(
                "GlowCannonM01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -8.5f, 7f, -0.8727f, 0f, 0f)
            )

            val GlowCannonM02 = GlowCannonM01.addOrReplaceChild(
                "GlowCannonM02",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(1.3f, -1.7f, -3.5f)
            )

            val CannonM03 = GlowCannonM02.addOrReplaceChild(
                "CannonM03",
                CubeListBuilder.create().texOffs(28, 15).addBox(0f, 0f, -6f, 1f, 1f, 6f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, -0.7f, -2f)
            )

            val GlowCannonM04 = GlowCannonM01.addOrReplaceChild(
                "GlowCannonM04",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(-1.3f, -1.7f, -3.5f)
            )

            val CannonM05 = GlowCannonM04.addOrReplaceChild(
                "CannonM05",
                CubeListBuilder.create().texOffs(28, 15).addBox(0f, 0f, -6f, 1f, 1f, 6f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, -0.7f, -2f)
            )

            val GlowBodyMain2 = partdefinition.addOrReplaceChild(
                "GlowBodyMain2",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 8f)
            )

            val WingL02 = GlowBodyMain2.addOrReplaceChild(
                "WingL02",
                CubeListBuilder.create().texOffs(0, 35).addBox(0f, -3f, -14f, 4f, 6f, 17f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, -5f, 6f, 0f, -0.1745f, 0f)
            )

            val WingR02 = GlowBodyMain2.addOrReplaceChild(
                "WingR02",
                CubeListBuilder.create().texOffs(0, 35).mirror()
                    .addBox(-4f, -3f, -14f, 4f, 6f, 17f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, -5f, 6f, 0f, 0.1745f, 0f)
            )

            val WingL03 = GlowBodyMain2.addOrReplaceChild(
                "WingL03",
                CubeListBuilder.create().texOffs(30, 40).addBox(0f, 0f, -20f, 2f, 4f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.5f, -0.5f, 11f, 0.2094f, -0.2618f, 0f)
            )

            val WingR03 = GlowBodyMain2.addOrReplaceChild(
                "WingR03",
                CubeListBuilder.create().texOffs(30, 40).mirror()
                    .addBox(-2f, 0f, -20f, 2f, 4f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7.5f, -0.5f, 11f, 0.2094f, 0.2618f, 0f)
            )

            val WingL04 = GlowBodyMain2.addOrReplaceChild(
                "WingL04",
                CubeListBuilder.create().texOffs(0, 47).addBox(0f, 0f, -10f, 2f, 5f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, 6f, 9f, 0.2094f, -0.3491f, 0.1745f)
            )

            val WingR04 = GlowBodyMain2.addOrReplaceChild(
                "WingR04",
                CubeListBuilder.create().texOffs(0, 47).mirror()
                    .addBox(-2f, 0f, -10f, 2f, 5f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8f, 6f, 9f, 0.2094f, 0.3491f, -0.1745f)
            )

            val GlowWingL01a2 = GlowBodyMain2.addOrReplaceChild(
                "GlowWingL01a2",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(6f, 13.5f, -4f, 0f, -0.3491f, 0.5236f)
            )

            val WingL01b = GlowWingL01a2.addOrReplaceChild(
                "WingL01b",
                CubeListBuilder.create().texOffs(25, 39).addBox(0f, 0f, 0f, 3f, 5f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.5f, -1.5f, -4f, -0.0873f, -0.0873f, 0f)
            )

            val WingL01c = WingL01b.addOrReplaceChild(
                "WingL01c",
                CubeListBuilder.create().texOffs(0, 53).addBox(-3f, 0f, -6f, 3f, 5f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3f, 0f, 0f, 0f, 0.5236f, 0f)
            )

            val GlowWingR01a2 = GlowBodyMain2.addOrReplaceChild(
                "GlowWingR01a2",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(-6f, 13.5f, -4f, 0f, 0.3491f, -0.5236f)
            )

            val WingR01b = GlowWingR01a2.addOrReplaceChild(
                "WingR01b",
                CubeListBuilder.create().texOffs(25, 39).mirror().addBox(-3f, 0f, 0f, 3f, 5f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.5f, -1.5f, -4f, -0.0873f, 0.0873f, 0f)
            )

            val WingR01c = WingR01b.addOrReplaceChild(
                "WingR01c",
                CubeListBuilder.create().texOffs(0, 53).mirror().addBox(0f, 0f, -6f, 3f, 5f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3f, 0f, 0f, 0f, -0.5236f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 64)
        }
    }
}
