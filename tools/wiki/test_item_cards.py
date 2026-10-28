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

    def test_block_recipes_use_local_transparent_model_renders(self):
        from PIL import Image
        for namespace, key in [('spectrobesreforged','excavation_table'),('minecraft','glass'),('minecraft','calibrated_sculk_sensor'),('minecraft','oak_planks')]:
            with Image.open(self.output/'assets/items/rendered'/namespace/(key+'.png')) as icon:
                self.assertEqual((64,64),icon.size)
                self.assertEqual('RGBA',icon.mode)
                self.assertEqual((0,255),icon.getchannel('A').getextrema())
        for language in LANGUAGE_NAMES:
            folder=self.output if language=='en' else self.output/language
            page=(folder/'getting-started.html').read_text(encoding='utf-8')
            for key in ['excavation_table']:
                self.assertIn('/rendered/spectrobesreforged/'+key+'.png',page)
            self.assertIn('/item/incubator.png',page)
            self.assertNotIn('/rendered/spectrobesreforged/incubator.png',page)
            self.assertIn('/rendered/minecraft/calibrated_sculk_sensor.png',page)
            self.assertIn('/rendered/minecraft/glass.png',page)

    def test_gallery_separates_current_and_temporary_designs(self):
        import re
        for language in LANGUAGE_NAMES:
            folder=self.output if language=='en' else self.output/language
            page=(folder/'gallery.html').read_text(encoding='utf-8')
            self.assertEqual(22,len(re.findall('class="design-card"',page)))
            self.assertEqual(6,page.count('data-provisional="false"'))
            self.assertEqual(16,page.count('data-provisional="true"'))
            self.assertNotIn('__GALLERY_ASSETS__',page)
            self.assertIn('/gallery/komainu/0.png',page)
            self.assertIn('/gallery/spiko/0.png',page)

    def test_grouped_navigation_has_four_sections_and_one_active_guide(self):
        import re
        for language in LANGUAGE_NAMES:
            folder=self.output if language=='en' else self.output/language
            page=(folder/'gallery.html').read_text(encoding='utf-8')
            nav=re.search(r'<nav class="guide-nav".*?</nav>',page,re.S)[0]
            self.assertEqual(4,nav.count('class="nav-group"'))
            self.assertEqual(1,nav.count('aria-current="page"'))
            self.assertEqual(16,len(re.findall('<a href=',nav)))
            self.assertIn('gallery.html" aria-current="page"',nav)

    def test_krawl_gallery_uses_all_three_real_designs_in_every_language(self):
        for language in LANGUAGE_NAMES:
            folder=self.output if language=='en' else self.output/language
            page=(folder/'krawl.html').read_text(encoding='utf-8')
            self.assertEqual(3,page.count('data-krawl="'))
            for key in ['blova','blovant','viblova']:
                self.assertIn('/gallery/krawl/'+key+'.png',page)
                self.assertTrue((self.output/'assets/gallery/krawl'/(key+'.png')).is_file())

if __name__=='__main__': unittest.main()
