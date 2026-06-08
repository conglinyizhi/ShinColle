package org.trp.shincolle.gametest

import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.neoforged.neoforge.gametest.GameTestHolder
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate

@GameTestHolder("shincolle")
@PrefixGameTestTemplate(false)
class MinimalGameTestBootstrapTest {
    private constructor()

    @GameTest(template = "empty")
    fun emptyTemplateShouldLoad(helper: GameTestHelper) {
        helper.succeed()
    }
}
