package org.trp.shincolle.entity.base.path;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShipLegacyPathTest {
    @Test
    void currentPathIndexShouldStayWithinPathBounds() {
        ShipLegacyPath path = new ShipLegacyPath(new ShipLegacyPathPoint[]{
                new ShipLegacyPathPoint(1, 2, 3, 1L),
                new ShipLegacyPathPoint(4, 5, 6, 2L)
        });

        path.setCurrentPathIndex(-5);
        assertEquals(0, path.getCurrentPathIndex(),
                "Negative path indexes should clamp to the first node");

        path.setCurrentPathIndex(99);
        assertEquals(2, path.getCurrentPathIndex(),
                "Indexes past the end should clamp to the finished sentinel");

        path.incrementPathIndex();
        assertEquals(2, path.getCurrentPathIndex(),
                "Incrementing a finished path should not move past the sentinel");
    }
}
