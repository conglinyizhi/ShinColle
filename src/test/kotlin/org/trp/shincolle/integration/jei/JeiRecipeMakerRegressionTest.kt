package org.trp.shincolle.integration.jei

import net.minecraft.world.item.Items
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JeiRecipeMakerRegressionTest {

    @Test
    fun shipyardAndEquipmentRecipesShouldKeepLegacyFourMaterialAndFuelLayout() {
        val shipyardRecipes = JeiRecipeMaker.smallShipyardRecipes + JeiRecipeMaker.largeShipyardRecipes
        val equipmentRecipes = JeiRecipeMaker.smallEquipRecipes + JeiRecipeMaker.largeEquipRecipes

        assertFalse(shipyardRecipes.isEmpty(), "JEI shipyard recipe list should not be empty")
        assertFalse(equipmentRecipes.isEmpty(), "JEI equipment recipe list should not be empty")

        shipyardRecipes.forEach { recipe ->
            assertEquals(4, recipe.inputs.size, "Shipyard JEI recipes should keep four material inputs")
            assertEquals(Items.LAVA_BUCKET, recipe.fuel.item, "Shipyard JEI recipes should keep lava bucket fuel")
            assertEquals(1, recipe.outputs.size, "Shipyard JEI recipes should keep one representative spawn egg output")
            recipe.inputs.forEach { input ->
                assertTrue(input.count > 0, "Shipyard JEI material inputs should stay positive")
            }
        }

        equipmentRecipes.forEach { recipe ->
            assertEquals(4, recipe.inputs.size, "Equipment JEI recipes should keep four material inputs")
            assertEquals(Items.LAVA_BUCKET, recipe.fuel.item, "Equipment JEI recipes should keep lava bucket fuel")
            assertFalse(recipe.outputs.isEmpty(), "Equipment JEI recipes should keep at least one representative output")
            recipe.inputs.forEach { input ->
                assertTrue(input.count > 0, "Equipment JEI material inputs should stay positive")
            }
        }
    }

    @Test
    fun shipAcquisitionsShouldKeepSmallLargeAndWildSourceBuckets() {
        val acquisitions = JeiRecipeMaker.shipAcquisitions
        val sourceKeys = acquisitions.flatMap { acquisition -> acquisition.sourceLangKeys }.toSet()

        assertTrue(sourceKeys.contains("jei.source.shincolle.small_shipyard")) {
            "Ship acquisitions should keep the small shipyard source bucket"
        }
        assertTrue(sourceKeys.contains("jei.source.shincolle.large_shipyard")) {
            "Ship acquisitions should keep the large shipyard source bucket"
        }
        assertTrue(sourceKeys.contains("jei.source.shincolle.wild_kanmusu")) {
            "Ship acquisitions should keep the wild Kanmusu source bucket"
        }
    }
}
