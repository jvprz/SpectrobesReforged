# Premiers pas

Installez **Minecraft 1.21.1**, **NeoForge 21.1.219+**, **GeckoLib 4.8.3+ pour NeoForge 1.21.1** et **Java 21**. Téléchargez le mod sur [CurseForge](https://www.curseforge.com/minecraft/mc-mods/spectrobes-reforged). Placez les deux JAR dans le dossier `mods` de votre instance. Ne conservez qu’une version du mod et sauvegardez le monde.

## Équipement {#craft-your-equipment}

### Prizmod blanc {#white-prizmod}

{{PRIZMOD_RECIPE}}

A : 2 éclats d’améthyste ; P : 1 vitre ; I : 2 lingots de fer ; G : 1 lingot d’or ; C : 2 lingots de cuivre ; - : vide. Produit un Prizmod. Combinez-le avec une teinture : 16 couleurs, collection conservée.

### Incubateur {#incubator}

{{INCUBATOR_RECIPE}}

G : 3 blocs de verre ; I : 2 lingots de fer ; S : 1 capteur sculk **calibré** ; O : 2 lingots d’or ; C : 1 lingot de cuivre. Produit un incubateur, à alimenter par redstone.

Placez l’incubateur avec deux emplacements horizontaux libres. Fournissez un signal de redstone, insérez un fossile de Komainu et démarrez. Après environ 10 secondes alimentées, vérifiez l’entrée dans le Prizmod. Le scanner utilise **R**, modifiable dans les commandes. Les recettes sont disponibles depuis 0.6.0-beta.1. Le mod suit la langue de Minecraft.

## Essai de fouille {#excavation}

**0.6.2-beta.3**

Insérez un fossile brut ou un minéral C/B/A/A+ brut. Cliquez trois fois pour ouvrir la surface, puis retirez la couverture avec le bon outil. Maintenez et déplacez le clic pour creuser ; nettoyez les sédiments fins et débris au souffleur. Extrayez dès **90 %**. **R** active le scanner ; Les dix outils de Beyond the Portals sont représentés par des icônes autour de la zone centrale. **I** alterne entre fouille et inventaire. Réparation, Giga et explosif agissent une fois par clic ; l’explosif demande une seconde confirmation. Survolez les boutons pour lire leur fonction.

La récupération manuelle développe une compétence de fouille et enregistre le type exact dans votre catalogue. Giga exige un type déjà récupéré et ne donne ni expérience ni couronne. La compétence survit à la mort. Les quatre réparations restent liées à l’objet, même sur une autre table. Temps, couches, couverture, tension, chaleur, gaz et progression suivent l’objet. La couronne exige **100 %, aucun dégât et 60 secondes actives maximum** ; elle est conservée à l’éveil mais ne permet pas encore l’évolution adulte.

{{EXCAVATION_RECIPE}}

I : 2 lingots de fer ; C : 1 lingot de cuivre ; P : 4 planches ; G : 1 bloc de verre.

| Outil | Effet |
| --- | --- |
| {{TOOL_DRILL}} | Retire la roche. Déplacez le curseur ; relâchez ou soufflez pour réduire la tension. |
| {{TOOL_BLOWER}} | Nettoie sable, couche fine, débris et gaz sans dégâts. Refroidit aussi la zone. |
| {{TOOL_RECOVERY}} | Restaure 10 points, quatre utilisations par objet. Ne restaure pas une couronne perdue. |
| {{TOOL_PITON}} | Brise deux couches sur une grande zone. Évitez les objets exposés. |
| {{TOOL_FIRE}} | Fait fondre la glace. Surveillez la chaleur et utilisez le souffleur. |
| {{TOOL_WATER}} | Retire la lave et refroidit la zone. |
| {{TOOL_VACUUM}} | Retire eau, boue, débris troubles et gaz. |
| {{TOOL_BLOWER_S}} | Nettoie poussière, gaz, sédiments fins et sable sur toute la surface. |
| {{TOOL_GIGA}} | Dégage un type déjà excavé à la main. Sans expérience ni couronne. |
| {{TOOL_BLAST}} | 50 % de réussite instantanée, 50 % de destruction. Sans expérience ni couronne. Deux clics pour confirmer. |

Pierre, terre et couvertures utilisent les textures de blocs de Minecraft. La poussière claire se distingue du sol ; le curseur indique le matériau et l’outil adapté. Un fossile ou minéral détruit est consommé : son image disparaît, avec fragments et message de perte. Une fouille inachevée reste sauvegardée ; l’échec demeure explicite à la réouverture.

### Poches géologiques — 0.6.2-beta.6

Les dépôts créent une petite poche enterrée avec un minerai central, sans exiger de matériau hôte préexistant. Glace (Flash) uniquement dans les biomes enneigés ou gelés ; grès (Corona) dans le désert ; grès rouge (Corona) dans les trois variantes de badlands ; boue (Aurora) dans les marais et mangroves. Les quatre familles incluent les minéraux C/B/A/A+. Poches de 5–7 blocs de large et 3–5 de haut, centrées 2–6 blocs sous la surface. Seul le terrain naturel autorisé est remplacé, jamais air, fluides, autres minerais ou troncs. La coque centrale doit tenir dans le sol autorisé et son biome.

| ID | Fósil / Fossil | C / B / A / A+ |
| --- | --- | --- |
| `frozen` | `frozen_flash_fossil_ore` | `frozen_{c,b,a,a_plus}_mineral_ore` |
| `sandy` | `sandy_corona_fossil_ore` | `sandy_{c,b,a,a_plus}_mineral_ore` |
| `red_sandy` | `red_sandy_corona_fossil_ore` | `red_sandy_{c,b,a,a_plus}_mineral_ore` |
| `muddy` | `muddy_aurora_fossil_ore` | `muddy_{c,b,a,a_plus}_mineral_ore` |

Fossiles : 2 essais par chunk. Minéraux : C 1/2, B 1/4, A 1/12, A+ 1/32. Échec possible en terrain inadapté. Seulement les nouveaux chunks ; les minerais souterrains ordinaires par température restent présents. Les pièces conservent leur géologie rouge et les fouilles existantes leurs couches sauvegardées.


Les dix outils existent aussi comme objets dans l’onglet Créatif des outils (`excavation_drill`, `excavation_blower`, etc.). Leurs recettes et leur usage comme ingrédients de la table viendront plus tard ; la table fournit encore ses outils sans objets en inventaire.


{{ITEM_CREDITS}}


## Réglages

Ouvrez **Mods → Spectrobes Reforged → Configuration**. Réattribuez **R** (scanner) et **G** (Prizmod) dans les touches des options. G accepte un Prizmod équipé ou dans l’inventaire/la seconde main. Les raccourcis sont inactifs dans le chat et les autres écrans.

[Réglages et serveurs](settings.md).


**Intégration facultative :** [Curios API 9.5.1+1.21.1 NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) ajoute le slot. **Sans Curios**, gardez le Prizmod dans l’inventaire/seconde main et ouvrez-le avec **G** ou clic droit. Le slot multijoueur exige Curios sur serveur et clients. Retirez l’objet équipé vers l’inventaire avant de désinstaller Curios.

Ouvrez l’inventaire puis le **bouton d’équipement Curios**. Placez l’une des seize couleurs dans le slot **Prizmod**, fermez et appuyez sur **G**. Aucun emplacement normal ou d’armure n’est occupé. Inventaire, seconde main et clic droit restent utilisables.


## Excavation coopérative {#cooperative-excavation}

Ouvrez la même table : jusqu’à **16 opérateurs** partagent un spécimen, ses couches, poussière, chaleur, intégrité et résultat. Des curseurs colorés montrent les autres et une icône leur outil actif. Un outil est réservé pendant son utilisation ; des outils différents fonctionnent simultanément. Relâcher, changer d’outil, fermer ou se déconnecter libère la réservation ; une réservation interrompue expire après environ une demi-seconde.

Il existe **un seul résultat**, extrait une seule fois. Chaque participant admissible reçoit une fois l’XP de compétence et la découverte ; regarder ne compte pas. Fermer conserve la progression. Le serveur vérifie réservations, distance et actions. [Règles des raids](vortices.md#encounter-rules).


Deux joueurs différents doivent utiliser des outils avec succès sur le même spécimen pour obtenir le souvenir coopératif cyan ⚒. Ouvrir la table ne suffit pas. Il reste sur le fossile ou minéral propre et le Spectrobe éveillé, avec la couronne éventuelle. Aucun changement de statistiques, XP ou récompenses.


[Crédit de fouille partagé](advancements.md): Chaque joueur ayant utilisé un outil avec succès sur le spécimen, vivant, connecté et à 8 blocs au plus lors de l’extraction, reçoit les succès existants de fouille manuelle, trouvaille parfaite et terrain applicables. Chaque participant admissible obtient une découverte, son catalogue et l’XP normale de compétence ; les seuils de 10/100 découvertes et niveau 5 restent personnels. La personne qui extrait reçoit aussi le crédit. Regarder ne compte pas. Réparer reste attribué à l’utilisateur de Récupération ; éveil et entraînement restent personnels. L’extraction automatique ne donne aucun crédit manuel. Un seul objet de sortie, aucun nouveau succès coopératif.


La nouvelle table fait face au joueur qui la pose. L’objet propre est prioritaire dans le creux. Une pièce brute peut attendre en entrée, mais aucun travail ne commence avant de récupérer la sortie ; vide, la table n’affiche rien.
