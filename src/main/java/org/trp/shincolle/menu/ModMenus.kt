package org.trp.shincolle.menu

import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.network.IContainerFactory
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.trp.shincolle.Shincolle
import java.util.function.Supplier

object ModMenus {
    val MENUS: DeferredRegister<MenuType<*>?> = DeferredRegister.create<MenuType<*>?>(Registries.MENU, Shincolle.MODID)

    @JvmField
    val SHIP_MENU: DeferredHolder<MenuType<*>?, MenuType<ShipContainerMenu?>?> =
        MENUS.register<MenuType<ShipContainerMenu?>?>(
            "ship_menu",
            Supplier {
                IMenuTypeExtension.create<ShipContainerMenu?>(IContainerFactory { containerId: Int, playerInv: Inventory, buf: RegistryFriendlyByteBuf ->
                    ShipContainerMenu(
                        containerId,
                        playerInv,
                        buf
                    )
                })
            })

    @JvmField
    val SMALL_SHIPYARD_MENU: DeferredHolder<MenuType<*>?, MenuType<SmallShipyardMenu?>?> =
        MENUS.register<MenuType<SmallShipyardMenu?>?>(
            "small_shipyard_menu",
            Supplier {
                IMenuTypeExtension.create<SmallShipyardMenu?>(IContainerFactory { containerId: Int, playerInventory: Inventory, buffer: RegistryFriendlyByteBuf ->
                    SmallShipyardMenu(
                        containerId,
                        playerInventory,
                        buffer
                    )
                })
            }
        )

    @JvmField
    val LARGE_SHIPYARD_MENU: DeferredHolder<MenuType<*>?, MenuType<LargeShipyardMenu?>?> =
        MENUS.register<MenuType<LargeShipyardMenu?>?>(
            "large_shipyard_menu",
            Supplier {
                IMenuTypeExtension.create<LargeShipyardMenu?>(IContainerFactory { containerId: Int, playerInventory: Inventory, buffer: RegistryFriendlyByteBuf ->
                    LargeShipyardMenu(
                        containerId,
                        playerInventory,
                        buffer
                    )
                })
            }
        )
    @JvmField
    val DESK_MENU: DeferredHolder<MenuType<*>?, MenuType<DeskMenu?>?> = MENUS.register<MenuType<DeskMenu?>?>(
        "desk_menu",
        Supplier {
            IMenuTypeExtension.create<DeskMenu?>(IContainerFactory { id: Int, playerInventory: Inventory, data: RegistryFriendlyByteBuf ->
                DeskMenu(
                    id,
                    playerInventory,
                    data
                )
            })
        }
    )

    @JvmField
    val VOL_CORE_MENU: DeferredHolder<MenuType<*>?, MenuType<VolCoreMenu?>?> = MENUS.register<MenuType<VolCoreMenu?>?>(
        "vol_core_menu",
        Supplier {
            IMenuTypeExtension.create<VolCoreMenu?>(IContainerFactory { containerId: Int, playerInventory: Inventory, buffer: RegistryFriendlyByteBuf ->
                VolCoreMenu(
                    containerId,
                    playerInventory,
                    buffer
                )
            })
        }
    )

    @JvmField
    val CRANE_MENU: DeferredHolder<MenuType<*>?, MenuType<CraneMenu?>?> = MENUS.register<MenuType<CraneMenu?>?>(
        "crane_menu",
        Supplier {
            IMenuTypeExtension.create<CraneMenu?>(IContainerFactory { containerId: Int, playerInventory: Inventory, buffer: RegistryFriendlyByteBuf ->
                CraneMenu(
                    containerId,
                    playerInventory,
                    buffer
                )
            })
        }
    )

    @JvmField
    val FORMATION: DeferredHolder<MenuType<*>?, MenuType<FormationMenu?>?> = MENUS.register<MenuType<FormationMenu?>?>(
        "formation",
        Supplier {
            IMenuTypeExtension.create<FormationMenu?>(IContainerFactory { containerId: Int, playerInventory: Inventory, buffer: RegistryFriendlyByteBuf ->
                FormationMenu(
                    containerId,
                    playerInventory,
                    buffer
                )
            })
        }
    )

    @JvmField
    val RECIPE_PAPER_MENU: DeferredHolder<MenuType<*>?, MenuType<RecipePaperMenu?>?> =
        MENUS.register<MenuType<RecipePaperMenu?>?>(
            "recipe_paper_menu",
            Supplier {
                IMenuTypeExtension.create<RecipePaperMenu?>(IContainerFactory { id: Int, playerInv: Inventory, buf: RegistryFriendlyByteBuf ->
                    RecipePaperMenu(
                        id,
                        playerInv,
                        buf
                    )
                })
            }
        )
}
