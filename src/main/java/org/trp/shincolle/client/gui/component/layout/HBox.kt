package org.trp.shincolle.client.gui.component.layout

import net.minecraft.client.gui.components.AbstractWidget

/**
 * A horizontal box layout that arranges children left-to-right with uniform spacing.
 * 
 * 
 * [.addChild] automatically positions each child at the correct X offset.
 * The panel height is the maximum child height; width auto-expands as children are added.
 * 
 * 
 * Unlike vanilla `LinearLayout`, this integrates with [Panel]'s
 * background rendering and interaction forwarding.
 * 
 * 
 * Usage:
 * <pre>`HBox toolbar = new HBox(this.leftPos + 10, this.topPos + 10, 4); toolbar.addChild(new IconButton(...)); toolbar.addChild(new IconButton(...)); toolbar.addChild(new IconButton(...)); this.addRenderableWidget(toolbar); `</pre>
 */
class HBox
/**
 * @param x        screen-absolute X
 * @param y        screen-absolute Y
 * @param spacing  horizontal gap between children (pixels)
 */(private var nextX: Int, y: Int, val spacing: Int) : Panel(nextX, y, 0, 0) {
    /**
     * Add a child, automatically positioning it at the next horizontal slot.
     * The panel's width grows to accommodate the child; height adjusts if necessary.
     */
    override fun addChild(child: AbstractWidget?): HBox {
        child ?: return this
        // Position child at the current slot
        child.setX(nextX)
        child.setY(getY())

        // Advance slot
        nextX += child.getWidth() + spacing

        // Expand panel bounds
        val rightEdge = child.x + child.getWidth()
        if (rightEdge > getX() + width) {
            this.width = rightEdge - getX()
        }
        if (child.getHeight() > this.height) {
            this.height = child.getHeight()
        }

        super.addChild(child)
        return this
    }

    /**
     * Set the Y position for all existing children.
     */
    override fun setY(y: Int) {
        super.setY(y)
        for (child in children) {
            child.setY(y)
        }
    }
}
