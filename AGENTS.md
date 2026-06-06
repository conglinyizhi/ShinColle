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

## API 文档约定

- `org.trp.shincolle.api` 包下的公共接口属于第三方扩展 API，变更时必须同步更新对应文档：
  - `api/equip/` 变更 → 更新 `docs/THIRD_PARTY_EQUIP.md`
  - `api/consumable/` 变更 → 更新 `docs/THIRD_PARTY_CONSUMABLE.md`
- 新增回调、修改方法签名、删除接口均视为破坏性变更，需在文档中标注版本历史。
