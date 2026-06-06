package org.trp.shincolle.api.consumable

import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.entity.base.EntityShipBase

/**
 * 第三方舰娘消耗品/交互物品扩展接口。
 *
 * 实现此接口的 [net.minecraft.world.item.Item] 可以赋予舰娘以下一种或多种能力：
 * - 右键交互补充（怨念、弹药、回血、心情等）
 * - 被动死亡保护（类似修理女神）
 * - 自动补给（舰娘 fuel 耗尽时自动从 inventory 消耗）
 * - 弹药统计（物品被计入轻/重弹药总量）
 *
 * 所有方法均有默认实现，第三方只需重写需要的能力即可。
 *
 * **使用示例：**
 * ```kotlin
 * class MyAddonConsumable(properties: Properties) : Item(properties), IShipConsumable {
 *     override fun canInteractWithShip(stack: ItemStack, ship: EntityShipBase, player: Player): Boolean = true
 *
 *     override fun onInteractWithShip(stack: ItemStack, ship: EntityShipBase, player: Player): Boolean {
 *         ship.heal(10.0f)
 *         ship.fuel += 500
 *         return true
 *     }
 *
 *     override fun consumeItemOnInteract(stack: ItemStack, ship: EntityShipBase, player: Player): Boolean = false
 * }
 * ```
 */
interface IShipConsumable {

    // ============================================================
    // 右键交互能力
    // ============================================================

    /**
     * 玩家手持此物品右键舰娘时，是否触发交互。
     *
     * 返回 true 时，会阻止后续的默认交互（如坐下 / 打开 GUI）。
     */
    fun canInteractWithShip(stack: ItemStack, ship: EntityShipBase, player: Player): Boolean = false

    /**
     * 执行交互效果。
     *
     * @return 若返回 true，表示交互成功，会根据 [consumeItemOnInteract] 决定是否消耗物品。
     */
    fun onInteractWithShip(stack: ItemStack, ship: EntityShipBase, player: Player): Boolean = false

    /**
     * 交互成功后是否从玩家手中消耗 1 个物品（[ItemStack.shrink]）。
     *
     * 对于可多次使用的物品（如通过 NBT 计数使用次数的修复桶），
     * 返回 false 并在 [onInteractWithShip] 中自行管理耐久/次数。
     */
    fun consumeItemOnInteract(stack: ItemStack, ship: EntityShipBase, player: Player): Boolean = true

    // ============================================================
    // 被动死亡保护
    // ============================================================

    /**
     * 舰娘受到致死伤害时，该物品是否可阻止死亡。
     *
     * 仅在 [hurt] 中伤害 >= 当前生命值且非无敌伤害时评估。
     */
    fun canPreventDeath(stack: ItemStack, ship: EntityShipBase, source: DamageSource): Boolean = false

    /**
     * 触发死亡保护。返回 true 表示成功阻止死亡。
     *
     * 实现方应在此方法中自行消耗/损坏物品（如需）。
     */
    fun onPreventDeath(stack: ItemStack, ship: EntityShipBase, source: DamageSource): Boolean = false

    // ============================================================
    // 自动补给能力（fuel <= 0 时）
    // ============================================================

    /**
     * 舰娘自动补给时（tickAutoSupplies），该物品是否可作为怨念来源被消耗。
     */
    fun canAutoSupplyGrudge(stack: ItemStack, ship: EntityShipBase): Boolean = false

    /**
     * 自动补给时提供的 fuel 量。
     */
    fun getAutoSupplyGrudgeAmount(stack: ItemStack, ship: EntityShipBase): Int = 0

    // ============================================================
    // 弹药能力
    // ============================================================

    /**
     * 该物品是否算作轻弹药（参与 ammoLight 统计）。
     */
    fun isLightAmmo(stack: ItemStack, ship: EntityShipBase): Boolean = false

    /**
     * 该物品是否算作重弹药（参与 ammoHeavy 统计）。
     */
    fun isHeavyAmmo(stack: ItemStack, ship: EntityShipBase): Boolean = false

    /**
     * 每个物品在 [recalculateAmmoCounts] 中提供的轻弹药量。
     */
    fun getLightAmmoValue(stack: ItemStack, ship: EntityShipBase): Int = 1

    /**
     * 每个物品在 [recalculateAmmoCounts] 中提供的重弹药量。
     */
    fun getHeavyAmmoValue(stack: ItemStack, ship: EntityShipBase): Int = 1
}
