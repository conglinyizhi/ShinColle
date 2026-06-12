package org.trp.shincolle.entity.base.tick

import org.trp.shincolle.entity.base.EntityShipBase

/**
 * Passive combat AI: retreat for low health, clear invalid targets and drive
 * retreat/pickup movement state transitions.
 */
object ShipPassiveCombatTickHandler : ShipTickHandler {

    override fun tick(ship: EntityShipBase): Boolean {
        if (ship.isNoFuel) {
            ship.retreatMovement.stop()
            ship.pickupMovement.stop()
            ship.clearPassiveCombatTargetBrain(true)
            return true
        }

        val retreatingForLowHealth = ship.shouldRetreatForLowHealth()
        if (retreatingForLowHealth) {
            ship.clearPassiveCombatTargetBrain(true)
            ship.tickRetreatMovement()
        } else {
            ship.retreatMovement.stop()
            if (ship.hasPointerTargetEntity()) {
                ship.clearPassiveCombatTargetBrain(true)
            }
        }

        return true
    }
}
