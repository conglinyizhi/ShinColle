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

class ModelMountIsH<T : Entity>(root: ModelPart) : EntityModel<T>(), IGlowableModel {
    private val BodyMain: ModelPart
    private val Neck: ModelPart
    private val Cannon01a: ModelPart
    private val Cannon01b: ModelPart
    private val Body01: ModelPart
    private val Body04: ModelPart
    private val Body05: ModelPart
    private val LegFL01: ModelPart
    private val LegFR01: ModelPart
    private val Head: ModelPart
    private val Jaw: ModelPart
    private val NeckFront: ModelPart
    private val HeadTooth: ModelPart
    private val HeadCannon: ModelPart
    private val TopCannonBase: ModelPart
    private val TopCannon01a: ModelPart
    private val TopCannon01b: ModelPart
    private val TopCannonBase02: ModelPart
    private val TopCannon02a: ModelPart
    private val TopCannon03a: ModelPart
    private val TopCannon04a: ModelPart
    private val TopCannon02b: ModelPart
    private val TopCannon03b: ModelPart
    private val TopCannon04b: ModelPart
    private val JawTooth: ModelPart
    private val Tongue01: ModelPart
    private val Tongue02: ModelPart
    private val Tongue03: ModelPart
    private val Cannon02a: ModelPart
    private val Cannon03a: ModelPart
    private val Cannon02b: ModelPart
    private val Cannon03b: ModelPart
    private val Body02: ModelPart
    private val Body03: ModelPart
    private val LegBR01: ModelPart
    private val LegBL01: ModelPart
    private val LegBR02: ModelPart
    private val LegBR03: ModelPart
    private val LegFR02: ModelPart
    private val LegFR03: ModelPart
    private val LegFL02: ModelPart
    private val LegFL03: ModelPart
    private val LegBL02: ModelPart
    private val LegBL03: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val GlowJaw: ModelPart
    private val GlowTopCannonBase: ModelPart

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Body04 = this.BodyMain.getChild("Body04")
        this.Body01 = this.BodyMain.getChild("Body01")
        this.Body02 = this.Body01.getChild("Body02")
        this.Body03 = this.Body02.getChild("Body03")
        this.LegFL01 = this.BodyMain.getChild("LegFL01")
        this.LegFL02 = this.LegFL01.getChild("LegFL02")
        this.LegFL03 = this.LegFL02.getChild("LegFL03")
        this.LegFR01 = this.BodyMain.getChild("LegFR01")
        this.LegBL02 = this.LegFR01.getChild("LegBL02")
        this.LegBL03 = this.LegBL02.getChild("LegBL03")
        this.Body05 = this.BodyMain.getChild("Body05")
        this.LegBR01 = this.Body05.getChild("LegBR01")
        this.LegBR02 = this.LegBR01.getChild("LegBR02")
        this.LegBR03 = this.LegBR02.getChild("LegBR03")
        this.LegBL01 = this.Body05.getChild("LegBL01")
        this.LegFR02 = this.LegBL01.getChild("LegFR02")
        this.LegFR03 = this.LegFR02.getChild("LegFR03")
        this.Neck = this.BodyMain.getChild("Neck")
        this.Jaw = this.Neck.getChild("Jaw")
        this.Head = this.Neck.getChild("Head")
        this.TopCannonBase = this.Head.getChild("TopCannonBase")
        this.TopCannonBase02 = this.TopCannonBase.getChild("TopCannonBase02")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        this.HeadTooth = this.GlowHead.getChild("HeadTooth")
        this.HeadCannon = this.GlowHead.getChild("HeadCannon")
        this.GlowTopCannonBase = this.GlowHead.getChild("GlowTopCannonBase")
        this.TopCannon01a = this.GlowTopCannonBase.getChild("TopCannon01a")
        this.TopCannon02a = this.TopCannon01a.getChild("TopCannon02a")
        this.TopCannon03a = this.TopCannon01a.getChild("TopCannon03a")
        this.TopCannon04a = this.TopCannon03a.getChild("TopCannon04a")
        this.TopCannon01b = this.GlowTopCannonBase.getChild("TopCannon01b")
        this.TopCannon02b = this.TopCannon01b.getChild("TopCannon02b")
        this.TopCannon03b = this.TopCannon01b.getChild("TopCannon03b")
        this.TopCannon04b = this.TopCannon03b.getChild("TopCannon04b")
        this.GlowJaw = this.GlowNeck.getChild("GlowJaw")
        this.JawTooth = this.GlowJaw.getChild("JawTooth")
        this.Tongue01 = this.GlowJaw.getChild("Tongue01")
        this.Tongue02 = this.Tongue01.getChild("Tongue02")
        this.Tongue03 = this.Tongue02.getChild("Tongue03")
        this.NeckFront = this.GlowNeck.getChild("NeckFront")
        this.Cannon01a = this.GlowBodyMain.getChild("Cannon01a")
        this.Cannon02a = this.Cannon01a.getChild("Cannon02a")
        this.Cannon03a = this.Cannon02a.getChild("Cannon03a")
        this.Cannon01b = this.GlowBodyMain.getChild("Cannon01b")
        this.Cannon02b = this.Cannon01b.getChild("Cannon02b")
        this.Cannon03b = this.Cannon02b.getChild("Cannon03b")
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
        val angleX2 = cos((ageInTicks * 0.5f + 0.3f).toDouble()).toFloat()
        val angleX3 = cos((ageInTicks * 0.5f + 0.6f).toDouble()).toFloat()
        val angleX4 = cos((ageInTicks * 0.5f + 0.9f).toDouble()).toFloat()
        val angleAdd1 = cos((limbSwing * 0.7f).toDouble()).toFloat() * limbSwingAmount * 0.7f
        val angleAdd2 = cos((limbSwing * 0.7f + Math.PI.toFloat()).toDouble()).toFloat() * limbSwingAmount * 0.7f
        val addk1 = angleAdd1 * 0.5f
        val addk2 = angleAdd2 * 0.5f

        var offsetY = 0.0f
        var isSitting = false
        if (entity is EntityMountBase) {
            if (entity.shipDepth > 0.0) {
                offsetY += angleX * 0.025f + 0.025f
            }
            if (entity.host != null && (entity.host!!.isOrderedToSit() || entity.host!!
                    .isInSittingPose())
            ) {
                isSitting = true
            }
        }
        this.BodyMain.y = -8.0f - (offsetY * 16.0f)
        this.GlowBodyMain.y = this.BodyMain.y

        this.LegFL01.xRot = addk1 + 1.04f
        this.LegFR01.xRot = addk2 + 1.04f
        this.LegBL01.xRot = addk1 + 1.04f
        this.LegBR01.xRot = addk2 + 1.04f

        if (isSitting) {
            this.Jaw.xRot = 0.7f
        } else {
            this.Jaw.xRot = angleX * 0.075f + 0.26f
        }
        this.GlowJaw.xRot = this.Jaw.xRot

        this.Tongue01.xRot = angleX2 * 0.05f - 0.38f
        this.Tongue02.xRot = -angleX3 * 0.08f + 0.52f
        this.Tongue03.xRot = -angleX4 * 0.05f + 0.69f

        val headX = headPitch * 0.01f
        val headY = netHeadYaw * 0.01f

        this.HeadCannon.xRot = headX - 1.68f
        this.Cannon03a.xRot = headX - 1.74f
        this.Cannon03b.xRot = headX - 1.7f
        this.TopCannon01a.xRot = headX - 0.2f
        this.TopCannon01b.xRot = headX - 0.2f

        this.TopCannonBase.yRot = headY
        this.GlowTopCannonBase.yRot = headY
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "mount_is_h"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -8f, 5f)
            )

            val Body04 = BodyMain.addOrReplaceChild(
                "Body04",
                CubeListBuilder.create().texOffs(7, 0).addBox(-7.5f, -6f, 0f, 15f, 15f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3f, -3f, -0.3491f, 0f, 0f)
            )

            val Body01 = BodyMain.addOrReplaceChild(
                "Body01",
                CubeListBuilder.create().texOffs(12, 0).addBox(-8.5f, -12f, -6f, 17f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1f, -0.5f, -0.1745f, 0f, 0f)
            )

            val Body02 = Body01.addOrReplaceChild(
                "Body02",
                CubeListBuilder.create().texOffs(6, 3).addBox(-8f, -12f, -6f, 16f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, 7f, -0.2618f, 0f, 0f)
            )

            val Body03 = Body02.addOrReplaceChild(
                "Body03",
                CubeListBuilder.create().texOffs(18, 0).addBox(-7.5f, -12f, -6f, 15f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, 10f, 0.3491f, 0f, 0f)
            )

            val LegFL01 = BodyMain.addOrReplaceChild(
                "LegFL01",
                CubeListBuilder.create().texOffs(34, 7).addBox(0f, -4.5f, -9f, 3f, 9f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(9f, 13f, -19f, 0.8727f, -0.1396f, 0.0524f)
            )

            val LegFL02 = LegFL01.addOrReplaceChild(
                "LegFL02",
                CubeListBuilder.create().texOffs(3, 5).addBox(-1f, 0f, 0f, 2f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.6f, -2f, -8.5f, -0.2618f, 0f, 0f)
            )

            val LegFL03 = LegFL02.addOrReplaceChild(
                "LegFL03",
                CubeListBuilder.create().texOffs(9, 0).addBox(-0.5f, -6f, 0f, 1f, 11f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10.2f, 1f, -1.0472f, 0f, 0f)
            )

            val LegFR01 = BodyMain.addOrReplaceChild(
                "LegFR01",
                CubeListBuilder.create().texOffs(0, 11).mirror()
                    .addBox(-3f, -4.5f, -9f, 3f, 9f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9f, 13f, -19f, 0.8727f, 0.1396f, -0.0524f)
            )

            val LegBL02 = LegFR01.addOrReplaceChild(
                "LegBL02",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1f, 0f, 0f, 2f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.6f, -2f, -8.5f, -0.2618f, 0f, 0f)
            )

            val LegBL03 = LegBL02.addOrReplaceChild(
                "LegBL03",
                CubeListBuilder.create().texOffs(8, 0).mirror()
                    .addBox(-0.5f, -6f, 0f, 1f, 11f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10.2f, 1f, -1.0472f, 0f, 0f)
            )

            val Body05 = BodyMain.addOrReplaceChild(
                "Body05",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.5f, -6f, 0f, 13f, 12f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9.4f, 4.5f, 0.0873f, 0f, 0f)
            )

            val LegBR01 = Body05.addOrReplaceChild(
                "LegBR01",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -4.5f, -9f, 3f, 9f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, 4f, 5f, 1.0472f, 3.002f, -0.0524f)
            )

            val LegBR02 = LegBR01.addOrReplaceChild(
                "LegBR02",
                CubeListBuilder.create().texOffs(0, 17).addBox(-1f, 0f, 0f, 2f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.6f, -2f, -8.5f, -0.2618f, 0f, 0f)
            )

            val LegBR03 = LegBR02.addOrReplaceChild(
                "LegBR03",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, -6f, 0f, 1f, 11f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10.2f, 1f, -1.0472f, 0f, 0f)
            )

            val LegBL01 = Body05.addOrReplaceChild(
                "LegBL01",
                CubeListBuilder.create().texOffs(5, 0).mirror()
                    .addBox(-3f, -4.5f, -9f, 3f, 9f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, 4f, 5f, 1.0472f, -3.002f, 0.0524f)
            )

            val LegFR02 = LegBL01.addOrReplaceChild(
                "LegFR02",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1f, 0f, 0f, 2f, 11f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.6f, -2f, -8.5f, -0.2618f, 0f, 0f)
            )

            val LegFR03 = LegFR02.addOrReplaceChild(
                "LegFR03",
                CubeListBuilder.create().texOffs(19, 0).mirror()
                    .addBox(-0.5f, -6f, 0f, 1f, 11f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 10.2f, 1f, -1.0472f, 0f, 0f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7.5f, -7.5f, -14f, 15f, 15f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0.182f, 0f, 0f)
            )

            val Jaw = Neck.addOrReplaceChild(
                "Jaw",
                CubeListBuilder.create().texOffs(7, 0).addBox(-9.5f, 0f, -15f, 19f, 7f, 19f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4f, -11f, 0.2618f, 0f, 0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(0, 3).addBox(-9.5f, -7f, -22f, 19f, 10f, 24f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -9f, -4f, -0.2094f, 0f, 0f)
            )

            val TopCannonBase = Head.addOrReplaceChild(
                "TopCannonBase",
                CubeListBuilder.create().texOffs(3, 0).addBox(-7.5f, -8f, -8f, 15f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -6f, -5f, -0.0873f, 0f, 0f)
            )

            val TopCannonBase02 = TopCannonBase.addOrReplaceChild(
                "TopCannonBase02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, -6f, 0f, 10f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.5f, -0.7f, -0.0873f, 0f, 0f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -8f, 5f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0.182f, 0f, 0f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -9f, -4f, -0.2094f, 0f, 0f)
            )

            val HeadTooth = GlowHead.addOrReplaceChild(
                "HeadTooth",
                CubeListBuilder.create().texOffs(62, 45).addBox(-9f, 0f, -6.5f, 18f, 4f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.5f, -15f, 0.0524f, 0f, 0f)
            )

            val HeadCannon = GlowHead.addOrReplaceChild(
                "HeadCannon",
                CubeListBuilder.create().texOffs(107, 0).addBox(-1.5f, 0f, -1.5f, 3f, 16f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2.9f, -21f, -1.6581f, 0f, 0f)
            )

            val GlowTopCannonBase = GlowHead.addOrReplaceChild(
                "GlowTopCannonBase",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -6f, -5f, -0.0873f, 0f, 0f)
            )

            val TopCannon01a = GlowTopCannonBase.addOrReplaceChild(
                "TopCannon01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, -1.5f, -6f, 3f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.2f, -4f, -6.7f, -0.3491f, 0f, 0f)
            )

            val TopCannon02a = TopCannon01a.addOrReplaceChild(
                "TopCannon02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, 0f, 0f, 1f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.8f, -7f)
            )

            val TopCannon03a = TopCannon01a.addOrReplaceChild(
                "TopCannon03a",
                CubeListBuilder.create().texOffs(120, 0).addBox(-1f, 0f, -1f, 2f, 18f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5.9f, -1.5708f, 0f, 0f)
            )

            val TopCannon04a = TopCannon03a.addOrReplaceChild(
                "TopCannon04a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 4f, 0f)
            )

            val TopCannon01b = GlowTopCannonBase.addOrReplaceChild(
                "TopCannon01b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, -1.5f, -6f, 3f, 4f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.2f, -4f, -6.7f, -0.3491f, 0f, 0f)
            )

            val TopCannon02b = TopCannon01b.addOrReplaceChild(
                "TopCannon02b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, 0f, 0f, 1f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.8f, -7f)
            )

            val TopCannon03b = TopCannon01b.addOrReplaceChild(
                "TopCannon03b",
                CubeListBuilder.create().texOffs(120, 0).addBox(-1f, 0f, -1f, 2f, 18f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -5.9f, -1.5708f, 0f, 0f)
            )

            val TopCannon04b = TopCannon03b.addOrReplaceChild(
                "TopCannon04b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 4f, 0f)
            )

            val GlowJaw = GlowNeck.addOrReplaceChild(
                "GlowJaw",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 4f, -11f, 0.2618f, 0f, 0f)
            )

            val JawTooth = GlowJaw.addOrReplaceChild(
                "JawTooth",
                CubeListBuilder.create().texOffs(63, 46).addBox(-9f, 0f, -14f, 18f, 3f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1.7f, -0.3f, -0.0873f, -0.0223f, 0f)
            )

            val Tongue01 = GlowJaw.addOrReplaceChild(
                "Tongue01",
                CubeListBuilder.create().texOffs(0, 50).mirror()
                    .addBox(-7f, 0f, -10f, 14f, 4f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3f, 0f, -0.384f, 0.3491f, -0.0524f)
            )

            val Tongue02 = Tongue01.addOrReplaceChild(
                "Tongue02",
                CubeListBuilder.create().texOffs(8, 52).addBox(-6f, -0.7f, -7f, 12f, 3f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1f, -10f, 0.5236f, 0f, 0f)
            )

            val Tongue03 = Tongue02.addOrReplaceChild(
                "Tongue03",
                CubeListBuilder.create().texOffs(0, 51).addBox(-5f, -0.3f, -6f, 10f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.2f, -6.7f, 0.6981f, 0f, 0f)
            )

            val NeckFront = GlowNeck.addOrReplaceChild(
                "NeckFront",
                CubeListBuilder.create().texOffs(46, 39).addBox(-6.5f, 0f, 0f, 13f, 14f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -8.5f, -16f)
            )

            val Cannon01a = GlowBodyMain.addOrReplaceChild(
                "Cannon01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, -2.5f, -2.5f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offset(7f, 2f, -10f)
            )

            val Cannon02a = Cannon01a.addOrReplaceChild(
                "Cannon02a",
                CubeListBuilder.create().texOffs(65, 0).addBox(0f, -4f, -8f, 8f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3f, -0.5f, 2f, 0.2094f, -0.1745f, 0f)
            )

            val Cannon03a = Cannon02a.addOrReplaceChild(
                "Cannon03a",
                CubeListBuilder.create().texOffs(98, 0).addBox(-1f, 0f, -1f, 2f, 9f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, 0f, -7f, -1.7453f, 0f, 0f)
            )

            val Cannon01b = GlowBodyMain.addOrReplaceChild(
                "Cannon01b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7f, -2.5f, -2.5f, 7f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offset(-7f, 2f, -10f)
            )

            val Cannon02b = Cannon01b.addOrReplaceChild(
                "Cannon02b",
                CubeListBuilder.create().texOffs(65, 0).mirror().addBox(-8f, -4f, -8f, 8f, 8f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3f, -0.5f, 2f, 0.2094f, 0.1745f, 0f)
            )

            val Cannon03b = Cannon02b.addOrReplaceChild(
                "Cannon03b",
                CubeListBuilder.create().texOffs(98, 0).addBox(-1f, 0f, -1f, 2f, 9f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, 0f, -7f, -1.7453f, 0f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 64)
        }
    }
}
