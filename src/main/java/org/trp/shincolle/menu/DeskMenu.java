package org.trp.shincolle.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import org.trp.shincolle.block.entity.DeskBlockEntity;

public class DeskMenu extends AbstractContainerMenu {
    private final int deskType;
    private int chapter;
    private int page;
    private int guiFunc;
    private int radarZoom;
    private final DeskBlockEntity blockEntity;
    private final boolean clientSide;

    public DeskMenu(int id, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(id, playerInventory, getDeskTypeAndEntity(playerInventory, data));
    }

    private static Object[] getDeskTypeAndEntity(Inventory playerInventory, RegistryFriendlyByteBuf data) {
        int deskType = data.readInt();
        if (deskType == 0) {
            BlockPos pos = data.readBlockPos();
            if (playerInventory.player.level().getBlockEntity(pos) instanceof DeskBlockEntity desk) {
                return new Object[]{deskType, desk};
            }
            throw new IllegalStateException("Desk block entity not found.");
        }
        return new Object[]{deskType, data.readInt(), data.readInt()};
    }

    private DeskMenu(int id, Inventory playerInventory, Object[] typeAndData) {
        this(id, playerInventory, (int) typeAndData[0], 
             typeAndData.length > 2 ? (int) typeAndData[1] : 0, 
             typeAndData.length > 2 ? (int) typeAndData[2] : 0,
             0, 0,
             typeAndData.length == 2 ? (DeskBlockEntity) typeAndData[1] : null);
    }

    public DeskMenu(int id, Inventory playerInventory, int deskType) {
        this(id, playerInventory, deskType, 0, 0, 0, 0, null);
    }

    public DeskMenu(int id, Inventory playerInventory, int deskType, int chapter, int page) {
        this(id, playerInventory, deskType, chapter, page, 0, 0, null);
    }

    public DeskMenu(int id, Inventory playerInventory, int deskType, int chapter, int page, int guiFunc, int radarZoom, DeskBlockEntity blockEntity) {
        super(ModMenus.DESK_MENU.get(), id);
        this.deskType = deskType;
        this.chapter = chapter;
        this.page = page;
        this.guiFunc = guiFunc;
        this.radarZoom = radarZoom;
        this.blockEntity = blockEntity;
        this.clientSide = playerInventory.player.level().isClientSide;

        if (this.blockEntity != null) {
            this.chapter = this.blockEntity.getBookChap();
            this.page = this.blockEntity.getBookPage();
            this.guiFunc = this.blockEntity.getGuiFunc();
            this.radarZoom = this.blockEntity.getRadarZoomLv();

            this.addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return DeskMenu.this.blockEntity.getBookChap();
                }

                @Override
                public void set(int value) {
                    DeskMenu.this.chapter = value;
                }
            });

            this.addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return DeskMenu.this.blockEntity.getBookPage();
                }

                @Override
                public void set(int value) {
                    DeskMenu.this.page = value;
                }
            });

            this.addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return DeskMenu.this.blockEntity.getGuiFunc();
                }

                @Override
                public void set(int value) {
                    DeskMenu.this.guiFunc = value;
                }
            });

            this.addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return DeskMenu.this.blockEntity.getRadarZoomLv();
                }

                @Override
                public void set(int value) {
                    DeskMenu.this.radarZoom = value;
                }
            });
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public int getDeskType() {
        return deskType;
    }

    public int getChapter() {
        return chapter;
    }

    public int getPage() {
        return page;
    }

    public int getGuiFunc() {
        return guiFunc;
    }

    public int getRadarZoom() {
        return radarZoom;
    }

    public DeskBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
