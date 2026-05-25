package org.trp.shincolle.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import org.trp.shincolle.entity.EntityShipGrudge;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.event.ModEventBusEvents;
import org.trp.shincolle.init.ModDataAttachments;
import org.trp.shincolle.server.ShipRegistrySavedData;
import org.trp.shincolle.utility.ShipTeleportHelper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
public final class ModCommands {
    private static boolean stopShipAi;
    private static final Map<String, Integer> EMOTE_NAME_TO_ID = createEmoteMap();

    private ModCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ship")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("stopai")
                        .executes(context -> setStopAi(context.getSource(), !stopShipAi))
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(context -> setStopAi(context.getSource(), BoolArgumentType.getBool(context, "enabled")))))
                .then(Commands.literal("info")
                        .executes(context -> showLookingShipInfo(context.getSource())))
                .then(Commands.literal("list")
                        .executes(context -> listRegisteredShips(context.getSource(), 0))
                        .then(Commands.argument("page", IntegerArgumentType.integer(0))
                                .executes(context -> listRegisteredShips(
                                        context.getSource(),
                                        IntegerArgumentType.getInteger(context, "page")))))
                .then(Commands.literal("get")
                        .then(Commands.argument("uuid", StringArgumentType.word())
                                .executes(context -> recallRegisteredShip(
                                        context.getSource(),
                                        parseUuidArgument(context.getSource(), StringArgumentType.getString(context, "uuid"))))))
                .then(Commands.literal("del")
                        .then(Commands.argument("uuid", StringArgumentType.word())
                                .executes(context -> deleteRegisteredShip(
                                        context.getSource(),
                                        parseUuidArgument(context.getSource(), StringArgumentType.getString(context, "uuid"))))))
                .then(Commands.literal("emote")
                        .executes(context -> triggerShipEmote(context.getSource(), -1))
                        .then(Commands.argument("emote", StringArgumentType.word())
                                .executes(context -> triggerShipEmote(context.getSource(),
                                        parseEmoteArgument(StringArgumentType.getString(context, "emote"))))))
                .then(Commands.literal("attrs")
                        .then(Commands.argument("level", IntegerArgumentType.integer(1, 150))
                                .executes(context -> setTargetShipAttrs(
                                        context.getSource(),
                                        IntegerArgumentType.getInteger(context, "level"),
                                        null, null, null, null,
                                        null, null, null, null, null, null))
                                .then(Commands.argument("fuel", IntegerArgumentType.integer(0, 30000))
                                        .executes(context -> setTargetShipAttrs(
                                                context.getSource(),
                                                IntegerArgumentType.getInteger(context, "level"),
                                                IntegerArgumentType.getInteger(context, "fuel"),
                                                null, null, null,
                                                null, null, null, null, null, null))
                                        .then(Commands.argument("ammo_light", IntegerArgumentType.integer(0, 30000))
                                                .executes(context -> setTargetShipAttrs(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "level"),
                                                        IntegerArgumentType.getInteger(context, "fuel"),
                                                        IntegerArgumentType.getInteger(context, "ammo_light"),
                                                        null, null,
                                                        null, null, null, null, null, null))
                                                .then(Commands.argument("ammo_heavy", IntegerArgumentType.integer(0, 30000))
                                                        .executes(context -> setTargetShipAttrs(
                                                                context.getSource(),
                                                                IntegerArgumentType.getInteger(context, "level"),
                                                                IntegerArgumentType.getInteger(context, "fuel"),
                                                                IntegerArgumentType.getInteger(context, "ammo_light"),
                                                                IntegerArgumentType.getInteger(context, "ammo_heavy"),
                                                                null,
                                                                null, null, null, null, null, null))
                                                        .then(Commands.argument("morale", IntegerArgumentType.integer(0, 16000))
                                                                .executes(context -> setTargetShipAttrs(
                                                                        context.getSource(),
                                                                        IntegerArgumentType.getInteger(context, "level"),
                                                                        IntegerArgumentType.getInteger(context, "fuel"),
                                                                        IntegerArgumentType.getInteger(context, "ammo_light"),
                                                                        IntegerArgumentType.getInteger(context, "ammo_heavy"),
                                                                        IntegerArgumentType.getInteger(context, "morale"),
                                                                        null, null, null, null, null, null))
                                                                .then(Commands.argument("bonus_hp", IntegerArgumentType.integer(0, 100))
                                                                        .then(Commands.argument("bonus_atk", IntegerArgumentType.integer(0, 100))
                                                                                .then(Commands.argument("bonus_def", IntegerArgumentType.integer(0, 100))
                                                                                        .then(Commands.argument("bonus_spd", IntegerArgumentType.integer(0, 100))
                                                                                                .then(Commands.argument("bonus_mov", IntegerArgumentType.integer(0, 100))
                                                                                                        .then(Commands.argument("bonus_hit", IntegerArgumentType.integer(0, 100))
                                                                                                                .executes(context -> setTargetShipAttrs(
                                                                                                                        context.getSource(),
                                                                                                                        IntegerArgumentType.getInteger(context, "level"),
                                                                                                                        IntegerArgumentType.getInteger(context, "fuel"),
                                                                                                                        IntegerArgumentType.getInteger(context, "ammo_light"),
                                                                                                                        IntegerArgumentType.getInteger(context, "ammo_heavy"),
                                                                                                                        IntegerArgumentType.getInteger(context, "morale"),
                                                                                                                        IntegerArgumentType.getInteger(context, "bonus_hp"),
                                                                                                                        IntegerArgumentType.getInteger(context, "bonus_atk"),
                                                                                                                        IntegerArgumentType.getInteger(context, "bonus_def"),
                                                                                                                        IntegerArgumentType.getInteger(context, "bonus_spd"),
                                                                                                                        IntegerArgumentType.getInteger(context, "bonus_mov"),
                                                                                                                        IntegerArgumentType.getInteger(context, "bonus_hit")))))))))))))))
                .then(Commands.literal("tp_selected")
                        .executes(context -> teleportSelectedShips(context.getSource())))
                .then(Commands.literal("change_owner")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> changeTargetShipOwner(context.getSource(), EntityArgument.getPlayer(context, "player")))))
                .then(Commands.literal("refresh_owner_state")
                        .executes(context -> refreshNearbyOwnerState(context.getSource(), 128))
                        .then(Commands.argument("range", IntegerArgumentType.integer(1, 1024))
                                .executes(context -> refreshNearbyOwnerState(context.getSource(), IntegerArgumentType.getInteger(context, "range")))))
                .then(Commands.literal("clear_drops")
                        .executes(context -> clearNearbyGrudgeDrops(context.getSource(), 128))
                        .then(Commands.argument("range", IntegerArgumentType.integer(1, 1024))
                                .executes(context -> clearNearbyGrudgeDrops(context.getSource(), IntegerArgumentType.getInteger(context, "range")))))
                .then(Commands.literal("kill")
                        .then(Commands.argument("type", StringArgumentType.word())
                                .executes(context -> killShips(context.getSource(),
                                        StringArgumentType.getString(context, "type"), 64))
                                .then(Commands.argument("range", IntegerArgumentType.integer(1, 1024))
                                        .executes(context -> killShips(context.getSource(),
                                                StringArgumentType.getString(context, "type"),
                                                IntegerArgumentType.getInteger(context, "range")))))));

        dispatcher.register(Commands.literal("shipstopai")
                .requires(source -> source.hasPermission(2))
                .executes(context -> setStopAi(context.getSource(), !stopShipAi))
                .then(Commands.argument("enabled", BoolArgumentType.bool())
                        .executes(context -> setStopAi(context.getSource(), BoolArgumentType.getBool(context, "enabled")))));

        dispatcher.register(Commands.literal("shipstop")
                .requires(source -> source.hasPermission(2))
                .executes(context -> setStopAi(context.getSource(), !stopShipAi))
                .then(Commands.argument("enabled", BoolArgumentType.bool())
                        .executes(context -> setStopAi(context.getSource(), BoolArgumentType.getBool(context, "enabled")))));

        dispatcher.register(Commands.literal("shipinfo")
                .requires(source -> source.hasPermission(2))
                .executes(context -> showLookingShipInfo(context.getSource())));

        dispatcher.register(Commands.literal("shipemotes")
                .requires(source -> source.hasPermission(2))
                .executes(context -> triggerShipEmote(context.getSource(), -1))
                .then(Commands.argument("emote", StringArgumentType.word())
                        .executes(context -> triggerShipEmote(context.getSource(),
                                parseEmoteArgument(StringArgumentType.getString(context, "emote"))))));

        dispatcher.register(Commands.literal("shipattrs")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("level", IntegerArgumentType.integer(1, 150))
                        .executes(context -> setTargetShipAttrs(
                                context.getSource(),
                                IntegerArgumentType.getInteger(context, "level"),
                                null, null, null, null,
                                null, null, null, null, null, null))
                        .then(Commands.argument("fuel", IntegerArgumentType.integer(0, 30000))
                                .executes(context -> setTargetShipAttrs(
                                        context.getSource(),
                                        IntegerArgumentType.getInteger(context, "level"),
                                        IntegerArgumentType.getInteger(context, "fuel"),
                                        null, null, null,
                                        null, null, null, null, null, null))
                                .then(Commands.argument("ammo_light", IntegerArgumentType.integer(0, 30000))
                                        .executes(context -> setTargetShipAttrs(
                                                context.getSource(),
                                                IntegerArgumentType.getInteger(context, "level"),
                                                IntegerArgumentType.getInteger(context, "fuel"),
                                                IntegerArgumentType.getInteger(context, "ammo_light"),
                                                null, null,
                                                null, null, null, null, null, null))
                                        .then(Commands.argument("ammo_heavy", IntegerArgumentType.integer(0, 30000))
                                                .executes(context -> setTargetShipAttrs(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "level"),
                                                        IntegerArgumentType.getInteger(context, "fuel"),
                                                        IntegerArgumentType.getInteger(context, "ammo_light"),
                                                        IntegerArgumentType.getInteger(context, "ammo_heavy"),
                                                        null,
                                                        null, null, null, null, null, null))
                                                .then(Commands.argument("morale", IntegerArgumentType.integer(0, 16000))
                                                        .executes(context -> setTargetShipAttrs(
                                                                context.getSource(),
                                                                IntegerArgumentType.getInteger(context, "level"),
                                                                IntegerArgumentType.getInteger(context, "fuel"),
                                                                IntegerArgumentType.getInteger(context, "ammo_light"),
                                                                IntegerArgumentType.getInteger(context, "ammo_heavy"),
                                                                IntegerArgumentType.getInteger(context, "morale"),
                                                                null, null, null, null, null, null))
                                                        .then(Commands.argument("bonus_hp", IntegerArgumentType.integer(0, 100))
                                                                .then(Commands.argument("bonus_atk", IntegerArgumentType.integer(0, 100))
                                                                        .then(Commands.argument("bonus_def", IntegerArgumentType.integer(0, 100))
                                                                                .then(Commands.argument("bonus_spd", IntegerArgumentType.integer(0, 100))
                                                                                        .then(Commands.argument("bonus_mov", IntegerArgumentType.integer(0, 100))
                                                                                                .then(Commands.argument("bonus_hit", IntegerArgumentType.integer(0, 100))
                                                                                                        .executes(context -> setTargetShipAttrs(
                                                                                                                context.getSource(),
                                                                                                                IntegerArgumentType.getInteger(context, "level"),
                                                                                                                IntegerArgumentType.getInteger(context, "fuel"),
                                                                                                                IntegerArgumentType.getInteger(context, "ammo_light"),
                                                                                                                IntegerArgumentType.getInteger(context, "ammo_heavy"),
                                                                                                                IntegerArgumentType.getInteger(context, "morale"),
                                                                                                                IntegerArgumentType.getInteger(context, "bonus_hp"),
                                                                                                                IntegerArgumentType.getInteger(context, "bonus_atk"),
                                                                                                                IntegerArgumentType.getInteger(context, "bonus_def"),
                                                                                                                IntegerArgumentType.getInteger(context, "bonus_spd"),
                                                                                                                IntegerArgumentType.getInteger(context, "bonus_mov"),
                                                                                                                IntegerArgumentType.getInteger(context, "bonus_hit")))))))))))))));

        dispatcher.register(Commands.literal("shipcleardrop")
                .requires(source -> source.hasPermission(2))
                .executes(context -> clearNearbyGrudgeDrops(context.getSource(), 128))
                .then(Commands.argument("range", IntegerArgumentType.integer(1, 1024))
                        .executes(context -> clearNearbyGrudgeDrops(context.getSource(), IntegerArgumentType.getInteger(context, "range")))));

        dispatcher.register(Commands.literal("shipupdateowneruid")
                .requires(source -> source.hasPermission(2))
                .executes(context -> refreshNearbyOwnerState(context.getSource(), 128))
                .then(Commands.argument("range", IntegerArgumentType.integer(1, 1024))
                        .executes(context -> refreshNearbyOwnerState(context.getSource(), IntegerArgumentType.getInteger(context, "range")))));

        dispatcher.register(Commands.literal("shipchangeowner")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> changeTargetShipOwner(context.getSource(), EntityArgument.getPlayer(context, "player")))));

        dispatcher.register(Commands.literal("shipkill")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("type", StringArgumentType.word())
                        .executes(context -> killShips(context.getSource(), StringArgumentType.getString(context, "type"), 64))
                        .then(Commands.argument("range", IntegerArgumentType.integer(1, 1024))
                                .executes(context -> killShips(context.getSource(),
                                        StringArgumentType.getString(context, "type"),
                                        IntegerArgumentType.getInteger(context, "range"))))));
    }

    public static boolean isStopShipAi() {
        return stopShipAi;
    }

    private static int setStopAi(CommandSourceStack source, boolean enabled) {
        stopShipAi = enabled;
        source.sendSuccess(() -> Component.literal("ship stopai: " + stopShipAi), true);
        return enabled ? 1 : 0;
    }

    private static int showLookingShipInfo(CommandSourceStack source) {
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Player only command."));
            return 0;
        }

        EntityShipBase ship = getTargetShip(player, 32.0D, false);
        if (ship == null) {
            source.sendFailure(Component.literal("No target ship found within 32 blocks."));
            return 0;
        }

        source.sendSuccess(() -> Component.literal(String.format(
                "Ship %s type=%s uuid=%s owner=%s lv=%d hp=%.1f/%.1f morale=%d team=%d slot=%d married=%s hostile=%s",
                ship.getName().getString(),
                BuiltInRegistries.ENTITY_TYPE.getKey(ship.getType()),
                ship.getUUID(),
                ship.getOwnerUUID(),
                ship.getLevel(),
                ship.getHealth(),
                ship.getMaxHealth(),
                ship.getMorale(),
                ship.getFormationTeam(),
                ship.getFormationSlot(),
                ship.isStateMarried(),
                ship.isHostileShipMob()
        )), false);
        return 1;
    }

    private static int listRegisteredShips(CommandSourceStack source, int page) {
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Player only command."));
            return 0;
        }

        List<ShipRegistrySavedData.ShipEntry> entries = ShipRegistrySavedData.get(player.serverLevel()).listSorted();
        if (entries.isEmpty()) {
            source.sendFailure(Component.literal("No registered ships in the server ship registry yet."));
            return 0;
        }

        final int pageSize = 8;
        int maxPage = Math.max(0, (entries.size() - 1) / pageSize);
        int currentPage = Math.max(0, Math.min(page, maxPage));
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, entries.size());

        source.sendSuccess(() -> Component.literal(String.format(
                Locale.ROOT,
                "Ship registry page %d/%d (%d total)",
                currentPage,
                maxPage,
                entries.size()
        )), false);

        for (int i = start; i < end; i++) {
            final int entryIndex = i;
            ShipRegistrySavedData.ShipEntry entry = entries.get(i);
            String owner = entry.ownerUuid() == null ? "-" : entry.ownerUuid().toString();
            String flags = (entry.hostile() ? "hostile" : "friendly")
                    + (entry.married() ? ",married" : "")
                    + (entry.removed() ? ",removed" : ",loaded");
            source.sendSuccess(() -> Component.literal(String.format(
                    Locale.ROOT,
                    "[%d] %s uuid=%s type=%s owner=%s dim=%s pos=%d,%d,%d flags=%s",
                    entryIndex,
                    entry.displayName(),
                    entry.shipUuid(),
                    entry.typeId(),
                    owner,
                    entry.dimension().location(),
                    entry.pos().getX(),
                    entry.pos().getY(),
                    entry.pos().getZ(),
                    flags
            )), false);
        }

        return end - start;
    }

    private static int recallRegisteredShip(CommandSourceStack source, UUID shipUuid) {
        if (shipUuid == null) {
            return 0;
        }
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Player only command."));
            return 0;
        }

        ShipRegistrySavedData registry = ShipRegistrySavedData.get(player.serverLevel());
        ShipRegistrySavedData.ShipEntry entry = registry.get(shipUuid);
        if (entry == null) {
            source.sendFailure(Component.literal("Ship UUID not found in registry: " + shipUuid));
            return 0;
        }

        if (!entry.dimension().equals(player.serverLevel().dimension())) {
            source.sendFailure(Component.literal("Registered ship is in another dimension: " + entry.dimension().location()));
            return 0;
        }

        Entity entityByUuid = player.serverLevel().getEntity(shipUuid);
        if (!(entityByUuid instanceof EntityShipBase ship) || ship.isInDeadPose()) {
            source.sendFailure(Component.literal("Ship is not currently loaded in this dimension. Registry recall currently supports loaded ships only."));
            return 0;
        }

        if (!ShipTeleportHelper.teleportNearLiving(ship, player, 0.5D)) {
            source.sendFailure(Component.literal("No safe recall position found near player."));
            return 0;
        }
        ship.getNavigation().stop();
        ship.clearPointerTarget();
        ship.clearPointerTargetEntity();
        ship.setOrderedToSit(false);
        ship.setInSittingPose(false);
        ship.setGuardBlockTarget(player.blockPosition());
        registry.updateShip(ship);

        source.sendSuccess(() -> Component.literal("Recalled ship " + ship.getName().getString() + " (" + shipUuid + ")."), true);
        return 1;
    }

    private static int deleteRegisteredShip(CommandSourceStack source, UUID shipUuid) {
        if (shipUuid == null) {
            return 0;
        }
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Player only command."));
            return 0;
        }

        ShipRegistrySavedData registry = ShipRegistrySavedData.get(player.serverLevel());
        ShipRegistrySavedData.ShipEntry entry = registry.get(shipUuid);
        if (entry == null) {
            source.sendFailure(Component.literal("Ship UUID not found in registry: " + shipUuid));
            return 0;
        }

        boolean discardedLoadedEntity = false;
        for (ServerPlayer serverPlayer : source.getServer().getPlayerList().getPlayers()) {
            Entity found = serverPlayer.serverLevel().getEntity(shipUuid);
            if (found instanceof EntityShipBase ship) {
                ship.discard();
                discardedLoadedEntity = true;
                break;
            }
        }

        registry.delete(shipUuid);
        boolean removed = registry.get(shipUuid) == null;
        if (!removed) {
            source.sendFailure(Component.literal("Failed to delete ship registry entry: " + shipUuid));
            return 0;
        }

        boolean discarded = discardedLoadedEntity;
        source.sendSuccess(() -> Component.literal(
                "Deleted ship registry entry " + shipUuid + (discarded ? " and discarded its loaded entity." : ".")
        ), true);
        return 1;
    }

    private static UUID parseUuidArgument(CommandSourceStack source, String raw) {
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException exception) {
            source.sendFailure(Component.literal("Invalid UUID: " + raw));
            return null;
        }
    }

    private static int teleportSelectedShips(CommandSourceStack source) {
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Player only command."));
            return 0;
        }

        List<EntityShipBase> ships = player.serverLevel().getEntitiesOfClass(
                EntityShipBase.class,
                player.getBoundingBox().inflate(128.0D),
                ship -> ship.isOwnedBy(player) && ship.isPointerSelected() && !ship.isInDeadPose()
        );
        if (ships.isEmpty()) {
            source.sendFailure(Component.literal("No selected ships found within 128 blocks."));
            return 0;
        }

        int successCount = 0;
        for (int i = 0; i < ships.size(); i++) {
            EntityShipBase ship = ships.get(i);
            if (!ShipTeleportHelper.teleportNearLiving(ship, player, 0.5D)) {
                continue;
            }
            ship.getNavigation().stop();
            ship.clearPointerTarget();
            ship.clearPointerTargetEntity();
            successCount++;
        }

        if (successCount <= 0) {
            source.sendFailure(Component.literal("No safe teleport positions found near player."));
            return 0;
        }

        final int teleportedCount = successCount;
        final int failedCount = ships.size() - teleportedCount;
        source.sendSuccess(() -> Component.literal(
                failedCount > 0
                        ? "Teleported " + teleportedCount + " selected ships; " + failedCount + " had no safe positions."
                        : "Teleported " + teleportedCount + " selected ships."
        ), true);
        return teleportedCount;
    }

    private static EntityShipBase getNearestOwnedShip(ServerPlayer player, double radius) {
        AABB box = player.getBoundingBox().inflate(radius);
        return player.serverLevel().getEntitiesOfClass(
                        EntityShipBase.class,
                        box,
                        ship -> ship.isOwnedBy(player) && !ship.isInDeadPose())
                .stream()
                .min(Comparator.comparingDouble(ship -> ship.distanceToSqr(player)))
                .orElse(null);
    }

    private static EntityShipBase getTargetShip(ServerPlayer player, double radius, boolean ownedOnly) {
        EntityHitResult hitResult = ModEventBusEvents.getLookTargetResult(player);
        if (hitResult != null && hitResult.getEntity() instanceof EntityShipBase ship && !ship.isInDeadPose()) {
            if (!ownedOnly || ship.isOwnedBy(player)) {
                return ship;
            }
        }

        return ownedOnly ? getNearestOwnedShip(player, radius) : getNearestShip(player, radius);
    }

    private static EntityShipBase getNearestShip(ServerPlayer player, double radius) {
        AABB box = player.getBoundingBox().inflate(radius);
        return player.serverLevel().getEntitiesOfClass(
                        EntityShipBase.class,
                        box,
                        ship -> !ship.isInDeadPose())
                .stream()
                .min(Comparator.comparingDouble(ship -> ship.distanceToSqr(player)))
                .orElse(null);
    }

    private static int changeTargetShipOwner(CommandSourceStack source, ServerPlayer newOwner) {
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Player only command."));
            return 0;
        }

        EntityShipBase ship = getTargetShip(player, 32.0D, false);
        if (ship == null) {
            source.sendFailure(Component.literal("No target ship found within 32 blocks."));
            return 0;
        }

        var oldOwner = ship.getOwnerUUID();
        if (ship.isStateMarried() && oldOwner != null) {
            ServerPlayer oldOwnerPlayer = source.getServer().getPlayerList().getPlayer(oldOwner);
            if (oldOwnerPlayer != null) {
                oldOwnerPlayer.getData(ModDataAttachments.ADMIRAL_DATA).addMarriedShipCount(-1);
            }
            newOwner.getData(ModDataAttachments.ADMIRAL_DATA).addMarriedShipCount(1);
        }
        ship.setOwnerUUID(newOwner.getUUID());
        ship.setTame(true, false);
        ship.setOrderedToSit(false);
        ship.setInSittingPose(false);
        ship.getNavigation().stop();
        ship.clearPointerTarget();
        ship.clearPointerTargetEntity();
        ShipRegistrySavedData.get(player.serverLevel()).updateShip(ship);

        source.sendSuccess(() -> Component.literal(String.format(
                "Changed ship owner: %s %s -> %s",
                ship.getName().getString(),
                oldOwner,
                newOwner.getGameProfile().getName()
        )), true);
        return 1;
    }

    private static int refreshNearbyOwnerState(CommandSourceStack source, int range) {
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Player only command."));
            return 0;
        }

        List<EntityShipBase> ships = player.serverLevel().getEntitiesOfClass(
                EntityShipBase.class,
                player.getBoundingBox().inflate(range),
                ship -> ship.getOwnerUUID() != null && !ship.isInDeadPose()
        );

        ShipRegistrySavedData registry = ShipRegistrySavedData.get(player.serverLevel());
        for (EntityShipBase ship : ships) {
            ship.setTame(true, false);
            ship.getNavigation().stop();
            ship.clearPointerTarget();
            ship.clearPointerTargetEntity();
            if (ship.isOrderedToSit()) {
                ship.setInSittingPose(true);
            }
            registry.updateShip(ship);
        }

        int count = ships.size();
        source.sendSuccess(() -> Component.literal("Refreshed tame/owner state on " + count + " loaded ships within range " + range + "."), true);
        return count;
    }

    private static int clearNearbyGrudgeDrops(CommandSourceStack source, int range) {
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Player only command."));
            return 0;
        }

        AABB box = player.getBoundingBox().inflate(range, 256.0D, range);
        List<EntityShipGrudge> drops = player.serverLevel().getEntitiesOfClass(EntityShipGrudge.class, box);
        for (EntityShipGrudge drop : drops) {
            drop.discard();
        }

        int count = drops.size();
        source.sendSuccess(() -> Component.literal("Removed " + count + " ship grudge drops within range " + range + "."), true);
        return count;
    }

    private static int killShips(CommandSourceStack source, String typeFilter, int range) {
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Player only command."));
            return 0;
        }

        String filter = typeFilter.toLowerCase(java.util.Locale.ROOT);
        AABB box = player.getBoundingBox().inflate(range, 256.0D, range);
        List<EntityShipBase> ships = player.serverLevel().getEntitiesOfClass(EntityShipBase.class, box, ship ->
                !ship.isInDeadPose() && matchesShipFilter(ship, filter));

        for (EntityShipBase ship : ships) {
            ship.hurt(player.damageSources().fellOutOfWorld(), Float.MAX_VALUE);
        }

        int count = ships.size();
        source.sendSuccess(() -> Component.literal("Removed " + count + " ships matching '" + typeFilter + "' within range " + range + "."), true);
        return count;
    }

    private static boolean matchesShipFilter(EntityShipBase ship, String filter) {
        if ("all".equals(filter)) {
            return true;
        }

        if ("friendly".equals(filter) || "tame".equals(filter)) {
            return ship.isTame() && !ship.isHostileShipMob();
        }

        if ("hostile".equals(filter) || "mob".equals(filter)) {
            return ship.isHostileShipMob();
        }

        String typeId = BuiltInRegistries.ENTITY_TYPE.getKey(ship.getType()).toString().toLowerCase(java.util.Locale.ROOT);
        String path = BuiltInRegistries.ENTITY_TYPE.getKey(ship.getType()).getPath().toLowerCase(java.util.Locale.ROOT);
        return typeId.equals(filter) || path.equals(filter) || path.contains(filter);
    }

    private static int triggerShipEmote(CommandSourceStack source, int emoteId) {
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Player only command."));
            return 0;
        }

        EntityShipBase ship = getTargetShip(player, 32.0D, false);
        if (ship == null) {
            source.sendFailure(Component.literal("No target ship found within 32 blocks."));
            return 0;
        }

        int resolved = emoteId >= 0 ? emoteId : player.serverLevel().random.nextInt(35);
        ship.applyParticleEmotion(resolved);
        source.sendSuccess(() -> Component.literal("Triggered emote " + resolved + " on " + ship.getName().getString() + "."), true);
        return 1;
    }

    private static int setTargetShipAttrs(
            CommandSourceStack source,
            int level,
            Integer fuel,
            Integer ammoLight,
            Integer ammoHeavy,
            Integer morale,
            Integer bonusHp,
            Integer bonusAtk,
            Integer bonusDef,
            Integer bonusSpd,
            Integer bonusMov,
            Integer bonusHit
    ) {
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Player only command."));
            return 0;
        }

        EntityShipBase ship = getTargetShip(player, 32.0D, false);
        if (ship == null) {
            source.sendFailure(Component.literal("No target ship found within 32 blocks."));
            return 0;
        }

        ship.setLevel(level);
        ship.setExp(0);
        ship.setHealth(ship.getMaxHealth());
        if (fuel != null) {
            ship.setFuel(fuel);
        }
        if (ammoLight != null) {
            ship.setAmmoLight(ammoLight);
        }
        if (ammoHeavy != null) {
            ship.setAmmoHeavy(ammoHeavy);
        }
        if (morale != null) {
            ship.setMorale(morale);
        }
        if (bonusHp != null) {
            ship.setAttrBonus(0, bonusHp);
        }
        if (bonusAtk != null) {
            ship.setAttrBonus(1, bonusAtk);
        }
        if (bonusDef != null) {
            ship.setAttrBonus(2, bonusDef);
        }
        if (bonusSpd != null) {
            ship.setAttrBonus(3, bonusSpd);
        }
        if (bonusMov != null) {
            ship.setAttrBonus(4, bonusMov);
        }
        if (bonusHit != null) {
            ship.setAttrBonus(5, bonusHit);
        }

        source.sendSuccess(() -> Component.literal(String.format(
                Locale.ROOT,
                "Updated ship attrs: %s lv=%d fuel=%d ammoLight=%d ammoHeavy=%d morale=%d bonus=[%d,%d,%d,%d,%d,%d]",
                ship.getName().getString(),
                ship.getLevel(),
                ship.getFuel(),
                ship.getAmmoLight(),
                ship.getAmmoHeavy(),
                ship.getMorale(),
                ship.getAttrBonus(0),
                ship.getAttrBonus(1),
                ship.getAttrBonus(2),
                ship.getAttrBonus(3),
                ship.getAttrBonus(4),
                ship.getAttrBonus(5)
        )), true);
        return 1;
    }

    private static int parseEmoteArgument(String value) {
        String normalized = value.toLowerCase(Locale.ROOT);
        try {
            int parsed = Integer.parseInt(normalized);
            if (parsed >= 0 && parsed <= 34) {
                return parsed;
            }
        } catch (NumberFormatException ignored) {
        }

        Integer mapped = EMOTE_NAME_TO_ID.get(normalized);
        return mapped != null ? mapped : -1;
    }

    private static Map<String, Integer> createEmoteMap() {
        Map<String, Integer> map = new HashMap<>();
        addEmoteAliases(map, 0, "0", "swt", "drop");
        addEmoteAliases(map, 1, "1", "lv", "love", "heart");
        addEmoteAliases(map, 2, "2", "swt2", "wah", "panic");
        addEmoteAliases(map, 3, "3", "?");
        addEmoteAliases(map, 4, "4", "!");
        addEmoteAliases(map, 5, "5", "...");
        addEmoteAliases(map, 6, "6", "an", "anger", "angry");
        addEmoteAliases(map, 7, "7", "note", "ho");
        addEmoteAliases(map, 8, "8", "sob", "cry", "sad");
        addEmoteAliases(map, 9, "9", "spit", "rice", "hungry");
        addEmoteAliases(map, 10, "10", "spin", "dizzy");
        addEmoteAliases(map, 11, "11", "find", "??");
        addEmoteAliases(map, 12, "12", "omg", "shock");
        addEmoteAliases(map, 13, "13", "ok", "nod");
        addEmoteAliases(map, 14, "14", "fsh", "flash", "+_+");
        addEmoteAliases(map, 15, "15", "kiss", "kis");
        addEmoteAliases(map, 16, "16", "lol", "ha", "heh");
        addEmoteAliases(map, 17, "17", "gg", "giggle");
        addEmoteAliases(map, 18, "18", "sigh");
        addEmoteAliases(map, 19, "19", "meh", "lick");
        addEmoteAliases(map, 20, "20", "orz", "otl");
        addEmoteAliases(map, 21, "21", "o", "oh", "yes");
        addEmoteAliases(map, 22, "22", "x", "no");
        addEmoteAliases(map, 23, "23", "!?", "surprised");
        addEmoteAliases(map, 24, "24", "rock", "bawi");
        addEmoteAliases(map, 25, "25", "paper", "bo");
        addEmoteAliases(map, 26, "26", "scissors", "gawi", "ya", "yeah");
        addEmoteAliases(map, 27, "27", "-w-");
        addEmoteAliases(map, 28, "28", "-o-");
        addEmoteAliases(map, 29, "29", "blink", "wink");
        addEmoteAliases(map, 30, "30", "pif");
        addEmoteAliases(map, 31, "31", "shy", "shine");
        addEmoteAliases(map, 32, "32", "hmm");
        addEmoteAliases(map, 33, "33", ":p");
        addEmoteAliases(map, 34, "34", "lll");
        return map;
    }

    private static void addEmoteAliases(Map<String, Integer> map, int id, String... aliases) {
        for (String alias : aliases) {
            map.put(alias.toLowerCase(Locale.ROOT), id);
        }
    }
}
