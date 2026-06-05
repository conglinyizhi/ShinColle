package org.trp.shincolle.menu

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.DataSlot
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.SlotItemHandler
import org.trp.shincolle.block.entity.LargeShipyardBlockEntity
import kotlin.math.min

class LargeShipyardMenu(
    containerId: Int,
    playerInventory: Inventory,
    private val blockEntity: LargeShipyardBlockEntity
) : AbstractContainerMenu(ModMenus.LARGE_SHIPYARD_MENU.get(), containerId) {
    private val clientSide: Boolean

    private var buildTypeSynced: Int
    private var selectMatSynced: Int
    private var invModeSynced: Int
    private val matBuildSynced = intArrayOf(0, 0, 0, 0)
    private val matStockSynced = intArrayOf(0, 0, 0, 0)
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
        this.selectMatSynced = blockEntity.getSelectMat()
        this.invModeSynced = blockEntity.getInvMode()
        for (i in 0..3) {
            this.matBuildSynced[i] = blockEntity.getMatBuild(i)
            this.matStockSynced[i] = min(Short.MAX_VALUE.toInt(), blockEntity.getMatStock(i))
        }
        this.powerScaleSynced = blockEntity.getPowerRemainingScaled(64)
        this.hasMaterialSynced = if (blockEntity.getPowerGoal() > 0) 1 else 0
        this.hasPowerSynced = if (blockEntity.hasRemainedPower()) 1 else 0
        this.remainingSecondsSynced = blockEntity.getRemainingTimeSeconds()
        this.powerRemainedSynced = blockEntity.getPowerRemained()
        this.powerRemainedLowSynced = this.powerRemainedSynced and 0xFFFF
        this.powerRemainedHighSynced = (this.powerRemainedSynced ushr 16) and 0xFFFF

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return this@LargeShipyardMenu.blockEntity.getBuildType()
            }

            override fun set(value: Int) {
                this@LargeShipyardMenu.buildTypeSynced = value
            }
        })

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return this@LargeShipyardMenu.blockEntity.getSelectMat()
            }

            override fun set(value: Int) {
                this@LargeShipyardMenu.selectMatSynced = value
            }
        })

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return this@LargeShipyardMenu.blockEntity.getInvMode()
            }

            override fun set(value: Int) {
                this@LargeShipyardMenu.invModeSynced = value
            }
        })

        for (i in 0..3) {
            val idx = i
            this.addDataSlot(object : DataSlot() {
                override fun get(): Int {
                    return this@LargeShipyardMenu.blockEntity.getMatBuild(idx)
                }

                override fun set(value: Int) {
                    this@LargeShipyardMenu.matBuildSynced[idx] = value
                }
            })
        }

        for (i in 0..3) {
            val idx = i
            this.addDataSlot(object : DataSlot() {
                override fun get(): Int {
                    return min(Short.MAX_VALUE.toInt(), this@LargeShipyardMenu.blockEntity.getMatStock(idx))
                }

                override fun set(value: Int) {
                    this@LargeShipyardMenu.matStockSynced[idx] = value
                }
            })
        }

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return this@LargeShipyardMenu.blockEntity.getPowerRemainingScaled(64)
            }

            override fun set(value: Int) {
                this@LargeShipyardMenu.powerScaleSynced = value
            }
        })

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return if (this@LargeShipyardMenu.blockEntity.getPowerGoal() > 0) 1 else 0
            }

            override fun set(value: Int) {
                this@LargeShipyardMenu.hasMaterialSynced = value
            }
        })

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return if (this@LargeShipyardMenu.blockEntity.hasRemainedPower()) 1 else 0
            }

            override fun set(value: Int) {
                this@LargeShipyardMenu.hasPowerSynced = value
            }
        })

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return min(Short.MAX_VALUE.toInt(), this@LargeShipyardMenu.blockEntity.getRemainingTimeSeconds())
            }

            override fun set(value: Int) {
                this@LargeShipyardMenu.remainingSecondsSynced = value
            }
        })

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return this@LargeShipyardMenu.blockEntity.getPowerRemained() and 0xFFFF
            }

            override fun set(value: Int) {
                this@LargeShipyardMenu.powerRemainedLowSynced = value and 0xFFFF
                this@LargeShipyardMenu.powerRemainedSynced = combineShortParts(
                    this@LargeShipyardMenu.powerRemainedLowSynced,
                    this@LargeShipyardMenu.powerRemainedHighSynced
                )
            }
        })

        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return (this@LargeShipyardMenu.blockEntity.getPowerRemained() ushr 16) and 0xFFFF
            }

            override fun set(value: Int) {
                this@LargeShipyardMenu.powerRemainedHighSynced = value and 0xFFFF
                this@LargeShipyardMenu.powerRemainedSynced = combineShortParts(
                    this@LargeShipyardMenu.powerRemainedLowSynced,
                    this@LargeShipyardMenu.powerRemainedHighSynced
                )
            }
        })

        this.addSlot(LargeShipyardMenu.OutputSlot(SLOT_OUTPUT, 168, 51))
        for (i in SLOT_IO_START..SLOT_IO_END) {
            this.addSlot(InputSlot(i, 7 + i * 18, 116))
        }

        for (row in 0..2) {
            for (col in 0..8) {
                val index = col + row * 9 + 9
                this.addSlot(Slot(playerInventory, index, 25 + col * 18, 141 + row * 18))
            }
        }

        for (col in 0..8) {
            this.addSlot(Slot(playerInventory, col, 25 + col * 18, 199))
        }
    }

    val buildType: Int
        get() = if (this.clientSide) this.buildTypeSynced else this.blockEntity.getBuildType()

    val selectMat: Int
        get() = if (this.clientSide) this.selectMatSynced else this.blockEntity.getSelectMat()

    val invMode: Int
        get() = if (this.clientSide) this.invModeSynced else this.blockEntity.getInvMode()

    fun getMatBuild(index: Int): Int {
        return if (this.clientSide) this.matBuildSynced[index] else this.blockEntity.getMatBuild(index)
    }

    fun getMatStock(index: Int): Int {
        return if (this.clientSide) this.matStockSynced[index] else this.blockEntity.getMatStock(index)
    }

    val powerScale: Int
        get() = if (this.clientSide) this.powerScaleSynced else this.blockEntity.getPowerRemainingScaled(64)

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
            val success: Boolean

            if (index == SLOT_OUTPUT) {
                success = this.moveItemStackTo(stack, SLOT_IO_START, HOTBAR_END, true)
            } else if (index >= HOTBAR_START) {
                success = this.moveItemStackTo(stack, SLOT_IO_START, PLAYER_INV_END, false)
            } else if (index >= PLAYER_INV_START && index < PLAYER_INV_END) {
                success = this.moveItemStackTo(stack, SLOT_IO_START, TILE_SLOT_END, true)
            } else {
                success = this.moveItemStackTo(stack, PLAYER_INV_START, HOTBAR_END, false)
            }

            if (!success) {
                return ItemStack.EMPTY
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

        val buildType = this.blockEntity.getBuildType()
        if (id == BUTTON_BUILD_SHIP) {
            val next = if (buildType == 1) 3 else (if (buildType == 3) 0 else 1)
            this.blockEntity.setBuildType(next)
            this.broadcastFullState()
            return true
        }
        if (id == BUTTON_BUILD_EQUIP) {
            val next = if (buildType == 2) 4 else (if (buildType == 4) 0 else 2)
            this.blockEntity.setBuildType(next)
            this.broadcastFullState()
            return true
        }
        if (id == BUTTON_TOGGLE_INV_MODE) {
            this.blockEntity.setInvMode(if (this.blockEntity.getInvMode() == 0) 1 else 0)
            this.broadcastFullState()
            return true
        }

        if (id >= BUTTON_SELECT_MAT_0_A && id <= BUTTON_SELECT_MAT_3_A) {
            this.blockEntity.setSelectMat(id - BUTTON_SELECT_MAT_0_A)
            this.broadcastFullState()
            return true
        }
        if (id >= BUTTON_SELECT_MAT_0_B && id <= BUTTON_SELECT_MAT_3_B) {
            this.blockEntity.setSelectMat(id - BUTTON_SELECT_MAT_0_B)
            this.broadcastFullState()
            return true
        }

        if (id >= BUTTON_MAT_AMOUNT_BASE && id < BUTTON_MAT_AMOUNT_BASE + 8) {
            this.blockEntity.moveBuildMaterialAmount(this.blockEntity.getSelectMat(), id - BUTTON_MAT_AMOUNT_BASE)
            this.broadcastFullState()
            return true
        }

        return super.clickMenuButton(player, id)
    }

    private inner class InputSlot(slot: Int, x: Int, y: Int) :
        SlotItemHandler(this@LargeShipyardMenu.blockEntity.inventory, slot, x, y) {
        override fun mayPlace(stack: ItemStack): Boolean {
            return this.getSlotIndex() != SLOT_OUTPUT
        }
    }

    private inner class OutputSlot(slot: Int, x: Int, y: Int) :
        SlotItemHandler(this@LargeShipyardMenu.blockEntity.inventory, slot, x, y) {
        override fun mayPlace(stack: ItemStack): Boolean {
            return false
        }
    }

    companion object {
        const val BUTTON_BUILD_SHIP: Int = 0
        const val BUTTON_BUILD_EQUIP: Int = 1
        const val BUTTON_TOGGLE_INV_MODE: Int = 2

        const val BUTTON_SELECT_MAT_0_A: Int = 3
        const val BUTTON_SELECT_MAT_1_A: Int = 4
        const val BUTTON_SELECT_MAT_2_A: Int = 5
        const val BUTTON_SELECT_MAT_3_A: Int = 6

        const val BUTTON_SELECT_MAT_0_B: Int = 7
        const val BUTTON_SELECT_MAT_1_B: Int = 8
        const val BUTTON_SELECT_MAT_2_B: Int = 9
        const val BUTTON_SELECT_MAT_3_B: Int = 10

        const val BUTTON_MAT_AMOUNT_BASE: Int = 20

        private const val TILE_SLOT_COUNT = 10
        private const val TILE_SLOT_START = 0
        private val TILE_SLOT_END: Int = TILE_SLOT_START + TILE_SLOT_COUNT

        private const val SLOT_OUTPUT = 0
        private const val SLOT_IO_START = 1
        private const val SLOT_IO_END = 9

        private val PLAYER_INV_START: Int = TILE_SLOT_END
        private val PLAYER_INV_END: Int = PLAYER_INV_START + 27
        private val HOTBAR_START: Int = PLAYER_INV_END
        private val HOTBAR_END: Int = HOTBAR_START + 9

        private fun getBlockEntity(
            playerInventory: Inventory,
            buffer: RegistryFriendlyByteBuf
        ): LargeShipyardBlockEntity {
            checkNotNull(buffer) { "Missing large shipyard menu data." }

            val pos = buffer.readBlockPos()
            if (playerInventory.player.level().getBlockEntity(pos) is LargeShipyardBlockEntity) {
                return shipyard
            }

            throw IllegalStateException("Large shipyard block entity not found.")
        }

        private fun combineShortParts(low: Int, high: Int): Int {
            return ((high and 0xFFFF) shl 16) or (low and 0xFFFF)
        }
    }
}
