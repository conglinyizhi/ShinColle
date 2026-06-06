package org.trp.shincolle.client.gui.component

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import org.trp.shincolle.client.gui.component.layout.VBox
import kotlin.math.max
import kotlin.math.min

/**
 * A scrollable container that clips its children to the panel bounds
 * and supports vertical scrolling via mouse wheel or drag.
 * 
 * 
 * Replaces the hardcoded scrolling logic in `DeskScreen` and
 * long-list rendering in other screens. Uses [GuiGraphics.enableScissor]
 * for proper clipping.
 * 
 * 
 * Children are NOT automatically positioned (use [VBox]
 * for automatic layout inside the panel). Each child's absolute Y determines
 * its visible position after scroll offset adjustment.
 * 
 * 
 * Usage:
 * <pre>`ScrollPanel panel = new ScrollPanel(x, y, width, height); panel.setContentHeight(totalContentHeight); panel.addChild(new IconButton(...)); panel.addChild(new ShipSlot(...)); this.addRenderableWidget(panel); `</pre>
 */
class ScrollPanel
/**
 * @param x       screen-absolute X position
 * @param y       screen-absolute Y position
 * @param width   panel width
 * @param height  panel height
 */
    (x: Int, y: Int, width: Int, height: Int) : AbstractWidget(x, y, width, height, Component.literal("")) {
    val children: MutableList<AbstractWidget> = ArrayList<AbstractWidget>()
    protected var contentHeight: Int = 0
        set(value) {
            field = value
            clampScrollOffset()
        }
    protected var scrollOffset: Int = 0
        set(value) {
            field = value
            clampScrollOffset()
        }
    protected var scrollBarWidth: Int = 6
    protected var draggingScrollbar: Boolean = false
    protected var dragStartY: Int = 0
    protected var dragStartOffset: Int = 0

    // ---- Content ----
    /** Set the total height of the scrollable content (in pixels).  */
    fun addChild(child: AbstractWidget?): ScrollPanel {
        children.add(child!!)
        return this
    }

    fun removeChild(child: AbstractWidget?) {
        children.remove(child)
    }

    fun clearChildren() {
        children.clear()
    }

    // ---- Scrolling ----
    protected fun clampScrollOffset() {
        val maxOffset = max(0, contentHeight - height)
        scrollOffset = max(0, min(scrollOffset, maxOffset))
    }

    /** Scroll by a relative delta (positive = down).  */
    fun scrollBy(delta: Int) {
        scrollOffset += delta
    }

    /** Convert a screen Y coordinate to a content Y coordinate.  */
    protected fun screenToContentY(screenY: Int): Int {
        return screenY - getY() + scrollOffset
    }

    // ---- Scroll bar visibility & dimensions ----
    protected fun needsScrollBar(): Boolean {
        return contentHeight > height
    }

    protected val scrollBarHeight: Int
        get() {
            if (contentHeight <= 0) return height
            return max(12, height * height / contentHeight)
        }

    protected val scrollBarY: Int
        get() {
            if (!needsScrollBar()) return getY()
            val availableTrack = height - this.scrollBarHeight
            val progress = scrollOffset.toFloat() / max(1, contentHeight - height).toFloat()
            return getY() + (progress * availableTrack).toInt()
        }

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        // ---- Background ----
        graphics.fill(getX(), getY(), getX() + width, getY() + height, 0x40000000)

        // ---- Clipped children ----
        graphics.enableScissor(getX(), getY(), getX() + width, getY() + height)

        for (child in children) {
            if (!child.visible) continue
            val childAbsY = child.getY()
            val childBottom = childAbsY + child.getHeight()

            // Transform child position for scroll offset
            child.setY(child.getY() - scrollOffset) // will be restored below
            // Only render if visible in the scissor rect
            if (child.getY() + child.getHeight() > getY() && child.getY() < getY() + height) {
                child.render(graphics, mouseX, mouseY, partialTick)
            }
            child.setY(childAbsY) // restore
        }

        graphics.disableScissor()

        // ---- Scroll bar ----
        if (needsScrollBar()) {
            val sbX = getX() + width - scrollBarWidth
            val sbY = this.scrollBarY
            val sbH = this.scrollBarHeight
            // Track
            graphics.fill(sbX, getY(), sbX + scrollBarWidth, getY() + height, 0x40FFFFFF)
            // Thumb
            val thumbColor = if (isHoveredOrFocused() || draggingScrollbar) -0x33000001 else -0x7f000001
            graphics.fill(sbX, sbY, sbX + scrollBarWidth, sbY + sbH, thumbColor)
        }
    }

    // ---- Mouse interaction ----
    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!isMouseOver(mouseX, mouseY)) return false

        // Check scroll bar click
        if (needsScrollBar() && button == 0) {
            val sbX = getX() + width - scrollBarWidth
            val sbY = this.scrollBarY
            val sbH = this.scrollBarHeight
            if (mouseX >= sbX && mouseX < sbX + scrollBarWidth && mouseY >= sbY && mouseY < sbY + sbH) {
                draggingScrollbar = true
                dragStartY = mouseY.toInt()
                dragStartOffset = scrollOffset
                return true
            }
            // Scroll track click = jump to position
            if (mouseX >= sbX && mouseX < sbX + scrollBarWidth && mouseY >= getY() && mouseY < getY() + height) {
                val clickRatio = (mouseY - getY()).toFloat() / height.toFloat()
                scrollOffset = (clickRatio * contentHeight - height / 2.0f).toInt()
                return true
            }
        }

        // Try children (reverse order for topmost priority)
        for (i in children.indices.reversed()) {
            val child = children.get(i)
            val origY = child.getY()
            child.setY(origY - scrollOffset)
            val handled = child.mouseClicked(mouseX, mouseY, button)
            child.setY(origY)
            if (handled) return true
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (draggingScrollbar && button == 0) {
            draggingScrollbar = false
            return true
        }
        for (child in children) {
            val origY = child.getY()
            child.setY(origY - scrollOffset)
            child.mouseReleased(mouseX, mouseY, button)
            child.setY(origY)
        }
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
        if (draggingScrollbar && button == 0) {
            val deltaY = mouseY.toInt() - dragStartY
            val scrollRange = (contentHeight - height).toFloat()
            val dragRange = (height - this.scrollBarHeight).toFloat()
            if (dragRange > 0) {
                val deltaScroll = (deltaY * scrollRange / dragRange).toInt()
                scrollOffset = dragStartOffset + deltaScroll
            }
            return true
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        if (isMouseOver(mouseX, mouseY)) {
            scrollBy((-scrollY * 12).toInt())
            return true
        }
        return false
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {
        // ScrollPanel doesn't have its own narration
    }
}
