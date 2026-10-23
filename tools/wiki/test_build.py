"""Regression checks for editions, localization and historically correct pages."""
from pathlib import Path
from html.parser import HTMLParser
import contextlib
import io
import json
import tempfile
import unittest
from build import build
from locales import LANGUAGE_NAMES, UI

ROOT=Path(__file__).resolve().parents[2]

class Options(HTMLParser):
    def __init__(self):
        super().__init__(); self.values=[]
    def handle_starttag(self,tag,attrs):
        if tag=='option': self.values.append(dict(attrs)['value'])

class WikiEditionsTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.temp=tempfile.TemporaryDirectory()
        cls.output=Path(cls.temp.name)/'wiki'
        with contextlib.redirect_stdout(io.StringIO()): build(ROOT,ROOT/'docs',cls.output)
    @classmethod
    def tearDownClass(cls): cls.temp.cleanup()

    def test_all_six_languages_have_actual_guides_and_localized_search(self):
        for language in LANGUAGE_NAMES:
            folder=self.output if language=='en' else self.output/language
            self.assertEqual(14,len(list(folder.glob('*.html'))),language)
            index=(folder/'index.html').read_text(encoding='utf-8')
            self.assertIn(f'<html lang="{language}">',index)
            self.assertIn(UI[language]['news'],index)
            self.assertIn(UI[language]['search'],index)
            self.assertEqual(3,index.count('class="news-card"'))
            self.assertIn('window.WIKI_SEARCH=',(folder/'assets/search-data.js').read_text(encoding='utf-8'))
            text=(folder/'vortices.html').read_text(encoding='utf-8')
            self.assertIn('15',text)
            self.assertIn('viblova',text.lower())

    def test_archives_retain_their_original_content_and_provenance(self):
        for file in (ROOT/'docs/versions').glob('*.json'):
            snapshot=json.loads(file.read_text(encoding='utf-8'))
            self.assertRegex(snapshot['commit'],r'^[a-f0-9]{40}$')
            folder=self.output/'versions'/snapshot['version']
            self.assertTrue((folder/'index.html').exists())
            text=(folder/'index.html').read_text(encoding='utf-8')
            self.assertIn('archive-notice',text)
            self.assertNotIn('class="news-card"',text)
        old=(self.output/'versions/0.6.0-beta.1/vortices.html').read_text(encoding='utf-8')
        self.assertNotIn('15 experience',old)
        self.assertNotIn('krawl-by-biome',old)

    def test_version_switch_preserves_pages_and_missing_ones_fall_back_to_home(self):
        html=(self.output/'es/vortices.html').read_text(encoding='utf-8')
        options=Options();options.feed(html)
        self.assertIn('../versions/0.6.0-beta.1/vortices.html',options.values)
        self.assertIn('../versions/0.5.1-alpha/index.html',options.values)
        self.assertIn('../fr/vortices.html',options.values)

if __name__=='__main__': unittest.main()
