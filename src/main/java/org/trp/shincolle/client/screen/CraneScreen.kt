package org.trp.shincolle.client.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.trp.shincolle.menu.CraneMenu

class CraneScreen(menu: CraneMenu, playerInventory: Inventory, title: Component) :
    AbstractContainerScreen<CraneMenu>(menu, playerInventory, title) {
    init {
        this.imageWidth = 176
        this.imageHeight = 166
    }
    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {}
}
