# 舰娘属性系统设计报告

> 分支：`design-attr-system`  
> 范围：Dodge 系统、Per-Ship 属性钩子、`IShipInvisible` 距离 dodge  
> 决策依据：对照旧版 1.12.2 缺失系统，结合 NeoForge 1.21.1 Attribute API 重新设计

---

## 1. NeoForge 1.21.1 Attribute 系统调研

### 1.1 注册自定义 Attribute

NeoForge 1.21.1 沿用 `DeferredRegister` 模式注册自定义 Attribute：

```kotlin
object ModAttributes {
    val ATTRIBUTES: DeferredRegister<Attribute> =
        DeferredRegister.create(Registries.ATTRIBUTE, Shincolle.MODID)

    @JvmField
    val DODGE_CHANCE: DeferredHolder<Attribute, Attribute> = ATTRIBUTES.register(
        "dodge_chance",
        Supplier {
            RangedAttribute(
                "attribute.name.shincolle.dodge_chance",
                0.0,           // 默认值
                0.0,           // 最小值
                1.0            // 最大值
            ).setSyncable(true)
        }
    )
}
```

注册后需在模组主类的构造器中绑定事件总线：

```kotlin
ModAttributes.ATTRIBUTES.register(modEventBus)
```

**与旧版 Forge 的差异：**
- 1.21.1 使用 `Registries.ATTRIBUTE`（旧版为 `ForgeRegistries.ATTRIBUTES`）。
- `AttributeModifier.Operation` 枚举已重命名：
  - `ADDITION` → `ADD_VALUE`
  - `MULTIPLY_BASE` → `ADD_MULTIPLIED_BASE`
  - `MULTIPLY_TOTAL` → `ADD_MULTIPLIED_TOTAL`
- `RangedAttribute` 构造参数顺序不变，但需调用 `.setSyncable(true)` 确保客户端同步。

### 1.2 AttributeModifier 的使用方式

现有代码中 `PointerItem` 已使用 `ItemAttributeModifiers` 为物品附加 AttributeModifier：

```kotlin
ItemAttributeModifiers.builder()
    .add(
        Attributes.ENTITY_INTERACTION_RANGE,
        AttributeModifier(
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "pointer_reach"),
            100.0,
            AttributeModifier.Operation.ADD_VALUE
        ),
        EquipmentSlotGroup.MAINHAND
    )
    .build()
```

对 LivingEntity 动态增删 Modifier 的 API：

```kotlin
// 添加
val instance = entity.getAttribute(ModAttributes.DODGE_CHANCE.get())
instance?.addTransientModifier(
    AttributeModifier(
        ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "hibiki_innate"),
        0.30,
        AttributeModifier.Operation.ADD_VALUE
    )
)

// 移除（通过 UUID 或 ResourceLocation）
instance?.removeModifier(location)
```

**注意：** `addTransientModifier` 不会持久化到 NBT，适合运行时动态计算；`addPermanentModifier` 会被序列化。

### 1.3 现有代码中的 Attribute 使用

- **EntityAttributeCreationEvent**（`ModEventBusEvents.registerAttributes`）：为所有舰娘注册基础 Attribute，目前仅调用 `EntityShipBaseSimple.createAttributes()`，包含 `MAX_HEALTH`、`MOVEMENT_SPEED`、`ATTACK_DAMAGE`、`FOLLOW_RANGE`、`STEP_HEIGHT`。
- **recalculateLegacyShipStats**（`EntityShipBase.kt:3334`）：在每次装备/等级/阵型/士气变更后，将 `LegacyShipStats` 计算结果写回 vanilla Attribute：
  - `MAX_HEALTH` ← `legacyShipStats.maxHealth`
  - `ATTACK_DAMAGE` ← `legacyShipStats.firepower * LEGACY_MELEE_DAMAGE_FACTOR`
  - `MOVEMENT_SPEED` ← `legacyShipStats.moveSpeed * Config.cruiseSpeedFactor`
  - `FOLLOW_RANGE` ← `legacyShipStats.attackRange`
- **未发现**自定义 Attribute 的注册，所有 dodge、armor、reloadSpeed 等仍走 `LegacyShipStats` 的 float 数组。

---

## 2. Dodge Attribute 设计方案

### 2.1 设计目标

将旧版 `LegacyShipStats` 索引 15 的 dodge 值迁移到 NeoForge Attribute 系统，使：
- Dodge 成为 vanilla 兼容的实体属性；
- 第三方可通过标准 `AttributeModifier` 接口注入 dodge 加成；
- 与 `defense`（armor）解耦：armor 做减伤，dodge 做闪避判定。

### 2.2 Attribute 定义

| 项目 | 值 |
|---|---|
| ID | `shincolle:dodge_chance` |
| 类 | `RangedAttribute` |
| 默认值 | `0.0` |
| 最小值 | `0.0` |
| 最大值 | `1.0`（硬上限 100% 闪避） |
| 是否同步 | 是（`setSyncable(true)`） |
| 中文名 | `attribute.name.shincolle.dodge_chance` → 语言文件 key |

### 2.3 基础注册

在 `EntityShipBaseSimple.createAttributes()` 中注册默认值：

```kotlin
companion object {
    fun createAttributes(): AttributeSupplier.Builder {
        return createLivingAttributes()
            .add(Attributes.MAX_HEALTH, 160.0)
            .add(Attributes.MOVEMENT_SPEED, 0.25)
            .add(Attributes.ATTACK_DAMAGE, 8.0)
            .add(Attributes.FOLLOW_RANGE, 36.0)
            .add(Attributes.STEP_HEIGHT, 1.0)
            .add(ModAttributes.DODGE_CHANCE.get(), 0.0)
    }
}
```

对于需要自定义基础 dodge 的舰娘（如潜艇），可在子类 `createAttributes()` 中覆盖：

```kotlin
// EntitySubmSo.kt
companion object {
    fun createAttributes(): AttributeSupplier.Builder {
        return createLivingAttributes()
            .add(Attributes.MAX_HEALTH, 160.0)
            .add(Attributes.MOVEMENT_SPEED, 0.25)
            .add(Attributes.ATTACK_DAMAGE, 8.0)
            .add(Attributes.FOLLOW_RANGE, 36.0)
            .add(Attributes.STEP_HEIGHT, 1.0)
            .add(ModAttributes.DODGE_CHANCE.get(), 0.25) // 潜艇基础 dodge 25%
    }
}
```

### 2.4 伤害计算时读取 Dodge

重写 `EntityShipBase.hurt()` 中的 `tryLegacyDodge()`：

```kotlin
private fun tryLegacyDodge(source: DamageSource): Boolean {
    val attacker = source.entity
    if (attacker == null || attacker === this) {
        return false
    }
    if (source.`is`(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
        return false
    }

    val dodge = Mth.clamp(
        this.getAttributeValue(ModAttributes.DODGE_CHANCE.get()),
        0.0, 1.0
    )
    if (dodge <= 0.0 || this.random.nextFloat() > dodge) {
        return false
    }

    this.spawnCombatTextParticle(COMBAT_TEXT_DODGE)
    return true
}
```

**与现有 `defense` 属性的关系：**
- `defense`（armor）在 `getDefenseReducedDamage()` 中做乘性减伤；
- `dodge` 在 `hurt()` 入口处做概率闪避，成功则直接返回 `false`（0 伤害、无后续减伤）；
- 判定顺序：dodge → armor减伤 → 修理女神/消耗品保命。

### 2.5 从 LegacyShipStats 同步 Dodge

在 `recalculateLegacyShipStats()` 中增加一行同步：

```kotlin
this.getAttribute(ModAttributes.DODGE_CHANCE.get())
    ?.setBaseValue(this.legacyShipStats.getRawAttr(15).toDouble())
```

这样：
- 装备、阵型、士气带来的 dodge 加成仍通过 `LegacyShipStats` 计算（保持旧版数据流兼容）；
- 计算结果写入 Attribute **base value**；
- 第三方 `AttributeModifier` 以 `ADD_VALUE` / `ADD_MULTIPLIED_BASE` / `ADD_MULTIPLIED_TOTAL` 方式叠加在 base 之上。

### 2.6 为特定舰娘添加 Innate Modifier（示例：Hibiki +30%）

**方案 A：在实体构造器中添加 Permanent Modifier**

```kotlin
// EntityDestroyerHibiki.kt
init {
    // ... 现有 init ...
    val dodgeAttr = this.getAttribute(ModAttributes.DODGE_CHANCE.get())
    dodgeAttr?.addPermanentModifier(
        AttributeModifier(
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "hibiki_innate_dodge"),
            0.30,
            AttributeModifier.Operation.ADD_VALUE
        )
    )
}
```

**方案 B：在 `recalculateLegacyShipStats()` 后通过 `IShipAttributeProvider` 统一注入（见第 3 章）**

推荐先实施方案 A（简单直接），待 `IShipAttributeProvider` 成熟后迁移到方案 B。

### 2.7 语言文件与 Tooltip

新增语言条目：

```json
{
  "attribute.name.shincolle.dodge_chance": "Dodge Chance",
  "attribute.name.shincolle.dodge_chance": "闪避率",
  "attribute.name.shincolle.dodge_chance": "回避率"
}
```

---

## 3. `IShipAttributeProvider` 接口设计方案

> **用户决策：B+D —— 改用 AttributeModifier + 设计新的 `IShipAttributeProvider` 接口，等待下一轮报告重新考虑方向。**  
> 本章提供接口草案与集成点，供下一轮架构决策使用。

### 3.1 背景

旧版 1.12.2 提供 `calcShipAttributesAddRaw/Equip/Effect` 三个钩子，允许舰娘子类直接修改 `float[21]` 属性数组。新版 1.21.1 中：
- `LegacyShipStats` 仍是核心属性容器；
- 但部分属性（HP、攻击、移速、跟随距离）已迁移到 vanilla Attribute；
- 未来可能继续迁移 dodge、armor 等属性。

因此，新接口不应直接暴露 `float[]`，而应以 **AttributeModifier + 扩展点** 的方式提供。

### 3.2 接口草案

```kotlin
package org.trp.shincolle.api.entity

import net.minecraft.world.entity.ai.attributes.AttributeModifier
import org.trp.shincolle.entity.base.EntityShipBase

/**
 * 舰娘自定义属性提供者接口。
 *
 * 实现此接口的 [EntityShipBase] 子类可在属性重算周期中注入额外的
 * [AttributeModifier]，或通过回调修改 [LegacyShipStats] 的计算结果。
 *
 * **注意：** 此接口属于 `org.trp.shincolle.api` 包下的公共 API，
 * 变更时需同步更新 `docs/THIRD_PARTY_EQUIP.md` 或新增实体扩展文档。
 */
interface IShipAttributeProvider {

    /**
     * 在 [LegacyShipStats.recalculate] 调用**之前**执行。
     *
     * 用于修改 raw 属性数组的基准值（相当于旧版 `calcShipAttributesAddRaw`）。
     *
     * @param ship 当前舰娘实体
     * @param rawAttrs 长度为 [org.trp.shincolle.item.LegacyEquipStats.ATTR_COUNT] 的 mutable 数组，
     *                 索引含义与 [IShipEquip.getMainAttributes] 一致。
     */
    fun modifyRawAttributes(ship: EntityShipBase, rawAttrs: FloatArray) {}

    /**
     * 在 [LegacyShipStats.recalculate] 调用**之后**、同步到 vanilla Attribute **之前**执行。
     *
     * 用于修改 buffed 属性数组（相当于旧版 `calcShipAttributesAddEquip/Effect`）。
     *
     * @param ship 当前舰娘实体
     * @param buffedAttrs 计算完成后的 mutable 数组。
     */
    fun modifyBuffedAttributes(ship: EntityShipBase, buffedAttrs: FloatArray) {}

    /**
     * 在 vanilla Attribute 同步阶段执行。
     *
     * 实现方可通过此方法动态增删 [AttributeModifier]，
     * 推荐只使用 [AttributeModifier.Operation.ADD_VALUE] 或
     * [AttributeModifier.Operation.ADD_MULTIPLIED_BASE]。
     *
     * @param ship 当前舰娘实体
     * @param context 属性修改上下文，封装常用 Attribute 的便捷操作。
     */
    fun applyAttributeModifiers(ship: EntityShipBase, context: AttributeModifierContext) {}
}

/**
 * 封装 AttributeModifier 增删的上下文对象，避免实现方直接操作 `getAttribute()` 的 boilerplate。
 */
class AttributeModifierContext(private val ship: EntityShipBase) {

    fun addModifier(
        attribute: Attribute,
        id: ResourceLocation,
        amount: Double,
        operation: AttributeModifier.Operation
    ) {
        val inst = ship.getAttribute(attribute) ?: return
        // 先移除同 ID 的旧 modifier，防止重复叠加
        inst.removeModifier(id)
        inst.addTransientModifier(AttributeModifier(id, amount, operation))
    }

    fun removeModifier(attribute: Attribute, id: ResourceLocation) {
        ship.getAttribute(attribute)?.removeModifier(id)
    }

    fun getValue(attribute: Attribute): Double {
        return ship.getAttributeValue(attribute)
    }
}
```

### 3.3 接口放置位置

```
src/main/java/org/trp/shincolle/api/entity/
├── IShipAttributeProvider.kt
└── AttributeModifierContext.kt
```

### 3.4 舰娘如何实现该接口

以 `EntityDestroyerHibiki` 为例：

```kotlin
class EntityDestroyerHibiki(type: EntityType<out TamableAnimal>, level: Level) :
    EntityShipBase(type, level), IShipAttributeProvider, IShipRiderType {

    override fun applyAttributeModifiers(ship: EntityShipBase, context: AttributeModifierContext) {
        // Hibiki 固有 dodge +30%
        context.addModifier(
            ModAttributes.DODGE_CHANCE.get(),
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "hibiki_innate_dodge"),
            0.30,
            AttributeModifier.Operation.ADD_VALUE
        )
    }
}
```

以 `EntitySubmSo`（潜艇基础 dodge + 隐身距离 dodge）为例：

```kotlin
class EntitySubmSo(type: EntityType<out TamableAnimal>, level: Level) :
    EntityShipBase(type, level), IShipAttributeProvider {

    override fun modifyRawAttributes(ship: EntityShipBase, rawAttrs: FloatArray) {
        // 潜艇基础 dodge 25%（也可通过 createAttributes 设置 base value）
        rawAttrs[15] += 0.25f
    }

    override fun applyAttributeModifiers(ship: EntityShipBase, context: AttributeModifierContext) {
        // 隐身距离 dodge 逻辑将在 tick 中动态管理，见第 4 章
    }
}
```

### 3.5 调用时机

在 `EntityShipBase.recalculateLegacyShipStats()` 中插入三个回调：

```kotlin
fun recalculateLegacyShipStats() {
    // 1. 收集装备、阵型、士气
    // ... 现有代码 ...

    // 2. 调用 LegacyShipStats.recalculate
    this.legacyShipStats.recalculate(...)

    // 3. IShipAttributeProvider 回调（如果有）
    if (this is IShipAttributeProvider) {
        // 3a. 修改 raw（在 recalculate 后实际上 raw 已被复制到 buffed，
        //     所以更合理的做法是：在 recalculate 之前给 raw 打补丁）
        // 建议调整 LegacyShipStats.recalculate 支持 pre-raw hook，
        // 或在 EntityShipBase 层构造临时 raw 数组传入。
    }

    // 4. 同步到 vanilla Attribute
    if (!this.level().isClientSide) {
        this.getAttribute(Attributes.MAX_HEALTH)?.setBaseValue(...)
        this.getAttribute(Attributes.ATTACK_DAMAGE)?.setBaseValue(...)
        this.getAttribute(Attributes.MOVEMENT_SPEED)?.setBaseValue(...)
        this.getAttribute(Attributes.FOLLOW_RANGE)?.setBaseValue(...)
        this.getAttribute(ModAttributes.DODGE_CHANCE.get())
            ?.setBaseValue(this.legacyShipStats.getRawAttr(15).toDouble())
    }

    // 5. applyAttributeModifiers（在 base value 确定后叠加 modifier）
    if (this is IShipAttributeProvider) {
        this.applyAttributeModifiers(this, AttributeModifierContext(this))
    }
}
```

**调整后的推荐调用链：**

```
收集 equipBonuses
    ↓
[IShipAttributeProvider.modifyRawAttributes] → 修改 rawAttrs
    ↓
LegacyShipStats.recalculate(shipClass, level, rawAttrs, formationBuffs, moraleBuffs)
    ↓
[IShipAttributeProvider.modifyBuffedAttributes] → 修改 buffedAttrs
    ↓
同步 buffedAttrs → vanilla Attribute base values
    ↓
[IShipAttributeProvider.applyAttributeModifiers] → 注入 AttributeModifier
```

### 3.6 与 AttributeModifier 的整合方式

| 来源 | 作用对象 | 方式 |
|---|---|---|
| `LegacyShipStats` | Attribute **base value** | `setBaseValue()` |
| 装备/阵型/士气 | `LegacyShipStats` float[] | 现有 `collectEquipBonuses()` + `recalculate()` |
| 舰娘固有特性 | Attribute **modifier** | `IShipAttributeProvider.applyAttributeModifiers()` |
| 第三方模组 | Attribute **modifier** | 通过 NeoForge 标准 API 直接操作 `entity.getAttribute()` |

### 3.7 第三方模组如何使用

第三方 Addon 无需实现 `IShipAttributeProvider`，可直接监听舰娘属性重算事件（若后续提供），或在任意时机通过标准 API 修改：

```kotlin
// 第三方代码
val ship = ... // EntityShipBase
val dodgeInst = ship.getAttribute(ModAttributes.DODGE_CHANCE.get())
dodgeInst?.addTransientModifier(
    AttributeModifier(
        ResourceLocation.fromNamespaceAndPath("myaddon", "lucky_charm"),
        0.15,
        AttributeModifier.Operation.ADD_VALUE
    )
)
```

若第三方添加自己的舰娘实体，则建议实现 `IShipAttributeProvider` 以融入本模组的属性计算周期。

---

## 4. IShipInvisible 距离 Dodge 方案

### 4.1 旧版行为回顾

旧版 1.12.2 中：
- 部分舰娘（潜艇、特定驱逐）实现 `IShipInvisible` 接口；
- 提供 `partial invisibility`（部分隐身，敌人更难发现）；
- 提供 **距离 dodge**：距离敌人越远，dodge 加成越高。

### 4.2 新版简化方案

用户决策：**B or C —— 全隐身 + 距离 dodge（简化处理）**

放弃 `partial invisibility` 的复杂渲染/AI 逻辑，改用 `MobEffects.INVISIBILITY`（全隐身）+ 距离判定动态添加 dodge AttributeModifier。

### 4.3 方案细节

#### 4.3.1 隐身状态

保持现有潜艇实现：在 `tickAliveLogic()` 中周期性添加 `MobEffects.INVISIBILITY`。

```kotlin
// EntitySubmSo.kt（现有代码，保持不变）
if (this.isStateRingEffect) {
    val duration = 40 + this.level
    this.addEffect(MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false))
    // ...
}
```

#### 4.3.2 距离 Dodge 逻辑

新增 `EntityShipBase` 层面的 tick 逻辑（仅服务端）：

```kotlin
private val INVISIBILITY_DODGE_MODIFIER_ID =
    ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "invisibility_distance_dodge")

private fun tickInvisibilityDodge() {
    if (this.level().isClientSide) return

    val hasInvisibility = this.hasEffect(MobEffects.INVISIBILITY)
    val dodgeAttr = this.getAttribute(ModAttributes.DODGE_CHANCE.get()) ?: return

    if (!hasInvisibility) {
        dodgeAttr.removeModifier(INVISIBILITY_DODGE_MODIFIER_ID)
        return
    }

    val target = this.target
    if (target == null || !target.isAlive) {
        // 无战斗目标时保持最小加成（或移除）
        dodgeAttr.removeModifier(INVISIBILITY_DODGE_MODIFIER_ID)
        return
    }

    val distance = this.distanceTo(target)
    val dodgeBonus = when {
        distance < 2.0 -> {
            // 过近：移除 dodge 加成
            dodgeAttr.removeModifier(INVISIBILITY_DODGE_MODIFIER_ID)
            return
        }
        distance < 5.0 -> (distance - 2.0) / 3.0 * 0.15 // 2~5 格线性增长到 +15%
        distance < 10.0 -> 0.15 + (distance - 5.0) / 5.0 * 0.10 // 5~10 格再 +10%
        else -> 0.25 // 超过 10 格上限 +25%
    }

    val existing = dodgeAttr.getModifier(INVISIBILITY_DODGE_MODIFIER_ID)
    if (existing != null && existing.amount == dodgeBonus) {
        return // 避免每 tick 都重建 modifier
    }

    dodgeAttr.removeModifier(INVISIBILITY_DODGE_MODIFIER_ID)
    dodgeAttr.addTransientModifier(
        AttributeModifier(INVISIBILITY_DODGE_MODIFIER_ID, dodgeBonus, AttributeModifier.Operation.ADD_VALUE)
    )
}
```

#### 4.3.3 触发时机

在 `EntityShipBase.tickAliveLogic()`（或 `aiStep()`）中每 tick 调用 `tickInvisibilityDodge()`。由于 modifier 是 `Transient`，死亡/卸载人时自动清除。

#### 4.3.4 配置化

建议将距离阈值和 dodge 上限提取到 `Config`：

```kotlin
// Config.kt（新增）
val invisibilityDodgeNearThreshold: Double = 2.0
val invisibilityDodgeMidThreshold: Double = 5.0
val invisibilityDodgeFarThreshold: Double = 10.0
val invisibilityDodgeMaxBonus: Double = 0.25
```

### 4.4 与现有代码的集成点

| 集成点 | 改动 |
|---|---|
| `EntityShipBase.tickAliveLogic()` | 增加 `tickInvisibilityDodge()` 调用 |
| `EntityShipBase.hurt()` | dodge 判定改为读取 `getAttributeValue(ModAttributes.DODGE_CHANCE)` |
| `EntityShipBase.recalculateLegacyShipStats()` | 同步 `legacyShipStats` 的 dodge 到 Attribute base value |
| 潜艇实体（`EntitySubmSo` 等） | 无需改动，继续使用 `MobEffects.INVISIBILITY` |

---

## 5. 实现优先级建议

| 优先级 | 任务 | 预估工作量 | 依赖 |
|---|---|---|---|
| **P0** | 注册 `DODGE_CHANCE` Attribute，在 `hurt()` 中替换 `tryLegacyDodge()` | 小（1~2 文件） | 无 |
| **P0** | 在 `recalculateLegacyShipStats()` 中同步 dodge 到 Attribute base value | 极小（1 行） | P0 Attribute 注册 |
| **P1** | 为 Hibiki 等舰娘添加 innate `AttributeModifier` | 小（2~3 实体文件） | P0 |
| **P1** | 潜艇 `createAttributes()` 设置基础 dodge | 小（5~6 实体文件） | P0 |
| **P2** | `IShipAttributeProvider` 接口设计与 `recalculateLegacyShipStats()` 集成 | 中（新增 2 文件 + 修改 1 文件） | P0 |
| **P2** | 将 Hibiki/潜艇的 innate modifier 迁移到 `IShipAttributeProvider` | 小 | P2 接口完成 |
| **P3** | `IShipInvisible` 距离 dodge（隐身状态 + 距离判定） | 中（新增 tick 逻辑 + Config） | P0 |
| **P4** | 文档更新：`docs/THIRD_PARTY_EQUIP.md` 或新增实体扩展文档 | 小 | P2 |

---

## 6. 与现有代码的集成点汇总

### 6.1 新增文件

```
src/main/java/org/trp/shincolle/init/ModAttributes.kt
src/main/java/org/trp/shincolle/api/entity/IShipAttributeProvider.kt
src/main/java/org/trp/shincolle/api/entity/AttributeModifierContext.kt
```

### 6.2 修改文件

```
src/main/java/org/trp/shincolle/Shincolle.kt                    // 注册 ModAttributes 事件总线
src/main/java/org/trp/shincolle/entity/base/EntityShipBaseSimple.kt  // createAttributes() 增加 DODGE_CHANCE
src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt   // hurt(), recalculateLegacyShipStats(), tickAliveLogic()
src/main/java/org/trp/shincolle/event/ModEventBusEvents.kt      // EntityAttributeCreationEvent 中确认所有舰娘注册 DODGE_CHANCE
src/main/java/org/trp/shincolle/entity/EntityDestroyerHibiki.kt //  innate dodge modifier（或待 IShipAttributeProvider）
src/main/java/org/trp/shincolle/entity/EntitySubm*.kt           // 潜艇基础 dodge + 隐身距离 dodge
```

### 6.3 语言文件

```
src/main/resources/assets/shincolle/lang/en_us.json
src/main/resources/assets/shincolle/lang/zh_cn.json
src/main/resources/assets/shincolle/lang/zh_tw.json
src/main/resources/assets/shincolle/lang/ja_jp.json
```

新增 key：`attribute.name.shincolle.dodge_chance`

---

## 7. 风险评估

| 风险 | 等级 | 说明 | 缓解措施 |
|---|---|---|---|
| **Attribute 同步延迟** | 中 | `setSyncable(true)` 后客户端可见，但modifier 频繁变更可能导致网络包增加 | 距离 dodge 每 tick 只在数值变化时重建 modifier；使用 `Transient` 避免 NBT 序列化负担 |
| **与旧版存档兼容** | 低 | `LegacyShipStats` 仍保留 dodge 值，旧存档加载后会在 `recalculateLegacyShipStats()` 中同步到 Attribute | 保持 `LegacyShipStats` 计算链不变，Attribute 仅作为运行时镜像 |
| **第三方 API 稳定性** | 中 | `IShipAttributeProvider` 属于 `org.trp.shincolle.api` 包，按 AGENTS.md 约定，变更需同步递增 API 版本 | 在 `0.x.x` 阶段通过 MINOR 版本管理；接口设计尽量只增不减 |
| **dodge 上限溢出** | 低 | 多个来源叠加可能超过 100% | `tryLegacyDodge()` 中继续使用 `Mth.clamp(..., 0.0, 1.0)` |
| **性能** | 低 | `getAttributeValue()` 每 hurt 调用一次，有 attribute map 查找开销 | 现代 MC 的 Attribute 系统已高度优化；若出现瓶颈可缓存到 `LegacyShipStats` 的本地字段 |

---

## 8. 代码草案速览

### 8.1 ModAttributes.kt

```kotlin
package org.trp.shincolle.init

import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.RangedAttribute
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.trp.shincolle.Shincolle
import java.util.function.Supplier

object ModAttributes {
    val ATTRIBUTES: DeferredRegister<Attribute> =
        DeferredRegister.create(Registries.ATTRIBUTE, Shincolle.MODID)

    @JvmField
    val DODGE_CHANCE: DeferredHolder<Attribute, Attribute> = ATTRIBUTES.register(
        "dodge_chance",
        Supplier {
            RangedAttribute("attribute.name.shincolle.dodge_chance", 0.0, 0.0, 1.0)
                .setSyncable(true)
        }
    )
}
```

### 8.2 EntityShipBase.hurt() 片段

```kotlin
override fun hurt(source: DamageSource, amount: Float): Boolean {
    if (this.customHurtTime > 0 || source.`is`(DamageTypeTags.IS_FIRE) || source.`is`(DamageTypes.IN_WALL)) {
        return false
    }

    if (!this.level().isClientSide && tryLegacyDodge(source)) {
        return false
    }

    var reduced = amount
    if (!this.level().isClientSide && amount < 100000.0f) {
        reduced = this.legacyShipStats.getDefenseReducedDamage(amount, this.random)
    }
    // ... 后续逻辑不变 ...
}

private fun tryLegacyDodge(source: DamageSource): Boolean {
    val attacker = source.entity
    if (attacker == null || attacker === this) return false
    if (source.`is`(DamageTypeTags.BYPASSES_INVULNERABILITY)) return false

    val dodge = Mth.clamp(
        this.getAttributeValue(ModAttributes.DODGE_CHANCE.get()), 0.0, 1.0
    )
    if (dodge <= 0.0 || this.random.nextFloat() > dodge) return false

    this.spawnCombatTextParticle(COMBAT_TEXT_DODGE)
    return true
}
```

### 8.3 EntityShipBase.recalculateLegacyShipStats() 片段

```kotlin
fun recalculateLegacyShipStats() {
    // ... 现有装备/阵型/士气收集逻辑 ...

    this.legacyShipStats.recalculate(
        this.getStateMinor(STATE_MINOR_SHIP_CLASS),
        this.level,
        this.collectEquipBonuses(),
        formationBuffs,
        moraleBuffs
    )

    if (this.level() != null && !this.level().isClientSide) {
        this.getAttribute(Attributes.MAX_HEALTH)?.setBaseValue(this.legacyShipStats.maxHealth.toDouble())
        this.getAttribute(Attributes.ATTACK_DAMAGE)
            ?.setBaseValue((this.legacyShipStats.firepower * LEGACY_MELEE_DAMAGE_FACTOR).toDouble())
        this.getAttribute(Attributes.MOVEMENT_SPEED)
            ?.setBaseValue((this.legacyShipStats.moveSpeed * Config.cruiseSpeedFactor.toFloat()).toDouble())
        this.getAttribute(Attributes.FOLLOW_RANGE)
            ?.setBaseValue(max(24.0, this.legacyShipStats.attackRange.toDouble()))

        // 新增：同步 dodge 到 Attribute
        this.getAttribute(ModAttributes.DODGE_CHANCE.get())
            ?.setBaseValue(this.legacyShipStats.getRawAttr(15).toDouble())

        if (this.health > this.maxHealth) {
            this.health = this.maxHealth
        }
    }

    // 新增：IShipAttributeProvider 回调
    if (this is IShipAttributeProvider) {
        this.applyAttributeModifiers(this, AttributeModifierContext(this))
    }
}
```

---

## 9. 结论

1. **Dodge 系统**可直接通过 NeoForge `RangedAttribute` 实现，与 `LegacyShipStats` 保持双向兼容（`LegacyShipStats` 负责计算，Attribute 负责运行时暴露）。
2. **`IShipAttributeProvider`** 作为下一轮架构演进的方向，草案已就绪，但建议等 Dodge 系统落地并稳定后再实施，避免一次性改动面过大。
3. **IShipInvisible 距离 dodge** 采用简化方案：全隐身（`MobEffects.INVISIBILITY`）+ 距离判定动态 `AttributeModifier`，实现复杂度可控，且与现有潜艇逻辑无缝衔接。
