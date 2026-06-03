package org.trp.shincolle.block;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DeskBlockEntityPersistenceRegressionTest {
    private static final Path DESK_BLOCK_ENTITY =
            Path.of("src/main/java/org/trp/shincolle/block/entity/DeskBlockEntity.java");

    @Test
    void deskBlockEntityShouldPersistDeskGuiAndBookState() throws IOException {
        String source = Files.readString(DESK_BLOCK_ENTITY);

        assertTrue(source.contains("private int guiFunc = 0;"),
                "Desk block entity should keep persisting the current desk GUI tab");
        assertTrue(source.contains("private int radarZoomLv = 0;"),
                "Desk block entity should keep persisting the current radar zoom level");
        assertTrue(source.contains("private int bookChap = 0;"),
                "Desk block entity should keep persisting the current manual chapter");
        assertTrue(source.contains("private int bookPage = 0;"),
                "Desk block entity should keep persisting the current manual page");
        assertTrue(source.contains("tag.putInt(\"guiFunc\", this.guiFunc);"),
                "Desk block entity NBT should save the current GUI tab");
        assertTrue(source.contains("tag.putInt(\"radarZoom\", this.radarZoomLv);"),
                "Desk block entity NBT should save the current radar zoom level");
        assertTrue(source.contains("tag.putInt(\"bookChap\", this.bookChap);"),
                "Desk block entity NBT should save the current manual chapter");
        assertTrue(source.contains("tag.putInt(\"bookPage\", this.bookPage);"),
                "Desk block entity NBT should save the current manual page");
        assertTrue(source.contains("this.guiFunc = Math.max(0, Math.min(4, tag.getInt(\"guiFunc\")));"),
                "Desk block entity NBT load should restore and clamp GUI tabs");
        assertTrue(source.contains("this.radarZoomLv = Math.max(0, Math.min(2, tag.getInt(\"radarZoom\")));"),
                "Desk block entity NBT load should restore and clamp radar zoom level");
        assertTrue(source.contains("this.bookChap = clampChapter(tag.getInt(\"bookChap\"));"),
                "Desk block entity NBT load should restore and clamp the saved manual chapter");
        assertTrue(source.contains("this.bookPage = clampPageForChapter(this.bookChap, tag.getInt(\"bookPage\"));"),
                "Desk block entity NBT load should restore and clamp the saved manual page");
    }

    @Test
    void deskBlockEntityShouldClampStateBeforeSyncingMenus() throws IOException {
        String source = Files.readString(DESK_BLOCK_ENTITY);

        assertTrue(source.contains("int next = Math.max(0, Math.min(4, guiFunc));"),
                "Desk GUI writes should clamp tabs into the supported range before syncing");
        assertTrue(source.contains("int next = Math.max(0, Math.min(2, radarZoomLv));"),
                "Desk radar zoom writes should clamp levels into the supported range before syncing");
        assertTrue(source.contains("int nextChap = clampChapter(bookChap);"),
                "Desk manual chapter writes should clamp the chapter before syncing");
        assertTrue(source.contains("int nextPage = clampPageForChapter(nextChap, this.bookPage);"),
                "Changing the desk manual chapter should re-clamp the current page to the new chapter range");
        assertTrue(source.contains("int next = clampPageForChapter(this.bookChap, bookPage);"),
                "Desk manual page writes should clamp the page against the active chapter before syncing");
        assertTrue(source.contains("private static int clampChapter(int chapter) {"),
                "Desk block entity should keep a dedicated chapter clamping helper");
        assertTrue(source.contains("return Math.max(0, Math.min(Values.PageLimit.length - 1, chapter));"),
                "Chapter clamping should use the configured page limit table");
        assertTrue(source.contains("private static int clampPageForChapter(int chapter, int page) {"),
                "Desk block entity should keep a dedicated page clamping helper");
        assertTrue(source.contains("return Math.max(0, Math.min(Values.PageLimit[clampedChapter], page));"),
                "Page clamping should respect the allowed page range of the active chapter");
        assertTrue(source.contains("return new DeskMenu(containerId, playerInventory, 0, this.bookChap, this.bookPage, this.guiFunc, this.radarZoomLv, this);"),
                "Desk menu creation should expose the persisted and clamped desk state back to the GUI");
    }

    @Test
    void deskBlockEntityShouldSkipNoopStateSyncs() throws IOException {
        String source = Files.readString(DESK_BLOCK_ENTITY);

        assertTrue(source.contains("if (this.guiFunc == next) {\n            return;\n        }"),
                "Desk GUI writes should not mark the block entity dirty when the tab did not actually change");
        assertTrue(source.contains("if (this.radarZoomLv == next) {\n            return;\n        }"),
                "Desk radar zoom writes should not mark the block entity dirty when the zoom level did not actually change");
        assertTrue(source.contains("if (this.bookChap == nextChap && this.bookPage == nextPage) {\n            return;\n        }"),
                "Desk manual chapter writes should not mark the block entity dirty when chapter and clamped page stay unchanged");
        assertTrue(source.contains("if (this.bookPage == next) {\n            return;\n        }"),
                "Desk manual page writes should not mark the block entity dirty when the clamped page stays unchanged");
    }
}
