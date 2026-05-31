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

    private static boolean useMiSans() { return org.trp.shincolle.Config.useMiSansFont; }

    private static FormattedCharSequence forceFont(FormattedCharSequence seq) {
        return sink -> seq.accept((i, style, cp) -> sink.accept(i, style.withFont(FONT_MISANS), cp));
    }

    public static void drawBookBase(GuiGraphics g, int x, int y, int chapId, int pageId) {
        g.blit(GUI_BOOK, x, y, 0, 0, 256, 192);
        if ((chapId == 4 || chapId == 5) && pageId > 0) {
            int u = (chapId == 4) ? 0 : 105;
            g.blit(GUI_BOOK2, x + 20, y + 48, u, 0, 87, 130);
        }
    }

    public static void drawBookContent(GuiGraphics g, int x, int y, int page, int chapNum) {
        int bookID = chapNum * 1000 + page;
        List<int[]> content = Values.BookList.get(bookID);
        if (content == null) {
            drawTitleText(g, x, y, page, chapNum);
            drawBookText(g, x, y, 0, 0, 0, bookID);
            drawBookText(g, x, y, 1, 0, 0, bookID);
            return;
        }
        drawTitleText(g, x, y, page, chapNum);
        for (int[] data : content) {
            if (data == null) continue;
            switch (data[0]) {
                case 0 -> drawBookText(g, x, y, data[1], data[2], data[3], bookID);
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

    private static void drawBookText(GuiGraphics g, int x, int y, int side, int offX, int offY, int bookID) {
        String key = "gui.shincolle.book.chap" + (bookID / 1000) + ".text" + (bookID % 1000) + "d" + side;
        String text = net.minecraft.client.resources.language.I18n.get(key);
        if (text.equals(key)) return;
        Font font = Minecraft.getInstance().font;
        int sx = x + (side == 0 ? 13 : 132) + offX;
        int sy = y + 44 + offY;
        int curY = 0;
        for (String line : text.split("<BR>|<BR/>|<br>|<br/>|#")) {
            if (line.isEmpty()) { curY += font.lineHeight; continue; }
            List<FormattedCharSequence> wrapped;
            if (useMiSans()) {
                wrapped = font.split(Component.literal(line).withStyle(s -> s.withFont(FONT_MISANS)), 102)
                        .stream().map(BookRenderer::forceFont)
                        .collect(java.util.stream.Collectors.toList());
            } else {
                wrapped = font.split(Component.literal(line), 102);
            }
            for (var seq : wrapped) { g.drawString(font, seq, sx, sy + curY, 0, false); curY += font.lineHeight; }
        }
    }

    private static void drawBookPic(GuiGraphics g, int x, int y, int[] data) {
        if (data.length < 9) return;
        int px = x + (data[1] == 0 ? 13 : 133) + data[2];
        int py = y + 48 + data[3];
        g.blit(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/book/bookpic0" + (data[4] == 0 ? 1 : data[4]) + ".png"),
                px, py, (float) data[5], (float) data[6], data[7], data[8], 256, 256);
    }

    private static void drawBookIcon(GuiGraphics g, int x, int y, int side, int offX, int offY, int iconID) {
        ItemStack stack = Values.ItemIconMap.get((short) iconID);
        if (stack != null) g.renderItem(stack, x + (side == 0 ? 13 : 133) + offX, y + 48 + offY);
    }

    private static void drawTitleText(GuiGraphics g, int x, int y, int page, int chap) {
        String key = (chap == 0) ? "gui.shincolle.book.chap0.title" : "gui.shincolle.book.chap" + chap + ".title" + page;
        String text = net.minecraft.client.resources.language.I18n.get(key);
        if (text.equals(key)) return;
        Font font = Minecraft.getInstance().font;
        Component comp = useMiSans()
            ? Component.literal(text).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.DARK_RED).withStyle(s -> s.withFont(FONT_MISANS))
            : Component.literal(text).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.DARK_RED);
        if (useMiSans()) {
            var list = font.split(comp, 2048);
            if (!list.isEmpty())
                g.drawString(font, forceFont(list.get(0)), x + 64 - font.width(comp) / 2, y + 34, 0xFFFFFF, false);
        } else {
            g.drawString(font, comp, x + 64 - font.width(comp) / 2, y + 34, 0xFFFFFF, false);
        }
    }
}
