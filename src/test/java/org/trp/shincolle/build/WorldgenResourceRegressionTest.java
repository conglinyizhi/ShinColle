package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldgenResourceRegressionTest {
    private static final Path CONFIGURED_FEATURE_ROOT =
            Path.of("src/main/resources/data/shincolle/worldgen/configured_feature");
    private static final Path PLACED_FEATURE_ROOT =
            Path.of("src/main/resources/data/shincolle/worldgen/placed_feature");
    private static final Path BIOME_MODIFIER_ROOT =
            Path.of("src/main/resources/data/shincolle/neoforge/biome_modifier");

    private static final Pattern TYPE_PATTERN =
            Pattern.compile("\"type\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern FEATURE_PATTERN =
            Pattern.compile("\"feature\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern FEATURES_PATTERN =
            Pattern.compile("\"features\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern BIOMES_PATTERN =
            Pattern.compile("\"biomes\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern STEP_PATTERN =
            Pattern.compile("\"step\"\\s*:\\s*\"([^\"]+)\"");

    private static final Map<String, String> EXPECTED_CONFIGURED_FEATURE_TYPES = Map.of(
            "polymetal_gravel.json", "minecraft:disk",
            "polymetal_ore.json", "minecraft:ore"
    );

    private static final Map<String, String> EXPECTED_PLACED_FEATURES = Map.of(
            "polymetal_gravel.json", "shincolle:polymetal_gravel",
            "polymetal_ore.json", "shincolle:polymetal_ore",
            "polymetal_ore_ocean_extra.json", "shincolle:polymetal_ore"
    );

    private static final Map<String, String> EXPECTED_BIOME_MODIFIER_FEATURES = Map.of(
            "add_polymetal_gravel.json", "shincolle:polymetal_gravel",
            "add_polymetal_ore.json", "shincolle:polymetal_ore",
            "add_polymetal_ore_ocean_extra.json", "shincolle:polymetal_ore_ocean_extra"
    );

    private static final Map<String, String> EXPECTED_BIOME_MODIFIER_BIOMES = Map.of(
            "add_polymetal_gravel.json", "#minecraft:is_ocean",
            "add_polymetal_ore.json", "#minecraft:is_overworld",
            "add_polymetal_ore_ocean_extra.json", "#minecraft:is_ocean"
    );

    @Test
    void configuredFeaturesShouldStayWithinKnownPolymetalSet() throws IOException {
        Set<String> actual = listJsonNames(CONFIGURED_FEATURE_ROOT);
        assertTrue(actual.equals(new TreeSet<>(EXPECTED_CONFIGURED_FEATURE_TYPES.keySet())),
                () -> "Configured feature set changed unexpectedly, found: "
                        + String.join(", ", actual));
    }

    @Test
    void placedFeaturesShouldStayWithinKnownPolymetalSet() throws IOException {
        Set<String> actual = listJsonNames(PLACED_FEATURE_ROOT);
        assertTrue(actual.equals(new TreeSet<>(EXPECTED_PLACED_FEATURES.keySet())),
                () -> "Placed feature set changed unexpectedly, found: "
                        + String.join(", ", actual));
    }

    @Test
    void biomeModifiersShouldStayWithinKnownPolymetalSet() throws IOException {
        Set<String> actual = listJsonNames(BIOME_MODIFIER_ROOT);
        assertTrue(actual.equals(new TreeSet<>(EXPECTED_BIOME_MODIFIER_FEATURES.keySet())),
                () -> "Biome modifier set changed unexpectedly, found: "
                        + String.join(", ", actual));
    }

    @Test
    void configuredPlacedAndBiomeModifierChainShouldRemainConsistent() throws IOException {
        List<String> issues = new ArrayList<>();

        for (Map.Entry<String, String> entry : EXPECTED_CONFIGURED_FEATURE_TYPES.entrySet()) {
            String content = Files.readString(CONFIGURED_FEATURE_ROOT.resolve(entry.getKey()));
            String type = readFirst(TYPE_PATTERN, content);
            if (!entry.getValue().equals(type)) {
                issues.add(entry.getKey() + " should keep configured feature type " + entry.getValue() + " but was " + type);
            }
        }

        Set<String> configuredIds = new TreeSet<>();
        for (String fileName : EXPECTED_CONFIGURED_FEATURE_TYPES.keySet()) {
            configuredIds.add("shincolle:" + stripJson(fileName));
        }

        Set<String> placedIds = new TreeSet<>();
        for (Map.Entry<String, String> entry : EXPECTED_PLACED_FEATURES.entrySet()) {
            String content = Files.readString(PLACED_FEATURE_ROOT.resolve(entry.getKey()));
            String feature = readFirst(FEATURE_PATTERN, content);
            placedIds.add("shincolle:" + stripJson(entry.getKey()));

            if (!entry.getValue().equals(feature)) {
                issues.add(entry.getKey() + " should keep feature reference " + entry.getValue() + " but was " + feature);
            }
            if (!configuredIds.contains(feature)) {
                issues.add(entry.getKey() + " references missing configured feature " + feature);
            }
        }

        for (Map.Entry<String, String> entry : EXPECTED_BIOME_MODIFIER_FEATURES.entrySet()) {
            String content = Files.readString(BIOME_MODIFIER_ROOT.resolve(entry.getKey()));
            String type = readFirst(TYPE_PATTERN, content);
            String features = readFirst(FEATURES_PATTERN, content);
            String biomes = readFirst(BIOMES_PATTERN, content);
            String step = readFirst(STEP_PATTERN, content);

            if (!"neoforge:add_features".equals(type)) {
                issues.add(entry.getKey() + " should keep biome modifier type neoforge:add_features but was " + type);
            }
            if (!entry.getValue().equals(features)) {
                issues.add(entry.getKey() + " should keep placed feature reference " + entry.getValue() + " but was " + features);
            }
            if (!EXPECTED_BIOME_MODIFIER_BIOMES.get(entry.getKey()).equals(biomes)) {
                issues.add(entry.getKey() + " should keep biome selector "
                        + EXPECTED_BIOME_MODIFIER_BIOMES.get(entry.getKey()) + " but was " + biomes);
            }
            if (!"underground_ores".equals(step)) {
                issues.add(entry.getKey() + " should keep generation step underground_ores but was " + step);
            }
            if (!placedIds.contains(features)) {
                issues.add(entry.getKey() + " references missing placed feature " + features);
            }
        }

        assertTrue(issues.isEmpty(),
                () -> "Worldgen resource chain changed unexpectedly: " + String.join(", ", issues));
    }

    private static Set<String> listJsonNames(Path root) throws IOException {
        try (Stream<Path> stream = Files.list(root)) {
            Set<String> names = new TreeSet<>();
            for (Path path : (Iterable<Path>) stream
                    .filter(Files::isRegularFile)
                    .filter(file -> file.toString().endsWith(".json"))::iterator) {
                names.add(path.getFileName().toString());
            }
            return names;
        }
    }

    private static String readFirst(Pattern pattern, String content) {
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static String stripJson(String fileName) {
        return fileName.substring(0, fileName.length() - 5);
    }
}
