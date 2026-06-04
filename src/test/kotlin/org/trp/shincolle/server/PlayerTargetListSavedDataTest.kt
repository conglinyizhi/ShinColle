package org.trp.shincolle.server

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.UUID

class PlayerTargetListSavedDataTest {

    @Test
    fun `toggle should reject invalid owners and class names`() {
        val data = PlayerTargetListSavedData()
        val owner = UUID.randomUUID()

        assertThat(data.toggle(null, "mob.Zombie")).isFalse()
        assertThat(data.toggle(owner, null)).isFalse()
        assertThat(data.toggle(owner, "")).isFalse()
        assertThat(data.toggle(owner, "   ")).isFalse()
        assertThat(data.entries(owner)).isEmpty()
    }

    @Test
    fun `toggle should add remove and sort target classes per owner`() {
        val data = PlayerTargetListSavedData()
        val owner = UUID.randomUUID()

        assertThat(data.toggle(owner, "mob.Skeleton")).isTrue()
        assertThat(data.toggle(owner, "mob.Zombie")).isTrue()
        assertThat(data.contains(owner, "mob.Skeleton")).isTrue()
        assertThat(data.contains(owner, "mob.Zombie")).isTrue()
        assertThat(data.entries(owner)).containsExactly("mob.Skeleton", "mob.Zombie")

        assertThat(data.toggle(owner, "mob.Skeleton")).isFalse()
        assertThat(data.contains(owner, "mob.Skeleton")).isFalse()
        assertThat(data.entries(owner)).containsExactly("mob.Zombie")

        assertThat(data.toggle(owner, "mob.Zombie")).isFalse()
        assertThat(data.entries(owner)).isEmpty()
    }

    @Test
    fun `saved data should persist non blank class names by owner`() {
        val firstOwner = UUID.randomUUID()
        val secondOwner = UUID.randomUUID()
        val data = PlayerTargetListSavedData()

        data.toggle(firstOwner, "mob.Zombie")
        data.toggle(firstOwner, "mob.Skeleton")
        data.toggle(secondOwner, "mob.Creeper")

        val restored = loadFromSavedTag(data.save(CompoundTag(), null))

        assertThat(restored.entries(firstOwner)).containsExactly("mob.Skeleton", "mob.Zombie")
        assertThat(restored.entries(secondOwner)).containsExactly("mob.Creeper")
        assertThat(restored.contains(firstOwner, "mob.Zombie")).isTrue()
        assertThat(restored.contains(secondOwner, "mob.Zombie")).isFalse()
    }

    @Test
    fun `saved data should ignore missing owners and blank class names`() {
        val owner = UUID.randomUUID()
        val tag = CompoundTag()
        val entries = ListTag()

        entries.add(CompoundTag().apply {
            put("ClassNames", ListTag().apply {
                add(StringTag.valueOf("mob.Zombie"))
            })
        })

        entries.add(CompoundTag().apply {
            putUUID("Owner", owner)
            put("ClassNames", ListTag().apply {
                add(StringTag.valueOf(""))
                add(StringTag.valueOf("   "))
                add(StringTag.valueOf("mob.Skeleton"))
            })
        })

        tag.put("Entries", entries)

        val restored = loadFromSavedTag(tag)

        assertThat(restored.entries(owner)).containsExactly("mob.Skeleton")
    }

    @Test
    fun `entry views should stay read only`() {
        val owner = UUID.randomUUID()
        val data = PlayerTargetListSavedData()
        data.toggle(owner, "mob.Zombie")

        assertThatThrownBy { data.entries(owner).add("mob.Skeleton") }
            .isInstanceOf(UnsupportedOperationException::class.java)
    }

    private fun loadFromSavedTag(tag: CompoundTag): PlayerTargetListSavedData {
        val method = PlayerTargetListSavedData::class.java.getDeclaredMethod(
            "load",
            CompoundTag::class.java,
            net.minecraft.core.HolderLookup.Provider::class.java
        )
        method.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return method.invoke(null, tag, null as net.minecraft.core.HolderLookup.Provider?) as PlayerTargetListSavedData
    }
}
