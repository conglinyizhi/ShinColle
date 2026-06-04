package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;
import org.trp.shincolle.init.ModItems;
import org.trp.shincolle.item.CombatRationItem;
import org.trp.shincolle.item.ShipTankItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class VariantItemNameLocalizationRegressionTest {
    private record VariantNameExpectation(String nameBase, int variantCount) {
    }

    private static final List<Path> MAINTAINED_LANGS = List.of(
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    );
    private static final Path MOD_ITEMS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.java");

    private static final Pattern INT_ARRAY_PATTERN_TEMPLATE = Pattern.compile(
            "private static final int\\[] %s = new int\\[]\\{([^}]*)};");

    private static final Map<String, String> LEGACY_EQUIP_VARIANT_ARRAYS = Map.ofEntries(
            Map.entry("EquipAirplane", "EQUIP_AIRPLANE_TYPES"),
            Map.entry("EquipAmmo", "EQUIP_AMMO_TYPES"),
            Map.entry("EquipArmor", "EQUIP_ARMOR_TYPES"),
            Map.entry("EquipCannon", "EQUIP_CANNON_TYPES"),
            Map.entry("EquipCatapult", "EQUIP_CATAPULT_TYPES"),
            Map.entry("EquipCompass", "EQUIP_COMPASS_TYPES"),
            Map.entry("EquipDrum", "EQUIP_DRUM_TYPES"),
            Map.entry("EquipFlare", "EQUIP_FLARE_TYPES"),
            Map.entry("EquipMachinegun", "EQUIP_MACHINEGUN_TYPES"),
            Map.entry("EquipRadar", "EQUIP_RADAR_TYPES"),
            Map.entry("EquipSearchlight", "EQUIP_SEARCHLIGHT_TYPES"),
            Map.entry("EquipTorpedo", "EQUIP_TORPEDO_TYPES"),
            Map.entry("EquipTurbine", "EQUIP_TURBINE_TYPES")
    );

    @Test
    void maintainedLanguagesShouldCoverAllVariantItemNameKeys() throws IOException {
        List<VariantNameExpectation> expectations = readExpectations();

        for (Path lang : MAINTAINED_LANGS) {
            Set<String> keys = readKeys(lang);
            List<String> missing = new ArrayList<>();

            for (VariantNameExpectation expectation : expectations) {
                for (int variant = 0; variant < expectation.variantCount(); variant++) {
                    String key = keyFor(expectation.nameBase(), variant);
                    if (!keys.contains(key)) {
                        missing.add(key);
                    }
                }
            }

            assertTrue(missing.isEmpty(),
                    () -> lang.getFileName() + " should define every variant item name key, missing: "
                            + String.join(", ", missing));
        }
    }

    private static List<VariantNameExpectation> readExpectations() throws IOException {
        List<VariantNameExpectation> expectations = new ArrayList<>();
        String modItems = Files.readString(MOD_ITEMS_SOURCE);

        for (Map.Entry<String, String> entry : LEGACY_EQUIP_VARIANT_ARRAYS.entrySet()) {
            expectations.add(new VariantNameExpectation(entry.getKey(), readIntArrayLength(modItems, entry.getValue())));
        }

        expectations.add(new VariantNameExpectation("CombatRation",
                ((CombatRationItem) ModItems.COMBAT_RATION.get()).getVariantCount()));
        expectations.add(new VariantNameExpectation("ShipTank",
                ((ShipTankItem) ModItems.SHIP_TANK.get()).getVariantCount()));
        expectations.add(new VariantNameExpectation("Grudge", 2));
        expectations.add(new VariantNameExpectation("AbyssNugget", 2));

        return expectations;
    }

    private static int readIntArrayLength(String source, String arrayFieldName) {
        Pattern pattern = Pattern.compile(String.format(INT_ARRAY_PATTERN_TEMPLATE.pattern(), arrayFieldName));
        Matcher matcher = pattern.matcher(source);
        assertTrue(matcher.find(), () -> "Expected array field " + arrayFieldName + " to exist");

        String[] values = matcher.group(1).split(",");
        int count = 0;
        for (String value : values) {
            if (!value.trim().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    private static String keyFor(String nameBase, int variant) {
        if ("Grudge".equals(nameBase)) {
            return variant == 0 ? "item.shincolle.grudge" : "item.shincolle.Grudge1.name";
        }

        String suffix = variant == 0 ? "" : String.valueOf(variant);
        return "item.shincolle." + nameBase + suffix + ".name";
    }

    private static Set<String> readKeys(Path file) throws IOException {
        return Files.readAllLines(file).stream()
                .map(String::trim)
                .filter(line -> line.startsWith("\""))
                .map(line -> line.substring(1, line.indexOf('"', 1)))
                .collect(Collectors.toSet());
    }
}
