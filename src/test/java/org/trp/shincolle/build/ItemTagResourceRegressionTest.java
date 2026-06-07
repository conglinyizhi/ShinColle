package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemTagResourceRegressionTest {
    private static final Path TAG_ROOT =
            Path.of("src/main/resources/data/shincolle/tags/item");
    private static final Path EQUIP_TAG =
            TAG_ROOT.resolve("ship_equip_items.json");
    private static final Path SHIP_INVENTORY_HANDLER =
            Path.of("src/main/java/org/trp/shincolle/inventory/ShipInventoryHandler.kt");
    private static final Path MOD_ITEMS =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.kt");

    private static final Pattern TAG_VALUE_PATTERN =
            Pattern.compile("\"(shincolle:[^\"]+)\"");
    private static final Pattern EQUIP_ITEM_REGISTER_PATTERN =
            Pattern.compile("ITEMS\\.register<[^>]*>\\(\\s*\"(equip[a-z0-9_]+)\"");

    private static final Set<String> EXPECTED_EQUIP_TAG_VALUES = Set.of(
            "shincolle:equipairplane",
            "shincolle:equipammo",
            "shincolle:equiparmor",
            "shincolle:equipcannon",
            "shincolle:equipcatapult",
            "shincolle:equipcompass",
            "shincolle:equipdrum",
            "shincolle:equipflare",
            "shincolle:equipmachinegun",
            "shincolle:equipradar",
            "shincolle:equipsearchlight",
            "shincolle:equiptorpedo",
            "shincolle:equipturbine"
    );

    private static final Set<String> EXPECTED_ITEM_TAG_FILES = Set.of(
            "ship_equip_items.json",
            "spawn_eggs.json",
            "boss_eggs.json",
            "ammunition.json",
            "repair_items.json",
            "ship_consumables.json",
            "tools.json",
            "materials.json"
    );

    @Test
    void itemTagFilesShouldStayWithinKnownSet() throws IOException {
        Set<String> actual = listJsonNames(TAG_ROOT);
        assertTrue(actual.equals(EXPECTED_ITEM_TAG_FILES),
                () -> "Custom item tag file set changed unexpectedly, found: "
                        + String.join(", ", new TreeSet<>(actual)));
    }

    @Test
    void shipEquipItemTagShouldKeepExpectedValuesAndAppendSemantics() throws IOException {
        String content = Files.readString(EQUIP_TAG);
        Set<String> values = readTagValues(content);

        assertTrue(content.contains("\"replace\": false"),
                "ship_equip_items tag should keep replace=false so future extensions append safely");
        assertTrue(values.equals(new TreeSet<>(EXPECTED_EQUIP_TAG_VALUES)),
                () -> "ship_equip_items values changed unexpectedly, found: "
                        + String.join(", ", new TreeSet<>(values)));
    }

    @Test
    void shipInventoryHandlerShouldKeepUsingTheShipEquipItemTag() throws IOException {
        String inventory = Files.readString(SHIP_INVENTORY_HANDLER);

        assertTrue(inventory.contains("ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, \"ship_equip_items\")"),
                "Ship inventory handler should keep binding equip-slot validation to tag shincolle:ship_equip_items");
        assertTrue(inventory.contains("stack.item is LegacyEquipItem"),
                "Equip slot validation should continue accepting legacy equip items");
    }

    @Test
    void shipEquipItemTagShouldMatchCurrentEquipItemRegistrations() throws IOException {
        String modItems = Files.readString(MOD_ITEMS);
        Set<String> registeredEquipItems = new TreeSet<>();
        Matcher matcher = EQUIP_ITEM_REGISTER_PATTERN.matcher(modItems);
        while (matcher.find()) {
            registeredEquipItems.add("shincolle:" + matcher.group(1));
        }

        assertTrue(registeredEquipItems.equals(new TreeSet<>(EXPECTED_EQUIP_TAG_VALUES)),
                () -> "Equip item registrations changed unexpectedly, found: "
                        + String.join(", ", registeredEquipItems));
        assertTrue(readTagValues(Files.readString(EQUIP_TAG)).equals(registeredEquipItems),
                () -> "ship_equip_items tag should stay aligned with current equip item registrations, registered: "
                        + String.join(", ", registeredEquipItems));
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

    private static Set<String> readTagValues(String content) {
        Set<String> values = new TreeSet<>();
        Matcher matcher = TAG_VALUE_PATTERN.matcher(content);
        while (matcher.find()) {
            values.add(matcher.group(1));
        }
        return values;
    }
}
