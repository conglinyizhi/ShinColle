package org.trp.shincolle.init;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CreativeTabSectionOrderingRegressionTest {
    private static final Path MOD_TABS =
            Path.of("src/main/java/org/trp/shincolle/init/ModTabs.java");

    @Test
    void miscToolsSectionShouldKeepLegacyUtilityOrdering() throws IOException {
        String modTabs = Files.readString(MOD_TABS);

        assertOrder(modTabs, List.of(
                "output.accept(ModItems.BUCKET_REPAIR.get());",
                "ModItems.addCombatRationVariants(output);",
                "output.accept(ModItems.DESK_ITEM_BOOK.get());",
                "output.accept(ModItems.DESK_ITEM_RADAR.get());",
                "output.accept(ModItems.INSTANT_CON_MAT.get());",
                "output.accept(ModItems.KAITAI_HAMMER.get());",
                "output.accept(ModItems.MARRIAGE_RING.get());",
                "output.accept(ModItems.MODERN_KIT.get());",
                "output.accept(ModItems.OWNER_PAPER.get());",
                "output.accept(ModItems.OP_TOOL.get());",
                "output.accept(ModItems.SHIN_COMB.get());",
                "ModItems.addPointerVariants(output);",
                "output.accept(ModItems.RECIPE_PAPER.get());",
                "output.accept(ModItems.REPAIR_GODDESS.get());",
                "output.accept(ModItems.TARGET_WRENCH.get());",
                "output.accept(ModItems.TRAINING_BOOK.get());",
                "output.accept(ModItems.TOY_AIRPLANE.get());"
        ), "Creative tab misc/tools section should keep the preserved legacy utility ordering");
    }

    @Test
    void blockSectionShouldKeepCoreBuildProgressionOrdering() throws IOException {
        String modTabs = Files.readString(MOD_TABS);

        assertOrder(modTabs, List.of(
                "output.accept(ModItems.ABYSSIUM.get());",
                "output.accept(ModItems.CRANE.get());",
                "output.accept(ModItems.DESK.get());",
                "output.accept(ModItems.FRAME_BLOCK.get());",
                "output.accept(ModItems.GRUDGE_BLOCK.get());",
                "output.accept(ModItems.GRUDGE_HEAVY_BLOCK.get());",
                "output.accept(ModItems.POLYMETAL.get());",
                "output.accept(ModItems.POLYMETAL_GRAVEL.get());",
                "output.accept(ModItems.POLYMETAL_ORE.get());",
                "output.accept(ModItems.SMALL_SHIPYARD.get());",
                "output.accept(ModItems.LARGE_SHIPYARD.get());",
                "output.accept(ModItems.VOL_CORE.get());",
                "output.accept(ModItems.WAYPOINT.get());"
        ), "Creative tab block section should keep the legacy build-block ordering");
    }

    private static void assertOrder(String source, List<String> snippets, String message) {
        int cursor = -1;
        for (String snippet : snippets) {
            int next = source.indexOf(snippet, cursor + 1);
            assertTrue(next >= 0, () -> "Expected snippet to exist: " + snippet);
            assertTrue(next > cursor, () -> message + " around " + snippet);
            cursor = next;
        }
    }
}
