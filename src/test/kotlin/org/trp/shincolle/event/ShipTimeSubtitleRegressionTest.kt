package org.trp.shincolle.event

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class ShipTimeSubtitleRegressionTest {
    private val SOUNDS: Path =
        Path.of("src/main/resources/assets/shincolle/sounds.json")
    private val EN_US: Path =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val JA_JP: Path =
        Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json")
    private val ZH_CN: Path =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")
    private val ZH_TW: Path =
        Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")

    @Test
    fun eachHourlyTimekeepingSoundShouldUseDedicatedSubtitleKey() {
        val sounds: String = Files.readString(SOUNDS)
        for (hour in 0 until 24) {
            val hourKey: String = String.format("subtitle.shincolle.ship_time.%02d", hour)
            val soundEntry: String = String.format("\"ship-time%d\": { \"subtitle\": \"%s\"", hour, hourKey)
            assertTrue(sounds.contains(soundEntry)) {
                "Timekeeping sound ship-time" + hour + " should point at subtitle key " + hourKey
            }
        }
    }

    @Test
    fun hourlySubtitleTranslationsShouldExistInAllMaintainedLanguages() {
        val en: String = Files.readString(EN_US)
        val ja: String = Files.readString(JA_JP)
        val zh: String = Files.readString(ZH_CN)
        val zhTw: String = Files.readString(ZH_TW)
        for (hour in 0 until 24) {
            val hourKey: String = String.format("\"subtitle.shincolle.ship_time.%02d\":", hour)
            assertTrue(en.contains(hourKey)) {
                "English translations should include " + hourKey
            }
            assertTrue(ja.contains(hourKey)) {
                "Japanese translations should include " + hourKey
            }
            assertTrue(zh.contains(hourKey)) {
                "Chinese translations should include " + hourKey
            }
            assertTrue(zhTw.contains(hourKey)) {
                "Traditional Chinese translations should include " + hourKey
            }
        }
    }

    @Test
    fun coreShipSubtitleTranslationsShouldExistInAllMaintainedLanguages() {
        val en: String = Files.readString(EN_US)
        val ja: String = Files.readString(JA_JP)
        val zh: String = Files.readString(ZH_CN)
        val zhTw: String = Files.readString(ZH_TW)
        val keys: Array<String> = arrayOf(
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
        )
        for (key in keys) {
            assertTrue(en.contains(key)) { "English translations should include " + key }
            assertTrue(ja.contains(key)) { "Japanese translations should include " + key }
            assertTrue(zh.contains(key)) { "Chinese translations should include " + key }
            assertTrue(zhTw.contains(key)) { "Traditional Chinese translations should include " + key }
        }
    }
}
