package org.trp.shincolle.client.gui.component.layout

import net.minecraft.client.gui.components.AbstractWidget

/**
 * A vertical box layout that arranges children top-to-bottom with uniform spacing.
 * 
 * 
 * [.addChild] automatically positions each child at the correct Y offset.
 * The panel width is the maximum child width; height auto-expands as children are added.
 * 
 * 
 * Unlike vanilla `LinearLayout`, this integrates with [Panel]'s
 * background rendering and interaction forwarding.
 * 
 * 
 * Usage:
 * <pre>`VBox list = new VBox(this.leftPos + 10, this.topPos + 10, 2); list.addChild(new IconButton(...)); list.addChild(new IconButton(...)); this.addRenderableWidget(list); `</pre>
 */
class VBox
/**
 * @param x        screen-absolute X
 * @param y        screen-absolute Y
 * @param spacing  vertical gap between children (pixels)
 */(x: Int, private var nextY: Int, val spacing: Int) : Panel(
    x,
    nextY, 0, 0
) {
    /**
     * Add a child, automatically positioning it at the next vertical slot.
     */
    override fun addChild(child: AbstractWidget): VBox {
        // Position child at the current slot
        child.setX(getX())
        child.setY(nextY)

        // Advance slot
        nextY += child.getHeight() + spacing

        // Expand panel bounds
        val bottomEdge = child.getY() + child.getHeight()
        if (bottomEdge > getY() + height) {
            this.height = bottomEdge - getY()
        }
        if (child.getWidth() > this.width) {
            this.width = child.getWidth()
        }

        super.addChild(child)
        return this
    }

    /**
     * Set the X position for all existing children.
     */
    override fun setX(x: Int) {
        super.setX(x)
        for (child in children) {
            child.setX(x)
        }
    }
}
