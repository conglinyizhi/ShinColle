package org.trp.shincolle.item

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BossSpawnEggGuardRegressionTest {
    private val SOURCE =
        Path.of("src/main/java/org/trp/shincolle/item/BossSpawnEggItem.kt")

    @Test
    fun bossSpawnEggShouldGuardMissingEntityTypeKeysAndDelayedSpawnLookup() {
        val source = Files.readString(SOURCE)

        assertTrue(source.contains("val key = BuiltInRegistries.ENTITY_TYPE.getKey(this.typeSupplier.get())")) {
            "Boss spawn eggs should resolve the registry key from the supplied entity type before mutating entity data"
        }
        assertTrue(source.contains("if (key == null) return super.useOn(context)")) {
            "Boss spawn egg block placement should keep falling back cleanly when the entity type key is missing"
        }
        assertTrue(source.contains("if (key == null) return super.use(level, player, hand)")) {
            "Boss spawn egg right-click spawning should keep falling back cleanly when the entity type key is missing"
        }
        assertTrue(source.contains("if (result.consumesAction() && level is ServerLevel) {")) {
            "Boss spawn egg block placement should only scan nearby entities after a real spawn action succeeds"
        }
        assertTrue(source.contains("if (result.result.consumesAction() && level is ServerLevel) {")) {
            "Boss spawn egg right-click spawning should only scan nearby entities after a real spawn action succeeds"
        }
        assertTrue(source.contains("Predicate { e: Entity? -> e!!.type === spawnedType && e is EntityShipBase }")) {
            "Boss spawn eggs should keep narrowing hostile post-processing to the expected spawned ship type"
        }
    }
}
