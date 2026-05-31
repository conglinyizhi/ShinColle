package org.trp.shincolle.client.gui.component.layout;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

/**
 * A vertical box layout that arranges children top-to-bottom with uniform spacing.
 * <p>
 * {@link #addChild} automatically positions each child at the correct Y offset.
 * The panel width is the maximum child width; height auto-expands as children are added.
 * <p>
 * Unlike vanilla {@code LinearLayout}, this integrates with {@link Panel}'s
 * background rendering and interaction forwarding.
 * <p>
 * Usage:
 * <pre>{@code
 * VBox list = new VBox(this.leftPos + 10, this.topPos + 10, 2);
 * list.addChild(new IconButton(...));
 * list.addChild(new IconButton(...));
 * this.addRenderableWidget(list);
 * }</pre>
 */
public class VBox extends Panel {

    private final int spacing;
    private int nextY;

    /**
     * @param x        screen-absolute X
     * @param y        screen-absolute Y
     * @param spacing  vertical gap between children (pixels)
     */
    public VBox(int x, int y, int spacing) {
        super(x, y, 0, 0);
        this.spacing = spacing;
        this.nextY = y;
    }

    /**
     * Add a child, automatically positioning it at the next vertical slot.
     */
    @Override
    public VBox addChild(AbstractWidget child) {
        // Position child at the current slot
        child.setX(getX());
        child.setY(nextY);

        // Advance slot
        nextY += child.getHeight() + spacing;

        // Expand panel bounds
        int bottomEdge = child.getY() + child.getHeight();
        if (bottomEdge > getY() + height) {
            this.height = bottomEdge - getY();
        }
        if (child.getWidth() > this.width) {
            this.width = child.getWidth();
        }

        super.addChild(child);
        return this;
    }

    /**
     * Set the X position for all existing children.
     */
    @Override
    public void setX(int x) {
        super.setX(x);
        for (AbstractWidget child : children) {
            child.setX(x);
        }
    }

    public int getSpacing() {
        return spacing;
    }
}
