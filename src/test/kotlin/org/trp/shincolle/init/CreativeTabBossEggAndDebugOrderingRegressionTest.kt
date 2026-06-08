package org.trp.shincolle.init

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

class CreativeTabBossEggAndDebugOrderingRegressionTest {
    @Test
    fun creativeTabShouldKeepUsingBossEggLoopInsteadOfManualAccepts() {
        val modTabs = Files.readString(MOD_TABS)
        val modItems = Files.readString(MOD_ITEMS)

        assertTrue(modTabs.contains("for (var egg : ModItems.BOSS_EGGS) {\n                    output.accept(egg.get());\n                }"))
        assertTrue(modItems.contains("public static final List<DeferredItem<BossSpawnEggItem>> BOSS_EGGS = new ArrayList<>();"))
        assertTrue(modItems.contains("BOSS_EGGS.add(egg);"))

        val matcher = REGISTER_BOSS_EGG_PATTERN.matcher(modItems)
        var registeredBossEggs = 0
        while (matcher.find()) {
            registeredBossEggs++
        }

        val listedBossEggs = countOccurrences(modItems, "_BOSS_EGG")
        assertTrue(registeredBossEggs > 0)
        assertTrue(listedBossEggs >= registeredBossEggs)
    }

    @Test
    fun debugInspectorShouldRemainTheLastCreativeTabEntry() {
        val modTabs = Files.readString(MOD_TABS)

        val debugSection = modTabs.indexOf("// ===== DEBUG (appended last, not in 1.12.2) =====")
        val debugAccept = modTabs.indexOf("output.accept(ModItems.DEBUG_INSPECTOR.get());")
        val buildEnd = modTabs.indexOf("}).build());")

        assertTrue(debugSection >= 0)
        assertTrue(debugAccept > debugSection)
        assertTrue(buildEnd > debugAccept)
        val trailingSegment = modTabs.substring(debugAccept, buildEnd)
        assertEquals(1, countOccurrences(trailingSegment, "output.accept("))
    }

    private fun countOccurrences(source: String, needle: String): Int {
        var count = 0
        var index = 0
        while (source.indexOf(needle, index).also { index = it } >= 0) {
            count++
            index += needle.length
        }
        return count
    }

    companion object {
        private val MOD_TABS: Path = Path.of("src/main/java/org/trp/shincolle/init/ModTabs.java")
        private val MOD_ITEMS: Path = Path.of("src/main/java/org/trp/shincolle/init/ModItems.java")
        private val REGISTER_BOSS_EGG_PATTERN: Pattern = Pattern.compile("registerBossEgg\\(\"")
    }
}
