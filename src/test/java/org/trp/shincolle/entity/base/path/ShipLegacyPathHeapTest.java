package org.trp.shincolle.entity.base.path;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShipLegacyPathHeapTest {
    @Test
    void clearPathShouldResetAssignedPoints() {
        ShipLegacyPathHeap heap = new ShipLegacyPathHeap();
        ShipLegacyPathPoint first = point(1, 0.0F);
        ShipLegacyPathPoint second = point(2, 1.0F);

        heap.addPoint(first);
        heap.addPoint(second);

        heap.clearPath();

        assertFalse(first.isAssigned(),
                "Clearing a path heap should release points that were still queued");
        assertFalse(second.isAssigned(),
                "Clearing a path heap should release all queued points");
        assertThrows(IllegalStateException.class, heap::dequeue,
                "Cleared heaps should behave as empty heaps");
    }

    @Test
    void dequeueShouldRejectEmptyHeap() {
        ShipLegacyPathHeap heap = new ShipLegacyPathHeap();

        assertThrows(IllegalStateException.class, heap::dequeue,
                "Empty heap dequeue should fail explicitly");
    }

    @Test
    void changeDistanceShouldRejectPointsNotInHeap() {
        ShipLegacyPathHeap heap = new ShipLegacyPathHeap();
        ShipLegacyPathPoint point = point(1, 0.0F);

        assertThrows(IllegalStateException.class, () -> heap.changeDistance(point, 1.0F),
                "Distance changes should only apply to points currently owned by the heap");
    }

    @Test
    void dequeueShouldReturnLowestDistancePoint() {
        ShipLegacyPathHeap heap = new ShipLegacyPathHeap();
        ShipLegacyPathPoint far = point(1, 10.0F);
        ShipLegacyPathPoint near = point(2, 1.0F);

        heap.addPoint(far);
        heap.addPoint(near);

        assertEquals(near, heap.dequeue(),
                "Path heap should dequeue the point with the lowest distance first");
        assertFalse(near.isAssigned(),
                "Dequeued points should be released from the heap");
    }

    private static ShipLegacyPathPoint point(int x, float distance) {
        ShipLegacyPathPoint point = new ShipLegacyPathPoint(x, 0, 0, x);
        point.setDistanceToTarget(distance);
        return point;
    }
}
