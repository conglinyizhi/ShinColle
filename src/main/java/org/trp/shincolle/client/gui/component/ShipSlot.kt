package org.trp.shincolle.client.gui.component

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import org.joml.Quaternionf
import kotlin.math.max
import kotlin.math.min

/**
 * A widget that renders a ship entity preview with status bars.
 * 
 * 
 * Replaces the hardcoded ship slot rendering in `FormationScreen` and
 * ship list entries in `DeskScreen`. Renders:
 * 
 *  * 3D ship entity preview (using [EntityRenderDispatcher])
 *  * Health bar (green → red based on HP ratio)
 *  * Ship name label above the slot
 *  * Click handling for ship selection
 * 
 * 
 * 
 * Usage:
 * <pre>`ShipSlot slot = new ShipSlot(x, y, 48, 64, ship, () -> selectShip(ship)); this.addRenderableWidget(slot); `</pre>
 * 
 * 
 * The entity preview uses the same rendering technique as
 * `ShipInventoryScreen.renderEntityWithPassengers()`. The model is
 * centered in the widget bounds and automatically scaled to fit.
 */
class ShipSlot(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private val ship: LivingEntity?,
    private val onPress: Runnable?
) : AbstractWidget(x, y, width, height, Component.literal("")) {
    private var modelScale = 24.0f

    /**
     * @param x        screen-absolute X position
     * @param y        screen-absolute Y position
     * @param width    slot width
     * @param height   slot height
     * @param ship     the ship entity to display
     * @param onPress  optional click callback (null for non-interactive)
     */
    init {
        this.active = onPress != null
    }

    /**
     * Set a custom model scale factor.
     * Smaller value = larger model (default 24).
     */
    fun withScale(scale: Float): ShipSlot {
        this.modelScale = scale
        return this
    }

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        // ---- Background ----
        val bgColor = if (this.isHoveredOrFocused()) 0x60FFFFFF else 0x40000000
        graphics.fill(getX(), getY(), getX() + width, getY() + height, bgColor)

        // ---- Entity preview ----
        if (ship != null) {
            val centerX = getX() + width / 2
            val centerY = getY() + height / 2 - 6
            val entitySize = modelScale.toInt()
            renderShipEntity(graphics, centerX, centerY, entitySize, mouseX, mouseY)
        }

        // ---- Name label ----
        val name = if (ship!!.hasCustomName())
            ship.getCustomName()
        else
            Component.translatable(ship.getType().getDescriptionId())
        val nameWidth = Minecraft.getInstance().font.width(name)
        val nameX = getX() + (width - nameWidth) / 2
        val nameY = getY() + height - 10
        graphics.drawString(Minecraft.getInstance().font, name, nameX, nameY, 0xFFFFFF, true)

        // ---- Status bars ----
        drawBar(
            graphics, getX() + 2, getY() + 2, BAR_WIDTH, BAR_HEIGHT,
            ship.getHealth() / ship.getMaxHealth(), 0xFF4444, -0xbb55bc
        )
    }

    // ---- Entity rendering ----
    private fun renderShipEntity(graphics: GuiGraphics, x: Int, y: Int, scale: Int, mouseX: Int, mouseY: Int) {
        val lookX = (x - mouseX) / 40.0f
        val lookY = (y - 50.0f - mouseY) / 40.0f

        val poseStack = graphics.pose()
        poseStack.pushPose()
        poseStack.translate(x.toDouble(), y.toDouble(), 50.0)
        poseStack.scale(scale.toFloat(), scale.toFloat(), -scale.toFloat())

        val rotation = Quaternionf().rotateZ(Math.PI.toFloat())
        val pitch = Quaternionf().rotateX(lookY * 20.0f * 0.017453292f)
        rotation.mul(pitch)
        poseStack.mulPose(rotation)

        // Save entity rotation
        val prevYBodyRot = ship!!.yBodyRotO
        val prevYRot = ship.getYRot()
        val prevXRot = ship.getXRot()
        val prevYHeadRotO = ship.yHeadRotO
        val prevYHeadRot = ship.yHeadRot
        val prevYBodyRotO = ship.yBodyRotO

        ship.yBodyRotO = 180.0f + lookX * 20.0f
        ship.yBodyRot = 180.0f + lookX * 20.0f
        ship.setYRot(180.0f + lookX * 40.0f)
        ship.yHeadRotO = ship.getYRot()
        ship.yHeadRot = ship.getYRot()
        ship.setXRot(-lookY * 20.0f)

        val dispatcher = Minecraft.getInstance().getEntityRenderDispatcher()
        pitch.conjugate()
        dispatcher.overrideCameraOrientation(pitch)
        dispatcher.setRenderShadow(false)

        RenderSystem.runAsFancy(Runnable {
            dispatcher.render<LivingEntity?>(
                ship,
                0.0,
                0.0,
                0.0,
                0.0f,
                1.0f,
                poseStack,
                graphics.bufferSource(),
                15728880
            )
        }
        )

        // Restore entity rotation
        ship.yBodyRotO = prevYBodyRotO
        ship.yBodyRot = prevYBodyRot
        ship.setYRot(prevYRot)
        ship.setXRot(prevXRot)
        ship.yHeadRotO = prevYHeadRotO
        ship.yHeadRot = prevYHeadRot

        dispatcher.setRenderShadow(true)
        dispatcher.overrideCameraOrientation(null)

        poseStack.popPose()
    }

    // ---- Interaction ----
    override fun onClick(mouseX: Double, mouseY: Double) {
        if (onPress != null) {
            onPress.run()
        }
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {
        this.defaultButtonNarrationText(output)
    }

    companion object {
        private const val BAR_WIDTH = 40
        private const val BAR_HEIGHT = 3
        private const val BAR_SPACING = 5
        private const val BAR_Y_OFFSET = 4

        // ---- Bar drawing ----
        private fun drawBar(
            graphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int,
            ratio: Float, colorBg: Int, colorFg: Int
        ) {
            // Background
            graphics.fill(x, y, x + width, y + height, colorBg)
            // Foreground
            val fillWidth = (width * min(1.0f, max(0.0f, ratio))).toInt()
            if (fillWidth > 0) {
                graphics.fill(x + 1, y + 1, x + fillWidth - 1, y + height - 1, colorFg)
            }
        }
    }
}
