package org.trp.shincolle.integration.jade

import net.minecraft.network.chat.Component
import snownee.jade.api.BlockAccessor

class ShipyardJadeProvider private constructor() : IBlockComponentProvider, IServerDataProvider<BlockAccessor?>,
    IJadeProvider {
    public override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig?) {
        val data: CompoundTag? = accessor.getServerData()
        if (data == null || data.isEmpty()) {
            return
        }

        val buildType: Int = data.getInt(KEY_BUILD_TYPE)
        if (buildType <= 0) {
            tooltip.add(Component.translatable("tooltip.shincolle.jade.shipyard.idle"))
            return
        }

        val active: Boolean = data.getBoolean(KEY_ACTIVE)
        val consumed: Int = data.getInt(KEY_POWER_CONSUMED)
        val goal: Int = data.getInt(KEY_POWER_GOAL)
        val remained: Int = data.getInt(KEY_POWER_REMAINED)
        val remainingTime: String = data.getString(KEY_REMAINING_TIME)

        tooltip.add(
            Component.translatable(
                if (active) "tooltip.shincolle.jade.shipyard.active" else "tooltip.shincolle.jade.shipyard.ready"
            )
        )
        tooltip.add(Component.translatable("tooltip.shincolle.jade.shipyard.progress", consumed, goal))
        tooltip.add(Component.translatable("tooltip.shincolle.jade.shipyard.fuel", remained))
        if (!remainingTime.isEmpty()) {
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

    public override fun appendServerData(tag: CompoundTag, accessor: BlockAccessor) {
        val blockEntity: BlockEntity? = accessor.getBlockEntity()
        if (blockEntity is SmallShipyardBlockEntity) {
            appendSmallShipyard(tag, blockEntity)
        } else if (blockEntity is LargeShipyardBlockEntity) {
            appendLargeShipyard(tag, blockEntity)
        }
    }

    val uid: ResourceLocation
        get() = UID

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
            tag.putBoolean(KEY_ACTIVE, shipyard.hasRemainedPower() && shipyard.getPowerGoal() > 0)
            tag.putInt(KEY_POWER_CONSUMED, shipyard.getPowerConsumed())
            tag.putInt(KEY_POWER_GOAL, shipyard.getPowerGoal())
            tag.putInt(KEY_POWER_REMAINED, shipyard.getPowerRemained())
            tag.putInt(KEY_BUILD_TYPE, shipyard.getBuildType())
            tag.putString(KEY_REMAINING_TIME, shipyard.getBuildTimeString())
            tag.putIntArray(KEY_MATS_BUILD, shipyard.getBuildRecord())
        }

        private fun appendLargeShipyard(tag: CompoundTag, shipyard: LargeShipyardBlockEntity) {
            tag.putBoolean(KEY_ACTIVE, shipyard.hasRemainedPower() && shipyard.getPowerGoal() > 0)
            tag.putInt(KEY_POWER_CONSUMED, shipyard.getPowerConsumed())
            tag.putInt(KEY_POWER_GOAL, shipyard.getPowerGoal())
            tag.putInt(KEY_POWER_REMAINED, shipyard.getPowerRemained())
            tag.putInt(KEY_BUILD_TYPE, shipyard.getBuildType())
            tag.putString(KEY_REMAINING_TIME, shipyard.getBuildTimeString())
            tag.putIntArray(KEY_MATS_BUILD, shipyard.getMatsBuild())
            tag.putIntArray(KEY_MATS_STOCK, shipyard.getMatsStock())
        }

        private fun formatMats(mats: IntArray?): String {
            if (mats == null || mats.size < 4) {
                return "0 / 0 / 0 / 0"
            }
            return mats[0].toString() + " / " + mats[1] + " / " + mats[2] + " / " + mats[3]
        }
    }
}
