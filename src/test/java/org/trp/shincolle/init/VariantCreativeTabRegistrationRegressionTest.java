package org.trp.shincolle.init;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class VariantCreativeTabRegistrationRegressionTest {
    private record VariantHelperExpectation(
            String itemFieldName,
            String helperMethodName,
            String itemClassName
    ) {
    }

    private static final Path ITEM_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.java");
    private static final Path TAB_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModTabs.java");

    private static final List<VariantHelperExpectation> EXPECTATIONS = List.of(
            new VariantHelperExpectation("SHIP_TANK", "addShipTankVariants", "ShipTankItem"),
            new VariantHelperExpectation("COMBAT_RATION", "addCombatRationVariants", "CombatRationItem"),
            new VariantHelperExpectation("GRUDGE", "addGrudgeVariants", "GrudgeItem"),
            new VariantHelperExpectation("ABYSS_NUGGET", "addAbyssNuggetVariants", "AbyssNuggetItem"),
            new VariantHelperExpectation("POINTER_ITEM", "addPointerVariants", "PointerItem")
    );

    @Test
    void variantItemsShownInCreativeTabShouldUseDedicatedVariantHelpers() throws IOException {
        String modItems = Files.readString(ITEM_SOURCE);
        String modTabs = Files.readString(TAB_SOURCE);

        for (VariantHelperExpectation expectation : EXPECTATIONS) {
            String helperBody = extractHelperBody(modItems, expectation.helperMethodName());

            assertTrue(modItems.contains("public static void " + expectation.helperMethodName() + "(CreativeModeTab.Output output) {"),
                    () -> "Expected ModItems to keep helper " + expectation.helperMethodName());
            assertTrue(helperBody.contains("Item resolved = " + expectation.itemFieldName() + ".get();"),
                    () -> expectation.helperMethodName() + " should keep resolving " + expectation.itemFieldName());
            assertTrue(helperBody.contains("resolved instanceof " + expectation.itemClassName()),
                    () -> expectation.helperMethodName() + " should keep checking for " + expectation.itemClassName());
            assertTrue(helperBody.contains(".addAllVariantsToCreativeTab(output);"),
                    () -> expectation.helperMethodName() + " should keep delegating to addAllVariantsToCreativeTab");
            assertTrue(helperBody.contains("output.accept(resolved);"),
                    () -> expectation.helperMethodName() + " should keep the single-item fallback");
            assertTrue(modTabs.contains("ModItems." + expectation.helperMethodName() + "(output);"),
                    () -> "Creative tab population should keep using " + expectation.helperMethodName());
        }

        assertTrue(!modTabs.contains("output.accept(ModItems.POINTER_ITEM.get());"),
                "Pointer item should no longer be inserted twice as the same base stack");
    }

    private static String extractHelperBody(String modItems, String helperMethodName) {
        String methodStart = "public static void " + helperMethodName + "(CreativeModeTab.Output output) {";
        int start = modItems.indexOf(methodStart);
        assertTrue(start >= 0, () -> "Expected helper method " + helperMethodName + " to exist");

        int nextMethod = modItems.indexOf("\n        public static void ", start + methodStart.length());
        int classEnd = modItems.lastIndexOf("\n}");
        int end = nextMethod >= 0 ? nextMethod : classEnd;
        return modItems.substring(start, end);
    }
}
