package org.trp.shincolle.entity.base.tick

import org.trp.shincolle.entity.base.EntityShipBase

/**
 * Updates emotion state and periodic reaction emotes.
 */
object ShipEmotionTickHandler : ShipTickHandler {

    override fun tick(ship: EntityShipBase): Boolean {
        ship.emotions.tickEmotions()

        if (!ship.isNoFuel) {
            ship.reactions.tickEmotes()
            if ((ship.tickCount and 0xFF) == 0) {
                ship.applyEmotesReaction(4)
            }
        }

        return true
    }
}
