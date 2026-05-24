package org.trp.shincolle.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipePaperScreenRegressionTest {
    private static final Path SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/screen/RecipePaperScreen.java");

    @Test
    void recipePaperScreenShouldDrawReadableSectionHeaders() throws IOException {
        String source = Files.readString(SCREEN_SOURCE);
        assertTrue(source.contains("drawSectionLabel(guiGraphics, Component.translatable(\"gui.shincolle.recipepaper.material\"), 29, 6);"),
                "Recipe Paper screen should draw the materials header explicitly");
        assertTrue(source.contains("drawSectionLabel(guiGraphics, Component.translatable(\"gui.shincolle.recipepaper.result\"), 114, 24);"),
                "Recipe Paper screen should draw the result header explicitly");
        assertTrue(source.contains("SECTION_COLOR = 0xFFF1C8"),
                "Recipe Paper section headers should use a brighter foreground color for readability");
        assertTrue(source.contains("SECTION_SHADOW_COLOR"),
                "Recipe Paper section headers should keep a darker shadow pass for contrast");
    }
}
