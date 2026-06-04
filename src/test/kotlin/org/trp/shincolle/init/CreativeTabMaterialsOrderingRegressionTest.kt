package org.trp.shincolle.init

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class CreativeTabMaterialsOrderingRegressionTest {
    @Test
    fun materialsSectionShouldKeepLegacyResourceOrdering() {
        val modTabs = Files.readString(MOD_TABS)

        assertOrder(
            modTabs,
            listOf(
                "output.accept(ModItems.ABYSS_METAL.get());",
                "ModItems.addAbyssNuggetVariants(output);",
                "output.accept(ModItems.AMMO_LIGHT.get());",
                "output.accept(ModItems.AMMO_LIGHT_CONTAINER.get());",
                "output.accept(ModItems.AMMO_HEAVY.get());",
                "output.accept(ModItems.AMMO_HEAVY_CONTAINER.get());",
                "ModItems.addGrudgeVariants(output);",
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
        private val MOD_TABS: Path = Path.of("src/main/java/org/trp/shincolle/init/ModTabs.java")
    }
}
