package org.trp.shincolle.integration.jei

import mezz.jei.api.registration.IRecipeRegistration
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.init.ModItems

/**
 * Registers item descriptions for JEI's ingredient panel.
 * Shows ship class info for spawn eggs and category descriptions for equipment.
 */
object JeiItemDescription {

    /**
     * Register all item descriptions.
     */
    fun register(registration: IRecipeRegistration) {
        registerSpawnEggDescriptions(registration)
        registerEquipmentDescriptions(registration)
        registerBlockDescriptions(registration)
    }

    private fun registerSpawnEggDescriptions(registration: IRecipeRegistration) {
        // Random ship spawn eggs used in shipyard crafting
        addInfo(
            registration, ItemStack(ModItems.SHIPSPAWNEGGS.get()),
            Component.translatable("jei.description.shincolle.shipspawneggs")
        )
        addInfo(
            registration, ItemStack(ModItems.SHIPSPAWNEGGL.get()),
            Component.translatable("jei.description.shincolle.shipspawneggl")
        )

        // Individual ship spawn eggs
        addInfo(registration, ItemStack(ModItems.DESTROYER_I_SPAWN_EGG.get()), shipClassDesc("Destroyer I-Class"))
        addInfo(registration, ItemStack(ModItems.DESTROYER_RO_SPAWN_EGG.get()), shipClassDesc("Destroyer Ro-Class"))
        addInfo(registration, ItemStack(ModItems.DESTROYER_HA_SPAWN_EGG.get()), shipClassDesc("Destroyer Ha-Class"))
        addInfo(registration, ItemStack(ModItems.DESTROYER_NI_SPAWN_EGG.get()), shipClassDesc("Destroyer Ni-Class"))
        addInfo(registration, ItemStack(ModItems.HEAVY_CRUISER_RI_SPAWN_EGG.get()), shipClassDesc("Heavy Cruiser Ri-Class"))
        addInfo(registration, ItemStack(ModItems.HEAVY_CRUISER_NE_SPAWN_EGG.get()), shipClassDesc("Heavy Cruiser Ne-Class"))
        addInfo(registration, ItemStack(ModItems.CARRIER_WO_SPAWN_EGG.get()), shipClassDesc("Carrier Wo-Class"))
        addInfo(registration, ItemStack(ModItems.BATTLESHIP_RU_SPAWN_EGG.get()), shipClassDesc("Battleship Ru-Class"))
        addInfo(registration, ItemStack(ModItems.BATTLESHIP_TA_SPAWN_EGG.get()), shipClassDesc("Battleship Ta-Class"))
        addInfo(registration, ItemStack(ModItems.BATTLESHIP_RE_SPAWN_EGG.get()), shipClassDesc("Battleship Re-Class"))
        addInfo(registration, ItemStack(ModItems.TRANSPORT_WA_SPAWN_EGG.get()), shipClassDesc("Transport Wa-Class"))
        addInfo(registration, ItemStack(ModItems.SUBM_KA_SPAWN_EGG.get()), shipClassDesc("Submarine Ka-Class"))
        addInfo(registration, ItemStack(ModItems.SUBM_YO_SPAWN_EGG.get()), shipClassDesc("Submarine Yo-Class"))
        addInfo(registration, ItemStack(ModItems.SUBM_SO_SPAWN_EGG.get()), shipClassDesc("Submarine So-Class"))
        addInfo(registration, ItemStack(ModItems.DESTROYER_HIME_SPAWN_EGG.get()), shipClassDesc("Destroyer Hime"))
        addInfo(registration, ItemStack(ModItems.CA_HIME_SPAWN_EGG.get()), shipClassDesc("CA Hime"))
        addInfo(registration, ItemStack(ModItems.AIRFIELD_HIME_SPAWN_EGG.get()), shipClassDesc("Airfield Hime"))
        addInfo(registration, ItemStack(ModItems.BATTLESHIP_HIME_SPAWN_EGG.get()), shipClassDesc("Battleship Hime"))
        addInfo(registration, ItemStack(ModItems.CARRIER_HIME_SPAWN_EGG.get()), shipClassDesc("Carrier Hime"))
        addInfo(registration, ItemStack(ModItems.HARBOUR_HIME_SPAWN_EGG.get()), shipClassDesc("Harbour Hime"))
        addInfo(registration, ItemStack(ModItems.ISOLATED_HIME_SPAWN_EGG.get()), shipClassDesc("Isolated Island Hime"))
        addInfo(registration, ItemStack(ModItems.MIDWAY_HIME_SPAWN_EGG.get()), shipClassDesc("Midway Hime"))
        addInfo(registration, ItemStack(ModItems.NORTHERN_HIME_SPAWN_EGG.get()), shipClassDesc("Northern Hime"))
        addInfo(registration, ItemStack(ModItems.SUBM_HIME_SPAWN_EGG.get()), shipClassDesc("Submarine Hime"))
        addInfo(registration, ItemStack(ModItems.SSNH_SPAWN_EGG.get()), shipClassDesc("SSNH"))
        addInfo(registration, ItemStack(ModItems.CARRIER_W_DEMON_SPAWN_EGG.get()), shipClassDesc("Carrier W Demon"))
        addInfo(registration, ItemStack(ModItems.DESTROYER_AKATSUKI_SPAWN_EGG.get()), shipClassDesc("Destroyer Akatsuki"))
        addInfo(registration, ItemStack(ModItems.DESTROYER_HIBIKI_SPAWN_EGG.get()), shipClassDesc("Destroyer Hibiki"))
        addInfo(registration, ItemStack(ModItems.DESTROYER_IKAZUCHI_SPAWN_EGG.get()), shipClassDesc("Destroyer Ikazuchi"))
        addInfo(registration, ItemStack(ModItems.DESTROYER_INAZUMA_SPAWN_EGG.get()), shipClassDesc("Destroyer Inazuma"))
        addInfo(registration, ItemStack(ModItems.DESTROYER_SHIMAKAZE_SPAWN_EGG.get()), shipClassDesc("Destroyer Shimakaze"))
        addInfo(registration, ItemStack(ModItems.CRUISER_TENRYUU_SPAWN_EGG.get()), shipClassDesc("Light Cruiser Tenryuu"))
        addInfo(registration, ItemStack(ModItems.CRUISER_TATSUTA_SPAWN_EGG.get()), shipClassDesc("Light Cruiser Tatsuta"))
        addInfo(registration, ItemStack(ModItems.CRUISER_TAKAO_SPAWN_EGG.get()), shipClassDesc("Heavy Cruiser Takao"))
        addInfo(registration, ItemStack(ModItems.CRUISER_ATAGO_SPAWN_EGG.get()), shipClassDesc("Heavy Cruiser Atago"))
        addInfo(registration, ItemStack(ModItems.CARRIER_KAGA_SPAWN_EGG.get()), shipClassDesc("Aircraft Carrier Kaga"))
        addInfo(registration, ItemStack(ModItems.CARRIER_AKAGI_SPAWN_EGG.get()), shipClassDesc("Aircraft Carrier Akagi"))
        addInfo(registration, ItemStack(ModItems.BB_KONGOU_SPAWN_EGG.get()), shipClassDesc("Battleship Kongou"))
        addInfo(registration, ItemStack(ModItems.BB_HIEI_SPAWN_EGG.get()), shipClassDesc("Battleship Hiei"))
        addInfo(registration, ItemStack(ModItems.BB_HARUNA_SPAWN_EGG.get()), shipClassDesc("Battleship Haruna"))
        addInfo(registration, ItemStack(ModItems.BB_KIRISHIMA_SPAWN_EGG.get()), shipClassDesc("Battleship Kirishima"))
        addInfo(registration, ItemStack(ModItems.BATTLESHIP_NAGATO_SPAWN_EGG.get()), shipClassDesc("Battleship Nagato"))
        addInfo(registration, ItemStack(ModItems.BATTLESHIP_YAMATO_SPAWN_EGG.get()), shipClassDesc("Battleship Yamato"))
        addInfo(registration, ItemStack(ModItems.SUBM_U511_SPAWN_EGG.get()), shipClassDesc("Submarine U-511"))
        addInfo(registration, ItemStack(ModItems.SUBM_RO500_SPAWN_EGG.get()), shipClassDesc("Submarine Ro-500"))
    }

    private fun registerEquipmentDescriptions(registration: IRecipeRegistration) {
        addInfo(registration, ItemStack(ModItems.EQUIP_CANNON.get()), Component.translatable("jei.description.shincolle.equip.cannon"))
        addInfo(registration, ItemStack(ModItems.EQUIP_TORPEDO.get()), Component.translatable("jei.description.shincolle.equip.torpedo"))
        addInfo(registration, ItemStack(ModItems.EQUIP_AIRPLANE.get()), Component.translatable("jei.description.shincolle.equip.airplane"))
        addInfo(registration, ItemStack(ModItems.EQUIP_RADAR.get()), Component.translatable("jei.description.shincolle.equip.radar"))
        addInfo(registration, ItemStack(ModItems.EQUIP_TURBINE.get()), Component.translatable("jei.description.shincolle.equip.turbine"))
        addInfo(registration, ItemStack(ModItems.EQUIP_ARMOR.get()), Component.translatable("jei.description.shincolle.equip.armor"))
        addInfo(registration, ItemStack(ModItems.EQUIP_MACHINEGUN.get()), Component.translatable("jei.description.shincolle.equip.machinegun"))
        addInfo(registration, ItemStack(ModItems.EQUIP_CATAPULT.get()), Component.translatable("jei.description.shincolle.equip.catapult"))
        addInfo(registration, ItemStack(ModItems.EQUIP_DRUM.get()), Component.translatable("jei.description.shincolle.equip.drum"))
        addInfo(registration, ItemStack(ModItems.EQUIP_COMPASS.get()), Component.translatable("jei.description.shincolle.equip.compass"))
        addInfo(registration, ItemStack(ModItems.EQUIP_FLARE.get()), Component.translatable("jei.description.shincolle.equip.flare"))
        addInfo(registration, ItemStack(ModItems.EQUIP_SEARCHLIGHT.get()), Component.translatable("jei.description.shincolle.equip.searchlight"))
        addInfo(registration, ItemStack(ModItems.EQUIP_AMMO.get()), Component.translatable("jei.description.shincolle.equip.ammo"))
    }

    private fun registerBlockDescriptions(registration: IRecipeRegistration) {
        addInfo(
            registration, ItemStack(ModItems.LARGE_SHIPYARD.get()),
            Component.translatable("jei.description.shincolle.large_shipyard")
        )
    }

    private fun shipClassDesc(name: String): Component =
        Component.translatable("jei.description.shincolle.shipclass", name)

    private fun addInfo(registration: IRecipeRegistration, stack: ItemStack, vararg description: Component) {
        if (stack.isEmpty) return
        try {
            registration.addItemStackInfo(stack, *description)
        } catch (_: Exception) {
            // JEI API variation - skip silently to avoid crashes
        }
    }
}
