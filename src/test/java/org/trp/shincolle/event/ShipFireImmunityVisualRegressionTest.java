package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipFireImmunityVisualRegressionTest {
    private static final Path SHIP_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt");

    @Test
    void shipShouldIgnoreFireDamageAndFireVisualsFromLava() throws IOException {
        String source = Files.readString(SHIP_SOURCE);

        assertTrue(source.contains("source.is(DamageTypeTags.IS_FIRE)"),
                "Ship hurt logic should continue ignoring fire-tagged damage sources");
        assertTrue(source.contains("source.is(DamageTypes.IN_WALL)"),
                "Ship hurt logic should ignore suffocation damage when ships clip into blocks");
        assertTrue(source.contains("public boolean displayFireAnimation() {\n        return (this.getHealth() / this.getMaxHealth()) <= 0.25F;\n    }"),
                "Ship fire visuals should now come only from the low-health effect, not vanilla burning state");
        assertFalse(source.contains("if (super.displayFireAnimation()) {"),
                "Ship fire visuals should not reuse vanilla burning animation while lava-immune");
    }
}
