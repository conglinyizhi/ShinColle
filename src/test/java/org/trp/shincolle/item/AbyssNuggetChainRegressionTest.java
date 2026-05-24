package org.trp.shincolle.item;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AbyssNuggetChainRegressionTest {

    private static final Path NUGGET_ITEM_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/item/AbyssNuggetItem.java");
    private static final Path CLIENT_EVENT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.java");
    private static final Path TABS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModTabs.java");
    private static final Path NUGGET_RECIPE =
            Path.of("src/main/resources/data/shincolle/recipe/abyss_nugget.json");
    private static final Path POLY_NUGGET_RECIPE =
            Path.of("src/main/resources/data/shincolle/recipe/abyss_nugget_polymetal.json");
    private static final Path METAL_RECIPE =
            Path.of("src/main/resources/data/shincolle/recipe/abyss_metal_from_nugget.json");
    private static final Path POLYMETAL_RECIPE =
            Path.of("src/main/resources/data/shincolle/recipe/abyss_polymetal_from_nugget.json");
    private static final Path MARRIAGE_RING_RECIPE =
            Path.of("src/main/resources/data/shincolle/recipe/marriagering.json");

    @Test
    void abyssNuggetLegacyCraftChainShouldStayRestored() throws IOException {
        String itemSource = Files.readString(NUGGET_ITEM_SOURCE);
        String clientSource = Files.readString(CLIENT_EVENT_SOURCE);
        String tabsSource = Files.readString(TABS_SOURCE);
        String nuggetRecipe = Files.readString(NUGGET_RECIPE);
        String polyNuggetRecipe = Files.readString(POLY_NUGGET_RECIPE);
        String metalRecipe = Files.readString(METAL_RECIPE);
        String polymetalRecipe = Files.readString(POLYMETAL_RECIPE);
        String marriageRingRecipe = Files.readString(MARRIAGE_RING_RECIPE);

        assertTrue(itemSource.contains("return Mth.clamp(customData.copyTag().getInt(TAG_VARIANT), 0, 1);"),
                "AbyssNuggetItem should keep the legacy two-variant storage");
        assertTrue(clientSource.contains("registerLegacyVariantProperty(ModItems.ABYSS_NUGGET.get());"),
                "Client item properties should keep the abyss nugget model override");
        assertTrue(tabsSource.contains("ModItems.addAbyssNuggetVariants(output);"),
                "Creative tab should keep exposing both abyss nugget variants");
        assertTrue(nuggetRecipe.contains("\"id\": \"shincolle:abyss_nugget\""),
                "Base abyss nugget recipe should still exist");
        assertTrue(polyNuggetRecipe.contains("\"minecraft:custom_data\": \"{LegacyVariant:1}\""),
                "Polymetal nugget recipe should still output the variant-1 nugget");
        assertTrue(metalRecipe.contains("\"item\": \"shincolle:abyss_nugget\""),
                "Abyss metal should still be craftable back from abyss nuggets");
        assertTrue(polymetalRecipe.contains("\"id\": \"shincolle:abyss_polymetal\""),
                "Polymetal item should still be craftable back from variant-1 nuggets");
        assertTrue(marriageRingRecipe.contains("\"item\": \"shincolle:abyss_nugget\""),
                "Marriage ring should keep using abyss nuggets like the legacy recipe");
    }
}
