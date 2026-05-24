package org.trp.shincolle.item;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OwnerPaperSourceRegressionTest {

    private static final Path OWNER_PAPER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/item/OwnerPaperItem.java");

    @Test
    void ownerPaperShouldKeepAlternatingStringBasedSignatures() throws IOException {
        String source = Files.readString(OWNER_PAPER_SOURCE);

        assertTrue(source.contains("tag.putString(SIGN_ID_A, player.getUUID().toString());"),
                "OwnerPaper should store signature A as a stable string UUID");
        assertTrue(source.contains("tag.putString(SIGN_ID_B, player.getUUID().toString());"),
                "OwnerPaper should store signature B as a stable string UUID");
        assertTrue(source.contains("if (tag.getBoolean(SIGN_POS)) {"),
                "OwnerPaper should keep alternating between signature slots");
        assertTrue(source.contains("tag.putBoolean(SIGN_POS, false);")
                        && source.contains("tag.putBoolean(SIGN_POS, true);"),
                "OwnerPaper should toggle signPos after each write");
    }
}
