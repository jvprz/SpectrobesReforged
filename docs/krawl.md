# Krawl enemies

The current mod includes **Blova, Viblova, and Blovant** definitions with custom models, sounds and melee behavior. They can be created using their registered spawn eggs.

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

Krawl definitions include spawn-related fields, but the reviewed code does not register a natural-spawn biome integration for these enemies. Do not assume a configured weight or light range means they will appear naturally. Use eggs for reproducible testing.

Krawl nests and corrupted areas are not implemented in the bundled content. A complete Spectrobe team combat loop is also still in development.
