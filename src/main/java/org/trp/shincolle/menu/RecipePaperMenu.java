package org.trp.shincolle.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.trp.shincolle.utility.RecipePaperData;

import java.util.ArrayList;
import java.util.List;

public class RecipePaperMenu extends AbstractContainerMenu {
    private final ItemStack hostStack;
    private final InteractionHand hand;
    private final Container craftMatrix = new SimpleContainer(9);
    private final Container craftResult = new SimpleContainer(1);
    private final Level level;

    public RecipePaperMenu(int id, Inventory playerInv, RegistryFriendlyByteBuf buf) {
        this(id, playerInv, buf.readEnum(InteractionHand.class));
    }

    private RecipePaperMenu(int id, Inventory playerInv, InteractionHand hand) {
        this(id, playerInv, playerInv.player.getItemInHand(hand), hand);
    }

    public RecipePaperMenu(int id, Inventory playerInv, ItemStack hostStack, InteractionHand hand) {
        super(ModMenus.RECIPE_PAPER_MENU.get(), id);
        this.hostStack = hostStack;
        this.hand = hand;
        this.level = playerInv.player.level();

        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        
        this.addSlot(new Slot(this.craftResult, 0, 124, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player player) {
                return false;
            }
        });

        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
        }

        loadRecipe();
    }

    private void loadRecipe() {
        ItemStack[] recipeGrid = RecipePaperData.loadRecipeGrid(this.hostStack, this.level.registryAccess());
        for (int i = 0; i < recipeGrid.length; i++) {
            if (!recipeGrid[i].isEmpty()) {
                this.craftMatrix.setItem(i, recipeGrid[i]);
            }
        }
        updateResult();
    }

    private void saveRecipe() {
        List<ItemStack> grid = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            grid.add(this.craftMatrix.getItem(i));
        }
        RecipePaperData.saveRecipeGrid(this.hostStack, this.level.registryAccess(), grid, this.craftResult.getItem(0));
    }

    private void updateResult() {
        List<ItemStack> inputList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            inputList.add(craftMatrix.getItem(i));
        }
        this.craftResult.setItem(0, RecipePaperData.getRecipePreviewResult(this.level, inputList));
    }

    private void saveRecipeIfServer() {
        if (!this.level.isClientSide) {
            saveRecipe();
        }
    }

    @Override
    public void slotsChanged(Container container) {
        if (container == craftMatrix) {
            updateResult();
            if (!level.isClientSide) {
                saveRecipe();
            }
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!level.isClientSide) {
            saveRecipe();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return isStillBoundToHostStack(player.getItemInHand(hand), hostStack);
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (isPreviewResultSlot(slotId)) {
            // Legacy recipe paper uses the output slot as a preview only.
            return;
        }
        if (slotId >= 0 && slotId < 9) {
            Slot slot = slots.get(slotId);
            ItemStack cursorStack = getCarried();
            if (!cursorStack.isEmpty()) {
                if (button == 1) { 
                    slot.set(ItemStack.EMPTY);
                } else { 
                    ItemStack copy = cursorStack.copy();
                    copy.setCount(1);
                    slot.set(copy);
                }
            } else {
                slot.set(ItemStack.EMPTY);
            }
            updateResult();
            saveRecipeIfServer();
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    static boolean isStillBoundToHostStack(ItemStack currentHandStack, ItemStack hostStack) {
        return currentHandStack == hostStack;
    }

    static boolean isPreviewResultSlot(int slotId) {
        return slotId == 9;
    }
}
