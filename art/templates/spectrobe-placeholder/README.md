# Plantilla de trabajo fuera del juego

Estos archivos conservan la última plantilla que diseñaste. No se incluyen en el JAR.

Las 16 especies pendientes ya tienen copias independientes. Para sustituir una especie, reemplaza:

- `src/main/resources/assets/spectrobesreforged/geo/entity/spectrobe/<especie>.geo.json`
- `src/main/resources/assets/spectrobesreforged/animations/entity/spectrobe/<especie>.animation.json`
- `src/main/resources/assets/spectrobesreforged/textures/entity/spectrobe/<especie>/<especie>_color_0.png` (y variantes 1 y 2)
- Su retrato opcional: `textures/gui/icons/<especie>.png`.

Las animaciones usadas por el juego son `idle`, `walk` y `sniff`. Mantén esos nombres y los nombres de archivo. La geometría debe conservar el identificador `geometry.<especie>`. Las dimensiones UV del modelo deben coincidir con sus imágenes.

Los JSON de estadísticas siguen separados en `data/spectrobesreforged/entity/spectrobe/`; no hace falta cambiarlos para reemplazar el aspecto.
