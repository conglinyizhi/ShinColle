package org.trp.shincolle.server

import net.minecraft.nbt.CompoundTag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class PlayerStateServiceTest {

    @Test
    fun `player state service should reject null players at guardable entrypoints`() {
        val shipUuid = UUID.randomUUID()

        PlayerStateService.applyAdmiralSync(null, CompoundTag(), intArrayOf(1, 2, 3))
        PlayerStateService.adjustOwnedMarriedShipCount(null, 1)
        PlayerStateService.adjustOwnedMarriedShipCount(null, 0)
        PlayerStateService.setRingFlightActive(null, true)

        assertThat(PlayerStateService.hasCollectedShip(null, 7)).isFalse()
        assertThat(PlayerStateService.getOwnedMarriedShipCount(null)).isZero()
        assertThat(PlayerStateService.isRingFlightActive(null)).isFalse()
        assertThat(PlayerStateService.currentTeamId(null)).isZero()
        assertThat(PlayerStateService.setCurrentTeamId(null, 0)).isFalse()
        assertThat(PlayerStateService.setCurrentTeamFormation(null, 0)).isFalse()
        assertThat(PlayerStateService.setCurrentTeamSlotSelected(null, 0, true)).isFalse()
        assertThat(PlayerStateService.setCurrentTeamName(null, "Fleet")).isFalse()
        assertThat(PlayerStateService.swapCurrentTeamSlots(null, 0, 1)).isFalse()
        assertThat(PlayerStateService.assignShipToCurrentTeam(null, shipUuid)).isEqualTo(-1)
        assertThat(PlayerStateService.removeShipFromTeams(null, shipUuid)).isFalse()
        assertThat(PlayerStateService.setCurrentTeamSlot(null, 0, shipUuid)).isNull()
    }
}
