# Prizmod and companions

Use/right-click any Prizmod to open the same player-owned collection.

Open **Mods → Spectrobes Reforged → Config** for personal preferences. Rebind **R** (scan) and **G** (open Prizmod) under **Options → Controls → Key Binds**. G works with an equipped Prizmod or any Prizmod carried in your inventory or offhand; it does not grant free access without the item. Shortcuts do not fire while typing in chat or using another screen.


## Equip your Prizmod

**Optional integration:** [Curios API 9.5.1+1.21.1 for NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) adds the extra equipment slot. **Without Curios**, carry a Prizmod in your inventory or offhand and open it with **G** or right-click, as before. For the extra slot in multiplayer, install Curios on both server and clients. Unequip the item into normal inventory before uninstalling Curios.

Open your inventory and use the **Curios equipment button**. Place any of the sixteen Prizmod colors in the **Prizmod** slot, then close the inventory and press **G**. It uses no normal inventory or armor slot. Carrying one in your inventory/offhand and right-clicking still work.

Curios also defaults to G. With both actions on the same key, G opens equipment when you have no Prizmod, and opens your collection when you carry or equip one. The inventory button always opens equipment; both keys remain rebindable.

Equipment is saved by Curios. Death follows the server’s Curios settings and, by default, `keepInventory`. Your collection belongs to your player, not the item; unequipping or losing it does not erase Spectrobes. The accessory grants no armor points.

## Collection and controls

Storage shows **16 entries per page**, with previous/next buttons. Search by name, filter by element or crown, and sort by name or level. Click a card to select it; filters never rearrange the saved collection.

The detail panel shows stage, element, HP, attack, defense, level, XP, quality bonus, minerals fed and battles won. Use **Companion**, **Store**, **Team** or **Feed** to act on the selection. Feeding lists minerals in your inventory; click one to give exactly one item in Survival.

The companion slot accepts one living Child. Six team slots accept Adult/Evolved entries; the bundled roster currently contains Children. Stage rules are checked before any swap. Evolution is not available yet.

## Quality and training

Excavation integrity gives **+1% at 70–84, +2% at 85–94, +3% at 95–100**, rounded down per base stat. A perfect excavation adds a permanent gold crown after the fossil and awakened Spectrobe name. Quality is saved separately from the crown; old entries without quality retain their existing stats.

Minerals C/B/A/A+ grant **10/20/40/60% of a level**, alongside their existing stat effects. Other active stat minerals grant 25%; Diamond/Evolve remain inactive. Level cap: **100**. Filling the XP bar raises stats gradually within species limits. Minerals can still be useful for XP when their stat is capped. No item is consumed if neither XP nor stats change.

Children gain XP through feeding. Completed raids grant one victory and up to 60 training XP to living Adult/Evolved in each participant’s team, reduced after that player dies; Children gain no battle XP. These saved counters prepare future evolution without enabling it.

## Companions and persistence

Select a living Child and press Companion. It follows you and assists scanning. During [vortex encounters](vortices.md) it stays safely stored, then returns. Training and collection data persist; Children are protected from damage. Switching companion refreshes its entity. Back up your world before updating.

The selected entry displays its animated 3D model and actual color beside the stats. Collection cards keep their small portraits.

## Evolution reference: Beyond the Portals

| Spectrobe | Minerals |
| --- | --- |
| Komainu | 5 |
| Inkana | 4 |
| Vilar | 7 |
| Segu | 4 |
| Spiko | 3 |
| Shakin | 4 |

Reference only; evolution remains unavailable.


## Crafting and colors

{{PRIZMOD_RECIPE}}

{{DYE_RECIPES}}

{{ITEM_CREDITS}}


Child companions cannot take combat or environmental damage, including arrows, fire, lava, falls, suffocation and drowning. Their saved PS remain protected too. Opening the Prizmod restores injured or zero-HP Children from older saves without changing their stats, training or medals. Adult/Evolved combat forms retain their damage rules. Children still return to storage during raids.
