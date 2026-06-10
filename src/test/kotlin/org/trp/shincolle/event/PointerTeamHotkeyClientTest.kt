package org.trp.shincolle.event

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PointerTeamHotkeyClientTest {
    private val CLIENT_EVENTS =
        Path.of("src/main/java/org/trp/shincolle/event/ClientForgeEventBusEvents.kt")

    @Test
    fun pointerCtrlHotkeyShouldSendFormationTeamSwitchPayloadWithoutSwitchingHeldSlot() {
        val source = Files.readString(CLIENT_EVENTS)

        assertTrue(source.contains("val originalSlot = player.inventory.selected")) {
            "Pointer team hotkeys should capture the held slot before consuming hotbar key presses"
        }
        assertTrue(source.contains("C2SFormationActionPayload(0, i, 0, null, Optional.empty())")) {
            "Pointer team hotkeys should send the existing formation team-switch payload"
        }
        assertTrue(source.contains("player.inventory.selected = originalSlot")) {
            "Pointer team hotkeys should restore the held hotbar slot after switching teams"
        }
    }
}
