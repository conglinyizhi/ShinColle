package org.trp.shincolle.book

import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import org.junit.jupiter.api.Assertions.assertTrue

class PatchouliBookMetadataRegressionTest {
    private val BOOK_JSON: Path =
            Path.of("src/main/resources/data/shincolle/patchouli_books/shincolle_manual/book.json")
    private val EN_US: Path =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val JA_JP: Path =
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json")
    private val ZH_CN: Path =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")
    private val ZH_TW: Path =
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    private val MOD_ITEMS: Path =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.kt")
    private val DESK_ITEM_BOOK_RECIPE: Path =
            Path.of("src/main/resources/data/shincolle/recipe/deskitembook.json")
    private val DESK_ITEM_BOOK_MODEL: Path =
            Path.of("src/main/resources/assets/shincolle/models/item/deskitembook.json")
    private val DESK_ITEM_BOOK_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/item/DeskItemBook.kt")

    private val MAINTAINED_LANGS: Map<String, Path> = mapOf(
            "en_us" to EN_US,
            "ja_jp" to JA_JP,
            "zh_cn" to ZH_CN,
            "zh_tw" to ZH_TW
    )

    @Test
    fun patchouliBookMetadataShouldKeepCurrentCoreSettings() {
        val book = Files.readString(BOOK_JSON)
        val issues = ArrayList<String>()

        assertContains(book, "\"name\": \"patchouli.shincolle.book.name\"", "book name translation key", issues)
        assertContains(book, "\"landing_text\": \"patchouli.shincolle.book.landing_text\"", "landing text translation key", issues)
        assertContains(book, "\"subtitle\": \"patchouli.shincolle.book.subtitle\"", "subtitle translation key", issues)
        assertContains(book, "\"custom_book_item\": \"shincolle:deskitembook\"", "custom book item deskitembook", issues)
        assertContains(book, "\"model\": \"shincolle:deskitembook\"", "deskitembook model binding", issues)
        assertContains(book, "\"index_icon\": \"shincolle:deskitembook\"", "deskitembook index icon", issues)
        assertContains(book, "\"book_texture\": \"patchouli:textures/gui/book_brown.png\"", "book texture", issues)
        assertContains(book, "\"open_sound\": \"minecraft:item.book.page_turn\"", "open sound", issues)
        assertContains(book, "\"flip_sound\": \"minecraft:item.book.page_turn\"", "flip sound", issues)
        assertContains(book, "\"dont_generate_book\": true", "dont_generate_book=true", issues)
        assertContains(book, "\"use_resource_pack\": true", "use_resource_pack=true", issues)
        assertContains(book, "\"i18n\": true", "i18n=true", issues)
        assertContains(book, "\"text_overflow_mode\": \"resize\"", "resize overflow mode", issues)
        assertContains(book, "\"show_progress\": false", "show_progress=false", issues)
        assertContains(book, "\"use_blocky_font\": false", "use_blocky_font=false", issues)
        assertContains(book, "\"$(thing)\": \"$(l)\"", "thing macro", issues)
        assertContains(book, "\"$(item)\": \"$(6)\"", "item macro", issues)
        assertContains(book, "\"$(ship)\": \"$(c)\"", "ship macro", issues)

        assertTrue(issues.isEmpty()) {
            "Patchouli book metadata changed unexpectedly: " + issues.joinToString(", ")
        }
    }

    @Test
    fun patchouliBookMetadataShouldStayBackedByDeskItemBookResources() {
        val modItems = Files.readString(MOD_ITEMS)
        val recipe = Files.readString(DESK_ITEM_BOOK_RECIPE)
        val model = Files.readString(DESK_ITEM_BOOK_MODEL)
        val source = Files.readString(DESK_ITEM_BOOK_SOURCE)
        val issues = ArrayList<String>()

        assertContains(modItems, "ITEMS.register(\"deskitembook\"", "deskitembook item registration", issues)
        assertContains(recipe, "\"id\": \"shincolle:deskitembook\"", "deskitembook recipe result", issues)
        assertContains(model, "\"layer0\": \"shincolle:item/deskitembook\"", "deskitembook item texture binding", issues)
        assertContains(source, "PATCHOULI_BOOK_ID", "Patchouli book id constant", issues)
        assertContains(source, "PatchouliAPI.get().openBookGUI", "Patchouli GUI open path", issues)

        assertTrue(issues.isEmpty()) {
            "Patchouli book backing resources changed unexpectedly: " + issues.joinToString(", ")
        }
    }

    @Test
    fun patchouliBookTranslationKeysShouldExistInAllMaintainedLanguages() {
        for (entry in MAINTAINED_LANGS.entries) {
            val lang = Files.readString(entry.value)

            assertTrue(lang.contains("\"patchouli.shincolle.book.name\":")) {
                "Language " + entry.key + " should define patchouli.shincolle.book.name"
            }
            assertTrue(lang.contains("\"patchouli.shincolle.book.landing_text\":")) {
                "Language " + entry.key + " should define patchouli.shincolle.book.landing_text"
            }
            assertTrue(lang.contains("\"patchouli.shincolle.book.subtitle\":")) {
                "Language " + entry.key + " should define patchouli.shincolle.book.subtitle"
            }
        }
    }

    private fun assertContains(content: String, expected: String, label: String, issues: MutableList<String>) {
        if (!content.contains(expected)) {
            issues.add("missing " + label)
        }
    }
}
