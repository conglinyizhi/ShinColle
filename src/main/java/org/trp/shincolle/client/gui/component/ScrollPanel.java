package org.trp.shincolle.client.gui.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A scrollable container that clips its children to the panel bounds
 * and supports vertical scrolling via mouse wheel or drag.
 * <p>
 * Replaces the hardcoded scrolling logic in {@code DeskScreen} and
 * long-list rendering in other screens. Uses {@link GuiGraphics#enableScissor}
 * for proper clipping.
 * <p>
 * Children are NOT automatically positioned (use {@link org.trp.shincolle.client.gui.component.layout.VBox}
 * for automatic layout inside the panel). Each child's absolute Y determines
 * its visible position after scroll offset adjustment.
 * <p>
 * Usage:
 * <pre>{@code
 * ScrollPanel panel = new ScrollPanel(x, y, width, height);
 * panel.setContentHeight(totalContentHeight);
 * panel.addChild(new IconButton(...));
 * panel.addChild(new ShipSlot(...));
 * this.addRenderableWidget(panel);
 * }</pre>
 */
public class ScrollPanel extends AbstractWidget {

    protected final List<AbstractWidget> children = new ArrayList<>();
    protected int contentHeight;
    protected int scrollOffset;
    protected int scrollBarWidth = 6;
    protected boolean draggingScrollbar;
    protected int dragStartY;
    protected int dragStartOffset;

    /**
     * @param x       screen-absolute X position
     * @param y       screen-absolute Y position
     * @param width   panel width
     * @param height  panel height
     */
    public ScrollPanel(int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal(""));
    }

    // ---- Content ----

    /** Set the total height of the scrollable content (in pixels). */
    public void setContentHeight(int contentHeight) {
        this.contentHeight = contentHeight;
        clampScrollOffset();
    }

    public int getContentHeight() {
        return contentHeight;
    }

    public ScrollPanel addChild(AbstractWidget child) {
        children.add(child);
        return this;
    }

    public void removeChild(AbstractWidget child) {
        children.remove(child);
    }

    public void clearChildren() {
        children.clear();
    }

    public List<AbstractWidget> getChildren() {
        return children;
    }

    // ---- Scrolling ----

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setScrollOffset(int offset) {
        this.scrollOffset = offset;
        clampScrollOffset();
    }

    protected void clampScrollOffset() {
        int maxOffset = Math.max(0, contentHeight - height);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxOffset));
    }

    /** Scroll by a relative delta (positive = down). */
    public void scrollBy(int delta) {
        setScrollOffset(scrollOffset + delta);
    }

    /** Convert a screen Y coordinate to a content Y coordinate. */
    protected int screenToContentY(int screenY) {
        return screenY - getY() + scrollOffset;
    }

    // ---- Scroll bar visibility & dimensions ----

    protected boolean needsScrollBar() {
        return contentHeight > height;
    }

    protected int getScrollBarHeight() {
        if (contentHeight <= 0) return height;
        return Math.max(12, height * height / contentHeight);
    }

    protected int getScrollBarY() {
        if (!needsScrollBar()) return getY();
        int availableTrack = height - getScrollBarHeight();
        float progress = (float) scrollOffset / (float) Math.max(1, contentHeight - height);
        return getY() + (int) (progress * availableTrack);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // ---- Background ----
        graphics.fill(getX(), getY(), getX() + width, getY() + height, 0x40000000);

        // ---- Clipped children ----
        graphics.enableScissor(getX(), getY(), getX() + width, getY() + height);

        for (AbstractWidget child : children) {
            if (!child.visible) continue;
            int childAbsY = child.getY();
            int childBottom = childAbsY + child.getHeight();

            // Transform child position for scroll offset
            child.setY(child.getY() - scrollOffset); // will be restored below
            // Only render if visible in the scissor rect
            if (child.getY() + child.getHeight() > getY() && child.getY() < getY() + height) {
                child.render(graphics, mouseX, mouseY, partialTick);
            }
            child.setY(childAbsY); // restore
        }

        graphics.disableScissor();

        // ---- Scroll bar ----
        if (needsScrollBar()) {
            int sbX = getX() + width - scrollBarWidth;
            int sbY = getScrollBarY();
            int sbH = getScrollBarHeight();
            // Track
            graphics.fill(sbX, getY(), sbX + scrollBarWidth, getY() + height, 0x40FFFFFF);
            // Thumb
            int thumbColor = (isHoveredOrFocused() || draggingScrollbar) ? 0xCCFFFFFF : 0x80FFFFFF;
            graphics.fill(sbX, sbY, sbX + scrollBarWidth, sbY + sbH, thumbColor);
        }
    }

    // ---- Mouse interaction ----

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) return false;

        // Check scroll bar click
        if (needsScrollBar() && button == 0) {
            int sbX = getX() + width - scrollBarWidth;
            int sbY = getScrollBarY();
            int sbH = getScrollBarHeight();
            if (mouseX >= sbX && mouseX < sbX + scrollBarWidth
                    && mouseY >= sbY && mouseY < sbY + sbH) {
                draggingScrollbar = true;
                dragStartY = (int) mouseY;
                dragStartOffset = scrollOffset;
                return true;
            }
            // Scroll track click = jump to position
            if (mouseX >= sbX && mouseX < sbX + scrollBarWidth
                    && mouseY >= getY() && mouseY < getY() + height) {
                float clickRatio = (float) (mouseY - getY()) / (float) height;
                setScrollOffset((int) (clickRatio * contentHeight - height / 2.0f));
                return true;
            }
        }

        // Try children (reverse order for topmost priority)
        for (int i = children.size() - 1; i >= 0; i--) {
            AbstractWidget child = children.get(i);
            int origY = child.getY();
            child.setY(origY - scrollOffset);
            boolean handled = child.mouseClicked(mouseX, mouseY, button);
            child.setY(origY);
            if (handled) return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingScrollbar && button == 0) {
            draggingScrollbar = false;
            return true;
        }
        for (AbstractWidget child : children) {
            int origY = child.getY();
            child.setY(origY - scrollOffset);
            child.mouseReleased(mouseX, mouseY, button);
            child.setY(origY);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingScrollbar && button == 0) {
            int deltaY = (int) mouseY - dragStartY;
            float scrollRange = contentHeight - height;
            float dragRange = height - getScrollBarHeight();
            if (dragRange > 0) {
                int deltaScroll = (int) (deltaY * scrollRange / dragRange);
                setScrollOffset(dragStartOffset + deltaScroll);
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isMouseOver(mouseX, mouseY)) {
            scrollBy((int) (-scrollY * 12));
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        // ScrollPanel doesn't have its own narration
    }
}
