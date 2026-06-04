package org.trp.shincolle.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("shincolle")
@PrefixGameTestTemplate(false)
public final class MinimalGameTestBootstrapTest {
    private MinimalGameTestBootstrapTest() {
    }

    @GameTest(template = "empty")
    public static void emptyTemplateShouldLoad(GameTestHelper helper) {
        helper.succeed();
    }
}
