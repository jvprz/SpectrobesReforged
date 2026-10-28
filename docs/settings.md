# Settings and servers

**{{VERSION}}**

Open **Mods → Spectrobes Reforged → Config** for personal preferences. Rebind **R** (scan) and **G** (open Prizmod) under **Options → Controls → Key Binds**. G works with an equipped Prizmod or any Prizmod carried in your inventory or offhand; it does not grant free access without the item. Shortcuts do not fire while typing in chat or using another screen.

## Personal preferences

| Setting | Default | Effect |
| --- | --- | --- |
| Vortex particles | High | Changes vortex particle density only. Minecraft particle settings still apply. |
| Effect volume (%) | 100% | Scales mod effects, including scanner, awakening cues, Krawl and battle sounds. Minecraft category volumes still apply. |
| Scanner feedback | Visual and sound | Choose visual signals, sound, or both. Does not change detection radius or server gameplay. |
| Excavation explanations | On | Show extended tool explanations. Names, repair counts, warnings and excavation status remain visible. |
| Reduced interface motion | Off | Reduces chamber decoration, moving excavation debris and Prizmod preview motion. Progress and warnings remain visible. |

## World and server settings

These defaults match the vortex guide. The host/server controls gameplay values for everyone. A client preference cannot override them. In singleplayer the world is its own server. Natural discovery still requires suitable terrain, non-Peaceful difficulty and `doMobSpawning`.

| Setting | Default | Effect |
| --- | --- | --- |
| Natural vortices | On | Controls natural discovery for this world. Command-summoned vortices still work. |
| Discovery interval (seconds) | 20 | Game-time seconds between eligible checks at 20 TPS. A check is not a guaranteed spawn. |
| Attempt chance (0–1) | 0.5 (50%) | 0 disables successful natural attempts; 0.5 means 50%; 1 means every eligible check attempts a spawn. |
| Personal cooldown (seconds) | 60 | Pause after successful discovery and a finished/cancelled encounter. Existing nearby vortices remain. |
| Vortex speed (blocks/second) | 5 | Clear, level-ground speed at 20 TPS. The server controls this for every player. |
| Loaded vortex limit per dimension | 32 | Natural discovery stops at this number of loaded vortices, including active encounters. Existing entities are not deleted. |

## Files

Personal settings: `config/spectrobesreforged-client.toml`. Each player has their own file.

World/server settings: `<world>/serverconfig/spectrobesreforged-server.toml`. For a dedicated server, edit that world’s file. NeoForge synchronizes server settings to clients. Restarting after manual edits is a reliable way to apply them. Lowering the vortex limit does not delete existing entities; command summons remain possible.

## Multiplayer behavior

Install the **same mod version on server and clients**, with Minecraft 1.21.1, NeoForge, GeckoLib and Java 21. Beta.12 uses network protocol 6 and cannot connect to earlier protocol revisions. Worlds retain their existing save data.

The server validates Prizmod possession, ownership, selections, inventory ingredients and machine distance; opening, moving, feeding and scanning requests are bounded. The scanner cannot be repeatedly extended by spamming its key. Collection actions remain player-owned; [Cooperative excavation](getting-started.md#cooperative-excavation).

Scan ranges and Krawl definitions synchronize on login and `/reload`. Existing loaded Krawl refresh without healing. Disconnecting clears local display data without touching the server’s collection or registry. Textures/models still come from the matching mod/resources; datapack synchronization does not download artwork.

## Verification

Development checks cover dedicated-server startup without a client, loaded definitions and configuration, Krawl construction, vortex descent, Prizmod possession and independent player request limits. A full live session with two connected human clients remains a separate gameplay check.


**Optional integration:** [Curios API 9.5.1+1.21.1 for NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) adds the extra equipment slot. **Without Curios**, carry a Prizmod in your inventory or offhand and open it with **G** or right-click, as before. For the extra slot in multiplayer, install Curios on both server and clients. Unequip the item into normal inventory before uninstalling Curios.

Open your inventory and use the **Curios equipment button**. Place any of the sixteen Prizmod colors in the **Prizmod** slot, then close the inventory and press **G**. It uses no normal inventory or armor slot. Carrying one in your inventory/offhand and right-clicking still work.

Curios also defaults to G. With both actions on the same key, G opens equipment when you have no Prizmod, and opens your collection when you carry or equip one. The inventory button always opens equipment; both keys remain rebindable.


[Cooperative raids](vortices.md#encounter-rules) · [Cooperative excavation](getting-started.md#cooperative-excavation).


## HUD layout and beta feedback

Open **Mods → Spectrobes Reforged → Config → Configure HUD**. Drag the preview, scroll to resize (50–150%), toggle the panel or team slots, or reset. Preferences also expose horizontal/vertical percentages and scale; these settings are local and survive restarts. The default size is 85%. Use **Share feedback on Discord** to open the invitation after confirmation. A beta-only local chat message appears once when joining a world/server, with a clickable Discord invitation. It is not broadcast and does not repeat on respawn or dimension changes. Use the vertical HUD toggle for a team column; resize with the − / + buttons or the wheel.
