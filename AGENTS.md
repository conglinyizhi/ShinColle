# AGENTS

## 提交约定

- 提交信息必须遵循约定式提交（Conventional Commits）。
- 提交信息的 `message` 部分使用中文。
- commit body 中不使用 emoji。

## 调试约定

- 遇到游戏内难以稳定复现、仅靠静态阅读难以定位的问题时，优先在关键交互入口、状态流转节点和失败恢复分支加入可检索日志，再让用户复测并根据 `latest.log` 继续定位。
- 调试日志应使用统一且易于检索的前缀，避免和普通运行日志混杂。

## 当前配置约定

- `ModernKit` 满改无效果时应给玩家明确反馈，默认使用轻提示。
- 相关配置项为 `modernKitNotifyWhenMaxed` 和 `modernKitNotifyWhenMaxedActionBar`，位于 `common` 配置的 `ship_interaction` 分组。

## API 版本约定

- 模组版本号遵循语义化版本号（SemVer，https://semver.org/），格式为 `MAJOR.MINOR.PATCH`。
- 自 `v0.0.1` 起，`org.trp.shincolle.api` 包下的公共接口版本与模组版本绑定：
  - **MAJOR** 递增：API 发生不兼容变更（删除接口、修改方法签名、改变回调语义等）。
  - **MINOR** 递增：向下兼容的功能新增（新增接口、新增回调、新增可选方法等）。
  - **PATCH** 递增：向下兼容的问题修复（bug 修复、异常拦截加固、文档勘误等）。
- 提交信息中的 `feat(api)`、`fix(api)`、`docs(api)` 类型变更，在审查时应核对版本号是否需要同步递增。

## API 文档约定

- `org.trp.shincolle.api` 包下的公共接口属于第三方扩展 API，变更时必须同步更新对应文档：
  - `api/equip/` 变更 → 更新 `docs/THIRD_PARTY_EQUIP.md`
  - `api/consumable/` 变更 → 更新 `docs/THIRD_PARTY_CONSUMABLE.md`
- 新增回调、修改方法签名、删除接口均视为破坏性变更，需在文档版本历史中标注引入/变更的模组版本号。
