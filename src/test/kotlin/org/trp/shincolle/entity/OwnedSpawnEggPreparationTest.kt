package org.trp.shincolle.entity

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class OwnedSpawnEggPreparationTest {
    private val SOURCE =
        Path.of("src/main/java/org/trp/shincolle/item/OwnedSpawnEggItem.kt")

    @Test
    fun ownedSpawnEggShouldPrepareOwnerTameAndEntityIdBeforeSpawn() {
        val source = Files.readString(SOURCE)

        assertTrue(source.contains("ensureOwnedEntityData(stack, player, \"useOn\")")) {
            "Owned spawn eggs should prepare entity data before useOn spawning"
        }
        assertTrue(source.contains("ensureOwnedEntityData(stack, player, \"use\")")) {
            "Owned spawn eggs should prepare entity data before right-click spawning"
        }
        assertTrue(source.contains("if (!tag!!.hasUUID(\"Owner\"))")) {
            "Owned spawn eggs should preserve or inject an Owner UUID before spawning"
        }
        assertTrue(source.contains("tag.putBoolean(\"Tame\", true)")) {
            "Owned spawn eggs should force Tame=true when preparing spawn data"
        }
        assertTrue(source.contains("tag.putString(\n                                \"id\",\n                                BuiltInRegistries.ENTITY_TYPE.getKey(this.typeSupplier.get()).toString()")) {
            "Owned spawn eggs should inject the entity id when missing"
        }
    }
}
