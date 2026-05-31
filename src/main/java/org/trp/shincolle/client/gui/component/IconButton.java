package org.trp.shincolle.client.gui.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BooleanSupplier;

/**
 * A clickable icon button rendered from a texture sheet using UV coordinates.
 * <p>
 * Replaces the pattern of hardcoded {@code inside()} bounds checks in
 * {@code mouseClicked()} paired with manually positioned {@code blit()} calls.
 * Supports three visual states: normal, hover, and disabled, each with
 * its own UV region on the same texture sheet.
 * <p>
 * Hover state can also be activated independently of mouse position via an
 * optional {@link BooleanSupplier} (see {@link Builder#activeState(BooleanSupplier)}),
 * which is useful for buttons whose visuals mirror a server-side toggle state.
 * <p>
 * Setting a UV coordinate to {@code -1} skips the blit for that state,
 * allowing the background texture to show through.
 * <p>
 * Usage in a Screen:
 * <pre>{@code
 * this.addRenderableWidget(IconButton.builder(Sprites.T_VOLCORE)
 *     .pos(this.leftPos + 7, this.topPos + 6)
 *     .size(13, 13)
 *     .uv(0, 166)          // normal state (will be overlaid on background)
 *     .hoverUv(12, 166)    // hover / active indicator
 *     .onPress(() -> sendButton(0))
 *     .build());
 * }</pre>
 * <p>
 * All coordinates are screen-absolute (matching {@link AbstractWidget} conventions).
 */
public class IconButton extends AbstractWidget {

    private final ResourceLocation texture;
    private final int u, v;
    private final int hoverU, hoverV;
    private final int disabledU, disabledV;
    private final int texWidth;
    private final int texHeight;
    private final Runnable onPress;
    private final BooleanSupplier activeState;

    /**
     * Full constructor.
     *
     * @param x            screen-absolute X position
     * @param y            screen-absolute Y position
     * @param width        button width in pixels
     * @param height       button height in pixels
     * @param texture      the texture sheet {@link ResourceLocation}
     * @param u            UV u-offset for normal state ({@code -1} = skip)
     * @param v            UV v-offset for normal state ({@code -1} = skip)
     * @param texWidth     texture sheet width (usually 256)
     * @param texHeight    texture sheet height (usually 256)
     * @param hoverU       UV u-offset for hover / focus / active state
     * @param hoverV       UV v-offset for hover / focus / active state
     * @param disabledU    UV u-offset for disabled state
     * @param disabledV    UV v-offset for disabled state
     * @param activeState  supplier that returns true when the button should show hover UV
*                        (independent of mouse position, e.g. server-side toggle state).
*                        Pass {@code () -> false} or {@code null} to disable.
     * @param message      narration message
     * @param onPress      click callback
     */
    public IconButton(int x, int y, int width, int height,
                      ResourceLocation texture,
                      int u, int v, int texWidth, int texHeight,
                      int hoverU, int hoverV,
                      int disabledU, int disabledV,
                      BooleanSupplier activeState,
                      Component message, Runnable onPress) {
        super(x, y, width, height, message);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.hoverU = hoverU;
        this.hoverV = hoverV;
        this.disabledU = disabledU;
        this.disabledV = disabledV;
        this.activeState = activeState;
        this.onPress = onPress;
    }

    /**
     * Convenience constructor with 256x256 texture, no active state, no disabled UV override.
     */
    public IconButton(int x, int y, int width, int height,
                      ResourceLocation texture,
                      int u, int v,
                      int hoverU, int hoverV,
                      Runnable onPress) {
        this(x, y, width, height, texture, u, v, 256, 256, hoverU, hoverV, u, v,
                () -> false, Component.literal(""), onPress);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean showHover = this.isHoveredOrFocused()
                || (activeState != null && activeState.getAsBoolean());

        int drawU, drawV;
        if (!this.active) {
            drawU = disabledU;
            drawV = disabledV;
        } else if (showHover) {
            drawU = hoverU;
            drawV = hoverV;
        } else {
            drawU = u;
            drawV = v;
        }

        // Skip blit when UV is (-1, -1) — allows background to show through
        if (drawU >= 0 && drawV >= 0) {
            graphics.blit(texture, getX(), getY(), drawU, drawV, width, height, texWidth, texHeight);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onPress.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    // ---- Builder ----

    public static Builder builder(ResourceLocation texture) {
        return new Builder(texture);
    }

    public static final class Builder {
        private final ResourceLocation texture;
        private int x, y;
        private int width = 18, height = 18;
        private int u, v;
        private int hoverU, hoverV;
        private int disabledU, disabledV;
        private int texWidth = 256, texHeight = 256;
        private Runnable onPress;
        private BooleanSupplier activeState = () -> false;
        private Component message = Component.literal("");
        private boolean explicitHover;
        private boolean explicitDisabled;

        private Builder(ResourceLocation texture) {
            this.texture = texture;
        }

        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /** UV coordinates for the normal (idle) state. Set to (-1, -1) for transparent. */
        public Builder uv(int u, int v) {
            this.u = u;
            this.v = v;
            return this;
        }

        /** UV coordinates for the hover / focused state. */
        public Builder hoverUv(int u, int v) {
            this.hoverU = u;
            this.hoverV = v;
            this.explicitHover = true;
            return this;
        }

        /** UV coordinates for the disabled state. Defaults to normal UV if not set. */
        public Builder disabledUv(int u, int v) {
            this.disabledU = u;
            this.disabledV = v;
            this.explicitDisabled = true;
            return this;
        }

        /** Texture sheet dimensions (default 256x256). */
        public Builder textureSize(int texWidth, int texHeight) {
            this.texWidth = texWidth;
            this.texHeight = texHeight;
            return this;
        }

        /**
         * Optional supplier that makes the button show hover UV when its value is {@code true},
         * regardless of mouse position. Useful for menu-state-driven toggles.
         */
        public Builder activeState(BooleanSupplier activeState) {
            this.activeState = activeState;
            return this;
        }

        public Builder onPress(Runnable onPress) {
            this.onPress = onPress;
            return this;
        }

        public Builder message(Component message) {
            this.message = message;
            return this;
        }

        public IconButton build() {
            if (!explicitHover) {
                hoverU = u;
                hoverV = v;
            }
            if (!explicitDisabled) {
                disabledU = u;
                disabledV = v;
            }
            if (onPress == null) {
                throw new IllegalStateException("IconButton.Builder: onPress must be set");
            }
            return new IconButton(x, y, width, height, texture,
                    u, v, texWidth, texHeight,
                    hoverU, hoverV,
                    disabledU, disabledV,
                    activeState, message, onPress);
        }
    }
}
