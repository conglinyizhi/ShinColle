package org.trp.shincolle.utility

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import java.util.UUID

/**
 * Helpers for looking up entities by UUID on either side and across dimensions.
 *
 * Centralizes the repeated pattern of "current level lookup → cross-dimension
 * fallback → alive/removed validation" used by guarded/pointer/formation targets.
 */
object EntityLookupHelper {

    /**
     * Looks up an entity by UUID in [level].
     *
     * On the server this queries the level's entity map directly.
     * On the client this scans [ClientLevel.entitiesForRendering].
     */
    @JvmStatic
    fun findEntityByUuid(
        level: Level,
        uuid: UUID,
        predicate: (Entity) -> Boolean = { true }
    ): Entity? {
        return when (level) {
            is ServerLevel -> level.getEntity(uuid)?.takeAlive(predicate)
            is ClientLevel -> level.entitiesForRendering().find { it.uuid == uuid && it.isAliveAndValid(predicate) }
            else -> null
        }
    }

    /**
     * Looks up an entity by UUID across all dimensions on a server.
     *
     * The optional [levelPredicate] can restrict which dimensions are searched,
     * e.g. to match a stored legacy dimension id.
     */
    @JvmStatic
    fun findEntityByUuidCrossDimension(
        server: MinecraftServer,
        uuid: UUID,
        levelPredicate: (Level) -> Boolean = { true },
        entityPredicate: (Entity) -> Boolean = { true }
    ): Entity? {
        for (level in server.allLevels) {
            if (!levelPredicate(level)) continue
            val entity = level.getEntity(uuid)?.takeAlive(entityPredicate)
            if (entity != null) return entity
        }
        return null
    }

    private inline fun Entity.takeAlive(predicate: (Entity) -> Boolean): Entity? {
        return if (this.isAlive && !this.isRemoved && predicate(this)) this else null
    }

    private inline fun Entity.isAliveAndValid(predicate: (Entity) -> Boolean): Boolean {
        return this.isAlive && !this.isRemoved && predicate(this)
    }
}
