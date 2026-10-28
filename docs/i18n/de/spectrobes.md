# Spectrobe-Arten

Alle **22 Child-Arten der vorhandenen Fossilien** sind enthalten. Die sechs ursprünglichen Modelle bleiben erhalten; die anderen sechzehn verwenden das bereitgestellte Platzhaltermodell mit Animation und Textur in getrennten Dateien.

Basiswerte, Grenzen und Zuwächse folgen den bisher verwendeten Spectrobes-Tabellen. Mineralanforderungen stammen aus Beyond the Portals. Suchreichweite gemäß Artquelle; die bisherigen sechs bleiben unverändert. Qualität und Levelwachstum sind Mod-Anpassungen. Tabelle ohne Qualitätsbonus.

{{SPECIES}}

## Entwicklung: noch nicht spielbar {#evolution-data-not-yet-playable}

{{EVOLUTIONS}}

Nur vorbereitende Anforderungen: Adult/Evolved und Entwicklung bleiben deaktiviert. Platzhaltervarianten teilen eine Textur, behalten aber getrennte Variantennummern. Der ursprüngliche Sechs-Arten-Fortschritt bleibt erhalten.


## Individuelle Platzhalterressourcen

Jede unfertige Art besitzt eigene Kopien des aktuellen Platzhaltermodells, der Animationen und dreier Texturen. Keine generischen default-Dateien im JAR. Vorlage außerhalb des Spiels unter `art/templates/spectrobe-placeholder/`. Nur die Artdateien ersetzen; `idle`, `walk`, `sniff` beibehalten.

```text
geo/entity/spectrobe/<key>.geo.json
animations/entity/spectrobe/<key>.animation.json
textures/entity/spectrobe/<key>/<key>_color_0.png
textures/entity/spectrobe/<key>/<key>_color_1.png
textures/entity/spectrobe/<key>/<key>_color_2.png
```


[Spectrobe-Designgalerie](gallery.md)
