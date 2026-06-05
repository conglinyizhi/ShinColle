package org.trp.shincolle.inventory

import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.util.Mth
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.ItemStackHandler
import org.trp.shincolle.Shincolle
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.item.LegacyEquipItem

class ShipInventoryHandler(private val ship: EntityShipBase, size: Int) : ItemStackHandler(size) {
    val unlockedExtraPages: Int
        get() = Mth.clamp(
            this.ship.getStateMinor(STATE_MINOR_EQUIP_DRUM),
            0,
            STORAGE_EXTRA_PAGES_MAX
        )

    val accessibleSlotCount: Int
        get() {
            val allowed: Int =
                STORAGE_BASE_SIZE + this.unlockedExtraPages * STORAGE_PAGE_SIZE
            return Mth.clamp(allowed, equipSlotCount, this.getSlots())
        }

    fun isSlotAvailable(slot: Int): Boolean {
        return slot >= 0 && slot < this.accessibleSlotCount
    }

    override fun onContentsChanged(slot: Int) {
        this.ship.onInventoryChanged()
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        if (slot < 0 || stack.isEmpty() || !isSlotAvailable(slot)) {
            return false
        }
        if (slot < equipSlotCount) {
            return stack.`is`(equipItemsTag) || stack.getItem() is LegacyEquipItem
        }
        return true
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (!isSlotAvailable(slot)) {
            return stack
        }
        return super.insertItem(slot, stack, simulate)
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (!isSlotAvailable(slot)) {
            return ItemStack.EMPTY
        }
        return super.extractItem(slot, amount, simulate)
    }

    override fun getSlotLimit(slot: Int): Int {
        if (!isSlotAvailable(slot)) {
            return 0
        }
        return super.getSlotLimit(slot)
    }

    companion object {
        const val equipSlotCount: Int = 6
        private const val STORAGE_BASE_SIZE = 24
        private const val STORAGE_PAGE_SIZE = 18
        private const val STORAGE_EXTRA_PAGES_MAX = 2
        private const val STATE_MINOR_EQUIP_DRUM = 36
        val equipItemsTag: TagKey<Item?> = ItemTags.create(
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "ship_equip_items")
        )
    }
}
