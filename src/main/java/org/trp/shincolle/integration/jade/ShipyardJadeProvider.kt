package org.trp.shincolle.integration.jade

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.BlockEntity
import org.trp.shincolle.Shincolle
import org.trp.shincolle.block.entity.LargeShipyardBlockEntity
import org.trp.shincolle.block.entity.SmallShipyardBlockEntity
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.IServerDataProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.config.IPluginConfig

class ShipyardJadeProvider private constructor() : IBlockComponentProvider,
    IServerDataProvider<BlockAccessor> {

    override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
        val data = accessor.serverData
        if (data.isEmpty()) {
            return
        }

        val buildType = data.getInt(KEY_BUILD_TYPE)
        if (buildType <= 0) {
            tooltip.add(Component.translatable("tooltip.shincolle.jade.shipyard.idle"))
            return
        }

        val active = data.getBoolean(KEY_ACTIVE)
        val consumed = data.getInt(KEY_POWER_CONSUMED)
        val goal = data.getInt(KEY_POWER_GOAL)
        val remained = data.getInt(KEY_POWER_REMAINED)
        val remainingTime = data.getString(KEY_REMAINING_TIME)

        tooltip.add(
            Component.translatable(
                if (active) "tooltip.shincolle.jade.shipyard.active" else "tooltip.shincolle.jade.shipyard.ready"
            )
        )
        tooltip.add(Component.translatable("tooltip.shincolle.jade.shipyard.progress", consumed, goal))
        tooltip.add(Component.translatable("tooltip.shincolle.jade.shipyard.fuel", remained))
        if (remainingTime.isNotEmpty()) {
            tooltip.add(Component.translatable("tooltip.shincolle.jade.shipyard.time", remainingTime))
        }

        if (data.contains(KEY_MATS_BUILD)) {
            tooltip.add(
                Component.translatable(
                    "tooltip.shincolle.jade.shipyard.materials",
                    formatMats(data.getIntArray(KEY_MATS_BUILD))
                )
            )
        }

        if (data.contains(KEY_MATS_STOCK)) {
            tooltip.add(
                Component.translatable(
                    "tooltip.shincolle.jade.shipyard.stock",
                    formatMats(data.getIntArray(KEY_MATS_STOCK))
                )
            )
        }
    }

    override fun appendServerData(tag: CompoundTag, accessor: BlockAccessor) {
        when (val blockEntity = accessor.blockEntity) {
            is SmallShipyardBlockEntity -> appendSmallShipyard(tag, blockEntity)
            is LargeShipyardBlockEntity -> appendLargeShipyard(tag, blockEntity)
            else -> {}
        }
    }

    override fun getUid(): ResourceLocation = UID

    companion object {
        val INSTANCE: ShipyardJadeProvider = ShipyardJadeProvider()
        private val UID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "shipyard")

        private const val KEY_ACTIVE = "active"
        private const val KEY_POWER_CONSUMED = "powerConsumed"
        private const val KEY_POWER_GOAL = "powerGoal"
        private const val KEY_POWER_REMAINED = "powerRemained"
        private const val KEY_REMAINING_TIME = "remainingTime"
        private const val KEY_BUILD_TYPE = "buildType"
        private const val KEY_MATS_BUILD = "matsBuild"
        private const val KEY_MATS_STOCK = "matsStock"

        private fun appendSmallShipyard(tag: CompoundTag, shipyard: SmallShipyardBlockEntity) {
            tag.putBoolean(KEY_ACTIVE, shipyard.hasRemainedPower() && shipyard.powerGoal > 0)
            tag.putInt(KEY_POWER_CONSUMED, shipyard.powerConsumed)
            tag.putInt(KEY_POWER_GOAL, shipyard.powerGoal)
            tag.putInt(KEY_POWER_REMAINED, shipyard.powerRemained)
            tag.putInt(KEY_BUILD_TYPE, shipyard.getBuildType())
            tag.putString(KEY_REMAINING_TIME, shipyard.buildTimeString)
            tag.putIntArray(KEY_MATS_BUILD, shipyard.getBuildRecord())
        }

        private fun appendLargeShipyard(tag: CompoundTag, shipyard: LargeShipyardBlockEntity) {
            tag.putBoolean(KEY_ACTIVE, shipyard.hasRemainedPower() && shipyard.powerGoal > 0)
            tag.putInt(KEY_POWER_CONSUMED, shipyard.powerConsumed)
            tag.putInt(KEY_POWER_GOAL, shipyard.powerGoal)
            tag.putInt(KEY_POWER_REMAINED, shipyard.powerRemained)
            tag.putInt(KEY_BUILD_TYPE, shipyard.getBuildType())
            tag.putString(KEY_REMAINING_TIME, shipyard.buildTimeString)
            tag.putIntArray(KEY_MATS_BUILD, shipyard.matsBuild)
            tag.putIntArray(KEY_MATS_STOCK, shipyard.matsStock)
        }

        private fun formatMats(mats: IntArray?): String {
            if (mats == null || mats.size < 4) {
                return "0 / 0 / 0 / 0"
            }
            return "${mats[0]} / ${mats[1]} / ${mats[2]} / ${mats[3]}"
        }
    }
}
