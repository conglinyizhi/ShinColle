package org.trp.shincolle.docs

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SingleplayerDedicatedParityChecklistRegressionTest {
    private val DOC = Path.of("docs/SINGLEPLAYER_DEDICATED_PARITY_CHECKLIST.md")

    @Test
    fun parityChecklistShouldKeepIssue1NamedCoverageAndEvidenceAnchors() {
        val source = Files.readString(DOC)

        assertTrue(source.contains("# ShinColle 单人世界与专用服一致性检查清单")) {
            "Parity checklist should keep its dedicated title"
        }
        assertTrue(source.contains("单人世界") && source.contains("专用服")) {
            "Parity checklist should explicitly mention both singleplayer and dedicated server scopes"
        }
        assertTrue(source.contains("## 1. 菜单")) {
            "Parity checklist should keep the menu section"
        }
        assertTrue(source.contains("## 2. 网络同步")) {
            "Parity checklist should keep the network sync section"
        }
        assertTrue(source.contains("## 3. 存档重进")) {
            "Parity checklist should keep the save re-entry section"
        }
        assertTrue(source.contains("## 4. 实体行为")) {
            "Parity checklist should keep the entity behavior section"
        }
        assertTrue(source.contains("BlockMenuProtocolRegressionTest")) {
            "Parity checklist should anchor menu verification to existing protocol regressions"
        }
        assertTrue(source.contains("PayloadClientSyncTest") && source.contains("PayloadPlayerGuardRegressionTest")) {
            "Parity checklist should anchor network verification to payload sync and guard regressions"
        }
        assertTrue(source.contains("PlayerStatePersistenceArchitectureRegressionTest") && source.contains("AdmiralDataPersistenceRegressionTest")) {
            "Parity checklist should anchor persistence verification to existing regression tests"
        }
        assertTrue(source.contains("ENTITY_DIFF_CHECK.md") && source.contains("ShipLegacyNbtCompatibilityRegressionTest")) {
            "Parity checklist should anchor entity verification to existing diff docs and regressions"
        }
        assertTrue(source.contains("当前状态") && source.contains("手工复测")) {
            "Parity checklist should distinguish automated baseline from manual verification work"
        }
    }
}
