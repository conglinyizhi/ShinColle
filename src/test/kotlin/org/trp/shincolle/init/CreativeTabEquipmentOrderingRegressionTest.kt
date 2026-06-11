package org.trp.shincolle.init

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class CreativeTabEquipmentOrderingRegressionTest {
    @Test
    fun equipmentSectionShouldKeepLegacyEquipOrdering() {
        val creativeTabContents = Files.readString(CREATIVE_TAB_CONTENTS)

        assertOrder(
            creativeTabContents,
            listOf(
                "CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_AIRPLANE)",
                "CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_AMMO)",
                "CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_ARMOR)",
                "CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_CANNON)",
                "CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_CATAPULT)",
                "CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_COMPASS)",
                "CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_DRUM)",
                "CreativeTabVariantHelper.addShipTankVariants(output)",
                "CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_FLARE)",
                "CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_MACHINEGUN)",
                "CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_RADAR)",
                "CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_SEARCHLIGHT)",
                "CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_TORPEDO)",
                "CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_TURBINE)"
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
        private val CREATIVE_TAB_CONTENTS: Path = Path.of("src/main/java/org/trp/shincolle/init/ShinColleCreativeTabContents.kt")
    }
}
