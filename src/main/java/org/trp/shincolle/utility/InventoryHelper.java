package org.trp.shincolle.utility;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class InventoryHelper {

    private InventoryHelper() {}

    
    public static boolean checkInventoryAmount(IItemHandler inv, ItemStack[] tempStacks, boolean[] modeStacks, boolean checkMetadata, boolean checkNbt, boolean checkOredict, boolean excess) {
        if (inv == null || tempStacks == null || tempStacks.length != 9) {
            return true;
        }
        boolean hasTemplate = false;
        for (ItemStack stack : tempStacks) {
            if (!stack.isEmpty()) {
                hasTemplate = true;
                break;
            }
        }
        if (!hasTemplate) {
            return true;
        }

        for (int i = 0; i < 9; ++i) {
            if (!tempStacks[i].isEmpty() && !modeStacks[i]) {
                int currentAmount = calcItemStackAmount(inv, tempStacks[i], checkMetadata, checkNbt, checkOredict);
                if (excess) {
                    if (currentAmount < tempStacks[i].getCount()) {
                        return false;
                    }
                } else {
                    if (currentAmount > tempStacks[i].getCount()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static int calcItemStackAmount(IItemHandler inv, ItemStack temp, boolean checkMetadata, boolean checkNbt, boolean checkOredict) {
        int targetAmount = 0;
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (matchTargetItem(stack, temp, checkMetadata, checkNbt, checkOredict)) {
                targetAmount += stack.getCount();
            }
        }
        return targetAmount;
    }

    public static boolean matchTargetItem(ItemStack target, ItemStack temp, boolean checkMetadata, boolean checkNbt, boolean checkOredict) {
        if (temp.isEmpty() || target.isEmpty()) {
            return false;
        }
        if (target.getItem() == temp.getItem()) {
            if (checkMetadata && target.getDamageValue() != temp.getDamageValue()) {
                return false;
            }
            
            
            if (checkNbt && !ItemStack.isSameItemSameComponents(target, temp)) {
                return false;
            }
            return true;
        }

        if (checkOredict && sharesMatchableTag(target, temp)) {
            return true;
        }

        return false;
    }

    private static boolean sharesMatchableTag(ItemStack first, ItemStack second) {
        var firstTags = first.getTags().toList();
        if (firstTags.isEmpty()) {
            return false;
        }

        for (var firstTag : firstTags) {
            if (!isLooseMatchingTag(firstTag.location().getPath())) {
                continue;
            }
            if (second.is(firstTag)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isLooseMatchingTag(String path) {
        return path.startsWith("c/")
                || path.startsWith("forge/")
                || path.startsWith("ores/")
                || path.startsWith("ingots/")
                || path.startsWith("nuggets/")
                || path.startsWith("storage_blocks/")
                || path.startsWith("dusts/")
                || path.startsWith("gems/")
                || path.startsWith("plates/")
                || path.startsWith("rods/");
    }

    public static boolean getItemMode(int slotID, int stackMode) {
        return ((stackMode >> slotID) & 1) == 1;
    }

    public static int setItemMode(int slotID, int stackMode, boolean notMode) {
        int slot = 1 << slotID;
        if (notMode) {
            stackMode |= slot;
        } else {
            stackMode &= ~slot;
        }
        return stackMode;
    }

    public static int[] getSlotsFromSide(IItemHandler handler) {
        if (handler == null) return new int[0];
        int[] slots = new int[handler.getSlots()];
        for (int i = 0; i < slots.length; i++) slots[i] = i;
        return slots;
    }

    public static java.util.List<IItemHandler> getHandlersFromSide(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, int taskSide, int type) {
        java.util.List<IItemHandler> handlers = new java.util.ArrayList<>();
        int padbit = type * 6;
        for (int i = 0; i < 6; i++) {
            int tarbit = i + padbit;
            if (((taskSide >> tarbit) & 1) == 1) {
                net.minecraft.core.Direction face = net.minecraft.core.Direction.from3DDataValue(i);
                IItemHandler handler = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, pos, face);
                if (handler != null) {
                    handlers.add(handler);
                }
            }
        }
        return handlers;
    }

    public static ItemStack getAndRemoveItem(IItemHandler inv, ItemStack temp, int number, boolean checkMetadata, boolean checkNbt, boolean checkOredict, int[] exceptSlots) {
        if (temp.isEmpty() || number <= 0) return ItemStack.EMPTY;
        
        int numToGet = Math.min(number, temp.getMaxStackSize());
        ItemStack resultStack = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSlots() && numToGet > 0; i++) {
            ItemStack inSlot = inv.getStackInSlot(i);
            if (isNotInArray(i, exceptSlots) && matchTargetItem(inSlot, temp, checkMetadata, checkNbt, checkOredict)) {
                ItemStack extracted = inv.extractItem(i, numToGet, false);
                if (!extracted.isEmpty()) {
                    if (resultStack.isEmpty()) {
                        resultStack = extracted.copy();
                    } else {
                        resultStack.grow(extracted.getCount());
                    }
                    numToGet -= extracted.getCount();
                }
            }
        }
        return resultStack;
    }

    private static boolean isNotInArray(int target, int[] array) {
        if (array == null) return true;
        for (int i : array) if (i == target) return false;
        return true;
    }

    public static boolean moveItemstackToInv(IItemHandler inv, ItemStack moveitem, int[] toSlots) {
        if (moveitem.isEmpty() || inv == null) {
            return false;
        }
        
        
        if (inv instanceof net.neoforged.neoforge.items.wrapper.InvWrapper wrapper) {
            return moveToContainer(wrapper.getInv(), moveitem, toSlots);
        }
        
        int initialCount = moveitem.getCount();
        
        
        for (int i = 0; i < inv.getSlots() && !moveitem.isEmpty(); i++) {
            if (toSlots != null && isNotInArray(i, toSlots)) continue;
            
            ItemStack inSlot = inv.getStackInSlot(i);
            if (!inSlot.isEmpty()) {
                if (!ItemStack.isSameItemSameComponents(inSlot, moveitem)) continue;
                if (inSlot.getCount() >= inSlot.getMaxStackSize()) continue;
            }
            
            ItemStack remaining = inv.insertItem(i, moveitem.copy(), false);
            int moved = moveitem.getCount() - remaining.getCount();
            if (moved > 0) {
                moveitem.shrink(moved);
            }
        }
        
        return moveitem.getCount() < initialCount;
    }

    private static boolean moveToContainer(net.minecraft.world.Container inv, ItemStack moveitem, int[] toSlots) {
        if (moveitem.isEmpty() || inv == null) return false;
        int initialCount = moveitem.getCount();
        
        
        for (int i = 0; i < inv.getContainerSize() && !moveitem.isEmpty(); i++) {
            if (toSlots != null && isNotInArray(i, toSlots)) continue;
            ItemStack inSlot = inv.getItem(i);
            if (!inSlot.isEmpty() && ItemStack.isSameItemSameComponents(inSlot, moveitem)) {
                int add = Math.min(moveitem.getCount(), inSlot.getMaxStackSize() - inSlot.getCount());
                if (add > 0) {
                    inSlot.grow(add);
                    moveitem.shrink(add);
                    inv.setChanged();
                }
            }
        }
        
        
        for (int i = 0; i < inv.getContainerSize() && !moveitem.isEmpty(); i++) {
            if (toSlots != null && isNotInArray(i, toSlots)) continue;
            ItemStack inSlot = inv.getItem(i);
            if (inSlot.isEmpty() && inv.canPlaceItem(i, moveitem)) {
                int add = Math.min(moveitem.getCount(), moveitem.getMaxStackSize());
                ItemStack newStack = moveitem.copy();
                newStack.setCount(add);
                inv.setItem(i, newStack);
                moveitem.shrink(add);
                inv.setChanged();
            }
        }
        
        return moveitem.getCount() < initialCount;
    }

    public static boolean tryFillContainer(IItemHandler inv, FluidStack fs) {
        if (inv == null || fs == null || fs.getAmount() <= 0) return false;
        
        int amountMovedTotal = 0;
        for (int i = 0; i < inv.getSlots() && fs.getAmount() > 0; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            
            ItemStack single = stack.copy();
            single.setCount(1);
            
            var handler = FluidUtil.getFluidHandler(single);
            if (handler.isPresent()) {
                IFluidHandlerItem fh = handler.orElseThrow();
                int filled = fh.fill(fs, IFluidHandler.FluidAction.EXECUTE);
                if (filled > 0) {
                    ItemStack result = fh.getContainer();
                    inv.extractItem(i, 1, false);
                    ItemStack remaining = ItemHandlerHelper.insertItemStacked(inv, result, false);
                    if (!remaining.isEmpty()) {
                        
                        
                    }
                    amountMovedTotal += filled;
                    fs.setAmount(fs.getAmount() - filled);
                    i--; 
                }
            }
        }
        return amountMovedTotal > 0;
    }

    public static FluidStack tryDrainContainer(IItemHandler inv, FluidStack targetFluid, int maxDrain) {
        if (inv == null || maxDrain <= 0) return FluidStack.EMPTY;
        
        FluidStack drainedTotal = FluidStack.EMPTY;
        int remainingDrain = maxDrain;
        
        for (int i = 0; i < inv.getSlots() && remainingDrain > 0; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            
            ItemStack single = stack.copy();
            single.setCount(1);
            
            var handler = FluidUtil.getFluidHandler(single);
            if (handler.isPresent()) {
                IFluidHandlerItem fh = handler.orElseThrow();
                FluidStack drained;
                if (targetFluid != null && !targetFluid.isEmpty()) {
                    drained = fh.drain(new FluidStack(targetFluid.getFluid(), remainingDrain), IFluidHandler.FluidAction.EXECUTE);
                } else {
                    drained = fh.drain(remainingDrain, IFluidHandler.FluidAction.EXECUTE);
                }
                
                if (drained != null && !drained.isEmpty()) {
                    ItemStack result = fh.getContainer();
                    inv.extractItem(i, 1, false);
                    ItemHandlerHelper.insertItemStacked(inv, result, false);
                    
                    if (drainedTotal.isEmpty()) {
                        drainedTotal = drained.copy();
                    } else {
                        drainedTotal.grow(drained.getAmount());
                    }
                    remainingDrain -= drained.getAmount();
                    i--; 
                }
            }
        }
        return drainedTotal;
    }

    public static boolean checkInventoryFluidContainer(IItemHandler inv, FluidStack targetFluid, boolean checkFull) {
        if (inv == null) {
            return true;
        }

        int startSlot = getFluidContainerStartSlot(inv);
        for (int i = startSlot; i < inv.getSlots(); i++) {
            if (!checkFluidContainer(inv.getStackInSlot(i), targetFluid, checkFull)) {
                return false;
            }
        }

        return true;
    }

    public static boolean checkFluidContainer(ItemStack stack, FluidStack targetFluid, boolean checkFull) {
        if (stack.isEmpty()) {
            return true;
        }

        var handler = FluidUtil.getFluidHandler(stack.copyWithCount(1));
        if (handler.isEmpty()) {
            return true;
        }

        IFluidHandlerItem fluidHandler = handler.orElseThrow();
        if (checkFull) {
            FluidStack probe = targetFluid == null || targetFluid.isEmpty()
                    ? new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 1)
                    : targetFluid.copyWithAmount(1);
            return fluidHandler.fill(probe, IFluidHandler.FluidAction.SIMULATE) <= 0;
        }

        if (targetFluid != null && !targetFluid.isEmpty()) {
            FluidStack drained = fluidHandler.drain(new FluidStack(targetFluid.getFluid(), 1), IFluidHandler.FluidAction.SIMULATE);
            return drained.isEmpty();
        }

        FluidStack drained = fluidHandler.drain(1, IFluidHandler.FluidAction.SIMULATE);
        return drained.isEmpty();
    }

    private static int getFluidContainerStartSlot(IItemHandler inv) {
        String handlerName = inv.getClass().getName();
        if (handlerName.endsWith("ShipInventoryHandler")) {
            return 6;
        }
        return 0;
    }
}
