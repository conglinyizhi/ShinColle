package org.trp.shincolle.build

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.TreeSet
import java.util.regex.Pattern

import org.junit.jupiter.api.Assertions.assertTrue

class WorldgenResourceRegressionTest {
    private val CONFIGURED_FEATURE_ROOT: Path =
            Path.of("src/main/resources/data/shincolle/worldgen/configured_feature")
    private val PLACED_FEATURE_ROOT: Path =
            Path.of("src/main/resources/data/shincolle/worldgen/placed_feature")
    private val BIOME_MODIFIER_ROOT: Path =
            Path.of("src/main/resources/data/shincolle/neoforge/biome_modifier")

    private val TYPE_PATTERN: Pattern =
            Pattern.compile("\"type\"\\s*:\\s*\"([^\"]+)\"")
    private val FEATURE_PATTERN: Pattern =
            Pattern.compile("\"feature\"\\s*:\\s*\"([^\"]+)\"")
    private val FEATURES_PATTERN: Pattern =
            Pattern.compile("\"features\"\\s*:\\s*\"([^\"]+)\"")
    private val BIOMES_PATTERN: Pattern =
            Pattern.compile("\"biomes\"\\s*:\\s*\"([^\"]+)\"")
    private val STEP_PATTERN: Pattern =
            Pattern.compile("\"step\"\\s*:\\s*\"([^\"]+)\"")

    private val EXPECTED_CONFIGURED_FEATURE_TYPES: Map<String, String> = mapOf(
            "polymetal_gravel.json" to "minecraft:disk",
            "polymetal_ore.json" to "minecraft:ore"
    )

    private val EXPECTED_PLACED_FEATURES: Map<String, String> = mapOf(
            "polymetal_gravel.json" to "shincolle:polymetal_gravel",
            "polymetal_ore.json" to "shincolle:polymetal_ore",
            "polymetal_ore_ocean_extra.json" to "shincolle:polymetal_ore"
    )

    private val EXPECTED_BIOME_MODIFIER_FEATURES: Map<String, String> = mapOf(
            "add_polymetal_gravel.json" to "shincolle:polymetal_gravel",
            "add_polymetal_ore.json" to "shincolle:polymetal_ore",
            "add_polymetal_ore_ocean_extra.json" to "shincolle:polymetal_ore_ocean_extra"
    )

    private val EXPECTED_BIOME_MODIFIER_BIOMES: Map<String, String> = mapOf(
            "add_polymetal_gravel.json" to "#minecraft:is_ocean",
            "add_polymetal_ore.json" to "#minecraft:is_overworld",
            "add_polymetal_ore_ocean_extra.json" to "#minecraft:is_ocean"
    )

    @Test
    fun configuredFeaturesShouldStayWithinKnownPolymetalSet() {
        val actual = listJsonNames(CONFIGURED_FEATURE_ROOT)
        assertTrue(actual.equals(TreeSet(EXPECTED_CONFIGURED_FEATURE_TYPES.keys))) {
            "Configured feature set changed unexpectedly, found: " +
                    actual.joinToString(", ")
        }
    }

    @Test
    fun placedFeaturesShouldStayWithinKnownPolymetalSet() {
        val actual = listJsonNames(PLACED_FEATURE_ROOT)
        assertTrue(actual.equals(TreeSet(EXPECTED_PLACED_FEATURES.keys))) {
            "Placed feature set changed unexpectedly, found: " +
                    actual.joinToString(", ")
        }
    }

    @Test
    fun biomeModifiersShouldStayWithinKnownPolymetalSet() {
        val actual = listJsonNames(BIOME_MODIFIER_ROOT)
        assertTrue(actual.equals(TreeSet(EXPECTED_BIOME_MODIFIER_FEATURES.keys))) {
            "Biome modifier set changed unexpectedly, found: " +
                    actual.joinToString(", ")
        }
    }

    @Test
    fun configuredPlacedAndBiomeModifierChainShouldRemainConsistent() {
        val issues = ArrayList<String>()

        for (entry in EXPECTED_CONFIGURED_FEATURE_TYPES) {
            val content = Files.readString(CONFIGURED_FEATURE_ROOT.resolve(entry.key))
            val type = readFirst(TYPE_PATTERN, content)
            if (entry.value != type) {
                issues.add(entry.key + " should keep configured feature type " + entry.value + " but was " + type)
            }
        }

        val configuredIds = TreeSet<String>()
        for (fileName in EXPECTED_CONFIGURED_FEATURE_TYPES.keys) {
            configuredIds.add("shincolle:" + stripJson(fileName))
        }

        val placedIds = TreeSet<String>()
        for (entry in EXPECTED_PLACED_FEATURES) {
            val content = Files.readString(PLACED_FEATURE_ROOT.resolve(entry.key))
            val feature = readFirst(FEATURE_PATTERN, content)
            placedIds.add("shincolle:" + stripJson(entry.key))

            if (entry.value != feature) {
                issues.add(entry.key + " should keep feature reference " + entry.value + " but was " + feature)
            }
            if (!configuredIds.contains(feature)) {
                issues.add(entry.key + " references missing configured feature " + feature)
            }
        }

        for (entry in EXPECTED_BIOME_MODIFIER_FEATURES) {
            val content = Files.readString(BIOME_MODIFIER_ROOT.resolve(entry.key))
            val type = readFirst(TYPE_PATTERN, content)
            val features = readFirst(FEATURES_PATTERN, content)
            val biomes = readFirst(BIOMES_PATTERN, content)
            val step = readFirst(STEP_PATTERN, content)

            if (type != "neoforge:add_features") {
                issues.add(entry.key + " should keep biome modifier type neoforge:add_features but was " + type)
            }
            if (entry.value != features) {
                issues.add(entry.key + " should keep placed feature reference " + entry.value + " but was " + features)
            }
            if (EXPECTED_BIOME_MODIFIER_BIOMES[entry.key]!! != biomes) {
                issues.add(entry.key + " should keep biome selector " +
                        EXPECTED_BIOME_MODIFIER_BIOMES[entry.key]!! + " but was " + biomes)
            }
            if (step != "underground_ores") {
                issues.add(entry.key + " should keep generation step underground_ores but was " + step)
            }
            if (!placedIds.contains(features)) {
                issues.add(entry.key + " references missing placed feature " + features)
            }
        }

        assertTrue(issues.isEmpty()) {
            "Worldgen resource chain changed unexpectedly: " + issues.joinToString(", ")
        }
    }

    private fun listJsonNames(root: Path): Set<String> {
        Files.list(root).use { stream ->
            val names = TreeSet<String>()
            for (path in stream
                    .filter(Files::isRegularFile)
                    .filter { it.toString().endsWith(".json") }
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
