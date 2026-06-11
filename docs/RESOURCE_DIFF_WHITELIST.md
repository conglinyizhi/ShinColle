# ShinColle Resource Diff Whitelist

本文档用于记录 `ShinColle 1.12.2 -> NeoForge 1.21.1` 迁移过程中，资源层面的“差异白名单”。

目标是把三类情况区分清楚：

- 哪些资源已经在 `1.21.1` 中迁移完成，后续应继续保留。
- 哪些旧资源因为新版架构或 API 变化而**故意不迁移**。
- 哪些资源目前仍属于“待进一步核对 / 清理”的范围，不应误判为已经完成。

## A. 应保留的已迁移资源

以下资源目录已经存在于当前仓库，属于 `1.21.1` 分支中确认需要继续维护的内容：

### 数据包层

- `src/main/resources/data/shincolle/recipe`
  - 旧版配方已迁移到数据包结构。
- `src/main/resources/data/shincolle/loot_table`
  - 方块掉落与注入掉落表。
- `src/main/resources/data/shincolle/loot_modifiers`
  - 宝箱注入规则改为 NeoForge `loot modifier`。
- `src/main/resources/data/shincolle/neoforge/biome_modifier`
  - 世界生成的 biome modifier。
- `src/main/resources/data/shincolle/worldgen/configured_feature`
  - configured feature。
- `src/main/resources/data/shincolle/worldgen/placed_feature`
  - placed feature。
- `src/main/resources/data/shincolle/tags/block`
  - 方块标签。
- `src/main/resources/data/shincolle/tags/item`
  - 物品标签。
- `src/main/resources/data/shincolle/tags/entity_type`
  - 实体标签。
- `src/main/resources/data/shincolle/patchouli_books`
  - Patchouli 手册元数据。
- `src/main/resources/data/neoforge/loot_modifiers/global_loot_modifiers.json`
  - NeoForge 全局 loot modifier 注册入口。

### 资源包层

- `src/main/resources/assets/shincolle/lang`
  - `en_us / ja_jp / zh_cn / zh_tw`
- `src/main/resources/assets/shincolle/models`
  - block / item model
- `src/main/resources/assets/shincolle/blockstates`
  - blockstate 定义
- `src/main/resources/assets/shincolle/particles`
  - 粒子 JSON
- `src/main/resources/assets/shincolle/sounds`
  - 音频资源
- `src/main/resources/assets/shincolle/font`
  - 字体资源
- `src/main/resources/assets/shincolle/patchouli_books`
  - Patchouli 正文、分类与词条

## B. 故意不迁移的旧资源类型

以下内容即使在旧版里以资源或“资源式配置”存在，也不应在 `1.21.1` 中机械照搬：

- 旧版自定义 GUI 书籍资源体系
  - 已由 Patchouli 方案替代，原始手写书页系统不再作为主实现保留。
- 旧版全量实体同步封包配套资源
  - 新版使用 `EntityDataAccessor + DataSlot + 属性系统`，对应旧资源链路不再保留。
- 依赖旧 Forge / 已废弃模组接口的兼容资源
  - 例如 Metamorph 兼容链已明确废弃，不应继续为其保留资源入口。
- 与旧版集中式 Handler 架构绑定的辅助资源
  - 新版已拆分到 `event/ + server/ + entity`，旧命名或旧目录约定不再作为迁移目标。

## C. 当前仍待清理或核对的范围

这些内容不应被误判为“已经彻底完成迁移”，仍需要后续单独处理：

- `recipe` 目录中的残留旧语义文件
  - 当前 issue 已明确仍有“清理已无实现支持的残留 recipes 与相关引用”的后续工作。
- 资源目录中的模板化重复内容
  - 例如部分 `models / lang / Patchouli` 结构已经适合 datagen，但目前仍为手写维护。
- 旧版资源与新版资源的一一对应关系
  - 当前仓库已有迁移结果，但尚未建立“旧资源文件 -> 新资源去向 / 删除原因”的逐项映射表。
- 高耦合代码驱动内容的资源边界
  - 例如 `ShipyardRecipes`、`LegacyEquipStats`、敌对生成和任务系统，仍需要进一步区分哪些只是“表现资源”，哪些属于代码语义。

## D. 白名单判定规则

后续如果再做资源差异核对，建议统一按下面规则判定：

1. 若资源已经在 `src/main/resources/data` 或 `assets` 中有稳定新位置，归入“应保留的已迁移资源”。
2. 若旧资源依赖旧 Forge API、旧 GUI 体系或已废弃兼容链，归入“故意不迁移”。
3. 若当前仓库中仍有对应目录，但 issue 明确标记为后续要清理、拆分或重新归类，则归入“仍待清理或核对”。

## E. 当前结论

- `recipes / loot / worldgen / tags / lang / models / particles / Patchouli` 已经构成 `1.21.1` 分支的主资源基线，应继续保留。
- 白名单的主要作用不是声明“所有旧资源都已迁移”，而是防止后续把“故意删除”和“遗漏未迁移”混为一谈。
- 下一步最值得继续推进的是：
  - 清理残留 recipe 与相关引用。
  - 继续细化“代码实现 vs 数据定义”边界。
  - 在需要时补充更细粒度的旧资源去向映射表。
