package org.trp.shincolle.entity.base;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.menu.ShipContainerMenu;

import java.util.EnumSet;

final class EntityShipFollowOwnerGoal extends Goal {

    private static final int TP_COOLDOWN = 200;
    private static final int STUCK_TICK_LIMIT = 120;
    private static final double TP_DIST_SQ = 256.0;

    private final EntityShipBase ship;
    private final ShipMovementCoordinator movement;
    private final ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();
    private final double speed;
    private final float defaultMaxDist;
    private final float defaultMinDist;
    private double lastOwnerX;
    private double lastOwnerY;
    private double lastOwnerZ;
    private boolean hasOwnerPos;
    private boolean[] formationDir = new boolean[]{false, true};

    EntityShipFollowOwnerGoal(EntityShipBase ship, double speed, float maxDist, float minDist) {
        this.ship = ship;
        this.movement = new ShipMovementCoordinator(ship);
        this.speed = speed;
        this.defaultMaxDist = maxDist;
        this.defaultMinDist = minDist;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!canFollowOwner()) {
            return false;
        }
        LivingEntity owner = this.ship.getOwner();
        if (owner == null) {
            return false;
        }
        double distSq = this.ship.distanceToSqr(owner);
        if (owner instanceof Player player && this.ship.playerHasCombatRation(player)) {
            return distSq > 1.5 * 1.5;
        }
        float minDist = resolveFollowMinDistance();
        float maxDist = resolveFollowMaxDistance(minDist);
        return distSq > maxDist * maxDist;
    }

    @Override
    public boolean canContinueToUse() {
        if (!canFollowOwner()) {
            return false;
        }
        LivingEntity owner = this.ship.getOwner();
        if (owner == null) {
            return false;
        }
        double distSq = this.ship.distanceToSqr(owner);
        if (owner instanceof Player player && this.ship.playerHasCombatRation(player)) {
            return distSq > 1.5 * 1.5;
        }
        float minDist = resolveFollowMinDistance();
        return distSq > minDist * minDist;
    }

    @Override
    public void start() {
        this.recovery.reset(this.ship.position());
        this.hasOwnerPos = false;
        this.movement.reset();
    }

    @Override
    public void tick() {
        LivingEntity owner = ship.getOwner();
        if (owner == null) {
            return;
        }

        ship.resetInteractionEmotionState();
        if (owner instanceof Player player && this.ship.playerHasCombatRation(player)) {
            ship.setEmotionPrimary(EntityShipBase.EMOTION_HAPPY);
            if (this.ship.tickCount % 32 == 0) {
                EmotionParticleType[] positiveEmotes = {
                        EmotionParticleType.HEART,
                        EmotionParticleType.MUSIC_NOTE,
                        EmotionParticleType.HAPPY_BOB,
                        EmotionParticleType.SPARKLE_EYES,
                        EmotionParticleType.POUT_BOUNCE,
                        EmotionParticleType.LAUGH,
                        EmotionParticleType.HAPPY_GLANCE,
                        EmotionParticleType.BLINK,
                        EmotionParticleType.BLUSH
                };
                EmotionParticleType selected = positiveEmotes[this.ship.getRandom().nextInt(positiveEmotes.length)];
                this.ship.applyParticleEmotion(selected);
            }
        }
        ship.getLookControl().setLookAt(owner, 30.0F, 30.0F);

        int teamId = ship.getFormationTeam();
        int slotId = ship.getFormationSlot();
        net.minecraft.world.phys.Vec3 moveTarget = owner.position();

        if (teamId >= 0 && slotId >= 0) {
            org.trp.shincolle.attachment.AdmiralData data = owner.getData(org.trp.shincolle.init.ModDataAttachments.ADMIRAL_DATA);
            int formationId = data.getFormationID(teamId);
            updateFormationDirection(owner);
            moveTarget = org.trp.shincolle.utility.FormationHelper.getFormationPos(formationId, slotId, owner.position(), formationDir[0], formationDir[1]);
        }

        this.movement.moveTo(moveTarget, this.speed);

        double distSq = ship.distanceToSqr(owner);
        this.recovery.trackProgress(this.ship.position());
        boolean force = this.recovery.isStuckLongerThan(STUCK_TICK_LIMIT);
        tryTeleportRecovery(owner, distSq, force);
    }

    @Override
    public void stop() {
        this.movement.stop();
    }

    private boolean canFollowOwner() {
        return this.ship.shouldFollowOwner();
    }

    private float resolveFollowMinDistance() {
        int configured = this.ship.getStateMinor(ShipContainerMenu.STATE_MINOR_FOLLOW_MIN);
        if (configured <= 0) {
            return this.defaultMinDist;
        }
        int clamped = Mth.clamp(configured, 1, 31);
        return (float) clamped;
    }

    private float resolveFollowMaxDistance(float minDist) {
        int configured = this.ship.getStateMinor(ShipContainerMenu.STATE_MINOR_FOLLOW_MAX);
        if (configured <= 0) {
            return Math.max(this.defaultMaxDist, minDist + 1.0F);
        }
        int minValue = Math.max(2, Mth.floor(minDist) + 1);
        int clamped = Mth.clamp(configured, minValue, 32);
        return (float) clamped;
    }

    private void tryTeleportRecovery(LivingEntity owner, double distSq, boolean force) {
        if (!this.recovery.shouldTryTeleportThrottled(force, distSq, TP_DIST_SQ, TP_COOLDOWN)) {
            return;
        }
        if (!this.movement.teleportNearLiving(owner, 0.75D)) {
            return;
        }
        Shincolle.debugLog("FollowOwner teleportRecovery ship={} owner={} force={} distSq={} stuckTicks={}",
                ship.getUUID(), owner.getUUID(), force, distSq, this.recovery.stuckTicks());
        this.recovery.reset(this.ship.position());
    }

    private void updateFormationDirection(LivingEntity owner) {
        double ox = owner.getX();
        double oy = owner.getY();
        double oz = owner.getZ();
        if (!hasOwnerPos) {
            this.lastOwnerX = ox;
            this.lastOwnerY = oy;
            this.lastOwnerZ = oz;
            this.hasOwnerPos = true;
            return;
        }

        double dx = this.lastOwnerX - ox;
        double dy = this.lastOwnerY - oy;
        double dz = this.lastOwnerZ - oz;
        double dsq = dx * dx + dy * dy + dz * dz;
        if (dsq > 7.0D) {
            this.formationDir = org.trp.shincolle.utility.FormationHelper.getFormationDirection(ox, oz, this.lastOwnerX, this.lastOwnerZ);
            this.lastOwnerX = ox;
            this.lastOwnerY = oy;
            this.lastOwnerZ = oz;
        }
    }
}
