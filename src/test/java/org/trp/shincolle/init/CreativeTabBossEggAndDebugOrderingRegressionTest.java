package org.trp.shincolle.init;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreativeTabBossEggAndDebugOrderingRegressionTest {
    private static final Path MOD_TABS =
            Path.of("src/main/java/org/trp/shincolle/init/ModTabs.java");
    private static final Path MOD_ITEMS =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.java");
    private static final Pattern REGISTER_BOSS_EGG_PATTERN =
            Pattern.compile("registerBossEgg\\(\"");

    @Test
    void creativeTabShouldKeepUsingBossEggLoopInsteadOfManualAccepts() throws IOException {
        String modTabs = Files.readString(MOD_TABS);
        String modItems = Files.readString(MOD_ITEMS);

        assertTrue(modTabs.contains("for (var egg : ModItems.BOSS_EGGS) {\n                    output.accept(egg.get());\n                }"),
                "Creative tab should keep exposing hostile boss eggs through the shared BOSS_EGGS loop");
        assertTrue(modItems.contains("public static final List<DeferredItem<BossSpawnEggItem>> BOSS_EGGS = new ArrayList<>();"),
                "ModItems should keep a shared mutable BOSS_EGGS registry list");
        assertTrue(modItems.contains("BOSS_EGGS.add(egg);"),
                "registerBossEgg should keep appending every registered boss egg into BOSS_EGGS");

        Matcher matcher = REGISTER_BOSS_EGG_PATTERN.matcher(modItems);
        int registeredBossEggs = 0;
        while (matcher.find()) {
            registeredBossEggs++;
        }

        int listedBossEggs = countOccurrences(modItems, "_BOSS_EGG");
        assertTrue(registeredBossEggs > 0,
                "Expected hostile boss eggs to remain registered through registerBossEgg");
        assertTrue(listedBossEggs >= registeredBossEggs,
                "Expected the shared boss egg list flow to keep covering all registered boss egg holders");
    }

    @Test
    void debugInspectorShouldRemainTheLastCreativeTabEntry() throws IOException {
        String modTabs = Files.readString(MOD_TABS);

        int debugSection = modTabs.indexOf("// ===== DEBUG (appended last, not in 1.12.2) =====");
        int debugAccept = modTabs.indexOf("output.accept(ModItems.DEBUG_INSPECTOR.get());");
        int buildEnd = modTabs.indexOf("}).build());");

        assertTrue(debugSection >= 0,
                "Creative tab should keep a dedicated trailing debug section");
        assertTrue(debugAccept > debugSection,
                "Debug inspector should stay inside the trailing debug section");
        assertTrue(buildEnd > debugAccept,
                "Creative tab builder should close after the debug inspector is inserted");
        String trailingSegment = modTabs.substring(debugAccept, buildEnd);
        assertEquals(1, countOccurrences(trailingSegment, "output.accept("),
                "No normal creative-tab entries should be appended after the debug inspector");
    }

    private static int countOccurrences(String source, String needle) {
        int count = 0;
        int index = 0;
        while ((index = source.indexOf(needle, index)) >= 0) {
            count++;
            index += needle.length();
        }
        return count;
    }
}
