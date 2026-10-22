# Prizmod and companions

Hold any Prizmod variant and **use/right-click** it to open your collection. All color variants access the same player-owned data; they are not separate boxes.

## Collection areas

| Area | Purpose |
| --- | --- |
| Storage / box | Holds your collection entries; the data model has no fixed capacity |
| Child / baby slot | One Child Spectrobe used as the active following companion |
| Team | Six slots accepting Adult or Evolved entries |

Use the interface’s drag-and-drop controls to move entries between compatible areas. Child entries cannot be placed in team slots; Adult and Evolved entries cannot be placed in the child slot. Swaps must satisfy both slots’ stage rules.

## Bringing a companion out

Place a Child entry with HP remaining into the child slot. Changing this slot refreshes the following entity. The companion follows you and pauses when you trigger a scan. During an active [vortex encounter](vortices.md), the scanner companion stays safely stored until the enemies are defeated or the encounter is cancelled; its saved entry and health are retained.

An active recovery check looks for your companion approximately once per second. Dimension changes and respawn also have refresh handling. A Prizmod companion does not travel through a portal as an independent entity; the owner’s refresh handling manages its return.

This is recovery logic, not a guarantee against every beta issue. If it does not return, see [troubleshooting](troubleshooting.md).

## Reading an entry

Hover an entry to see species, stage, level, color variant, HP, attack, defense, stat bonuses and evolution mineral progress. Stat caps come from the species definition.

**“READY” means a mineral threshold was reached. It does not currently provide an evolution action.** The bundled roster contains Child species only; the complete growth, adult-team deployment and battle loop is still in development.

## Health and ownership

Companion damage is reflected in the saved entry. Entries with no HP remaining are not spawned by the companion manager. The admin [healing command](commands.md) can restore equipped entries during testing.

Minerals are applied by using them on a living Spectrobe that belongs to you and is linked to your Prizmod entry. A separately summoned creature is not necessarily a collection-linked companion.

## Persistence

Collection entries, team slots and the child slot are serialized as player data. Back up the entire world before updating; replacing a JAR is not a substitute for a world backup.

Next: [use the scanner](scanning.md) or [improve stats with minerals](minerals.md).
