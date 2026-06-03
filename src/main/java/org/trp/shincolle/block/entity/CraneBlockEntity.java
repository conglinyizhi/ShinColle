package org.trp.shincolle.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.trp.shincolle.Config;
import org.trp.shincolle.block.CraneBlock;
import org.trp.shincolle.client.WaypointClientHelper;
import org.trp.shincolle.entity.EntityTransportWa;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.init.ModBlockEntities;
import org.trp.shincolle.init.ModParticles;
import org.trp.shincolle.init.ModSounds;
import org.trp.shincolle.item.LegacyEquipItem;
import org.trp.shincolle.menu.CraneMenu;
import org.trp.shincolle.utility.InventoryHelper;
import org.trp.shincolle.utility.PerformanceTrace;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class CraneBlockEntity extends BlockEntity implements MenuProvider, IWaypoint {

    private final ItemStackHandler inventory = new ItemStackHandler(18) {
        @Override
        protected void onContentsChanged(int slot) {
            markForSync();
        }
    };

    private final FluidTank fluidTank = new FluidTank(16000) {
        @Override
        protected void onContentsChanged() {
            markForSync();
        }

        @Override
        public int getCapacity() {
            return Math.max(1, Config.craneTankCapacity);
        }
    };

    private int remainedPower = 0;
    private int powerMax = 1000000;
    private boolean isActive = false;
    private boolean checkMetadata = false;
    private boolean checkOredict = false;
    private boolean checkNbt = false;
    private boolean enabLoad = true;
    private boolean enabUnload = true;
    private int craneMode = 0;
    private int modeItem = 0;
    private int modeRedstone = 0;
    private int modeLiquid = 0;
    private int modeEnergy = 0;

    private BlockPos lastPos = BlockPos.ZERO;
    private BlockPos nextPos = BlockPos.ZERO;
    private BlockPos chestPos = BlockPos.ZERO;
    private boolean isPaired = false;
    private UUID ownerUUID = null;
    private String ownerName = "";

    private int tickCount = 0;
    private int tickRedstone = 0;
    private EntityShipBase craningShip = null;
    private int syncedShipId = -1;
    private int liquidTransferRate = 0;
    private IItemHandler chestHandler = null;
    private IItemHandler combinedChestHandler = null;
    private int partDelay = 0;

    public CraneBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CRANE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CraneBlockEntity be) {
        be.tickCount++;
        if (!level.isClientSide) {
            boolean tracing = PerformanceTrace.enabled();
            long start = tracing ? PerformanceTrace.now() : 0L;
            try {
                be.serverTick();
            } finally {
                if (tracing) {
                    long elapsed = PerformanceTrace.elapsed(start);
                    PerformanceTrace.addBlockEntityTime(elapsed);
                    PerformanceTrace.logSlowBlockEntityTick(be, "crane", elapsed,
                            "active=" + be.isActive
                                    + " paired=" + be.isPaired
                                    + " shipId=" + be.syncedShipId
                                    + " modeItem=" + be.modeItem
                                    + " modeLiquid=" + be.modeLiquid
                                    + " modeEnergy=" + be.modeEnergy);
                }
            }
        } else {
            be.clientTick();
        }
    }

    private void clientTick() {
        if (this.level != null) {
            WaypointClientHelper.tickClient(this.level, this.worldPosition, this, this.tickCount);

            if (this.partDelay > 0) this.partDelay--;

            if (this.isActive && this.partDelay <= 0) {
                EntityShipBase targetShip = null;
                if (this.level.getEntity(this.syncedShipId) instanceof EntityShipBase ship) {
                    targetShip = ship;
                }
                
                if (targetShip != null) {
                    this.partDelay = 128;
                    double distY = this.worldPosition.getY() - targetShip.getY() - 1.0;
                    if (distY < 1.0) {
                        distY = 1.0;
                    }
                    this.level.addParticle(ModParticles.PARTICLE_CRANING.get(), 
                        this.worldPosition.getX() + 0.5, this.worldPosition.getY() - 1.0, this.worldPosition.getZ() + 0.5,
                        distY, 0.25, 0.0);
                    
                    
                    this.level.addParticle(ModParticles.PARTICLE_SPARKLE.get(), 
                        targetShip.getX(), targetShip.getY() + targetShip.getBbHeight() * 0.4, targetShip.getZ(), 
                        3.0, targetShip.getBbWidth(), 0.1);
                }
            }
        }
    }

    private void serverTick() {
        if (this.tickRedstone > 0) {
            this.tickRedstone--;
            if (this.tickRedstone == 0) setRedstoneSignal(false);
        }

        if (this.tickCount % 16 == 0) {
            if (this.isActive) {
                if (checkPairedChest()) {
                    
                    applyPreLiquidTransfer(this.modeLiquid);

                    if (checkCraningShip()) {
                        if (this.modeRedstone == 1) {
                            this.tickRedstone = 18;
                            setRedstoneSignal(true);
                        }

                        this.craningShip.setStateTimer(1, this.craningShip.getStateTimer(1) + 16);
                        boolean moved = false;

                        if (this.enabLoad) {
                            if (applyItemTransfer(true)) moved = true;
                        }

                        if (!moved && this.enabUnload) {
                            if (applyItemTransfer(false)) moved = true;
                        }

                        
                        if (this.modeLiquid != 0) {
                            if (applyLiquidTransfer(this.modeLiquid)) moved = true;
                        }

                        if (this.modeEnergy != 0) {
                            if (applyEnergyTransfer()) moved = true;
                        }

                        if (moved) {
                            if (this.level != null) {
                                this.level.playSound(null, this.worldPosition, ModSounds.SHIP_AIRCRAFT.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
                            }
                        }
                        checkCraneEnding();
                    }
                } else {
                    this.isActive = false;
                    markForSync();
                }
            }
        }

        if (this.tickCount % 64 == 0) {
            checkValidity();
        }
    }

    private void checkValidity() {
        if (this.level == null || this.level.isClientSide) return;

        if (this.isPaired && this.chestPos != BlockPos.ZERO) {
            var handler = this.level.getCapability(Capabilities.ItemHandler.BLOCK, this.chestPos, null);
            if (handler == null) {
                this.isPaired = false;
                this.chestPos = BlockPos.ZERO;
                this.chestHandler = null;
                this.combinedChestHandler = null;
                markForSync();
            }
        }

        if (this.nextPos != BlockPos.ZERO) {
            var be = this.level.getBlockEntity(this.nextPos);
            if (!(be instanceof IWaypoint)) {
                this.nextPos = BlockPos.ZERO;
                markForSync();
            }
        }
    }

    private boolean checkPairedChest() {
        if (this.chestPos == BlockPos.ZERO || this.level == null) return false;
        var handler = this.level.getCapability(Capabilities.ItemHandler.BLOCK, this.chestPos, null);
        if (handler != null) {
            this.chestHandler = handler;
            this.combinedChestHandler = createCombinedChestHandler(handler);
            return true;
        }
        this.chestHandler = null;
        this.combinedChestHandler = null;
        return false;
    }

    private boolean checkCraningShip() {
        if (this.craningShip != null && this.craningShip.isAlive() && !this.craningShip.isRemoved()) {
            if (this.craningShip.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY(), this.worldPosition.getZ() + 0.5) < 64.0) {
                if (this.craningShip.getStateMinor(43) == 2) {
                    if (this.syncedShipId != this.craningShip.getId()) {
                        this.syncedShipId = this.craningShip.getId();
                        markForSync();
                    }
                    return true;
                }
                if (this.craningShip.getStateMinor(43) == 1) {
                    moveShipToCrane(this.craningShip);
                    if (this.syncedShipId != this.craningShip.getId()) {
                        this.syncedShipId = this.craningShip.getId();
                        markForSync();
                    }
                    return true;
                }
            }
        }

        AABB aabb = new AABB(this.worldPosition).inflate(8.0);
        List<EntityShipBase> ships = this.level.getEntitiesOfClass(EntityShipBase.class, aabb);
        for (EntityShipBase ship : ships) {
            if (ship.isAlive() && ship.isTame() && this.ownerUUID != null && this.ownerUUID.equals(ship.getOwnerUUID())) {
                if (ship.getStateMinor(43) == 1 || ship.getStateMinor(43) == 2) {
                    this.craningShip = ship;
                    this.liquidTransferRate = calculateLiquidTransferRate(ship);
                    if (this.syncedShipId != ship.getId()) {
                        this.syncedShipId = ship.getId();
                        markForSync();
                    }
                    if (ship.getStateMinor(43) == 1) {
                        moveShipToCrane(ship);
                        ship.setStateMinor(43, 2);
                    }
                    return true;
                }
            }
        }
        if (this.syncedShipId != -1) {
            this.syncedShipId = -1;
            this.liquidTransferRate = 0;
            markForSync();
        }
        return false;
    }

    private void moveShipToCrane(EntityShipBase ship) {
        ship.moveGuardTargetTo(
                new Vec3(
                        this.worldPosition.getX() + 0.5D,
                        this.worldPosition.getY() - 2.0D,
                        this.worldPosition.getZ() + 0.5D),
                1.0D);
    }

    private void applyPreLiquidTransfer(int mode) {
        if (this.chestHandler == null) return;
        int preTransferAmount = Math.max(1000, this.liquidTransferRate);
        if (mode == 1) { 
            int maxDrain = Math.min(preTransferAmount, this.fluidTank.getCapacity() - this.fluidTank.getFluidAmount());
            if (maxDrain <= 0) return;
            FluidStack drained = drainFromChestContainers(this.fluidTank.getFluid(), maxDrain);
            if (!drained.isEmpty()) {
                this.fluidTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                markChestForSync();
            }
        } else if (mode == 2) { 
            if (!this.fluidTank.getFluid().isEmpty()) {
                if (fillChestContainers(this.fluidTank.getFluid())) {
                    markChestForSync();
                }
            }
        }
    }

    private boolean applyItemTransfer(boolean isLoading) {
        if (this.craningShip == null || this.combinedChestHandler == null) return false;
        IItemHandler invFrom = isLoading ? this.combinedChestHandler : this.craningShip.getInventory();
        IItemHandler invTo = isLoading ? this.craningShip.getInventory() : this.combinedChestHandler;
        
        int filterStart = isLoading ? 0 : 9;
        boolean hasNormalFilter = false;
        for (int i = 0; i < 9; i++) {
            ItemStack filter = this.inventory.getStackInSlot(filterStart + i);
            if (!filter.isEmpty() && !getItemMode(filterStart + i)) {
                hasNormalFilter = true;
                break;
            }
        }

        if (hasNormalFilter) {
            for (int i = 0; i < 9; i++) {
                ItemStack filter = this.inventory.getStackInSlot(filterStart + i);
                if (!filter.isEmpty() && !getItemMode(filterStart + i)) {
                    if (canMoveItem(isLoading, filter)) {
                        for (int slot = 0; slot < invFrom.getSlots(); slot++) {
                            ItemStack stack = invFrom.getStackInSlot(slot);
                            if (InventoryHelper.matchTargetItem(stack, filter, this.checkMetadata, this.checkNbt, this.checkOredict)) {
                                ItemStack extracted = invFrom.extractItem(slot, stack.getCount(), false);
                                if (!extracted.isEmpty()) {
                                    boolean moved = InventoryHelper.moveItemstackToInv(invTo, extracted, null);
                                    if (extracted.getCount() > 0) {
                                        returnRemainderToSourceOrDrop(invFrom, extracted);
                                    }
                                    if (moved) {
                                        markChestForSync();
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (int slot = 0; slot < invFrom.getSlots(); slot++) {
                ItemStack stack = invFrom.getStackInSlot(slot);
                if (!stack.isEmpty() && isNotModeItem(stack, isLoading)) {
                    ItemStack extracted = invFrom.extractItem(slot, stack.getCount(), false);
                    if (!extracted.isEmpty()) {
                        boolean moved = InventoryHelper.moveItemstackToInv(invTo, extracted, null);
                        if (extracted.getCount() > 0) {
                            returnRemainderToSourceOrDrop(invFrom, extracted);
                        }
                        if (moved) {
                            markChestForSync();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean canMoveItem(boolean isLoading, ItemStack temp) {
        if (this.craneMode == 3) { 
            IItemHandler targetInv = isLoading ? this.craningShip.getInventory() : this.combinedChestHandler;
            int current = InventoryHelper.calcItemStackAmount(targetInv, temp, this.checkMetadata, this.checkNbt, this.checkOredict);
            return current < temp.getCount();
        } else if (this.craneMode == 4) { 
            IItemHandler sourceInv = isLoading ? this.combinedChestHandler : this.craningShip.getInventory();
            int current = InventoryHelper.calcItemStackAmount(sourceInv, temp, this.checkMetadata, this.checkNbt, this.checkOredict);
            return current > temp.getCount();
        }
        return true;
    }

    private boolean isNotModeItem(ItemStack stack, boolean isLoading) {
        int startIdx = isLoading ? 0 : 9;
        for (int i = 0; i < 9; i++) {
            ItemStack temp = this.inventory.getStackInSlot(startIdx + i);
            if (!temp.isEmpty() && InventoryHelper.matchTargetItem(stack, temp, this.checkMetadata, this.checkNbt, this.checkOredict)) {
                if (getItemMode(startIdx + i)) return false;
            }
        }
        return true;
    }

    private boolean applyLiquidTransfer(int mode) {
        if (this.craningShip == null) return false;
        int transferRate = Math.max(0, this.liquidTransferRate);
        if (transferRate <= 0) return false;
        if (mode == 1) { 
            if (this.fluidTank.getFluidAmount() <= 0) return false;
            FluidStack toFill = this.fluidTank.getFluid().copy();
            int amountBefore = Math.min(transferRate, toFill.getAmount());
            toFill.setAmount(amountBefore);
            if (InventoryHelper.tryFillContainer(this.craningShip.getInventory(), toFill)) {
                int filled = amountBefore - toFill.getAmount();
                if (filled > 0) {
                    this.fluidTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                    return true;
                }
            }
        } else if (mode == 2) { 
            int maxDrain = Math.min(transferRate, this.fluidTank.getCapacity() - this.fluidTank.getFluidAmount());
            if (maxDrain <= 0) return false;
            FluidStack drained = InventoryHelper.tryDrainContainer(this.craningShip.getInventory(), this.fluidTank.getFluid(), maxDrain);
            if (!drained.isEmpty()) {
                this.fluidTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                return true;
            }
        }
        return false;
    }

    private boolean applyEnergyTransfer() {
        if (this.craningShip == null) {
            return false;
        }

        int transferRate = calculateEnergyTransferRate(this.craningShip);
        if (transferRate <= 0) {
            return false;
        }

        if (this.modeEnergy == 1) {
            boolean moved = pullEnergyFromChest(transferRate);
            moved |= transferEnergyToShip(this.craningShip.getInventory(), transferRate);
            if (moved) {
                markChestForSync();
            }
            return moved;
        } else if (this.modeEnergy == 2) {
            boolean moved = extractEnergyFromShip(this.craningShip.getInventory(), transferRate);
            moved |= pushEnergyToChest(transferRate);
            if (moved) {
                markChestForSync();
            }
            return moved;
        }

        return false;
    }

    private void checkCraneEnding() {
        if (this.craningShip == null) return;
        boolean stop = false;
        if (this.craneMode == 0) {
            stop = true;
        } else if (this.craneMode == 1) {
            stop = isWaitModeFull();
        } else if (this.craneMode == 2) {
            stop = isWaitModeEmpty();
        } else if (this.craneMode == 3) {
            stop = isInventoryExcess();
        } else if (this.craneMode == 4) {
            stop = isInventoryRemain();
        } else {
            stop = this.craningShip.getStateTimer(1) >= getWaitTime(this.craneMode);
        }

        if (stop) {
            if (this.modeRedstone == 2) {
                this.tickRedstone = 2;
                setRedstoneSignal(true);
            }
            this.craningShip.setStateMinor(43, 0);
            this.craningShip.setStateTimer(1, 0);
            this.craningShip = null;
            markForSync();
        }
    }

    private boolean isWaitModeFull() {
        if (this.enabLoad && !isInventoryFull(this.craningShip.getInventory())) {
            return false;
        }
        if (this.enabUnload && !isInventoryFull(this.combinedChestHandler)) {
            return false;
        }
        if (this.modeLiquid == 1 && !InventoryHelper.checkInventoryFluidContainer(this.craningShip.getInventory(), this.fluidTank.getFluid(), true)) {
            return false;
        }
        if (this.modeLiquid == 2 && !isChestFluidContainersFull(this.fluidTank.getFluid())) {
            return false;
        }
        return true;
    }

    private boolean isWaitModeEmpty() {
        if (this.enabLoad && !isInventoryEmpty(this.combinedChestHandler)) {
            return false;
        }
        if (this.enabUnload && !isInventoryEmpty(this.craningShip.getInventory())) {
            return false;
        }
        if (this.modeLiquid == 1 && !InventoryHelper.checkInventoryFluidContainer(this.chestHandler, this.fluidTank.getFluid(), false)) {
            return false;
        }
        if (this.modeLiquid == 1 && !isChestFluidContainersEmpty(this.fluidTank.getFluid())) {
            return false;
        }
        if (this.modeLiquid == 2 && !InventoryHelper.checkInventoryFluidContainer(this.craningShip.getInventory(), this.fluidTank.getFluid(), false)) {
            return false;
        }
        return true;
    }

    private boolean isInventoryExcess() {
        if (this.enabLoad && !matchesRequestedAmounts(this.craningShip.getInventory(), 0, true)) {
            return false;
        }
        if (this.enabUnload && !matchesRequestedAmounts(this.combinedChestHandler, 9, true)) {
            return false;
        }
        return true;
    }

    private boolean isInventoryRemain() {
        if (this.enabLoad && !matchesRequestedAmounts(this.combinedChestHandler, 0, false)) {
            return false;
        }
        if (this.enabUnload && !matchesRequestedAmounts(this.craningShip.getInventory(), 9, false)) {
            return false;
        }
        return true;
    }

    private boolean matchesRequestedAmounts(IItemHandler target, int filterStart, boolean atLeast) {
        if (target == null) {
            return true;
        }

        boolean foundNormalFilter = false;
        for (int i = 0; i < 9; i++) {
            ItemStack filter = this.inventory.getStackInSlot(filterStart + i);
            if (!filter.isEmpty() && !getItemMode(filterStart + i)) {
                foundNormalFilter = true;
                int current = InventoryHelper.calcItemStackAmount(target, filter, this.checkMetadata, this.checkNbt, this.checkOredict);
                if (atLeast) {
                    if (current < filter.getCount()) {
                        return false;
                    }
                } else if (current > filter.getCount()) {
                    return false;
                }
            }
        }

        return foundNormalFilter;
    }

    private static int getWaitTime(int mode) {
        if (mode >= 5 && mode <= 9) {
            return (mode - 4) * 16;
        }
        if (mode >= 10 && mode <= 14) {
            return (mode - 9) * 20 * 5;
        }
        if (mode >= 15 && mode <= 19) {
            return (mode - 14) * 20 * 60;
        }
        if (mode >= 20 && mode <= 24) {
            return (mode - 19) * 20 * 60 * 10;
        }
        return 0;
    }

    private boolean isInventoryFull(IItemHandler inv) {
        for (int i = 0; i < inv.getSlots(); i++) {
            if (inv.getStackInSlot(i).isEmpty() || inv.getStackInSlot(i).getCount() < inv.getSlotLimit(i)) return false;
        }
        return true;
    }

    private boolean isInventoryEmpty(IItemHandler inv) {
        for (int i = 0; i < inv.getSlots(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    private void setRedstoneSignal(boolean power) {
        if (this.level != null && !this.level.isClientSide) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            if (state.hasProperty(CraneBlock.POWERED) && state.getValue(CraneBlock.POWERED) != power) {
                this.level.setBlock(this.worldPosition, state.setValue(CraneBlock.POWERED, power), 3);
            }
        }
    }

    private void returnRemainderToSourceOrDrop(IItemHandler invFrom, ItemStack remainder) {
        if (remainder.isEmpty()) return;
        InventoryHelper.moveItemstackToInv(invFrom, remainder, null);
        if (!remainder.isEmpty() && this.level instanceof ServerLevel serverLevel) {
            net.minecraft.world.entity.item.ItemEntity drop = new net.minecraft.world.entity.item.ItemEntity(serverLevel, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.0, this.worldPosition.getZ() + 0.5, remainder.copy());
            drop.setDefaultPickUpDelay();
            serverLevel.addFreshEntity(drop);
            remainder.setCount(0);
        }
    }

    private void markChestForSync() {
        if (this.chestPos != null && this.level != null && !this.level.isClientSide) {
            net.minecraft.world.level.block.entity.BlockEntity be = this.level.getBlockEntity(this.chestPos);
            if (be != null) {
                be.setChanged();
                this.level.sendBlockUpdated(this.chestPos, be.getBlockState(), be.getBlockState(), 3);
            }
        }
    }

    @Nullable
    private IItemHandler getAdjacentChestHandler() {
        if (this.level == null || this.chestPos == BlockPos.ZERO) {
            return null;
        }

        for (net.minecraft.core.Direction direction : net.minecraft.core.Direction.Plane.HORIZONTAL) {
            BlockPos adjacentPos = this.chestPos.relative(direction);
            BlockState adjacentState = this.level.getBlockState(adjacentPos);
            if (!adjacentState.is(this.level.getBlockState(this.chestPos).getBlock())) {
                continue;
            }

            return this.level.getCapability(Capabilities.ItemHandler.BLOCK, adjacentPos, null);
        }

        return null;
    }

    private IItemHandler createCombinedChestHandler(IItemHandler primary) {
        IItemHandler adjacent = getAdjacentChestHandler();
        if (adjacent == null) {
            return primary;
        }
        return new CombinedItemHandler(primary, adjacent);
    }

    private FluidStack drainFromChestContainers(@Nullable FluidStack targetFluid, int maxDrain) {
        FluidStack drained = InventoryHelper.tryDrainContainer(this.chestHandler, targetFluid, maxDrain);
        if (!drained.isEmpty()) {
            return drained;
        }

        IItemHandler adjacent = getAdjacentChestHandler();
        if (adjacent == null) {
            return FluidStack.EMPTY;
        }

        return InventoryHelper.tryDrainContainer(adjacent, targetFluid, maxDrain);
    }

    private boolean fillChestContainers(FluidStack fluid) {
        if (fluid.isEmpty()) {
            return false;
        }

        boolean moved = InventoryHelper.tryFillContainer(this.chestHandler, fluid);
        if (!fluid.isEmpty()) {
            IItemHandler adjacent = getAdjacentChestHandler();
            if (adjacent != null) {
                moved |= InventoryHelper.tryFillContainer(adjacent, fluid);
            }
        }
        return moved;
    }

    private boolean isChestFluidContainersFull(@Nullable FluidStack targetFluid) {
        if (!InventoryHelper.checkInventoryFluidContainer(this.chestHandler, targetFluid, true)) {
            return false;
        }
        IItemHandler adjacent = getAdjacentChestHandler();
        return adjacent == null || InventoryHelper.checkInventoryFluidContainer(adjacent, targetFluid, true);
    }

    private boolean isChestFluidContainersEmpty(@Nullable FluidStack targetFluid) {
        if (!InventoryHelper.checkInventoryFluidContainer(this.chestHandler, targetFluid, false)) {
            return false;
        }
        IItemHandler adjacent = getAdjacentChestHandler();
        return adjacent == null || InventoryHelper.checkInventoryFluidContainer(adjacent, targetFluid, false);
    }

    private int calculateLiquidTransferRate(EntityShipBase ship) {
        int drumCount = 0;
        int enchantCount = 0;
        int equipSlots = Math.min(6, ship.getInventory().getSlots());

        if (ship instanceof EntityTransportWa && ship.isStateMarried()) {
            drumCount = 1;
        }

        for (int slot = 0; slot < equipSlots; slot++) {
            ItemStack stack = ship.getInventory().getStackInSlot(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof LegacyEquipItem equipItem)) {
                continue;
            }

            if (equipItem.getEquipTypeId(stack) != 24 || equipItem.getVariant(stack) != 1) {
                continue;
            }

            drumCount++;
            enchantCount += EnchantmentHelper.getEnchantmentsForCrafting(stack).size();
        }

        int perTickRate = drumCount * Math.max(0, Config.drumLiquidBaseRate)
                + enchantCount * Math.max(0, Config.drumLiquidEnchantRate);
        if (perTickRate <= 0) {
            return 0;
        }

        int shipLevelMultiplier = (int) (ship.getLevel() * 0.1F) + 1;
        return perTickRate * 16 * Math.max(1, shipLevelMultiplier);
    }

    private int calculateEnergyTransferRate(EntityShipBase ship) {
        int drumCount = 0;
        int enchantCount = 0;
        int equipSlots = Math.min(6, ship.getInventory().getSlots());

        if (ship instanceof EntityTransportWa && ship.isStateMarried()) {
            drumCount = 1;
        }

        for (int slot = 0; slot < equipSlots; slot++) {
            ItemStack stack = ship.getInventory().getStackInSlot(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof LegacyEquipItem equipItem)) {
                continue;
            }

            if (equipItem.getEquipTypeId(stack) != 24 || equipItem.getVariant(stack) != 2) {
                continue;
            }

            drumCount++;
            enchantCount += EnchantmentHelper.getEnchantmentsForCrafting(stack).size();
        }

        int perTickRate = drumCount * Math.max(0, Config.drumEnergyBaseRate)
                + enchantCount * Math.max(0, Config.drumEnergyEnchantRate);
        if (perTickRate <= 0) {
            return 0;
        }

        int shipLevelMultiplier = (int) (ship.getLevel() * 0.1F) + 1;
        return perTickRate * 16 * Math.max(1, shipLevelMultiplier);
    }

    private boolean transferEnergyToShip(IItemHandler shipInventory, int maxTransfer) {
        int available = Math.min(maxTransfer, this.remainedPower);
        if (available <= 0) {
            return false;
        }

        for (int slot = 0; slot < shipInventory.getSlots(); slot++) {
            ItemStack stack = shipInventory.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energy == null || !energy.canReceive()) {
                continue;
            }

            int accepted = energy.receiveEnergy(available, false);
            if (accepted > 0) {
                this.remainedPower = Math.max(0, this.remainedPower - accepted);
                markForSync();
                return true;
            }
        }

        return false;
    }

    private boolean extractEnergyFromShip(IItemHandler shipInventory, int maxTransfer) {
        int capacityLeft = Math.max(0, this.powerMax - this.remainedPower);
        int allowed = Math.min(maxTransfer, capacityLeft);
        if (allowed <= 0) {
            return false;
        }

        for (int slot = 0; slot < shipInventory.getSlots(); slot++) {
            ItemStack stack = shipInventory.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energy == null || !energy.canExtract()) {
                continue;
            }

            int extracted = energy.extractEnergy(allowed, false);
            if (extracted > 0) {
                this.remainedPower = Math.min(this.powerMax, this.remainedPower + extracted);
                markForSync();
                return true;
            }
        }

        return false;
    }

    private boolean pullEnergyFromChest(int maxTransfer) {
        int capacityLeft = Math.max(0, this.powerMax - this.remainedPower);
        int allowed = Math.min(maxTransfer, capacityLeft);
        if (allowed <= 0 || this.chestHandler == null) {
            return false;
        }

        int moved = extractEnergyFromInventory(this.chestHandler, allowed);
        if (moved > 0) {
            this.remainedPower = Math.min(this.powerMax, this.remainedPower + moved);
            markForSync();
            return true;
        }

        IItemHandler adjacent = getAdjacentChestHandler();
        if (adjacent == null) {
            return false;
        }

        moved = extractEnergyFromInventory(adjacent, allowed);
        if (moved > 0) {
            this.remainedPower = Math.min(this.powerMax, this.remainedPower + moved);
            markForSync();
            return true;
        }

        return false;
    }

    private boolean pushEnergyToChest(int maxTransfer) {
        int available = Math.min(maxTransfer, this.remainedPower);
        if (available <= 0 || this.chestHandler == null) {
            return false;
        }

        int moved = receiveEnergyIntoInventory(this.chestHandler, available);
        if (moved > 0) {
            this.remainedPower = Math.max(0, this.remainedPower - moved);
            markForSync();
            return true;
        }

        IItemHandler adjacent = getAdjacentChestHandler();
        if (adjacent == null) {
            return false;
        }

        moved = receiveEnergyIntoInventory(adjacent, available);
        if (moved > 0) {
            this.remainedPower = Math.max(0, this.remainedPower - moved);
            markForSync();
            return true;
        }

        return false;
    }

    private static int extractEnergyFromInventory(IItemHandler inventory, int maxTransfer) {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energy == null || !energy.canExtract()) {
                continue;
            }

            int extracted = energy.extractEnergy(maxTransfer, false);
            if (extracted > 0) {
                return extracted;
            }
        }

        return 0;
    }

    private static int receiveEnergyIntoInventory(IItemHandler inventory, int maxTransfer) {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energy == null || !energy.canReceive()) {
                continue;
            }

            int accepted = energy.receiveEnergy(maxTransfer, false);
            if (accepted > 0) {
                return accepted;
            }
        }

        return 0;
    }



    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.put("Tank", fluidTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("Power", remainedPower);
        tag.putInt("PowerMax", powerMax);
        tag.putBoolean("IsActive", isActive);
        tag.putBoolean("CheckMetadata", checkMetadata);
        tag.putBoolean("CheckOredict", checkOredict);
        tag.putBoolean("CheckNbt", checkNbt);
        tag.putBoolean("EnabLoad", enabLoad);
        tag.putBoolean("EnabUnload", enabUnload);
        tag.putInt("CraneMode", craneMode);
        tag.putInt("ModeItem", modeItem);
        tag.putInt("ModeRedstone", modeRedstone);
        tag.putInt("ModeLiquid", modeLiquid);
        tag.putInt("ModeEnergy", modeEnergy);
        tag.putLong("LastPos", lastPos.asLong());
        tag.putLong("NextPos", nextPos.asLong());
        tag.putLong("ChestPos", chestPos.asLong());
        tag.putBoolean("IsPaired", isPaired);
        if (ownerUUID != null) tag.putUUID("OwnerUUID", ownerUUID);
        tag.putString("OwnerName", ownerName);
        tag.putInt("SyncedShipId", syncedShipId);
        tag.putInt("LiquidTransferRate", liquidTransferRate);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("Tank")) fluidTank.readFromNBT(registries, tag.getCompound("Tank"));
        remainedPower = tag.getInt("Power");
        powerMax = tag.getInt("PowerMax");
        isActive = tag.getBoolean("IsActive");
        checkMetadata = tag.getBoolean("CheckMetadata");
        checkOredict = tag.getBoolean("CheckOredict");
        checkNbt = tag.getBoolean("CheckNbt");
        enabLoad = tag.getBoolean("EnabLoad");
        enabUnload = tag.getBoolean("EnabUnload");
        craneMode = tag.getInt("CraneMode");
        modeItem = tag.getInt("ModeItem");
        modeRedstone = tag.getInt("ModeRedstone");
        modeLiquid = tag.getInt("ModeLiquid");
        modeEnergy = tag.getInt("ModeEnergy");
        if (tag.contains("LastPos")) lastPos = BlockPos.of(tag.getLong("LastPos"));
        if (tag.contains("NextPos")) nextPos = BlockPos.of(tag.getLong("NextPos"));
        if (tag.contains("ChestPos")) chestPos = BlockPos.of(tag.getLong("ChestPos"));
        isPaired = tag.getBoolean("IsPaired");
        if (tag.hasUUID("OwnerUUID")) ownerUUID = tag.getUUID("OwnerUUID");
        ownerName = tag.getString("OwnerName");
        syncedShipId = tag.getInt("SyncedShipId");
        liquidTransferRate = tag.getInt("LiquidTransferRate");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("tile.shincolle.BlockCrane.name");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CraneMenu(containerId, playerInventory, this);
    }

    public ItemStackHandler getInventory() { return inventory; }
    public FluidTank getFluidTank() { return fluidTank; }

    public int getCraningShipId() {
        return this.craningShip == null ? 0 : this.craningShip.getId();
    }

    public int getCraningShipTimer() {
        return this.craningShip == null ? 0 : this.craningShip.getStateTimer(1);
    }

    public int getRemainedPower() { return remainedPower; }
    public void setRemainedPower(int val) {
        if (this.remainedPower == val) {
            return;
        }
        this.remainedPower = val;
        markForSync();
    }

    public int getPowerMax() { return powerMax; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean val) {
        if (this.isActive == val) {
            return;
        }
        this.isActive = val;
        markForSync();
    }

    public boolean isCheckMetadata() { return checkMetadata; }
    public void setCheckMetadata(boolean val) {
        if (this.checkMetadata == val) {
            return;
        }
        this.checkMetadata = val;
        markForSync();
    }

    public boolean isCheckOredict() { return checkOredict; }
    public void setCheckOredict(boolean val) {
        if (this.checkOredict == val) {
            return;
        }
        this.checkOredict = val;
        markForSync();
    }

    public boolean isCheckNbt() { return checkNbt; }
    public void setCheckNbt(boolean val) {
        if (this.checkNbt == val) {
            return;
        }
        this.checkNbt = val;
        markForSync();
    }

    public boolean isEnabLoad() { return enabLoad; }
    public void setEnabLoad(boolean val) {
        if (this.enabLoad == val) {
            return;
        }
        this.enabLoad = val;
        markForSync();
    }

    public boolean isEnabUnload() { return enabUnload; }
    public void setEnabUnload(boolean val) {
        if (this.enabUnload == val) {
            return;
        }
        this.enabUnload = val;
        markForSync();
    }

    public int getCraneMode() { return craneMode; }
    public void setCraneMode(int val) {
        if (this.craneMode == val) {
            return;
        }
        this.craneMode = val;
        markForSync();
    }

    public int getModeItem() { return modeItem; }
    public void setModeItem(int val) {
        if (this.modeItem == val) {
            return;
        }
        this.modeItem = val;
        markForSync();
    }
    public void setItemMode(int id, boolean val) {
        int next = val ? modeItem | (1 << id) : modeItem & ~(1 << id);
        if (this.modeItem == next) {
            return;
        }
        modeItem = next;
        markForSync();
    }
    public boolean getItemMode(int id) { return (modeItem & (1 << id)) != 0; }

    public int getModeRedstone() { return modeRedstone; }
    public void setModeRedstone(int val) {
        if (this.modeRedstone == val) {
            return;
        }
        this.modeRedstone = val;
        markForSync();
    }

    public int getModeLiquid() { return modeLiquid; }
    public void setModeLiquid(int val) {
        if (this.modeLiquid == val) {
            return;
        }
        this.modeLiquid = val;
        markForSync();
    }

    public int getModeEnergy() { return modeEnergy; }
    public void setModeEnergy(int val) {
        if (this.modeEnergy == val) {
            return;
        }
        this.modeEnergy = val;
        markForSync();
    }

    public BlockPos getLastPos() { return lastPos; }
    public void setLastPos(BlockPos pos) {
        BlockPos next = pos == null ? BlockPos.ZERO : pos;
        if (this.lastPos.equals(next)) {
            return;
        }
        this.lastPos = next;
        markForSync();
    }

    public BlockPos getNextPos() { return nextPos; }
    public void setNextPos(BlockPos pos) {
        BlockPos next = pos == null ? BlockPos.ZERO : pos;
        if (this.nextPos.equals(next)) {
            return;
        }
        this.nextPos = next;
        markForSync();
    }

    public BlockPos getChestPos() { return chestPos; }
    public void setChestPos(BlockPos pos) {
        BlockPos next = pos == null ? BlockPos.ZERO : pos;
        boolean nextPaired = next != BlockPos.ZERO;
        if (this.chestPos.equals(next) && this.isPaired == nextPaired) {
            return;
        }
        this.chestPos = next;
        this.isPaired = nextPaired;
        markForSync();
    }

    @Override
    @Nullable
    public UUID getOwnerUUID() { return ownerUUID; }

    public void setOwnerUUID(@Nullable UUID uuid) {
        if (java.util.Objects.equals(this.ownerUUID, uuid)) {
            return;
        }
        this.ownerUUID = uuid;
        markForSync();
    }

    @Override
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String name) {
        String next = name == null ? "" : name;
        if (this.ownerName.equals(next)) {
            return;
        }
        this.ownerName = next;
        markForSync();
    }

    @Override
    public boolean showBaseParticle() { return false; }

    public void markForSync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private static final class CombinedItemHandler implements IItemHandler {
        private final IItemHandler first;
        private final IItemHandler second;

        private CombinedItemHandler(IItemHandler first, IItemHandler second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public int getSlots() {
            return this.first.getSlots() + this.second.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return isFirst(slot) ? this.first.getStackInSlot(slot) : this.second.getStackInSlot(slot - this.first.getSlots());
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return isFirst(slot) ? this.first.insertItem(slot, stack, simulate) : this.second.insertItem(slot - this.first.getSlots(), stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return isFirst(slot) ? this.first.extractItem(slot, amount, simulate) : this.second.extractItem(slot - this.first.getSlots(), amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return isFirst(slot) ? this.first.getSlotLimit(slot) : this.second.getSlotLimit(slot - this.first.getSlots());
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isFirst(slot) ? this.first.isItemValid(slot, stack) : this.second.isItemValid(slot - this.first.getSlots(), stack);
        }

        private boolean isFirst(int slot) {
            return slot < this.first.getSlots();
        }
    }
}
