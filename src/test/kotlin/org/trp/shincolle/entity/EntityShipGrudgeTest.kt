package org.trp.shincolle.entity

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EntityShipGrudgeTest {
    private val SOURCE =
        Path.of("src/main/java/org/trp/shincolle/entity/EntityShipGrudge.kt")

    @Test
    fun shipGrudgeShouldKeepPickupEntryGuards() {
        val source = Files.readString(SOURCE)

        assertTrue(source.contains("if (this.level().isClientSide) {\n            return\n        }")) {
            "Ship grudge pickup should remain server-side only"
        }
        assertTrue(source.contains("if (this.pickupDelay > 0 || this.storedItem.isEmpty()) {\n            return\n        }")) {
            "Ship grudge pickup should keep guarding pickup delay and empty stored item"
        }
        assertTrue(source.contains("if (this.ownerId != null && this.ownerId != player.uuid) {\n            return\n        }")) {
            "Ship grudge pickup should keep rejecting non-owner players while ownership is set"
        }
        assertTrue(source.contains("if (player.addItem(this.storedItem)) {\n            if (this.storedItem.isEmpty()) {\n                player.take(this, count)\n                this.discard()\n            }\n        }")) {
            "Ship grudge pickup should only discard after the stored stack is fully transferred"
        }
    }

    @Test
    fun shipGrudgeShouldKeepSaveLoadFieldsAndServerDespawn() {
        val source = Files.readString(SOURCE)

        assertTrue(source.contains("if (this.age++ >= DESPAWN_TICKS) {\n                this.discard()\n                return\n            }")) {
            "Ship grudge should keep despawning after the configured server-side age limit"
        }
        assertTrue(source.contains("this.deltaMovement = Vec3.ZERO")) {
            "Ship grudge should keep clearing server-side drift every tick"
        }
        assertTrue(source.contains("tag.put(TAG_ITEM, this.storedItem.save(this.registryAccess()))")) {
            "Ship grudge save data should keep serializing the stored item"
        }
        assertTrue(source.contains("tag.putUUID(TAG_OWNER, this.ownerId)")) {
            "Ship grudge save data should keep serializing the owner UUID"
        }
        assertTrue(source.contains("tag.putInt(TAG_PICKUP_DELAY, this.pickupDelay)")) {
            "Ship grudge save data should keep serializing pickup delay"
        }
        assertTrue(source.contains("tag.putInt(TAG_AGE, this.age)")) {
            "Ship grudge save data should keep serializing age"
        }
        assertTrue(source.contains("this.storedItem = ItemStack.parse(this.registryAccess(), tag.getCompound(TAG_ITEM)).orElse(ItemStack.EMPTY)")) {
            "Ship grudge load should keep falling back to ItemStack.EMPTY for invalid stored items"
        }
        assertTrue(source.contains("this.ownerId = if (tag.hasUUID(TAG_OWNER)) tag.getUUID(TAG_OWNER) else null")) {
            "Ship grudge load should keep tolerating missing owner UUID"
        }
        assertTrue(source.contains("this.pickupDelay = tag.getInt(TAG_PICKUP_DELAY)")) {
            "Ship grudge load should keep restoring pickup delay"
        }
        assertTrue(source.contains("this.age = tag.getInt(TAG_AGE)")) {
            "Ship grudge load should keep restoring age"
        }
    }
}
