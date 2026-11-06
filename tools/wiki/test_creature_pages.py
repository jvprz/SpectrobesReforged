"""Profiles must reflect real species data and preserve locale/archive navigation."""
from pathlib import Path
import json
import re
import unittest
import test_build
from creature_pages import detail_pages, variants
from locales import LANGUAGE_NAMES

ROOT=Path(__file__).resolve().parents[2]
class CreatureProfilesTest(unittest.TestCase):
    setUpClass=classmethod(test_build.WikiEditionsTest.setUpClass.__func__)
    tearDownClass=classmethod(test_build.WikiEditionsTest.tearDownClass.__func__)
    def test_every_definition_has_a_localized_searchable_profile(self):
        for language in LANGUAGE_NAMES:
            folder=self.output if language=='en' else self.output/language
            pages=detail_pages(ROOT,language)
            self.assertEqual(25,len(pages))
            search=next((folder/'assets').glob('search-data.*.js')).read_text(encoding='utf-8')
            for page in pages:
                html=(folder/(page['slug']+'.html')).read_text(encoding='utf-8')
                self.assertIn('data-creature-profile',html)
                self.assertIn(page['slug']+'.html',search)
                self.assertIn('id="statistics"',html)
                self.assertIn('id="variants"',html)
                self.assertIn('creatures.',html)
                nav=re.search(r'<nav class="guide-nav".*?</nav>',html,re.S)[0]
                self.assertIn(page['parent']+'.html" aria-current="page"',nav)
    def test_stats_are_rendered_from_definitions_and_variants_are_distinct(self):
        for key in ['komainu','harumi','zoza']:
            data=json.loads((ROOT/'src/main/resources/data/spectrobesreforged/entity/spectrobe'/(key+'.json')).read_text(encoding='utf-8'))
            html=(self.output/'es'/('spectrobe-'+key+'.html')).read_text(encoding='utf-8')
            self.assertEqual(len(variants(ROOT,data,'spectrobe')),html.count('class="profile-variant"'))
            for stat in ['hp','attack','defense']:
                values=data['stats'][stat]
                sequence=''.join('<td'+(' class="numeric"' if str(values[field])[0].isdigit() else '')+'>'+str(values[field])+'</td>' for field in ['base','max','increment'])
                # Ignore whitespace and let the renderer retain its semantic numeric cells.
                self.assertIn(sequence,re.sub(r'\s+(?=<)','',html))
            self.assertIn('id="evolution"',html)
        temporary=(self.output/'es/spectrobe-harumi.html').read_text(encoding='utf-8')
        self.assertIn('Diseño provisional',temporary)
        self.assertEqual(1,temporary.count('class="profile-variant"'))
        krawl=(self.output/'es/krawl-blova.html').read_text(encoding='utf-8')
        self.assertEqual(1,krawl.count('class="profile-variant"'))
        self.assertIn('krawl-viblova.html',krawl)
        self.assertIn('krawl-blovant.html',krawl)
    def test_profile_locale_switches_keep_species_and_archive_falls_back(self):
        html=(self.output/'es/spectrobe-komainu.html').read_text(encoding='utf-8')
        options=test_build.Options();options.feed(html)
        self.assertIn('../fr/spectrobe-komainu.html',options.values)
        self.assertIn('../versions/0.6.0-beta.1/index.html',options.values)
        self.assertIn('../versions/0.5.1-alpha/index.html',options.values)
        self.assertFalse((self.output/'versions/0.6.0-beta.1/spectrobe-komainu.html').exists())
        self.assertIn('href="spectrobe-komainu.html"',(self.output/'es/gallery.html').read_text(encoding='utf-8'))
        self.assertIn('href="krawl-blova.html"',(self.output/'es/krawl.html').read_text(encoding='utf-8'))

if __name__=='__main__':unittest.main()
