# ShinColle Datapack / Datagen Candidates

本文档用于记录 `1.12.2 -> 1.21.1` 迁移阶段里，哪些内容已经迁移为数据资源、哪些适合进一步 datapack / datagen 化、以及哪些逻辑当前仍应保留在代码中。

目标不是立即把所有内容改成 datagen，而是先建立一份稳定的候选清单，避免后续重构时把“适合数据化”和“必须保留代码语义”的内容混在一起。

## 已经数据化的内容

当前仓库已经存在、且适合作为后续 datagen 目标继续维护的数据目录：

- `src/main/resources/data/shincolle/recipe`
  - 常规物品与方块配方。
- `src/main/resources/data/shincolle/loot_table`
  - 方块掉落与注入型掉落表。
- `src/main/resources/data/shincolle/loot_modifiers`
  - 旧版宝箱掉落迁移后的 NeoForge loot modifier 配置。
- `src/main/resources/data/shincolle/neoforge/biome_modifier`
  - 矿物生成的 biome modifier。
- `src/main/resources/data/shincolle/worldgen/configured_feature`
  - worldgen configured feature。
- `src/main/resources/data/shincolle/worldgen/placed_feature`
  - worldgen placed feature。
- `src/main/resources/data/shincolle/tags/block`
  - 方块标签。
- `src/main/resources/data/shincolle/tags/item`
  - 物品标签。
- `src/main/resources/data/shincolle/tags/entity_type`
  - 实体类型标签。
- `src/main/resources/assets/shincolle/lang`
  - 四语言文本资源。
- `src/main/resources/assets/shincolle/models`
  - 方块/物品模型。
- `src/main/resources/assets/shincolle/blockstates`
  - blockstate 定义。
- `src/main/resources/assets/shincolle/particles`
  - 粒子资源定义。
- `src/main/resources/data/shincolle/patchouli_books`
  - Patchouli 手册元数据。
- `src/main/resources/assets/shincolle/patchouli_books`
  - Patchouli 手册正文与分类。

## 优先候选

这些内容已经部分数据化，或者当前代码里存在明显的“表驱动”特征，适合后续优先评估 datagen / datapack 化：

- `recipes`
  - 当前已完整进入 `data/shincolle/recipe`，后续可优先用 datagen 统一生成，减少手写 JSON 漏项。
- `loot tables / loot modifiers`
  - 已经迁移到数据目录；适合后续将注入规则、分组与权重整理成生成脚本或更明确的数据模板。
- `tags`
  - 当前数量相对较少，后续适合和配方、worldgen 一起纳入 datagen，避免注册表扩展后手工遗漏。
- `worldgen`
  - 已在数据目录中，后续适合通过 datagen 固化 `configured_feature / placed_feature / biome_modifier` 的关联关系。
- `lang`
  - 当前依赖大量手写 key，后续可考虑先对“固定模式”键建立生成器，例如 spawn egg、配置项、Patchouli 成套键。
- `models / blockstates`
  - 物品蛋、变体模型和基础 block/item model 存在明显模板化机会，适合后续做规则生成。
- `Patchouli book skeleton`
  - 目录已经固定，但正文仍偏手工维护；后续可先对目录索引、词条骨架或统一页模板做生成，而不是一次性数据化全文。

## 暂不建议直接数据化的内容

以下内容当前仍明显依赖运行时逻辑、注册表对象或复杂旧版语义，不建议在当前阶段强行改成纯 datapack：

- `ShipyardRecipes` 中的建造概率、均值与候选选择算法
  - 当前不仅有候选表，还有概率分布、材料偏好、回退逻辑与旧版兼容语义。
  - 后续可以先提炼“候选表”，但选择算法仍应保留代码层守卫。
- `LegacyEquipStats` / `LegacyEquipItem` 相关装备数值
  - 目前和物品实现、tooltip、附魔显示、JEI 映射高度耦合。
  - 若要数据化，需先拆清“展示值”“战斗计算值”“变体映射”三个层面。
- `HostileSpawnManager`
  - 敌对生成包含玩家状态、环境、随机广播、失败守卫等逻辑，不适合直接下沉为 datapack。
- `TaskHelper`
  - 采矿、钓鱼、烹饪、合成等自动化任务依赖大量运行时条件判断，现阶段更适合保留代码。
- `EntityShipBase` 及其战斗 / AI / 被动反应链
  - 这是核心行为语义，不属于 datapack 优先拆分范围。

## 推荐拆分顺序

后续如果继续推进数据驱动迁移，建议按下面顺序执行：

1. `recipe + tags`
2. `loot tables + loot modifiers`
3. `worldgen + biome modifiers`
4. `models + blockstates`
5. `lang` 中可模板化的批量 key
6. `Patchouli` 的目录骨架与统一模板
7. 最后再评估 `ShipyardRecipes` / `LegacyEquipStats` 这类高耦合表驱动逻辑

## 当前结论

- 仓库已经完成第一阶段的数据包化：`recipes / loot / worldgen / tags / Patchouli / lang / models` 均已落到资源目录。
- 下一阶段最值得做的是“用 datagen 减少重复手写资源”，而不是把 AI、战斗、自动化任务等运行时逻辑误判成 datapack 候选。
- `JEI`、`Patchouli`、资源引用链路等已经有测试守卫，后续新增数据化改动时应继续补对应防回退测试。
