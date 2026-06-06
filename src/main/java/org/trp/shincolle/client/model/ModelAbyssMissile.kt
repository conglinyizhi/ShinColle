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


class ModelAbyssMissile<T : Entity>(root: ModelPart) : EntityModel<T>() {
    private val Body: ModelPart
    private val Head: ModelPart
    private val Tail: ModelPart
    private val Tail1: ModelPart
    private val Tail2: ModelPart

    init {
        this.Body = root.getChild("Body")
        this.Head = this.Body.getChild("Head")
        this.Tail = this.Body.getChild("Tail")
        this.Tail1 = this.Body.getChild("Tail1")
        this.Tail2 = this.Body.getChild("Tail2")
    }

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        Body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    companion object {
        @JvmField
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "abyss_missile"), "main")

        @JvmStatic
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val Body = partdefinition.addOrReplaceChild(
                "Body",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-2.0f, -2.0f, -5.5f, 4.0f, 4.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 14.0f, -1.5f)
            )

            val Head = Body.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, 0.0f, 0.0f, 3.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.5f, -1.5f, -6.5f)
            )

            val Tail = Body.addOrReplaceChild(
                "Tail",
                CubeListBuilder.create().texOffs(0, 4)
                    .addBox(0.0f, 0.0f, 0.0f, 2.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.0f, -1.0f, 5.5f)
            )

            val Tail1 = Body.addOrReplaceChild(
                "Tail1",
                CubeListBuilder.create().texOffs(0, 20)
                    .addBox(0.0f, 0.0f, 0.0f, 1.0f, 5.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-0.5f, -2.5f, 5.5f)
            )

            val Tail2 = Body.addOrReplaceChild(
                "Tail2",
                CubeListBuilder.create().texOffs(0, 15)
                    .addBox(0.0f, 0.0f, 0.0f, 5.0f, 1.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.5f, -0.5f, 5.5f)
            )

            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }
}