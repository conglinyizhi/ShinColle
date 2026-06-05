package org.trp.shincolle.event

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent.*
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.*
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import org.trp.shincolle.Shincolle
import org.trp.shincolle.command.ModCommands
import org.trp.shincolle.entity.*
import org.trp.shincolle.entity.base.EntityShincolleSimpleMob
import org.trp.shincolle.entity.base.EntityShipBaseSimple
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.item.DebugInspectorItem.Companion.handleItemFrameInteract
import org.trp.shincolle.server.HostileDropService.handleLivingDrops
import org.trp.shincolle.server.MarriageRingService.applyTickAbilities
import org.trp.shincolle.server.MarriageRingService.getUnderwaterBreakSpeedMultiplier
import org.trp.shincolle.server.MarriageRingService.handleFireDamageEvent
import org.trp.shincolle.server.PlayerStateService.copyPersistentPlayerState
import org.trp.shincolle.server.PlayerStateService.giveInitialManualIfNeeded
import org.trp.shincolle.server.PlayerStateService.syncAdmiralState
import org.trp.shincolle.server.PointerInteractionService.handleLeftClickBlock
import org.trp.shincolle.server.PointerInteractionService.handlePointerAttack
import org.trp.shincolle.server.PointerInteractionService.handleRightClickBlock
import org.trp.shincolle.server.PointerInteractionService.handleRightClickItem
import org.trp.shincolle.utility.PerformanceTrace.beginServerTick
import org.trp.shincolle.utility.PerformanceTrace.endServerTick

@EventBusSubscriber(modid = Shincolle.MODID)
object ModEventBusEvents {
    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        ModCommands.register(event.getDispatcher())
    }

    @SubscribeEvent
    fun onServerTickPre(event: ServerTickEvent.Pre) {
        beginServerTick(event.getServer().getTickCount())
    }

    @SubscribeEvent
    fun onServerTickPost(event: ServerTickEvent.Post?) {
        endServerTick()
    }

    @SubscribeEvent
    fun registerAttributes(event: EntityAttributeCreationEvent) {
        event.put(ModEntities.NORTHERN_HIME.get(), EntityNorthernHime.createAttributes().build())
        event.put(ModEntities.DESTROYER_IKAZUCHI.get(), EntityDestroyerIkazuchi.createAttributes().build())
        event.put(ModEntities.AIRFIELD_HIME.get(), EntityAirfieldHime.createAttributes().build())
        event.put(ModEntities.BATTLESHIP_RU.get(), EntityBattleshipRu.createAttributes().build())

        event.put(ModEntities.BATTLESHIP_HIME.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.BATTLESHIP_NAGATO.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.BATTLESHIP_RE.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.BATTLESHIP_TA.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.BATTLESHIP_YAMATO.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.BB_HARUNA.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.BB_HIEI.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.BB_KIRISHIMA.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.BB_KONGOU.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CA_HIME.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CARRIER_AKAGI.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CARRIER_HIME.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CARRIER_KAGA.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CARRIER_W_DEMON.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CARRIER_WO.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CRUISER_ATAGO.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CRUISER_TAKAO.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CRUISER_TATSUTA.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CRUISER_TENRYUU.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_AKATSUKI.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_HA.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_HIBIKI.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_HIME.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_I.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_INAZUMA.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_NI.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_RO.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_SHIMAKAZE.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.HARBOUR_HIME.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.HEAVY_CRUISER_NE.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.HEAVY_CRUISER_RI.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.ISOLATED_HIME.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.MIDWAY_HIME.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.SSNH.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.SUBM_HIME.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.SUBM_KA.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.SUBM_RO500.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.SUBM_SO.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.SUBM_U511.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.SUBM_YO.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.TRANSPORT_WA.get(), EntityShipBaseSimple.createAttributes().build())

        event.put(ModEntities.AIRPLANE.get(), EntityAircraftBase.createAttributes().build())
        event.put(ModEntities.AIRPLANE_T.get(), EntityAircraftBase.createAttributes().build())
        event.put(ModEntities.AIRPLANE_ZERO.get(), EntityAircraftBase.createAttributes().build())
        event.put(ModEntities.MOUNT_AF_H.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.MOUNT_BA_H.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.MOUNT_CA_H.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.MOUNT_CA_WD.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.MOUNT_HB_H.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.MOUNT_IS_H.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.MOUNT_MI_H.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.MOUNT_SU_H.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.RENSOUHOU.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.RENSOUHOU_S.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.TAKOYAKI.get(), EntityAircraftBase.createAttributes().build())
    }

    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent.Post) {
        val player = event.getEntity()
        HostileSpawnManager.tickPlayer(player)

        if (player.level().isClientSide) {
            return
        }

        applyTickAbilities(player)
    }

    @SubscribeEvent
    fun onPlayerBreakSpeed(event: BreakSpeed) {
        val multiplier = getUnderwaterBreakSpeedMultiplier(event.getEntity())
        if (multiplier > 1.0f) {
            event.setNewSpeed(event.getOriginalSpeed() * multiplier)
        }
    }

    @SubscribeEvent
    fun onPlayerIncomingDamage(event: LivingIncomingDamageEvent) {
        if (event.getEntity() is Player
            && handleFireDamageEvent(player, event.getSource())
        ) {
            event.setCanceled(true)
        }
    }

    @SubscribeEvent
    fun onPlayerLogin(event: PlayerLoggedInEvent) {
        if (event.getEntity() is ServerPlayer) {
            giveInitialManualIfNeeded(serverPlayer)
            syncAdmiralState(serverPlayer)
        }
    }

    @SubscribeEvent
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        if (event.getEntity() is ServerPlayer) {
            syncAdmiralState(serverPlayer)
        }
    }

    @SubscribeEvent
    fun onPlayerChangedDimension(event: PlayerChangedDimensionEvent) {
        if (event.getEntity() is ServerPlayer) {
            syncAdmiralState(serverPlayer)
        }
    }

    @SubscribeEvent
    fun onPlayerClone(event: PlayerEvent.Clone) {
        copyPersistentPlayerState(event.getOriginal(), event.getEntity())
    }

    @SubscribeEvent
    fun onPointerItemAttack(event: AttackEntityEvent) {
        if (handlePointerAttack(event.getEntity(), event.getTarget())) {
            event.setCanceled(true)
        }
    }

    @SubscribeEvent
    fun onPointerItemLeftClickBlock(event: LeftClickBlock) {
        handleLeftClickBlock(event.getEntity(), event)
    }

    @SubscribeEvent
    fun onPointerItemRightClickItem(event: RightClickItem) {
        if (handleRightClickItem(event.getEntity(), event)) {
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getEntity().level().isClientSide))
        }
    }

    @SubscribeEvent
    fun onPointerItemRightClickBlock(event: RightClickBlock) {
        if (handleRightClickBlock(event.getEntity(), event)) {
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getEntity().level().isClientSide))
        }
    }


    @SubscribeEvent
    fun onHostileEntityDropsGrudge(event: LivingDropsEvent?) {
        handleLivingDrops(event)
    }

    @SubscribeEvent
    fun onEntityInteract(event: EntityInteract) {
        handleItemFrameInteract(event)
    }
}
