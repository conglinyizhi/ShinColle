package org.trp.shincolle.event

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class OpToolHotkeyClientTest {
    private val CLIENT_EVENTS =
        Path.of("src/main/java/org/trp/shincolle/event/ClientForgeEventBusEvents.kt")

    @Test
    fun opToolHotkeysShouldSendServerPayloadsForToggleAndListActions() {
        val source = Files.readString(CLIENT_EVENTS)

        assertTrue(source.contains("C2SOpToolActionPayload.ACTION_TOGGLE_UNATTACKABLE_TARGET")) {
            "OP Tool toggle hotkey should use the dedicated server payload action"
        }
        assertTrue(source.contains("C2SOpToolActionPayload.ACTION_SHOW_UNATTACKABLE_TARGETS")) {
            "OP Tool list hotkey should use the dedicated server payload action"
        }
        assertTrue(source.contains("is net.minecraft.world.phys.EntityHitResult -> Optional.of(hitResult.entity.uuid)")) {
            "OP Tool toggle hotkey should only send the target toggle action when the crosshair hits an entity"
        }
    }
}
