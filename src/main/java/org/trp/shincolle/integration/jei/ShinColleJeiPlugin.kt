package org.trp.shincolle.integration.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IRecipeCatalystRegistration
import mezz.jei.api.registration.IRecipeCategoryRegistration
import mezz.jei.api.registration.IRecipeRegistration
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.Shincolle
import org.trp.shincolle.init.ModItems

@JeiPlugin
class ShinColleJeiPlugin : IModPlugin {

    override fun getPluginUid(): ResourceLocation = PLUGIN_UID

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        val guiHelper = registration.jeiHelpers.guiHelper

        registration.addRecipeCategories(
            ShipyardRecipeCategory(guiHelper, false),
            ShipyardRecipeCategory(guiHelper, true),
            EquipmentRecipeCategory(guiHelper, false),
            EquipmentRecipeCategory(guiHelper, true),
            ShipAcquisitionCategory(guiHelper)
        )
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        // Shipyard recipes
        registration.addRecipes(ShipyardRecipeCategory.SMALL_TYPE, JeiRecipeMaker.smallShipyardRecipes)
        registration.addRecipes(ShipyardRecipeCategory.LARGE_TYPE, JeiRecipeMaker.largeShipyardRecipes)

        // Equipment development recipes
        registration.addRecipes(EquipmentRecipeCategory.SMALL_TYPE, JeiRecipeMaker.smallEquipRecipes)
        registration.addRecipes(EquipmentRecipeCategory.LARGE_TYPE, JeiRecipeMaker.largeEquipRecipes)

        // Ship acquisition reference
        registration.addRecipes(ShipAcquisitionCategory.TYPE, JeiRecipeMaker.shipAcquisitions)

        // Item descriptions
        JeiItemDescription.register(registration)
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.addRecipeCatalyst(
            ItemStack(ModItems.SMALL_SHIPYARD.get()),
            ShipyardRecipeCategory.SMALL_TYPE,
            EquipmentRecipeCategory.SMALL_TYPE
        )
        registration.addRecipeCatalyst(
            ItemStack(ModItems.LARGE_SHIPYARD.get()),
            ShipyardRecipeCategory.LARGE_TYPE,
            EquipmentRecipeCategory.LARGE_TYPE
        )
    }

    companion object {
        val PLUGIN_UID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "jei_plugin")
    }
}
