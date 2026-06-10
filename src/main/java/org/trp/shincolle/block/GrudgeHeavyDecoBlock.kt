package org.trp.shincolle.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import java.util.function.Function

class GrudgeHeavyDecoBlock(properties: Properties) : Block(properties) {
    override fun codec(): MapCodec<out Block?> {
        return CODEC
    }

    fun isBeaconBase(state: BlockState?, level: BlockGetter?, pos: BlockPos?, beaconPos: BlockPos?): Boolean {
        return true
    }

    companion object {
        val CODEC: MapCodec<GrudgeHeavyDecoBlock?> =
            simpleCodec<GrudgeHeavyDecoBlock?>(Function { properties: Properties? ->
                GrudgeHeavyDecoBlock(properties!!)
            })
    }
}
