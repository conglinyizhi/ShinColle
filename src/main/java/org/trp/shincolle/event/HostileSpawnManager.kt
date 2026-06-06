package org.trp.shincolle.event

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BiomeTags
import net.minecraft.tags.FluidTags
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.Difficulty
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.init.ModItems
import java.util.*
import java.util.function.Predicate
import kotlin.math.max

internal object HostileSpawnManager {
    private val BOSS_COOLDOWNS: MutableMap<UUID?, Int?> = HashMap<UUID?, Int?>()

    fun tickPlayer(player: Player?) {
        if (player == null || !player.isAlive || player.isSpectator()) {
            return
        }
        if (player.level() !is ServerLevel) {
            return
        }
        val level = player.level() as ServerLevel
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            return
        }

        if ((player.tickCount and 0x7F) == 0) {
            spawnMobShips(level, player)
        }

        spawnBossShips(level, player)
    }

    private fun spawnMobShips(level: ServerLevel, player: Player) {
        if (Config.hostileSpawnRequireRing && !hasMarriageRing(player)) {
            return
        }

        val blockX = Mth.floor(player.getX())
        val blockZ = Mth.floor(player.getZ())
        val playerPos = BlockPos(blockX, player.getBlockY(), blockZ)
        if (!isWaterOrBeachBiome(level, playerPos)) {
            return
        }

        val random = player.getRandom()
        if (countHostileMinions(level) > Config.hostileMobSpawnMax) {
            return
        }

        if (random.nextInt(100) > Config.hostileMobSpawnChancePercent) {
            return
        }

        var groups = max(1, Config.hostileMobSpawnGroups)
        var loop = 30 + groups * 30
        while (groups > 0 && loop > 0) {
            val offX = random.nextInt(30) + 20
            val offZ = random.nextInt(30) + 20
            var spawnX = blockX
            var spawnZ = blockZ

            when (random.nextInt(4)) {
                0 -> {
                    spawnX += offX
                    spawnZ += offZ
                }

                1 -> {
                    spawnX -= offX
                    spawnZ -= offZ
                }

                2 -> {
                    spawnX += offX
                    spawnZ -= offZ
                }

                else -> {
                    spawnX -= offX
                    spawnZ += offZ
                }
            }

            val groundY = level.getSeaLevel() - 2
            if (!isWaterAt(level, spawnX, groundY, spawnZ)) {
                --loop
                continue
            }

            groups--

            val spawnY = getTopWaterHeight(level, spawnX, level.getSeaLevel() - 3, spawnZ)
            val shipMin = max(1, Config.hostileMobSpawnGroupMin)
            var shipNum = shipMin
            val rangeMax = Config.hostileMobSpawnGroupMax - shipMin
            if (rangeMax > 0) {
                shipNum += random.nextInt(rangeMax + 1)
            }

            for (i in 0..<shipNum) {
                val scaleLevel = if (random.nextInt(10) > 7) 1 else 0
                spawnRandomHostileShip(
                    level, random, scaleLevel,
                    spawnX + random.nextDouble(),
                    spawnY + 0.5,
                    spawnZ + random.nextDouble()
                )
            }
            --loop
        }
    }

    private fun spawnBossShips(level: ServerLevel, player: Player) {
        val playerId = player.getUUID()
        var cooldown = BOSS_COOLDOWNS.getOrDefault(playerId, 0)!!

        val playerPos = player.blockPosition()
        val canTickCooldown = isWaterOrBeachBiome(level, playerPos)
                && (!Config.hostileSpawnRequireRing || hasMarriageRing(player))
        if (canTickCooldown) {
            cooldown--
        }

        if (cooldown <= 0 && canTickCooldown) {
            val random = player.getRandom()
            cooldown = Config.hostileBossCooldownTicks

            if (random.nextInt(4) == 0) {
                trySpawnBossFleet(level, player, random)
            }
        }

        BOSS_COOLDOWNS.put(playerId, cooldown)
    }

    private fun trySpawnBossFleet(level: ServerLevel, player: Player, random: RandomSource) {
        val baseX = Mth.floor(player.getX())
        val baseZ = Mth.floor(player.getZ())

        for (i in 0..19) {
            val offX = random.nextInt(32) + 32
            val offZ = random.nextInt(32) + 32
            var spawnX = baseX
            var spawnZ = baseZ

            when (random.nextInt(4)) {
                0 -> {
                    spawnX += offX
                    spawnZ += offZ
                }

                1 -> {
                    spawnX -= offX
                    spawnZ -= offZ
                }

                2 -> {
                    spawnX += offX
                    spawnZ -= offZ
                }

                else -> {
                    spawnX -= offX
                    spawnZ += offZ
                }
            }

            val groundY = level.getSeaLevel() - 2
            if (!isWaterAt(level, spawnX, groundY, spawnZ)) {
                continue
            }

            val spawnY = getTopWaterHeight(level, spawnX, level.getSeaLevel(), spawnZ)
            val search = AABB(
                spawnX - 48.0, spawnY - 48.0, spawnZ - 48.0,
                spawnX + 48.0, spawnY + 48.0, spawnZ + 48.0
            )
            val ships = level.getEntitiesOfClass<EntityShipBase?>(
                EntityShipBase::class.java,
                search,
                Predicate { obj: EntityShipBase? -> obj!!.isHostileShipMob })
            val bossNum = ships.stream().filter { ship: EntityShipBase? -> ship!!.scaleLevel > 1 }.count()
            if (bossNum >= 2) {
                continue
            }

            var spawnedCount = 0
            val bossCount = max(1, Config.hostileSpawnBossCount)
            for (j in 0..<bossCount) {
                val scaleLevel = if (random.nextInt(100) > 65) 3 else 2
                if (spawnRandomHostileShip(
                        level, random, scaleLevel,
                        (spawnX + random.nextInt(3)).toDouble(),
                        spawnY + 0.5,
                        (spawnZ + random.nextInt(3)).toDouble()
                    )
                ) {
                    spawnedCount++
                }
            }

            val minionCount = max(1, Config.hostileSpawnMinionCount)
            for (j in 0..<minionCount) {
                if (spawnRandomHostileShip(
                        level, random, random.nextInt(2),
                        (spawnX + random.nextInt(3)).toDouble(),
                        spawnY + 0.5,
                        (spawnZ + random.nextInt(3)).toDouble()
                    )
                ) {
                    spawnedCount++
                }
            }

            if (spawnedCount > 0) {
                broadcastBossSpawn(level, random, spawnX, spawnY, spawnZ)
            }
            break
        }
    }

    private fun spawnRandomHostileShip(
        level: ServerLevel, random: RandomSource, scaleLevel: Int,
        x: Double, y: Double, z: Double
    ): Boolean {
        val type = rollRandomMobShipType(random)
        val ship: EntityShipBase? = type.create(level)
        if (ship == null) {
            return false
        }

        ship.initializeHostileSpawnState(scaleLevel)
        ship.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f)
        if (!level.noCollision(ship, ship.getBoundingBox())) {
            return false
        }

        return level.addFreshEntity(ship)
    }

    private fun rollRandomMobShipType(random: RandomSource): EntityType<out EntityShipBase?> {
        val ran1 = random.nextInt(100)
        if (ran1 > 75) {
            return when (random.nextInt(3)) {
                1 -> ModEntities.BATTLESHIP_YAMATO.get()
                2 -> when (random.nextInt(4)) {
                    1 -> ModEntities.BB_HIEI.get()
                    2 -> ModEntities.BB_HARUNA.get()
                    3 -> ModEntities.BB_KIRISHIMA.get()
                    else -> ModEntities.BB_KONGOU.get()
                }

                else -> ModEntities.BATTLESHIP_NAGATO.get()
            }!!
        }

        if (ran1 > 45) {
            return when (random.nextInt(3)) {
                1, 2 -> when (random.nextInt(4)) {
                    1 -> ModEntities.CRUISER_TENRYUU.get()
                    2 -> ModEntities.CRUISER_TATSUTA.get()
                    3 -> ModEntities.CRUISER_ATAGO.get()
                    else -> ModEntities.CRUISER_TAKAO.get()
                }

                else -> if (random.nextInt(2) == 1) ModEntities.CARRIER_KAGA.get() else ModEntities.CARRIER_AKAGI.get()
            }!!
        }

        return when (random.nextInt(7)) {
            1 -> ModEntities.DESTROYER_HIBIKI.get()
            2 -> ModEntities.DESTROYER_IKAZUCHI.get()
            3 -> ModEntities.DESTROYER_INAZUMA.get()
            4 -> ModEntities.DESTROYER_SHIMAKAZE.get()
            5 -> ModEntities.SUBM_U511.get()
            6 -> ModEntities.SUBM_RO500.get()
            else -> ModEntities.DESTROYER_AKATSUKI.get()
        }!!
    }

    private fun countHostileMinions(level: ServerLevel): Int {
        var count = 0
        for (entity in level.getAllEntities()) {
            if (entity is EntityShipBase
                && entity.isHostileShipMob
                && entity.scaleLevel < 2
            ) {
                count++
            }
        }
        return count
    }

    private fun getTopWaterHeight(level: ServerLevel, x: Int, startY: Int, z: Int): Int {
        val minY = level.getMinBuildHeight()
        val maxY = level.getMaxBuildHeight() - 1
        var y = Mth.clamp(startY, minY, maxY)

        if (!isWaterAt(level, x, y, z)) {
            return y - 1
        }

        while (y < maxY && isWaterAt(level, x, y + 1, z)) {
            y++
        }

        return y
    }

    private fun isWaterAt(level: ServerLevel, x: Int, y: Int, z: Int): Boolean {
        val pos = BlockPos(x, y, z)
        return level.getFluidState(pos).`is`(FluidTags.WATER)
    }

    private fun isWaterOrBeachBiome(level: ServerLevel, pos: BlockPos): Boolean {
        val biome = level.getBiome(pos)
        return biome.`is`(BiomeTags.IS_OCEAN)
                || biome.`is`(BiomeTags.IS_BEACH)
                || biome.`is`(BiomeTags.IS_RIVER)
    }

    private fun hasMarriageRing(player: Player): Boolean {
        for (stack in player.inventory.items) {
            if (!stack.isEmpty() && stack.`is`(ModItems.MARRIAGE_RING.get())) {
                return true
            }
        }
        for (stack in player.inventory.offhand) {
            if (!stack.isEmpty() && stack.`is`(ModItems.MARRIAGE_RING.get())) {
                return true
            }
        }
        return false
    }

    private fun broadcastBossSpawn(level: ServerLevel, random: RandomSource, x: Int, y: Int, z: Int) {
        if (level.getServer() == null) {
            return
        }

        val text: Component = Component.translatable(
            if (random.nextBoolean())
                "chat.shincolle.bossspawn1"
            else
                "chat.shincolle.bossspawn2"
        )
            .append(Component.literal(" " + x + " " + y + " " + z))
        level.getServer().getPlayerList().broadcastSystemMessage(text, false)
    }
}
