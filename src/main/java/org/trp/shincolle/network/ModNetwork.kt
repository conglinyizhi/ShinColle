package org.trp.shincolle.network

import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.phys.Vec3
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.handling.IPayloadHandler
import org.trp.shincolle.Shincolle
import org.trp.shincolle.server.DeskInteractionService.openOwnedShipFromDesk
import org.trp.shincolle.server.DeskInteractionService.summonOwnedShipsToDesk
import org.trp.shincolle.server.DeskInteractionService.updateBookState
import org.trp.shincolle.server.DeskInteractionService.updateDeskGui
import org.trp.shincolle.server.FormationService.handleFormationAction
import org.trp.shincolle.server.PlayerStateService.applyAdmiralSync
import org.trp.shincolle.server.PlayerStateService.refreshClientPointerSelection
import org.trp.shincolle.server.PointerInteractionService.getPointerStack
import org.trp.shincolle.server.PointerInteractionService.handlePayloadAction
import org.trp.shincolle.server.TeamDiplomacyService.handleAction
import org.trp.shincolle.server.WaypointService.handleAction
import java.util.*
import java.util.List

@EventBusSubscriber(modid = Shincolle.MODID)
object ModNetwork {
    @JvmStatic
    fun sendToServer(payload: CustomPacketPayload) {
        PacketDistributor.sendToServer(payload)
    }

    @SubscribeEvent
    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar(Shincolle.MODID)
        registrar.playToServer<C2SBookStatePayload>(
            C2SBookStatePayload.Companion.TYPE,
            C2SBookStatePayload.Companion.STREAM_CODEC,
            IPayloadHandler { payload: C2SBookStatePayload, context: IPayloadContext ->
                ModNetwork.handleBookState(payload, context)
            }
        )
        registrar.playToServer<C2SDeskGuiPayload>(
            C2SDeskGuiPayload.Companion.TYPE,
            C2SDeskGuiPayload.Companion.STREAM_CODEC,
            IPayloadHandler { payload: C2SDeskGuiPayload, context: IPayloadContext ->
                ModNetwork.handleDeskGui(payload, context)
            }
        )
        registrar.playToServer<C2SWaypointActionPayload>(
            C2SWaypointActionPayload.Companion.TYPE,
            C2SWaypointActionPayload.Companion.STREAM_CODEC,
            IPayloadHandler { payload: C2SWaypointActionPayload, context: IPayloadContext ->
                ModNetwork.handleWaypointAction(payload, context)
            }
        )
        registrar.playToServer<C2SPointerActionPayload>(
            C2SPointerActionPayload.Companion.TYPE,
            C2SPointerActionPayload.Companion.STREAM_CODEC,
            IPayloadHandler { payload: C2SPointerActionPayload, context: IPayloadContext ->
                ModNetwork.handlePointerAction(payload, context)
            }
        )
        registrar.playToServer<C2SFormationActionPayload>(
            C2SFormationActionPayload.Companion.TYPE,
            C2SFormationActionPayload.Companion.STREAM_CODEC,
            IPayloadHandler { payload: C2SFormationActionPayload, context: IPayloadContext ->
                ModNetwork.handleFormationAction(payload, context)
            }
        )
        registrar.playToServer<C2SDeskOpenShipPayload>(
            C2SDeskOpenShipPayload.Companion.TYPE,
            C2SDeskOpenShipPayload.Companion.STREAM_CODEC,
            IPayloadHandler { payload: C2SDeskOpenShipPayload, context: IPayloadContext ->
                ModNetwork.handleDeskOpenShip(payload, context)
            }
        )
        registrar.playToServer<C2SDeskSummonPayload>(
            C2SDeskSummonPayload.Companion.TYPE,
            C2SDeskSummonPayload.Companion.STREAM_CODEC,
            IPayloadHandler { payload: C2SDeskSummonPayload, context: IPayloadContext ->
                ModNetwork.handleDeskSummon(payload, context)
            }
        )
        registrar.playToServer<C2STeamDiplomacyPayload>(
            C2STeamDiplomacyPayload.Companion.TYPE,
            C2STeamDiplomacyPayload.Companion.STREAM_CODEC,
            IPayloadHandler { payload: C2STeamDiplomacyPayload, context: IPayloadContext ->
                ModNetwork.handleTeamDiplomacy(payload, context)
            }
        )
        registrar.playToClient<S2CAdmiralDataSyncPayload>(
            S2CAdmiralDataSyncPayload.Companion.TYPE,
            S2CAdmiralDataSyncPayload.Companion.STREAM_CODEC,
            IPayloadHandler { payload: S2CAdmiralDataSyncPayload, context: IPayloadContext ->
                ModNetwork.handleAdmiralDataSync(payload, context)
            }
        )
        registrar.playToClient<S2CDeskDiplomacySyncPayload>(
            S2CDeskDiplomacySyncPayload.Companion.TYPE,
            S2CDeskDiplomacySyncPayload.Companion.STREAM_CODEC,
            IPayloadHandler { payload: S2CDeskDiplomacySyncPayload, context: IPayloadContext ->
                ModNetwork.handleDeskDiplomacySync(payload, context)
            }
        )
    }

    private fun handleAdmiralDataSync(payload: S2CAdmiralDataSyncPayload, context: IPayloadContext) {
        context.enqueueWork(Runnable {
            val player = context.player()
            val nbt = payload.admiralNbt
            val ships = payload.collectedShips
            if (player != null && nbt != null && ships != null) {
                applyAdmiralSync(player, nbt, ships)
                refreshClientPointerSelection(player)
            }
        })
    }

    private fun handleDeskDiplomacySync(payload: S2CDeskDiplomacySyncPayload, context: IPayloadContext) {
        context.enqueueWork(Runnable {
            DeskDiplomacySync.update(
                payload.ownerUuid,
                listOf(*payload.allies),
                listOf(*payload.banned),
                listOf(*payload.displayUuids),
                listOf(*payload.displayTeamNames),
                listOf(*payload.displayLeaderNames)
            )
        })
    }

    private fun handleBookState(payload: C2SBookStatePayload, context: IPayloadContext) {
        context.enqueueWork(Runnable {
            val player = context.player()
            if (player != null) {
                updateBookState(player, payload.chapter, payload.page)
            }
        })
    }

    private fun handleDeskGui(payload: C2SDeskGuiPayload, context: IPayloadContext) {
        context.enqueueWork(Runnable {
            val player = context.player()
            if (player != null) {
                updateDeskGui(player, payload.guiFunc, payload.radarZoom)
            }
        })
    }

    private fun handleWaypointAction(payload: C2SWaypointActionPayload, context: IPayloadContext) {
        context.enqueueWork(Runnable {
            val player = context.player()
            if (player != null) {
                val pos1 = BlockPos(payload.x1, payload.y1, payload.z1)
                val pos2 = BlockPos(payload.x2, payload.y2, payload.z2)
                handleAction(player, payload.action, pos1, pos2)
            }
        })
    }

    private fun handlePointerAction(payload: C2SPointerActionPayload, context: IPayloadContext) {
        context.enqueueWork(Runnable {
            val player = context.player()
            if (player != null) {
                val stack = getPointerStack(player)
                handlePayloadAction(player, stack, payload.action, payload.targetEntity, payload.targetPos)
            }
        })
    }

    private fun handleFormationAction(payload: C2SFormationActionPayload, context: IPayloadContext) {
        context.enqueueWork(Runnable {
            val player = context.player()
            if (player != null) {
                handleFormationAction(
                    player, payload.action, payload.param1, payload.param2,
                    payload.paramString, payload.paramUUID
                )
            }
        })
    }

    private fun handleDeskOpenShip(payload: C2SDeskOpenShipPayload, context: IPayloadContext) {
        context.enqueueWork(Runnable {
            val player = context.player()
            if (player != null) {
                val shipUuid = payload.shipUuid
                if (shipUuid != null) {
                    openOwnedShipFromDesk(player, shipUuid)
                }
            }
        })
    }

    private fun handleDeskSummon(payload: C2SDeskSummonPayload, context: IPayloadContext) {
        context.enqueueWork(Runnable {
            val player = context.player()
            if (player != null) {
                summonOwnedShipsToDesk(player, payload.shipUuids)
            }
        })
    }

    private fun handleTeamDiplomacy(payload: C2STeamDiplomacyPayload, context: IPayloadContext) {
        context.enqueueWork(Runnable {
            val player = context.player()
            if (player != null) {
                handleAction(player, payload.action, payload.targetUuid)
            }
        })
    }
}
