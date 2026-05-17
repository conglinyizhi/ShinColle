package org.trp.shincolle.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.Shincolle;

public class ModelBlockDesk extends Model {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "blockdesk"), "main");

    private final ModelPart root;

    public ModelBlockDesk(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        part.addOrReplaceChild("bone",
                CubeListBuilder.create()
                        .texOffs(0, 30).addBox(-16.0F, -16.0F, 0.0F, 16.0F, 1.0F, 16.0F, CubeDeformation.NONE)
                        .texOffs(1, 0).addBox(-16.0F, -15.0F, 0.0F, 1.0F, 15.0F, 15.0F, CubeDeformation.NONE)
                        .texOffs(1, 0).addBox(-1.0F, -15.0F, 0.0F, 1.0F, 15.0F, 15.0F, CubeDeformation.NONE)
                        .texOffs(0, 0).addBox(-16.0F, -15.0F, 15.0F, 16.0F, 15.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(34, 0).addBox(-15.0F, -15.0F, 0.0F, 14.0F, 6.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(0, 15).addBox(-15.0F, -10.0F, 1.0F, 14.0F, 1.0F, 14.0F, CubeDeformation.NONE),
                PartPose.offset(8.0F, 24.0F, -8.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
