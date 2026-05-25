package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipCommandRecoveryRegressionTest {
    private static final Path POINTER_GOAL_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipPointerGoals.java");
    private static final Path GUARD_GOAL_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipGuardGoal.java");

    @Test
    void pointerMoveGoalShouldClearPositionCommandAfterRepeatedFailuresOrNoProgress() throws IOException {
        String source = Files.readString(POINTER_GOAL_SOURCE);

        assertTrue(source.contains("private static final int POINTER_MOVE_FAIL_LIMIT = 40;"),
                "Pointer move goal should define a move failure limit");
        assertTrue(source.contains("private static final int POINTER_MOVE_STUCK_TICK_LIMIT = 120;"),
                "Pointer move goal should define a stuck timeout");
        assertTrue(source.contains("ship.clearPointerTarget();\n                    ship.getNavigation().stop();"),
                "Pointer move goal should clear pointer position commands when recovery triggers");
    }

    @Test
    void guardGoalShouldDisableGuardStateAfterRepeatedFailuresOrNoProgress() throws IOException {
        String source = Files.readString(GUARD_GOAL_SOURCE);

        assertTrue(source.contains("private static final int GUARD_MOVE_FAIL_LIMIT = 40;"),
                "Guard goal should define a move failure limit");
        assertTrue(source.contains("private static final int GUARD_STUCK_TICK_LIMIT = 120;"),
                "Guard goal should define a stuck timeout");
        assertTrue(source.contains("ship.setStateFlag(EntityShipBase.STATE_FLAG_DISABLE_GUARD_POS, true);"),
                "Guard goal should disable guard mode when recovery triggers");
        assertTrue(source.contains("ship.setGuardedPos(-1, -1, -1, 0, 0);"),
                "Guard goal recovery should clear the stale guarded position");
    }
}
