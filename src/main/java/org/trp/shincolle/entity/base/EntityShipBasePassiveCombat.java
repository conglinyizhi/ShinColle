package org.trp.shincolle.entity.base;

import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.entity.EntityAircraftBase;
import org.trp.shincolle.menu.ShipContainerMenu;
import org.trp.shincolle.server.PlayerTargetListSavedData;
import org.trp.shincolle.server.TeamDiplomacySavedData;
import org.trp.shincolle.server.UnattackableTargetData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

final class EntityShipBasePassiveCombat {

    private static final int STATE_FLAG_ON_SIGHT = 12;
    private static final int STATE_FLAG_PVP = 18;
    private static final int STATE_FLAG_ANTI_AIR = 19;
    private static final int STATE_FLAG_ANTI_SUB = 20;
    private static final int STATE_FLAG_PASSIVE_ATTACK = 21;

    private static final int STATE_MINOR_FLEE_HP = ShipContainerMenu.STATE_MINOR_FLEE_HP;

    private static final int PASSIVE_TARGET_SCAN_INTERVAL = 8;
    private static final int PASSIVE_PATH_RECALC_INTERVAL = 10;
    private static final int PASSIVE_TARGET_CHOICE_RANDOM_TOP = 3;
    private static final int PASSIVE_OWNER_REVENGE_DISTANCE_SQR = 32 * 32;
    private static final double PASSIVE_TARGET_VERTICAL_RANGE_FACTOR = 0.75D;
    private static final double PASSIVE_TARGET_LOST_DISTANCE_MULTIPLIER = 1.5D;
    private static final double PASSIVE_MOVE_SPEED_MIN = 0.8D;
    private static final double PASSIVE_MOVE_SPEED_MAX = 1.6D;
    private static final int PASSIVE_MOVE_FAIL_LIMIT = 40;
    private static final int PASSIVE_STUCK_TICK_LIMIT = 120;

    private final EntityShipBase ship;
    private final ShipMovementCoordinator movement;

    private int passiveTargetScanTick;
    private int passiveTargetPathTick;
    private int passiveTargetSightTick;
    private int passiveMeleeCooldownTick;
    private int passiveLightCooldownTick;
    private int passiveHeavyCooldownTick;
    private boolean isFirstEngagementWaiting;
    private int passiveLastHurtByMobTimestamp;
    private int passiveLastOwnerHurtByTimestamp;
    private int passiveLastOwnerHurtMobTimestamp;
    private final ShipMovementRecoveryState movementRecovery = new ShipMovementRecoveryState();

    EntityShipBasePassiveCombat(EntityShipBase ship) {
        this.ship = ship;
        this.movement = new ShipMovementCoordinator(ship);
    }

    void tickTargeting() {
        if (!canFight()) {
            clearTarget(true);
            return;
        }

        Entity targetEntity = this.ship.getPointerTargetEntity();

        LivingEntity pointerTarget = targetEntity instanceof LivingEntity ? (LivingEntity) targetEntity : null;

        if (pointerTarget != null && canAttackTarget(pointerTarget, false, true)) {
            if (this.ship.getTarget() != pointerTarget) {
                setPassiveCombatTarget(pointerTarget, true);
            }
        }

        LivingEntity currentTarget = this.ship.getTarget();
        if (currentTarget != null) {
            double maxLostDistance = getPassiveAcquireRangeSqr() * PASSIVE_TARGET_LOST_DISTANCE_MULTIPLIER;
            if (!isValidPassiveTarget(currentTarget) || this.ship.distanceToSqr(currentTarget) > maxLostDistance) {
                clearTarget(true);
            }
        }

        tryAcquireRevengeTargets();

        if (this.ship.getTarget() != null) {
            return;
        }

        if (this.ship.getStateFlag(STATE_FLAG_PASSIVE_ATTACK)) {
            return;
        }

        if (this.passiveTargetScanTick-- > 0) {
            return;
        }
        this.passiveTargetScanTick = PASSIVE_TARGET_SCAN_INTERVAL;

        tryAcquireNearbyCombatTarget();
    }

    void tickActions() {
        LivingEntity target = this.ship.getTarget();
        if (target == null) return;

        boolean isRevenge = (target == this.ship.getLastHurtByMob());
        if (!isRevenge && this.ship.getOwner() != null) {
            isRevenge = (target == this.ship.getOwner().getLastHurtByMob() || target == this.ship.getOwner().getLastHurtMob());
        }

        boolean isCommanded = (target == this.ship.getPointerTargetEntity());

        if (!isAttackAllowed(target, isRevenge, isCommanded)) {
            clearTarget(true);
            return;
        }

        if (this.passiveMeleeCooldownTick > 0) {
            this.passiveMeleeCooldownTick--;
        }
        if (this.passiveLightCooldownTick > 0) {
            this.passiveLightCooldownTick--;
        }
        if (this.passiveHeavyCooldownTick > 0) {
            this.passiveHeavyCooldownTick--;
        }

        double distanceSqr = this.ship.distanceToSqr(target);
        boolean onSight = this.ship.hasLineOfSight(target);
        if (onSight) {
            this.passiveTargetSightTick++;
        } else {
            this.passiveTargetSightTick = 0;
            if (this.ship.getStateFlag(STATE_FLAG_ON_SIGHT)) {
                clearTarget(true);
                return;
            }
        }

        EntityShipBaseCombat combat = this.ship.getCombat();
        double preferredRangeSqr = getPassivePreferredRangeSqr(target);
        boolean hasRangedAttack = combat.canUseLightAmmo()
                || combat.canUseHeavyAmmo()
                || combat.hasAircraftAttackEnabled();
        double stopRangeSqr = hasRangedAttack ? preferredRangeSqr + 1.0D : preferredRangeSqr;

        boolean needsCloser = distanceSqr > stopRangeSqr;
        boolean cannotSee = !onSight && distanceSqr > preferredRangeSqr * 0.5D;
        boolean hasAttackMeans = hasRangedAttack || combat.canUseMeleeAttack();

        if (needsCloser || cannotSee) {
            if (this.ship.hasPointerTarget() || !hasAttackMeans) {
                return;
            }
            this.movementRecovery.trackProgress(this.ship.position());
            if (this.movementRecovery.isStuckLongerThan(PASSIVE_STUCK_TICK_LIMIT)) {
                Shincolle.debugLog("PassiveCombat stuckClear ship={} target={} stuckTicks={} distanceSqr={}",
                        this.ship.getUUID(), target.getUUID(), this.movementRecovery.stuckTicks(), distanceSqr);
                clearTarget(true);
                return;
            }
            if (this.passiveTargetPathTick-- <= 0) {
                this.passiveTargetPathTick = PASSIVE_PATH_RECALC_INTERVAL;
                if (!this.movement.moveTo(target, getPassiveMoveSpeed())) {
                    int failCount = this.movementRecovery.recordMoveFailure();
                    Shincolle.debugLog("PassiveCombat moveFail ship={} target={} failCount={} distanceSqr={}",
                            this.ship.getUUID(), target.getUUID(), failCount, distanceSqr);
                    if (failCount > PASSIVE_MOVE_FAIL_LIMIT) {
                        Shincolle.debugLog("PassiveCombat failClear ship={} target={} failCount={}",
                                this.ship.getUUID(), target.getUUID(), this.movementRecovery.moveFailCount());
                        clearTarget(true);
                        return;
                    }
                    this.passiveTargetPathTick = 2;
                } else {
                    this.movementRecovery.clearMoveFailures();
                }
            }
            return;
        }

        this.movementRecovery.reset(this.ship.position());
        if (!this.ship.shouldFollowOwner() && !this.ship.hasPointerTarget()) {
            this.movement.stop();
            this.ship.getMoveControl().setWantedPosition(
                    this.ship.getX(), this.ship.getY(), this.ship.getZ(), 0.0D);
        }

        if (!this.isFirstEngagementWaiting) {
            this.isFirstEngagementWaiting = true;
            resetPassiveCombatCooldowns();
            return;
        }

        if (this.passiveTargetSightTick < getPassiveAimTime()) {
            return;
        }

        if (combat.hasAircraftAttackEnabled()) {
            combat.tryPerformAircraftCycle(target);
        }

        if (combat.canUseLightAmmo() && this.passiveLightCooldownTick <= 0) {
            this.ship.performLightAttack(target);
            this.passiveLightCooldownTick = Math.max(1, this.ship.getLegacyShipStats().getLightDelay());
        }

        if (combat.canUseHeavyAmmo() && this.passiveHeavyCooldownTick <= 0) {
            this.ship.performHeavyAttack(target);
            this.passiveHeavyCooldownTick = Math.max(1, this.ship.getLegacyShipStats().getHeavyDelay());
        }

        if (combat.canUseMeleeAttack()
                && this.passiveMeleeCooldownTick <= 0
                && distanceSqr <= getPassiveAttackRangeSqr(target)) {
            this.ship.doHurtTarget(target);
            this.passiveMeleeCooldownTick = Math.max(1, this.ship.getLegacyShipStats().getMeleeDelay());
        }
    }

    void clearTarget(boolean stopNavigation) {
        if (this.ship.getTarget() != null) {
            this.ship.setTarget(null);
        }
        this.passiveTargetSightTick = 0;
        this.passiveTargetPathTick = 0;
        this.isFirstEngagementWaiting = false;
        this.movementRecovery.clear();
        this.movement.reset();
        if (stopNavigation) {
            this.movement.stop();
        }
    }

    private void tryAcquireRevengeTargets() {
        tryAcquireSelfRevengeTarget();
        tryAcquireOwnerRevengeTarget();
    }

    private void tryAcquireSelfRevengeTarget() {
        LivingEntity attacker = this.ship.getLastHurtByMob();
        int timestamp = this.ship.getLastHurtByMobTimestamp();
        if (attacker != null && timestamp != this.passiveLastHurtByMobTimestamp) {
            this.passiveLastHurtByMobTimestamp = timestamp;
            tryPromoteRevengeTarget(attacker);
        }
    }

    private void tryAcquireOwnerRevengeTarget() {
        LivingEntity owner = this.ship.getOwner();
        if (owner == null || this.ship.distanceToSqr(owner) > PASSIVE_OWNER_REVENGE_DISTANCE_SQR) {
            return;
        }

        LivingEntity ownerAttacker = owner.getLastHurtByMob();
        int ownerAttackerTimestamp = owner.getLastHurtByMobTimestamp();
        if (ownerAttacker != null && ownerAttackerTimestamp != this.passiveLastOwnerHurtByTimestamp) {
            this.passiveLastOwnerHurtByTimestamp = ownerAttackerTimestamp;
            tryPromoteRevengeTarget(ownerAttacker);
        }

        LivingEntity ownerTarget = owner.getLastHurtMob();
        int ownerTargetTimestamp = owner.getLastHurtMobTimestamp();
        if (ownerTarget != null && ownerTargetTimestamp != this.passiveLastOwnerHurtMobTimestamp) {
            this.passiveLastOwnerHurtMobTimestamp = ownerTargetTimestamp;
            tryPromoteRevengeTarget(ownerTarget);
        }
    }

    private void tryPromoteRevengeTarget(@Nullable LivingEntity candidate) {
        if (!canAttackTarget(candidate, true, false)) {
            return;
        }

        LivingEntity currentTarget = this.ship.getTarget();
        if (currentTarget == null || !isValidPassiveTarget(currentTarget)) {
            setPassiveCombatTarget(candidate, true);
            return;
        }

        if (this.ship.distanceToSqr(candidate) < this.ship.distanceToSqr(currentTarget)) {
            setPassiveCombatTarget(candidate, true);
        }
    }

    private void tryAcquireNearbyCombatTarget() {
        double range = getPassiveAcquireRange();
        double rangeY = range * PASSIVE_TARGET_VERTICAL_RANGE_FACTOR;
        List<LivingEntity> candidates = this.ship.level().getEntitiesOfClass(LivingEntity.class,
                this.ship.getBoundingBox().inflate(range, rangeY, range),
                target -> canAttackTarget(target, false, false));
        if (candidates.isEmpty()) {
            return;
        }

        List<LivingEntity> prioritized = pickPrioritizedTargets(candidates);
        prioritized.sort(Comparator.comparingDouble(this.ship::distanceToSqr));

        LivingEntity selected;
        if (prioritized.size() > 2) {
            int pickBound = Math.min(PASSIVE_TARGET_CHOICE_RANDOM_TOP, prioritized.size());
            selected = prioritized.get(this.ship.getRandom().nextInt(pickBound));
        } else {
            selected = prioritized.get(0);
        }

        setPassiveCombatTarget(selected, true);
    }

    private List<LivingEntity> pickPrioritizedTargets(List<LivingEntity> candidates) {
        if (candidates.isEmpty()) {
            return candidates;
        }

        List<LivingEntity> prioritized = new ArrayList<>();

        if (this.ship.getStateFlag(STATE_FLAG_ANTI_AIR)) {
            for (LivingEntity target : candidates) {
                if (isAntiAirTarget(target)) {
                    prioritized.add(target);
                }
            }
        }

        if (prioritized.isEmpty() && this.ship.getStateFlag(STATE_FLAG_ANTI_SUB)) {
            for (LivingEntity target : candidates) {
                if (isAntiSubTarget(target)) {
                    prioritized.add(target);
                }
            }
        }

        if (prioritized.isEmpty() && this.ship.getStateFlag(STATE_FLAG_PVP)) {
            for (LivingEntity target : candidates) {
                if (isPlayerOrShip(target)) {
                    prioritized.add(target);
                }
            }
        }

        return prioritized.isEmpty() ? candidates : prioritized;
    }

    private boolean isAntiAirTarget(LivingEntity target) {
        return target instanceof EntityAircraftBase || target instanceof FlyingMob;
    }

    private boolean isAntiSubTarget(LivingEntity target) {
        if (target.isInvisible()) {
            return true;
        }
        return target instanceof EntityShipBase shipTarget && shipTarget.isInWaterOrBubble();
    }

    private void setPassiveCombatTarget(@Nullable LivingEntity target, boolean resetCooldown) {
        if (target == null) {
            clearTarget(false);
            return;
        }

        this.ship.setTarget(target);
        this.passiveTargetPathTick = 0;
        this.passiveTargetSightTick = 0;
        this.isFirstEngagementWaiting = false;
        this.movement.reset();
        this.movementRecovery.reset(this.ship.position());

        if (resetCooldown) {
            resetPassiveCombatCooldowns();
        }
    }

    private void resetPassiveCombatCooldowns() {
        int aimTime = getPassiveAimTime();
        this.passiveTargetScanTick = PASSIVE_TARGET_SCAN_INTERVAL;
        
        if (this.passiveMeleeCooldownTick <= 20) {
            this.passiveMeleeCooldownTick = 20;
        }
        if (this.passiveLightCooldownTick <= aimTime) {
            this.passiveLightCooldownTick = aimTime;
        }
        if (this.passiveHeavyCooldownTick <= aimTime * 2) {
            this.passiveHeavyCooldownTick = aimTime * 2;
        }
        
        this.ship.getCombat().resetAircraftLaunchDelay();
    }

    private boolean shouldRetreatForLowHealth() {
        int fleeHp = Mth.clamp(this.ship.getStateMinor(STATE_MINOR_FLEE_HP), 0, 100);
        if (fleeHp <= 0) {
            return false;
        }
        return this.ship.getHealth() <= this.ship.getMaxHealth() * (fleeHp / 100.0F);
    }

    private boolean isValidPassiveTarget(@Nullable LivingEntity target) {
        if (target == null) return false;

        boolean isRevenge = (target == this.ship.getLastHurtByMob());
        boolean isCommanded = (target == this.ship.getPointerTargetEntity());
        return canAttackTarget(target, isRevenge, isCommanded);
    }

    private boolean canAttackTarget(@Nullable LivingEntity target, boolean revengeContext, boolean commandContext) {
        if (target == null) return false;
        if (!isAttackAllowed(target, revengeContext, commandContext)) {
            return false;
        }

        if (target == this.ship) {
            return false;
        }
        if (!target.isAlive()) {
            return false;
        }
        if (target.isSpectator()) {
            return false;
        }

        if (target instanceof Player player && player.getAbilities().invulnerable) {
            return false;
        }

        if (isUnattackableTargetClass(target)) {
            return false;
        }

        if (commandContext) {
            return true;
        }

        boolean pvpEnabled = this.ship.getStateFlag(STATE_FLAG_PVP);

        if (isPlayerOrShip(target) && !pvpEnabled) {
            return false;
        }

        if (target.isInvisible() && this.ship.getStateMinor(38) < 1 && this.ship.getStateMinor(39) < 1) {
            return false;
        }

        if (isFriendlyTarget(target)) {
            return false;
        }

        if (isDiplomaticAlly(target)) {
            return false;
        }

        if (this.ship.getOwnerUUID() != null) {
            if (target instanceof Enemy) {
                return true;
            }
            if (target instanceof EntityShipBase shipTarget && shipTarget.getOwnerUUID() == null) {
                return true;
            }
            if (isPlayerConfiguredTargetClass(target)) {
                return true;
            }
            if (isDiplomaticBanned(target)) {
                return true;
            }
            return isPlayerOrShip(target) && pvpEnabled;
        }

        if (target instanceof EntityShipBase shipTarget && shipTarget.getOwnerUUID() == null) {
            return false;
        }

        if (target instanceof Enemy) {
            return true;
        }

        if (isPlayerOrShip(target)) {
            return true;
        }

        if (target instanceof TamableAnimal tamable) {
            return tamable.getOwnerUUID() != null;
        }

        return revengeContext;
    }

    private boolean isFriendlyTarget(@Nullable Entity target) {
        if (target == null) {
            return false;
        }

        if (target == this.ship) {
            return true;
        }

        if (sharesOwner(target)) {
            return true;
        }

        if (target instanceof EntityMountBase mount) {
            EntityShipBase host = mount.getHost();
            if (host != null) {
                return Objects.equals(host.getOwnerUUID(), this.ship.getOwnerUUID());
            }
            return Objects.equals(mount.getHostUUID(), this.ship.getOwnerUUID());
        }

        return target instanceof EntityShipBase shipTarget
                && this.ship.getOwnerUUID() == null
                && shipTarget.getOwnerUUID() == null;
    }

    private boolean sharesOwner(Entity target) {
        if (this.ship.getOwnerUUID() == null) {
            return false;
        }

        if (target instanceof Player player) {
            return this.ship.getOwnerUUID().equals(player.getUUID());
        }

        if (target instanceof TamableAnimal tamable) {
            return this.ship.getOwnerUUID().equals(tamable.getOwnerUUID());
        }

        return false;
    }

    private boolean isPlayerOrShip(Entity target) {
        return target instanceof Player || target instanceof EntityShipBase;
    }

    private boolean isUnattackableTargetClass(LivingEntity target) {
        if (!(this.ship.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        return UnattackableTargetData.get(serverLevel).contains(target.getClass().getName());
    }

    private boolean isPlayerConfiguredTargetClass(Entity target) {
        if (!(this.ship.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        UUID owner = this.ship.getOwnerUUID();
        return owner != null && PlayerTargetListSavedData.get(serverLevel).contains(owner, target.getClass().getName());
    }

    private boolean isDiplomaticAlly(Entity target) {
        if (!(this.ship.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        UUID owner = this.ship.getOwnerUUID();
        UUID targetOwner = getTargetOwnerUUID(target);
        return TeamDiplomacySavedData.get(serverLevel).areAllies(owner, targetOwner);
    }

    private boolean isDiplomaticBanned(Entity target) {
        if (!(this.ship.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        UUID owner = this.ship.getOwnerUUID();
        UUID targetOwner = getTargetOwnerUUID(target);
        return TeamDiplomacySavedData.get(serverLevel).isBanned(owner, targetOwner);
    }

    @Nullable
    private UUID getTargetOwnerUUID(Entity target) {
        if (target instanceof Player player) {
            return player.getUUID();
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
        return null;
    }

    private int getPassiveAimTime() {
        return Math.max(5, (int) (20.0F * (150 - this.ship.getLevel()) / 150.0F) + 10);
    }

    private double getPassiveAcquireRange() {
        double range = Math.max(2.0D, this.ship.getLegacyShipStats().getAttackRange());
        if (this.ship.getCombat().hasAircraftAttackEnabled()) {
            range = Math.max(range, this.ship.getLegacyShipStats().getAttackRange() * 1.5D);
        }
        return range;
    }

    private double getPassiveAcquireRangeSqr() {
        double range = getPassiveAcquireRange();
        return range * range;
    }

    private double getPassiveAttackRangeSqr(Entity target) {
        double width = this.ship.getBbWidth() * 2.0F;
        double reach = width * width + target.getBbWidth();
        return Math.max(reach, 4.0D);
    }

    private double getPassivePreferredRangeSqr(Entity target) {
        EntityShipBaseCombat combat = this.ship.getCombat();

        if (combat.canUseLightAmmo() || combat.canUseHeavyAmmo()) {
            double range = Math.max(2.0D, this.ship.getLegacyShipStats().getAttackRange());
            return range * range;
        }

        if (combat.hasAircraftAttackEnabled()) {
            double range = Math.max(24.0D, this.ship.getLegacyShipStats().getAttackRange() * 1.5D);
            return range * range;
        }

        return getPassiveAttackRangeSqr(target);
    }

    private double getPassiveMoveSpeed() {
        double speed = this.ship.getLegacyShipStats().getMoveSpeed() * 3.0D;
        return Mth.clamp(speed, PASSIVE_MOVE_SPEED_MIN, PASSIVE_MOVE_SPEED_MAX);
    }

    private boolean canFight() {
        if (shouldRetreatForLowHealth() || this.ship.getIsSitting() ||
                this.ship.isInDeadPose() || this.ship.isPassenger() || this.ship.isVehicle()) {
            return false;
        }
        return !this.ship.isNoFuel();
    }

    private boolean isAttackAllowed(@Nullable LivingEntity target, boolean isRevenge, boolean isCommanded) {
        if (!canFight()) return false;

        if (isRevenge || isCommanded) return true;

        return !this.ship.getStateFlag(STATE_FLAG_PASSIVE_ATTACK);
    }

}
