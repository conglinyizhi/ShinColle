package org.trp.shincolle.utility

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import org.trp.shincolle.Config
import org.trp.shincolle.Shincolle.Companion.perfLog
import java.util.*
import kotlin.math.max

object PerformanceTrace {
    private const val NS_PER_MS = 1000000.0
    private val SERVER_TICK_OWNER = Any()
    private val LAST_LOGS: MutableMap<String?, LastLog?> = HashMap<String?, LastLog?>()
    private var serverTickTrace: ServerTickTrace? = null

    @JvmStatic
    fun enabled(): Boolean {
        return Config.debugPerformanceLogging
    }

    @JvmStatic
    fun now(): Long {
        return System.nanoTime()
    }

    @JvmStatic
    fun elapsed(startNanos: Long): Long {
        return System.nanoTime() - startNanos
    }

    @JvmStatic
    fun beginServerTick(tickCount: Int) {
        if (!enabled()) {
            serverTickTrace = null
            return
        }
        serverTickTrace = ServerTickTrace(tickCount)
    }

    @JvmStatic
    fun endServerTick() {
        if (!enabled() || serverTickTrace == null) {
            serverTickTrace = null
            return
        }
        val trace = serverTickTrace
        serverTickTrace = null
        if (trace.totalNanos >= thresholdNanos(Config.debugPerfSlowServerTickMs)
            && shouldLog(SERVER_TICK_OWNER, "serverTick", trace.tickCount.toLong())
        ) {
            perfLog(
                "SlowServerTick tick={} shincolleMs={} shipMs={} shipCalls={} taskMs={} taskCalls={} blockEntityMs={} blockEntityCalls={} projectileMs={} projectileCalls={} otherMs={}",
                trace.tickCount,
                formatMs(trace.totalNanos),
                formatMs(trace.shipNanos), trace.shipCalls,
                formatMs(trace.taskNanos), trace.taskCalls,
                formatMs(trace.blockEntityNanos), trace.blockEntityCalls,
                formatMs(trace.projectileNanos), trace.projectileCalls,
                formatMs(trace!!.otherNanos())
            )
        }
    }

    @JvmStatic
    fun addShipTime(nanos: Long) {
        if (serverTickTrace != null) {
            serverTickTrace.shipNanos += nanos
            serverTickTrace.shipCalls++
            serverTickTrace.totalNanos += nanos
        }
    }

    @JvmStatic
    fun addTaskTime(nanos: Long) {
        if (serverTickTrace != null) {
            serverTickTrace.taskNanos += nanos
            serverTickTrace.taskCalls++
        }
    }

    @JvmStatic
    fun addBlockEntityTime(nanos: Long) {
        if (serverTickTrace != null) {
            serverTickTrace.blockEntityNanos += nanos
            serverTickTrace.blockEntityCalls++
            serverTickTrace.totalNanos += nanos
        }
    }

    @JvmStatic
    fun addProjectileTime(nanos: Long) {
        if (serverTickTrace != null) {
            serverTickTrace.projectileNanos += nanos
            serverTickTrace.projectileCalls++
            serverTickTrace.totalNanos += nanos
        }
    }

    @JvmStatic
    fun logSlowShipTick(
        entity: Entity, elapsedNanos: Long,
        coreNanos: Long, taskNanos: Long, supportNanos: Long,
        periodicNanos: Long, detail: String?
    ) {
        if (!enabled() || elapsedNanos < thresholdNanos(Config.debugPerfSlowShipTickMs)) {
            return
        }
        val gameTime = gameTime(entity.level())
        if (!shouldLog(entity, "shipTick", gameTime)) {
            return
        }
        perfLog(
            "SlowShipTick gameTime={} entity={} dim={} pos={} totalMs={} coreMs={} taskMs={} supportMs={} periodicMs={} {}",
            gameTime, describeEntity(entity), dimension(entity.level()), blockPos(entity),
            formatMs(elapsedNanos), formatMs(coreNanos), formatMs(taskNanos),
            formatMs(supportNanos), formatMs(periodicNanos), detail
        )
    }

    @JvmStatic
    fun logSlowTaskTick(entity: Entity, taskName: String?, taskId: Int, elapsedNanos: Long) {
        if (!enabled() || elapsedNanos < thresholdNanos(Config.debugPerfSlowTaskTickMs)) {
            return
        }
        val gameTime = gameTime(entity.level())
        if (!shouldLog(entity, "task:" + taskId, gameTime)) {
            return
        }
        perfLog(
            "SlowTaskTick gameTime={} entity={} dim={} pos={} task={} taskId={} ms={}",
            gameTime, describeEntity(entity), dimension(entity.level()), blockPos(entity),
            taskName, taskId, formatMs(elapsedNanos)
        )
    }

    @JvmStatic
    fun logTaskStage(entity: Entity, taskName: String?, stage: String?, elapsedNanos: Long, detail: String?) {
        if (!enabled() || elapsedNanos < thresholdNanos(Config.debugPerfSlowTaskTickMs)) {
            return
        }
        val gameTime = gameTime(entity.level())
        if (!shouldLog(entity, "taskStage:" + taskName + ":" + stage, gameTime)) {
            return
        }
        perfLog(
            "SlowTaskStage gameTime={} entity={} dim={} pos={} task={} stage={} ms={} {}",
            gameTime, describeEntity(entity), dimension(entity.level()), blockPos(entity),
            taskName, stage, formatMs(elapsedNanos), detail
        )
    }

    @JvmStatic
    fun logSlowBlockEntityTick(be: BlockEntity, name: String?, elapsedNanos: Long, detail: String?) {
        if (!enabled() || elapsedNanos < thresholdNanos(Config.debugPerfSlowBlockEntityTickMs)) {
            return
        }
        val level = be.getLevel()
        val gameTime = gameTime(level)
        if (!shouldLog(be, "blockEntity:" + name, gameTime)) {
            return
        }
        perfLog(
            "SlowBlockEntityTick gameTime={} type={} dim={} pos={} ms={} {}",
            gameTime, name, dimension(level), be.getBlockPos(), formatMs(elapsedNanos), detail
        )
    }

    @JvmStatic
    fun logSlowProjectileTick(entity: Entity, name: String?, elapsedNanos: Long, detail: String?) {
        if (!enabled() || elapsedNanos < thresholdNanos(Config.debugPerfSlowProjectileTickMs)) {
            return
        }
        val gameTime = gameTime(entity.level())
        if (!shouldLog(entity, "projectile:" + name, gameTime)) {
            return
        }
        perfLog(
            "SlowProjectileTick gameTime={} entity={} dim={} pos={} type={} ms={} {}",
            gameTime, describeEntity(entity), dimension(entity.level()), blockPos(entity),
            name, formatMs(elapsedNanos), detail
        )
    }

    @JvmStatic
    fun formatMs(nanos: Long): String {
        return String.format(Locale.ROOT, "%.3f", nanos / NS_PER_MS)
    }

    private fun thresholdNanos(millis: Int): Long {
        return max(1L, millis.toLong()) * 1000000L
    }

    private fun shouldLog(owner: Any?, key: String, gameTime: Long): Boolean {
        val id = ownerKey(owner) + ':' + key
        val last = LAST_LOGS.get(id)
        val interval = max(1, Config.debugPerfMinLogIntervalTicks)
        if (last != null && gameTime >= last.gameTime && gameTime - last.gameTime < interval) {
            return false
        }
        LAST_LOGS.put(id, LastLog(gameTime))
        if (LAST_LOGS.size > 4096) {
            LAST_LOGS.clear()
        }
        return true
    }

    private fun ownerKey(owner: Any?): String {
        if (owner is Entity) {
            val uuid = owner.getUUID()
            return "entity:" + uuid
        }
        if (owner is BlockEntity) {
            val level = owner.getLevel()
            return "be:" + dimension(level) + ':' + owner.getBlockPos().asLong()
        }
        return "object:" + System.identityHashCode(owner)
    }

    private fun describeEntity(entity: Entity): String {
        val type = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType())
        var owner = ""
        if (entity is TamableAnimal && entity.getOwnerUUID() != null) {
            owner = ",owner=" + entity.getOwnerUUID()
        }
        return type.toString() + "[id=" + entity.getId() + ",uuid=" + entity.getUUID() + owner + "]"
    }

    private fun blockPos(entity: Entity): String {
        val pos = entity.blockPosition()
        return pos.getX().toString() + "," + pos.getY() + "," + pos.getZ()
    }

    private fun dimension(level: Level?): String {
        if (level == null) {
            return "unknown"
        }
        return level.dimension().location().toString()
    }

    private fun gameTime(level: Level?): Long {
        return if (level == null) -1L else level.getGameTime()
    }

    @JvmRecord
    private data class LastLog(val gameTime: Long)

    private class ServerTickTrace(private val tickCount: Int) {
        private var totalNanos: Long = 0
        private var shipNanos: Long = 0
        private var taskNanos: Long = 0
        private var blockEntityNanos: Long = 0
        private var projectileNanos: Long = 0
        private var shipCalls = 0
        private var taskCalls = 0
        private var blockEntityCalls = 0
        private var projectileCalls = 0

        fun otherNanos(): Long {
            val known = shipNanos + blockEntityNanos + projectileNanos
            return max(0L, totalNanos - known)
        }
    }
}
