package org.trp.shincolle.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerStatePersistenceArchitectureRegressionTest {
    private static final Path PLAYER_STATE_SERVICE =
            Path.of("src/main/java/org/trp/shincolle/server/PlayerStateService.kt");
    private static final Path DATA_ATTACHMENTS =
            Path.of("src/main/java/org/trp/shincolle/init/ModDataAttachments.kt");
    private static final Path EVENT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/event/ModEventBusEvents.kt");
    private static final Path NETWORK_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/network/ModNetwork.kt");
    private static final Path DESK_SERVICE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/server/DeskInteractionService.kt");
    private static final Path FORMATION_SERVICE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/server/FormationService.kt");
    private static final Path POINTER_SERVICE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/server/PointerInteractionService.kt");
    private static final Path TARGET_PROTECTION_SERVICE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/server/TargetProtectionService.kt");
    private static final Path TEAM_DIPLOMACY_SERVICE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/server/TeamDiplomacyService.kt");
    private static final Path SHIP_MENU_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/menu/ShipContainerMenu.kt");
    private static final Path DESK_MENU_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/menu/DeskMenu.kt");
    private static final Path FORMATION_MENU_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/menu/FormationMenu.kt");
    private static final Path POINTER_ITEM_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/item/PointerItem.kt");
    private static final Path POINTER_ITEM_CLIENT_HELPER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/PointerItemClientHelper.kt");
    private static final Path SHIP_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt");
    private static final Path SHIP_POINTER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBasePointer.kt");
    private static final Path BRAIN_AI_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBrainAi.kt");
    private static final Path COMMANDS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/command/ModCommands.kt");
    private static final Path SHIP_REGISTRY =
            Path.of("src/main/java/org/trp/shincolle/server/ShipRegistrySavedData.kt");
    private static final Path TEAM_DIPLOMACY =
            Path.of("src/main/java/org/trp/shincolle/server/TeamDiplomacySavedData.kt");
    private static final Path PLAYER_TARGET_LIST =
            Path.of("src/main/java/org/trp/shincolle/server/PlayerTargetListSavedData.kt");
    private static final Path UNATTACKABLE_TARGETS =
            Path.of("src/main/java/org/trp/shincolle/server/UnattackableTargetData.kt");
    private static final Path TARGET_WRENCH =
            Path.of("src/main/java/org/trp/shincolle/item/TargetWrenchItem.kt");
    private static final Path PASSIVE_COMBAT =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBasePassiveCombat.kt");

    @Test
    void playerAttachmentsShouldUseModernSerializedCopyOnDeathTypes() throws IOException {
        String source = Files.readString(DATA_ATTACHMENTS);

        assertTrue(source.contains("AttachmentType.builder(() -> new HashSet<Integer>())"),
                "Collected ship IDs should be a player attachment, not a legacy capability");
        assertTrue(source.contains("AttachmentType.builder(() -> new AdmiralData())"),
                "Admiral data should be a player attachment, not a legacy capability");
        assertTrue(source.contains(".serialize(SET_CODEC)\n                    .copyOnDeath()"),
                "Collected ship IDs should serialize and survive player death");
        assertTrue(source.contains(".serialize(ADMIRAL_CODEC)\n                    .copyOnDeath()"),
                "Admiral data should serialize and survive player death");
        assertFalse(source.contains("readSet("),
                "Collected ship serialization should use the NeoForge attachment serializer instead of old helper callbacks");
        assertFalse(source.contains("writeSet("),
                "Collected ship serialization should use the NeoForge attachment serializer instead of old helper callbacks");
    }

    @Test
    void playerStateServiceShouldOwnMutablePlayerAttachmentAccess() throws IOException {
        String service = Files.readString(PLAYER_STATE_SERVICE);

        assertTrue(service.contains("public static AdmiralData admiralData(Player player)"),
                "PlayerStateService should expose the admiral attachment access boundary");
        assertTrue(service.contains("return player.getData(ModDataAttachments.ADMIRAL_DATA);"),
                "PlayerStateService should be the business-layer owner of admiral attachment access");
        assertTrue(service.contains("return player.getData(ModDataAttachments.COLLECTED_SHIPS);"),
                "PlayerStateService should be the business-layer owner of collected ship attachment access");
        assertTrue(service.contains("public static void applyAdmiralSync(Player player, CompoundTag admiralNbt, int[] collectedShipIds)"),
                "Client sync application should be centralized");
        assertTrue(service.contains("public static void copyPersistentPlayerState(Player original, Player clone)"),
                "Death clone persistence should be centralized");
        assertTrue(service.contains("public static boolean registerCollectedShip(ServerPlayer player, int classId)"),
                "Ship collection registration should be centralized");
        assertTrue(service.contains("public static int reconcileOwnedMarriedShipCount(ServerPlayer player)"),
                "Persistent married ship counters should be reconciled server-side");
        assertTrue(service.contains("ship -> ship.isAlive()\n                        && !ship.isRemoved()\n                        && ship.isTame()"),
                "Nearby married-ship scans should ignore removed ship entities");
        assertTrue(service.contains("ship -> ship.isAlive()\n                            && !ship.isRemoved()\n                            && ship.isTame()"),
                "Server-wide married-ship reconciliation should ignore removed ship entities");
        assertTrue(service.contains("public static SlotAssignment setCurrentTeamSlot(Player player, int slotId, UUID shipUuid)"),
                "Direct slot assignment should go through the service so replacement semantics stay consistent");
    }

    @Test
    void eventNetworkAndMenuEntrypointsShouldUsePlayerStateService() throws IOException {
        Map<Path, String> sources = Map.ofEntries(
                Map.entry(EVENT_SOURCE, Files.readString(EVENT_SOURCE)),
                Map.entry(NETWORK_SOURCE, Files.readString(NETWORK_SOURCE)),
                Map.entry(DESK_SERVICE_SOURCE, Files.readString(DESK_SERVICE_SOURCE)),
                Map.entry(FORMATION_SERVICE_SOURCE, Files.readString(FORMATION_SERVICE_SOURCE)),
                Map.entry(POINTER_SERVICE_SOURCE, Files.readString(POINTER_SERVICE_SOURCE)),
                Map.entry(TEAM_DIPLOMACY_SERVICE_SOURCE, Files.readString(TEAM_DIPLOMACY_SERVICE_SOURCE)),
                Map.entry(TARGET_PROTECTION_SERVICE_SOURCE, Files.readString(TARGET_PROTECTION_SERVICE_SOURCE)),
                Map.entry(SHIP_MENU_SOURCE, Files.readString(SHIP_MENU_SOURCE)),
                Map.entry(FORMATION_MENU_SOURCE, Files.readString(FORMATION_MENU_SOURCE)),
                Map.entry(POINTER_ITEM_SOURCE, Files.readString(POINTER_ITEM_SOURCE)),
                Map.entry(POINTER_ITEM_CLIENT_HELPER_SOURCE, Files.readString(POINTER_ITEM_CLIENT_HELPER_SOURCE)),
                Map.entry(SHIP_SOURCE, Files.readString(SHIP_SOURCE)),
                Map.entry(SHIP_POINTER_SOURCE, Files.readString(SHIP_POINTER_SOURCE)),
                Map.entry(BRAIN_AI_SOURCE, Files.readString(BRAIN_AI_SOURCE))
        );

        for (Map.Entry<Path, String> entry : sources.entrySet()) {
            String source = entry.getValue();
            assertFalse(source.contains("getData(ModDataAttachments.ADMIRAL_DATA)"),
                    entry.getKey() + " should not directly read/write admiral attachments");
            assertFalse(source.contains("getData(org.trp.shincolle.init.ModDataAttachments.ADMIRAL_DATA)"),
                    entry.getKey() + " should not directly read/write admiral attachments");
            assertFalse(source.contains("getData(ModDataAttachments.COLLECTED_SHIPS)"),
                    entry.getKey() + " should not directly read/write collected ship attachments");
            assertFalse(source.contains("getData(org.trp.shincolle.init.ModDataAttachments.COLLECTED_SHIPS)"),
                    entry.getKey() + " should not directly read/write collected ship attachments");
        }

        String events = sources.get(EVENT_SOURCE);
        assertTrue(events.contains("PlayerStateService.giveInitialManualIfNeeded(serverPlayer);"),
                "Login should route initial manual persistence through the player state service");
        assertTrue(events.contains("PlayerStateService.syncAdmiralState(serverPlayer);"),
                "Login/respawn/dimension sync should route through the player state service");
        assertTrue(events.contains("PlayerStateService.copyPersistentPlayerState(event.getOriginal(), event.getEntity());"),
                "Player clone should copy persistent attachment state through the service");

        String network = sources.get(NETWORK_SOURCE);
        assertTrue(network.contains("PlayerStateService.applyAdmiralSync(player, payload.admiralNbt(), payload.collectedShips());"),
                "S2C admiral sync should apply through the service");
        assertTrue(network.contains("FormationService.handleFormationAction(player, payload.action(), payload.param1(), payload.param2(),"),
                "Network formation handler should only dispatch to the formation service");
        assertTrue(network.contains("var stack = PointerInteractionService.getPointerStack(player);"),
                "Network pointer handler should delegate pointer stack resolution to the pointer interaction service");
        assertTrue(network.contains("PointerInteractionService.handlePayloadAction(player, stack, payload.action(), payload.targetEntity(), payload.targetPos());"),
                "Network pointer handler should only dispatch to the pointer interaction service");
        assertFalse(network.contains("player.getMainHandItem()"),
                "Network pointer handler should not reimplement mainhand pointer lookup logic");
        assertFalse(network.contains("player.getOffhandItem()"),
                "Network pointer handler should not reimplement offhand pointer lookup logic");
        assertTrue(network.contains("DeskInteractionService.updateBookState(player, payload.chapter(), payload.page());"),
                "Book state writes should dispatch through the desk interaction service");
        assertTrue(network.contains("DeskInteractionService.updateDeskGui(player, payload.guiFunc(), payload.radarZoom());"),
                "Desk GUI state writes should dispatch through the desk interaction service");
        assertTrue(network.contains("DeskInteractionService.openOwnedShipFromDesk(player, payload.shipUuid());"),
                "Desk ship-open payload should dispatch through the desk interaction service");
        assertTrue(network.contains("DeskInteractionService.summonOwnedShipsToDesk(player, payload.shipUuids());"),
                "Desk summon payload should dispatch through the desk interaction service");
        assertTrue(network.contains("TeamDiplomacyService.handleAction(player, payload.action(), payload.targetUuid());"),
                "Diplomacy payload should dispatch through the diplomacy service");
        assertFalse(network.contains("S2CAdmiralDataSyncPayload.of(data.serializeNBT()"),
                "Network handlers should not hand-roll admiral sync payloads");

        String formationService = sources.get(FORMATION_SERVICE_SOURCE);
        assertTrue(formationService.contains("PlayerStateService.setCurrentTeamId(player, param1)"),
                "Current team changes should go through the player state service");
        assertTrue(formationService.contains("PlayerStateService.setCurrentTeamFormation(player, param1)"),
                "Formation changes should go through the player state service");
        assertTrue(formationService.contains("PlayerStateService.setCurrentTeamSlot(player, slotId, shipUuid)"),
                "Slot replacement should go through the player state service");
        assertTrue(formationService.contains("PlayerStateService.sendAdmiralState(serverPlayer);"),
                "Formation service should reply with service-built admiral state");

        String pointerService = sources.get(POINTER_SERVICE_SOURCE);
        assertTrue(pointerService.contains("public static ItemStack getPointerStack(Player player)"),
                "Pointer stack resolution should be centralized in the pointer interaction service");
        assertTrue(pointerService.contains("PlayerStateService.assignShipToCurrentTeam(player, ship.getUUID())"),
                "Pointer roster assignment should go through the player state service");
        assertTrue(pointerService.contains("PlayerStateService.removeShipFromTeams(player, ship.getUUID())"),
                "Pointer roster removal should go through the player state service");
        assertTrue(pointerService.contains("PlayerStateService.setCurrentTeamSlotSelected(player, existingSlot, nextState)"),
                "Pointer selection changes should go through the player state service");
        assertTrue(pointerService.contains("PlayerStateService.sendAdmiralState(serverPlayer);"),
                "Pointer service should sync admiral state after roster changes");
        assertTrue(pointerService.contains("private static void applyPointerModeSelectionState(Player player, int nextMode)"),
                "Pointer mode selection reconciliation should live in the pointer interaction service");
        assertTrue(pointerService.contains("private static void clearOwnedPointerSelection(Player player, EntityShipBase keepSelected, double radius)"),
                "Pointer selection clearing should live in the pointer interaction service");
        assertFalse(sources.get(POINTER_ITEM_SOURCE).contains("updateServerSideMode("),
                "Pointer item should not own server-side pointer mode reconciliation rules");
        assertFalse(sources.get(POINTER_ITEM_SOURCE).contains("clearOwnedPointerSelection("),
                "Pointer item should not own server-side selection clearing rules");

        assertTrue(sources.get(SHIP_MENU_SOURCE).contains("PlayerStateService.registerCollectedShip(serverPlayer, classID);"),
                "Opening ship inventory should register collection through the service");
        assertTrue(sources.get(FORMATION_MENU_SOURCE).contains("PlayerStateService.admiralData(playerInventory.player)"),
                "Formation menu should get player state through the service");
        assertTrue(sources.get(POINTER_ITEM_SOURCE).contains("PointerItemClientHelper"),
                "Pointer item should delegate client-only tooltip state to the client helper");
        assertTrue(sources.get(POINTER_ITEM_CLIENT_HELPER_SOURCE).contains("PlayerStateService.admiralData(mc.player)"),
                "Pointer item client helper should read current team through the service");
        assertTrue(sources.get(SHIP_SOURCE).contains("PlayerStateService.adjustOwnedMarriedShipCount(player, 1);"),
                "Marriage item flow should update persistent player counters through the service");
    }

    @Test
    void mutableTeamSlotWritesShouldStayInsideAdmiralDataOrPlayerStateService() throws IOException {
        Map<Path, String> sources = Map.of(
                EVENT_SOURCE, Files.readString(EVENT_SOURCE),
                NETWORK_SOURCE, Files.readString(NETWORK_SOURCE),
                SHIP_MENU_SOURCE, Files.readString(SHIP_MENU_SOURCE),
                FORMATION_MENU_SOURCE, Files.readString(FORMATION_MENU_SOURCE),
                POINTER_ITEM_SOURCE, Files.readString(POINTER_ITEM_SOURCE),
                SHIP_SOURCE, Files.readString(SHIP_SOURCE),
                SHIP_POINTER_SOURCE, Files.readString(SHIP_POINTER_SOURCE),
                BRAIN_AI_SOURCE, Files.readString(BRAIN_AI_SOURCE),
                COMMANDS_SOURCE, Files.readString(COMMANDS_SOURCE)
        );

        for (Map.Entry<Path, String> entry : sources.entrySet()) {
            String source = entry.getValue();
            assertFalse(source.contains(".setShipUUID("),
                    entry.getKey() + " should not mutate team slots outside PlayerStateService");
            assertFalse(source.contains(".setCurrentTeamID("),
                    entry.getKey() + " should not mutate current team outside PlayerStateService");
            assertFalse(source.contains(".setFormationID("),
                    entry.getKey() + " should not mutate formation IDs outside PlayerStateService");
            assertFalse(source.contains(".setTeamName("),
                    entry.getKey() + " should not mutate team names outside PlayerStateService");
            assertFalse(source.contains(".swapShips("),
                    entry.getKey() + " should not swap team slots outside PlayerStateService");
            assertFalse(source.contains(".assignShipToTeam("),
                    entry.getKey() + " should not assign team slots outside PlayerStateService");
            assertFalse(source.contains(".removeShip("),
                    entry.getKey() + " should not remove team slots outside PlayerStateService");
        }
    }

    @Test
    void shipRegistrySavedDataShouldPreserveOwnerCacheFields() throws IOException {
        String registry = Files.readString(SHIP_REGISTRY);

        assertTrue(registry.contains("extends SavedData"),
                "Ship registry should be persisted as server SavedData");
        assertTrue(registry.contains("level.getServer().overworld().getDataStorage().computeIfAbsent("),
                "Ship registry should be stored in server-wide SavedData, not a single dimension cache");
        assertTrue(registry.contains("tag.putUUID(\"ShipUuid\", this.shipUuid);"),
                "Ship registry entries should keep the ship UUID");
        assertTrue(registry.contains("tag.putUUID(\"OwnerUuid\", this.ownerUuid);"),
                "Ship registry entries should keep the owner UUID");
        assertTrue(registry.contains("tag.putString(\"Dimension\", this.dimension.location().toString());"),
                "Ship registry entries should keep the dimension");
        assertTrue(registry.contains("tag.putInt(\"PosX\", this.pos.getX());"),
                "Ship registry entries should keep X position");
        assertTrue(registry.contains("tag.putInt(\"PosY\", this.pos.getY());"),
                "Ship registry entries should keep Y position");
        assertTrue(registry.contains("tag.putInt(\"PosZ\", this.pos.getZ());"),
                "Ship registry entries should keep Z position");
        assertTrue(registry.contains("tag.putBoolean(\"Married\", this.married);"),
                "Ship registry entries should keep married state");
        assertTrue(registry.contains("tag.putBoolean(\"Removed\", this.removed);"),
                "Ship registry entries should keep removed state");
        assertTrue(registry.contains("tag.hasUUID(\"OwnerUuid\") ? tag.getUUID(\"OwnerUuid\") : null"),
                "Ship registry loading should restore owner UUID");
        assertTrue(registry.contains("tag.getBoolean(\"Married\")"),
                "Ship registry loading should restore married state");
        assertTrue(registry.contains("tag.getBoolean(\"Removed\")"),
                "Ship registry loading should restore removed state");
    }

    @Test
    void normalEntityUnloadShouldNotMarkShipRegistryEntryRemoved() throws IOException {
        String ship = Files.readString(SHIP_SOURCE);

        assertTrue(ship.contains("private boolean shouldMarkRemovedInRegistry(Entity.RemovalReason reason)"),
                "Registry removal semantics should be centralized next to entity removal handling");
        assertTrue(ship.contains("return reason == Entity.RemovalReason.KILLED || reason == Entity.RemovalReason.DISCARDED;"),
                "Only real death/discard removals should mark registry entries removed");
        assertTrue(ship.contains("if (shouldMarkRemovedInRegistry(reason)) {\n                registry.markRemoved(this);\n            } else {\n                registry.updateShip(this);\n            }"),
                "Chunk unload, player unload, and dimension changes should keep registry entries live");
    }

    @Test
    void playerScopedTargetAndDiplomacyDataShouldPersistAsSavedData() throws IOException {
        String diplomacy = Files.readString(TEAM_DIPLOMACY);
        String targets = Files.readString(PLAYER_TARGET_LIST);
        String unattackable = Files.readString(UNATTACKABLE_TARGETS);
        String registry = Files.readString(SHIP_REGISTRY);

        assertSavedDataUsesServerWideStorage(diplomacy, "shincolle_team_diplomacy");

        assertSavedDataUsesServerWideStorage(targets, "shincolle_player_target_lists");

        assertSavedDataUsesServerWideStorage(unattackable, "shincolle_unattackable_targets");
        assertTrue(registry.contains("return java.util.Collections.unmodifiableCollection(this.ships.values());"),
                "Ship registry collection views should stay read-only so callers cannot mutate cached entries without dirty tracking");
    }

    @Test
    void gameplayEntrypointsShouldUseSavedDataForTargetAndDiplomacyState() throws IOException {
        String network = Files.readString(NETWORK_SOURCE);
        String pointerService = Files.readString(POINTER_SERVICE_SOURCE);
        String diplomacyService = Files.readString(TEAM_DIPLOMACY_SERVICE_SOURCE);
        String targetProtectionService = Files.readString(TARGET_PROTECTION_SERVICE_SOURCE);
        String deskMenu = Files.readString(DESK_MENU_SOURCE);
        String targetWrench = Files.readString(TARGET_WRENCH);
        String passiveCombat = Files.readString(PASSIVE_COMBAT);

        assertTrue(pointerService.contains("TeamDiplomacyService.isDiplomaticAlly(ship, target)"),
                "Pointer target validation should route diplomacy checks through the diplomacy service");
        assertTrue(pointerService.contains("TargetProtectionService.isUnattackableTargetClass(ship, livingTarget)"),
                "Pointer target validation should route protected-target checks through the target service");

        assertTrue(diplomacyService.contains("TeamDiplomacySavedData diplomacy = TeamDiplomacySavedData.get(serverLevel);"),
                "Diplomacy mutations should use SavedData inside the diplomacy service");
        assertTrue(diplomacyService.contains("diplomacy.addAlly(owner, target)"),
                "Adding allies should mutate SavedData");
        assertTrue(diplomacyService.contains("diplomacy.addBanned(owner, target)"),
                "Adding banned players should mutate SavedData");
        assertTrue(diplomacyService.contains("diplomacy.setDisplayData(player.getUUID(), teamName, leaderName);"),
                "Diplomacy display metadata should be stored in SavedData");
        assertTrue(diplomacyService.contains("TeamDiplomacySavedData.get(serverLevel).areAllies(owner, targetOwner)"),
                "Diplomacy ally checks should read SavedData inside the diplomacy service");
        assertTrue(diplomacyService.contains("TeamDiplomacySavedData.get(serverLevel).isBanned(owner, targetOwner)"),
                "Diplomacy ban checks should read SavedData inside the diplomacy service");

        assertTrue(targetProtectionService.contains("UnattackableTargetData.get(serverLevel).contains(target.getClass().getName())"),
                "Global protected-target checks should read SavedData inside the target service");
        assertTrue(targetProtectionService.contains("UnattackableTargetData.get(serverLevel).toggle(className)"),
                "Global protected-target edits should persist through SavedData inside the target service");
        assertTrue(targetProtectionService.contains("PlayerTargetListSavedData.get(serverLevel).contains(ship.getOwnerUUID(), target.getClass().getName())"),
                "Per-player target-list checks should read SavedData inside the target service");
        assertTrue(targetProtectionService.contains("PlayerTargetListSavedData.get(serverLevel).toggle(player.getUUID(), className)"),
                "Per-player target-list edits should persist through SavedData inside the target service");

        assertTrue(deskMenu.contains("TeamDiplomacyService.sendDeskDiplomacySync(serverPlayer);"),
                "Opening desk diplomacy should delegate sync to the diplomacy service");
        assertTrue(targetWrench.contains("TargetProtectionService.toggleUnattackableTarget(player, entity);"),
                "Target wrench should delegate global protected-target edits to the target service");
        assertTrue(targetWrench.contains("TargetProtectionService.togglePlayerTarget(player, entity);"),
                "Target wrench should delegate per-player target-list edits to the target service");
        assertTrue(passiveCombat.contains("TargetProtectionService.isUnattackableTargetClass(this.ship, target)"),
                "Passive combat should consult global protected targets through the target service");
        assertTrue(passiveCombat.contains("TargetProtectionService.isPlayerConfiguredTargetClass(this.ship, target)"),
                "Passive combat should consult per-player target lists through the target service");
        assertTrue(passiveCombat.contains("TeamDiplomacyService.isDiplomaticAlly(this.ship, target)"),
                "Passive combat should consult diplomacy allies through the diplomacy service");
        assertTrue(passiveCombat.contains("TeamDiplomacyService.isDiplomaticBanned(this.ship, target)"),
                "Passive combat should consult diplomacy bans through the diplomacy service");

        assertFalse(network.contains("TeamDiplomacySavedData"),
                "Network handlers should not access diplomacy SavedData directly");
        assertFalse(network.contains("UnattackableTargetData"),
                "Network handlers should not access target SavedData directly");
        assertFalse(network.contains("PlayerTargetListSavedData"),
                "Network handlers should not access player target-list SavedData directly");
        assertFalse(deskMenu.contains("TeamDiplomacySavedData"),
                "Desk menu should not access diplomacy SavedData directly");
        assertFalse(targetWrench.contains("UnattackableTargetData"),
                "Target wrench should not access target SavedData directly");
        assertFalse(targetWrench.contains("PlayerTargetListSavedData"),
                "Target wrench should not access player target-list SavedData directly");
        assertFalse(passiveCombat.contains("TeamDiplomacySavedData"),
                "Passive combat should not access diplomacy SavedData directly");
        assertFalse(passiveCombat.contains("UnattackableTargetData"),
                "Passive combat should not access target SavedData directly");
        assertFalse(passiveCombat.contains("PlayerTargetListSavedData"),
                "Passive combat should not access player target-list SavedData directly");
    }

    private static void assertSavedDataUsesServerWideStorage(String source, String dataId) {
        assertTrue(source.contains("extends SavedData"),
                dataId + " should be backed by SavedData");
        assertTrue(source.contains("private static final String DATA_ID = \"" + dataId + "\";"),
                dataId + " should keep a stable SavedData id");
        assertTrue(source.contains("level.getServer().overworld().getDataStorage().computeIfAbsent("),
                dataId + " should be stored server-wide instead of per loaded dimension");
        assertTrue(source.contains("new SavedData.Factory"),
                dataId + " should register a SavedData load/save factory");
    }
}
