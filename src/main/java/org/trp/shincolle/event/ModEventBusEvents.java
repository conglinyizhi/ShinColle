package org.trp.shincolle.event;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
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
import org.trp.shincolle.block.entity.CraneBlockEntity;
import org.trp.shincolle.command.ModCommands;
import org.trp.shincolle.entity.EntityAirfieldHime;
import org.trp.shincolle.entity.EntityBattleshipRu;
import org.trp.shincolle.entity.EntityDestroyerIkazuchi;
import org.trp.shincolle.entity.EntityNorthernHime;
import org.trp.shincolle.entity.EntityAircraftBase;
import org.trp.shincolle.entity.base.EntityShincolleSimpleMob;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.entity.base.EntityShipBaseSimple;
import org.trp.shincolle.init.ModDataAttachments;
import org.trp.shincolle.init.ModEntities;
import org.trp.shincolle.init.ModItems;
import org.trp.shincolle.item.MarriageRingItem;
import org.trp.shincolle.item.PointerItem;
import org.trp.shincolle.network.ModNetwork;
import org.trp.shincolle.utility.FormationHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@EventBusSubscriber(modid = Shincolle.MODID)
public class ModEventBusEvents {

    private static final double POINTER_SEARCH_RADIUS = 100.0;
    private static final long POINTER_TARGET_DURATION_TICKS = 20L * 60L * 5L;
    private static final double POINTER_TARGET_SAME_DISTANCE_SQR = 0.25D;

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

        int marriedCount = getOwnedMarriedShipCount(player);
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
            org.trp.shincolle.attachment.AdmiralData data = serverPlayer.getData(org.trp.shincolle.init.ModDataAttachments.ADMIRAL_DATA);
            if (!data.hasReceivedBook()) {
                ItemStack bookStack = new ItemStack(ModItems.DESK_ITEM_BOOK.get());
                if (!serverPlayer.addItem(bookStack)) {
                    serverPlayer.drop(bookStack, false);
                }
                data.setHasReceivedBook(true);
            }
            syncPlayerAdmiralState(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            syncPlayerAdmiralState(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            syncPlayerAdmiralState(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        var originalData = event.getOriginal().getData(ModDataAttachments.ADMIRAL_DATA);
        var clonedData = event.getEntity().getData(ModDataAttachments.ADMIRAL_DATA);
        clonedData.deserializeNBT(originalData.serializeNBT());

        HashSet<Integer> originalCollected = event.getOriginal().getData(ModDataAttachments.COLLECTED_SHIPS);
        HashSet<Integer> clonedCollected = event.getEntity().getData(ModDataAttachments.COLLECTED_SHIPS);
        clonedCollected.clear();
        clonedCollected.addAll(originalCollected);
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

        if (player.isShiftKeyDown()) {
            if (pointerStack.getItem() instanceof PointerItem pointerItem) {
                int next = pointerItem.cycleMode(pointerStack);
                PointerItem.updateServerSideMode(player, pointerStack, next);
            }
            event.setCanceled(true);
            return;
        }

        event.setCanceled(true);

        Entity targetEntity = event.getTarget();
        EntityShipBase ship;
        if (targetEntity instanceof EntityShipBase targetShip) {
            ship = targetShip;
        } else if (targetEntity instanceof org.trp.shincolle.entity.base.EntityMountBase mount && mount.getHost() instanceof EntityShipBase hostShip) {
            ship = hostShip;
        } else {
            return;
        }

        if (!ship.isAlive() || ship.isInDeadPose() || !ship.isOwnedBy(player)) {
            return;
        }

        int mode = pointerStack.getItem() instanceof PointerItem pi ? pi.getMode(pointerStack) : PointerItem.MODE_SINGLE;
        if (mode == PointerItem.MODE_GROUP || mode == PointerItem.MODE_FORMATION) {
            org.trp.shincolle.attachment.AdmiralData data = player.getData(org.trp.shincolle.init.ModDataAttachments.ADMIRAL_DATA);
            int teamId = data.getCurrentTeamID();
            int existingTeam = -1;
            int existingSlot = -1;
            for (int t = 0; t < org.trp.shincolle.attachment.AdmiralData.TEAM_COUNT; t++) {
                for (int s = 0; s < org.trp.shincolle.attachment.AdmiralData.SLOT_COUNT; s++) {
                    if (ship.getUUID().equals(data.getShipUUID(t, s))) {
                        existingTeam = t;
                        existingSlot = s;
                        break;
                    }
                }
                if (existingTeam != -1) break;
            }

            if (existingTeam != -1) {
                if (existingTeam == teamId) {
                    if (mode == PointerItem.MODE_FORMATION) {
                        data.setShipUUID(teamId, existingSlot, null);
                        data.setSelected(teamId, existingSlot, true);
                        ship.setFormationTeam(-1);
                        ship.setFormationSlot(-1);
                        ship.setPointerSelected(false);
                    } else {
                        boolean nextState = !data.isSelected(teamId, existingSlot);
                        data.setSelected(teamId, existingSlot, nextState);
                        ship.setPointerSelected(nextState);
                    }
                } else {
                    if (mode == PointerItem.MODE_FORMATION) {
                        int assignedSlot = data.assignShipToTeam(teamId, ship.getUUID());
                        if (assignedSlot != -1) {
                            ship.setFormationTeam(teamId);
                            ship.setFormationSlot(assignedSlot);
                            ship.setPointerSelected(true);
                        } else {
                            player.displayClientMessage(net.minecraft.network.chat.Component.translatable("chat.shincolle.formation.teamfull"), false);
                        }
                    } else {
                        ship.togglePointerSelected();
                    }
                }
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer((net.minecraft.server.level.ServerPlayer) player, org.trp.shincolle.network.S2CAdmiralDataSyncPayload.of(
                        data.serializeNBT(),
                        player.getData(org.trp.shincolle.init.ModDataAttachments.COLLECTED_SHIPS)
                ));
            } else if (mode == PointerItem.MODE_FORMATION) {
                int assignedSlot = data.assignShipToTeam(teamId, ship.getUUID());
                if (assignedSlot != -1) {
                    ship.setFormationTeam(teamId);
                    ship.setFormationSlot(assignedSlot);
                    ship.setPointerSelected(true);
                    net.neoforged.neoforge.network.PacketDistributor.sendToPlayer((net.minecraft.server.level.ServerPlayer) player, org.trp.shincolle.network.S2CAdmiralDataSyncPayload.of(
                            data.serializeNBT(),
                            player.getData(org.trp.shincolle.init.ModDataAttachments.COLLECTED_SHIPS)
                    ));
                } else {
                    player.displayClientMessage(net.minecraft.network.chat.Component.translatable("chat.shincolle.formation.teamfull"), false);
                }
            } else {
                ship.togglePointerSelected();
            }

            if (!ship.isPointerSelected()) {
                ship.clearPointerTarget();
                ship.clearPointerTargetEntity();
            }
            return;
        }

        PointerItem.clearOwnedPointerSelection(player, ship, POINTER_SEARCH_RADIUS);
        PointerItem.updateServerSideMode(player, pointerStack, PointerItem.MODE_SINGLE);
        ship.setPointerSelected(true);
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

        handlePointerTargetCommand(player, pointerStack);
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

        handlePointerTargetCommand(player, pointerStack);
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

        int marriedCount = getOwnedMarriedShipCount(player);

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

    private static int getOwnedMarriedShipCount(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return reconcileOwnedMarriedShipCount(serverPlayer);
        }

        int stored = player.getData(ModDataAttachments.ADMIRAL_DATA).getMarriedShipCount();
        if (stored > 0) {
            return stored;
        }

        UUID ownerId = player.getUUID();
        AABB search = player.getBoundingBox().inflate(256.0D, 128.0D, 256.0D);
        List<EntityShipBase> ships = player.level().getEntitiesOfClass(EntityShipBase.class, search,
                ship -> ship.isAlive()
                        && ship.isTame()
                        && ship.isStateMarried()
                        && Objects.equals(ship.getOwnerUUID(), ownerId));
        int scanned = ships.size();
        if (scanned > 0) {
            player.getData(ModDataAttachments.ADMIRAL_DATA).setMarriedShipCount(scanned);
        }
        return scanned;
    }

    private static int reconcileOwnedMarriedShipCount(ServerPlayer serverPlayer) {
        UUID ownerId = serverPlayer.getUUID();
        int marriedCount = 0;
        for (ServerLevel level : serverPlayer.server.getAllLevels()) {
            marriedCount += level.getEntitiesOfClass(
                    EntityShipBase.class,
                    AABB.INFINITE,
                    ship -> ship.isAlive()
                            && ship.isTame()
                            && ship.isStateMarried()
                            && Objects.equals(ship.getOwnerUUID(), ownerId)
            ).size();
        }

        serverPlayer.getData(ModDataAttachments.ADMIRAL_DATA).setMarriedShipCount(marriedCount);
        return marriedCount;
    }



    private static void handlePointerTargetCommand(Player player, ItemStack pointerStack) {
        if (player == null || player.level().isClientSide) {
            return;
        }

        if (pointerStack.isEmpty()) {
            return;
        }

        AABB searchArea = player.getBoundingBox().inflate(POINTER_SEARCH_RADIUS);
        List<EntityShipBase> ships = player.level().getEntitiesOfClass(EntityShipBase.class, searchArea,
                ship -> ship.isOwnedBy(player) && ship.isPointerSelected() && !ship.isInDeadPose());
        if (ships.isEmpty()) {
            return;
        }

        if (!(pointerStack.getItem() instanceof PointerItem pointerItem)) {
            return;
        }

        int mode = pointerItem.getMode(pointerStack);
        if (mode == PointerItem.MODE_SINGLE && ships.size() > 1) {
            ships.sort((a, b) -> Double.compare(a.distanceToSqr(player), b.distanceToSqr(player)));
            EntityShipBase selected = ships.get(0);
            PointerItem.updateServerSideMode(player, pointerStack, PointerItem.MODE_SINGLE);
            ships = List.of(selected);
        } else if (mode == PointerItem.MODE_FORMATION) {
            org.trp.shincolle.attachment.AdmiralData data = player.getData(org.trp.shincolle.init.ModDataAttachments.ADMIRAL_DATA);
            int tid = data.getCurrentTeamID();
            ships = player.level().getEntitiesOfClass(EntityShipBase.class, searchArea,
                    ship -> ship.isOwnedBy(player) && ship.getFormationTeam() == tid && !ship.isInDeadPose());
        }

        EntityHitResult hitRes = getLookTargetResult(player);
        if (hitRes != null) {
            Entity target = hitRes.getEntity();
            if (player.isShiftKeyDown()) {
                Entity guardTarget = target;
                if (guardTarget instanceof org.trp.shincolle.entity.base.EntityMountBase mount && mount.getHost() != null) {
                    guardTarget = mount.getHost();
                }
                if (guardTarget instanceof EntityShipBase ownShip && ownShip.isOwnedBy(player)) {
                    for (EntityShipBase ship : ships) {
                        FormationHelper.applyShipGuardEntity(ship, ownShip);
                        ship.clearPointerTarget();
                        ship.clearPointerTargetEntity();
                    }
                }
                return;
            }
            if (target == player || target instanceof EntityShipBase ship && ship.isOwnedBy(player)) {
                return;
            }
            for (EntityShipBase ship : ships) {
                if (!ModNetwork.canAssignPointerEntityTarget(player, ship, target)) {
                    continue;
                }
                if (ship.hasPointerTargetEntity() && ship.getPointerTargetEntity() == target) {
                    ship.clearPointerTargetEntity();
                    ship.clearPointerTarget();
                    continue;
                }
                ship.setPointerTargetEntity(target, POINTER_TARGET_DURATION_TICKS);
            }
            return;
        }

        Vec3 target = getLookTarget(player);
        if (target == null) {
            return;
        }
        BlockHitResult blockHit = getLookBlockResult(player);
        BlockPos guardPos = null;
        if (blockHit != null && player.level().getBlockEntity(blockHit.getBlockPos()) instanceof org.trp.shincolle.block.entity.IWaypoint wp) {
            BlockPos resolved = resolveWaypointTarget(player.level(), blockHit.getBlockPos(), wp);
            guardPos = resolved;
            target = Vec3.atBottomCenterOf(resolved);
        }
        for (EntityShipBase ship : ships) {
            if (ship.hasPointerTarget() && isSamePointerTarget(ship.getPointerTarget(), target)) {
                ship.clearPointerTarget();
                continue;
            }
            ship.setPointerTarget(target, POINTER_TARGET_DURATION_TICKS);

            
            if (guardPos != null) {
                ship.setGuardBlockTarget(guardPos);
                ship.setStateFlag(EntityShipBase.STATE_FLAG_DISABLE_GUARD_POS, false); 
            }
        }
    }

    private static boolean isSamePointerTarget(Vec3 current, Vec3 next) {
        if (current == null || next == null) {
            return false;
        }
        return current.distanceToSqr(next) <= POINTER_TARGET_SAME_DISTANCE_SQR;
    }

    private static EntityHitResult getLookTargetEntity(Player player) {
        double reach = POINTER_SEARCH_RADIUS;
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = eyePos.add(look.x * reach, look.y * reach, look.z * reach);
        AABB searchBox = player.getBoundingBox().expandTowards(look.scale(reach)).inflate(1.0D);
        return ProjectileUtil.getEntityHitResult(player.level(), player, eyePos, end, searchBox,
                entity -> !entity.isSpectator() && entity.isPickable() && entity != player);
    }

    private static Vec3 getLookTarget(Player player) {
        BlockHitResult hit = getLookBlockResult(player);
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return null;
        }
        
        BlockPos pos = hit.getBlockPos();
        if (player.level().getBlockEntity(pos) instanceof org.trp.shincolle.block.entity.IWaypoint) {
            return Vec3.atBottomCenterOf(pos).add(0.0, 1.0, 0.0);
        }
        return Vec3.atBottomCenterOf(pos).add(0.0, 1.0, 0.0);
    }

    private static BlockHitResult getLookBlockResult(Player player) {
        double reach = POINTER_SEARCH_RADIUS;
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = eyePos.add(look.x * reach, look.y * reach, look.z * reach);
        return player.level().clip(new ClipContext(eyePos, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, player));
    }

    private static BlockPos resolveWaypointTarget(Level level, BlockPos waypointPos, org.trp.shincolle.block.entity.IWaypoint waypoint) {
        BlockPos next = waypoint.getNextPos();
        if (isCraneTarget(level, next)) {
            return next;
        }
        BlockPos chest = waypoint.getChestPos();
        if (isCraneTarget(level, chest)) {
            return chest;
        }
        return waypointPos;
    }

    private static boolean isCraneTarget(Level level, BlockPos pos) {
        if (pos == null || pos.equals(BlockPos.ZERO)) {
            return false;
        }
        return level.getBlockEntity(pos) instanceof CraneBlockEntity;
    }

    public static EntityHitResult getLookTargetResult(Player player) {
        double reach = POINTER_SEARCH_RADIUS;
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = eyePos.add(look.x * reach, look.y * reach, look.z * reach);
        AABB searchBox = player.getBoundingBox().expandTowards(look.scale(reach)).inflate(1.0D);
        return ProjectileUtil.getEntityHitResult(player.level(), player, eyePos, end, searchBox,
                entity -> !entity.isSpectator() && entity.isPickable() && entity != player);
    }

    private static void syncPlayerAdmiralState(ServerPlayer serverPlayer) {
        reconcileOwnedMarriedShipCount(serverPlayer);
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
                serverPlayer,
                org.trp.shincolle.network.S2CAdmiralDataSyncPayload.of(
                        serverPlayer.getData(org.trp.shincolle.init.ModDataAttachments.ADMIRAL_DATA).serializeNBT(),
                        serverPlayer.getData(org.trp.shincolle.init.ModDataAttachments.COLLECTED_SHIPS)
                )
        );
    }
}
