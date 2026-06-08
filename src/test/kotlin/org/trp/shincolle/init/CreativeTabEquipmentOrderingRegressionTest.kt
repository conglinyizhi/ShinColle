package org.trp.shincolle.init

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class CreativeTabEquipmentOrderingRegressionTest {
    @Test
    fun equipmentSectionShouldKeepLegacyEquipOrdering() {
        val modTabs = Files.readString(MOD_TABS)

        assertOrder(
            modTabs,
            listOf(
                "ModItems.addSortedLegacyEquipVariants(output, ModItems.EQUIP_AIRPLANE);",
                "ModItems.addSortedLegacyEquipVariants(output, ModItems.EQUIP_AMMO);",
                "ModItems.addSortedLegacyEquipVariants(output, ModItems.EQUIP_ARMOR);",
                "ModItems.addSortedLegacyEquipVariants(output, ModItems.EQUIP_CANNON);",
                "ModItems.addSortedLegacyEquipVariants(output, ModItems.EQUIP_CATAPULT);",
                "ModItems.addSortedLegacyEquipVariants(output, ModItems.EQUIP_COMPASS);",
                "ModItems.addSortedLegacyEquipVariants(output, ModItems.EQUIP_DRUM);",
                "ModItems.addShipTankVariants(output);",
                "ModItems.addSortedLegacyEquipVariants(output, ModItems.EQUIP_FLARE);",
                "ModItems.addSortedLegacyEquipVariants(output, ModItems.EQUIP_MACHINEGUN);",
                "ModItems.addSortedLegacyEquipVariants(output, ModItems.EQUIP_RADAR);",
                "ModItems.addSortedLegacyEquipVariants(output, ModItems.EQUIP_SEARCHLIGHT);",
                "ModItems.addSortedLegacyEquipVariants(output, ModItems.EQUIP_TORPEDO);",
                "ModItems.addSortedLegacyEquipVariants(output, ModItems.EQUIP_TURBINE);"
            ),
            "Creative tab equipment section should keep the preserved legacy equipment ordering"
        )
    }

    private fun assertOrder(source: String, snippets: List<String>, message: String) {
        var cursor = -1
        snippets.forEach { snippet ->
            val next = source.indexOf(snippet, cursor + 1)
            assertTrue(next >= 0) { "Expected snippet to exist: $snippet" }
            assertTrue(next > cursor) { "$message around $snippet" }
            cursor = next
        }
    }

    companion object {
        private val MOD_TABS: Path = Path.of("src/main/java/org/trp/shincolle/init/ModTabs.java")
    }
}
