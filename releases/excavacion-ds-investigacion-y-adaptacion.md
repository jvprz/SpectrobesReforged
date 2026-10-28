# Excavación de Spectrobes en DS: investigación y adaptación jugable

Fecha: 27 de octubre de 2026. Compilación de prueba: **0.6.2-beta.2**.

Este documento separa tres cosas: lo documentado en los juegos, lo que se ha implementado en Spectrobes Reforged y los detalles que no se pueden afirmar con certeza. Los números de equilibrio del mod son decisiones propias; no son fórmulas extraídas de una ROM. No se ha descompilado ninguno de los juegos ni se han reutilizado sus modelos, sonidos o texturas.

## 1. Qué juegos estamos intentando representar

Los dos títulos de Nintendo DS son **Spectrobes** (2007) y **Spectrobes: Beyond the Portals** (2008). No hay que confundirlos con **Spectrobes: Origins**, de Wii. Nuestra mesa y el transporte de una pieza en bruto son una adaptación a Minecraft. No deben presentarse como una reproducción literal del recorrido de DS.

La ficha de Nintendo para el primer juego confirma el núcleo: extraer fósiles de la roca con el lápiz táctil, evitar dañarlos y soplar al micrófono para retirar polvo. El despertar y el entrenamiento vienen después. [Ficha del editor en Nintendo](https://www.nintendo.com/en-gb/Games/Nintendo-DS/Spectrobes-273014.html).

## 2. Reconstrucción del recorrido del primer DS

La guía de 2007 describe una búsqueda con el Spectrobe niño, el acceso al punto encontrado y una apertura inicial por golpes. La profundidad indica cuántos toques hacen falta antes de llegar al objeto. Después se descubre su superficie. Esa guía sitúa la recogida en el **95 %** y describe seis niveles de excavación, con menos daño al ganar experiencia. También menciona recompensas de personalización. Son datos de una guía comunitaria contemporánea, no una tabla oficial de fórmulas. [Guía de scorchy99](https://gamefaqs.gamespot.com/ds/932841-spectrobes/faqs/47770).

La interfaz del primer juego incluye tiempo, profundidad, nivel de excavación, daño, tensión, herramienta y porcentaje. Hay tres taladros: grande, mediano y pequeño. Su diferencia es la relación entre alcance, velocidad y riesgo; el pequeño permite trabajar con precisión. El disolvente ayuda en zonas estrechas y la herramienta sónica ofrece una vía lenta y suave. [Descripción de la excavación de Spectrobes](https://strategywiki.org/wiki/Spectrobes/Excavation).

Una impresión de prensa que probó el juego antes de su lanzamiento confirma el cambio de gesto: primero tocar para bajar de profundidad; después recortar alrededor de la forma, limpiar y quitar material con cuidado. Esa secuencia importa para el diseño, aunque una versión preliminar no sirve para confirmar el equilibrio final. [Prueba directa de GameSpot](https://www.gamespot.com/articles/spectrobes-hands-on/1100-6156617/).

## 3. Qué aporta Beyond the Portals

El manual de BTP confirma: localizar el punto con un niño, abrir capas, trabajar alrededor del objeto, limpiar residuos, usar el escáner y recoger desde el **90 %**. El daño puede destruirlo. Una excavación al **100 %, sin daño y en 60 segundos** obtiene corona; su efecto original facilita la evolución adulta sin minerales. También confirma experiencia de excavación que reduce la probabilidad de daño. Los terrenos son roca, arena, agua, lava, hielo y barro. Los controles incluyen caja de herramientas con L, escáner con R y salida con B. [Manual, páginas impresas 20–28](https://device.report/m/345bfe58d1698dae1a65749b93076db3579d894ada7e01b961ffc3eb2681b25c.pdf).

El repertorio de BTP incluye taladro, pitón, soplador, fuego, agua, aspirador, reparación, soplador S, Giga y explosivo. El manual asigna fuego al hielo, agua a lava y aspiración a excavaciones acuáticas. Giga automatiza tipos recuperados antes; el explosivo tiene un 50 % de éxito. [Manual de BTP, herramientas](https://www.manualshelf.com/manual/disney/spectrobes-beyond-the-portals-ntr-yv4e-usa/user-guide-english.html).

La fuente anterior es el mismo manual alojado en otro sitio, no una confirmación independiente. Las descripciones de este apartado y su lista de herramientas se han resumido deliberadamente: la tabla posterior documenta nuestra implementación.

## 4. Diferencias que no conviene mezclar sin avisar

- **Recogida:** 95 % en las guías consultadas del primer DS; 90 % confirmado por el manual de BTP. El mod utiliza 90 %.
- **Herramientas:** tres tamaños de taladro y útiles químicos/sonoros en el primero; mayor especialización ambiental en BTP. La interfaz actual utiliza los diez útiles de BTP; las reglas conservan los otros útiles del prototipo anterior para compatibilidad.
- **Giga:** las guías del primer juego discrepan sobre sus restricciones con fósiles. No he dado por cierta una de esas variantes. El mod usa la regla clara de BTP: tipo recuperado manualmente antes.
- **Corona:** no es un objeto adicional ni una evolución instantánea. La condición y su efecto son conceptos diferentes.
- **Búsqueda y excavación:** son fases distintas. Nuestro escáner del compañero sigue detectando los bloques; la mesa procesa la pieza obtenida al picarlos.

La guía de minerales de 2007 documenta cuatro usos de reparación y una vía sónica lenta. También reconoce expresamente la incertidumbre sobre Giga y fósiles. Esa discrepancia impide presentar una probabilidad exacta universal como si perteneciera a los dos juegos. [Guía de BigGuyThe3000th](https://gamefaqs.gamespot.com/ds/932841-spectrobes/faqs/47849).

## 5. Qué hacía que la primera prueba se sintiera diferente

Esta es una lectura de diseño de las referencias y del feedback, no una afirmación sobre código interno de DS:

1. Empezar directamente ante una cuadrícula oculta suprimía la sensación de llegar hasta una pieza enterrada.
2. La profundidad aleatoria por píxel parecía ruido y no estratos de material.
3. El daño inmediato convertía el gesto de descubrir en una penalización difícil de anticipar.
4. Sin tensión visible, enfriamiento o limpieza significativa, cambiar de herramienta era un trámite.
5. Sin terreno, reparar, aprender ni automatizar tipos conocidos, todas las piezas repetían la misma secuencia.
6. Un porcentaje aislado no explica si faltan roca, sedimento, polvo o una cubierta especial.
7. Un icono plano enterrado transmite menos volumen que un objeto con geometría propia. Esta última diferencia continúa pendiente.

## 6. Secuencia que puedes jugar ahora

1. **Encontrar:** picar una mena de fósil o de mineral de los cuatro grados normales genera una pieza en bruto. Toque de Seda conserva la mena.
2. **Identidad fijada:** la especie o tipo de mineral se decide al generar el botín. No se vuelve a sortear al cerrar o mover la pieza. Los objetos obtenidos por Creative sin identidad la fijan al introducirlos por primera vez.
3. **Abrir:** introducirla en la mesa y hacer tres clics separados sobre la superficie para bajar las capas generales.
4. **Preparar el terreno:** retirar la cubierta ambiental con su herramienta.
5. **Trabajar la roca:** mantener clic y recorrer la superficie. Las capas forman parches de 4 × 4 celdas en lugar del ruido anterior.
6. **Limpiar y enfriar:** usar soplador o aspirador cuando proceda. El último sedimento fino puede retirarse sin insistir con el taladro.
7. **Controlar:** observar integridad, tensión, calor y gas; el escáner muestra el contorno y el nombre del objeto.
8. **Recuperar:** desde el 90 %, pulsar Extraer y retirar el resultado. Puedes arriesgarte a terminar el 100 % para una mejor excavación.
9. **Continuar:** despertar el fósil en la incubadora o usar el mineral con los sistemas existentes.

## 7. Herramientas de esta compilación

La caja visible sigue los diez útiles de BTP. Los cuatro útiles añadidos desde el primer DS siguen en las reglas para compatibilidad con el prototipo anterior, pero no aparecen en esta interfaz. Los útiles visibles están disponibles para facilitar la prueba. No se han añadido tiendas ni consumibles fabricables para cada herramienta. Los números siguientes son del mod.

| Herramienta | Uso implementado | Riesgo o límite |
| --- | --- | --- |
| Taladro mediano | Radio de 2 celdas; retira una capa por acción | Insistir sobre objeto descubierto acumula tensión |
| Taladro fino | Radio de 1; una capa | Acumula tensión más lentamente |
| Taladro grueso | Radio de 3; dos capas | Mayor presión y daño por error |
| Pitón | Apertura amplia de dos capas | También es peligroso sobre la pieza descubierta |
| Soplador | Radio de 4; polvo, arena y último sedimento | No atraviesa roca gruesa, hielo, agua ni barro |
| Reparador | Recupera hasta 10 puntos de integridad por uso | Cuatro usos por pieza; no revive una destruida ni borra el historial de daño |
| Taladro de fuego | Retira la cubierta de hielo | Produce calor; se enfría con soplador |
| Taladro de agua | Retira la cubierta de lava | También enfría |
| Aspirador | Retira cubierta de agua y barro; limpia residuos | No sustituye al fuego o al agua para hielo/lava |
| Soplador S | Limpia arena, sedimento fino y residuos de toda la superficie | No excava roca gruesa ni atraviesa cubiertas incompatibles |
| Disolvente | Una capa de una zona pequeña cada dos acciones | El contacto prolongado puede producir daño |
| Herramienta sónica | Una capa cada tres acciones, sin dañar directamente | Es más lenta y aún requiere retirar las cubiertas ambientales |
| Giga | Descubre automáticamente un tipo del catálogo del jugador | Exige recuperación manual previa; sin experiencia ni corona |
| Explosivo | Resuelve la pieza en una acción | 50 % éxito / 50 % destrucción; confirmación con otro clic; sin experiencia ni corona |

La sónica se activa manteniendo el ratón. No se usa el micrófono ni se graba audio. Es una adaptación de control para PC.

## 8. Terrenos y relación con Minecraft

El terreno se guarda en la pieza al generarse el botín. Transportarla a otra mesa no cambia ese entorno.

| Entorno del hallazgo | Cubierta | Preparación |
| --- | --- | --- |
| Bioma corriente | Roca | Taladro y limpieza |
| Desierto, badlands o playa | Arena | Soplador, soplador S o aspirador |
| Bioma frío con condiciones de nieve | Hielo | Fuego; controlar calor |
| Mena junto a lava | Lava | Agua |
| Océano, río o agua inmediatamente encima | Agua | Aspirador |
| Pantano o manglar | Barro | Aspirador |

La comprobación da prioridad a lava cercana, después frío, pantano, agua y arena. Los bloques colocados y picados se evalúan en su nueva ubicación. Las piezas antiguas sin cubierta guardada conservan el terreno normal; las piezas con una excavación empezada no se regeneran.

La cubierta es actualmente binaria por celda: se retira antes de la roca, no se simula un fluido tridimensional. No hay inundación o lava real en el mundo al excavar.

## 9. Tensión, calor, gas y daño: reglas exactas del mod

- La zona sigue siendo de 32 × 32 celdas, presentada a 128 × 128 píxeles de interfaz.
- El servidor acepta como máximo una acción cada dos ticks. A 20 ticks normales por segundo, son diez acciones por segundo.
- Taladrar alrededor de un borde descubierto sigue retirando la roca cercana antes de considerar el contacto accidental con el centro expuesto.
- Trabajar un punto ya descubierto acumula tensión: 4 con fino/disolvente, 9 con mediano/fuego y 16 con grueso/pitón.
- Cambiar de zona al menos tres celdas reduce tensión. Tras cuatro ticks sin trabajar, la tensión cae dos puntos por tick.
- Al llegar a 100 de tensión puede producirse daño y la tensión vuelve a 72. El error normal quita 2 puntos; grueso y pitón quitan 6.
- La experiencia reduce la probabilidad de esos errores, no vuelve invulnerable al objeto.
- El trabajo normal genera gas; el grueso y el pitón lo generan más deprisa. Desde 85 se bloquea la retirada de roca hasta limpiar.
- El fuego añade calor. A partir de 85, continuar con herramientas de riesgo aumenta la tensión. El calor también baja muy lentamente con el tiempo.
- Soplador, soplador S, aspirador y agua enfrían. La limpieza reduce tensión y gas.
- El polvo tiene presencia visual; el gas forma una neblina creciente. Hay fragmentos al desprender material y un aviso visual/sonoro al dañarlo.
- Con integridad cero se consume la pieza y se pierde el objeto. Reparar exige que siga vivo.

Estas reglas están pensadas para que un recorrido fluido y la limpieza frecuente sean más útiles que mantener el taladro inmóvil. Su equilibrio necesita tu prueba en juego.

## 10. Experiencia y catálogo

Es una habilidad propia, separada de la experiencia verde de Minecraft. Se guarda en el jugador y se conserva al morir.

La recuperación manual concede:

```text
20 + porcentaje/5 + integridad/5
+15 si terminas en 60 segundos de tiempo activo
+25 si cumples las condiciones de excavación perfecta
```

Se usan divisiones enteras. Cada 250 puntos aumenta un nivel, hasta nivel 10. La probabilidad de daño de un contacto peligroso baja ocho puntos porcentuales por nivel adicional, con un mínimo efectivo del 28 % en nivel 10. No se han copiado las curvas de experiencia de DS: esta progresión es más rápida para probarla en Minecraft.

El catálogo guarda el identificador exacto recuperado: conocer `power_c` no equivale a conocer `health_c` ni `power_b`. Giga consulta este catálogo del jugador que usa la mesa. Automatizar no entrena la habilidad ni añade tipos nuevos al catálogo. Fallar no concede experiencia.

## 11. Resultado, corona y guardado

La salida es un fósil o mineral real del mod, no una nueva moneda. La corona requiere 100 %, integridad completa, ausencia de daño durante toda la prueba, un máximo de 60 segundos y trabajo manual. Reparar hasta 100 no borra un error anterior.

En los fósiles, el resultado conserva integridad, segundos y corona. Al despertar, la marca se guarda asociada al UUID del Spectrobe en los datos del jugador. **Todavía no modifica la evolución:** el mod no tiene implementadas las formas adultas necesarias. Tampoco se ha inventado un aumento de estadísticas para sustituir ese efecto.

Cerrar la interfaz pausa el reloj. Retirar la pieza guarda sus capas, cubierta, polvo, daño, tiempo, tensión, calor, gas, reparaciones, tipo de terreno y estado aleatorio. Abrirla en otra mesa continúa esa misma prueba. Romper la mesa deja caer su contenido.

La extracción se valida en el servidor: propietario de la sesión, distancia, pieza presente, mínimo del 90 % y salida libre. El consumo y la recompensa son una operación. La experiencia se concede al extraer, no al hacer clic repetidamente. El resultado de una automatización ya realizada no puede volver a sortearse al abrir la mesa.

## 12. Cómo probarlo rápidamente

Sustituye la compilación anterior por `spectrobesreforged-0.6.2-beta.2.jar`. Minecraft 1.21.1, NeoForge 21.1.219+, GeckoLib 4.8.3+ y Java 21. Usa un mundo de prueba y la misma compilación en cliente y servidor.

```mcfunction
/give @s spectrobesreforged:excavation_table
/give @s spectrobesreforged:raw_corona_fossil[minecraft:custom_data={Fossil:"komainu",ExcavationTerrain:0}]
/give @s spectrobesreforged:raw_flash_fossil[minecraft:custom_data={ExcavationTerrain:2}]
/give @s spectrobesreforged:raw_aurora_fossil[minecraft:custom_data={ExcavationTerrain:3}]
/give @s spectrobesreforged:raw_c_mineral[minecraft:custom_data={Mineral:"power_c",ExcavationTerrain:4}]
```

Valores de terreno: 0 roca, 1 arena, 2 hielo, 3 lava, 4 agua, 5 barro. Son comandos para preparar casos de prueba; en Supervivencia lo asigna el hallazgo.

**Controles:** clics separados para abrir la superficie; mantener/arrastrar clic para herramientas continuas; 1 taladro mediano; 2 soplador; R escáner; los diez útiles de BTP aparecen como iconos alrededor de la excavación central; I abre o cierra el inventario. Reparador, Giga y explosivo actúan con un clic sobre la superficie. El explosivo exige un segundo clic de confirmación en cinco segundos. Las descripciones aparecen al posar el ratón sobre los botones.

Secuencia de feedback recomendada:

1. Extraer un Komainu de roca sin usar herramientas avanzadas. ¿Ahora se entiende el ciclo?
2. Repetir buscando 100 % y corona. ¿Es posible sin recorrer cada píxel?
3. Comparar fino y grueso sobre la misma zona. ¿Se percibe la diferencia?
4. Probar hielo/fuego y agua/aspirador. ¿Las cubiertas se entienden y aportan variedad?
5. Dejar subir tensión y gas; comprobar si el aviso llega antes de perder la pieza.
6. Reparar, cerrar y mover a otra mesa. Verificar los usos restantes.
7. Recuperar un `power_c` manualmente y usar Giga en otro igual; comprobar que no añade experiencia.
8. Llenar la salida y probar Extraer: la entrada no debe consumirse.
9. Probar una pieza vieja a medio excavar: debe continuar sin perder trabajo.
10. Verificar la interfaz en tu escala de GUI habitual y probar la mesa con otro jugador.

## 13. Qué falta para una reproducción completa

Esta compilación cubre el repertorio funcional de herramientas y los seis terrenos, pero no todos los sistemas de los juegos:

- Modelos propios del fósil con volumen real y distintas orientaciones; hoy se usa la imagen existente y capas de superficie.
- Micrófono para soplado y sónica; hoy se usan botones de ratón.
- Piezas de personalización con modelos y efectos en criaturas; no existe todavía su sistema de equipamiento.
- Cubos y piedras misteriosas con sus recompensas, análisis y progresión; no se han añadido objetos sin función para aparentar completitud.
- Evolución adulta y su interacción efectiva con la corona. La marca queda conservada para poder conectarla.
- Tiendas, precios y desbloqueos de útiles. Todo está disponible para evaluar el minijuego primero.
- Los valores internos originales de dureza, probabilidad, experiencia y puntuación. No se presentan nuestros ajustes como una copia exacta.

Los fósiles nuevos siguen limitados a las seis especies que ya pueden despertarse. Las piezas limpias antiguas siguen funcionando. El botín de los vórtices conserva sus minerales limpios; el cambio a bruto se aplica a las ocho menas normales de mineral.

## 14. Verificación y límites de la prueba

Se han compilado el mod y su JAR y ejecutado las pruebas automáticas. Hay comprobaciones de terrenos compatibles, precisión de útiles, daño, reparación persistente, automatización, catálogo, experiencia, coronas, formato de guardado y recuperación de todas las especies en los terrenos. También se mantienen las comprobaciones de los seis idiomas.

Eso no sustituye una prueba visual dentro de Minecraft ni una sesión multijugador. No afirmo haber jugado esta compilación. El siguiente paso es tu prueba y ajustar ritmo, claridad y tacto del cursor.

## Ajuste visual posterior: referencia de BTP

El panel superior reúne integridad, tensión, calor, gas, nivel de excavación, porcentaje y reloj. La superficie central pasa de 128 a 160 píxeles, con cinco iconos a cada lado. La tecla I muestra el inventario sobre la zona central; seleccionar una herramienta vuelve al trabajo. Los huecos de entrada y salida permanecen visibles en el panel superior. Los iconos son dibujos originales sobre la cuadrícula de píxeles de Minecraft. Se mantienen R para escáner, 1 para taladro y 2 para soplador.
