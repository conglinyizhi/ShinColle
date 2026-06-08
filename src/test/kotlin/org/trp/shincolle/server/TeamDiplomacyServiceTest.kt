package org.trp.shincolle.server

import org.junit.jupiter.api.Test
import java.util.UUID

class TeamDiplomacyServiceTest {

    @Test
    fun `team diplomacy service should ignore null players and invalid targets`() {
        TeamDiplomacyService.handleAction(null, 0, UUID.randomUUID())
        TeamDiplomacyService.handleAction(null, 1, null)
        TeamDiplomacyService.handleAction(null, 99, UUID.randomUUID())
    }
}
