package org.trp.shincolle.server

import net.minecraft.nbt.CompoundTag
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.UUID

class TeamDiplomacySavedDataRegressionTest {

    @Test
    fun `display data should normalize values and reject no-op writes`() {
        val owner = UUID.randomUUID()
        val data = TeamDiplomacySavedData()

        assertThat(data.setDisplayData(null, "Fleet", "Leader")).isFalse()
        assertThat(data.get(owner)).isNull()

        assertThat(data.setDisplayData(owner, null, null)).isFalse()
        val entry = data.get(owner)
        assertThat(entry).isNotNull
        assertThat(entry!!.teamName()).isEmpty()
        assertThat(entry.leaderName()).isEmpty()

        assertThat(data.setDisplayData(owner, null, null)).isFalse()
        assertThat(data.setDisplayData(owner, "Fleet", "Leader")).isTrue()
        assertThat(data.setDisplayData(owner, "Fleet", "Leader")).isFalse()
        assertThat(data.get(owner)!!.teamName()).isEqualTo("Fleet")
        assertThat(data.get(owner)!!.leaderName()).isEqualTo("Leader")
    }

    @Test
    fun `ally and banned relations should be exclusive and ignore invalid owners`() {
        val owner = UUID.randomUUID()
        val target = UUID.randomUUID()
        val data = TeamDiplomacySavedData()

        assertThat(data.addAlly(null, target)).isFalse()
        assertThat(data.addAlly(owner, null)).isFalse()
        assertThat(data.addAlly(owner, owner)).isFalse()

        assertThat(data.addAlly(owner, target)).isTrue()
        assertThat(data.areAllies(owner, target)).isTrue()
        assertThat(data.isBanned(owner, target)).isFalse()

        assertThat(data.addAlly(owner, target)).isFalse()

        assertThat(data.addBanned(owner, target)).isTrue()
        assertThat(data.areAllies(owner, target)).isFalse()
        assertThat(data.isBanned(owner, target)).isTrue()

        assertThat(data.removeBanned(owner, target)).isTrue()
        assertThat(data.removeBanned(owner, target)).isFalse()
        assertThat(data.isBanned(owner, target)).isFalse()
    }

    @Test
    fun `saved data should persist display data and relations`() {
        val owner = UUID.randomUUID()
        val ally = UUID.randomUUID()
        val banned = UUID.randomUUID()
        val data = TeamDiplomacySavedData()

        data.setDisplayData(owner, "Fleet", "Leader")
        data.addAlly(owner, ally)
        data.addBanned(owner, banned)

        val restored = loadFromSavedTag(data.save(CompoundTag(), null))
        val entry = restored.get(owner)

        assertThat(entry).isNotNull
        assertThat(entry!!.teamName()).isEqualTo("Fleet")
        assertThat(entry.leaderName()).isEqualTo("Leader")
        assertThat(restored.areAllies(owner, ally)).isTrue()
        assertThat(restored.isBanned(owner, banned)).isTrue()
    }

    @Test
    fun `saved data should ignore invalid persisted owner and malformed relation uuids`() {
        val tag = CompoundTag()
        val entries = net.minecraft.nbt.ListTag()

        val invalidOwner = CompoundTag().apply {
            putString("TeamName", "Broken")
            putString("LeaderName", "Ignored")
        }
        val malformedRelations = CompoundTag().apply {
            val owner = UUID.randomUUID()
            putUUID("Owner", owner)
            putString("TeamName", "Fleet")
            putString("LeaderName", "Leader")
            put("Allies", net.minecraft.nbt.ListTag().apply {
                add(net.minecraft.nbt.StringTag.valueOf("not-a-uuid"))
            })
            put("Banned", net.minecraft.nbt.ListTag().apply {
                add(net.minecraft.nbt.StringTag.valueOf(UUID.randomUUID().toString()))
                add(net.minecraft.nbt.StringTag.valueOf("broken"))
            })
        }

        entries.add(invalidOwner)
        entries.add(malformedRelations)
        tag.put("Entries", entries)

        val restored = loadFromSavedTag(tag)
        val validOwner = malformedRelations.getUUID("Owner")

        assertThat(restored.get(null)).isNull()
        assertThat(restored.get(validOwner)).isNotNull
        assertThat(restored.get(validOwner)!!.allies()).isEmpty()
        assertThat(restored.get(validOwner)!!.banned()).hasSize(1)
    }

    @Test
    fun `entry views should stay read-only`() {
        val owner = UUID.randomUUID()
        val ally = UUID.randomUUID()
        val banned = UUID.randomUUID()
        val data = TeamDiplomacySavedData()

        data.addAlly(owner, ally)
        data.addBanned(owner, banned)

        val entry = data.get(owner)!!

        assertThatThrownBy { entry.allies().add(UUID.randomUUID()) }
            .isInstanceOf(UnsupportedOperationException::class.java)
        assertThatThrownBy { entry.banned().add(UUID.randomUUID()) }
            .isInstanceOf(UnsupportedOperationException::class.java)
    }

    private fun loadFromSavedTag(tag: CompoundTag): TeamDiplomacySavedData {
        val method = TeamDiplomacySavedData::class.java.getDeclaredMethod(
            "load",
            CompoundTag::class.java,
            net.minecraft.core.HolderLookup.Provider::class.java
        )
        method.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return method.invoke(null, tag, null as net.minecraft.core.HolderLookup.Provider?) as TeamDiplomacySavedData
    }
}
