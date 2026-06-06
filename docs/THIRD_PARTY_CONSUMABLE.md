# 第三方舰娘消耗品/交互物品扩展接口

> 目标读者：为 ShinColle 开发 Addon 模组的开发者。
> 与装备接口（`IShipEquip`）不同，本接口面向**消耗品、交互物品**（如食物、修复桶、弹药包、修理女神等）。

## 概述

`IShipConsumable` 允许第三方模组创建可被舰娘识别并产生特殊效果的物品。与 `IShipEquip` 不同，这类物品**不需要放入装备槽**。

> **使用方式分为两类：**
> - **被动效果**（死亡保护、自动补给、弹药统计）：物品放入舰娘 cargo Inventory 中即可自动生效。
> - **主动交互**（右键交互）：玩家手持物品右键点击舰娘时触发。这是附加能力，若你的物品只需要被动效果，无需实现右键交互相关方法。

## 快速开始

### 1. 实现 `IShipConsumable` 接口

```kotlin
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.api.consumable.IShipConsumable
import org.trp.shincolle.entity.base.EntityShipBase

class MyAddonConsumable(properties: Properties) : Item(properties), IShipConsumable {

    override fun canInteractWithShip(stack: ItemStack, ship: EntityShipBase, player: Player): Boolean = true

    override fun onInteractWithShip(stack: ItemStack, ship: EntityShipBase, player: Player): Boolean {
        // 右键效果：回血 + 补充怨念
        ship.heal(10.0f)
        ship.fuel += 500
        return true
    }

    // 不自动消耗物品（自行管理耐久或使用次数）
    override fun consumeItemOnInteract(stack: ItemStack, ship: EntityShipBase, player: Player): Boolean = false
}
```

完成这一步后，玩家手持该物品右键舰娘即可触发效果。

## API 参考

### `IShipConsumable`

位于 `org.trp.shincolle.api.consumable.IShipConsumable`。

所有方法均有默认实现，只需重写需要的能力。

#### 右键交互能力（玩家手持主动使用）

> 若你的物品只需要放在 cargo Inventory 中自动生效，**无需实现以下方法**。

| 方法 | 说明 |
|------|------|
| `canInteractWithShip(stack, ship, player)` | 玩家手持此物品右键舰娘时，是否触发交互。返回 true 会阻止默认交互（坐下/GUI）。 |
| `onInteractWithShip(stack, ship, player)` | 执行交互效果。返回 true 表示成功。 |
| `consumeItemOnInteract(stack, ship, player)` | 成功后是否自动 shrink 1 个物品。可多次使用的物品返回 false。 |

#### 被动效果（放入 cargo Inventory 自动生效）

##### 死亡保护

| 方法 | 说明 |
|------|------|
| `canPreventDeath(stack, ship, source)` | 致死伤害时，该物品是否可以触发保护。 |
| `onPreventDeath(stack, ship, source)` | 触发保护效果。返回 true 阻止死亡。需自行消耗物品。 |

**触发条件：** 伤害 >= 当前生命值、非无敌伤害、非解体锤、非主人攻击。

##### 自动补给

| 方法 | 说明 |
|------|------|
| `canAutoSupplyGrudge(stack, ship)` | fuel <= 0 时，该物品是否可被自动消耗。 |
| `getAutoSupplyGrudgeAmount(stack, ship)` | 提供的 fuel 量（会乘以舰娘的怨念消耗系数）。 |

##### 弹药统计

| 方法 | 说明 |
|------|------|
| `isLightAmmo(stack, ship)` | 是否算作轻弹药（参与 `ammoLight` 统计）。 |
| `isHeavyAmmo(stack, ship)` | 是否算作重弹药（参与 `ammoHeavy` 统计）。 |
| `getLightAmmoValue(stack, ship)` | 每个物品提供的轻弹药量（默认 1）。 |
| `getHeavyAmmoValue(stack, ship)` | 每个物品提供的重弹药量（默认 1）。 |

弹药消耗逻辑与内置弹药物品一致：攻击时按物品数量消耗。

## 常见场景示例

### 场景 1：可多次使用的修复桶（NBT 计数）

```kotlin
class ReusableRepairBucket(properties: Properties) : Item(properties), IShipConsumable {

    override fun canInteractWithShip(stack: ItemStack, ship: EntityShipBase, player: Player): Boolean =
        ship.health < ship.maxHealth

    override fun onInteractWithShip(stack: ItemStack, ship: EntityShipBase, player: Player): Boolean {
        ship.heal(ship.maxHealth * 0.1f + 5.0f)
        // 自行管理使用次数
        stack.damageValue++
        if (stack.damageValue >= stack.maxDamage) {
            stack.shrink(1)
        }
        return true
    }

    override fun consumeItemOnInteract(stack: ItemStack, ship: EntityShipBase, player: Player): Boolean = false
}
```

### 场景 2：被动死亡保护（替代修理女神）

```kotlin
class EmergencyRepairKit(properties: Properties) : Item(properties), IShipConsumable {

    override fun canPreventDeath(stack: ItemStack, ship: EntityShipBase, source: DamageSource): Boolean = true

    override fun onPreventDeath(stack: ItemStack, ship: EntityShipBase, source: DamageSource): Boolean {
        ship.health = ship.maxHealth * 0.5f
        stack.shrink(1)
        return true
    }
}
```

放入舰娘 cargo Inventory 即可生效，无需手持。

### 场景 3：自动补给怨念

```kotlin
class AdvancedGrudgePack(properties: Properties) : Item(properties), IShipConsumable {

    override fun canAutoSupplyGrudge(stack: ItemStack, ship: EntityShipBase): Boolean = true

    override fun getAutoSupplyGrudgeAmount(stack: ItemStack, ship: EntityShipBase): Int = 1500
}
```

舰娘 fuel 耗尽时会自动从 cargo Inventory 中消耗此物品。

### 场景 4：同时提供轻/重弹药

```kotlin
class UniversalAmmoBox(properties: Properties) : Item(properties), IShipConsumable {

    override fun isLightAmmo(stack: ItemStack, ship: EntityShipBase): Boolean = true
    override fun isHeavyAmmo(stack: ItemStack, ship: EntityShipBase): Boolean = true
    override fun getLightAmmoValue(stack: ItemStack, ship: EntityShipBase): Int = 60
    override fun getHeavyAmmoValue(stack: ItemStack, ship: EntityShipBase): Int = 30
}
```

放入舰娘 cargo Inventory 即可同时补充轻/重弹药统计。

## 注意事项

1. **右键交互是附加能力**：`IShipConsumable` 的核心设计是 cargo Inventory 中的**被动效果**（死亡保护、自动补给、弹药）。右键交互是额外提供的主动能力，若不需要请勿重写相关方法。
2. **右键交互优先级**：在食物/怨念检查之后、Shift+右键打开 GUI 之前。若 `canInteractWithShip` 返回 true 且 `onInteractWithShip` 返回 true，会跳过默认交互。
3. **死亡保护优先级**：内置 `REPAIR_GODDESS` 优先于 `IShipConsumable`。只有当 Repair Goddess 不可用时，才会扫描 cargo Inventory 中的 `IShipConsumable`。
4. **自动补给优先级**：内置 `GRUDGE` / `GRUDGE_BLOCK` 优先于 `IShipConsumable` 自动补给。
5. **弹药消耗**：`IShipConsumable` 弹药消耗方式和内置弹药一致，每次攻击按物品数量消耗（不是按弹药点数）。
6. **客户端同步**：`onInteractWithShip` 和 `onPreventDeath` 在服务端执行。若需要粒子/音效，请使用服务端 API。

## 版本历史

| 版本 | 变更 |
|------|------|
| 1.21.1-neoforge | 初始引入 `IShipConsumable`。 |
