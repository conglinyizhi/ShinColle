package org.trp.shincolle.server

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.trp.shincolle.attachment.AdmiralData
import java.util.UUID

class AdmiralDataPersistenceRegressionTest {

    @Test
    fun `admiral data should persist roster selection and marriage state fields`() {
        val firstShip = UUID.randomUUID()
        val secondShip = UUID.randomUUID()
        val data = AdmiralData()

        data.setShipUUID(0, 0, firstShip)
        data.setSelected(0, 0, false)
        data.setShipUUID(1, 2, secondShip)
        data.setFormationID(1, 7)
        data.setTeamName(1, "  Vanguard  ")
        data.setCurrentTeamID(1)
        data.setHasReceivedBook(true)
        data.setMarriedShipCount(3)
        data.isRingFlightActive = true

        val restored = AdmiralData().apply {
            deserializeNBT(data.serializeNBT())
        }

        assertThat(restored.getShipUUID(0, 0)).isEqualTo(firstShip)
        assertThat(restored.isSelected(0, 0)).isFalse()
        assertThat(restored.getShipUUID(1, 2)).isEqualTo(secondShip)
        assertThat(restored.getFormationID(1)).isEqualTo(7)
        assertThat(restored.getTeamName(1)).isEqualTo("Vanguard")
        assertThat(restored.getCurrentTeamID()).isEqualTo(1)
        assertThat(restored.hasReceivedBook()).isTrue()
        assertThat(restored.getMarriedShipCount()).isEqualTo(3)
        assertThat(restored.isRingFlightActive).isTrue()
    }

    @Test
    fun `admiral data should sanitize invalid restored state`() {
        val data = AdmiralData()

        data.setCurrentTeamID(99)
        data.setFormationID(0, -5)
        data.setTeamName(0, "   ")
        data.setSelected(0, 3, false)

        val restored = AdmiralData().apply {
            deserializeNBT(data.serializeNBT())
        }

        assertThat(restored.getCurrentTeamID()).isEqualTo(AdmiralData.TEAM_COUNT - 1)
        assertThat(restored.getFormationID(0)).isZero()
        assertThat(restored.getTeamName(0)).isEqualTo("Team 1")
        assertThat(restored.isSelected(0, 3)).isFalse()
        assertThat((0 until AdmiralData.SLOT_COUNT).all { restored.isSelected(2, it) }).isTrue()
    }
}
