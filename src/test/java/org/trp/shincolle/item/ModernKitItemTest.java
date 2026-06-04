package org.trp.shincolle.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.junit.jupiter.api.Test;
import org.trp.shincolle.Config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModernKitItemTest {

    @Test
    void maxedFeedbackShouldUseLocalizedMessageAndActionBarConfig() {
        boolean originalActionBar = Config.modernKitNotifyWhenMaxedActionBar;
        try {
            Config.modernKitNotifyWhenMaxedActionBar = true;
            ModernKitItem.MaxedFeedback actionBarFeedback = ModernKitItem.maxedFeedback();
            assertEquals("chat.shincolle.modernkit.maxed", translationKey(actionBarFeedback.message()));
            assertTrue(actionBarFeedback.actionBar());

            Config.modernKitNotifyWhenMaxedActionBar = false;
            ModernKitItem.MaxedFeedback chatFeedback = ModernKitItem.maxedFeedback();
            assertEquals("chat.shincolle.modernkit.maxed", translationKey(chatFeedback.message()));
            assertFalse(chatFeedback.actionBar());
        } finally {
            Config.modernKitNotifyWhenMaxedActionBar = originalActionBar;
        }
    }

    private static String translationKey(Component component) {
        return ((TranslatableContents) component.getContents()).getKey();
    }
}
