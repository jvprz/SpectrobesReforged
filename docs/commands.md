# Admin commands

The mod registers two testing commands. Both require **permission level 2** and a player command source, so run them as an operator or in a cheats-enabled world rather than directly from the server console.

## Give a Spectrobe entry

```text
/givespectrobe <species> [color]
```

Creates a level-1 entry in **storage**, with base stats and full HP. It does not put it into the child slot automatically. The optional color is **0, 1, or 2**, defaulting to 0. Species suggestions come from loaded definitions.

```text
/givespectrobe komainu
/givespectrobe segu 2
```

Open the Prizmod and move the entry into the child slot to use it as a companion.

## Heal equipped entries

```text
/spectrobehealth
```

Restores the saved current HP of the **six team slots and the child slot** to their respective maximum HP. It does not heal every entry in storage. The command updates collection data and sends a snapshot; if a living entity still shows old health, return the entry to storage and re-equip it to recreate it from saved data.

## Vanilla item commands for testing

```text
/give @s spectrobesreforged:incubator
/give @s spectrobesreforged:white_prizmod
/give @s spectrobesreforged:komainu_fossil
```

These are vanilla `/give` examples, not additional mod commands. There is no completed evolution command in the registered command set.
