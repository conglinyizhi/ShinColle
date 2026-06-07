package org.trp.shincolle.server

import org.junit.jupiter.api.Test
import java.util.Optional
import java.util.UUID

class FormationServiceTest {

    @Test
    fun `formation service should ignore null players at payload facing entrypoints`() {
        FormationService.syncNearbyShipsForCurrentTeam(null, true)
        FormationService.syncNearbyShipsForCurrentTeam(null, false)

        FormationService.handleFormationAction(null, 0, 0, 0, null, Optional.empty<UUID>())
        FormationService.handleFormationAction(null, 5, 0, 0, "Fleet", Optional.of(UUID.randomUUID()))
        FormationService.handleFormationAction(null, 99, 0, 0, "", Optional.empty<UUID>())

        FormationService.handlePointerRosterToggle(null, UUID.randomUUID())
        FormationService.handlePointerRosterToggle(null, null)
    }
}
