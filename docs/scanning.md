# Companion scanner

Your active child companion can search for buried fossil and mineral ores.

## Start a scan

1. Equip a Child Spectrobe in the Prizmod’s child slot and wait for it to appear.
2. Stand near your companion.
3. Press **R** by default, or your rebound **Scan** key.
4. Watch the circular sweep, listen for discovery sounds, and look for colored particle hints.

The companion pauses for **66 ticks**, about **3.3 seconds at normal tick rate**. The ring opens over its first 6 ticks. A scan is temporary; pressing the key triggers the range-pause behavior again.

## Range and targets

The current horizontal radius is **twice the species’ configured range value**, centered on the companion, not the player. The scan searches downward from its block position by up to **64 blocks**.

| Companions | Horizontal radius |
| --- | --- |
| Inkana, Komainu, Shakin, Spiko | 6 blocks |
| Segu, Vilar | 8 blocks |

- **Orange hints:** fossil ore.
- **Light blue hints:** mineral ore.
- The result list is limited to **12 ore positions**, not every deposit in the area.
- Only loaded world data is searched.

Search work is spread across ticks. Results are not necessarily immediate. Discovery hints appear at a suitable air position above a detected ore; terrain and caves can affect where you see them. The scanner does not mine blocks for you.

## No result?

Check the key binding, child slot and companion HP first. Move closer to the terrain you want to scan and try again. A deposit can be outside the horizontal radius, deeper than the search depth, or outside currently available client world data.
