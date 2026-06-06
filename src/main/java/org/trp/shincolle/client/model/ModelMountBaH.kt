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

class ModelMountBaH<T : Entity>(root: ModelPart) : EntityModel<T>(), IGlowableModel {
    private val BodyMain: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val Butt: ModelPart
    private val Neck: ModelPart
    private val ChestCannon01a: ModelPart
    private val ChestCannon02a: ModelPart
    private val ChestCannon03a: ModelPart
    private val ChestCannon04a: ModelPart
    private val ChestCannon05a: ModelPart
    private val ChestCannon06: ModelPart
    private val EquipBaseL: ModelPart
    private val EquipBaseR: ModelPart
    private val ArmLeft02: ModelPart
    private val ArmRight02: ModelPart
    private val LegRight01: ModelPart
    private val LegLeft01: ModelPart
    private val LegRight02: ModelPart
    private val LefLeft02: ModelPart
    private val Head: ModelPart
    private val HeadTooth: ModelPart
    private val Jaw: ModelPart
    private val HeadBack01: ModelPart
    private val HeadBack02: ModelPart
    private val HeadBack03: ModelPart
    private val JawTooth: ModelPart
    private val Tongue: ModelPart
    private val ChestCannon01b: ModelPart
    private val ChestCannon02b: ModelPart
    private val ChestCannon03b: ModelPart
    private val ChestCannon04b: ModelPart
    private val ChestCannon05b: ModelPart
    private val EquipL01: ModelPart
    private val EquipL03: ModelPart
    private val EquipL02: ModelPart
    private val EquipCannon01: ModelPart
    private val EquipCannon02: ModelPart
    private val EquipCannon03: ModelPart
    private val ChestCannonL01a: ModelPart
    private val ChestCannonL01b: ModelPart
    private val EquipR01: ModelPart
    private val EquipR03: ModelPart
    private val EquipR02: ModelPart
    private val EquipCannon01_1: ModelPart
    private val EquipCannon02_1: ModelPart
    private val EquipCannon03_1: ModelPart
    private val ChestCannonR01a: ModelPart
    private val ChestCannonR01b: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowNeck: ModelPart
    private val GlowEquipBaseL: ModelPart
    private val GlowEquipBaseR: ModelPart
    private val GlowEquipL01: ModelPart
    private val GlowEquipL02: ModelPart
    private val GlowEquipR01: ModelPart
    private val GlowEquipR02: ModelPart
    private val GlowChestCannonL01a: ModelPart
    private val GlowChestCannonR01a: ModelPart
    private val GlowChestCannon01a: ModelPart
    private val GlowChestCannon02a: ModelPart
    private val GlowChestCannon03a: ModelPart
    private val GlowChestCannon04a: ModelPart
    private val GlowChestCannon05a: ModelPart
    private val GlowEquipL03: ModelPart
    private val GlowEquipR03: ModelPart

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.ChestCannon06 = this.BodyMain.getChild("ChestCannon06")
        this.ChestCannon01a = this.BodyMain.getChild("ChestCannon01a")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegRight01 = this.Butt.getChild("LegRight01")
        this.LegRight02 = this.LegRight01.getChild("LegRight02")
        this.LegLeft01 = this.Butt.getChild("LegLeft01")
        this.LefLeft02 = this.LegLeft01.getChild("LefLeft02")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.ChestCannon04a = this.BodyMain.getChild("ChestCannon04a")
        this.EquipBaseL = this.BodyMain.getChild("EquipBaseL")
        this.EquipL01 = this.EquipBaseL.getChild("EquipL01")
        this.EquipL02 = this.EquipL01.getChild("EquipL02")
        this.EquipL03 = this.EquipBaseL.getChild("EquipL03")
        this.ChestCannonL01a = this.EquipL03.getChild("ChestCannonL01a")
        this.EquipBaseR = this.BodyMain.getChild("EquipBaseR")
        this.EquipR01 = this.EquipBaseR.getChild("EquipR01")
        this.EquipR02 = this.EquipR01.getChild("EquipR02")
        this.EquipR03 = this.EquipBaseR.getChild("EquipR03")
        this.ChestCannonR01a = this.EquipR03.getChild("ChestCannonR01a")
        this.ChestCannon03a = this.BodyMain.getChild("ChestCannon03a")
        this.ChestCannon02a = this.BodyMain.getChild("ChestCannon02a")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.ChestCannon05a = this.BodyMain.getChild("ChestCannon05a")
        this.Neck = this.BodyMain.getChild("Neck")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeck = this.GlowBodyMain.getChild("GlowNeck")
        this.Head = this.GlowNeck.getChild("Head")
        this.HeadTooth = this.Head.getChild("HeadTooth")
        this.HeadBack01 = this.Head.getChild("HeadBack01")
        this.HeadBack02 = this.Head.getChild("HeadBack02")
        this.HeadBack03 = this.Head.getChild("HeadBack03")
        this.Jaw = this.Head.getChild("Jaw")
        this.Tongue = this.Jaw.getChild("Tongue")
        this.JawTooth = this.Jaw.getChild("JawTooth")
        this.GlowEquipBaseL = this.GlowBodyMain.getChild("GlowEquipBaseL")
        this.GlowEquipL01 = this.GlowEquipBaseL.getChild("GlowEquipL01")
        this.GlowEquipL02 = this.GlowEquipL01.getChild("GlowEquipL02")
        this.EquipCannon01 = this.GlowEquipL02.getChild("EquipCannon01")
        this.EquipCannon02 = this.GlowEquipL02.getChild("EquipCannon02")
        this.EquipCannon03 = this.GlowEquipL02.getChild("EquipCannon03")
        this.GlowEquipL03 = this.GlowEquipBaseL.getChild("GlowEquipL03")
        this.GlowChestCannonL01a = this.GlowEquipL03.getChild("GlowChestCannonL01a")
        this.ChestCannonL01b = this.GlowChestCannonL01a.getChild("ChestCannonL01b")
        this.GlowEquipBaseR = this.GlowBodyMain.getChild("GlowEquipBaseR")
        this.GlowEquipR01 = this.GlowEquipBaseR.getChild("GlowEquipR01")
        this.GlowEquipR02 = this.GlowEquipR01.getChild("GlowEquipR02")
        this.EquipCannon01_1 = this.GlowEquipR02.getChild("EquipCannon01_1")
        this.EquipCannon02_1 = this.GlowEquipR02.getChild("EquipCannon02_1")
        this.EquipCannon03_1 = this.GlowEquipR02.getChild("EquipCannon03_1")
        this.GlowEquipR03 = this.GlowEquipBaseR.getChild("GlowEquipR03")
        this.GlowChestCannonR01a = this.GlowEquipR03.getChild("GlowChestCannonR01a")
        this.ChestCannonR01b = this.GlowChestCannonR01a.getChild("ChestCannonR01b")
        this.GlowChestCannon01a = this.GlowBodyMain.getChild("GlowChestCannon01a")
        this.ChestCannon01b = this.GlowChestCannon01a.getChild("ChestCannon01b")
        this.GlowChestCannon02a = this.GlowBodyMain.getChild("GlowChestCannon02a")
        this.ChestCannon02b = this.GlowChestCannon02a.getChild("ChestCannon02b")
        this.GlowChestCannon03a = this.GlowBodyMain.getChild("GlowChestCannon03a")
        this.ChestCannon03b = this.GlowChestCannon03a.getChild("ChestCannon03b")
        this.GlowChestCannon04a = this.GlowBodyMain.getChild("GlowChestCannon04a")
        this.ChestCannon04b = this.GlowChestCannon04a.getChild("ChestCannon04b")
        this.GlowChestCannon05a = this.GlowBodyMain.getChild("GlowChestCannon05a")
        this.ChestCannon05b = this.GlowChestCannon05a.getChild("ChestCannon05b")
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
        val angleFast = cos(ageInTicks.toDouble()).toFloat()
        val angleAdd1 = cos((limbSwing * 0.2f).toDouble()).toFloat() * limbSwingAmount * 0.7f
        val angleAdd2 = cos((limbSwing * 0.2f + Math.PI.toFloat()).toDouble()).toFloat() * limbSwingAmount * 0.7f

        val headX = headPitch * 0.017453292f
        val headY = netHeadYaw * 0.017453292f

        var offsetY = 0.0f
        if (entity is EntityMountBase && entity.shipDepth > 0.0) {
            offsetY += angleX * 0.025f + 0.025f
        }
        this.BodyMain.y = offsetY * 16.0f - 10.0f
        this.GlowBodyMain.y = this.BodyMain.y

        this.HeadBack01.xRot = angleFast * 0.04f + 0.68f
        this.HeadBack02.xRot = angleFast * 0.06f + 0.77f
        this.HeadBack03.xRot = angleFast * 0.05f + 0.6f

        this.Jaw.xRot = angleX * 0.12f + 0.83f

        this.ArmLeft01.xRot = angleAdd2 * 1.2f - 0.7f
        this.ArmRight01.xRot = angleAdd1 * 1.2f - 0.7f

        if (entity is EntityMountBase && entity.getHost() != null && entity.getHost()!!.isOrderedToSit()) {
            this.ArmRight01.xRot = -1.57f
        }

        this.EquipCannon01.xRot = headX * 0.85f
        this.EquipCannon02.xRot = headX * 0.95f
        this.EquipCannon03.xRot = headX * 0.75f
        this.EquipCannon01_1.xRot = headX * 0.95f
        this.EquipCannon02_1.xRot = headX * 1.1f
        this.EquipCannon03_1.xRot = headX * 0.85f

        this.EquipL02.yRot = headY
        this.EquipR02.yRot = headY
        this.GlowEquipL02.yRot = headY
        this.GlowEquipR02.yRot = headY

        this.LegLeft01.xRot = angleAdd1 - 1.6755f
        this.LegRight01.xRot = angleAdd2 - 1.6755f
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "mount_ba_h"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 51).addBox(-15f, -11f, -5f, 30f, 20f, 18f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10f, 0f, 1.0472f, 0f, 0f)
            )

            val ChestCannon06 = BodyMain.addOrReplaceChild(
                "ChestCannon06",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 9f, 10f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4f, -1f, -7f, 0.0911f, 0f, 0f)
            )

            val ChestCannon01a = BodyMain.addOrReplaceChild(
                "ChestCannon01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 7f, 8f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, -8f, -7f, -0.1367f, -0.0911f, 0f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 96).addBox(-11f, 0f, -2.5f, 22f, 18f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2f, 0f, -0.5009f, 0f, 0f)
            )

            val LegRight01 = Butt.addOrReplaceChild(
                "LegRight01",
                CubeListBuilder.create().texOffs(0, 101).addBox(-4.5f, 0f, -4.5f, 9f, 18f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5f, 16f, 7f, -1.6755f, 0.2094f, 0f)
            )

            val LegRight02 = LegRight01.addOrReplaceChild(
                "LegRight02",
                CubeListBuilder.create().texOffs(0, 102).addBox(-4f, 0f, -2f, 8f, 18f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 18f, -2f, 1.7453f, 0f, 0f)
            )

            val LegLeft01 = Butt.addOrReplaceChild(
                "LegLeft01",
                CubeListBuilder.create().texOffs(0, 101).mirror()
                    .addBox(-4.5f, 0f, -4.5f, 9f, 18f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5f, 16f, 7f, -1.6755f, -0.2094f, 0f)
            )

            val LefLeft02 = LegLeft01.addOrReplaceChild(
                "LefLeft02",
                CubeListBuilder.create().texOffs(0, 102).mirror()
                    .addBox(-4f, 0f, -2f, 8f, 18f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 18f, -2f, 1.7453f, 0f, 0f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(0, 92).mirror()
                    .addBox(-1f, -7f, -7f, 14f, 22f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(15f, 1f, 2f, -0.8727f, -0.2094f, 0f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(0, 89).mirror()
                    .addBox(-6.5f, 0f, -13f, 13f, 26f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, 15f, 7f, -0.6981f, 0f, 0f)
            )

            val ChestCannon04a = BodyMain.addOrReplaceChild(
                "ChestCannon04a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 9f, 7f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-10f, -1f, -7f, 0.182f, 0.0911f, 0f)
            )

            val EquipBaseL = BodyMain.addOrReplaceChild(
                "EquipBaseL",
                CubeListBuilder.create().texOffs(0, 0).addBox(-9f, -6f, -8.5f, 18f, 5f, 18f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(20f, -3f, 2f, -0.7741f, 0f, 0.1745f)
            )

            val EquipL01 = EquipBaseL.addOrReplaceChild(
                "EquipL01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7f, 0f, -7f, 14f, 4f, 14f, CubeDeformation(0f)),
                PartPose.offset(0f, -10f, 1f)
            )

            val EquipL02 = EquipL01.addOrReplaceChild(
                "EquipL02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-9f, -9f, -9f, 18f, 9f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1.8f, 0f, -0.2618f, -0.1396f, 0f)
            )

            val EquipL03 = EquipBaseL.addOrReplaceChild(
                "EquipL03",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 5f, 13f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, -6f, -2f, 0f, 0f, -0.3142f)
            )

            val ChestCannonL01a = EquipL03.addOrReplaceChild(
                "ChestCannonL01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 7f, 8f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.5f, 2f, 1f, -0.1367f, -1.4114f, 0f)
            )

            val EquipBaseR = BodyMain.addOrReplaceChild(
                "EquipBaseR",
                CubeListBuilder.create().texOffs(0, 0).addBox(-9f, -6f, -8.5f, 18f, 5f, 18f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-20f, -3f, 2f, -0.7741f, 0f, -0.1745f)
            )

            val EquipR01 = EquipBaseR.addOrReplaceChild(
                "EquipR01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7f, 0f, -7f, 14f, 4f, 14f, CubeDeformation(0f)),
                PartPose.offset(0f, -10f, 1f)
            )

            val EquipR02 = EquipR01.addOrReplaceChild(
                "EquipR02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-9f, -9f, -9f, 18f, 9f, 20f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1.8f, 0f, -0.3142f, 0.1396f, 0f)
            )

            val EquipR03 = EquipBaseR.addOrReplaceChild(
                "EquipR03",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 5f, 13f, 10f, CubeDeformation(0f)),
                PartPose.offset(-7f, -6f, 7f)
            )

            val ChestCannonR01a = EquipR03.addOrReplaceChild(
                "ChestCannonR01a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 7f, 8f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.5f, 2f, 1f, -0.1367f, -1.4114f, 0f)
            )

            val ChestCannon03a = BodyMain.addOrReplaceChild(
                "ChestCannon03a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 12f, 9f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5f, -6.4f, -7f, -0.182f, -0.0202f, 0f)
            )

            val ChestCannon02a = BodyMain.addOrReplaceChild(
                "ChestCannon02a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 7f, 8f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.6f, -2.4f, -7f, 0.0911f, -0.0911f, 0f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(0, 92).addBox(-13f, -7f, -7f, 14f, 22f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-15f, 0f, 2f, -0.8727f, 0.2094f, 0f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(0, 89).addBox(-6.5f, 0f, -13f, 13f, 26f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, 15f, 7f, -0.6981f, 0f, 0f)
            )

            val ChestCannon05a = BodyMain.addOrReplaceChild(
                "ChestCannon05a",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 9f, 7f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-13f, -8f, -6f, -0.0911f, 0.1367f, 0.0221f)
            )

            val Neck = BodyMain.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(0, 0).addBox(-9f, -18f, -6f, 18f, 18f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -8f, 6f, -0.2618f, 0f, 0f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -10f, 0f, 1.0472f, 0f, 0f)
            )

            val GlowNeck = GlowBodyMain.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -8f, 6f, -0.2618f, 0f, 0f)
            )

            val Head = GlowNeck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(0, 20).addBox(-9.5f, -9f, -16f, 19f, 12f, 19f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -12f, -2.6f, -0.8727f, 0f, 0f)
            )

            val HeadTooth = Head.addOrReplaceChild(
                "HeadTooth",
                CubeListBuilder.create().texOffs(68, 91).addBox(-7.5f, 0f, -7.5f, 15f, 4f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 1f, -7f, 0.1745f, 0f, 0f)
            )

            val HeadBack01 = Head.addOrReplaceChild(
                "HeadBack01",
                CubeListBuilder.create().texOffs(45, 6).addBox(-3.5f, -3.5f, 0f, 7f, 7f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6f, -2f, 4f, 0.6829f, 0.4363f, 0f)
            )

            val HeadBack02 = Head.addOrReplaceChild(
                "HeadBack02",
                CubeListBuilder.create().texOffs(45, 6).addBox(-3.5f, -3.5f, 0f, 7f, 7f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3f, 5f, 0.7741f, 0.0873f, 0f)
            )

            val HeadBack03 = Head.addOrReplaceChild(
                "HeadBack03",
                CubeListBuilder.create().texOffs(45, 6).addBox(-3.5f, -3.5f, 0f, 7f, 7f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6f, -3f, 5f, 0.5918f, -0.5009f, 0f)
            )

            val Jaw = Head.addOrReplaceChild(
                "Jaw",
                CubeListBuilder.create().texOffs(77, 25).addBox(-8.5f, 0f, -14.5f, 17f, 9f, 17f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.5f, 0f, 0.8727f, 0f, 0f)
            )

            val Tongue = Jaw.addOrReplaceChild(
                "Tongue",
                CubeListBuilder.create().texOffs(82, 54).addBox(-5f, 0f, -13f, 10f, 2f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -3.1f, 1f)
            )

            val JawTooth = Jaw.addOrReplaceChild(
                "JawTooth",
                CubeListBuilder.create().texOffs(68, 91).addBox(-7.5f, 0f, -13f, 15f, 3f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1.5f, -0.5f, -0.0873f, 0f, 0f)
            )

            val GlowEquipBaseL = GlowBodyMain.addOrReplaceChild(
                "GlowEquipBaseL",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(20f, -3f, 2f, -0.7741f, 0f, 0.1745f)
            )

            val GlowEquipL01 = GlowEquipBaseL.addOrReplaceChild(
                "GlowEquipL01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -10f, 1f)
            )

            val GlowEquipL02 = GlowEquipL01.addOrReplaceChild(
                "GlowEquipL02",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 1.8f, 0f, -0.2618f, -0.1396f, 0f)
            )

            val EquipCannon01 = GlowEquipL02.addOrReplaceChild(
                "EquipCannon01",
                CubeListBuilder.create().texOffs(90, 0).addBox(0f, 0f, -16f, 3f, 3f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, -7.5f, -8f, -0.1367f, -0.0873f, 0f)
            )

            val EquipCannon02 = GlowEquipL02.addOrReplaceChild(
                "EquipCannon02",
                CubeListBuilder.create().texOffs(90, 0).addBox(0f, 0f, -16f, 3f, 3f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.5f, -7.5f, -8f, -0.4554f, 0f, 0f)
            )

            val EquipCannon03 = GlowEquipL02.addOrReplaceChild(
                "EquipCannon03",
                CubeListBuilder.create().texOffs(90, 0).addBox(0f, 0f, -16f, 3f, 3f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7f, -7.5f, -8f, -0.2731f, 0.0873f, 0f)
            )

            val GlowEquipL03 = GlowEquipBaseL.addOrReplaceChild(
                "GlowEquipL03",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(6f, -6f, -2f, 0f, 0f, -0.3142f)
            )

            val GlowChestCannonL01a = GlowEquipL03.addOrReplaceChild(
                "GlowChestCannonL01a",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(5.5f, 2f, 1f, -0.1367f, -1.4114f, 0f)
            )

            val ChestCannonL01b = GlowChestCannonL01a.addOrReplaceChild(
                "ChestCannonL01b",
                CubeListBuilder.create().texOffs(84, 0).addBox(0f, 0f, -9f, 2f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.5f, 2.5f, 1f, 0.1367f, 0.0911f, 0f)
            )

            val GlowEquipBaseR = GlowBodyMain.addOrReplaceChild(
                "GlowEquipBaseR",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(-20f, -3f, 2f, -0.7741f, 0f, -0.1745f)
            )

            val GlowEquipR01 = GlowEquipBaseR.addOrReplaceChild(
                "GlowEquipR01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -10f, 1f)
            )

            val GlowEquipR02 = GlowEquipR01.addOrReplaceChild(
                "GlowEquipR02",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 1.8f, 0f, -0.3142f, 0.1396f, 0f)
            )

            val EquipCannon01_1 = GlowEquipR02.addOrReplaceChild(
                "EquipCannon01_1",
                CubeListBuilder.create().texOffs(90, 0).addBox(0f, 0f, -16f, 3f, 3f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4f, -7.5f, -8f, 0f, -0.0873f, 0f)
            )

            val EquipCannon02_1 = GlowEquipR02.addOrReplaceChild(
                "EquipCannon02_1",
                CubeListBuilder.create().texOffs(90, 0).addBox(0f, 0f, -16f, 3f, 3f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-1.5f, -7.5f, -8f, -0.182f, 0f, 0f)
            )

            val EquipCannon03_1 = GlowEquipR02.addOrReplaceChild(
                "EquipCannon03_1",
                CubeListBuilder.create().texOffs(90, 0).addBox(0f, 0f, -16f, 3f, 3f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7f, -7.5f, -8f, -0.2731f, 0.0873f, 0f)
            )

            val GlowEquipR03 = GlowEquipBaseR.addOrReplaceChild(
                "GlowEquipR03",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(-7f, -6f, 7f)
            )

            val GlowChestCannonR01a = GlowEquipR03.addOrReplaceChild(
                "GlowChestCannonR01a",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(5.5f, 2f, 1f, -0.1367f, -1.4114f, 0f)
            )

            val ChestCannonR01b = GlowChestCannonR01a.addOrReplaceChild(
                "ChestCannonR01b",
                CubeListBuilder.create().texOffs(84, 0).addBox(0f, 0f, -9f, 2f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.5f, 2.5f, 1f, 0.182f, -0.182f, 0f)
            )

            val GlowChestCannon01a = GlowBodyMain.addOrReplaceChild(
                "GlowChestCannon01a",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(6f, -8f, -7f, -0.1367f, -0.0911f, 0f)
            )

            val ChestCannon01b = GlowChestCannon01a.addOrReplaceChild(
                "ChestCannon01b",
                CubeListBuilder.create().texOffs(84, 0).addBox(0f, 0f, -9f, 2f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.5f, 3.5f, 1f, -0.4554f, -0.5463f, 0f)
            )

            val GlowChestCannon02a = GlowBodyMain.addOrReplaceChild(
                "GlowChestCannon02a",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(4.6f, -2.4f, -7f, 0.0911f, -0.0911f, 0f)
            )

            val ChestCannon02b = GlowChestCannon02a.addOrReplaceChild(
                "ChestCannon02b",
                CubeListBuilder.create().texOffs(84, 0).addBox(0f, 0f, -9f, 2f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.5f, 2.5f, 1f, 0.0456f, -0.2731f, 0f)
            )

            val GlowChestCannon03a = GlowBodyMain.addOrReplaceChild(
                "GlowChestCannon03a",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(-5f, -6.4f, -7f, -0.182f, -0.0202f, 0f)
            )

            val ChestCannon03b = GlowChestCannon03a.addOrReplaceChild(
                "ChestCannon03b",
                CubeListBuilder.create().texOffs(84, 0).addBox(0f, 0f, -9f, 2f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.5f, 2.5f, 1.5f, -0.6374f, 0f, 0f)
            )

            val GlowChestCannon04a = GlowBodyMain.addOrReplaceChild(
                "GlowChestCannon04a",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(-10f, -1f, -7f, 0.182f, 0.0911f, 0f)
            )

            val ChestCannon04b = GlowChestCannon04a.addOrReplaceChild(
                "ChestCannon04b",
                CubeListBuilder.create().texOffs(84, 0).addBox(0f, 0f, -9f, 2f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.5f, 2.5f, 1f, -0.2731f, 0.2276f, 0f)
            )

            val GlowChestCannon05a = GlowBodyMain.addOrReplaceChild(
                "GlowChestCannon05a",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(-13f, -8f, -6f, -0.0911f, 0.1367f, 0.0221f)
            )

            val ChestCannon05b = GlowChestCannon05a.addOrReplaceChild(
                "ChestCannon05b",
                CubeListBuilder.create().texOffs(84, 0).addBox(0f, 0f, -9f, 2f, 2f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.5f, 2.5f, 1f, -0.7285f, 0.3389f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
