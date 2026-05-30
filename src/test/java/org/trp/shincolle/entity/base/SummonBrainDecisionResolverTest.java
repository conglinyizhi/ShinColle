package org.trp.shincolle.entity.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SummonBrainDecisionResolverTest {

    @Test
    void attackShouldRequireAliveTarget() {
        assertTrue(SummonBrainDecisionResolver.shouldAttack(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(true)
                .build()));
        assertFalse(SummonBrainDecisionResolver.shouldAttack(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(false)
                .build()));
    }

    @Test
    void chaseShouldOnlyContinueWhileTargetIsAliveAndOutOfRange() {
        assertTrue(SummonBrainDecisionResolver.shouldChaseAttackTarget(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(true)
                .targetDistanceSq(16.01D)
                .attackRangeSq(16.0D)
                .build()));
        assertFalse(SummonBrainDecisionResolver.shouldChaseAttackTarget(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(true)
                .targetDistanceSq(16.0D)
                .attackRangeSq(16.0D)
                .build()));
        assertFalse(SummonBrainDecisionResolver.shouldChaseAttackTarget(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(false)
                .targetDistanceSq(20.0D)
                .attackRangeSq(16.0D)
                .build()));
    }

    @Test
    void performAttackShouldRequireAliveTargetCooldownAndInRange() {
        assertTrue(SummonBrainDecisionResolver.shouldPerformAttack(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(true)
                .targetDistanceSq(16.0D)
                .attackRangeSq(16.0D)
                .attackDelay(0)
                .build()));
        assertFalse(SummonBrainDecisionResolver.shouldPerformAttack(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(true)
                .targetDistanceSq(16.01D)
                .attackRangeSq(16.0D)
                .attackDelay(0)
                .build()));
        assertFalse(SummonBrainDecisionResolver.shouldPerformAttack(stateBuilder()
                .hasAttackTarget(true)
                .hasAliveAttackTarget(true)
                .targetDistanceSq(16.0D)
                .attackRangeSq(16.0D)
                .attackDelay(1)
                .build()));
    }

    @Test
    void followCarrierShouldRequireAliveCarrierDistanceAndNoAttackTarget() {
        assertTrue(SummonBrainDecisionResolver.shouldFollowCarrier(stateBuilder()
                .carrierDistanceSq(SummonAiNumbers.FOLLOW_CARRIER_DISTANCE_SQ + 0.01D)
                .build()));
        assertFalse(SummonBrainDecisionResolver.shouldFollowCarrier(stateBuilder()
                .carrierDistanceSq(SummonAiNumbers.FOLLOW_CARRIER_DISTANCE_SQ)
                .build()));
        assertFalse(SummonBrainDecisionResolver.shouldFollowCarrier(stateBuilder()
                .carrierPresent(false)
                .build()));
        assertFalse(SummonBrainDecisionResolver.shouldFollowCarrier(stateBuilder()
                .carrierAlive(false)
                .build()));
        assertFalse(SummonBrainDecisionResolver.shouldFollowCarrier(stateBuilder()
                .hasAttackTarget(true)
                .build()));
    }

    @Test
    void randomStrollShouldNotRaceCarrierFollowOrCombatMovement() {
        assertTrue(SummonBrainDecisionResolver.shouldRandomStroll(stateBuilder()
                .carrierDistanceSq(0.0D)
                .randomStrollRollHit(true)
                .build()));
        assertFalse(SummonBrainDecisionResolver.shouldRandomStroll(stateBuilder()
                .carrierPresent(false)
                .randomStrollRollHit(true)
                .build()));
        assertFalse(SummonBrainDecisionResolver.shouldRandomStroll(stateBuilder()
                .carrierAlive(false)
                .randomStrollRollHit(true)
                .build()));
        assertFalse(SummonBrainDecisionResolver.shouldRandomStroll(stateBuilder()
                .hasAttackTarget(true)
                .randomStrollRollHit(true)
                .build()));
        assertFalse(SummonBrainDecisionResolver.shouldRandomStroll(stateBuilder()
                .carrierDistanceSq(SummonAiNumbers.FOLLOW_CARRIER_DISTANCE_SQ + 0.01D)
                .randomStrollRollHit(true)
                .build()));
        assertFalse(SummonBrainDecisionResolver.shouldRandomStroll(stateBuilder()
                .randomStrollRollHit(false)
                .build()));
    }

    private static StateBuilder stateBuilder() {
        return new StateBuilder();
    }

    private static final class StateBuilder {
        private boolean carrierPresent = true;
        private boolean carrierAlive = true;
        private double carrierDistanceSq;
        private boolean hasAttackTarget;
        private boolean hasAliveAttackTarget;
        private boolean randomStrollRollHit;
        private double targetDistanceSq;
        private double attackRangeSq = SummonAiNumbers.DEFAULT_ATTACK_RANGE_SQ;
        private int attackDelay;

        private StateBuilder carrierPresent(boolean carrierPresent) {
            this.carrierPresent = carrierPresent;
            return this;
        }

        private StateBuilder carrierAlive(boolean carrierAlive) {
            this.carrierAlive = carrierAlive;
            return this;
        }

        private StateBuilder carrierDistanceSq(double carrierDistanceSq) {
            this.carrierDistanceSq = carrierDistanceSq;
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

        private StateBuilder targetDistanceSq(double targetDistanceSq) {
            this.targetDistanceSq = targetDistanceSq;
            return this;
        }

        private StateBuilder attackRangeSq(double attackRangeSq) {
            this.attackRangeSq = attackRangeSq;
            return this;
        }

        private StateBuilder attackDelay(int attackDelay) {
            this.attackDelay = attackDelay;
            return this;
        }

        private SummonBrainDecisionResolver.State build() {
            return new SummonBrainDecisionResolver.State(
                    this.carrierPresent,
                    this.carrierAlive,
                    this.carrierDistanceSq,
                    this.hasAttackTarget,
                    this.hasAliveAttackTarget,
                    this.randomStrollRollHit,
                    this.targetDistanceSq,
                    this.attackRangeSq,
                    this.attackDelay
            );
        }
    }
}
