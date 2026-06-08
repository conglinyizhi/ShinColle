package org.trp.shincolle.server

import org.junit.jupiter.api.Test

class TargetProtectionServiceTest {

    @Test
    fun `target protection entrypoints should ignore null players`() {
        TargetProtectionService.toggleUnattackableTarget(null, null)
        TargetProtectionService.showUnattackableTargets(null)
        TargetProtectionService.togglePlayerTarget(null, null)
        TargetProtectionService.showPlayerTargets(null)
    }
}
