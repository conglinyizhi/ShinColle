package org.trp.shincolle.build

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PackageBoundariesRegressionTest {
    private val DOC = Path.of("docs/PACKAGE_BOUNDARIES.md")
    private val API_DIR = Path.of("src/main/java/org/trp/shincolle/api")
    private val COMPAT_BOUNDARY = Path.of("src/main/java/org/trp/shincolle/compat/CompatBoundary.kt")
    private val DATAGEN_BOUNDARY = Path.of("src/main/java/org/trp/shincolle/datagen/DatagenBoundary.kt")

    @Test
    fun packageBoundaryDocShouldKeepReservedCompatApiDatagenGuidance() {
        val doc = Files.readString(DOC)

        assertTrue(doc.contains("# ShinColle Package Boundaries")) {
            "Package boundary doc should keep its dedicated title"
        }
        assertTrue(doc.contains("`api`") && doc.contains("`compat`") && doc.contains("`datagen`")) {
            "Package boundary doc should explicitly mention the reserved api/compat/datagen boundaries"
        }
        assertTrue(doc.contains("当前仅作为目录边界预留")) {
            "Package boundary doc should state that compat/datagen are currently reserved boundaries"
        }
        assertTrue(doc.contains("不把目录预留本身当作主线迁移阻塞项")) {
            "Package boundary doc should keep the non-blocking expectation explicit"
        }
    }

    @Test
    fun reservedBoundaryPackagesShouldExistInSourceTree() {
        assertTrue(Files.isDirectory(API_DIR)) {
            "API package boundary should continue to exist"
        }
        assertTrue(Files.exists(COMPAT_BOUNDARY)) {
            "Compat package boundary marker should exist"
        }
        assertTrue(Files.exists(DATAGEN_BOUNDARY)) {
            "Datagen package boundary marker should exist"
        }

        val compatSource = Files.readString(COMPAT_BOUNDARY)
        val datagenSource = Files.readString(DATAGEN_BOUNDARY)

        assertTrue(compatSource.contains("package org.trp.shincolle.compat")) {
            "Compat boundary marker should live under the compat namespace"
        }
        assertTrue(datagenSource.contains("package org.trp.shincolle.datagen")) {
            "Datagen boundary marker should live under the datagen namespace"
        }
    }
}
