# Krawl enemies

The current mod includes **Blova, Viblova, and Blovant** definitions with custom models, sounds and melee behavior. Spawn eggs work for all three. This beta adds [small vortex encounters](vortices.md) that produce three Blova.

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

Krawl definitions include spawn-related fields, but ordinary biome-based natural spawning is not registered. This beta adds rare [small vortices](vortices.md) in the Overworld for Survival players. A vortex follows nearby players slowly and creates an encounter when it catches one, regardless of their held item.

Large nests and corrupted areas are not implemented. A complete Spectrobe team combat loop is also still in development.
