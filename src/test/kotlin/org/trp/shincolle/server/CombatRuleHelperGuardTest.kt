package org.trp.shincolle.server

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CombatRuleHelperGuardTest {

    @Test
    fun `diplomacy and target protection helpers should reject null rule inputs`() {
        assertThat(TeamDiplomacyService.isDiplomaticAlly(null, null)).isFalse()
        assertThat(TeamDiplomacyService.isDiplomaticBanned(null, null)).isFalse()
        assertThat(TargetProtectionService.isUnattackableTargetClass(null, null)).isFalse()
        assertThat(TargetProtectionService.isPlayerConfiguredTargetClass(null, null)).isFalse()
    }
}
