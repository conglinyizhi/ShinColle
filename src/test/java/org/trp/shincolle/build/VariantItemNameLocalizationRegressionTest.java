package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;
import org.trp.shincolle.init.ModItems;
import org.trp.shincolle.item.CombatRationItem;
import org.trp.shincolle.item.LegacyEquipItem;
import org.trp.shincolle.item.ShipTankItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
        expectations.add(legacyEquipExpectation("EquipAirplane", (LegacyEquipItem) ModItems.EQUIP_AIRPLANE.get()));
        expectations.add(legacyEquipExpectation("EquipAmmo", (LegacyEquipItem) ModItems.EQUIP_AMMO.get()));
        expectations.add(legacyEquipExpectation("EquipArmor", (LegacyEquipItem) ModItems.EQUIP_ARMOR.get()));
        expectations.add(legacyEquipExpectation("EquipCannon", (LegacyEquipItem) ModItems.EQUIP_CANNON.get()));
        expectations.add(legacyEquipExpectation("EquipCatapult", (LegacyEquipItem) ModItems.EQUIP_CATAPULT.get()));
        expectations.add(legacyEquipExpectation("EquipCompass", (LegacyEquipItem) ModItems.EQUIP_COMPASS.get()));
        expectations.add(legacyEquipExpectation("EquipDrum", (LegacyEquipItem) ModItems.EQUIP_DRUM.get()));
        expectations.add(legacyEquipExpectation("EquipFlare", (LegacyEquipItem) ModItems.EQUIP_FLARE.get()));
        expectations.add(legacyEquipExpectation("EquipMachinegun", (LegacyEquipItem) ModItems.EQUIP_MACHINEGUN.get()));
        expectations.add(legacyEquipExpectation("EquipRadar", (LegacyEquipItem) ModItems.EQUIP_RADAR.get()));
        expectations.add(legacyEquipExpectation("EquipSearchlight", (LegacyEquipItem) ModItems.EQUIP_SEARCHLIGHT.get()));
        expectations.add(legacyEquipExpectation("EquipTorpedo", (LegacyEquipItem) ModItems.EQUIP_TORPEDO.get()));
        expectations.add(legacyEquipExpectation("EquipTurbine", (LegacyEquipItem) ModItems.EQUIP_TURBINE.get()));

        expectations.add(new VariantNameExpectation("CombatRation",
                ((CombatRationItem) ModItems.COMBAT_RATION.get()).getVariantCount()));
        expectations.add(new VariantNameExpectation("ShipTank",
                ((ShipTankItem) ModItems.SHIP_TANK.get()).getVariantCount()));
        expectations.add(new VariantNameExpectation("Grudge", 2));
        expectations.add(new VariantNameExpectation("AbyssNugget", 2));

        return expectations;
    }

    private static VariantNameExpectation legacyEquipExpectation(String nameBase, LegacyEquipItem item) {
        return new VariantNameExpectation(nameBase, item.getVariantCount());
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
