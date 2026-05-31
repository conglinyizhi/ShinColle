package org.trp.shincolle.integration.jei;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.trp.shincolle.init.ModItems;
import org.trp.shincolle.item.LegacyEquipItem;
import org.trp.shincolle.item.LegacyEquipStats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generates JEI recipe data from ShipyardRecipes and LegacyEquipStats constants.
 * Since the candidate lists in ShipyardRecipes are private, we replicate the data here
 * to keep the JEI integration self-contained.
 */
public final class JeiRecipeMaker {

    // ---- Ship type-to-egg mapping (mirrors ShipyardRecipes.getShipEggForType) ----

    private static Item getShipEggForType(int type, boolean largeShipyard) {
        return switch (type) {
            case 0 -> ModItems.DESTROYER_I_SPAWN_EGG.get();
            case 1 -> ModItems.DESTROYER_RO_SPAWN_EGG.get();
            case 2 -> ModItems.DESTROYER_HA_SPAWN_EGG.get();
            case 3 -> ModItems.DESTROYER_NI_SPAWN_EGG.get();
            case 9 -> ModItems.HEAVY_CRUISER_RI_SPAWN_EGG.get();
            case 10 -> ModItems.HEAVY_CRUISER_NE_SPAWN_EGG.get();
            case 12 -> ModItems.CARRIER_WO_SPAWN_EGG.get();
            case 13 -> ModItems.BATTLESHIP_RU_SPAWN_EGG.get();
            case 14 -> ModItems.BATTLESHIP_TA_SPAWN_EGG.get();
            case 15 -> ModItems.BATTLESHIP_RE_SPAWN_EGG.get();
            case 16 -> ModItems.TRANSPORT_WA_SPAWN_EGG.get();
            case 17 -> ModItems.SUBM_KA_SPAWN_EGG.get();
            case 18 -> ModItems.SUBM_YO_SPAWN_EGG.get();
            case 19 -> ModItems.SUBM_SO_SPAWN_EGG.get();
            case 20 -> ModItems.CARRIER_HIME_SPAWN_EGG.get();
            case 21 -> ModItems.AIRFIELD_HIME_SPAWN_EGG.get();
            case 26 -> ModItems.BATTLESHIP_HIME_SPAWN_EGG.get();
            case 27 -> ModItems.DESTROYER_HIME_SPAWN_EGG.get();
            case 28 -> ModItems.HARBOUR_HIME_SPAWN_EGG.get();
            case 29 -> ModItems.ISOLATED_HIME_SPAWN_EGG.get();
            case 30 -> ModItems.MIDWAY_HIME_SPAWN_EGG.get();
            case 31 -> ModItems.NORTHERN_HIME_SPAWN_EGG.get();
            case 33 -> ModItems.CARRIER_W_DEMON_SPAWN_EGG.get();
            case 44 -> ModItems.SUBM_HIME_SPAWN_EGG.get();
            case 49 -> ModItems.CA_HIME_SPAWN_EGG.get();
            case 72 -> ModItems.SSNH_SPAWN_EGG.get();
            default -> largeShipyard ? ModItems.DESTROYER_HIME_SPAWN_EGG.get() : ModItems.DESTROYER_I_SPAWN_EGG.get();
        };
    }

    // ---- Equipment type-to-item mapping (mirrors ShipyardRecipes.resolveEquipItemByType) ----

    private static Item resolveEquipItemByType(int itemType) {
        return switch (itemType) {
            case 0, 1, 2, 3 -> ModItems.EQUIP_CANNON.get();
            case 4, 5 -> ModItems.EQUIP_TORPEDO.get();
            case 6, 7, 8, 9, 10, 11, 12, 13 -> ModItems.EQUIP_AIRPLANE.get();
            case 14, 15 -> ModItems.EQUIP_RADAR.get();
            case 16, 17 -> ModItems.EQUIP_TURBINE.get();
            case 18, 19 -> ModItems.EQUIP_ARMOR.get();
            case 20, 21 -> ModItems.EQUIP_MACHINEGUN.get();
            case 22, 23 -> ModItems.EQUIP_CATAPULT.get();
            case 24 -> ModItems.EQUIP_DRUM.get();
            case 25 -> ModItems.EQUIP_COMPASS.get();
            case 26 -> ModItems.EQUIP_FLARE.get();
            case 27 -> ModItems.EQUIP_SEARCHLIGHT.get();
            case 28, 29 -> ModItems.EQUIP_AMMO.get();
            default -> null;
        };
    }

    // ---- Candidate data (mirrors ShipyardRecipes private constants) ----

    private static final int[][] SMALL_SHIP_CANDIDATES = {
            {0, 80, 0},   // Destroyer I
            {1, 90, 0},   // Destroyer Ro
            {2, 100, 0},  // Destroyer Ha
            {3, 110, 0},  // Destroyer Ni
            {16, 120, 1}, // Transport Wa
            {17, 140, 2}, // Subm Ka
            {18, 160, 2}, // Subm Yo
            {19, 180, 2}, // Subm So
            {9, 200, 2},  // Heavy Cruiser Ri
            {10, 256, 2}  // Heavy Cruiser Ne
    };

    private static final int[][] LARGE_SHIP_CANDIDATES = {
            {27, 500, 0},   // Destroyer Hime
            {12, 650, 3},   // Carrier Wo
            {14, 800, 2},   // Battleship Ta
            {13, 800, 2},   // Battleship Ru
            {49, 2000, 2},  // CA Hime
            {31, 2600, 1},  // Northern Hime
            {72, 2600, 2},  // SSNH
            {29, 2700, 1},  // Isolated Hime
            {28, 2800, 1},  // Harbour Hime
            {21, 3000, 1},  // Airfield Hime
            {20, 3000, 3},  // Carrier Hime
            {44, 3500, 2},  // Subm Hime
            {15, 3800, 2},  // Battleship Re
            {26, 4600, 2},  // Battleship Hime
            {30, 4800, 1},  // Midway Hime
            {33, 5000, 3}   // Carrier W Demon
    };

    private static final int[][] SMALL_EQUIP_CANDIDATES = {
            {18, 80, 1},   // Armor
            {26, 80, 2},   // Flare
            {27, 80, 0},   // Searchlight
            {25, 90, 0},   // Compass
            {20, 100, 2},  // Machinegun
            {24, 120, 1},  // Drum
            {28, 120, 2},  // Ammo
            {0, 128, 2},   // Cannon (small)
            {4, 160, 2},   // Torpedo
            {14, 200, 0},  // Radar
            {12, 256, 3},  // Airplane
            {1, 320, 2}    // Cannon (medium)
    };

    private static final int[][] LARGE_EQUIP_CANDIDATES = {
            {19, 500, 1},   // Armor (large)
            {21, 800, 2},   // Machinegun (large)
            {29, 1000, 2},  // Ammo (large)
            {13, 1000, 3},  // Airplane (large)
            {5, 1200, 2},   // Torpedo (large)
            {16, 1400, 0},  // Turbine
            {2, 1600, 2},   // Cannon (large)
            {15, 2000, 0},  // Radar (large)
            {6, 2400, 3},   // Airplane (dive bomber)
            {8, 2400, 3},   // Airplane (torpedo bomber)
            {10, 2400, 3},  // Airplane (recon)
            {22, 2800, 3},  // Catapult
            {17, 3200, 0},  // Turbine (large)
            {7, 3800, 3},   // Airplane (jet)
            {9, 3800, 3},   // Airplane (fighter)
            {11, 3800, 3},  // Airplane (seaplane)
            {3, 4400, 2},   // Cannon (largest)
            {23, 5000, 3}   // Catapult (large)
    };

    private static final ItemStack[] MATERIAL_ITEMS = {
            new ItemStack(ModItems.GRUDGE.get()),
            new ItemStack(ModItems.ABYSS_METAL.get()),
            new ItemStack(ModItems.AMMO_LIGHT.get()),
            new ItemStack(ModItems.ABYSS_POLYMETAL.get())
    };

    private static final ItemStack FUEL = new ItemStack(Items.LAVA_BUCKET);

    private JeiRecipeMaker() {
    }

    /**
     * Distribute the mean material value across 4 material types.
     * The preferred material gets 40%, others get 20% each (rounded up).
     */
    private static List<ItemStack> createMaterialInputs(int mean, int preferredMat) {
        List<ItemStack> inputs = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            int amount;
            if (i == preferredMat) {
                amount = Math.max(1, (int) Math.ceil(mean * 0.4f));
            } else {
                amount = Math.max(1, (int) Math.ceil(mean * 0.2f));
            }
            ItemStack stack = MATERIAL_ITEMS[i].copy();
            stack.setCount(Math.min(amount, 999));
            inputs.add(stack);
        }
        return inputs;
    }

    /**
     * Find representative equipment stacks for a given equipment type.
     * Searches LegacyEquipStats for matching entries.
     */
    private static List<ItemStack> findEquipOutputs(int equipType) {
        // Map the equipType to an item first (the base item class)
        Item baseItem = resolveEquipItemByType(equipType);
        if (baseItem == null) {
            return List.of();
        }

        // Find matching equip IDs from LegacyEquipStats
        List<ItemStack> results = new ArrayList<>();
        for (Map.Entry<Integer, int[]> entry : LegacyEquipStats.getAllMiscAttrs().entrySet()) {
            int[] misc = entry.getValue();
            if (misc.length >= 2 && misc[1] == equipType) {
                int equipId = entry.getKey();
                int itemType = equipId % 100;
                int variant = equipId / 100;
                Item equipItem = resolveEquipItemByType(itemType);
                if (equipItem instanceof LegacyEquipItem legacyEquipItem) {
                    results.add(legacyEquipItem.createVariantStack(variant));
                } else if (equipItem != null) {
                    results.add(new ItemStack(equipItem));
                }
            }
        }

        // Fallback: show variant 0 of the base item
        if (results.isEmpty() && baseItem instanceof LegacyEquipItem legacyEquipItem) {
            results.add(legacyEquipItem.createVariantStack(0));
        } else if (results.isEmpty()) {
            results.add(new ItemStack(baseItem));
        }

        return results;
    }

    // ---- Public API ----

    public static List<ShipyardRecipeWrapper> getSmallShipyardRecipes() {
        return buildShipRecipes(SMALL_SHIP_CANDIDATES, false);
    }

    public static List<ShipyardRecipeWrapper> getLargeShipyardRecipes() {
        return buildShipRecipes(LARGE_SHIP_CANDIDATES, true);
    }

    public static List<EquipmentRecipeWrapper> getSmallEquipRecipes() {
        return buildEquipRecipes(SMALL_EQUIP_CANDIDATES, false);
    }

    public static List<EquipmentRecipeWrapper> getLargeEquipRecipes() {
        return buildEquipRecipes(LARGE_EQUIP_CANDIDATES, true);
    }

    private static List<ShipyardRecipeWrapper> buildShipRecipes(int[][] candidates, boolean large) {
        List<ShipyardRecipeWrapper> recipes = new ArrayList<>();
        for (int[] cand : candidates) {
            int type = cand[0];
            int mean = cand[1];
            int preferredMat = cand[2];

            List<ItemStack> inputs = createMaterialInputs(mean, preferredMat);
            Item eggItem = getShipEggForType(type, large);
            List<ItemStack> outputs = List.of(new ItemStack(eggItem));

            recipes.add(new ShipyardRecipeWrapper(inputs, FUEL.copy(), outputs));
        }
        return recipes;
    }

    private static List<EquipmentRecipeWrapper> buildEquipRecipes(int[][] candidates, boolean large) {
        List<EquipmentRecipeWrapper> recipes = new ArrayList<>();
        for (int[] cand : candidates) {
            int type = cand[0];
            int mean = cand[1];
            int preferredMat = cand[2];

            List<ItemStack> inputs = createMaterialInputs(mean, preferredMat);
            List<ItemStack> outputs = findEquipOutputs(type);

            if (!outputs.isEmpty()) {
                recipes.add(new EquipmentRecipeWrapper(inputs, FUEL.copy(), outputs));
            }
        }
        return recipes;
    }
}
