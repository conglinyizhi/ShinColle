package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarriageRingFogRegressionTest {
    private static final Path CONFIG_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/Config.java");
    private static final Path CLIENT_EVENT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/event/ClientForgeEventBusEvents.java");
    private static final Path RING_SERVICE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/server/MarriageRingService.java");

    @Test
    void underwaterFogAbilityShouldNoLongerBeMarkedAsUnmigrated() throws IOException {
        String config = Files.readString(CONFIG_SOURCE);

        assertFalse(config.contains("client fog hook not migrated yet"),
                "Underwater fog config should no longer advertise the feature as unmigrated");
        assertTrue(config.contains("Legacy underwater fog reduction threshold"),
                "Underwater fog config should keep the legacy threshold semantics");
    }

    @Test
    void underwaterFogAbilityShouldBeHandledThroughClientFogEventAndService() throws IOException {
        String clientEvents = Files.readString(CLIENT_EVENT_SOURCE);
        String ringService = Files.readString(RING_SERVICE_SOURCE);

        assertTrue(clientEvents.contains("@EventBusSubscriber(modid = Shincolle.MODID, value = Dist.CLIENT)"),
                "Client fog hook should live on the client-only Forge event bus");
        assertTrue(clientEvents.contains("public static void onRenderFog(ViewportEvent.RenderFog event)"),
                "Client fog hook should use the RenderFog viewport event");
        assertTrue(clientEvents.contains("if (event.getType() != FogType.WATER) {"),
                "Underwater fog hook should only affect water fog");
        assertTrue(clientEvents.contains("float multiplier = MarriageRingService.getUnderwaterFogDistanceMultiplier(player);"),
                "Client fog hook should delegate ring fog rules to MarriageRingService");
        assertTrue(clientEvents.contains("event.scaleNearPlaneDistance(multiplier);"),
                "Underwater fog hook should expand the near fog plane");
        assertTrue(clientEvents.contains("event.scaleFarPlaneDistance(multiplier);"),
                "Underwater fog hook should expand the far fog plane");
        assertTrue(clientEvents.contains("event.setCanceled(true);"),
                "Underwater fog hook should cancel vanilla fog rendering to apply the adjusted distances");

        assertTrue(ringService.contains("public static float getUnderwaterFogDistanceMultiplier(Player player)"),
                "MarriageRingService should own underwater fog reduction rules");
        assertTrue(ringService.contains("if (Config.ringAbilityUnderwaterFogCap == 0) {"),
                "Underwater fog ability should preserve the legacy always-clear mode for threshold 0");
        assertTrue(ringService.contains("float fogFactor = (float) (Config.ringAbilityUnderwaterFogCap - marriedCount) / (float) Config.ringAbilityUnderwaterFogCap;"),
                "Underwater fog ability should scale with married ship count like the legacy branch");
    }
}
