package org.trp.shincolle.menu

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.DataSlot
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.SlotItemHandler
import org.trp.shincolle.block.entity.SmallShipyardBlockEntity
import org.trp.shincolle.crafting.ShipyardRecipes
import org.trp.shincolle.init.ModItems
import kotlin.math.min

class SmallShipyardMenu(
    containerId: Int,
    playerInventory: Inventory,
    private val blockEntity: SmallShipyardBlockEntity
) : AbstractContainerMenu(ModMenus.SMALL_SHIPYARD_MENU.get(), containerId) {
    private val clientSide: Boolean

    private var buildTypeSynced: Int
    private var powerScaleSynced: Int
    private var hasMaterialSynced: Int
    private var hasPowerSynced: Int
    private var remainingSecondsSynced: Int
    private var powerRemainedSynced: Int
    private var powerRemainedLowSynced: Int
    private var powerRemainedHighSynced: Int

    constructor(containerId: Int, playerInventory: Inventory, buffer: RegistryFriendlyByteBuf) : this(
        containerId,
        playerInventory,
        getBlockEntity(playerInventory, buffer)
    )

    init {
        this.clientSide = playerInventory.player.level().isClientSide

        this.buildTypeSynced = blockEntity.getBuildType()
        this.powerScaleSynced = blockEntity.getPowerRemainingScaled(31)
        this.hasMaterialSynced = if (blockEntity.getPowerGoal() > 0) 1 else 0
        this.hasPowerSynced = if (blockEntity.hasRemainedPower()) 1 else 0
        this.remainingSecondsSynced = blockEntity.getRemainingTimeSeconds()
        this.powerRemainedSynced = blockEntity.getPowerRemained()
        this.powerRemainedLowSynced = this.powerRemainedSynced and 0xFFFF
        this.powerRemainedHighSynced = (this.powerRemainedSynced ushr 16) and 0xFFFF

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return this@SmallShipyardMenu.blockEntity.getBuildType()
            }

            override fun set(value: Int) {
                this@SmallShipyardMenu.buildTypeSynced = value
            }
        })

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return this@SmallShipyardMenu.blockEntity.getPowerRemainingScaled(31)
            }

            override fun set(value: Int) {
                this@SmallShipyardMenu.powerScaleSynced = value
            }
        })

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return if (this@SmallShipyardMenu.blockEntity.getPowerGoal() > 0) 1 else 0
            }

            override fun set(value: Int) {
                this@SmallShipyardMenu.hasMaterialSynced = value
            }
        })

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return if (this@SmallShipyardMenu.blockEntity.hasRemainedPower()) 1 else 0
            }

            override fun set(value: Int) {
                this@SmallShipyardMenu.hasPowerSynced = value
            }
        })

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return min(Short.MAX_VALUE.toInt(), this@SmallShipyardMenu.blockEntity.getRemainingTimeSeconds())
            }

            override fun set(value: Int) {
                this@SmallShipyardMenu.remainingSecondsSynced = value
            }
        })

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return this@SmallShipyardMenu.blockEntity.getPowerRemained() and 0xFFFF
            }

            override fun set(value: Int) {
                this@SmallShipyardMenu.powerRemainedLowSynced = value and 0xFFFF
                this@SmallShipyardMenu.powerRemainedSynced = combineShortParts(
                    this@SmallShipyardMenu.powerRemainedLowSynced,
                    this@SmallShipyardMenu.powerRemainedHighSynced
                )
            }
        })

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return (this@SmallShipyardMenu.blockEntity.getPowerRemained() ushr 16) and 0xFFFF
            }

            override fun set(value: Int) {
                this@SmallShipyardMenu.powerRemainedHighSynced = value and 0xFFFF
                this@SmallShipyardMenu.powerRemainedSynced = combineShortParts(
                    this@SmallShipyardMenu.powerRemainedLowSynced,
                    this@SmallShipyardMenu.powerRemainedHighSynced
                )
            }
        })

        this.addSlot(MaterialSlot(SLOT_GRUDGE, 33, 29))
        this.addSlot(MaterialSlot(SLOT_ABYSSIUM, 53, 29))
        this.addSlot(MaterialSlot(SLOT_AMMO, 73, 29))
        this.addSlot(MaterialSlot(SLOT_POLYMETAL, 93, 29))
        this.addSlot(MaterialSlot(SLOT_FUEL, 8, 53))
        this.addSlot(SmallShipyardMenu.OutputSlot(SLOT_OUTPUT, 134, 44))

        for (row in 0..2) {
            for (col in 0..8) {
                val index = col + row * 9 + 9
                this.addSlot(Slot(playerInventory, index, 8 + col * 18, 87 + row * 18))
            }
        }

        for (col in 0..8) {
            this.addSlot(Slot(playerInventory, col, 8 + col * 18, 145))
        }
    }

    val buildType: Int
        get() = if (this.clientSide) this.buildTypeSynced else this.blockEntity.getBuildType()

    val powerScale: Int
        get() = if (this.clientSide) this.powerScaleSynced else this.blockEntity.getPowerRemainingScaled(31)

    fun hasMaterial(): Boolean {
        return (if (this.clientSide) this.hasMaterialSynced else (if (this.blockEntity.getPowerGoal() > 0) 1 else 0)) != 0
    }

    fun hasPower(): Boolean {
        return (if (this.clientSide) this.hasPowerSynced else (if (this.blockEntity.hasRemainedPower()) 1 else 0)) != 0
    }

    val powerRemained: Int
        get() = if (this.clientSide) this.powerRemainedSynced else this.blockEntity.getPowerRemained()

    val buildTimeString: String
        get() {
            val seconds =
                if (this.clientSide) this.remainingSecondsSynced else this.blockEntity.getRemainingTimeSeconds()
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val secs = seconds % 60
            return String.format("%02d:%02d:%02d", hours, minutes, secs)
        }

    override fun stillValid(player: Player): Boolean {
        if (this.blockEntity.getLevel() == null) {
            return false
        }
        if (player.level().getBlockEntity(this.blockEntity.getBlockPos()) !== this.blockEntity) {
            return false
        }
        val x = this.blockEntity.getBlockPos().getX() + 0.5
        val y = this.blockEntity.getBlockPos().getY() + 0.5
        val z = this.blockEntity.getBlockPos().getZ() + 0.5
        return player.distanceToSqr(x, y, z) <= 64.0
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        var copied = ItemStack.EMPTY
        val slot = this.slots.get(index)
        if (slot != null && slot.hasItem()) {
            val stack = slot.getItem()
            copied = stack.copy()

            if (index == SLOT_OUTPUT) {
                if (!this.moveItemStackTo(stack, PLAYER_INV_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY
                }
                slot.onQuickCraft(stack, copied)
            } else if (index < TILE_SLOT_END) {
                if (!this.moveItemStackTo(stack, PLAYER_INV_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY
                }
            } else {
                val type = ShipyardRecipes.getSmallMaterialType(stack)
                if (type >= 0 && type <= 3) {
                    if (!this.moveItemStackTo(stack, type, type + 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (type == 4) {
                    if (!this.moveItemStackTo(stack, SLOT_FUEL, SLOT_FUEL + 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (stack.`is`(ModItems.INSTANT_CON_MAT.get())) {
                    if (!this.moveItemStackTo(stack, SLOT_FUEL, SLOT_FUEL + 1, false)) {
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

    override fun clickMenuButton(player: Player, id: Int): Boolean {
        if (player.level().isClientSide) {
            return true
        }

        val current = this.blockEntity.getBuildType()
        val next: Int
        if (id == BUTTON_SHIP) {
            next = if (current == 1) 3 else (if (current == 3) 0 else 1)
            this.blockEntity.setBuildType(next)
            return true
        }
        if (id == BUTTON_EQUIP) {
            next = if (current == 2) 4 else (if (current == 4) 0 else 2)
            this.blockEntity.setBuildType(next)
            return true
        }

        return super.clickMenuButton(player, id)
    }

    private inner class MaterialSlot(slot: Int, x: Int, y: Int) :
        SlotItemHandler(this@SmallShipyardMenu.blockEntity.inventory, slot, x, y) {
        override fun mayPlace(stack: ItemStack): Boolean {
            return when (this.getSlotIndex()) {
                SLOT_GRUDGE -> stack.`is`(ModItems.GRUDGE.get())
                SLOT_ABYSSIUM -> stack.`is`(ModItems.ABYSS_METAL.get())
                SLOT_AMMO -> (stack.`is`(ModItems.AMMO_LIGHT.get())
                        || stack.`is`(ModItems.AMMO_LIGHT_CONTAINER.get())
                        || stack.`is`(ModItems.AMMO_HEAVY.get())
                        || stack.`is`(ModItems.AMMO_HEAVY_CONTAINER.get()))

                SLOT_POLYMETAL -> stack.`is`(ModItems.ABYSS_POLYMETAL.get())
                SLOT_FUEL -> ShipyardRecipes.isFuel(stack) || stack.`is`(ModItems.INSTANT_CON_MAT.get())
                else -> false
            }
        }
    }

    private inner class OutputSlot(slot: Int, x: Int, y: Int) :
        SlotItemHandler(this@SmallShipyardMenu.blockEntity.inventory, slot, x, y) {
        override fun mayPlace(stack: ItemStack): Boolean {
            return false
        }
    }

    companion object {
        const val BUTTON_SHIP: Int = 0
        const val BUTTON_EQUIP: Int = 1

        private const val TILE_SLOT_COUNT = 6
        private const val TILE_SLOT_START = 0
        private val TILE_SLOT_END: Int = TILE_SLOT_START + TILE_SLOT_COUNT

        private const val SLOT_GRUDGE = 0
        private const val SLOT_ABYSSIUM = 1
        private const val SLOT_AMMO = 2
        private const val SLOT_POLYMETAL = 3
        private const val SLOT_FUEL = 4
        private const val SLOT_OUTPUT = 5

        private val PLAYER_INV_START: Int = TILE_SLOT_END
        private val PLAYER_INV_END: Int = PLAYER_INV_START + 27
        private val HOTBAR_START: Int = PLAYER_INV_END
        private val HOTBAR_END: Int = HOTBAR_START + 9

        private fun getBlockEntity(
            playerInventory: Inventory,
            buffer: RegistryFriendlyByteBuf
        ): SmallShipyardBlockEntity {
            checkNotNull(buffer) { "Missing small shipyard menu data." }

            val pos = buffer.readBlockPos()
            if (playerInventory.player.level().getBlockEntity(pos) is SmallShipyardBlockEntity) {
                return shipyard
            }

            throw IllegalStateException("Small shipyard block entity not found.")
        }

        private fun combineShortParts(low: Int, high: Int): Int {
            return ((high and 0xFFFF) shl 16) or (low and 0xFFFF)
        }
    }
}
