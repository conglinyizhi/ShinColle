package org.trp.shincolle.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.menu.VolCoreMenu;

import java.util.ArrayList;
import java.util.List;

public class VolCoreScreen extends AbstractContainerScreen<VolCoreMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/guivolcore.png");

    public VolCoreScreen(VolCoreMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(GUI_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (menu.isBtnActive()) {
            graphics.blit(GUI_TEXTURE, leftPos + 7, topPos + 6, 12, 166, 13, 13);
        }

        int power = menu.getRemainedPower();
        if (power > 0) {
            int scaleBar = (int) (power * 31.0 / 9600.0);
            graphics.blit(GUI_TEXTURE, leftPos + 38, topPos + 59 - scaleBar, 0, 197 - scaleBar, 12, scaleBar);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.imageWidth / 2 - this.font.width(this.title) / 2, 6, 0x404040, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);

        if (isHovering(36, 27, 16, 34, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal(String.valueOf(menu.getRemainedPower())));
            graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int xClick = (int) (mouseX - leftPos);
        int yClick = (int) (mouseY - topPos);

        if (xClick >= 7 && xClick <= 20 && yClick >= 6 && yClick <= 19) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
