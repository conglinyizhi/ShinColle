package org.trp.shincolle.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.Shincolle
import org.trp.shincolle.block.LargeShipyardBlock
import org.trp.shincolle.block.entity.LargeShipyardBlockEntity
import org.trp.shincolle.client.model.ModelLargeShipyard
import org.trp.shincolle.client.model.ModelVortex
import kotlin.math.atan2
import kotlin.math.sqrt

class RenderLargeShipyard(context: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<LargeShipyardBlockEntity> {
    private val modelBase: ModelLargeShipyard
    private val modelVortex: ModelVortex

    init {
        this.modelBase = ModelLargeShipyard(context.bakeLayer(ModelLargeShipyard.LAYER_LOCATION))
        this.modelVortex = ModelVortex(context.bakeLayer(ModelVortex.LAYER_LOCATION))
    }

    override fun render(
        blockEntity: LargeShipyardBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val state = blockEntity.getBlockState()
        val isActive =
            state.hasProperty<Boolean?>(LargeShipyardBlock.ACTIVE) && state.getValue<Boolean?>(LargeShipyardBlock.ACTIVE)

        var yaw = 0.0f
        var pitch = 90.0f
        val player = Minecraft.getInstance().player
        if (player != null) {
            val pos = blockEntity.getBlockPos()
            val distX = pos.getX() + 0.5 - player.getX()
            val distY = pos.getY() - 0.75 - player.getY()
            val distZ = pos.getZ() + 0.5 - player.getZ()
            val horizontalDistance = sqrt(distX * distX + distZ * distZ).toFloat()

            val currentYawDeg = atan2(distX, distZ).toFloat() * Mth.RAD_TO_DEG
            val currentPitchDeg =
                (atan2(horizontalDistance.toDouble(), distY).toFloat() + (Mth.PI / 2.0f)) * Mth.RAD_TO_DEG

            if (!blockEntity.hasRenderAngles()) {
                blockEntity.setRenderAngles(currentYawDeg, currentPitchDeg)
            }

            val interpFactor = 0.15f
            yaw =
                blockEntity.renderYaw + Mth.wrapDegrees(currentYawDeg - blockEntity.renderYaw) * interpFactor
            pitch =
                blockEntity.renderPitch + Mth.wrapDegrees(currentPitchDeg - blockEntity.renderPitch) * interpFactor
            blockEntity.setRenderAngles(yaw, pitch)
        }

        var angle = if (player != null) -(player.tickCount.toFloat() + partialTick) % 360.0f else 0.0f
        if (isActive) {
            angle *= 5.0f
        }

        poseStack.pushPose()
        poseStack.translate(0.5, -0.2, 0.5)
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f))
        poseStack.scale(1.0f, 1.2f, 1.0f)
        val baseConsumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE_BASE))
        modelBase.renderToBuffer(poseStack, baseConsumer, packedLight, packedOverlay, -0x1)
        poseStack.popPose()

        poseStack.pushPose()
        poseStack.translate(0.5, 0.5, 0.5)
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw))
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch))
        poseStack.mulPose(Axis.ZP.rotationDegrees(angle))
        poseStack.scale(VORTEX_SCALE, VORTEX_SCALE, VORTEX_SCALE)

        val texture: ResourceLocation = if (isActive) VORTEX_ON else VORTEX_OFF
        val renderType = RenderType.beaconBeam(texture, true)

        val vortexConsumer = bufferSource.getBuffer(renderType)
        modelVortex.renderToBuffer(poseStack, vortexConsumer, LightTexture.FULL_BRIGHT, packedOverlay, -0x1)
        poseStack.popPose()
    }

    override fun shouldRenderOffScreen(blockEntity: LargeShipyardBlockEntity): Boolean {
        return true
    }

    override fun getViewDistance(): Int {
        return 256
    }

    override fun shouldRender(be: LargeShipyardBlockEntity, cameraPos: Vec3): Boolean {
        return true
    }

    override fun getRenderBoundingBox(be: LargeShipyardBlockEntity): AABB {
        return AABB.INFINITE
    }

    companion object {
        private val TEXTURE_BASE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/block/blocklargeshipyard.png")
        private val VORTEX_OFF: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/block/modelvortex.png")
        private val VORTEX_ON: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/block/modelvortexon.png")
        private const val VORTEX_SCALE = 0.5f
    }
}
