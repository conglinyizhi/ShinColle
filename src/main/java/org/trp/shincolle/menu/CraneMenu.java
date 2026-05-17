package org.trp.shincolle.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.trp.shincolle.block.entity.CraneBlockEntity;
import org.trp.shincolle.entity.base.EntityShipBase;

public class CraneMenu extends AbstractContainerMenu {
    private final CraneBlockEntity blockEntity;
    private final boolean clientSide;
    private final Level level;

    
    private int remainedPower;
    private int powerMax;
    private int packedBooleans; 
    private int craneMode;
    private int modeItem;
    private int modeRedstone;
    private int modeLiquid;
    private int modeEnergy;
    private int shipEntityId;
    private int shipTimer;

    public CraneMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, buffer));
    }

    public CraneMenu(int containerId, Inventory playerInventory, CraneBlockEntity blockEntity) {
        super(ModMenus.CRANE_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.clientSide = playerInventory.player.level().isClientSide;
        this.level = playerInventory.player.level();

        if (!this.clientSide && blockEntity.getOwnerUUID() == null) {
            blockEntity.setOwnerUUID(playerInventory.player.getUUID());
            blockEntity.setOwnerName(playerInventory.player.getName().getString());
        }

        
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return blockEntity.getRemainedPower(); }
            @Override public void set(int value) { remainedPower = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return blockEntity.getPowerMax(); }
            @Override public void set(int value) { powerMax = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() {
                int packed = 0;
                if (blockEntity.isActive()) packed |= 1;
                if (blockEntity.isCheckMetadata()) packed |= 2;
                if (blockEntity.isCheckOredict()) packed |= 4;
                if (blockEntity.isCheckNbt()) packed |= 8;
                if (blockEntity.isEnabLoad()) packed |= 16;
                if (blockEntity.isEnabUnload()) packed |= 32;
                return packed;
            }
            @Override public void set(int value) { packedBooleans = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return blockEntity.getCraneMode(); }
            @Override public void set(int value) { craneMode = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return blockEntity.getModeItem(); }
            @Override public void set(int value) { modeItem = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return blockEntity.getModeRedstone(); }
            @Override public void set(int value) { modeRedstone = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return blockEntity.getModeLiquid(); }
            @Override public void set(int value) { modeLiquid = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return blockEntity.getModeEnergy(); }
            @Override public void set(int value) { modeEnergy = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return blockEntity.getCraningShipId(); }
            @Override public void set(int value) { shipEntityId = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return blockEntity.getCraningShipTimer(); }
            @Override public void set(int value) { shipTimer = value; }
        });

        
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new SlotItemHandler(blockEntity.getInventory(), i, 8 + i * 18, 65));
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new SlotItemHandler(blockEntity.getInventory(), i + 9, 8 + i * 18, 96));
        }

        
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 119 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 177));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY() + 0.5, blockEntity.getBlockPos().getZ() + 0.5) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        
        
        return ItemStack.EMPTY;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        
        if (slotId >= 0 && slotId < 18) {
            ItemStack held = player.containerMenu.getCarried();
            Slot slot = this.slots.get(slotId);
            
            if (!held.isEmpty()) {
                ItemStack ghost = held.copy();
                if (button == 1) { 
                    ghost.setCount(1);
                    slot.set(ghost);
                    blockEntity.setItemMode(slotId, true);
                } else { 
                    
                    ItemStack old = slot.getItem();
                    if (ItemStack.isSameItem(ghost, old)) {
                        ghost.setCount(Math.min(ghost.getMaxStackSize(), ghost.getCount() + old.getCount()));
                    }
                    slot.set(ghost);
                    blockEntity.setItemMode(slotId, false);
                }
            } else {
                slot.set(ItemStack.EMPTY);
                blockEntity.setItemMode(slotId, false);
            }
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        switch (id) {
            case 0: 
                blockEntity.setActive(!blockEntity.isActive());
                return true;
            case 1: 
                int m = blockEntity.getCraneMode() + 1;
                if (m > 24) m = 24;
                blockEntity.setCraneMode(m);
                return true;
            case 11: 
                int m2 = blockEntity.getCraneMode() - 1;
                if (m2 < 0) m2 = 0;
                blockEntity.setCraneMode(m2);
                return true;
            case 2: 
                blockEntity.setCheckMetadata(!blockEntity.isCheckMetadata());
                return true;
            case 3: 
                blockEntity.setCheckOredict(!blockEntity.isCheckOredict());
                return true;
            case 6: 
                blockEntity.setCheckNbt(!blockEntity.isCheckNbt());
                return true;
            case 7: 
                int r = (blockEntity.getModeRedstone() + 1) % 3;
                blockEntity.setModeRedstone(r);
                return true;
            case 4: 
                blockEntity.setEnabLoad(!blockEntity.isEnabLoad());
                return true;
            case 5: 
                blockEntity.setEnabUnload(!blockEntity.isEnabUnload());
                return true;
            case 8: 
                int l = (blockEntity.getModeLiquid() + 1) % 3;
                blockEntity.setModeLiquid(l);
                return true;
        }
        return super.clickMenuButton(player, id);
    }

    
    public int getRemainedPower() { return clientSide ? remainedPower : blockEntity.getRemainedPower(); }
    public int getPowerMax() { return clientSide ? powerMax : blockEntity.getPowerMax(); }
    public boolean isActive() { 
        int val = clientSide ? packedBooleans : (
            (blockEntity.isActive() ? 1 : 0) |
            (blockEntity.isCheckMetadata() ? 2 : 0) |
            (blockEntity.isCheckOredict() ? 4 : 0) |
            (blockEntity.isCheckNbt() ? 8 : 0) |
            (blockEntity.isEnabLoad() ? 16 : 0) |
            (blockEntity.isEnabUnload() ? 32 : 0)
        );
        return (val & 1) != 0; 
    }
    public boolean isCheckMetadata() { 
        int val = clientSide ? packedBooleans : (
            (blockEntity.isActive() ? 1 : 0) |
            (blockEntity.isCheckMetadata() ? 2 : 0) |
            (blockEntity.isCheckOredict() ? 4 : 0) |
            (blockEntity.isCheckNbt() ? 8 : 0) |
            (blockEntity.isEnabLoad() ? 16 : 0) |
            (blockEntity.isEnabUnload() ? 32 : 0)
        );
        return (val & 2) != 0; 
    }
    public boolean isCheckOredict() { 
        int val = clientSide ? packedBooleans : (
            (blockEntity.isActive() ? 1 : 0) |
            (blockEntity.isCheckMetadata() ? 2 : 0) |
            (blockEntity.isCheckOredict() ? 4 : 0) |
            (blockEntity.isCheckNbt() ? 8 : 0) |
            (blockEntity.isEnabLoad() ? 16 : 0) |
            (blockEntity.isEnabUnload() ? 32 : 0)
        );
        return (val & 4) != 0; 
    }
    public boolean isCheckNbt() { 
        int val = clientSide ? packedBooleans : (
            (blockEntity.isActive() ? 1 : 0) |
            (blockEntity.isCheckMetadata() ? 2 : 0) |
            (blockEntity.isCheckOredict() ? 4 : 0) |
            (blockEntity.isCheckNbt() ? 8 : 0) |
            (blockEntity.isEnabLoad() ? 16 : 0) |
            (blockEntity.isEnabUnload() ? 32 : 0)
        );
        return (val & 8) != 0; 
    }
    public boolean isEnabLoad() { 
        int val = clientSide ? packedBooleans : (
            (blockEntity.isActive() ? 1 : 0) |
            (blockEntity.isCheckMetadata() ? 2 : 0) |
            (blockEntity.isCheckOredict() ? 4 : 0) |
            (blockEntity.isCheckNbt() ? 8 : 0) |
            (blockEntity.isEnabLoad() ? 16 : 0) |
            (blockEntity.isEnabUnload() ? 32 : 0)
        );
        return (val & 16) != 0; 
    }
    public boolean isEnabUnload() { 
        int val = clientSide ? packedBooleans : (
            (blockEntity.isActive() ? 1 : 0) |
            (blockEntity.isCheckMetadata() ? 2 : 0) |
            (blockEntity.isCheckOredict() ? 4 : 0) |
            (blockEntity.isCheckNbt() ? 8 : 0) |
            (blockEntity.isEnabLoad() ? 16 : 0) |
            (blockEntity.isEnabUnload() ? 32 : 0)
        );
        return (val & 32) != 0; 
    }
    public int getCraneMode() { return clientSide ? craneMode : blockEntity.getCraneMode(); }
    public int getModeItem() { return clientSide ? modeItem : blockEntity.getModeItem(); }
    public int getModeRedstone() { return clientSide ? modeRedstone : blockEntity.getModeRedstone(); }
    public int getModeLiquid() { return clientSide ? modeLiquid : blockEntity.getModeLiquid(); }
    public int getModeEnergy() { return clientSide ? modeEnergy : blockEntity.getModeEnergy(); }

    public int getShipEntityId() { return clientSide ? shipEntityId : blockEntity.getCraningShipId(); }
    public int getShipTimer() { return clientSide ? shipTimer : blockEntity.getCraningShipTimer(); }

    public EntityShipBase getShipEntity() {
        int id = getShipEntityId();
        if (id <= 0) {
            return null;
        }
        if (level.getEntity(id) instanceof EntityShipBase ship) {
            return ship;
        }
        return null;
    }

    public CraneBlockEntity getBlockEntity() { return blockEntity; }

    private static CraneBlockEntity getBlockEntity(Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        if (playerInventory.player.level().getBlockEntity(pos) instanceof CraneBlockEntity crane) {
            return crane;
        }
        throw new IllegalStateException("Crane block entity not found.");
    }
}
