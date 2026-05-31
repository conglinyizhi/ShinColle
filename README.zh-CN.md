[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/conglinyizhi/ShinColle)
# ShinColle NeoForge 1.21.1

[English](./README.md)

这个仓库是 ShinColle 在 NeoForge 1.21.1 平台上的延续与迁移工程。

当前工作基于以下上游项目继续推进：
- [reiwa/Shincolle-1.21.1](https://github.com/reiwa/Shincolle-1.21.1)
- [PinkaLulan/ShinColle](https://github.com/PinkaLulan/ShinColle)

上述两个上游项目当前均采用 MIT License 发布，本仓库也同样采用 MIT License。

许可证与上游归属说明请参见：
- [LICENSE](./LICENSE)
- [NOTICE](./NOTICE)

相关上游参考：
- 更新记录：[updates.MD](https://github.com/reiwa/Shincolle-1.21.1/blob/main/updates.MD)
- 上游 jar 归档：[Shincolle-1.12.2-1.21.1alpha](https://github.com/reiwa/Shincolle-1.12.2-1.21.1alpha)


## 字体驱动

本项目使用 [MiSans](https://hyperos.mi.com/font) 作为字体驱动，用于旧版本深海日志的渲染（即办公桌内嵌的书籍与玩家手持物品在无帕秋莉手册时的显示）。

字体文件位于 `assets/shincolle/font/MiSans-Normal.ttf`。

**配置项**（`shincolle-client.toml`）：
- `misans_font.useMiSansFont` — 启用 MiSans 字体（默认：`true`）
- `misans_font.miSansOnlyForLegacyLogs` — 仅影响旧版本深海日志（默认：`true`）
