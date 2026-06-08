package org.trp.shincolle.event

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class ShipFollowOwnerRegressionTest {
    private val SHIP_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt")

    @Test
    fun followOwnerShouldStillRequireFuelAndNoGuardBlockState() {
        val source = Files.readString(SHIP_SOURCE)

        assertTrue(source.contains("if (this.isNoFuel()) {\n            return false;\n        }")) {
            "Ship follow-owner logic should refuse to follow without grudge/fuel"
        }
        assertTrue(source.contains("if (this.hasBlockGuardTarget() || this.hasPointerTarget()) {")) {
            "Ship follow-owner logic should still avoid active guard-block mode"
        }
        assertTrue(source.contains("if (this.getTarget() != null) {\n            return false;\n        }")) {
            "Ship follow-owner logic should not pull ships away while combat movement owns the navigation"
        }
    }

    @Test
    fun standingUpShouldClearLegacyGuardPositionSoFollowCanResume() {
        val source = Files.readString(SHIP_SOURCE)

        assertTrue(source.contains("if (!isSitting && this.hasBlockGuardTarget()) {\n                this.clearGuardTarget();\n            }")) {
            "Ship stand-up interaction should clear stale guard-block mode"
        }
        assertTrue(source.contains("public void clearGuardTarget()")) {
            "Guard target clearing should be routed through the public API"
        }
        assertTrue(source.contains("this.guardMovement.stop();")) {
            "Guard target clearing should stop stale guard navigation"
        }
    }
}
