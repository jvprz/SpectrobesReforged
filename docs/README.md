# Maintaining the player wiki

The current English guides are in `docs/*.md`. Translated player guides and navigation are in `docs/i18n/{es,fr,de,it,pt-BR}/`. Edit those Markdown sources when a mechanic changes. Tables and item names are generated from the mod resources in each language. Biome names and identifiers follow the source data.

## Build and verify

```text
python -m pip install -r tools/wiki/requirements.txt
python -m unittest discover -s tools/wiki -p "test_*.py"
python tools/wiki/build.py
python -m http.server 8000 --directory build/wiki
```

Open `http://localhost:8000/es/index.html` for Spanish. English keeps the existing root URLs, so previously shared links continue to work. Python 3.10+ is required; CI uses 3.12. All output is static and works on GitHub Pages, under its repository subpath, or locally. No API keys, external translation services, remote fonts or analytics are required.

The build checks every HTML page, local link, fragment, icon, script and selector destination. Search is scoped to the selected language and release and ignores accents. Light/dark mode retains the existing saved preference. Icons have accessible labels; language/version controls work by keyboard.

## Homepage news

Edit `docs/news.json`: set the release date and maintain the three relevant highlights in all six languages. Targets are local HTML guide links. News appears only in the current edition, above the main guide. Do not add current news to historical snapshots.

## Archived versions

`docs/versions/*.json` stores resolved Markdown, navigation, the release number and the exact Git commit. Included snapshots: 0.5.1-alpha (`cdb1536`) and 0.6.0-beta.1 (`0558fe6`). These are actual historical guides, not current pages relabeled with an old version. They retain their original English text and visible archive notices. The version menu marks unavailable guides as Home and sends the reader to that edition's start page. Selecting a current language from an archive explicitly changes to the current release.

Before bumping to the next mod version, commit the documented release, then run:

```text
python tools/wiki/freeze_version.py
```

Commit the resulting snapshot. While its version equals `mod_version`, the builder continues to show the current edition only. After the version bump, the snapshot automatically appears in the archive selector. The freezer requires a clean Git worktree and refuses to overwrite an existing snapshot. It preserves English guides and resolved data; translated historical editions are not implied.

## Other source files

- `docs/navigation.json` and each translated `navigation.json`: page names and order. Keep all six current editions complete.
- `tools/wiki/locales.py`: interface, theme and search messages.
- `tools/wiki/table_terms.json`: generated table vocabulary. Game item names use the bundled language JSON resources.
- `tools/wiki/assets/`: neutral light/dark styles, local SVG icons and scripts. The supplied mod icon is retained.
- Keep links as `other-page.md#heading`. For translated headings needed by shared links, use stable IDs such as `## Localized heading {#incubator}`.

## Publish

Commit and push the changes, then merge into **main**. The existing **Publish player wiki** GitHub Actions workflow validates the site and deploys GitHub Pages from main. Pull requests validate without deploying. The public URL is `https://jvprz.github.io/SpectrobesReforged/`; it does not change until deployment succeeds. In repository Settings → Pages, keep **GitHub Actions** as the source.
