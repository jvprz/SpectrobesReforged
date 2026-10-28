# Spectrobe roster

The **22 Child species corresponding to the existing fossils** are now included. The original six retain their own models; the other sixteen use the supplied temporary default model, animation and texture. Each species has separate files ready to replace with final art.

The following base stats, caps and increments follow the Spectrobes tables used by the existing data. Evolution mineral thresholds follow Beyond the Portals. Scan ranges follow the per-species reference; the existing six are unchanged. Quality and level growth remain the mod’s adaptations. The table shows base → cap before the integrity bonus.

{{SPECIES}}

## Evolution data: not yet playable {#evolution-data-not-yet-playable}

{{EVOLUTIONS}}

These are preparatory requirements only. Adult/Evolved definitions and evolution are not enabled in this update. Color variants of a temporary model currently share the same texture, but their saved variant numbers remain distinct. The original six-species collection advancement is retained.


## Individual temporary resources

Each unfinished species now has its own copy of the latest placeholder geometry, animations and three textures. No generic default model/animation/texture is packaged. The artist’s source is archived outside the game under `art/templates/spectrobe-placeholder/`. Replace only the species files below; retain `idle`, `walk`, `sniff` animation names.

```text
geo/entity/spectrobe/<key>.geo.json
animations/entity/spectrobe/<key>.animation.json
textures/entity/spectrobe/<key>/<key>_color_0.png
textures/entity/spectrobe/<key>/<key>_color_1.png
textures/entity/spectrobe/<key>/<key>_color_2.png
```
