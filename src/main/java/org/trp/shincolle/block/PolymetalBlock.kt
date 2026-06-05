package org.trp.shincolle.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty

class PolymetalBlock(properties: Properties) : Block(properties) {
    init {
        this.registerDefaultState(this.stateDefinition.any().setValue<Boolean?, Boolean?>(FORMED, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FORMED)
    }

    override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean {
        return true
    }

    override fun getShadeBrightness(state: BlockState, level: BlockGetter, pos: BlockPos): Float {
        return 1.0f
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return if (state.getValue<Boolean?>(FORMED)) RenderShape.INVISIBLE else RenderShape.MODEL
    }

    companion object {
        val FORMED: BooleanProperty = BooleanProperty.create("formed")
    }
}