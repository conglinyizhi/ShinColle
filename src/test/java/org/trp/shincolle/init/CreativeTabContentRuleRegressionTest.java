package org.trp.shincolle.init;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreativeTabContentRuleRegressionTest {
    private record HelperPlacementExpectation(String helperCall, String forbiddenDirectAccept) {
    }

    private static final Path MOD_TABS =
            Path.of("src/main/java/org/trp/shincolle/init/ModTabs.java");

    private static final List<HelperPlacementExpectation> VARIANT_HELPER_RULES = List.of(
            new HelperPlacementExpectation("ModItems.addAbyssNuggetVariants(output);", "output.accept(ModItems.ABYSS_NUGGET.get());"),
            new HelperPlacementExpectation("ModItems.addGrudgeVariants(output);", "output.accept(ModItems.GRUDGE.get());"),
            new HelperPlacementExpectation("ModItems.addShipTankVariants(output);", "output.accept(ModItems.SHIP_TANK.get());"),
            new HelperPlacementExpectation("ModItems.addCombatRationVariants(output);", "output.accept(ModItems.COMBAT_RATION.get());"),
            new HelperPlacementExpectation("ModItems.addPointerVariants(output);", "output.accept(ModItems.POINTER_ITEM.get());")
    );

    @Test
    void creativeTabShouldKeepUsingVariantHelpersWithoutDuplicateBaseItems() throws IOException {
        String modTabs = Files.readString(MOD_TABS);

        for (HelperPlacementExpectation expectation : VARIANT_HELPER_RULES) {
            assertTrue(modTabs.contains(expectation.helperCall()),
                    () -> "Creative tab should keep using helper call " + expectation.helperCall());
            assertFalse(modTabs.contains(expectation.forbiddenDirectAccept()),
                    () -> "Creative tab should not directly accept " + expectation.forbiddenDirectAccept()
                            + " after variant helper coverage exists");
        }
    }

    @Test
    void creativeTabShouldKeepKeyUtilityItemsAndDebugItemInExpectedSections() throws IOException {
        String modTabs = Files.readString(MOD_TABS);

        assertTrue(modTabs.contains("output.accept(ModItems.DESK_ITEM_BOOK.get());"),
                "Creative tab should keep the desk book utility item available");
        assertTrue(modTabs.contains("output.accept(ModItems.DESK_ITEM_RADAR.get());"),
                "Creative tab should keep the desk radar utility item available");
        assertTrue(modTabs.contains("output.accept(ModItems.DEBUG_INSPECTOR.get());"),
                "Creative tab should keep exposing the debug inspector at the end");
    }
}
