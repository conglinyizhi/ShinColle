package org.trp.shincolle.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.init.ModItems;

@JeiPlugin
public class ShinColleJeiPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_UID =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(
                new ShipyardRecipeCategory(guiHelper, false),  // Small Shipyard
                new ShipyardRecipeCategory(guiHelper, true),   // Large Shipyard
                new EquipmentRecipeCategory(guiHelper, false), // Small Equipment Development
                new EquipmentRecipeCategory(guiHelper, true)   // Large Equipment Development
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Shipyard recipes
        registration.addRecipes(ShipyardRecipeCategory.SMALL_TYPE,
                JeiRecipeMaker.getSmallShipyardRecipes());
        registration.addRecipes(ShipyardRecipeCategory.LARGE_TYPE,
                JeiRecipeMaker.getLargeShipyardRecipes());

        // Equipment development recipes
        registration.addRecipes(EquipmentRecipeCategory.SMALL_TYPE,
                JeiRecipeMaker.getSmallEquipRecipes());
        registration.addRecipes(EquipmentRecipeCategory.LARGE_TYPE,
                JeiRecipeMaker.getLargeEquipRecipes());

        // Item descriptions
        JeiItemDescription.register(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                new ItemStack(ModItems.SMALL_SHIPYARD.get()),
                ShipyardRecipeCategory.SMALL_TYPE,
                EquipmentRecipeCategory.SMALL_TYPE
        );
        registration.addRecipeCatalyst(
                new ItemStack(ModItems.LARGE_SHIPYARD.get()),
                ShipyardRecipeCategory.LARGE_TYPE,
                EquipmentRecipeCategory.LARGE_TYPE
        );
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        // No item subtypes needed for JEI integration
    }
}
