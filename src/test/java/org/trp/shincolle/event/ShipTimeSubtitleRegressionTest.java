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
    private static final Path JA_JP =
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json");
    private static final Path ZH_CN =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json");
    private static final Path ZH_TW =
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json");

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
    void hourlySubtitleTranslationsShouldExistInAllMaintainedLanguages() throws IOException {
        String en = Files.readString(EN_US);
        String ja = Files.readString(JA_JP);
        String zh = Files.readString(ZH_CN);
        String zhTw = Files.readString(ZH_TW);
        for (int hour = 0; hour < 24; hour++) {
            String hourKey = String.format("\"subtitle.shincolle.ship_time.%02d\":", hour);
            assertTrue(en.contains(hourKey),
                    "English translations should include " + hourKey);
            assertTrue(ja.contains(hourKey),
                    "Japanese translations should include " + hourKey);
            assertTrue(zh.contains(hourKey),
                    "Chinese translations should include " + hourKey);
            assertTrue(zhTw.contains(hourKey),
                    "Traditional Chinese translations should include " + hourKey);
        }
    }

    @Test
    void coreShipSubtitleTranslationsShouldExistInAllMaintainedLanguages() throws IOException {
        String en = Files.readString(EN_US);
        String ja = Files.readString(JA_JP);
        String zh = Files.readString(ZH_CN);
        String zhTw = Files.readString(ZH_TW);
        String[] keys = {
                "\"subtitle.shincolle.ship_death\":",
                "\"subtitle.shincolle.ship_explode\":",
                "\"subtitle.shincolle.ship_feed\":",
                "\"subtitle.shincolle.ship_fireheavy\":",
                "\"subtitle.shincolle.ship_firelight\":",
                "\"subtitle.shincolle.ship_hurt\":",
                "\"subtitle.shincolle.ship_hit\":",
                "\"subtitle.shincolle.ship_idle\":",
                "\"subtitle.shincolle.ship_aircraft\":",
                "\"subtitle.shincolle.ship_ap_attack\":",
                "\"subtitle.shincolle.ship_ap_phase1\":",
                "\"subtitle.shincolle.ship_ap_phase2\":",
                "\"subtitle.shincolle.ship_levelup\":",
                "\"subtitle.shincolle.ship_marry\":",
                "\"subtitle.shincolle.ship_machinegun\":",
                "\"subtitle.shincolle.ship_yamato_ready\":",
                "\"subtitle.shincolle.ship_yamato_shot\":"
        };
        for (String key : keys) {
            assertTrue(en.contains(key), "English translations should include " + key);
            assertTrue(ja.contains(key), "Japanese translations should include " + key);
            assertTrue(zh.contains(key), "Chinese translations should include " + key);
            assertTrue(zhTw.contains(key), "Traditional Chinese translations should include " + key);
        }
    }
}
