# Zoza handmade texture

The CURRENT runtime `zoza_color_0.png` is the artist’s handmade source. Do not repaint, regenerate its UV art, resize or overwrite it. Exact source copies are kept in `handmade/`.

Use `python tools/art/recolor_zoza.py` (Pillow required) for RGB-only recolors 1 and 2. It reads the current base, leaves the base file and geometry byte-identical, and checks that both variants retain every alpha value and pixel position. Palette mappings and source hashes are recorded in `handmade-palettes.json`.

The old geometry/painting generator is disabled to avoid overwriting the artist’s work. `source/` preserves the earlier model history and is not used for recoloring.

## Current artist animation and portrait

The current geometry and animations were updated by the artist and must not be replaced by the archived generator. The idle/walk/sniff tracks reference the current hierarchy. `python tools/art/recolor_zoza_icon.py` generates the two 16×16 icon recolors from the current `zoza.png`, leaving the original byte-identical. Both Prizmod and HUD select the correct variant.
