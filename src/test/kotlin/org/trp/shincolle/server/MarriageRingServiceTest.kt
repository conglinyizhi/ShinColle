package org.trp.shincolle.server

import net.minecraft.world.item.ItemStack
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MarriageRingServiceTest {

    @Test
    fun `marriage ring service should reject null players at inventory and tick entrypoints`() {
        MarriageRingService.applyTickAbilities(null)

        assertThat(MarriageRingService.getUnderwaterBreakSpeedMultiplier(null)).isEqualTo(1.0F)
        assertThat(MarriageRingService.shouldCancelFireDamage(null, null)).isFalse()
        assertThat(MarriageRingService.handleFireDamageEvent(null, null)).isFalse()
        assertThat(MarriageRingService.getUnderwaterFogDistanceMultiplier(null)).isEqualTo(1.0F)
        assertThat(MarriageRingService.hasActiveMarriageRing(null)).isFalse()
        assertThat(MarriageRingService.findActiveMarriageRing(null)).isEqualTo(ItemStack.EMPTY)
    }
}
