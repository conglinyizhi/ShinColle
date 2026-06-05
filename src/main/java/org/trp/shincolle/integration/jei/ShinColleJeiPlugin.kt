package org.trp.shincolle.integration.jei

import mezz.jei.api.IModPlugin

@JeiPlugin
class ShinColleJeiPlugin : IModPlugin {
    val pluginUid: ResourceLocation
        get() = PLUGIN_UID

    public override fun registerCategories(registration: IRecipeCategoryRegistration) {
        val guiHelper: IGuiHelper = registration.getJeiHelpers().getGuiHelper()

        registration.addRecipeCategories(
            ShipyardRecipeCategory(guiHelper, false),
            ShipyardRecipeCategory(guiHelper, true),
            EquipmentRecipeCategory(guiHelper, false),
            EquipmentRecipeCategory(guiHelper, true),
            ShipAcquisitionCategory(guiHelper)
        )
    }

    public override fun registerRecipes(registration: IRecipeRegistration) {
        // Shipyard recipes
        registration.addRecipes(
            ShipyardRecipeCategory.Companion.SMALL_TYPE,
            JeiRecipeMaker.getSmallShipyardRecipes()
        )
        registration.addRecipes(
            ShipyardRecipeCategory.Companion.LARGE_TYPE,
            JeiRecipeMaker.getLargeShipyardRecipes()
        )

        // Equipment development recipes
        registration.addRecipes(
            EquipmentRecipeCategory.Companion.SMALL_TYPE,
            JeiRecipeMaker.getSmallEquipRecipes()
        )
        registration.addRecipes(
            EquipmentRecipeCategory.Companion.LARGE_TYPE,
            JeiRecipeMaker.getLargeEquipRecipes()
        )

        // Ship acquisition reference
        registration.addRecipes(
            ShipAcquisitionCategory.Companion.TYPE,
            JeiRecipeMaker.getShipAcquisitions()
        )

        // Item descriptions
        JeiItemDescription.register(registration)
    }

    public override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.addRecipeCatalyst(
            ItemStack(ModItems.SMALL_SHIPYARD.get()),
            ShipyardRecipeCategory.Companion.SMALL_TYPE,
            EquipmentRecipeCategory.Companion.SMALL_TYPE
        )
        registration.addRecipeCatalyst(
            ItemStack(ModItems.LARGE_SHIPYARD.get()),
            ShipyardRecipeCategory.Companion.LARGE_TYPE,
            EquipmentRecipeCategory.Companion.LARGE_TYPE
        )
    }

    public override fun registerItemSubtypes(registration: ISubtypeRegistration?) {
        // No item subtypes needed for JEI integration
    }

    companion object {
        val PLUGIN_UID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "jei_plugin")
    }
}
