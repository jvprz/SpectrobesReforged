# About this wiki

This wiki documents **<!-- VERSION -->0.6.0-beta.1<!-- /VERSION -->** from the local source tree. It is a source-based guide, not a claim that every described feature has been tested across all servers and modpacks.

The website builds directly from Markdown in `docs/`. Reference tables are generated from the bundled species, recipes, biome tags, loot tables and mineral registrations. A version change in `gradle.properties` updates the displayed version on the next build.

## Evidence behind the guides

| Guide | Main implementation or data |
| --- | --- |
| Installation | `gradle.properties`, `META-INF/neoforge.mods.toml` |
| Fossils | Biome modifiers, biome tags, placed features, fossil block loot tables |
| Awakening | `IncubatorBlock`, `IncubatorBlockEntity`, `IncubatorMenu`, `IncubatorMusic` |
| Collection | `PrizmodData`, `PrizmodItem`, `C2SMoveSpectrobe`, `SpectrobeManager` |
| Companion | Follow and portal guards; Spectrobe goals and spawn handling |
| Scanning | `ModKeybinds`, scan packet handler, `SpectrobeRangeRenderer`, `FossilScan` |
| Minerals | `ModItems`, `MineralItem`, `ChromaMineralItem`, recipe and loot JSON |
| Krawl | Krawl JSON, factory, entity, spawn eggs and entity registration |
| Commands | `ModCommands` |

## Help keep the guide accurate

Report an incorrect instruction or propose a documentation change on [GitHub](https://github.com/jvprz/SpectrobesReforged). Include the version you tested. A feature described by a JSON field or flavor text is not marked playable unless the surrounding implementation supports it.

This is a fan-made project. Spectrobes Reforged is not an official Minecraft or Spectrobes product.
