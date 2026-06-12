package org.trp.shincolle.utility

import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.entity.base.EntityShipBase
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate

/**
 * Helper for caching nearby owned ship lookups on the server.
 *
 * The cache is keyed by dimension, player UUID and search radius. Entries expire
 * after [MAX_CACHE_TICKS] game ticks or when the player moves more than
 * [CACHE_POSITION_THRESHOLD_SQR] blocks. Cached results are also discarded as soon
 * as any returned ship dies or is removed, ensuring callers do not act on stale
 * entities. Critical interactions may bypass the cache via [forceRefresh].
 */
object ShipLookupHelper {
    private const val MAX_CACHE_TICKS = 10L
    private const val CACHE_POSITION_THRESHOLD_SQR = 64.0 // 8 blocks

    private data class CacheKey(
        val levelKey: ResourceKey<Level>?,
        val playerUuid: UUID,
        val radius: Double
    )

    private data class CacheEntry(
        val tick: Long,
        val playerPos: Vec3,
        val ships: List<EntityShipBase>
    )

    private val cache = ConcurrentHashMap<CacheKey, CacheEntry>()

    /**
     * Returns nearby ships owned by [player] within [radius], using the cached
     * result when it is still valid.
     */
    @JvmStatic
    fun nearbyOwnedShips(level: Level, player: Player, radius: Double, tick: Long): List<EntityShipBase> {
        return nearbyOwnedShips(level, player, radius, tick, false)
    }

    /**
     * Returns nearby ships owned by [player] within [radius].
     *
     * @param forceRefresh if true, bypass the cache and perform a fresh scan.
     */
    @JvmStatic
    fun nearbyOwnedShips(
        level: Level,
        player: Player,
        radius: Double,
        tick: Long,
        forceRefresh: Boolean
    ): List<EntityShipBase> {
        if (forceRefresh) {
            return scanAndCache(level, player, radius, tick)
        }

        val key = CacheKey(level.dimension(), player.uuid, radius)
        val entry = cache[key]
        val playerPos = player.position()
        if (entry == null
            || tick - entry.tick > MAX_CACHE_TICKS
            || tick < entry.tick
            || playerPos.distanceToSqr(entry.playerPos) > CACHE_POSITION_THRESHOLD_SQR
            || entry.ships.any { !it.isAlive || it.isRemoved }
        ) {
            return scanAndCache(level, player, radius, tick)
        }
        return entry.ships
    }

    private fun scanAndCache(
        level: Level,
        player: Player,
        radius: Double,
        tick: Long
    ): List<EntityShipBase> {
        val searchArea = player.boundingBox.inflate(radius)
        val ships = level.getEntitiesOfClass<EntityShipBase>(
            EntityShipBase::class.java,
            searchArea,
            Predicate { ship -> ship.isOwnedBy(player) && !ship.isInDeadPose }
        )
        val key = CacheKey(level.dimension(), player.uuid, radius)
        cache[key] = CacheEntry(tick, player.position(), ships)
        return ships
    }

    /**
     * Immediately removes all cached lookups for [playerUuid].
     */
    @JvmStatic
    fun invalidateForPlayer(playerUuid: UUID) {
        cache.keys.removeIf { it.playerUuid == playerUuid }
    }

    /**
     * Immediately removes cached lookups that include [ship] as one of its owner's
     * nearby ships. This should be called when a ship dies or is unloaded.
     */
    @JvmStatic
    fun invalidateForShip(ship: EntityShipBase) {
        val ownerId = ship.ownerUUID ?: return
        invalidateForPlayer(ownerId)
    }

    @JvmStatic
    fun clear() {
        cache.clear()
    }
}
