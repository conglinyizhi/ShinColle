package org.trp.shincolle.item

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class ShinCombRegressionTest {
    @Test
    fun shinCombShouldStayRestoredAsLegacyItem() {
        val modTabs = Files.readString(MOD_TABS_SOURCE)
        val model = Files.readString(MODEL_SOURCE)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(modTabs.contains("output.accept(ModItems.SHIN_COMB.get());"))
        assertTrue(model.contains("\"layer0\": \"shincolle:item/shincomb\""))
        assertTrue(enUs.contains("\"item.shincolle.shincomb\": \"Abyssal Beehive\""))
        assertTrue(zhCn.contains("\"item.shincolle.shincomb\": \"深海蜂巢\""))
    }

    companion object {
        private val MOD_TABS_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/init/ModTabs.java")
        private val MODEL_SOURCE: Path = Path.of("src/main/resources/assets/shincolle/models/item/shincomb.json")
        private val EN_US_LANG: Path = Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
        private val ZH_CN_LANG: Path = Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")
    }
}
