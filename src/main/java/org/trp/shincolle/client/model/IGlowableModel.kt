package org.trp.shincolle.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer

interface IGlowableModel {
    fun renderGlow(
        poseStack: PoseStack?,
        vertexConsumer: VertexConsumer?,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    )
}