package org.trp.shincolle.client.gui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.joml.Quaternionf;

import org.trp.shincolle.entity.base.EntityShipBase;

/**
 * A widget that renders a ship entity preview with status bars.
 * <p>
 * Replaces the hardcoded ship slot rendering in {@code FormationScreen} and
 * ship list entries in {@code DeskScreen}. Renders:
 * <ul>
 *   <li>3D ship entity preview (using {@link EntityRenderDispatcher})</li>
 *   <li>Health bar (green → red based on HP ratio)</li>
 *   <li>Ship name label above the slot</li>
 *   <li>Click handling for ship selection</li>
 * </ul>
 * <p>
 * Usage:
 * <pre>{@code
 * ShipSlot slot = new ShipSlot(x, y, 48, 64, ship, () -> selectShip(ship));
 * this.addRenderableWidget(slot);
 * }</pre>
 * <p>
 * The entity preview uses the same rendering technique as
 * {@code ShipInventoryScreen.renderEntityWithPassengers()}. The model is
 * centered in the widget bounds and automatically scaled to fit.
 */
public class ShipSlot extends AbstractWidget {

    private static final int BAR_WIDTH = 40;
    private static final int BAR_HEIGHT = 3;
    private static final int BAR_SPACING = 5;
    private static final int BAR_Y_OFFSET = 4;

    private final LivingEntity ship;
    private final Runnable onPress;
    private float modelScale = 24.0f;

    /**
     * @param x        screen-absolute X position
     * @param y        screen-absolute Y position
     * @param width    slot width
     * @param height   slot height
     * @param ship     the ship entity to display
     * @param onPress  optional click callback (null for non-interactive)
     */
    public ShipSlot(int x, int y, int width, int height, LivingEntity ship, Runnable onPress) {
        super(x, y, width, height, Component.literal(""));
        this.ship = ship;
        this.onPress = onPress;
        this.active = onPress != null;
    }

    /**
     * Set a custom model scale factor.
     * Smaller value = larger model (default 24).
     */
    public ShipSlot withScale(float scale) {
        this.modelScale = scale;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // ---- Background ----
        int bgColor = this.isHoveredOrFocused() ? 0x60FFFFFF : 0x40000000;
        graphics.fill(getX(), getY(), getX() + width, getY() + height, bgColor);

        // ---- Entity preview ----
        if (ship != null) {
            int centerX = getX() + width / 2;
            int centerY = getY() + height / 2 - 6;
            int entitySize = (int) modelScale;
            renderShipEntity(graphics, centerX, centerY, entitySize, mouseX, mouseY);
        }

        // ---- Name label ----
        Component name = ship.hasCustomName()
                ? ship.getCustomName()
                : Component.translatable(ship.getType().getDescriptionId());
        int nameWidth = Minecraft.getInstance().font.width(name);
        int nameX = getX() + (width - nameWidth) / 2;
        int nameY = getY() + height - 10;
        graphics.drawString(Minecraft.getInstance().font, name, nameX, nameY, 0xFFFFFF, true);

        // ---- Status bars ----
        drawBar(graphics, getX() + 2, getY() + 2, BAR_WIDTH, BAR_HEIGHT,
                ship.getHealth() / ship.getMaxHealth(), 0xFF4444, 0xFF44AA44);
    }

    // ---- Entity rendering ----

    private void renderShipEntity(GuiGraphics graphics, int x, int y, int scale, int mouseX, int mouseY) {
        float lookX = (x - mouseX) / 40.0f;
        float lookY = (y - 50.0f - mouseY) / 40.0f;

        var poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(x, y, 50.0);
        poseStack.scale(scale, scale, -scale);

        Quaternionf rotation = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf pitch = new Quaternionf().rotateX(lookY * 20.0f * 0.017453292f);
        rotation.mul(pitch);
        poseStack.mulPose(rotation);

        // Save entity rotation
        float prevYBodyRot = ship.yBodyRotO;
        float prevYRot = ship.getYRot();
        float prevXRot = ship.getXRot();
        float prevYHeadRotO = ship.yHeadRotO;
        float prevYHeadRot = ship.yHeadRot;
        float prevYBodyRotO = ship.yBodyRotO;

        ship.yBodyRotO = 180.0f + lookX * 20.0f;
        ship.yBodyRot = 180.0f + lookX * 20.0f;
        ship.setYRot(180.0f + lookX * 40.0f);
        ship.yHeadRotO = ship.getYRot();
        ship.yHeadRot = ship.getYRot();
        ship.setXRot(-lookY * 20.0f);

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        pitch.conjugate();
        dispatcher.overrideCameraOrientation(pitch);
        dispatcher.setRenderShadow(false);

        RenderSystem.runAsFancy(() ->
                dispatcher.render(ship, 0.0, 0.0, 0.0, 0.0f, 1.0f, poseStack, graphics.bufferSource(), 15728880)
        );

        // Restore entity rotation
        ship.yBodyRotO = prevYBodyRotO;
        ship.yBodyRot = prevYBodyRot;
        ship.setYRot(prevYRot);
        ship.setXRot(prevXRot);
        ship.yHeadRotO = prevYHeadRotO;
        ship.yHeadRot = prevYHeadRot;

        dispatcher.setRenderShadow(true);
        dispatcher.overrideCameraOrientation(null);

        poseStack.popPose();
    }

    // ---- Bar drawing ----

    private static void drawBar(GuiGraphics graphics, int x, int y, int width, int height,
                                float ratio, int colorBg, int colorFg) {
        // Background
        graphics.fill(x, y, x + width, y + height, colorBg);
        // Foreground
        int fillWidth = (int) (width * Math.min(1.0f, Math.max(0.0f, ratio)));
        if (fillWidth > 0) {
            graphics.fill(x + 1, y + 1, x + fillWidth - 1, y + height - 1, colorFg);
        }
    }

    // ---- Interaction ----

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (onPress != null) {
            onPress.run();
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
