package org.trp.shincolle.event

import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.*
import org.trp.shincolle.Shincolle
import org.trp.shincolle.block.entity.IWaypoint
import org.trp.shincolle.client.particle.ParticleTeam
import org.trp.shincolle.client.particle.ParticleTeam.RenderStyle
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.init.ModParticles
import org.trp.shincolle.item.PointerItem
import java.util.function.Predicate

@EventBusSubscriber(modid = Shincolle.MODID, value = [Dist.CLIENT])
object ClientPointerItemParticles {
    private const val PARTICLE_INTERVAL_TICKS = 10
    private const val SEARCH_RADIUS = 100.0
    private const val CACHE_TICKS = 5
    private const val CACHE_POSITION_THRESHOLD_SQR = 64.0 // 8 blocks

    private var cachedOwnedShips: List<EntityShipBase> = emptyList()
    private var cachedSelectedShips: List<EntityShipBase> = emptyList()
    private var cachedAtTick: Long = -1
    private var cachedPlayerPos: Vec3 = Vec3.ZERO

    @JvmStatic
    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent.Post?) {
        val minecraft = Minecraft.getInstance()
        val player: Player? = minecraft.player
        val level: Level? = minecraft.level

        if (player == null || level == null) {
            ParticleTeam.clearAllFollowParticles()
            return
        }

        val pointerStack = getPointerStack(player)
        if (pointerStack.isEmpty()) {
            ParticleTeam.clearAllFollowParticles()
            return
        }

        val pointerMode = getPointerMode(pointerStack)
        val ships = getCachedOwnedShips(level, player)
        if (ships.isEmpty()) {
            ParticleTeam.clearFollowParticles(ParticleTeam.FollowKind.SHIP_MARKER, null)
            return
        }

        val isIntervalTick = level.gameTime % PARTICLE_INTERVAL_TICKS == 0L
        val activeShipIds: MutableSet<Int?> = HashSet()

        for (ship in ships) {
            activeShipIds.add(ship.getId())

            val groupMode = pointerMode == PointerItem.MODE_GROUP
            val formationMode = pointerMode == PointerItem.MODE_FORMATION
            val selectedStyle = if (groupMode)
                RenderStyle.SELECTED_RED
            else
                (if (formationMode) RenderStyle.SELECTED_YELLOW else RenderStyle.DEFAULT_BLUE)
            val desiredStyle = if (ship.isPointerSelected)
                selectedStyle
            else
                RenderStyle.DEFAULT_GREEN
            val existing = ParticleTeam.getFollowParticle(ParticleTeam.FollowKind.SHIP_MARKER, ship.getId())
            val styleMismatch =
                existing == null || !existing.isAliveParticle || existing.renderStyle != desiredStyle

            if (isIntervalTick || styleMismatch) {
                spawnShipMarker(level, ship, pointerMode)
            }

            handleShipTargetParticles(level, ship)
        }

        if (isIntervalTick) {
            ParticleTeam.clearFollowParticles(ParticleTeam.FollowKind.SHIP_MARKER, activeShipIds)
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onPointerItemUse(event: RightClickItem) {
        handlePointerTargetMarker(event.getLevel(), event.getEntity())
    }

    @JvmStatic
    @SubscribeEvent
    fun onPointerItemUse(event: RightClickBlock) {
        handlePointerTargetMarker(event.getLevel(), event.getEntity())
    }

    @JvmStatic
    @SubscribeEvent
    fun onPointerItemUse(event: EntityInteract) {
        handlePointerEntityMarker(event.getLevel(), event.getEntity(), event.getTarget())
    }

    private fun isHoldingPointerItem(player: Player): Boolean {
        return !getPointerStack(player).isEmpty()
    }

    private fun getPointerStack(player: Player): ItemStack {
        val main = player.getMainHandItem()
        if (isPointerItem(main)) {
            return main
        }
        val off = player.getOffhandItem()
        if (isPointerItem(off)) {
            return off
        }
        return ItemStack.EMPTY
    }

    private fun isPointerItem(stack: ItemStack): Boolean {
        return !stack.isEmpty() && stack.`is`(ModItems.POINTER_ITEM.get())
    }

    private fun getPointerMode(stack: ItemStack): Int {
        val item = stack.getItem()
        if (item is PointerItem) {
            return item.getMode(stack)
        }
        return PointerItem.MODE_SINGLE
    }

    private fun getCachedOwnedShips(level: Level, player: Player): List<EntityShipBase> {
        val gameTime = level.gameTime
        val playerPos = player.position()
        if (cachedAtTick < 0
            || gameTime - cachedAtTick >= CACHE_TICKS
            || playerPos.distanceToSqr(cachedPlayerPos) > CACHE_POSITION_THRESHOLD_SQR
        ) {
            val searchArea = player.getBoundingBox().inflate(SEARCH_RADIUS)
            cachedOwnedShips = level.getEntitiesOfClass<EntityShipBase?>(
                EntityShipBase::class.java, searchArea,
                Predicate { ship: EntityShipBase? -> ship != null && ship.isOwnedBy(player) && !ship.isInDeadPose }
            )
            cachedSelectedShips = cachedOwnedShips.filter { it.isPointerSelected }
            cachedAtTick = gameTime
            cachedPlayerPos = playerPos
        }
        return cachedOwnedShips
    }

    private fun getCachedSelectedShips(level: Level, player: Player): List<EntityShipBase> {
        // Ensure the base cache is populated first.
        getCachedOwnedShips(level, player)
        return cachedSelectedShips
    }

    private fun spawnShipMarker(level: Level, ship: EntityShipBase, pointerMode: Int) {
        val baseX = ship.getX()
        val baseY = ship.getY()
        val baseZ = ship.getZ()

        val groupMode = pointerMode == PointerItem.MODE_GROUP
        val formationMode = pointerMode == PointerItem.MODE_FORMATION
        val selectedStyle = if (groupMode)
            RenderStyle.SELECTED_RED
        else
            (if (formationMode) RenderStyle.SELECTED_YELLOW else RenderStyle.DEFAULT_BLUE)
        val desiredStyle = if (ship.isPointerSelected)
            selectedStyle
        else
            RenderStyle.DEFAULT_GREEN
        val existing = ParticleTeam.getFollowParticle(ParticleTeam.FollowKind.SHIP_MARKER, ship.getId())
        if (existing != null) {
            if (existing.isAliveParticle && existing.renderStyle == desiredStyle) {
                return
            }
            ParticleTeam.removeFollowParticle(ParticleTeam.FollowKind.SHIP_MARKER, ship.getId())
        }

        if (ship.isPointerSelected) {
            var type: SimpleParticleType = ModParticles.PARTICLE_TEAM_SELECTED.get()!!
            if (groupMode) type = ModParticles.PARTICLE_TEAM_SELECTED_RED.get()!!
            else if (formationMode) type = ModParticles.PARTICLE_TEAM_SELECTED_YELLOW.get()!!

            level.addParticle(
                type,
                baseX,
                baseY,
                baseZ,
                ship.bbHeight.toDouble(),
                ship.getId().toDouble(),
                ParticleTeam.FollowKind.SHIP_MARKER.markerId.toDouble()
            )
        } else {
            level.addParticle(
                ModParticles.PARTICLE_TEAM.get()!!,
                baseX,
                baseY,
                baseZ,
                ship.bbHeight.toDouble(),
                ship.getId().toDouble(),
                ParticleTeam.FollowKind.SHIP_MARKER.markerId.toDouble()
            )
        }
    }

    private fun handleShipTargetParticles(level: Level, ship: EntityShipBase) {
        var targetPos: Vec3? = null
        var isEntity = false

        if (ship.hasPointerTargetEntity()) {
            val target = ship.pointerTargetEntity
            if (target != null) {
                targetPos = target.position().add(0.0, target.bbHeight * 0.5, 0.0)
                isEntity = true
            }
        } else if (ship.hasPointerTarget()) {
            targetPos = ship.pointerTarget
        }

        if (targetPos != null) {
            val isIntervalTick = level.gameTime % 16 == 0L

            if (isIntervalTick) {
                if (isEntity) {
                    spawnEntityTargetMarker(level, targetPos)
                } else {
                    ClientPointerItemParticles.spawnTargetMarker(
                        level,
                        targetPos,
                        ModParticles.PARTICLE_TEAM_TARGET.get()!!,
                        1.5
                    )
                }

                val start = ship.position().add(0.0, ship.bbHeight * 0.5, 0.0)
                val dir = targetPos.subtract(start)
                val dist = dir.length()
                if (dist > 1.0) {
                    val particle: ParticleType<*> =
                        (if (isEntity) ModParticles.PARTICLE_WAYPOINT_LINE_PURPLE.get()!! else ModParticles.PARTICLE_WAYPOINT_LINE.get()!!)
                    level.addParticle(
                        particle as SimpleParticleType,
                        start.x, start.y, start.z,
                        dir.x, dir.y, dir.z
                    )
                }
            }
        }
    }

    private fun spawnTargetMarker(level: Level, target: Vec3, type: SimpleParticleType, height: Double) {
        level.addParticle(
            type,
            target.x, target.y, target.z,
            height, -1.0, ParticleTeam.FollowKind.NONE.markerId.toDouble()
        )
    }

    private fun spawnEntityTargetMarker(level: Level, target: Vec3) {
        level.addParticle(
            ModParticles.PARTICLE_TEAM_TARGET_ENTITY.get()!!,
            target.x, target.y, target.z,
            1.5, -1.0, ParticleTeam.FollowKind.NONE.markerId.toDouble()
        )
    }

    private fun spawnEntityTargetMarker(level: Level, target: Entity) {
        val existing = ParticleTeam.getFollowParticle(ParticleTeam.FollowKind.TARGET_ENTITY, target.getId())
        if (existing != null) {
            ParticleTeam.removeFollowParticle(ParticleTeam.FollowKind.TARGET_ENTITY, target.getId())
        }
        val pos = target.position()
        level.addParticle(
            ModParticles.PARTICLE_TEAM_TARGET_ENTITY.get()!!,
            pos.x,
            pos.y,
            pos.z,
            target.bbHeight.toDouble(),
            target.getId().toDouble(),
            ParticleTeam.FollowKind.TARGET_ENTITY.markerId.toDouble()
        )
    }

    private fun handlePointerTargetMarker(level: Level?, player: Player?) {
        if (level == null || !level.isClientSide) {
            return
        }

        if (player == null || !isHoldingPointerItem(player)) {
            return
        }

        val ships = getCachedSelectedShips(level, player)
        if (ships.isEmpty()) {
            return
        }

        val entityHit = getLookTargetEntity(player)
        if (entityHit != null) {
            val entity = entityHit.getEntity()
            if (entity !== player && !(entity is EntityShipBase && entity.isOwnedBy(player))) {
                spawnEntityTargetMarker(level, entity)
                return
            }
        }

        val target = getLookTarget(player, level)
        if (target == null) {
            return
        }
        ClientPointerItemParticles.spawnTargetMarker(level, target, ModParticles.PARTICLE_TEAM.get()!!, 1.5)
    }

    private fun handlePointerEntityMarker(level: Level?, player: Player?, target: Entity?) {
        if (level == null || !level.isClientSide) {
            return
        }

        if (player == null || !isHoldingPointerItem(player)) {
            return
        }

        if (target == null) {
            return
        }

        if (target === player) {
            return
        }

        if (target is EntityShipBase && target.isOwnedBy(player)) {
            return
        }

        val ships = getCachedSelectedShips(level, player)
        if (ships.isEmpty()) {
            return
        }

        spawnEntityTargetMarker(level, target)
    }

    private fun getLookTargetEntity(player: Player): EntityHitResult? {
        val range = SEARCH_RADIUS
        val eyePos = player.getEyePosition()
        val look = player.getViewVector(1.0f)
        val end = eyePos.add(look.x * range, look.y * range, look.z * range)
        val searchBox = player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0)
        return ProjectileUtil.getEntityHitResult(
            player.level(), player, eyePos, end, searchBox,
            Predicate { entity: Entity? -> entity != null && !entity.isSpectator() && entity.isPickable() && entity !== player })
    }

    private fun getLookTarget(player: Player, level: Level): Vec3? {
        val range = SEARCH_RADIUS

        val eyePos = player.getEyePosition()
        val look = player.getViewVector(1.0f)

        val end = eyePos.add(look.x * range, look.y * range, look.z * range)

        val hit = level.clip(
            ClipContext(
                eyePos,
                end,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.ANY,
                player
            )
        )

        if (hit.getType() != HitResult.Type.BLOCK) {
            return null
        }

        val pos = hit.getBlockPos()
        if (level.getBlockEntity(pos) is IWaypoint) {
            return Vec3.atBottomCenterOf(pos).add(0.0, 1.0, 0.0)
        }
        return Vec3.atBottomCenterOf(pos).add(0.0, 1.0, 0.0)
    }
}
