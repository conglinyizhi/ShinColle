package org.trp.shincolle.item;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShinCombRegressionTest {

    private static final Path MOD_ITEMS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.java");
    private static final Path MOD_TABS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModTabs.java");
    private static final Path MODEL_SOURCE =
            Path.of("src/main/resources/assets/shincolle/models/item/shincomb.json");
    private static final Path EN_US_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");
    private static final Path ZH_CN_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json");

    @Test
    void shinCombShouldStayRestoredAsLegacyItem() throws IOException {
        String modItems = Files.readString(MOD_ITEMS_SOURCE);
        String modTabs = Files.readString(MOD_TABS_SOURCE);
        String model = Files.readString(MODEL_SOURCE);
        String enUs = Files.readString(EN_US_LANG);
        String zhCn = Files.readString(ZH_CN_LANG);

        assertTrue(modItems.contains("SHIN_COMB = ITEMS.register(\"shincomb\""),
                "ModItems should keep registering the legacy ShinComb item");
        assertTrue(modTabs.contains("output.accept(ModItems.SHIN_COMB.get());"),
                "Creative tab should keep exposing the legacy ShinComb item");
        assertTrue(model.contains("\"layer0\": \"shincolle:item/shincomb\""),
                "ShinComb item model should keep its restored texture path");
        assertTrue(enUs.contains("\"item.shincolle.shincomb\": \"Abyssal Beehive\""),
                "English localization should keep the lowercase ShinComb key");
        assertTrue(zhCn.contains("\"item.shincolle.shincomb\": \"深海蜂巢\""),
                "Chinese localization should keep the lowercase ShinComb key");
    }
}
