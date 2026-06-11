package org.trp.shincolle.build

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DataDefinitionCandidatesDocRegressionTest {
    private val DOC =
        Path.of("docs/DATA_DEFINITION_CANDIDATES.md")

    @Test
    fun dataDefinitionCandidateDocShouldKeepThreeMigrationTracks() {
        val doc = Files.readString(DOC)

        assertTrue(doc.contains("# ShinColle Data Definition Candidates")) {
            "Data definition candidate doc should keep its dedicated overview title"
        }
        assertTrue(doc.contains("## 优先候选")) {
            "Data definition candidate doc should keep the prioritized candidate section"
        }
        assertTrue(doc.contains("LegacyEquipStats")) {
            "Data definition candidate doc should explicitly mention LegacyEquipStats"
        }
        assertTrue(doc.contains("ModEntities")) {
            "Data definition candidate doc should explicitly mention ModEntities"
        }
        assertTrue(doc.contains("ModSounds")) {
            "Data definition candidate doc should explicitly mention ModSounds"
        }
        assertTrue(doc.contains("ShipCustomSoundType")) {
            "Data definition candidate doc should explicitly mention ShipCustomSoundType"
        }
        assertTrue(doc.contains("代码 + 数据")) {
            "Data definition candidate doc should keep the mixed code-plus-data migration boundary"
        }
    }
}
