"""Build the player wiki and verify all local page links and anchors."""
from pathlib import Path
from html import escape
from html.parser import HTMLParser
from urllib.parse import urlsplit, unquote
import argparse
import json
import re
import shutil
import markdown


def table(headers, rows):
    return '\n'.join(['| ' + ' | '.join(headers) + ' |',
                      '| ' + ' | '.join(['---'] * len(headers)) + ' |'] +
                     ['| ' + ' | '.join(str(cell).replace('|', '\\|') for cell in row) + ' |'
                      for row in rows])


def load(path):
    return json.loads(path.read_text(encoding='utf-8-sig'))


def references(root):
    resources = root / 'src/main/resources/data/spectrobesreforged'
    names = load(root / 'src/main/resources/assets/spectrobesreforged/lang/en_us.json')

    def item(identifier):
        identifier = identifier.split(':')[-1]
        return names.get('item.spectrobesreforged.' + identifier, identifier.replace('_', ' ').title())

    species = sorted((load(p) for p in (resources / 'entity/spectrobe').glob('*.json')),
                     key=lambda s: s['key'])
    assert species, 'No species definitions found'
    known = {s['key'] for s in species}
    stats = []
    evolutions = []
    for s in species:
        values = [f"{s['stats'][key]['base']} → {s['stats'][key]['max']}"
                  for key in ['hp', 'attack', 'defense']]
        stats.append([s['key'].capitalize(), s['type'].capitalize(), *values,
                      str(s['range'] * 2) + ' blocks'])
        evolution = s['evolution']
        evolutions.append([s['key'].capitalize(), evolution['to'].capitalize(),
                           evolution['minerals_required'],
                           'Defined' if evolution['to'] in known else 'Not bundled'])

    biomes = []
    for element, group in [('Corona', 'hot'), ('Aurora', 'temperate'), ('Flash', 'cold')]:
        values = load(resources / f'tags/worldgen/biome/{group}.json')['values']
        biomes.append([element, ', '.join(v.split(':')[-1].replace('_', ' ').title() for v in values)])

    drops = []
    for element in ['corona', 'aurora', 'flash']:
        loot = load(resources / f'loot_table/blocks/{element}_fossil_ore.json')
        fossils = [entry['name'] for pool in loot['pools'] for entry in pool['entries']
                   if entry.get('name', '').endswith('_fossil')]
        drops.append([element.capitalize(), ', '.join(item(f) + ' (`' + f.split(':')[-1] + '`)' for f in fossils)])

    java = (root / 'src/main/java/com/jvprz/spectrobesreforged/common/registry/ModItems.java').read_text(encoding='utf-8')
    mineral_rows = []
    for match in re.finditer(r'ITEMS\.register\("([a-z_]+)",\s*\(\) -> new MineralItem\((.*?)"([^"\n]+)"\)\);', java, re.S):
        identifier, params, _ = match.groups()
        values = re.findall(r'(-?\d+|null),\s*(VAL|MAX)', params)
        assert len(values) == 3, identifier
        cells = ['MAX' if mode == 'MAX' else ('—' if value == 'null' else f'{int(value):+d}')
                 for value, mode in values]
        mineral_rows.append([item(identifier), '`' + identifier + '`', *cells])
    assert mineral_rows, 'No mineral registrations found'

    ore_rows = []
    for tier in ['c', 'b', 'a', 'a_plus']:
        identifier = tier + '_mineral_ore'
        placements = load(resources / f'worldgen/placed_feature/{identifier}.json')['placement']
        height = next(p['height'] for p in placements if p['type'] == 'minecraft:height_range')
        loot = load(resources / f'loot_table/blocks/{identifier}.json')
        pool = [entry['name'] for p in loot['pools'] for entry in p['entries']
                if entry.get('name') != 'spectrobesreforged:' + identifier]
        ore_rows.append([tier.upper().replace('_PLUS', '+'),
                         f"{height['min_inclusive']['absolute']} to {height['max_inclusive']['absolute']}",
                         ', '.join(item(v) for v in pool)])

    recipes = []
    for file in sorted((resources / 'recipe').glob('*.json')):
        recipe = load(file)
        assert recipe['type'] == 'minecraft:crafting_shapeless', file
        recipes.append([item(recipe['result']['id']),
                        ' + '.join(item(v['item']) for v in recipe['ingredients'])])
    krawl_rows = []
    for file in sorted((resources / 'entity/krawl').glob('*.json')):
        k = load(file)
        krawl_rows.append([k['name'], k['element'].capitalize(), k['rank'].capitalize(),
                           k['stats']['hp'], k['stats']['attack'], k['stats']['defense']])
    return {
        'BIOMES': table(['Element', 'Included biomes'], biomes),
        'FOSSIL_DROPS': table(['Ore element', 'Possible fossil items'], drops),
        'SPECIES': table(['Species', 'Element', 'HP', 'Attack', 'Defense', 'Scan radius'], stats),
        'EVOLUTIONS': table(['Child', 'Configured target', 'Mineral threshold', 'Target definition'], evolutions),
        'MINERALS': table(['Mineral', 'Item ID', 'HP', 'Attack', 'Defense'], mineral_rows),
        'MINERAL_ORES': table(['Tier', 'Y range', 'Non-Silk-Touch drops'], ore_rows),
        'RECIPES': table(['Result', 'Ingredients'], recipes),
        'KRAWL': table(['Krawl', 'Element', 'Rank', 'HP', 'Attack', 'Armor'], krawl_rows)
    }


class PageParser(HTMLParser):
    def __init__(self):
        super().__init__()
        self.links = []
        self.ids = set()

    def handle_starttag(self, tag, attrs):
        values = dict(attrs)
        if 'id' in values:
            assert values['id'] not in self.ids, f'Duplicate anchor: {values["id"]}'
            self.ids.add(values['id'])
        for attribute in ['href', 'src']:
            if attribute in values:
                self.links.append(values[attribute])


def validate(output):
    pages = {}
    for file in output.glob('*.html'):
        parser = PageParser()
        parser.feed(file.read_text(encoding='utf-8'))
        pages[file.resolve()] = parser
    count = 0
    for source, page in pages.items():
        for link in page.links:
            parts = urlsplit(link)
            if parts.scheme or parts.netloc:
                continue
            target = (source.parent / unquote(parts.path)).resolve() if parts.path else source
            assert target.is_relative_to(output.resolve()), f'Link escapes site: {link}'
            assert target.is_file(), f'Broken link in {source.name}: {link}'
            if parts.fragment and target in pages:
                assert unquote(parts.fragment) in pages[target].ids, f'Broken anchor in {source.name}: {link}'
            count += 1
    print(f'Validated {len(pages)} HTML pages and {count} local links/assets.')


def build(root, docs, output):
    navigation = load(docs / 'navigation.json')
    assert len({p['slug'] for p in navigation}) == len(navigation), 'Duplicate navigation slug'
    version = re.search(r'^mod_version=(.+)$', (root / 'gradle.properties').read_text(), re.M).group(1).strip()
    replacements = references(root) | {'VERSION': version}
    output.mkdir(parents=True, exist_ok=True)
    assets = Path(__file__).parent / 'assets'
    (output / 'assets').mkdir(exist_ok=True)
    for file in assets.iterdir():
        shutil.copy2(file, output / 'assets' / file.name)
    search = []
    for position, page in enumerate(navigation):
        slug = page['slug']
        assert re.fullmatch(r'[a-z0-9-]+', slug), slug
        source = (docs / (slug + '.md')).read_text(encoding='utf-8')
        for key, value in replacements.items():
            source = source.replace('{{' + key + '}}', value)
            source = re.sub(r'<!-- BEGIN AUTO:' + key + r' -->.*?<!-- END AUTO:' + key + r' -->',
                            lambda _: value, source, flags=re.S)
        source = re.sub(r'<!-- VERSION -->.*?<!-- /VERSION -->',
                        lambda _: version, source)
        assert not re.search(r'\{\{[^}]+\}\}', source), f'Unresolved placeholder in {slug}'
        renderer = markdown.Markdown(extensions=['tables', 'fenced_code', 'toc'],
                                     extension_configs={'toc': {'permalink': True}})
        body = renderer.convert(source)
        body = re.sub(r'href="([a-z0-9-]+)\.md([^"]*)"', r'href="\1.html\2"', body)
        nav = ''.join(f'<a href="{p["slug"]}.html"' +
                      (' aria-current="page"' if p['slug'] == slug else '') +
                      f'>{escape(p["label"])}</a>' for p in navigation)
        previous = navigation[position - 1] if position else None
        following = navigation[position + 1] if position + 1 < len(navigation) else None
        pager = ''.join(f'<a href="{p["slug"]}.html">{label} {escape(p["title"])}</a>'
                        for p, label in [(previous, '←'), (following, 'Next:') ] if p)
        title = escape(page['title'])
        html = f'''<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>{title} · Spectrobes Reforged Wiki</title>
<meta name="description" content="{escape(page['title'])} for Spectrobes Reforged {escape(version)}. Fossils, companions, minerals and current alpha mechanics.">
<meta name="theme-color" content="#101114"><link rel="icon" type="image/png" href="assets/icon.png">
<script src="assets/theme.js"></script><link rel="stylesheet" href="assets/wiki.css">
<script src="assets/search-data.js" defer></script><script src="assets/search.js" defer></script></head>
<body><a class="skip" href="#main">Skip to guide</a>
<header class="topbar"><a class="brand" href="index.html"><img class="brand-icon" src="assets/icon.png" alt="" width="48" height="48"><span>SPECTROBES REFORGED<small>FIELD GUIDE</small></span></a>
<div class="header-links"><button id="theme-toggle" type="button" aria-label="Switch color theme" hidden>Theme</button><a href="https://www.curseforge.com/minecraft/mc-mods/spectrobes-reforged">Download</a><a href="https://discord.gg/7QkScUWBe9">Discord ↗</a></div></header>
<div class="shell"><aside class="sidebar"><span class="version">{escape(version)} · Minecraft 1.21.1</span>
<label for="search">Search the guide</label><input id="search" type="search" placeholder="Fossils, awakening, minerals…" autocomplete="off" aria-controls="search-results"><div id="search-status" role="status" aria-live="polite"></div><ul id="search-results" hidden></ul>
<details open><summary>EXPLORE THE WIKI</summary><nav aria-label="Guides">{nav}</nav></details>
<p class="sidebar-note">Built for explorers.<br>Grounded in the current mod.</p></aside>
<main id="main"><div class="eyebrow">EXPLORER’S FIELD GUIDE / {escape(page['label'].upper())}</div><article>{body}</article>
<nav class="pager" aria-label="Previous and next guide">{pager}</nav>
<footer>Fan-made · Alpha mechanics may change · <a href="https://github.com/jvprz/SpectrobesReforged/issues">Report a documentation issue</a></footer></main>
<aside class="contents"><span>ON THIS PAGE</span><nav aria-label="On this page">{renderer.toc}</nav></aside></div></body></html>'''
        (output / (slug + '.html')).write_text(html, encoding='utf-8')
        plain = re.sub(r'<[^>]+>', ' ', body)
        search.append({'title': page['title'], 'url': slug + '.html',
                       'text': re.sub(r'\s+', ' ', plain).strip()})
    (output / 'assets/search-data.js').write_text('window.WIKI_SEARCH = ' +
            json.dumps(search, ensure_ascii=False).replace('</', '<\\/') + ';\n', encoding='utf-8')
    (output / '.nojekyll').write_text('')
    validate(output)
    print(f'Built Spectrobes Reforged {version} wiki at {output}')


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[2])
    parser.add_argument('--docs', type=Path)
    parser.add_argument('--output', type=Path)
    args = parser.parse_args()
    root = args.root.resolve()
    build(root, args.docs or root / 'docs', args.output or root / 'build/wiki')
