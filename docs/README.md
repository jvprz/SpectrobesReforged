# Maintaining the player wiki

Player pages live in this directory. The site builder reads Markdown, navigation.json and the current mod data. Generated HTML goes into build/wiki, already excluded from Git.

## Build and preview

```text
python -m pip install -r tools/wiki/requirements.txt
python tools/wiki/build.py
python -m http.server 8000 --directory build/wiki
```

Open http://localhost:8000. No Discord webhook, CurseForge API key or personal token is required. The build also checks local links, anchors, navigation and unresolved data placeholders.

The supported Python version for CI is 3.12. The builder requires Python 3.10 or newer.

## Publish on GitHub Pages

1. Commit and push the wiki files to the repository's **main** branch (merge from dev when ready).
2. In GitHub, open **Settings → Pages → Build and deployment → Source → GitHub Actions**.
3. Run **Publish player wiki** from Actions on **main**, or let the next relevant push trigger it.
4. If GitHub requests environment approval, approve the github-pages deployment.
5. Copy the published URL from the deployment result to CurseForge and Discord.

The expected project URL is https://jvprz.github.io/SpectrobesReforged/ once deployment succeeds. It is not live merely because these files exist. Pull requests build and validate the wiki without deploying. The workflow only deploys from main.

## Edit

- Edit player-facing prose in the corresponding Markdown file.
- Keep relative links in the form `other-page.md#heading` so GitHub and the generated site both work.
- Add pages to navigation.json.
- Do not hand-edit generated tables: the builder fills the named placeholders from source data.
- Whenever mechanics change, check the relevant guide and limitations, not only the tables.
- The wiki is English; the six bundled game locales are independent resources.

The site uses local CSS and JavaScript, with no remote fonts, analytics or required external theme.
