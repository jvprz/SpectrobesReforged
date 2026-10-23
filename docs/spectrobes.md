# Spectrobe roster

These are the **six species with bundled definitions** in <!-- VERSION -->0.6.1-beta<!-- /VERSION -->. All start as Child entries. Numbers below come directly from the species JSON files when the wiki is built.

## Stats and scan radius

Each stat cell shows **base → cap**. The scan column shows the actual horizontal radius used by the current scanner.

<!-- BEGIN AUTO:SPECIES -->

| Species | Element | HP | Attack | Defense | Scan radius |
| --- | --- | --- | --- | --- | --- |
| Inkana | Corona | 40 → 75 | 70 → 125 | 95 → 175 | 6 blocks |
| Komainu | Corona | 120 → 200 | 65 → 125 | 55 → 105 | 6 blocks |
| Segu | Aurora | 50 → 50 | 50 → 100 | 90 → 160 | 8 blocks |
| Shakin | Flash | 72 → 125 | 65 → 145 | 40 → 115 | 6 blocks |
| Spiko | Aurora | 65 → 140 | 75 → 175 | 30 → 95 | 6 blocks |
| Vilar | Corona | 70 → 125 | 60 → 115 | 100 → 180 | 8 blocks |

<!-- END AUTO:SPECIES -->

Minerals can improve stats up to the listed caps. For example, Segu’s HP base and cap are both 50, so an HP-only mineral cannot increase that stat.

## Evolution data: not yet playable

<!-- BEGIN AUTO:EVOLUTIONS -->

| Child | Configured target | Mineral threshold | Target definition |
| --- | --- | --- | --- |
| Inkana | Inkanapa | 4 | Not bundled |
| Komainu | Komanoto | 5 | Not bundled |
| Segu | Segulos | 3 | Not bundled |
| Shakin | Shakor | 4 | Not bundled |
| Spiko | Spikan | 3 | Not bundled |
| Vilar | Vilamasta | 7 | Not bundled |

<!-- END AUTO:EVOLUTIONS -->

These targets and thresholds are data for a developing system. The destination species do not have bundled definitions, and feeding minerals does not currently trigger a transformation. A “READY” tooltip is not evidence that evolution is available.

## Other fossils

More fossil items exist than awakenable species. Collecting a fossil is not a promise that its species is ready. See the [fossil drop pools](fossils.md#mining-and-drops) for the distinction.
