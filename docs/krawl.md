# Krawl enemies

The current mod includes **Blova, Viblova and Blovant**, with animated models, sounds, melee attacks and spawn eggs. [Vortices](vortices.md) produce three identical Krawl per participant: **Blovant/Corona in hot climates, green Viblova/Aurora in temperate climates and blue Blova/Flash in cold climates**. The origin biome fixes the type; tags override base-temperature fallback. See the vortex guide for the exact rules.

## Current definitions

<!-- BEGIN AUTO:KRAWL -->

| Krawl | Element | Rank | HP | Attack | Armor |
| --- | --- | --- | --- | --- | --- |
| Blova | Flash | Basic | 20 | 4 | 2 |
| Blovant | Corona | Basic | 20 | 4 | 2 |
| Viblova | Aurora | Basic | 20 | 4 | 2 |

<!-- END AUTO:KRAWL -->

These are the configured entity attributes, not a guarantee of final combat damage after every armor or gameplay modifier.

## Try them in a test world

Use the Creative inventory’s spawn-egg tab, or give yourself the eggs with operator permission:

```text
/give @s spectrobesreforged:blova_spawn_egg
/give @s spectrobesreforged:viblova_spawn_egg
/give @s spectrobesreforged:blovant_spawn_egg
```

Use an egg on a block to create that Krawl. Test away from an important build or companion.

## Natural spawns and nests

Krawl definitions include spawn-related fields, but ordinary biome-based natural spawning is not registered. This beta uses [small vortices](vortices.md) for natural Survival encounters in the Overworld. They detect players at 64 blocks and pursue at about 5 blocks/second. The guide documents discovery checks, cooldowns and biome selection.

Large nests and corrupted areas are not implemented. A complete Spectrobe team combat loop is also still in development.
