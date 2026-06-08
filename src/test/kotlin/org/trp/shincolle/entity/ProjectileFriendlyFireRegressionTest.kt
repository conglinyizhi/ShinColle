package org.trp.shincolle.entity

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

import org.junit.jupiter.api.Assertions.assertTrue

class ProjectileFriendlyFireRegressionTest {
    private val MISSILE: Path =
            Path.of("src/main/java/org/trp/shincolle/entity/projectile/EntityAbyssMissile.kt")
    private val BEAM: Path =
            Path.of("src/main/java/org/trp/shincolle/entity/projectile/EntityProjectileBeam.kt")

    @Test
    fun missileFriendlyFireCheckShouldResolvePlayersShipsMountsAndAircraft() {
        val source = Files.readString(MISSILE)

        assertTrue(source.contains("Entity entity = serverLevel.getEntity(ownerUuid.get());")) {
            "Missile owner lookup should resolve the current owner entity through the server world"
        }
        assertTrue(source.contains("Entity entity = serverLevel.getEntity(targetUuid.get());")) {
            "Missile target lookup should resolve the current target entity through the server world"
        }
        assertTrue(source.contains("if (entity == null || !entity.isAlive() || entity.isRemoved()) {\n            return null;\n        }")) {
            "Missile owner/target lookup should treat dead or removed entities as invalid projectile references"
        }
        assertTrue(source.contains("UUID ownerId = resolveOwnerUuid(owner);")) {
            "Missile friendly-fire checks should normalize owner identity through a shared resolver"
        }
        assertTrue(source.contains("UUID targetId = resolveOwnerUuid(target);")) {
            "Missile friendly-fire checks should normalize target identity through a shared resolver"
        }
        assertTrue(source.contains("if (entity instanceof Player player) {")) {
            "Missile owner resolver should recognize players"
        }
        assertTrue(source.contains("if (entity instanceof EntityShipBase ship) {")) {
            "Missile owner resolver should recognize ships"
        }
        assertTrue(source.contains("if (entity instanceof TamableAnimal tamable) {")) {
            "Missile owner resolver should recognize tameables"
        }
        assertTrue(source.contains("if (entity instanceof EntityMountBase mount) {")) {
            "Missile owner resolver should recognize mounts"
        }
        assertTrue(source.contains("if (entity instanceof EntityAircraftBase aircraft) {")) {
            "Missile owner resolver should recognize aircraft"
        }
    }

    @Test
    fun beamFriendlyFireCheckShouldResolvePlayersShipsMountsAndAircraft() {
        val source = Files.readString(BEAM)

        assertTrue(source.contains("Entity entity = serverLevel.getEntity(ownerUuid.get());")) {
            "Beam owner lookup should resolve the current owner entity through the server world"
        }
        assertTrue(source.contains("if (entity == null || !entity.isAlive() || entity.isRemoved()) {\n            return null;\n        }")) {
            "Beam owner lookup should treat dead or removed entities as invalid projectile references"
        }
        assertTrue(source.contains("UUID ownerId = resolveOwnerUuid(owner);")) {
            "Beam same-owner checks should normalize owner identity through a shared resolver"
        }
        assertTrue(source.contains("return ownerId.equals(resolveOwnerUuid(target));")) {
            "Beam same-owner checks should compare normalized target ownership"
        }
        assertTrue(source.contains("if (entity instanceof Player player) {")) {
            "Beam owner resolver should recognize players"
        }
        assertTrue(source.contains("if (entity instanceof EntityShipBase ship) {")) {
            "Beam owner resolver should recognize ships"
        }
        assertTrue(source.contains("if (entity instanceof TamableAnimal tamable) {")) {
            "Beam owner resolver should recognize tameables"
        }
        assertTrue(source.contains("if (entity instanceof EntityMountBase mount) {")) {
            "Beam owner resolver should recognize mounts"
        }
        assertTrue(source.contains("if (entity instanceof EntityAircraftBase aircraft) {")) {
            "Beam owner resolver should recognize aircraft"
        }
    }
}
