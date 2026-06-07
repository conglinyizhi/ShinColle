package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AmmoStackClampRegressionTest {
    private static final Path SHIP_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt");
    private static final Path COMBAT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBaseCombat.kt");

    @Test
    void ammoRemaindersShouldSplitIntoLegalStacks() throws IOException {
        String source = Files.readString(COMBAT_SOURCE);

        assertTrue(source.contains("int maxStackSize = item.getDefaultInstance().getMaxStackSize();"),
                "Ammo remainder reinsertion should respect the item's max stack size");
        assertTrue(source.contains("ItemStack stack = new ItemStack(item, Math.min(remaining, maxStackSize));"),
                "Ammo remainder reinsertion should split oversized remainder stacks");
        assertTrue(source.contains("serverLevel.addFreshEntity(new ItemEntity(serverLevel, this.ship.getX(), this.ship.getY(), this.ship.getZ(), leftover));"),
                "Ammo remainder drops should only spawn already-clamped stacks");
    }

    @Test
    void shipDropsShouldSplitOversizedItemCounts() throws IOException {
        String source = Files.readString(SHIP_SOURCE);

        assertTrue(source.contains("int maxStackSize = item.getDefaultInstance().getMaxStackSize();"),
                "Ship drops should respect item max stack size");
        assertTrue(source.contains("int stackCount = Math.min(remaining, maxStackSize);"),
                "Ship drops should split oversized counts into multiple stacks");
    }
}
