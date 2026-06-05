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

class ModelMountMiH<T : Entity?>(root: ModelPart) : EntityModel<T?>(), IGlowableModel {
    private val BodyMain: ModelPart
    private val UpperMain: ModelPart
    private val LowerMain: ModelPart
    private val LegArmorBase: ModelPart
    private val Back: ModelPart
    private val Head: ModelPart
    private val Back_1: ModelPart
    private val EquipHeadBack1: ModelPart
    private val EquipHeadBack1b: ModelPart
    private val EquipHead01: ModelPart
    private val EquipHead01c: ModelPart
    private val EquipHeadBack2: ModelPart
    private val EquipHead03: ModelPart
    private val EquipHeadBack3: ModelPart
    private val EquipHeadBack3b: ModelPart
    private val EquipHeadBack2b: ModelPart
    private val EquipHead03_1: ModelPart
    private val EquipHead03_2: ModelPart
    private val EquipHead03_3: ModelPart
    private val EquipHeadBack3c: ModelPart
    private val EquipHeadBack3d: ModelPart
    private val EquipHeadBack3e: ModelPart
    private val EquipHeadBack3f: ModelPart
    private val EquipHeadBack3g: ModelPart
    private val EquipHeadBack3h: ModelPart
    private val EquipHead03a: ModelPart
    private val EquipHead01_1: ModelPart
    private val EquipHead02: ModelPart
    private val EquipHead00: ModelPart
    private val EquipHead01a: ModelPart
    private val EquipHead02a: ModelPart
    private val EquipHead00a: ModelPart
    private val EquipHead03a_1: ModelPart
    private val EquipHead03a_2: ModelPart
    private val EquipHead03a_3: ModelPart
    private val EquipHead01a_1: ModelPart
    private val EquipHead01b: ModelPart
    private val EquipHead01d: ModelPart
    private val Back_2: ModelPart
    private val TopCannonBase: ModelPart
    private val TopCannonBase_1: ModelPart
    private val TongueBase1: ModelPart
    private val Head_1: ModelPart
    private val Back_3: ModelPart
    private val EquipHeadBack1_1: ModelPart
    private val EquipHeadBack1b_1: ModelPart
    private val EquipHead01_2: ModelPart
    private val EquipHead01c_1: ModelPart
    private val EquipHeadBack2_1: ModelPart
    private val EquipHead03_4: ModelPart
    private val EquipHeadBack3_1: ModelPart
    private val EquipHeadBack3b_1: ModelPart
    private val EquipHeadBack2b_1: ModelPart
    private val EquipHead03_5: ModelPart
    private val EquipHead03_6: ModelPart
    private val EquipHead03_7: ModelPart
    private val EquipHeadBack3c_1: ModelPart
    private val EquipHeadBack3d_1: ModelPart
    private val EquipHeadBack3e_1: ModelPart
    private val EquipHeadBack3f_1: ModelPart
    private val EquipHeadBack3g_1: ModelPart
    private val EquipHeadBack3h_1: ModelPart
    private val EquipHead03a_4: ModelPart
    private val EquipHead01_3: ModelPart
    private val EquipHead02_1: ModelPart
    private val EquipHead00_1: ModelPart
    private val EquipHead01a_2: ModelPart
    private val EquipHead02a_1: ModelPart
    private val EquipHead00a_1: ModelPart
    private val EquipHead03a_5: ModelPart
    private val EquipHead03a_6: ModelPart
    private val EquipHead03a_7: ModelPart
    private val EquipHead01a_3: ModelPart
    private val EquipHead01b_1: ModelPart
    private val EquipHead01d_1: ModelPart
    private val TopCannon01b: ModelPart
    private val TopCannon01b_1: ModelPart
    private val TopCannon01b_2: ModelPart
    private val TopCannonUnder: ModelPart
    private val TopCannon02b: ModelPart
    private val TopCannon03b: ModelPart
    private val TopCannon04b: ModelPart
    private val TopCannon02b_1: ModelPart
    private val TopCannon03b_1: ModelPart
    private val TopCannon04b_1: ModelPart
    private val TopCannon02b_2: ModelPart
    private val TopCannon03b_2: ModelPart
    private val TopCannon04b_2: ModelPart
    private val TopCannon01b_3: ModelPart
    private val TopCannon01b_4: ModelPart
    private val TopCannon01b_5: ModelPart
    private val TopCannonUnder_1: ModelPart
    private val TopCannon02b_3: ModelPart
    private val TopCannon03b_3: ModelPart
    private val TopCannon04b_3: ModelPart
    private val TopCannon02b_4: ModelPart
    private val TopCannon03b_4: ModelPart
    private val TopCannon04b_4: ModelPart
    private val TopCannon02b_5: ModelPart
    private val TopCannon03b_5: ModelPart
    private val TopCannon04b_5: ModelPart
    private val Tongue01: ModelPart
    private val Tongue01a: ModelPart
    private val TongueBase2: ModelPart
    private val Tongue02: ModelPart
    private val Tongue02a: ModelPart
    private val TongueBase3: ModelPart
    private val Tongue03: ModelPart
    private val Tongue03a: ModelPart
    private val LegArmorA1: ModelPart
    private val LegArmorA2: ModelPart
    private val LegArmorA3: ModelPart
    private val LegArmorA4: ModelPart
    private val LegArmorB1: ModelPart
    private val LegArmorB2: ModelPart
    private val LegArmorB3: ModelPart
    private val LegArmorB4: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowLowerMain: ModelPart
    private val GlowTopCannonBase: ModelPart
    private val GlowTopCannonBase_1: ModelPart

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.UpperMain = this.BodyMain.getChild("UpperMain")
        this.Back = this.UpperMain.getChild("Back")
        this.Back_1 = this.Back.getChild("Back_1")
        this.EquipHead01b = this.Back_1.getChild("EquipHead01b")
        this.EquipHead01d = this.Back_1.getChild("EquipHead01d")
        this.Head = this.Back.getChild("Head")
        this.EquipHead01 = this.Head.getChild("EquipHead01")
        this.EquipHead01a_1 = this.EquipHead01.getChild("EquipHead01a_1")
        this.EquipHeadBack1 = this.Head.getChild("EquipHeadBack1")
        this.EquipHeadBack2 = this.EquipHeadBack1.getChild("EquipHeadBack2")
        this.EquipHead03_1 = this.EquipHeadBack2.getChild("EquipHead03_1")
        this.EquipHead03a_1 = this.EquipHead03_1.getChild("EquipHead03a_1")
        this.EquipHeadBack3g = this.EquipHeadBack2.getChild("EquipHeadBack3g")
        this.EquipHeadBack3b = this.EquipHeadBack2.getChild("EquipHeadBack3b")
        this.EquipHead03 = this.EquipHeadBack2.getChild("EquipHead03")
        this.EquipHead03a = this.EquipHead03.getChild("EquipHead03a")
        this.EquipHeadBack3h = this.EquipHeadBack2.getChild("EquipHeadBack3h")
        this.EquipHeadBack2b = this.EquipHeadBack2.getChild("EquipHeadBack2b")
        this.EquipHeadBack3 = this.EquipHeadBack2.getChild("EquipHeadBack3")
        this.EquipHead02 = this.EquipHeadBack3.getChild("EquipHead02")
        this.EquipHead02a = this.EquipHead02.getChild("EquipHead02a")
        this.EquipHead01_1 = this.EquipHeadBack3.getChild("EquipHead01_1")
        this.EquipHead01a = this.EquipHead01_1.getChild("EquipHead01a")
        this.EquipHead00 = this.EquipHeadBack3.getChild("EquipHead00")
        this.EquipHead00a = this.EquipHead00.getChild("EquipHead00a")
        this.EquipHeadBack3e = this.EquipHeadBack2.getChild("EquipHeadBack3e")
        this.EquipHeadBack3f = this.EquipHeadBack2.getChild("EquipHeadBack3f")
        this.EquipHead03_3 = this.EquipHeadBack2.getChild("EquipHead03_3")
        this.EquipHead03a_3 = this.EquipHead03_3.getChild("EquipHead03a_3")
        this.EquipHeadBack3c = this.EquipHeadBack2.getChild("EquipHeadBack3c")
        this.EquipHeadBack3d = this.EquipHeadBack2.getChild("EquipHeadBack3d")
        this.EquipHead03_2 = this.EquipHeadBack2.getChild("EquipHead03_2")
        this.EquipHead03a_2 = this.EquipHead03_2.getChild("EquipHead03a_2")
        this.EquipHeadBack1b = this.Head.getChild("EquipHeadBack1b")
        this.EquipHead01c = this.Head.getChild("EquipHead01c")
        this.LowerMain = this.BodyMain.getChild("LowerMain")
        this.Back_2 = this.LowerMain.getChild("Back_2")
        this.Head_1 = this.Back_2.getChild("Head_1")
        this.EquipHead01c_1 = this.Head_1.getChild("EquipHead01c_1")
        this.EquipHeadBack1_1 = this.Head_1.getChild("EquipHeadBack1_1")
        this.EquipHeadBack2_1 = this.EquipHeadBack1_1.getChild("EquipHeadBack2_1")
        this.EquipHeadBack3_1 = this.EquipHeadBack2_1.getChild("EquipHeadBack3_1")
        this.EquipHead02_1 = this.EquipHeadBack3_1.getChild("EquipHead02_1")
        this.EquipHead02a_1 = this.EquipHead02_1.getChild("EquipHead02a_1")
        this.EquipHead00_1 = this.EquipHeadBack3_1.getChild("EquipHead00_1")
        this.EquipHead00a_1 = this.EquipHead00_1.getChild("EquipHead00a_1")
        this.EquipHead01_3 = this.EquipHeadBack3_1.getChild("EquipHead01_3")
        this.EquipHead01a_2 = this.EquipHead01_3.getChild("EquipHead01a_2")
        this.EquipHeadBack3g_1 = this.EquipHeadBack2_1.getChild("EquipHeadBack3g_1")
        this.EquipHeadBack3e_1 = this.EquipHeadBack2_1.getChild("EquipHeadBack3e_1")
        this.EquipHead03_6 = this.EquipHeadBack2_1.getChild("EquipHead03_6")
        this.EquipHead03a_6 = this.EquipHead03_6.getChild("EquipHead03a_6")
        this.EquipHead03_7 = this.EquipHeadBack2_1.getChild("EquipHead03_7")
        this.EquipHead03a_7 = this.EquipHead03_7.getChild("EquipHead03a_7")
        this.EquipHead03_5 = this.EquipHeadBack2_1.getChild("EquipHead03_5")
        this.EquipHead03a_5 = this.EquipHead03_5.getChild("EquipHead03a_5")
        this.EquipHeadBack3d_1 = this.EquipHeadBack2_1.getChild("EquipHeadBack3d_1")
        this.EquipHead03_4 = this.EquipHeadBack2_1.getChild("EquipHead03_4")
        this.EquipHead03a_4 = this.EquipHead03_4.getChild("EquipHead03a_4")
        this.EquipHeadBack2b_1 = this.EquipHeadBack2_1.getChild("EquipHeadBack2b_1")
        this.EquipHeadBack3c_1 = this.EquipHeadBack2_1.getChild("EquipHeadBack3c_1")
        this.EquipHeadBack3f_1 = this.EquipHeadBack2_1.getChild("EquipHeadBack3f_1")
        this.EquipHeadBack3h_1 = this.EquipHeadBack2_1.getChild("EquipHeadBack3h_1")
        this.EquipHeadBack3b_1 = this.EquipHeadBack2_1.getChild("EquipHeadBack3b_1")
        this.EquipHead01_2 = this.Head_1.getChild("EquipHead01_2")
        this.EquipHead01a_3 = this.EquipHead01_2.getChild("EquipHead01a_3")
        this.EquipHeadBack1b_1 = this.Head_1.getChild("EquipHeadBack1b_1")
        this.Back_3 = this.Back_2.getChild("Back_3")
        this.EquipHead01b_1 = this.Back_3.getChild("EquipHead01b_1")
        this.EquipHead01d_1 = this.Back_3.getChild("EquipHead01d_1")
        this.TopCannonBase = this.LowerMain.getChild("TopCannonBase")
        this.TopCannonUnder = this.TopCannonBase.getChild("TopCannonUnder")
        this.TopCannonBase_1 = this.LowerMain.getChild("TopCannonBase_1")
        this.TopCannonUnder_1 = this.TopCannonBase_1.getChild("TopCannonUnder_1")
        this.LegArmorBase = this.BodyMain.getChild("LegArmorBase")
        this.LegArmorA4 = this.LegArmorBase.getChild("LegArmorA4")
        this.LegArmorB4 = this.LegArmorA4.getChild("LegArmorB4")
        this.LegArmorA1 = this.LegArmorBase.getChild("LegArmorA1")
        this.LegArmorB1 = this.LegArmorA1.getChild("LegArmorB1")
        this.LegArmorA2 = this.LegArmorBase.getChild("LegArmorA2")
        this.LegArmorB2 = this.LegArmorA2.getChild("LegArmorB2")
        this.LegArmorA3 = this.LegArmorBase.getChild("LegArmorA3")
        this.LegArmorB3 = this.LegArmorA3.getChild("LegArmorB3")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowLowerMain = this.GlowBodyMain.getChild("GlowLowerMain")
        this.TongueBase1 = this.GlowLowerMain.getChild("TongueBase1")
        this.Tongue01a = this.TongueBase1.getChild("Tongue01a")
        this.Tongue01 = this.TongueBase1.getChild("Tongue01")
        this.TongueBase2 = this.TongueBase1.getChild("TongueBase2")
        this.TongueBase3 = this.TongueBase2.getChild("TongueBase3")
        this.Tongue03 = this.TongueBase3.getChild("Tongue03")
        this.Tongue03a = this.TongueBase3.getChild("Tongue03a")
        this.Tongue02a = this.TongueBase2.getChild("Tongue02a")
        this.Tongue02 = this.TongueBase2.getChild("Tongue02")
        this.GlowTopCannonBase = this.GlowLowerMain.getChild("GlowTopCannonBase")
        this.TopCannon01b_2 = this.GlowTopCannonBase.getChild("TopCannon01b_2")
        this.TopCannon02b_2 = this.TopCannon01b_2.getChild("TopCannon02b_2")
        this.TopCannon03b_2 = this.TopCannon01b_2.getChild("TopCannon03b_2")
        this.TopCannon04b_2 = this.TopCannon03b_2.getChild("TopCannon04b_2")
        this.TopCannon01b = this.GlowTopCannonBase.getChild("TopCannon01b")
        this.TopCannon02b = this.TopCannon01b.getChild("TopCannon02b")
        this.TopCannon03b = this.TopCannon01b.getChild("TopCannon03b")
        this.TopCannon04b = this.TopCannon03b.getChild("TopCannon04b")
        this.TopCannon01b_1 = this.GlowTopCannonBase.getChild("TopCannon01b_1")
        this.TopCannon03b_1 = this.TopCannon01b_1.getChild("TopCannon03b_1")
        this.TopCannon04b_1 = this.TopCannon03b_1.getChild("TopCannon04b_1")
        this.TopCannon02b_1 = this.TopCannon01b_1.getChild("TopCannon02b_1")
        this.GlowTopCannonBase_1 = this.GlowLowerMain.getChild("GlowTopCannonBase_1")
        this.TopCannon01b_4 = this.GlowTopCannonBase_1.getChild("TopCannon01b_4")
        this.TopCannon03b_4 = this.TopCannon01b_4.getChild("TopCannon03b_4")
        this.TopCannon04b_4 = this.TopCannon03b_4.getChild("TopCannon04b_4")
        this.TopCannon02b_4 = this.TopCannon01b_4.getChild("TopCannon02b_4")
        this.TopCannon01b_3 = this.GlowTopCannonBase_1.getChild("TopCannon01b_3")
        this.TopCannon03b_3 = this.TopCannon01b_3.getChild("TopCannon03b_3")
        this.TopCannon04b_3 = this.TopCannon03b_3.getChild("TopCannon04b_3")
        this.TopCannon02b_3 = this.TopCannon01b_3.getChild("TopCannon02b_3")
        this.TopCannon01b_5 = this.GlowTopCannonBase_1.getChild("TopCannon01b_5")
        this.TopCannon02b_5 = this.TopCannon01b_5.getChild("TopCannon02b_5")
        this.TopCannon03b_5 = this.TopCannon01b_5.getChild("TopCannon03b_5")
        this.TopCannon04b_5 = this.TopCannon03b_5.getChild("TopCannon04b_5")
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
        if (entity is EntityMountBase && entity.shipDepth > 0.0) {
            offsetY += angleX * 0.015f + 0.025f
        }
        this.BodyMain.y = offsetY * 16.0f - 10.0f
        this.GlowBodyMain.y = this.BodyMain.y

        this.UpperMain.xRot = -0.46f

        this.LegArmorA1.yRot = addk1 + 0.35f
        this.LegArmorA2.yRot = addk1 + 0.0f
        this.LegArmorA3.yRot = addk2 + 0.0f
        this.LegArmorA4.yRot = addk2 - 0.68f

        this.LegArmorA1.y = (angleX * 0.1f + angleAdd1 * 0.3f) * 16.0f + 6.0f
        this.LegArmorA2.y = (-angleX * 0.1f + angleAdd1 * 0.3f) * 16.0f + 6.0f
        this.LegArmorA3.y = (-angleX * 0.1f + angleAdd2 * 0.2f) * 16.0f + 6.0f
        this.LegArmorA4.y = (angleX * 0.1f + angleAdd2 * 0.2f) * 16.0f + 8.0f

        this.TongueBase1.xRot = angleX2 * 0.05f - 0.61f
        this.TongueBase2.xRot = -angleX3 * 0.08f + 0.61f
        this.TongueBase3.xRot = -angleX4 * 0.05f + 0.61f

        val headX = headPitch * 0.014f
        val headY = netHeadYaw * 0.008f

        this.TopCannonBase.yRot = headY
        this.GlowTopCannonBase.yRot = headY
        this.TopCannonBase_1.yRot = headY
        this.GlowTopCannonBase_1.yRot = headY

        this.TopCannon01b.xRot = headX - 0.3f
        this.TopCannon01b_1.xRot = headX - 0.25f
        this.TopCannon01b_2.xRot = headX - 0.35f
        this.TopCannon01b_3.xRot = headX - 0.15f
        this.TopCannon01b_4.xRot = headX - 0.2f
        this.TopCannon01b_5.xRot = headX - 0.1f

        if (entity is EntityMountBase && entity.getHost() != null) {
            if (entity.getHost()!!.isOrderedToSit() || entity.getHost()!!.isInSittingPose()) {
                this.TongueBase1.xRot = angleX2 * 0.025f - 0.41f
                this.TongueBase2.xRot = -angleX3 * 0.04f + 0.41f
                this.TongueBase3.xRot = -angleX4 * 0.025f + 0.71f
                this.UpperMain.xRot = -0.15f
            }
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "mount_mi_h"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -10f, 0f, -0.1396f, 0f, 0f)
            )

            val UpperMain = BodyMain.addOrReplaceChild(
                "UpperMain",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9.5f, 14f, -0.4363f, 0f, 0f)
            )

            val Back = UpperMain.addOrReplaceChild(
                "Back",
                CubeListBuilder.create().texOffs(19, 15).mirror()
                    .addBox(-10f, -14f, 9.5f, 10f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 9f, -10f)
            )

            val Back_1 = Back.addOrReplaceChild(
                "Back_1",
                CubeListBuilder.create().texOffs(19, 15).addBox(0f, -5f, 0f, 10f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, -9f, 9.5f)
            )

            val EquipHead01b = Back_1.addOrReplaceChild(
                "EquipHead01b",
                CubeListBuilder.create().texOffs(16, 17).addBox(-6.5f, 0f, 0f, 13f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(13.6f, -5f, -3.5f, 0f, 0.6109f, 0f)
            )

            val EquipHead01d = Back_1.addOrReplaceChild(
                "EquipHead01d",
                CubeListBuilder.create().texOffs(16, 15).addBox(-6.5f, 0f, 0f, 13f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-13.6f, -5f, -3.5f, 0f, -0.6109f, 0f)
            )

            val Head = Back.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(20, 17).mirror()
                    .addBox(-9f, -8f, -5f, 9f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -12f, 14f, 0.0873f, 0f, 0f)
            )

            val EquipHead01 = Head.addOrReplaceChild(
                "EquipHead01",
                CubeListBuilder.create().texOffs(20, 16).addBox(0f, -8f, -5f, 9f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipHead01a_1 = EquipHead01.addOrReplaceChild(
                "EquipHead01a_1",
                CubeListBuilder.create().texOffs(17, 0).addBox(-6f, 0f, 0f, 12f, 7f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(14f, -8.1f, -6.1f, -0.2094f, -2.5307f, 0.0524f)
            )

            val EquipHeadBack1 = Head.addOrReplaceChild(
                "EquipHeadBack1",
                CubeListBuilder.create().texOffs(13, 1).mirror()
                    .addBox(-9f, -9.5f, -9.2f, 9f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2f, -10.1f, -0.8727f, 0f, 0f)
            )

            val EquipHeadBack2 = EquipHeadBack1.addOrReplaceChild(
                "EquipHeadBack2",
                CubeListBuilder.create().texOffs(9, 0).mirror()
                    .addBox(-10f, -12f, -11f, 10f, 2f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, -2.2f, 0.7854f, 0f, 0f)
            )

            val EquipHead03_1 = EquipHeadBack2.addOrReplaceChild(
                "EquipHead03_1",
                CubeListBuilder.create().texOffs(31, 50).addBox(-6f, 1f, -4f, 10f, 11f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(13.3f, -8.2f, -1.4f, -0.1745f, -1.5708f, -0.1745f)
            )

            val EquipHead03a_1 = EquipHead03_1.addOrReplaceChild(
                "EquipHead03a_1",
                CubeListBuilder.create().texOffs(28, 43).mirror().addBox(-6f, 0f, 0f, 10f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.1f, 10.5f, -3.7f, 0.2443f, 0f, 0f)
            )

            val EquipHeadBack3g = EquipHeadBack2.addOrReplaceChild(
                "EquipHeadBack3g",
                CubeListBuilder.create().texOffs(12, 0).addBox(-8f, 0f, 0f, 8f, 2f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-10f, -12.5f, -10f, 0f, 0f, -0.576f)
            )

            val EquipHeadBack3b = EquipHeadBack2.addOrReplaceChild(
                "EquipHeadBack3b",
                CubeListBuilder.create().texOffs(11, 0).addBox(0f, -4f, -5.5f, 10f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, -13f, 0.4363f, 0f, 0f)
            )

            val EquipHead03 = EquipHeadBack2.addOrReplaceChild(
                "EquipHead03",
                CubeListBuilder.create().texOffs(0, 50).mirror()
                    .addBox(-6f, 1f, -4f, 12f, 10f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(11.2f, -7.9f, -10.6f, -0.384f, -1.2217f, 0f)
            )

            val EquipHead03a = EquipHead03.addOrReplaceChild(
                "EquipHead03a",
                CubeListBuilder.create().texOffs(0, 43).addBox(-6f, 0f, 0f, 12f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.1f, 10.5f, -3.7f, 0.2618f, 0f, 0f)
            )

            val EquipHeadBack3h = EquipHeadBack2.addOrReplaceChild(
                "EquipHeadBack3h",
                CubeListBuilder.create().texOffs(15, 0).addBox(-9f, 0f, 0f, 9f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-10f, -12f, 3.5f, -1.0123f, -0.2443f, -0.5236f)
            )

            val EquipHeadBack2b = EquipHeadBack2.addOrReplaceChild(
                "EquipHeadBack2b",
                CubeListBuilder.create().texOffs(9, 0).addBox(0f, -12f, -11f, 10f, 2f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipHeadBack3 = EquipHeadBack2.addOrReplaceChild(
                "EquipHeadBack3",
                CubeListBuilder.create().texOffs(12, 0).mirror()
                    .addBox(-10f, -4f, -5.5f, 10f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, -13f, 0.4363f, 0f, 0f)
            )

            val EquipHead02 = EquipHeadBack3.addOrReplaceChild(
                "EquipHead02",
                CubeListBuilder.create().texOffs(0, 50).addBox(-6f, 0f, -4f, 12f, 10f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, -1f, -2f, -0.8727f, -0.4363f, 0.2269f)
            )

            val EquipHead02a = EquipHead02.addOrReplaceChild(
                "EquipHead02a",
                CubeListBuilder.create().texOffs(0, 43).addBox(-6f, 0f, 0f, 12f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9.5f, -3.6f, 0.2967f, 0f, 0f)
            )

            val EquipHead01_1 = EquipHeadBack3.addOrReplaceChild(
                "EquipHead01_1",
                CubeListBuilder.create().texOffs(0, 50).addBox(-6f, 0f, -4f, 12f, 10f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8f, -1f, -2f, -0.8727f, 0.4363f, -0.2269f)
            )

            val EquipHead01a = EquipHead01_1.addOrReplaceChild(
                "EquipHead01a",
                CubeListBuilder.create().texOffs(0, 43).addBox(-6f, 0f, 0f, 12f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9.5f, -3.6f, 0.2967f, 0f, 0f)
            )

            val EquipHead00 = EquipHeadBack3.addOrReplaceChild(
                "EquipHead00",
                CubeListBuilder.create().texOffs(0, 50).mirror()
                    .addBox(-6f, 0f, -4f, 12f, 10f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2f, -4f, -0.8029f, 0f, 0f)
            )

            val EquipHead00a = EquipHead00.addOrReplaceChild(
                "EquipHead00a",
                CubeListBuilder.create().texOffs(0, 43).addBox(-6f, 0f, 0f, 12f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9.5f, -3.6f, 0.3142f, 0f, 0f)
            )

            val EquipHeadBack3e = EquipHeadBack2.addOrReplaceChild(
                "EquipHeadBack3e",
                CubeListBuilder.create().texOffs(15, 0).mirror().addBox(0f, 0f, 0f, 9f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(10f, -12f, 3.5f, -1.0123f, 0.2443f, 0.5236f)
            )

            val EquipHeadBack3f = EquipHeadBack2.addOrReplaceChild(
                "EquipHeadBack3f",
                CubeListBuilder.create().texOffs(18, 3).addBox(-3f, -2f, -4f, 6f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-11.2f, -8f, -12.4f, 0.3491f, 0f, -0.5236f)
            )

            val EquipHead03_3 = EquipHeadBack2.addOrReplaceChild(
                "EquipHead03_3",
                CubeListBuilder.create().texOffs(31, 50).mirror()
                    .addBox(-4f, 1f, -4f, 10f, 11f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-13.3f, -8.2f, -1.4f, -0.1745f, 1.5708f, 0.1745f)
            )

            val EquipHead03a_3 = EquipHead03_3.addOrReplaceChild(
                "EquipHead03a_3",
                CubeListBuilder.create().texOffs(28, 43).addBox(-4f, 0f, 0f, 10f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.1f, 10.5f, -3.7f, 0.2443f, 0f, 0f)
            )

            val EquipHeadBack3c = EquipHeadBack2.addOrReplaceChild(
                "EquipHeadBack3c",
                CubeListBuilder.create().texOffs(18, 0).mirror().addBox(-3f, -2f, -4f, 6f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(11.2f, -8f, -12.4f, 0.3491f, 0f, 0.5236f)
            )

            val EquipHeadBack3d = EquipHeadBack2.addOrReplaceChild(
                "EquipHeadBack3d",
                CubeListBuilder.create().texOffs(12, 0).mirror().addBox(0f, 0f, 0f, 8f, 2f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(10f, -12.5f, -10f, 0f, 0f, 0.576f)
            )

            val EquipHead03_2 = EquipHeadBack2.addOrReplaceChild(
                "EquipHead03_2",
                CubeListBuilder.create().texOffs(0, 50).addBox(-6f, 1f, -4f, 12f, 10f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-11.2f, -7.9f, -10.6f, -0.384f, 1.2217f, 0f)
            )

            val EquipHead03a_2 = EquipHead03_2.addOrReplaceChild(
                "EquipHead03a_2",
                CubeListBuilder.create().texOffs(0, 43).addBox(-6f, 0f, 0f, 12f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.1f, 10.5f, -3.7f, 0.2618f, 0f, 0f)
            )

            val EquipHeadBack1b = Head.addOrReplaceChild(
                "EquipHeadBack1b",
                CubeListBuilder.create().texOffs(13, 0).addBox(-8f, -9.5f, -9.4f, 9f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -2f, -10.1f, -0.8727f, 0f, 0f)
            )

            val EquipHead01c = Head.addOrReplaceChild(
                "EquipHead01c",
                CubeListBuilder.create().texOffs(17, 0).mirror().addBox(-6f, 0f, 0f, 12f, 7f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-14f, -8.1f, -6.1f, -0.2094f, 2.5307f, 0.0524f)
            )

            val LowerMain = BodyMain.addOrReplaceChild(
                "LowerMain",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8f, 14f, 0.2618f, 0f, 0f)
            )

            val Back_2 = LowerMain.addOrReplaceChild(
                "Back_2",
                CubeListBuilder.create().texOffs(16, 15).mirror()
                    .addBox(-10f, -17f, 9.5f, 10f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -11f, -11f, 0f, 0f, Math.PI.toFloat())
            )

            val Head_1 = Back_2.addOrReplaceChild(
                "Head_1",
                CubeListBuilder.create().texOffs(19, 16).addBox(-9f, -8f, -5f, 9f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -12f, 14f, 0.0873f, 0f, 0f)
            )

            val EquipHead01c_1 = Head_1.addOrReplaceChild(
                "EquipHead01c_1",
                CubeListBuilder.create().texOffs(17, 17).addBox(-6f, 0f, 0f, 12f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-11.8f, -8.1f, -8.9f, 0.2094f, -0.6109f, 0.0524f)
            )

            val EquipHeadBack1_1 = Head_1.addOrReplaceChild(
                "EquipHeadBack1_1",
                CubeListBuilder.create().texOffs(13, 3).mirror()
                    .addBox(-9f, -10.5f, -9.2f, 9f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2f, -10.1f, -0.8727f, 0f, 0f)
            )

            val EquipHeadBack2_1 = EquipHeadBack1_1.addOrReplaceChild(
                "EquipHeadBack2_1",
                CubeListBuilder.create().texOffs(9, 0).mirror()
                    .addBox(-10f, -12f, -11f, 10f, 2f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, -2.2f, 0.7854f, 0f, 0f)
            )

            val EquipHeadBack3_1 = EquipHeadBack2_1.addOrReplaceChild(
                "EquipHeadBack3_1",
                CubeListBuilder.create().texOffs(11, 0).mirror()
                    .addBox(-10f, -4f, -5.5f, 10f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, -13f, 0.4363f, 0f, 0f)
            )

            val EquipHead02_1 = EquipHeadBack3_1.addOrReplaceChild(
                "EquipHead02_1",
                CubeListBuilder.create().texOffs(0, 50).addBox(-6f, 0f, -4f, 12f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(8f, -1f, -2f, -0.8727f, -0.4363f, 0.2269f)
            )

            val EquipHead02a_1 = EquipHead02_1.addOrReplaceChild(
                "EquipHead02a_1",
                CubeListBuilder.create().texOffs(0, 43).addBox(-6f, 0f, 0f, 12f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.5f, -3.6f, 0.2967f, 0f, 0f)
            )

            val EquipHead00_1 = EquipHeadBack3_1.addOrReplaceChild(
                "EquipHead00_1",
                CubeListBuilder.create().texOffs(0, 50).mirror().addBox(-6f, 0f, -4f, 12f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2f, -4f, -0.8029f, 0f, 0f)
            )

            val EquipHead00a_1 = EquipHead00_1.addOrReplaceChild(
                "EquipHead00a_1",
                CubeListBuilder.create().texOffs(0, 43).addBox(-6f, 0f, 0f, 12f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.5f, -3.6f, 0.192f, 0f, 0f)
            )

            val EquipHead01_3 = EquipHeadBack3_1.addOrReplaceChild(
                "EquipHead01_3",
                CubeListBuilder.create().texOffs(0, 50).mirror().addBox(-6f, 0f, -4f, 12f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8f, -1f, -2f, -0.8727f, 0.4363f, -0.2269f)
            )

            val EquipHead01a_2 = EquipHead01_3.addOrReplaceChild(
                "EquipHead01a_2",
                CubeListBuilder.create().texOffs(0, 43).addBox(-6f, 0f, 0f, 12f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.5f, -3.6f, 0.2967f, 0f, 0f)
            )

            val EquipHeadBack3g_1 = EquipHeadBack2_1.addOrReplaceChild(
                "EquipHeadBack3g_1",
                CubeListBuilder.create().texOffs(12, 0).addBox(-8f, 0f, 0f, 8f, 2f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-10f, -12.5f, -10f, 0f, 0f, -0.576f)
            )

            val EquipHeadBack3e_1 = EquipHeadBack2_1.addOrReplaceChild(
                "EquipHeadBack3e_1",
                CubeListBuilder.create().texOffs(15, 0).mirror().addBox(0f, 0f, 0f, 9f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(10f, -12f, 3.5f, -1.0123f, 0.2443f, 0.5236f)
            )

            val EquipHead03_6 = EquipHeadBack2_1.addOrReplaceChild(
                "EquipHead03_6",
                CubeListBuilder.create().texOffs(0, 50).addBox(-6f, 1f, -4f, 12f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-11.2f, -7.9f, -10.6f, -0.384f, 1.2217f, 0f)
            )

            val EquipHead03a_6 = EquipHead03_6.addOrReplaceChild(
                "EquipHead03a_6",
                CubeListBuilder.create().texOffs(0, 43).addBox(-6f, 0f, 0f, 11f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 6.2f, -3.7f)
            )

            val EquipHead03_7 = EquipHeadBack2_1.addOrReplaceChild(
                "EquipHead03_7",
                CubeListBuilder.create().texOffs(31, 50).mirror()
                    .addBox(-4f, 1f, -4f, 10f, 7f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-13.3f, -8.2f, -1.4f, -0.1745f, 1.5708f, 0.1745f)
            )

            val EquipHead03a_7 = EquipHead03_7.addOrReplaceChild(
                "EquipHead03a_7",
                CubeListBuilder.create().texOffs(28, 43).addBox(-4f, 0f, 0f, 10f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.1f, 6.5f, -3.7f, 0.2443f, 0f, 0f)
            )

            val EquipHead03_5 = EquipHeadBack2_1.addOrReplaceChild(
                "EquipHead03_5",
                CubeListBuilder.create().texOffs(31, 50).addBox(-6f, 1f, -4f, 10f, 7f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(13.3f, -8.2f, -1.4f, -0.1745f, -1.5708f, -0.1745f)
            )

            val EquipHead03a_5 = EquipHead03_5.addOrReplaceChild(
                "EquipHead03a_5",
                CubeListBuilder.create().texOffs(28, 43).mirror().addBox(-6f, 0f, 0f, 10f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.1f, 6.5f, -3.7f, 0.2443f, 0f, 0f)
            )

            val EquipHeadBack3d_1 = EquipHeadBack2_1.addOrReplaceChild(
                "EquipHeadBack3d_1",
                CubeListBuilder.create().texOffs(12, 0).mirror().addBox(0f, 0f, 0f, 8f, 2f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(10f, -12.5f, -10f, 0f, 0f, 0.576f)
            )

            val EquipHead03_4 = EquipHeadBack2_1.addOrReplaceChild(
                "EquipHead03_4",
                CubeListBuilder.create().texOffs(0, 50).mirror().addBox(-6f, 1f, -4f, 12f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(11.2f, -7.9f, -10.6f, -0.384f, -1.2217f, 0f)
            )

            val EquipHead03a_4 = EquipHead03_4.addOrReplaceChild(
                "EquipHead03a_4",
                CubeListBuilder.create().texOffs(0, 43).addBox(-6f, 0f, 0f, 11f, 5f, 2f, CubeDeformation(0f)),
                PartPose.offset(1f, 6.3f, -3.7f)
            )

            val EquipHeadBack2b_1 = EquipHeadBack2_1.addOrReplaceChild(
                "EquipHeadBack2b_1",
                CubeListBuilder.create().texOffs(9, 0).addBox(0f, -12f, -11f, 10f, 2f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipHeadBack3c_1 = EquipHeadBack2_1.addOrReplaceChild(
                "EquipHeadBack3c_1",
                CubeListBuilder.create().texOffs(18, 2).mirror().addBox(-3f, -2f, -4f, 6f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(11.2f, -8f, -12.4f, 0.3491f, 0f, 0.5236f)
            )

            val EquipHeadBack3f_1 = EquipHeadBack2_1.addOrReplaceChild(
                "EquipHeadBack3f_1",
                CubeListBuilder.create().texOffs(18, 2).addBox(-3f, -2f, -4f, 6f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-11.2f, -8f, -12.4f, 0.3491f, 0f, -0.5236f)
            )

            val EquipHeadBack3h_1 = EquipHeadBack2_1.addOrReplaceChild(
                "EquipHeadBack3h_1",
                CubeListBuilder.create().texOffs(15, 0).addBox(-9f, 0f, 0f, 9f, 2f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-10f, -12f, 3.5f, -1.0123f, -0.2443f, -0.5236f)
            )

            val EquipHeadBack3b_1 = EquipHeadBack2_1.addOrReplaceChild(
                "EquipHeadBack3b_1",
                CubeListBuilder.create().texOffs(11, 0).addBox(0f, -4f, -5.5f, 10f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, -13f, 0.4363f, 0f, 0f)
            )

            val EquipHead01_2 = Head_1.addOrReplaceChild(
                "EquipHead01_2",
                CubeListBuilder.create().texOffs(19, 18).addBox(0f, -8f, -5f, 9f, 4f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val EquipHead01a_3 = EquipHead01_2.addOrReplaceChild(
                "EquipHead01a_3",
                CubeListBuilder.create().texOffs(17, 17).mirror().addBox(-6f, 0f, 0f, 12f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(11.7f, -8.1f, -8.9f, 0.2094f, 0.6109f, -0.0524f)
            )

            val EquipHeadBack1b_1 = Head_1.addOrReplaceChild(
                "EquipHeadBack1b_1",
                CubeListBuilder.create().texOffs(13, 3).addBox(-8f, -10.5f, -9.4f, 9f, 2f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7.8f, -2f, -10.1f, -0.8727f, 0f, 0f)
            )

            val Back_3 = Back_2.addOrReplaceChild(
                "Back_3",
                CubeListBuilder.create().texOffs(16, 15).addBox(0f, -5f, 0f, 10f, 7f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, -12f, 9.5f)
            )

            val EquipHead01b_1 = Back_3.addOrReplaceChild(
                "EquipHead01b_1",
                CubeListBuilder.create().texOffs(16, 15).mirror()
                    .addBox(-6.5f, 0f, 0f, 13f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(12.7f, -5f, -3.3f, 0f, 0.6109f, 0f)
            )

            val EquipHead01d_1 = Back_3.addOrReplaceChild(
                "EquipHead01d_1",
                CubeListBuilder.create().texOffs(16, 15).addBox(-6.5f, 0f, 0f, 13f, 6f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-12.7f, -5f, -3.3f, 0f, -0.6981f, 0f)
            )

            val TopCannonBase = LowerMain.addOrReplaceChild(
                "TopCannonBase",
                CubeListBuilder.create().texOffs(32, 26).addBox(-5f, -6f, -3f, 10f, 5f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(13f, 1f, -15f, -0.1396f, -0.1745f, 0.0873f)
            )

            val TopCannonUnder = TopCannonBase.addOrReplaceChild(
                "TopCannonUnder",
                CubeListBuilder.create().texOffs(44, 27).addBox(-2f, 0f, -4f, 4f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, 2f)
            )

            val TopCannonBase_1 = LowerMain.addOrReplaceChild(
                "TopCannonBase_1",
                CubeListBuilder.create().texOffs(32, 26).addBox(-5f, -6f, -3f, 10f, 5f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-13f, 1f, -15f, -0.1396f, 0.2618f, -0.0873f)
            )

            val TopCannonUnder_1 = TopCannonBase_1.addOrReplaceChild(
                "TopCannonUnder_1",
                CubeListBuilder.create().texOffs(38, 28).addBox(-2f, 0f, -4f, 4f, 3f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, 2f)
            )

            val LegArmorBase = BodyMain.addOrReplaceChild(
                "LegArmorBase",
                CubeListBuilder.create().texOffs(32, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, 20f, 6f)
            )

            val LegArmorA4 = LegArmorBase.addOrReplaceChild(
                "LegArmorA4",
                CubeListBuilder.create().texOffs(0, 4).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8f, 11f, 1.3963f, -0.6829f, -0.3142f)
            )

            val LegArmorB4 = LegArmorA4.addOrReplaceChild(
                "LegArmorB4",
                CubeListBuilder.create().texOffs(21, 2).mirror()
                    .addBox(-4.5f, -4.5f, -1f, 9f, 9f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-8f, 3f, 0f, 0.5236f, 0.7854f, -0.2793f)
            )

            val LegArmorA1 = LegArmorBase.addOrReplaceChild(
                "LegArmorA1",
                CubeListBuilder.create().texOffs(0, 4).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, -6f, -1.2217f, 0.3491f, 0.3142f)
            )

            val LegArmorB1 = LegArmorA1.addOrReplaceChild(
                "LegArmorB1",
                CubeListBuilder.create().texOffs(21, 15).addBox(-4.5f, -4.5f, -1f, 9f, 9f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(20f, -4.5f, 0f, -0.1396f, 0.6109f, -0.1396f)
            )

            val LegArmorA2 = LegArmorBase.addOrReplaceChild(
                "LegArmorA2",
                CubeListBuilder.create().texOffs(0, 4).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 6f, -10f, -1.2217f, 0f, -0.3142f)
            )

            val LegArmorB2 = LegArmorA2.addOrReplaceChild(
                "LegArmorB2",
                CubeListBuilder.create().texOffs(21, 15).mirror()
                    .addBox(-4.5f, -4.5f, -1f, 9f, 9f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-18f, -4.5f, 0f, -0.1396f, -0.6981f, 0.1396f)
            )

            val LegArmorA3 = LegArmorBase.addOrReplaceChild(
                "LegArmorA3",
                CubeListBuilder.create().texOffs(0, 4).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3f, 6f, 10f, -1.2217f, 0f, 0.3142f)
            )

            val LegArmorB3 = LegArmorA3.addOrReplaceChild(
                "LegArmorB3",
                CubeListBuilder.create().texOffs(21, 15).addBox(-4.5f, -4.5f, -1f, 9f, 9f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(12f, 0f, 0f, -0.0524f, 0.5236f, -0.2793f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -10f, 0f, -0.1396f, 0f, 0f)
            )

            val GlowLowerMain = GlowBodyMain.addOrReplaceChild(
                "GlowLowerMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 8f, 14f, 0.2618f, 0f, 0f)
            )

            val TongueBase1 = GlowLowerMain.addOrReplaceChild(
                "TongueBase1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3.5f, -17f, -0.6109f, 0.2618f, -0.0524f)
            )

            val Tongue01a = TongueBase1.addOrReplaceChild(
                "Tongue01a",
                CubeListBuilder.create().texOffs(0, 29).addBox(-10f, -2f, -10f, 10f, 4f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.3f, 0f, 0f, 0f, 0f, -0.1047f)
            )

            val Tongue01 = TongueBase1.addOrReplaceChild(
                "Tongue01",
                CubeListBuilder.create().texOffs(0, 29).addBox(0f, -2f, -10f, 10f, 4f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.3f, 0f, 0f, 0f, 0f, 0.1047f)
            )

            val TongueBase2 = TongueBase1.addOrReplaceChild(
                "TongueBase2",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.5f, -9f, 0.6109f, 0f, 0f)
            )

            val TongueBase3 = TongueBase2.addOrReplaceChild(
                "TongueBase3",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 0f, 0f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0.2f, -6.5f, 0.6109f, 0f, 0f)
            )

            val Tongue03 = TongueBase3.addOrReplaceChild(
                "Tongue03",
                CubeListBuilder.create().texOffs(8, 29).addBox(0f, -2f, -8f, 8f, 3f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.3f, 0.2f, 0f, 0f, 0.0524f, 0.1396f)
            )

            val Tongue03a = TongueBase3.addOrReplaceChild(
                "Tongue03a",
                CubeListBuilder.create().texOffs(6, 29).addBox(-8f, -2f, -8f, 8f, 3f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.3f, 0f, 0f, 0f, -0.0524f, -0.1396f)
            )

            val Tongue02a = TongueBase2.addOrReplaceChild(
                "Tongue02a",
                CubeListBuilder.create().texOffs(0, 31).addBox(-9f, -2f, -8f, 9f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.3f, 0f, 0f, 0f, 0f, -0.1047f)
            )

            val Tongue02 = TongueBase2.addOrReplaceChild(
                "Tongue02",
                CubeListBuilder.create().texOffs(4, 30).addBox(0f, -2f, -8f, 9f, 4f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.3f, 0f, 0f, 0f, 0f, 0.1047f)
            )

            val GlowTopCannonBase = GlowLowerMain.addOrReplaceChild(
                "GlowTopCannonBase",
                CubeListBuilder.create().texOffs(32, 26),
                PartPose.offsetAndRotation(13f, 1f, -15f, -0.1396f, -0.1745f, 0.0873f)
            )

            val TopCannon01b_2 = GlowTopCannonBase.addOrReplaceChild(
                "TopCannon01b_2",
                CubeListBuilder.create().texOffs(43, 26).addBox(-1f, -1.2f, -4f, 2f, 3f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3f, -3.5f, -2f, -0.3187f, 0f, 0f)
            )

            val TopCannon02b_2 = TopCannon01b_2.addOrReplaceChild(
                "TopCannon02b_2",
                CubeListBuilder.create().texOffs(45, 26).addBox(-0.5f, 0f, 0f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.2f, -7f)
            )

            val TopCannon03b_2 = TopCannon01b_2.addOrReplaceChild(
                "TopCannon03b_2",
                CubeListBuilder.create().texOffs(60, 52).addBox(-0.5f, 0f, -0.5f, 1f, 10f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.3f, -3.9f, -1.5708f, 0f, 0f)
            )

            val TopCannon04b_2 = TopCannon03b_2.addOrReplaceChild(
                "TopCannon04b_2",
                CubeListBuilder.create().texOffs(56, 26).addBox(-1f, 0f, -1f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 2f, 0f)
            )

            val TopCannon01b = GlowTopCannonBase.addOrReplaceChild(
                "TopCannon01b",
                CubeListBuilder.create().texOffs(37, 30).addBox(-1f, -1.2f, -4f, 2f, 3f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3f, -3.5f, -2f, -0.3187f, 0f, 0f)
            )

            val TopCannon02b = TopCannon01b.addOrReplaceChild(
                "TopCannon02b",
                CubeListBuilder.create().texOffs(42, 27).addBox(-0.5f, 0f, 0f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.2f, -7f)
            )

            val TopCannon03b = TopCannon01b.addOrReplaceChild(
                "TopCannon03b",
                CubeListBuilder.create().texOffs(60, 52).addBox(-0.5f, 0f, -0.5f, 1f, 10f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.3f, -3.9f, -1.5708f, 0f, 0f)
            )

            val TopCannon04b = TopCannon03b.addOrReplaceChild(
                "TopCannon04b",
                CubeListBuilder.create().texOffs(56, 28).addBox(-1f, 0f, -1f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 2f, 0f)
            )

            val TopCannon01b_1 = GlowTopCannonBase.addOrReplaceChild(
                "TopCannon01b_1",
                CubeListBuilder.create().texOffs(46, 30).addBox(-1f, -1.2f, -4f, 2f, 3f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3.5f, -2f, -0.3187f, 0f, 0f)
            )

            val TopCannon03b_1 = TopCannon01b_1.addOrReplaceChild(
                "TopCannon03b_1",
                CubeListBuilder.create().texOffs(60, 52).addBox(-0.5f, 0f, -0.5f, 1f, 10f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.3f, -3.9f, -1.5708f, 0f, 0f)
            )

            val TopCannon04b_1 = TopCannon03b_1.addOrReplaceChild(
                "TopCannon04b_1",
                CubeListBuilder.create().texOffs(47, 26).addBox(-1f, 0f, -1f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 2f, 0f)
            )

            val TopCannon02b_1 = TopCannon01b_1.addOrReplaceChild(
                "TopCannon02b_1",
                CubeListBuilder.create().texOffs(35, 26).addBox(-0.5f, 0f, 0f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.2f, -7f)
            )

            val GlowTopCannonBase_1 = GlowLowerMain.addOrReplaceChild(
                "GlowTopCannonBase_1",
                CubeListBuilder.create().texOffs(32, 26),
                PartPose.offsetAndRotation(-13f, 1f, -15f, -0.1396f, 0.2618f, -0.0873f)
            )

            val TopCannon01b_4 = GlowTopCannonBase_1.addOrReplaceChild(
                "TopCannon01b_4",
                CubeListBuilder.create().texOffs(52, 28).addBox(-1f, -1.2f, -4f, 2f, 3f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3.5f, -2f, -0.3187f, 0f, 0f)
            )

            val TopCannon03b_4 = TopCannon01b_4.addOrReplaceChild(
                "TopCannon03b_4",
                CubeListBuilder.create().texOffs(60, 52).addBox(-0.5f, 0f, -0.5f, 1f, 10f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.3f, -3.9f, -1.5708f, 0f, 0f)
            )

            val TopCannon04b_4 = TopCannon03b_4.addOrReplaceChild(
                "TopCannon04b_4",
                CubeListBuilder.create().texOffs(33, 26).addBox(-1f, 0f, -1f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 2f, 0f)
            )

            val TopCannon02b_4 = TopCannon01b_4.addOrReplaceChild(
                "TopCannon02b_4",
                CubeListBuilder.create().texOffs(37, 27).addBox(-0.5f, 0f, 0f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.2f, -7f)
            )

            val TopCannon01b_3 = GlowTopCannonBase_1.addOrReplaceChild(
                "TopCannon01b_3",
                CubeListBuilder.create().texOffs(35, 28).addBox(-1f, -1.2f, -4f, 2f, 3f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3f, -3.5f, -2f, -0.3187f, 0f, 0f)
            )

            val TopCannon03b_3 = TopCannon01b_3.addOrReplaceChild(
                "TopCannon03b_3",
                CubeListBuilder.create().texOffs(60, 52).addBox(-0.5f, 0f, -0.5f, 1f, 10f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.3f, -3.9f, -1.5708f, 0f, 0f)
            )

            val TopCannon04b_3 = TopCannon03b_3.addOrReplaceChild(
                "TopCannon04b_3",
                CubeListBuilder.create().texOffs(33, 28).addBox(-1f, 0f, -1f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 2f, 0f)
            )

            val TopCannon02b_3 = TopCannon01b_3.addOrReplaceChild(
                "TopCannon02b_3",
                CubeListBuilder.create().texOffs(37, 27).addBox(-0.5f, 0f, 0f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.2f, -7f)
            )

            val TopCannon01b_5 = GlowTopCannonBase_1.addOrReplaceChild(
                "TopCannon01b_5",
                CubeListBuilder.create().texOffs(46, 27).addBox(-1f, -1.2f, -4f, 2f, 3f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3f, -3.5f, -2f, -0.3187f, 0f, 0f)
            )

            val TopCannon02b_5 = TopCannon01b_5.addOrReplaceChild(
                "TopCannon02b_5",
                CubeListBuilder.create().texOffs(42, 27).addBox(-0.5f, 0f, 0f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.2f, -7f)
            )

            val TopCannon03b_5 = TopCannon01b_5.addOrReplaceChild(
                "TopCannon03b_5",
                CubeListBuilder.create().texOffs(60, 52).addBox(-0.5f, 0f, -0.5f, 1f, 10f, 1f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.3f, -3.9f, -1.5708f, 0f, 0f)
            )

            val TopCannon04b_5 = TopCannon03b_5.addOrReplaceChild(
                "TopCannon04b_5",
                CubeListBuilder.create().texOffs(40, 27).addBox(-1f, 0f, -1f, 2f, 2f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 2f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}
