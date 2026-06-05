package org.trp.shincolle.client.gui.component

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

/**
 * A horizontal tab strip widget.
 * 
 * 
 * Renders tabs as colored rectangles with centered labels.
 * Supports an active tab with distinct visual state.
 * Replaces the old detail-tab IconButton group in `ShipInventoryScreen`.
 */
class TabPanel
/**
 * @param x      screen-absolute X position
 * @param y      screen-absolute Y position
 * @param width  total width of the tab strip (divided equally among tabs)
 * @param height tab strip height
 */
    (x: Int, y: Int, width: Int, height: Int) : AbstractWidget(x, y, width, height, Component.literal("")) {
    private val tabs: MutableList<Tab> = ArrayList<Tab>()
    var activeTabId: Int = 0

    /**
     * Add a tab to the strip. Tabs are rendered left-to-right in order of addition.
     * 
     * @param label   display label
     * @param id      identifier returned by [.getActiveTabId]
     * @param onSelect callback invoked when this tab is clicked
     */
    fun addTab(label: Component?, id: Int, onSelect: Runnable?) {
        tabs.add(Tab(label, id, onSelect))
    }

    override fun renderWidget(g: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (tabs.isEmpty()) return

        val tabWidth = width / tabs.size
        val font = Minecraft.getInstance().font

        for (i in tabs.indices) {
            val tab = tabs.get(i)
            val tx = getX() + i * tabWidth
            val ty = getY()

            val hovered = mouseX >= tx && mouseX < tx + tabWidth && mouseY >= ty && mouseY < ty + height
            val active = tab.id == activeTabId

            val bgColor: Int
            if (active) {
                bgColor = -0x333334
            } else if (hovered) {
                bgColor = -0x555556
            } else {
                bgColor = -0x777778
            }

            g.fill(tx, ty, tx + tabWidth, ty + height, bgColor)

            // Draw label centered
            val textX = tx + (tabWidth - font.width(tab.label)) / 2
            val textY = ty + (height - font.lineHeight) / 2 + 1
            g.drawString(font, tab.label, textX, textY, -0x1, false)
        }
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        if (tabs.isEmpty()) return
        val tabWidth = width / tabs.size
        val index = ((mouseX - getX()) / tabWidth).toInt()
        if (index >= 0 && index < tabs.size) {
            val tab = tabs.get(index)
            this.activeTabId = tab.id
            tab.onSelect!!.run()
        }
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {
        this.defaultButtonNarrationText(output)
    }

    @JvmRecord
    private data class Tab(val label: Component?, val id: Int, val onSelect: Runnable?)
}
