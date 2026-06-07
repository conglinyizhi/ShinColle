# 实体差异清单

> 对照旧版 ShinColle 1.12.2（Forge）与新版 NeoForge 1.21.1 实体行为差异。
> 检查日期：2026-06-07

---

## 实体覆盖度

| 旧版实体 | 新版对应 | 状态 | 备注 |
|---|---|---|---|
| `BasicEntityShip` | `EntityShipBase` | ✅ | 架构重构为 Brain AI + 多模块拆分 |
| `BasicEntityShipCV` | `EntityShipBase` + `supportsAircraftCombat()` | ✅ | CV 基类取消，功能并入主基类 |
| `BasicEntityShipHostile` | `EntityShipBase` (`isHostileShipMob`) | ✅ |  hostile 行为改为同一实体的状态标记 |
| `BasicEntityMount` | `EntityMountBase` | ✅ | 重构为 Kotlin，保留核心骑乘/攻击逻辑 |
| `BasicEntitySummon` | `EntitySummonBase` | ✅ | 重构为 Kotlin，Brain AI 驱动 |
| `BasicEntityAirplane` | `EntityAircraftBase` | ✅ | 重构为 Kotlin，Brain AI 驱动 |
| `BasicEntityShipSmall` | — | ❌ | 已移除，新版所有舰娘直接继承 `EntityShipBase` |
| `EntityBattleshipYMT` | `EntityBattleshipYamato` | ✅ | 名称规范化 |
| `EntityBattleshipNGT` | `EntityBattleshipNagato` | ✅ | 名称规范化 |
| `EntityCarrierAkagi` | `EntityCarrierAkagi` | ✅ | 存在 |
| `EntityCarrierKaga` | `EntityCarrierKaga` | ✅ | 存在 |
| `EntityCarrierWo` | `EntityCarrierWo` | ✅ | 存在 |
| `EntityCarrierWD` | `EntityCarrierWDemon` | ✅ | 名称规范化 |
| `EntityDestroyerAkatsuki` | `EntityDestroyerAkatsuki` | ✅ | 存在 |
| `EntityDestroyerHibiki` | `EntityDestroyerHibiki` | ✅ | 存在 |
| `EntityDestroyerIkazuchi` | `EntityDestroyerIkazuchi` | ✅ | 存在 |
| `EntityDestroyerInazuma` | `EntityDestroyerInazuma` | ✅ | 存在 |
| `EntityDestroyerShimakaze` | `EntityDestroyerShimakaze` | ✅ | 存在 |
| `EntitySubmHime` | `EntitySubmHime` | ✅ | 存在 |
| `EntitySubmKa` | `EntitySubmKa` | ✅ | 存在 |
| `EntitySubmSo` | `EntitySubmSo` | ✅ | 存在 |
| `EntitySubmYo` | `EntitySubmYo` | ✅ | 存在 |
| `EntitySubmU511` | `EntitySubmU511` | ✅ | 存在 |
| `EntitySubmRo500` | `EntitySubmRo500` | ✅ | 存在 |
| `EntitySSNH` | `EntitySSNH` | ✅ | 存在 |
| `EntityBattleshipYMTMob` | — | ❌ | 旧版 hostile Mob 实体，新版由 `isHostileShipMob` 统一处理 |
| `EntityBB*Mob` 系列 | — | ❌ | 同上 |
| `EntityCarrierAkagiMob` | — | ❌ | 同上 |
| `EntityCarrierKagaMob` | — | ❌ | 同上 |
| `EntityDestroyer*Mob` 系列 | — | ❌ | 同上 |
| `EntityCANe` / `EntityCARi` | — | ❌ | 新版未实现 |
| `EntityAirplaneTakoyaki` | — | ❌ | 旧版重型舰载机，新版由 `EntityAirplaneT` 替代 |
| `EntityFloatingFort` | — | ❌ | 未迁移 |
| `EntityProjectileStatic` | — | ❌ | 未迁移 |
| `EntityAbyssMissile` | `EntityAbyssMissile` | ✅ | 重构为 Kotlin |
| `EntityProjectileBeam` | `EntityProjectileBeam` | ✅ | 重构为 Kotlin |
| `EntityRensouhou` / `EntityRensouhouS` | `EntityRensouhou` / `EntityRensouhouS` | ✅ | 存在 |
| `EntityRensouhouMob` | — | ❌ | 旧版 hostile 召唤物，新版未单独实现 |

---

## 基类行为差异

| 检查项 | 旧版 | 新版 | 状态 | 备注 |
|---|---|---|---|---|
| AI 系统 | `Task AI` (`EntityAIShip*`) | `Brain AI` (`EntityShipBrainAi` 等) | ✅ | 全新架构，行为树驱动 |
| 状态机 | `ShipStateHandler` (`getStateMinor`/`setStateMinor`) | `EntityShipLegacyState` + `SynchedEntityData` | ✅ | 保留兼容层 |
| 属性计算 | `calcShipAttributesAddRaw` / `calcShipAttributesAddEquip` / `calcShipAttributesAddEffect` | `LegacyShipStats.recalculate()` | ⚠️ | 新版集中式计算，**缺少 per-ship 钩子** |
| 攻击方式 | `attackEntityWithAmmo` / `attackEntityWithHeavyAmmo` / `attackEntityWithRangedAttack` | `performLightAttack` / `performHeavyAttack` | ✅ | 语义对应，由 `EntityShipBaseCombat` 执行 |
| 舰载机发射 | `BasicEntityShipCV.getAttackAirplane()` | `EntityShipBase.getAttackAircraftType()` | ✅ | 接口语义一致 |
| 舰载机恢复 | `BasicEntityShipCV.onLivingUpdate()` 手动计时 | `EntityShipBaseCombat.tickAircraftRecovery()` | ✅ | 逻辑已迁移 |
| 召唤物生命周期 | `BasicEntitySummon.onUpdate()` | `EntitySummonBase.updateServerLogic()` | ✅ | 逻辑已迁移 |
| 骑乘系统 | `getRidingEntity()` / `startRiding()` | `getVehicle()` / `startRiding()` | ✅ | 1.21 API 变更 |
| 隐身系统 | `IShipInvisible` (partial invisibility + dodge) | 仅 `MobEffects.INVISIBILITY` | ❌ | 旧版提供距离判定 dodge 加成，新版仅全隐身 |
| 粒子/声音 | `applySoundAtAttacker()` / `applyParticleAtAttacker()` | `spawnLightAttackMuzzleParticles()` 等 | ⚠️ | **缺少 per-ship 攻击声音钩子** |
| 弹药系统 | `decrAmmoNum()` / `MissileData` | `consumeLightAmmo()` / `consumeHeavyAmmo()` | ✅ | 新版支持 `IShipConsumable` API 扩展 |
| 伤害计算 | `CombatHelper.applyDamageReduceByDEF()` + dodge | `LegacyShipStats.getDefenseReducedDamage()` (仅 armor) | ❌ | **新版无 dodge 系统** |
|  chunk loading | `ForgeChunkManager.Ticket` | `forcedCompassChunks` + `ShipCompassBlock` | ✅ | 机制重构 |

---

## 具体舰娘差异

### 雷 / 电（第六驱逐队，含雷電合体）

| 舰娘 | 检查项 | 旧版 | 新版 | 状态 | 备注 |
|---|---|---|---|---|---|
| 雷 (Inazuma) | 雷電合体 | `tryRaidenGattai()` 无时限 | 新增 `raidenGattaiExpireTick` / `raidenGattaiCooldownUntilTick` | ⚠️ | 新版增加 45s 持续 + 20s CD |
| 雷 (Inazuma) | 合体后跟随 | 无主动跟随逻辑 | 新增 `raidenMovement` + `raidenRecovery` 跟随/传送 | ⚠️ | 行为增强 |
| 雷 (Inazuma) | 解除合体后位置 | 直接 `dismountRidingEntity()` | 新增 `placeIkazuchiAfterRaidenDismount()` | ✅ | 放置位置优化 |
| 电 (Ikazuchi) | `mobInteract` 转发 | 无 | 合体状态下转发到载体 | ✅ | UX 优化 |
| 雷/电 | 六驱合体 (Akatsuki) | `tryGattai()` 基于 `FormatType == 1` | `tryGattai()` 基于 `formationTeam` | ⚠️ | 判定条件变更 |
| 曉 (Akatsuki) | 合体时限 | 无时限 | 新增 `akatsukiGattaiExpireTick` / `akatsukiGattaiCooldownUntilTick` | ⚠️ | 新版增加 60s 持续 + 120s CD |
| 響 (Hibiki) | 闪避加成 | `calcShipAttributesAddRaw()` +30% dodge | **无** | ❌ | 新版无 dodge 系统，无法挂钩 |

### 岛风（含连装炮召唤）

| 舰娘 | 检查项 | 旧版 | 新版 | 状态 | 备注 |
|---|---|---|---|---|---|
| Shimakaze | 连装炮召唤 | `attackEntityWithAmmo()` 召唤 `EntityRensouhou`/`EntityRensouhouS` | `performLightAttack()` 中调用 `attackEntityWithAmmo()` | ✅ | 逻辑一致 |
| Shimakaze | 鱼雷属性修正 | `calcShipAttributesAddEquip()` `md.vel0 += 0.2F` | **无** | ❌ | 新版 `LegacyShipStats` 无 per-ship 导弹修正钩子 |
| Shimakaze | 重型攻击 | `attackEntityWithHeavyAmmo()` 5 连装导弹 | `performHeavyAttack()` 5 连装 `EntityAbyssMissile` | ✅ | 逻辑一致 |

### 大和（含光束攻击）

| 舰娘 | 检查项 | 旧版 | 新版 | 状态 | 备注 |
|---|---|---|---|---|---|
| Yamato | 光束攻击 | `attackEntityWithHeavyAmmo()` 二段蓄力 (`Phase`) | `performHeavyAttack()` 二段蓄力 (`EMOTION_ATTACK_PHASE`) | ✅ | 逻辑一致，蓄力粒子已迁移 |
| Yamato | 蓄力粒子 | `PARTICLE_LIGHTNING` (byte 4) | `ModParticles.PARTICLE_LIGHTNING` | ✅ | 已迁移 |
| Yamato | 攻击效果 Map | `calcShipAttributesAddEffect()` `AttackEffectMap.put(4, ...)` | **无** | ❌ | 旧版 buff 系统未迁移 |
| Yamato | 食物饱和度 | `setFoodSaturationMax(30)` | **无** | ❌ | 新版无此系统 |
| Yamato | 已婚抗性 Buff | 给附近同owner舰娘 `RESISTANCE` + `FIRE_RESISTANCE` | 已实现 | ✅ | 逻辑一致 |

### 赤城 / 加贺（含舰载机）

| 舰娘 | 检查项 | 旧版 | 新版 | 状态 | 备注 |
|---|---|---|---|---|---|
| Akagi | 舰载机数量加成 | `calcShipAttributesAddRaw()` `maxAircraftLight += level * 0.28F` | `aircraftLightLevelBonus = 0.28f` | ✅ | 由 `EntityShipBaseCombat.maxAircraftLight` 计算 |
| Kaga | 舰载机数量加成 | `calcShipAttributesAddRaw()` `maxAircraftLight += level * 0.4F` | `aircraftLightLevelBonus = 0.4f` | ✅ | 同上 |
| Akagi | 舰载机发射声音 | `applySoundAtAttacker()` 播放 `ARROW_SHOOT` | **无** | ❌ | **已修复**：新增 `onAircraftLaunched` 钩子 |
| Kaga | 舰载机发射声音 | `applySoundAtAttacker()` 播放 `ARROW_SHOOT` | **无** | ❌ | **已修复**：同上 |
| Akagi | 饥饿粒子 | `applyParticleEmotion(9)` 每 128 tick | `updateClientEffects()` 中 `applyParticleEmotion(9)` | ✅ | 已迁移 |
| Akagi/Kaga | 弓声音 + 语音 | `ARROW_SHOOT` + 30% 攻击语音 | 仅 `ARROW_SHOOT` | ⚠️ | 语音概率未恢复（可后续补充） |

### 潜水姬（含隐身）

| 舰娘 | 检查项 | 旧版 | 新版 | 状态 | 备注 |
|---|---|---|---|---|---|
| SubmHime | 隐身等级 | `IShipInvisible.getInvisibleLevel() = 0.3F` | 仅 `MobEffects.INVISIBILITY` | ❌ | 旧版为 partial invisibility + 距离 dodge，新版为全隐身 |
| SubmHime | 轻攻击 | `attackEntityWithAmmo()` 双发导弹 | **无**（默认 firepower 近战） | ⚠️ | 新版将双发鱼雷移至 `performHeavyAttack()`，攻击类型重新分类 |
| SubmHime | 重型攻击 | 默认重导弹 | `performHeavyAttack()` 双发鱼雷 | ⚠️ | 行为迁移至重攻击 |
| SubmHime | 浮动深度 | `getEntityFloatingDepth() = 1D` | **无** | ❌ | 新版潜艇通过 `isSubmarine` 跳过浮力计算 |
| SubmHime | 已婚隐身共享 | 给 owner 隐身 | 已实现 | ✅ | 逻辑一致 |
| 其他潜艇 (Yo/Ro500/U511) | 轻攻击导弹 | 旧版均有 `attackEntityWithAmmo()` 导弹 | 新版无 | ⚠️ | 新版统一将鱼雷放在重攻击 |

---

## 关键缺失系统

以下旧版系统在新版中未迁移，导致多个舰娘的共同行为差异：

1. **`IShipInvisible` 接口**：提供基于距离的 dodge 加成 + partial rendering invisibility。新版仅使用 vanilla `INVISIBILITY` 效果。
2. **`calcShipAttributesAddRaw` 钩子**：允许舰娘在属性计算时注入自定义 raw 属性（如 Hibiki +30% dodge）。新版 `LegacyShipStats.recalculate()` 为集中式，无 per-ship 钩子。
3. **`calcShipAttributesAddEquip` 钩子**：允许舰娘修改导弹参数（如 Shimakaze 导弹速度）。新版无 per-ship 导弹修正机制。
4. **`calcShipAttributesAddEffect` 钩子**：允许舰娘注入攻击效果（如 Yamato `AttackEffectMap`）。新版无 `AttackEffectMap` 系统。
5. **`applySoundAtAttacker` / `applyParticleAtAttacker` 钩子**：旧版 per-ship 攻击声音/粒子。新版仅保留基类通用粒子，缺少 per-ship 声音钩子。
6. **`setFoodSaturationMax`**：旧版舰娘特定饱食度上限。新版无此系统。

---

## 修复记录

### 修复 1：恢复赤城/加贺舰载机发射声音
- **问题**：新版 `EntityShipBaseCombat.spawnAircraft()` 未播放任何声音，旧版 Akagi/Kaga 在发射舰载机时播放弓弦声 (`ARROW_SHOOT`)。
- **修复**：
  - 在 `EntityShipBase` 新增 `open fun onAircraftLaunched(lightAircraft: Boolean)` 钩子。
  - 在 `EntityShipBaseCombat.spawnAircraft()` 中调用该钩子。
  - 在 `EntityCarrierAkagi` 与 `EntityCarrierKaga` 中覆盖该方法，恢复 `SoundEvents.ARROW_SHOOT` 播放。
