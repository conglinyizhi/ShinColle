package org.trp.shincolle.docs

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EntityDiffCheckCoverageRegressionTest {
    private val DOC = Path.of("docs/ENTITY_DIFF_CHECK.md")

    @Test
    fun entityDiffCheckShouldCoverIssue1NamedModules() {
        val source = Files.readString(DOC)

        assertTrue(source.contains("## Issue 1 点名模块补充核对")) {
            "Entity diff document should include a dedicated section for issue 1 named modules"
        }
        assertTrue(source.contains("### `EntityShipBase*`")) {
            "Entity diff document should cover EntityShipBase variants"
        }
        assertTrue(source.contains("### `EntitySummonBase`")) {
            "Entity diff document should cover summon base behavior"
        }
        assertTrue(source.contains("### `EntityMountBase`")) {
            "Entity diff document should cover mount base behavior"
        }
        assertTrue(source.contains("### `GoalShipAircraftAttack`")) {
            "Entity diff document should cover the legacy aircraft attack goal mapping"
        }
        assertTrue(source.contains("### `ShipLegacyPath*`")) {
            "Entity diff document should cover the legacy ship path family"
        }
        assertTrue(source.contains("EntityShipBaseCombat.tryPerformAircraftCycle()")) {
            "Aircraft attack section should name the new combat entry point"
        }
        assertTrue(source.contains("ShipLegacyNavigation") && source.contains("ShipLegacyPathFinder")) {
            "Path section should name the migrated navigation and path finder classes"
        }
    }
}
