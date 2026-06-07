@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
package org.trp.shincolle.init

import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import com.mojang.datafixers.types.Type
import org.trp.shincolle.Shincolle
import org.trp.shincolle.block.entity.*
import java.util.function.Supplier

private val NO_DATAFIXER_TYPE: Type<*>? = null

object ModBlockEntities {
    val BLOCK_ENTITY_TYPES: DeferredRegister<BlockEntityType<*>?> =
        DeferredRegister.create<BlockEntityType<*>?>(Registries.BLOCK_ENTITY_TYPE, Shincolle.MODID)

    @JvmField
    val DESK: DeferredHolder<BlockEntityType<*>?, BlockEntityType<DeskBlockEntity?>?> =
        BLOCK_ENTITY_TYPES.register<BlockEntityType<DeskBlockEntity?>?>(
            "blockdesk",
            Supplier {
                BlockEntityType.Builder.of<DeskBlockEntity?>(BlockEntitySupplier { pos: BlockPos, blockState: BlockState ->
                    DeskBlockEntity(
                        pos,
                        blockState
                    )
                }, ModBlocks.DESK.get()).build(NO_DATAFIXER_TYPE)
            })

    @JvmField
    val SMALL_SHIPYARD: DeferredHolder<BlockEntityType<*>?, BlockEntityType<SmallShipyardBlockEntity?>?> =
        BLOCK_ENTITY_TYPES.register<BlockEntityType<SmallShipyardBlockEntity?>?>(
            "small_shipyard",
            Supplier {
                BlockEntityType.Builder.of<SmallShipyardBlockEntity?>(BlockEntitySupplier { pos: BlockPos, blockState: BlockState ->
                    SmallShipyardBlockEntity(
                        pos,
                        blockState
                    )
                }, ModBlocks.SMALL_SHIPYARD.get()).build(NO_DATAFIXER_TYPE)
            })

    @JvmField
    val LARGE_SHIPYARD: DeferredHolder<BlockEntityType<*>?, BlockEntityType<LargeShipyardBlockEntity?>?> =
        BLOCK_ENTITY_TYPES.register<BlockEntityType<LargeShipyardBlockEntity?>?>(
            "large_shipyard",
            Supplier {
                BlockEntityType.Builder.of<LargeShipyardBlockEntity?>(BlockEntitySupplier { pos: BlockPos, blockState: BlockState ->
                    LargeShipyardBlockEntity(
                        pos,
                        blockState
                    )
                }, ModBlocks.LARGE_SHIPYARD.get()).build(NO_DATAFIXER_TYPE)
            })

    @JvmField
    val VOL_CORE: DeferredHolder<BlockEntityType<*>?, BlockEntityType<VolCoreBlockEntity?>?> =
        BLOCK_ENTITY_TYPES.register<BlockEntityType<VolCoreBlockEntity?>?>(
            "blockvolcore",
            Supplier {
                BlockEntityType.Builder.of<VolCoreBlockEntity?>(BlockEntitySupplier { pos: BlockPos, blockState: BlockState ->
                    VolCoreBlockEntity(
                        pos,
                        blockState
                    )
                }, ModBlocks.VOL_CORE.get()).build(NO_DATAFIXER_TYPE)
            })

    @JvmField
    val WAYPOINT: DeferredHolder<BlockEntityType<*>?, BlockEntityType<WayPointBlockEntity?>?> =
        BLOCK_ENTITY_TYPES.register<BlockEntityType<WayPointBlockEntity?>?>(
            "blockwaypoint",
            Supplier {
                BlockEntityType.Builder.of<WayPointBlockEntity?>(BlockEntitySupplier { pos: BlockPos, state: BlockState ->
                    WayPointBlockEntity(
                        pos,
                        state
                    )
                }, ModBlocks.WAYPOINT.get()).build(NO_DATAFIXER_TYPE)
            })

    @JvmField
    val CRANE: DeferredHolder<BlockEntityType<*>?, BlockEntityType<CraneBlockEntity?>?> =
        BLOCK_ENTITY_TYPES.register<BlockEntityType<CraneBlockEntity?>?>(
            "blockcrane",
            Supplier {
                BlockEntityType.Builder.of<CraneBlockEntity?>(BlockEntitySupplier { pos: BlockPos, blockState: BlockState ->
                    CraneBlockEntity(
                        pos,
                        blockState
                    )
                }, ModBlocks.CRANE.get()).build(NO_DATAFIXER_TYPE)
            })
}

