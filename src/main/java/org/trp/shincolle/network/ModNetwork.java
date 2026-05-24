package org.trp.shincolle.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.trp.shincolle.Config;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.attachment.AdmiralData;
import org.trp.shincolle.block.entity.IWaypoint;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.init.ModDataAttachments;
import org.trp.shincolle.init.ModDataComponents;
import org.trp.shincolle.item.DeskItemBook;
import org.trp.shincolle.item.PointerItem;
import org.trp.shincolle.server.UnattackableTargetData;
import org.trp.shincolle.server.TeamDiplomacySavedData;
import org.trp.shincolle.entity.base.EntityMountBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@EventBusSubscriber(modid = Shincolle.MODID)
public class ModNetwork {

    private static void clearFormationState(EntityShipBase ship) {
        ship.setFormationTeam(-1);
        ship.setFormationSlot(-1);
        ship.setPointerSelected(false);
        ship.clearPointerTarget();
        ship.clearPointerTargetEntity();
    }

    private static void applyFormationState(EntityShipBase ship, int teamId, int slotId, boolean selected) {
        ship.setFormationTeam(teamId);
        ship.setFormationSlot(slotId);
        ship.setPointerSelected(selected);
    }

    public static boolean canAssignPointerEntityTarget(Player player, EntityShipBase ship, Entity target) {
        if (player == null || ship == null || target == null) {
            return false;
        }
        if (!(target instanceof LivingEntity livingTarget)) {
            return false;
        }
        if (target == player || target == ship || !target.isAlive()) {
            return false;
        }
        if (livingTarget.isSpectator()) {
            return false;
        }
        if (livingTarget instanceof Player targetPlayer && targetPlayer.getAbilities().invulnerable) {
            return false;
        }
        if (target instanceof EntityShipBase targetShip && targetShip.isOwnedBy(player)) {
            return false;
        }
        if (sharesOwner(ship, target)) {
            return false;
        }
        if (isDiplomaticAlly(ship, target)) {
            return false;
        }
        if (isUnattackableTargetClass(ship, livingTarget)) {
            return false;
        }
        return true;
    }

    private static boolean sharesOwner(EntityShipBase ship, Entity target) {
        UUID ownerId = ship.getOwnerUUID();
        if (ownerId == null) {
            return false;
        }
        if (target instanceof Player player) {
            return ownerId.equals(player.getUUID());
        }
        if (target instanceof TamableAnimal tamable) {
            return ownerId.equals(tamable.getOwnerUUID());
        }
        if (target instanceof EntityMountBase mount) {
            EntityShipBase host = mount.getHost();
            if (host != null) {
                return Objects.equals(host.getOwnerUUID(), ownerId);
            }
            return Objects.equals(mount.getHostUUID(), ownerId);
        }
        return false;
    }

    private static boolean isDiplomaticAlly(EntityShipBase ship, Entity target) {
        if (!(ship.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        UUID owner = ship.getOwnerUUID();
        UUID targetOwner = getTargetOwnerUUID(target);
        return TeamDiplomacySavedData.get(serverLevel).areAllies(owner, targetOwner);
    }

    private static boolean isUnattackableTargetClass(EntityShipBase ship, LivingEntity target) {
        if (!(ship.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        return UnattackableTargetData.get(serverLevel).contains(target.getClass().getName());
    }

    private static UUID getTargetOwnerUUID(Entity target) {
        if (target instanceof Player player) {
            return player.getUUID();
        }
        if (target instanceof EntityShipBase shipTarget) {
            return shipTarget.getOwnerUUID();
        }
        if (target instanceof TamableAnimal tamable) {
            return tamable.getOwnerUUID();
        }
        if (target instanceof EntityMountBase mount) {
            EntityShipBase host = mount.getHost();
            if (host != null) {
                return host.getOwnerUUID();
            }
            return mount.getHostUUID();
        }
        if (target instanceof Enemy) {
            return null;
        }
        return null;
    }

    public static void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Shincolle.MODID);
        registrar.playToServer(
                C2SBookStatePayload.TYPE,
                C2SBookStatePayload.STREAM_CODEC,
                ModNetwork::handleBookState
        );
        registrar.playToServer(
                C2SDeskGuiPayload.TYPE,
                C2SDeskGuiPayload.STREAM_CODEC,
                ModNetwork::handleDeskGui
        );
        registrar.playToServer(
                C2SWaypointActionPayload.TYPE,
                C2SWaypointActionPayload.STREAM_CODEC,
                ModNetwork::handleWaypointAction
        );
        registrar.playToServer(
                C2SPointerActionPayload.TYPE,
                C2SPointerActionPayload.STREAM_CODEC,
                ModNetwork::handlePointerAction
        );
        registrar.playToServer(
                C2SFormationActionPayload.TYPE,
                C2SFormationActionPayload.STREAM_CODEC,
                ModNetwork::handleFormationAction
        );
        registrar.playToServer(
                C2SDeskOpenShipPayload.TYPE,
                C2SDeskOpenShipPayload.STREAM_CODEC,
                ModNetwork::handleDeskOpenShip
        );
        registrar.playToServer(
                C2STeamDiplomacyPayload.TYPE,
                C2STeamDiplomacyPayload.STREAM_CODEC,
                ModNetwork::handleTeamDiplomacy
        );
        registrar.playToClient(
                S2CAdmiralDataSyncPayload.TYPE,
                S2CAdmiralDataSyncPayload.STREAM_CODEC,
                ModNetwork::handleAdmiralDataSync
        );
        registrar.playToClient(
                S2CDeskDiplomacySyncPayload.TYPE,
                S2CDeskDiplomacySyncPayload.STREAM_CODEC,
                ModNetwork::handleDeskDiplomacySync
        );
    }

    private static void handleAdmiralDataSync(final S2CAdmiralDataSyncPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null) {
                AdmiralData data = player.getData(ModDataAttachments.ADMIRAL_DATA);
                data.deserializeNBT(payload.admiralNbt());
                var collected = player.getData(ModDataAttachments.COLLECTED_SHIPS);
                collected.clear();
                for (int classId : payload.collectedShips()) {
                    collected.add(classId);
                }

                if (player.level().isClientSide) {
                    int mode = PointerItem.MODE_SINGLE;
                    ItemStack pointerStack = ItemStack.EMPTY;
                    ItemStack main = player.getMainHandItem();
                    if (main.is(org.trp.shincolle.init.ModItems.POINTER_ITEM.get())) {
                        pointerStack = main;
                    } else {
                        ItemStack off = player.getOffhandItem();
                        if (off.is(org.trp.shincolle.init.ModItems.POINTER_ITEM.get())) {
                            pointerStack = off;
                        }
                    }
                    if (!pointerStack.isEmpty() && pointerStack.getItem() instanceof PointerItem pi) {
                        mode = pi.getMode(pointerStack);
                    }

                    if (mode == PointerItem.MODE_FORMATION) {
                        int teamId = data.getCurrentTeamID();
                        List<EntityShipBase> ships = player.level().getEntitiesOfClass(EntityShipBase.class, player.getBoundingBox().inflate(100.0),
                                ship -> ship.isOwnedBy(player) && !ship.isInDeadPose());
                        for (EntityShipBase ship : ships) {
                            if (ship.getFormationTeam() == teamId) {
                                int slot = ship.getFormationSlot();
                                ship.setPointerSelected(data.isSelected(teamId, slot));
                            } else {
                                ship.setPointerSelected(false);
                            }
                        }
                    }
                }
            }
        });
    }

    private static void handleDeskDiplomacySync(final S2CDeskDiplomacySyncPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> DeskDiplomacySync.update(
                payload.ownerUuid(),
                java.util.List.of(payload.allies()),
                java.util.List.of(payload.banned()),
                java.util.List.of(payload.displayUuids()),
                java.util.List.of(payload.displayTeamNames()),
                java.util.List.of(payload.displayLeaderNames())
        ));
    }

    private static void handleBookState(final C2SBookStatePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.containerMenu instanceof org.trp.shincolle.menu.DeskMenu deskMenu) {
                if (deskMenu.getDeskType() == 0 && deskMenu.getBlockEntity() != null) {
                    deskMenu.getBlockEntity().setBookChap(payload.chapter());
                    deskMenu.getBlockEntity().setBookPage(payload.page());
                    return;
                }
            }

            ItemStack stack = player.getMainHandItem();
            if (!(stack.getItem() instanceof DeskItemBook)) {
                stack = player.getOffhandItem();
            }

            if (stack.getItem() instanceof DeskItemBook) {
                stack.set(ModDataComponents.BOOK_CHAPTER, payload.chapter());
                stack.set(ModDataComponents.BOOK_PAGE, payload.page());
            }
        });
    }

    private static void handleDeskGui(final C2SDeskGuiPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.containerMenu instanceof org.trp.shincolle.menu.DeskMenu deskMenu) {
                if (deskMenu.getDeskType() == 0 && deskMenu.getBlockEntity() != null) {
                    deskMenu.getBlockEntity().setGuiFunc(payload.guiFunc());
                    deskMenu.getBlockEntity().setRadarZoomLv(payload.radarZoom());
                    if (payload.guiFunc() >= 3 && payload.guiFunc() <= 4 && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                        sendDeskDiplomacySync(serverPlayer);
                    }
                }
            }
        });
    }

    private static void handleWaypointAction(final C2SWaypointActionPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.level() == null) return;

            BlockPos pos1 = new BlockPos(payload.x1(), payload.y1(), payload.z1());
            BlockPos pos2 = new BlockPos(payload.x2(), payload.y2(), payload.z2());

            double dist = pos1.distSqr(pos2);

            if (payload.action() == 0) {
                if (dist > (double) Config.pairDistWaypoint * Config.pairDistWaypoint) {
                    player.displayClientMessage(Component.translatable("chat.shincolle.wrench.wptoofar"), false);
                    return;
                }
                if (player.level().getBlockEntity(pos1) instanceof IWaypoint wpFrom
                        && player.level().getBlockEntity(pos2) instanceof IWaypoint wpTo) {
                    if (wpFrom.getOwnerUUID() != null && !wpFrom.getOwnerUUID().equals(player.getUUID())) {
                        player.displayClientMessage(Component.translatable("chat.shincolle.wrongowner"), false);
                        return;
                    }
                    wpFrom.setNextPos(pos2);
                    if (!wpTo.getNextPos().equals(pos1)) {
                        wpTo.setLastPos(pos1);
                    }
                    player.displayClientMessage(
                        Component.translatable("chat.shincolle.wrench.setwp")
                            .append(" " + pos1.getX() + " " + pos1.getY() + " " + pos1.getZ()
                                + " --> " + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ()),
                        false
                    );
                }
            } else if (payload.action() == 1) {
                if (dist > (double) Config.pairDistChest * Config.pairDistChest) {
                    player.displayClientMessage(Component.translatable("chat.shincolle.wrench.toofar"), false);
                    return;
                }
                if (player.level().getBlockEntity(pos1) instanceof IWaypoint wpFrom
                        && (player.level().getBlockEntity(pos2) instanceof net.minecraft.world.level.block.entity.BaseContainerBlockEntity
                        || player.level().getBlockEntity(pos2) instanceof org.trp.shincolle.block.entity.CraneBlockEntity)) {
                    if (wpFrom.getOwnerUUID() != null && !wpFrom.getOwnerUUID().equals(player.getUUID())) {
                        player.displayClientMessage(Component.translatable("chat.shincolle.wrongowner"), false);
                        return;
                    }
                    wpFrom.setChestPos(pos2);
                    player.displayClientMessage(
                        Component.translatable("chat.shincolle.wrench.setwp")
                            .append(" " + pos1.getX() + " " + pos1.getY() + " " + pos1.getZ()
                                + " & " + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ()),
                        false
                    );
                }
            } else if (payload.action() == 2) {
                
                var be1 = player.level().getBlockEntity(pos1);
                var be2 = player.level().getBlockEntity(pos2);

                if (be1 instanceof IWaypoint wp1 && be2 instanceof IWaypoint wp2) {
                    
                    if (dist > (double) Config.pairDistWaypoint * Config.pairDistWaypoint) {
                        player.displayClientMessage(Component.translatable("chat.shincolle.wrench.wptoofar"), false);
                        return;
                    }
                    if (wp1.getOwnerUUID() != null && !wp1.getOwnerUUID().equals(player.getUUID())) {
                        player.displayClientMessage(Component.translatable("chat.shincolle.wrongowner"), false);
                        return;
                    }
                    wp1.setNextPos(pos2);
                    if (!wp2.getNextPos().equals(pos1)) {
                        wp2.setLastPos(pos1);
                    }
                    player.displayClientMessage(Component.translatable("chat.shincolle.wrench.setwp")
                        .append(" " + pos1.getX() + " " + pos1.getY() + " " + pos1.getZ()
                            + " --> " + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ()), false);
                } else if (be1 instanceof IWaypoint wp && (be2 instanceof net.minecraft.world.level.block.entity.BaseContainerBlockEntity || be2 instanceof org.trp.shincolle.block.entity.CraneBlockEntity)) {
                    
                    if (dist > (double) Config.pairDistChest * Config.pairDistChest) {
                        player.displayClientMessage(Component.translatable("chat.shincolle.wrench.toofar"), false);
                        return;
                    }
                    if (wp.getOwnerUUID() != null && !wp.getOwnerUUID().equals(player.getUUID())) {
                        player.displayClientMessage(Component.translatable("chat.shincolle.wrongowner"), false);
                        return;
                    }
                    wp.setChestPos(pos2);
                    player.displayClientMessage(Component.translatable("chat.shincolle.wrench.setwp")
                        .append(" " + pos1.getX() + " " + pos1.getY() + " " + pos1.getZ()
                            + " & " + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ()), false);
                } else if (be2 instanceof IWaypoint wp && (be1 instanceof net.minecraft.world.level.block.entity.BaseContainerBlockEntity || be1 instanceof org.trp.shincolle.block.entity.CraneBlockEntity)) {
                    
                    if (dist > (double) Config.pairDistChest * Config.pairDistChest) {
                        player.displayClientMessage(Component.translatable("chat.shincolle.wrench.toofar"), false);
                        return;
                    }
                    if (wp.getOwnerUUID() != null && !wp.getOwnerUUID().equals(player.getUUID())) {
                        player.displayClientMessage(Component.translatable("chat.shincolle.wrongowner"), false);
                        return;
                    }
                    wp.setChestPos(pos1);
                    player.displayClientMessage(Component.translatable("chat.shincolle.wrench.setwp")
                        .append(" " + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ()
                            + " & " + pos1.getX() + " " + pos1.getY() + " " + pos1.getZ()), false);
                } else {
                    player.displayClientMessage(Component.translatable("chat.shincolle.wrench.wrongtile"), false);
                }
            }
        });
    }

    private static void handlePointerAction(final C2SPointerActionPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ItemStack stack = player.getMainHandItem();
            if (!(stack.getItem() instanceof PointerItem)) {
                stack = player.getOffhandItem();
            }

            if (!(stack.getItem() instanceof PointerItem pointerItem)) return;

            if (payload.action() == 0) {
                
                int next = pointerItem.cycleMode(stack);
                PointerItem.updateServerSideMode(player, stack, next);
            } else if (payload.action() == 1 || payload.action() == 2) {
                
                int mode = pointerItem.getMode(stack);
                AdmiralData data = player.getData(ModDataAttachments.ADMIRAL_DATA);
                int teamId = data.getCurrentTeamID();

                if (mode == PointerItem.MODE_FORMATION) {
                    for (int i = 0; i < AdmiralData.SLOT_COUNT; i++) {
                        if (data.isSelected(teamId, i)) {
                            UUID shipUUID = data.getShipUUID(teamId, i);
                            if (shipUUID != null && player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                                net.minecraft.world.entity.Entity entity = serverLevel.getEntity(shipUUID);
                                if (entity instanceof EntityShipBase ship) {
                                    if (payload.action() == 1 && payload.targetEntity().isPresent()) {
                                        Entity target = serverLevel.getEntity(payload.targetEntity().get());
                                        if (canAssignPointerEntityTarget(player, ship, target)) {
                                            ship.setPointerTargetEntity(target, 1200);
                                        }
                                    } else if (payload.action() == 2 && payload.targetPos().isPresent()) {
                                        ship.setPointerTarget(payload.targetPos().get(), 1200);
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (payload.action() == 3 && payload.targetEntity().isPresent()) {
                if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    net.minecraft.world.entity.Entity entity = serverLevel.getEntity(payload.targetEntity().get());
                    if (entity instanceof EntityShipBase ship && ship.isOwnedBy(player)) {
                        ship.openShipMenu(player);
                    }
                }
            } else if (payload.action() == 5 && payload.targetEntity().isPresent()) {
                UUID targetUUID = payload.targetEntity().get();
                AdmiralData data = player.getData(ModDataAttachments.ADMIRAL_DATA);
                int teamId = data.getCurrentTeamID();
                int slot = -1;
                for (int i = 0; i < AdmiralData.SLOT_COUNT; i++) {
                    if (targetUUID.equals(data.getShipUUID(teamId, i))) {
                        slot = i;
                        break;
                    }
                }

                if (slot != -1) {
                    boolean nextState = !data.isSelected(teamId, slot);
                    data.setSelected(teamId, slot, nextState);
                    if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        net.minecraft.world.entity.Entity e = serverLevel.getEntity(targetUUID);
                        if (e instanceof EntityShipBase ship) {
                            ship.setPointerSelected(nextState);
                        }
                    }
                } else {
                    int previousTeam = data.findShipTeam(targetUUID);
                    int previousSlot = previousTeam >= 0 ? data.findShipSlot(previousTeam, targetUUID) : -1;
                    int assignedSlot = data.assignShipToTeam(teamId, targetUUID);
                    if (assignedSlot == -1) {
                        player.displayClientMessage(Component.translatable("chat.shincolle.formation.teamfull"), false);
                        context.reply(S2CAdmiralDataSyncPayload.of(data.serializeNBT(), player.getData(ModDataAttachments.COLLECTED_SHIPS)));
                        return;
                    }
                    if (assignedSlot != -1 && player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        net.minecraft.world.entity.Entity e = serverLevel.getEntity(targetUUID);
                        if (e instanceof EntityShipBase ship) {
                            applyFormationState(ship, teamId, assignedSlot, true);
                        }
                        if (previousTeam >= 0 && previousTeam != teamId && previousSlot >= 0) {
                            UUID oldUuid = data.getShipUUID(previousTeam, previousSlot);
                            if (oldUuid != null && !oldUuid.equals(targetUUID)) {
                                net.minecraft.world.entity.Entity displacedEntity = serverLevel.getEntity(oldUuid);
                                if (displacedEntity instanceof EntityShipBase displacedShip) {
                                    clearFormationState(displacedShip);
                                }
                            }
                        }
                    }
                }
                context.reply(S2CAdmiralDataSyncPayload.of(data.serializeNBT(), player.getData(ModDataAttachments.COLLECTED_SHIPS)));
            } else if (payload.action() == 4) {
                player.openMenu(new net.minecraft.world.SimpleMenuProvider(
                        (id, inv, p) -> new org.trp.shincolle.menu.FormationMenu(id, inv),
                        net.minecraft.network.chat.Component.translatable("gui.shincolle.formation.title")
                ));
            }
        });
    }

    private static void handleFormationAction(final C2SFormationActionPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            AdmiralData data = player.getData(ModDataAttachments.ADMIRAL_DATA);

            switch (payload.action()) {
                case 0: 
                {
                    int nextTeam = payload.param1();
                    if (nextTeam < 0 || nextTeam >= AdmiralData.TEAM_COUNT) {
                        break;
                    }
                    data.setCurrentTeamID(nextTeam);
                    if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        List<EntityShipBase> ships = serverLevel.getEntitiesOfClass(EntityShipBase.class, player.getBoundingBox().inflate(100.0),
                            ship -> player.getUUID().equals(ship.getOwnerUUID()) && !ship.isInDeadPose());
                        for (EntityShipBase ship : ships) {
                            if (ship.getFormationTeam() == nextTeam) {
                                int slot = ship.getFormationSlot();
                                ship.setPointerSelected(data.isSelected(nextTeam, slot));
                            } else {
                                ship.setPointerSelected(false);
                                ship.clearPointerTarget();
                                ship.clearPointerTargetEntity();
                            }
                        }
                    }
                    break;
                }
                case 1: 
                    if (payload.param1() < 0) {
                        break;
                    }
                    data.setFormationID(data.getCurrentTeamID(), payload.param1());
                    break;
                case 2: 
                {
                    if (payload.param1() < 0 || payload.param1() >= AdmiralData.SLOT_COUNT) {
                        break;
                    }
                    boolean nextState = payload.param2() != 0;
                    data.setSelected(data.getCurrentTeamID(), payload.param1(), nextState);
                    UUID shipUUID = data.getShipUUID(data.getCurrentTeamID(), payload.param1());
                    if (shipUUID != null && player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        net.minecraft.world.entity.Entity e = serverLevel.getEntity(shipUUID);
                        if (e instanceof EntityShipBase ship) {
                            ship.setPointerSelected(nextState);
                        }
                    }
                    break;
                }
                case 8: 
                    if (payload.param1() < 0 || payload.param1() >= AdmiralData.SLOT_COUNT) {
                        break;
                    }
                    UUID guiTarget = data.getShipUUID(data.getCurrentTeamID(), payload.param1());
                    if (guiTarget != null && player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        net.minecraft.world.entity.Entity e = serverLevel.getEntity(guiTarget);
                        if (e instanceof org.trp.shincolle.entity.base.EntityShipBase ship) {
                            ship.openShipMenu(player);
                        }
                    }
                    break;
                case 3: 
                {
                    if (payload.param1() < 0 || payload.param1() >= AdmiralData.SLOT_COUNT) {
                        break;
                    }
                    UUID shipUUID = data.getShipUUID(data.getCurrentTeamID(), payload.param1());
                    if (shipUUID != null && player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        net.minecraft.world.entity.Entity entity = serverLevel.getEntity(shipUUID);
                        if (entity instanceof EntityShipBase ship) {
                            clearFormationState(ship);
                        }
                    }
                    data.removeShip(shipUUID);
                    break;
                }
                case 4:
                    data.setTeamName(data.getCurrentTeamID(), payload.paramString());
                    break;
                case 5:
                    payload.paramUUID().ifPresent(uuid -> {
                        if (payload.param1() < 0 || payload.param1() >= AdmiralData.SLOT_COUNT) {
                            return;
                        }
                        UUID replacedUuid = data.getShipUUID(data.getCurrentTeamID(), payload.param1());
                        if (replacedUuid != null && player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                            net.minecraft.world.entity.Entity replacedEntity = serverLevel.getEntity(replacedUuid);
                            if (replacedEntity instanceof EntityShipBase replacedShip) {
                                clearFormationState(replacedShip);
                            }
                        }

                        data.removeShip(uuid);
                        if (replacedUuid != null && !replacedUuid.equals(uuid)) {
                            data.removeShip(replacedUuid);
                        }
                        data.setShipUUID(data.getCurrentTeamID(), payload.param1(), uuid);
                        data.setSelected(data.getCurrentTeamID(), payload.param1(), true);
                        if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                            net.minecraft.world.entity.Entity entity = serverLevel.getEntity(uuid);
                            if (entity instanceof EntityShipBase ship) {
                                applyFormationState(ship, data.getCurrentTeamID(), payload.param1(), true);
                            }
                        }
                    });
                    break;
                case 6:
                    int slot1 = payload.param1();
                    int slot2 = payload.param2();
                    if (slot1 < 0 || slot1 >= AdmiralData.SLOT_COUNT || slot2 < 0 || slot2 >= AdmiralData.SLOT_COUNT || slot1 == slot2) {
                        break;
                    }
                    int currentTeamId = data.getCurrentTeamID();
                    data.swapShips(currentTeamId, slot1, slot2);
                    if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        UUID uuid1 = data.getShipUUID(currentTeamId, slot1);
                        if (uuid1 != null) {
                            net.minecraft.world.entity.Entity e1 = serverLevel.getEntity(uuid1);
                            if (e1 instanceof EntityShipBase ship1) {
                                ship1.setFormationSlot(slot1);
                                ship1.setPointerSelected(data.isSelected(currentTeamId, slot1));
                            }
                        }
                        UUID uuid2 = data.getShipUUID(currentTeamId, slot2);
                        if (uuid2 != null) {
                            net.minecraft.world.entity.Entity e2 = serverLevel.getEntity(uuid2);
                            if (e2 instanceof EntityShipBase ship2) {
                                ship2.setFormationSlot(slot2);
                                ship2.setPointerSelected(data.isSelected(currentTeamId, slot2));
                            }
                        }
                    }
                    break;
                case 7:
                    if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        int tid = data.getCurrentTeamID();
                        boolean teamFilledDuringSync = false;
                        List<EntityShipBase> nearbySelected = serverLevel.getEntitiesOfClass(EntityShipBase.class, player.getBoundingBox().inflate(64), 
                            ship -> ship.isPointerSelected() && player.getUUID().equals(ship.getOwnerUUID()));
                        List<EntityShipBase> nearbyOwned = serverLevel.getEntitiesOfClass(EntityShipBase.class, player.getBoundingBox().inflate(100.0),
                            ship -> player.getUUID().equals(ship.getOwnerUUID()) && !ship.isInDeadPose());
                        
                        for (EntityShipBase ship : nearbySelected) {
                            if (!data.isShipInTeam(tid, ship.getUUID())) {
                                if (data.assignShipToTeam(tid, ship.getUUID()) == -1) {
                                    teamFilledDuringSync = true;
                                }
                            }
                        }

                        for (EntityShipBase ship : nearbyOwned) {
                            if (ship.getFormationTeam() == tid && !data.isShipInTeam(tid, ship.getUUID())) {
                                clearFormationState(ship);
                            }
                        }

                        for (int i = 0; i < AdmiralData.SLOT_COUNT; i++) {
                            UUID uuid = data.getShipUUID(tid, i);
                            if (uuid != null) {
                                net.minecraft.world.entity.Entity e = serverLevel.getEntity(uuid);
                                if (e instanceof EntityShipBase ship) {
                                    applyFormationState(ship, tid, i, data.isSelected(tid, i));
                                }
                            }
                        }

                        if (teamFilledDuringSync) {
                            player.displayClientMessage(Component.translatable("chat.shincolle.formation.teamfull"), false);
                        }
                    }
                    break;
            }
            
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, S2CAdmiralDataSyncPayload.of(data.serializeNBT(), player.getData(ModDataAttachments.COLLECTED_SHIPS)));
            }
        });
    }

    private static void handleDeskOpenShip(final C2SDeskOpenShipPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
                return;
            }

            Entity entity = serverLevel.getEntity(payload.shipUuid());
            if (entity instanceof EntityShipBase ship && ship.isOwnedBy(player)) {
                ship.openShipMenu(player);
            }
        });
    }

    private static void handleTeamDiplomacy(final C2STeamDiplomacyPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
                return;
            }

            UUID owner = player.getUUID();
            UUID target = payload.targetUuid();
            if (target == null || owner.equals(target)) {
                return;
            }

            TeamDiplomacySavedData diplomacy = TeamDiplomacySavedData.get(serverLevel);
            Player targetPlayer = serverLevel.getPlayerByUUID(target);
            Component targetName = targetPlayer != null ? targetPlayer.getDisplayName() : Component.literal(target.toString());
            boolean changed;
            Component message;
            switch (payload.action()) {
                case C2STeamDiplomacyPayload.ACTION_ADD_ALLY -> {
                    changed = diplomacy.addAlly(owner, target);
                    message = changed
                            ? Component.translatable("chat.shincolle.team.ally_added").append(targetName)
                            : Component.translatable("chat.shincolle.team.ally_unchanged").append(targetName);
                }
                case C2STeamDiplomacyPayload.ACTION_REMOVE_ALLY -> {
                    changed = diplomacy.removeAlly(owner, target);
                    message = changed
                            ? Component.translatable("chat.shincolle.team.ally_removed").append(targetName)
                            : Component.translatable("chat.shincolle.team.ally_missing").append(targetName);
                }
                case C2STeamDiplomacyPayload.ACTION_ADD_BANNED -> {
                    changed = diplomacy.addBanned(owner, target);
                    message = changed
                            ? Component.translatable("chat.shincolle.team.hostile_added").append(targetName)
                            : Component.translatable("chat.shincolle.team.hostile_unchanged").append(targetName);
                }
                case C2STeamDiplomacyPayload.ACTION_REMOVE_BANNED -> {
                    changed = diplomacy.removeBanned(owner, target);
                    message = changed
                            ? Component.translatable("chat.shincolle.team.hostile_removed").append(targetName)
                            : Component.translatable("chat.shincolle.team.hostile_missing").append(targetName);
                }
                default -> {
                    return;
                }
            }
            player.displayClientMessage(message, false);
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                sendDeskDiplomacySync(serverPlayer);
            }
        });
    }

    private static void sendDeskDiplomacySync(net.minecraft.server.level.ServerPlayer player) {
        TeamDiplomacySavedData diplomacy = TeamDiplomacySavedData.get(player.serverLevel());
        updateDiplomacyDisplayData(player, diplomacy);
        TeamDiplomacySavedData.TeamDiplomacyEntry entry = diplomacy.getOrCreate(player.getUUID());

        java.util.LinkedHashSet<UUID> displayIds = new java.util.LinkedHashSet<>();
        displayIds.addAll(entry.allies());
        displayIds.addAll(entry.banned());

        java.util.ArrayList<UUID> uuids = new java.util.ArrayList<>();
        java.util.ArrayList<String> teamNames = new java.util.ArrayList<>();
        java.util.ArrayList<String> leaderNames = new java.util.ArrayList<>();
        for (UUID target : displayIds) {
            if (target == null) {
                continue;
            }
            uuids.add(target);
            TeamDiplomacySavedData.TeamDiplomacyEntry targetEntry = diplomacy.get(target);
            teamNames.add(targetEntry == null ? "" : targetEntry.teamName());
            String leaderName = targetEntry == null ? "" : targetEntry.leaderName();
            if (leaderName.isBlank()) {
                leaderName = resolveDiplomacyLeaderName(player, target);
            }
            leaderNames.add(leaderName);
        }

        PacketDistributor.sendToPlayer(player, S2CDeskDiplomacySyncPayload.of(
                player.getUUID(),
                entry.allies(),
                entry.banned(),
                uuids,
                teamNames,
                leaderNames
        ));
    }

    private static void updateDiplomacyDisplayData(net.minecraft.server.level.ServerPlayer player, TeamDiplomacySavedData diplomacy) {
        AdmiralData data = player.getData(ModDataAttachments.ADMIRAL_DATA);
        String teamName = data.getTeamName(data.getCurrentTeamID());
        String leaderName = player.getName().getString();
        diplomacy.setDisplayData(player.getUUID(), teamName, leaderName);
    }

    private static String resolveDiplomacyLeaderName(net.minecraft.server.level.ServerPlayer player, UUID target) {
        if (target == null) {
            return "";
        }
        net.minecraft.server.level.ServerPlayer onlinePlayer = player.server.getPlayerList().getPlayer(target);
        if (onlinePlayer != null) {
            return onlinePlayer.getName().getString();
        }
        GameProfileCache profileCache = player.server.getProfileCache();
        if (profileCache == null) {
            return "";
        }
        return profileCache.get(target).map(com.mojang.authlib.GameProfile::getName).orElse("");
    }
}
