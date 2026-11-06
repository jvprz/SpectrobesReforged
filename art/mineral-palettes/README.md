# Mineral palette pass

44 existing item textures receive gentle saturation reduction (10%) and value-range compression (10%). Pixel coordinates, dimensions, alpha and darkest outlines are preserved. No blur, resampling or redrawing is applied to runtime textures.

Original files are retained in source/ with hashes in sources.json. tools/art/soften_mineral_palettes.py always reads these frozen sources, so running it again does not repeatedly soften the results.

Coronium, Aurorium and Flashium are prepared as fifteen unregistered textures. C/B/A/A+ share shapes:
- C: power_c silhouette
- B: defense_b silhouette
- A: health_a silhouette
- A+: artist-authored Flashium star retained byte-for-byte; Coronium and Aurorium use hue-only shifts from that source. The archived artist source is artist-flashium-a-plus.png.
- S: Titanium silhouette for Coronium, Uranium for Aurorium, Chrome for Flashium. Coronium recolors Titanium’s neutral facets to red; highlights and dark outlines remain.

The recolors use red, green and blue/cyan ramps and preserve neutral bevels and highlights. No item registrations, drops, recipes or XP behavior are added in this art pass.

comparison.png shows originals on the left and edited versions on the right. flashium-preview.png shows the corrected blue family; elemental-minerals-preview.png shows all fifteen variants. Preview enlargements use nearest-neighbor scaling only.


## Authored sprite integration

Current seeds, Evolve variants, materials, cube and all elemental sprites are recorded in authored-sprites.json and backed up in authored-current/. Existing elemental runtime files take precedence over old palette-generation sources, so later hand edits are not overwritten by that script. Titanium is excluded from food palette edits. Uranium, Chrome and generic Evolve runtime resources are retired; their old copies remain in retired-runtime/. Titanium Plate is a registry alias for the single Titanium material, not a second item.
