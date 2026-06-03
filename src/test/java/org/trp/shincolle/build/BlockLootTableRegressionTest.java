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

class BlockLootTableRegressionTest {
    private static final Path BLOCK_LOOT_ROOT =
            Path.of("src/main/resources/data/shincolle/loot_table/blocks");
    private static final Path MOD_BLOCKS =
            Path.of("src/main/java/org/trp/shincolle/init/ModBlocks.java");
    private static final Path MOD_ITEMS =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.java");

    private static final Pattern BLOCK_REGISTER_PATTERN =
            Pattern.compile("BLOCKS\\.register\\(\"([a-z0-9_]+)\"");
    private static final Pattern TYPE_PATTERN =
            Pattern.compile("\"type\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern DROP_NAME_PATTERN =
            Pattern.compile("\"name\"\\s*:\\s*\"(shincolle:[^\"]+)\"");

    private static final Set<String> EXPECTED_LOOT_TABLE_FILES = Set.of(
            "abyssium.json",
            "grudge_block.json",
            "grudge_heavy_block.json",
            "large_shipyard.json",
            "polymetal.json",
            "polymetal_gravel.json",
            "polymetal_ore.json",
            "small_shipyard.json"
    );

    private static final Set<String> EXPECTED_BLOCKS_WITHOUT_LOOT_TABLE = Set.of(
            "blockcrane",
            "blockdesk",
            "blockframe",
            "blockvolblock",
            "blockvolcore",
            "blockwaypoint",
            "grudge_xp_block"
    );

    private static final Map<String, String> EXPECTED_DROP_RESULTS = Map.of(
            "abyssium.json", "shincolle:abyssium",
            "grudge_block.json", "shincolle:grudge_block",
            "grudge_heavy_block.json", "shincolle:grudge_heavy_block",
            "large_shipyard.json", "shincolle:grudge_heavy_block",
            "polymetal.json", "shincolle:polymetal",
            "polymetal_gravel.json", "shincolle:polymetal_gravel",
            "polymetal_ore.json", "shincolle:abyss_polymetal",
            "small_shipyard.json", "shincolle:small_shipyard"
    );

    @Test
    void blockLootTableFilesShouldStayWithinKnownLegacySet() throws IOException {
        Set<String> actual = listJsonNames(BLOCK_LOOT_ROOT);
        assertTrue(actual.equals(new TreeSet<>(EXPECTED_LOOT_TABLE_FILES)),
                () -> "Block loot table file set changed unexpectedly, found: "
                        + String.join(", ", actual));
    }

    @Test
    void blockLootTablesShouldStayAlignedWithRegisteredBlocksAndKnownWhitelist() throws IOException {
        String modBlocks = Files.readString(MOD_BLOCKS);
        Set<String> registeredBlocks = new TreeSet<>();
        Matcher matcher = BLOCK_REGISTER_PATTERN.matcher(modBlocks);
        while (matcher.find()) {
            registeredBlocks.add(matcher.group(1));
        }

        Set<String> actualLootTables = new TreeSet<>();
        for (String fileName : EXPECTED_LOOT_TABLE_FILES) {
            actualLootTables.add(stripJson(fileName));
        }

        Set<String> withoutLootTable = new TreeSet<>(registeredBlocks);
        withoutLootTable.removeAll(actualLootTables);

        assertTrue(actualLootTables.equals(new TreeSet<>(EXPECTED_DROP_RESULTS.keySet().stream()
                .map(BlockLootTableRegressionTest::stripJson)
                .toList())),
                "Expected drop-result mapping should stay aligned with current loot table files");
        assertTrue(withoutLootTable.equals(new TreeSet<>(EXPECTED_BLOCKS_WITHOUT_LOOT_TABLE)),
                () -> "Registered blocks without loot tables changed unexpectedly, found: "
                        + String.join(", ", withoutLootTable));
    }

    @Test
    void blockLootTablesShouldKeepExpectedDropTargetsAndResolvableItemBackings() throws IOException {
        String modItems = Files.readString(MOD_ITEMS);
        List<String> issues = new ArrayList<>();

        for (Map.Entry<String, String> entry : EXPECTED_DROP_RESULTS.entrySet()) {
            String fileName = entry.getKey();
            String content = Files.readString(BLOCK_LOOT_ROOT.resolve(fileName));
            String type = readFirst(TYPE_PATTERN, content);
            String drop = readFirst(DROP_NAME_PATTERN, content);

            if (!"minecraft:block".equals(type)) {
                issues.add(fileName + " should keep loot table type minecraft:block but was " + type);
            }
            if (!entry.getValue().equals(drop)) {
                issues.add(fileName + " should keep drop result " + entry.getValue() + " but was " + drop);
            }
            if (!content.contains("\"condition\": \"minecraft:survives_explosion\"")) {
                issues.add(fileName + " should keep survives_explosion condition");
            }
            if (drop == null || !itemRegistrationExists(modItems, drop)) {
                issues.add(fileName + " references missing registered item " + drop);
            }
        }

        assertTrue(issues.isEmpty(),
                () -> "Block loot tables changed unexpectedly: " + String.join(", ", issues));
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

    private static String stripJson(String fileName) {
        return fileName.substring(0, fileName.length() - 5);
    }
}
