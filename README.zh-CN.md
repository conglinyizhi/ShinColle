[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/conglinyizhi/ShinColle)

# ShinColle NeoForge 1.21.1

[English](./README.md)

⚠️ 因为缺少积极的反馈信号以及项目管理问题（实际上是新项目太多、时间不够）暂时停止对该项目的后续积极开发工作。但实话说，该模组目前基本可玩，在 Linux 上运行一段时间，基本上没有发现什么崩溃问题。该项目不会归档存储，用于开放 issue 的提交，仍会接受运行时发生的各种问题，以及和其他模组冲突的问题

这个仓库是 ShinColle 在 NeoForge 1.21.1 平台上的延续与迁移工程，多数代码由 Coding Agent 完成

当前工作基于以下上游项目继续推进：

- [reiwa/Shincolle-1.21.1](https://github.com/reiwa/Shincolle-1.21.1) 该项目提供了一个能跑的舰队收藏移植在 1.21.1 NeoForge 基座，本项目是基于这个项目的工作进行大量（或许）开发，在此感谢该开发者提供的开发基座
- [PinkaLulan/ShinColle](https://github.com/PinkaLulan/ShinColle) 1.12.2 版本的原始作者开源的仓库

上述两个上游项目当前均采用 MIT License 发布，本仓库也同样采用 MIT License。

许可证与上游归属说明请参见：

- [LICENSE](./LICENSE)
- [NOTICE](./NOTICE)

相关上游参考：

- 更新记录：[updates.MD](https://github.com/reiwa/Shincolle-1.21.1/blob/main/updates.MD)
- 上游 jar 归档：[Shincolle-1.12.2-1.21.1alpha](https://github.com/reiwa/Shincolle-1.12.2-1.21.1alpha)

## 依赖

- **NeoForge** `21.1.x` 或更高版本
- **Kotlin for Forge**（NeoForge 版）`5.10.0` 或更高版本  
  ⚠️ 请务必安装 **NeoForge 版** Kotlin for Forge，而非旧版 Forge 专用版本。

## 字体驱动

本项目使用免费商用许可的 [MiSans](https://hyperos.mi.com/font) 作为字体驱动，用于旧版本深海日志的渲染（即办公桌内嵌的书籍与玩家手持物品在无帕秋莉手册时的显示）。

字体文件位于 `assets/shincolle/font/MiSans-Normal.ttf`。

**配置项**（`shincolle-client.toml`）：

- `misans_font.useMiSansFont` — 启用 MiSans 字体（默认：`true`）
- `misans_font.miSansOnlyForLegacyLogs` — 仅影响旧版本深海日志（默认：`true`）
