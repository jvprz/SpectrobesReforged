# Getting started

## Requirements

| Component | Current requirement |
| --- | --- |
| Minecraft | 1.21.1 |
| Loader | NeoForge 21.1.219 or newer for Minecraft 1.21.1 |
| GeckoLib | 4.8.3 or newer for NeoForge 1.21.1 |
| Java | 21 |
| Curios API (optional extra slot) | 9.5.1+1.21.1 for NeoForge |

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

Use a crafting table. Expand the ingredient list to see the required quantities.

### White Prizmod

{{PRIZMOD_RECIPE}}


Produces one white Prizmod. Combine any Prizmod with one dye in a shapeless recipe to recolor it. All 16 colors are supported, including returning to white. Your collection belongs to the player, so recoloring does not erase it.

### Incubator

{{INCUBATOR_RECIPE}}


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

Dedicated-server development checks cover startup, definitions, configuration, entity ticking and per-player access. See [settings and servers](settings.md) for setup and the remaining live multiplayer check.

## Excavation playtest {#excavation}

**0.6.2-beta.3**

Insert a raw fossil or a raw C/B/A/A+ mineral. Click three times to open the surface, then remove the environment cover with the matching tool. Hold and drag to excavate, clean thin sediment and debris with the blower, and extract from **90%**. **R** toggles the scanner; The ten Beyond the Portals tools are shown as icons around the central excavation area. **I** switches between excavation and inventory. Repair, Giga and Blast act once per click; Blast needs a second confirmation click. Hover a tool to read its purpose.

Manual recovery grants a separate excavation skill and records the exact type in your catalogue. Giga requires a previously recovered type and gives no skill XP or crown. Skill persists on death. Four repairs are shared by that specimen even if you move it to another table. Time, cover, layers, stress, heat, gas and progress follow the raw item. A perfect fossil crown needs **100%, no damage, within 60 active seconds**; it is preserved on awakening but does not yet enable adult evolution.

{{EXCAVATION_RECIPE}}



| Tool | Effect |
| --- | --- |
| {{TOOL_DRILL}} | Balanced rock removal. Keep moving; release or blow to reduce stress. |
| {{TOOL_BLOWER}} | Safely clears sand, the last thin layer, debris and gas. Also cools the site. |
| {{TOOL_RECOVERY}} | Repairs 10 integrity, up to four uses per specimen. Cannot restore a lost perfect crown. |
| {{TOOL_PITON}} | Breaks two rock layers over a wide area. Avoid exposed objects. |
| {{TOOL_FIRE}} | Melts the ice cover. Watch heat and cool with the blower. |
| {{TOOL_WATER}} | Removes the lava cover and cools the working area. |
| {{TOOL_VACUUM}} | Removes water and mud cover, cloudy debris and gas. |
| {{TOOL_BLOWER_S}} | Clears dust, gas, thin sediment and sand across the whole working surface. |
| {{TOOL_GIGA}} | Automatically uncovers a type you have already excavated manually. No skill XP or crown. |
| {{TOOL_BLAST}} | 50% chance of instant excavation, 50% chance of destruction. No skill XP or crown. Click twice to confirm. |

Stone, dirt and environmental covers use Minecraft’s block textures. Pale loose dust is visually distinct and the cursor hint names the material and suitable tool. Breaking a fossil or mineral consumes the specimen, clears its image and shows fragments with a loss notice. An unfinished excavation remains saved when closed; a failed outcome also remains clear when reopening the table.

### Geological pockets — 0.6.2-beta.6

Environmental deposits now create a small buried pocket with one central ore, rather than requiring an existing host block. Ice pockets (Flash) occur only in snowy/frozen biomes; sandstone (Corona) only in desert; red sandstone (Corona) only in badlands, wooded badlands and eroded badlands; mud (Aurora) only in swamp and mangrove swamp. All four families also include C/B/A/A+ minerals. Pockets are 5–7 blocks wide and 3–5 high, with their centre 2–6 blocks below the surface heightmap. They replace selected natural ground blocks, never air, fluids, other ores or logs. The central shell must fit in solid permitted ground and stay in its allowed biome family.

| ID | Fósil / Fossil | C / B / A / A+ |
| --- | --- | --- |
| `frozen` | `frozen_flash_fossil_ore` | `frozen_{c,b,a,a_plus}_mineral_ore` |
| `sandy` | `sandy_corona_fossil_ore` | `sandy_{c,b,a,a_plus}_mineral_ore` |
| `red_sandy` | `red_sandy_corona_fossil_ore` | `red_sandy_{c,b,a,a_plus}_mineral_ore` |
| `muddy` | `muddy_aurora_fossil_ore` | `muddy_{c,b,a,a_plus}_mineral_ore` |

Fossils: 2 attempts per chunk. Mineral attempts: C 1/2, B 1/4, A 1/12, A+ 1/32 per chunk. Attempts can fail on unsuitable ground. Only newly generated chunks are affected. Existing ordinary temperature-based underground ores remain. Raw pieces keep their geology, including red sand/red sandstone in the excavation table; existing excavations retain their saved layers.


The ten excavation tools also exist as separate items in the Creative tools tab (`excavation_drill`, `excavation_blower`, etc.). Their crafting recipes and use as table ingredients are planned; the table currently supplies its tools without needing inventory items.


{{ITEM_CREDITS}}


## Settings

Open **Mods → Spectrobes Reforged → Config** for personal preferences. Rebind **R** (scan) and **G** (open Prizmod) under **Options → Controls → Key Binds**. G works with an equipped Prizmod or any Prizmod carried in your inventory or offhand; it does not grant free access without the item. Shortcuts do not fire while typing in chat or using another screen.

[Settings and servers](settings.md).


**Optional integration:** [Curios API 9.5.1+1.21.1 for NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) adds the extra equipment slot. **Without Curios**, carry a Prizmod in your inventory or offhand and open it with **G** or right-click, as before. For the extra slot in multiplayer, install Curios on both server and clients. Unequip the item into normal inventory before uninstalling Curios.

Open your inventory and use the **Curios equipment button**. Place any of the sixteen Prizmod colors in the **Prizmod** slot, then close the inventory and press **G**. It uses no normal inventory or armor slot. Carrying one in your inventory/offhand and right-clicking still work.


## Cooperative excavation {#cooperative-excavation}

Open the same excavation table together: up to **16 operators** share one specimen, layers, dust, heat, integrity and result. Colored cursors show where others point, with an icon while they work. A tool is reserved while a player uses it; other players may use different tools at the same time. Releasing, switching tools, closing or disconnecting frees the reservation; an interrupted reservation expires after about half a second.

There is **one output**, consumed/extracted once. Every eligible worker receives excavation skill XP and the catalogue entry once. Closing the interface preserves specimen progress. The server validates tool ownership, distance and actions. [Raid rules and rewards](vortices.md#encounter-rules).


Two different players must successfully use tools on the same specimen to earn the cyan ⚒ cooperative keepsake. Merely opening the table does not count. It persists on the clean fossil or mineral and on the awakened Spectrobe, alongside a possible crown. It changes no stats, XP or rewards.


[Shared excavation credit](advancements.md): Everyone who successfully used a tool on the specimen and is alive, online and within 8 blocks when it is extracted receives the existing manual excavation, perfect finding and terrain milestones where applicable. Each eligible worker gets one discovery, its catalogue entry and the normal excavation skill XP; counters for 10/100 discoveries and skill level 5 use personal progress. The extractor also receives credit. Merely watching does not count. Repair milestones belong to the player using Recovery; awakening and training milestones remain personal. Automatic extraction gives no manual excavation credit. There is still only one output item and no new cooperative advancement.


The updated table faces the placing player. A clean output item has priority on its recessed surface. A raw piece can wait in the input, but cannot be worked on until the clean output is collected; an empty table shows nothing.


The icons are named **Perfect medal** (♛) and **Cooperative medal** (⚒). Item tooltips show only these names; this guide contains their requirements.


## Handheld blower

Hold Use/right-click with the blower item to push mobs in a forward cone up to five blocks. Solid walls block the airflow. It causes no direct damage and does not push players. It has 256 durability, consuming one point per burst every five ticks; Creative mode does not consume durability. The excavation minigame blower remains independent. Obtain the item from the Creative tools tab or `/give @s spectrobesreforged:excavation_blower`; no new crafting recipe is added.
