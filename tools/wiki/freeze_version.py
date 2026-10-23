"""Freeze the current English guide and resolved data before changing mod_version."""
from pathlib import Path
import argparse
import json
import re
import subprocess
from build import references, load


def freeze(root):
    version = re.search(r'^mod_version=(.+)$', (root / 'gradle.properties').read_text(encoding='utf-8'), re.M)[1].strip()
    assert re.fullmatch(r'[a-zA-Z0-9.-]+', version)
    destination = root / 'docs/versions' / (version + '.json')
    if destination.exists():
        raise FileExistsError(f'Historical snapshot already exists: {destination}')
    commit = subprocess.check_output(['git','rev-parse','HEAD'],cwd=root,text=True).strip()
    changes = subprocess.check_output(['git','status','--porcelain'],cwd=root,text=True).strip()
    if changes:
        raise RuntimeError('Commit the documented release first, then freeze it so the provenance is exact.')
    navigation = load(root / 'docs/navigation.json')
    replacements = references(root) | {'VERSION':version}
    pages = {}
    for page in navigation:
        source = (root / 'docs' / (page['slug']+'.md')).read_text(encoding='utf-8')
        for key,value in replacements.items():
            source = source.replace('{{'+key+'}}',value)
            source = re.sub(r'<!-- BEGIN AUTO:'+key+r' -->.*?<!-- END AUTO:'+key+r' -->',lambda _:value,source,flags=re.S)
        source = re.sub(r'<!-- VERSION -->.*?<!-- /VERSION -->',lambda _:version,source)
        pages[page['slug']] = source
    snapshot = dict(version=version,commit=commit,language='en',navigation=navigation,pages=pages)
    destination.parent.mkdir(parents=True,exist_ok=True)
    destination.write_text(json.dumps(snapshot,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    print(f'Frozen {version}: {destination}')


if __name__ == '__main__':
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--root',type=Path,default=Path(__file__).resolve().parents[2])
    freeze(parser.parse_args().root.resolve())
