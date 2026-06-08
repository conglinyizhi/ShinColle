package org.trp.shincolle.block

import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue

class GrudgeXpBlockResourceRegressionTest {

    private val GRUDGE_XP_BLOCK_TEXTURE: Path =
            Path.of("src/main/resources/assets/shincolle/textures/block/grudge_xp_block.png")
    private val GRUDGE_XP_BLOCK_ANIMATION: Path =
            Path.of("src/main/resources/assets/shincolle/textures/block/grudge_xp_block.png.mcmeta")
    private val GRUDGE_XP_BLOCK_MODEL: Path =
            Path.of("src/main/resources/assets/shincolle/models/block/grudge_xp_block.json")

    @Test
    fun animatedGrudgeXpBlockTextureShouldKeepItsMcmeta() {
        assertTrue(Files.exists(GRUDGE_XP_BLOCK_TEXTURE)) {
            "GrudgeXP block texture should stay present"
        }
        assertTrue(Files.exists(GRUDGE_XP_BLOCK_ANIMATION)) {
            "Animated GrudgeXP block texture should keep its mcmeta"
        }
        assertTrue(Files.readString(GRUDGE_XP_BLOCK_ANIMATION).contains("\"frametime\": 2")) {
            "GrudgeXP animation metadata should keep the legacy animation speed"
        }
    }

    @Test
    fun grudgeXpBlockModelShouldNotForceTranslucentRenderType() {
        val model = Files.readString(GRUDGE_XP_BLOCK_MODEL)
        assertTrue(!model.contains("\"render_type\": \"minecraft:translucent\"")) {
            "GrudgeXP block should use the default cube render path like the legacy block"
        }
    }
}
