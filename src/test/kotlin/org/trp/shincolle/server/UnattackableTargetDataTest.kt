package org.trp.shincolle.server

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class UnattackableTargetDataTest {

    @Test
    fun `toggle should reject blank class names`() {
        val data = UnattackableTargetData()

        assertThat(data.toggle(null)).isFalse()
        assertThat(data.toggle("")).isFalse()
        assertThat(data.toggle("   ")).isFalse()
        assertThat(data.entries()).isEmpty()
    }

    @Test
    fun `toggle should add remove and sort class names`() {
        val data = UnattackableTargetData()

        assertThat(data.toggle("mob.Zombie")).isTrue()
        assertThat(data.toggle("mob.Skeleton")).isTrue()
        assertThat(data.contains("mob.Zombie")).isTrue()
        assertThat(data.entries()).containsExactly("mob.Skeleton", "mob.Zombie")

        assertThat(data.toggle("mob.Zombie")).isFalse()
        assertThat(data.contains("mob.Zombie")).isFalse()
        assertThat(data.entries()).containsExactly("mob.Skeleton")
    }

    @Test
    fun `saved data should persist non blank class names`() {
        val data = UnattackableTargetData()
        data.toggle("mob.Zombie")
        data.toggle("mob.Skeleton")

        val restored = loadFromSavedTag(data.save(CompoundTag(), null))

        assertThat(restored.entries()).containsExactly("mob.Skeleton", "mob.Zombie")
        assertThat(restored.contains("mob.Zombie")).isTrue()
    }

    @Test
    fun `saved data should ignore blank persisted class names`() {
        val tag = CompoundTag().apply {
            put("ClassNames", ListTag().apply {
                add(StringTag.valueOf(""))
                add(StringTag.valueOf("   "))
                add(StringTag.valueOf("mob.Creeper"))
            })
        }

        val restored = loadFromSavedTag(tag)

        assertThat(restored.entries()).containsExactly("mob.Creeper")
    }

    @Test
    fun `entry views should stay read only`() {
        val data = UnattackableTargetData()
        data.toggle("mob.Zombie")

        assertThatThrownBy { data.entries().add("mob.Skeleton") }
            .isInstanceOf(UnsupportedOperationException::class.java)
    }

    private fun loadFromSavedTag(tag: CompoundTag): UnattackableTargetData {
        val method = UnattackableTargetData::class.java.getDeclaredMethod(
            "load",
            CompoundTag::class.java,
            net.minecraft.core.HolderLookup.Provider::class.java
        )
        method.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return method.invoke(null, tag, null as net.minecraft.core.HolderLookup.Provider?) as UnattackableTargetData
    }
}
