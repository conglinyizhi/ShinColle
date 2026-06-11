package org.trp.shincolle.init

import net.minecraft.world.item.CreativeModeTab

object ShinColleCreativeTabContents {
    fun populate(output: CreativeModeTab.Output) {
        addSpawnEggs(output)
        addMaterials(output)
        addEquipment(output)
        addMiscTools(output)
        addBlocks(output)
        addDebug(output)
    }

    private fun addSpawnEggs(output: CreativeModeTab.Output) {
        output.accept(ModItems.SHIPSPAWNEGGS.get())
        output.accept(ModItems.SHIPSPAWNEGGL.get())
        output.accept(ModItems.DESTROYER_I_SPAWN_EGG.get())
        output.accept(ModItems.DESTROYER_RO_SPAWN_EGG.get())
        output.accept(ModItems.DESTROYER_HA_SPAWN_EGG.get())
        output.accept(ModItems.DESTROYER_NI_SPAWN_EGG.get())
        output.accept(ModItems.HEAVY_CRUISER_RI_SPAWN_EGG.get())
        output.accept(ModItems.HEAVY_CRUISER_NE_SPAWN_EGG.get())
        output.accept(ModItems.CARRIER_WO_SPAWN_EGG.get())
        output.accept(ModItems.BATTLESHIP_RU_SPAWN_EGG.get())
        output.accept(ModItems.BATTLESHIP_TA_SPAWN_EGG.get())
        output.accept(ModItems.BATTLESHIP_RE_SPAWN_EGG.get())
        output.accept(ModItems.TRANSPORT_WA_SPAWN_EGG.get())
        output.accept(ModItems.SUBM_KA_SPAWN_EGG.get())
        output.accept(ModItems.SUBM_YO_SPAWN_EGG.get())
        output.accept(ModItems.SUBM_SO_SPAWN_EGG.get())
        output.accept(ModItems.DESTROYER_HIME_SPAWN_EGG.get())
        output.accept(ModItems.CA_HIME_SPAWN_EGG.get())
        output.accept(ModItems.CARRIER_HIME_SPAWN_EGG.get())
        output.accept(ModItems.BATTLESHIP_HIME_SPAWN_EGG.get())
        output.accept(ModItems.AIRFIELD_HIME_SPAWN_EGG.get())
        output.accept(ModItems.HARBOUR_HIME_SPAWN_EGG.get())
        output.accept(ModItems.ISOLATED_HIME_SPAWN_EGG.get())
        output.accept(ModItems.MIDWAY_HIME_SPAWN_EGG.get())
        output.accept(ModItems.NORTHERN_HIME_SPAWN_EGG.get())
        output.accept(ModItems.SUBM_HIME_SPAWN_EGG.get())
        output.accept(ModItems.SSNH_SPAWN_EGG.get())
        output.accept(ModItems.CARRIER_W_DEMON_SPAWN_EGG.get())
        output.accept(ModItems.DESTROYER_AKATSUKI_SPAWN_EGG.get())
        output.accept(ModItems.DESTROYER_HIBIKI_SPAWN_EGG.get())
        output.accept(ModItems.DESTROYER_IKAZUCHI_SPAWN_EGG.get())
        output.accept(ModItems.DESTROYER_INAZUMA_SPAWN_EGG.get())
        output.accept(ModItems.DESTROYER_SHIMAKAZE_SPAWN_EGG.get())
        output.accept(ModItems.CRUISER_TENRYUU_SPAWN_EGG.get())
        output.accept(ModItems.CRUISER_TATSUTA_SPAWN_EGG.get())
        output.accept(ModItems.CRUISER_ATAGO_SPAWN_EGG.get())
        output.accept(ModItems.CRUISER_TAKAO_SPAWN_EGG.get())
        output.accept(ModItems.CARRIER_KAGA_SPAWN_EGG.get())
        output.accept(ModItems.CARRIER_AKAGI_SPAWN_EGG.get())
        output.accept(ModItems.BB_KONGOU_SPAWN_EGG.get())
        output.accept(ModItems.BB_HIEI_SPAWN_EGG.get())
        output.accept(ModItems.BB_HARUNA_SPAWN_EGG.get())
        output.accept(ModItems.BB_KIRISHIMA_SPAWN_EGG.get())
        output.accept(ModItems.BATTLESHIP_NAGATO_SPAWN_EGG.get())
        output.accept(ModItems.BATTLESHIP_YAMATO_SPAWN_EGG.get())
        output.accept(ModItems.SUBM_U511_SPAWN_EGG.get())
        output.accept(ModItems.SUBM_RO500_SPAWN_EGG.get())

        for (egg in ModItems.BOSS_EGGS) {
            output.accept(egg!!.get())
        }
    }

    private fun addMaterials(output: CreativeModeTab.Output) {
        output.accept(ModItems.ABYSS_METAL.get())
        CreativeTabVariantHelper.addAbyssNuggetVariants(output)
        output.accept(ModItems.AMMO_LIGHT.get())
        output.accept(ModItems.AMMO_LIGHT_CONTAINER.get())
        output.accept(ModItems.AMMO_HEAVY.get())
        output.accept(ModItems.AMMO_HEAVY_CONTAINER.get())
        CreativeTabVariantHelper.addGrudgeVariants(output)
        output.accept(ModItems.ABYSS_POLYMETAL.get())
    }

    private fun addEquipment(output: CreativeModeTab.Output) {
        CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_AIRPLANE)
        CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_AMMO)
        CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_ARMOR)
        CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_CANNON)
        CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_CATAPULT)
        CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_COMPASS)
        CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_DRUM)
        CreativeTabVariantHelper.addShipTankVariants(output)
        CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_FLARE)
        CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_MACHINEGUN)
        CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_RADAR)
        CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_SEARCHLIGHT)
        CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_TORPEDO)
        CreativeTabVariantHelper.addSortedLegacyEquipVariants(output, ModItems.EQUIP_TURBINE)
    }

    private fun addMiscTools(output: CreativeModeTab.Output) {
        output.accept(ModItems.BUCKET_REPAIR.get())
        CreativeTabVariantHelper.addCombatRationVariants(output)
        output.accept(ModItems.DESK_ITEM_BOOK.get())
        output.accept(ModItems.DESK_ITEM_RADAR.get())
        output.accept(ModItems.INSTANT_CON_MAT.get())
        output.accept(ModItems.KAITAI_HAMMER.get())
        output.accept(ModItems.MARRIAGE_RING.get())
        output.accept(ModItems.MODERN_KIT.get())
        output.accept(ModItems.OWNER_PAPER.get())
        output.accept(ModItems.OP_TOOL.get())
        output.accept(ModItems.SHIN_COMB.get())
        CreativeTabVariantHelper.addPointerVariants(output)
        output.accept(ModItems.RECIPE_PAPER.get())
        output.accept(ModItems.REPAIR_GODDESS.get())
        output.accept(ModItems.TARGET_WRENCH.get())
        output.accept(ModItems.TRAINING_BOOK.get())
        output.accept(ModItems.TOY_AIRPLANE.get())
    }

    private fun addBlocks(output: CreativeModeTab.Output) {
        output.accept(ModItems.ABYSSIUM.get())
        output.accept(ModItems.CRANE.get())
        output.accept(ModItems.DESK.get())
        output.accept(ModItems.FRAME_BLOCK.get())
        output.accept(ModItems.GRUDGE_BLOCK.get())
        output.accept(ModItems.GRUDGE_HEAVY_BLOCK.get())
        output.accept(ModItems.GRUDGE_HEAVY_DECO_BLOCK.get())
        output.accept(ModItems.POLYMETAL.get())
        output.accept(ModItems.POLYMETAL_GRAVEL.get())
        output.accept(ModItems.POLYMETAL_ORE.get())
        output.accept(ModItems.SMALL_SHIPYARD.get())
        output.accept(ModItems.LARGE_SHIPYARD.get())
        output.accept(ModItems.VOL_CORE.get())
        output.accept(ModItems.WAYPOINT.get())
    }

    private fun addDebug(output: CreativeModeTab.Output) {
        output.accept(ModItems.DEBUG_INSPECTOR.get())
    }
}
