package org.trp.shincolle.menu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.trp.shincolle.Shincolle;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Shincolle.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ShipContainerMenu>> SHIP_MENU = MENUS.register("ship_menu",
            () -> IMenuTypeExtension.create(ShipContainerMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<SmallShipyardMenu>> SMALL_SHIPYARD_MENU = MENUS.register(
        "small_shipyard_menu",
        () -> IMenuTypeExtension.create(SmallShipyardMenu::new)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<LargeShipyardMenu>> LARGE_SHIPYARD_MENU = MENUS.register(
        "large_shipyard_menu",
        () -> IMenuTypeExtension.create(LargeShipyardMenu::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<DeskMenu>> DESK_MENU = MENUS.register(
        "desk_menu",
        () -> IMenuTypeExtension.create(DeskMenu::new)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<VolCoreMenu>> VOL_CORE_MENU = MENUS.register(
            "vol_core_menu",
            () -> IMenuTypeExtension.create(VolCoreMenu::new)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<CraneMenu>> CRANE_MENU = MENUS.register(
            "crane_menu",
            () -> IMenuTypeExtension.create(CraneMenu::new)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<FormationMenu>> FORMATION = MENUS.register(
            "formation",
            () -> IMenuTypeExtension.create(FormationMenu::new)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<RecipePaperMenu>> RECIPE_PAPER_MENU = MENUS.register(
            "recipe_paper_menu",
            () -> IMenuTypeExtension.create(RecipePaperMenu::new)
    );
}
