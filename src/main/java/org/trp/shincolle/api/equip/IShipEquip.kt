package org.trp.shincolle.api.equip

import net.minecraft.world.item.ItemStack
import org.trp.shincolle.entity.base.EntityShipBase

/**
 * 第三方舰娘装备扩展接口。
 *
 * 实现此接口的 [net.minecraft.world.item.Item] 可以被舰娘放入装备槽，
 * 并提供属性加成与可选的特殊效果。
 *
 * **使用示例：**
 * ```kotlin
 * class MyAddonEquipItem(properties: Properties) : Item(properties), IShipEquip {
 *     override fun getEquipTypeId(stack: ItemStack): Int = 1000
 *     override fun getEquipId(stack: ItemStack): Int = 1000
 *     override fun getMainAttributes(stack: ItemStack): FloatArray =
 *         floatArrayOf(0f, 5f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
 * }
 * ```
 *
 * 属性数组长度必须为 [org.trp.shincolle.item.LegacyEquipStats.ATTR_COUNT]（21），索引含义：
 * 0=HP, 1=火力, 2=雷装, 3=对空火力, 4=对空雷装, 5=装甲, 6=攻速, 7=移速,
 * 8=射程, 9=暴击, 10=双击, 11=三联击, 12=命中补正, 13=对空, 14=对潜,
 * 15=回避, 16=XP加成, 17=怨念加成, 18=弹药加成, 19=HP恢复加成, 20=击退加成。
 *
 * **装备类型 ID 分配建议：**
 * - 0~99：保留给本模组内置装备类型。
 * - 1000+：建议第三方 Addon 使用，避免冲突。
 */
interface IShipEquip {

    /**
     * 返回装备类型 ID。用于特殊效果分类与注册表查找。
     *
     * 相同类型 ID 的装备共享同一个 [ShipEquipSpecialEffect] 处理器。
     */
    fun getEquipTypeId(stack: ItemStack): Int

    /**
     * 返回装备唯一 ID。用于兼容性检查与调试。
     */
    fun getEquipId(stack: ItemStack): Int

    /**
     * 返回主属性数组。
     *
     * 长度必须为 21（[org.trp.shincolle.item.LegacyEquipStats.ATTR_COUNT]）。
     */
    fun getMainAttributes(stack: ItemStack): FloatArray

    /**
     * 返回杂项属性数组（可选）。
     *
     * 索引：0=稀有度, 1=是否限航母, 2=建造消耗...
     * 若返回 null 则表示无杂项属性。
     */
    fun getMiscAttributes(stack: ItemStack): FloatArray? = null
}
