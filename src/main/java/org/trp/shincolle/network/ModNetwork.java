package org.trp.shincolle.network;

import net.minecraft.core.BlockPos;
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
import org.trp.shincolle.item.PointerItem;
import org.trp.shincolle.server.DeskInteractionService;
import org.trp.shincolle.server.FormationService;
import org.trp.shincolle.server.PlayerStateService;
import org.trp.shincolle.server.PointerInteractionService;
import org.trp.shincolle.server.TeamDiplomacyService;
import org.trp.shincolle.server.WaypointService;

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
                C2SDeskOpenShipPayload.TYPE,
                C2SDeskOpenShipPayload.STREAM_CODEC,
                ModNetwork::handleDeskOpenShip
        );
        registrar.playToServer(
                C2SDeskSummonPayload.TYPE,
                C2SDeskSummonPayload.STREAM_CODEC,
                ModNetwork::handleDeskSummon
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
                PlayerStateService.applyAdmiralSync(player, payload.admiralNbt(), payload.collectedShips());
                PlayerStateService.refreshClientPointerSelection(player);
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
            DeskInteractionService.updateBookState(player, payload.chapter(), payload.page());
        });
    }

    private static void handleDeskGui(final C2SDeskGuiPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            DeskInteractionService.updateDeskGui(player, payload.guiFunc(), payload.radarZoom());
        });
    }

    private static void handleWaypointAction(final C2SWaypointActionPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            BlockPos pos1 = new BlockPos(payload.x1(), payload.y1(), payload.z1());
            BlockPos pos2 = new BlockPos(payload.x2(), payload.y2(), payload.z2());
            WaypointService.handleAction(player, payload.action(), pos1, pos2);
        });
    }

    private static void handlePointerAction(final C2SPointerActionPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ItemStack stack = player.getMainHandItem();
            if (!(stack.getItem() instanceof PointerItem)) {
                stack = player.getOffhandItem();
            }
            PointerInteractionService.handlePayloadAction(player, stack, payload.action(), payload.targetEntity(), payload.targetPos());
        });
    }

    private static void handleFormationAction(final C2SFormationActionPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            FormationService.handleFormationAction(player, payload.action(), payload.param1(), payload.param2(),
                    payload.paramString(), payload.paramUUID());
        });
    }

    private static void handleDeskOpenShip(final C2SDeskOpenShipPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            DeskInteractionService.openOwnedShipFromDesk(player, payload.shipUuid());
        });
    }

    private static void handleDeskSummon(final C2SDeskSummonPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            DeskInteractionService.summonOwnedShipsToDesk(player, payload.shipUuids());
        });
    }

    private static void handleTeamDiplomacy(final C2STeamDiplomacyPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            TeamDiplomacyService.handleAction(player, payload.action(), payload.targetUuid());
        });
    }
}
