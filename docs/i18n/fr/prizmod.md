# Prizmod et compagnons

Utilisez n’importe quel Prizmod pour ouvrir votre collection commune à toutes les couleurs.

Ouvrez **Mods → Spectrobes Reforged → Configuration**. Réattribuez **R** (scanner) et **G** (Prizmod) dans les touches des options. G accepte un Prizmod équipé ou dans l’inventaire/la seconde main. Les raccourcis sont inactifs dans le chat et les autres écrans.


## Équiper le Prizmod

**Intégration facultative :** [Curios API 9.5.1+1.21.1 NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) ajoute le slot. **Sans Curios**, gardez le Prizmod dans l’inventaire/seconde main et ouvrez-le avec **G** ou clic droit. Le slot multijoueur exige Curios sur serveur et clients. Retirez l’objet équipé vers l’inventaire avant de désinstaller Curios.

Ouvrez l’inventaire puis le **bouton d’équipement Curios**. Placez l’une des seize couleurs dans le slot **Prizmod**, fermez et appuyez sur **G**. Aucun emplacement normal ou d’armure n’est occupé. Inventaire, seconde main et clic droit restent utilisables.

Curios utilise aussi G par défaut. Si les touches coïncident, G ouvre l’équipement sans Prizmod et la collection avec un Prizmod porté ou équipé. Le bouton d’inventaire reste disponible ; les touches sont réattribuables.

Curios sauvegarde l’équipement. La mort suit ses réglages serveur et, par défaut, `keepInventory`. La collection appartient au joueur : perdre ou retirer l’objet ne l’efface pas. Aucun point d’armure accordé.

## Collection et commandes

La réserve affiche **16 entrées par page**. Recherchez un nom, filtrez par élément ou couronne et triez par nom ou niveau. Cliquez sur une carte pour la sélectionner. Les filtres ne modifient pas l’ordre sauvegardé.

La fiche affiche stade, élément, PV, attaque, défense, niveau, XP, qualité, minéraux et victoires. Utilisez **Compagnon**, **Ranger**, **Équipe** ou **Nourrir**. Chaque clic sur un minéral de l’inventaire en donne une unité en Survie.

Le compagnon doit être un Child vivant. Les six places d’équipe acceptent Adult/Evolved ; les espèces actuelles sont des Children. Les deux destinations sont vérifiées avant un échange. L’évolution n’est pas encore disponible.

## Qualité et entraînement

L’intégrité du fossile donne **+1% à 70–84, +2% à 85–94 et +3% à 95–100**, arrondi vers le bas par statistique de base. Une fouille parfaite ajoute une couronne dorée permanente après le nom du fossile puis du Spectrobe éveillé. Qualité et couronne sont distinctes. Les anciennes entrées sans qualité conservent leurs statistiques.

Les minéraux élémentaires C/B/A/A+/S donnent **10/20/40/60/100% de base** avec la même affinité, moitié sinon, avant la courbe de niveau. Power/Defense/Health entraînent uniquement les statistiques et ne donnent plus d’XP. Les autres minéraux gardent leurs effets.

L’efficacité des minéraux diminue progressivement : **niveau 1 → 100%, 25 → 80%, 50 → 60%, 75 → 45%, 100 → 35%** du pourcentage de base. Un minéral de base à 10% apporte environ 6% au niveau 50, arrondi au point d’XP supérieur. Après un changement de niveau, le reste utilise la nouvelle efficacité. Les effets sur les statistiques, le compteur de minéraux et l’XP de combat restent inchangés. Au niveau maximal 100, aucun gain d’XP supplémentaire.

Les Children gagnent de l’XP en mangeant. Un raid terminé donne une victoire et jusqu’à 60 XP à chaque Adult/Evolved vivant de chaque participant, réduits après sa mort. Les Children ne gagnent pas d’XP de combat. Ces compteurs préparent une évolution future sans l’activer.

## Compagnons et sauvegarde

Sélectionnez un Child vivant puis Compagnon. Il suit le joueur et aide au scan. Pendant les [rencontres de vortex](vortices.md), il reste rangé puis revient. Dégâts, entraînement et collection sont sauvegardés.  Sauvegardez le monde avant une mise à jour.

La fiche sélectionnée affiche le modèle 3D animé et sa vraie couleur près des statistiques. Les cartes conservent leurs portraits.

## Référence d’évolution : Beyond the Portals

| Spectrobe | Minéraux |
| --- | --- |
| Komainu | 5 |
| Inkana | 4 |
| Vilar | 7 |
| Segu | 4 |
| Spiko | 3 |
| Shakin | 4 |

Référence uniquement ; l’évolution reste indisponible.


## Fabrication et couleurs

{{PRIZMOD_RECIPE}}

{{DYE_RECIPES}}

{{ITEM_CREDITS}}


Les compagnons Child ne subissent aucun dégât de combat ou environnemental : flèches, feu, lave, chutes, suffocation et noyade compris. Leurs PV sauvegardés sont aussi protégés. Ouvrir le Prizmod restaure les Child blessés ou à zéro PV des anciennes sauvegardes, sans changer statistiques, entraînement ou médailles. Les formes Adult/Evolved gardent leurs règles de dégâts. Les Child restent rangés pendant les raids.


## HUD du compagnon et de l’équipe

Un Child équipé affiche un panneau compact en haut à gauche : portrait, médailles, niveau et XP. Six emplacements numérotés représentent l’équipe ; les Adult/Evolved assignés montrent portrait et PV. Les emplacements vides n’activent ni évolution ni combat. La touche du Prizmod suit vos commandes. Masqué avec F1, débogage, menus, spectateur ou sans compagnon ni équipe.


## Dernières versions expérimentales

Les médailles ont leurs emplacements près de la vue 3D. Survolez une icône pour voir comment elle a été obtenue. Elles ne recouvrent plus les portraits.

### Minéraux et découvertes — test expérimental 14

Les minéraux élémentaires C/B/A/A+/S donnent **10/20/40/60/100% de base** avec la même affinité, moitié sinon, avant la courbe de niveau. Power/Defense/Health entraînent uniquement les statistiques et ne donnent plus d’XP. Les autres minéraux gardent leurs effets.

Graines : ATQ +1, DEF +1, PV +10. Limite commune cumulable d’une graine par niveau, maximum 100, plafonds de l’espèce ; aucune XP. Evolve M remplit les minéraux, L le niveau défini, B les combats définis. Sans condition ou si elle est déjà remplie, rien n’est consommé. Les Child actuels n’ont que la condition de minéraux. Pas d’évolution automatique.
