package org.trp.shincolle.block

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.trp.shincolle.block.entity.WayPointBlockEntity
import org.trp.shincolle.init.ModBlocks
import java.util.UUID

class WayPointBlockEntityTest {

    @Test
    fun `waypoint block entity should normalize setters and persist owner routing state`() {
        val owner = UUID.randomUUID()
        val entity = TestWayPointBlockEntity()

        entity.lastPos = BlockPos(1, 2, 3)
        entity.nextPos = BlockPos(4, 5, 6)
        entity.chestPos = BlockPos(7, 8, 9)
        entity.nextWpStayTime()
        entity.nextWpStayTime()
        entity.ownerUUID = owner
        entity.ownerName = "Admiral"

        val restored = TestWayPointBlockEntity()
        restored.loadForTest(entity.saveForTest())

        assertThat(restored.lastPos).isEqualTo(BlockPos(1, 2, 3))
        assertThat(restored.nextPos).isEqualTo(BlockPos(4, 5, 6))
        assertThat(restored.chestPos).isEqualTo(BlockPos(7, 8, 9))
        assertThat(restored.wpStayTime).isEqualTo(2)
        assertThat(restored.ownerUUID).isEqualTo(owner)
        assertThat(restored.ownerName).isEqualTo("Admiral")
    }

    @Test
    fun `waypoint block entity should treat null setters and sparse tags as zero state`() {
        val entity = TestWayPointBlockEntity()
        entity.lastPos = null
        entity.nextPos = null
        entity.chestPos = null
        entity.ownerUUID = null
        entity.ownerName = ""

        assertThat(entity.lastPos).isEqualTo(BlockPos.ZERO)
        assertThat(entity.nextPos).isEqualTo(BlockPos.ZERO)
        assertThat(entity.chestPos).isEqualTo(BlockPos.ZERO)
        assertThat(entity.ownerUUID).isNull()
        assertThat(entity.ownerName).isEmpty()

        val restored = TestWayPointBlockEntity()
        restored.loadForTest(CompoundTag())

        assertThat(restored.lastPos).isEqualTo(BlockPos.ZERO)
        assertThat(restored.nextPos).isEqualTo(BlockPos.ZERO)
        assertThat(restored.chestPos).isEqualTo(BlockPos.ZERO)
        assertThat(restored.wpStayTime).isZero()
        assertThat(restored.ownerUUID).isNull()
        assertThat(restored.ownerName).isEmpty()
    }

    @Test
    fun `waypoint stay time display should match configured tick buckets`() {
        val entity = TestWayPointBlockEntity()

        assertThat(entity.stayTimeDisplay).isEqualTo("0s")

        repeat(1) { entity.nextWpStayTime() }
        assertThat(entity.stayTimeTicks).isEqualTo(100)
        assertThat(entity.stayTimeDisplay).isEqualTo("5s")

        repeat(5) { entity.nextWpStayTime() }
        assertThat(entity.stayTimeTicks).isEqualTo(1200)
        assertThat(entity.stayTimeDisplay).isEqualTo("1m")

        repeat(5) { entity.nextWpStayTime() }
        assertThat(entity.stayTimeTicks).isEqualTo(12000)
        assertThat(entity.stayTimeDisplay).isEqualTo("10m")
    }

    private class TestWayPointBlockEntity : WayPointBlockEntity(BlockPos.ZERO, ModBlocks.WAYPOINT.get()!!.defaultBlockState()) {
        fun saveForTest(): CompoundTag = CompoundTag().also { saveAdditional(it, registries()) }

        fun loadForTest(tag: CompoundTag) {
            loadAdditional(tag, registries())
        }
    }

    companion object {
        private fun registries(): HolderLookup.Provider = RegistryAccess.EMPTY
    }
}
