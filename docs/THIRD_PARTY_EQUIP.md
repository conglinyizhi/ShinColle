# 第三方舰娘装备扩展接口

> 目标读者：为 ShinColle 开发 Addon 模组的开发者。

## 概述

ShinColle 从 `1.21.1-neoforge` 版本开始提供 `IShipEquip` 接口，允许第三方模组创建可被舰娘放入装备槽、提供属性加成和特殊效果的自定义装备。

## 快速开始

### 1. 实现 `IShipEquip` 接口

让你的物品类实现 `IShipEquip`：

```kotlin
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.api.equip.IShipEquip

class MyAddonEquipItem(properties: Properties) : Item(properties), IShipEquip {

    /** 装备类型 ID。相同类型共享同一个 ShipEquipSpecialEffect 处理器。 */
    override fun getEquipTypeId(stack: ItemStack): Int = 1000

    /** 装备唯一 ID，用于调试和兼容性检查。 */
    override fun getEquipId(stack: ItemStack): Int = 1000

    /**
     * 主属性数组。长度必须为 21，索引含义：
     * 0=HP, 1=火力, 2=雷装, 3=对空火力, 4=对空雷装, 5=装甲, 6=攻速, 7=移速,
     * 8=射程, 9=暴击, 10=双击, 11=三联击, 12=命中补正, 13=对空, 14=对潜,
     * 15=回避, 16=XP加成, 17=怨念加成, 18=弹药加成, 19=HP恢复加成, 20=击退加成。
     */
    override fun getMainAttributes(stack: ItemStack): FloatArray =
        floatArrayOf(0f, 5f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
}
```

完成这一步后，该物品即可被舰娘放入装备槽，并自动提供属性加成。

### 2. 注册特殊效果（可选）

如果你的装备需要**属性加成之外**的特殊行为（如开火消耗耐久、每 tick 回血、导弹特效等），需要额外注册 `ShipEquipSpecialEffect`：

```kotlin
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.api.equip.ShipEquipRegistry
import org.trp.shincolle.api.equip.ShipEquipSpecialEffect
import org.trp.shincolle.entity.base.EntityShipBase

// 在模组初始化时注册（如 FMLCommonSetupEvent）
ShipEquipRegistry.register(object : ShipEquipSpecialEffect {
    override fun getEquipTypeId(): Int = 1000

    override fun tick(ship: EntityShipBase, stack: ItemStack) {
        // 每 5 秒给舰娘回 1 点血
        if (ship.level().gameTime % 100L == 0L) {
            ship.heal(1.0f)
        }
    }
})
```

## API 参考

### `IShipEquip`

位于 `org.trp.shincolle.api.equip.IShipEquip`。

| 方法 | 说明 |
|------|------|
| `getEquipTypeId(stack)` | 返回装备类型 ID。决定了使用哪个 `ShipEquipSpecialEffect`。 |
| `getEquipId(stack)` | 返回装备唯一 ID。 |
| `getMainAttributes(stack)` | 返回长度为 21 的属性数组。 |
| `getMiscAttributes(stack)` | 可选。返回杂项属性，null 表示无。 |

**装备类型 ID 分配建议：**
- `0~99`：保留给 ShinColle 内置装备。
- `1000+`：建议第三方 Addon 使用，避免冲突。

### `ShipEquipSpecialEffect`

位于 `org.trp.shincolle.api.equip.ShipEquipSpecialEffect`。

| 回调 | 触发时机 | 典型用途 |
|------|---------|---------|
| `collectCount(ship, stack)` | 舰娘属性重算时（每 40 tick） | 统计鼓、罗盘、探照灯等计数 |
| `applyToMissile(ship, missile, stack)` | 重攻击导弹创建后、加入世界前 | 给导弹附加中毒/凋零等冲击效果 |
| `onLightAttack(ship, stack, target)` | 轻攻击**弹药已消耗、伤害未打出前** | 消耗耐久、触发额外特效 |
| `onHeavyAttack(ship, stack, target, missile)` | 重攻击**导弹已创建、未加入世界前** | 消耗耐久、修改导弹参数 |
| `tick(ship, stack)` | 舰娘每 tick（装备在槽中且存活） | 持续回血、发光、计时器等 |

所有回调都有**默认空实现**，只需重写你需要的即可。

### `ShipEquipRegistry`

位于 `org.trp.shincolle.api.equip.ShipEquipRegistry`。

| 方法 | 说明 |
|------|------|
| `register(effect)` | 注册一个效果处理器。同一类型 ID 后注册覆盖先前的。 |
| `getEffect(typeId)` | 获取指定类型 ID 的效果处理器，未注册返回 null。 |
| `hasEffect(typeId)` | 检查是否已注册。 |

**注册时机**：建议在 `FMLCommonSetupEvent` 中执行，确保服务端和客户端都注册。

## 常见场景示例

### 场景 1：开火后消耗耐久

```kotlin
ShipEquipRegistry.register(object : ShipEquipSpecialEffect {
    override fun getEquipTypeId(): Int = 1000

    override fun onLightAttack(ship: EntityShipBase, stack: ItemStack, target: net.minecraft.world.entity.Entity?): Boolean {
        stack.hurtAndBreak(1, ship, null)
        return false // false = 不拦截默认攻击
    }

    override fun onHeavyAttack(ship: EntityShipBase, stack: ItemStack, target: net.minecraft.world.entity.Entity?, missile: org.trp.shincolle.entity.projectile.EntityAbyssMissile?): Boolean {
        stack.hurtAndBreak(2, ship, null)
        return false
    }
})
```

### 场景 2：两件套效果（tick 检测）

```kotlin
ShipEquipRegistry.register(object : ShipEquipSpecialEffect {
    override fun getEquipTypeId(): Int = 1001

    override fun tick(ship: EntityShipBase, stack: ItemStack) {
        val inv = ship.inventory ?: return
        var count = 0
        for (i in 0..<inv.slots) {
            val s = inv.getStackInSlot(i)
            if (!s.isEmpty() && s.item is IShipEquip && (s.item as IShipEquip).getEquipTypeId(s) == 1001) {
                count++
            }
        }
        if (count >= 2 && ship.level().gameTime % 100L == 0L) {
            ship.heal(2.0f)
        }
    }
})
```

### 场景 3：给导弹附加冲击效果

```kotlin
ShipEquipRegistry.register(object : ShipEquipSpecialEffect {
    override fun getEquipTypeId(): Int = 1002

    override fun applyToMissile(ship: EntityShipBase, missile: org.trp.shincolle.entity.projectile.EntityAbyssMissile, stack: ItemStack) {
        missile.addImpactEffect(
            net.minecraft.core.Holder.direct(net.minecraft.world.effect.MobEffects.POISON),
            0, // 等级
            120, // 持续时间（tick）
            50 // 触发概率（%）
        )
    }
})
```

## 注意事项

1. **属性数组长度**：`getMainAttributes()` 返回的 `FloatArray` 长度必须是 21。超出部分会被忽略，不足会导致 `IndexOutOfBoundsException`。
2. **状态持久化**：`IShipEquip` 的属性值是**实时计算**的（通过 `getMainAttributes(stack)` 调用），因此你可以根据 NBT、耐久度等动态变化属性。
3. **客户端同步**：`ShipEquipSpecialEffect.tick()` 在**服务端和客户端**都会调用。如果你的效果只需要在服务端执行（如区块加载、生成实体），请检查 `!ship.level().isClientSide`。
4. **性能**：`collectCount()` 每 40 tick 调用一次；`tick()` 每 tick 调用。请避免在 `tick()` 中进行重型计算。

## 版本历史

| 版本 | 变更 |
|------|------|
| 1.21.1-neoforge | 初始引入 `IShipEquip`、`ShipEquipSpecialEffect`、`ShipEquipRegistry`。 |
