package org.trp.shincolle.init

import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EntityType.EntityFactory
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.Level
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.trp.shincolle.Shincolle
import org.trp.shincolle.entity.*
import org.trp.shincolle.entity.projectile.EntityAbyssMissile
import org.trp.shincolle.entity.projectile.EntityProjectileBeam
import java.util.function.Supplier

object ModEntities {
    @JvmField
    val ENTITY_TYPES: DeferredRegister<EntityType<*>?> =
        DeferredRegister.create<EntityType<*>?>(Registries.ENTITY_TYPE, Shincolle.MODID)

    @JvmField
    val DESTROYER_I: DeferredHolder<EntityType<*>?, EntityType<EntityDestroyerI?>?> =
        ENTITY_TYPES.register<EntityType<EntityDestroyerI?>?>(
            "destroyer_i",
            Supplier {
                EntityType.Builder.of<EntityDestroyerI?>(EntityFactory { type: EntityType<EntityDestroyerI?>?, level: Level? ->
                    EntityDestroyerI(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("destroyer_i")
            })

    @JvmField
    val DESTROYER_RO: DeferredHolder<EntityType<*>?, EntityType<EntityDestroyerRo?>?> =
        ENTITY_TYPES.register<EntityType<EntityDestroyerRo?>?>(
            "destroyer_ro",
            Supplier {
                EntityType.Builder.of<EntityDestroyerRo?>(EntityFactory { type: EntityType<EntityDestroyerRo?>?, level: Level? ->
                    EntityDestroyerRo(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("destroyer_ro")
            })

    @JvmField
    val DESTROYER_HA: DeferredHolder<EntityType<*>?, EntityType<EntityDestroyerHa?>?> =
        ENTITY_TYPES.register<EntityType<EntityDestroyerHa?>?>(
            "destroyer_ha",
            Supplier {
                EntityType.Builder.of<EntityDestroyerHa?>(EntityFactory { type: EntityType<EntityDestroyerHa?>?, level: Level? ->
                    EntityDestroyerHa(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("destroyer_ha")
            })

    @JvmField
    val DESTROYER_NI: DeferredHolder<EntityType<*>?, EntityType<EntityDestroyerNi?>?> =
        ENTITY_TYPES.register<EntityType<EntityDestroyerNi?>?>(
            "destroyer_ni",
            Supplier {
                EntityType.Builder.of<EntityDestroyerNi?>(EntityFactory { type: EntityType<EntityDestroyerNi?>?, level: Level? ->
                    EntityDestroyerNi(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("destroyer_ni")
            })

    @JvmField
    val HEAVY_CRUISER_RI: DeferredHolder<EntityType<*>?, EntityType<EntityHeavyCruiserRi?>?> =
        ENTITY_TYPES.register<EntityType<EntityHeavyCruiserRi?>?>(
            "heavy_cruiser_ri",
            Supplier {
                EntityType.Builder.of<EntityHeavyCruiserRi?>(EntityFactory { type: EntityType<EntityHeavyCruiserRi?>?, level: Level? ->
                    EntityHeavyCruiserRi(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("heavy_cruiser_ri")
            })

    @JvmField
    val HEAVY_CRUISER_NE: DeferredHolder<EntityType<*>?, EntityType<EntityHeavyCruiserNe?>?> =
        ENTITY_TYPES.register<EntityType<EntityHeavyCruiserNe?>?>(
            "heavy_cruiser_ne",
            Supplier {
                EntityType.Builder.of<EntityHeavyCruiserNe?>(EntityFactory { type: EntityType<EntityHeavyCruiserNe?>?, level: Level? ->
                    EntityHeavyCruiserNe(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("heavy_cruiser_ne")
            })

    @JvmField
    val CARRIER_WO: DeferredHolder<EntityType<*>?, EntityType<EntityCarrierWo?>?> =
        ENTITY_TYPES.register<EntityType<EntityCarrierWo?>?>(
            "carrier_wo",
            Supplier {
                EntityType.Builder.of<EntityCarrierWo?>(EntityFactory { type: EntityType<EntityCarrierWo?>?, level: Level? ->
                    EntityCarrierWo(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("carrier_wo")
            })

    @JvmField
    val BATTLESHIP_RU: DeferredHolder<EntityType<*>?, EntityType<EntityBattleshipRu?>?> =
        ENTITY_TYPES.register<EntityType<EntityBattleshipRu?>?>(
            "battleship_ru",
            Supplier {
                EntityType.Builder.of<EntityBattleshipRu?>(EntityFactory { type: EntityType<EntityBattleshipRu?>?, level: Level? ->
                    EntityBattleshipRu(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("battleship_ru")
            })

    @JvmField
    val BATTLESHIP_TA: DeferredHolder<EntityType<*>?, EntityType<EntityBattleshipTa?>?> =
        ENTITY_TYPES.register<EntityType<EntityBattleshipTa?>?>(
            "battleship_ta",
            Supplier {
                EntityType.Builder.of<EntityBattleshipTa?>(EntityFactory { type: EntityType<EntityBattleshipTa?>?, level: Level? ->
                    EntityBattleshipTa(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("battleship_ta")
            })

    @JvmField
    val BATTLESHIP_RE: DeferredHolder<EntityType<*>?, EntityType<EntityBattleshipRe?>?> =
        ENTITY_TYPES.register<EntityType<EntityBattleshipRe?>?>(
            "battleship_re",
            Supplier {
                EntityType.Builder.of<EntityBattleshipRe?>(EntityFactory { type: EntityType<EntityBattleshipRe?>?, level: Level? ->
                    EntityBattleshipRe(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("battleship_re")
            })

    @JvmField
    val TRANSPORT_WA: DeferredHolder<EntityType<*>?, EntityType<EntityTransportWa?>?> =
        ENTITY_TYPES.register<EntityType<EntityTransportWa?>?>(
            "transport_wa",
            Supplier {
                EntityType.Builder.of<EntityTransportWa?>(EntityFactory { type: EntityType<EntityTransportWa?>?, level: Level? ->
                    EntityTransportWa(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("transport_wa")
            })

    @JvmField
    val SUBM_KA: DeferredHolder<EntityType<*>?, EntityType<EntitySubmKa?>?> =
        ENTITY_TYPES.register<EntityType<EntitySubmKa?>?>(
            "subm_ka",
            Supplier {
                EntityType.Builder.of<EntitySubmKa?>(EntityFactory { type: EntityType<EntitySubmKa?>?, level: Level? ->
                    EntitySubmKa(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("subm_ka")
            })

    @JvmField
    val SUBM_YO: DeferredHolder<EntityType<*>?, EntityType<EntitySubmYo?>?> =
        ENTITY_TYPES.register<EntityType<EntitySubmYo?>?>(
            "subm_yo",
            Supplier {
                EntityType.Builder.of<EntitySubmYo?>(EntityFactory { type: EntityType<EntitySubmYo?>?, level: Level? ->
                    EntitySubmYo(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("subm_yo")
            })

    @JvmField
    val SUBM_SO: DeferredHolder<EntityType<*>?, EntityType<EntitySubmSo?>?> =
        ENTITY_TYPES.register<EntityType<EntitySubmSo?>?>(
            "subm_so",
            Supplier {
                EntityType.Builder.of<EntitySubmSo?>(EntityFactory { type: EntityType<EntitySubmSo?>?, level: Level? ->
                    EntitySubmSo(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("subm_so")
            })

    @JvmField
    val DESTROYER_HIME: DeferredHolder<EntityType<*>?, EntityType<EntityDestroyerHime?>?> =
        ENTITY_TYPES.register<EntityType<EntityDestroyerHime?>?>(
            "destroyer_hime",
            Supplier {
                EntityType.Builder.of<EntityDestroyerHime?>(EntityFactory { type: EntityType<EntityDestroyerHime?>?, level: Level? ->
                    EntityDestroyerHime(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("destroyer_hime")
            })

    @JvmField
    val CA_HIME: DeferredHolder<EntityType<*>?, EntityType<EntityCAHime?>?> =
        ENTITY_TYPES.register<EntityType<EntityCAHime?>?>(
            "ca_hime",
            Supplier {
                EntityType.Builder.of<EntityCAHime?>(EntityFactory { type: EntityType<EntityCAHime?>?, level: Level? ->
                    EntityCAHime(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("ca_hime")
            })

    @JvmField
    val AIRFIELD_HIME: DeferredHolder<EntityType<*>?, EntityType<EntityAirfieldHime?>?> =
        ENTITY_TYPES.register<EntityType<EntityAirfieldHime?>?>(
            "airfield_hime",
            Supplier {
                EntityType.Builder.of<EntityAirfieldHime?>(EntityFactory { type: EntityType<EntityAirfieldHime?>?, level: Level? ->
                    EntityAirfieldHime(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("airfield_hime")
            })

    @JvmField
    val BATTLESHIP_HIME: DeferredHolder<EntityType<*>?, EntityType<EntityBattleshipHime?>?> =
        ENTITY_TYPES.register<EntityType<EntityBattleshipHime?>?>(
            "battleship_hime",
            Supplier {
                EntityType.Builder.of<EntityBattleshipHime?>(EntityFactory { type: EntityType<EntityBattleshipHime?>?, level: Level? ->
                    EntityBattleshipHime(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("battleship_hime")
            })

    @JvmField
    val CARRIER_HIME: DeferredHolder<EntityType<*>?, EntityType<EntityCarrierHime?>?> =
        ENTITY_TYPES.register<EntityType<EntityCarrierHime?>?>(
            "carrier_hime",
            Supplier {
                EntityType.Builder.of<EntityCarrierHime?>(EntityFactory { type: EntityType<EntityCarrierHime?>?, level: Level? ->
                    EntityCarrierHime(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("carrier_hime")
            })

    @JvmField
    val HARBOUR_HIME: DeferredHolder<EntityType<*>?, EntityType<EntityHarbourHime?>?> =
        ENTITY_TYPES.register<EntityType<EntityHarbourHime?>?>(
            "harbour_hime",
            Supplier {
                EntityType.Builder.of<EntityHarbourHime?>(EntityFactory { type: EntityType<EntityHarbourHime?>?, level: Level? ->
                    EntityHarbourHime(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("harbour_hime")
            })

    @JvmField
    val ISOLATED_HIME: DeferredHolder<EntityType<*>?, EntityType<EntityIsolatedHime?>?> =
        ENTITY_TYPES.register<EntityType<EntityIsolatedHime?>?>(
            "isolated_hime",
            Supplier {
                EntityType.Builder.of<EntityIsolatedHime?>(EntityFactory { type: EntityType<EntityIsolatedHime?>?, level: Level? ->
                    EntityIsolatedHime(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("isolated_hime")
            })

    @JvmField
    val MIDWAY_HIME: DeferredHolder<EntityType<*>?, EntityType<EntityMidwayHime?>?> =
        ENTITY_TYPES.register<EntityType<EntityMidwayHime?>?>(
            "midway_hime",
            Supplier {
                EntityType.Builder.of<EntityMidwayHime?>(EntityFactory { type: EntityType<EntityMidwayHime?>?, level: Level? ->
                    EntityMidwayHime(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("midway_hime")
            })

    @JvmField
    val NORTHERN_HIME: DeferredHolder<EntityType<*>?, EntityType<EntityNorthernHime?>?> =
        ENTITY_TYPES.register<EntityType<EntityNorthernHime?>?>(
            "northern_hime",
            Supplier {
                EntityType.Builder.of<EntityNorthernHime?>(EntityFactory { type: EntityType<EntityNorthernHime?>?, level: Level? ->
                    EntityNorthernHime(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("northern_hime")
            })

    @JvmField
    val SUBM_HIME: DeferredHolder<EntityType<*>?, EntityType<EntitySubmHime?>?> =
        ENTITY_TYPES.register<EntityType<EntitySubmHime?>?>(
            "subm_hime",
            Supplier {
                EntityType.Builder.of<EntitySubmHime?>(EntityFactory { type: EntityType<EntitySubmHime?>?, level: Level? ->
                    EntitySubmHime(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("subm_hime")
            })

    @JvmField
    val SSNH: DeferredHolder<EntityType<*>?, EntityType<EntitySSNH?>?> =
        ENTITY_TYPES.register<EntityType<EntitySSNH?>?>(
            "ssnh",
            Supplier {
                EntityType.Builder.of<EntitySSNH?>(EntityFactory { type: EntityType<EntitySSNH?>?, level: Level? ->
                    EntitySSNH(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("ssnh")
            })

    @JvmField
    val CARRIER_W_DEMON: DeferredHolder<EntityType<*>?, EntityType<EntityCarrierWDemon?>?> =
        ENTITY_TYPES.register<EntityType<EntityCarrierWDemon?>?>(
            "carrier_w_demon",
            Supplier {
                EntityType.Builder.of<EntityCarrierWDemon?>(EntityFactory { type: EntityType<EntityCarrierWDemon?>?, level: Level? ->
                    EntityCarrierWDemon(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("carrier_w_demon")
            })

    @JvmField
    val DESTROYER_AKATSUKI: DeferredHolder<EntityType<*>?, EntityType<EntityDestroyerAkatsuki?>?> =
        ENTITY_TYPES.register<EntityType<EntityDestroyerAkatsuki?>?>(
            "destroyer_akatsuki",
            Supplier {
                EntityType.Builder.of<EntityDestroyerAkatsuki?>(EntityFactory { type: EntityType<EntityDestroyerAkatsuki?>?, level: Level? ->
                    EntityDestroyerAkatsuki(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("destroyer_akatsuki")
            })

    @JvmField
    val DESTROYER_HIBIKI: DeferredHolder<EntityType<*>?, EntityType<EntityDestroyerHibiki?>?> =
        ENTITY_TYPES.register<EntityType<EntityDestroyerHibiki?>?>(
            "destroyer_hibiki",
            Supplier {
                EntityType.Builder.of<EntityDestroyerHibiki?>(EntityFactory { type: EntityType<EntityDestroyerHibiki?>?, level: Level? ->
                    EntityDestroyerHibiki(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("destroyer_hibiki")
            })

    @JvmField
    val DESTROYER_IKAZUCHI: DeferredHolder<EntityType<*>?, EntityType<EntityDestroyerIkazuchi?>?> =
        ENTITY_TYPES.register<EntityType<EntityDestroyerIkazuchi?>?>(
            "destroyer_ikazuchi",
            Supplier {
                EntityType.Builder.of<EntityDestroyerIkazuchi?>(EntityFactory { type: EntityType<EntityDestroyerIkazuchi?>?, level: Level? ->
                    EntityDestroyerIkazuchi(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("destroyer_ikazuchi")
            })

    @JvmField
    val DESTROYER_INAZUMA: DeferredHolder<EntityType<*>?, EntityType<EntityDestroyerInazuma?>?> =
        ENTITY_TYPES.register<EntityType<EntityDestroyerInazuma?>?>(
            "destroyer_inazuma",
            Supplier {
                EntityType.Builder.of<EntityDestroyerInazuma?>(EntityFactory { type: EntityType<EntityDestroyerInazuma?>?, level: Level? ->
                    EntityDestroyerInazuma(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("destroyer_inazuma")
            })

    @JvmField
    val DESTROYER_SHIMAKAZE: DeferredHolder<EntityType<*>?, EntityType<EntityDestroyerShimakaze?>?> =
        ENTITY_TYPES.register<EntityType<EntityDestroyerShimakaze?>?>(
            "destroyer_shimakaze",
            Supplier {
                EntityType.Builder.of<EntityDestroyerShimakaze?>(EntityFactory { type: EntityType<EntityDestroyerShimakaze?>?, level: Level? ->
                    EntityDestroyerShimakaze(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("destroyer_shimakaze")
            })

    @JvmField
    val CRUISER_TENRYUU: DeferredHolder<EntityType<*>?, EntityType<EntityCruiserTenryuu?>?> =
        ENTITY_TYPES.register<EntityType<EntityCruiserTenryuu?>?>(
            "cruiser_tenryuu",
            Supplier {
                EntityType.Builder.of<EntityCruiserTenryuu?>(EntityFactory { type: EntityType<EntityCruiserTenryuu?>?, level: Level? ->
                    EntityCruiserTenryuu(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("cruiser_tenryuu")
            })

    @JvmField
    val CRUISER_TATSUTA: DeferredHolder<EntityType<*>?, EntityType<EntityCruiserTatsuta?>?> =
        ENTITY_TYPES.register<EntityType<EntityCruiserTatsuta?>?>(
            "cruiser_tatsuta",
            Supplier {
                EntityType.Builder.of<EntityCruiserTatsuta?>(EntityFactory { type: EntityType<EntityCruiserTatsuta?>?, level: Level? ->
                    EntityCruiserTatsuta(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("cruiser_tatsuta")
            })

    @JvmField
    val CRUISER_TAKAO: DeferredHolder<EntityType<*>?, EntityType<EntityCruiserTakao?>?> =
        ENTITY_TYPES.register<EntityType<EntityCruiserTakao?>?>(
            "cruiser_takao",
            Supplier {
                EntityType.Builder.of<EntityCruiserTakao?>(EntityFactory { type: EntityType<EntityCruiserTakao?>?, level: Level? ->
                    EntityCruiserTakao(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("cruiser_takao")
            })

    @JvmField
    val CRUISER_ATAGO: DeferredHolder<EntityType<*>?, EntityType<EntityCruiserAtago?>?> =
        ENTITY_TYPES.register<EntityType<EntityCruiserAtago?>?>(
            "cruiser_atago",
            Supplier {
                EntityType.Builder.of<EntityCruiserAtago?>(EntityFactory { type: EntityType<EntityCruiserAtago?>?, level: Level? ->
                    EntityCruiserAtago(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("cruiser_atago")
            })

    @JvmField
    val CARRIER_KAGA: DeferredHolder<EntityType<*>?, EntityType<EntityCarrierKaga?>?> =
        ENTITY_TYPES.register<EntityType<EntityCarrierKaga?>?>(
            "carrier_kaga",
            Supplier {
                EntityType.Builder.of<EntityCarrierKaga?>(EntityFactory { type: EntityType<EntityCarrierKaga?>?, level: Level? ->
                    EntityCarrierKaga(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("carrier_kaga")
            })

    @JvmField
    val CARRIER_AKAGI: DeferredHolder<EntityType<*>?, EntityType<EntityCarrierAkagi?>?> =
        ENTITY_TYPES.register<EntityType<EntityCarrierAkagi?>?>(
            "carrier_akagi",
            Supplier {
                EntityType.Builder.of<EntityCarrierAkagi?>(EntityFactory { type: EntityType<EntityCarrierAkagi?>?, level: Level? ->
                    EntityCarrierAkagi(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("carrier_akagi")
            })

    @JvmField
    val BB_KONGOU: DeferredHolder<EntityType<*>?, EntityType<EntityBBKongou?>?> =
        ENTITY_TYPES.register<EntityType<EntityBBKongou?>?>(
            "bb_kongou",
            Supplier {
                EntityType.Builder.of<EntityBBKongou?>(EntityFactory { type: EntityType<EntityBBKongou?>?, level: Level? ->
                    EntityBBKongou(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("bb_kongou")
            })

    @JvmField
    val BB_HIEI: DeferredHolder<EntityType<*>?, EntityType<EntityBBHiei?>?> =
        ENTITY_TYPES.register<EntityType<EntityBBHiei?>?>(
            "bb_hiei",
            Supplier {
                EntityType.Builder.of<EntityBBHiei?>(EntityFactory { type: EntityType<EntityBBHiei?>?, level: Level? ->
                    EntityBBHiei(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("bb_hiei")
            })

    @JvmField
    val BB_HARUNA: DeferredHolder<EntityType<*>?, EntityType<EntityBBHaruna?>?> =
        ENTITY_TYPES.register<EntityType<EntityBBHaruna?>?>(
            "bb_haruna",
            Supplier {
                EntityType.Builder.of<EntityBBHaruna?>(EntityFactory { type: EntityType<EntityBBHaruna?>?, level: Level? ->
                    EntityBBHaruna(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("bb_haruna")
            })

    @JvmField
    val BB_KIRISHIMA: DeferredHolder<EntityType<*>?, EntityType<EntityBBKirishima?>?> =
        ENTITY_TYPES.register<EntityType<EntityBBKirishima?>?>(
            "bb_kirishima",
            Supplier {
                EntityType.Builder.of<EntityBBKirishima?>(EntityFactory { type: EntityType<EntityBBKirishima?>?, level: Level? ->
                    EntityBBKirishima(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("bb_kirishima")
            })

    @JvmField
    val BATTLESHIP_NAGATO: DeferredHolder<EntityType<*>?, EntityType<EntityBattleshipNagato?>?> =
        ENTITY_TYPES.register<EntityType<EntityBattleshipNagato?>?>(
            "battleship_nagato",
            Supplier {
                EntityType.Builder.of<EntityBattleshipNagato?>(EntityFactory { type: EntityType<EntityBattleshipNagato?>?, level: Level? ->
                    EntityBattleshipNagato(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("battleship_nagato")
            })

    @JvmField
    val BATTLESHIP_YAMATO: DeferredHolder<EntityType<*>?, EntityType<EntityBattleshipYamato?>?> =
        ENTITY_TYPES.register<EntityType<EntityBattleshipYamato?>?>(
            "battleship_yamato",
            Supplier {
                EntityType.Builder.of<EntityBattleshipYamato?>(EntityFactory { type: EntityType<EntityBattleshipYamato?>?, level: Level? ->
                    EntityBattleshipYamato(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("battleship_yamato")
            })

    @JvmField
    val SUBM_U511: DeferredHolder<EntityType<*>?, EntityType<EntitySubmU511?>?> =
        ENTITY_TYPES.register<EntityType<EntitySubmU511?>?>(
            "subm_u511",
            Supplier {
                EntityType.Builder.of<EntitySubmU511?>(EntityFactory { type: EntityType<EntitySubmU511?>?, level: Level? ->
                    EntitySubmU511(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("subm_u511")
            })

    @JvmField
    val SUBM_RO500: DeferredHolder<EntityType<*>?, EntityType<EntitySubmRo500?>?> =
        ENTITY_TYPES.register<EntityType<EntitySubmRo500?>?>(
            "subm_ro500",
            Supplier {
                EntityType.Builder.of<EntitySubmRo500?>(EntityFactory { type: EntityType<EntitySubmRo500?>?, level: Level? ->
                    EntitySubmRo500(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.4f, 1.5f)
                    .build("subm_ro500")
            })

    @JvmField
    val ABYSS_MISSILE: DeferredHolder<EntityType<*>?, EntityType<EntityAbyssMissile?>?> =
        ENTITY_TYPES.register<EntityType<EntityAbyssMissile?>?>(
            "abyss_missile",
            Supplier {
                EntityType.Builder.of<EntityAbyssMissile?>(EntityFactory { type: EntityType<EntityAbyssMissile?>?, level: Level? ->
                    EntityAbyssMissile(
                        type!!,
                        level!!
                    )
                }, MobCategory.MISC)
                    .sized(0.6f, 0.6f)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("abyss_missile")
            })

    @JvmField
    val AIRPLANE: DeferredHolder<EntityType<*>?, EntityType<EntityAirplane?>?> =
        ENTITY_TYPES.register<EntityType<EntityAirplane?>?>(
            "airplane",
            Supplier {
                EntityType.Builder.of<EntityAirplane?>(EntityFactory { type: EntityType<EntityAirplane?>?, level: Level? ->
                    EntityAirplane(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build("airplane")
            })

    @JvmField
    val AIRPLANE_T: DeferredHolder<EntityType<*>?, EntityType<EntityAirplaneT?>?> =
        ENTITY_TYPES.register<EntityType<EntityAirplaneT?>?>(
            "airplane_t",
            Supplier {
                EntityType.Builder.of<EntityAirplaneT?>(EntityFactory { type: EntityType<EntityAirplaneT?>?, level: Level? ->
                    EntityAirplaneT(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build("airplane_t")
            })

    @JvmField
    val AIRPLANE_ZERO: DeferredHolder<EntityType<*>?, EntityType<EntityAirplaneZero?>?> =
        ENTITY_TYPES.register<EntityType<EntityAirplaneZero?>?>(
            "airplane_zero",
            Supplier {
                EntityType.Builder.of<EntityAirplaneZero?>(EntityFactory { type: EntityType<EntityAirplaneZero?>?, level: Level? ->
                    EntityAirplaneZero(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build("airplane_zero")
            })

    @JvmField
    val MOUNT_AF_H: DeferredHolder<EntityType<*>?, EntityType<EntityMountAfH?>?> =
        ENTITY_TYPES.register<EntityType<EntityMountAfH?>?>(
            "mount_af_h",
            Supplier {
                EntityType.Builder.of<EntityMountAfH?>(EntityFactory { type: EntityType<EntityMountAfH?>?, level: Level? ->
                    EntityMountAfH(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(1.9f, 1.3f)
                    .build("mount_af_h")
            })

    @JvmField
    val MOUNT_BA_H: DeferredHolder<EntityType<*>?, EntityType<EntityMountBaH?>?> =
        ENTITY_TYPES.register<EntityType<EntityMountBaH?>?>(
            "mount_ba_h",
            Supplier {
                EntityType.Builder.of<EntityMountBaH?>(EntityFactory { type: EntityType<EntityMountBaH?>?, level: Level? ->
                    EntityMountBaH(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(1.9f, 3.1f)
                    .build("mount_ba_h")
            })

    @JvmField
    val MOUNT_CA_H: DeferredHolder<EntityType<*>?, EntityType<EntityMountCaH?>?> =
        ENTITY_TYPES.register<EntityType<EntityMountCaH?>?>(
            "mount_ca_h",
            Supplier {
                EntityType.Builder.of<EntityMountCaH?>(EntityFactory { type: EntityType<EntityMountCaH?>?, level: Level? ->
                    EntityMountCaH(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(1.9f, 2.1f)
                    .build("mount_ca_h")
            })

    @JvmField
    val MOUNT_CA_WD: DeferredHolder<EntityType<*>?, EntityType<EntityMountCaWD?>?> =
        ENTITY_TYPES.register<EntityType<EntityMountCaWD?>?>(
            "mount_ca_wd",
            Supplier {
                EntityType.Builder.of<EntityMountCaWD?>(EntityFactory { type: EntityType<EntityMountCaWD?>?, level: Level? ->
                    EntityMountCaWD(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(1.9f, 2.1f)
                    .build("mount_ca_wd")
            })

    @JvmField
    val MOUNT_HB_H: DeferredHolder<EntityType<*>?, EntityType<EntityMountHbH?>?> =
        ENTITY_TYPES.register<EntityType<EntityMountHbH?>?>(
            "mount_hb_h",
            Supplier {
                EntityType.Builder.of<EntityMountHbH?>(EntityFactory { type: EntityType<EntityMountHbH?>?, level: Level? ->
                    EntityMountHbH(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(1.9f, 1.6f)
                    .build("mount_hb_h")
            })

    @JvmField
    val MOUNT_IS_H: DeferredHolder<EntityType<*>?, EntityType<EntityMountIsH?>?> =
        ENTITY_TYPES.register<EntityType<EntityMountIsH?>?>(
            "mount_is_h",
            Supplier {
                EntityType.Builder.of<EntityMountIsH?>(EntityFactory { type: EntityType<EntityMountIsH?>?, level: Level? ->
                    EntityMountIsH(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(1.6f, 2.2f)
                    .build("mount_is_h")
            })

    @JvmField
    val MOUNT_MI_H: DeferredHolder<EntityType<*>?, EntityType<EntityMountMiH?>?> =
        ENTITY_TYPES.register<EntityType<EntityMountMiH?>?>(
            "mount_mi_h",
            Supplier {
                EntityType.Builder.of<EntityMountMiH?>(EntityFactory { type: EntityType<EntityMountMiH?>?, level: Level? ->
                    EntityMountMiH(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(2.5f, 2.9f)
                    .build("mount_mi_h")
            })

    @JvmField
    val MOUNT_SU_H: DeferredHolder<EntityType<*>?, EntityType<EntityMountSuH?>?> =
        ENTITY_TYPES.register<EntityType<EntityMountSuH?>?>(
            "mount_su_h",
            Supplier {
                EntityType.Builder.of<EntityMountSuH?>(EntityFactory { type: EntityType<EntityMountSuH?>?, level: Level? ->
                    EntityMountSuH(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(1.8f, 1.6f)
                    .build("mount_su_h")
            })

    @JvmField
    val PROJECTILE_BEAM: DeferredHolder<EntityType<*>?, EntityType<EntityProjectileBeam?>?> =
        ENTITY_TYPES.register<EntityType<EntityProjectileBeam?>?>(
            "projectile_beam",
            Supplier {
                EntityType.Builder.of<EntityProjectileBeam?>(EntityFactory { type: EntityType<EntityProjectileBeam?>?, level: Level? ->
                    EntityProjectileBeam(
                        type!!,
                        level!!
                    )
                }, MobCategory.MISC)
                    .sized(0.6f, 0.6f)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("projectile_beam")
            })

    @JvmField
    val RENSOUHOU: DeferredHolder<EntityType<*>?, EntityType<EntityRensouhou?>?> =
        ENTITY_TYPES.register<EntityType<EntityRensouhou?>?>(
            "rensouhou",
            Supplier {
                EntityType.Builder.of<EntityRensouhou?>(EntityFactory { type: EntityType<EntityRensouhou?>?, level: Level? ->
                    EntityRensouhou(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build("rensouhou")
            })

    @JvmField
    val RENSOUHOU_S: DeferredHolder<EntityType<*>?, EntityType<EntityRensouhouS?>?> =
        ENTITY_TYPES.register<EntityType<EntityRensouhouS?>?>(
            "rensouhou_s",
            Supplier {
                EntityType.Builder.of<EntityRensouhouS?>(EntityFactory { type: EntityType<EntityRensouhouS?>?, level: Level? ->
                    EntityRensouhouS(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build("rensouhou_s")
            })

    @JvmField
    val SHIP_GRUDGE: DeferredHolder<EntityType<*>?, EntityType<EntityShipGrudge?>?> =
        ENTITY_TYPES.register<EntityType<EntityShipGrudge?>?>(
            "ship_grudge",
            Supplier {
                EntityType.Builder.of<EntityShipGrudge?>(EntityFactory { type: EntityType<EntityShipGrudge?>?, level: Level? ->
                    EntityShipGrudge(
                        type!!,
                        level!!
                    )
                }, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(32)
                    .updateInterval(10)
                    .build("ship_grudge")
            })

    @JvmField
    val TAKOYAKI: DeferredHolder<EntityType<*>?, EntityType<EntityTakoyaki?>?> =
        ENTITY_TYPES.register<EntityType<EntityTakoyaki?>?>(
            "takoyaki",
            Supplier {
                EntityType.Builder.of<EntityTakoyaki?>(EntityFactory { type: EntityType<EntityTakoyaki?>?, level: Level? ->
                    EntityTakoyaki(
                        type,
                        level
                    )
                }, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build("takoyaki")
            })

    @JvmField
    val SHIP_FISHING_HOOK: DeferredHolder<EntityType<*>?, EntityType<EntityShipFishingHook?>?> =
        ENTITY_TYPES.register<EntityType<EntityShipFishingHook?>?>(
            "ship_fishing_hook",
            Supplier {
                EntityType.Builder.of<EntityShipFishingHook?>(EntityFactory { type: EntityType<EntityShipFishingHook?>?, level: Level? ->
                    EntityShipFishingHook(
                        type!!,
                        level!!
                    )
                }, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build("ship_fishing_hook")
            })
}
