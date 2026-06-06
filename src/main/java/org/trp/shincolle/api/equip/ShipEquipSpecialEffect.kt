package org.trp.shincolle.api.equip

import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.projectile.EntityAbyssMissile

/**
 * 舰娘装备特殊效果处理器。
 *
 * 第三方可通过 [ShipEquipRegistry.register] 注册自定义效果处理器，
 * 为特定装备类型 ID 添加特殊行为（如自动抽水、区块加载、弹药特效等）。
 *
 * **使用示例：**
 * ```kotlin
 * ShipEquipRegistry.register(object : ShipEquipSpecialEffect {
 *     override fun getEquipTypeId(): Int = 1000
 *
 *     override fun tick(ship: EntityShipBase, stack: ItemStack) {
 *         // 每 tick 给舰娘回血
 *         if (ship.level().gameTime % 100L == 0L) {
 *             ship.heal(1.0f)
 *         }
 *     }
 * })
 * ```
 */
interface ShipEquipSpecialEffect {

    /**
     * 返回该效果处理器对应的装备类型 ID。
     */
    fun getEquipTypeId(): Int

    /**
     * 属性收集阶段调用。返回要写入 `stateMinor` 的计数/状态值。
     *
     * @return 要累加的计数，通常为 1；返回 0 表示不计数。
     */
    fun collectCount(ship: EntityShipBase, stack: ItemStack): Int = 1

    /**
     * 创建重攻击导弹时调用，允许修改导弹参数或附加效果。
     */
    fun applyToMissile(ship: EntityShipBase, missile: EntityAbyssMissile, stack: ItemStack) {}

    /**
     * 舰娘执行轻攻击时调用。
     *
     * 可在此回调中消耗装备耐久、触发额外效果等。
     *
     * @param target 被攻击目标，可能为 null
     * @return 若返回 `true`，表示该装备已"处理"此次攻击（可用于拦截或替代默认行为）
     */
    fun onLightAttack(ship: EntityShipBase, stack: ItemStack, target: Entity?): Boolean = false

    /**
     * 舰娘执行重攻击（发射导弹）时调用。
     *
     * @param target 被攻击目标，可能为 null
     * @param missile 已创建但尚未加入世界的导弹实体
     * @return 若返回 `true`，表示该装备已"处理"此次攻击
     */
    fun onHeavyAttack(
        ship: EntityShipBase,
        stack: ItemStack,
        target: Entity?,
        missile: EntityAbyssMissile?
    ): Boolean = false

    /**
     * 每 tick 调用（如果装备在装备槽中且舰娘存活）。
     */
    fun tick(ship: EntityShipBase, stack: ItemStack) {}
}
