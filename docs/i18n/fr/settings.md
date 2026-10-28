# Réglages et serveurs

**{{VERSION}}**

Ouvrez **Mods → Spectrobes Reforged → Configuration**. Réattribuez **R** (scanner) et **G** (Prizmod) dans les touches des options. G accepte un Prizmod équipé ou dans l’inventaire/la seconde main. Les raccourcis sont inactifs dans le chat et les autres écrans.

## Préférences personnelles

| Réglage | Par défaut | Effet |
| --- | --- | --- |
| Particules des vortex | Élevée | Modifie uniquement la densité des particules. Le réglage Minecraft reste appliqué. |
| Volume des effets (%) | 100% | Volume des effets du mod : scanner, éveil, Krawl et combats. Les volumes des catégories Minecraft restent appliqués. |
| Signaux du scanner | Visuel et son | Signaux visuels, son ou les deux. Ne change pas la portée ni les règles du serveur. |
| Explications de fouille | Activé | Affiche les explications détaillées. Noms, réparations, avertissements et état restent visibles. |
| Réduire les mouvements des interfaces | Désactivé | Réduit les décorations mobiles, les débris et les mouvements du modèle Prizmod. Progression et avertissements restent visibles. |

## Réglages du monde et serveur

Les valeurs par défaut correspondent au guide des vortex. Le serveur décide des règles pour tous ; le client ne peut pas les remplacer. En solo, le monde sert de serveur. Terrain valide, difficulté non Paisible et `doMobSpawning` restent nécessaires.

| Réglage | Par défaut | Effet |
| --- | --- | --- |
| Vortex naturels | Activé | Active les apparitions naturelles dans ce monde. Les vortex invoqués restent disponibles. |
| Intervalle de recherche (secondes) | 20 | Secondes de jeu entre contrôles à 20 TPS. Un contrôle ne garantit pas une apparition. |
| Probabilité de tentative (0–1) | 0.5 (50%) | 0 désactive les tentatives réussies ; 0,5 = 50 % ; 1 tente à chaque contrôle admissible. |
| Pause personnelle (secondes) | 60 | Pause après apparition réussie et combat terminé/annulé. Les vortex déjà présents restent. |
| Vitesse des vortex (blocs/seconde) | 5 | Vitesse sur terrain plat dégagé à 20 TPS. Le serveur décide pour tous les joueurs. |
| Limite de vortex chargés par dimension | 32 | Arrête les apparitions naturelles à ce nombre de vortex chargés, combats inclus. Ne supprime pas les entités existantes. |

## Fichiers

Préférences : `config/spectrobesreforged-client.toml`, propres à chaque joueur.

Serveur : `<monde>/serverconfig/spectrobesreforged-server.toml`. Modifiez le fichier du monde du serveur dédié ; NeoForge synchronise les valeurs. Un redémarrage après modification manuelle permet de les appliquer. Baisser la limite ne supprime aucun vortex ; les invocations restent disponibles.

## Multijoueur

Même **version du mod sur serveur et clients**, avec Minecraft 1.21.1, NeoForge, GeckoLib et Java 21. Beta.12 utilise le protocole 6, incompatible avec les protocoles précédents. Les sauvegardes restent conservées.

Le serveur valide Prizmod, propriété, sélection, ingrédients et distance aux machines. Il limite les requêtes d’ouverture, déplacement, alimentation et scan ; le spam ne prolonge pas le scanner. Collections individuelles et jusqu’à 16 opérateurs coopératifs par table.

Portées et définitions Krawl se synchronisent à la connexion et après `/reload`, sans soigner les Krawl existants. La déconnexion efface le cache local sans toucher au serveur. Les modèles/textures doivent toujours être présents dans les ressources correspondantes ; aucun téléchargement d’illustrations par les datapacks.

## Vérification

Tests de développement : serveur sans client, données et configuration, création des Krawl, descente du vortex, possession du Prizmod et limites par joueur. Une partie réelle à deux clients humains reste un contrôle supplémentaire.


**Intégration facultative :** [Curios API 9.5.1+1.21.1 NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) ajoute le slot. **Sans Curios**, gardez le Prizmod dans l’inventaire/seconde main et ouvrez-le avec **G** ou clic droit. Le slot multijoueur exige Curios sur serveur et clients. Retirez l’objet équipé vers l’inventaire avant de désinstaller Curios.

Ouvrez l’inventaire puis le **bouton d’équipement Curios**. Placez l’une des seize couleurs dans le slot **Prizmod**, fermez et appuyez sur **G**. Aucun emplacement normal ou d’armure n’est occupé. Inventaire, seconde main et clic droit restent utilisables.

Curios utilise aussi G par défaut. Si les touches coïncident, G ouvre l’équipement sans Prizmod et la collection avec un Prizmod porté ou équipé. Le bouton d’inventaire reste disponible ; les touches sont réattribuables.


[Raids coopératifs](vortices.md#encounter-rules) · [Excavation coopérative](getting-started.md#cooperative-excavation).


## Position du HUD et avis sur la bêta

Ouvrez **Mods → Spectrobes Reforged → Config → Placer le HUD**. Déplacez le panneau et utilisez la molette (50–150%). Vous pouvez masquer le HUD ou les emplacements et réinitialiser. Les préférences conservent position et taille localement. Taille initiale : 85%. Le bouton Discord ouvre une invitation après confirmation, avec un message local de bêta à chaque connexion, sans diffusion aux autres joueurs ni répétition après une mort ou un changement de dimension. Activez le HUD vertical pour une colonne et utilisez − / + ou la molette pour la taille.
