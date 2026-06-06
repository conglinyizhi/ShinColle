package org.trp.shincolle.client.renderer.layer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.Config
import org.trp.shincolle.client.model.ShipModelBaseAdv
import org.trp.shincolle.entity.base.EntityShipBase

@Suppress("UNCHECKED_CAST")
class ShipHeldItemLayer<T : EntityShipBase, M : EntityModel<T>>(renderer: RenderLayerParent<T, M>) :
    RenderLayer<T, M>(renderer) {
    private val shipModel: ShipModelBaseAdv<T>
        get() = this.getParentModel() as ShipModelBaseAdv<T>
    override fun render(
        poseStack: PoseStack, bufferSource: MultiBufferSource, packedLight: Int, entity: T?,
        limbSwing: Float, limbSwingAmount: Float, partialTick: Float, ageInTicks: Float,
        netHeadYaw: Float, headPitch: Float
    ) {
        val main = entity!!.getItemBySlot(EquipmentSlot.MAINHAND)
        val off = entity.getItemBySlot(EquipmentSlot.OFFHAND)
        if (main.isEmpty() && off.isEmpty()) {
            return
        }

        if (!main.isEmpty()) {
            renderHeldItem(
                entity, main, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT,
                poseStack, bufferSource, packedLight
            )
        }
        if (!off.isEmpty()) {
            renderHeldItem(
                entity, off, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT,
                poseStack, bufferSource, packedLight
            )
        }
    }

    private fun renderHeldItem(
        entity: T?, stack: ItemStack, displayContext: ItemDisplayContext, side: HumanoidArm,
        poseStack: PoseStack, bufferSource: MultiBufferSource, packedLight: Int
    ) {
        if (stack.isEmpty()) {
            return
        }
        if (this.getParentModel() !is ShipModelBaseAdv<*>) {
            return
        }

        poseStack.pushPose()


        if (entity!!.isCrouching()) {
            poseStack.translate(0.0f, 0.2f, 0.0f)
        }

        val poseTranslateY: Float = shipModel.poseTranslateY
        if (poseTranslateY != 0.0f) {
            poseStack.translate(0.0f, poseTranslateY, 0.0f)
        }

        val isBlock = stack.getItem() is BlockItem
        val offset: FloatArray? = shipModel.getHeldItemOffset(entity, side, isBlock)
        val rotate: FloatArray? = shipModel.getHeldItemRotate(entity, side, isBlock)
        val modelScale: Float = shipModel.getScale(entity)
        val left = side == HumanoidArm.LEFT


        val ox = (offset!![0] + Config.offsetHeldItemX) / 16.0f
        val oy = (offset[1] + Config.offsetHeldItemY) / 16.0f
        val oz = (offset[2] + Config.offsetHeldItemZ) / 16.0f

        poseStack.translate(ox * (if (left) -1.0f else 1.0f), oy, oz)


        shipModel.translateToHand(side, poseStack)




        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0f + rotate!![0]))
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f + rotate[1]))
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotate[2]))



        poseStack.translate((if (left) -1.0f else 1.0f) / 16.0f, 0.3125f, -0.625f)


        val itemScale = Config.scaleHeldItem * 1.8f
        if (isBlock) {
            poseStack.scale(itemScale * 0.75f, itemScale * 0.75f, itemScale * 0.75f)
        } else {
            poseStack.scale(itemScale, itemScale, itemScale)
        }

        Minecraft.getInstance().getItemRenderer().renderStatic(
            entity,
            stack,
            displayContext,
            left,
            poseStack,
            bufferSource,
            entity.level(),
            packedLight,
            OverlayTexture.NO_OVERLAY,
            entity.getId()
        )

        poseStack.popPose()
    }
}
