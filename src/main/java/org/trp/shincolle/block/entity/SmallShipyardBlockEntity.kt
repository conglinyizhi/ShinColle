package org.trp.shincolle.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.ItemStackHandler
import org.trp.shincolle.Config
import org.trp.shincolle.block.SmallShipyardBlock
import org.trp.shincolle.crafting.ShipyardRecipes
import org.trp.shincolle.crafting.ShipyardRecipes.calcSmallGoalPower
import org.trp.shincolle.crafting.ShipyardRecipes.consumeOneFuel
import org.trp.shincolle.crafting.ShipyardRecipes.createSmallEquipResult
import org.trp.shincolle.crafting.ShipyardRecipes.createSmallShipResult
import org.trp.shincolle.crafting.ShipyardRecipes.getCurrentSmallMaterialAmount
import org.trp.shincolle.crafting.ShipyardRecipes.getFuelValue
import org.trp.shincolle.init.ModBlockEntities
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.menu.SmallShipyardMenu
import org.trp.shincolle.utility.PerformanceTrace.addBlockEntityTime
import org.trp.shincolle.utility.PerformanceTrace.elapsed
import org.trp.shincolle.utility.PerformanceTrace.enabled
import org.trp.shincolle.utility.PerformanceTrace.logSlowBlockEntityTick
import org.trp.shincolle.utility.PerformanceTrace.now
import kotlin.math.max
import kotlin.math.min

open class SmallShipyardBlockEntity(pos: BlockPos, blockState: BlockState) :
    BlockEntity(ModBlockEntities.SMALL_SHIPYARD.get(), pos, blockState), MenuProvider {
    val inventory: ItemStackHandler = object : ItemStackHandler(SLOT_COUNT) {
        override fun onContentsChanged(slot: Int) {
            markForSync()
        }
    }

    var powerConsumed: Int = 0
        private set
    var powerRemained: Int = 0
        private set
    var powerGoal: Int = 0
        private set
    private var buildType = 0
    private var buildRecord: IntArray? = intArrayOf(0, 0, 0, 0)
    private var active = false

    private fun serverTickInternal() {
        var stateChanged = false

        if (consumeFuel()) {
            stateChanged = true
        }

        val oldGoal = this.powerGoal
        updatePowerGoal()
        if (oldGoal != this.powerGoal) {
            stateChanged = true
        }

        if (this.isBuilding) {
            if (consumeInstantConstructionMaterial()) {
                stateChanged = true
            }

            this.powerRemained -= Config.smallShipyardBuildSpeed
            this.powerConsumed += Config.smallShipyardBuildSpeed
            stateChanged = true

            if (this.powerConsumed >= this.powerGoal) {
                finishBuild()
                stateChanged = true
            }
        }

        if (!canBuild() && this.powerConsumed != 0) {
            this.powerConsumed = 0
            stateChanged = true
        }

        val nowActive = this.isBuilding
        if (nowActive != this.active) {
            this.active = nowActive
            updateActiveBlockState(nowActive)
            stateChanged = true
        }

        if (stateChanged) {
            markForSync()
        }
    }

    fun dropContents() {
        if (this.level == null) {
            return
        }

        for (i in 0..<SLOT_COUNT) {
            val stack = this.inventory.getStackInSlot(i)
            if (stack.isEmpty()) {
                continue
            }
            Block.popResource(this.level, this.worldPosition, stack.copy())
            this.inventory.setStackInSlot(i, ItemStack.EMPTY)
        }
    }

    fun getBuildType(): Int {
        return this.buildType
    }

    fun setBuildType(buildType: Int) {
        val clamped = max(0, min(buildType, 4))
        val repeatBuild = clamped == 3 || clamped == 4
        val nextRecord = if (repeatBuild) this.currentMaterialAmount else this.buildRecord
        if (this.buildType == clamped
            && (!repeatBuild || this.buildRecord.contentEquals(nextRecord))
        ) {
            return
        }
        this.buildType = clamped
        if (repeatBuild) {
            this.buildRecord = nextRecord
        }
        markForSync()
    }

    fun hasRemainedPower(): Boolean {
        return this.powerRemained > Config.smallShipyardBuildSpeed
    }

    fun getPowerRemainingScaled(scale: Int): Int {
        if (scale <= 0) {
            return 0
        }
        return this.powerRemained * scale / Config.smallShipyardPowerMax
    }

    val remainingTimeSeconds: Int
        get() {
            if (this.powerGoal <= 0 || this.powerConsumed >= this.powerGoal) {
                return 0
            }
            return (((this.powerGoal - this.powerConsumed).toDouble() / Config.smallShipyardBuildSpeed) * 0.05).toInt()
        }

    val buildTimeString: String
        get() {
            val sec = this.remainingTimeSeconds
            val hours = sec / 3600
            val minutes = (sec % 3600) / 60
            val seconds = sec % 60
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

    fun getBuildRecord(): IntArray? {
        if (this.buildRecord == null || this.buildRecord!!.size < 4) {
            this.buildRecord = intArrayOf(0, 0, 0, 0)
        }
        return this.buildRecord
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.put("Inventory", this.inventory.serializeNBT(registries))
        tag.putInt("PowerConsumed", this.powerConsumed)
        tag.putInt("PowerRemained", this.powerRemained)
        tag.putInt("PowerGoal", this.powerGoal)
        tag.putInt("BuildType", this.buildType)
        tag.putIntArray("BuildRecord", getBuildRecord())
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        if (tag.contains("Inventory")) {
            this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"))
        }
        this.powerConsumed = tag.getInt("PowerConsumed")
        this.powerRemained = tag.getInt("PowerRemained")
        this.powerGoal = tag.getInt("PowerGoal")
        this.buildType = tag.getInt("BuildType")
        val loaded = tag.getIntArray("BuildRecord")
        if (loaded.size >= 4) {
            this.buildRecord = intArrayOf(loaded[0], loaded[1], loaded[2], loaded[3])
        } else {
            this.buildRecord = intArrayOf(0, 0, 0, 0)
        }
    }

    override fun getDisplayName(): Component {
        return Component.translatable("tile.shincolle.BlockSmallShipyard.name")
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu? {
        return SmallShipyardMenu(containerId, playerInventory, this)
    }

    private fun consumeFuel(): Boolean {
        if (this.powerRemained >= Config.smallShipyardPowerMax) {
            return false
        }

        val fuel = this.inventory.getStackInSlot(SLOT_FUEL)
        if (fuel.isEmpty() || fuel.`is`(ModItems.INSTANT_CON_MAT.get())) {
            return false
        }

        val fuelPower = getFuelValue(fuel, Config.smallShipyardFuelMagnification)
        if (fuelPower <= 0 || this.powerRemained + fuelPower > Config.smallShipyardPowerMax) {
            return false
        }

        this.inventory.setStackInSlot(SLOT_FUEL, consumeOneFuel(fuel))

        this.powerRemained += fuelPower
        return true
    }

    private fun consumeInstantConstructionMaterial(): Boolean {
        if (this.powerConsumed >= this.powerGoal) {
            return false
        }

        val stack = this.inventory.getStackInSlot(SLOT_FUEL)
        if (stack.isEmpty() || !stack.`is`(ModItems.INSTANT_CON_MAT.get())) {
            return false
        }

        stack.shrink(1)
        this.inventory.setStackInSlot(SLOT_FUEL, stack)
        this.powerConsumed += Config.smallShipyardBuildSpeed * Config.smallShipyardInstantTicks
        return true
    }

    private fun updatePowerGoal() {
        if (this.buildType == 0) {
            this.powerGoal = 0
            return
        }

        if (this.buildType == 3 || this.buildType == 4) {
            this.powerGoal = if (ShipyardRecipes.canSmallBuild(getBuildRecord()!!))
                ShipyardRecipes.calcSmallGoalPower(getBuildRecord()!!)
            else
                0
            return
        }

        this.powerGoal = calcSmallGoalPower(this.currentMaterialAmount)
    }

    private val isBuilding: Boolean
        get() = hasRemainedPower() && canBuild()

    private fun canBuild(): Boolean {
        if (this.powerGoal <= 0) {
            return false
        }
        if (!this.inventory.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
            return false
        }
        if (this.buildType == 3 || this.buildType == 4) {
            return ShipyardRecipes.canSmallBuild(getBuildRecord()!!) && canConsumeMaterials(getBuildRecord()!!)
        }
        return true
    }

    private fun finishBuild() {
        val mats: IntArray
        if (this.buildType == 3 || this.buildType == 4) {
            mats = getBuildRecord()!!.clone()
        } else {
            mats = this.currentMaterialAmount
        }

        if (!canConsumeMaterials(mats)) {
            this.powerConsumed = 0
            return
        }

        consumeMaterials(mats)

        val result = when (this.buildType) {
            2, 4 -> createSmallEquipResult(mats)
            else -> createSmallShipResult(mats)
        }
        this.inventory.setStackInSlot(SLOT_OUTPUT, result)

        this.powerConsumed = 0
        this.powerGoal = 0
        if (this.buildType < 3) {
            this.buildType = 0
        }
    }

    private val currentMaterialAmount: IntArray
        get() {
            val mats: Array<ItemStack?> = arrayOf<ItemStack?>(
                this.inventory.getStackInSlot(SLOT_GRUDGE),
                this.inventory.getStackInSlot(SLOT_ABYSSIUM),
                this.inventory.getStackInSlot(SLOT_AMMO),
                this.inventory.getStackInSlot(SLOT_POLYMETAL)
            )
            return getCurrentSmallMaterialAmount(mats)
        }

    private fun canConsumeMaterials(mats: IntArray): Boolean {
        for (i in 0..3) {
            if (this.inventory.getStackInSlot(i).count < mats[i]) {
                return false
            }
        }
        return true
    }

    private fun consumeMaterials(mats: IntArray) {
        for (i in 0..3) {
            val stack = this.inventory.getStackInSlot(i)
            if (stack.isEmpty()) {
                continue
            }
            stack.shrink(mats[i])
            this.inventory.setStackInSlot(i, stack)
        }
    }

    private fun updateActiveBlockState(nowActive: Boolean) {
        if (this.level == null) {
            return
        }

        val state = getBlockState()
        if (!state.hasProperty<Boolean?>(SmallShipyardBlock.Companion.ACTIVE)) {
            return
        }

        if (state.getValue<Boolean?>(SmallShipyardBlock.Companion.ACTIVE) != nowActive) {
            this.level!!.setBlock(
                this.worldPosition,
                state.setValue<Boolean?, Boolean?>(SmallShipyardBlock.Companion.ACTIVE, nowActive),
                3
            )
        }
    }

    open fun markForSync() {
        setChanged()
        if (this.level != null) {
            this.level!!.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3)
        }
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener?>? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return saveWithoutMetadata(registries)
    }

    companion object {
        const val SLOT_GRUDGE: Int = 0
        const val SLOT_ABYSSIUM: Int = 1
        const val SLOT_AMMO: Int = 2
        const val SLOT_POLYMETAL: Int = 3
        const val SLOT_FUEL: Int = 4
        const val SLOT_OUTPUT: Int = 5
        const val SLOT_COUNT: Int = 6

        fun serverTick(level: Level, pos: BlockPos?, state: BlockState?, blockEntity: SmallShipyardBlockEntity) {
            if (level.isClientSide) {
                return
            }

            val tracing = enabled()
            val start = if (tracing) now() else 0L
            try {
                blockEntity.serverTickInternal()
            } finally {
                if (tracing) {
                    val elapsed = elapsed(start)
                    addBlockEntityTime(elapsed)
                    logSlowBlockEntityTick(
                        blockEntity, "small_shipyard", elapsed,
                        ("active=" + blockEntity.active
                                + " buildType=" + blockEntity.buildType
                                + " power=" + blockEntity.powerConsumed + "/" + blockEntity.powerGoal
                                + " remained=" + blockEntity.powerRemained)
                    )
                }
            }
        }
    }
}
