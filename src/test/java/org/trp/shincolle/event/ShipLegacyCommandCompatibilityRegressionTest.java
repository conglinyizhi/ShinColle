package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipLegacyCommandCompatibilityRegressionTest {
    private static final Path COMMANDS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/command/ModCommands.java");
    private static final Path MOUNT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityMountBase.java");

    @Test
    void publicLegacyCommandBranchesShouldStayAvailableToNormalPlayers() throws IOException {
        String source = Files.readString(COMMANDS_SOURCE);

        assertTrue(source.contains("dispatcher.register(Commands.literal(\"ship\")\n                .then(Commands.literal(\"info\")"),
                "The /ship root should keep normal-player info/list/emote branches instead of requiring OP globally");
        assertFalse(source.contains("dispatcher.register(Commands.literal(\"ship\")\n                .requires(source -> source.hasPermission(2))"),
                "The /ship root must not hide legacy public branches behind a global OP predicate");
        assertTrue(source.contains("dispatcher.register(Commands.literal(\"shipinfo\")\n                .executes(context -> showLookingShipInfo(context.getSource())))"),
                "Legacy /shipinfo should remain usable by normal players");
        assertTrue(source.contains("registerEmoteAlias(dispatcher, \"shipemotes\");"),
                "Legacy /shipemotes command name should stay registered");
        assertTrue(source.contains("registerEmoteAlias(dispatcher, \"emote\");"),
                "Old emote aliases should stay registered");
        assertTrue(source.contains("private static boolean canUseLegacyAdminCommand(CommandSourceStack source)"),
                "Admin-only legacy command branches should use one compatibility predicate");
        assertTrue(source.contains("player.isCreative() || source.getServer().isSingleplayerOwner(player.getGameProfile())"),
                "Legacy admin predicate should preserve creative and integrated-server owner access");
    }

    @Test
    void oldManagementCommandArgumentShapesShouldRemainRegistered() throws IOException {
        String source = Files.readString(COMMANDS_SOURCE);

        assertTrue(source.contains("Commands.argument(\"ship_id\", IntegerArgumentType.integer(0))"),
                "Legacy /ship get|del numeric ship-id paths should remain registered");
        assertTrue(source.contains("recallRegisteredShipByListIndex("),
                "Legacy numeric /ship get path should resolve the id shown by /ship list");
        assertTrue(source.contains("deleteRegisteredShipByListIndex("),
                "Legacy numeric /ship del path should resolve the id shown by /ship list");
        assertTrue(source.contains("levelArg.then(legacyShipAttrsBonusArguments());"),
                "Legacy /shipattrs <level> <6 bonus> path should not require fuel/ammo/morale arguments");
        assertTrue(source.contains("dispatcher.register(Commands.literal(\"shipch\")"),
                "Legacy /shipch alias should be registered beside /shipchangeowner");
        assertTrue(source.contains("Commands.argument(\"class_id\", IntegerArgumentType.integer(2))"),
                "Legacy /shipkill <class id> numeric form should stay registered");
        assertTrue(source.contains("killShipsByLegacyClassId("),
                "Legacy numeric class-id shipkill path should have a dedicated implementation");
        assertTrue(source.contains("dispatcher.register(Commands.literal(\"shipupdateowneruid\")\n                .executes(context -> updateOwnerUid(context.getSource(), null))"),
                "No-arg /shipupdateowneruid should refresh the sender's loaded ships without OP");
        assertTrue(source.contains("Commands.argument(\"player\", EntityArgument.player())\n                        .requires(ModCommands::canUseLegacyAdminCommand)"),
                "Named-player /shipupdateowneruid should remain admin-only");
    }

    @Test
    void globalStopAiShouldCoverMountsAsWellAsShips() throws IOException {
        String source = Files.readString(MOUNT_SOURCE);

        assertTrue(source.contains("ModCommands.isStopShipAi()"),
                "Legacy /shipstopai should stop ship mounts as well as ship base entities");
    }
}
