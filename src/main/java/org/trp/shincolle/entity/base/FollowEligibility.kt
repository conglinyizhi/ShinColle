package org.trp.shincolle.entity.base

import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import org.trp.shincolle.menu.ShipContainerMenu

/**
 * Immutable result of evaluating whether a ship should currently follow its owner.
 *
 * Centralizes the dual logic that previously lived in both
 * [EntityShipBase.shouldFollowOwner] and [EntityShipBase.explainFollowBlockReason],
 * ensuring the reason and the boolean decision can never drift out of sync.
 */
data class FollowEligibility(
    val shouldFollow: Boolean,
    val reason: String,
    val minDistance: Double
) {
    companion object {
        /** Reason returned when the ship is eligible to follow. */
        const val REASON_ELIGIBLE = "eligible"

        private const val DEFAULT_FOLLOW_MIN_DISTANCE = 5.0
        private const val COMBAT_RATION_FOLLOW_DISTANCE = 1.5
        private const val FOLLOW_DISTANCE_CLAMP_LOW = 1
        private const val FOLLOW_DISTANCE_CLAMP_HIGH = 31

        /** Evaluates the ship's current owner-follow eligibility. */
        fun evaluate(ship: EntityShipBase): FollowEligibility {
            val owner = ship.owner
            val blockReason = evaluateBlockReason(ship, owner)
            if (blockReason != null) {
                return blocked(blockReason)
            }

            val minDistance = computeMinFollowDistance(ship, owner!!)
            val distanceSqr = ship.distanceToSqr(owner)
            return if (distanceSqr <= (minDistance * minDistance)) {
                blocked("withinMinDistance", minDistance)
            } else {
                FollowEligibility(true, REASON_ELIGIBLE, minDistance)
            }
        }

        private fun evaluateBlockReason(ship: EntityShipBase, owner: net.minecraft.world.entity.Entity?): String? {
            return when {
                ship.isOrderedToSit() -> "orderedToSit"
                ship.isInSittingPose() -> "sittingPose"
                ship.isInDeadPose -> "deadPose"
                ship.isPassenger && ship.vehicle?.isAlive == true -> "passenger"
                ship.isNoFuel -> "noFuel"
                owner == null -> if (ship.ownerUUID == null) "noOwnerUuid" else "ownerEntityMissing"
                ship.hasBlockGuardTarget() -> "blockGuardTarget"
                ship.hasPointerTarget() -> "pointerTarget"
                ship.hasPointerTargetEntity() -> "pointerTargetEntity"
                ship.target != null -> "attackTarget"
                else -> null
            }
        }

        private fun blocked(reason: String, minDistance: Double = 0.0): FollowEligibility {
            return FollowEligibility(false, reason, minDistance)
        }

        private fun computeMinFollowDistance(ship: EntityShipBase, owner: net.minecraft.world.entity.Entity): Double {
            val configuredMin = ship.getStateMinor(ShipContainerMenu.STATE_MINOR_FOLLOW_MIN)
            val minDist = if (configuredMin <= 0) {
                DEFAULT_FOLLOW_MIN_DISTANCE
            } else {
                Mth.clamp(configuredMin, FOLLOW_DISTANCE_CLAMP_LOW, FOLLOW_DISTANCE_CLAMP_HIGH).toDouble()
            }
            return if (owner is Player && ship.playerHasCombatRation(owner)) COMBAT_RATION_FOLLOW_DISTANCE else minDist
        }
    }
}
