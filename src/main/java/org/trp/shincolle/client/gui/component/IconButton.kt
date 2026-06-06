package org.trp.shincolle.client.gui.component

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import java.util.function.BooleanSupplier

/**
 * A clickable icon button rendered from a texture sheet using UV coordinates.
 * 
 * 
 * Replaces the pattern of hardcoded `inside()` bounds checks in
 * `mouseClicked()` paired with manually positioned `blit()` calls.
 * Supports three visual states: normal, hover, and disabled, each with
 * its own UV region on the same texture sheet.
 * 
 * 
 * Hover state can also be activated independently of mouse position via an
 * optional [BooleanSupplier] (see [Builder.activeState]),
 * which is useful for buttons whose visuals mirror a server-side toggle state.
 * 
 * 
 * Setting a UV coordinate to `-1` skips the blit for that state,
 * allowing the background texture to show through.
 * 
 * 
 * Usage in a Screen:
 * <pre>`this.addRenderableWidget(IconButton.builder(Sprites.T_VOLCORE)     .pos(this.leftPos + 7, this.topPos + 6)     .size(13, 13)     .uv(0, 166)          // normal state (will be overlaid on background)     .hoverUv(12, 166)    // hover / active indicator     .onPress(() -> sendButton(0))     .build()); `</pre>
 * 
 * 
 * All coordinates are screen-absolute (matching [AbstractWidget] conventions).
 */
class IconButton
/**
 * Full constructor.
 * 
 * @param x            screen-absolute X position
 * @param y            screen-absolute Y position
 * @param width        button width in pixels
 * @param height       button height in pixels
 * @param texture      the texture sheet [ResourceLocation]
 * @param u            UV u-offset for normal state (`-1` = skip)
 * @param v            UV v-offset for normal state (`-1` = skip)
 * @param texWidth     texture sheet width (usually 256)
 * @param texHeight    texture sheet height (usually 256)
 * @param hoverU       UV u-offset for hover / focus / active state
 * @param hoverV       UV v-offset for hover / focus / active state
 * @param disabledU    UV u-offset for disabled state
 * @param disabledV    UV v-offset for disabled state
 * @param activeState  supplier that returns true when the button should show hover UV
 * (independent of mouse position, e.g. server-side toggle state).
 * Pass `() -> false` or `null` to disable.
 * @param message      narration message
 * @param onPress      click callback
 */(
    x: Int, y: Int, width: Int, height: Int,
    private val texture: ResourceLocation,
    private val u: Int, private val v: Int, private val texWidth: Int, private val texHeight: Int,
    private val hoverU: Int, private val hoverV: Int,
    private val disabledU: Int, private val disabledV: Int,
    private val activeState: BooleanSupplier?,
    message: Component, private val onPress: Runnable
) : AbstractWidget(x, y, width, height, message) {
    /**
     * Convenience constructor with 256x256 texture, no active state, no disabled UV override.
     */
    constructor(
        x: Int, y: Int, width: Int, height: Int,
        texture: ResourceLocation,
        u: Int, v: Int,
        hoverU: Int, hoverV: Int,
        onPress: Runnable
    ) : this(
        x, y, width, height, texture, u, v, 256, 256, hoverU, hoverV, u, v,
        BooleanSupplier { false }, Component.literal(""), onPress
    )

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val showHover = this.isHoveredOrFocused()
                || (activeState != null && activeState.getAsBoolean())

        val drawU: Int
        val drawV: Int
        if (!this.active) {
            drawU = disabledU
            drawV = disabledV
        } else if (showHover) {
            drawU = hoverU
            drawV = hoverV
        } else {
            drawU = u
            drawV = v
        }

        // Skip blit when UV is (-1, -1) — allows background to show through
        if (drawU >= 0 && drawV >= 0) {
            graphics.blit(texture, getX(), getY(), drawU.toFloat(), drawV.toFloat(), width, height, texWidth, texHeight)
        }
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        this.onPress.run()
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {
        this.defaultButtonNarrationText(output)
    }

    class Builder(private val texture: ResourceLocation) {
        private var x = 0
        private var y = 0
        private var width = 18
        private var height = 18
        private var u = 0
        private var v = 0
        private var hoverU = 0
        private var hoverV = 0
        private var disabledU = 0
        private var disabledV = 0
        private var texWidth = 256
        private var texHeight = 256
        private var onPress: Runnable? = null
        private var activeState: BooleanSupplier? = BooleanSupplier { false }
        private var message: Component = Component.literal("")
        private var explicitHover = false
        private var explicitDisabled = false

        fun pos(x: Int, y: Int): Builder {
            this.x = x
            this.y = y
            return this
        }

        fun size(width: Int, height: Int): Builder {
            this.width = width
            this.height = height
            return this
        }

        /** UV coordinates for the normal (idle) state. Set to (-1, -1) for transparent.  */
        fun uv(u: Int, v: Int): Builder {
            this.u = u
            this.v = v
            return this
        }

        /** UV coordinates for the hover / focused state.  */
        fun hoverUv(u: Int, v: Int): Builder {
            this.hoverU = u
            this.hoverV = v
            this.explicitHover = true
            return this
        }

        /** UV coordinates for the disabled state. Defaults to normal UV if not set.  */
        fun disabledUv(u: Int, v: Int): Builder {
            this.disabledU = u
            this.disabledV = v
            this.explicitDisabled = true
            return this
        }

        /** Texture sheet dimensions (default 256x256).  */
        fun textureSize(texWidth: Int, texHeight: Int): Builder {
            this.texWidth = texWidth
            this.texHeight = texHeight
            return this
        }

        /**
         * Optional supplier that makes the button show hover UV when its value is `true`,
         * regardless of mouse position. Useful for menu-state-driven toggles.
         */
        fun activeState(activeState: BooleanSupplier?): Builder {
            this.activeState = activeState
            return this
        }

        fun onPress(onPress: Runnable): Builder {
            this.onPress = onPress
            return this
        }

        fun message(message: Component): Builder {
            this.message = message
            return this
        }

        fun build(): IconButton {
            if (!explicitHover) {
                hoverU = u
                hoverV = v
            }
            if (!explicitDisabled) {
                disabledU = u
                disabledV = v
            }
            checkNotNull(onPress) { "IconButton.Builder: onPress must be set" }
            return IconButton(
                x, y, width, height, texture,
                u, v, texWidth, texHeight,
                hoverU, hoverV,
                disabledU, disabledV,
                activeState, message, onPress!!
            )
        }
    }

    companion object {
        // ---- Builder ----
        fun builder(texture: ResourceLocation): Builder {
            return IconButton.Builder(texture)
        }
    }
}
