package org.trp.shincolle.event

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class ShipMountFireImmunityRegressionTest {
    private val MOUNT_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/entity/base/EntityMountBase.kt")

    @Test
    fun mountEntitiesShouldIgnoreFireDamageAndBurningVisuals() {
        val source: String = Files.readString(MOUNT_SOURCE)

        assertTrue(source.contains("if (this.isOnFire()) {\n            this.clearFire();\n        }")) {
            "Mount entities should clear burning state during tick updates"
        }
        assertTrue(source.contains("if (source.is(DamageTypeTags.IS_FIRE)\n                || source.is(DamageTypeTags.IS_FALL)")) {
            "Mount entities should ignore fire-tagged damage together with other rejected damage types"
        }
        assertTrue(source.contains("public boolean fireImmune() {\n        return true;\n    }")) {
            "Mount entities should be marked fire-immune"
        }
        assertTrue(source.contains("public boolean displayFireAnimation() {\n        return false;\n    }")) {
            "Mount entities should not display vanilla burning visuals"
        }
    }
}
