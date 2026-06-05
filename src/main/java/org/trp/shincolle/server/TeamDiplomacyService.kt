package org.trp.shincolle.server

import com.mojang.authlib.GameProfile
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.network.C2STeamDiplomacyPayload
import org.trp.shincolle.network.S2CDeskDiplomacySyncPayload
import java.util.*

object TeamDiplomacyService {
    @JvmStatic
    fun isDiplomaticAlly(ship: EntityShipBase?, target: Entity?): Boolean {
        if (ship == null || target == null) {
            return false
        }
        if (ship.level() !is ServerLevel) {
            return false
        }
        val serverLevel = ship.level() as ServerLevel
        val owner = ship.ownerUUID
        val targetOwner = PointerInteractionService.getTargetOwnerUUID(target)
        return TeamDiplomacySavedData.Companion.get(serverLevel).areAllies(owner, targetOwner)
    }

    @JvmStatic
    fun isDiplomaticBanned(ship: EntityShipBase?, target: Entity?): Boolean {
        if (ship == null || target == null) {
            return false
        }
        if (ship.level() !is ServerLevel) {
            return false
        }
        val serverLevel = ship.level() as ServerLevel
        val owner = ship.ownerUUID
        val targetOwner = PointerInteractionService.getTargetOwnerUUID(target)
        return TeamDiplomacySavedData.Companion.get(serverLevel).isBanned(owner, targetOwner)
    }

    @JvmStatic
    fun handleAction(player: Player?, action: Int, target: UUID?) {
        if (player == null) {
            return
        }
        if (player.level() !is ServerLevel) {
            return
        }

        val serverLevel = player.level() as ServerLevel
        val owner = player.uuid
        if (target == null || owner == target) {
            return
        }

        val diplomacy: TeamDiplomacySavedData = TeamDiplomacySavedData.Companion.get(serverLevel)
        val targetPlayer: Player? = serverLevel.getPlayerByUUID(target)
        val targetName: Component =
            (targetPlayer?.displayName ?: net.minecraft.network.chat.Component.literal(target.toString()))
        val changed = applyDiplomacyAction(diplomacy, owner, action, target)
        val message: Component?
        when (action) {
            C2STeamDiplomacyPayload.ACTION_ADD_ALLY -> {
                message = if (changed)
                    Component.translatable("chat.shincolle.team.ally_added").append(targetName)
                else
                    Component.translatable("chat.shincolle.team.ally_unchanged").append(targetName)
            }

            C2STeamDiplomacyPayload.ACTION_REMOVE_ALLY -> {
                message = if (changed)
                    Component.translatable("chat.shincolle.team.ally_removed").append(targetName)
                else
                    Component.translatable("chat.shincolle.team.ally_missing").append(targetName)
            }

            C2STeamDiplomacyPayload.ACTION_ADD_BANNED -> {
                message = if (changed)
                    Component.translatable("chat.shincolle.team.hostile_added").append(targetName)
                else
                    Component.translatable("chat.shincolle.team.hostile_unchanged").append(targetName)
            }

            C2STeamDiplomacyPayload.ACTION_REMOVE_BANNED -> {
                message = if (changed)
                    Component.translatable("chat.shincolle.team.hostile_removed").append(targetName)
                else
                    Component.translatable("chat.shincolle.team.hostile_missing").append(targetName)
            }

            else -> {
                return
            }
        }

        player.displayClientMessage(message, false)
        if (changed && player is ServerPlayer) {
            sendDeskDiplomacySync(player)
        }
    }

    fun applyDiplomacyAction(diplomacy: TeamDiplomacySavedData, owner: UUID?, action: Int, target: UUID?): Boolean {
        return when (action) {
            C2STeamDiplomacyPayload.ACTION_ADD_ALLY -> diplomacy.addAlly(owner, target)
            C2STeamDiplomacyPayload.ACTION_REMOVE_ALLY -> diplomacy.removeAlly(owner, target)
            C2STeamDiplomacyPayload.ACTION_ADD_BANNED -> diplomacy.addBanned(owner, target)
            C2STeamDiplomacyPayload.ACTION_REMOVE_BANNED -> diplomacy.removeBanned(owner, target)
            else -> false
        }
    }

    @JvmStatic
    fun sendDeskDiplomacySync(player: ServerPlayer) {
        val diplomacy: TeamDiplomacySavedData = TeamDiplomacySavedData.Companion.get(player.serverLevel())
        updateDiplomacyDisplayData(player, diplomacy)
        val entry = diplomacy.getOrCreate(player.uuid)

        val displayIds = LinkedHashSet<UUID?>()
        displayIds.addAll(entry.allies)
        displayIds.addAll(entry.banned)

        val uuids = ArrayList<UUID?>()
        val teamNames = ArrayList<String?>()
        val leaderNames = ArrayList<String?>()
        for (target in displayIds) {
            if (target == null) {
                continue
            }
            uuids.add(target)
            val targetEntry = diplomacy.get(target)
            teamNames.add(targetEntry?.teamName ?: "")
            var leaderName = targetEntry?.leaderName ?: ""
            if (leaderName.isBlank()) {
                leaderName = resolveDiplomacyLeaderName(player, target)
            }
            leaderNames.add(leaderName)
        }

        PacketDistributor.sendToPlayer(
            player, S2CDeskDiplomacySyncPayload.of(
                player.uuid,
                ArrayList(entry.allies),
                ArrayList(entry.banned),
                uuids,
                teamNames,
                leaderNames
            )
        )
    }

    private fun updateDiplomacyDisplayData(player: ServerPlayer, diplomacy: TeamDiplomacySavedData) {
        val data = PlayerStateService.admiralData(player)
        val teamName = data.getTeamName(data.getCurrentTeamID())
        val leaderName = player.name.string
        diplomacy.setDisplayData(player.uuid, teamName, leaderName)
    }

    private fun resolveDiplomacyLeaderName(player: ServerPlayer, target: UUID?): String {
        if (target == null) {
            return ""
        }
        val onlinePlayer = player.server.playerList.getPlayer(target)
        if (onlinePlayer != null) {
            return onlinePlayer.name.string
        }
        val profileCache = player.server.profileCache
        if (profileCache == null) {
            return ""
        }
        return profileCache.get(target).map { it.name }.orElse("")
    }
}
