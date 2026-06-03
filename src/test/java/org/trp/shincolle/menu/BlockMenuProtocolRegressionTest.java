package org.trp.shincolle.menu;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockMenuProtocolRegressionTest {
    private static final Path MOD_MENUS =
            Path.of("src/main/java/org/trp/shincolle/menu/ModMenus.java");

    private static final List<MenuContract> MENU_CONTRACTS = List.of(
            new MenuContract(
                    "crane_menu",
                    Path.of("src/main/java/org/trp/shincolle/menu/CraneMenu.java"),
                    "CraneBlockEntity",
                    "Missing crane menu data.",
                    "Crane block entity not found."
            ),
            new MenuContract(
                    "small_shipyard_menu",
                    Path.of("src/main/java/org/trp/shincolle/menu/SmallShipyardMenu.java"),
                    "SmallShipyardBlockEntity",
                    "Missing small shipyard menu data.",
                    "Small shipyard block entity not found."
            ),
            new MenuContract(
                    "large_shipyard_menu",
                    Path.of("src/main/java/org/trp/shincolle/menu/LargeShipyardMenu.java"),
                    "LargeShipyardBlockEntity",
                    "Missing large shipyard menu data.",
                    "Large shipyard block entity not found."
            ),
            new MenuContract(
                    "vol_core_menu",
                    Path.of("src/main/java/org/trp/shincolle/menu/VolCoreMenu.java"),
                    "VolCoreBlockEntity",
                    "Missing VolCore menu data.",
                    "VolCore block entity not found."
            )
    );

    @Test
    void blockMenusShouldKeepRegistryFriendlyByteBufClientFactories() throws IOException {
        String modMenus = Files.readString(MOD_MENUS);

        assertTrue(modMenus.contains("IMenuTypeExtension.create(CraneMenu::new)"),
                "Crane menu should keep the RegistryFriendlyByteBuf client factory");
        assertTrue(modMenus.contains("IMenuTypeExtension.create(SmallShipyardMenu::new)"),
                "Small shipyard menu should keep the RegistryFriendlyByteBuf client factory");
        assertTrue(modMenus.contains("IMenuTypeExtension.create(LargeShipyardMenu::new)"),
                "Large shipyard menu should keep the RegistryFriendlyByteBuf client factory");
        assertTrue(modMenus.contains("IMenuTypeExtension.create(VolCoreMenu::new)"),
                "VolCore menu should keep the RegistryFriendlyByteBuf client factory");
    }

    @Test
    void blockMenusShouldKeepBlockPosDecodeAndFailFastLookupContract() throws IOException {
        for (MenuContract contract : MENU_CONTRACTS) {
            String source = Files.readString(contract.source());

            assertTrue(source.contains("RegistryFriendlyByteBuf buffer"),
                    contract.menuId() + " should keep the client constructor that receives RegistryFriendlyByteBuf");
            assertTrue(source.contains("this(containerId, playerInventory, getBlockEntity(playerInventory, buffer));"),
                    contract.menuId() + " should keep delegating through getBlockEntity(playerInventory, buffer)");
            assertTrue(source.contains("private static " + contract.blockEntityType() + " getBlockEntity(Inventory playerInventory, RegistryFriendlyByteBuf buffer)"),
                    contract.menuId() + " should keep a dedicated buffer -> block entity resolver");
            assertTrue(source.contains("BlockPos pos = buffer.readBlockPos();"),
                    contract.menuId() + " should keep decoding exactly one BlockPos from the menu payload");
            assertTrue(source.contains("playerInventory.player.level().getBlockEntity(pos) instanceof " + contract.blockEntityType()),
                    contract.menuId() + " should keep validating the expected block entity type at the decoded position");
            assertTrue(source.contains(contract.missingDataMessage()),
                    contract.menuId() + " should fail fast with a stable missing-data message");
            assertTrue(source.contains(contract.missingBlockEntityMessage()),
                    contract.menuId() + " should fail fast with a stable block-entity-missing message");
        }
    }

    @Test
    void blockMenusShouldInvalidateWhenTheirBlockEntityGetsDetached() throws IOException {
        for (MenuContract contract : MENU_CONTRACTS) {
            String source = Files.readString(contract.source());

            assertTrue(source.contains("if (this.blockEntity.getLevel() == null) {\n            return false;\n        }"),
                    contract.menuId() + " should reject detached block entities before distance checks");
            assertTrue(source.contains("if (player.level().getBlockEntity(this.blockEntity.getBlockPos()) != this.blockEntity) {\n            return false;\n        }"),
                    contract.menuId() + " should reject replaced block entities before distance checks");
        }
    }

    @Test
    void craneMenuShouldRejectStaleCraningShipEntityReferences() throws IOException {
        String source = Files.readString(Path.of("src/main/java/org/trp/shincolle/menu/CraneMenu.java"));

        assertTrue(source.contains("if (level.getEntity(id) instanceof EntityShipBase ship\n                && ship.isAlive()\n                && !ship.isRemoved()) {\n            return ship;\n        }"),
                "Crane menu should only surface live, non-removed craning ships to the client screen");
        assertTrue(source.contains("if (id <= 0) {\n            return null;\n        }"),
                "Crane menu should still treat non-positive ship ids as no active craning ship");
    }

    private record MenuContract(
            String menuId,
            Path source,
            String blockEntityType,
            String missingDataMessage,
            String missingBlockEntityMessage
    ) {
    }
}
