package org.trp.shincolle.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.selector.EntitySelector
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import org.trp.shincolle.entity.EntityShipGrudge
import org.trp.shincolle.entity.base.EntityShipBase
// import org.trp.shincolle.entity.base.EntityShipBase.isInDeadPose
import org.trp.shincolle.entity.base.ShipMovementCoordinator
import org.trp.shincolle.init.ModParticles
import org.trp.shincolle.server.PlayerStateService.adjustOwnedMarriedShipCount
import org.trp.shincolle.server.PointerInteractionService.getLookTargetResult
import org.trp.shincolle.server.ShipRegistrySavedData.Companion.get
import org.trp.shincolle.server.ShipRegistrySavedData.ShipEntry
import java.util.*
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.function.ToDoubleFunction
import kotlin.math.max
import kotlin.math.min

object ModCommands {
    var isStopShipAi: Boolean = false
        private set
    private val EMOTE_NAME_TO_ID: MutableMap<String?, Int?> = createEmoteMap()

    fun register(dispatcher: CommandDispatcher<CommandSourceStack?>) {
        dispatcher.register(
            Commands.literal("ship")
                .then(
                    Commands.literal("info")
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.showLookingShipInfo(
                                context.source
                            )
                        })
                )
                .then(
                    Commands.literal("list")
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.listRegisteredShips(
                                context.source,
                                0
                            )
                        })
                        .then(
                            Commands.argument<Int?>("page", IntegerArgumentType.integer(0))
                                .executes(Command { context: CommandContext<CommandSourceStack> ->
                                    ModCommands.listRegisteredShips(
                                        context.source,
                                        IntegerArgumentType.getInteger(context, "page")
                                    )
                                })
                        )
                )
                .then(
                    Commands.literal("emote")
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.triggerShipEmote(
                                context.source,
                                -1
                            )
                        })
                        .then(
                            Commands.argument<String?>("emote", StringArgumentType.word())
                                .executes(Command { context: CommandContext<CommandSourceStack> ->
                                    ModCommands.triggerShipEmote(
                                        context.source,
                                        parseEmoteArgument(StringArgumentType.getString(context, "emote"))
                                    )
                                })
                        )
                )
                .then(
                    Commands.literal("stopai")
                        .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.setStopAi(
                                context.source,
                                !isStopShipAi
                            )
                        })
                        .then(
                            Commands.argument<Boolean?>("enabled", BoolArgumentType.bool())
                                .executes(Command { context: CommandContext<CommandSourceStack> ->
                                    ModCommands.setStopAi(
                                        context.source,
                                        BoolArgumentType.getBool(context, "enabled")
                                    )
                                })
                        )
                )
                .then(
                    Commands.literal("get")
                        .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                        .then(
                            Commands.argument<Int?>("ship_id", IntegerArgumentType.integer(0))
                                .executes(Command { context: CommandContext<CommandSourceStack> ->
                                    ModCommands.recallRegisteredShipByListIndex(
                                        context.source,
                                        IntegerArgumentType.getInteger(context, "ship_id")
                                    )
                                })
                        )
                        .then(
                            Commands.argument<String?>("uuid", StringArgumentType.word())
                                .executes(Command { context: CommandContext<CommandSourceStack> ->
                                    ModCommands.recallRegisteredShip(
                                        context.source,
                                        ModCommands.parseUuidArgument(
                                            context.getSource()!!,
                                            StringArgumentType.getString(context, "uuid")
                                        )
                                    )
                                })
                        )
                )
                .then(
                    Commands.literal("del")
                        .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                        .then(
                            Commands.argument<Int?>("ship_id", IntegerArgumentType.integer(0))
                                .executes(Command { context: CommandContext<CommandSourceStack> ->
                                    ModCommands.deleteRegisteredShipByListIndex(
                                        context.source,
                                        IntegerArgumentType.getInteger(context, "ship_id")
                                    )
                                })
                        )
                        .then(
                            Commands.argument<String?>("uuid", StringArgumentType.word())
                                .executes(Command { context: CommandContext<CommandSourceStack> ->
                                    ModCommands.deleteRegisteredShip(
                                        context.source,
                                        ModCommands.parseUuidArgument(
                                            context.getSource()!!,
                                            StringArgumentType.getString(context, "uuid")
                                        )
                                    )
                                })
                        )
                )
                .then(
                    Commands.literal("attrs")
                        .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                        .then(shipAttrsArguments())
                )
                .then(
                    Commands.literal("tp_selected")
                        .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.teleportSelectedShips(
                                context.source
                            )
                        })
                )
                .then(
                    Commands.literal("change_owner")
                        .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                        .then(
                            Commands.argument<EntitySelector?>("player", EntityArgument.player())
                                .executes(Command { context: CommandContext<CommandSourceStack> ->
                                    ModCommands.changeTargetShipOwner(
                                        context.source,
                                        EntityArgument.getPlayer(context, "player")
                                    )
                                })
                        )
                )
                .then(
                    Commands.literal("refresh_owner_state")
                        .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.refreshNearbyOwnerState(
                                context.source,
                                128
                            )
                        })
                        .then(
                            Commands.argument<Int?>("range", IntegerArgumentType.integer(1, 1024))
                                .executes(Command { context: CommandContext<CommandSourceStack> ->
                                    ModCommands.refreshNearbyOwnerState(
                                        context.source,
                                        IntegerArgumentType.getInteger(context, "range")
                                    )
                                })
                        )
                )
                .then(
                    Commands.literal("clear_drops")
                        .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.clearNearbyGrudgeDrops(
                                context.source,
                                128
                            )
                        })
                        .then(
                            Commands.argument<Int?>("range", IntegerArgumentType.integer(1, 1024))
                                .executes(Command { context: CommandContext<CommandSourceStack> ->
                                    ModCommands.clearNearbyGrudgeDrops(
                                        context.source,
                                        IntegerArgumentType.getInteger(context, "range")
                                    )
                                })
                        )
                )
                .then(
                    Commands.literal("kill")
                        .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                        .then(
                            Commands.argument<String?>("type", StringArgumentType.word())
                                .executes(Command { context: CommandContext<CommandSourceStack> ->
                                    ModCommands.killShips(
                                        context.source,
                                        StringArgumentType.getString(context, "type"), 64
                                    )
                                })
                                .then(
                                    Commands.argument<Int?>("range", IntegerArgumentType.integer(1, 1024))
                                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                                            ModCommands.killShips(
                                                context.source,
                                                StringArgumentType.getString(context, "type"),
                                                IntegerArgumentType.getInteger(context, "range")
                                            )
                                        })
                                )
                        )
                )
        )

        dispatcher.register(
            Commands.literal("shipstopai")
                .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                .executes(Command { context: CommandContext<CommandSourceStack> ->
                    ModCommands.setStopAi(
                        context.source,
                        !isStopShipAi
                    )
                })
                .then(
                    Commands.argument<Boolean?>("enabled", BoolArgumentType.bool())
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.setStopAi(
                                context.source,
                                BoolArgumentType.getBool(context, "enabled")
                            )
                        })
                )
        )

        dispatcher.register(
            Commands.literal("shipstop")
                .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                .executes(Command { context: CommandContext<CommandSourceStack> ->
                    ModCommands.setStopAi(
                        context.source,
                        !isStopShipAi
                    )
                })
                .then(
                    Commands.argument<Boolean?>("enabled", BoolArgumentType.bool())
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.setStopAi(
                                context.source,
                                BoolArgumentType.getBool(context, "enabled")
                            )
                        })
                )
        )

        dispatcher.register(
            Commands.literal("shipinfo")
                .executes(Command { context: CommandContext<CommandSourceStack> ->
                    ModCommands.showLookingShipInfo(
                        context.source
                    )
                })
        )

        registerEmoteAlias(dispatcher, "shipemotes")
        registerEmoteAlias(dispatcher, "em")
        registerEmoteAlias(dispatcher, "emo")
        registerEmoteAlias(dispatcher, "emote")
        registerEmoteAlias(dispatcher, "emotes")

        dispatcher.register(
            Commands.literal("shipattrs")
                .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                .then(shipAttrsArguments())
        )

        dispatcher.register(
            Commands.literal("shipcleardrop")
                .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                .executes(Command { context: CommandContext<CommandSourceStack> ->
                    ModCommands.clearNearbyGrudgeDrops(
                        context.source,
                        128
                    )
                })
                .then(
                    Commands.argument<Int?>("range", IntegerArgumentType.integer(1, 1024))
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.clearNearbyGrudgeDrops(
                                context.source,
                                IntegerArgumentType.getInteger(context, "range")
                            )
                        })
                )
        )

        dispatcher.register(
            Commands.literal("shipupdateowneruid")
                .executes(Command { context: CommandContext<CommandSourceStack> ->
                    ModCommands.updateOwnerUid(
                        context.source,
                        null
                    )
                })
                .then(
                    Commands.argument<EntitySelector?>("player", EntityArgument.player())
                        .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.updateOwnerUid(
                                context.source,
                                EntityArgument.getPlayer(context, "player")
                            )
                        })
                )
                .then(
                    Commands.literal("range")
                        .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.refreshNearbyOwnerState(
                                context.source,
                                128
                            )
                        })
                        .then(
                            Commands.argument<Int?>("range", IntegerArgumentType.integer(1, 1024))
                                .executes(Command { context: CommandContext<CommandSourceStack> ->
                                    ModCommands.refreshNearbyOwnerState(
                                        context.source,
                                        IntegerArgumentType.getInteger(context, "range")
                                    )
                                })
                        )
                )
        )

        dispatcher.register(
            Commands.literal("shipchangeowner")
                .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                .then(
                    Commands.argument<EntitySelector?>("player", EntityArgument.player())
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.changeTargetShipOwner(
                                context.source,
                                EntityArgument.getPlayer(context, "player")
                            )
                        })
                )
        )

        dispatcher.register(
            Commands.literal("shipch")
                .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                .then(
                    Commands.argument<EntitySelector?>("player", EntityArgument.player())
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.changeTargetShipOwner(
                                context.source,
                                EntityArgument.getPlayer(context, "player")
                            )
                        })
                )
        )

        dispatcher.register(
            Commands.literal("shipkill")
                .requires(Predicate { obj: CommandSourceStack -> ModCommands.canUseLegacyAdminCommand(obj) })
                .then(
                    Commands.argument<Int?>("class_id", IntegerArgumentType.integer(2))
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.killShipsByLegacyClassId(
                                context.source,
                                IntegerArgumentType.getInteger(context, "class_id"),
                                64
                            )
                        })
                        .then(
                            Commands.argument<Int?>("range", IntegerArgumentType.integer(1, 1024))
                                .executes(Command { context: CommandContext<CommandSourceStack> ->
                                    ModCommands.killShipsByLegacyClassId(
                                        context.source,
                                        IntegerArgumentType.getInteger(context, "class_id"),
                                        IntegerArgumentType.getInteger(context, "range")
                                    )
                                })
                        )
                )
                .then(
                    Commands.argument<String?>("type", StringArgumentType.word())
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.killShips(
                                context.source,
                                StringArgumentType.getString(context, "type"),
                                64
                            )
                        })
                        .then(
                            Commands.argument<Int?>("range", IntegerArgumentType.integer(1, 1024))
                                .executes(Command { context: CommandContext<CommandSourceStack> ->
                                    ModCommands.killShips(
                                        context.source,
                                        StringArgumentType.getString(context, "type"),
                                        IntegerArgumentType.getInteger(context, "range")
                                    )
                                })
                        )
                )
        )
    }

    private fun shipAttrsArguments(): ArgumentBuilder<CommandSourceStack?, *> {
        val levelArg = Commands.argument<Int?>("level", IntegerArgumentType.integer(1, 150))
            .executes(Command { context: CommandContext<CommandSourceStack> ->
                ModCommands.setTargetShipAttrs(
                    context.source,
                    IntegerArgumentType.getInteger(context, "level"),
                    null, null, null, null,
                    null, null, null, null, null, null
                )
            })
        val fuelArg = Commands.argument<Int?>("fuel", IntegerArgumentType.integer(0, 30000))
            .executes(Command { context: CommandContext<CommandSourceStack> ->
                ModCommands.setTargetShipAttrs(
                    context.source,
                    IntegerArgumentType.getInteger(context, "level"),
                    IntegerArgumentType.getInteger(context, "fuel"),
                    null, null, null,
                    null, null, null, null, null, null
                )
            })
        val ammoLightArg = Commands.argument<Int?>("ammo_light", IntegerArgumentType.integer(0, 30000))
            .executes(Command { context: CommandContext<CommandSourceStack> ->
                ModCommands.setTargetShipAttrs(
                    context.source,
                    IntegerArgumentType.getInteger(context, "level"),
                    IntegerArgumentType.getInteger(context, "fuel"),
                    IntegerArgumentType.getInteger(context, "ammo_light"),
                    null, null,
                    null, null, null, null, null, null
                )
            })
        val ammoHeavyArg = Commands.argument<Int?>("ammo_heavy", IntegerArgumentType.integer(0, 30000))
            .executes(Command { context: CommandContext<CommandSourceStack> ->
                ModCommands.setTargetShipAttrs(
                    context.source,
                    IntegerArgumentType.getInteger(context, "level"),
                    IntegerArgumentType.getInteger(context, "fuel"),
                    IntegerArgumentType.getInteger(context, "ammo_light"),
                    IntegerArgumentType.getInteger(context, "ammo_heavy"),
                    null,
                    null, null, null, null, null, null
                )
            })
        val moraleArg = Commands.argument<Int?>("morale", IntegerArgumentType.integer(0, 16000))
            .executes(Command { context: CommandContext<CommandSourceStack> ->
                ModCommands.setTargetShipAttrs(
                    context.source,
                    IntegerArgumentType.getInteger(context, "level"),
                    IntegerArgumentType.getInteger(context, "fuel"),
                    IntegerArgumentType.getInteger(context, "ammo_light"),
                    IntegerArgumentType.getInteger(context, "ammo_heavy"),
                    IntegerArgumentType.getInteger(context, "morale"),
                    null, null, null, null, null, null
                )
            })

        moraleArg.then(legacyShipAttrsBonusArgumentsWithSupplies())
        ammoHeavyArg.then(moraleArg)
        ammoLightArg.then(ammoHeavyArg)
        fuelArg.then(ammoLightArg)
        levelArg.then(legacyShipAttrsBonusArguments())
        levelArg.then(fuelArg)
        return levelArg
    }

    private fun legacyShipAttrsBonusArguments(): ArgumentBuilder<CommandSourceStack?, *> {
        val hpArg = Commands.argument<Int?>("bonus_hp", IntegerArgumentType.integer(0, 100))
        val atkArg = Commands.argument<Int?>("bonus_atk", IntegerArgumentType.integer(0, 100))
        val defArg = Commands.argument<Int?>("bonus_def", IntegerArgumentType.integer(0, 100))
        val spdArg = Commands.argument<Int?>("bonus_spd", IntegerArgumentType.integer(0, 100))
        val movArg = Commands.argument<Int?>("bonus_mov", IntegerArgumentType.integer(0, 100))
        val hitArg = Commands.argument<Int?>("bonus_hit", IntegerArgumentType.integer(0, 100))
            .executes(Command { context: CommandContext<CommandSourceStack> ->
                ModCommands.setTargetShipAttrs(
                    context.source,
                    IntegerArgumentType.getInteger(context, "level"),
                    null, null, null, null,
                    IntegerArgumentType.getInteger(context, "bonus_hp"),
                    IntegerArgumentType.getInteger(context, "bonus_atk"),
                    IntegerArgumentType.getInteger(context, "bonus_def"),
                    IntegerArgumentType.getInteger(context, "bonus_spd"),
                    IntegerArgumentType.getInteger(context, "bonus_mov"),
                    IntegerArgumentType.getInteger(context, "bonus_hit")
                )
            })

        movArg.then(hitArg)
        spdArg.then(movArg)
        defArg.then(spdArg)
        atkArg.then(defArg)
        hpArg.then(atkArg)
        return hpArg
    }

    private fun legacyShipAttrsBonusArgumentsWithSupplies(): ArgumentBuilder<CommandSourceStack?, *> {
        val hpArg = Commands.argument<Int?>("bonus_hp", IntegerArgumentType.integer(0, 100))
        val atkArg = Commands.argument<Int?>("bonus_atk", IntegerArgumentType.integer(0, 100))
        val defArg = Commands.argument<Int?>("bonus_def", IntegerArgumentType.integer(0, 100))
        val spdArg = Commands.argument<Int?>("bonus_spd", IntegerArgumentType.integer(0, 100))
        val movArg = Commands.argument<Int?>("bonus_mov", IntegerArgumentType.integer(0, 100))
        val hitArg = Commands.argument<Int?>("bonus_hit", IntegerArgumentType.integer(0, 100))
            .executes(Command { context: CommandContext<CommandSourceStack> ->
                ModCommands.setTargetShipAttrs(
                    context.source,
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
                    IntegerArgumentType.getInteger(context, "bonus_hit")
                )
            })

        movArg.then(hitArg)
        spdArg.then(movArg)
        defArg.then(spdArg)
        atkArg.then(defArg)
        hpArg.then(atkArg)
        return hpArg
    }

    private fun registerEmoteAlias(dispatcher: CommandDispatcher<CommandSourceStack?>, name: String) {
        dispatcher.register(
            Commands.literal(name)
                .executes(Command { context: CommandContext<CommandSourceStack> ->
                    ModCommands.triggerSourceEmote(
                        context.source,
                        -1
                    )
                })
                .then(
                    Commands.argument<String?>("emote", StringArgumentType.word())
                        .executes(Command { context: CommandContext<CommandSourceStack> ->
                            ModCommands.triggerSourceEmote(
                                context.source,
                                parseEmoteArgument(StringArgumentType.getString(context, "emote"))
                            )
                        })
                )
        )
    }

    private fun canUseLegacyAdminCommand(source: CommandSourceStack): Boolean {
        if (source.hasPermission(2)) {
            return true
        }
        val player = source.getPlayer()
        return player != null
                && (player.isCreative() || source.getServer().isSingleplayerOwner(player.getGameProfile()))
    }

    private fun setStopAi(source: CommandSourceStack, enabled: Boolean): Int {
        isStopShipAi = enabled
        source.sendSuccess(Supplier { Component.literal("ship stopai: " + isStopShipAi) }, true)
        return if (enabled) 1 else 0
    }

    private fun showLookingShipInfo(source: CommandSourceStack): Int {
        val entity = source.getEntity()
        if (entity !is ServerPlayer) {
            source.sendFailure(Component.literal("Player only command."))
            return 0
        }

        val ship = getTargetShip(entity, 32.0, false)
        if (ship == null) {
            source.sendFailure(Component.literal("No target ship found within 32 blocks."))
            return 0
        }

        source.sendSuccess(Supplier {
            Component.literal(
                String.format(
                    "Ship %s type=%s uuid=%s owner=%s lv=%d hp=%.1f/%.1f morale=%d team=%d slot=%d married=%s hostile=%s",
                    ship.getName().getString(),
                    BuiltInRegistries.ENTITY_TYPE.getKey(ship.getType()),
                    ship.getUUID(),
                    ship.getOwnerUUID(),
                    ship.level,
                    ship.getHealth(),
                    ship.getMaxHealth(),
                    ship.morale,
                    ship.formationTeam,
                    ship.formationSlot,
                    ship.isStateMarried,
                    ship.isHostileShipMob
                )
            )
        }, false)
        return 1
    }

    private fun listRegisteredShips(source: CommandSourceStack, page: Int): Int {
        val entity = source.getEntity()
        if (entity !is ServerPlayer) {
            source.sendFailure(Component.literal("Player only command."))
            return 0
        }

        val entries: MutableList<ShipEntry> = get(entity.serverLevel()).listSorted()
        if (entries.isEmpty()) {
            source.sendFailure(Component.literal("No registered ships in the server ship registry yet."))
            return 0
        }

        val pageSize = 8
        val maxPage = max(0, (entries.size - 1) / pageSize)
        val currentPage = max(0, min(page, maxPage))
        val start = currentPage * pageSize
        val end = min(start + pageSize, entries.size)

        source.sendSuccess(Supplier {
            Component.literal(
                String.format(
                    Locale.ROOT,
                    "Ship registry page %d/%d (%d total)",
                    currentPage,
                    maxPage,
                    entries.size
                )
            )
        }, false)

        for (i in start..<end) {
            val entryIndex = i
            val entry = entries.get(i)
            val owner: String? = if (entry.ownerUuid == null) "-" else entry.ownerUuid.toString()
            val flags = ((if (entry.hostile) "hostile" else "friendly")
                    + (if (entry.married) ",married" else "")
                    + (if (entry.removed) ",removed" else ",loaded"))
            source.sendSuccess(Supplier {
                Component.literal(
                    String.format(
                        Locale.ROOT,
                        "[%d] %s uuid=%s type=%s owner=%s dim=%s pos=%d,%d,%d flags=%s",
                        entryIndex,
                        entry.displayName,
                        entry.shipUuid,
                        entry.typeId,
                        owner,
                        entry.dimension!!.location(),
                        entry.pos!!.getX(),
                        entry.pos.getY(),
                        entry.pos.getZ(),
                        flags
                    )
                )
            }, false)
        }

        return end - start
    }

    private fun recallRegisteredShip(source: CommandSourceStack, shipUuid: UUID?): Int {
        if (shipUuid == null) {
            return 0
        }
        val entity = source.getEntity()
        if (entity !is ServerPlayer) {
            source.sendFailure(Component.literal("Player only command."))
            return 0
        }

        val registry = get(entity.serverLevel())
        val entry = registry.get(shipUuid)
        if (entry == null) {
            source.sendFailure(Component.literal("Ship UUID not found in registry: " + shipUuid))
            return 0
        }

        if (entry.dimension != entity.serverLevel().dimension()) {
            source.sendFailure(Component.literal("Registered ship is in another dimension: " + entry.dimension!!.location()))
            return 0
        }

        val entityByUuid = entity.serverLevel().getEntity(shipUuid)
        if (entityByUuid !is EntityShipBase || entityByUuid.isInDeadPose) {
            source.sendFailure(Component.literal("Ship is not currently loaded in this dimension. Registry recall currently supports loaded ships only."))
            return 0
        }

        val movement = ShipMovementCoordinator(entityByUuid)
        if (!movement.teleportNearLivingIgnoringConfig(entity, 0.5)) {
            source.sendFailure(Component.literal("No safe recall position found near player."))
            return 0
        }
        entityByUuid.clearPointerTarget()
        entityByUuid.clearPointerTargetEntity()
        entityByUuid.setOrderedToSit(false)
        entityByUuid.setInSittingPose(false)
        entityByUuid.setGuardBlockTarget(entity.blockPosition())
        registry.updateShip(entityByUuid)

        source.sendSuccess(Supplier {
            Component.literal(
                "Recalled ship " + entityByUuid.getName().getString() + " (" + shipUuid + ")."
            )
        }, true)
        return 1
    }

    private fun recallRegisteredShipByListIndex(source: CommandSourceStack, shipId: Int): Int {
        val shipUuid = getShipUuidByListIndex(source, shipId)
        if (shipUuid == null) {
            return 0
        }
        return recallRegisteredShip(source, shipUuid)
    }

    private fun deleteRegisteredShip(source: CommandSourceStack, shipUuid: UUID?): Int {
        if (shipUuid == null) {
            return 0
        }
        val entity = source.getEntity()
        if (entity !is ServerPlayer) {
            source.sendFailure(Component.literal("Player only command."))
            return 0
        }

        val registry = get(entity.serverLevel())
        val entry = registry.get(shipUuid)
        if (entry == null) {
            source.sendFailure(Component.literal("Ship UUID not found in registry: " + shipUuid))
            return 0
        }

        var discardedLoadedEntity = false
        for (serverPlayer in source.getServer().getPlayerList().getPlayers()) {
            val found = serverPlayer.serverLevel().getEntity(shipUuid)
            if (found is EntityShipBase) {
                found.discard()
                discardedLoadedEntity = true
                break
            }
        }

        registry.delete(shipUuid)
        val removed = registry.get(shipUuid) == null
        if (!removed) {
            source.sendFailure(Component.literal("Failed to delete ship registry entry: " + shipUuid))
            return 0
        }

        val discarded = discardedLoadedEntity
        source.sendSuccess(Supplier {
            Component.literal(
                "Deleted ship registry entry " + shipUuid + (if (discarded) " and discarded its loaded entity." else ".")
            )
        }, true)
        return 1
    }

    private fun deleteRegisteredShipByListIndex(source: CommandSourceStack, shipId: Int): Int {
        val shipUuid = getShipUuidByListIndex(source, shipId)
        if (shipUuid == null) {
            return 0
        }
        return deleteRegisteredShip(source, shipUuid)
    }

    private fun getShipUuidByListIndex(source: CommandSourceStack, shipId: Int): UUID? {
        val entity = source.getEntity()
        if (entity !is ServerPlayer) {
            source.sendFailure(Component.literal("Player only command."))
            return null
        }

        val entries: MutableList<ShipEntry> = get(entity.serverLevel()).listSorted()
        if (shipId < 0 || shipId >= entries.size) {
            source.sendFailure(Component.literal("Ship list index not found: " + shipId + ". Use /ship list to see available ids."))
            return null
        }
        return entries.get(shipId).shipUuid
    }

    private fun parseUuidArgument(source: CommandSourceStack, raw: String): UUID? {
        try {
            return UUID.fromString(raw)
        } catch (exception: IllegalArgumentException) {
            source.sendFailure(Component.literal("Invalid UUID: " + raw))
            return null
        }
    }

    private fun teleportSelectedShips(source: CommandSourceStack): Int {
        val entity = source.getEntity()
        if (entity !is ServerPlayer) {
            source.sendFailure(Component.literal("Player only command."))
            return 0
        }

        val ships = entity.serverLevel().getEntitiesOfClass<EntityShipBase?>(
            EntityShipBase::class.java,
            entity.getBoundingBox().inflate(128.0),
            Predicate { ship: EntityShipBase? -> ship!!.isOwnedBy(entity) && ship.isPointerSelected && !ship.isInDeadPose }
        )
        if (ships.isEmpty()) {
            source.sendFailure(Component.literal("No selected ships found within 128 blocks."))
            return 0
        }

        var successCount = 0
        for (i in ships.indices) {
            val ship = ships.get(i)
            if (!ShipMovementCoordinator(ship).teleportNearLivingIgnoringConfig(entity, 0.5)) {
                continue
            }
            ship.clearPointerTarget()
            ship.clearPointerTargetEntity()
            successCount++
        }

        if (successCount <= 0) {
            source.sendFailure(Component.literal("No safe teleport positions found near player."))
            return 0
        }

        val teleportedCount = successCount
        val failedCount = ships.size - teleportedCount
        source.sendSuccess(Supplier {
            Component.literal(
                if (failedCount > 0)
                    "Teleported " + teleportedCount + " selected ships; " + failedCount + " had no safe positions."
                else
                    "Teleported " + teleportedCount + " selected ships."
            )
        }, true)
        return teleportedCount
    }

    private fun getNearestOwnedShip(player: ServerPlayer, radius: Double): EntityShipBase? {
        val box = player.getBoundingBox().inflate(radius)
        return player.serverLevel().getEntitiesOfClass<EntityShipBase?>(
            EntityShipBase::class.java,
            box,
            Predicate { ship: EntityShipBase? -> ship!!.isOwnedBy(player) && !ship.isInDeadPose })
            .stream()
            .min(Comparator.comparingDouble<EntityShipBase?>(ToDoubleFunction { ship: EntityShipBase? ->
                ship!!.distanceToSqr(
                    player
                )
            }))
            .orElse(null)
    }

    private fun getTargetShip(player: ServerPlayer, radius: Double, ownedOnly: Boolean): EntityShipBase? {
        val hitResult = getLookTargetResult(player)
        val ship = hitResult?.getEntity()
        if (ship is EntityShipBase && !ship.isInDeadPose) {
            if (!ownedOnly || ship.isOwnedBy(player)) {
                return ship
            }
        }

        return if (ownedOnly) getNearestOwnedShip(player, radius) else getNearestShip(player, radius)
    }

    private fun getNearestShip(player: ServerPlayer, radius: Double): EntityShipBase? {
        val box = player.getBoundingBox().inflate(radius)
        return player.serverLevel().getEntitiesOfClass<EntityShipBase?>(
            EntityShipBase::class.java,
            box,
            Predicate { ship: EntityShipBase? -> !ship!!.isInDeadPose })
            .stream()
            .min(Comparator.comparingDouble<EntityShipBase?>(ToDoubleFunction { ship: EntityShipBase? ->
                ship!!.distanceToSqr(
                    player
                )
            }))
            .orElse(null)
    }

    private fun changeTargetShipOwner(source: CommandSourceStack, newOwner: ServerPlayer): Int {
        val entity = source.getEntity()
        if (entity !is ServerPlayer) {
            source.sendFailure(Component.literal("Player only command."))
            return 0
        }

        val ship = getTargetShip(entity, 32.0, false)
        if (ship == null) {
            source.sendFailure(Component.literal("No target ship found within 32 blocks."))
            return 0
        }

        val oldOwner = ship.getOwnerUUID()
        if (ship.isStateMarried && oldOwner != null) {
            val oldOwnerPlayer = source.getServer().getPlayerList().getPlayer(oldOwner)
            if (oldOwnerPlayer != null) {
                adjustOwnedMarriedShipCount(oldOwnerPlayer, -1)
            }
            adjustOwnedMarriedShipCount(newOwner, 1)
        }
        ship.setOwnerUUID(newOwner.getUUID())
        ship.setTame(true, false)
        ship.setOrderedToSit(false)
        ship.setInSittingPose(false)
        ShipMovementCoordinator(ship).stopAny()
        ship.clearPointerTarget()
        ship.clearPointerTargetEntity()
        get(entity.serverLevel()).updateShip(ship)

        source.sendSuccess(Supplier {
            Component.literal(
                String.format(
                    "Changed ship owner: %s %s -> %s",
                    ship.getName().getString(),
                    oldOwner,
                    newOwner.getGameProfile().getName()
                )
            )
        }, true)
        return 1
    }

    private fun refreshNearbyOwnerState(source: CommandSourceStack, range: Int): Int {
        val entity = source.getEntity()
        if (entity !is ServerPlayer) {
            source.sendFailure(Component.literal("Player only command."))
            return 0
        }

        val ships = entity.serverLevel().getEntitiesOfClass<EntityShipBase?>(
            EntityShipBase::class.java,
            entity.getBoundingBox().inflate(range.toDouble()),
            Predicate { ship: EntityShipBase? -> ship!!.getOwnerUUID() != null && !ship.isInDeadPose }
        )

        val registry = get(entity.serverLevel())
        for (ship in ships) {
            ship.setTame(true, false)
            ShipMovementCoordinator(ship).stopAny()
            ship.clearPointerTarget()
            ship.clearPointerTargetEntity()
            if (ship.isOrderedToSit()) {
                ship.setInSittingPose(true)
            }
            registry.updateShip(ship)
        }

        val count = ships.size
        source.sendSuccess(
            Supplier { Component.literal("Refreshed tame/owner state on " + count + " loaded ships within range " + range + ".") },
            true
        )
        return count
    }

    private fun updateOwnerUid(source: CommandSourceStack, targetPlayer: ServerPlayer?): Int {
        var player = targetPlayer
        if (player == null) {
            val entity = source.getEntity()
            if (entity !is ServerPlayer) {
                source.sendFailure(Component.literal("Player only command."))
                return 0
            }
            player = entity
        }

        val ownerUuid = player.getUUID()
        var count = 0
        for (level in source.getServer().getAllLevels()) {
            val registry = get(level)
            for (entity in level.getAllEntities()) {
                if (entity !is EntityShipBase || entity.isInDeadPose) {
                    continue
                }
                if (ownerUuid != entity.getOwnerUUID()) {
                    continue
                }

                entity.setTame(true, false)
                ShipMovementCoordinator(entity).stopAny()
                entity.clearPointerTarget()
                entity.clearPointerTargetEntity()
                if (entity.isOrderedToSit()) {
                    entity.setInSittingPose(true)
                }
                registry.updateShip(entity)
                count++
            }
        }

        val refreshedPlayer: ServerPlayer? = player
        val refreshedCount = count
        source.sendSuccess(Supplier {
            Component.literal(
                ("shipupdateowneruid: owner "
                        + refreshedPlayer!!.getGameProfile().getName()
                        + " " + ownerUuid
                        + ", refreshed " + refreshedCount + " loaded ships.")
            )
        }, true)
        return count
    }

    private fun clearNearbyGrudgeDrops(source: CommandSourceStack, range: Int): Int {
        val entity = source.getEntity()
        if (entity !is ServerPlayer) {
            source.sendFailure(Component.literal("Player only command."))
            return 0
        }

        val box = entity.getBoundingBox().inflate(range.toDouble(), 256.0, range.toDouble())
        val drops = entity.serverLevel().getEntitiesOfClass<EntityShipGrudge?>(EntityShipGrudge::class.java, box)
        for (drop in drops) {
            drop.discard()
        }

        val count = drops.size
        source.sendSuccess(
            Supplier { Component.literal("Removed " + count + " ship grudge drops within range " + range + ".") },
            true
        )
        return count
    }

    private fun killShips(source: CommandSourceStack, typeFilter: String, range: Int): Int {
        val entity = source.getEntity()
        if (entity !is ServerPlayer) {
            source.sendFailure(Component.literal("Player only command."))
            return 0
        }

        val filter = typeFilter.lowercase()
        val box = entity.getBoundingBox().inflate(range.toDouble(), 256.0, range.toDouble())
        val ships = entity.serverLevel().getEntitiesOfClass<EntityShipBase?>(
            EntityShipBase::class.java,
            box,
            Predicate { ship: EntityShipBase? ->
                !ship!!.isInDeadPose && ModCommands.matchesShipFilter(
                    ship,
                    filter
                )
            })

        for (ship in ships) {
            ship.hurt(entity.damageSources().fellOutOfWorld(), Float.MAX_VALUE)
        }

        val count = ships.size
        source.sendSuccess(
            Supplier { Component.literal("Removed " + count + " ships matching '" + typeFilter + "' within range " + range + ".") },
            true
        )
        return count
    }

    private fun killShipsByLegacyClassId(source: CommandSourceStack, classId: Int, range: Int): Int {
        val entity = source.getEntity()
        if (entity !is ServerPlayer) {
            source.sendFailure(Component.literal("Player only command."))
            return 0
        }

        var legacyClass = classId - 2
        val targetHostile = legacyClass >= 2000
        if (targetHostile) {
            legacyClass -= 2000
        }
        val targetClass = legacyClass

        val box = entity.getBoundingBox().inflate(range.toDouble(), 256.0, range.toDouble())
        val ships = entity.serverLevel().getEntitiesOfClass<EntityShipBase?>(
            EntityShipBase::class.java,
            box,
            Predicate { ship: EntityShipBase? ->
                !ship!!.isInDeadPose && ship.isHostileShipMob == targetHostile && ship.getStateMinor(EntityShipBase.STATE_MINOR_SHIP_CLASS) == targetClass
            })

        for (ship in ships) {
            ship.hurt(entity.damageSources().fellOutOfWorld(), Float.MAX_VALUE)
        }

        val count = ships.size
        source.sendSuccess(Supplier {
            Component.literal(
                ("Removed " + count + " ships with legacy class id "
                        + classId + " within range " + range + ".")
            )
        }, true)
        return count
    }

    private fun matchesShipFilter(ship: EntityShipBase, filter: String): Boolean {
        if ("all" == filter) {
            return true
        }

        if ("friendly" == filter || "tame" == filter) {
            return ship.isTame && !ship.isHostileShipMob
        }

        if ("hostile" == filter || "mob" == filter) {
            return ship.isHostileShipMob
        }

        val typeId = BuiltInRegistries.ENTITY_TYPE.getKey(ship.getType()).toString().lowercase()
        val path = BuiltInRegistries.ENTITY_TYPE.getKey(ship.getType()).getPath().lowercase()
        return typeId == filter || path == filter || path.contains(filter)
    }

    private fun triggerShipEmote(source: CommandSourceStack, emoteId: Int): Int {
        val entity = source.getEntity()
        if (entity !is ServerPlayer) {
            source.sendFailure(Component.literal("Player only command."))
            return 0
        }

        val ship = getTargetShip(entity, 32.0, false)
        if (ship == null) {
            source.sendFailure(Component.literal("No target ship found within 32 blocks."))
            return 0
        }

        val resolved = if (emoteId >= 0) emoteId else entity.serverLevel().random.nextInt(35)
        ship.applyParticleEmotion(resolved)
        source.sendSuccess(Supplier {
            Component.literal(
                "Triggered emote " + resolved + " on " + ship.getName().getString() + "."
            )
        }, true)
        return 1
    }

    private fun triggerSourceEmote(source: CommandSourceStack, emoteId: Int): Int {
        if (source.getLevel() !is ServerLevel) {
            source.sendFailure(Component.literal("Server level only command."))
            return 0
        }

        val serverLevel = source.getLevel() as ServerLevel

        val resolved = if (emoteId >= 0) emoteId else serverLevel.random.nextInt(30)
        val entity = source.getEntity()
        if (entity is EntityShipBase) {
            entity.applyParticleEmotion(resolved)
        } else {
            var x = source.getPosition().x()
            var y = source.getPosition().y()
            var z = source.getPosition().z()
            var height = 0.5
            var hostId = -1
            if (entity != null) {
                height = if (entity is ServerPlayer) entity.getBbHeight() * 0.65 else entity.getBbHeight() * 0.25
                hostId = entity.getId()
                x = entity.getX()
                y = entity.getY()
                z = entity.getZ()
            }
            spawnEmotionParticle<SimpleParticleType?>(
                serverLevel,
                ModParticles.PARTICLE_EMOTION.get(),
                x,
                y,
                z,
                height,
                hostId,
                resolved
            )
        }

        source.sendSuccess(Supplier { Component.literal("Triggered emote " + resolved + ".") }, false)
        return 1
    }

    private fun <T : ParticleOptions?> spawnEmotionParticle(
        level: ServerLevel, particle: T?,
        x: Double, y: Double, z: Double,
        height: Double, hostId: Int, emotionId: Int
    ) {
        level.sendParticles<T?>(particle, x, y, z, 0, height, hostId.toDouble(), emotionId.toDouble(), 1.0)
    }

    private fun setTargetShipAttrs(
        source: CommandSourceStack,
        level: Int,
        fuel: Int?,
        ammoLight: Int?,
        ammoHeavy: Int?,
        morale: Int?,
        bonusHp: Int?,
        bonusAtk: Int?,
        bonusDef: Int?,
        bonusSpd: Int?,
        bonusMov: Int?,
        bonusHit: Int?
    ): Int {
        val entity = source.getEntity()
        if (entity !is ServerPlayer) {
            source.sendFailure(Component.literal("Player only command."))
            return 0
        }

        val ship = getTargetShip(entity, 32.0, false)
        if (ship == null) {
            source.sendFailure(Component.literal("No target ship found within 32 blocks."))
            return 0
        }

        ship.level = level
        ship.exp = 0
        ship.setHealth(ship.getMaxHealth())
        if (fuel != null) {
            ship.fuel = fuel
        }
        if (ammoLight != null) {
            ship.ammoLight = ammoLight
        }
        if (ammoHeavy != null) {
            ship.ammoHeavy = ammoHeavy
        }
        if (morale != null) {
            ship.morale = morale
        }
        if (bonusHp != null) {
            ship.setAttrBonus(0, bonusHp)
        }
        if (bonusAtk != null) {
            ship.setAttrBonus(1, bonusAtk)
        }
        if (bonusDef != null) {
            ship.setAttrBonus(2, bonusDef)
        }
        if (bonusSpd != null) {
            ship.setAttrBonus(3, bonusSpd)
        }
        if (bonusMov != null) {
            ship.setAttrBonus(4, bonusMov)
        }
        if (bonusHit != null) {
            ship.setAttrBonus(5, bonusHit)
        }

        source.sendSuccess(Supplier {
            Component.literal(
                String.format(
                    Locale.ROOT,
                    "Updated ship attrs: %s lv=%d fuel=%d ammoLight=%d ammoHeavy=%d morale=%d bonus=[%d,%d,%d,%d,%d,%d]",
                    ship.getName().getString(),
                    ship.level,
                    ship.fuel,
                    ship.ammoLight,
                    ship.ammoHeavy,
                    ship.morale,
                    ship.getAttrBonus(0),
                    ship.getAttrBonus(1),
                    ship.getAttrBonus(2),
                    ship.getAttrBonus(3),
                    ship.getAttrBonus(4),
                    ship.getAttrBonus(5)
                )
            )
        }, true)
        return 1
    }

    private fun parseEmoteArgument(value: String): Int {
        val normalized = value.lowercase()
        try {
            val parsed = normalized.toInt()
            if (parsed >= 0 && parsed <= 34) {
                return parsed
            }
        } catch (ignored: NumberFormatException) {
        }

        val mapped = EMOTE_NAME_TO_ID.get(normalized)
        return if (mapped != null) mapped else -1
    }

    private fun createEmoteMap(): MutableMap<String?, Int?> {
        val map: MutableMap<String?, Int?> = HashMap<String?, Int?>()
        addEmoteAliases(map, 0, "0", "swt", "drop")
        addEmoteAliases(map, 1, "1", "lv", "love", "heart")
        addEmoteAliases(map, 2, "2", "swt2", "wah", "panic")
        addEmoteAliases(map, 3, "3", "?")
        addEmoteAliases(map, 4, "4", "!")
        addEmoteAliases(map, 5, "5", "...")
        addEmoteAliases(map, 6, "6", "an", "anger", "angry")
        addEmoteAliases(map, 7, "7", "note", "ho")
        addEmoteAliases(map, 8, "8", "sob", "cry", "sad")
        addEmoteAliases(map, 9, "9", "spit", "rice", "hungry")
        addEmoteAliases(map, 10, "10", "spin", "dizzy")
        addEmoteAliases(map, 11, "11", "find", "??")
        addEmoteAliases(map, 12, "12", "omg", "shock")
        addEmoteAliases(map, 13, "13", "ok", "nod")
        addEmoteAliases(map, 14, "14", "fsh", "flash", "+_+")
        addEmoteAliases(map, 15, "15", "kiss", "kis")
        addEmoteAliases(map, 16, "16", "lol", "ha", "heh")
        addEmoteAliases(map, 17, "17", "gg", "giggle")
        addEmoteAliases(map, 18, "18", "sigh")
        addEmoteAliases(map, 19, "19", "meh", "lick")
        addEmoteAliases(map, 20, "20", "orz", "otl")
        addEmoteAliases(map, 21, "21", "o", "oh", "yes")
        addEmoteAliases(map, 22, "22", "x", "no")
        addEmoteAliases(map, 23, "23", "!?", "surprised")
        addEmoteAliases(map, 24, "24", "rock", "bawi")
        addEmoteAliases(map, 25, "25", "paper", "bo")
        addEmoteAliases(map, 26, "26", "scissors", "gawi", "ya", "yeah")
        addEmoteAliases(map, 27, "27", "-w-")
        addEmoteAliases(map, 28, "28", "-o-")
        addEmoteAliases(map, 29, "29", "blink", "wink")
        addEmoteAliases(map, 30, "30", "pif")
        addEmoteAliases(map, 31, "31", "shy", "shine")
        addEmoteAliases(map, 32, "32", "hmm")
        addEmoteAliases(map, 33, "33", ":p")
        addEmoteAliases(map, 34, "34", "lll")
        return map
    }

    private fun addEmoteAliases(map: MutableMap<String?, Int?>, id: Int, vararg aliases: String) {
        for (alias in aliases) {
            map.put(alias.lowercase(), id)
        }
    }
}
