package org.trp.shincolle.server

import java.util.HashMap
import java.util.UUID
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LightBlock
import net.minecraft.world.level.block.state.BlockState

object TemporaryLightService {
    private const val DEFAULT_LIGHT_LEVEL = 15
    const val TEMP_LIGHT_LIFETIME_TICKS: Int = 120

    private val lightsByLevel: MutableMap<String, MutableMap<BlockPos, TemporaryLightEntry>> = HashMap()

    @JvmStatic
    fun refreshLight(level: ServerLevel, pos: BlockPos, ownerId: UUID? = null) {
        val currentState = level.getBlockState(pos)
        if (!canManageAt(currentState)) {
            return
        }

        val lightState = Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, DEFAULT_LIGHT_LEVEL)
        if (!currentState.`is`(Blocks.LIGHT)) {
            level.setBlockAndUpdate(pos, lightState)
        } else if (currentState.getValue(LightBlock.LEVEL) != DEFAULT_LIGHT_LEVEL) {
            level.setBlockAndUpdate(pos, lightState)
        }

        val levelLights = lightsByLevel.computeIfAbsent(levelKey(level)) { HashMap() }
        levelLights[pos.immutable()] = TemporaryLightEntry(level.gameTime + TEMP_LIGHT_LIFETIME_TICKS, ownerId)
    }

    @JvmStatic
    fun tick(serverLevel: ServerLevel) {
        val key = levelKey(serverLevel)
        val levelLights = lightsByLevel[key] ?: return
        if (levelLights.isEmpty()) {
            lightsByLevel.remove(key)
            return
        }

        val now = serverLevel.gameTime
        val expired = ArrayList<BlockPos>()
        for ((pos, entry) in levelLights) {
            if (entry.expiresAtTick > now) {
                continue
            }

            val currentState = serverLevel.getBlockState(pos)
            if (currentState.`is`(Blocks.LIGHT)) {
                serverLevel.removeBlock(pos, false)
            }
            expired.add(pos)
        }

        for (pos in expired) {
            levelLights.remove(pos)
        }
        if (levelLights.isEmpty()) {
            lightsByLevel.remove(key)
        }
    }

    private fun canManageAt(state: BlockState): Boolean {
        return state.isAir || state.`is`(Blocks.LIGHT)
    }

    private fun levelKey(level: ServerLevel): String {
        return level.dimension().location().toString()
    }

    private data class TemporaryLightEntry(
        val expiresAtTick: Long,
        val ownerId: UUID?,
    )
}
