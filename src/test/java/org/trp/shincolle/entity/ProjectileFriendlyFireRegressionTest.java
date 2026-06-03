package org.trp.shincolle.entity;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectileFriendlyFireRegressionTest {
    private static final Path MISSILE =
            Path.of("src/main/java/org/trp/shincolle/entity/projectile/EntityAbyssMissile.java");
    private static final Path BEAM =
            Path.of("src/main/java/org/trp/shincolle/entity/projectile/EntityProjectileBeam.java");

    @Test
    void missileFriendlyFireCheckShouldResolvePlayersShipsMountsAndAircraft() throws IOException {
        String source = Files.readString(MISSILE);

        assertTrue(source.contains("UUID ownerId = resolveOwnerUuid(owner);"),
                "Missile friendly-fire checks should normalize owner identity through a shared resolver");
        assertTrue(source.contains("UUID targetId = resolveOwnerUuid(target);"),
                "Missile friendly-fire checks should normalize target identity through a shared resolver");
        assertTrue(source.contains("if (entity instanceof Player player) {"),
                "Missile owner resolver should recognize players");
        assertTrue(source.contains("if (entity instanceof EntityShipBase ship) {"),
                "Missile owner resolver should recognize ships");
        assertTrue(source.contains("if (entity instanceof TamableAnimal tamable) {"),
                "Missile owner resolver should recognize tameables");
        assertTrue(source.contains("if (entity instanceof EntityMountBase mount) {"),
                "Missile owner resolver should recognize mounts");
        assertTrue(source.contains("if (entity instanceof EntityAircraftBase aircraft) {"),
                "Missile owner resolver should recognize aircraft");
    }

    @Test
    void beamFriendlyFireCheckShouldResolvePlayersShipsMountsAndAircraft() throws IOException {
        String source = Files.readString(BEAM);

        assertTrue(source.contains("UUID ownerId = resolveOwnerUuid(owner);"),
                "Beam same-owner checks should normalize owner identity through a shared resolver");
        assertTrue(source.contains("return ownerId.equals(resolveOwnerUuid(target));"),
                "Beam same-owner checks should compare normalized target ownership");
        assertTrue(source.contains("if (entity instanceof Player player) {"),
                "Beam owner resolver should recognize players");
        assertTrue(source.contains("if (entity instanceof EntityShipBase ship) {"),
                "Beam owner resolver should recognize ships");
        assertTrue(source.contains("if (entity instanceof TamableAnimal tamable) {"),
                "Beam owner resolver should recognize tameables");
        assertTrue(source.contains("if (entity instanceof EntityMountBase mount) {"),
                "Beam owner resolver should recognize mounts");
        assertTrue(source.contains("if (entity instanceof EntityAircraftBase aircraft) {"),
                "Beam owner resolver should recognize aircraft");
    }
}
