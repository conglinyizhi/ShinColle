package org.trp.shincolle.menu

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.DataSlot
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.SlotItemHandler
import org.trp.shincolle.block.entity.VolCoreBlockEntity

class VolCoreMenu(containerId: Int, playerInventory: Inventory, private val blockEntity: VolCoreBlockEntity) :
    AbstractContainerMenu(ModMenus.VOL_CORE_MENU.get(), containerId) {
    private val clientSide: Boolean

    private var powerSynced: Int
    private var activeSynced: Int

    constructor(containerId: Int, playerInventory: Inventory, buffer: RegistryFriendlyByteBuf) : this(
        containerId,
        playerInventory,
        getBlockEntity(playerInventory, buffer)
    )

    init {
        this.clientSide = playerInventory.player.level().isClientSide

        this.powerSynced = blockEntity.getRemainedPower()
        this.activeSynced = if (blockEntity.isBtnActive()) 1 else 0

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return this@VolCoreMenu.blockEntity.getRemainedPower()
            }

            override fun set(value: Int) {
                this@VolCoreMenu.powerSynced = value
            }
        })

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return if (this@VolCoreMenu.blockEntity.isBtnActive()) 1 else 0
            }

            override fun set(value: Int) {
                this@VolCoreMenu.activeSynced = value
            }
        })

        for (row in 0..2) {
            for (col in 0..2) {
                this.addSlot(SlotItemHandler(blockEntity.inventory, col + row * 3, 62 + col * 18, 19 + row * 18))
            }
        }

        for (row in 0..2) {
            for (col in 0..8) {
                this.addSlot(Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18))
            }
        }

        for (col in 0..8) {
            this.addSlot(Slot(playerInventory, col, 8 + col * 18, 142))
        }
    }

    val remainedPower: Int
        get() = if (clientSide) powerSynced else blockEntity.getRemainedPower()

    val isBtnActive: Boolean
        get() = (if (clientSide) activeSynced else (if (blockEntity.isBtnActive()) 1 else 0)) != 0

    override fun stillValid(player: Player): Boolean {
        if (this.blockEntity.level == null) {
            return false
        }
        if (player.level().getBlockEntity(this.blockEntity.blockPos) !== this.blockEntity) {
            return false
        }
        return player.distanceToSqr(
            blockEntity.blockPos.x + 0.5,
            blockEntity.blockPos.y + 0.5,
            blockEntity.blockPos.z + 0.5
        ) <= 64.0
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        var copied = ItemStack.EMPTY
        val slot = this.slots.get(index)
        if (slot != null && slot.hasItem()) {
            val stack = slot.item
            copied = stack.copy()

            if (index < TILE_SLOT_COUNT) {
                if (!this.moveItemStackTo(stack, PLAYER_INV_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY
                }
            } else {
                if (isValidFuel(stack)) {
                    if (!this.moveItemStackTo(stack, 0, TILE_SLOT_COUNT, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index >= PLAYER_INV_START && index < PLAYER_INV_END) {
                    if (!this.moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index >= HOTBAR_START && index < HOTBAR_END) {
                    if (!this.moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_END, false)) {
                        return ItemStack.EMPTY
                    }
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }

            if (stack.getCount() == copied.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(player, stack)
        }
        return copied
    }

    private fun isValidFuel(stack: ItemStack): Boolean {
        for (slot in 0..<TILE_SLOT_COUNT) {
            if (blockEntity.inventory.isItemValid(slot, stack)) {
                return true
            }
        }
        return false
    }

    override fun clickMenuButton(player: Player, id: Int): Boolean {
        if (player.level().isClientSide) {
            return true
        }

        if (id == 0) {
            blockEntity.setBtnActive(!blockEntity.isBtnActive())
            return true
        }
        return super.clickMenuButton(player, id)
    }

    companion object {
        private const val TILE_SLOT_COUNT = 9
        private val PLAYER_INV_START: Int = TILE_SLOT_COUNT
        private val PLAYER_INV_END: Int = PLAYER_INV_START + 27
        private val HOTBAR_START: Int = PLAYER_INV_END
        private val HOTBAR_END: Int = HOTBAR_START + 9

        private fun getBlockEntity(playerInventory: Inventory, buffer: RegistryFriendlyByteBuf): VolCoreBlockEntity {
            checkNotNull(buffer) { "Missing VolCore menu data." }
            val pos = buffer.readBlockPos()
            val be = playerInventory.player.level().getBlockEntity(pos)
            if (be is VolCoreBlockEntity) {
                return be
            }
            throw IllegalStateException("VolCore block entity not found.")
        }
    }
}
