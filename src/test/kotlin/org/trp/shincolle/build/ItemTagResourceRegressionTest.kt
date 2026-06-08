package org.trp.shincolle.build

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.TreeSet
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.junit.jupiter.api.Assertions.assertTrue

class ItemTagResourceRegressionTest {
    private val TAG_ROOT: Path =
        Path.of("src/main/resources/data/shincolle/tags/item")
    private val EQUIP_TAG: Path =
        TAG_ROOT.resolve("ship_equip_items.json")
    private val SHIP_INVENTORY_HANDLER: Path =
        Path.of("src/main/java/org/trp/shincolle/inventory/ShipInventoryHandler.kt")
    private val MOD_ITEMS: Path =
        Path.of("src/main/java/org/trp/shincolle/init/ModItems.kt")

    private val TAG_VALUE_PATTERN: Pattern =
        Pattern.compile("\"(shincolle:[^\"]+)\"")
    private val EQUIP_ITEM_REGISTER_PATTERN: Pattern =
        Pattern.compile("ITEMS\\.register<[^>]*>\\(\\s*\"(equip[a-z0-9_]+)\"")

    private val EXPECTED_EQUIP_TAG_VALUES = setOf(
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
    )

    private val EXPECTED_ITEM_TAG_FILES = setOf(
        "ship_equip_items.json",
        "spawn_eggs.json",
        "boss_eggs.json",
        "ammunition.json",
        "repair_items.json",
        "ship_consumables.json",
        "tools.json",
        "materials.json"
    )

    @Test
    fun itemTagFilesShouldStayWithinKnownSet() {
        val actual = listJsonNames(TAG_ROOT)
        assertTrue(actual == EXPECTED_ITEM_TAG_FILES) {
            "Custom item tag file set changed unexpectedly, found: " +
                TreeSet(actual).joinToString(", ")
        }
    }

    @Test
    fun shipEquipItemTagShouldKeepExpectedValuesAndAppendSemantics() {
        val content = Files.readString(EQUIP_TAG)
        val values = readTagValues(content)

        assertTrue(content.contains("\"replace\": false")) {
            "ship_equip_items tag should keep replace=false so future extensions append safely"
        }
        assertTrue(values == TreeSet(EXPECTED_EQUIP_TAG_VALUES)) {
            "ship_equip_items values changed unexpectedly, found: " +
                TreeSet(values).joinToString(", ")
        }
    }

    @Test
    fun shipInventoryHandlerShouldKeepUsingTheShipEquipItemTag() {
        val inventory = Files.readString(SHIP_INVENTORY_HANDLER)

        assertTrue(inventory.contains("ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, \"ship_equip_items\")")) {
            "Ship inventory handler should keep binding equip-slot validation to tag shincolle:ship_equip_items"
        }
        assertTrue(inventory.contains("stack.item is LegacyEquipItem")) {
            "Equip slot validation should continue accepting legacy equip items"
        }
    }

    @Test
    fun shipEquipItemTagShouldMatchCurrentEquipItemRegistrations() {
        val modItems = Files.readString(MOD_ITEMS)
        val registeredEquipItems = TreeSet<String>()
        val matcher: Matcher = EQUIP_ITEM_REGISTER_PATTERN.matcher(modItems)
        while (matcher.find()) {
            registeredEquipItems.add("shincolle:" + matcher.group(1)!!)
        }

        assertTrue(registeredEquipItems == TreeSet(EXPECTED_EQUIP_TAG_VALUES)) {
            "Equip item registrations changed unexpectedly, found: " +
                registeredEquipItems.joinToString(", ")
        }
        assertTrue(readTagValues(Files.readString(EQUIP_TAG)) == registeredEquipItems) {
            "ship_equip_items tag should stay aligned with current equip item registrations, registered: " +
                registeredEquipItems.joinToString(", ")
        }
    }

    private fun listJsonNames(root: Path): Set<String> {
        val stream = Files.list(root)
        try {
            val names = TreeSet<String>()
            stream
                .filter { path -> Files.isRegularFile(path) }
                .filter { file -> file.toString().endsWith(".json") }
                .forEach { path ->
                    names.add(path.fileName.toString())
                }
            return names
        } finally {
            stream.close()
        }
    }

    private fun readTagValues(content: String): Set<String> {
        val values = TreeSet<String>()
        val matcher: Matcher = TAG_VALUE_PATTERN.matcher(content)
        while (matcher.find()) {
            values.add(matcher.group(1)!!)
        }
        return values
    }
}
