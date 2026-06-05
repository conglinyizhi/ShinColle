package org.trp.shincolle.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.Model
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import java.util.function.Function

class ModelBlockDesk(private val root: ModelPart) :
    Model(Function { p_110459_: ResourceLocation? -> RenderType.entityCutoutNoCull(p_110459_) }) {
    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "blockdesk"), "main")

        fun createBodyLayer(): LayerDefinition {
            val mesh = MeshDefinition()
            val part = mesh.getRoot()

            part.addOrReplaceChild(
                "bone",
                CubeListBuilder.create()
                    .texOffs(0, 30).addBox(-16.0f, -16.0f, 0.0f, 16.0f, 1.0f, 16.0f, CubeDeformation.NONE)
                    .texOffs(1, 0).addBox(-16.0f, -15.0f, 0.0f, 1.0f, 15.0f, 15.0f, CubeDeformation.NONE)
                    .texOffs(1, 0).addBox(-1.0f, -15.0f, 0.0f, 1.0f, 15.0f, 15.0f, CubeDeformation.NONE)
                    .texOffs(0, 0).addBox(-16.0f, -15.0f, 15.0f, 16.0f, 15.0f, 1.0f, CubeDeformation.NONE)
                    .texOffs(34, 0).addBox(-15.0f, -15.0f, 0.0f, 14.0f, 6.0f, 1.0f, CubeDeformation.NONE)
                    .texOffs(0, 15).addBox(-15.0f, -10.0f, 1.0f, 14.0f, 1.0f, 14.0f, CubeDeformation.NONE),
                PartPose.offset(8.0f, 24.0f, -8.0f)
            )

            return LayerDefinition.create(mesh, 64, 64)
        }
    }
}
