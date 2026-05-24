package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipTimeSubtitleRegressionTest {
    private static final Path SOUNDS =
            Path.of("src/main/resources/assets/shincolle/sounds.json");
    private static final Path EN_US =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");
    private static final Path ZH_CN =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json");

    @Test
    void eachHourlyTimekeepingSoundShouldUseDedicatedSubtitleKey() throws IOException {
        String sounds = Files.readString(SOUNDS);
        for (int hour = 0; hour < 24; hour++) {
            String hourKey = String.format("subtitle.shincolle.ship_time.%02d", hour);
            String soundEntry = String.format("\"ship-time%d\": { \"subtitle\": \"%s\"", hour, hourKey);
            assertTrue(sounds.contains(soundEntry),
                    "Timekeeping sound ship-time" + hour + " should point at subtitle key " + hourKey);
        }
    }

    @Test
    void hourlySubtitleTranslationsShouldExistInBothLanguages() throws IOException {
        String en = Files.readString(EN_US);
        String zh = Files.readString(ZH_CN);
        for (int hour = 0; hour < 24; hour++) {
            String hourKey = String.format("\"subtitle.shincolle.ship_time.%02d\":", hour);
            assertTrue(en.contains(hourKey),
                    "English translations should include " + hourKey);
            assertTrue(zh.contains(hourKey),
                    "Chinese translations should include " + hourKey);
        }
    }
}
