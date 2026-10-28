# Small Krawl vortices

**{{VERSION}}**

These are server defaults; see [settings](settings.md) for configurable values.

A black and violet vortex detects living, non-spectator players within **64 blocks** and follows them at **5 blocks/second** on clear, level ground. Walking is no longer enough to escape; sprinting can create distance. Contact starts a raid of **three identical Krawl per joining player**, regardless of your held item. An action-bar warning names its element when pursuit begins.

## Natural discovery {#discovery-in-survival}

In the Overworld, living non-Creative, non-spectator players exposed to the sky get a check every **20 seconds**, with a **50% chance to attempt** discovery. Each attempt tests up to **six positions, 32–48 blocks away**. This is not a guaranteed spawn every 20 seconds: terrain, other players and existing vortices can prevent it. `doMobSpawning` must be enabled and difficulty must be Easy, Normal or Hard.

The position must be loaded, dry, open for three blocks above suitable natural ground and inside the world border. Dirt, sand, stone, gravel, terracotta, snow, ice, mud and sandstone are supported through the `vortex_ground` block tag. No player may be within 24 blocks of the site; no vortex may be within 96 blocks of the discovering player. No chunks are force-loaded. Players underground do not initiate discovery.

After a successful discovery, and again after finishing or cancelling your encounter while online, natural discovery pauses for **60 seconds** for that player. An active encounter also blocks their discovery checks. Nearby-vortex limits still apply to other players. Idle vortices expire after 20 minutes of loaded ticking.

## Krawl by biome {#krawl-by-biome}

The **biome at the vortex’s origin** fixes its wave, including summoned vortices. Crossing a biome boundary later does not reroll the Krawl. The selected type is saved with the vortex.

| Climate | Enemies | Examples |
| --- | --- | --- |
| Hot | Blovant · Corona | Desert, savanna, badlands and tagged jungles |
| Temperate | Viblova · Aurora (green) | Plains, forests; untagged swamps and mangroves use temperature |
| Cold | Blova · Flash (blue) | Snowy biomes, frozen peaks and tagged taiga |

The existing biome tags override base temperature, with priority **hot → temperate → cold** if a datapack overlaps them. Without a matching tag: **temperature ≥ 1.0 → Corona; < 0.2 → Flash; otherwise Aurora**. This includes modded biomes. Base biome temperature is used, not weather, time of day or height-adjusted snow checks. These are the explicit tag lists; the fallback also covers biomes not listed:

{{BIOMES}}

## Pursuit and terrain

Detection reaches 64 blocks; an acquired target is retained to **80 blocks** if there is no nearer target inside detection range. Speed is 0.25 blocks/tick, about 5 blocks/second at 20 TPS. One-block ledges, slabs, stairs and gravel surfaces are supported. Tall walls, water, narrow passages and cliffs can still block movement. Airborne vortices descend to the ground. Server lag slows real-time movement; `randomTickSpeed` does not accelerate discovery.

## Cooperative raids {#encounter-rules}

A vanilla-style bar shows the remaining Krawl and participant count. Living Survival/Adventure players join within **32 blocks**: **three Krawl per player**, up to **16 players / 48 enemies**. Each UUID joins only once, including after leaving, returning or reloading. A player cannot join two active raids. If three safe spawn positions are unavailable, joining waits. The origin biome determines the same Krawl type for every wave. Companions stay hidden for all participants.

A solo death immediately cancels the raid and removes its enemies without loot. In multiplayer, the raid continues while an ally survives. Returning alive restores participation, but preserves your death penalty. If all registered participants are fallen at the same time, the raid ends without rewards. Peaceful, ten loaded minutes, or twenty loaded seconds with no living participant within 32 blocks also cancel it.

Defeat **every enemy from every joining wave**. Rewards are personal, paid once at completion; raid Krawl drop no separate loot or XP. Offline or dead participants receive their pending reward when next alive and online. Leaving the area does not create an extra reward, and registered participants still receive their share after a successful completion.

| Registered players | C minerals each | B minerals each | Player XP each |
| --- | --- | --- | --- |
| 1 | 2 | 0 | 15 |
| 2 | 3 | 0 | 20 |
| 3 | 3 | 1 | 25 |
| 4–16 | 3 | 1 | 30 |

Minerals are random Power, Defense or Health. A death removes B minerals, subtracts one C mineral per death up to two (minimum one), and divides XP by deaths + 1, rounded down to multiples of five, minimum five. Penalties cap at three deaths. Eligible Adult/Evolved training receives 60 XP without deaths, otherwise 60 / (deaths + 1), minimum ten. Inventory overflow drops are reserved for their recipient. Vortices do not destroy blocks; fight with Minecraft weapons. Child evolution remains unavailable.

## Quick test {#quick-test}

```text
/summon spectrobesreforged:krawl_vortex ~10 ~ ~
```

Use an open surface in Easy, Normal or Hard, summon the vortex below, then switch to Survival. Test walking versus sprinting, a ledge, gravel, biome borders, saving/reloading and killing the three Krawl. Natural encounters work in existing chunks; world regeneration is unnecessary.


An idle vortex blocks arrows with the vanilla shield-style rebound and sound, including Piercing arrows. A shot neither destroys it nor starts a raid. The invisible controller stops intercepting projectiles after the encounter begins.
