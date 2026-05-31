package org.trp.shincolle.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.trp.shincolle.client.gui.component.Sprites;
import org.trp.shincolle.menu.RecipePaperMenu;

public class RecipePaperScreen extends AbstractContainerScreen<RecipePaperMenu> {

    private static final ResourceLocation TEXTURE = Sprites.T_RECIPE_PAPER;
    private static final int SECTION_COLOR = 0xFFF1C8;
    private static final int SECTION_SHADOW_COLOR = 0x6A5640;
    private static final int LABEL_COLOR = 0x404040;

    public RecipePaperScreen(RecipePaperMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, LABEL_COLOR, false);
        drawSectionLabel(guiGraphics, Component.translatable("gui.shincolle.recipepaper.material"), 29, 6);
        drawSectionLabel(guiGraphics, Component.translatable("gui.shincolle.recipepaper.result"), 114, 24);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, LABEL_COLOR, false);
    }

    private void drawSectionLabel(GuiGraphics guiGraphics, Component text, int x, int y) {
        guiGraphics.drawString(this.font, text, x + 1, y + 1, SECTION_SHADOW_COLOR, false);
        guiGraphics.drawString(this.font, text, x, y, SECTION_COLOR, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
