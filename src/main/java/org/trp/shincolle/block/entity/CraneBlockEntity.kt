package org.trp.shincolle.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler
import org.trp.shincolle.Config
import org.trp.shincolle.block.CraneBlock
import org.trp.shincolle.client.WaypointClientHelper
import org.trp.shincolle.entity.EntityTransportWa
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModBlockEntities
import org.trp.shincolle.init.ModParticles
import org.trp.shincolle.init.ModSounds
import org.trp.shincolle.item.LegacyEquipItem
// getEquipTypeId and getVariant are instance methods on LegacyEquipItem, not top-level imports
import org.trp.shincolle.menu.CraneMenu
import org.trp.shincolle.utility.InventoryHelper.calcItemStackAmount
import org.trp.shincolle.utility.InventoryHelper.checkInventoryFluidContainer
import org.trp.shincolle.utility.InventoryHelper.matchTargetItem
import org.trp.shincolle.utility.InventoryHelper.moveItemstackToInv
import org.trp.shincolle.utility.InventoryHelper.tryDrainContainer
import org.trp.shincolle.utility.InventoryHelper.tryFillContainer
import org.trp.shincolle.utility.PerformanceTrace.addBlockEntityTime
import org.trp.shincolle.utility.PerformanceTrace.elapsed
import org.trp.shincolle.utility.PerformanceTrace.enabled
import org.trp.shincolle.utility.PerformanceTrace.logSlowBlockEntityTick
import org.trp.shincolle.utility.PerformanceTrace.now
import java.util.*
import kotlin.math.max
import kotlin.math.min

class CraneBlockEntity(pos: BlockPos, blockState: BlockState) :
    BlockEntity(ModBlockEntities.CRANE.get(), pos, blockState), MenuProvider, IWaypoint {
    val inventory: ItemStackHandler = object : ItemStackHandler(18) {
        override fun onContentsChanged(slot: Int) {
            markForSync()
        }
    }

    val fluidTank: FluidTank = object : FluidTank(16000) {
        override fun onContentsChanged() {
            markForSync()
        }

        override fun getCapacity(): Int {
            return max(1, Config.craneTankCapacity)
        }
    }

    private var remainedPower = 0
    var powerMax: Int = 1000000
        private set
    private var isActive = false
    private var checkMetadata = false
    private var checkOredict = false
    private var checkNbt = false
    private var enabLoad = true
    private var enabUnload = true
    private var craneMode = 0
    private var modeItem = 0
    private var modeRedstone = 0
    private var modeLiquid = 0
    private var modeEnergy = 0

    override var lastPos: BlockPos? = BlockPos.ZERO
    override var nextPos: BlockPos? = BlockPos.ZERO
    override var chestPos: BlockPos? = BlockPos.ZERO
    private var isPaired = false
    override var ownerUUID: UUID? = null
        set(value) {
            if (field == value) return
            field = value
            markForSync()
        }
    override var ownerName = ""
        set(value) {
            val next = value ?: ""
            if (field == next) return
            field = next
            markForSync()
        }

    private var tickCount = 0
    private var tickRedstone = 0
    private var craningShip: EntityShipBase? = null
    private var syncedShipId = -1
    private var liquidTransferRate = 0
    private var chestHandler: IItemHandler? = null
    private var combinedChestHandler: IItemHandler? = null
    private var partDelay = 0

    private fun clientTick() {
        if (this.level != null) {
            WaypointClientHelper.tickClient(this.level!!, this.worldPosition, this, this.tickCount)

            if (this.partDelay > 0) this.partDelay--

            if (this.isActive && this.partDelay <= 0) {
                var targetShip: EntityShipBase? = null
                if (this.level!!.getEntity(this.syncedShipId) is EntityShipBase) {
                    targetShip = this.level!!.getEntity(this.syncedShipId) as EntityShipBase
                }

                if (targetShip != null) {
                    this.partDelay = 128
                    var distY = this.worldPosition.getY() - targetShip.getY() - 1.0
                    if (distY < 1.0) {
                        distY = 1.0
                    }
                    this.level!!.addParticle(
                        ModParticles.PARTICLE_CRANING.get(),
                        this.worldPosition.getX() + 0.5,
                        this.worldPosition.getY() - 1.0,
                        this.worldPosition.getZ() + 0.5,
                        distY,
                        0.25,
                        0.0
                    )


                    this.level!!.addParticle(
                        ModParticles.PARTICLE_SPARKLE.get(),
                        targetShip.getX(), targetShip.getY() + targetShip.getBbHeight() * 0.4, targetShip.getZ(),
                        3.0, targetShip.getBbWidth().toDouble(), 0.1
                    )
                }
            }
        }
    }

    private fun serverTick() {
        if (this.tickRedstone > 0) {
            this.tickRedstone--
            if (this.tickRedstone == 0) setRedstoneSignal(false)
        }

        if (this.tickCount % 16 == 0) {
            if (this.isActive) {
                if (checkPairedChest()) {
                    applyPreLiquidTransfer(this.modeLiquid)

                    if (checkCraningShip()) {
                        if (this.modeRedstone == 1) {
                            this.tickRedstone = 18
                            setRedstoneSignal(true)
                        }

                        this.craningShip!!.setStateTimer(1, this.craningShip!!.getStateTimer(1) + 16)
                        var moved = false

                        if (this.enabLoad) {
                            if (applyItemTransfer(true)) moved = true
                        }

                        if (!moved && this.enabUnload) {
                            if (applyItemTransfer(false)) moved = true
                        }


                        if (this.modeLiquid != 0) {
                            if (applyLiquidTransfer(this.modeLiquid)) moved = true
                        }

                        if (this.modeEnergy != 0) {
                            if (applyEnergyTransfer()) moved = true
                        }

                        if (moved) {
                            if (this.level != null) {
                                this.level!!.playSound(
                                    null,
                                    this.worldPosition,
                                    ModSounds.SHIP_AIRCRAFT.get(),
                                    SoundSource.BLOCKS,
                                    0.5f,
                                    1.0f
                                )
                            }
                        }
                        checkCraneEnding()
                    }
                } else {
                    this.isActive = false
                    markForSync()
                }
            }
        }

        if (this.tickCount % 64 == 0) {
            checkValidity()
        }
    }

    private fun checkValidity() {
        if (this.level == null || this.level!!.isClientSide) return

        if (this.isPaired && this.chestPos !== BlockPos.ZERO) {
            val handler = this.level!!.getCapability<IItemHandler?, Direction?>(
                Capabilities.ItemHandler.BLOCK,
                this.chestPos,
                null
            )
            if (handler == null) {
                this.isPaired = false
                this.chestPos = BlockPos.ZERO
                this.chestHandler = null
                this.combinedChestHandler = null
                markForSync()
            }
        }

        if (this.nextPos !== BlockPos.ZERO) {
            val be = this.level!!.getBlockEntity(this.nextPos)
            if (be !is IWaypoint) {
                this.nextPos = BlockPos.ZERO
                markForSync()
            }
        }
    }

    private fun checkPairedChest(): Boolean {
        if (this.chestPos === BlockPos.ZERO || this.level == null) return false
        val handler =
            this.level!!.getCapability<IItemHandler?, Direction?>(Capabilities.ItemHandler.BLOCK, this.chestPos, null)
        if (handler != null) {
            this.chestHandler = handler
            this.combinedChestHandler = createCombinedChestHandler(handler)
            return true
        }
        this.chestHandler = null
        this.combinedChestHandler = null
        return false
    }

    private fun checkCraningShip(): Boolean {
        if (this.craningShip != null && this.craningShip!!.isAlive && !this.craningShip!!.isRemoved) {
            if (this.craningShip!!.distanceToSqr(
                    this.worldPosition.getX() + 0.5,
                    this.worldPosition.getY().toDouble(),
                    this.worldPosition.getZ() + 0.5
                ) < 64.0
            ) {
                if (this.craningShip!!.getStateMinor(43) == 2) {
                    if (this.syncedShipId != this.craningShip!!.getId()) {
                        this.syncedShipId = this.craningShip!!.getId()
                        markForSync()
                    }
                    return true
                }
                if (this.craningShip!!.getStateMinor(43) == 1) {
                    moveShipToCrane(this.craningShip!!)
                    if (this.syncedShipId != this.craningShip!!.getId()) {
                        this.syncedShipId = this.craningShip!!.getId()
                        markForSync()
                    }
                    return true
                }
            }
        }

        val aabb = AABB(this.worldPosition).inflate(8.0)
        val ships = this.level!!.getEntitiesOfClass<EntityShipBase?>(EntityShipBase::class.java, aabb)
        for (ship in ships) {
            if (ship.isAlive && !ship.isRemoved && ship.isTame && this.ownerUUID != null && this.ownerUUID == ship.getOwnerUUID()) {
                if (ship.getStateMinor(43) == 1 || ship.getStateMinor(43) == 2) {
                    this.craningShip = ship
                    this.liquidTransferRate = calculateLiquidTransferRate(ship)
                    if (this.syncedShipId != ship.getId()) {
                        this.syncedShipId = ship.getId()
                        markForSync()
                    }
                    if (ship.getStateMinor(43) == 1) {
                        moveShipToCrane(ship)
                        ship.setStateMinor(43, 2)
                    }
                    return true
                }
            }
        }
        if (this.syncedShipId != -1) {
            this.syncedShipId = -1
            this.liquidTransferRate = 0
            markForSync()
        }
        return false
    }

    private fun moveShipToCrane(ship: EntityShipBase) {
        ship.moveGuardTargetTo(
            Vec3(
                this.worldPosition.getX() + 0.5,
                this.worldPosition.getY() - 2.0,
                this.worldPosition.getZ() + 0.5
            ),
            1.0
        )
    }

    private fun applyPreLiquidTransfer(mode: Int) {
        if (this.chestHandler == null) return
        val preTransferAmount = max(1000, this.liquidTransferRate)
        if (mode == 1) {
            val maxDrain = min(preTransferAmount, this.fluidTank.getCapacity() - this.fluidTank.getFluidAmount())
            if (maxDrain <= 0) return
            val drained = drainFromChestContainers(this.fluidTank.getFluid(), maxDrain)
            if (!drained.isEmpty()) {
                this.fluidTank.fill(drained, IFluidHandler.FluidAction.EXECUTE)
                markChestForSync()
            }
        } else if (mode == 2) {
            if (!this.fluidTank.getFluid().isEmpty()) {
                if (fillChestContainers(this.fluidTank.getFluid())) {
                    markChestForSync()
                }
            }
        }
    }

    private fun applyItemTransfer(isLoading: Boolean): Boolean {
        if (this.craningShip == null || this.combinedChestHandler == null) return false
        val invFrom = if (isLoading) this.combinedChestHandler else this.craningShip!!.inventory
        val invTo = if (isLoading) this.craningShip!!.inventory else this.combinedChestHandler

        val filterStart = if (isLoading) 0 else 9
        var hasNormalFilter = false
        for (i in 0..8) {
            val filter = this.inventory.getStackInSlot(filterStart + i)
            if (!filter.isEmpty() && !getItemMode(filterStart + i)) {
                hasNormalFilter = true
                break
            }
        }

        if (hasNormalFilter) {
            for (i in 0..8) {
                val filter = this.inventory.getStackInSlot(filterStart + i)
                if (!filter.isEmpty() && !getItemMode(filterStart + i)) {
                    if (canMoveItem(isLoading, filter)) {
                        for (slot in 0..<invFrom!!.getSlots()) {
                            val stack = invFrom.getStackInSlot(slot)
                            if (matchTargetItem(stack, filter, this.checkMetadata, this.checkNbt, this.checkOredict)) {
                                val extracted = invFrom.extractItem(slot, stack.getCount(), false)
                                if (!extracted.isEmpty()) {
                                    val moved = moveItemstackToInv(invTo, extracted, null)
                                    if (extracted.getCount() > 0) {
                                        returnRemainderToSourceOrDrop(invFrom, extracted)
                                    }
                                    if (moved) {
                                        markChestForSync()
                                        return true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (slot in 0..<invFrom!!.getSlots()) {
                val stack = invFrom.getStackInSlot(slot)
                if (!stack.isEmpty() && isNotModeItem(stack, isLoading)) {
                    val extracted = invFrom.extractItem(slot, stack.getCount(), false)
                    if (!extracted.isEmpty()) {
                        val moved = moveItemstackToInv(invTo, extracted, null)
                        if (extracted.getCount() > 0) {
                            returnRemainderToSourceOrDrop(invFrom, extracted)
                        }
                        if (moved) {
                            markChestForSync()
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun canMoveItem(isLoading: Boolean, temp: ItemStack): Boolean {
        if (this.craneMode == 3) {
            val targetInv: IItemHandler = (if (isLoading) this.craningShip!!.inventory else this.combinedChestHandler)!!
            val current = calcItemStackAmount(targetInv, temp, this.checkMetadata, this.checkNbt, this.checkOredict)
            return current < temp.getCount()
        } else if (this.craneMode == 4) {
            val sourceInv: IItemHandler = (if (isLoading) this.combinedChestHandler else this.craningShip!!.inventory)!!
            val current = calcItemStackAmount(sourceInv, temp, this.checkMetadata, this.checkNbt, this.checkOredict)
            return current > temp.getCount()
        }
        return true
    }

    private fun isNotModeItem(stack: ItemStack, isLoading: Boolean): Boolean {
        val startIdx = if (isLoading) 0 else 9
        for (i in 0..8) {
            val temp = this.inventory.getStackInSlot(startIdx + i)
            if (!temp.isEmpty() && matchTargetItem(stack, temp, this.checkMetadata, this.checkNbt, this.checkOredict)) {
                if (getItemMode(startIdx + i)) return false
            }
        }
        return true
    }

    private fun applyLiquidTransfer(mode: Int): Boolean {
        if (this.craningShip == null) return false
        val transferRate = max(0, this.liquidTransferRate)
        if (transferRate <= 0) return false
        if (mode == 1) {
            if (this.fluidTank.getFluidAmount() <= 0) return false
            val toFill = this.fluidTank.getFluid().copy()
            val amountBefore = min(transferRate, toFill.getAmount())
            toFill.setAmount(amountBefore)
            if (tryFillContainer(this.craningShip!!.inventory, toFill)) {
                val filled = amountBefore - toFill.getAmount()
                if (filled > 0) {
                    this.fluidTank.drain(filled, IFluidHandler.FluidAction.EXECUTE)
                    return true
                }
            }
        } else if (mode == 2) {
            val maxDrain = min(transferRate, this.fluidTank.getCapacity() - this.fluidTank.getFluidAmount())
            if (maxDrain <= 0) return false
            val drained = tryDrainContainer(this.craningShip!!.inventory, this.fluidTank.getFluid(), maxDrain)
            if (!drained.isEmpty()) {
                this.fluidTank.fill(drained, IFluidHandler.FluidAction.EXECUTE)
                return true
            }
        }
        return false
    }

    private fun applyEnergyTransfer(): Boolean {
        if (this.craningShip == null) {
            return false
        }

        val transferRate = calculateEnergyTransferRate(this.craningShip!!)
        if (transferRate <= 0) {
            return false
        }

        if (this.modeEnergy == 1) {
            var moved = pullEnergyFromChest(transferRate)
            moved = moved or transferEnergyToShip(this.craningShip!!.inventory!!, transferRate)
            if (moved) {
                markChestForSync()
            }
            return moved
        } else if (this.modeEnergy == 2) {
            var moved = extractEnergyFromShip(this.craningShip!!.inventory!!, transferRate)
            moved = moved or pushEnergyToChest(transferRate)
            if (moved) {
                markChestForSync()
            }
            return moved
        }

        return false
    }

    private fun checkCraneEnding() {
        if (this.craningShip == null) return
        var stop = false
        if (this.craneMode == 0) {
            stop = true
        } else if (this.craneMode == 1) {
            stop = this.isWaitModeFull
        } else if (this.craneMode == 2) {
            stop = this.isWaitModeEmpty
        } else if (this.craneMode == 3) {
            stop = this.isInventoryExcess
        } else if (this.craneMode == 4) {
            stop = this.isInventoryRemain
        } else {
            stop = this.craningShip!!.getStateTimer(1) >= getWaitTime(this.craneMode)
        }

        if (stop) {
            if (this.modeRedstone == 2) {
                this.tickRedstone = 2
                setRedstoneSignal(true)
            }
            this.craningShip!!.setStateMinor(43, 0)
            this.craningShip!!.setStateTimer(1, 0)
            this.craningShip = null
            markForSync()
        }
    }

    private val isWaitModeFull: Boolean
        get() {
            if (this.enabLoad && !isInventoryFull(this.craningShip!!.inventory!!)) {
                return false
            }
            if (this.enabUnload && !isInventoryFull(this.combinedChestHandler!!)) {
                return false
            }
            if (this.modeLiquid == 1 && !checkInventoryFluidContainer(
                    this.craningShip!!.inventory,
                    this.fluidTank.getFluid(),
                    true
                )
            ) {
                return false
            }
            if (this.modeLiquid == 2 && !isChestFluidContainersFull(this.fluidTank.getFluid())) {
                return false
            }
            return true
        }

    private val isWaitModeEmpty: Boolean
        get() {
            if (this.enabLoad && !isInventoryEmpty(this.combinedChestHandler!!)) {
                return false
            }
            if (this.enabUnload && !isInventoryEmpty(this.craningShip!!.inventory!!)) {
                return false
            }
            if (this.modeLiquid == 1 && !checkInventoryFluidContainer(
                    this.chestHandler,
                    this.fluidTank.getFluid(),
                    false
                )
            ) {
                return false
            }
            if (this.modeLiquid == 1 && !isChestFluidContainersEmpty(this.fluidTank.getFluid())) {
                return false
            }
            if (this.modeLiquid == 2 && !checkInventoryFluidContainer(
                    this.craningShip!!.inventory,
                    this.fluidTank.getFluid(),
                    false
                )
            ) {
                return false
            }
            return true
        }

    private val isInventoryExcess: Boolean
        get() {
            if (this.enabLoad && !matchesRequestedAmounts(this.craningShip!!.inventory, 0, true)) {
                return false
            }
            if (this.enabUnload && !matchesRequestedAmounts(this.combinedChestHandler, 9, true)) {
                return false
            }
            return true
        }

    private val isInventoryRemain: Boolean
        get() {
            if (this.enabLoad && !matchesRequestedAmounts(this.combinedChestHandler, 0, false)) {
                return false
            }
            if (this.enabUnload && !matchesRequestedAmounts(this.craningShip!!.inventory, 9, false)) {
                return false
            }
            return true
        }

    private fun matchesRequestedAmounts(target: IItemHandler?, filterStart: Int, atLeast: Boolean): Boolean {
        if (target == null) {
            return true
        }

        var foundNormalFilter = false
        for (i in 0..8) {
            val filter = this.inventory.getStackInSlot(filterStart + i)
            if (!filter.isEmpty() && !getItemMode(filterStart + i)) {
                foundNormalFilter = true
                val current = calcItemStackAmount(target, filter, this.checkMetadata, this.checkNbt, this.checkOredict)
                if (atLeast) {
                    if (current < filter.getCount()) {
                        return false
                    }
                } else if (current > filter.getCount()) {
                    return false
                }
            }
        }

        return foundNormalFilter
    }

    private fun isInventoryFull(inv: IItemHandler): Boolean {
        for (i in 0..<inv.getSlots()) {
            if (inv.getStackInSlot(i).isEmpty() || inv.getStackInSlot(i).getCount() < inv.getSlotLimit(i)) return false
        }
        return true
    }

    private fun isInventoryEmpty(inv: IItemHandler): Boolean {
        for (i in 0..<inv.getSlots()) {
            if (!inv.getStackInSlot(i).isEmpty()) return false
        }
        return true
    }

    private fun setRedstoneSignal(power: Boolean) {
        if (this.level != null && !this.level!!.isClientSide) {
            val state = this.level!!.getBlockState(this.worldPosition)
            if (state.hasProperty<Boolean?>(CraneBlock.Companion.POWERED) && state.getValue<Boolean?>(CraneBlock.Companion.POWERED) != power) {
                this.level!!.setBlock(
                    this.worldPosition,
                    state.setValue<Boolean?, Boolean?>(CraneBlock.Companion.POWERED, power),
                    3
                )
            }
        }
    }

    private fun returnRemainderToSourceOrDrop(invFrom: IItemHandler?, remainder: ItemStack) {
        if (remainder.isEmpty()) return
        moveItemstackToInv(invFrom, remainder, null)
        val lvl = this.level
        if (!remainder.isEmpty() && lvl is ServerLevel) {
            val drop = ItemEntity(
                lvl,
                this.worldPosition.getX() + 0.5,
                this.worldPosition.getY() + 1.0,
                this.worldPosition.getZ() + 0.5,
                remainder.copy()
            )
            drop.setDefaultPickUpDelay()
            lvl.addFreshEntity(drop)
            remainder.setCount(0)
        }
    }

    private fun markChestForSync() {
        if (this.chestPos != null && this.level != null && !this.level!!.isClientSide) {
            val be = this.level!!.getBlockEntity(this.chestPos)
            if (be != null) {
                be.setChanged()
                this.level!!.sendBlockUpdated(this.chestPos, be.getBlockState(), be.getBlockState(), 3)
            }
        }
    }

    private val adjacentChestHandler: IItemHandler?
        get() {
            if (this.level == null || this.chestPos === BlockPos.ZERO) {
                return null
            }

            for (direction in Direction.Plane.HORIZONTAL) {
                val adjacentPos = this.chestPos!!.relative(direction)
                val adjacentState = this.level!!.getBlockState(adjacentPos)
                if (!adjacentState.`is`(this.level!!.getBlockState(this.chestPos).getBlock())) {
                    continue
                }

                return this.level!!.getCapability<IItemHandler?, Direction?>(
                    Capabilities.ItemHandler.BLOCK,
                    adjacentPos,
                    null
                )
            }

            return null
        }

    private fun createCombinedChestHandler(primary: IItemHandler): IItemHandler? {
        val adjacent = this.adjacentChestHandler
        if (adjacent == null) {
            return primary
        }
        return CombinedItemHandler(primary, adjacent)
    }

    private fun drainFromChestContainers(targetFluid: FluidStack?, maxDrain: Int): FluidStack {
        val drained = tryDrainContainer(this.chestHandler, targetFluid, maxDrain)
        if (!drained.isEmpty()) {
            return drained
        }

        val adjacent = this.adjacentChestHandler
        if (adjacent == null) {
            return FluidStack.EMPTY
        }

        return tryDrainContainer(adjacent, targetFluid, maxDrain)
    }

    private fun fillChestContainers(fluid: FluidStack): Boolean {
        if (fluid.isEmpty()) {
            return false
        }

        var moved = tryFillContainer(this.chestHandler, fluid)
        if (!fluid.isEmpty()) {
            val adjacent = this.adjacentChestHandler
            if (adjacent != null) {
                moved = moved or tryFillContainer(adjacent, fluid)
            }
        }
        return moved
    }

    private fun isChestFluidContainersFull(targetFluid: FluidStack?): Boolean {
        if (!checkInventoryFluidContainer(this.chestHandler, targetFluid, true)) {
            return false
        }
        val adjacent = this.adjacentChestHandler
        return adjacent == null || checkInventoryFluidContainer(adjacent, targetFluid, true)
    }

    private fun isChestFluidContainersEmpty(targetFluid: FluidStack?): Boolean {
        if (!checkInventoryFluidContainer(this.chestHandler, targetFluid, false)) {
            return false
        }
        val adjacent = this.adjacentChestHandler
        return adjacent == null || checkInventoryFluidContainer(adjacent, targetFluid, false)
    }

    private fun calculateLiquidTransferRate(ship: EntityShipBase): Int {
        var drumCount = 0
        var enchantCount = 0
        val equipSlots = min(6, ship.inventory!!.getSlots())

        if (ship is EntityTransportWa && ship.isStateMarried) {
            drumCount = 1
        }

        for (slot in 0..<equipSlots) {
            val stack = ship.inventory.getStackInSlot(slot)
            if (stack.isEmpty() || stack.getItem() !is LegacyEquipItem) {
                continue
            }

            if ((stack.getItem() as LegacyEquipItem).getEquipTypeId(stack) != 24 || (stack.getItem() as LegacyEquipItem).getVariant(stack) != 1) {
                continue
            }

            drumCount++
            enchantCount += EnchantmentHelper.getEnchantmentsForCrafting(stack).size()
        }

        val perTickRate = (drumCount * max(0, Config.drumLiquidBaseRate)
                + enchantCount * max(0, Config.drumLiquidEnchantRate))
        if (perTickRate <= 0) {
            return 0
        }

        val shipLevelMultiplier = (ship.level * 0.1f).toInt() + 1
        return perTickRate * 16 * max(1, shipLevelMultiplier)
    }

    private fun calculateEnergyTransferRate(ship: EntityShipBase): Int {
        var drumCount = 0
        var enchantCount = 0
        val equipSlots = min(6, ship.inventory!!.getSlots())

        if (ship is EntityTransportWa && ship.isStateMarried) {
            drumCount = 1
        }

        for (slot in 0..<equipSlots) {
            val stack = ship.inventory.getStackInSlot(slot)
            if (stack.isEmpty() || stack.getItem() !is LegacyEquipItem) {
                continue
            }

            if ((stack.getItem() as LegacyEquipItem).getEquipTypeId(stack) != 24 || (stack.getItem() as LegacyEquipItem).getVariant(stack) != 2) {
                continue
            }

            drumCount++
            enchantCount += EnchantmentHelper.getEnchantmentsForCrafting(stack).size()
        }

        val perTickRate = (drumCount * max(0, Config.drumEnergyBaseRate)
                + enchantCount * max(0, Config.drumEnergyEnchantRate))
        if (perTickRate <= 0) {
            return 0
        }

        val shipLevelMultiplier = (ship.level * 0.1f).toInt() + 1
        return perTickRate * 16 * max(1, shipLevelMultiplier)
    }

    private fun transferEnergyToShip(shipInventory: IItemHandler, maxTransfer: Int): Boolean {
        val available = min(maxTransfer, this.remainedPower)
        if (available <= 0) {
            return false
        }

        for (slot in 0..<shipInventory.getSlots()) {
            val stack = shipInventory.getStackInSlot(slot)
            if (stack.isEmpty()) {
                continue
            }

            val energy = stack.getCapability<IEnergyStorage?>(Capabilities.EnergyStorage.ITEM)
            if (energy == null || !energy.canReceive()) {
                continue
            }

            val accepted = energy.receiveEnergy(available, false)
            if (accepted > 0) {
                this.remainedPower = max(0, this.remainedPower - accepted)
                markForSync()
                return true
            }
        }

        return false
    }

    private fun extractEnergyFromShip(shipInventory: IItemHandler, maxTransfer: Int): Boolean {
        val capacityLeft = max(0, this.powerMax - this.remainedPower)
        val allowed = min(maxTransfer, capacityLeft)
        if (allowed <= 0) {
            return false
        }

        for (slot in 0..<shipInventory.getSlots()) {
            val stack = shipInventory.getStackInSlot(slot)
            if (stack.isEmpty()) {
                continue
            }

            val energy = stack.getCapability<IEnergyStorage?>(Capabilities.EnergyStorage.ITEM)
            if (energy == null || !energy.canExtract()) {
                continue
            }

            val extracted = energy.extractEnergy(allowed, false)
            if (extracted > 0) {
                this.remainedPower = min(this.powerMax, this.remainedPower + extracted)
                markForSync()
                return true
            }
        }

        return false
    }

    private fun pullEnergyFromChest(maxTransfer: Int): Boolean {
        val capacityLeft = max(0, this.powerMax - this.remainedPower)
        val allowed = min(maxTransfer, capacityLeft)
        if (allowed <= 0 || this.chestHandler == null) {
            return false
        }

        var moved: Int = Companion.extractEnergyFromInventory(this.chestHandler!!, allowed)
        if (moved > 0) {
            this.remainedPower = min(this.powerMax, this.remainedPower + moved)
            markForSync()
            return true
        }

        val adjacent = this.adjacentChestHandler
        if (adjacent == null) {
            return false
        }

        moved = extractEnergyFromInventory(adjacent, allowed)
        if (moved > 0) {
            this.remainedPower = min(this.powerMax, this.remainedPower + moved)
            markForSync()
            return true
        }

        return false
    }

    private fun pushEnergyToChest(maxTransfer: Int): Boolean {
        val available = min(maxTransfer, this.remainedPower)
        if (available <= 0 || this.chestHandler == null) {
            return false
        }

        var moved: Int = Companion.receiveEnergyIntoInventory(this.chestHandler!!, available)
        if (moved > 0) {
            this.remainedPower = max(0, this.remainedPower - moved)
            markForSync()
            return true
        }

        val adjacent = this.adjacentChestHandler
        if (adjacent == null) {
            return false
        }

        moved = receiveEnergyIntoInventory(adjacent, available)
        if (moved > 0) {
            this.remainedPower = max(0, this.remainedPower - moved)
            markForSync()
            return true
        }

        return false
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.put("Inventory", inventory.serializeNBT(registries))
        tag.put("Tank", fluidTank.writeToNBT(registries, CompoundTag()))
        tag.putInt("Power", remainedPower)
        tag.putInt("PowerMax", powerMax)
        tag.putBoolean("IsActive", isActive)
        tag.putBoolean("CheckMetadata", checkMetadata)
        tag.putBoolean("CheckOredict", checkOredict)
        tag.putBoolean("CheckNbt", checkNbt)
        tag.putBoolean("EnabLoad", enabLoad)
        tag.putBoolean("EnabUnload", enabUnload)
        tag.putInt("CraneMode", craneMode)
        tag.putInt("ModeItem", modeItem)
        tag.putInt("ModeRedstone", modeRedstone)
        tag.putInt("ModeLiquid", modeLiquid)
        tag.putInt("ModeEnergy", modeEnergy)
        tag.putLong("LastPos", lastPos!!.asLong())
        tag.putLong("NextPos", nextPos!!.asLong())
        tag.putLong("ChestPos", chestPos!!.asLong())
        tag.putBoolean("IsPaired", isPaired)
        if (ownerUUID != null) tag.putUUID("OwnerUUID", ownerUUID)
        tag.putString("OwnerName", ownerName)
        tag.putInt("SyncedShipId", syncedShipId)
        tag.putInt("LiquidTransferRate", liquidTransferRate)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"))
        if (tag.contains("Tank")) fluidTank.readFromNBT(registries, tag.getCompound("Tank"))
        remainedPower = tag.getInt("Power")
        powerMax = tag.getInt("PowerMax")
        isActive = tag.getBoolean("IsActive")
        checkMetadata = tag.getBoolean("CheckMetadata")
        checkOredict = tag.getBoolean("CheckOredict")
        checkNbt = tag.getBoolean("CheckNbt")
        enabLoad = tag.getBoolean("EnabLoad")
        enabUnload = tag.getBoolean("EnabUnload")
        craneMode = tag.getInt("CraneMode")
        modeItem = tag.getInt("ModeItem")
        modeRedstone = tag.getInt("ModeRedstone")
        modeLiquid = tag.getInt("ModeLiquid")
        modeEnergy = tag.getInt("ModeEnergy")
        if (tag.contains("LastPos")) lastPos = BlockPos.of(tag.getLong("LastPos"))
        if (tag.contains("NextPos")) nextPos = BlockPos.of(tag.getLong("NextPos"))
        if (tag.contains("ChestPos")) chestPos = BlockPos.of(tag.getLong("ChestPos"))
        isPaired = tag.getBoolean("IsPaired")
        if (tag.hasUUID("OwnerUUID")) ownerUUID = tag.getUUID("OwnerUUID")
        ownerName = tag.getString("OwnerName")
        syncedShipId = tag.getInt("SyncedShipId")
        liquidTransferRate = tag.getInt("LiquidTransferRate")
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val tag = CompoundTag()
        saveAdditional(tag, registries)
        return tag
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("tile.shincolle.BlockCrane.name")
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu? {
        return CraneMenu(containerId, playerInventory, this)
    }

    val craningShipId: Int
        get() = if (this.craningShip == null) 0 else this.craningShip!!.getId()

    val craningShipTimer: Int
        get() = if (this.craningShip == null) 0 else this.craningShip!!.getStateTimer(1)

    fun getRemainedPower(): Int {
        return remainedPower
    }

    fun setRemainedPower(`val`: Int) {
        if (this.remainedPower == `val`) {
            return
        }
        this.remainedPower = `val`
        markForSync()
    }

    fun isActive(): Boolean {
        return isActive
    }

    fun setActive(`val`: Boolean) {
        if (this.isActive == `val`) {
            return
        }
        this.isActive = `val`
        markForSync()
    }

    fun isCheckMetadata(): Boolean {
        return checkMetadata
    }

    fun setCheckMetadata(`val`: Boolean) {
        if (this.checkMetadata == `val`) {
            return
        }
        this.checkMetadata = `val`
        markForSync()
    }

    fun isCheckOredict(): Boolean {
        return checkOredict
    }

    fun setCheckOredict(`val`: Boolean) {
        if (this.checkOredict == `val`) {
            return
        }
        this.checkOredict = `val`
        markForSync()
    }

    fun isCheckNbt(): Boolean {
        return checkNbt
    }

    fun setCheckNbt(`val`: Boolean) {
        if (this.checkNbt == `val`) {
            return
        }
        this.checkNbt = `val`
        markForSync()
    }

    fun isEnabLoad(): Boolean {
        return enabLoad
    }

    fun setEnabLoad(`val`: Boolean) {
        if (this.enabLoad == `val`) {
            return
        }
        this.enabLoad = `val`
        markForSync()
    }

    fun isEnabUnload(): Boolean {
        return enabUnload
    }

    fun setEnabUnload(`val`: Boolean) {
        if (this.enabUnload == `val`) {
            return
        }
        this.enabUnload = `val`
        markForSync()
    }

    fun getCraneMode(): Int {
        return craneMode
    }

    fun setCraneMode(`val`: Int) {
        if (this.craneMode == `val`) {
            return
        }
        this.craneMode = `val`
        markForSync()
    }

    fun getModeItem(): Int {
        return modeItem
    }

    fun setModeItem(`val`: Int) {
        if (this.modeItem == `val`) {
            return
        }
        this.modeItem = `val`
        markForSync()
    }

    fun setItemMode(id: Int, `val`: Boolean) {
        val next = if (`val`) modeItem or (1 shl id) else modeItem and (1 shl id).inv()
        if (this.modeItem == next) {
            return
        }
        modeItem = next
        markForSync()
    }

    fun getItemMode(id: Int): Boolean {
        return (modeItem and (1 shl id)) != 0
    }

    fun getModeRedstone(): Int {
        return modeRedstone
    }

    fun setModeRedstone(`val`: Int) {
        if (this.modeRedstone == `val`) {
            return
        }
        this.modeRedstone = `val`
        markForSync()
    }

    fun getModeLiquid(): Int {
        return modeLiquid
    }

    fun setModeLiquid(`val`: Int) {
        if (this.modeLiquid == `val`) {
            return
        }
        this.modeLiquid = `val`
        markForSync()
    }

    fun getModeEnergy(): Int {
        return modeEnergy
    }

    fun setModeEnergy(`val`: Int) {
        if (this.modeEnergy == `val`) {
            return
        }
        this.modeEnergy = `val`
        markForSync()
    }

    override fun showBaseParticle(): Boolean {
        return false
    }

    fun markForSync() {
        setChanged()
        if (level != null) {
            level!!.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3)
        }
    }

    private class CombinedItemHandler(private val first: IItemHandler, private val second: IItemHandler) :
        IItemHandler {
        override fun getSlots(): Int {
            return this.first.getSlots() + this.second.getSlots()
        }

        override fun getStackInSlot(slot: Int): ItemStack {
            return if (isFirst(slot)) this.first.getStackInSlot(slot) else this.second.getStackInSlot(slot - this.first.getSlots())
        }

        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
            return if (isFirst(slot)) this.first.insertItem(
                slot,
                stack,
                simulate
            ) else this.second.insertItem(slot - this.first.getSlots(), stack, simulate)
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            return if (isFirst(slot)) this.first.extractItem(
                slot,
                amount,
                simulate
            ) else this.second.extractItem(slot - this.first.getSlots(), amount, simulate)
        }

        override fun getSlotLimit(slot: Int): Int {
            return if (isFirst(slot)) this.first.getSlotLimit(slot) else this.second.getSlotLimit(slot - this.first.getSlots())
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return if (isFirst(slot)) this.first.isItemValid(
                slot,
                stack
            ) else this.second.isItemValid(slot - this.first.getSlots(), stack)
        }

        fun isFirst(slot: Int): Boolean {
            return slot < this.first.getSlots()
        }
    }

    companion object {
        fun tick(level: Level, pos: BlockPos?, state: BlockState?, be: CraneBlockEntity) {
            be.tickCount++
            if (!level.isClientSide) {
                val tracing = enabled()
                val start = if (tracing) now() else 0L
                try {
                    be.serverTick()
                } finally {
                    if (tracing) {
                        val elapsed = elapsed(start)
                        addBlockEntityTime(elapsed)
                        logSlowBlockEntityTick(
                            be, "crane", elapsed,
                            ("active=" + be.isActive
                                    + " paired=" + be.isPaired
                                    + " shipId=" + be.syncedShipId
                                    + " modeItem=" + be.modeItem
                                    + " modeLiquid=" + be.modeLiquid
                                    + " modeEnergy=" + be.modeEnergy)
                        )
                    }
                }
            } else {
                be.clientTick()
            }
        }

        private fun getWaitTime(mode: Int): Int {
            if (mode >= 5 && mode <= 9) {
                return (mode - 4) * 16
            }
            if (mode >= 10 && mode <= 14) {
                return (mode - 9) * 20 * 5
            }
            if (mode >= 15 && mode <= 19) {
                return (mode - 14) * 20 * 60
            }
            if (mode >= 20 && mode <= 24) {
                return (mode - 19) * 20 * 60 * 10
            }
            return 0
        }

        private fun extractEnergyFromInventory(inventory: IItemHandler, maxTransfer: Int): Int {
            for (slot in 0..<inventory.getSlots()) {
                val stack = inventory.getStackInSlot(slot)
                if (stack.isEmpty()) {
                    continue
                }

                val energy = stack.getCapability<IEnergyStorage?>(Capabilities.EnergyStorage.ITEM)
                if (energy == null || !energy.canExtract()) {
                    continue
                }

                val extracted = energy.extractEnergy(maxTransfer, false)
                if (extracted > 0) {
                    return extracted
                }
            }

            return 0
        }

        private fun receiveEnergyIntoInventory(inventory: IItemHandler, maxTransfer: Int): Int {
            for (slot in 0..<inventory.getSlots()) {
                val stack = inventory.getStackInSlot(slot)
                if (stack.isEmpty()) {
                    continue
                }

                val energy = stack.getCapability<IEnergyStorage?>(Capabilities.EnergyStorage.ITEM)
                if (energy == null || !energy.canReceive()) {
                    continue
                }

                val accepted = energy.receiveEnergy(maxTransfer, false)
                if (accepted > 0) {
                    return accepted
                }
            }

            return 0
        }
    }
}
