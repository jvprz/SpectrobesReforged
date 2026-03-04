# Spectrobes Reforged

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-informational)
![NeoForge](https://img.shields.io/badge/Loader-NeoForge-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![License](https://img.shields.io/badge/License-MIT-green)
![Status](https://img.shields.io/badge/Status-Alpha-yellow)

Spectrobes Reforged is a fan-made Minecraft mod that reimagines the mechanics and universe of the original *Spectrobes* games inside the Minecraft sandbox.

The project is built for **NeoForge on Minecraft 1.21.1** and aims to recreate the gameplay loop of fossil discovery, spectrobe revival, and team management while adapting these systems to Minecraft’s exploration and survival mechanics.

---

# Project Overview

Spectrobes Reforged is not a direct port of the original games.  
Instead, it is a reinterpretation designed to integrate the core ideas of the Spectrobes franchise into Minecraft’s world systems.

The project focuses on creating a new gameplay loop based on:

- Fossil discovery through world exploration
- Excavation and fossil recovery
- Fossil cleaning and revival
- Spectrobe management and team building
- Exploration systems connected to biome and world generation

---

# Current Features

## Fossil System

- Fossil ore blocks generate naturally in the world
- Fossil types depend on biome classification:
  - Corona
  - Aurora
  - Flash
- Breaking fossil ore yields raw fossil items
- Fossils use rarity-based formatting and categorization

## Prizmod Storage System

The Prizmod acts as the central interface for managing spectrobes.

Features include:

- Box storage system
- Six-slot active team system
- Dedicated baby slot
- Persistent data storage using Minecraft Codec serialization
- Drag-and-drop spectrobe management
- Quick transfer mechanics (shift-click)
- Tooltip interface showing spectrobe statistics
- Variant-based icon rendering

## Custom Blocks

- Fossil Ore Blocks
- Incubator Block
- Custom drop logic
- Creative mode drop handling

## Custom Assets

- Custom textures
- Independent item icons
- Spawn eggs
- Transparent texture handling
- Color-coded fossil subtitles

---

# Work in Progress

The following systems are currently under development:

- Spectrobe awakening mechanics
- Initial spectrobe implementation (Komainu)
- Spectrobe stage system (Child / Adult / Evolved)
- Combat and team battle mechanics
- Incubator interaction system
- Krawl nest structures
- Fossil laboratory mechanics
- Advanced world generation improvements

---

# Project Structure
```
src/main/java/com/jvprz/spectrobesreforged
│
├── client
│ ├── model
│ ├── render
│ ├── screen
│ └── ui
│
├── common
│ ├── content
│ │ ├── block
│ │ ├── entity
│ │ └── item
│ │
│ ├── feature
│ │ ├── prizmod
│ │ └── spectrobe
│ │
│ ├── network
│ └── registry
```
This structure separates client rendering logic, gameplay systems, networking, and registry management to maintain scalability as the mod grows.

---

# Technology Stack

The project is built using the following technologies:

- Java 21
- NeoForge (Minecraft 1.21.1)
- GeckoLib for animated entities
- Minecraft Codec API for persistent data
- Gradle build system
- IntelliJ IDEA development environment

---

# Development Setup

To run the mod in a development environment:

1. Clone the repository
```git clone https://github.com/your-repository/spectrobes-reforged.git```

2. Open the project in IntelliJ IDEA

3. Generate run configurations
```gradlew genIntellijRuns```


4. Launch the **Minecraft Client** run configuration

---

# Versioning

The project follows semantic versioning during development.

Example:
```
0.1.0-alpha Initial entity prototype
0.2.0-alpha Prizmod system and spectrobe stats
```

---

# License

This project is licensed under the **MIT License**.

See the `LICENSE` file for full license information.

---

# Credits

Original *Spectrobes* intellectual property belongs to:

- Disney
- Jupiter Corporation

Spectrobes Reforged is an independent fan project developed by:

**JPrimee (@jvprz)**

---

# Disclaimer

Spectrobes Reforged is a non-commercial fan project.  
All rights to the original Spectrobes franchise remain with their respective copyright holders.


