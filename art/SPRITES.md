# Guía de sprites — pausa de diseño

Los archivos del juego están en `src/main/resources/assets/spectrobesreforged/`.

## Herramientas de excavación

Cada herramienta es ahora un objeto independiente, no apilable, disponible en la pestaña de herramientas de Creativo y mediante `/give`. Se usarán en futuras recetas de la mesa. De momento no tienen recetas propias ni son necesarias en el inventario para usar la mesa; su comportamiento sigue siendo el del minijuego.

Los siguientes PNG **16×16, transparentes** se comparten entre el objeto del inventario y el botón de la mesa. Reemplazarlos actualiza ambos. No incluyas el borde/fondo del botón dentro del sprite.

| Herramienta | ID del objeto | PNG dentro de assets/spectrobesreforged |
| --- | --- | --- |
| Taladro | `spectrobesreforged:excavation_drill` | `textures/item/excavation/drill.png` |
| Soplador | `spectrobesreforged:excavation_blower` | `textures/item/excavation/blower.png` |
| Reparador | `spectrobesreforged:excavation_recovery` | `textures/item/excavation/recovery.png` |
| Pitón | `spectrobesreforged:excavation_piton` | `textures/item/excavation/piton.png` |
| Taladro de fuego | `spectrobesreforged:excavation_fire` | `textures/item/excavation/fire.png` |
| Taladro de agua | `spectrobesreforged:excavation_water` | `textures/item/excavation/water.png` |
| Soplador S | `spectrobesreforged:excavation_blower_s` | `textures/item/excavation/blower_s.png` |
| Aspirador | `spectrobesreforged:excavation_vacuum` | `textures/item/excavation/vacuum.png` |
| Herramienta Giga | `spectrobesreforged:excavation_giga` | `textures/item/excavation/giga.png` |
| Herramienta explosiva | `spectrobesreforged:excavation_blast` | `textures/item/excavation/blast.png` |

Ejemplo: `/give @s spectrobesreforged:excavation_drill`.

## Preparación de archivos

- Guarda en PNG RGBA con fondo transparente para los objetos. Conserva nombre, ruta y tamaño para reemplazar directamente.
- Usa pincel de un píxel, sin suavizado. Amplía la vista con vecino más cercano para ver píxeles nítidos.
- Los nuevos iconos están a 16×16. Para otras imágenes conserva primero su resolución actual: cada una está recogida en `sprites-inventory.csv`.
- Puedes sustituir texturas con Minecraft abierto y recargar recursos con **F3 + T**. Añadir objetos nuevos sí requiere reiniciar el juego.
- En el proyecto, `gradlew.bat build` vuelve a empaquetar los PNG en el JAR. Cambiar el PNG del proyecto no modifica un JAR ya copiado a otra instalación.

## Qué archivos revisar

| Grupo | Carpeta de texturas | Uso |
| --- | --- | --- |
| Herramientas nuevas | `textures/item/excavation/` | Objeto y botón de la mesa |
| Fósiles en bruto y minerales en bruto | `textures/item/raw_*.png` | Material antes de limpiar |
| Fósiles limpios | `textures/item/fossil/` | Objeto limpio y silueta en la mesa |
| Minerales | `textures/item/minerals/` | Alimentación y excavación |
| Prizmod | `textures/item/prizmod/` y `textures/item/prizmod.png` | Variantes del objeto; revisar qué archivo referencia cada modelo |
| Mesa y menas ambientales | `textures/block/` | Bloques colocados y caras de sus modelos |
| Retratos de colección | `textures/gui/icons/` | Iconos de las fichas; la selección usa ahora el modelo 3D |
| Texturas de Spectrobes/Krawl/vórtice | `textures/entity/` | Texturas UV de modelos 3D; no son sprites planos |
| Partículas | `textures/particle/` | Gas y efectos |
| Otros elementos GUI | `textures/gui/` | Mezcla de recursos activos y antiguos |

La interfaz actual del Prizmod y gran parte de la mesa dibujan paneles, botones y barras por código. Editar `textures/gui/prizmod.png` no cambia el panel actual. Los iconos de herramientas sí están conectados directamente a los nuevos PNG.

## Inventario completo

`sprites-inventory.csv` incluye todos los PNG actuales y sus tamaños, incluso recursos antiguos o preparados para contenido futuro. Estar en la lista no garantiza que un archivo se use hoy. El modelo de cada objeto en `models/item/` indica qué textura utiliza realmente. Para los modelos 3D, edita la textura respetando el UV o abre el proyecto en Blockbench.

Prioridad sugerida: herramientas → materiales en bruto → fósiles y minerales limpios → objetos Prizmod/mesa → retratos → texturas de entidades.

La interfaz de despertar renovada (beta.5) también dibuja sus paneles y medidor por código; las antiguas texturas `incubator.png`, `frequency_leds.png`, `progress.png` y `start_button.png` ya no controlan esta pantalla.

## Plantel Child — beta.7

Las 16 especies nuevas tienen modelos, animaciones y texturas individuales copiados del default. La plantilla actual se conserva fuera del juego en `art/templates/spectrobe-placeholder/`; ya no hay recursos genéricos default en el JAR. Sustituye los archivos de cada especie cuando diseñes su modelo real. Las variantes 0/1/2 comparten por ahora la misma imagen; los iconos GUI provisionales son sus fósiles. Detalle y fuentes en `releases/0.6.2-beta.7-child-roster-playtest.md`.


En beta.10 las 16 copias se han actualizado con la última plantilla del artista. Cada especie mantiene archivos propios; consulta `art/templates/spectrobe-placeholder/README.md`.
