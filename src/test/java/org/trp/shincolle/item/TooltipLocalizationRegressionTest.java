package org.trp.shincolle.item;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TooltipLocalizationRegressionTest {

    private static final Path EN_US_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");
    private static final Path ZH_CN_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json");

    @Test
    void tooltipFallbackKeysShouldStayLocalizedAndReadable() throws IOException {
        String enUs = Files.readString(EN_US_LANG);
        String zhCn = Files.readString(ZH_CN_LANG);

        assertTrue(enUs.contains("\"item.shincolle.modernkit\": \"Modernization Toolkit\""),
                "ModernKit should keep the lowercase item translation alias");
        assertTrue(zhCn.contains("\"item.shincolle.modernkit\": \"近代化改修工具\""),
                "ModernKit should keep the lowercase Chinese item translation alias");
        assertTrue(enUs.contains("\"block.shincolle.blockvolblock\": \"Abyssal Volcano Block\""),
                "VolBlock should keep the block translation key");
        assertTrue(zhCn.contains("\"block.shincolle.blockvolblock\": \"深海火山方块\""),
                "VolBlock should keep the Chinese block translation key");
    }
}
