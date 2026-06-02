package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FontResourceReferenceRegressionTest {
    private static final Path FONT_JSON =
            Path.of("src/main/resources/assets/shincolle/font/default.json");
    private static final Path MISANS_TTF =
            Path.of("src/main/resources/assets/shincolle/font/misans.ttf");
    private static final Path BOOK_RENDERER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/renderer/BookRenderer.java");

    @Test
    void fontDefinitionShouldKeepMiSansProviderAndMinecraftFallback() throws IOException {
        String fontJson = Files.readString(FONT_JSON);

        assertTrue(fontJson.contains("\"providers\""),
                "Font definition should keep its provider list");
        assertTrue(fontJson.contains("\"type\": \"ttf\""),
                "Font definition should keep the TTF provider for MiSans");
        assertTrue(fontJson.contains("\"file\": \"shincolle:misans.ttf\""),
                "Font definition should keep pointing at the bundled MiSans font file");
        assertTrue(fontJson.contains("\"type\": \"reference\""),
                "Font definition should keep the fallback provider");
        assertTrue(fontJson.contains("\"id\": \"minecraft:default\""),
                "Font definition should keep falling back to the vanilla default font");
    }

    @Test
    void bundledMiSansFontFileShouldRemainPresent() {
        assertTrue(Files.exists(MISANS_TTF),
                "Bundled MiSans font file should remain present for the custom font provider");
    }

    @Test
    void bookRendererShouldKeepUsingClientMiSansTogglePath() throws IOException {
        String bookRenderer = Files.readString(BOOK_RENDERER_SOURCE);

        assertTrue(bookRenderer.contains("return org.trp.shincolle.Config.useMiSansFont")
                        && bookRenderer.contains("&& org.trp.shincolle.Config.miSansOnlyForLegacyLogs;"),
                "Legacy book renderer should keep gating MiSans through both client font toggles");
        assertTrue(bookRenderer.contains("private static final ResourceLocation FONT_MISANS = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, \"default\");"),
                "Legacy book renderer should keep the ShinColle custom font id constant");
        assertTrue(bookRenderer.contains("withStyle(s -> s.withFont(FONT_MISANS))")
                        && bookRenderer.contains("s.withFont(FONT_MISANS)"),
                "Legacy book renderer should keep applying the ShinColle custom font when MiSans is enabled");
    }
}
