package org.trp.shincolle.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.attachment.AdmiralData;
import org.trp.shincolle.block.entity.IWaypoint;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.init.ModDataAttachments;
import org.trp.shincolle.init.ModDataComponents;
import org.trp.shincolle.item.DeskItemBook;
import org.trp.shincolle.item.PointerItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = Shincolle.MODID)
public class ModNetwork {

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
                C2SDeskSummonPayload.TYPE,
                C2SDeskSummonPayload.STREAM_CODEC,
                ModNetwork::handleDeskSummon
        );
        registrar.playToClient(
                S2CAdmiralDataSyncPayload.TYPE,
                S2CAdmiralDataSyncPayload.STREAM_CODEC,
                ModNetwork::handleAdmiralDataSync
        );
    }

    private static void handleAdmiralDataSync(final S2CAdmiralDataSyncPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null) {
                AdmiralData data = player.getData(ModDataAttachments.ADMIRAL_DATA);
                data.deserializeNBT(payload.nbt());
            }
        });
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
                if (dist > 48.0 * 48.0) {
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
                if (dist > 16.0 * 16.0) {
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
                    
                    if (dist > 48.0 * 48.0) {
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
                    
                    if (dist > 16.0 * 16.0) {
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
                    
                    if (dist > 16.0 * 16.0) {
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
                                        ship.setPointerTargetEntity(serverLevel.getEntity(payload.targetEntity().get()), 1200);
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
                    for (int i = 0; i < AdmiralData.SLOT_COUNT; i++) {
                        if (data.getShipUUID(teamId, i) == null) {
                            data.setShipUUID(teamId, i, targetUUID);
                            data.setSelected(teamId, i, true);
                            if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                                net.minecraft.world.entity.Entity e = serverLevel.getEntity(targetUUID);
                                if (e instanceof EntityShipBase ship) {
                                    ship.setFormationTeam(teamId);
                                    ship.setFormationSlot(i);
                                    ship.setPointerSelected(true);
                                }
                            }
                            break;
                        }
                    }
                }
                context.reply(new S2CAdmiralDataSyncPayload(data.serializeNBT()));
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
                    data.setCurrentTeamID(payload.param1());
                    break;
                case 1: 
                    data.setFormationID(data.getCurrentTeamID(), payload.param1());
                    break;
                case 2: 
                {
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
                    UUID shipUUID = data.getShipUUID(data.getCurrentTeamID(), payload.param1());
                    if (shipUUID != null && player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        net.minecraft.world.entity.Entity entity = serverLevel.getEntity(shipUUID);
                        if (entity instanceof EntityShipBase ship) {
                            ship.setFormationTeam(-1);
                            ship.setFormationSlot(-1);
                            ship.setPointerSelected(false);
                        }
                    }
                    data.setShipUUID(data.getCurrentTeamID(), payload.param1(), null);
                    break;
                }
                case 4: 
                    data.setTeamName(data.getCurrentTeamID(), payload.paramString());
                    break;
                case 5: 
                    payload.paramUUID().ifPresent(uuid -> {
                        data.setShipUUID(data.getCurrentTeamID(), payload.param1(), uuid);
                        if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                            net.minecraft.world.entity.Entity entity = serverLevel.getEntity(uuid);
                            if (entity instanceof EntityShipBase ship) {
                                ship.setFormationTeam(data.getCurrentTeamID());
                                ship.setFormationSlot(payload.param1());
                            }
                        }
                    });
                    break;
                case 6:
                    int slot1 = payload.param1();
                    int slot2 = payload.param2();
                    data.swapShips(data.getCurrentTeamID(), slot1, slot2);
                    if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        UUID uuid1 = data.getShipUUID(data.getCurrentTeamID(), slot1);
                        if (uuid1 != null) {
                            net.minecraft.world.entity.Entity e1 = serverLevel.getEntity(uuid1);
                            if (e1 instanceof EntityShipBase ship1) {
                                ship1.setFormationSlot(slot1);
                            }
                        }
                        UUID uuid2 = data.getShipUUID(data.getCurrentTeamID(), slot2);
                        if (uuid2 != null) {
                            net.minecraft.world.entity.Entity e2 = serverLevel.getEntity(uuid2);
                            if (e2 instanceof EntityShipBase ship2) {
                                ship2.setFormationSlot(slot2);
                            }
                        }
                    }
                    break;
                case 7:
                    if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        int tid = data.getCurrentTeamID();
                        List<EntityShipBase> nearbySelected = serverLevel.getEntitiesOfClass(EntityShipBase.class, player.getBoundingBox().inflate(64), 
                            ship -> ship.isPointerSelected() && player.getUUID().equals(ship.getOwnerUUID()));
                        
                        for (EntityShipBase ship : nearbySelected) {
                            if (!data.isShipInTeam(tid, ship.getUUID())) {
                                int emptySlot = data.findFirstEmptySlot(tid);
                                if (emptySlot != -1) {
                                    data.setShipUUID(tid, emptySlot, ship.getUUID());
                                }
                            }
                        }

                        for (int i = 0; i < AdmiralData.SLOT_COUNT; i++) {
                            UUID uuid = data.getShipUUID(tid, i);
                            if (uuid != null) {
                                net.minecraft.world.entity.Entity e = serverLevel.getEntity(uuid);
                                if (e instanceof EntityShipBase ship) {
                                    ship.setFormationTeam(tid);
                                    ship.setFormationSlot(i);
                                    ship.setPointerSelected(data.isSelected(tid, i));
                                }
                            }
                        }
                    }
                    break;
            }
            
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new S2CAdmiralDataSyncPayload(data.serializeNBT()));
            }
        });
    }

    private static void handleDeskSummon(final C2SDeskSummonPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            org.trp.shincolle.utility.FormationHelper.applySummonShipsToDesk(player, payload.deskPos(), payload.shipUuids());
        });
    }
}
