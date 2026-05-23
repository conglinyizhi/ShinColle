package org.trp.shincolle.menu;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class RecipePaperMenu extends AbstractContainerMenu {
    private static final int SLOT_INPUT_START = 0;
    private static final int SLOT_INPUT_END = 9;
    private static final int SLOT_RESULT = 9;
    private static final int SLOT_PLAYER_START = 10;
    private static final int SLOT_HOTBAR_START = 37;
    private static final int SLOT_ALL_END = 46;

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
        CustomData customData = hostStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("Recipe", 9)) {
                ListTag list = tag.getList("Recipe", 10);
                for (int i = 0; i < list.size(); i++) {
                    CompoundTag itemTag = list.getCompound(i);
                    int slot = itemTag.getInt("Slot");
                    if (slot >= 0 && slot < 9) {
                        this.craftMatrix.setItem(slot, ItemStack.parseOptional(level.registryAccess(), itemTag));
                    }
                }
            }
        }
        updateResult();
    }

    private void saveRecipe() {
        ListTag list = new ListTag();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = craftMatrix.getItem(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = (CompoundTag) stack.save(level.registryAccess());
                itemTag.putInt("Slot", i);
                list.add(itemTag);
            }
        }

        hostStack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY,
                data -> data.update(tag -> tag.put("Recipe", list)));
    }

    private void updateResult() {
        List<ItemStack> inputList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            inputList.add(craftMatrix.getItem(i));
        }
        CraftingInput input = CraftingInput.of(3, 3, inputList);
        
        var recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level);
        if (recipe.isPresent()) {
            this.craftResult.setItem(0, recipe.get().value().assemble(input, level.registryAccess()));
        } else {
            this.craftResult.setItem(0, ItemStack.EMPTY);
        }
    }

    @Override
    public void slotsChanged(Container container) {
        if (container == craftMatrix) {
            updateResult();
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
        return ItemStack.isSameItemSameComponents(player.getItemInHand(hand), hostStack);
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
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
            return;
        }
        if (slotId == 9) {
            // Legacy recipe paper uses the output slot as a preview only.
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= this.slots.size()) {
            return ItemStack.EMPTY;
        }

        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = slot.getItem();
        ItemStack copy = stackInSlot.copy();

        if (index >= SLOT_INPUT_START && index < SLOT_INPUT_END) {
            slot.set(ItemStack.EMPTY);
            slot.setChanged();
            updateResult();
            return copy;
        }

        if (index == SLOT_RESULT) {
            return ItemStack.EMPTY;
        }

        if (index >= SLOT_PLAYER_START && index < SLOT_ALL_END) {
            if (!placeGhostIngredient(stackInSlot)) {
                return ItemStack.EMPTY;
            }
            updateResult();
            return copy;
        }

        return ItemStack.EMPTY;
    }

    private boolean placeGhostIngredient(ItemStack stack) {
        for (int i = SLOT_INPUT_START; i < SLOT_INPUT_END; i++) {
            ItemStack existing = this.craftMatrix.getItem(i);
            if (existing.isEmpty()) {
                ItemStack ghost = stack.copy();
                ghost.setCount(1);
                this.craftMatrix.setItem(i, ghost);
                return true;
            }
        }

        for (int i = SLOT_INPUT_START; i < SLOT_INPUT_END; i++) {
            ItemStack existing = this.craftMatrix.getItem(i);
            if (ItemStack.isSameItemSameComponents(existing, stack)) {
                ItemStack ghost = stack.copy();
                ghost.setCount(1);
                this.craftMatrix.setItem(i, ghost);
                return true;
            }
        }

        return false;
    }
}
