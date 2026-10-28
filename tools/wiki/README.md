# Player wiki

Build with `python tools/wiki/build.py`; Python dependencies are in `requirements.txt`.
Run integration checks with `python -m unittest discover -s tools/wiki -p "test_*.py"`.

Recipes and inventory sprites are resolved directly from bundled recipe/model JSON. Mod textures are copied to the generated site; vanilla textures use external links pinned to Minecraft 1.21.1. Builds do not require external image downloads. Image credits appear with illustrated recipes.

Tables retain semantic headers inside keyboard-focusable horizontal scroll regions. Edition selectors restore the loaded edition after browser history navigation. Session storage remembers the preferred current language while viewing English-only historical editions; navigation still works when storage is unavailable.

Optional browser regression (requires Node and Playwright):

`node tools/wiki/test_navigation.cjs build/wiki [screenshots-folder]`

Uses local HTTP and headless Edge by default. Set `WIKI_BROWSER_CHANNEL` for another installed Chromium channel. Covers six-language switches, historical editions, return to the preferred language, repeated pageshow, Back/Forward and 24 mobile table pages.

Historical snapshots are immutable. Use `freeze_version.py` after committing a release; local development builds are not automatically archived as published releases.
