"""Render localized editions and immutable historical documentation snapshots."""
from pathlib import Path
from html import escape
import json
import re
import shutil
import os
import hashlib
import markdown
from locales import UI, LANGUAGE_NAMES


def relative(target, directory):
    return Path(os.path.relpath(target, directory)).as_posix()


NAV_LABELS = {
'en': ['Start here','Exploration','Collection','Help and settings','Wiki guides','Language and edition'],
'es': ['Inicio','Exploración','Colección','Ayuda y ajustes','Guías de la wiki','Idioma y edición'],
'fr': ['Pour commencer','Exploration','Collection','Aide et réglages','Guides du wiki','Langue et édition'],
'de': ['Einstieg','Erkundung','Sammlung','Hilfe und Einstellungen','Wiki-Leitfäden','Sprache und Ausgabe'],
'it': ['Per iniziare','Esplorazione','Collezione','Aiuto e impostazioni','Guide della wiki','Lingua ed edizione'],
'pt-BR': ['Comece aqui','Exploração','Coleção','Ajuda e configurações','Guias da wiki','Idioma e edição']}
NAV_SECTIONS = [
['index','getting-started'],
['fossils','scanning','vortices','krawl'],
['awakening','prizmod','spectrobes','gallery','minerals','advancements'],
['settings','troubleshooting','commands','about']]

def grouped_navigation(navigation, slug, language):
    by_slug={p['slug']:p for p in navigation};groups=[]
    for number,keys in enumerate(NAV_SECTIONS):
        links=[]
        for key in keys:
            if key not in by_slug:continue
            page=by_slug[key]
            current=' aria-current="page"' if key==slug else ''
            links.append(f'<a href="{key}.html"{current}>{escape(page["label"])}</a>')
        if links:groups.append(f'<section class="nav-group"><h2>{NAV_LABELS[language][number]}</h2>'+''.join(links)+'</section>')
    assert set(by_slug)<=set(k for group in NAV_SECTIONS for k in group), 'New guide needs a navigation group'
    return '<nav class="guide-nav" aria-label="'+escape(NAV_LABELS[language][4])+'">'+''.join(groups)+'</nav>'


def render(root, docs, output, references, validate):
    version = re.search(r'^mod_version=(.+)$', (root / 'gradle.properties').read_text(encoding='utf-8'), re.M)[1].strip()
    editions = []
    for language in LANGUAGE_NAMES:
        directory = docs if language == 'en' else docs / 'i18n' / language
        navigation = json.loads((directory / 'navigation.json').read_text(encoding='utf-8'))
        editions.append(dict(version=version, language=language, navigation=navigation,
            pages={p['slug']: (directory / (p['slug']+'.md')).read_text(encoding='utf-8') for p in navigation},
            folder=output if language == 'en' else output / language, archived=False))
    for file in sorted((docs / 'versions').glob('*.json'), reverse=True):
        snapshot = json.loads(file.read_text(encoding='utf-8'))
        assert re.fullmatch(r'[a-zA-Z0-9.-]+', snapshot['version'])
        if snapshot['version'] == version: continue
        snapshot.update(folder=output / 'versions' / snapshot['version'], archived=True)
        editions.append(snapshot)
    output.mkdir(parents=True, exist_ok=True)
    shared = output / 'assets'
    shutil.copytree(Path(__file__).parent / 'assets', shared, dirs_exist_ok=True)
    asset_names = {}
    for file in (Path(__file__).parent / 'assets').iterdir():
        if not file.is_file(): continue
        digest = hashlib.sha256(file.read_bytes()).hexdigest()[:12]
        filename = f'{file.stem}.{digest}{file.suffix}'
        shutil.copy2(file, shared / filename)
        asset_names[file.name] = filename

    from item_cards import copy_icons
    copy_icons(root, shared / 'items')
    from design_gallery import copy_gallery
    copy_gallery(root, shared / 'gallery')

    replacements = {lang: references(root, lang) | {'VERSION':version} for lang in LANGUAGE_NAMES}
    news = json.loads((docs / 'news.json').read_text(encoding='utf-8'))
    manifest = []
    for edition in editions:
        language = edition['language']; ui = UI[language]; folder = edition['folder']
        folder.mkdir(parents=True,exist_ok=True); (folder / 'assets').mkdir(exist_ok=True)
        navigation = edition['navigation']; search = []
        assert len({p['slug'] for p in navigation}) == len(navigation)
        asset = relative(shared, folder)
        def asset_url(name): return asset + '/' + asset_names[name]
        rendered_pages = {}
        def destination(other, slug):
            actual = slug if slug in other['pages'] else 'index'
            return relative(other['folder'] / (actual+'.html'), folder)
        for position, page in enumerate(navigation):
            slug = page['slug']; assert re.fullmatch(r'[a-z0-9-]+', slug)
            source = edition['pages'][slug]
            if not edition['archived']:
                for key,value in replacements[language].items():
                    source = source.replace('{{'+key+'}}',value)
                    source = re.sub(r'<!-- BEGIN AUTO:'+key+r' -->.*?<!-- END AUTO:'+key+r' -->',lambda _:value,source,flags=re.S)
                source = re.sub(r'<!-- VERSION -->.*?<!-- /VERSION -->',lambda _:version,source)
            assert not re.search(r'\{\{[^}]+\}\}',source), f'Unresolved placeholder {language}/{slug}'
            source = source.replace('__ITEM_ASSETS__', asset + '/items').replace('__GALLERY_ASSETS__', asset + '/gallery')
            md = markdown.Markdown(extensions=['tables','fenced_code','toc','attr_list'],extension_configs={'toc':{'permalink':True}})
            body = md.convert(source)
            body = re.sub(r'href="([a-z0-9-]+)\.md([^"]*)"',r'href="\1.html\2"',body)
            # Keep semantic tables intact; the wrapper owns horizontal scrolling on phones.
            def table_region(match):
                content = match[0].replace('<th>', '<th scope="col">')
                content = re.sub(r'<td>([+−\-]?\d[^<]*)</td>', r'<td class="numeric">\1</td>', content)
                content = re.sub(r'<td>(Corona|Aurora|Flash)</td>', lambda m: '<td><span class="element-badge '+m[1].lower()+'">'+m[1]+'</span></td>', content)
                return '<div class="table-scroll" tabindex="0" role="region" aria-label="'+escape(page['title'], quote=True)+'">'+content+'</div>'
            body = re.sub(r'<table>.*?</table>', table_region, body, flags=re.S)
            nav = grouped_navigation(navigation,slug,language)
            pager=''.join(f'<a href="{p["slug"]}.html">{label} {escape(p["title"])}</a>' for p,label in [(navigation[position-1] if position else None,'←'),(navigation[position+1] if position+1<len(navigation) else None,ui['next'])] if p)
            languages = ''.join(f'<option data-language="{e["language"]}" value="{destination(e,slug)}"'+(' selected' if e is edition else '')+f'>{escape(LANGUAGE_NAMES[e["language"]])}{" · "+version if edition["archived"] else ""}</option>' for e in editions if not e['archived'])
            if edition['archived']: languages=f'<option selected data-language="en" value="{slug}.html">English · {escape(edition["version"])}</option>'+languages
            choices=[e for e in editions if e['language']==language and not e['archived']]+[e for e in editions if e['archived']]
            versions=''.join(f'<option data-current="{str(not e["archived"]).lower()}" value="{destination(e,slug)}"'+(' selected' if e is edition else '')+f'>{escape(e["version"])}{" · "+ui["current"] if not e["archived"] else " · English"}{" · "+ui["home"] if slug not in e["pages"] else ""}</option>' for e in choices)
            banner=''
            if edition['archived']:
                latest=next(e for e in editions if e['language']==language and not e['archived'])
                banner=f'<aside class="archive-notice"><strong>{escape(ui["archive"])}</strong> {escape(edition["version"])} · {escape(ui["archive_note"])} <a href="{destination(latest,slug)}">{escape(ui["latest"])}</a></aside>'
            spotlight=''
            if slug=='index' and not edition['archived']:
                cards=''.join(f'<a class="news-card" href="{escape(n["href"])}"><strong>{escape(n[language][0])}</strong><span>{escape(n[language][1])}</span><span class="news-arrow" aria-hidden="true">↗</span></a>' for n in news['items'])
                spotlight=f'<section class="release-spotlight" aria-labelledby="news-title"><div class="release-meta"><span>{escape(ui["news"])}</span><time datetime="{news["date"]}">{news["date"]}</time></div><h2 id="news-title">{escape(ui["release"])} {escape(version)}</h2><div class="news-grid">{cards}</div></section>'
            is_home = slug == 'index' and not edition['archived']
            introduction = ''
            if is_home:
                match = re.match(r'(<h1.*?</h1>\s*<p>.*?</p>)', body, re.S)
                if match:
                    introduction = '<div class="home-intro">' + match[1] + '</div>'
                    body = body[match.end():]
            ui_json=json.dumps(ui,ensure_ascii=False).replace('<','\\u003c')
            html=f'''<!doctype html>
<html lang="{language}"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>{escape(page['title'])} · Spectrobes Reforged Wiki · {escape(edition['version'])}</title>
<meta name="description" content="{escape(page['title'])} · Spectrobes Reforged {escape(edition['version'])}">
<meta name="theme-color" content="#101114"><link rel="icon" href="{asset_url('icon.png')}" type="image/png">
<script>window.WIKI_UI={ui_json};</script><script src="{asset_url('theme.js')}"></script><link rel="stylesheet" href="{asset_url('wiki.css')}">
<script src="assets/search-data.__SEARCH_HASH__.js" defer></script><script src="{asset_url('search.js')}" defer></script><script src="{asset_url('navigation.js')}" defer></script></head>
<body data-archived="{str(edition['archived']).lower()}" class="{'home-page' if is_home else 'guide-page'}"><a class="skip" href="#main">{escape(ui['skip'])}</a>
<header class="topbar"><a class="brand" href="index.html"><img class="brand-icon" src="{asset_url('icon.png')}" alt="" width="48" height="48"><span>SPECTROBES REFORGED<small>{escape(ui['field'])}</small></span></a>
<div class="header-links"><button id="theme-toggle" type="button" hidden>{escape(ui['theme'])}</button>
<a class="icon-link" href="https://www.curseforge.com/minecraft/mc-mods/spectrobes-reforged" aria-label="{escape(ui['download'])}" title="{escape(ui['download'])}"><img src="{asset_url('download.svg')}" alt="" width="22" height="22"><span>{escape(ui['download'])}</span></a>
<a class="icon-link" href="https://discord.gg/7QkScUWBe9" aria-label="Discord" title="Discord"><img src="{asset_url('discord.svg')}" alt="" width="24" height="24"><span>Discord</span></a></div></header>
<div class="shell"><aside class="sidebar"><label for="search">{escape(ui['search'])}</label><input id="search" type="search" placeholder="{escape(ui['placeholder'])}" autocomplete="off" aria-controls="search-results"><div id="search-status" role="status" aria-live="polite"></div><ul id="search-results" hidden></ul>
<details class="guide-menu" open><summary><span aria-hidden="true">☰</span> {escape(NAV_LABELS[language][4])}<small>{escape(page['label'])}</small></summary>{nav}</details>
<div class="reading-options"><h2>{escape(NAV_LABELS[language][5])}</h2><div class="edition-controls"><label for="language">{escape(ui['language'])}</label><select id="language" data-navigate autocomplete="off">{languages}</select><label for="version">{escape(ui['version'])}</label><select id="version" data-navigate autocomplete="off">{versions}</select><p class="edition-help">{escape(ui['editions_note'])}</p></div><span class="version">{escape(edition['version'])} · Minecraft 1.21.1</span></div></aside>
<main id="main">{banner}{introduction}{spotlight}<article>{body}</article><nav class="pager" aria-label="{escape(ui['pager'])}">{pager}</nav><footer>{escape(ui['footer'])} · <a href="https://github.com/jvprz/SpectrobesReforged/issues">{escape(ui['report'])}</a></footer></main>
<aside class="contents"><span>{escape(ui['contents'])}</span><nav aria-label="{escape(ui['contents'])}">{md.toc}</nav></aside></div></body></html>'''
            rendered_pages[slug] = html
            search.append(dict(title=page['title'],url=slug+'.html',text=re.sub(r'\s+',' ',re.sub(r'<[^>]+>',' ',body)).strip()))
        search_js = 'window.WIKI_SEARCH=' + json.dumps(search,ensure_ascii=False).replace('</','<\\/') + ';\n'
        search_hash = hashlib.sha256(search_js.encode('utf-8')).hexdigest()[:12]
        (folder / ('assets/search-data.'+search_hash+'.js')).write_text(search_js,encoding='utf-8')
        for slug, html in rendered_pages.items():
            (folder / (slug+'.html')).write_text(html.replace('__SEARCH_HASH__', search_hash),encoding='utf-8')
        manifest.append(dict(version=edition['version'],language=language,archived=edition['archived'],url=relative(folder/'index.html',output),commit=edition.get('commit')))
    (output/'editions.json').write_text(json.dumps(manifest,indent=2),encoding='utf-8')
    (output/'.nojekyll').write_text('')
    validate(output)
    print(f'Built {version}: {len(LANGUAGE_NAMES)} languages and {sum(e["archived"] for e in editions)} historical editions.')
