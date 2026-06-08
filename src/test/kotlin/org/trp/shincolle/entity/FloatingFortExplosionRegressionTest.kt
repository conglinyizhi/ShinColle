package org.trp.shincolle.entity

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse

class FloatingFortExplosionRegressionTest {
    private val FLOATING_FORT: Path =
            Path.of("src/main/java/org/trp/shincolle/entity/EntityFloatingFort.kt")

    @Test
    fun explosionShouldTriggerWithinTwoBlocksOfTarget() {
        val source = Files.readString(FLOATING_FORT)

        assertTrue(source.contains("if (distSqr < 4.0)")) {
            "Explosion should trigger when distance squared to target is less than 4.0 (within 2 blocks)"
        }
    }

    @Test
    fun explosionShouldTriggerOnMaxLifetime() {
        val source = Files.readString(FLOATING_FORT)

        assertTrue(source.contains("if (this.fuseTicks >= this.maxLifeTicks)")) {
            "Explosion should trigger when fuse ticks reach max lifetime"
        }
    }

    @Test
    fun explosionShouldTriggerWhenTargetDiesOrRemoved() {
        val source = Files.readString(FLOATING_FORT)

        assertTrue(source.contains("if (target != null && (!target.isAlive || target.isRemoved))")) {
            "Explosion should trigger when target is dead or removed"
        }
    }

    @Test
    fun explosionShouldUseDistanceBasedDamageFalloff() {
        val source = Files.readString(FLOATING_FORT)

        assertTrue(source.contains("val factor = 1.0f - (dist / explosionRange.toFloat()).coerceIn(0.0f, 1.0f)")) {
            "Explosion damage should use linear falloff based on distance from center"
        }
        assertTrue(source.contains("val damage = 15.0f * factor")) {
            "Base explosion damage should be 15.0f scaled by distance factor"
        }
    }

    @Test
    fun explosionShouldExcludeCarrierAndFriendlyEntities() {
        val source = Files.readString(FLOATING_FORT)

        assertTrue(source.contains("if (entity === this) continue")) {
            "Explosion should not damage itself"
        }
        assertTrue(source.contains("if (entity === carrier) continue")) {
            "Explosion should not damage its carrier"
        }
        assertTrue(source.contains("if (carrier != null && carrier.isOwnedBy(entity)) continue")) {
            "Explosion should not damage entities owned by the carrier"
        }
        assertTrue(source.contains("if (entity is EntityShipBase && entity.ownerUUID == carrierOwner) continue")) {
            "Explosion should not damage friendly ships with same owner"
        }
        assertTrue(source.contains("if (entity is TamableAnimal && entity.ownerUUID == carrierOwner) continue")) {
            "Explosion should not damage friendly tameables with same owner"
        }
    }

    @Test
    fun explosionRangeShouldBeFourPointFiveBlocks() {
        val source = Files.readString(FLOATING_FORT)

        assertTrue(source.contains("val explosionRange = 4.5")) {
            "Explosion range should be 4.5 blocks"
        }
    }

    @Test
    fun explosionDamageSourceShouldUseCarrierWhenAvailable() {
        val source = Files.readString(FLOATING_FORT)

        assertTrue(source.contains("val source = if (carrier != null) {")) {
            "Explosion damage source should use carrier when available"
        }
        assertTrue(source.contains("this.damageSources().mobAttack(carrier)")) {
            "Damage source should be mob attack from carrier"
        }
        assertTrue(source.contains("this.damageSources().explosion(this, this)")) {
            "Damage source should fallback to self-explosion when no carrier"
        }
    }

    @Test
    fun floatingFortShouldBeImmuneToNonKillDamage() {
        val source = Files.readString(FLOATING_FORT)

        assertTrue(source.contains("if (source.`is`(net.minecraft.world.damagesource.DamageTypes.GENERIC_KILL))")) {
            "FloatingFort should only take damage from GENERIC_KILL"
        }
        assertTrue(source.contains("return false")) {
            "FloatingFort should ignore all other damage sources"
        }
    }

    @Test
    fun floatingFortShouldNotBePickableOrPushable() {
        val source = Files.readString(FLOATING_FORT)

        assertTrue(source.contains("override fun isPickable(): Boolean {\n        return false\n    }")) {
            "FloatingFort should not be pickable (no crosshair interaction)"
        }
        assertTrue(source.contains("override fun isPushable(): Boolean {\n        return false\n    }")) {
            "FloatingFort should not be pushable"
        }
    }

    @Test
    fun maxLifeTicksShouldBeFiveHundred() {
        val source = Files.readString(FLOATING_FORT)

        assertTrue(source.contains("private var maxLifeTicks = 500")) {
            "Max lifetime should be 500 ticks (25 seconds)"
        }
    }
}
