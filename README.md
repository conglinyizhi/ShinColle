[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/conglinyizhi/ShinColle)

# ShinColle for NeoForge 1.21.1

[中文说明](./README.zh-CN.md)

⚠️ Due to a lack of positive feedback signals and project management issues (essentially too many new projects and insufficient time), active development on this project has been temporarily suspended. Honestly speaking, the mod is currently largely playable — after running on Linux for some time, essentially no crashes have been found. This project will not be archived; it will remain open for issue submissions and will still accept reports of runtime problems and mod conflicts.

This repository is a NeoForge 1.21.1 continuation and migration of ShinColle, with most code done by Coding Agent.

Current work is based on the following upstream projects:

- [reiwa/Shincolle-1.21.1](https://github.com/reiwa/Shincolle-1.21.1) — This project provides a working Kantai Collection port on NeoForge 1.21.1. This project is built upon its foundation for extensive (perhaps) development. Thanks to the developer for providing the development base.
- [PinkaLulan/ShinColle](https://github.com/PinkaLulan/ShinColle) — The original author's open-source repository for version 1.12.2.

Both upstream projects currently publish under the MIT License, and this repository is also distributed under the MIT License.

See [LICENSE](./LICENSE) and [NOTICE](./NOTICE) for license and upstream attribution details.

Related upstream references:

- Update history: [updates.MD](https://github.com/reiwa/Shincolle-1.21.1/blob/main/updates.MD)
- Upstream jar archive: [Shincolle-1.12.2-1.21.1alpha](https://github.com/reiwa/Shincolle-1.12.2-1.21.1alpha)

## Dependencies

- **NeoForge** `21.1.x` or later
- **Kotlin for Forge** (NeoForge edition) `5.10.0` or later  
  ⚠️ Make sure to install the **NeoForge** edition of Kotlin for Forge, not the legacy Forge edition.

## Font Driver

This project uses the free commercial-license [MiSans](https://hyperos.mi.com/font) as its font driver for legacy deep-sea log books (the desk book and the held-item book when Patchouli is not installed).

The font is embedded at `assets/shincolle/font/MiSans-Normal.ttf`.

**Configuration** (via `shincolle-client.toml`):

- `misans_font.useMiSansFont` — Enable MiSans font (default: `true`)
- `misans_font.miSansOnlyForLegacyLogs` — Only apply to legacy logs (default: `true`)
