package org.trp.shincolle.client.gui.component.layout;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;


import java.util.ArrayList;
import java.util.List;

/**
 * A rectangular container widget with an optional background texture.
 * <p>
 * Serves as a base class for layout containers ({@link HBox}, {@link VBox})
 * and as a stand-in for the hardcoded background blitting used in every screen.
 * Children are rendered at their own (absolute) coordinates — this Panel
 * does NOT reposition them. For automatic positioning, use {@link HBox} or {@link VBox}.
 * <p>
 * To use as a background panel in a screen:
 * <pre>{@code
 * Panel panel = new Panel(this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
 * panel.setBackground(TEXTURE_BG);
 * this.addRenderableWidget(panel);
 * }</pre>
 * This replaces the {@code blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight)} call in {@code renderBg()}.
 */
public class Panel extends AbstractWidget {

    protected final List<AbstractWidget> children = new ArrayList<>();
    protected ResourceLocation background;

    /**
     * @param x      screen-absolute X
     * @param y      screen-absolute Y
     * @param width  panel width
     * @param height panel height
     */
    public Panel(int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal(""));
    }

    // ---- Background ----
    public void setBackground(ResourceLocation background) {
        this.background = background;
    }

    public ResourceLocation getBackground() {
        return background;
    }

    // ---- Children ----

    public Panel addChild(AbstractWidget child) {
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

    // ---- Rendering ----

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Background
        if (background != null) {
            graphics.blit(background, getX(), getY(), 0, 0, width, height, 256, 256);
        }

        // Children (rendered at their own absolute coordinates)
        for (AbstractWidget child : children) {
            if (child.visible) {
                child.render(graphics, mouseX, mouseY, partialTick);
            }
        }
    }

    // ---- Interaction forwarding ----

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Try children first (reverse order so topmost gets priority)
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        // Panels don't have their own narration
    }
}
