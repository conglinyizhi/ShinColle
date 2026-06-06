package org.trp.shincolle.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import java.util.function.Function
import java.util.function.ToIntFunction

class VolBlock : Block(
    Properties.of()
        .mapColor(MapColor.COLOR_RED)
        .strength(3.0f, 200.0f)
        .lightLevel(ToIntFunction { state: BlockState? -> 15 })
        .sound(SoundType.SAND)
        .requiresCorrectToolForDrops()
) {
    override fun codec(): MapCodec<out Block?> {
        return CODEC
    }

    public override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun isFlammable(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Boolean {
        return false
    }

    override fun getFlammability(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int {
        return 0
    }

    override fun getFireSpreadSpeed(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int {
        return 0
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
        if (random.nextInt(3) != 0) {
            return
        }

        val belowPos = pos.below()
        val belowState = level.getBlockState(belowPos)
        val openBelow = !belowState.isFaceSturdy(level, belowPos, Direction.UP)

        if (openBelow) {
            spawnDripParticle(
                level,
                pos,
                random,
                0.18 + random.nextDouble() * 0.64,
                0.05 + random.nextDouble() * 0.9,
                0.02
            )
        }

        for (direction in Direction.Plane.HORIZONTAL) {
            if (random.nextBoolean()) {
                continue
            }
            val sidePos = pos.relative(direction)
            val sideState = level.getBlockState(sidePos)
            if (sideState.isFaceSturdy(level, sidePos, direction.opposite)) {
                continue
            }

            val x =
                if (direction.stepX == 0) 0.2 + random.nextDouble() * 0.6 else 0.5 + direction.stepX * 0.48
            val y = 0.1 + random.nextDouble() * 0.7
            val z =
                if (direction.stepZ == 0) 0.2 + random.nextDouble() * 0.6 else 0.5 + direction.stepZ * 0.48
            spawnDripParticle(level, pos, random, x, y, z)
        }
    }

    fun isBeaconBase(state: BlockState?, level: BlockGetter?, pos: BlockPos?, beaconPos: BlockPos?): Boolean {
        return true
    }

    companion object {
        val CODEC: MapCodec<VolBlock?> = simpleCodec<VolBlock?>(Function { properties: Properties? -> VolBlock() })

        private fun spawnDripParticle(
            level: Level,
            pos: BlockPos,
            random: RandomSource,
            offsetX: Double,
            offsetY: Double,
            offsetZ: Double
        ) {
            level.addParticle(
                ParticleTypes.DRIPPING_WATER,
                pos.x + offsetX,
                pos.y + offsetY,
                pos.z + offsetZ,
                (random.nextDouble() - 0.5) * 0.01,
                -0.02 - random.nextDouble() * 0.01,
                (random.nextDouble() - 0.5) * 0.01
            )
        }
    }
}
