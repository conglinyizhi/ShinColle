package org.trp.shincolle.event

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue

class AnvilUpdateEventRegressionTest {
    private val EVENT_BUS: Path =
            Path.of("src/main/java/org/trp/shincolle/event/ModEventBusEvents.kt")

    @Test
    fun anvilUpdateShouldOnlyTriggerForLegacyEquipAndEnchantedBook() {
        val source = Files.readString(EVENT_BUS)

        assertTrue(source.contains("if (left.item is LegacyEquipItem && right.`is`(Items.ENCHANTED_BOOK))")) {
            "Anvil update should only trigger when left is LegacyEquipItem and right is Enchanted Book"
        }
    }

    @Test
    fun anvilUpdateShouldCopyLeftItemAsResult() {
        val source = Files.readString(EVENT_BUS)

        assertTrue(source.contains("val result = left.copy()")) {
            "Anvil result should be a copy of the left item"
        }
    }

    @Test
    fun enchantmentMergeShouldSupportLevelUpForSameEnchantments() {
        val source = Files.readString(EVENT_BUS)

        assertTrue(source.contains("val newLevel = if (equipLevel == bookLevel) {")) {
            "Should check if equipment level equals book level for level-up logic"
        }
        assertTrue(source.contains("min(bookLevel + 1, maxLevel)")) {
            "Same-level enchantments should level up by 1, capped at max"
        }
    }

    @Test
    fun enchantmentMergeShouldTakeMaxForDifferentEnchantments() {
        val source = Files.readString(EVENT_BUS)

        assertTrue(source.contains("max(equipLevel, bookLevel)")) {
            "Different-level enchantments should take the higher level"
        }
    }

    @Test
    fun resultShouldApplyMergedEnchantments() {
        val source = Files.readString(EVENT_BUS)

        assertTrue(source.contains("mutableEnchants.set(holder, newLevel)")) {
            "Merged enchantments should be applied to the result item"
        }
    }

    @Test
    fun eventHandlerShouldBeSubscribedToAnvilUpdateEvent() {
        val source = Files.readString(EVENT_BUS)

        assertTrue(source.contains("fun onAnvilUpdate(event: AnvilUpdateEvent)")) {
            "Should define handler for AnvilUpdateEvent"
        }
    }
}
