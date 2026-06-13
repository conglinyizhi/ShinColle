package org.trp.shincolle.utility

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

object SpawnHelper {
    const val SPAWN_CLEARANCE = 0.25

    /**
     * 为 entity 寻找一个不会与方块相交、且脚下有可站立支撑的安全生成位置。
     *
     * 算法：
     * 1. 先给 desiredPos 的 Y 坐标加上 [SPAWN_CLEARANCE] 的垂直偏移。
     * 2. 用 [Level.noCollision] 检查 entity 的碰撞箱是否与新位置相交。
     * 3. 若相交，则向上扫描最多 [maxVerticalScan] 格，寻找第一个不碰撞且下方可站立的位置。
     * 4. 若仍未找到，则返回加过 clearance 的原始位置（调用方应自行回退处理）。
     */
    @JvmStatic
    @JvmOverloads
    fun findSafeSpawnPosition(
        level: Level,
        entity: Entity,
        desiredPos: Vec3,
        maxVerticalScan: Int = 5
    ): Vec3 {
        val basePos = desiredPos.add(0.0, SPAWN_CLEARANCE, 0.0)
        for (dy in 0..maxVerticalScan) {
            val candidate = basePos.add(0.0, dy.toDouble(), 0.0)
            if (isSafeSpawnPosition(level, entity, candidate)) {
                return candidate
            }
        }
        return basePos
    }

    /**
     * 检查 entity 在 [pos] 处是否安全：不越界、不与方块碰撞、脚下有可站立支撑。
     */
    @JvmStatic
    fun isSafeSpawnPosition(level: Level, entity: Entity, pos: Vec3): Boolean {
        val posBlock = BlockPos.containing(pos.x, pos.y, pos.z)
        if (level.isOutsideBuildHeight(posBlock) || level.isOutsideBuildHeight(posBlock.above())) {
            return false
        }
        val boxAtPos = entity.boundingBox.move(
            pos.x - entity.x,
            pos.y - entity.y,
            pos.z - entity.z
        )
        val standBlock = posBlock.below()
        return level.noCollision(entity, boxAtPos)
            && level.getBlockState(standBlock).entityCanStandOn(level, standBlock, entity)
    }
}
