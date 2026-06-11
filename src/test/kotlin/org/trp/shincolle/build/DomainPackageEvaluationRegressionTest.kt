package org.trp.shincolle.build

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DomainPackageEvaluationRegressionTest {
    private val DOC = Path.of("docs/DOMAIN_PACKAGE_EVALUATION.md")

    @Test
    fun domainPackageEvaluationDocShouldKeepFiveNamedDomainsAndRecommendation() {
        val doc = Files.readString(DOC)

        assertTrue(doc.contains("# ShinColle Domain Package Evaluation")) {
            "Domain package evaluation doc should keep its dedicated title"
        }
        assertTrue(doc.contains("### 1. `ship`")) {
            "Domain package evaluation doc should cover the ship domain"
        }
        assertTrue(doc.contains("### 2. `fleet`")) {
            "Domain package evaluation doc should cover the fleet domain"
        }
        assertTrue(doc.contains("### 3. `diplomacy`")) {
            "Domain package evaluation doc should cover the diplomacy domain"
        }
        assertTrue(doc.contains("### 4. `automation`")) {
            "Domain package evaluation doc should cover the automation domain"
        }
        assertTrue(doc.contains("### 5. `manual`")) {
            "Domain package evaluation doc should cover the manual domain"
        }
        assertTrue(doc.contains("不进行仓库级大规模领域迁包")) {
            "Domain package evaluation doc should keep the current recommendation explicit"
        }
        assertTrue(doc.contains("`diplomacy`") && doc.contains("`fleet`") && doc.contains("`automation`")) {
            "Domain package evaluation doc should state the current internal business-boundary priorities"
        }
        assertTrue(doc.contains("后续若真的启动领域拆包，应优先从 `diplomacy` 和 `fleet` 两块开始")) {
            "Domain package evaluation doc should keep the current split priority conclusion"
        }
    }
}
