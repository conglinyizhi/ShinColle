package org.trp.shincolle.init

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class CreativeTabContentRuleRegressionTest {
    @Test
    fun creativeTabShouldKeepUsingVariantHelpersWithoutDuplicateBaseItems() {
        val modTabs = Files.readString(MOD_TABS)

        VARIANT_HELPER_RULES.forEach { expectation ->
            assertTrue(modTabs.contains(expectation.helperCall)) {
                "Creative tab should keep using helper call ${expectation.helperCall}"
            }
            assertFalse(modTabs.contains(expectation.forbiddenDirectAccept)) {
                "Creative tab should not directly accept ${expectation.forbiddenDirectAccept} after variant helper coverage exists"
            }
        }
    }

    @Test
    fun creativeTabShouldKeepKeyUtilityItemsAndDebugItemInExpectedSections() {
        val modTabs = Files.readString(MOD_TABS)

        assertTrue(modTabs.contains("output.accept(ModItems.DESK_ITEM_BOOK.get());"))
        assertTrue(modTabs.contains("output.accept(ModItems.DESK_ITEM_RADAR.get());"))
        assertTrue(modTabs.contains("output.accept(ModItems.DEBUG_INSPECTOR.get());"))
    }

    private data class HelperPlacementExpectation(
        val helperCall: String,
        val forbiddenDirectAccept: String
    )

    companion object {
        private val MOD_TABS: Path = Path.of("src/main/java/org/trp/shincolle/init/ModTabs.java")
        private val VARIANT_HELPER_RULES = listOf(
            HelperPlacementExpectation("ModItems.addAbyssNuggetVariants(output);", "output.accept(ModItems.ABYSS_NUGGET.get());"),
            HelperPlacementExpectation("ModItems.addGrudgeVariants(output);", "output.accept(ModItems.GRUDGE.get());"),
            HelperPlacementExpectation("ModItems.addShipTankVariants(output);", "output.accept(ModItems.SHIP_TANK.get());"),
            HelperPlacementExpectation("ModItems.addCombatRationVariants(output);", "output.accept(ModItems.COMBAT_RATION.get());"),
            HelperPlacementExpectation("ModItems.addPointerVariants(output);", "output.accept(ModItems.POINTER_ITEM.get());")
        )
    }
}
