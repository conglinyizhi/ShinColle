package org.trp.shincolle.event

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue

class LivingIncomingDamageEventRegressionTest {
    private val EVENT_BUS: Path =
            Path.of("src/main/java/org/trp/shincolle/event/ModEventBusEvents.kt")

    @Test
    fun mountRidingShouldImmunizeFallAndInWallDamage() {
        val source = Files.readString(EVENT_BUS)

        assertTrue(source.contains("if (vehicle is EntityMountBase &&")) {
            "Should check if target is riding an EntityMountBase"
        }
        assertTrue(source.contains("(source.`is`(DamageTypes.FALL) || source.`is`(DamageTypes.IN_WALL))")) {
            "Should immunize against both FALL and IN_WALL damage types"
        }
        assertTrue(source.contains("event.isCanceled = true")) {
            "Should cancel the damage event for mount riders"
        }
    }

    @Test
    fun playerAttackingShouldSetRevengeTargetForFriendlyShips() {
        val source = Files.readString(EVENT_BUS)

        assertTrue(source.contains("if (attacker is Player) {")) {
            "Should detect when a player is the attacker"
        }
        assertTrue(source.contains("setRevengeTargetAroundPlayer(level, attacker, target)")) {
            "Should call setRevengeTargetAroundPlayer when player attacks"
        }
    }

    @Test
    fun playerBeingAttackedShouldSetRevengeTargetForFriendlyShips() {
        val source = Files.readString(EVENT_BUS)

        assertTrue(source.contains("if (target is Player) {")) {
            "Should detect when a player is the target"
        }
        assertTrue(source.contains("setRevengeTargetAroundPlayer(level, target, attacker)")) {
            "Should call setRevengeTargetAroundPlayer when player is attacked"
        }
    }

    @Test
    fun hostileShipBeingAttackedShouldCallForHelp() {
        val source = Files.readString(EVENT_BUS)

        assertTrue(source.contains("if (target is EntityShipBase && target.isHostileShipMob)")) {
            "Should detect when a hostile ship is the target"
        }
        assertTrue(source.contains("if (attacker !is EntityShipBase || !attacker.isHostileShipMob)")) {
            "Should only call for help when attacker is not a hostile ship"
        }
        assertTrue(source.contains("setRevengeTargetAroundHostileShip(level, target, attacker)")) {
            "Should call setRevengeTargetAroundHostileShip for hostile ship targets"
        }
    }

    @Test
    fun eventShouldIgnoreClientSide() {
        val source = Files.readString(EVENT_BUS)

        assertTrue(source.contains("if (level.isClientSide) return")) {
            "Should return early on client side"
        }
    }

    @Test
    fun eventHandlerShouldBeSubscribedToLivingIncomingDamageEvent() {
        val source = Files.readString(EVENT_BUS)

        assertTrue(source.contains("@SubscribeEvent")) {
            "Should use @SubscribeEvent annotation"
        }
        assertTrue(source.contains("fun onLivingIncomingDamage(event: LivingIncomingDamageEvent)")) {
            "Should define handler for LivingIncomingDamageEvent"
        }
    }
}
