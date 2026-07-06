# Spectrobes Reforged

> A complete reimagining of the Spectrobes universe inside Minecraft.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-informational)
![NeoForge](https://img.shields.io/badge/Loader-NeoForge-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![License](https://img.shields.io/badge/License-MIT-green)
![Status](https://img.shields.io/badge/Status-Alpha-yellow)

Spectrobes Reforged is a fan-made mod for **Minecraft 1.21.1
(NeoForge)** that recreates the exploration, excavation, revival and
creature progression systems from the original *Spectrobes* games while
adapting them to Minecraft's sandbox gameplay.

Rather than being a direct port, the mod is designed as a faithful
reinterpretation built specifically around Minecraft mechanics.

------------------------------------------------------------------------

# Features

-   Fossil discovery and excavation
-   Spectrobe revival and progression (in development)
-   Prizmod storage and team management
-   Krawl enemies with JSON-driven definitions
-   Biome-based world generation
-   Custom blocks, items and assets
-   Modular and data-driven architecture

------------------------------------------------------------------------

# Current Progress

## Implemented

-   Fossil generation
-   Fossil items and rarity system
-   Prizmod storage
-   Active team management
-   JSON-based Krawl system
-   Custom entities using GeckoLib
-   Persistent data using Minecraft Codecs

## In Progress

-   Spectrobe awakening
-   Evolution system
-   Incubator mechanics
-   Combat balancing
-   Krawl nests
-   Laboratory mechanics

------------------------------------------------------------------------

# Project Architecture

``` text
common
├── content
├── feature
│   ├── fossil
│   ├── krawl
│   ├── mineral
│   ├── prizmod
│   └── spectrobe
├── network
└── registry
```

Each gameplay system contains its own data models, loaders, parsers and
logic to keep the project modular and scalable.

------------------------------------------------------------------------

# Data-Driven Design

Most game content is loaded from JSON resources.

Current JSON systems include:

-   Krawls
-   Spectrobes
-   Fossils
-   Minerals

Adding new content usually requires only creating a new JSON definition
rather than modifying gameplay code.

------------------------------------------------------------------------

# Technology Stack

-   Java 21
-   NeoForge 1.21.1
-   GeckoLib
-   Minecraft Codec API
-   Gradle
-   IntelliJ IDEA
-   JUnit 5

------------------------------------------------------------------------

# Testing

Run all tests with:

``` bash
gradlew test
```

------------------------------------------------------------------------

# Development Setup

``` bash
git clone https://github.com/your-repository/spectrobes-reforged.git
cd spectrobes-reforged
gradlew genIntellijRuns
```

Open the project in IntelliJ IDEA and launch the Minecraft Client run
configuration.

------------------------------------------------------------------------

# Roadmap

## Alpha

-   [x] Fossil system
-   [x] Prizmod
-   [x] JSON loaders
-   [x] Krawls
-   [ ] Spectrobe revival
-   [ ] Incubator
-   [ ] Evolution

## Beta

-   [ ] Combat
-   [ ] World structures
-   [ ] Multiplayer polish

## Release

-   [ ] Full Spectrobe roster
-   [ ] Boss encounters
-   [ ] Story progression

------------------------------------------------------------------------

# License

MIT License.

------------------------------------------------------------------------

# Credits

Original Spectrobes intellectual property belongs to Disney and Jupiter
Corporation.

Fan project developed by **JPrimee (@jvprz)**.
