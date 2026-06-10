package org.trp.shincolle.event

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class HostileSpawnManagerGuardTest {

    private val hostileSpawnManager =
        Path.of("src/main/java/org/trp/shincolle/event/HostileSpawnManager.kt")

    @Test
    fun `tick player should guard invalid player states before spawn logic runs`() {
        val source = Files.readString(hostileSpawnManager)

        assertThat(source).contains("fun tickPlayer(player: Player?) {")
        assertThat(source).contains("if (player == null || !player.isAlive || player.isSpectator()) {")
        assertThat(source).contains("if (player.level() !is ServerLevel) {")
        assertThat(source).contains("val level = player.level() as ServerLevel")
        assertThat(source).contains("if (level.getDifficulty() == Difficulty.PEACEFUL) {")
    }

    @Test
    fun `hostile ship spawn should abort cleanly when entity creation or placement fails`() {
        val source = Files.readString(hostileSpawnManager)

        assertThat(source).contains("private fun spawnRandomHostileShip(")
        assertThat(source).contains("val ship: EntityShipBase? = type.create(level)")
        assertThat(source).contains("if (ship == null) {")
        assertThat(source).contains("ship.initializeHostileSpawnState(scaleLevel)")
        assertThat(source).contains("if (!level.noCollision(ship, ship.boundingBox)) {")
        assertThat(source).contains("return level.addFreshEntity(ship)")
    }
}
