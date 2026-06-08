package org.trp.shincolle.server

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.trp.shincolle.network.C2STeamDiplomacyPayload
import java.util.UUID

class TeamDiplomacyActionDispatchTest {

    @Test
    fun `diplomacy action dispatch should mutate saved data and report real changes`() {
        val owner = UUID.randomUUID()
        val ally = UUID.randomUUID()
        val banned = UUID.randomUUID()
        val diplomacy = TeamDiplomacySavedData()

        assertThat(TeamDiplomacyService.applyDiplomacyAction(diplomacy, owner, C2STeamDiplomacyPayload.ACTION_ADD_ALLY, ally)).isTrue()
        assertThat(TeamDiplomacyService.applyDiplomacyAction(diplomacy, owner, C2STeamDiplomacyPayload.ACTION_ADD_ALLY, ally)).isFalse()
        assertThat(diplomacy.areAllies(owner, ally)).isTrue()

        assertThat(TeamDiplomacyService.applyDiplomacyAction(diplomacy, owner, C2STeamDiplomacyPayload.ACTION_REMOVE_ALLY, ally)).isTrue()
        assertThat(TeamDiplomacyService.applyDiplomacyAction(diplomacy, owner, C2STeamDiplomacyPayload.ACTION_REMOVE_ALLY, ally)).isFalse()
        assertThat(diplomacy.areAllies(owner, ally)).isFalse()

        assertThat(TeamDiplomacyService.applyDiplomacyAction(diplomacy, owner, C2STeamDiplomacyPayload.ACTION_ADD_BANNED, banned)).isTrue()
        assertThat(TeamDiplomacyService.applyDiplomacyAction(diplomacy, owner, C2STeamDiplomacyPayload.ACTION_ADD_BANNED, banned)).isFalse()
        assertThat(diplomacy.isBanned(owner, banned)).isTrue()

        assertThat(TeamDiplomacyService.applyDiplomacyAction(diplomacy, owner, C2STeamDiplomacyPayload.ACTION_REMOVE_BANNED, banned)).isTrue()
        assertThat(TeamDiplomacyService.applyDiplomacyAction(diplomacy, owner, C2STeamDiplomacyPayload.ACTION_REMOVE_BANNED, banned)).isFalse()
        assertThat(diplomacy.isBanned(owner, banned)).isFalse()
    }

    @Test
    fun `diplomacy action dispatch should reject unsupported actions and invalid relations`() {
        val owner = UUID.randomUUID()
        val target = UUID.randomUUID()
        val diplomacy = TeamDiplomacySavedData()

        assertThat(TeamDiplomacyService.applyDiplomacyAction(diplomacy, owner, 99, target)).isFalse()
        assertThat(TeamDiplomacyService.applyDiplomacyAction(diplomacy, owner, C2STeamDiplomacyPayload.ACTION_ADD_ALLY, owner)).isFalse()
        assertThat(TeamDiplomacyService.applyDiplomacyAction(diplomacy, owner, C2STeamDiplomacyPayload.ACTION_ADD_BANNED, null)).isFalse()
    }
}
