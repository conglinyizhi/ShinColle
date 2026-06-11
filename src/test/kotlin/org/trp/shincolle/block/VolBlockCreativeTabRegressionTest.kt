package org.trp.shincolle.block

import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class VolBlockCreativeTabRegressionTest {
    private val CREATIVE_TAB_CONTENTS: Path =
            Path.of("src/main/java/org/trp/shincolle/init/ShinColleCreativeTabContents.kt")
    private val MOD_ITEMS: Path =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.kt")

    @Test
    fun multiblockVolcanoShellShouldNotAppearInCreativeTab() {
        val creativeTabContents = Files.readString(CREATIVE_TAB_CONTENTS)
        assertFalse(creativeTabContents.contains("output.accept(ModItems.VOL_BLOCK.get())")) {
            "Abyssal Volcano Block is a multiblock shell and should not be exposed in the creative tab"
        }
        assertTrue(creativeTabContents.contains("output.accept(ModItems.VOL_CORE.get())")) {
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
