package org.trp.shincolle.block;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GrudgeXpBlockResourceRegressionTest {

    private static final Path GRUDGE_XP_BLOCK_TEXTURE =
            Path.of("src/main/resources/assets/shincolle/textures/block/grudge_xp_block.png");
    private static final Path GRUDGE_XP_BLOCK_ANIMATION =
            Path.of("src/main/resources/assets/shincolle/textures/block/grudge_xp_block.png.mcmeta");

    @Test
    void animatedGrudgeXpBlockTextureShouldKeepItsMcmeta() throws IOException {
        assertTrue(Files.exists(GRUDGE_XP_BLOCK_TEXTURE),
                "GrudgeXP block texture should stay present");
        assertTrue(Files.exists(GRUDGE_XP_BLOCK_ANIMATION),
                "Animated GrudgeXP block texture should keep its mcmeta");
        assertTrue(Files.readString(GRUDGE_XP_BLOCK_ANIMATION).contains("\"frametime\": 2"),
                "GrudgeXP animation metadata should keep the legacy animation speed");
    }
}
