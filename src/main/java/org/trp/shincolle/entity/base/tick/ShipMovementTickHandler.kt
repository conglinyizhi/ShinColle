package org.trp.shincolle.entity.base.tick

import org.trp.shincolle.entity.base.EntityShipBase

/**
 * Handles mount summon, water buoyancy and lifecycle movement state machine
 * switches for sitting/dead ships.
 */
object ShipMovementTickHandler : ShipTickHandler {

    override fun tick(ship: EntityShipBase): Boolean {
        if (!ship.level().isClientSide && ship.tickHostileDespawn()) {
            return false
        }

        ship.updateMountSummon()
        ship.applyWaterBuoyancyIfNeeded()

        if (ship.isSitting || ship.isInDeadPose) {
            ship.lifecycleMovement.stopAny()
        }

        return true
    }
}
