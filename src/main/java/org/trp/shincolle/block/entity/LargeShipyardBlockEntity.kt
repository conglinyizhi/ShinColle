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
import org.trp.shincolle.block.GrudgeHeavyBlock
import org.trp.shincolle.block.LargeShipyardBlock
import org.trp.shincolle.crafting.ShipyardRecipes.addLargeMaterialStock
import org.trp.shincolle.crafting.ShipyardRecipes.calcLargeGoalPower
import org.trp.shincolle.crafting.ShipyardRecipes.consumeOneFuel
import org.trp.shincolle.crafting.ShipyardRecipes.createLargeEquipResult
import org.trp.shincolle.crafting.ShipyardRecipes.createLargeOutputMaterial
import org.trp.shincolle.crafting.ShipyardRecipes.createLargeShipResult
import org.trp.shincolle.crafting.ShipyardRecipes.getFuelValue
import org.trp.shincolle.crafting.ShipyardRecipes.moveBuildMaterialAmount
import org.trp.shincolle.crafting.ShipyardRecipes.putHeavyGrudgeStorageTag
import org.trp.shincolle.init.ModBlockEntities
import org.trp.shincolle.init.ModBlocks
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.menu.LargeShipyardMenu
import org.trp.shincolle.utility.PerformanceTrace.addBlockEntityTime
import org.trp.shincolle.utility.PerformanceTrace.elapsed
import org.trp.shincolle.utility.PerformanceTrace.enabled
import org.trp.shincolle.utility.PerformanceTrace.logSlowBlockEntityTick
import org.trp.shincolle.utility.PerformanceTrace.now
import kotlin.math.max
import kotlin.math.min

class LargeShipyardBlockEntity(pos: BlockPos, blockState: BlockState) :
    BlockEntity(ModBlockEntities.LARGE_SHIPYARD.get(), pos, blockState), MenuProvider {
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
    private var invMode = 0
    private var selectMat = 0
    var matsBuild: IntArray = intArrayOf(0, 0, 0, 0)
        private set
    var matsStock: IntArray = intArrayOf(0, 0, 0, 0)
        private set
    private var active = false
    var renderYaw: Float = 0f
        private set
    var renderPitch: Float = 0f
        private set
    private var renderAnglesInitialized = false

    private fun serverTickInternal(level: Level, pos: BlockPos) {
        if (!GrudgeHeavyBlock.Companion.hasLargeShipyardSupport(level, pos)) {
            collapseStructure()
            return
        }

        var stateChanged = false

        val oldGoal = this.powerGoal
        this.powerGoal = if (this.buildType == 0) 0 else calcLargeGoalPower(this.matsBuild)
        if (oldGoal != this.powerGoal) {
            stateChanged = true
        }

        if (consumeFuel()) {
            stateChanged = true
        }

        if (handleMaterials()) {
            stateChanged = true
        }

        if (this.isBuilding) {
            this.powerRemained -= Config.largeShipyardBuildSpeed
            this.powerConsumed += Config.largeShipyardBuildSpeed
            stateChanged = true

            if (consumeInstantConstructionMaterial()) {
                stateChanged = true
            }

            if (this.powerConsumed >= this.powerGoal) {
                finishBuild()
                stateChanged = true
            }
        } else if (this.powerConsumed != 0) {
            this.powerConsumed = 0
            stateChanged = true
        }

        val nowActive = this.isBuilding
        if (this.active != nowActive) {
            this.active = nowActive
            updateActiveBlockState(nowActive)
            stateChanged = true
        }

        if (stateChanged) {
            markForSync()
        }
    }

    private fun collapseStructure() {
        if (this.level == null) {
            return
        }

        dropInventoryContents()
        Block.popResource(this.level, this.worldPosition, createStoredHeavyGrudgeStack())
        GrudgeHeavyBlock.Companion.setLargeShipyardSupportFormed(this.level, this.worldPosition, false)
        this.level!!.setBlock(
            this.worldPosition,
            ModBlocks.GRUDGE_HEAVY_BLOCK.get().defaultBlockState(),
            Block.UPDATE_ALL
        )
    }

    private fun dropInventoryContents() {
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

    fun createStoredHeavyGrudgeStack(): ItemStack {
        val stack = ItemStack(ModItems.GRUDGE_HEAVY_BLOCK.get())
        val mats = IntArray(MAT_COUNT)
        for (i in 0..<MAT_COUNT) {
            mats[i] = max(0, this.matsBuild[i] + this.matsStock[i])
        }
        putHeavyGrudgeStorageTag(stack, mats, this.powerRemained)
        return stack
    }

    fun getBuildType(): Int {
        return this.buildType
    }

    fun setBuildType(buildType: Int) {
        val next = max(0, min(buildType, 4))
        if (this.buildType == next) {
            return
        }
        this.buildType = next
        markForSync()
    }

    fun getInvMode(): Int {
        return this.invMode
    }

    fun setInvMode(invMode: Int) {
        val next = if (invMode <= 0) 0 else 1
        if (this.invMode == next) {
            return
        }
        this.invMode = next
        markForSync()
    }

    fun getSelectMat(): Int {
        return this.selectMat
    }

    fun setSelectMat(selectMat: Int) {
        val next = max(0, min(selectMat, MAT_COUNT - 1))
        if (this.selectMat == next) {
            return
        }
        this.selectMat = next
        markForSync()
    }

    fun getMatBuild(index: Int): Int {
        return this.matsBuild[index]
    }

    fun getMatStock(index: Int): Int {
        return this.matsStock[index]
    }

    fun getPowerRemainingScaled(scale: Int): Int {
        if (scale <= 0) {
            return 0
        }
        return this.powerRemained * scale / Config.largeShipyardPowerMax
    }

    fun hasRemainedPower(): Boolean {
        return this.powerRemained > Config.largeShipyardBuildSpeed
    }

    val remainingTimeSeconds: Int
        get() {
            if (this.powerGoal <= 0 || this.powerConsumed >= this.powerGoal) {
                return 0
            }
            return (((this.powerGoal - this.powerConsumed).toDouble() / Config.largeShipyardBuildSpeed) * 0.05).toInt()
        }

    val buildTimeString: String
        get() {
            val sec = this.remainingTimeSeconds
            val hours = sec / 3600
            val minutes = (sec % 3600) / 60
            val seconds = sec % 60
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

    fun moveBuildMaterialAmount(matType: Int, value: Int) {
        val beforeBuild: IntArray? = this.matsBuild.clone()
        val beforeStock: IntArray? = this.matsStock.clone()
        moveBuildMaterialAmount(this.matsBuild, this.matsStock, matType, value)
        if (beforeBuild.contentEquals(this.matsBuild) && beforeStock.contentEquals(this.matsStock)) {
            return
        }
        markForSync()
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.put("Inventory", this.inventory.serializeNBT(registries))
        tag.putInt("PowerConsumed", this.powerConsumed)
        tag.putInt("PowerRemained", this.powerRemained)
        tag.putInt("PowerGoal", this.powerGoal)
        tag.putInt("BuildType", this.buildType)
        tag.putInt("InvMode", this.invMode)
        tag.putInt("SelectMat", this.selectMat)
        tag.putIntArray("MatsBuild", this.matsBuild)
        tag.putIntArray("MatsStock", this.matsStock)
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
        this.invMode = tag.getInt("InvMode")
        this.selectMat = max(0, min(tag.getInt("SelectMat"), MAT_COUNT - 1))
        this.matsBuild = sanitizeMatsArray(tag.getIntArray("MatsBuild"))
        this.matsStock = sanitizeMatsArray(tag.getIntArray("MatsStock"))
    }

    override fun getDisplayName(): Component {
        return Component.translatable("tile.shincolle.BlockLargeShipyard.name")
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu? {
        return LargeShipyardMenu(containerId, playerInventory, this)
    }

    fun hasRenderAngles(): Boolean {
        return this.renderAnglesInitialized
    }

    fun setRenderAngles(yaw: Float, pitch: Float) {
        this.renderYaw = yaw
        this.renderPitch = pitch
        this.renderAnglesInitialized = true
    }

    private fun consumeFuel(): Boolean {
        if (this.powerRemained >= Config.largeShipyardPowerMax) {
            return false
        }

        for (i in SLOT_IO_START..SLOT_IO_END) {
            val stack = this.inventory.getStackInSlot(i)
            if (stack.isEmpty()) {
                continue
            }

            val fuelPower = getFuelValue(stack, Config.largeShipyardFuelMagnification)
            if (fuelPower <= 0 || this.powerRemained + fuelPower > Config.largeShipyardPowerMax) {
                continue
            }

            this.inventory.setStackInSlot(i, consumeOneFuel(stack))

            this.powerRemained += fuelPower
            return true
        }

        return false
    }

    private fun handleMaterials(): Boolean {
        if (this.invMode == 0) {
            for (i in SLOT_IO_START..SLOT_IO_END) {
                val stack = this.inventory.getStackInSlot(i)
                if (stack.isEmpty()) {
                    continue
                }
                if (!addLargeMaterialStock(this.matsStock, stack)) {
                    continue
                }

                stack.shrink(1)
                this.inventory.setStackInSlot(i, stack)
                return true
            }
            return false
        }

        val compressNum = 9
        val normalNum = 1
        val matType = this.selectMat

        if (this.matsStock[matType] >= compressNum && outputMaterialToSlot(matType, true)) {
            this.matsStock[matType] -= compressNum
            return true
        }

        if (this.matsStock[matType] >= normalNum && outputMaterialToSlot(matType, false)) {
            this.matsStock[matType] -= normalNum
            return true
        }

        return false
    }

    private fun outputMaterialToSlot(selectMat: Int, compressed: Boolean): Boolean {
        val output = createLargeOutputMaterial(selectMat, compressed)
        if (output.isEmpty()) {
            return false
        }

        val slot = findFitSlot(output)
        if (slot < 0) {
            return false
        }

        val current = this.inventory.getStackInSlot(slot)
        if (current.isEmpty()) {
            this.inventory.setStackInSlot(slot, output)
        } else {
            current.grow(output.getCount())
            this.inventory.setStackInSlot(slot, current)
        }
        return true
    }

    private fun findFitSlot(output: ItemStack): Int {
        for (i in SLOT_IO_START..SLOT_IO_END) {
            val current = this.inventory.getStackInSlot(i)
            if (current.isEmpty()) {
                return i
            }

            if (ItemStack.isSameItemSameComponents(current, output)
                && current.getCount() + output.getCount() <= current.getMaxStackSize()
            ) {
                return i
            }
        }
        return -1
    }

    private val isBuilding: Boolean
        get() = hasRemainedPower() && canBuild()

    private fun canBuild(): Boolean {
        return this.powerGoal > 0 && this.inventory.getStackInSlot(SLOT_OUTPUT).isEmpty()
    }

    private fun consumeInstantConstructionMaterial(): Boolean {
        if (this.powerConsumed >= this.powerGoal) {
            return false
        }

        for (i in SLOT_IO_START..SLOT_IO_END) {
            val stack = this.inventory.getStackInSlot(i)
            if (stack.isEmpty() || !stack.`is`(ModItems.INSTANT_CON_MAT.get())) {
                continue
            }

            stack.shrink(1)
            this.inventory.setStackInSlot(i, stack)
            this.powerConsumed += Config.largeShipyardBuildSpeed * Config.largeShipyardInstantTicks
            return true
        }

        return false
    }

    private fun finishBuild() {
        val result = when (this.buildType) {
            2, 4 -> createLargeEquipResult(this.matsBuild)
            else -> createLargeShipResult(this.matsBuild)
        }
        this.inventory.setStackInSlot(SLOT_OUTPUT, result)

        this.powerConsumed = 0
        this.powerGoal = 0

        if (this.buildType == 3 || this.buildType == 4) {
            setupRepeatBuild()
        } else {
            this.buildType = 0
            this.matsBuild = intArrayOf(0, 0, 0, 0)
        }
    }

    private fun setupRepeatBuild() {
        var canRepeat = true
        for (i in 0..<MAT_COUNT) {
            if (this.matsStock[i] < this.matsBuild[i]) {
                canRepeat = false
                break
            }
        }

        if (!canRepeat) {
            this.buildType = 0
            this.matsBuild = intArrayOf(0, 0, 0, 0)
            return
        }

        for (i in 0..<MAT_COUNT) {
            this.matsStock[i] -= this.matsBuild[i]
        }
    }

    private fun updateActiveBlockState(nowActive: Boolean) {
        if (this.level == null) {
            return
        }

        val state = getBlockState()
        if (!state.hasProperty<Boolean?>(LargeShipyardBlock.Companion.ACTIVE)) {
            return
        }

        if (state.getValue<Boolean?>(LargeShipyardBlock.Companion.ACTIVE) != nowActive) {
            this.level!!.setBlock(
                this.worldPosition,
                state.setValue<Boolean?, Boolean?>(LargeShipyardBlock.Companion.ACTIVE, nowActive),
                3
            )
        }
    }

    fun markForSync() {
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
        const val SLOT_OUTPUT: Int = 0
        const val SLOT_IO_START: Int = 1
        const val SLOT_IO_END: Int = 9
        const val SLOT_COUNT: Int = 10

        private const val MAT_COUNT = 4

        fun serverTick(level: Level, pos: BlockPos, state: BlockState?, blockEntity: LargeShipyardBlockEntity) {
            if (level.isClientSide) {
                return
            }

            val tracing = enabled()
            val start = if (tracing) now() else 0L
            try {
                blockEntity.serverTickInternal(level, pos)
            } finally {
                if (tracing) {
                    val elapsed = elapsed(start)
                    addBlockEntityTime(elapsed)
                    logSlowBlockEntityTick(
                        blockEntity, "large_shipyard", elapsed,
                        ("active=" + blockEntity.active
                                + " buildType=" + blockEntity.buildType
                                + " invMode=" + blockEntity.invMode
                                + " power=" + blockEntity.powerConsumed + "/" + blockEntity.powerGoal
                                + " remained=" + blockEntity.powerRemained)
                    )
                }
            }
        }

        private fun sanitizeMatsArray(input: IntArray): IntArray {
            if (input.size < MAT_COUNT) {
                return intArrayOf(0, 0, 0, 0)
            }
            return intArrayOf(input[0], input[1], input[2], input[3])
        }
    }
}
