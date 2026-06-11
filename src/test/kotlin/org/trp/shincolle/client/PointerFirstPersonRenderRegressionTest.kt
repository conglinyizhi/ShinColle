package org.trp.shincolle.client

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PointerFirstPersonRenderRegressionTest {
    private val CLIENT_EVENTS =
        Path.of("src/main/java/org/trp/shincolle/event/ClientForgeEventBusEvents.kt")

    @Test
    fun pointerCaressModeShouldReplaceCanceledHandRenderWithMainArmRender() {
        val source = Files.readString(CLIENT_EVENTS)

        assertTrue(source.contains("event.isCanceled = true")) {
            "Pointer caress mode should still cancel the default first-person item render"
        }
        assertTrue(source.contains("renderPointerCaressMainArm(")) {
            "Pointer caress mode should delegate to a dedicated main-arm renderer"
        }
        assertTrue(source.contains("resolveItemInHandRenderer(mc)")) {
            "Pointer first-person rendering should resolve the vanilla ItemInHandRenderer"
        }
        assertTrue(source.contains("renderPlayerArm")) {
            "Pointer first-person rendering should call the vanilla arm renderer"
        }
        assertFalse(source.contains("TODO: custom first-person arm rendering")) {
            "Pointer first-person rendering should no longer be left as a TODO"
        }
    }
}
