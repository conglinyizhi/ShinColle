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

class ModelMountHbH<T : Entity>(root: ModelPart) : EntityModel<T>(), IGlowableModel {
    private val BodyMain: ModelPart
    private val EquipBaseR: ModelPart
    private val Back01: ModelPart
    private val Back02: ModelPart
    private val EquipBaseL: ModelPart
    private val EquipR01: ModelPart
    private val Back01b: ModelPart
    private val Back02b: ModelPart
    private val Back02c: ModelPart
    private val Back02d: ModelPart
    private val Back02e: ModelPart
    private val Neck: ModelPart
    private val Head: ModelPart
    private val Jaw: ModelPart
    private val HeadTooth: ModelPart
    private val Road01: ModelPart
    private val Road02: ModelPart
    private val Road03: ModelPart
    private val Road04: ModelPart
    private val Road05: ModelPart
    private val JawTooth: ModelPart
    private val EquipL01: ModelPart
    private val EquipCannonPlate: ModelPart
    private val CanonBase: ModelPart
    private val EquipCannon01: ModelPart
    private val Neck_1: ModelPart
    private val Head_1: ModelPart
    private val Jaw_1: ModelPart
    private val Road01u: ModelPart
    private val Road01v: ModelPart
    private val HeadTooth_1: ModelPart
    private val JawTooth_1: ModelPart
    private val Road02u: ModelPart
    private val Road03u: ModelPart
    private val Road02v: ModelPart
    private val Road03v: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowEquipBaseL: ModelPart
    private val GlowEquipL01: ModelPart
    private val GlowEquipCannonPlate: ModelPart
    private val GlowBack02: ModelPart
    private val GlowBack02b: ModelPart
    private val GlowBack02c: ModelPart
    private val GlowBack02d: ModelPart
    private val GlowBack02e: ModelPart
    private val GlowNeck: ModelPart
    private val GlowHead: ModelPart
    private val GlowJaw: ModelPart
    private val GlowCanonBase: ModelPart
    private val GlowNeck_1: ModelPart
    private val GlowHead_1: ModelPart
    private val GlowJaw_1: ModelPart

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Back01 = this.BodyMain.getChild("Back01")
        this.Back01b = this.Back01.getChild("Back01b")
        this.EquipBaseL = this.BodyMain.getChild("EquipBaseL")
        this.EquipL01 = this.EquipBaseL.getChild("EquipL01")
        this.CanonBase = this.EquipL01.getChild("CanonBase")
        this.Neck_1 = this.CanonBase.getChild("Neck_1")
        this.Jaw_1 = this.Neck_1.getChild("Jaw_1")
        this.Head_1 = this.Neck_1.getChild("Head_1")
        this.EquipCannonPlate = this.EquipL01.getChild("EquipCannonPlate")
        this.Back02 = this.BodyMain.getChild("Back02")
        this.Back02b = this.Back02.getChild("Back02b")
        this.Back02c = this.Back02b.getChild("Back02c")
        this.Back02d = this.Back02c.getChild("Back02d")
        this.Back02e = this.Back02d.getChild("Back02e")
        this.Neck = this.Back02e.getChild("Neck")
        this.Head = this.Neck.getChild("Head")
        this.Jaw = this.Neck.getChild("Jaw")
        this.EquipBaseR = this.BodyMain.getChild("EquipBaseR")
        this.EquipR01 = this.EquipBaseR.getChild("EquipR01")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowEquipBaseL = this.GlowBodyMain.getChild("GlowEquipBaseL")
        this.GlowEquipL01 = this.GlowEquipBaseL.getChild("GlowEquipL01")
        this.GlowEquipCannonPlate = this.GlowEquipL01.getChild("GlowEquipCannonPlate")
        this.EquipCannon01 = this.GlowEquipCannonPlate.getChild("EquipCannon01")
        this.GlowCanonBase = this.GlowEquipL01.getChild("GlowCanonBase")
        this.GlowNeck_1 = this.GlowCanonBase.getChild("GlowNeck_1")
        this.GlowHead_1 = this.GlowNeck_1.getChild("GlowHead_1")
        this.HeadTooth_1 = this.GlowHead_1.getChild("HeadTooth_1")
        this.GlowJaw_1 = this.GlowNeck_1.getChild("GlowJaw_1")
        this.JawTooth_1 = this.GlowJaw_1.getChild("JawTooth_1")
        this.Road01u = this.GlowNeck_1.getChild("Road01u")
        this.Road02u = this.Road01u.getChild("Road02u")
        this.Road03u = this.Road02u.getChild("Road03u")
        this.Road01v = this.GlowNeck_1.getChild("Road01v")
        this.Road02v = this.Road01v.getChild("Road02v")
        this.Road03v = this.Road02v.getChild("Road03v")
        this.GlowBack02 = this.GlowBodyMain.getChild("GlowBack02")
        this.GlowBack02b = this.GlowBack02.getChild("GlowBack02b")
        this.GlowBack02c = this.GlowBack02b.getChild("GlowBack02c")
        this.GlowBack02d = this.GlowBack02c.getChild("GlowBack02d")
        this.GlowBack02e = this.GlowBack02d.getChild("GlowBack02e")
        this.GlowNeck = this.GlowBack02e.getChild("GlowNeck")
        this.GlowHead = this.GlowNeck.getChild("GlowHead")
        this.HeadTooth = this.GlowHead.getChild("HeadTooth")
        this.Road01 = this.GlowHead.getChild("Road01")
        this.Road02 = this.Road01.getChild("Road02")
        this.Road03 = this.Road02.getChild("Road03")
        this.Road04 = this.Road03.getChild("Road04")
        this.Road05 = this.Road04.getChild("Road05")
        this.GlowJaw = this.GlowNeck.getChild("GlowJaw")
        this.JawTooth = this.GlowJaw.getChild("JawTooth")
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

        var offsetY = 0.6f
        if (entity is EntityMountBase) {
            if (entity.shipDepth > 0.0) {
                offsetY += angleX * 0.025f + 0.025f
            }
            if (entity.host != null && entity.host!!.isOrderedToSit) {
                offsetY += 0.12f
            }
        }
        this.BodyMain.y = offsetY * 16.0f
        this.GlowBodyMain.y = this.BodyMain.y

        this.Jaw.xRot = angleX * 0.1f + 0.7f
        this.GlowJaw.xRot = this.Jaw.xRot

        this.EquipCannon01.xRot = angleX * 0.08f - 0.32f
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "mount_hb_h"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 0).addBox(-9f, -2f, 14f, 18f, 10f, 9f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val Back01 = BodyMain.addOrReplaceChild(
                "Back01",
                CubeListBuilder.create().texOffs(29, 22).addBox(0f, 0f, 0f, 13f, 10f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1f, -7f, 19f, 0f, 0.1396f, 0f)
            )

            val Back01b = Back01.addOrReplaceChild(
                "Back01b",
                CubeListBuilder.create().texOffs(29, 22).addBox(0f, 0f, 0f, 13f, 10f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -10f, 0f)
            )

            val EquipBaseL = BodyMain.addOrReplaceChild(
                "EquipBaseL",
                CubeListBuilder.create().texOffs(64, 30).addBox(-6f, -4f, -7f, 11f, 11f, 21f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(14.5f, 2f, 5f, 0f, -0.0524f, 0f)
            )

            val EquipL01 = EquipBaseL.addOrReplaceChild(
                "EquipL01",
                CubeListBuilder.create().texOffs(66, 31).addBox(-6f, 0f, -7f, 10f, 4f, 20f, CubeDeformation(0f)),
                PartPose.offset(0.5f, -8f, 1f)
            )

            val CanonBase = EquipL01.addOrReplaceChild(
                "CanonBase",
                CubeListBuilder.create().texOffs(0, 21).addBox(0f, 0f, 0f, 7f, 9f, 7f, CubeDeformation(0f)),
                PartPose.offset(-3.5f, -9f, 3f)
            )

            val Neck_1 = CanonBase.addOrReplaceChild(
                "Neck_1",
                CubeListBuilder.create().texOffs(0, 37).addBox(-4f, -6f, -0.5f, 8f, 8f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.5f, -1f, 3f, -0.2618f, -0.0873f, 0f)
            )

            val Jaw_1 = Neck_1.addOrReplaceChild(
                "Jaw_1",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-5f, -1f, -15f, 10f, 4f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -5f, 3f, 0.8727f, 0f, 0f)
            )

            val Head_1 = Neck_1.addOrReplaceChild(
                "Head_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, -4f, -17f, 10f, 4f, 17f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.1f, -2.5f, 3f, -0.3643f, 0f, 0f)
            )

            val EquipCannonPlate = EquipL01.addOrReplaceChild(
                "EquipCannonPlate",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 4f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offset(-3f, 1.8f, -7.5f)
            )

            val Back02 = BodyMain.addOrReplaceChild(
                "Back02",
                CubeListBuilder.create().texOffs(29, 22).addBox(-14f, 0f, 0f, 13f, 10f, 13f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7f, 19f, 0f, -0.1396f, 0f)
            )

            val Back02b = Back02.addOrReplaceChild(
                "Back02b",
                CubeListBuilder.create().texOffs(29, 22).addBox(0f, 0f, 0f, 13f, 10f, 13f, CubeDeformation(0f)),
                PartPose.offset(-14f, -10f, 0f)
            )

            val Back02c = Back02b.addOrReplaceChild(
                "Back02c",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, -4f, -9f, 8f, 8f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.5f, 2f, 8f, -0.44f, 1.22f, 0f)
            )

            val Back02d = Back02c.addOrReplaceChild(
                "Back02d",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, -4f, -9f, 8f, 8f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -8f, 0.5236f, -0.6981f, -0.2618f)
            )

            val Back02e = Back02d.addOrReplaceChild(
                "Back02e",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4f, -4f, -9f, 8f, 8f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -8f, 0.3491f, -0.3491f, 0f)
            )

            val Neck = Back02e.addOrReplaceChild(
                "Neck",
                CubeListBuilder.create().texOffs(0, 37).addBox(-4f, -4f, -7f, 8f, 8f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, -10f, -0.1745f, 0.0873f, -0.0873f)
            )

            val Head = Neck.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, -4f, -17f, 10f, 4f, 17f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1f, -4f, 0.0873f, 0f, 0f)
            )

            val Jaw = Neck.addOrReplaceChild(
                "Jaw",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-5f, -1f, -15f, 10f, 4f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -0.8f, -3f, 0.6283f, 0f, 0f)
            )

            val EquipBaseR = BodyMain.addOrReplaceChild(
                "EquipBaseR",
                CubeListBuilder.create().texOffs(64, 30).addBox(-6f, -4f, -7f, 11f, 11f, 21f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-13.5f, 2f, 5f, 0f, 0.0524f, 0f)
            )

            val EquipR01 = EquipBaseR.addOrReplaceChild(
                "EquipR01",
                CubeListBuilder.create().texOffs(66, 31).addBox(-5f, 0f, -7f, 10f, 4f, 20f, CubeDeformation(0f)),
                PartPose.offset(-0.5f, -8f, 1f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 0f)
            )

            val GlowEquipBaseL = GlowBodyMain.addOrReplaceChild(
                "GlowEquipBaseL",
                CubeListBuilder.create().texOffs(64, 30),
                PartPose.offsetAndRotation(14.5f, 2f, 5f, 0f, -0.0524f, 0f)
            )

            val GlowEquipL01 = GlowEquipBaseL.addOrReplaceChild(
                "GlowEquipL01",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0.5f, -8f, 1f)
            )

            val GlowEquipCannonPlate = GlowEquipL01.addOrReplaceChild(
                "GlowEquipCannonPlate",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 4f, 3f, 1f, CubeDeformation(0f)),
                PartPose.offset(-3f, 1.8f, -7.5f)
            )

            val EquipCannon01 = GlowEquipCannonPlate.addOrReplaceChild(
                "EquipCannon01",
                CubeListBuilder.create().texOffs(47, 0).addBox(0f, 0f, -7f, 1f, 1f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(1.5f, 1f, 0.5f, -0.3187f, -0.0873f, 0f)
            )

            val GlowCanonBase = GlowEquipL01.addOrReplaceChild(
                "GlowCanonBase",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(-3.5f, -9f, 3f)
            )

            val GlowNeck_1 = GlowCanonBase.addOrReplaceChild(
                "GlowNeck_1",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(3.5f, -1f, 3f, -0.2618f, -0.0873f, 0f)
            )

            val GlowHead_1 = GlowNeck_1.addOrReplaceChild(
                "GlowHead_1",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0.1f, -2.5f, 3f, -0.3643f, 0f, 0f)
            )

            val HeadTooth_1 = GlowHead_1.addOrReplaceChild(
                "HeadTooth_1",
                CubeListBuilder.create().texOffs(22, 46).addBox(-4.5f, 0f, -6.5f, 9f, 4f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2f, -8f, 0.1745f, 0f, 0f)
            )

            val GlowJaw_1 = GlowNeck_1.addOrReplaceChild(
                "GlowJaw_1",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -5f, 3f, 0.8727f, 0f, 0f)
            )

            val JawTooth_1 = GlowJaw_1.addOrReplaceChild(
                "JawTooth_1",
                CubeListBuilder.create().texOffs(22, 46).addBox(-4.5f, 0f, -14f, 9f, 3f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.1f, -0.6f, -0.3f, -0.2094f, 0f, 0f)
            )

            val Road01u = GlowNeck_1.addOrReplaceChild(
                "Road01u",
                CubeListBuilder.create().texOffs(86, 16).addBox(-4.5f, 0f, -12f, 9f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, -4.7f, -3f)
            )

            val Road02u = Road01u.addOrReplaceChild(
                "Road02u",
                CubeListBuilder.create().texOffs(86, 16).addBox(-4.5f, 0f, -12f, 9f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -12f)
            )

            val Road03u = Road02u.addOrReplaceChild(
                "Road03u",
                CubeListBuilder.create().texOffs(86, 16).addBox(-4.5f, 0f, -12f, 9f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -12f)
            )

            val Road01v = GlowNeck_1.addOrReplaceChild(
                "Road01v",
                CubeListBuilder.create().texOffs(86, 16).addBox(-4.5f, 0f, -12f, 9f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 3.2f, -2.4f, -0.0349f, 0f, -Math.PI.toFloat())
            )

            val Road02v = Road01v.addOrReplaceChild(
                "Road02v",
                CubeListBuilder.create().texOffs(86, 16).addBox(-4.5f, 0f, -12f, 9f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -12f)
            )

            val Road03v = Road02v.addOrReplaceChild(
                "Road03v",
                CubeListBuilder.create().texOffs(86, 16).addBox(-4.5f, 0f, -12f, 9f, 1f, 12f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, -12f)
            )

            val GlowBack02 = GlowBodyMain.addOrReplaceChild(
                "GlowBack02",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -7f, 19f, 0f, -0.1396f, 0f)
            )

            val GlowBack02b = GlowBack02.addOrReplaceChild(
                "GlowBack02b",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(-14f, -10f, 0f)
            )

            val GlowBack02c = GlowBack02b.addOrReplaceChild(
                "GlowBack02c",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(3.5f, 2f, 8f, -0.44f, 1.22f, 0f)
            )

            val GlowBack02d = GlowBack02c.addOrReplaceChild(
                "GlowBack02d",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 0f, -8f, 0.5236f, -0.6981f, -0.2618f)
            )

            val GlowBack02e = GlowBack02d.addOrReplaceChild(
                "GlowBack02e",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 0f, -8f, 0.3491f, -0.3491f, 0f)
            )

            val GlowNeck = GlowBack02e.addOrReplaceChild(
                "GlowNeck",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 0f, -10f, -0.1745f, 0.0873f, -0.0873f)
            )

            val GlowHead = GlowNeck.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -1f, -4f, 0.0873f, 0f, 0f)
            )

            val HeadTooth = GlowHead.addOrReplaceChild(
                "HeadTooth",
                CubeListBuilder.create().texOffs(22, 46).addBox(-4.5f, 0f, -6.5f, 9f, 4f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2f, -8f, 0.1745f, 0f, 0f)
            )

            val Road01 = GlowHead.addOrReplaceChild(
                "Road01",
                CubeListBuilder.create().texOffs(55, 0).addBox(-5.5f, 0f, 0f, 11f, 1f, 14f, CubeDeformation(0f)),
                PartPose.offset(0f, -5f, -23f)
            )

            val Road02 = Road01.addOrReplaceChild(
                "Road02",
                CubeListBuilder.create().texOffs(55, 0).addBox(-5.5f, 0f, 0f, 11f, 1f, 14f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 14f)
            )

            val Road03 = Road02.addOrReplaceChild(
                "Road03",
                CubeListBuilder.create().texOffs(55, 0).addBox(-5.5f, 0f, 0f, 11f, 1f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.4f, 0.1f, 12f, 0.0873f, -0.3643f, -0.0175f)
            )

            val Road04 = Road03.addOrReplaceChild(
                "Road04",
                CubeListBuilder.create().texOffs(55, 0).addBox(-5.5f, 0f, 0f, 10f, 1f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.6f, 0.1f, 10f, 0.0384f, 0.8652f, 0.014f)
            )

            val Road05 = Road04.addOrReplaceChild(
                "Road05",
                CubeListBuilder.create().texOffs(55, 0).addBox(-5.5f, 0f, 0f, 11f, 1f, 14f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 14f, -0.6981f, 0f, 0f)
            )

            val GlowJaw = GlowNeck.addOrReplaceChild(
                "GlowJaw",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -0.8f, -3f, 0.6283f, 0f, 0f)
            )

            val JawTooth = GlowJaw.addOrReplaceChild(
                "JawTooth",
                CubeListBuilder.create().texOffs(22, 46).addBox(-4.5f, 0f, -14f, 9f, 3f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -1.6f, -0.3f, -0.1745f, 0f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 64)
        }
    }
}
