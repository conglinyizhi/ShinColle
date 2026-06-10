package org.trp.shincolle.item

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BossSpawnEggItemTest {
    private val SOURCE =
        Path.of("src/main/java/org/trp/shincolle/item/BossSpawnEggItem.kt")

    @Test
    fun bossSpawnEggShouldKeepHostileSpawnPreparationLogic() {
        val source = Files.readString(SOURCE)

        assertTrue(source.contains("tag.remove(\"Owner\")")) {
            "Boss spawn eggs should keep removing Owner so they spawn untamed"
        }
        assertTrue(source.contains("tag.remove(\"Tame\")")) {
            "Boss spawn eggs should keep removing Tame so they stay hostile"
        }
        assertTrue(source.contains("initializeHostileSpawnState(bossScale)")) {
            "Boss spawn eggs should keep applying hostile spawn state initialization"
        }
        assertTrue(source.contains("ship.ammoLight += 128")) {
            "Boss spawn eggs should keep boosting light ammo after spawn"
        }
        assertTrue(source.contains("ship.ammoHeavy += 64")) {
            "Boss spawn eggs should keep boosting heavy ammo after spawn"
        }
        assertTrue(source.contains("ship.fuel = max(ship.fuel, 5000)")) {
            "Boss spawn eggs should keep enforcing the boss fuel floor"
        }
    }
}
