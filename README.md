# Spectrobes Reforged

Spectrobes Reforged is a fan-made Minecraft mod that reimagines the mechanics and universe of the original *Spectrobes* games within the Minecraft sandbox.

The project is built for **NeoForge on Minecraft 1.21.1** and aims to recreate the core gameplay loop of the Spectrobes franchise while adapting it to Minecraft’s systems and world generation.

---

## Project Overview

Spectrobes Reforged is not a direct port of the original games.  
Instead, it is a reinterpretation designed specifically for Minecraft, integrating Spectrobes-inspired mechanics into the game's existing survival and exploration systems.

The long-term goal of the project is to introduce a complete gameplay loop involving fossil discovery, excavation, cleaning, revival, and spectrobe management.

Planned gameplay pillars include:

- Fossil excavation and discovery
- Biome-driven fossil generation
- Fossil cleaning and revival mechanics
- Spectrobe awakening and evolution
- Team management systems
- Exploration and world interaction mechanics

---

## Current Features

### Fossil System

- Fossil ore blocks generate naturally in the world.
- Fossil types are determined by biome categories:
  - Corona
  - Aurora
  - Flash
- Breaking fossil ore blocks yields raw fossil items.
- Fossils include rarity-based formatting and categorization.

### Prizmod Storage System

The Prizmod acts as the primary spectrobe management interface.

Features include:

- Box storage system
- Six-slot active team management
- Dedicated baby slot
- Persistent storage using Minecraft Codec-based serialization
- Drag-and-drop spectrobe management
- Quick transfer mechanics
- Stat tooltips and icon rendering

### Custom Blocks

- Fossil Ore Blocks
- Incubator Block
- Custom drop logic
- Creative mode drop handling

### Custom Assets

- Custom textures
- Independent item icons
- Spawn eggs
- Transparent texture handling
- Color-coded fossil subtitles

---

## Work in Progress

The following systems are currently under development:

- Spectrobe awakening mechanics
- Initial spectrobe implementation (Komainu)
- Spectrobe stage system (Child / Adult / Evolved)
- Combat and team battle systems
- Incubator interaction mechanics
- Krawl nest world structures
- Fossil laboratory mechanics
- Advanced world generation improvements

---

## Technology Stack

The project is built using the following technologies:

- Java 21
- NeoForge (Minecraft 1.21.1)
- GeckoLib (entity animation framework)
- Minecraft Codec API for persistent data
- Gradle build system
- IntelliJ IDEA development environment

---

## Development Setup

To run the mod in a development environment:

1. Clone the repository.
2. Open the project in IntelliJ IDEA.
3. Run: ```gradlew genIntellijRuns```


4. Launch the **Minecraft Client** run configuration.

---

## License

This project is licensed under the **MIT License**.

See the `LICENSE` file for full license details.

---

## Credits

Original *Spectrobes* intellectual property is owned by **Disney** and **Jupiter Corporation**.

Spectrobes Reforged is an independent fan project developed by:

**JPrimee (@jvprz)**

---

## Disclaimer

Spectrobes Reforged is a non-commercial fan project.  
All rights to the original Spectrobes franchise remain with their respective copyright holders.
