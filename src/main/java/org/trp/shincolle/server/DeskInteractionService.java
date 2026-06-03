package org.trp.shincolle.server;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.trp.shincolle.block.entity.DeskBlockEntity;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.init.ModDataComponents;
import org.trp.shincolle.item.DeskItemBook;
import org.trp.shincolle.menu.DeskMenu;
import org.trp.shincolle.utility.FormationHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public final class DeskInteractionService {
    private DeskInteractionService() {
    }

    public static void updateBookState(Player player, int chapter, int page) {
        if (player == null) {
            return;
        }
        if (player.containerMenu instanceof DeskMenu deskMenu) {
            DeskBlockEntity blockEntity = deskMenu.getBlockEntity();
            if (deskMenu.getDeskType() == 0 && blockEntity != null) {
                blockEntity.setBookChap(chapter);
                blockEntity.setBookPage(page);
                return;
            }
        }

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof DeskItemBook)) {
            stack = player.getOffhandItem();
        }

        if (stack.getItem() instanceof DeskItemBook) {
            stack.set(ModDataComponents.BOOK_CHAPTER, chapter);
            stack.set(ModDataComponents.BOOK_PAGE, page);
        }
    }

    public static void updateDeskGui(Player player, int guiFunc, int radarZoom) {
        if (player == null) {
            return;
        }
        if (!(player.containerMenu instanceof DeskMenu deskMenu)) {
            return;
        }

        DeskBlockEntity blockEntity = deskMenu.getBlockEntity();
        if (deskMenu.getDeskType() == 0 && blockEntity != null) {
            blockEntity.setGuiFunc(guiFunc);
            blockEntity.setRadarZoomLv(radarZoom);
            if (guiFunc >= 3 && guiFunc <= 4 && player instanceof ServerPlayer serverPlayer) {
                TeamDiplomacyService.sendDeskDiplomacySync(serverPlayer);
            }
        }
    }

    public static void openOwnedShipFromDesk(Player player, UUID shipUuid) {
        if (player == null) {
            return;
        }
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Entity entity = serverLevel.getEntity(shipUuid);
        if (entity instanceof EntityShipBase ship && ship.isOwnedBy(player)) {
            ship.openShipMenu(player);
        }
    }

    public static void summonOwnedShipsToDesk(Player player, Collection<UUID> shipUuids) {
        if (player == null
                || !(player.level() instanceof ServerLevel serverLevel)
                || !(player.containerMenu instanceof DeskMenu deskMenu)
                || deskMenu.getDeskType() != 0
                || deskMenu.getBlockEntity() == null
                || shipUuids == null
                || shipUuids.isEmpty()) {
            return;
        }

        ArrayList<UUID> ownedShips = new ArrayList<>();
        HashSet<UUID> seen = new HashSet<>();
        for (UUID shipUuid : shipUuids) {
            if (shipUuid == null || !seen.add(shipUuid)) {
                continue;
            }
            Entity entity = serverLevel.getEntity(shipUuid);
            if (entity instanceof EntityShipBase ship && ship.isOwnedBy(player) && ship.isAlive() && !ship.isInDeadPose()) {
                ownedShips.add(shipUuid);
            }
        }

        if (!ownedShips.isEmpty()) {
            FormationHelper.applySummonShipsToDesk(player, deskMenu.getBlockEntity().getBlockPos(), ownedShips);
        }
    }
}
