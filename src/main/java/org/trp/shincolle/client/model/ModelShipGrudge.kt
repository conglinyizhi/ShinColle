package org.trp.shincolle.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import org.trp.shincolle.Shincolle

class ModelShipGrudge<T : Entity>(root: ModelPart) : EntityModel<T>() {
    private val cube: ModelPart

    init {
        this.cube = root.getChild("cube")
        this.cube.zRot = 0.774f
    }

    fun setDynamicRotation(ticks: Float) {
        val angle = (ticks % 360.0f) * 0.1f
        cube.xRot = angle
        cube.yRot = angle
    }

    override fun setupAnim(
        entity: Entity,
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
        cube.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "ship_grudge"), "main"
        )

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            partdefinition.addOrReplaceChild(
                "cube",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f),
                PartPose.ZERO
            )

            return LayerDefinition.create(meshdefinition, 16, 16)
        }
    }
}
