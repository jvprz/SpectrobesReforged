# Small Krawl vortices

> **Added in 0.6.0-beta.1:** small pursuing vortex encounters. Larger encounters remain in development.

> **Updated in 0.6.1-beta:** animated block model, dense black and violet gas, terrain dust, grounding improvements, biome-dependent enemies and 15 XP per completed encounter.

A black and violet funnel slowly pursues the nearest living, non-spectator player within 24 blocks. **Touching the vortex starts the encounter regardless of what you hold.** It dissolves and produces **three Krawl of the same type around the caught player**. An invisible controller tracks the encounter until it finishes. A 12-cube animated funnel is wrapped in dense inner black gas and outer violet gas. Its idle and pursuit animations rotate and float, with a contraction animation on contact. Occasional black/purple dust, portal particles and smoke use the Krawl death effect. Moving vortices kick up particles matching the block beneath them. Vortices spawned in mid-air descend to the ground even without a nearby target. Particle density is reduced at distance and respects Minecraft particle settings. Terrain-following checks collision surfaces so it can lift over one-block ledges, slabs and stairs on otherwise clear ground; network positions are interpolated on the client. It cannot cross tall walls or squeeze through gaps narrower than its body. Your scanner companion returns to the Prizmod while your encounter has living Krawl and normally reappears within about a second after the last enemy dies or the encounter is cancelled. Its saved HP, stats and child slot are preserved; an entry at zero HP still cannot spawn.

## Quick test

1. Launch the updated development client or install the newly built JAR in a test instance.
2. Use Easy, Normal or Hard difficulty. Find open, fairly level **surface** ground.
3. With cheats/operator permission, place a vortex on the ground using:

```text
/summon spectrobesreforged:krawl_vortex ~3 ~ ~
```

4. Equip normal Minecraft armor and weapons. Your child scanner companion is automatically stored when the encounter starts.
5. Stay still and let the funnel reach you, or walk into it. Exactly three Krawl of the same type should appear around you. The funnel shrinks and disappears over about 0.8 seconds. You do not need a Prizmod or any held item.
6. Defeat the three enemies and remain within 32 blocks. A victory message appears, two random tier-C minerals drop onto the ground in front of you, 15 experience points appear as XP orbs beside you, and **Complete a Vortex** is granted.

For a more representative combat test, switch to Survival after placing it. Creative can start encounters but enemies may not target Creative players normally.

## Discovery in Survival

In the Overworld, a living Survival player gets a discovery check once per minute with a one-in-four attempt chance. The attempt places a vortex 40-64 blocks away only if the location is loaded, exposed to the sky, dry, clear and on suitable natural ground. It avoids players within 32 blocks and existing vortices within 128 blocks of the discovering player. Failed checks do not force-load terrain or guarantee a later spawn.

These are runtime encounters rather than structures generated into new chunks. Idle vortices expire after 20 minutes of loaded ticking. Do not wait for a random discovery when testing; use the command above.

## Krawl by biome

The biome where the player is caught selects all three enemies in the wave:

| Biome category | Krawl | Element |
| --- | --- | --- |
| Hot (deserts, savannas, badlands and jungles) | Blovant | Corona |
| Temperate (plains, forests and meadows in the mod's temperate biome tag) | Viblova (green) | Aurora |
| All other biomes | Blova (blue) | Flash |

This uses the existing `spectrobesreforged:hot` and `spectrobesreforged:temperate` biome tags. If a datapack places a biome in both tags, hot takes priority. Untagged biomes use blue Blova. The selection is made once when the encounter starts; crossing a biome boundary during combat does not change the enemies.

## Encounter rules

- The player caught by the vortex owns the encounter and receives its reward. Others can help defeat enemies; rewards are not duplicated per participant.
- The owner must stay alive, in the same dimension, and within 32 blocks. Being absent for 20 loaded seconds cancels the encounter.
- Encounters have a maximum of 10 minutes of loaded ticking.
- Peaceful difficulty prevents activation and cancels active encounters.
- Cancellation removes the encounter’s loaded enemies without a victory reward. Unloaded enemies are cleaned when they tick again. Enemies also cancel an orphaned encounter if their vortex is no longer loaded after a short grace period.
- Only confirmed enemy deaths count. An unloaded, discarded or missing enemy is not automatically credited as defeated.
- The remaining enemies, owner and terminal state are saved. Normal save/reload does not respawn a wave or repeat a reward. Unloaded encounters can pause until loaded again; no permanent chunk ticket is used.
- The vortex does not place, destroy or corrupt blocks. Combat occurs in the existing world, without teleporting to an arena.

The first reward is **two total items**, each independently chosen from Power C, Defense C and Health C. Duplicate mineral types are possible. Completing the encounter also awards **15 experience points** as XP orbs near the owner, once per victory. The reward always drops in front of the owner as item entities; it is not inserted directly into the inventory.

## Useful checks

- Stand at the catch position after activation: a second wave must not appear.
- Defeat one enemy, save, reload and finish: only two should remain.
- Walk away or die: cancellation must not grant minerals or victory XP.
- Return to a completed encounter: no second reward.
- Try Peaceful: the vortex must not pursue or create enemies.
- Test a one-block ledge, slabs and stairs: the funnel should lift and continue without snapping between network updates.
- Listen for the distinct opening cue and victory chime; both have localized subtitles. The victory sound plays at the owner's current position.
- Try empty hands and different held items: contact must still trigger the encounter.
- Equip a child companion before contact: it must hide during combat, resist manual re-equipping during the wave, and return after victory or cancellation with its saved stats.
- Try a cliff, water or a cramped area: unsafe enemy placement must not start a partial wave.

Evolution and Spectrobe-controlled battles are not added by this encounter. Fight using Minecraft weapons for now.
