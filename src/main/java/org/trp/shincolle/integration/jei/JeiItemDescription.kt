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
        addSpawnEggInfo(registration, ItemStack(ModItems.DESTROYER_I_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.DESTROYER_RO_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.DESTROYER_HA_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.DESTROYER_NI_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.HEAVY_CRUISER_RI_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.HEAVY_CRUISER_NE_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.CARRIER_WO_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.BATTLESHIP_RU_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.BATTLESHIP_TA_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.BATTLESHIP_RE_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.TRANSPORT_WA_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.SUBM_KA_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.SUBM_YO_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.SUBM_SO_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.DESTROYER_HIME_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.CA_HIME_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.AIRFIELD_HIME_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.BATTLESHIP_HIME_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.CARRIER_HIME_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.HARBOUR_HIME_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.ISOLATED_HIME_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.MIDWAY_HIME_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.NORTHERN_HIME_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.SUBM_HIME_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.SSNH_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.CARRIER_W_DEMON_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.DESTROYER_AKATSUKI_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.DESTROYER_HIBIKI_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.DESTROYER_IKAZUCHI_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.DESTROYER_INAZUMA_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.DESTROYER_SHIMAKAZE_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.CRUISER_TENRYUU_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.CRUISER_TATSUTA_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.CRUISER_TAKAO_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.CRUISER_ATAGO_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.CARRIER_KAGA_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.CARRIER_AKAGI_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.BB_KONGOU_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.BB_HIEI_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.BB_HARUNA_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.BB_KIRISHIMA_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.BATTLESHIP_NAGATO_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.BATTLESHIP_YAMATO_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.SUBM_U511_SPAWN_EGG.get()))
        addSpawnEggInfo(registration, ItemStack(ModItems.SUBM_RO500_SPAWN_EGG.get()))
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

    private fun shipClassDesc(stack: ItemStack): Component =
        Component.translatable("jei.description.shincolle.shipclass", stack.hoverName.string)

    private fun addSpawnEggInfo(registration: IRecipeRegistration, stack: ItemStack) {
        if (stack.isEmpty) return
        addInfo(registration, stack, shipClassDesc(stack))
    }

    private fun addInfo(registration: IRecipeRegistration, stack: ItemStack, vararg description: Component) {
        if (stack.isEmpty) return
        try {
            registration.addItemStackInfo(stack, *description)
        } catch (_: Exception) {
            // JEI API variation - skip silently to avoid crashes
        }
    }
}
