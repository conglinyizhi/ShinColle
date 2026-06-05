package org.trp.shincolle.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.trp.shincolle.init.ModBlockEntities
import org.trp.shincolle.menu.DeskMenu
import org.trp.shincolle.reference.Values
import kotlin.math.max
import kotlin.math.min

class DeskBlockEntity(pos: BlockPos, blockState: BlockState) :
    BlockEntity(ModBlockEntities.DESK.get(), pos, blockState), MenuProvider {
    private var guiFunc = 0
    private var radarZoomLv = 0
    private var bookChap = 0
    private var bookPage = 0

    fun getGuiFunc(): Int {
        return this.guiFunc
    }

    fun setGuiFunc(guiFunc: Int) {
        val next = max(0, min(4, guiFunc))
        if (this.guiFunc == next) {
            return
        }
        this.guiFunc = next
        markForSync()
    }

    fun getRadarZoomLv(): Int {
        return this.radarZoomLv
    }

    fun setRadarZoomLv(radarZoomLv: Int) {
        val next = max(0, min(2, radarZoomLv))
        if (this.radarZoomLv == next) {
            return
        }
        this.radarZoomLv = next
        markForSync()
    }

    fun getBookChap(): Int {
        return this.bookChap
    }

    fun setBookChap(bookChap: Int) {
        val nextChap: Int = clampChapter(bookChap)
        val nextPage: Int = clampPageForChapter(nextChap, this.bookPage)
        if (this.bookChap == nextChap && this.bookPage == nextPage) {
            return
        }
        this.bookChap = nextChap
        this.bookPage = nextPage
        markForSync()
    }

    fun getBookPage(): Int {
        return this.bookPage
    }

    fun setBookPage(bookPage: Int) {
        val next: Int = clampPageForChapter(this.bookChap, bookPage)
        if (this.bookPage == next) {
            return
        }
        this.bookPage = next
        markForSync()
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putInt("guiFunc", this.guiFunc)
        tag.putInt("radarZoom", this.radarZoomLv)
        tag.putInt("bookChap", this.bookChap)
        tag.putInt("bookPage", this.bookPage)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        this.guiFunc = max(0, min(4, tag.getInt("guiFunc")))
        this.radarZoomLv = max(0, min(2, tag.getInt("radarZoom")))
        this.bookChap = clampChapter(tag.getInt("bookChap"))
        this.bookPage = clampPageForChapter(this.bookChap, tag.getInt("bookPage"))
    }

    override fun getDisplayName(): Component {
        return Component.translatable("tile.shincolle.BlockDesk.name")
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu? {
        return DeskMenu(
            containerId,
            playerInventory,
            0,
            this.bookChap,
            this.bookPage,
            this.guiFunc,
            this.radarZoomLv,
            this
        )
    }

    fun markForSync() {
        setChanged()
        if (level != null) {
            level!!.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3)
        }
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener?>? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return saveWithoutMetadata(registries)
    }

    companion object {
        private fun clampChapter(chapter: Int): Int {
            return max(0, min(Values.PageLimit.size - 1, chapter))
        }

        private fun clampPageForChapter(chapter: Int, page: Int): Int {
            val clampedChapter: Int = clampChapter(chapter)
            return max(0, min(Values.PageLimit[clampedChapter], page))
        }
    }
}
