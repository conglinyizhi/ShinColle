package org.trp.shincolle.client

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PlayerSkillHudRegressionTest {
    private val CLIENT_EVENTS =
        Path.of("src/main/java/org/trp/shincolle/event/ClientForgeEventBusEvents.kt")
    private val HUD_RENDERER =
        Path.of("src/main/java/org/trp/shincolle/client/renderer/PlayerSkillHudRenderer.kt")
    private val SPRITES =
        Path.of("src/main/java/org/trp/shincolle/client/gui/component/Sprites.kt")

    @Test
    fun riderSkillHudShouldRenderFromGuiPostEvent() {
        val eventSource = Files.readString(CLIENT_EVENTS)

        assertTrue(eventSource.contains("fun onRenderGui(event: RenderGuiEvent.Post)")) {
            "Client forge events should hook RenderGuiEvent.Post for rider skill HUD rendering"
        }
        assertTrue(eventSource.contains("PlayerSkillHudRenderer.render(event.guiGraphics)")) {
            "Client forge events should delegate rider skill HUD rendering to PlayerSkillHudRenderer"
        }
    }

    @Test
    fun riderSkillHudShouldUseLegacyHudTextureAndFourSkillSlots() {
        val rendererSource = Files.readString(HUD_RENDERER)
        val spriteSource = Files.readString(SPRITES)

        assertTrue(spriteSource.contains("val T_HUD: ResourceLocation = gui(\"guihud.png\")")) {
            "Sprites should expose the legacy rider skill HUD texture"
        }
        assertTrue(rendererSource.contains("ship.isStateGuiBtn1") && rendererSource.contains("ship.isStateLightAttack")) {
            "Slot 1 should reflect light attack availability"
        }
        assertTrue(rendererSource.contains("ship.isStateGuiBtn2") && rendererSource.contains("ship.isStateHeavyAttack")) {
            "Slot 2 should reflect heavy attack availability"
        }
        assertTrue(rendererSource.contains("ship.isStateGuiBtn3") && rendererSource.contains("ship.hasAirLight()")) {
            "Slot 3 should reflect light aircraft availability"
        }
        assertTrue(rendererSource.contains("ship.isStateGuiBtn4") && rendererSource.contains("ship.hasAirHeavy()")) {
            "Slot 4 should reflect heavy aircraft availability"
        }
        assertTrue(rendererSource.contains("if (vehicle is EntityMountBase)")) {
            "HUD should render for mount mode"
        }
        assertTrue(rendererSource.contains("if (passenger is EntityShipBase && passenger.isAlive)")) {
            "HUD should render for rider mode"
        }
    }
}
