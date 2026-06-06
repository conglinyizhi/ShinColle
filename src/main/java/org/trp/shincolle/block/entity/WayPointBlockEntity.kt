package org.trp.shincolle.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler
import org.trp.shincolle.client.WaypointClientHelper.tickClient
import org.trp.shincolle.init.ModBlockEntities
import org.trp.shincolle.utility.PerformanceTrace.addBlockEntityTime
import org.trp.shincolle.utility.PerformanceTrace.elapsed
import org.trp.shincolle.utility.PerformanceTrace.enabled
import org.trp.shincolle.utility.PerformanceTrace.logSlowBlockEntityTick
import org.trp.shincolle.utility.PerformanceTrace.now
import java.util.*

class WayPointBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(ModBlockEntities.WAYPOINT.get(), pos, state),
    IWaypoint {
    private var tickCount = 0
    override var lastPos: BlockPos? = BlockPos.ZERO
    override var nextPos: BlockPos? = BlockPos.ZERO
    override var chestPos: BlockPos? = BlockPos.ZERO
    var wpStayTime: Int = 0
        private set
    override var ownerUUID: UUID? = null
    override var ownerName = ""

    val stayTimeTicks: Int
        get() {
            if (wpStayTime >= 1 && wpStayTime <= 5) return wpStayTime * 100
            if (wpStayTime >= 6 && wpStayTime <= 10) return (wpStayTime - 5) * 1200
            if (wpStayTime >= 11 && wpStayTime <= 16) return (wpStayTime - 10) * 12000
            return 0
        }

    override val stayTimeDisplay: String
        get() {
            val ticks = this.stayTimeTicks
            if (ticks == 0) return "0s"
            val totalSec = ticks / 20
            if (totalSec < 60) return totalSec.toString() + "s"
            return (totalSec / 60).toString() + "m"
        }

    fun nextWpStayTime() {
        val next = (wpStayTime + 1) % 17
        if (wpStayTime == next) {
            return
        }
        wpStayTime = next
        markForSync()
    }

    fun setOwnerUUID(uuid: UUID?) {
        if (this.ownerUUID == uuid) {
            return
        }
        this.ownerUUID = uuid
        markForSync()
    }

    fun setOwnerName(name: String?) {
        val next = if (name == null) "" else name
        if (this.ownerName == next) {
            return
        }
        this.ownerName = next
        markForSync()
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.put("lastPos", NbtUtils.writeBlockPos(lastPos))
        tag.put("nextPos", NbtUtils.writeBlockPos(nextPos))
        tag.put("chestPos", NbtUtils.writeBlockPos(chestPos))
        tag.putInt("wpStayTime", wpStayTime)
        if (ownerUUID != null) {
            tag.putUUID("ownerUUID", ownerUUID)
        }
        tag.putString("ownerName", ownerName)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        if (tag.contains("lastPos")) lastPos = NbtUtils.readBlockPos(tag, "lastPos").orElse(BlockPos.ZERO)
        if (tag.contains("nextPos")) nextPos = NbtUtils.readBlockPos(tag, "nextPos").orElse(BlockPos.ZERO)
        if (tag.contains("chestPos")) chestPos = NbtUtils.readBlockPos(tag, "chestPos").orElse(BlockPos.ZERO)
        wpStayTime = tag.getInt("wpStayTime")
        if (tag.hasUUID("ownerUUID")) ownerUUID = tag.getUUID("ownerUUID")
        ownerName = tag.getString("ownerName")
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val tag = CompoundTag()
        saveAdditional(tag, registries)
        return tag
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    private fun serverTick() {
        if (this.tickCount % 64 == 0) {
            checkValidity()
        }
    }

    private fun checkValidity() {
        if (this.level == null || this.level!!.isClientSide) return

        if (this.nextPos !== BlockPos.ZERO) {
            val be = this.level!!.getBlockEntity(this.nextPos)
            if (be !is IWaypoint) {
                this.nextPos = BlockPos.ZERO
                markForSync()
            }
        }

        if (this.chestPos !== BlockPos.ZERO) {
            val be = this.level!!.getBlockEntity(this.chestPos)
            if (be == null || this.level!!.getCapability<IItemHandler?, Direction?>(
                    Capabilities.ItemHandler.BLOCK,
                    this.chestPos,
                    null
                ) == null
            ) {
                this.chestPos = BlockPos.ZERO
                markForSync()
            }
        }
    }

    private fun markForSync() {
        setChanged()
        if (level != null) {
            level!!.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL)
        }
    }

    companion object {
        fun tick(level: Level, pos: BlockPos, state: BlockState?, be: WayPointBlockEntity) {
            be.tickCount++

            if (level.isClientSide) {
                tickClient(level, pos, be)
            } else {
                val tracing = enabled()
                val start = if (tracing) now() else 0L
                try {
                    be.serverTick()
                } finally {
                    if (tracing) {
                        val elapsed = elapsed(start)
                        addBlockEntityTime(elapsed)
                        logSlowBlockEntityTick(
                            be, "waypoint", elapsed,
                            "chest=" + be.chestPos + " next=" + be.nextPos + " stay=" + be.wpStayTime
                        )
                    }
                }
            }
        }

        private fun tickClient(level: Level, pos: BlockPos, be: WayPointBlockEntity) {
            tickClient(level, pos, be, be.tickCount)
        }
    }
}
