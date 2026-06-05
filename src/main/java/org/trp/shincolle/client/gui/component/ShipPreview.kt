package org.trp.shincolle.client.gui.component

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import org.joml.Quaternionf
import java.util.function.Supplier
import kotlin.math.atan
import kotlin.math.max
import kotlin.math.min

/**
 * A widget that renders a 3D ship entity model using [EntityRenderDispatcher].
 * 
 * 
 * The ship entity is fetched from the provided [Supplier] each frame so the
 * displayed entity can change dynamically. Mouse position affects the look-at
 * rotation of the rendered model.
 */
class ShipPreview
/**
 * @param x            screen-absolute X position
 * @param y            screen-absolute Y position
 * @param width        widget width
 * @param height       widget height
 * @param shipSupplier supplier that returns the entity to render (fetched each frame)
 */(
    x: Int, y: Int, width: Int, height: Int,
    private val shipSupplier: Supplier<LivingEntity?>
) : AbstractWidget(x, y, width, height, Component.literal("")) {
    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val entity = shipSupplier.get()
        if (entity == null) return

        val cx = getX() + getWidth() / 2
        val cy = getY() + getHeight() / 2
        val scale = max(16, min(getWidth(), getHeight()) / 2)

        renderEntityWithPassengers(guiGraphics, cx, cy, scale, mouseX.toFloat(), mouseY.toFloat(), entity)
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {
        this.defaultButtonNarrationText(output)
    }

    companion object {
        /**
         * Render a living entity with its passengers at the given screen position.
         * 
         * 
         * The mouse coordinates (`mx`, `my`) control the look-at rotation,
         * producing the same interactive preview effect used in `ShipInventoryScreen`.
         * 
         * @param g      GUI graphics
         * @param x      screen X centre for the model
         * @param y      screen Y centre for the model
         * @param scale  model scale factor
         * @param mx     mouse X (screen-absolute)
         * @param my     mouse Y (screen-absolute)
         * @param entity the entity to render
         */
        fun renderEntityWithPassengers(
            g: GuiGraphics, x: Int, y: Int, scale: Int,
            mx: Float, my: Float, entity: LivingEntity
        ) {
            val f = atan(((x - mx) / 40.0f).toDouble()).toFloat()
            val f1 = atan(((y - 50.0f - my) / 40.0f).toDouble()).toFloat()
            val pose = g.pose()
            pose.pushPose()
            pose.translate(x.toDouble(), y.toDouble(), 50.0)
            pose.scale(scale.toFloat(), scale.toFloat(), -scale.toFloat())
            val q1 = (Quaternionf()).rotateZ(Math.PI.toFloat())
            val q2 = (Quaternionf()).rotateX(f1 * 20f * 0.017453292f)
            q1.mul(q2)
            pose.mulPose(q1)
            val byo = entity.yBodyRotO
            val by = entity.yBodyRot
            val yr = entity.getYRot()
            val xr = entity.getXRot()
            val yho = entity.yHeadRotO
            val yh = entity.yHeadRot
            entity.yBodyRotO = 180f + f * 20f
            entity.yBodyRot = 180f + f * 20f
            entity.setYRot(180f + f * 40f)
            entity.yHeadRotO = entity.getYRot()
            entity.yHeadRot = entity.getYRot()
            entity.setXRot(-f1 * 20f)
            val d = Minecraft.getInstance().getEntityRenderDispatcher()
            q2.conjugate()
            d.overrideCameraOrientation(q2)
            d.setRenderShadow(false)
            RenderSystem.runAsFancy(Runnable {
                d.render<LivingEntity?>(entity, 0.0, 0.0, 0.0, 0f, 1f, pose, g.bufferSource(), 15728880)
                for (pass in entity.getPassengers()) {
                    var pbyo = 0f
                    var pby = 0f
                    var pyr = 0f
                    var pxr = 0f
                    var pyho = 0f
                    var pyh = 0f
                    if (pass is LivingEntity) {
                        pbyo = pass.yBodyRotO
                        pby = pass.yBodyRot
                        pyr = pass.getYRot()
                        pxr = pass.getXRot()
                        pyho = pass.yHeadRotO
                        pyh = pass.yHeadRot
                        pass.yBodyRotO = entity.yBodyRotO
                        pass.yBodyRot = entity.yBodyRot
                        pass.setYRot(entity.getYRot())
                        pass.yHeadRotO = entity.yHeadRotO
                        pass.yHeadRot = entity.yHeadRot
                        pass.setXRot(entity.getXRot())
                    }
                    pose.pushPose()
                    val rp = entity.getPassengerRidingPosition(pass)
                    val inv = 1.0 / scale
                    pose.translate(
                        (rp.x - entity.getX()) * inv,
                        (rp.y - entity.getY()) * inv + 0.09,
                        (rp.z - entity.getZ()) * inv
                    )
                    pose.translate(0.0, 0.0, 0.2)
                    d.render<Entity?>(pass, 0.0, 0.0, 0.0, 0f, 1f, pose, g.bufferSource(), 15728880)
                    pose.popPose()
                    if (pass is LivingEntity) {
                        pass.yBodyRotO = pbyo
                        pass.yBodyRot = pby
                        pass.setYRot(pyr)
                        pass.setXRot(pxr)
                        pass.yHeadRotO = pyho
                        pass.yHeadRot = pyh
                    }
                }
            })
            g.flush()
            d.setRenderShadow(true)
            entity.yBodyRotO = byo
            entity.yBodyRot = by
            entity.setYRot(yr)
            entity.setXRot(xr)
            entity.yHeadRotO = yho
            entity.yHeadRot = yh
            pose.popPose()
        }
    }
}
