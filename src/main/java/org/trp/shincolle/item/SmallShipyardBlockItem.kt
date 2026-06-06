package org.trp.shincolle.item

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import org.trp.shincolle.client.renderer.item.SmallShipyardItemRenderer
import java.util.function.Consumer

class SmallShipyardBlockItem(block: Block, properties: Properties) : BlockItem(block, properties) {
    @Deprecated("Overrides deprecated NeoForge client API")
    override fun initializeClient(consumer: Consumer<IClientItemExtensions?>) {
        consumer.accept(object : IClientItemExtensions {
            private var renderer: BlockEntityWithoutLevelRenderer? = null

            override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer {
                if (this.renderer == null) {
                    val minecraft = Minecraft.getInstance()
                    this.renderer = SmallShipyardItemRenderer(
                        minecraft.blockEntityRenderDispatcher,
                        minecraft.entityModels
                    )
                }
                return this.renderer!!
            }
        })
    }
}
