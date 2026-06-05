package org.trp.shincolle.utility

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidUtil
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemHandlerHelper
import net.neoforged.neoforge.items.wrapper.InvWrapper
import kotlin.math.min

object InventoryHelper {
    fun checkInventoryAmount(
        inv: IItemHandler?,
        tempStacks: Array<ItemStack>?,
        modeStacks: BooleanArray,
        checkMetadata: Boolean,
        checkNbt: Boolean,
        checkOredict: Boolean,
        excess: Boolean
    ): Boolean {
        if (inv == null || tempStacks == null || tempStacks.size != 9) {
            return true
        }
        var hasTemplate = false
        for (stack in tempStacks) {
            if (!stack.isEmpty()) {
                hasTemplate = true
                break
            }
        }
        if (!hasTemplate) {
            return true
        }

        for (i in 0..8) {
            if (!tempStacks[i].isEmpty() && !modeStacks[i]) {
                val currentAmount = calcItemStackAmount(inv, tempStacks[i], checkMetadata, checkNbt, checkOredict)
                if (excess) {
                    if (currentAmount < tempStacks[i].getCount()) {
                        return false
                    }
                } else {
                    if (currentAmount > tempStacks[i].getCount()) {
                        return false
                    }
                }
            }
        }
        return true
    }

    @JvmStatic
    fun calcItemStackAmount(
        inv: IItemHandler,
        temp: ItemStack,
        checkMetadata: Boolean,
        checkNbt: Boolean,
        checkOredict: Boolean
    ): Int {
        var targetAmount = 0
        for (i in 0..<inv.getSlots()) {
            val stack = inv.getStackInSlot(i)
            if (matchTargetItem(stack, temp, checkMetadata, checkNbt, checkOredict)) {
                targetAmount += stack.getCount()
            }
        }
        return targetAmount
    }

    @JvmStatic
    fun matchTargetItem(
        target: ItemStack,
        temp: ItemStack,
        checkMetadata: Boolean,
        checkNbt: Boolean,
        checkOredict: Boolean
    ): Boolean {
        if (temp.isEmpty() || target.isEmpty()) {
            return false
        }
        if (target.getItem() === temp.getItem()) {
            if (checkMetadata && target.getDamageValue() != temp.getDamageValue()) {
                return false
            }


            if (checkNbt && !ItemStack.isSameItemSameComponents(target, temp)) {
                return false
            }
            return true
        }

        if (checkOredict && sharesMatchableTag(target, temp)) {
            return true
        }

        return false
    }

    private fun sharesMatchableTag(first: ItemStack, second: ItemStack): Boolean {
        val firstTags = first.getTags().toList()
        if (firstTags.isEmpty()) {
            return false
        }

        for (firstTag in firstTags) {
            if (!isLooseMatchingTag(firstTag.location().getPath())) {
                continue
            }
            if (second.`is`(firstTag)) {
                return true
            }
        }
        return false
    }

    private fun isLooseMatchingTag(path: String): Boolean {
        return path.startsWith("c/")
                || path.startsWith("forge/")
                || path.startsWith("ores/")
                || path.startsWith("ingots/")
                || path.startsWith("nuggets/")
                || path.startsWith("storage_blocks/")
                || path.startsWith("dusts/")
                || path.startsWith("gems/")
                || path.startsWith("plates/")
                || path.startsWith("rods/")
    }

    fun getItemMode(slotID: Int, stackMode: Int): Boolean {
        return ((stackMode shr slotID) and 1) == 1
    }

    fun setItemMode(slotID: Int, stackMode: Int, notMode: Boolean): Int {
        var stackMode = stackMode
        val slot = 1 shl slotID
        if (notMode) {
            stackMode = stackMode or slot
        } else {
            stackMode = stackMode and slot.inv()
        }
        return stackMode
    }

    fun getSlotsFromSide(handler: IItemHandler?): IntArray {
        if (handler == null) return IntArray(0)
        val slots = IntArray(handler.getSlots())
        for (i in slots.indices) slots[i] = i
        return slots
    }

    @JvmStatic
    fun getHandlersFromSide(level: Level, pos: BlockPos, taskSide: Int, type: Int): MutableList<IItemHandler> {
        val handlers: MutableList<IItemHandler> = ArrayList<IItemHandler>()
        val padbit = type * 6
        for (i in 0..5) {
            val tarbit = i + padbit
            if (((taskSide shr tarbit) and 1) == 1) {
                val face = Direction.from3DDataValue(i)
                val handler = level.getCapability<IItemHandler?, Direction?>(Capabilities.ItemHandler.BLOCK, pos, face)
                if (handler != null) {
                    handlers.add(handler)
                }
            }
        }
        return handlers
    }

    @JvmStatic
    fun getAndRemoveItem(
        inv: IItemHandler,
        temp: ItemStack,
        number: Int,
        checkMetadata: Boolean,
        checkNbt: Boolean,
        checkOredict: Boolean,
        exceptSlots: IntArray?
    ): ItemStack {
        if (temp.isEmpty() || number <= 0) return ItemStack.EMPTY

        var numToGet = min(number, temp.getMaxStackSize())
        var resultStack = ItemStack.EMPTY

        var i = 0
        while (i < inv.getSlots() && numToGet > 0) {
            val inSlot = inv.getStackInSlot(i)
            if (isNotInArray(i, exceptSlots) && matchTargetItem(inSlot, temp, checkMetadata, checkNbt, checkOredict)) {
                val extracted = inv.extractItem(i, numToGet, false)
                if (!extracted.isEmpty()) {
                    if (resultStack.isEmpty()) {
                        resultStack = extracted.copy()
                    } else {
                        resultStack.grow(extracted.getCount())
                    }
                    numToGet -= extracted.getCount()
                }
            }
            i++
        }
        return resultStack
    }

    private fun isNotInArray(target: Int, array: IntArray?): Boolean {
        if (array == null) return true
        for (i in array) if (i == target) return false
        return true
    }

    @JvmStatic
    fun moveItemstackToInv(inv: IItemHandler?, moveitem: ItemStack, toSlots: IntArray?): Boolean {
        if (moveitem.isEmpty() || inv == null) {
            return false
        }


        if (inv is InvWrapper) {
            return moveToContainer(inv.getInv(), moveitem, toSlots)
        }

        val initialCount = moveitem.getCount()


        var i = 0
        while (i < inv.getSlots() && !moveitem.isEmpty()) {
            if (toSlots != null && isNotInArray(i, toSlots)) {
                i++
                continue
            }

            val inSlot = inv.getStackInSlot(i)
            if (!inSlot.isEmpty()) {
                if (!ItemStack.isSameItemSameComponents(inSlot, moveitem)) {
                    i++
                    continue
                }
                if (inSlot.getCount() >= inSlot.getMaxStackSize()) {
                    i++
                    continue
                }
            }

            val remaining = inv.insertItem(i, moveitem.copy(), false)
            val moved = moveitem.getCount() - remaining.getCount()
            if (moved > 0) {
                moveitem.shrink(moved)
            }
            i++
        }

        return moveitem.getCount() < initialCount
    }

    private fun moveToContainer(inv: Container?, moveitem: ItemStack, toSlots: IntArray?): Boolean {
        if (moveitem.isEmpty() || inv == null) return false
        val initialCount = moveitem.getCount()


        run {
            var i = 0
            while (i < inv.getContainerSize() && !moveitem.isEmpty()) {
                if (toSlots != null && InventoryHelper.isNotInArray(i, toSlots)) {
                    i++
                    continue
                }
                val inSlot = inv.getItem(i)
                if (!inSlot.isEmpty() && ItemStack.isSameItemSameComponents(inSlot, moveitem)) {
                    val add = min(moveitem.getCount(), inSlot.getMaxStackSize() - inSlot.getCount())
                    if (add > 0) {
                        inSlot.grow(add)
                        moveitem.shrink(add)
                        inv.setChanged()
                    }
                }
                i++
            }
        }


        var i = 0
        while (i < inv.getContainerSize() && !moveitem.isEmpty()) {
            if (toSlots != null && isNotInArray(i, toSlots)) {
                i++
                continue
            }
            val inSlot = inv.getItem(i)
            if (inSlot.isEmpty() && inv.canPlaceItem(i, moveitem)) {
                val add = min(moveitem.getCount(), moveitem.getMaxStackSize())
                val newStack = moveitem.copy()
                newStack.setCount(add)
                inv.setItem(i, newStack)
                moveitem.shrink(add)
                inv.setChanged()
            }
            i++
        }

        return moveitem.getCount() < initialCount
    }

    @JvmStatic
    fun tryFillContainer(inv: IItemHandler?, fs: FluidStack?): Boolean {
        if (inv == null || fs == null || fs.getAmount() <= 0) return false

        var amountMovedTotal = 0
        var i = 0
        while (i < inv.getSlots() && fs.getAmount() > 0) {
            val stack = inv.getStackInSlot(i)
            if (stack.isEmpty()) {
                i++
                continue
            }

            val single = stack.copy()
            single.setCount(1)

            val handler = FluidUtil.getFluidHandler(single)
            if (handler.isPresent()) {
                val fh = handler.orElseThrow()
                val filled = fh.fill(fs, IFluidHandler.FluidAction.EXECUTE)
                if (filled > 0) {
                    val result = fh.getContainer()
                    inv.extractItem(i, 1, false)
                    val remaining = ItemHandlerHelper.insertItemStacked(inv, result, false)
                    if (!remaining.isEmpty()) {
                    }
                    amountMovedTotal += filled
                    fs.setAmount(fs.getAmount() - filled)
                    i--
                }
            }
            i++
        }
        return amountMovedTotal > 0
    }

    @JvmStatic
    fun tryDrainContainer(inv: IItemHandler?, targetFluid: FluidStack?, maxDrain: Int): FluidStack {
        if (inv == null || maxDrain <= 0) return FluidStack.EMPTY

        var drainedTotal = FluidStack.EMPTY
        var remainingDrain = maxDrain

        var i = 0
        while (i < inv.getSlots() && remainingDrain > 0) {
            val stack = inv.getStackInSlot(i)
            if (stack.isEmpty()) {
                i++
                continue
            }

            val single = stack.copy()
            single.setCount(1)

            val handler = FluidUtil.getFluidHandler(single)
            if (handler.isPresent()) {
                val fh = handler.orElseThrow()
                val drained: FluidStack?
                if (targetFluid != null && !targetFluid.isEmpty()) {
                    drained =
                        fh.drain(FluidStack(targetFluid.getFluid(), remainingDrain), IFluidHandler.FluidAction.EXECUTE)
                } else {
                    drained = fh.drain(remainingDrain, IFluidHandler.FluidAction.EXECUTE)
                }

                if (drained != null && !drained.isEmpty()) {
                    val result = fh.getContainer()
                    inv.extractItem(i, 1, false)
                    ItemHandlerHelper.insertItemStacked(inv, result, false)

                    if (drainedTotal.isEmpty()) {
                        drainedTotal = drained.copy()
                    } else {
                        drainedTotal.grow(drained.getAmount())
                    }
                    remainingDrain -= drained.getAmount()
                    i--
                }
            }
            i++
        }
        return drainedTotal
    }

    @JvmStatic
    fun checkInventoryFluidContainer(inv: IItemHandler?, targetFluid: FluidStack?, checkFull: Boolean): Boolean {
        if (inv == null) {
            return true
        }

        val startSlot = getFluidContainerStartSlot(inv)
        for (i in startSlot..<inv.getSlots()) {
            if (!checkFluidContainer(inv.getStackInSlot(i), targetFluid, checkFull)) {
                return false
            }
        }

        return true
    }

    fun checkFluidContainer(stack: ItemStack, targetFluid: FluidStack?, checkFull: Boolean): Boolean {
        if (stack.isEmpty()) {
            return true
        }

        val handler = FluidUtil.getFluidHandler(stack.copyWithCount(1))
        if (handler.isEmpty()) {
            return true
        }

        val fluidHandler = handler.orElseThrow()
        if (checkFull) {
            val probe = if (targetFluid == null || targetFluid.isEmpty())
                FluidStack(Fluids.WATER, 1)
            else
                targetFluid.copyWithAmount(1)
            return fluidHandler.fill(probe, IFluidHandler.FluidAction.SIMULATE) <= 0
        }

        if (targetFluid != null && !targetFluid.isEmpty()) {
            val drained = fluidHandler.drain(FluidStack(targetFluid.getFluid(), 1), IFluidHandler.FluidAction.SIMULATE)
            return drained.isEmpty()
        }

        val drained = fluidHandler.drain(1, IFluidHandler.FluidAction.SIMULATE)
        return drained.isEmpty()
    }

    private fun getFluidContainerStartSlot(inv: IItemHandler): Int {
        val handlerName = inv.javaClass.getName()
        if (handlerName.endsWith("ShipInventoryHandler")) {
            return 6
        }
        return 0
    }
}
