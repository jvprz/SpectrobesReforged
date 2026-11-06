# Minerals and recipes

Mineral ores now identify the elemental family, not the grade: Coronium, Aurorium and Flashium have stone and deepslate variants. Blue ice contains Flashium, sandstone and red sandstone contain Coronium, and mud contains Aurorium. Stone/deepslate veins follow the same biome-temperature rules as vortices. Existing grade frequencies and depths are preserved; grade is saved internally and retained with Silk Touch. New generation applies to unexplored chunks; legacy blocks remain compatible.

Minerals modify a linked companion’s HP, attack and defense. Hold a mineral and use it on **your own living Prizmod companion**.

## Feeding rules

- Stat changes are clamped between the species’ base and maximum values.
- A trade-off mineral can lower one stat while raising another, but not below the species base.
- A feeding that changes stats or XP increments the saved mineral counter.
- In Survival, one item is consumed after a successful change. Creative does not consume it.
- If neither stats nor XP change, the mineral is not consumed and the counter does not advance.
- HP minerals adjust current HP by the change in maximum HP; they are not a separate full-heal system.

The table lists the configured changes **before clamping**. `MAX` sets that stat to the species cap.

<!-- BEGIN AUTO:MINERALS -->

| Mineral | Item ID | HP | Attack | Defense |
| --- | --- | --- | --- | --- |
| Power C | `power_c` | +0 | +8 | +0 |
| Power B | `power_b` | +0 | +16 | +0 |
| Defense C | `defense_c` | +0 | +0 | +8 |
| Defense B | `defense_b` | +0 | +0 | +16 |
| Health C | `health_c` | +8 | +0 | +0 |
| Health B | `health_b` | +16 | +0 | +0 |
| Power A | `power_a` | +0 | +24 | +0 |
| Power A+ | `power_a_plus` | +0 | +32 | +0 |
| Defense A | `defense_a` | +0 | +0 | +24 |
| Defense A+ | `defense_a_plus` | +0 | +0 | +32 |
| Health A | `health_a` | +24 | +0 | +0 |
| Health A+ | `health_a_plus` | +32 | +0 | +0 |
| Agate | `agate` | +0 | +8 | +8 |
| Lazuli | `lazuli` | +0 | +16 | +16 |
| Jade | `jade` | +8 | +8 | +0 |
| Amber | `amber` | +16 | +16 | +0 |
| Fluorite | `fluorite` | +8 | +0 | +8 |
| Coral | `coral` | +16 | +0 | +16 |
| Graphite | `graphite` | +0 | +30 | -8 |
| Garnet | `garnet` | +0 | -8 | +30 |
| Onyx | `onyx` | +0 | +40 | -16 |
| Topaz | `topaz` | +0 | -16 | +40 |
| Zircon | `zircon` | -8 | +30 | +0 |
| Opal | `opal` | +30 | -8 | +0 |
| Cobalt | `cobalt` | -16 | +40 | +0 |
| Citrine | `citrine` | +40 | -16 | +0 |
| Sylvite | `sylvite` | -8 | +0 | +30 |
| Spinal | `spinal` | +30 | +0 | -8 |
| Azurite | `azurite` | -16 | +0 | +40 |
| Quartz | `quartz` | +40 | +0 | -16 |
| Platinum | `platinum` | +5 | +5 | +5 |
| Pearl | `pearl` | +12 | +12 | +12 |
| Sapphire | `sapphire` | +24 | +24 | +24 |
| Emerald | `emerald` | +36 | +36 | +36 |
| Ruby | `ruby` | +50 | +50 | +50 |
| Diamond | `diamond` | +0 | +0 | +0 |
| Gold | `gold` | -15 | -15 | -15 |

<!-- END AUTO:MINERALS -->

## Special items and current limits

Diamond remains inactive. Legacy Evolve now acts as Evolve M: it fulfills the configured minerals-fed requirement without evolving the Spectrobe. Gold retains its existing stat reductions.

The tooltip’s evolution progress is preparatory data. See [evolution status](spectrobes.md#evolution-data-not-yet-playable).

## Chroma

Use Chroma on a Spectrobe you own:

| Item ID | Result |
| --- | --- |
| `chroma_0` | Restore color variant 0 |
| `chroma_1` | Apply color variant 1 |
| `chroma_2` | Apply color variant 2 |

All three currently display as **Chroma**; their tooltips identify the effect. A successful change consumes one outside Creative. If the variant already matches, it is not consumed. When the entity is linked to your collection, a successful color change also increments its mineral counter.

## Ore generation

Use an iron pickaxe or better. Mineral features are added to the Overworld biome tag.

<!-- BEGIN AUTO:MINERAL_ORES -->

| Tier | Y range | Non-Silk-Touch drops |
| --- | --- | --- |
| C | -64 to -16 | Power C, Defense C, Health C |
| B | -16 to 32 | Power B, Defense B, Health B |
| A | 32 to 80 | Power A, Defense A, Health A |
| A+ | 64 to 160 | Power A+, Defense A+, Health A+ |

<!-- END AUTO:MINERAL_ORES -->

The default biome modifier adds the four named mineral features above. Deepslate mineral blocks are registered, but no separate deepslate mineral placed features are bundled. Silk Touch gives the ore block; otherwise the loot table chooses an item from the tier’s drop pool.

## Crafting recipes

The following are the **six mineral-combination recipes**, all shapeless and producing one item:

<!-- BEGIN AUTO:RECIPES -->

| Result | Ingredients |
| --- | --- |
| Agate | Power C + Defense C |
| Amber | Power B + Health B |
| Coral | Defense B + Health B |
| Fluorite | Defense C + Health C |
| Jade | Power C + Health C |
| Lazuli | Power B + Defense B |

<!-- END AUTO:RECIPES -->

Chroma still has no bundled crafting recipe. This beta includes [incubator and Prizmod recipes](getting-started.md#craft-your-equipment), including Prizmod dyeing. Registered items without a recipe or documented drop route may need Creative or administrator access in this beta.


## Levels and XP

Excavation integrity gives **+1% at 70–84, +2% at 85–94, +3% at 95–100**, rounded down per base stat. A perfect excavation adds a permanent gold crown after the fossil and awakened Spectrobe name. Quality is saved separately from the crown; old entries without quality retain their existing stats.

Elemental minerals C/B/A/A+/S provide **10/20/40/60/100% base level progress** with matching affinity, half otherwise, before the level-efficiency curve. Power/Defense/Health now train stats only and no longer grant XP. Other existing mixed/stat minerals retain their existing behavior.

Mineral XP efficiency decreases smoothly between these balance points: **level 1 → 100%, 25 → 80%, 50 → 60%, 75 → 45%, 100 → 35%** of the base percentage. For example, a base 10% mineral grants about 6% at level 50 (rounded up to whole XP). If it crosses a level, the remaining portion uses the new level’s efficiency. Only mineral XP is reduced: stat effects and the one-mineral feeding counter are unchanged. Combat XP is unchanged. Level 100 remains the cap and grants no further XP.

Children gain XP through feeding. Completed raids grant one victory and up to 60 training XP to living Adult/Evolved in each participant’s team, reduced after that player dies; Children gain no battle XP. These saved counters prepare future evolution without enabling it.

See [Prizmod](prizmod.md) for feeding from the collection.


[Environmental raw specimens](fossils.md#environmental-raws)

## Minerals and discoveries — experimental playtest 14

Coronium is fixed when mining in hot biomes, Aurorium in temperate biomes and Flashium in cold biomes, using the same climate rules as vortices. The raw item saves its origin; moving it to another biome does not change its result. C/B/A/A+ retain their existing ore heights and excavation layers; 1% of newly mined A+ specimens become S. Started legacy excavation boards keep their old result.

Power/Defense/Health come from completed vortices, not fresh excavations. Existing C/B quantities and death penalties remain; a death-free raid can additionally grant A/A+ food. With n participants (1–16), A+ chance is (5+n)/1000 and A chance is (30+3n)/1000. Separate death-free chances: 10% for one random seed and 1% for Evolve M. Extras are granted only with the final personal reward and are never rerolled by re-entry.

Seeds: ATK +1, DEF +1, HP +10; shared lifetime allowance of one seed per level, maximum 100, with species stat caps. Seeds give no XP and unused allowances accumulate. Evolve M satisfies minerals fed; Evolve L reaches the configured level; Evolve B satisfies battles. Missing or already-satisfied requirements consume nothing. Current Child definitions only have mineral requirements; L/B remain ready for future definitions. None initiates evolution.

Authored sprites are applied to seeds, Evolve M/L/B, Metalium, Titanium, Marble and all 20 cubes. Mystery Stones and the two relic items still use provisional icons. Collectibles and materials remain prepared content without new uses or drops.

{{DISCOVERY_ITEMS}}

Titanium is now a material, not food. The separate Titanium Plate duplicate was removed. Old saved Uranium becomes Aurorium S, Chrome becomes Flashium S, Evolve becomes Evolve M and Titanium Plate becomes Titanium through registry aliases, preserving stack counts.
