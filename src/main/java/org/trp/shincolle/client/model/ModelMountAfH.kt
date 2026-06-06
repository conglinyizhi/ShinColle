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

class ModelMountAfH<T : Entity>(root: ModelPart) : EntityModel<T>(), IGlowableModel {
    private val BodyMain: ModelPart
    private val ChestCannon01a: ModelPart
    private val ChestCannon02a: ModelPart
    private val ChestCannon03a: ModelPart
    private val EquipBaseL: ModelPart
    private val EquipBaseR: ModelPart
    private val Neck: ModelPart
    private val EquipL01: ModelPart
    private val EquipL02: ModelPart
    private val EquipCannonPlate: ModelPart
    private val EquipCannon01: ModelPart
    private val EquipCannon02: ModelPart
    private val EquipR01: ModelPart
    private val EquipR02: ModelPart
    private val EquipCannonPlate_1: ModelPart
    private val EquipCannon01_1: ModelPart
    private val EquipCannon02_1: ModelPart
    private val Head: ModelPart
    private val Jaw: ModelPart
    private val HeadBack01: ModelPart
    private val NeckBack: ModelPart
    private val HeadBack03: ModelPart
    private val NeckFront: ModelPart
    private val CannonBase: ModelPart
    private val HeadTooth: ModelPart
    private val HeadTooth2: ModelPart
    private val JawTooth: ModelPart
    private val JawTooth2: ModelPart
    private val Cannon01: ModelPart
    private val Cannon02: ModelPart
    private val Cannon03: ModelPart
    private val Cannon04: ModelPart
    private val Cannon05: ModelPart
    private val Cannon06: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowNeck: ModelPart
    private val GlowJaw: ModelPart
    private val GlowHead: ModelPart
    private val GlowCannonBase: ModelPart
    private val GlowCannon04: ModelPart
    private val GlowEquipBaseL: ModelPart
    private val GlowEquipL01: ModelPart
    private val GlowEquipL02: ModelPart
    private val GlowEquipBaseR: ModelPart
    private val GlowEquipR01: ModelPart

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.ChestCannon01a = this.BodyMain.getChild("ChestCannon01a")
        this.EquipBaseL = this.BodyMain.getChild("EquipBaseL")
        this.EquipL01 = this.EquipBaseL.getChild("EquipL01")
        this.EquipL02 = this.EquipL01.getChild("EquipL02")
        this.ChestCannon03a = this.BodyMain.getChild("ChestCannon03a")
        this.Neck = this.BodyMain.getChild("Neck")
        this.CannonBase = this.Neck.getChild("CannonBase")
        this.Cannon01 = this.CannonBase.getChild("Cannon01")
        this.Cannon02 = this.CannonBase.getChild("Cannon02")
        this.Cannon05 = this.CannonBase.getChild("Cannon05")
        this.Cannon04 = this.CannonBase.getChild("Cannon04")
        this.Cannon03 = this.CannonBase.getChild("Cannon03")
        this.HeadBack03 = this.Neck.getChild("HeadBack03")
        this.NeckBack = this.Neck.getChild("NeckBack")
        this.HeadBack01 = this.Neck.getChild("HeadBack01")
        this.Head = this.Neck.getChild("Head")
        this.Jaw = this.Neck.getChild("Jaw")
        this.EquipBaseR = this.BodyMain.getChild("EquipBaseR")
        this.EquipR01 = this.EquipBaseR.getChild("EquipR01")
        this.EquipR02 = this.EquipR01.getChild("EquipR02")
        this.ChestCannon02a = this.BodyMain.getChild("ChestCannon02a")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.GlowJaw = this.GlowNeck.getChild("GlowJaw")
        this.JawTooth = this.GlowJaw.getChild("JawTooth")
        this.JawTooth2 = this.GlowJaw.getChild("JawTooth2")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        this.HeadTooth = this.GlowHead.getChild("HeadTooth")
        this.HeadTooth2 = this.GlowHead.getChild("HeadTooth2")
        this.NeckFront = this.GlowNeck.getChild("NeckFront")
        this.GlowCannonBase = this.GlowNeck.getChild("GlowCannonBase")
        this.GlowCannon04 = this.GlowCannonBase.getChild("GlowCannon04")
        this.Cannon06 = this.GlowCannon04.getChild("Cannon06")
        this.GlowEquipBaseL = this.GlowBodyMain.getChild("GlowEquipBaseL")
        this.GlowEquipL01 = this.GlowEquipBaseL.getChild("GlowEquipL01")
        this.GlowEquipL02 = this.GlowEquipL01.getChild("GlowEquipL02")
        this.EquipCannonPlate = this.GlowEquipL01.getChild("EquipCannonPlate")
        this.EquipCannon01 = this.EquipCannonPlate.getChild("EquipCannon01")
        this.EquipCannon02 = this.EquipCannonPlate.getChild("EquipCannon02")
        this.GlowEquipBaseR = this.GlowBodyMain.getChild("GlowEquipBaseR")
        this.GlowEquipR01 = this.GlowEquipBaseR.getChild("GlowEquipR01")
        this.EquipCannonPlate_1 = this.GlowEquipR01.getChild("EquipCannonPlate_1")
        this.EquipCannon01_1 = this.EquipCannonPlate_1.getChild("EquipCannon01_1")
        this.EquipCannon02_1 = this.EquipCannonPlate_1.getChild("EquipCannon02_1")
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

        var offsetY = 0.6f
        if (entity is EntityMountBase && entity.shipDepth > 0.0) {
            offsetY = angleX * 0.025f + 0.025f
        }
        this.BodyMain.y = offsetY * 16.0f
        this.GlowBodyMain.y = this.BodyMain.y

        this.Jaw.xRot = angleX * 0.1f + 0.4f
        this.GlowJaw.xRot = this.Jaw.xRot

        this.EquipCannon01.xRot = angleX * 0.08f - 0.32f
        this.EquipCannon02.xRot = -angleX * 0.14f
        this.EquipCannon01_1.xRot = -angleX * 0.12f - 0.18f
        this.EquipCannon02_1.xRot = angleX * 0.08f + 0.18f
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "mount_af_h"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 0).addBox(-9f, -7f, 10f, 18f, 12f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val ChestCannon01a = BodyMain.addOrReplaceChild(
                "ChestCannon01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 5f, 5f, 17f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.3f, 6f, -2f, 0.0873f, 0f, 0f)
            )

            val EquipBaseL = BodyMain.addOrReplaceChild(
                "EquipBaseL",
                CubeListBuilder.create().texOffs(0, 10).addBox(-6f, 0f, -10f, 11f, 6f, 21f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(14.5f, 2f, 5f, 0f, -0.0873f, 0f)
            )

            val EquipL01 = EquipBaseL.addOrReplaceChild(
                "EquipL01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6f, 0f, -7f, 10f, 9f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -8f, 1f, -0.1396f, 0f, 0f)
            )

            val EquipL02 = EquipL01.addOrReplaceChild(
                "EquipL02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.5f, 0f, -9f, 11f, 4f, 23f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3f, 0f, 0.0524f, 0f, 0f)
            )

            val ChestCannon03a = BodyMain.addOrReplaceChild(
                "ChestCannon03a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 5f, 5f, 17f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8.3f, 6f, -2f, 0.0873f, 0f, 0f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(70, 58).addBox(-7.5f, -15f, -3f, 15f, 15f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-29f, 5f, 6f, 0f, 0.2618f, 0f)
            )

            val CannonBase = Neck.addOrReplaceChild(
                "CannonBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, -14f, 0f, 10f, 14f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1f, -16f, 7f, -0.5236f, 0f, 0f)
            )

            val Cannon01 = CannonBase.addOrReplaceChild(
                "Cannon01",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, -10f, 3f, 3f, 10f, CubeDeformation(0f)),
                PartPose.offset(2f, -9f, 0f)
            )

            val Cannon02 = CannonBase.addOrReplaceChild(
                "Cannon02",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, -10f, 3f, 3f, 10f, CubeDeformation(0f)),
                PartPose.offset(-3f, -9f, 0f)
            )

            val Cannon05 = CannonBase.addOrReplaceChild(
                "Cannon05",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, -10f, 2f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -14.6f, 0.5f, -0.0524f, 0f, 0f)
            )

            val Cannon04 = CannonBase.addOrReplaceChild(
                "Cannon04",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, -13f, 4f, 4f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1f, -13.5f, 0f, 0f, 0f, 0.7854f)
            )

            val Cannon03 = CannonBase.addOrReplaceChild(
                "Cannon03",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, -9f, 2f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offset(-3.5f, -11.3f, 0f)
            )

            val HeadBack03 = Neck.addOrReplaceChild(
                "HeadBack03",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 8f, 18f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, -16.1f, 14.5f, 0.0911f, 0f, 0f)
            )

            val NeckBack = Neck.addOrReplaceChild(
                "NeckBack",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 5f, 5f, 5f, CubeDeformation(0f)),
                PartPose.offset(-2f, -6f, 11f)
            )

            val HeadBack01 = Neck.addOrReplaceChild(
                "HeadBack01",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 8f, 8f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, -18f, 8f, -0.1396f, 0f, 0f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(0, 94).addBox(-9.5f, -7f, -22f, 19f, 10f, 24f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -15f, 7f, -0.2094f, 0f, 0f)
            )

            val Jaw = Neck.addOrReplaceChild(
                "Jaw",
                CubeListBuilder.create().texOffs(0, 68).mirror()
                    .addBox(-9.5f, 0f, -15f, 19f, 7f, 19f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3f, 0.5f, 0.5463f, 0f, 0f)
            )

            val EquipBaseR = BodyMain.addOrReplaceChild(
                "EquipBaseR",
                CubeListBuilder.create().texOffs(0, 10).addBox(-6f, 0f, -10f, 11f, 6f, 21f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-13.5f, 2f, 5f, 0f, 0.0873f, 0f)
            )

            val EquipR01 = EquipBaseR.addOrReplaceChild(
                "EquipR01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, 0f, -7f, 10f, 9f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -8f, 1f, -0.1396f, 0f, 0f)
            )

            val EquipR02 = EquipR01.addOrReplaceChild(
                "EquipR02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.5f, 0f, -9f, 11f, 4f, 23f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3f, 0f, 0.0524f, 0f, 0f)
            )

            val ChestCannon02a = BodyMain.addOrReplaceChild(
                "ChestCannon02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 5f, 5f, 17f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.5f, 6f, -2f, 0.0873f, 0f, 0f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 0f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(-29f, 5f, 6f, 0f, 0.2618f, 0f)
            )

            val GlowJaw = GlowNeck.addOrReplaceChild(
                "GlowJaw",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -3f, 0.5f)
            )

            val JawTooth = GlowJaw.addOrReplaceChild(
                "JawTooth",
                CubeListBuilder.create().texOffs(63, 99).addBox(-9f, 0f, -14f, 18f, 3f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1.6f, -0.3f, -0.0873f, -0.0223f, 0f)
            )

            val JawTooth2 = GlowJaw.addOrReplaceChild(
                "JawTooth2",
                CubeListBuilder.create().texOffs(66, 100).mirror()
                    .addBox(-8f, 0f, -13f, 16f, 3f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2.6f, 0f, -0.1396f, 0f, 0f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -15f, 7f, -0.2094f, 0f, 0f)
            )

            val HeadTooth = GlowHead.addOrReplaceChild(
                "HeadTooth",
                CubeListBuilder.create().texOffs(62, 98).addBox(-9f, 0f, -6.5f, 18f, 4f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, -15f, 0.0524f, 0f, 0f)
            )

            val HeadTooth2 = GlowHead.addOrReplaceChild(
                "HeadTooth2",
                CubeListBuilder.create().texOffs(65, 99).mirror()
                    .addBox(-8f, 0f, -14f, 16f, 3f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3.6f, -6.5f, 0.1745f, 0f, 0f)
            )

            val NeckFront = GlowNeck.addOrReplaceChild(
                "NeckFront",
                CubeListBuilder.create().texOffs(0, 52).addBox(-6.5f, 0f, 0f, 13f, 14f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -14f, -5f)
            )

            val GlowCannonBase = GlowNeck.addOrReplaceChild(
                "GlowCannonBase",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(-1f, -16f, 7f, -0.5236f, 0f, 0f)
            )

            val GlowCannon04 = GlowCannonBase.addOrReplaceChild(
                "GlowCannon04",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(1f, -13.5f, 0f, 0f, 0f, 0.7854f)
            )

            val Cannon06 = GlowCannon04.addOrReplaceChild(
                "Cannon06",
                CubeListBuilder.create().texOffs(74, 0).addBox(0f, 0f, -15f, 2f, 2f, 15f, CubeDeformation(0f)),
                PartPose.offset(1f, 1f, -13f)
            )

            val GlowEquipBaseL = GlowBodyMain.addOrReplaceChild(
                "GlowEquipBaseL",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(14.5f, 2f, 5f, 0f, -0.0873f, 0f)
            )

            val GlowEquipL01 = GlowEquipBaseL.addOrReplaceChild(
                "GlowEquipL01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -8f, 1f, -0.1396f, 0f, 0f)
            )

            val GlowEquipL02 = GlowEquipL01.addOrReplaceChild(
                "GlowEquipL02",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -3f, 0f, 0.0524f, 0f, 0f)
            )

            val EquipCannonPlate = GlowEquipL01.addOrReplaceChild(
                "EquipCannonPlate",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 4f, 6f, 1f, CubeDeformation(0f)),
                PartPose.offset(-2f, 2f, -8f)
            )

            val EquipCannon01 = EquipCannonPlate.addOrReplaceChild(
                "EquipCannon01",
                CubeListBuilder.create().texOffs(73, 0).addBox(0f, 0f, -7f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.5f, 1f, 0.5f, -0.3187f, -0.0873f, 0f)
            )

            val EquipCannon02 = EquipCannonPlate.addOrReplaceChild(
                "EquipCannon02",
                CubeListBuilder.create().texOffs(73, 0).addBox(0f, 0f, -7f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.5f, 4f, 0.5f, 0f, -0.0873f, 0f)
            )

            val GlowEquipBaseR = GlowBodyMain.addOrReplaceChild(
                "GlowEquipBaseR",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(-13.5f, 2f, 5f, 0f, 0.0873f, 0f)
            )

            val GlowEquipR01 = GlowEquipBaseR.addOrReplaceChild(
                "GlowEquipR01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -8f, 1f, -0.1396f, 0f, 0f)
            )

            val EquipCannonPlate_1 = GlowEquipR01.addOrReplaceChild(
                "EquipCannonPlate_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 4f, 6f, 1f, CubeDeformation(0f)),
                PartPose.offset(-2f, 2f, -8f)
            )

            val EquipCannon01_1 = EquipCannonPlate_1.addOrReplaceChild(
                "EquipCannon01_1",
                CubeListBuilder.create().texOffs(73, 0).addBox(0f, 0f, -7f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.5f, 1f, 0.5f, -0.182f, 0.1367f, 0f)
            )

            val EquipCannon02_1 = EquipCannonPlate_1.addOrReplaceChild(
                "EquipCannon02_1",
                CubeListBuilder.create().texOffs(73, 0).addBox(0f, 0f, -7f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.5f, 4f, 0.5f, 0.182f, 0.0911f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
