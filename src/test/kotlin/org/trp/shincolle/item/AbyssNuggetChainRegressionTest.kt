package org.trp.shincolle.item

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class AbyssNuggetChainRegressionTest {
    @Test
    fun abyssNuggetChainShouldKeepLegacyVariantRegistrationAndRecipeLinks() {
        val clientSrc = Files.readString(clientEventSource)
        val tabsSrc = Files.readString(tabsSource)
        val nuggetText = Files.readString(nuggetRecipe)
        val polyNuggetText = Files.readString(polyNuggetRecipe)
        val metalText = Files.readString(metalRecipe)
        val polymetalText = Files.readString(polymetalRecipe)
        val marriageRingText = Files.readString(marriageRingRecipe)

        assertTrue(clientSrc.contains("registerLegacyVariantProperty(ModItems.ABYSS_NUGGET.get());"))
        assertTrue(tabsSrc.contains("CreativeTabVariantHelper.addAbyssNuggetVariants(output)"))
        assertTrue(nuggetText.contains("\"id\": \"shincolle:abyss_nugget\""))
        assertTrue(polyNuggetText.contains("\"minecraft:custom_data\": \"{LegacyVariant:1}\""))
        assertTrue(metalText.contains("\"item\": \"shincolle:abyss_nugget\""))
        assertTrue(polymetalText.contains("\"id\": \"shincolle:abyss_polymetal\""))
        assertTrue(marriageRingText.contains("\"item\": \"shincolle:abyss_nugget\""))
    }

    companion object {
        private val clientEventSource: Path =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.java")
        private val tabsSource: Path =
            Path.of("src/main/java/org/trp/shincolle/init/ShinColleCreativeTabContents.kt")
        private val nuggetRecipe: Path = Path.of("src/main/resources/data/shincolle/recipe/abyss_nugget.json")
        private val polyNuggetRecipe: Path =
            Path.of("src/main/resources/data/shincolle/recipe/abyss_nugget_polymetal.json")
        private val metalRecipe: Path =
            Path.of("src/main/resources/data/shincolle/recipe/abyss_metal_from_nugget.json")
        private val polymetalRecipe: Path =
            Path.of("src/main/resources/data/shincolle/recipe/abyss_polymetal_from_nugget.json")
        private val marriageRingRecipe: Path = Path.of("src/main/resources/data/shincolle/recipe/marriagering.json")
    }
}
