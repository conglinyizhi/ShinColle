package org.trp.shincolle.menu;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DeskMenuProtocolRegressionTest {

    private static final Path DESK_MENU_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/menu/DeskMenu.java");
    private static final Path DESK_RADAR_ITEM_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/item/DeskItemRadar.java");
    private static final Path DESK_BOOK_ITEM_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/item/DeskItemBook.java");

    @Test
    void deskMenuShouldKeepClientPayloadContract() throws IOException {
        String menuSource = Files.readString(DESK_MENU_SOURCE);
        String radarSource = Files.readString(DESK_RADAR_ITEM_SOURCE);
        String bookSource = Files.readString(DESK_BOOK_ITEM_SOURCE);

        assertTrue(menuSource.contains("if (data == null) {\n            throw new IllegalStateException(\"Missing desk menu data.\");\n        }"),
                "DeskMenu should fail fast when the client payload is missing");
        assertTrue(menuSource.contains("return new Object[]{deskType, data.readInt(), data.readInt()};"),
                "DeskMenu should keep decoding two ints for non-block desk variants");
        assertTrue(radarSource.contains("buffer.writeInt(1);")
                        && radarSource.contains("buffer.writeInt(0);")
                        && radarSource.contains("buffer.writeInt(0);"),
                "DeskItemRadar should keep sending deskType=1 with two placeholder ints");
        assertTrue(bookSource.contains("buffer.writeInt(2);")
                        && bookSource.contains("buffer.writeInt(chap);")
                        && bookSource.contains("buffer.writeInt(page);"),
                "DeskItemBook fallback menu should keep sending deskType=2 with chapter/page payload");
    }

    @Test
    void deskBlockMenuShouldRejectStaleBlockEntitiesDuringValidation() throws IOException {
        String menuSource = Files.readString(DESK_MENU_SOURCE);

        assertTrue(menuSource.contains("if (playerInventory.player.level().getBlockEntity(pos) instanceof DeskBlockEntity desk) {\n                return new Object[]{deskType, desk};\n            }\n            throw new IllegalStateException(\"Desk block entity not found.\");"),
                "DeskMenu should fail fast when the synced desk block entity is missing");
        assertTrue(menuSource.contains("if (blockEntity.getLevel() == null || player.level().getBlockEntity(blockEntity.getBlockPos()) != blockEntity) {\n                return false;\n            }"),
                "DeskMenu should become invalid when the backing desk block entity is detached or replaced");
        assertTrue(menuSource.contains("case 1 -> player.getMainHandItem().getItem() instanceof DeskItemRadar\n                    || player.getOffhandItem().getItem() instanceof DeskItemRadar;"),
                "Desk radar menus should close once the player no longer holds a desk radar");
        assertTrue(menuSource.contains("case 2 -> player.getMainHandItem().getItem() instanceof DeskItemBook\n                    || player.getOffhandItem().getItem() instanceof DeskItemBook;"),
                "Desk book fallback menus should close once the player no longer holds a desk book");
    }
}
