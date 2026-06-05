package org.trp.shincolle.menu

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.attachment.AdmiralData
import org.trp.shincolle.item.PointerItem
import org.trp.shincolle.server.PlayerStateService.admiralData

class FormationMenu(containerId: Int, playerInventory: Inventory) :
    AbstractContainerMenu(ModMenus.FORMATION.get(), containerId) {
    @JvmField
    val admiralData: AdmiralData

    constructor(containerId: Int, playerInventory: Inventory, buffer: RegistryFriendlyByteBuf?) : this(
        containerId,
        playerInventory
    )

    init {
        this.admiralData = admiralData(playerInventory.player)






        for (i in 0..2) {
            for (j in 0..8) {
                this.addSlot(Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 1000))
            }
        }
        for (i in 0..8) {
            this.addSlot(Slot(playerInventory, i, 8 + i * 18, 1000))
        }
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun stillValid(player: Player): Boolean {
        if (player.isRemoved || !player.isAlive) {
            return false
        }
        return player.getMainHandItem().getItem() is PointerItem
                || player.getOffhandItem().getItem() is PointerItem
    }
}
