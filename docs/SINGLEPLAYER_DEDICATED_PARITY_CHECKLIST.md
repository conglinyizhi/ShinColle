# ShinColle 单人世界与专用服一致性检查清单

## 目的与边界

本文档用于收敛 issue 1 中“对单人世界与专用服进行一致性测试”的当前证据基线，重点覆盖以下四类风险：

- 菜单
- 网络同步
- 存档重进
- 实体行为

当前结论仅代表：

- 已有自动化回归测试能够覆盖一批单机/联机共用的协议、状态与持久化约束
- 已整理出单人世界与专用服的手工复测矩阵，便于继续做真实运行环境核对

本文档不宣称“所有单人世界与专用服差异已经完全消除”。凡是仍需依赖集成服或真实专用服运行结果确认的项目，均保留为手工复测项。

## 1. 菜单

### 当前自动化证据

- `src/test/kotlin/org/trp/shincolle/menu/BlockMenuProtocolRegressionTest.kt`
  - 锁定方块菜单的 `RegistryFriendlyByteBuf` 解码、`BlockPos` 载荷格式、方块实体校验与失效分支
- `src/test/kotlin/org/trp/shincolle/server/ShipMenuOpenGuardRegressionTest.kt`
  - 锁定舰娘菜单仅允许服务端玩家、同维度、存活且归属正确的实体打开
- `src/test/kotlin/org/trp/shincolle/client/DeskRadarOpenShipRegressionTest.kt`
  - 锁定桌面雷达打开舰娘界面的入口仍通过受控载荷和服务端校验链路

### 单人世界手工复测

- 在单人世界中分别打开船坞、起重机、VolCore 等方块菜单，确认不会出现空菜单、错位菜单或打开后立刻失效
- 使用舰娘右键、潜行右键、桌面雷达与指针相关入口打开舰娘菜单，确认界面可见且不会打开到错误目标
- 破坏或替换对应方块实体后再次尝试打开菜单，确认失败表现明确且不会导致客户端假死

### 专用服手工复测

- 在专用服中重复同一组方块菜单和舰娘菜单操作，确认客户端与服务端打开条件一致
- 使用非拥有者、跨维度或目标实体刚移除的场景尝试打开舰娘菜单，确认服务端拒绝逻辑稳定
- 检查菜单打开后拖拽物品、关闭菜单、再次打开时是否存在状态不同步

### 当前状态

- 菜单协议与服务端守卫已有自动化基线
- 仍需在真实专用服环境补做打开时序、权限拒绝反馈和异常断线场景复测

## 2. 网络同步

### 当前自动化证据

- `src/test/kotlin/org/trp/shincolle/network/PayloadClientSyncTest.kt`
  - 锁定客户端同步载荷在缺少本地玩家引用时的容错行为
- `src/test/kotlin/org/trp/shincolle/network/PayloadPlayerGuardRegressionTest.kt`
  - 锁定关键 C2S 载荷处理器仍保持独立入口，避免把玩家守卫散落回调用点
- `src/test/kotlin/org/trp/shincolle/client/DeskRadarOpenShipRegressionTest.kt`
  - 覆盖桌面雷达相关载荷入口
- `src/test/kotlin/org/trp/shincolle/server/PlayerStatePersistenceArchitectureRegressionTest.kt`
  - 锁定玩家状态同步、编队/指针/外交等入口继续通过统一服务层分发

### 单人世界手工复测

- 在单人世界切换编队、指针目标、桌面 GUI 状态与外交信息，确认界面和实体状态同步正常
- 反复打开/关闭相关界面并触发载荷，确认本地集成服环境下不会出现重复包、副作用或客户端残留状态

### 专用服手工复测

- 在专用服中重复编队、指针、桌面雷达与外交操作，确认客户端显示与服务端真实状态一致
- 在网络抖动、快速连点或目标实体刚失效时复测，确认服务端守卫不会放过无效载荷，也不会错误污染玩家状态

### 当前状态

- 网络入口、客户端同步容错与服务层分发已具备自动化回归约束
- 仍需通过真实专用服联机验证时序、延迟和多人并发下的表现

## 3. 存档重进

### 当前自动化证据

- `src/test/kotlin/org/trp/shincolle/server/PlayerStatePersistenceArchitectureRegressionTest.kt`
  - 锁定玩家附件、死亡复制、登录同步与编队槽位写入边界
- `src/test/kotlin/org/trp/shincolle/server/AdmiralDataPersistenceRegressionTest.kt`
  - 锁定提督数据与相关持久化结构的序列化/反序列化契约

### 单人世界手工复测

- 在单人世界中修改提督数据、编队、桌面书籍状态后退出再进入，确认状态能够恢复
- 在拥有舰队、婚舰计数或外交数据时执行死亡、重生、回档和再次进入，确认附件复制与重载结果稳定

### 专用服手工复测

- 在专用服中完成同一组状态修改后断开重连，确认玩家状态与服务器保存结果一致
- 重点检查存档保存时机、重连首包同步和多次进出服务器后的状态漂移

### 当前状态

- 已有自动化测试锁定主要持久化边界和服务层职责
- 仍需在真实运行环境继续核对退出时机、断线重连与多人同服时的保存一致性

## 4. 实体行为

### 当前自动化证据

- `docs/ENTITY_DIFF_CHECK.md`
  - 记录 issue 1 点名实体/AI 差异的迁移核对基线
- `src/test/kotlin/org/trp/shincolle/entity/ShipLegacyNbtCompatibilityRegressionTest.kt`
  - 锁定旧版舰娘 NBT 兼容边界
- `src/test/kotlin/org/trp/shincolle/server/ShipMenuOpenGuardRegressionTest.kt`
  - 覆盖菜单相关的实体存活、归属与移除态守卫
- `src/test/kotlin/org/trp/shincolle/server/PlayerStatePersistenceArchitectureRegressionTest.kt`
  - 覆盖婚舰计数扫描、指针交互与部分实体状态访问边界

### 单人世界手工复测

- 检查舰娘待机/跟随切换、潜行右键交互、菜单打开、实体移除后的交互失败路径
- 检查旧存档导入后的实体生成、行为恢复和基础 AI 逻辑是否符合预期

### 专用服手工复测

- 在专用服中核对实体生成、跟随、待机、菜单交互、移除/死亡后的同步表现
- 检查服务器重启或玩家重连后，实体是否仍维持预期归属、状态与交互结果

### 当前状态

- 实体迁移差异与若干关键回归点已有文档和测试基线
- 真实专用服下的 AI 节奏、交互顺序和多人附近实体同步仍需继续实机核对

## 复测记录建议

后续补做真实单人世界与专用服联调时，建议按以下格式追加记录，避免结论不可追溯：

| 日期 | 环境 | 范围 | 结果 | 备注 |
| --- | --- | --- | --- | --- |
| 2026-06-11 | 基线整理 | 菜单 / 网络同步 / 存档重进 / 实体行为 | 已建立自动化证据索引与手工复测矩阵 | 未宣称已完成真实专用服联调 |
