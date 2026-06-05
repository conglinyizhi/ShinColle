package org.trp.shincolle.integration.jei

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.item.LegacyEquipItem
import org.trp.shincolle.item.LegacyEquipStats.allMiscAttrs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * Generates JEI recipe data from ShipyardRecipes and LegacyEquipStats constants.
 * Since the candidate lists in ShipyardRecipes are private, we replicate the data here
 * to keep the JEI integration self-contained.
 */
object JeiRecipeMaker {

    // ---- Ship type-to-egg mapping (mirrors ShipyardRecipes.getShipEggForType) ----
    private fun getShipEggForType(type: Int, largeShipyard: Boolean): Item {
        return when (type) {
            0 -> ModItems.DESTROYER_I_SPAWN_EGG.get()
            1 -> ModItems.DESTROYER_RO_SPAWN_EGG.get()
            2 -> ModItems.DESTROYER_HA_SPAWN_EGG.get()
            3 -> ModItems.DESTROYER_NI_SPAWN_EGG.get()
            9 -> ModItems.HEAVY_CRUISER_RI_SPAWN_EGG.get()
            10 -> ModItems.HEAVY_CRUISER_NE_SPAWN_EGG.get()
            12 -> ModItems.CARRIER_WO_SPAWN_EGG.get()
            13 -> ModItems.BATTLESHIP_RU_SPAWN_EGG.get()
            14 -> ModItems.BATTLESHIP_TA_SPAWN_EGG.get()
            15 -> ModItems.BATTLESHIP_RE_SPAWN_EGG.get()
            16 -> ModItems.TRANSPORT_WA_SPAWN_EGG.get()
            17 -> ModItems.SUBM_KA_SPAWN_EGG.get()
            18 -> ModItems.SUBM_YO_SPAWN_EGG.get()
            19 -> ModItems.SUBM_SO_SPAWN_EGG.get()
            20 -> ModItems.CARRIER_HIME_SPAWN_EGG.get()
            21 -> ModItems.AIRFIELD_HIME_SPAWN_EGG.get()
            26 -> ModItems.BATTLESHIP_HIME_SPAWN_EGG.get()
            27 -> ModItems.DESTROYER_HIME_SPAWN_EGG.get()
            28 -> ModItems.HARBOUR_HIME_SPAWN_EGG.get()
            29 -> ModItems.ISOLATED_HIME_SPAWN_EGG.get()
            30 -> ModItems.MIDWAY_HIME_SPAWN_EGG.get()
            31 -> ModItems.NORTHERN_HIME_SPAWN_EGG.get()
            33 -> ModItems.CARRIER_W_DEMON_SPAWN_EGG.get()
            44 -> ModItems.SUBM_HIME_SPAWN_EGG.get()
            49 -> ModItems.CA_HIME_SPAWN_EGG.get()
            72 -> ModItems.SSNH_SPAWN_EGG.get()
            else -> if (largeShipyard) ModItems.DESTROYER_HIME_SPAWN_EGG.get() else ModItems.DESTROYER_I_SPAWN_EGG.get()
        } ?: throw IllegalStateException("Missing spawn egg item for type=$type")
    }

    // ---- Equipment type-to-item mapping (mirrors ShipyardRecipes.resolveEquipItemByType) ----
    private fun resolveEquipItemByType(itemType: Int): Item? {
        return when (itemType) {
            0, 1, 2, 3 -> ModItems.EQUIP_CANNON.get()
            4, 5 -> ModItems.EQUIP_TORPEDO.get()
            6, 7, 8, 9, 10, 11, 12, 13 -> ModItems.EQUIP_AIRPLANE.get()
            14, 15 -> ModItems.EQUIP_RADAR.get()
            16, 17 -> ModItems.EQUIP_TURBINE.get()
            18, 19 -> ModItems.EQUIP_ARMOR.get()
            20, 21 -> ModItems.EQUIP_MACHINEGUN.get()
            22, 23 -> ModItems.EQUIP_CATAPULT.get()
            24 -> ModItems.EQUIP_DRUM.get()
            25 -> ModItems.EQUIP_COMPASS.get()
            26 -> ModItems.EQUIP_FLARE.get()
            27 -> ModItems.EQUIP_SEARCHLIGHT.get()
            28, 29 -> ModItems.EQUIP_AMMO.get()
            else -> null
        }
    }

    // ---- Candidate data (mirrors ShipyardRecipes private constants) ----
    private val SMALL_SHIP_CANDIDATES = arrayOf(
        intArrayOf(0, 80, 0),    // Destroyer I
        intArrayOf(1, 90, 0),    // Destroyer Ro
        intArrayOf(2, 100, 0),   // Destroyer Ha
        intArrayOf(3, 110, 0),   // Destroyer Ni
        intArrayOf(16, 120, 1),  // Transport Wa
        intArrayOf(17, 140, 2),  // Subm Ka
        intArrayOf(18, 160, 2),  // Subm Yo
        intArrayOf(19, 180, 2),  // Subm So
        intArrayOf(9, 200, 2),   // Heavy Cruiser Ri
        intArrayOf(10, 256, 2)   // Heavy Cruiser Ne
    )

    private val LARGE_SHIP_CANDIDATES = arrayOf(
        intArrayOf(27, 500, 0),  // Destroyer Hime
        intArrayOf(12, 650, 3),  // Carrier Wo
        intArrayOf(14, 800, 2),  // Battleship Ta
        intArrayOf(13, 800, 2),  // Battleship Ru
        intArrayOf(49, 2000, 2), // CA Hime
        intArrayOf(31, 2600, 1), // Northern Hime
        intArrayOf(72, 2600, 2), // SSNH
        intArrayOf(29, 2700, 1), // Isolated Hime
        intArrayOf(28, 2800, 1), // Harbour Hime
        intArrayOf(21, 3000, 1), // Airfield Hime
        intArrayOf(20, 3000, 3), // Carrier Hime
        intArrayOf(44, 3500, 2), // Subm Hime
        intArrayOf(15, 3800, 2), // Battleship Re
        intArrayOf(26, 4600, 2), // Battleship Hime
        intArrayOf(30, 4800, 1), // Midway Hime
        intArrayOf(33, 5000, 3)  // Carrier W Demon
    )

    private val SMALL_EQUIP_CANDIDATES = arrayOf(
        intArrayOf(18, 80, 1),   // Armor
        intArrayOf(26, 80, 2),   // Flare
        intArrayOf(27, 80, 0),   // Searchlight
        intArrayOf(25, 90, 0),   // Compass
        intArrayOf(20, 100, 2),  // Machinegun
        intArrayOf(24, 120, 1),  // Drum
        intArrayOf(28, 120, 2),  // Ammo
        intArrayOf(0, 128, 2),   // Cannon (small)
        intArrayOf(4, 160, 2),   // Torpedo
        intArrayOf(14, 200, 0),  // Radar
        intArrayOf(12, 256, 3),  // Airplane
        intArrayOf(1, 320, 2)    // Cannon (medium)
    )

    private val LARGE_EQUIP_CANDIDATES = arrayOf(
        intArrayOf(19, 500, 1),  // Armor (large)
        intArrayOf(21, 800, 2),  // Machinegun (large)
        intArrayOf(29, 1000, 2), // Ammo (large)
        intArrayOf(13, 1000, 3), // Airplane (large)
        intArrayOf(5, 1200, 2),  // Torpedo (large)
        intArrayOf(16, 1400, 0), // Turbine
        intArrayOf(2, 1600, 2),  // Cannon (large)
        intArrayOf(15, 2000, 0), // Radar (large)
        intArrayOf(6, 2400, 3),  // Airplane (dive bomber)
        intArrayOf(8, 2400, 3),  // Airplane (torpedo bomber)
        intArrayOf(10, 2400, 3), // Airplane (recon)
        intArrayOf(22, 2800, 3), // Catapult
        intArrayOf(17, 3200, 0), // Turbine (large)
        intArrayOf(7, 3800, 3),  // Airplane (jet)
        intArrayOf(9, 3800, 3),  // Airplane (fighter)
        intArrayOf(11, 3800, 3), // Airplane (seaplane)
        intArrayOf(3, 4400, 2),  // Cannon (largest)
        intArrayOf(23, 5000, 3)  // Catapult (large)
    )

    private val MATERIAL_ITEMS = arrayOf(
        ItemStack(ModItems.GRUDGE.get()),
        ItemStack(ModItems.ABYSS_METAL.get()),
        ItemStack(ModItems.AMMO_LIGHT.get()),
        ItemStack(ModItems.ABYSS_POLYMETAL.get())
    )

    private val FUEL = ItemStack(Items.LAVA_BUCKET)

    /**
     * Distribute the mean material value across 4 material types.
     * The preferred material gets 40%, others get 20% each (rounded up).
     */
    private fun createMaterialInputs(mean: Int, preferredMat: Int): List<ItemStack> {
        val inputs = mutableListOf<ItemStack>()
        for (i in 0..3) {
            val amount = if (i == preferredMat) {
                max(1, ceil((mean * 0.4f).toDouble()).toInt())
            } else {
                max(1, ceil((mean * 0.2f).toDouble()).toInt())
            }
            val stack = MATERIAL_ITEMS[i].copy()
            stack.count = min(amount, 999)
            inputs.add(stack)
        }
        return inputs
    }

    /**
     * Find representative equipment stacks for a given equipment type.
     * Searches LegacyEquipStats for matching entries.
     */
    private fun findEquipOutputs(equipType: Int): List<ItemStack> {
        val baseItem = resolveEquipItemByType(equipType) ?: return emptyList()

        val results = mutableListOf<ItemStack>()
        for (entry in allMiscAttrs.entries) {
            val misc = entry.value ?: continue
            if (misc.size >= 2 && misc[1] == equipType) {
                val equipId = entry.key ?: continue
                val itemType = equipId % 100
                val variant = equipId / 100
                val equipItem = resolveEquipItemByType(itemType)
                if (equipItem is LegacyEquipItem) {
                    results.add(equipItem.createVariantStack(variant))
                } else if (equipItem != null) {
                    results.add(ItemStack(equipItem))
                }
            }
        }

        // Fallback: show variant 0 of the base item
        if (results.isEmpty() && baseItem is LegacyEquipItem) {
            results.add(baseItem.createVariantStack(0))
        } else if (results.isEmpty()) {
            results.add(ItemStack(baseItem))
        }

        return results
    }

    // ---- Public API ----
    val smallShipyardRecipes: List<ShipyardRecipeWrapper>
        get() = buildShipRecipes(SMALL_SHIP_CANDIDATES, false)

    val largeShipyardRecipes: List<ShipyardRecipeWrapper>
        get() = buildShipRecipes(LARGE_SHIP_CANDIDATES, true)

    val smallEquipRecipes: List<EquipmentRecipeWrapper>
        get() = buildEquipRecipes(SMALL_EQUIP_CANDIDATES, false)

    val largeEquipRecipes: List<EquipmentRecipeWrapper>
        get() = buildEquipRecipes(LARGE_EQUIP_CANDIDATES, true)

    private fun buildShipRecipes(candidates: Array<IntArray>, large: Boolean): List<ShipyardRecipeWrapper> {
        val recipes = mutableListOf<ShipyardRecipeWrapper>()
        for (cand in candidates) {
            val type = cand[0]
            val mean = cand[1]
            val preferredMat = cand[2]

            val inputs = createMaterialInputs(mean, preferredMat)
            val eggItem = getShipEggForType(type, large)
            val outputs = listOf(ItemStack(eggItem))

            recipes.add(ShipyardRecipeWrapper(inputs, FUEL.copy(), outputs))
        }
        return recipes
    }

    private fun buildEquipRecipes(candidates: Array<IntArray>, large: Boolean): List<EquipmentRecipeWrapper> {
        val recipes = mutableListOf<EquipmentRecipeWrapper>()
        for (cand in candidates) {
            val type = cand[0]
            val mean = cand[1]
            val preferredMat = cand[2]

            val inputs = createMaterialInputs(mean, preferredMat)
            val outputs = findEquipOutputs(type)

            if (outputs.isNotEmpty()) {
                recipes.add(EquipmentRecipeWrapper(inputs, FUEL.copy(), outputs))
            }
        }
        return recipes
    }

    // ---- Ship Acquisition Data ----
    /**
     * Returns ShipAcquisitionWrapper entries showing how each ship can be obtained.
     */
    val shipAcquisitions: List<ShipAcquisitionWrapper>
        get() {
            val result = mutableListOf<ShipAcquisitionWrapper>()
            val smallYard = ItemStack(ModItems.SMALL_SHIPYARD.get())
            val largeYard = ItemStack(ModItems.LARGE_SHIPYARD.get())
            val battleIcon = ItemStack(Items.DIAMOND_SWORD)

            for (cand in SMALL_SHIP_CANDIDATES) {
                result.add(
                    ShipAcquisitionWrapper(
                        ItemStack(getShipEggForType(cand[0], false)),
                        listOf(smallYard),
                        listOf("jei.source.shincolle.small_shipyard")
                    )
                )
            }
            for (cand in LARGE_SHIP_CANDIDATES) {
                result.add(
                    ShipAcquisitionWrapper(
                        ItemStack(getShipEggForType(cand[0], true)),
                        listOf(largeYard),
                        listOf("jei.source.shincolle.large_shipyard")
                    )
                )
            }
            // Enemy kanmusu
            addAcq(result, ModItems.DESTROYER_SHIMAKAZE_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.DESTROYER_AKATSUKI_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.DESTROYER_HIBIKI_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.DESTROYER_IKAZUCHI_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.DESTROYER_INAZUMA_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.CRUISER_TENRYUU_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.CRUISER_TATSUTA_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.CRUISER_TAKAO_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.CRUISER_ATAGO_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.CARRIER_KAGA_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.CARRIER_AKAGI_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.BB_KONGOU_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.BB_HIEI_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.BB_HARUNA_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.BB_KIRISHIMA_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.BATTLESHIP_NAGATO_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.BATTLESHIP_YAMATO_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.SUBM_U511_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            addAcq(result, ModItems.SUBM_RO500_SPAWN_EGG.get(), battleIcon, "jei.source.shincolle.wild_kanmusu")
            return result
        }

    private fun addAcq(list: MutableList<ShipAcquisitionWrapper>, egg: Item?, icon: ItemStack, langKey: String) {
        if (egg == null) return
        list.add(ShipAcquisitionWrapper(ItemStack(egg), listOf(icon), listOf(langKey)))
    }
}
