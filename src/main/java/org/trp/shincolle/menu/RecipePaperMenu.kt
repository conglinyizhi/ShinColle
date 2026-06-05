package org.trp.shincolle.menu

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.Container
import net.minecraft.world.InteractionHand
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.trp.shincolle.utility.RecipePaperData
import org.trp.shincolle.utility.RecipePaperData.getRecipePreviewResult
import org.trp.shincolle.utility.RecipePaperData.loadRecipeGrid

class RecipePaperMenu(
    id: Int,
    playerInv: Inventory,
    private val hostStack: ItemStack,
    private val hand: InteractionHand
) : AbstractContainerMenu(ModMenus.RECIPE_PAPER_MENU.get(), id) {
    private val craftMatrix: Container = SimpleContainer(9)
    private val craftResult: Container = SimpleContainer(1)
    private val level: Level

    constructor(id: Int, playerInv: Inventory, buf: RegistryFriendlyByteBuf) : this(
        id,
        playerInv,
        buf.readEnum<InteractionHand?>(InteractionHand::class.java)
    )

    private constructor(id: Int, playerInv: Inventory, hand: InteractionHand) : this(
        id,
        playerInv,
        playerInv.player.getItemInHand(hand),
        hand
    )

    init {
        this.level = playerInv.player.level()


        for (i in 0..2) {
            for (j in 0..2) {
                this.addSlot(Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18))
            }
        }


        this.addSlot(object : Slot(this.craftResult, 0, 124, 35) {
            override fun mayPlace(stack: ItemStack): Boolean {
                return false
            }

            override fun mayPickup(player: Player): Boolean {
                return false
            }
        })


        for (i in 0..2) {
            for (j in 0..8) {
                this.addSlot(Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }

        for (i in 0..8) {
            this.addSlot(Slot(playerInv, i, 8 + i * 18, 142))
        }

        loadRecipe()
    }

    private fun loadRecipe() {
        val recipeGrid = loadRecipeGrid(this.hostStack, this.level.registryAccess())
        for (i in recipeGrid.indices) {
            if (!recipeGrid[i]!!.isEmpty()) {
                this.craftMatrix.setItem(i, recipeGrid[i])
            }
        }
        updateResult()
    }

    private fun saveRecipe() {
        val grid: MutableList<ItemStack?> = ArrayList<ItemStack?>(9)
        for (i in 0..8) {
            grid.add(this.craftMatrix.getItem(i))
        }
        RecipePaperData.saveRecipeGrid(this.hostStack, this.level.registryAccess(), grid, this.craftResult.getItem(0))
    }

    private fun updateResult() {
        val inputList: MutableList<ItemStack?> = ArrayList<ItemStack?>()
        for (i in 0..8) {
            inputList.add(craftMatrix.getItem(i))
        }
        this.craftResult.setItem(0, getRecipePreviewResult(this.level, inputList))
    }

    private fun saveRecipeIfServer() {
        if (!this.level.isClientSide) {
            saveRecipe()
        }
    }

    override fun slotsChanged(container: Container) {
        if (container === craftMatrix) {
            updateResult()
            if (!level.isClientSide) {
                saveRecipe()
            }
        }
    }

    override fun removed(player: Player) {
        super.removed(player)
        if (!level.isClientSide) {
            saveRecipe()
        }
    }

    override fun stillValid(player: Player): Boolean {
        return isStillBoundToHostStack(player.getItemInHand(hand), hostStack)
    }

    override fun clicked(slotId: Int, button: Int, clickType: ClickType, player: Player) {
        if (isPreviewResultSlot(slotId)) {
            // Legacy recipe paper uses the output slot as a preview only.
            return
        }
        if (slotId >= 0 && slotId < 9) {
            val slot = slots.get(slotId)
            val cursorStack = getCarried()
            if (!cursorStack.isEmpty()) {
                if (button == 1) {
                    slot.set(ItemStack.EMPTY)
                } else {
                    val copy = cursorStack.copy()
                    copy.setCount(1)
                    slot.set(copy)
                }
            } else {
                slot.set(ItemStack.EMPTY)
            }
            updateResult()
            saveRecipeIfServer()
            return
        }
        super.clicked(slotId, button, clickType, player)
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return ItemStack.EMPTY
    }

    companion object {
        fun isStillBoundToHostStack(currentHandStack: ItemStack?, hostStack: ItemStack?): Boolean {
            return currentHandStack == hostStack
        }

        fun isPreviewResultSlot(slotId: Int): Boolean {
            return slotId == 9
        }
    }
}
