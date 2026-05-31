[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/conglinyizhi/ShinColle)
# ShinColle for NeoForge 1.21.1

[中文说明](./README.zh-CN.md)

This repository is a NeoForge 1.21.1 continuation and migration of ShinColle.

Current work is based on the following upstream projects:
- [reiwa/Shincolle-1.21.1](https://github.com/reiwa/Shincolle-1.21.1)
- [PinkaLulan/ShinColle](https://github.com/PinkaLulan/ShinColle)

Both upstream projects currently publish under the MIT License, and this repository is also distributed under the MIT License.

See [LICENSE](./LICENSE) and [NOTICE](./NOTICE) for license and upstream attribution details.

Related upstream references:
- Update history: [updates.MD](https://github.com/reiwa/Shincolle-1.21.1/blob/main/updates.MD)
- Upstream jar archive: [Shincolle-1.12.2-1.21.1alpha](https://github.com/reiwa/Shincolle-1.12.2-1.21.1alpha)


## Font Driver

This project uses [MiSans](https://hyperos.mi.com/font) as its font driver for legacy deep-sea log books (the desk book and the held-item book when Patchouli is not installed).

The font is embedded at `assets/shincolle/font/MiSans-Normal.ttf`.

**Configuration** (via `shincolle-client.toml`):
- `misans_font.useMiSansFont` — Enable MiSans font (default: `true`)
- `misans_font.miSansOnlyForLegacyLogs` — Only apply to legacy logs (default: `true`)
