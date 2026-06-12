package org.trp.shincolle.entity.base.tick

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.Config
import org.trp.shincolle.Config.ShipCustomSoundType.Companion.timeKeeping
import org.trp.shincolle.block.entity.CraneBlockEntity
import org.trp.shincolle.block.entity.IWaypoint
import org.trp.shincolle.block.entity.WayPointBlockEntity
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.base.ShipGuardTarget
import org.trp.shincolle.init.ModSounds
import org.trp.shincolle.menu.ShipContainerMenu
import org.trp.shincolle.server.PlayerStateService
import org.trp.shincolle.server.ShipRegistrySavedData.Companion.get
import org.trp.shincolle.server.TemporaryLightService
import kotlin.math.max

/**
 * Periodic synchronization and environmental effects: searchlight, compass chunk
 * loading, time-keeping voice, legacy timers, formation membership, waypoint
 * movement and legacy stat recalculation.
 */
object ShipPeriodicSyncTickHandler : ShipTickHandler {

    override fun tick(ship: EntityShipBase): Boolean {
        tickSearchlightAssist(ship)
        tickCompassChunkLoading(ship)
        tickTimeKeepingSound(ship)

        tickLegacyTimers(ship)
        if ((ship.tickCount % 20) == 0) {
            syncFormationMembershipFromOwnerData(ship)
        }
        if ((ship.tickCount % 16) == 0) {
            tickWaypointMove(ship)
        }
        if ((ship.tickCount % 40) == 0) {
            ship.recalculateLegacyShipStats()
        }
        if ((ship.tickCount % 40) == 0 && ship.level() is ServerLevel) {
            val serverLevel = ship.level() as ServerLevel
            get(serverLevel).updateShip(ship)
        }

        return true
    }

    fun clearCompassChunks(ship: EntityShipBase) {
        if (ship.level() is ServerLevel) {
            clearCompassForcedChunks(ship, ship.level() as ServerLevel)
        }
    }

    private fun tickSearchlightAssist(ship: EntityShipBase) {
        if ((ship.tickCount % EntityShipBase.SEARCHLIGHT_INTERVAL_TICKS) != 0) {
            return
        }
        if (ship.getStateMinor(EntityShipBase.STATE_MINOR_EQUIP_SEARCHLIGHT) <= 0 || !ship.isAlive) {
            return
        }
        if (ship.level() !is ServerLevel) {
            return
        }
        val serverLevel = ship.level() as ServerLevel
        if (ship.level().getMaxLocalRawBrightness(ship.blockPosition()) >= 10) {
            return
        }

        ship.addEffect(
            MobEffectInstance(
                MobEffects.NIGHT_VISION,
                EntityShipBase.SPECIAL_EQUIP_SEARCHLIGHT_NIGHT_VISION_TICKS, 0, true, false, false
            )
        )
        TemporaryLightService.refreshLight(serverLevel, ship.blockPosition(), ship.uuid)
        serverLevel.sendParticles<SimpleParticleType?>(
            ParticleTypes.END_ROD,
            ship.x, ship.y + 1.2, ship.z,
            3, 0.25, 0.35, 0.25, 0.01
        )
    }

    private fun tickCompassChunkLoading(ship: EntityShipBase) {
        if (ship.level() !is ServerLevel) {
            return
        }
        val serverLevel = ship.level() as ServerLevel

        if (!ship.isAlive || ship.getStateMinor(EntityShipBase.STATE_MINOR_EQUIP_COMPASS) <= 0) {
            clearCompassForcedChunks(ship, serverLevel)
            return
        }

        val chunkPos = ship.chunkPosition()
        val movedChunk = chunkPos.x != ship.forcedCompassChunkCenterX || chunkPos.z != ship.forcedCompassChunkCenterZ
        if (!movedChunk && (ship.tickCount % EntityShipBase.COMPASS_CHUNK_REFRESH_INTERVAL_TICKS) != 0) {
            return
        }

        ship.forcedCompassChunkCenterX = chunkPos.x
        ship.forcedCompassChunkCenterZ = chunkPos.z
        updateCompassForcedChunks(ship, serverLevel, chunkPos.x, chunkPos.z)
    }

    private fun updateCompassForcedChunks(ship: EntityShipBase, serverLevel: ServerLevel, centerX: Int, centerZ: Int) {
        val desired: MutableSet<Long> = HashSet()
        for (dx in -EntityShipBase.COMPASS_CHUNK_RADIUS..EntityShipBase.COMPASS_CHUNK_RADIUS) {
            for (dz in -EntityShipBase.COMPASS_CHUNK_RADIUS..EntityShipBase.COMPASS_CHUNK_RADIUS) {
                val cx = centerX + dx
                val cz = centerZ + dz
                val key = ChunkPos.asLong(cx, cz)
                desired.add(key)
                if (!ship.forcedCompassChunks.contains(key)) {
                    serverLevel.setChunkForced(cx, cz, true)
                }
            }
        }

        if (ship.forcedCompassChunks.isNotEmpty()) {
            for (key in HashSet(ship.forcedCompassChunks)) {
                if (desired.contains(key)) {
                    continue
                }
                serverLevel.setChunkForced(ChunkPos.getX(key), ChunkPos.getZ(key), false)
            }
        }

        ship.forcedCompassChunks.clear()
        ship.forcedCompassChunks.addAll(desired)
    }

    private fun clearCompassForcedChunks(ship: EntityShipBase, serverLevel: ServerLevel) {
        if (ship.forcedCompassChunks.isEmpty()) {
            ship.forcedCompassChunkCenterX = Int.MIN_VALUE
            ship.forcedCompassChunkCenterZ = Int.MIN_VALUE
            return
        }

        for (key in HashSet(ship.forcedCompassChunks)) {
            serverLevel.setChunkForced(ChunkPos.getX(key), ChunkPos.getZ(key), false)
        }
        ship.forcedCompassChunks.clear()
        ship.forcedCompassChunkCenterX = Int.MIN_VALUE
        ship.forcedCompassChunkCenterZ = Int.MIN_VALUE
    }

    private fun tickTimeKeepingSound(ship: EntityShipBase) {
        if (!Config.canTimeKeeping || !ship.getStateFlag(ShipContainerMenu.STATE_FLAG_TIMEKEEP) || !ship.isAlive || ship.isInDeadPose) {
            return
        }
        val worldTime = ship.level().dayTime
        if (worldTime % EntityShipBase.TIMEKEEP_INTERVAL_TICKS != 0L) {
            return
        }

        val hour = ((worldTime / EntityShipBase.TIMEKEEP_INTERVAL_TICKS) % 24L).toInt()
        timeKeeping(hour)?.let { timeType ->
            val timeSound = ModSounds.getShipSound(
                timeType,
                ship.getStateMinor(EntityShipBase.STATE_MINOR_SHIP_CLASS),
                ship.random
            )
            ship.playSound(timeSound, max(0.0f, Config.volumeTimeKeeping), 1.0f)
        }
    }

    private fun tickLegacyTimers(ship: EntityShipBase) {
        val attackTick = ship.attackTick
        if (attackTick > 0) {
            ship.attackTick = attackTick - 1
        }
        if (ship.customHurtTime > 0) {
            ship.customHurtTime--
        }
        if (ship.hurtSoundCooldown > 0) {
            ship.hurtSoundCooldown--
        }
        if (ship.feedSoundCooldown > 0) {
            ship.feedSoundCooldown--
        }
    }

    private fun syncFormationMembershipFromOwnerData(ship: EntityShipBase) {
        val ownerRaw = ship.owner
        if (ownerRaw !is ServerPlayer) {
            return
        }

        val data = PlayerStateService.admiralData(ownerRaw)
        val actualTeam = data.findShipTeam(ship.uuid)
        if (actualTeam >= 0) {
            val actualSlot = data.findShipSlot(actualTeam, ship.uuid)
            if (ship.formationTeam != actualTeam) {
                ship.formationTeam = actualTeam
            }
            if (ship.formationSlot != actualSlot) {
                ship.formationSlot = actualSlot
            }
            if (actualTeam == data.getCurrentTeamID()) {
                val selected = data.isSelected(actualTeam, actualSlot)
                if (ship.isPointerSelected != selected) {
                    ship.isPointerSelected = selected
                }
            } else if (ship.isPointerSelected) {
                ship.isPointerSelected = false
            }
            return
        }

        if (ship.formationTeam != -1) {
            ship.formationTeam = -1
        }
        if (ship.formationSlot != -1) {
            ship.formationSlot = -1
        }
        if (ship.isPointerSelected) {
            ship.isPointerSelected = false
        }
    }

    private fun tickWaypointMove(ship: EntityShipBase) {
        if (ship.getStateFlag(11) || ship.isOrderedToSit() || ship.isLeashed() || ship.isVehicle()) {
            clearWaypointMoveRuntimeState(ship)
            return
        }

        if (ship.hasEntityGuardTarget()) {
            val guardedEntity = ship.guardedEntity
            if (guardedEntity == null || !guardedEntity.isAlive) {
                ship.guardedEntity = null
            } else {
                val guardedDim: Int = EntityShipBase.getLegacyDimensionId(guardedEntity.level())
                if (guardedDim != ship.getGuardedPos(3)) {
                    ship.setGuardedPos(-1, -1, -1, guardedDim, ShipGuardTarget.Type.ENTITY.legacyId())
                }
            }
            return
        }

        val guardTarget = ship.guardTarget
        if (!guardTarget.isBlock) {
            clearWaypointMoveRuntimeState(ship)
            return
        }

        val pos = guardTarget.blockPos()
        val distSq = ship.distanceToSqr(pos.x + 0.5, pos.y.toDouble(), pos.z + 0.5)

        val be = ship.level().getBlockEntity(pos)
        if (be is CraneBlockEntity) {
            if (distSq < 64.0) {
                if (ship.getStateMinor(43) == 0) {
                    ship.setStateMinor(43, 1)
                    ship.ejectPassengers()
                }
            } else if (ship.getStateMinor(6) > 0) {
                ship.guardMovement.moveTo(Vec3(pos.x + 0.5, pos.y - 2.0, pos.z + 0.5), 1.0)
            }
        } else {
            ship.setStateMinor(43, 0)
        }

        if (be is IWaypoint) {
            if (ship.getStateMinor(26) > 0 && ship.getStateMinor(27) > 0) return
            if (distSq < 9.0) {
                try {
                    var timeout = false
                    val wpstay = ship.getStateTimer(4)
                    val staytimemax =
                        max(ship.wpStayTimeMax, if (be is WayPointBlockEntity) be.stayTimeTicks else 0)
                    if (wpstay < staytimemax) {
                        ship.setStateTimer(4, wpstay + 16)
                    } else {
                        timeout = true
                    }
                    if (timeout) {
                        ship.setStateTimer(4, 0)
                        val next = be.nextPos
                        val last = be.lastPos
                        val wps = ship.waypoints
                        val shiplast = if (wps != null && wps.size > 0) wps[0] else BlockPos.ZERO
                        var targetPos: BlockPos? = null
                        if (next != null && next.y > 0 && next == shiplast) {
                            if (last != null && last.y > 0) targetPos = last
                            else if (next.y > 0) targetPos = next
                        } else if (next != null && next.y > 0) {
                            targetPos = next
                        }
                        if (targetPos != null) {
                            ship.setGuardedPos(
                                targetPos.x,
                                targetPos.y,
                                targetPos.z,
                                EntityShipBase.getLegacyDimensionId(ship.level()),
                                guardTarget.legacyType()
                            )
                            if (ship.getStateMinor(6) > 0) {
                                ship.setStateMinor(10, 2)
                                ship.guardMovement.moveTo(
                                    Vec3(
                                        targetPos.x + 0.5,
                                        targetPos.y.toDouble(),
                                        targetPos.z + 0.5
                                    ), 1.0
                                )
                            }
                        }
                        val newWps: Array<BlockPos?> =
                            if (wps != null && wps.size > 0) wps.copyOf() else arrayOf(BlockPos.ZERO)
                        newWps[0] = pos
                        ship.waypoints = newWps
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if ((ship.tickCount and 0x7F) == 0 && ship.getStateMinor(6) > 0) {
                ship.guardMovement.moveTo(Vec3(pos.x + 0.5, pos.y.toDouble(), pos.z + 0.5), 1.0)
            }
        }
    }

    private fun clearWaypointMoveRuntimeState(ship: EntityShipBase) {
        ship.setStateMinor(43, 0)
        ship.setStateTimer(4, 0)
        ship.guardMovement.stop()
    }
}
