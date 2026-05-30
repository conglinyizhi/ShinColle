package org.trp.shincolle.entity.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MountBrainDecisionResolverTest {

    @Test
    void followShouldStartForPointerOrGuardBeforeOwnerDistanceChecks() {
        assertTrue(MountBrainDecisionResolver.shouldFollowHost(stateBuilder()
                .hostHasPointerTarget(true)
                .ownerPresent(false)
                .build()));
        assertTrue(MountBrainDecisionResolver.shouldFollowHost(stateBuilder()
                .hasGuardTarget(true)
                .ownerPresent(false)
                .build()));
    }

    @Test
    void followShouldUseOwnerDistanceWhenNoCommandOrGuardTargetExists() {
        double maxDistSq = 8.0D * 8.0D + MountAiNumbers.FOLLOW_WIDTH_PADDING;

        assertFalse(MountBrainDecisionResolver.shouldFollowHost(stateBuilder()
                .ownerDistanceSq(maxDistSq)
                .followMaxConfig(8)
                .mountWidth(1.0D)
                .build()));
        assertTrue(MountBrainDecisionResolver.shouldFollowHost(stateBuilder()
                .ownerDistanceSq(maxDistSq + 0.01D)
                .followMaxConfig(8)
                .mountWidth(1.0D)
                .build()));
    }

    @Test
    void followShouldNotStartWhenHostOrMountStateBlocksMovement() {
        assertFalse(MountBrainDecisionResolver.shouldFollowHost(stateBuilder().hostPresent(false).build()));
        assertFalse(MountBrainDecisionResolver.shouldFollowHost(stateBuilder().hostAlive(false).build()));
        assertFalse(MountBrainDecisionResolver.shouldFollowHost(stateBuilder().hostOrderedToSit(true).build()));
        assertFalse(MountBrainDecisionResolver.shouldFollowHost(stateBuilder().mountPassenger(true).build()));
        assertFalse(MountBrainDecisionResolver.shouldFollowHost(stateBuilder().ownerPresent(false).build()));
    }

    @Test
    void rangeAttackShouldRequireUsableHostStateAndAliveTarget() {
        assertTrue(MountBrainDecisionResolver.shouldRangeAttack(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(true)
                .build()));
        assertFalse(MountBrainDecisionResolver.shouldRangeAttack(stateBuilder()
                .hostAlive(false)
                .hasAliveAttackTarget(true)
                .build()));
        assertFalse(MountBrainDecisionResolver.shouldRangeAttack(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(false)
                .build()));
        assertFalse(MountBrainDecisionResolver.shouldRangeAttack(stateBuilder()
                .hostOrderedToSit(true)
                .hasAliveAttackTarget(true)
                .build()));
        assertFalse(MountBrainDecisionResolver.shouldRangeAttack(stateBuilder()
                .mountPassenger(true)
                .hasAliveAttackTarget(true)
                .build()));
    }

    @Test
    void lightAttackShouldRequireAimRangeAmmoAndCooldown() {
        assertTrue(MountBrainDecisionResolver.shouldFireLight(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(true)
                .aimTick(20)
                .aimRequiredTicks(20)
                .targetDistanceSq(16.0D)
                .attackRangeSq(16.0D)
                .hostLightAttackEnabled(true)
                .hostLightAmmo(1)
                .lightDelay(0)
                .build()));
        assertFalse(MountBrainDecisionResolver.shouldFireLight(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(true)
                .aimTick(19)
                .aimRequiredTicks(20)
                .targetDistanceSq(16.0D)
                .attackRangeSq(16.0D)
                .hostLightAttackEnabled(true)
                .hostLightAmmo(1)
                .lightDelay(0)
                .build()));
        assertFalse(MountBrainDecisionResolver.shouldFireLight(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(true)
                .aimTick(20)
                .aimRequiredTicks(20)
                .targetDistanceSq(16.01D)
                .attackRangeSq(16.0D)
                .hostLightAttackEnabled(true)
                .hostLightAmmo(1)
                .lightDelay(0)
                .build()));
        assertFalse(MountBrainDecisionResolver.shouldFireLight(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(true)
                .aimTick(20)
                .aimRequiredTicks(20)
                .targetDistanceSq(16.0D)
                .attackRangeSq(16.0D)
                .hostLightAttackEnabled(true)
                .hostLightAmmo(0)
                .lightDelay(0)
                .build()));
        assertFalse(MountBrainDecisionResolver.shouldFireLight(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(true)
                .aimTick(20)
                .aimRequiredTicks(20)
                .targetDistanceSq(16.0D)
                .attackRangeSq(16.0D)
                .hostLightAttackEnabled(true)
                .hostLightAmmo(1)
                .lightDelay(1)
                .build()));
    }

    @Test
    void heavyAttackShouldRequireAimRangeAmmoAndCooldown() {
        assertTrue(MountBrainDecisionResolver.shouldFireHeavy(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(true)
                .aimTick(15)
                .aimRequiredTicks(15)
                .targetDistanceSq(9.0D)
                .attackRangeSq(9.0D)
                .hostHeavyAttackEnabled(true)
                .hostHeavyAmmo(1)
                .heavyDelay(0)
                .build()));
        assertFalse(MountBrainDecisionResolver.shouldFireHeavy(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(true)
                .aimTick(15)
                .aimRequiredTicks(15)
                .targetDistanceSq(9.01D)
                .attackRangeSq(9.0D)
                .hostHeavyAttackEnabled(true)
                .hostHeavyAmmo(1)
                .heavyDelay(0)
                .build()));
        assertFalse(MountBrainDecisionResolver.shouldFireHeavy(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(true)
                .aimTick(15)
                .aimRequiredTicks(15)
                .targetDistanceSq(9.0D)
                .attackRangeSq(9.0D)
                .hostHeavyAttackEnabled(false)
                .hostHeavyAmmo(1)
                .heavyDelay(0)
                .build()));
        assertFalse(MountBrainDecisionResolver.shouldFireHeavy(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(true)
                .aimTick(15)
                .aimRequiredTicks(15)
                .targetDistanceSq(9.0D)
                .attackRangeSq(9.0D)
                .hostHeavyAttackEnabled(true)
                .hostHeavyAmmo(1)
                .heavyDelay(2)
                .build()));
    }

    @Test
    void randomStrollShouldNotRaceFollowOrCombatMovement() {
        assertTrue(MountBrainDecisionResolver.shouldRandomStroll(stateBuilder()
                .ownerDistanceSq(0.0D)
                .randomStrollRollHit(true)
                .build()));
        assertFalse(MountBrainDecisionResolver.shouldRandomStroll(stateBuilder()
                .hostAlive(false)
                .randomStrollRollHit(true)
                .build()));
        assertFalse(MountBrainDecisionResolver.shouldRandomStroll(stateBuilder()
                .hasAttackTarget(true)
                .randomStrollRollHit(true)
                .build()));
        assertFalse(MountBrainDecisionResolver.shouldRandomStroll(stateBuilder()
                .hostHasPointerTarget(true)
                .randomStrollRollHit(true)
                .build()));
        assertFalse(MountBrainDecisionResolver.shouldRandomStroll(stateBuilder()
                .randomStrollRollHit(false)
                .build()));
    }

    private static StateBuilder stateBuilder() {
        return new StateBuilder();
    }

    private static final class StateBuilder {
        private boolean hostPresent = true;
        private boolean hostAlive = true;
        private boolean hostOrderedToSit;
        private boolean mountPassenger;
        private boolean hostHasPointerTarget;
        private boolean hasGuardTarget;
        private boolean ownerPresent = true;
        private double ownerDistanceSq;
        private double mountWidth = 1.0D;
        private int followMaxConfig = 8;
        private boolean hasAttackTarget;
        private boolean hasAliveAttackTarget;
        private boolean randomStrollRollHit;
        private int aimTick;
        private int aimRequiredTicks;
        private double targetDistanceSq;
        private double attackRangeSq;
        private boolean hostLightAttackEnabled;
        private int hostLightAmmo;
        private int lightDelay;
        private boolean hostHeavyAttackEnabled;
        private int hostHeavyAmmo;
        private int heavyDelay;

        private StateBuilder hostPresent(boolean hostPresent) {
            this.hostPresent = hostPresent;
            return this;
        }

        private StateBuilder hostAlive(boolean hostAlive) {
            this.hostAlive = hostAlive;
            return this;
        }

        private StateBuilder hostOrderedToSit(boolean hostOrderedToSit) {
            this.hostOrderedToSit = hostOrderedToSit;
            return this;
        }

        private StateBuilder mountPassenger(boolean mountPassenger) {
            this.mountPassenger = mountPassenger;
            return this;
        }

        private StateBuilder hostHasPointerTarget(boolean hostHasPointerTarget) {
            this.hostHasPointerTarget = hostHasPointerTarget;
            return this;
        }

        private StateBuilder hasGuardTarget(boolean hasGuardTarget) {
            this.hasGuardTarget = hasGuardTarget;
            return this;
        }

        private StateBuilder ownerPresent(boolean ownerPresent) {
            this.ownerPresent = ownerPresent;
            return this;
        }

        private StateBuilder ownerDistanceSq(double ownerDistanceSq) {
            this.ownerDistanceSq = ownerDistanceSq;
            return this;
        }

        private StateBuilder mountWidth(double mountWidth) {
            this.mountWidth = mountWidth;
            return this;
        }

        private StateBuilder followMaxConfig(int followMaxConfig) {
            this.followMaxConfig = followMaxConfig;
            return this;
        }

        private StateBuilder hasAttackTarget(boolean hasAttackTarget) {
            this.hasAttackTarget = hasAttackTarget;
            return this;
        }

        private StateBuilder hasAliveAttackTarget(boolean hasAliveAttackTarget) {
            this.hasAliveAttackTarget = hasAliveAttackTarget;
            return this;
        }

        private StateBuilder randomStrollRollHit(boolean randomStrollRollHit) {
            this.randomStrollRollHit = randomStrollRollHit;
            return this;
        }

        private StateBuilder aimTick(int aimTick) {
            this.aimTick = aimTick;
            return this;
        }

        private StateBuilder aimRequiredTicks(int aimRequiredTicks) {
            this.aimRequiredTicks = aimRequiredTicks;
            return this;
        }

        private StateBuilder targetDistanceSq(double targetDistanceSq) {
            this.targetDistanceSq = targetDistanceSq;
            return this;
        }

        private StateBuilder attackRangeSq(double attackRangeSq) {
            this.attackRangeSq = attackRangeSq;
            return this;
        }

        private StateBuilder hostLightAttackEnabled(boolean hostLightAttackEnabled) {
            this.hostLightAttackEnabled = hostLightAttackEnabled;
            return this;
        }

        private StateBuilder hostLightAmmo(int hostLightAmmo) {
            this.hostLightAmmo = hostLightAmmo;
            return this;
        }

        private StateBuilder lightDelay(int lightDelay) {
            this.lightDelay = lightDelay;
            return this;
        }

        private StateBuilder hostHeavyAttackEnabled(boolean hostHeavyAttackEnabled) {
            this.hostHeavyAttackEnabled = hostHeavyAttackEnabled;
            return this;
        }

        private StateBuilder hostHeavyAmmo(int hostHeavyAmmo) {
            this.hostHeavyAmmo = hostHeavyAmmo;
            return this;
        }

        private StateBuilder heavyDelay(int heavyDelay) {
            this.heavyDelay = heavyDelay;
            return this;
        }

        private MountBrainDecisionResolver.State build() {
            return new MountBrainDecisionResolver.State(
                    this.hostPresent,
                    this.hostAlive,
                    this.hostOrderedToSit,
                    this.mountPassenger,
                    this.hostHasPointerTarget,
                    this.hasGuardTarget,
                    this.ownerPresent,
                    this.ownerDistanceSq,
                    this.mountWidth,
                    this.followMaxConfig,
                    this.hasAttackTarget,
                    this.hasAliveAttackTarget,
                    this.randomStrollRollHit,
                    this.aimTick,
                    this.aimRequiredTicks,
                    this.targetDistanceSq,
                    this.attackRangeSq,
                    this.hostLightAttackEnabled,
                    this.hostLightAmmo,
                    this.lightDelay,
                    this.hostHeavyAttackEnabled,
                    this.hostHeavyAmmo,
                    this.heavyDelay
            );
        }
    }
}
