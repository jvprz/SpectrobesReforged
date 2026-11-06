"""Individual creature profiles generated from the same definitions and art as the mod."""
from pathlib import Path
from html import escape
import json
from PIL import Image

KEYS='overview|stats|gallery|evolution|obtain|behavior|related|base|max|increment|hp|attack|defense|speed|charge|scan|form|element|rank|temporary|custom|variant|default|support|width|height|follow|interval|knockback|mineral_requirement|target|back_species|back_krawl|open_image|stats_note|evolution_note|temporary_note|single_appearance|fossil|chroma|appearance|growth_level|growth_battles|portrait'.split('|')
ROWS={
'en':'Overview|Statistics|Gallery and variants|Planned evolution|How to obtain|Behavior and encounters|Related guides|Base|Maximum|Increment|HP|Attack|Defense|Movement speed|Charge speed|Scan radius|Form|Element|Rank|Temporary design|Custom design|Variant|Original|Support|Width|Height|Detection range|Attack interval|Knockback resistance|Minerals required|Target form|All Spectrobes|All Krawl|Open full image|Base species values from the mod, before training and fossil-quality bonuses. Increments are configured species data, not a promise of a gain at every level.|Evolution is not playable yet. These are the requirements currently prepared in the mod.|This species uses temporary artwork. Its final model and colors are still being developed.|One distinct appearance is currently available.|Fossil|Chroma minerals|Available appearance|Level required|Battles required|Portrait',
'es':'Resumen|Estadísticas|Galería y variantes|Evolución prevista|Cómo conseguirlo|Comportamiento y encuentros|Guías relacionadas|Base|Máximo|Incremento|Vida|Ataque|Defensa|Velocidad de movimiento|Velocidad de carga|Radio de escaneo|Forma|Elemento|Rango|Diseño provisional|Diseño propio|Variante|Original|Apoyo|Anchura|Altura|Alcance de detección|Intervalo de ataque|Resistencia al empuje|Minerales necesarios|Forma siguiente|Todos los Spectrobes|Todos los Krawl|Abrir imagen completa|Valores base de la especie incluidos en el mod, antes del entrenamiento y los bonus de calidad del fósil. Los incrementos son datos configurados de la especie, no una subida garantizada en cada nivel.|La evolución todavía no es jugable. Estos son los requisitos que el mod tiene preparados.|Esta especie utiliza recursos provisionales. Su modelo y colores definitivos siguen en desarrollo.|Actualmente hay un único aspecto diferente disponible.|Fósil|Minerales Chroma|Aspecto disponible|Nivel necesario|Victorias necesarias|Retrato',
'fr':'Résumé|Statistiques|Galerie et variantes|Évolution prévue|Obtention|Comportement et rencontres|Guides associés|Base|Maximum|Incrément|PV|Attaque|Défense|Vitesse de déplacement|Vitesse de charge|Rayon de détection|Forme|Élément|Rang|Design provisoire|Design propre|Variante|Original|Soutien|Largeur|Hauteur|Portée de détection|Intervalle d’attaque|Résistance au recul|Minéraux requis|Forme suivante|Tous les Spectrobes|Tous les Krawl|Ouvrir l’image complète|Valeurs de base de l’espèce dans le mod, avant entraînement et bonus de qualité. Les incréments sont des données configurées, pas un gain garanti à chaque niveau.|L’évolution n’est pas encore jouable. Voici les conditions actuellement préparées dans le mod.|Cette espèce utilise un modèle provisoire. Son modèle et ses couleurs définitifs sont en développement.|Une seule apparence différente est actuellement disponible.|Fossile|Minéraux Chroma|Apparence disponible|Niveau requis|Victoires requises|Portrait',
'de':'Übersicht|Werte|Galerie und Varianten|Geplante Entwicklung|Erhalt|Verhalten und Begegnungen|Weitere Leitfäden|Basis|Maximum|Zuwachs|KP|Angriff|Verteidigung|Bewegungstempo|Ladetempo|Scanradius|Form|Element|Rang|Vorläufiges Design|Eigenes Design|Variante|Original|Unterstützung|Breite|Höhe|Erkennungsreichweite|Angriffsintervall|Rückstoßresistenz|Benötigte Mineralien|Nächste Form|Alle Spectrobes|Alle Krawl|Ganzes Bild öffnen|Basiswerte der Art im Mod vor Training und Qualitätsboni. Zuwächse sind konfigurierte Artwerte und kein garantierter Gewinn pro Level.|Entwicklung ist noch nicht spielbar. Dies sind die bisher vorbereiteten Voraussetzungen.|Diese Art verwendet vorläufige Grafiken. Endgültiges Modell und Farben sind noch in Entwicklung.|Derzeit ist eine unterschiedliche Erscheinung verfügbar.|Fossil|Chroma-Mineralien|Verfügbare Erscheinung|Benötigtes Level|Benötigte Siege|Porträt',
'it':'Riepilogo|Statistiche|Galleria e varianti|Evoluzione prevista|Come ottenerlo|Comportamento e incontri|Guide correlate|Base|Massimo|Incremento|PV|Attacco|Difesa|Velocità di movimento|Velocità di carica|Raggio di scansione|Forma|Elemento|Rango|Design provvisorio|Design originale|Variante|Originale|Supporto|Larghezza|Altezza|Raggio di rilevamento|Intervallo di attacco|Resistenza al contraccolpo|Minerali richiesti|Forma successiva|Tutti gli Spectrobes|Tutti i Krawl|Apri immagine completa|Valori base della specie nel mod, prima dell’allenamento e dei bonus di qualità. Gli incrementi sono dati configurati e non aumenti garantiti a ogni livello.|L’evoluzione non è ancora giocabile. Questi sono i requisiti attualmente preparati nel mod.|Questa specie usa risorse provvisorie. Modello e colori definitivi sono ancora in sviluppo.|Al momento è disponibile un solo aspetto differente.|Fossile|Minerali Chroma|Aspetto disponibile|Livello richiesto|Vittorie richieste|Ritratto',
'pt-BR':'Resumo|Estatísticas|Galeria e variantes|Evolução prevista|Como obter|Comportamento e encontros|Guias relacionados|Base|Máximo|Incremento|Vida|Ataque|Defesa|Velocidade de movimento|Velocidade de carga|Raio de escaneamento|Forma|Elemento|Categoria|Visual provisório|Visual próprio|Variante|Original|Suporte|Largura|Altura|Alcance de detecção|Intervalo de ataque|Resistência ao empurrão|Minerais necessários|Próxima forma|Todos os Spectrobes|Todos os Krawl|Abrir imagem completa|Valores base da espécie no mod, antes do treino e dos bônus de qualidade. Os incrementos são dados configurados, não ganhos garantidos por nível.|A evolução ainda não é jogável. Estes são os requisitos atualmente preparados no mod.|Esta espécie usa recursos provisórios. Seu modelo e suas cores definitivos continuam em desenvolvimento.|Atualmente há uma única aparência diferente disponível.|Fóssil|Minerais Chroma|Aparência disponível|Nível necessário|Vitórias necessárias|Retrato'
}
LABELS={lang:dict(zip(KEYS,row.split('|'),strict=True)) for lang,row in ROWS.items()}
TEXT={
'en':{
'child':'{name} is a {element} Child Spectrobe. As a companion, it follows you and detects nearby buried fossils and minerals within a {radius}-block radius.',
'care':'Manage this companion in the Prizmod. It can bring you dropped Spectrobe minerals; collect the mineral from it, or it may eat it after waiting. Child Spectrobes are protected from damage.',
'obtain':'Find a {element} raw fossil, excavate it and awaken the identified {name} fossil in the incubator. The available species depend on the fossil family.',
'colors':'Awakening selects its color. Chroma minerals can later switch between the available variants. The previews show the artwork currently included in the mod.',
'krawl':'{name} is an aggressive {element} Krawl. It pursues players and fights at close range.',
'encounter':'Vortices originating in {climate} biomes summon {name}. Each new participant adds three enemies; rewards arrive when the entire encounter is complete.',
'loot':'This definition has no individual item drops. The vortex encounter awards personal rewards; see the vortex guide for multiplayer and death rules.',
'climates':['hot','temperate','cold'],'basic':'Basic','blocks':'blocks','seconds':'s','all_krawl':'Other Krawl','awakening':'Awakening','prizmod':'Prizmod','minerals':'Minerals and training','vortices':'Vortex encounters','gallery':'Design gallery'},
'es':{
'child':'{name} es un Spectrobe Child de elemento {element}. Como compañero, te sigue y detecta fósiles y minerales enterrados en un radio de {radius} bloques.',
'care':'Gestiona este compañero desde el Prizmod. Puede traerte minerales de Spectrobe que encuentre en el suelo; recógelos o, tras esperar, puede comérselos. Los Child están protegidos frente al daño.',
'obtain':'Encuentra un fósil en bruto {element}, excávalo y despierta el fósil identificado de {name} en la incubadora. Las especies disponibles dependen de la familia del fósil.',
'colors':'El despertar determina su color. Después puedes cambiar entre las variantes disponibles con minerales Chroma. Las imágenes muestran el diseño que incluye actualmente el mod.',
'krawl':'{name} es un Krawl agresivo de elemento {element}. Persigue al jugador y combate cuerpo a cuerpo.',
'encounter':'Los vórtices cuyo origen está en biomas {climate} generan {name}. Cada participante nuevo añade tres enemigos; las recompensas llegan al completar toda la incursión.',
'loot':'Esta definición no tiene botín individual de objetos. El encuentro del vórtice entrega recompensas personales; consulta su guía para las reglas de multijugador y muerte.',
'climates':['cálidos','templados','fríos'],'basic':'Básico','blocks':'bloques','seconds':'s','all_krawl':'Otros Krawl','awakening':'Despertar','prizmod':'Prizmod','minerals':'Minerales y entrenamiento','vortices':'Encuentros de vórtice','gallery':'Galería de diseños'},
'fr':{
'child':'{name} est un Spectrobe Child d’élément {element}. Il vous accompagne et détecte les fossiles et minéraux enfouis dans un rayon de {radius} blocs.',
'care':'Gérez ce compagnon dans le Prizmod. Il peut rapporter des minéraux Spectrobe au sol ; récupérez-les avant qu’il ne les mange après un délai. Les Child sont protégés contre les dégâts.',
'obtain':'Trouvez un fossile brut {element}, fouillez-le puis éveillez le fossile identifié de {name} dans l’incubateur. Les espèces possibles dépendent de la famille du fossile.',
'colors':'L’éveil détermine la couleur. Les minéraux Chroma permettent ensuite de changer de variante. Les aperçus montrent les ressources actuellement incluses dans le mod.',
'krawl':'{name} est un Krawl agressif d’élément {element}. Il poursuit les joueurs et combat au corps à corps.',
'encounter':'Les vortex originaires de biomes {climate} invoquent {name}. Chaque nouveau participant ajoute trois ennemis ; les récompenses arrivent à la fin de la rencontre.',
'loot':'Cette définition ne prévoit aucun butin individuel. Le vortex distribue des récompenses personnelles ; consultez son guide pour les règles multijoueur et de mort.',
'climates':['chauds','tempérés','froids'],'basic':'Basique','blocks':'blocs','seconds':'s','all_krawl':'Autres Krawl','awakening':'Éveil','prizmod':'Prizmod','minerals':'Minéraux et entraînement','vortices':'Rencontres de vortex','gallery':'Galerie des designs'},
'de':{
'child':'{name} ist ein Child-Spectrobe des Elements {element}. Als Begleiter folgt es dir und erkennt vergrabene Fossilien und Mineralien im Umkreis von {radius} Blöcken.',
'care':'Verwalte den Begleiter im Prizmod. Er kann gefundene Spectrobe-Mineralien bringen; hole sie ab, bevor er sie nach einer Wartezeit frisst. Child-Spectrobes sind vor Schaden geschützt.',
'obtain':'Finde ein rohes {element}-Fossil, grabe es aus und erwecke das identifizierte Fossil von {name} im Inkubator. Die möglichen Arten hängen von der Fossilfamilie ab.',
'colors':'Das Erwecken bestimmt die Farbe. Chroma-Mineralien wechseln später zwischen verfügbaren Varianten. Die Vorschau zeigt die aktuellen Grafiken des Mods.',
'krawl':'{name} ist ein aggressiver Krawl des Elements {element}. Er verfolgt Spieler und kämpft im Nahkampf.',
'encounter':'Wirbel aus {climate} Biomen rufen {name} hervor. Jeder neue Teilnehmer fügt drei Gegner hinzu; Belohnungen gibt es nach Abschluss der gesamten Begegnung.',
'loot':'Diese Definition enthält keine individuellen Gegenstandsbeuten. Der Wirbel vergibt persönliche Belohnungen; Regeln für Mehrspieler und Tod stehen im Wirbel-Leitfaden.',
'climates':['heißen','gemäßigten','kalten'],'basic':'Einfach','blocks':'Blöcke','seconds':'s','all_krawl':'Weitere Krawl','awakening':'Erwecken','prizmod':'Prizmod','minerals':'Mineralien und Training','vortices':'Wirbelbegegnungen','gallery':'Designgalerie'},
'it':{
'child':'{name} è uno Spectrobe Child di elemento {element}. Come compagno, ti segue e rileva fossili e minerali sepolti entro {radius} blocchi.',
'care':'Gestisci il compagno dal Prizmod. Può portarti minerali Spectrobe trovati a terra: raccoglili prima che li mangi dopo un’attesa. I Child sono protetti dai danni.',
'obtain':'Trova un fossile grezzo {element}, scavalo e risveglia il fossile identificato di {name} nell’incubatrice. Le specie possibili dipendono dalla famiglia del fossile.',
'colors':'Il risveglio determina il colore. I minerali Chroma consentono poi di cambiare variante. Le anteprime mostrano le risorse attualmente incluse nel mod.',
'krawl':'{name} è un Krawl aggressivo di elemento {element}. Insegue i giocatori e combatte da vicino.',
'encounter':'I vortici originati in biomi {climate} generano {name}. Ogni nuovo partecipante aggiunge tre nemici; le ricompense arrivano alla fine dell’intero incontro.',
'loot':'Questa definizione non prevede oggetti individuali. Il vortice assegna ricompense personali; consulta la guida per le regole multigiocatore e di morte.',
'climates':['caldi','temperati','freddi'],'basic':'Base','blocks':'blocchi','seconds':'s','all_krawl':'Altri Krawl','awakening':'Risveglio','prizmod':'Prizmod','minerals':'Minerali e allenamento','vortices':'Incontri dei vortici','gallery':'Galleria dei design'},
'pt-BR':{
'child':'{name} é um Spectrobe Child de elemento {element}. Como companheiro, segue você e detecta fósseis e minerais enterrados num raio de {radius} blocos.',
'care':'Gerencie o companheiro no Prizmod. Ele pode trazer minerais Spectrobe encontrados no chão: recolha-os antes que ele os coma após esperar. Os Child são protegidos contra dano.',
'obtain':'Encontre um fóssil bruto {element}, escave-o e desperte o fóssil identificado de {name} na incubadora. As espécies possíveis dependem da família do fóssil.',
'colors':'O despertar determina a cor. Minerais Chroma permitem mudar entre variantes disponíveis. As prévias mostram os recursos atualmente incluídos no mod.',
'krawl':'{name} é um Krawl agressivo de elemento {element}. Persegue jogadores e combate de perto.',
'encounter':'Vórtices originados em biomas {climate} geram {name}. Cada participante novo adiciona três inimigos; as recompensas chegam ao concluir todo o encontro.',
'loot':'Esta definição não possui itens de saque individuais. O vórtice entrega recompensas pessoais; consulte o guia para regras multijogador e de morte.',
'climates':['quentes','temperados','frios'],'basic':'Básico','blocks':'blocos','seconds':'s','all_krawl':'Outros Krawl','awakening':'Despertar','prizmod':'Prizmod','minerals':'Minerais e treinamento','vortices':'Encontros de vórtices','gallery':'Galeria de designs'}
}

def profile_slug(family,key):
    return family+'-'+key.replace('_','-')

def load(path):
    return json.loads(path.read_text(encoding='utf-8-sig'))

def table(headers, rows):
    return '\n'.join(['| '+' | '.join(headers)+' |','| '+' | '.join('---' for _ in headers)+' |']+['| '+' | '.join(str(value) for value in row)+' |' for row in rows])

def variants(root, species, family):
    if family=='krawl':return [(0,'__GALLERY_ASSETS__/krawl/'+species['key']+'.png',None)]
    assets=root/'src/main/resources/assets/spectrobesreforged';result=[];seen=set();key=species['key']
    for color in range(len(species.get('textures',[]))):
        path=assets/f'textures/entity/spectrobe/{key}/{key}_color_{color}.png'
        if not path.is_file():continue
        with Image.open(path) as im:signature=(im.size,im.convert('RGBA').tobytes())
        if signature in seen:continue
        seen.add(signature)
        icon=key+('' if color==0 else '_color_'+str(color))+'.png'
        icon_url='__GALLERY_ASSETS__/'+key+'/icon_'+str(color)+'.png' if (assets/'textures/gui/icons'/icon).is_file() else None
        result.append((color,'__GALLERY_ASSETS__/'+key+'/'+str(color)+'.png',icon_url))
    assert result, 'Missing creature gallery: '+key
    return result

def hero(species, family, language, images):
    ui=LABELS[language];t=TEXT[language];key=species['key'];name=species.get('name',key.capitalize());element=species.get('type',species.get('element')).capitalize()
    pending=species.get('model_status','').startswith('temporary')
    description=species.get('description')
    if isinstance(description,dict):description=description.get(language)
    if not description:description=t['child' if family=='spectrobe' else 'krawl'].format(name=name,element=element,radius=species.get('range',0)*2)
    image=images[0][1]
    identity='Child' if family=='spectrobe' else t.get(species.get('rank','basic'),species.get('rank','basic').capitalize())
    status='<span class="profile-status">'+escape(ui['temporary' if pending else 'custom'])+'</span>'
    if family=='spectrobe':metrics=[(ui['hp'],species['stats']['hp']['base']),(ui['attack'],species['stats']['attack']['base']),(ui['defense'],species['stats']['defense']['base']),(ui['scan'],str(species['range']*2)+' '+t['blocks'])]
    else:metrics=[(ui['hp'],species['stats']['hp']),(ui['attack'],species['stats']['attack']),(ui['defense'],species['stats']['defense']),(ui['follow'],str(species['stats']['follow_range'])+' '+t['blocks'])]
    facts=''.join('<div><dt>'+escape(label)+'</dt><dd>'+escape(str(value))+'</dd></div>' for label,value in metrics)
    switches=''
    if len(images)>1:
        controls=[]
        for index,(color,url,icon) in enumerate(images):
            label=ui['default'] if color==0 else ui['variant']+' '+str(color)
            picture='<img src="'+icon+'" alt="" width="24" height="24">' if icon else ''
            controls.append('<button type="button" class="profile-chip" aria-pressed="'+str(index==0).lower()+'" data-profile-image="'+url+'" data-variant-label="'+escape(label,quote=True)+'">'+picture+'<span>'+escape(label)+'</span></button>')
        switches='<div class="profile-switches">'+''.join(controls)+'</div>'
    return '<section class="creature-hero"><figure class="creature-portrait"><a data-preview-link href="'+image+'" target="_blank" rel="noopener" title="'+escape(ui['open_image'])+'"><img data-profile-preview src="'+image+'" alt="'+escape(name)+'" width="240" height="240"></a><figcaption data-preview-caption>'+escape(ui['default'])+'</figcaption></figure><div class="creature-intro"><div class="creature-badges"><span class="element-badge '+element.lower()+'">'+escape(element)+'</span><span class="profile-status">'+escape(identity)+'</span>'+status+'</div><p>'+escape(description)+'</p><dl class="creature-facts">'+facts+'</dl>'+switches+'</div></section>'

def gallery(images, ui):
    buttons=[]
    for index,(color,url,icon) in enumerate(images):
        label=ui['default'] if color==0 else ui['variant']+' '+str(color)
        portrait='<img class="variant-icon" src="'+icon+'" alt="" width="24" height="24">' if icon else ''
        buttons.append('<button class="profile-variant" id="variant-'+str(color)+'" type="button" aria-pressed="'+str(index==0).lower()+'" data-profile-image="'+url+'" data-variant-label="'+escape(label,quote=True)+'"><img class="variant-model" src="'+url+'" alt="" width="144" height="144" loading="lazy"><span>'+portrait+escape(label)+'</span></button>')
    return '<div class="profile-variants">'+''.join(buttons)+'</div>'

def spectrobe_page(root,species,language,known):
    ui=LABELS[language];t=TEXT[language];key=species['key'];name=key.capitalize();images=variants(root,species,'spectrobe');pending=species.get('model_status','').startswith('temporary');stats=species['stats']
    body=['# '+name,'<p class="profile-back"><a href="spectrobes.html">← '+ui['back_species']+'</a></p>',hero(species,'spectrobe',language,images)]
    if pending:body.append('<p class="profile-note">'+ui['temporary_note']+'</p>')
    growth_rows=[[ui[label],stats[field]['base'],stats[field]['max'],stats[field]['increment']] for label,field in [('hp','hp'),('attack','attack'),('defense','defense')]]
    movement_rows=[[ui['speed'],stats.get('move_speed',0)],[ui['charge'],stats.get('charge_speed',0)],[ui['scan'],str(species['range']*2)+' '+t['blocks']]]
    support_rows=[[ui[label],stats.get('support',{}).get(field,0)] for label,field in [('attack','attack'),('defense','defense'),('speed','speed'),('charge','charge')]]
    body+=['## '+ui['stats']+' {#statistics}',ui['stats_note'],table([ui['stats'],ui['base'],ui['max'],ui['increment']],growth_rows),table([ui['stats'],ui['base']],movement_rows),table([ui['support'],ui['base']],support_rows)]
    body+=['## '+ui['gallery']+' {#variants}',t['colors'],gallery(images,ui)]
    if len(images)==1:body.append(ui['single_appearance'])
    body+=['## '+ui['obtain']+' {#obtaining}',t['obtain'].format(name=name,element=species['type'].capitalize()),'## '+ui['behavior']+' {#behavior}',t['care']]
    evolution=species.get('evolution',{})
    if evolution:
        target=evolution.get('to','');next_name=target.capitalize();target_value='['+next_name+']('+profile_slug('spectrobe',target)+'.html)' if target in known else next_name
        rows=[[ui['target'],target_value],[ui['mineral_requirement'],evolution.get('minerals_required',0)]]
        for field,label in [('level_required','growth_level'),('battles_required','growth_battles')]:
            if evolution.get(field,0)>0:rows.append([ui[label],evolution[field]])
        body+=['## '+ui['evolution']+' {#evolution}',ui['evolution_note'],table([ui['evolution'],ui['base']],rows)]
    links=[('awakening','awakening'),('prizmod','prizmod'),('minerals','minerals'),('gallery','gallery')]
    body+=['## '+ui['related']+' {#related}','<nav class="profile-related">'+''.join('<a href="'+slug+'.html">'+escape(t[label])+'</a>' for slug,label in links)+'</nav>']
    return '\n\n'.join(body)

def krawl_page(root,species,language,all_krawl):
    ui=LABELS[language];t=TEXT[language];key=species['key'];name=species['name'];images=variants(root,species,'krawl');stats=species['stats'];combat=species.get('combat',{})
    climate=t['climates'][{'corona':0,'aurora':1,'flash':2}[species['element']]]
    rows=[[ui['hp'],stats['hp']],[ui['attack'],stats['attack']],[ui['defense'],stats['defense']],[ui['speed'],stats['move_speed']],[ui['follow'],str(stats['follow_range'])+' '+t['blocks']],[ui['interval'],f"{combat.get('attack_interval',stats['attack_speed'])/20:g} "+t['seconds']],[ui['knockback'],f"{stats['knockback_resistance']*100:g}%"],[ui['width'],species['entity']['width']],[ui['height'],species['entity']['height']]]
    others=''.join('<a href="'+profile_slug('krawl',other['key'])+'.html"><img src="__GALLERY_ASSETS__/krawl/'+other['key']+'.png" alt="" width="64" height="64"><span>'+escape(other['name'])+'</span><span class="element-badge '+other['element']+'">'+other['element'].capitalize()+'</span></a>' for other in all_krawl if other['key']!=key)
    return '\n\n'.join(['# '+name,'<p class="profile-back"><a href="krawl.html">← '+ui['back_krawl']+'</a></p>',hero(species,'krawl',language,images),'## '+ui['stats']+' {#statistics}',table([ui['stats'],ui['base']],rows),'## '+ui['gallery']+' {#variants}',gallery(images,ui),ui['single_appearance'],'## '+ui['behavior']+' {#behavior}',t['encounter'].format(name=name,climate=climate),t['loot'],'<p><a href="vortices.html">'+t['vortices']+' →</a></p>','## '+t['all_krawl']+' {#other-krawl}','<div class="profile-other-krawl">'+others+'</div>'])

def detail_pages(root,language):
    data=root/'src/main/resources/data/spectrobesreforged/entity'
    species=[load(p) for p in sorted((data/'spectrobe').glob('*.json'))];krawl=[load(p) for p in sorted((data/'krawl').glob('*.json'))];known={s['key'] for s in species};result=[]
    for family,definitions in [('spectrobe',species),('krawl',krawl)]:
        for definition in definitions:
            name=definition.get('name',definition['key'].capitalize())
            source=spectrobe_page(root,definition,language,known) if family=='spectrobe' else krawl_page(root,definition,language,krawl)
            result.append(dict(slug=profile_slug(family,definition['key']),title=name,label=name,parent='spectrobes' if family=='spectrobe' else 'krawl',family=family,key=definition['key'],source=source))
    return result
