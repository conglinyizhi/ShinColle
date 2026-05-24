package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GrudgeResourceRegressionTest {
    private static final Path GRUDGE_MODEL =
            Path.of("src/main/resources/assets/shincolle/models/item/grudge.json");
    private static final Path GRUDGE_VARIANT_MODEL =
            Path.of("src/main/resources/assets/shincolle/models/item/grudge1.json");
    private static final Path EN_US_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");
    private static final Path ZH_CN_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json");

    @Test
    void grudgeItemShouldStillExposeLegacyVariantOverride() throws IOException {
        String baseModel = Files.readString(GRUDGE_MODEL);
        String variantModel = Files.readString(GRUDGE_VARIANT_MODEL);

        assertTrue(baseModel.contains("\"shincolle:legacy_variant\": 1.0"),
                "Grudge item should still override to the legacy variant model");
        assertTrue(baseModel.contains("\"model\": \"shincolle:item/grudge1\""),
                "Grudge item should still point variant 1 at the dedicated grudge1 model");
        assertTrue(variantModel.contains("\"layer0\": \"shincolle:item/grudge1\""),
                "Legacy variant model should still render the dedicated grudge1 texture");
    }

    @Test
    void grudgeXpBlockAndFrameShouldKeepTranslatedBlockNames() throws IOException {
        String enUs = Files.readString(EN_US_LANG);
        String zhCn = Files.readString(ZH_CN_LANG);

        assertTrue(enUs.contains("\"block.shincolle.blockframe\": \"Abyss Frame\""),
                "English lang should define the abyss frame block name");
        assertTrue(enUs.contains("\"block.shincolle.grudge_xp_block\": \"Sublimated Grudge Lump\""),
                "English lang should define the grudge XP block name");
        assertTrue(zhCn.contains("\"block.shincolle.blockframe\": \"深海框架\""),
                "Simplified Chinese lang should define the abyss frame block name");
        assertTrue(zhCn.contains("\"block.shincolle.grudge_xp_block\": \"升华怨念团块\""),
                "Simplified Chinese lang should define the grudge XP block name");
    }
}
