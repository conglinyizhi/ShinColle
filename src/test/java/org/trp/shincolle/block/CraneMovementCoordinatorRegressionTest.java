package org.trp.shincolle.block;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CraneMovementCoordinatorRegressionTest {
    private static final Path CRANE_BE =
            Path.of("src/main/java/org/trp/shincolle/block/entity/CraneBlockEntity.java");

    @Test
    void craneShouldRouteShipMovementThroughCoordinator() throws IOException {
        String source = Files.readString(CRANE_BE);

        assertTrue(source.contains("import org.trp.shincolle.entity.base.ShipMovementCoordinator;"),
                "Crane should use the shared movement coordinator");
        assertTrue(source.contains("private void moveShipToCrane(EntityShipBase ship)"),
                "Crane ship movement should be centralized in a helper");
        assertTrue(source.contains("new ShipMovementCoordinator(ship).moveTo("),
                "Crane movement helper should route movement through the coordinator");
        assertTrue(source.contains("moveShipToCrane(this.craningShip);"),
                "Existing craning ship should use the movement helper");
        assertTrue(source.contains("moveShipToCrane(ship);"),
                "Newly detected craning ship should use the movement helper");
        assertFalse(source.contains("getNavigation().moveTo(this.worldPosition.getX() + 0.5"),
                "Crane should not issue raw navigation requests to its working position");
    }
}
