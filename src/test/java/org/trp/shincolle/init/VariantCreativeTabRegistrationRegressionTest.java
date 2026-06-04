package org.trp.shincolle.init;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class VariantCreativeTabRegistrationRegressionTest {
    private record VariantHelperExpectation(String helperMethodName) {
    }

    private static final Path TAB_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModTabs.java");

    private static final List<VariantHelperExpectation> EXPECTATIONS = List.of(
            new VariantHelperExpectation("addShipTankVariants"),
            new VariantHelperExpectation("addCombatRationVariants"),
            new VariantHelperExpectation("addGrudgeVariants"),
            new VariantHelperExpectation("addAbyssNuggetVariants"),
            new VariantHelperExpectation("addPointerVariants")
    );

    @Test
    void variantItemsShownInCreativeTabShouldUseDedicatedVariantHelpers() throws IOException {
        String modTabs = Files.readString(TAB_SOURCE);

        for (VariantHelperExpectation expectation : EXPECTATIONS) {
            assertTrue(modTabs.contains("ModItems." + expectation.helperMethodName() + "(output);"),
                    () -> "Creative tab population should keep using " + expectation.helperMethodName());
        }

        assertTrue(!modTabs.contains("output.accept(ModItems.POINTER_ITEM.get());"),
                "Pointer item should no longer be inserted twice as the same base stack");
    }
}
