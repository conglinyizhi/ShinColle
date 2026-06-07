package org.trp.shincolle.api.equip

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import org.trp.shincolle.item.LegacyEquipStats

/**
 * 装备附魔属性加成辅助类。
 *
 * 提供一套默认的原版附魔 → 舰娘属性映射逻辑。
 * 第三方 Addon 可以在 [IShipEquip.getEnchantmentBonusAttributes] 中直接调用 [getDefaultEnchantmentBonus]，
 * 也可以完全自行实现加成规则。
 */
object ShipEquipEnchantmentHelper {

    /**
     * 根据物品上的原版附魔，计算默认的舰娘属性加成。
     *
     * @return 长度为 [LegacyEquipStats.ATTR_COUNT]（21）的 FloatArray，无加成时返回全 0 数组。
     */
    @JvmStatic
    fun getDefaultEnchantmentBonus(stack: ItemStack): FloatArray {
        val bonuses = FloatArray(LegacyEquipStats.ATTR_COUNT)
        val enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack)
        if (enchantments.isEmpty) {
            return bonuses
        }

        for (holder in enchantments.keySet()) {
            val level = enchantments.getLevel(holder)
            if (level <= 0) continue

            val key = holder.unwrapKey()
                .map { it.location().toString() }
                .orElse(null) ?: continue

            when (key) {
                "minecraft:sharpness",
                "minecraft:power" -> bonuses[1] += level * 1.0f

                "minecraft:smite",
                "minecraft:bane_of_arthropods" -> bonuses[13] += level * 1.5f

                "minecraft:impaling" -> bonuses[2] += level * 1.5f

                "minecraft:protection",
                "minecraft:fire_protection",
                "minecraft:blast_protection",
                "minecraft:projectile_protection" -> bonuses[5] += level * 0.01f

                "minecraft:efficiency" -> bonuses[6] += level * 0.05f

                "minecraft:unbreaking" -> bonuses[0] += level * 2.0f

                "minecraft:looting" -> bonuses[16] += level * 0.02f
                "minecraft:fortune" -> bonuses[17] += level * 0.02f

                "minecraft:luck_of_the_sea" -> bonuses[15] += level * 0.01f
                "minecraft:lure" -> bonuses[12] += level * 0.01f

                "minecraft:feather_falling",
                "minecraft:depth_strider",
                "minecraft:swift_sneak" -> bonuses[7] += level * 0.01f

                "minecraft:soul_speed",
                "minecraft:riptide" -> bonuses[7] += level * 0.02f

                "minecraft:sweeping_edge" -> bonuses[10] += level * 0.02f
                "minecraft:channeling" -> bonuses[9] += level * 0.02f

                "minecraft:loyalty" -> bonuses[12] += level * 0.01f
                "minecraft:mending" -> bonuses[19] += level * 0.01f
                "minecraft:thorns" -> bonuses[20] += level * 0.02f

                "minecraft:respiration" -> bonuses[14] += level * 1.0f
                "minecraft:aqua_affinity" -> bonuses[14] += 2.0f

                else -> {}
            }
        }

        return bonuses
    }
}
