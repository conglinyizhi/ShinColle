# ShinColle Domain Package Evaluation

## 目的

本文档用于落实 issue 1 中“评估是否按 `ship / fleet / diplomacy / automation / manual` 等领域进一步拆分包结构”的要求。

当前目标不是立刻进行大规模包迁移，而是先基于仓库现状给出一份可执行的评估结论，明确：

- 哪些领域已经有自然边界
- 哪些领域适合继续收敛
- 哪些区域当前不适合强行按领域拆包

## 当前结构观察

当前主代码大致分为几类：

- 运行时行为层：
  - `entity`
  - `server`
  - `event`
  - `network`
- 表现层：
  - `client`
  - `menu`
  - `block`
  - `item`
- 注册与边界层：
  - `init`
  - `api`
  - `compat`
  - `datagen`
  - `integration`

这说明仓库当前主要还是“技术分层 + 少量业务收敛”的结构，而不是完整的领域包结构。

## 领域评估

### 1. `ship`

现状：

- `entity/base` 已经天然承载舰娘主体行为
- 大量舰娘实体类型仍集中在 `entity/`
- 舰娘菜单、战斗、寻路、表现和交互分散在 `entity / menu / client / server`

结论：

- `ship` 是真实存在的核心领域
- 但当前它天然横跨实体、客户端渲染、菜单和服务层，不适合在这一阶段做纯粹按包移动的大拆分
- 更合理的方向是继续通过 `EntityShipBase*`、`ShipMovementCoordinator`、`PlayerSkillService` 一类边界逐步收敛“舰娘语义”，而不是先改目录

### 2. `fleet`

现状：

- `FormationService`
- `PlayerStateService`
- `ShipRegistrySavedData`

这些已经在 `server/` 下形成了相对清晰的舰队/编队边界。

结论：

- `fleet` 是当前最接近独立领域包的候选
- 但在主线迁移尚未完全结束前，继续保留在 `server/` 下比整体搬迁到 `fleet/` 更稳妥
- 后续若继续拆包，建议优先从 `FormationService` 和队伍槽位/已拥有舰娘数据开始

### 3. `diplomacy`

现状：

- `TeamDiplomacyService`
- `TeamDiplomacySavedData`
- `TargetProtectionService`

这些已经具备独立业务边界，且和舰娘实体本体耦合度低于 `ship`。

结论：

- `diplomacy` 同样是合理的独立领域候选
- 当前保留在 `server/` 更合适，因为它仍依赖统一玩家状态、同步入口和 SavedData 生命周期
- 后续若拆包，优先级可以高于 `ship`

### 4. `automation`

现状：

- `WaypointService`
- `TaskHelper`
- 船坞、起重机、火山核心、多方块和容器写回逻辑散落在 `server / block / block.entity / menu / crafting`

结论：

- `automation` 是一个真实但跨层非常明显的领域
- 当前最关键的问题不是目录名，而是运行时语义和容器/配方/路径点边界
- 在迁移稳定前，不建议为了领域命名而强行移动 `TaskHelper`、船坞方块实体或菜单类

### 5. `manual`

现状：

- Patchouli 文档、书籍物品、书籍渲染、Patchouli 资源和截图资料分布在 `item / client / docs / assets`

结论：

- `manual` 更像“内容系统 + 客户端呈现”的组合，不是单一运行时领域
- 当前最好的边界仍然是“文档资源 + 书籍入口物品 + 客户端渲染辅助”
- 暂不建议专门为了目录名建立 `manual/` 代码包

## 当前推荐

基于上述评估，当前推荐是：

1. 不进行仓库级大规模领域迁包。
2. 继续保留现有 `client / entity / server / item / block / menu / init` 技术分层。
3. 在这些技术层内部继续强化业务边界：
   - `fleet`：编队、队伍槽位、拥有舰队状态
   - `diplomacy`：外交、目标保护、敌友关系
   - `automation`：路径点、任务、船坞与容器自动化
4. 若未来继续拆包，优先级建议为：
   - `diplomacy`
   - `fleet`
   - `automation`
   - `ship`
   - `manual`

## 暂不建议立即拆包的原因

- `ship`、`automation`、`manual` 都明显跨越客户端、服务端、实体、菜单和资源层
- 当前主线迁移虽已接近收尾，但仍应优先保持行为语义稳定
- 强行按领域迁目录，短期内更可能制造 import 扰动和回归噪音，而不是带来立即收益

## 当前结论

- 已完成领域拆分评估
- 结论不是“必须现在拆包”，而是“继续保持技术分层，并优先在 `server/` 内收敛 `fleet / diplomacy / automation` 的业务边界”
- 后续若真的启动领域拆包，应优先从 `diplomacy` 和 `fleet` 两块开始，而不是先动 `ship`
