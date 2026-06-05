package org.trp.shincolle.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.trp.shincolle.init.ModBlocks

class GrudgeHeavyBlock(properties: Properties) : Block(properties) {
    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }

        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS
        }

        if (!isLargeShipyardPattern(level, pos)) {
            return InteractionResult.PASS
        }

        setLargeShipyardSupportFormed(level, pos, true)

        val activatedState = ModBlocks.LARGE_SHIPYARD.get().defaultBlockState()
            .setValue<Direction?, Direction?>(LargeShipyardBlock.Companion.FACING, player.getDirection().getOpposite())
            .setValue<Boolean?, Boolean?>(LargeShipyardBlock.Companion.ACTIVE, false)
        level.setBlock(pos, activatedState, UPDATE_ALL)
        level.playSound(
            null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.7f,
            0.9f + level.random.nextFloat() * 0.2f
        )
        return InteractionResult.CONSUME
    }

    companion object {
        private val TYPE_OTHER: Byte = -1
        private const val TYPE_POLYMETAL: Byte = 1
        private const val TYPE_GRUDGE_HEAVY: Byte = 2

        private val LARGE_SHIPYARD_PATTERN: Array<Array<ByteArray?>> = arrayOf<Array<ByteArray?>?>(
            arrayOf<ByteArray?>(
                byteArrayOf(TYPE_POLYMETAL, TYPE_POLYMETAL, TYPE_POLYMETAL),
                byteArrayOf(TYPE_POLYMETAL, TYPE_POLYMETAL, TYPE_POLYMETAL),
                byteArrayOf(TYPE_POLYMETAL, TYPE_POLYMETAL, TYPE_POLYMETAL)
            ),
            arrayOf<ByteArray?>(
                byteArrayOf(TYPE_POLYMETAL, TYPE_OTHER, TYPE_POLYMETAL),
                byteArrayOf(TYPE_OTHER, TYPE_OTHER, TYPE_OTHER),
                byteArrayOf(TYPE_POLYMETAL, TYPE_OTHER, TYPE_POLYMETAL)
            ),
            arrayOf<ByteArray?>(
                byteArrayOf(TYPE_OTHER, TYPE_OTHER, TYPE_OTHER),
                byteArrayOf(TYPE_OTHER, TYPE_GRUDGE_HEAVY, TYPE_OTHER),
                byteArrayOf(TYPE_OTHER, TYPE_OTHER, TYPE_OTHER)
            )
        )

        fun hasLargeShipyardSupport(level: Level, center: BlockPos): Boolean {
            if (center.getY() - 2 < level.getMinBuildHeight()) {
                return false
            }

            for (x in -1..1) {
                for (z in -1..1) {
                    if (!level.getBlockState(center.offset(x, -2, z)).`is`(ModBlocks.POLYMETAL.get())) {
                        return false
                    }
                }
            }

            if (!level.getBlockState(center.offset(1, -1, 1)).`is`(ModBlocks.POLYMETAL.get())) {
                return false
            }
            if (!level.getBlockState(center.offset(1, -1, -1)).`is`(ModBlocks.POLYMETAL.get())) {
                return false
            }
            if (!level.getBlockState(center.offset(-1, -1, 1)).`is`(ModBlocks.POLYMETAL.get())) {
                return false
            }
            if (!level.getBlockState(center.offset(-1, -1, -1)).`is`(ModBlocks.POLYMETAL.get())) {
                return false
            }

            return true
        }

        fun setLargeShipyardSupportFormed(level: Level, center: BlockPos, formed: Boolean) {
            setSupportPolymetalFormed(level, center.offset(1, -1, 1), formed)
            setSupportPolymetalFormed(level, center.offset(1, -1, -1), formed)
            setSupportPolymetalFormed(level, center.offset(-1, -1, 1), formed)
            setSupportPolymetalFormed(level, center.offset(-1, -1, -1), formed)

            for (x in -1..1) {
                for (z in -1..1) {
                    setSupportPolymetalFormed(level, center.offset(x, -2, z), formed)
                }
            }
        }

        private fun isLargeShipyardPattern(level: Level, center: BlockPos): Boolean {
            if (center.getY() - 2 < level.getMinBuildHeight()) {
                return false
            }

            if (!hasLargeShipyardSupport(level, center)) {
                return false
            }

            for (dy in -1..0) {
                val layer: Array<ByteArray?> = LARGE_SHIPYARD_PATTERN[dy + 2]
                for (dx in -1..1) {
                    for (dz in -1..1) {
                        val expected = layer[dx + 1]!![dz + 1]
                        val actual: Byte = getPatternType(level.getBlockState(center.offset(dx, dy, dz)))

                        if (actual != expected) {
                            return false
                        }
                    }
                }
            }

            return true
        }

        private fun setSupportPolymetalFormed(level: Level, pos: BlockPos, formed: Boolean) {
            val state = level.getBlockState(pos)
            if (!state.`is`(ModBlocks.POLYMETAL.get()) || !state.hasProperty<Boolean?>(PolymetalBlock.Companion.FORMED)) {
                return
            }

            if (state.getValue<Boolean?>(PolymetalBlock.Companion.FORMED) != formed) {
                level.setBlock(
                    pos,
                    state.setValue<Boolean?, Boolean?>(PolymetalBlock.Companion.FORMED, formed),
                    UPDATE_ALL
                )
            }
        }

        private fun getPatternType(state: BlockState): Byte {
            if (state.`is`(ModBlocks.POLYMETAL.get())) {
                return TYPE_POLYMETAL
            }
            if (state.`is`(ModBlocks.GRUDGE_HEAVY_BLOCK.get())) {
                return TYPE_GRUDGE_HEAVY
            }
            return TYPE_OTHER
        }
    }
}
