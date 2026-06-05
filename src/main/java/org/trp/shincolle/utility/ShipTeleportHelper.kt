package org.trp.shincolle.utility

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.item.DebugInspectorItem

object ShipTeleportHelper {
    private const val MIN_PLAYER_DISTANCE_SQ = 9.0
    private const val TARGET_REACH_DISTANCE_SQ = 144.0
    private val PREFERRED_OFFSETS = arrayOf<IntArray>(
        intArrayOf(-4, 0), intArrayOf(-4, -2), intArrayOf(-4, 2),
        intArrayOf(-5, 0), intArrayOf(-5, -3), intArrayOf(-5, 3),
        intArrayOf(-3, -3), intArrayOf(-3, 3), intArrayOf(-6, -1), intArrayOf(-6, 1),
        intArrayOf(-2, -4), intArrayOf(-2, 4)
    )
    private val FALLBACK_OFFSETS = arrayOf<IntArray>(
        intArrayOf(-7, 0), intArrayOf(-7, -2), intArrayOf(-7, 2),
        intArrayOf(-5, -5), intArrayOf(-5, 5), intArrayOf(-1, -5), intArrayOf(-1, 5),
        intArrayOf(0, -6), intArrayOf(0, 6), intArrayOf(-8, 0)
    )
    private val POINT_OFFSETS = arrayOf<IntArray>(
        intArrayOf(0, 0), intArrayOf(1, 0), intArrayOf(-1, 0), intArrayOf(0, 1), intArrayOf(0, -1),
        intArrayOf(2, 0), intArrayOf(-2, 0), intArrayOf(0, 2), intArrayOf(0, -2),
        intArrayOf(2, 1), intArrayOf(2, -1), intArrayOf(-2, 1), intArrayOf(-2, -1),
        intArrayOf(1, 2), intArrayOf(-1, 2), intArrayOf(1, -2), intArrayOf(-1, -2),
        intArrayOf(3, 0), intArrayOf(-3, 0), intArrayOf(0, 3), intArrayOf(0, -3)
    )

    @JvmStatic
    fun teleportNearLiving(entity: Entity, anchor: LivingEntity, verticalOffset: Double): Boolean {
        if (entity.level() !is ServerLevel) {
            return false
        }

        val basePos = Vec3(anchor.getX(), anchor.getY() + verticalOffset, anchor.getZ())
        val facing = anchor.getLookAngle()
        var horizontalFacing = Vec3(facing.x, 0.0, facing.z)
        if (horizontalFacing.lengthSqr() < 1.0E-4) {
            horizontalFacing = Vec3(0.0, 0.0, 1.0)
        } else {
            horizontalFacing = horizontalFacing.normalize()
        }
        val right = Vec3(-horizontalFacing.z, 0.0, horizontalFacing.x)

        var candidate =
            findCandidate(serverLevel, entity, anchor, basePos, horizontalFacing, right, PREFERRED_OFFSETS, true)
        if (candidate == null) {
            candidate =
                findCandidate(serverLevel, entity, anchor, basePos, horizontalFacing, right, FALLBACK_OFFSETS, true)
        }
        if (candidate == null) {
            candidate = findVerticalFallback(serverLevel, entity, anchor, basePos, horizontalFacing)
        }
        if (candidate == null) {
            return false
        }

        entity.teleportTo(candidate.x, candidate.y, candidate.z)
        notifyOwnerIfDebugging(entity)
        return true
    }

    @JvmStatic
    fun teleportNearPoint(entity: Entity, anchor: Vec3, verticalOffset: Double): Boolean {
        if (entity.level() !is ServerLevel) {
            return false
        }

        val candidate = findPointCandidate(serverLevel, entity, anchor.add(0.0, verticalOffset, 0.0))
        if (candidate == null) {
            return false
        }

        entity.teleportTo(candidate.x, candidate.y, candidate.z)
        notifyOwnerIfDebugging(entity)
        return true
    }

    private fun findCandidate(
        level: ServerLevel,
        entity: Entity,
        anchor: LivingEntity,
        basePos: Vec3,
        back: Vec3,
        right: Vec3,
        offsets: Array<IntArray>,
        rejectFront: Boolean
    ): Vec3? {
        for (offset in offsets) {
            val candidate = basePos
                .add(back.scale(offset[0].toDouble()))
                .add(right.scale(offset[1].toDouble()))
            val safePos = validateCandidate(level, entity, anchor, candidate, back, rejectFront)
            if (safePos != null) {
                return safePos
            }
        }
        return null
    }

    private fun validateCandidate(
        level: ServerLevel,
        entity: Entity,
        anchor: LivingEntity,
        candidate: Vec3,
        facing: Vec3,
        rejectFront: Boolean
    ): Vec3? {
        val dx = candidate.x - anchor.getX()
        val dz = candidate.z - anchor.getZ()
        val horizontalDistSq = dx * dx + dz * dz
        if (horizontalDistSq < MIN_PLAYER_DISTANCE_SQ) {
            return null
        }

        if (rejectFront) {
            val dot = dx * facing.x + dz * facing.z
            if (dot > 0.0) {
                return null
            }
        }

        if (anchor.distanceToSqr(candidate) > TARGET_REACH_DISTANCE_SQ) {
            return null
        }

        val baseBlock = BlockPos.containing(candidate.x, candidate.y, candidate.z)
        for (dy in 2 downTo -2) {
            val testPos = baseBlock.offset(0, dy, 0)
            if (!canStandAt(level, entity, testPos)) {
                continue
            }
            return Vec3(testPos.getX() + 0.5, testPos.getY().toDouble(), testPos.getZ() + 0.5)
        }
        return null
    }

    private fun findVerticalFallback(
        level: ServerLevel,
        entity: Entity,
        anchor: LivingEntity?,
        basePos: Vec3,
        facing: Vec3?
    ): Vec3? {
        val baseBlock = BlockPos.containing(basePos.x, basePos.y, basePos.z)
        for (dy in 4 downTo -4) {
            val testPos = baseBlock.offset(0, dy, 0)
            if (!canStandAt(level, entity, testPos)) {
                continue
            }
            return Vec3(testPos.getX() + 0.5, testPos.getY().toDouble(), testPos.getZ() + 0.5)
        }
        return null
    }

    private fun findPointCandidate(level: ServerLevel, entity: Entity, basePos: Vec3): Vec3? {
        val baseBlock = BlockPos.containing(basePos.x, basePos.y, basePos.z)
        for (offset in POINT_OFFSETS) {
            for (dy in 2 downTo -3) {
                val testPos = baseBlock.offset(offset[0], dy, offset[1])
                if (!canStandAt(level, entity, testPos)) {
                    continue
                }
                return Vec3(testPos.getX() + 0.5, testPos.getY().toDouble(), testPos.getZ() + 0.5)
            }
        }
        return null
    }

    private fun canStandAt(level: Level, entity: Entity, pos: BlockPos): Boolean {
        if (level.isOutsideBuildHeight(pos) || level.isOutsideBuildHeight(pos.above())) {
            return false
        }
        if (!level.getBlockState(pos.below()).entityCanStandOn(level, pos.below(), entity)) {
            return false
        }
        if (!level.getBlockState(pos).canBeReplaced() || !level.getBlockState(pos.above()).canBeReplaced()) {
            return false
        }

        val x = pos.getX() + 0.5
        val y = pos.getY().toDouble()
        val z = pos.getZ() + 0.5
        return level.noCollision(
            entity, entity.getBoundingBox().move(
                x - entity.getX(),
                y - entity.getY(),
                z - entity.getZ()
            )
        )
    }

    private fun notifyOwnerIfDebugging(entity: Entity?) {
        if (entity !is TamableAnimal) return
        val ownerId = entity.getOwnerUUID()
        if (ownerId == null) return
        if (entity.level() !is ServerLevel) return
        val player: ServerPlayer? = sl.getServer().getPlayerList().getPlayer(ownerId)
        if (player == null) return
        if (!hasDebugInspectorEquipped(player)) return
        val pos = String.format("(%.2f, %.2f, %.2f)", entity.getX(), entity.getY(), entity.getZ())
        player.sendSystemMessage(
            Component.literal(
                "[DebugInspector] Ship " + entity.getUUID().toString().substring(0, 8) + "... teleported to " + pos
            ), false
        )
    }

    private fun hasDebugInspectorEquipped(player: Player): Boolean {
        if (player.getOffhandItem().getItem() is DebugInspectorItem) return true
        for (stack in player.getArmorSlots()) {
            if (stack.getItem() is DebugInspectorItem) return true
        }
        return false
    }
}
