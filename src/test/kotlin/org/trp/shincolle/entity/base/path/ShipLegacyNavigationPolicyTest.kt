package org.trp.shincolle.entity.base.path

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShipLegacyNavigationPolicyTest {

    @Test
    fun `same navigation target should use legacy drift tolerance`() {
        val origin = ShipLegacyNavigationPolicy.Target(0, 64, 0)

        assertThat(
            ShipLegacyNavigationPolicy.isSameNavigationTarget(
                origin,
                ShipLegacyNavigationPolicy.Target(3, 64, 0)
            )
        ).isTrue()
        assertThat(
            ShipLegacyNavigationPolicy.isSameNavigationTarget(
                origin,
                ShipLegacyNavigationPolicy.Target(4, 64, 0)
            )
        ).isFalse()
        assertThat(ShipLegacyNavigationPolicy.isSameNavigationTarget(null, origin)).isFalse()
    }

    @Test
    fun `stuck progress should reset only for fresh paths or real target changes`() {
        assertThat(ShipLegacyNavigationPolicy.shouldResetStuckProgress(false, true)).isTrue()
        assertThat(ShipLegacyNavigationPolicy.shouldResetStuckProgress(true, false)).isTrue()
        assertThat(ShipLegacyNavigationPolicy.shouldResetStuckProgress(true, true)).isFalse()
    }

    @Test
    fun `set path logs should trigger for failure change target change or throttle expiry`() {
        val target = ShipLegacyNavigationPolicy.Target(8, 64, 8)
        val nearby = ShipLegacyNavigationPolicy.Target(10, 64, 8)
        val far = ShipLegacyNavigationPolicy.Target(12, 64, 8)

        assertThat(ShipLegacyNavigationPolicy.shouldLogSetPath(false, true, target, target, 0, 0)).isTrue()
        assertThat(ShipLegacyNavigationPolicy.shouldLogSetPath(false, false, target, far, 0, 0)).isTrue()
        assertThat(
            ShipLegacyNavigationPolicy.shouldLogSetPath(
                false,
                false,
                target,
                nearby,
                ShipLegacyNavigationPolicy.NAVIGATION_SET_PATH_LOG_INTERVAL - 1,
                0
            )
        ).isFalse()
        assertThat(
            ShipLegacyNavigationPolicy.shouldLogSetPath(
                false,
                false,
                target,
                nearby,
                ShipLegacyNavigationPolicy.NAVIGATION_SET_PATH_LOG_INTERVAL,
                0
            )
        ).isTrue()
    }

    @Test
    fun `navigation event logs should respect target drift and throttle`() {
        val target = ShipLegacyNavigationPolicy.Target(8, 64, 8)
        val nearby = ShipLegacyNavigationPolicy.Target(10, 64, 8)
        val far = ShipLegacyNavigationPolicy.Target(12, 64, 8)

        assertThat(ShipLegacyNavigationPolicy.shouldLogNavigationEvent(target, target, 0, Int.MIN_VALUE)).isTrue()
        assertThat(ShipLegacyNavigationPolicy.shouldLogNavigationEvent(target, far, 0, 0)).isTrue()
        assertThat(
            ShipLegacyNavigationPolicy.shouldLogNavigationEvent(
                target,
                nearby,
                ShipLegacyNavigationPolicy.NAVIGATION_DEBUG_LOG_INTERVAL - 1,
                0
            )
        ).isFalse()
        assertThat(
            ShipLegacyNavigationPolicy.shouldLogNavigationEvent(
                target,
                nearby,
                ShipLegacyNavigationPolicy.NAVIGATION_DEBUG_LOG_INTERVAL,
                0
            )
        ).isTrue()
    }

    @Test
    fun `timeout policy should clamp speed and retry after double limit`() {
        assertThat(ShipLegacyNavigationPolicy.calculateTimeoutLimit(1.0, 0.0)).isEqualTo(6000.0)
        assertThat(ShipLegacyNavigationPolicy.shouldRetryTimedOutPath(200L, 100.0)).isFalse()
        assertThat(ShipLegacyNavigationPolicy.shouldRetryTimedOutPath(201L, 100.0)).isTrue()
        assertThat(ShipLegacyNavigationPolicy.shouldRetryTimedOutPath(1L, 0.0)).isFalse()
    }
}
