package org.trp.shincolle.init

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class VariantCreativeTabRegistrationRegressionTest {
    @Test
    fun variantItemsShownInCreativeTabShouldUseDedicatedVariantHelpers() {
        val creativeTabContents = Files.readString(CONTENT_SOURCE)

        EXPECTATIONS.forEach { expectation ->
            assertTrue(creativeTabContents.contains("CreativeTabVariantHelper.${expectation.helperMethodName}(output)")) {
                "Creative tab population should keep using ${expectation.helperMethodName}"
            }
        }

        assertTrue(!creativeTabContents.contains("output.accept(ModItems.POINTER_ITEM.get())"))
    }

    private data class VariantHelperExpectation(val helperMethodName: String)

    companion object {
        private val CONTENT_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/init/ShinColleCreativeTabContents.kt")
        private val EXPECTATIONS = listOf(
            VariantHelperExpectation("addShipTankVariants"),
            VariantHelperExpectation("addCombatRationVariants"),
            VariantHelperExpectation("addGrudgeVariants"),
            VariantHelperExpectation("addAbyssNuggetVariants"),
            VariantHelperExpectation("addPointerVariants")
        )
    }
}
