package org.trp.shincolle.client.gui.component.layout

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

/**
 * A rectangular container widget with an optional background texture.
 * 
 * 
 * Serves as a base class for layout containers ([HBox], [VBox])
 * and as a stand-in for the hardcoded background blitting used in every screen.
 * Children are rendered at their own (absolute) coordinates — this Panel
 * does NOT reposition them. For automatic positioning, use [HBox] or [VBox].
 * 
 * 
 * To use as a background panel in a screen:
 * <pre>`Panel panel = new Panel(this.leftPos, this.topPos, this.imageWidth, this.imageHeight); panel.setBackground(TEXTURE_BG); this.addRenderableWidget(panel); `</pre>
 * This replaces the `blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight)` call in `renderBg()`.
 */
open class Panel
/**
 * @param x      screen-absolute X
 * @param y      screen-absolute Y
 * @param width  panel width
 * @param height panel height
 */
    (x: Int, y: Int, width: Int, height: Int) : AbstractWidget(x, y, width, height, Component.literal("")) {
    val children: MutableList<AbstractWidget> = ArrayList<AbstractWidget>()

    // ---- Background ----
    var background: ResourceLocation? = null

    // ---- Children ----
    open fun addChild(child: AbstractWidget?): Panel? {
        children.add(child!!)
        return this
    }

    fun removeChild(child: AbstractWidget?) {
        children.remove(child)
    }

    fun clearChildren() {
        children.clear()
    }

    // ---- Rendering ----
    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        // Background
        if (background != null) {
            graphics.blit(background, getX(), getY(), 0f, 0f, width, height, 256, 256)
        }

        // Children (rendered at their own absolute coordinates)
        for (child in children) {
            if (child.visible) {
                child.render(graphics, mouseX, mouseY, partialTick)
            }
        }
    }

    // ---- Interaction forwarding ----
    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        // Try children first (reverse order so topmost gets priority)
        for (i in children.indices.reversed()) {
            if (children.get(i).mouseClicked(mouseX, mouseY, button)) {
                return true
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for (i in children.indices.reversed()) {
            if (children.get(i).mouseReleased(mouseX, mouseY, button)) {
                return true
            }
        }
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
        for (i in children.indices.reversed()) {
            if (children.get(i).mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        for (i in children.indices.reversed()) {
            if (children.get(i).mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                return true
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {
        // Panels don't have their own narration
    }
}
