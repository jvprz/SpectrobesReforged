# Minerals and recipes

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
| Uranium | `uranium` | -16 | MAX | -16 |
| Titanium | `titanium` | -16 | -16 | MAX |
| Chrome | `chrome` | MAX | -16 | -16 |
| Sapphire | `sapphire` | +24 | +24 | +24 |
| Emerald | `emerald` | +36 | +36 | +36 |
| Ruby | `ruby` | +50 | +50 | +50 |
| Diamond | `diamond` | +0 | +0 | +0 |
| Gold | `gold` | -15 | -15 | -15 |
| Evolve | `evolve` | +0 | +0 | +0 |

<!-- END AUTO:MINERALS -->

## Special items and current limits

**Diamond** and **Evolve** are registered as minerals with zero stat deltas. Despite their descriptive flavor text, the current feeding implementation gives them no effect: they do not consume an item or evolve a Spectrobe. **Gold** reduces all three stats by 15, subject to the base-stat floor.

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

Minerals C/B/A/A+ grant **10/20/40/60% of a level**, alongside their existing stat effects. Other active stat minerals grant 25%; Diamond/Evolve remain inactive. Level cap: **100**. Filling the XP bar raises stats gradually within species limits. Minerals can still be useful for XP when their stat is capped. No item is consumed if neither XP nor stats change.

Children gain XP through feeding. Completed raids grant one victory and up to 60 training XP to living Adult/Evolved in each participant’s team, reduced after that player dies; Children gain no battle XP. These saved counters prepare future evolution without enabling it.

See [Prizmod](prizmod.md) for feeding from the collection.


[Environmental raw specimens](fossils.md#environmental-raws)
