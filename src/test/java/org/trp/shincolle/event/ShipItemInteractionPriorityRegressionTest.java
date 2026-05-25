package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipItemInteractionPriorityRegressionTest {
    private static final Path SHIP_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.java");

    @Test
    void shipMobInteractShouldLetItemEntityInteractionsWinBeforeDefaultShipInteraction() throws IOException {
        String source = Files.readString(SHIP_SOURCE);

        assertTrue(source.contains("ItemStack stack = player.getItemInHand(hand);\n" +
                        "        InteractionResult itemInteractionResult = stack.getItem().interactLivingEntity(stack, player, this, hand);\n" +
                        "        if (itemInteractionResult != InteractionResult.PASS) {\n" +
                        "            return itemInteractionResult;\n" +
                        "        }\n\n" +
                        "        if (!this.level().isClientSide && hand == InteractionHand.MAIN_HAND) {"),
                "Ship interactions should give held items a chance to handle entity right-clicks before default ship interaction");
    }
}
