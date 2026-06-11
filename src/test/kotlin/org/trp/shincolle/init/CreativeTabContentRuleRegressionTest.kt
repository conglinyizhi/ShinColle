package org.trp.shincolle.init

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class CreativeTabContentRuleRegressionTest {
    @Test
    fun creativeTabShouldKeepUsingVariantHelpersWithoutDuplicateBaseItems() {
        val creativeTabContents = Files.readString(CREATIVE_TAB_CONTENTS)

        VARIANT_HELPER_RULES.forEach { expectation ->
            assertTrue(creativeTabContents.contains(expectation.helperCall)) {
                "Creative tab should keep using helper call ${expectation.helperCall}"
            }
            assertFalse(creativeTabContents.contains(expectation.forbiddenDirectAccept)) {
                "Creative tab should not directly accept ${expectation.forbiddenDirectAccept} after variant helper coverage exists"
            }
        }
    }

    @Test
    fun creativeTabShouldKeepKeyUtilityItemsAndDebugItemInExpectedSections() {
        val creativeTabContents = Files.readString(CREATIVE_TAB_CONTENTS)

        assertTrue(creativeTabContents.contains("output.accept(ModItems.DESK_ITEM_BOOK.get())"))
        assertTrue(creativeTabContents.contains("output.accept(ModItems.DESK_ITEM_RADAR.get())"))
        assertTrue(creativeTabContents.contains("output.accept(ModItems.DEBUG_INSPECTOR.get())"))
    }

    private data class HelperPlacementExpectation(
        val helperCall: String,
        val forbiddenDirectAccept: String
    )

    companion object {
        private val CREATIVE_TAB_CONTENTS: Path = Path.of("src/main/java/org/trp/shincolle/init/ShinColleCreativeTabContents.kt")
        private val VARIANT_HELPER_RULES = listOf(
            HelperPlacementExpectation("CreativeTabVariantHelper.addAbyssNuggetVariants(output)", "output.accept(ModItems.ABYSS_NUGGET.get())"),
            HelperPlacementExpectation("CreativeTabVariantHelper.addGrudgeVariants(output)", "output.accept(ModItems.GRUDGE.get())"),
            HelperPlacementExpectation("CreativeTabVariantHelper.addShipTankVariants(output)", "output.accept(ModItems.SHIP_TANK.get())"),
            HelperPlacementExpectation("CreativeTabVariantHelper.addCombatRationVariants(output)", "output.accept(ModItems.COMBAT_RATION.get())"),
            HelperPlacementExpectation("CreativeTabVariantHelper.addPointerVariants(output)", "output.accept(ModItems.POINTER_ITEM.get())")
        )
    }
}
