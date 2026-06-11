package org.trp.shincolle.build

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DatapackCandidateDocRegressionTest {
    private val DOC =
        Path.of("docs/DATAPACK_CANDIDATES.md")

    @Test
    fun datapackCandidateDocShouldKeepPriorityBucketsAndCodeBoundaries() {
        val doc = Files.readString(DOC)

        assertTrue(doc.contains("# ShinColle Datapack / Datagen Candidates")) {
            "Datapack candidate doc should keep its dedicated overview title"
        }
        assertTrue(doc.contains("## 已经数据化的内容")) {
            "Datapack candidate doc should keep the already-datafied section"
        }
        assertTrue(doc.contains("## 优先候选")) {
            "Datapack candidate doc should keep the prioritized datapack candidates section"
        }
        assertTrue(doc.contains("## 暂不建议直接数据化的内容")) {
            "Datapack candidate doc should keep the code-boundary section"
        }
        assertTrue(doc.contains("ShipyardRecipes")) {
            "Datapack candidate doc should explicitly call out ShipyardRecipes as a code-heavy boundary"
        }
        assertTrue(doc.contains("Patchouli")) {
            "Datapack candidate doc should keep Patchouli in the candidate discussion"
        }
        assertTrue(doc.contains("JEI")) {
            "Datapack candidate doc should keep JEI regression coupling called out"
        }
    }
}
