# Player wiki

Build with `python tools/wiki/build.py`; Python dependencies are in `requirements.txt`.
Run integration checks with `python -m unittest discover -s tools/wiki -p "test_*.py"`.

Recipes resolve directly from bundled recipe/model JSON. Block ingredients and outputs render as transparent 64×64 orthographic PNGs with nearest-neighbor UV sampling and directional face shading. The excavation table uses its current five-cube model; the incubator keeps its purpose-made inventory sprite. Flat inventory items retain their sprite, including glass panes.

`block_icons.py` resolves model parents, texture aliases, cuboid rotations, transparent faces and first animation frames. Mod textures come from the source tree; the minimal Minecraft 1.21.1 reference subset in `vanilla/` makes block rendering deterministic and offline. Other vanilla item sprites keep their version-pinned external links. Pillow is installed with the wiki requirements. Artwork attribution remains visible with illustrated recipes. Rebuilding updates icons after model/texture changes; runtime mod assets are unaffected.

Tables retain semantic headers inside keyboard-focusable horizontal scroll regions. Edition selectors restore the loaded edition after browser history navigation. Session storage remembers the preferred current language while viewing English-only historical editions; navigation still works when storage is unavailable.

Optional browser regression (requires Node and Playwright):

`node tools/wiki/test_navigation.cjs build/wiki [screenshots-folder]`

Uses local HTTP and headless Edge by default. Set `WIKI_BROWSER_CHANNEL` for another installed Chromium channel. Covers six-language switches, historical editions, return to the preferred language, repeated pageshow, Back/Forward and 24 mobile table pages.

Historical snapshots are immutable. Use `freeze_version.py` after committing a release; local development builds are not automatically archived as published releases.

`design_gallery.py` generates static previews from the actual Child geometry, bone hierarchy and color textures. The six existing designs and sixteen temporary models are labeled separately; gallery previews do not imply that an unfinished design is final.

Creature previews follow GeckoLib 4 cube/bone handedness and pivot order, floor box-UV dimensions and cull back-facing coplanar planes. Block rendering remains independent. Desktop navigation is permanently grouped into four sections; mobile uses a labeled native details menu with the current guide, and edition controls remain separate.
