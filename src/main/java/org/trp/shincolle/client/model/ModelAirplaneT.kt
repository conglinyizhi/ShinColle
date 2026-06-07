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

class ModelAirplaneT<T : Entity>(root: ModelPart) : EntityModel<T>(), IGlowableModel {
    private val BodyMain: ModelPart
    private val Tail01: ModelPart
    private val Wing01: ModelPart
    private val Wing02: ModelPart
    private val BodyU: ModelPart
    private val Propeller: ModelPart
    private val Prop02: ModelPart
    private val Tank: ModelPart
    private val Tail02: ModelPart
    private val Tail03: ModelPart
    private val Tail04: ModelPart
    private val GlowBodyMain: ModelPart

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.Wing02 = this.BodyMain.getChild("Wing02")
        this.Tail01 = this.BodyMain.getChild("Tail01")
        this.Tail02 = this.Tail01.getChild("Tail02")
        this.Tail04 = this.Tail02.getChild("Tail04")
        this.Tail03 = this.Tail02.getChild("Tail03")
        this.Propeller = this.BodyMain.getChild("Propeller")
        this.Prop02 = this.BodyMain.getChild("Prop02")
        this.Wing01 = this.BodyMain.getChild("Wing01")
        this.Tank = this.BodyMain.getChild("Tank")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.BodyU = this.GlowBodyMain.getChild("BodyU")
    }

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val yaw = netHeadYaw * (Math.PI.toFloat() / 180f)
        val pitch = headPitch * (Math.PI.toFloat() / 180f)

        BodyMain.yRot = yaw
        BodyMain.xRot = pitch
        syncGlowParts()
    }

    private fun syncGlowParts() {
        GlowBodyMain.copyFrom(BodyMain)
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
        @JvmField
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "airplane_t"), "main")

        @JvmStatic
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 17).addBox(-2f, -3f, -6f, 4f, 4f, 11f, CubeDeformation(0f)),
                PartPose.offset(0f, 0f, 0f)
            )

            val Wing02 = BodyMain.addOrReplaceChild(
                "Wing02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-13f, 0f, 0f, 13f, 1f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2f, -0.4f, -2.5f, 0f, 0f, 0.0698f)
            )

            val Tail01 = BodyMain.addOrReplaceChild(
                "Tail01",
                CubeListBuilder.create().texOffs(30, 25).addBox(-2f, 0f, 0f, 4f, 3f, 4f, CubeDeformation(0f)),
                PartPose.offset(0f, -2.8f, 5f)
            )

            val Tail02 = Tail01.addOrReplaceChild(
                "Tail02",
                CubeListBuilder.create().texOffs(46, 24).addBox(-1.5f, 0f, 0f, 3f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.1f, 4f)
            )

            val Tail04 = Tail02.addOrReplaceChild(
                "Tail04",
                CubeListBuilder.create().texOffs(0, 13).addBox(-6.5f, 0f, 0f, 13f, 1f, 3f, CubeDeformation(0f)),
                PartPose.offset(0f, 0.2f, 2f)
            )

            val Tail03 = Tail02.addOrReplaceChild(
                "Tail03",
                CubeListBuilder.create().texOffs(0, 17).addBox(-0.5f, 0f, 0f, 1f, 4f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -2.2f, 4.5f, -1.0472f, 0f, 0f)
            )

            val Propeller = BodyMain.addOrReplaceChild(
                "Propeller",
                CubeListBuilder.create().texOffs(0, 6).addBox(-3f, -3f, 0f, 6f, 6f, 0f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, -6.5f)
            )

            val Prop02 = BodyMain.addOrReplaceChild(
                "Prop02",
                CubeListBuilder.create().texOffs(1, 25).addBox(-0.5f, -0.5f, 0f, 1f, 1f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, -7.1f)
            )

            val Wing01 = BodyMain.addOrReplaceChild(
                "Wing01",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0f, 0f, 0f, 13f, 1f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -0.4f, -2.5f, 0f, 0f, -0.0698f)
            )

            val Tank = BodyMain.addOrReplaceChild(
                "Tank",
                CubeListBuilder.create().texOffs(32, 7).addBox(-1f, 0f, 0f, 2f, 2f, 14f, CubeDeformation(0f)),
                PartPose.offset(0f, 1.5f, -5.5f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 17),
                PartPose.offset(0f, 0f, 0f)
            )

            val BodyU = GlowBodyMain.addOrReplaceChild(
                "BodyU",
                CubeListBuilder.create().texOffs(19, 17).addBox(-1.5f, 0f, 0f, 3f, 2f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -4.9f, -1.8f, -0.3142f, 0f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 32)
        }
    }
}
