# ShinColle Data Definition Candidates

## 范围

本文聚焦三类仍以代码硬编码为主、但已经具备明显整理价值的内容：

- 舰娘模板
- 装备参数
- 声音覆盖规则

目标不是立即把它们全部改成 datapack，而是先识别：

- 哪些字段适合先抽成数据定义
- 哪些部分仍应保留在代码里
- 迁移时需要哪些回归保护

## 现状概览

### 舰娘模板

- `ModEntities` 负责注册全部舰娘实体 ID、尺寸和基础类型。
- 具体舰娘类在各自构造阶段写入 `STATE_MINOR_SHIP_CLASS`、阵营、稀有度、特殊装备位、模型偏移等模板字段。
- `equipOptions`、`shipSpawnEggItem`、舰载机类型、婚后增益、合体规则等也分散在各实体类中。

代表性入口：

- `src/main/java/org/trp/shincolle/init/ModEntities.kt`
- `src/main/java/org/trp/shincolle/entity/EntityDestroyerIkazuchi.kt`
- `src/main/java/org/trp/shincolle/entity/EntityCarrierKaga.kt`

### 装备参数

- `LegacyEquipStats` 以大表形式维护旧版装备主属性和杂项属性。
- 多数装备参数按 `equipId -> float[] / int[]` 读取，属于典型“数据长表塞进代码”的形态。
- 当前实现已经稳定，但扩展和审查成本较高，不利于后续校对旧版语义。

代表性入口：

- `src/main/java/org/trp/shincolle/item/LegacyEquipStats.kt`
- `src/main/java/org/trp/shincolle/item/LegacyEquipItem.kt`
- `src/main/java/org/trp/shincolle/crafting/ShipyardRecipes.kt`

### 声音覆盖规则

- 基础声音事件注册在 `ModSounds`，底层资源定义依赖 `assets/shincolle/sounds.json`。
- `Config.ShipCustomSoundType` 维护可覆盖的声音类型和命名约定。
- `ModSounds.getShipSound` 按 `shipClass` 和配置概率拼接 `ship-xxx-{classId}` 自定义语音。
- 个别舰娘仍有实体类内的专用覆盖逻辑，例如 `EntityCarrierKaga.tryGetAttackVoice()`。

代表性入口：

- `src/main/java/org/trp/shincolle/init/ModSounds.kt`
- `src/main/java/org/trp/shincolle/Config.kt`
- `src/main/resources/assets/shincolle/sounds.json`
- `src/main/java/org/trp/shincolle/entity/EntityCarrierKaga.kt`

## 优先候选

### 候选 A：舰娘静态模板字段表

适合先数据化的字段：

- 实体注册名与展示用 ship class 映射
- 阵营、稀有度、特殊装备位默认值
- 模型预览偏移 `modelPos`
- 默认 GUI 按钮启用状态
- 默认装备槽位清单 `equipOptions`
- 默认 spawn egg 关联关系

建议形态：

- 先做“代码加载 JSON 资源”的轻量表，而不是一步到位开放外部 datapack 覆盖。
- 由实体类保留行为逻辑，只把静态模板字段迁到集中定义。

原因：

- 这些字段在多个实体类里重复出现，结构相似，适合统一校对。
- 先收敛静态模板，可以降低后续继续拆分 `entity/` 目录时的回归风险。

暂不建议数据化的部分：

- 婚后增益、舰载机行为、合体逻辑、受击/寻路/技能行为
- 依赖 `Level`、实体搜索、效果施加、随机数或复杂状态机的运行时规则

### 候选 B：装备参数资源表

适合先数据化的字段：

- `LegacyEquipStats` 中按 `equipId` 索引的主属性数组
- 杂项属性数组
- 属性显示顺序所依赖的稳定字段定义

建议形态：

- 用结构化 JSON 取代匿名 `float[]` / `int[]`，字段名直接对应属性语义。
- 保留 `LegacyEquipStats` 作为加载与兼容门面，对外 API 先不动。

原因：

- 这是当前最典型的“数据在代码里”的区域，收益最高。
- 结构化后更容易做旧版对表、自动校验、生成 tooltip 与后续 datagen。

迁移前提：

- 需要先补一层字段命名规范，避免把匿名数组原样搬进 JSON 后继续难维护。
- 需要保留现有测试对关键 equipId 的数值守卫。

### 候选 C：按 ship class 的声音覆盖清单

适合先数据化的字段：

- 某个 `shipClass` 支持哪些 `ShipCustomSoundType`
- 自定义音频资源 ID
- 覆盖概率或权重
- 是否回退到通用默认音效

建议形态：

- 把“支持哪些覆盖、覆盖到哪个资源 ID”提取到资源表。
- `ModSounds.getShipSound` 继续负责运行时选择、随机与回退。

原因：

- 当前规则已经部分依赖资源命名约定，天然接近数据配置。
- 这一层抽离后，可以减少实体类里写死 `ship-hit-47` 之类拼接逻辑的分散实现。

暂不建议数据化的部分：

- 与攻击流程强绑定的播放时机
- 完全特例化的实体表现分支，除非先抽出统一钩子

## 分阶段建议

### 第一阶段：集中定义，不开放外部覆盖

- 新建内部资源表，先由模组自身打包使用。
- 代码端继续保留默认值与回退路径。
- 为舰娘模板、装备参数、声音覆盖各补 1 组结构化回归测试。

### 第二阶段：收敛加载器与校验

- 为模板/参数/声音表增加缺字段、重复 ID、非法 ship class 的启动期校验。
- 把当前散落在实体类中的静态字段逐步迁回集中定义。

### 第三阶段：评估是否开放 datapack 扩展

- 仅在格式稳定、回归测试充足后，再评估是否允许外部覆盖。
- 对高风险行为型字段继续坚持“代码 + 数据”分层，不把 AI / 战斗状态机直接外置。

## 不建议一次性推进的风险点

- `EntityShipBase` 及其子类存在大量运行时行为差异，若模板字段和行为字段一起外置，问题定位会明显变难。
- `LegacyEquipStats` 当前是匿名数组；如果不先命名字段，直接迁 JSON 只会把难读性从 Kotlin 挪到资源文件。
- 声音覆盖已经同时受 `sounds.json`、`Config.ShipCustomSoundType`、`shipClass` 命名约定影响，必须先明确唯一数据源。

## 推荐顺序

1. `LegacyEquipStats`
2. 舰娘静态模板字段
3. 声音覆盖清单

推荐理由：

- `LegacyEquipStats` 最像纯数据，改造面最集中。
- 舰娘模板字段次之，静态值可先抽离，行为逻辑继续留在实体类。
- 声音覆盖虽然适合数据化，但当前命名约定与特例逻辑并存，适合放在第三步清理。
