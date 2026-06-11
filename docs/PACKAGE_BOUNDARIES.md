# ShinColle Package Boundaries

## 目的

本文档用于落实 issue 1 中“预留 `compat` / `api` / `datagen` 等目录边界”的要求。

当前目标不是立刻把仓库重构成完整多层架构，而是先明确三个稳定结论：

- `api`：第三方扩展边界，继续作为公开接口区域维护
- `compat`：未来外部模组兼容与桥接逻辑的预留落点
- `datagen`：未来数据生成脚本、生成器入口与资源导出逻辑的预留落点

## 当前约束

### `api`

- 已存在 `src/main/java/org/trp/shincolle/api`
- 该目录下的公共接口继续受第三方文档约束：
  - `docs/THIRD_PARTY_EQUIP.md`
  - `docs/THIRD_PARTY_CONSUMABLE.md`
  - `docs/THIRD_PARTY_ENTITY.md`

### `compat`

- 新增 `src/main/java/org/trp/shincolle/compat`
- 当前仅作为目录边界预留，不要求本轮迁移立即把现有 `integration/` 逻辑整体迁入
- 后续若增加 JEI、Jade 或其他外部模组桥接层，应优先评估是否落在该边界而不是继续扩散到通用业务包

### `datagen`

- 新增 `src/main/java/org/trp/shincolle/datagen`
- 当前仅作为目录边界预留，不要求本轮迁移立即补全 datagen 运行入口
- 后续若开始生成 `recipes / loot / tags / worldgen / lang / models`，应优先从该边界进入，而不是把生成脚本混入 `init / utility / build` 等运行时代码包

## 非目标

以下事项不属于本轮完成标准：

- 不要求立刻迁移所有 `integration/` 实现
- 不要求立刻新增完整 datagen pipeline
- 不要求调整当前 `api` 下已有公开接口的语义
- 不把目录预留本身当作主线迁移阻塞项

## 当前状态

- `api` 已存在并持续使用
- `compat` 已建立目录占位
- `datagen` 已建立目录占位
- 后续可以在不打断主线迁移的前提下，逐步把兼容层与数据生成入口向这些边界收拢
