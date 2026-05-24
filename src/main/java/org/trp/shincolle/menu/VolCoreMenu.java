package org.trp.shincolle.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.trp.shincolle.block.entity.VolCoreBlockEntity;

public class VolCoreMenu extends AbstractContainerMenu {
    private static final int TILE_SLOT_COUNT = 9;
    private static final int PLAYER_INV_START = TILE_SLOT_COUNT;
    private static final int PLAYER_INV_END = PLAYER_INV_START + 27;
    private static final int HOTBAR_START = PLAYER_INV_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final VolCoreBlockEntity blockEntity;
    private final boolean clientSide;

    private int powerSynced;
    private int activeSynced;

    public VolCoreMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, buffer));
    }

    public VolCoreMenu(int containerId, Inventory playerInventory, VolCoreBlockEntity blockEntity) {
        super(ModMenus.VOL_CORE_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.clientSide = playerInventory.player.level().isClientSide;

        this.powerSynced = blockEntity.getRemainedPower();
        this.activeSynced = blockEntity.isBtnActive() ? 1 : 0;

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return VolCoreMenu.this.blockEntity.getRemainedPower();
            }

            @Override
            public void set(int value) {
                VolCoreMenu.this.powerSynced = value;
            }
        });

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return VolCoreMenu.this.blockEntity.isBtnActive() ? 1 : 0;
            }

            @Override
            public void set(int value) {
                VolCoreMenu.this.activeSynced = value;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new SlotItemHandler(blockEntity.getInventory(), col + row * 3, 62 + col * 18, 19 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public int getRemainedPower() {
        return clientSide ? powerSynced : blockEntity.getRemainedPower();
    }

    public boolean isBtnActive() {
        return (clientSide ? activeSynced : (blockEntity.isBtnActive() ? 1 : 0)) != 0;
    }

    @Override
    public boolean stillValid(Player player) {
        if (player.level().getBlockEntity(this.blockEntity.getBlockPos()) != this.blockEntity) {
            return false;
        }
        return player.distanceToSqr(blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY() + 0.5, blockEntity.getBlockPos().getZ() + 0.5) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copied = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copied = stack.copy();

            if (index < TILE_SLOT_COUNT) {
                if (!this.moveItemStackTo(stack, PLAYER_INV_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (isValidFuel(stack)) {
                    if (!this.moveItemStackTo(stack, 0, TILE_SLOT_COUNT, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= PLAYER_INV_START && index < PLAYER_INV_END) {
                    if (!this.moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= HOTBAR_START && index < HOTBAR_END) {
                    if (!this.moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == copied.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }
        return copied;
    }

    private boolean isValidFuel(ItemStack stack) {
        for (int slot = 0; slot < TILE_SLOT_COUNT; slot++) {
            if (blockEntity.getInventory().isItemValid(slot, stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (player.level().isClientSide) {
            return true;
        }

        if (id == 0) {
            blockEntity.setBtnActive(!blockEntity.isBtnActive());
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    private static VolCoreBlockEntity getBlockEntity(Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        if (buffer == null) throw new IllegalStateException("Missing VolCore menu data.");
        BlockPos pos = buffer.readBlockPos();
        if (playerInventory.player.level().getBlockEntity(pos) instanceof VolCoreBlockEntity volCore) {
            return volCore;
        }
        throw new IllegalStateException("VolCore block entity not found.");
    }
}
