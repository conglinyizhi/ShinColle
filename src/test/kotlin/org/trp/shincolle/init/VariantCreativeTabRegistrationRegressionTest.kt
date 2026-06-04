package org.trp.shincolle.init

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class VariantCreativeTabRegistrationRegressionTest {
    @Test
    fun variantItemsShownInCreativeTabShouldUseDedicatedVariantHelpers() {
        val modTabs = Files.readString(TAB_SOURCE)

        EXPECTATIONS.forEach { expectation ->
            assertTrue(modTabs.contains("ModItems.${expectation.helperMethodName}(output);")) {
                "Creative tab population should keep using ${expectation.helperMethodName}"
            }
        }

        assertTrue(!modTabs.contains("output.accept(ModItems.POINTER_ITEM.get());"))
    }

    private data class VariantHelperExpectation(val helperMethodName: String)

    companion object {
        private val TAB_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/init/ModTabs.java")
        private val EXPECTATIONS = listOf(
            VariantHelperExpectation("addShipTankVariants"),
            VariantHelperExpectation("addCombatRationVariants"),
            VariantHelperExpectation("addGrudgeVariants"),
            VariantHelperExpectation("addAbyssNuggetVariants"),
            VariantHelperExpectation("addPointerVariants")
        )
    }
}
