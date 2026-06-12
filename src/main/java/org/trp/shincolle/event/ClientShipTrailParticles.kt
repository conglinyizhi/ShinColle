package org.trp.shincolle.event

import net.minecraft.client.Minecraft
import net.minecraft.client.ParticleStatus
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import org.trp.shincolle.Shincolle
import org.trp.shincolle.entity.base.EntityShipBase

@EventBusSubscriber(modid = Shincolle.MODID, value = [Dist.CLIENT])
object ClientShipTrailParticles {
    private const val SEARCH_RADIUS = 128.0
    private const val CACHE_TICKS = 5
    private const val CACHE_POSITION_THRESHOLD_SQR = 64.0 // 8 blocks
    private const val WATER_TRAIL_SPEED_CLAMP = 0.25
    private const val WATER_TRAIL_MIN_SPEED_SQR = 0.001
    private const val WATER_TRAIL_OFFSET_Y = 0.4
    private const val WATER_TRAIL_DISTANCE_MULT = 3.0
    private const val WATER_TRAIL_SPREAD_Y = 0.15
    private const val WATER_TRAIL_MOTION_SCALE = 1.5
    private const val HEALTH_PARTICLE_INTERVAL = 16
    private const val HEALTH_PARTICLE_OFFSET_Y = 0.7
    private const val HEALTH_PARTICLE_UP_SPEED = 0.05

    private var cachedShips: List<EntityShipBase> = emptyList()
    private var cachedAtTick: Long = -1
    private var cachedPlayerPos: Vec3 = Vec3.ZERO

    @JvmStatic
    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent.Post?) {
        val minecraft = Minecraft.getInstance()
        val player: Player? = minecraft.player
        val level: Level? = minecraft.level

        if (player == null || level == null) {
            return
        }

        val maxParticles = getMaxTrailParticles(minecraft)
        if (maxParticles <= 0) {
            return
        }

        val ships = getCachedShips(level, player)
        if (ships.isEmpty()) {
            return
        }

        val skipHealthParticles = minecraft.options.particles().get() == ParticleStatus.MINIMAL
        for (ship in ships) {
            spawnShipTrail(level, ship, maxParticles)
            if (!skipHealthParticles) {
                spawnShipHealthParticles(level, ship)
            }
        }
    }

    private fun getCachedShips(level: Level, player: Player): List<EntityShipBase> {
        val gameTime = level.gameTime
        val playerPos = player.position()
        if (cachedAtTick < 0
            || gameTime - cachedAtTick >= CACHE_TICKS
            || playerPos.distanceToSqr(cachedPlayerPos) > CACHE_POSITION_THRESHOLD_SQR
        ) {
            val searchArea = player.boundingBox.inflate(SEARCH_RADIUS)
            cachedShips = level.getEntitiesOfClass<EntityShipBase?>(EntityShipBase::class.java, searchArea)
                .filterNotNull()
            cachedAtTick = gameTime
            cachedPlayerPos = playerPos
        }
        return cachedShips
    }

    private fun getMaxTrailParticles(minecraft: Minecraft): Int {
        if (minecraft.options == null) {
            return 0
        }

        val status = minecraft.options.particles().get()
        val setting = when (status) {
            ParticleStatus.ALL -> 0
            ParticleStatus.DECREASED -> 1
            ParticleStatus.MINIMAL -> 2
        }

        return ((3 - setting) * 1.8f).toInt()
    }

    private fun spawnShipTrail(level: Level, ship: EntityShipBase, maxParticles: Int) {
        if (ship.isPassenger() || ship.shipDepth <= 0.0) {
            return
        }

        val delta = ship.deltaMovement
        val motX = Mth.clamp(delta.x, -WATER_TRAIL_SPEED_CLAMP, WATER_TRAIL_SPEED_CLAMP)
        val motZ = Mth.clamp(delta.z, -WATER_TRAIL_SPEED_CLAMP, WATER_TRAIL_SPEED_CLAMP)
        if (motX * motX + motZ * motZ <= WATER_TRAIL_MIN_SPEED_SQR) {
            return
        }

        val px = ship.x + motX * WATER_TRAIL_DISTANCE_MULT
        val py = ship.y + WATER_TRAIL_OFFSET_Y
        val pz = ship.z + motZ * WATER_TRAIL_DISTANCE_MULT

        val width = ship.getBbWidth().toDouble()
        val velX = Mth.clamp(-motX * WATER_TRAIL_MOTION_SCALE, -WATER_TRAIL_SPEED_CLAMP, WATER_TRAIL_SPEED_CLAMP)
        val velZ = Mth.clamp(-motZ * WATER_TRAIL_MOTION_SCALE, -WATER_TRAIL_SPEED_CLAMP, WATER_TRAIL_SPEED_CLAMP)
        val random = ship.random

        for (i in 0..<maxParticles) {
            val ox = (random.nextDouble() - 0.5) * width
            val oy = (random.nextDouble() - 0.5) * width * WATER_TRAIL_SPREAD_Y - 0.1f
            val oz = (random.nextDouble() - 0.5) * width
            level.addParticle(ParticleTypes.CLOUD, px + ox, py + oy, pz + oz, velX, 0.0, velZ)
        }
    }

    private fun spawnShipHealthParticles(level: Level, ship: EntityShipBase) {
        if ((ship.tickCount and (HEALTH_PARTICLE_INTERVAL - 1)) != 0) {
            return
        }

        val healthRatio = ship.health / ship.maxHealth
        if (healthRatio > 0.75f) {
            return
        }

        val baseX = ship.x
        val baseY = ship.y + HEALTH_PARTICLE_OFFSET_Y
        val baseZ = ship.z
        val spread = ship.getBbWidth().toDouble()
        val random = ship.random

        if (healthRatio > 0.5f) {
            spawnSmokeNormal(level, random, baseX, baseY, baseZ, spread, 3, false)
        } else if (healthRatio > 0.25f) {
            spawnSmokeNormal(level, random, baseX, baseY, baseZ, spread, 3, true)
        } else {
            spawnSmokeLarge(level, random, baseX, baseY, baseZ, spread, 4, true)
        }
    }


    private fun spawnSmokeNormal(
        level: Level, random: RandomSource, baseX: Double, baseY: Double, baseZ: Double,
        spread: Double, count: Int, withFlame: Boolean
    ) {
        for (i in 0..<count) {
            val ranX = random.nextDouble() * spread - spread / 2.0
            val ranY = random.nextDouble() * spread - spread / 2.0
            val ranZ = random.nextDouble() * spread - spread / 2.0
            level.addParticle(
                ParticleTypes.SMOKE, baseX + ranX, baseY + ranY, baseZ + ranZ, 0.0,
                HEALTH_PARTICLE_UP_SPEED, 0.0
            )
            if (withFlame) {
                level.addParticle(
                    ParticleTypes.FLAME, baseX + ranZ, baseY + ranY, baseZ + ranX, 0.0,
                    HEALTH_PARTICLE_UP_SPEED, 0.0
                )
            }
        }
    }

    private fun spawnSmokeLarge(
        level: Level, random: RandomSource, baseX: Double, baseY: Double, baseZ: Double,
        spread: Double, count: Int, withFlame: Boolean
    ) {
        for (i in 0..<count) {
            val ranX = random.nextDouble() * spread - spread / 2.0
            val ranY = random.nextDouble() * spread - spread / 2.0
            val ranZ = random.nextDouble() * spread - spread / 2.0
            level.addParticle(
                ParticleTypes.LARGE_SMOKE, baseX + ranX, baseY + ranY, baseZ + ranZ, 0.0,
                0.0, 0.0
            )
            if (withFlame) {
                level.addParticle(
                    ParticleTypes.FLAME, baseX + ranZ, baseY + ranY, baseZ + ranX, 0.0,
                    HEALTH_PARTICLE_UP_SPEED, 0.0
                )
            }
        }
    }
}
