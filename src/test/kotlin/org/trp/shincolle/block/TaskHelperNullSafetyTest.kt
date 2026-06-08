package org.trp.shincolle.block

import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Test
import org.trp.shincolle.utility.TaskHelper

class TaskHelperNullSafetyTest {

    @Test
    fun `onUpdateTask should not crash with null host`() {
        assertThatNoException().isThrownBy {
            TaskHelper.onUpdateTask(null)
        }
    }

    @Test
    fun `onUpdateCooking should not crash with null host`() {
        assertThatNoException().isThrownBy {
            TaskHelper.onUpdateCooking(null)
        }
    }

    @Test
    fun `onUpdateFishing should not crash with null host`() {
        assertThatNoException().isThrownBy {
            TaskHelper.onUpdateFishing(null)
        }
    }

    @Test
    fun `onUpdateMining should not crash with null host`() {
        assertThatNoException().isThrownBy {
            TaskHelper.onUpdateMining(null)
        }
    }

    @Test
    fun `onUpdateCrafting should not crash with null host`() {
        assertThatNoException().isThrownBy {
            TaskHelper.onUpdateCrafting(null)
        }
    }
}
