# Troubleshooting

## The game will not start

Check Minecraft 1.21.1, the NeoForge version, Java 21, and the **NeoForge 1.21.1 build of GeckoLib**. Remove duplicate mod versions. Reproduce in a clean test instance before attributing an issue to a mod interaction.

## I cannot craft the incubator or Prizmod

The [equipment recipes](getting-started.md#craft-your-equipment) require 0.6.0-beta.1 or newer. If you still have 0.5.1-alpha, update the mod or use Creative/administrator access for that older build.

## The incubator has no power

It requires a redstone signal at either half, not an FE energy cable or fuel. Keep both halves intact and the area loaded.

## My fossil cannot awaken

Check that it belongs to [a supported species](spectrobes.md). Many fossil items exist without loaded species data. Missing-species failures should leave the fossil unconsumed.

## The companion is missing

1. Open the Prizmod and check the child slot.
2. Check that the entry is a Child and has HP remaining.
3. Wait briefly for recovery handling.
4. If necessary, move it to storage and back to refresh it.
5. For an entry at zero HP, ask an administrator about `/spectrobehealth`.

Do not use `/givespectrobe` as the first recovery step: it creates a new entry rather than recovering the existing one.

## The scanner does nothing

Check the **Scan** binding under Spectrobes Reforged; its default is **R**. Resolve any key conflicts, equip a healthy child companion, and stand near it. See [scan limits](scanning.md#range-and-targets).

## A mineral has no effect

You must own the living target and it must be linked to a collection entry. Its stats may already be capped, or a negative effect may already be at the base-stat floor. Diamond and Evolve currently have no stat effect. Reopen the Prizmod to refresh its displayed data if needed.

## Evolution says READY but nothing happens

That indicator only compares minerals fed with a configured threshold. The transformation interaction and destination species are not implemented yet.

## Krawl are not spawning naturally

Use the Creative spawn eggs for direct spawns. In 0.6.0-beta.1, [small vortex encounters](vortices.md) offer Survival discovery; contact with a pursuing vortex starts the encounter in non-Peaceful difficulty, without a held-item requirement. Spawn fields alone are not an ordinary natural-spawn system.

## Report an issue

Post in **#bug-reports** on [Discord](https://discord.gg/7QkScUWBe9) or open a [GitHub issue](https://github.com/jvprz/SpectrobesReforged/issues). Include:

- Mod, Minecraft, NeoForge and GeckoLib versions.
- Single-player or server; relevant other mods or datapacks.
- Exact steps, expected result, and actual result.
- Screenshots and relevant `latest.log` or crash-report excerpts.

Review logs before sharing: remove personal information, server addresses, private tokens and webhook URLs. Always keep a world backup before testing updates.
