package org.trp.shincolle.entity.base

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShipBrainActivityResolverTest {

    @Test
    fun `pointer command should outrank combat follow and idle`() {
        val state = stateBuilder()
            .hasPointerTarget(true)
            .hasAttackTarget(true)
            .canGuard(true)
            .ownerDistanceSq(400.0)
            .build()

        assertThat(ShipBrainActivityResolver.resolveActiveModes(state, false)).containsExactly(
            ShipBrainActivityResolver.Mode.COMMAND,
            ShipBrainActivityResolver.Mode.COMBAT,
            ShipBrainActivityResolver.Mode.FOLLOW,
            ShipBrainActivityResolver.Mode.IDLE
        )
        assertThat(ShipBrainActivityResolver.describeDesiredActivity(state)).isEqualTo("COMMAND")
    }

    @Test
    fun `pointer entity command should use same command activity as pointer point`() {
        val state = stateBuilder()
            .hasPointerTarget(true)
            .ownerDistanceSq(0.0)
            .build()

        assertThat(ShipBrainActivityResolver.resolveActiveModes(state, false)).containsExactly(
            ShipBrainActivityResolver.Mode.COMMAND,
            ShipBrainActivityResolver.Mode.COMBAT,
            ShipBrainActivityResolver.Mode.FOLLOW,
            ShipBrainActivityResolver.Mode.IDLE
        )
        assertThat(ShipBrainActivityResolver.describeDesiredActivity(state)).isEqualTo("COMMAND")
    }

    @Test
    fun `combat should suppress follow when no pointer command exists`() {
        val state = stateBuilder()
            .hasAttackTarget(true)
            .canGuard(true)
            .ownerDistanceSq(400.0)
            .build()

        assertThat(ShipBrainActivityResolver.resolveActiveModes(state, false)).containsExactly(
            ShipBrainActivityResolver.Mode.COMBAT,
            ShipBrainActivityResolver.Mode.IDLE
        )
        assertThat(ShipBrainActivityResolver.describeDesiredActivity(state)).isEqualTo("COMBAT")
    }

    @Test
    fun `guard should outrank follow when no command or combat exists`() {
        val state = stateBuilder()
            .canGuard(true)
            .ownerDistanceSq(400.0)
            .build()

        assertThat(ShipBrainActivityResolver.resolveActiveModes(state, false)).containsExactly(
            ShipBrainActivityResolver.Mode.GUARD,
            ShipBrainActivityResolver.Mode.FOLLOW,
            ShipBrainActivityResolver.Mode.IDLE
        )
        assertThat(ShipBrainActivityResolver.describeDesiredActivity(state)).isEqualTo("GUARD")
    }

    @Test
    fun `follow should use max distance to start and min distance to continue`() {
        val belowStart = stateBuilder()
            .ownerDistanceSq((ShipAiNumbers.FOLLOW_OWNER_MAX_DIST * ShipAiNumbers.FOLLOW_OWNER_MAX_DIST).toDouble())
            .build()
        val aboveStart = stateBuilder()
            .ownerDistanceSq(square(ShipAiNumbers.FOLLOW_OWNER_MAX_DIST.toDouble() + 0.5))
            .build()
        val aboveMinWhileFollowing = stateBuilder()
            .ownerDistanceSq(square(ShipAiNumbers.FOLLOW_OWNER_MIN_DIST.toDouble() + 0.5))
            .build()
        val belowMinWhileFollowing = stateBuilder()
            .ownerDistanceSq(square(ShipAiNumbers.FOLLOW_OWNER_MIN_DIST.toDouble() - 0.5))
            .build()

        assertThat(ShipBrainActivityResolver.shouldFollowOwner(belowStart, false)).isFalse()
        assertThat(ShipBrainActivityResolver.shouldFollowOwner(aboveStart, false)).isTrue()
        assertThat(ShipBrainActivityResolver.shouldFollowOwner(aboveMinWhileFollowing, true)).isTrue()
        assertThat(ShipBrainActivityResolver.shouldFollowOwner(belowMinWhileFollowing, true)).isFalse()
    }

    @Test
    fun `follow should not compete with combat target`() {
        val combat = stateBuilder()
            .hasAttackTarget(true)
            .ownerDistanceSq(square(ShipAiNumbers.FOLLOW_OWNER_MAX_DIST.toDouble() + 0.5))
            .build()

        assertThat(ShipBrainActivityResolver.shouldFollowOwner(combat, false)).isFalse()
        assertThat(ShipBrainActivityResolver.shouldFollowOwner(combat, true)).isFalse()
    }

    @Test
    fun `follow distance config should clamp and keep max above min`() {
        val min = ShipBrainActivityResolver.resolveFollowMinDistance(99)
        val max = ShipBrainActivityResolver.resolveFollowMaxDistance(1, min)

        assertThat(min).isEqualTo(ShipAiNumbers.FOLLOW_MIN_DIST_CONFIG_MAX.toFloat())
        assertThat(max).isEqualTo(ShipAiNumbers.FOLLOW_MAX_DIST_CONFIG_MAX.toFloat())
    }

    @Test
    fun `combat ration should use short follow threshold`() {
        val close = stateBuilder()
            .ownerHasCombatRation(true)
            .ownerDistanceSq(ShipAiNumbers.FOLLOW_RATION_DISTANCE_SQ)
            .build()
        val far = stateBuilder()
            .ownerHasCombatRation(true)
            .ownerDistanceSq(ShipAiNumbers.FOLLOW_RATION_DISTANCE_SQ + 0.01)
            .build()

        assertThat(ShipBrainActivityResolver.shouldFollowOwner(close, false)).isFalse()
        assertThat(ShipBrainActivityResolver.shouldFollowOwner(far, false)).isTrue()
    }

    @Test
    fun `follow continuation should use unified resolver state`() {
        val blockedByDimension = followStateBuilder()
            .hasSameDimensionOwnerPosition(false)
            .ownerDistanceSq(400.0)
            .build()
        val closeWhileRationed = followStateBuilder()
            .ownerHasCombatRation(true)
            .ownerDistanceSq(ShipAiNumbers.FOLLOW_RATION_DISTANCE_SQ)
            .build()
        val farWhileRationed = followStateBuilder()
            .ownerHasCombatRation(true)
            .ownerDistanceSq(ShipAiNumbers.FOLLOW_RATION_DISTANCE_SQ + 0.01)
            .build()
        val belowMinDistance = followStateBuilder()
            .followMinConfig(ShipAiNumbers.FOLLOW_OWNER_MIN_DIST.toInt())
            .ownerDistanceSq(square(ShipAiNumbers.FOLLOW_OWNER_MIN_DIST.toDouble()) - 0.01)
            .build()
        val aboveMinDistance = followStateBuilder()
            .followMinConfig(ShipAiNumbers.FOLLOW_OWNER_MIN_DIST.toInt())
            .ownerDistanceSq(square(ShipAiNumbers.FOLLOW_OWNER_MIN_DIST.toDouble()) + 0.01)
            .build()

        assertThat(ShipBrainActivityResolver.shouldContinueFollow(blockedByDimension)).isFalse()
        assertThat(ShipBrainActivityResolver.shouldContinueFollow(closeWhileRationed)).isFalse()
        assertThat(ShipBrainActivityResolver.shouldContinueFollow(farWhileRationed)).isTrue()
        assertThat(ShipBrainActivityResolver.shouldContinueFollow(belowMinDistance)).isFalse()
        assertThat(ShipBrainActivityResolver.shouldContinueFollow(aboveMinDistance)).isTrue()
    }

    @Test
    fun `idle should be only activity when movement or owner follow is unavailable`() {
        val noOwner = stateBuilder()
            .ownerPresent(false)
            .ownerDistanceSq(400.0)
            .build()
        val cannotMove = stateBuilder()
            .canMove(false)
            .hasPointerTarget(true)
            .canGuard(true)
            .ownerDistanceSq(400.0)
            .build()

        assertThat(ShipBrainActivityResolver.resolveActiveModes(noOwner, false))
            .containsExactly(ShipBrainActivityResolver.Mode.IDLE)
        assertThat(ShipBrainActivityResolver.resolveActiveModes(cannotMove, false))
            .containsExactly(ShipBrainActivityResolver.Mode.IDLE)
    }

    private fun square(value: Double): Double = value * value

    private fun stateBuilder(): StateBuilder = StateBuilder()

    private fun followStateBuilder(): FollowStateBuilder = FollowStateBuilder()

    private class StateBuilder {
        private var canMove = true
        private var hasPointerTarget = false
        private var hasAttackTarget = false
        private var canGuard = false
        private var shouldFollowOwner = true
        private var ownerPresent = true
        private var ownerHasCombatRation = false
        private var ownerDistanceSq = 0.0
        private var followMinConfig = 0
        private var followMaxConfig = 0

        fun canMove(canMove: Boolean) = apply { this.canMove = canMove }
        fun hasPointerTarget(hasPointerTarget: Boolean) = apply { this.hasPointerTarget = hasPointerTarget }
        fun hasAttackTarget(hasAttackTarget: Boolean) = apply { this.hasAttackTarget = hasAttackTarget }
        fun canGuard(canGuard: Boolean) = apply { this.canGuard = canGuard }
        fun ownerPresent(ownerPresent: Boolean) = apply { this.ownerPresent = ownerPresent }
        fun ownerHasCombatRation(ownerHasCombatRation: Boolean) = apply { this.ownerHasCombatRation = ownerHasCombatRation }
        fun ownerDistanceSq(ownerDistanceSq: Double) = apply { this.ownerDistanceSq = ownerDistanceSq }

        fun build(): ShipBrainActivityResolver.State {
            return ShipBrainActivityResolver.State(
                canMove,
                hasPointerTarget,
                hasAttackTarget,
                canGuard,
                shouldFollowOwner,
                ownerPresent,
                ownerHasCombatRation,
                ownerDistanceSq,
                followMinConfig,
                followMaxConfig
            )
        }
    }

    private class FollowStateBuilder {
        private var canMove = true
        private var shouldFollow = true
        private var hasSameDimensionOwnerPosition = true
        private var ownerHasCombatRation = false
        private var ownerDistanceSq = 0.0
        private var followMinConfig = 0

        fun hasSameDimensionOwnerPosition(hasSameDimensionOwnerPosition: Boolean) = apply {
            this.hasSameDimensionOwnerPosition = hasSameDimensionOwnerPosition
        }

        fun ownerHasCombatRation(ownerHasCombatRation: Boolean) = apply {
            this.ownerHasCombatRation = ownerHasCombatRation
        }

        fun ownerDistanceSq(ownerDistanceSq: Double) = apply { this.ownerDistanceSq = ownerDistanceSq }
        fun followMinConfig(followMinConfig: Int) = apply { this.followMinConfig = followMinConfig }

        fun build(): ShipBrainActivityResolver.FollowState {
            return ShipBrainActivityResolver.FollowState(
                canMove,
                shouldFollow,
                hasSameDimensionOwnerPosition,
                ownerHasCombatRation,
                ownerDistanceSq,
                followMinConfig
            )
        }
    }
}
