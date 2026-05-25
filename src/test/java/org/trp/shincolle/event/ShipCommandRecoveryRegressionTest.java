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
        assertTrue(source.contains("ship.clearPointerTarget();\n            this.movement.stop();"),
                "Pointer move goal should clear pointer position commands when recovery triggers");
    }

    @Test
    void guardGoalShouldDisableGuardStateAfterRepeatedFailuresOrNoProgress() throws IOException {
        String source = Files.readString(GUARD_GOAL_SOURCE);

        assertTrue(source.contains("private static final int GUARD_MOVE_FAIL_LIMIT = 40;"),
                "Guard goal should define a move failure limit");
        assertTrue(source.contains("private static final int GUARD_STUCK_TICK_LIMIT = 120;"),
                "Guard goal should define a stuck timeout");
        assertTrue(source.contains("private static final int GUARD_TELEPORT_COOLDOWN_TICKS = 100;"),
                "Guard goal should use a cooldown before remote teleport recovery");
        assertTrue(source.contains("tryTeleportRecovery(target, guardedEntity, distSq, false)"),
                "Guard goal should try teleport recovery for distant guard targets before disabling guard");
        assertTrue(source.contains("tryTeleportRecovery(target, guardedEntity, distSq, true)"),
                "Guard goal should force one teleport recovery attempt before disabling stuck guard targets");
        assertTrue(source.contains("this.movement.teleportNearPoint(target, 0.75D)"),
                "Guard block recovery should use the movement coordinator's safe point teleport");
        assertTrue(source.contains("ship.setStateFlag(EntityShipBase.STATE_FLAG_DISABLE_GUARD_POS, true);"),
                "Guard goal should disable guard mode when recovery triggers");
        assertTrue(source.contains("ship.clearGuardTarget();"),
                "Guard goal recovery should clear the stale guarded position");
    }
}
