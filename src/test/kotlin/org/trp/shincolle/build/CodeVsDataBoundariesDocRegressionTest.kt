package org.trp.shincolle.build

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CodeVsDataBoundariesDocRegressionTest {
    private val DOC =
        Path.of("docs/CODE_VS_DATA_BOUNDARIES.md")

    @Test
    fun codeVsDataBoundariesDocShouldKeepRuntimeBoundaryExamples() {
        val doc = Files.readString(DOC)

        assertTrue(doc.contains("# ShinColle Code vs Data Boundaries")) {
            "Code-vs-data boundary doc should keep its dedicated title"
        }
        assertTrue(doc.contains("ShipyardRecipes")) {
            "Code-vs-data boundary doc should explicitly mention ShipyardRecipes"
        }
        assertTrue(doc.contains("LegacyEquipStats")) {
            "Code-vs-data boundary doc should explicitly mention LegacyEquipStats"
        }
        assertTrue(doc.contains("TaskHelper")) {
            "Code-vs-data boundary doc should explicitly mention TaskHelper"
        }
        assertTrue(doc.contains("HostileSpawnManager")) {
            "Code-vs-data boundary doc should explicitly mention HostileSpawnManager"
        }
        assertTrue(doc.contains("EntityShipBase")) {
            "Code-vs-data boundary doc should explicitly mention EntityShipBase runtime behavior"
        }
        assertTrue(doc.contains("代码 + 数据")) {
            "Code-vs-data boundary doc should keep the mixed-governance section"
        }
    }
}
