package org.trp.shincolle.server;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.trp.shincolle.attachment.AdmiralData;
import org.trp.shincolle.block.entity.CraneBlockEntity;
import org.trp.shincolle.block.entity.IWaypoint;
import org.trp.shincolle.entity.base.EntityMountBase;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.item.PointerItem;
import org.trp.shincolle.utility.FormationHelper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PointerInteractionService {
    public static final double POINTER_SEARCH_RADIUS = 100.0D;
    private static final long POINTER_TARGET_DURATION_TICKS = 20L * 60L * 5L;
    private static final double POINTER_TARGET_SAME_DISTANCE_SQR = 0.25D;

    private PointerInteractionService() {
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
        if (TeamDiplomacyService.isDiplomaticAlly(ship, target)) {
            return false;
        }
        return !TargetProtectionService.isUnattackableTargetClass(ship, livingTarget);
    }

    public static void handlePayloadAction(Player player, ItemStack pointerStack, int action,
                                           Optional<UUID> targetEntityUuid, Optional<Vec3> targetPos) {
        if (!(pointerStack.getItem() instanceof PointerItem pointerItem)) {
            return;
        }

        if (action == 0) {
            cyclePointerMode(player, pointerItem, pointerStack);
        } else if (action == 1 || action == 2) {
            assignFormationPointerTarget(player, pointerItem, pointerStack, action, targetEntityUuid, targetPos);
        } else if (action == 3) {
            targetEntityUuid.ifPresent(uuid -> openOwnedShipMenu(player, uuid));
        } else if (action == 4) {
            player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new org.trp.shincolle.menu.FormationMenu(id, inv),
                    net.minecraft.network.chat.Component.translatable("gui.shincolle.formation.title")
            ));
        } else if (action == 5) {
            targetEntityUuid.ifPresent(uuid -> FormationService.handlePointerRosterToggle(player, uuid));
        }
    }

    public static void handleAttackSelection(Player player, ItemStack pointerStack, Entity targetEntity) {
        if (player == null || pointerStack.isEmpty() || player.level().isClientSide) {
            return;
        }
        if (player.isShiftKeyDown()) {
            if (pointerStack.getItem() instanceof PointerItem pointerItem) {
                cyclePointerMode(player, pointerItem, pointerStack);
            }
            return;
        }

        EntityShipBase ship = asShipOrHostedShip(targetEntity);
        if (ship == null || !ship.isAlive() || ship.isInDeadPose() || !ship.isOwnedBy(player)) {
            return;
        }

        int mode = pointerStack.getItem() instanceof PointerItem pi ? pi.getMode(pointerStack) : PointerItem.MODE_SINGLE;
        if (mode == PointerItem.MODE_GROUP || mode == PointerItem.MODE_FORMATION) {
            toggleGroupedSelection(player, ship, mode);
            return;
        }

        clearOwnedPointerSelection(player, ship, POINTER_SEARCH_RADIUS);
        applyPointerModeSelectionState(player, PointerItem.MODE_SINGLE);
        ship.setPointerSelected(true);
    }

    public static void handleTargetCommand(Player player, ItemStack pointerStack) {
        if (player == null || player.level().isClientSide || pointerStack.isEmpty()) {
            return;
        }

        AABB searchArea = player.getBoundingBox().inflate(POINTER_SEARCH_RADIUS);
        List<EntityShipBase> ships = player.level().getEntitiesOfClass(EntityShipBase.class, searchArea,
                ship -> ship.isOwnedBy(player) && ship.isPointerSelected() && !ship.isInDeadPose());
        if (ships.isEmpty() || !(pointerStack.getItem() instanceof PointerItem pointerItem)) {
            return;
        }

        int mode = pointerItem.getMode(pointerStack);
        if (mode == PointerItem.MODE_SINGLE && ships.size() > 1) {
            ships.sort((a, b) -> Double.compare(a.distanceToSqr(player), b.distanceToSqr(player)));
            EntityShipBase selected = ships.get(0);
            applyPointerModeSelectionState(player, PointerItem.MODE_SINGLE);
            ships = List.of(selected);
        } else if (mode == PointerItem.MODE_FORMATION) {
            AdmiralData data = PlayerStateService.admiralData(player);
            int teamId = data.getCurrentTeamID();
            ships = player.level().getEntitiesOfClass(EntityShipBase.class, searchArea,
                    ship -> ship.isOwnedBy(player) && ship.getFormationTeam() == teamId && !ship.isInDeadPose());
        }

        EntityHitResult hitRes = getLookTargetResult(player);
        if (hitRes != null) {
            handleEntityTargetCommand(player, ships, hitRes.getEntity());
            return;
        }

        handleBlockTargetCommand(player, ships);
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

    private static void cyclePointerMode(Player player, PointerItem pointerItem, ItemStack pointerStack) {
        if (player.level().isClientSide) {
            return;
        }

        int nextMode = pointerItem.cycleMode(pointerStack);
        applyPointerModeSelectionState(player, nextMode);
        player.displayClientMessage(net.minecraft.network.chat.Component.translatable(PointerItem.getModeTranslationKey(nextMode)), true);
    }

    private static void applyPointerModeSelectionState(Player player, int nextMode) {
        if (player.level().isClientSide) {
            return;
        }

        if (nextMode == PointerItem.MODE_SINGLE) {
            List<EntityShipBase> ships = player.level().getEntitiesOfClass(EntityShipBase.class,
                    player.getBoundingBox().inflate(POINTER_SEARCH_RADIUS),
                    ship -> ship.isOwnedBy(player) && ship.isPointerSelected() && !ship.isInDeadPose());
            if (ships.size() > 1) {
                ships.sort((a, b) -> Double.compare(a.distanceToSqr(player), b.distanceToSqr(player)));
                EntityShipBase keep = ships.get(0);
                clearOwnedPointerSelection(player, keep, POINTER_SEARCH_RADIUS);
                keep.setPointerSelected(true);
            }
        } else if (nextMode == PointerItem.MODE_FORMATION) {
            AdmiralData data = PlayerStateService.admiralData(player);
            int teamId = data.getCurrentTeamID();
            List<EntityShipBase> ships = player.level().getEntitiesOfClass(EntityShipBase.class,
                    player.getBoundingBox().inflate(POINTER_SEARCH_RADIUS),
                    ship -> ship.isOwnedBy(player) && !ship.isInDeadPose());
            for (EntityShipBase ship : ships) {
                ship.setPointerSelected(ship.getFormationTeam() == teamId);
            }
        }
    }

    private static void clearOwnedPointerSelection(Player player, EntityShipBase keepSelected, double radius) {
        List<EntityShipBase> ships = player.level().getEntitiesOfClass(EntityShipBase.class,
                player.getBoundingBox().inflate(radius),
                ship -> ship.isOwnedBy(player) && ship.isPointerSelected() && !ship.isInDeadPose());
        for (EntityShipBase ship : ships) {
            if (ship == keepSelected) {
                continue;
            }
            ship.setPointerSelected(false);
            ship.clearPointerTarget();
            ship.clearPointerTargetEntity();
        }
    }

    private static void assignFormationPointerTarget(Player player, PointerItem pointerItem, ItemStack pointerStack,
                                                     int action, Optional<UUID> targetEntityUuid, Optional<Vec3> targetPos) {
        if (pointerItem.getMode(pointerStack) != PointerItem.MODE_FORMATION
                || !(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        AdmiralData data = PlayerStateService.admiralData(player);
        int teamId = data.getCurrentTeamID();
        for (int i = 0; i < AdmiralData.SLOT_COUNT; i++) {
            if (!data.isSelected(teamId, i)) {
                continue;
            }

            UUID shipUuid = data.getShipUUID(teamId, i);
            if (shipUuid == null) {
                continue;
            }

            Entity entity = serverLevel.getEntity(shipUuid);
            if (!(entity instanceof EntityShipBase ship)) {
                continue;
            }

            if (action == 1 && targetEntityUuid.isPresent()) {
                Entity target = serverLevel.getEntity(targetEntityUuid.get());
                if (canAssignPointerEntityTarget(player, ship, target)) {
                    ship.setPointerTargetEntity(target, POINTER_TARGET_DURATION_TICKS);
                }
            } else if (action == 2 && targetPos.isPresent()) {
                ship.setPointerTarget(targetPos.get(), POINTER_TARGET_DURATION_TICKS);
            }
        }
    }

    private static void openOwnedShipMenu(Player player, UUID shipUuid) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Entity entity = serverLevel.getEntity(shipUuid);
        if (entity instanceof EntityShipBase ship && ship.isOwnedBy(player)) {
            ship.openShipMenu(player);
        }
    }

    private static void toggleGroupedSelection(Player player, EntityShipBase ship, int mode) {
        AdmiralData data = PlayerStateService.admiralData(player);
        int teamId = data.getCurrentTeamID();
        int existingTeam = data.findShipTeam(ship.getUUID());
        int existingSlot = existingTeam >= 0 ? data.findShipSlot(existingTeam, ship.getUUID()) : -1;

        if (existingTeam != -1) {
            if (existingTeam == teamId) {
                if (mode == PointerItem.MODE_FORMATION) {
                    PlayerStateService.removeShipFromTeams(player, ship.getUUID());
                    FormationService.clearFormationState(ship);
                } else {
                    boolean nextState = !data.isSelected(teamId, existingSlot);
                    PlayerStateService.setCurrentTeamSlotSelected(player, existingSlot, nextState);
                    ship.setPointerSelected(nextState);
                }
            } else if (mode == PointerItem.MODE_FORMATION) {
                int assignedSlot = PlayerStateService.assignShipToCurrentTeam(player, ship.getUUID());
                if (assignedSlot != -1) {
                    FormationService.applyFormationState(ship, teamId, assignedSlot, true);
                } else {
                    player.displayClientMessage(net.minecraft.network.chat.Component.translatable("chat.shincolle.formation.teamfull"), false);
                }
            } else {
                ship.togglePointerSelected();
            }
            sendAdmiralStateIfServerPlayer(player);
        } else if (mode == PointerItem.MODE_FORMATION) {
            int assignedSlot = PlayerStateService.assignShipToCurrentTeam(player, ship.getUUID());
            if (assignedSlot != -1) {
                FormationService.applyFormationState(ship, teamId, assignedSlot, true);
                sendAdmiralStateIfServerPlayer(player);
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
    }

    private static void handleEntityTargetCommand(Player player, List<EntityShipBase> ships, Entity target) {
        if (player.isShiftKeyDown()) {
            Entity guardTarget = target;
            if (guardTarget instanceof EntityMountBase mount && mount.getHost() != null) {
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
            if (!canAssignPointerEntityTarget(player, ship, target)) {
                continue;
            }
            if (ship.hasPointerTargetEntity() && ship.getPointerTargetEntity() == target) {
                ship.clearPointerTargetEntity();
                ship.clearPointerTarget();
                continue;
            }
            ship.setPointerTargetEntity(target, POINTER_TARGET_DURATION_TICKS);
        }
    }

    private static void handleBlockTargetCommand(Player player, List<EntityShipBase> ships) {
        Vec3 target = getLookTarget(player);
        if (target == null) {
            return;
        }

        BlockHitResult blockHit = getLookBlockResult(player);
        BlockPos guardPos = null;
        if (blockHit != null && player.level().getBlockEntity(blockHit.getBlockPos()) instanceof IWaypoint waypoint) {
            BlockPos resolved = resolveWaypointTarget(player.level(), blockHit.getBlockPos(), waypoint);
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

    private static EntityShipBase asShipOrHostedShip(Entity targetEntity) {
        if (targetEntity instanceof EntityShipBase targetShip) {
            return targetShip;
        }
        if (targetEntity instanceof EntityMountBase mount && mount.getHost() instanceof EntityShipBase hostShip) {
            return hostShip;
        }
        return null;
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

    static UUID getTargetOwnerUUID(Entity target) {
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

    private static Vec3 getLookTarget(Player player) {
        BlockHitResult hit = getLookBlockResult(player);
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return null;
        }

        BlockPos pos = hit.getBlockPos();
        return Vec3.atBottomCenterOf(pos).add(0.0, 1.0, 0.0);
    }

    private static BlockHitResult getLookBlockResult(Player player) {
        double reach = POINTER_SEARCH_RADIUS;
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = eyePos.add(look.x * reach, look.y * reach, look.z * reach);
        return player.level().clip(new ClipContext(eyePos, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, player));
    }

    private static BlockPos resolveWaypointTarget(Level level, BlockPos waypointPos, IWaypoint waypoint) {
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

    private static boolean isSamePointerTarget(Vec3 current, Vec3 next) {
        if (current == null || next == null) {
            return false;
        }
        return current.distanceToSqr(next) <= POINTER_TARGET_SAME_DISTANCE_SQR;
    }

    private static void sendAdmiralStateIfServerPlayer(Player player) {
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            PlayerStateService.sendAdmiralState(serverPlayer);
        }
    }
}
