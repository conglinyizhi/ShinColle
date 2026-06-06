package org.trp.shincolle.client.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.trp.shincolle.menu.RecipePaperMenu

class RecipePaperScreen(menu: RecipePaperMenu, inventory: Inventory, title: Component) :
    AbstractContainerScreen<RecipePaperMenu>(menu, inventory, title) {
    init {
        this.imageWidth = 176
        this.imageHeight = 166
    }
    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {}
}
