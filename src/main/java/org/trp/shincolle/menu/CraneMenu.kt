package org.trp.shincolle.menu

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.DataSlot
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.items.SlotItemHandler
import org.trp.shincolle.block.entity.CraneBlockEntity
import org.trp.shincolle.entity.base.EntityShipBase
import kotlin.math.min

class CraneMenu(containerId: Int, playerInventory: Inventory, val blockEntity: CraneBlockEntity) :
    AbstractContainerMenu(ModMenus.CRANE_MENU.get(), containerId) {
    private val clientSide: Boolean
    private val level: Level


    private var remainedPower = 0
    private var powerMax = 0
    private var packedBooleans = 0
    private var craneMode = 0
    private var modeItem = 0
    private var modeRedstone = 0
    private var modeLiquid = 0
    private var modeEnergy = 0
    private var shipEntityId = 0
    private var shipTimer = 0

    constructor(containerId: Int, playerInventory: Inventory, buffer: RegistryFriendlyByteBuf) : this(
        containerId,
        playerInventory,
        getBlockEntity(playerInventory, buffer)
    )

    init {
        this.clientSide = playerInventory.player.level().isClientSide
        this.level = playerInventory.player.level()

        if (!this.clientSide && blockEntity.getOwnerUUID() == null) {
            blockEntity.setOwnerUUID(playerInventory.player.getUUID())
            blockEntity.setOwnerName(playerInventory.player.getName().getString())
        }


        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return blockEntity.getRemainedPower()
            }

            override fun set(value: Int) {
                remainedPower = value
            }
        })
        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return blockEntity.getPowerMax()
            }

            override fun set(value: Int) {
                powerMax = value
            }
        })
        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                var packed = 0
                if (blockEntity.isActive()) packed = packed or 1
                if (blockEntity.isCheckMetadata()) packed = packed or 2
                if (blockEntity.isCheckOredict()) packed = packed or 4
                if (blockEntity.isCheckNbt()) packed = packed or 8
                if (blockEntity.isEnabLoad()) packed = packed or 16
                if (blockEntity.isEnabUnload()) packed = packed or 32
                return packed
            }

            override fun set(value: Int) {
                packedBooleans = value
            }
        })
        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return blockEntity.getCraneMode()
            }

            override fun set(value: Int) {
                craneMode = value
            }
        })
        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return blockEntity.getModeItem()
            }

            override fun set(value: Int) {
                modeItem = value
            }
        })
        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return blockEntity.getModeRedstone()
            }

            override fun set(value: Int) {
                modeRedstone = value
            }
        })
        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return blockEntity.getModeLiquid()
            }

            override fun set(value: Int) {
                modeLiquid = value
            }
        })
        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return blockEntity.getModeEnergy()
            }

            override fun set(value: Int) {
                modeEnergy = value
            }
        })
        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return blockEntity.getCraningShipId()
            }

            override fun set(value: Int) {
                shipEntityId = value
            }
        })
        this.addDataSlot(object : DataSlot() {
            override fun get(): Int {
                return blockEntity.getCraningShipTimer()
            }

            override fun set(value: Int) {
                shipTimer = value
            }
        })


        for (i in 0..8) {
            this.addSlot(SlotItemHandler(blockEntity.inventory, i, 8 + i * 18, 65))
        }
        for (i in 0..8) {
            this.addSlot(SlotItemHandler(blockEntity.inventory, i + 9, 8 + i * 18, 96))
        }


        for (i in 0..2) {
            for (j in 0..8) {
                this.addSlot(Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 119 + i * 18))
            }
        }
        for (i in 0..8) {
            this.addSlot(Slot(playerInventory, i, 8 + i * 18, 177))
        }
    }

    override fun stillValid(player: Player): Boolean {
        if (this.blockEntity.getLevel() == null) {
            return false
        }
        if (player.level().getBlockEntity(this.blockEntity.getBlockPos()) !== this.blockEntity) {
            return false
        }
        return player.distanceToSqr(
            blockEntity.getBlockPos().getX() + 0.5,
            blockEntity.getBlockPos().getY() + 0.5,
            blockEntity.getBlockPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        // Legacy crane UI intentionally disables shift-transfer because the first 18 slots
        // are ghost filters rather than real storage destinations.
        return ItemStack.EMPTY
    }

    override fun clicked(slotId: Int, button: Int, clickType: ClickType, player: Player) {
        if (player.level().isClientSide) {
            return
        }

        if (slotId >= 0 && slotId < 18) {
            val held = player.containerMenu.getCarried()
            val slot = this.slots.get(slotId)

            if (!held.isEmpty()) {
                val ghost = held.copy()
                if (button == 1) {
                    ghost.setCount(1)
                    slot.set(ghost)
                    blockEntity.setItemMode(slotId, true)
                } else {
                    val old = slot.getItem()
                    if (ItemStack.isSameItem(ghost, old)) {
                        ghost.setCount(min(ghost.getMaxStackSize(), ghost.getCount() + old.getCount()))
                    }
                    slot.set(ghost)
                    blockEntity.setItemMode(slotId, false)
                }
            } else {
                slot.set(ItemStack.EMPTY)
                blockEntity.setItemMode(slotId, false)
            }
            return
        }
        super.clicked(slotId, button, clickType, player)
    }

    override fun clickMenuButton(player: Player, id: Int): Boolean {
        if (player.level().isClientSide) {
            return true
        }

        when (id) {
            0 -> {
                blockEntity.setActive(!blockEntity.isActive())
                return true
            }

            1 -> {
                val m = blockEntity.getCraneMode() + 1
                if (m > 24) m = 24
                blockEntity.setCraneMode(m)
                return true
            }

            11 -> {
                val m2 = blockEntity.getCraneMode() - 1
                if (m2 < 0) m2 = 0
                blockEntity.setCraneMode(m2)
                return true
            }

            2 -> {
                blockEntity.setCheckMetadata(!blockEntity.isCheckMetadata())
                return true
            }

            3 -> {
                blockEntity.setCheckOredict(!blockEntity.isCheckOredict())
                return true
            }

            6 -> {
                blockEntity.setCheckNbt(!blockEntity.isCheckNbt())
                return true
            }

            7 -> {
                val r = (blockEntity.getModeRedstone() + 1) % 3
                blockEntity.setModeRedstone(r)
                return true
            }

            4 -> {
                blockEntity.setEnabLoad(!blockEntity.isEnabLoad())
                return true
            }

            5 -> {
                blockEntity.setEnabUnload(!blockEntity.isEnabUnload())
                return true
            }

            8 -> {
                val l = (blockEntity.getModeLiquid() + 1) % 3
                blockEntity.setModeLiquid(l)
                return true
            }

            9 -> {
                val e = (blockEntity.getModeEnergy() + 1) % 3
                blockEntity.setModeEnergy(e)
                return true
            }
        }
        return super.clickMenuButton(player, id)
    }


    fun getRemainedPower(): Int {
        return if (clientSide) remainedPower else blockEntity.getRemainedPower()
    }

    fun getPowerMax(): Int {
        return if (clientSide) powerMax else blockEntity.getPowerMax()
    }

    val isActive: Boolean
        get() {
            val `val` = if (clientSide) packedBooleans else ((if (blockEntity.isActive()) 1 else 0) or
                    (if (blockEntity.isCheckMetadata()) 2 else 0) or
                    (if (blockEntity.isCheckOredict()) 4 else 0) or
                    (if (blockEntity.isCheckNbt()) 8 else 0) or
                    (if (blockEntity.isEnabLoad()) 16 else 0) or
                    (if (blockEntity.isEnabUnload()) 32 else 0)
                    )
            return (`val` and 1) != 0
        }
    val isCheckMetadata: Boolean
        get() {
            val `val` = if (clientSide) packedBooleans else ((if (blockEntity.isActive()) 1 else 0) or
                    (if (blockEntity.isCheckMetadata()) 2 else 0) or
                    (if (blockEntity.isCheckOredict()) 4 else 0) or
                    (if (blockEntity.isCheckNbt()) 8 else 0) or
                    (if (blockEntity.isEnabLoad()) 16 else 0) or
                    (if (blockEntity.isEnabUnload()) 32 else 0)
                    )
            return (`val` and 2) != 0
        }
    val isCheckOredict: Boolean
        get() {
            val `val` = if (clientSide) packedBooleans else ((if (blockEntity.isActive()) 1 else 0) or
                    (if (blockEntity.isCheckMetadata()) 2 else 0) or
                    (if (blockEntity.isCheckOredict()) 4 else 0) or
                    (if (blockEntity.isCheckNbt()) 8 else 0) or
                    (if (blockEntity.isEnabLoad()) 16 else 0) or
                    (if (blockEntity.isEnabUnload()) 32 else 0)
                    )
            return (`val` and 4) != 0
        }
    val isCheckNbt: Boolean
        get() {
            val `val` = if (clientSide) packedBooleans else ((if (blockEntity.isActive()) 1 else 0) or
                    (if (blockEntity.isCheckMetadata()) 2 else 0) or
                    (if (blockEntity.isCheckOredict()) 4 else 0) or
                    (if (blockEntity.isCheckNbt()) 8 else 0) or
                    (if (blockEntity.isEnabLoad()) 16 else 0) or
                    (if (blockEntity.isEnabUnload()) 32 else 0)
                    )
            return (`val` and 8) != 0
        }
    val isEnabLoad: Boolean
        get() {
            val `val` = if (clientSide) packedBooleans else ((if (blockEntity.isActive()) 1 else 0) or
                    (if (blockEntity.isCheckMetadata()) 2 else 0) or
                    (if (blockEntity.isCheckOredict()) 4 else 0) or
                    (if (blockEntity.isCheckNbt()) 8 else 0) or
                    (if (blockEntity.isEnabLoad()) 16 else 0) or
                    (if (blockEntity.isEnabUnload()) 32 else 0)
                    )
            return (`val` and 16) != 0
        }
    val isEnabUnload: Boolean
        get() {
            val `val` = if (clientSide) packedBooleans else ((if (blockEntity.isActive()) 1 else 0) or
                    (if (blockEntity.isCheckMetadata()) 2 else 0) or
                    (if (blockEntity.isCheckOredict()) 4 else 0) or
                    (if (blockEntity.isCheckNbt()) 8 else 0) or
                    (if (blockEntity.isEnabLoad()) 16 else 0) or
                    (if (blockEntity.isEnabUnload()) 32 else 0)
                    )
            return (`val` and 32) != 0
        }

    fun getCraneMode(): Int {
        return if (clientSide) craneMode else blockEntity.getCraneMode()
    }

    fun getModeItem(): Int {
        return if (clientSide) modeItem else blockEntity.getModeItem()
    }

    fun getItemMode(slotId: Int): Boolean {
        return (getModeItem() and (1 shl slotId)) != 0
    }

    fun getModeRedstone(): Int {
        return if (clientSide) modeRedstone else blockEntity.getModeRedstone()
    }

    fun getModeLiquid(): Int {
        return if (clientSide) modeLiquid else blockEntity.getModeLiquid()
    }

    fun getModeEnergy(): Int {
        return if (clientSide) modeEnergy else blockEntity.getModeEnergy()
    }

    fun getShipEntityId(): Int {
        return if (clientSide) shipEntityId else blockEntity.getCraningShipId()
    }

    fun getShipTimer(): Int {
        return if (clientSide) shipTimer else blockEntity.getCraningShipTimer()
    }

    val shipEntity: EntityShipBase?
        get() {
            val id = getShipEntityId()
            if (id <= 0) {
                return null
            }
            if (level.getEntity(id) is EntityShipBase
                && ship.isAlive()
                && !ship.isRemoved()
            ) {
                return ship
            }
            return null
        }

    companion object {
        private fun getBlockEntity(playerInventory: Inventory, buffer: RegistryFriendlyByteBuf): CraneBlockEntity {
            checkNotNull(buffer) { "Missing crane menu data." }
            val pos = buffer.readBlockPos()
            if (playerInventory.player.level().getBlockEntity(pos) is CraneBlockEntity) {
                return crane
            }
            throw IllegalStateException("Crane block entity not found.")
        }
    }
}
