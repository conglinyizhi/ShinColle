package org.trp.shincolle.build

import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.item.CombatRationItem
import org.trp.shincolle.item.LegacyEquipItem
import org.trp.shincolle.item.ShipTankItem

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.stream.Collectors

import org.junit.jupiter.api.Assertions.assertTrue

class VariantItemNameLocalizationRegressionTest {
    private data class VariantNameExpectation(val nameBase: String, val variantCount: Int)

    private val MAINTAINED_LANGS: List<Path> = listOf(
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    )

    @Test
    fun maintainedLanguagesShouldCoverAllVariantItemNameKeys() {
        val expectations = readExpectations()

        for (lang in MAINTAINED_LANGS) {
            val keys = readKeys(lang)
            val missing = ArrayList<String>()

            for (expectation in expectations) {
                for (variant in 0 until expectation.variantCount) {
                    val key = keyFor(expectation.nameBase, variant)
                    if (!keys.contains(key)) {
                        missing.add(key)
                    }
                }
            }

            assertTrue(missing.isEmpty()) {
                lang.fileName.toString() + " should define every variant item name key, missing: " +
                        missing.joinToString(", ")
            }
        }
    }

    private fun readExpectations(): List<VariantNameExpectation> {
        val expectations = ArrayList<VariantNameExpectation>()
        expectations.add(legacyEquipExpectation("EquipAirplane", ModItems.EQUIP_AIRPLANE.get() as LegacyEquipItem))
        expectations.add(legacyEquipExpectation("EquipAmmo", ModItems.EQUIP_AMMO.get() as LegacyEquipItem))
        expectations.add(legacyEquipExpectation("EquipArmor", ModItems.EQUIP_ARMOR.get() as LegacyEquipItem))
        expectations.add(legacyEquipExpectation("EquipCannon", ModItems.EQUIP_CANNON.get() as LegacyEquipItem))
        expectations.add(legacyEquipExpectation("EquipCatapult", ModItems.EQUIP_CATAPULT.get() as LegacyEquipItem))
        expectations.add(legacyEquipExpectation("EquipCompass", ModItems.EQUIP_COMPASS.get() as LegacyEquipItem))
        expectations.add(legacyEquipExpectation("EquipDrum", ModItems.EQUIP_DRUM.get() as LegacyEquipItem))
        expectations.add(legacyEquipExpectation("EquipFlare", ModItems.EQUIP_FLARE.get() as LegacyEquipItem))
        expectations.add(legacyEquipExpectation("EquipMachinegun", ModItems.EQUIP_MACHINEGUN.get() as LegacyEquipItem))
        expectations.add(legacyEquipExpectation("EquipRadar", ModItems.EQUIP_RADAR.get() as LegacyEquipItem))
        expectations.add(legacyEquipExpectation("EquipSearchlight", ModItems.EQUIP_SEARCHLIGHT.get() as LegacyEquipItem))
        expectations.add(legacyEquipExpectation("EquipTorpedo", ModItems.EQUIP_TORPEDO.get() as LegacyEquipItem))
        expectations.add(legacyEquipExpectation("EquipTurbine", ModItems.EQUIP_TURBINE.get() as LegacyEquipItem))

        expectations.add(VariantNameExpectation("CombatRation",
                (ModItems.COMBAT_RATION.get() as CombatRationItem).variantCount))
        expectations.add(VariantNameExpectation("ShipTank",
                (ModItems.SHIP_TANK.get() as ShipTankItem).variantCount))
        expectations.add(VariantNameExpectation("Grudge", 2))
        expectations.add(VariantNameExpectation("AbyssNugget", 2))

        return expectations
    }

    private fun legacyEquipExpectation(nameBase: String, item: LegacyEquipItem): VariantNameExpectation {
        return VariantNameExpectation(nameBase, item.variantCount)
    }

    private fun keyFor(nameBase: String, variant: Int): String {
        if (nameBase == "Grudge") {
            return if (variant == 0) "item.shincolle.grudge" else "item.shincolle.Grudge1.name"
        }

        val suffix = if (variant == 0) "" else variant.toString()
        return "item.shincolle.$nameBase$suffix.name"
    }

    private fun readKeys(file: Path): Set<String> {
        return Files.readAllLines(file).stream()
                .map(String::trim)
                .filter { it.startsWith("\"") }
                .map { it.substring(1, it.indexOf('"', 1)) }
                .collect(Collectors.toSet())
    }
}
