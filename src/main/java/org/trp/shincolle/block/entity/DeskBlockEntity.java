package org.trp.shincolle.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.trp.shincolle.init.ModBlockEntities;
import org.trp.shincolle.menu.DeskMenu;
import org.trp.shincolle.reference.Values;

public class DeskBlockEntity extends BlockEntity implements MenuProvider {
    private int guiFunc = 0;
    private int radarZoomLv = 0;
    private int bookChap = 0;
    private int bookPage = 0;

    public DeskBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.DESK.get(), pos, blockState);
    }

    public int getGuiFunc() {
        return this.guiFunc;
    }

    public void setGuiFunc(int guiFunc) {
        int next = Math.max(0, Math.min(4, guiFunc));
        if (this.guiFunc == next) {
            return;
        }
        this.guiFunc = next;
        markForSync();
    }

    public int getRadarZoomLv() {
        return this.radarZoomLv;
    }

    public void setRadarZoomLv(int radarZoomLv) {
        int next = Math.max(0, Math.min(2, radarZoomLv));
        if (this.radarZoomLv == next) {
            return;
        }
        this.radarZoomLv = next;
        markForSync();
    }

    public int getBookChap() {
        return this.bookChap;
    }

    public void setBookChap(int bookChap) {
        int nextChap = clampChapter(bookChap);
        int nextPage = clampPageForChapter(nextChap, this.bookPage);
        if (this.bookChap == nextChap && this.bookPage == nextPage) {
            return;
        }
        this.bookChap = nextChap;
        this.bookPage = nextPage;
        markForSync();
    }

    public int getBookPage() {
        return this.bookPage;
    }

    public void setBookPage(int bookPage) {
        int next = clampPageForChapter(this.bookChap, bookPage);
        if (this.bookPage == next) {
            return;
        }
        this.bookPage = next;
        markForSync();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("guiFunc", this.guiFunc);
        tag.putInt("radarZoom", this.radarZoomLv);
        tag.putInt("bookChap", this.bookChap);
        tag.putInt("bookPage", this.bookPage);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.guiFunc = Math.max(0, Math.min(4, tag.getInt("guiFunc")));
        this.radarZoomLv = Math.max(0, Math.min(2, tag.getInt("radarZoom")));
        this.bookChap = clampChapter(tag.getInt("bookChap"));
        this.bookPage = clampPageForChapter(this.bookChap, tag.getInt("bookPage"));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("tile.shincolle.BlockDesk.name");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new DeskMenu(containerId, playerInventory, 0, this.bookChap, this.bookPage, this.guiFunc, this.radarZoomLv, this);
    }

    public void markForSync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    private static int clampChapter(int chapter) {
        return Math.max(0, Math.min(Values.PageLimit.length - 1, chapter));
    }

    private static int clampPageForChapter(int chapter, int page) {
        int clampedChapter = clampChapter(chapter);
        return Math.max(0, Math.min(Values.PageLimit[clampedChapter], page));
    }
}
