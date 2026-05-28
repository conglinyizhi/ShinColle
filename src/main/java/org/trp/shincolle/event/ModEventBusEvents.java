package org.trp.shincolle.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
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
import org.trp.shincolle.init.ModItems;
import org.trp.shincolle.item.MarriageRingItem;
import org.trp.shincolle.server.PlayerStateService;
import org.trp.shincolle.server.PointerInteractionService;

@EventBusSubscriber(modid = Shincolle.MODID)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
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

        applyMarriageRingAbilities(player);
    }

    @SubscribeEvent
    public static void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player == null || Config.ringAbilityUnderwaterDigCap <= 0) {
            return;
        }

        if (!hasActiveMarriageRing(player) || !player.isInWaterOrBubble()) {
            return;
        }

        int marriedCount = PlayerStateService.getOwnedMarriedShipCount(player);
        if (marriedCount <= 0) {
            return;
        }

        int effectiveCount = Math.min(marriedCount, Config.ringAbilityUnderwaterDigCap);
        float digBoost = effectiveCount * 0.2F + 1.0F;
        event.setNewSpeed(event.getOriginalSpeed() * 5.0F * digBoost);
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

        ItemStack pointerStack = getPointerStack(player);
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
        ItemStack pointerStack = getPointerStack(player);
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
        ItemStack pointerStack = player == null ? ItemStack.EMPTY : getPointerStack(player);
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
        ItemStack pointerStack = player == null ? ItemStack.EMPTY : getPointerStack(player);
        if (player == null || pointerStack.isEmpty() || player.isShiftKeyDown()) {
            return;
        }

        PointerInteractionService.handleTargetCommand(player, pointerStack);
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide));
    }


    @SubscribeEvent
    public static void onHostileEntityDropsGrudge(LivingDropsEvent event) {
        Entity target = event.getEntity();
        if (target.level().isClientSide) {
            return;
        }

        if (!isHostileDropTarget(target)) {
            return;
        }

        if (!target.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            return;
        }

        Entity sourceEntity = event.getSource().getEntity();
        if (sourceEntity instanceof EntityShipBase ship) {
            ship.addShipExp(Config.shipExpGainKill);
        }

        float dropRate = Math.max(0.0F, Config.hostileDropGrudgeRate);
        if (dropRate <= 0.0F) {
            return;
        }

        int fixedDrop = (int) dropRate;
        if (fixedDrop > 0) {
            event.getDrops().add(new ItemEntity(target.level(),
                    target.getX(), target.getY(), target.getZ(), new ItemStack(ModItems.GRUDGE.get(), fixedDrop)));
        }

        if (target.getRandom().nextFloat() < (dropRate - fixedDrop)) {
            event.getDrops().add(new ItemEntity(target.level(),
                    target.getX(), target.getY(), target.getZ(), new ItemStack(ModItems.GRUDGE.get())));
        }
    }

    private static boolean isHostileDropTarget(Entity entity) {
        if (entity instanceof EntityShipBase ship) {
            return ship.isHostileShipMob();
        }
        return entity instanceof Enemy || entity instanceof Slime || entity instanceof AbstractGolem;
    }

    private static ItemStack getPointerStack(Player player) {
        ItemStack main = player.getMainHandItem();
        if (isPointerItem(main)) {
            return main;
        }
        ItemStack off = player.getOffhandItem();
        if (isPointerItem(off)) {
            return off;
        }
        return ItemStack.EMPTY;
    }

    private static boolean isPointerItem(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModItems.POINTER_ITEM.get());
    }

    private static void applyMarriageRingAbilities(Player player) {
        if (!hasActiveMarriageRing(player)) {
            return;
        }

        int marriedCount = PlayerStateService.getOwnedMarriedShipCount(player);

        if (Config.ringAbilityWaterBreathing >= 0
                && marriedCount >= Config.ringAbilityWaterBreathing
                && player.isInWaterOrBubble()
                && player.getAirSupply() < player.getMaxAirSupply()) {
            player.setAirSupply(player.getMaxAirSupply());
        }

        if (Config.ringAbilityFireImmunity >= 0
                && marriedCount >= Config.ringAbilityFireImmunity
                && (player.isOnFire() || player.getRemainingFireTicks() > 0)) {
            player.clearFire();
        }
    }

    private static boolean hasActiveMarriageRing(Player player) {
        return findActiveMarriageRing(player) != ItemStack.EMPTY;
    }

    private static ItemStack findActiveMarriageRing(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (isActiveMarriageRingStack(stack)) {
                return stack;
            }
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (isActiveMarriageRingStack(stack)) {
                return stack;
            }
        }

        for (ItemStack stack : player.getInventory().armor) {
            if (isActiveMarriageRingStack(stack)) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    private static boolean isActiveMarriageRingStack(ItemStack stack) {
        Item item = stack.getItem();
        return !stack.isEmpty()
                && item == ModItems.MARRIAGE_RING.get()
                && item instanceof MarriageRingItem
                && MarriageRingItem.isActive(stack);
    }

}
