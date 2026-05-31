package org.trp.shincolle.client.gui.component;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A chain-style builder for screen tooltips.
 * <p>
 * Wraps {@link GuiGraphics#renderComponentTooltip(Font, List, int, int)} with a
 * fluent API. Replaces scattered ad-hoc tooltip construction patterns like:
 * <pre>{@code
 * List<Component> tooltip = new ArrayList<>();
 * tooltip.add(Component.translatable("gui.shincolle.foo"));
 * tooltip.add(Component.translatable("gui.shincolle.bar"));
 * graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
 * }</pre>
 * with:
 * <pre>{@code
 * TooltipBuilder.create()
 *     .add("gui.shincolle.foo")
 *     .add("gui.shincolle.bar")
 *     .renderIfNotEmpty(graphics, font, mouseX, mouseY);
 * }</pre>
 * <p>
 * Instances are single-use: {@link #renderIfNotEmpty} clears the internal list
 * after rendering so the builder can be reused.
 */
public class TooltipBuilder {

    private final List<Component> lines = new ArrayList<>();

    private TooltipBuilder() {}

    /** Create a new tooltip builder. */
    public static TooltipBuilder create() {
        return new TooltipBuilder();
    }

    /** Create a new tooltip builder and add one line immediately. */
    public static TooltipBuilder of(Component firstLine) {
        return new TooltipBuilder().add(firstLine);
    }

    /** Create a new tooltip builder and add one translatable line immediately. */
    public static TooltipBuilder of(String translationKey) {
        return new TooltipBuilder().add(translationKey);
    }

    // ---- Fluent adder methods ----

    /** Append a {@link Component} line. */
    public TooltipBuilder add(Component component) {
        lines.add(component);
        return this;
    }

    /** Append a translatable line. */
    public TooltipBuilder add(String translationKey) {
        lines.add(Component.translatable(translationKey));
        return this;
    }

    /** Append a translatable line with format arguments. */
    public TooltipBuilder add(String translationKey, Object... args) {
        lines.add(Component.translatable(translationKey, args));
        return this;
    }

    /** Append a blank separator line (an empty text component). */
    public TooltipBuilder addBlank() {
        lines.add(Component.literal(""));
        return this;
    }

    /** Append all lines from another builder (or any {@link List} of components). */
    public TooltipBuilder addAll(List<? extends Component> components) {
        lines.addAll(components);
        return this;
    }

    /**
     * Append a line with a color style.
     * <pre>{@code .add(Component.translatable("key").withStyle(ChatFormatting.GRAY))}</pre>
     */
    public TooltipBuilder addColored(String translationKey, net.minecraft.ChatFormatting... styles) {
        lines.add(Component.translatable(translationKey).withStyle(styles));
        return this;
    }

    // ---- Conditional adders ----

    /** Append a line only if {@code condition} is true. */
    public TooltipBuilder addIf(boolean condition, Component component) {
        if (condition) lines.add(component);
        return this;
    }

    /** Append a translatable line only if {@code condition} is true. */
    public TooltipBuilder addIf(boolean condition, String translationKey) {
        if (condition) lines.add(Component.translatable(translationKey));
        return this;
    }

    // ---- Render ----

    /**
     * Render the tooltip at the given mouse coordinates, then clear the internal list.
     * If no lines have been added this is a no-op.
     */
    public void renderIfNotEmpty(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        if (!lines.isEmpty()) {
            graphics.renderComponentTooltip(font, List.copyOf(lines), mouseX, mouseY);
            lines.clear();
        }
    }

    /**
     * Render the tooltip unconditionally (even if empty).
     */
    public void render(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        graphics.renderComponentTooltip(font, List.copyOf(lines), mouseX, mouseY);
        lines.clear();
    }

    // ---- Query ----

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public int size() {
        return lines.size();
    }

    /**
     * Return the current lines without rendering.
     * The returned list is a snapshot; the builder retains ownership of its internal list.
     */
    public List<Component> snapshot() {
        return List.copyOf(lines);
    }
}
