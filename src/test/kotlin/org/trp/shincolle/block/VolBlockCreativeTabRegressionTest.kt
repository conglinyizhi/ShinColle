package org.trp.shincolle.block

import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class VolBlockCreativeTabRegressionTest {
    private val MOD_TABS: Path =
            Path.of("src/main/java/org/trp/shincolle/init/ModTabs.kt")
    private val MOD_ITEMS: Path =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.kt")

    @Test
    fun multiblockVolcanoShellShouldNotAppearInCreativeTab() {
        val modTabs = Files.readString(MOD_TABS)
        assertFalse(modTabs.contains("output.accept(ModItems.VOL_BLOCK.get());")) {
            "Abyssal Volcano Block is a multiblock shell and should not be exposed in the creative tab"
        }
        assertTrue(modTabs.contains("output.accept(ModItems.VOL_CORE.get());")) {
            "Abyssal Volcano Core should remain available because it is the real crafted block"
        }
    }

    @Test
    fun volcanoShellItemShouldRemainRegisteredForRecipesAndStructureLogic() {
        val modItems = Files.readString(MOD_ITEMS)
        assertTrue(modItems.contains("VOL_BLOCK = ITEMS.register(\"blockvolblock\"")) {
            "Abyssal Volcano Block item should stay registered for recipes and legacy structure compatibility"
        }
    }
}
