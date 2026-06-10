package org.trp.shincolle.init

import net.minecraft.util.valueproviders.UniformInt
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DropExperienceBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister
import org.trp.shincolle.Shincolle
import org.trp.shincolle.block.AbyssiumBlock
import org.trp.shincolle.block.CraneBlock
import org.trp.shincolle.block.DeskBlock
import org.trp.shincolle.block.FrameBlock
import org.trp.shincolle.block.GrudgeHeavyBlock
import org.trp.shincolle.block.GrudgeHeavyDecoBlock
import org.trp.shincolle.block.LargeShipyardBlock
import org.trp.shincolle.block.PolymetalBlock
import org.trp.shincolle.block.SmallShipyardBlock
import org.trp.shincolle.block.VolBlock
import org.trp.shincolle.block.VolCoreBlock
import org.trp.shincolle.block.WayPointBlock
import java.util.function.Supplier
import java.util.function.ToIntFunction

object ModBlocks {
    val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(Shincolle.MODID)

    val DESK: DeferredBlock<Block?> = BLOCKS.register<Block?>(
        "blockdesk",
        Supplier { DeskBlock() })

    val ABYSSIUM: DeferredBlock<Block?> = BLOCKS.register<Block?>(
        "abyssium",
        Supplier {
            AbyssiumBlock(
                BlockBehaviour.Properties.of().strength(3.0f).requiresCorrectToolForDrops().sound(SoundType.METAL)
            )
        })

    val GRUDGE_BLOCK: DeferredBlock<Block?> = BLOCKS.register<Block?>(
        "grudge_block",
        Supplier { Block(BlockBehaviour.Properties.of().strength(1.5f).noOcclusion()) })

    val GRUDGE_XP_BLOCK: DeferredBlock<Block?> = BLOCKS.register<Block?>(
        "grudge_xp_block",
        Supplier {
            Block(
                BlockBehaviour.Properties.of()
                    .strength(1.0f, 200.0f)
                    .lightLevel(ToIntFunction { state: BlockState? -> 15 })
                    .sound(SoundType.SAND)
                    .noOcclusion()
            )
        })

    @JvmField
    val GRUDGE_HEAVY_BLOCK: DeferredBlock<Block?> = BLOCKS.register<Block?>(
        "grudge_heavy_block",
        Supplier { GrudgeHeavyBlock(BlockBehaviour.Properties.of().strength(1.5f)) })

    @JvmField
    val GRUDGE_HEAVY_DECO_BLOCK: DeferredBlock<Block?> = BLOCKS.register<Block?>(
        "grudge_heavy_deco_block",
        Supplier {
            GrudgeHeavyDecoBlock(
                BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .lightLevel(ToIntFunction { state: BlockState? -> 15 })
            )
        })

    val FRAME_BLOCK: DeferredBlock<Block?> = BLOCKS.register<Block?>(
        "blockframe",
        Supplier { FrameBlock() })

    val SMALL_SHIPYARD: DeferredBlock<Block?> = BLOCKS.register<Block?>(
        "small_shipyard",
        Supplier { SmallShipyardBlock() })

    @JvmField
    val LARGE_SHIPYARD: DeferredBlock<Block?> = BLOCKS.register<Block?>(
        "large_shipyard",
        Supplier { LargeShipyardBlock() })

    @JvmField
    val POLYMETAL: DeferredBlock<Block?> = BLOCKS.register<Block?>(
        "polymetal",
        Supplier {
            PolymetalBlock(
                BlockBehaviour.Properties.of().strength(3.0f).requiresCorrectToolForDrops().noOcclusion()
            )
        })

    val POLYMETAL_ORE: DeferredBlock<Block?> = BLOCKS.register<Block?>(
        "polymetal_ore",
        Supplier {
            DropExperienceBlock(
                UniformInt.of(1, 4), BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(ToIntFunction { state: BlockState? -> 10 })
                    .sound(SoundType.STONE)
            )
        })

    val POLYMETAL_GRAVEL: DeferredBlock<Block?> = BLOCKS.register<Block?>(
        "polymetal_gravel",
        Supplier { Block(BlockBehaviour.Properties.of().strength(0.8f).sound(SoundType.SAND)) })

    val VOL_BLOCK: DeferredBlock<Block?> = BLOCKS.register<Block?>(
        "blockvolblock",
        Supplier { VolBlock() })

    val VOL_CORE: DeferredBlock<Block?> = BLOCKS.register<Block?>(
        "blockvolcore",
        Supplier { VolCoreBlock() })

    val WAYPOINT: DeferredBlock<Block?> = BLOCKS.register<Block?>(
        "blockwaypoint",
        Supplier { WayPointBlock() })

    val CRANE: DeferredBlock<Block?> = BLOCKS.register<Block?>(
        "blockcrane",
        Supplier { CraneBlock() })
}
