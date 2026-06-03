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

class ChestInjectLootTableRegressionTest {
    private static final Path CHEST_LOOT_ROOT =
            Path.of("src/main/resources/data/shincolle/loot_table/inject/chests");
    private static final Path LEGACY_CHEST_LOOT_MODIFIER =
            Path.of("src/main/java/org/trp/shincolle/loot/LegacyChestLootModifier.java");
    private static final Path MOD_ITEMS =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.java");
    private static final Path CONFIG =
            Path.of("src/main/java/org/trp/shincolle/Config.java");

    private static final Pattern TYPE_PATTERN =
            Pattern.compile("\"type\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern NAME_PATTERN =
            Pattern.compile("\"name\"\\s*:\\s*\"(shincolle:[^\"]+)\"");
    private static final Pattern POOL_NAME_PATTERN =
            Pattern.compile("\"name\"\\s*:\\s*\"([a-z0-9_]+)\"");
    private static final Pattern CHANCE_PATTERN =
            Pattern.compile("\"chance\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");

    private static final Set<String> EXPECTED_CHEST_LOOT_FILES = Set.of(
            "basic.json",
            "mid.json",
            "high.json",
            "trial.json"
    );

    private static final Map<String, String> EXPECTED_POOL_PREFIX = Map.of(
            "basic.json", "shincolle_basic_",
            "mid.json", "shincolle_mid_",
            "high.json", "shincolle_high_",
            "trial.json", "shincolle_trial_"
    );

    private static final Map<String, Set<String>> EXPECTED_ITEMS = Map.of(
            "basic.json", Set.of(
                    "shincolle:grudge",
                    "shincolle:ammo",
                    "shincolle:abyss_metal",
                    "shincolle:abyss_polymetal",
                    "shincolle:ammo1",
                    "shincolle:ammo2",
                    "shincolle:instantconmat",
                    "shincolle:destroyer_i_spawn_egg",
                    "shincolle:destroyer_ro_spawn_egg",
                    "shincolle:destroyer_ha_spawn_egg",
                    "shincolle:destroyer_ni_spawn_egg",
                    "shincolle:trainingbook",
                    "shincolle:marriagering"
            ),
            "mid.json", Set.of(
                    "shincolle:grudge",
                    "shincolle:ammo",
                    "shincolle:ammo1",
                    "shincolle:ammo2",
                    "shincolle:ammo3",
                    "shincolle:abyss_metal",
                    "shincolle:abyss_polymetal",
                    "shincolle:polymetal_gravel",
                    "shincolle:instantconmat",
                    "shincolle:heavy_cruiser_ri_spawn_egg",
                    "shincolle:heavy_cruiser_ne_spawn_egg",
                    "shincolle:cruiser_tenryuu_spawn_egg",
                    "shincolle:cruiser_tatsuta_spawn_egg",
                    "shincolle:subm_ka_spawn_egg",
                    "shincolle:subm_yo_spawn_egg",
                    "shincolle:subm_so_spawn_egg",
                    "shincolle:trainingbook",
                    "shincolle:marriagering"
            ),
            "high.json", Set.of(
                    "shincolle:grudge",
                    "shincolle:ammo",
                    "shincolle:ammo1",
                    "shincolle:ammo2",
                    "shincolle:ammo3",
                    "shincolle:abyss_metal",
                    "shincolle:abyss_polymetal",
                    "shincolle:polymetal",
                    "shincolle:polymetal_gravel",
                    "shincolle:instantconmat",
                    "shincolle:carrier_wo_spawn_egg",
                    "shincolle:battleship_ru_spawn_egg",
                    "shincolle:battleship_ta_spawn_egg",
                    "shincolle:battleship_re_spawn_egg",
                    "shincolle:transport_wa_spawn_egg",
                    "shincolle:northern_hime_spawn_egg",
                    "shincolle:pointer_item",
                    "shincolle:trainingbook",
                    "shincolle:marriagering"
            ),
            "trial.json", Set.of(
                    "shincolle:ammo",
                    "shincolle:ammo1",
                    "shincolle:ammo2",
                    "shincolle:ammo3",
                    "shincolle:grudge",
                    "shincolle:abyss_metal",
                    "shincolle:abyss_polymetal",
                    "shincolle:instantconmat",
                    "shincolle:destroyer_shimakaze_spawn_egg",
                    "shincolle:cruiser_takao_spawn_egg",
                    "shincolle:cruiser_atago_spawn_egg",
                    "shincolle:battleship_nagato_spawn_egg",
                    "shincolle:battleship_yamato_spawn_egg",
                    "shincolle:subm_u511_spawn_egg",
                    "shincolle:subm_ro500_spawn_egg",
                    "shincolle:trainingbook",
                    "shincolle:marriagering"
            )
    );

    @Test
    void chestInjectLootTableFilesShouldStayWithinKnownLegacySet() throws IOException {
        Set<String> actual = listJsonNames(CHEST_LOOT_ROOT);
        assertTrue(actual.equals(new TreeSet<>(EXPECTED_CHEST_LOOT_FILES)),
                () -> "Chest inject loot table file set changed unexpectedly, found: "
                        + String.join(", ", actual));
    }

    @Test
    void chestInjectLootTablesShouldKeepExpectedStructureAndResolvableDrops() throws IOException {
        String modItems = Files.readString(MOD_ITEMS);
        List<String> issues = new ArrayList<>();

        for (String fileName : EXPECTED_CHEST_LOOT_FILES) {
            String content = Files.readString(CHEST_LOOT_ROOT.resolve(fileName));
            String type = readFirst(TYPE_PATTERN, content);
            Set<String> itemNames = readAll(NAME_PATTERN, content);
            Set<String> poolNames = readAll(POOL_NAME_PATTERN, content);
            Set<String> chances = readNumericStrings(CHANCE_PATTERN, content);

            if (!"minecraft:chest".equals(type)) {
                issues.add(fileName + " should keep loot table type minecraft:chest but was " + type);
            }
            if (!itemNames.equals(new TreeSet<>(EXPECTED_ITEMS.get(fileName)))) {
                issues.add(fileName + " should keep expected drop set, found: "
                        + String.join(", ", itemNames));
            }
            for (String itemName : itemNames) {
                if (!itemRegistrationExists(modItems, itemName)) {
                    issues.add(fileName + " references missing registered item " + itemName);
                }
            }
            if (!poolNames.contains(EXPECTED_POOL_PREFIX.get(fileName) + "common")
                    || !poolNames.contains(EXPECTED_POOL_PREFIX.get(fileName) + "rare")) {
                issues.add(fileName + " should keep common/rare pool names with prefix " + EXPECTED_POOL_PREFIX.get(fileName));
            }
            if (poolNames.size() != 2) {
                issues.add(fileName + " should keep exactly 2 named pools but found " + poolNames.size());
            }
            if (chances.size() != 2) {
                issues.add(fileName + " should keep exactly 2 random chance gates but found " + chances.size());
            }
        }

        assertTrue(issues.isEmpty(),
                () -> "Chest inject loot tables changed unexpectedly: " + String.join(", ", issues));
    }

    @Test
    void legacyChestLootModifierShouldKeepCurrentCategoryRanges() throws IOException {
        String source = Files.readString(LEGACY_CHEST_LOOT_MODIFIER);
        String config = Files.readString(CONFIG);

        assertTrue(source.contains("case \"basic\" -> chestId >= 0 && chestId <= 3;"),
                "basic chest category should continue covering chestId 0..3");
        assertTrue(source.contains("case \"mid\" -> chestId >= 4 && chestId <= 8;"),
                "mid chest category should continue covering chestId 4..8");
        assertTrue(source.contains("case \"high\" -> chestId == 9;"),
                "high chest category should continue mapping only chestId 9");
        assertTrue(source.contains("case \"trial\" -> chestId >= 7 && chestId <= 9;"),
                "trial chest category should continue covering chestId 7..9");

        for (int chestId = 0; chestId <= 9; chestId++) {
            assertTrue(config.contains("entries.add(\"" + chestId + ",shincolle:"),
                    "Default legacy loot config should keep entries for chestId " + chestId);
        }
    }

    private static boolean itemRegistrationExists(String modItems, String resourceLocation) {
        String path = resourceLocation.substring("shincolle:".length());
        return modItems.contains("ITEMS.register(\"" + path + "\"");
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

    private static Set<String> readAll(Pattern pattern, String content) {
        Set<String> values = new TreeSet<>();
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            values.add(matcher.group(1));
        }
        return values;
    }

    private static Set<String> readNumericStrings(Pattern pattern, String content) {
        Set<String> values = new TreeSet<>();
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            values.add(matcher.group(1));
        }
        return values;
    }
}
