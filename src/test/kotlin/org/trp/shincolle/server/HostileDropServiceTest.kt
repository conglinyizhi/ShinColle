package org.trp.shincolle.server

import org.junit.jupiter.api.Test

class HostileDropServiceTest {

    @Test
    fun `hostile drop service should ignore null events`() {
        HostileDropService.handleLivingDrops(null)
    }
}
