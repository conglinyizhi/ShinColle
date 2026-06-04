package org.trp.shincolle.item

import net.minecraft.nbt.CompoundTag
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class OwnerPaperItemTest {

    @Test
    fun ownerPaperShouldAlternateStringBasedSignatures() {
        val tag = CompoundTag()
        val firstUuid = UUID.randomUUID()
        val secondUuid = UUID.randomUUID()
        val thirdUuid = UUID.randomUUID()

        OwnerPaperItem.writeOwnerSignature(tag, "First", firstUuid)
        assertEquals("First", tag.getString("SignNameA"))
        assertEquals("", tag.getString("SignNameB"))
        assertEquals(firstUuid.toString(), tag.getString("SignIDA"))
        assertEquals("", tag.getString("SignIDB"))
        assertFalse(tag.getBoolean("signPos"))

        OwnerPaperItem.writeOwnerSignature(tag, "Second", secondUuid)
        assertEquals("First", tag.getString("SignNameA"))
        assertEquals("Second", tag.getString("SignNameB"))
        assertEquals(firstUuid.toString(), tag.getString("SignIDA"))
        assertEquals(secondUuid.toString(), tag.getString("SignIDB"))
        assertTrue(tag.getBoolean("signPos"))

        OwnerPaperItem.writeOwnerSignature(tag, "Third", thirdUuid)
        assertEquals("Third", tag.getString("SignNameA"))
        assertEquals("Second", tag.getString("SignNameB"))
        assertEquals(thirdUuid.toString(), tag.getString("SignIDA"))
        assertEquals(secondUuid.toString(), tag.getString("SignIDB"))
        assertFalse(tag.getBoolean("signPos"))
    }
}
