package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModEventBusArchitectureRegressionTest {
    private static final Path EVENT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/event/ModEventBusEvents.kt");
    private static final Path MARRIAGE_RING_SERVICE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/server/MarriageRingService.kt");
    private static final Path HOSTILE_DROP_SERVICE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/server/HostileDropService.kt");

    @Test
    void marriageRingRulesShouldLiveInDedicatedService() throws IOException {
        String eventSource = Files.readString(EVENT_SOURCE);
        String ringService = Files.readString(MARRIAGE_RING_SERVICE_SOURCE);

        assertTrue(eventSource.contains("MarriageRingService.applyTickAbilities(player);"),
                "Player tick handling should delegate marriage ring abilities to the service layer");
        assertTrue(eventSource.contains("MarriageRingService.getUnderwaterBreakSpeedMultiplier(event.getEntity())"),
                "Break-speed event should delegate marriage ring dig rules to the service layer");
        assertTrue(eventSource.contains("MarriageRingService.handleFireDamageEvent(player, event.getSource())"),
                "Fire-immunity damage cancellation should delegate to the marriage ring service");
        assertFalse(eventSource.contains("applyMarriageRingAbilities("),
                "Event layer should not keep the old inlined marriage ring rules");
        assertFalse(eventSource.contains("findActiveMarriageRing("),
                "Event layer should not scan inventories for ring state");

        assertTrue(ringService.contains("public static void applyTickAbilities(Player player)"),
                "Marriage ring service should own tick-based ring ability rules");
        assertTrue(ringService.contains("public static float getUnderwaterBreakSpeedMultiplier(Player player)"),
                "Marriage ring service should own underwater mining speed rules");
        assertTrue(ringService.contains("public static boolean shouldCancelFireDamage(Player player, net.minecraft.world.damagesource.DamageSource source)"),
                "Marriage ring service should own fire-damage immunity rules");
        assertTrue(ringService.contains("source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)"),
                "Marriage ring fire immunity should still trigger specifically on fire-tagged damage");
        assertTrue(ringService.contains("public static boolean hasActiveMarriageRing(Player player)"),
                "Marriage ring service should own ring activation checks");
    }

    @Test
    void hostileDropsShouldLiveInDedicatedService() throws IOException {
        String eventSource = Files.readString(EVENT_SOURCE);
        String hostileDropService = Files.readString(HOSTILE_DROP_SERVICE_SOURCE);

        assertTrue(eventSource.contains("HostileDropService.handleLivingDrops(event);"),
                "LivingDropsEvent should delegate hostile grudge drops to the service layer");
        assertTrue(eventSource.contains("PointerInteractionService.handlePointerAttack(event.getEntity(), event.getTarget())"),
                "Pointer attack event should delegate pointer interaction to the service layer");
        assertTrue(eventSource.contains("PointerInteractionService.handleLeftClickBlock(event.getEntity(), event)"),
                "Pointer left-click block should delegate to the service layer");
        assertTrue(eventSource.contains("PointerInteractionService.handleRightClickItem(event.getEntity(), event)"),
                "Pointer right-click item should delegate to the service layer");
        assertTrue(eventSource.contains("PointerInteractionService.handleRightClickBlock(event.getEntity(), event)"),
                "Pointer right-click block should delegate to the service layer");
        assertFalse(eventSource.contains("isHostileDropTarget("),
                "Event layer should not keep hostile target classification logic");
        assertFalse(eventSource.contains("hostileDropGrudgeRate"),
                "Event layer should not keep grudge drop-rate calculations");
        assertFalse(eventSource.contains("private static ItemStack getPointerStack(Player player)"),
                "Event layer should not keep duplicated pointer hand-resolution helpers");
        assertFalse(eventSource.contains("private static boolean isPointerItem(ItemStack stack)"),
                "Event layer should not keep duplicated pointer item checks");

        assertTrue(hostileDropService.contains("public static void handleLivingDrops(LivingDropsEvent event)"),
                "Hostile drop service should own the LivingDropsEvent business logic");
        assertTrue(hostileDropService.contains("ship.addShipExp(Config.shipExpGainKill);"),
                "Hostile drop service should keep the old kill-exp side effect");
        assertTrue(hostileDropService.contains("new ItemStack(ModItems.GRUDGE.get(), fixedDrop)"),
                "Hostile drop service should own deterministic grudge drops");
    }
}
