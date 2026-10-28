# Petits vortex Krawl

**{{VERSION}}**

Valeurs par défaut du serveur : consultez les [réglages](settings.md).

Le vortex détecte les joueurs vivants non spectateurs à **64 blocs** et avance à **5 blocs/seconde** sur terrain plat dégagé. Marcher ne suffit plus pour fuir ; sprinter permet de prendre de la distance. Le contact déclenche **trois Krawl par participant**, quel que soit l’objet tenu. Un avertissement indique son élément au début de la poursuite.

## Apparition naturelle {#discovery-in-survival}

Dans l’Overworld, un joueur vivant hors Créatif/Spectateur, à ciel ouvert, reçoit un contrôle toutes les **20 secondes**, avec **50 % de chance de tentative**. Chaque tentative examine jusqu’à **six positions à 32–48 blocs**. Une apparition n’est pas garantie : terrain, joueurs et vortex présents peuvent l’empêcher. `doMobSpawning` doit être activé ; difficulté Facile, Normale ou Difficile.

Le site doit être chargé, sec, dans la bordure et offrir trois blocs d’air sur un sol naturel adapté : terre, sable, roche, gravier, terre cuite, neige, glace, boue ou grès, via `vortex_ground`. Aucun joueur à moins de 24 blocs du site ni vortex à moins de 96 blocs du joueur. Aucun chargement forcé ; les joueurs souterrains ne déclenchent pas de découverte.

Après une apparition réussie et après la fin ou l’annulation du combat lorsque le propriétaire est connecté : pause de **60 secondes**. Pas de tentative pendant son combat. La limite des vortex proches s’applique aussi aux autres joueurs. Un vortex inactif disparaît après 20 minutes de ticks chargés.

## Krawl selon le biome {#krawl-by-biome}

Le **biome d’origine du vortex** fixe les ennemis, même pour une invocation. Traverser une frontière de biome ne les change pas ; le choix est sauvegardé.

| Climat | Ennemis | Exemples |
| --- | --- | --- |
| Chaud | Blovant · Corona | Déserts, savanes, badlands et jungles étiquetées |
| Tempéré | Viblova · Aurora vert | Plaines, forêts ; marais et mangroves selon leur température |
| Froid | Blova · Flash bleu | Biomes enneigés, pics gelés et taïgas étiquetées |

Les tags ont priorité : **hot → temperate → cold**. Sans tag correspondant : **température ≥ 1,0 → Corona ; < 0,2 → Flash ; sinon Aurora**, y compris les biomes moddés. Il s’agit de la température de base, pas de la météo, de l’heure ou de la neige liée à l’altitude. Listes explicites :

{{BIOMES}}

## Poursuite et terrain

Détection à 64 blocs ; cible conservée jusqu’à **80 blocs** si aucune cible plus proche n’est détectée. Vitesse : 0,25 bloc/tick à 20 TPS. Les marches d’un bloc, dalles, escaliers et graviers sont pris en charge. Murs hauts, eau, passages étroits et falaises peuvent le bloquer. Un vortex aérien descend au sol. Le lag ralentit la vitesse réelle ; `randomTickSpeed` n’accélère pas les apparitions.

## Raids coopératifs {#encounter-rules}

Une barre de style vanilla affiche les Krawl restants et le nombre de participants. Les joueurs vivants en Survie/Aventure rejoignent à **32 blocs** : **trois Krawl par joueur**, jusqu’à **16 joueurs / 48 ennemis**. Chaque UUID ne rejoint qu’une fois, même après un retour ou un rechargement. Impossible de participer à deux raids actifs. Sans trois emplacements sûrs, l’arrivée attend. Toutes les vagues conservent le type du biome d’origine. Les compagnons sont masqués pour tous les participants.

En solo, mourir annule immédiatement le raid et supprime ses ennemis sans butin. En multijoueur, un allié survivant peut continuer. Revenir vivant rétablit la participation mais conserve la pénalité de mort. Si tous les participants enregistrés sont tombés simultanément, le raid est perdu. Le mode Paisible, dix minutes chargées, ou vingt secondes chargées sans participant vivant à 32 blocs annulent aussi le raid.

Battez **tous les ennemis de toutes les vagues**. Les récompenses personnelles sont versées une seule fois à la fin ; ces Krawl ne lâchent aucun butin ni XP individuel. Les joueurs morts ou déconnectés reçoivent leur gain en attente une fois vivants et connectés. Sortir de la zone ne crée aucun gain supplémentaire.

| Joueurs inscrits | Minéraux C chacun | Minéraux B chacun | XP chacun |
| --- | --- | --- | --- |
| 1 | 2 | 0 | 15 |
| 2 | 3 | 0 | 20 |
| 3 | 3 | 1 | 25 |
| 4–16 | 3 | 1 | 30 |

Minéraux aléatoires : puissance, défense ou santé. Chaque mort supprime les B et retire un C, au maximum deux (minimum un C). XP divisée par morts + 1, arrondie au multiple de cinq inférieur, minimum cinq. Pénalité limitée à trois morts. Les Adult/Evolved admissibles gagnent 60 XP d’entraînement sans mort, sinon 60 / (morts + 1), minimum dix. Un surplus d’inventaire est réservé à son destinataire. Aucun bloc détruit ; combattez avec des armes Minecraft. L’évolution Child reste indisponible.

## Test rapide {#quick-test}

```text
/summon spectrobesreforged:krawl_vortex ~10 ~ ~
```

Invoquez le vortex ci-dessous sur un terrain ouvert, puis passez en Survie. Testez marche/sprint, marches, gravier, frontières de biomes et sauvegarde/rechargement. Les apparitions fonctionnent dans les chunks existants.
