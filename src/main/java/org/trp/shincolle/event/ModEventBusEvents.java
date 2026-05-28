package org.trp.shincolle.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.trp.shincolle.Config;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.command.ModCommands;
import org.trp.shincolle.entity.EntityAirfieldHime;
import org.trp.shincolle.entity.EntityBattleshipRu;
import org.trp.shincolle.entity.EntityDestroyerIkazuchi;
import org.trp.shincolle.entity.EntityNorthernHime;
import org.trp.shincolle.entity.EntityAircraftBase;
import org.trp.shincolle.entity.base.EntityShincolleSimpleMob;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.entity.base.EntityShipBaseSimple;
import org.trp.shincolle.init.ModEntities;
import org.trp.shincolle.server.HostileDropService;
import org.trp.shincolle.server.MarriageRingService;
import org.trp.shincolle.server.PlayerStateService;
import org.trp.shincolle.server.PointerInteractionService;
import org.trp.shincolle.utility.PerformanceTrace;

@EventBusSubscriber(modid = Shincolle.MODID)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerTickPre(ServerTickEvent.Pre event) {
        PerformanceTrace.beginServerTick(event.getServer().getTickCount());
    }

    @SubscribeEvent
    public static void onServerTickPost(ServerTickEvent.Post event) {
        PerformanceTrace.endServerTick();
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.NORTHERN_HIME.get(), EntityNorthernHime.createAttributes().build());
        event.put(ModEntities.DESTROYER_IKAZUCHI.get(), EntityDestroyerIkazuchi.createAttributes().build());
        event.put(ModEntities.AIRFIELD_HIME.get(), EntityAirfieldHime.createAttributes().build());
        event.put(ModEntities.BATTLESHIP_RU.get(), EntityBattleshipRu.createAttributes().build());

        event.put(ModEntities.BATTLESHIP_HIME.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.BATTLESHIP_NAGATO.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.BATTLESHIP_RE.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.BATTLESHIP_TA.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.BATTLESHIP_YAMATO.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.BB_HARUNA.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.BB_HIEI.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.BB_KIRISHIMA.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.BB_KONGOU.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.CA_HIME.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.CARRIER_AKAGI.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.CARRIER_HIME.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.CARRIER_KAGA.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.CARRIER_W_DEMON.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.CARRIER_WO.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.CRUISER_ATAGO.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.CRUISER_TAKAO.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.CRUISER_TATSUTA.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.CRUISER_TENRYUU.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.DESTROYER_AKATSUKI.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.DESTROYER_HA.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.DESTROYER_HIBIKI.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.DESTROYER_HIME.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.DESTROYER_I.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.DESTROYER_INAZUMA.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.DESTROYER_NI.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.DESTROYER_RO.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.DESTROYER_SHIMAKAZE.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.HARBOUR_HIME.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.HEAVY_CRUISER_NE.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.HEAVY_CRUISER_RI.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.ISOLATED_HIME.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.MIDWAY_HIME.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.SSNH.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.SUBM_HIME.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.SUBM_KA.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.SUBM_RO500.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.SUBM_SO.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.SUBM_U511.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.SUBM_YO.get(), EntityShipBaseSimple.createAttributes().build());
        event.put(ModEntities.TRANSPORT_WA.get(), EntityShipBaseSimple.createAttributes().build());

        event.put(ModEntities.AIRPLANE.get(), EntityAircraftBase.createAttributes().build());
        event.put(ModEntities.AIRPLANE_T.get(), EntityAircraftBase.createAttributes().build());
        event.put(ModEntities.AIRPLANE_ZERO.get(), EntityAircraftBase.createAttributes().build());
        event.put(ModEntities.MOUNT_AF_H.get(), EntityShincolleSimpleMob.createAttributes().build());
        event.put(ModEntities.MOUNT_BA_H.get(), EntityShincolleSimpleMob.createAttributes().build());
        event.put(ModEntities.MOUNT_CA_H.get(), EntityShincolleSimpleMob.createAttributes().build());
        event.put(ModEntities.MOUNT_CA_WD.get(), EntityShincolleSimpleMob.createAttributes().build());
        event.put(ModEntities.MOUNT_HB_H.get(), EntityShincolleSimpleMob.createAttributes().build());
        event.put(ModEntities.MOUNT_IS_H.get(), EntityShincolleSimpleMob.createAttributes().build());
        event.put(ModEntities.MOUNT_MI_H.get(), EntityShincolleSimpleMob.createAttributes().build());
        event.put(ModEntities.MOUNT_SU_H.get(), EntityShincolleSimpleMob.createAttributes().build());
        event.put(ModEntities.RENSOUHOU.get(), EntityShincolleSimpleMob.createAttributes().build());
        event.put(ModEntities.RENSOUHOU_S.get(), EntityShincolleSimpleMob.createAttributes().build());
        event.put(ModEntities.TAKOYAKI.get(), EntityAircraftBase.createAttributes().build());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        HostileSpawnManager.tickPlayer(player);

        if (player.level().isClientSide) {
            return;
        }

        MarriageRingService.applyTickAbilities(player);
    }

    @SubscribeEvent
    public static void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
        float multiplier = MarriageRingService.getUnderwaterBreakSpeedMultiplier(event.getEntity());
        if (multiplier > 1.0F) {
            event.setNewSpeed(event.getOriginalSpeed() * multiplier);
        }
    }

    @SubscribeEvent
    public static void onPlayerIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof net.minecraft.world.entity.player.Player player
                && MarriageRingService.shouldCancelFireDamage(player, event.getSource())) {
            if (player.isOnFire()) {
                player.clearFire();
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            PlayerStateService.giveInitialManualIfNeeded(serverPlayer);
            PlayerStateService.syncAdmiralState(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            PlayerStateService.syncAdmiralState(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            PlayerStateService.syncAdmiralState(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerStateService.copyPersistentPlayerState(event.getOriginal(), event.getEntity());
    }

    @SubscribeEvent
    public static void onPointerItemAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player == null) {
            return;
        }

        ItemStack pointerStack = PointerInteractionService.getPointerStack(player);
        if (pointerStack.isEmpty()) {
            return;
        }

        if (player.level().isClientSide) {
            return;
        }

        event.setCanceled(true);
        PointerInteractionService.handleAttackSelection(player, pointerStack, event.getTarget());
    }

    @SubscribeEvent
    public static void onPointerItemLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (player == null) return;
        ItemStack pointerStack = PointerInteractionService.getPointerStack(player);
        if (pointerStack.isEmpty()) return;

        if (player.level().isClientSide) {
            return;
        }

        if (player.isShiftKeyDown()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPointerItemRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack pointerStack = player == null ? ItemStack.EMPTY : PointerInteractionService.getPointerStack(player);
        if (player == null || pointerStack.isEmpty() || player.isShiftKeyDown()) {
            return;
        }

        PointerInteractionService.handleTargetCommand(player, pointerStack);
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide));
    }

    @SubscribeEvent
    public static void onPointerItemRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack pointerStack = player == null ? ItemStack.EMPTY : PointerInteractionService.getPointerStack(player);
        if (player == null || pointerStack.isEmpty() || player.isShiftKeyDown()) {
            return;
        }

        PointerInteractionService.handleTargetCommand(player, pointerStack);
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide));
    }


    @SubscribeEvent
    public static void onHostileEntityDropsGrudge(LivingDropsEvent event) {
        HostileDropService.handleLivingDrops(event);
    }

}
