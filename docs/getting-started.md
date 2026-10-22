# Getting started

## Requirements

| Component | Current requirement |
| --- | --- |
| Minecraft | 1.21.1 |
| Loader | NeoForge 21.1.219 or newer for Minecraft 1.21.1 |
| GeckoLib | 4.8.3 or newer for NeoForge 1.21.1 |
| Java | 21 |

These requirements describe <!-- VERSION -->0.5.1-alpha<!-- /VERSION -->. Match every download to **Minecraft 1.21.1 and NeoForge**; older mod releases have different requirements.

## Install

1. Create a Minecraft 1.21.1 instance with NeoForge.
2. Download Spectrobes Reforged from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/spectrobes-reforged).
3. Install the matching GeckoLib dependency. A launcher may offer to install it automatically.
4. For a manual installation, place the mod and GeckoLib JAR files in that instance’s `mods` folder.
5. Launch the NeoForge profile and create a test world.

Do not leave multiple versions of Spectrobes Reforged in the same instance. Back up existing worlds before updating an alpha mod.

## Alpha access to essential items

The bundled recipes currently cover **six mineral combinations**. There is no bundled incubator or Prizmod crafting recipe yet. Obtain them from the Creative inventory, ask an administrator, or use commands with cheats/operator permission:

```text
/give @s spectrobesreforged:incubator
/give @s spectrobesreforged:white_prizmod
/give @s spectrobesreforged:komainu_fossil
```

The last command gives a known supported fossil for your first awakening. These are testing instructions, not a finished survival progression. A modpack may supply its own recipes.

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
