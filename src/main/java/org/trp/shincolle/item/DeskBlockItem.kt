package org.trp.shincolle.item

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import org.trp.shincolle.client.renderer.item.DeskItemRenderer
import java.util.function.Consumer

class DeskBlockItem(block: Block, properties: Properties) : BlockItem(block, properties) {
    override fun initializeClient(consumer: Consumer<IClientItemExtensions?>) {
        consumer.accept(object : IClientItemExtensions {
            private var renderer: BlockEntityWithoutLevelRenderer? = null

            override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer {
                if (this.renderer == null) {
                    val minecraft = Minecraft.getInstance()
                    this.renderer = DeskItemRenderer(
                        minecraft.getBlockEntityRenderDispatcher(),
                        minecraft.getEntityModels()
                    )
                }
                return this.renderer!!
            }
        })
    }
}
