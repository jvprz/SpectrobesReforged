"""Inventory illustrations and crafting cards read directly from mod resources."""
from pathlib import Path
from html import escape
from collections import Counter
import json
import shutil

VANILLA = 'https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/1.21.1/assets/minecraft/textures/'
LABELS = {
    'en': ['Shaped crafting', 'Shapeless crafting', 'Any planks', 'Any Prizmod', 'Ingredients', 'Item images'],
    'es': ['Crafteo con forma', 'Crafteo sin forma', 'Cualquier tablón', 'Cualquier Prizmod', 'Ingredientes', 'Imágenes de objetos'],
    'fr': ['Recette avec forme', 'Recette sans forme', 'Toutes les planches', 'Tout Prizmod', 'Ingrédients', 'Images des objets'],
    'de': ['Geformtes Rezept', 'Formloses Rezept', 'Beliebige Bretter', 'Beliebiges Prizmod', 'Zutaten', 'Gegenstandsbilder'],
    'it': ['Ricetta con forma', 'Ricetta senza forma', 'Qualsiasi assi', 'Qualsiasi Prizmod', 'Ingredienti', 'Immagini degli oggetti'],
    'pt-BR': ['Receita com formato', 'Receita sem formato', 'Qualquer tábua', 'Qualquer Prizmod', 'Ingredientes', 'Imagens dos itens']
}

def read(path):
    return json.loads(path.read_text(encoding='utf-8-sig'))

class ItemCards:
    def __init__(self, root, language):
        from block_icons import BlockIcons
        self.blocks = BlockIcons(root)
        self.language = language
        self.root = root
        self.assets = root / 'src/main/resources/assets/spectrobesreforged'
        self.data = root / 'src/main/resources/data/spectrobesreforged'
        locale = {'en':'en_us','es':'es_es','fr':'fr_fr','de':'de_de','it':'it_it','pt-BR':'pt_br'}[language]
        self.names = read(self.assets / 'lang' / (locale+'.json'))
        self.ui = LABELS[language]
        self.vanilla_names = read(Path(__file__).with_name('vanilla_items.json'))[language]

    def name(self, identifier):
        key = identifier.split(':')[-1]
        if identifier == '#minecraft:planks': return self.ui[2]
        if identifier == '#spectrobesreforged:prizmods': return self.ui[3]
        if identifier.startswith('minecraft:'): return self.vanilla_names[key]
        return self.names.get('item.spectrobesreforged.'+key, self.names.get('block.spectrobesreforged.'+key, key.replace('_',' ').title()))

    def texture(self, identifier):
        identifier = {'#minecraft:planks':'minecraft:oak_planks', '#spectrobesreforged:prizmods':'spectrobesreforged:white_prizmod'}.get(identifier,identifier)
        namespace, key = identifier.split(':')
        if self.blocks.block_model(identifier):
            return '__ITEM_ASSETS__/rendered/'+namespace+'/'+key+'.png'
        if namespace == 'minecraft':
            texture = {'glass':'block/glass','glass_pane':'block/glass','calibrated_sculk_sensor':'block/calibrated_sculk_sensor_top','oak_planks':'block/oak_planks'}.get(key,'item/'+key)
            return VANILLA+texture+'.png'
        model = read(self.assets/'models/item'/(key+'.json'))
        while not model.get('textures'):
            model = read(self.assets/'models'/(model['parent'].split(':',1)[1]+'.json'))
        textures = model['textures']
        value = textures.get('layer0',textures.get('top',textures.get('all')))
        if value and value.startswith('minecraft:'):return VANILLA+value.split(':',1)[1]+'.png'
        assert value and value.startswith('spectrobesreforged:'), identifier
        return '__ITEM_ASSETS__/'+value.split(':',1)[1]+'.png'

    def icon(self, identifier, text=True):
        label = escape(self.name(identifier))
        return '<span class="item-label" title="'+label+'"><img class="item-icon" src="'+escape(self.texture(identifier),quote=True)+'" alt="" width="32" height="32" loading="lazy" referrerpolicy="no-referrer">'+('<span>'+label+'</span>' if text else '<span class="sr-only">'+label+'</span>')+'</span>'

    def recipe(self, key):
        recipe = read(self.data/'recipe'/(key+'.json'))
        shaped = recipe['type'] == 'minecraft:crafting_shaped'
        def ingredient(value): return value.get('item') or '#'+value['tag']
        if shaped:
            slots = [ingredient(recipe['key'][c]) if c != ' ' else None for row in recipe['pattern'] for c in row.ljust(3)]
        else:
            slots = [ingredient(v) for v in recipe['ingredients']]
        slots += [None]*(9-len(slots))
        counts = Counter(i for i in slots if i)
        grid = ''.join('<span class="craft-slot">'+(self.icon(i,False) if i else '')+'</span>' for i in slots)
        result = recipe['result']
        ingredients = ''.join('<li><b>'+str(n)+' ×</b> '+self.icon(i)+'</li>' for i,n in counts.items())
        return '<section class="recipe-card" id="recipe-'+key+'" data-recipe="'+key+'"><div class="recipe-title">'+self.icon(result['id'])+'</div><p class="recipe-kind">'+self.ui[0 if shaped else 1]+'</p><div class="craft-layout"><div class="craft-grid" aria-label="3 × 3">'+grid+'</div><span class="craft-arrow" aria-hidden="true">→</span><div class="craft-result">'+self.icon(result['id'],False)+'<b>'+str(result.get('count',1))+'</b></div></div><details><summary>'+self.ui[4]+'</summary><ul class="craft-ingredients">'+ingredients+'</ul></details></section>'

    def organized_recipes(self):
        labels = {
            'en': ['Browse recipes', 'Mining', 'Air and water collection', 'Thermal tools', 'Recovery and extraction', 'Mineral combinations', 'Craft first'],
            'es': ['Ir a una categoría', 'Minería', 'Soplado y aspiración', 'Herramientas térmicas', 'Recuperación y extracción', 'Combinaciones de minerales', 'Fabricar primero'],
            'fr': ['Parcourir les recettes', 'Extraction minière', 'Soufflage et aspiration', 'Outils thermiques', 'Récupération et extraction', 'Combinaisons de minéraux', 'Fabriquer d’abord'],
            'de': ['Rezepte durchsuchen', 'Bergbau', 'Blasen und Saugen', 'Thermische Werkzeuge', 'Heilung und Bergung', 'Mineralkombinationen', 'Zuerst herstellen'],
            'it': ['Sfoglia le ricette', 'Estrazione mineraria', 'Soffio e aspirazione', 'Attrezzi termici', 'Recupero ed estrazione', 'Combinazioni di minerali', 'Fabbrica prima'],
            'pt-BR': ['Explorar receitas', 'Mineração', 'Sopro e aspiração', 'Ferramentas térmicas', 'Recuperação e extração', 'Combinações de minerais', 'Fabricar primeiro']
        }[self.language]
        groups = [
            ('mining', ['excavation_drill', 'excavation_piton']),
            ('air', ['excavation_blower', 'excavation_blower_s', 'excavation_vacuum']),
            ('thermal', ['excavation_fire', 'excavation_water']),
            ('extraction', ['excavation_recovery', 'excavation_giga', 'excavation_blast']),
            ('minerals', ['agate', 'lazuli', 'jade', 'amber', 'fluorite', 'coral'])
        ]
        available={p.stem for p in (self.data/'recipe').glob('*.json') if 'prizmod' not in p.stem and p.stem not in ['incubator','excavation_table']}
        listed=[key for _,keys in groups for key in keys]
        assert len(listed)==len(set(listed)) and set(listed)==available, 'Assign each recipe to one wiki category'
        prerequisites={'excavation_blower_s':'excavation_blower','excavation_fire':'excavation_drill','excavation_water':'excavation_drill','excavation_giga':'excavation_drill'}
        jump='<nav class="recipe-categories" aria-label="'+escape(labels[0])+'">'+''.join('<a href="#recipes-'+slug+'">'+escape(labels[i+1])+'</a>' for i,(slug,_) in enumerate(groups))+'</nav>'
        sections=[]
        for i,(slug,keys) in enumerate(groups):
            rendered=[]
            for key in keys:
                card=self.recipe(key)
                if key in prerequisites:
                    base=prerequisites[key]
                    hint='<p class="recipe-prerequisite">'+escape(labels[6])+': <a href="#recipe-'+base+'">'+self.icon('spectrobesreforged:'+base)+'</a></p>'
                else:
                    hint='<p class="recipe-prerequisite" aria-hidden="true"></p>'
                card=card.replace('<div class="craft-layout">',hint+'<div class="craft-layout">',1)
                rendered.append(card)
            sections.append('<section class="recipe-group" aria-labelledby="recipes-'+slug+'"><h3 id="recipes-'+slug+'">'+escape(labels[i+1])+' <span class="recipe-count">'+str(len(keys))+'</span></h3><div class="recipe-gallery">'+''.join(rendered)+'</div></section>')
        return jump+''.join(sections)

    def group(self, prefix):
        return '<div class="recipe-gallery">'+''.join(self.recipe(p.stem) for p in sorted((self.data/'recipe').glob(prefix+'*.json')))+'</div>'

    def credits(self):
        return '<p class="item-credits">'+self.ui[5]+': Spectrobes Reforged · <a href="https://github.com/InventivetalentDev/minecraft-assets/tree/1.21.1">Minecraft 1.21.1 / Mojang (minecraft-assets)</a> · <a href="https://minecraft.wiki/">Minecraft Wiki</a>.</p>'

def copy_icons(root, output):
    cards = ItemCards(root,'en')
    for model in (cards.assets/'models/item').glob('*.json'):
        try: texture = cards.texture('spectrobesreforged:'+model.stem)
        except (KeyError,FileNotFoundError,AssertionError): continue
        if texture.startswith('https://'):continue
        relative = texture.removeprefix('__ITEM_ASSETS__/')
        if relative.startswith('rendered/'):
            cards.blocks.render(cards.blocks.block_model('spectrobesreforged:'+model.stem), output/relative)
            continue
        target = output/relative
        target.parent.mkdir(parents=True,exist_ok=True)
        shutil.copy2(cards.assets/'textures'/relative,target)

    for path in sorted((Path(__file__).with_name('vanilla')/'models/item').glob('*.json')):
        key=path.stem
        identifier='minecraft:'+key
        model = cards.blocks.block_model(identifier)
        if model: cards.blocks.render(model,output/'rendered/minecraft'/(key+'.png'))
