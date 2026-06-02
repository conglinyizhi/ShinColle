package org.trp.shincolle.client.renderer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.reference.Values;

import java.util.ArrayList;
import java.util.List;

public class BookRenderer {

    public static final ResourceLocation GUI_BOOK = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/guideskbook.png");
    public static final ResourceLocation GUI_BOOK2 = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/guideskbook2.png");
    public static final ResourceLocation GUI_NAME_ICON0 = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/guinameicon0.png");
    public static final ResourceLocation GUI_NAME_ICON1 = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/guinameicon1.png");
    public static final ResourceLocation GUI_NAME_ICON2 = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/guinameicon2.png");
    public static final ResourceLocation GUI_RADAR = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/guideskradar.png");
    public static final ResourceLocation GUI_DESK = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/guidesk.png");

    private static final ResourceLocation FONT_MISANS = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "default");
    private static final int LX = 13, RX = 132, TY = 44, MAXW = 102;

    private static boolean useMiSans() {
        return org.trp.shincolle.Config.useMiSansFont
                && org.trp.shincolle.Config.miSansOnlyForLegacyLogs;
    }

    private static FormattedCharSequence forceFont(FormattedCharSequence seq) {
        return sink -> seq.accept((i, s, cp) -> sink.accept(i, s.withFont(FONT_MISANS), cp));
    }

    // ---- Public API ----

    public static void drawBookBase(GuiGraphics g, int x, int y, int chapId, int pageId) {
        g.blit(GUI_BOOK, x, y, 0, 0, 256, 192);
        if ((chapId == 4 || chapId == 5) && pageId > 0) {
            g.blit(GUI_BOOK2, x + 20, y + 48, (chapId == 4) ? 0 : 105, 0, 87, 130);
        }
    }

    /** @param guiScale DeskScreen's rendering scale (typically 1.25f). */
    public static void drawBookContent(GuiGraphics g, int x, int y, int page, int chapNum, float guiScale) {
        int bookID = chapNum * 1000 + page;
        List<int[]> content = Values.BookList.get(bookID);
        if (content == null) {
            drawTitleText(g, x, y, page, chapNum, guiScale);
            drawBookText(g, x, y, 0, 0, 0, bookID, guiScale, 0);
            drawBookText(g, x, y, 1, 0, 0, bookID, guiScale, 0);
            return;
        }
        // Collect picture ranges per side: {picTop, picBottom}
        List<int[]>[] pics = new List[]{new ArrayList<>(), new ArrayList<>()};
        for (int[] data : content) {
            if (data != null) {
                if (data[0] == 1 && data.length >= 9) { // picture
                    int s = data[1];
                    if (s >= 0 && s < pics.length) {
                        int top = y + 48 + data[3], h = data[8];
                        pics[s].add(new int[]{top, top + h});
                    }
                }
            }
        }

        drawTitleText(g, x, y, page, chapNum, guiScale);
        for (int[] data : content) {
            if (data == null) continue;
            switch (data[0]) {
                case 0 -> {
                    if (data.length < 4) continue;
                    int s = data[1], tt = y + TY + data[3];
                    int push = 0;
                    if (s >= 0 && s < pics.length) { for (int[] p : pics[s]) {
                        if (tt < p[1] && tt + 5 > p[0]) { // text overlaps picture
                            if (p[1] > push) push = p[1];
                        }
                        }
                    }
                    drawBookText(g, x, y, s, data[2], data[3], bookID, guiScale, push);
                }
                case 1 -> drawBookPic(g, x, y, data);
                case 2 -> drawBookIcon(g, x, y, data[1], data[2], data[3], data[4]);
            }
        }
    }

    public static void drawStateFlags(GuiGraphics g, int x, int y, net.minecraft.world.entity.LivingEntity entity) {
        if (!(entity instanceof org.trp.shincolle.entity.base.EntityShipBase ship)) return;
        int stats = ship.getStateEmotion(0), maxStats = ship.getStateMinor(13);
        int idx = 0, start = ship.hasShipMounts() ? 1 : 0;
        for (int i = start; i < 16; ++i) {
            if (i >= maxStats) break;
            g.blit(GUI_BOOK2, x + 45 + (idx % 8) * 9, y + 158 + (idx / 8) * 9, 115, ((stats >> i) & 1) == 1 ? 156 : 147, 7, 9);
            idx++;
        }
    }

    // ---- Text: counter-scaled to native resolution ----

    private static void drawBookText(GuiGraphics g, int x, int y, int side, int offX, int offY, int bookID, float guiScale, int reservedY) {
        String key = "gui.shincolle.book.chap" + (bookID / 1000) + ".text" + (bookID % 1000) + "d" + side;
        String text = net.minecraft.client.resources.language.I18n.get(key);
        if (text.equals(key)) return;

        Font font = Minecraft.getInstance().font;
        int bx = x + (side == 0 ? LX : RX) + offX;
        int by = y + TY + offY;
        // Push below reserved picture/icon area on this side
        if (reservedY > by) by = reservedY + 2;
        String[] lines = text.split("(?i)<\\s*br\\s*/?\\s*>|#");

        // scale(1/guiScale) cancels DeskScreen's scale(guiScale):
        //   T*S * scale(inv) = T*scale(1.25)*scale(0.8) = T*scale(1.0) = pure translation to (leftPos,topPos).
        //   Screen position of (scx,scy) = leftPos + scx, topPos + scy.
        //   We want it to match the unscaled position: leftPos + bx*guiScale, topPos + by*guiScale.
        //   -> scx = bx*guiScale, scy = by*guiScale.
        float inv = 1.0f / guiScale;
        g.pose().pushPose();
        g.pose().scale(inv, inv, 1.0f);

        int sx = Math.round(bx * guiScale);
        int sy = Math.round(by * guiScale);
        int ww = Math.round(MAXW * guiScale); // wrap width at native resolution
        int curY = 0;

        for (String line : lines) {
            if (line.isEmpty()) { curY += font.lineHeight + 1; continue; }
            List<FormattedCharSequence> wrap = useMiSans()
                ? font.split(Component.literal(line).withStyle(s -> s.withFont(FONT_MISANS)), ww)
                        .stream().map(BookRenderer::forceFont)
                        .collect(java.util.stream.Collectors.toList())
                : font.split(Component.literal(line), ww);
            for (var seq : wrap) { g.drawString(font, seq, sx, sy + curY, 0, false); curY += font.lineHeight + 1; }
        }
        g.pose().popPose();
    }

    // ---- Pictures & icons (unscaled — they follow background texture) ----

    private static void drawBookPic(GuiGraphics g, int x, int y, int[] data) {
        if (data.length < 9) return;
        g.blit(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID,
                "textures/gui/book/bookpic0" + (data[4] == 0 ? 1 : data[4]) + ".png"),
                x + (data[1] == 0 ? LX : RX + 1) + data[2], y + TY + 4 + data[3],
                (float) data[5], (float) data[6], data[7], data[8], 256, 256);
    }

    private static void drawBookIcon(GuiGraphics g, int x, int y, int side, int offX, int offY, int iconID) {
        ItemStack stack = Values.ItemIconMap.get((short) iconID);
        if (stack != null) g.renderItem(stack, x + (side == 0 ? LX : RX + 1) + offX, y + TY + 4 + offY);
    }

    // ---- Title text ----

    private static void drawTitleText(GuiGraphics g, int x, int y, int page, int chap, float guiScale) {
        String key = (chap == 0) ? "gui.shincolle.book.chap0.title" : "gui.shincolle.book.chap" + chap + ".title" + page;
        String text = net.minecraft.client.resources.language.I18n.get(key);
        if (text.equals(key)) return;

        Font font = Minecraft.getInstance().font;
        Component comp = useMiSans()
            ? Component.literal(text).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.DARK_RED).withStyle(s -> s.withFont(FONT_MISANS))
            : Component.literal(text).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.DARK_RED);

        float inv = 1.0f / guiScale;
        g.pose().pushPose();
        g.pose().scale(inv, inv, 1.0f);

        int cx = Math.round((x + 64) * guiScale);
        int cy = Math.round((y + 34) * guiScale);
        int tx = cx - font.width(comp) / 2;

        if (useMiSans()) {
            var parts = font.split(comp, 2048);
            if (!parts.isEmpty()) g.drawString(font, forceFont(parts.get(0)), tx, cy, 0xFFFFFF, false);
        } else {
            g.drawString(font, comp, tx, cy, 0xFFFFFF, false);
        }
        g.pose().popPose();
    }
}
