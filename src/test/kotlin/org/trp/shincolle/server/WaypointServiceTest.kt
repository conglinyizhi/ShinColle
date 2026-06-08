package org.trp.shincolle.server

import net.minecraft.core.BlockPos
import org.junit.jupiter.api.Test

class WaypointServiceTest {

    @Test
    fun `waypoint service should ignore null players and malformed payload positions`() {
        val pos = BlockPos.ZERO

        WaypointService.handleAction(null, 0, pos, pos)
        WaypointService.handleAction(null, 1, pos, pos)
        WaypointService.handleAction(null, 2, pos, pos)
        WaypointService.handleAction(null, 99, pos, pos)

        WaypointService.handleAction(null, 0, null, pos)
        WaypointService.handleAction(null, 0, pos, null)
        WaypointService.handleAction(null, 0, null, null)
    }
}
