# Espèces de Spectrobes

Les **22 espèces Child des fossiles existants** sont incluses. Les six modèles originaux restent inchangés ; les seize autres utilisent le modèle, l’animation et la texture temporaires fournis, dans des fichiers séparés.

Les statistiques de base, plafonds et incréments suivent les tableaux Spectrobes déjà utilisés. Les seuils de minéraux suivent Beyond the Portals. Portées selon chaque référence ; les six espèces existantes restent inchangées. Qualité et croissance par niveau sont des adaptations du mod. Tableau avant bonus de qualité.

{{SPECIES}}

## Évolution : pas encore disponible {#evolution-data-not-yet-playable}

{{EVOLUTIONS}}

Exigences préparatoires uniquement : ni formes Adult/Evolved ni évolution activées. Les variantes temporaires partagent une texture mais gardent leurs numéros sauvegardés. Le défi original des six espèces est conservé.


## Ressources temporaires individuelles

Chaque espèce inachevée possède sa copie du dernier modèle provisoire, des animations et des trois textures. Aucun fichier default générique dans le JAR. Source conservée hors jeu dans `art/templates/spectrobe-placeholder/`. Remplacez les fichiers de l’espèce et conservez `idle`, `walk`, `sniff`.

```text
geo/entity/spectrobe/<key>.geo.json
animations/entity/spectrobe/<key>.animation.json
textures/entity/spectrobe/<key>/<key>_color_0.png
textures/entity/spectrobe/<key>/<key>_color_1.png
textures/entity/spectrobe/<key>/<key>_color_2.png
```
