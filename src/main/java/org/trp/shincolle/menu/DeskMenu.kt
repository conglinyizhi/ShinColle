package org.trp.shincolle.menu

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.DataSlot
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.block.entity.DeskBlockEntity
import org.trp.shincolle.item.DeskItemBook
import org.trp.shincolle.item.DeskItemRadar
import org.trp.shincolle.server.TeamDiplomacyService.sendDeskDiplomacySync

class DeskMenu @JvmOverloads constructor(
    id: Int,
    playerInventory: Inventory,
    @JvmField val deskType: Int,
    @JvmField var chapter: Int = 0,
    @JvmField var page: Int = 0,
    @JvmField var guiFunc: Int = 0,
    @JvmField var radarZoom: Int = 0,
    val blockEntity: DeskBlockEntity? = null
) : AbstractContainerMenu(ModMenus.DESK_MENU.get(), id) {
    private val clientSide: Boolean

    constructor(id: Int, playerInventory: Inventory, data: RegistryFriendlyByteBuf) : this(
        id,
        playerInventory,
        getDeskTypeAndEntity(playerInventory, data)
    )

    private constructor(id: Int, playerInventory: Inventory, typeAndData: Array<Any?>) : this(
        id, playerInventory, typeAndData[0] as Int,
        if (typeAndData.size > 2) typeAndData[1] as Int else 0,
        if (typeAndData.size > 2) typeAndData[2] as Int else 0,
        0, 0,
        if (typeAndData.size == 2) typeAndData[1] as DeskBlockEntity? else null
    )

    init {
        this.clientSide = playerInventory.player.level().isClientSide

        if (this.blockEntity != null) {
            this.chapter = this.blockEntity.getBookChap()
            this.page = this.blockEntity.getBookPage()
            this.guiFunc = this.blockEntity.getGuiFunc()
            this.radarZoom = this.blockEntity.getRadarZoomLv()

            this.addDataSlot(object : DataSlot() {
                override fun get(): Int {
                    return this@DeskMenu.blockEntity.getBookChap()
                }

                override fun set(value: Int) {
                    this@DeskMenu.chapter = value
                }
            })

            this.addDataSlot(object : DataSlot() {
                override fun get(): Int {
                    return this@DeskMenu.blockEntity.getBookPage()
                }

                override fun set(value: Int) {
                    this@DeskMenu.page = value
                }
            })

            this.addDataSlot(object : DataSlot() {
                override fun get(): Int {
                    return this@DeskMenu.blockEntity.getGuiFunc()
                }

                override fun set(value: Int) {
                    this@DeskMenu.guiFunc = value
                }
            })

            this.addDataSlot(object : DataSlot() {
                override fun get(): Int {
                    return this@DeskMenu.blockEntity.getRadarZoomLv()
                }

                override fun set(value: Int) {
                    this@DeskMenu.radarZoom = value
                }
            })

            if (!this.clientSide && this.guiFunc >= 3 && this.guiFunc <= 4 && playerInventory.player is ServerPlayer) {
                sendDeskDiplomacySync(playerInventory.player as ServerPlayer)
            }
        }
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun stillValid(player: Player): Boolean {
        if (blockEntity != null) {
            if (blockEntity.getLevel() == null || player.level()
                    .getBlockEntity(blockEntity.getBlockPos()) !== blockEntity
            ) {
                return false
            }
            return stillValid(
                ContainerLevelAccess.create(blockEntity.getLevel()!!, blockEntity.getBlockPos()),
                player,
                blockEntity.getBlockState().getBlock()
            )
        }
        if (player.isRemoved || !player.isAlive) {
            return false
        }
        return when (this.deskType) {
            1 -> player.getMainHandItem().getItem() is DeskItemRadar
                    || player.getOffhandItem().getItem() is DeskItemRadar

            2 -> player.getMainHandItem().getItem() is DeskItemBook
                    || player.getOffhandItem().getItem() is DeskItemBook

            else -> true
        }
    }

    companion object {
        private fun getDeskTypeAndEntity(playerInventory: Inventory, data: RegistryFriendlyByteBuf): Array<Any?> {
            checkNotNull(data) { "Missing desk menu data." }
            val deskType = data.readInt()
            if (deskType == 0) {
                val pos = data.readBlockPos()
                if (playerInventory.player.level().getBlockEntity(pos) is DeskBlockEntity) {
                    val desk = playerInventory.player.level().getBlockEntity(pos) as DeskBlockEntity
                    return arrayOf<Any?>(deskType, desk)
                }
                throw IllegalStateException("Desk block entity not found.")
            }
            return arrayOf<Any?>(deskType, data.readInt(), data.readInt())
        }
    }
}
