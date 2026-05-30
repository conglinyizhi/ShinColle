package org.trp.shincolle.entity.base;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.trp.shincolle.menu.ShipContainerMenu;

import java.util.UUID;

public final class ShipBrainMemory {
    private ShipBrainMemory() {
    }

    public static PointerTargetMemory pointerTarget(EntityShipBase ship) {
        Vec3 rawTarget = ship.getRawPointerTarget();
        Vec3 target = ship.getPointerTarget();
        Entity targetEntity = ship.getPointerTargetEntity();
        boolean hasEntityTargetCommand = ship.hasPointerTargetEntity();
        double entityDistanceSqr = targetEntity == null ? -1.0D : ship.distanceToSqr(targetEntity);
        EntityShipBaseCombat combat = ship.getCombat();
        ShipPointerEntityDecisionResolver.Decision entityDecision = ShipPointerEntityDecisionResolver.resolve(
                new ShipPointerEntityDecisionResolver.State(
                        targetEntity != null,
                        entityDistanceSqr,
                        targetEntity != null && ship.hasLineOfSight(targetEntity),
                        combat.canUseLightAmmo(),
                        combat.canUseHeavyAmmo(),
                        combat.hasAircraftAttackEnabled(),
                        combat.canUseMeleeAttack(),
                        ship.getLegacyShipStats().getAttackRange(),
                        ship.getBbWidth(),
                        targetEntity == null ? 0.0D : targetEntity.getBbWidth()
                ));
        return new PointerTargetMemory(
                ship.hasPointerTarget(),
                rawTarget,
                target,
                ship.getPointerTargetRemainingTicks(),
                hasEntityTargetCommand,
                targetEntity != null ? targetEntity.getUUID() : null,
                targetEntity != null && targetEntity.isAlive(),
                targetEntity != null ? targetEntity.position() : null,
                ship.getPointerTargetEntityRemainingTicks(),
                entityDistanceSqr,
                entityDecision.preferredRangeSqr(),
                entityDecision.stopRangeSqr(),
                entityDecision.needsCloser(),
                entityDecision.cannotSee(),
                entityDecision.shouldChase(),
                entityDecision.attackRangeSqr(),
                entityDecision.hasRangedAttack(),
                entityDecision.canMeleeAttack()
        );
    }

    public static GuardTargetMemory guardTarget(EntityShipBase ship) {
        ShipGuardTarget target = ship.getGuardTarget();
        Entity guarded = ship.getGuardedEntity();
        boolean disabled = ship.getStateFlag(EntityShipBase.STATE_FLAG_DISABLE_GUARD_POS);
        boolean canGuard = target.isActive()
                && !disabled
                && (target.isBlock() && target.isIn(ship.level())
                || target.isEntity() && guarded != null && guarded.isAlive());
        return new GuardTargetMemory(
                target,
                canGuard,
                disabled,
                guarded != null ? guarded.getUUID() : null,
                guarded != null && guarded.isAlive(),
                guarded != null ? guarded.position() : null,
                target.isBlock() ? target.blockCenter() : null,
                target.dimensionId()
        );
    }

    public static FollowStateMemory followState(EntityShipBase ship) {
        LivingEntity owner = ship.getOwner();
        boolean ownerHasCombatRation = owner instanceof Player player && ship.playerHasCombatRation(player);
        return new FollowStateMemory(
                ship.shouldFollowOwner(),
                ship.explainFollowBlockReason(),
                owner != null,
                owner != null ? owner.getUUID() : null,
                owner != null ? owner.position() : null,
                owner != null ? owner.getEyeY() : 0.0D,
                owner != null ? EntityShipBase.getLegacyDimensionId(owner.level()) : 0,
                ownerHasCombatRation,
                owner == null ? -1.0D : ship.distanceToSqr(owner),
                ship.getStateMinor(ShipContainerMenu.STATE_MINOR_FOLLOW_MIN),
                ship.getStateMinor(ShipContainerMenu.STATE_MINOR_FOLLOW_MAX)
        );
    }

    public static RecoveryStateMemory recoveryState(ShipMovementRecoveryState state, boolean forceRecovery) {
        return new RecoveryStateMemory(state.stuckTicks(), state.moveFailCount(), forceRecovery);
    }

    public static PassiveCombatStateMemory noPassiveCombatState() {
        return new PassiveCombatStateMemory(
                null,
                false,
                null,
                0.0D,
                0.0D,
                0.0D,
                false,
                false,
                false,
                false,
                false,
                false,
                0.0D,
                0
        );
    }

    public record PointerTargetMemory(
            boolean hasPointTarget,
            Vec3 rawPointTarget,
            Vec3 adjustedPointTarget,
            long pointTargetRemainingTicks,
            boolean hasEntityTargetCommand,
            UUID entityTargetId,
            boolean entityTargetAlive,
            Vec3 entityTargetPos,
            long entityTargetRemainingTicks,
            double entityDistanceSqr,
            double entityPreferredRangeSqr,
            double entityStopRangeSqr,
            boolean entityNeedsCloser,
            boolean entityCannotSee,
            boolean entityShouldChase,
            double entityAttackRangeSqr,
            boolean entityHasRangedAttack,
            boolean entityCanMeleeAttack
    ) {
        public boolean hasAnyTarget() {
            return this.hasPointTarget || this.hasEntityTargetCommand;
        }

        public boolean hasAdjustedPointTarget() {
            return this.hasPointTarget && this.adjustedPointTarget != null;
        }
    }

    public record GuardTargetMemory(
            ShipGuardTarget target,
            boolean canGuard,
            boolean disabled,
            UUID guardedEntityId,
            boolean guardedEntityAlive,
            Vec3 guardedEntityPos,
            Vec3 blockCenter,
            int dimensionId
    ) {
        public boolean hasBlockTarget() {
            return this.target.isBlock() && this.blockCenter != null;
        }

        public boolean hasLiveEntityTarget() {
            return this.target.isEntity() && this.guardedEntityAlive && this.guardedEntityPos != null;
        }
    }

    public record FollowStateMemory(
            boolean shouldFollow,
            String blockReason,
            boolean ownerPresent,
            UUID ownerId,
            Vec3 ownerPos,
            double ownerEyeY,
            int ownerDimensionId,
            boolean ownerHasCombatRation,
            double ownerDistanceSq,
            int followMinConfig,
            int followMaxConfig
    ) {
    }

    public record RecoveryStateMemory(
            int stuckTicks,
            int moveFailCount,
            boolean forceRecovery
    ) {
    }

    public record PassiveCombatStateMemory(
            UUID targetId,
            boolean targetAlive,
            Vec3 targetPos,
            double distanceSqr,
            double preferredRangeSqr,
            double stopRangeSqr,
            boolean needsCloser,
            boolean cannotSee,
            boolean hasAttackMeans,
            boolean hasPointerTarget,
            boolean needsMovement,
            boolean shouldChase,
            double moveSpeed,
            int sightTicks
    ) {
        public boolean hasTarget() {
            return this.targetAlive && this.targetId != null && this.targetPos != null;
        }
    }
}
