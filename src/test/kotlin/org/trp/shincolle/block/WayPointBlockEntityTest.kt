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

        entity.setLastPos(BlockPos(1, 2, 3))
        entity.setNextPos(BlockPos(4, 5, 6))
        entity.setChestPos(BlockPos(7, 8, 9))
        entity.nextWpStayTime()
        entity.nextWpStayTime()
        entity.setOwnerUUID(owner)
        entity.setOwnerName("Admiral")

        val restored = TestWayPointBlockEntity()
        restored.loadForTest(entity.saveForTest())

        assertThat(restored.getLastPos()).isEqualTo(BlockPos(1, 2, 3))
        assertThat(restored.getNextPos()).isEqualTo(BlockPos(4, 5, 6))
        assertThat(restored.getChestPos()).isEqualTo(BlockPos(7, 8, 9))
        assertThat(restored.getWpStayTime()).isEqualTo(2)
        assertThat(restored.ownerUUID).isEqualTo(owner)
        assertThat(restored.ownerName).isEqualTo("Admiral")
    }

    @Test
    fun `waypoint block entity should treat null setters and sparse tags as zero state`() {
        val entity = TestWayPointBlockEntity()
        entity.setLastPos(null)
        entity.setNextPos(null)
        entity.setChestPos(null)
        entity.setOwnerUUID(null)
        entity.setOwnerName(null)

        assertThat(entity.getLastPos()).isEqualTo(BlockPos.ZERO)
        assertThat(entity.getNextPos()).isEqualTo(BlockPos.ZERO)
        assertThat(entity.getChestPos()).isEqualTo(BlockPos.ZERO)
        assertThat(entity.ownerUUID).isNull()
        assertThat(entity.ownerName).isEmpty()

        val restored = TestWayPointBlockEntity()
        restored.loadForTest(CompoundTag())

        assertThat(restored.getLastPos()).isEqualTo(BlockPos.ZERO)
        assertThat(restored.getNextPos()).isEqualTo(BlockPos.ZERO)
        assertThat(restored.getChestPos()).isEqualTo(BlockPos.ZERO)
        assertThat(restored.getWpStayTime()).isZero()
        assertThat(restored.ownerUUID).isNull()
        assertThat(restored.ownerName).isEmpty()
    }

    @Test
    fun `waypoint stay time display should match configured tick buckets`() {
        val entity = TestWayPointBlockEntity()

        assertThat(entity.stayTimeDisplay).isEqualTo("0s")

        repeat(1) { entity.nextWpStayTime() }
        assertThat(entity.getStayTimeTicks()).isEqualTo(100)
        assertThat(entity.stayTimeDisplay).isEqualTo("5s")

        repeat(5) { entity.nextWpStayTime() }
        assertThat(entity.getStayTimeTicks()).isEqualTo(1200)
        assertThat(entity.stayTimeDisplay).isEqualTo("1m")

        repeat(5) { entity.nextWpStayTime() }
        assertThat(entity.getStayTimeTicks()).isEqualTo(12000)
        assertThat(entity.stayTimeDisplay).isEqualTo("10m")
    }

    private class TestWayPointBlockEntity : WayPointBlockEntity(BlockPos.ZERO, ModBlocks.WAYPOINT.get().defaultBlockState()) {
        fun saveForTest(): CompoundTag = CompoundTag().also { saveAdditional(it, registries()) }

        fun loadForTest(tag: CompoundTag) {
            loadAdditional(tag, registries())
        }
    }

    companion object {
        private fun registries(): HolderLookup.Provider = RegistryAccess.EMPTY
    }
}
