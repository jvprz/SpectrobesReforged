"""Localized design gallery generated from the bundled creature geometry and textures."""
from pathlib import Path
from html import escape
import hashlib
import json
import shutil
from block_icons import BlockIcons

LABELS = {
'en': ['Current designs','Temporary models','Color variants','Static previews of the models and textures currently bundled with the mod.','These species still use temporary artwork; this is not their final design.'],
'es': ['Diseños actuales','Modelos provisionales','Variantes de color','Vistas estáticas de los modelos y texturas que incluye actualmente el mod.','Estas especies siguen usando recursos provisionales; no representan su diseño definitivo.'],
'fr': ['Designs actuels','Modèles provisoires','Variantes de couleur','Aperçus statiques des modèles et textures actuellement inclus dans le mod.','Ces espèces utilisent encore des ressources provisoires, et non leur design définitif.'],
'de': ['Aktuelle Designs','Vorläufige Modelle','Farbvarianten','Statische Vorschauen der derzeit im Mod enthaltenen Modelle und Texturen.','Diese Arten nutzen noch vorläufige Ressourcen, nicht ihr endgültiges Design.'],
'it': ['Design attuali','Modelli provvisori','Varianti di colore','Anteprime statiche dei modelli e delle texture attualmente inclusi nel mod.','Queste specie usano ancora risorse provvisorie, non il loro design definitivo.'],
'pt-BR': ['Designs atuais','Modelos provisórios','Variantes de cor','Prévias estáticas dos modelos e texturas incluídos atualmente no mod.','Estas espécies ainda usam recursos provisórios, não o design definitivo.']}

PORTRAITS = {'en':'Prizmod icons','es':'Iconos del Prizmod','fr':'Icônes du Prizmod','de':'Prizmod-Symbole','it':'Icone Prizmod','pt-BR':'Ícones do Prizmod'}
KOMAINU_ANIMATIONS = {'en': 'Komainu: handmade walking, running, curiosity, sitting, happiness, eating and independent blinking animations; carried minerals attach to the mouth. Static preview. Experimental companion playtest.', 'es': 'Komainu: animaciones hechas a mano para caminar, correr, investigar, sentarse, alegrarse, comer y parpadear de forma independiente; lleva los minerales en la boca. Vista estática. Prueba experimental de compañeros.', 'fr': 'Komainu : animations artisanales de marche, course, curiosité, assise, joie, repas et clignement indépendant ; minerais portés dans la bouche. Aperçu statique. Test expérimental des compagnons.', 'de': 'Komainu: handgemachte Animationen für Gehen, Rennen, Neugier, Sitzen, Freude, Fressen und unabhängiges Blinzeln; Mineralien werden im Maul getragen. Statische Vorschau. Experimenteller Begleitertest.', 'it': 'Komainu: animazioni fatte a mano per camminata, corsa, curiosità, seduta, felicità, pasto e battito delle palpebre indipendente; minerali trasportati in bocca. Anteprima statica. Prova sperimentale dei compagni.', 'pt-BR': 'Komainu: animações feitas à mão para andar, correr, investigar, sentar, demonstrar alegria, comer e piscar de forma independente; minerais carregados na boca. Prévia estática. Teste experimental de companheiros.'}
KOMAINU_NEW_EXPRESSIONS = {'en': 'New experimental clips: occasional playful hop and mouth movement synchronized with its cry. Happy now includes its own eye expression.', 'es': 'Nuevas animaciones experimentales: salto juguetón ocasional y movimiento de boca sincronizado con su grito. La alegría incluye ahora su propia expresión de ojos.', 'fr': 'Nouvelles animations expérimentales : petit saut occasionnel et bouche synchronisée avec le cri. La joie possède sa propre expression des yeux.', 'de': 'Neue experimentelle Animationen: gelegentlicher spielerischer Sprung und Mundbewegung zum Ruf. Freude hat einen eigenen Augenausdruck.', 'it': 'Nuove animazioni sperimentali: salto giocoso occasionale e bocca sincronizzata con il verso. La felicità include una propria espressione degli occhi.', 'pt-BR': 'Novas animações experimentais: salto brincalhão ocasional e boca sincronizada com o grito. A alegria inclui sua própria expressão dos olhos.'}
ZOZA_ANIMATIONS = {
'en':'Includes idle, walking and scanning animations. The gallery shows a static pose.',
'es':'Incluye animaciones de reposo, movimiento y escaneo. La galería muestra una pose estática.',
'fr':'Comprend les animations de repos, de marche et de détection. La galerie montre une pose statique.',
'de':'Mit Ruhe-, Lauf- und Suchanimationen. Die Galerie zeigt eine statische Pose.',
'it':'Include animazioni di riposo, camminata e scansione. La galleria mostra una posa statica.',
'pt-BR':'Inclui animações de repouso, movimento e busca. A galeria mostra uma pose estática.'}

def load(path): return json.loads(path.read_text(encoding='utf-8-sig'))

def geometry(root, key, texture, family="spectrobe"):
    """Bake Bedrock cuboids with GeckoLib 4's handedness, UVs and pivot order."""
    import math
    geo=load(root/f'src/main/resources/assets/spectrobesreforged/geo/entity/{family}/{key}.geo.json')['minecraft:geometry'][0]
    width=geo['description']['texture_width'];height=geo['description']['texture_height']
    bones={b['name']:b for b in geo['bones']};quads=[]
    def rotations(node):
        pivot=node.get('pivot',[0,0,0]);pivot=[-pivot[0],pivot[1],pivot[2]]
        return [{'axis':axis,'angle':angle*(-1 if axis!='z' else 1),'origin':pivot}
                for axis,angle in zip('xyz',node.get('rotation',[0,0,0])) if angle]
    for bone in bones.values():
        chain=[];current=bone;seen=set()
        while current:
            if current['name'] in seen: raise ValueError('Cyclic geometry bones')
            seen.add(current['name']);chain+=rotations(current);current=bones.get(current.get('parent'))
        if bone.get('neverRender'):continue
        for cube in bone.get('cubes',[]):
            ox,y,z=cube['origin'];dx,dy,dz=cube['size'];x=-ox-dx
            inflate=cube.get('inflate',bone.get('inflate',0));X=x+dx+inflate;Y=y+dy+inflate;Z=z+dz+inflate
            x-=inflate;y-=inflate;z-=inflate
            vertices={'west':[(x,Y,Z),(x,Y,z),(x,y,Z)],'east':[(X,Y,z),(X,Y,Z),(X,y,z)],
                'north':[(x,Y,z),(X,Y,z),(x,y,z)],'south':[(X,Y,Z),(x,Y,Z),(X,y,Z)],
                'up':[(x,Y,Z),(X,Y,Z),(x,Y,z)],'down':[(x,y,z),(X,y,z),(x,y,Z)]}
            normals={'west':[-1,0,0],'east':[1,0,0],'north':[0,0,-1],'south':[0,0,1],'up':[0,1,0],'down':[0,-1,0]}
            uv=cube.get('uv',[0,0]);box=isinstance(uv,list);mirror=bool(cube.get('mirror',False))
            if box:
                u,v=uv;w,h,d=map(math.floor,(dx,dy,dz))
                rect={'west':[u+d+w,v+d,d,h],'east':[u,v+d,d,h],'north':[u+d,v+d,w,h],
                    'south':[u+d+w+d,v+d,w,h],'up':[u+d,v+d,w,-d],'down':[u+d+w,v+d,w,-d]}
            else:rect={face:[*spec['uv'],*spec['uv_size']] for face,spec in uv.items()}
            transforms=rotations(cube)+chain+[{'axis':'y','angle':180,'origin':[0,0,0]}]
            zero=BlockIcons.rotate([0,0,0],transforms)
            for face,r in rect.items():
                actual=({'west':'east','east':'west'}.get(face,face) if mirror else face)
                if mirror and not box:actual={'up':'down','down':'up'}.get(actual,actual)
                normal=normals[face].copy()
                if mirror:normal[0]*=-1
                normal=[n-o for n,o in zip(BlockIcons.rotate(normal,transforms),zero)]
                # Coplanar front/back faces (Shakin's fins) must never overdraw
                # each other with conflicting UVs. Match runtime face culling.
                if normal[0]*.612372+normal[1]*.5+normal[2]*.612372<=1e-8:continue
                u,v,uw,vh=r
                if not mirror:u,uw=u+uw,-uw
                points=[BlockIcons.rotate(p,transforms) for p in vertices[actual]]
                quads.append({'points':points,'uv':[u*16/width,v*16/height,(u+uw)*16/width,(v+vh)*16/height],
                    'rotation':0 if box else uv[face].get('uv_rotation',0),
                    'texture':'spectrobesreforged:'+texture,'shade':.75+.25*max(0,normal[1])})
    return {'textures':{},'elements':[],'quads':quads}

def copy_gallery(root, output):
    renderer=BlockIcons(root);cache={}
    for p in sorted((root/'src/main/resources/data/spectrobesreforged/entity/spectrobe').glob('*.json')):
        species=load(p);key=species['key']
        # Match SpectrobeModel's actual runtime path, including legacy JSON path typos.
        for color in range(len(species['textures'])):
            texture=f'entity/spectrobe/{key}/{key}_color_{color}'
            if not renderer.resource('spectrobesreforged:'+texture,'textures','.png').is_file(): continue
            model=geometry(root,key,texture)
            digest=hashlib.sha256(json.dumps([{k:v for k,v in q.items() if k!='texture'} for q in model['quads']],sort_keys=True).encode()+renderer.texture('spectrobesreforged:'+texture).tobytes()).hexdigest()
            target=output/key/(str(color)+'.png');target.parent.mkdir(parents=True,exist_ok=True)
            icon_name=key+('' if color==0 else '_color_'+str(color))+'.png'
            icon=root/'src/main/resources/assets/spectrobesreforged/textures/gui/icons'/icon_name
            if icon.is_file():shutil.copy2(icon,output/key/('icon_'+str(color)+'.png'))
            if digest in cache: shutil.copy2(cache[digest],target)
            else: renderer.render(model,target,size=160);cache[digest]=target
    for p in sorted((root/'src/main/resources/data/spectrobesreforged/entity/krawl').glob('*.json')):
        species=load(p);key=species['key']
        renderer.render(geometry(root,key,'entity/krawl/'+key,family='krawl'),output/'krawl'/(key+'.png'),size=160)

    medals=output/'medals';medals.mkdir(parents=True,exist_ok=True)
    for name in ['crown','coop']:
        shutil.copy2(root/f'src/main/resources/assets/spectrobesreforged/textures/gui/medals/{name}.png',medals/(name+'.png'))


def gallery(root, language):
    labels=LABELS[language];finished=[];temporary=[]
    for p in sorted((root/'src/main/resources/data/spectrobesreforged/entity/spectrobe').glob('*.json')):
        s=load(p);key=s['key'];pending=s.get('model_status','').startswith('temporary')
        def image(color):
            url='__GALLERY_ASSETS__/'+key+'/'+str(color)+'.png'
            return f'<a href="spectrobe-{key}.html#variant-{color}"><img src="{url}" alt="{escape(key.capitalize())}" width="160" height="160" loading="lazy"></a>'
        card=f'<article class="design-card" id="species-{key}" data-species="{key}" data-provisional="{str(pending).lower()}">{image(0)}<h3><a href="spectrobe-{key}.html">{escape(key.capitalize())}</a></h3>'
        if pending: card+=f'<p class="design-status">{labels[1]}</p>'
        else:
            colors=[i for i in range(len(s['textures'])) if (root/f'src/main/resources/assets/spectrobesreforged/textures/entity/spectrobe/{key}/{key}_color_{i}.png').is_file()]
            if len(colors)>1: card+='<details><summary>'+labels[2]+'</summary><div class="design-variants">'+''.join(image(i) for i in colors)+'</div></details>'
            portraits=[]
            for color in colors:
                icon_name=key+('' if color==0 else '_color_'+str(color))+'.png'
                if (root/'src/main/resources/assets/spectrobesreforged/textures/gui/icons'/icon_name).is_file():
                    url='__GALLERY_ASSETS__/'+key+'/icon_'+str(color)+'.png'
                    portraits.append(f'<img src="{url}" alt="{escape(key.capitalize())} · {color}" width="32" height="32" loading="lazy">')
            if portraits:card+='<p class="design-status">'+PORTRAITS[language]+'</p><div class="design-portraits">'+''.join(portraits)+'</div>'
            if key in {'komainu','spiko'}:card+='<p class="design-status">'+KOMAINU_ANIMATIONS[language].replace('Komainu',key.title())+' '+KOMAINU_NEW_EXPRESSIONS[language]+'</p>'
            if key=='zoza':card+='<p class="design-status">'+ZOZA_ANIMATIONS[language]+'</p>'
        card+='</article>'
        (temporary if pending else finished).append(card)
    return (labels[3]+'\n\n## '+labels[0]+' {#current-designs}\n\n<div class="design-gallery">'
            +''.join(finished)+'</div>\n\n## '+labels[1]+' {#temporary-designs}\n\n'+labels[4]
            +'\n\n<div class="design-gallery design-gallery-temporary">'+''.join(temporary)+'</div>')


def krawl_gallery(root, language):
    cards=[]
    for p in sorted((root/'src/main/resources/data/spectrobesreforged/entity/krawl').glob('*.json')):
        species=load(p);key=species['key'];element=species['element']
        url='__GALLERY_ASSETS__/krawl/'+key+'.png'
        cards.append(f'<article class="design-card" data-krawl="{key}"><a href="krawl-{key}.html"><img src="{url}" alt="{escape(species["name"])}" width="160" height="160" loading="lazy"></a><h3><a href="krawl-{key}.html">{escape(species["name"])}</a></h3><span class="element-badge {element}">{escape(element.capitalize())}</span></article>')
    return '<div class="design-gallery krawl-gallery">'+''.join(cards)+'</div>'
