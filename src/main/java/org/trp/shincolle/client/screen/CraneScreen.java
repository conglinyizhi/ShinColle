package org.trp.shincolle.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.trp.shincolle.client.gui.component.Sprites;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.menu.CraneMenu;

import java.util.ArrayList;
import java.util.List;

public class CraneScreen extends AbstractContainerScreen<CraneMenu> {
    private static final ResourceLocation TEXTURE = Sprites.T_CRANE;

    public CraneScreen(CraneMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 201;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        
        graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        
        if (menu.isActive()) {
            graphics.blit(TEXTURE, x + 7, y + 6, Sprites.CRANE_BTN_ACTIVE_U, Sprites.CRANE_BTN_ACTIVE_V, Sprites.CRANE_BTN_ACTIVE_W, Sprites.CRANE_BTN_ACTIVE_H);
        }

        
        if (menu.isCheckMetadata()) graphics.blit(TEXTURE, x + 23, y + 22, Sprites.CRANE_CHK_METADATA_U, Sprites.CRANE_CHK_METADATA_V, Sprites.CRANE_CHK_METADATA_W, Sprites.CRANE_CHK_METADATA_H);
        if (menu.isCheckOredict()) graphics.blit(TEXTURE, x + 37, y + 22, Sprites.CRANE_CHK_OREDICT_U, Sprites.CRANE_CHK_OREDICT_V, Sprites.CRANE_CHK_OREDICT_W, Sprites.CRANE_CHK_OREDICT_H);
        if (menu.isCheckNbt()) graphics.blit(TEXTURE, x + 51, y + 22, Sprites.CRANE_CHK_NBT_U, Sprites.CRANE_CHK_NBT_V, Sprites.CRANE_CHK_NBT_W, Sprites.CRANE_CHK_NBT_H);

        
        int redMode = menu.getModeRedstone();
        if (redMode == 1) graphics.blit(TEXTURE, x + 65, y + 22, Sprites.CRANE_RED_MODE1_U, Sprites.CRANE_RED_MODE1_V, Sprites.CRANE_RED_MODE1_W, Sprites.CRANE_RED_MODE1_H);
        else if (redMode == 2) graphics.blit(TEXTURE, x + 65, y + 22, Sprites.CRANE_RED_MODE2_U, Sprites.CRANE_RED_MODE2_V, Sprites.CRANE_RED_MODE2_W, Sprites.CRANE_RED_MODE2_H);

        
        if (!menu.isEnabLoad()) {
            graphics.blit(TEXTURE, x + 7, y + 52, Sprites.CRANE_BTN_DISABLED_U, Sprites.CRANE_BTN_DISABLED_V, Sprites.CRANE_BTN_DISABLED_W, Sprites.CRANE_BTN_DISABLED_H);
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
            com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
            com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            graphics.blit(TEXTURE, x + 8, y + 65, Sprites.CRANE_DISABLED_OVERLAY_U, Sprites.CRANE_DISABLED_OVERLAY_V, Sprites.CRANE_DISABLED_OVERLAY_W, Sprites.CRANE_DISABLED_OVERLAY_H); 
            com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (!menu.isEnabUnload()) {
            graphics.blit(TEXTURE, x + 7, y + 83, Sprites.CRANE_BTN_DISABLED_U, Sprites.CRANE_BTN_DISABLED_V, Sprites.CRANE_BTN_DISABLED_W, Sprites.CRANE_BTN_DISABLED_H);
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
            com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
            com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            graphics.blit(TEXTURE, x + 8, y + 96, Sprites.CRANE_DISABLED_OVERLAY_U, Sprites.CRANE_DISABLED_OVERLAY_V, Sprites.CRANE_DISABLED_OVERLAY_W, Sprites.CRANE_DISABLED_OVERLAY_H); 
            com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        
        int liqMode = menu.getModeLiquid();
        if (liqMode == 0) graphics.blit(TEXTURE, x + 23, y + 36, Sprites.CRANE_LIQ_MODE0_U, Sprites.CRANE_LIQ_MODE0_V, Sprites.CRANE_LIQ_MODE0_W, Sprites.CRANE_LIQ_MODE0_H);
        else if (liqMode == 1) graphics.blit(TEXTURE, x + 23, y + 36, Sprites.CRANE_LIQ_MODE1_U, Sprites.CRANE_LIQ_MODE1_V, Sprites.CRANE_LIQ_MODE1_W, Sprites.CRANE_LIQ_MODE1_H);
        else if (liqMode == 2) graphics.blit(TEXTURE, x + 23, y + 36, Sprites.CRANE_LIQ_MODE2_U, Sprites.CRANE_LIQ_MODE2_V, Sprites.CRANE_LIQ_MODE2_W, Sprites.CRANE_LIQ_MODE2_H);

        int energyMode = menu.getModeEnergy();
        if (energyMode == 0) graphics.blit(TEXTURE, x + 39, y + 36, Sprites.CRANE_ENERGY_MODE0_U, Sprites.CRANE_ENERGY_MODE0_V, Sprites.CRANE_ENERGY_MODE0_W, Sprites.CRANE_ENERGY_MODE0_H);
        else if (energyMode == 1) graphics.blit(TEXTURE, x + 39, y + 36, Sprites.CRANE_ENERGY_MODE1_U, Sprites.CRANE_ENERGY_MODE1_V, Sprites.CRANE_ENERGY_MODE1_W, Sprites.CRANE_ENERGY_MODE1_H);
        else if (energyMode == 2) graphics.blit(TEXTURE, x + 39, y + 36, Sprites.CRANE_ENERGY_MODE2_U, Sprites.CRANE_ENERGY_MODE2_V, Sprites.CRANE_ENERGY_MODE2_W, Sprites.CRANE_ENERGY_MODE2_H);

        
        for (int i = 0; i < 18; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                boolean slotMode = menu.getItemMode(i);
                int sx = x + 7 + (i % 9) * 18;
                int sy = y + (i < 9 ? 64 : 95);
                if (slotMode) {
                    graphics.blit(TEXTURE, sx, sy, Sprites.CRANE_SLOT_ON_U, Sprites.CRANE_SLOT_ON_V, Sprites.CRANE_SLOT_ON_W, Sprites.CRANE_SLOT_ON_H);
                } else {
                    graphics.blit(TEXTURE, sx, sy, Sprites.CRANE_SLOT_OFF_U, Sprites.CRANE_SLOT_OFF_V, Sprites.CRANE_SLOT_OFF_W, Sprites.CRANE_SLOT_OFF_H);
                }
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, (this.imageWidth - this.font.width(this.title)) / 2, 6, 0x404040, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);

        
        String modeStr = getModeString(menu.getCraneMode());
        int len = this.font.width(modeStr) / 2;
        graphics.drawString(this.font, modeStr, 57 - len, 9, 0xFFFF00, true);

        graphics.drawString(this.font, Component.translatable("gui.shincolle.crane.toship"), 21, 54, 0xFF6666, false);
        graphics.drawString(this.font, Component.translatable("gui.shincolle.crane.tochest"), 21, 85, 0x404040, false);

        EntityShipBase ship = menu.getShipEntity();
        if (ship != null) {
            String timeStr = formatTime(menu.getShipTimer());
            int timeLen = this.font.width(timeStr) / 2;
            graphics.drawString(this.font, timeStr, 133 - timeLen, 10, 0x404040, false);

            Component name = ship.hasCustomName()
                    ? ship.getCustomName()
                    : Component.translatable(ship.getType().getDescriptionId());
            graphics.drawString(this.font, name, 80, 24, 0xFFFFFF, true);
        }
    }

    private static String formatTime(int ticks) {
        int sec = (int) (ticks * 0.05f);
        int hours = sec / 3600;
        int minutes = (sec % 3600) / 60;
        int seconds = sec % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private String getModeString(int mode) {
        if (mode == 0) return Component.translatable("gui.shincolle.crane.nowait").getString();
        if (mode == 1) return Component.translatable("gui.shincolle.crane.untilfull").getString();
        if (mode == 2) return Component.translatable("gui.shincolle.crane.untilempty").getString();
        if (mode == 3) return Component.translatable("gui.shincolle.crane.excess").getString();
        if (mode == 4) return Component.translatable("gui.shincolle.crane.remain").getString();
        
        float time;
        if (mode < 10) {
            time = (mode - 4) * 0.5f;
            return String.format("%.1f s", time);
        } else if (mode < 15) {
            time = (mode - 9) * 10f;
            return String.format("%.1f s", time);
        } else {
            time = (mode - 14);
            return String.format("%.0f min", time);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int xClick = (int) (mouseX - leftPos);
        int yClick = (int) (mouseY - topPos);

        
        
        if (inside(xClick, yClick, 7, 6, 20, 19)) {
            sendButton(0);
            return true;
        }
        
        if (inside(xClick, yClick, 22, 5, 91, 20)) {
            sendButton(button == 1 ? 11 : 1); 
            return true;
        }
        
        if (inside(xClick, yClick, 23, 22, 34, 33)) {
            sendButton(2);
            return true;
        }
        
        if (inside(xClick, yClick, 37, 22, 48, 33)) {
            sendButton(3);
            return true;
        }
        
        if (inside(xClick, yClick, 51, 22, 62, 33)) {
            sendButton(6);
            return true;
        }
        
        if (inside(xClick, yClick, 65, 22, 76, 33)) {
            sendButton(7);
            return true;
        }
        
        if (inside(xClick, yClick, 7, 52, 18, 63)) {
            sendButton(4);
            return true;
        }
        
        if (inside(xClick, yClick, 7, 83, 18, 94)) {
            sendButton(5);
            return true;
        }
        
        if (inside(xClick, yClick, 23, 36, 36, 49)) {
            sendButton(8);
            return true;
        }
        if (inside(xClick, yClick, 39, 36, 52, 49)) {
            sendButton(9);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void sendButton(int id) {
        if (Minecraft.getInstance().gameMode != null) {
            Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, id);
        }
    }

    private static boolean inside(int x, int y, int x1, int y1, int x2, int y2) {
        return x >= x1 && x < x2 && y >= y1 && y < y2;
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        int mx = mouseX - leftPos;
        int my = mouseY - topPos;

        List<Component> tooltip = new ArrayList<>();
        if (my > 21 && my < 34) {
            if (mx > 22 && mx < 35) tooltip.add(Component.translatable("gui.shincolle.crane.usemeta"));
            else if (mx > 36 && mx < 49) tooltip.add(Component.translatable("gui.shincolle.crane.useoredict"));
            else if (mx > 50 && mx < 63) tooltip.add(Component.translatable("gui.shincolle.crane.usenbt"));
            else if (mx > 64 && mx < 77) {
                int r = menu.getModeRedstone();
                tooltip.add(Component.translatable("gui.shincolle.crane.red" + r));
            }
        } else if (my > 35 && my < 50) {
            if (mx > 22 && mx < 37) {
                int l = menu.getModeLiquid();
                tooltip.add(Component.translatable("gui.shincolle.crane.liquid" + l));
            } else if (mx > 38 && mx < 53) {
                int e = menu.getModeEnergy();
                tooltip.add(Component.translatable("gui.shincolle.crane.energy" + e));
            }
        }

        if (mx > 22 && mx < 91 && my > 5 && my < 20) {
            int m = menu.getCraneMode();
            if (m == 0) tooltip.add(Component.translatable("gui.shincolle.crane.nowait1"));
            else if (m == 1) {
                tooltip.add(Component.translatable("gui.shincolle.crane.untilfull1"));
                tooltip.add(Component.translatable("gui.shincolle.crane.untilfull2"));
            } else if (m == 2) {
                tooltip.add(Component.translatable("gui.shincolle.crane.untilempty1"));
                tooltip.add(Component.translatable("gui.shincolle.crane.untilempty2"));
            } else if (m == 3) {
                tooltip.add(Component.translatable("gui.shincolle.crane.excess1"));
                tooltip.add(Component.translatable("gui.shincolle.crane.excess2"));
            } else if (m == 4) {
                tooltip.add(Component.translatable("gui.shincolle.crane.remain1"));
                tooltip.add(Component.translatable("gui.shincolle.crane.remain2"));
            }
        }

        if (!tooltip.isEmpty()) {
            graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }
}
