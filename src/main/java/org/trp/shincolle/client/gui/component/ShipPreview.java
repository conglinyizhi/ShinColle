package org.trp.shincolle.client.gui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import java.util.function.Supplier;

/**
 * A widget that renders a 3D ship entity model using {@link EntityRenderDispatcher}.
 * <p>
 * The ship entity is fetched from the provided {@link Supplier} each frame so the
 * displayed entity can change dynamically. Mouse position affects the look-at
 * rotation of the rendered model.
 */
public class ShipPreview extends AbstractWidget {

    private final Supplier<LivingEntity> shipSupplier;

    /**
     * @param x            screen-absolute X position
     * @param y            screen-absolute Y position
     * @param width        widget width
     * @param height       widget height
     * @param shipSupplier supplier that returns the entity to render (fetched each frame)
     */
    public ShipPreview(int x, int y, int width, int height,
                       Supplier<LivingEntity> shipSupplier) {
        super(x, y, width, height, Component.literal(""));
        this.shipSupplier = shipSupplier;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        LivingEntity entity = shipSupplier.get();
        if (entity == null) return;

        int cx = getX() + getWidth() / 2;
        int cy = getY() + getHeight() / 2;
        int scale = Math.max(16, Math.min(getWidth(), getHeight()) / 2);

        renderEntityWithPassengers(guiGraphics, cx, cy, scale, mouseX, mouseY, entity);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    /**
     * Render a living entity with its passengers at the given screen position.
     * <p>
     * The mouse coordinates ({@code mx}, {@code my}) control the look-at rotation,
     * producing the same interactive preview effect used in {@code ShipInventoryScreen}.
     *
     * @param g      GUI graphics
     * @param x      screen X centre for the model
     * @param y      screen Y centre for the model
     * @param scale  model scale factor
     * @param mx     mouse X (screen-absolute)
     * @param my     mouse Y (screen-absolute)
     * @param entity the entity to render
     */
    public static void renderEntityWithPassengers(GuiGraphics g, int x, int y, int scale,
                                                  float mx, float my, LivingEntity entity) {
        float f = (float) Math.atan((x - mx) / 40.0F);
        float f1 = (float) Math.atan((y - 50.0F - my) / 40.0F);
        var pose = g.pose();
        pose.pushPose();
        pose.translate(x, y, 50.0D);
        pose.scale(scale, scale, -scale);
        Quaternionf q1 = (new Quaternionf()).rotateZ((float) Math.PI);
        Quaternionf q2 = (new Quaternionf()).rotateX(f1 * 20f * 0.017453292f);
        q1.mul(q2);
        pose.mulPose(q1);
        float byo = entity.yBodyRotO, by = entity.yBodyRot, yr = entity.getYRot(), xr = entity.getXRot(), yho = entity.yHeadRotO, yh = entity.yHeadRot;
        entity.yBodyRotO = 180f + f * 20f;
        entity.yBodyRot = 180f + f * 20f;
        entity.setYRot(180f + f * 40f);
        entity.yHeadRotO = entity.getYRot();
        entity.yHeadRot = entity.getYRot();
        entity.setXRot(-f1 * 20f);
        EntityRenderDispatcher d = Minecraft.getInstance().getEntityRenderDispatcher();
        q2.conjugate();
        d.overrideCameraOrientation(q2);
        d.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> {
            d.render(entity, 0, 0, 0, 0f, 1f, pose, g.bufferSource(), 15728880);
            for (Entity pass : entity.getPassengers()) {
                float pbyo = 0, pby = 0, pyr = 0, pxr = 0, pyho = 0, pyh = 0;
                if (pass instanceof LivingEntity lp) {
                    pbyo = lp.yBodyRotO;
                    pby = lp.yBodyRot;
                    pyr = lp.getYRot();
                    pxr = lp.getXRot();
                    pyho = lp.yHeadRotO;
                    pyh = lp.yHeadRot;
                    lp.yBodyRotO = entity.yBodyRotO;
                    lp.yBodyRot = entity.yBodyRot;
                    lp.setYRot(entity.getYRot());
                    lp.yHeadRotO = entity.yHeadRotO;
                    lp.yHeadRot = entity.yHeadRot;
                    lp.setXRot(entity.getXRot());
                }
                pose.pushPose();
                Vec3 rp = entity.getPassengerRidingPosition(pass);
                double inv = 1.0 / scale;
                pose.translate((rp.x - entity.getX()) * inv, (rp.y - entity.getY()) * inv + 0.09, (rp.z - entity.getZ()) * inv);
                pose.translate(0, 0, 0.2);
                d.render(pass, 0, 0, 0, 0f, 1f, pose, g.bufferSource(), 15728880);
                pose.popPose();
                if (pass instanceof LivingEntity lp) {
                    lp.yBodyRotO = pbyo;
                    lp.yBodyRot = pby;
                    lp.setYRot(pyr);
                    lp.setXRot(pxr);
                    lp.yHeadRotO = pyho;
                    lp.yHeadRot = pyh;
                }
            }
        });
        g.flush();
        d.setRenderShadow(true);
        entity.yBodyRotO = byo;
        entity.yBodyRot = by;
        entity.setYRot(yr);
        entity.setXRot(xr);
        entity.yHeadRotO = yho;
        entity.yHeadRot = yh;
        pose.popPose();
    }
}
