# Especies de Spectrobes

Ya están incluidas las **22 especies Child de los fósiles existentes**. Las seis originales conservan sus modelos; las otras dieciséis usan el modelo, animación y textura temporal facilitados. Cada especie tiene sus archivos separados para sustituirlos por el arte final.

Las estadísticas base, máximos e incrementos siguen las tablas de Spectrobes usadas por los datos existentes. Los umbrales de minerales para evolucionar son de Beyond the Portals. El alcance sigue la referencia de cada especie; las seis existentes no cambian. La calidad y el crecimiento por nivel son adaptaciones del mod. La tabla muestra base → máximo antes del bonus de integridad.

{{SPECIES}}

## Datos de evolución: todavía no jugable {#evolution-data-not-yet-playable}

{{EVOLUTIONS}}

Estos requisitos son preparatorios. Esta actualización no incorpora formas Adult/Evolved ni activa la evolución. Las variantes del modelo temporal comparten textura, pero sus números de variante se guardan por separado. Se mantiene el logro original de colección de seis especies.


## Recursos temporales individuales

Cada especie pendiente tiene su propia copia de la última geometría, animaciones y tres texturas provisionales. El JAR ya no incluye un modelo, animación o textura default genéricos. El original del artista se conserva fuera del juego en `art/templates/spectrobe-placeholder/`. Reemplaza solo los archivos de la especie; conserva los nombres de animación `idle`, `walk` y `sniff`.

```text
geo/entity/spectrobe/<key>.geo.json
animations/entity/spectrobe/<key>.animation.json
textures/entity/spectrobe/<key>/<key>_color_0.png
textures/entity/spectrobe/<key>/<key>_color_1.png
textures/entity/spectrobe/<key>/<key>_color_2.png
```
