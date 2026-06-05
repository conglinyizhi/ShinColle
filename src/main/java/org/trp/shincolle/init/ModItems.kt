package org.trp.shincolle.init

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import org.trp.shincolle.Shincolle
import org.trp.shincolle.item.*
import org.trp.shincolle.item.LegacyEquipStats.getMainAttrs
import java.util.function.Supplier
import java.util.function.ToIntFunction

object ModItems {
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(Shincolle.MODID)

    private val GRUDGE_FOOD: FoodProperties = FoodProperties.Builder()
        .nutrition(6)
        .saturationModifier(0.0f)
        .alwaysEdible()
        .build()

    private val EQUIP_AIRPLANE_TYPES =
        intArrayOf(6, 6, 6, 7, 8, 8, 8, 9, 9, 10, 10, 11, 11, 12, 13, 7, 9, 11, 9, 11, 11, 9)
    private val EQUIP_AIRPLANE_MODELS = intArrayOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 0, 1, 2, 1, 2, 2, 1)
    private val EQUIP_AMMO_TYPES = intArrayOf(28, 29, 28, 29, 29, 29, 29, 29, 29)
    private val EQUIP_ARMOR_TYPES = intArrayOf(18, 19, 18, 18, 19, 18, 19)
    private val EQUIP_CANNON_TYPES = intArrayOf(0, 0, 1, 1, 1, 1, 2, 2, 2, 3, 3, 3, 0, 1, 3, 3)
    private val EQUIP_CANNON_MODELS = intArrayOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 0, 1, 2, 2)
    private val EQUIP_CATAPULT_TYPES = intArrayOf(22, 22, 23, 23)
    private val EQUIP_COMPASS_TYPES = intArrayOf(25)
    private val EQUIP_DRUM_TYPES = intArrayOf(24, 24, 24)
    private val EQUIP_DRUM_MODELS = intArrayOf(0, 1, 2)
    private val EQUIP_FLARE_TYPES = intArrayOf(26)
    private val EQUIP_MACHINEGUN_TYPES = intArrayOf(20, 20, 20, 20, 21, 21, 21)
    private val EQUIP_RADAR_TYPES = intArrayOf(14, 14, 14, 14, 14, 15, 15, 15, 15)
    private val EQUIP_SEARCHLIGHT_TYPES = intArrayOf(27)
    private val EQUIP_TORPEDO_TYPES = intArrayOf(4, 4, 4, 5, 5, 5, 5)
    private val EQUIP_TURBINE_TYPES = intArrayOf(16, 16, 17, 17, 17)

    @JvmField
    val SHIPSPAWNEGGS: DeferredItem<Item?> = ITEMS.register<Item?>(
        "shipspawneggs",
        Supplier {
            RandomShipSpawnEggItem(
                ModEntities.DESTROYER_I, ShipClass.DESTROYER, false,
                0xFFFFFF, 0xFFFFFF, Item.Properties()
            )
        })

    @JvmField
    val SHIPSPAWNEGGL: DeferredItem<Item?> = ITEMS.register<Item?>(
        "shipspawneggl",
        Supplier {
            RandomShipSpawnEggItem(
                ModEntities.DESTROYER_HIME, ShipClass.PRINCESS, true,
                0xFFFFFF, 0xFFFFFF, Item.Properties()
            )
        })

    @JvmField
    val DESTROYER_I_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "destroyer_i_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.DESTROYER_I,
                ShipClass.DESTROYER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val DESTROYER_RO_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "destroyer_ro_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.DESTROYER_RO,
                ShipClass.DESTROYER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val DESTROYER_HA_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "destroyer_ha_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.DESTROYER_HA,
                ShipClass.DESTROYER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val DESTROYER_NI_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "destroyer_ni_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.DESTROYER_NI,
                ShipClass.DESTROYER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val HEAVY_CRUISER_RI_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "heavy_cruiser_ri_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.HEAVY_CRUISER_RI,
                ShipClass.HEAVY_CRUISER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val HEAVY_CRUISER_NE_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "heavy_cruiser_ne_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.HEAVY_CRUISER_NE,
                ShipClass.HEAVY_CRUISER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val CARRIER_WO_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "carrier_wo_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.CARRIER_WO,
                ShipClass.AIRCRAFT_CARRIER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val BATTLESHIP_RU_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "battleship_ru_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.BATTLESHIP_RU,
                ShipClass.BATTLESHIP,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val BATTLESHIP_TA_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "battleship_ta_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.BATTLESHIP_TA,
                ShipClass.BATTLESHIP,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val BATTLESHIP_RE_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "battleship_re_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.BATTLESHIP_RE,
                ShipClass.BATTLESHIP,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val TRANSPORT_WA_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "transport_wa_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.TRANSPORT_WA,
                ShipClass.AUXILIARY_OILER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val SUBM_KA_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "subm_ka_spawn_egg",
        Supplier { ShipSpawnEggItem(ModEntities.SUBM_KA, ShipClass.SUBMARINE, 0xFFFFFF, 0xFFFFFF, Item.Properties()) })

    @JvmField
    val SUBM_YO_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "subm_yo_spawn_egg",
        Supplier { ShipSpawnEggItem(ModEntities.SUBM_YO, ShipClass.SUBMARINE, 0xFFFFFF, 0xFFFFFF, Item.Properties()) })

    @JvmField
    val SUBM_SO_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "subm_so_spawn_egg",
        Supplier { ShipSpawnEggItem(ModEntities.SUBM_SO, ShipClass.SUBMARINE, 0xFFFFFF, 0xFFFFFF, Item.Properties()) })

    @JvmField
    val DESTROYER_HIME_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "destroyer_hime_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.DESTROYER_HIME,
                ShipClass.PRINCESS,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val CA_HIME_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "ca_hime_spawn_egg",
        Supplier { ShipSpawnEggItem(ModEntities.CA_HIME, ShipClass.PRINCESS, 0xFFFFFF, 0xFFFFFF, Item.Properties()) })

    @JvmField
    val AIRFIELD_HIME_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "airfield_hime_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.AIRFIELD_HIME,
                ShipClass.PRINCESS,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val BATTLESHIP_HIME_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "battleship_hime_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.BATTLESHIP_HIME,
                ShipClass.PRINCESS,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val CARRIER_HIME_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "carrier_hime_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.CARRIER_HIME,
                ShipClass.PRINCESS,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val HARBOUR_HIME_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "harbour_hime_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.HARBOUR_HIME,
                ShipClass.PRINCESS,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val ISOLATED_HIME_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "isolated_hime_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.ISOLATED_HIME,
                ShipClass.PRINCESS,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val MIDWAY_HIME_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "midway_hime_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.MIDWAY_HIME,
                ShipClass.PRINCESS,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val NORTHERN_HIME_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "northern_hime_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.NORTHERN_HIME,
                ShipClass.PRINCESS,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    @JvmField
    val SUBM_HIME_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "subm_hime_spawn_egg",
        Supplier { ShipSpawnEggItem(ModEntities.SUBM_HIME, ShipClass.PRINCESS, 0xFFFFFF, 0xFFFFFF, Item.Properties()) })

    @JvmField
    val SSNH_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "ssnh_spawn_egg",
        Supplier { ShipSpawnEggItem(ModEntities.SSNH, ShipClass.SUBMARINE, 0xFFFFFF, 0xFFFFFF, Item.Properties()) })

    @JvmField
    val CARRIER_W_DEMON_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "carrier_w_demon_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.CARRIER_W_DEMON,
                ShipClass.DEMON,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val DESTROYER_AKATSUKI_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "destroyer_akatsuki_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.DESTROYER_AKATSUKI,
                ShipClass.DESTROYER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val DESTROYER_HIBIKI_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "destroyer_hibiki_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.DESTROYER_HIBIKI,
                ShipClass.DESTROYER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val DESTROYER_IKAZUCHI_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "destroyer_ikazuchi_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.DESTROYER_IKAZUCHI,
                ShipClass.DESTROYER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val DESTROYER_INAZUMA_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "destroyer_inazuma_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.DESTROYER_INAZUMA,
                ShipClass.DESTROYER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val DESTROYER_SHIMAKAZE_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "destroyer_shimakaze_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.DESTROYER_SHIMAKAZE,
                ShipClass.DESTROYER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val CRUISER_TENRYUU_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "cruiser_tenryuu_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.CRUISER_TENRYUU,
                ShipClass.LIGHT_CRUISER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val CRUISER_TATSUTA_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "cruiser_tatsuta_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.CRUISER_TATSUTA,
                ShipClass.LIGHT_CRUISER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val CRUISER_TAKAO_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "cruiser_takao_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.CRUISER_TAKAO,
                ShipClass.HEAVY_CRUISER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val CRUISER_ATAGO_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "cruiser_atago_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.CRUISER_ATAGO,
                ShipClass.HEAVY_CRUISER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val CARRIER_KAGA_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "carrier_kaga_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.CARRIER_KAGA,
                ShipClass.AIRCRAFT_CARRIER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val CARRIER_AKAGI_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "carrier_akagi_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.CARRIER_AKAGI,
                ShipClass.AIRCRAFT_CARRIER,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val BB_KONGOU_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "bb_kongou_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.BB_KONGOU,
                ShipClass.BATTLESHIP,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val BB_HIEI_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "bb_hiei_spawn_egg",
        Supplier { ShipSpawnEggItem(ModEntities.BB_HIEI, ShipClass.BATTLESHIP, 0xFFFFFF, 0xFFFFFF, Item.Properties()) })

    val BB_HARUNA_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "bb_haruna_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.BB_HARUNA,
                ShipClass.BATTLESHIP,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val BB_KIRISHIMA_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "bb_kirishima_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.BB_KIRISHIMA,
                ShipClass.BATTLESHIP,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val BATTLESHIP_NAGATO_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "battleship_nagato_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.BATTLESHIP_NAGATO,
                ShipClass.BATTLESHIP,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val BATTLESHIP_YAMATO_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "battleship_yamato_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.BATTLESHIP_YAMATO,
                ShipClass.BATTLESHIP,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val SUBM_U511_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "subm_u511_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.SUBM_U511,
                ShipClass.SUBMARINE,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    val SUBM_RO500_SPAWN_EGG: DeferredItem<Item?> = ITEMS.register<Item?>(
        "subm_ro500_spawn_egg",
        Supplier {
            ShipSpawnEggItem(
                ModEntities.SUBM_RO500,
                ShipClass.SUBMARINE,
                0xFFFFFF,
                0xFFFFFF,
                Item.Properties()
            )
        })

    // ===== BOSS SPAWN EGGS (hostile, with ammo) =====
    val BOSS_EGGS: MutableList<DeferredItem<BossSpawnEggItem?>?> = ArrayList<DeferredItem<BossSpawnEggItem?>?>()

    private fun registerBossEgg(
        name: String?,
        type: Supplier<out EntityType<out Mob?>?>
    ): DeferredItem<BossSpawnEggItem?> {
        val egg = ITEMS.register<BossSpawnEggItem?>(
            name + "_boss_egg",
            Supplier { BossSpawnEggItem(type, 0x444444, 0x888888, Item.Properties()) })
        BOSS_EGGS.add(egg)
        return egg
    }

    val DESTROYER_I_BOSS_EGG: DeferredItem<BossSpawnEggItem?> = registerBossEgg("destroyer_i", ModEntities.DESTROYER_I)
    val DESTROYER_RO_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("destroyer_ro", ModEntities.DESTROYER_RO)
    val DESTROYER_HA_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("destroyer_ha", ModEntities.DESTROYER_HA)
    val DESTROYER_NI_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("destroyer_ni", ModEntities.DESTROYER_NI)
    val HEAVY_CRUISER_RI_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("heavy_cruiser_ri", ModEntities.HEAVY_CRUISER_RI)
    val HEAVY_CRUISER_NE_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("heavy_cruiser_ne", ModEntities.HEAVY_CRUISER_NE)
    val CARRIER_WO_BOSS_EGG: DeferredItem<BossSpawnEggItem?> = registerBossEgg("carrier_wo", ModEntities.CARRIER_WO)
    val BATTLESHIP_RU_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("battleship_ru", ModEntities.BATTLESHIP_RU)
    val BATTLESHIP_TA_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("battleship_ta", ModEntities.BATTLESHIP_TA)
    val BATTLESHIP_RE_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("battleship_re", ModEntities.BATTLESHIP_RE)
    val TRANSPORT_WA_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("transport_wa", ModEntities.TRANSPORT_WA)
    val SUBM_KA_BOSS_EGG: DeferredItem<BossSpawnEggItem?> = registerBossEgg("subm_ka", ModEntities.SUBM_KA)
    val SUBM_YO_BOSS_EGG: DeferredItem<BossSpawnEggItem?> = registerBossEgg("subm_yo", ModEntities.SUBM_YO)
    val SUBM_SO_BOSS_EGG: DeferredItem<BossSpawnEggItem?> = registerBossEgg("subm_so", ModEntities.SUBM_SO)
    val DESTROYER_HIME_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("destroyer_hime", ModEntities.DESTROYER_HIME)
    val CA_HIME_BOSS_EGG: DeferredItem<BossSpawnEggItem?> = registerBossEgg("ca_hime", ModEntities.CA_HIME)
    val CARRIER_HIME_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("carrier_hime", ModEntities.CARRIER_HIME)
    val BATTLESHIP_HIME_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("battleship_hime", ModEntities.BATTLESHIP_HIME)
    val AIRFIELD_HIME_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("airfield_hime", ModEntities.AIRFIELD_HIME)
    val HARBOUR_HIME_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("harbour_hime", ModEntities.HARBOUR_HIME)
    val ISOLATED_HIME_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("isolated_hime", ModEntities.ISOLATED_HIME)
    val MIDWAY_HIME_BOSS_EGG: DeferredItem<BossSpawnEggItem?> = registerBossEgg("midway_hime", ModEntities.MIDWAY_HIME)
    val NORTHERN_HIME_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("northern_hime", ModEntities.NORTHERN_HIME)
    val SUBM_HIME_BOSS_EGG: DeferredItem<BossSpawnEggItem?> = registerBossEgg("subm_hime", ModEntities.SUBM_HIME)
    val SSNH_BOSS_EGG: DeferredItem<BossSpawnEggItem?> = registerBossEgg("ssnh", ModEntities.SSNH)
    val CARRIER_W_DEMON_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("carrier_w_demon", ModEntities.CARRIER_W_DEMON)
    val DESTROYER_AKATSUKI_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("destroyer_akatsuki", ModEntities.DESTROYER_AKATSUKI)
    val DESTROYER_HIBIKI_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("destroyer_hibiki", ModEntities.DESTROYER_HIBIKI)
    val DESTROYER_IKAZUCHI_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("destroyer_ikazuchi", ModEntities.DESTROYER_IKAZUCHI)
    val DESTROYER_INAZUMA_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("destroyer_inazuma", ModEntities.DESTROYER_INAZUMA)
    val DESTROYER_SHIMAKAZE_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("destroyer_shimakaze", ModEntities.DESTROYER_SHIMAKAZE)
    val CRUISER_TENRYUU_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("cruiser_tenryuu", ModEntities.CRUISER_TENRYUU)
    val CRUISER_TATSUTA_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("cruiser_tatsuta", ModEntities.CRUISER_TATSUTA)
    val CRUISER_TAKAO_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("cruiser_takao", ModEntities.CRUISER_TAKAO)
    val CRUISER_ATAGO_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("cruiser_atago", ModEntities.CRUISER_ATAGO)
    val CARRIER_KAGA_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("carrier_kaga", ModEntities.CARRIER_KAGA)
    val CARRIER_AKAGI_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("carrier_akagi", ModEntities.CARRIER_AKAGI)
    val BB_KONGOU_BOSS_EGG: DeferredItem<BossSpawnEggItem?> = registerBossEgg("bb_kongou", ModEntities.BB_KONGOU)
    val BB_HIEI_BOSS_EGG: DeferredItem<BossSpawnEggItem?> = registerBossEgg("bb_hiei", ModEntities.BB_HIEI)
    val BB_HARUNA_BOSS_EGG: DeferredItem<BossSpawnEggItem?> = registerBossEgg("bb_haruna", ModEntities.BB_HARUNA)
    val BB_KIRISHIMA_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("bb_kirishima", ModEntities.BB_KIRISHIMA)
    val BATTLESHIP_NAGATO_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("battleship_nagato", ModEntities.BATTLESHIP_NAGATO)
    val BATTLESHIP_YAMATO_BOSS_EGG: DeferredItem<BossSpawnEggItem?> =
        registerBossEgg("battleship_yamato", ModEntities.BATTLESHIP_YAMATO)
    val SUBM_U511_BOSS_EGG: DeferredItem<BossSpawnEggItem?> = registerBossEgg("subm_u511", ModEntities.SUBM_U511)
    val SUBM_RO500_BOSS_EGG: DeferredItem<BossSpawnEggItem?> = registerBossEgg("subm_ro500", ModEntities.SUBM_RO500)

    @JvmField
    val POINTER_ITEM: DeferredItem<Item> = ITEMS.register<Item?>(
        "pointer_item",
        Supplier {
            PointerItem(
                Item.Properties().attributes(
                    ItemAttributeModifiers.builder()
                        .add(
                            Attributes.ENTITY_INTERACTION_RANGE,
                            AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "pointer_reach"),
                                100.0,
                                AttributeModifier.Operation.ADD_VALUE
                            ),
                            EquipmentSlotGroup.MAINHAND
                        )
                        .build()
                )
            )
        })

    @JvmField
    val REPAIR_GODDESS: DeferredItem<RepairGoddessItem?> = ITEMS.register<RepairGoddessItem?>(
        "repairgoddess",
        Supplier { RepairGoddessItem(Item.Properties()) })

    @JvmField
    val COMBAT_RATION: DeferredItem<Item> = ITEMS.register<Item?>(
        "combatration",
        Supplier { CombatRationItem(Item.Properties().stacksTo(16)) })

    @JvmField
    val BUCKET_REPAIR: DeferredItem<Item?> = ITEMS.register<Item?>(
        "bucketrepair",
        Supplier { BucketRepairItem(Item.Properties().stacksTo(16)) })

    @JvmField
    val TOY_AIRPLANE: DeferredItem<Item?> = ITEMS.register<Item?>(
        "toyairplane",
        Supplier { ToyAirplaneItem(Item.Properties().stacksTo(16)) })

    @JvmField
    val INSTANT_CON_MAT: DeferredItem<Item?> = ITEMS.register<Item?>(
        "instantconmat",
        Supplier { InstantConstructionMaterialItem(Item.Properties()) })

    @JvmField
    val KAITAI_HAMMER: DeferredItem<Item?> = ITEMS.register<Item?>(
        "kaitaihammer",
        Supplier { KaitaiHammerItem(Item.Properties()) })

    @JvmField
    val MARRIAGE_RING: DeferredItem<Item?> = ITEMS.register<Item?>(
        "marriagering",
        Supplier { MarriageRingItem(Item.Properties().stacksTo(1)) })

    @JvmField
    val MODERN_KIT: DeferredItem<Item?> = ITEMS.register<Item?>(
        "modernkit",
        Supplier { ModernKitItem(Item.Properties()) })

    val OWNER_PAPER: DeferredItem<Item?> = ITEMS.register<Item?>(
        "ownerpaper",
        Supplier { OwnerPaperItem(Item.Properties()) })

    val OP_TOOL: DeferredItem<Item?> = ITEMS.register<Item?>(
        "optool",
        Supplier { OPToolItem(Item.Properties()) })

    val SHIN_COMB: DeferredItem<Item?> = ITEMS.register<Item?>(
        "shincomb",
        Supplier { Item(Item.Properties()) })

    @JvmField
    val EQUIP_AIRPLANE: DeferredItem<Item?> = ITEMS.register<Item?>(
        "equipairplane",
        Supplier { LegacyEquipItem(Item.Properties(), "EquipAirplane", EQUIP_AIRPLANE_TYPES, EQUIP_AIRPLANE_MODELS) })

    @JvmField
    val EQUIP_AMMO: DeferredItem<Item?> = ITEMS.register<Item?>(
        "equipammo",
        Supplier { LegacyEquipItem(Item.Properties(), "EquipAmmo", EQUIP_AMMO_TYPES) })

    @JvmField
    val EQUIP_ARMOR: DeferredItem<Item?> = ITEMS.register<Item?>(
        "equiparmor",
        Supplier { LegacyEquipItem(Item.Properties(), "EquipArmor", EQUIP_ARMOR_TYPES) })

    @JvmField
    val EQUIP_CANNON: DeferredItem<Item?> = ITEMS.register<Item?>(
        "equipcannon",
        Supplier { LegacyEquipItem(Item.Properties(), "EquipCannon", EQUIP_CANNON_TYPES, EQUIP_CANNON_MODELS) })

    @JvmField
    val EQUIP_CATAPULT: DeferredItem<Item?> = ITEMS.register<Item?>(
        "equipcatapult",
        Supplier { LegacyEquipItem(Item.Properties(), "EquipCatapult", EQUIP_CATAPULT_TYPES) })

    @JvmField
    val EQUIP_COMPASS: DeferredItem<Item?> = ITEMS.register<Item?>(
        "equipcompass",
        Supplier { LegacyEquipItem(Item.Properties(), "EquipCompass", EQUIP_COMPASS_TYPES) })

    @JvmField
    val EQUIP_DRUM: DeferredItem<Item?> = ITEMS.register<Item?>(
        "equipdrum",
        Supplier { LegacyEquipItem(Item.Properties(), "EquipDrum", EQUIP_DRUM_TYPES, EQUIP_DRUM_MODELS) })

    @JvmField
    val SHIP_TANK: DeferredItem<Item> = ITEMS.register<Item?>(
        "shiptank",
        Supplier { ShipTankItem(Item.Properties().stacksTo(1)) })

    @JvmField
    val EQUIP_FLARE: DeferredItem<Item?> = ITEMS.register<Item?>(
        "equipflare",
        Supplier { LegacyEquipItem(Item.Properties(), "EquipFlare", EQUIP_FLARE_TYPES) })

    @JvmField
    val EQUIP_MACHINEGUN: DeferredItem<Item?> = ITEMS.register<Item?>(
        "equipmachinegun",
        Supplier { LegacyEquipItem(Item.Properties(), "EquipMachinegun", EQUIP_MACHINEGUN_TYPES) })

    @JvmField
    val EQUIP_RADAR: DeferredItem<Item?> = ITEMS.register<Item?>(
        "equipradar",
        Supplier { LegacyEquipItem(Item.Properties(), "EquipRadar", EQUIP_RADAR_TYPES) })

    @JvmField
    val EQUIP_SEARCHLIGHT: DeferredItem<Item?> = ITEMS.register<Item?>(
        "equipsearchlight",
        Supplier { LegacyEquipItem(Item.Properties(), "EquipSearchlight", EQUIP_SEARCHLIGHT_TYPES) })

    @JvmField
    val EQUIP_TORPEDO: DeferredItem<Item?> = ITEMS.register<Item?>(
        "equiptorpedo",
        Supplier { LegacyEquipItem(Item.Properties(), "EquipTorpedo", EQUIP_TORPEDO_TYPES) })

    @JvmField
    val EQUIP_TURBINE: DeferredItem<Item?> = ITEMS.register<Item?>(
        "equipturbine",
        Supplier { LegacyEquipItem(Item.Properties(), "EquipTurbine", EQUIP_TURBINE_TYPES) })

    @JvmField
    val TRAINING_BOOK: DeferredItem<Item?> = ITEMS.register<Item?>(
        "trainingbook",
        Supplier { TrainingBookItem(Item.Properties().stacksTo(1)) })
    val DESK_ITEM_BOOK: DeferredItem<Item?> = ITEMS.register<Item?>(
        "deskitembook",
        Supplier { DeskItemBook(Item.Properties().stacksTo(1)) })
    val DESK_ITEM_RADAR: DeferredItem<Item?> = ITEMS.register<Item?>(
        "deskitemradar",
        Supplier { DeskItemRadar(Item.Properties().stacksTo(1)) })

    val TARGET_WRENCH: DeferredItem<TargetWrenchItem?> = ITEMS.register<TargetWrenchItem?>(
        "targetwrench",
        Supplier { TargetWrenchItem(Item.Properties().stacksTo(1)) })

    val RECIPE_PAPER: DeferredItem<Item?> = ITEMS.register<Item?>(
        "recipepaper",
        Supplier { RecipePaperItem(Item.Properties()) })

    @JvmField
    val AMMO_LIGHT: DeferredItem<Item?> = ITEMS.register<Item?>(
        "ammo",
        Supplier { Item(Item.Properties()) })

    @JvmField
    val AMMO_LIGHT_CONTAINER: DeferredItem<Item?> = ITEMS.register<Item?>(
        "ammo1",
        Supplier { Item(Item.Properties()) })

    @JvmField
    val AMMO_HEAVY: DeferredItem<Item?> = ITEMS.register<Item?>(
        "ammo2",
        Supplier { Item(Item.Properties()) })

    @JvmField
    val AMMO_HEAVY_CONTAINER: DeferredItem<Item?> = ITEMS.register<Item?>(
        "ammo3",
        Supplier { Item(Item.Properties()) })

    @JvmField
    val GRUDGE: DeferredItem<Item> = ITEMS.register<Item?>(
        "grudge",
        Supplier { GrudgeItem(Item.Properties().food(GRUDGE_FOOD)) })

    @JvmField
    val ABYSS_NUGGET: DeferredItem<Item> = ITEMS.register<Item?>(
        "abyss_nugget",
        Supplier { AbyssNuggetItem(Item.Properties()) })

    @JvmField
    val ABYSS_METAL: DeferredItem<Item?> = ITEMS.register<Item?>(
        "abyss_metal",
        Supplier { Item(Item.Properties()) })

    @JvmField
    val ABYSS_POLYMETAL: DeferredItem<Item?> = ITEMS.register<Item?>(
        "abyss_polymetal",
        Supplier { Item(Item.Properties()) })

    @JvmField
    val ABYSSIUM: DeferredItem<Item?> = ITEMS.register<Item?>(
        "abyssium",
        Supplier { BlockItem(ModBlocks.ABYSSIUM.get(), Item.Properties()) })

    val DESK: DeferredItem<Item?> = ITEMS.register<Item?>(
        "blockdesk",
        Supplier { DeskBlockItem(ModBlocks.DESK.get(), Item.Properties()) })

    @JvmField
    val GRUDGE_BLOCK: DeferredItem<Item?> = ITEMS.register<Item?>(
        "grudge_block",
        Supplier { BlockItem(ModBlocks.GRUDGE_BLOCK.get(), Item.Properties()) })

    val GRUDGE_XP_BLOCK: DeferredItem<Item?> = ITEMS.register<Item?>(
        "grudge_xp_block",
        Supplier { BlockItem(ModBlocks.GRUDGE_XP_BLOCK.get(), Item.Properties()) })

    @JvmField
    val GRUDGE_HEAVY_BLOCK: DeferredItem<Item?> = ITEMS.register<Item?>(
        "grudge_heavy_block",
        Supplier { GrudgeHeavyBlockItem(ModBlocks.GRUDGE_HEAVY_BLOCK.get(), Item.Properties()) })

    val FRAME_BLOCK: DeferredItem<Item?> = ITEMS.register<Item?>(
        "blockframe",
        Supplier { BlockItem(ModBlocks.FRAME_BLOCK.get(), Item.Properties()) })

    @JvmField
    val POLYMETAL: DeferredItem<Item?> = ITEMS.register<Item?>(
        "polymetal",
        Supplier { BlockItem(ModBlocks.POLYMETAL.get(), Item.Properties()) })

    val POLYMETAL_ORE: DeferredItem<Item?> = ITEMS.register<Item?>(
        "polymetal_ore",
        Supplier { BlockItem(ModBlocks.POLYMETAL_ORE.get(), Item.Properties()) })

    val POLYMETAL_GRAVEL: DeferredItem<Item?> = ITEMS.register<Item?>(
        "polymetal_gravel",
        Supplier { BlockItem(ModBlocks.POLYMETAL_GRAVEL.get(), Item.Properties()) })

    val SMALL_SHIPYARD: DeferredItem<Item?> = ITEMS.register<Item?>(
        "small_shipyard",
        Supplier { SmallShipyardBlockItem(ModBlocks.SMALL_SHIPYARD.get(), Item.Properties()) })

    val LARGE_SHIPYARD: DeferredItem<Item?> = ITEMS.register<Item?>(
        "large_shipyard",
        Supplier { BlockItem(ModBlocks.LARGE_SHIPYARD.get(), Item.Properties()) })

    val VOL_CORE: DeferredItem<Item?> = ITEMS.register<Item?>(
        "blockvolcore",
        Supplier { BlockItem(ModBlocks.VOL_CORE.get(), Item.Properties()) })

    val VOL_BLOCK: DeferredItem<Item?> = ITEMS.register<Item?>(
        "blockvolblock",
        Supplier { BlockItem(ModBlocks.VOL_BLOCK.get(), Item.Properties()) })

    @JvmField
    val WAYPOINT: DeferredItem<Item?> = ITEMS.register<Item?>(
        "blockwaypoint",
        Supplier { BlockItem(ModBlocks.WAYPOINT.get(), Item.Properties()) })

    @JvmField
    val CRANE: DeferredItem<Item?> = ITEMS.register<Item?>(
        "blockcrane",
        Supplier { BlockItem(ModBlocks.CRANE.get(), Item.Properties()) })
    @JvmField
    val DEBUG_INSPECTOR: DeferredItem<DebugInspectorItem?> = ITEMS.register<DebugInspectorItem?>(
        "debug_inspector",
        Supplier { DebugInspectorItem(Item.Properties().stacksTo(1)) })

    fun addLegacyEquipVariants(output: CreativeModeTab.Output, item: DeferredItem<Item>) {
        val resolved = item.get()
        if (resolved is LegacyEquipItem) {
            resolved.addAllVariantsToCreativeTab(output)
            return
        }

        output.accept(resolved)
    }

    fun addSortedLegacyEquipVariants(output: CreativeModeTab.Output, item: DeferredItem<Item>) {
        val resolved = item.get()
        if (resolved !is LegacyEquipItem) {
            output.accept(resolved)
            return
        }

        val variants: MutableList<ItemStack> = ArrayList<ItemStack>()
        for (variant in 0..<resolved.variantCount) {
            variants.add(resolved.createVariantStack(variant))
        }

        variants.sort(
            Comparator
                .comparingInt<ItemStack>(ToIntFunction { stack: ItemStack -> resolved.getVariant(stack) })
                .thenComparingInt(ToIntFunction { stack: ItemStack? -> resolved.getEquipId(stack!!) })
                .thenComparingInt(ToIntFunction { stack: ItemStack? -> resolved.getVariant(stack!!) })
        )

        for (stack in variants) {
            output.accept(stack)
        }
    }

    private fun getLegacyEquipSortScore(item: LegacyEquipItem, stack: ItemStack): Double {
        val main = getMainAttrs(item.getEquipId(stack))
        if (main == null) {
            return 0.0
        }

        return when (item.getEquipTypeId(stack)) {
            0, 1, 2, 3 -> main[1] + main[3] * 0.5f + main[8] * 0.05f
            4, 5 -> main[2] + main[14] * 0.15f + main[8] * 0.05f
            6, 7, 8, 9, 10, 11, 12, 13 -> main[3] + main[4] + main[13] * 0.25f + main[8] * 0.05f
            14, 15 -> main[13] + main[14] + main[8] * 0.2f + main[9] * 25.0f
            16, 17 -> main[7] * 100.0f + main[15] * 25.0f + main[17] * 10.0f
            18, 19 -> main[0] + main[5] * 100.0f + main[20] * 50.0f
            20, 21 -> main[13] + main[1] * 0.25f
            22, 23 -> main[6] * 100.0f + main[8] * 0.2f
            24 -> main[16] * 100.0f + main[19] * 100.0f
            25 -> main[8] * 0.3f + main[12] * 100.0f
            26, 27 -> main[12] * 100.0f + main[8] * 0.2f
            28, 29 -> main[1] + main[3] + main[2] + main[4] + main[13] * 0.2f
            else -> 0.0
        }
    }

    fun addShipTankVariants(output: CreativeModeTab.Output) {
        val resolved = SHIP_TANK.get()
        if (resolved is ShipTankItem) {
            resolved.addAllVariantsToCreativeTab(output)
            return
        }

        output.accept(resolved)
    }

    fun addCombatRationVariants(output: CreativeModeTab.Output) {
        val resolved = COMBAT_RATION.get()
        if (resolved is CombatRationItem) {
            resolved.addAllVariantsToCreativeTab(output)
            return
        }

        output.accept(resolved)
    }

    fun addGrudgeVariants(output: CreativeModeTab.Output) {
        val resolved = GRUDGE.get()
        if (resolved is GrudgeItem) {
            resolved.addAllVariantsToCreativeTab(output)
            return
        }

        output.accept(resolved)
    }

    fun addAbyssNuggetVariants(output: CreativeModeTab.Output) {
        val resolved = ABYSS_NUGGET.get()
        if (resolved is AbyssNuggetItem) {
            resolved.addAllVariantsToCreativeTab(output)
            return
        }

        output.accept(resolved)
    }

    fun addPointerVariants(output: CreativeModeTab.Output) {
        val resolved = POINTER_ITEM.get()
        if (resolved is PointerItem) {
            resolved.addAllVariantsToCreativeTab(output)
            return
        }

        output.accept(resolved)
    }
}
