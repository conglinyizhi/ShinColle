package org.trp.shincolle.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AircraftBrainDecisionResolverTest {

    @Test
    void lightAircraftShouldRequireAliveTargetAndLightAmmo() {
        assertTrue(AircraftBrainDecisionResolver.canAttackMissionTarget(stateBuilder()
                .lightAircraft(true)
                .hasLightAmmo(true)
                .build()));
        assertFalse(AircraftBrainDecisionResolver.canAttackMissionTarget(stateBuilder()
                .lightAircraft(true)
                .hasLightAmmo(false)
                .hasHeavyAmmo(true)
                .build()));
        assertFalse(AircraftBrainDecisionResolver.canAttackMissionTarget(stateBuilder()
                .lightAircraft(true)
                .targetAlive(false)
                .hasLightAmmo(true)
                .build()));
    }

    @Test
    void heavyAircraftShouldRequireAliveTargetAndHeavyAmmo() {
        assertTrue(AircraftBrainDecisionResolver.canAttackMissionTarget(stateBuilder()
                .lightAircraft(false)
                .hasHeavyAmmo(true)
                .build()));
        assertFalse(AircraftBrainDecisionResolver.canAttackMissionTarget(stateBuilder()
                .lightAircraft(false)
                .hasLightAmmo(true)
                .hasHeavyAmmo(false)
                .build()));
        assertFalse(AircraftBrainDecisionResolver.canAttackMissionTarget(stateBuilder()
                .targetPresent(false)
                .hasHeavyAmmo(true)
                .build()));
    }

    @Test
    void attackShouldStartOnlyAfterActivationTicks() {
        assertFalse(AircraftBrainDecisionResolver.shouldStartAttack(stateBuilder()
                .missionTick(AircraftAiNumbers.ATTACK_ACTIVATION_TICKS)
                .hasLightAmmo(true)
                .build()));
        assertTrue(AircraftBrainDecisionResolver.shouldStartAttack(stateBuilder()
                .missionTick(AircraftAiNumbers.ATTACK_ACTIVATION_TICKS + 1)
                .hasLightAmmo(true)
                .build()));
    }

    @Test
    void attackRangeShouldFollowAircraftType() {
        assertEquals(36.0D, AircraftBrainDecisionResolver.attackRangeSqr(stateBuilder()
                .lightAircraft(true)
                .build()));
        assertEquals(256.0D, AircraftBrainDecisionResolver.attackRangeSqr(stateBuilder()
                .lightAircraft(false)
                .build()));
    }

    @Test
    void attackMovementShouldSlowWhileCooldownIsActive() {
        assertEquals(AircraftAiNumbers.ATTACK_SPEED_SLOW, AircraftBrainDecisionResolver.attackMoveSpeed(stateBuilder()
                .attackDelay(1)
                .distanceSqr(400.0D)
                .build()));
    }

    @Test
    void attackMovementShouldCloseFastUntilWithinRange() {
        assertEquals(AircraftAiNumbers.ATTACK_SPEED_FAST, AircraftBrainDecisionResolver.attackMoveSpeed(stateBuilder()
                .lightAircraft(true)
                .attackDelay(0)
                .distanceSqr(36.01D)
                .build()));
        assertEquals(AircraftAiNumbers.ATTACK_SPEED_SLOW, AircraftBrainDecisionResolver.attackMoveSpeed(stateBuilder()
                .lightAircraft(true)
                .attackDelay(0)
                .distanceSqr(36.0D)
                .build()));
    }

    @Test
    void fireShouldRequireAmmoCooldownLineOfSightAndStrictRange() {
        assertTrue(AircraftBrainDecisionResolver.shouldFire(stateBuilder()
                .hasLightAmmo(true)
                .attackDelay(0)
                .hasLineOfSight(true)
                .distanceSqr(35.99D)
                .build()));
        assertFalse(AircraftBrainDecisionResolver.shouldFire(stateBuilder()
                .hasLightAmmo(true)
                .attackDelay(0)
                .hasLineOfSight(true)
                .distanceSqr(36.0D)
                .build()));
        assertFalse(AircraftBrainDecisionResolver.shouldFire(stateBuilder()
                .hasLightAmmo(true)
                .attackDelay(1)
                .hasLineOfSight(true)
                .distanceSqr(35.99D)
                .build()));
        assertFalse(AircraftBrainDecisionResolver.shouldFire(stateBuilder()
                .hasLightAmmo(true)
                .attackDelay(0)
                .hasLineOfSight(false)
                .distanceSqr(35.99D)
                .build()));
        assertFalse(AircraftBrainDecisionResolver.shouldFire(stateBuilder()
                .hasLightAmmo(false)
                .attackDelay(0)
                .hasLineOfSight(true)
                .distanceSqr(35.99D)
                .build()));
    }

    private static StateBuilder stateBuilder() {
        return new StateBuilder();
    }

    private static final class StateBuilder {
        private boolean targetPresent = true;
        private boolean targetAlive = true;
        private boolean lightAircraft = true;
        private boolean hasLightAmmo;
        private boolean hasHeavyAmmo;
        private int missionTick;
        private int attackDelay;
        private boolean hasLineOfSight = true;
        private double distanceSqr;

        private StateBuilder targetPresent(boolean targetPresent) {
            this.targetPresent = targetPresent;
            return this;
        }

        private StateBuilder targetAlive(boolean targetAlive) {
            this.targetAlive = targetAlive;
            return this;
        }

        private StateBuilder lightAircraft(boolean lightAircraft) {
            this.lightAircraft = lightAircraft;
            return this;
        }

        private StateBuilder hasLightAmmo(boolean hasLightAmmo) {
            this.hasLightAmmo = hasLightAmmo;
            return this;
        }

        private StateBuilder hasHeavyAmmo(boolean hasHeavyAmmo) {
            this.hasHeavyAmmo = hasHeavyAmmo;
            return this;
        }

        private StateBuilder missionTick(int missionTick) {
            this.missionTick = missionTick;
            return this;
        }

        private StateBuilder attackDelay(int attackDelay) {
            this.attackDelay = attackDelay;
            return this;
        }

        private StateBuilder hasLineOfSight(boolean hasLineOfSight) {
            this.hasLineOfSight = hasLineOfSight;
            return this;
        }

        private StateBuilder distanceSqr(double distanceSqr) {
            this.distanceSqr = distanceSqr;
            return this;
        }

        private AircraftBrainDecisionResolver.State build() {
            return new AircraftBrainDecisionResolver.State(
                    this.targetPresent,
                    this.targetAlive,
                    this.lightAircraft,
                    this.hasLightAmmo,
                    this.hasHeavyAmmo,
                    this.missionTick,
                    this.attackDelay,
                    this.hasLineOfSight,
                    this.distanceSqr
            );
        }
    }
}
