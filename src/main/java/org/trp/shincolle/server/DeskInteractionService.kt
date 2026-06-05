package org.trp.shincolle.server

import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import org.trp.shincolle.block.entity.DeskBlockEntity
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModDataComponents
import org.trp.shincolle.item.DeskItemBook
import org.trp.shincolle.menu.DeskMenu
import org.trp.shincolle.utility.FormationHelper
import java.util.*

object DeskInteractionService {
    @JvmStatic
    fun updateBookState(player: Player?, chapter: Int, page: Int) {
        if (player == null) {
            return
        }
        if (player.containerMenu is DeskMenu) {
            val blockEntity: DeskBlockEntity? = containerMenu.getBlockEntity()
            if (containerMenu.getDeskType() == 0 && blockEntity != null) {
                blockEntity.setBookChap(chapter)
                blockEntity.setBookPage(page)
                return
            }
        }

        var stack = player.getMainHandItem()
        if (stack.getItem() !is DeskItemBook) {
            stack = player.getOffhandItem()
        }

        if (stack.getItem() is DeskItemBook) {
            stack.set<Int?>(ModDataComponents.BOOK_CHAPTER, chapter)
            stack.set<Int?>(ModDataComponents.BOOK_PAGE, page)
        }
    }

    @JvmStatic
    fun updateDeskGui(player: Player?, guiFunc: Int, radarZoom: Int) {
        if (player == null) {
            return
        }
        if (player.containerMenu !is DeskMenu) {
            return
        }

        val blockEntity: DeskBlockEntity? = containerMenu.getBlockEntity()
        if (containerMenu.getDeskType() == 0 && blockEntity != null) {
            blockEntity.setGuiFunc(guiFunc)
            blockEntity.setRadarZoomLv(radarZoom)
            if (guiFunc >= 3 && guiFunc <= 4 && player is ServerPlayer) {
                TeamDiplomacyService.sendDeskDiplomacySync(player)
            }
        }
    }

    @JvmStatic
    fun openOwnedShipFromDesk(player: Player?, shipUuid: UUID) {
        if (player == null) {
            return
        }
        if (player.level() !is ServerLevel) {
            return
        }

        val entity: Entity? = serverLevel.getEntity(shipUuid)
        if (entity is EntityShipBase
            && entity.isOwnedBy(player)
            && entity.isAlive()
            && !entity.isRemoved()
        ) {
            entity.openShipMenu(player)
        }
    }

    fun summonOwnedShipsToDesk(player: Player?, shipUuids: MutableCollection<UUID?>?) {
        if (player == null || (player.level() !is ServerLevel) || (player.containerMenu !is DeskMenu) || containerMenu.getDeskType() != 0 || containerMenu.getBlockEntity() == null || shipUuids == null || shipUuids.isEmpty()) {
            return
        }

        val ownedShips = ArrayList<UUID?>()
        val seen = HashSet<UUID?>()
        for (shipUuid in shipUuids) {
            if (shipUuid == null || !seen.add(shipUuid)) {
                continue
            }
            val entity: Entity? = serverLevel.getEntity(shipUuid)
            if (entity is EntityShipBase && entity.isOwnedBy(player) && entity.isAlive() && !entity.isRemoved() && !entity.isInDeadPose()) {
                ownedShips.add(shipUuid)
            }
        }

        if (!ownedShips.isEmpty()) {
            FormationHelper.applySummonShipsToDesk(player, containerMenu.getBlockEntity().getBlockPos(), ownedShips)
        }
    }
}
