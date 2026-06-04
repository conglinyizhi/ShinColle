package org.trp.shincolle.entity.base.path

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class ShipLegacyPathHeapTest {

    @Test
    fun `clear path should reset assigned points`() {
        val heap = ShipLegacyPathHeap()
        val first = point(1, 0.0f)
        val second = point(2, 1.0f)

        heap.addPoint(first)
        heap.addPoint(second)

        heap.clearPath()

        assertThat(first.isAssigned()).isFalse()
        assertThat(second.isAssigned()).isFalse()
        assertThatThrownBy { heap.dequeue() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("empty path heap")
    }

    @Test
    fun `dequeue should reject empty heap`() {
        val heap = ShipLegacyPathHeap()

        assertThatThrownBy { heap.dequeue() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("empty path heap")
    }

    @Test
    fun `change distance should reject points not in heap`() {
        val heap = ShipLegacyPathHeap()
        val point = point(1, 0.0f)

        assertThatThrownBy { heap.changeDistance(point, 1.0f) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("not assigned to this heap")
    }

    @Test
    fun `dequeue should return lowest distance point`() {
        val heap = ShipLegacyPathHeap()
        val far = point(1, 10.0f)
        val near = point(2, 1.0f)

        heap.addPoint(far)
        heap.addPoint(near)

        assertThat(heap.dequeue()).isEqualTo(near)
        assertThat(near.isAssigned()).isFalse()
        assertThat(far.isAssigned()).isTrue()
    }

    private fun point(x: Int, distance: Float): ShipLegacyPathPoint {
        return ShipLegacyPathPoint(x, 0, 0, x.toLong()).apply {
            setDistanceToTarget(distance)
        }
    }
}
