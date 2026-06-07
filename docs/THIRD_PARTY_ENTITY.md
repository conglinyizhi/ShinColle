# 第三方舰娘实体扩展接口

> 目标读者：为 ShinColle 开发 Addon 模组的开发者。

## 概述

ShinColle 从 `1.21.1-neoforge` 版本开始提供 `IShipAttackEffect` 接口与 `ShipAttackEvent` 事件，允许第三方模组：

1. 为自定义舰娘实体定制攻击时的声音与粒子效果。
2. 通过 NeoForge 事件总线监听舰娘攻击，修改伤害、取消攻击或附加额外效果。

## 快速开始

### 1. 实现 `IShipAttackEffect` 接口

让你的舰娘实体类（需继承 [EntityShipBase]）实现 `IShipAttackEffect`：

```kotlin
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.LivingEntity
import org.trp.shincolle.api.entity.IShipAttackEffect
import org.trp.shincolle.entity.base.EntityShipBase

class MyCustomShip(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level), IShipAttackEffect {

    override fun onLightAttackSound(ship: EntityShipBase, target: LivingEntity?): SoundEvent? {
        // 30% 概率播放自定义声音
        return if (this.random.nextFloat() < 0.3f) {
            SoundEvents.PLAYER_ATTACK_CRIT
        } else null
    }

    override fun onHeavyAttackSound(ship: EntityShipBase, target: LivingEntity?): SoundEvent? {
        return null // 使用默认重攻击声音
    }

    override fun onLightAttackParticles(ship: EntityShipBase, target: LivingEntity?): Boolean {
        // 返回 true 表示自行处理粒子，不再播放默认粒子
        return false
    }

    override fun onHeavyAttackParticles(ship: EntityShipBase, target: LivingEntity?): Boolean {
        return false
    }
}
```

若不需要自定义效果，**无需任何操作**：`EntityShipBase` 已实现 `IShipAttackEffect` 并提供了默认的空实现（返回 `null` / `false`）。

### 2. 监听 `ShipAttackEvent`

```kotlin
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import org.trp.shincolle.api.event.ShipAttackEvent
import org.trp.shincolle.Shincolle

@EventBusSubscriber(modid = Shincolle.MODID)
object MyAttackListener {

    @JvmStatic
    @SubscribeEvent
    fun onShipAttackPre(event: ShipAttackEvent.Pre) {
        // 仅对重攻击生效：伤害 +20%
        if (event.attackType == ShipAttackEvent.AttackType.HEAVY) {
            event.damage *= 1.2f
        }

        // 阻止特定舰娘的攻击
        if (event.ship.name.string == "MyCustomShip") {
            event.isCanceled = true
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onShipAttackPost(event: ShipAttackEvent.Post) {
        // 攻击成功后给目标附加燃烧效果
        val target = event.target ?: return
        if (event.attackType == ShipAttackEvent.AttackType.LIGHT && !event.ship.level().isClientSide) {
            target.setSecondsOnFire(3)
        }
    }
}
```

## API 参考

### `IShipAttackEffect`

位于 `org.trp.shincolle.api.entity.IShipAttackEffect`。

| 方法 | 说明 |
|------|------|
| `onLightAttackSound(ship, target)` | 轻攻击自定义声音。返回 `SoundEvent` 替换默认声音，返回 `null` 使用默认。 |
| `onHeavyAttackSound(ship, target)` | 重攻击自定义声音。同上。 |
| `onLightAttackParticles(ship, target)` | 轻攻击自定义粒子。返回 `true` 跳过默认粒子，`false` 继续播放默认粒子。 |
| `onHeavyAttackParticles(ship, target)` | 重攻击自定义粒子。同上。 |

### `ShipAttackEvent`

位于 `org.trp.shincolle.api.event.ShipAttackEvent`。

#### `ShipAttackEvent.Pre`

在**弹药已消耗、伤害即将打出前**触发。

| 字段 | 类型 | 说明 |
|------|------|------|
| `ship` | `EntityShipBase` | 发起攻击的舰娘 |
| `target` | `LivingEntity?` | 攻击目标 |
| `attackType` | `AttackType` | `LIGHT` / `HEAVY` / `AIRCRAFT` |
| `damage` | `Float` | **可修改**。修改后的值将用于实际伤害计算 |
| `isCanceled` | `Boolean` | **可取消**。设为 `true` 则中断攻击（弹药已消耗，但不出伤害/不生成导弹/不放飞机） |

#### `ShipAttackEvent.Post`

在**攻击动作完成后**触发（轻攻击已 hurt、重攻击导弹已加入世界、舰载机已生成）。

| 字段 | 类型 | 说明 |
|------|------|------|
| `ship` | `EntityShipBase` | 发起攻击的舰娘 |
| `target` | `LivingEntity?` | 攻击目标 |
| `attackType` | `AttackType` | `LIGHT` / `HEAVY` / `AIRCRAFT` |
| `damage` | `Float` | 最终伤害值（Post 中只读，修改无效） |

**注意**：`Post` 事件**不可取消**。若需要在攻击前拦截，请使用 `Pre`。

## 常见场景示例

### 场景 1：特定舰娘攻击时播放自定义语音

```kotlin
class MyVoiceShip(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level), IShipAttackEffect {

    override fun onLightAttackSound(ship: EntityShipBase, target: LivingEntity?): SoundEvent? {
        return if (this.random.nextFloat() < 0.25f) {
            ModSounds.SHIP_HIT.get() // 或使用自定义 DeferredRegister<SoundEvent>
        } else null
    }
}
```

### 场景 2：通过事件给所有重攻击附加凋零效果

```kotlin
@EventBusSubscriber(modid = Shincolle.MODID)
object WitherMissileAddon {

    @JvmStatic
    @SubscribeEvent
    fun onHeavyAttackPost(event: ShipAttackEvent.Post) {
        if (event.attackType != ShipAttackEvent.AttackType.HEAVY) return
        val target = event.target ?: return
        if (target.level().isClientSide) return

        target.addEffect(MobEffectInstance(MobEffects.WITHER, 100, 0))
    }
}
```

### 场景 3：通过事件实现攻击伤害加成（装备套装效果）

```kotlin
@EventBusSubscriber(modid = Shincolle.MODID)
object DamageBuffAddon {

    @JvmStatic
    @SubscribeEvent
    fun onLightAttackPre(event: ShipAttackEvent.Pre) {
        val ship = event.ship
        val inv = ship.inventory ?: return
        var buffCount = 0
        for (i in 0..<inv.slots) {
            val stack = inv.getStackInSlot(i)
            if (stack.item == MyAddonItems.DAMAGE_BUFF.get()) {
                buffCount++
            }
        }
        if (buffCount >= 2) {
            event.damage *= (1.0f + buffCount * 0.1f)
        }
    }
}
```

## 注意事项

1. **事件触发时机**：`Pre` 事件在**弹药已消耗后**触发。如果第三方取消攻击，弹药不会退回。这是有意设计，防止恶意 Addon 无限刷弹药。
2. **伤害修改范围**：`Pre` 中的 `damage` 修改对轻攻击直接生效（`target.hurt`），对重攻击会同步修改导弹的 `damage` 字段，对舰载机仅作为参考值（实际伤害由舰载机 AI 计算）。
3. **客户端同步**：`IShipAttackEffect` 的声音与粒子回调在**服务端**执行。若需要客户端粒子，请使用服务端的发包 API（如 `ServerLevel.sendParticles`）。
4. **异常安全**：`ShipAttackEvent` 的订阅回调若抛出异常，可能导致攻击流程中断并打印堆栈。建议保持事件处理的健壮性。
5. **继承关系**：第三方舰娘实体**必须**继承 `EntityShipBase`（或其子类）才能被 ShinColle 系统正确识别。仅实现 `IShipAttackEffect` 而不继承 `EntityShipBase` 无效。

## 版本历史

| 版本 | 变更 |
|------|------|
| 0.0.1 | 初始引入 `IShipAttackEffect`、`ShipAttackEvent.Pre`、`ShipAttackEvent.Post`。 |
