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


def references(root, language="en"):
    resources = root / 'src/main/resources/data/spectrobesreforged'
    locale = {'en':'en_us','es':'es_es','fr':'fr_fr','de':'de_de','it':'it_it','pt-BR':'pt_br'}[language]
    names = load(root / f'src/main/resources/assets/spectrobesreforged/lang/{locale}.json')

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
        if recipe['type'] != 'minecraft:crafting_shapeless' or 'prizmod' in recipe['result']['id']:
            continue
        recipes.append([item(recipe['result']['id']),
                        ' + '.join(item(v['item']) for v in recipe['ingredients'])])
    krawl_rows = []
    for file in sorted((resources / 'entity/krawl').glob('*.json')):
        k = load(file)
        krawl_rows.append([k['name'], k['element'].capitalize(), k['rank'].capitalize(),
                           k['stats']['hp'], k['stats']['attack'], k['stats']['defense']])
    result = {
        'BIOMES': table(['Element', 'Included biomes'], biomes),
        'FOSSIL_DROPS': table(['Ore element', 'Possible fossil items'], drops),
        'SPECIES': table(['Species', 'Element', 'HP', 'Attack', 'Defense', 'Scan radius'], stats),
        'EVOLUTIONS': table(['Child', 'Configured target', 'Mineral threshold', 'Target definition'], evolutions),
        'MINERALS': table(['Mineral', 'Item ID', 'HP', 'Attack', 'Defense'], mineral_rows),
        'MINERAL_ORES': table(['Tier', 'Y range', 'Non-Silk-Touch drops'], ore_rows),
        'RECIPES': table(['Result', 'Ingredients'], recipes),
        'KRAWL': table(['Krawl', 'Element', 'Rank', 'HP', 'Attack', 'Armor'], krawl_rows)
    }
    if language != 'en':
        terms = load(Path(__file__).parent / 'table_terms.json')[language]
        pattern = re.compile(r'\b(' + '|'.join(re.escape(k) for k in sorted(terms, key=len, reverse=True)) + r')\b')
        result = {key: pattern.sub(lambda match: terms[match[0]], value) for key, value in result.items()}
    return result


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
        for attribute in ['href', 'src'] + (['value'] if tag == 'option' else []):
            if attribute in values:
                self.links.append(values[attribute])


def validate(output):
    pages = {}
    for file in output.rglob('*.html'):
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
    from wiki_site import render
    render(root, docs, output, references, validate)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[2])
    parser.add_argument('--docs', type=Path)
    parser.add_argument('--output', type=Path)
    args = parser.parse_args()
    root = args.root.resolve()
    build(root, args.docs or root / 'docs', args.output or root / 'build/wiki')
