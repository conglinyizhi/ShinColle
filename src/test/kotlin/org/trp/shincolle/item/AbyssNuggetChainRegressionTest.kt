package org.trp.shincolle.item

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class AbyssNuggetChainRegressionTest {
    @Test
    fun abyssNuggetChainShouldKeepLegacyVariantRegistrationAndRecipeLinks() {
        val clientSource = Files.readString(CLIENT_EVENT_SOURCE)
        val tabsSource = Files.readString(TABS_SOURCE)
        val nuggetRecipe = Files.readString(NUGGET_RECIPE)
        val polyNuggetRecipe = Files.readString(POLY_NUGGET_RECIPE)
        val metalRecipe = Files.readString(METAL_RECIPE)
        val polymetalRecipe = Files.readString(POLYMETAL_RECIPE)
        val marriageRingRecipe = Files.readString(MARRIAGE_RING_RECIPE)

        assertTrue(clientSource.contains("registerLegacyVariantProperty(ModItems.ABYSS_NUGGET.get());"))
        assertTrue(tabsSource.contains("ModItems.addAbyssNuggetVariants(output);"))
        assertTrue(nuggetRecipe.contains("\"id\": \"shincolle:abyss_nugget\""))
        assertTrue(polyNuggetRecipe.contains("\"minecraft:custom_data\": \"{LegacyVariant:1}\""))
        assertTrue(metalRecipe.contains("\"item\": \"shincolle:abyss_nugget\""))
        assertTrue(polymetalRecipe.contains("\"id\": \"shincolle:abyss_polymetal\""))
        assertTrue(marriageRingRecipe.contains("\"item\": \"shincolle:abyss_nugget\""))
    }

    companion object {
        private val NUGGET_ITEM_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/item/AbyssNuggetItem.java")
        private val CLIENT_EVENT_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.java")
        private val TABS_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/init/ModTabs.java")
        private val NUGGET_RECIPE: Path = Path.of("src/main/resources/data/shincolle/recipe/abyss_nugget.json")
        private val POLY_NUGGET_RECIPE: Path = Path.of("src/main/resources/data/shincolle/recipe/abyss_nugget_polymetal.json")
        private val METAL_RECIPE: Path = Path.of("src/main/resources/data/shincolle/recipe/abyss_metal_from_nugget.json")
        private val POLYMETAL_RECIPE: Path = Path.of("src/main/resources/data/shincolle/recipe/abyss_polymetal_from_nugget.json")
        private val MARRIAGE_RING_RECIPE: Path = Path.of("src/main/resources/data/shincolle/recipe/marriagering.json")
    }
}
