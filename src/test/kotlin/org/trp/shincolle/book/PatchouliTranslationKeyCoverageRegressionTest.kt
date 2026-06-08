package org.trp.shincolle.book

import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertTrue

class PatchouliTranslationKeyCoverageRegressionTest {
    private val PATCHOULI_ROOT: Path =
            Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us")
    private val EN_US_LANG: Path =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG: Path =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")
    private val PATCHOULI_KEY_PATTERN: Pattern =
            Pattern.compile("patchouli\\.shincolle\\.[A-Za-z0-9_./-]+")

    @Test
    fun patchouliEntriesShouldOnlyReferenceExistingEnglishTranslationKeys() {
        assertPatchouliEntriesReferenceExistingTranslationKeys(EN_US_LANG, "English")
    }

    @Test
    fun patchouliEntriesShouldOnlyReferenceExistingChineseTranslationKeys() {
        assertPatchouliEntriesReferenceExistingTranslationKeys(ZH_CN_LANG, "Chinese")
    }

    private fun assertPatchouliEntriesReferenceExistingTranslationKeys(langFile: Path, label: String) {
        val lang = Files.readString(langFile)
        val missing = ArrayList<String>()

        Files.walk(PATCHOULI_ROOT).use { stream ->
            for (json in Iterable { stream.filter(Files::isRegularFile).filter { path -> path.toString().endsWith(".json") }.iterator() }) {
                val content = Files.readString(json)
                val matcher = PATCHOULI_KEY_PATTERN.matcher(content)
                while (matcher.find()) {
                    val key = matcher.group()!!
                    if (!lang.contains("\"" + key + "\"")) {
                        missing.add(PATCHOULI_ROOT.relativize(json).toString() + " -> " + key)
                    }
                }
            }
        }

        assertTrue(missing.isEmpty()) {
            "Patchouli entries must only reference existing " + label +
                    " translation keys, missing: " + missing.joinToString(", ")
        }
    }
}
