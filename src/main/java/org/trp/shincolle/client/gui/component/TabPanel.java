package org.trp.shincolle.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A horizontal tab strip widget.
 * <p>
 * Renders tabs as colored rectangles with centered labels.
 * Supports an active tab with distinct visual state.
 * Replaces the old detail-tab IconButton group in {@code ShipInventoryScreen}.
 */
public class TabPanel extends AbstractWidget {

    private final List<Tab> tabs = new ArrayList<>();
    private int activeTabId;

    /**
     * @param x      screen-absolute X position
     * @param y      screen-absolute Y position
     * @param width  total width of the tab strip (divided equally among tabs)
     * @param height tab strip height
     */
    public TabPanel(int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal(""));
    }

    /**
     * Add a tab to the strip. Tabs are rendered left-to-right in order of addition.
     *
     * @param label   display label
     * @param id      identifier returned by {@link #getActiveTabId()}
     * @param onSelect callback invoked when this tab is clicked
     */
    public void addTab(Component label, int id, Runnable onSelect) {
        tabs.add(new Tab(label, id, onSelect));
    }

    public int getActiveTabId() {
        return activeTabId;
    }

    public void setActiveTabId(int activeTabId) {
        this.activeTabId = activeTabId;
    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        if (tabs.isEmpty()) return;

        int tabWidth = width / tabs.size();
        Font font = Minecraft.getInstance().font;

        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);
            int tx = getX() + i * tabWidth;
            int ty = getY();

            boolean hovered = mouseX >= tx && mouseX < tx + tabWidth
                    && mouseY >= ty && mouseY < ty + height;
            boolean active = tab.id() == activeTabId;

            int bgColor;
            if (active) {
                bgColor = 0xFFCCCCCC;
            } else if (hovered) {
                bgColor = 0xFFAAAAAA;
            } else {
                bgColor = 0xFF888888;
            }

            g.fill(tx, ty, tx + tabWidth, ty + height, bgColor);

            // Draw label centered
            int textX = tx + (tabWidth - font.width(tab.label())) / 2;
            int textY = ty + (height - font.lineHeight) / 2 + 1;
            g.drawString(font, tab.label(), textX, textY, 0xFFFFFFFF, false);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (tabs.isEmpty()) return;
        int tabWidth = width / tabs.size();
        int index = (int) ((mouseX - getX()) / tabWidth);
        if (index >= 0 && index < tabs.size()) {
            Tab tab = tabs.get(index);
            this.activeTabId = tab.id();
            tab.onSelect().run();
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    private record Tab(Component label, int id, Runnable onSelect) {}
}
