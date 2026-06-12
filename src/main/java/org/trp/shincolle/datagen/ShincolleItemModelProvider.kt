package org.trp.shincolle.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.common.data.ExistingFileHelper
import org.trp.shincolle.Shincolle

/**
 * Generates item models for ShinColle.
 *
 * Spawn eggs and boss eggs are grouped by their shared base texture so that
 * identical models are no longer maintained as hand-written JSON files.
 */
class ShincolleItemModelProvider(
    output: PackOutput,
    existingFileHelper: ExistingFileHelper
) : ItemModelProvider(output, Shincolle.MODID, existingFileHelper) {

    override fun registerModels() {
        // Destroyer eggs (shipspawnegg0)
        spawnEggGroup(
            layer0 = "shipspawnegg0",
            isBoss = false,
            "destroyer_akatsuki", "destroyer_ha", "destroyer_hibiki",
            "destroyer_i", "destroyer_ikazuchi", "destroyer_inazuma",
            "destroyer_ni", "destroyer_ro", "destroyer_shimakaze"
        )
        bossEggGroup(
            layer0 = "shipspawnegg0",
            "destroyer_akatsuki", "destroyer_ha", "destroyer_hibiki",
            "destroyer_i", "destroyer_ikazuchi", "destroyer_inazuma",
            "destroyer_ni", "destroyer_ro", "destroyer_shimakaze"
        )

        // Light cruiser eggs (shipspawnegg1)
        spawnEggGroup(layer0 = "shipspawnegg1", isBoss = false, "cruiser_tatsuta", "cruiser_tenryuu")
        bossEggGroup(layer0 = "shipspawnegg1", "cruiser_tatsuta", "cruiser_tenryuu")

        // Heavy cruiser eggs (shipspawnegg2)
        spawnEggGroup(
            layer0 = "shipspawnegg2",
            isBoss = false,
            "cruiser_atago", "cruiser_takao", "heavy_cruiser_ne", "heavy_cruiser_ri"
        )
        bossEggGroup(layer0 = "shipspawnegg2", "cruiser_atago", "cruiser_takao", "heavy_cruiser_ne", "heavy_cruiser_ri")

        // Battleship eggs (shipspawnegg3)
        spawnEggGroup(
            layer0 = "shipspawnegg3",
            isBoss = false,
            "battleship_nagato", "battleship_re", "battleship_ru",
            "battleship_ta", "battleship_yamato",
            "bb_haruna", "bb_hiei", "bb_kirishima", "bb_kongou"
        )
        bossEggGroup(
            layer0 = "shipspawnegg3",
            "battleship_nagato", "battleship_re", "battleship_ru",
            "battleship_ta", "battleship_yamato",
            "bb_haruna", "bb_hiei", "bb_kirishima", "bb_kongou"
        )

        // Submarine eggs (shipspawnegg5)
        spawnEggGroup(
            layer0 = "shipspawnegg5",
            isBoss = false,
            "ssnh", "subm_ka", "subm_ro500", "subm_so", "subm_u511", "subm_yo"
        )
        bossEggGroup(layer0 = "shipspawnegg5", "ssnh", "subm_ka", "subm_ro500", "subm_so", "subm_u511", "subm_yo")

        // Hime eggs (shipspawnegg7)
        spawnEggGroup(
            layer0 = "shipspawnegg7",
            isBoss = false,
            "airfield_hime", "battleship_hime", "ca_hime", "carrier_hime",
            "destroyer_hime", "harbour_hime", "isolated_hime",
            "midway_hime", "northern_hime", "subm_hime"
        )
        bossEggGroup(
            layer0 = "shipspawnegg7",
            "airfield_hime", "battleship_hime", "ca_hime", "carrier_hime",
            "destroyer_hime", "harbour_hime", "isolated_hime",
            "midway_hime", "northern_hime", "subm_hime"
        )

        // Carrier eggs (shipspawnegg8)
        spawnEggGroup(layer0 = "shipspawnegg8", isBoss = false, "carrier_akagi", "carrier_kaga", "carrier_wo")
        bossEggGroup(layer0 = "shipspawnegg8", "carrier_akagi", "carrier_kaga", "carrier_wo")
    }

    private fun spawnEggGroup(layer0: String, isBoss: Boolean, vararg names: String) {
        for (name in names) {
            val suffix = if (isBoss) "boss_egg" else "spawn_egg"
            val builder = getBuilder("${name}_$suffix")
                .parent(ModelFile.UncheckedModelFile("minecraft:item/generated"))
                .texture("layer0", modLoc("item/$layer0"))
            if (isBoss) {
                builder.texture("layer1", modLoc("item/boss_dot"))
            }
        }
    }

    private fun bossEggGroup(layer0: String, vararg names: String) {
        spawnEggGroup(layer0, true, *names)
    }
}
