package org.trp.shincolle.client.renderer

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.resources.language.I18n
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.FormattedCharSink
import net.minecraft.world.entity.LivingEntity
import org.trp.shincolle.Config
import org.trp.shincolle.Shincolle
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.reference.Values
import java.util.function.UnaryOperator
import java.util.stream.Collectors

object BookRenderer {
    val GUI_BOOK: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/guideskbook.png")
    val GUI_BOOK2: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/guideskbook2.png")
    val GUI_NAME_ICON0: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/guinameicon0.png")
    val GUI_NAME_ICON1: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/guinameicon1.png")
    val GUI_NAME_ICON2: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/guinameicon2.png")
    val GUI_RADAR: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/guideskradar.png")
    val GUI_DESK: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/guidesk.png")

    private val FONT_MISANS: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "default")
    private const val LX = 13
    private const val RX = 132
    private const val TY = 44
    private const val MAXW = 102

    private fun useMiSans(): Boolean {
        return Config.useMiSansFont
                && Config.miSansOnlyForLegacyLogs
    }

    private fun forceFont(seq: FormattedCharSequence): FormattedCharSequence {
        return FormattedCharSequence { sink: FormattedCharSink? ->
            seq.accept(FormattedCharSink { i: Int, s: Style?, cp: Int ->
                sink!!.accept(
                    i,
                    s!!.withFont(FONT_MISANS),
                    cp
                )
            })
        }
    }

    // ---- Public API ----
    fun drawBookBase(g: GuiGraphics, x: Int, y: Int, chapId: Int, pageId: Int) {
        g.blit(GUI_BOOK, x, y, 0, 0, 256, 192)
        if ((chapId == 4 || chapId == 5) && pageId > 0) {
            g.blit(GUI_BOOK2, x + 20, y + 48, if (chapId == 4) 0 else 105, 0, 87, 130)
        }
    }

    /** @param guiScale DeskScreen's rendering scale (typically 1.25f).
     */
    fun drawBookContent(g: GuiGraphics, x: Int, y: Int, page: Int, chapNum: Int, guiScale: Float) {
        val bookID = chapNum * 1000 + page
        val content = Values.BookList.get(bookID)
        if (content == null) {
            drawTitleText(g, x, y, page, chapNum, guiScale)
            drawBookText(g, x, y, 0, 0, 0, bookID, guiScale, 0)
            drawBookText(g, x, y, 1, 0, 0, bookID, guiScale, 0)
            return
        }
        // Collect picture ranges per side: {picTop, picBottom}
        val pics: Array<MutableList<IntArray>?> = arrayOf<MutableList<IntArray>?>(ArrayList<IntArray>(), ArrayList<IntArray>())
        for (data in content) {
            if (data != null) {
                if (data[0] == 1 && data.size >= 9) { // picture
                    val s = data[1]
                    if (s >= 0 && s < pics.size) {
                        val top = y + 48 + data[3]
                        val h = data[8]
                        pics[s]!!.add(intArrayOf(top, top + h))
                    }
                }
            }
        }

        drawTitleText(g, x, y, page, chapNum, guiScale)
        for (data in content) {
            if (data == null) continue
            when (data[0]) {
                0 -> {
                    if (data.size < 4) continue
                    val s = data[1]
                    val tt = y + TY + data[3]
                    var push = 0
                    if (s >= 0 && s < pics.size) {
                        for (p in pics[s]!!) {
                            if (tt < p[1] && tt + 5 > p[0]) { // text overlaps picture
                                if (p[1] > push) push = p[1]
                            }
                        }
                    }
                    drawBookText(g, x, y, s, data[2], data[3], bookID, guiScale, push)
                }

                1 -> drawBookPic(g, x, y, data)
                2 -> drawBookIcon(g, x, y, data[1], data[2], data[3], data[4])
            }
        }
    }

    fun drawStateFlags(g: GuiGraphics, x: Int, y: Int, entity: LivingEntity?) {
        if (entity !is EntityShipBase) return
        val stats = entity.getStateEmotion(0)
        val maxStats = entity.getStateMinor(13)
        var idx = 0
        val start = if (entity.hasShipMounts()) 1 else 0
        for (i in start..15) {
            if (i >= maxStats) break
            g.blit(
                GUI_BOOK2,
                x + 45 + (idx % 8) * 9,
                y + 158 + (idx / 8) * 9,
                115,
                if (((stats shr i) and 1) == 1) 156 else 147,
                7,
                9
            )
            idx++
        }
    }

    // ---- Text: counter-scaled to native resolution ----
    private fun drawBookText(
        g: GuiGraphics,
        x: Int,
        y: Int,
        side: Int,
        offX: Int,
        offY: Int,
        bookID: Int,
        guiScale: Float,
        reservedY: Int
    ) {
        val key = "gui.shincolle.book.chap" + (bookID / 1000) + ".text" + (bookID % 1000) + "d" + side
        val text = I18n.get(key)
        if (text == key) return

        val font = Minecraft.getInstance().font
        val bx = x + (if (side == 0) LX else RX) + offX
        var by = y + TY + offY
        // Push below reserved picture/icon area on this side
        if (reservedY > by) by = reservedY + 2
        val lines = text.split("(?i)<\\s*br\\s*/?\\s*>|#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        // scale(1/guiScale) cancels DeskScreen's scale(guiScale):
        //   T*S * scale(inv) = T*scale(1.25)*scale(0.8) = T*scale(1.0) = pure translation to (leftPos,topPos).
        //   Screen position of (scx,scy) = leftPos + scx, topPos + scy.
        //   We want it to match the unscaled position: leftPos + bx*guiScale, topPos + by*guiScale.
        //   -> scx = bx*guiScale, scy = by*guiScale.
        val inv = 1.0f / guiScale
        g.pose().pushPose()
        g.pose().scale(inv, inv, 1.0f)

        val sx = Math.round(bx * guiScale)
        val sy = Math.round(by * guiScale)
        val ww = Math.round(MAXW * guiScale) // wrap width at native resolution
        var curY = 0

        for (line in lines) {
            if (line.isEmpty()) {
                curY += font.lineHeight + 1
                continue
            }
            val wrap = if (useMiSans())
                font.split(
                    Component.literal(line).withStyle(UnaryOperator { s: Style? -> s!!.withFont(FONT_MISANS) }),
                    ww
                )
                    .stream().map<FormattedCharSequence?> { obj: FormattedCharSequence? -> BookRenderer.forceFont(obj!!) }
                    .collect(Collectors.toList())
            else
                font.split(Component.literal(line), ww)
            for (seq in wrap) {
                g.drawString(font, seq, sx, sy + curY, 0, false)
                curY += font.lineHeight + 1
            }
        }
        g.pose().popPose()
    }

    // ---- Pictures & icons (unscaled — they follow background texture) ----
    private fun drawBookPic(g: GuiGraphics, x: Int, y: Int, data: IntArray) {
        if (data.size < 9) return
        g.blit(
            ResourceLocation.fromNamespaceAndPath(
                Shincolle.MODID,
                "textures/gui/book/bookpic0" + (if (data[4] == 0) 1 else data[4]) + ".png"
            ),
            x + (if (data[1] == 0) LX else RX + 1) + data[2], y + TY + 4 + data[3],
            data[5].toFloat(), data[6].toFloat(), data[7], data[8], 256, 256
        )
    }

    private fun drawBookIcon(g: GuiGraphics, x: Int, y: Int, side: Int, offX: Int, offY: Int, iconID: Int) {
        val stack = Values.ItemIconMap.get(iconID.toShort())
        if (stack != null) g.renderItem(stack, x + (if (side == 0) LX else RX + 1) + offX, y + TY + 4 + offY)
    }

    // ---- Title text ----
    private fun drawTitleText(g: GuiGraphics, x: Int, y: Int, page: Int, chap: Int, guiScale: Float) {
        val key =
            if (chap == 0) "gui.shincolle.book.chap0.title" else "gui.shincolle.book.chap" + chap + ".title" + page
        val text = I18n.get(key)
        if (text == key) return

        val font = Minecraft.getInstance().font
        val comp: Component = if (useMiSans())
            Component.literal(text).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.DARK_RED)
                .withStyle(UnaryOperator { s: Style? -> s!!.withFont(FONT_MISANS) })
        else
            Component.literal(text).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.DARK_RED)

        val inv = 1.0f / guiScale
        g.pose().pushPose()
        g.pose().scale(inv, inv, 1.0f)

        val cx = Math.round((x + 64) * guiScale)
        val cy = Math.round((y + 34) * guiScale)
        val tx = cx - font.width(comp) / 2

        if (useMiSans()) {
            val parts = font.split(comp, 2048)
            if (!parts.isEmpty()) g.drawString(font, BookRenderer.forceFont(parts.get(0)!!), tx, cy, 0xFFFFFF, false)
        } else {
            g.drawString(font, comp, tx, cy, 0xFFFFFF, false)
        }
        g.pose().popPose()
    }
}
