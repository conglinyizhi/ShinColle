package org.trp.shincolle.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class DeskMenu extends AbstractContainerMenu {
    private final int deskType;
    private final int chapter;
    private final int page;

    public DeskMenu(int id, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(id, playerInventory, data.readInt(), data.readInt(), data.readInt());
    }

    public DeskMenu(int id, Inventory playerInventory, int deskType) {
        this(id, playerInventory, deskType, 0, 0);
    }

    public DeskMenu(int id, Inventory playerInventory, int deskType, int chapter, int page) {
        super(ModMenus.DESK_MENU.get(), id);
        this.deskType = deskType;
        this.chapter = chapter;
        this.page = page;
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
}
