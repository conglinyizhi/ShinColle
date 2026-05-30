package org.trp.shincolle.entity.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipPointerEntityDecisionResolverTest {
    @Test
    void ammoAttackShouldUseShipAttackRangeAndStopAfterSmallPadding() {
        ShipPointerEntityDecisionResolver.Decision inRange = ShipPointerEntityDecisionResolver.resolve(stateBuilder()
                .canUseLightAmmo(true)
                .attackRange(12.0D)
                .distanceSqr(12.0D * 12.0D + ShipAiNumbers.POINTER_ENTITY_PATH_REFRESH_DISTANCE_SQR)
                .build());
        ShipPointerEntityDecisionResolver.Decision tooFar = ShipPointerEntityDecisionResolver.resolve(stateBuilder()
                .canUseHeavyAmmo(true)
                .attackRange(12.0D)
                .distanceSqr(12.0D * 12.0D + ShipAiNumbers.POINTER_ENTITY_PATH_REFRESH_DISTANCE_SQR + 0.01D)
                .build());

        assertEquals(144.0D, inRange.preferredRangeSqr());
        assertFalse(inRange.shouldChase());
        assertTrue(tooFar.needsCloser());
        assertTrue(tooFar.shouldChase());
    }

    @Test
    void aircraftAttackShouldUseLongerCarrierRangeFloor() {
        ShipPointerEntityDecisionResolver.Decision shortStatRange = ShipPointerEntityDecisionResolver.resolve(stateBuilder()
                .hasAircraftAttackEnabled(true)
                .attackRange(8.0D)
                .distanceSqr(24.0D * 24.0D)
                .build());
        ShipPointerEntityDecisionResolver.Decision scaledStatRange = ShipPointerEntityDecisionResolver.resolve(stateBuilder()
                .hasAircraftAttackEnabled(true)
                .attackRange(20.0D)
                .distanceSqr(30.0D * 30.0D + ShipAiNumbers.POINTER_ENTITY_PATH_REFRESH_DISTANCE_SQR + 0.01D)
                .build());

        assertEquals(24.0D * 24.0D, shortStatRange.preferredRangeSqr());
        assertFalse(shortStatRange.shouldChase());
        assertEquals(30.0D * 30.0D, scaledStatRange.preferredRangeSqr());
        assertTrue(scaledStatRange.shouldChase());
    }

    @Test
    void meleeAttackShouldUseBodyReachAndMinimumAttackRange() {
        ShipPointerEntityDecisionResolver.Decision minimumRange = ShipPointerEntityDecisionResolver.resolve(stateBuilder()
                .canUseMeleeAttack(true)
                .shipWidth(0.6D)
                .targetWidth(0.6D)
                .distanceSqr(ShipAiNumbers.POINTER_ENTITY_ATTACK_RANGE_SQR)
                .build());
        ShipPointerEntityDecisionResolver.Decision largeBodyRange = ShipPointerEntityDecisionResolver.resolve(stateBuilder()
                .canUseMeleeAttack(true)
                .shipWidth(2.0D)
                .targetWidth(1.0D)
                .distanceSqr(17.01D)
                .build());

        assertEquals(ShipAiNumbers.POINTER_ENTITY_ATTACK_RANGE_SQR, minimumRange.attackRangeSqr());
        assertFalse(minimumRange.shouldChase());
        assertEquals(17.0D, largeBodyRange.attackRangeSqr());
        assertTrue(largeBodyRange.needsCloser());
    }

    @Test
    void lostLineOfSightShouldChaseWhenTargetIsNotAlreadyNearPreferredRange() {
        ShipPointerEntityDecisionResolver.Decision nearBlind = ShipPointerEntityDecisionResolver.resolve(stateBuilder()
                .canUseLightAmmo(true)
                .attackRange(10.0D)
                .hasLineOfSight(false)
                .distanceSqr(50.0D)
                .build());
        ShipPointerEntityDecisionResolver.Decision farBlind = ShipPointerEntityDecisionResolver.resolve(stateBuilder()
                .canUseLightAmmo(true)
                .attackRange(10.0D)
                .hasLineOfSight(false)
                .distanceSqr(50.01D)
                .build());

        assertFalse(nearBlind.cannotSee());
        assertFalse(nearBlind.shouldChase());
        assertTrue(farBlind.cannotSee());
        assertTrue(farBlind.shouldChase());
    }

    @Test
    void missingTargetShouldNotChaseEvenWhenDistanceWouldOtherwiseRequireMovement() {
        ShipPointerEntityDecisionResolver.Decision decision = ShipPointerEntityDecisionResolver.resolve(stateBuilder()
                .targetPresent(false)
                .canUseLightAmmo(true)
                .attackRange(10.0D)
                .hasLineOfSight(false)
                .distanceSqr(10_000.0D)
                .build());

        assertFalse(decision.needsCloser());
        assertFalse(decision.cannotSee());
        assertFalse(decision.shouldChase());
    }

    @Test
    void aimDelayShouldShrinkWithShipLevelAndKeepMinimum() {
        assertEquals(30, ShipPointerEntityDecisionResolver.aimDelayTicks(0));
        assertEquals(20, ShipPointerEntityDecisionResolver.aimDelayTicks(75));
        assertEquals(10, ShipPointerEntityDecisionResolver.aimDelayTicks(150));
        assertEquals(ShipAiNumbers.POINTER_ENTITY_AIM_DELAY_MIN,
                ShipPointerEntityDecisionResolver.aimDelayTicks(300));
    }

    @Test
    void lightAttackShouldRequireAmmoAndElapsedCadence() {
        assertTrue(ShipPointerEntityDecisionResolver.shouldFireLightAttack(attackStateBuilder()
                .canUseLightAmmo(true)
                .lightAttackInterval(5)
                .tickCount(25)
                .lastLightShotTick(20)
                .build()));
        assertFalse(ShipPointerEntityDecisionResolver.shouldFireLightAttack(attackStateBuilder()
                .canUseLightAmmo(true)
                .lightAttackInterval(5)
                .tickCount(24)
                .lastLightShotTick(20)
                .build()));
        assertFalse(ShipPointerEntityDecisionResolver.shouldFireLightAttack(attackStateBuilder()
                .canUseLightAmmo(false)
                .lightAttackInterval(5)
                .tickCount(25)
                .lastLightShotTick(20)
                .build()));
    }

    @Test
    void heavyAttackShouldRequireAmmoAndElapsedCadence() {
        assertTrue(ShipPointerEntityDecisionResolver.shouldFireHeavyAttack(attackStateBuilder()
                .canUseHeavyAmmo(true)
                .heavyAttackInterval(7)
                .tickCount(27)
                .lastHeavyShotTick(20)
                .build()));
        assertFalse(ShipPointerEntityDecisionResolver.shouldFireHeavyAttack(attackStateBuilder()
                .canUseHeavyAmmo(true)
                .heavyAttackInterval(7)
                .tickCount(26)
                .lastHeavyShotTick(20)
                .build()));
        assertFalse(ShipPointerEntityDecisionResolver.shouldFireHeavyAttack(attackStateBuilder()
                .canUseHeavyAmmo(false)
                .heavyAttackInterval(7)
                .tickCount(27)
                .lastHeavyShotTick(20)
                .build()));
    }

    @Test
    void meleeAttackShouldRequireReachAndElapsedCadence() {
        assertTrue(ShipPointerEntityDecisionResolver.shouldFireMeleeAttack(attackStateBuilder()
                .canUseMeleeAttack(true)
                .targetDistanceSqr(4.0D)
                .attackRangeSqr(4.0D)
                .meleeAttackInterval(4)
                .tickCount(24)
                .lastMeleeAttackTick(20)
                .build()));
        assertFalse(ShipPointerEntityDecisionResolver.shouldFireMeleeAttack(attackStateBuilder()
                .canUseMeleeAttack(true)
                .targetDistanceSqr(4.01D)
                .attackRangeSqr(4.0D)
                .meleeAttackInterval(4)
                .tickCount(24)
                .lastMeleeAttackTick(20)
                .build()));
        assertFalse(ShipPointerEntityDecisionResolver.shouldFireMeleeAttack(attackStateBuilder()
                .canUseMeleeAttack(true)
                .targetDistanceSqr(4.0D)
                .attackRangeSqr(4.0D)
                .meleeAttackInterval(4)
                .tickCount(23)
                .lastMeleeAttackTick(20)
                .build()));
    }

    private static StateBuilder stateBuilder() {
        return new StateBuilder();
    }

    private static AttackStateBuilder attackStateBuilder() {
        return new AttackStateBuilder();
    }

    private static final class StateBuilder {
        private boolean targetPresent = true;
        private double distanceSqr;
        private boolean hasLineOfSight = true;
        private boolean canUseLightAmmo;
        private boolean canUseHeavyAmmo;
        private boolean hasAircraftAttackEnabled;
        private boolean canUseMeleeAttack;
        private double attackRange = 10.0D;
        private double shipWidth = 0.6D;
        private double targetWidth = 0.6D;

        private StateBuilder targetPresent(boolean targetPresent) {
            this.targetPresent = targetPresent;
            return this;
        }

        private StateBuilder distanceSqr(double distanceSqr) {
            this.distanceSqr = distanceSqr;
            return this;
        }

        private StateBuilder hasLineOfSight(boolean hasLineOfSight) {
            this.hasLineOfSight = hasLineOfSight;
            return this;
        }

        private StateBuilder canUseLightAmmo(boolean canUseLightAmmo) {
            this.canUseLightAmmo = canUseLightAmmo;
            return this;
        }

        private StateBuilder canUseHeavyAmmo(boolean canUseHeavyAmmo) {
            this.canUseHeavyAmmo = canUseHeavyAmmo;
            return this;
        }

        private StateBuilder hasAircraftAttackEnabled(boolean hasAircraftAttackEnabled) {
            this.hasAircraftAttackEnabled = hasAircraftAttackEnabled;
            return this;
        }

        private StateBuilder canUseMeleeAttack(boolean canUseMeleeAttack) {
            this.canUseMeleeAttack = canUseMeleeAttack;
            return this;
        }

        private StateBuilder attackRange(double attackRange) {
            this.attackRange = attackRange;
            return this;
        }

        private StateBuilder shipWidth(double shipWidth) {
            this.shipWidth = shipWidth;
            return this;
        }

        private StateBuilder targetWidth(double targetWidth) {
            this.targetWidth = targetWidth;
            return this;
        }

        private ShipPointerEntityDecisionResolver.State build() {
            return new ShipPointerEntityDecisionResolver.State(
                    this.targetPresent,
                    this.distanceSqr,
                    this.hasLineOfSight,
                    this.canUseLightAmmo,
                    this.canUseHeavyAmmo,
                    this.hasAircraftAttackEnabled,
                    this.canUseMeleeAttack,
                    this.attackRange,
                    this.shipWidth,
                    this.targetWidth
            );
        }
    }

    private static final class AttackStateBuilder {
        private int tickCount;
        private boolean canUseLightAmmo;
        private int lightAttackInterval = 1;
        private int lastLightShotTick;
        private boolean canUseHeavyAmmo;
        private int heavyAttackInterval = 1;
        private int lastHeavyShotTick;
        private boolean canUseMeleeAttack;
        private double targetDistanceSqr;
        private double attackRangeSqr;
        private int meleeAttackInterval = 1;
        private int lastMeleeAttackTick;

        private AttackStateBuilder tickCount(int tickCount) {
            this.tickCount = tickCount;
            return this;
        }

        private AttackStateBuilder canUseLightAmmo(boolean canUseLightAmmo) {
            this.canUseLightAmmo = canUseLightAmmo;
            return this;
        }

        private AttackStateBuilder lightAttackInterval(int lightAttackInterval) {
            this.lightAttackInterval = lightAttackInterval;
            return this;
        }

        private AttackStateBuilder lastLightShotTick(int lastLightShotTick) {
            this.lastLightShotTick = lastLightShotTick;
            return this;
        }

        private AttackStateBuilder canUseHeavyAmmo(boolean canUseHeavyAmmo) {
            this.canUseHeavyAmmo = canUseHeavyAmmo;
            return this;
        }

        private AttackStateBuilder heavyAttackInterval(int heavyAttackInterval) {
            this.heavyAttackInterval = heavyAttackInterval;
            return this;
        }

        private AttackStateBuilder lastHeavyShotTick(int lastHeavyShotTick) {
            this.lastHeavyShotTick = lastHeavyShotTick;
            return this;
        }

        private AttackStateBuilder canUseMeleeAttack(boolean canUseMeleeAttack) {
            this.canUseMeleeAttack = canUseMeleeAttack;
            return this;
        }

        private AttackStateBuilder targetDistanceSqr(double targetDistanceSqr) {
            this.targetDistanceSqr = targetDistanceSqr;
            return this;
        }

        private AttackStateBuilder attackRangeSqr(double attackRangeSqr) {
            this.attackRangeSqr = attackRangeSqr;
            return this;
        }

        private AttackStateBuilder meleeAttackInterval(int meleeAttackInterval) {
            this.meleeAttackInterval = meleeAttackInterval;
            return this;
        }

        private AttackStateBuilder lastMeleeAttackTick(int lastMeleeAttackTick) {
            this.lastMeleeAttackTick = lastMeleeAttackTick;
            return this;
        }

        private ShipPointerEntityDecisionResolver.AttackState build() {
            return new ShipPointerEntityDecisionResolver.AttackState(
                    this.tickCount,
                    this.canUseLightAmmo,
                    this.lightAttackInterval,
                    this.lastLightShotTick,
                    this.canUseHeavyAmmo,
                    this.heavyAttackInterval,
                    this.lastHeavyShotTick,
                    this.canUseMeleeAttack,
                    this.targetDistanceSqr,
                    this.attackRangeSqr,
                    this.meleeAttackInterval,
                    this.lastMeleeAttackTick
            );
        }
    }
}
