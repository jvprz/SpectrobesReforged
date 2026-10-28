"""Integration checks for illustrated recipes across current wiki editions."""
from pathlib import Path
import unittest
import test_build
from locales import LANGUAGE_NAMES

class IllustratedRecipesTest(unittest.TestCase):
    setUpClass = classmethod(test_build.WikiEditionsTest.setUpClass.__func__)
    tearDownClass = classmethod(test_build.WikiEditionsTest.tearDownClass.__func__)
    def test_all_bundled_recipes_are_displayed_in_every_current_language(self):
        expected={p.stem for p in (Path(__file__).resolve().parents[2]/'src/main/resources/data/spectrobesreforged/recipe').glob('*.json')}
        import re
        for language in LANGUAGE_NAMES:
            folder=self.output if language=='en' else self.output/language
            pages=''.join((folder/(slug+'.html')).read_text(encoding='utf-8') for slug in ['getting-started','prizmod','minerals'])
            shown=set(re.findall(r'data-recipe="([^"]+)"',pages))
            self.assertEqual(expected,shown,language)
            self.assertNotIn('__ITEM_ASSETS__',pages)
            self.assertIn('image',pages)

    def test_tools_and_results_have_local_sprites_and_nine_crafting_slots(self):
        from html.parser import HTMLParser
        class Cards(HTMLParser):
            def __init__(self):super().__init__();self.slots=0;self.cards=0
            def handle_starttag(self,tag,attrs):
                attrs=dict(attrs)
                self.slots+=attrs.get('class')=='craft-slot'
                self.cards+='data-recipe' in attrs
        for language in LANGUAGE_NAMES:
            folder=self.output if language=='en' else self.output/language
            parser=Cards()
            text=(folder/'getting-started.html').read_text(encoding='utf-8')
            parser.feed(text)
            self.assertEqual(3,parser.cards)
            self.assertEqual(27,parser.slots)
            for tool in ['drill','blower','recovery','piton','fire','water','vacuum','blower_s','giga','blast']:
                self.assertIn('/item/excavation/'+tool+'.png',text)

if __name__=='__main__': unittest.main()
