package org.trp.shincolle.server

import org.junit.jupiter.api.Test

class DeskInteractionServiceTest {

    @Test
    fun `desk interaction entrypoints should ignore null players`() {
        DeskInteractionService.updateBookState(null, 1, 2)
        DeskInteractionService.updateDeskGui(null, 3, 1)
        DeskInteractionService.openOwnedShipFromDesk(null, null)
        DeskInteractionService.summonOwnedShipsToDesk(null, listOf())
    }
}
