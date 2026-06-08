package org.trp.shincolle.server

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse

class PlayerSkillCooldownRegressionTest {
    private val SKILL_SERVICE: Path =
            Path.of("src/main/java/org/trp/shincolle/server/PlayerSkillService.kt")

    @Test
    fun cooldownTickShouldDecrementAllPositiveSlots() {
        val source = Files.readString(SKILL_SERVICE)

        assertTrue(source.contains("""
        for (i in cds.indices) {
                if (cds[i] > 0) cds[i]--
            }
""")) { "tickCooldowns should decrement every positive slot in the cooldown array" }
    }

    @Test
    fun cooldownEntryShouldBeRemovedWhenAllSlotsReachZero() {
        val source = Files.readString(SKILL_SERVICE)

        assertTrue(source.contains("""
            if (cds.all { it <= 0 }) {
                iterator.remove()
            }
""")) { "Empty cooldown entries should be removed to prevent memory leak" }
    }

    @Test
    fun cooldownMapShouldUseShipUuidAsKey() {
        val source = Files.readString(SKILL_SERVICE)

        assertTrue(source.contains("skillCooldowns.getOrPut(ship.uuid)")) {
            "Cooldown lookup should use the ship's UUID as the map key"
        }
    }

    @Test
    fun cooldownArrayShouldHaveFiveSlots() {
        val source = Files.readString(SKILL_SERVICE)

        assertTrue(source.contains("IntArray(5)")) {
            "Cooldown array should be initialized with 5 slots (0=light, 1=heavy, 2=lightAir, 3=heavyAir, 4=reserved)"
        }
    }

    @Test
    fun skillShouldBeBlockedWhenCooldownIsPositive() {
        val source = Files.readString(SKILL_SERVICE)

        assertTrue(source.contains("if (cdArray[0] > 0) return")) {
            "Light attack skill should be blocked when cooldown slot 0 is positive"
        }
        assertTrue(source.contains("if (cdArray[1] > 0) return")) {
            "Heavy attack skill should be blocked when cooldown slot 1 is positive"
        }
        assertTrue(source.contains("if (cdArray[2] > 0 || cdArray[3] > 0) return")) {
            "Aircraft attacks should share cooldown slots 2 and 3"
        }
    }

    @Test
    fun attackDelayShouldScaleWithShipLevel() {
        val source = Files.readString(SKILL_SERVICE)

        assertTrue(source.contains("max(10, 60 - ship.level * 2)")) {
            "Attack delay should decrease as ship level increases, with a floor of 10 ticks"
        }
    }

    @Test
    fun lightAndHeavyAircraftShouldShareCooldownSlots() {
        val source = Files.readString(SKILL_SERVICE)

        // Both aircraft attacks read cdArray[2] and cdArray[3]
        assertTrue(source.contains("cdArray[2] = getAttackDelay(ship) * 3") &&
                   source.contains("cdArray[3] = cdArray[2]")) {
            "Light aircraft should set shared cooldown slots 2 and 3 to the same value"
        }
        assertTrue(source.contains("cdArray[2] = getAttackDelay(ship) * 4") &&
                   source.contains("cdArray[3] = cdArray[2]")) {
            "Heavy aircraft should set shared cooldown slots 2 and 3 to the same value"
        }
    }

    @Test
    fun heavyAttackCooldownShouldBeDoubleLightAttack() {
        val source = Files.readString(SKILL_SERVICE)

        assertTrue(source.contains("cdArray[1] = getAttackDelay(ship) * 2")) {
            "Heavy attack cooldown should be twice the base attack delay"
        }
    }

    @Test
    fun skillHostShipShouldSupportMountAndRiderModes() {
        val source = Files.readString(SKILL_SERVICE)

        assertTrue(source.contains("if (vehicle is EntityMountBase)")) {
            "getSkillHostShip should check mount mode (player riding mount)"
        }
        assertTrue(source.contains("for (passenger in player.passengers)")) {
            "getSkillHostShip should check rider mode (ship riding on player)"
        }
        assertTrue(source.contains("if (passenger is EntityShipBase && passenger.isAlive)")) {
            "Rider mode should only consider alive EntityShipBase passengers"
        }
    }

    @Test
    fun skillShouldRequireOwnership() {
        val source = Files.readString(SKILL_SERVICE)

        assertTrue(source.contains("if (!ship.isOwnedBy(player)) return")) {
            "handlePlayerSkill should reject ships not owned by the player"
        }
    }

    @Test
    fun skillShouldCheckAmmoBeforeCasting() {
        val source = Files.readString(SKILL_SERVICE)

        assertTrue(source.contains("if (ship.ammoLight <= 0) return")) {
            "Light attack skill should check ammo before casting"
        }
        assertTrue(source.contains("if (ship.ammoHeavy <= 0) return")) {
            "Heavy attack skill should check ammo before casting"
        }
        assertTrue(source.contains("if (!ship.hasAirLight() || ship.ammoLight < 5) return")) {
            "Light aircraft skill should check aircraft count and ammo"
        }
        assertTrue(source.contains("if (!ship.hasAirHeavy() || ship.ammoHeavy < 5) return")) {
            "Heavy aircraft skill should check aircraft count and ammo"
        }
    }

    @Test
    fun skillShouldCheckAttackStateFlagsBeforeCasting() {
        val source = Files.readString(SKILL_SERVICE)

        assertTrue(source.contains("if (!ship.isStateGuiBtn1 || !ship.isStateLightAttack) return")) {
            "Light attack should verify both GUI button 1 and light attack state flag"
        }
        assertTrue(source.contains("if (!ship.isStateGuiBtn2 || !ship.isStateHeavyAttack) return")) {
            "Heavy attack should verify both GUI button 2 and heavy attack state flag"
        }
        assertTrue(source.contains("if (!ship.isStateGuiBtn3 || !ship.isStateLightAircraftAttack) return")) {
            "Light aircraft should verify both GUI button 3 and light aircraft state flag"
        }
        assertTrue(source.contains("if (!ship.isStateGuiBtn4 || !ship.isStateHeavyAircraftAttack) return")) {
            "Heavy aircraft should verify both GUI button 4 and heavy aircraft state flag"
        }
    }
}
