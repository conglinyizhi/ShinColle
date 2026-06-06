package org.trp.shincolle.client.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.trp.shincolle.client.gui.component.Sprites
import org.trp.shincolle.menu.RecipePaperMenu

class RecipePaperScreen(menu: RecipePaperMenu, inventory: Inventory, title: Component) :
    AbstractContainerScreen<RecipePaperMenu>(menu, inventory, title) {

    init {
        this.imageWidth = 176
        this.imageHeight = 166
        this.titleLabelY = 6
        this.inventoryLabelX = 8
        this.inventoryLabelY = this.imageHeight - 94
    }

    override fun init() {
        super.init()
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick)
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        this.renderTooltip(guiGraphics, mouseX, mouseY)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val x = (this.width - this.imageWidth) / 2
        val y = (this.height - this.imageHeight) / 2
        guiGraphics.blit(TEXTURE, x, y, 0f, 0f, this.imageWidth, this.imageHeight, 256, 256)
    }

    override fun renderLabels(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, LABEL_COLOR, false)
        drawSectionLabel(guiGraphics, Component.translatable("gui.shincolle.recipepaper.material"), 29, 6)
        drawSectionLabel(guiGraphics, Component.translatable("gui.shincolle.recipepaper.result"), 114, 24)
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, LABEL_COLOR, false)
    }

    private fun drawSectionLabel(guiGraphics: GuiGraphics, text: Component, x: Int, y: Int) {
        guiGraphics.drawString(this.font, text, x + 1, y + 1, SECTION_SHADOW_COLOR, false)
        guiGraphics.drawString(this.font, text, x, y, SECTION_COLOR, false)
    }

    companion object {
        private val TEXTURE: ResourceLocation = Sprites.T_RECIPE_PAPER
        private const val SECTION_COLOR = 0xFFF1C8
        private const val SECTION_SHADOW_COLOR = 0x6A5640
        private const val LABEL_COLOR = 0x404040
    }
}