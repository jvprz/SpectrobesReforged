# Advancements

> **Added in 0.6.0-beta.1:** the translated advancement tree for the currently playable systems.

Open Minecraft’s Advancements screen to find the Spectrobes Reforged tab after obtaining a fossil, Prizmod, mineral or incubator.

| Advancement | Requirement |
| --- | --- |
| Spectrobes Reforged | Obtain an item from the mod’s progression item groups |
| Buried History | Obtain any fossil |
| An Explorer’s Prizmod | Obtain any Prizmod color |
| Ready to Awaken | Obtain an incubator |
| A New Companion | Complete a successful incubator awakening |
| Hidden Signals | Trigger a scan with an active companion |
| Mineral Diet | Feed a mineral that changes a linked companion’s stats |
| A Splash of Color | Successfully recolor a collection-linked companion with Chroma |
| Complete a Vortex | Defeat all Krawl and complete the raid as a registered participant |
| Six New Beginnings | Awaken Inkana, Komainu, Segu, Shakin, Spiko and Vilar in the incubator |

Obtaining an item in Creative can satisfy item-based advancements. Giving yourself a collection entry through `/givespectrobe` does not count as awakening it. Ineffective minerals and unchanged Chroma colors do not grant their action advancements.

The collection challenge remembers each species awakened; repeated awakenings of the same species do not replace another requirement. It tracks six successful species awakenings, not six simultaneous world entities. There are no XP or item rewards in this first tree.

This beta includes [small vortex encounters](vortices.md), which allow Krawl battles in Survival. Spawn eggs remain useful for reproducible tests. No advancement requires unfinished evolution or team combat.

For old worlds, action advancements are not reconstructed from past activity. Perform the action again after installing the update. Item-based advancements are evaluated when your inventory changes.

The old single-Krawl kill milestone has been removed. A spawn-egg kill alone does not award Complete a Vortex. Previous completed vortex advancements retain their internal ID and remain completed.


## 0.6.2-beta.4

| Advancement | Requirement |
| --- | --- |
| First excavation | Manually extract a fossil or mineral. |
| A perfect discovery | Earn a crown: 100% exposed, no damage, within 60 seconds. |
| Careful restoration | Repair a damaged find with the recovery tool. |
| Under the ice | Manually extract a frozen find. |
| Every terrain | Manually extract finds in all six excavation environments. |
| Experienced excavator | Reach excavation skill level 5. |
| Ten discoveries | Complete 10 manual excavations. |
| A hundred discoveries | Complete 100 manual excavations. |
| A crowned companion | Awaken a crowned fossil. |
| Level 10 | Train a Spectrobe to level 10. |
| Level 25 | Train a Spectrobe to level 25. |
| Level 50 | Train a Spectrobe to level 50. |
| Level 100 | Train a Spectrobe to level 100. |

Manual excavation totals start counting with this update. Automatic Giga/Blast extraction does not count. Older crowned entries regain their crown achievement when opening Prizmod.


## Shared excavation credit

Everyone who successfully used a tool on the specimen and is alive, online and within 8 blocks when it is extracted receives the existing manual excavation, perfect finding and terrain milestones where applicable. Each eligible worker gets one discovery, its catalogue entry and the normal excavation skill XP; counters for 10/100 discoveries and skill level 5 use personal progress. The extractor also receives credit. Merely watching does not count. Repair milestones belong to the player using Recovery; awakening and training milestones remain personal. Automatic extraction gives no manual excavation credit. There is still only one output item and no new cooperative advancement.


Raw specimens of every material unlock the same existing obtaining milestones: fossil raws count for the first fossil, and all raw minerals/fossils unlock the mod tree. Cleaning is credited only after a successful manual extraction. The 28 creative material/content combinations do not require separate advancements. For All Terrains, sand and red sand share one criterion; the six required environments remain rock, sand, ice, lava, water and mud. Existing advancement IDs and earned progress are preserved; eligible collaborators still share excavation credit.
