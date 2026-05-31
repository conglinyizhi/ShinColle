package org.trp.shincolle.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.trp.shincolle.init.ModItems;
import org.trp.shincolle.item.LegacyEquipItem;

/**
 * Registers item descriptions for JEI's ingredient panel.
 * Shows ship class info for spawn eggs and category descriptions for equipment.
 */
public final class JeiItemDescription {

    private JeiItemDescription() {
    }

    /**
     * Register all item descriptions.
     */
    public static void register(IRecipeRegistration registration) {
        registerSpawnEggDescriptions(registration);
        registerEquipmentDescriptions(registration);
        registerBlockDescriptions(registration);
    }

    private static void registerSpawnEggDescriptions(IRecipeRegistration registration) {
        // Random ship spawn eggs used in shipyard crafting
        addInfo(registration, new ItemStack(ModItems.SHIPSPAWNEGGS.get()),
                Component.translatable("jei.description.shincolle.shipspawneggs"));
        addInfo(registration, new ItemStack(ModItems.SHIPSPAWNEGGL.get()),
                Component.translatable("jei.description.shincolle.shipspawneggl"));

        // Individual ship spawn eggs
        addInfo(registration, new ItemStack(ModItems.DESTROYER_I_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Destroyer I-Class"));
        addInfo(registration, new ItemStack(ModItems.DESTROYER_RO_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Destroyer Ro-Class"));
        addInfo(registration, new ItemStack(ModItems.DESTROYER_HA_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Destroyer Ha-Class"));
        addInfo(registration, new ItemStack(ModItems.DESTROYER_NI_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Destroyer Ni-Class"));
        addInfo(registration, new ItemStack(ModItems.HEAVY_CRUISER_RI_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Heavy Cruiser Ri-Class"));
        addInfo(registration, new ItemStack(ModItems.HEAVY_CRUISER_NE_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Heavy Cruiser Ne-Class"));
        addInfo(registration, new ItemStack(ModItems.CARRIER_WO_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Carrier Wo-Class"));
        addInfo(registration, new ItemStack(ModItems.BATTLESHIP_RU_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Battleship Ru-Class"));
        addInfo(registration, new ItemStack(ModItems.BATTLESHIP_TA_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Battleship Ta-Class"));
        addInfo(registration, new ItemStack(ModItems.BATTLESHIP_RE_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Battleship Re-Class"));
        addInfo(registration, new ItemStack(ModItems.TRANSPORT_WA_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Transport Wa-Class"));
        addInfo(registration, new ItemStack(ModItems.SUBM_KA_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Submarine Ka-Class"));
        addInfo(registration, new ItemStack(ModItems.SUBM_YO_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Submarine Yo-Class"));
        addInfo(registration, new ItemStack(ModItems.SUBM_SO_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Submarine So-Class"));
        addInfo(registration, new ItemStack(ModItems.DESTROYER_HIME_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Destroyer Hime"));
        addInfo(registration, new ItemStack(ModItems.CA_HIME_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "CA Hime"));
        addInfo(registration, new ItemStack(ModItems.AIRFIELD_HIME_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Airfield Hime"));
        addInfo(registration, new ItemStack(ModItems.BATTLESHIP_HIME_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Battleship Hime"));
        addInfo(registration, new ItemStack(ModItems.CARRIER_HIME_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Carrier Hime"));
        addInfo(registration, new ItemStack(ModItems.HARBOUR_HIME_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Harbour Hime"));
        addInfo(registration, new ItemStack(ModItems.ISOLATED_HIME_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Isolated Island Hime"));
        addInfo(registration, new ItemStack(ModItems.MIDWAY_HIME_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Midway Hime"));
        addInfo(registration, new ItemStack(ModItems.NORTHERN_HIME_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Northern Hime"));
        addInfo(registration, new ItemStack(ModItems.SUBM_HIME_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Submarine Hime"));
        addInfo(registration, new ItemStack(ModItems.SSNH_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "SSNH"));
        addInfo(registration, new ItemStack(ModItems.CARRIER_W_DEMON_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Carrier W Demon"));
        addInfo(registration, new ItemStack(ModItems.DESTROYER_AKATSUKI_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Destroyer Akatsuki"));
        addInfo(registration, new ItemStack(ModItems.DESTROYER_HIBIKI_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Destroyer Hibiki"));
        addInfo(registration, new ItemStack(ModItems.DESTROYER_IKAZUCHI_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Destroyer Ikazuchi"));
        addInfo(registration, new ItemStack(ModItems.DESTROYER_INAZUMA_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Destroyer Inazuma"));
        addInfo(registration, new ItemStack(ModItems.DESTROYER_SHIMAKAZE_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Destroyer Shimakaze"));
        addInfo(registration, new ItemStack(ModItems.CRUISER_TENRYUU_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Light Cruiser Tenryuu"));
        addInfo(registration, new ItemStack(ModItems.CRUISER_TATSUTA_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Light Cruiser Tatsuta"));
        addInfo(registration, new ItemStack(ModItems.CRUISER_TAKAO_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Heavy Cruiser Takao"));
        addInfo(registration, new ItemStack(ModItems.CRUISER_ATAGO_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Heavy Cruiser Atago"));
        addInfo(registration, new ItemStack(ModItems.CARRIER_KAGA_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Aircraft Carrier Kaga"));
        addInfo(registration, new ItemStack(ModItems.CARRIER_AKAGI_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Aircraft Carrier Akagi"));
        addInfo(registration, new ItemStack(ModItems.BB_KONGOU_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Battleship Kongou"));
        addInfo(registration, new ItemStack(ModItems.BB_HIEI_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Battleship Hiei"));
        addInfo(registration, new ItemStack(ModItems.BB_HARUNA_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Battleship Haruna"));
        addInfo(registration, new ItemStack(ModItems.BB_KIRISHIMA_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Battleship Kirishima"));
        addInfo(registration, new ItemStack(ModItems.BATTLESHIP_NAGATO_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Battleship Nagato"));
        addInfo(registration, new ItemStack(ModItems.BATTLESHIP_YAMATO_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Battleship Yamato"));
        addInfo(registration, new ItemStack(ModItems.SUBM_U511_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Submarine U-511"));
        addInfo(registration, new ItemStack(ModItems.SUBM_RO500_SPAWN_EGG.get()),
                Component.translatable("jei.description.shincolle.shipclass", "Submarine Ro-500"));
    }

    private static void registerEquipmentDescriptions(IRecipeRegistration registration) {
        // Register general descriptions for each equipment category
        addInfo(registration, new ItemStack(ModItems.EQUIP_CANNON.get()),
                Component.translatable("jei.description.shincolle.equip.cannon"));
        addInfo(registration, new ItemStack(ModItems.EQUIP_TORPEDO.get()),
                Component.translatable("jei.description.shincolle.equip.torpedo"));
        addInfo(registration, new ItemStack(ModItems.EQUIP_AIRPLANE.get()),
                Component.translatable("jei.description.shincolle.equip.airplane"));
        addInfo(registration, new ItemStack(ModItems.EQUIP_RADAR.get()),
                Component.translatable("jei.description.shincolle.equip.radar"));
        addInfo(registration, new ItemStack(ModItems.EQUIP_TURBINE.get()),
                Component.translatable("jei.description.shincolle.equip.turbine"));
        addInfo(registration, new ItemStack(ModItems.EQUIP_ARMOR.get()),
                Component.translatable("jei.description.shincolle.equip.armor"));
        addInfo(registration, new ItemStack(ModItems.EQUIP_MACHINEGUN.get()),
                Component.translatable("jei.description.shincolle.equip.machinegun"));
        addInfo(registration, new ItemStack(ModItems.EQUIP_CATAPULT.get()),
                Component.translatable("jei.description.shincolle.equip.catapult"));
        addInfo(registration, new ItemStack(ModItems.EQUIP_DRUM.get()),
                Component.translatable("jei.description.shincolle.equip.drum"));
        addInfo(registration, new ItemStack(ModItems.EQUIP_COMPASS.get()),
                Component.translatable("jei.description.shincolle.equip.compass"));
        addInfo(registration, new ItemStack(ModItems.EQUIP_FLARE.get()),
                Component.translatable("jei.description.shincolle.equip.flare"));
        addInfo(registration, new ItemStack(ModItems.EQUIP_SEARCHLIGHT.get()),
                Component.translatable("jei.description.shincolle.equip.searchlight"));
        addInfo(registration, new ItemStack(ModItems.EQUIP_AMMO.get()),
                Component.translatable("jei.description.shincolle.equip.ammo"));
    }

    private static void registerBlockDescriptions(IRecipeRegistration registration) {
        addInfo(registration, new ItemStack(ModItems.LARGE_SHIPYARD.get()),
                Component.translatable("jei.description.shincolle.large_shipyard"));
    }

    private static void addInfo(IRecipeRegistration registration, ItemStack stack, Component... description) {
        if (stack.isEmpty()) return;

        try {
            registration.addIngredientInfo(stack, VanillaTypes.ITEM_STACK, description);
        } catch (Exception ignored) {
            // JEI API variation - skip silently to avoid crashes
        }
    }
}
