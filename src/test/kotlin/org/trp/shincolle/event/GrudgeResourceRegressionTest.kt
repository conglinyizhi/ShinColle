package org.trp.shincolle.event

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

import org.junit.jupiter.api.Assertions.assertTrue

class GrudgeResourceRegressionTest {
    private val GRUDGE_MODEL: Path =
            Path.of("src/main/resources/assets/shincolle/models/item/grudge.json")
    private val GRUDGE_VARIANT_MODEL: Path =
            Path.of("src/main/resources/assets/shincolle/models/item/grudge1.json")
    private val EN_US_LANG: Path =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val JA_JP_LANG: Path =
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json")
    private val ZH_CN_LANG: Path =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")
    private val ZH_TW_LANG: Path =
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")

    @Test
    fun grudgeItemShouldStillExposeLegacyVariantOverride() {
        val baseModel = Files.readString(GRUDGE_MODEL)
        val variantModel = Files.readString(GRUDGE_VARIANT_MODEL)

        assertTrue(baseModel.contains("\"shincolle:legacy_variant\": 1.0")) {
            "Grudge item should still override to the legacy variant model"
        }
        assertTrue(baseModel.contains("\"model\": \"shincolle:item/grudge1\"")) {
            "Grudge item should still point variant 1 at the dedicated grudge1 model"
        }
        assertTrue(variantModel.contains("\"layer0\": \"shincolle:item/grudge1\"")) {
            "Legacy variant model should still render the dedicated grudge1 texture"
        }
    }

    @Test
    fun grudgeXpBlockAndFrameShouldKeepTranslatedBlockNames() {
        val enUs = Files.readString(EN_US_LANG)
        val jaJp = Files.readString(JA_JP_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)
        val zhTw = Files.readString(ZH_TW_LANG)

        assertTrue(enUs.contains("\"block.shincolle.blockframe\": \"Abyss Frame\"")) {
            "English lang should define the abyss frame block name"
        }
        assertTrue(enUs.contains("\"block.shincolle.grudge_xp_block\": \"Sublimated Grudge Lump\"")) {
            "English lang should define the grudge XP block name"
        }
        assertTrue(jaJp.contains("\"block.shincolle.blockframe\": \"深海支柱\"")) {
            "Japanese lang should define the abyss frame block name"
        }
        assertTrue(jaJp.contains("\"block.shincolle.grudge_xp_block\": \"昇華シタ怨念ノ塊\"")) {
            "Japanese lang should define the grudge XP block name"
        }
        assertTrue(zhCn.contains("\"block.shincolle.blockframe\": \"深海框架\"")) {
            "Simplified Chinese lang should define the abyss frame block name"
        }
        assertTrue(zhCn.contains("\"block.shincolle.grudge_xp_block\": \"升华怨念团块\"")) {
            "Simplified Chinese lang should define the grudge XP block name"
        }
        assertTrue(zhTw.contains("\"block.shincolle.blockframe\": \"深海支架\"")) {
            "Traditional Chinese lang should define the abyss frame block name"
        }
        assertTrue(zhTw.contains("\"block.shincolle.grudge_xp_block\": \"昇華的怨念團塊\"")) {
            "Traditional Chinese lang should define the grudge XP block name"
        }

        assertTrue(enUs.contains("\"item.shincolle.blockframe\": \"Abyss Frame\"")) {
            "English lang should define the abyss frame item name"
        }
        assertTrue(enUs.contains("\"item.shincolle.blockvolcore\": \"Abyssal Volcano Core\"")) {
            "English lang should define the volcano core item name"
        }
        assertTrue(enUs.contains("\"item.shincolle.grudge_xp_block\": \"Sublimated Grudge Lump\"")) {
            "English lang should define the grudge XP item name"
        }
        assertTrue(enUs.contains("\"item.shincolle.hostile_egg\": \"Hostile Ship (Small)\"")) {
            "English lang should define the hostile ship item name"
        }
        assertTrue(enUs.contains("\"item.shincolle.large_shipyard\": \"Hadal Hydrothermal Vortex\"")) {
            "English lang should define the large shipyard item name"
        }
        assertTrue(jaJp.contains("\"item.shincolle.blockframe\": \"深海支柱\"")) {
            "Japanese lang should define the abyss frame item name"
        }
        assertTrue(jaJp.contains("\"item.shincolle.blockvolcore\": \"深海火山コア\"")) {
            "Japanese lang should define the volcano core item name"
        }
        assertTrue(jaJp.contains("\"item.shincolle.grudge_xp_block\": \"昇華シタ怨念ノ塊\"")) {
            "Japanese lang should define the grudge XP item name"
        }
        assertTrue(jaJp.contains("\"item.shincolle.hostile_egg\": \"敵対艦娘（小型）\"")) {
            "Japanese lang should define the hostile ship item name"
        }
        assertTrue(jaJp.contains("\"item.shincolle.large_shipyard\": \"超深海ノ熱水噴出孔\"")) {
            "Japanese lang should define the large shipyard item name"
        }
        assertTrue(zhCn.contains("\"item.shincolle.blockframe\": \"深海框架\"")) {
            "Simplified Chinese lang should define the abyss frame item name"
        }
        assertTrue(zhCn.contains("\"item.shincolle.blockvolcore\": \"深海火山核心\"")) {
            "Simplified Chinese lang should define the volcano core item name"
        }
        assertTrue(zhCn.contains("\"item.shincolle.grudge_xp_block\": \"升华怨念团块\"")) {
            "Simplified Chinese lang should define the grudge XP item name"
        }
        assertTrue(zhCn.contains("\"item.shincolle.hostile_egg\": \"敌对舰娘（小型）\"")) {
            "Simplified Chinese lang should define the hostile ship item name"
        }
        assertTrue(zhCn.contains("\"item.shincolle.large_shipyard\": \"超深海热漩\"")) {
            "Simplified Chinese lang should define the large shipyard item name"
        }
        assertTrue(zhTw.contains("\"item.shincolle.blockframe\": \"深海支架\"")) {
            "Traditional Chinese lang should define the abyss frame item name"
        }
        assertTrue(zhTw.contains("\"item.shincolle.blockvolcore\": \"深海火山核心\"")) {
            "Traditional Chinese lang should define the volcano core item name"
        }
        assertTrue(zhTw.contains("\"item.shincolle.grudge_xp_block\": \"昇華的怨念團塊\"")) {
            "Traditional Chinese lang should define the grudge XP item name"
        }
        assertTrue(zhTw.contains("\"item.shincolle.hostile_egg\": \"敵對艦娘（小型）\"")) {
            "Traditional Chinese lang should define the hostile ship item name"
        }
        assertTrue(zhTw.contains("\"item.shincolle.large_shipyard\": \"超深海熱漩\"")) {
            "Traditional Chinese lang should define the large shipyard item name"
        }
    }

    @Test
    fun traditionalChineseShouldKeepPrimaryBlockDisplayNames() {
        val zhTw = Files.readString(ZH_TW_LANG)

        assertTrue(zhTw.contains("\"block.shincolle.blockcrane\": \"深海起重機\"")) {
            "Traditional Chinese lang should define the abyss crane block name"
        }
        assertTrue(zhTw.contains("\"block.shincolle.blockdesk\": \"深海提督的辦公桌\"")) {
            "Traditional Chinese lang should define the abyss desk block name"
        }
        assertTrue(zhTw.contains("\"block.shincolle.blockvolcore\": \"深海火山核心\"")) {
            "Traditional Chinese lang should define the volcano core block name"
        }
        assertTrue(zhTw.contains("\"block.shincolle.small_shipyard\": \"深海熱泉\"")) {
            "Traditional Chinese lang should define the small shipyard block name"
        }
        assertTrue(zhTw.contains("\"block.shincolle.large_shipyard\": \"超深海熱漩\"")) {
            "Traditional Chinese lang should define the large shipyard block name"
        }
    }
}
