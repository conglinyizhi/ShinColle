package org.trp.shincolle.loot

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.neoforged.neoforge.common.loot.IGlobalLootModifier
import net.neoforged.neoforge.common.loot.LootModifier
import org.trp.shincolle.Config
import org.trp.shincolle.Config.LootEntry
import org.trp.shincolle.init.ModLootModifiers
import org.trp.shincolle.item.LegacyEquipItem
import java.util.function.BiFunction
import java.util.function.Function
import kotlin.math.max

class LegacyChestLootModifier(conditions: Array<LootItemCondition?>, private val category: String) :
    LootModifier(conditions) {
    fun category(): String {
        return this.category
    }

    override fun doApply(
        generatedLoot: ObjectArrayList<ItemStack?>,
        context: LootContext
    ): ObjectArrayList<ItemStack?> {
        val candidates: MutableList<LootEntry> = ArrayList<LootEntry>()
        var totalWeight = 0
        for (entry in Config.lootEntries) {
            if (!matchesCategory(entry!!.chestId, this.category)) continue
            if (context.getRandom().nextFloat() > entry.chance) continue
            candidates.add(entry)
            totalWeight += entry.weight
        }

        if (candidates.isEmpty() || totalWeight <= 0) {
            return generatedLoot
        }

        val rolls = max(1, candidates.size / 2 + 1)
        val random = context.getRandom()
        for (i in 0..<rolls) {
            val chosen: LootEntry = chooseWeighted(candidates, totalWeight, random.nextInt(totalWeight))
            val stack: ItemStack = createStack(chosen, random)
            if (!stack.isEmpty()) {
                generatedLoot.add(stack)
            }
        }
        return generatedLoot
    }

    override fun codec(): MapCodec<out IGlobalLootModifier?> {
        return ModLootModifiers.LEGACY_CHEST_LOOT.get()!!
    }

    companion object {
        @JvmField
        val CODEC: MapCodec<LegacyChestLootModifier?> =
            RecordCodecBuilder.mapCodec<LegacyChestLootModifier?>(Function { instance: RecordCodecBuilder.Instance<LegacyChestLootModifier?>? ->
                codecStart<LegacyChestLootModifier?>(instance)
                    .and<String?>(
                        Codec.STRING.fieldOf("category")
                            .forGetter<LegacyChestLootModifier?>(Function { obj: LegacyChestLootModifier? -> obj!!.category() })
                    )
                    .apply<LegacyChestLootModifier?>(
                        instance,
                        BiFunction { conditions: Array<LootItemCondition?>?, category: String? ->
                            LegacyChestLootModifier(
                                conditions!!,
                                category!!
                            )
                        })
            })

        private fun chooseWeighted(entries: MutableList<LootEntry>, totalWeight: Int, roll: Int): LootEntry {
            var roll = roll
            var chosen = entries.get(0)
            for (entry in entries) {
                roll -= entry.weight
                if (roll < 0) {
                    chosen = entry
                    break
                }
            }
            return chosen
        }

        private fun createStack(entry: LootEntry, random: RandomSource): ItemStack {
            var count = entry.min
            if (entry.max > entry.min) {
                count += random.nextInt(entry.max - entry.min + 1)
            }

            var stack = ItemStack(entry.item, count)
            if (entry.itemMeta == -1 && stack.getItem() is LegacyEquipItem) {
                val equipItem = stack.getItem() as LegacyEquipItem
                val variant = random.nextInt(equipItem.variantCount)
                stack = equipItem.createVariantStack(variant)
                stack.setCount(count)
            }
            return stack
        }

        private fun matchesCategory(chestId: Int, category: String): Boolean {
            if (category.startsWith("id:")) {
                try {
                    return chestId == category.substring(3).toInt()
                } catch (ignored: NumberFormatException) {
                    return false
                }
            }
            return when (category) {
                "basic" -> chestId >= 0 && chestId <= 3
                "mid" -> chestId >= 4 && chestId <= 8
                "high" -> chestId == 9
                "trial" -> chestId >= 7 && chestId <= 9
                else -> false
            }
        }
    }
}
