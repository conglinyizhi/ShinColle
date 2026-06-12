package org.trp.shincolle.entity.base.tick

import org.trp.shincolle.entity.base.EntityShipBase

/**
 * Single step of per-tick alive logic for an [EntityShipBase].
 *
 * Implementations must be stateless and thread-safe; they receive the ship
 * instance on each call.
 */
fun interface ShipTickHandler {

    /**
     * Performs the handler's work for [ship].
     *
     * @return true if normal ticking should continue, false to stop further
     * handlers (for example when a hostile ship despawns mid-tick).
     */
    fun tick(ship: EntityShipBase): Boolean
}
