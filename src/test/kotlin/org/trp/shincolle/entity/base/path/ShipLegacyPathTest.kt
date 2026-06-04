package org.trp.shincolle.entity.base.path

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShipLegacyPathTest {

    @Test
    fun `current path index should stay within path bounds`() {
        val path = ShipLegacyPath(
            arrayOf(
                ShipLegacyPathPoint(1, 2, 3, 1L),
                ShipLegacyPathPoint(4, 5, 6, 2L)
            )
        )

        path.setCurrentPathIndex(-5)
        assertThat(path.currentPathIndex).isZero()

        path.setCurrentPathIndex(99)
        assertThat(path.currentPathIndex).isEqualTo(2)

        path.incrementPathIndex()
        assertThat(path.currentPathIndex).isEqualTo(2)
    }

    @Test
    fun `finished or invalid path positions should report missing targets`() {
        val path = ShipLegacyPath(
            arrayOf(
                ShipLegacyPathPoint(1, 2, 3, 1L)
            )
        )

        assertThat(path.getPathPointFromIndex(-1)).isNull()
        assertThat(path.getPathPointFromIndex(1)).isNull()

        path.setCurrentPathIndex(1)
        assertThat(path.currentPos).isNull()
    }
}
