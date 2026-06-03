package org.trp.shincolle.book;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PatchouliBookMetadataRegressionTest {
    private static final Path BOOK_JSON =
            Path.of("src/main/resources/data/shincolle/patchouli_books/shincolle_manual/book.json");
    private static final Path EN_US =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");
    private static final Path JA_JP =
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json");
    private static final Path ZH_CN =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json");
    private static final Path ZH_TW =
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json");
    private static final Path MOD_ITEMS =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.java");
    private static final Path DESK_ITEM_BOOK_RECIPE =
            Path.of("src/main/resources/data/shincolle/recipe/deskitembook.json");
    private static final Path DESK_ITEM_BOOK_MODEL =
            Path.of("src/main/resources/assets/shincolle/models/item/deskitembook.json");
    private static final Path DESK_ITEM_BOOK_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/item/DeskItemBook.java");

    private static final Map<String, Path> MAINTAINED_LANGS = Map.of(
            "en_us", EN_US,
            "ja_jp", JA_JP,
            "zh_cn", ZH_CN,
            "zh_tw", ZH_TW
    );

    @Test
    void patchouliBookMetadataShouldKeepCurrentCoreSettings() throws IOException {
        String book = Files.readString(BOOK_JSON);
        List<String> issues = new ArrayList<>();

        assertContains(book, "\"name\": \"patchouli.shincolle.book.name\"", "book name translation key", issues);
        assertContains(book, "\"landing_text\": \"patchouli.shincolle.book.landing_text\"", "landing text translation key", issues);
        assertContains(book, "\"subtitle\": \"patchouli.shincolle.book.subtitle\"", "subtitle translation key", issues);
        assertContains(book, "\"custom_book_item\": \"shincolle:deskitembook\"", "custom book item deskitembook", issues);
        assertContains(book, "\"model\": \"shincolle:deskitembook\"", "deskitembook model binding", issues);
        assertContains(book, "\"index_icon\": \"shincolle:deskitembook\"", "deskitembook index icon", issues);
        assertContains(book, "\"book_texture\": \"patchouli:textures/gui/book_brown.png\"", "book texture", issues);
        assertContains(book, "\"open_sound\": \"minecraft:item.book.page_turn\"", "open sound", issues);
        assertContains(book, "\"flip_sound\": \"minecraft:item.book.page_turn\"", "flip sound", issues);
        assertContains(book, "\"dont_generate_book\": true", "dont_generate_book=true", issues);
        assertContains(book, "\"use_resource_pack\": true", "use_resource_pack=true", issues);
        assertContains(book, "\"i18n\": true", "i18n=true", issues);
        assertContains(book, "\"text_overflow_mode\": \"resize\"", "resize overflow mode", issues);
        assertContains(book, "\"show_progress\": false", "show_progress=false", issues);
        assertContains(book, "\"use_blocky_font\": false", "use_blocky_font=false", issues);
        assertContains(book, "\"$(thing)\": \"$(l)\"", "thing macro", issues);
        assertContains(book, "\"$(item)\": \"$(6)\"", "item macro", issues);
        assertContains(book, "\"$(ship)\": \"$(c)\"", "ship macro", issues);

        assertTrue(issues.isEmpty(),
                () -> "Patchouli book metadata changed unexpectedly: " + String.join(", ", issues));
    }

    @Test
    void patchouliBookMetadataShouldStayBackedByDeskItemBookResources() throws IOException {
        String modItems = Files.readString(MOD_ITEMS);
        String recipe = Files.readString(DESK_ITEM_BOOK_RECIPE);
        String model = Files.readString(DESK_ITEM_BOOK_MODEL);
        String source = Files.readString(DESK_ITEM_BOOK_SOURCE);
        List<String> issues = new ArrayList<>();

        assertContains(modItems, "ITEMS.register(\"deskitembook\"", "deskitembook item registration", issues);
        assertContains(recipe, "\"id\": \"shincolle:deskitembook\"", "deskitembook recipe result", issues);
        assertContains(model, "\"layer0\": \"shincolle:item/deskitembook\"", "deskitembook item texture binding", issues);
        assertContains(source, "PATCHOULI_BOOK_ID", "Patchouli book id constant", issues);
        assertContains(source, "PatchouliAPI.get().openBookGUI", "Patchouli GUI open path", issues);

        assertTrue(issues.isEmpty(),
                () -> "Patchouli book backing resources changed unexpectedly: " + String.join(", ", issues));
    }

    @Test
    void patchouliBookTranslationKeysShouldExistInAllMaintainedLanguages() throws IOException {
        for (Map.Entry<String, Path> entry : MAINTAINED_LANGS.entrySet()) {
            String lang = Files.readString(entry.getValue());

            assertTrue(lang.contains("\"patchouli.shincolle.book.name\":"),
                    "Language " + entry.getKey() + " should define patchouli.shincolle.book.name");
            assertTrue(lang.contains("\"patchouli.shincolle.book.landing_text\":"),
                    "Language " + entry.getKey() + " should define patchouli.shincolle.book.landing_text");
            assertTrue(lang.contains("\"patchouli.shincolle.book.subtitle\":"),
                    "Language " + entry.getKey() + " should define patchouli.shincolle.book.subtitle");
        }
    }

    private static void assertContains(String content, String expected, String label, List<String> issues) {
        if (!content.contains(expected)) {
            issues.add("missing " + label);
        }
    }
}
