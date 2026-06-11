package org.trp.shincolle.init

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class CreativeTabMaterialsOrderingRegressionTest {
    @Test
    fun materialsSectionShouldKeepLegacyResourceOrdering() {
        val creativeTabContents = Files.readString(CREATIVE_TAB_CONTENTS)

        assertOrder(
            creativeTabContents,
            listOf(
                "output.accept(ModItems.ABYSS_METAL.get());",
                "CreativeTabVariantHelper.addAbyssNuggetVariants(output)",
                "output.accept(ModItems.AMMO_LIGHT.get());",
                "output.accept(ModItems.AMMO_LIGHT_CONTAINER.get());",
                "output.accept(ModItems.AMMO_HEAVY.get());",
                "output.accept(ModItems.AMMO_HEAVY_CONTAINER.get());",
                "CreativeTabVariantHelper.addGrudgeVariants(output)",
                "output.accept(ModItems.ABYSS_POLYMETAL.get());"
            ),
            "Creative tab materials section should keep the preserved legacy resource ordering"
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
