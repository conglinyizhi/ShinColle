package org.trp.shincolle.item

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.trp.shincolle.Config

class ModernKitItemTest {

    @Test
    fun maxedFeedbackShouldUseLocalizedMessageAndActionBarConfig() {
        val originalActionBar = Config.modernKitNotifyWhenMaxedActionBar
        try {
            Config.modernKitNotifyWhenMaxedActionBar = true
            val actionBarFeedback = ModernKitItem.maxedFeedback()
            assertEquals("chat.shincolle.modernkit.maxed", translationKey(actionBarFeedback.message!!))
            assertTrue(actionBarFeedback.actionBar)

            Config.modernKitNotifyWhenMaxedActionBar = false
            val chatFeedback = ModernKitItem.maxedFeedback()
            assertEquals("chat.shincolle.modernkit.maxed", translationKey(chatFeedback.message!!))
            assertFalse(chatFeedback.actionBar)
        } finally {
            Config.modernKitNotifyWhenMaxedActionBar = originalActionBar
        }
    }

    private fun translationKey(component: Component): String {
        return (component.contents as TranslatableContents).key
    }
}
