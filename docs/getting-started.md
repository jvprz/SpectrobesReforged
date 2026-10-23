# Getting started

## Requirements

| Component | Current requirement |
| --- | --- |
| Minecraft | 1.21.1 |
| Loader | NeoForge 21.1.219 or newer for Minecraft 1.21.1 |
| GeckoLib | 4.8.3 or newer for NeoForge 1.21.1 |
| Java | 21 |

These requirements describe <!-- VERSION -->0.6.1-beta<!-- /VERSION -->. Match every download to **Minecraft 1.21.1 and NeoForge**; older mod releases have different requirements.

## Install

1. Create a Minecraft 1.21.1 instance with NeoForge.
2. Download Spectrobes Reforged from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/spectrobes-reforged).
3. Install the matching GeckoLib dependency. A launcher may offer to install it automatically.
4. For a manual installation, place the mod and GeckoLib JAR files in that instance’s `mods` folder.
5. Launch the NeoForge profile and create a test world.

Do not leave multiple versions of Spectrobes Reforged in the same instance. Back up existing worlds before updating a beta mod.

## Craft your equipment

> **Added in 0.6.0-beta.1:** equipment recipes, dyeing and advancements. Update from 0.5.1-alpha to use them.

Use a crafting table. In the grids below, each letter represents one item.

### White Prizmod

```text
A P A
I G I
C - C
```

- **A:** amethyst shard (2)
- **P:** glass pane (1)
- **I:** iron ingot (2)
- **G:** gold ingot (1)
- **C:** copper ingot (2)
- **-:** empty slot

Produces one white Prizmod. Combine any Prizmod with one dye in a shapeless recipe to recolor it. All 16 colors are supported, including returning to white. Your collection belongs to the player, so recoloring does not erase it.

### Incubator

```text
G G G
I S I
O C O
```

- **G:** glass block (3)
- **I:** iron ingot (2)
- **S:** calibrated sculk sensor (1)
- **O:** gold ingot (2)
- **C:** copper ingot (1)

Produces one incubator. The ingredient is a **calibrated sculk sensor**, not a regular sculk sensor. This places survival access behind obtaining a sculk sensor and calibrating it. Crafting it does not supply power: place it and provide a redstone signal.

The recipe book unlocks the white Prizmod after obtaining iron, the incubator after obtaining a calibrated sculk sensor, and recoloring recipes after obtaining a Prizmod. These unlock conditions do not restrict manual crafting unless a server enables limited crafting.

For Creative testing, the existing `/give` commands still work. Try a Komainu fossil for your first awakening.

## Your first companion

1. Place the incubator with two horizontal spaces available.
2. Supply redstone power, for example from a nearby powered lever.
3. Open it, insert the Komainu fossil, and click its start button.
4. Keep it powered for about 10 seconds. Sounds affect color, not whether the timer advances.
5. Hold the Prizmod and use it to inspect the new entry.

If your child slot was empty, the awakened entry goes there; otherwise it goes into storage. The companion recovery check normally brings a child-slot Spectrobe into the world shortly afterward.

Continue with [awakening](awakening.md) for color control and [Prizmod management](prizmod.md).

## Language and controls

Change the language in Minecraft’s language settings. Available translations are English, Spanish, French, German, Italian, and Brazilian Portuguese.

The default **Scan** key is **R**, under the Spectrobes Reforged controls category. Rebind it if another mod uses the same key.

## Multiplayer scope

The implementation includes player-owned collection data and network messages. A dedicated-server compatibility test is not part of this documentation review. Test your server/modpack combination before using an important world.
