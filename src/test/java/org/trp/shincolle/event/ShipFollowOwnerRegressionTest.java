package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipFollowOwnerRegressionTest {
    private static final Path SHIP_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.java");

    @Test
    void followOwnerShouldStillRequireFuelAndNoGuardBlockState() throws IOException {
        String source = Files.readString(SHIP_SOURCE);

        assertTrue(source.contains("if (this.isNoFuel()) {\n            return false;\n        }"),
                "Ship follow-owner logic should refuse to follow without grudge/fuel");
        assertTrue(source.contains("if (this.getGuardedPos(4) == 1 || this.hasPointerTarget()) {"),
                "Ship follow-owner logic should still avoid active guard-block mode");
    }

    @Test
    void standingUpShouldClearLegacyGuardPositionSoFollowCanResume() throws IOException {
        String source = Files.readString(SHIP_SOURCE);

        assertTrue(source.contains("if (!isSitting && this.getGuardedPos(4) == 1) {\n                this.setGuardedPos(-1, -1, -1, 0, 0);\n                this.getNavigation().stop();\n            }"),
                "Ship stand-up interaction should clear stale guard-block mode");
    }
}
