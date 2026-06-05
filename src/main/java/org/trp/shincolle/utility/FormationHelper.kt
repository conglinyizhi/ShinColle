package org.trp.shincolle.utility

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.Shincolle.Companion.debugLog
import org.trp.shincolle.block.DeskBlock
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.base.ShipMovementCoordinator
import org.trp.shincolle.reference.Values
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object FormationHelper {
    fun applySummonShipsToDesk(player: Player?, deskPos: BlockPos, shipUuids: MutableList<UUID>) {
        if (player !is ServerPlayer) return
        val world = player.serverLevel()
        val deskState = world.getBlockState(deskPos)
        if (deskState.getBlock() !is DeskBlock) return

        val facing = deskState.getValue<Direction>(DeskBlock.FACING)
        val spawnDir = facing.getOpposite()
        val rightDir = spawnDir.getClockWise()
        val leftDir = spawnDir.getCounterClockWise()

        val refPos = deskPos.relative(spawnDir, 3).relative(leftDir, 1)

        var totalShips = 0
        for (uuid in shipUuids) {
            val e = world.getEntity(uuid)
            if (e is EntityShipBase) totalShips++
        }

        val maxPerRow = 4
        val horizontalSpacing = 1
        val depthSpacing = 1
        var col = 0
        var row = 0

        for (uuid in shipUuids) {
            val entity = world.getEntity(uuid)
            if (entity !is EntityShipBase) continue
            if (!entity.isOwnedBy(player) || !entity.isAlive() || entity.isInDeadPose) continue

            if (totalShips == 1) {
                col = 1
            } else if (col >= maxPerRow) {
                row++
                col = 0
            }

            var spawnBlock = refPos.relative(rightDir, col * horizontalSpacing).relative(spawnDir, row * depthSpacing)
            var spawnX = spawnBlock.getX() + 0.5
            val spawnY = deskPos.getY() + 1.0
            var spawnZ = spawnBlock.getZ() + 0.5

            if (!world.isEmptyBlock(BlockPos(spawnX.toInt(), spawnY.toInt(), spawnZ.toInt()))) {
                row++
                col = 0
                spawnBlock = refPos.relative(rightDir, col * horizontalSpacing).relative(spawnDir, row * depthSpacing)
                spawnX = spawnBlock.getX() + 0.5
                spawnZ = spawnBlock.getZ() + 0.5
            }

            if (entity.distanceToSqr(spawnX, spawnY, spawnZ) > 1024.0) {
                val movement = ShipMovementCoordinator(entity)
                if (!movement.teleportNearLiving(player, 0.75)) {
                    debugLog(
                        "Formation summon teleportFailed ship={} desk={} target={},{},{}",
                        entity.getUUID(), deskPos, spawnX, spawnY, spawnZ
                    )
                    continue
                }
            }

            val yaw = facing.toYRot()
            entity.setYRot(yaw)
            entity.setYHeadRot(yaw)
            entity.setYBodyRot(yaw)
            entity.setXRot(0f)

            applyShipGuard(entity, Mth.floor(spawnX), spawnY.toInt(), Mth.floor(spawnZ))
            col++
        }
    }

    fun applyShipGuard(ship: EntityShipBase?, x: Int, y: Int, z: Int) {
        if (ship == null) return
        ship.setOrderedToSit(false)
        ship.setInSittingPose(false)
        ship.setGuardBlockTarget(BlockPos(x, y, z))
        ship.setStateFlag(EntityShipBase.STATE_FLAG_DISABLE_GUARD_POS, false)
        ship.moveGuardTargetTo(Vec3(x + 0.5, y.toDouble(), z + 0.5), 1.2)
        ship.setStateTimer(18, 200)
    }

    @JvmStatic
    fun applyShipGuardEntity(ship: EntityShipBase?, guarded: Entity?) {
        if (ship == null || guarded == null) return

        val current = ship.guardedEntity
        if (current != null && current.uuid == guarded.uuid) {
            ship.guardedEntity = null
            ship.setStateFlag(EntityShipBase.STATE_FLAG_DISABLE_GUARD_POS, false)
            return
        }

        ship.setOrderedToSit(false)
        ship.setInSittingPose(false)
        ship.guardedEntity = guarded
        ship.setStateFlag(EntityShipBase.STATE_FLAG_DISABLE_GUARD_POS, false)
        ship.moveGuardTargetTo(guarded, 1.2)
        ship.setStateTimer(18, 200)
    }


    fun getFormationPos(formationId: Int, slotId: Int, flagshipPos: Vec3, yaw: Float): Vec3 {
        if (slotId == 0) return flagshipPos
        val dir = getFormationDirectionFromYaw(yaw)
        return getFormationPos(formationId, slotId, flagshipPos, dir[0], dir[1])
    }

    @JvmStatic
    fun getFormationPos(formationId: Int, slotId: Int, flagshipPos: Vec3, alongX: Boolean, faceP: Boolean): Vec3 {
        val target = calcFormationPos(formationId, slotId, flagshipPos.x, flagshipPos.y, flagshipPos.z, alongX, faceP)
        return Vec3(target[0], flagshipPos.y, target[2])
    }

    @JvmStatic
    fun getFormationDirection(toX: Double, toZ: Double, fromX: Double, fromZ: Double): BooleanArray {
        val face = BooleanArray(2)
        val dx = toX - fromX
        val dz = toZ - fromZ
        face[0] = abs(dx) > abs(dz)
        face[1] = if (face[0]) (dx >= 0.0) else (dz >= 0.0)
        return face
    }

    fun getFormationDirectionFromYaw(yaw: Float): BooleanArray {
        val dir = BooleanArray(2)
        val rad = Math.toRadians(yaw.toDouble())
        val dx = -sin(rad)
        val dz = cos(rad)
        dir[0] = abs(dx) > abs(dz)
        dir[1] = if (dir[0]) (dx >= 0.0) else (dz >= 0.0)
        return dir
    }

    fun calcFormationPos(
        formationId: Int,
        slotId: Int,
        x: Double,
        y: Double,
        z: Double,
        alongX: Boolean,
        faceP: Boolean
    ): DoubleArray {
        var newPos = doubleArrayOf(x, y, z)
        if (slotId == 0) return newPos

        when (formationId) {
            1 -> {
                var i = 0
                while (i < slotId) {
                    newPos = nextLineAheadPos(alongX, faceP, newPos[0], newPos[1], newPos[2])
                    ++i
                }
            }

            4 -> {
                var i = 0
                while (i < slotId) {
                    newPos = nextEchelonPos(faceP, newPos[0], newPos[1], newPos[2])
                    ++i
                }
            }

            2 -> newPos = nextDoubleLinePos(alongX, faceP, slotId, newPos[0], newPos[1], newPos[2])
            3 -> newPos = nextDiamondPos(alongX, faceP, slotId, newPos[0], newPos[1], newPos[2])
            5 -> newPos = nextLineAbreastPos(alongX, slotId, newPos[0], newPos[1], newPos[2])
            else -> {}
        }
        return newPos
    }

    private fun nextLineAheadPos(alongX: Boolean, faceP: Boolean, x: Double, y: Double, z: Double): DoubleArray {
        val pos = doubleArrayOf(x, y, z)
        val offset = if (faceP) -3.0 else 3.0
        if (alongX) {
            pos[0] += offset
        } else {
            pos[2] += offset
        }
        return pos
    }

    private fun nextDoubleLinePos(
        alongX: Boolean,
        faceP: Boolean,
        formatPos: Int,
        x: Double,
        y: Double,
        z: Double
    ): DoubleArray {
        val pos = doubleArrayOf(x, y, z)
        when (formatPos) {
            1 -> if (alongX) pos[2] += 3.0 else pos[0] += 3.0
            2 -> if (alongX) pos[0] += if (faceP) 3.0 else -3.0 else pos[2] += if (faceP) 3.0 else -3.0
            3 -> if (alongX) {
                pos[0] += if (faceP) 3.0 else -3.0
                pos[2] += 3.0
            } else {
                pos[0] += 3.0
                pos[2] += if (faceP) 3.0 else -3.0
            }

            4 -> if (alongX) pos[0] += if (faceP) -3.0 else 3.0 else pos[2] += if (faceP) -3.0 else 3.0
            5 -> if (alongX) {
                pos[0] += if (faceP) -3.0 else 3.0
                pos[2] += 3.0
            } else {
                pos[0] += 3.0
                pos[2] += if (faceP) -3.0 else 3.0
            }

            else -> {}
        }
        return pos
    }

    private fun nextDiamondPos(
        alongX: Boolean,
        faceP: Boolean,
        formatPos: Int,
        x: Double,
        y: Double,
        z: Double
    ): DoubleArray {
        val pos = doubleArrayOf(x, y, z)
        when (formatPos) {
            1 -> if (alongX) pos[0] += if (faceP) 5.0 else -5.0 else pos[2] += if (faceP) 5.0 else -5.0
            2 -> if (alongX) {
                pos[0] += if (faceP) 1.0 else -1.0
                pos[2] -= 4.0
            } else {
                pos[0] -= 4.0
                pos[2] += if (faceP) 1.0 else -1.0
            }

            3 -> if (alongX) {
                pos[0] += if (faceP) 1.0 else -1.0
                pos[2] += 4.0
            } else {
                pos[0] += 4.0
                pos[2] += if (faceP) 1.0 else -1.0
            }

            4 -> if (alongX) pos[0] += if (faceP) -3.0 else 3.0 else pos[2] += if (faceP) -3.0 else 3.0
            5 -> if (alongX) pos[0] += if (faceP) 2.0 else -2.0 else pos[2] += if (faceP) 2.0 else -2.0
            else -> {}
        }
        return pos
    }

    private fun nextEchelonPos(faceP: Boolean, x: Double, y: Double, z: Double): DoubleArray {
        val pos = doubleArrayOf(x, y, z)
        val offset = if (faceP) -2.0 else 2.0
        pos[0] += offset
        pos[2] += offset
        return pos
    }

    private fun nextLineAbreastPos(alongX: Boolean, formatPos: Int, x: Double, y: Double, z: Double): DoubleArray {
        val pos = doubleArrayOf(x, y, z)
        val offset: Double
        when (formatPos) {
            1 -> offset = 3.0
            2 -> offset = -3.0
            3 -> offset = 6.0
            4 -> offset = -6.0
            5 -> offset = 9.0
            else -> offset = 0.0
        }
        if (alongX) pos[2] += offset else pos[0] += offset
        return pos
    }

    @JvmStatic
    fun getFormationBuffs(formationId: Int, slotId: Int): FloatArray? {
        val fvalue = Values.FormationAttrs.get(formationId * 10 + slotId)
        return fvalue?.copyOf(fvalue.size) ?: Values.resetFormationValue
    }
}
