package org.trp.shincolle.network

import java.util.*
import java.util.function.IntFunction
import kotlin.math.min

object DeskDiplomacySync {
    var ownerUuid: UUID? = null
        private set
    private val allies: MutableSet<UUID?> = HashSet<UUID?>()
    private val banned: MutableSet<UUID?> = HashSet<UUID?>()
    private val teamNames: MutableMap<UUID?, String?> = HashMap<UUID?, String?>()
    private val leaderNames: MutableMap<UUID?, String?> = HashMap<UUID?, String?>()

    fun update(
        owner: UUID?,
        nextAllies: Collection<UUID?>,
        nextBanned: Collection<UUID?>,
        displayUuids: Collection<UUID?>,
        displayTeamNames: Collection<String?>,
        displayLeaderNames: Collection<String?>
    ) {
        ownerUuid = owner
        allies.clear()
        banned.clear()
        teamNames.clear()
        leaderNames.clear()
        allies.addAll(nextAllies)
        banned.addAll(nextBanned)

        val uuids: Array<UUID?> = displayUuids.toTypedArray()
        val teams: Array<String?> = displayTeamNames.toTypedArray()
        val leaders: Array<String?> = displayLeaderNames.toTypedArray()
        val count = min(uuids.size, min(teams.size, leaders.size))
        for (i in 0..<count) {
            val uuid = uuids[i]
            if (uuid == null) {
                continue
            }
            teamNames.put(uuid, if (teams[i] == null) "" else teams[i])
            leaderNames.put(uuid, if (leaders[i] == null) "" else leaders[i])
        }
    }

    @JvmStatic
    fun clear() {
        ownerUuid = null
        allies.clear()
        banned.clear()
        teamNames.clear()
        leaderNames.clear()
    }

    @JvmStatic
    fun isAlly(target: UUID?): Boolean {
        return target != null && allies.contains(target)
    }

    @JvmStatic
    fun isBanned(target: UUID?): Boolean {
        return target != null && banned.contains(target)
    }

    @JvmStatic
    fun getAllies(): MutableSet<UUID?> {
        return Collections.unmodifiableSet<UUID?>(allies)
    }

    @JvmStatic
    fun getBanned(): MutableSet<UUID?> {
        return Collections.unmodifiableSet<UUID?>(banned)
    }

    @JvmStatic
    fun getTeamName(target: UUID?): String? {
        return if (target == null) "" else teamNames.getOrDefault(target, "")
    }

    @JvmStatic
    fun getLeaderName(target: UUID?): String? {
        return if (target == null) "" else leaderNames.getOrDefault(target, "")
    }
}
