package org.trp.shincolle.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.trp.shincolle.Config;
import org.trp.shincolle.Shincolle;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PerformanceTrace {
    private static final double NS_PER_MS = 1_000_000.0D;
    private static final Object SERVER_TICK_OWNER = new Object();
    private static final Map<String, LastLog> LAST_LOGS = new HashMap<>();
    private static ServerTickTrace serverTickTrace;

    private PerformanceTrace() {}

    public static boolean enabled() {
        return Config.debugPerformanceLogging;
    }

    public static long now() {
        return System.nanoTime();
    }

    public static long elapsed(long startNanos) {
        return System.nanoTime() - startNanos;
    }

    public static void beginServerTick(int tickCount) {
        if (!enabled()) {
            serverTickTrace = null;
            return;
        }
        serverTickTrace = new ServerTickTrace(tickCount);
    }

    public static void endServerTick() {
        if (!enabled() || serverTickTrace == null) {
            serverTickTrace = null;
            return;
        }
        ServerTickTrace trace = serverTickTrace;
        serverTickTrace = null;
        if (trace.totalNanos >= thresholdNanos(Config.debugPerfSlowServerTickMs)
                && shouldLog(SERVER_TICK_OWNER, "serverTick", trace.tickCount)) {
            Shincolle.perfLog(
                    "SlowServerTick tick={} shincolleMs={} shipMs={} shipCalls={} taskMs={} taskCalls={} blockEntityMs={} blockEntityCalls={} projectileMs={} projectileCalls={} otherMs={}",
                    trace.tickCount,
                    formatMs(trace.totalNanos),
                    formatMs(trace.shipNanos), trace.shipCalls,
                    formatMs(trace.taskNanos), trace.taskCalls,
                    formatMs(trace.blockEntityNanos), trace.blockEntityCalls,
                    formatMs(trace.projectileNanos), trace.projectileCalls,
                    formatMs(trace.otherNanos()));
        }
    }

    public static void addShipTime(long nanos) {
        if (serverTickTrace != null) {
            serverTickTrace.shipNanos += nanos;
            serverTickTrace.shipCalls++;
            serverTickTrace.totalNanos += nanos;
        }
    }

    public static void addTaskTime(long nanos) {
        if (serverTickTrace != null) {
            serverTickTrace.taskNanos += nanos;
            serverTickTrace.taskCalls++;
        }
    }

    public static void addBlockEntityTime(long nanos) {
        if (serverTickTrace != null) {
            serverTickTrace.blockEntityNanos += nanos;
            serverTickTrace.blockEntityCalls++;
            serverTickTrace.totalNanos += nanos;
        }
    }

    public static void addProjectileTime(long nanos) {
        if (serverTickTrace != null) {
            serverTickTrace.projectileNanos += nanos;
            serverTickTrace.projectileCalls++;
            serverTickTrace.totalNanos += nanos;
        }
    }

    public static void logSlowShipTick(Entity entity, long elapsedNanos,
                                       long coreNanos, long taskNanos, long supportNanos,
                                       long periodicNanos, String detail) {
        if (!enabled() || elapsedNanos < thresholdNanos(Config.debugPerfSlowShipTickMs)) {
            return;
        }
        long gameTime = gameTime(entity.level());
        if (!shouldLog(entity, "shipTick", gameTime)) {
            return;
        }
        Shincolle.perfLog(
                "SlowShipTick gameTime={} entity={} dim={} pos={} totalMs={} coreMs={} taskMs={} supportMs={} periodicMs={} {}",
                gameTime, describeEntity(entity), dimension(entity.level()), blockPos(entity),
                formatMs(elapsedNanos), formatMs(coreNanos), formatMs(taskNanos),
                formatMs(supportNanos), formatMs(periodicNanos), detail);
    }

    public static void logSlowTaskTick(Entity entity, String taskName, int taskId, long elapsedNanos) {
        if (!enabled() || elapsedNanos < thresholdNanos(Config.debugPerfSlowTaskTickMs)) {
            return;
        }
        long gameTime = gameTime(entity.level());
        if (!shouldLog(entity, "task:" + taskId, gameTime)) {
            return;
        }
        Shincolle.perfLog("SlowTaskTick gameTime={} entity={} dim={} pos={} task={} taskId={} ms={}",
                gameTime, describeEntity(entity), dimension(entity.level()), blockPos(entity),
                taskName, taskId, formatMs(elapsedNanos));
    }

    public static void logTaskStage(Entity entity, String taskName, String stage, long elapsedNanos, String detail) {
        if (!enabled() || elapsedNanos < thresholdNanos(Config.debugPerfSlowTaskTickMs)) {
            return;
        }
        long gameTime = gameTime(entity.level());
        if (!shouldLog(entity, "taskStage:" + taskName + ":" + stage, gameTime)) {
            return;
        }
        Shincolle.perfLog("SlowTaskStage gameTime={} entity={} dim={} pos={} task={} stage={} ms={} {}",
                gameTime, describeEntity(entity), dimension(entity.level()), blockPos(entity),
                taskName, stage, formatMs(elapsedNanos), detail);
    }

    public static void logSlowBlockEntityTick(BlockEntity be, String name, long elapsedNanos, String detail) {
        if (!enabled() || elapsedNanos < thresholdNanos(Config.debugPerfSlowBlockEntityTickMs)) {
            return;
        }
        Level level = be.getLevel();
        long gameTime = gameTime(level);
        if (!shouldLog(be, "blockEntity:" + name, gameTime)) {
            return;
        }
        Shincolle.perfLog("SlowBlockEntityTick gameTime={} type={} dim={} pos={} ms={} {}",
                gameTime, name, dimension(level), be.getBlockPos(), formatMs(elapsedNanos), detail);
    }

    public static void logSlowProjectileTick(Entity entity, String name, long elapsedNanos, String detail) {
        if (!enabled() || elapsedNanos < thresholdNanos(Config.debugPerfSlowProjectileTickMs)) {
            return;
        }
        long gameTime = gameTime(entity.level());
        if (!shouldLog(entity, "projectile:" + name, gameTime)) {
            return;
        }
        Shincolle.perfLog("SlowProjectileTick gameTime={} entity={} dim={} pos={} type={} ms={} {}",
                gameTime, describeEntity(entity), dimension(entity.level()), blockPos(entity),
                name, formatMs(elapsedNanos), detail);
    }

    public static String formatMs(long nanos) {
        return String.format(java.util.Locale.ROOT, "%.3f", nanos / NS_PER_MS);
    }

    private static long thresholdNanos(int millis) {
        return Math.max(1L, millis) * 1_000_000L;
    }

    private static boolean shouldLog(Object owner, String key, long gameTime) {
        String id = ownerKey(owner) + ':' + key;
        LastLog last = LAST_LOGS.get(id);
        int interval = Math.max(1, Config.debugPerfMinLogIntervalTicks);
        if (last != null && gameTime >= last.gameTime && gameTime - last.gameTime < interval) {
            return false;
        }
        LAST_LOGS.put(id, new LastLog(gameTime));
        if (LAST_LOGS.size() > 4096) {
            LAST_LOGS.clear();
        }
        return true;
    }

    private static String ownerKey(Object owner) {
        if (owner instanceof Entity entity) {
            UUID uuid = entity.getUUID();
            return "entity:" + uuid;
        }
        if (owner instanceof BlockEntity be) {
            Level level = be.getLevel();
            return "be:" + dimension(level) + ':' + be.getBlockPos().asLong();
        }
        return "object:" + System.identityHashCode(owner);
    }

    private static String describeEntity(Entity entity) {
        ResourceLocation type = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        String owner = "";
        if (entity instanceof TamableAnimal tamable && tamable.getOwnerUUID() != null) {
            owner = ",owner=" + tamable.getOwnerUUID();
        }
        return type + "[id=" + entity.getId() + ",uuid=" + entity.getUUID() + owner + "]";
    }

    private static String blockPos(Entity entity) {
        BlockPos pos = entity.blockPosition();
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    private static String dimension(Level level) {
        if (level == null) {
            return "unknown";
        }
        return level.dimension().location().toString();
    }

    private static long gameTime(Level level) {
        return level == null ? -1L : level.getGameTime();
    }

    private record LastLog(long gameTime) {}

    private static final class ServerTickTrace {
        private final int tickCount;
        private long totalNanos;
        private long shipNanos;
        private long taskNanos;
        private long blockEntityNanos;
        private long projectileNanos;
        private int shipCalls;
        private int taskCalls;
        private int blockEntityCalls;
        private int projectileCalls;

        private ServerTickTrace(int tickCount) {
            this.tickCount = tickCount;
        }

        private long otherNanos() {
            long known = shipNanos + blockEntityNanos + projectileNanos;
            return Math.max(0L, totalNanos - known);
        }
    }
}
