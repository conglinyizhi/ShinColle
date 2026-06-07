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
import org.trp.shincolle.Shincolle
import org.trp.shincolle.entity.base.EntityShincolleSimpleMob

class ModelRensouhouS<T : EntityShincolleSimpleMob?>(root: ModelPart) : EntityModel<T>(), IGlowableModel {
    private val BodyMain: ModelPart
    private val HeadBase: ModelPart
    private val TailJaw1: ModelPart
    private val Head: ModelPart
    private val TailHeadCL1: ModelPart
    private val TailHeadCR1: ModelPart
    private val Tooth02: ModelPart
    private val Tube01: ModelPart
    private val Tube02: ModelPart
    private val Tube03: ModelPart
    private val TailHead2: ModelPart
    private val Tooth01: ModelPart
    private val HeadCannon1: ModelPart
    private val HeadCannon2: ModelPart
    private val GlowBodyMain: ModelPart
    private val GlowHeadBase: ModelPart
    private val GlowHead: ModelPart
    private val GlowTailJaw1: ModelPart
    private val GlowTailHead2: ModelPart

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.HeadBase = this.BodyMain.getChild("HeadBase")
        this.TailJaw1 = this.HeadBase.getChild("TailJaw1")
        this.Tube03 = this.TailJaw1.getChild("Tube03")
        this.Tube01 = this.TailJaw1.getChild("Tube01")
        this.Tube02 = this.TailJaw1.getChild("Tube02")
        this.Head = this.HeadBase.getChild("Head")
        this.TailHead2 = this.Head.getChild("TailHead2")
        this.TailHeadCR1 = this.HeadBase.getChild("TailHeadCR1")
        this.TailHeadCL1 = this.HeadBase.getChild("TailHeadCL1")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowHeadBase = this.GlowBodyMain.getChild("GlowHeadBase")
        this.GlowHead = this.GlowHeadBase.getChild("GlowHead")
        this.GlowTailHead2 = this.GlowHead.getChild("GlowTailHead2")
        this.HeadCannon1 = this.GlowTailHead2.getChild("HeadCannon1")
        this.HeadCannon2 = this.GlowTailHead2.getChild("HeadCannon2")
        this.Tooth01 = this.GlowHead.getChild("Tooth01")
        this.GlowTailJaw1 = this.GlowHeadBase.getChild("GlowTailJaw1")
        this.Tooth02 = this.GlowTailJaw1.getChild("Tooth02")
    }

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val angleX = Mth.cos(ageInTicks * 0.1f)
        this.TailJaw1.xRot = angleX * 0.05f - 0.3142f
        this.HeadCannon1.xRot = angleX * 0.1f + 0.15f
        this.HeadCannon2.xRot = -angleX * 0.1f + 0.15f

        if (entity != null && entity.attackTick > 0) {
            this.TailJaw1.xRot = angleX * 0.3f - 0.8f
        }

        this.GlowTailJaw1.xRot = this.TailJaw1.xRot
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
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "rensouhou_s"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 0f)
            )

            val HeadBase = BodyMain.addOrReplaceChild(
                "HeadBase",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6f, -8f, 2f, 12f, 15f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 9f, -0.1396f, 3.1416f, 0f)
            )

            val TailJaw1 = HeadBase.addOrReplaceChild(
                "TailJaw1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.5f, 0f, 0f, 13f, 5f, 16f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, -0.3142f, 0f, 0f)
            )

            val Tube03 = TailJaw1.addOrReplaceChild(
                "Tube03",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 11f, 1f, 1f, CubeDeformation(0f)),
                PartPose.offset(-5.5f, 4.6f, 22f)
            )

            val Tube01 = TailJaw1.addOrReplaceChild(
                "Tube01",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, 0f, 0f, 1f, 1f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.5f, 3f, 13f, -0.1745f, -0.0524f, 0f)
            )

            val Tube02 = TailJaw1.addOrReplaceChild(
                "Tube02",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, 0f, 0f, 1f, 1f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.5f, 3f, 13f, -0.1745f, 0.0524f, 0f)
            )

            val Head = HeadBase.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7f, -0.2f, -3.6f, 14f, 8f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -8.5f, 4f, 0.1745f, 0f, 0f)
            )

            val TailHead2 = Head.addOrReplaceChild(
                "TailHead2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7f, 0f, 0f, 14f, 8f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -1f, 6.5f)
            )

            val TailHeadCR1 = HeadBase.addOrReplaceChild(
                "TailHeadCR1",
                CubeListBuilder.create().texOffs(36, 25).mirror()
                    .addBox(-3f, -3f, -3f, 3f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5.5f, 0f, 9f, 0.7854f, -0.1396f, 0f)
            )

            val TailHeadCL1 = HeadBase.addOrReplaceChild(
                "TailHeadCL1",
                CubeListBuilder.create().texOffs(36, 25).addBox(0f, -3f, -3f, 3f, 6f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.5f, 0f, 9f, 0.7854f, 0.1396f, 0f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, 0f, 0f)
            )

            val GlowHeadBase = GlowBodyMain.addOrReplaceChild(
                "GlowHeadBase",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 0f, 9f, -0.1396f, 3.1416f, 0f)
            )

            val GlowHead = GlowHeadBase.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, -8.5f, 4f, 0.1745f, 0f, 0f)
            )

            val GlowTailHead2 = GlowHead.addOrReplaceChild(
                "GlowTailHead2",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -1f, 6.5f)
            )

            val HeadCannon1 = GlowTailHead2.addOrReplaceChild(
                "HeadCannon1",
                CubeListBuilder.create().texOffs(26, 6).addBox(-2f, -2f, 0f, 4f, 4f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.2f, 3.5f, 12f, 0.0873f, 0.0873f, 0.0176f)
            )

            val HeadCannon2 = GlowTailHead2.addOrReplaceChild(
                "HeadCannon2",
                CubeListBuilder.create().texOffs(26, 6).addBox(-2f, -2f, 0f, 4f, 4f, 15f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.2f, 3.5f, 12f, 0.0873f, -0.0873f, 0f)
            )

            val Tooth01 = GlowHead.addOrReplaceChild(
                "Tooth01",
                CubeListBuilder.create().texOffs(0, 25).addBox(-6f, 0f, 0f, 12f, 5f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 4.5f, 4.5f, -0.1745f, 0f, 0f)
            )

            val GlowTailJaw1 = GlowHeadBase.addOrReplaceChild(
                "GlowTailJaw1",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0f, 0f, 5.5f, -0.3142f, 0f, 0f)
            )

            val Tooth02 = GlowTailJaw1.addOrReplaceChild(
                "Tooth02",
                CubeListBuilder.create().texOffs(2, 42).addBox(-5.5f, 0f, 0f, 11f, 5f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -3f, 4f, 0.1745f, 0f, 0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}
