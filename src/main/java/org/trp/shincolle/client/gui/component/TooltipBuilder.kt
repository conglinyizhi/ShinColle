package org.trp.shincolle.client.gui.component

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import java.util.List

/**
 * A chain-style builder for screen tooltips.
 * 
 * 
 * Wraps [GuiGraphics.renderComponentTooltip] with a
 * fluent API. Replaces scattered ad-hoc tooltip construction patterns like:
 * <pre>`List<Component> tooltip = new ArrayList<>(); tooltip.add(Component.translatable("gui.shincolle.foo")); tooltip.add(Component.translatable("gui.shincolle.bar")); graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY); `</pre>
 * with:
 * <pre>`TooltipBuilder.create()     .add("gui.shincolle.foo")     .add("gui.shincolle.bar")     .renderIfNotEmpty(graphics, font, mouseX, mouseY); `</pre>
 * 
 * 
 * Instances are single-use: [.renderIfNotEmpty] clears the internal list
 * after rendering so the builder can be reused.
 */
class TooltipBuilder private constructor() {
    private val lines: MutableList<Component?> = ArrayList<Component?>()

    // ---- Fluent adder methods ----
    /** Append a [Component] line.  */
    fun add(component: Component?): TooltipBuilder {
        lines.add(component)
        return this
    }

    /** Append a translatable line.  */
    fun add(translationKey: String): TooltipBuilder {
        lines.add(Component.translatable(translationKey))
        return this
    }

    /** Append a translatable line with format arguments.  */
    fun add(translationKey: String, vararg args: Any?): TooltipBuilder {
        lines.add(Component.translatable(translationKey, *args))
        return this
    }

    /** Append a blank separator line (an empty text component).  */
    fun addBlank(): TooltipBuilder {
        lines.add(Component.literal(""))
        return this
    }

    /** Append all lines from another builder (or any [List] of components).  */
    fun addAll(components: MutableList<out Component?>): TooltipBuilder {
        lines.addAll(components)
        return this
    }

    /**
     * Append a line with a color style.
     * <pre>`.add(Component.translatable("key").withStyle(ChatFormatting.GRAY))`</pre>
     */
    fun addColored(translationKey: String, vararg styles: ChatFormatting?): TooltipBuilder {
        lines.add(Component.translatable(translationKey).withStyle(*styles))
        return this
    }

    // ---- Conditional adders ----
    /** Append a line only if `condition` is true.  */
    fun addIf(condition: Boolean, component: Component?): TooltipBuilder {
        if (condition) lines.add(component)
        return this
    }

    /** Append a translatable line only if `condition` is true.  */
    fun addIf(condition: Boolean, translationKey: String): TooltipBuilder {
        if (condition) lines.add(Component.translatable(translationKey))
        return this
    }

    // ---- Render ----
    /**
     * Render the tooltip at the given mouse coordinates, then clear the internal list.
     * If no lines have been added this is a no-op.
     */
    fun renderIfNotEmpty(graphics: GuiGraphics, font: Font, mouseX: Int, mouseY: Int) {
        if (!lines.isEmpty()) {
            graphics.renderComponentTooltip(font, List.copyOf<Component?>(lines), mouseX, mouseY)
            lines.clear()
        }
    }

    /**
     * Render the tooltip unconditionally (even if empty).
     */
    fun render(graphics: GuiGraphics, font: Font, mouseX: Int, mouseY: Int) {
        graphics.renderComponentTooltip(font, List.copyOf<Component?>(lines), mouseX, mouseY)
        lines.clear()
    }

    val isEmpty: Boolean
        // ---- Query ----
        get() = lines.isEmpty()

    fun size(): Int {
        return lines.size
    }

    /**
     * Return the current lines without rendering.
     * The returned list is a snapshot; the builder retains ownership of its internal list.
     */
    fun snapshot(): MutableList<Component?> {
        return List.copyOf<Component?>(lines)
    }

    companion object {
        /** Create a new tooltip builder.  */
        fun create(): TooltipBuilder {
            return TooltipBuilder()
        }

        /** Create a new tooltip builder and add one line immediately.  */
        fun of(firstLine: Component?): TooltipBuilder {
            return TooltipBuilder().add(firstLine)
        }

        /** Create a new tooltip builder and add one translatable line immediately.  */
        fun of(translationKey: String): TooltipBuilder {
            return TooltipBuilder().add(translationKey)
        }
    }
}
