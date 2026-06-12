package org.trp.shincolle.entity.base.tick

import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.utility.PerformanceTrace
import org.trp.shincolle.utility.TaskHelper.onUpdateTask

/**
 * Coordinates per-tick alive logic for ships by invoking [ShipTickHandler]s in a
 * fixed order and attributing their execution time to the existing performance
 * trace buckets.
 */
object ShipTickCoordinator {

    private val emotionHandler = ShipEmotionTickHandler
    private val movementHandler = ShipMovementTickHandler
    private val passiveCombatHandler = ShipPassiveCombatTickHandler
    private val fuelSuppliesHandler = ShipFuelSuppliesTickHandler
    private val periodicSyncHandler = ShipPeriodicSyncTickHandler
    private val equipEffectsHandler = ShipEquipEffectsTickHandler

    fun tickAliveLogic(ship: EntityShipBase) {
        val tracing = PerformanceTrace.enabled()
        if (tracing) {
            ship.perfShipCoreNanos = 0L
            ship.perfShipTaskNanos = 0L
            ship.perfShipSupportNanos = 0L
            ship.perfShipPeriodicNanos = 0L
        }

        var segmentStart = startPerfSegment(tracing)
        emotionHandler.tick(ship)
        ship.perfShipCoreNanos += finishPerfSegment(tracing, segmentStart)

        segmentStart = startPerfSegment(tracing)
        if (!movementHandler.tick(ship)) {
            ship.perfShipCoreNanos += finishPerfSegment(tracing, segmentStart)
            return
        }
        ship.perfShipCoreNanos += finishPerfSegment(tracing, segmentStart)

        segmentStart = startPerfSegment(tracing)
        passiveCombatHandler.tick(ship)
        ship.perfShipSupportNanos += finishPerfSegment(tracing, segmentStart)

        segmentStart = startPerfSegment(tracing)
        fuelSuppliesHandler.tick(ship)
        ship.perfShipSupportNanos += finishPerfSegment(tracing, segmentStart)

        if (ship.isAlive && (ship.tickCount and 7) == 0) {
            segmentStart = startPerfSegment(tracing)
            onUpdateTask(ship)
            ship.perfShipTaskNanos += finishPerfSegment(tracing, segmentStart)
        }

        segmentStart = startPerfSegment(tracing)
        periodicSyncHandler.tick(ship)
        ship.perfShipPeriodicNanos += finishPerfSegment(tracing, segmentStart)

        segmentStart = startPerfSegment(tracing)
        equipEffectsHandler.tick(ship)
        ship.perfShipPeriodicNanos += finishPerfSegment(tracing, segmentStart)
    }

    private fun startPerfSegment(tracing: Boolean): Long {
        return if (tracing) PerformanceTrace.now() else 0L
    }

    private fun finishPerfSegment(tracing: Boolean, startNanos: Long): Long {
        return if (tracing) PerformanceTrace.elapsed(startNanos) else 0L
    }
}
