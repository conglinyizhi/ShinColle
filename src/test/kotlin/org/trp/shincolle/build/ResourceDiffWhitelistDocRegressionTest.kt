package org.trp.shincolle.build

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ResourceDiffWhitelistDocRegressionTest {
    private val DOC =
        Path.of("docs/RESOURCE_DIFF_WHITELIST.md")

    @Test
    fun resourceDiffWhitelistDocShouldKeepThreeWayClassification() {
        val doc = Files.readString(DOC)

        assertTrue(doc.contains("# ShinColle Resource Diff Whitelist")) {
            "Resource diff whitelist doc should keep its dedicated title"
        }
        assertTrue(doc.contains("## A. 应保留的已迁移资源")) {
            "Resource diff whitelist doc should keep the migrated-resource whitelist section"
        }
        assertTrue(doc.contains("## B. 故意不迁移的旧资源类型")) {
            "Resource diff whitelist doc should keep the intentionally-not-migrated section"
        }
        assertTrue(doc.contains("## C. 当前仍待清理或核对的范围")) {
            "Resource diff whitelist doc should keep the pending-cleanup section"
        }
        assertTrue(doc.contains("Patchouli")) {
            "Resource diff whitelist doc should explicitly mention Patchouli resource migration"
        }
        assertTrue(doc.contains("recipe")) {
            "Resource diff whitelist doc should explicitly mention remaining recipe cleanup scope"
        }
        assertTrue(doc.contains("旧 Forge API")) {
            "Resource diff whitelist doc should explicitly mention old Forge API bound resources"
        }
    }
}
