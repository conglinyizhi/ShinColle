package org.trp.shincolle.entity

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RandomShipSpawnEggPreparationTest {
    private val SOURCE =
        Path.of("src/main/java/org/trp/shincolle/item/RandomShipSpawnEggItem.kt")

    @Test
    fun randomSpawnEggShouldGuardClientSideAndInjectMissingOwnershipData() {
        val source = Files.readString(SOURCE)

        assertTrue(source.contains("if (level == null || level.isClientSide || stack.isEmpty()) {")) {
            "Random spawn eggs should keep aborting preparation on null/client/empty input"
        }
        assertTrue(source.contains("tag!!.putString(\"id\", key.toString())")) {
            "Random spawn eggs should keep injecting the selected entity id"
        }
        assertTrue(source.contains("if (player != null && !tag.hasUUID(\"Owner\")) {")) {
            "Random spawn eggs should only inject owner UUID when one is missing"
        }
        assertTrue(source.contains("if (!tag.contains(\"Tame\")) {\n                            tag.putBoolean(\"Tame\", true)\n                        }")) {
            "Random spawn eggs should keep defaulting Tame=true when missing"
        }
    }
}
