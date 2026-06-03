package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LootModifierResourceRegressionTest {
    private static final Path LOOT_MODIFIER_ROOT =
            Path.of("src/main/resources/data/shincolle/loot_modifiers");
    private static final Path GLOBAL_LOOT_MODIFIERS =
            Path.of("src/main/resources/data/neoforge/loot_modifiers/global_loot_modifiers.json");

    private static final Pattern TYPE_PATTERN =
            Pattern.compile("\"type\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern CATEGORY_PATTERN =
            Pattern.compile("\"category\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern LOOT_TABLE_ID_PATTERN =
            Pattern.compile("\"loot_table_id\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern ENTRY_PATTERN =
            Pattern.compile("\"(shincolle:[^\"]+)\"");

    private static final Map<String, String> EXPECTED_FILE_CATEGORIES = Map.ofEntries(
            Map.entry("chest_inject_spawn_bonus.json", "id:0"),
            Map.entry("chest_inject_igloo.json", "id:1"),
            Map.entry("chest_inject_dungeon.json", "id:2"),
            Map.entry("chest_inject_village.json", "id:3"),
            Map.entry("chest_inject_mineshaft.json", "id:4"),
            Map.entry("chest_inject_pyramid.json", "id:5"),
            Map.entry("chest_inject_jungle_temple.json", "id:6"),
            Map.entry("chest_inject_nether_bridge.json", "id:7"),
            Map.entry("chest_inject_stronghold.json", "id:8"),
            Map.entry("chest_inject_end_city.json", "id:9")
    );

    private static final Map<String, Set<String>> EXPECTED_FILE_TARGETS = Map.ofEntries(
            Map.entry("chest_inject_spawn_bonus.json", Set.of("minecraft:chests/spawn_bonus_chest")),
            Map.entry("chest_inject_igloo.json", Set.of("minecraft:chests/igloo_chest")),
            Map.entry("chest_inject_dungeon.json", Set.of("minecraft:chests/simple_dungeon")),
            Map.entry("chest_inject_village.json", Set.of(
                    "minecraft:chests/village/village_armorer",
                    "minecraft:chests/village/village_butcher",
                    "minecraft:chests/village/village_cartographer",
                    "minecraft:chests/village/village_desert_house",
                    "minecraft:chests/village/village_fisher",
                    "minecraft:chests/village/village_fletcher",
                    "minecraft:chests/village/village_mason",
                    "minecraft:chests/village/village_plains_house",
                    "minecraft:chests/village/village_savanna_house",
                    "minecraft:chests/village/village_shepherd",
                    "minecraft:chests/village/village_snowy_house",
                    "minecraft:chests/village/village_taiga_house",
                    "minecraft:chests/village/village_tannery",
                    "minecraft:chests/village/village_temple",
                    "minecraft:chests/village/village_toolsmith",
                    "minecraft:chests/village/village_weaponsmith"
            )),
            Map.entry("chest_inject_mineshaft.json", Set.of("minecraft:chests/abandoned_mineshaft")),
            Map.entry("chest_inject_pyramid.json", Set.of("minecraft:chests/desert_pyramid")),
            Map.entry("chest_inject_jungle_temple.json", Set.of("minecraft:chests/jungle_temple")),
            Map.entry("chest_inject_nether_bridge.json", Set.of("minecraft:chests/nether_bridge")),
            Map.entry("chest_inject_stronghold.json", Set.of(
                    "minecraft:chests/stronghold_corridor",
                    "minecraft:chests/stronghold_crossing",
                    "minecraft:chests/stronghold_library"
            )),
            Map.entry("chest_inject_end_city.json", Set.of("minecraft:chests/end_city_treasure"))
    );

    private static final Set<String> EXPECTED_GLOBAL_ENTRIES = Set.of(
            "shincolle:chest_inject_spawn_bonus",
            "shincolle:chest_inject_igloo",
            "shincolle:chest_inject_dungeon",
            "shincolle:chest_inject_village",
            "shincolle:chest_inject_mineshaft",
            "shincolle:chest_inject_pyramid",
            "shincolle:chest_inject_jungle_temple",
            "shincolle:chest_inject_nether_bridge",
            "shincolle:chest_inject_stronghold",
            "shincolle:chest_inject_end_city"
    );

    @Test
    void lootModifierFilesShouldStayWithinKnownLegacyChestInjectionSet() throws IOException {
        Set<String> actual = new TreeSet<>();
        try (Stream<Path> stream = Files.list(LOOT_MODIFIER_ROOT)) {
            for (Path path : (Iterable<Path>) stream
                    .filter(Files::isRegularFile)
                    .filter(file -> file.toString().endsWith(".json"))::iterator) {
                actual.add(path.getFileName().toString());
            }
        }

        assertTrue(actual.equals(new TreeSet<>(EXPECTED_FILE_CATEGORIES.keySet())),
                () -> "Legacy chest loot modifier file set changed unexpectedly, found: "
                        + String.join(", ", actual));
    }

    @Test
    void lootModifierFilesShouldKeepExpectedTypeCategoryAndLootTableTargets() throws IOException {
        List<String> issues = new ArrayList<>();

        for (Map.Entry<String, String> entry : EXPECTED_FILE_CATEGORIES.entrySet()) {
            String fileName = entry.getKey();
            Path file = LOOT_MODIFIER_ROOT.resolve(fileName);
            String content = Files.readString(file);

            String type = readFirst(TYPE_PATTERN, content);
            if (!"shincolle:legacy_chest_loot".equals(type)) {
                issues.add(fileName + " should keep type shincolle:legacy_chest_loot but was " + type);
            }

            String category = readFirst(CATEGORY_PATTERN, content);
            if (!entry.getValue().equals(category)) {
                issues.add(fileName + " should keep category " + entry.getValue() + " but was " + category);
            }

            Set<String> lootTables = readAll(LOOT_TABLE_ID_PATTERN, content);
            if (!lootTables.equals(EXPECTED_FILE_TARGETS.get(fileName))) {
                issues.add(fileName + " should keep loot table targets "
                        + String.join(", ", new TreeSet<>(EXPECTED_FILE_TARGETS.get(fileName)))
                        + " but found "
                        + String.join(", ", new TreeSet<>(lootTables)));
            }
        }

        assertTrue(issues.isEmpty(),
                () -> "Legacy chest loot modifier resources changed unexpectedly: " + String.join(", ", issues));
    }

    @Test
    void globalLootModifierIndexShouldListEveryLegacyChestInjectionFile() throws IOException {
        String content = Files.readString(GLOBAL_LOOT_MODIFIERS);
        Set<String> entries = readAll(ENTRY_PATTERN, content);
        Set<String> expected = new TreeSet<>(EXPECTED_GLOBAL_ENTRIES);

        assertTrue(content.contains("\"replace\": false"),
                "Global loot modifier index should keep append semantics with replace=false");
        assertTrue(entries.equals(expected),
                () -> "Global loot modifier index changed unexpectedly, found: "
                        + String.join(", ", new TreeSet<>(entries)));
    }

    @Test
    void everyIndexedLootModifierShouldResolveToAnExistingJsonFile() throws IOException {
        Set<String> missing = new LinkedHashSet<>();
        for (String entry : EXPECTED_GLOBAL_ENTRIES) {
            String path = entry.substring("shincolle:".length()) + ".json";
            if (!Files.exists(LOOT_MODIFIER_ROOT.resolve(path))) {
                missing.add(entry);
            }
        }

        assertTrue(missing.isEmpty(),
                () -> "Every indexed global loot modifier should resolve to a json file, missing: "
                        + String.join(", ", missing));
    }

    private static String readFirst(Pattern pattern, String content) {
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static Set<String> readAll(Pattern pattern, String content) {
        Set<String> values = new TreeSet<>();
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            values.add(matcher.group(1));
        }
        return values;
    }
}
