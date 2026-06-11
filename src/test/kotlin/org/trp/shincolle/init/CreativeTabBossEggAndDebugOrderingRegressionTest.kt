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
        val creativeTabContents = Files.readString(CREATIVE_TAB_CONTENTS)
        val modItems = Files.readString(MOD_ITEMS)

        assertTrue(creativeTabContents.contains("for (egg in ModItems.BOSS_EGGS) {\n            output.accept(egg!!.get())\n        }"))
        assertTrue(modItems.contains("val BOSS_EGGS: MutableList<DeferredItem<BossSpawnEggItem?>?> = ArrayList<DeferredItem<BossSpawnEggItem?>?>()"))
        assertTrue(modItems.contains("BOSS_EGGS.add(egg)"))

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
        val creativeTabContents = Files.readString(CREATIVE_TAB_CONTENTS)

        val debugMethod = creativeTabContents.indexOf("private fun addDebug(output: CreativeModeTab.Output)")
        val debugAccept = creativeTabContents.indexOf("output.accept(ModItems.DEBUG_INSPECTOR.get())")
        val fileEnd = creativeTabContents.lastIndexOf("}")

        assertTrue(debugMethod >= 0)
        assertTrue(debugAccept > debugMethod)
        assertTrue(fileEnd > debugAccept)
        val trailingSegment = creativeTabContents.substring(debugAccept, fileEnd)
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
        private val CREATIVE_TAB_CONTENTS: Path = Path.of("src/main/java/org/trp/shincolle/init/ShinColleCreativeTabContents.kt")
        private val MOD_ITEMS: Path = Path.of("src/main/java/org/trp/shincolle/init/ModItems.kt")
        private val REGISTER_BOSS_EGG_PATTERN: Pattern = Pattern.compile("registerBossEgg\\(\"")
    }
}
