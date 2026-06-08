package org.trp.shincolle.build

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.TreeSet
import java.util.regex.Pattern
import java.util.stream.Stream

class BlockLootTableRegressionTest {
    private val BLOCK_LOOT_ROOT: Path =
            Path.of("src/main/resources/data/shincolle/loot_table/blocks")
    private val MOD_BLOCKS: Path =
            Path.of("src/main/java/org/trp/shincolle/init/ModBlocks.kt")
    private val MOD_ITEMS: Path =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.kt")

    private val BLOCK_REGISTER_PATTERN: Pattern =
            Pattern.compile("BLOCKS\\.register\\(\"([a-z0-9_]+)\"")
    private val TYPE_PATTERN: Pattern =
            Pattern.compile("\"type\"\\s*:\\s*\"([^\"]+)\"")
    private val DROP_NAME_PATTERN: Pattern =
            Pattern.compile("\"name\"\\s*:\\s*\"(shincolle:[^\"]+)\"")

    private val EXPECTED_LOOT_TABLE_FILES: Set<String> = setOf(
            "abyssium.json",
            "grudge_block.json",
            "grudge_heavy_block.json",
            "large_shipyard.json",
            "polymetal.json",
            "polymetal_gravel.json",
            "polymetal_ore.json",
            "small_shipyard.json"
    )

    private val EXPECTED_BLOCKS_WITHOUT_LOOT_TABLE: Set<String> = setOf(
            "blockcrane",
            "blockdesk",
            "blockframe",
            "blockvolblock",
            "blockvolcore",
            "blockwaypoint",
            "grudge_xp_block"
    )

    private val EXPECTED_DROP_RESULTS: Map<String, String> = mapOf(
            "abyssium.json" to "shincolle:abyssium",
            "grudge_block.json" to "shincolle:grudge_block",
            "grudge_heavy_block.json" to "shincolle:grudge_heavy_block",
            "large_shipyard.json" to "shincolle:grudge_heavy_block",
            "polymetal.json" to "shincolle:polymetal",
            "polymetal_gravel.json" to "shincolle:polymetal_gravel",
            "polymetal_ore.json" to "shincolle:abyss_polymetal",
            "small_shipyard.json" to "shincolle:small_shipyard"
    )

    @Test
    fun blockLootTableFilesShouldStayWithinKnownLegacySet() {
        val actual = listJsonNames(BLOCK_LOOT_ROOT)
        assertTrue(actual.equals(TreeSet(EXPECTED_LOOT_TABLE_FILES))) {
            "Block loot table file set changed unexpectedly, found: " +
                    actual.joinToString(", ")
        }
    }

    @Test
    fun blockLootTablesShouldStayAlignedWithRegisteredBlocksAndKnownWhitelist() {
        val modBlocks = Files.readString(MOD_BLOCKS)
        val registeredBlocks = TreeSet<String>()
        val matcher = BLOCK_REGISTER_PATTERN.matcher(modBlocks)
        while (matcher.find()) {
            registeredBlocks.add(matcher.group(1)!!)
        }

        val actualLootTables = TreeSet<String>()
        for (fileName in EXPECTED_LOOT_TABLE_FILES) {
            actualLootTables.add(stripJson(fileName))
        }

        val withoutLootTable = TreeSet(registeredBlocks)
        withoutLootTable.removeAll(actualLootTables)

        assertTrue(actualLootTables == TreeSet(EXPECTED_DROP_RESULTS.keys.map(::stripJson))) {
            "Expected drop-result mapping should stay aligned with current loot table files"
        }
        assertTrue(withoutLootTable.equals(TreeSet(EXPECTED_BLOCKS_WITHOUT_LOOT_TABLE))) {
            "Registered blocks without loot tables changed unexpectedly, found: " +
                    withoutLootTable.joinToString(", ")
        }
    }

    @Test
    fun blockLootTablesShouldKeepExpectedDropTargetsAndResolvableItemBackings() {
        val modItems = Files.readString(MOD_ITEMS)
        val issues = ArrayList<String>()

        for ((fileName, expectedDrop) in EXPECTED_DROP_RESULTS) {
            val content = Files.readString(BLOCK_LOOT_ROOT.resolve(fileName))
            val type = readFirst(TYPE_PATTERN, content)
            val drop = readFirst(DROP_NAME_PATTERN, content)

            if ("minecraft:block" != type) {
                issues.add(fileName + " should keep loot table type minecraft:block but was " + type)
            }
            if (expectedDrop != drop) {
                issues.add(fileName + " should keep drop result " + expectedDrop + " but was " + drop)
            }
            if (!content.contains("\"condition\": \"minecraft:survives_explosion\"")) {
                issues.add(fileName + " should keep survives_explosion condition")
            }
            if (drop == null || !itemRegistrationExists(modItems, drop)) {
                issues.add(fileName + " references missing registered item " + drop)
            }
        }

        assertTrue(issues.isEmpty()) {
            "Block loot tables changed unexpectedly: " + issues.joinToString(", ")
        }
    }

    private fun itemRegistrationExists(modItems: String, resourceLocation: String): Boolean {
        val path = resourceLocation.substring("shincolle:".length)
        return modItems.contains("ITEMS.register(\"" + path + "\"")
    }

    private fun listJsonNames(root: Path): Set<String> {
        Files.list(root).use { stream ->
            val names = TreeSet<String>()
            for (path in stream
                    .filter(Files::isRegularFile)
                    .filter { file -> file.toString().endsWith(".json") }
                    .toList()) {
                names.add(path.fileName.toString())
            }
            return names
        }
    }

    private fun readFirst(pattern: Pattern, content: String): String? {
        val matcher = pattern.matcher(content)
        return if (matcher.find()) matcher.group(1)!! else null
    }

    private fun stripJson(fileName: String): String {
        return fileName.substring(0, fileName.length - 5)
    }
}
